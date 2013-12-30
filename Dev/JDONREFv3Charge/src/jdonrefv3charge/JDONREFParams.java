/*
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
package jdonrefv3charge;

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
        else{
            return new JDONREFService();
        }
    }
}
