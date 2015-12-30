package org.apache.lucene.search.spans;

import java.io.IOException;
import org.apache.lucene.document.Document;

/**
 *
 * @author moquetju
 */


public interface IMultiPayload {

    int getCurrentCountByPayload();

    byte[] getCurrentPayload();
    
    Document document() throws IOException;
    
    int getOrder();
}
