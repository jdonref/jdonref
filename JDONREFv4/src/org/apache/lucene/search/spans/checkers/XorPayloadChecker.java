package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;

/**
 * 
 * @author Julien
 */
public class XorPayloadChecker extends AbstractIPayloadCheckerCollectionChecker
{
    public XorPayloadChecker(IPayloadChecker... checkers)
    {
        this.checkers = new ArrayList<>();
        this.checkers.addAll(Arrays.asList(checkers));
    }
    
    @Override
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
    {
        for(int i=0;i<checkers.size();i++)
        {
            checkers.get(i).checkNextPayload(subspan);
        }
        return true;
    }

    @Override
    public boolean check()
    {
        int count = 0;
        for(int i=0;i<checkers.size();i++)
        {
            if (checkers.get(i).check())
                count++;
        }
        return count==1;
    }
    
    @Override
    public String toString()
    {
        String res = "XOR[";
        for(int i=0;i<checkers.size();i++)
        {
            if (i>0) res += ",";
            res += checkers.get(i).toString();
        }
        res += "]";
        return res;
    }
    
    @Override
    public XorPayloadChecker clone()
    {
        IPayloadChecker[] clone = new IPayloadChecker[this.checkers.size()];
        for(int i=0;i<this.checkers.size();i++)
            clone[i] = this.checkers.get(i).clone();
        
        XorPayloadChecker checker = new XorPayloadChecker(clone);
        return checker;
    }
}