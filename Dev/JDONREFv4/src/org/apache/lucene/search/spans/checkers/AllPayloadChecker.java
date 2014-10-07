package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Check every token with given payload in a document are present in the query.
 * @author Julien
 */
public class AllPayloadChecker extends AbstractPayloadChecker
{
    protected byte[] payload;

    public byte[] getPayload() {
        return payload;
    }
    
    public AllPayloadChecker(byte[] payload)
    {
        this.payload = payload;
    }
    
    protected int count;
    protected int total;
    protected boolean check;
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (Arrays.equals(this.payload, subspan.getCurrentPayload()))
        {
             count++;
             total = subspan.getCurrentCountByPayload();
        }
        return true;
    }

    @Override
    public boolean check() {
        return count>0 && total <= count; // oh ... yeah. there may be multipayload by token
    }

    @Override
    public void clear() {
        count = 0;
        check = false;
    }
    
    public String toString()
    {
        String res = "ALL VALUES FOR PAYLOAD ";
        res += payload.toString();
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
    }
}