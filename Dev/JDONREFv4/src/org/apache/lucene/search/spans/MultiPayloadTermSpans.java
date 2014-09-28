package org.apache.lucene.search.spans;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TermSpans that returns all the available payload for each match.
 * 
 * start return the last position. TODO: return all the positions available
 * 
 * @author Julien
 */
public class MultiPayloadTermSpans extends TermSpans
{
    Collection<byte[]> payloads = new ArrayList<>();
    
    
    
  public MultiPayloadTermSpans(DocsAndPositionsEnum postings, Term term) {
    super(postings,term);
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
  
  protected void readPayloads() throws IOException
  {
      payloads.clear();
      do
      {
          payloads.add(convertPayload(postings.getPayload()));
          position = postings.nextPosition();
      } while(count++<freq);
  }
  
  protected byte[] convertPayload(BytesRef payload)
  {
      final byte[] bytes;
      if (payload != null)
      {
          bytes = new byte[payload.length];
          System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);
      }
      else
      {
          bytes = null;
      }
      return bytes;
  }
  
  @Override
  public Collection<byte[]> getPayload() throws IOException {
    return payloads;
  }
}
