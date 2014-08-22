/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author akchana
 */
public class PoizonDAO {
    public ResultSet getAllPoizon(Connection connection, String lastUpdate) throws SQLException
    {
        String sql = "SELECT " +
        "poizon_service, " +                //1
        "poizon_cle, " +                    //2
        "poizon_cle_pq, " +                 //3
        "poizon_lbl, " +                    //4
        "poizon_lbl_pq, " +                 //5
        "poizon_lbl_sans_articles, " +      //6
        "poizon_lbl_sans_articles_pq, " +   //7
        "poizon_id1, " +                    //8
        "poizon_id2, " +                    //9
        "poizon_id3, " +                    //10
        "poizon_id4, " +                    //11
        "poizon_id5, " +                    //12
        "poizon_id6, " +                    //13
        "poizon_id7, " +                    //14
        "poizon_donnee1, " +                //15
        "poizon_donnee2, " +                //16
        "poizon_donnee3, " +                //17
        "poizon_donnee4, " +                //18
        "poizon_donnee5, " +                //19
        "poizon_donnee6, " +                //20
        "poizon_donnee7, " +                //21
        "poizon_donnee_origine1, " +        //22
        "poizon_donnee_origine2, " +        //23
        "poizon_donnee_origine3, " +        //24
        "poizon_donnee_origine4, " +        //25
        "poizon_donnee_origine5, " +        //26
        "poizon_donnee_origine6, " +        //27
        "poizon_donnee_origine7, " +        //28
        "t0, " +                            //29
        "t1, " +                            //30
        "poizon_referentiel, " +            //31
        "st_AsGeoJSON(st_transform(geometrie,4326))," +                     //32
        "st_AsGeoJSON(ST_Centroid(st_transform(geometrie,4326))) " +        //33
        "FROM poizon " +
                "WHERE t0>= '"+lastUpdate+"'" +
                "order by t0";      
        

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
    
    public ResultSet getDateT0AllPoizon(Connection connection) throws SQLException
    {
        String sql = "SELECT t0 " +
                "FROM poizon " +
                "group by t0 "+
                "order by t0 desc";
    
    
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    
    }

    
    
}
