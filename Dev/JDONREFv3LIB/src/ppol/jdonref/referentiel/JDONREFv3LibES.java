/**
 * Version 3 – 2014
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
package ppol.jdonref.referentiel;

import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.GestionConnectionES;
import ppol.jdonref.GestionMail;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFv3Exception;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.poizon.GestionPoizon;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;

/**
 * 
 * @author moquetju
 */
public class JDONREFv3LibES extends JDONREFv3Lib {
    /**
     * Le gestionnaire Poizon.
     */
    private GestionPoizon gestionPoizon;
    
    public GestionPoizon getGestionPoizon() {
        return gestionPoizon;
    }
    
    protected JDONREFv3LibES() {
        // Initialise les paramètres
        try {
            params.load("webapps/JADRREF/META-INF/", "params.xml");
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", jde);
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", ioe);
        } catch (JDONREFException jdone) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", jdone);
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", ex);
        }
    }
    
    protected JDONREFv3LibES(String file) {
        // Initialise les paramètres
        try {
            params.load(file);
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", jde);
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", ioe);
        } catch (JDONREFException jdone) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", jdone);
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture de params.xml", ex);
        }
    }
    
    public static JDONREFv3Lib getInstance(String file) {
        if (INSTANCE == null) {
            INSTANCE = new JDONREFv3LibES(file).init();
        }

        return INSTANCE;
    }

    @Override
    protected JDONREFv3Lib init() {
        Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.INFO, "Début d'initialisation de JDONREFv3Lib...");

        // Initialise la connection
        GestionConnection gestionConnection = null;
        try {
            gestionConnection = new GestionConnection(params);
            gestionConnection.load(params.obtientConfigPath() + "connections.xml");
            gestionConnection.connecte();
        } catch (SQLException sqle) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème durant l'établissement des connexions", sqle);
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème durant la lecture du fichier connections.xml", jde);
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème durant la lecture du fichier connections.xml", ioe);
        } catch (ClassNotFoundException cfe) {
            gestionConnection = null;
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "La classe org.postgresql.Driver n'a pas été trouvée.", cfe);
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème lors de la lecture du fichier connections.xml", ex);
        }
        
        GestionConnectionES gestionConnectionES = null;
        try {
            gestionConnectionES = new GestionConnectionES(params);
            gestionConnectionES.load(params.obtientConfigPath() + "connectionsES.xml");
        } catch (JDOMException ex) {
            java.util.logging.Logger.getLogger(JDONREFv3LibES.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(JDONREFv3LibES.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        

        // Gestionnaire de departements
        if (params.isUtilisationDeLaGestionDesDepartements()) {
            try {
                GestionCodesDepartements.getInstance().loadDptCodes(
                        gestionConnection.obtientConnection().connection,
                        params.obtientConfigPath() + "departementsSynonymes.xml",
                        params.obtientConfigPath() + "algosCP-Departements.xml");
            } catch (SQLException sqle) {
                Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème durant l'initialisation du gestionnaire de départements", sqle);
            } catch (JDONREFException jdone) {
                Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème durant l'initialisation du gestionnaire de départements", jdone);
            }
        }

        // Définit le gestionnaire de l'envoi de mail.
        gestionMail = new GestionMail();
        try {
            gestionMail.loadConfig(params.obtientConfigPath() + "mail.xml");
        } catch (JDOMException jde) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Erreur dans la syntaxe du fichier mail.xml", jde);
            gestionMail = null;
        } catch (IOException ioe) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Erreur lors de la lecture du fichier mail.xml", ioe);
            gestionMail = null;
        } catch (JDONREFException jdone) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Erreur dans le format du fichier mail.xml", jdone);
            gestionMail = null;
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème non répertorié lié à la lecture du fichier mail.xml", ex);
            gestionMail = null;
        }

        try {
        //services= Services.getInstance(params.obtientConfigPath() + "services.xml");
        services= Services.getInstance(params.obtientConfigPath());
        
        }catch(JDONREFv3Exception exJ){
             Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème non répertorié lié à la lecture du fichier services.xml", exJ);
            gestionMail = null;
        }catch(Exception ex){
             Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Problème non répertorié lié à la lecture du fichier services.xml", ex);
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
            final GestionReferentiel gestionReferentiel = new GestionReferentielES(gestionMots, gestionMiseAJour, params);

            ((GestionReferentielES)gestionReferentiel).setConnexionES(gestionConnectionES);
            
            // Fait le lien entre le gestionnaire de mots et le gestionnaire de référentiel.
            gestionMots.definitGestionReferentiel(gestionReferentiel);

            // Définit le gestionnaire du reverse geocoding
            final GestionInverse gestionInverse = new GestionInverse(params);

            // Initialise les paramètres de gestionMots.
            gestionMots.definitJDONREFParams(params);

            // Définit le répertoire des logs.
//            GestionLogs.getInstance().definitRepertoire(params.obtientLogPath());
            params.getGestionLog();

            // Définit les paramètres du gestionnaire de description de tables.
            GestionDescriptionTables.definitJDONREFParams(params);

            // Définit le gestionnaire d'adresses.
            gestionAdr = new GestionAdr(gestionConnection, gestionMots, gestionReferentiel, gestionInverse);
            gestionAdr.definitJDONREFParams(params);

            // Gestion Poizon
            gestionPoizon = new GestionPoizon(params, gestionMots, gestionConnection);
            
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.INFO, "...Fin d'initialisation de JDONREFv3Lib.");

        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.ERROR, "Erreur durant la fin de l'initialisation de JDONREFv3Lib", ex);
        }

        return this;
    }
}
