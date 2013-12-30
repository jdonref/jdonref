package ppol.jdonref.geocodeur;

import java.io.IOException;
import java.net.MalformedURLException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ppol.jdonref.JDONREFService;

/**
 * Permet de charger la configuration de JDONREFv2Geocodeur, et d'obtenir le service
 * JDONREF.
 * @author jmoquet
 */
public class Config
{
    public int application;
    public int note;
    public Geocodage geocodage;
    public boolean normalise;
    public boolean restructure;
    public boolean entete;
    public boolean idvoie;
    public boolean codeinsee;
    public boolean codeSovAc3;
    public boolean t0;
    public boolean t1;
    public boolean decoupe;
    public boolean gererPays;
    JDONREFParams params;
    
    /**
     * Constructeur par défaut.
     */
    public Config()
    {
    }
    
    /**
     * Obtient le service JDONREF.
     * @return
     */
    public JDONREFService getService() throws MalformedURLException
    {
        return params.getService();
    }
    
    /**
     * Charge le fichier de configuration de JDONREFv2Geocodeur.<br>
     * La méthode s'attend à trouver l'arborescence suivante:
     * <ul>
     *   <li>jdonref
     *       <ul>
     *           <li>wsdl</li>
     *           <li>uri</li>
     *           <li>service</li>
     *       </ul>
     *   </li>
     *   <li>notemin</li>
     *   <li>application</li>
     *   <li>geocodage avec pour valeur optionnelle voie ou ville</li>
     *   <li>normalise</li>
     *   <li>restructure</li>
     *   <li>gererpays</li>
     *   <li>entete</li>
     *   <li>idvoie</li>
     *   <li>codeinsee</li>
     *   <li>decoupe</li>
     *   <li>t0</li>
     *   <li>t1</li>
     * </ul>
     * @param config
     */
    public void load(String config) throws JDOMException, IOException, Exception
    {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(config);
        
        Element root = d.getRootElement();
        
        if (root.getName().compareTo("params")!=0) throw(new Exception("La structure du fichier de configuration est incorrecte."));
        
        Element e_jdonref = root.getChild("jdonref");
        
        params = new JDONREFParams();
        params.load(e_jdonref);
        
        Element e_notemin = root.getChild("notemin");
        Element e_application = root.getChild("application");
        Element e_geocodage = root.getChild("geocodage");
        Element e_restructure = root.getChild("restructure");
        Element e_gererpays = root.getChild("gererpays");
        Element e_normalise = root.getChild("normalise");
        Element e_entete = root.getChild("entete");
        Element e_idvoie = root.getChild("idvoie");
        Element e_codeinsee = root.getChild("codeinsee");
        Element e_codeSovAc3 = root.getChild("codesovac3");
        Element e_decoupe = root.getChild("decoupe");
        Element e_t0 = root.getChild("t0");
        Element e_t1 = root.getChild("t1");
        
        if (e_application==null)
            throw(new Exception("L'élément application du fichier de configuration n'a pas été trouvé."));
        
        if (e_notemin==null)
            throw(new Exception("L'élément notemin du fichier de configuration n'a pas été trouvé."));
        
        application = Integer.parseInt(e_application.getValue());
        note = Integer.parseInt(e_notemin.getValue());
        
        if (e_geocodage==null) geocodage = Geocodage.Aucun;
        else
        {
            String str_geocodage = e_geocodage.getValue();

            if (str_geocodage.equals("pays"))
            {
                geocodage = Geocodage.Pays;
            }
            else if (str_geocodage.compareTo("ville")==0)
            {
                geocodage=Geocodage.Ville;
            }
            else
                geocodage=Geocodage.Voie;
        }
        
        if (e_restructure!=null) restructure = true;
        if (e_normalise!=null) normalise = true;
        if (e_entete!=null) entete = true;
        if (e_idvoie!=null) idvoie = true;
        if (e_codeinsee!=null) codeinsee = true;
        if (e_decoupe!=null) decoupe = true;
        if (e_t0!=null) t0 = true;
        if (e_t1!=null) t1 = true;
        if(e_gererpays != null) gererPays = true;
        if(e_codeSovAc3 != null) codeSovAc3 = true;
    }
}