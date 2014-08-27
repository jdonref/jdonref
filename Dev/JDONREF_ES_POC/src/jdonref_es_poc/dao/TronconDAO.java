package jdonref_es_poc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Julien
 */
public class TronconDAO
{
    public ResultSet getAllTronconsGaucheOfDepartment(Connection connection,String dpt) throws SQLException
    {
        String sql = "SELECT voi_id_gauche, voi_nom, com_communes.com_code_insee,cdp_code_postal, voi_type_de_voie, voi_lbl,tro_numero_debut_gauche, tro_numero_fin_gauche,com_nom, com_code_insee_commune "+
                     "from voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
                     "where voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee and "+
                     "voi_id_gauche = voi_id";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
    public ResultSet getAllTronconsDroitOfDepartment(Connection connection,String dpt) throws SQLException
    {
        String sql = "SELECT voi_id_droit, voi_nom, com_communes.com_code_insee,cdp_code_postal, voi_type_de_voie, voi_lbl,tro_numero_debut_droit, tro_numero_fin_droit,com_nom, com_code_insee_commune "+
                     "from voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
                     "where voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee and "+
                     "voi_id_droit = voi_id";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    


    public ResultSet getAllTronconsByDep(Connection connection,String dpt) throws SQLException
    {
         String sql = "SELECT " +      
                "tro_id, " +                            // 1
                "voi_id_droit, " +                      // 2
                "voi_id_gauche, " +                     // 3
                "tro_numero_debut_droit, " +            // 4
                "tro_numero_debut_gauche, " +           // 5
                "tro_numero_fin_droit, " +              // 6
                "tro_numero_fin_gauche, " +             // 7
                "tro_rep_debut_droit, " +               // 8
                "tro_rep_debut_gauche, " +              // 9
                "tro_rep_fin_droit, " +                 // 10
                "tro_rep_fin_gauche, " +                // 11
                "tro_typ_adr, " +                       // 12
                "tro_troncons_"+dpt+"_0.t0, " +         // 13
                "tro_troncons_"+dpt+"_0.t1, " +         // 14
                "st_AsGeoJSON(st_transform(geometrie,4326)) "+ // 15
                "FROM tro_troncons_"+dpt+"_0 ";

   
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
    
    public ResultSet getAllTronconsByDepD(Connection connection,String dpt) throws SQLException
    {
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
                "cdp_code_postal," +                        //15
                "voi_type_de_voie," +                       //16
                "voi_type_de_voie_pq," +                    //17
                "voi_lbl," +                                //18
                "voi_lbl_pq," +                             //19
                "voi_lbl_sans_articles," +                  //20
                "voi_lbl_sans_articles_pq," +               //21
                "voi_mot_determinant," +                    //22
                "voi_mot_determinant_pq," +                 //23
                "voi_min_numero," +                         //24
                "voi_max_numero," +                         //25
                "voi_voies_"+dpt+".t0," +                   //26
                "voi_voies_"+dpt+".t1, " +                   //27
                "tro_id, " +                            // 28
                "voi_id_droit, " +                      // 29
                "tro_numero_debut_droit, " +            // 30
                "tro_numero_fin_droit, " +              // 31   
                "tro_rep_debut_droit, " +               // 32             
                "tro_rep_fin_droit, " +                 // 33
                "tro_typ_adr, " +                       // 34
                "tro_troncons_"+dpt+"_0.t0, " +         // 35
                "tro_troncons_"+dpt+"_0.t1, " +         // 36
//                "st_astext(st_transform(tro_troncons_"+dpt+"_0.geometrie,4326)) " +    // 37
                "st_AsGeoJSON(st_transform(tro_troncons_"+dpt+"_0.geometrie,4326)) " +    // 37

                "FROM voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
                "WHERE voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee " +
                        "and voi_id_droit = voi_id";        
       
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }
    
    public ResultSet getAllTronconsByDepG(Connection connection,String dpt) throws SQLException
    {
//         String sql = "SELECT " +      
//                 
//                "tro_id, " +                            // 1
//                "voi_id_gauche, " +                     // 2
//                "tro_numero_debut_gauche, " +           // 3
//                "tro_numero_fin_gauche, " +             // 4
//                "tro_rep_debut_gauche, " +              // 5
//                "tro_rep_fin_gauche, " +                // 6
//                "tro_typ_adr, " +                       // 7
//                "tro_troncons_"+dpt+"_0.t0, " +         // 8
//                "tro_troncons_"+dpt+"_0.t1, " +         // 9
//                "st_astext(st_transform(geometrie,4326)) "+ // 10
//                "FROM voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
//                "WHERE voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee " +
//                        "and voi_id_gauche = voi_id";        
        
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
                "cdp_code_postal," +                        //15
                "voi_type_de_voie," +                       //16
                "voi_type_de_voie_pq," +                    //17
                "voi_lbl," +                                //18
                "voi_lbl_pq," +                             //19
                "voi_lbl_sans_articles," +                  //20
                "voi_lbl_sans_articles_pq," +               //21
                "voi_mot_determinant," +                    //22
                "voi_mot_determinant_pq," +                 //23
                "voi_min_numero," +                         //24
                "voi_max_numero," +                         //25
                "voi_voies_"+dpt+".t0," +                   //26
                "voi_voies_"+dpt+".t1, " +                   //27
                "tro_id, " +                            // 28
                "voi_id_gauche, " +                      // 29
                "tro_numero_debut_gauche, " +            // 30
                "tro_numero_fin_gauche, " +              // 31   
                "tro_rep_debut_gauche, " +               // 32             
                "tro_rep_fin_gauche, " +                 // 33
                "tro_typ_adr, " +                       // 34
                "tro_troncons_"+dpt+"_0.t0, " +         // 35
                "tro_troncons_"+dpt+"_0.t1, " +         // 36
//                "st_astext(st_transform(tro_troncons_"+dpt+"_0.geometrie,4326)) " +    // 37
                "st_AsGeoJSON(st_transform(tro_troncons_"+dpt+"_0.geometrie,4326)) " +    // 37

                "FROM voi_voies_"+dpt+", com_communes, tro_troncons_"+dpt+"_0 "+
                "WHERE voi_voies_"+dpt+".com_code_insee = com_communes.com_code_insee " +
                        "and voi_id_gauche = voi_id";        
       
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        return rs;
    }


 
}