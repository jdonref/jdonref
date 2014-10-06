package org.apache.lucene.analysis;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

/**
 * 
 * @author Julien
 */
public class UnsplitFilterFactory extends TokenFilterFactory
{
  /** Creates a new EdgeNGramFilterFactory */
  public UnsplitFilterFactory(Map<String, String> args) {
    super(args);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public UnsplitFilter create(TokenStream input) {
      return new UnsplitFilter(input);
  }
}
