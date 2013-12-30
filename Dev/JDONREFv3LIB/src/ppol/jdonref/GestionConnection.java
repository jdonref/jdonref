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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
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
public class GestionConnection
{
    // Liste des bases.
    private ArrayList<Base> bases = new ArrayList<Base>();
    
    private int totalponderation = 0;
        
    private Random r = new Random(Calendar.getInstance().getTimeInMillis());
    
    private JDONREFParams params = null;
    
    /*
     * Crée l'objet GestionConnection sans aucun paramètres de connection.
     */
    public GestionConnection(JDONREFParams params) throws ClassNotFoundException
    {
        Class.forName("org.postgresql.Driver");
        this.params = params;
    }
    
    /**
     * Charge un fichier xml de configuration des connections.<br>
     * La structure est telle que:<br>
     * connections
     * <ul><li>connection
     *         <ul><li>server</li>
     *             <li>port</li>
     *             <li>user</li>
     *             <li>password</li>
     *             <li>database</li>
     *             <li>ponderation La ponderation 0 est acceptée, auquel cas la connection n'est jamais utilisée.</li></ul>
     *             <li>connections le nombre de connections</li>
     * </li>
     * <li>connection
     *         <ul><li>server</li>
     *             <li>port</li>
     *             <li>user</li>
     *             <li>password</li>
     *             <li>database</li>
     *             <li>ponderation</li></ul>
     *             <li>connections le nombre de connections</li>
     * </li>
     * <li>...</li>
     * </ul>
     * Les série et leurs index sont initialisés.
     * @param file
     */
    public void load(String file) throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder();
        Document d = builder.build(file);
        Element root = d.getRootElement();
        
        if (root.getName().compareTo("connections")!=0)
        {
            Logger.getLogger("GestionConnection").log(Level.SEVERE,"La structure du fichier "+file+" n'est pas correcte (racine "+root.getName()+").");
            throw(new IOException("La structure du fichier "+file+" n'est pas correcte (racine "+root.getName()+"."));
        }
        
        for(int i=0;i<root.getChildren().size();i++)
        {
            Element child = (Element) root.getChildren().get(i);
            
            if (child.getName().compareTo("connection")!=0)
            {
                Logger.getLogger("GestionConnection").log(Level.INFO,"Un des �l�ments du fichier "+file+" n'est pas correct ("+child.getName()+").");
            }
            
            ConnectionStruct connectionstruct = new ConnectionStruct();
            
            int ponderation = -1, nbconnections = -1; // Les paramètres ponderation et connections disposent de valeurs par d�faut
                                                      // initialis�s dans la structure et sont initialis�s diff�remment.
            
            for(int j=0;j<child.getChildren().size();j++)
            {
                Element subchild = (Element)child.getChildren().get(j);
                String subchildname = subchild.getName();
                
                if (subchildname.compareTo("server")==0)
                {
                    if (connectionstruct.server==null)
                        connectionstruct.server = subchild.getText();
                    else
                    {
                        Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut server. '"+connectionstruct.server+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                    }
                }
                else if (subchildname.compareTo("port")==0)
                {
                    if (connectionstruct.port==null)
                        connectionstruct.port = subchild.getText();
                    else
                    {
                        Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut port. '"+connectionstruct.port+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                    }
                }
                else if (subchildname.compareTo("user")==0)
                {
                    if (connectionstruct.user==null)
                        connectionstruct.user = subchild.getText();
                    else
                    {
                        Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut user. '"+connectionstruct.user+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                    }
                }
                else if (subchildname.compareTo("password")==0)
                {
                    if (connectionstruct.password==null)
                        connectionstruct.password = subchild.getText();
                    else
                    {
                        Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut password. '"+connectionstruct.password+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                    }
                }
                else if (subchildname.compareTo("database")==0)
                {
                    if (connectionstruct.database==null)
                        connectionstruct.database = subchild.getText();
                    else
                    {
                        Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut database. '"+connectionstruct.database+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                    }
                }
                else if (subchildname.compareTo("ponderation")==0)
                {
                    int temp=-1;
                    boolean error = false;
                    
                    try
                    {
                        temp = Integer.parseInt(subchild.getText());
                    }
                    catch(NumberFormatException nfe)
                    {
                        error = true;
                    }
                    
                    if (temp<0) error = true;
                    
                    if (error)
                    {
                        if (ponderation==-1)
                        {
                            Logger.getLogger("GestionConnection").log(Level.INFO,"La valeur saisie pour l'attribut ponderation n'est pas correcte ('"+subchild.getText()+"' n'est pas un entier positif).");
                        }
                        else
                        {
                            Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut ponderation et la valeur saisie n'est pas correcte. '"+ponderation+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                        }
                    }
                    else
                    {
                        if (ponderation==-1)
                            ponderation = temp;
                        else
                        {
                            Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut ponderation. '"+ponderation+"'est conserv�. '"+temp+"' est ignor�.");
                        }
                    }
                }
                else if (subchildname.compareTo("connections")==0)
                {
                    int temp=-1;
                    boolean error = false;
                    
                    try
                    {
                        temp = Integer.parseInt(subchild.getText());
                    }
                    catch(NumberFormatException nfe)
                    {
                        error = true;
                    }
                    
                    if (temp<0) error = true;
                    
                    if (error)
                    {
                        if (nbconnections==-1)
                        {
                            Logger.getLogger("GestionConnection").log(Level.INFO,"La valeur saisie pour l'attribut connections n'est pas correcte ('"+subchild.getText()+"' n'est pas un entier positif).");
                        }
                        else
                        {
                            Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut connections et la valeur saisie n'est pas correcte. '"+nbconnections+"'est conserv�. '"+subchild.getText()+"' est ignor�.");
                        }
                    }
                    else
                    {
                        if (nbconnections==-1)
                            nbconnections = temp;
                        else
                        {
                            Logger.getLogger("GestionConnection").log(Level.INFO,"Une connection contient plusieurs valeurs pour l'attribut connections. '"+nbconnections+"'est conserv�. '"+temp+"' est ignor�.");
                        }
                    }
                }
            }

            boolean connectionok = true;
            if (connectionstruct.server==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnection").log(Level.SEVERE,"Une connection ne contient pas d'attribut server.");
            }
            if (connectionstruct.port==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnection").log(Level.SEVERE,"Une connection ne contient pas d'attribut port.");
            }
            if (connectionstruct.user==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnection").log(Level.SEVERE,"Une connection ne contient pas d'attribut user.");
            }
            if (connectionstruct.password==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnection").log(Level.SEVERE,"Une connection ne contient pas d'attribut password.");
            }
            if (connectionstruct.database==null)
            {
                connectionok = false;
                Logger.getLogger("GestionConnection").log(Level.SEVERE,"Une connection ne contient pas d'attribut database.");
            }
            if (ponderation!=-1) // sinon la valeur par d�faut est conserv�e.
            {
                connectionstruct.ponderation = ponderation;
            }
            if (nbconnections!=-1)
            {
                connectionstruct.connections = nbconnections;
            }
            
            if (!connectionok)
            {
                Logger.getLogger("GestionConnection").log(Level.SEVERE,"Cette connection est ignor�e.");
            }
            else
            {
                totalponderation+=connectionstruct.ponderation;
                Base rc = new Base(connectionstruct,params);
                bases.add(rc);
            }
        }
        
        if (bases.size()==0)
        {
            Logger.getLogger("GestionConnection").log(Level.SEVERE,"Aucune connection n'est disponible.");
        }
        else
        {
            Logger.getLogger("GestionConnection").log(Level.INFO,"Bases de données disponibles : "+bases.size());
            Logger.getLogger("GestionConnection").log(Level.INFO,"Connections disponibles pour chaque base:");
            for(int i=0;i<bases.size();i++)
            {
                Logger.getLogger("GestionConnection").log(Level.INFO,"Base "+i+": "+this.bases.get(i).obtientParametres().connections);
            }
        }
    }
    
    /**
     * Supprime tous les paramètres et ferme les connections existantes.
     */
    public void resetConnectionsParameters() throws SQLException
    {
        resetConnections();
        bases = new ArrayList<Base>();
        totalponderation = 0;
    }
    
    /**
     * Ferme toutes les connections.
     * @throws java.sql.SQLException
     */
    public void resetConnections() throws SQLException
    {
        for(int i=0;i<bases.size();i++)
        {
            bases.get(i).resetConnections();
        }
    }

    /**
     * Initialise les connections.<br>
     * Si des connections sont déjà effectuées, elles sont rompues.
     */
    public void connecte() throws SQLException
    {
        for(int i=0;i<bases.size();i++)
        {
            bases.get(i).connecte();
        }
    }
    
    /**
     * Obtient l'index aléatoire d'une base en respectant les pondérations.<br>
     * Une première recherche est effectuée sur les bases possédant moins de 50% de charge.<br>
     * Toutes les bases sont considérées indépendament de leur charge.
     * @return
     */
    private int obtientBaseAleatoire()
    {
        if (totalponderation>0)
        {
            int index = r.nextInt(totalponderation);
            int sum = 0;
            for(int i=0;i<bases.size()-1;i++)
            {
                sum+=bases.get(i).obtientParametres().ponderation;
                if (index<sum)
                {
                    return i;
                }
            }
            return bases.size()-1;
        }
        else
            return -1;
    }
    
    /**
     * Obtient la charge actuelle de JDONREF v2 en pourcentage.<br>
     * La charge est calculée à partir de la charge de chaque base qui dépend
     * de son nombre d'utilisateur et de son nombre de connections disponibles
     * et de la pondération de chaque base.
     * @return le pourcentage de charge qui peut dépasser 100%
     */
    public int obtientCharge()
    {
        if (totalponderation==0) return 0;
        int totalcharge = 0;
        for(int i=0;i<bases.size();i++)
        {
            totalcharge += bases.get(i).obtientCharge();
        }
        return totalcharge/totalponderation;
    }
    
    /**
     * Obtient les bases dont la charge est inférieure à 50%.
     * @return
     */
    private ArrayList<Base> obtientBasesLibres()
    {
        ArrayList<Base> l_bases = new ArrayList<Base>();
        for(int i=0;i<l_bases.size();i++)
        {
            if (l_bases.get(i).obtientCharge()<50)
                l_bases.add(l_bases.get(i));
        }
        return l_bases;
    }
   
    /**
     * Obtient l'index aléatoire d'une base en respectant les pondérations.<br>
     * Une première recherche est effectuée sur les bases possédant moins de 50% de charge.<br>
     * S'il n'y en a aucune, elles sont toutes prises en considération.
     * @return
     */
    private int obtientBaseAleatoire(boolean toutes)
    {
        if (!toutes)
        {
            ArrayList<Base> basesLibres=obtientBasesLibres();

            if (basesLibres.size()>0)
            {
                // Calcule la ponderation des bases < 50%.
                int totalponderationlibre=0;

                for(int i=0; i<basesLibres.size(); i++)
                {
                    totalponderationlibre+=basesLibres.get(i).obtientParametres().ponderation;
                }

                if (totalponderationlibre>0)
                {
                    int index=r.nextInt(totalponderationlibre);
                    int sum=0;
                    for(int i=0; i<basesLibres.size()-1; i++)
                    {
                        sum+=basesLibres.get(i).obtientParametres().ponderation;
                        if (index<sum)
                        {
                            return i;
                        }
                    }
                    return basesLibres.size()-1;
                }
            }
        }
        return obtientBaseAleatoire();
    }
    
    /**
     * Obtient une connection aléatoire au référentiel.<br>
     * Une série aléatoire est choisie. Les connections sont choisies une à une dans cette série.
     * Si une connection ne fonctionne pas pour cette série, une autre série est choisie.<br>
     * @throws SQLException erreur SQL lors de la deuxième tentative de connection.
     * @return une référence vers une connection null si aucune connection n'a été trouvée.
     */
    public RefConnection obtientConnection() throws SQLException, JDONREFException
    {
        int serie = obtientBaseAleatoire();
        
        if (serie==-1)
        {
            throw(new SQLException("Aucune connection n'est disponible."));
        }
        
        RefConnection con = bases.get(serie).obtientConnection();

        if (con.connection==null || con.connection.isClosed())
        {
            Logger.getLogger("GestionConnection").log(Level.INFO,
                    "Problème lors de la connection ("+bases.get(serie).toString()+"). Recherche une autre connection si possible.");

            if (bases.size()>1)
            {
                int autreserie;
                do
                {
                    autreserie = obtientBaseAleatoire(false);
                } while(autreserie==serie);
                
                con = bases.get(autreserie).obtientConnection();
                
                if (con.connection==null || con.connection.isClosed())
                {
                    Logger.getLogger("GestionConnection").log(Level.INFO,
                        "Problème lors de la connection à ("+bases.get(autreserie).toString()+"). Abandon.");
                    throw(new SQLException("Les deux tentatives de connection ont échoués."));
                }

                bases.get(autreserie).reserve(con.index);
                con.base = autreserie;
            }
        }
        else
        {
            bases.get(serie).reserve(con.index);
            con.base = serie;
        }
        
        return con;
    }
    
    /**
     * Relache une connection préalablement réservée.<br>
     * Rien n'est fait si rc est null.
     * @param rc la référence à la connection en question.
     */
    public void relache(RefConnection rc)
    {
        if (rc!=null)
            bases.get(rc.base).relache(rc.index);
    }
    
    public static void main(String[] args)
    {
        try
        {
            JDONREFParams params = new JDONREFParams();
            GestionConnection gc=new GestionConnection(params);
            gc.load("connections.xml");
            
            gc.connecte();
            
            for(int i=0;i<gc.bases.size();i++)
            {
                for(int j=0;j<gc.bases.get(i).cs.connections;j++)
                {
                    Connection c = gc.bases.get(i).obtientConnection(j);
                    
                    boolean ok = Base.isConnectionOk(c);
                    
                    System.out.println("La connection "+j+" de la série "+i+" a pour statut : "+(ok?"ok":"bad"));
                }
            }
            
            RefConnection rc = gc.obtientConnection();
            gc.relache(rc);
            
            gc.resetConnections();
        }
        catch(JDONREFException ex)
        {
            Logger.getLogger(GestionConnection.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(SQLException ex)
        {
            Logger.getLogger(GestionConnection.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(JDOMException ex)
        {
            Logger.getLogger(GestionConnection.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(IOException ex)
        {
            Logger.getLogger(GestionConnection.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(ClassNotFoundException ex)
        {
            Logger.getLogger(GestionConnection.class.getName()).log(Level.SEVERE,null,ex);
        }
        
    }
}