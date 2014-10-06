package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author Julien
 */
public class OrPayloadChecker extends PayloadChecker
{
    PayloadChecker[] checkers;
    
    public PayloadChecker[] getCheckers() {
        return checkers;
    }
    
    public OrPayloadChecker(PayloadChecker... checkers)
    {
        this.checkers = checkers;
    }
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        for(int i=0;i<checkers.length;i++)
        {
            checkers[i].checkNextPayload(subspan);
        }
        return true;
    }

    @Override
    public boolean check()
    {
        for(int i=0;i<checkers.length;i++)
            if (checkers[i].check())
                return true;
        return false;
    }

    @Override
    public void clear() {
        for(int i=0;i<checkers.length;i++)
            checkers[i].clear();
    }
    
    public String toString()
    {
        String res = "OR[";
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