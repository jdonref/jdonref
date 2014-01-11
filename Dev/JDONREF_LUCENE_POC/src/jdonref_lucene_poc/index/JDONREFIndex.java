/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_lucene_poc.index;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import jdonref_lucene_poc.entity.Commune;
import jdonref_lucene_poc.entity.Departement;
import jdonref_lucene_poc.entity.Voie;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Julien
 */
public class JDONREFIndex
{
    boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }
    
    Connection connection = null;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    boolean bouchon = false;

    public boolean isBouchon() {
        return bouchon;
    }

    public void setBouchon(boolean bouchon) {
        this.bouchon = bouchon;
    }
    
    Departement[] departements = null;
    Voie[] voies = null;
    Commune[] communes = null;

    public Commune[] getCommunes() {
        return communes;
    }

    public void setCommunes(Commune[] communes) {
        this.communes = communes;
    }

    public Departement[] getDepartements() {
        return departements;
    }

    public void setDepartements(Departement[] departements) {
        this.departements = departements;
    }

    public Voie[] getVoies() {
        return voies;
    }

    public void setVoies(Voie[] voies) {
        this.voies = voies;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }    
    
    public Directory openInRAMIndex() throws IOException
    {
        FSDirectory index = FSDirectory.open(new File("build"));
        Directory res = new RAMDirectory(index,new IOContext());
        
        return res;
    }
    
    public IndexWriter getIndexWriter() throws IOException
    {
        Analyzer analyzer = new FrenchAnalyzer(Version.LUCENE_45);
        Directory index = FSDirectory.open(new File("build"));    
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        
        return w;
    }
    
    /**
     * L'index sera vidé s'il existe déjà.
     * @return
     * @throws java.io.IOException
     */
    public IndexWriter getNewIndexWriter() throws IOException
    {
        IndexWriter w = getIndexWriter();
        w.deleteAll();
        
        return w;
    }
    
    public void reindex() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Démarrage de l'indexation");
        long start = Calendar.getInstance().getTimeInMillis();
        
        IndexWriter w = getNewIndexWriter();
        if (bouchon)
        {
            DepartementIndex dptIndex = new DepartementIndex();
            dptIndex.setVerbose(isVerbose());
            dptIndex.indexJDONREFDepartements(w, getDepartements());
            dptIndex.indexJDONREFDepartement(w, getVoies(), "75");
        
            CommuneIndex cIndex = new CommuneIndex();
            cIndex.setVerbose(isVerbose());
            cIndex.indexJDONREFCommune(w, getCommunes());
        }
        else
        {
            Connection connection = getConnection();
        
            DepartementIndex dptIndex = new DepartementIndex();
            dptIndex.setVerbose(isVerbose());
            dptIndex.indexJDONREFDepartements(w, connection);
            dptIndex.indexJDONREFDepartement(w, connection, "75");
            //dptIndex.indexJDONREFDepartement(w, connection, "92");
            //dptIndex.indexJDONREFDepartement(w, connection, "93");
            //dptIndex.indexJDONREFDepartement(w, connection, "94");
        
            CommuneIndex cIndex = new CommuneIndex();
            cIndex.setVerbose(isVerbose());
            cIndex.indexJDONREFCommune(w, connection);
        }
        w.close();
        
        long end = Calendar.getInstance().getTimeInMillis();
        
        if (isVerbose())
            System.out.println("Indexation complète en "+(end-start)+" millis.");
    }
}
