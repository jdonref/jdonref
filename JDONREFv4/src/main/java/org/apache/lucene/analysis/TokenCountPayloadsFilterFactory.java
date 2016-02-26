package org.apache.lucene.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * 
 * @author Julien
 */
public class TokenCountPayloadsFilterFactory extends TokenFilterFactory
{
  public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    
  protected int termCountPayloadFactor = TokenCountPayloadsFilterFactory.NOTERMCOUNTPAYLOADFACTOR;
    
  Set<String> ignoredTypes;
    
  protected Set<Integer> convert(Set<String> tab)
  {
    Set<Integer> res = new HashSet<>();
    for(String payload : tab)
    {
        try
        {
            res.add(Integer.parseInt(payload));
        }
        catch(NumberFormatException nfe)
        {
            IllegalArgumentException e = new IllegalArgumentException("Parameter "+ payload+" must be an integer");
            e.initCause(nfe);
            throw e;
        }
    }
    return res;
  }
  
  protected Set<String> getStringSet(Map<String,String> args, String field, Set<String> default_value)
  {
    Set<String> tmpRes = getSet(args, field);
    Set<String> res;
    if (tmpRes==null || tmpRes.isEmpty())
        res = default_value;
    else
    {
        res = tmpRes;
    }
    return res;
  }
    
  /** Creates a new EdgeNGramFilterFactory */
  public TokenCountPayloadsFilterFactory(Map<String, String> args) {
    super(args);
    termCountPayloadFactor = getInt(args, "factor", TokenCountPayloadsFilterFactory.NOTERMCOUNTPAYLOADFACTOR);
    
    ignoredTypes = getStringSet(args, "ignored_types", TokenCountPayloadsFilter.DEFAULT_IGNOREDTYPES);
    
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public TokenCountPayloadsFilter create(TokenStream input) {
      return new TokenCountPayloadsFilter(input, ignoredTypes, termCountPayloadFactor, luceneMatchVersion);
  }
}