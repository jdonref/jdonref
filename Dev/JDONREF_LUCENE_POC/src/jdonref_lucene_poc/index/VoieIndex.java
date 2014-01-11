package jdonref_lucene_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdonref_lucene_poc.dao.VoieDAO;
import jdonref_lucene_poc.entity.Voie;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author Julien
 */
public class VoieIndex {
     boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void addVoie(IndexWriter w,Voie v)
            throws IOException
    {
        Document d = v.toDocument();
        
        w.addDocument(d);
    }

    public void indexJDONREFVoiesDepartement(IndexWriter writer,Connection connection, String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        VoieDAO dao = new VoieDAO();
        ResultSet rs = dao.getAllVoiesOfDepartement(connection, dpt);
        
        int c =0;
        
        while(rs.next())
        {
            if (isVerbose() && c++%1000==0)
                System.out.println(c+" voies traitées");
            
            Voie v = new Voie(rs);
            
            addVoie(writer, v);
        }
    }

    void indexJDONREFVoiesDepartement(IndexWriter w, Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        int c =0;
        
        for(int i=0;i<voies.length;i++)
        {
            if (isVerbose() && c++%1000==0)
                System.out.println(c+" voies traitées");
            
            Voie v = voies[i];
            
            addVoie(w, v);
        }
    }
}