package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Be aware of performances
 * @author Julien
 */
public class IfPayloadElseChecker extends IfPayloadChecker
{
    IPayloadChecker elseChecker;

    public IPayloadChecker getElse() {
        return elseChecker;
    }
    
    public IfPayloadElseChecker(IPayloadChecker condition, IPayloadChecker then, IPayloadChecker elseChecker)
    {
        super(condition,then);
        this.elseChecker = elseChecker;
    }
    
    boolean keepTrying = true;
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        boolean res = true;
        if (res==false) return false;
        if (keepTrying && condition.checkNextPayload(subspan))
        {
            res  = then.checkNextPayload(subspan);
            res &= elseChecker.checkNextPayload(subspan); // they need to advance simultanneously
            return res;
        }
        else
        {
            keepTrying = false;
            res = elseChecker.checkNextPayload(subspan);
            return res;
        }
    }

    @Override
    public boolean check()
    {
        if (condition.check())
            return then.check();
        else
            return elseChecker.check();
    }

    @Override
    public void clear() {
        condition.clear();
        then.clear();
        keepTrying = true;
        res = true;
    }
    
    public Object clone()
    {
        IfPayloadElseChecker checker = new IfPayloadElseChecker(condition,then,elseChecker);
        return checker;
    }
    
    public String toString()
    {
        String res = "IF (";
        res += condition.toString();
        res += ") then (";
        res += then.toString();
        res += ") else (";
        res += elseChecker.toString();
        res += ")";
        return res;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        condition.setQuery(query);
        then.setQuery(query);
        elseChecker.setQuery(query);
    }
}
