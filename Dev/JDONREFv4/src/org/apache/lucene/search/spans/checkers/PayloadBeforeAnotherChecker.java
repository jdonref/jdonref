package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author Julien
 */
public class PayloadBeforeAnotherChecker extends AbstractPayloadChecker
{
    byte[] payloadbefore;
    byte[] another;

    public byte[] getAnother() {
        return another;
    }

    public byte[] getPayloadbefore() {
        return payloadbefore;
    }
    
    public PayloadBeforeAnotherChecker(byte[] payloadbefore, byte[] another)
    {
        this.payloadbefore = payloadbefore;
        this.another = another;
    }
    
    MultiPayloadTermSpans lastSubSpan = null;
    
    // 0: initial state
    // 1: before
    // 2: another after before
    // 3: fail
    // 4: good . but 2 is suffisant
    // 5: just another. 2 is suffisant
    // 6: both
    // 7: both after before. 6 is suffisant
    int state = 0;
    
    // 0 : no before nor another
    // 1 : before
    // 2 : another
    // 3 : both
    int substate = 0;
    
    public boolean checkState()
    {
        switch(state)
                {
                    default:
                        break;
                    case 0:
                        // substate==0 => state = 0;
                        if (substate==1) state = 1;
                        else if (substate==2) state = 2;
                        else if (substate==3) state = 6;
                        break;
                    case 1:
                        if (substate==0) { state = 3; return false; }
                        //else if (substate==1) state = 1;
                        else if (substate==2) state = 2;
                        else if (substate==3) state = 6;
                        break;
                    case 2:
                        // if (substate==0) state=2;
                        // if (substate==2 or substate==3) state=2;
                        if (substate==1) { state=3; return false;}
                        break;
                    case 3:
                        return false;
                    case 4: 
                        assert(false); // NB: not possible state
                        break;
                    case 5:
                        assert(false); // NB: not possible state
                        break;
                    case 6:
                        if (substate==0) state = 2;
                        else if (substate==1) state = 1;
                        else if (substate==2) state = 2;
                        //else if (substate==3) state = 6;
                        break;
                    case 7:
                        assert(false); // NB: not possible state
                        break;
                }
        return true;
    }
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (lastSubSpan!=subspan)
        {
            if (lastSubSpan!=null)
            {
                if (!checkState())
                    return false;
                
                substate = 0;
            }
            
            lastSubSpan = subspan;
        }
        
        if (Arrays.equals(subspan.getCurrentPayload(),payloadbefore))
        {
            if (substate!=1 && substate !=3)
                substate += 1;
        }
        if (Arrays.equals(subspan.getCurrentPayload(), another))
        {
            if (substate!=2 && substate !=3)
                substate += 2;
        }
        
        return true;
    }

    @Override
    public boolean check()
    {
        if (!checkState()) return false;
        return state == 2 || state == 0;
    }

    @Override
    public void clear() {
        state = 0;
        substate = 0;
        lastSubSpan = null;
    }
    
    @Override
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

    @Override
    public PayloadBeforeAnotherChecker clone()
    {
        return new PayloadBeforeAnotherChecker(payloadbefore, another);
    }
}
