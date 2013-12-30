package ppol.jdonref.geocodeur;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Contient l'ensemble des rejets obtenus suite à une validation d'adresse.
 * @author jmoquet
 */
public class Rejets
{
    public ArrayList<AdresseNonValide> adresses = new ArrayList<AdresseNonValide>();
    
    /**
     * Permet d'écrire la représentation xml de la classe dans un fichier.
     * Des messages d'erreurs peuvent être émis sur la sortie standard pour chaque adresse
     * qui n'a pas été inscrite dans le fichier.
     * @return false en cas d'erreur
     */
    public boolean write(String repertoire) throws IOException
    {
        boolean error = false;
        Element racine = new Element("rejets");
        for(int i=0;i<adresses.size();i++)
        {
            try
            {
                racine.addContent(adresses.get(i).toXml());
            }
            catch(Exception e)
            {
                System.out.println("Impossible d'écrire "+adresses.get(i).toString()+" dans le fichier rejets.xml : "+e.getMessage());
                error = true;
            }
        }
        
        Document d=new Document(racine);
        XMLOutputter outputter=new XMLOutputter();
        OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(repertoire+"/rejets.xml"),"UTF-8");
        outputter.output(d,writer);
        writer.close();
        
        return error;
    }
    
    /**
     * Permet de lire les rejets à partir du fichier rejets.xml trouvé dans le répertoire.
     * @param repertoire
     */
    public void read(String repertoire,Config config) throws JDOMException, IOException
    {
       	SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(repertoire+"/rejets.xml");
        
        Element root = d.getRootElement();
        
        List rejets = root.getChildren("adresse");
        
        for(int i=0;i<rejets.size();i++)
        {
            AdresseNonValide anv = AdresseNonValide.loadElement((Element)rejets.get(i),config);
            anv.config = config;
            adresses.add(anv);
        }
    }
}