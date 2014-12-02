package org.apache.lucene.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
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
public class TokenCountPayloadsFilter extends TokenFilter {

    public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    protected Version version;
    protected int termCountPayloadFactor = TokenCountPayloadsFilterFactory.NOTERMCOUNTPAYLOADFACTOR;
    protected final CharacterUtils charUtils;
    
    IntegerEncoder encoder = new IntegerEncoder();
    
    protected ConcurrentHashMap<BytesRef,Integer> payloadsCounts = new ConcurrentHashMap<>();
    protected ArrayList<Token> tokens = new ArrayList<>();
    
    protected boolean updateOffsets; // never if the length changed before this filter
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
    public TokenCountPayloadsFilter(TokenStream input, int termCountPayloadFactor, Version version) {
        super(input);

        this.version = version;
        this.termCountPayloadFactor = termCountPayloadFactor;
        this.charUtils = version.onOrAfter(Version.LUCENE_44)
                ? CharacterUtils.getInstance(version)
                : CharacterUtils.getJava4Instance();
    }

    public class Token
    {
        char[] curTermBuffer;
        int curTermLength;
        int curCodePointCount;
        int positionIncrement;
        int positionLength;
        int tokStart;
        int tokEnd;
        BytesRef payload;
        
        public Token(char[] curTermBuffer, int curTermLength, int curCodePointCount, int positionIncrement, int positionLength, int tokStart, int tokEnd, BytesRef payload)
        {
            this.curTermBuffer = curTermBuffer;
            this.curTermLength = curTermLength;
            this.curCodePointCount = curCodePointCount;
            this.positionIncrement = positionIncrement;
            this.positionLength = positionLength;
            this.tokStart = tokStart;
            this.tokEnd = tokEnd;
            this.payload = payload;
        }
    }
    
    public void addToken(char[] curTermBuffer, int curTermLength, int curCodePointCount, int positionIncrement, int positionLength, int tokStart, int tokEnd, BytesRef payload)
    {
        if (payload!=null)
        {
            Token t = new Token(curTermBuffer, curTermLength, curCodePointCount, positionIncrement, positionLength, tokStart, tokEnd, payload);
        
            Integer count = payloadsCounts.get(payload);
            if (count==null)
                payloadsCounts.put(payload,1);
            else
                payloadsCounts.put(payload,count+1);
        
            tokens.add(t);
        }
    }
    
    public BytesRef getNewPayload(BytesRef payload,int count)
    {
        int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
        int newPayloadStuff = termCountPayloadFactor*count + payloadStuff;
        
        BytesRef bytes = encoder.encode(Integer.toString(newPayloadStuff).toCharArray());
        return bytes;
    }
    
    public void writeToken(Token t)
    {
        int count = payloadsCounts.get(t.payload);
        assert(count>0);
        
        BytesRef newPayload = getNewPayload(t.payload,count);
        
        clearAttributes();
        offsetAtt.setOffset(t.tokStart, t.tokEnd);
        posIncrAtt.setPositionIncrement(t.positionIncrement);
        posLenAtt.setPositionLength(t.positionLength);
        termAtt.copyBuffer(t.curTermBuffer, 0, t.curTermLength);
        payloadAtt.setPayload(newPayload);
    }
    
    int current = 0;
    
    @Override
    public final boolean incrementToken() throws IOException {
        
        // get all payloads & counts
        if (input.incrementToken())
        {
            current = 0;
            do
            {
                char[] curTermBuffer = termAtt.buffer().clone();
                int curTermLength = termAtt.length();
                int curCodePointCount = charUtils.codePointCount(termAtt);
                int positionIncrement = posIncrAtt.getPositionIncrement();
                int positionLength = posLenAtt.getPositionLength();
                int tokStart = offsetAtt.startOffset();
                int tokEnd = offsetAtt.endOffset();
                BytesRef payload = payloadAtt.getPayload();
                
                addToken(curTermBuffer,curTermLength,curCodePointCount,positionIncrement,positionLength,tokStart,tokEnd,payload);
            } while(input.incrementToken());
        }
        
        while (current<tokens.size()) {
            writeToken(tokens.get(current++));
            return true;
        }
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        payloadsCounts.clear();
        tokens.clear();
    }
}
