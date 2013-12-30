package ppol.jdonref.geocodeur;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Permet de récolter les informations concernant la validation
 * @author jmoquet
 */
public class Info
{   
    public int nb_erreurs_grave = 0;
    public int nb_adresses_validees = 0;
    public int nb_adresses_non_validees = 0;
    public int total_notes_validees = 0;
    int start = 0;
    long duree = 0;
    
    /**
     * Obtient la moyenne des notes attribuées aux adresses validées
     * @return
     */
    public int obtientNoteMoyenneValidee()
    {
        if (nb_adresses_validees!=0)
            return total_notes_validees/nb_adresses_validees;
        else
            return 0;
    }

    /**
     * Lit l'objet à partir de sa représentation XML.
     */
    public void load(Element e)
    {
       Element e_duree = e.getChild("duree");
       Element e_adresses_validees = e.getChild("adresses_validees");
       Element e_adresses_non_validees = e.getChild("adresses_non_validees");
       Element e_note_moyennes_validees = e.getChild("note_moyennes_validees");
       Element e_start = e.getChild("depart");
       Element e_erreurs_grave = e.getChild("erreur_grave");
       
       if (e_start==null) start = 0;
       else start = Integer.parseInt(e_start.getValue());
       duree = Long.parseLong(e_duree.getValue());
       nb_adresses_validees = Integer.parseInt(e_adresses_validees.getValue());
       nb_adresses_non_validees = Integer.parseInt(e_adresses_non_validees.getValue());
       if (e_erreurs_grave!=null) nb_erreurs_grave = Integer.parseInt(e_erreurs_grave.getValue());
       
       int note_moyennes_validees = Integer.parseInt(e_note_moyennes_validees.getValue());
       total_notes_validees = nb_adresses_validees*note_moyennes_validees;
    }
    
    /**
     * Lit l'objet depuis le fichier info.xml.<br>
     * @return null si la racine n'est pas correcte.
     */
    public static Info read(String repertoire) throws JDOMException, IOException
    {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(repertoire+"/info.xml");
        
        Element root = d.getRootElement();
        
        if (root.getName().compareTo("info")==0)
        {
            Info i = new Info();
            i.load(root);
            return i;
        }
        else if (root.getName().compareTo("info_geocodage")==0)
        {
            InfoGeocodage ig = new InfoGeocodage();
            ig.load(root);
            return ig;
        }
        return null;
    }
    
    /**
     * Obtient la représentation XML de l'objet.
     * @return
     */
    public Element toXML()
    {
        Element racine = new Element("info");
        Element e_duree = new Element("duree");
        Element e_adresses_validees = new Element("adresses_validees");
        Element e_adresses_nonvalidees = new Element("adresses_non_validees");
        Element e_note_moyennes_validees = new Element("note_moyennes_validees");
        Element e_start = new Element("depart");
        Element e_erreur_grave = new Element("erreur_grave");
        
        racine.addContent(e_duree);
        racine.addContent(e_adresses_validees);
        racine.addContent(e_adresses_nonvalidees);
        racine.addContent(e_note_moyennes_validees);
        racine.addContent(e_erreur_grave);
        racine.addContent(e_start);
        
        e_duree.setText(Long.toString(duree));
        e_adresses_validees.setText(Integer.toString(nb_adresses_validees));
        e_adresses_nonvalidees.setText(Integer.toString(nb_adresses_non_validees));
        e_note_moyennes_validees.setText(Integer.toString(obtientNoteMoyenneValidee()));
        e_erreur_grave.setText(Integer.toString(nb_erreurs_grave));
        e_start.setText(Long.toString(start));
        
        return racine;
    }
    
    /**
     * Ecrit dans le fichier info.xml les informations de la classe
     * @param repertoire
     * @return false en cas d'erreur
     */
    public boolean write(String repertoire) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
	Document d = new Document(toXML());
        XMLOutputter outputter=new XMLOutputter();
        OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(repertoire+"/info.xml"),"UTF-8");
        outputter.output(d,writer);

        writer.close();
        
        return true;
    }
}