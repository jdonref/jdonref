/*
 * GestionTables.java
 *
 * Created on 12 mars 2008, 08:58
 *
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

package ppol.jdonref.Tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import ppol.jdonref.referentiel.GestionReferentielException;

/**
 * Faille de sécurité dans AjouteColonne.
 * @author jmoquet
 */
public class GestionTables
{
    /** Creates a new instance of GestionTables */
    public GestionTables()
    {
    }
    
    /**
     * Nettoye une table en effectuant un VACUUM ANALYSE.
     * @param nomTable
     * @param connection
     */
    public static void nettoye(String nomTable,Connection connection) throws SQLException
    {
        Statement nettoyeTroncons = connection.createStatement();
        nettoyeTroncons.execute("VACUUM ANALYSE "+nomTable);
        nettoyeTroncons.close();
    }
    
    /**
     * Ajoute un index à la table spécifiée.<br>
     * L'index n'est pas ajouté si la table possède déjà un index pour les mêmes colonnes.
     * @param name
     * @param index
     * @param connection
     * @return true si l'index a été crée.
     * @throws GestionReferentielException Un index du même nom existe déjà.
     */
    public static boolean ajouteIndex(String name,Index index,Connection connection) throws SQLException, GestionReferentielException
    {
        String nomindex = indexExiste(name,index,connection);
        if (nomindex!=null) return false;

        if (indexExiste(name,index.getNom(),connection))
            throw(new GestionReferentielException("Un index du nom "+index.getNom()+" existe déjà pour cette table.",GestionReferentielException.INDEXEXISTE,11));
        
        Statement sCreate = connection.createStatement();
        sCreate.execute(index.toString(name));
        
        return true;
    }
    
    /**
     * Supprime l'index spécifié.
     */
    public static void supprimeIndex(Index index,Connection connection) throws SQLException
    {
        Statement sDrop = connection.createStatement();
        sDrop.execute(index.dropString());
    }
    
    /**
     * Retourne si une index du nom spécifié existe.
     * @return
     */
    public static boolean indexExiste(String tablename,String indexname,Connection connection) throws SQLException
    {
        String schema = null;
        String nomTable = null;
        
        tablename=tablename.replaceAll("\"","");
        int idx = tablename.indexOf(".");
        
        // Extrait le shéma et le nom de la table.
        if (idx!=-1)
        {
            schema = tablename.substring(0,idx);
            nomTable = tablename.substring(idx+1);
        }
        else
            nomTable = tablename;
        
        // Prépare la requête
        StringBuffer sb = new StringBuffer();
        sb.append("Select indexname FROM pg_indexes WHERE indexname=? AND tablename=?");
        if (idx!=-1)
        {
            sb.append(" AND schemaname=?");
        }
        sb.append(" LIMIT 1");
        
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ps.setString(1,indexname);
        ps.setString(2,nomTable);
        if (idx!=-1)
            ps.setString(3,schema);
        
        ResultSet rs = ps.executeQuery();
        
        if (rs.next())
        {
            rs.close();
            ps.close();
            return true;
        }
        
        rs.close();
        ps.close();
        return false;
    }
    
    /**
     * Retourne si un index pour les colonnes spécifiées existe pour la table spécifiée.<br>
     * Le nom de l'index n'est ainsi pas utilisé pour effectuer la comparaison.<br>
     * testé avec PostgreSQL 8.0. utilise la table pg_indexes.<br>
     * Attention, si le shéma n'est pas spécifié, la recherche est effectuée dans tous les schémas.<br>
     * @param name peut être de la forme nomschema.nomtable
     * @param index
     * @param connection
     * @return null si aucun index ne correspond, le nom de l'index sinon.
     */
    public static String indexExiste(String tablename,Index index,Connection connection) throws SQLException
    {
        String schema = null;
        String nomTable = null;
        
        tablename = tablename.replaceAll("\"","");
        int idx = tablename.indexOf(".");
        
        // Extrait le shéma et le nom de la table.
        if (idx!=-1)
        {
            schema = tablename.substring(0,idx);
            nomTable = tablename.substring(idx+1);
        }
        else
            nomTable = tablename;
        
        // Prépare la requête
        StringBuffer sb = new StringBuffer();
        sb.append("Select indexdef,indexname FROM pg_indexes WHERE tablename=?");
        if (idx!=-1)
        {
            sb.append(" AND schemaname=?");
        }
        
        PreparedStatement ps = connection.prepareStatement(sb.toString());
        ps.setString(1,nomTable);
        if (idx!=-1)
            ps.setString(2,schema);
        
        ResultSet rs = ps.executeQuery();
        
        while(rs.next())
        {
            String indexdef = rs.getString(1);
            
            idx = indexdef.indexOf("(");
            indexdef = indexdef.substring(idx+1);
            idx = indexdef.indexOf(")");
            indexdef = indexdef.substring(0,idx);
            
            String[] data = indexdef.split(",");
            
            if (index.compareTo(data))
            {
                String indexname = rs.getString(2);
                rs.close();
                ps.close();
                return indexname;
            }
        }
        
        rs.close();
        ps.close();
        return null;
    }
    
    /**
     * Retourne si la table spécifiée existe.<br>
     * testé avec PostgreSQL 8.1. utilise la table pg_tables.<br>
     * Attention, si le shéma n'est pas spécifié, la recherche est effectuée dans tous les schémas.<br>
     * name peut être de la forme nomschema.nomtable
     * @throws SQLException exception durant la requête de recherche de la table.
     */
    public static boolean tableExiste(String name,Connection connection) throws SQLException
    {
        String schema = null;
        String nomTable = null;
        
        name=name.replaceAll("\"","");
        int index = name.indexOf(".");
        
        // Extrait le shéma et le nom de la table.
        if (index!=-1)
        {
            schema = name.substring(0,index);
            nomTable = name.substring(index+1);
        }
        else
            nomTable = name;
        
        // Prépare la requête
        StringBuffer sb = new StringBuffer();
        sb.append("Select count(*) from pg_tables where tablename=?");
        if (index!=-1)
        {
            sb.append(" and schemaname=?");
        }
        
        boolean res = false;
        PreparedStatement st = connection.prepareStatement(sb.toString());
        
        st.setString(1,nomTable.toLowerCase()); // Les noms de table et de schéma sont en minuscule dans la table pg_tables.
        if (index!=-1)
            st.setString(2,schema.toLowerCase());
        
        ResultSet rs = st.executeQuery();
        
        rs.next();
        
        if (rs.getInt(1)>=1)
            res= true;
        else
            res = false;
        
        rs.close();
        st.close();
        
        return res;
    }
    
    /**
     * Obtiens les shémas qui contiennent une table du nom spécifié.<br>
     * Utilise la table pg_tables.
     * @param name le nom de la table à rechercher (sans son shéma).
     * @throws probl�me durant la requête sur pgtables
     */
    public static String[] obtientShemas(String name,Connection connection) throws SQLException
    {
        PreparedStatement st = connection.prepareStatement("Select schemaname from pg_tables where tablename=?");
        
        st.setString(1,name.toLowerCase()); // Les noms de table sont en minuscule dans pg_tables.
        
        ResultSet rs = st.executeQuery();
        
        ArrayList shemas = new ArrayList();
        
        while(rs.next())
        {
            shemas.add(rs.getString(1));
        }
        
        String[] res = new String[shemas.size()];
        
        for(int i=0;i<shemas.size();i++)
            res[i] = (String)shemas.get(i);
        
        return res;
    }
    
    /**
     * Obtiens la description d'une table.
     * testé avec PostgreSQL 8.1. Utilise la table INFORMATION_SCHEMA.COLUMNS. <br>
     * Si la description ne contient aucune colonne, la table n'existe peut être pas.<br>
     * Les colonnes de nom geometrie et de type USER DEFINED sont consid�r� comme des colonnes géométrie.
     * @param name le nom de la table de la forme nomshema.nomtable
     * @throws SQLException un probl�me est survenu durant la requête permettant d'obtenir la liste des colonnes de la table.
     * @throws GestionReferentielException De multiples schémas ont �t� trouv� pour la table (le nom de schéma n'�tait pas spécifié).
     */
    public static DescriptionTable obtientDescription(String name,Connection connection) throws SQLException, GestionReferentielException
    {
        String shema = null;
        String nomTable = null;
        
        // Extrait le shéma et le nom de la table.
        name = name.replaceAll("\"","");
        int index = name.indexOf(".");
        if (index!=-1)
        {
            shema = name.substring(0,index);
            nomTable = name.substring(index+1);
        }
        else
        {
            String[] schemas = obtientShemas(name,connection);
            if (schemas.length>1)
            {
                StringBuilder sb = new StringBuilder();
                
                sb.append("De multiples shemas contiennent une table de nom ");
                sb.append(name);
                sb.append(" (");
                for(int i=0;i<schemas.length;i++)
                {
                    if (i>0)
                        sb.append(",");
                    sb.append(schemas[i]);
                }
                sb.append(")");
                
                throw(new GestionReferentielException(sb.toString(),GestionReferentielException.MULTIPLESSHEMAS,10));
            }
            else if (schemas.length==0)
            {
                return new DescriptionTable();
            }
            else
                shema = schemas[0];
            
            nomTable = name;
        }

        // Prépare la requête.
        StringBuilder sb = new StringBuilder();
        sb.append("Select column_name,data_type,character_maximum_length FROM INFORMATION_SCHEMA.COLUMNS where table_name=?");
        if (shema!=null)
            sb.append(" and table_schema=?");
        
        PreparedStatement st = null;
        String requete = sb.toString();
        st = connection.prepareStatement(requete);
        
        st.setString(1,nomTable.toLowerCase()); // Les noms et shémas sont inscrits en minuscules dans la table INFORMATION_SCHEMA.COLUMNS.
        if (shema!=null)
            st.setString(2,shema.toLowerCase()); 
        
        // Ex�cute la requête.
        ResultSet rs = st.executeQuery();
        
        ArrayList<Colonne> colonnes = new ArrayList<Colonne>();
        
        while(rs.next())
        {
            try
            {
                String nom = rs.getString(1).toLowerCase();
                String type = rs.getString(2).toLowerCase();
                
                if (nom.compareTo("geometrie")==0 && type.compareTo("user-defined")==0)
                {
                    colonnes.add(Colonne.creeColonneGeometrie());
                }
                else
                {
                    Colonne c = new Colonne(nom,type,rs.getInt(3));
                    colonnes.add(c);
                }
            }
            catch(ColonneException ce)
            {
                // Cela ne devrait pas arriver.
            }
        }
        
        rs.close();
        st.close();
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Ajoute une colonne à une table.<br>
     * Faille de sécurité dans la requête (injection sql).<br>
     * Si la taille du champ est 0, la longueur n'est pas spécifiée dans la requête.<br>
     * S'il s'agit d'une colonne géométrie, la m�thode ajouteColonneGeometrie est appel�e.
     * @throws SQLException Une erreur est survenue durant la requête.
     * @throws GestionReferentielException la table n'existe pas.
     */
    public static void ajouteColonne(String nom,Colonne colonne,Connection connection) throws SQLException, GestionReferentielException
    {
        if (tableExiste(nom,connection))
        {
            if (colonne.estGeometrie())
                ajouteColonneGeometrie(nom,colonne.getNom(),connection);
            else
            {
                StringBuffer sb = new StringBuffer();
                
                nom = nom.replaceAll("\"","");
                nom = GestionTables.formateNom(nom);
                
                sb.append("ALTER TABLE ");
                sb.append(nom.toLowerCase());
                sb.append(" ADD COLUMN \"");
                sb.append(colonne.getNom());
                sb.append("\" ");
                sb.append(colonne.getType());
                if (colonne.getLength()!=0)
                {
                    sb.append("(");
                    sb.append(colonne.getLength());
                    sb.append(")");
                }
                
                PreparedStatement st = connection.prepareStatement(sb.toString());
                
                st.execute();
                st.close();
            }
        }
        else
            throw(new GestionReferentielException("La table "+nom+" n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
    }
    
    /**
     * Supprime une table.<br>
     * Supporte les noms composés d'un shéma et d'un nom de table.
     * @throws GestionReferentielException la table n'existe pas.
     * @throws SQLException erreur durant la requête.
     */
    public static void supprimeTable(String nom,Connection connection) throws SQLException, GestionReferentielException
    {
        if (tableExiste(nom,connection))
        {
            StringBuffer sb = new StringBuffer();
            
            nom = formateNom(nom);
            
            sb.append("DROP TABLE ");
            sb.append(nom.toLowerCase());
            
            PreparedStatement st = connection.prepareStatement(sb.toString());
            
            st.execute();
            st.close();
        }
        else
            throw(new GestionReferentielException("La table "+nom+" n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
    }
    
    /**
     * Trouve le premier schéma qui contient la table nomTable.<br>
     * testé avec PostgreSQL 8.1 (utilise pg_tables).<br>
     * Le schéma ne doit pas être spécifié dans le nom de table.<br>
     * @throws SQLException un probl�me est survenu durant la requête.
     */
    private static String trouvePremierShema(String nomTable,Connection connection) throws SQLException
    {
        // Prépare la requête
        String request = "Select schemaname from pg_tables where tablename=?";
        
        boolean res = false;
        PreparedStatement st = connection.prepareStatement(request);
        
        st.setString(1,nomTable.toLowerCase()); // les noms de table sont en minuscule dans pg_tables
        
        ResultSet rs = st.executeQuery();
        
        if (rs.next())
        {
            return rs.getString(1);
        }
        
        return null;
    }
    
    /**
     * Ajoute une colonne géométrie à la table.<br>
     * Si le shéma n'est pas spécifié, le premier shéma où la table est trouv�e est utilisé (table pg_tables).<br>
     * Utilise Select AddGeometryColumn(schema,table,colonne,-1,'GEOMETRY',2);
     * @throws GestionReferentielException La table n'existe pas.
     * @throws SQLException Problème lors de l'ajout de la colonne.
     * @throws SQLException Problème lors de la r�cup�ration du premier schéma de la table.
     * @throws GestionReferentielException Problème lors de la v�rification de l'existence de la table.
     */
    public static void ajouteColonneGeometrie(String nomTable,String nomColonne,Connection connection) throws SQLException, GestionReferentielException
    {
        if (tableExiste(nomTable,connection))
        {
            String schema = null;
            
            nomTable = nomTable.replaceAll("\"",""); // au cas ou GestionTables.formateNom aurait �t� utilisé.
            int index = nomTable.indexOf(".");
            
            // Extrait le shéma et le nom de la table.
            if (index!=-1)
            {
                schema = nomTable.substring(0,index);
                nomTable = nomTable.substring(index+1);
            }
            else
                schema = trouvePremierShema(nomTable,connection);
            
            // Prépare la requête.
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT AddGeometryColumn('");
            sb.append(schema.toLowerCase()); // les noms de shémas et de table sont en minuscule.
            sb.append("','");
            sb.append(nomTable.toLowerCase());
            sb.append("','");
            sb.append(nomColonne.toLowerCase());
            sb.append("',-1,'GEOMETRY',2);");
            
            Statement st = connection.createStatement();
            
            // Ex�cute la requête.
            st.execute(sb.toString());
        }
        else
        {
            throw(new GestionReferentielException("La table "+nomTable+" n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
        }
    }
    
    /**
     * Crée une table.<br>
     * Pour les champs dont la longueur est zéro, la taille n'est pas spécifiée dans la requête.<br>
     * @throws java.sql.SQLException Problème durant la requête.
     */
    public static boolean creeTable(String nom,DescriptionTable dt,Connection connection) throws SQLException
    {
        ArrayList<Colonne> colonnes = new ArrayList<Colonne>();
        
        for(int i=0;i<dt.obtientQuantitéColonnes();i++)
            colonnes.add(dt.getColonne(i));
        
        return creeTable(nom,colonnes,connection);
    }
    
    /**
     * Crée une table.<br>
     * Pour les champs dont la longueur est zéro, la taille n'est pas spécifiée dans la requête.<br>
     * Si la table existe déjà, il est préférable d'utiliser GestionReferentiel.majStructure.<br>
     * @throws SQLException Problème durant la requête.
     * @return false si la table existait déjà.
     */
    public static boolean creeTable(String nom,ArrayList<Colonne> colonnes,Connection connection) throws SQLException
    {
        if (!tableExiste(nom, connection))
        {
            StringBuffer sb = new StringBuffer();
            boolean geometrie = false;
            Colonne cgeometrie = null;

            nom = GestionTables.formateNom(nom);
            
            sb.append("CREATE TABLE ");
            sb.append(nom.toLowerCase());
            sb.append(" (");
            for (int i = 0; i < colonnes.size(); i++)
            {
                Colonne c = colonnes.get(i);
                if (c.estGeometrie())
                {
                    geometrie = true;
                    cgeometrie = c;
                }
                else
                {
                    if (i > 0)
                    {
                        sb.append(",");
                    }
                    sb.append("\"");
                    sb.append(c.getNom());
                    sb.append("\"");
                    sb.append(" ");
                    sb.append(c.getType());
                    if (c.getLength() != 0)
                    {
                        sb.append("(");
                        sb.append(c.getLength());
                        sb.append(")");
                    }
                }
            }
            sb.append(")");

            PreparedStatement st = connection.prepareStatement(sb.toString());

            st.execute();
            st.close();
            
            // Ajoute la géométrie si nécessaire.
            if (geometrie)
            {
                try
                {
                    ajouteColonneGeometrie(nom,cgeometrie.getNom(),connection);
                }
                catch(GestionReferentielException gre)
                {
                    // N'arrive qu'au cas où la table n'existe pas, mais elle vient d'être crée.
                }
            }
            
            return true;
        }
        return false;
    }
    
    /**
     * Formate le nom d'une table pour l'ajouter dans une requête.<br>
     * Prendre en compte le . séparant le schéma de la table, pour ajouter des guillemets.<br>
     * @param nom
     * @return nom de table entouré de guillemets.
     */
    public static String formateNom(String nom)
    {
        if (nom.startsWith("\"") && nom.endsWith("\""))
            return nom;
        if (nom.indexOf(".")!=-1)
        {
            int index = nom.indexOf(".");
            nom = "\""+nom.substring(0,index)+"\".\""+nom.substring(index+1)+"\"";
        }
        else
        {
            nom = "\""+nom+"\"";
        }
        return nom;
    }
    
    /**
     * Vide la table spécifiée.
     * @throws SQLException erreur durant la requête.
     */
    public static void vide(String nom,Connection connection) throws SQLException
    {
        nom = formateNom(nom);
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("DELETE FROM ");
        sb.append(nom);
        sb.append(";");
        
        Statement st = connection.createStatement();
        st.execute(sb.toString());
        st.close();
    }
    
    /**
     * Supprime une colonne d'une table.
     * Seul le nom de la colonne est utilisé pour supprimer la colonne (le type n'est pas vérifié).
     * @throws SQLException Problème durant la requête.
     */
    public static void supprimeColonne(String nom,Colonne colonne,Connection connection) throws SQLException
    {
        StringBuffer sb = new StringBuffer();
        
        nom = GestionTables.formateNom(nom);
        
        sb.append("ALTER TABLE ");
        sb.append(nom.toLowerCase());
        sb.append(" DROP COLUMN \"");
        sb.append(colonne.getNom());
        sb.append("\"");
        
        PreparedStatement st = connection.prepareStatement(sb.toString());
        
        st.execute();
        st.close();
    }
    
    /**
     * Permet de tester la classe GestionTables.
     */
    public static void main(String[] args)
    {
        System.out.println("Tests de la classe GestionTables");
        System.out.println("dans la base jdbc:postgresql:5430//localhost/test avec l'utilisateur test password test.");
        
        try
        {
            
            // Nécessaire pour le chargement de la classe g�rant la connection avec une base postgresql.
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
            return;
        }
                
        Connection db;
        
        try
        {
            
            // Le protocole utilisé est jdbc:postgresql.
            db  = DriverManager.getConnection("jdbc:postgresql://localhost:5430/test","test","test");
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return;
        }
        
        System.out.println("\r\nVérifie l'existence de la table tableX");
        
        try
        {
            if (tableExiste("tableX",db))
                System.out.println("La table tableX existe");
            else
                System.out.println("La table tableX n'existe pas.");
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nTente de créer la table table1 :");
        System.out.println("nom CHARACTER(80)");
        System.out.println("date TIMESTAMP");
        
        ArrayList<Colonne> cs = new ArrayList<Colonne>();
        Colonne c0 = null,c1 = null;
        try
        {
            c0 = new Colonne("nom", "CHARACTER", 80);
            c1 = new Colonne("date","TIMESTAMP",0);
        }
        catch (ColonneException ex)
        {
            ex.printStackTrace();
        }
        
        cs.add(c0);
        cs.add(c1);
        try
        {
            
            GestionTables.creeTable("table1",cs,db);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nVérifie l'existence de la table table1");
        
        try
        {
            if (tableExiste("table1",db))
                System.out.println("La table table1 existe");
            else
                System.out.println("La table table1 n'existe pas.");
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nTente d'ajouter la colonne col3 de type CHARACTER de longueur 38 à la table table1.");
        
        Colonne c = null;
        
        try
        {
            c = new Colonne("col3","CHARACTER",38);
        }
        catch(ColonneException ce)
        {
        }
        try
        {
            GestionTables.ajouteColonne("table1",c,db);
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
         catch (GestionReferentielException ex)
        {
            ex.printStackTrace();
        }
                
        System.out.println("\r\nObtiens la description de la table table1:");
        
        DescriptionTable dt = null;
        
        try
        {
            dt = GestionTables.obtientDescription("table1", db);
            
            for(int i=0;i<dt.getCount();i++)
            {
                System.out.println(dt.getColonne(i).getNom()+" "+dt.getColonne(i).getType()+"("+dt.getColonne(i).getLength()+")");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        catch (GestionReferentielException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nTente de créer un index 'table1_idx' sur col3 et nom dans la table table1.");
        try
        {
            Index i = new Index();
            i.setNom("table1_idx");
            i.ajouteColonne("col3");
            i.ajouteColonne("nom");
            
            GestionTables.ajouteIndex("table1",i,db);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        catch(GestionReferentielException gre)
        {
            gre.printStackTrace();
        }

        System.out.println("\r\nVérifie l'existence d'un index 'table1_idx'");
        try
        {
            boolean existe = GestionTables.indexExiste("table1","table1_idx",db);
            if (existe)
                System.out.println("oui");
            else
                System.out.println("non");
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nCherche l'existence d'un index des colonnes nom et col3");
        try
        {
            Index i = new Index();
            i.ajouteColonne("col3");
            i.ajouteColonne("nom");
            
            String existe = GestionTables.indexExiste("table1",i,db);
            
            if (existe==null)
                System.out.println("non");
            else
                System.out.println("oui");
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nSupprime l'index table1_idx");
        try
        {
            Index i = new Index();
            i.setNom("table1_idx");
            GestionTables.supprimeIndex(i,db);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nTente de supprimer la colonne col3 de la table table1.");
        try
        {
            
            GestionTables.supprimeColonne("table1",c,db);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nObtiens la description de la table table1:");
        
        try
        {
            dt = GestionTables.obtientDescription("table1", db);
            
            for(int i=0;i<dt.getCount();i++)
            {
                System.out.println(dt.getColonne(i).getNom()+" "+dt.getColonne(i).getType()+"("+dt.getColonne(i).getLength()+")");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        catch (GestionReferentielException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nTente de créer une colonne géométrie");

        try
        {
            GestionTables.ajouteColonneGeometrie("table1","geometrie",db);
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        } catch (GestionReferentielException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("\r\nObtiens la description de la table table1:");
        
        try
        {
            dt = GestionTables.obtientDescription("table1", db);
            
            for(int i=0;i<dt.getCount();i++)
            {
                System.out.println(dt.getColonne(i).getNom()+" "+dt.getColonne(i).getType()+"("+dt.getColonne(i).getLength()+")");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        catch(GestionReferentielException gre)
        {
            gre.printStackTrace();
        }
        
        System.out.println("\r\nTente de supprimer la table table1");
        
        try
        {
            GestionTables.supprimeTable("table1",db);
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
        catch(GestionReferentielException gre)
        {
            gre.printStackTrace();
        }        
        
        System.out.println("\r\nVérifie l'existence de la table table1");
        
        try
        {
            if (tableExiste("table1",db))
                System.out.println("La table table1 existe");
            else
                System.out.println("La table table1 n'existe pas.");
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }




    // WA 09/2011 Ajout de methodes utilitaires liees au fait qu'un code departement n'est pas forcement compose de deux chiffres
    private final static String TRO_TRONCONS = "tro_troncons_";
    private final static String VHI_VOIES_HISTORISEE = "vhi_voies_historisee_";
    private final static  String VOA_VOIES_AMBIGUES = "voa_voies_ambigues_";
    private final static String VOI_VOIES = "voi_voies_";

    /**
     * Donne le nom de la table des troncons a partir de son code departement
     * @param dptCode
     * @return
     */
    public static String getTroTronconsTableName(String dptCode)
    {
        return getTableNameWithPrefixAndSuffix(TRO_TRONCONS, dptCode);
    }

    /**
     * Donne le nom de la table des voies historisees a partir de son code departement
     * @param dptCode
     * @return
     */
    public static String getVhiVoiesHistoriseeTableName(String dptCode)
    {
        return getTableNameWithPrefixAndSuffix(VHI_VOIES_HISTORISEE, dptCode);
    }

    /**
     * Donne le nom de la table des voies ambigues a partir de son code departement
     * @param dptCode
     * @return
     */
    public static String getVoaVoiesAmbiguesTableName(String dptCode)
    {
        return getTableNameWithPrefixAndSuffix(VOA_VOIES_AMBIGUES, dptCode);
    }

    /**
     * Donne le nom de la table des voies a partir de son code departement
     * @param dptCode
     * @return
     */
    public static String getVoiVoiesTableName(String dptCode)
    {
        return getTableNameWithPrefixAndSuffix(VOI_VOIES, dptCode);
    }

    /**
     * Construit un nom de table a partir d'un prefix et d'un suffixe
     * seuls les caracteres [A-Za-z0-9_] sont authorises, les autres sont remplaces par '_'
     * tout est passe en minuscule.
     * @param prefix
     * @param suffix
     * @return
     */
    public static String getTableNameWithPrefixAndSuffix(String prefix, String suffix)
    {
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(suffix);
        String result = builder.toString().replaceAll("[^A-Za-z0-9_]", "_").toLowerCase();
        return result;
    }

}