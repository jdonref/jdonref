package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.InPlaceMergeSorter;

/**
 * A Spans that is formed by subspans which target to check payload values.
 * 
 * @author Julien
 */
public class SpansPayloadChecker extends Spans
{
  protected boolean firstTime = true;
  protected boolean more = false;

  /** The spans in the same order as the SpanNearQuery */
  protected final MultiPayloadTermSpans[] subSpans;

  /** Indicates that all subSpans have same doc() */
  protected boolean inSameDoc = false;

  protected int matchDoc = -1;
  protected int matchStart = -1;
  protected int matchEnd = -1;
  protected List<byte[]> matchPayload;

  protected final MultiPayloadTermSpans[] subSpansByDoc;
  
  protected int numSpans = -1;
  
  protected IPayloadChecker checker;
  
  // Even though the array is probably almost sorted, InPlaceMergeSorter will likely
  // perform better since it has a lower overhead than TimSorter for small arrays
  private final InPlaceMergeSorter sorter = new InPlaceMergeSorter() {
    @Override
    protected void swap(int i, int j) {
      ArrayUtil.swap(subSpansByDoc, i, j);
    }
    @Override
    protected int compare(int i, int j) {
      return subSpansByDoc[i].doc() - subSpansByDoc[j].doc();
    }
  };

  protected PayloadCheckerSpanFilter filter;
  
  public SpansPayloadChecker(PayloadCheckerSpanFilter gspFilter, AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts, IPayloadChecker checker) throws IOException {
    MultiPayloadSpanTermFilter[] clauses = gspFilter.getClauses();
    if (clauses.length<1)
        throw new IllegalArgumentException("Less than 1 clauses ");
    subSpans = new MultiPayloadTermSpans[clauses.length];
    matchPayload = new LinkedList<>();
    subSpansByDoc = new MultiPayloadTermSpans[clauses.length];
    for (int i = 0; i < clauses.length; i++) {
      subSpans[i] = clauses[i].getSpans(context, acceptDocs, termContexts); 
      subSpans[i].setOrder(clauses[i].getOrder()); // note original order are keeped
      subSpansByDoc[i] = subSpans[i]; // used in toSameDoc()
    }
    numSpans = subSpans.length;
    filter = gspFilter; // kept for toString() only.
    this.checker = checker;
    
    heapify();
  }

  // inherit javadocs
  @Override
  public int doc() { return matchDoc; }

  // inherit javadocs
  @Override
  public int start() { return matchStart; }

  // inherit javadocs
  @Override
  public int end() { return matchEnd; }
  
  public Spans[] getSubSpans() {
    return subSpans;
  }  

  // TODO: Remove warning after API has been finalized
  // TODO: Would be nice to be able to lazy load payloads
  @Override
  public Collection<byte[]> getPayload() throws IOException {
    return matchPayload;
  }

  // TODO: Remove warning after API has been finalized
  @Override
  public boolean isPayloadAvailable() {
    return matchPayload.isEmpty() == false;
  }

  @Override
  // TODO: understand what to do with that
  public long cost() {
    long minCost = Long.MAX_VALUE;
    for (int i = 0; i < subSpans.length; i++) {
      minCost = Math.min(minCost, subSpans[i].cost());
    }
    return minCost;
  }
  
  /** 
   * extract from DisjunctionScorer
   * Organize subScorers into a min heap with scorers generating the earliest document on top.
   */
  protected final void heapify() {
    for (int i = (numSpans >> 1) - 1; i >= 0; i--) {
      heapAdjust(i);
    }
  }
  
  /** 
   * extract from DisjunctionScorer
   * The subtree of subScorers at root is a min heap except possibly for its root element.
   * Bubble the root down as required to make the subtree a heap.
   */
  protected final void heapAdjust(int root) {
    MultiPayloadTermSpans scorer = subSpans[root];
    int tmpDoc = scorer.doc();
    int i = root;
    while (i <= (numSpans >> 1) - 1) {
      int lchild = (i << 1) + 1;
      MultiPayloadTermSpans lscorer = subSpans[lchild];
      int ldoc = lscorer.doc();
      int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
      MultiPayloadTermSpans rscorer = null;
      if (rchild < numSpans) {
        rscorer = subSpans[rchild];
        rdoc = rscorer.doc();
      }
      if (ldoc < tmpDoc) {
        if (rdoc < ldoc) {
          subSpans[i] = rscorer;
          subSpans[rchild] = scorer;
          i = rchild;
        } else {
          subSpans[i] = lscorer;
          subSpans[lchild] = scorer;
          i = lchild;
        }
      } else if (rdoc < tmpDoc) {
        subSpans[i] = rscorer;
        subSpans[rchild] = scorer;
        i = rchild;
      } else {
        return;
      }
    }
  }
  
  /** 
   * extract from DisjunctionScorer
   * Remove the root Scorer from subScorers and re-establish it as a heap
   */
  protected final void heapRemoveRoot() {
    if (numSpans == 1) {
      subSpans[0] = null;
      numSpans = 0;
    } else {
      subSpans[0] = subSpans[numSpans - 1];
      subSpans[numSpans - 1] = null;
      --numSpans;
      heapAdjust(0);
    }
  }
  
  
  @Override
  public boolean next() throws IOException
  {
    firstTime = false;
    
    if (numSpans==0){
        this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
        return false;
    }
    
    while(true) {
      boolean next = subSpans[0].next();
      this.matchDoc = subSpans[0].doc();
      if (next) {
        heapAdjust(0);
      } else {
        heapRemoveRoot();
        if (numSpans == 0) {
          this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
          return false;
        }
      }
      if (subSpans[0].doc() == matchDoc && subSpans[0].doc()!=DocIdSetIterator.NO_MORE_DOCS) { // check if heap has been adjusted
        matchPayload.clear();
        if (checkPayloads())
        {
            return true;
        }
      }
    }
  }
  
  @Override
  public boolean skipTo(int target) throws IOException {
    if (numSpans==0){
        this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
        return false;
    }

    while(true) {
      // NB: don't know why subScorers[0] may be null
      if (subSpans[0].skipTo(target)) {
        this.matchDoc = subSpans[0].doc();
        heapAdjust(0);
      } else {
        heapRemoveRoot();
        if (numSpans == 0) {
          this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
          return false;
        }
      }
      if (subSpans[0].doc() >= target && subSpans[0].doc != DocIdSetIterator.NO_MORE_DOCS) {
        matchPayload.clear();
        if (checkPayloads())
        {
            return true;
        }
        else
            return next();
      }
    }
  }
  
  public boolean checkPayloads() throws IOException
  {
      checker.clear();
      
      for(int i=0;i<subSpans.length;i++)
      {
          if (subSpans[i]!=null && subSpans[i].isChecked() && subSpans[i].doc()==subSpans[0].doc())
          {
            do
            {
                if (!checker.checkNextPayload(subSpans[i]))
                    return false;
            } while (subSpans[i].nextPayload());
          }
      }
      
      return checker.check();
  }
  
  @Override
  public String toString() {
    return getClass().getName() + "("+filter.toString()+")@"+
      (firstTime?"START":(more?(doc()+":"+start()+"-"+end()):"END"));
  }
}