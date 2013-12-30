/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.referentiel;

import ppol.jdonref.referentiel.*;
import ppol.jdonref.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.GestionLogs;
import ppol.jdonref.GestionMail;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.JDONREFv3Exception;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.referentiel.GestionCodesDepartements;
import ppol.jdonref.referentiel.GestionDescriptionTables;
import ppol.jdonref.referentiel.GestionMiseAJour;
import ppol.jdonref.referentiel.GestionReferentiel;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;
import ppol.jdonref.referentiel.GestionAdr;
import ppol.jdonref.referentiel.Services;

/**
 *
 * @author marcanhe
 */
public class JDONREFv3Lib {

    private final JDONREFParams params = new JDONREFParams();
    private GestionMail gestionMail;
    private GestionAdr gestionAdr;
    private static JDONREFv3Lib INSTANCE;
    private  Services services;

    public static JDONREFv3Lib getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JDONREFv3Lib().init();
        }

        return INSTANCE;
    }

    public Services getServices() {
        return services;
    }
    
    

    public static JDONREFv3Lib getInstance(String file) {
        if (INSTANCE == null) {
            INSTANCE = new JDONREFv3Lib(file).init();
        }

        return INSTANCE;
    }

    private JDONREFv3Lib() {
        // Initialise les paramètres
        try {
            params.load("webapps/JADRREF/META-INF/", "params.xml");
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", jde);
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", ioe);
        } catch (JDONREFException jdone) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", jdone);
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", ex);
        }
    }

    private JDONREFv3Lib(String file) {
        // Initialise les paramètres
        try {
            params.load(file);
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", jde);
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", ioe);
        } catch (JDONREFException jdone) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", jdone);
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de params.xml", ex);
        }
    }

    private JDONREFv3Lib init() {
        Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.INFO, "Début d'initialisation de JDONREFv3Lib...");

        // Initialise la connection
        GestionConnection gestionConnection = null;
        try {
            gestionConnection = new GestionConnection(params);
            gestionConnection.load(params.obtientConfigPath() + "connections.xml");
            gestionConnection.connecte();
        } catch (SQLException sqle) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème durant l'établissement des connexions", sqle);
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème durant la lecture du fichier connections.xml", jde);
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème durant la lecture du fichier connections.xml", ioe);
        } catch (ClassNotFoundException cfe) {
            gestionConnection = null;
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "La classe org.postgresql.Driver n'a pas été trouvée.", cfe);
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème lors de la lecture du fichier connections.xml", ex);
        }

        // Gestionnaire de departements
        if (params.isUtilisationDeLaGestionDesDepartements()) {
            try {
                GestionCodesDepartements.getInstance().loadDptCodes(
                        gestionConnection.obtientConnection().connection,
                        params.obtientConfigPath() + "departementsSynonymes.xml",
                        params.obtientConfigPath() + "algosCP-Departements.xml");
            } catch (SQLException sqle) {
                Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème durant l'initialisation du gestionnaire de départements", sqle);
            } catch (JDONREFException jdone) {
                Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème durant l'initialisation du gestionnaire de départements", jdone);
            }
        }

        // Définit le gestionnaire de l'envoi de mail.
        gestionMail = new GestionMail();
        try {
            gestionMail.loadConfig(params.obtientConfigPath() + "mail.xml");
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Erreur dans la syntaxe du fichier mail.xml", jde);
            gestionMail = null;
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Erreur lors de la lecture du fichier mail.xml", ioe);
            gestionMail = null;
        } catch (JDONREFException jdone) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Erreur dans le format du fichier mail.xml", jdone);
            gestionMail = null;
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème non répertorié lié à la lecture du fichier mail.xml", ex);
            gestionMail = null;
        }

        try {
        //services= Services.getInstance(params.obtientConfigPath() + "services.xml");
        services= Services.getInstance(params.obtientConfigPath());
        
        }catch(JDONREFv3Exception exJ){
             Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème non répertorié lié à la lecture du fichier services.xml", exJ);
            gestionMail = null;
        }catch(Exception ex){
             Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Problème non répertorié lié à la lecture du fichier services.xml", ex);
            gestionMail = null;
        }
        
        
        try {
            // Initialise les mots.
            final GestionMots gestionMots = new GestionMots();
            gestionMots.initMots(
                    params.obtientConfigPath() + "abbreviations.xml",
                    params.obtientConfigPath() + "cles.xml",
                    params.obtientConfigPath() + "prenoms.txt");

            // Définit le gestionnaire de mise à jour
            final GestionMiseAJour gestionMiseAJour = new GestionMiseAJour(gestionMots, params);

            // Définit le gestionnaire de référentiel.
            final GestionReferentiel gestionReferentiel = new GestionReferentiel(gestionMots, gestionMiseAJour, params);

            // Fait le lien entre le gestionnaire de mots et le gestionnaire de référentiel.
            gestionMots.definitGestionReferentiel(gestionReferentiel);

            // Définit le gestionnaire du reverse geocoding
            final GestionInverse gestionInverse = new GestionInverse(params);

            // Initialise les paramètres de gestionMots.
            gestionMots.definitJDONREFParams(params);

            // Définit le répertoire des logs.
            GestionLogs.getInstance().definitRepertoire(params.obtientLogPath());

            // Définit les paramètres du gestionnaire de description de tables.
            GestionDescriptionTables.definitJDONREFParams(params);

            // Définit le gestionnaire d'adresses.
            gestionAdr = new GestionAdr(gestionConnection, gestionMots, gestionReferentiel, gestionInverse);

            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.INFO, "...Fin d'initialisation de JDONREFv3Lib.");

        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Erreur durant la fin de l'initialisation de JDONREFv3Lib", ex);
        }

        return this;
    }

    public JDONREFParams getParams() {
        return params;
    }

    public GestionAdr getGestionAdr() {
        return gestionAdr;
    }

    public GestionMail getGestionMail() {
        return gestionMail;
    }
}
