/*
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ , MEDDTL (GS)
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
package ppol.jdonref.referentiel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import ppol.jdonref.AGestionLogs;
import ppol.jdonref.Algos;
//import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.Tables.GestionTables;
import ppol.jdonref.dao.PayPaysBean;
import ppol.jdonref.dao.RecherchesDao;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.mots.RefCle;
import ppol.jdonref.mots.RefNumero;
import ppol.jdonref.mots.RefTypeVoie;
import ppol.jdonref.utils.DateUtils;

/**
 * Contient les méthodes utiles à la validation d'adresse.<br>
 * Le retour des méthodes valideXXX est un tableau dont le premier élément indique la nature du traitement effectué:
 * <ul>
 *     <li>0 en cas d'erreur</li>
 *     <li>1 s'il s'agit d'une recherche de voie exacte.</li>
 *     <li>2 s'il s'agit d'une recherche de voie par approximation.</li>
 *     <li>3 s'il s'agit d'une recherche de commune exacte.</li>
 *     <li>4 s'il s'agit d'une recherche de commune par approximation.</li>
 * </ul>
 * @author jmoquet
 * 
 * Patch GS: valideVoieCodePostal
 * Recherche par code postal (sans numéro de voie)
 * La validation est infructueuse suite à une mauvaise construction de la
 * requête SQL.
 */
public class GestionValidation {

    //private final static String un = "1";
    //private final static String deux = "2";
    private final static String trois = "3";
    private final static String quatre = "4";
    private final static String cinq = "5";
    private final static String six = "6";
    //private final static String voi_voies = "voi_voies_";
    GestionMots gestionMots = null;
    private JDONREFParams jdonrefParams;
    // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
    // private final SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE);
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;
    public static final int NOTE_MAX = 200;
    public static final int VALIDE_CODE_FONCTION = 0;
    public static final int VALIDE_NB_RES = 1;
    public static final int VALIDEPAYS_TABSIZE = 6;
    public static final int VALIDEPAYS_CODEAC3 = 8;
    public static final int VALIDEPAYS_LIGNE7 = 9;
    public static final int VALIDEPAYS_LIGNE7_DESABBREVIE = 10;
    public static final int VALIDEPAYS_NOTE = 11;
    public static final int VALIDEPAYS_T0 = 12;
    public static final int VALIDEPAYS_T1 = 13;
    public static final int VALIDECOMMUNE_TABSIZE = 6;
    public static final int VALIDECOMMUNE_CODEINSEE_NP = 7;
    public static final int VALIDECOMMUNE_LIGNE6_NP = 8;
    public static final int VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP = 9;
    public static final int VALIDECOMMUNE_NOTE_NP = 10;
    public static final int VALIDECOMMUNE_T0_NP = 11;
    public static final int VALIDECOMMUNE_T1_NP = 12;
    public final static int VALIDEVOIE_TABSIZE = 11;
    public final static int VALIDEVOIE_ID = 6;
    public final static int VALIDEVOIE_LIGNE4 = 7;
    public final static int VALIDEVOIE_LIGNE4_DESABBREVIE = 8;
    public final static int VALIDEVOIE_CODEINSEE = 9;
    public final static int VALIDEVOIE_LIGNE6 = 10;
    public final static int VALIDEVOIE_LIGNE6_DESABBREVIE = 11;
    public final static int VALIDEVOIE_T0 = 12;
    public final static int VALIDEVOIE_T1 = 13;
    public final static int VALIDEVOIE_NOTE = 14;
    public final static int VALIDEVOIE_FANTOIR = 15;
    public final static int VALIDEVOIE_SERVICE = 16;

    public GestionMots getGestionMots() {
        return gestionMots;
    }

    public void setGestionMots(GestionMots gestionMots) {
        this.gestionMots = gestionMots;
    }

    public JDONREFParams getJdonrefParams() {
        return jdonrefParams;
    }

    public void setJdonrefParams(JDONREFParams jdonrefParams) {
        this.jdonrefParams = jdonrefParams;
    }

    /**
     * Valide le pays spécifié jusque la date sélectionnée.<br>
     * La recherche est effectuée en deux étapes:
     * La première effectue une recherche par correspondance exacte</li>
     * Si elle n'est pas concluante, une recherche phonétique orthographique est effectuée.</li></ul>
     * La première étape peut être ignorée si le paramétre force est à true.<br>
     * Si la date est null, la date actuelle est utilisée.<br>
     * Utilise un algorithme de levenstein dont la distance à la chaine peut être spécifiée avec JDONREFParams.<br>
     * Retourne une liste dont le premier paramêtre est le nombre de pays trouvés.<br>
     * Retourne un tableau de la forme (taille 2+6*n):
     * <ul><li>5 ou 6 selon que la recherche est exacte ou non</li>
     *     <li>Nombre de pays retournés</li>
     *     <li>ligne1</li>
     *     <li>ligne2</li>
     *     <li>ligne3</li>
     *     <li>ligne4</li>
     *     <li>ligne5</li>
     *     <li>ligne6</li>
     *     <li>CodeSovAc3 1</li>
     *     <li>ligne7 1</li>
     *     <li>ligne7 desabbrevie 1</li>
     *     <li>Note sur 200 1</li>
     *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
     *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
     *     <li>CodeSovAc3 2</li>
     *     <li>lign7 2</li>
     *     <li>...</li></ul>
     */
    public String[] validePays(int application, String lignes[], Date dt, boolean force, Connection connection) throws SQLException {
        boolean rechercheexacte = true;
        String ligne7 = lignes[6];
        if (dt == null) {
            dt = Calendar.getInstance().getTime();
        }

        // Recherche exacte puis levensteinisee
        List<PayPaysBean> paysFound = null;
        if (!force) {
            paysFound = RecherchesDao.foundPaysByNameAtDate(connection, ligne7, dt, jdonrefParams.obtientNombreDePaysParDefaut());
        }
        if ((paysFound == null) || (paysFound.size() == 0)) // Pas de resultat exact ou force.
        {
            rechercheexacte = false;
            paysFound = RecherchesDao.foundPaysByLevAtDate(connection, ligne7, dt, jdonrefParams.obtientSeuilDeCorrespondanceDeCommune(),
                    jdonrefParams.obtientNombreDePaysParDefaut());
        }

        String[] res = formateResultForPays(lignes, paysFound, rechercheexacte);
//        GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_PAYS, true);
        jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_PAYS, true);
        return res;
    }

    /**
     * Formate le resultat en sortie pour les pays
     *     code sov_ac3 1
     *     ligne7 1
     *     ligne7 desabbrevie 1
     *     Note sur 200 1
     *     t0 1 sous la forme JJ/MM/AAAA
     *     t1 1 sous la forme JJ/MM/AAAA
     * @param lignes
     * @param pays
     * @param rechercheexacte
     * @return
     */
    private String[] formateResultForPays(String lignes[], List<PayPaysBean> pays, boolean rechercheexacte) {
        // FORMATE LE RESULTAT EN SORTIE.
        // Si des pays sont trouvées
        /*    <li>code sov_ac3 1</li>
         *     <li>ligne7 1</li>
         *     <li>ligne7 desabbrevie 1</li>
         *     <li>Note sur 200 1</li>
         *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
         *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
         */
        String[] res = new String[VALIDEPAYS_CODEAC3 + VALIDEPAYS_TABSIZE * pays.size()];
        res[0] = rechercheexacte ? cinq : six;
        res[1] = Integer.toString(pays.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[3];
        res[6] = lignes[4];
        res[7] = lignes[5];
        for (int i = 0,  offset = 0; i < pays.size(); i++, offset += VALIDEPAYS_TABSIZE) {
            PayPaysBean bean = pays.get(i);
            res[offset + VALIDEPAYS_CODEAC3] = bean.getSovAc3();
            res[offset + VALIDEPAYS_LIGNE7] = bean.getNomFr();
            res[offset + VALIDEPAYS_LIGNE7_DESABBREVIE] = bean.getNomFrDesab();
            res[offset + VALIDEPAYS_NOTE] = Integer.toString(bean.getNote());
            res[offset + VALIDEPAYS_T0] = DateUtils.formatDateToString(bean.getT0(), sdformat);
            res[offset + VALIDEPAYS_T1] = DateUtils.formatDateToString(bean.getT1(), sdformat);
        }
        return res;
    }

    /**
     * Inserre la 7eme ligne du paysen insertionIndex dans un tableau de resultal.
     * @param src
     * @param insertionIndex
     * @param pays
     * @return
     */
    private String[] insertPaysIntoRes(String[] src, int insertionIndex, String pays) {
        int srcSize = src.length;
        String[] dest = new String[srcSize + 1];

        for (int i = 0; i < insertionIndex; i++) {
            dest[i] = src[i];
        }
        dest[insertionIndex] = pays;
        for (int i = insertionIndex + 1; i < srcSize + 1; i++) {
            dest[i] = src[i - 1];
        }
        return dest;
    }
    private final static String valideCommune_psTime_0 = "SELECT t0,t1 FROM com_communes WHERE com_code_insee=? AND com_nom=? AND t0<=?";
    private final static String valideCommune_psChercheExact_0 = "SELECT DISTINCT com_communes.com_code_insee,com_communes.com_nom,cdp_codes_postaux.cdp_code_postal,note_commune(?,com_communes.com_nom_pq,?,cdp_code_postal,com_code_insee_commune is null) AS note, com_nom_desab ";
    private final static String valideCommune_psChercheExact_1 = "SELECT DISTINCT com_communes.com_code_insee,com_communes.com_nom,cdp_codes_postaux.cdp_code_postal,note_commune(?,com_communes.com_nom_pq,cdp_code_postal,cdp_code_postal,com_code_insee_commune is null) AS note, com_nom_desab ";
    private final static String valideCommune_psChercheExact_2 = "FROM com_communes,cdp_codes_postaux WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND com_nom_desab=? AND com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0)";
    private final static String valideCommune_psChercheExact_3 = " AND substr(com_communes.com_code_insee,4,2)=? ";
    private final static String valideCommune_psChercheExact_4 = " AND (";
    private final static String valideCommune_psChercheExact_5 = " OR ";
    private final static String valideCommune_psChercheExact_6 = "cdp_codes_postaux.dpt_code_departement=?";
    private final static String valideCommune_psChercheExact_7 = " )";
    private final static String valideCommune_psChercheExact_8 = "ORDER BY note DESC LIMIT ?";
    
    
    private final static String valideCommune_psCherche_0 = "SELECT DISTINCT com_communes.com_code_insee,com_communes.com_nom,cdp_codes_postaux.cdp_code_postal,note_commune(?,com_communes.com_nom_pq,?,cdp_code_postal,com_code_insee_commune is null) AS note, com_nom_desab ";
    private final static String valideCommune_psCherche_1 = "SELECT DISTINCT com_communes.com_code_insee,com_communes.com_nom,cdp_codes_postaux.cdp_code_postal,note_commune(?,com_communes.com_nom_pq,cdp_code_postal,cdp_code_postal,com_code_insee_commune is null) AS note, com_nom_desab ";
    private final static String valideCommune_psCherche_2 = "FROM com_communes,cdp_codes_postaux WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND ";
    private final static String valideCommune_psCherche_3 = "note_commune(?,com_nom_pq,?,cdp_code_postal,com_code_insee_commune is null)>=? AND ";
    private final static String valideCommune_psCherche_4 = "note_commune(?,com_nom_pq,cdp_code_postal,cdp_code_postal,com_code_insee_commune is null)>=? AND ";
    private final static String valideCommune_psCherche_5 = "com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0)";
    private final static String valideCommune_psCherche_6 = " AND (";
    private final static String valideCommune_psCherche_7 = " OR ";
    private final static String valideCommune_psCherche_8 = "cdp_codes_postaux.dpt_code_departement=?";
    private final static String valideCommune_psCherche_9 = " )";
    private final static String valideCommune_psCherche_10 = " ORDER BY note DESC LIMIT ?";

    public String[] valideCommune(int application, String lignes[], RefCle rccommune, Date dt, boolean force, boolean gererPays, String pays,
            Connection connection) throws SQLException {
        return valideCommune(application, lignes, rccommune, dt, force, gererPays, pays, connection, "");
    }
    
    /**
     * Valide la commune spécifiée jusque la date sélectionnée.<br>
     * La recherche est effectuée en deux étapes:
     * <ul><li>La première effectue une recherche par correspondance exacte phonétique.</li>
     * <li>Si elle n'est pas concluante, une recherche phonétique orthographique est effectuée.</li></ul>
     * La première étape peut être ignorée si le paramétre force est à true.<br>
     * Si la date est null, la date actuelle est utilisée.<br>
     * Utilise un algorithme de levenstein dont la distance à la chaine peut être spécifiée avec JDONREFParams.<br>
     * Retourne une liste dont le premier paramêtre est le nombre de communes trouvées.<br>
     * Retourne un tableau de la forme (taille 2+6*n):
     * <ul><li>3 ou 4 selon que la recherche est exacte ou non</li>
     *     <li>Nombre de communes retournées</li>
     *     <li>ligne1</li>
     *     <li>ligne2</li>
     *     <li>ligne3</li>
     *     <li>ligne4</li>
     *     <li>ligne5</li>
     *     <li>CodeInsee 1</li>
     *     <li>ligne6 1</li>
     *     <li>ligne6 desabbrevie 1</li>
     *     <li>Note sur 20 1</li>
     *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
     *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
     *     <li>CodeInsee 2</li>
     *     <li>lign6 2</li>
     *     <li>...</li></ul>
     */
    public String[] valideCommune(int application, String lignes[], RefCle rccommune, Date dt, boolean force, boolean gererPays, String pays,
            Connection connection,String restriction_departements) throws SQLException {
        String ligne6 = lignes[5];
        if (dt == null) {
            dt = Calendar.getInstance().getTime();
        }

        if (rccommune == null) {
            rccommune = gestionMots.trouveNomVille(ligne6, null);
        }
        String commune = rccommune.obtientMot();
        String commune_phonetique = Algos.phonexNonVide(commune);

        String arrondissement = gestionMots.trouveNumeroArrondissement(ligne6, rccommune).obtientMot();
        if (arrondissement.length() > 0) {
            arrondissement = Algos.deuxChiffresObligatoires(arrondissement);
        } else {
            arrondissement = null;
        }
        
        String[] dpts;
        if (restriction_departements!=null && restriction_departements.length()>0)
            dpts = restriction_departements.split(",");
        else
            dpts = null;

        ArrayList<String> communes = new ArrayList<String>();
        ArrayList<String> communes_desabbrevie = new ArrayList<String>();
        ArrayList<String> codepostal = new ArrayList<String>();
        ArrayList<String> codeinsee = new ArrayList<String>();
        ArrayList<String> notes = new ArrayList<String>();
        ArrayList<Date> date0 = new ArrayList<Date>();
        ArrayList<Date> date1 = new ArrayList<Date>();

        // Prépare la requête permettant d'obtenir les compléments d'information nécessaires
        PreparedStatement psTime = connection.prepareStatement(valideCommune_psTime_0);
        Timestamp tsdate = new Timestamp(dt.getTime());

        PreparedStatement psChercheExact = null;
        ResultSet rsChercheExact = null;

        boolean rechercheexacte = false;

        StringBuilder sb = new StringBuilder();

        // Prépare la requête permettant de chercher phonétiquement les communes dont le nom s'approche du nom spécifié.
        if (!force) {
            sb.setLength(0);
            if (arrondissement != null) {
                sb.append(valideCommune_psChercheExact_0);
            } else {
                sb.append(valideCommune_psChercheExact_1);
            }
            sb.append(valideCommune_psChercheExact_2);
            if (arrondissement != null) {
                sb.append(valideCommune_psChercheExact_3);
            }
            if (dpts!=null)
            {
                sb.append(valideCommune_psChercheExact_4);
                for(int i=0;i<dpts.length;i++)
                {
                    if (i>0) sb.append(valideCommune_psChercheExact_5);
                    sb.append(valideCommune_psChercheExact_6);
                }
                sb.append(valideCommune_psChercheExact_7);
            }
            
            sb.append(valideCommune_psChercheExact_8);
            
            
            
            psChercheExact = connection.prepareStatement(sb.toString());

            // note_commune(?,nom,?,cdp_code_postal,com_code_insee_commune is null)
            int index = 1;
            psChercheExact.setString(index++, commune_phonetique);
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            // com_nom_pq=? AND 
            psChercheExact.setString(index++, commune);

            // com_communes.t0<=? AND cdp_codes_postaux.t0<=?
            psChercheExact.setTimestamp(index++, tsdate);
            psChercheExact.setTimestamp(index++, tsdate);

            // substr(com_communes.com_code_insee,3,2)=?
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }
            
            if (dpts!=null)
                for(String dpt : dpts)
                {
                    psChercheExact.setString(index++, dpt.trim());
                }
            
            // LIMIT ?
            psChercheExact.setInt(index++, jdonrefParams.obtientNombreDeCommuneParDefaut());

            rsChercheExact = psChercheExact.executeQuery();
        }

        if (!force && rsChercheExact.next()) {
            rechercheexacte = true;
            do {
                communes.add(rsChercheExact.getString(2));
                codepostal.add(rsChercheExact.getString(3));
                codeinsee.add(rsChercheExact.getString(1));
                notes.add(Integer.toString(rsChercheExact.getInt(4)));
                communes_desabbrevie.add(rsChercheExact.getString(5));
            } while (rsChercheExact.next());

            rsChercheExact.close();
            psChercheExact.close();
        } else {
            if (rsChercheExact != null) {
                rsChercheExact.close();
                psChercheExact.close();
            }

            // Prépare la requête permettant de chercher les communes dont le nom s'approche du nom spécifié.
            sb.setLength(0);
            if (arrondissement != null) {
                sb.append(valideCommune_psCherche_0);
            } else {
                sb.append(valideCommune_psCherche_1);
            }
            sb.append(valideCommune_psCherche_2);
            if (arrondissement != null) {
                sb.append(valideCommune_psCherche_3);
            } else {
                sb.append(valideCommune_psCherche_4);
            }
            sb.append(valideCommune_psCherche_5);
            if (dpts!=null)
            {
                sb.append(valideCommune_psCherche_6);
                for(int i=0;i<dpts.length;i++)
                {
                    if (i>0) sb.append(valideCommune_psCherche_7);
                    sb.append(valideCommune_psCherche_8);
                }
                sb.append(valideCommune_psCherche_9);
            }
            sb.append(valideCommune_psCherche_10);
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());

            //
            // CHERCHE LES COMMUNES DONT LE NOM S'APPROCHE DU NOM PROPOSE
            //
            int index = 1;
            // note_commune(?,nom,?,cdp_code_postal,com_code_insee_commune is null)
            psCherche.setString(index++, commune_phonetique);
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            // note_commune(?,nom,?,cdp_code_postal,com_code_insee_commune is null)>=?
            }
            psCherche.setString(index++, commune_phonetique);
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }
            psCherche.setInt(index++, jdonrefParams.obtientSeuilDeCorrespondanceDeCommune());

            // com_communes.t0<=? AND cdp_codes_postaux.t0<=?
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            if (dpts!=null)
                for(String dpt : dpts)
                {
                    psCherche.setString(index++, dpt.trim());
                }
            
            // LIMIT ?
            psCherche.setInt(index++, jdonrefParams.obtientNombreDeCommuneParDefaut());

            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                communes.add(rsCherche.getString(2));
                codepostal.add(rsCherche.getString(3));
                codeinsee.add(rsCherche.getString(1));
                notes.add(Integer.toString(rsCherche.getInt(4)));
                communes_desabbrevie.add(rsCherche.getString(5));
            }

            rsCherche.close();
            psCherche.close();
        }

        psTime.setTimestamp(3, tsdate);

        //
        // POUR CHAQUE PROPOSITION TROUVEE, NE CONSERVE QUE LA DERNIERE MISE A JOUR.
        //
        for (int i = 0; i < communes.size(); i++) {
            psTime.setString(1, codeinsee.get(i));
            psTime.setString(2, communes.get(i));

            ResultSet rsTime = psTime.executeQuery();

            if (rsTime.next()) {
                long dt0 = rsTime.getTimestamp(1).getTime();
                long dt1 = rsTime.getTimestamp(2).getTime();
                do {
                    long tdt1 = rsTime.getTimestamp(2).getTime();
                    if (tdt1 > dt1) {
                        dt0 = rsTime.getTimestamp(1).getTime(); // tdt0
                        dt1 = tdt1;
                    }
                } while (rsTime.next());

                date0.add(new Date(dt0));
                date1.add(new Date(dt1));
            } else {
                communes.remove(i);
                codepostal.remove(i);
                codeinsee.remove(i);
                notes.remove(i);
                communes_desabbrevie.remove(i);
                i--;
            }

            rsTime.close();
        }
        psTime.close();

        //
        // FORMATE LE RESULTAT EN SORTIE.
        //
        // Si des communes sont trouvées
        /*    <li>CodeInsee 1</li>
         *     <li>ligne6 1</li>
         *     <li>ligne6 desabbrevie 1</li>
         *     <li>Note sur 20 1</li>
         *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
         *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
         */
        String[] res = new String[VALIDECOMMUNE_CODEINSEE_NP + communes.size() * 6];
        res[0] = rechercheexacte ? trois : quatre;
        res[1] = Integer.toString(communes.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[3];
        res[6] = lignes[4];
        for (int i = 0,  offset = 0; i < communes.size(); i++, offset += VALIDECOMMUNE_TABSIZE) {
            res[offset + VALIDECOMMUNE_CODEINSEE_NP] = codeinsee.get(i);
            res[offset + VALIDECOMMUNE_LIGNE6_NP] = Algos.unsplit(codepostal.get(i), communes.get(i)); // ligne6
            res[offset + VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP] = Algos.unsplit(codepostal.get(i), communes_desabbrevie.get(i)); // ligne6 desabbrevie
            res[offset + VALIDECOMMUNE_NOTE_NP] = notes.get(i);
            //////////////////////////////////////
            // WA 09/2011 DateFormat -> DateUtils
            // res[offset+VALIDECOMMUNE_T0] = sdformat.format(date0.get(i));
            // res[offset+VALIDECOMMUNE_T1] = sdformat.format(date1.get(i));
            res[offset + VALIDECOMMUNE_T0_NP] = DateUtils.formatDateToString(date0.get(i), sdformat);
            res[offset + VALIDECOMMUNE_T1_NP] = DateUtils.formatDateToString(date1.get(i), sdformat);
        }
        if (gererPays) {
            res = insertPaysIntoRes(res, 7, pays);
        }

        jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_COMMUNE, true);
        return res;
    }
    
    private final static String valideCommuneEtCodePostal_psTime_0 = "SELECT communes.t0,communes.t1,cdp_codes_postaux.t0,cdp_codes_postaux.t1 FROM com_communes as communes,cdp_codes_postaux WHERE communes.com_code_insee=cdp_codes_postaux.com_code_insee AND communes.com_code_insee=? AND com_nom=? AND cdp_codes_postaux.cdp_code_postal=? AND communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<communes.t0)";
    private final static String valideCommuneEtCodePostal_psChercheExact_0 = "SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal_commune(?,com_nom_pq,?,cdp_code_postal,?,com_code_insee_commune IS null) AS note, com_nom_desab ";
    private final static String valideCommuneEtCodePostal_psChercheExact_1 = "SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal_commune(?,com_nom_pq,?,cdp_code_postal,cdp_code_postal,com_code_insee_commune IS null) AS note, com_nom_desab ";
    private final static String valideCommuneEtCodePostal_psChercheExact_2 = "FROM com_communes,cdp_codes_postaux WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND ";
    private final static String valideCommuneEtCodePostal_psChercheExact_3 = "cdp_code_postal=? AND ";
    private final static String valideCommuneEtCodePostal_psChercheExact_4 = "substr(cdp_code_postal,4,2)=? AND ";
    private final static String valideCommuneEtCodePostal_psChercheExact_5 = "com_nom_desab=? AND com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0) ";
    private final static String valideCommuneEtCodePostal_psChercheExact_6 = " AND (";
    private final static String valideCommuneEtCodePostal_psChercheExact_7 = " OR ";
    private final static String valideCommuneEtCodePostal_psChercheExact_8 = "cdp_codes_postaux.dpt_code_departement=?";
    private final static String valideCommuneEtCodePostal_psChercheExact_9 = " )";       
    private final static String valideCommuneEtCodePostal_psChercheExact_10 = "ORDER BY note DESC LIMIT ?";
    
    private final static String valideCommuneEtCodePostal_psCherche_0 = "SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal_commune(?,com_nom_pq,?,cdp_code_postal,?,com_code_insee_commune is null) as note, com_nom_desab ";
    private final static String valideCommuneEtCodePostal_psCherche_1 = "SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal_commune(?,com_nom_pq,?,cdp_code_postal,cdp_code_postal,com_code_insee_commune is null) as note, com_nom_desab ";
    private final static String valideCommuneEtCodePostal_psCherche_2 = "FROM com_communes,cdp_codes_postaux WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND ";
    private final static String valideCommuneEtCodePostal_psCherche_3 = "com_communes.dpt_code_departement=? AND ";
    private final static String valideCommuneEtCodePostal_psCherche_4 = "note_codepostal_commune(?,com_nom_pq,?,cdp_code_postal,?,com_code_insee_commune is null)>=? AND ";
    private final static String valideCommuneEtCodePostal_psCherche_5 = "note_codepostal_commune(?,com_nom_pq,?,cdp_code_postal,cdp_code_postal,com_code_insee_commune is null)>=? AND ";
    private final static String valideCommuneEtCodePostal_psCherche_6 = "com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0) ";
    private final static String valideCommuneEtCodePostal_psCherche_7 = " AND (";
    private final static String valideCommuneEtCodePostal_psCherche_8 = " OR ";
    private final static String valideCommuneEtCodePostal_psCherche_9 = "cdp_codes_postaux.dpt_code_departement=?";
    private final static String valideCommuneEtCodePostal_psCherche_10 = " )";
    private final static String valideCommuneEtCodePostal_psCherche_11 = "ORDER BY note DESC LIMIT ?";

    public String[] valideCommuneEtCodePostal(int application, String[] lignes, RefCle rccodepostal, RefCle rccommune, Date date,
            boolean force, boolean gererPays, String pays, Connection connection) throws SQLException {
        return valideCommuneEtCodePostal(application, lignes, rccodepostal, rccommune, date, force, gererPays, pays, connection, "");
    }
    
    /**
     * Valide le couple commune et code postal spécifié.<br>
     * La recherche est effectuée en deux étapes:
     * <ul><li>Dans un premier temps, une recherche phonétique est effectuée.</li>
     * <li>Si elle n'est pas concluante, une recherche phonétique et ortographique est effectueé.</li>
     * </ul>
     * La seconde étape peut être forcée avec le paramètre force.<br>
     * Utilise un algorithme de levenstein dont la distance à la chaine peut être spécifiée avec JDONREFParams.<br>
     * Retourne une liste dont le premier paramêtre est le nombre de communes trouvées.<br>
     * Retourne un tableau de la forme (taille 2+6*n):
     * <ul><li>3 ou 4 selon que la recherche est exacte ou non</li>
     *     <li>Nombre de communes retournées</li>
     *     <li>ligne1</li>
     *     <li>ligne2</li>
     *     <li>ligne3</li>
     *     <li>ligne4</li>
     *     <li>ligne5</li>
     *     <li>CodeInsee 1</li>
     *     <li>ligne6 1</li>
     *     <li>ligne6 desabbrevie 1</li>
     *     <li>Note sur 20 1</li>
     *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
     *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
     *     <li>CodeInsee 2</li>
     *     <li>Commune 2</li>
     *     <li>...</li></ul>
     */
    public String[] valideCommuneEtCodePostal(int application, String[] lignes, RefCle rccodepostal, RefCle rccommune, Date date,
            boolean force, boolean gererPays, String pays, Connection connection,String restriction_departements) throws SQLException {
        String ligne6 = lignes[5];
        if (rccodepostal == null) {
            rccodepostal = gestionMots.trouveCodePostal(ligne6);
        }
        if (rccommune == null) {
            rccommune = gestionMots.trouveNomVille(ligne6, rccodepostal);
        }

        String commune = rccommune.obtientMot();
        String commune_phonetique = Algos.phonexNonVide(commune);
        String cdp_code_postal = rccodepostal.obtientMot();
        String arrondissement = gestionMots.trouveNumeroArrondissement(ligne6, rccommune).obtientMot();
        if (arrondissement.length() > 0) {
            arrondissement = Algos.deuxChiffresObligatoires(arrondissement);
        } else {
            arrondissement = null;
        }
        String code_departement = gestionMots.trouveCodeDepartement(cdp_code_postal).obtientMot();

        String[] dpts;
        if (restriction_departements!=null && restriction_departements.length()>0)
            dpts = restriction_departements.split(",");
        else
            dpts = null;
        
        ArrayList<String> communes = new ArrayList<String>();
        ArrayList<String> communes_desabbrevie = new ArrayList<String>();
        ArrayList<String> codepostal = new ArrayList<String>();
        ArrayList<String> codeinsee = new ArrayList<String>();
        ArrayList<String> notes = new ArrayList<String>();
        ArrayList<String> t0 = new ArrayList<String>();
        ArrayList<String> t1 = new ArrayList<String>();

        // Prépare la requête permettant d'obtenir les dernieres mise à jour pour chaque proposition
        StringBuilder sb = new StringBuilder();
        PreparedStatement psTime = connection.prepareStatement(valideCommuneEtCodePostal_psTime_0);

        ResultSet rsChercheExact = null;
        PreparedStatement psChercheExact = null;
        Timestamp tsdate = new Timestamp(date.getTime());

        boolean rechercheexacte = false;

        if (!force) {
            sb.setLength(0);
            if (arrondissement != null) {
                sb.append(valideCommuneEtCodePostal_psChercheExact_0);
            } else {
                sb.append(valideCommuneEtCodePostal_psChercheExact_1);
            }
            sb.append(valideCommuneEtCodePostal_psChercheExact_2);
            if (cdp_code_postal.length() == 5) {
                sb.append(valideCommuneEtCodePostal_psChercheExact_3);
            }
            if (arrondissement != null) {
                sb.append(valideCommuneEtCodePostal_psChercheExact_4);
            }
            sb.append(valideCommuneEtCodePostal_psChercheExact_5);
            if (dpts!=null)
            {
                sb.append(valideCommuneEtCodePostal_psChercheExact_6);
                for(int i=0;i<dpts.length;i++)
                {
                    if (i>0) sb.append(valideCommuneEtCodePostal_psChercheExact_7);
                    sb.append(valideCommuneEtCodePostal_psChercheExact_8);
                }
                sb.append(valideCommuneEtCodePostal_psChercheExact_9);
            }
            sb.append(valideCommuneEtCodePostal_psChercheExact_10);

            psChercheExact = connection.prepareStatement(sb.toString());

            // note_codepostal_commune(?,nom_pq,?,cdp_code_postal,?,com_code_insee_commune is null)
            int index = 1;
            psChercheExact.setString(index++, commune_phonetique);
            psChercheExact.setString(index++, cdp_code_postal);
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            // cdp_code_postal=? and com_nom_pq=? AND 
            if (cdp_code_postal.length() == 5) {
                psChercheExact.setString(index++, cdp_code_postal);
            }
            // substr(cdp_code_postal,4,2)=? AND 
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            psChercheExact.setString(index++, commune);

            // com_communes.t0<=? AND cdp_codes_postaux.t0<=?
            psChercheExact.setTimestamp(index++, tsdate);
            psChercheExact.setTimestamp(index++, tsdate);

            if (dpts!=null)
                for(String dpt : dpts)
                {
                    psChercheExact.setString(index++, dpt.trim());
                }
            
            // LIMIT ?
            psChercheExact.setInt(index++, jdonrefParams.obtientNombreDeCommuneParDefaut());

            rsChercheExact = psChercheExact.executeQuery();
        }

        if (!force && rsChercheExact.next()) {
            rechercheexacte = true;
            do {
                communes.add(rsChercheExact.getString(1));
                codepostal.add(rsChercheExact.getString(2));
                codeinsee.add(rsChercheExact.getString(3));
                notes.add(Integer.toString(rsChercheExact.getInt(4)));
                communes_desabbrevie.add(rsChercheExact.getString(5));
            } while (rsChercheExact.next());

            rsChercheExact.close();
            psChercheExact.close();
        } else {
            if (rsChercheExact != null) {
                rsChercheExact.close();
                psChercheExact.close();
            }
            sb.setLength(0);
            if (arrondissement != null) {
                sb.append(valideCommuneEtCodePostal_psCherche_0);
            } else {
                sb.append(valideCommuneEtCodePostal_psCherche_1);
            }
            sb.append(valideCommuneEtCodePostal_psCherche_2);
            if (code_departement.length() == 2) {
                sb.append(valideCommuneEtCodePostal_psCherche_3);
            }
            if (arrondissement != null) {
                sb.append(valideCommuneEtCodePostal_psCherche_4);
            } else {
                sb.append(valideCommuneEtCodePostal_psCherche_5);
            }
            sb.append(valideCommuneEtCodePostal_psCherche_6);
            
            if (dpts!=null)
            {
                sb.append(valideCommuneEtCodePostal_psCherche_7);
                for(int i=0;i<dpts.length;i++)
                {
                    if (i>0) sb.append(valideCommuneEtCodePostal_psCherche_8);
                    sb.append(valideCommuneEtCodePostal_psCherche_9);
                }
                sb.append(valideCommuneEtCodePostal_psCherche_10);
            }
            
            sb.append(valideCommuneEtCodePostal_psCherche_11);

            PreparedStatement psCherche = connection.prepareStatement(sb.toString());

            //
            // RECHERCHE LES COMMUNES DE NOM ET CODE POSTAL SIMILAIRE A CEUX PROPOSES
            //       
            int index = 1;
            // note_codepostal_commune(?,nom_pq,?,cdp_code_postal,?,com_code_insee_commune is null)
            psCherche.setString(index++, commune_phonetique);
            psCherche.setString(index++, cdp_code_postal);
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }

            // com_communes.dpt_code_departement=? AND
            if (code_departement.length() == 2) {
                psCherche.setString(index++, code_departement);
            }

            // note_codepostal_commune(?,nom_pq,?,cdp_code_postal,?,com_code_insee_commune is null)>=?
            psCherche.setString(index++, commune_phonetique);
            psCherche.setString(index++, cdp_code_postal);
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }
            psCherche.setInt(index++,
                    5 * (jdonrefParams.obtientSeuilDeCorrespondanceDeCodePostal() + jdonrefParams.obtientSeuilDeCorrespondanceDeCommune()));

            // com_communes.t0<=? AND cdp_codes_postaux.t0<=?
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            if (dpts!=null)
                for(String dpt : dpts)
                {
                    psCherche.setString(index++, dpt.trim());
                }
            
            // LIMIT ?
            psCherche.setInt(index++, jdonrefParams.obtientNombreDeCommuneParDefaut());

            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                communes.add(rsCherche.getString(1));
                codepostal.add(rsCherche.getString(2));
                codeinsee.add(rsCherche.getString(3));
                notes.add(Integer.toString(rsCherche.getInt(4)));
                communes_desabbrevie.add(rsCherche.getString(5));
            }

            rsCherche.close();
            psCherche.close();
        }

        psTime.setTimestamp(4, tsdate);
        psTime.setTimestamp(5, tsdate);

        //
        // POUR CHAQUE SOLUTION TROUVE, CONSERVE LES DERNIERES MISES A JOUR.
        //
        for (int i = 0; i < communes.size(); i++) {
            psTime.setString(1, codeinsee.get(i));
            psTime.setString(2, communes.get(i));
            psTime.setString(3, codepostal.get(i));
            ResultSet rsTime = psTime.executeQuery();

            if (rsTime.next()) {
                long dt0 = 0;
                long dt1 = 0;
                long dtv0 = rsTime.getTimestamp(1).getTime();
                long dtv1 = rsTime.getTimestamp(2).getTime();
                long dtc0 = rsTime.getTimestamp(3).getTime();
                long dtc1 = rsTime.getTimestamp(4).getTime();

                // Il faut d'abord voir de qui, la commune ou la voie, a la durée de vie la plus restreinte.
                if (dtv0 < dtc0) {
                    dt0 = dtc0;
                } else {
                    dt0 = dtv0;
                }
                if (dtv1 < dtc1) {
                    dt1 = dtv1;
                } else {
                    dt1 = dtc1;
                }

                // Fait de même avec chacun et conserve l'intervalle le plus tardif.
                do {
                    long tdt0 = 0;
                    long tdt1 = 0;
                    long tdtv1 = rsTime.getTimestamp(2).getTime();
                    long tdtc1 = rsTime.getTimestamp(4).getTime();

                    if (tdtv1 < tdtc1) {
                        tdt1 = tdtv1;
                    } else {
                        tdt1 = tdtc1;
                    }

                    if (tdt1 > dt1) {
                        long tdtv0 = rsTime.getTimestamp(1).getTime();
                        long tdtc0 = rsTime.getTimestamp(3).getTime();

                        if (tdtv0 < tdtc0) {
                            tdt0 = tdtc0;
                        } else {
                            tdt0 = tdtv0;
                        }

                        dt0 = tdt0;
                        dt1 = tdt1;
                    }
                } while (rsTime.next());

                // WA 09/2011 DateFormat -> DateUtils
                // t0.add(sdformat.format(new Date(dt0)));
                // t1.add(sdformat.format(new Date(dt1)));
                t0.add(DateUtils.formatDateToString(new Date(dt0), sdformat));
                t1.add(DateUtils.formatDateToString(new Date(dt1), sdformat));
            } else {
                communes.remove(i);
                codepostal.remove(i);
                codeinsee.remove(i);
                notes.remove(i);
                communes_desabbrevie.remove(i);
                i--;
            }

            rsTime.close();
        }
        psTime.close();

        String[] res = new String[VALIDECOMMUNE_CODEINSEE_NP + communes.size() * 6];
        res[0] = rechercheexacte ? trois : quatre;
        res[1] = Integer.toString(communes.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[3];
        res[6] = lignes[4];
        for (int i = 0,  offset = 0; i < communes.size(); i++, offset += VALIDECOMMUNE_TABSIZE) {
            res[offset + VALIDECOMMUNE_CODEINSEE_NP] = codeinsee.get(i);
            res[offset + VALIDECOMMUNE_LIGNE6_NP] = Algos.unsplit(codepostal.get(i), communes.get(i));
            res[offset + VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP] = Algos.unsplit(codepostal.get(i), communes_desabbrevie.get(i));
            res[offset + VALIDECOMMUNE_NOTE_NP] = notes.get(i);
            res[offset + VALIDECOMMUNE_T0_NP] = t0.get(i);
            res[offset + VALIDECOMMUNE_T1_NP] = t1.get(i);
        }
        if (gererPays) {
            res = insertPaysIntoRes(res, 7, pays);
        }
        jdonrefParams.getGestionLog().logValidation(application, code_departement,
                AGestionLogs.FLAG_VALIDE_COMMUNE + AGestionLogs.FLAG_VALIDE_CODEPOSTAL, true);
        return res;
    }
    private final static String valideCodePostal_psTime_0 = "SELECT t0,t1 FROM cdp_codes_postaux WHERE com_code_insee=? AND cdp_code_postal=? AND t0<=?";
    private final static String valideCodePostal_psChercheLike_1 = "SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal(?,cdp_code_postal) as note,com_nom_desab FROM com_communes,cdp_codes_postaux WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND cdp_code_postal=? AND com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0)";
    private final static String valideCodePostal_psChercheLike_2 = " AND (";
    private final static String valideCodePostal_psChercheLike_3 = " OR ";
    private final static String valideCodePostal_psChercheLike_4 = "cdp_codes_postaux.dpt_code_departement=?";
    private final static String valideCodePostal_psChercheLike_5 = " )";
    private final static String valideCodePostal_psChercheLike_6 = " ORDER BY note DESC LIMIT ?";
    private final static String valideCodePostal_psCherche_0 = "SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal(?,cdp_code_postal) as note,com_nom_desab FROM com_communes,cdp_codes_postaux WHERE cdp_codes_postaux.dpt_code_departement = ? AND com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND note_codepostal(?,cdp_code_postal)>=? AND com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0)";
    private final static String valideCodePostal_psCherche_1 = " AND (";
    private final static String valideCodePostal_psCherche_2 = " OR ";
    private final static String valideCodePostal_psCherche_3 = "cdp_codes_postaux.dpt_code_departement=?";
    private final static String valideCodePostal_psCherche_4 = " )";
    private final static String valideCodePostal_psCherche_5 = " ORDER BY note DESC LIMIT ?";

    public String[] valideCodePostal(int application, String[] lignes, Date date, boolean force, boolean gererPays, String pays, Connection connection) throws SQLException {
        return valideCodePostal(application, lignes, date, force, gererPays, pays, connection, "");
    }
    
    /**
     * Valide le code postal spécifiée jusqu'à la date spécifiée.<br>
     * La recherche est effectuée en deux temps:
     * <ul><li>Une première recherche en comparaison exacte</li>
     *     <li>Une deuxième recherche au moyen d'un algorithme de levenstein
     * dont la distance à la chaine peut être spécifiée avec JDONREFParams.</li></ul>
     * La deuxième recherche n'est effectuée que si la première ne retourne aucun résultats.
     * Elle peut toutefois être forcée (la première étant ignorée au moyen du paramètre force.
     * La recherche de code postal porte uniquement sur le département associé au code postal.<br>
     * Retourne un tableau de la forme (taille 2+6*n):
     * <ul><li>3 ou 4 selon que la recherche est exacte ou non</li>
     *     <li>Nombre de propositions retournées</li>
     *     <li>ligne1</li>
     *     <li>ligne2</li>
     *     <li>ligne3</li>
     *     <li>ligne4</li>
     *     <li>ligne5</li>
     *     <li>CodeInsee 1</li>
     *     <li>Ligne6 1</li>
     *     <li>Ligne6 desabbrevie 1</li>
     *     <li>Note sur 20 1</li>
     *     <li>t0 1 sous la forme JJ/MM/AA</li>
     *     <li>t1 1 sous la forme JJ/MM/AA</li>
     *     <li>CodeInsee 2</li>
     *     <li>Commune 2</li>
     *     <li>...</li></ul>
     * @param force a true, la recherche exacte n'est pas effectuée.
     */
    public String[] valideCodePostal(int application, String[] lignes, Date date, boolean force, boolean gererPays, String pays, Connection connection,String restriction_departements) throws SQLException {
        String ligne6 = lignes[5];
        String cdp_code_postal = gestionMots.trouveCodePostal(ligne6).obtientMot();
        String codedepartement = gestionMots.trouveCodeDepartement(cdp_code_postal).obtientMot();
        String[] dpts;
        if (restriction_departements!=null && restriction_departements.length()>0)
            dpts = restriction_departements.split(",");
        else
            dpts = null;

        ResultSet rsChercheLike = null;
        PreparedStatement psChercheLike = null;

        Timestamp tsdate = new Timestamp(date.getTime());
        ArrayList<String> communes = new ArrayList<String>();
        ArrayList<String> communes_desabbrevie = new ArrayList<String>();
        ArrayList<String> codepostal = new ArrayList<String>();
        ArrayList<String> codeinsee = new ArrayList<String>();
        ArrayList<String> notes = new ArrayList<String>();
        ArrayList<String> t0 = new ArrayList<String>();
        ArrayList<String> t1 = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();

        // Prépare la requête permettant d'obtenir les dernieres mise à jour pour chaque proposition
        PreparedStatement psTime = connection.prepareStatement(valideCodePostal_psTime_0);

        boolean rechercheexacte = false;

        if (!force) {
            StringBuilder psChercheLike_sb = new StringBuilder();
            psChercheLike_sb.append(valideCodePostal_psChercheLike_1);
            if (dpts!=null)
            {
                psChercheLike_sb.append(valideCodePostal_psChercheLike_2);
                for(int i=0;i<dpts.length;i++)
                {
                    if (i>0) psChercheLike_sb.append(valideCodePostal_psChercheLike_3);
                    psChercheLike_sb.append(valideCodePostal_psChercheLike_4);
                }
                psChercheLike_sb.append(valideCodePostal_psChercheLike_5);
            }
            psChercheLike_sb.append(valideCodePostal_psChercheLike_6);
            
            // Prépare la requête permettant de chercher les communes dont le code postal
            // s'approche du code postal spécifié.
            //sb.setLength(0);
            //sb.append("SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal(?,cdp_code_postal) as note,com_nom_desab FROM com_communes,cdp_codes_postaux WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND cdp_code_postal=? AND com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0) ORDER BY note DESC LIMIT ?");
            psChercheLike = connection.prepareStatement(psChercheLike_sb.toString());
            
            int index = 1;
            // SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal(?,cdp_code_postal) as note,com_nom_desab 
            psChercheLike.setString(index++, cdp_code_postal);
            // cdp_code_postal like ? AND 
            psChercheLike.setString(index++, cdp_code_postal);
            
            // com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND
            psChercheLike.setTimestamp(index++, tsdate);
            psChercheLike.setTimestamp(index++, tsdate);
            
            
            if (dpts!=null)
            {
                for(String dpt : dpts)
                    psChercheLike.setString(index++,dpt.trim());
            }
            
            // ORDER BY note DESC LIMIT ?
            psChercheLike.setInt(index++, jdonrefParams.obtientNombreDeCommuneParDefaut());
            
            rsChercheLike = psChercheLike.executeQuery();
        }

        // Si des solutions avec l'opérateur = existent,
        if (!force && rsChercheLike.next()) {
            rechercheexacte = true;
            do {
                communes.add(rsChercheLike.getString(1));
                codepostal.add(rsChercheLike.getString(2));
                codeinsee.add(rsChercheLike.getString(3));
                notes.add(Integer.toString(rsChercheLike.getInt(4)));
                communes_desabbrevie.add(rsChercheLike.getString(5));
            } while (rsChercheLike.next());

            rsChercheLike.close();
            psChercheLike.close();
        } else // Sinon, effectue une recherche phonétique et orthographique.
        {
            if (rsChercheLike != null) {
                rsChercheLike.close();
                psChercheLike.close();
            }

            StringBuilder psCherche_sb = new StringBuilder();
            psCherche_sb.append(valideCodePostal_psCherche_0);
            if (dpts!=null)
            {
                psCherche_sb.append(valideCodePostal_psCherche_1);
                for(int i=0;i<dpts.length;i++)
                {
                    if (i>0) psCherche_sb.append(valideCodePostal_psCherche_2);
                    psCherche_sb.append(valideCodePostal_psCherche_3);
                }
                psCherche_sb.append(valideCodePostal_psCherche_4);
            }
            psCherche_sb.append(valideCodePostal_psCherche_5);
            
            // Prépare la requête permettant de chercher les communes dont le code postal s'approche du code postal spécifié.
            //sb.setLength(0);
            //sb.append("SELECT DISTINCT com_nom,cdp_code_postal,com_communes.com_code_insee,note_codepostal(?,cdp_code_postal) as note,com_nom_desab FROM com_communes,cdp_codes_postaux WHERE cdp_codes_postaux.dpt_code_departement = ? AND com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND note_codepostal(?,cdp_code_postal)>=? AND com_communes.t0<=? AND cdp_codes_postaux.t0<=? AND NOT (com_communes.t1<cdp_codes_postaux.t0 OR cdp_codes_postaux.t1<com_communes.t0) ORDER BY note DESC LIMIT ?");
            PreparedStatement psCherche = connection.prepareStatement(psCherche_sb.toString());

            int index = 1;

            // note_codepostal(?,cdp_code_postal) 
            psCherche.setString(index++, cdp_code_postal);

            // cdp_code_departement = ? AND 
            psCherche.setString(index++, codedepartement);

            // note_codepostal(?,cdp_code_postal)<=? 
            psCherche.setString(index++, cdp_code_postal);
            psCherche.setInt(index++, 10 * jdonrefParams.obtientSeuilDeCorrespondanceDeCodePostal());

            // com_communes.t0<=? AND cdp_codes_postaux.t0<=? 
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            if (dpts!=null)
            {
                for(String dpt : dpts)
                    psCherche.setString(index++,dpt.trim());
            }
            
            // LIMIT ? 
            psCherche.setInt(index++, jdonrefParams.obtientNombreDeCommuneParDefaut());
            
            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                communes.add(rsCherche.getString(1));
                codepostal.add(rsCherche.getString(2));
                codeinsee.add(rsCherche.getString(3));
                notes.add(Integer.toString(rsCherche.getInt(4)));
                communes_desabbrevie.add(rsCherche.getString(5));
            }

            rsCherche.close();
            psCherche.close();
        }

        psTime.setTimestamp(3, tsdate);

        //
        // POUR CHAQUE SOLUTION TROUVE, CONSERVE LES DERNIERES MISES A JOUR.
        //
        for (int i = 0; i < communes.size(); i++) {
            psTime.setString(1, codeinsee.get(i));
            psTime.setString(2, codepostal.get(i));
            ResultSet rsTime = psTime.executeQuery();

            if (rsTime.next()) {
                long dt0 = rsTime.getTimestamp(1).getTime();
                long dt1 = rsTime.getTimestamp(2).getTime();
                do {
                    long tdt1 = rsTime.getTimestamp(2).getTime();
                    if (tdt1 > dt1) {
                        dt0 = rsTime.getTimestamp(1).getTime(); // tdt0
                        dt1 = tdt1;
                    }
                } while (rsTime.next());

                // WA 09/2011 DateFormat -> DateUtils
                // t0.add(sdformat.format(new Date(dt0)));
                // t1.add(sdformat.format(new Date(dt1)));
                t0.add(DateUtils.formatDateToString(new Date(dt0), sdformat));
                t1.add(DateUtils.formatDateToString(new Date(dt1), sdformat));
            } else {
                communes.remove(i);
                codepostal.remove(i);
                codeinsee.remove(i);
                notes.remove(i);
                i--;
            }
            rsTime.close();
        }
        psTime.close();

        // Si des communes sont trouvées
        String[] res = new String[VALIDECOMMUNE_CODEINSEE_NP + communes.size() * 6];
        res[0] = rechercheexacte ? "3" : "4";
        res[1] = Integer.toString(communes.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[3];
        res[6] = lignes[4];
        for (int i = 0; i < communes.size(); i++) {
            res[VALIDECOMMUNE_TABSIZE * i + VALIDECOMMUNE_CODEINSEE_NP] = codeinsee.get(i);
            res[VALIDECOMMUNE_TABSIZE * i + VALIDECOMMUNE_LIGNE6_NP] = Algos.unsplit(codepostal.get(i), communes.get(i));
            res[VALIDECOMMUNE_TABSIZE * i + VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP] = Algos.unsplit(codepostal.get(i), communes_desabbrevie.get(i));
            res[VALIDECOMMUNE_TABSIZE * i + VALIDECOMMUNE_NOTE_NP] = notes.get(i);
            res[VALIDECOMMUNE_TABSIZE * i + VALIDECOMMUNE_T0_NP] = t0.get(i);
            res[VALIDECOMMUNE_TABSIZE * i + VALIDECOMMUNE_T1_NP] = t1.get(i);
        }
        if (gererPays) {
            res = insertPaysIntoRes(res, 7, pays);
        }
        jdonrefParams.getGestionLog().logValidation(application, codedepartement, AGestionLogs.FLAG_VALIDE_CODEPOSTAL, true);
        return res;
    }
    private final static String valideVoieCodePostal_sbtime_0 = "SELECT voies.t0,voies.t1,communes.t0,communes.t1,voi_min_numero,voi_max_numero FROM \"";    
    private final static String valideVoieCodePostal_sbtime_1 = "\" as voies,com_communes as communes WHERE communes.com_code_insee=voies.com_code_insee AND voi_id=? AND voi_nom=? AND communes.com_code_insee=? and communes.com_nom=? and voies.cdp_code_postal=? AND voies.t0<=? AND communes.t0<=? AND NOT (voies.t1<communes.t0 OR communes.t1<voies.t0)";
    private final static String valideVoieCodePostal_chercheExact_0 = "SELECT DISTINCT voies.voi_id,voies.voi_nom,voies.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal) AS note,voies.voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostal_chercheExact_0_adr = "SELECT DISTINCT voies.voi_id,adr.adr_id, adr.adr_numero, adr.adr_rep,voies.voi_nom,voies.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal) AS note,voies.voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    
    private final static String valideVoieCodePostal_chercheExact_1 = "\" AS voies,com_communes AS communes WHERE voi_lbl_sans_articles=? AND ";
    
    private final static String valideVoieCodePostal_chercheExact_1_adr_1 = "\" AS voies left outer join \"";
    private final static String valideVoieCodePostal_chercheExact_1_adr_2 = "\" as adr on adr.voi_id=voies.voi_id and adr.adr_numero=?";
    private final static String valideVoieCodePostal_chercheExact_1_adr_3 = " and adr.adr_rep = ?";
    private final static String valideVoieCodePostal_chercheExact_1_adr_4 = ",com_communes AS communes WHERE voi_lbl_sans_articles=? AND ";
    
    private final static String valideVoieCodePostal_chercheExact_2 = "voi_type_de_voie=? AND ";
    private final static String valideVoieCodePostal_chercheExact_3 = "dpt_code_departement=? AND ";
    private final static String valideVoieCodePostal_chercheExact_4 = "voies.com_code_insee = communes.com_code_insee AND voies.t0<=? AND communes.t0<=? AND ";
    private final static String valideVoieCodePostal_chercheExact_5 = "voies.voi_min_numero<=? AND voies.voi_max_numero>=? AND ";
    private final static String valideVoieCodePostal_chercheExact_6 = "NOT (voies.t1<communes.t0 OR communes.t1<voies.t0) ORDER BY note DESC LIMIT ?";
    
    private final static String valideVoieCodePostal_cherche_0 = "SELECT DISTINCT voi_id,voies.voi_nom,voies.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal) AS note,voies.voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq,voi_min_numero,voi_max_numero FROM \"";
    
    private final static String valideVoieCodePostal_cherche_0_adr = "SELECT DISTINCT voies.voi_id,adr.adr_id, adr.adr_numero, adr.adr_rep,voies.voi_nom,voies.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal) AS note,voies.voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq,voi_min_numero,voi_max_numero FROM \"";
    
    private final static String valideVoieCodePostal_cherche_1 = "\" AS voies,com_communes AS communes WHERE note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal)>=? AND voies.com_code_insee = communes.com_code_insee AND voies.t0<=? AND communes.t0<=? AND NOT (voies.t1<communes.t0 OR communes.t1<voies.t0)";
    
    private final static String valideVoieCodePostal_cherche_1_adr_1 = "\" AS voies left outer join \"";
    private final static String valideVoieCodePostal_cherche_1_adr_2 = "\" as adr on adr.voi_id=voies.voi_id and adr.adr_numero=? and adr.adr_rep = ?";
    private final static String valideVoieCodePostal_cherche_1_adr_3 = ",com_communes AS communes WHERE note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal)>=? AND voies.com_code_insee = communes.com_code_insee AND voies.t0<=? AND communes.t0<=? AND NOT (voies.t1<communes.t0 OR communes.t1<voies.t0)";
    
    private final static String valideVoieCodePostal_cherche_2 = " AND voies.voi_min_numero<=? AND voies.voi_max_numero>=?";
    private final static String valideVoieCodePostal_cherche_3 = " ORDER BY note DESC LIMIT ?";

    /**
     * Valide la voie et le code postal spécifié jusqu'à la date spécifiée.<br>
     * Si la date est null, la date actuelle est choisie.<br>
     * Le type de voie et le libellé de voie sont validés séparemment.<br>
     * La recherche est effectuée en deux étapes:
     * <ul><li>La première par correspondance exacte</li>
     * <li>La seconde par correspondance phonétique et orthographique</li</ul>
     * La première étape peut être ignorée si le paramètre force vaut false.<br>
     * La tolérance d'erreur entre la voie et le cdp_code_postal est <br>
     * Retourne un tableau de la forme (taille 2+10*n):
     * <ul><li>1 ou 2 selon que la recherche est exacte ou non</li>
     *     <li>Nombre de résultats</li>
     *     <li>ligne1</li>
     *     <li>ligne2</li>
     *     <li>ligne3</li>
     *     <li>ligne5</li>
     *     <li>Identifiant de la voie 1</li>
     *     <li>ligne4 1</li>
     *     <li>ligne4 desabbrevie 1</li>
     *     <li>code insee 1</li>
     *     <li>ligne6 1</li>
     *     <li>ligne6 desabbrevie 1</li>
     *     <li>t0 1 sous la forme JJ/MM/AA</li>
     *     <li>t1 1 sous la forme JJ/MM/AA</li>
     *     <li>distance trouvée 1</li>
     *     <li>code fantoire 1</li>
     *     <li>Identifiant de la voie 2</li>
     *     <li>nom de la voie 2</li>
     *     <li>...</li></ul>
     * En cas d'erreur:
     * <ul><li>0</li>
     *     <li>Code d'erreur</li>
     *     <li>Message d'erreur</li></ul>
     * Les erreurs gérées:
     * <ul><li>1 = Le département spécifié n'est pas géré.</li></ul>
     * 
     * Patché par GS
     * 
     * @return null si le code departement n'a pas pu être déduit du cdp_code_postal.
     */
    public String[] valideVoieCodePostal(int application, String[] lignes, Date date, boolean force, boolean gererAdresse,boolean gererPays, String payS, Connection connection) throws
            SQLException {
        String ligne4 = lignes[3];
        String ligne6 = lignes[5];
        ArrayList<RefNumero> rcnumeros = gestionMots.trouveNumeros(ligne4);
        String strnumero = null;
        int numero = 0;
        String repetition = " ";
        if (rcnumeros.size() > 0) {
            strnumero = rcnumeros.get(0).obtientNumeroNormalise();
            numero = Integer.parseInt(strnumero);
            if (rcnumeros.get(0).obtientRepetition() == null) {
                repetition = " ";
            } else {
                repetition = Character.toString(rcnumeros.get(0).obtientRepetitionNormalise());
            }
        }
        RefTypeVoie rctypedevoie = gestionMots.trouveTypeVoie(ligne4, rcnumeros);
        String stypedevoie = rctypedevoie.obtientMot();
        boolean stypedevoie_present = stypedevoie.length() > 0;
        String stypedevoie_phonetique = Algos.phonexNonVide(stypedevoie);
        RefCle rclibelle;
        if (stypedevoie_present) {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, gestionMots.trouveArticleVoie(ligne4, rctypedevoie));
        } else {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, rcnumeros);
        }

        String slibelle_sans_articles, slibelle_phonetique, sderniermot_phonetique;
        if (rclibelle.obtientMot().length() > 0) {
            slibelle_sans_articles = Algos.sansarticles(rclibelle.obtientMot());
            slibelle_phonetique = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        } else {
            stypedevoie_present = false;
            slibelle_sans_articles = Algos.sansarticles(ligne4);
            slibelle_phonetique = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        }

        String cdp_code_postal = gestionMots.trouveCodePostal(ligne6).obtientMot();
        String code_departement = gestionMots.trouveCodeDepartement(cdp_code_postal).obtientMot();

        if (code_departement.length() == 0) {
            return new String[]{"0", "5", "Le code département n'a pas été trouvé ou est incorrect."};
        }

        // WA 09/2011 utilisation de GestionTables.getXXTableName
        //  String nomTable = voi_voies+code_departement;
        String nomTableVoie = GestionTables.getVoiVoiesTableName(code_departement);
        String nomTableAdresse = GestionTables.getAdrAdressesTableName(code_departement);

        if (!GestionTables.tableExiste(nomTableVoie, connection)) {
            return new String[]{"0", "1", "Le département " + code_departement + " n'est pas géré."};
        }
        if (!GestionTables.tableExiste(nomTableAdresse,connection)) {
            gererAdresse = false;
        }

        if (date == null) {
            date = Calendar.getInstance().getTime();
        }

        Timestamp tsdate = new Timestamp(date.getTime());
        ArrayList<String[]> voies = new ArrayList<String[]>();
        ArrayList<Integer> notes = new ArrayList<Integer>();

        // Prépare la requête permettant de trouver les informations complémentaires.
        StringBuilder sb = new StringBuilder();

        sb.append(valideVoieCodePostal_sbtime_0);
        sb.append(nomTableVoie);
        sb.append(valideVoieCodePostal_sbtime_1);
        PreparedStatement psTime = connection.prepareStatement(sb.toString());
        psTime.setTimestamp(6, tsdate);
        psTime.setTimestamp(7, tsdate);

        ResultSet rsChercheExact = null;
        PreparedStatement psChercheExact = null;

        boolean rechercheexacte = false;

        if (!force) {
            // Prépare la requête permettant de chercher les voies.
            sb.setLength(0);
            if (gererAdresse && numero!=0)
                sb.append(valideVoieCodePostal_chercheExact_0_adr);
            else
                sb.append(valideVoieCodePostal_chercheExact_0);
            sb.append(nomTableVoie);
            if (gererAdresse && numero!=0)
            {
                sb.append(valideVoieCodePostal_chercheExact_1_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostal_chercheExact_1_adr_2);
                sb.append(valideVoieCodePostal_chercheExact_1_adr_3);
                sb.append(valideVoieCodePostal_chercheExact_1_adr_4);
            }
            else
            {
                sb.append(valideVoieCodePostal_chercheExact_1);
            }
            
            if (stypedevoie_present) {
                sb.append(valideVoieCodePostal_chercheExact_2);
            }
            if (cdp_code_postal.length() == 5) {
                sb.append(valideVoieCodePostal_chercheExact_3);
            }
            sb.append(valideVoieCodePostal_chercheExact_4);
            if (numero != 0) {
                sb.append(valideVoieCodePostal_chercheExact_5);
            }
            sb.append(valideVoieCodePostal_chercheExact_6);
            psChercheExact = connection.prepareStatement(sb.toString());

            // note_voie_cdp_code_postal(?,motdeterminant,?,libellesansarticles_phonetique,?,typedevoie,communes.nom,?,voies.cdp_code_postal,?,voies.min_numero,voies.max_numero)
            // HM 05/10/2012 note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal)
            int index = 1;
            psChercheExact.setString(index++, sderniermot_phonetique);
            psChercheExact.setString(index++, slibelle_phonetique);
            psChercheExact.setString(index++, stypedevoie_phonetique);
            psChercheExact.setString(index++, cdp_code_postal);

            // JM 02/2015 OUTER LEFT JOIN
            if (gererAdresse && numero!=0)
            {
                psChercheExact.setInt(index++,numero);
                psChercheExact.setString(index++,repetition);
            }
            
            // WHERE voi_lbl=? AND voi_type_de_voie=? AND cdp_code_postal=? AND 
            psChercheExact.setString(index++, slibelle_sans_articles);
            if (stypedevoie_present) {
                psChercheExact.setString(index++, stypedevoie);
            }
            if (cdp_code_postal.length() == 5) {
                psChercheExact.setString(index++, code_departement);
            }

            // voies.t0<=? AND communes.t0<=? 
            psChercheExact.setTimestamp(index++, tsdate);
            psChercheExact.setTimestamp(index++, tsdate);

            // voies.numero_debut<=? AND voies.numero_fin>=? AND 
            if (numero != 0) {
                psChercheExact.setInt(index++, numero);
                psChercheExact.setInt(index++, numero);
            }

            // LIMIT ?
            psChercheExact.setInt(index, jdonrefParams.obtientNombreDeVoieParDefaut());

            rsChercheExact = psChercheExact.executeQuery();
        }

        if (!force && rsChercheExact.next()) {
            rechercheexacte = true;
            do {
                String service = (numero!=0)? (gererAdresse?((rsChercheExact.getString(2)!=null)?"2":"3"):"3"):"4";
                
                voies.add(new String[]{
                    rsChercheExact.getString(1), // voi_id
                    rsChercheExact.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsChercheExact.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsChercheExact.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsChercheExact.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsChercheExact.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsChercheExact.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsChercheExact.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsChercheExact.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    "", // indique qu'il faut conserver le numéro d'adresse dans l'adresse.
                    service,
                    (gererAdresse && numero!=0)? rsChercheExact.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsChercheExact.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsChercheExact.getString(4):null   // adr_rep
                });

                notes.add(new Integer(rsChercheExact.getInt(6+((gererAdresse && numero!=0)?3:0))));
            } while (rsChercheExact.next());

            psChercheExact.close();
            rsChercheExact.close();
        } else {
            if (rsChercheExact != null) {
                psChercheExact.close();
                rsChercheExact.close();
            }

            // Prépare la requête permettant de chercher les voies.
            sb.setLength(0);
            if (gererAdresse&&numero!=0)
                sb.append(valideVoieCodePostal_cherche_0_adr);
            else
                sb.append(valideVoieCodePostal_cherche_0);
            sb.append(nomTableVoie);
            if (gererAdresse&&numero!=0)
            {
                sb.append(valideVoieCodePostal_cherche_1_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostal_cherche_1_adr_2);
                sb.append(valideVoieCodePostal_cherche_1_adr_3);
            }
            else
                sb.append(valideVoieCodePostal_cherche_1);
            sb.append(valideVoieCodePostal_cherche_3);
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());

            int index = 1;
            // note_voie_cdp_code_postal(?,motdeterminant,?,libellesansarticles_phonetique,?,typedevoie,communes.nom,?,voies.cdp_code_postal,?,voies.min_numero,voies.max_numero)
            // HM 05/10/2012 note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal)
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_phonetique);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, cdp_code_postal);

            if (gererAdresse&&numero!=0)
            {
                psCherche.setInt(index++,numero);
                psCherche.setString(index++,repetition);
            }
            
            // note_voie_cdp_code_postal(?,motdeterminant,?,libellesansarticles_phonetique,?,typedevoie,communes.nom,?,voies.cdp_code_postal,?,voies.min_numero,voies.max_numero)>=?
            // HM 05/10/2012 note_voie_codepostal(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,communes.com_nom_pq,?,voies.cdp_code_postal)
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_phonetique);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, cdp_code_postal);
            psCherche.setInt(index++, 10 * jdonrefParams.obtientSeuilDeCorrespondanceDAdresse());

            // voies.t0<=? AND communes.t0<=? 
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            // LIMIT ?
            psCherche.setInt(index, jdonrefParams.obtientNombreDeVoieParDefaut());

            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                String presencenumero = null;
                boolean auTroncon = false;
                if (numero >= rsCherche.getInt(11+((gererAdresse && numero!=0)?3:0)) && numero <= rsCherche.getInt(12+((gererAdresse && numero!=0)?3:0))) {
                    presencenumero = "";
                    if(numero != 0){
                        auTroncon = true;
                    }
                }
                String service = (numero!=0 && gererAdresse && rsCherche.getString(2)!=null)?"2":(auTroncon?"3":"4");
                voies.add(new String[]{
                    rsCherche.getString(1), // voi_id
                    rsCherche.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsCherche.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsCherche.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsCherche.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsCherche.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsCherche.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsCherche.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsCherche.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    presencenumero, // indique s'il faut ou pas conserver le numéro dans la ligne 4
                    service,
                    (gererAdresse && numero!=0)? rsCherche.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsCherche.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsCherche.getString(4):null   // adr_rep
                });
                notes.add(new Integer(rsCherche.getInt(6+((gererAdresse && numero!=0)?3:0))));
            }

            rsCherche.close();
            psCherche.close();
        }

        HashMap<String,Integer> solutions = new HashMap<String,Integer>(); // gestion de la redondance.
        
        int maxnote_constant = jdonrefParams.obtientNotePourMotDeterminant() +
                jdonrefParams.obtientNotePourTypeDeVoie() +
                jdonrefParams.obtientNotePourCodePostal();
        int notelibelle = jdonrefParams.obtientNotePourLibelle();
        int notecommune = jdonrefParams.obtientNotePourCommune();
        int notenumero = jdonrefParams.obtientNotePourNumero();
        //
        // POUR CHAQUE SOLUTION TROUVE, CONSERVE LES DERNIERES MISES A JOUR.
        // AJUSTE LA NOTE SUIVANT LE NUMERO
        // ELIMINE LES SOLUTIONS REDONDANTES (MEME LIBELLE, MEME NUMERO, MEME COMMUNE)
        // voi_id=? and communes.com_code_insee=? and communes.nom=? and communes.cdp_code_postal=?
        for (int i = 0; i < voies.size(); i++) {
            psTime.setString(1, voies.get(i)[0]);
            psTime.setString(2, voies.get(i)[1]);
            psTime.setString(3, voies.get(i)[2]);
            psTime.setString(4, voies.get(i)[3]);
            psTime.setString(5, voies.get(i)[4]);
            ResultSet rsTime = psTime.executeQuery();

            // Permet de corriger la note.
            int maxnote = maxnote_constant +
                    notelibelle * Algos.nombreDeMots(voies.get(i)[10]) +
                    notecommune * Algos.nombreDeMots(Algos.phonexNonVide(voies.get(i)[9]));
            double notesurmax = ((double) (notes.get(i).intValue() * maxnote)) / NOTE_MAX;
            maxnote += notenumero;

            // Dedoublonnage
            String candidat = voies.get(i)[1]+" "+voies.get(i)[3];
            Integer ancienne_solution;
            boolean doublon = false;
            if ((ancienne_solution=solutions.get(candidat))!=null)
            {
                int i_ancienne_solution = ancienne_solution.intValue();
                int service_ancienne_solution = Integer.parseInt(voies.get(i_ancienne_solution)[13]);
                if (service_ancienne_solution<Integer.parseInt(voies.get(i)[13]))
                    doublon = true;
                else if (service_ancienne_solution>Integer.parseInt(voies.get(i)[13])) // UNIQUEMENT 2 solutions
                {
                    // suppression de l'ancienne solution
                    voies.remove(i_ancienne_solution);
                    notes.remove(i_ancienne_solution);
                    i--;
                    Iterator<String> keys = solutions.keySet().iterator();
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        if (solutions.get(key).intValue()>i_ancienne_solution)
                        {
                            solutions.put(key,solutions.get(key).intValue()-1);
                        }
                    }
                }
            }
            
            if (!doublon && rsTime.next()) {
                long dt0;
                long dt1;
                long dtv0 = rsTime.getTimestamp(1).getTime();
                long dtv1 = rsTime.getTimestamp(2).getTime();
                long dtc0 = rsTime.getTimestamp(3).getTime();
                long dtc1 = rsTime.getTimestamp(4).getTime();

                int min_numero = rsTime.getInt(5);
                int max_numero = rsTime.getInt(6);

                // Il faut d'abord voir de qui, la commune ou la voie, a la durée de vie la plus restreinte.
                if (dtv0 < dtc0) {
                    dt0 = dtc0;
                } else {
                    dt0 = dtv0;
                }
                if (dtv1 < dtc1) {
                    dt1 = dtv1;
                } else {
                    dt1 = dtc1;
                }

                while (rsTime.next()) {
                    long tdt1;
                    long tdtv1 = rsTime.getTimestamp(2).getTime();
                    long tdtc1 = rsTime.getTimestamp(4).getTime();

                    if (tdtv1 < tdtc1) {
                        tdt1 = tdtv1;
                    } else {
                        tdt1 = tdtc1;
                    }

                    if (tdt1 > dt1) {
                        min_numero = rsTime.getInt(5);
                        max_numero = rsTime.getInt(6);

                        long tdt0;
                        long tdtv0 = rsTime.getTimestamp(1).getTime();
                        long tdtc0 = rsTime.getTimestamp(3).getTime();

                        if (tdtv0 < tdtc0) {
                            tdt0 = tdtc0;
                        } else {
                            tdt0 = tdtv0;
                        }

                        dt0 = tdt0;
                        dt1 = tdt1;
                    }
                }

                // WA 09/2011 DateFormat -> DateUtils
                // voies.get(i)[5] = sdformat.format(new Date(dt0));
                // voies.get(i)[6] = sdformat.format(new Date(dt1));
                voies.get(i)[5] = DateUtils.formatDateToString(new Date(dt0), sdformat);
                voies.get(i)[6] = DateUtils.formatDateToString(new Date(dt1), sdformat);


                // voir valideVoieCodePostalCommune
                if (numero != 0)
                {
                    if (gererAdresse && voies.get(i)[14]!=null)
                        notesurmax += notenumero;
                    else
                    if (((min_numero != 0 || max_numero != 0) &&
                        numero >= min_numero && numero <= max_numero)) {
                        if (min_numero%2 == max_numero%2)
                            notesurmax += notenumero/2;
                        else
                            notesurmax += notenumero/4;
                    }
                }
                else
                {
                    notesurmax += notenumero;
                }

                int note = (int) (notesurmax * NOTE_MAX) / maxnote;
                notes.set(i, new Integer(note));
                voies.get(i)[11] = Integer.toString(note);
                solutions.put(candidat, i);
            } else {
                voies.remove(i);
                notes.remove(i);
                i--;
            }
            rsTime.close();
        }
        psTime.close();

        // Comme les notes ont été recalculées, il est nécessaire de les trier.
        voies = triePropositions(voies, notes);

        String[] res = new String[11 * voies.size() + VALIDEVOIE_ID];

        res[0] = rechercheexacte ? "1" : "2";
        res[1] = Integer.toString(voies.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[4];

        int voisize = voies.size();
        for (int i=0,offset = 0; i < voisize; i++, offset += VALIDEVOIE_TABSIZE) {
            String[] str = voies.get(i);
            res[offset + VALIDEVOIE_ID] = str[0];
            if (str[12] != null) {
                res[offset + VALIDEVOIE_LIGNE4] = Algos.unsplit(strnumero, repetition, str[1]); // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = Algos.unsplit(strnumero, repetition, str[8]); // ligne4 désabbréviée
            } else {
                res[offset + VALIDEVOIE_LIGNE4] = str[1]; // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = str[8]; // ligne4 désabbréviée
            }
            res[offset + VALIDEVOIE_CODEINSEE] = str[2]; // code insee
            res[offset + VALIDEVOIE_LIGNE6] = Algos.unsplit(str[4], str[3]); // ligne6
            res[offset + VALIDEVOIE_LIGNE6_DESABBREVIE] = Algos.unsplit(str[4], str[9]); // ligne6
            res[offset + VALIDEVOIE_T0] = str[5];
            res[offset + VALIDEVOIE_T1] = str[6];
            res[offset + VALIDEVOIE_NOTE] = str[11];
            res[offset + VALIDEVOIE_FANTOIR] = str[7];
            res[offset + VALIDEVOIE_SERVICE] = str[13];
        }

        if (gererPays) {
            res = insertPaysIntoRes(res, 6, payS);
        }
        if (stypedevoie_present) {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE + AGestionLogs.FLAG_VALIDE_TYPEDEVOIE, true);
        } else {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE, true);
        }

        return res;
    }

    public String[] valideVoieCodePostal(int application, String[] lignes, Date date, boolean auTroncon, boolean force, boolean gererAdresse,boolean gererPays, String payS, Connection connection) throws
            SQLException {
        String ligne4 = lignes[3];
        String ligne6 = lignes[5];
        ArrayList<RefNumero> rcnumeros = gestionMots.trouveNumeros(ligne4);
        String strnumero = null;
        int numero = 0;
        String repetition = " ";
        if (rcnumeros.size() > 0) {
            strnumero = rcnumeros.get(0).obtientNumeroNormalise();
            numero = Integer.parseInt(strnumero);
            if (rcnumeros.get(0).obtientRepetition() == null) {
                repetition = " ";
            } else {
                repetition = Character.toString(rcnumeros.get(0).obtientRepetitionNormalise());
            }
        }

        if ((auTroncon || gererAdresse) && numero == 0) {
            return new String[]{"0", "5", "Aucun numéro de voie n'a été spécifié."};
        }

        RefTypeVoie rctypedevoie = gestionMots.trouveTypeVoie(ligne4, rcnumeros);
        String stypedevoie = rctypedevoie.obtientMot();
        boolean stypedevoie_present = stypedevoie.length() > 0;
        String stypedevoie_phonetique = Algos.phonexNonVide(stypedevoie);
        RefCle rclibelle;
        if (stypedevoie_present) {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, gestionMots.trouveArticleVoie(ligne4, rctypedevoie));
        } else {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, rcnumeros);
        }

        String slibelle_sans_articles, slibelle_phonetique, sderniermot_phonetique;
        if (rclibelle.obtientMot().length() > 0) {
            slibelle_sans_articles = Algos.sansarticles(rclibelle.obtientMot());
            slibelle_phonetique = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        } else {
            stypedevoie_present = false;
            slibelle_sans_articles = Algos.sansarticles(ligne4);
            slibelle_phonetique = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        }

        String cdp_code_postal = gestionMots.trouveCodePostal(ligne6).obtientMot();
        String code_departement = gestionMots.trouveCodeDepartement(cdp_code_postal).obtientMot();

        if (code_departement.length() == 0) {
            return new String[]{"0", "5", "Le code département n'a pas été trouvé ou est incorrect."};
        }

        // WA 09/2011 utilisation de GestionTables.getXXTableName
        //  String nomTable = voi_voies+code_departement;
        String nomTableVoie = GestionTables.getVoiVoiesTableName(code_departement);
        String nomTableAdresse = GestionTables.getAdrAdressesTableName(code_departement);

        if (!GestionTables.tableExiste(nomTableVoie, connection)) {
            return new String[]{"0", "1", "Le département " + code_departement + " n'est pas géré."};
        }
        if (!GestionTables.tableExiste(nomTableAdresse,connection)) {
            gererAdresse = false;
        }

        if (date == null) {
            date = Calendar.getInstance().getTime();
        }

        Timestamp tsdate = new Timestamp(date.getTime());
        ArrayList<String[]> voies = new ArrayList<String[]>();
        ArrayList<Integer> notes = new ArrayList<Integer>();

        // Prépare la requête permettant de trouver les informations complémentaires.
        StringBuilder sb = new StringBuilder();

        sb.append(valideVoieCodePostal_sbtime_0);
        sb.append(nomTableVoie);
        sb.append(valideVoieCodePostal_sbtime_1);
        PreparedStatement psTime = connection.prepareStatement(sb.toString());
        psTime.setTimestamp(6, tsdate);
        psTime.setTimestamp(7, tsdate);

        ResultSet rsChercheExact = null;
        PreparedStatement psChercheExact = null;

        boolean rechercheexacte = false;

        if (!force) {
            // Prépare la requête permettant de chercher les voies.
            sb.setLength(0);
            if (gererAdresse && numero!=0)
                sb.append(valideVoieCodePostal_chercheExact_0_adr);
            else
                sb.append(valideVoieCodePostal_chercheExact_0);
            sb.append(nomTableVoie);
            if (gererAdresse && numero!=0)
            {
                sb.append(valideVoieCodePostal_chercheExact_1_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostal_chercheExact_1_adr_2);
                sb.append(valideVoieCodePostal_chercheExact_1_adr_3);
                sb.append(valideVoieCodePostal_chercheExact_1_adr_4);
            }
            else
            {
                sb.append(valideVoieCodePostal_chercheExact_1);
            }
            if (stypedevoie_present) {
                sb.append(valideVoieCodePostal_chercheExact_2);
            }
            if (cdp_code_postal.length() == 5) {
                sb.append(valideVoieCodePostal_chercheExact_3);
            }
            sb.append(valideVoieCodePostal_chercheExact_4);
            if (auTroncon) {
                sb.append(valideVoieCodePostal_chercheExact_5);
            }
            sb.append(valideVoieCodePostal_chercheExact_6);
            psChercheExact = connection.prepareStatement(sb.toString());

            // note_voie_cdp_code_postal(?,motdeterminant,?,libellesansarticles_phonetique,?,typedevoie,communes.nom,?,voies.cdp_code_postal,?,voies.min_numero,voies.max_numero)
            int index = 1;
            psChercheExact.setString(index++, sderniermot_phonetique);
            psChercheExact.setString(index++, slibelle_phonetique);
            psChercheExact.setString(index++, stypedevoie_phonetique);
            psChercheExact.setString(index++, cdp_code_postal);

            // JM 02/2015 OUTER LEFT JOIN
            if (gererAdresse && numero!=0)
            {
                psChercheExact.setInt(index++,numero);
                psChercheExact.setString(index++,repetition);
            }
            
            // WHERE voi_lbl=? AND voi_type_de_voie=? AND cdp_code_postal=? AND 
            psChercheExact.setString(index++, slibelle_sans_articles);
            if (stypedevoie_present) {
                psChercheExact.setString(index++, stypedevoie);
            }
            if (cdp_code_postal.length() == 5) {
                psChercheExact.setString(index++, code_departement);
            }

            // voies.t0<=? AND communes.t0<=? 
            psChercheExact.setTimestamp(index++, tsdate);
            psChercheExact.setTimestamp(index++, tsdate);

            // voies.numero_debut<=? AND voies.numero_fin>=? AND 
            if (auTroncon) {
                psChercheExact.setInt(index++, numero);
                psChercheExact.setInt(index++, numero);
            }

            // LIMIT ?
            psChercheExact.setInt(index, jdonrefParams.obtientNombreDeVoieParDefaut());

            rsChercheExact = psChercheExact.executeQuery();
        }

        if (!force && rsChercheExact.next()) {
            rechercheexacte = true;
            do {
                String service = (numero!=0)? (gererAdresse?((rsChercheExact.getString(2)!=null)?"2":"3"):"3"):"4";
                
                voies.add(new String[]{
                    rsChercheExact.getString(1), // voi_id
                    rsChercheExact.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsChercheExact.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsChercheExact.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsChercheExact.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsChercheExact.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsChercheExact.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsChercheExact.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsChercheExact.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    "", // indique qu'il faut conserver le numéro d'adresse dans l'adresse.
                    service,
                    (gererAdresse && numero!=0)? rsChercheExact.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsChercheExact.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsChercheExact.getString(4):null   // adr_rep
                });

                notes.add(new Integer(rsChercheExact.getInt(6+((gererAdresse && numero!=0)?3:0))));
            } while (rsChercheExact.next());

            psChercheExact.close();
            rsChercheExact.close();
        } else {
            if (rsChercheExact != null) {
                psChercheExact.close();
                rsChercheExact.close();
            }

            // Prépare la requête permettant de chercher les voies.
            sb.setLength(0);
            if (gererAdresse&&numero!=0)
                sb.append(valideVoieCodePostal_cherche_0_adr);
            else
                sb.append(valideVoieCodePostal_cherche_0);
            sb.append(nomTableVoie);
            if (gererAdresse&&numero!=0)
            {
                sb.append(valideVoieCodePostal_cherche_1_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostal_cherche_1_adr_2);
                sb.append(valideVoieCodePostal_cherche_1_adr_3);
            }
            else
                sb.append(valideVoieCodePostal_cherche_1);
            if (auTroncon) {
                sb.append(valideVoieCodePostal_cherche_2);
            }
            sb.append(valideVoieCodePostal_cherche_3);
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());

            int index = 1;
            // note_voie_cdp_code_postal(?,motdeterminant,?,libellesansarticles_phonetique,?,typedevoie,communes.nom,?,voies.cdp_code_postal,?,voies.min_numero,voies.max_numero)
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_phonetique);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, cdp_code_postal);
            
            if (gererAdresse&&numero!=0)
            {
                psCherche.setInt(index++,numero);
                psCherche.setString(index++,repetition);
            }

            // note_voie_cdp_code_postal(?,motdeterminant,?,libellesansarticles_phonetique,?,typedevoie,communes.nom,?,voies.cdp_code_postal,?,voies.min_numero,voies.max_numero)>=?
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_phonetique);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, cdp_code_postal);
            psCherche.setInt(index++, 10 * jdonrefParams.obtientSeuilDeCorrespondanceDAdresse());

            // voies.t0<=? AND communes.t0<=? 
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            // AND voies.numero_debut<=? AND voies.numero_fin>=?
            if (auTroncon) {
                psCherche.setInt(index++, numero);
                psCherche.setInt(index++, numero);
            }

            // LIMIT ?
            psCherche.setInt(index, jdonrefParams.obtientNombreDeVoieParDefaut());

            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                String presencenumero = null;
                boolean serviceTroncon = false;
                if (numero >= rsCherche.getInt(11+((gererAdresse && numero!=0)?3:0)) && numero <= rsCherche.getInt(12+((gererAdresse && numero!=0)?3:0))) {
                    presencenumero = "";
                    if(numero != 0){
                        serviceTroncon = true;
                    }
                }
                String service = (numero!=0 && gererAdresse && rsCherche.getString(2)!=null)?"2":(serviceTroncon?"3":"4");
                voies.add(new String[]{
                    rsCherche.getString(1), // voi_id
                    rsCherche.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsCherche.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsCherche.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsCherche.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsCherche.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsCherche.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsCherche.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsCherche.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    presencenumero, // indique s'il faut ou pas conserver le numéro dans la ligne 4
                    service,
                    (gererAdresse && numero!=0)? rsCherche.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsCherche.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsCherche.getString(4):null   // adr_rep
                });
                notes.add(new Integer(rsCherche.getInt(6+((gererAdresse && numero!=0)?3:0))));
            }

            rsCherche.close();
            psCherche.close();
        }

        HashMap<String,Integer> solutions = new HashMap<String,Integer>(); // gestion de la redondance.
        
        int maxnote_constant = jdonrefParams.obtientNotePourMotDeterminant() +
                jdonrefParams.obtientNotePourTypeDeVoie() +
                jdonrefParams.obtientNotePourCodePostal();
        int notelibelle = jdonrefParams.obtientNotePourLibelle();
        int notecommune = jdonrefParams.obtientNotePourCommune();
        int notenumero = jdonrefParams.obtientNotePourNumero();
        //
        // POUR CHAQUE SOLUTION TROUVE, CONSERVE LES DERNIERES MISES A JOUR.
        // voi_id=? and communes.com_code_insee=? and communes.nom=? and communes.cdp_code_postal=?
        for (int i = 0; i < voies.size(); i++) {
            psTime.setString(1, voies.get(i)[0]);
            psTime.setString(2, voies.get(i)[1]);
            psTime.setString(3, voies.get(i)[2]);
            psTime.setString(4, voies.get(i)[3]);
            psTime.setString(5, voies.get(i)[4]);
            ResultSet rsTime = psTime.executeQuery();

            // Permet de corriger la note.
            int maxnote = maxnote_constant +
                    notelibelle * Algos.nombreDeMots(voies.get(i)[10]) +
                    notecommune * Algos.nombreDeMots(Algos.phonexNonVide(voies.get(i)[9]));
            double notesurmax = ((double) (notes.get(i).intValue() * maxnote)) / NOTE_MAX;
            maxnote += notenumero;

            // Dedoublonnage
            String candidat = voies.get(i)[1]+" "+voies.get(i)[3];
            Integer ancienne_solution;
            boolean doublon = false;
            if ((ancienne_solution=solutions.get(candidat))!=null)
            {
                int i_ancienne_solution = ancienne_solution.intValue();
                int service_ancienne_solution = Integer.parseInt(voies.get(i_ancienne_solution)[13]);
                if (service_ancienne_solution<Integer.parseInt(voies.get(i)[13]))
                    doublon = true;
                else if (service_ancienne_solution>Integer.parseInt(voies.get(i)[13])) // UNIQUEMENT 2 solutions
                {
                    // suppression de l'ancienne solution
                    voies.remove(i_ancienne_solution);
                    notes.remove(i_ancienne_solution);
                    i--;
                    Iterator<String> keys = solutions.keySet().iterator();
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        if (solutions.get(key).intValue()>i_ancienne_solution)
                        {
                            solutions.put(key,solutions.get(key).intValue()-1);
                        }
                    }
                }
            }
            
            if (!doublon && rsTime.next()) {
                long dt0;
                long dt1;
                long dtv0 = rsTime.getTimestamp(1).getTime();
                long dtv1 = rsTime.getTimestamp(2).getTime();
                long dtc0 = rsTime.getTimestamp(3).getTime();
                long dtc1 = rsTime.getTimestamp(4).getTime();

                int min_numero = rsTime.getInt(5);
                int max_numero = rsTime.getInt(6);

                // Il faut d'abord voir de qui, la commune ou la voie, a la durée de vie la plus restreinte.
                if (dtv0 < dtc0) {
                    dt0 = dtc0;
                } else {
                    dt0 = dtv0;
                }
                if (dtv1 < dtc1) {
                    dt1 = dtv1;
                } else {
                    dt1 = dtc1;
                }

                while (rsTime.next()) {
                    long tdt1;
                    long tdtv1 = rsTime.getTimestamp(2).getTime();
                    long tdtc1 = rsTime.getTimestamp(4).getTime();

                    if (tdtv1 < tdtc1) {
                        tdt1 = tdtv1;
                    } else {
                        tdt1 = tdtc1;
                    }

                    if (tdt1 > dt1) {
                        min_numero = rsTime.getInt(5);
                        max_numero = rsTime.getInt(6);

                        long tdt0;
                        long tdtv0 = rsTime.getTimestamp(1).getTime();
                        long tdtc0 = rsTime.getTimestamp(3).getTime();

                        if (tdtv0 < tdtc0) {
                            tdt0 = tdtc0;
                        } else {
                            tdt0 = tdtv0;
                        }

                        dt0 = tdt0;
                        dt1 = tdt1;
                    }
                }

                // WA 09/2011 DateFormat -> DateUtils
                // voies.get(i)[5] = sdformat.format(new Date(dt0));
                // voies.get(i)[6] = sdformat.format(new Date(dt1));
                voies.get(i)[5] = DateUtils.formatDateToString(new Date(dt0), sdformat);
                voies.get(i)[6] = DateUtils.formatDateToString(new Date(dt1), sdformat);


                // voir valideVoieCodePostalCommune
                if (numero != 0)
                {
                    if (gererAdresse && voies.get(i)[14]!=null)
                        notesurmax += notenumero;
                    else
                    if (((min_numero != 0 || max_numero != 0) &&
                        numero >= min_numero && numero <= max_numero)) {
                        if (min_numero%2 == max_numero%2)
                            notesurmax += notenumero/2;
                        else
                            notesurmax += notenumero/4;
                    }
                }
                else
                {
                    notesurmax += notenumero;
                }

                int note = (int) (notesurmax * NOTE_MAX) / maxnote;
                notes.set(i, new Integer(note));
                voies.get(i)[11] = Integer.toString(note);
                solutions.put(candidat,i);
            } else {
                voies.remove(i);
                notes.remove(i);
                i--;
            }
            rsTime.close();
        }
        psTime.close();

        // Comme les notes ont été recalculées, il est nécessaire de les trier.
        voies = triePropositions(voies, notes);

        String[] res = new String[11 * voies.size() + VALIDEVOIE_ID];

        res[0] = rechercheexacte ? "1" : "2";
        res[1] = Integer.toString(voies.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[4];

        int voisize = voies.size();
        for (int i = 0,  offset = 0; i < voisize; i++, offset += VALIDEVOIE_TABSIZE) {
            String[] str = voies.get(i);
            res[offset + VALIDEVOIE_ID] = str[0];
            if (str[12] != null) {
                res[offset + VALIDEVOIE_LIGNE4] = Algos.unsplit(strnumero, repetition, str[1]); // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = Algos.unsplit(strnumero, repetition, str[8]); // ligne4 désabbréviée
            } else {
                res[offset + VALIDEVOIE_LIGNE4] = str[1]; // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = str[8]; // ligne4 désabbréviée
            }
            res[offset + VALIDEVOIE_CODEINSEE] = str[2]; // code insee
            res[offset + VALIDEVOIE_LIGNE6] = Algos.unsplit(str[4], str[3]); // ligne6
            res[offset + VALIDEVOIE_LIGNE6_DESABBREVIE] = Algos.unsplit(str[4], str[9]); // ligne6
            res[offset + VALIDEVOIE_T0] = str[5];
            res[offset + VALIDEVOIE_T1] = str[6];
            res[offset + VALIDEVOIE_NOTE] = str[11];
            res[offset + VALIDEVOIE_FANTOIR] = str[7];
            res[offset + VALIDEVOIE_SERVICE] = str[13];
        }

        if (gererPays) {
            res = insertPaysIntoRes(res, 6, payS);
        }
        if (stypedevoie_present) {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE + AGestionLogs.FLAG_VALIDE_TYPEDEVOIE, true);
        } else {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE, true);
        }

        return res;
    }
    private final static String valideVoieCodePostalCommune_psTime_0 = "SELECT voies.t0,voies.t1,communes.t0,communes.t1,voi_min_numero,voi_max_numero FROM \"";
    private final static String valideVoieCodePostalCommune_psTime_1 = "\" as voies,com_communes as communes WHERE communes.com_code_insee=voies.com_code_insee and voi_id=? and voi_nom=? and communes.com_code_insee=? and communes.com_nom=? and voies.cdp_code_postal=? AND voies.t0<=? AND communes.t0<=? AND NOT (voies.t1<communes.t0 OR communes.t1<voies.t0)";
    
    private final static String valideVoieCodePostalCommune_psChercheExact_0 = "SELECT DISTINCT ";
    private final static String valideVoieCodePostalCommune_psChercheExact_1 = "voi_id,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,?,voies.cdp_code_postal,?,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_2 = "voi_id,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,?,voies.cdp_code_postal,voies.cdp_code_postal,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_3 = "voi_id,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,voies.cdp_code_postal,voies.cdp_code_postal,?,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_4 = "voi_id,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,voies.cdp_code_postal,voies.cdp_code_postal,voies.cdp_code_postal,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    
    private final static String valideVoieCodePostalCommune_psChercheExact_1_adr = "voies.voi_id,adr_id,adr_numero,adr_rep,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,?,voies.cdp_code_postal,?,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_2_adr = "voies.voi_id,adr_id,adr_numero,adr_rep,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,?,voies.cdp_code_postal,voies.cdp_code_postal,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_3_adr = "voies.voi_id,adr_id,adr_numero,adr_rep,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,voies.cdp_code_postal,voies.cdp_code_postal,?,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_4_adr = "voies.voi_id,adr_id,adr_numero,adr_rep,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,voies.cdp_code_postal,voies.cdp_code_postal,voies.cdp_code_postal,com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq FROM \"";
    
    private final static String valideVoieCodePostalCommune_psChercheExact_5 = "\" as voies,com_communes as communes WHERE communes.dpt_code_departement=? AND voi_lbl_sans_articles=? AND ";
    
    private final static String valideVoieCodePostalCommune_psChercheExact_5_adr_1 = "\" as voies left outer join \"";
    private final static String valideVoieCodePostalCommune_psChercheExact_5_adr_2 = "\" as adr on adr.voi_id=voies.voi_id and adr.adr_numero=? and adr.adr_rep = ?";
    private final static String valideVoieCodePostalCommune_psChercheExact_5_adr_3 = ",com_communes as communes WHERE communes.dpt_code_departement=? AND voi_lbl_sans_articles=? AND ";
    
    private final static String valideVoieCodePostalCommune_psChercheExact_6 = "voi_type_de_voie=? AND ";
    private final static String valideVoieCodePostalCommune_psChercheExact_7 = "communes.com_nom_desab=? AND ";
    private final static String valideVoieCodePostalCommune_psChercheExact_8 = "communes.dpt_code_departement=? AND ";
    private final static String valideVoieCodePostalCommune_psChercheExact_9 = "substr(voies.cdp_code_postal,4,2)=? AND ";
    private final static String valideVoieCodePostalCommune_psChercheExact_10 = "voies.voi_min_numero<=? AND voies.voi_max_numero>=? AND ";
    private final static String valideVoieCodePostalCommune_psChercheExact_11 = "voies.com_code_insee = communes.com_code_insee AND voies.t0<=? AND communes.t0<=? AND NOT (voies.t1<communes.t0 OR communes.t1<voies.t0) ORDER BY note DESC LIMIT ?";

    private final static String valideVoieCodePostalCommune_psCherche_0 = "SELECT DISTINCT ";
    private final static String valideVoieCodePostalCommune_psCherche_1 = "voies.voi_id,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,";
    
    private final static String valideVoieCodePostalCommune_psCherche_1_adr = "voies.voi_id,adr_id,adr_numero,adr_rep,voies.voi_nom,communes.com_code_insee,communes.com_nom,voies.cdp_code_postal,note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,";
    
    private final static String valideVoieCodePostalCommune_psCherche_2 = "?";
    private final static String valideVoieCodePostalCommune_psCherche_3 = "voies.cdp_code_postal";
    private final static String valideVoieCodePostalCommune_psCherche_4 = ",voies.cdp_code_postal,";
    private final static String valideVoieCodePostalCommune_psCherche_5 = "?";
    private final static String valideVoieCodePostalCommune_psCherche_6 = "voies.cdp_code_postal";
    private final static String valideVoieCodePostalCommune_psCherche_7 = ",com_code_insee_commune is null) as note,voi_code_fantoir,voies.voi_nom_desab,communes.com_nom_desab,voi_lbl_sans_articles_pq,voi_min_numero,voi_max_numero FROM \"";
    private final static String valideVoieCodePostalCommune_psCherche_8 = "\" as voies,";
    
    private final static String valideVoieCodePostalCommune_psCherche_8_adr_1 = "\" as voies left outer join \"";
    private final static String valideVoieCodePostalCommune_psCherche_8_adr_2 = "\" as adr on adr.voi_id=voies.voi_id and adr.adr_numero=? and adr.adr_rep = ?";
    private final static String valideVoieCodePostalCommune_psCherche_8_adr_3 = ",";
    
    private final static String valideVoieCodePostalCommune_psCherche_9 = "(SELECT com_nom,com_code_insee,com_nom_pq,com_nom_desab,com_code_insee_commune,t0,t1 FROM com_communes WHERE dpt_code_departement=? AND note_commune_seul(?,com_nom_pq)>=? ) as communes WHERE note_voie_codepostal_commune(?,voi_mot_determinant_pq,?,voi_lbl_sans_articles_pq,?,voi_type_de_voie_pq,?,communes.com_nom_pq,";
    private final static String valideVoieCodePostalCommune_psCherche_10 = "?";
    private final static String valideVoieCodePostalCommune_psCherche_11 = "voies.cdp_code_postal";
    private final static String valideVoieCodePostalCommune_psCherche_12 = ",voies.cdp_code_postal,";
    private final static String valideVoieCodePostalCommune_psCherche_13 = "?";
    private final static String valideVoieCodePostalCommune_psCherche_14 = "voies.cdp_code_postal";
    private final static String valideVoieCodePostalCommune_psCherche_15 = ",com_code_insee_commune is null)>=? AND voies.com_code_insee = communes.com_code_insee AND voies.t0<=? AND communes.t0<=? AND NOT (voies.t1<communes.t0 OR communes.t1<voies.t0) ";
    private final static String valideVoieCodePostalCommune_psCherche_16 = " AND voies.voi_min_numero<=? AND voies.voi_max_numero>=?";
    private final static String valideVoieCodePostalCommune_psCherche_17 = " ORDER BY note DESC LIMIT ?";

    /**
     * Valide la voie, la commune, et le code postal spécifié jusqu'à la date spécifiée.<br>
     * Si la date est null, la date actuelle est utilisée.
     * Retourne un tableau de taille multiple de 10 + 1 avec pour chaque suite de 8 chaines:
     * <ul><li>1 ou 2 selon que la recherche est exacte ou non</li>
     *     <li>Nombre de résultats</li>
     *     <li>ligne1</li>
     *     <li>ligne2</li>
     *     <li>ligne3</li>
     *     <li>ligne5</li>
     *     <li>Identifiant de la voie 1</li>
     *     <li>ligne4 1</li>
     *     <li>ligne4 desabbreviée 1</li>
     *     <li>code insee 1</li>
     *     <li>ligne6 1</li>
     *     <li>ligne6 désabbreviée 1</li>
     *     <li>t0 1 sous la forme JJ/MM/AA</li>
     *     <li>t1 1 sous la forme JJ/MM/AA</li>
     *     <li>note trouvée 1</li>
     *     <li>code fantoir 1</li>
     *     <li>Identifiant de la voie 2</li>
     *     <li>nom de la voie 2</li>
     *     <li>...</li></ul>
     * </ul>
     * En cas d'erreur:
     * <ul><li>0</li>
     *     <li>Code d'erreur</li>
     *     <li>Message d'erreur</li></ul>
     * Les erreurs gérées:
     * <ul><li>1 = Le département spécifié n'est pas géré.</li></ul>
     * @return null si aucun département ne peut être extrait du code postal.
     */
    public String[] valideVoieCodePostalCommune(int application, String[] lignes, RefCle rccodepostal, RefCle rccommune, Date date,
            boolean force, boolean gererAdresse, boolean gererPays, String pays, Connection connection) throws SQLException {
        String ligne4 = lignes[3];
        String ligne6 = lignes[5];
        ArrayList<RefNumero> rcnumeros = gestionMots.trouveNumeros(ligne4);
        int numero = 0;
        String strnumero = null;
        String repetition = " ";
        if (rcnumeros.size() > 0) {
            strnumero = rcnumeros.get(0).obtientNumeroNormalise();
            numero = Integer.parseInt(strnumero);
            if (rcnumeros.get(0).obtientRepetition() == null) {
                repetition = " ";
            } else {
                repetition = Character.toString(rcnumeros.get(0).obtientRepetitionNormalise());
            }
        }

        RefTypeVoie rctypedevoie = gestionMots.trouveTypeVoie(ligne4, rcnumeros);
        String stypedevoie = rctypedevoie.obtientMot();
        boolean typedevoie_present = stypedevoie.length() > 0;
        String stypedevoie_phonetique = Algos.phonexNonVide(stypedevoie);

        RefCle rclibelle;
        if (typedevoie_present) {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, gestionMots.trouveArticleVoie(ligne4, rctypedevoie));
        } else {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, rcnumeros);
        }

        String slibelle_sans_articles, slibelle_sans_articles_pq, sderniermot_phonetique;
        if (rclibelle.obtientMot().length() > 0) {
            slibelle_sans_articles = Algos.sansarticles(rclibelle.obtientMot());
            slibelle_sans_articles_pq = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        } else {
            typedevoie_present = false;
            slibelle_sans_articles = Algos.sansarticles(ligne4);
            slibelle_sans_articles_pq = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        }

        if (rccodepostal == null) {
            rccodepostal = gestionMots.trouveCodePostal(ligne6);
        }
        String cdp_code_postal = rccodepostal.obtientMot();
        boolean cdp_present = (cdp_code_postal.length() > 2);
        String code_departement = gestionMots.trouveCodeDepartement(cdp_code_postal).obtientMot();
        if (code_departement.length() == 0) {
            return new String[]{
                "0", "5", "Aucun département connu n'a été spécifié."
            };
        }

        // WA 09/2011 utilisation de GestionTables.getXXTableName
        // String nomTable = voi_voies+code_departement;
        String nomTableVoie = GestionTables.getVoiVoiesTableName(code_departement);
        if (!GestionTables.tableExiste(nomTableVoie, connection)) {
            return new String[]{
                "0", "1", "Le département " + code_departement + " n'est pas géré."
            };
        }
        
        String nomTableAdresse = GestionTables.getAdrAdressesTableName(code_departement);
        if (!GestionTables.tableExiste(nomTableAdresse, connection)) {
            gererAdresse = false;
        }

        if (rccommune == null) {
            rccommune = gestionMots.trouveNomVille(ligne6, rccodepostal);
        }
        String commune = rccommune.obtientMot();
        String commune_phonetique = Algos.phonexNonVide(commune);

        String arrondissement = gestionMots.trouveNumeroArrondissement(ligne6, rccommune).obtientMot();
        if (arrondissement.length() > 0) {
            arrondissement = Algos.deuxChiffresObligatoires(arrondissement);
        } else {
            arrondissement = null;
        }

        if (date == null) {
            date = Calendar.getInstance().getTime();
        }
        Timestamp tsdate = new Timestamp(date.getTime());

        // Prépare la requête permettant de trouver les informations complémentaires : l'intervalle de validité
        StringBuilder sb = new StringBuilder();
        sb.append(valideVoieCodePostalCommune_psTime_0);
        sb.append(nomTableVoie);
        sb.append(valideVoieCodePostalCommune_psTime_1);
        PreparedStatement psTime = connection.prepareStatement(sb.toString());
        psTime.setTimestamp(6, tsdate);
        psTime.setTimestamp(7, tsdate);

        ArrayList<String[]> voies = new ArrayList<String[]>();
        ArrayList<Integer> notes = new ArrayList<Integer>();

        PreparedStatement psChercheExact = null;
        ResultSet rsChercheExact = null;

        boolean rechercheexacte = false;

        if (!force) {
            sb.setLength(0);
            sb.append(valideVoieCodePostalCommune_psChercheExact_0);
            if (cdp_present) {
                if (arrondissement != null) {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_1_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_1);
                } else {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_2_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_2);
                }
            } else {
                if (arrondissement != null) {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_3_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_3);
                } else {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_4_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_4);
                }
            }
            sb.append(nomTableVoie);
            
            if (gererAdresse && numero!=0)
            {
                sb.append(valideVoieCodePostalCommune_psChercheExact_5_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostalCommune_psChercheExact_5_adr_2);
                sb.append(valideVoieCodePostalCommune_psChercheExact_5_adr_3);
            }
            else
                sb.append(valideVoieCodePostalCommune_psChercheExact_5);
            
            if (typedevoie_present) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_6);
            }
            sb.append(valideVoieCodePostalCommune_psChercheExact_7);
            if (cdp_present) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_8);
            }
            if (arrondissement != null) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_9);
            }
            if (numero != 0) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_10);
            }
            sb.append(valideVoieCodePostalCommune_psChercheExact_11);

            psChercheExact = connection.prepareStatement(sb.toString());

            // note_voie_codepostal_commune(?,motdeterminant,?,libellesansarticles,?,typedevoie,?,communes.nom,?,voies.cdp_code_postal)
            int index = 1;
            psChercheExact.setString(index++, sderniermot_phonetique);
            psChercheExact.setString(index++, slibelle_sans_articles_pq);
            psChercheExact.setString(index++, stypedevoie_phonetique);
            psChercheExact.setString(index++, commune_phonetique);
            if (cdp_present) {
                psChercheExact.setString(index++, cdp_code_postal);
            }
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            // JM 02/2015 OUTER LEFT JOIN
            if (gererAdresse && numero!=0)
            {
                psChercheExact.setInt(index++,numero);
                psChercheExact.setString(index++,repetition);
            }
            
            // communes.dpt_code_departement=? AND voi_lbl_sans_articles=? AND voi_type_de_voie=? AND communes.com_nom_pq=? AND voies.cdp_code_postal=? AND
            psChercheExact.setString(index++, code_departement);
            psChercheExact.setString(index++, slibelle_sans_articles);
            if (typedevoie_present) {
                psChercheExact.setString(index++, stypedevoie);
            }
            psChercheExact.setString(index++, commune);
            if (cdp_present) {
                psChercheExact.setString(index++, code_departement);
            }
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            //"voies.voi_min_numero>=? AND voies.voi_max_numero<=? AND "
            if (numero != 0) {
                psChercheExact.setInt(index++, numero);
                psChercheExact.setInt(index++, numero);
            }

            // voies.t0<=? and communes.t0<=?
            psChercheExact.setTimestamp(index++, tsdate);
            psChercheExact.setTimestamp(index++, tsdate);

            // LIMIT ?
            psChercheExact.setInt(index++, jdonrefParams.obtientNombreDeVoieParDefaut());
            rsChercheExact = psChercheExact.executeQuery();
        }

        if (!force && rsChercheExact.next()) {
            rechercheexacte = true;
            do {
                String service = (numero!=0)? (gererAdresse?((rsChercheExact.getString(2)!=null)?"2":"3"):"3"):"4";
                
                voies.add(new String[]{
                    rsChercheExact.getString(1), // voi_id
                    rsChercheExact.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsChercheExact.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsChercheExact.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsChercheExact.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsChercheExact.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsChercheExact.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsChercheExact.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsChercheExact.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    "", // indique qu'il faut conserver le numéro d'adresse dans l'adresse.
                    service,
                    (gererAdresse && numero!=0)? rsChercheExact.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsChercheExact.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsChercheExact.getString(4):null   // adr_rep
                });
                notes.add(new Integer(rsChercheExact.getInt(6+((gererAdresse && numero!=0)?3:0))));
            } while (rsChercheExact.next());

            rsChercheExact.close();
            psChercheExact.close();
        } else {
            if (rsChercheExact != null) {
                rsChercheExact.close();
                psChercheExact.close();
            }
            sb.setLength(0);
            sb.append(valideVoieCodePostalCommune_psCherche_0);
            
            if (gererAdresse && numero!=0)
                sb.append(valideVoieCodePostalCommune_psCherche_1_adr);
            else
                sb.append(valideVoieCodePostalCommune_psCherche_1);
            if (cdp_present) {
                sb.append(valideVoieCodePostalCommune_psCherche_2);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_3);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_4);
            if (arrondissement != null) {
                sb.append(valideVoieCodePostalCommune_psCherche_5);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_6);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_7);
            sb.append(nomTableVoie);
            if (gererAdresse && numero!=0)
            {
                sb.append(valideVoieCodePostalCommune_psCherche_8_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostalCommune_psCherche_8_adr_2);
                sb.append(valideVoieCodePostalCommune_psCherche_8_adr_3);
            }
            else
                sb.append(valideVoieCodePostalCommune_psCherche_8);
            // Sous requête de sélection de la commune
            sb.append(valideVoieCodePostalCommune_psCherche_9);
            if (cdp_present) {
                sb.append(valideVoieCodePostalCommune_psCherche_10);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_11);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_12);
            if (arrondissement != null) {
                sb.append(valideVoieCodePostalCommune_psCherche_13);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_14);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_15);
            sb.append(valideVoieCodePostalCommune_psCherche_17);
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());

            int index = 1;
            // note_voie_codepostal_commune(?,motdeterminant,?,libellesansarticles,?,typedevoie,?,communes.nom,?,voies.cdp_code_postal)
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_sans_articles_pq);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, commune_phonetique);
            if (cdp_present) {
                psCherche.setString(index++, cdp_code_postal);
            }
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }
            
            if (gererAdresse && numero!=0)
            {
                psCherche.setInt(index++,numero);
                psCherche.setString(index++,repetition);
            }
            
            // communes.dpt_code_departement=? AND
            // WHERE communes.dpt_code_departement=? AND note_commune_seul(?,com_nom_pq)>=? 
            psCherche.setString(index++, code_departement);
            psCherche.setString(index++, commune_phonetique);
            psCherche.setInt(index++, 10 * jdonrefParams.obtientNotePourCommuneSelectionnee());

            // note_voie_codepostal_commune(?,motdeterminant,?,libellesansarticles,?,typedevoie,?,communes.nom,?,voies.cdp_code_postal)>=?
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_sans_articles_pq);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, commune_phonetique);
            if (cdp_present) {
                psCherche.setString(index++, cdp_code_postal);
            }
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }
            psCherche.setInt(index++, 10 * jdonrefParams.obtientSeuilDeCorrespondanceDAdresse());

            // voies.t0<=? and communes.t0<=?
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            // LIMIT ?
            psCherche.setInt(index++, jdonrefParams.obtientNombreDeVoieParDefaut());
            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                String presencenumero = null;
                boolean serviceTroncon = false;
                if (numero >= rsCherche.getInt(11+((gererAdresse && numero!=0)?3:0)) && numero <= rsCherche.getInt(12+((gererAdresse && numero!=0)?3:0))) {
                    presencenumero = "";
                    if(numero != 0){
                        serviceTroncon = true;
                    }
                }
                String service = (numero!=0 && gererAdresse && rsCherche.getString(2)!=null)?"2":(serviceTroncon?"3":"4");
                voies.add(new String[]{
                    rsCherche.getString(1), // voi_id
                    rsCherche.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsCherche.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsCherche.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsCherche.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsCherche.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsCherche.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsCherche.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsCherche.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    presencenumero, // indique s'il faut ou pas conserver le numéro dans la ligne 4
                    service,
                    (gererAdresse && numero!=0)? rsCherche.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsCherche.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsCherche.getString(4):null   // adr_rep
                });
                notes.add(new Integer(rsCherche.getInt(6+((gererAdresse && numero!=0)?3:0))));
            }
        }

        HashMap<String,Integer> solutions = new HashMap<String,Integer>(); // gestion de la redondance.

        int maxnote_constant = jdonrefParams.obtientNotePourMotDeterminant() +
                jdonrefParams.obtientNotePourTypeDeVoie() +
                jdonrefParams.obtientNotePourCodePostal();
        int notelibelle = jdonrefParams.obtientNotePourLibelle();
        int notecommune = jdonrefParams.obtientNotePourCommune();
        int notenumero = jdonrefParams.obtientNotePourNumero();
        //
        // POUR CHAQUE SOLUTION TROUVEE, CONSERVE LES DERNIERES MISES A JOUR.
        // voi_id=? and communes.com_code_insee=? and communes.nom=? and communes.cdp_code_postal=?
        for (int i = 0; i < voies.size(); i++) {
            String[] voie = voies.get(i);
            psTime.setString(1, voie[0]);
            psTime.setString(2, voie[1]);
            psTime.setString(3, voie[2]);
            psTime.setString(4, voie[3]);
            psTime.setString(5, voie[4]);
            ResultSet rsTime = psTime.executeQuery();
            // Permet de corriger la note.
            int maxnote = maxnote_constant +
                    notelibelle * Algos.nombreDeMots(voie[10]) +
                    notecommune * Algos.nombreDeMots(Algos.phonexNonVide(voie[9]));
            double notesurmax = ((double) (notes.get(i).intValue() * maxnote)) / NOTE_MAX;
            maxnote += notenumero;
            
            
            // Dedoublonnage
            String candidat = voies.get(i)[1]+" "+voies.get(i)[3];
            Integer ancienne_solution;
            boolean doublon = false;
            if ((ancienne_solution=solutions.get(candidat))!=null)
            {
                int i_ancienne_solution = ancienne_solution.intValue();
                int service_ancienne_solution = Integer.parseInt(voies.get(i_ancienne_solution)[13]);
                if (service_ancienne_solution<Integer.parseInt(voies.get(i)[13]))
                    doublon = true;
                else if (service_ancienne_solution>Integer.parseInt(voies.get(i)[13])) // UNIQUEMENT 2 solutions
                {
                    // suppression de l'ancienne solution
                    voies.remove(i_ancienne_solution);
                    notes.remove(i_ancienne_solution);
                    i--;
                    Iterator<String> keys = solutions.keySet().iterator();
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        if (solutions.get(key).intValue()>i_ancienne_solution)
                        {
                            solutions.put(key,solutions.get(key).intValue()-1);
                        }
                    }
                }
            }

            if (!doublon && rsTime.next()) {
                long dt0;
                long dt1;
                long dtv0 = rsTime.getTimestamp(1).getTime(); // intervalle de validité de la voie
                long dtv1 = rsTime.getTimestamp(2).getTime();

                long dtc0 = rsTime.getTimestamp(3).getTime(); // intervalle de validité de la commune
                long dtc1 = rsTime.getTimestamp(4).getTime();

                int min_numero = rsTime.getInt(5);
                int max_numero = rsTime.getInt(6);

                // Il faut d'abord trouver l'intervalle de validité commune entre la voie et la commune
                if (dtv0 < dtc0) {
                    dt0 = dtc0;
                } else {
                    dt0 = dtv0;
                }
                if (dtv1 < dtc1) {
                    dt1 = dtv1;
                } else {
                    dt1 = dtc1;
                }

                while (rsTime.next()) // compare l'intervalle trouvé à chacun des autres intervalles
                {
                    long tdt1;
                    long tdtv1 = rsTime.getTimestamp(2).getTime();
                    long tdtc1 = rsTime.getTimestamp(4).getTime();

                    if (tdtv1 < tdtc1) {
                        tdt1 = tdtv1;
                    } else {
                        tdt1 = tdtc1;
                    }

                    if (tdt1 > dt1) // si l'intervalle est plus récent, il est pris à la place.
                    {
                        min_numero = rsTime.getInt(5);
                        max_numero = rsTime.getInt(6);

                        long tdtv0 = rsTime.getTimestamp(1).getTime();
                        long tdtc0 = rsTime.getTimestamp(3).getTime();

                        if (tdtv0 < tdtc0) {
                            dt0 = tdtc0;
                        } else {
                            dt0 = tdtv0;
                        }

                        dt1 = tdt1;
                    }
                }

                // redéfinit les bornes de validité
                // WA 09/2011 DateFormat -> DateUtils
                // voie[5] = sdformat.format(dt0);
                // voie[6] = sdformat.format(dt1);
                voie[5] = DateUtils.formatDateToString(dt0, sdformat);
                voie[6] = DateUtils.formatDateToString(dt1, sdformat);


                // et recalcule la note si nécessaire
                // Plusieurs cas sont gérés :
                // 1. la voie est dotée de numéros de voie
                // 2. le voie n'est pas dotée de numéros de voie
                // a. la proposition n'est pas dotée de numéros de voie
                // b. la proposition est dotée de numéros de voie
                //if (min_numero==0 && max_numero==0) // la voie n'est pas dotée de numéros
                //{
                //    if (numero==0) notesurmax += notenumero;
                //}
                // sinon, la voie est dotée de numéro
                //else if (numero==0 || (numero>=min_numero && numero<=max_numero))
                //{
                //    notesurmax += notenumero;
                //}
                // OPTIMISATION DU CODE PRECEDENT
                if (numero != 0)
                {
                    if (gererAdresse && voies.get(i)[14]!=null)
                        notesurmax += notenumero;
                    else
                    if (((min_numero != 0 || max_numero != 0) &&
                        numero >= min_numero && numero <= max_numero)) {
                        if (min_numero%2 == max_numero%2)
                            notesurmax += notenumero/2;
                        else
                            notesurmax += notenumero/4;
                    }
                }
                else
                {
                    notesurmax += notenumero;
                }

                int note = (int) ((notesurmax * NOTE_MAX) / (maxnote));
                notes.set(i, new Integer(note));
                voie[11] = Integer.toString(note);
                solutions.put(candidat,i);
            } else {
                voies.remove(i);
                notes.remove(i);
                i--;
            }
            rsTime.close();
        }
        psTime.close();

        // Comme les notes ont été recalculées, il est nécessaire de les trier.
        voies = triePropositions(voies, notes);

        // Prépare le résultat
        String[] res = new String[11 * voies.size() + VALIDEVOIE_ID];

        res[0] = rechercheexacte ? "1" : "2";
        res[1] = Integer.toString(voies.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[4];

        /*     <li>Identifiant de la voie 1</li>
         *     <li>ligne4 1</li>
         *     <li>ligne4 desabbrevie 1</li>
         *     <li>code insee 1</li>
         *     <li>ligne6 1</li>
         *     <li>ligne6 desabbrevie 1</li>
         *     <li>t0 1 sous la forme JJ/MM/AA</li>
         *     <li>t1 1 sous la forme JJ/MM/AA</li>
         *     <li>distance trouvée 1</li>
         *     <li>code fantoir 1</li>
         *     <li>Identifiant de la voie 2</li>
         *     <li>nom de la voie 2</li>
         *     <li>...</li>*/
        int voisize = voies.size();
        for (int i = 0,  offset = 0; i < voisize; i++, offset += VALIDEVOIE_TABSIZE) {
            String[] str = voies.get(i);
            res[offset + VALIDEVOIE_ID] = str[0];
            if (str[12] == null) {
                res[offset + VALIDEVOIE_LIGNE4] = str[1]; // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = str[8]; // ligne4 desabbrevie
            } else {
                res[offset + VALIDEVOIE_LIGNE4] = Algos.unsplit(strnumero, repetition, str[1]); // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = Algos.unsplit(strnumero, repetition, str[8]); // ligne4 desabbrevie
            }
            res[offset + VALIDEVOIE_CODEINSEE] = str[2]; // code insee
            res[offset + VALIDEVOIE_LIGNE6] = Algos.unsplit(str[4], str[3]); // ligne6
            res[offset + VALIDEVOIE_LIGNE6_DESABBREVIE] = Algos.unsplit(str[4], str[9]); // ligne6 desabbrevie
            res[offset + VALIDEVOIE_T0] = str[5];
            res[offset + VALIDEVOIE_T1] = str[6];
            res[offset + VALIDEVOIE_NOTE] = str[11];
            res[offset + VALIDEVOIE_FANTOIR] = str[7];
            res[offset + VALIDEVOIE_SERVICE] = str[13];
        }

        if (gererPays) {
            res = insertPaysIntoRes(res, 6, pays);
        }

        if (typedevoie_present) {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_COMMUNE + AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE + AGestionLogs.FLAG_VALIDE_TYPEDEVOIE,
                    true);
        } else {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_COMMUNE + AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE, true);
        }

        return res;
    }

    public String[] valideVoieCodePostalCommune(int application, String[] lignes, RefCle rccodepostal, RefCle rccommune, Date date,
            boolean auTroncon, boolean force, boolean gererAdresse, boolean gererPays, String pays, Connection connection) throws SQLException {
        String ligne4 = lignes[3];
        String ligne6 = lignes[5];
        ArrayList<RefNumero> rcnumeros = gestionMots.trouveNumeros(ligne4);
        int numero = 0;
        String strnumero = null;
        String repetition = " ";
        if (rcnumeros.size() > 0) {
            strnumero = rcnumeros.get(0).obtientNumeroNormalise();
            numero = Integer.parseInt(strnumero);
            if (rcnumeros.get(0).obtientRepetition() == null) {
                repetition = " ";
            } else {
                repetition = Character.toString(rcnumeros.get(0).obtientRepetitionNormalise());
            }
        }

        if ((auTroncon || gererAdresse) && numero == 0) {
            return new String[]{"0", "5", "Aucun numéro de voie n'a été spécifié."};
        }

        RefTypeVoie rctypedevoie = gestionMots.trouveTypeVoie(ligne4, rcnumeros);
        String stypedevoie = rctypedevoie.obtientMot();
        boolean typedevoie_present = stypedevoie.length() > 0;
        String stypedevoie_phonetique = Algos.phonexNonVide(stypedevoie);

        RefCle rclibelle;
        if (typedevoie_present) {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, gestionMots.trouveArticleVoie(ligne4, rctypedevoie));
        } else {
            rclibelle = gestionMots.trouveLibelleVoie(ligne4, rcnumeros);
        }

        String slibelle_sans_articles, slibelle_sans_articles_pq, sderniermot_phonetique;
        if (rclibelle.obtientMot().length() > 0) {
            slibelle_sans_articles = Algos.sansarticles(rclibelle.obtientMot());
            slibelle_sans_articles_pq = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        } else {
            typedevoie_present = false;
            slibelle_sans_articles = Algos.sansarticles(ligne4);
            slibelle_sans_articles_pq = Algos.phonexNonVide(slibelle_sans_articles);
            sderniermot_phonetique = Algos.phonexNonVide(Algos.derniermot(rclibelle.obtientMot()));
        }

        if (rccodepostal == null) {
            rccodepostal = gestionMots.trouveCodePostal(ligne6);
        }
        String cdp_code_postal = rccodepostal.obtientMot();
        boolean cdp_present = (cdp_code_postal.length() > 2);
        String code_departement = gestionMots.trouveCodeDepartement(cdp_code_postal).obtientMot();
        if (code_departement.length() == 0) {
            return new String[]{"0", "5", "Aucun département connu n'a été spécifié."};
        }

        // WA 09/2011 utilisation de GestionTables.getXXTableName
        // String nomTable = voi_voies+code_departement;
        String nomTableVoie = GestionTables.getVoiVoiesTableName(code_departement);
        if (!GestionTables.tableExiste(nomTableVoie, connection)) {
            return new String[]{"0", "1", "Le département " + code_departement + " n'est pas géré."};
        }

        String nomTableAdresse = GestionTables.getAdrAdressesTableName(code_departement);
        if (!GestionTables.tableExiste(nomTableAdresse, connection)) {
            gererAdresse = false;
        }
        
        if (rccommune == null) {
            rccommune = gestionMots.trouveNomVille(ligne6, rccodepostal);
        }
        String commune = rccommune.obtientMot();
        String commune_phonetique = Algos.phonexNonVide(commune);

        String arrondissement = gestionMots.trouveNumeroArrondissement(ligne6, rccommune).obtientMot();
        if (arrondissement.length() > 0) {
            arrondissement = Algos.deuxChiffresObligatoires(arrondissement);
        } else {
            arrondissement = null;
        }

        if (date == null) {
            date = Calendar.getInstance().getTime();
        }
        Timestamp tsdate = new Timestamp(date.getTime());

        // Prépare la requête permettant de trouver les informations complémentaires : l'intervalle de validité
        StringBuilder sb = new StringBuilder();
        sb.append(valideVoieCodePostalCommune_psTime_0);
        sb.append(nomTableVoie);
        sb.append(valideVoieCodePostalCommune_psTime_1);
        PreparedStatement psTime = connection.prepareStatement(sb.toString());
        psTime.setTimestamp(6, tsdate);
        psTime.setTimestamp(7, tsdate);

        ArrayList<String[]> voies = new ArrayList<String[]>();
        ArrayList<Integer> notes = new ArrayList<Integer>();

        PreparedStatement psChercheExact = null;
        ResultSet rsChercheExact = null;

        boolean rechercheexacte = false;

        if (!force) {
            sb.setLength(0);
            sb.append(valideVoieCodePostalCommune_psChercheExact_0);
            if (cdp_present) {
                if (arrondissement != null) {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_1_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_1);
                } else {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_2_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_2);
                }
            } else {
                if (arrondissement != null) {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_3_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_3);
                } else {
                    if (gererAdresse && numero!=0)
                        sb.append(valideVoieCodePostalCommune_psChercheExact_4_adr);
                    else
                        sb.append(valideVoieCodePostalCommune_psChercheExact_4);
                }
            }
            sb.append(nomTableVoie);
            if (gererAdresse && numero!=0)
            {
                sb.append(valideVoieCodePostalCommune_psChercheExact_5_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostalCommune_psChercheExact_5_adr_2);
                sb.append(valideVoieCodePostalCommune_psChercheExact_5_adr_3);
            }
            else
                sb.append(valideVoieCodePostalCommune_psChercheExact_5);
            
            if (typedevoie_present) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_6);
            }
            sb.append(valideVoieCodePostalCommune_psChercheExact_7);
            if (cdp_present) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_8);
            }
            if (arrondissement != null) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_9);
            }
            if (auTroncon) {
                sb.append(valideVoieCodePostalCommune_psChercheExact_10);
            }
            sb.append(valideVoieCodePostalCommune_psChercheExact_11);

            psChercheExact = connection.prepareStatement(sb.toString());

            // note_voie_codepostal_commune(?,motdeterminant,?,libellesansarticles,?,typedevoie,?,communes.nom,?,voies.cdp_code_postal)
            int index = 1;
            psChercheExact.setString(index++, sderniermot_phonetique);
            psChercheExact.setString(index++, slibelle_sans_articles_pq);
            psChercheExact.setString(index++, stypedevoie_phonetique);
            psChercheExact.setString(index++, commune_phonetique);
            if (cdp_present) {
                psChercheExact.setString(index++, cdp_code_postal);
            }
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            // JM 02/2015 OUTER LEFT JOIN
            if (gererAdresse && numero!=0)
            {
                psChercheExact.setInt(index++,numero);
                psChercheExact.setString(index++,repetition);
            }
            
            // communes.dpt_code_departement=? AND voi_lbl_sans_articles=? AND voi_type_de_voie=? AND communes.com_nom_pq=? AND voies.cdp_code_postal=? AND
            psChercheExact.setString(index++, code_departement);
            psChercheExact.setString(index++, slibelle_sans_articles);
            if (typedevoie_present) {
                psChercheExact.setString(index++, stypedevoie);
            }
            psChercheExact.setString(index++, commune);
            if (cdp_present) {
                psChercheExact.setString(index++, code_departement);
            }
            if (arrondissement != null) {
                psChercheExact.setString(index++, arrondissement);
            }

            //"voies.voi_min_numero>=? AND voies.voi_max_numero<=? AND "
            if (auTroncon) {
                psChercheExact.setInt(index++, numero);
                psChercheExact.setInt(index++, numero);
            }

            // voies.t0<=? and communes.t0<=?
            psChercheExact.setTimestamp(index++, tsdate);
            psChercheExact.setTimestamp(index++, tsdate);

            // LIMIT ?
            psChercheExact.setInt(index++, jdonrefParams.obtientNombreDeVoieParDefaut());
            rsChercheExact = psChercheExact.executeQuery();
        }

        if (!force && rsChercheExact.next()) {
            rechercheexacte = true;
            do {
                String service = (numero!=0)? (gererAdresse?((rsChercheExact.getString(2)!=null)?"2":"3"):"3"):"4";
                
                voies.add(new String[]{
                    rsChercheExact.getString(1), // voi_id
                    rsChercheExact.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsChercheExact.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsChercheExact.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsChercheExact.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsChercheExact.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsChercheExact.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsChercheExact.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsChercheExact.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    "", // indique qu'il faut conserver le numéro d'adresse dans l'adresse.
                    service,
                    (gererAdresse && numero!=0)? rsChercheExact.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsChercheExact.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsChercheExact.getString(4):null   // adr_rep
                });
                notes.add(new Integer(rsChercheExact.getInt(6+((gererAdresse && numero!=0)?3:0))));
            } while (rsChercheExact.next());

            rsChercheExact.close();
            psChercheExact.close();
        } else {
            if (rsChercheExact != null) {
                rsChercheExact.close();
                psChercheExact.close();
            }

            sb.setLength(0);
            sb.append(valideVoieCodePostalCommune_psCherche_0);
            if (gererAdresse && numero!=0)
                sb.append(valideVoieCodePostalCommune_psCherche_1_adr);
            else
                sb.append(valideVoieCodePostalCommune_psCherche_1);
            if (cdp_present) {
                sb.append(valideVoieCodePostalCommune_psCherche_2);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_3);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_4);
            if (arrondissement != null) {
                sb.append(valideVoieCodePostalCommune_psCherche_5);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_6);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_7);
            sb.append(nomTableVoie);
            if (gererAdresse && numero!=0)
            {
                sb.append(valideVoieCodePostalCommune_psCherche_8_adr_1);
                sb.append(nomTableAdresse);
                sb.append(valideVoieCodePostalCommune_psCherche_8_adr_2);
                sb.append(valideVoieCodePostalCommune_psCherche_8_adr_3);
            }
            else
                sb.append(valideVoieCodePostalCommune_psCherche_8);
            // Sous requête de sélection de la commune
            sb.append(valideVoieCodePostalCommune_psCherche_9);
            if (cdp_present) {
                sb.append(valideVoieCodePostalCommune_psCherche_10);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_11);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_12);
            if (arrondissement != null) {
                sb.append(valideVoieCodePostalCommune_psCherche_13);
            } else {
                sb.append(valideVoieCodePostalCommune_psCherche_14);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_15);
            if (auTroncon) {
                sb.append(valideVoieCodePostalCommune_psCherche_16);
            }
            sb.append(valideVoieCodePostalCommune_psCherche_17);
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());

            int index = 1;
            // note_voie_codepostal_commune(?,motdeterminant,?,libellesansarticles,?,typedevoie,?,communes.nom,?,voies.cdp_code_postal)
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_sans_articles_pq);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, commune_phonetique);
            if (cdp_present) {
                psCherche.setString(index++, cdp_code_postal);
            }
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }
            
            if (gererAdresse && numero!=0)
            {
                psCherche.setInt(index++,numero);
                psCherche.setString(index++,repetition);
            }
            
            // communes.dpt_code_departement=? AND
            // WHERE communes.dpt_code_departement=? AND note_commune_seul(?,com_nom_pq)>=? 
            psCherche.setString(index++, code_departement);
            psCherche.setString(index++, commune_phonetique);
            psCherche.setInt(index++, 10 * jdonrefParams.obtientNotePourCommuneSelectionnee());

            // note_voie_codepostal_commune(?,motdeterminant,?,libellesansarticles,?,typedevoie,?,communes.nom,?,voies.cdp_code_postal)>=?
            psCherche.setString(index++, sderniermot_phonetique);
            psCherche.setString(index++, slibelle_sans_articles_pq);
            psCherche.setString(index++, stypedevoie_phonetique);
            psCherche.setString(index++, commune_phonetique);
            if (cdp_present) {
                psCherche.setString(index++, cdp_code_postal);
            }
            if (arrondissement != null) {
                psCherche.setString(index++, arrondissement);
            }
            psCherche.setInt(index++, 10 * jdonrefParams.obtientSeuilDeCorrespondanceDAdresse());

            // voies.t0<=? and communes.t0<=?
            psCherche.setTimestamp(index++, tsdate);
            psCherche.setTimestamp(index++, tsdate);

            // AND voies.numero_debut<=? AND voies.numero_fin>=?
            if (auTroncon) {
                psCherche.setInt(index++, numero);
                psCherche.setInt(index++, numero);
            }

            // LIMIT ?
            psCherche.setInt(index++, jdonrefParams.obtientNombreDeVoieParDefaut());

            ResultSet rsCherche = psCherche.executeQuery();

            while (rsCherche.next()) {
                String presencenumero = null;
                boolean serviceTroncon = false;
                if (numero >= rsCherche.getInt(11+((gererAdresse && numero!=0)?3:0)) && numero <= rsCherche.getInt(12+((gererAdresse && numero!=0)?3:0))) {
                    presencenumero = "";
                    if(numero != 0){
                        serviceTroncon = true;
                    }
                }
                String service = (numero!=0 && gererAdresse && rsCherche.getString(2)!=null)?"2":(serviceTroncon?"3":"4");
                voies.add(new String[]{
                    rsCherche.getString(1), // voi_id
                    rsCherche.getString(2+((gererAdresse && numero!=0)?3:0)), // nom
                    rsCherche.getString(3+((gererAdresse && numero!=0)?3:0)), // code insee
                    rsCherche.getString(4+((gererAdresse && numero!=0)?3:0)), // nom commune
                    rsCherche.getString(5+((gererAdresse && numero!=0)?3:0)), // code postal
                    null, null, // t0 t1
                    rsCherche.getString(7+((gererAdresse && numero!=0)?3:0)), // code fantoire
                    rsCherche.getString(8+((gererAdresse && numero!=0)?3:0)), // nom desabbrevie
                    rsCherche.getString(9+((gererAdresse && numero!=0)?3:0)), // nom commune desabbrevie
                    rsCherche.getString(10+((gererAdresse && numero!=0)?3:0)), // libellé sans articles phonétique
                    null, // emplacement libre pour la note
                    presencenumero, // indique s'il faut ou pas conserver le numéro dans la ligne 4
                    service,
                    (gererAdresse && numero!=0)? rsCherche.getString(2):null , // adr_id
                    (gererAdresse && numero!=0)? rsCherche.getString(3):null , // adr_numero
                    (gererAdresse && numero!=0)? rsCherche.getString(4):null   // adr_rep
                });
                notes.add(new Integer(rsCherche.getInt(6+((gererAdresse && numero!=0)?3:0))));
            }
        }

        HashMap<String,Integer> solutions = new HashMap<String,Integer>(); // gestion de la redondance.

        int maxnote_constant = jdonrefParams.obtientNotePourMotDeterminant() +
                jdonrefParams.obtientNotePourTypeDeVoie() +
                jdonrefParams.obtientNotePourCodePostal();
        int notelibelle = jdonrefParams.obtientNotePourLibelle();
        int notecommune = jdonrefParams.obtientNotePourCommune();
        int notenumero = jdonrefParams.obtientNotePourNumero();

        //
        // POUR CHAQUE SOLUTION TROUVEE, CONSERVE LES DERNIERES MISES A JOUR.
        // voi_id=? and communes.com_code_insee=? and communes.nom=? and communes.cdp_code_postal=?
        for (int i = 0; i < voies.size(); i++) {
            String[] voie = voies.get(i);
            psTime.setString(1, voie[0]);
            psTime.setString(2, voie[1]);
            psTime.setString(3, voie[2]);
            psTime.setString(4, voie[3]);
            psTime.setString(5, voie[4]);
            ResultSet rsTime = psTime.executeQuery();
            // Permet de corriger la note.
            int maxnote = maxnote_constant +
                    notelibelle * Algos.nombreDeMots(voie[10]) +
                    notecommune * Algos.nombreDeMots(Algos.phonexNonVide(voie[9]));
            double notesurmax = ((double) (notes.get(i).intValue() * maxnote)) / NOTE_MAX;
            maxnote += notenumero;

            // Dedoublonnage
            String candidat = voies.get(i)[1]+" "+voies.get(i)[3];
            Integer ancienne_solution;
            boolean doublon = false;
            if ((ancienne_solution=solutions.get(candidat))!=null)
            {
                int i_ancienne_solution = ancienne_solution.intValue();
                int service_ancienne_solution = Integer.parseInt(voies.get(i_ancienne_solution)[13]);
                if (service_ancienne_solution<Integer.parseInt(voies.get(i)[13]))
                    doublon = true;
                else if (service_ancienne_solution>Integer.parseInt(voies.get(i)[13])) // UNIQUEMENT 2 solutions
                {
                    // suppression de l'ancienne solution
                    voies.remove(i_ancienne_solution);
                    notes.remove(i_ancienne_solution);
                    i--;
                    Iterator<String> keys = solutions.keySet().iterator();
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        if (solutions.get(key).intValue()>i_ancienne_solution)
                        {
                            solutions.put(key,solutions.get(key).intValue()-1);
                        }
                    }
                }
            }
            
            if (!doublon && rsTime.next()) {
                long dt0;
                long dt1;
                long dtv0 = rsTime.getTimestamp(1).getTime(); // intervalle de validité de la voie
                long dtv1 = rsTime.getTimestamp(2).getTime();

                long dtc0 = rsTime.getTimestamp(3).getTime(); // intervalle de validité de la commune
                long dtc1 = rsTime.getTimestamp(4).getTime();

                int min_numero = rsTime.getInt(5);
                int max_numero = rsTime.getInt(6);

                // Il faut d'abord trouver l'intervalle de validité commune entre la voie et la commune
                if (dtv0 < dtc0) {
                    dt0 = dtc0;
                } else {
                    dt0 = dtv0;
                }
                if (dtv1 < dtc1) {
                    dt1 = dtv1;
                } else {
                    dt1 = dtc1;
                }

                while (rsTime.next()) // compare l'intervalle trouvé à chacun des autres intervalles
                {
                    long tdt1;
                    long tdtv1 = rsTime.getTimestamp(2).getTime();
                    long tdtc1 = rsTime.getTimestamp(4).getTime();

                    if (tdtv1 < tdtc1) {
                        tdt1 = tdtv1;
                    } else {
                        tdt1 = tdtc1;
                    }

                    if (tdt1 > dt1) // si l'intervalle est plus récent, il est pris à la place.
                    {
                        min_numero = rsTime.getInt(5);
                        max_numero = rsTime.getInt(6);

                        long tdtv0 = rsTime.getTimestamp(1).getTime();
                        long tdtc0 = rsTime.getTimestamp(3).getTime();

                        if (tdtv0 < tdtc0) {
                            dt0 = tdtc0;
                        } else {
                            dt0 = tdtv0;
                        }

                        dt1 = tdt1;
                    }
                }

                // redéfinit les bornes de validité
                // WA 09/2011 DateFormat -> DateUtils
                // voie[5] = sdformat.format(dt0);
                // voie[6] = sdformat.format(dt1);
                voie[5] = DateUtils.formatDateToString(dt0, sdformat);
                voie[6] = DateUtils.formatDateToString(dt1, sdformat);


                // et recalcule la note si nécessaire
                // Plusieurs cas sont gérés :
                // 1. la voie est dotée de numéros de voie
                // 2. le voie n'est pas dotée de numéros de voie
                // a. la proposition n'est pas dotée de numéros de voie
                // b. la proposition est dotée de numéros de voie
                //if (min_numero==0 && max_numero==0) // la voie n'est pas dotée de numéros
                //{
                //    if (numero==0) notesurmax += notenumero;
                //}
                // sinon, la voie est dotée de numéro
                //else if (numero==0 || (numero>=min_numero && numero<=max_numero))
                //{
                //    notesurmax += notenumero;
                //}
                // OPTIMISATION DU CODE PRECEDENT
                if (numero != 0)
                {
                    if (gererAdresse && voies.get(i)[14]!=null)
                        notesurmax += notenumero;
                    else
                    if (((min_numero != 0 || max_numero != 0) &&
                        numero >= min_numero && numero <= max_numero)) {
                        if (min_numero%2 == max_numero%2)
                            notesurmax += notenumero/2;
                        else
                            notesurmax += notenumero/4;
                    }
                }
                else
                {
                    notesurmax += notenumero;
                }

                int note = (int) ((notesurmax * NOTE_MAX) / (maxnote));
                notes.set(i, new Integer(note));
                voie[11] = Integer.toString(note);
                solutions.put(candidat,i);
            } else {
                voies.remove(i);
                notes.remove(i);
                i--;
            }
            rsTime.close();
        }
        psTime.close();

        // Comme les notes ont été recalculées, il est nécessaire de les trier.
        voies = triePropositions(voies, notes);

        // Prépare le résultat
        String[] res = new String[11 * voies.size() + VALIDEVOIE_ID];

        res[0] = rechercheexacte ? "1" : "2";
        res[1] = Integer.toString(voies.size());
        res[2] = lignes[0];
        res[3] = lignes[1];
        res[4] = lignes[2];
        res[5] = lignes[4];

        if (rcnumeros.size() > 0) {
            RefNumero premier = rcnumeros.get(0);
            if (premier.obtientRepetition() != null) {
                repetition = Character.toString(premier.obtientRepetitionNormalise());
            } else {
                repetition = "";
            }
        } else {
            repetition = "";
        }

        /*     <li>Identifiant de la voie 1</li>
         *     <li>ligne4 1</li>
         *     <li>ligne4 desabbrevie 1</li>
         *     <li>code insee 1</li>
         *     <li>ligne6 1</li>
         *     <li>ligne6 desabbrevie 1</li>
         *     <li>t0 1 sous la forme JJ/MM/AA</li>
         *     <li>t1 1 sous la forme JJ/MM/AA</li>
         *     <li>distance trouvée 1</li>
         *     <li>code fantoir 1</li>
         *     <li>Identifiant de la voie 2</li>
         *     <li>nom de la voie 2</li>
         *     <li>...</li>*/
        int voisize = voies.size();
        for (int i = 0,  offset = 0; i < voisize; i++, offset += VALIDEVOIE_TABSIZE) {
            String[] str = voies.get(i);
            res[offset + VALIDEVOIE_ID] = str[0];
            if (str[12] == null) {
                res[offset + VALIDEVOIE_LIGNE4] = str[1]; // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = str[8]; // ligne4 desabbrevie
            } else {
                res[offset + VALIDEVOIE_LIGNE4] = Algos.unsplit(strnumero, repetition, str[1]); // ligne4
                res[offset + VALIDEVOIE_LIGNE4_DESABBREVIE] = Algos.unsplit(strnumero, repetition, str[8]); // ligne4 desabbrevie
            }
            res[offset + VALIDEVOIE_CODEINSEE] = str[2]; // code insee
            res[offset + VALIDEVOIE_LIGNE6] = Algos.unsplit(str[4], str[3]); // ligne6
            res[offset + VALIDEVOIE_LIGNE6_DESABBREVIE] = Algos.unsplit(str[4], str[9]); // ligne6 desabbrevie
            res[offset + VALIDEVOIE_T0] = str[5];
            res[offset + VALIDEVOIE_T1] = str[6];
            res[offset + VALIDEVOIE_NOTE] = str[11];
            res[offset + VALIDEVOIE_FANTOIR] = str[7];
            res[offset + VALIDEVOIE_SERVICE] = str[13];
        }

        if (gererPays) {
            res = insertPaysIntoRes(res, 6, pays);
        }

        if (typedevoie_present) {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_COMMUNE + AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE + AGestionLogs.FLAG_VALIDE_TYPEDEVOIE,
                    true);
        } else {
            jdonrefParams.getGestionLog().logValidation(application, code_departement,
                    AGestionLogs.FLAG_VALIDE_COMMUNE + AGestionLogs.FLAG_VALIDE_CODEPOSTAL + AGestionLogs.FLAG_VALIDE_LIBELLE, true);
        }

        return res;
    }

    /**
     * Trie les propositions par rapport à leur note.<br>
     * Un tri-bulle est implémenté.<br>
     * La liste retournée est vide s'il n'y a pas autant de propositions que de notes ou si l'un
     * des tableaux est null.
     */
    private ArrayList<String[]> triePropositions(ArrayList<String[]> propositions, ArrayList<Integer> notes) {
        ArrayList<String[]> res = new ArrayList<String[]>();
        ArrayList<Integer> nvl_notes = new ArrayList<Integer>();

        if (propositions == null || notes == null) {
            return res;
        }
        if (propositions.size() != notes.size()) {
            return res;
        }
        if (propositions.size() == 0) {
            return res;
        }

        res.add(propositions.get(0));
        nvl_notes.add(notes.get(0));
        for (int i = 1,  k = 1; i < propositions.size(); i++, k++) {
            int j = k; // taille de nvl_notes, augmente de 1 à chaque itération.
            int note = notes.get(i);
            // la recherche commence à la fin car globalement, les notes sont bien ordonnées.
            while (j > 0 && note > nvl_notes.get(j - 1).intValue()) {
                j--;
            }

            if (j < nvl_notes.size()) {
                res.add(j, propositions.get(i));
                nvl_notes.add(j, notes.get(i));
            } else {
                res.add(propositions.get(i));
                nvl_notes.add(notes.get(i));
            }
        }

        return res;
    }
}
