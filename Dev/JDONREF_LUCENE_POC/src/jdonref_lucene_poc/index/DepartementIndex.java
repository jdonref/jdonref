package jdonref_lucene_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdonref_lucene_poc.dao.DepartementDAO;
import jdonref_lucene_poc.entity.Departement;
import jdonref_lucene_poc.entity.Voie;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author Julien
 */
public class DepartementIndex
{
    boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void indexJDONREFDepartement(IndexWriter w,Voie[] voies,String dpt) throws IOException
    {
        VoieIndex vIndex = new VoieIndex();
        vIndex.setVerbose(isVerbose());
        vIndex.indexJDONREFVoiesDepartement(w, voies, dpt);
        
        TronconIndex tIndex = new TronconIndex();
        tIndex.setVerbose(isVerbose());
        tIndex.indexJDONREFTronconsDroitDepartement(w, voies, dpt);
        tIndex.indexJDONREFTronconsGaucheDepartement(w, voies, dpt);
    }
    
    public void indexJDONREFDepartement(IndexWriter w,Connection connection, String dpt) throws IOException, SQLException
    {
        VoieIndex vIndex = new VoieIndex();
        vIndex.setVerbose(isVerbose());
        vIndex.indexJDONREFVoiesDepartement(w, connection, dpt);
        
        TronconIndex tIndex = new TronconIndex();
        tIndex.setVerbose(isVerbose());
        tIndex.indexJDONREFTronconsDroitDepartement(w, connection, dpt);
        tIndex.indexJDONREFTronconsGaucheDepartement(w, connection, dpt);
    }
 
    public void indexJDONREFDepartements(IndexWriter w,Departement[] departements) throws IOException
    {
        if (isVerbose())
            System.out.println("Départements");
        
        for(int i=0;i<departements.length;i++)
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" départements traités");
            Departement d = departements[i];
            
            addDepartment(w,d);
        }
    }
    
    public void indexJDONREFDepartements(IndexWriter w,Connection connection) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Départements");
        
        DepartementDAO dao = new DepartementDAO();
        ResultSet rs = dao.getAllDepartement(connection);
        
        int i =0;
        while(rs.next())
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" départements traités");
            String dpt_code_departement = rs.getString(1);
            
            Departement d = new Departement();
            d.code_departement = dpt_code_departement;
            
            addDepartment(w,d);
        }
    }
    
    public void addDepartment(IndexWriter w,Departement departement) throws IOException
    {
        Document doc = new Document();
        
        doc.add(new TextField("toString",departement.code_departement,Field.Store.YES));
        doc.add(new TextField("fullName","",Field.Store.YES));
        
        doc.add(new TextField("code_departement",departement.code_departement,Field.Store.YES));
        doc.add(new StringField("nom",departement.code_departement,Field.Store.YES));
        
        w.addDocument(doc);
    }
}