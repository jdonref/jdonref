package org.apache.lucene.analysis;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.BytesRef;

/**
 * 
 * Front EdgeNgram compatible only.
 * 
 * @author Julien
 */
public class UnsplitFilter extends TokenFilter
{
  public static final String UNSPLIT_TYPE = "UNSPLITED";
    
  public static final boolean DEFAULT_DEBUGMODE = false;
  public static final String DEFAULT_PATH = "/usr/share/elasticsearch/plugins/jdonrefv4-0.2/word84.txt";
  public static final boolean DEFAULT_KEEPORIGINALS = true;
  public static final int DEFAULT_MINWORDSUNSPLITTED = 2;
  public static final int DEFAULT_CALCULATEDWORDSUNSPLITTED = -1;
  public static final int DEFAULT_PERCENTAGEWORDSUNSPLITTED = 70;
  public static final boolean DEFAULT_FREQUENTTERMSONLY = true;
  public static final int DEFAULT_MAXSUBWORDS = 1;
  public static final Set<String> DEFAULT_SUBWORDSTYPES = new HashSet<>(Arrays.asList(new String[]{"NGRAM"}));
  public static final Set<Integer> DEFAULT_REQUIREDPAYLOADS = new HashSet<>();
  public static final Set<Integer> DEFAULT_ORDEREDPAYLOADS = new HashSet<>();
  public static final Set<Integer> DEFAULT_ALONEPAYLOADS = new HashSet<>();
  public static final Set<Integer> DEFAULT_ATLEASTONEPAYLOADS = new HashSet<>();
  public static final Integer[] DEFAULT_SCOREITEMS = new Integer[]{};
  public static final Integer[] DEFAULT_SCOREVALUE = new Integer[]{};
  public static final float DEFAULT_SCOREMAXIMUM = 200;
  public static final boolean DEFAULT_SPANSCOREPAYLOAD = false;
  public static final char DEFAULT_DELIMITER = 0;
  public static final BytesRef DEFAULT_SPANPAYLOAD = null;
  public static final boolean DEFAULT_COUNTPAYLOAD = false;
  public static final int DEFAULT_FREQUENTTERMSLIMIT = 0;
  public static final int DEFAULT_MINIMUMSCORE = 100;
  
  protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
  protected final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
  protected final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
  protected final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
  protected final PayloadAttribute payloadAtt = addAttribute(PayloadAttribute.class);
  
  /**
   * @param version the <a href="#version">Lucene match version</a>
   * @param input {@link TokenStream} holding the input to be tokenized
   */
  public UnsplitFilter(TokenStream input) {
    super(input);
    
    current = 0;
    phrases = new ArrayList<>();
    sizes = new ArrayList<>();
    offsets = new ArrayList<>();
    numWords = new ArrayList<>();
    numSubWords = new ArrayList<>();
    nGramPayloads = new ArrayList<>();
    tokenAlonePayloads = new ArrayList<>();
    tokenAtLeastOnePayloads = new ArrayList<>();
    
    lastphrases = new ArrayList<>(); // not needed. a clone is done later
    lastsizes = new ArrayList<>();
    lastoffsets = new ArrayList<>();
    lastnumWords = new ArrayList<>();
    lastnumSubWords = new ArrayList<>();
    lastNGramPayloads = new ArrayList<>();
    lastTokenAlonePayloads = new ArrayList<>();
    lastTokenAtLeastOnePayloads = new ArrayList<>();
    
    scores = new ArrayList<>();
    lastScores = new ArrayList<>();
    
    lastPositionByPayload = new HashMap<Integer,Integer>();
  }
  
  protected int current;
  protected ArrayList<String[]> lastphrases;
  protected ArrayList<Integer> lastsizes;
  protected ArrayList<Integer> lastoffsets;
  protected ArrayList<Integer> lastnumWords;
  protected ArrayList<Integer> lastnumSubWords;
  protected ArrayList<Boolean> lastNGramPayloads;
  protected ArrayList<HashSet<Integer>> lastTokenAlonePayloads;
  protected ArrayList<HashSet<Integer>> lastTokenAtLeastOnePayloads;
  protected ArrayList<Integer> lastScores;
  
  protected ArrayList<String[]> phrases;
  protected ArrayList<Integer> sizes;
  protected ArrayList<Integer> offsets;
  protected ArrayList<Integer> numWords;
  protected ArrayList<Integer> numSubWords;
  protected ArrayList<Boolean> nGramPayloads;
  protected ArrayList<HashSet<Integer>> tokenAlonePayloads;
  protected ArrayList<HashSet<Integer>> tokenAtLeastOnePayloads;
  protected ArrayList<Integer> scores;
  
  protected int totalScore;
  
  protected HashMap<Integer,Integer> lastPositionByPayload;
  
  protected String path = DEFAULT_PATH;
  protected boolean debugMode = DEFAULT_DEBUGMODE;
  protected boolean keepOriginals = DEFAULT_KEEPORIGINALS;
  protected boolean frequentTermsOnly = DEFAULT_FREQUENTTERMSONLY;
  protected int minWordsUnsplitted = DEFAULT_MINWORDSUNSPLITTED; // 0 means automatically take the maximum number.
  protected int percentageWordsUnsplitted = DEFAULT_PERCENTAGEWORDSUNSPLITTED;
  protected int maxSubwords = DEFAULT_MAXSUBWORDS;
  protected char delimiter = DEFAULT_DELIMITER;
  protected BytesRef spanPayload = DEFAULT_SPANPAYLOAD;
  protected Set<String> subwordsTypes = DEFAULT_SUBWORDSTYPES;
  protected Set<Integer> requiredPayloads = DEFAULT_REQUIREDPAYLOADS;
  protected Set<Integer> orderedPayloads = DEFAULT_ORDEREDPAYLOADS;
  protected Set<Integer> alonePayloads = DEFAULT_ALONEPAYLOADS;
  protected Set<Integer> atLeastOnePayloads = DEFAULT_ATLEASTONEPAYLOADS; // same as alonePayloads. Must be fusionned
  
  protected int frequentTermsLimit = DEFAULT_FREQUENTTERMSLIMIT;
  protected HashMap<Double,Integer> frequentPhrases;
  
  protected int minimum_score = DEFAULT_MINIMUMSCORE;
  
  protected boolean count_payload = DEFAULT_COUNTPAYLOAD;
  
  protected HashMap<Integer,Integer> score_value_by_payload;
  protected Integer[] score_items = DEFAULT_SCOREITEMS;
  protected Integer[] score_value = DEFAULT_SCOREVALUE;
  protected float score_maximum = DEFAULT_SCOREMAXIMUM;
  protected boolean span_score_payload = DEFAULT_SPANSCOREPAYLOAD;
  
  protected int maxsizefrequentsize = 1;
  
  UnsplitFilterFrequentTermsUtil util;

  public UnsplitFilter(TokenStream input, String path, boolean keepOriginals, boolean frequentTermsOnly, int minWordsUnsplitted, 
          int percentageWordsUnsplitted,int maxSubwords, char delimiter, BytesRef spanPayload,
          Set<String> subwordsTypes, Set<Integer> requiredPayloads, Set<Integer> orderedPayloads, Set<Integer> alonePayloads, Set<Integer> atLeastOnePayloads,
          boolean span_score_payload, float score_maximum, Integer[] score_items, Integer[] score_value, boolean countPayload, int frequentTermsLimit,
          int minimumScore,
          boolean debugMode)
  {
      this(input);
      
      this.path = path;
      this.keepOriginals = keepOriginals;
      this.frequentTermsOnly = frequentTermsOnly;
      this.minWordsUnsplitted = minWordsUnsplitted;
      this.percentageWordsUnsplitted = percentageWordsUnsplitted;
      this.maxSubwords = maxSubwords;
      this.delimiter = delimiter;
      this.spanPayload = spanPayload;
      this.subwordsTypes = subwordsTypes;
      this.requiredPayloads = requiredPayloads;
      this.orderedPayloads = orderedPayloads;
      this.alonePayloads = alonePayloads;
      this.atLeastOnePayloads = atLeastOnePayloads;
      this.span_score_payload = span_score_payload;
      this.count_payload = countPayload;
      this.score_maximum = score_maximum;
      this.score_items = score_items;
      this.score_value = score_value;
      this.minimum_score = minimumScore;
      makeScoreByPayload();
      this.debugMode = debugMode;
      currentMinWordUnsplitted = this.minWordsUnsplitted;
      this.frequentTermsLimit = frequentTermsLimit;
      
      this.tokens = new ArrayList<>();
      
      if (frequentTermsOnly || frequentTermsLimit>0)
      {
          util = new UnsplitFilterFrequentTermsUtil();
          util.setFilePath(path);
          util.getMostFrequentHashTerms();
          
          maxsizefrequentsize = util.getMostFrequentHashTerms().size();
      }
      
      if (frequentTermsLimit!=0)
      {
          frequentPhrases = new HashMap<>();
      }
  }
  
  protected boolean isFrequentPhrase(String[] get) {
      for(String token : get)
      {
          if (util.getMostFrequentHashTerms().get(token)==null) return false;
      }
      return true;
  }
  
  // assert : get is sorted
  protected double getFrequentIndice(String[] get)
  {
      double res = 0;
      for(String token : get)
      {
          Integer indice = util.getMostFrequentHashTerms().get(token);
          if (indice==null) return 0;
          res = res*maxsizefrequentsize + indice;
      }
      return res;
  }
  
  protected void makeScoreByPayload()
  {
      score_value_by_payload = new HashMap<Integer,Integer>();
      for(int i=0;i<score_items.length;i++)
      {
          score_value_by_payload.put(score_items[i],score_value[i]);
      }
  }
  
  protected void fillPhrases(char[] current,int size,int offset,boolean subword,boolean required,boolean ordered,boolean alone,int payload,boolean atleastone,boolean lastbypayload)
  {
      if (lastphrases.isEmpty()) // firsts tokens (with ngrams)
      {
          // only one empty one (if not required)
          if (phrases.isEmpty()                      // empty token only needed one time per phrase
                  && this.minWordsUnsplitted>0       // no empty token if all-token-in-one-only-mode is active
                  && !required                       // no empty token if payload required
                  && !(atleastone && lastbypayload)) // check if atleastone is not lonely
          {
            phrases.add(new String[]{""});
            sizes.add(0);
            offsets.add(offset);
            numWords.add(0);
            numSubWords.add(0);
            nGramPayloads.add(false);
            tokenAlonePayloads.add(new HashSet<Integer>());
            tokenAtLeastOnePayloads.add(new HashSet<Integer>());
            if (span_score_payload)
                scores.add(0);
          }
          
          phrases.add(new String[]{new String(current,0,size)});
          sizes.add(size);
          offsets.add(offset);
          numWords.add(1);
          numSubWords.add(subword?1:0);
          nGramPayloads.add(ordered && subword);
          if (span_score_payload)
            scores.add(payload<=0?0:(score_value_by_payload.containsKey(payload)?score_value_by_payload.get(payload):0));
          if (alone)
            tokenAlonePayloads.add(new HashSet<Integer>(payload));
          else
            tokenAlonePayloads.add(new HashSet<Integer>());
          if (atleastone)
            tokenAtLeastOnePayloads.add(new HashSet<Integer>(payload));
          else
            tokenAtLeastOnePayloads.add(new HashSet<Integer>());
      }
      else
      {
        if (phrases.isEmpty() && this.minWordsUnsplitted>0 && !required) // check if atleast is not the last
            for(int i=0;i<lastphrases.size();i++)
            {
                if (!(atleastone && lastbypayload && !lastTokenAtLeastOnePayloads.get(i).contains(payload)))
                {
                    // empty one
                    phrases.add(lastphrases.get(i));
                    sizes.add(lastsizes.get(i));
                    offsets.add(lastoffsets.get(i));
                    numWords.add(lastnumWords.get(i));
                    numSubWords.add(lastnumSubWords.get(i));
                    nGramPayloads.add(lastNGramPayloads.get(i));
                    tokenAlonePayloads.add(lastTokenAlonePayloads.get(i));
                    tokenAtLeastOnePayloads.add(lastTokenAtLeastOnePayloads.get(i));
                    if (span_score_payload)
                        scores.add(lastScores.get(i));
                }
            }
        
        for(int i=0;i<lastphrases.size();i++)
        {
            if (!(subword && lastnumSubWords.get(i)>=maxSubwords) &&          // check du seuil des ngrams
                    (!ordered || (ordered && !lastNGramPayloads.get(i))) &&   // seul le dernier des ordonnés est ngram
                    (!alone || alone && !lastTokenAlonePayloads.get(i).contains(payload))) // pour les tokens seuls
            {
                String[] new_phrase_i;
                boolean concat = false;
                if (lastsizes.get(i)>0)
                {
                    concat = true;
                    new_phrase_i = Arrays.copyOf(lastphrases.get(i), lastphrases.get(i).length+1);
                    new_phrase_i[new_phrase_i.length-1] = new String(current,0,size);
                }
                else
                    new_phrase_i = new String[]{new String(current,0,size)};
                
                //Logger.getLogger("unsplit").info("Span "+new String(new_phrase_i,0,lastsizes.get(i)+(delimiter!=DEFAULT_DELIMITER?1:0)+size));
                
                phrases.add(new_phrase_i);
                sizes.add(lastsizes.get(i)+(concat && delimiter!=DEFAULT_DELIMITER?1:0)+size);
                offsets.add(lastoffsets.get(i)+(concat && delimiter!=DEFAULT_DELIMITER?1:0)+offset);
                numWords.add(lastnumWords.get(i)+1);
                numSubWords.add(lastnumSubWords.get(i)+(subword?1:0));
                nGramPayloads.add(ordered && subword);
                
                HashSet<Integer> tmpTokenAlonePayload = ((HashSet<Integer>)lastTokenAlonePayloads.get(i).clone());
                if (alone)
                    tmpTokenAlonePayload.add(payload);
                tokenAlonePayloads.add(tmpTokenAlonePayload);
                
                HashSet<Integer> tmpTokenAtLeastOnePayloads = ((HashSet<Integer>)lastTokenAtLeastOnePayloads.get(i).clone());
                if (atleastone)
                    tmpTokenAtLeastOnePayloads.add(payload);
                tokenAtLeastOnePayloads.add(tmpTokenAtLeastOnePayloads);
                
                if (span_score_payload)
                    scores.add(lastScores.get(i)+(payload<=0?0:(score_value_by_payload.containsKey(payload)?score_value_by_payload.get(payload):0)));
            }
        }
      }
  }

  int lastIntPayload = -1;
  int lastPosition = -1;
  int lastPositionLength = -1; 
  int firstPositionIncrement = 0;
  int currentMinWordUnsplitted;

  class Token
  {
        char[] curTermBuffer;
        int length;
        int tokStart;
        int tokEnd;
        String type;
        boolean subword;
        BytesRef payload;
        int intPayload;
        boolean required;
        boolean ordered;
        boolean alone;
        boolean atleastone;
  }
  
  ArrayList<Token> tokens;
  int heap = 0;
  int nbtokens = 0;
  
  boolean debugTime = false;
  long startTime;
  long endComputeTokens;
  long endUnHeapTokens;
  long clearAttributesTime;
  long returnTrueTime;
  
  @Override
  public final boolean incrementToken() throws IOException
  { 
    if (debugTime && startTime==0) startTime = System.nanoTime();
      
    // compute everytokens
    while(heap==0 && input.incrementToken())
    {
        Token t = new Token();
        
        t.curTermBuffer = termAtt.buffer().clone();
        t.length = termAtt.length();
        t.tokStart = offsetAtt.startOffset();
        t.tokEnd = offsetAtt.endOffset();
        t.type = typeAtt.type();
        t.subword = subwordsTypes.contains(t.type);
        t.payload = payloadAtt.getPayload();
        if (t.payload!=null)
        {
            t.intPayload = PayloadHelper.decodeInt(t.payload.bytes,t.payload.offset);
            t.required = requiredPayloads.contains(t.intPayload);
            t.ordered = orderedPayloads.contains(t.intPayload);
            t.alone = alonePayloads.contains(t.intPayload);
            t.atleastone = atLeastOnePayloads.contains(t.intPayload);
        }
        else
        {
            t.intPayload = -1;
            t.required = false;
            t.ordered = false;
            t.alone = false;
            t.atleastone = false;
        }
        
        if (t.tokStart!=lastPosition)
        {
            nbtokens++;
            lastPosition = t.tokStart;
            
            if (span_score_payload && t.payload!=null)
            {
                if (!(alonePayloads.contains(t.intPayload) && lastPositionByPayload.containsKey(t.intPayload)) && this.score_value_by_payload.containsKey(t.intPayload))
                    totalScore += this.score_value_by_payload.get(t.intPayload);
            }
        }
        if (t.payload!=null)
        {
            lastPositionByPayload.put(t.intPayload, t.tokStart); // need to be at the end (used before)
        }
        
        tokens.add(t);        
    }
    
    if (heap==0) // first start of unheap
    {
        lastPosition=-1;
        if (minWordsUnsplitted==DEFAULT_CALCULATEDWORDSUNSPLITTED)
        {
            currentMinWordUnsplitted = minWordsUnsplitted = (int)(((float)nbtokens*(float)percentageWordsUnsplitted)/100.0f);
        }
    } 
      
    if (endComputeTokens==0) endComputeTokens = System.nanoTime();
    
    // unheap every tokens
    while(heap<tokens.size())
    {
        Token t = tokens.get(heap);
        char[] curTermBuffer = t.curTermBuffer;
        int length = t.length;
        int tokStart = t.tokStart;
        int tokEnd = t.tokEnd;
        String type = t.type;
        boolean subword = t.subword;
        BytesRef payload = t.payload;
        int intPayload = t.intPayload;
        boolean required = t.required;
        boolean ordered = t.ordered;
        boolean alone = t.alone;
        boolean atleastone = t.atleastone;
        boolean lastbypayload = t.payload==null?false:(lastPositionByPayload.get(t.intPayload)==t.tokStart);

        if (debugMode)
            Logger.getLogger("unsplit").log(Level.INFO, "Add {0}", new String(curTermBuffer,0,length));

        if (tokStart!=lastPosition)
        {
            if (!phrases.isEmpty())
            {
                lastphrases = (ArrayList<String[]>) phrases.clone();
                lastsizes = (ArrayList<Integer>) sizes.clone();
                lastoffsets = (ArrayList<Integer>) offsets.clone();
                lastnumWords = (ArrayList<Integer>) numWords.clone();
                lastnumSubWords = (ArrayList<Integer>) numSubWords.clone();
                lastNGramPayloads = (ArrayList<Boolean>) nGramPayloads.clone();
                lastTokenAlonePayloads = (ArrayList<HashSet<Integer>>) tokenAlonePayloads.clone();
                lastTokenAtLeastOnePayloads = (ArrayList<HashSet<Integer>>) tokenAtLeastOnePayloads.clone();
                lastScores = (ArrayList<Integer>) scores.clone();
                
                phrases.clear();
                sizes.clear();
                offsets.clear();
                numWords.clear();
                numSubWords.clear();
                nGramPayloads.clear();
                tokenAlonePayloads.clear();
                tokenAtLeastOnePayloads.clear();
                scores.clear();
            }
            
            lastPosition = tokStart;
            lastIntPayload = intPayload;
        }
        
        if (!this.frequentTermsOnly || (util.getMostFrequentHashTerms().get(new String(curTermBuffer,0,length))!=null))
        {
//            if (this.minWordsUnsplitted==0 && phrases.isEmpty()) // calculate currentMinWordUnsplitted for all-token-in-one-only-mode
//                currentMinWordUnsplitted++;
            
            if (intPayload!=lastIntPayload && ordered)
            {
                for(int i=0;i<lastNGramPayloads.size();i++)
                    lastNGramPayloads.set(i, false);
                lastIntPayload = intPayload;
            }
            
            fillPhrases(curTermBuffer,length,tokEnd-tokStart+1,subword,required,ordered,alone,intPayload,atleastone,lastbypayload);
        }
        
        heap++;
        if (keepOriginals)
        {
            clearAttributes();
            offsetAtt.setOffset(0,tokEnd-tokStart+1);
            if (firstPositionIncrement++==0)
                posIncrAtt.setPositionIncrement(1);
            else
                posIncrAtt.setPositionIncrement(0);
            posLenAtt.setPositionLength(length);
            termAtt.copyBuffer(curTermBuffer, 0, length);
            payloadAtt.setPayload(payload);
            typeAtt.setType(type);
            
            if (debugMode)
                Logger.getLogger("unsplit").log(Level.INFO, "Span {0}", new String(curTermBuffer,0,length));
            
            return true;
        }
    }

    if (debugTime && endUnHeapTokens==0) endUnHeapTokens = System.nanoTime();
    
    if (phrases.isEmpty())
    {
        phrases = lastphrases;
        sizes = lastsizes;
        offsets = lastoffsets;
        numWords = lastnumWords;
        numSubWords = lastnumSubWords;
        nGramPayloads = lastNGramPayloads;
        tokenAlonePayloads = lastTokenAlonePayloads;
        tokenAtLeastOnePayloads = lastTokenAtLeastOnePayloads;
        scores = lastScores;
    }
    
    // return the generated phrases one after an other
    while (current<phrases.size())
    {
        if (numWords.get(current)>=minWordsUnsplitted)
        {
            if (!span_score_payload || (int)((((float)scores.get(current))*score_maximum)/totalScore)>=minimum_score)
            {
                String unsplitted = unsplit(phrases.get(current));
                double indice;
                if (frequentTermsLimit>0 && (indice = getFrequentIndice(phrases.get(current)))>0)
                {
                    Integer limit = frequentPhrases.get(indice);
                    if (limit==null || limit<frequentTermsLimit)
                    {
                        frequentPhrases.put(indice,limit==null?1:limit+1);
                        write(current++,unsplitted);
                        return true;
                    }
                }
                else
                {
                    write(current++,unsplitted);
                    return true;
                }
            }
        }
        current++;
    }
    
    if (debugTime)
    {
        long endTime = System.nanoTime();
        System.out.println("UnsplitFilter totalTime : "+(((float)endTime-startTime)/1000000)+" ms");
        System.out.println("  UnsplitFilter computeTokens : "+(((float)endComputeTokens-startTime)/1000000)+" ms");
        System.out.println("  UnsplitFilter unheapTokens : "+(((float)endUnHeapTokens-endComputeTokens)/1000000)+" ms");
        System.out.println("  UnsplitFilter return : "+(((float)endTime-endUnHeapTokens)/1000000)+" ms");
        System.out.println("    UnsplitFilter clearAttributes : "+(((float)clearAttributesTime-endUnHeapTokens)/1000000)+" ms");
        System.out.println("    UnsplitFilter returnTrue : "+(((float)returnTrueTime-clearAttributesTime)/1000000)+" ms");
    }
    return false;
  }
  
  public void write(int current,String unsplitted)
  {
      char[] curTermBuffer = unsplitted.toCharArray();
        int size = sizes.get(current);
        int offset = offsets.get(current);
        
        if (debugTime)
            clearAttributesTime += System.nanoTime();
        
        clearAttributes();
        
        offsetAtt.setOffset(0,offset);
        if (firstPositionIncrement++==0)
            posIncrAtt.setPositionIncrement(1);
        else
            posIncrAtt.setPositionIncrement(0);
        posLenAtt.setPositionLength(size);
        
        termAtt.copyBuffer(curTermBuffer, 0, size);
        
        if (span_score_payload)
        {
            payloadAtt.setPayload(new BytesRef(PayloadHelper.encodeInt((int)((((float)scores.get(current))*score_maximum)/totalScore))));
        }
        else if (count_payload)
        {
            payloadAtt.setPayload(new BytesRef(PayloadHelper.encodeInt(phrases.get(current).length)));
        }
        else if (spanPayload!=DEFAULT_SPANPAYLOAD)
            payloadAtt.setPayload(spanPayload);
        else
            payloadAtt.setPayload(DEFAULT_SPANPAYLOAD);
        
        typeAtt.setType(UNSPLIT_TYPE);
        
        if (debugMode)
        {
            if (span_score_payload)
                Logger.getLogger("unsplit").log(Level.INFO, "Span {0} : {1}", new Object[]{new String(curTermBuffer,0,size),(int)((((float)scores.get(current))*score_maximum)/totalScore)});
            else
                Logger.getLogger("unsplit").log(Level.INFO, "Span {0}",new String(curTermBuffer,0,size));
           
        }
        
        current++;
        
        if (debugTime)
            returnTrueTime += System.nanoTime();
  }
  
  public class StringComparator implements Comparator<String>
  {
      public StringComparator()
      {
          
      }

        @Override
        public int compare(String o1, String o2) {
            if (o1==null) return 0;
            if (o2==null) return -1;
            return o1.compareTo(o2);
        }
  }
  
  StringComparator comparator = new StringComparator();
  
  // unsplit and order
  public String unsplit(String[] splitted)
  {
      boolean first = true;
      Arrays.sort(splitted, comparator);
      StringBuilder builder = new StringBuilder();
      for(String s : splitted)
      {
          if (s!=null && s.length()>0)
          {
            if (delimiter!=0)
            {
                  if (!first) builder.append(delimiter);
                  else first = false;
              }
              builder.append(s);
          }
      }
      return builder.toString();
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    
    heap = 0;
    current = 0;
    lastPosition = -1;
    lastPositionLength = -1;
    firstPositionIncrement = 0;
    currentMinWordUnsplitted = this.minWordsUnsplitted;
    
    tokens.clear();
    phrases.clear();
    sizes.clear();
    offsets.clear();
    numWords.clear();
    numSubWords.clear();
    nGramPayloads.clear();
    tokenAlonePayloads.clear();
    tokenAtLeastOnePayloads.clear();
    scores.clear();
    
    lastphrases.clear();
    lastsizes.clear();
    lastoffsets.clear();
    lastnumWords.clear();
    lastnumSubWords.clear();
    lastNGramPayloads.clear();
    lastTokenAlonePayloads.clear();
    lastTokenAtLeastOnePayloads.clear();
    lastScores.clear();
    
    startTime=endComputeTokens=endUnHeapTokens=clearAttributesTime=returnTrueTime=0;
    
    totalScore = 0;
    
    lastPositionByPayload.clear();
  }
}