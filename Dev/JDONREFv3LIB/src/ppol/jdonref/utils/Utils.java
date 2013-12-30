/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcanhe
 */
public class Utils {

    public static boolean isEmpty(
            int[] anarray) {
        boolean booleanRet = true;
        if (anarray != null) {
            for (int i = 0; i < anarray.length; i++) {
                if (anarray[i] > 0) {
                    booleanRet = false;
                    break;
                }
            }
        }

        return booleanRet;
    }

    public static List<String> toList(String[] anarray) {
        final List<String> listRet = new ArrayList<String>();
        for (String astring : anarray) {
            listRet.add(astring);
        }
        
        return listRet;
    }

    public static List<Integer> toList(int[] anarray) {
        final List<Integer> listRet = new ArrayList<Integer>();
        for (Integer anint : anarray) {
            listRet.add(anint);
        }
        
        return listRet;
    }
}
