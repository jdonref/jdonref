package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * Make EdgeNGramTokenFilter non final without private fields please !
 * Payloads support
 * 
 * @author Julien
 */
public class EdgeNGramWithPayloadsFilter extends TokenFilter
{
  public static final Side DEFAULT_SIDE = Side.FRONT;
  public static final int DEFAULT_MAX_GRAM_SIZE = 1;
  public static final int DEFAULT_MIN_GRAM_SIZE = 1;

  /** Specifies which side of the input the n-gram should be generated from */
  public static enum Side {

    /** Get the n-gram from the front of the input */
    FRONT {
      @Override
      public String getLabel() { return "front"; }
    },

    /** Get the n-gram from the end of the input */
    @Deprecated
    BACK  {
      @Override
      public String getLabel() { return "back"; }
    };

    public abstract String getLabel();

    // Get the appropriate Side from a string
    public static Side getSide(String sideName) {
      if (FRONT.getLabel().equals(sideName)) {
        return FRONT;
      }
      if (BACK.getLabel().equals(sideName)) {
        return BACK;
      }
      return null;
    }
  }

  protected final Version version;
  protected final CharacterUtils charUtils;
  protected final int minGram;
  protected final int maxGram;
  protected Side side;
  protected char[] curTermBuffer;
  protected int curTermLength;
  protected int curCodePointCount;
  protected int curGramSize;
  protected int tokStart;
  protected int tokEnd; // only used if the length changed before this filter
  protected boolean updateOffsets; // never if the length changed before this filter
  protected int savePosIncr;
  protected int savePosLen;
  protected BytesRef curPayload;
  
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
  @Deprecated
  public EdgeNGramWithPayloadsFilter(Version version, TokenStream input, Side side, int minGram, int maxGram, boolean withPayloads) {
    super(input);

    if (version == null) {
      throw new IllegalArgumentException("version must not be null");
    }

    if (version.onOrAfter(Version.LUCENE_44) && side == Side.BACK) {
      throw new IllegalArgumentException("Side.BACK is not supported anymore as of Lucene 4.4, use ReverseStringFilter up-front and afterward");
    }

    if (side == null) {
      throw new IllegalArgumentException("sideLabel must be either front or back");
    }

    if (minGram < 1) {
      throw new IllegalArgumentException("minGram must be greater than zero");
    }

    if (minGram > maxGram) {
      throw new IllegalArgumentException("minGram must not be greater than maxGram");
    }

    this.version = version;
    this.charUtils = version.onOrAfter(Version.LUCENE_44)
        ? CharacterUtils.getInstance(version)
        : CharacterUtils.getJava4Instance();
    this.minGram = minGram;
    this.maxGram = maxGram;
    this.side = side;
  }

  /**
   * Creates JDONREFv3EdgeNGramFilterWithPayload that can generate n-grams in the sizes of the given range
   * and may be keep payloads
   *
   * @param version the <a href="#version">Lucene match version</a>
   * @param input {@link TokenStream} holding the input to be tokenized
   * @param sideLabel the name of the {@link Side} from which to chop off an n-gram
   * @param minGram the smallest n-gram to generate
   * @param maxGram the largest n-gram to generate
   * @param withPayloads true to keep payloads
   */
  @Deprecated
  public EdgeNGramWithPayloadsFilter(Version version, TokenStream input, String sideLabel, int minGram, int maxGram, boolean withPayloads) {
    this(version, input, Side.getSide(sideLabel), minGram, maxGram,withPayloads);
  }

  /**
   * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range
   * and may be keep payloads
   *
   * @param version the <a href="#version">Lucene match version</a>
   * @param input {@link TokenStream} holding the input to be tokenized
   * @param minGram the smallest n-gram to generate
   * @param maxGram the largest n-gram to generate
   * @param withPayloads true to keep payloads
   */
  public EdgeNGramWithPayloadsFilter(Version version, TokenStream input, int minGram, int maxGram, boolean withPayloads) {
    this(version, input, Side.FRONT, minGram, maxGram,withPayloads);
  }

  @Override
  public final boolean incrementToken() throws IOException {
    while (true) {
      if (curTermBuffer == null) {
        if (!input.incrementToken()) {
          return false;
        } else {
          curTermBuffer = termAtt.buffer().clone();
          curTermLength = termAtt.length();
          curPayload = payloadAtt.getPayload();
          curCodePointCount = charUtils.codePointCount(termAtt);
          curGramSize = minGram;
          tokStart = offsetAtt.startOffset();
          tokEnd = offsetAtt.endOffset();
          if (version.onOrAfter(Version.LUCENE_44)) {
            // Never update offsets
            updateOffsets = false;
          } else {
            // if length by start + end offsets doesn't match the term text then assume
            // this is a synonym and don't adjust the offsets.
            updateOffsets = (tokStart + curTermLength) == tokEnd;
          }
          savePosIncr += posIncrAtt.getPositionIncrement();
          savePosLen = posLenAtt.getPositionLength();
        }
      }
      if (curGramSize <= maxGram) {         // if we have hit the end of our n-gram size range, quit
        if (curGramSize <= curCodePointCount) { // if the remaining input is too short, we can't generate any n-grams
          // grab gramSize chars from front or back
          final int start = side == Side.FRONT ? 0 : charUtils.offsetByCodePoints(curTermBuffer, 0, curTermLength, curTermLength, -curGramSize);
          final int end = charUtils.offsetByCodePoints(curTermBuffer, 0, curTermLength, start, curGramSize);
          clearAttributes();
          if (updateOffsets) {
            offsetAtt.setOffset(tokStart + start, tokStart + end);
          } else {
            offsetAtt.setOffset(tokStart, tokEnd);
          }
          // first ngram gets increment, others don't
          if (curGramSize == minGram) {
            posIncrAtt.setPositionIncrement(savePosIncr);
            savePosIncr = 0;
          } else {
            posIncrAtt.setPositionIncrement(0);
          }
          posLenAtt.setPositionLength(savePosLen);
          termAtt.copyBuffer(curTermBuffer, start, end - start);
          payloadAtt.setPayload(curPayload);
          
          curGramSize++;
          return true;
        }
      }
      curTermBuffer = null;
    }
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    curTermBuffer = null;
    savePosIncr = 0;
  }
}
