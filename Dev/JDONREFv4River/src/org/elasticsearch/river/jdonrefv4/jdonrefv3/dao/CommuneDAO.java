package org.elasticsearch.river.jdonrefv4.jdonrefv3.dao;

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
        String sql="SELECT " +
                "com_communes.com_code_insee," +            //1
                "com_communes.dpt_code_departement," +      //2
                "com_nom," +                                //3
                "com_nom_desab," +                          //4
                "com_nom_origine," +                        //5
                "com_nom_pq," +                             //6
                "com_code_insee_commune," +                 //7
                "com_communes.t0," +                        //8
                "com_communes.t1," +                        //9
                "st_AsGeoJSON(st_transform(geometrie,4326))," +    //10
                "cdp_code_postal, " +                        //11
                "st_AsGeoJSON(ST_Centroid(st_transform(geometrie,4326))) "+ //12
                "FROM com_communes, cdp_codes_postaux " +
                "WHERE com_communes.com_code_insee = cdp_codes_postaux.com_code_insee";
          
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
        public ResultSet getAllCommunes(Connection connection, String[] dept) throws SQLException
    {
        if(dept.length != 0 && dept != null){
            int taille = dept.length;
            int i = 0;
            String sql="SELECT " +
                    "com_communes.com_code_insee," +            //1
                    "com_communes.dpt_code_departement," +      //2
                    "com_nom," +                                //3
                    "com_nom_desab," +                          //4
                    "com_nom_origine," +                        //5
                    "com_nom_pq," +                             //6
                    "com_code_insee_commune," +                 //7
                    "com_communes.t0," +                        //8
                    "com_communes.t1," +                        //9
                    "st_AsGeoJSON(st_transform(geometrie,4326))," +    //10
                    "cdp_code_postal, " +                        //11
                    "st_AsGeoJSON(ST_Centroid(st_transform(geometrie,4326))) "+ //12
                    "FROM com_communes, cdp_codes_postaux " +
                    "WHERE com_communes.com_code_insee = cdp_codes_postaux.com_code_insee"
                    + " and ( com_communes.dpt_code_departement = '"+dept[i]+"' ";
            
            taille--;
            i++;
            while(taille>0){
                sql += "OR com_communes.dpt_code_departement = '"+dept[i]+"' ";
                taille--;
                i++;
            }
            sql += ")";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            return rs;
        }else 
            return getAllCommunes(connection);
    }
}
