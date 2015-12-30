package org.apache.lucene.analysis;

import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * 
 * @author Julien
 */
public class IntegerTypeAsPayloadFilterFactory extends TokenFilterFactory
{
    protected int integerTypeAsPayloadFactor = IntegerTypeAsPayloadFilter.NOTINTEGERTYPEASPAYLOADFACTOR;
    
    protected int defaultIntegerType = IntegerTypeAsPayloadFilter.DEFAUTDEFAULTINTEGERTYPE;
    protected int integerType = IntegerTypeAsPayloadFilter.DEFAUTINTEGERTYPE;
    protected String tokentype = IntegerTypeAsPayloadFilter.DEFAUTTYPE;
    
  /** Creates a new EdgeNGramFilterFactory */
  public IntegerTypeAsPayloadFilterFactory(Map<String, String> args) {
    super(args);
    integerTypeAsPayloadFactor = getInt(args, "factor", IntegerTypeAsPayloadFilter.NOTINTEGERTYPEASPAYLOADFACTOR);
    defaultIntegerType = getInt(args,"defautIntegerType", IntegerTypeAsPayloadFilter.DEFAUTDEFAULTINTEGERTYPE);
    integerType = getInt(args,"integerType", IntegerTypeAsPayloadFilter.DEFAUTINTEGERTYPE);
    tokentype = get(args,"tokentype",IntegerTypeAsPayloadFilter.DEFAUTTYPE);
    
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public IntegerTypeAsPayloadFilter create(TokenStream input)
  {
      return new IntegerTypeAsPayloadFilter(input, tokentype, integerType, defaultIntegerType, integerTypeAsPayloadFactor, luceneMatchVersion);
  }
}