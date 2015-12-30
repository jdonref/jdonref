package org.apache.lucene.analysis.synonym;


import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;

@Deprecated
final class SlowSynonymWithPayloadsFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  private final String synonyms;
  private final boolean ignoreCase;
  private final boolean expand;
  private final String tf;
  private final Map<String, String> tokArgs = new HashMap<>();
  
  public SlowSynonymWithPayloadsFilterFactory(Map<String,String> args) {
    super(args);
    synonyms = require(args, "synonyms");
    ignoreCase = getBoolean(args, "ignoreCase", false);
    expand = getBoolean(args, "expand", true);

    tf = get(args, "tokenizerFactory");
    if (tf != null) {
      assureMatchVersion();
      tokArgs.put("luceneMatchVersion", getLuceneMatchVersion().toString());
      for (Iterator<String> itr = args.keySet().iterator(); itr.hasNext();) {
        String key = itr.next();
        tokArgs.put(key.replaceAll("^tokenizerFactory\\.",""), args.get(key));
        itr.remove();
      }
    }
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }
  
  public void inform(ResourceLoader loader) throws IOException {
    TokenizerFactory tokFactory = null;
    if( tf != null ){
      tokFactory = loadTokenizerFactory(loader, tf);
    }

    Iterable<String> wlist=loadRules( synonyms, loader );
    
    synMap = new SlowSynonymMap(ignoreCase);
    parseRules(wlist, synMap, "=>", ",", expand,tokFactory);
  }
  
  /**
   * @return a list of all rules
   */
  protected Iterable<String> loadRules( String synonyms, ResourceLoader loader ) throws IOException {
    List<String> wlist=null;
    File synonymFile = new File(synonyms);
    if (synonymFile.exists()) {
      wlist = getLines(loader, synonyms);
    } else  {
      List<String> files = splitFileNames(synonyms);
      wlist = new ArrayList<>();
      for (String file : files) {
        List<String> lines = getLines(loader, file.trim());
        wlist.addAll(lines);
      }
    }
    return wlist;
  }

  private SlowSynonymMap synMap;

  static void parseRules(Iterable<String> rules, SlowSynonymMap map, String mappingSep,
    String synSep, boolean expansion, TokenizerFactory tokFactory) throws IOException {
    int count=0;
    for (String rule : rules) {
      // To use regexes, we need an expression that specifies an odd number of chars.
      // This can't really be done with string.split(), and since we need to
      // do unescaping at some point anyway, we wouldn't be saving any effort
      // by using regexes.

      List<String> mapping = splitSmart(rule, mappingSep, false);

      List<List<String>> source;
      List<List<String>> target;

      if (mapping.size() > 2) {
        throw new IllegalArgumentException("Invalid Synonym Rule:" + rule);
      } else if (mapping.size()==2) {
        source = getSynList(mapping.get(0), synSep, tokFactory);
        target = getSynList(mapping.get(1), synSep, tokFactory);
      } else {
        source = getSynList(mapping.get(0), synSep, tokFactory);
        if (expansion) {
          // expand to all arguments
          target = source;
        } else {
          // reduce to first argument
          target = new ArrayList<>(1);
          target.add(source.get(0));
        }
      }

      boolean includeOrig=false;
      for (List<String> fromToks : source) {
        count++;
        for (List<String> toToks : target) {
          map.add(fromToks,
                  SlowSynonymMap.makeTokens(toToks),
                  includeOrig,
                  true
          );
        }
      }
    }
  }

  // a , b c , d e f => [[a],[b,c],[d,e,f]]
  private static List<List<String>> getSynList(String str, String separator, TokenizerFactory tokFactory) throws IOException {
    List<String> strList = splitSmart(str, separator, false);
    // now split on whitespace to get a list of token strings
    List<List<String>> synList = new ArrayList<>();
    for (String toks : strList) {
      List<String> tokList = tokFactory == null ?
        splitWS(toks, true) : splitByTokenizer(toks, tokFactory);
      synList.add(tokList);
    }
    return synList;
  }

  private static List<String> splitByTokenizer(String source, TokenizerFactory tokFactory) throws IOException{
    StringReader reader = new StringReader( source );
    TokenStream ts = loadTokenizer(tokFactory, reader);
    List<String> tokList = new ArrayList<>();
    try {
      CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
      ts.reset();
      while (ts.incrementToken()){
        if( termAtt.length() > 0 )
          tokList.add( termAtt.toString() );
      }
    } finally{
      reader.close();
    }
    return tokList;
  }

  private TokenizerFactory loadTokenizerFactory(ResourceLoader loader, String cname) throws IOException {
    Class<? extends TokenizerFactory> clazz = loader.findClass(cname, TokenizerFactory.class);
    try {
      TokenizerFactory tokFactory = clazz.getConstructor(Map.class).newInstance(tokArgs);
      if (tokFactory instanceof ResourceLoaderAware) {
        ((ResourceLoaderAware) tokFactory).inform(loader);
      }
      return tokFactory;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static TokenStream loadTokenizer(TokenizerFactory tokFactory, Reader reader){
    return tokFactory.create( reader );
  }

  public SlowSynonymMap getSynonymMap() {
    return synMap;
  }

  public SlowSynonymWithPayloadsFilter create(TokenStream input) {
    return new SlowSynonymWithPayloadsFilter(input,synMap);
  }
  
  public static List<String> splitWS(String s, boolean decode) {
    ArrayList<String> lst = new ArrayList<>(2);
    StringBuilder sb = new StringBuilder();
    int pos=0, end=s.length();
    while (pos < end) {
      char ch = s.charAt(pos++);
      if (Character.isWhitespace(ch)) {
        if (sb.length() > 0) {
          lst.add(sb.toString());
          sb=new StringBuilder();
        }
        continue;
      }

      if (ch=='\\') {
        if (!decode) sb.append(ch);
        if (pos>=end) break;  // ERROR, or let it go?
        ch = s.charAt(pos++);
        if (decode) {
          switch(ch) {
            case 'n' : ch='\n'; break;
            case 't' : ch='\t'; break;
            case 'r' : ch='\r'; break;
            case 'b' : ch='\b'; break;
            case 'f' : ch='\f'; break;
          }
        }
      }

      sb.append(ch);
    }

    if (sb.length() > 0) {
      lst.add(sb.toString());
    }

    return lst;
  }
  
  /** Splits a backslash escaped string on the separator.
   * <p>
   * Current backslash escaping supported:
   * <br> \n \t \r \b \f are escaped the same as a Java String
   * <br> Other characters following a backslash are produced verbatim (\c => c)
   *
   * @param s  the string to split
   * @param separator the separator to split on
   * @param decode decode backslash escaping
   */
  public static List<String> splitSmart(String s, String separator, boolean decode) {
    ArrayList<String> lst = new ArrayList<>(2);
    StringBuilder sb = new StringBuilder();
    int pos=0, end=s.length();
    while (pos < end) {
      if (s.startsWith(separator,pos)) {
        if (sb.length() > 0) {
          lst.add(sb.toString());
          sb=new StringBuilder();
        }
        pos+=separator.length();
        continue;
      }

      char ch = s.charAt(pos++);
      if (ch=='\\') {
        if (!decode) sb.append(ch);
        if (pos>=end) break;  // ERROR, or let it go?
        ch = s.charAt(pos++);
        if (decode) {
          switch(ch) {
            case 'n' : ch='\n'; break;
            case 't' : ch='\t'; break;
            case 'r' : ch='\r'; break;
            case 'b' : ch='\b'; break;
            case 'f' : ch='\f'; break;
          }
        }
      }

      sb.append(ch);
    }

    if (sb.length() > 0) {
      lst.add(sb.toString());
    }

    return lst;
  }
}
