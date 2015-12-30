/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.elasticsearch.river.jdonrefv4.jdonrefv3.dao;

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
                "t0 AS dpt_t0, " +                    //4
                "t1 AS dpt_t1, " +                    //5
                "st_AsGeoJSON(st_transform(geometrie,4326)) AS dpt_geometrie, " + //6
                "st_AsGeoJSON(ST_Centroid(st_transform(geometrie,4326))) AS dpt_centroide " +    //7
                "FROM dpt_departements "+
                "order by dpt_code_departement asc";
        

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
    
    
        public ResultSet getAllDepartement(Connection connection, String[] dept) throws SQLException
    {
       
            String sql = "SELECT " +
                    "dpt_code_departement, " +  //1
                    "dpt_projection, " +        //2    
                    "dpt_referentiel, " +       //3
                    "t0 AS dpt_t0, " +          //4
                    "t1 AS dpt_t1, " +          //5
                    "st_AsGeoJSON(st_transform(geometrie,4326)) AS dpt_geometrie, " + //6
                    "st_AsGeoJSON(ST_Centroid(st_transform(geometrie,4326))) AS dpt_centroide " +    //7
                    "FROM dpt_departements ";
            if(dept.length != 0 && dept != null){
                int taille = dept.length;
                int i = 0;
                sql += "WHERE dpt_code_departement = '"+dept[i]+"' ";
                taille--;
                i++;
                while(taille>0){
                    sql += "OR dpt_code_departement = '"+dept[i]+"' ";
                    taille--;
                    i++;
                }
                sql +="order by dpt_code_departement asc";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                return rs;
            }else 
                return getAllDepartement(connection);
    }
}
