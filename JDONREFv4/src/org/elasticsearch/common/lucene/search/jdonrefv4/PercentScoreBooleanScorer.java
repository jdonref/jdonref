package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.lucene.search.jdonrefv4.PercentScoreBooleanQuery.PercentScoreWeight;

/**
 *
 * @author Julien
 */
public class PercentScoreBooleanScorer extends Scorer implements ICountable
{
    protected final Scorer innerScorer;
    
    protected float score = 0.0f;
    
    protected boolean active = true;
    
    protected int maxCoord = 0;
    
    protected int minShouldMatch = 0;
    
    @Override
    public int getTokenMask()
    {
        return ((ICountable)innerScorer).getTokenMask();
    }
    
    @Override
    public int getMinFreq()
    {
        return ((ICountable)innerScorer).getMinFreq();
    }
    
    @Override
    public int getTypeMask()
    {
        return ((ICountable)innerScorer).getTypeMask();
    }
    
    /**
     * @return Le masque des tokens qui matchent
     */
    @Override
    public int tokenMatch()
    {
        return ((ICountable)innerScorer).tokenMatch();
    }
    /**
     * @return Le masque des tokens qui matchent plusieurs fois
     */
    @Override
    public int multiTokenMatch()
    {
        return ((ICountable)innerScorer).multiTokenMatch();
    }
    
    @Override
    public int count()
    {
        return ((ICountable)innerScorer).count();
    }
    
    public void setMaxCoord(int maxCoord)
    {
        this.maxCoord = maxCoord;
    }
    
    public int getMaxCoord()
    {
        return maxCoord;
    }
    
    public PercentScoreBooleanScorer(Weight w, Scorer innerScorer, boolean active, int maxCoord, int minShouldMatch)
    {
        super(w);
        this.innerScorer = innerScorer;
        this.active = active;
        this.minShouldMatch = minShouldMatch;
        this.maxCoord = maxCoord;
    }

    @Override
    public float score() throws IOException {
        return score;
    }

    @Override
    public int freq() throws IOException {
        return innerScorer.freq();
    }

    @Override
    public int docID() {
        return innerScorer.docID();
    }

    public int countBits(int value)
    {
        int count=0;
        while(value>0)
        {
            count+=value&1;
            value>>=1;
        }
        return count;
    }
    
    public int getDocScore(int doc) throws IOException
    {
        TermsEnum te = ((PercentScoreWeight)this.weight).searcher.getIndexReader().getTermVector(doc, "score").iterator(TermsEnum.EMPTY);
        BytesRef br = te.next();
        if (br==null) return 0;
        return Integer.parseInt(br.utf8ToString());
    }
    
    public boolean isRepetitionInDoc(int doc) throws IOException
    {
        Terms t = ((PercentScoreWeight)this.weight).searcher.getIndexReader().getTermVector(doc, "repetition");
        if (t==null) return false;
        TermsEnum te = t.iterator(TermsEnum.EMPTY);
        if (te==null) return false;
        BytesRef br = te.next();
        if (br==null) return false;
        return true;
    }

    int minfreq;
    
    public void makeScore(int doc) throws IOException
    {
        if (active)
        {
            int docScore = getDocScore(doc);
            if (docScore==0)
                this.score = 0;
            else if (((getTypeMask()&2)==0) && isRepetitionInDoc(doc))
                this.score = 0;
            else
            {
                this.score = innerScorer.score()*200/docScore;
                this.score *= ((ICountable)innerScorer).count();
                this.score /= this.maxCoord;
                this.score -= countBits(multiTokenMatch())*20;
            }
        }
        else
            this.score = innerScorer.score();
    }
    
    @Override
    public int nextDoc() throws IOException {
        
        int doc = -1;
        
        do
        {
            do
            {
                doc = innerScorer.nextDoc();
            } while (doc!=NO_MORE_DOCS && (((ICountable)innerScorer).count()<minShouldMatch));
        
            if (doc!=NO_MORE_DOCS)
            {
                makeScore(doc);
            }
        }
        while(this.score==0 && doc!=NO_MORE_DOCS);
        
        return doc;
    }

    @Override
    public int advance(int i) throws IOException {
        int doc = innerScorer.advance(i);
        
        do
        {
            while (doc!=NO_MORE_DOCS && (((ICountable)innerScorer).count()<minShouldMatch))
                doc = innerScorer.nextDoc();
            
            if (doc!=NO_MORE_DOCS)
            {
                makeScore(doc);
            }
        }
        while(this.score==0 && doc!=NO_MORE_DOCS);
        
        return doc;
    }

    @Override
    public long cost() {
        return innerScorer.cost();
    }
}