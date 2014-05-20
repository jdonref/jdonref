package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Julien
 */
public class TronconDAO
{
    public ResultSet getAllTronconsGaucheOfDepartment(Connection connection,String dpt) throws SQLException
    {
        String sql = "SELECT voi_id_gauche, voi_nom, com_communes.com_code_insee,cdp_code_postal, voi_type_de_voie, voi_lbl,tro_numero_debut_gauche, tro_numero_fin_gauche,com_nom, com_code_insee_commune "+
                     "from voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
                     "where voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee and "+
                     "voi_id_gauche = voi_id";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
    public ResultSet getAllTronconsDroitOfDepartment(Connection connection,String dpt) throws SQLException
    {
        String sql = "SELECT voi_id_droit, voi_nom, com_communes.com_code_insee,cdp_code_postal, voi_type_de_voie, voi_lbl,tro_numero_debut_droit, tro_numero_fin_droit,com_nom, com_code_insee_commune "+
                     "from voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
                     "where voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee and "+
                     "voi_id_droit = voi_id";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    


    public ResultSet getAllTronconsByDep(Connection connection,String dpt) throws SQLException
    {
         String sql = "SELECT " +      
                "tro_id, " +                            // 1
                "voi_id_droit, " +                      // 2
                "voi_id_gauche, " +                     // 3
                "tro_numero_debut_droit, " +            // 4
                "tro_numero_debut_gauche, " +           // 5
                "tro_numero_fin_droit, " +              // 6
                "tro_numero_fin_gauche, " +             // 7
                "tro_rep_debut_droit, " +               // 8
                "tro_rep_debut_gauche, " +              // 9
                "tro_rep_fin_droit, " +                 // 10
                "tro_rep_fin_gauche, " +                // 11
                "tro_typ_adr, " +                       // 12
                "tro_troncons_"+dpt+"_0.t0, " +         // 13
                "tro_troncons_"+dpt+"_0.t1, " +         // 14
                "st_astext(st_transform(geometrie,4326)) "+ // 15
                "FROM tro_troncons_"+dpt+"_0 ";

   
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }

 
}