package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.lucene.search.jdonrefv4.PayloadAsScoreTermQuery.TermWeight;

/** Expert: A <code>Scorer</code> from the payload value.
 */
public class PayloadTermScorer extends Scorer
{
    protected final DocsAndPositionsEnum docsEnum;
    
    public static IntegerEncoder encoder = new IntegerEncoder();
    
    protected float score = 0.0f;
    
    public Weight weight()
    {
        return weight;
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
    public PayloadTermScorer(TermWeight weight, DocsAndPositionsEnum td)
    {
        super(weight);
        this.docsEnum = td;
    }
    
    protected void makeScore() throws IOException
    {
        if (docsEnum.freq()==0)
        {
            score = 0.0f;
        }
        else
        {
            int f = docsEnum.freq();
                    
            docsEnum.nextPosition();
            BytesRef payload = docsEnum.getPayload();
            score = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            
            while(--f>0 && score>200) // rustine en attendant mieux ! Multiples payloads
            {
                docsEnum.nextPosition();
                payload = docsEnum.getPayload();
                score = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            }
            if (score>200) score=0;
        }
    }
    
    /**
     * Advances to the next document matching the query. <br>
     * 
     * @return the document matching the query or NO_MORE_DOCS if there are no more documents.
     */
    @Override
    public int nextDoc() throws IOException
    {
        docsEnum.nextDoc();
        if (docID() == DocIdSetIterator.NO_MORE_DOCS) {
            return NO_MORE_DOCS;
        }
        
        makeScore();
        
        return docID();
    }
    
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
    int tmpDoc;
    for (tmpDoc = firstDocID; tmpDoc < max; tmpDoc = nextDoc()) {       
      collector.collect(tmpDoc);
    }
    return tmpDoc != NO_MORE_DOCS;
  }
    
    @Override
    public float score() throws IOException {
        assert docID() != NO_MORE_DOCS;

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
        docsEnum.advance(target);
        if (docID() == DocIdSetIterator.NO_MORE_DOCS) {
            return NO_MORE_DOCS;
        }
        
        makeScore();
        
        return docID();
    }

    @Override
    public long cost() {
        return docsEnum.cost();
    }

    /** Returns a string representation of this <code>TermScorer</code>. */
    @Override
    public String toString() {
        return "payloadTermScorer()";
    }

    @Override
    public int freq() throws IOException {
        return docsEnum.freq();
    }

    @Override
    public int docID() {
        return docsEnum.docID();
    }
}