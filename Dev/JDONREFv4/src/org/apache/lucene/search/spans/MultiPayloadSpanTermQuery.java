package org.apache.lucene.search.spans;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermQuery extends SpanTermQuery
{
    /** Construct a SpanTermQuery matching the named term's spans. */
  public MultiPayloadSpanTermQuery(Term term) { super(term); }
  
  /**
   * :P
   * @param postings
   * @param term
   * @return 
   */
  protected TermSpans makeTermSpans(DocsAndPositionsEnum postings,Term term)
  {
      return new MultiPayloadTermSpans(postings, term);
  }
  
  @Override
  public Spans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
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
      return TermSpans.EMPTY_TERM_SPANS;
    }
    
    final TermsEnum termsEnum = context.reader().terms(term.field()).iterator(null);
    termsEnum.seekExact(term.bytes(), state);
    
    final DocsAndPositionsEnum postings = termsEnum.docsAndPositions(acceptDocs, null, DocsAndPositionsEnum.FLAG_PAYLOADS);

    if (postings != null) {
      return makeTermSpans(postings, term);
    } else {
      // term does exist, but has no positions
      throw new IllegalStateException("field \"" + term.field() + "\" was indexed without position data; cannot run SpanTermQuery (term=" + term.text() + ")");
    }
  }
}
