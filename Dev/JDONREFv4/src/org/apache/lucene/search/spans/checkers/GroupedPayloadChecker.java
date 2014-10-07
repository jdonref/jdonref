package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author Julien
 */
public class GroupedPayloadChecker extends AbstractPayloadChecker
{
    protected PayloadCheckerSpanQuery query = null;
    
    
    
    public GroupedPayloadChecker()
    {
    }
    
    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        this.query = query;
    }
    
    ArrayList<byte[][]> lastpayloads = new ArrayList<>();
    
    protected boolean checkPayloads(int index)
    {
      if (index<=1) return true;
      
      boolean check = false;
      int size = lastpayloads.size();
      
      for(int i=0;i<size;i++)
      {
          byte[][] payloads = lastpayloads.get(i);
          if (Arrays.equals(payloads[index-1], payloads[index]))
          {
              check |= true;
          }
          else
          {
              boolean subcheck = true;
              for (int j = 0; subcheck && (j < index - 1); j++)
              {
                  if (Arrays.equals(payloads[j], payloads[index]))
                  {
                      lastpayloads.remove(i); // it may improve performances
                      i--;
                      size--;
                      subcheck = false;
                  }
              }
              check |= subcheck;
          }
      }
      
      return check;
    }
  
    protected void addPayload(byte[] payload,int index,int count)
    {
      if (index==0)
      {
          byte[][] payloads = new byte[query.getClauses().length][];
          payloads[0] = payload;
          lastpayloads.add(payloads);
      }
      else
      if (count==0)
      {
          for(int i=0;i<lastpayloads.size();i++)
          {
              byte[][] payloads = lastpayloads.get(i);
              payloads[index] = payload;
          }
      }
      else
      {
        int size = lastpayloads.size()/(count);
        for(int i=0;i<size;i++)
        {
            byte[][] payloads = lastpayloads.get(i).clone();
            payloads[index] = payload;
              lastpayloads.add(payloads);
        }
      }
    }
    
    protected int count;
    protected int index;
    protected boolean check;
    
    protected MultiPayloadTermSpans currentSubSpan;
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (currentSubSpan!=subspan)
        {
            if (currentSubSpan!=null)
            {
                count=0;
                check = checkPayloads(index);
                index++;
                if (!check) return false;
            }
            
            currentSubSpan = subspan;
        }
        else
        {
            count++;
        }
        
        byte[] currentPayload = subspan.getCurrentPayload();
        addPayload(currentPayload, index, count);
        
        return true;
    }

    @Override
    public boolean check()
    {
        if (currentSubSpan!=null)
            check = checkPayloads(index);
        return check;
    }

    @Override
    public void clear() {
        count = 0;
        index = 0;
        check = false;
        currentSubSpan = null;
        lastpayloads.clear();
    }
    
    public String toString()
    {
        return "GROUPED";
    }
}