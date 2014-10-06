package org.apache.lucene.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;

/**
 * Make EdgeNGramTokenFilter non final without private fields please !
 * Payloads support
 * 
 * @author Julien
 */
public class UnsplitFilter extends TokenFilter
{
  protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
  protected final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
  protected final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
  protected final PayloadAttribute payloadAtt = addAttribute(PayloadAttribute.class);

  /**
   * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range
   * and may be keep payloads
   * 
   * @param version the <a href="#version">Lucene match version</a>
   * @param input {@link TokenStream} holding the input to be tokenized
   * @param side the {@link Side} from which to chop off an n-gram
   * @param minGram the smallest n-gram to generate
   * @param maxGram the largest n-gram to generate
   * @param withPayloads true to keep payloads
   */
  public UnsplitFilter(TokenStream input) {
    super(input);
    
    phrases = new ArrayList<>();
    sizes = new ArrayList<>();
    offsets = new ArrayList<>();
  }
  
  protected int current;
  protected ArrayList<char[]> lastphrases;
  protected ArrayList<char[]> phrases;
  protected ArrayList<Integer> lastsizes;
  protected ArrayList<Integer> sizes;
  protected ArrayList<Integer> lastoffsets;
  protected ArrayList<Integer> offsets;
  
  protected char[] makeNewPhrase(char[] current,int current_size,char[] phrase_i,int phrase_i_size)
  {
      char[] new_phrase_i = Arrays.copyOf(phrase_i, phrase_i_size+1+current_size);
      
      new_phrase_i[phrase_i_size] = ' ';
      
      System.arraycopy(current, 0, new_phrase_i, phrase_i_size+1, current_size);
      
      return new_phrase_i;
  }
  
  protected void fillPhrases(char[] current,int size,int offset)
  {
      if (lastphrases.isEmpty())
      {
          phrases.add(current);
          sizes.add(size);
          offsets.add(offset);
      }
      else
      for(int i=0;i<lastphrases.size();i++)
      {
          char[] new_phrase_i = makeNewPhrase(current,size,lastphrases.get(i),lastsizes.get(i));
          
          //Logger.getLogger("unsplit").info("Génère "+new String(new_phrase_i,0,lastsizes.get(i)+1+size));
          
          phrases.add(new_phrase_i);
          sizes.add(lastsizes.get(i)+1+size);
          offsets.add(lastoffsets.get(i)+1+offset);
      }
  }
  
  @Override
  public final boolean incrementToken() throws IOException
  {
    if (input.incrementToken())
    {
        // compute everytokens
        int lastPosition = -1;
        do
        {
            char[] curTermBuffer = termAtt.buffer().clone();
            int length = termAtt.length();
            int tokStart = offsetAtt.startOffset();
            int tokEnd = offsetAtt.endOffset();
            
            //Logger.getLogger("unsplit").info("Ajoute "+new String(curTermBuffer,0,length));
            
            if (tokStart!=lastPosition)
            {
                lastphrases = (ArrayList<char[]>) phrases.clone();
                lastsizes = (ArrayList<Integer>) sizes.clone();
                lastoffsets = (ArrayList<Integer>) offsets.clone();
                phrases.clear();
                sizes.clear();
                offsets.clear();
                lastPosition = tokStart;
            }
            fillPhrases(curTermBuffer,length,tokEnd-tokStart+1);
            
        } while(input.incrementToken());
        
        current = 0;
        lastphrases = null; // for gc, it might be long !
        lastsizes = null;
        lastoffsets = null;
    }
    
    // return the generated phrases one after an other
    if (current<phrases.size())
    {
        char[] curTermBuffer = phrases.get(current);
        int size = sizes.get(current);
        int offset = offsets.get(current);
        
        clearAttributes();
        
        offsetAtt.setOffset(0,offset);
        if (current==0)
            posIncrAtt.setPositionIncrement(1);
        else
            posIncrAtt.setPositionIncrement(0);
        posLenAtt.setPositionLength(size);
        
        termAtt.copyBuffer(curTermBuffer, 0, size-1);
        
        //Logger.getLogger("unsplit").info("Indexe "+new String(curTermBuffer,0,size));
        
        current++;
        return true;
    }
    else
    {
        return false;
    }
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    phrases = new ArrayList<>();
    sizes = new ArrayList<>();
    offsets = new ArrayList<>();
  }
}
