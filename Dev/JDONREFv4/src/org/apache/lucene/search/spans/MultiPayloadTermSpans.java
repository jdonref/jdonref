package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

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
    protected boolean checked = true;
    
    protected Bits acceptDocs;
    protected AtomicReader reader;
    
    byte[] currentPayload;
    int currentCountByPayload;
    
    int order = -1;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    public byte[] getCurrentPayload()
    {
        return currentPayload;
    }
    
    public int getCurrentCountByPayload()
    {
        return currentCountByPayload;
    }
    
    public boolean nextPayload() throws IOException
    {
        if (count++ < freq)
        {
            position = postings.nextPosition();
            BytesRef payload = postings.getPayload();
            if (payload != null)
            {
                currentPayload = convertPayload(extractPayload(payload));
                currentCountByPayload = extractCountTermByPayload(payload);
            }
            else
            {
                currentPayload = null;
                currentCountByPayload = -1;
            }
            return true;
        }
        return false;
    }
    
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

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public boolean skipTo(int target) {
            return false;
        }

        @Override
        public int doc() {
            return DocIdSetIterator.NO_MORE_DOCS;
        }

        @Override
        public int start() {
            return 0;
        }

        @Override
        public int end() {
            return 0;
        }

        @Override
        public Collection<byte[]> getPayload() {
            return null;
        }

        @Override
        public boolean isPayloadAvailable() {
            return false;
        }

        @Override
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
    
    public MultiPayloadTermSpans(DocsAndPositionsEnum postings, Term term, int termCountPayloadFactor, AtomicReader reader, Bits acceptDocs) {
        this(postings, term, termCountPayloadFactor);
        this.reader = reader;
        this.acceptDocs = acceptDocs;
    }
    
    public Bits acceptDocs()
    {
        return acceptDocs;
    }
    
    protected Document document;
    protected int documentDoc;
    
    public Document document() throws IOException
    {
        if (document==null || doc!=documentDoc)
        {
            document = reader==null?null:reader.document(doc);
            documentDoc = doc;
        }
        return document;
    }
    
    @Override
    public boolean next() throws IOException {
        doc = postings.nextDoc();
        if (doc == DocIdSetIterator.NO_MORE_DOCS) {
            return false;
        }
        
        freq = postings.freq();
        count = 0;
        //readPayloads();
        //resetPayloads();
        nextPayload();
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
        //readPayloads();
        //resetPayloads();
        nextPayload();
        readPayload = true;
        return true;
    }
    
//    protected void readPayloads() throws IOException {
//        payloads.clear();
//        termCountsByPayload.clear();
//        while (count++ < freq)
//        {
//            position = postings.nextPosition();
//            BytesRef payload = postings.getPayload();
//            
//            if (payload!=null) // TODO : understand how it can be null
//            {
//                byte[] newpayload = convertPayload(extractPayload(payload));
//                payloads.add(newpayload);
//                int totalCount = extractCountTermByPayload(payload);
//                termCountsByPayload.add(totalCount);
//            }
//        } 
//    }
    
    protected BytesRef extractPayload(BytesRef payload)
    {
        if (termCountPayloadFactor!=NOTERMCOUNTPAYLOADFACTOR)
        {
            int payloadStuff = PayloadHelper.decodeInt(payload.bytes,payload.offset);
            //int payloadValue     = payload/termCountPayloadFactor;
            int payloadValue = payloadStuff%termCountPayloadFactor;
            
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
            int payloadTermCount = payloadStuff/termCountPayloadFactor;
            
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
}