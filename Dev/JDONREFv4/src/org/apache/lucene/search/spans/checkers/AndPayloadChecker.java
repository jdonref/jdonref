package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Julien
 */
public class AndPayloadChecker extends AbstractIPayloadCheckerCollectionChecker
{  
    public AndPayloadChecker(IPayloadChecker... checkers)
    {
        this.checkers = new ArrayList<>();
        this.checkers.addAll(Arrays.asList(checkers));
    }
    
    @Override
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
    {
        for(int i=0;i<checkers.size();i++)
        {
            if (!checkers.get(i).checkNextPayload(subspan))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean check()
    {
        for(int i=0;i<checkers.size();i++)
            if (!checkers.get(i).check())
                return false;
        return true;
    }
    
    @Override
    public AndPayloadChecker clone()
    {
        IPayloadChecker[] clone = new IPayloadChecker[this.checkers.size()];
        for(int i=0;i<this.checkers.size();i++)
            clone[i] = this.checkers.get(i).clone();
        
        AndPayloadChecker checker = new AndPayloadChecker(clone);
        return checker;
    }
    
    @Override
    public String toString()
    {
        String res = "AND[";
        for(int i=0;i<checkers.size();i++)
        {
            if (i>0) res += ",";
            res += checkers.get(i).toString();
        }
        res += "]";
        return res;
    }
}
