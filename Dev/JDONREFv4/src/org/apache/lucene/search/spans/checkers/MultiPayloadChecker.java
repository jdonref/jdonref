package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.search.spans.IMultiPayload;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.IPayloadCheckerSpanQuery;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Julien
 */
public abstract class MultiPayloadChecker extends AbstractPayloadChecker
{
    protected IPayloadCheckerSpanQuery query = null;
    
    public MultiPayloadChecker()
    {
    }
    
    @Override
    public void setQuery(IPayloadCheckerSpanQuery query) {
        this.query = query;
    }
    
    ArrayList<BytesRef[]> lastpayloads = new ArrayList<>();
    
    protected abstract boolean checkPayloads(int index);
  
    protected void addPayload(byte[] payload,int index,int count,int order)
    {
      if (index==0)
      {
          BytesRef[] payloads = new BytesRef[query.getClausesCount()];
          payloads[order] = new BytesRef(payload);
          lastpayloads.add(payloads);
      }
      else
      if (count==0)
      {
          for(int i=0;i<lastpayloads.size();i++)
          {
              BytesRef[] payloads = lastpayloads.get(i);
              payloads[order] = new BytesRef(payload);
          }
      }
      else
      {
        int size = lastpayloads.size()/(count);
        for(int i=0;i<size;i++)
        {
            BytesRef[] payloads = lastpayloads.get(i).clone();
            payloads[order] = new BytesRef(payload);
            lastpayloads.add(payloads);
        }
      }
    }
    
    protected int count;
    protected int index;
    protected boolean check;
    
    protected IMultiPayload currentSubSpan;
    
    @Override
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
    {
        byte[] currentPayload = subspan.getCurrentPayload();
        
        if (currentPayload!=null)
        {
            if (currentSubSpan!=subspan)
            {
                if (currentSubSpan!=null)
                {
                    count=0;
                    //check = checkPayloads(index);
                    index++;
                    //if (!check) return false;
                }

                currentSubSpan = subspan;
            }
            else
            {
                count++;
            }

            addPayload(currentPayload, index, count, subspan.getOrder());
        }
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
}