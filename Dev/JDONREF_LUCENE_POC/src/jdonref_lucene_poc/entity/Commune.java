package jdonref_lucene_poc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 *
 * @author Julien
 */
public class Commune
{

    public Commune(String commune, String codeinsee, String codepostal, String com_code_insee_commune) {
        this.commune = commune;
        this.codeinsee = codeinsee;
        this.codepostal = codepostal;
        this.com_code_insee_commune = com_code_insee_commune;
    }

    public Commune(ResultSet rs,int[] index) throws SQLException
    {
        codeinsee = rs.getString(index[0]);
        codepostal = rs.getString(index[1]);
        commune = rs.getString(index[2]);
        com_code_insee_commune = rs.getString(index[3]);
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
        return codepostal.substring(0,2);
    }
    
    public String commune;
    public String codeinsee;
    public String codepostal;
    
    public String com_code_insee_commune;
    
    public boolean equals(Commune commune)
    {
        if (!this.commune.equals(commune.commune)) return false;
        if (!this.codeinsee.equals(commune.codeinsee)) return false;
        if (!this.codepostal.equals(commune.codepostal)) return false;
        if (!this.com_code_insee_commune.equals(commune.com_code_insee_commune)) return false;
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
    
    public Document toDocument()
    {
        Document doc = new Document();
        
        doc.add(new TextField("toString",toString(),Field.Store.YES));
        doc.add(new TextField("fullName",toFullString(),Field.Store.YES));
        /*doc.add(new TextField("fullName",toFullString1(),Field.Store.YES));
        doc.add(new TextField("fullName",toFullString1(),Field.Store.YES));
        doc.add(new TextField("fullName",toFullString1(),Field.Store.YES));*/
        doc.add(new TextField("code_insee",codeinsee,Field.Store.YES));
        doc.add(new TextField("code_departement",getCodeDepartement(),Field.Store.YES));
        doc.add(new TextField("code_postal",codepostal,Field.Store.YES));
        String code_arrondissement = getCodeArrondissement();
        if (code_arrondissement!=null)
            doc.add(new StringField("code_arrondissement",code_arrondissement,Field.Store.YES));
        doc.add(new StringField("com_nom",commune,Field.Store.YES));
        
        return doc;
    }
}