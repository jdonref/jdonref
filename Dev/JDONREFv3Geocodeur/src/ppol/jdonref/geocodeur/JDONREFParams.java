package ppol.jdonref.geocodeur;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.jdom.Element;
import ppol.jdonref.JDONREFService;

/**
 * Permet de se connecter à JDONREF.
 * @author jmoquet
 */
public class JDONREFParams
{
    String wsdl;
    String uri;
    String service;
    
    /**
     * Constructeur par défaut.
     */
    public JDONREFParams()
    {
    }
    
    /**
     * Permet de charger la classe à partir des informations stockées dans l'élément.<br>
     * La méthode s'attend à trouver les éléments suivants:
     * <ul>
     *   <li>wsdl</li>
     *   <li>uri</li>
     *   <li>service</li>
     * </ul>
     * @param e
     */
    public void load(Element e) throws Exception
    {
        Element e_wsdl = e.getChild("wsdl");
        Element e_uri = e.getChild("uri");
        Element e_service = e.getChild("service");
        
        if (e_wsdl==null) throw(new Exception("jdonref/wsdl est absent du fichier de configuration"));
        if (e_uri==null) throw(new Exception("jdonref/uri est absent du fichier de configuration"));
        if (e_service==null) throw(new Exception("jdonref/service est absent du fichier de configuration"));
        
        wsdl = e_wsdl.getValue();
        uri = e_uri.getValue();
        service = e_service.getValue();
    }
    
    /**
     * Obtient le service JDONREF correspondant aux paramètres spécifiés.
     */
    public JDONREFService getService() throws MalformedURLException
    {
        if (wsdl!=null && uri!=null && service!=null)
        {
            URL url = null;
            try
            {
                url = new URL(wsdl);
            }
            catch (MalformedURLException ex) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url "+wsdl+" spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", ex);
                throw(ex);
            }
            
            QName qname = new QName(uri,service);
            
            return new JDONREFService(url,qname);
        }
        else
            return new JDONREFService();
    }
}
