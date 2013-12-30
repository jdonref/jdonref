/*
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
 * julien.moquet@interieur.gouv.fr
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
package jdonrefv3charge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Représente une ligne de log de l'activité simulée d'un utilisateur.
 * @author jmoquet
 */
public class Log {
    // WA 03/2012 potentiellement dangereux mais ne semble pas etre utilise en contexte multi-threade.
    static SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRANCE);
    long starttime;
    String profil;
    int user;
    char scenario;
    String modele;
    int nbfautes;
    int structure;
    String departement;
    boolean ok;
    long dureetotale;
    long dureeNormalise1;
    long dureeNormalise2;
    long dureeValide1;
    long dureeValide2;
    long dureeGeocode1;
    long dureeGeocode2;
    long dureeReverse1;
    long dureeReverse2;
    String ligne1;
    String ligne2;
    String ligne3;
    String ligne4;
    String ligne5;
    String ligne6;
    String ligne7;
    String date;
    String x;
    String y;
    String note;
    String retourNormalise1;
    int serviceNormalise1;
    int operationNormalise1;
    String retourNormalise2;
    int serviceNormalise2;
    int operationNormalise2;
    String retourValide1;
    int serviceValide1;
    String retourValide2;
    int serviceValide2;
    String retourGeocode1;
    int serviceGeocode1;
    String retourGeocode2;
    int serviceGeocode2;
    String retourReverse1;
    int serviceReverse1;
    String retourReverse2;
    int serviceReverse2;
    /**
     * Le code d'erreur durant la simulation de l'activité d'un utilisateur.
     *
     * <ul>
     *   <li>0 si aucune erreur ne s'est produit.</li>
     *   <li>Concernant la validation<ul>
     *   <li>1 si la méthode a retourné une erreur.</li>
     *   <li>2 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>3 si aucune proposition n'est retournée.</li>
     *   <li>4 si jdonref a retourné la valeur null</li>
     *   <li>5 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant le géocodage<ul>
     *   <li>6 si la méthode a retourné une erreur.</li>
     *   <li>7 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>8 si aucune proposition n'est retournée.</li>
     *   <li>9 si jdonref a retourné la valeur null</li>
     *   <li>10 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant la restructuration<ul>
     *   <li>11 si la méthode a retourné une erreur.</li>
     *   <li>12 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>13 si aucune proposition n'est retournée.</li>
     *   <li>14 si jdonref a retourné la valeur null</li>
     *   <li>15 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant le géocodage inverse<ul>
     *   <li>16 si la méthode a retourné une erreur.</li>
     *   <li>17 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>18 si aucune proposition n'est retournée.</li>
     *   <li>19 si jdonref a retourné la valeur null</li>
     *   <li>20 si une exception s'est produite</li>
     *   </ul></li>
     * </ul>
     */
    int codeerreur;

    /**
     * Constructeur par défaut.
     */
    public Log() {
    }

    /**
     * Retourne la chaine sans guillemets.
     * @param s
     * @return
     */
    private static String noGuillemets(String s) {
        return s.substring(1, s.length() - 1);
    }

    /**
     * Obtient l'entête correspondant au résultat retourné par toString().
     * @return
     */
    public static String getHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"date\"\t\"profil\"\t\"user\"\t\"scenario\"\t\"modele\"\t\"fautes\"\t\"structure\"\t\"departement\"\t\"ok\"\t\"codeerreur\"\t");
        sb.append("\"total\"\t\"normalise1\"\t\"normalise2\"\t\"valide1\"\t\"valide2\"\t");
        sb.append("\"geocode1\"\t\"geocode2\"\t\"reverse1\"\t\"reverse2\"\t");
        sb.append("\"ligne1\"\t\"ligne2\"\t\"ligne3\"\t\"ligne4\"\t\"ligne5\"\t\"ligne6\"\t\"ligne7\"\t\"date\"\t\"x\"\t\"y\"\t\"note\"\t");
        sb.append("\"retourNormalise1\"\t\"serviceNormalise1\"\t\"operationNormalise1\"\t\"retourNormalise2\"\t\"serviceNormalise2\"\t\"operationNormalise2\"\t");
        sb.append("\"retourValide1\"\t\"servicevalide1\"\t\"retourValide2\"\t\"serviceValide2\"\t");
        sb.append("\"retourGeocode1\"\t\"serviceGeocode1\"\t\"retourGeocode2\"\t\"serviceGeocode2\"\t");
        sb.append("\"retourReverse1\"\t\"serviceReverse1\"\t\"retourReverse2\"\t\"serviceReverse2\"\r\n");
        return sb.toString();
    }

    /**
     * Charge les informations à partir de la ligne.
     * @param ligne
     */
    public void load(String ligne) throws ParseException {
        String[] data = ligne.split("\t");
        starttime = sdformat.parse(noGuillemets(data[0])).getTime();
        profil = noGuillemets(data[1]);
        user = Integer.parseInt(data[2]);
        scenario = noGuillemets(data[3]).charAt(0);
        modele = noGuillemets(data[4]);
        nbfautes = Integer.parseInt(data[5]);
        structure = Integer.parseInt(data[6]);
        departement = noGuillemets(data[7]);
        ok = Boolean.parseBoolean(data[8]);
        int index = 9;
        codeerreur = Integer.parseInt(data[index++]);
        dureetotale = Long.parseLong(data[index++]);
        dureeNormalise1 = Long.parseLong(data[index++]);
        dureeNormalise2 = Long.parseLong(data[index++]);
        dureeValide1 = Long.parseLong(data[index++]);
        dureeValide2 = Long.parseLong(data[index++]);
        dureeGeocode1 = Long.parseLong(data[index++]);
        dureeGeocode2 = Long.parseLong(data[index++]);
        dureeReverse1 = Long.parseLong(data[index++]);
        dureeReverse2 = Long.parseLong(data[index++]);
        ligne1 = data[index++];
        ligne2 = data[index++];
        ligne3 = data[index++];
        ligne4 = data[index++];
        ligne5 = data[index++];
        ligne6 = data[index++];
        ligne7 = readString(data, index++, false, "");
        date = data[index++];
        x = data[index++];
        y = data[index++];
        note = data[index++];
        retourNormalise1 = data[index++];
        serviceNormalise1 = Integer.parseInt(data[index++]);
        operationNormalise1 = Integer.parseInt(data[index++]);
        retourNormalise2 = data[index++];
        serviceNormalise2 = Integer.parseInt(data[index++]);
        operationNormalise2 = Integer.parseInt(data[index++]);
        retourValide1 = data[index++];
        serviceValide1 = Integer.parseInt(data[index++]);
        retourValide2 = data[index++];
        serviceValide2 = Integer.parseInt(data[index++]);
        retourGeocode1 = data[index++];
        serviceGeocode1 = Integer.parseInt(data[index++]);
        retourGeocode2 = data[index++];
        serviceGeocode2 = Integer.parseInt(data[index++]);
        retourReverse1 = data[index++];
        serviceReverse1 = Integer.parseInt(data[index++]);
        retourReverse2 = data[index++];
        serviceReverse2 = Integer.parseInt(data[index++]);

    }

    private long readLong(String[] data, int index, long def) {
        long res = def;

        if (data.length > index) {
            try {
                res = Long.parseLong(data[index]);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            /*default value*/
            }
        }
        return res;
    }

    private String readString(String[] data, int index, boolean removeQuotes, String def) {
        String res = def;
        if (data.length > index) {
            if (removeQuotes) {
                res = noGuillemets(data[index]);
            } else {
                res = data[index];
            }
        }
        return res;
    }

    /**
     * Retourne la représentation sous forme de chaine de la ligne de log.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(sdformat.format(new Date(starttime)));
        sb.append("\"\t\"");
        sb.append(profil);
        sb.append("\"\t");
        sb.append(user);
        sb.append("\t\"");
        sb.append(scenario);
        sb.append("\"\t\"");
        sb.append(modele);
        sb.append("\"\t");
        sb.append(nbfautes);
        sb.append("\t");
        sb.append(structure);
        sb.append("\t\"");
        sb.append(departement);
        sb.append("\"\t");
        sb.append(ok);
        sb.append("\t");
        sb.append(codeerreur);
        sb.append("\t");
        sb.append(dureetotale);
        sb.append("\t");
        sb.append(dureeNormalise1);
        sb.append("\t");
        sb.append(dureeNormalise2);
        sb.append("\t");
        sb.append(dureeValide1);
        sb.append("\t");
        sb.append(dureeValide2);
        sb.append("\t");
        sb.append(dureeGeocode1);
        sb.append("\t");
        sb.append(dureeGeocode2);
        sb.append("\t");
        sb.append(dureeReverse1);
        sb.append("\t");
        sb.append(dureeReverse2);
        sb.append("\t\"");
        sb.append(ligne1);
        sb.append("\"\t\"");
        sb.append(ligne2);
        sb.append("\"\t\"");
        sb.append(ligne3);
        sb.append("\"\t\"");
        sb.append(ligne4);
        sb.append("\"\t\"");
        sb.append(ligne5);
        sb.append("\"\t\"");
        sb.append(ligne6);
        sb.append("\"\t\"");
        sb.append(ligne7);
        sb.append("\"\t\"");
        sb.append(date);
        sb.append("\"\t\"");
        sb.append(x);
        sb.append("\"\t\"");
        sb.append(y);
        sb.append("\"\t\"");
        sb.append(note);
        sb.append("\"\t\"");
        sb.append(retourNormalise1);
        sb.append("\"\t");
        sb.append(serviceNormalise1);
        sb.append("\t");
        sb.append(operationNormalise1);
        sb.append("\t\"");
        sb.append(retourNormalise2);
        sb.append("\"\t");
        sb.append(serviceNormalise2);
        sb.append("\t");
        sb.append(operationNormalise2);
        sb.append("\t\"");
        sb.append(retourValide1);
        sb.append("\"\t");
        sb.append(serviceValide1);
        sb.append("\t\"");
        sb.append(retourValide2);
        sb.append("\"\t");
        sb.append(serviceValide2);
        sb.append("\t\"");
        sb.append(retourGeocode1);
        sb.append("\"\t");
        sb.append(serviceGeocode1);
        sb.append("\t\"");
        sb.append(retourGeocode2);
        sb.append("\"\t");
        sb.append(serviceGeocode2);
        sb.append("\t\"");
        sb.append(retourReverse1);
        sb.append("\"\t");
        sb.append(serviceReverse1);
        sb.append("\t\"");
        sb.append(retourReverse2);
        sb.append("\"\t");
        sb.append(serviceReverse2);

        return sb.toString();
    }
}
