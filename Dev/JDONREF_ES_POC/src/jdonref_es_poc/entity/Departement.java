package jdonref_es_poc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
        
    }
    
    public Departement(ResultSet rs,int[] index) throws SQLException
    {
        code_departement = rs.getString(index[0]);
        dpt_projection = rs.getString(index[1]);
        dpt_referentiel = rs.getString(index[2]);
        t0 = rs.getTimestamp(index[3]);
        t1 = rs.getTimestamp(index[4]);
        geometrie = rs.getString(index[5]);
    }
    public Departement(ResultSet rs) throws SQLException
    {
        code_departement = rs.getString(1);
        dpt_projection = rs.getString(2);
        dpt_referentiel = rs.getString(3);
        t0 = rs.getTimestamp(4);
        t1 = rs.getTimestamp(5);
        geometrie = rs.getString(6);
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
         
         builder.add("code_departement", code_departement);
         builder.add("dpt_referentiel", dpt_referentiel);
         builder.add("t0" , t0.toString());
         builder.add("t1" , t1.toString());
//         builder.add("t0" , t0.getTime());
//         builder.add("t1" , t1.getTime());
         builder.add("geometrie" , geometrieJSON(geometrie));
         
         builder.add("fullName",toString());
         builder.add("fullName_sansngram",toString().trim());
         builder.add("ligne6",toLigne6());
         builder.add("ligne7",toLigne7());
         
         return builder.build();
    }
    
    
    
}
