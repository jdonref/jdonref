package org.apache.lucene.analysis;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * 
 * @author Julien
 */
public class TokenCountPayloadsFilterFactory extends TokenFilterFactory
{
    public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    
    protected int termCountPayloadFactor = TokenCountPayloadsFilterFactory.NOTERMCOUNTPAYLOADFACTOR;
    
  /** Creates a new EdgeNGramFilterFactory */
  public TokenCountPayloadsFilterFactory(Map<String, String> args) {
    super(args);
    termCountPayloadFactor = getInt(args, "factor", TokenCountPayloadsFilterFactory.NOTERMCOUNTPAYLOADFACTOR);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public TokenCountPayloadsFilter create(TokenStream input) {
      return new TokenCountPayloadsFilter(input, termCountPayloadFactor,luceneMatchVersion);
  }
}