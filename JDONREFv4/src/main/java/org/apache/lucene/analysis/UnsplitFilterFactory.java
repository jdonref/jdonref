package org.apache.lucene.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.BytesRef;

/**
 * WARNING : Pas opérationnel pour le scoring
 * @author Julien
 */
public class UnsplitFilterFactory extends TokenFilterFactory
{
  boolean debugMode;
  boolean keepOriginals;
  int minWordsUnsplitted;
  int percentageWordsUnsplitted;
  char delimiter;
  BytesRef spanPayload;

  String path;
  boolean frequentTermsOnly;
  int maxSubwords;
  Set<String> subWordsTypes;
  Set<Integer> requiredPayloads;
  Set<Integer> orderedPayloads;
  Set<Integer> alonePayloads;
  Set<Integer> atLeastOnePayloads;
  
  int minimumScore;
  
  boolean countPayload;
  
  boolean spanScorePayload;
  float scoreMaximum;
  Integer[] scoreItems;
  Integer[] scoreValue;
  
  int frequentTermsLimit;
  
  public static int DEFAULT_SPANPAYLOAD = 0;
  
  protected IntegerEncoder intEncoder = new IntegerEncoder();

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
  
  protected Set<Integer> getIntSet(Map<String,String> args, String field, Set<Integer> default_value)
  {
    Set<String> tmpRes = getSet(args, field);
    Set<Integer> res;
    if (tmpRes==null || tmpRes.isEmpty())
        res = default_value;
    else
    {
        res = convert(tmpRes);
    }
    return res;
  }
  
  /** Creates a new EdgeNGramFilterFactory */
  public UnsplitFilterFactory(Map<String, String> args) {
    super(args);
    
    path = get(args,"path",UnsplitFilter.DEFAULT_PATH);
    keepOriginals = getBoolean(args, "keep_originals", UnsplitFilter.DEFAULT_KEEPORIGINALS);
    frequentTermsOnly = getBoolean(args, "frequent_terms_only", UnsplitFilter.DEFAULT_FREQUENTTERMSONLY);
    
    String tmpMinWordsUnsplitted = get(args,"min_words_unsplitted");
    if (tmpMinWordsUnsplitted==null)
        minWordsUnsplitted = UnsplitFilter.DEFAULT_MINWORDSUNSPLITTED;
    else
    {
        if (tmpMinWordsUnsplitted.contains("%"))
        {
            minWordsUnsplitted = UnsplitFilter.DEFAULT_CALCULATEDWORDSUNSPLITTED;
            percentageWordsUnsplitted = Integer.parseInt(tmpMinWordsUnsplitted.split("%")[0]);
        }
        else
            minWordsUnsplitted = getInt(args, "min_words_unsplitted", UnsplitFilter.DEFAULT_MINWORDSUNSPLITTED);
    }
    
    maxSubwords = getInt(args, "max_subwords", UnsplitFilter.DEFAULT_MAXSUBWORDS);
    delimiter = getChar(args, "delimiter", UnsplitFilter.DEFAULT_DELIMITER);
    debugMode = getBoolean(args,"debugMode", UnsplitFilter.DEFAULT_DEBUGMODE);
    
    subWordsTypes = getSet(args, "subwords_types");
    if (subWordsTypes==null || subWordsTypes.isEmpty())
        subWordsTypes = UnsplitFilter.DEFAULT_SUBWORDSTYPES;
    
    requiredPayloads = getIntSet(args, "required_payloads",UnsplitFilter.DEFAULT_REQUIREDPAYLOADS);
    orderedPayloads = getIntSet(args, "ordered_payloads",UnsplitFilter.DEFAULT_ORDEREDPAYLOADS);
    alonePayloads = getIntSet(args, "alone_payloads",UnsplitFilter.DEFAULT_ALONEPAYLOADS);
    atLeastOnePayloads = getIntSet(args, "at_least_one_payloads",UnsplitFilter.DEFAULT_ATLEASTONEPAYLOADS);
    
    int luceneSpanPayload = getInt(args,"span_payload", DEFAULT_SPANPAYLOAD);
    if (luceneSpanPayload==DEFAULT_SPANPAYLOAD)
        spanPayload = UnsplitFilter.DEFAULT_SPANPAYLOAD;
    else
        spanPayload = intEncoder.encode(Integer.toString(luceneSpanPayload).toCharArray());
    
    minimumScore = getInt(args,"minimum_score",UnsplitFilter.DEFAULT_MINIMUMSCORE);
    
    countPayload = getBoolean(args, "count_payload", UnsplitFilter.DEFAULT_COUNTPAYLOAD);
    
    spanScorePayload = getBoolean(args, "span_score_payload", UnsplitFilter.DEFAULT_SPANSCOREPAYLOAD);
    scoreMaximum = getFloat(args,"score_maximum", UnsplitFilter.DEFAULT_SCOREMAXIMUM);
    //scoreItems = getIntSet(args,"score_items", UnsplitFilter.DEFAULT_SCOREITEMS);
    //scoreValue = getIntSet(args,"score_value", UnsplitFilter.DEFAULT_SCOREVALUE);
    
    frequentTermsLimit = getInt(args, "frequent_terms_limit", UnsplitFilter.DEFAULT_FREQUENTTERMSLIMIT);
    
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public UnsplitFilter create(TokenStream input)
  {
      return new UnsplitFilter(input,path,keepOriginals,frequentTermsOnly,minWordsUnsplitted,percentageWordsUnsplitted,maxSubwords,delimiter,spanPayload,subWordsTypes,requiredPayloads,orderedPayloads,alonePayloads,atLeastOnePayloads,spanScorePayload,scoreMaximum,scoreItems,scoreValue,countPayload,frequentTermsLimit,minimumScore,debugMode);
  }
}