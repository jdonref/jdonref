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
public class DepartementDAO
{
    public ResultSet getAllDepartement(Connection connection) throws SQLException
    {
        String sql = "SELECT " +
                "dpt_code_departement, " +  //1
                "dpt_projection, " +        //2    
                "dpt_referentiel, " +       //3
                "t0, " +                    //4
                "t1, " +                    //5
                "st_AsGeoJSON(st_transform(geometrie,4326))" +    //6
                    "FROM dpt_departements ";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
