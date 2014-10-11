package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Does nothing ...
 * @author Julien
 */
public class NullPayloadChecker extends AbstractPayloadChecker
{
    public NullPayloadChecker()
    {
    }
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        return true;
    }

    @Override
    public boolean check()
    {
            return true;
    }

    @Override
    public void clear() {
    }
    
    public Object clone()
    {
        return this;
    }
    
    public String toString()
    {
        String res = "TRUE";
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
    }
}
