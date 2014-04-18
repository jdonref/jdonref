package jdonref_es_poc.index;

import com.sun.jersey.api.client.Client;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
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
//            dptIndex.indexJDONREFDepartement(getVoies(), "75");
        
//            CommuneIndex cIndex = new CommuneIndex();
//            cIndex.setVerbose(isVerbose());
//            cIndex.setConnection(connection);
//            cIndex.setUtil(util);
//            cIndex.indexJDONREFCommune(getCommunes());
        }
        else
        {
            Connection connection = getConnection();
        
            DepartementIndex dptIndex = new DepartementIndex();
            dptIndex.setVerbose(isVerbose());
            dptIndex.setConnection(connection);
            dptIndex.setUtil(util);
            dptIndex.indexJDONREFDepartements();
//            dptIndex.indexJDONREFDepartement("75");
//            dptIndex.indexJDONREFDepartement("92");
//            dptIndex.indexJDONREFDepartement("93");
//            dptIndex.indexJDONREFDepartement("94");
//        
//            CommuneIndex cIndex = new CommuneIndex();
//            cIndex.setVerbose(isVerbose());
//            cIndex.setConnection(connection);
//            cIndex.setUtil(util);
//            cIndex.indexJDONREFCommune();
        }
        
        long end = Calendar.getInstance().getTimeInMillis();
        
        if (isVerbose())
            System.out.println("Indexation complète en "+(end-start)+" millis.");
    }
}
