package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.util.Bits;

/**
 *
 * @author moquetju
 */


public interface IGetSpans {

    MultiPayloadTermSpans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException;
    
}
