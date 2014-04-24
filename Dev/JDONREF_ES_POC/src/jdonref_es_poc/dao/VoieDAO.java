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
//        String sql = "SELECT " +
//                "voi_id, " +
//                "voi_nom_desab, " +
//                "com_communes.com_code_insee," +
//                "cdp_code_postal, " +
//                "voi_type_de_voie, " +
//                "voi_lbl, " +
//                "voi_min_numero, " +
//                "voi_max_numero," +
//                "com_nom," +
//                "com_code_insee_commune "+
//                     "from voi_voies_"+dpt+", com_communes "+
//                        "where voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee";
        
        
        String sql = "SELECT " +
                "com_communes.com_code_insee," +
                "com_communes.dpt_code_departement," +
                "cdp_code_postal," +
                "com_nom," +
                "com_nom_desab," +
                "com_nom_origine," +
                "com_nom_pq," +
                "com_code_insee_commune," +
                "com_communes.t0," +
                "com_communes.t1, " +
                "voi_id, " +
                "voi_code_fantoir," +
                "voi_nom," +
                "voi_nom_desab," +
                "voi_nom_origine," +
                "voi_type_de_voie," +
                "voi_type_de_voie_pq," +
                "voi_lbl," +
                "voi_lbl_pq," +
                "voi_lbl_sans_articles," +
                "voi_lbl_sans_articles_pq," +
                "voi_mot_determinant," +
                "voi_mot_determinant_pq," +
                "voi_min_numero," +
                "voi_max_numero " +
                "FROM voi_voies_"+dpt+", com_communes "+
                "WHERE voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee";

        
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
