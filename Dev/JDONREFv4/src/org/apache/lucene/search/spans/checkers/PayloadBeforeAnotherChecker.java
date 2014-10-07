package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Julien
 */
public class PayloadBeforeAnotherChecker extends AbstractPayloadChecker
{
    BytesRef payloadbefore;
    BytesRef another;

    public BytesRef getAnother() {
        return another;
    }

    public BytesRef getPayloadbefore() {
        return payloadbefore;
    }
    
    public PayloadBeforeAnotherChecker(BytesRef payloadbefore, BytesRef another)
    {
        this.payloadbefore = payloadbefore;
        this.another = another;
    }
    
    boolean maybegood = false;
    boolean isThereBeforePayload = false;
    boolean wasThereBeforePayload = false;
    MultiPayloadTermSpans lastSubSpan = null;
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (lastSubSpan!=subspan)
        {
            if (lastSubSpan!=null)
            {
                wasThereBeforePayload = isThereBeforePayload;
                isThereBeforePayload = false;
            }
            
            lastSubSpan = subspan;
        }
        
        if (Arrays.equals(subspan.getCurrentPayload(),payloadbefore.bytes))
        {
            isThereBeforePayload = true;
        }
        if (Arrays.equals(subspan.getCurrentPayload(), another.bytes))
        {
            if (wasThereBeforePayload)
            {
                maybegood = true;
            }
        }
        
        return true;
    }

    @Override
    public boolean check()
    {
        return maybegood;
    }

    @Override
    public void clear() {
        maybegood = false;
        isThereBeforePayload = false;
        wasThereBeforePayload = false;
        lastSubSpan = null;
    }
    
    public Object clone()
    {
        PayloadBeforeAnotherChecker checker = new PayloadBeforeAnotherChecker(payloadbefore,another);
        return checker;
    }
    
    public String toString()
    {
        String res = "";
        res += payloadbefore.toString();
        res += " JUST BEFORE ";
        res += another.toString();
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
    }
}
