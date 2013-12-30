package ppol.jdonref.geocodeur;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

/**
 * Représente une adresse non validée, où plusieurs propositions sont données.
 * @author jmoquet
 */
public class Propositions extends AdresseNonValide
{
    public ArrayList<AdresseValide> propositions = new ArrayList<AdresseValide>();
    
    /**
     * Construit une erreur à partir d'une adresse
     */
    public Propositions(Adresse a)
    {
        super(a);
    }
    
    /**
     * Constructeur par défaut.
     */
    public Propositions()
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
        
        Element e_propositions = e.getChild("propositions");
        
        List el_propositions = e_propositions.getChildren("proposition");
        
        for(int i=0;i<el_propositions.size();i++)
        {
            Element e_proposition = (Element) el_propositions.get(i);
            
            AdresseValide av = new AdresseValide(this);
            av.load(e_proposition);
            
            propositions.add(av);
        }
    }
    
    /**
     * Retourne la représentation xml de la classe.
     */
    @Override
    public Element toXml()
    {
        Element e_adresse = super.toXml();
        
        Element e_propositions = new Element("propositions");
        
        for(int i=0;i<propositions.size();i++)
        {
            e_propositions.addContent(propositions.get(i).toXml());
        }
        e_adresse.addContent(e_propositions);
        
        return e_adresse;
    }
}