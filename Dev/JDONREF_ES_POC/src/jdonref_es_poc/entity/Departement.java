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
public class Departement {
    public String code_departement;
    public String dpt_projection;      
    public String dpt_referentiel; 
    public Date t0;
    public Date t1;
    public String geometrie;
    public String centroide;
    

    public Departement() {
    }

    public Departement(String string) {
        code_departement = string;
    }

    public Departement(String code_departement, String dpt_projection, String dpt_referentiel, Date t0, Date t1) {
        this.code_departement = code_departement;
        this.dpt_projection = dpt_projection;
        this.dpt_referentiel = dpt_referentiel;
        this.t0 = t0;
        this.t1 = t1;
//        this.geometrie = geometrie;
    }

    public Departement(ResultSet rs,int[] index) throws SQLException
    {
        code_departement = rs.getString(index[0]);
        dpt_projection = rs.getString(index[1]);
        dpt_referentiel = rs.getString(index[2]);
        t0 = rs.getTimestamp(index[3]);
        t1 = rs.getTimestamp(index[4]);
//        geometrie = rs.getString(index[5]);
    }
    public Departement(ResultSet rs) throws SQLException
    {
        code_departement = rs.getString(1);
        dpt_projection = rs.getString(2);
        dpt_referentiel = rs.getString(3);
        t0 = rs.getTimestamp(4);
        t1 = rs.getTimestamp(5);
        geometrie = rs.getString(6);
        centroide = rs.getString(7);

    }

    public String toLigne6()
    {
        return toString();
    }
    
    public String toLigne7()
    {
        return "FRANCE";
    }
    
    @Override
    public String toString()
    {
        return code_departement;
    }
    
    public String toStringWithoutNumbers()
    {
        return toString();
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
         JsonObjectBuilder departement = Json.createObjectBuilder();
         
         departement.add("code_departement", code_departement);
         departement.add("code_pays","FR1");
//         departement.add("codes","");
         departement.add("pays","FRANCE");
//         departement.add("dpt_referentiel", dpt_referentiel);
         departement.add("t0" , getDatForm(t0));
         departement.add("t1" , getDatForm(t1));
         departement.add("ligne6",toLigne6());
         departement.add("ligne7",toLigne7()); 
         departement.add("type","departement");
//         departement.add("pin" , centroideJSON(centroide));
         if (withGeometry)
            departement.add("geometrie" , geometrieJSON(geometrie));
         departement.add("fullName",toString());
//         departement.add("fullName_sansngram",toString().trim());

//         builder.add("properties", departement);
//         return builder.build();
         
           return departement.build();
         
    }

}
