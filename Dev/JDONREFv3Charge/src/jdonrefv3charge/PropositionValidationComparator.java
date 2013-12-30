/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdonrefv3charge;

import java.util.Comparator;
import ppol.jdonref.PropositionValidation;

/**
 *
 * @author marcanhe
 */
public class PropositionValidationComparator implements Comparator<PropositionValidation> {

    /**
     * Classe les propositions dans un ordre descendant en fonction de leur note.
     * @param pv1
     * @param pv2
     * @return
     */
    public int compare(PropositionValidation pv1, PropositionValidation pv2) {
        final int note1 = Integer.parseInt(pv1.getNote());
        final int note2 = Integer.parseInt(pv2.getNote());

        return note2 - note1;
    }
}
