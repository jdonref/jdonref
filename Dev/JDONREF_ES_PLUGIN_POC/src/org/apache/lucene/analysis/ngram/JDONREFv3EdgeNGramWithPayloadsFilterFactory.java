package org.apache.lucene.analysis.ngram;

import mi.ppol.jdonref.espluginpoc.index.analysis.*;
import org.apache.lucene.analysis.ngram.JDONREFv3EdgeNGramWithPayloadsFilter;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * 
 * @author Julien
 */
public class JDONREFv3EdgeNGramWithPayloadsFilterFactory extends TokenFilterFactory
{
  protected boolean withPayloads = false;
  protected int nonPrivateMinGramSize;
  protected int nonPrivateMaxGramSize;
  protected String nonPrivateSide;
  
  /** Creates a new EdgeNGramFilterFactory */
  public JDONREFv3EdgeNGramWithPayloadsFilterFactory(Map<String, String> args) {
    super(args);
    nonPrivateMinGramSize = getInt(args, "minGramSize", EdgeNGramTokenFilter.DEFAULT_MIN_GRAM_SIZE);
    nonPrivateMaxGramSize = getInt(args, "maxGramSize", EdgeNGramTokenFilter.DEFAULT_MAX_GRAM_SIZE);
    nonPrivateSide = get(args, "side", EdgeNGramTokenFilter.Side.FRONT.getLabel());
    withPayloads = getBoolean(args, "withPayloads", false);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public JDONREFv3EdgeNGramWithPayloadsFilter create(TokenStream input) {
      return new JDONREFv3EdgeNGramWithPayloadsFilter(luceneMatchVersion, input, nonPrivateSide, nonPrivateMinGramSize, nonPrivateMaxGramSize, withPayloads);
  }
}
