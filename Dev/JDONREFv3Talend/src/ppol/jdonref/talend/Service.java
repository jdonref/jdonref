/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.talend;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import ppol.jdonref.talend.client.JDONREFService;

/**
 *
 * @author marcanhe
 */
public class Service {

    private static String JDONREFwsdl;
    private static String JDONREFuri;
    private static String JDONREFservice;
    
    static{
        final Properties config = new Properties();
        try {
            final InputStream in = Service.class.getResourceAsStream("/webservice.properties");
            config.load(in);
            in.close();
            JDONREFwsdl = config.getProperty("wsdl");
            JDONREFuri = config.getProperty("uri");
            JDONREFservice = config.getProperty("service");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static JDONREFService getService() {
        return getJDONREFService(JDONREFwsdl, JDONREFuri, JDONREFservice);
    }

    private static JDONREFService getJDONREFService(String JDONREFwsdl, String uri, String service) {
        if (JDONREFwsdl != null) {
            URL url = null;
            try {
                url = new URL(JDONREFwsdl);
            } catch (MalformedURLException ex) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + JDONREFwsdl + " spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", ex);
            }

            try {
                QName qname = new QName(uri, service);

                return new JDONREFService(url, qname);

            } catch (Exception e) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + JDONREFwsdl + " spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", e);
                return null;
            }
        } else {
            try {
                return new JDONREFService();
            } catch (Exception e) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + JDONREFwsdl + " spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", e);
                return null;
            }
        }
    }
}
