package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;

/**
 *
 * @author Julien
 */
public class OrPayloadChecker extends AbstractIPayloadCheckerCollectionChecker
{
    public OrPayloadChecker(IPayloadChecker... checkers)
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
        if (checkers.isEmpty()) return true;
        for(int i=0;i<checkers.size();i++)
            if (checkers.get(i).check())
                return true;
        return false;
    }
    
    @Override
    public String toString()
    {
        String res = "OR[";
        for(int i=0;i<checkers.size();i++)
        {
            if (i>0) res += ",";
            res += checkers.get(i).toString();
        }
        res += "]";
        return res;
    }
    
    @Override
    public OrPayloadChecker clone()
    {
        IPayloadChecker[] clone = new IPayloadChecker[this.checkers.size()];
        for(int i=0;i<this.checkers.size();i++)
            clone[i] = this.checkers.get(i).clone();
        
        OrPayloadChecker checker = new OrPayloadChecker(clone);
        return checker;
    }
}