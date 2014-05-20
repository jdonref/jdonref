/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_es_poc.entity;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import jdonref_es_poc.dao.TronconDAO;

/**
 *
 * @author akchana
 */


public class Troncon {
  
  public String tro_id;
  public String voi_id_droit;
  public String voi_id_gauche;
  public int tro_numero_debut_droit;
  public int tro_numero_debut_gauche;
  public int tro_numero_fin_droit;
  public int tro_numero_fin_gauche;
  public String tro_rep_debut_droit;
  public String tro_rep_debut_gauche;
  public String tro_rep_fin_droit;
  public String tro_rep_fin_gauche;
  public String tro_typ_adr;
  
  public Date t0;
  public Date t1;
  public String geometrie;

    public Troncon() {
    }
  

    public Troncon(ResultSet rs) throws SQLException
    {
        
        this.tro_id = rs.getString(1);
        this.voi_id_droit = rs.getString(2);
        this.voi_id_gauche = rs.getString(3);
        this.tro_numero_debut_droit = rs.getInt(4);
        this.tro_numero_debut_gauche = rs.getInt(5);
        this.tro_numero_fin_droit = rs.getInt(6);
        this.tro_numero_fin_gauche = rs.getInt(7);
        this.tro_rep_debut_droit = rs.getString(8);
        this.tro_rep_debut_gauche = rs.getString(9);
        this.tro_rep_fin_droit = rs.getString(10);
        this.tro_rep_fin_gauche = rs.getString(11);
        this.tro_typ_adr = rs.getString(12);
        this.t0 = rs.getTimestamp(13);
        this.t1 = rs.getTimestamp(14);
        this.geometrie = rs.getString(15);
        
    }

    
    public JsonObject geometrieJSON(String geometrie){
        GeometrieUtil geomUtil = new GeometrieUtil();
        String type = geomUtil.getGeoTYPE(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()
         .add("type", type.toLowerCase())
         .add("coordinates", geomUtil.getGeoJSON(geometrie, type));
        return geo.build();
    }
    
    public JsonObject toJSONDocument()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
//                .add("toString", toString());
         
         builder.add("tro_id", tro_id);
         if(voi_id_droit!=null)
         builder.add("voi_id_droit", voi_id_droit);
         if(voi_id_gauche!=null)
         builder.add("voi_id_gauche", voi_id_gauche);    
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
