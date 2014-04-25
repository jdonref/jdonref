package jdonref_es_poc.index;

import com.sun.jersey.api.client.Client;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.entity.Commune;
import jdonref_es_poc.entity.Departement;
import jdonref_es_poc.entity.Voie;

/**
 *
 * @author Julien
 */
public class JDONREFIndex
{
    boolean verbose = false;
    ElasticSearchUtil util;
    String index = "jdonref";

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
        this.util.setIndex(index);
    }

    public ElasticSearchUtil getUtil() {
        return util;
    }

    public void setUtil(ElasticSearchUtil util) {
        this.util = util;
    }
     
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
    
    
    public JDONREFIndex(String url)
    {
        this.util = new ElasticSearchUtil();
        Client client = Client.create();
        util.setClient(client);
        util.setUrl(url);
        util.setIndex(index);
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
    
    public void reindex() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Démarrage de l'indexation");
        long start = Calendar.getInstance().getTimeInMillis();
        
        util.showDeleteIndex();
        //util.deleteIndex();
        
        if (bouchon)
        {
            DepartementIndex dptIndex = new DepartementIndex();
            dptIndex.setVerbose(isVerbose());
            dptIndex.setConnection(connection);
            dptIndex.setUtil(util);
            dptIndex.indexJDONREFDepartements(getDepartements());
            dptIndex.indexJDONREFDepartement(getVoies(), "75");
        
            CommuneIndex cIndex = new CommuneIndex();
            cIndex.setVerbose(isVerbose());
            cIndex.setConnection(connection);
            cIndex.setUtil(util);
            cIndex.indexJDONREFCommune(getCommunes());
        }
        else
        {
            Connection connection = getConnection();
        
            DepartementIndex dptIndex = new DepartementIndex();
            dptIndex.setVerbose(isVerbose());
            dptIndex.setConnection(connection);
            dptIndex.setUtil(util);
//            dptIndex.indexJDONREFDepartements();        
//            dptIndex.indexJDONREFDepartements(getDepartements2());  
//            dptIndex.indexJDONREFDepartement(getVoies2(), "75");
            dptIndex.indexJDONREFDepartement("75");
//            dptIndex.indexJDONREFDepartement("91");
//            dptIndex.indexJDONREFDepartement("92");
//            dptIndex.indexJDONREFDepartement("93");
//            dptIndex.indexJDONREFDepartement("94");
//            dptIndex.indexJDONREFDepartement("95");
        
//            CommuneIndex cIndex = new CommuneIndex();
//            cIndex.setVerbose(isVerbose());
//            cIndex.setConnection(connection);
//            cIndex.setUtil(util);
//            cIndex.indexJDONREFCommune();
//            cIndex.indexJDONREFCommune(getCommunes2());
        }
        
        long end = Calendar.getInstance().getTimeInMillis();
        
        if (isVerbose())
            System.out.println("Indexation complète en "+(end-start)+" millis.");
    }
    
    
 ///////////////////////////////////METHODES POUR TEST/////////////////////////////////   
    
        public Departement[] getDepartements2()
    {
        Departement[] d = new Departement[6];
        int i=0;
        Date t0 = new Date(2014, 02, 01, 00, 00, 00);
        Date t1 = new Date(2014, 02, 01, 00, 00, 00);
        d[i++] = new Departement("======01","WGS84","IGN2014",t0,t1);
        d[i++] = new Departement("======02","WGS84","IGN2014",t0,t1);
        d[i++] = new Departement("======03","WGS84","IGN2014",t0,t1);
        d[i++] = new Departement("======04","WGS84","IGN2014",t0,t1);
        d[i++] = new Departement("======05","WGS84","IGN2014",t0,t1);
        d[i++] = new Departement("======06","WGS84","IGN2014",t0,t1);
        return d;
    }
    
        public Commune[] getCommunes2()
    {
        Commune[] c = new Commune[4];
        int i=0;
         Date t0 = new Date(2014, 02, 01, 00, 00, 00);
         Date t1 = new Date(2014, 02, 01, 00, 00, 00);
         c[i++] = new Commune("75056", "75", "75000", "PARIS", "PARIS", "Paris", "PARIS", null, t0, t1);
         c[i++] = new Commune("75105", "75", "75005", "PARIS 5 E ARRONDISSEMENT", "PARIS 5 E ARRONDISSEMENT", "Paris 5e Arrondissement", "PARIS K4K E AR1DI5EN1", "75056", t0, t1);
         c[i++] = new Commune("93008", "93", "93000", "BOBIGNY", "BOBIGNY", "Bobigny", "BOBIGNI", null, t0, t1);
         c[i++] = new Commune("75113", "75", "75003", "PARIS 13 E ARRONDISSEMENT", "PARIS 13 E ARRONDISSEMENT", "Paris 13e Arrondissement", "PARIS TRYZE E AR1DI5EN1", "75056", t0, t1);  
        return c;
    }

    public jdonref_es_poc.entity.Voie[] getVoies2()
    {
        Commune[] c = getCommunes2();    
        jdonref_es_poc.entity.Voie[] v = new jdonref_es_poc.entity.Voie[5];  
        int i=0;
        v[i++] = new jdonref_es_poc.entity.Voie(c[1],"751054649","7581134649","BOULEVARD DE L HOPITAL","BOULEVARD DE L HOPITAL","BD DE L'HOPITAL","BOULEVARD","B3LEVARD","DE L HOPITAL","DE L OPITAL","HOPITAL","OPITAL","HOPITAL","OPITAL",0,42,"");
        v[i++] = new jdonref_es_poc.entity.Voie(c[3],"751134649","7581134649","BOULEVARD DE L HOPITAL","BOULEVARD DE L HOPITAL","BD DE L'HOPITAL","BOULEVARD","B3LEVARD","DE L HOPITAL","DE L OPITAL","HOPITAL","OPITAL","HOPITAL","OPITAL",0,171,"");
        v[i++] = new jdonref_es_poc.entity.Voie(c[2],"930087142","9300087142","AVENUE PAUL ELUARD","AVENUE PAUL ELUARD","AV PAUL ELUARD","AVENUE","AVENUE","PAUL ELUARD","POL ELUARD","PAUL ELUARD","POL ELUARD","PAUL ELUARD","POL ELUARD",1,23,"");
        v[i++] = new jdonref_es_poc.entity.Voie(c[3],"75113#04L","7542021547","RUE MAURICE ET LOUIS DE BROGLIE","RUE MAURICE ET LOUIS DE BROGLIE","R MAURICE ET LOUIS DE BROGLIE","RUE","RUE","MAURICE ET LOUIS DE BROGLIE","NORISE Y L3IS DE BROGLIE","MAURICE LOUIS BROGLIE","NORISE L3IS BROGLIE","MAURICE LOUIS BROGLIE","NORISE L3IS BROGLIE",1,12,"");
        v[i++] = new jdonref_es_poc.entity.Voie(c[2],"930087133","9300781015","RUE DE PARIS","RUE DE PARIS","R DE PARIS","RUE","RUE","DE PARIS","DE PARIS","PARIS","PARIS","PARIS","PARIS",0,191,"");     
        return v;
    }        
        
    
}