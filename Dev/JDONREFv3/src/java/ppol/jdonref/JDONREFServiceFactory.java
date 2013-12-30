/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import ppol.jdonref.wsclient.JDONREFService;
import ppol.jdonref.wservice.ServiceParameters;

/**
 *
 * @author marcanhe
 */
public class JDONREFServiceFactory {

    public static JDONREFService getInstance(ServiceParameters parametres) {
        if (parametres.getWsdl() != null) {
            URL url = null;
            try {
                url = new URL(parametres.getWsdl());
            } catch (MalformedURLException ex) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + parametres.getWsdl() + " spÃ©cifiÃ©e pour JDONREFv3 dans le fichier de configuration est incorrecte.", ex);
            }

            try {
                QName qname = new QName(parametres.getUri(), parametres.getService());

                return new JDONREFService(url, qname);

            } catch (Exception e) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + parametres.getWsdl() + " spÃ©cifiÃ©e pour JDONREFv3 dans le fichier de configuration est incorrecte.", e);
                return null;
            }
        } else {
            try {
                return new JDONREFService();
            } catch (Exception e) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + parametres.getWsdl() + " spÃ©cifiÃ©e pour JDONREFv3 dans le fichier de configuration est incorrecte.", e);
                return null;
            }
        }
    }
}
