package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.*;
import org.apache.lucene.util.Bits;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermQuery extends SpanTermQuery
{
  protected int termCountPayloadFactor = MultiPayloadTermSpans.NOTERMCOUNTPAYLOADFACTOR;
  protected int order;
  protected boolean checked = true;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
  /** Construct a SpanTermQuery matching the named term's spans. */
  public MultiPayloadSpanTermQuery(Term term) { super(term); }
  
  public int termCountPayloadFactor()
  {
      return termCountPayloadFactor();
  }
  
  public void setTermCountPayloadFactor(int factor)
  {
      this.termCountPayloadFactor = factor;
  }
  
  /**
   * :P
   * @param postings
   * @param term
   * @return 
   */
  protected MultiPayloadTermSpans makeTermSpans(DocsAndPositionsEnum postings,Term term, AtomicReader reader, Bits acceptDocs)
  {
      MultiPayloadTermSpans span = new MultiPayloadTermSpans(postings, term, termCountPayloadFactor, reader, acceptDocs );
      span.setOrder(order);
      span.setChecked(checked);
      return span;
  }
  
  @Override
  public MultiPayloadTermSpans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
    TermContext termContext = termContexts.get(term);
    final TermState state;
    if (termContext == null) {
      // this happens with span-not query, as it doesn't include the NOT side in extractTerms()
      // so we seek to the term now in this segment..., this sucks because its ugly mostly!
      final Fields fields = context.reader().fields();
      if (fields != null) {
        final Terms terms = fields.terms(term.field());
        if (terms != null) {
          final TermsEnum termsEnum = terms.iterator(null);
          if (termsEnum.seekExact(term.bytes())) { 
            state = termsEnum.termState();
          } else {
            state = null;
          }
        } else {
          state = null;
        }
      } else {
        state = null;
      }
    } else {
      state = termContext.get(context.ord);
    }
    
    if (state == null) { // term is not present in that reader
      return MultiPayloadTermSpans.emptyTermSpans();
    }
    
    final TermsEnum termsEnum = context.reader().terms(term.field()).iterator(null);
    termsEnum.seekExact(term.bytes(), state);
    
    final DocsAndPositionsEnum postings = termsEnum.docsAndPositions(acceptDocs, null, DocsAndPositionsEnum.FLAG_PAYLOADS);
    
    if (postings != null) {
      return makeTermSpans(postings, term, context.reader(), acceptDocs);
    } else {
      // term does exist, but has no positions
      throw new IllegalStateException("field \"" + term.field() + "\" was indexed without position data; cannot run SpanTermQuery (term=" + term.text() + ")");
    }
  }

  public int getOrder()
  {
      return this.order;
  }
  
    void setOrder(int i) {
        this.order = i;
    }
}