package ppol.jdonref.geocodeur;

import org.jdom.Element;

/**
 * Permet de récolter les informations concernant le géocodage
 * @author jmoquet
 */
public class InfoGeocodage extends Info
{
    int[] nb_adresse_geocodee = new int[8];

    /**
     * Incrémente le nombre d'adresse géocodée pour le type de géocodage spécifié.
     * @param type
     */
    public void incAdresseGeocodee(TypeGeocodage type)
    {
        nb_adresse_geocodee[TypeGeocodage.getValue(type)]++;
    }
    
    /**
     * Charge l'objet à partir de sa représentation XML.
     * @param e
     */
    @Override
    public void load(Element e)
    {
        super.load(e);
        
        Element e_geocodage = e.getChild("geocodage");
        
        for(int i=0;i<7;i++)
        {
            Element e_nature = e_geocodage.getChild(TypeGeocodage.getValue(i).toString());
            nb_adresse_geocodee[i] = Integer.parseInt(e_nature.getValue());
        }
    }
    
     /**
     * Ecrit dans le fichier info.txt les informations de la classe<br>
     * @param repertoire
     */
    @Override
    public Element toXML()
    {
        Element racine = super.toXML();
        
        racine.setName("info_geocodage");
        
        Element e_geocodage = new Element("geocodage");
        
        for(int i=0;i<7;i++)
        {
            Element e_nature = new Element(TypeGeocodage.getValue(i).toString());
            e_nature.setText(Integer.toString(nb_adresse_geocodee[i]));
            
            e_geocodage.addContent(e_nature);
        }
        
        racine.addContent(e_geocodage);
        
        return racine;
    }
}