package jdonref_es_poc.jdonrefindextest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import jdonref_es_poc.business.AdresseBusiness;
import jdonref_es_poc.entity.Commune;
import jdonref_es_poc.entity.Departement;
import jdonref_es_poc.index.JDONREFIndex;
import org.junit.Test;

/**
 *
 * @author Julien
 */
public class Valide
{
    
    
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
    
    public jdonref_es_poc.entity.Voie[] getVoies()
    {
        Commune[] c = getCommunes();
        
        jdonref_es_poc.entity.Voie[] v = new jdonref_es_poc.entity.Voie[5];
        
        int i=0;
        v[i++] = new jdonref_es_poc.entity.Voie(1,25,"111","BOULEVARD","DE","L HOPITAL",c[1]);
        v[i++] = new jdonref_es_poc.entity.Voie(26,114,"112","BOULEVARD","DE","L HOPITAL",c[3]);
        v[i++] = new jdonref_es_poc.entity.Voie(1,100,"113","AVENUE","","PAUL ELUARD",c[2]);
        v[i++] = new jdonref_es_poc.entity.Voie(1,100,"113","RUE","DE","L HOPITAL SAINT LOUIS",c[3]);
        v[i++] = new jdonref_es_poc.entity.Voie(1,100,"113","BD","DE","PARIS",c[2]);
        
        return v;
    }
    
    public JDONREFIndex getJDONREFIndex(boolean bouchon,boolean reindex,boolean verboseIndexation,String url,String connectionString,String user,String passwd) throws SQLException, IOException
    {
        JDONREFIndex jdonrefIndex = new JDONREFIndex(url);
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
                Connection connection = DriverManager.getConnection(connectionString,user,passwd);
                jdonrefIndex.setConnection(connection);
                jdonrefIndex.reindex();
            }
        }
        return jdonrefIndex;
    }
    
    @Test
    public void valideTestsAfterIndexation() throws ParseException, SQLException
    {
        // URL d'un master et load balancer d'elasticsearch
        String url = "192.168.0.11:9200";
        boolean bouchon = false;
        boolean reindex = false;
        boolean verboseIndexation = true;
        
        // connection à la base de JDONREF
        String connectionString = "jdbc:postgresql://192.168.0.2:5432/db1";
        String user = "postgres";
        String passwd = "postgres";
        
        try {
            JDONREFIndex index = getJDONREFIndex(bouchon,reindex,verboseIndexation,url,connectionString,user,passwd);
            
            AdresseBusiness adresseBO = new AdresseBusiness(index);
            adresseBO.setHitsPerPage(5);
            adresseBO.setLimit(1.0f);
            
            JsonArray hits;
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