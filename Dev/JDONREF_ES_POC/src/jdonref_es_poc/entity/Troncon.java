/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_es_poc.entity;


import java.sql.ResultSet;
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

  public String voi_id;
  public int tro_numero_debut;
  public int tro_numero_fin;
  public String tro_rep_debut;
  public String tro_rep_fin;
  
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
    
    public Troncon(ResultSet rs, int test) throws SQLException
    {
        voie = new Voie(rs);    
        this.tro_id = rs.getString(28);
        this.voi_id_droit = rs.getString(29);
        this.tro_numero_debut_droit = rs.getInt(30);
        this.tro_numero_fin_droit = rs.getInt(31);
        this.tro_rep_debut_droit = rs.getString(32);
        this.tro_rep_fin_droit = rs.getString(33);
        this.tro_typ_adr = rs.getString(34);
        this.t0 = rs.getTimestamp(35);
        this.t1 = rs.getTimestamp(36);
        this.geometrie = rs.getString(37);
        
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
    
    public String getDatForm(Date d){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }    
    
    public JsonObject toJSONDocument()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
         
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
         troncon.add("codes", "");
         troncon.add("numero_debut", tro_numero_debut);
         troncon.add("numero_fin", tro_numero_fin);
         if(tro_rep_debut != null)
         troncon.add("repetition_debut", tro_rep_debut);
         if(tro_rep_fin != null)
         troncon.add("repetition_fin", tro_rep_fin);
         troncon.add("typedevoie", voie.typedevoie);
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
         troncon.add("ligne4" , "ligne4");
         troncon.add("ligne5" , "ligne5");
         troncon.add("ligne6" , "ligne6");
         troncon.add("ligne7" , toLigne7().trim());
         troncon.add("type" , "troncon");
         troncon.add("geometrie" , geometrieJSON(geometrie));
         troncon.add("fullName",toString().trim());

         builder.add("adresse", troncon);
         
         return builder.build();
    }
    
    
    
}
