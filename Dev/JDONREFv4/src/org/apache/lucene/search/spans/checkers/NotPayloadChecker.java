package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author Julien
 */
public class NotPayloadChecker extends PayloadChecker
{
    PayloadChecker checker;
    
    public PayloadChecker getChecker() {
        return checker;
    }
    
    public NotPayloadChecker(PayloadChecker checker)
    {
        this.checker = checker;
    }
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (checker.checkNextPayload(subspan))
            return false;
        return true;
    }

    @Override
    public boolean check()
    {
        return !checker.check();
    }

    @Override
    public void clear() {
        checker.clear();
    }
    
    public String toString()
    {
        String res = "NOT(";
        res += checker.toString();
        res += ")";
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        checker.setQuery(query);
    }
}
