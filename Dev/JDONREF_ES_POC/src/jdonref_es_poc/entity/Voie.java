package jdonref_es_poc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import jdonref_es_poc.entity.GeometrieUtil;
import jdonref_es_poc.entity.GeometrieUtil;
import org.elasticsearch.common.mvel2.optimizers.impl.refl.nodes.ThisValueAccessor;
import jdonref_es_poc.index.TronconIndex;


/**
 *
 * @author Julien
 */
public class Voie
{
    public Commune commune;
    public String idvoie; //voi_id
    public String voi_code_fantoir;
    public String voi_nom;
    public String voi_nom_desab;
    public String voi_nom_origine;
    public String typedevoie; //voi_type_de_voie
    public String voi_type_de_voie_pq;
    public String libelle; //voi_lbl
    public String voi_lbl_pq;
    public String voi_lbl_sans_articles;
    public String voi_lbl_sans_articles_pq;
    public String voi_mot_determinant;
    public String voi_mot_determinant_pq;
    public int min_numero; //voi_min_numero
    public int max_numero; //voi_max_numero
    public Date t0;
    public Date t1;
    public String geometrie;
    public String typeGeo="MULTILINESTRING";
    
    public void setGeometrie(String geometrie) {
        this.geometrie = typeGeo+geometrie;
    }
    
    public String article;
    
    
    

    public Voie(Commune commune, String idvoie, String voi_code_fantoir, String voi_nom, String voi_nom_desab, String voi_nom_origine, String typedevoie, String voi_type_de_voie_pq, String libelle, String voi_lbl_pq, String voi_lbl_sans_articles, String voi_lbl_sans_articles_pq, String voi_mot_determinant, String voi_mot_determinant_pq, int min_numero, int max_numero, String article, Date t0, Date t1) {
        this.commune = commune;
        this.idvoie = idvoie;
        this.voi_code_fantoir = voi_code_fantoir;
        this.voi_nom = voi_nom;
        this.voi_nom_desab = voi_nom_desab;
        this.voi_nom_origine = voi_nom_origine;
        this.typedevoie = typedevoie;
        this.voi_type_de_voie_pq = voi_type_de_voie_pq;
        this.libelle = libelle;
        this.voi_lbl_pq = voi_lbl_pq;
        this.voi_lbl_sans_articles = voi_lbl_sans_articles;
        this.voi_lbl_sans_articles_pq = voi_lbl_sans_articles_pq;
        this.voi_mot_determinant = voi_mot_determinant;
        this.voi_mot_determinant_pq = voi_mot_determinant_pq;
        this.min_numero = min_numero;
        this.max_numero = max_numero;
        this.article = article;
        this.t0=t0;
        this.t1=t1;

    }

    public Voie()
    {
        
    }
    
    public Voie(ResultSet rs,int[] index,int[] indexcommune) throws SQLException
    {
        commune = new Commune(rs,indexcommune);
        idvoie = rs.getString(index[0]);
        voi_code_fantoir = rs.getString(index[1]);
        voi_nom = rs.getString(index[2]);
        voi_nom_desab = rs.getString(index[3]);
        voi_nom_origine = rs.getString(index[4]);
        typedevoie = rs.getString(index[5]); 
        voi_type_de_voie_pq = rs.getString(index[6]);
        libelle = rs.getString(index[7]); 
        voi_lbl_pq = rs.getString(index[8]);
        voi_lbl_sans_articles = rs.getString(index[9]);
        voi_lbl_sans_articles_pq = rs.getString(index[10]);
        voi_mot_determinant = rs.getString(index[11]);
        voi_mot_determinant_pq = rs.getString(index[12]);
        min_numero = rs.getInt(index[13]); 
        max_numero = rs.getInt(index[14]); 
        article = getArticle(rs.getString(index[3]), typedevoie,libelle);
        t0 = rs.getTimestamp(index[15]);
        t1 = rs.getTimestamp(index[16]);

    }
    
    public Voie(ResultSet rs) throws SQLException
    {
//        commune = new Commune(rs,new int[]{3,4,9,10});
        commune = new Commune(rs,new int[]{1,2,3,4,5,6,7,8,9,10});    
        idvoie = rs.getString(11);
        voi_code_fantoir = rs.getString(12);
        voi_nom = rs.getString(13);
        voi_nom_desab = rs.getString(14);
        voi_nom_origine = rs.getString(15);
        typedevoie = rs.getString(16); 
        voi_type_de_voie_pq = rs.getString(17);
        libelle = rs.getString(18); 
        voi_lbl_pq = rs.getString(19);
        voi_lbl_sans_articles = rs.getString(20);
        voi_lbl_sans_articles_pq = rs.getString(21);
        voi_mot_determinant = rs.getString(22);
        voi_mot_determinant_pq = rs.getString(23);
        min_numero = rs.getInt(24); 
        max_numero = rs.getInt(25); 
        article = getArticle(rs.getString(14), typedevoie,libelle);
        t0 = rs.getTimestamp(26);
        t1 = rs.getTimestamp(27);
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
    

    
    public boolean equals(Voie v)
    {   
    if (this.commune.equals(v.commune)) return false;
    if (this.idvoie.equals(v.idvoie)) return false;
    if (this.voi_code_fantoir.equals(v.voi_code_fantoir)) return false;
    if (this.voi_nom.equals(v.voi_nom)) return false;
    if (this.voi_nom_desab.equals(v.voi_nom_desab)) return false;
    if (this.voi_nom_origine.equals(v.voi_nom_origine)) return false;
    if (this.typedevoie.equals(v.typedevoie)) return false;
    if (this.voi_type_de_voie_pq.equals(v.voi_type_de_voie_pq)) return false;
    if (this.libelle.equals(v.libelle)) return false;
    if (this.voi_lbl_pq.equals(v.voi_lbl_pq)) return false;
    if (this.voi_lbl_sans_articles.equals(v.voi_lbl_sans_articles)) return false;
    if (this.voi_lbl_sans_articles_pq.equals(v.voi_lbl_sans_articles_pq)) return false;
    if (this.voi_mot_determinant.equals(v.voi_mot_determinant)) return false;
    if (this.voi_mot_determinant_pq.equals(v.voi_mot_determinant_pq)) return false;
    if (this.min_numero!= v.min_numero) return false;
    if (this.max_numero!= v.max_numero) return false;
    
    if (this.article.equals(v.article)) return false;
    
    return true;
    }
    
    public String toLigne4()
    {
        return (typedevoie==null?"":(typedevoie+" "))+(article==null?"":(article+" "))+libelle;
    }
    
    public String toLigne6()
    {
        String arrondissement = commune.getCodeArrondissement();
        
        return commune.codepostal+ " "+ commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public String toLigne7()
    {
        return "FRANCE";
    }
    
    public String toString()
    {
        String arrondissement = commune.getCodeArrondissement();
        
        return (typedevoie==null?"":(typedevoie+" "))+(article==null?"":(article+" "))+libelle+ " "+ commune.codepostal+ " "+ commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    
    public String toStringWithoutNumbers()
    {
        return (typedevoie==null?"":(typedevoie+" "))+(article==null?"":(article+" "))+libelle+ " "+  commune.commune;
    }

    
    public JsonObject geometrieJSON(String geometrie){
        GeometrieUtil geomUtil = new GeometrieUtil();
        String type = geomUtil.getGeoTYPE(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()
         .add("type", type.toLowerCase())
         .add("coordinates", geomUtil.getGeoJSON(geometrie, type));
        return geo.build();
    }
    
    
    public JsonObject toJSONDocument()
    {
        
        JsonObjectBuilder builder = Json.createObjectBuilder();
         builder.add("code_insee",commune.codeinsee);
         builder.add("code_departement",commune.dpt_code_departement);
         builder.add("code_postal",commune.codepostal);
         String code_arrondissement = commune.getCodeArrondissement();
         if (code_arrondissement!=null)
            builder.add("code_arrondissement",code_arrondissement);
         builder.add("commune",commune.commune);
         if (commune.com_code_insee_commune!=null)
            builder.add("code_insee_commune",commune.com_code_insee_commune);
         builder.add("commune.t0",commune.t0.getTime());
         builder.add("commune.t1",commune.t1.getTime());

         builder.add("voi_id", idvoie);
         builder.add("voi_code_fantoir",voi_code_fantoir);
         if (typedevoie!=null)
            builder.add("type_de_voie",typedevoie);
         if (libelle!=null)
            builder.add("voi_lbl",libelle);
         builder.add("numero_min",min_numero);
         builder.add("numero_max",max_numero);
         builder.add("t0" , t0.toString());
         builder.add("t1" , t1.toString());
         builder.add("geometrie" , geometrieJSON(geometrie));
         
         builder.add("fullName",toString().trim());
         builder.add("fullName_sansngram",toString().trim());
         builder.add("ligne4",toLigne4().trim());
         builder.add("ligne6",toLigne6().trim());
         builder.add("ligne7",toLigne7().trim());
         
        return builder.build();
    }
}