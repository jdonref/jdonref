package org.apache.lucene.analysis;

import com.sun.org.apache.xml.internal.utils.StringComparable;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
  public static final String DEFAULT_PATH = "/usr/share/elasticsearch/plugins/jdonrefv4-0.2/word84.txt";
  public static final boolean DEFAULT_KEEPORIGINALS = true;
  public static final int DEFAULT_MINWORDSUNSPLITTED = 2;
  public static final boolean DEFAULT_FREQUENTTERMSONLY = true;
  public static final int DEFAULT_MAXSUBWORDS = 1;
  public static final Set<String> DEFAULT_SUBWORDSTYPES = new HashSet<>(Arrays.asList(new String[]{"NGRAM"}));
  public static final char DEFAULT_DELIMITER = 0;
  public static final BytesRef DEFAULT_SPANPAYLOAD = null;
  
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
    
    lastphrases = new ArrayList<>();
    lastsizes = new ArrayList<>();
    lastoffsets = new ArrayList<>();
    lastnumWords = new ArrayList<>();
    lastnumSubWords = new ArrayList<>();
  }
  
  protected int current;
  protected ArrayList<String[]> lastphrases;
  protected ArrayList<Integer> lastsizes;
  protected ArrayList<Integer> lastoffsets;
  protected ArrayList<Integer> lastnumWords;
  protected ArrayList<Integer> lastnumSubWords;
  
  protected ArrayList<String[]> phrases;
  protected ArrayList<Integer> sizes;
  protected ArrayList<Integer> offsets;
  protected ArrayList<Integer> numWords;
  protected ArrayList<Integer> numSubWords;
  
  protected String path = DEFAULT_PATH;
  protected boolean keepOriginals = DEFAULT_KEEPORIGINALS;
  protected boolean frequentTermsOnly = DEFAULT_FREQUENTTERMSONLY;
  protected int minWordsUnsplitted = DEFAULT_MINWORDSUNSPLITTED; // 0 means automatically take the maximum number.
  protected int maxSubwords = DEFAULT_MAXSUBWORDS;
  protected char delimiter = DEFAULT_DELIMITER;
  protected BytesRef spanPayload = DEFAULT_SPANPAYLOAD;
  protected Set<String> subwordsTypes = DEFAULT_SUBWORDSTYPES;
  
  UnsplitFilterFrequentTermsUtil util;

  public UnsplitFilter(TokenStream input, String path, boolean keepOriginals, boolean frequentTermsOnly, int minWordsUnsplitted, int maxSubwords, char delimiter, BytesRef spanPayload,Set<String> subwordsTypes)
  {
      this(input);
      
      this.path = path;
      this.keepOriginals = keepOriginals;
      this.frequentTermsOnly = frequentTermsOnly;
      this.minWordsUnsplitted = minWordsUnsplitted;
      this.maxSubwords = maxSubwords;
      this.delimiter = delimiter;
      this.spanPayload = spanPayload;
      this.subwordsTypes = subwordsTypes;
      currentMinWordUnsplitted = this.minWordsUnsplitted;
      
      if (frequentTermsOnly)
      {
          util = new UnsplitFilterFrequentTermsUtil();
          util.setFilePath(path);
          util.getMostFrequentHashTerms();
      }
  }
  
  // append it
  protected char[] makeNewPhrase(char[] current,int current_size,char[] phrase_i,int phrase_i_size)
  {
      char[] new_phrase_i = Arrays.copyOf(phrase_i, phrase_i_size+(delimiter!=DEFAULT_DELIMITER?1:0)+current_size);
      
      if (delimiter!=DEFAULT_DELIMITER)
        new_phrase_i[phrase_i_size] = delimiter;
      
      System.arraycopy(current, 0, new_phrase_i, phrase_i_size+(delimiter!=DEFAULT_DELIMITER?1:0), current_size);
      
      return new_phrase_i;
  }
  
  protected void fillPhrases(char[] current,int size,int offset,boolean subword)
  {
      if (lastphrases.isEmpty())
      {
          // only one empty one
          if (phrases.isEmpty() && this.minWordsUnsplitted>0)
          {
            //phrases.add(new char[]{});
            phrases.add(new String[]{""});
            sizes.add(0);
            offsets.add(offset);
            numWords.add(0);
            numSubWords.add(0);
          }
          
          phrases.add(new String[]{new String(current,0,size)});
          sizes.add(size);
          offsets.add(offset);
          numWords.add(1);
          numSubWords.add(subword?1:0);
      }
      else
      {
        if (phrases.isEmpty() && this.minWordsUnsplitted>0)
            for(int i=0;i<lastphrases.size();i++)
            {
                // empty one
                phrases.add(lastphrases.get(i));
                sizes.add(lastsizes.get(i));
                offsets.add(lastoffsets.get(i));
                numWords.add(lastnumWords.get(i));
                numSubWords.add(lastnumSubWords.get(i));
            }
          
        for(int i=0;i<lastphrases.size();i++)
        {
            if (!(subword && lastnumSubWords.get(i)>=maxSubwords))
            {
                String[] new_phrase_i;
                if (lastsizes.get(i)>0)
                {
                    //new_phrase_i = makeNewPhrase(current,size,lastphrases.get(i),lastsizes.get(i));
                    new_phrase_i = Arrays.copyOf(lastphrases.get(i), lastphrases.get(i).length+1);
                    new_phrase_i[new_phrase_i.length-1] = new String(current,0,size);
                }
                else
                    new_phrase_i = new String[]{new String(current,0,size)};

                //Logger.getLogger("unsplit").info("Span "+new String(new_phrase_i,0,lastsizes.get(i)+(delimiter!=DEFAULT_DELIMITER?1:0)+size));

                phrases.add(new_phrase_i);
                sizes.add(lastsizes.get(i)+(delimiter!=DEFAULT_DELIMITER?1:0)+size);
                offsets.add(lastoffsets.get(i)+(delimiter!=DEFAULT_DELIMITER?1:0)+offset);
                numWords.add(lastnumWords.get(i)+1);
                numSubWords.add(lastnumSubWords.get(i)+(subword?1:0));
            }
        }
      }
  }

  int lastPosition = -1;
  
  int lastPositionLength = -1;
  
  int firstPositionIncrement = 0;
  
  int currentMinWordUnsplitted;
  
  @Override
  public final boolean incrementToken() throws IOException
  {
    // compute everytokens
    while(input.incrementToken())
    {
        char[] curTermBuffer = termAtt.buffer().clone();
        int length = termAtt.length();
        int tokStart = offsetAtt.startOffset();
        int tokEnd = offsetAtt.endOffset();
        String type = typeAtt.type();
        boolean subword = subwordsTypes.contains(type);

        //Logger.getLogger("unsplit").info("Add "+new String(curTermBuffer,0,length));

        if (tokStart!=lastPosition)
        {
            if (!phrases.isEmpty())
            {
                lastphrases = (ArrayList<String[]>) phrases.clone();
                lastsizes = (ArrayList<Integer>) sizes.clone();
                lastoffsets = (ArrayList<Integer>) offsets.clone();
                lastnumWords = (ArrayList<Integer>) numWords.clone();
                lastnumSubWords = (ArrayList<Integer>) numSubWords.clone();
                
                phrases.clear();
                sizes.clear();
                offsets.clear();
                numWords.clear();
                numSubWords.clear();
            }
            
            lastPosition = tokStart;
        }
        
        if (!this.frequentTermsOnly || (util.getMostFrequentHashTerms().get(new String(curTermBuffer,0,length))!=null))
        {
            if (this.minWordsUnsplitted==0 && phrases.isEmpty())
                currentMinWordUnsplitted++;
            
            fillPhrases(curTermBuffer,length,tokEnd-tokStart+1,subword);
        }
        
        if (keepOriginals)
            return true;
    }

    if (phrases.isEmpty())
    {
        phrases = lastphrases;
        sizes = lastsizes;
        offsets = lastoffsets;
        numWords = lastnumWords;
        numSubWords = lastnumSubWords;
    }
    
    lastphrases = null; // for gc, it might be long !
    lastsizes = null;
    lastoffsets = null;
    lastnumWords = null;
    lastnumSubWords = null;
    
    // return the generated phrases one after an other
    if (current<phrases.size())
    {
        // minwords option
        while(numWords.get(current)<currentMinWordUnsplitted)
        {
            current++;
            if (current>=phrases.size()) return false;
        }
        
        //char[] curTermBuffer = phrases.get(current);
        char[] curTermBuffer = unsplit(phrases.get(current)).toCharArray();
        int size = sizes.get(current);
        int offset = offsets.get(current);
        
        clearAttributes();
        
        offsetAtt.setOffset(0,offset);
        if (firstPositionIncrement++==0)
            posIncrAtt.setPositionIncrement(1);
        else
            posIncrAtt.setPositionIncrement(0);
        posLenAtt.setPositionLength(size);
        
        termAtt.copyBuffer(curTermBuffer, 0, size);
        
        if (spanPayload!=DEFAULT_SPANPAYLOAD)
            payloadAtt.setPayload(spanPayload);
        
        typeAtt.setType(UNSPLIT_TYPE);
        
        //Logger.getLogger("unsplit").log(Level.INFO, "Span {0}", new String(curTermBuffer,0,size));
        
        current++;
        return true;
    }
    else
    {
        return false;
    }
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
      Arrays.sort(splitted, comparator);
      StringBuilder builder = new StringBuilder();
      for(String s : splitted)
          builder.append(s);
      return builder.toString();
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    
    current = 0;
    lastPosition = -1;
    lastPositionLength = -1;
    firstPositionIncrement = 0;
    currentMinWordUnsplitted = this.minWordsUnsplitted;
    
    phrases = new ArrayList<>();
    sizes = new ArrayList<>();
    offsets = new ArrayList<>();
    numWords = new ArrayList<>();
    numSubWords = new ArrayList<>();
    
    lastphrases = new ArrayList<>();
    lastsizes = new ArrayList<>();
    lastoffsets = new ArrayList<>();
    lastnumWords = new ArrayList<>();
    lastnumSubWords = new ArrayList<>();
  }
}
