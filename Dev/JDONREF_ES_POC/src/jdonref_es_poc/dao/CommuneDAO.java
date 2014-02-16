package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Julien
 */
public class CommuneDAO
{
    /**
     * com_communes.com_code_insee,
     * com_nom,
     * com_communes.dpt_code_departement,
     * cdp_code_postal,
     * com_code_insee_commune
     * 
     * @param connection
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet getAllCommunes(Connection connection) throws SQLException
    {
        String sql = "SELECT com_communes.com_code_insee,com_nom,com_communes.dpt_code_departement,cdp_code_postal,com_code_insee_commune from com_communes, cdp_codes_postaux where com_communes.com_code_insee = cdp_codes_postaux.com_code_insee";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
