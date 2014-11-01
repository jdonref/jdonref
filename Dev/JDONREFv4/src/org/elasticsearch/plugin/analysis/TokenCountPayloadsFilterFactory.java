package org.elasticsearch.plugin.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenCountPayloadsFilter;
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
public class TokenCountPayloadsFilterFactory extends AbstractTokenFilterFactory
{
  public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    
  protected int termCountPayloadFactor = NOTERMCOUNTPAYLOADFACTOR;
    
  /** Creates a new JDONREFv3EdgeNGramWithPayloadsFilterFactory */
  @Inject
  public TokenCountPayloadsFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
    super(index, indexSettings, name, settings);
    termCountPayloadFactor = settings.getAsInt("factor", TokenCountPayloadsFilterFactory.NOTERMCOUNTPAYLOADFACTOR);
  }

  @Override
  public TokenStream create(TokenStream tokenStream) {
      
      return new TokenCountPayloadsFilter(tokenStream, termCountPayloadFactor, this.version);
  }
}
