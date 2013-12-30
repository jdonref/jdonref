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

package ppol.jdonref.referentiel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import ppol.jdonref.Tables.DescriptionTable;
import ppol.jdonref.Tables.GestionTables;

/**
 * Permet de gérer les tables troncons suite aux changements de référentiel.
 * @author Julien Moquet
 */
public class GestionHistoriqueTables
{      
    private final static String nomTableTroncons = "ttr_tables_troncons";

    private final static String prefixeTableTroncon = "tro_troncons";
    
    /**
     * Crée la table de gestion des troncons.
     * @param connection la connection au référentiel dans lequel Créer la table de gestion.
     */
    public static void creeTableGestionTroncons(Connection connection) throws SQLException, GestionReferentielException
    {
        if (!tableTronconExiste(connection))
        {
            GestionTables.creeTable(nomTableTroncons,GestionDescriptionTables.creeDescriptionTableGestionTables(),connection);
        }
    }
    
    /**
     * Retourne vrai si la table qui gère les tables de troncon existe.<br>
     * Utilise GestionTables.tableExiste.
     */
    public static boolean tableTronconExiste(Connection connection) throws SQLException
    {
        return GestionTables.tableExiste(nomTableTroncons,connection);
    }
    
    /**
     * Obtient le nom de la table utilisée à la date donnée tel qu'elle est indiquée dans la table spécifiée.<br>
     * @param connection
     * @param nomTableHistorisee La table qui contient la référence à la table cherchée.
     * @return Le nom de la table recherchée, null si il n'y en jamais eu.
     */
    private static String obtientTable(Connection connection,String nomTableHistorisee,String code_departement,Date date) throws SQLException
    {
        String request = "SELECT ttr_nom from \""+nomTableHistorisee.toLowerCase()+"\" where dpt_code_departement=? and t0<=? and t1>=?";
        
        PreparedStatement ps = connection.prepareStatement(request); // SQLException
        
        Timestamp ts = new Timestamp(date.getTime());
        
        ps.setString(1,code_departement);
        ps.setTimestamp(2,ts);
        ps.setTimestamp(3,ts);
        
        ResultSet rs = ps.executeQuery();
        
        if (rs.next())
        {
            return rs.getString(1);
        }
        else
            return null;
    }
    
    /**
     * Obtient la table Troncon utilisée à la date spécifiée
     */
    public static String obtientTableTroncon(Connection connection,String code_departement,Date date) throws SQLException
    {
        return obtientTable(connection,nomTableTroncons,code_departement,date);
    }
    
    /**
     * Obtient le nom de la table actuellement utilisée tel qu'elle est indiquée dans la table spécifiée.<br>
     * La date actuelle plus 10 jours est utilisée pour identifier cette table.
     * @param connection
     * @param nomTableHistorisee La table qui contient la référence à la table cherchée.
     * @return Le nom de la table recherchée, null si il n'y en jamais eu.
     */
    private static String obtientDerniereTable(Connection connection,String nomTableHistorisee,String code_departement) throws SQLException
    {
        Calendar c = Calendar.getInstance();
        
        c.add(Calendar.DAY_OF_WEEK, 10);
        
        String request = "SELECT ttr_nom from \""+nomTableHistorisee.toLowerCase()+"\" where dpt_code_departement=? and t1>?";
        
        PreparedStatement ps = connection.prepareStatement(request); // SQLException
        
        Timestamp ts = new Timestamp(c.getTimeInMillis());
        
        ps.setString(1,code_departement);
        ps.setTimestamp(2,ts);
        
        ResultSet rs = ps.executeQuery();
        
        if (rs.next())
        {
            return rs.getString(1);
        }
        else
            return null;
    }
    
    /**
     * Obtient la table Troncon actuellement utilisée.
     */
    public static String obtientDerniereTableTroncon(Connection connection,String code_departement) throws SQLException
    {
        return obtientDerniereTable(connection,nomTableTroncons,code_departement);
    }

    /**
     * Obtient le nombre de tables historisée du type spécifié pour le département spécifié.
     * @param connection
     * @param code_departement
     * @param nomTableHistorisee
     * @return
     */
    public static int obtientQuantiteTable(Connection connection,String code_departement,String nomTableHistorisee) throws SQLException
    {
        String request = "Select count(*) from \""+nomTableHistorisee.toLowerCase()+"\" where dpt_code_departement=?;";
        
        PreparedStatement ps = connection.prepareStatement(request);
        
        ps.setString(1,code_departement);
        
        ResultSet rs = ps.executeQuery();
        
        if (rs.next())
        {
            return rs.getInt(1);
        }
        return 0;
    }
    
    /**
     * Crée un nom unique pour une nouvelle table de prefixe et de département donné.<br>
     * Le nom fourni à pour modèle prefixe-code_departement-identifiant.<br>
     * Ce nom est valable jusqu'à ce qu'une table soit ajoutée...
     * @param connection
     * @param code_departement
     * @param prefixe
     * @return
     */
    private static String obtientNouveauNom(Connection connection,String nomTableHistorisee,String code_departement,String prefixe) throws SQLException
    {
        String nom;
        
        int quantite = obtientQuantiteTable(connection,code_departement,nomTableHistorisee);
        
        nom = prefixe+"_"+code_departement+"_"+quantite;
        
        return nom;
    }
    
   /* Crée un nom unique pour une nouvelle table de troncons de département donné.<br>
     * Le nom fourni à pour modèle tro_troncons_code_departement_identifiant.<br>
     * Ce nom est valable jusqu'à ce qu'une table soit ajoutée...
     */
    public static String obtientNouveauNomPourTableTroncon(Connection connection,String code_departement) throws SQLException
    {
        return obtientNouveauNom(connection,nomTableTroncons,code_departement,prefixeTableTroncon);
    }

    /**
     * Enregistre une nouvelle table.<br>
     * Une fois cette table enregistrée, une nouvelle table ne devrait pas être enregistrée avant la date spécifiée.
     * @param connection La connection à la base qui contient la table nomTablehistorisée.
     * @param nomTable Le nom de la table à enregistrer.
     * @param code_departement Le code du département de la table.
     * @param nomTableHistorisee Le nom de la table qui historise les tables, dans laquelle la table sera enregistrée.
     * @param date la date à laquelle la nouvelle table sera enregistrée.
     */
    private static void enregistreNouvelleTable(Connection connection,String nomTable,String code_departement,String nomTableHistorisee,Date date) throws SQLException
    {
        Calendar c = Calendar.getInstance();
        
        c.add(Calendar.DAY_OF_WEEK, 10);
        
        // Cherche les dates de validité de la table actuellement utilisée.
        String request = "SELECT t0,t1 from \""+nomTableHistorisee.toLowerCase()+"\" where dpt_code_departement=? and t0<=?";
        
        PreparedStatement ps = connection.prepareStatement(request); // SQLException
        
        Timestamp ts = new Timestamp(c.getTimeInMillis());
        
        ps.setString(1,code_departement);
        ps.setTimestamp(2,ts);
        
        ResultSet rs = ps.executeQuery();
        
        // Si une table est utilisée actuellement, ses dates de validité doivent être modifi�es.
        if (rs.next())
        {
            String updateRequest = "UPDATE \""+nomTableHistorisee.toLowerCase()+"\" set t1=?";
            
            PreparedStatement psUpdate = connection.prepareStatement(updateRequest); // SQLException
            
            // Sa date de validité s'arrête un jour avant le début de validité de la nouvelle table.
            c.setTime(date);
            c.add(Calendar.DAY_OF_WEEK, -1);
            
            psUpdate.setTimestamp(1,new Timestamp(c.getTimeInMillis()));
            
            psUpdate.execute();
        }

        // Et enfin, ajoute la nouvelle table.
        String insertRequest = "INSERT into \""+nomTableHistorisee.toLowerCase()+"\" (dpt_code_departement,ttr_nom,t0,t1) VALUES (?,?,?,?);";
        
        PreparedStatement psInsert = connection.prepareStatement(insertRequest);
               
        psInsert.setString(1,code_departement);
        psInsert.setString(2,nomTable);
        c.setTime(date);
        psInsert.setTimestamp(3,new Timestamp(c.getTimeInMillis()));
        c.add(Calendar.YEAR, 10);
        psInsert.setTimestamp(4,new Timestamp(c.getTimeInMillis()));
        
        psInsert.execute();
    }

    /**
     * Obtient la date de début de validité d'une table.
     * @throws GestionReferentielException De multiples tables sont référencées avec ce nom ou la table n'est pas référencée.
     */
    public static Date obtientDebutValidite(Connection connection,String nomTable) throws SQLException, GestionReferentielException
    {
        String request = "SELECT t0 from \""+GestionHistoriqueTables.nomTableTroncons.toLowerCase()+"\" where ttr_nom=?";
        
        PreparedStatement psDates = connection.prepareStatement(request);
        
        psDates.setString(1,nomTable);
        ResultSet rsDates = psDates.executeQuery();        
        
        Date res = null;
        
        if (rsDates.next())
        {
            Timestamp ts = rsDates.getTimestamp(1);
            
            res = new Date(ts.getTime());
            
            if (rsDates.next())
            {
                throw(new GestionReferentielException("Deux tables "+nomTable+" sont référencées dans la table "+GestionHistoriqueTables.nomTableTroncons,GestionReferentielException.MULTIPLESTABLES,10));
            }
            
            return res;
        }
        throw(new GestionReferentielException("La table de troncon "+nomTable+" n'est pas référencée dans la table "+GestionHistoriqueTables.nomTableTroncons,GestionReferentielException.TABLENEXISTEPAS,10));
    }
    
    /**
     * Enregistre la nouvelle table troncon donnée
     * Une fois cette table enregistrée, une nouvelle table ne devrait pas être enregistrée avant la date spécifiée.
     * @param connection La connection à la base qui contient la table nomTablehistorisée.
     * @param nomTable Le nom de la table à enregistrer.
     * @param code_departement Le code du département de la table.
     * @param date la date à laquelle la nouvelle table sera enregistrée.
     */
    public static void enregistreNouvelleTableTroncons(Connection connection,String nomTable,String code_departement,Date date) throws SQLException
    {
       enregistreNouvelleTable(connection, nomTable, code_departement, GestionHistoriqueTables.nomTableTroncons, date);
    }
    
    /**
     * Tests
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("Tests de GestionHistoriqueTables");
        
        System.out.println("\r\nCrée une table tablesVoies");
        
        DescriptionTable dt = GestionDescriptionTables.creeDescriptionTableGestionTables();
        
        //GestionTables.creeTable(nomTableVoies, dt, connection);
    }
}