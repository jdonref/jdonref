package jdonref_es_poc.jdonrefindextest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdonref_es_poc.entity.Commune;
import jdonref_es_poc.entity.Departement;
import jdonref_es_poc.entity.Voie;
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
        Departement[] d = new Departement[6];
        
        int i=0;
        
//        d[i++] = new Departement("75");
//        d[i++] = new Departement("93");
        
        Date t0 = new Date(2014, 02, 01, 00, 00, 00);
        Date t1 = new Date(2014, 02, 01, 00, 00, 00);
//        d[i++] = new Departement("======01","WGS84","IGN2014",t0,t1);
//        d[i++] = new Departement("======02","WGS84","IGN2014",t0,t1);
//        d[i++] = new Departement("======03","WGS84","IGN2014",t0,t1);
//        d[i++] = new Departement("======04","WGS84","IGN2014",t0,t1);
//        d[i++] = new Departement("======05","WGS84","IGN2014",t0,t1);
//        d[i++] = new Departement("======06","WGS84","IGN2014",t0,t1);
        
        return d;
    }
    
    public Commune[] getCommunes()
    {
        Commune[] c = new Commune[4];
        
        int i=0;
//        c[i++] = new Commune("PARIS","75056","75000",null);
//        c[i++] = new Commune("PARIS","75105","75005","75056");
//        c[i++] = new Commune("BOBIGNY","93000","93000",null);
//        c[i++] = new Commune("PARIS","75113","75013","75056");
        
         Date t0 = new Date(2014, 02, 01, 00, 00, 00);
         Date t1 = new Date(2014, 02, 01, 00, 00, 00);
         c[i++] = new Commune("75056", "75", "PARIS", "PARIS", "Paris", "PARIS", null, t0, t1, "75000");
         c[i++] = new Commune("75105", "75", "PARIS 5 E ARRONDISSEMENT", "PARIS 5 E ARRONDISSEMENT", "Paris 5e Arrondissement", "PARIS K4K E AR1DI5EN1", "75056", t0, t1, "75005");
         c[i++] = new Commune("93008", "93", "BOBIGNY", "BOBIGNY", "Bobigny", "BOBIGNI", null, t0, t1, "93000");
         c[i++] = new Commune("75113", "75", "PARIS 13 E ARRONDISSEMENT", "PARIS 13 E ARRONDISSEMENT", "Paris 13e Arrondissement", "PARIS TRYZE E AR1DI5EN1", "75056", t0, t1, "75003");
        
        return c;
    }
    
    public jdonref_es_poc.entity.Voie[] getVoies()
    {
        Commune[] c = getCommunes();
        
        jdonref_es_poc.entity.Voie[] v = new jdonref_es_poc.entity.Voie[5];
         Date t0 = new Date(2014, 02, 01, 00, 00, 00);
         Date t1 = new Date(2014, 02, 01, 00, 00, 00);
        int i=0;
//        v[i++] = new jdonref_es_poc.entity.Voie(1,25,"111","BOULEVARD","DE","L HOPITAL",c[1]);
//        v[i++] = new jdonref_es_poc.entity.Voie(26,114,"112","BOULEVARD","DE","L HOPITAL",c[3]);
//        v[i++] = new jdonref_es_poc.entity.Voie(1,100,"113","AVENUE","","PAUL ELUARD",c[2]);
//        v[i++] = new jdonref_es_poc.entity.Voie(1,100,"113","RUE","DE","L HOPITAL SAINT LOUIS",c[3]);
//        v[i++] = new jdonref_es_poc.entity.Voie(1,100,"113","BD","DE","PARIS",c[2]);

        v[i++] = new Voie(c[1],"751054649","7581134649","BOULEVARD DE L HOPITAL","BOULEVARD DE L HOPITAL","BD DE L'HOPITAL","cdp_code_postal","BOULEVARD","B3LEVARD","DE L HOPITAL","DE L OPITAL","HOPITAL","OPITAL","HOPITAL","OPITAL",0,42, t0, t1,"");
        v[i++] = new Voie(c[3],"751134649","7581134649","BOULEVARD DE L HOPITAL","BOULEVARD DE L HOPITAL","BD DE L'HOPITAL","cdp_code_postal","BOULEVARD","B3LEVARD","DE L HOPITAL","DE L OPITAL","HOPITAL","OPITAL","HOPITAL","OPITAL",0,171, t0, t1,"");
        v[i++] = new Voie(c[2],"930087142","9300087142","AVENUE PAUL ELUARD","AVENUE PAUL ELUARD","AV PAUL ELUARD","cdp_code_postal","AVENUE","AVENUE","PAUL ELUARD","POL ELUARD","PAUL ELUARD","POL ELUARD","PAUL ELUARD","POL ELUARD",1,23, t0, t1,"");
        v[i++] = new Voie(c[3],"75113#04L","7542021547","RUE MAURICE ET LOUIS DE BROGLIE","RUE MAURICE ET LOUIS DE BROGLIE","R MAURICE ET LOUIS DE BROGLIE","cdp_code_postal","RUE","RUE","MAURICE ET LOUIS DE BROGLIE","NORISE Y L3IS DE BROGLIE","MAURICE LOUIS BROGLIE","NORISE L3IS BROGLIE","MAURICE LOUIS BROGLIE","NORISE L3IS BROGLIE",1,12, t0, t1,"");
        v[i++] = new Voie(c[2],"930087133","9300781015","RUE DE PARIS","RUE DE PARIS","R DE PARIS","cdp_code_postal","RUE","RUE","DE PARIS","DE PARIS","PARIS","PARIS","PARIS","PARIS",0,191, t0, t1,"");
        
        return v;
    }
//"geometrie":{"type":"point","coordinates":[2.3292484283447266,48.84809494018555]}}}}
    
    public JDONREFIndex getJDONREFIndex(boolean bouchon,boolean reindex,boolean verboseIndexation,boolean withGeometry,String url,String connectionString,String user,String passwd) throws SQLException, IOException
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
                jdonrefIndex.setWithGeometry(withGeometry);
                
                jdonrefIndex.reindex();
            }
            else
            {
                Connection connection = DriverManager.getConnection(connectionString,user,passwd);
                jdonrefIndex.setConnection(connection);
                jdonrefIndex.setWithGeometry(withGeometry);
                jdonrefIndex.reindex();
            }
        }
        return jdonrefIndex;
    }
    

    @Test
    public void valideTestsAfterIndexation() throws ParseException, SQLException
    {
        // URL d'un master et load balancer d'elasticsearch
        String url = "10.213.92.202:9200";
        boolean bouchon = false;
        boolean reindex = true;
        boolean verboseIndexation = true;
        boolean withGeometry = false;

 
        // connection à la base de JDONREF
        String connectionString = "jdbc:postgresql://localhost:5432/JDONREF_IGN2";
        String user = "postgres";
        String passwd = "postgres";
        
        try {
            JDONREFIndex index = getJDONREFIndex(bouchon,reindex,verboseIndexation,withGeometry,url,connectionString,user,passwd);
            
//            AdresseBusiness adresseBO = new AdresseBusiness(index);
//            adresseBO.setHitsPerPage(5);
//            adresseBO.setLimit(1.0f);
//            
//            JsonArray hits;
//            hits = adresseBO.valide("BOULEVARD DE L HOPITAL 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !

            // fonctionne avec un mot unique !
//            hits = adresseBO.valide("hopital");
//            hits = adresseBO.valide("hapitol"); // pietre performances avec 1 VM de 450000 objets 5 shards (2.5 sec)

//            hits = adresseBO.valide("hopital paris"); // problème : propose les numéros plutôt que hopital saint louis !
//            hits = adresseBO.valide("15 hopital paris"); // problème : propose les numéros plutôt que hopital saint louis !
//            hits = adresseBO.valide("15 hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !
//            hits = adresseBO.valide("75 hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !

//            hits = adresseBO.valide("bd hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !
//            hits = adresseBO.valide("bd hapitol 75 paris"); // problème : propose les arrondissements !
//            hits = adresseBO.valide("24 bd hapitol 75 paris"); // problème : propose la route 24 !
//            hits = adresseBO.valide("24 bd hopital 75 paris"); // problème : propose les numéros plutôt que hopital saint louis !
//            hits = adresseBO.valide("75 paris");
//            hits = adresseBO.valide("75"); // problème : uniquement en recherche approchée !
//            hits = adresseBO.valide("93"); // problème : uniquement en recherche approchée !
        } catch (IOException ex) {
            Logger.getLogger(Valide.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Valide.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}