package jdonref_lucene_poc.tests;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdonref_lucene_poc.business.AdresseBusiness;
import jdonref_lucene_poc.entity.Commune;
import jdonref_lucene_poc.entity.Departement;
import jdonref_lucene_poc.index.JDONREFIndex;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.junit.Test;

/**
 *
 * @author Julien
 */
public class Valide
{
    public void initDatabase() throws SQLException
    {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.22:5432/jdonref-navteq-bspp","postgres","postgres");
        Statement st = connection.createStatement();
        System.out.println("CREATE DATABASE jdonref");
        st.execute("CREATE DATABASE jdonref;");
        st.execute("");
    }
    
    public void purgeDatabase() throws SQLException
    {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.22:5432/jdonref-navteq-bspp","postgres","postgres");
        Statement st = connection.createStatement();
        System.out.println("DROP DATABASE jdonref");
        st.execute("DROP DATABASE jdonref");
    }
    
    public Departement[] getDepartements()
    {
        Departement[] d = new Departement[2];
        
        int i=0;
        d[i++] = new Departement("75");
        d[i++] = new Departement("93");
        
        return d;
    }
    
    public Commune[] getCommunes()
    {
        Commune[] c = new Commune[4];
        
        int i=0;
        c[i++] = new Commune("PARIS","75056","75000",null);
        c[i++] = new Commune("PARIS","75105","75005","75056");
        c[i++] = new Commune("BOBIGNY","93000","93000",null);
        c[i++] = new Commune("PARIS","75113","75013","75056");
        
        return c;
    }
    
    public jdonref_lucene_poc.entity.Voie[] getVoies()
    {
        Commune[] c = getCommunes();
        
        jdonref_lucene_poc.entity.Voie[] v = new jdonref_lucene_poc.entity.Voie[5];
        
        int i=0;
        v[i++] = new jdonref_lucene_poc.entity.Voie(1,25,"111","BOULEVARD","DE","L HOPITAL",c[1]);
        v[i++] = new jdonref_lucene_poc.entity.Voie(26,114,"112","BOULEVARD","DE","L HOPITAL",c[3]);
        v[i++] = new jdonref_lucene_poc.entity.Voie(1,100,"113","AVENUE","","PAUL ELUARD",c[2]);
        v[i++] = new jdonref_lucene_poc.entity.Voie(1,100,"113","RUE","DE","L HOPITAL SAINT LOUIS",c[3]);
        v[i++] = new jdonref_lucene_poc.entity.Voie(1,100,"113","BD","DE","PARIS",c[2]);
        
        return v;
    }
    
    public Directory getIndex(boolean bouchon,boolean reindex,boolean verboseIndexation) throws SQLException, IOException
    {
        JDONREFIndex jdonrefIndex = new JDONREFIndex();
        jdonrefIndex.setVerbose(verboseIndexation);
        if (reindex)
        {
            if (bouchon)
            {
                jdonrefIndex.setBouchon(true);
                jdonrefIndex.setDepartements(getDepartements());
                jdonrefIndex.setCommunes(getCommunes());
                jdonrefIndex.setVoies(getVoies());
                
                jdonrefIndex.reindex();
            }
            else
            {
                Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.22:5432/jdonref-navteq-bspp","postgres","postgres");
                jdonrefIndex.setConnection(connection);
                jdonrefIndex.reindex();
            }
        }
        return jdonrefIndex.openInRAMIndex();
    }
    
    @Test
    public void valideTestsAfterIndexation() throws ParseException, SQLException, org.apache.lucene.queryparser.classic.ParseException
    {
        boolean bouchon = true;
        boolean reindex = true;
        boolean verboseIndexation = true;
    
        try {
            Directory index = getIndex(bouchon,reindex,verboseIndexation);
            
            AdresseBusiness adresseBO = new AdresseBusiness(index);
            adresseBO.setHitsPerPage(5);
            adresseBO.setLimit(1.0f);
            
            Document[] hits;
//            hits = adresseBO.valide("BOULEVARD DE L HOPITAL 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !

            hits = adresseBO.valide("hopital paris"); // problème : propose les numéros plutôt que hopital saint louis !
            hits = adresseBO.valide("15 hopital paris"); // problème : propose les numéros plutôt que hopital saint louis !
            hits = adresseBO.valide("15 hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !
            hits = adresseBO.valide("75 hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !

            hits = adresseBO.valide("bd hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !
            hits = adresseBO.valide("bd hapitol 75 paris"); // problème : propose les arrondissements !
            hits = adresseBO.valide("24 bd hapitol 75 paris"); // problème : propose la route 24 !
            hits = adresseBO.valide("24 bd hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !
            hits = adresseBO.valide("75 paris");
            hits = adresseBO.valide("75"); // problème : uniquement en recherche approchée !
            hits = adresseBO.valide("93"); // problème : uniquement en recherche approchée !

        } catch (IOException ex) {
            Logger.getLogger(Valide.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}