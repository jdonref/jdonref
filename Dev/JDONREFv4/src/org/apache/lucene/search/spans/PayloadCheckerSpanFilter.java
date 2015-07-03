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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.apache.lucene.util.Bits;

/**
 * Matches span where payload checks the given rules.
 * @author Julien
 */
public class PayloadCheckerSpanFilter extends Filter implements Cloneable, IPayloadCheckerSpanFilter
{
    public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    
    protected int termCountPayloadFactor = NOTERMCOUNTPAYLOADFACTOR;
  
    protected List<MultiPayloadSpanTermFilter> clauses;
    
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
    public MultiPayloadSpanTermFilter[] getClauses() {
        return clauses.toArray(new MultiPayloadSpanTermFilter[clauses.size()]);
    }
    
    public String getField() { return field; }
    
    public IPayloadChecker getChecker() {
        return checker;
    }

    public void setChecker(IPayloadChecker checker) {
        this.checker = checker;
    }
    
    /** Construct a PayloadSpanFilter.
     * @param clauses the clauses to find near each other
     * */
    public PayloadCheckerSpanFilter(MultiPayloadSpanTermFilter... clauses) {
        // copy clauses array into an ArrayList
        this.clauses = new ArrayList<>(clauses.length);
        for (int i = 0; i < clauses.length; i++) {
            addClause(clauses[i]);
            clauses[i].setOrder(i); // here we set the original order
        }
    }

    /** Adds a clause to this query */
    public final void addClause(MultiPayloadSpanTermFilter clause) {
        if (field == null) {
            field = clause.getField();
        } else if (clause.getField() != null && !clause.getField().equals(field)) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
        clause.termCountPayloadFactor = termCountPayloadFactor;
        clause.setOrder(clauses.size());
        this.clauses.add(clause);
    }

    public Spans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {

        SpansPayloadChecker spans = new SpansPayloadChecker(this, context, acceptDocs, termContexts, checker);
        
        return spans;
    }

    /** Returns true iff <code>o</code> is equal to this. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PayloadCheckerSpanFilter gpsQuery = (PayloadCheckerSpanFilter) o;

        return clauses.equals(gpsQuery.clauses) && 
                checker.equals(gpsQuery.checker) &&
                  termCountPayloadFactor == gpsQuery.termCountPayloadFactor;
    }
    
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("PayloadCheckerSpan([");
        Iterator<MultiPayloadSpanTermFilter> i = clauses.iterator();
        while (i.hasNext()) {
            MultiPayloadSpanTermFilter clause = i.next();
            buffer.append(clause.toString());
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
        h ^= checker.hashCode();
        h ^= termCountPayloadFactor;
        return h;
    }
    
    @Override
    public PayloadCheckerSpanFilter clone() throws CloneNotSupportedException {
        int sz = clauses.size();
        MultiPayloadSpanTermFilter[] newClauses = new MultiPayloadSpanTermFilter[sz];

        for (int i = 0; i < sz; i++) {
            newClauses[i] = (MultiPayloadSpanTermFilter) clauses.get(i).clone();
        }
        
        PayloadCheckerSpanFilter soq = new PayloadCheckerSpanFilter(newClauses);
        
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
    public int getClausesCount() {
        return clauses.size();
    }

    @Override
    public DocIdSet getDocIdSet(final AtomicReaderContext arc, final Bits bits) throws IOException {
        
        return new DocIdSetImpl(arc,bits);
    }

    private class DocIdSetImpl extends DocIdSet {

        private final AtomicReaderContext arc;
        private final Bits bits;

        public DocIdSetImpl(AtomicReaderContext arc, Bits bits) {
            this.arc = arc;
            this.bits = bits;
        }

        @Override
        public DocIdSetIterator iterator() throws IOException {
            return new DocIdSetIteratorImpl(arc, bits);
        }

        class DocIdSetIteratorImpl extends DocIdSetIterator {

            private SpansPayloadChecker check1;

            public DocIdSetIteratorImpl(AtomicReaderContext arc, Bits bits) throws IOException {
                check1 = (SpansPayloadChecker) getSpans(arc, bits, null);
            }

            @Override
            public int docID() {
                return check1.doc();
            }

            @Override
            public int nextDoc() throws IOException {
                if (check1.next())
                 return check1.doc();
                check1.matchDoc = NO_MORE_DOCS;
                return NO_MORE_DOCS;
            }

            @Override
            public int advance(int i) throws IOException {
                if (i==NO_MORE_DOCS)
                {
                    check1.matchDoc = NO_MORE_DOCS;
                    return NO_MORE_DOCS;
                }
                if (check1.skipTo(i))
                    return check1.doc();
                check1.matchDoc = NO_MORE_DOCS;
                return NO_MORE_DOCS;
            }

            @Override
            public long cost() {
                return check1.cost();
            }
        }
    }
}