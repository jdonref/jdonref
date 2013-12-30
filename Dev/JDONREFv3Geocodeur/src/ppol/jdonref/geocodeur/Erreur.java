package ppol.jdonref.geocodeur;

import org.jdom.Element;

/**
 * Représente une adresse en cas d'erreur
 * @author jmoquet
 */
public class Erreur extends AdresseNonValide
{
    public String message;
    
    /**
     * Construit une erreur à partir d'une adresse
     */
    public Erreur(Adresse a)
    {
        super(a);
    }
    
    /**
     * Constructeur par défaut.
     */
    public Erreur()
    {
    }
    
    /**
     * Charge l'objet à partir de sa représentation XML.
     * @param e
     */
    @Override
    public void load(Element e)
    {
        super.load(e);
        
        Element e_erreur = e.getChild("erreur");
        
        message = e_erreur.getValue();
    }
    
    @Override
    /**
     * Retourne la représentation sous forme de chaîne.
     */
    public String toString()
    {
        return " Erreur avec ("+super.toString()+") : "+message;
    }
    
    /**
     * Obtient la réprésentation XML.
     * @return
     */
    @Override
    public Element toXml()
    {
        Element e_adresse = super.toXml();
        
        Element erreur = new Element("erreur");
        erreur.addContent(message);
        
        e_adresse.addContent(erreur);
        
        return e_adresse;
    }
}