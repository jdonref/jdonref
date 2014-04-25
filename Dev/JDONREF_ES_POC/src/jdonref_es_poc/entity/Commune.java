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
public class Commune
{
    
    public String codeinsee;
    public String dpt_code_departement;
    public String codepostal;
    public String commune;
    public String com_nom_desab;
    public String com_nom_origine;
    public String com_nom_pq;
    public String com_code_insee_commune;
    public Date t0;
    public Date t1;
    
    
//    public Commune(String commune, String codeinsee, String codepostal, String com_code_insee_commune) {
//        this.commune = commune;
//        this.codeinsee = codeinsee;
//        this.codepostal = codepostal;
//        this.com_code_insee_commune = com_code_insee_commune;
//    }

    public Commune(String codeinsee, String dpt_code_departement, String codepostal, String commune, String com_nom_desab, String com_nom_origine, String com_nom_pq, String com_code_insee_commune, Date t0, Date t1) {
        this.codeinsee = codeinsee;
        this.dpt_code_departement = dpt_code_departement;
        this.codepostal = codepostal;
        this.commune = commune;
        this.com_nom_desab = com_nom_desab;
        this.com_nom_origine = com_nom_origine;
        this.com_nom_pq = com_nom_pq;
        this.com_code_insee_commune = com_code_insee_commune;
        this.t0 = t0;
        this.t1 = t1;
    }

//    public Commune(ResultSet rs,int[] index) throws SQLException
//    {
//        codeinsee = rs.getString(index[0]);
//        codepostal = rs.getString(index[1]);
//        commune = rs.getString(index[2]);
//        com_code_insee_commune = rs.getString(index[3]);
//    }
    
    
    public Commune(ResultSet rs,int[] index) throws SQLException
    {
      codeinsee = rs.getString(index[0]);
      dpt_code_departement = rs.getString(index[1]);
      codepostal = rs.getString(index[2]);
      commune = rs.getString(index[3]);
      com_nom_desab = rs.getString(index[4]);
      com_nom_origine = rs.getString(index[5]);
      com_nom_pq = rs.getString(index[6]);
      com_code_insee_commune = rs.getString(index[7]);
      t0 = rs.getDate(index[8]);
      t1 = rs.getDate(index[9]);
        
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

        return true;
    }
    
    public String toString()
    {
        String arrondissement = getCodeArrondissement();
        
        return codepostal+ " "+ commune+(arrondissement==null?"":(" "+arrondissement));
    }
    public String toFullString()
    {
        String arrondissement = getCodeArrondissement();
        
        return commune;
    }

    public String toFullString1()
    {
        return codepostal+" "+commune;
    }

    public String toFullString2()
    {
        return codeinsee+" "+commune;
    }
    
    public String toFullString3()
    {
        String arrondissement = getCodeArrondissement();
        
        return commune;
    }
    
    public JsonObject toJSONDocument()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
//                .add("toString", toString());
//         builder.add("fullName",toFullString());
         builder.add("code_insee",codeinsee);
//         builder.add("code_departement",getCodeDepartement());
         builder.add("code_departement",dpt_code_departement);
         builder.add("code_postal",codepostal);
         String code_arrondissement = getCodeArrondissement();
          if (code_arrondissement!=null)
            builder.add("code_arrondissement",code_arrondissement);
         builder.add("com_nom",commune);
         builder.add("com_nom_desab",com_nom_desab);
         builder.add("com_nom_origine",com_nom_origine);
         builder.add("com_nom_pq",com_nom_pq);
         if (com_code_insee_commune!=null)
         builder.add("com_code_insee_commune",com_code_insee_commune);
         builder.add("t0",t0.toString());
         builder.add("t1",t1.toString());
        
        return builder.build();
    }
}