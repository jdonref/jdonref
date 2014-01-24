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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.utils.DateUtils;

/**
 * Permet de gérer les fichiers d'archives de JDONREF.<br>
 * A chaque exécution d'une méthode utilisateur ou d'une méthode d'administration, 
 * des informations sont stockées dans les log. Elles peuvent ensuite être traitées
 * pour obtenir des statistiques.<br>
 * Les informations de log sont stockées dans des fichiers qui sont recréées chaque jour. Le format
 * utiliser pour nommer ces fichiers est nom-JJ-MM-AAAA.log.<br>
 * Les noms des fichiers dépendent de la nature des informations qui y sont stockées:
 * <ul>
 *     <li>jdonref-user-date.log pour les commandes utilisateur.</li>
 *     <li>jdonref-meta-admin-date.log pour les commandes de méta-administration.</li>
 *     <li>jdonref-admin-processus-version-date.log pour les commandes d'administration.</li>
 * </ul>
 * Lorsque le service web est relancé plusieurs fois par jour, il est possible que différents
 * processus d'administration aient le même numéro de processus. C'est pourquoi un numéro de version
 * y est ajouté. Ce numéro de version est attribué par la méthode obtientNomFichier qui doit être
 * appelée lors de la création d'un processus.<br>
 * Le format des lignes de ces fichiers est le suivant: JJ-MM-AA:xxxx<br>
 * Les lignes des fichiers utilisateurs respectent un format plus précis : JJ-MM-AA:code application:codefonction:statut:xxxx<br>
 * Où le code application correspond au code de l'application qui appelle JDONREF:
 * <ul><li>0 est réservé aux applications non répertoriées</li>
 *     <li>1 est réservé aux processus d'administration</li>
 *     <li>2 est réservé aux tests</li>
 *     <li>3 est réservé (support JDONREF v1)</li>
 * </ul>
 * et où codefonction correspond une fonction:
 * <ul><li>0 pour l'échec de validation</li>
 *     <li>1 pour la normalisation</li>
 *     <li>2 pour la validation</li>
 *     <li>3 pour le géocodage</li>
 *     <li>4 pour le découpage</li>
 *     <li>5 pour la fonction getState(0)</li>
 *     <li>6 pour la revalidation</li>
 *     <li>7 pour contacte</li>
 *     <li>8 pour getVersion</li>
 * </ul>
 * Le statut peut valoir 0 ou 1 selon que la méthode s'est bien déroulée ou non.
 * @author jmoquet
 */
public class GestionLogs extends AGestionLogs{
    // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//    private final static SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy",Locale.FRANCE);
//    private final static SimpleDateFormat sdtimeformat = new SimpleDateFormat("HH:mm",Locale.FRANCE);
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleDashed;
    private final static DateUtils.DateFormatType sdtimeformat = DateUtils.DateFormatType.SimpleTime;
    static GestionLogs instance = null;
    /**
     * Ce booleen a été crée pour éviter d'inscrire une erreur dans les logs lorsque le système de log
     * ne fonctionne pas.
     */
    private boolean errorlog = false;
    


    private GestionLogs() {
    }

    /**
     * Obtient une instance de GestionLogs.
     * @return l'unique instance de GestionLogs (singleton).
     */
    public static GestionLogs getInstance() {
        if (instance == null) {
            instance = new GestionLogs();
        }
        return instance;
    }
    
    
    public void logs(String message){
        //Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, message);
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, message);  
    }

    public void logs(String message, Throwable thrown){
        //Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, message,thrown); 
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, message,thrown); 
    }
    
    final static String ecrit_utf8 = "UTF-8";
    final static String ecrit_retour = "\r\n";

    /**
     * Ecrit le message spécifié suivi d'un retour chariot dans le fichier de chemin spécifié.<br>
     * @param filepath
     * @param message
     */
    private static void ecrit(String filepath, String message) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//        sb.append(sdtimeformat.format(Calendar.getInstance().getTime()));
        sb.append(DateUtils.formatDateToString(Calendar.getInstance().getTime(), sdtimeformat));
        sb.append(':');
        sb.append(message);
        sb.append(ecrit_retour);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath, true), ecrit_utf8));
        bw.write(sb.toString());
        bw.close();
    }
    final String obtientNomFichierUser_log = ".log";
    final String obtientNomFichierUser_jdonref_user = "/jdonref-user-";

    /**
     * Obtient le nom du fichier de log des méthodes utilisateur.
     * @return
     */
    private String obtientNomFichierUser() {
        Calendar c = Calendar.getInstance();
        StringBuilder name = new StringBuilder();
        name.append(repertoire);
        name.append(obtientNomFichierUser_jdonref_user);
        // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//        name.append(sdformat.format(c.getTime()));
        name.append(DateUtils.formatDateToString(c.getTime(), sdformat));
        name.append(obtientNomFichierUser_log);
        return name.toString();
    }

    /**
     * Obtient le nom du fichier de log des méthodes administrateur.
     * @return
     */
    private String obtientNomFichierMetaAdmin() {
        Calendar c = Calendar.getInstance();
        StringBuilder name = new StringBuilder();
        name.append(repertoire);
        name.append("/jdonref-meta-admin-");
        // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//        name.append(sdformat.format(c.getTime()));
        name.append(DateUtils.formatDateToString(c.getTime(), sdformat));
        name.append(".log");
        return name.toString();
    }

    /**
     * Obtient le nom de fichier de log pour le processus d'administration spécifié.
     * @return
     */
    private String obtientNomFichierAdmin(int processus, int version) {
        Calendar c = Calendar.getInstance();
        StringBuilder name = new StringBuilder();
        name.append(repertoire);
        name.append("/jdonref-admin-");
        name.append(Integer.toString(processus));
        name.append('-');
        name.append(Integer.toString(version));
        name.append('-');
        // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//        name.append(sdformat.format(c.getTime()));
        name.append(DateUtils.formatDateToString(c.getTime(), sdformat));
        name.append(".log");
        return name.toString();
    }


    /**
     * Permet de logger qu'un échec de validation d'adresse.<br>
     * Il s'agit des appels effectuée à la méthode contacte.<br>
     * L'information logguée est de la forme : HH:mm:application:0:statut:Echec de validation où
     * statut vaut 1 si statut est à true ce qui signifie que la méthode s'est bien déroulée,
     * et 0 sinon.
     */
    public void logEchecValidation(int application, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logEchecValidation_0);
            sb.append(statut ? '1' : '0');
            sb.append(logEchecValidation_echec);

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logEchecValidation_echeclog, ex);
                errorlog = true;
            }
        }
    }

    /**
     * Permet de logger une normalisation effectuée par JDONREF.<br>
     * L'information logguée est de la forme : "HH:mm:application:1:statut:flags:normalisation" où statut vaut 1 si statut est à true ce qui signifie que la méthode s'est bien déroulée,
     * et 0 sinon
     * et flags est la valeur passée en paramètre.
     * @param flags la normalisation effectuée.
     */
    public void logNormalisation(int application, int flags, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logNormalisation_dp_1);
            sb.append(statut ? logNormalisation_1 : logNormalisation_0);
            sb.append(Integer.toString(flags));
            sb.append(':');
            boolean first = true;
            if ((flags & FLAG_NORMALISE_1) != 0) {
                first = false;
                sb.append(logNormalisation_normalise);
            }
            if ((flags & FLAG_NORMALISE_RESTRUCTURE) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logNormalisation_restructure);
            }
            if ((flags & FLAG_NORMALISE_2) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logNormalisation_normalise_2);
            }
            if ((flags & FLAG_NORMALISE_2_38) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logNormalisation_normalise_2_38);
            }
            if ((flags & FLAG_NORMALISE_PHONETISE) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logNormalisation_phonetise);
            }
            if ((flags & FLAG_NORMALISE_SANS_ARTICLES) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logNormalisation_sans_articles);
            }

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logNormalisation_erreurlog, ex);
            }
        }
    }

    /**
     * Permet de logger une validation d'adresse.<br>
     * L'information logguée est de la forme : "HH:mm:2:statut:flags:departement:Validation" où statut vaut 1 si statut est à true
     * ce qui signifie que la méthode s'est bien déroulée et 0 sinon
     * et flags
     * correspond au paramètre de la méthode.
     * @param flags un champ de bit où chaque bit correspond à un champ de l'adresse comme suit:
     * <ul><li>1 pour le type de voie</li>
     *     <li>2 pour la voie</li>
     *     <li>4 pour le code postal</li>
     *     <li>8 pour la commune</li>
     *     <li>16 pour l'arrondissement</li></ul>
     * Si aucun bit n'est défini, il s'agit d'une erreur.<br>
     * et enfin, departement est le département dans lequel la validation a été effectuée, 0 sinon.
     */
    public void logValidation(int application, String departement, int flags, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logValidation_2);
            sb.append(statut ? logValidation_1 : logValidation_0);
            sb.append(flags);
            sb.append(':');
            if (departement != null && departement.length() > 0) {
                sb.append(departement);
            } else {
                sb.append('0');
            }
            sb.append(logValidation_validationavec);

            boolean first = true;
            if (flags == 0) {
                sb.append(logValidation_erreur);
            }
            if ((flags & FLAG_VALIDE_TYPEDEVOIE) != 0) {
                sb.append(logValidation_type_de_voie);
                first = false;
            }
            if ((flags & FLAG_VALIDE_LIBELLE) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logValidation_voie);
            }
            if ((flags & FLAG_VALIDE_CODEPOSTAL) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logValidation_codepostal);
            }
            if ((flags & FLAG_VALIDE_COMMUNE) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logValidation_commune);
            }
            if ((flags & FLAG_VALIDE_ARRONDISSEMENT) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logValidation_arrondissement);
            }
            if ((flags & FLAG_VALIDE_PAYS) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logValidation_pays);
            }
            if ((flags & FLAG_VALIDE_POIZON) != 0) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append(logValidation_poizon);
            }

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logValidation_nonloggee, ex);
            }
        }
    }

    /**
     * Permet de logger un reverse géocoding.
     * L'information logguée est de la forme : "HH:mm:application:9:statut:operation" où statut vaut 1 si statut est à true
     * ce qui signifie que la méthode s'est bien déroulée, et 0 sinon
     * et flags correspond au paramètre de la méthode.
     * @param qualite indique la catégorie de géocodage retourné par JDONREF:
     * <ul>
     * <li>1 pour à la plaque,</li>
     * <li>2 pour à l'interpolation de la plaque,</li>
     * <li>3 pour à l'interpolation métrique du troncon ou les bornes du troncon (qualité équivalente),</li>
     * <li>4 au centroide du troncon,</li>
     * <li>5 pour à le centroide de la voie.</li>
     * <li>6 à l'arrondissement ou à la commune.</li>
     * <li>7 au département.</li>
     * </ul>
     * si qualite est nul, il s'agit d'une erreur.
     */
    public void logInverse(int application, int operation, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logInverse_9);
            sb.append(statut ? logInverse_1 : logInverse_0);
            sb.append(Integer.toString(operation));
            sb.append(logInverse_inverse);

            switch (operation) {
                case 0: // FLAG_INVERSE_ERREUR
                    sb.append(logInverse_erreur);
                    break;
                case 1: // FLAG_INVERSE_PLAQUE
                    sb.append(logInverse_plaque);
                    break;
                case 2: // FLAG_INVERSE_INTERPOLATION_PLAQUE
                    sb.append(logInverse_interpolation_plaque);
                    break;
                case 3: // FLAG_INVERSE_INTERPOLATION_TRONCON
                    sb.append(logInverse_interpolation_metrique);
                    break;
                case 4: // FLAG_INVERSE_CENTROIDE_TRONCON
                    sb.append(logInverse_centroide_troncon);
                    break;
                case 5: // FLAG_INVERSE_CENTROIDE_VOIE
                    sb.append(logInverse_centroide_voie);
                    break;
                case 6: // FLAG_INVERSE_COMMUNE
                    sb.append(logInverse_arrondissement);
                    break;
                case 7: // FLAG_INVERSE_DEPARTEMENT
                    sb.append(logInverse_departement);
                    break;
                case 8: // FLAG_INVERSE_POIZON
                    sb.append(logInverse_poizon);
                    break;
            }

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logInverse_erreurlog, ex);
            }
        }
    }

    /**
     * Permet de logger un géocodage.
     * L'information logguée est de la forme : "HH:mm:application:3:statut:qualite:Géocodage qualite" où statut vaut 1 si statut est à true
     * ce qui signifie que la méthode s'est bien déroulée, et 0 sinon
     * @param qualite indique la catégorie de géocodage retourné par JDONREF:
     * <ul>
     * <li>1 pour à la plaque,</li>
     * <li>2 pour à l'interpolation de la plaque,</li>
     * <li>3 pour à l'interpolation métrique du troncon ou les bornes du troncon (qualité équivalente),</li>
     * <li>4 au centroide du troncon,</li>
     * <li>5 pour à le centroide de la voie.</li>
     * <li>6 à l'arrondissement ou à la commune.</li>
     * <li>7 au département.</li>
     * </ul>
     * si qualite est nul, il s'agit d'une erreur.
     */
    public void logGeocodage(int application, int qualite, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logGeocodage_3);
            sb.append(statut ? logGeocodage_1 : logGeocodage_0);
            sb.append(Integer.toString(qualite));
            sb.append(logGeocodage_geocodage);

            switch (qualite) {
                case 0: // FLAG_GEOCODE_ERREUR
                    sb.append(logGeocodage_erreur);
                    break;
                case 1: // FLAG_GEOCODE_PLAQUE
                    sb.append(logGeocodage_plaque);
                    break;
                case 2: // FLAG_GEOCODE_INTERPOLATION_PLAQUE
                    sb.append(logGeocodage_interpolation_plaque);
                    break;
                case 3: // FLAG_GEOCODE_INTERPOLATION_TRONCON
                    sb.append(logGeocodage_interpolation_metrique);
                    break;
                case 4: // FLAG_GEOCODE_CENTROIDE_TRONCON
                    sb.append(logGeocodage_centroide_troncon);
                    break;
                case 5: // FLAG_GEOCODE_CENTROIDE_VOIE
                    sb.append(logGeocodage_centroide_voie);
                    break;
                case 6: // FLAG_GEOCODE_COMMUNE
                    sb.append(logGeocodage_arrondissement);
                    break;
                case 7: // FLAG_GEOCODE_DEPARTEMENT
                    sb.append(logGeocodage_departement);
                    break;
                case 8: // FLAG_GEOCODE_PAYS
                    sb.append(logGeocodage_pays);
                    break;
                case 9: // FLAG_GEOCODE_POIZON
                    sb.append(logGeocodage_poizon);
            }

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logGeocodage_erreurlog, ex);
            }
        }
    }

    /**
     * Permet de logger la récupération du numéro de version.<br>
     * L'information logguée est de la forme : "HH:mm:application:8:getVersion"
     * @param app
     * @param b
     */
    public void logVersion(int application) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logVersion_8);
            sb.append(logVersion_getVersion);

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logVersion_nonlogge, ex);
            }
        }
    }
 
    /**
     * Permet de logger le contact d'un administrateur.<br>
     * L'information logguée est de la forme : "HH:mm:application:7:statut:contacte" où statut vaut 1 si statut est a true
     * ce qui signifie que la méthode s'est bien déroulée.
     * @param app
     * @param b
     */
    public void logContacte(int application, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logContacte_7);
            sb.append(statut ? '1' : '0');
            sb.append(logContacte_contacte);

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logContacte_nonlogge, ex);
            }
        }
    }

    /**
     * Permet de logger un découpage.<br>
     * L'information logguée est de la forme : "HH:mm:application:4:statut:decoupage" où statut vaut 1 si statut est à true
     * ce qui signifie que la méthode s'est bien déroulée, et 0 sinon.
     */
    public void logDecoupage(int application, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logDecoupage_4);
            sb.append(statut ? '1' : '0');
            sb.append(logDecoupage_decoupage);

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logDecoupage_nonlogge, ex);
            }
        }
    }

    /**
     * Permet de logger un accès à l'état de JDONREF.<br>
     * L'information logguée est de la forme : "HH:mm:application:5:statut:getState(0)" statut vaut 1 si statut est à true
     * ce qui signifie que la méthode s'est bien déroulée, et 0 sinon.
     */
    public void logGetState(int application, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logGetState_5);
            sb.append(statut ? '1' : '0');
            sb.append(logGetState_getstate);

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logGetState_nonlogge, ex);
            }
        }
    }
 
    /**
     * Permet de logger une revalidation.<br>
     * L'information logguée est de la forme : "HH:mm:application:6:statut:revalidation" où statut vaut 1 si statut est à true
     * ce qui signifie que la méthode s'est bien déroulée, et 0 sinon.
     */
    public void logRevalidation(int application, boolean statut) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(application);
            sb.append(logRevalidation_6);
            sb.append(statut ? '1' : '0');
            sb.append(logRevalidation_revalidation);

            ecrit(obtientNomFichierUser(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, logRevalidation_logerreur, ex);
            }
        }
    }

    /**
     * Loggue l'utilisation d'une méthode de méta-administration.<br>
     * L'information logguée est de la forme : "HH:mm:numero:code:message" où message correspond
     * au paramètre de la méthode, numero correspond à une méthode:
     * <ul>
     *     <li>0 pour la méthode administre</li>
     *     <li>1 pour la méthode stop</li>
     *     <li>2 pour getState</li>
     *     <li>3 pour obtientProcessus</li>
     *     <li>4 pour free</li>
     *     <li>5 pour getList</li>
     * </ul>
     * et code indique la nature du retour effectué qui dépend de la méthode.
     * @param message
     */
    public void logMetaAdmin(int numero, int code, String message) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(numero));
            sb.append(':');
            sb.append(Integer.toString(code));
            sb.append(':');
            sb.append(message);
            ecrit(obtientNomFichierMetaAdmin(), sb.toString());
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, "L'utilisation d'une méthode de méta-administration n'a pas pu être logguée", ex);
            }
        }
    }

    /**
     * Loggue un message provenant d'une méthode d'administration.
     * L'information logguée est de la forme : "HH:mm:message" où message correspond
     * au paramètre de la méthode.
     */
    public void logAdmin(int processus, int version, String message) {
        try {
            ecrit(obtientNomFichierAdmin(processus, version), message);
        } catch (IOException ex) {
            if (!errorlog) {
                errorlog = true;
                Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, "L'utilisation d'une méthode d'administration n'a pas pu être logguée", ex);
            }
        }
    }
    
    

    public static void main(String[] args) {
        try {

            GestionLogs gl = GestionLogs.getInstance();
            gl.definitRepertoire(".");

            gl.logEchecValidation(2, true);
            gl.logNormalisation(2, FLAG_NORMALISE_1, true);
            gl.logValidation(2, "75", FLAG_VALIDE_CODEPOSTAL + FLAG_VALIDE_ARRONDISSEMENT + FLAG_VALIDE_LIBELLE, true);
            gl.logGeocodage(2, FLAG_GEOCODE_COMMUNE, true);

            gl.logMetaAdmin(0, 0, "LANCEMENT D UNE MISE A JOUR PROCESSUS 0");

            int version = gl.obtientNumeroVersion(0);
            gl.logAdmin(0, version, "LANCEMENT MISE A JOUR");

            int version2 = gl.obtientNumeroVersion(0);
            gl.logAdmin(0, version2, "LANCEMENT MISE A JOUR 2");
            gl.logAdmin(0, version, "FIN MISE A JOUR");

            gl.logEchecValidation(2, true);
            gl.logNormalisation(2, FLAG_NORMALISE_RESTRUCTURE, true);
        } catch (JDONREFException ex) {
            Logger.getLogger(GestionLogs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
