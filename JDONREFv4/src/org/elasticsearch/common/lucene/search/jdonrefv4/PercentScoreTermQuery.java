package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

/**
 * A Query that matches documents containing a term.
 * This may be combined with other terms with a {@link BooleanQuery}.
 * 
 * A constant score is applyed to them.
 */
public class PercentScoreTermQuery extends Query {

    protected final Term term;
    protected final int docFreq;
    private final TermContext perReaderTermState;
    private boolean finalWildCard = false;
    
    protected float score;
    protected int typemask;
    protected int tokenmask;   
    
    protected int freq;
    
    public int getFreq()
    {
        return freq;
    }
    
    public void setFreq(int freq)
    {
        this.freq = freq;
    }
    
    public int getTypeMask()
    {
        return typemask;
    }
    
    public int getTokenMask()
    {
        return tokenmask;
    }
    
    public float getScore()
    {
        return score;
    }
    
    public void setScore(float score)
    {
        this.score = score;
    }

    public boolean isFinalWildCard() {
        return finalWildCard;
    }

    public void setFinalWildCard(boolean finalWildCard) {
        this.finalWildCard = finalWildCard;
    }
    
    public final class PercentScoreTermWeight extends Weight {

        protected TermContext termStates;
        protected IndexSearcher searcher;

        public TermContext getContext() {
            return termStates;
        }

        public PercentScoreTermWeight(TermContext termStates)
                throws IOException {
            assert termStates != null : "TermContext must not be null";
            this.termStates = termStates;
        }

        @Override
        public String toString() {
            return "weight(" + PercentScoreTermQuery.this + ")";
        }

        @Override
        public Query getQuery() {
            return PercentScoreTermQuery.this;
        }

        @Override
        public float getValueForNormalization() {
            return 1.0f;
        }
        
        float queryNorm;
        float topLevelBoost;

        public float getQueryNorm() {
            return queryNorm;
        }

        public float getTopLevelBoost() {
            return topLevelBoost;
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost)
        {
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            assert termStates.topReaderContext == ReaderUtil.getTopLevelContext(context) : "The top-reader used to create Weight (" + termStates.topReaderContext + ") is not the same as the current reader's top-reader (" + ReaderUtil.getTopLevelContext(context);
            
            final TermsEnum termsEnum = getTermsEnum(context);
            if (termsEnum == null) {
                return null;
            }
            
            final DocsEnum postings = termsEnum.docs(acceptDocs, null, DocsEnum.FLAG_NONE);
                        
            assert postings != null;
            return new PercentScoreTermScorer(this,postings, getScore(), getTypeMask(), getTokenMask(), getFreq());
        }

        /**
         * Returns a {@link TermsEnum} positioned at this weights Term or null if
         * the term does not exist in the given context
         */
        private TermsEnum getTermsEnum(AtomicReaderContext context) throws IOException {
            final TermState state = termStates.get(context.ord);
            if (state == null) { // term is not present in that reader

                assert termNotInReader(context.reader(), term) : "no termstate found but term exists in reader term=" + term;
                return null;
            }
            //System.out.println("LD=" + reader.getLiveDocs() + " set?=" + (reader.getLiveDocs() != null ? reader.getLiveDocs().get(0) : "null"));
            final TermsEnum termsEnum = context.reader().terms(term.field()).iterator(null);
            termsEnum.seekExact(term.bytes(), state);
            return termsEnum;
        }

        private boolean termNotInReader(AtomicReader reader, Term term) throws IOException {
            // only called from assert
            //System.out.println("TQ.termNotInReader reader=" + reader + " term=" + field + ":" + bytes.utf8ToString());
            return reader.docFreq(term) == 0;
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            Scorer scorer = scorer(context, context.reader().getLiveDocs());
            if (scorer != null) {
                int newDoc = scorer.advance(doc);
                if (newDoc == doc) {
                    float score = scorer.score();
                    Explanation scoreExplanation = new Explanation(score, "payload=" + score);
                    scoreExplanation.setValue(score);
                    return scoreExplanation;
                }
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }
    }
    
    /** Constructs a query for the term <code>t</code>.
     * @param t
     * @param score  the score for matching the term */
    public PercentScoreTermQuery(Term t, float score, int typemask, int tokenmask) {
        this(t, -1, score, typemask, tokenmask);
    }

    /** Expert: constructs a TermQuery that will use the
     *  provided docFreq instead of looking up the docFreq
     *  against the searcher. */
    public PercentScoreTermQuery(Term t, int docFreq, float score, int typemask, int tokenmask) {
        term = t;
        this.typemask = typemask;
        this.tokenmask = tokenmask;
        this.docFreq = docFreq;
        this.score = score;
        perReaderTermState = null;
    }

    /** Expert: constructs a TermQuery that will use the
     *  provided docFreq instead of looking up the docFreq
     *  against the searcher. */
    public PercentScoreTermQuery(Term t, TermContext states, float score, int typemask, int tokenmask) {
        assert states != null;
        term = t;
        this.typemask = typemask;
        this.tokenmask = tokenmask;
        docFreq = states.docFreq();
        this.score = score;
        perReaderTermState = states;
    }

    /** Returns the term of this query. */
    public Term getTerm() {
        return term;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        final IndexReaderContext context = searcher.getTopReaderContext();
        final TermContext termState;
        if (perReaderTermState == null || perReaderTermState.topReaderContext != context) {
            // make TermQuery single-pass if we don't have a PRTS or if the context differs!
            termState = TermContext.build(context, term);
        } else {
            // PRTS was pre-build for this IS
            termState = this.perReaderTermState;
        }

        // we must not ignore the given docFreq - if set use the given value (lie)
        if (docFreq != -1) {
            termState.setDocFreq(docFreq);
        }
        
        int termfreq = collectTermContext(searcher.getIndexReader(), searcher.getIndexReader().leaves(), getTerm());
        this.setFreq(termfreq);
        
        return new PercentScoreTermWeight(termState);
    }

    
   public int collectTermContext(IndexReader reader,
                                List<AtomicReaderContext> leaves, 
                                Term term) throws IOException {
    TermsEnum termsEnum = null;
    TermContext termContext = null;
    for (AtomicReaderContext context : leaves)
    {
        final Fields fields = context.reader().fields();
        if (fields == null) {
          // reader has no fields
          continue;
        }
      
        
        final Terms terms = fields.terms(term.field());
        if (terms == null) {
          // field does not exist
          continue;
        }
        termsEnum = terms.iterator(termsEnum);
        assert termsEnum != null;
        
        if (termsEnum == TermsEnum.EMPTY) continue;
        if (termsEnum.seekExact(term.bytes())) {
          if (termContext == null) {
            termContext = new TermContext(reader.getContext(),
                                                termsEnum.termState(), 
                                                context.ord, 
                                                termsEnum.docFreq(),
                                                termsEnum.totalTermFreq());
          } else {
            termContext.register(termsEnum.termState(), 
                                    context.ord,
                                    termsEnum.docFreq(), 
                                    termsEnum.totalTermFreq());
          }
          
        }
        
      
    }
    if (termContext==null) return 0;
    return termContext.docFreq();
  }
    
    @Override
    public void extractTerms(Set<Term> terms) {
        terms.add(getTerm());
    }

    /** Prints a user-readable version of this query. */
    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!term.field().equals(field)) {
            buffer.append(term.field());
            buffer.append(":");
        }
        buffer.append(term.text());
        buffer.append(ToStringUtils.boost(getBoost()));
        return buffer.toString();
    }

    /** Returns true iff <code>o</code> is equal to this. */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PercentScoreTermQuery)) {
            return false;
        }
        PercentScoreTermQuery other = (PercentScoreTermQuery) o;
        return (this.getBoost() == other.getBoost()) && this.term.equals(other.term);
    }

    /** Returns a hash code value for this object.*/
    @Override
    public int hashCode() {
        return Float.floatToIntBits(getBoost()) ^ term.hashCode();
    }
}