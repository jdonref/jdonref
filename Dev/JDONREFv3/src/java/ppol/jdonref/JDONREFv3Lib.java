package ppol.jdonref;



import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.referentiel.GestionDescriptionTables;
import ppol.jdonref.referentiel.GestionMiseAJour;
import ppol.jdonref.referentiel.GestionReferentiel;

/**
 *
 * @author marcanhe
 */
public class JDONREFv3Lib extends ppol.jdonref.referentiel.JDONREFv3Lib {

    /**
     * Le gestionnaire de mots.
     */
    private final GestionMots gestionMots = new GestionMots();

    public static ppol.jdonref.referentiel.JDONREFv3Lib getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JDONREFv3Lib().init();
        }

        return INSTANCE;
    }

    public static ppol.jdonref.referentiel.JDONREFv3Lib getInstance(String file) {
        if (INSTANCE == null) {
            INSTANCE = new JDONREFv3Lib(file).init();
        }

        return INSTANCE;
    }

    private JDONREFv3Lib() {
        // Initialise les paramètres
        try {
            params.load("webapps/JDONREFv3/META-INF/", "params.xml");
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

    protected ppol.jdonref.referentiel.JDONREFv3Lib init() {
        Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.INFO, "Début d'initialisation de JDONREFv3Lib...");

        try {
            // Initialise les mots.
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

            // Initialise les paramètres de gestionMots.
            gestionMots.definitJDONREFParams(params);

            // Définit le répertoire des logs.
//            GestionLogs.getInstance().definitRepertoire(params.obtientLogPath());
            params.getGestionLog();

            // Définit les paramètres du gestionnaire de description de tables.
            GestionDescriptionTables.definitJDONREFParams(params);

            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.INFO, "...Fin d'initialisation de JDONREFv3Lib.");
        } catch (Exception ex) {
            Logger.getLogger(JDONREFv3Lib.class.getName()).log(Level.SEVERE, "Erreur durant la fin de l'initialisation de JDONREFv3Lib", ex);
        }

        return this;
    }

    public JDONREFParams getParams() {
        return params;
    }

    public GestionMots getGestionMots() {
        return gestionMots;
    }
}
