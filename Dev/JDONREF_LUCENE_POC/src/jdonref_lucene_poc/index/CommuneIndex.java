package jdonref_lucene_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdonref_lucene_poc.dao.CommuneDAO;
import jdonref_lucene_poc.entity.Commune;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author Julien
 */
public class CommuneIndex
{
    boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }    
    public void addCommune(IndexWriter w,Commune commune) throws IOException
    {
        Document doc = commune.toDocument();
        
        w.addDocument(doc);
    }
    
    public void indexJDONREFCommune(IndexWriter w,Connection connection) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        CommuneDAO dao = new CommuneDAO();
        ResultSet rs = dao.getAllCommunes(connection);
        
        int i =0;
        while(rs.next())
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" communes traitées");
            
            Commune commune = new Commune(rs,new int[]{1,4,2,5});
            
            addCommune(w,commune);
        }
    }

    void indexJDONREFCommune(IndexWriter w, Commune[] communes) throws IOException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        for(int i=0;i<communes.length;i++)
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" communes traitées");
            
            Commune commune = communes[i];
            
            addCommune(w,commune);
        }
    }
}