/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_lucene_poc.dao;

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
        String sql = "SELECT dpt_code_departement from dpt_departements";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
