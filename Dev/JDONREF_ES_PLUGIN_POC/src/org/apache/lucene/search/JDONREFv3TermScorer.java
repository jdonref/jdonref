package org.apache.lucene.search;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.similarities.Similarity;

/** Expert: A <code>Scorer</code> for documents matching a <code>Term</code>.
 */
public class JDONREFv3TermScorer extends Scorer {

    protected final DocsEnum docsEnum;
    protected final Similarity.SimScorer docScorer;
    protected boolean last;
    protected int index;
    protected IndexSearcher searcher;
    protected int maxSizePerType;
    
    protected JDONREFv3Scorer parentScorer;

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

    public int getMaxSizePerType() {
        return maxSizePerType;
    }

    public void setMaxSizePerType(int maxSizePerType) {
        this.maxSizePerType = maxSizePerType;
    }

    public JDONREFv3Scorer getParentScorer() {
        return parentScorer;
    }

    public void setParentScorer(JDONREFv3Scorer parentScorer) {
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
    public JDONREFv3TermScorer(Weight weight, DocsEnum td, Similarity.SimScorer docScorer, int index, IndexSearcher searcher, int maxSizePerType) {
        super(weight);
        this.docScorer = docScorer;
        this.docsEnum = td;
        this.index = index;
        this.searcher = searcher;
        this.maxSizePerType = maxSizePerType;
    }

    @Override
    public int docID() {
        return docsEnum.docID();
    }

    @Override
    public int freq() throws IOException {
        return docsEnum.freq();
    }
    
    public int nextReachLimitDoc() throws IOException
    {
        int doc;
        Document d;
        String type;
        
        do
        {
            doc = docsEnum.nextDoc();
            if (doc == NO_MORE_DOCS) break;
            d = this.searcher.doc(doc);
            type = this.parentScorer.getType(d);
        } while(this.parentScorer.typeReachLimit(type));
        
        return doc;
    }
    
    /**
     * Advances to the next document matching the query. <br>
     * 
     * @return the document matching the query or NO_MORE_DOCS if there are no more documents.
     */
    @Override
    public int nextDoc() throws IOException
    {
        if (this.parentScorer==null)
            return docsEnum.nextDoc();
        else
            return nextReachLimitDoc();
    }
        
    
    boolean debugbar = false;
    double malus;

    public double getMalus() {
        return malus;
    }
    boolean adressNumberPresent = false;

    public boolean getAdressNumberPresent() {
        return adressNumberPresent;
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
    int doc;
    for (doc = firstDocID; doc < max; doc = nextDoc()) {
      if (this.parentScorer!=null && this.parentScorer.debugDoc==doc)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc :"+doc+" start collect for term "+((JDONREFv3TermQuery)getWeight().getQuery()).getTerm().field());
        
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

    public int advanceReachLimit(int target) throws IOException {
        int doc = docsEnum.advance(target);
        
        if (doc!=NO_MORE_DOCS)
        {
            if (target==this.parentScorer.debugDoc && doc!=target )
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" miss "+target);
            Document d = this.searcher.doc(doc);
            String type = this.parentScorer.getType(d);
            if (this.parentScorer.typeReachLimit(type))
            {
                if (target==this.parentScorer.debugDoc && doc!=target )
                    Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" "+target+" reach "+((JDONREFv3TermQuery)weight.getQuery()).getTerm().field()+" limit");
                return nextDoc();
            }
        }
        
        return doc;
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
        if (this.parentScorer==null)
            return docsEnum.advance(target);
        else
            return advanceReachLimit(target);
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
}