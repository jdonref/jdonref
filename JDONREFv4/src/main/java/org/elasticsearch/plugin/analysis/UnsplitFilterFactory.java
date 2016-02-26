package org.elasticsearch.plugin.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.UnsplitFilter;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.collect.ImmutableMap;
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
  protected boolean debugMode;
  protected boolean keepOriginals;
  protected boolean frequentTermsOnly;
  protected int minWordsUnsplitted;
  protected int percentageWordsUnsplitted = UnsplitFilter.DEFAULT_PERCENTAGEWORDSUNSPLITTED;
  protected int maxSubwords;
  protected char delimiter;
  protected BytesRef spanPayload;
  protected Set<String> subwordTypes;
  protected Set<Integer> requiredPayloads;
  protected Set<Integer> orderedPayloads;
  protected Set<Integer> alonePayloads;
  protected Set<Integer> atLeastOnePayloads;
  
  int minimumScore;
  
  boolean spanScorePayload;
  float scoreMaximum;
  protected Integer[] scoreItems;
  protected Integer[] scoreValue;
  
  boolean countPayload;
  
  int frequentTermsLimit;
  
  public static final String DEFAULT_DELIMITER = "";
  
  protected IntegerEncoder intEncoder = new IntegerEncoder();

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
  
  protected String[] convert(Integer[] tab)
  {
      if (tab==null || tab.length==0) return new String[]{};
      String[] res = new String[tab.length];
      int index=0;
      for(int i : tab)
      {
          res[index++] = Integer.toString(i);
      }
      return res;
  }
  
  protected Set<Integer> convert(String[] tab)
  {
      Set<Integer> requiredPayloads = new HashSet<>();
      for (String payload : tab)
      {
          requiredPayloads.add(Integer.parseInt(payload)); // NumberFormatException
      }
      return requiredPayloads;
  }
  
  protected Integer[] convert2tab(String[] tab)
  {
      Integer[] requiredPayloads = new Integer[tab.length];
      for(int i=0;i<tab.length;i++)
      {
          requiredPayloads[i] = Integer.parseInt(tab[i]); // NumberFormatException
      }
      return requiredPayloads;
  }
  
  @Inject
  public UnsplitFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings)
  {
      super(index, indexSettings, name, settings);
      path = settings.get("path",UnsplitFilter.DEFAULT_PATH);
      keepOriginals = settings.getAsBoolean("keep_originals", UnsplitFilter.DEFAULT_KEEPORIGINALS);
      frequentTermsOnly = settings.getAsBoolean("frequent_terms_only", UnsplitFilter.DEFAULT_FREQUENTTERMSONLY);
      
      ImmutableMap<String,String> map = settings.getAsMap();
      if (map.containsKey("min_words_unsplitted"))
      {
          if (map.get("min_words_unsplitted").contains("%"))
          {
              minWordsUnsplitted = UnsplitFilter.DEFAULT_CALCULATEDWORDSUNSPLITTED;
              String tmpMinWordsUnsplitted = settings.get("min_words_unsplitted");
              if (tmpMinWordsUnsplitted==null)
                percentageWordsUnsplitted = UnsplitFilter.DEFAULT_PERCENTAGEWORDSUNSPLITTED;
              else
                percentageWordsUnsplitted = Integer.parseInt(tmpMinWordsUnsplitted.split("%")[0]);
          }
          else
          {
              minWordsUnsplitted = settings.getAsInt("min_words_unsplitted", UnsplitFilter.DEFAULT_MINWORDSUNSPLITTED);
          }
      }
      maxSubwords = settings.getAsInt("max_subwords", UnsplitFilter.DEFAULT_MAXSUBWORDS);
      debugMode = settings.getAsBoolean("debugMode", UnsplitFilter.DEFAULT_DEBUGMODE);
      
      String[] esSubwordTypes = settings.getAsArray("subword_types",UnsplitFilter.DEFAULT_SUBWORDSTYPES.toArray(new String[]{}));
      subwordTypes = new HashSet<>(Arrays.asList(esSubwordTypes));
      
      String[] tmpRequiredPayloads = settings.getAsArray("required_payloads",convert(UnsplitFilter.DEFAULT_REQUIREDPAYLOADS));
      requiredPayloads = convert(tmpRequiredPayloads);
      
      String[] tmpOrderedPayloads = settings.getAsArray("ordered_payloads",convert(UnsplitFilter.DEFAULT_ORDEREDPAYLOADS));
      orderedPayloads = convert(tmpOrderedPayloads);
      
      String[] tmpAlonePayloads = settings.getAsArray("alone_payloads",convert(UnsplitFilter.DEFAULT_ALONEPAYLOADS));
      alonePayloads = convert(tmpAlonePayloads);
      
      String[] tmpAtLeastOnePayloads = settings.getAsArray("at_least_one_payloads",convert(UnsplitFilter.DEFAULT_ATLEASTONEPAYLOADS));
      atLeastOnePayloads = convert(tmpAtLeastOnePayloads);
      
      int esspanPayload = settings.getAsInt("span_payload",org.apache.lucene.analysis.UnsplitFilterFactory.DEFAULT_SPANPAYLOAD);
      if (esspanPayload==org.apache.lucene.analysis.UnsplitFilterFactory.DEFAULT_SPANPAYLOAD)
          spanPayload = UnsplitFilter.DEFAULT_SPANPAYLOAD;
      else
          spanPayload = intEncoder.encode(Integer.toString(esspanPayload).toCharArray());
      
      countPayload = settings.getAsBoolean("count_payload", UnsplitFilter.DEFAULT_COUNTPAYLOAD);
      
      scoreMaximum = settings.getAsFloat("score_maximum", UnsplitFilter.DEFAULT_SCOREMAXIMUM);
      spanScorePayload = settings.getAsBoolean("span_score_payload", UnsplitFilter.DEFAULT_SPANSCOREPAYLOAD);
      
      String[] tmpScoreItems = settings.getAsArray("score_items",convert(UnsplitFilter.DEFAULT_SCOREITEMS));
      scoreItems = convert2tab(tmpScoreItems);
      
      String[] tmpScoreValue = settings.getAsArray("score_value",convert(UnsplitFilter.DEFAULT_SCOREVALUE));
      scoreValue = convert2tab(tmpScoreValue);
      
      frequentTermsLimit = settings.getAsInt("frequent_terms_limit", UnsplitFilter.DEFAULT_FREQUENTTERMSLIMIT);
      
      minimumScore = settings.getAsInt("minimum_score", UnsplitFilter.DEFAULT_MINIMUMSCORE);
      
      String esdelimiter = settings.get("delimiter",DEFAULT_DELIMITER);
      if (esdelimiter.equals(DEFAULT_DELIMITER))
          delimiter = UnsplitFilter.DEFAULT_DELIMITER;
      else
          delimiter = esdelimiter.charAt(0);
  }
  
  @Override
  public TokenStream create(TokenStream tokenStream) {
      return new UnsplitFilter(tokenStream, path,keepOriginals, frequentTermsOnly, minWordsUnsplitted, percentageWordsUnsplitted, maxSubwords,delimiter,spanPayload,subwordTypes,requiredPayloads,orderedPayloads,alonePayloads,atLeastOnePayloads,spanScorePayload,scoreMaximum,scoreItems,scoreValue,countPayload,frequentTermsLimit,minimumScore,debugMode);
  }
}