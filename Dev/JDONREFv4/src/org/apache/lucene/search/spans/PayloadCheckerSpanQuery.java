/*
 * JDONREFv4
 * CeCILL Copyright 2014 © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, INSEE
 * Julien
 * 
 * Ce logiciel est un service web servant à valider et géocoder des adresses postales.
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant
 * les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier
 * et/ou redistribuer ce programme sous les conditions de la licence CeCILL telle que
 * diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info".
 * En contrepartie de l'accessibilité au code source et des droits de copie, de
 * modification et de redistribution accordés par cette licence, il n'est offert aux
 * utilisateurs qu'une garantie limitée.  Pour les mêmes raisons, seule une
 * responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits
 * patrimoniaux et les concédants successifs.
 * A cet égard l'attention de l'utilisateur est attirée sur les risques associés au
 * chargement,  à l'utilisation,  à la modification et/ou au développement et à la
 * reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel
 * libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des
 * développeurs et des professionnels avertis possédant  des  connaissances
 * informatiques approfondies.  Les utilisateurs sont donc invités à charger  et tester
 * l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser
 * et l'exploiter dans les mêmes conditions de sécurité.
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.apache.lucene.util.Bits;
import org.elasticsearch.common.lucene.search.MatchNoDocsQuery;

/**
 * Matches span where payload checks the given rules.
 * @author Julien
 */
public class PayloadCheckerSpanQuery extends SpanQuery implements Cloneable
{
    protected int termCountPayloadFactor = MultiPayloadTermSpans.NOTERMCOUNTPAYLOADFACTOR;
  
    protected List<MultiPayloadSpanTermQuery> clauses;
    
    protected String field;
    
    protected IPayloadChecker checker;
    
    protected int limit = -1; 
    
    public int termCountPayloadFactor() {
        return termCountPayloadFactor();
    }

    public void setTermCountPayloadFactor(int factor) {
        this.termCountPayloadFactor = factor;
        for(int i=0;i<clauses.size();i++)
            this.clauses.get(i).termCountPayloadFactor = termCountPayloadFactor;
    }
  
    /** Return the clauses whose spans are matched. */
    public MultiPayloadSpanTermQuery[] getClauses() {
        return clauses.toArray(new MultiPayloadSpanTermQuery[clauses.size()]);
    }
    
    @Override
    public String getField() { return field; }
    
    @Override
    public void extractTerms(Set<Term> terms) {
        for (final SpanQuery clause : clauses) {
            clause.extractTerms(terms);
        }
    }

    public IPayloadChecker getChecker() {
        return checker;
    }

    public void setChecker(IPayloadChecker checker) {
        this.checker = checker;
    }
    
    /** Construct a PayloadSpanQuery.
     * @param clauses the clauses to find near each other
     * */
    public PayloadCheckerSpanQuery(MultiPayloadSpanTermQuery... clauses) {
        // copy clauses array into an ArrayList
        this.clauses = new ArrayList<>(clauses.length);
        for (int i = 0; i < clauses.length; i++) {
            addClause(clauses[i]);
            clauses[i].setOrder(i); // here we set the original order
        }
    }

    /** Adds a clause to this query */
    public final void addClause(MultiPayloadSpanTermQuery clause) {
        if (field == null) {
            field = clause.getField();
        } else if (clause.getField() != null && !clause.getField().equals(field)) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
        clause.termCountPayloadFactor = termCountPayloadFactor;
        clause.setOrder(clauses.size());
        this.clauses.add(clause);
    }

    @Override 
    public Spans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
        if (clauses.isEmpty())                      // optimize 0-clause case
          return new SpanOrQuery(getClauses()).getSpans(context, acceptDocs, termContexts);
        
        SpansPayloadChecker spans = new SpansPayloadChecker(this, context, acceptDocs, termContexts, checker);
        
        return spans;
    }

    /** Returns true iff <code>o</code> is equal to this. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PayloadCheckerSpanQuery gpsQuery = (PayloadCheckerSpanQuery) o;

        return clauses.equals(gpsQuery.clauses) && 
                checker.equals(gpsQuery.checker) &&
                  termCountPayloadFactor == gpsQuery.termCountPayloadFactor;
    }
    
    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("PayloadCheckerSpan([");
        Iterator<MultiPayloadSpanTermQuery> i = clauses.iterator();
        while (i.hasNext()) {
            SpanQuery clause = i.next();
            buffer.append(clause.toString(field));
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("]");
        buffer.append(checker.toString());
        buffer.append(")");
        return buffer.toString();
    }
   
    @Override
    public int hashCode() {
        int h = clauses.hashCode();
        h ^= (h << 10) | (h >>> 23);
        h ^= Float.floatToRawIntBits(getBoost());
        h ^= checker.hashCode();
        h ^= termCountPayloadFactor;
        return h;
    }
    
    @Override
    public PayloadCheckerSpanQuery clone() {
        int sz = clauses.size();
        MultiPayloadSpanTermQuery[] newClauses = new MultiPayloadSpanTermQuery[sz];

        for (int i = 0; i < sz; i++) {
            newClauses[i] = (MultiPayloadSpanTermQuery) clauses.get(i).clone();
        }
        
        PayloadCheckerSpanQuery soq = new PayloadCheckerSpanQuery(newClauses);
        
        soq.setChecker(checker);
        
        soq.setTermCountPayloadFactor(termCountPayloadFactor);
        
        return soq;
    }
    
    public Term[] getQueryTerms()
    {
        ArrayList<Term> terms = new ArrayList<>();
        for(int i=0;i<this.clauses.size();i++)
        {
            terms.add(this.clauses.get(i).getTerm());
        }
        return terms.toArray(new Term[0]);
    }
    
    @Override
    public Query rewrite(IndexReader reader) throws IOException
    {
        // get clause order by frequencies
        final List<AtomicReaderContext> leaves = reader.leaves();
        final TermContext[] contextArray = new TermContext[clauses.size()];
        final Term[] queryTerms = getQueryTerms();
        collectTermContext(reader, leaves, contextArray, queryTerms);
        TermFrequency[] freq = getTermInOrder(contextArray);
        int[] indices = revertFrequencies(freq);
        
        // check limits
        if (limit!=-1 && overLimits(freq))
        {
                return new MatchNoDocsQuery();
        }
        
        // change clause order
        ArrayList<MultiPayloadSpanTermQuery> newclauses = new ArrayList<>();
        for(int i=0;i<clauses.size();i++)
            newclauses.add(null);
        for(int i=0;i<clauses.size();i++)
            newclauses.set(indices[i], clauses.get(i)); // NB: original order kept
        this.clauses = newclauses;
        
        // rewrite
        PayloadCheckerSpanQuery clone = null;
        for (int i = 0; i < clauses.size(); i++) {
            MultiPayloadSpanTermQuery c = clauses.get(i);
            MultiPayloadSpanTermQuery query = (MultiPayloadSpanTermQuery) c.rewrite(reader);
            if (query != c) {                     // clause rewrote: must clone
                if (clone == null) {
                    clone = this.clone();
                }
                clone.clauses.set(i, query); // write over existing clause
            }
        }
        
        if (clone != null) {
            return clone;                        // some clauses rewrote
        } else {
            return this;                         // no clauses rewrote
        }
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public int getLimit()
    {
        return this.limit;
    }

    /**
     * Return true if the less term frequencies is over the limit
     * 
     * @param contextArray assume frequencies are in order
     * @return 
     */
    protected boolean overLimits(TermFrequency[] freq)
    {
        if (freq.length==0) return true; // empty indeed ! => NoMatch
        
        return freq[0].freq>limit;
    }
    
    public class TermFrequency implements Comparable<TermFrequency>
    {
        public int i;
        public int freq;
        
        @Override
        public int compareTo(TermFrequency o)
        {
            return freq - o.freq;
        }
    }
    
    /**
     * give the order by frequencies for term query from context
     * @param contextArray
     * @return 
     */
    public TermFrequency[] getTermInOrder(TermContext[] contextArray)
    {
        TermFrequency[] frequencies = new TermFrequency[contextArray.length];
        for(int i=0;i<contextArray.length;i++)
        {
            TermFrequency tf = new TermFrequency();
            tf.i = i;
            if (contextArray[i]!=null)
                tf.freq = contextArray[i].docFreq();
            else
                tf.freq = 0;
            frequencies[i] = tf;
        }
        Arrays.sort(frequencies);
        
        return frequencies;
    }
    
    public int[] revertFrequencies(TermFrequency[] freq)
    {
        int[] res = new int[freq.length];
        for(int i=0;i<freq.length;i++)
            res[freq[i].i] = i;
        
        return res;
    }
    
  public void collectTermContext(IndexReader reader,
      List<AtomicReaderContext> leaves, TermContext[] contextArray,
      Term[] queryTerms) throws IOException {
    TermsEnum termsEnum = null;
    for (AtomicReaderContext context : leaves) {
      final Fields fields = context.reader().fields();
      if (fields == null) {
        // reader has no fields
        continue;
      }
      for (int i = 0; i < queryTerms.length; i++) {
        Term term = queryTerms[i];
        TermContext termContext = contextArray[i];
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
            contextArray[i] = new TermContext(reader.getContext(),
                termsEnum.termState(), context.ord, termsEnum.docFreq(),
                termsEnum.totalTermFreq());
          } else {
            termContext.register(termsEnum.termState(), context.ord,
                termsEnum.docFreq(), termsEnum.totalTermFreq());
          }
          
        }
        
      }
    }
  }
}