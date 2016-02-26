package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
        
        voiAdr = new VoieAdr(voie, Integer.parseInt(numero),repetition);
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
        
        voiAdr = new VoieAdr(voie, Integer.parseInt(numero),repetition);
    }   
    
//    public Adresse(ResultSet rs) throws SQLException
//    {
//        voie = new Voie(rs,new int[]{10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27},new int[]{1,2,3,4,5,6,7,8,9,15});
//        
//        this.idadresse = rs.getString(28);
//        this.repetition = rs.getString(29);
//        this.numero = rs.getString(30);
//        if (numero.equals("0") || numero.trim().equals(""))
//            this.numero = null;
//        if (repetition.equals("0") || repetition.trim().equals(""))
//        {
//            this.repetition = null;
//        }
//      t0 = rs.getTimestamp(31);
//      t1 = rs.getTimestamp(32);
//      geometrie = rs.getString(33);          
////        setXY(rs.getString(33)); 
//    }
    
    public Adresse(ResultSet rs) throws SQLException
    {
        int nbColumnUnknown = 0;
        voie = new Voie(rs);
        ResultSetMetaData metaData = rs.getMetaData();
        int nbColumn = metaData.getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            String nomColonne = metaData.getColumnLabel(i+1); 
            switch(nomColonne){
                case "adr_id" : idadresse = rs.getString(nomColonne); break;
                case "adr_rep" : repetition = rs.getString(nomColonne); break;
                case "adr_numero" : numero = rs.getString(nomColonne); break;
                case "adr_t0" : t0 = rs.getTimestamp(nomColonne); break;
                case "adr_t1" : t1 = rs.getTimestamp(nomColonne); break;
                case "adr_geometrie" : geometrie = rs.getString(nomColonne); break;
//                case "adr_geometrie" : geometrie = setXY(rs.getString(nomColonne)); break;
                default: nbColumnUnknown = nbColumnUnknown+1;
            }
        }
        if (numero.equals("0") || numero.trim().equals(""))
            this.numero = null;
        if (repetition.equals("0") || repetition.trim().equals(""))
            this.repetition = null;
        
        
        voiAdr = new VoieAdr(voie, numero==null?0:Integer.parseInt(numero),repetition);
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
    public VoieAdr voiAdr;
    
    public String toLigne4()
    {
        return numero+" "+((repetition==null||repetition.endsWith("0"))?"":(repetition+" "))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle;
    }
    
    public String toLigne6()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
        
        return voie.commune.codepostal+ " "+ voie.commune.commune+(arrondissement==null?"":(" "+arrondissement));
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
        HashMap<String,String> hash = GeomUtil.toHashGeo(geometrie);
        JsonObjectBuilder geo = Json.createObjectBuilder()  
                .add("type", hash.get("type"))
                .add("coordinates", GeomUtil.toGeojson(hash.get("coordinates"), hash.get("type")));
        return geo.build();
    }        
    
    public String getDatForm(Date d){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }
    
    public JsonObject toJSONDocument(boolean withGeometry)
    {
        JsonObjectBuilder adresse = Json.createObjectBuilder();
        
        adresse.add("adr_id",idadresse);
//        adresse.add("tro_id",troncon.tro_id);
        adresse.add("voi_id",voie.idvoie);
        //adresse.add("voi_nom",voie.voi_nom);
        adresse.add("code_insee",voie.commune.codeinsee);
        if (voie.commune.com_code_insee_commune!=null)
            adresse.add("code_insee_commune",voie.commune.com_code_insee_commune);
        adresse.add("code_departement",voie.commune.getCodeDepartement());
        adresse.add("code_pays","FR1");
//        adresse.add("codes","");
        if (numero!=null)
            adresse.add("numero",numero); // need a boost ?
        if (repetition!=null)
            adresse.add("repetition",repetition); 
        adresse.add("type_de_voie",voie.typedevoie);
        if (voie.article!=null)
            adresse.add("article",voie.article);
        adresse.add("libelle",voie.libelle);
        adresse.add("commune",voie.commune.commune);
        String code_arrondissement = voie.commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            adresse.add("code_arrondissement",code_arrondissement);
        adresse.add("code_postal",voie.commune.codepostal);
        adresse.add("pays","FRANCE");
        adresse.add("t0" , getDatForm(t0));
        adresse.add("t1" , getDatForm(t1));
        //adresse.add("ligne2","");
        //adresse.add("ligne3","");
        adresse.add("ligne4",toLigne4().trim());
        //adresse.add("ligne5","");
        adresse.add("ligne6",toLigne6().trim());
        adresse.add("ligne7",toLigne7().trim());
        adresse.add("type","adresse");
        if (withGeometry)
            adresse.add("geometrie" , geometrieJSON(geometrie));
        adresse.add("pin" , geometrieJSON(geometrie));
        
//        LongArrayList fullNameTerms = FrequentTermsUtil.getMostFrequentTerms(toLigne4().trim()+" "+voie.commune.codeinsee+ " "+voie.commune.commune);
        
//        if (fullNameTerms.size()>1)
        {
            //CollectionUtils.sort(fullNameTerms);
        
        
            //ArrayList<String> allTerms = FrequentTermsUtil.genereMostFrequentTerms(toLigne4().trim()+" "+voie.commune.dpt_code_departement+" "+voie.commune.codeinsee+ " "+voie.commune.commune,2,5,1);
            //if (allTerms.size()>0)
            //    adresse.add("mostFrequentTerms",FrequentTermsUtil.cumul(allTerms));
            
            
        }
        
        //adresse.add("fullName",toString().trim());

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
        
        
        return adresse.build();
    }
    
    
        public JsonObject toJSONDocumentNested(JsonArrayBuilder ad)
    {
        JsonObjectBuilder adresse = Json.createObjectBuilder();
        adresse.add("adresses",ad);
        adresse.add("code_departement",voie.commune.getCodeDepartement());
        adresse.add("code_pays","FR1");
        adresse.add("type_de_voie",voie.typedevoie);
        if (voie.article!=null)
            adresse.add("article",voie.article);
        adresse.add("libelle",voie.libelle);
        adresse.add("voi_nom",voie.voi_nom);
        adresse.add("pays","FRANCE");
        adresse.add("ligne7",toLigne7().trim());
//        adresse.add("type","adresse");
       
        
        return adresse.build();
    }
    
    public void toJSONDocumentNestedAdd(JsonArrayBuilder ad, boolean withGeometry)
    {
        JsonObjectBuilder adresse = Json.createObjectBuilder();
        adresse.add("adr_id",idadresse);
        adresse.add("voi_id",voie.idvoie);
        adresse.add("code_insee",voie.commune.codeinsee);
        if (voie.commune.com_code_insee_commune!=null)
            adresse.add("code_insee_commune",voie.commune.com_code_insee_commune);
        if (numero!=null)
                adresse.add("numero",numero); // need a boost ?
        if (repetition!=null)
            adresse.add("repetition",repetition); 
        adresse.add("commune",voie.commune.commune);
        String code_arrondissement = voie.commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            adresse.add("code_arrondissement",code_arrondissement);
        adresse.add("code_postal",voie.commune.codepostal);
        adresse.add("t0" , getDatForm(t0));
        adresse.add("t1" , getDatForm(t1));
        adresse.add("ligne4",toLigne4().trim());
        adresse.add("ligne6",toLigne6().trim());
//        adresse.add("type","voie");
        if (withGeometry)
            adresse.add("geometrie" , geometrieJSON(geometrie));
        adresse.add("pin" , geometrieJSON(geometrie));

        ad.add(adresse);
    }
    public void toJSONDocumentNestedAddWithoutNum(JsonArrayBuilder ad, boolean withGeometry)
    {
        //modifier les champs renseigner sans num : exemple idadresse pas coherent de le mettre ..
        
        JsonObjectBuilder adresse = Json.createObjectBuilder();
        adresse.add("adr_id",idadresse);
        adresse.add("voi_id",voie.idvoie);
        adresse.add("code_insee",voie.commune.codeinsee);
        if (voie.commune.com_code_insee_commune!=null)
            adresse.add("code_insee_commune",voie.commune.com_code_insee_commune);
        adresse.add("numero","");
        if (repetition!=null)
            adresse.add("repetition",repetition); 
        adresse.add("commune",voie.commune.commune);
        String code_arrondissement = voie.commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            adresse.add("code_arrondissement",code_arrondissement);
        adresse.add("code_postal",voie.commune.codepostal);
        adresse.add("t0" , getDatForm(t0));
        adresse.add("t1" , getDatForm(t1));
        adresse.add("ligne4",voie.voi_nom);
        adresse.add("ligne6",toLigne6().trim());
//        adresse.add("type","voie");   
        if (withGeometry)
            adresse.add("geometrie" , geometrieJSON(geometrie));
        adresse.add("pin" , geometrieJSON(geometrie));

        ad.add(adresse);
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