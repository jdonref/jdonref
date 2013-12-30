package ppol.jdonref.geocodeur;

/**
 *
 * @author jmoquet
 */
public enum StatutValidation
{
    /**
     * Retourné en cas d'erreur de validation.
     */
    pb,
    /**
     * Retourné au cas où une et une seule adresse a une note satisfaisante.
     */
    valide,
    /**
     * Retourné au cas où plusieurs adresses ou aucune adresse n'est satisfaisante.
     */
    choix
}
