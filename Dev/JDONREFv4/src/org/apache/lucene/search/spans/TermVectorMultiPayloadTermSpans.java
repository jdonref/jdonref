package org.apache.lucene.search.spans;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DocsAndPositionsEnum;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;

/**
 * TermSpans that returns all the available payload for each match.
 * 
 * start return the last position. TODO: return all the positions available
 * 
 * @author Julien
 */
public class TermVectorMultiPayloadTermSpans extends MultiPayloadTermSpans
{
    AtomicReader reader;
    Bits acceptDocs;
    
    public TermVectorMultiPayloadTermSpans(DocsAndPositionsEnum postings, Term term, AtomicReader reader, Bits acceptDocs) {
        super(postings, term);
        this.reader = reader;
        this.acceptDocs = acceptDocs;
    }
    
    public TermVectorMultiPayloadTermSpans(DocsAndPositionsEnum postings, Term term, AtomicReader reader, Bits acceptDocs, int termCountPayloadFactor) {
        this(postings, term, reader, acceptDocs);
        this.termCountPayloadFactor = termCountPayloadFactor;
    }
    
    public TermVectorMultiPayloadTermSpans() {
    }
    
    public Bits acceptDocs()
    {
        return acceptDocs;
    }
    
    public Document document() throws IOException
    {
        return reader.document(doc);
    }
    
    public Fields termVector() throws IOException
    {
        return reader.getTermVectors(doc);
    }
    
    public static TermVectorMultiPayloadTermSpans emptyTermSpans()
    {
        return new EmptyTermSpans();
    }
    
    private static final class EmptyTermSpans extends TermVectorMultiPayloadTermSpans {

        private EmptyTermSpans() {
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
}