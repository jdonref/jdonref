package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.elasticsearch.common.lucene.search.jdonrefv4.PercentScoreTermQuery.PercentScoreTermWeight;

/** Expert: A <code>Scorer</code> with a mask
 */
public class PercentScoreTermScorer extends Scorer implements ICountable 
{
    protected final DocsEnum docsEnum;
    
    public static IntegerEncoder encoder = new IntegerEncoder();
    
    protected float score = 0.0f;
    protected float maxScore = 0.0f;
    protected int typemask = 0;
    protected int tokenmask = 0;
    
    protected int freq;
    
    protected int ensureTokenmask = -1;
    
    public void ensureToken(int mask)
    {
        this.ensureTokenmask = mask;
    }
    
    public int getFreq()
    {
        return freq;
    }
    
    public int getMinFreq()
    {
        return freq;
    }

    public int getTypeMask() {
        return typemask;
    }

    public int getTokenMask() {
        return tokenmask;
    }
    
    /**
     * @return Le masque des tokens qui matchent
     */
    public int tokenMatch()
    {
        return tokenmask;
    }
    
    /**
     * @return Le masque des tokens qui matchent plusieurs fois
     */
    public int multiTokenMatch()
    {
        return 0;
    }
    
    @Override
    public int count()
    {
        return 1;
    }
    
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
     * @param maxScore
     *          The maximum score this term to give
     * @param typemask
     *          Le type associé à la recherche
     * @param tokenmask
     *          Le token associé à la recherche
     */
    public PercentScoreTermScorer(PercentScoreTermWeight weight, DocsEnum td, float maxScore, int typemask, int tokenmask, int freq)
    {
        super(weight);
        this.docsEnum = td;
        this.maxScore = maxScore;
        this.typemask = typemask;
        this.tokenmask = tokenmask;
        this.freq = freq;
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
        
        this.score = maxScore;
        
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
        
        this.score = maxScore;
        
        return docID();
    }

    @Override
    public long cost() {
        return docsEnum.cost();
    }

    /** Returns a string representation of this <code>TermScorer</code>. */
    @Override
    public String toString() {
        return "PercentScoreTermScorer()";
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