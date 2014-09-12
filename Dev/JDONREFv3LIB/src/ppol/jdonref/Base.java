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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Permet de gérer les connections à un référentiel.
 * @author jmoquet
 */
public class Base
{
    ArrayList<Connection> connections = new ArrayList<Connection>();
    ConnectionStruct cs = null;
    JDONREFParams params = null;

    /**
     * L'index de la dernière connection utilisée.
     */
    int index = 0;
    
    /**
     * Le nombre d'utilisateurs affectés à une connection.
     */
    ArrayList<Integer> users = new ArrayList<Integer>();
    
    /**
     * Obtient les paramètres de la connection à cette base.
     * @return
     */
    public ConnectionStruct obtientParametres()
    {
        return cs;
    }
    
    /**
     * Construit un nouveau gestionnaire de connection.
     */
    public Base(ConnectionStruct cs,JDONREFParams params)
    {
        this.cs = cs;
        this.params = params;
        for(int j=0;j<cs.connections;j++)
        {
           connections.add(null);
           users.add(new Integer(0));
        }
    }
    
    /**
     * Vérifie si une connection est opérationnelle.
     * @return
     */
    public static boolean isConnectionOk(Connection c)
    {
        try
        {
            Statement st = c.createStatement();
            st.executeQuery("SELECT true;");
        }
        catch(SQLException sqle)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Teste si une connection d'une série fonctionne.<br>
     * Si la connection est fermée, l'ouvre.
     * @param serie
     * @param index
     * @return
     * @throws java.sql.SQLException
     */
    private Connection testeConnection(int index) throws SQLException
    {
        Connection con = connections.get(index);
        if (con==null || !isConnectionOk(con))
        {
            if (con==null)
//                Logger.getLogger("GestionConnection").log(Level.INFO,"Initialise la connection : "+toString());
                params.getGestionLog().logs_INFO("GestionConnection","Initialise la connection : "+toString());
                
            else
//                Logger.getLogger("GestionConnection").log(Level.INFO,"La connection est fermée, réinitialise la connection : "+toString());
                params.getGestionLog().logs_INFO("GestionConnection","La connection est fermée, réinitialise la connection : "+toString());
            try
            {
                connecte(index);
                con = connections.get(index);
            }
            catch(SQLException sqle)
            {
                con = null;
            }
        }
        return con;
    }
    
    /**
     * Crée une connection.
     * @param serveur
     * @param port
     * @param database
     * @param user
     * @param password
     * @return la connection à la base
     */
    public static Connection connecte(String serveur,String port,String database,String user,String password) throws SQLException
    {
        return DriverManager.getConnection(toString(serveur,port,database),
                                           user,
                                           password);
    }
    
    /**
     * Crée une connection à partir d'un tableau de paramètres.
     * @param params tableau qui doit contenir:
     * <ul>
     * <li>Serveur</li>
     * <li>Port</li>
     * <li>Database</li>
     * <li>user</li>
     * <li>password</li>
     * </ul>
     * @throws JDONREFException le nombre de paramètres est incorrect.
     * @return la connection à la base
     */
    public static Connection connecte(String[] params) throws JDONREFException, SQLException
    {
        if (params.length!=5) throw(new JDONREFException("Le nombre de paramètres est incorrect"));
        return connecte(params[0],params[1],params[2],params[3],params[4]);
    }
    
    /**
     * Crée une des connections de ce gestionnaire.
     * Les paramètres sont chargés sur cette connection.
     * @param index l'index de la connection.
     */
    private void connecte(int index) throws SQLException
    {
        Connection connection = connections.get(index);

        if (connection!=null && isConnectionOk(connection))
        {
            connection.close();
        }

        connection = null;
        
        try
        {
            connection = connecte(cs.server,cs.port,cs.database,cs.user,cs.password);
        }
        catch(SQLException sqle)
        {
//            Logger.getLogger("Connection").log(Level.SEVERE,"La connection "+toString()+" n'a pas pu etre établie.");
            params.getGestionLog().logs_SEVERE("Connection","La connection "+toString()+" n'a pas pu etre établie.");
        }
        try
        {
            if (connection!=null)
                loadParameters(connection,params);
        }
        catch(SQLException sqle)
        {
//            Logger.getLogger("Connection").log(Level.SEVERE,"Les paramètres n'ont pas pu être chargées sur cette connexion.",sqle);
            params.getGestionLog().logs_SEVERE("Connection","Les paramètres n'ont pas pu être chargées sur cette connexion.",sqle);
        }
        connections.set(index,connection);
    }    
    
    /**
     * Initialise toutes les connections sur cette base.<br>
     * Les paramètres sont chargés sur chaque connection.
     */
    public void connecte() throws SQLException
    {
        for(int i=0;i<connections.size();i++)
        {
            connecte(i);
        }
    }
    
    /**
     * Obtient la connection d'index spécifié
     * @param i
     * @return la connection demandée
     */
    public Connection obtientConnection(int i)
    {
        return connections.get(i);
    }
    
    /**
     * Obtient une connection aléatoire au référentiel.<br>
     * Une série aléatoire est choisie. Les connections sont choisies une à une dans cette série.
     * Si une connection ne fonctionne pas pour cette série, une autre série est choisie.<br>
     * Cette méthode est thread-safe.
     * @throws SQLException erreur SQL lors de la deuxième tentative de connection.
     * @return null si aucune connection n'a été trouvée.
     */
    public synchronized RefConnection obtientConnection() throws SQLException, JDONREFException
    {
        indexSuivant();
        
        Connection con = testeConnection(index);
        
        return new RefConnection(index,con);
    }
    
    /**
     * Réserve la connection spécifiée.<br>
     * thread-safe.
     * @param index
     */
    public synchronized void reserve(int index)
    {
        this.users.set(index,this.users.get(index)+1);
    }
    
    /**
     * Relache une connection spécifiée.<br>
     * thread-safe.
     * @param index
     */
    public synchronized void relache(int index)
    {
        this.users.set(index,this.users.get(index)-1);
    }
    
    /**
     * Obtient le nombre d'utilisateur dans les connections de la base.
     * @return le nombre d'utilisateurs.
     */
    public int obtientNombreUtilisateur()
    {
        int total = 0;
        for(int i=0;i<users.size();i++)
            total += this.users.get(i).intValue();
        return total;
    }
    
    /**
     * Obtient la charge actuelle de la base.<br>
     * Cette charge est un pourcentage du taux d'occupation des connections.<br>
     * Elle est calculée par la formule (nb_utilisateur_total*100)/nb_connections_total<br>
     * La charge totale peut donc être supérieure à 100%.
     * @return la charge de la base ou -1 si aucune connection n'est disponible.
     */
    public int obtientCharge()
    {
        if (cs.connections>0)
            return (obtientNombreUtilisateur()*100)/cs.connections;
        else
            return -1;
    }
    
    /**
     * Obtient l'index suivant de la série spécifiée.<br>
     * Cette méthode est thread-safe.
     * @param serie
     * @return l'index suivant
     */
    public synchronized int indexSuivant()
    {
        if (++index==cs.connections)
        {
            index = 0;
        }
        
        return index;
    }    
    
    /**
     * Charge les paramètres sur une connection.
     * @param c
     */
    public static void loadParameters(Connection c, JDONREFParams params) throws SQLException
    {
        PreparedStatement psChargeNotes = c.prepareStatement("SELECT definitnotes(?,?,?,?,?,?,?);");
        psChargeNotes.setInt(1,params.obtientNotePourCodePostal());
        psChargeNotes.setInt(2,params.obtientNotePourCommune());
        psChargeNotes.setInt(3,params.obtientNotePourMotDeterminant());
        psChargeNotes.setInt(4,params.obtientNotePourLibelle());
        psChargeNotes.setInt(5,params.obtientNotePourTypeDeVoie());
        psChargeNotes.setInt(6,params.obtientNotePourNumero());
        psChargeNotes.setInt(7,params.obtientNotePourArrondissement());
        psChargeNotes.execute();
        psChargeNotes.close();
        
        PreparedStatement psChargePourcentages = c.prepareStatement("SELECT definitpourcentages(?,?,?,?,?);");
        psChargePourcentages.setInt(1,params.obtientPourcentageDeCorrespondanceDeCodePostal());
        psChargePourcentages.setInt(2,params.obtientPourcentageDeCorrespondanceDeCommune());
        psChargePourcentages.setInt(3,params.obtientPourcentageDeCorrespondanceDeMotDeterminant());
        psChargePourcentages.setInt(4,params.obtientPourcentageDeCorrespondanceDeLibelle());
        psChargePourcentages.setInt(5,params.obtientPourcentageDeCorrespondanceDeTypeDeVoie());
        psChargePourcentages.execute();
        psChargePourcentages.close();
        
        PreparedStatement psChargeMalus = c.prepareStatement("SELECT definitmalus(?,?);");
        psChargeMalus.setInt(1,params.obtientMalusPasDeMot());
        psChargeMalus.setInt(2,params.obtientMalusPasDeMotDirecteur());
        psChargeMalus.execute();
        psChargeMalus.close();
        
        PreparedStatement psChargeDivers = c.prepareStatement("SELECT definitdivers(?);");
        psChargeDivers.setInt(1,params.obtientTailleMinimaleDAbbreviation());
        psChargeDivers.execute();
        psChargeDivers.close();
        
        PreparedStatement psChargeNotesPoizon = c.prepareStatement("SELECT definitnotespoizon(?,?,?,?,?,?,?,?);");
        psChargeNotesPoizon.setInt(1,params.obtientNotePourCle());
        psChargeNotesPoizon.setInt(2,params.obtientNotePourPoizon());
        psChargeNotesPoizon.setInt(3,params.obtientNotePourLigne2());
        psChargeNotesPoizon.setInt(4,params.obtientNotePourLigne3());
        psChargeNotesPoizon.setInt(5,params.obtientNotePourLigne4());
        psChargeNotesPoizon.setInt(6,params.obtientNotePourLigne5());
        psChargeNotesPoizon.setInt(7,params.obtientNotePourLigne6());
        psChargeNotesPoizon.setInt(8,params.obtientNotePourLigne7());
        psChargeNotesPoizon.execute();
        psChargeNotesPoizon.close();
        
        PreparedStatement psChargePourcentagesPoizon = c.prepareStatement("SELECT definitpourcentagespoizon(?,?,?,?,?,?,?,?);");
        psChargePourcentagesPoizon.setInt(1,params.obtientPourcentagePourCle());
        psChargePourcentagesPoizon.setInt(2,params.obtientPourcentagePourPoizon());
        psChargePourcentagesPoizon.setInt(3,params.obtientPourcentagePourLigne2());
        psChargePourcentagesPoizon.setInt(4,params.obtientPourcentagePourLigne3());
        psChargePourcentagesPoizon.setInt(5,params.obtientPourcentagePourLigne4());
        psChargePourcentagesPoizon.setInt(6,params.obtientPourcentagePourLigne5());
        psChargePourcentagesPoizon.setInt(7,params.obtientPourcentagePourLigne6());
        psChargePourcentagesPoizon.setInt(8,params.obtientPourcentagePourLigne7());
        psChargePourcentagesPoizon.execute();
        psChargePourcentagesPoizon.close();
        
        PreparedStatement psChargeDiversPoizon = c.prepareStatement("SELECT definitdiverspoizon(?);");
        psChargeDiversPoizon.setInt(1,params.obtientTailleAbbreviationMminimalePoizon());
        psChargeDiversPoizon.execute();
        psChargeDiversPoizon.close();
    }
    

    /**
     * Ferme chaque connection établie.
     */
    public void resetConnections() throws SQLException
    {
        for(int i=0;i<connections.size();i++)
        {
            connections.get(i).close();
        }
    }
    
    /**
     * Obtient la chaine de connection d'une série.
     * @param index
     * @return la chaine de connection
     */
    @Override
    public String toString()
    {
        return toString(cs.server,cs.port,cs.database);
    }
    
    /**
     * Obtient la chaine de connection d'une connection.
     * @return
     */
    public static String toString(String server,String port,String database)
    {
        return "jdbc:postgresql://"+server+":"+port+"/"+database;
    }
}
