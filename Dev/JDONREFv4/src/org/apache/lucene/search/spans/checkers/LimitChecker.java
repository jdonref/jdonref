package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * 
 * @author Julien
 */
public class LimitChecker extends AbstractPayloadChecker
{
    protected int limit = Integer.MAX_VALUE;

    public int getLimit() {
        return limit;
    }
    
    public LimitChecker()
    {
    }
    
    public LimitChecker(int limit)
    {
        this.limit = limit;
    }
    
    int count = 0;
    
    /**
     * @param subspan
     * @return
     * @throws IOException 
     */
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        return count<=limit;
    }

    @Override
    public boolean check() {
        return count<=limit;
    }

    @Override
    public void clear() {
        count++;
    }
    
    public String toString()
    {
        String res = "LIMIT TO ";
        res += limit;
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
    }

    @Override
    public LimitChecker clone()
    {
        return new LimitChecker(limit);
    }
}