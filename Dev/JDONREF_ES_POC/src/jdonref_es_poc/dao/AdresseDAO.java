package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Julien
 */
public class AdresseDAO
{
    /**
     * adr_id
     * adr_rep
     * adr_numero
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
    public ResultSet getAllAdressesOfDepartement(Connection connection,String dpt) throws SQLException
    {
        
        String sql = "SELECT " +
                "com_communes.com_code_insee," +  // 1
                "com_communes.dpt_code_departement," + // 2
                "cdp_code_postal," + // 3
                "com_nom," + // 4
                "com_nom_desab," + // 5
                "com_nom_origine," + // 6
                "com_nom_pq," + // 7
                "com_code_insee_commune," + // 8
                "com_communes.t0," + // 9
                "com_communes.t1, " + // 10
                "voi_voies_"+dpt+".voi_id, " + // 11
                "voi_code_fantoir," + // 12
                "voi_nom," + // 13
                "voi_nom_desab," + // 14
                "voi_nom_origine," + // 15
                "voi_type_de_voie," + // 16
                "voi_type_de_voie_pq," + // 17
                "voi_lbl," + // 18
                "voi_lbl_pq," + // 19
                "voi_lbl_sans_articles," + // 20
                "voi_lbl_sans_articles_pq," + // 21
                "voi_mot_determinant," + // 22
                "voi_mot_determinant_pq," + // 23
                "voi_min_numero," + // 24
                "voi_max_numero, " + // 25
                "adr_id," + // 26
                "adr_rep," + // 27
                "adr_numero " + // 28
                "FROM adr_adresses_"+dpt+", voi_voies_"+dpt+", com_communes "+
                "WHERE voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee and " +
                "adr_adresses_"+dpt+".voi_id = voi_voies_"+dpt+".voi_id";

        
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}