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
 * @author Julien
 */
public class Pays {
    public String pays_sov_a3;
    public String ligne7;
    public String pay_projection;      
    public String pay_referentiel; 
    public Date t0;
    public Date t1;
    public String geometrie;
    public String centroide;
    

    public Pays() {
    }

    public Pays(String string) {
        pays_sov_a3 = string;
    }

    public Pays(String pays_sov_a3, String ligne7,String pay_referentiel, Date t0, Date t1) {
        this.pays_sov_a3 = pays_sov_a3;
        this.ligne7 = ligne7;
        //this.pay_projection = pay_projection;
        this.pay_referentiel = pay_referentiel;
        this.t0 = t0;
        this.t1 = t1;
//        this.geometrie = geometrie;
    }

    public Pays(ResultSet rs,int[] index) throws SQLException
    {
        pays_sov_a3 = rs.getString(index[0]);
        ligne7 = rs.getString(index[1]);
        //pay_projection = rs.getString(index[2]);
        pay_referentiel = rs.getString(index[2]);
        t0 = rs.getTimestamp(index[3]);
        t1 = rs.getTimestamp(index[4]);
//        geometrie = rs.getString(index[5]);
    }
    public Pays(ResultSet rs) throws SQLException
    {
        pays_sov_a3 = rs.getString(1);
        ligne7 = rs.getString(2);
        //pay_projection = rs.getString(2);
        pay_referentiel = rs.getString(3);
        t0 = rs.getTimestamp(4);
        t1 = rs.getTimestamp(5);
        geometrie = rs.getString(6);
        centroide = rs.getString(7);
    }
    
    @Override
    public String toString()
    {
        return ligne7;
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
//        SimpleDateFormat formater = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return formater.format(d);
    }


    public JsonObject toJSONDocument(boolean withGeometry)
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
         JsonObjectBuilder pays = Json.createObjectBuilder();
         
         pays.add("code_pays", pays_sov_a3);
//         departement.add("codes","");
         pays.add("pays",ligne7);
//         departement.add("dpt_referentiel", dpt_referentiel);
         pays.add("t0" , getDatForm(t0));
         pays.add("t1" , getDatForm(t1));
         pays.add("ligne7",ligne7); 
         //pays.add("type","pays");
         
         if (withGeometry)  
            pays.add("geometrie" , geometrieJSON(geometrie));
         pays.add("pin" , centroideJSON(centroide));   
         
         //departement.add("fullName",toString());
//         departement.add("fullName_sansngram",toString().trim());

//         builder.add("properties", departement);
//         return builder.build();
         
           return pays.build();
    }
}
