/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public interface IJDONREFv3 {

    ResultatNormalisation normalise(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options);

    ResultatValidation valide(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] ids,
            String[] options);

    /**
     * Géocode l'adresse valide spécifiée.
     * @return voir GestionReferentiel.geocode et GestionValidation.
     */
    ResultatGeocodage geocode(
            int application,
            int[] services,
            String[] donnees,
            String[] ids,
            String[] options);

    /**
     * Revalide une adresse validée au préalable.
     */
    ResultatRevalidation revalide(
            int application,
            int[] services,
            String[] donnees,
            String[] ids,
            String date,
            String[] options);

    /**
     * Effectue un géocodage inverse des coordonnées spécifiées.
     */
    ResultatGeocodageInverse inverse(
            int application,
            int[] services,
            String[] donnees,
            double distance,
            String[] options);

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
    ResultatDecoupage decoupe(
            int application,
            int[] services,
            int[] operations,
            String[] donnees,
            String[] options);

    /**
     * Permet de signaler un défaut à l'administrateur.
     */
    ResultatContacte contacte(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options);

    /**
     * Obtient la version de JDONREF.
     */
    ResultatVersion getVersion(
            int application,
            int[] services);
}
