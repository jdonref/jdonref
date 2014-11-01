package org.apache.lucene.search.spans.checkers;

import java.util.Arrays;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Julien
 */
public class PayloadBeforeAnotherChecker extends MultiPayloadChecker
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
    public PayloadBeforeAnotherChecker clone()
    {
        return new PayloadBeforeAnotherChecker(payloadbefore, another);
    }

    @Override
    protected boolean checkPayloads(int index)
    {
      if (index<=1) return true;
      
      boolean check = false;
      int size = lastpayloads.size();
      
      for(int i=0;i<size;i++)
      {
          BytesRef[] payloads = lastpayloads.get(i);
          byte[] payload;
          int state = 0;
          boolean subcheck = true;

          for(int j=0;subcheck && j<=index;j++)
          {
              payload = payloads[j].bytes;
              int substate = 0;
              
              if (Arrays.equals(payload, payloadbefore))
              {
                  substate = 1;
              }
              if (Arrays.equals(payload, another))
              {
                  substate = 2;
              }
              
              switch(state)
              {
                  case 0:
                      switch(substate)
                      {
                          default:
                          case 0: break;
                          case 1: state = 1; break;
                          case 2: state = 2; break;
                      }
                      break;
                  case 1:
                      switch(substate)
                      {
                          default:
                          case 0: subcheck = false; break;
                          case 1: break;
                          case 2: state = 2; break;
                      }
                      break;
                  case 2:
                      switch(substate)
                      {
                          default:
                          case 0:
                          case 2: break;
                          case 1: subcheck = false; break;
                      }
                      break;
              }
          }
          
          check |= subcheck;
      }
      
      return check;
    }
}