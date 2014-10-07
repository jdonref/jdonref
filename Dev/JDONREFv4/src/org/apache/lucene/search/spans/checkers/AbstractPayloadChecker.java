package org.apache.lucene.search.spans.checkers;

import java.io.IOException;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;

/**
 * Abstract class to check document subspans for payload rules.
 * @author Julien
 */
public abstract class AbstractPayloadChecker implements IPayloadChecker
{
    public abstract void setQuery(PayloadCheckerSpanQuery query);
    
    /**
     * Check one of the payload from one of the spans.
     * Subspans might not be in order.
     * 
     * @return false means check() will return false. true means nothing.
     */
    public abstract boolean checkNextPayload(MultiPayloadTermSpans subspan) throws IOException;
    
    /**
     * Return whether current document is checked or not
     * @return 
     */
    public abstract boolean check();
    
    /**
     * Prepare to start a new check
     */
    public abstract void clear();
}