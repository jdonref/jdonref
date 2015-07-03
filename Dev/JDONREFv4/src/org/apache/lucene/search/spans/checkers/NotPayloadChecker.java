package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;

/**
 *
 * @author Julien
 */
public class NotPayloadChecker extends AbstractPayloadChecker
{
    IPayloadChecker checker;
    
    public IPayloadChecker getChecker() {
        return checker;
    }
    
    public NotPayloadChecker(IPayloadChecker checker)
    {
        this.checker = checker;
    }
    
    @Override
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
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

//    @Override
//    public void setQuery(IPayloadCheckerSpanQuery query) {
//        checker.setQuery(query);
//    }

    @Override
    public NotPayloadChecker clone() {
        return new NotPayloadChecker(this.checker.clone());
    }
}
