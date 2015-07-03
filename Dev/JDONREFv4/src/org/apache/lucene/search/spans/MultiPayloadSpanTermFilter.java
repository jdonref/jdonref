package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.automaton.CompiledAutomaton;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermFilter extends Filter implements IGetSpans
{
  protected Term term;
  protected int termCountPayloadFactor = MultiPayloadTermSpans.NOTERMCOUNTPAYLOADFACTOR;
  protected int order;
  protected boolean checked = true;
  private boolean finalWildCard = false;

    public boolean isFinalWildCard() {
        return finalWildCard;
    }

    public void setFinalWildCard(boolean finalWildCard) {
        this.finalWildCard = finalWildCard;
    }


    @Override
  public Object clone() throws CloneNotSupportedException
  {
      MultiPayloadSpanTermFilter clone = (MultiPayloadSpanTermFilter) super.clone();
      clone.term = term;
      clone.termCountPayloadFactor = termCountPayloadFactor;
      clone.order = order;
      clone.checked = checked;
      return clone;
  }
  
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public Term getTerm()
    {
        return term;
    }
    
    public String getField()
    {
        return term.field();
    }
    
  /** Construct a SpanTermQuery matching the named term's spans. */
  public MultiPayloadSpanTermFilter(Term term)
  {
      this.term = term;
  }
  
  public int termCountPayloadFactor()
  {
      return termCountPayloadFactor();
  }
  
  public void setTermCountPayloadFactor(int factor)
  {
      this.termCountPayloadFactor = factor;
  }
  
  public int getOrder()
  {
      return this.order;
  }
  
    void setOrder(int i) {
        this.order = i;
    }
    
  /**
   * :P
   * @param postings
   * @param term
   * @return 
   */
  protected MultiPayloadTermSpans makeTermSpans(DocsAndPositionsEnum postings,Term term, AtomicReader reader, Bits acceptDocs)
  {
      MultiPayloadTermSpans span = new MultiPayloadTermSpans(postings, term, termCountPayloadFactor, reader, acceptDocs );
      span.setOrder(order);
      span.setChecked(checked);
      return span;
  }
  
  public MultiPayloadTermSpans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
    TermContext termContext = null;
    TermsEnum termsEnum = null;
    if (termContexts!=null)
      termContext = termContexts.get(term);
    Boolean state = false;
    Boolean change = false;
    if (termContext == null) {
      // this happens with span-not query, as it doesn't include the NOT side in extractTerms()
      // so we seek to the term now in this segment..., this sucks because its ugly mostly!
      final Fields fields = context.reader().fields();
      if (fields != null) {
        final Terms terms = fields.terms(term.field());
        if (terms != null) {
          termsEnum = terms.iterator(null);
          if(this.isFinalWildCard()){   
            BytesRef prefix = term.bytes();
            if (prefix.length > 2) {
                termsEnum = new PrefixTermsEnum(termsEnum, prefix);
                change = true;
                state = true;
//                termsEnum.next();
//                state = p.termState();
//                 state = termsEnum.termState(); 
            }else {
                if (termsEnum.seekExact(prefix)) 
                    state = true;         
            }
          }
          else{
            if (termsEnum.seekExact(term.bytes())) { 
                state = true;
            } 
          }    
        } 
      } 
    } else {
         TermState tstate = termContext.get(context.ord);
         termsEnum = context.reader().terms(term.field()).iterator(null);
         termsEnum.seekExact(term.bytes(), tstate);
         state = true;
    }
    if (state == false) { // term is not present in that reader
      return MultiPayloadTermSpans.emptyTermSpans();
    }
    
    DocsAndPositionsEnum mdpe;
    if(change)
        mdpe = new MultiDocsAndPositionsEnum(termsEnum, acceptDocs);
    else
        mdpe = termsEnum.docsAndPositions(acceptDocs, null, DocsAndPositionsEnum.FLAG_PAYLOADS);
 
    if (mdpe != null) {
        return makeTermSpans(mdpe, term, context.reader(), acceptDocs);
    } else {
        // term does exist, but has no positions
        throw new IllegalStateException("field \"" + term.field() + "\" was indexed without position data; cannot run SpanTermFilter (term=" + term.text() + ")");
    }

  }
  
  public class MultiDocsAndPositionsEnum extends DocsAndPositionsEnum
  {
      
      DocsAndPositionsEnum[] terms;
      DocsAndPositionsEnum[] termsStartOffset;
      int numTerm;
      int numTermStartOffset ;
      
      protected List<byte[]> matchPayload = new LinkedList<>();
      protected boolean firstTime = true;
      protected boolean firstTimeStartOffset = true;
      
      protected int matchDoc = -1;
      protected int startOffset = -1;
      protected int endOffset = -1;
      protected int frequency = -1;
      
      protected BytesRef payload = null;
      
      public MultiDocsAndPositionsEnum(TermsEnum termsEnum, Bits acceptDocs)
      {
        // contruire terms
        List<DocsAndPositionsEnum> dape1 = new ArrayList<>();
        BytesRef term;
        ArrayList<String> words = new ArrayList<>();
        try {
            while((term=termsEnum.next())!=null){
                words.add(term.utf8ToString());
                DocsAndPositionsEnum docPosEnum1 = termsEnum.docsAndPositions(acceptDocs, null, DocsAndPositionsEnum.FLAG_PAYLOADS);
           //         docPosEnum1.nextDoc();
                dape1.add(docPosEnum1);
            }
            terms = dape1.toArray(new DocsAndPositionsEnum[dape1.size()]);
            numTerm=terms.length;
            heapify();
            
        } catch (IOException ex) {
            Logger.getLogger(MultiPayloadSpanTermFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      
      
    public List<DocsAndPositionsEnum> ArrayCopy(List<DocsAndPositionsEnum> field){
      List<DocsAndPositionsEnum> fieldCopy = new ArrayList<DocsAndPositionsEnum>();
      for (DocsAndPositionsEnum dape : field) {
          fieldCopy.add(dape);
      }
      return fieldCopy;
  } 
  @Override
  public int nextDoc() throws IOException
  {
      firstTimeStartOffset = true;
    firstTime = false;
    if (numTerm==0){
        this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
        return matchDoc;
    }
    this.matchDoc = terms[0].docID();
    while(true) {
      int next = terms[0].nextDoc();
      if (next != DocIdSetIterator.NO_MORE_DOCS) {
        heapAdjust(0);
      } else {
        heapRemoveRoot();
        if (numTerm == 0) {
          this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
          return matchDoc;
        }
      }
      if (terms[0].docID() != matchDoc && terms[0].docID()!=DocIdSetIterator.NO_MORE_DOCS) { // check if heap has been adjusted
        this.matchDoc = terms[0].docID();
          //matchPayload.clear();
            return matchDoc;
      }
    }
  }
  
  @Override
  public int advance(int target) throws IOException {  //interet d'avoir deux methode similaire !!!!!!
      firstTimeStartOffset = true;
    if (numTerm==0){
        this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
        return matchDoc;
    }
    while(true) {
      // NB: don't know why subScorers[0] may be null
      if (terms[0].advance(target) != DocIdSetIterator.NO_MORE_DOCS) {
        
        heapAdjust(0);
      } else {
        heapRemoveRoot();
        if (numTerm == 0) {
          this.matchDoc = DocIdSetIterator.NO_MORE_DOCS;
          return matchDoc;        
        }
      }
      if (terms[0].docID() >= target && terms[0].docID() != DocIdSetIterator.NO_MORE_DOCS) {
          this.matchDoc = terms[0].docID();
        matchPayload.clear();
            return matchDoc;
      }
    }
  }
      
        @Override
    public int nextPosition() throws IOException{
        if (firstTimeStartOffset)
        {
            
            // crée le tableau avec uniquement les DocEnum correspondant au docId
            // les autres à null
            // initialise numTermStartOffset en conséquence
            
            List<DocsAndPositionsEnum> dape = new ArrayList<>();
            for(DocsAndPositionsEnum dap : terms){
                if(dap.docID()==matchDoc) dape.add(dap);
            }
            termsStartOffset = dape.toArray(new DocsAndPositionsEnum[dape.size()]);
            numTermStartOffset=termsStartOffset.length;       
            firstTimeStartOffset = false;
            heapifyStartOffset(); 
        }
        
        if (numTermStartOffset==0){
            this.startOffset = DocsAndPositionsEnum.FLAG_OFFSETS;
            return startOffset;
        }
        while(true) 
        {
            int next = termsStartOffset[0].nextPosition();
            this.startOffset = termsStartOffset[0].startOffset();
            this.endOffset = termsStartOffset[0].endOffset();
            this.payload = termsStartOffset[0].getPayload();
            
            if (next != DocsAndPositionsEnum.FLAG_OFFSETS) {
                heapAdjustStartOffSet(0);
            } else {
                heapRemoveRootStartOffset();
                if (numTermStartOffset == 0) {
                this.startOffset = DocsAndPositionsEnum.FLAG_OFFSETS;
                return startOffset;
                }
            }
            if (termsStartOffset[0].startOffset() == startOffset && termsStartOffset[0].startOffset()!=DocsAndPositionsEnum.FLAG_OFFSETS) 
            { // check if heap has been adjusted
                matchPayload.clear();
                return startOffset;
//                if (checkPayloads())
//                {
//                    return true;
//                }
            }
        }
    }
        
        
        
        @Override
        public int startOffset() {
            return startOffset; 
        }
        @Override
        public int endOffset() {
            return endOffset;
        }
        
        @Override
        public BytesRef getPayload() throws IOException{
            return payload;
        }

        @Override
        public int freq() throws IOException{
            int freq = 0;
            for(DocsAndPositionsEnum doc : terms)
            {
                if (doc.docID()==this.matchDoc)
                    freq += doc.freq();
            }
            return freq;
        }

        @Override
        public int docID(){
            return matchDoc;
        }
        
  
    protected final void heapifyStartOffset() throws IOException {
        for (int i = (numTermStartOffset >> 1) - 1; i >= 0; i--) {
            heapAdjustStartOffSet(i);
        }
    }
  
  
  protected final void heapAdjustStartOffSet(int root) throws IOException {
    DocsAndPositionsEnum scorer = termsStartOffset[root];
    int tmpStartOffSet = scorer.startOffset();
    int i = root;
    while (i <= (numTermStartOffset >> 1) - 1) {
      int lchild = (i << 1) + 1;
      DocsAndPositionsEnum lscorer = termsStartOffset[lchild];
      int lStartOffSet = lscorer.startOffset();
      int rStartOffSet = Integer.MAX_VALUE; 
      int rchild = (i << 1) + 2;
      DocsAndPositionsEnum rscorer = null;
      if (rchild < numTermStartOffset) {
        rscorer = termsStartOffset[rchild];
        rStartOffSet = rscorer.startOffset();
      }
      if (lStartOffSet < tmpStartOffSet) {
        if (rStartOffSet < lStartOffSet) {
          termsStartOffset[i] = rscorer;
          termsStartOffset[rchild] = scorer;
          i = rchild;
        } else {
          termsStartOffset[i] = lscorer;
          termsStartOffset[lchild] = scorer;
          i = lchild;
        }
      } else if (rStartOffSet < tmpStartOffSet) {
        termsStartOffset[i] = rscorer;
        termsStartOffset[rchild] = scorer;
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
  protected final void heapRemoveRootStartOffset() throws IOException {
    if (numTermStartOffset == 1) {
      termsStartOffset[0] = null;
      numTermStartOffset = 0;
    } else {
      termsStartOffset[0] = termsStartOffset[numTermStartOffset - 1];
      termsStartOffset[numTermStartOffset - 1] = null;
      --numTermStartOffset;
      heapAdjustStartOffSet(0);
    }
  }
  
        /** 
   * extract from DisjunctionScorer
   * Organize subScorers into a min heap with scorers generating the earliest document on top.
   */
    protected final void heapify() {
        for (int i = (numTerm >> 1) - 1; i >= 0; i--) {
            heapAdjust(i);
        }
    }

    
  /** 
   * extract from DisjunctionScorer
   * The subtree of subScorers at root is a min heap except possibly for its root element.
   * Bubble the root down as required to make the subtree a heap.
   */
  protected final void heapAdjust(int root) {
    DocsAndPositionsEnum scorer = terms[root];
    int tmpDoc = scorer.docID();
    int i = root;
    while (i <= (numTerm >> 1) - 1) {
      int lchild = (i << 1) + 1;
      DocsAndPositionsEnum lscorer = terms[lchild];
      int ldoc = lscorer.docID();
      int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
      DocsAndPositionsEnum rscorer = null;
      if (rchild < numTerm) {
        rscorer = terms[rchild];
        rdoc = rscorer.docID();
      }
      if (ldoc < tmpDoc) {
        if (rdoc < ldoc) {
          terms[i] = rscorer;
          terms[rchild] = scorer;
          i = rchild;
        } else {
          terms[i] = lscorer;
          terms[lchild] = scorer;
          i = lchild;
        }
      } else if (rdoc < tmpDoc) {
        terms[i] = rscorer;
        terms[rchild] = scorer;
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
    if (numTerm == 1) {
      terms[0] = null;
      numTerm = 0;
    } else {
      terms[0] = terms[numTerm - 1];
      terms[numTerm - 1] = null;
      --numTerm;
      heapAdjust(0);
    }
  }

    

    @Override
    public long cost() {
        long minCost = Long.MAX_VALUE;
        for (int i = 0; i < terms.length; i++) {
        minCost = Math.min(minCost, terms[i].cost());
        }
        return minCost;
    }       
  }

    @Override
    public DocIdSet getDocIdSet(final AtomicReaderContext context, final Bits acceptDocs) throws IOException
    {
        return new DocIdSet()
        {
            @Override
            public DocIdSetIterator iterator() throws IOException {
                return getSpans(context,acceptDocs,null).getPostings();
            }
        };
    }
    
    public String toString()
    {
        return term.toString();
    }
}