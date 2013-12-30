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
import java.sql.Statement;
import java.util.Calendar;
import java.util.Random;
import ppol.jdonref.Algos;
import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.Processus;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.Tables.GestionTables;

/**
 * Fournit des méthodes permettant de gérer l'unicité d'identifiants dans les tables:
 * <ul>
 * <li>Création d'identifiants avec generateId.</li>
 * <li>Mise à jour des identifiants des tables suite à un changement de référentiel avec mise_a_jour_identifiants</li>
 * </ul>
 * @author Julien
 */
public class GestionIdentifiants
{
    static Random r = new Random(Calendar.getInstance().getTimeInMillis());

    static JDONREFParams params = null;
    
    public static void setParams(JDONREFParams params)
    {
        GestionIdentifiants.params = params;
    }
    
    public static JDONREFParams getParams()
    {
        return params;
    }
    
    /**
     * Retourne un code Fantoir aléatoire unique pour la requête spécifiée.<br>
     * Algos.obtientFantoireAleatoire est utilisé.
     * @param psChercheFantoir
     * @return
     * @throws java.sql.SQLException
     */
    public static String generateFantoir(PreparedStatement psChercheFantoir) throws SQLException
    {
        String codeFantoir = Algos.obtientFantoireAleatoire();
        psChercheFantoir.setString(1,codeFantoir);
        ResultSet rsChercheFantoire=psChercheFantoir.executeQuery();
        
        while(rsChercheFantoire.next())
        {
            codeFantoir = Algos.obtientFantoireAleatoire();
            psChercheFantoir.setString(1,codeFantoir);
            rsChercheFantoire=psChercheFantoir.executeQuery();
        }

        return codeFantoir;
    }
    
    /**
     * Permet de générer une clé unique pour les tables spécifiées<br>
     * L'existance de la table et de la colonne ne sont pas vérifiés.<br>
     * La chaine générée est issue de la valeur d'un entier Long (donc inférieur à Long.MAX_VALUE.
     * @throws SQLException un problème est survenu durant une requête sur la table.
     */
    public static String generateId(String nomTable1,String nomTable2,String nomColonne1,String nomColonne2,Connection connection1,Connection connection2) throws SQLException
    {
        boolean done = false;
        long value = 0;
        PreparedStatement ps1 = connection1.prepareStatement("Select \""+nomColonne1+"\" from \""+nomTable1+"\" where \""+nomColonne1+"\"=? limit 1;");
        PreparedStatement ps2 = connection2.prepareStatement("Select \""+nomColonne2+"\" from \""+nomTable2+"\" where \""+nomColonne2+"\"=? limit 1;");
        
        while(!done)
        {
            value = r.nextLong();
            
            ps1.setString(1,Long.toString(value));
            ps2.setString(1,Long.toString(value));
            
            ResultSet rs1 = ps1.executeQuery();
            ResultSet rs2 = ps2.executeQuery();
            
            // Si la valeur est unique dans la table,
            if (!rs1.next() && !rs2.next())
                done = true;
        }
        
        return Long.toString(value);
    }
    
    /**
     * Permet de générer une clé unique pour la table spécifiée<br>
     * L'existance de la table et de la colonne ne sont pas vérifiés.<br>
     * La chaine générée est issue de la valeur d'un entier Long (donc inférieur à Long.MAX_VALUE.
     * @throws SQLException un problème est survenu durant une requête sur la table.
     */
    public static String generateId(String nomTable,String nomColonne,Connection connection) throws SQLException
    {
        boolean done = false;
        long value = 0;
        
        nomTable = nomTable.replaceAll("\"","");
        nomTable = GestionTables.formateNom(nomTable);
        PreparedStatement ps = connection.prepareStatement("Select \""+nomColonne+"\" from "+nomTable+" where \""+nomColonne+"\"=? limit 1;");
        
        while(!done)
        {
            value = Math.abs(r.nextLong());
            
            String strvalue = Long.toString(value);
            
            if (strvalue.length()>params.obtientTailleDesCles())
                strvalue = strvalue.substring(0,params.obtientTailleDesCles());
            
            ps.setString(1,strvalue);
            
            ResultSet rs = ps.executeQuery();
            
            // Si la valeur est unique dans la table,
            if (!rs.next())
                done = true;
        }
        
        return Long.toString(value);
    }
    
    /**
     * Permet de générer une clé unique pour la requête spécifiée.<br>
     * La chaine générée est issue de la valeur d'un entier Long (donc inférieur à Long.MAX_VALUE).
     * @throws SQLException un problème est survenu durant une requête sur la table.
     */
    public static String generateId(PreparedStatement psUniqueId) throws SQLException
    {
        boolean done = false;
        long value = 0;
        
        while(!done)
        {
            value = r.nextLong();
            
            psUniqueId.setString(1,Long.toString(value));
            
            ResultSet rs = psUniqueId.executeQuery();
            
            // Si la valeur est unique pour cette requête,
            if (!rs.next())
                done = true;
            
            rs.close();
        }
        
        return Long.toString(value);
    }
    
    /**
     * Met à jour les identifiants dans le référentiel d'origine à partir des identifiants du référentiel à jour.<br>
     * Les identifiants de voie des tables
     * <ul>
     * <li>voi_voies_dpt</li>
     * <li>voa_voiesambigues_dpt</li>
     * <li>idv_id_voies</li>
     * <li>tro_troncons_dpt_XX</li>
     * <li>adr_adresses_dpt</li>
     * </ul>
     * sont mises à jour.
     */
    private static void mise_a_jour_identifiants_origine(Processus p, String code_departement, Connection connectionOrigine, Connection connectionDestination) throws SQLException, GestionReferentielException, ColonneException
    {
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"MAJ IDENTIFIANTS");
        p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","LANCEMENT"};
        
        int voies_modifies = 0;
        int voies_maj = 0;
        
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        p.state[2] = "PREPARE LES REQUETES";
        
        // Prépare la requête de mise à jour des identifiants des voies
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        PreparedStatement psUpdateVoie = connectionOrigine.prepareStatement("UPDATE \"voi_voies_"+code_departement+"\" set voi_id=? where voi_id=?");
        PreparedStatement psUpdateVoie = connectionOrigine.prepareStatement("UPDATE \"" + GestionTables.getVoiVoiesTableName(code_departement) + "\" set voi_id=? where voi_id=?");
        
        PreparedStatement psUdpateVoiesHistorisee1 = connectionOrigine.prepareStatement("UDPATE \"vhi_voieshistorisee_"+code_departement+"\" set voi_id_suivant=? WHERE voi_id_suivant=?");
        PreparedStatement psUdpateVoiesHistorisee2 = connectionOrigine.prepareStatement("UDPATE \"vhi_voieshistorisee_"+code_departement+"\" set voi_id_suivant=? WHERE voi_id_suivant=?");
        
        // Prépare la requête de mise à jour des identifiants des id de voies
        PreparedStatement psUpdateIdVoies = connectionOrigine.prepareStatement("UPDATE idv_idvoies set voi_id=? where voi_id=?");
        
        // Prépare la requête de mise à jour des identifiants des adresses.
        PreparedStatement psUpdateAdresses = connectionOrigine.prepareStatement("UPDATE \"adr_adresses_"+code_departement+"\" set voi_id=? where voi_id=?");
        
        // Prépare la requête de mise à jour des identifiants des tronçons
        String nomTableTroncons = GestionHistoriqueTables.obtientDerniereTableTroncon(connectionOrigine,code_departement);
        PreparedStatement psUpdateTronconsDroit = connectionOrigine.prepareStatement("UPDATE \""+nomTableTroncons+"\" set voi_id_droit=? where voi_id_droit=?");
        PreparedStatement psUpdateTronconsGauche = connectionOrigine.prepareStatement("UPDATE \""+nomTableTroncons+"\" set voi_id_gauche=? where voi_id_gauche=?");
        
        // Prépare la requête permettant de mettre à jour les identifiants dans lienvoies
        PreparedStatement psUpdateLienVoies = connectionDestination.prepareStatement("UPDATE \"lvo_lien_voies_"+code_departement+"\" set voi_id_source=? where voi_id_source=?");
        
        // Prépare la requête permettant de savoir si un id est déjà utilisé.
        PreparedStatement psUniqueOrigine = connectionOrigine.prepareStatement("SELECT voi_id from idv_id_voies where voi_id=? LIMIT 1");
        
        // Marque la table lienvoies
        GestionMarqueurs.purgeMarqueursDeTable("lvo_lien_voies_"+code_departement,connectionDestination);
        String marker = GestionMarqueurs.ajouteMarqueur("lvo_lien_voies_"+code_departement,connectionDestination);
        
        // Prépare la requête permettant de marquer les voies déjà trait�es.
        PreparedStatement psMarque = connectionDestination.prepareStatement("Update \"lvo_lien_voies_"+code_departement+"\" set \""+marker+"\"=1 where voi_id_source=? and voi_id_destination=?");
        PreparedStatement psMarqueNull = connectionDestination.prepareStatement("Update \"lvo_lien_voies_"+code_departement+"\" set \""+marker+"\"=1 where (voi_id_source is null or voi_id_source='') and voi_id_destination=?");
        
        // Prépare la requête permettant d'énumérer les modifications d'identifiants
        Statement stChercheCorrespond = connectionDestination.createStatement();
        String rqChercheCorrespond = "SELECT voi_id_source,voi_id_destination from \"lvo_lien_voies_"+code_departement+"\" where voi_id_source<>'' and voi_id_destination<>'' and not voi_id_source is null and not voi_id_destination is null and (\""+marker+"\"=0 or \""+marker+"\" is null) LIMIT 1";
        
        boolean autocommitOrigine = connectionOrigine.getAutoCommit();
        boolean autocommitDestination = connectionDestination.getAutoCommit();
        connectionOrigine.setAutoCommit(false);
        connectionDestination.setAutoCommit(false);
        
        try
        {
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"CHERCHE LES CORRESPONDANCES");
            p.state[2] = "CHERCHE LES CORRESPONDANCES";
            ResultSet rsChercheCorrespond = stChercheCorrespond.executeQuery(rqChercheCorrespond);
            
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"TRAITEMENT DES CORRESPONDANCES");
            p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","TRAITEMENT DES CORRESPONDANCES","VOIES TRAITEES","0"};
            
            // Pour chaque modification d'identifiant
            while(rsChercheCorrespond.next())
            {
                String id_orig = rsChercheCorrespond.getString(1);
                String id_dest = rsChercheCorrespond.getString(2);
                
                // Cherche si l'id n'est pas déjà utilisé
                psUniqueOrigine.setString(1,id_dest);
                ResultSet rsOrigine = psUniqueOrigine.executeQuery();
                
                // Si il l'est, 
                if (rsOrigine.next())
                {
                    // Cherche un nouvel identifiant
                    String newid = GestionIdentifiants.generateId("idv_id_voies","lvo_lien_voies_"+code_departement,"voi_id","voi_id_destination",connectionOrigine,connectionDestination);
                    
                    // Met à jour les voies avec ce nouvel identifiant
                    psUpdateVoie.setString(1,newid);
                    psUpdateVoie.setString(2,id_dest);
                    psUpdateVoie.execute();
                    
                    psUdpateVoiesHistorisee1.setString(1,newid);
                    psUdpateVoiesHistorisee1.setString(2,id_dest);
                    psUdpateVoiesHistorisee1.execute();
                    
                    psUdpateVoiesHistorisee2.setString(1,newid);
                    psUdpateVoiesHistorisee2.setString(2,id_dest);
                    psUdpateVoiesHistorisee2.execute();
                    
                    psUpdateTronconsDroit.setString(1,newid);
                    psUpdateTronconsDroit.setString(2,id_dest);
                    psUpdateTronconsDroit.execute();
                    
                    psUpdateTronconsGauche.setString(1,newid);
                    psUpdateTronconsGauche.setString(2,id_dest);
                    psUpdateTronconsGauche.execute();
                    
                    psUpdateIdVoies.setString(1,newid);
                    psUpdateIdVoies.setString(2,id_dest);
                    psUpdateIdVoies.execute();

                    psUpdateAdresses.setString(1,newid);
                    psUpdateAdresses.setString(2,id_dest);
                    psUpdateAdresses.execute();
                    
                    psUpdateLienVoies.setString(1,newid);
                    psUpdateLienVoies.setString(2,id_dest);
                    psUpdateLienVoies.execute();
                    
                    voies_modifies++;
                }
                
                // Effectue les modifications nécessaires, 
                psUpdateVoie.setString(1,id_dest);
                psUpdateVoie.setString(2,id_orig);
                psUpdateVoie.execute();
                
                psUpdateTronconsDroit.setString(1,id_dest);
                psUpdateTronconsDroit.setString(2,id_orig);
                psUpdateTronconsDroit.execute();
                
                psUpdateTronconsGauche.setString(1,id_dest);
                psUpdateTronconsGauche.setString(2,id_orig);
                psUpdateTronconsGauche.execute();
                
                psUpdateAdresses.setString(1,id_dest);
                psUpdateAdresses.setString(2,id_orig);
                psUpdateAdresses.execute();
                
                psUpdateIdVoies.setString(1,id_dest);
                psUpdateIdVoies.setString(2,id_orig);
                psUpdateIdVoies.execute();
                
                // Marque la voie
                psMarque.setString(1,id_orig);
                psMarque.setString(2,id_dest);
                psMarque.execute();
                
                voies_maj++;
                
                if (voies_maj%50==0)
                    p.state[4] = Integer.toString(voies_maj);
                
                connectionOrigine.commit();                    
                connectionDestination.commit();
                
                stChercheCorrespond.close();
                rsChercheCorrespond.close();
                
                stChercheCorrespond = connectionDestination.createStatement();
                rsChercheCorrespond = stChercheCorrespond.executeQuery(rqChercheCorrespond);
            }
            
            rsChercheCorrespond.close();
            stChercheCorrespond.close();
            
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"RECHERCHE DES ORPHELINS");
            p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","RECHERCHE DES ORPHELINS"};
            
            Statement stChercheCorrespondPas = connectionDestination.createStatement();
            String rqChercheCorrespondPas = "SELECT voi_id_source,voi_id_destination from \"lvo_lien_voies_"+code_departement+"\" where (voi_id_source='' or voi_id_source is null) and voi_id_destination<>'' and not voi_id_destination is null and (\""+marker+"\"=0 or \""+marker+"\" is null) LIMIT 1";
            ResultSet rsChercheCorrespondPas = stChercheCorrespondPas.executeQuery(rqChercheCorrespondPas);
            
            int id_traites = 0;
            
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"TRAITEMENT DES ORPHELINS");
            p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","TRAITEMENT DES ORPHELINS","VOIES TRAITEES","0"};
            
            // Pour chaque identifiant de la destination qui n'a pas son équivalent,
            while(rsChercheCorrespondPas.next())
            {
                String id_orig = rsChercheCorrespondPas.getString(1);
                String id_dest = rsChercheCorrespondPas.getString(2);
                
                // Cherche si l'id n'est pas déjà utilisé
                psUniqueOrigine.setString(1,id_dest);
                ResultSet rsOrigine = psUniqueOrigine.executeQuery();
                
                // Si il l'est, 
                if (rsOrigine.next())
                {
                    // Cherche un nouvel identifiant
                    String newid = GestionIdentifiants.generateId("idv_id_voies","lvo_lien_voies_"+code_departement,"voi_id","voi_id_destination",connectionOrigine,connectionDestination);
                    
                    // Met à jour les voies avec ce nouvel identifiant
                    psUpdateVoie.setString(1,newid);
                    psUpdateVoie.setString(2,id_dest);
                    psUpdateVoie.execute();
                    
                    psUpdateTronconsDroit.setString(1,newid);
                    psUpdateTronconsDroit.setString(2,id_dest);
                    psUpdateTronconsDroit.execute();
                    
                    psUpdateTronconsGauche.setString(1,newid);
                    psUpdateTronconsGauche.setString(2,id_dest);
                    psUpdateTronconsGauche.execute();
                    
                    psUpdateIdVoies.setString(1,newid);
                    psUpdateIdVoies.setString(2,id_dest);
                    psUpdateIdVoies.execute();
                    
                    psUpdateLienVoies.setString(1,newid);
                    psUpdateLienVoies.setString(2,id_dest);
                    psUpdateLienVoies.execute();
                      
                    connectionOrigine.commit();
                    
                    voies_modifies++;
                }
                
                // Marque la voie
                psMarqueNull.setString(1,id_dest);
                psMarqueNull.execute();
                
                id_traites++;
                if (id_traites%50==0)
                    p.state[4] = Integer.toString(id_traites);
                
                connectionDestination.commit();
                
                stChercheCorrespondPas.close();
                rsChercheCorrespondPas.close();
                
                stChercheCorrespondPas = connectionDestination.createStatement();
                rsChercheCorrespondPas = stChercheCorrespondPas.executeQuery(rqChercheCorrespondPas);
            }
            
            stChercheCorrespondPas.close();
            rsChercheCorrespondPas.close();
            
            psMarqueNull.close();
            psUniqueOrigine.close();
            psUpdateLienVoies.close();
            psUpdateTronconsDroit.close();
            psUpdateTronconsGauche.close();
            psUpdateIdVoies.close();
            psUdpateVoiesHistorisee2.close();
            psUdpateVoiesHistorisee1.close();
            psUpdateVoie.close();
            
            GestionMarqueurs.supprimeMarqueur("lvo_lien_voies_"+code_departement,marker,connectionDestination);
        }
        finally
        {
            connectionOrigine.setAutoCommit(autocommitOrigine);
            connectionDestination.setAutoCommit(autocommitDestination);
        }
        
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"MISE A JOUR IDENTIFIANTS TERMINE");
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"identifiants reattribués "+voies_modifies);
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"identifiants maj "+voies_maj);
        p.resultat.add("MISE A JOUR IDENTIFIANTS TERMINE");
        p.resultat.add("identifiants reattribués "+voies_modifies);
        p.resultat.add("identifiants maj "+voies_maj);
    }
    
    /**
     * Met à jour les identifiants dans le référentiel à jour à partir des identifiants du référentiel d'origine.<br>
     * Les identifiants des voies de la table tro_troncons_dpt_numero sont mis à jour.
     */
    private static void mise_a_jour_identifiants_destination(Processus p, String code_departement, Connection connectionOrigine, Connection connectionDestination) throws SQLException, GestionReferentielException, ColonneException
    {   
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"LANCEMENT");
        p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","LANCEMENT"};
        
        int voies_modifies = 0;
        int voies_maj = 0;
        
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"PREPARATION DES REQUETES");
        p.state[2] = "PREPARATION DES REQUETES";
        // Prépare la requête de mise à jour des identifiants des tron�ons
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        PreparedStatement psUpdateTronconsDroit = connectionDestination.prepareStatement("UPDATE \"tro_troncons_"+code_departement+"\" set voi_id_droit=? where voi_id_droit=?");
//        PreparedStatement psUpdateTronconsGauche = connectionDestination.prepareStatement("UPDATE \"tro_troncons_"+code_departement+"\" set voi_id_gauche=? where voi_id_gauche=?");
        PreparedStatement psUpdateTronconsDroit = connectionDestination.prepareStatement("UPDATE \"" + GestionTables.getTroTronconsTableName(code_departement) + "\" set voi_id_droit=? where voi_id_droit=?");
        PreparedStatement psUpdateTronconsGauche = connectionDestination.prepareStatement("UPDATE \"" + GestionTables.getTroTronconsTableName(code_departement) + "\" set voi_id_gauche=? where voi_id_gauche=?");
        
        // Prépare la requête permettant de mettre à jour les identifiants dans lienvoies
        PreparedStatement psUpdateLienVoies = connectionDestination.prepareStatement("UPDATE \"lvo_lien_voies_"+code_departement+"\" set voi_id_destination=? where voi_id_destination=?");
        
        // Prépare la requête permettant de savoir si un id est déjà utilisé.
        PreparedStatement psUniqueDestination = connectionDestination.prepareStatement("SELECT voi_id_destination from \"lvo_lien_voies_"+code_departement+"\" where voi_id_destination=? LIMIT 1");
                
        // Marque la table lienvoies
        GestionMarqueurs.purgeMarqueursDeTable("lvo_lien_voies_"+code_departement,connectionDestination);
        String marker = GestionMarqueurs.ajouteMarqueur("lvo_lien_voies_"+code_departement,connectionDestination);
        
        // Prépare la requête permettant de marquer les voies déjà trait�es.
        PreparedStatement psMarque = connectionDestination.prepareStatement("Update \"lvo_lien_voies_"+code_departement+"\" set \""+marker+"\"=1 where voi_id_source=? and voi_id_destination=?");
        PreparedStatement psMarqueNull = connectionDestination.prepareStatement("Update \"lvo_lien_voies_"+code_departement+"\" set \""+marker+"\"=1 where voi_id_source=? and (voi_id_destination is null or voi_id_destination='')");
        
        boolean autocommitDestination = connectionDestination.getAutoCommit();
        connectionDestination.setAutoCommit(false);
        
        // Prépare la requête permettant d'énumérer les modifications d'identifiants
        Statement stChercheCorrespond = connectionDestination.createStatement();
        String rqChercheCorrespond = "SELECT voi_id_source,voi_id_destination from \"lvo_lien_voies_"+code_departement+"\" where voi_id_source<>'' and voi_id_destination<>'' and not voi_id_source is null and not voi_id_destination is null and (\""+marker+"\"=0 or \""+marker+"\" is null) LIMIT 1";

        try
        {
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"RECHERCHE DES CORRESPONDANCES");
            p.state[2] = "RECHERCHE DES CORRESPONDANCES";
            ResultSet rsChercheCorrespond = stChercheCorrespond.executeQuery(rqChercheCorrespond);
            
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"TRAITEMENT DES CORRESPONDANCES");
            p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","TRAITEMENT CORRESPONDANCES","VOIES TRAITEES","0"};
            // Pour chaque modification d'identifiant
            while(rsChercheCorrespond.next())
            {
                String id_orig = rsChercheCorrespond.getString(1);
                String id_dest = rsChercheCorrespond.getString(2);
                
                // Cherche si l'id n'est pas déjà utilisé
                psUniqueDestination.setString(1,id_orig);
                ResultSet rsOrigine = psUniqueDestination.executeQuery();
                
                // Si il l'est, 
                if (rsOrigine.next())
                {
                    // Cherche un nouvel identifiant
                    String newid = GestionIdentifiants.generateId("idv_id_voies","lvo_lien_voies_"+code_departement,"voi_id","voi_id_destination",connectionOrigine,connectionDestination);
                    
                    // Met à jour les voies avec ce nouvel identifiant
                    psUpdateTronconsDroit.setString(1,newid);
                    psUpdateTronconsDroit.setString(2,id_orig);
                    psUpdateTronconsDroit.execute();
                    
                    psUpdateTronconsGauche.setString(1,newid);
                    psUpdateTronconsGauche.setString(2,id_orig);
                    psUpdateTronconsGauche.execute();
                    
                    psUpdateLienVoies.setString(1,newid);
                    psUpdateLienVoies.setString(2,id_orig);
                    psUpdateLienVoies.execute();
                    
                    voies_modifies++;
                }
                
                // Effectue les modifications nécessaires, 
                psUpdateTronconsDroit.setString(1,id_orig);
                psUpdateTronconsDroit.setString(2,id_dest);
                psUpdateTronconsDroit.execute();
                
                psUpdateTronconsGauche.setString(1,id_orig);
                psUpdateTronconsGauche.setString(2,id_dest);
                psUpdateTronconsGauche.execute();

                // Marque l'association
                psMarque.setString(1,id_orig);
                psMarque.setString(2,id_dest);
                psMarque.execute();
                
                connectionDestination.commit();
                voies_maj++;
                
                if (voies_maj%50==0)
                {
                    p.state[4] = Integer.toString(voies_maj);
                }
                
                stChercheCorrespond.close();
                rsChercheCorrespond.close();
                
                stChercheCorrespond = connectionDestination.createStatement();
                rsChercheCorrespond = stChercheCorrespond.executeQuery(rqChercheCorrespond);
            }
            
            rsChercheCorrespond.close();
            stChercheCorrespond.close();
            
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"RECHERCHE DES ORPHELINS");
            p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","RECHERCHE DES ORPHELINS"};
            Statement stChercheCorrespondPas = connectionDestination.createStatement();
            String rqChercheCorrespondPas = "SELECT voi_id_source from \"lvo_lien_voies_"+code_departement+"\" where (voi_id_destination='' or voi_id_destination is null) and voi_id_source<>'' and not voi_id_source is null and (\""+marker+"\"=0 or \""+marker+"\" is null) LIMIT 1";
            ResultSet rsChercheCorrespondPas = stChercheCorrespondPas.executeQuery(rqChercheCorrespondPas);
            
            int voies_traitees = 0;
            
            GestionLogs.getInstance().logAdmin(p.numero,p.version,"TRAITEMENT DES ORPHELINS");
            p.state = new String[]{"EN COURS","MAJ IDENTIFIANTS","TRAITEMENT DES ORPHELINS","VOIES TRAITEES","0"};
            // Pour chaque identifiant de la destination qui n'a pas son équivalent,
            while(rsChercheCorrespondPas.next())
            {
                String id_orig = rsChercheCorrespondPas.getString(1);
                
                // Cherche si l'id n'est pas déjà utilisé
                psUniqueDestination.setString(1,id_orig);
                ResultSet rsOrigine = psUniqueDestination.executeQuery();
                
                // Si il l'est, 
                if (rsOrigine.next())
                {
                    // Cherche un nouvel identifiant
                    String newid = GestionIdentifiants.generateId("idv_id_voies","lvo_lien_voies_"+code_departement,"voi_id","voi_id_destination",connectionOrigine,connectionDestination);
                    
                    // Met à jour les voies avec ce nouvel identifiant
                    psUpdateTronconsDroit.setString(1,newid);
                    psUpdateTronconsDroit.setString(2,id_orig);
                    psUpdateTronconsDroit.execute();
                    
                    psUpdateTronconsGauche.setString(1,newid);
                    psUpdateTronconsGauche.setString(2,id_orig);
                    psUpdateTronconsGauche.execute();
                    
                    psUpdateLienVoies.setString(1,newid);
                    psUpdateLienVoies.setString(2,id_orig);
                    psUpdateLienVoies.execute();
                    
                    voies_modifies++;
                }
                
                // Marque la voie
                psMarqueNull.setString(1,id_orig);
                psMarqueNull.execute();
                
                voies_traitees++;
                if (voies_traitees%50==0)
                    p.state[4] = Integer.toString(voies_traitees);
                
                connectionDestination.commit();
                
                stChercheCorrespondPas.close();
                rsChercheCorrespondPas.close();
                
                stChercheCorrespondPas = connectionDestination.createStatement();
                rsChercheCorrespondPas = stChercheCorrespondPas.executeQuery(rqChercheCorrespondPas);
            }

            stChercheCorrespondPas.close();
            rsChercheCorrespondPas.close();
            
            psMarque.close();
            psUniqueDestination.close();
            psUpdateLienVoies.close();
            psUpdateTronconsDroit.close();
            psUpdateTronconsGauche.close();

            GestionMarqueurs.supprimeMarqueur("lvo_lien_voies_"+code_departement,marker,connectionDestination);
        }
        finally
        {            
            connectionDestination.setAutoCommit(autocommitDestination);
        }
        
        p.finished = true;
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"MISE A JOUR IDENTIFIANTS TERMINE");
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"identifiants reattribués "+voies_modifies);
        GestionLogs.getInstance().logAdmin(p.numero,p.version,"identifiants maj "+voies_maj);
        p.resultat.add("MISE A JOUR IDENTIFIANTS TERMINE");
        p.resultat.add("identifiants reattribués "+voies_modifies);
        p.resultat.add("identifiants maj "+voies_maj);
    }
    
    /**
     * Met à jour les identfiants du référentiel d'origine à partir du référentiel destination préparé.<br>
     * Si les id source sont utilisés (voi_id_source=true), alors les identifiants de voies du référentiel actuel sont conservés, 
     * et les identifiants de voies du référentiel à jour sont modifiés avec ceux du référentiel actuel.<br>
     * Si les id destination sont utilisés (voi_id_source=false), alors les identifiants de voies du référentiel actuel sont modifiés avec
     * ceux du référentiel à jour.
     * @param processus le processus attribué à cette tâche
     * @param code_departement le département concerné
     * @param connectionOrigine la connection au référentiel actuel
     * @param connectionDestination la connection au référentiel à jour
     * @param date la date à laquelle la mise à jour sera valable
     * @param idsource spécifie s'il faut utiliser les id source ou les id destination
     */
    public static void mise_a_jour_identifiants(Processus p, String code_departement, Connection connectionOrigine, Connection connectionDestination, boolean idsource) throws SQLException, GestionReferentielException, ColonneException
    {
        if (idsource)
        {
            // logs dans la méthode
            mise_a_jour_identifiants_origine(p,code_departement,connectionOrigine,connectionDestination);
        }
        else
        {
            // logs dans la méthode
            mise_a_jour_identifiants_destination(p,code_departement,connectionOrigine,connectionDestination);
        }
    }
}