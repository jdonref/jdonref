/*
 * GestionReferentiel.java
 *
 * Created on 17 mars 2008, 16:16
 * 
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
 * julien.moquet@interieur.gouv.fr
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
package ppol.jdonref.referentiel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.AGestionLogs;
import ppol.jdonref.Algos;
//import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFParams;

import ppol.jdonref.Processus;
import ppol.jdonref.Tables.Colonne;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.Tables.DescriptionTable;
import ppol.jdonref.Tables.GestionTables;
import ppol.jdonref.Tables.Index;
import ppol.jdonref.dao.PayPaysBean;
import ppol.jdonref.dao.RecherchesDao;
import ppol.jdonref.mots.CategorieMot;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.mots.Mot;
import ppol.jdonref.mots.RefCle;
import ppol.jdonref.mots.RefCommune;
import ppol.jdonref.mots.RefNumero;
import ppol.jdonref.mots.RefPays;
import ppol.jdonref.utils.DateUtils;

/**
 * Fournit des méthodes qui nécessitent un accès au référentiel.<br>
 * Certaines méthodes retournent une liste de chaines, dont la première est le code de la méthode.<br>
 * Dans ce cas, le code de la méthode utilisé est:
 * <ul>
 * <li>1 valideVoieCodePostalCommune</li>
 * <li>2 valideVoieCodePostal</li>
 * <li>3 valideCommuneEtCodePostal</li>
 * <li>4 valideCommune</li>
 * <li>5 valideCodePostal</li>
 * <li>1 2 3 4 ou 5 pour valideVoie</li>
 * <li>6 revalide</li>
 * <li>7 geocode</li>
 * </ul>
 * @author jmoquet
 */
public class GestionReferentiel {

    public static final int SERVICE_ADRESSE = 1;
    public static final int SERVICE_POINT_ADRESSE = 2;
    public static final int SERVICE_TRONCON = 3;
    public static final int SERVICE_VOIE = 4;
    public static final int SERVICE_COMMUNE = 5;
    public static final int SERVICE_DEPARTEMENT = 6;
    public static final int SERVICE_PAYS = 7;
    static private String empty = "";
    static private String en_cours = "EN COURS";
    static private String erreur = "ERREUR";
    static private String gestion_referentiel = "GestionReferentiel";
    static private String maj = "MAJ";
    static private String lancement = "LANCEMENT";
    static private String preparation = "PREPARATION";
    static public final int MAJ_DEPARTEMENTS = 1;
    static public final int MAJ_COMMUNES = 2;
    static public final int MAJ_TRONCONS = 4;
    static public final int MAJ_VOIES = 8;
    static public final int MAJ_CODEPOSTAUX = 16;
    static public final int MAJ_ADRESSES = 32;
    static public final int MAJ_TABLEVOIES = 64;
    static public final int MAJ_TABLECODEPOSTAUX = 128;
    static public final int MAJ_VOIESAMBIGUES = 256;
    protected JDONREFParams jdonrefParams = null;
    protected GestionMots gestionMots = null;
    protected GestionMiseAJour gestionMiseAJour = null;
    protected GestionValidation gestionValidation = null;
    // Utilisé pour formatter les dates des méthodes de validation.
    // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//   static final DateFormat dtformat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.FRANCE);
//   static final DateFormat dformat = DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.FRANCE);
//   static final SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE);
    protected final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;

    public GestionValidation getGestionValidation() {
        return gestionValidation;
    }

    public GestionMiseAJour getGestionMiseAJour() {
        return gestionMiseAJour;
    }

    public void setGestionMiseAJour(GestionMiseAJour gestionMiseAJour) {
        this.gestionMiseAJour = gestionMiseAJour;
    }

    /** Creates a new instance of GestionReferentiel */
    public GestionReferentiel(GestionMots gestionMots, GestionMiseAJour gestionMiseAJour, JDONREFParams jdonrefParams) {
        gestionValidation = new GestionValidation();
        this.gestionMots = gestionMots;
        gestionValidation.setGestionMots(gestionMots);
        this.jdonrefParams = jdonrefParams;
        gestionValidation.setJdonrefParams(jdonrefParams);
        this.gestionMiseAJour = gestionMiseAJour;
    }

    /**
     * 
     */
    public JDONREFParams obtientParametres() {
        return jdonrefParams;
    }

    /**
     * Définit les paramètres utilisés par les méthodes de la classe.
     */
    public void definitParametres(JDONREFParams jdonrefParams) {
        gestionValidation.setJdonrefParams(jdonrefParams);
        this.jdonrefParams = jdonrefParams;
    }
    String obtientGeometries_psTroncons_0 = "SELECT astext(geometrie) FROM \"";
    String obtientGeometries_psTroncons_1 = "\" WHERE voi_id_droit=? or voi_id_gauche=?;";

    /**
     * Obtiens les géométries des tronçons de la voie spécifiée.<br>
     * Attention : injection sql possible dans nomTableTroncon.<br>
     * Si des erreurs de géométries sont rencontrées, les erreurs sont remontées dans le fichier de log.<br>
     * INDEX A CREER pour optimiser cette méthode:
     * <ul><li>pour nomTableTroncon : voi_id_droit, voi_id_gauche séparemment</li></ul>
     * @throws SQLException exception durant la création de la requête, son exécution, l'exploitation du résultat...
     */
    public ArrayList<Geometry> obtientGeometries(String idvoie, String nomTableTroncon, Connection connection) throws SQLException {
        ArrayList<Geometry> geometries = new ArrayList<Geometry>();

        // Commence par chercher les tronçons qui composent cette voie.
        StringBuffer sb = new StringBuffer();

        sb.append(obtientGeometries_psTroncons_0);
        sb.append(nomTableTroncon);
        sb.append(obtientGeometries_psTroncons_1);

        PreparedStatement psTroncons = connection.prepareStatement(sb.toString()); // SQLException
        psTroncons.setString(1, idvoie);
        psTroncons.setString(2, idvoie);

        ResultSet rsTroncons = psTroncons.executeQuery(); // SQLException

        WKTReader reader = new WKTReader();

        while (rsTroncons.next()) // SQLException
        {
            String geometrie = rsTroncons.getString(1); // SQLException

            Geometry g = null;

            try {
                g = reader.read(geometrie); // SQLException
            } catch (ParseException pe) {
                Logger.getLogger(gestion_referentiel, pe.getMessage());
            } catch (NullPointerException npe) {
                Logger.getLogger(gestion_referentiel, npe.getMessage());
            }

            geometries.add(g);
        }

        rsTroncons.close();
        psTroncons.close();

        return geometries;
    }

    /**
     * Retourne le nombre de troncons qui sont proches entre les deux géométries spécifiées.
     * Deux tronçons sont proches lorsque 
     */
    private int nbTronconProches(ArrayList<Geometry> geometries1, ArrayList<Geometry> geometries2) {
        // Calcule les distances tronçons à tronçons, jusqu'à ce qu'un
        // nombre suffisant ai été trouvé.
        int nbtronconsproche = 0;

        for (int j = 0; (j < geometries2.size()) && (nbtronconsproche < jdonrefParams.obtientSeuilDeTronconsProches()); j++) {
            boolean suivant = false;

            for (int i = 0; (i < geometries1.size()) && !suivant; i++) {
                double distance = DistanceOp.distance(geometries1.get(i), geometries2.get(j)); // Permet de calculer les distances entre géométries.
                if (distance <= jdonrefParams.obtientSeuilDeDistanceDeVoies()) // Le référentiel est stocké au centimêtre près
                {
                    nbtronconsproche++;
                    suivant = true;
                }
            }
        }

        return nbtronconsproche;
    }
    String chercheCorrespondanceVoie_stVoies_0 = "SELECT voies.voi_id FROM (SELECT voi_id,voi_lbl_pq,voi_type_de_voie FROM \"";
    String chercheCorrespondanceVoie_stVoies_1 = "\" WHERE com_code_insee='";
    String chercheCorrespondanceVoie_stVoies_2 = "') as voies WHERE distance_levenstein(voies.voi_lbl_pq,'";
    String chercheCorrespondanceVoie_stVoies_3 = "')<";
    String chercheCorrespondanceVoie_stVoies_4 = " AND estabbreviation(voies.voi_type_de_voie,'";
    String chercheCorrespondanceVoie_stVoies_5 = "')=1;";

    /**
     * Permet de chercher dans la table de voies spécifiée les voies qui correspondent fortement à 
     * la voie spécifiée.<br>
     * Les voies sont validées si leur nom est proche, leur code insee égale, et leur géométrie proche.<br>
     * La géométrie de deux voies est proche, si la géométrie de plusieurs tronçons sont proche.<br>
     * jdonrefParams est utilisé pour déterminer ce que "proche" signifie:<br>
     * <ul><li>deux noms sont proche lorsque les types sont une abbreviation l'un de l'autre et que les libelle ont quelques fautes d'écart (phonétique tolérée).</li>
     *     <li>deux géométries sont proches lorsque le nombre indiqué de auTroncon est proche s'il possédent suffisamment de auTroncon, ou
     * lorsque un nombre non nul de troncons sont proche.</li>
     * </ul>
     * Utilise la procédure stockée levenstein.<br>
     * Attention : injection SQL possible dans tableVoies, codeInsee, et nom.<br>
     * @throws SQLException exception durant la création de la requête, son exploitation, l'obtention des géométries d'une voie, ...
     */
    public ArrayList<String> chercheCorrespondanceVoie(String nom, String codeInsee, ArrayList<Geometry> geometries, String tableVoies,
            String tableTroncons, Connection connection) throws SQLException {
        ArrayList<String> idvoies = new ArrayList<String>();
        RefCle article, libelle = null;

        RefCle typedevoie = gestionMots.trouveTypeVoie(nom, null);
        if (typedevoie.obtientMot().length() == 0) {
            article = new RefCle("", 0);
            libelle = new RefCle(nom, 0);
        } else {
            article = gestionMots.trouveArticleVoie(nom, typedevoie);
            libelle = gestionMots.trouveLibelleVoie(nom, article);
        }

        // Commence par chercher les voies dont le nom est proche et le code insee égal.        
        StringBuffer sb = new StringBuffer();
        sb.append(chercheCorrespondanceVoie_stVoies_0);
        sb.append(tableVoies);
        sb.append(chercheCorrespondanceVoie_stVoies_1);
        sb.append(codeInsee);
        sb.append(chercheCorrespondanceVoie_stVoies_2);
        sb.append(Algos.formatSQL(Algos.phonex(libelle.obtientMot())));
        sb.append(chercheCorrespondanceVoie_stVoies_3);
        sb.append(jdonrefParams.obtientSeuilDeCorrespondanceDeLibelle());
        sb.append(chercheCorrespondanceVoie_stVoies_4);
        sb.append(Algos.formatSQL(typedevoie.obtientMot()));
        sb.append(chercheCorrespondanceVoie_stVoies_5);

        Statement stVoies = connection.createStatement(); // SQLException

        ResultSet rsVoies = stVoies.executeQuery(sb.toString()); // SQLException

        int seuiltroncon = jdonrefParams.obtientSeuilDeTronconsProches();

        // Pour chaque voie qui correspond apparemment à la voie recherchée,
        // compare sa géométrie à celle de la voie.
        while (rsVoies.next()) // SQLException
        {
            String idvoie = rsVoies.getString(1); //SQLException

            // Obtient les géométries de la voie en cours,
            ArrayList<Geometry> geometries2 = obtientGeometries(idvoie, tableTroncons, connection); // SQLException

            int nbtronconsproches = nbTronconProches(geometries, geometries2);

            if (geometries.size() > seuiltroncon && geometries2.size() > seuiltroncon) {
                if (nbtronconsproches >= jdonrefParams.obtientSeuilDeTronconsProches()) {
                    idvoies.add(idvoie);
                }
            } else {
                if (nbtronconsproches > 0) {
                    idvoies.add(idvoie);
                }
            }
        }

        return idvoies;
    }

    /**
     * Obtient une date considérée comme infinie.
     */
    private Timestamp obtientDateInfinie(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 50);
        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * Obtient une date précédent la date.
     */
    private Timestamp obtientDateMoinsUn(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, -1);
        return new Timestamp(c.getTimeInMillis());
    }
    String mise_a_jour_0 = "MISE A JOUR";
    String mise_a_jour_1 = "Departement : ";
    String mise_a_jour_2 = "FLAGS : ";
    String mise_a_jour_3 = "SUPPRIME MARQUEURS";
    String mise_a_jour_voi_voies = "voi_voies_";
    String mise_a_jour_4 = "MET A JOUR LA STRUCTURE";

    /**
     * Met à jour un département du référentiel à partir d'une mise à jour de référentiel.<br>
     * ATTENTION: La table idvoies de la mise à jour doit contenir les identifiants de toutes les voies
     * déjà présentes dans le référentiel. Après l'exécution de cette méthode, les nouveaux identifiants
     * apportés par cette mise à jour sont ajoutés à idvoies.
     * Le référentiel actuel et la mise à jour doivent se trouver dans des bases différentes.<br>
     * Les noms des tables troncons d'origine sont référencées par la table tablesToncons.<br>
     * Les tables voies, adresses, et voiesAmbigues d'origine sont de la forme:
     * <ul><li>voi_voies_code_departement</li>
     *     <li>adr_adresses_code_departement</li>
     *     <li>voa_voies_ambigues_code_departement</li></ul>
     * Les noms des tables de destination sont de la forme:
     * <ul><li>tro_troncons_code_departement</li>
     *     <li>voi_voies_code_departement</li>
     *     <li>adr_adresses_code_departement</li>
     *     <li>voi_voies_code_departement</li></ul><br>
     * Attention: Une seule mise à jour doit être effectuée par minute !
     * @param connectionReferentiel la connection au référentiel mis à jour
     * @param connectionMaj la connection au référentiel qui contient la mise à jour
     * @param flags permet de spécifier les objets à mettre à jour en utilisant des combinaisons des bits suivants:
     * <ul>
     * <li>1 departements</li>
     * <li>2 communes et arrondissements</li>
     * <li>4 troncons</li>
     * <li>8 voies</li>
     * <li>16 code postaux</li>
     * <li>32 adresses</li>
     * <li>64 table voies, s'il est présent la table de voies est recalculée à partir de la table de tronçons.</li>
     * <li>128 table des codes postaux, s'il est présent la table des codes postaux est recalculée à partir de la table des voies.</li>
     * <li>256 ne pas mettre à jour les voies ambigues</li>
     * </ul>
     * @throws GestionReferentielException problème avec une structure
     */
    public void mise_a_jour(Processus p, String code_departement, int flags, Connection connectionReferentiel, Connection connectionMaj,
            Date date) throws SQLException, GestionReferentielException {
        p.state = new String[]{
            en_cours, maj, lancement
        };

//        GestionLogs.getInstance().logAdmin(p.numero, p.version, mise_a_jour_0);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, mise_a_jour_0);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, mise_a_jour_1 + code_departement);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, mise_a_jour_2 + flags);
        p.resultat.add(mise_a_jour_0);
        p.resultat.add(mise_a_jour_1 + code_departement);
        p.resultat.add(mise_a_jour_2 + flags);

        boolean autocommitOrigine = false, autocommitDestination = false;

        Timestamp tsdate = new Timestamp(date.getTime());
        Timestamp tsdatemoinsun = obtientDateMoinsUn(date);
        Timestamp tsinfini = obtientDateInfinie(date);

        autocommitOrigine = connectionReferentiel.getAutoCommit();
        autocommitDestination = connectionMaj.getAutoCommit();
        connectionReferentiel.setAutoCommit(false);
        connectionMaj.setAutoCommit(false);

        try {
            String[] marqueurs = null;

            p.state[2] = mise_a_jour_3;
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, mise_a_jour_3);
            // Supprime d'éventuels marqueurs oubliés.
            GestionMarqueurs.purgeMarqueurs(code_departement, flags, connectionMaj);
            if ((flags & MAJ_VOIES) != 0) // WA 09/2011 utilisation de GestionTables.getXXTableName
            //                GestionMarqueurs.purgeMarqueursDeTable(mise_a_jour_voi_voies+code_departement,connectionReferentiel);
            {
                GestionMarqueurs.purgeMarqueursDeTable(
                        GestionTables.getTableNameWithPrefixAndSuffix(mise_a_jour_voi_voies, code_departement), connectionReferentiel);
            }

            p.state[2] = mise_a_jour_4;
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, mise_a_jour_4);
            // Met à jour la structure du référentiel (la crée éventuellement)
            GestionStructure.creeStructures(code_departement, flags, connectionReferentiel, date);

            // Obtient le nom de la table de auTroncon.
            String nomTableTroncon = null;

            if ((flags & MAJ_TRONCONS) != 0) {
                nomTableTroncon = GestionHistoriqueTables.obtientDerniereTableTroncon(connectionReferentiel, code_departement);
            }

            if ((flags & MAJ_TRONCONS) != 0 && nomTableTroncon == null) {
                p.state = new String[]{
                    erreur, "La table de troncon n'a pas été identifiée."
                };
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "La table de troncon n'a pas été identifiée.");
            } else {
                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_TABLEVOIES) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREE LA TABLE DE VOIES");
                    p.state = new String[]{
                        en_cours, maj, "CREE LA TABLE DE VOIES", lancement, "TRONCONS TRAITES", "0", "SUR 0"
                    };
                    // Crée la table des voies pour la mise à jour.
                    // logs dans la méthode
                    // WA 09/2011 utilisation de GestionTables.getXXTableName
//                    gestionMiseAJour.creeTableVoieMaj(p,code_departement,"tro_troncons_"+code_departement,"voi_voies_"+code_departement,"idv_id_voies",connectionMaj);
                    gestionMiseAJour.creeTableVoieMaj(p, code_departement, GestionTables.getTroTronconsTableName(code_departement),
                            "voi_voies_" + code_departement, "idv_id_voies", connectionMaj);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_TABLECODEPOSTAUX) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREE LA TABLE DES CODES POSTAUX");
                    p.state = new String[]{
                        en_cours, maj, "CREE LA TABLE DES CODES POSTAUX", lancement, "VOIES TRAITEES", "0"
                    };
                    // Crée la table des code postaux pour la mise à jour.
                    // logs dans la méthode
                    gestionMiseAJour.creeTableCodePostauxMaj(p, code_departement, connectionMaj);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                p.state[2] = "CREE LES INDEX";
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREE LES INDEX");
                // crée les index nécessaires sur le référentiel et  la mise à jour
                GestionStructure.creeIndexesMaj(code_departement, flags, connectionMaj);
                GestionStructure.creeIndexesReferentiel(code_departement, nomTableTroncon, flags, connectionReferentiel);

                // Crée les marqueurs sur les tables sources
                // Les marques utilisées sont 1 pour identique, et 2 pour modifié
                p.state[2] = "CREE LES MARQUEURS";
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREE LES MARQUEURS");
                marqueurs = GestionMarqueurs.ajouteMarqueursMaj(code_departement, flags, connectionMaj);

                p.state = new String[]{
                    en_cours, maj, "INVALIDATION", "",
                    lancement, "", ""
                };

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_ADRESSES) != 0) {
                    p.state[3] = "ADRESSES";
                    p.state[4] = lancement;
                    p.state[5] = "ADRESSES TRAITEES";
                    p.state[6] = "0";

                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES ADRESSES");
                    // Invalide les adresses et marque les autres
                    // logs dans la méthode
                    gestionMiseAJour.invalideObjets(p, "adr_id",
                            "adr_adresses_" +
                            code_departement,
                            "adr_adresses_" +
                            code_departement,
                            marqueurs[GestionMarqueurs.MARQUEUR_ADRESSES],
                            GestionDescriptionTables.creeDescriptionTableAdressesReferentiel(),
                            connectionMaj,
                            connectionReferentiel,
                            tsdate, tsdatemoinsun);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_CODEPOSTAUX) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES CODES POSTAUX");
                    p.state = new String[] // Exception, les autres seront corrigés au fur et à mesure (pour le total)
                        {
                        en_cours, maj, "INVALIDATION", "CODES POSTAUX",
                        lancement, "", "", ""
                    };

                    // logs dans la méthode
                    gestionMiseAJour.invalideCodePostaux(p,
                            code_departement,
                            marqueurs[GestionMarqueurs.MARQUEUR_CODEPOSTAUX],
                            tsdate,
                            tsdatemoinsun,
                            connectionMaj,
                            connectionReferentiel);
                    p.state = new String[]{
                        en_cours, maj, "INVALIDATION", "",
                        lancement, "", ""
                    };
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_VOIES) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES VOIES");
                    // Invalide les voies et marque les autres
                    p.state[3] = "VOIES";
                    p.state[4] = lancement;
                    p.state[5] = "VOIES TRAITEES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.invalideVoies(p,
                            code_departement,
                            marqueurs[GestionMarqueurs.MARQUEUR_VOIES],
                            connectionMaj,
                            connectionReferentiel,
                            tsdate, tsdatemoinsun);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_TRONCONS) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES TRONCONS");
                    // Invalide les troncons et marque les autres
                    p.state[3] = "TRONCONS";
                    p.state[4] = lancement;
                    p.state[5] = "TRONCONS TRAITES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.invalideTroncons(p, code_departement,
                            marqueurs[GestionMarqueurs.MARQUEUR_TRONCONS],
                            nomTableTroncon,
                            connectionMaj,
                            connectionReferentiel,
                            tsdate, tsdatemoinsun);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_COMMUNES) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES COMMUNES");
                    // Invalide les communes et arrondissements
                    p.state[3] = "COMMUNES ET ARRONDISSEMENTS";
                    p.state[4] = lancement;
                    p.state[5] = "COMMUNES TRAITEES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.invalideCommunes(p,
                            marqueurs[GestionMarqueurs.MARQUEUR_COMMUNES],
                            connectionMaj,
                            connectionReferentiel,
                            tsdate,
                            tsdatemoinsun);
                }


                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_DEPARTEMENTS) != 0) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES DEPARTEMENTS");
                    // Invalide les départements
                    p.state[3] = "DEPARTEMENTS";
                    p.state[4] = lancement;
                    p.state[5] = "DEPARTEMENTS TRAITES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.invalideObjets(p,
                            "dpt_code_departement",
                            "dpt_departements", "dpt_departements",
                            marqueurs[GestionMarqueurs.MARQUEUR_DEPARTEMENTS],
                            GestionDescriptionTables.creeDescriptionTableDepartementsReferentiel(),
                            connectionMaj, connectionReferentiel,
                            tsdate, tsdatemoinsun);
                }


                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                p.state = new String[]{
                    en_cours, maj, "CREATION", "",
                    lancement, "", ""
                };


                if ((flags & MAJ_DEPARTEMENTS) != 0) {
                    // crée les nouveaux départements
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES DEPARTEMENTS");
                    p.state[3] = "DEPARTEMENTS";
                    p.state[4] = lancement;
                    p.state[5] = "DEPARTEMENTS TRAITES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.creeNouveauxObjets(p, "dpt_departements", "dpt_departements",
                            marqueurs[GestionMarqueurs.MARQUEUR_DEPARTEMENTS],
                            GestionDescriptionTables.creeDescriptionTableDepartementsReferentiel(),
                            connectionReferentiel, connectionMaj,
                            tsdate, tsinfini);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_COMMUNES) != 0) {
                    // crée les nouvelles communes et arrondissements
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES COMMUNES");
                    p.state[3] = "COMMUNES ET ARRONDISSEMENTS";
                    p.state[4] = lancement;
                    p.state[5] = "COMMUNES TRAITEES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.creeNouvellesCommunes(p,
                            marqueurs[GestionMarqueurs.MARQUEUR_COMMUNES],
                            connectionReferentiel, connectionMaj,
                            tsdate, tsinfini);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_TRONCONS) != 0) {
                    // crée les nouveaux troncons
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES TRONCONS");
                    p.state[3] = "TRONCONS";
                    p.state[4] = lancement;
                    p.state[5] = "TRONCONS TRAITES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    // WA 09/2011 utilisation de GestionTables.getXXTableName
//                    gestionMiseAJour.creeNouveauxObjets(p,
//                                                        nomTableTroncon,"tro_troncons_"+code_departement,
//                                                        marqueurs[GestionMarqueurs.MARQUEUR_TRONCONS],
//                                                        GestionDescriptionTables.creeDescriptionTableTronconReferentiel(),
//                                                        connectionReferentiel,connectionMaj,
//                                                        tsdate,tsinfini);
                    gestionMiseAJour.creeNouveauxObjets(p,
                            nomTableTroncon, GestionTables.getTroTronconsTableName(code_departement),
                            marqueurs[GestionMarqueurs.MARQUEUR_TRONCONS],
                            GestionDescriptionTables.creeDescriptionTableTronconReferentiel(),
                            connectionReferentiel, connectionMaj,
                            tsdate, tsinfini);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_VOIES) != 0) {
                    // crée les nouvelles voies
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES VOIES");
                    p.state[3] = "VOIES";
                    p.state[4] = lancement;
                    p.state[5] = "VOIES TRAITEES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.creeNouvellesVoies(p,
                            marqueurs[GestionMarqueurs.MARQUEUR_VOIES], code_departement,
                            connectionMaj, connectionReferentiel,
                            (flags & GestionReferentiel.MAJ_VOIESAMBIGUES) == 0,
                            tsdate, tsinfini);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_CODEPOSTAUX) != 0) {
                    // Crée les nouveaux code postaux
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES CODES POSTAUX");
                    p.state[3] = "CODEPOSTAUX";
                    p.state[4] = lancement;
                    p.state[5] = "VOIES TRAITEES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.creeNouveauxCodesPostaux(p, code_departement,
                            marqueurs[GestionMarqueurs.MARQUEUR_CODEPOSTAUX],
                            connectionReferentiel, connectionMaj,
                            tsdate, tsinfini);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_ADRESSES) != 0) {
                    // crée les nouvelles adresses
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES ADRESSES");
                    p.state[3] = "ADRESSES";
                    p.state[4] = lancement;
                    p.state[5] = "ADRESSES TRAITEES";
                    p.state[6] = "0";
                    // logs dans la méthode
                    gestionMiseAJour.creeNouveauxObjets(p,
                            "adr_adresses_" + code_departement, "adr_adresses_" + code_departement,
                            marqueurs[GestionMarqueurs.MARQUEUR_ADRESSES],
                            GestionDescriptionTables.creeDescriptionTableAdressesReferentiel(),
                            connectionReferentiel, connectionMaj,
                            tsdate, tsinfini);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & (MAJ_COMMUNES + MAJ_VOIES)) != 0 && // les voies ou les communes peuvent faire évoluer
                        (flags & GestionReferentiel.MAJ_VOIESAMBIGUES) == 0) // cette catégorie d'ambiguités
                {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CALCULE AMBIGUITE COMMUNE DANS VOIE");
                    p.state = new String[]{
                        en_cours, maj, "CALCULE AMBIGUITE",
                        "COMMUNE DANS VOIE", lancement,
                        "VOIES TRAITEES", "0"
                    };

                    // Calcule les communes ambigues
                    // logs dans méthode
                    gestionMiseAJour.calculeCommunesAmbiguesDansVoies(p,
                            code_departement,
                            connectionReferentiel);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                if ((flags & MAJ_COMMUNES) != 0 && // les voies ou les communes peuvent faire évoluer
                        (flags & GestionReferentiel.MAJ_VOIESAMBIGUES) == 0) // cette catégorie d'ambiguités
                {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CALCULE AMBIGUITE CLES DANS COMMUNE");
                    p.state = new String[]{
                        en_cours, maj, "CALCULE AMBIGUITE",
                        "CLES DANS COMMUNE", lancement,
                        "VOIES TRAITEES", "0"
                    };

                    // Calcule les communes ambigues
                    // logs dans méthode
                    gestionMiseAJour.calculeClesAmbiguesDansCommunes(p,
                            code_departement,
                            connectionReferentiel);
                }

                if (p.stop) {
                    p.state = new String[]{
                        "TERMINE"
                    };
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                    return;
                }

                // Retire les marqueurs
                try {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "SUPPRIME LES MARQUEURS");
                    p.state = new String[]{
                        en_cours, maj, "SUPPRIME MARQUEURS"
                    };
                    GestionMarqueurs.supprimeMarqueursMaj(marqueurs, code_departement, flags, connectionMaj);
                } catch (ColonneException ce) {
                // Ne peut pas arriver si des modifications ne sont pas effectuées dans GestionTables.supprimeColonne.
                }
            }
        } finally {
            connectionReferentiel.setAutoCommit(autocommitOrigine);
            connectionMaj.setAutoCommit(autocommitDestination);
        }
    }

    /**
     * Découpe quatre numéros et répétitions d'adresse dans deux champs différents: numéro et répétition.<br>
     * 
     * Les numéros trouvés dans la colonne source de la forme '24 BIS' ou '24 B' ou '24BIS' ou '24B' sont découpés
     * dans les colonnes destination sous la forme 24 et B.<br>
     * Les suffixes suivant sont ajoutés aux préfixes:
     * <ul><li>_debut_droit</li>
     *     <li>_debut_gauche</li>
     *     <li>_fin_droit</li>
     *     <li>_fin_gauche</li></ul>
     * Si plusieurs numéros sont présents, seul le premier est conservé.
     * La colonne source doit être de type chaine.<br>
     * La colonne numéro destination doit être de type entier.<br>
     * La colonne répétition destination doit être de type charactère.<br>
     * @param p le processus affecté
     * @param table le nom de la table concernée
     * @param id l'identifiant unique dans la table
     * @param source le préfixe de la colonne source
     * @param numero le préfixe de la colonne numéro destination de type entier
     * @param repetition le préfixe de la colonne répétition destination de type character
     * @param connection la connection à la base de la table
     */
    public void decoupenumeros(Processus p, String table, String id, String source, String numero, String repetition, Connection connection)
            throws SQLException {
        int count = 0;

        try {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "DECOUPE NUMEROS");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "table     :" + table);
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "id        :" + id);
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "source    :" + source);
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "numeros   :" + numero);
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "repetition:" + repetition);

            p.resultat.add("DECOUPE NUMEROS");
            p.resultat.add("table     :" + table);
            p.resultat.add("id        :" + id);
            p.resultat.add("source    :" + source);
            p.resultat.add("numeros   :" + numero);
            p.resultat.add("repetition:" + repetition);

            p.state = new String[]{
                en_cours, preparation
            };

            // Prépare la requête permettant d'évaluer le nombre de lignes à traiter
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT count(*) from \"");
            sb.append(table);
            sb.append("\" WHERE (not \"");
            sb.append(source);
            sb.append("_debut_droit\" is null AND length(\"");
            sb.append(source);
            sb.append("_debut_droit\")<>0) OR (not \"");

            sb.append(source);
            sb.append("_debut_gauche\" is null AND length(\"");
            sb.append(source);
            sb.append("_debut_gauche\")<>0) OR (not \"");

            sb.append(source);
            sb.append("_fin_droit\" is null AND length(\"");
            sb.append(source);
            sb.append("_fin_droit\")<>0) OR (not \"");

            sb.append(source);
            sb.append("_fin_gauche\" is null AND length(\"");
            sb.append(source);
            sb.append("_fin_gauche\")<>0)");
            Statement sCompte = connection.createStatement();
            ResultSet rsCompte = sCompte.executeQuery(sb.toString());

            int compte = 0;
            if (rsCompte.next()) {
                compte = rsCompte.getInt(1);
            }
            rsCompte.close();
            sCompte.close();

            // Prépare la requête permettant de lister les lignes
            sb.setLength(0);
            sb.append("SELECT \"");
            sb.append(id);
            sb.append("\",\"");
            sb.append(source);
            sb.append("_debut_droit\",\"");
            sb.append(source);
            sb.append("_debut_gauche\",\"");
            sb.append(source);
            sb.append("_fin_droit\",\"");
            sb.append(source);
            sb.append("_fin_gauche\" FROM \"");
            sb.append(table);
            sb.append("\" WHERE (not \"");
            sb.append(source);
            sb.append("_debut_droit\" is null AND length(\"");
            sb.append(source);
            sb.append("_debut_droit\")<>0) OR (not \"");

            sb.append(source);
            sb.append("_debut_gauche\" is null AND length(\"");
            sb.append(source);
            sb.append("_debut_gauche\")<>0) OR (not \"");

            sb.append(source);
            sb.append("_fin_droit\" is null AND length(\"");
            sb.append(source);
            sb.append("_fin_droit\")<>0) OR (not \"");

            sb.append(source);
            sb.append("_fin_gauche\" is null AND length(\"");
            sb.append(source);
            sb.append("_fin_gauche\")<>0)");
            String rqCherche = sb.toString();

            // Prépare la requête permettant de mettre à jour les lignes
            sb.setLength(0);
            sb.append("UPDATE \"");
            sb.append(table);
            sb.append("\" SET \"");
            sb.append(numero);
            sb.append("_debut_droit\"=?, \"");
            sb.append(numero);
            sb.append("_debut_gauche\"=?, \"");
            sb.append(numero);
            sb.append("_fin_droit\"=?, \"");
            sb.append(numero);
            sb.append("_fin_gauche\"=?, \"");
            sb.append(repetition);
            sb.append("_debut_droit\"=?, \"");
            sb.append(repetition);
            sb.append("_debut_gauche\"=?, \"");
            sb.append(repetition);
            sb.append("_fin_droit\"=?, \"");
            sb.append(repetition);
            sb.append("_fin_gauche\"=? WHERE \"");
            sb.append(id);
            sb.append("\"=?");
            PreparedStatement psUpdate = connection.prepareStatement(sb.toString());

            p.state[1] = "CHERCHE LES LIGNES";
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES LIGNES");
            Statement sCherche = connection.createStatement();
            ResultSet rsCherche = sCherche.executeQuery(rqCherche);

            if (p.stop) {
                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
                return;
            }

            p.state = new String[]{
                en_cours, "TRAITEMENT", "LIGNES EFFECTUEES", "0", "SUR " + compte
            };
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT DE " + compte + " numeros");
            while (!p.stop && rsCherche.next()) {
                // debut_droit
                ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(rsCherche.getString(2));

                if (numeros.size() > 0) {
                    RefNumero premiernumero = numeros.get(0);

                    psUpdate.setInt(1, Integer.parseInt(premiernumero.obtientNumero()));

                    if (premiernumero.obtientRepetition() != null) {
                        psUpdate.setString(5, Character.toString(premiernumero.obtientRepetitionNormalise()));
                    } else {
                        psUpdate.setString(5, null);
                    }
                } else {
                    psUpdate.setInt(1, 0);
                    psUpdate.setString(5, null);
                }

                // debut_gauche
                numeros = gestionMots.trouveNumeros(rsCherche.getString(3));

                if (numeros.size() > 0) {
                    RefNumero premiernumero = numeros.get(0);

                    psUpdate.setInt(2, Integer.parseInt(premiernumero.obtientNumero()));

                    if (premiernumero.obtientRepetition() != null) {
                        psUpdate.setString(6, Character.toString(premiernumero.obtientRepetitionNormalise()));
                    } else {
                        psUpdate.setString(6, null);
                    }
                } else {
                    psUpdate.setInt(2, 0);
                    psUpdate.setString(6, null);
                }

                // fin_droit
                numeros = gestionMots.trouveNumeros(rsCherche.getString(4));

                if (numeros.size() > 0) {
                    RefNumero premiernumero = numeros.get(0);

                    psUpdate.setInt(3, Integer.parseInt(premiernumero.obtientNumero()));

                    if (premiernumero.obtientRepetition() != null) {
                        psUpdate.setString(7, Character.toString(premiernumero.obtientRepetitionNormalise()));
                    } else {
                        psUpdate.setString(7, null);
                    }
                } else {
                    psUpdate.setInt(3, 0);
                    psUpdate.setString(7, null);
                }

                // fin_gauche
                numeros = gestionMots.trouveNumeros(rsCherche.getString(5));

                if (numeros.size() > 0) {
                    RefNumero premiernumero = numeros.get(0);

                    psUpdate.setInt(4, Integer.parseInt(premiernumero.obtientNumero()));

                    if (premiernumero.obtientRepetition() != null) {
                        psUpdate.setString(8, Character.toString(premiernumero.obtientRepetitionNormalise()));
                    } else {
                        psUpdate.setString(8, null);
                    }
                } else {
                    psUpdate.setInt(4, 0);
                    psUpdate.setString(8, null);
                }

                psUpdate.setString(9, rsCherche.getString(1));

                psUpdate.execute();

                count++;

                if (count % 200 == 0) {
                    p.state[3] = Integer.toString(count);
                }
            }

            psUpdate.close();
            rsCherche.close();
            sCherche.close();

            if (p.stop) {
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
                p.resultat.add("INTERRUPTION UTILISATEUR");
            }

        } finally {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "DECOUPAGE NUMEROS TERMINE");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Nombre de lignes traitées: " + count);
            p.resultat.add("Nombre de lignes traitées:");
            p.resultat.add(Integer.toString(count));
        }
    }
    static final int FLAG_DECOUPE_NUMERO = 1;
    static final int FLAG_DECOUPE_REPETITION = 2;
    static final int FLAG_DECOUPE_AUTRES_NUMEROS = 4;
    static final int FLAG_DECOUPE_TYPE_DE_VOIE = 8;
    static final int FLAG_DECOUPE_ARTICLE = 16;
    static final int FLAG_DECOUPE_LIBELLE = 32;
    static final int FLAG_DECOUPE_MOT_DETERMINANT = 64;
    static final int FLAG_DECOUPE_CODE_POSTAL = 128;
    static final int FLAG_DECOUPE_COMMUNE = 256;
    static final int FLAG_DECOUPE_NUMERO_ARRDT = 512;
    static final int FLAG_DECOUPE_CEDEX = 1024;
    static final int FLAG_DECOUPE_CODE_CEDEX = 2048;
    static final int FLAG_DECOUPE_LIGNE1 = 4096;
    static final int FLAG_DECOUPE_LIGNE2 = 8192;
    static final int FLAG_DECOUPE_LIGNE3 = 16384;
    static final int FLAG_DECOUPE_LIGNE5 = 32768;

    /**
     * Découpe les noms de voies d'une table de voie en typedevoie, article et libelle.<br>
     * 
     * Les éléments du paramètre natures peuvent être une combinaison des flags suivants:
     * <ul>
     * <li>1 pour numero</li>
     * <li>2 pour repetition</li>
     * <li>4 pour autres numeros</li>
     * <li>8 pour type de voie</li>
     * <li>16 pour article</li>
     * <li>32 pour libelle</li>
     * <li>64 pour mot déterminant</li>
     * <li>128 pour code postal</li>
     * <li>256 pour commune</li>
     * <li>512 pour numero d'arrondissement</li>
     * <li>1024 pour cedex</li>
     * <li>2048 pour le code cedex</li>
     * <li>4096 pour ligne1</li>
     * <li>8192 pour ligne2</li>
     * <li>16384 pour ligne3</li>
     * <li>32768 pour ligne5</li>
     * </ul>
     * Le nombre de champs du paramètre champs ne doit pas excèder 6 (6 lignes d'adresse).
     * @param nomTable le nom de la table dans laquelle travailler
     * @param id un vrai identifiant unique pour la table
     * @param champs permet d'indiquer les colonnes de la table qui contiennent représentent l'adresse.<br>
     * @param decoupage permet d'indiquer les colonnes qui contiendront les morceaux de l'adresse.<br>
     * @param nature permet d'indiquer la nature des colonnes du découpage.<br>
     * @param lignes indique les lignes auxquelles appartiennent les champs d'adresse. S'il est null, les adresses
     * seront restructurées.
     * @throws GestionReferentielException Les champs découpage et nature ne correspondent pas ou trop de champs ont été spécifiés.
     */
    public void decoupe(Processus p, String nomTable, String id, String[] champs, String[] decoupage, int[] natures, int[] lignes,
            Connection connection, Connection referentiel) throws SQLException, GestionReferentielException {
        p.state = new String[]{
            en_cours, preparation
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, preparation);

        if (decoupage.length == 0 || champs.length == 0) {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version,
                    "Aucun découpage effectué (pas de champs à découper ou de découpage à produire.");
            p.resultat.add("Aucun découpage effectué (pas de champs à découper ou de découpage à produire.");
            return;
        }
        if (decoupage.length != natures.length) {
            throw (new GestionReferentielException("Les champs découpés et les natures ne correspondent pas",
                    GestionReferentielException.PARAMETREERRONNE, 5));
        }
        if (champs.length > 6) {
            throw (new GestionReferentielException("Trop de champs ont été spécifiés", GestionReferentielException.PARAMETREERRONNE, 5));
        }
        p.resultat.add("DECOUPE");
        p.resultat.add("table : " + nomTable);
        p.resultat.add("id    : " + id);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < champs.length; i++) {
            sb.append(champs[i]);
            sb.append(" ");
        }
        p.resultat.add("champs :" + sb.toString());
        sb.setLength(0);
        for (int i = 0; i < decoupage.length; i++) {
            sb.append(decoupage[i]);
            sb.append(" ");
        }
        p.resultat.add("decoupage : " + sb.toString());
        sb.setLength(0);
        for (int i = 0; i < natures.length; i++) {
            sb.append(natures[i]);
            sb.append(" ");
        }
        p.resultat.add("natures : " + sb.toString());
        sb.setLength(0);
        for (int i = 0; i < lignes.length; i++) {
            sb.append(lignes[i]);
            sb.append(" ");
        }
        p.resultat.add("lignes : " + sb.toString());

        boolean idx_ajoute = false;
        Index idx = new Index();
        idx.setNom(nomTable + "_" + id);
        idx.ajouteColonne(id);
        try {
            idx_ajoute = GestionTables.ajouteIndex(nomTable, idx, connection);
        } catch (GestionReferentielException ex) {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version,
                    "L'index " + nomTable + "_" + id + " doit être supprimé si ses colonnes ne sont pas correctes.");
            Logger.getLogger(gestion_referentiel).log(Level.SEVERE,
                    "L'index " + nomTable + "_" + id + " doit être supprimé si ses colonnes ne sont pas correctes.", ex);
            idx_ajoute = false;
        }

        // Prépare la requête permettant de lister les adresses.
        sb.setLength(0);
        sb.append("SELECT \"");
        sb.append(id);
        sb.append("\"");
        for (int i = 0; i < champs.length; i++) {
            sb.append(",\"");
            sb.append(champs[i]);
            sb.append('\"');
        }
        sb.append(" FROM \"");
        sb.append(nomTable);
        sb.append("\"");
        Statement stSelect = connection.createStatement();
        String rqCherche = sb.toString();

        // Prépare la requête permettant de mettre à jour la table.
        sb.setLength(0);
        sb.append("UPDATE \"");
        sb.append(nomTable);
        sb.append("\" SET ");
        for (int i = 0; i < decoupage.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('\"');
            sb.append(decoupage[i]);
            sb.append("\"=?");
        }
        sb.append(" WHERE \"");
        sb.append(id);
        sb.append("\"=?");
        PreparedStatement psUpdate = connection.prepareStatement(sb.toString());

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
            return;
        }

        p.state[1] = "CHERCHE LES VOIES";
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES VOIES");
        ResultSet rsSelect = stSelect.executeQuery(rqCherche);
        int count = 0;
        String[] adresse;
        if (lignes == null) {
            adresse = new String[champs.length];
        } else {
            adresse = new String[7];
            for (int i = 0; i < 7; i++) {
                adresse[i] = "";
            }
        }
        ArrayList<RefCle> numerosSupplementaires = new ArrayList<RefCle>();

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        p.state[1] = "DEBUTE LE TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "DEBUTE LE TRAITEMENT");
        if (rsSelect.next()) {
            p.state = new String[]{
                en_cours, "TRAITEMENT", "LIGNES TRAITEES", "0"
            };
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT");

            do {
                numerosSupplementaires.clear();

                String id_val = rsSelect.getString(1);

                if (lignes == null) {
                    for (int i = 0; i < champs.length; i++) {
                        adresse[i] = rsSelect.getString(2 + i);
                    }
                    for (int i = champs.length; i < adresse.length; i++) {
                        adresse[i] = empty;
                    }

                    adresse = this.gestionMots.normalise_1(adresse);
                    adresse = this.gestionMots.restructure(adresse, false, false, referentiel);
                } else {
                    for (int i = 0; i < champs.length; i++) {
                        adresse[lignes[i] - 1] = "";
                    }
                    for (int i = 0; i < champs.length; i++) {
                        int ligne = lignes[i] - 1;
                        if (adresse[ligne].length() == 0) {
                            adresse[ligne] = rsSelect.getString(2 + i);
                        } else {
                            adresse[ligne] += " " + rsSelect.getString(2 + i);
                        }
                    }
                    adresse = this.gestionMots.normalise_1(adresse);
                }

                // La voie est découpée,
                ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(adresse[3]);
                RefCle typedevoie = gestionMots.trouveTypeVoie(adresse[3], numeros);
                RefCle article, libelle;
                if (typedevoie.obtientMot().length() != 0) {
                    article = gestionMots.trouveArticleVoie(adresse[3], typedevoie);
                    libelle = gestionMots.trouveLibelleVoie(adresse[3], article);
                } else {
                    article = new RefCle("", 0);
                    libelle = gestionMots.trouveLibelleVoie(adresse[3], numeros);
                }

                RefCle codepostal = gestionMots.trouveCodePostal(adresse[5]);
                RefCle commune = gestionMots.trouveNomVille(adresse[5], codepostal);
                RefCle numeroArrdt = gestionMots.trouveNumeroArrondissement(adresse[5], commune);
                RefCle cedex = gestionMots.trouveCedex(adresse[5], numeroArrdt);
                RefCle numeroCedex = gestionMots.trouveNumeroCedex(adresse[5], cedex);

                // et les champs découpés affectés.
                for (int i = 0; i < decoupage.length; i++) {
                    int nature = natures[i];
                    sb.setLength(0);

                    if ((nature & FLAG_DECOUPE_NUMERO) != 0) // numéro
                    {
                        if (numeros.size() > 0) {
                            Algos.appendWithSpace(sb, numeros.get(0).obtientNumeroNormalise(), true);
                        }
                    }
                    if ((nature & FLAG_DECOUPE_REPETITION) != 0) // repetition
                    {
                        if (numeros.size() > 0) {
                            char rep = numeros.get(0).obtientRepetitionNormalise();
                            if (rep != 0) {
                                Algos.appendWithSpace(sb, rep, true);
                            }
                        }
                    }
                    if ((nature & FLAG_DECOUPE_AUTRES_NUMEROS) != 0) // autres numéros
                    {
                        for (int j = 1; j < numeros.size(); j++) {
                            if (j != 1) {
                                sb.append(';');
                            }
                            Algos.appendWithSpace(sb, numeros.get(j).obtientNumeroNormalise(), true);

                            char rep = numeros.get(j).obtientRepetitionNormalise();
                            if (rep != 0) {
                                Algos.appendWithSpace(sb, rep, true);
                            }
                        }
                    }
                    if ((nature & FLAG_DECOUPE_TYPE_DE_VOIE) != 0) // type de voie
                    {
                        Algos.appendWithSpace(sb, typedevoie.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_ARTICLE) != 0) // article
                    {
                        Algos.appendWithSpace(sb, article.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_LIBELLE) != 0) // libelle
                    {
                        Algos.appendWithSpace(sb, libelle.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_MOT_DETERMINANT) != 0) // mot déterminant
                    {
                        Algos.appendWithSpace(sb, Algos.derniermot(libelle.obtientMot()), true);
                    }
                    if ((nature & FLAG_DECOUPE_CODE_POSTAL) != 0) // code postal
                    {
                        Algos.appendWithSpace(sb, codepostal.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_COMMUNE) != 0) // commune
                    {
                        Algos.appendWithSpace(sb, commune.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_NUMERO_ARRDT) != 0) // code arrondissement
                    {
                        Algos.appendWithSpace(sb, numeroArrdt.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_CEDEX) != 0) // cedex
                    {
                        Algos.appendWithSpace(sb, cedex.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_CODE_CEDEX) != 0) // code cedex
                    {
                        Algos.appendWithSpace(sb, numeroCedex.obtientMot(), true);
                    }
                    if ((nature & FLAG_DECOUPE_LIGNE1) != 0) // ligne 1
                    {
                        Algos.appendWithSpace(sb, adresse[0], true);
                    }
                    if ((nature & FLAG_DECOUPE_LIGNE2) != 0) // ligne 2
                    {
                        Algos.appendWithSpace(sb, adresse[1], true);
                    }
                    if ((nature & FLAG_DECOUPE_LIGNE3) != 0) // ligne 3
                    {
                        Algos.appendWithSpace(sb, adresse[2], true);
                    }
                    if ((nature & FLAG_DECOUPE_LIGNE5) != 0) // ligne 5
                    {
                        Algos.appendWithSpace(sb, adresse[4], true);
                    }

                    psUpdate.setString(i + 1, sb.toString());
                }
                psUpdate.setString(decoupage.length + 1, id_val);

                psUpdate.execute();

                count++;

                if (count % 200 == 0) {
                    p.state[3] = Integer.toString(count);
                }
            } while (!p.stop && rsSelect.next());
        }

        connection.commit();
        connection.setAutoCommit(autocommit);

        psUpdate.close();
        rsSelect.close();
        stSelect.close();

        if (p.stop) {
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
        }

        if (idx_ajoute) {
            GestionTables.supprimeIndex(idx, connection);
        }

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "DECOUPAGE D'ADRESSES");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "LIGNES TRAITEES:" + count);

        p.resultat.add("DECOUPAGE D'ADRESSES");
        p.resultat.add("LIGNES TRAITEES:" + count);
    }

    /**
     * Calcule la table idvoies à partir de la table de troncons.<br>
     * INDEX A CREER pour optimiser cette méthode:
     * <ul><li>sur la table idvoies : voi_id</li></ul>
     */
    public void creeIdVoies(String tableTroncons, String tableIdVoies, Connection connection) throws SQLException,
            GestionReferentielException {
        if (!GestionTables.tableExiste(tableIdVoies, connection)) {
            DescriptionTable dtIdVoies = GestionDescriptionTables.creeDescriptionTableIdVoies();
            GestionTables.creeTable(tableIdVoies, dtIdVoies, connection);
        }

        // Prépare la requête permettant de vérifier l'unicité des ids.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_id from \"");
        sb.append(tableIdVoies);
        sb.append("\" WHERE voi_id=? LIMIT 1");
        PreparedStatement stUnique = connection.prepareStatement(sb.toString());

        // Prépare la requête permettant d'insérer un id
        sb = new StringBuilder();
        sb.append("INSERT INTO \"");
        sb.append(tableIdVoies);
        sb.append("\" (voi_id) VALUES (?)");
        PreparedStatement stInsertId = connection.prepareStatement(sb.toString());

        // Prépare la requête énumérant tous les tronçons.
        sb = new StringBuilder();
        sb.append("SELECT voi_id_droit,voi_id_gauche from \"");
        sb.append(tableTroncons);
        sb.append("\"");
        Statement stCherche = connection.createStatement();
        ResultSet rsCherche = stCherche.executeQuery(sb.toString());

        while (rsCherche.next()) {
            String voi_id_droit = rsCherche.getString(1);

            if (voi_id_droit != null && voi_id_droit.length() > 0) {
                // Vérifie la prèsence de l'id droit
                stUnique.setString(1, voi_id_droit);
                ResultSet rsUnique = stUnique.executeQuery();

                // Si la voie n'a pas encore été ajoutée,
                if (!rsUnique.next()) {
                    // Ajoute la voie.
                    stInsertId.setString(1, voi_id_droit);
                    stInsertId.execute();
                }
                rsUnique.close();
            }

            String voi_id_gauche = rsCherche.getString(2);

            // Vérifie la prèsence de l'id gauche
            if (voi_id_gauche != null && voi_id_gauche.length() > 0) {
                // Vérifie la prèsence de l'id droit
                stUnique.setString(1, voi_id_gauche);
                ResultSet rsUnique = stUnique.executeQuery();

                // Si la voie n'a pas encore été ajoutée,
                if (!rsUnique.next()) {
                    // Ajoute la voie.
                    stInsertId.setString(1, voi_id_gauche);
                    stInsertId.execute();
                }
                rsUnique.close();
            }
        }

        rsCherche.close();
        stCherche.close();
        stInsertId.close();
        stUnique.close();
    }

    /**
     * Prépare à la mise à jour du référentiel.<br>
     * Les troncons du référentiel à jour sont comparés aux tronçons du référentiel en cour de manière à leur
     * affecter les identifiants de auTroncon corrects. Les identifiants de voies sont aussi affectés pour ces tronçons.<br>
     * Pour la comparaison, seuls la géométrie, le nom, et le code insee sont utilisés, selon ces règles:<br>
     * <ul>
     * <li>Si la géométrie est identique, l'id de auTroncon est affecté.<li>
     * <li>Si les codes insee et nom sont en plus identiques, les id de voies sont affectés.</li>
     * </ul>
     * La colonne géométrie et les colonnes nom et insee sont utilisées comme clé primaire pour la mise à jour des identifiants.<br>
     * Il est ainsi nécessaire de vérifier que chaque tronçon a un identifiant unique (donc que chaque
     * géométrie est unique) au préalable, ou après l'exécution de la méthode.
     * Postgis (index gist) est mis à contribution dans cet algorithme.
     * @param p le processus attribué à cette tâche
     * @param code_departement le département à traiter
     * @param connectionOrigine la connection au référentiel en cour
     * @param connectionDestination la connection au référentiel à jour
     */
    public void prepareMajReferentiel(Processus p, String code_departement, Connection connectionOrigine, Connection connectionDestination)
            throws SQLException, GestionReferentielException {
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE MISE A JOUR REFERENTIEL");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "departement : " + code_departement);
        p.resultat.add("PREPARE MISE A JOUR REFERENTIEL");
        p.resultat.add("departement : " + code_departement);

        int troncon_apparies = 0;
        int troncon_voie_apparies = 0;
        int count = 0;
        String nomTableTroncon = GestionHistoriqueTables.obtientDerniereTableTroncon(connectionOrigine, code_departement);

        if (nomTableTroncon == null) {
            throw (new GestionReferentielException("Aucune table de troncon n'a été trouvée.", GestionReferentielException.TABLENEXISTEPAS,
                    11));
        }

        p.state = new String[]{
            en_cours, "PREPARE INDEX"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE INDEX");
        // Crée les index nécessaires.
        Index idx_geometrie = new Index();
        idx_geometrie.setNom("tr" + code_departement + "_id_geometrie");
        idx_geometrie.ajouteColonne("geometrie");
        idx_geometrie.setType("gist"); // il s'agit de l'opérateur de postgis
        Index idx_noms_insees = new Index();
        idx_noms_insees.setNom("tr" + code_departement + "_noms_insees");
        idx_noms_insees.ajouteColonne("tro_nom_droit");
        idx_noms_insees.ajouteColonne("tro_nom_gauche");
        idx_noms_insees.ajouteColonne("com_code_insee_droit");
        idx_noms_insees.ajouteColonne("com_code_insee_gauche");

        boolean idx_geometrie_cree = GestionTables.ajouteIndex(nomTableTroncon, idx_geometrie, connectionOrigine);
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        boolean idx_noms_insee_cree = GestionTables.ajouteIndex("tro_troncons_"+code_departement,idx_noms_insees,connectionDestination);
        boolean idx_noms_insee_cree = GestionTables.ajouteIndex(GestionTables.getTroTronconsTableName(code_departement), idx_noms_insees,
                connectionDestination);

        p.state[1] = "PREPARE REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE REQUETES");

        // Prépare la requête permettant d'énumérer les tronçons à modifier.
        StringBuilder sb = new StringBuilder();
        sb.append("Select tro_id,tro_nom_droit,tro_nom_gauche,com_code_insee_droit,com_code_insee_gauche,geometrie");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sbCom.append(" from \"tro_troncons_");
//        sbCom.append(code_departement);
//        sbCom.append("\"");
        sb.append(" from \"");
        sb.append(GestionTables.getTroTronconsTableName(code_departement)).append("\"");

        String requeteCherche = sb.toString();
        Statement sCherche = connectionDestination.createStatement();

        // Prépare la requête permettant de chercher les tronçons de même nom et de même code insee dans le référentiel en cours
        sb.setLength(0);
        sb.append(
                "Select tro_id,astext(geometrie),voi_id_droit,voi_id_gauche,tro_nom_droit,tro_nom_gauche,com_code_insee_droit,com_code_insee_gauche from \"");
        sb.append(nomTableTroncon);
        sb.append("\" where geometrie~=? and t0<=? and t1>=?");
        String rqChercheCorrespondance = sb.toString();
        PreparedStatement psChercheCorrespondance = connectionOrigine.prepareStatement(rqChercheCorrespondance);
        Timestamp tsdate = new Timestamp(Calendar.getInstance().getTimeInMillis());
        psChercheCorrespondance.setTimestamp(2, tsdate);
        psChercheCorrespondance.setTimestamp(3, tsdate);

        // Prépare la requête permettant de mettre à jour les tronçons à modifier
        sb.setLength(0);
        sb.append("UPDATE \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sbCom.append("tro_troncons_").append(code_departement);
        sb.append(GestionTables.getTroTronconsTableName(code_departement));
        sb.append("\" set tro_id=?,voi_id_droit=?,voi_id_gauche=? where geometrie~=? and "); // l'opérateur ~= défini par postgis est utilisé pour la jointure.
        // il retourne vrai lorsque les géométries sont égales.
        sb.append("tro_nom_droit=? and tro_nom_gauche=? and com_code_insee_droit=? and com_code_insee_gauche=?");
        String rqUpdate = sb.toString();
        PreparedStatement psUpdate = connectionDestination.prepareStatement(rqUpdate);

        WKTReader reader = new WKTReader();

        p.state[1] = "CHERCHE LES TRONCONS";
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES TRONCONS");
        ResultSet rsCherche = sCherche.executeQuery(requeteCherche);

        if (p.stop) {
            rsCherche.close();
            sCherche.close();
            psChercheCorrespondance.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        p.state = new String[]{
            en_cours, "TRAITEMENT", "TRONCONS TRAITES", "0"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT");
        // Pour chaque auTroncon
        while (!p.stop && rsCherche.next()) {
            String id_troncon = null;

            String nom_droit = rsCherche.getString(2);
            String nom_gauche = rsCherche.getString(3);
            String code_insee_droit = rsCherche.getString(4);
            String code_insee_gauche = rsCherche.getString(5);
            Object geometrie = rsCherche.getObject(6);

            psChercheCorrespondance.setObject(1, geometrie);
            ResultSet rsChercheCorrespondance = psChercheCorrespondance.executeQuery();
            // Pour chaque auTroncon de même géométrie
            if (rsChercheCorrespondance.next()) {
                String voi_id_droit = "", voi_id_gauche = "";

                String nom_ref_droit = rsChercheCorrespondance.getString(5);
                String nom_ref_gauche = rsChercheCorrespondance.getString(6);
                String code_insee_ref_droit = rsChercheCorrespondance.getString(7);
                String code_insee_ref_gauche = rsChercheCorrespondance.getString(8);

                boolean avecvoie = false;

                // Vérifie si il est possible de propager les id de voies.
                if (nom_ref_droit.compareTo(nom_droit) == 0 && code_insee_ref_droit.compareTo(code_insee_droit) == 0) {
                    voi_id_droit = rsChercheCorrespondance.getString(3);
                    avecvoie = true;
                }
                if (nom_ref_gauche.compareTo(nom_gauche) == 0 && code_insee_ref_gauche.compareTo(code_insee_gauche) == 0) {
                    voi_id_gauche = rsChercheCorrespondance.getString(3);
                    avecvoie = true;
                }
                if (avecvoie) {
                    troncon_voie_apparies++;
                }

                troncon_apparies++;
                id_troncon = rsChercheCorrespondance.getString(1);

                // Si les tronçons correspondent
                // l'id de auTroncon et éventuellement les id de voie sont mis à jour
                psUpdate.setString(1, id_troncon);
                psUpdate.setString(2, voi_id_droit);
                psUpdate.setString(3, voi_id_gauche);
                psUpdate.setObject(4, geometrie);
                psUpdate.setString(5, nom_droit);
                psUpdate.setString(6, nom_gauche);
                psUpdate.setString(7, code_insee_droit);
                psUpdate.setString(8, code_insee_droit);
                psUpdate.execute();

                // S'il y a plus d'un tronçon pour cette géométrie, affiche un avertissement
                if (psUpdate.getUpdateCount() > 1) {
                    p.resultat.add("Pb ds la maj : plusieurs troncons vont avoir l'id " + id_troncon + " (même géométrie)");
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version,
                            "Pb ds la maj : plusieurs troncons vont avoir l'id " + id_troncon + " (même géométrie)");
                }

                while (rsChercheCorrespondance.next()) {
                    p.resultat.add(
                            "Pb ds le référentiel : le troncon " + rsChercheCorrespondance.getString(1) + " est similaire au tronçon " + id_troncon);
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Pb ds le référentiel : le troncon " + rsChercheCorrespondance.getString(1) + " est similaire au tronçon " + id_troncon);
                }
            }

            count++;
            if (count % 100 == 0) {
                p.state[3] = Integer.toString(count);
            }

            rsChercheCorrespondance.close();
        }

        rsCherche.close();
        sCherche.close();
        psChercheCorrespondance.close();

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        // Supprime les index si nécessaire.
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "SUPPRIME INDEX");
        p.state = new String[]{
            en_cours, "SUPPRIME INDEX"
        };
        if (idx_geometrie_cree) {
            GestionTables.supprimeIndex(idx_geometrie, connectionOrigine);
        }
        if (idx_noms_insee_cree) {
            GestionTables.supprimeIndex(idx_noms_insees, connectionDestination);
        }

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARATION DE LA MISE A JOUR");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Troncons apparies: " + troncon_apparies);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Dont troncons qui ont un id de voie: " + troncon_voie_apparies);
        p.resultat.add("PREPARATION DE LA MISE A JOUR");
        p.resultat.add("Troncons apparies: " + troncon_apparies);
        p.resultat.add("Dont troncons qui ont un id de voie: " + troncon_voie_apparies);
    }

    /**
     * Prépare au changement de référentiel en précalculant la correspondance entre les voies.<br>
     * Les tables lienvoie, voies, et idvoies sont alors crées.<br>
     * Les marqueurs de la table voies sont purgés. <br>
     * Le flag de la table lienvoie est utilisé dans deux cas:
     * <ul><li>une correspondance entre les voies est trouvée, mais la voie destination correspond à plusieurs voies source.</li>
     *     <li>plusieurs voies destinations correspondent à une voie source. Dans ce cas, aucune voie destination n'est indiquée (null).</li></ul>
     * Si elles étaient existantes, elles sont vidées au préalable.<br>
     */
    public void prepareChangementReferentiel(Processus p, String code_departement, Connection connectionOrigine,
            Connection connectionDestination) throws GestionReferentielException, SQLException {
        int voiecorrespondantes = 0;
        int voiesnoncorrespondantes = 0;
        int voiestraitees = 0;

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, lancement);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE CHANGEMENT REFERENTIEL");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "departement : " + code_departement);
        p.state = new String[]{
            en_cours, lancement
        };
        p.resultat.add("PREPARE CHANGEMENT REFERENTIEL");
        p.resultat.add("departement : " + code_departement);

        // Calcule la table de voies et initialise la table idvoies.
        p.state = new String[]{
            en_cours, "TRAITEMENT TABLES", "CREE TABLE VOIES", "", "TRONCONS TRAITES", "0", "SUR 0"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREE TABLE VOIES");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        gestionMiseAJour.creeTableVoieMaj(p,code_departement,"tro_troncons_"+code_departement,"voi_voies_"+code_departement,"idvoies",connectionDestination);
        gestionMiseAJour.creeTableVoieMaj(p, code_departement, GestionTables.getTroTronconsTableName(code_departement),
                "voi_voies_" + code_departement, "idvoies", connectionDestination);

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "MANAGE LES TABLES");
        p.state = new String[]{
            en_cours, "MANAGE LES TABLES"
        };
        // crée la table lienvoies si nécessaire.
        GestionTables.creeTable("lvi_lien_voies_" + code_departement, GestionDescriptionTables.creeDescriptionTableLienVoies(),
                connectionDestination);

        // Vide la table lienvoies
        GestionTables.vide("lvi_lien_voies_" + code_departement, connectionDestination);

        // Purge les marqueurs de la table voies.
        // WA 09/2011 utilisation de GestionTables.getXXTableName
        final String voiTableName = GestionTables.getVoiVoiesTableName(code_departement);
//        GestionMarqueurs.purgeMarqueursDeTable("voi_voies_"+code_departement,connectionDestination);
        GestionMarqueurs.purgeMarqueursDeTable(voiTableName, connectionDestination);

        // crée un marqueur sur la table de voies ainsi crée.
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        String marqueurVoie = GestionMarqueurs.ajouteMarqueur("voi_voies_"+code_departement,connectionDestination);
        String marqueurVoie = GestionMarqueurs.ajouteMarqueur(voiTableName, connectionDestination);

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE LES REQUETES");
        p.state = new String[]{
            en_cours, "PREPARE LES REQUETES"
        };

        // Prépare la requête permettant de flaguer les correspondances non flaguées.
        String rqMajFlag = "UPDATE \"lvi_lien_voies_" + code_departement + "\" SET flag=1 WHERE voi_id_destination=? AND flag=0";
        PreparedStatement psMajFlag = connectionDestination.prepareStatement(rqMajFlag);

        // Prépare la requête permettant de savoir si la voie a déjà été utilisée
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_id FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sbCom.append("voi_voies_").append(code_departement);
        sb.append(voiTableName);
        sb.append("\" WHERE voi_id=? AND \"");
        sb.append(marqueurVoie);
        sb.append("\"=1 LIMIT 1");
        String rqChercheMarque = sb.toString();
        PreparedStatement psChercheMarque = connectionDestination.prepareStatement(rqChercheMarque);

        // Prépare la requête permettant de marquer les voies
        sb.setLength(0);
        sb.append("UPDATE \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sbCom.append("voi_voies_").append(code_departement);
        sb.append(voiTableName);
        sb.append("\" SET \"");
        sb.append(marqueurVoie);
        sb.append("\"=1 WHERE voi_id=?");
        String rqMarque = sb.toString();
        PreparedStatement psMarque = connectionDestination.prepareStatement(rqMarque);

        // Prépare la requête permettant d'indiquer une correspondance
        String rqInsert = "INSERT INTO \"lvo_lien_voies_" + code_departement + "\" (voi_id_source,voi_id_destination,lvo_flag) VALUES (?,?,?)";
        PreparedStatement psInsert = connectionDestination.prepareStatement(rqInsert);

        // Prépare la requête permettant d'énumérer toutes les voies à préparer.
        sb.setLength(0);
        sb.append("SELECT voi_id,voi_nom,com_code_insee from \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sbCom.append("voi_voies_").append(code_departement);
        sb.append(voiTableName);
        sb.append("\"");
        String rqCherche = sb.toString();
        Statement stCherche = connectionOrigine.createStatement();

        if (p.stop) {
            psMajFlag.close();
            psMarque.close();
            psChercheMarque.close();
            stCherche.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES VOIES");
        p.state = new String[]{
            en_cours, "CHERCHE LES VOIES"
        };
        ResultSet rsCherche = stCherche.executeQuery(rqCherche);

        String tronconOrigine = GestionHistoriqueTables.obtientDerniereTableTroncon(connectionOrigine, code_departement);

        boolean autocommit = connectionDestination.getAutoCommit();
        connectionDestination.setAutoCommit(false);

        if (p.stop) {
            psMajFlag.close();
            psMarque.close();
            psChercheMarque.close();
            rsCherche.close();
            stCherche.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        try {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT CORRESPONDANCES");
            p.state = new String[]{
                en_cours, "TRAITEMENT CORRESPONDANCES", "VOIES TRAITEES", "0"
            };
            ////////////////////////////////////////////////////////////////////////////////////
            // Cherche parmi les voies existantes les voies correspondantes dans la destination.
            ////////////////////////////////////////////////////////////////////////////////////
            while (!p.stop && rsCherche.next()) {
                String voi_id = rsCherche.getString(1);
                String nom = rsCherche.getString(2);
                String code_insee = rsCherche.getString(3);

                // Obtient la géométrie de la voie,
                ArrayList<Geometry> geometrie = obtientGeometries(voi_id, tronconOrigine, connectionOrigine);

                // Cherche les voies qui correspondent,
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                ArrayList<String> voies = chercheCorrespondanceVoie(nom,code_insee,geometrie,"voi_voies_"+code_departement,"tro_troncons_"+code_departement,connectionDestination);
                ArrayList<String> voies = chercheCorrespondanceVoie(nom, code_insee, geometrie, GestionTables.getVoiVoiesTableName(
                        code_departement), GestionTables.getTroTronconsTableName(code_departement), connectionDestination);

                if (voies.size() == 1) {
                    voiecorrespondantes++;

                    // Une unique voie correspond, la correspondance est ajoutée.
                    psInsert.setString(2, voies.get(0));

                    // Cherche si cette voie trouvée n'a pas déjà été utilisée.
                    psChercheMarque.setString(1, voies.get(0));
                    ResultSet rsChercheMarque = psChercheMarque.executeQuery();
                    if (rsChercheMarque.next()) {
                        // Marque alors chacune des utilisations de cette voie dans la table lienvoie
                        // comme étant non unique.
                        psMajFlag.setString(1, voies.get(0));
                        psMajFlag.execute();

                        // La voie correspond a une unique voie, la correspondance est flaguée.
                        psInsert.setInt(3, 1);
                    } else {
                        psInsert.setInt(3, 0); // voie non utilisée, pas de flag.
                    }
                    // Marque la voie comme étant déjà utilisée
                    psMarque.setString(1, voies.get(0));
                    psMarque.execute();
                } else {
                    voiesnoncorrespondantes++;

                    // Plusieurs voies ou aucune voie ne correspond, aucune correspondance n'est ajoutée.
                    psInsert.setString(2, null);

                    if (voies.size() > 1) {
                        psInsert.setInt(3, 1); // Plusieurs correspondances ont été trouvées, la correspondance est flaguée.

                        // Marque toutes ces voies (car si elles ont été trouvées, c'est qu'elle n'étaient pas flaguées).
                        for (int i = 0; i < voies.size(); i++) {
                            psMarque.setString(1, voies.get(i)); //
                            psMarque.execute();
                        }
                    } else {
                        psInsert.setInt(3, 0);
                    }
                }
                psInsert.setString(1, voi_id);
                psInsert.execute();

                // Application des modifications.
                connectionDestination.commit();

                voiestraitees++;

                if (voiestraitees % 100 == 0) {
                    p.state[3] = Integer.toString(voiestraitees);
                }
            }

            psMajFlag.close();
            psMarque.close();
            psChercheMarque.close();
            rsCherche.close();
            stCherche.close();

            if (p.stop) {

                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                return;
            }

            // Prépare la requête permettant d'énumérer les voies qui n'ont pas été marquées
            sb.setLength(0);
            sb.append("SELECT voi_id,voi_nom,com_code_insee from \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sbCom.append("voi_voies_").append(code_departement);
            sb.append(GestionTables.getVoiVoiesTableName(code_departement));
            sb.append("\" WHERE \"");
            sb.append(marqueurVoie);
            sb.append("\"=0 OR \"");
            sb.append(marqueurVoie);
            sb.append("\" is NULL");
            String rqChercheNonMarque = sb.toString();
            Statement stChercheNonMarque = connectionDestination.createStatement();

            /////////////////////////////////////////////////////////////////////////
            // Ajoute les voies qui n'ont pas été marquées, donc sans correspondances
            /////////////////////////////////////////////////////////////////////////
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES NON CORRESPONDANCES");
            p.state = new String[]{
                en_cours, "CHERCHE LES NON CORRESPONDANCES"
            };
            ResultSet rsChercheNonMarque = stChercheNonMarque.executeQuery(rqChercheNonMarque);

            if (p.stop) {
                psInsert.close();
                rsChercheNonMarque.close();
                stChercheNonMarque.close();
                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                return;
            }

            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT DES NON CORRESPONDANCES");
            p.state = new String[]{
                en_cours, "TRAITEMENT DES NON CORRESPONDANCES", "VOIES TRAITEES", "0"
            };
            int voiestraitees2 = 0;
            while (!p.stop && rsChercheNonMarque.next()) {
                voiesnoncorrespondantes++;

                // Ajoute la voie
                psInsert.setString(1, null);
                psInsert.setString(2, rsChercheNonMarque.getString(1));
                psInsert.setInt(3, 0);
                psInsert.execute();

                connectionDestination.commit();

                voiestraitees2++;

                if (voiestraitees2 % 100 == 0) {
                    p.state[3] = Integer.toString(voiestraitees);
                }
            }

            psInsert.close();
            rsChercheNonMarque.close();
            stChercheNonMarque.close();

            if (p.stop) {
                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                return;
            }

            try {
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "SUPPRESSION DU MARQUEUR");
                p.state = new String[]{
                    en_cours, "SUPPRESSION DU MARQUEUR"
                };
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                GestionMarqueurs.supprimeMarqueur("voi_voies_"+code_departement,marqueurVoie,connectionDestination);
                GestionMarqueurs.supprimeMarqueur(GestionTables.getVoiVoiesTableName(code_departement), marqueurVoie, connectionDestination);
            } catch (ColonneException ce) {
            // Ne devrait pas arriver.
            }
        } finally {
            connectionDestination.setAutoCommit(autocommit);
        }
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARATION DU CHANGEMENT DE REFERENTIEL TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Voies correspondantes: " + voiecorrespondantes);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Voies non correspondantes: " + voiesnoncorrespondantes);
        p.resultat.add("PREPARATION DU CHANGEMENT DE REFERENTIEL TERMINE");
        p.resultat.add("Voies correspondantes: " + voiecorrespondantes);
        p.resultat.add("Voies non correspondantes: " + voiesnoncorrespondantes);
    }

    /**
     * Effectue le changement de référentiel. <br>
     * Le référentiel destination aura été préalablement préparé avec la méthode prepareChangementReferentiel et 
     * éventuellement traité à la main.
     * @param processus le numéro du processus attribué
     * @param code_departement le departement concerné
     * @param flags permet de spécifier les objets à mettre à jour en utilisant des combinaisons des bits suivants:
     * <ul>
     * <li>1 departements</li>
     * <li>2 communes et arrondissements</li>
     * <li>4 troncons</li>
     * <li>8 voies</li>
     * <li>16 adresses</li>
     * <li>32 table voies, s'il est présent la table de voies de la mise à jour est recalculée à partir de la table de tronçons.</li>
     * </ul>
     * Toutefois, les bits 4, 8, et 32 seront ajoutés par défaut.
     * @param connectionOrigine la connection au référentiel actuel.
     * @param connectionDestination la connection au référentiel à jour préparé.
     * @param date la date à laquelle la mise à jour sera valable.
     * @param idsource spécifie s'il faut utiliser les id source ou les id destination
     */
    public void changementReferentiel(Processus p, String code_departement, int flags, Connection connectionOrigine,
            Connection connectionDestination, Date date, boolean idsource) throws SQLException, GestionReferentielException,
            ColonneException {
        flags |= 4 + 8 + 32; // Les voies et troncons sont obligatoires

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "ChangementReferentiel");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "departement : " + code_departement);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "flags : " + flags);
        p.resultat.add("ChangementReferentiel");
        p.resultat.add("departement : " + code_departement);
        p.resultat.add("flags : " + flags);

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "MAJ IDENTIFIANTS");
        p.state = new String[]{
            en_cours, "MAJ IDENTIFIANTS"
        };
        // Met à jour les identifiants de voie.
        GestionIdentifiants.mise_a_jour_identifiants(p, code_departement, connectionOrigine, connectionDestination, idsource);

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        p.state = new String[]{
            en_cours, "INVALIDATION DES TRONCONS"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INVALIDATION DES TRONCONS");
        // Invalide les troncons de l'ancien référentiel
        Timestamp tsdate = new Timestamp(date.getTime());
        Timestamp tsdatemoinsun = obtientDateMoinsUn(date);
        String nomTableTroncon = GestionHistoriqueTables.obtientDerniereTableTroncon(connectionOrigine, code_departement);
        gestionMiseAJour.invalideTroncons(nomTableTroncon, connectionOrigine, tsdate, tsdatemoinsun);

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        // crée une nouvelle table de troncons.
        nomTableTroncon = GestionHistoriqueTables.obtientNouveauNomPourTableTroncon(connectionOrigine, code_departement);
        GestionHistoriqueTables.enregistreNouvelleTableTroncons(connectionOrigine, nomTableTroncon, code_departement, date);

        // Effectue la mise à jour.
        p.state = new String[]{
            en_cours, maj
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, maj);
        // logs dans la méthode
        mise_a_jour(p, code_departement, flags, connectionOrigine, connectionDestination, date);
    }

    /**
     * Retourne si le mot spécifié dans le département spécifié est ambigu pour la catégorie spécifiée.<br>
     * Le référentiel de voie, indexé par les mots ambigus, est utilisé pour résoudre l'ambiguité (construit lors de la mise
     * à jour).<br>
     * Contrairement à la méthode resoudAmbiguite, le contexte n'est pas utilisé, car seule les adresses du référentiel qui se résument
     * au mot recherché sont considérées.<br>
     * @param mot le mot clé qui permet de résoudre l'ambiguité (ex: SAINT). Il ne s'agit pas forcemment du mot trouvé dans l'adresse.
     * @param categorie la catégorie de l'ambiguité trouvée
     * @param codes_departement les departements dans lequels l'ambiguité doit etre résolue. S'il est vide, les codes de département
     * par défaut sont utilisés.
     * @param connection la connection au référentiel utilisé pour résoudre l'ambiguité
     * @return true si le terme appartient à une voie.
     */
    public boolean resoudAmbiguiteSansContexte(String mot, String categorie, ArrayList<String> codes_departement, Connection connection)
            throws SQLException {
        boolean res = false;

        // Vérifie si les départements sont référencés.
        if (codes_departement.size() != 0) {
            for (int i = 0; i < codes_departement.size(); i++) {
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                if (!GestionTables.tableExiste("voa_voies_ambigues_"+codes_departement.get(i),connection))
                if (!GestionTables.tableExiste(GestionTables.getVoaVoiesAmbiguesTableName(codes_departement.get(i)), connection)) {
                    return false;
                }
            }
        } else {
            String[] departements = this.jdonrefParams.obtientCodeDepartementParDefaut();
            for (int i = 0; i < departements.length; i++) {
                codes_departement.add(departements[i]);
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                if (!GestionTables.tableExiste("voa_voies_ambigues_"+departements[i],connection))
                if (!GestionTables.tableExiste(GestionTables.getVoaVoiesAmbiguesTableName(departements[i]), connection)) {
                    return false;
                }
            }
        }

        // Prépare la requête permettant de trouver les voies semblables de même ambiguité
        StringBuilder sb = new StringBuilder();
        for (int i = 0; (i < codes_departement.size()) && !res; i++) {
            sb.setLength(0);
            sb.append("SELECT voa_mot FROM \"");
            sb.append(GestionTables.getVoaVoiesAmbiguesTableName(codes_departement.get(i)));
            sb.append("\" WHERE voa_mot=? AND voa_categorie_ambiguite=? AND ?=voa_lbl_pq  LIMIT 1");
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());
            psCherche.setString(1, mot);
            psCherche.setString(2, categorie);
            psCherche.setString(3, Algos.phonex(mot));

            ResultSet rsCherche = psCherche.executeQuery();
            if (rsCherche.next()) {
                res = true;
            }
            rsCherche.close();
            psCherche.close();
        }

        return res;
    }

    /**
     * Retourne si l'adresse spécifiée dans le département spécifiée est ambigue pour le mot et la catégorie spécifiée.<br>
     * Le référentiel de voie, indexé par les mots ambigus, est utilisé pour résoudre l'ambiguité (construit lors de la mise
     * à jour).<br>
     * Les voies ambigues indéxés qui ne sont composés que du mot recherché sont ignorées.
     * @param mot le mot clé qui permet de résoudre l'ambiguité (ex: SAINT)
     * @param motorigine le mot tel qu'il a été trouvé dans l'adresse (ex: ST)
     * @param categorie la catégorie de l'ambiguité trouvée
     * @param adresse l'adresse dans laquelle l'ambiguité a été trouvée
     * @param code_departement les departements dans lequels l'ambiguité doit etre résolue, S'il est vide, les codes de département
     * par défaut sont utilisés.
     * @param connection la connection au référentiel utilisé pour résoudre l'ambiguité
     * @return true si le terme appartient à une voie.
     */
    public boolean resoudAmbiguiteAvecContexte(String mot, String motorigine, String categorie, String adresse,
            ArrayList<String> codes_departement, Connection connection) throws SQLException {
        boolean res = false;
        // Vérifie si les départements sont référencés.
        if (codes_departement.size() != 0) {
            for (int i = 0; i < codes_departement.size(); i++) {
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                if (!GestionTables.tableExiste("voa_voies_ambigues_"+codes_departement.get(i),connection))
                if (!GestionTables.tableExiste(GestionTables.getVoaVoiesAmbiguesTableName(codes_departement.get(i)), connection)) {
                    return false;
                }
            }
        } else {
            String[] departements = this.jdonrefParams.obtientCodeDepartementParDefaut();
            for (int i = 0; i < departements.length; i++) {
                codes_departement.add(departements[i]);
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                if (!GestionTables.tableExiste("voa_voies_ambigues_"+departements[i],connection))
                if (!GestionTables.tableExiste(GestionTables.getVoaVoiesAmbiguesTableName(departements[i]), connection)) {
                    return false;
                }
            }
        }

        // le mot doit être retiré de l'adresse au préalable
        // notamment s'il s'agit d'une abbréviation
        // ex: l'abbréviation ST est incluse dans de nombreux
        // autres mots (ex: AUGUSTIN contient ST).
        adresse = Algos.supprimeMot(adresse, motorigine);

        // Prépare la requête permettant de trouver les voies semblables de même ambiguité
        StringBuilder sb = new StringBuilder();
        for (int i = 0; (i < codes_departement.size()) && !res; i++) {
            sb.setLength(0);
            sb.append("SELECT count(voa_mot) FROM \"");
            sb.append(GestionTables.getVoaVoiesAmbiguesTableName(codes_departement.get(i)));
            sb.append("\" WHERE voa_mot=? and voa_categorie_ambiguite=? and ?<>voa_lbl_pq and contexte(?, voa_lbl_pq,?)>=? LIMIT 1");
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());
            psCherche.setString(1, mot);
            psCherche.setString(2, categorie);
            psCherche.setString(3, Algos.phonex(mot));
            psCherche.setString(4, Algos.phonex(adresse));
            psCherche.setInt(5, jdonrefParams.obtientPourcentageDeCorrespondanceDeMot());
            psCherche.setInt(6, jdonrefParams.obtientSeuilDeNombreDeMotsCorrespondants());
            ResultSet rsCherche = psCherche.executeQuery();

            if (rsCherche.next()) {
                if (rsCherche.getInt(1) > 0) {
                    res = true;
                }
            }
            rsCherche.close();
            psCherche.close();
        }

        return res;
    }

    /**
     * Retourne si l'adresse spécifiée dans le département spécifiée est ambigue pour le mot et la catégorie spécifiée.<br>
     * Le référentiel de voie, indexé par les mots ambigus, est utilisé pour résoudre l'ambiguité (construit lors de la mise
     * à jour).
     * @param mot le mot clé qui permet de résoudre l'ambiguité (ex: SAINT)
     * @param motorigine le mot tel qu'il a été trouvé dans l'adresse (ex: ST)
     * @param categorie la catégorie de l'ambiguité trouvée
     * @param adresse l'adresse dans laquelle l'ambiguité a été trouvée
     * @param codse_departement les departements dans lequels l'ambiguité doit etre résolue. S'il est vide, les codes de département
     * par défaut sont utilisés.
     * @param connection la connection au référentiel utilisé pour résoudre l'ambiguité
     * @return true si le terme appartient à une voie.
     */
    public boolean resoudAmbiguite(String mot, String motorigine, String categorie, String adresse, ArrayList<String> codes_departement,
            Connection connection) throws SQLException {
        boolean res = false;

        // Vérifie si les départements sont référencés.
        if (codes_departement.size() != 0) {
            for (int i = 0; i < codes_departement.size(); i++) {
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                if (!GestionTables.tableExiste("voa_voies_ambigues_"+codes_departement.get(i),connection))
                if (!GestionTables.tableExiste(GestionTables.getVoaVoiesAmbiguesTableName(codes_departement.get(i)), connection)) {
                    return false;
                }
            }
        } else {
            String[] departements = this.jdonrefParams.obtientCodeDepartementParDefaut();
            for (int i = 0; i < departements.length; i++) {
                codes_departement.add(departements[i]);
                // WA 09/2011 utilisation de GestionTables.getXXTableName
//                if (!GestionTables.tableExiste("voa_voies_ambigues_"+departements[i],connection))
                if (!GestionTables.tableExiste(GestionTables.getVoaVoiesAmbiguesTableName(departements[i]), connection)) {
                    return false;
                }
            }
        }

        // le mot doit être retiré de l'adresse au préalable
        // notamment s'il s'agit d'une abbréviation
        // ex: l'abbréviation ST est incluse dans de nombreux
        // autres mots (ex: AUGUSTIN contient ST).
        adresse = Algos.supprimeMot(adresse, motorigine);

        // Prépare la requête permettant de trouver les voies semblables de même ambiguité
        StringBuilder sb = new StringBuilder();

        for (int i = 0; (i < codes_departement.size()) && !res; i++) {
            sb.setLength(0);
            sb.append("SELECT voa_mot FROM \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sbCom.append("voa_voies_ambigues_").append(codes_departement.get(i));
            sb.append(GestionTables.getVoaVoiesAmbiguesTableName(codes_departement.get(i)));
            sb.append("\" WHERE voa_mot=? and voa_categorie_ambiguite=? and (?=voa_lbl_pq or contexte(?, voa_lbl_pq,?)>=?) LIMIT 1");
            PreparedStatement psCherche = connection.prepareStatement(sb.toString());
            psCherche.setString(1, mot);
            psCherche.setString(2, categorie);
            psCherche.setString(3, Algos.phonex(mot));
            psCherche.setString(4, Algos.phonex(adresse));
            psCherche.setInt(5, jdonrefParams.obtientPourcentageDeCorrespondanceDeMot());
            psCherche.setInt(6, jdonrefParams.obtientSeuilDeNombreDeMotsCorrespondants());

            ResultSet rsCherche = psCherche.executeQuery();
            if (rsCherche.next()) {
                res = true;
            }

            rsCherche.close();
            psCherche.close();
        }

        return res;
    }

    /**
     * Permet de savoir si dans le contexte du mot spécifié, une ville qui contient des arrondissements
     * est présente.
     * @param mot le mot qui doit être le numéro d'arrondissement à tester
     * @param contexte le contexte du mot, qui doit contenir la ville à tester
     * @param connection
     * @return vrai si le mot est un arrondissement de la ville
     */
    public boolean resoudAmbiguiteArrondissement(String mot, String contexte, Connection connection) throws SQLException {
        contexte = Algos.supprimeMot(contexte, mot);

        PreparedStatement psCherche = connection.prepareStatement(
                "SELECT com_nom FROM com_communes WHERE NOT com_code_insee_commune is null AND contexte(?, com_nom_pq,?)>=? AND substr(com_code_insee,4,2)=? LIMIT 1");
        psCherche.setString(1, Algos.phonex(contexte));
        psCherche.setInt(2, jdonrefParams.obtientPourcentageDeCorrespondanceDeMot());
        psCherche.setInt(3, jdonrefParams.obtientSeuilDeNombreDeMotsCorrespondants());
        psCherche.setString(4, mot);

        ResultSet rs = psCherche.executeQuery();
        if (rs.next()) {
            rs.close();
            psCherche.close();
            return true;
        }
        rs.close();
        psCherche.close();
        return false;
    }

    /**
     * Valide l'adresse normalisée et restructurée proposée.<br>
     * Quelque soit les paramètres spécifiés, deux recherches peuvent être effectuées:
     * <ul>
     * <li>Une recherche phonétique</li>
     * <li>Si elle n'est pas concluante, une recherche phonétique et orthographique.</li>
     * </ul>
     * Toutefois, la recherche orthographique peut être forcée au moyen du paramètre force.<br>
     * Le choix de l'algorithme est déterminé par la qualité des paramètres:
     * <ul>
     * <li>Si une commune est proposée mais pas de code postal, retourne un choix de communes (avec valideCommune), sauf</li>
     * <li>si une commune a une note égale à 200 ou qu'aucune autre commune proposée n'a une note supérieure à
     * NotePourCommuneValide, auquel cas cette commune est choisie.</li>
     * <li>Si seul le code postal est spécifié, valide avec ce code postal (avec valideVoieCodePostalCommune).</li>
     * <li>Sinon, si une voie est proposée, valide avec code postal et commune (avec valideVoieCodePostal).</li>
     * <li>Sinon, si aucune voie n'est proposée, retourne une liste de choix de communes et codes postaux (avec valideCommuneEtCodePostal).</li>
     * </ul>
     * @param lignes un tableau de 6 lignes représentant une adresse.
     * @param strdate la date à laquelle la validation est effectuée au format JJ/MM/AAAA
     * @param force pour force la recherche par correspondance phonétique et orthographique
     * @return null si ni le code postal ni la commune n'ont été trouvés dans la ligne 6.
     */
    public String[] valide(int application, String[] lignes, String strdate, boolean force, boolean gererPays, Connection connection) throws
            SQLException {
        // WA 01/2012 Pays
        int nbLinesAddr = gererPays ? 7 : 6;

        if (lignes == null || lignes.length < nbLinesAddr) {
//            jdonrefParams.getGestionLog().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
            jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
            return new String[]{"0", "5", "L'adresse spécifiée ne comporte pas ses " + nbLinesAddr + " lignes"};
        }

        String ligne4 = lignes[3];
        String ligne6 = lignes[5];

        RefCle refcodepostal = gestionMots.trouveCodePostal(ligne6);
        RefCle refcommune = gestionMots.trouveNomVille(ligne6, refcodepostal);

        String codepostal = refcodepostal.obtientMot();
        String commune = refcommune.obtientMot();
        String pays = null;     // WA 01/2012 Pays
        if (gererPays) {
            pays = lignes[6];
        }

        Date date;
        if (strdate == null) {
            date = Calendar.getInstance().getTime();
        } else {
            try {
                // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                // date = sdformat.parse(strdate);
                date = DateUtils.parseStringToDate(strdate, sdformat);
            } catch (java.text.ParseException pe) {
                jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                return new String[]{"0", "5", "La date est mal formée."};
            }
        }

        // WA 01/2012 Pays
        boolean continuerValidation = false;    // Indique si on peut poursuivre la validation aux dpt/communes, etc ...
        String[] paysValides = null;
        String codeAcs3 = null;
        if (gererPays) {
            if ((pays == null) || (pays.length() == 0)) {
                // Si on n'a pas de pays : pays par defaut
                pays = lignes[6] = jdonrefParams.obtientPaysParDefaut();
            }
            paysValides = gestionValidation.validePays(application, lignes, date, force, connection);
            if (paysValides[GestionValidation.VALIDE_CODE_FONCTION].equals("5") || paysValides[GestionValidation.VALIDE_CODE_FONCTION].equals("6")) {
                // Determine si on peut continuer la validation
                // ie on a des resultats,
                // il n'y en a qu'un qui possède une note suffisante
                // et ce pays est celui par defaut
                int size = Integer.parseInt(paysValides[GestionValidation.VALIDE_NB_RES]);
                if (size > 0) {
                    int offset = 0;
                    int note = 0;
                    int notePourPaysValide = jdonrefParams.obtientNotePourPaysValide();
                    do {
                        note = Integer.parseInt(
                                paysValides[GestionValidation.VALIDEPAYS_NOTE + (GestionValidation.VALIDEPAYS_TABSIZE * offset)]);
                        offset++;
                    } while ((offset < size) && (note >= notePourPaysValide));
                    if ((offset == 0) && (note >= notePourPaysValide) || (offset == 1)) // On a un resultat raisonnablement sur
                    {
                        pays = paysValides[GestionValidation.VALIDEPAYS_LIGNE7_DESABBREVIE];
                        codeAcs3 = paysValides[GestionValidation.VALIDEPAYS_CODEAC3];
                        continuerValidation = pays.equals(jdonrefParams.obtientPaysParDefaut());
                    }
                }
            }
            if (!continuerValidation) {
                jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_PAYS, false);
                return paysValides;
            } else {
                pays = pays + ";" + codeAcs3; // INSERE LE CODE PAYS DANS TOUS LES RESULTATS.
            }
        }

        if (commune.length() > 0) {
            if (codepostal.length() == 0) {
                // Les logs sont effectués dans cette méthode
                String[] communes = gestionValidation.valideCommune(application, lignes, refcommune, date, force, gererPays, pays,
                        connection);

                // Vérifie si la première commune trouvée n'est pas la seule qui pourrait correspondre.
                if (ligne4.length() > 0 && (communes[GestionValidation.VALIDE_CODE_FONCTION].compareTo("3") == 0 ||
                        communes[GestionValidation.VALIDE_CODE_FONCTION].compareTo("4") == 0)) {
                    boolean dptok = false;
                    int malusPays = gererPays ? 1 : 0;

                    int size = Integer.parseInt(communes[GestionValidation.VALIDE_NB_RES]);
                    if (size > 0) {
                        int note1 = Integer.parseInt(communes[GestionValidation.VALIDECOMMUNE_NOTE_NP + malusPays]);
                        // WA 09/2011 Utilisation de trouveCodeDepartement au lieu de trouveCodePostal().substring(0, 2)
                        // String dpt1 = gestionMots.trouveCodePostal(communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE]).obtientMot().substring(0,2);
                        String dpt1 = gestionMots.trouveCodeDepartement(communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP + malusPays]).
                                obtientMot();

                        if (note1 >= jdonrefParams.obtientNotePourCommuneValide()) {
                            if (size > 1) {
                                String dptx;
                                int i = 0, note = 0, offset = malusPays;
                                boolean compare_dpt;
                                do {
                                    i++;
                                    offset += GestionValidation.VALIDECOMMUNE_TABSIZE;
                                    note = Integer.parseInt(communes[GestionValidation.VALIDECOMMUNE_NOTE_NP + offset]);
                                    // WA 09/2011 Utilisation de trouveCodeDepartement au lieu de trouveCodePostal().substring(0, 2)
                                    // dptx = gestionMots.trouveCodePostal(communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE+offset]).obtientMot().substring(0,2);
                                    dptx = gestionMots.trouveCodeDepartement(
                                            communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP + offset]).obtientMot();
                                    compare_dpt = dptx.compareTo(dpt1) == 0;
                                } while ((i + 1) < size && note >= jdonrefParams.obtientNotePourCommuneValide() && compare_dpt);
                                // Si toutes les communes dont la note est suffisante appartiennent au même département,
                                // c'est ok.
                                if (note < jdonrefParams.obtientNotePourCommuneValide()) {
                                    dptok = true;
                                } else if (compare_dpt) {
                                    dptok = true;
                                }
                            } else {
                                dptok = true;
                            }
                        }

                        if (dptok) {
                            // La recherche peut alors être effectuée sur le département de la commune trouvée.
                            StringBuilder commune1 = new StringBuilder();
                            commune1.append(dpt1);
                            commune1.append(' ');
                            commune1.append(ligne6);
                            RefCle rccodepostal = new RefCle(dpt1, 0, CategorieMot.CodePostal);
                            String[] dpt_lignes = new String[]{
                                lignes[0], lignes[1], lignes[2], ligne4, lignes[4], commune1.toString()
                            };

                            // La première note est la seule qui correspond bien, la commune est choisie.
                            // Les logs sont effectués dans cette méthode.
                            String[] res = gestionValidation.valideVoieCodePostalCommune(application, dpt_lignes, rccodepostal, null, date,
                                    force, gererPays, pays, connection);

                            return res;
                        }
                    }
                }
                return communes;
            } else {
                if (ligne4.length() > 0) // Les logs sont effectués dans cette méthode
                {
                    return gestionValidation.valideVoieCodePostalCommune(application, lignes, refcodepostal, refcommune, date, force,
                            gererPays, pays, connection);
                } else // Les logs sont effectués dans cette méthode
                {
                    return gestionValidation.valideCommuneEtCodePostal(application, lignes, refcodepostal, refcommune, date, force,
                            gererPays, pays, connection);
                }
            }
        } else if (codepostal.length() > 0) {
            if (ligne4.length() > 0) // Les logs sont effectués dans cette méthode
            {
                return gestionValidation.valideVoieCodePostal(application, lignes, date, force, gererPays, pays, connection);
            } else // Les logs sont effectués dans cette méthode
            {
                return gestionValidation.valideCodePostal(application, lignes, date, force, gererPays, pays, connection);
            }
        }

        // Si on n'a rien trouve de probant dans l'adresse mais q'on avait un pays valide, on valide au pays.
        if (gererPays && continuerValidation) {
            jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_PAYS, false);
            return paysValides;
        }

        jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
        return new String[]{
            "0", "8", "L'adresse ne comprend ni code postal ni nom de commune."
        };
    }

    public List<String[]> valide(int application, int[] services, String[] lignes, String strdate, boolean force, boolean gererPays, Connection connection) throws
            SQLException {
        final List<String[]> listRet = new ArrayList<String[]>();
        // WA 01/2012 Pays
        int nbLinesAddr = gererPays ? 7 : 6;

        if (lignes == null || lignes.length < nbLinesAddr) {
            jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
            listRet.add(new String[]{"0", "5", "L'adresse spécifiée ne comporte pas ses " + nbLinesAddr + " lignes"}); // RETURN
        }

        String ligne4 = lignes[3];
        String ligne6 = lignes[5];

        RefCle refcodepostal = gestionMots.trouveCodePostal(ligne6);
        RefCle refcommune = gestionMots.trouveNomVille(ligne6, refcodepostal);

        String codepostal = refcodepostal.obtientMot();
        String commune = refcommune.obtientMot();
        String pays = null;     // WA 01/2012 Pays
        String codeAcs3 = null;
        if (gererPays) {
            pays = lignes[6];
        }

        Date date = null;
        if (strdate == null) {
            date = Calendar.getInstance().getTime();
        } else {
            try {
                // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                // date = sdformat.parse(strdate);
                date = DateUtils.parseStringToDate(strdate, sdformat);
            } catch (java.text.ParseException pe) {
                jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                listRet.add(new String[]{"0", "5", "La date est mal formée."}); // RETURN
            }
        }
        // PAYS
        if ((pays == null) || (pays.length() == 0)) {
            // Si on n'a pas de pays : pays par defaut
            lignes = Arrays.copyOf(lignes, 7);
            pays = lignes[6] = jdonrefParams.obtientPaysParDefaut();
        }
        String[] paysValides = gestionValidation.validePays(application, lignes, date, force, connection);
        if (paysValides[GestionValidation.VALIDE_CODE_FONCTION].equals("5") || paysValides[GestionValidation.VALIDE_CODE_FONCTION].equals("6")) {
            // Determine si on peut continuer la validation
            // ie on a des resultats,
            // il n'y en a qu'un qui possède une note suffisante
            // et ce pays est celui par defaut
            int size = Integer.parseInt(paysValides[GestionValidation.VALIDE_NB_RES]);
            if (size > 0) {
                int offset = 0;
                int note = 0;
                int notePourPaysValide = jdonrefParams.obtientNotePourPaysValide();
                do {
                    note = Integer.parseInt(
                            paysValides[GestionValidation.VALIDEPAYS_NOTE + (GestionValidation.VALIDEPAYS_TABSIZE * offset)]);
                    offset++;
                } while ((offset < size) && (note >= notePourPaysValide));
                if ((offset == 0) && (note >= notePourPaysValide) || (offset == 1)) // On a un resultat raisonnablement sur
                {
                    pays = paysValides[GestionValidation.VALIDEPAYS_LIGNE7_DESABBREVIE];
                    codeAcs3 = paysValides[GestionValidation.VALIDEPAYS_CODEAC3];
                }
            }
        }
        // POUR INSERER LE CODE PAYS DANS TOUS LES RESULTATS.
        String paysLigneId7 = null;
        if (pays != null && codeAcs3 != null) {
            paysLigneId7 = pays + ";" + codeAcs3;
        } else if (pays != null && codeAcs3 == null) {
            paysLigneId7 = pays;
        }
        for (Integer serviceCle : services) {
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(serviceCle).getId();
            
            //if (serviceCle == SERVICE_POINT_ADRESSE) {           
            if (id == SERVICE_POINT_ADRESSE) { 
                
                continue; // NON IMPLEMENTE
            } else if (id == SERVICE_ADRESSE) {
                listRet.add(valide(application, lignes, strdate, force, gererPays, connection));
            } else if (id == SERVICE_PAYS) {
                listRet.add(paysValides);
            } else if (id == SERVICE_DEPARTEMENT) {
                if (codepostal.length() > 0) {
                    listRet.add(gestionValidation.valideCodePostal(application, lignes, date, force, gererPays, paysLigneId7, connection));
                } else {
                    jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                    listRet.add(new String[]{"0", "8", "L'adresse ne comprend pas de code postal."});
                }
            } else if (id == SERVICE_COMMUNE) {
                if (commune.length() > 0) {
                    if (codepostal.length() == 0) {
                        listRet.add(gestionValidation.valideCommune(application, lignes, refcommune, date, force, gererPays, paysLigneId7,
                                connection));
                    } else {
                        listRet.add(gestionValidation.valideCommuneEtCodePostal(application, lignes, refcodepostal, refcommune, date, force,
                                gererPays, paysLigneId7, connection));
                    }
                } else if (codepostal.length() > 0) {
                    listRet.add(gestionValidation.valideCodePostal(application, lignes, date, force, gererPays, pays, connection));

                } else {
                    jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                    listRet.add(new String[]{"0", "8", "L'adresse ne comprend pas de nom de commune ni de code postal."});
                }
            } else { // TRONCON ET VOIE
                final boolean auTroncon = (id == SERVICE_TRONCON);
                if (commune.length() > 0) {
                    if (codepostal.length() == 0) {
                        // Les logs sont effectués dans cette méthode
                        String[] communes = gestionValidation.valideCommune(application, lignes, refcommune, date, force, gererPays, paysLigneId7,
                                connection);

                        // Vérifie si la première commune trouvée n'est pas la seule qui pourrait correspondre.
                        if (ligne4.length() > 0 && (communes[GestionValidation.VALIDE_CODE_FONCTION].compareTo("3") == 0 ||
                                communes[GestionValidation.VALIDE_CODE_FONCTION].compareTo("4") == 0)) {
                            boolean dptok = false;
                            int malusPays = gererPays ? 1 : 0;

                            int size = Integer.parseInt(communes[GestionValidation.VALIDE_NB_RES]);
                            if (size > 0) {
                                int note1 = Integer.parseInt(communes[GestionValidation.VALIDECOMMUNE_NOTE_NP + malusPays]);
                                // WA 09/2011 Utilisation de trouveCodeDepartement au lieu de trouveCodePostal().substring(0, 2)
                                // String dpt1 = gestionMots.trouveCodePostal(communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE]).obtientMot().substring(0,2);
                                String dpt1 = gestionMots.trouveCodeDepartement(communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP + malusPays]).
                                        obtientMot();

                                if (note1 >= jdonrefParams.obtientNotePourCommuneValide()) {
                                    if (size > 1) {
                                        String dptx;
                                        int i = 0, note = 0, offset = malusPays;
                                        boolean compare_dpt;
                                        do {
                                            i++;
                                            offset += GestionValidation.VALIDECOMMUNE_TABSIZE;
                                            note = Integer.parseInt(communes[GestionValidation.VALIDECOMMUNE_NOTE_NP + offset]);
                                            // WA 09/2011 Utilisation de trouveCodeDepartement au lieu de trouveCodePostal().substring(0, 2)
                                            // dptx = gestionMots.trouveCodePostal(communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE+offset]).obtientMot().substring(0,2);
                                            dptx = gestionMots.trouveCodeDepartement(
                                                    communes[GestionValidation.VALIDECOMMUNE_LIGNE6_DESABBREVIE_NP + offset]).obtientMot();
                                            compare_dpt = dptx.compareTo(dpt1) == 0;
                                        } while ((i + 1) < size && note >= jdonrefParams.obtientNotePourCommuneValide() && compare_dpt);
                                        // Si toutes les communes dont la note est suffisante appartiennent au même département,
                                        // c'est ok.
                                        if (note < jdonrefParams.obtientNotePourCommuneValide()) {
                                            dptok = true;
                                        } else if (compare_dpt) {
                                            dptok = true;
                                        }
                                    } else {
                                        dptok = true;
                                    }
                                }

                                if (dptok) {
                                    // La recherche peut alors être effectuée sur le département de la commune trouvée.
                                    StringBuilder commune1 = new StringBuilder();
                                    commune1.append(dpt1);
                                    commune1.append(' ');
                                    commune1.append(ligne6);
                                    RefCle rccodepostal = new RefCle(dpt1, 0, CategorieMot.CodePostal);
                                    String[] dpt_lignes = new String[]{
                                        lignes[0], lignes[1], lignes[2], ligne4, lignes[4], commune1.toString()
                                    };

                                    // La première note est la seule qui correspond bien, la commune est choisie.
                                    // Les logs sont effectués dans cette méthode.
                                    listRet.add(gestionValidation.valideVoieCodePostalCommune(application, dpt_lignes, rccodepostal, null, date,
                                            auTroncon, force, gererPays, paysLigneId7, connection));
                                }
                            }
                        } else {
                            jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                            listRet.add(new String[]{"0", "8", "L'adresse ne comprend pas de nom de voie."});
                        }
                    } else {
                        if (ligne4.length() > 0) {
                            listRet.add(gestionValidation.valideVoieCodePostalCommune(application, lignes, refcodepostal, refcommune, date,
                                    auTroncon, force, gererPays, paysLigneId7, connection));
                        } else {
                            jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                            listRet.add(new String[]{"0", "8", "L'adresse ne comprend pas de nom de voie."});
                        }
                    }
                } else if (codepostal.length() > 0) {
                    if (ligne4.length() > 0) {
                        listRet.add(gestionValidation.valideVoieCodePostal(application, lignes, date, auTroncon, force, gererPays, paysLigneId7, connection));
                    } else {
                        jdonrefParams.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                        listRet.add(new String[]{"0", "8", "L'adresse ne comprend pas de nom de voie."});
                    }
                }
            }

        }

        return listRet;
    }

    /**
     * Trouve des mises à jour de nom pour la voie spécifiée à la date spécifiée.<br>
     * Les mises à jour répertoriées sont les changements de nom, de commune, de code postal.<br>
     * Les changements au niveau de la géométrie ou les changements de numéro d'adresse ne sont
     * pas retournés par cette méthode.<br>
     * La revalidation des communes sans voie ne tient pas compte des scissions et des fusions
     * de communes (car cela nécessiterait un calcul de géométrie trop couteux ou d'obtenir).<br>
     * En cas de succès, le résultat est de la forme:
     * <ul>
     *   <li>6</li>
     *   <li>nombre d'adresses retournées</li>
     *   <li>identifiant de la voie 1</li>
     *   <li>ligne 4 de l'adresse à jour 1</li>
     *   <li>ligne 6 de l'adresse à jour 1</li>
     *   <li>code insee 1</li>
     *   <li>t0Voie de l'adresse 1</li>
     *   <li>t1Voie de l'adresse 1</li>
     *   <li>identifiant de la voie 2</li>
     *   <li>...</li>
     * </ul>
     * En cas d'erreur, le résultat est de la forme:
     * <ul>
     * <li>0</li>
     * <li>code erreur</li>
     * <li>message d'erreur</li>
     * </ul>
     * @param dateValidation la date à laquelle l'adresse a été validée.
     * @param date la date à laquelle l'adresse doit être revalidée.
     */
    public String[] revalide(int application, String[] lignes, String strdateValidation, String strdate, Connection connection) throws
            SQLException {
        if (lignes.length < 6) {
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            return new String[]{"0", "5", "L'adresse ne comporte pas ses 6 lignes"};
        }

        String ligne4 = lignes[3];
        String ligne6 = lignes[5];
        String ligne7 = null;
        String codeSovAc3Origine = null;

        Date dateValidation = null;
        try {
            // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//                dateValidation = sdformat.parse(strdateValidation);
            dateValidation = DateUtils.parseStringToDate(strdateValidation, sdformat);

        } catch (java.text.ParseException pe) {
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            return new String[]{"0", "5", "La date de validation est mal formée."};
        }
        Date date;
        if (strdate != null) {
            try {
                // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//                date=sdformat.parse(strdate);
                date = DateUtils.parseStringToDate(strdate, sdformat);
            } catch (java.text.ParseException pe) {
                jdonrefParams.getGestionLog().logRevalidation(application, false);
                return new String[]{
                    "0", "5", "La date de revalidation est mal formée."
                };
            }
        } else {
            date = Calendar.getInstance().getTime();
        }

        Timestamp tsvalidation = new Timestamp(dateValidation.getTime());
        Timestamp tsrevalidation = new Timestamp(date.getTime());

        if (lignes.length > 6) // il y a une ligne 7
        {
            ligne7 = lignes[6];
            if (ligne7 != null) {
                // Verification du pays
                List<PayPaysBean> pays = RecherchesDao.foundPaysByNameAtDate(connection, ligne7, dateValidation, 1);
                if ((pays == null) || (pays.size() != 1)) {
                    return new String[]{"0", "8", "Le pays de l'adresse spécifiée n'a pas été trouvé."};
                }
                codeSovAc3Origine = pays.get(0).getSovAc3();
                // Si la version actuelle du pays n'est pas le pays par defaut, on ne revalide que le pays
                pays = RecherchesDao.foundPaysByCodeAtDate(connection, codeSovAc3Origine, new Date(), 1);
                if ((pays != null) && (pays.size() == 1)) {
                    String actualPaysName = pays.get(0).getNomFr();
                    // Si le pays trouve n'est pas le pays par defaut ou s'il n'y a ni ligne 4 ni ligne 6
                    // on ne revalide QUE le pays
                    if (((ligne4.length() == 0) && (ligne6.length() == 0)) || (!jdonrefParams.obtientPaysParDefaut().equals(actualPaysName))) {
                        return revalidePays(codeSovAc3Origine, date, connection);
                    }
                }
            }
        }

        // Vérifie si un département est spécifié.
        RefCle codedepartement = gestionMots.trouveCodeDepartement(lignes[5]);
        // WA gestion Dpts les codes dpt ne font plus forcement 2 cars
//        if(codedepartement.obtientMot().length() != 2)
        if (codedepartement.obtientMot().length() < 2) {
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            return new String[]{"0", "8", "Le département de l'adresse spécifiée n'a pas été trouvé."};
        }

        //
        // Cherche la commune dans l'adresse.
        //
        RefCle codepostal = gestionMots.trouveCodePostal(ligne6);
        RefCle commune = gestionMots.trouveNomVille(ligne6, codepostal);

        if (codepostal.obtientMot().length() == 0 || commune.obtientMot().length() == 0) {
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            return new String[]{"0", "8", "Il manque la commune ou le code postal à l'adresse."};
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT com_communes.com_code_insee FROM com_communes,cdp_codes_postaux ");
        sb.append("WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND com_nom=? AND cdp_code_postal=? AND ");
        sb.append("com_communes.t0<=? AND com_communes.t1>=? AND ");
        sb.append("cdp_codes_postaux.t0<=? AND cdp_codes_postaux.t1>=? ");
        sb.append("LIMIT 1");
        PreparedStatement psChercheCommune = connection.prepareStatement(sb.toString());
        psChercheCommune.setString(1, commune.obtientMot());
        psChercheCommune.setString(2, codepostal.obtientMot());
        psChercheCommune.setTimestamp(3, tsvalidation);
        psChercheCommune.setTimestamp(4, tsvalidation);
        psChercheCommune.setTimestamp(5, tsvalidation);
        psChercheCommune.setTimestamp(6, tsvalidation);
        ResultSet rsChercheCommune = psChercheCommune.executeQuery();

        if (!rsChercheCommune.next()) {
            rsChercheCommune.close();
            psChercheCommune.close();
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            return new String[]{"0", "8", "La commune spécifiée dans l'adresse n'a pas été trouvée."};
        }

        String code_insee = rsChercheCommune.getString(1);
        rsChercheCommune.close();
        psChercheCommune.close();

        if (ligne4.length() == 0) // Si la voie n'est pas spécifiée, il s'agit de chercher les mises à jour de la commune.
        {
            // Cherche les éventuelles mises à jour de cette commune  
            sb.setLength(0);
            sb.append(
                    "SELECT com_nom,cdp_code_postal,com_communes.t0,com_communes.t1,cdp_codes_postaux.t0,cdp_codes_postaux.t1 FROM com_communes,cdp_codes_postaux ");
            sb.append("WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND ");
            sb.append(
                    "com_communes.com_code_insee=? AND com_communes.t0<=? AND com_communes.t1>=? AND cdp_codes_postaux.t0<=? AND cdp_codes_postaux.t1>=?");
            PreparedStatement psChercheCommuneSuivantes = connection.prepareStatement(sb.toString());

            psChercheCommuneSuivantes.setString(1, code_insee);
            psChercheCommuneSuivantes.setTimestamp(2, tsrevalidation);
            psChercheCommuneSuivantes.setTimestamp(3, tsvalidation);
            psChercheCommuneSuivantes.setTimestamp(4, tsrevalidation);
            psChercheCommuneSuivantes.setTimestamp(5, tsvalidation);

            ArrayList<String> nom = new ArrayList<String>();
            ArrayList<String> code_postal = new ArrayList<String>();
            ArrayList<Timestamp> t0s = new ArrayList<Timestamp>();
            ArrayList<Timestamp> t1s = new ArrayList<Timestamp>();

            ResultSet rsChercheCommuneSuivantes = psChercheCommuneSuivantes.executeQuery();
            while (rsChercheCommuneSuivantes.next()) {
                String nom_suivant = rsChercheCommuneSuivantes.getString(1);
                String code_postal_suivant = rsChercheCommuneSuivantes.getString(2);

                if (nom_suivant.compareTo(commune.obtientMot()) != 0 ||
                        code_postal_suivant.compareTo(codepostal.obtientMot()) != 0) {
                    nom.add(nom_suivant);
                    code_postal.add(code_postal_suivant);

                    Timestamp commune_t0 = rsChercheCommuneSuivantes.getTimestamp(3);
                    Timestamp commune_t1 = rsChercheCommuneSuivantes.getTimestamp(4);
                    Timestamp cp_t0 = rsChercheCommuneSuivantes.getTimestamp(5);
                    Timestamp cp_t1 = rsChercheCommuneSuivantes.getTimestamp(6);

                    if (commune_t0.getTime() < cp_t0.getTime()) {
                        t0s.add(cp_t0);
                    } else {
                        t0s.add(commune_t0);
                    }

                    if (commune_t1.getTime() < cp_t1.getTime()) {
                        t1s.add(commune_t1);
                    } else {
                        t1s.add(cp_t1);
                    }
                }
            }

            rsChercheCommuneSuivantes.close();
            psChercheCommuneSuivantes.close();

            // crée le résultat.
            int blockSize = (codeSovAc3Origine != null) ? 8 : 6;
            String[] res = new String[2 + blockSize * nom.size()];

            res[0] = "6";
            res[1] = Integer.toString(nom.size());
            for (int i = 0; i < nom.size(); i++) {
                res[2 + blockSize * i] = "";
                res[3 + blockSize * i] = "";
                res[4 + blockSize * i] = Algos.unsplit(code_postal.get(i), nom.get(i));
                res[5 + blockSize * i] = code_insee;
                if (codeSovAc3Origine != null) {
                    res[6 + blockSize * i] = ligne7;
                    res[7 + blockSize * i] = codeSovAc3Origine;
                }
                Date dt0 = new Date(t0s.get(i).getTime());
                Date dt1 = new Date(t1s.get(i).getTime());
                // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//                resCom[6+6*i] = sdformat.format(dt0Com);
//                resCom[7+6*i] = sdformat.format(dt1Com);
                res[blockSize + blockSize * i] = DateUtils.formatDateToString(dt0, sdformat);
                res[blockSize + 1 + blockSize * i] = DateUtils.formatDateToString(dt1, sdformat);
            }

            jdonrefParams.getGestionLog().logRevalidation(application, true);
            return res;
        } else // Sinon, il s'agit de chercher les mises à jour de la voie complète.
        {
            //
            // Cherche la voie décrite dans l'adresse.
            //
            ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(ligne4);
            RefCle typedevoie = gestionMots.trouveTypeVoie(ligne4, numeros);
            RefCle article, libelle;
            if (typedevoie.obtientMot().length() > 0) {
                article = gestionMots.trouveArticleVoie(ligne4, typedevoie);
                libelle = gestionMots.trouveLibelleVoie(ligne4, article);
            } else {
                article = new RefCle("", 0);
                libelle = gestionMots.trouveLibelleVoie(ligne4, numeros);
            }
            String nomVoie = Algos.unsplit(typedevoie.obtientMot(), article.obtientMot(), libelle.obtientMot());

            sb.setLength(0);
            sb.append("SELECT voi_id FROM \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
            // sbCom.append("voi_voies_").append(codedepartement.obtientMot());
            sb.append(GestionTables.getVoiVoiesTableName(codedepartement.obtientMot()));
            sb.append("\" where voi_nom=? and com_code_insee=? and t0<=? and t1>=? LIMIT 1");
            PreparedStatement psChercheVoie = connection.prepareStatement(sb.toString());
            psChercheVoie.setString(1, nomVoie);
            psChercheVoie.setString(2, code_insee);
            psChercheVoie.setTimestamp(3, tsvalidation);
            psChercheVoie.setTimestamp(4, tsvalidation);
            ResultSet rsVoie = psChercheVoie.executeQuery();

            if (!rsVoie.next()) {
                rsVoie.close();
                psChercheVoie.close();
                jdonrefParams.getGestionLog().logRevalidation(application, false);
                return new String[]{
                    "0", "8", "La voie spécifiée n'a pas été trouvée."
                };
            }

            String voi_id = rsVoie.getString(1);
            rsVoie.close();
            psChercheVoie.close();

            // Cherche les éventuelles mises à jour de cette voie (hors géométrie et numéros d'adresse)
            sb.setLength(0);
            // sbCom.append("SELECT voi_id_suivant,t0Voie FROM \"vhi_voies_historisees_");
            // sbCom.append(codedepartement.obtientMot());
            sb.append("SELECT voi_id_suivant,t0 FROM ");
            sb.append(GestionTables.getVhiVoiesHistoriseeTableName(codedepartement.obtientMot()));
            sb.append(" WHERE voi_id_precedent=? AND t0>=?");
            PreparedStatement psChercheVoieSuivantes = connection.prepareStatement(sb.toString());
            psChercheVoieSuivantes.setString(1, voi_id);
            psChercheVoieSuivantes.setTimestamp(2, tsvalidation);
            ResultSet rsChercheVoieSuivantes = psChercheVoieSuivantes.executeQuery();

            // Extrait les identifiants de voies
            ArrayList<String> voi_ids = new ArrayList<String>();
            ArrayList<Timestamp> t0s = new ArrayList<Timestamp>();

            voi_ids.add(voi_id); // la recherche porte aussi sur la voie utilisée actuellement.
            t0s.add(tsvalidation);

            while (rsChercheVoieSuivantes.next()) {
                voi_ids.add(rsChercheVoieSuivantes.getString(1));
                t0s.add(rsChercheVoieSuivantes.getTimestamp(2));
            }

            rsChercheVoieSuivantes.close();
            psChercheVoieSuivantes.close();

            // Prépare la requête permettant de chercher les propriétés des voies trouvées.
            sb.setLength(0);
            sb.append(
                    "SELECT voies.voi_nom,voies.com_code_insee,voies.t0,voies.t1,communes.com_nom,voies.cdp_code_postal," +
                    "communes.t0,communes.t1,voies.voi_nom_origine,communes.com_nom_origine FROM \""); // HM
            // WA 09/2011 utilisation de GestionTables.getXXTableName
            // sbCom.append("voi_voies_").append(codedepartement.obtientMot());
            sb.append(GestionTables.getVoiVoiesTableName(codedepartement.obtientMot()));
            sb.append("\" as voies, com_communes as communes ");
            sb.append("WHERE voies.voi_id=? AND voies.t1>=? AND voies.com_code_insee=communes.com_code_insee AND ");
            sb.append("NOT (voies.t1<communes.t0 OR communes.t1<voies.t0)");
            PreparedStatement psChercheVoieSuivante = connection.prepareStatement(sb.toString());

            // Cherche les informations concernant ces voies,
            // et ne conserve que celles qui sont différentes de la voie actuelle.
            ArrayList<String> voi_id_suivantes = new ArrayList<String>();
            ArrayList<String> voi_nom_origine_suivantes = new ArrayList<String>(); // HM
            ArrayList<String> voies_suivantes = new ArrayList<String>();
            ArrayList<String> codeinsee_suivants = new ArrayList<String>();
            ArrayList<String> commune_suivants = new ArrayList<String>();
            ArrayList<String> commune_nom_origine_suivantes = new ArrayList<String>(); // HM
            ArrayList<String> codepostal_suivants = new ArrayList<String>();
            ArrayList<Timestamp> t0_suivants = new ArrayList<Timestamp>();
            ArrayList<Timestamp> t1_suivants = new ArrayList<Timestamp>();
            for (int i = 0; i < voi_ids.size(); i++) // Pour chaque identifiant trouvé, cherche les propriétés
            {
                String voi_id_suivante = voi_ids.get(i);
                Timestamp t0_suivant = t0s.get(i);

                // Cherche les propriétés pour la voie en cour,
                psChercheVoieSuivante.setString(1, voi_id_suivante);
                psChercheVoieSuivante.setTimestamp(2, tsrevalidation);
                ResultSet rsChercheVoieSuivante = psChercheVoieSuivante.executeQuery();
                while (rsChercheVoieSuivante.next()) {
                    String nom_suivant = rsChercheVoieSuivante.getString(1);
                    String code_insee_suivant = rsChercheVoieSuivante.getString(2);
                    String nomcommune_suivant = rsChercheVoieSuivante.getString(5);
                    String nomcodepostal_suivant = rsChercheVoieSuivante.getString(6);
                    Timestamp voiet0 = rsChercheVoieSuivante.getTimestamp(3);
                    Timestamp voiet1 = rsChercheVoieSuivante.getTimestamp(4);
                    Timestamp comt0 = rsChercheVoieSuivante.getTimestamp(7);
                    Timestamp comt1 = rsChercheVoieSuivante.getTimestamp(8);
                    String voieNomOrigine = rsChercheVoieSuivante.getString(9); // HM
                    String communeNomOrigine = rsChercheVoieSuivante.getString(10); // HM
                    Timestamp t0;
                    if (voiet0.getTime() < comt0.getTime()) {
                        t0 = comt0;
                    } else {
                        t0 = voiet0;
                    }
                    // Prend en compte si nécessaire la mise à jour.
                    if (t0_suivant != t0) // si l'intervalle de validité n'est pas le même.
                    /*if (nom_suivant.compareTo(nomVoie)!=0 ||          // (méthode précédente : si une propriété a changé
                    code_insee_suivant.compareTo(code_insee)!=0 ||  // cette méthode a été écartée car elle ne permet
                    nomcommune_suivant.compareTo(commune.obtientMot())!=0 || // pas de revalidation lorsque la géométrie
                    nomcodepostal_suivant.compareTo(codepostal.obtientMot())!=0)*/ // change)
                    {
                        // Recalcule l'intervalle de validité à partir de celui de la voie et celui de la commune.

                        Timestamp t1;
                        if (voiet1.getTime() < comt1.getTime()) {
                            t1 = voiet1;
                        } else {
                            t1 = comt1;
                        }
                        voies_suivantes.add(nom_suivant);
                        voi_nom_origine_suivantes.add(voieNomOrigine); // HM
                        codeinsee_suivants.add(code_insee_suivant);
                        t0_suivants.add(t0);
                        t1_suivants.add(t1);
                        commune_suivants.add(nomcommune_suivant);
                        commune_nom_origine_suivantes.add(nomcodepostal_suivant + "\u0020" + communeNomOrigine); // HM
                        codepostal_suivants.add(nomcodepostal_suivant);
                        voi_id_suivantes.add(voi_id_suivante);
                    }
                }
                rsChercheVoieSuivante.close();
            }
            psChercheVoieSuivante.close();

            int blockSize = (codeSovAc3Origine != null) ? 10 : 8; // HM +2 pour les noms desab de la voie et de la commune
            String[] res = new String[2 + voi_id_suivantes.size() * blockSize];

            String numero = null;
            String repetition = null;
            if (numeros.size() > 0) {
                numero = numeros.get(0).obtientNumeroNormalise();
                if (numeros.get(0).obtientRepetition() == null) {
                    repetition = "";
                } else {
                    repetition = Character.toString(numeros.get(0).obtientRepetitionNormalise());
                }
            } else {
                numero = "";
                repetition = "";
            }

            res[0] = "6";
            res[1] = Integer.toString(voi_id_suivantes.size());
            for (int i = 0; i < voi_id_suivantes.size(); i++) {
                res[2 + blockSize * i] = voi_id_suivantes.get(i);
                res[3 + blockSize * i] = Algos.unsplit(numero, repetition, voies_suivantes.get(i));
                res[4 + blockSize * i] = Algos.unsplit(codepostal_suivants.get(i), commune_suivants.get(i));
                res[5 + blockSize * i] = codeinsee_suivants.get(i);
                res[6 + blockSize * i] = voi_nom_origine_suivantes.get(i); // HM
                res[7 + blockSize * i] = commune_nom_origine_suivantes.get(i); // HM
                if (codeSovAc3Origine != null) {
                    res[8 + blockSize * i] = ligne7; // HM +2
                    res[9 + blockSize * i] = codeSovAc3Origine; // HM +2
                }
                Date dt0 = new Date(t0_suivants.get(i).getTime());
                Date dt1 = new Date(t1_suivants.get(i).getTime());
                // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//                resCom[6+6*i]=sdformat.format(dt0Com);
//                resCom[7+6*i]=sdformat.format(dt1Com);
                res[blockSize + blockSize * i] = DateUtils.formatDateToString(dt0, sdformat);
                res[blockSize + 1 + blockSize * i] = DateUtils.formatDateToString(dt1, sdformat);
            }

            jdonrefParams.getGestionLog().logRevalidation(application, true);
            return res;
        }
    }

    public List<String[]> revalide(int application, int[] services, String[] lignes, String strdateValidation, String strdate, Connection connection) throws
            SQLException {
        final List<String[]> listRet = new ArrayList<String[]>();

        // VERIFICATION DES PARAMETRES
        if (lignes.length < 6) {
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "5", "L'adresse ne comporte pas ses 6 lignes"});
            return listRet;
        }

        String ligne4 = lignes[3];
        String ligne6 = lignes[5];
        String ligne7 = null;
        String codeSovAc3Origine = null;

        Date dateValidation = null;
        try {
            // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            // dateValidation = sdformat.parse(strdateValidation);
            dateValidation = DateUtils.parseStringToDate(strdateValidation, sdformat);

        } catch (java.text.ParseException pe) {
            jdonrefParams.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "5", "La date de validation est mal formée."});
            return listRet;
        }
        Date date;
        if (strdate != null) {
            try {
                // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                // date=sdformat.parse(strdate);
                date = DateUtils.parseStringToDate(strdate, sdformat);
            } catch (java.text.ParseException pe) {
                jdonrefParams.getGestionLog().logRevalidation(application, false);
                listRet.add(new String[]{"0", "5", "La date de revalidation est mal formée."});
                return listRet;
            }
        } else {
            date = Calendar.getInstance().getTime();
        }

        Timestamp tsvalidation = new Timestamp(dateValidation.getTime());
        Timestamp tsrevalidation = new Timestamp(date.getTime());

        for (Integer serviceCle : services) {
            
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(serviceCle).getId();
            
            if (id == SERVICE_POINT_ADRESSE) {
                break; // NON IMPLEMENTE
            } else if (id == SERVICE_ADRESSE) {
                listRet.add(this.revalide(application, lignes, strdateValidation, strdate, connection));
            } else if (id == SERVICE_PAYS) {
                if (lignes.length > 6) {
                    ligne7 = lignes[6];
                    if (ligne7 != null) {
                        // Verification du pays
                        List<PayPaysBean> pays = RecherchesDao.foundPaysByNameAtDate(connection, ligne7, dateValidation, 1);
                        if ((pays == null) || (pays.size() != 1)) {
                            listRet.add(new String[]{"0", "8", "Le pays de l'adresse spécifiée n'a pas été trouvé."});
                        }
                        codeSovAc3Origine = pays.get(0).getSovAc3();
                        // Si la version actuelle du pays n'est pas le pays par defaut, on ne revalide que le pays
                        pays = RecherchesDao.foundPaysByCodeAtDate(connection, codeSovAc3Origine, new Date(), 1);
                        if ((pays != null) && (pays.size() == 1)) {
                            String actualPaysName = pays.get(0).getNomFr();
                            // Si le pays trouve n'est pas le pays par defaut ou s'il n'y a ni ligne 4 ni ligne 6
                            // on ne revalide QUE le pays
                            if (((ligne4.length() == 0) && (ligne6.length() == 0)) || (!jdonrefParams.obtientPaysParDefaut().equals(actualPaysName))) {
                                listRet.add(revalidePays(codeSovAc3Origine, date, connection));
                            }
                        }
                    }
                }
            } else {
                // Vérifie si un département est spécifié.
                RefCle codedepartement = gestionMots.trouveCodeDepartement(lignes[5]);
                // WA gestion Dpts les codes dpt ne font plus forcement 2 cars
                // if(codedepartement.obtientMot().length() != 2)
                if (codedepartement.obtientMot().length() < 2) {
                    jdonrefParams.getGestionLog().logRevalidation(application, false);
                    listRet.add(new String[]{"0", "8", "Le département de l'adresse spécifiée n'a pas été trouvé."});
                    break; // PASSE AU SERVICE SUIVANT
                }
                //
                // Cherche la commune dans l'adresse.
                //
                RefCle codepostal = gestionMots.trouveCodePostal(ligne6);
                RefCle commune = gestionMots.trouveNomVille(ligne6, codepostal);

                if (codepostal.obtientMot().length() == 0 || commune.obtientMot().length() == 0) {
                    jdonrefParams.getGestionLog().logRevalidation(application, false);
                    listRet.add(new String[]{"0", "8", "Il manque la commune ou le code postal à l'adresse."});
                    break; // PASSE AU SERVICE SUIVANT
                }

                StringBuilder sbCom = new StringBuilder();
                sbCom.append("SELECT com_communes.com_code_insee FROM com_communes,cdp_codes_postaux ");
                sbCom.append("WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND com_nom=? AND cdp_code_postal=? AND ");
                sbCom.append("com_communes.t0<=? AND com_communes.t1>=? AND ");
                sbCom.append("cdp_codes_postaux.t0<=? AND cdp_codes_postaux.t1>=? ");
                sbCom.append("LIMIT 1");
                PreparedStatement psChercheCommune = connection.prepareStatement(sbCom.toString());
                psChercheCommune.setString(1, commune.obtientMot());
                psChercheCommune.setString(2, codepostal.obtientMot());
                psChercheCommune.setTimestamp(3, tsvalidation);
                psChercheCommune.setTimestamp(4, tsvalidation);
                psChercheCommune.setTimestamp(5, tsvalidation);
                psChercheCommune.setTimestamp(6, tsvalidation);
                ResultSet rsChercheCommune = psChercheCommune.executeQuery();

                if (!rsChercheCommune.next()) {
                    rsChercheCommune.close();
                    psChercheCommune.close();
                    jdonrefParams.getGestionLog().logRevalidation(application, false);
                    listRet.add(new String[]{"0", "8", "La commune spécifiée dans l'adresse n'a pas été trouvée."});
                    break; // PASSE AU SERVICE SUIVANT
                }

                String code_insee = rsChercheCommune.getString(1);
                rsChercheCommune.close();
                psChercheCommune.close();

                if (id == SERVICE_DEPARTEMENT) {
                    break;
                } else if (id == SERVICE_COMMUNE) {
                    // Cherche les éventuelles mises à jour de cette commune  
                    sbCom.setLength(0);
                    sbCom.append("SELECT com_nom,cdp_code_postal,com_communes.t0,com_communes.t1,cdp_codes_postaux.t0,cdp_codes_postaux.t1 FROM com_communes,cdp_codes_postaux ");
                    sbCom.append("WHERE com_communes.com_code_insee=cdp_codes_postaux.com_code_insee AND ");
                    sbCom.append("com_communes.com_code_insee=? AND com_communes.t0<=? AND com_communes.t1>=? AND cdp_codes_postaux.t0<=? AND cdp_codes_postaux.t1>=?");
                    PreparedStatement psChercheCommuneSuivantes = connection.prepareStatement(sbCom.toString());

                    psChercheCommuneSuivantes.setString(1, code_insee);
                    psChercheCommuneSuivantes.setTimestamp(2, tsrevalidation);
                    psChercheCommuneSuivantes.setTimestamp(3, tsvalidation);
                    psChercheCommuneSuivantes.setTimestamp(4, tsrevalidation);
                    psChercheCommuneSuivantes.setTimestamp(5, tsvalidation);

                    ArrayList<String> nom = new ArrayList<String>();
                    ArrayList<String> code_postal = new ArrayList<String>();
                    ArrayList<Timestamp> t0sCom = new ArrayList<Timestamp>();
                    ArrayList<Timestamp> t1sCom = new ArrayList<Timestamp>();

                    ResultSet rsChercheCommuneSuivantes = psChercheCommuneSuivantes.executeQuery();
                    while (rsChercheCommuneSuivantes.next()) {
                        String nom_suivant = rsChercheCommuneSuivantes.getString(1);
                        String code_postal_suivant = rsChercheCommuneSuivantes.getString(2);

                        if (nom_suivant.compareTo(commune.obtientMot()) != 0 ||
                                code_postal_suivant.compareTo(codepostal.obtientMot()) != 0) {
                            nom.add(nom_suivant);
                            code_postal.add(code_postal_suivant);

                            Timestamp commune_t0 = rsChercheCommuneSuivantes.getTimestamp(3);
                            Timestamp commune_t1 = rsChercheCommuneSuivantes.getTimestamp(4);
                            Timestamp cp_t0 = rsChercheCommuneSuivantes.getTimestamp(5);
                            Timestamp cp_t1 = rsChercheCommuneSuivantes.getTimestamp(6);

                            if (commune_t0.getTime() < cp_t0.getTime()) {
                                t0sCom.add(cp_t0);
                            } else {
                                t0sCom.add(commune_t0);
                            }

                            if (commune_t1.getTime() < cp_t1.getTime()) {
                                t1sCom.add(commune_t1);
                            } else {
                                t1sCom.add(cp_t1);
                            }
                        }
                    }

                    rsChercheCommuneSuivantes.close();
                    psChercheCommuneSuivantes.close();

                    // crée le résultat.
                    int blockSizeCom = (codeSovAc3Origine != null) ? 8 : 6;
                    String[] resCom = new String[2 + blockSizeCom * nom.size()];

                    resCom[0] = "6";
                    resCom[1] = Integer.toString(nom.size());
                    for (int i = 0; i < nom.size(); i++) {
                        resCom[2 + blockSizeCom * i] = "";
                        resCom[3 + blockSizeCom * i] = "";
                        resCom[4 + blockSizeCom * i] = Algos.unsplit(code_postal.get(i), nom.get(i));
                        resCom[5 + blockSizeCom * i] = code_insee;
                        if (codeSovAc3Origine != null) {
                            resCom[6 + blockSizeCom * i] = ligne7;
                            resCom[7 + blockSizeCom * i] = codeSovAc3Origine;
                        }
                        Date dt0Com = new Date(t0sCom.get(i).getTime());
                        Date dt1Com = new Date(t1sCom.get(i).getTime());
                        // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        // resCom[6+6*i] = sdformat.format(dt0Com);
                        // resCom[7+6*i] = sdformat.format(dt1Com);
                        resCom[blockSizeCom + blockSizeCom * i] = DateUtils.formatDateToString(dt0Com, sdformat);
                        resCom[blockSizeCom + 1 + blockSizeCom * i] = DateUtils.formatDateToString(dt1Com, sdformat);
                    }

                    jdonrefParams.getGestionLog().logRevalidation(application, true);
                    listRet.add(resCom);

                } else {
                    // Cherche la voie décrite dans l'adresse.
                    //
                    ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(ligne4);
                    RefCle typedevoie = gestionMots.trouveTypeVoie(ligne4, numeros);
                    RefCle article,
                            libelle;
                    if (typedevoie.obtientMot().length() > 0) {
                        article = gestionMots.trouveArticleVoie(ligne4, typedevoie);
                        libelle = gestionMots.trouveLibelleVoie(ligne4, article);
                    } else {
                        article = new RefCle("", 0);
                        libelle = gestionMots.trouveLibelleVoie(ligne4, numeros);
                    }
                    String nomVoie = Algos.unsplit(typedevoie.obtientMot(), article.obtientMot(), libelle.obtientMot());

                    StringBuilder sbVoie = new StringBuilder();
                    sbVoie.append("SELECT voi_id FROM \"");
                    // WA 09/2011 utilisation de GestionTables.getXXTableName
                    // sbCom.append("voi_voies_").append(codedepartement.obtientMot());
                    sbVoie.append(GestionTables.getVoiVoiesTableName(codedepartement.obtientMot()));
                    sbVoie.append("\" where voi_nom=? and com_code_insee=? and t0<=? and t1>=? LIMIT 1");
                    PreparedStatement psChercheVoie = connection.prepareStatement(sbVoie.toString());
                    psChercheVoie.setString(1, nomVoie);
                    psChercheVoie.setString(2, code_insee);
                    psChercheVoie.setTimestamp(3, tsvalidation);
                    psChercheVoie.setTimestamp(4, tsvalidation);
                    ResultSet rsVoie = psChercheVoie.executeQuery();

                    if (!rsVoie.next()) {
                        rsVoie.close();
                        psChercheVoie.close();
                        jdonrefParams.getGestionLog().logRevalidation(application, false);
                        listRet.add(new String[]{"0", "8", "La voie spécifiée n'a pas été trouvée."});
                    }

                    String voi_id = rsVoie.getString(1);
                    rsVoie.close();
                    psChercheVoie.close();

                    // Cherche les éventuelles mises à jour de cette voie (hors géométrie et numéros d'adresse)
                    sbVoie.setLength(0);
                    // sbCom.append("SELECT voi_id_suivant,t0Voie FROM \"vhi_voies_historisees_");
                    // sbCom.append(codedepartement.obtientMot());
                    sbVoie.append("SELECT voi_id_suivant,t0 FROM ");
                    sbVoie.append(GestionTables.getVhiVoiesHistoriseeTableName(codedepartement.obtientMot()));
                    sbVoie.append(" WHERE voi_id_precedent=? AND t0>=?");
                    PreparedStatement psChercheVoieSuivantes = connection.prepareStatement(sbVoie.toString());
                    psChercheVoieSuivantes.setString(1, voi_id);
                    psChercheVoieSuivantes.setTimestamp(2, tsvalidation);
                    ResultSet rsChercheVoieSuivantes = psChercheVoieSuivantes.executeQuery();

                    // Extrait les identifiants de voies
                    ArrayList<String> voi_ids = new ArrayList<String>();
                    ArrayList<Timestamp> t0sVoie = new ArrayList<Timestamp>();

                    voi_ids.add(voi_id); // la recherche porte aussi sur la voie utilisée actuellement.
                    t0sVoie.add(tsvalidation);

                    while (rsChercheVoieSuivantes.next()) {
                        voi_ids.add(rsChercheVoieSuivantes.getString(1));
                        t0sVoie.add(rsChercheVoieSuivantes.getTimestamp(2));
                    }

                    rsChercheVoieSuivantes.close();
                    psChercheVoieSuivantes.close();

                    // Prépare la requête permettant de chercher les propriétés des voies trouvées.
                    sbVoie.setLength(0);
                    sbVoie.append("SELECT voies.voi_nom,voies.com_code_insee,voies.t0,voies.t1,communes.com_nom,voies.cdp_code_postal," +
                            "communes.t0,communes.t1,voies.voi_nom_origine,communes.com_nom_origine FROM \""); // HM
                    // WA 09/2011 utilisation de GestionTables.getXXTableName
                    // sbCom.append("voi_voies_").append(codedepartement.obtientMot());
                    sbVoie.append(GestionTables.getVoiVoiesTableName(codedepartement.obtientMot()));
                    sbVoie.append("\" as voies, com_communes as communes ");
                    sbVoie.append("WHERE voies.voi_id=? AND voies.t1>=? AND voies.com_code_insee=communes.com_code_insee AND ");
                    sbVoie.append("NOT (voies.t1<communes.t0 OR communes.t1<voies.t0)");
                    PreparedStatement psChercheVoieSuivante = connection.prepareStatement(sbVoie.toString());

                    // Cherche les informations concernant ces voies,
                    // et ne conserve que celles qui sont différentes de la voie actuelle.
                    ArrayList<String> voi_id_suivantes = new ArrayList<String>();
                    ArrayList<String> voi_nom_origine_suivantes = new ArrayList<String>(); // HM
                    ArrayList<String> voies_suivantes = new ArrayList<String>();
                    ArrayList<String> codeinsee_suivants = new ArrayList<String>();
                    ArrayList<String> commune_suivants = new ArrayList<String>();
                    ArrayList<String> commune_nom_origine_suivantes = new ArrayList<String>(); // HM
                    ArrayList<String> codepostal_suivants = new ArrayList<String>();
                    ArrayList<Timestamp> t0_suivants = new ArrayList<Timestamp>();
                    ArrayList<Timestamp> t1_suivants = new ArrayList<Timestamp>();
                    for (int i = 0; i < voi_ids.size(); i++) // Pour chaque identifiant trouvé, cherche les propriétés
                    {
                        String voi_id_suivante = voi_ids.get(i);
                        Timestamp t0_suivant = t0sVoie.get(i);

                        // Cherche les propriétés pour la voie en cour,
                        psChercheVoieSuivante.setString(1, voi_id_suivante);
                        psChercheVoieSuivante.setTimestamp(2, tsrevalidation);
                        ResultSet rsChercheVoieSuivante = psChercheVoieSuivante.executeQuery();
                        while (rsChercheVoieSuivante.next()) {
                            String nom_suivant = rsChercheVoieSuivante.getString(1);
                            String code_insee_suivant = rsChercheVoieSuivante.getString(2);
                            String nomcommune_suivant = rsChercheVoieSuivante.getString(5);
                            String nomcodepostal_suivant = rsChercheVoieSuivante.getString(6);
                            Timestamp voiet0 = rsChercheVoieSuivante.getTimestamp(3);
                            Timestamp voiet1 = rsChercheVoieSuivante.getTimestamp(4);
                            Timestamp comt0 = rsChercheVoieSuivante.getTimestamp(7);
                            Timestamp comt1 = rsChercheVoieSuivante.getTimestamp(8);
                            String voieNomOrigine = rsChercheVoieSuivante.getString(9); // HM
                            String communeNomOrigine = rsChercheVoieSuivante.getString(10); // HM
                            Timestamp t0Voie;
                            if (voiet0.getTime() < comt0.getTime()) {
                                t0Voie = comt0;
                            } else {
                                t0Voie = voiet0;
                            }
                            // Prend en compte si nécessaire la mise à jour.
                            if (t0_suivant != t0Voie) // si l'intervalle de validité n'est pas le même.
                            /*if (nom_suivant.compareTo(nomVoie)!=0 ||          // (méthode précédente : si une propriété a changé
                            code_insee_suivant.compareTo(code_insee)!=0 ||  // cette méthode a été écartée car elle ne permet
                            nomcommune_suivant.compareTo(commune.obtientMot())!=0 || // pas de revalidation lorsque la géométrie
                            nomcodepostal_suivant.compareTo(codepostal.obtientMot())!=0)*/ // change)
                            {
                                // Recalcule l'intervalle de validité à partir de celui de la voie et celui de la commune.

                                Timestamp t1Voie;
                                if (voiet1.getTime() < comt1.getTime()) {
                                    t1Voie = voiet1;
                                } else {
                                    t1Voie = comt1;
                                }
                                voies_suivantes.add(nom_suivant);
                                voi_nom_origine_suivantes.add(voieNomOrigine); // HM
                                codeinsee_suivants.add(code_insee_suivant);
                                t0_suivants.add(t0Voie);
                                t1_suivants.add(t1Voie);
                                commune_suivants.add(nomcommune_suivant);
                                commune_nom_origine_suivantes.add(nomcodepostal_suivant + "\u0020" + communeNomOrigine); // HM
                                codepostal_suivants.add(nomcodepostal_suivant);
                                voi_id_suivantes.add(voi_id_suivante);
                            }
                        }
                        rsChercheVoieSuivante.close();
                    }
                    psChercheVoieSuivante.close();

                    int blockSizeVoie = (codeSovAc3Origine != null) ? 10 : 8; // HM +2 pour les noms desab de la voie et de la commune
                    String[] resVoie = new String[2 + voi_id_suivantes.size() * blockSizeVoie];

                    String numero = null;
                    String repetition = null;
                    if (numeros.size() > 0) {
                        numero = numeros.get(0).obtientNumeroNormalise();
                        if (numeros.get(0).obtientRepetition() == null) {
                            repetition = "";
                        } else {
                            repetition = Character.toString(numeros.get(0).obtientRepetitionNormalise());
                        }
                    } else {
                        numero = "";
                        repetition = "";
                    }

                    resVoie[0] = "6";
                    resVoie[1] = Integer.toString(voi_id_suivantes.size());
                    for (int i = 0; i < voi_id_suivantes.size(); i++) {
                        resVoie[2 + blockSizeVoie * i] = voi_id_suivantes.get(i);
                        resVoie[3 + blockSizeVoie * i] = Algos.unsplit(numero, repetition, voies_suivantes.get(i));
                        resVoie[4 + blockSizeVoie * i] = Algos.unsplit(codepostal_suivants.get(i), commune_suivants.get(i));
                        resVoie[5 + blockSizeVoie * i] = codeinsee_suivants.get(i);
                        resVoie[6 + blockSizeVoie * i] = voi_nom_origine_suivantes.get(i); // HM
                        resVoie[7 + blockSizeVoie * i] = commune_nom_origine_suivantes.get(i); // HM
                        if (codeSovAc3Origine != null) {
                            resVoie[8 + blockSizeVoie * i] = ligne7; // HM +2
                            resVoie[9 + blockSizeVoie * i] = codeSovAc3Origine; // HM +2
                        }
                        Date dt0 = new Date(t0_suivants.get(i).getTime());
                        Date dt1 = new Date(t1_suivants.get(i).getTime());
                        // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        // resCom[6+6*i]=sdformat.format(dt0Com);
                        // resCom[7+6*i]=sdformat.format(dt1Com);
                        resVoie[blockSizeVoie + blockSizeVoie * i] = DateUtils.formatDateToString(dt0, sdformat);
                        resVoie[blockSizeVoie + 1 + blockSizeVoie * i] = DateUtils.formatDateToString(dt1, sdformat);
                    }

                    jdonrefParams.getGestionLog().logRevalidation(application, true);
                    listRet.add(resVoie);
                }
            }
        }

        return listRet;
    }

    /**
     * Obtient les nouvelles versions d'un pays donne a la date de revalidation specifiee
     * Le resultat est formate selon le format de revalide (avec gestion du pays) :
     * - 6
     * - nb resultats
     * - id_voie
     * - ligne4
     * - ligne6
     * - code insee
     * - ligne7
     * - code sov ac3
     * @param origineCodePays
     * @param dateRevalid
     * @param connection
     * @return
     * @throws SQLException
     */
    public String[] revalidePays(String origineCodePays, Date dateRevalid, Connection connection) throws SQLException {
        List<String> res = new ArrayList<String>();
        // On cherche la version du pays a la nouvelle date (dateRevalid)
        List<PayPaysBean> newPays = RecherchesDao.foundPaysByCodeAtDate(connection, origineCodePays, dateRevalid, jdonrefParams.obtientNombreDePaysParDefaut());
        if ((newPays != null) && (newPays.size() > 0)) {
            res.add("6");
            res.add(Integer.toString(newPays.size()));
            for (PayPaysBean pay : newPays) {
                // les id_voie, ligne4, ligne6 et code_insee ne sont pas revalides -> laisses vides
                res.add("");    // id_voie
                res.add("");    // ligne4
                res.add("");    // ligne6
                res.add("");    // code insee
                res.add(pay.getNomFr());
                res.add(pay.getSovAc3());
                res.add(DateUtils.formatDateToString(pay.getT0(), sdformat));
                res.add(DateUtils.formatDateToString(pay.getT1(), sdformat));
            }
        }
        return res.toArray(new String[res.size()]);
    }

    /**
     * Normalise la colonne source de type nom de voie dans la colonne destination.<br>
     * @param nomTable Le nom de la table dont la colonne est à normaliser
     * @param columnSource la colonne à normaliser.
     * @param columnDestination le résultat de la normalisation.
     * @param columnDepartement la colonne qui contient le code département, le code postal, ou le code insee
     * @param flags une combinaison des flags suivants:
     * <ul>
     * <li>1 pour la première phase de normalisation</li>
     * <li>4 pour la seconde phase de normalisation</li>
     * <li>8 pour activer la réduction à 38 caractères dans la seconde phase de normalisation</li>
     * <li>16 pour retourner l'équivalent phonétique</li>
     * <li>32 pour activer la désabbréviation</li>
     * <li>64 pour retourner l'équivalent sans articles</li>
     * </ul>
     * Si le flag 2 est activé, il ne sera pas pris en compte.
     * @param ligne permet d'effectuer une normalisation propre à la ligne spécifiée (pris en compte si flags==4)
     * @param connection La connection donnant accès à cette table
     * @param connectionReferentiel le référentiel utilisé pour la normalisation
     * @param tsdate la date à laquelle le référentiel est normalisé.
     */
    public void normalise(Processus p, String nomTable, String columnSource, String columnDestination, String columnDepartement, int flags,
            int ligne, Connection connection, Connection connectionReferentiel) throws SQLException, Exception {
        int index = 0;

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Création des index");
        p.state = new String[]{
            en_cours, "CREATION DES INDEX"
        };

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "NORMALISATION");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "table  : " + nomTable);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "source : " + columnSource);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "destination : " + columnDestination);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "flags : " + flags);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "ligne : " + ligne);
        p.resultat.add("NORMALISE");
        p.resultat.add("table  : " + nomTable);
        p.resultat.add("source : " + columnSource);
        p.resultat.add("destination : " + columnDestination);
        p.resultat.add("departement : " + columnDepartement);
        p.resultat.add("flags : " + flags);
        p.resultat.add("ligne : " + ligne);

        // Crée l'index nécessaire
        Index idx_id = new Index();
        idx_id.setNom(nomTable + "_" + columnSource);
        idx_id.ajouteColonne(columnSource);

        boolean idx_id_cree = false;

        try {
            idx_id_cree = GestionTables.ajouteIndex(nomTable, idx_id, connection);
        } catch (GestionReferentielException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("L'index ");
            sb.append(nomTable);
            sb.append("_");
            sb.append(columnSource);
            sb.append(" doit être supprimé de la table ");
            sb.append(nomTable);
            sb.append(" si ses colonnes ne sont pas correctes (");
            sb.append(columnSource);
            sb.append(").");
            String message = sb.toString();
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, message);
            p.resultat.add(message);
            Logger.getLogger(gestion_referentiel).log(Level.INFO, message, ex);
        }

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARATION DES REQUETES");
        p.state[1] = "PREPARATION DES REQUETES";

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(DISTINCT \"");
        sb.append(columnSource);
        if (columnDepartement != null) {
            sb.append("\"||\"");
            sb.append(columnDepartement);
        }
        sb.append("\") FROM \"");
        sb.append(nomTable);
        sb.append("\"");
        Statement stCompte = connection.createStatement();
        ResultSet rsCompte = stCompte.executeQuery(sb.toString());
        int compte = 0;
        if (rsCompte.next()) {
            compte = rsCompte.getInt(1);
        }
        rsCompte.close();
        stCompte.close();

        sb.setLength(0);
        sb.append("SELECT DISTINCT \"");
        sb.append(columnSource);
        if (columnDepartement != null) {
            sb.append("\",\"");
            sb.append(columnDepartement);
        }
        sb.append("\" FROM \"");
        sb.append(nomTable);
        sb.append("\"");
        String rqChercheVoie = sb.toString();
        Statement stChercheVoie = connection.createStatement();

        sb.setLength(0);
        sb.append("UPDATE \"");
        sb.append(nomTable);
        sb.append("\" SET \"");
        sb.append(columnDestination);
        sb.append("\"=? WHERE \"");
        sb.append(columnSource);
        sb.append("\"=?");
        String rqUpdateVoie = sb.toString();
        PreparedStatement psUpdateVoie = connection.prepareStatement(rqUpdateVoie);

        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(true);

        if (p.stop) {
            stChercheVoie.close();
            psUpdateVoie.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        try {
            boolean abbrevie = (flags & 8) != 0;
            boolean desabbrevie = (flags & 32) != 0;

            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "RECHERCHE DES VOIES");
            p.state = new String[]{
                en_cours, "RECHERCHE DES VOIES"
            };
            ResultSet rsChercheVoie = stChercheVoie.executeQuery(rqChercheVoie);

            p.state = new String[]{
                en_cours, "TRAITEMENT", "LIGNES TRAITEES", "0", "SUR " + compte
            };
            String[] array_dpt = new String[1];
            while (!p.stop && rsChercheVoie.next()) {
                String nomval = null;
                try {
                    String nomdepart = nomval = rsChercheVoie.getString(1);
                    String dpt = null;
                    if (columnDepartement != null) {
                        dpt = this.gestionMots.trouveCodeDepartement(rsChercheVoie.getString(2)).obtientMot();
                    }
                    if (dpt.length() == 0) {
                        dpt = null;
                    } else {
                        array_dpt[0] = dpt;
                    }

                    if ((flags & 1) != 0) {
                        nomval = Algos.normalise_1(nomval); // gère aussi le cas où nomval est null.
                    }
                    if ((flags & 4) != 0) {
                        if (ligne == 4) {
                            if (dpt != null) {
                                nomval = gestionMots.normalise_2_ligne4(nomval, null, abbrevie, desabbrevie, array_dpt,
                                        connectionReferentiel);
                            } else {
                                nomval = gestionMots.normalise_2_ligne4(nomval, null, abbrevie, desabbrevie, null, connectionReferentiel);
                            }
                        } else {
                            nomval = gestionMots.normalise_2_ligneX(nomval, ligne, abbrevie, desabbrevie);
                        }
                    }

                    if ((flags & 64) != 0) {
                        nomval = Algos.sansarticles(nomval);
                    }

                    if (abbrevie && nomval.length() > 32) {
                        StringBuilder sb_error = new StringBuilder();
                        sb_error.append("Nom '");
                        sb_error.append(nomdepart);
                        sb_error.append("' normalisé en '");
                        sb_error.append(nomval);
                        sb_error.append("' dépasse 32 caractères.");
                        String error = sb.toString();
                        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, error);
                        p.resultat.add(error);
                    } else {
                        psUpdateVoie.setString(1, nomval);
                        psUpdateVoie.setString(2, nomdepart);
                        psUpdateVoie.execute();
                    }
                } catch (Exception e) {
                    jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Problème avec la voie " + nomval);
                    p.resultat.add("Problème avec la voie " + nomval);
                    throw (e);
                }

                index++;
                if (index % 200 == 0) {
                    p.state[3] = Integer.toString(index); // Mise à jour de ligne traitée.
                }
            }

            if (p.stop) {
                rsChercheVoie.close();
                stChercheVoie.close();
                psUpdateVoie.close();
                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
                return;
            }

            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "NORMALISATION");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "lignes traitées: " + index);
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "sur " + compte);
            p.resultat.add("NORMALISATION");
            p.resultat.add("lignes traitées: " + index);
            p.resultat.add("sur " + compte);

            p.state[1] = "FERMETURE DES CONNECTIONS";
            rsChercheVoie.close();
            stChercheVoie.close();
            psUpdateVoie.close();
        } catch (SQLException sqle) {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, sqle.getMessage());
            p.resultat.add(sqle.getMessage());
            throw (sqle);
        } finally {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "SUPPRESSION DES INDEX");
            p.state = new String[]{
                en_cours, "SUPPRESSION DES INDEX"
            };
            if (idx_id_cree) {
                GestionTables.supprimeIndex(idx_id, connection);
            }

            connection.setAutoCommit(autocommit);
        }
    }

    /**
     * Retourne le géocodage de l'adresse spécifiée dans la voie spécifiée du département spécifié.
     * Selon les informations fournies par le référentiel, la précision du géocodage peut être:
     * <ul>
     * <li>Au centroide de la voie si le numéro est 0.</li>
     * <li>A la plaque, si le point adresse a été trouvé.</li>
     * <li>Sinon, à l'interpolation de la plaque, si des points adresse proches sont trouvés et que le auTroncon contenant le numéro a un adressage classique ou métrique</li>
     * <li>Sinon, à l'interpolation métrique sur le auTroncon, si le auTroncon concenant le numéro a un adressage classique ou métrique</li>
     * <li>Sinon, à l'une des extrémité du auTroncon, si le auTroncon contenant le numéro a été trouvé, et qu'il s'agit d'une des bornes.</li>
     * <li>Sinon, au centroïde du auTroncon, si le auTroncon contenant le numéro a été trouvé.</li>
     * <li>Sinon, au centroïde de la voie.</li>
     * </ul>
     * Si la méthode réussi, le retour est de la forme:
     * <ul>
     * <li>Code de la méthode: 11</li>
     * <li>Nombre de résultats: 1</li>
     * <li>Type de géocodage (de moins en moins précis):
     * <ul>
     * <li>1 pour à la plaque,</li>
     * <li>2 pour à l'interpolation de la plaque,</li>
     * <li>3 pour à l'interpolation métrique du auTroncon ou les bornes du auTroncon (qualité équivalente),</li>
     * <li>4 au centroide du auTroncon,</li>
     * <li>5 pour le centroide de la voie.</li>
     * <li>6 à l'arrondissement ou la commune.</li>
     * <li>7 au département.</li>
     * <li>8 au pays.</li>
     * </ul>
     * <li>X précision cm</li>
     * <li>Y précision cm</li>
     * <li>Date de validation</li>
     * <li>Referentiel</li>
     * <li>Projection</li>
     * </ul>
     * En cas d'erreur, le retour est de la forme:
     * <ul>
     * <li>0</li>
     * <li>Code d'erreur</li>
     * <li>Message d'erreur</li>
     * </ul>
     */
    public String[] geocode(int application, String voi_id, String ligne4, String code_insee, String dateValidation, int projection, Connection connection)
            throws SQLException {
        int numero = 0;
        char repetition = 0;
        String paysSovAc3 = null;

        if (voi_id != null) {
            ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(ligne4);

            if (numeros.size() > 0) {
                numero = Integer.parseInt(numeros.get(0).obtientNumero()); // Il n'est pas nécessaire d'utiliser obtientNumeroNormalise, parseInt s'en charge.

                repetition = numeros.get(0).obtientRepetitionNormalise();
            }
        } else if (code_insee == null) {
            // Alors geocodage au pays
            paysSovAc3 = ligne4; // ????????????????????????????
        }

        Date date;
        try {
            // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//            date=sdformat.parse();
            date = DateUtils.parseStringToDate(dateValidation, sdformat);
        } catch (NumberFormatException nfe) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            return new String[]{
                "0", "5", "La date est mal formatée (" + dateValidation + ")."
            };
        } catch (java.text.ParseException ex) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            return new String[]{
                "0", "5", "La date est mal formatée (" + dateValidation + ")."
            };
        }

        String[] res = null;
        try {
            final GestionGeocodage gg = new GestionGeocodage(jdonrefParams);
            res = gg.geocodeAdresse(application, voi_id, numero, repetition, code_insee, paysSovAc3, projection, jdonrefParams.obtientProjectionPaysParDefaut(), date, connection);
        } catch (GestionReferentielException ex) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            return new String[]{
                "0", "11", ex.getMessage()
            };
        }
        return res;
    }

    public List<String[]> geocode(int application, int[] services, String voi_id, String ligne4, String code_insee, String pays_id, String dateValidation, int projection, Connection connection)
            throws SQLException {
        final List<String[]> listRet = new ArrayList<String[]>();
        int numero = 0;
        char repetition = 0;
        String paysSovAc3 = pays_id;

        if (voi_id != null) {
            ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(ligne4);

            if (numeros.size() > 0) {
                numero = Integer.parseInt(numeros.get(0).obtientNumero()); // Il n'est pas nécessaire d'utiliser obtientNumeroNormalise, parseInt s'en charge.

                repetition = numeros.get(0).obtientRepetitionNormalise();
            }
        }

        Date date = null;
        try {
            date = DateUtils.parseStringToDate(dateValidation, sdformat);
        } catch (NumberFormatException nfe) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{
                "0", "5", "La date est mal formatée (" + dateValidation + ")."
            });
        } catch (java.text.ParseException ex) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{
                "0", "5", "La date est mal formatée (" + dateValidation + ")."
            });
        }

        try {
            final GestionGeocodage gg = new GestionGeocodage(jdonrefParams);
            listRet.addAll(gg.geocodeAdresse(application, services, voi_id, numero, repetition, code_insee, paysSovAc3, projection, jdonrefParams.obtientProjectionPaysParDefaut(), date, connection));
        } catch (GestionReferentielException ex) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{
                "0", "11", ex.getMessage()
            });
        }

        return listRet;
    }

    public List<String[]> geocode(int application, int[] services, String voi_id, String ligne4, String code_insee, String pays_id, String dateValidation, double distance, int projection, Connection connection)
            throws SQLException {
        final List<String[]> listRet = new ArrayList<String[]>();
        int numero = 0;
        char repetition = 0;
        String paysSovAc3 = pays_id;

        if (voi_id != null) {
            ArrayList<RefNumero> numeros = gestionMots.trouveNumeros(ligne4);

            if (numeros.size() > 0) {
                numero = Integer.parseInt(numeros.get(0).obtientNumero()); // Il n'est pas nécessaire d'utiliser obtientNumeroNormalise, parseInt s'en charge.

                repetition = numeros.get(0).obtientRepetitionNormalise();
            }
        }

        Date date = null;
        try {
            date = DateUtils.parseStringToDate(dateValidation, sdformat);
        } catch (NumberFormatException nfe) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{
                "0", "5", "La date est mal formatée (" + dateValidation + ")."
            });
        } catch (java.text.ParseException ex) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{
                "0", "5", "La date est mal formatée (" + dateValidation + ")."
            });
        }

        try {
            final GestionGeocodage gg = new GestionGeocodage(jdonrefParams);
            if (distance > 0) {
                listRet.addAll(gg.geocodeAdresse(application, services, voi_id, numero, distance, repetition, code_insee, paysSovAc3, projection, jdonrefParams.obtientProjectionPaysParDefaut(), date, connection));
            } else {
                listRet.addAll(gg.geocodeAdresse(application, services, voi_id, numero, repetition, code_insee, paysSovAc3, projection, jdonrefParams.obtientProjectionPaysParDefaut(), date, connection));
            }
        } catch (GestionReferentielException ex) {
            jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{
                "0", "11", ex.getMessage()
            });
        }

        return listRet;
    }

    /**
     * Calcule l'équivalent phonétique d'une colonne d'une table.
     * @param columnSource
     * @param colonneSource
     * @param table
     * @param connection
     */
    public void phonetise(Processus p, String colonneSource, String colonneDestination, String table, Connection connection) throws
            SQLException {
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PHONETISE");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "table : " + table);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "source :" + colonneSource);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "destination :" + colonneDestination);

        p.resultat.add("PHONETISE");
        p.resultat.add("table : " + table);
        p.resultat.add("source :" + colonneSource);
        p.resultat.add("destination :" + colonneDestination);

        p.state = new String[]{
            en_cours, "CREE L'INDEX"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREE L'INDEX");
        Index idx = new Index();
        idx.setNom(table + "_" + colonneSource);
        idx.ajouteColonne(colonneSource);
        boolean idx_cree = false;
        try {
            idx_cree = GestionTables.ajouteIndex(table, idx, connection);
        } catch (GestionReferentielException ex) {
            // L'index n'est alors pas pris en compte.
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version,
                    "La phonétisation de " + colonneSource + " de " + table + " est effectuée sans index.");
            p.resultat.add("La phonétisation de " + colonneSource + " de " + table + " est effectuée sans index.");
            Logger.getLogger(gestion_referentiel).log(Level.INFO,
                    "L'index " + table + "_" + colonneSource + " doit être supprimé si ses colonnes ne sont pas correctes (" + colonneSource + ")",
                    ex);
        }

        p.state = new String[]{
            en_cours, "PREPARE LES TABLES"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE LES TABLES");

        // Prépare la requête permettant de lister toutes les valeurs à phonétiser.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT \"");
        sb.append(colonneSource);
        sb.append("\" FROM \"");
        sb.append(table);
        sb.append("\"");
        PreparedStatement psCherche = connection.prepareStatement(sb.toString());

        // Prépare la requête permettant de mettre à jour les valeurs.
        sb.setLength(0);
        sb.append("UPDATE \"");
        sb.append(table);
        sb.append("\" SET \"");
        sb.append(colonneDestination);
        sb.append("\"=? WHERE \"");
        sb.append(colonneSource);
        sb.append("\"=?");
        PreparedStatement psUpdate = connection.prepareStatement(sb.toString());

        if (p.stop) {
            psUpdate.close();
            psCherche.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        // Pour chaque valeur à lister,
        p.state = new String[]{
            en_cours, "CHERCHE LES NOMS"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES NOMS");
        ResultSet rsCherche = psCherche.executeQuery();

        if (p.stop) {
            psUpdate.close();
            rsCherche.close();
            psCherche.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        p.state = new String[]{
            en_cours, "TRAITEMENT", "NOMS TRAITEES", "0"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT");
        int compte = 0;
        while (!p.stop && rsCherche.next()) {
            String valeurSource = rsCherche.getString(1);

            // phonétise la valeur.
            String valeurDestination = Algos.phonex(valeurSource);

            psUpdate.setString(1, valeurDestination);
            psUpdate.setString(2, valeurSource);
            psUpdate.execute();

            compte++;
            if (compte % 200 == 0) {
                p.state[3] = Integer.toString(compte);
            }
        }

        // supprime l'index éventuel
        if (idx_cree) {
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "SUPPRIME L'INDEX");
            p.state = new String[]{
                en_cours, "SUPPRIME L'INDEX"
            };
            GestionTables.supprimeIndex(idx, connection);
        }

        if (p.stop) {
            psUpdate.close();
            rsCherche.close();
            psCherche.close();
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION PAR L UTILISATEUR");
            return;
        }

        // Ferme les requêtes.
        psUpdate.close();
        rsCherche.close();
        psCherche.close();

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PHONETISATION de " + colonneSource + " de " + table + " TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Noms traités : " + compte);
        p.resultat.add("PHONETISATION de " + colonneSource + " de " + table);
        p.resultat.add("Noms traités : " + compte);
    }
    String chercheCommuneDansChaine_cherche_optimisee = "SELECT DISTINCT ON (com_nom,dpt_code_departement) com_nom,position_levenstein_joker(?,com_nom_desab,?,0),dpt_code_departement FROM com_communes WHERE position_levenstein_joker(?,com_nom_desab,?,0)<>0 order by com_nom";
    String chercheCommuneDansChaine_cherche = "SELECT com_nom,position_levenstein_joker(?,com_nom_desab,?,0),dpt_code_departement FROM com_communes WHERE position_levenstein_joker(?,com_nom_desab,?,0)<>0";
    String chercheCommuneDansChaine_chercheEncore = "SELECT position_levenstein_joker(?,?,?,?)";

    /**
     * Cherche les noms de commune de France présentes dans la chaine spécifiée, aux erreurs près.<br>
     * La recherche est effectuée sur la version désabbréviée de la commune.<br>
     * Les erreurs prises en comptes sont définies par jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune.<br>
     * La procédure stockée position_levenstein_joker est utilisée autant de fois que nécessaire
     * avec chaque commune trouvée.<br>
     * Les noms de communes qui pourraient être imbriqués (qui pourrait se contenir eux-même aux
     * fautes près) ne sont pas pris en compte. Cette contrainte a pour effet de ne pas pouvoir trouver 
     * dans une chaine le même nom de commune autrement que dans des portions disjointes.<br>
     * ex: dans la chaine 'DODOUAI', la ville DOUAI n'est trouvée qu'une seule fois.
     * @param code_departement le département dans lequel chercher la commune, s'il n'est pas spécifié, 
     * les codes de département par défaut sont utilisés.
     * 
     */
    public ArrayList<RefCommune> chercheCommuneDansChaine(String chaine, Connection connection) throws SQLException {
        // Prépare la requête permettant de chercher une commune dans une chaine
        PreparedStatement psCherche = connection.prepareStatement(chercheCommuneDansChaine_cherche_optimisee);
        int index = 1;
        psCherche.setString(index++, chaine);
        psCherche.setInt(index++, jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());
        psCherche.setString(index++, chaine);
        psCherche.setInt(index++, jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());

        // Optimisation possible : à implémenter en java plutôt qu'en procédure stockée.
        // Prépare la requête permettant de chercher une autre occurence de la commune dans une chaine
        PreparedStatement psChercheEncore = connection.prepareStatement(chercheCommuneDansChaine_chercheEncore);
        psChercheEncore.setString(1, chaine);
        psChercheEncore.setInt(3, jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());

        ArrayList<RefCommune> refcles = new ArrayList<RefCommune>();

        ResultSet rsCherche = psCherche.executeQuery(); // Modification des variables internes ?????

        Hashtable<String, Integer> nomsCommune = new Hashtable<String, Integer>(); // patch 2
        while (rsCherche.next()) {
            String nomCommune = rsCherche.getString(1);
            String code_departement = rsCherche.getString(3);

            // Si la commune suivante est la même que la précédente (même nom de commune dans
            // un autre département), ce département est ajouté aux occurences de la commune.
            //if (lastNomCommune!=null && lastNomCommune.compareTo(nomCommune)==0) // patch1 et avant
            if (nomsCommune.get(nomCommune) != null) {
                int lastindex = nomsCommune.get(nomCommune).intValue();
                if (!refcles.get(lastindex).contientDepartement(code_departement)) {
                    refcles.get(lastindex).ajouteCodeDepartement(code_departement);
                }
            } // Sinon, il s'agit d'une nouvelle commune.
            else {
                nomsCommune.put(nomCommune, refcles.size());
//                lastNomCommune = nomCommune; // patch1 et avant
//                lastindex = refcles.size();

                int position = rsCherche.getInt(2);
                int end = (position - 1) % 256;     // la procédure stockée position_levenstein_joker_no_spaces
                int start = ((position - 1) % 65536) / 256; // retourne le début et la fin de la chaine trouvée.
                int fautes = (position - 1) / 65536; // mais aussi le nombre de fautes constatées pour cette chaine.

                try {
                    Mot m = new Mot(nomCommune);
                    RefCommune ref = new RefCommune(chaine.substring(start, end + 1), m, start, chaine, CategorieMot.Ville,
                            fautes);
                    ref.ajouteCodeDepartement(code_departement);
                    refcles.add(ref);
                } catch (StringIndexOutOfBoundsException sioobe) {
                    Logger.getLogger(gestion_referentiel).log(Level.INFO,
                            "Erreur avec la chaine " + chaine + " et la commune " + nomCommune);
                    throw (sioobe);
                }

                // Cherche si d'autres occurences de la commune sont présentes dans la chaine.
                psChercheEncore.setString(2, nomCommune);
                do {
                    psChercheEncore.setInt(4, end + 1);
                    ResultSet rsChercheEncore = psChercheEncore.executeQuery();

                    if (rsChercheEncore.next()) {
                        position = rsChercheEncore.getInt(1);

                        if (position != 0) {
                            end = (position - 1) % 256;
                            start = ((position - 1) % 65536) / 256;
                            fautes = (position - 1) / 65536;

                            Mot m = new Mot(nomCommune);
                            RefCommune ref = new RefCommune(chaine.substring(start, end + 1), m, start, chaine,
                                    CategorieMot.Ville, fautes);
                            ref.ajouteCodeDepartement(code_departement);
                            refcles.add(ref);
                        }
                    } else {
                        position = 0;
                    }
                    rsChercheEncore.close();

                } while (position != 0);
            }
        }

        psChercheEncore.close();
        psCherche.close();
        rsCherche.close();

        return refcles;
    }
    String chercheCommuneDansChaine_1_optimisee = "SELECT DISTINCT ON (com_nom,dpt_code_departement) com_nom,position_levenstein_joker(?,com_nom_desab,?,0),dpt_code_departement ";
    String chercheCommuneDansChaine_1 = "SELECT com_nom,position_levenstein_joker(?,com_nom_desab,?,0),dpt_code_departement ";
    String chercheCommuneDansChaine_2 = "FROM com_communes WHERE (dpt_code_departement=?";
    String chercheCommuneDansChaine_3 = " OR dpt_code_departement=?";
    String chercheCommuneDansChaine_4 = ") AND position_levenstein_joker(?,com_nom_desab,?,0)<>0";
    String chercheCommuneDansChaine_4_optimisee = ") AND position_levenstein_joker(?,com_nom_desab,?,0)<>0 order by com_nom";

    /**
     * Cherche les noms de commune du département spécifié présentes dans la chaine spécifiée, aux erreurs près.<br>
     * La recherche est effectuée sur la version désabbréviée de la commune.<br>
     * Les erreurs prises en comptes sont définies par jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune.<br>
     * La procédure stockée position_levenstein_joker est utilisée autant de fois que nécessaire
     * avec chaque commune trouvée.<br>
     * Les noms de communes qui pourraient être imbriqués (qui pourrait se contenir eux-même aux
     * fautes près) ne sont pas pris en compte. Cette contrainte a pour effet de ne pas pouvoir trouver 
     * dans une chaine le même nom de commune autrement que dans des portions disjointes.<br>
     * ex: dans la chaine 'DODOUAI', la ville DOUAI n'est trouvée qu'une seule fois.
     * @param codes_departement les départements dans lequels chercher la commune, s'il n'est pas spécifié, 
     * les codes de département par défaut sont utilisés.
     */
    public ArrayList<RefCommune> chercheCommuneDansChaine(String chaine, ArrayList<String> codes_departement, Connection connection) throws
            SQLException {
        // utilise les départements par défaut si nécessaire
        if (codes_departement.size() == 0) {
            for (int i = 0; i < jdonrefParams.obtientCodeDepartementParDefaut().length; i++) {
                codes_departement.add(jdonrefParams.obtientCodeDepartementParDefaut()[i]);
            }
        }

        // Prépare la requête permettant de chercher une commune dans une chaine
        StringBuilder sb = new StringBuilder();
        sb.append(chercheCommuneDansChaine_1_optimisee);
        sb.append(chercheCommuneDansChaine_2);
        for (int i = 1; i < codes_departement.size(); i++) {
            sb.append(chercheCommuneDansChaine_3);
        }
        sb.append(chercheCommuneDansChaine_4_optimisee);
        PreparedStatement psCherche = connection.prepareStatement(sb.toString());
        int index = 1;
        psCherche.setString(index++, chaine);
        psCherche.setInt(index++, jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());
        for (int i = 0; i < codes_departement.size(); i++) {
            psCherche.setString(index++, codes_departement.get(i));
        }
        psCherche.setString(index++, chaine);
        psCherche.setInt(index++, jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());

        // Prépare la requête permettant de chercher une autre occurence de la commune dans une chaine
        sb.setLength(0);
        sb.append("SELECT position_levenstein_joker(?,?,?,?)");
        PreparedStatement psChercheEncore = connection.prepareStatement(sb.toString());
        psChercheEncore.setString(1, chaine);
        psChercheEncore.setInt(3, jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());

        ArrayList<RefCommune> refcles = new ArrayList<RefCommune>();
        ResultSet rsCherche = psCherche.executeQuery();
        //String lastnomCommune = null;
        //int lastindex = 0;
        Hashtable<String, ArrayList<Integer>> communes = new Hashtable<String, ArrayList<Integer>>();
        while (rsCherche.next()) {
            String nomCommune = rsCherche.getString(1);
            String departement = rsCherche.getString(3);

            //if (lastnomCommune!=null && lastnomCommune.compareTo(nomCommune)==0)
            if (communes.get(nomCommune) != null) {
                ArrayList<Integer> lastindex = communes.get(nomCommune);

                for (int i = 0; i < lastindex.size(); i++) {
                    RefCommune rc = refcles.get(lastindex.get(i));
                    if (!rc.contientDepartement(departement)) {
                        refcles.get(lastindex.get(i).intValue()).ajouteCodeDepartement(departement);
                    }
                }

            /*for(int i=lastindex;i<refcles.size();i++) // patch 1 et 2
            refcles.get(i).ajouteCodeDepartement(departement);*/
            } else {
                //lastnomCommune = nomCommune; // patch 1 et 2
                //lastindex = refcles.size();

                int position = rsCherche.getInt(2);
                int end = (position - 1) % 256;         // la procédure stockée position_levenstein_joker_no_spaces
                int start = ((position - 1) % 65536) / 256; // retourne le début et la fin de la chaine trouvée
                int fautes = (position - 1) / 65536;      // mais aussi le nombre de fautes.

                ArrayList<Integer> lastindex = new ArrayList<Integer>();

                try {
                    Mot m = new Mot(nomCommune);
                    RefCommune ref = new RefCommune(chaine.substring(start, end + 1), m, start, chaine, CategorieMot.Ville, fautes);
                    ref.ajouteCodeDepartement(departement);
                    lastindex.add(refcles.size());
                    refcles.add(ref);
                } catch (StringIndexOutOfBoundsException sioobe) {
                    Logger.getLogger(gestion_referentiel).log(Level.INFO,
                            "Erreur avec la chaine " + chaine + " et la commune " + nomCommune);
                    throw (sioobe);
                }

                // Cherche si d'autres occurences de la commune sont présentes dans la chaine.
                psChercheEncore.setString(2, nomCommune);
                do {
                    psChercheEncore.setInt(4, end + 1);
                    ResultSet rsChercheEncore = psChercheEncore.executeQuery();
                    if (rsChercheEncore.next()) {
                        position = rsChercheEncore.getInt(1);

                        if (position != 0) {
                            end = (position - 1) % 256;
                            start = ((position - 1) % 65536) / 256;
                            fautes = (position - 1) / 65536;

                            Mot m = new Mot(nomCommune);
                            RefCommune ref = new RefCommune(chaine.substring(start, end + 1), m, start, chaine,
                                    CategorieMot.Ville, fautes);
                            ref.ajouteCodeDepartement(departement);
                            lastindex.add(refcles.size()); // patch 2
                            refcles.add(ref);
                        }
                    } else {
                        position = 0;
                    }
                    rsChercheEncore.close();

                } while (position != 0);

                communes.put(nomCommune, lastindex);
            }
        }

        psChercheEncore.close();
        psCherche.close();
        rsCherche.close();

        return refcles;
    }
    private final static String cherchePaysDansChaine = "SELECT DISTINCT ON (pay_sov_a3) pay_nom_fr FROM pay_pays WHERE position_levenstein_joker(?,pay_nom_fr,?,0) <> 0 order by pay_sov_a3;";

    /**
     * Cherche les noms de pays présents dans la chaine spécifiée, aux erreurs près.<br>
     * La recherche est effectuée sur la version désabbréviée du pays.<br>
     * Les erreurs prises en comptes sont définies par jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune.<br>
     * La procédure stockée position_levenstein_joker est utilisée autant de fois que nécessaire
     * avec chaque pays trouvée.<br>
     * Les noms de pays qui pourraient être imbriqués (qui pourrait se contenir eux-même aux
     * fautes près) ne sont pas pris en compte. Cette contrainte a pour effet de ne pas pouvoir trouver
     * dans une chaine le même nom de pays autrement que dans des portions disjointes.<br>
     */
    public ArrayList<RefPays> cherchePaysDansChaine(String chaine, Connection connection) throws SQLException {
        PreparedStatement psCherche = null;
        ResultSet rsCherche = null;
        ArrayList<String> rawNomsPays = new ArrayList<String>();
        try {
            // Prépare la requête permettant de chercher un pays dans une chaine
            psCherche = connection.prepareStatement(cherchePaysDansChaine);
            int pourcentageCorresDePays = jdonrefParams.obtientPourcentageDeCorrespondanceDePays();
            psCherche.setString(1, chaine);
            psCherche.setInt(2, pourcentageCorresDePays);

            rsCherche = psCherche.executeQuery();
            while (rsCherche.next()) {
                rawNomsPays.add(rsCherche.getString(1));
            }
        } finally {
            if (psCherche != null) {
                psCherche.close();
            }
            if (rsCherche != null) {
                rsCherche.close();
            }
        }

        return chercheEncorePaysDansChaine(chaine, connection, rawNomsPays);
    }
    private final static String chercheEncorePaysDansChaine = "SELECT position_levenstein_joker(?,?,?,?)";

    private ArrayList<RefPays> chercheEncorePaysDansChaine(String chaine, Connection connection, List<String> paysNames) throws SQLException {
        ArrayList<RefPays> refcles = new ArrayList<RefPays>();

        // Prépare la requête permettant de chercher toutes les occurence d'un pays dans une chaine
        PreparedStatement psChercheEncore = connection.prepareStatement(chercheEncorePaysDansChaine);
        psChercheEncore.setString(1, chaine);
        psChercheEncore.setInt(3, jdonrefParams.obtientPourcentageDeCorrespondanceDePays());

        int positionLev = 0;
        int end, start, fautes;
        for (String curPays : paysNames) {
            int lastendindex = 0;
            do {
                psChercheEncore.setString(2, curPays);
                psChercheEncore.setInt(4, lastendindex);
                ResultSet rsChercheEncore = psChercheEncore.executeQuery();
                if (rsChercheEncore.next()) {
                    positionLev = rsChercheEncore.getInt(1);
                    if (positionLev != 0) {
                        end = (positionLev - 1) % 256;
                        start = ((positionLev - 1) % 65536) / 256;
                        fautes = (positionLev - 1) / 65536;

                        try {
                            Mot m = new Mot(curPays);
                            RefPays ref = new RefPays(chaine.substring(start, end + 1), m, start, chaine, CategorieMot.Pays, fautes);
                            refcles.add(ref);
                        } catch (StringIndexOutOfBoundsException sioobe) {
                            Logger.getLogger(gestion_referentiel).log(Level.INFO,
                                    "Erreur avec la chaine " + chaine + " et le pays " + curPays);
                            throw (sioobe);
                        }
                        lastendindex = end + 1;
                    }
                }
            } while (positionLev != 0);
        }

        return refcles;
    }

    /**
     * Génére des fantoires aléatoires et uniques dans la table idvoies.<br>
     * Algos.obtientFantoireAleatoire est utilisé pour générer les codes fantoires.<br>
     * La table est utilisée pour vérifier l'unicité des fantoires.<br>
     * L'unicité est garantie pour les nombres en excluant les deux premiers chiffres (ce qui inclu 
     * donc l'unicité pour la chaine complète).
     * Seuls les fantoires non initialisés dans la table sont générés.<br>
     * 36 milliards de fantoires différents peuvent être générés, mais l'algorithme ne peut pas
     * générer plus de 360 millions de fantoires différents (à cause de l'unicité excluant les deux
     * premiers chiffres).<br>
     * Les performances de l'algorithme sont décroissantes avec la taille de la table : une valeur aléatoire
     * est générée jusqu'à ce qu'une valeur non utilisée dans la table soit trouvée.<br>
     * De manière à optimiser la recherche d'unicité excluant les deux premiers chiffres, une colonne supplémentaire
     * et son index sont crées puis supprimés. Le nom de la colonne est code_fantoire_bis, de type
     * character varying(10). L'index a pour nom idvoies_code_fantoire_bis.
     * @param connection la connection à utiliser
     * @throws GestionReferentielException si la table contient plus de 360 millions d'objets.
     */
    public void genereFantoir(Processus p, String nomTable, String id, String fantoire, Connection connection) throws SQLException,
            GestionReferentielException {
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "GENERE FANTOIR");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "table    : " + nomTable);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "id       : " + id);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "fantoire : " + fantoire);

        p.resultat.add("GENERE FANTOIR");
        p.resultat.add("table    : " + nomTable);
        p.resultat.add("id       : " + id);
        p.resultat.add("fantoire : " + fantoire);

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE LES REQUETES ET LES TABLES");
        p.state = new String[]{
            en_cours, "PREPARE LES REQUETES ET LES TABLES"
        };
        // Prépare la requête permettant de compter le nombre de lignes de la table.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(*) ");
        sb.append("FROM \"");
        sb.append(nomTable);
        sb.append("\"");
        Statement sCompte = connection.createStatement();
        ResultSet rsCompte = sCompte.executeQuery(sb.toString());
        if (rsCompte.next()) {
            int compte = rsCompte.getInt(1);
            rsCompte.close();
            sCompte.close();
            if (compte > 360000000) {
                throw (new GestionReferentielException("La table " + nomTable + " a plus de 360000000 objets.",
                        GestionReferentielException.TABLETROPGRANDE, 10));
            }
        } else {
            rsCompte.close();
            sCompte.close();
        }

        // Prépare la requête pour rechercher les lignes à mettre à jour
        sb.setLength(0);
        sb.append("SELECT \"");
        sb.append(id);
        sb.append("\" FROM \"");
        sb.append(nomTable);
        sb.append("\" WHERE \"");
        sb.append(fantoire);
        sb.append("\" IS null OR \"");
        sb.append(fantoire);
        sb.append("\"=''");
        String rqCherche = sb.toString();
        Statement sCherche = connection.createStatement();

        // Ajoute une colonne code_fantoire_bis
        DescriptionTable dt = GestionTables.obtientDescription(nomTable, connection);
        Colonne code_fantoire_bis = null;
        try {
            code_fantoire_bis = new Colonne("code_fantoire_bis", "CHARACTER VARYING", 10);
            int contient = dt.contient(code_fantoire_bis);
            if (contient == Colonne.DIFFERENT) {
                GestionTables.ajouteColonne(nomTable, code_fantoire_bis, connection);
            } else if (contient == Colonne.TYPEDIFFERENT) {
                throw (new GestionReferentielException(
                        "Une colonne code_fantoire_bis existe déjà avec le mauvais type. Type attendu : character varying 10",
                        GestionReferentielException.COLONNEERRONEE, 10));
            }
        } catch (ColonneException ex) {
        // Ne devrait pas arriver.
        }
        // Crée un index pour cette colonne
        Index idvoies_code_fantoire_bis = new Index();
        idvoies_code_fantoire_bis.setNom("idvoies_code_fantoire_bis");
        idvoies_code_fantoire_bis.ajouteColonne("code_fantoire_bis");
        boolean idvoies_code_fantoire_bis_cree = GestionTables.ajouteIndex(nomTable, idvoies_code_fantoire_bis, connection);

        // Prépare la requête pour vérifier qu'un fantoire n'est pas déjà attribué
        sb.setLength(0);
        sb.append("SELECT \"");
        sb.append(fantoire);
        sb.append("\" FROM \"");
        sb.append(nomTable);
        sb.append("\" WHERE code_fantoire_bis=? ");
        sb.append("LIMIT 1");
        PreparedStatement psChercheFantoire = connection.prepareStatement(sb.toString());

        // Prépare la requête pour mettre à jour les fantoires
        sb.setLength(0);
        sb.append("UPDATE \"");
        sb.append(nomTable);
        sb.append("\" SET \"");
        sb.append(fantoire);
        sb.append("\"=?,code_fantoire_bis=? ");
        sb.append("WHERE \"");
        sb.append(id);
        sb.append("\"=?");
        PreparedStatement psUpdate = connection.prepareStatement(sb.toString());

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
            return;
        }

        // Crée chaque fantoire unique.
        p.state[1] = "CHERCHE LES VOIES";
        ResultSet rsCherche = sCherche.executeQuery(rqCherche);

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
            return;
        }

        p.state = new String[]{
            en_cours, "TRAITEMENT", "LIGNES TRAITEES", "0"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT");
        int count = 0;
        while (!p.stop && rsCherche.next()) {
            String idval = rsCherche.getString(1);
            String code_fantoire = Algos.obtientFantoireAleatoire();

            psChercheFantoire.setString(1, code_fantoire);
            ResultSet rsChercheFantoire = psChercheFantoire.executeQuery();
            while (rsChercheFantoire.next()) {
                rsChercheFantoire.close();
                code_fantoire = Algos.obtientFantoireAleatoire();
                psChercheFantoire.setString(1, code_fantoire);
                rsChercheFantoire = psChercheFantoire.executeQuery();
            }
            rsChercheFantoire.close();

            psUpdate.setString(1, code_fantoire);
            psUpdate.setString(2, code_fantoire.substring(2));
            psUpdate.setString(3, idval);
            psUpdate.execute();

            count++;

            if (count % 500 == 0) {
                p.state[3] = Integer.toString(count);
            }
        }

        // Ferme les requêtes.
        psChercheFantoire.close();
        psUpdate.close();
        rsCherche.close();
        sCherche.close();

        if (p.stop) {
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
        }

        p.state = new String[]{
            en_cours, "RESTAURE LES TABLES"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "RESTAURE LES TABLES");
        // Supprime l'index
        if (idvoies_code_fantoire_bis_cree) {
            GestionTables.supprimeIndex(idvoies_code_fantoire_bis, connection);
        }
        // Supprime la colonne ajoutée        
        GestionTables.supprimeColonne(nomTable, code_fantoire_bis, connection);

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CREATION DES FANTOIRES");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Lignes traitées: " + count);
        p.resultat.add("CREATION DES FANTOIRES");
        p.resultat.add("Lignes traitées: " + count);
    }

    /**
     * Permet de restructurer l'adresse spécifié dans chaque ligne d'une table.<br>
     * @return
     */
    public String[] restructure(Processus p, String nomTable, String id, String[] columns, String[] restructuration, Connection connection,
            Connection referentiel) throws SQLException {
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "RESTRUCTURE");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "table : " + nomTable);
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "id    : " + id);
        p.resultat.add("RESTRUCTURE");
        p.resultat.add("table : " + nomTable);
        p.resultat.add("id    : " + id);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            sb.append(columns[i]);
            sb.append(' ');
        }
        p.resultat.add("colonnes : " + sb.toString());
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "colonnes : " + sb.toString());
        sb.setLength(0);
        for (int i = 0; i < restructuration.length; i++) {
            sb.append(restructuration[i]);
            sb.append(' ');
        }
        p.resultat.add("restructuration : " + sb.toString());
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "restructuration : " + sb.toString());

        p.state = new String[]{
            en_cours, preparation
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, preparation);
        sb.setLength(0);
        sb.append("SELECT \"");
        sb.append(id);
        sb.append("\"");
        for (int i = 0; i < columns.length; i++) {
            sb.append(",\"");
            sb.append(id);
            sb.append('\"');
        }
        sb.append(" FROM \"");
        sb.append(nomTable);
        sb.append("\"");
        String rqCherche = sb.toString();
        Statement stCherche = connection.createStatement();

        sb.setLength(0);
        sb.append("UPDATE \"");
        sb.append(nomTable);
        sb.append("\" SET ");
        for (int i = 0; i < restructuration.length; i++) {
            if (restructuration[i] != null && restructuration[i].length() > 0) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append('\"');
                sb.append(restructuration[i]);
                sb.append("\"=?");
            }
        }
        sb.append(" WHERE \"");
        sb.append(id);
        sb.append("\"=?");
        PreparedStatement psUpdate = connection.prepareStatement(sb.toString());

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
            return new String[]{
                "1"
            };
        }

        p.state[1] = "CHERCHE LES ADRESSES";
        ResultSet rsCherche = stCherche.executeQuery(rqCherche);

        if (p.stop) {
            p.state = new String[]{
                "TERMINE"
            };
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
            return new String[]{
                "1"
            };
        }

        p.state = new String[]{
            en_cours, "TRAITEMENT", "ADRESSES TRAITEES", "0"
        };
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRAITEMENT");
        int count = 0;
        String[] adresse = new String[columns.length];
        while (!p.stop && rsCherche.next()) {
            // Restructure l'adresse
            String id_val = rsCherche.getString(1);
            for (int i = 0; i < columns.length; i++) {
                adresse[i] = rsCherche.getString(i + 2);
            }
            String[] adressenormalisee = this.gestionMots.restructure(adresse, false, false, referentiel);

            // Puis l'insère dans la table.
            int index = 1;
            for (int i = 0; i < 6; i++) {
                if (restructuration[i] != null && restructuration[i].length() > 0) {
                    psUpdate.setString(index++, adressenormalisee[i]);
                }
            }
            psUpdate.setString(index, id_val);

            psUpdate.execute();

            count++;

            if (count % 200 == 0) {
                p.state[3] = Integer.toString(count);
            }
        }

        rsCherche.close();
        stCherche.close();
        psUpdate.close();

        if (p.stop) {
            p.resultat.add("INTERRUPTION UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
        }

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "RESTRUCTURATION TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "ADRESSE TRAITEES:" + count);
        p.resultat.add("RESTRUCTURATION");
        p.resultat.add("ADRESSE TRAITEES:" + count);

        return new String[]{
            "1"
        };
    }

    /**
     * Permet de générer des id de troncons uniques.<br>
     * Les id de tronçons sont crées dans la table "tro_troncons_codeDepartement",
     * et l'unicité est garantie par la table idtroncons.<br>
     * Le champ oid est utilisé comme identifiant unique.
     * Les nouveaux id de tronçons sont ajoutés dans cette table.
     * @param p le processus utilisé
     * @param code_departement le département concerné
     * @param connection
     */
    public void genereIdTroncon(Processus p, String code_departement, Connection connection) throws GestionReferentielException,
            SQLException {
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "GENERE ID TRONCON");
        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "Departement : " + code_departement);
        p.resultat.add("GENERE ID TRONCON");
        p.resultat.add("Departement : " + code_departement);

        jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "PREPARE LES TABLES ET REQUETES");
        p.state = new String[]{
            en_cours, "PREPARE LES TABLES ET REQUETES"
        };

        try {
            GestionTables.creeTable("idt_id_troncons", GestionDescriptionTables.creeDescriptionTableIdTroncons(), connection);
        } catch (SQLException sqle) {
            p.state = new String[]{
                erreur, "La table idtroncons est incorrecte."
            };
            throw (new GestionReferentielException("La table idtroncons est incorrecte", GestionReferentielException.ERREURTABLE, 11));
        }

        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            // Crée les index nécessaires
            Index idx_id_troncon = new Index();
            idx_id_troncon.setNom("tro_" + code_departement + "_id_troncon");
            idx_id_troncon.ajouteColonne("tro_id");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            boolean idx_id_troncon_ajoute=GestionTables.ajouteIndex("tro_troncons_"+code_departement,idx_id_troncon, connection);
            boolean idx_id_troncon_ajoute = GestionTables.ajouteIndex(GestionTables.getTroTronconsTableName(code_departement),
                    idx_id_troncon, connection);
            Index idx_id_troncon2 = new Index();
            idx_id_troncon2.setNom("idt_id_troncon");
            idx_id_troncon2.ajouteColonne("tro_id");
            boolean idx_id_troncon2_ajoute = GestionTables.ajouteIndex("idt_id_troncons", idx_id_troncon2, connection);

            // La requête de recherche des tronçons
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT oid FROM \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sbCom.append("tro_troncons_").append(code_departement);
            sb.append(GestionTables.getTroTronconsTableName(code_departement));
            sb.append("\" WHERE tro_id='' or tro_id is NULL");
            String rqCherche = sb.toString();
            Statement sCherche = connection.createStatement();

            // La requête de mise à jour de l'id du tronçon
            sb.setLength(0);
            sb.append("UPDATE \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sbCom.append("tro_troncons_").append(code_departement);
            sb.append(GestionTables.getTroTronconsTableName(code_departement));
            sb.append("\" set tro_id=? WHERE oid=?");
            PreparedStatement psUpdate = connection.prepareStatement(sb.toString());

            // La requête de recherche d'unicité de l'identifiant
            sb.setLength(0);
            sb.append("SELECT tro_id FROM idt_id_troncons WHERE tro_id=? LIMIT 1");
            PreparedStatement psUniqueId = connection.prepareStatement(sb.toString());

            // La requête d'insertion du nouvel identifiant
            sb.setLength(0);
            sb.append("INSERT INTO idt_id_troncons (tro_id,dpt_code_departement) VALUES (?,?)");
            PreparedStatement psInsertId = connection.prepareStatement(sb.toString());

            if (p.stop) {
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION UTILISATEUR");
                return;
            }

            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "CHERCHE LES TRONCONS");
            p.state[1] = "CHERCHE LES TRONCONS";
            ResultSet rsCherche = sCherche.executeQuery(rqCherche);
            p.state = new String[]{
                en_cours, "TRAITEMENT", "TRONCONS TRAITES", "0"
            };

            if (p.stop) {
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
                p.state = new String[]{
                    "TERMINE"
                };
                p.resultat.add("INTERRUPTION UTILISATEUR");
                return;
            }

            int troncons = 0;
            while (!p.stop && rsCherche.next()) {
                String oid = rsCherche.getString(1);
                String id_troncon = GestionIdentifiants.generateId(psUniqueId);

                psUpdate.setString(1, id_troncon);
                psUpdate.setString(2, oid);
                psUpdate.execute();

                psInsertId.setString(1, id_troncon);
                psInsertId.setString(2, code_departement);
                psInsertId.execute();

                connection.commit();

                troncons++;
                if (troncons % 100 == 0) {
                    p.state[3] = Integer.toString(troncons);
                }
            }

            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "SUPPRIME LES INDEX");
            p.state = new String[]{
                en_cours, "SUPPRIME LES INDEX"
            };
            if (idx_id_troncon_ajoute) {
                GestionTables.supprimeIndex(idx_id_troncon, connection);
            }
            if (idx_id_troncon2_ajoute) {
                GestionTables.supprimeIndex(idx_id_troncon2, connection);
            }

            if (p.stop) {
                jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "INTERRUPTION UTILISATEUR");
                p.resultat.add("INTERRUPTION UTILISATEUR");
            }

            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "GENERATION D'IDENTIFIANTS DE TRONCON");
            jdonrefParams.getGestionLog().logAdmin(p.numero, p.version, "TRONCONS TRAITES :" + troncons);
            p.resultat.add("GENERATION D'IDENTIFIANTS DE TRONCON");
            p.resultat.add("TRONCONS TRAITES :" + troncons);
        } finally {
            connection.setAutoCommit(autocommit);
        }
    }

    /**
     * Obtient le nombre total de voies trouvées dans le référentiel.<br>
     * Les départements recensés par la table dpt_departements sont utilisés
     * pour trouver les tables de voies voi_voies_dpt
     * @return un tableau composé <ul>
     * <li>du nombre total de voies</li>
     * <li>du nombre maximal de voies trouvé dans un département</li>
     * <li>du département concerné</li>
     * <li>du nombre minimal de voies trouvé dans un département</li>
     * <li>du département concerné</li>
     * </ul>
     * Si il n'y a pas de minimum ou de maximum les valeurs sont null.
     */
    public String[] obtientTotalVoies(Connection c) throws SQLException {
        int total = 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        String min_dpt = null, max_dpt = null;
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT DISTINCT dpt_code_departement FROM dpt_departements");

        while (rs.next()) {
            String code_departement = rs.getString(1);

            Statement stCompte = c.createStatement();
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            ResultSet rsCompte = stCompte.executeQuery("SELECT count(*) from \"voi_voies_"+code_departement+"\";");
            ResultSet rsCompte = stCompte.executeQuery(
                    "SELECT count(*) from \"" + GestionTables.getVoiVoiesTableName(code_departement) + "\";");

            if (rsCompte.next()) {
                int compte = rsCompte.getInt(1);
                total += compte;
                if (compte > max) {
                    max = compte;
                    max_dpt = code_departement;
                } else if (compte < min) {
                    min = compte;
                    min_dpt = code_departement;
                }
            }

            rsCompte.close();
            stCompte.close();
        }

        st.close();
        rs.close();

        String[] res = new String[5];
        res[0] = Integer.toString(total);
        if (min != Integer.MAX_VALUE) {
            res[1] = Integer.toString(min);
        } else {
            res[1] = null;
        }
        res[2] = min_dpt;
        if (max != Integer.MIN_VALUE) {
            res[3] = Integer.toString(max);
        } else {
            res[3] = null;
        }
        res[4] = max_dpt;

        return res;
    }

    /**
     * Obtient le nombre total de communes du référentiel.
     * @param c
     * @return un tableau composé <ul>
     * <li>du nombre total de communes</li>
     * <li>du nombre maximal de communes trouvé dans un département</li>
     * <li>du département concerné</li>
     * <li>du nombre minimal de communes trouvé dans un département</li>
     * <li>du département concerné</li>
     * </ul>
     * @throws java.sql.SQLException
     */
    public String[] obtientTotalCommunes(Connection c) throws SQLException {
        int total = 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        String min_dpt = null, max_dpt = null;

        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT count(*),dpt_code_departement FROM com_communes GROUP BY dpt_code_departement");

        while (rs.next()) {
            int compte = rs.getInt(1);

            total += compte;

            if (compte > max) {
                max = compte;
                max_dpt = rs.getString(2);
            } else if (compte < min) {
                min = compte;
                min_dpt = rs.getString(2);
            }
        }

        rs.close();
        st.close();

        String[] res = new String[5];
        res[0] = Integer.toString(total);
        if (min != Integer.MAX_VALUE) {
            res[1] = Integer.toString(min);
        } else {
            res[1] = null;
        }
        res[2] = min_dpt;
        if (max != Integer.MIN_VALUE) {
            res[3] = Integer.toString(max);
        } else {
            res[3] = null;
        }
        res[4] = max_dpt;

        return res;
    }

    /**
     * Obtient le nombre total de codes postaux du référentiel.<br>
     * Il ne s'agit pas du nombre de couples (code insee, code postaux) mais du nombre
     * de codes postaux différents (close DISTINCT).
     * @param c
     * @return un tableau composé <ul>
     * <li>du nombre total de code postaux</li>
     * <li>du nombre maximal de code postaux trouvé dans un département</li>
     * <li>du département concerné</li>
     * <li>du nombre minimal de code postaux trouvé dans un département</li>
     * <li>du département concerné</li>
     * </ul>
     * @throws java.sql.SQLException
     */
    public String[] obtientTotalCodesPostaux(Connection c) throws SQLException {
        int total = 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        String min_dpt = null, max_dpt = null;

        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT count(*),dpt_code_departement FROM cdp_codes_postaux GROUP BY dpt_code_departement");

        while (rs.next()) {
            int compte = rs.getInt(1);

            total += compte;

            if (compte > max) {
                max = compte;
                max_dpt = rs.getString(2);
            } else if (compte < min) {
                min = compte;
                min_dpt = rs.getString(2);
            }
        }

        rs.close();
        st.close();

        String[] res = new String[5];
        res[0] = Integer.toString(total);
        if (min != Integer.MAX_VALUE) {
            res[1] = Integer.toString(min);
        } else {
            res[1] = null;
        }
        res[2] = min_dpt;
        if (max != Integer.MIN_VALUE) {
            res[3] = Integer.toString(max);
        } else {
            res[3] = null;
        }
        res[4] = max_dpt;

        return res;
    }

    /**
     * Obtient le nombre total de tronçons trouvés dans le référentiel.<br>
     * Les tables de tronçons sont identifiées par la table ttr_tables_troncons.<br>
     * Le com
     * @param c
     * @return un tableau composé <ul>
     * <li>du nombre total de troncons</li>
     * <li>du nombre maximal de troncons trouvé dans un département</li>
     * <li>du département concerné</li>
     * <li>du nombre minimal de troncons trouvé dans un département</li>
     * <li>du département concerné</li>
     * </ul>
     * @throws java.sql.SQLException
     */
    public String[] obtientTotalTroncons(Connection c) throws SQLException {
        int total = 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        String min_dpt = null, max_dpt = null;
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT DISTINCT dpt_code_departement FROM ttr_tables_troncons");

        PreparedStatement psCherchettr = c.prepareStatement("SELECT ttr_nom FROM ttr_tables_troncons where dpt_code_departement=?");

        // Enumére les départements
        while (rs.next()) {
            int compte_dpt = 0;
            String code_departement = rs.getString(1);

            psCherchettr.setString(1, code_departement);
            ResultSet rsCherchettr = psCherchettr.executeQuery();

            while (rsCherchettr.next()) {
                String nom = rsCherchettr.getString(1);

                Statement stCompte = c.createStatement();
                ResultSet rsCompte = stCompte.executeQuery("SELECT count(*) from \"" + nom + "\";");

                if (rsCompte.next()) {
                    int compte = rsCompte.getInt(1);
                    compte_dpt += compte;
                    total += compte;
                }

                rsCompte.close();
                stCompte.close();
            }

            if (compte_dpt > max) {
                max = compte_dpt;
                max_dpt = code_departement;
            } else if (compte_dpt < min) {
                min = compte_dpt;
                min_dpt = code_departement;
            }

            rsCherchettr.close();
        }

        st.close();
        rs.close();

        String[] res = new String[5];
        res[0] = Integer.toString(total);
        if (min != Integer.MAX_VALUE) {
            res[1] = Integer.toString(min);
        } else {
            res[1] = null;
        }
        res[2] = min_dpt;
        if (max != Integer.MIN_VALUE) {
            res[3] = Integer.toString(max);
        } else {
            res[3] = null;
        }
        res[4] = max_dpt;

        return res;
    }
}
