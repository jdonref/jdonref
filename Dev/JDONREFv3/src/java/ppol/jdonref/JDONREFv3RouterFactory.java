/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref;


import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.wservice.IJDONREFv3Router;

/**
 *
 * @author marcanhe
 */
public class JDONREFv3RouterFactory {

    private static IJDONREFv3Router instance;

    public static IJDONREFv3Router getInstance(ppol.jdonref.referentiel.JDONREFv3Lib jdonrefv3lib) throws JDONREFv3Exception {
        if (instance == null) {
            try {
                instance = (IJDONREFv3Router) Class.forName(jdonrefv3lib.getParams().obtientRouterClassName()).newInstance();
            } catch (ClassNotFoundException cnfe) {
                Logger.getLogger(JDONREFv3RouterFactory.class.getName()).log(Level.SEVERE, "Problème lors de l'instantiation du routeur JDONREVv3.", cnfe);
                throw new JDONREFv3Exception(1, "Erreur lors de la configuration du service de routage");
            } catch (InstantiationException ie) {
                Logger.getLogger(JDONREFv3RouterFactory.class.getName()).log(Level.SEVERE, "Problème lors de l'instantiation du routeur JDONREVv3.", ie);
                throw new JDONREFv3Exception(1, "Erreur lors de la configuration du service de routage");
            } catch (IllegalAccessException iae) {
                Logger.getLogger(JDONREFv3RouterFactory.class.getName()).log(Level.SEVERE, "Problème lors de l'instantiation du routeur JDONREVv3.", iae);
                throw new JDONREFv3Exception(1, "Erreur lors de la configuration du service de routage");
            }
            instance.init(jdonrefv3lib);
        }
        
        return instance;
    }
}