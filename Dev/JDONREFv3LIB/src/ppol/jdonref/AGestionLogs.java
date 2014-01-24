/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref;

import java.io.File;

/**
 *
 * @author akchana
 */
public abstract class AGestionLogs implements IGestionLogs {

    public final static int FLAG_NORMALISE_ERREUR = 0;
    public final static int FLAG_NORMALISE_1 = 1;
    public final static int FLAG_NORMALISE_RESTRUCTURE = 2;
    public final static int FLAG_NORMALISE_2 = 4;
    public final static int FLAG_NORMALISE_2_38 = 8;
    public final static int FLAG_NORMALISE_PHONETISE = 16;
    public final static int FLAG_NORMALISE_SANS_ARTICLES = 64;
    public final static int FLAG_VALIDE_ERREUR = 0;
    public final static int FLAG_VALIDE_TYPEDEVOIE = 1;
    public final static int FLAG_VALIDE_LIBELLE = 2;
    public final static int FLAG_VALIDE_CODEPOSTAL = 4;
    public final static int FLAG_VALIDE_COMMUNE = 8;
    public final static int FLAG_VALIDE_ARRONDISSEMENT = 16;
    public final static int FLAG_VALIDE_CODEINSEE = 32;
    public final static int FLAG_VALIDE_CODEFANTOIRE = 64;
    public final static int FLAG_VALIDE_PAYS = 128;
    public final static int FLAG_VALIDE_POIZON = 256;
    public final static int FLAG_GEOCODE_ERREUR = 0;
    public final static int FLAG_GEOCODE_PLAQUE = 1;
    public final static int FLAG_GEOCODE_INTERPOLATION_PLAQUE = 2;
    public final static int FLAG_GEOCODE_INTERPOLATION_TRONCON = 3;
    public final static int FLAG_GEOCODE_CENTROIDE_TRONCON = 4;
    public final static int FLAG_GEOCODE_CENTROIDE_VOIE = 5;
    public final static int FLAG_GEOCODE_COMMUNE = 6;
    public final static int FLAG_GEOCODE_DEPARTEMENT = 7;
    public final static int FLAG_GEOCODE_PAYS = 8;
    public final static int FLAG_GEOCODE_POIZON = 9;
    public final static int FLAG_INVERSE_ERREUR = 0;
    public final static int FLAG_INVERSE_PLAQUE = 1;
    public final static int FLAG_INVERSE_INTERPOLATION_PLAQUE = 2;
    public final static int FLAG_INVERSE_INTERPOLATION_TRONCON = 3;
    public final static int FLAG_INVERSE_CENTROIDE_TRONCON = 4;
    public final static int FLAG_INVERSE_CENTROIDE_VOIE = 5;
    public final static int FLAG_INVERSE_COMMUNE = 6;
    public final static int FLAG_INVERSE_DEPARTEMENT = 7;
    public final static int FLAG_INVERSE_POIZON = 8;
    /**
     * Ce booleen a été crée pour éviter d'inscrire une erreur dans les logs lorsque le système de log
     * ne fonctionne pas.
     */
    
    String repertoire = null;
//  logEchecValidation
    final String logEchecValidation_0 = ":0:";
    final String logEchecValidation_echec = ":Echec de validation";
    final String logEchecValidation_echeclog = "L'échec de revalidation n'a pas pu être loggé.";
//  logNormalisation
    final String logNormalisation_dp_1 = ":1:";
    final String logNormalisation_0 = "0:";
    final String logNormalisation_1 = "1:";
    final String logNormalisation_normalise = "normalise_1";
    final String logNormalisation_restructure = "restructure";
    final String logNormalisation_normalise_2 = "normalise_2";
    final String logNormalisation_normalise_2_38 = "normalise_2 à 38 caractères";
    final String logNormalisation_phonetise = "phonetise";
    final String logNormalisation_sans_articles = "enlève les articles";
    final String logNormalisation_erreurlog = "La normalisation n'a pas pu être loggée";
//  logValidation
    String logValidation_2 = ":2:";
    String logValidation_1 = "1:";
    String logValidation_0 = "0:";
    String logValidation_validationavec = ":validation avec ";
    String logValidation_erreur = "erreur";
    String logValidation_type_de_voie = "type de voie";
    String logValidation_voie = "voie";
    String logValidation_codepostal = "code postal";
    String logValidation_commune = "commune";
    String logValidation_arrondissement = "arrondissement";
    String logValidation_pays = "pays";
    String logValidation_poizon = "poizon";
    String logValidation_nonloggee = "La validation n'a pas pu être loggée";
//  logInverse
    final String logInverse_9 = ":9:";
    final String logInverse_1 = "1:";
    final String logInverse_0 = "0:";
    final String logInverse_inverse = ":inverse ";
    final String logInverse_erreur = "erreur";
    final String logInverse_plaque = "à la plaque";
    final String logInverse_interpolation_plaque = "à l'interpolation à la plaque";
    final String logInverse_interpolation_metrique = "à l'interpolation métrique au tronçon ou aux bornes du tronçon";
    final String logInverse_centroide_troncon = "au centroïde de la voiecentroïde du tronçon";
    final String logInverse_centroide_voie = "au centroïde de la voie";
    final String logInverse_arrondissement = "à l'arrondissement ou à la commune";
    final String logInverse_departement = "au département";
    final String logInverse_poizon = "un poizon";
    final String logInverse_erreurlog = "Le reverse geocoding n'a pas pu être loggé";
//  logGeocodage
    final String logGeocodage_3 = ":3:";
    final String logGeocodage_1 = "1:";
    final String logGeocodage_0 = "0:";
    final String logGeocodage_geocodage = ":geocodage ";
    final String logGeocodage_erreur = "erreur";
    final String logGeocodage_plaque = "à la plaque";
    final String logGeocodage_interpolation_plaque = "à l'interpolation à la plaque";
    final String logGeocodage_interpolation_metrique = "à l'interpolation métrique au tronçon ou aux bornes du tronçon";
    final String logGeocodage_centroide_troncon = "au centroïde du tronçon";
    final String logGeocodage_centroide_voie = "au centroïde de la voie";
    final String logGeocodage_arrondissement = "à l'arrondissement ou à la commune";
    final String logGeocodage_departement = "au département";
    final String logGeocodage_pays = "au pays";
    final String logGeocodage_poizon = "d'un poizon";
    final String logGeocodage_erreurlog = "Le géocodage n'a pas pu être loggé";
//  logVersion
    final String logVersion_8 = ":8:";
    final String logVersion_getVersion = ":getVersion";
    final String logVersion_nonlogge = "La récupération de la version n'a pas pu être loggée";
//  logContacte
    final String logContacte_7 = ":7:";
    final String logContacte_contacte = ":contacte";
    final String logContacte_nonlogge = "Le contact n'a pas pu être loggé";
//  logDecoupage    
    final String logDecoupage_4 = ":4:";
    final String logDecoupage_decoupage = ":decoupage";
    final String logDecoupage_nonlogge = "Le découpage n'a pas pu être loggé";
//  logGetState
    final String logGetState_5 = ":5:";
    final String logGetState_getstate = ":getState(0)";
    final String logGetState_nonlogge = "La récupération de l'état de JDONREF n'a pas pu être loggée";
//  logRevalidation
    final String logRevalidation_6 = ":6:";
    final String logRevalidation_revalidation = ":revalidation";
    final String logRevalidation_logerreur = "La revalidation n'a pas pu être loggée";

    /**
     * Obtient le répertoire où sont placés les fichiers de log.
     * @return le répertoire en cours.
     */
    public String obtientRepertoire() {
        return repertoire;
    }

    /**
     * Obtient un numéro de version pour un processus.<br>
     * Ce numéro est utilisé dans le nom du fichier de log du processus.
     * @param processus
     */
    public int obtientNumeroVersion(int processus) {
        File d = new File(repertoire);
        LogAdminFilter laf = new LogAdminFilter(processus);

        File[] files = d.listFiles(laf);

        int max = 0;

        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            int version = LogAdminFilter.extraitVersion(name);
            if (version >= max) {
                max = version + 1;
            }
        }

        return max;
    }
    
    /**
     * Définit le répertoire où sont placés les fichiers de log.
     * @param repertoire le répertoire
     */
    public void definitRepertoire(String repertoire) throws JDONREFException {
        File f = new File(repertoire);
        if (!f.exists()) {
            throw (new JDONREFException("Le répertoire " + repertoire + " n'a pas été trouvé."));
        }
        this.repertoire = repertoire;
    }
    
}




