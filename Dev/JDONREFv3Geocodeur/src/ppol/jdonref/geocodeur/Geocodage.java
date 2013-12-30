package ppol.jdonref.geocodeur;

/**
 * Enumére les catégories de géocodage disponibles
 * @author jmoquet
 */
public enum Geocodage
{
    /**
     * Le géocodage n'est pas effectué.
     */
    Aucun,
    /**
     * Le géocodage est effectué à la voie.
     */
    Voie,
    /**
     * Le géocodage est effectué à la ville.
     */
    Ville,
    /**
     * Le géocodage est effectué au pays.
     */
    Pays
}
