package org.elasticsearch.plugin.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.UnsplitFilter;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.util.BytesRef;
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
  protected String path;
  protected boolean keepOriginals;
  protected boolean frequentTermsOnly;
  protected int minWordsUnsplitted;
  protected int maxSubwords;
  protected char delimiter;
  protected BytesRef spanPayload;
  protected Set<String> subwordTypes;
  
  public static final String DEFAULT_DELIMITER = "";
  
  protected IntegerEncoder intEncoder = new IntegerEncoder();
    
  @Inject
  public UnsplitFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
      super(index, indexSettings, name, settings);
      path = settings.get("path",UnsplitFilter.DEFAULT_PATH);
      keepOriginals = settings.getAsBoolean("keep_originals", UnsplitFilter.DEFAULT_KEEPORIGINALS);
      frequentTermsOnly = settings.getAsBoolean("frequent_terms_only", UnsplitFilter.DEFAULT_FREQUENTTERMSONLY);
      minWordsUnsplitted = settings.getAsInt("min_words_unsplitted", UnsplitFilter.DEFAULT_MINWORDSUNSPLITTED);
      maxSubwords = settings.getAsInt("max_subwords", UnsplitFilter.DEFAULT_MAXSUBWORDS);
      
      String[] esSubwordTypes = settings.getAsArray("subword_types",UnsplitFilter.DEFAULT_SUBWORDSTYPES.toArray(new String[]{}));
      subwordTypes = new HashSet<>(Arrays.asList(esSubwordTypes));
      
      int esspanPayload = settings.getAsInt("span_payload",org.apache.lucene.analysis.UnsplitFilterFactory.DEFAULT_SPANPAYLOAD);
      if (esspanPayload==org.apache.lucene.analysis.UnsplitFilterFactory.DEFAULT_SPANPAYLOAD)
          spanPayload = UnsplitFilter.DEFAULT_SPANPAYLOAD;
      else
          spanPayload = intEncoder.encode(Integer.toString(esspanPayload).toCharArray());
      
      String esdelimiter = settings.get("delimiter",DEFAULT_DELIMITER);
      if (esdelimiter.equals(DEFAULT_DELIMITER))
          delimiter = UnsplitFilter.DEFAULT_DELIMITER;
      else
          delimiter = esdelimiter.charAt(0);
  }
  
  @Override
  public TokenStream create(TokenStream tokenStream) {
      return new UnsplitFilter(tokenStream, path,keepOriginals, frequentTermsOnly, minWordsUnsplitted, maxSubwords,delimiter,spanPayload,subwordTypes);
  }
}