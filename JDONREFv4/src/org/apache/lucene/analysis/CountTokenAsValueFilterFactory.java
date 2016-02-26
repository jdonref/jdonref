package org.apache.lucene.analysis;

import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 *
 * @author moquetju
 */
public class CountTokenAsValueFilterFactory extends TokenFilterFactory
{
    public CountTokenAsValueFilterFactory(Map<String, String> args) {
        super(args);
        
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream stream) {
        return new CountTokenAsValueFilter(stream,luceneMatchVersion);
    }
    
}
