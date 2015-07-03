package org.apache.lucene.search.spans.checkers;

import java.util.Arrays;
import java.util.HashSet;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Julien
 */
public class GroupedPayloadChecker extends MultiPayloadChecker
{
    HashSet<BytesRef> set = new HashSet<>();
    
    public GroupedPayloadChecker()
    {
    }
    
    @Override
    protected boolean checkPayloads(int index)
    {
      if (index<=1) return true;
      
      for(int i=0;i<numcolumns;i++)
      {
          set.clear();
          BytesRef[] payloads = lastpayloads[i];
          int j = index;
          
          do
          {
              while(j>=0 && payloads[j]==null)
              {
                  j--;
              }
              if (j==-1) return true;
              
              int savedj = j;
              byte[] payload = payloads[j].bytes;
              
              if (!set.contains(payloads[j])) // Array do not override equals method, BytesRef do
              {
                  do
                  {
                      j--;
                  } while (j >= 0 && (payloads[j]==null || Arrays.equals(payloads[j].bytes, payload)));
                  if (j == -1) return true;
                  else set.add(payloads[savedj]);
              }
              else
                  break;
          } while(j>=0);
      }
      
      return false;
    }
    
    @Override
    public String toString()
    {
        return "GROUPED";
    }

    @Override
    public GroupedPayloadChecker clone()
    {
        return new GroupedPayloadChecker();
    }
}