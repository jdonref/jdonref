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
public class Voie
{
    public Commune commune;
    public String idvoie; //voi_id
    public String voi_code_fantoir;
    public String voi_nom;
    public String voi_nom_desab;
    public String voi_nom_origine;
    public String cdp_code_postal;    
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
    public String article;
    
    public void setGeometrie(String geometrie) {
        this.geometrie = "{\"type\":\"MultiLineString\",\"coordinates\":"+geometrie+"}";
    }

    public Voie(Commune commune, String idvoie, String voi_code_fantoir, String voi_nom, String voi_nom_desab, String voi_nom_origine, String cdp_code_postal, String typedevoie, String voi_type_de_voie_pq, String libelle, String voi_lbl_pq, String voi_lbl_sans_articles, String voi_lbl_sans_articles_pq, String voi_mot_determinant, String voi_mot_determinant_pq, int min_numero, int max_numero, Date t0, Date t1, String article) {
        this.commune = commune;
        this.idvoie = idvoie;
        this.voi_code_fantoir = voi_code_fantoir;
        this.voi_nom = voi_nom;
        this.voi_nom_desab = voi_nom_desab;
        this.voi_nom_origine = voi_nom_origine;
        this.cdp_code_postal = cdp_code_postal;
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
        this.t0 = t0;
        this.t1 = t1;
//        this.geometrie = geometrie;
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
        cdp_code_postal = rs.getString(index[5]);
        typedevoie = rs.getString(index[6]); 
        voi_type_de_voie_pq = rs.getString(index[7]);
        libelle = rs.getString(index[8]); 
        voi_lbl_pq = rs.getString(index[9]);
        voi_lbl_sans_articles = rs.getString(index[10]);
        voi_lbl_sans_articles_pq = rs.getString(index[11]);
        voi_mot_determinant = rs.getString(index[12]);
        voi_mot_determinant_pq = rs.getString(index[13]);
        min_numero = rs.getInt(index[14]); 
        max_numero = rs.getInt(index[15]); 
        t0 = rs.getTimestamp(index[16]);
        t1 = rs.getTimestamp(index[17]);
        article = getArticle(rs.getString(index[3]), typedevoie,libelle);
        
    }
    
    public Voie(ResultSet rs) throws SQLException
    {
//        commune = new Commune(rs,new int[]{3,4,9,10});
        commune = new Commune(rs,new int[]{1,2,3,4,5,6,7,8,9,15});    
        idvoie = rs.getString(10);
        voi_code_fantoir = rs.getString(11);
        voi_nom = rs.getString(12);
        voi_nom_desab = rs.getString(13);
        voi_nom_origine = rs.getString(14);
        cdp_code_postal = rs.getString(15);
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
        t0 = rs.getTimestamp(26);
        t1 = rs.getTimestamp(27);
        article = getArticle(rs.getString(13), typedevoie,libelle);
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
        if (index==0) return null;
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
        GeomUtil geomUtil = new GeomUtil();
        HashMap<String,String> hash = geomUtil.toHashGeo(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()  
                .add("type", hash.get("type"))
                .add("coordinates", geomUtil.toGeojson(hash.get("coordinates"), hash.get("type")));
        return geo.build();
    }       
    
    public String getDatForm(Date d){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }    
    
    public JsonObject toJSONDocument()
    {
        JsonObjectBuilder voie = Json.createObjectBuilder();
        
        voie.add("voi_id", idvoie);    
         voie.add("code_insee",commune.codeinsee);
         if (commune.com_code_insee_commune!=null)
         voie.add("code_insee_commune",commune.com_code_insee_commune);
         voie.add("code_departement",commune.dpt_code_departement);
         voie.add("code_pays","FR1");
//         voie.add("codes","");
         voie.add("numero_min",min_numero);
         voie.add("numero_max",max_numero);
         voie.add("type_de_voie",typedevoie);
         if (article!=null)
            voie.add("article",article);
         voie.add("libelle",libelle);
         voie.add("commune",commune.commune);       
         String code_arrondissement = commune.getCodeArrondissement();
         if (code_arrondissement!=null)
            voie.add("code_arrondissement",code_arrondissement);
         voie.add("code_postal",cdp_code_postal);
         voie.add("pays","FRANCE");
         voie.add("t0" , getDatForm(t0));
         voie.add("t1" , getDatForm(t1));
         voie.add("ligne4",toLigne4().trim());
         voie.add("ligne5","ligne 5");
         voie.add("ligne6",toLigne6().trim());
         voie.add("ligne7",toLigne7().trim());
         voie.add("type","voie");
         voie.add("geometrie" , geometrieJSON(geometrie));
         voie.add("fullName",toString().trim());
//         voie.add("fullName_sansngram",toString().trim());
     
        return voie.build();
    }
}