package org.elasticsearch.plugin.analysis;

import org.apache.lucene.analysis.ngram.EdgeNGramWithPayloadsFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.Lucene43EdgeNGramTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.reverse.ReverseStringFilter;
import org.apache.lucene.util.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.EdgeNGramTokenFilterFactory;
import org.elasticsearch.index.analysis.EdgeNGramTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * 
 * @author Julien
 */
public class EdgeNGramWithPayloadsFilterFactory extends EdgeNGramTokenFilterFactory
{
  protected boolean withPayloads = false;
  protected int nonPrivateMinGramSize;
  protected int nonPrivateMaxGramSize;
  protected EdgeNGramWithPayloadsFilter.Side nonPrivateSide;
  protected org.elasticsearch.Version nonPrivateEsVersion;
  
  /** Creates a new JDONREFv3EdgeNGramWithPayloadsFilterFactory */
  @Inject
  public EdgeNGramWithPayloadsFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
    super(index, indexSettings, name, settings);
    nonPrivateMinGramSize = settings.getAsInt("min_gram", NGramTokenFilter.DEFAULT_MIN_NGRAM_SIZE);
    nonPrivateMaxGramSize = settings.getAsInt("max_gram", NGramTokenFilter.DEFAULT_MAX_NGRAM_SIZE);
    nonPrivateSide = EdgeNGramWithPayloadsFilter.Side.getSide(settings.get("side", Lucene43EdgeNGramTokenizer.DEFAULT_SIDE.getLabel()));
    withPayloads = settings.getAsBoolean("withPayloads", false);
    nonPrivateEsVersion = indexSettings.getAsVersion(IndexMetaData.SETTING_VERSION_CREATED, org.elasticsearch.Version.CURRENT);
  }

  @Override
  public TokenStream create(TokenStream tokenStream) {
      if (!withPayloads) return super.create(tokenStream);
      
      if (version.onOrAfter(Version.LUCENE_43) && nonPrivateEsVersion.onOrAfter(org.elasticsearch.Version.V_0_90_2))
      {
            /*
             * We added this in 0.90.2 but 0.90.1 used LUCENE_43 already so we can not rely on the lucene version.
             * Yet if somebody uses 0.90.2 or higher with a prev. lucene version we should also use the deprecated version.
             */
            final Version version = this.version == Version.LUCENE_43 ? Version.LUCENE_44 : this.version; // always use 4.4 or higher
            TokenStream result = tokenStream;
            // side=BACK is not supported anymore but applying ReverseStringFilter up-front and after the token filter has the same effect
            if (nonPrivateSide == EdgeNGramWithPayloadsFilter.Side.BACK) {
                result = new ReverseStringFilter(version, result);
            }
            result = new EdgeNGramWithPayloadsFilter(version, result, nonPrivateMinGramSize, nonPrivateMaxGramSize, withPayloads);
            if (nonPrivateSide == EdgeNGramWithPayloadsFilter.Side.BACK) {
                result = new ReverseStringFilter(version, result);
            }
            return result;
      }
      return new EdgeNGramWithPayloadsFilter(version, tokenStream, nonPrivateSide, nonPrivateMinGramSize, nonPrivateMaxGramSize, withPayloads);
  }
}
