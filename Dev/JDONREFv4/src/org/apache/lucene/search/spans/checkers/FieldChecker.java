package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.document.Document;

/**
 * 
 * @author Julien
 */
public class FieldChecker extends AbstractPayloadChecker
{
    protected String field;
    protected String value;

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
    
    public FieldChecker(String value)
    {
        this.field = "_type";
        this.value = value;
    }
    
    public FieldChecker(String field, String value)
    {
        this.field = field;
        this.value = value;
    }
    
    protected boolean check;
    protected boolean checked;

    /**
     * Expert : variable check must be initialized on first call to checkNextPayload
     * 
     * @param subspan
     * @return
     * @throws IOException 
     */
    @Override
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
    {
        if (!checked)
        {
            Document d = subspan.document();
            String[] types = d.getValues(field);
            for(int i=0;i<types.length;i++)
            {
                if (types[i].equals(value))
                {
                    check = true;
                }
            }
            checked = true;
        }
        return check;
    }

    @Override
    public boolean check() {
        return check;
    }

    @Override
    public void clear() {
        check = false;
        checked = false;
    }
    
    public String toString()
    {
        String res = "ONE VALUE FROM(";
        res += field;
        res += "=";
        res += value;
        res += ")";
        return res;
    }

//    @Override
//    public void setQuery(IPayloadCheckerSpanQuery query) {
//    }

    @Override
    public FieldChecker clone()
    {
        return new FieldChecker(field,value);
    }
}