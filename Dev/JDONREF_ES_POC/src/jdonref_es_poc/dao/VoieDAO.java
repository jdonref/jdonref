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
                "com_communes.com_code_insee," +            //1
                "com_communes.dpt_code_departement," +      //2
                "com_nom," +                                //3
                "com_nom_desab," +                          //4
                "com_nom_origine," +                        //5
                "com_nom_pq," +                             //6
                "com_code_insee_commune," +                 //7
                "com_communes.t0," +                        //8
                "com_communes.t1, " +                       //9
                "voi_id, " +                                //10
                "voi_code_fantoir," +                       //11
                "voi_nom," +                                //12
                "voi_nom_desab," +                          //13
                "voi_nom_origine," +                        //14
//                "voi_voies_"+dpt+".com_code_insee,"+        //15
                "cdp_code_postal," +                        //16
                "voi_type_de_voie," +                       //17
                "voi_type_de_voie_pq," +                    //18
                "voi_lbl," +                                //19
                "voi_lbl_pq," +                             //20
                "voi_lbl_sans_articles," +                  //21
                "voi_lbl_sans_articles_pq," +               //22
                "voi_mot_determinant," +                    //23
                "voi_mot_determinant_pq," +                 //24
                "voi_min_numero," +                         //25
                "voi_max_numero," +                         //26
                "voi_voies_"+dpt+".t0," +                   //27
                "voi_voies_"+dpt+".t1 " +                   //28
                "FROM voi_voies_"+dpt+", com_communes "+
                "WHERE voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee";

        
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
}
