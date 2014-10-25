package org.apache.lucene.search.spans.checkers;

import java.util.List;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 *
 * @author moquetju
 */
public abstract class AbstractIPayloadCheckerCollectionChecker extends AbstractPayloadChecker
{
    List<IPayloadChecker> checkers;

    public List<IPayloadChecker> getCheckers() {
        return checkers;
    }
    
    public void setCheckers(List<IPayloadChecker> checkers)
    {
        this.checkers = checkers;
    }
    
    @Override
    public void setQuery(PayloadCheckerSpanQuery query) {
        for(int i=0;i<checkers.size();i++)
        {
            checkers.get(i).setQuery(query);
        }
    }
    
    @Override
    public void clear() {
        for(int i=0;i<checkers.size();i++)
            checkers.get(i).clear();
    }
}
