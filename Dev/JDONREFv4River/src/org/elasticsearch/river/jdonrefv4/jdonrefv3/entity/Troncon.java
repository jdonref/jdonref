/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
  public String centroide;
  

  public String voi_id;
  public int tro_numero_debut;
  public int tro_numero_fin;
  public String tro_rep_debut;
  public String tro_rep_fin;
  
    public Troncon() {
    }
  

//    public Troncon(ResultSet rs) throws SQLException
//    {
//        this.tro_id = rs.getString(1);
//        this.voi_id_droit = rs.getString(2);
//        this.voi_id_gauche = rs.getString(3);
//        this.tro_numero_debut_droit = rs.getInt(4);
//        this.tro_numero_debut_gauche = rs.getInt(5);
//        this.tro_numero_fin_droit = rs.getInt(6);
//        this.tro_numero_fin_gauche = rs.getInt(7);
//        this.tro_rep_debut_droit = rs.getString(8);
//        this.tro_rep_debut_gauche = rs.getString(9);
//        this.tro_rep_fin_droit = rs.getString(10);
//        this.tro_rep_fin_gauche = rs.getString(11);
//        this.tro_typ_adr = rs.getString(12);
//        this.t0 = rs.getTimestamp(13);
//        this.t1 = rs.getTimestamp(14);
//        this.geometrie = rs.getString(15);
//        
//    }
//    
//        public Troncon(ResultSet rs, String side) throws SQLException
//    {
//        voie = new Voie(rs);    
//        this.tro_id = rs.getString(28);
//        this.voi_id_droit = rs.getString(29);
//        this.tro_numero_debut_droit = rs.getInt(30);
//        this.tro_numero_fin_droit = rs.getInt(31);
//        this.tro_rep_debut_droit = rs.getString(32);
//        this.tro_rep_fin_droit = rs.getString(33);
//        this.tro_typ_adr = rs.getString(34);
//        this.t0 = rs.getTimestamp(35);
//        this.t1 = rs.getTimestamp(36);
//        this.geometrie = rs.getString(37);
//        this.centroide = rs.getString(38);
//        
//    }
    
    public Troncon(ResultSet rs) throws SQLException
    {
        int nbColumnUnknown = 0;
        ResultSetMetaData metaData = rs.getMetaData();
        int nbColumn = metaData.getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            String nomColonne = metaData.getColumnLabel(i+1); 
            switch(nomColonne){
                case "tro_id" : tro_id = rs.getString(nomColonne); break;
                case "voi_id_droit" : voi_id_droit = rs.getString(nomColonne); break;
                case "voi_id_gauche" : voi_id_gauche = rs.getString(nomColonne); break;
                case "tro_numero_debut_droit" : tro_numero_debut_droit = rs.getInt(nomColonne); break;
                case "tro_numero_debut_gauche" : tro_numero_debut_gauche = rs.getInt(nomColonne); break;
                case "tro_numero_fin_droit" : tro_numero_fin_droit = rs.getInt(nomColonne); break;
                case "tro_numero_fin_gauche" : tro_numero_fin_gauche = rs.getInt(nomColonne); break;
                case "tro_rep_debut_droit" : tro_rep_debut_droit = rs.getString(nomColonne); break;
                case "tro_rep_debut_gauche" : tro_rep_debut_gauche = rs.getString(nomColonne); break;
                case "tro_rep_fin_droit" : tro_rep_fin_droit = rs.getString(nomColonne); break;
                case "tro_rep_fin_gauche" : tro_rep_fin_gauche = rs.getString(nomColonne); break;
                case "tro_typ_adr" : tro_typ_adr = rs.getString(nomColonne); break;
                case "tr_t0" : t0 = rs.getTimestamp(nomColonne); break;
                case "tr_t1" : t1 = rs.getTimestamp(nomColonne); break;
                case "tr_geometrie" : geometrie = rs.getString(nomColonne); break;
                case "tr_centroide" : centroide = rs.getString(nomColonne); break;
                default: nbColumnUnknown = nbColumnUnknown+1;
            }
        }  
    }
    
    public Troncon(ResultSet rs, String side) throws SQLException
    {
        this(rs);
        voie = new Voie(rs);  
    }

    
    public String toLigne7()
    {
        return "FRANCE";
    }
    
    public String toString()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
         return (voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.codepostal+ " "+ voie.commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public JsonObject geometrieJSON(String geometrie){
        GeomUtil geomUtil = new GeomUtil();
        HashMap<String,String> hash = geomUtil.toHashGeo(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()  
                .add("type", hash.get("type"))
                .add("coordinates", geomUtil.toGeojson(hash.get("coordinates"), hash.get("type")));
        return geo.build();
    }    
    
    public JsonObject centroideJSON(String centroide){
        GeomUtil geomUtil = new GeomUtil();
        HashMap<String,String> hash = geomUtil.toHashGeo(centroide);
        JsonObjectBuilder geo = Json.createObjectBuilder()  
                .add("centroide", geomUtil.toGeojson(hash.get("coordinates"), hash.get("type")));
        return geo.build();
    }
    
    public String getDatForm(Date d){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }    
    
    public JsonObject toJSONDocument(boolean withGeometry)
    {

         JsonObjectBuilder troncon = Json.createObjectBuilder();
//                .add("toString", toString());
         
         troncon.add("tro_id", tro_id);
         if(voi_id!=null)
         troncon.add("voi_id", voi_id);
         troncon.add("code_insee", voie.commune.codeinsee);
         if (voie.commune.com_code_insee_commune!=null)
            troncon.add("code_insee_commune", voie.commune.com_code_insee_commune);
         troncon.add("code_departement", voie.commune.dpt_code_departement);
         troncon.add("code_pays", "FR1");
//         troncon.add("codes", "");
         troncon.add("numero_debut", tro_numero_debut);
         troncon.add("numero_fin", tro_numero_fin);
         if(tro_rep_debut != null)
         troncon.add("repetition_debut", tro_rep_debut);
         if(tro_rep_fin != null)
         troncon.add("repetition_fin", tro_rep_fin);
         troncon.add("type_de_voie", voie.typedevoie);
         if (voie.article!=null)
         troncon.add("article", voie.article);
         troncon.add("libelle", voie.libelle);
         troncon.add("commune", voie.commune.commune);
         String code_arrondissement = voie.commune.getCodeArrondissement();
         if (code_arrondissement!=null)
            troncon.add("code_arrondissement",code_arrondissement);
         troncon.add("code_postal", voie.cdp_code_postal);
         troncon.add("pays", "FRANCE");
         troncon.add("t0" , getDatForm(t0));
         troncon.add("t1" , getDatForm(t1));
         //troncon.add("ligne4" , "");
         //troncon.add("ligne5" , "");
         //troncon.add("ligne6" , "");
         troncon.add("ligne7" , toLigne7().trim());
         troncon.add("type" , "troncon");
         if (withGeometry)
            troncon.add("geometrie" , geometrieJSON(geometrie));
         troncon.add("pin" , centroideJSON(centroide));
         
         //troncon.add("fullName",toString().trim());

         
         return troncon.build();
    }


}
