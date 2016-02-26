package org.apache.lucene.search.spans.checkers;

import java.io.IOException;

/**
 * Abstract class to check document subspans for payload rules.
 * @author Julien
 */
public abstract class AbstractPayloadChecker implements IPayloadChecker
{
//    @Override
//    public abstract void setQuery(IPayloadCheckerSpanQuery query);
    
    /**
     * Check one of the payload from one of the spans.
     * Subspans might not be in order.
     * 
     * @return false means check() will return false. true means nothing.
     */
    @Override
    public abstract boolean checkNextPayload(org.apache.lucene.search.spans.IMultiPayload subspan) throws IOException;
    
    /**
     * Return whether current document is checked or not
     * @return 
     */
    @Override
    public abstract boolean check();
    
    /**
     * Prepare to start a new check
     */
    @Override
    public abstract void clear();
    
    @Override
    public abstract AbstractPayloadChecker clone();
}