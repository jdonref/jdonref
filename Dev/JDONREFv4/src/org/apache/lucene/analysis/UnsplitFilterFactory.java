package org.apache.lucene.analysis;

import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.BytesRef;

/**
 * 
 * @author Julien
 */
public class UnsplitFilterFactory extends TokenFilterFactory
{
  boolean keepOriginals;
  int minWordsUnsplitted;
  char delimiter;
  BytesRef spanPayload;

  String path;
  boolean frequentTermsOnly;
  int maxSubwords;
  Set<String> subWordsTypes;
  
  public static int DEFAULT_SPANPAYLOAD = 0;
  
  protected IntegerEncoder intEncoder = new IntegerEncoder();
    
  /** Creates a new EdgeNGramFilterFactory */
  public UnsplitFilterFactory(Map<String, String> args) {
    super(args);
    
    path = get(args,"path",UnsplitFilter.DEFAULT_PATH);
    keepOriginals = getBoolean(args, "keep_originals", UnsplitFilter.DEFAULT_KEEPORIGINALS);
    frequentTermsOnly = getBoolean(args, "frequent_terms_only", UnsplitFilter.DEFAULT_FREQUENTTERMSONLY);
    minWordsUnsplitted = getInt(args, "min_words_unsplitted", UnsplitFilter.DEFAULT_MINWORDSUNSPLITTED);
    maxSubwords = getInt(args, "max_subwords", UnsplitFilter.DEFAULT_MAXSUBWORDS);
    delimiter = getChar(args, "delimiter", UnsplitFilter.DEFAULT_DELIMITER);
    
    subWordsTypes = getSet(args, "subwords_types");
    if (subWordsTypes==null || subWordsTypes.isEmpty())
        subWordsTypes = UnsplitFilter.DEFAULT_SUBWORDSTYPES;
    
    int luceneSpanPayload = getInt(args,"span_payload", DEFAULT_SPANPAYLOAD);
    if (luceneSpanPayload==DEFAULT_SPANPAYLOAD)
        spanPayload = UnsplitFilter.DEFAULT_SPANPAYLOAD;
    else
        spanPayload = intEncoder.encode(Integer.toString(luceneSpanPayload).toCharArray());
    
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public UnsplitFilter create(TokenStream input)
  {
      return new UnsplitFilter(input,path,keepOriginals,frequentTermsOnly,minWordsUnsplitted,maxSubwords,delimiter,spanPayload,subWordsTypes);
  }
}