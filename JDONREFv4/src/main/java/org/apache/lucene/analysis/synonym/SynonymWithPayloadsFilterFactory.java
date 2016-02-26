package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.Version;

/**
 * Adjustement for payloads
 */
public class SynonymWithPayloadsFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  private final TokenFilterFactory delegator;

  public SynonymWithPayloadsFilterFactory(Map<String,String> args) {
    super(args);
    assureMatchVersion();
    if (luceneMatchVersion.onOrAfter(Version.LUCENE_3_4)) {
      delegator = new FSTSynonymWithPayloadsFilterFactory(new HashMap<>(getOriginalArgs()));
    } else {
      // check if you use the new optional arg "format". this makes no sense for the old one, 
      // as its wired to solr's synonyms format only.
      if (args.containsKey("format") && !args.get("format").equals("solr")) {
        throw new IllegalArgumentException("You must specify luceneMatchVersion >= 3.4 to use alternate synonyms formats");
      }
      delegator = new SlowSynonymWithPayloadsFilterFactory(new HashMap<>(getOriginalArgs()));
    }
  }

  @Override
  public TokenStream create(TokenStream input) {
    return delegator.create(input);
  }

  @Override
  public void inform(ResourceLoader loader) throws IOException {
    ((ResourceLoaderAware) delegator).inform(loader);
  }

  /**
   * Access to the delegator TokenFilterFactory for test verification
   *
   * @deprecated Method exists only for testing 4x, will be removed in 5.0
   * @lucene.internal
   */
  @Deprecated
  TokenFilterFactory getDelegator() {
    return delegator;
  }
}
