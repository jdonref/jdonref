/**
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
package ppol.jdonrefv3v4;

import ppol.jdonref.referentiel.JDONREFv3Lib;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import ppol.jdonref.*;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import ppol.jdonref.referentiel.GestionAdr;
import ppol.jdonref.referentiel.GestionReferentiel;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;
import ppol.jdonref.utils.MiscUtils;
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
 * Service web JDONREFv3v4.<br>
 * Cette classe est utilisée pour générer le proxy de l'interface SOAP de JDONREFv3v4.<br>
 * En cas d'erreur, le code de retour utilise la codification suivante:
 * <ul>
 * <li>Code 1 = GestionMots non initialisé.</li>
 * <li>Code 2 = GestionConnection non initialisé.</li>
 * <li>Code 3 = Problème SQL durant la normalisation.</li>
 * <li>Code 4 = Le service web ne répond pas.</li>
 * <li>Code 5 = Un paramètre a été mal formaté.</li>
 * <li>Code 6 = Problème d'encodage (requete ou client).</li>
 * <li>Code 7 = Erreur inconnue</li>
 * <li>Code 8 = L'adresse fournie n'est pas correcte.</li>
 * <li>Code 9 = Pas encore implémenté</li>
 * <li>Code 10 = Erreur dans le référentiel</li>
 * <li>Code 11 = Erreur liée aux paramètres et au référentiel.</li>
 * <li>Code 12 = Le processus est déjà en cour d'exécution.</li>
 * </ul>
 * @author jmoquet
 */
@WebService(name = "JDONREF", portName = "JDONREFPort", targetNamespace = "http://jdonref.ppol/", serviceName = "JDONREFService")
public class JDONREFv3v4 implements IJDONREFv3 {

    @Resource
    private WebServiceContext context;
    private static ServletContext servletContext;
    private JDONREFv3Lib jdonrefv3lib;
    JDONREFParams params = null;


    /**
     * Constructeur du service web.
     */
    public JDONREFv3v4() {
        //jdonrefv3lib = JDONREFv3Lib.getInstance();
    }

    public static ServletContext getContext(WebServiceContext context) {
        if (servletContext == null) {
            MessageContext ms = context.getMessageContext();
            Object obj = ms.get(MessageContext.SERVLET_CONTEXT);
            servletContext = (ServletContext) obj;

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
//        if (jdonrefv3lib == null) {
//            jdonrefv3lib = JDONREFv3Lib.getInstance(getContext(context).getInitParameter("file"));
//        }
        jdonrefv3lib = chargeConf(jdonrefv3lib);
        params = jdonrefv3lib.getParams();

        

        // TRAITEMENT DES DONNEES
        if (donnees == null || donnees.length < 1) {
//            GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_ERREUR, false);
            params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_ERREUR, false);
            final ResultatNormalisation resultatRet = new ResultatNormalisation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        } else {
            final int size = (donnees.length > 6) ? 7 : 6;
            final List<String> donneesTemp = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                donneesTemp.add((donnees[i] != null && donnees[i].trim().length() > 0) ? donnees[i] : "");
            }
            donnees = donneesTemp.toArray(new String[size]);
        }

        // Les opérations superflues sont ignorées.
        if ((operation & 8) != 0) {
            operation -= 8;
        }
        if ((operation & 32) != 0) {
            operation -= 32;
        }
        if ((operation & 128) != 0) {
            operation -= 128;
        }
        if ((operation & 256) != 0) {
            operation -= 256;
        }

        // TRAITEMENT DES OPTIONS
        String dpt = "";
        boolean pays = false;
        boolean getDpt = false;
        boolean desabreviation = false;
        boolean reduction38 = false;
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("dpt")) {
                    dpt = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("pays")) {
                    pays = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                } else if (tokens[0].trim().equalsIgnoreCase("getdpt")) {
                    getDpt = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                } else if (tokens[0].trim().equalsIgnoreCase("desabreviation")) {
                    desabreviation = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                } else if (tokens[0].trim().equalsIgnoreCase("reduction38")) {
                    reduction38 = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                }
            }
        }

        if (pays) {
            operation |= 256;
        }

        if ((operation & 2) != 0 && getDpt) {
            operation |= 128;
        }

        if ((operation & 4) != 0 && reduction38) {
            operation |= 8;
        }

        if ((operation & 4) != 0 && desabreviation) {
            operation |= 32;
        }

        // EXECUTION DU SERVICE
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final String[] result = gestionAdr.normalise(application, operation, donnees, dpt);

        return ResultAdapter.adapteNormalise(result, operation);
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
        params = jdonrefv3lib.getParams();

        // VERIFICATION DES DONNEES
        if (donnees == null || donnees.length < 1) {
            params.getGestionLog().logValidation(application, "", AGestionLogs.FLAG_VALIDE_ERREUR, false);
            final ResultatValidation resultatRet = new ResultatValidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        } else {
            final int size = (donnees.length > 6) ? 7 : 6;
            final List<String> donneesTemp = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                donneesTemp.add((donnees[i] != null && donnees[i].trim().length() > 0) ? donnees[i] : "");
            }
            donnees = donneesTemp.toArray(new String[size]);
        }

        // SERVICES
        boolean gererPays = donnees.length > 6 || (operation & 256) != 0;
        for (Integer service : services) {
            if (service == GestionReferentiel.SERVICE_PAYS) {
                gererPays = true;
            }
        }

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        boolean force = false;
        boolean fantoire = false;
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("force")) {
                    force = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                } else if (tokens[0].trim().equalsIgnoreCase("date")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("fantoire")) {
                    fantoire = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                }
            }
        }
        final String date = (dateOption.equals("")) ? MiscUtils.getToday() : dateOption;

        // EXECUTION DU SERVICE
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final List<String[]> result = gestionAdr.valide(application, services, operation, donnees, date, force, null);

        return ResultAdapter.adapteValide(result, gererPays, fantoire);
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
        params = jdonrefv3lib.getParams();

        // VERIFICATION DES DONNEES
        if (donnees == null || donnees.length < 1) {
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        } else {
            final int size = (donnees.length > 6) ? 7 : 6;
            final List<String> donneesTemp = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                donneesTemp.add((donnees[i] != null && donnees[i].trim().length() > 0) ? donnees[i] : "");
            }
            donnees = donneesTemp.toArray(new String[size]);
        }

        // VERIFICATION DES IDS
        if (ids == null || ids.length < 1) {
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre ids est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        } else {
            final int size = (ids.length > 6) ? 7 : 6;
            final List<String> idsTemp = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                idsTemp.add((ids[i] != null && ids[i].trim().length() > 0) ? ids[i] : "");
            }
            ids = idsTemp.toArray(new String[size]);

        }

        // TRAITEMENT DES DONNEES
        String ligne4 = donnees[3];
        String voi_id = ids[3];
        String code_insee = ids[5];
        String pays_id = (ids.length == 7) ? ids[6] : "";

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        String projectionOption = "";
        String distanceOption = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("date")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("projection")) {
                    projectionOption = (tokens[1] != null) ? tokens[1].trim() : "";
                } else if (tokens[0].trim().equalsIgnoreCase("distance")) {
                    distanceOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        final String date = (dateOption == null || dateOption.trim().equals("")) ? MiscUtils.getToday() : dateOption;
        int projection = 0;
        if (projectionOption != null && !projectionOption.equals("")) {
            try {
                projection = Integer.parseInt(projectionOption);
            } catch (NumberFormatException nfe) {
                Logger.getLogger(JDONREFv3v4.class.getName()).log(Level.SEVERE, "La projection n'est pas un nombre.", nfe);
            }
        }
        int distance = 0;
        if (distanceOption != null && !distanceOption.equals("")) {
            try {
                distance = Integer.parseInt(distanceOption);
            } catch (NumberFormatException nfe) {
                Logger.getLogger(JDONREFv3v4.class.getName()).log(Level.SEVERE, "La distance n'est pas un nombre.", nfe);
            }
        }

        // EXECUTION DU SERVICE
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final List<String[]> result = gestionAdr.geocode(application, services, voi_id, ligne4, code_insee, pays_id, date, distance, projection);

        return ResultAdapter.adapteGeocode(result);
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
        params = jdonrefv3lib.getParams();

        // VERIFICATION DES DONNEES
        if (donnees == null || donnees.length < 1) {
            params.getGestionLog().logValidation(application, "", AGestionLogs.FLAG_VALIDE_ERREUR, false);
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        } else {
            final int size = (donnees.length > 6) ? 7 : 6;
            final List<String> donneesTemp = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                donneesTemp.add((donnees[i] != null && donnees[i].trim().length() > 0) ? donnees[i] : "");
            }
            donnees = donneesTemp.toArray(new String[size]);
        }
        final boolean pays = donnees.length > 6;

        final String dateValidation = (date == null || date.trim().equals("")) ? MiscUtils.getToday() : date;

        // TRAITEMENT DES OPTIONS
        String dateOption = "";
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("date_revalidation")) {
                    dateOption = (tokens[1] != null) ? tokens[1].trim() : "";
                }
            }
        }
        final String dateRevalidation = (dateOption.trim().equals("")) ? MiscUtils.getToday() : dateOption;

        // EXECUTION DU SERVICE
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final List<String[]> result = gestionAdr.revalide(application, services, donnees, dateValidation, dateRevalidation);

        return ResultAdapter.adapteRevalide(result, pays, new String[]{donnees[0], donnees[1], donnees[2], donnees[4]});
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
        params = jdonrefv3lib.getParams();

        // TRAITEMENT DES DONNEES
        String[] position = new String[donnees.length];
        for (int i = 0; i < donnees.length; i++) {
            position[i] = donnees[i];
        }

        int operation = 0;
        for (Integer service : services) {
            switch (service) {
                case 1: // Adresse

                    if ((operation & GestionInverse.GestionInverse_PLAQUE) == 0) {
                        operation += GestionInverse.GestionInverse_PLAQUE;
                    }
                    if ((operation & GestionInverse.GestionInverse_TRONCON) == 0) {
                        operation += GestionInverse.GestionInverse_TRONCON;
                    }
                    if ((operation & GestionInverse.GestionInverse_VOIE) == 0) {
                        operation += GestionInverse.GestionInverse_VOIE;
                    }
                    if ((operation & GestionInverse.GestionInverse_COMMUNE) == 0) {
                        operation += GestionInverse.GestionInverse_COMMUNE;
                    }
                    if ((operation & GestionInverse.GestionInverse_DEPARTEMENT) == 0) {
                        operation += GestionInverse.GestionInverse_DEPARTEMENT;
                    }
                    if ((operation & GestionInverse.GestionInverse_PAYS) == 0) {
                        operation += GestionInverse.GestionInverse_PAYS;
                    }
                    break;
                case 2: // Point adresse

                    if ((operation & GestionInverse.GestionInverse_PLAQUE) == 0) {
                        operation += GestionInverse.GestionInverse_PLAQUE;
                    }
                    break;
                case 3: // Tronçon

                    if ((operation & GestionInverse.GestionInverse_TRONCON) == 0) {
                        operation += GestionInverse.GestionInverse_TRONCON;
                    }
                    break;
                case 4: // Voie

                    if ((operation & GestionInverse.GestionInverse_VOIE) == 0) {
                        operation += GestionInverse.GestionInverse_VOIE;
                    }
                    break;
                case 5: // Commune

                    if ((operation & GestionInverse.GestionInverse_COMMUNE) == 0) {
                        operation += GestionInverse.GestionInverse_COMMUNE;
                    }
                    break;
                case 6: // Département

                    if ((operation & GestionInverse.GestionInverse_DEPARTEMENT) == 0) {
                        operation += GestionInverse.GestionInverse_DEPARTEMENT;
                    }
                    break;
                case 7: // Pays

                    if ((operation & GestionInverse.GestionInverse_PAYS) == 0) {
                        operation += GestionInverse.GestionInverse_PAYS;
                    }
                    break;
            }
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
        final String date = (dateOption.trim().equals("")) ? MiscUtils.getToday() : dateOption;
        int projection = 0;
        if (projectionOption != null && projectionOption.length() > 0) {
            try {
                projection = Integer.parseInt(projectionOption);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        // EXECUTION DU SERVICE
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final String[] result = gestionAdr.inverse(application, services, operation, position, distance + "", date, projection, options);


        return ResultAdapter.adapteGeocodeInverse(result);
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
        params = jdonrefv3lib.getParams();

        // TRAITEMENT DES DONNEES
        if (donnees == null || donnees.length < 1) {
            params.getGestionLog().logDecoupage(application, false);
            final ResultatDecoupage resultatRet = new ResultatDecoupage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Le paramètre donnees est vide.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        } else {
            final int size = (donnees.length > 6 && donnees[6] != null && donnees[6].trim().length() > 0) ? 7 : 6;
            final List<String> donneesTemp = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                donneesTemp.add(donnees[i]);
            }
            donnees = donneesTemp.toArray(new String[size]);
        }
        @Deprecated
        int[] numeros = new int[donnees.length]; // numeros

        for (int i = 0; i < donnees.length; i++) {
            numeros[i] = i + 1;
        }

        // TRAITEMENT DES OPTIONS
        boolean restructure = false;
        for (String option : options) {
            final String[] tokens = option.split("=");
            if (tokens != null && tokens.length == 2) {
                if (tokens[0].trim().equalsIgnoreCase("restructure")) {
                    restructure = (tokens[1] != null) ? Boolean.parseBoolean(tokens[1].trim()) : false;
                }
            }
        }
        final String[] lignes = restructure ? restructure(application, donnees) : donnees;

        // EXECUTION DU SERVICE
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final String[] result = gestionAdr.decoupe(application, lignes, operations, numeros);


        return ResultAdapter.adapteDecoupe(result);
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
        params = jdonrefv3lib.getParams();

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
            params.getGestionLog().logContacte(application, false);
            Logger.getLogger(JDONREFv3v4.class.getName()).log(Level.SEVERE, sb.toString());
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Les paramètres de contact de l'administrateur n'ont pas été initialisés.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            try {
                gm.envoieMail(emetteur, titre, String.valueOf(application), donnees);
                params.getGestionLog().logContacte(application, true);
                resultatRet.setCodeRetour(1);
            } catch (MessagingException ex) {
                params.getGestionLog().logContacte(application, false);
                Logger.getLogger(JDONREFv3v4.class.getName()).log(Level.SEVERE, "Un problème a été rencontré durant l'envoi d'un mail à l'administrateur de JDONREF.", ex);
                resultatRet.setCodeRetour(0);
                final ResultatErreur erreur = new ResultatErreur();
                erreur.setCode(0);
                erreur.setMessage("Un problème a été rencontré durant l'envoi d'un mail à l'administrateur de JDONREF.");
                resultatRet.setErreurs(new ResultatErreur[]{erreur});
            } catch (Exception ex) {
                params.getGestionLog().logContacte(application, false);
                Logger.getLogger(JDONREFv3v4.class.getName()).log(Level.SEVERE, "Un problème inconnu a été rencontré", ex);
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
        params = jdonrefv3lib.getParams();

        // EXECUTION DU SERVICE
        final ResultatVersion resultatRet = new ResultatVersion();
        resultatRet.setCodeRetour(1);
        final PropositionVersion proposition = new PropositionVersion();
        proposition.setNom("Jdonref");
        proposition.setVersion(jdonrefv3lib.getParams().getVersion());
        resultatRet.setPropositions(new PropositionVersion[]{proposition});
        params.getGestionLog().logVersion(application);

        return resultatRet;
    }

    private String[] restructure(int application, String[] donnees) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);
        params = jdonrefv3lib.getParams();

        final int operation = 1 + 2;
        final GestionAdr gestionAdr = jdonrefv3lib.getGestionAdr();
        final String[] result = gestionAdr.normalise(application, operation, donnees, "");

        return result;
    }
}
