package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;

/**
 *
 * @author Julien
 */
public class OrPayloadChecker extends AbstractIPayloadCheckerCollectionChecker
{
    public OrPayloadChecker(IPayloadChecker... checkers)
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
    public OrPayloadChecker clone()
    {
        OrPayloadChecker checker = new OrPayloadChecker(checkers.clone());
        return checker;
    }
}