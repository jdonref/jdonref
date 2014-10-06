package org.elasticsearch.plugin.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.UnsplitFilter;
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
public class UnsplitFilterFactory extends AbstractTokenFilterFactory
{
  @Inject
  public UnsplitFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
      super(index, indexSettings, name, settings);
  }
  
  @Override
  public TokenStream create(TokenStream tokenStream) {
      return new UnsplitFilter(tokenStream);
  }
}