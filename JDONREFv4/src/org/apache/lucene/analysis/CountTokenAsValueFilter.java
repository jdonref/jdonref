package org.apache.lucene.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static org.apache.lucene.analysis.UnsplitFilter.DEFAULT_SPANPAYLOAD;
import static org.apache.lucene.analysis.UnsplitFilter.UNSPLIT_TYPE;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * @author moquetju
 */
public class CountTokenAsValueFilter extends TokenFilter
{
    public static final String CountTokenAsValue_TYPE = "COUNTVALUE";
    
    protected Version version;
    protected final CharacterUtils charUtils;
    
    IntegerEncoder encoder = new IntegerEncoder();
    
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
    public CountTokenAsValueFilter(TokenStream input, Version version) {
        super(input);

        this.version = version;
        this.charUtils = version.onOrAfter(Version.LUCENE_44)
                ? CharacterUtils.getInstance(version)
                : CharacterUtils.getJava4Instance();
    }
    
    public int getValue(BytesRef payload)
    {
        if (payload!=null)
        {
            int field = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            
            switch(field)
            {
                case 1: return 30; // numero
                case 2: return 30; // repetition
                case 3: return 10; // type_de_voie
                case 4: return 50; // libelle
                case 5: return 0; // code_postal
                case 6: return 50; // code_departement
                case 7: return 0; // code_arrondissement
                case 8: return 0; // code_insee
                case 9: return 0; // code_insee_commune
                case 10: return 50; // commune
                case 11: return 50; // ligne1
                case 12: return 10; // ligne4
                case 13: return 10; // ligne6
                case 14: return 10; // ligne7
                default : break;
            }
        }
        return 0;
    }
    
    public void writeToken(int value)
    {
        String value_as_string = Integer.toString(value);
        int size = value_as_string.length();
        char[] curTermBuffer = value_as_string.toCharArray();
        
        clearAttributes();
        
        offsetAtt.setOffset(0,size);
        posIncrAtt.setPositionIncrement(1);
        posLenAtt.setPositionLength(size);
        termAtt.copyBuffer(curTermBuffer, 0, size);
        typeAtt.setType(CountTokenAsValue_TYPE);
    }
    
    int current = 0;
    
    @Override
    public final boolean incrementToken() throws IOException
    {
        int value = 0;
        
        // get all payloads & counts
        while (input.incrementToken())
        {
            BytesRef payload = payloadAtt.getPayload();

            value += getValue(payload);
        }
        
        if (value==0) return false;
        
        writeToken(value);
        return true;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }
}
