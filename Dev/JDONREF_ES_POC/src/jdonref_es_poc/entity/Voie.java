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
    
    public String article;

    public Voie(Commune commune, String idvoie, String voi_code_fantoir, String voi_nom, String voi_nom_desab, String voi_nom_origine, String typedevoie, String voi_type_de_voie_pq, String libelle, String voi_lbl_pq, String voi_lbl_sans_articles, String voi_lbl_sans_articles_pq, String voi_mot_determinant, String voi_mot_determinant_pq, int min_numero, int max_numero, String article) {
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
        
        JsonObjectBuilder builder = Json.createObjectBuilder();
         builder.add("code_insee",commune.codeinsee);
//         builder.add("code_departement",getCodeDepartement());
         builder.add("code_departement",commune.dpt_code_departement);
         builder.add("code_postal",commune.codepostal);
         String code_arrondissement = commune.getCodeArrondissement();
          if (code_arrondissement!=null)
            builder.add("code_arrondissement",code_arrondissement);
         builder.add("com_nom",commune.commune);
         builder.add("com_nom_desab",commune.com_nom_desab);
         builder.add("com_nom_origine",commune.com_nom_origine);
         builder.add("com_nom_pq",commune.com_nom_pq);
         if (commune.com_code_insee_commune!=null)
         builder.add("com_code_insee_commune",commune.com_code_insee_commune);
         builder.add("t0",commune.t0.toString());
         builder.add("t1",commune.t1.toString());

//        if (idvoie!=null)
         builder.add("voi_id", idvoie);
//        if (voi_code_fantoir!=null)
         builder.add("voi_code_fantoir",voi_code_fantoir);
        if (voi_nom!=null)
         builder.add("voi_nom",voi_nom);
        if (voi_nom_desab!=null)
         builder.add("voi_nom_desab",voi_nom_desab);
        if (voi_nom_origine!=null)
         builder.add("voi_nom_origine",voi_nom_origine);
        if (typedevoie!=null)
         builder.add("voi_type_de_voie",typedevoie);
        if (voi_type_de_voie_pq!=null)
         builder.add("voi_type_de_voie_pq",voi_type_de_voie_pq);
        if (libelle!=null)
         builder.add("voi_lbl",libelle);
        if (voi_lbl_pq!=null)
         builder.add("voi_lbl_pq",voi_lbl_pq);
        if (voi_lbl_sans_articles!=null)
         builder.add("voi_lbl_sans_articles",voi_lbl_sans_articles);
        if (voi_lbl_sans_articles_pq!=null)
         builder.add("voi_lbl_sans_articles_pq",voi_lbl_sans_articles_pq);
        if (voi_mot_determinant!=null)
         builder.add("voi_mot_determinant",voi_mot_determinant);
        if (voi_mot_determinant_pq!=null)
         builder.add("voi_mot_determinant_pq",voi_mot_determinant_pq);
         builder.add("voi_min_numero",min_numero);
         builder.add("voi_max_numero",max_numero);
         
        return builder.build();
        
//        JsonObjectBuilder builder = Json.createObjectBuilder()
//                .add("toString", toString())
//                .add("code_insee",commune.codeinsee)
//                .add("code_departement",commune.getCodeDepartement())
//                .add("code_postal",commune.codepostal);
//        
//        String code_arrondissement = commune.getCodeArrondissement();
//        if (code_arrondissement!=null)
//            builder.add("code_arrondissement",code_arrondissement);
//        builder.add("com_nom",commune.commune);
//        builder.add("type_de_voie",typedevoie);
//        builder.add("libelle",libelle);
//        builder.add("voi_min_numero",min_numero);
//        builder.add("voi_max_numero",max_numero);
//        builder.add("voi_id",idvoie);
//        builder.add("fullName",toFullString());
//        return builder.build();

    }
}