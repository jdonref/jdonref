package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;

/**
 * 
 * @author Julien
 */
public class XorPayloadChecker extends AbstractIPayloadCheckerCollectionChecker
{
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
    public XorPayloadChecker clone()
    {
        XorPayloadChecker checker = new XorPayloadChecker(checkers.clone());
        return checker;
    }
}