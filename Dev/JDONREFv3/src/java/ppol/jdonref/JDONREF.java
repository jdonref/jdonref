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
package ppol.jdonref;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


import javax.servlet.ServletContext;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;


import javax.xml.ws.handler.MessageContext;
import ppol.jdonref.utils.Utils;
import ppol.jdonref.wsclient.JDONREFService;
import ppol.jdonref.wservice.IJDONREFv3;
import ppol.jdonref.wservice.IJDONREFv3Router;
import ppol.jdonref.wservice.ResultatContacte;
import ppol.jdonref.wservice.ResultatDecoupage;
import ppol.jdonref.wservice.ResultatErreur;
import ppol.jdonref.wservice.ResultatGeocodage;
import ppol.jdonref.wservice.ResultatGeocodageInverse;
import ppol.jdonref.wservice.ResultatNormalisation;
import ppol.jdonref.wservice.ResultatRevalidation;
import ppol.jdonref.wservice.ResultatValidation;
import ppol.jdonref.wservice.ResultatVersion;
import ppol.jdonref.wservice.ServiceParameters;

/**
 * Service web JDONREFold.<br>
 * Cette classe est utilisée pour générer le proxy de l'interface SOAP de JDONREFold.<br>
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
public class JDONREF implements IJDONREFv3 {

    @Resource
    private WebServiceContext context;
    private static ServletContext servletContext;
    private ppol.jdonref.referentiel.JDONREFv3Lib jdonrefv3lib;

    /**
     * Constructeur du service web.
     */
    public JDONREF() {
        //jdonrefv3lib = JDONREFv3Lib.getInstance();
    }

    public static ServletContext getContext(WebServiceContext context) {
        if (servletContext == null) {
            servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        }
        return servletContext;
    }

    // FONCTION QUI CHARGE LE FICHIER DE CONFIGURATION
    public ppol.jdonref.referentiel.JDONREFv3Lib chargeConf(ppol.jdonref.referentiel.JDONREFv3Lib lib) {
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.normalise(application, services, operation, donnees, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatNormalisation resultatRet = new ResultatNormalisation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatNormalisation resultatRet = new ResultatNormalisation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }
        // Appel des services.
        final List<ppol.jdonref.wsclient.ResultatNormalisation> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatNormalisation>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.normalise(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        service.getOperation(),
                        Utils.toList(service.getDonnees()),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatNormalisation resultatRet = new ResultatNormalisation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }
        // Aggregation du resulat
        return ResultAdapter.adapteNormalise(resultList);
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.valide(application, services, operation, donnees, ids, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatValidation resultatRet = new ResultatValidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatValidation resultatRet = new ResultatValidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatValidation> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatValidation>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.valide(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        service.getOperation(),
                        Utils.toList(service.getDonnees()),
                        Utils.toList(service.getIds()),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatValidation resultatRet = new ResultatValidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }
        // Aggregation du resulat

        return ResultAdapter.adapteValide(resultList);
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.geocode(application, services, donnees, ids, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatGeocodage> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatGeocodage>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.geocode(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        Utils.toList(service.getDonnees()),
                        Utils.toList(service.getIds()),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatGeocodage resultatRet = new ResultatGeocodage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // Aggregation du resulat

        return ResultAdapter.adapteGeocode(resultList);
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.revalide(application, services, donnees, ids, date, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatRevalidation> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatRevalidation>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.revalide(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        Utils.toList(service.getDonnees()),
                        Utils.toList(service.getIds()),
                        service.getDate(),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatRevalidation resultatRet = new ResultatRevalidation();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // Aggregation du resulat

        return ResultAdapter.adapteRevalide(resultList);
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.inverse(application, services, donnees, distance, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatGeocodageInverse> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatGeocodageInverse>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.reverse(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        Utils.toList(service.getDonnees()),
                        service.getDistance(),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // Aggregation du resulat

        return ResultAdapter.adapteGeocodeInverse(resultList);
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.decoupe(application, services, operations, donnees, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatDecoupage resultatRet = new ResultatDecoupage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatDecoupage resultatRet = new ResultatDecoupage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatDecoupage> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatDecoupage>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.decoupe(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        Utils.toList(service.getOperations()),
                        Utils.toList(service.getDonnees()),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatDecoupage resultatRet = new ResultatDecoupage();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // Aggregation du resulat

        return ResultAdapter.adapteDecoupe(resultList);
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

        //Gestion des options par défaut
        options = ((ppol.jdonref.JDONREFv3Lib)jdonrefv3lib).getGestionOptions().getOptions(application, options);
        
        // Liste des services.
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.contacte(application, services, operation, donnees, options));
        } catch (JDONREFv3Exception jde) {
            final ResultatContacte resultatRet = new ResultatContacte();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatContacte resultatRet = new ResultatContacte();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatContacte> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatContacte>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.contacte(
                        service.getApplication(),
                        Utils.toList(service.getServices()),
                        operation,
                        Utils.toList(service.getDonnees()),
                        Utils.toList(service.getOptions())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatContacte resultatRet = new ResultatContacte();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // Aggregation du resulat

        return ResultAdapter.adapteContacte(resultList);
    }

    /**
     * Obtient la version de JDONREFold.
     */
    @WebMethod(operationName = "getVersion")
    public ResultatVersion getVersion(
            @WebParam(name = "application") int application,
            @WebParam(name = "services") int[] services) {

        //CHARGEMENT DU FICHIER DE CONFIGURATION
        jdonrefv3lib = chargeConf(jdonrefv3lib);

        // Liste des services. 
        final List<ServiceParameters> serviceList = new ArrayList<ServiceParameters>();
        try {
            final IJDONREFv3Router router = JDONREFv3RouterFactory.getInstance(jdonrefv3lib);
            serviceList.addAll(router.getVersion(application, services));
        } catch (JDONREFv3Exception jde) {
            final ResultatVersion resultatRet = new ResultatVersion();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(jde.getErrorcode());
            erreur.setMessage(jde.getMessage());
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }
        if (serviceList.isEmpty()) {
            final ResultatVersion resultatRet = new ResultatVersion();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(5);
            erreur.setMessage("Aucun service n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});

            return resultatRet;
        }

        // Appel des services
        final List<ppol.jdonref.wsclient.ResultatVersion> resultList = new ArrayList<ppol.jdonref.wsclient.ResultatVersion>();
        for (ServiceParameters service : serviceList) {
            try {
                final JDONREFService JDONREFservice = JDONREFServiceFactory.getInstance(service);
                final ppol.jdonref.wsclient.JDONREF port = JDONREFservice.getJDONREFPort();
                resultList.add(port.getVersion(service.getApplication(), Utils.toList(service.getServices())));
            } catch (Exception ex) {
                Logger.getLogger(JDONREF.class.getName()).log(Level.SEVERE, "Problème lors de l'invocation du service " + service.toString(), ex);
            }
        }
        if (resultList.isEmpty()) {
            final ResultatVersion resultatRet = new ResultatVersion();
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(0);
            erreur.setMessage("Aucun résultat n'a été trouvé.");
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
            return resultatRet;
        }

        // Aggregation du resulat

        return ResultAdapter.adapteGetVersion(resultList);
    }
}
