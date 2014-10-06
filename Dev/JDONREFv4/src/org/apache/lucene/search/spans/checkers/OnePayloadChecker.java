package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Check a token with given payload in a document is present in the query.
 * @author Julien
 */
public class OnePayloadChecker extends PayloadChecker
{
    protected byte[] payload;
    
    public byte[] getPayload() {
        return payload;
    }
    
    public OnePayloadChecker(byte[] payload)
    {
        this.payload = payload;
    }
    
    protected boolean check;
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (!check)
        {
            if (Arrays.equals(this.payload, subspan.getCurrentPayload()))
            {
                check = true;
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean check() {
        return check;
    }

    @Override
    public void clear() {
        check = false;
    }
    
    public String toString()
    {
        String res = "ONE VALUE FOR PAYLOAD ";
        res += payload.toString();
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
    }
}