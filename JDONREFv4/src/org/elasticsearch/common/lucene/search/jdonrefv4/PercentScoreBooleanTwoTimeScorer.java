package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

/**
 *
 * @author Julien
 */
public class PercentScoreBooleanTwoTimeScorer extends Scorer implements ICountable
{
    protected Scorer innerScorer;
    protected Scorer secondScorer;
    
    protected float score = 0.0f;
    
    protected int maxFreq = 10000;
    protected int limit = 100;  // nombre maximum de documents qui matchent et dont tous les termes dépassent maxFreq
    protected int limit2 = 200;
    protected int count = 0;
    
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
    
    public int getLimit()
    {
        return limit;
    }
    
    public int getLimit2()
    {
        return limit2;
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
    
    public PercentScoreBooleanTwoTimeScorer(Weight w, Scorer innerScorer, Scorer secondScorer, int limit, int limit2)
    {
        super(w);
        this.innerScorer = innerScorer;
        this.limit = limit;
        this.limit2 = limit2;
        this.secondScorer = secondScorer;
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
        if (count>limit2) return NO_MORE_DOCS;
        return innerScorer.docID();
    }

    int minfreq;
    
    boolean justswitched = false;
    boolean switched = false;
    
    public void switchScorer()
    {
        Scorer temp = innerScorer;
        innerScorer = secondScorer;
        secondScorer = temp;
        
        justswitched = switched = true;
    }
    
    @Override
    public int nextDoc() throws IOException {
        
        int doc = -1;
        
        do
        {
            if (justswitched)
            {
                doc = innerScorer.advance(doc); // juste une fois
                justswitched = false;
            }
            else
                doc = innerScorer.nextDoc();
        
            if (doc!=NO_MORE_DOCS)
            {
                if (!switched && (minfreq=getMinFreq())>maxFreq && count>limit)
                {
                    //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" Reach limit 1");
                    switchScorer();
                    this.score = 0;
                    //this.score = innerScorer.score();
                }
                else if (switched && (minfreq=getMinFreq())>maxFreq && count>limit2)
                {
                    //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" Reach limit 2");
                    this.score = 0;
                    return NO_MORE_DOCS;
                }
                else
                {
                    //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" under max Freq : "+getMinFreq()+"/"+maxFreq);
                    this.score = innerScorer.score();
                }
            }
        }
        while(this.score==0 && doc!=NO_MORE_DOCS);
        
        if (minfreq>maxFreq)
        {
            //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" count="+count+"/"+limit+","+limit2);
            count++;
        }
        
        return doc;
    }

    @Override
    public int advance(int i) throws IOException {
        int doc = innerScorer.advance(i);
        
        do
        {
            if (justswitched) // juste une fois
            {
                doc = innerScorer.advance(doc);
                justswitched = false;
            }
            else
                doc = innerScorer.nextDoc();
            
            if (doc!=NO_MORE_DOCS)
            {
                if (!switched && (minfreq=getMinFreq())>maxFreq && count>limit)
                {
                    //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" Reach limit 1");
                    switchScorer();
                    this.score = 0;
                    //this.score = innerScorer.score();
                }
                else if (switched && (minfreq=getMinFreq())>maxFreq && count>limit2)
                {
                    //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" Reach limit 2");
                    this.score = 0;
                    return NO_MORE_DOCS;
                }
                else
                {
                    //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" under max Freq : "+getMinFreq()+"/"+maxFreq);
                    this.score = innerScorer.score();
                }
            }
        }
        while(this.score==0 && doc!=NO_MORE_DOCS);
        
        if (minfreq>maxFreq)
        {
            //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" count="+count+"/"+limit+","+limit2);
            count++;
        }
        
        return doc;
    }

    @Override
    public long cost() {
        return innerScorer.cost();
    }
}