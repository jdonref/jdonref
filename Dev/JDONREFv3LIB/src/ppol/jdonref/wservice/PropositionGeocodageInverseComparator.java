/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

import java.util.Comparator;
import ppol.jdonref.GestionLogs;

/**
 *
 * @author marcanhe
 */
public class PropositionGeocodageInverseComparator implements Comparator<PropositionGeocodageInverse> {

    private static final PropositionGeocodageInverseComparator instance = new PropositionGeocodageInverseComparator();

    public static PropositionGeocodageInverseComparator getInstance() {
        return instance;
    }

    public int compare(PropositionGeocodageInverse pgi1, PropositionGeocodageInverse pgi2) {
        int intRet = 0;
        try {
            final Double distance1 = Double.valueOf(pgi1.getDistance());
            final Double distance2 = Double.valueOf(pgi2.getDistance());
            intRet = Double.valueOf(distance1 - distance2).intValue();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        return intRet;
    }
}
