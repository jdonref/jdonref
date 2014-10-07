package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * 
 * @author Julien
 */
public class XorPayloadChecker extends AbstractPayloadChecker
{
    IPayloadChecker[] checkers;
    
    public IPayloadChecker[] getCheckers() {
        return checkers;
    }
    
    public XorPayloadChecker(IPayloadChecker... checkers)
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
        int count = 0;
        for(int i=0;count<=1 && i<checkers.length;i++)
        {
            if (checkers[i].check())
            {
                count++;
            }
        }
        return count==1;
    }

    @Override
    public void clear() {
        for(int i=0;i<checkers.length;i++)
            checkers[i].clear();
    }
    
    public String toString()
    {
        String res = "XOR[";
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