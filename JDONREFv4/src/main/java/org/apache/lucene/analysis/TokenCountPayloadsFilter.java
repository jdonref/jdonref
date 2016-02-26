package org.apache.lucene.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * Makes payload the number of tokens with a given payload.
 * 
 * @author Julien
 */
public class TokenCountPayloadsFilter extends TokenFilter {

    public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    public static final Set<String> DEFAULT_IGNOREDTYPES = new HashSet<>();
    
    protected int termCountPayloadFactor = NOTERMCOUNTPAYLOADFACTOR;
    protected Set<String> ignoredTypes = DEFAULT_IGNOREDTYPES;
    
    protected Version version;
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
    protected final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    /**
     * Creates TokenCountPayloadsFilter payload the number of tokens with a given payload.
     * 
     * @param version the <a href="#version">Lucene match version</a>
     * @param input {@link TokenStream} holding the input to be tokenized
     */
    public TokenCountPayloadsFilter(TokenStream input, Set<String> ignoredTypes, int termCountPayloadFactor, Version version) {
        super(input);

        this.version = version;
        this.termCountPayloadFactor = termCountPayloadFactor;
        this.ignoredTypes = ignoredTypes;
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
        String type;
        BytesRef payload;
        
        public Token(char[] curTermBuffer, int curTermLength, int curCodePointCount, int positionIncrement, int positionLength, int tokStart, int tokEnd, BytesRef payload, String type)
        {
            this.curTermBuffer = curTermBuffer;
            this.curTermLength = curTermLength;
            this.curCodePointCount = curCodePointCount;
            this.positionIncrement = positionIncrement;
            this.positionLength = positionLength;
            this.tokStart = tokStart;
            this.tokEnd = tokEnd;
            this.payload = payload;
            this.type = type;
        }
    }
    
    public void addToken(char[] curTermBuffer, int curTermLength, int curCodePointCount, int positionIncrement, int positionLength, int tokStart, int tokEnd, BytesRef payload, String type)
    {
        Token t = new Token(curTermBuffer, curTermLength, curCodePointCount, positionIncrement, positionLength, tokStart, tokEnd, payload, type);
        if (payload!=null)
        {
            if (!this.ignoredTypes.contains(type))
            {
                Integer count = payloadsCounts.get(payload);
                if (count==null)
                    payloadsCounts.put(payload,1);
                else
                    payloadsCounts.put(payload,count+1);
            }
        }
        tokens.add(t);
    }
    
    public BytesRef getNewPayload(BytesRef payload,int count)
    {
        int payloadStuff;
        if (payload!=null)
            payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
        else
            payloadStuff = 0;
        int newPayloadStuff = termCountPayloadFactor*count + payloadStuff;
        
        BytesRef bytes = encoder.encode(Integer.toString(newPayloadStuff).toCharArray());
        return bytes;
    }
    
    public void writeToken(Token t)
    {
        BytesRef newPayload = null;
        if (this.ignoredTypes.contains(t.type))
        {
            newPayload = t.payload;
            if (newPayload == null)
                newPayload = encoder.encode(Integer.toString(0).toCharArray());
        }
        else
        {
            int count = payloadsCounts.get(t.payload);
            newPayload = getNewPayload(t.payload,count);
        }
        
        clearAttributes();
        offsetAtt.setOffset(t.tokStart, t.tokEnd);
        posIncrAtt.setPositionIncrement(t.positionIncrement);
        posLenAtt.setPositionLength(t.positionLength);
        termAtt.copyBuffer(t.curTermBuffer, 0, t.curTermLength);
        typeAtt.setType(t.type);
        if (newPayload!=null)
            payloadAtt.setPayload(newPayload);
        //assert(newPayload.equals(payloadAtt.getPayload()));
        
    }
    
    int current = 0;
    
    @Override
    public final boolean incrementToken() throws IOException {
        
        // get all payloads & counts
        while (current==0 && input.incrementToken())
        {
            char[] curTermBuffer = termAtt.buffer().clone();
            int curTermLength = termAtt.length();
            int curCodePointCount = charUtils.codePointCount(termAtt);
            int positionIncrement = posIncrAtt.getPositionIncrement();
            int positionLength = posLenAtt.getPositionLength();
            int tokStart = offsetAtt.startOffset();
            int tokEnd = offsetAtt.endOffset();
            String type = typeAtt.type();
            BytesRef payload = payloadAtt.getPayload();

            addToken(curTermBuffer,curTermLength,curCodePointCount,positionIncrement,positionLength,tokStart,tokEnd,payload,type);
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
        current=0;
        payloadsCounts.clear();
        tokens.clear();
    }
}
