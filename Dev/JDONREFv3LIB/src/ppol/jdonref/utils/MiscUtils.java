/**
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
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
package ppol.jdonref.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author arochewi
 */
public class MiscUtils {

    /**
     * Produit un nouveau tableau de chaines extrait de srcTab e nprenant toutes les lignes de
     * beginIndex a endIndex (compris)
     * @param srcTab
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public static String[] extractPartOfTab(String[] srcTab, int beginIndex, int endIndex) {
        int oldLen = srcTab.length;
        int newLen = endIndex - beginIndex + 1;
        String[] resTab = new String[newLen];

        int j;
        for (int i = 0; i < newLen; i++) {
            j = beginIndex + i;
            if (j < oldLen) {
                resTab[i] = srcTab[j];
            }
        }

        return resTab;
    }

    public static String getToday() {
        final Calendar calendar = GregorianCalendar.getInstance();
        final DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        df.setLenient(false);

        return df.format(calendar.getTime());
    }

    public static double truncate(double adouble, int decimals) {
        double doubleRet = adouble;
        final String doubleStr = String.valueOf(adouble);
        final String[] splitedDoubleStr = doubleStr.split("[.]");
        if (splitedDoubleStr.length == 2) {
            String truncatedDecimalsStr = (splitedDoubleStr[1].length() > decimals) ? splitedDoubleStr[1].substring(0, decimals) : splitedDoubleStr[1];
            doubleRet = Double.valueOf(splitedDoubleStr[0] + "." + truncatedDecimalsStr);
        }

        return doubleRet;
    }
    
    public static double distanceSpheroid(double distance){
        return distance/((Math.PI/180) * 6378137);
    }
}
