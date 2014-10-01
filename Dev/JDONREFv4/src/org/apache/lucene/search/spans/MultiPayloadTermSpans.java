package org.apache.lucene.search.spans;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;

/**
 * TermSpans that returns all the available payload for each match.
 * 
 * start return the last position. TODO: return all the positions available
 * 
 * @author Julien
 */
public class MultiPayloadTermSpans extends TermSpans
{
    public static int NOTERMCOUNTPAYLOADFACTOR = -1;
    public static int NOTERMCOUNTBYPAYLOAD = -1;
    
    public static IntegerEncoder encoder = new IntegerEncoder();
    
    protected int termCountPayloadFactor = MultiPayloadTermSpans.NOTERMCOUNTPAYLOADFACTOR;
  
    Collection<byte[]> payloads = new ArrayList<>();
    Collection<Integer> termCountsByPayload = new ArrayList<>();
    
    public int termCountPayloadFactor()
    {
      return termCountPayloadFactor();
    }
  
    public void setTermCountPayloadFactor(int factor)
    {
      this.termCountPayloadFactor = factor;
    }
    
    public static MultiPayloadTermSpans emptyTermSpans()
    {
        return new EmptyTermSpans();
    }
    
    protected static final class EmptyTermSpans extends MultiPayloadTermSpans {

        public EmptyTermSpans() {
        }

        public boolean next() {
            return false;
        }

        public boolean skipTo(int target) {
            return false;
        }

        public int doc() {
            return DocIdSetIterator.NO_MORE_DOCS;
        }

        public int start() {
            return 0;
        }

        public int end() {
            return 0;
        }

        public Collection<byte[]> getPayload() {
            return null;
        }

        public boolean isPayloadAvailable() {
            return false;
        }

        public long cost() {
            return 0;
        }
     
        public int termCountByPayload()
        {
            return NOTERMCOUNTBYPAYLOAD;
        }
    }
    
    public MultiPayloadTermSpans() {
    }
    
    public MultiPayloadTermSpans(DocsAndPositionsEnum postings, Term term) {
        super(postings, term);
    }
    
    public MultiPayloadTermSpans(DocsAndPositionsEnum postings, Term term,int termCountPayloadFactor) {
        super(postings, term);
        this.termCountPayloadFactor = termCountPayloadFactor;
    }
    
    @Override
    public boolean next() throws IOException {
        doc = postings.nextDoc();
        if (doc == DocIdSetIterator.NO_MORE_DOCS) {
            return false;
        }
        
        freq = postings.freq();
        count = 0;
        position = postings.nextPosition();
        count++;
        readPayloads();
        readPayload = true;
        return true;
    }
    
    @Override
    public boolean skipTo(int target) throws IOException {
        assert target > doc;
        doc = postings.advance(target);
        if (doc == DocIdSetIterator.NO_MORE_DOCS) {
            return false;
        }

        freq = postings.freq();
        count = 0;
        position = postings.nextPosition();
        count++;
        readPayloads();
        readPayload = true;
        return true;
    }
    
    protected void readPayloads() throws IOException {
        payloads.clear();
        termCountsByPayload.clear();
        do {
            BytesRef payload = postings.getPayload();
            
            payloads.add(convertPayload(extractPayload(payload)));
            termCountsByPayload.add(extractCountTermByPayload(payload));
            
            position = postings.nextPosition();
        } while (count++ < freq);
    }
    
    protected BytesRef extractPayload(BytesRef payload)
    {
        if (termCountPayloadFactor!=NOTERMCOUNTPAYLOADFACTOR)
        {
            int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            //int payloadValue     = payload/termCountPayloadFactor;
            int payloadValue = payloadStuff/termCountPayloadFactor;
            
            return encoder.encode(Integer.toString(payloadValue).toCharArray());
        }
        else
            return payload;
    }
    
    protected int extractCountTermByPayload(BytesRef payload)
    {
        if (termCountPayloadFactor!=NOTERMCOUNTPAYLOADFACTOR)
        {
            int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            //int payloadValue     = payload/termCountPayloadFactor;
            int payloadTermCount = payloadStuff%termCountPayloadFactor;
            
            return payloadTermCount;
        }
        else
            return NOTERMCOUNTBYPAYLOAD;
    }
    
    protected byte[] convertPayload(BytesRef payload) {
        final byte[] bytes;
        if (payload != null) {
            bytes = new byte[payload.length];
            System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);
        } else {
            bytes = null;
        }
        return bytes;
    }
    
    @Override
    public Collection<byte[]> getPayload() throws IOException {
        return payloads;
    }
    
    /**
     * Get termCount corresponing to payloads from getPayload()
     * @return 
     */
    public Collection<Integer> termCountsByPayload()
    {
        return termCountsByPayload;
    }
}