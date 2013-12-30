package ppol.jdonref.geocodeur;

/**
 * Quelques algorithmes "utiles"
 * @author jmoquet
 */
public class Util
{    
    /**
     * Permet de supprimer les caractères " présents en début et fin de chaine.
     * @param chaine
     * @return
     */
    public static String noGuillemets(String chaine)
    {
        if (chaine!=null && chaine.length()>=2 && chaine.charAt(0)=='"' && chaine.charAt(chaine.length()-1)=='"')
            return chaine.substring(1,chaine.length()-1);
        return chaine;
    }
}
