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
 * @author Julien
 */
public class Commune
{
    
    public String codeinsee;
    public String dpt_code_departement;
    public String commune;
    public String com_nom_desab;
    public String com_nom_origine;
    public String com_nom_pq;
    public String com_code_insee_commune;
    public Date t0;
    public Date t1;
    public String geometrie;
    public String codepostal;
    public String centroide;

    public Commune(String codeinsee, String dpt_code_departement, String commune, String com_nom_desab, String com_nom_origine, String com_nom_pq, String com_code_insee_commune, Date t0, Date t1, String codepostal) {
        this.codeinsee = codeinsee;
        this.dpt_code_departement = dpt_code_departement;
        this.commune = commune;
        this.com_nom_desab = com_nom_desab;
        this.com_nom_origine = com_nom_origine;
        this.com_nom_pq = com_nom_pq;
        this.com_code_insee_commune = com_code_insee_commune;
        this.t0 = t0;
        this.t1 = t1;
//        this.geometrie = geometrie;
        this.codepostal = codepostal;
    }

    public Commune(ResultSet rs,int[] index) throws SQLException
    {
      codeinsee = rs.getString(index[0]);
      dpt_code_departement = rs.getString(index[1]);
      commune = rs.getString(index[2]);
      com_nom_desab = rs.getString(index[3]);
      com_nom_origine = rs.getString(index[4]);
      com_nom_pq = rs.getString(index[5]);
      com_code_insee_commune = rs.getString(index[6]);
      t0 = rs.getTimestamp(index[7]);
      t1 = rs.getTimestamp(index[8]);
//      geometrie = rs.getString(index[9]);
      codepostal = rs.getString(index[9]);
    }
    
//    public Commune(ResultSet rs) throws SQLException
//    {
//      codeinsee = rs.getString(1);
//      dpt_code_departement = rs.getString(2);
//      commune = rs.getString(3);
//      com_nom_desab = rs.getString(4);
//      com_nom_origine = rs.getString(5);
//      com_nom_pq = rs.getString(6);
//      com_code_insee_commune = rs.getString(7);
//      t0 = rs.getTimestamp(8);
//      t1 = rs.getTimestamp(9);
//      geometrie = rs.getString(10);
//      codepostal = rs.getString(11);
//      centroide = rs.getString(12);
//    }
    
    public Commune(ResultSet rs) throws SQLException
    {
        ResultSetMetaData metaData = rs.getMetaData();
        int nbColumn = metaData.getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            String nomColonne = metaData.getColumnLabel(i+1); 
            switch(nomColonne){
                case "com_code_insee" : codeinsee = rs.getString(nomColonne); break;
                case "dpt_code_departement" : dpt_code_departement = rs.getString(nomColonne); break;
                case "com_nom" : commune = rs.getString(nomColonne); break;
                case "com_nom_desab" : com_nom_desab = rs.getString(nomColonne); break;
                case "com_nom_origine" : com_nom_origine = rs.getString(nomColonne); break;
                case "com_nom_pq" : com_nom_pq = rs.getString(nomColonne); break;
                case "com_code_insee_commune" : com_code_insee_commune = rs.getString(nomColonne); break;
                case "t0" : t0 = rs.getTimestamp(nomColonne); break;
                case "t1" : t1 = rs.getTimestamp(nomColonne); break;
                case "geometrie" : geometrie = rs.getString(nomColonne); break;
                case "cdp_code_postal" : codepostal = rs.getString(nomColonne); break;
                case "centroide" : centroide = rs.getString(nomColonne); break;
                default: throw new Error("Nom ou labelle \""+nomColonne+"\" n'est pas renseigné");
            }
        }
    }

    public String[] getLignes()
    {
        return new String[]{"","","","","",toString()};
    }

    public String getCodeArrondissement()
    {
        if (com_code_insee_commune!=null)
            return codepostal.substring(3,5);
        return null;
    }
    
    public String getCodeDepartement()
    {
        return dpt_code_departement;
//        return codepostal.substring(0,2);
    }
    
    public boolean equals(Commune commune)
    {
        if (!this.commune.equals(commune.commune)) return false;
        if (!this.codeinsee.equals(commune.codeinsee)) return false;
        if (!this.codepostal.equals(commune.codepostal)) return false;
        if (!this.com_code_insee_commune.equals(commune.com_code_insee_commune)) return false;
        if (!this.dpt_code_departement.equals(commune.dpt_code_departement)) return false;
        if (!this.com_nom_desab.equals(commune.com_nom_desab)) return false;
        if (!this.com_nom_origine.equals(commune.com_nom_origine)) return false;
        if (!this.com_nom_pq.equals(commune.com_nom_pq)) return false;
        if (!this.t0.equals(commune.t0)) return false;
        if (!this.t1.equals(commune.t1)) return false;
        if (!this.geometrie.equals(commune.geometrie)) return false;

        return true;
    }
    
    public String toLigne6()
    {
        String arrondissement = getCodeArrondissement();
        
        return codepostal+ " "+ commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public String toLigne7()
    {
        return "FRANCE";
    }
    
    public String toString()
    {
        String arrondissement = getCodeArrondissement();
        
        return codepostal+ " "+ commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public String toStringWithoutNumbers()
    {
        return commune;
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

         JsonObjectBuilder commmune = Json.createObjectBuilder();

         commmune.add("code_insee",codeinsee);
//         commmune.add("code_departement",getCodeDepartement());
         commmune.add("code_departement",dpt_code_departement);
         if (com_code_insee_commune!=null)
            commmune.add("code_insee_commune",com_code_insee_commune);
         commmune.add("code_pays","FR1");
         commmune.add("commune",commune);
         String code_arrondissement = getCodeArrondissement();
         if (code_arrondissement!=null)
            commmune.add("code_arrondissement",code_arrondissement);         
         commmune.add("code_postal",codepostal);
//         commmune.add("codes","");
         commmune.add("pays","FRANCE");
         commmune.add("t0" ,getDatForm(t0));
         commmune.add("t1",getDatForm(t1));
         //commmune.add("ligne5","");
         commmune.add("ligne6",toLigne6().trim());
         commmune.add("ligne7",toLigne7().trim());
         commmune.add("type","commmune");
         
         if (withGeometry)
            commmune.add("geometrie" , geometrieJSON(geometrie));
         commmune.add("pin" , centroideJSON(centroide));  

         //commmune.add("fullName",toString().trim());

        return commmune.build();
    }
}