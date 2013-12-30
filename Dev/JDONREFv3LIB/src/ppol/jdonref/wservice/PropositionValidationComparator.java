/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.wservice;

import java.util.Comparator;

/**
 *
 * @author marcanhe
 */
public class PropositionValidationComparator implements Comparator<PropositionValidation> {

    private static final PropositionValidationComparator instance = new PropositionValidationComparator();

    public static PropositionValidationComparator getInstance() {
        return instance;
    }

    public int compare(PropositionValidation pv1, PropositionValidation pv2) {
        int intRet = 0;
        try {
            final Integer note1 = Integer.valueOf(pv1.getNote());
            final Integer note2 = Integer.valueOf(pv2.getNote());
            intRet = Integer.valueOf(note2 - note1);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        return intRet;
    }

}
