package org.apache.lucene.search.spans;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.InPlaceMergeSorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * A Spans that is formed from the subspans of a GroupedPayloadSpanQuery
 * where the subspans are grouped by payload values.
 * 
 * @author Julien
 */
public class SpansGroupedByPayload extends Spans
{
  private boolean firstTime = true;
  private boolean more = false;

  /** The spans in the same order as the SpanNearQuery */
  private final Spans[] subSpans;

  /** Indicates that all subSpans have same doc() */
  private boolean inSameDoc = false;

  private int matchDoc = -1;
  private int matchStart = -1;
  private int matchEnd = -1;
  private List<byte[]> matchPayload;

  private final Spans[] subSpansByDoc;
  
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

  protected GroupedPayloadSpanQuery query;
  
  public SpansGroupedByPayload(GroupedPayloadSpanQuery gpsQuery, AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
    if (gpsQuery.getClauses().length < 2) {
      throw new IllegalArgumentException("Less than 2 clauses: "
                                         + gpsQuery);
    }
    SpanQuery[] clauses = gpsQuery.getClauses();
    subSpans = new Spans[clauses.length];
    matchPayload = new LinkedList<>();
    subSpansByDoc = new Spans[clauses.length];
    for (int i = 0; i < clauses.length; i++) {
      subSpans[i] = clauses[i].getSpans(context, acceptDocs, termContexts);
      subSpansByDoc[i] = subSpans[i]; // used in toSameDoc()
    }
    query = gpsQuery; // kept for toString() only.
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

  // inherit javadocs
  @Override
  public boolean next() throws IOException {

      firstTime = false;
      for (int i = 0; i < subSpans.length; i++) {
        if (! subSpans[i].next()) {
          more = false;
          return false;
        }
      }
      more = true;
      
    matchPayload.clear();
    return advanceAfterOrdered();
  }

  // inherit javadocs
  @Override
  public boolean skipTo(int target) throws IOException {
    if (firstTime) {
      firstTime = false;
      for (int i = 0; i < subSpans.length; i++) {
        if (! subSpans[i].skipTo(target)) {
          more = false;
          return false;
        }
      }
      more = true;
    } else if (more && (subSpans[0].doc() < target)) {
      if (subSpans[0].skipTo(target)) {
        inSameDoc = false;
      } else {
        more = false;
        return false;
      }
    }
    matchPayload.clear();
    return advanceAfterOrdered();
  }
  
  /** Advances the subSpans to just after payload values are grouped together.
   * @return true if there is such a match.
   */
  private boolean advanceAfterOrdered() throws IOException {
    while (more && (inSameDoc || toSameDoc())) {
      if (stretchToOrder()) {
        return true;
      }
    }
    return false; // no more matches
  }


  /** Advance the subSpans to the same document */
  private boolean toSameDoc() throws IOException {
    sorter.sort(0, subSpansByDoc.length);
    int firstIndex = 0;
    int maxDoc = subSpansByDoc[subSpansByDoc.length - 1].doc();
    while (subSpansByDoc[firstIndex].doc() != maxDoc) {
      if (! subSpansByDoc[firstIndex].skipTo(maxDoc)) {
        more = false;
        inSameDoc = false;
        return false;
      }
      maxDoc = subSpansByDoc[firstIndex].doc();
      if (++firstIndex == subSpansByDoc.length) {
        firstIndex = 0;
      }
    }
    for (int i = 0; i < subSpansByDoc.length; i++) {
      assert (subSpansByDoc[i].doc() == maxDoc)
             : " SpansGroupedByPayload.toSameDoc() spans " + subSpansByDoc[0]
                                 + "\n at doc " + subSpansByDoc[i].doc()
                                 + ", but should be at " + maxDoc;
    }
    inSameDoc = true;
    return true;
  }
  
  // TODO : make it paramaterable
  final int MAX_PAYLOADS = 50;
  
  protected boolean checkPayloads(int index)
  {
      if (index==0) return true;
      
      boolean check = false;
      int size = lastpayloads.size();
      
      for(int i=0;i<size;i++)
      {   
          byte[][] payloads = lastpayloads.get(i);
          if (Arrays.equals(payloads[index-1], payloads[index]))
          {
              check |= true;
          }
          else
          {
              boolean subcheck = true;
              for (int j = 0; subcheck && (j < index - 1); j++) {
                  if (Arrays.equals(payloads[j], payloads[index])) {
                        lastpayloads.remove(i); // it may improve performances
                      i--;
                      size--;
                      subcheck = false;
                  }
              }
              check |= subcheck;
          }
      }
      
      return check;
  }
  
  protected void addPayload(byte[] payload,int index,int count)
  {
      if (index==0)
      {
          byte[][] payloads = new byte[subSpans.length][];
          payloads[0] = payload;
          lastpayloads.add(payloads);
      }
      else
      if (count==0)
      {
          for(int i=0;i<lastpayloads.size();i++)
          {
              byte[][] payloads = lastpayloads.get(i);
              payloads[index] = payload;
          }
      }
      else
      {
        int size = lastpayloads.size()/(count);
        for(int i=0;i<size;i++)
        {
            byte[][] payloads = lastpayloads.get(i).clone();
            payloads[index] = payload;
              lastpayloads.add(payloads);
        }
      }
  }
  
  ArrayList<byte[][]> lastpayloads = new ArrayList<>();
  
  /**
   * Recursive version.
   * @param lastpayloads
   * @param index
   * @return 
   */
  protected boolean payloadGroupedTogether() throws IOException
  {
      lastpayloads.clear();
      
      for(int index=0;index<subSpans.length;index++)
      {
          Collection<byte[]> current_payloads = subSpans[index].getPayload();
          
          Iterator<byte[]> iterator = current_payloads.iterator();
          int count = 0;
          while (iterator.hasNext())
          {
              byte[] payload = iterator.next();
              addPayload(payload, index, count);
              count++;
          }
          
          boolean check = checkPayloads(index);
          
          if (!check) return false;
      }
      return true;
  }
  
  /** Order the subSpans within the same document by advancing all later spans
   * after the previous one.
   */
  private boolean stretchToOrder() throws IOException {
    matchDoc = subSpans[0].doc();
    
    if (!payloadGroupedTogether())
    {
        if (!subSpans[0].next())
        {
            more = false;
        }
        inSameDoc = false;
    }
    
    return inSameDoc;
  }

  @Override
  public String toString() {
    return getClass().getName() + "("+query.toString()+")@"+
      (firstTime?"START":(more?(doc()+":"+start()+"-"+end()):"END"));
  }
}
