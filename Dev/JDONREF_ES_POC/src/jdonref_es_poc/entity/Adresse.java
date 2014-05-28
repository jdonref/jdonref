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
 * @author Julien
 */
public class Adresse
{
    public Adresse(Voie v, String numero, String repetition)
    {
        voie = v;
        
        this.numero = numero;
        this.repetition = repetition;
    }
    
    public Adresse(ResultSet rs,String numero, String repetition,float lat,float lon) throws SQLException
    {
//        voie = new Voie(rs,new int[]{11,12,13,14,15,16,17,18,19,20,21,22,23,24,25},new int[]{1,2,3,4,5,6,7,8,9,10});
        voie = new Voie(rs,new int[]{10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27},new int[]{1,2,3,4,5,6,7,8,9,15});
        
        this.numero = numero;
        if (numero.equals("0") || numero.trim().equals(""))
            this.numero = null;
        
        if (repetition.equals("0") || repetition.trim().equals(""))
        {
            this.repetition = null;
        }
        else
        {
            this.repetition = repetition;
        }
        this.lat = lat;
        this.lon = lon;
    }   
    
    public Adresse(ResultSet rs) throws SQLException
    {
        voie = new Voie(rs,new int[]{10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27},new int[]{1,2,3,4,5,6,7,8,9,15});
        
        this.idadresse = rs.getString(28);
        this.repetition = rs.getString(29);
        this.numero = rs.getString(30);
        if (numero.equals("0") || numero.trim().equals(""))
            this.numero = null;
        if (repetition.equals("0") || repetition.trim().equals(""))
        {
            this.repetition = null;
        }
      t0 = rs.getTimestamp(31);
      t1 = rs.getTimestamp(32);
      geometrie = rs.getString(33);          
//        setXY(rs.getString(33));
        
        
    }
    
    public String[] getLignes()
    {
        String[] lignes = voie.getLignes();
        lignes[2] = batiment;
        lignes[3] = numero+" "+repetition+" "+lignes[3];
        return lignes;
    }
    
    public String batiment;
    
    public String idtroncon;
    public String idadresse;
    public String numero;
    public String repetition;
    public Date t0;
    public Date t1;
    
    public String geometrie;

    public float lat;
    public float lon;

    
    public Voie voie;
    
    public String toLigne4()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
        
        return voie.commune.codepostal+ " "+ voie.commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public String toLigne6()
    {
        return numero+" "+((repetition==null||repetition.endsWith("0"))?"":(repetition+" "))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle;
    }
    
    public String toLigne7()
    {
        return "FRANCE";
    }
    
    public String toStringWithoutNumbers()
    {
        return ((repetition==null||repetition.endsWith("0"))?"":(repetition+" "))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+  voie.commune.commune;
    }
    
    public String toString()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
        
        return numero+" "+((repetition==null||repetition.endsWith("0"))?"":(repetition+" "))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.codepostal+ " "+ voie.commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public JsonObject geometrieJSON(String geometrie){
        GeomUtil geomUtil = new GeomUtil();
        HashMap<String,String> hash = geomUtil.getHash(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()  
                .add("type", hash.get("type"))
                .add("coordinates", geomUtil.getGeoJSON(hash.get("coordinates"), hash.get("type")));
        return geo.build();
    }        
    
    public String getDatForm(Date d){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }
    
    public JsonObject toJSONDocument()
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        
        JsonObjectBuilder adresse = Json.createObjectBuilder()
                .add("code_insee",voie.commune.codeinsee)
                .add("code_departement",voie.commune.getCodeDepartement())
                .add("code_postal",voie.commune.codepostal);
        
        String code_arrondissement = voie.commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            adresse.add("code_arrondissement",code_arrondissement);
        adresse.add("commune",voie.commune.commune);
        if (voie.commune.com_code_insee_commune!=null)
            adresse.add("code_insee_commune",voie.commune.com_code_insee_commune);
        adresse.add("type_de_voie",voie.typedevoie);
        adresse.add("libelle",voie.libelle);
        adresse.add("voi_id",voie.idvoie);
        adresse.add("fullName",toString().trim());
//        adresse.add("fullName_sansngram",toString().trim());
        adresse.add("ligne4",toLigne4().trim());
        adresse.add("ligne6",toLigne6().trim());
        adresse.add("ligne7",toLigne7().trim());
        if (numero!=null)
        {
            adresse.add("numero",numero); // need a boost ?
        }
        if (repetition!=null)
            adresse.add("repetition",repetition);
         adresse.add("t0" , getDatForm(t0));
         adresse.add("t1" , getDatForm(t1));
        adresse.add("geometrie" , geometrieJSON(geometrie));  
//        JsonArray coordinates = Json.createArrayBuilder()
//                .add(lat)
//                .add(lon)
//                .build();
//        
//        JsonObject point = Json.createObjectBuilder()
//                .add("type","point")
//                .add("coordinates", coordinates)
//                .build();
//        adresse.add("geometrie",point);
        
        builder.add("adresse", adresse);
        
        return builder.build();
    }
    
    public boolean equals(Adresse a)
    {
        if (!this.batiment.equals(a.batiment)) return false;
        if (!this.idtroncon.equals(a.idtroncon)) return false;
        if (!this.numero.equals(a.numero)) return false;
        if (!this.repetition.equals(a.repetition)) return false;
        if (!this.voie.equals(a.voie)) return false;
        return true;
    }
    


//    private void setXY(String string)
//    {
//        string = string.substring(6,string.length()-1);
//        
//        String[] xy = string.split(" ");
//        
//        lat = Float.parseFloat(xy[0]);
//        lon = Float.parseFloat(xy[1]);
//    }
    

}