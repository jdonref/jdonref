/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_es_poc.entity;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author akchana
 */


public class Troncon {
  
  public Voie voie;  
  public String tro_id;
//  String voi_id_droit;
//  String voi_id_gauche;
  public String voi_id;
//  int tro_numero_debut_droit;
//  int tro_numero_debut_gauche;
  public int tro_numero_debut;
//  int tro_numero_fin_droit;
//  int tro_numero_fin_gauche;
  public int tro_numero_fin;
//  String tro_rep_debut_droit;
//  String tro_rep_debut_gauche;
  public String tro_rep_debut;
//  String tro_rep_fin_droit;
//  String tro_rep_fin_gauche;
  public String tro_rep_fin;
  public String tro_typ_adr;
  
  public Date t0;
  public Date t1;
  public String geometrie;

    public Troncon() {
    }

    public Troncon(Voie voie, String tro_id, String voi_id, int tro_numero_debut, int tro_numero_fin, String tro_rep_debut, String tro_rep_fin, String tro_typ_adr, Date t0, Date t1, String geometrie) {
        this.voie = voie;
        this.tro_id = tro_id;
        this.voi_id = voi_id;
        this.tro_numero_debut = tro_numero_debut;
        this.tro_numero_fin = tro_numero_fin;
        this.tro_rep_debut = tro_rep_debut;
        this.tro_rep_fin = tro_rep_fin;
        this.tro_typ_adr = tro_typ_adr;
        this.t0 = t0;
        this.t1 = t1;
        this.geometrie = geometrie;
    }




    
    public Troncon(ResultSet rs) throws SQLException
    {
        voie = new Voie(rs,new int[]{11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27},new int[]{1,2,3,4,5,6,7,8,9,10});
        this.tro_id = rs.getString(28);
        this.voi_id = rs.getString(29);
        this.tro_numero_debut = rs.getInt(30);
        this.tro_numero_fin = rs.getInt(31);
        this.tro_rep_debut = rs.getString(32);
        this.tro_rep_fin = rs.getString(33);
        this.tro_typ_adr = rs.getString(34);
        this.t0 = rs.getTimestamp(35);
        this.t1 = rs.getTimestamp(36);
        this.geometrie = rs.getString(37);
    }



    
    GeometrieUtil geomUtil = GeometrieUtil.getInstance();
    public JsonObject geometrieJSON(String geometrie){
//        GeometrieUtil geomUtil = GeometrieUtil.getInstance();
//        GeometrieUtil geomUtil = new GeometrieUtil();
        String type = geomUtil.getGeoTYPE(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()
         .add("type", type.toLowerCase())
         .add("coordinates", geomUtil.getGeoJSON(geometrie, type));

        JsonObjectBuilder location = Json.createObjectBuilder()
         .add("location", geo);
        
        return location.build();
    }
    
    public JsonObject toJSONDocument()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
//                .add("toString", toString());
         
//         builder.add("fullName",code_departement);
         
         builder.add("tro_id", tro_id);
         builder.add("voi_id", voi_id);
         builder.add("t0" , t0.toString());
         builder.add("t1" , t1.toString());
//         builder.add("t0" , t0.getTime());
//         builder.add("t1" , t1.getTime());
         builder.add("geometrie" , geometrieJSON(geometrie));
         
//         builder.add("fullName",toString());
//         builder.add("fullName_sansngram",toString().trim());

         
         return builder.build();
    }
    
    
    
}
