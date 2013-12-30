/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jpoizonref;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import ppol.jdonref.GestionLogs;
import ppol.jdonref.GestionMail;
import ppol.jdonref.JDONREFv3Exception;
import ppol.jdonref.dao.PoizonBean;
import ppol.jdonref.poizon.GestionPoizon;
import ppol.jdonref.utils.DateUtils;
import ppol.jdonref.wservice.IJDONREFv3;
import ppol.jdonref.wservice.PropositionVersion;
import ppol.jdonref.wservice.ResultatContacte;
import ppol.jdonref.wservice.ResultatDecoupage;
import ppol.jdonref.wservice.ResultatErreur;
import ppol.jdonref.wservice.ResultatGeocodage;
import ppol.jdonref.wservice.ResultatGeocodageInverse;
import ppol.jdonref.wservice.ResultatNormalisation;
import ppol.jdonref.wservice.ResultatRevalidation;
import ppol.jdonref.wservice.ResultatValidation;
import ppol.jdonref.wservice.ResultatVersion;

/**
 *
 * @author marcanhe
 */
@WebService(name = "JDONREF", portName = "JDONREFPort", targetNamespace = "http://jdonref.ppol/", serviceName = "JDONREFService")
public class JPOIZONREF implements IJDONREFv3 {

    @Resource
    private WebServiceContext context;
    private static ServletContext servletContext;
    private JDONREFv3Lib jdonrefv3lib;
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;

    public JPOIZONREF() {
        //jdonrefv3lib = JDONREFv3Lib.getInstance();
    }

    public static ServletContext getContext(WebServiceContext context) {
        if (servletContext == null) {
            servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        }
        return servletContext;
    }

    // FONCTION QUI CHARGE LE FICHIER DE CONFIGURATION
    public JDONREFv3Lib chargeConf(JDONREFv3Lib lib) {
        if (lib == null) {
            lib = JDONREFv3Lib.getInstance(getContext(context).getInitParameter("file"));
        }
        return lib;
    }

    /**
     * Effectue les opérations de normalisation demandées.
     * @param operation une combinaison de bits :
     * <ul>
     *     <li>1 pour la première passe de normalisation</li>
     *     <li>2 pour la restructuration</li>
     *     <li>4 pour la deuxième passe de normalisation</li>
     *     <li>8 pour activer la réduction à 38 caractère durant la deuxième passe de normalisation</li>
     *     <li>16 pour retourner l'équivalent phonétique de chaque ligne</li>
     *     <li>32 pour activer la désabbréviation durant la deuxième passe de normalisation</li>
     *     <li>64 pour retourne l'équivalent sans articles</li>
     *     <li>128 pour que la restructuration retourne les numéros de départements présumés de l'adresse (dernière ligne, séparés par des virgules)</li>
     * </ul>
     * @param departements la liste des départements dans laquelle effectuer la deuxième passe de normalisation (séparés par des , sans espaces)
     * Ce paramètre peut être défini à null si le code de département est spécifié dans la ligne 6 ou si la restructuration est
     * effectuée avant. Sinon, les départements par défaut seront choisis.
     *
     * Attention, une deuxième passe de normalisation sur une adresse non normalisée (passe 1) et non structurée
     * peut donner des résultats innatendus.
     */
    @WebMethod(operationName = "normalise")
    public ResultatNormalisation normalise(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "options") String[] options) {


        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // VERIFICATION DES DONNEES
        if (donnees == null || donnees.length < 1 || donnees[0] == null || donnees[0].trim().equals("")) {
            GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_ERREUR, false);
            final ResultatNormalisation resultatRet = new ResultatNormalisation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // EXECUTION DU SERVICE
        final GestionPoizon service = jdonrefv3lib.getGestionPoizon();
        final ResultatNormalisation resultatRet = ResultAdapter.adapteNormalise(service.normalise(services, operation, donnees));
        GestionLogs.getInstance().logNormalisation(application, operation, true);

        return resultatRet;

    }

    /**
     * Valide en normalisant au préalable si nécessaire.
     * @param operation une combinaison de bits :
     * <ul><li>1 pour la première passe de normalisation</li>
     *     <li>2 pour la restructuration</li>
     *     <li>4 pour la deuxième passe de normalisation</li>
     *     <li>8 pour activer la réduction à 38 caractères durant la deuxième passe de normalisation.</li>
     *     <li>32 pour activer la désabbréviation</li>
     *     <li>64 pour supprimer les articles</li>
     *     <li>128 pour que la restructuration retourne les numéros de départements présumés de l'adresse (dernière ligne, séparés par des virgules)</li>
     * </ul>
     * @param departements la liste des départements dans laquelle effectuer la deuxième passe de normalisation (séparés par des , sans espaces)
     * Ce paramètre peut être défini à null si le code de département est spécifié dans la ligne 6 ou si la restructuration est
     * effectuée avant. Sinon, les départements par défaut seront choisis.
     * @return voir GestionValidation pour les valeurs de retour.
     */
    @WebMethod(operationName = "valide")
    @RequestWrapper(className = "ppol.valide")
    @ResponseWrapper(className = "ppol.valideResponse")
    public ResultatValidation valide(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "ids") String[] ids,
            @WebParam(name = "options") String[] options) {


        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // VERIFICATION DES DONNEES
        if (donnees == null || donnees.length < 1 || donnees[0] == null || donnees[0].trim().equals("")) {
            GestionLogs.getInstance().logValidation(application, "", GestionLogs.FLAG_VALIDE_ERREUR, false);
            final ResultatValidation resultatRet = new ResultatValidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // TRAITEMENT DES IDS
        if (ids == null) {
            ids = new String[0];
        }

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        String forceOption = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("date")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
                if (tokens[0].trim().equalsIgnoreCase("force")) {
                    forceOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        // Date
        final Calendar calendar = GregorianCalendar.getInstance();
        Date date = calendar.getTime(); // Valeur par défaut     

        if (!dateOption.trim().equals("")) {
            try {
                date = DateUtils.parseStringToDate(dateOption, sdformat);
            } catch (ParseException pe) {
                GestionLogs.getInstance().logValidation(application, "", GestionLogs.FLAG_VALIDE_ERREUR, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de la date n'est pas valide.", pe);
                final ResultatValidation resultatRet = new ResultatValidation();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de la date n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;
            }
        }
        boolean force = false;
        if (!forceOption.trim().equals("")) {
            try {
                force = Boolean.parseBoolean(forceOption);
            } catch (Exception ex) {
                GestionLogs.getInstance().logValidation(application, "", GestionLogs.FLAG_VALIDE_ERREUR, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de l'option force n'est pas valide.", ex);
                final ResultatValidation resultatRet = new ResultatValidation();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de l'option force n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;
            }
        }

        // EXECUTION DU SERVICE
        final GestionPoizon service = jdonrefv3lib.getGestionPoizon();
        final List<PoizonBean> list = new ArrayList<PoizonBean>();
        try {
            list.addAll(service.valide(services, operation, donnees, ids, force, date));
        } catch (JDONREFv3Exception jde) {
            GestionLogs.getInstance().logValidation(application, "", GestionLogs.FLAG_VALIDE_ERREUR, false);
            Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, jde.getMessage(), jde);
            final ResultatValidation resultatRet = new ResultatValidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        GestionLogs.getInstance().logValidation(application, "", GestionLogs.FLAG_VALIDE_POIZON, true);
        final ResultatValidation resultatRet = ResultAdapter.adapteValide(list);

        return resultatRet;
    }

    /**
     * Géocode l'adresse valide spécifiée.
     * @return voir GestionReferentiel.geocode et GestionValidation.
     */
    @WebMethod(operationName = "geocode")
    public ResultatGeocodage geocode(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "ids") String[] ids,
            @WebParam(name = "options") String[] options) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // VERIFICATION DES DONNEES
        if (donnees == null || donnees.length < 1 || donnees[0] == null || donnees[0].trim().equals("")) {
            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        if (ids == null || ids.length < 1 || ids[0] == null || ids[0].trim().equals("")) {
            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre ids est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        String projectionOption = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("date")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("projection")) {
                    projectionOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        // Date
        final Calendar calendar = GregorianCalendar.getInstance();
        Date date = calendar.getTime(); // Valeur par défaut      

        if (!dateOption.trim().equals("")) {
            try {
                date = DateUtils.parseStringToDate(dateOption, sdformat);
            } catch (ParseException pe) {
                GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de la date n'est pas valide.", pe);
                final ResultatGeocodage resultatRet = new ResultatGeocodage();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de la date n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;
            }
        }
        // Projection
        int projection = 0; // Valeur par défaut

        if (!projectionOption.trim().equals("")) {
            try {
                projection = Integer.parseInt(projectionOption);
            } catch (NumberFormatException nfe) {
                GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "La projection n'est pas un nombre.", nfe);
                final ResultatGeocodage resultatRet = new ResultatGeocodage();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("La projection n'est pas un nombre.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;

            }
        }

        // EXECUTION DU SERVICE
        final GestionPoizon service = jdonrefv3lib.getGestionPoizon();
        final List<PoizonBean> list = new ArrayList<PoizonBean>();
        try {
            list.addAll(service.geocode(services, donnees, ids, date, projection));
        } catch (JDONREFv3Exception jde) {
            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, jde.getMessage(), jde);
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;

        }
        final ResultatGeocodage resultatRet = ResultAdapter.adapteGeocode(list);
        GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_POIZON, true);

        return resultatRet;
    }

    /**
     * Revalide une adresse validée au préalable.
     */
    @WebMethod(operationName = "revalide")
    public ResultatRevalidation revalide(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "ids") String[] ids,
            @WebParam(name = "date") String date,
            @WebParam(name = "options") String[] options) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // VERIFICATION DES IDS
        if (ids == null || ids.length < 1 || ids[0] == null || ids[0].trim().equals("")) {
            GestionLogs.getInstance().logRevalidation(application, false);
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre ids est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // VERIFICATION DE LA DATE
        Date dateParam = null;
        if (date != null && !date.trim().equals("")) {
            try {
                dateParam = DateUtils.parseStringToDate(date, sdformat);
            } catch (ParseException pe) {
                GestionLogs.getInstance().logRevalidation(application, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de la date n'est pas valide.", pe);
                final ResultatRevalidation resultatRet = new ResultatRevalidation();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de la date n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;
            }
        } else {
            GestionLogs.getInstance().logRevalidation(application, false);
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre date est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;

        }

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("date")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        // Date optionnelle
        Date dateOptionnelle = null;
        if (!dateOption.trim().equals("")) {
            try {
                dateOptionnelle = DateUtils.parseStringToDate(dateOption, sdformat);
            } catch (ParseException pe) {
                GestionLogs.getInstance().logRevalidation(application, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de la date optionnelle n'est pas valide.", pe);
                final ResultatRevalidation resultatRet = new ResultatRevalidation();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de la date optionnelle n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;
            }
        }


        // EXECUTION DU SERVICE
        final GestionPoizon service = jdonrefv3lib.getGestionPoizon();
        final List<PoizonBean> list = new ArrayList<PoizonBean>();
        try {
            list.addAll(service.revalide(services, ids, dateParam, dateOptionnelle));
        } catch (JDONREFv3Exception jde) {
            GestionLogs.getInstance().logRevalidation(application, false);
            Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, jde.getMessage(), jde);
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;

        }
        final ResultatRevalidation resultatRet = ResultAdapter.adapteRevalide(list);
        GestionLogs.getInstance().logRevalidation(application, true);

        return resultatRet;
    }

    /**
     * Effectue un géocodage inverse des coordonnées spécifiées.
     */
    @WebMethod(operationName = "reverse")
    public ResultatGeocodageInverse inverse(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "distance") double distance,
            @WebParam(name = "options") String[] options) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // TRAITEMENT DES DONNEES
        String[] position = new String[donnees.length];
        for (int i = 0; i <
                donnees.length; i++) {
            position[i] = donnees[i];
        }

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        String projectionOption = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("date")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("projection")) {
                    projectionOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        // Date
        final Calendar calendar = GregorianCalendar.getInstance();
        Date date = calendar.getTime(); // Valeur par défaut    

        if (!dateOption.trim().equals("")) {
            try {
                date = DateUtils.parseStringToDate(dateOption, sdformat);
            } catch (ParseException pe) {
                GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de la date n'est pas valide.", pe);
                final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de la date n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;
            }
        }
        // Projection
        int projection = 0; // Valeur par défaut

        if (!projectionOption.trim().equals("")) {
            try {
                projection = Integer.parseInt(projectionOption);
            } catch (NumberFormatException nfe) {
                GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Le format de la projection n'est pas valide.", nfe);
                final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(5);
                erreur.setMessage("Le format de la projection n'est pas valide.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});

                return resultatRet;

            }
        }

        // EXECUTION DU SERVICE
        final GestionPoizon service = jdonrefv3lib.getGestionPoizon();
        final List<PoizonBean> list = new ArrayList<PoizonBean>();
        try {
            list.addAll(service.geocodeInverse(services, position, distance, date, projection));
        } catch (JDONREFv3Exception jde) {
            GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
            Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, jde.getMessage(), jde);
            final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;

        }
        final ResultatGeocodageInverse resultatRet = ResultAdapter.adapteGeocodeInverse(list);
        GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_POIZON, true);

        return resultatRet;
    }

    /**
     * Découpe l'adresse en les différents éléments qui la compose.
     * Les éléments du paramètre nature sont une combinaison des flags suivants:
     * <ul>
     * <li>1 pour numero</li>
     * <li>2 pour repetition</li>
     * <li>4 pour autres numeros</li>
     * <li>8 pour type de voie</li>
     * <li>16 pour article</li>
     * <li>32 pour libelle</li>
     * <li>64 pour code postal</li>
     * <li>128 pour commune</li>
     * <li>256 pour numero d'arrondissement</li>
     * <li>512 pour cedex</li>
     * <li>1024 pour le code cedex</li>
     * <li>2048 pour ligne1</li>
     * <li>4096 pour ligne2</li>
     * <li>8192 pour ligne3</li>
     * <li>16384 pour ligne5</li>
     * </ul>
     * @param lignes les lignes à découper.
     * @param natures la nature des informations à obtenir.
     * @param numeros les numéros des lignes à découper. Si numeros est null, une restrucuration est effectuée.
     */
    @WebMethod(operationName = "decoupe")
    public ResultatDecoupage decoupe(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "operations") int[] operations,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "options") String[] options) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // TRAITEMENT DES DONNEES
        if (donnees == null || donnees.length < 1 || donnees[0] == null || donnees[0].trim().equals("")) {
            GestionLogs.getInstance().logDecoupage(application, false);
            final ResultatDecoupage resultatRet = new ResultatDecoupage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // EXECUTION DU SERVICE
        final GestionPoizon service = jdonrefv3lib.getGestionPoizon();
        final List<PoizonBean> list = new ArrayList<PoizonBean>();
        try {
            list.addAll(service.decoupe(services, operations, donnees));
        } catch (JDONREFv3Exception jde) {
            GestionLogs.getInstance().logDecoupage(application, false);
            Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, jde.getMessage(), jde);
            final ResultatDecoupage resultatRet = new ResultatDecoupage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        GestionLogs.getInstance().logDecoupage(application, true);
        final ResultatDecoupage resultatRet = ResultAdapter.adapteDecoupe(list);

        return resultatRet;
    }

    /**
     * Permet de signaler un défaut à l'administrateur.
     */
    @WebMethod(operationName = "contacte")
    public ResultatContacte contacte(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services,
            @WebParam(name = "operation") int operation,
            @WebParam(name = "donnees") String[] donnees,
            @WebParam(name = "options") String[] options) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // TRAITEMENT DES OPTIONS
        String emetteur = "";
        String titre = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("titre")) {
                    titre = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("emetteur")) {
                    emetteur = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        // EXECUTION DU SERVICE
        final ResultatContacte resultatRet = new ResultatContacte();
        final GestionMail gm = jdonrefv3lib.getGestionMail();
        if (gm == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Une requête de contact a été envoyé à l'administrateur de JDONREF sans succès :");
            sb.append("application=");
            sb.append(application);
            sb.append(";titre=");
            sb.append(titre);
            for (int i = 0; i < donnees.length; i++) {
                sb.append(";ligne" + i + "=");
                sb.append(donnees[i]);
            }
            GestionLogs.getInstance().logContacte(application, false);
            Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, sb.toString());
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Les paramètres de contact de l'administrateur n'ont pas été initialisés.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            try {
                gm.envoieMail(emetteur, titre, String.valueOf(application), donnees);
                GestionLogs.getInstance().logContacte(application, true);
                resultatRet.setCodeRetour(1);
            } catch (MessagingException ex) {
                GestionLogs.getInstance().logContacte(application, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Un problème a été rencontré durant l'envoi d'un mail à l'administrateur de JDONREF.", ex);
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(0);
                erreur.setMessage("Un problème a été rencontré durant l'envoi d'un mail à l'administrateur de JDONREF.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});
            } catch (Exception ex) {
                GestionLogs.getInstance().logContacte(application, false);
                Logger.getLogger(JPOIZONREF.class.getName()).log(Level.SEVERE, "Un problème inconnu a été rencontré", ex);
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(0);
                erreur.setMessage("Une erreur non répertoriée est survenue durant l'envoi d'un mail à l'administrateur.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});
            }
        }

        return resultatRet;
    }

    /**
     * Obtient la version de JDONREF.
     */
    @WebMethod(operationName = "getVersion")
    public ResultatVersion getVersion(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // EXECUTION DU SERVICE
        final ResultatVersion resultatRet = new ResultatVersion();
        resultatRet.setCodeRetour(1);
        final PropositionVersion proposition = new PropositionVersion();
        proposition.setNom("Jdonref");
        proposition.setVersion(jdonrefv3lib.getParams().getVersion());
        resultatRet.setPropositions(new PropositionVersion[]{proposition});
        GestionLogs.getInstance().logVersion(application);

        return resultatRet;
    }
}
