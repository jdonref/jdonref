package jdonref_lucene_poc.dao;

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
}