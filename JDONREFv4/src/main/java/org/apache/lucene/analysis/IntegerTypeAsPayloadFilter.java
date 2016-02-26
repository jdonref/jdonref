package org.apache.lucene.analysis;

import java.io.IOException;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * Makes payload a given integer for one type.
 * 
 * @author Julien
 */
public class IntegerTypeAsPayloadFilter extends TokenFilter
{
    public static final int NOTINTEGERTYPEASPAYLOADFACTOR = -1;
    protected int integerTypeAsPayloadFactor = IntegerTypeAsPayloadFilter.NOTINTEGERTYPEASPAYLOADFACTOR;
    
    public static final int DEFAUTDEFAULTINTEGERTYPE = 2;
    public static final int DEFAUTINTEGERTYPE = 1;
    public static final String DEFAUTTYPE = "word";
    
    protected int defaultIntegerType = DEFAUTDEFAULTINTEGERTYPE;
    protected int integerType = DEFAUTINTEGERTYPE;
    protected String tokentype = DEFAUTTYPE;
    
    protected final CharacterUtils charUtils;
    
    IntegerEncoder encoder = new IntegerEncoder();
    
    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    protected final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    protected final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
    protected final PayloadAttribute payloadAtt = addAttribute(PayloadAttribute.class);
    protected final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    
    /**
     * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range
     * and may be keep payloads
     * 
     * @param input {@link TokenStream} holding the input to be tokenized
     */
    public IntegerTypeAsPayloadFilter(TokenStream input, String tokentype, int integerType, int defaultIntegerType, int integerTypeAsPayloadFactor,Version version) {
        super(input);

        this.tokentype = tokentype;
        this.integerType = integerType;
        this.defaultIntegerType = defaultIntegerType;
        this.integerTypeAsPayloadFactor = integerTypeAsPayloadFactor;
        
        this.charUtils = CharacterUtils.getInstance(version);
    }
    
    public BytesRef getNewPayload(BytesRef payload,int count)
    {
        int payloadStuff;
        if (payload!=null)
            payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
        else
            payloadStuff = 0;
        int newPayloadStuff = integerTypeAsPayloadFactor*count + payloadStuff;
        
        BytesRef bytes = encoder.encode(Integer.toString(newPayloadStuff).toCharArray());
        return bytes;
    }
    
    int current = 0;
    
    @Override
    public final boolean incrementToken() throws IOException
    {        
        if (input.incrementToken())
        {
            BytesRef newPayload;
            BytesRef payload = payloadAtt.getPayload();
            String currentType = typeAtt.type();
            
            if (currentType!=null && currentType.equals(tokentype))
                newPayload = getNewPayload(payload,this.integerType);
            else
                newPayload = getNewPayload(payload,this.defaultIntegerType);
            
            payloadAtt.setPayload(newPayload);
            return true;
        }
        
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }
}
