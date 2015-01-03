package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.IPayloadCheckerSpanQuery;

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
    
    @Override
    public boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException
    {
        boolean check = true;
        if (check==false) return false;
        if (keepTrying && condition.checkNextPayload(subspan))
        {
            check  = then.checkNextPayload(subspan);
            check &= elseChecker.checkNextPayload(subspan); // they need to advance simultanneously
            return check;
        }
        else
        {
            keepTrying = false;
            check = elseChecker.checkNextPayload(subspan);
            return check;
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
    
    @Override
    public IfPayloadElseChecker clone()
    {
        IfPayloadElseChecker checker = new IfPayloadElseChecker(condition,then,elseChecker);
        return checker;
    }
    
    @Override
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
    public void setQuery(IPayloadCheckerSpanQuery query) {
        condition.setQuery(query);
        then.setQuery(query);
        elseChecker.setQuery(query);
    }
}
