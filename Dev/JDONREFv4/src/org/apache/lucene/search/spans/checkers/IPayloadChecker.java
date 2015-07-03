package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.IMultiPayload;

/**
 * Public interface to check document subspans for payload rules.
 * @author Julien
 */
public interface IPayloadChecker extends Cloneable
{
//    void setQuery(IPayloadCheckerSpanQuery query);
    
    /**
     * Check one of the payload from one of the spans.
     * Subspans might not be in order.
     * 
     * @return false means check() will return false. true means nothing.
     */
    boolean checkNextPayload(IMultiPayload subspan) throws IOException;
    
    /**
     * Return whether current document is checked or not
     * @return 
     */
    boolean check();
    
    /**
     * Prepare to start a new check
     */
    void clear();
    
    public IPayloadChecker clone();
}