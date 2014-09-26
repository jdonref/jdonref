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
package org.elasticsearch.common.lucene.docset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;
import org.elasticsearch.common.lucene.docset.BitsDocIdSetIterator;
import org.elasticsearch.common.lucene.docset.DocIdSets;

/**
 *
 * @author Julien
 */
public class OrderedPayloadDocIdSet extends DocIdSet
{
    private final DocIdSet[] sets;

    public OrderedPayloadDocIdSet(DocIdSet[] sets) {
        this.sets = sets;
    }
    
    @Override
    public boolean isCacheable() {
        for (DocIdSet set : sets) {
            if (!set.isCacheable()) {
                return false;
            }
        }
        return true;
    }
    
    
    @Override
    public Bits bits() throws IOException {
        Bits[] bits = new Bits[sets.length];
        for (int i = 0; i < sets.length; i++) {
            bits[i] = sets[i].bits();
            if (bits[i] == null) {
                return null;
            }
        }
        return new OrderedPayloadBits(bits);
    }

    @Override
    public DocIdSetIterator iterator() throws IOException {
        // we try and be smart here, if we can iterate through docsets quickly, prefer to iterate
        // over them as much as possible, before actually going to "bits" based ones to check
        List<DocIdSet> iterators = new ArrayList<>(sets.length);
        List<Bits> bits = new ArrayList<>(sets.length);
        for (DocIdSet set : sets) {
            if (DocIdSets.isFastIterator(set)) {
                iterators.add(set);
            } else {
                Bits bit = set.bits();
                if (bit != null) {
                    bits.add(bit);
                } else {
                    iterators.add(set);
                }
            }
        }
        if (bits.isEmpty()) {
            return IteratorBasedIterator.newDocIdSetIterator(iterators.toArray(new DocIdSet[iterators.size()]));
        }
        if (iterators.isEmpty()) {
            return new BitsDocIdSetIterator(new OrderedPayloadBits(bits.toArray(new Bits[bits.size()])));
        }
        // combination of both..., first iterating over the "fast" ones, and then checking on the more
        // expensive ones
        return new BitsDocIdSetIterator.FilteredIterator(
                IteratorBasedIterator.newDocIdSetIterator(iterators.toArray(new DocIdSet[iterators.size()])),
                new OrderedPayloadBits(bits.toArray(new Bits[bits.size()]))
        );
    }
    
    protected static class OrderedPayloadBits implements Bits {

        private final Bits[] bits;

        OrderedPayloadBits(Bits[] bits) {
            this.bits = bits;
        }

        @Override
        public boolean get(int index) {
            for (Bits bit : bits) {
                if (!bit.get(index)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int length() {
            return bits[0].length();
        }
    }
    
    static class IteratorBasedIterator extends DocIdSetIterator {
        private int lastReturn = -1;
        private final DocIdSetIterator[] iterators;
        private final long cost;


        public static DocIdSetIterator newDocIdSetIterator(DocIdSet[] sets) throws IOException {
            if (sets.length == 0) {
                return  DocIdSetIterator.empty();
            }
            final DocIdSetIterator[] iterators = new DocIdSetIterator[sets.length];
            int j = 0;
            long cost = Integer.MAX_VALUE;
            for (DocIdSet set : sets) {
                if (set == null) {
                    return DocIdSetIterator.empty();
                } else {
                    DocIdSetIterator docIdSetIterator = set.iterator();
                    if (docIdSetIterator == null) {
                        return DocIdSetIterator.empty();// non matching
                    }
                    iterators[j++] = docIdSetIterator;
                    cost = Math.min(cost, docIdSetIterator.cost());
                }
            }
            if (sets.length == 1) {
               // shortcut if there is only one valid iterator.
               return iterators[0];
            }
            return new IteratorBasedIterator(iterators, cost);
        }

        private IteratorBasedIterator(DocIdSetIterator[] iterators, long cost) throws IOException {
            this.iterators = iterators;
            this.cost = cost;
        }

        @Override
        public final int docID() {
            return lastReturn;
        }

        @Override
        public final int nextDoc() throws IOException {

            if (lastReturn == DocIdSetIterator.NO_MORE_DOCS) {
                assert false : "Illegal State - DocIdSetIterator is already exhausted";
                return DocIdSetIterator.NO_MORE_DOCS;
            }

            DocIdSetIterator dcit = iterators[0];
            int target = dcit.nextDoc();
            int size = iterators.length;
            int skip = 0;
            int i = 1;
            while (i < size) {
                if (i != skip) {
                    dcit = iterators[i];
                    int docid = dcit.advance(target);
                    if (docid > target) {
                        target = docid;
                        if (i != 0) {
                            skip = i;
                            i = 0;
                            continue;
                        } else
                            skip = 0;
                    }
                }
                i++;
            }
            return (lastReturn = target);
        }

        @Override
        public final int advance(int target) throws IOException {

            if (lastReturn == DocIdSetIterator.NO_MORE_DOCS) {
                assert false : "Illegal State - DocIdSetIterator is already exhausted";
                return DocIdSetIterator.NO_MORE_DOCS;
            }

            DocIdSetIterator dcit = iterators[0];
            target = dcit.advance(target);
            int size = iterators.length;
            int skip = 0;
            int i = 1;
            while (i < size) {
                if (i != skip) {
                    dcit = iterators[i];
                    int docid = dcit.advance(target);
                    if (docid > target) {
                        target = docid;
                        if (i != 0) {
                            skip = i;
                            i = 0;
                            continue;
                        } else {
                            skip = 0;
                        }
                    }
                }
                i++;
            }
            return (lastReturn = target);
        }

        @Override
        public long cost() {
            return cost;
        }
    }
}
