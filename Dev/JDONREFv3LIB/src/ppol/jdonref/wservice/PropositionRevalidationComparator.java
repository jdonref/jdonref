/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import ppol.jdonref.utils.DateUtils;

/**
 *
 * @author marcanhe
 */
public class PropositionRevalidationComparator implements Comparator<PropositionRevalidation> {

    private static final PropositionRevalidationComparator instance = new PropositionRevalidationComparator();
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;

    public static PropositionRevalidationComparator getInstance() {
        return instance;
    }

    public int compare(PropositionRevalidation pv1, PropositionRevalidation pv2) {
        int intRet = 0;
        try{
        final Date date1 = DateUtils.parseStringToDate(pv1.getT0(), sdformat);
        final Date date2 = DateUtils.parseStringToDate(pv2.getT0(), sdformat);
        intRet = date2.compareTo(date1);
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return intRet;
    }
}

