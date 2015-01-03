package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.IMultiPayload;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4TermQuery.TermWeight;

/** Expert: A <code>Scorer</code> for documents matching a <code>Term</code>.
 */
public class JDONREFv4TermScorer extends Scorer implements IMultiPayload
{
    public static int NOTERMCOUNTPAYLOADFACTOR = -1;
    public static int NOTERMCOUNTBYPAYLOAD = -1;
    
    protected final DocsAndPositionsEnum docsEnum;
    protected final Similarity.SimScorer docScorer;
    protected boolean last;
    protected int index;
    protected IndexSearcher searcher;
    
    protected JDONREFv4Scorer parentScorer;
    protected  int order;
    protected  boolean checked;
    protected int termCountPayloadFactor= MultiPayloadTermSpans.NOTERMCOUNTPAYLOADFACTOR;
    protected final AtomicReader reader;
    protected int doc;
    protected int freq;
    protected   int count;
    protected int position;
    protected byte[] currentPayload;
    protected int currentCountByPayload;
    protected int currentIntegerTypeAsPayload;
    public static IntegerEncoder encoder = new IntegerEncoder();
    protected int integerTypeAsPayloadFactor = MultiPayloadTermSpans.NOINTEGERTYPEASPAYLOADFACTOR;
    protected int[] payloads;
    
    public Weight weight()
    {
        return weight;
    }

    public IndexSearcher getSearcher() {
        return searcher;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isLast() {
        return last;
    }

    /**
     * Why TermScorer is useFull.
     */
    public void setIsLast() {
        last = true;
    }

    public JDONREFv4Scorer getParentScorer() {
        return parentScorer;
    }

    public void setParentScorer(JDONREFv4Scorer parentScorer) {
        this.parentScorer = parentScorer;
    }

    /**
     * Construct a <code>TermScorer</code>.
     * 
     * @param weight
     *          The weight of the <code>Term</code> in the query.
     * @param td
     *          An iterator over the documents matching the <code>Term</code>.
     * @param docScorer
     *          The </code>Similarity.SimScorer</code> implementation 
     *          to be used for score computations.
     */
    public JDONREFv4TermScorer(TermWeight weight, DocsAndPositionsEnum td, Similarity.SimScorer docScorer, int index, IndexSearcher searcher, AtomicReader reader,int termCountPayloadFactor, int order, boolean checked)
    {
        super(weight);
        this.docScorer = docScorer;
        this.docsEnum = td;
        this.index = index;
        this.searcher = searcher;
        this.reader = reader;
        
        this.termCountPayloadFactor = termCountPayloadFactor;
        this.order = order;
        this.checked = checked;
    }
    
    @Override
    public int docID() {
        return docsEnum.docID();
    }

    @Override
    public int freq() throws IOException {
        return docsEnum.freq();
    }
    
    protected Document document;
    protected int documentDoc;
    
    @Override
    public Document document() throws IOException
    {
        if (document==null || doc!=documentDoc)
        {
            document = reader==null?null:reader.document(doc);
            documentDoc = doc;
        }
        return document;
    }
    
    /**
     * Advances to the next document matching the query. <br>
     * 
     * @return the document matching the query or NO_MORE_DOCS if there are no more documents.
     */
    @Override
    public int nextDoc() throws IOException
    {
        doc = docsEnum.nextDoc();
        if (doc == DocIdSetIterator.NO_MORE_DOCS) {
            return NO_MORE_DOCS;
        }
        
        freq = docsEnum.freq();
        payloads = new int[freq];
        count = 0;
        
        nextPayload();
        
        return doc;
    }
    
    public boolean nextPayload() throws IOException
    {
        if (count++ < freq)
        {
            position = docsEnum.nextPosition();
            BytesRef payload = docsEnum.getPayload();
            if (payload != null)
            {
                currentPayload = convertPayload(extractPayload(payload));
                currentCountByPayload = extractCountTermByPayload(payload);
                currentIntegerTypeAsPayload = extractIntegerTypeAsPayload(payload);
            }
            else
            {
                currentPayload = null;
                currentCountByPayload = -1;
                currentIntegerTypeAsPayload = -1;
            }
            payloads[count-1] = currentPayload==null?0:PayloadHelper.decodeInt(currentPayload,0);
            return true;
        }
        return false;
    }
    
    
    protected BytesRef extractPayload(BytesRef payload)
    {
        if (termCountPayloadFactor!=NOTERMCOUNTPAYLOADFACTOR)
        {
            int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            //int payloadValue     = payload/termCountPayloadFactor;
            int payloadValue = payloadStuff%termCountPayloadFactor;
            
            return encoder.encode(Integer.toString(payloadValue).toCharArray());
        }
        else
            return payload;
    }
    
    protected int extractCountTermByPayload(BytesRef payload)
    {
        if (termCountPayloadFactor!=NOTERMCOUNTPAYLOADFACTOR)
        {
            int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            //int payloadValue     = payload/termCountPayloadFactor;
            int payloadTermCount = (payloadStuff/termCountPayloadFactor)%integerTypeAsPayloadFactor;
            
            return payloadTermCount;
        }
        else
            return NOTERMCOUNTBYPAYLOAD;
    }
    
    protected int extractIntegerTypeAsPayload(BytesRef payload)
    {
        if (integerTypeAsPayloadFactor!=NOTERMCOUNTPAYLOADFACTOR)
        {
            int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            //int payloadValue     = payload/termCountPayloadFactor;
            int integerTypeAsPayload = payloadStuff/integerTypeAsPayloadFactor;
            
            return integerTypeAsPayload;
        }
        else
            return NOTERMCOUNTPAYLOADFACTOR;
    }
    
    protected byte[] convertPayload(BytesRef payload) {
        final byte[] bytes;
        if (payload != null) {
            bytes = new byte[payload.length];
            System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);
        } else {
            bytes = null;
        }
        return bytes;
    }
    
    boolean debugbar = false;

  /**
   * Expert: Collects matching documents in a range. Hook for optimization.
   * Note, <code>firstDocID</code> is added to ensure that {@link #nextDoc()}
   * was called before this method.
   * 
   * @param collector
   *          The collector to which all matching documents are passed.
   * @param max
   *          Do not score documents past this.
   * @param firstDocID
   *          The first document ID (ensures {@link #nextDoc()} is called before
   *          this method.
   * @return true if more matching documents may remain.
   */
  public boolean score(Collector collector, int max, int firstDocID) throws IOException {
    assert docID() == firstDocID;
    collector.setScorer(this);
    int doc;
    for (doc = firstDocID; doc < max; doc = nextDoc()) {
      if (this.parentScorer!=null && this.parentScorer.debugDoc==doc)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc :"+doc+" start collect for term "+((JDONREFv4TermQuery)getWeight().getQuery()).getTerm().field());
        
      collector.collect(doc);
    }
    return doc != NO_MORE_DOCS;
  }
    
    @Override
    public float score() throws IOException {
        assert docID() != NO_MORE_DOCS;

        float score = docScorer.score(docsEnum.docID(), docsEnum.freq());

        return score;
    }
    
    /**
     * Advances to the first match beyond the current whose document number is
     * greater than or equal to a given target. <br>
     * The implementation uses {@link DocsEnum#advance(int)}.
     * 
     * @param target
     *          The target document number.
     * @return the matching document or NO_MORE_DOCS if none exist.
     */
    @Override
    public int advance(int target) throws IOException {
           return docsEnum.advance(target);
    }

    @Override
    public long cost() {
        return docsEnum.cost();
    }

    /** Returns a string representation of this <code>TermScorer</code>. */
    @Override
    public String toString() {
        return "scorer(" + weight + ")";
    }

    public int[] getCurrentPayloads()
    {
        return payloads;
    }
    
    public int getCurrentIntegerTypeAsPayload()
    {
        return currentIntegerTypeAsPayload;
    }
    
    @Override
    public int getCurrentCountByPayload() {
        return currentCountByPayload;
    }

    @Override
    public byte[] getCurrentPayload() {
        return currentPayload;
    }

    @Override
    public int getOrder() {
        return order;
    }
}