package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.IMultiPayload;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Julien
 */
public abstract class MultiPayloadChecker extends AbstractPayloadChecker
{
    protected static final int MAXPAYLOADS_INROW = 20;
    protected static final int MAXPAYLOADS_INCOLUMN = 20;
        
    public MultiPayloadChecker()
    {
        lastpayloads = new BytesRef[MAXPAYLOADS_INCOLUMN][MAXPAYLOADS_INROW];
    }
    
//    @Override
//    public void setQuery(IPayloadCheckerSpanQuery query) {
//        this.query = query;
//    }
    
    protected BytesRef[][] lastpayloads;
    protected int numcolumns;
    protected int numrows;
    //ArrayList<BytesRef[]> lastpayloads = new ArrayList<>();
    
    protected abstract boolean checkPayloads(int index);
  
    /**
     * 
     * @param payload payload du terme en cours
     * @param index le numéro du terme en cours (row)
     * @param count le numéro du payload du terme en cours (column)
     * @param order l'ordre du terme en cours
     */
    protected void addPayload(byte[] payload,int index,int count,int order)
    {
      if (index==0)
      {
          numrows = Math.max(numrows, order+1);
          lastpayloads[numcolumns++][order] = new BytesRef(payload);
      }
      else
      if (count==0)
      {
          numrows = Math.max(numrows, order+1);
          for(int i=0;i<numcolumns;i++)
          {
              lastpayloads[i][order] = new BytesRef(payload);
          }
      }
      else
      {
        //int size = lastpayloads.size()/(count);
        numrows = Math.max(numrows, order+1);
        int size = numcolumns/count;
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<numrows;j++)
            {
                lastpayloads[numcolumns][j] = lastpayloads[i][j];
            }
            lastpayloads[numcolumns++][order] = new BytesRef(payload);
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
        numcolumns = 0;
        numrows = 0;
        //lastpayloads.clear();
    }
}