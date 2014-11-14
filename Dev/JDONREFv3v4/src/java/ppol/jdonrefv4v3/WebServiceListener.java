/**
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
 * julien.moquet@interieur.gouv.fr
 * 
 * Ce logiciel est un service web servant à valider et géocoder des adresses postales.
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant 
 * les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier 
 * et/ou redistribuer ce programme sous les conditions de la licence CeCILL telle que 
 * diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info".
 * En contrepartie de l'accessibilité au code source et des droits de copie, de 
 * modification et de redistribution accordés par cette licence, il n'est offert aux 
 * utilisateurs qu'une garantie limitée.  Pour les mêmes raisons, seule une 
 * responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits 
 * patrimoniaux et les concédants successifs.
 * A cet égard l'attention de l'utilisateur est attirée sur les risques associés au 
 * chargement,  à l'utilisation,  à la modification et/ou au développement et à la 
 * reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel 
 * libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des 
 * développeurs et des professionnels avertis possédant  des  connaissances 
 * informatiques approfondies.  Les utilisateurs sont donc invités à charger  et tester
 * l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser
 * et l'exploiter dans les mêmes conditions de sécurité. 
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris 
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes. 
 */
package ppol.jdonrefv4v3;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextEvent;

/**
 * Implémentation d'un écouteur pour le web service WebServiceFindRue.<br>
 * Est utilisée pour:
 * <ul><li>le déchargement du Driver jdbc du DriverManager</li>
 *     <li>le déchargement des MBeanServer de MBeanServerFactory</li>
 * </ul>
 * Ces déchargements permettent de réduire le nombre d'erreurs PermGenSpace.
 * @author jmoquet
 */
public class WebServiceListener implements javax.servlet.ServletContextAttributeListener, javax.servlet.ServletContextListener {

    public void attributeAdded(ServletContextAttributeEvent arg0) {
    }

    public void attributeRemoved(ServletContextAttributeEvent arg0) {
    }

    public void attributeReplaced(ServletContextAttributeEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent arg0) {
    }

    @SuppressWarnings(value = "unchecked")
    public void contextDestroyed(ServletContextEvent arg0) {
        ArrayList<MBeanServer> server = MBeanServerFactory.findMBeanServer(null);

        for (int i = 0; i < server.size(); i++) {
            MBeanServer mb = server.get(i);
            if (mb.getClass().getClassLoader() == getClass().getClassLoader()) {
                MBeanServerFactory.releaseMBeanServer(server.get(i));
            }
        }

        Enumeration e = DriverManager.getDrivers();
        Driver d = null;
        while (e.hasMoreElements()) {
            d = (Driver) e.nextElement();
            try {
                if (d.getClass().getClassLoader() == getClass().getClassLoader()) {
                    DriverManager.deregisterDriver(d);
                }
            } catch (SQLException ex) {
                Logger.getLogger(WebServiceListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
