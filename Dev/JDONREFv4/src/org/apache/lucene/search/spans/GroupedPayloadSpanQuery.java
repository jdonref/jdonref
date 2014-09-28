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
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Bits;

/**
 * Matches span which are grouped by payload values.
 * @author Julien
 */
public class GroupedPayloadSpanQuery extends SpanQuery implements Cloneable
{
    protected List<SpanQuery> clauses;
    
    protected String field;

    

    /** Return the clauses whose spans are matched. */
    public SpanQuery[] getClauses() {
        return clauses.toArray(new SpanQuery[clauses.size()]);
    }
    
    @Override
    public String getField() { return field; }
    
    @Override
    public void extractTerms(Set<Term> terms) {
        for (final SpanQuery clause : clauses) {
            clause.extractTerms(terms);
        }
    }
    
    /** Construct a GroupedPayloadSpanQuery.  Matches spans matching a span from each
     * clause, the spans from each clause must be grouped by payload values.
     * Term without payloads must be grouped together.
     * @param clauses the clauses to find near each other
     * */
    public GroupedPayloadSpanQuery(SpanQuery... clauses) {

        // copy clauses array into an ArrayList
        this.clauses = new ArrayList<>(clauses.length);
        for (int i = 0; i < clauses.length; i++) {
            addClause(clauses[i]);
        }
    }

    /** Adds a clause to this query */
    public final void addClause(SpanQuery clause) {
        if (field == null) {
            field = clause.getField();
        } else if (clause.getField() != null && !clause.getField().equals(field)) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
        this.clauses.add(clause);
    }

    @Override
    public Spans getSpans(final AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
        if (clauses.isEmpty())                      // optimize 0-clause case
          return new SpanOrQuery(getClauses()).getSpans(context, acceptDocs, termContexts);

        if (clauses.size() == 1)                      // optimize 1-clause case
          return clauses.get(0).getSpans(context, acceptDocs, termContexts);

        return new SpansGroupedByPayload(this, context, acceptDocs, termContexts);
    }

    /** Returns true iff <code>o</code> is equal to this. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GroupedPayloadSpanQuery gpsQuery = (GroupedPayloadSpanQuery) o;

        return clauses.equals(gpsQuery.clauses);
    }
    
    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("GroupedPayloadSpan([");
        Iterator<SpanQuery> i = clauses.iterator();
        while (i.hasNext()) {
            SpanQuery clause = i.next();
            buffer.append(clause.toString(field));
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("])");
        return buffer.toString();
    }
   
    @Override
    public int hashCode() {
        int h = clauses.hashCode();
        h ^= (h << 10) | (h >>> 23);
        h ^= Float.floatToRawIntBits(getBoost());
        return h;
    }
    
    @Override
    public GroupedPayloadSpanQuery clone() {
        int sz = clauses.size();
        SpanQuery[] newClauses = new SpanQuery[sz];

        for (int i = 0; i < sz; i++) {
            newClauses[i] = (SpanQuery) clauses.get(i).clone();
        }
        GroupedPayloadSpanQuery soq = new GroupedPayloadSpanQuery(newClauses);
        return soq;
    }
    
    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        GroupedPayloadSpanQuery clone = null;
        for (int i = 0; i < clauses.size(); i++) {
            SpanQuery c = clauses.get(i);
            SpanQuery query = (SpanQuery) c.rewrite(reader);
            if (query != c) {                     // clause rewrote: must clone
                if (clone == null) {
                    clone = this.clone();
                }
                clone.clauses.set(i, query);
            }
        }
        if (clone != null) {
            return clone;                        // some clauses rewrote
        } else {
            return this;                         // no clauses rewrote
        }
    }
}