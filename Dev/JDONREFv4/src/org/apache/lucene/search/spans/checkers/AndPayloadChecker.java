package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author Julien
 */
public class AndPayloadChecker extends PayloadChecker
{
    PayloadChecker[] checkers;

    public PayloadChecker[] getCheckers() {
        return checkers;
    }
    
    public AndPayloadChecker(PayloadChecker... checkers)
    {
        this.checkers = checkers;
    }
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        for(int i=0;i<checkers.length;i++)
        {
            if (!checkers[i].checkNextPayload(subspan))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean check()
    {
        for(int i=0;i<checkers.length;i++)
            if (!checkers[i].check())
                return false;
        return true;
    }

    @Override
    public void clear() {
        for(int i=0;i<checkers.length;i++)
            checkers[i].clear();
    }
    
    public Object clone()
    {
        AndPayloadChecker checker = new AndPayloadChecker(checkers.clone());
        return checker;
    }
    
    public String toString()
    {
        String res = "AND[";
        for(int i=0;i<checkers.length;i++)
        {
            if (i>0) res += ",";
            res += checkers[i].toString();
        }
        res += "]";
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        for(int i=0;i<checkers.length;i++)
        {
            checkers[i].setQuery(query);
        }
    }
}
