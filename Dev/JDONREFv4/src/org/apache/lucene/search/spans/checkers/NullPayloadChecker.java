package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.IPayloadCheckerSpanQuery;

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
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
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
    
    public String toString()
    {
        String res = "TRUE";
        return res;
    }

    @Override
    public void setQuery(IPayloadCheckerSpanQuery query) {
    }

    @Override
    public NullPayloadChecker clone() {
        return this;
    }
}
