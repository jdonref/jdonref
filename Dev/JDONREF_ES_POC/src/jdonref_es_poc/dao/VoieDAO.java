/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Julien
 */
public class VoieDAO
{
    /**
     * voi_id, 
     * voi_nom, 
     * com_communes.com_code_insee,
     * cdp_code_postal, 
     * voi_type_de_voie, 
     * voi_lbl, 
     * voi_min_numero, 
     * voi_max_numero,
     * com_nom,
     * com_code_insee_commune
     * 
     * @param connection
     * @param dpt
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet getAllVoiesOfDepartement(Connection connection,String dpt) throws SQLException
    {
        String sql = "SELECT voi_id, voi_nom_desab, com_communes.com_code_insee,cdp_code_postal, voi_type_de_voie, voi_lbl, voi_min_numero, voi_max_numero,com_nom,com_code_insee_commune "+
                     "from voi_voies_"+dpt+", com_communes "+
                     "where voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
