package jdonref_lucene_poc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

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
    
    public Adresse(ResultSet rs, String numero, String repetition) throws SQLException
    {
        voie = new Voie(rs,new int[]{1,5,6,2,7,8},new int[]{3,4,9,10});
        
        this.numero = numero;
        this.repetition = repetition;
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
    
    public Document toDocument()
    {
        Document doc = new Document();
        
        doc.add(new TextField("toString",toString(),Field.Store.YES));
        doc.add(new TextField("code_insee",voie.commune.codeinsee,Field.Store.YES));
        doc.add(new TextField("code_departement",voie.commune.getCodeDepartement(),Field.Store.YES));
        doc.add(new TextField("code_postal",voie.commune.codepostal,Field.Store.YES));
        String code_arrondissement = voie.commune.getCodeArrondissement();
        if (code_arrondissement!=null)
            doc.add(new TextField("code_arrondissement",code_arrondissement,Field.Store.YES));
        doc.add(new StringField("com_nom",voie.commune.commune,Field.Store.YES));
        
        doc.add(new StringField("type_de_voie",voie.typedevoie,Field.Store.YES));
        doc.add(new StringField("libelle",voie.libelle,Field.Store.YES));
        doc.add(new IntField("voi_min_numero",voie.min_numero, Field.Store.YES));
        doc.add(new IntField("voi_max_numero",voie.max_numero, Field.Store.YES));
        doc.add(new StringField("voi_id",voie.idvoie,Field.Store.YES));
        
        doc.add(new TextField("fullName",toFullString(),Field.Store.YES));
        /*TextField numeroTF = new TextField("fullName",numero,Field.Store.YES);
        numeroTF.setBoost(1f);
        doc.add(numeroTF);*/
        /*doc.add(new TextField("fullName",toFullString1(),Field.Store.YES));
        doc.add(new TextField("fullName",toFullString2(),Field.Store.YES));
        doc.add(new TextField("fullName",toFullString3(),Field.Store.YES));*/
        
        TextField numeroSF = new TextField("numero",numero,Field.Store.YES);
        numeroSF.setBoost(1f);
        doc.add(numeroSF);
        doc.add(new StringField("repetition",repetition,Field.Store.YES));
        return doc;
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