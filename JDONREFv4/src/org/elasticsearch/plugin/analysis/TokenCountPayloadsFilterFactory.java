package org.elasticsearch.plugin.analysis;

import java.util.HashSet;
import java.util.Set;
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
public class TokenCountPayloadsFilterFactory extends AbstractTokenFilterFactory
{
  protected int termCountPayloadFactor = TokenCountPayloadsFilter.NOTERMCOUNTPAYLOADFACTOR;
  
  Set<String> ignoredTypes;
  
  protected String[] convert(Set<Integer> tab)
  {
      if (tab==null || tab.size()==0) return new String[]{};
      String[] res = new String[tab.size()];
      int index=0;
      for(int i : tab)
      {
          res[index++] = Integer.toString(i);
      }
      return res;
  }
  
  protected Set<String> convert(String[] tab)
  {
      Set<String> requiredPayloads = new HashSet<>();
      for (String payload : tab)
      {
          requiredPayloads.add(payload); // NumberFormatException
      }
      return requiredPayloads;
  }
  
  protected Set<String> getAsStringArray(Settings indexSettings,String field,Set<String> default_value)
  {
      String[] tmp = indexSettings.getAsArray(field,default_value.toArray(new String[]{}));
      return convert(tmp);
  }
  
  /** Creates a new JDONREFv3EdgeNGramWithPayloadsFilterFactory */
  @Inject
  public TokenCountPayloadsFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
    super(index, indexSettings, name, settings);
    termCountPayloadFactor = settings.getAsInt("factor", TokenCountPayloadsFilter.NOTERMCOUNTPAYLOADFACTOR);
            
    ignoredTypes = getAsStringArray(settings,"ignored_types",TokenCountPayloadsFilter.DEFAULT_IGNOREDTYPES);
  }
  
  @Override
  public TokenStream create(TokenStream tokenStream) {
      
      return new TokenCountPayloadsFilter(tokenStream, ignoredTypes,termCountPayloadFactor, this.version);
  }
}