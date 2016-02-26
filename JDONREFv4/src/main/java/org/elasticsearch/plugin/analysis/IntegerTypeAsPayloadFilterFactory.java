package org.elasticsearch.plugin.analysis;

import org.apache.lucene.analysis.IntegerTypeAsPayloadFilter;
import org.apache.lucene.analysis.TokenCountPayloadsFilter;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * 
 * @author Julien
 */
public class IntegerTypeAsPayloadFilterFactory extends AbstractTokenFilterFactory
{
  protected int integerTypeAsPayloadFactor = IntegerTypeAsPayloadFilter.NOTINTEGERTYPEASPAYLOADFACTOR;
    
    protected int defaultIntegerType = IntegerTypeAsPayloadFilter.DEFAUTDEFAULTINTEGERTYPE;
    protected int integerType = IntegerTypeAsPayloadFilter.DEFAUTINTEGERTYPE;
    protected String tokentype = IntegerTypeAsPayloadFilter.DEFAUTTYPE;
    
  /** Creates a new JDONREFv3EdgeNGramWithPayloadsFilterFactory */
  @Inject
  public IntegerTypeAsPayloadFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
    super(index, indexSettings, name, settings);
    integerTypeAsPayloadFactor = settings.getAsInt("factor", IntegerTypeAsPayloadFilter.NOTINTEGERTYPEASPAYLOADFACTOR);
    defaultIntegerType = settings.getAsInt("defaultIntegerType", IntegerTypeAsPayloadFilter.DEFAUTDEFAULTINTEGERTYPE);
    integerType = settings.getAsInt("IntegerType", IntegerTypeAsPayloadFilter.DEFAUTINTEGERTYPE);
    tokentype = settings.get("tokentype",IntegerTypeAsPayloadFilter.DEFAUTTYPE);
  }

  @Override
  public TokenStream create(TokenStream tokenStream) {
      return new IntegerTypeAsPayloadFilter(tokenStream, tokentype, integerType, defaultIntegerType, integerTypeAsPayloadFactor, this.version());
  }
}
