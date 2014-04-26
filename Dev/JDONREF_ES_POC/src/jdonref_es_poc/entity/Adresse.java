package jdonref_es_poc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    public Adresse(ResultSet rs,String numero, String repetition) throws SQLException
    {
        voie = new Voie(rs,new int[]{11,12,13,14,15,16,17,18,19,20,21,22,23,24,25},new int[]{1,2,3,4,5,6,7,8,9,10});
        this.numero = numero;
        this.repetition = repetition;
    }
    
    public Adresse(ResultSet rs) throws SQLException
    {
        voie = new Voie(rs,new int[]{11,12,13,14,15,16,17,18,19,20,21,22,23,24,25},new int[]{1,2,3,4,5,6,7,8,9,10});
        
        this.idadresse = rs.getString(26);
        this.numero = rs.getString(27);
        this.repetition = rs.getString(28);
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
    
    public Voie voie;
    
    public String toString()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
        
        return numero+" "+(repetition==null?"":(repetition+" "))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.codepostal+ " "+ voie.commune.commune+(arrondissement==null?"":(" "+arrondissement));
    }
    public String toFullString()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
        
        return (voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.commune;
    }
    public String toFullString1()
    {
        return numero+" "+(repetition==null?"":(" "+repetition))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.codeinsee + " "+ voie.commune.commune;
    }
    public String toFullString2()
    {
        return numero+" "+(repetition==null?"":(" "+repetition))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.codepostal + " "+ voie.commune.commune;
    }
    public String toFullString3()
    {
        String arrondissement = voie.commune.getCodeArrondissement();
        
        return numero+" "+(repetition==null?"":(" "+repetition))+(voie.typedevoie==null?"":(voie.typedevoie+" "))+(voie.article==null?"":(voie.article+" "))+voie.libelle+ " "+ voie.commune.getCodeDepartement() + " "+ voie.commune.commune +(arrondissement==null?"":(" "+arrondissement));
    }
    
    public JsonObject toJSONDocument()
    {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("toString", toString())
                .add("code_insee",voie.commune.codeinsee)
                .add("code_departement",voie.commune.getCodeDepartement())
                .add("code_postal",voie.commune.codepostal);
        
        String code_arrondissement = voie.commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            builder.add("code_arrondissement",code_arrondissement);
        builder.add("com_nom",voie.commune.commune);
        builder.add("type_de_voie",voie.typedevoie);
        builder.add("libelle",voie.libelle);
        builder.add("voi_min_numero",voie.min_numero);
        builder.add("voi_max_numero",voie.max_numero);
        builder.add("voi_id",voie.idvoie);
        builder.add("fullName",toFullString());
        builder.add("numero",numero); // need a boost ?
        builder.add("repetition",repetition);
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
}