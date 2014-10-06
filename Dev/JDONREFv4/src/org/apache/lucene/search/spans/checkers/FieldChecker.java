package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author Julien
 */
public class FieldChecker extends PayloadChecker
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
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (!check)
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
        String res = "ONE VALUE FROM(";
        res += field;
        res += "=";
        res += value;
        res += ")";
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
    }
}