package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Be aware of performances
 * @author Julien
 */
public class IfPayloadChecker extends AbstractPayloadChecker
{
    IPayloadChecker condition;
    IPayloadChecker then;

    public IPayloadChecker getCondition() {
        return condition;
    }

    public IPayloadChecker getThen() {
        return then;
    }
    
    public IfPayloadChecker(IPayloadChecker condition, IPayloadChecker then)
    {
        this.condition = condition;
        this.then = then;
    }
    
    boolean keepTrying = true;
    boolean res = true;
    
    @Override
    public boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException
    {
        if (res==false) return false;
        if (keepTrying && condition.checkNextPayload(subspan))
        {
            res = then.checkNextPayload(subspan);
            return res;
        }
        else
        {
            keepTrying = false;
            return true;
        }
    }

    @Override
    public boolean check()
    {
        if (condition.check())
            return then.check();
        else
            return true;
    }

    @Override
    public void clear() {
        condition.clear();
        then.clear();
        keepTrying = true;
        res = true;
    }
    
    @Override
    public IfPayloadChecker clone()
    {
        IfPayloadChecker checker = new IfPayloadChecker(condition,then);
        return checker;
    }
    
    @Override
    public String toString()
    {
        String string = "IF (";
        string += condition.toString();
        string += ") then (";
        string += then.toString();
        string += ")";
        return string;
    }

    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        condition.setQuery(query);
        then.setQuery(query);
    }
}