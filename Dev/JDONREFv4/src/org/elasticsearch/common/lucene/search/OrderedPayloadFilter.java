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
package org.elasticsearch.common.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.elasticsearch.common.lucene.docset.DocIdSets;
import org.elasticsearch.common.lucene.docset.OrderedPayloadDocIdSet;

/**
 * 
 * @author Julien
 */
public class OrderedPayloadFilter extends Filter implements Iterable<TermFilter>
{
    protected final List<TermFilter> filters = new ArrayList<>();
    
    public void add(TermFilter filter)
    {
        filters.add(filter);
    }
    
    public List<TermFilter> filters()
    {
        return filters;
    }
    
    @Override
    public final Iterator<TermFilter> iterator() {
        return filters().iterator();
    }
    
    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException
    {
        if (filters.size()==1)
            return filters.get(0).getDocIdSet(context, acceptDocs);
        
        DocIdSet[] sets = new DocIdSet[filters.size()];
        for (int i = 0; i < filters.size(); i++) {
            DocIdSet set = filters.get(i).getDocIdSet(context, acceptDocs);
            if (DocIdSets.isEmpty(set)) { // none matching for this filter, we AND, so return EMPTY
                return null;
            }
            sets[i] = set;
        }
        return new OrderedPayloadDocIdSet(sets);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == filters ? 0 : filters.hashCode());
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;

        OrderedPayloadFilter other = (OrderedPayloadFilter) obj;
        return equalFilters(filters, other.filters);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Filter filter : filters) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append('+');
            builder.append(filter);
        }
        return builder.toString();
    }
    
    private boolean equalFilters(List<? extends Filter> filters1, List<? extends Filter> filters2) {
        return (filters1 == filters2) || ((filters1 != null) && filters1.equals(filters2));
    }
}