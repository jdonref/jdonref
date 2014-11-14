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
package ppol.jdonref;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Permet de gérer les connections au référentiel.<br>
 * Les connections sont organisées en série. Pour chaque série, les paramètres de connection sont identiques.<br>
 * Il est possible d'attribuer à chaque série une taille ainsi qu'une pondération.<br>
 * Toutes les séries doivent disposer des mêmes données.<br>
 * <br>
 * Il est possible de suivre la charge des bases si les méthodes obtientConnection et relache sont correctement utilisées.
 * <br>
 * L'attribution des connections se fait au hasard, en respectant les probabilités données
 * par chaque pondération. Toutefois, les connections présentant moins de 50% de charge sont privilégiées.<br>
 * Lorsqu'une connection ne parvient pas a être établie, une autre connection aléatoire est choisie si possible.<br>
 * @author jmoquet
 */
public class GestionConnectionES
{
    protected String server = null;
    protected String port = null;
    protected String index = null;
    
    private JDONREFParams params = null;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public String getURL()
    {
        return "http://"+this.server+":"+this.port+"/"+this.index;
    }
    
    /*
     * Crée l'objet GestionConnection sans aucun paramètres de connection.
     */
    public GestionConnectionES(JDONREFParams params)
    {
        this.params = params;
    }
    
    /**
     * Charge un fichier xml de configuration de la connection à ES.<br>
     * La structure est telle que:<br>
     * connections
     * <ul><li>connection
     *         <ul><li>server</li>
     *             <li>port</li>
     *             <li>index</li>
     * </li>
     * </ul>
     * @param file
     */
    public void load(String file) throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder();
        Document d = builder.build(file);
        Element root = d.getRootElement();
        
        if (root.getName().compareTo("connections")!=0)
        {
            Logger.getLogger("GestionConnectionES").log(Level.SEVERE,"La structure du fichier "+file+" n'est pas correcte (racine "+root.getName()+").");
            throw(new IOException("La structure du fichier "+file+" n'est pas correcte (racine "+root.getName()+"."));
        }
        boolean connectionok = true;
            
        for(int i=0;i<Math.min(1,root.getChildren().size());i++)
        {
            Element child = (Element) root.getChildren().get(i);
            
            if (child.getName().compareTo("connection")!=0)
            {
                Logger.getLogger("GestionConnectionES").log(Level.INFO,"Un des �l�ments du fichier "+file+" n'est pas correct ("+child.getName()+").");
            }
            
            for(int j=0;j<child.getChildren().size();j++)
            {
                Element subchild = (Element)child.getChildren().get(j);
                String subchildname = subchild.getName();
                
                if (subchildname.compareTo("server")==0)
                {
                    server = subchild.getText();
                }
                else if (subchildname.compareTo("port")==0)
                {
                    port = subchild.getText();
                }
                else if (subchildname.compareTo("index")==0)
                {
                    index = subchild.getText();
                }
            }

            if (server==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnectionES").log(Level.SEVERE,"Une connection ne contient pas d'attribut server.");
            }
            if (port==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnectionES").log(Level.SEVERE,"Une connection ne contient pas d'attribut port.");
            }
            if (index==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnectionES").log(Level.SEVERE,"Une connection ne contient pas d'attribut index.");
            }
            if (!connectionok)
            {
                Logger.getLogger("GestionConnectionES").log(Level.SEVERE,"Cette connection est ignorée.");
            }
        }
        
        if (!connectionok)
        {
            Logger.getLogger("GestionConnectionES").log(Level.SEVERE,"Aucune connection n'est disponible.");
        }
        else
        {
            Logger.getLogger("GestionConnectionES").log(Level.INFO,"ES disponibles : 1");
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            JDONREFParams params = new JDONREFParams();
            GestionConnectionES gc=new GestionConnectionES(params);
            gc.load("connectionsES.xml");
        }
                 catch(JDOMException ex)
        {
            Logger.getLogger(GestionConnectionES.class.getName()).log(Level.SEVERE,null,ex);
        }        catch(IOException ex)
        {
            Logger.getLogger(GestionConnectionES.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}