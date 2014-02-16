package jdonref_es_poc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Julien
 */
public class Voie
{

    public Voie(int min_numero, int max_numero, String idvoie, String typedevoie, String article, String libelle, Commune commune) {
        this.min_numero = min_numero;
        this.max_numero = max_numero;
        this.idvoie = idvoie;
        this.typedevoie = typedevoie;
        this.article = article;
        this.libelle = libelle;
        this.commune = commune;
    }
    
    
    public Voie()
    {
        
    }
    
    public Voie(ResultSet rs,int[] index,int[] indexcommune) throws SQLException
    {
        commune = new Commune(rs,indexcommune);
        idvoie = rs.getString(index[0]);
        typedevoie = rs.getString(index[1]);
        libelle = rs.getString(index[2]);
        article = getArticle(rs.getString(index[3]), typedevoie,libelle);
        min_numero = rs.getInt(index[4]);
        max_numero = rs.getInt(index[5]);
    }
    
    public Voie(ResultSet rs) throws SQLException
    {
        commune = new Commune(rs,new int[]{3,4,9,10});
        idvoie = rs.getString(1);
        typedevoie = rs.getString(5);
        libelle = rs.getString(6);
        article = getArticle(rs.getString(2), typedevoie,libelle);
        min_numero = rs.getInt(7);
        max_numero = rs.getInt(8);
    }
    
    public String[] getLignes()
    {
        String[] lignes = commune.getLignes();
        lignes[3] = typedevoie+" "+article+" "+libelle;
        return lignes;
    }
    
    public String getArticle(String nom, String typedevoie,String libelle)
    {
        nom = nom.substring(typedevoie.length()).trim();
        int index = nom.indexOf(libelle);
        if (index==0) return "";
        if (index==-1)
        {
            return nom.trim();
        }
        return nom.substring(0,index-1).trim();
    }
    
    public int min_numero;
    public int max_numero;
    
    public String idvoie;
    public String typedevoie;
    public String article;
    public String libelle;
    
    public Commune commune;
    
    public boolean equals(Voie v)
    {
        if (this.min_numero != v.min_numero) return false;
        if (this.max_numero != v.max_numero) return false;
        if (!this.idvoie.equals(v.idvoie)) return false;
        if (!this.typedevoie.equals(v.typedevoie)) return false;
        if (!this.article.equals(v.article)) return false;
        if (!this.libelle.equals(v.libelle)) return false;
        if (!this.commune.equals(v.commune)) return false;
        return true;
    }
    
    public String toString()
    {
        String arrondissement = commune.getCodeArrondissement();
        
        return (typedevoie==null?"":(typedevoie+" "))+(article==null?"":(article+" "))+libelle+ " "+ commune.codepostal+ " "+ commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    public String toFullString()
    {
        return (typedevoie==null?"":(typedevoie+" "))+(article==null?"":(article+" "))+libelle+" "+commune;
    }
    
    public JsonObject toJSONDocument()
    {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("toString", toString())
                .add("code_insee",commune.codeinsee)
                .add("code_departement",commune.getCodeDepartement())
                .add("code_postal",commune.codepostal);
        
        String code_arrondissement = commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            builder.add("code_arrondissement",code_arrondissement);
        builder.add("com_nom",commune.commune);
        builder.add("type_de_voie",typedevoie);
        builder.add("libelle",libelle);
        builder.add("voi_min_numero",min_numero);
        builder.add("voi_max_numero",max_numero);
        builder.add("voi_id",idvoie);
        builder.add("fullName",toFullString());
        return builder.build();
    }
}