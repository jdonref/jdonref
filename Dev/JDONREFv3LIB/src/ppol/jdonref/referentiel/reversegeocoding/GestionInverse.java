/*
 * Version 2.2 – Décembre 2010
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC
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
package ppol.jdonref.referentiel.reversegeocoding;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
//import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.Tables.GestionTables;
import ppol.jdonref.referentiel.GestionGeocodage;
import ppol.jdonref.referentiel.GestionHistoriqueTables;
import ppol.jdonref.referentiel.JDONREFv3Lib;
import ppol.jdonref.utils.DateUtils;
import ppol.jdonref.utils.GeometryUtils;
import ppol.jdonref.utils.MiscUtils;

/**
 * Gestion du reverse Géocoding.
 * 
 * @author Julien Moquet
 */
public class GestionInverse {

    public static final int SERVICE_ADRESSE = 1;
    public static final int SERVICE_POINT_ADRESSE = 2;
    public static final int SERVICE_TRONCON = 3;
    public static final int SERVICE_VOIE = 4;
    public static final int SERVICE_COMMUNE = 5;
    public static final int SERVICE_DEPARTEMENT = 6;
    public static final int SERVICE_PAYS = 7;

    //static WKTReader wktReader = new WKTReader();
    JDONREFParams jdonrefParams = null;
    GeometryFactory gf = new GeometryFactory();

    // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
    // static final SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE);
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;
    public static final int GestionInverse_PLAQUE = 1;
    public static final int GestionInverse_INTERPOLATION_PLAQUE = 2;  // non implémenté.
    public static final int GestionInverse_TRONCON = 4;
    public static final int GestionInverse_VOIE = 8;
    public static final int GestionInverse_COMMUNE = 16;
    public static final int GestionInverse_DEPARTEMENT = 32;
    public static final int GestionInverse_PAYS = 64; // non implémenté.
    /**
     * Description des options du géocodage inverse.
     * A chaque nom d'option est associé son code option.
     * ex:
     * <ul>
     * <li>pays : GestionInverse.OPTION_DPT</li>
     * <li>defaultdpt : GestionInverse.OPTION_DEFAULTDPT</li>
     * </ul>
     */
    protected Hashtable<String, Integer> optionsHash;
    /**
     * Permet de spécifier le département qui sera utilisé par le
     * reverse géocoding.
     */
    static final int OPTION_DPT = 1;
    /**
     * Permet de spécifier un département par défaut qui sera utilisé
     * par le géocodage si aucun département n'est identifié.
     */
    static final int OPTION_DEFAULTDPT = 2;
    /**
     * Permet de spécifier que toutes les solutions doivent être retournées
     * et pas uniquement les plus proches.
     */
    static final int OPTION_TOUTESREPONSES = 4;

    /** Creates a new instance of GestionReferentiel */
    public GestionInverse(JDONREFParams jdonrefParams) {
        this.jdonrefParams = jdonrefParams;

        initOptionHash();
    }

    public void initOptionHash() {
        this.optionsHash = new Hashtable<String, Integer>();
        this.optionsHash.put("dpt", GestionInverse.OPTION_DPT);
        this.optionsHash.put("defaultdpt", GestionInverse.OPTION_DEFAULTDPT);
        this.optionsHash.put("touteslesreponses", GestionInverse.OPTION_TOUTESREPONSES);
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
        this.jdonrefParams = jdonrefParams;
    }

    /**
     * Permet d'extraire les options du tableau spécifié.
     * Le tableau optionsHash décrit les options possibles.
     * 
     * 
     * @throw JDONREFException le tableau optionsHash n'est pas initialisé.
     */
    protected InverseOption extractOptions(String[] options) throws JDONREFException {
        if (this.optionsHash == null) {
            throw (new JDONREFException("Le tableau JDONREFException n'est pas initialisé."));
        }

        InverseOption io = new InverseOption();

        for (int i = 0; i < options.length; i++) {
            Integer flag = this.optionsHash.get(options[i]);
            if (flag != null) {
                int i_flag = flag.intValue();

                switch (i_flag) {
                    case GestionInverse.OPTION_TOUTESREPONSES:
                        io.touteslesreponses = true;
                        break;
                    case GestionInverse.OPTION_DPT:
                        if (i < options.length - 1) {
                            io.dpt = options[i++];
                        } else {
                            throw (new JDONREFException("L'option dpt n'est pas correctement employée."));
                        }
                        break;
                    case GestionInverse.OPTION_DEFAULTDPT:
                        if (i < options.length - 1) {
                            io.defaultdpt = options[i++];
                        } else {
                            throw (new JDONREFException("L'option defaultdpt n'est pas correctement employée."));
                        }
                }
            }
        }
        return io;
    }
    
    public String findDepVoie(String voi_id, Connection connection) throws SQLException{
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT dpt_code_departement FROM idv_id_voies where voi_id = '");
        sb.append(voi_id);sb.append("'");
        String sb1 = sb.toString();
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ResultSet rs = ps.executeQuery();        
        if (rs.next())
            return rs.getString(1);
        else
            return null;
    }
    
    
    public InverseOption extractOptionsIds4(String[] options, Connection connection) throws JDONREFException, SQLException {

        InverseOption io = new InverseOption();
        
        String ids4 = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("ids4")) {
                    ids4 = (tokens[1] != null) ? tokens[1].trim() : "";
                } 
            }
        }
        String dept = findDepVoie(ids4, connection);
        io.setVoi_id(ids4);
        io.setDpt(dept);
        return io;
    }

    /**
     * 
     * Effectue un géocodage inverse sur un département spécifié.
     * 
     * @param pays Département dans lequel effectuer la recherche
     * @param application numéro de l'application
     * @param operation numéro de l'opération à effectuer
     * @param date date à laquelle réaliser le géocodage inverse
     * @param pos point auquel réaliser le géocodage inverse
     * @param dst distance maximale utilisée par le géocodage inverse
     * @param io options de reverse géocoding
     * @param connection connection à la base
     * @return
     * @throws java.sql.SQLException Problème avec la base.
     */
    public ArrayList<GeocodageInverse> inverseSurDepartement(String dpt, int application, int operation, Date date, Point pos, double dst, int projection, InverseOption io, Connection connection) throws SQLException, ParseException {
        ArrayList<GeocodageInverse> res = new ArrayList<GeocodageInverse>();

        boolean no_more_needed = false; // indicateur de precision souhaitée obtenue. Equivalent à res.size()!=0

        GeocodageInverse[] com_res = null;
        GeocodageInverse[] voi_res = null;
        GeocodageInverse[] tro_res = null;
        GeocodageInverse[] pla_res = null;

        // Tente un reverse géocoding à la plaque
        if ((operation & GestionInverse.GestionInverse_PLAQUE) != 0) {
            pla_res = inversePlaque(application, dpt, pos, dst, date, projection, io, connection);

            if (pla_res != null && pla_res.length > 0) {
                no_more_needed = true;
                for (int j = 0; j < pla_res.length; j++) {
                    res.add(pla_res[j]);
                }
            }
        }

        // Tente un reverse géocoding à l'interpolation de la plaque
        if ((io.touteslesreponses || !no_more_needed) && ((operation & GestionInverse.GestionInverse_INTERPOLATION_PLAQUE) != 0)) {
        // n'est pas implémenté actuellement, ignoré (équivalent à aucun résultat).
        }

        // Tente un reverse géocoding au tronçon
        if ((io.touteslesreponses || !no_more_needed) && ((operation & GestionInverse.GestionInverse_TRONCON) != 0)) {
            tro_res = inverseTroncon(application, dpt, pos, dst, date, projection, io, connection);

            if (tro_res != null && tro_res.length > 0) {
                no_more_needed = true;
                for (int j = 0; j < tro_res.length; j++) {
                    res.add(tro_res[j]);
                }
            }
        }

        // Tente un reverse géocoding à la voie (sans numéro uniquement)
        if ((io.touteslesreponses || !no_more_needed) && ((operation & GestionInverse.GestionInverse_VOIE) != 0)) {
            voi_res = inverseVoie(application, dpt, pos, dst, date, projection, io, connection);

            if (voi_res != null && voi_res.length > 0) {
                no_more_needed = true;
                for (int j = 0; j < voi_res.length; j++) {
                    res.add(voi_res[j]);
                }
            }
        }

        // Tente un reverse géocoding à la commune
        if ((io.touteslesreponses || !no_more_needed) && ((operation & GestionInverse.GestionInverse_COMMUNE) != 0)) {
            com_res = inverseCommune(application, dpt, pos, dst, date, projection, io, connection);

            if (com_res != null && com_res.length > 0) {
                no_more_needed = true;
                for (int j = 0; j < com_res.length; j++) {
                    res.add(com_res[j]);
                }
            }
        }

        // tente un reverse au pays
        if ((io.touteslesreponses || !no_more_needed) && ((operation & GestionInverse.GestionInverse_PAYS) != 0)) {
            final GeocodageInverse[] pay_res = inversePays(application, pos, dst, date, projection, connection);
            if (pay_res != null && pay_res.length > 0) {
                for (int j = 0; j < pay_res.length; j++) {
                    res.add(pay_res[j]);
                }
            }
        }

        return res;
    }

    public ArrayList<GeocodageInverse> inverseSurDepartement(String dpt, int application, int[] services, int operation, Date date, Point pos, double dst, int projection, InverseOption io, Connection connection) throws SQLException, ParseException {
        ArrayList<GeocodageInverse> res = new ArrayList<GeocodageInverse>();

        ArrayList<GeocodageInverse> adr_res = new ArrayList<GeocodageInverse>();
        GeocodageInverse[] pay_res = null;
        GeocodageInverse[] com_res = null;
        GeocodageInverse[] voi_res = null;
        GeocodageInverse[] tro_res = null;
        GeocodageInverse[] pla_res = null;
        for (Integer service : services) {
            //
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(service).getId();
            switch (id) {
                case SERVICE_ADRESSE:
                    adr_res.addAll(inverseSurDepartement(dpt, application, operation, date, pos, dst, projection, io, connection));
                    if (!adr_res.isEmpty()) {
                        res.addAll(adr_res);
                    }
                    break;
                case SERVICE_POINT_ADRESSE:
                    pla_res = inversePlaque(application, dpt, pos, dst, date, projection, io, connection);

                    if (pla_res != null && pla_res.length > 0) {
                        for (int j = 0; j < pla_res.length; j++) {
                            res.add(pla_res[j]);
                        }
                    }
                    break;
                case SERVICE_TRONCON:
                    tro_res = inverseTroncon(application, dpt, pos, dst, date, projection, io, connection);

                    if (tro_res != null && tro_res.length > 0) {
                        for (int j = 0; j < tro_res.length; j++) {
                            res.add(tro_res[j]);
                        }
                    }
                    break;
                case SERVICE_VOIE:
                    voi_res = inverseVoie(application, dpt, pos, dst, date, projection, io, connection);

                    if (voi_res != null && voi_res.length > 0) {
                        for (int j = 0; j < voi_res.length; j++) {
                            res.add(voi_res[j]);
                        }
                    }
                    break;
                case SERVICE_COMMUNE:
                    com_res = inverseCommune(application, dpt, pos, dst, date, projection, io, connection);
                    if (com_res != null && com_res.length > 0) {
                        for (int j = 0; j < com_res.length; j++) {
                            res.add(com_res[j]);
                        }
                    }
                    break;
                case SERVICE_DEPARTEMENT: // Traité dans la méthode inverse
                    break;
                case SERVICE_PAYS:  // Traité dans la méthode inverse
                    break;
                }
        }

        return res;
    }

    /**
     * Calcul du reverse géocoding.
     * 
     * Le reverse géocoding est effectué par requête spatiale, utilisant la distance spécifiée.
     * Plusieurs résultats sont retournés.
     * 
     * La précision du reverse géocoding dépend de l'option et de la qualité du référentiel :
     * 1. A la plaque : le numéro de l'adresse a été identifié de manière sûre.
     * 2. A l'interpolation de la plaque : non supporté, ignoré.
     * 3. A l'interpolation du tronçon : le ou les résultats les plus proches de la projection du point sur le tronçon sont retournés
     * 4. A la voie : le numéro n'est pas conservé, seule la voie compte. Dans ce cas, les niveaux précédents (plaque, troncon) sont ignorés.
     * 5. A la commune ou l'arrondissement : la commune englobant le point est retournée
     * 6. Au département : le département contenant le point est retourné.
     * 
     * Seule les précisions demandées sont retournées. Le résultat le plus précis est retourné.
     * Ainsi, si une commune est trouvée, son département n'est pas retourné.
     * 
     * Les options GestionInverse.OPTION_DPT et GestionInverse.OPTION_DEFAULTDPT ne conduisent pas
     * à ajouter les départements correspondants parmi les résultats.
     * 
     * @param application Numéro de l'application utilisatrice
     * @param operation Niveau du reverse geocoding à calculer
     * @param position Position à reverse géocoder : tableau de positions
     * @param distance nombre flottant sous forme de chaîne (0 par défaut).
     * @param strdate date du géocodage inverse : null pour la date courante
     * @param options options de reverse géocoding
     * <ul>
     * <li>pays, numéro pays : permet de spécifier le département dans lequel géocoder.</li>
     * <li>defaultpt, numéro pays : permet de spécifier le département dans lequel géocoder si aucun département ne peut lui être attribué.</li>
     * </ul>
     * @param connection connection de travail
     * 
     * @return une liste qui représente un ensemble de solutions pour le géocodage inverse
     * de la forme {1,nb proposition,proposition 1,proposition 2, ...}
     * Pour chaque proposition:
     * code,ligne1,ligne2,...,ligne6,X,Y,t0,t1,distance
     * 
     * Le code indique la précision du reverse géocoding obtenue :
     * <ul>
     * <li>GestionInverse_PLAQUE</li>
     * <li>GestionInverse_INTERPOLATION_PLAQUE</li>
     * <li>GestionInverse_TRONCON</li>
     * <li>...</li>
     * </ul>
     * 
     * Si une erreur est survenue retourne un résultat de la forme
     * {0,code erreur, message erreur}
     */
    @Deprecated
    public String[] inverse(int application, int operation, String[] position, String distance, String strdate, int projection, String[] options, Connection connection) throws ParseException {
        // Paramètres
        Date date;
        if (strdate == null) {
            date = Calendar.getInstance().getTime();
        } else {
            try {
                date = DateUtils.parseStringToDate(strdate, sdformat);
            } catch (java.text.ParseException pe) {
//                GestionLogs.getInstance().logInverse(application, operation, false);
                jdonrefParams.getGestionLog().logInverse(application, operation, false);
                return new String[]{"0", "5", "La date est mal formée."};
            }
        }
        if (distance == null) // valeur par défaut
        {
            distance = "0";
        }

        double dst, x, y;
        try {
            dst = Double.parseDouble(distance);
        } catch (NumberFormatException nfe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "La distance est mal formée."};
        }
        try {
            x = Double.parseDouble(position[0]);
        } catch (NumberFormatException nfe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "La latitude est mal formée."};
        }
        try {
            y = Double.parseDouble(position[1]);
        } catch (NumberFormatException nfe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "La longitude est mal formée."};
        }

        // Options
        Coordinate c = new Coordinate(x, y);
        Point pos = gf.createPoint(c);
        InverseOption io = null;
        try {
            io = extractOptions(options);
        } catch (JDONREFException je) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "Problème dans l'analyse des options."};
        }

        ArrayList<GeocodageInverse> res = new ArrayList<GeocodageInverse>();

        try {
            GeocodageInverse[] dpt_res = null;
            ArrayList<String> departements = new ArrayList<String>();

            // Commencer par déterminer le ou les département, si nécessaire.
            if (io.dpt == null) {
                GeocodageInverse[] tmp_res = inverseDepartement(application, pos, dst, date, projection, io, connection);

                if (tmp_res.length == 0) {
                    // Aucun résultat, utilise si disponible le département par défaut.
                    if (io.defaultdpt == null) {
                        return new String[]{"1", "0"};
                    } else {
                        departements.add(io.defaultdpt);
                    }
                } // Les résultats ne sont conservés que si ce niveau de géocodage est demandé.
                else {
                    if ((operation & GestionInverse.GestionInverse_DEPARTEMENT) != 0) {
                        dpt_res = tmp_res;
                    }

                    // dans tous les cas, la recherche sera effectuée dans les départements trouvés (conservés ou non).
                    for (int i = 0; i < tmp_res.length; i++) {
                        departements.add(((GeocodageInverse_Departement) tmp_res[i]).getCodeDepartement());
                    }
                }


            } else {
                departements.add(io.dpt);
            }

            // Tente un géocodage inverse pour chaque département
            for (int i = 0; i < departements.size(); i++) {
                ArrayList<GeocodageInverse> ondpt_res;

                ondpt_res = inverseSurDepartement(departements.get(i), application, operation, date, pos, dst, projection, io, connection);

                if (ondpt_res.size() > 0) {
                    res.addAll(ondpt_res);
                } else {
                    // Finalement, si rien n'est satisfaisant, utilise le géocodage au département si demandé. 
                    if (dpt_res != null && ((operation & GestionInverse.GestionInverse_DEPARTEMENT) != 0)) {
                        res.add(dpt_res[i]);
                    }
                }
            }

            // Formate le résultat.
            return formateGeocodageInverse(res);
        } catch (SQLException sqle) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "Problème avec la base de données"};
        } catch (ParseException pe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "10", "Erreur de géométrie"};
        }
    }


    
    public String[] inverse(int application, int[] services, int operation, String[] position, String distance, String strdate, int projection, String[] options, Connection connection) {
       // Paramètres        
        Date date;
        if (strdate == null) {
            date = Calendar.getInstance().getTime();
        } else {
            try {
                date = DateUtils.parseStringToDate(strdate, sdformat);
            } catch (java.text.ParseException pe) {
//                GestionLogs.getInstance().logInverse(application, operation, false);
                jdonrefParams.getGestionLog().logInverse(application, operation, false);
                return new String[]{"0", "5", "La date est mal formée."};
            }
        }

        //service
        boolean servicePays = false;
        boolean serviceAdresse = false;
        boolean serviceDpt = false;
        boolean serviceTr = false;
        for (Integer service : services) {   
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(service).getId();
            if (id == SERVICE_ADRESSE) {
                serviceAdresse = true;
            } else if (id == SERVICE_DEPARTEMENT) {
                serviceDpt = true;
            }
            if (id == SERVICE_PAYS) {
                servicePays = true;
            }
            if (id == SERVICE_TRONCON) {
                serviceTr = true;
            }
        }
        
        try {
            String[] gi = null;
            // si d'autres services sont selectionnés avec SERVICE_TRONCON ils ne seront pas pris en compte 
            if(serviceTr){
                gi = geocaodageInverseTr(application, operation, options, connection, date, projection);
            }
            else{
                gi = geocaodageInverse(application, services, operation, position, distance, date, projection, options, connection, servicePays, serviceAdresse, serviceDpt);
            }
            return gi;
        } catch (SQLException sqle) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "Problème avec la base de données"};
        } catch (ParseException pe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "10", "Erreur de géométrie"};
        }
    }
    
    int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i=0; i<list.size();i++)
            ret[i]=list.get(i);
        return ret;
    }
    
    public String[] geocaodageInverseTr(int application, int operation, String[] options, Connection connection, Date date, int projection) throws SQLException, ParseException {
        // Options
        InverseOption io = null;
        try {
            io = extractOptionsIds4(options, connection);
        } catch (JDONREFException je) {
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "Problème dans l'analyse des options ids void_id."};
        } 
        final ArrayList<GeocodageInverse> res = new ArrayList<GeocodageInverse>();
        GeocodageInverse[] tro_res = inverseTronconsIntersections(application, io, connection, projection, date);
        if (tro_res != null && tro_res.length > 0) 
            for (int j = 0; j < tro_res.length; j++) 
                res.add(tro_res[j]);
        // Formate le résultat.
        String[] resform = formateGeocodageInverse(res);
        return resform;
    }
    
    public String[] geocaodageInverse(int application, int[] services, int operation, String[] position, String distance, Date date, int projection, String[] options, Connection connection, boolean servicePays, boolean serviceAdresse, boolean serviceDpt) throws SQLException, ParseException {
    // Paramètres        
        if (distance == null) // valeur par défaut
        {
            distance = "0";
        }

        double dst, x, y;
        try {
            dst = Double.parseDouble(distance);
        } catch (NumberFormatException nfe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "La distance est mal formée."};
        }
        try {
            x = Double.parseDouble(position[0]);
        } catch (NumberFormatException nfe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "La latitude est mal formée."};
        }
        try {
            y = Double.parseDouble(position[1]);
        } catch (NumberFormatException nfe) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "La longitude est mal formée."};
        }

        // Options
        Coordinate c = new Coordinate(x, y);
        Point pos = gf.createPoint(c);
        InverseOption io = null;
        try {
            io = extractOptions(options);
        } catch (JDONREFException je) {
//            GestionLogs.getInstance().logInverse(application, operation, false);
            jdonrefParams.getGestionLog().logInverse(application, operation, false);
            return new String[]{"0", "5", "Problème dans l'analyse des options."};
        }

        GeocodageInverse[] dpt_res = null;
        GeocodageInverse[] tmp_res = null;
        ArrayList<String> departements = new ArrayList<String>();

        final ArrayList<GeocodageInverse> res = new ArrayList<GeocodageInverse>();
        // Commencer par déterminer le ou les département, si nécessaire.
        if (io.dpt == null) {
            tmp_res = inverseDepartement(application, pos, dst, date, projection, io, connection);

            if (tmp_res.length == 0) {
                // Aucun résultat, utilise si disponible le département par défaut.
                if (io.defaultdpt == null) {
                    // PAYS ETRANGER
                    if (serviceAdresse) {
                        GeocodageInverse[] pay_res = inversePays(application, pos, dst, date, projection, connection);
                        if (pay_res != null && pay_res.length > 0) {
                            for (int j = 0; j < pay_res.length; j++) {
                                res.add(pay_res[j]);
                            }
                            String[] formateGeocodageInverse = formateGeocodageInverse(res);
                            return formateGeocodageInverse;
                        } else {
                            return new String[]{"1", "0"};
                        }
                    } else {
                        return new String[]{"1", "0"};
                    }
                } else {
                    departements.add(io.defaultdpt);
                }
            } // Les résultats ne sont conservés que si ce niveau de géocodage est demandé.
            else {
                if (serviceDpt) {
                    dpt_res = tmp_res;
                }
                // dans tous les cas, la recherche sera effectuée dans les départements trouvés (conservés ou non).
                for (int i = 0; i < tmp_res.length; i++) {
                    departements.add(((GeocodageInverse_Departement) tmp_res[i]).getCodeDepartement());
                }
            }
        } else {
            departements.add(io.dpt);
        }

        // Tente un géocodage inverse pour chaque département
        for (int i = 0; i < departements.size(); i++) {
            ArrayList<GeocodageInverse> ondpt_res;
            ondpt_res = inverseSurDepartement(departements.get(i), application, services, operation, date, pos, dst, projection, io, connection);
            if (ondpt_res.size() > 0) {
                res.addAll(ondpt_res);
            }
            //Utilise le géocodage au département si demandé.
            if (dpt_res != null && serviceDpt) {
                res.add(dpt_res[i]);
            }
        }
        if (servicePays) {
            GeocodageInverse[] pay_res = inversePays(application, pos, dst, date, projection, connection);
            if (pay_res != null && pay_res.length > 0) {
                for (int j = 0; j < pay_res.length; j++) {
                    res.add(pay_res[j]);
                }
            }
        }
        // Formate le résultat.
        String[] resform = formateGeocodageInverse(res);
        return resform;
    }

    /**
     * Formate une liste de résultats de géocodage inverse.
     * 
     * @param gi
     * @return une liste qui représente un ensemble de solutions pour le géocodage inverse
     * de la forme {1,nb proposition,proposition 1,proposition 2, ...}
     * Pour chaque proposition:
     * code,ligne1,ligne2,...,ligne6,X,Y,t0,t1,distance
     * 
     * Le code indique la précision du reverse géocoding obtenue :
     * <ul>
     * <li>GestionInverse_PLAQUE</li>
     * <li>GestionInverse_INTERPOLATION_PLAQUE</li>
     * <li>GestionInverse_TRONCON</li>
     * <li>...</li>
     * </ul>
     */
    public String[] formateGeocodageInverse(ArrayList<GeocodageInverse> gi) throws ParseException {
        final int offset = 19; // 19 au lieu de 16
        String[] res = new String[2 + gi.size() * offset];

        res[0] = "1";
        res[1] = Integer.toString(gi.size());

        for (int i = 0; i < gi.size(); i++) {
            GeocodageInverse g = gi.get(i);
            res[2 + i * offset] = Integer.toString(g.precision);

            // lignes d'adresse
            res[3 + i * offset] = g.getLigne1();
            res[4 + i * offset] = g.getLigne2();
            res[5 + i * offset] = g.getLigne3();
            res[6 + i * offset] = g.getLigne4();
            res[7 + i * offset] = g.getLigne5();
            res[8 + i * offset] = g.getLigne6();
            Coordinate c = g.point.getCoordinate();
            if (g.getCentroide() != null) {
                final Geometry geometry = GeometryUtils.readGeometryFromWKT(g.getCentroide());
                c = geometry.getCoordinate();
            }
            res[9 + i * offset] = String.format(Locale.US, "%9.9g", c.x);
            res[10 + i * offset] = String.format(Locale.US, "%9.9g", c.y);
            res[11 + i * offset] = DateUtils.formatDateToString(g.t0, sdformat);
            res[12 + i * offset] = DateUtils.formatDateToString(g.t1, sdformat);

            final String distance = String.format(Locale.US, "%9.9g", g.distance);
            res[13 + i * offset] = distance;
            // Origine
            res[14 + i * offset] = g.getLigne4Origine();
            res[15 + i * offset] = g.getLigne6Origine();
            // IDS
            res[16 + i * offset] = g.getId();
            res[17 + i * offset] = g.getCodeInsee();
            // PAYS
            res[18 + i * offset] = g.getLigne7();
            res[19 + i * offset] = g.getLigne7Origine();
            res[20 + i * offset] = g.getSovA3();
        }
        return res;
    }

    
    
    /**
     * Reverse geocoding d'une commune à partir d'une position.
     * 
     * @param application
     * @param departement Permet de restreindre la recherche à un département.
     * @param pos
     * @param dst
     * @param date
     * @param io
     * @param connection
     * @return
     */
    public GeocodageInverse[] inverseCommune(int application, String departement, Point pos, double distance, Date date, int projection, InverseOption io, Connection connection) throws SQLException {
        double x = pos.getCoordinates()[0].x;
        double y = pos.getCoordinates()[0].y;
        // Projection WGS84
        final String pointExpression = getPointSpheroidExpression(new String[]{String.valueOf(x), String.valueOf(y)}, projection);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT com_communes.com_code_insee, com_communes.t0, com_communes.t1, " +
                "cdp_codes_postaux.t0, cdp_codes_postaux.t1, com_nom, com_nom_origine, cdp_code_postal, st_distance_spheroid(geometrie,");
        sb.append(pointExpression);
        sb.append(",'");
        sb.append(jdonrefParams.obtientSpheroidPardefaut());
        sb.append("') ");
        sb.append("as distance, st_astext(st_transform(st_closestpoint(geometrie, ");
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(x);
        sb.append(" ");
        sb.append(y);
        sb.append(")',");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("), ");
        sb.append(jdonrefParams.obtientProjectionSpheroidPardefaut());
        sb.append(")");
        sb.append("),");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append(")) FROM ");
        sb.append("com_communes JOIN cdp_codes_postaux ON com_communes.com_code_insee = cdp_codes_postaux.com_code_insee ");
        sb.append("WHERE cdp_codes_postaux.t1 > com_communes.t0 AND cdp_codes_postaux.t0 < com_communes.t1 AND (st_dwithin(geometrie,");  // POUR UTILISER L'INDEX DE LA COLONNE GEOMETRIE !!
        sb.append(pointExpression);
        sb.append(", ");
        sb.append(MiscUtils.distanceSpheroid(distance));
        sb.append(")) AND  st_distance_spheroid(geometrie,");
        sb.append(pointExpression);
        sb.append(",'");
        sb.append(jdonrefParams.obtientSpheroidPardefaut());
        sb.append("') ");
        sb.append(" < ");
        sb.append(distance);
        if (departement != null) {
            sb.append(" AND com_communes.dpt_code_departement = ?");
        }
        sb.append(" AND com_communes.t0 <= ? AND com_communes.t1 > ?");

        PreparedStatement ps = connection.prepareStatement(sb.toString());
        int index = 1;
        if (departement != null) {
            ps.setString(index++, departement);
        }
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        ps.setDate(index++, sqlDate);
        ps.setDate(index++, sqlDate);

        ResultSet rs = ps.executeQuery();

        // Collecte
        ArrayList<Object[]> coms = new ArrayList<Object[]>();
        while (rs.next()) // collecte
        {
            Object[] com = new Object[10];
            com[0] = rs.getString(1); // code insee
            com[1] = new Date(rs.getTimestamp(2).getTime());
            com[2] = new Date(rs.getTimestamp(3).getTime());
            com[3] = new Date(rs.getTimestamp(4).getTime());
            com[4] = new Date(rs.getTimestamp(5).getTime());
            com[5] = rs.getString(6); // nom
            com[6] = rs.getString(7); // nom origine
            com[7] = rs.getString(8); // code postal
            com[8] = new Double(rs.getDouble(9)); // distance
            com[9] = rs.getString(10); // centroïde
            coms.add(com);
        }
        ps.close();

        // Mise en forme
        GeocodageInverse[] res = new GeocodageInverse_Commune[coms.size()];

        for (int i = 0; i < coms.size(); i++) {
            GeocodageInverse_Commune gi = new GeocodageInverse_Commune(pos);
            Object[] com = coms.get(i);
            gi.code_insee = (String) com[0];
            long t0_com = ((Date) com[1]).getTime();
            long t1_com = ((Date) com[2]).getTime();
            long t0_cdp = ((Date) com[3]).getTime();
            long t1_cdp = ((Date) com[4]).getTime();
            if (t0_com < t0_cdp) {
                t0_com = t0_cdp;
            }
            gi.setT0(new Date(t0_com));
            if (t1_com > t1_cdp) {
                t1_com = t1_cdp;
            }
            gi.setT1(new Date(t1_com));
            gi.setDistance(Double.valueOf((Double) com[8]));
            gi.setCodePostal((String) com[7]);
            gi.setCommune((String) com[5]);
            gi.setCommuneOrigine((String) com[6]);
            gi.setCentroide((String) com[9]);
            res[i] = gi;
        }

        return res;
    }

    /**
     * Effectue un reverse geocoding du département.
     * 
     * Utilise les méthodes Postgis :
     * <ul>
     * <li>st_distance</li>
     * <li>st_geometryfromtext</li>
     * </ul>
     * 
     * @param application
     * @param position
     * @param distance distance entre la position et le département
     * @param date
     * @param io
     * 
     * @return tableau de départements
     * <ul>
     * <li>nb de résultats</li>
     * <li>code département</li>
     * <li>t0</li>
     * <li>t1</li>
     * </ul>
     */
    public GeocodageInverse[] inverseDepartement(int application, Point position, double distance, Date date, int projection, InverseOption io, Connection connection) throws SQLException {
        // Pas de possibilité d'injection car x y et distance sont des numériques.
        double x = position.getCoordinates()[0].x;
        double y = position.getCoordinates()[0].y;
        // Projection WGS84
        final String pointExpression = getPointSpheroidExpression(new String[]{String.valueOf(x), String.valueOf(y)}, projection);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT dpt_code_departement, t0, t1, st_distance_spheroid(geometrie,");
        sb.append(pointExpression);
        sb.append(",'");
        sb.append(jdonrefParams.obtientSpheroidPardefaut());
        sb.append("') ");
        sb.append("as distance, st_astext(st_transform(st_closestpoint(geometrie, ");
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(x);
        sb.append(" ");
        sb.append(y);
        sb.append(")',");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("), ");
        sb.append(jdonrefParams.obtientProjectionSpheroidPardefaut());
        sb.append(")");
        sb.append("),");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append(")) FROM ");
        sb.append("dpt_departements WHERE st_dwithin(geometrie,");  // POUR UTILISER L'INDEX DE LA COLONNE GEOMETRIE !!
        sb.append(pointExpression);
        sb.append(", ");
        sb.append(MiscUtils.distanceSpheroid(distance));
        sb.append(") AND st_distance_spheroid(geometrie,");
        sb.append(pointExpression);
        sb.append(",'");
        sb.append(jdonrefParams.obtientSpheroidPardefaut());
        sb.append("')");
        sb.append(" < ");
        sb.append(distance);
        sb.append(" AND dpt_departements.t0 <= ? and dpt_departements.t1 > ?");

        PreparedStatement ps = connection.prepareStatement(sb.toString());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        ps.setDate(1, sqlDate);
        ps.setDate(2, sqlDate);
        ResultSet rs = ps.executeQuery();

        // Collecte
        ArrayList<Object[]> dpts = new ArrayList<Object[]>();
        while (rs.next()) {
            Object[] dpt = new Object[5];
            dpt[0] = rs.getString(1);
            dpt[1] = new Date(rs.getTimestamp(2).getTime());
            dpt[2] = new Date(rs.getTimestamp(3).getTime());
            dpt[3] = new Double(rs.getDouble(4));
            dpt[4] = rs.getString(5);
            dpts.add(dpt);
        }
        ps.close();

        // Forme le résultat
        GeocodageInverse[] res = new GeocodageInverse_Departement[dpts.size()];
        for (int i = 0; i < dpts.size(); i++) {
            Object[] dpt = dpts.get(i);
            GeocodageInverse_Departement gi = new GeocodageInverse_Departement(position);
            gi.setCodeDepartement((String) dpt[0]);
            gi.setT0((Date) dpt[1]);
            gi.setT1((Date) dpt[2]);
            gi.setDistance(Double.valueOf((Double) dpt[3]));
            gi.setCentroide((String) dpt[4]);
            res[i] = gi;
        }

        return res;
    }

    public GeocodageInverse[] inversePays(int application, Point position, double distance, Date date, int projection, Connection connection) throws SQLException {
        // Pas de possibilité d'injection car x y et distance sont des numériques.
        double x = position.getCoordinates()[0].x;
        double y = position.getCoordinates()[0].y;
        // Projection WGS84
        final String pointExpression = getPointPaysExpression(new String[]{String.valueOf(x), String.valueOf(y)}, projection);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT pay_sov_a3, pay_nom_fr, pay_nom_origine, t0, t1, ");
        sb.append("st_distance_sphere(");
        sb.append("st_closestpoint(pay_geometrie, ");
        sb.append(pointExpression);
        sb.append("),");
        sb.append(pointExpression);
        sb.append(") as distance, st_astext(st_transform(st_closestpoint(pay_geometrie, ");
        sb.append(pointExpression);
        sb.append("),");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append(")) FROM pay_pays WHERE ");
        sb.append("st_distance_sphere(");
        sb.append("st_closestpoint(pay_geometrie, ");
        sb.append(pointExpression);
        sb.append("),");
        sb.append(pointExpression);
        sb.append(") < ");
        sb.append(distance);
        sb.append(" AND t0 <= ? and t1 > ?");

        PreparedStatement ps = connection.prepareStatement(sb.toString());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        ps.setDate(1, sqlDate);
        ps.setDate(2, sqlDate);
        ResultSet rs = ps.executeQuery();

        // Collecte
        ArrayList<Object[]> paysList = new ArrayList<Object[]>();
        while (rs.next()) {
            Object[] pays = new Object[7];
            pays[0] = rs.getString(1); // sova3
            pays[1] = rs.getString(2); // ligne7
            pays[2] = rs.getString(3); // ligne7 origine
            pays[3] = new Date(rs.getTimestamp(4).getTime()); // t0
            pays[4] = new Date(rs.getTimestamp(5).getTime()); // t1
            pays[5] = new Double(rs.getDouble(6)); // distance
            pays[6] = rs.getString(7); //point le plus proche
            paysList.add(pays);
        }
        ps.close();

        // Forme le résultat
        GeocodageInverse[] res = new GeocodageInverse_Pays[paysList.size()];
        for (int i = 0; i < paysList.size(); i++) {
            Object[] pays = paysList.get(i);
            GeocodageInverse_Pays gi = new GeocodageInverse_Pays(position);
            gi.setSovA3(pays[0].toString());
            gi.setLigne7(pays[1].toString());
            gi.setLigne7Origine(pays[2].toString());
            gi.setT0((Date) pays[3]);
            gi.setT1((Date) pays[4]);
            gi.setDistance(Double.valueOf((Double) pays[5]));
            gi.setCentroide((String) pays[6]);
            res[i] = gi;
        }

        return res;
    }

    public GeocodageInverse[] inversePlaque(int application, String dpt, Point position, double distance, Date date, int projection, InverseOption io, Connection connection) throws SQLException {
        // tables concernées
        String tableAdresse = "adr_adresses_" + dpt;
        // WA 09/2011 utilisation de getTroTronconsTableName
        // String tableVoie = "voi_voies_"+pays;
        String tableVoie = GestionTables.getVoiVoiesTableName(dpt);

        if (!GestionTables.tableExiste(tableAdresse, connection)) {
            return new GeocodageInverse_Plaque[0]; // Pas de table, pas de résultat.
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT st_srid(geometrie) FROM ");
        sb.append(tableAdresse);
        sb.append(" LIMIT 1");
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ResultSet rs = ps.executeQuery();
        String baseSrid = (rs.next()) ? String.valueOf(rs.getInt(1)) : jdonrefParams.obtientProjectionPardefaut();

        // Pas de possibilité d'injection car x y et distance sont des numériques.
        double x = position.getCoordinates()[0].x;
        double y = position.getCoordinates()[0].y;
        // LAMBERT
        final String pointExpression = getPointExpression(new String[]{String.valueOf(x), String.valueOf(y)}, projection, jdonrefParams.obtientProjectionPardefaut(), baseSrid);
        sb.delete(0, sb.length());
        sb.append("SELECT DISTINCT ON (voies.voi_id,voi_nom,adr_numero,adr_rep,cdp_code_postal) voies.voi_id, voies.t0, voies.t1, ");
        sb.append("st_distance(geometrie,");
        sb.append(pointExpression);
        sb.append(")");
        sb.append(" as distance,voi_nom,voi_nom_origine,voies.com_code_insee,cdp_code_postal,com_nom,com_nom_origine,adr_numero,adr_rep, ");
        sb.append("st_centroid(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append(")) ");
        sb.append(" FROM ");
        sb.append(tableAdresse);
        sb.append(" as adresses INNER JOIN ");
        sb.append(tableVoie);
        sb.append(" as voies ON voies.voi_id=adresses.voi_id INNER JOIN com_communes ON voies.com_code_insee = com_communes.com_code_insee");
        sb.append(" WHERE st_dwithin(geometrie,");
        sb.append(pointExpression);
        sb.append(", ");
        sb.append(distance);
        sb.append(") AND st_distance(geometrie,");
        sb.append(pointExpression);
        sb.append(")");
        sb.append(" < ");
        sb.append(distance);
        sb.append(" AND adresses.t0 < ? AND adresses.t1 > ? AND voies.t0 < ? AND voies.t1 > ? AND com_communes.t0 < ? AND com_communes.t1 > ?");

        ps = connection.prepareStatement(sb.toString());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        ps.setDate(1, sqlDate);
        ps.setDate(2, sqlDate);
        ps.setDate(3, sqlDate);
        ps.setDate(4, sqlDate);
        ps.setDate(5, sqlDate);
        ps.setDate(6, sqlDate);
        rs = ps.executeQuery();


        // Collecte
        ArrayList<Object[]> plas = new ArrayList<Object[]>();

        while (rs.next()) {
            Object[] pla = new Object[13];
            pla[0] = rs.getString(1);                        // voi_id
            pla[1] = new Date(rs.getTimestamp(2).getTime()); // t0
            pla[2] = new Date(rs.getTimestamp(3).getTime()); // t1
            pla[3] = new Double(rs.getDouble(4)); // distance
            pla[4] = rs.getString(5);  // voi nom
            pla[5] = rs.getString(6);  // voi nom origine
            pla[6] = rs.getString(7);  // com code insee
            pla[7] = rs.getString(8);  // cdp code postal
            pla[8] = rs.getString(9);  // com nom
            pla[9] = rs.getString(10); // com nom origine
            pla[10] = new Integer(rs.getInt(11));   // adr_numero
            pla[11] = rs.getString(12); // adr_rep
            pla[12] = rs.getString(13); // centroïde
            plas.add(pla);

        }
        rs.close();
        ps.close();

        // Forme le résultat
        GeocodageInverse[] res = new GeocodageInverse_Plaque[plas.size()];
        for (int i = 0; i < plas.size(); i++) {
            Object[] pla = plas.get(i);
            GeocodageInverse_Plaque gip = new GeocodageInverse_Plaque(position);
            gip.setCodeDepartement(dpt);
            gip.setCodeInsee((String) pla[6]);
            gip.setCodePostal((String) pla[7]);
            gip.setCommune((String) pla[8]);
            gip.setCommuneOrigine((String) pla[9]);
            gip.setDistance(((Double) pla[3]).doubleValue());
            gip.setT0((Date) pla[1]);
            gip.setT1((Date) pla[2]);
            gip.setId((String) pla[0]);
            gip.setNomVoie((String) pla[4]);
            gip.setNomVoieOrigine((String) pla[5]);
            gip.setNumero(((Integer) pla[10]).intValue());
            String rep = (String) pla[11];
            if (rep != null && rep.length() > 0) {
                gip.setRep(rep.charAt(0));
            } else {
                gip.setRep((char) 0);
            }
            gip.setCentroide((String) pla[12]);
            res[i] = gip;
        }
        return res;
    }

    /**
     * Géocodage inverse au niveau d'un tronçon.
     * 
     * Ne prend pas en compte les répétitions.
     * 
     * @param application numéro d'application appelante
     * @param pays         département concerné
     * @param position    position du géocodage inverse
     * @param distance    distance seuil de la recherche
     * @param date        date du géocodage inverse
     * @param io          options
     * @param connection  connection à la base
     * @return
     * @throws java.sql.SQLException problème durant l'accès à la base
     */
    public GeocodageInverse[] inverseTroncon(int application, String dpt, Point position, double distance, Date date, int projection, InverseOption io, Connection connection) throws SQLException, ParseException {
        // tables concernées
        // WA 09/2011 utilisation de getTroTronconsTableName
        // String tableVoie = "voi_voies_"+pays;
        String tableVoie = GestionTables.getVoiVoiesTableName(dpt);
        String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, dpt, date);
        final ArrayList<Object[]> troncons = findTroncons(connection, tableTroncons, position, projection, distance, date, tableVoie);
        final Geometry cercle = position.buffer(distance, 12);
        for (Object[] troncon : troncons) {
            interpolleNumerosTroncon(troncon, cercle);
        }
        final GeocodageInverse[] res = formatTroncon(troncons, position, dpt);

        return res;
    }
    
    public GeocodageInverse[] inverseTronconsIntersections(int application, InverseOption io, Connection connection, int projection, Date date) throws SQLException, ParseException {
        String tableVoie = GestionTables.getVoiVoiesTableName(io.dpt);
        String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, io.dpt, date);
        final ArrayList<Object[]> troncons = findTronconsIntersection(connection, tableTroncons, tableVoie, io.voi_id, projection, date);
        final GeocodageInverse[] res = formatTronconsIntersection(troncons, io.dpt);

        return res;
    }
    
    

    /**
     * Deux conseils : 
     * <ul>
     * <li>Mettre en place des index GIST sur les colonnes géometries des tables tro_troncons com_communes dpt_departement</li>
     * <li>Définir le paramètre random_page_cost à 1 dans postgresql.conf peut être utile pour forcer l'usage des index.</li>
     * </ul>
     * 
     * @param application
     * @param pays
     * @param position
     * @param distance
     * @param date
     * @param io
     * @param connection
     * @return
     * @throws java.sql.SQLException
     */
    public GeocodageInverse[] inverseVoie(int application, String dpt, Point position, double distance, Date date, int projection, InverseOption io, Connection connection) throws SQLException {
        // tables concernées
        // WA 09/2011 utilisation de getTroTronconsTableName
        // String tableVoie = "voi_voies_"+pays;
        String tableVoie = GestionTables.getVoiVoiesTableName(dpt);
        String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, dpt, date);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT st_srid(geometrie) FROM ");
        sb.append(tableTroncons);
        sb.append(" LIMIT 1");
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ResultSet rs = ps.executeQuery();
        String baseSrid = (rs.next()) ? String.valueOf(rs.getInt(1)) : jdonrefParams.obtientProjectionPardefaut();

        // Pas de possibilité d'injection car x y et distance sont des numériques.
        double x = position.getCoordinates()[0].x;
        double y = position.getCoordinates()[0].y;
        // LAMBERT
        final String pointExpression = getPointExpression(new String[]{String.valueOf(x), String.valueOf(y)}, projection, jdonrefParams.obtientProjectionPardefaut(), baseSrid);
        sb.delete(0, sb.length());
        sb.append("SELECT DISTINCT ON (voi_id,voi_nom,cdp_code_postal) voi_id, voies.t0, voies.t1, st_distance(troncons.geometrie,");
        sb.append(pointExpression);
        sb.append(")");
        sb.append(" as distance,voi_nom,voi_nom_origine,voies.com_code_insee,cdp_code_postal,com_nom,com_nom_origine, ");
        sb.append("st_astext(st_transform(st_closestpoint(troncons.geometrie, ");
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(x);
        sb.append(" ");
        sb.append(y);
        sb.append(")',");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("), ");
        sb.append(baseSrid);
        sb.append(")");
        sb.append("),");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append(")) FROM ");
        sb.append(tableTroncons);
        sb.append(" as troncons INNER JOIN ");
        sb.append(tableVoie);
        sb.append(" as voies ON voi_id=voi_id_droit OR voi_id=voi_id_gauche INNER JOIN com_communes ON voies.com_code_insee = com_communes.com_code_insee ");
        sb.append("WHERE st_dwithin(troncons.geometrie,");
        sb.append(pointExpression);
        sb.append(", ");
        sb.append(distance);
        sb.append(") AND st_distance(troncons.geometrie,");
        sb.append(pointExpression);
        sb.append(")");
        sb.append(" < ");
        sb.append(distance);
        sb.append(" AND troncons.t0 <= ? AND troncons.t1 > ? AND voies.t0 <= ? AND voies.t1 > ? AND com_communes.t0 <= ? AND com_communes.t1 > ? ");
        sb.append("ORDER BY voi_id, voi_nom, cdp_code_postal, distance");

        ps = connection.prepareStatement(sb.toString());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        ps.setDate(1, sqlDate);
        ps.setDate(2, sqlDate);
        ps.setDate(3, sqlDate);
        ps.setDate(4, sqlDate);
        ps.setDate(5, sqlDate);
        ps.setDate(6, sqlDate);

        rs = ps.executeQuery();

        // Collecte
        ArrayList<Object[]> tros = new ArrayList<Object[]>();
        while (rs.next()) {
            Object[] tro = new Object[11];
            tro[0] = rs.getString(1);                        // id
            tro[1] = new Date(rs.getTimestamp(2).getTime()); // t0
            tro[2] = new Date(rs.getTimestamp(3).getTime()); // t1
            tro[3] = new Double(rs.getDouble(4));            // distance
            tro[4] = rs.getString(5);                        // nom de voie
            tro[5] = rs.getString(6);                        // nom de voie d'origine
            tro[6] = rs.getString(7);                        // code insee
            tro[7] = rs.getString(8);                        // code postal
            tro[8] = rs.getString(9);                        // nom de commune
            tro[9] = rs.getString(10);                       // nom de commune d'origine
            tro[10] = rs.getString(11);                       // centroïde
            tros.add(tro);
        }
        rs.close();
        ps.close();

        // Formatage
        GeocodageInverse[] res = new GeocodageInverse_Voie[tros.size()];
        for (int i = 0; i < tros.size(); i++) {
            Object[] tro = tros.get(i);
            GeocodageInverse_Voie gi = new GeocodageInverse_Voie(position);
            gi.setCodeDepartement(dpt);
            gi.setId((String) tro[0]);
            gi.setT0((Date) tro[1]);
            gi.setT1((Date) tro[2]);
            gi.setDistance(Double.valueOf((Double) tro[3]));
            gi.setCodeInsee((String) tro[6]);
            gi.setCodePostal((String) tro[7]);
            gi.setCommune((String) tro[8]);
            gi.setCommuneOrigine((String) tro[9]);
            gi.setNomVoie((String) tro[4]);
            gi.setNomVoieOrigine((String) tro[5]);
            gi.setCentroide((String) tro[10]);
            res[i] = gi;
        }

        return res;
    }

    /**
     * Retourne la liste des numéros de voie issue de la projection d'un point sur une géométrie.
     * 
     * Ne tient pas compte du cas particulier où les deux numéros (début et fin) sont égaux à 0.
     * 
     * @param projection          la projection considérée
     * @param abs_numero_debut
     * @param numero_fin
     * @return
     */
    protected ArrayList<NumeroProjection> trouveNumeroProjection(PointProjection projection, int numero_debut, int numero_fin) {
        ArrayList<NumeroProjection> res = new ArrayList<NumeroProjection>();

        double totalLength = projection.totalLength;
        double currentLength = projection.distanceFromStartOfGeometry;
        // currentLength == 0           => abs_numero_debut
        // currentLength == totalLength => numero_fin

        // Gestion du sens de la voie.
        if (numero_debut > numero_fin) {
            int temp = numero_debut;
            numero_debut = numero_fin;
            numero_fin = temp;

            currentLength = totalLength - currentLength;
        }

        // cas particulier : le premier numéro est 0 ou les numéros de début et de fin sont égaux.
        if (numero_debut == 0 || numero_debut == numero_fin) {
            NumeroProjection numero1 = new NumeroProjection(projection);
            numero1.setNumero(numero_fin);
            numero1.setNumeroMin(numero_debut);
            numero1.setNumeroMax(numero_fin);
            res.add(numero1);
            return res;
        }

        double numeroCalcule = ((numero_fin - numero_debut) * currentLength) / totalLength + numero_debut;

        int inumero1 = (int) Math.ceil(numeroCalcule);
        int inumero2 = (int) Math.floor(numeroCalcule);

        if (numero_debut % 2 == numero_fin % 2) {
            if (inumero1 % 2 != numero_debut % 2) {
                inumero1--;
            }
            if (inumero2 % 2 != numero_fin % 2) {
                inumero2++;
            }
        }

        if (inumero1 >= numero_debut) {
            NumeroProjection numero1 = new NumeroProjection(projection);
            numero1.setNumero(inumero1);
            numero1.setNumeroMin(numero_debut);
            numero1.setNumeroMax(numero_fin);
            res.add(numero1);
        }
        if (inumero2 >= numero_debut) {
            NumeroProjection numero2 = new NumeroProjection(projection);
            numero2.setNumero(inumero2);
            numero2.setNumeroMin(numero_debut);
            numero2.setNumeroMax(numero_fin);
            res.add(numero2);
        }

        return res;
    }

    /**
     * Retourne la liste des projection du point spécifié sur la géométrie considérée.
     * @param geometrieStr La géométrie considérée
     * @param position  Le point spécifié.
     * @param distance  La distance tolérée (du reverse geocoding)
     * 
     * @return une liste de projections
     */
    protected ArrayList<PointProjection> trouvePointProjection(String geometrieStr, Point position, double distance) throws ParseException {
        ArrayList<PointProjection> res = new ArrayList<PointProjection>();
        Geometry g = GeometryUtils.readGeometryFromWKT(geometrieStr);
        Coordinate[] coordinates = g.getCoordinates();
        if (coordinates.length == 1) {
            Point projection = gf.createPoint(coordinates[0]);
            PointProjection pp = new PointProjection();
            pp.setGeometry(geometrieStr);
            pp.setOrigine(position);
            pp.setProjection(projection);
            pp.setDistanceBetweenPoints(position.distance(projection));
            pp.setDistanceFromStartOfGeometry(0);
            pp.setTotalLength(0);
            res.add(pp);
        } else {
            double currentTotalDistance = 0;
            Point lastPoint = gf.createPoint(coordinates[0]);
            Point currentPoint = null;
            for (int i = 1; i < coordinates.length; i++, lastPoint = currentPoint) {
                currentPoint = gf.createPoint(coordinates[i]);
                double currentDistance = currentPoint.distance(lastPoint);

                // Projète le point sur le segment en cours
                Coordinate[] tab = new Coordinate[2];
                tab[0] = coordinates[i - 1];
                tab[1] = coordinates[i];
                Geometry boutdetroncon = gf.createLineString(tab);
                Point projection = gf.createPoint(GestionGeocodage.projete(position, boutdetroncon));
                double projection_distance = projection.distance(lastPoint);

                // Vérifie d'abord que la projection fait partie du segment
                if (projection_distance + projection.distance(currentPoint) <= currentDistance) {
                    // Vérifie ensuite que la projection se situe dans l'intervalle de recherche
                    if (projection.distance(position) <= distance) {
                        // La projection est alors conservée comme possibilité.
                        PointProjection pp = new PointProjection();
                        pp.setGeometry(geometrieStr);
                        pp.setOrigine(position);
                        pp.setProjection(projection);
                        pp.setDistanceBetweenPoints(position.distance(projection));
                        pp.setDistanceFromStartOfGeometry(currentTotalDistance + projection_distance);
                        res.add(pp);
                    }
                }

                currentTotalDistance += currentPoint.distance(lastPoint);
            }
            // Attribue la longueur totale du segment trouvée.
            for (int i = 0; i < res.size(); i++) {
                res.get(i).setTotalLength(currentTotalDistance);
            }
        }

        return res;
    }

    private ArrayList<Object[]> findTroncons(Connection connection, String tableTroncons, Point position, int projection, double distance, Date date, String tableVoie) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT st_srid(geometrie) FROM ");
        sb.append(tableTroncons);
        sb.append(" LIMIT 1");
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ResultSet rs = ps.executeQuery();
        String baseSrid = (rs.next()) ? String.valueOf(rs.getInt(1)) : jdonrefParams.obtientProjectionPardefaut();

        // Pas de possibilite d'injection car x y et distance sont des numeriques.
        double x = position.getCoordinates()[0].x;
        double y = position.getCoordinates()[0].y;
        // LAMBERT
        final String pointExpression = getPointExpression(new String[]{String.valueOf(x), String.valueOf(y)}, projection, jdonrefParams.obtientProjectionPardefaut(), baseSrid);
        sb.delete(0, sb.length());
        sb.append("SELECT DISTINCT ON (tro_id,voi_id) voi_id_droit,voi_id_gauche, voies.t0, voies.t1, st_distance(troncons.geometrie,");
        sb.append(pointExpression);
        sb.append(") as distance,voi_nom,voi_nom_origine,voies.com_code_insee,cdp_code_postal,com_nom,com_nom_origine,");
        sb.append("astext(st_transform(troncons.geometrie, ");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append(")), ");
        sb.append("troncons.tro_numero_debut_droit,troncons.tro_numero_debut_gauche,troncons.tro_numero_fin_droit,troncons.tro_numero_fin_gauche,voi_id=voi_id_droit,voi_id=voi_id_gauche");
        sb.append(" FROM ");
        sb.append(tableTroncons);
        sb.append(" as troncons INNER JOIN ");
        sb.append(tableVoie);
        sb.append(" as voies ON voi_id=voi_id_droit OR voi_id=voi_id_gauche INNER JOIN com_communes ON voies.com_code_insee = com_communes.com_code_insee ");
        sb.append("WHERE  st_dwithin(troncons.geometrie, ");  // POUR UTILISER L'INDEX DE LA COLONNE GEOMETRIE !!
        sb.append(pointExpression);
        sb.append(", ");
        sb.append(distance);
        sb.append(") AND st_distance(troncons.geometrie,");
        sb.append(pointExpression);
        sb.append(") < ");
        sb.append(distance);
        sb.append(" AND troncons.t0 <= ? AND troncons.t1 > ? AND voies.t0 <= ? AND voies.t1 > ? AND com_communes.t0 <= ? AND com_communes.t1 > ?");

        String sb1 = sb.toString();
        ps = connection.prepareStatement(sb.toString());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        ps.setDate(1, sqlDate);
        ps.setDate(2, sqlDate);
        ps.setDate(3, sqlDate);
        ps.setDate(4, sqlDate);
        ps.setDate(5, sqlDate);
        ps.setDate(6, sqlDate);
        rs = ps.executeQuery();

        // Collecte
        ArrayList<Object[]> tros = new ArrayList<Object[]>();
        while (rs.next()) {
            boolean droitechoisie = false;
            int numero_debut_droit = new Integer(rs.getInt(13));
            int numero_fin_droit = new Integer(rs.getInt(15));
            int numero_debut_gauche = new Integer(rs.getInt(14));
            int numero_fin_gauche = new Integer(rs.getInt(16));

            // jointure avec voie droite
            if (rs.getBoolean(17) && (numero_debut_droit != 0 || numero_fin_droit != 0)) {
                Object[] tro = new Object[14];
                tro[0] = rs.getString(1); // id
                tro[1] = new Date(rs.getTimestamp(3).getTime()); // t0
                tro[2] = new Date(rs.getTimestamp(4).getTime()); // t1
                tro[3] = new Double(rs.getDouble(5)); // distance
                tro[4] = rs.getString(6); // nom de voie
                tro[5] = rs.getString(7); // nom de voie d'origine
                tro[6] = rs.getString(8); // code insee
                tro[7] = rs.getString(9); // code postal
                tro[8] = rs.getString(10); // nom de commune
                tro[9] = rs.getString(11); // nom de commune d'origine
                tro[10] = rs.getString(12); // geometrieStr au format texte
                tro[11] = numero_debut_droit; // numÃ©ro debut droit
                tro[12] = numero_fin_droit; // numÃ©ro fin droit
                tros.add(tro);
                droitechoisie = true;
            }

            // jointure avec voie gauche
            // condition : il ne s'agit pas de la meme voie que la droite
            if ((!droitechoisie || rs.getInt(13) != rs.getInt(14)) && rs.getBoolean(18) && (numero_debut_gauche != 0 || numero_fin_gauche != 0)) {
                Object[] tro = new Object[14];
                tro[0] = rs.getString(2); // id
                tro[1] = new Date(rs.getTimestamp(3).getTime()); // t0
                tro[2] = new Date(rs.getTimestamp(4).getTime()); // t1
                tro[3] = new Double(rs.getDouble(5)); // distance
                tro[4] = rs.getString(6); // nom de voie
                tro[5] = rs.getString(7); // nom de voie d'origine
                tro[6] = rs.getString(8); // code insee
                tro[7] = rs.getString(9); // code postal
                tro[8] = rs.getString(10); // nom de commune
                tro[9] = rs.getString(11); // nom de commune d'origine
                tro[10] = rs.getString(12); // geometrieStr au format texte
                tro[11] = numero_debut_gauche; // numÃ©ro debut gauche
                tro[12] = numero_fin_gauche; // numÃ©ro fin gauche
                tros.add(tro);
            }
        }
        rs.close();
        ps.close();

        return tros;
    }

    @SuppressWarnings(value = "unchecked")
    private GeocodageInverse[] formatTroncon(ArrayList<Object[]> tros, Point position, String dpt) {
        ////////////////////////////////////////
        // Formatage
        ////////////////////////////////////////
        List<GeocodageInverse> list = new ArrayList<GeocodageInverse>();

        for (Object[] tro : tros) {
            final Map<Integer, Point> points = (Map<Integer, Point>) tro[13];
            Set<Integer> numeros = points.keySet();
            for (Integer numero : numeros) {
                //NumeroProjection numero = points.get(j);
                final GeocodageInverse_Troncon gi = new GeocodageInverse_Troncon(position);
                gi.setCodeDepartement(dpt);
                gi.setId((String) tro[0]);
                gi.setT0((Date) tro[1]);
                gi.setT1((Date) tro[2]);
                gi.setCodeInsee((String) tro[6]);
                gi.setCodePostal((String) tro[7]);
                gi.setCommune((String) tro[8]);
                gi.setCommuneOrigine((String) tro[9]);
                gi.setNomVoie((String) tro[4]);
                gi.setNomVoieOrigine((String) tro[5]);
                //gi.setNumero(numero.calculerNumero());
                gi.setNumero(numero);
                gi.setRep((char) 0);
                gi.point = points.get(numero);
                gi.setDistance(gi.point.distance(position));
                list.add(gi);
            }
        }


        return list.toArray(new GeocodageInverse[list.size()]);
    }
    
    
    private ArrayList<Object[]> findTronconsIntersection(Connection connection, String tableTroncons, String tableVoie, String voi_id, int projection, Date date) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("Select distinct tro_id,voi_id_droit,voi_id_gauche, voies.t0, voies.t1, point,voi_nom,voi_nom_origine, voies.com_code_insee, cdp_code_postal, com_nom, com_nom_origine, " +
                "astext(st_transform(parent.geometrie, 2154)), tro_numero_debut_droit, tro_numero_debut_gauche, tro_numero_fin_droit, tro_numero_fin_gauche, voi_id=voi_id_droit, voi_id=voi_id_gauche ");
        sb.append("from ");
        sb.append("(SELECT parent.tro_id,parent.voi_id_droit, parent.voi_id_gauche, ");
        sb.append("astext(st_intersection(parent.geometrie,enfant.geometrie)) as point ,");
        sb.append("parent.geometrie,parent.tro_numero_debut_droit,parent.tro_numero_debut_gauche,parent.tro_numero_fin_droit,parent.tro_numero_fin_gauche ");
        sb.append("FROM ");
        sb.append("(select * from ");sb.append(tableTroncons);sb.append(" where ");
        sb.append("voi_id_droit = '");sb.append(voi_id); sb.append("' or voi_id_gauche = '");sb.append(voi_id);
        sb.append("' ) as enfant, ");
        sb.append(tableTroncons);sb.append(" as parent ");
        sb.append("Where st_distance(parent.geometrie,enfant.geometrie)=0) as parent ");
        sb.append("inner join ");sb.append(tableVoie);sb.append(" as voies on ( parent.voi_id_droit = voi_id or parent.voi_id_gauche = voi_id ) ");  
        sb.append("inner join com_communes ON voies.com_code_insee = com_communes.com_code_insee ");  
        sb.append("where not ( voi_id_droit = '");sb.append(voi_id);sb.append("' or voi_id_gauche='");sb.append(voi_id);
        sb.append("') order by tro_id ");
        
        String sb1 = sb.toString();
        
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ResultSet rs = ps.executeQuery();
        
//        String baseSrid = (rs.next()) ? String.valueOf(rs.getInt(1)) : jdonrefParams.obtientProjectionPardefaut();

        // Collecte
        ArrayList<Object[]> tros = new ArrayList<Object[]>();
        while (rs.next()) {
            Object[] tro = new Object[17];
            tro[0] = rs.getString(1); // id tr
            tro[1] = rs.getString(2); // id voied
            tro[2] = rs.getString(3); // id voieg
            tro[3] = new Date(rs.getTimestamp(4).getTime()); // t0
            tro[4] = new Date(rs.getTimestamp(5).getTime()); // t1
            tro[5] = rs.getString(6); // point intersection
            tro[6] = rs.getString(7); // nom de voie
            tro[7] = rs.getString(8); // nom de voie d'origine
            tro[8] = rs.getString(9); // code insee
            tro[9] = rs.getString(10); // code postal
            tro[10] = rs.getString(11); // nom de commune
            tro[11] = rs.getString(12); // nom de commune d'origine
            tro[12] = rs.getString(13); // geometrieStr au format texte
            tro[13] = rs.getInt(14); // numÃ©ro debut droit
            tro[14] = rs.getInt(15); // numÃ©ro fin droit
            tro[15] = rs.getInt(16); // numÃ©ro debut gauche
            tro[16] = rs.getInt(17); // numÃ©ro fin gauche

            tros.add(tro);
        }
        rs.close();
        ps.close();
        return tros;
    }
    
    @SuppressWarnings(value = "unchecked")
    private GeocodageInverse[] formatTronconsIntersection(ArrayList<Object[]> tros, String dpt) {
    ////////////////////////////////////////
    // Formatage
    ////////////////////////////////////////
        List<GeocodageInverse> list = new ArrayList<GeocodageInverse>();

        for (Object[] tro : tros) {
            final GeocodageInverse_Troncon gi = new GeocodageInverse_Troncon();
            gi.setCodeDepartement(dpt);
            gi.setIdTr((String) tro[0]);
            gi.setId((String) tro[1]);
            gi.setT0((Date) tro[3]);
            gi.setT1((Date) tro[4]);
            gi.setCodeInsee((String) tro[8]);
            gi.setCodePostal((String) tro[9]);
            gi.setCommune((String) tro[10]);
            gi.setCommuneOrigine((String) tro[11]);
            gi.setNomVoie((String) tro[6]);
            gi.setNomVoieOrigine((String) tro[7]);
            gi.setPointIntersecton((String) tro[5]);
            String[] coor = gi.getPointIntersecton().substring(gi.getPointIntersecton().indexOf("(")+1, gi.getPointIntersecton().lastIndexOf(")")).split(",");  
            //si getPointIntersecton()est un multipoint on retiens uniquement le premier point de la liste
            String[] position=coor[0].split(" ");
            
//            String[] position={"0.0","0.0"};
            String distance="0.0";
            double dst, x, y;
            dst = Double.parseDouble(distance);
            x = Double.parseDouble(position[0]);
            y = Double.parseDouble(position[1]);
            Coordinate c = new Coordinate(x, y);
            gi.point = gf.createPoint(c);
            gi.setDistance(dst);
            gi.setNumero(0);
            gi.setRep((char) 0);
            
            list.add(gi);
        }
        return list.toArray(new GeocodageInverse[list.size()]);
    }

    
    private void interpolleNumerosTroncon(Object[] troncon, Geometry cercle) throws ParseException {
        final String tronconStr = (String) troncon[10];
        int numero_debut = ((Integer) troncon[11]).intValue();
        int numero_fin = ((Integer) troncon[12]).intValue();
        if (numero_debut == 0) {
            numero_debut = -1; // parité
            troncon[11] = 1;
        }
        if (numero_fin == 0) {
            numero_fin = -1; // parité
            troncon[12] = 1;
        }
        final Geometry tronconGeo = GeometryUtils.readGeometryFromWKT(tronconStr);
        final Coordinate[] coordTroncon = tronconGeo.getCoordinates();
        double distanceDepuisDebutTroncon = 0;
        Map<Integer, Point> points = new TreeMap<Integer, Point>();
        for (int i = 0; i < coordTroncon.length - 1; i++) {
            final LineString segmentTroncon = gf.createLineString(new Coordinate[]{coordTroncon[i], coordTroncon[i + 1]});
            if (segmentTroncon.intersects(cercle)) {
                // Calcul des numeros
                final Geometry intersection = segmentTroncon.intersection(cercle);
                final double distanceAvantIntersection = calculerDistanceAvantIntersection(segmentTroncon, intersection);
                distanceDepuisDebutTroncon += distanceAvantIntersection;
                final double premierNumero = calculerNumero(distanceDepuisDebutTroncon, tronconGeo.getLength(), numero_debut, numero_fin);
                final double dernierNumero = calculerNumero(distanceDepuisDebutTroncon + intersection.getLength(), tronconGeo.getLength(), numero_debut, numero_fin);
                final Set<Integer> numeros = calculerSuiteNumeros(premierNumero, dernierNumero, numero_debut, numero_fin);

                // Clacul des points
                points.putAll(calculerPointsPourIntersection(numeros, intersection, tronconGeo.getLength(), distanceDepuisDebutTroncon, numero_debut, numero_fin));

                distanceDepuisDebutTroncon += intersection.getLength();
                distanceDepuisDebutTroncon = calculerDistanceApresIntersection(segmentTroncon, intersection);
            } else {
                distanceDepuisDebutTroncon += segmentTroncon.getLength();
            }
        }

        troncon[13] = points;
    }

    private Map<Integer, Point> calculerPointsPourIntersection(Set<Integer> numeros, Geometry intersection, double longueurTroncon, double distanceDebutIntersectionDepuisDebutTroncon, int numero_debut, int numero_fin) {
        final Map<Integer, Point> mapRet = new TreeMap<Integer, Point>();
        if (intersection instanceof LineString) {
            final LineString intersectionLine = (LineString) intersection;
            for (Integer numero : numeros) {
                mapRet.put(numero, calculerPoint(
                        distanceDebutIntersectionDepuisDebutTroncon,
                        longueurTroncon,
                        numero,
                        numero_debut,
                        numero_fin,
                        intersectionLine));
            }
        }

        return mapRet;
    }

    private Point calculerPoint(double distanceDebutIntersectionDepuisDebutTroncon, double longueurTroncon, int numero, int numero_debut, int numero_fin, LineString intersectionLine) {
        final Point start = intersectionLine.getStartPoint();
        final Point end = intersectionLine.getEndPoint();
        final double x0 = start.getX();
        final double y0 = start.getY();
        final double x1 = end.getX();
        final double y1 = end.getY();
        final double distanceAuPointDepuisDebutTroncon = calculerDistance(longueurTroncon, numero, numero_debut, numero_fin);
        final double l = distanceAuPointDepuisDebutTroncon - distanceDebutIntersectionDepuisDebutTroncon;
        final double L = intersectionLine.getLength();
        double px = x0;
        double py = y0;
        if (x0 == x1 && y0 != y1) { // segment vertical
            py = ((l * (y1 - y0)) / L) + y0;
        } else if (x0 != x1 && y0 == y1) { // segment horizontal
            px = ((l * (x1 - x0)) / L) + x0;
        } else if (x0 != x1 && y0 != y1) {
            px = ((l * (x1 - x0)) / L) + x0;
            py = ((l * (y1 - y0)) / L) + y0;
        }

        return gf.createPoint(new Coordinate(px, py));
    }

    private double calculerNumero(double distanceDepuisDebutTroncon, double longueurTroncon, int numero_debut, int numero_fin) {
        final int abs_numero_debut = Math.abs(numero_debut);
        final int abs_numero_fin = Math.abs(numero_fin);

        return abs_numero_debut + ((distanceDepuisDebutTroncon * (abs_numero_fin - abs_numero_debut)) / longueurTroncon);
    }

    private double calculerDistance(double longueurTroncon, int numero, int numero_debut, int numero_fin) {
        final int abs_numero_debut = Math.abs(numero_debut);
        final int abs_numero_fin = Math.abs(numero_fin);
        final double doubleRet = ((numero - abs_numero_debut) * longueurTroncon) / (abs_numero_fin - abs_numero_debut);

        return doubleRet;
    }

    private Set<Integer> calculerSuiteNumeros(double premierNumero, double dernierNumero, int numero_debut, int numero_fin) {
        final Set<Integer> setRet = new HashSet<Integer>();
        final Double doubleMin = (premierNumero <= dernierNumero) ? premierNumero : dernierNumero;
        final Double doubleMax = (premierNumero <= dernierNumero) ? dernierNumero : premierNumero;
        final int numeroMin = (doubleMin - doubleMin.intValue() > 0) ? doubleMin.intValue() + 1 : doubleMin.intValue();
        final int numeroMax = doubleMax.intValue();
        final int parite = getPariteTroncon(numero_debut, numero_fin);
        for (int num = numeroMin; num <= numeroMax; num++) {
            switch (parite) {
                case 0:
                    setRet.add(num);
                    break;
                case 1:
                    if (num % 2 == 1) {
                        setRet.add(num);
                    }
                    break;
                case 2:
                    if (num % 2 == 0) {
                        setRet.add(num);
                    }
                    break;
            }
        }

        return setRet;
    }

    private int getPariteTroncon(int numero_debut, int numero_fin) {
        int intRet = 0;
        if (numero_debut > 0 && numero_fin > 0) {
            if (numero_debut % 2 == 0 && numero_fin % 2 == 0) {//tronçon avec numéros pairs
                intRet = 2;
            } else if (numero_debut % 2 == 1 && numero_fin % 2 == 1) { //tronçon avec numéros impairs
                intRet = 1;
            }
        }

        return intRet;
    }

    private double calculerDistanceAvantIntersection(LineString segmentLine, Geometry intersection) {
        double doubleRet = 0;
        final Coordinate[] coordIntersection = intersection.getCoordinates();
        final LineString intersectionLine = gf.createLineString(coordIntersection);
        final Point startSegment = segmentLine.getStartPoint();
        final Point startIntersection = intersectionLine.getStartPoint();
        doubleRet = startSegment.distance(startIntersection);

        return doubleRet;
    }

    private double calculerDistanceApresIntersection(LineString segmentLine, Geometry intersection) {
        double doubleRet = 0;
        final Coordinate[] coordIntersection = intersection.getCoordinates();
        final LineString intersectionLine = gf.createLineString(coordIntersection);
        final Point endSegment = segmentLine.getEndPoint();
        final Point endIntersection = intersectionLine.getEndPoint();
        doubleRet = endIntersection.distance(endSegment);

        return doubleRet;
    }

    // Projection
    // defaultSrid et baseSrid sont différents pour les pays.
    private String getPointExpression(String[] position, int projection, String defaultSrid, String baseSrid) {
        final StringBuilder sb = new StringBuilder();
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(position[0]);
        sb.append(" ");
        sb.append(position[1]);
        sb.append(")',");
        sb.append((projection != 0) ? projection : defaultSrid);
        sb.append("), ");
        sb.append(baseSrid);
        sb.append(")");

        return sb.toString();
    }

    private String getPointPaysExpression(String[] position, int projection) {
        final StringBuilder sb = new StringBuilder();
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(position[0]);
        sb.append(" ");
        sb.append(position[1]);
        sb.append(")',");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPaysParDefaut());
        sb.append("), ");
        sb.append(jdonrefParams.obtientProjectionPaysParDefaut());
        sb.append(")");

        return sb.toString();
    }

    private String getPointSpheroidExpression(String[] position, int projection) {
        final StringBuilder sb = new StringBuilder();
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(position[0]);
        sb.append(" ");
        sb.append(position[1]);
        sb.append(")',");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("), ");
        sb.append(jdonrefParams.obtientProjectionSpheroidPardefaut());
        sb.append(")");

        return sb.toString();
    }
}
