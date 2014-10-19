/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

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
public class Poizon {

    public int poizon_service;
    public String poizon_cle;
    public String poizon_cle_pq;
    public String poizon_lbl;
    public String poizon_lbl_pq;
    public String poizon_lbl_sans_articles;
    public String poizon_lbl_sans_articles_pq;
    public String poizon_id1;
    public String poizon_id2;
    public String poizon_id3;
    public String poizon_id4;
    public String poizon_id5;
    public String poizon_id6;
    public String poizon_id7;
    public String poizon_donnee1;
    public String poizon_donnee2;
    public String poizon_donnee3;
    public String poizon_donnee4;
    public String poizon_donnee5;
    public String poizon_donnee6;
    public String poizon_donnee7;
    public String poizon_donnee_origine1;
    public String poizon_donnee_origine2;
    public String poizon_donnee_origine3;
    public String poizon_donnee_origine4;
    public String poizon_donnee_origine5;
    public String poizon_donnee_origine6;
    public String poizon_donnee_origine7;
    public Date t0;
    public Date t1;
    public String poizon_referentiel;
    public String geometrie;
    public String centroide;

    public Poizon() {
    }

    public Poizon(ResultSet rs) throws SQLException {

        poizon_service = rs.getInt(1);
        poizon_cle = rs.getString(2);
        poizon_cle_pq = rs.getString(3);
        poizon_lbl = rs.getString(4);
        poizon_lbl_pq = rs.getString(5);
        poizon_lbl_sans_articles = rs.getString(6);
        poizon_lbl_sans_articles_pq = rs.getString(7);
        poizon_id1 = rs.getString(8);
        poizon_id2 = rs.getString(9);
        poizon_id3 = rs.getString(10);
        poizon_id4 = rs.getString(11);
        poizon_id5 = rs.getString(12);
        poizon_id6 = rs.getString(13);
        poizon_id7 = rs.getString(14);
        poizon_donnee1 = rs.getString(15);
        poizon_donnee2 = rs.getString(16);
        poizon_donnee3 = rs.getString(17);
        poizon_donnee4 = rs.getString(18);
        poizon_donnee5 = rs.getString(19);
        poizon_donnee6 = rs.getString(20);
        poizon_donnee7 = rs.getString(21);
        poizon_donnee_origine1 = rs.getString(22);
        poizon_donnee_origine2 = rs.getString(23);
        poizon_donnee_origine3 = rs.getString(24);
        poizon_donnee_origine4 = rs.getString(25);
        poizon_donnee_origine5 = rs.getString(26);
        poizon_donnee_origine6 = rs.getString(27);
        poizon_donnee_origine7 = rs.getString(28);
        t0 = rs.getTimestamp(29);
        t1 = rs.getTimestamp(30);
        poizon_referentiel = rs.getString(31);
        geometrie = rs.getString(32);
        centroide = rs.getString(33);

    }
    
    
    public String toString()
    {
        return poizon_donnee1 +" "
                +(poizon_donnee2==null?"":(poizon_donnee2+" "))
                +(poizon_donnee3==null?"":(poizon_donnee3+" "))
                +(poizon_donnee4==null?"":(poizon_donnee4+" "))
                +(poizon_donnee5==null?"":(poizon_donnee5+" "))
                +(poizon_donnee6==null?"":(poizon_donnee6+" "))
                +(poizon_donnee7==null?"":(poizon_donnee7+" "));     
    }
    
    
    public JsonObject geometrieJSON(String geometrie) {
        GeomUtil geomUtil = new GeomUtil();
        HashMap<String, String> hash = geomUtil.toHashGeo(geometrie);
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

    public String getDatForm(Date d) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat formater = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return formater.format(d);
    }
    
    
    
        public JsonObject toJSONDocument(boolean withGeometry)
    {

         JsonObjectBuilder poizon = Json.createObjectBuilder();

         poizon.add("poizon_id", poizon_id1);  
         if(poizon_id4!=null)
            poizon.add("adr_id",poizon_id4);
//         poizon.add("tro_id","..");
//         poizon.add("voi_id","..");
//         poizon.add("code_insee_commune","..");
         if(poizon_id6!=null){
            poizon.add("code_insee",poizon_id6);
            poizon.add("code_departement",poizon_donnee6.substring(0,2));
            //poizon.add("code_arrondissement",poizon_donnee6.substring(3,5)); // TODO : uniquement dans quelques communes
         }
         if(poizon_id7!=null)
            poizon.add("code_pays", poizon_id7);
         else
            poizon.add("code_pays", "FR");
//         poizon.add("codes","");
//         poizon.add("numero","..");
//        poizon.add("repetition","..");
//         poizon.add("type_de_voie","..");
//         poizon.add("article","..");
//         poizon.add("libelle","..");
//         poizon.add("commune",".."); //////
         
         if(poizon_donnee6!=null)
            poizon.add("code_postal",poizon_donnee6);
        poizon.add("t0" , getDatForm(t0));
        poizon.add("t1" , getDatForm(t1));
        poizon.add("poizon_service", poizon_service); 
        if(poizon_donnee1!=null && poizon_donnee1.length()>0)
            poizon.add("ligne1", poizon_donnee1);
        if(poizon_donnee2!=null && poizon_donnee2.length()>0)
            poizon.add("ligne2", poizon_donnee2);
        if(poizon_donnee3!=null && poizon_donnee3.length()>0)
            poizon.add("ligne3", poizon_donnee3);
        if(poizon_donnee4!=null && poizon_donnee4.length()>0)
            poizon.add("ligne4", poizon_donnee4);
        if(poizon_donnee5!=null && poizon_donnee5.length()>0)
            poizon.add("ligne5", poizon_donnee5);
        if(poizon_donnee6!=null && poizon_donnee5.length()>0)
            poizon.add("ligne6", poizon_donnee6);
        if(poizon_donnee7!=null && poizon_donnee7.length()>0)
        {
            poizon.add("ligne7", poizon_donnee7);
            poizon.add("pays", poizon_donnee7);
        }
        else
        {
            poizon.add("ligne7", "FRANCE");
            poizon.add("pays","FRANCE");
        }
        poizon.add("type","poizon");
//        poizon.add("poizon_referentiel", poizon_referentiel);
        if (withGeometry)
            poizon.add("geometrie" , geometrieJSON(geometrie));
        poizon.add("pin" , centroideJSON(centroide));
            
         //poizon.add("fullName",toString().trim());

         return poizon.build();   
    }
    
    

    
    
    
}
