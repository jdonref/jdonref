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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jdom.Element;

/**
 * Permet de conserver des paramètres de connection, ainsi que leur pondération.
 * @author jmoquet
 */
public class ConnectionStruct
{
    String server = null;
    String port = null;
    String database = null;
    String user = null;
    String password = null;
    
    /**
     * Constructeur par défaut.
     */
    public ConnectionStruct() throws ClassNotFoundException
    {
        Class.forName("org.postgresql.Driver");
    }
    
    /**
     * Permet de charger la classe à partir d'une représentation DOM XML
     * @param e
     */
    public void load(Element e) throws Exception
    {
        Element e_server = e.getChild("server");
        Element e_port = e.getChild("port");
        Element e_database = e.getChild("database");
        Element e_user = e.getChild("user");
        Element e_password = e.getChild("password");
        
        if (e_server==null) throw(new Exception("Le paramètre server n'a pas été trouvé."));
        if (e_port==null) throw(new Exception("Le paramètre port n'a pas été trouvé."));
        if (e_database==null) throw(new Exception("Le paramètre database n'a pas été trouvé."));
        if (e_user==null) throw(new Exception("Le paramètre user n'a pas été trouvé."));
        if (e_password==null) throw(new Exception("Le paramètre password n'a pas été trouvé."));
        
        server = e_server.getValue();
        port = e_port.getValue();
        database = e_database.getValue();
        user = e_user.getValue();
        password = e_password.getValue();
    }
    
    /**
     * Obtient une représentation sous forme de chaine respectant la forme:<br>
     * server:port/database(user,password)
     */
    @Override
    public String toString()
    {
        return "jdbc:postgresql://"+server+":"+port+"/"+database;
    }
    
   /**
     * Crée une connection à la base avec les paramètres.
     */
    public Connection connecte() throws SQLException
    {
        return DriverManager.getConnection(toString(),
                                           user,
                                           password);
    }
}
