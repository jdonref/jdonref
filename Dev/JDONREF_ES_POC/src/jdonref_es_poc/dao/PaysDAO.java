package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Julien
 */
public class PaysDAO
{
    public ResultSet getAllPays(Connection connection) throws SQLException
    {
        String sql = "SELECT " +
                "pay_sov_a3, " +  //1
                "pay_nom_origine, " +        //2    
                "pay_referentiel, " +       //3
                "t0, " +                    //4
                "t1, " +                    //5
                "st_AsGeoJSON(st_transform(pay_geometrie,4326))," + //6
                "st_AsGeoJSON(ST_Centroid(st_transform(pay_geometrie,4326))) " +    //7
                    "FROM pay_pays ";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
