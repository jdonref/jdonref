package jdonref_es_poc.index;

import com.sun.jersey.api.client.Client;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
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
    boolean withGeometry = true;
    ElasticSearchUtil util;
    String index = "jdonref";

    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
    }

    
    
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
//        util.deleteIndex();        
        
        
//        util.showDeleteType("departement");
//        util.showDeleteType("voie");
//        util.showDeleteType("adresse");
//        util.showDeleteType("pays");
//        util.showDeleteType("commune");
//        util.showDeleteType("troncon");
//        util.showDeleteType("poizon");   


        util.showCreateIndex("./src/resources/index/jdonrefv3es-settings.json");
        util.showPutMapping("departement", "./src/resources/mapping/mapping-departement.json");
        util.showPutMapping("voie", "./src/resources/mapping/mapping-voie.json");
        util.showPutMapping("adresse", "./src/resources/mapping/mapping-adresse.json");
        util.showPutMapping("pays", "./src/resources/mapping/mapping-pays.json");
        util.showPutMapping("commune", "./src/resources/mapping/mapping-commune.json");
        util.showPutMapping("troncon", "./src/resources/mapping/mapping-troncon.json");
        util.showPutMapping("poizon", "./src/resources/mapping/mapping-poizon.json");
        
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
            dptIndex.setWithGeometry(withGeometry);
            dptIndex.indexJDONREFDepartements();
            
//            AllDepVoieAdrTron(dptIndex);
            
            dptIndex.indexJDONREFDepartement("01");
//            dptIndex.indexJDONREFDepartement("02");
//            dptIndex.indexJDONREFDepartement("03");
//            dptIndex.indexJDONREFDepartement("04");
//            dptIndex.indexJDONREFDepartement("05");
//            dptIndex.indexJDONREFDepartement("06");
//            dptIndex.indexJDONREFDepartement("07");
//            dptIndex.indexJDONREFDepartement("08");
//            dptIndex.indexJDONREFDepartement("09");
//            dptIndex.indexJDONREFDepartement("10");
//            dptIndex.indexJDONREFDepartement("11");
//            dptIndex.indexJDONREFDepartement("12");
//            dptIndex.indexJDONREFDepartement("13");
//            dptIndex.indexJDONREFDepartement("14");
//            dptIndex.indexJDONREFDepartement("15");
//            dptIndex.indexJDONREFDepartement("16");
//            dptIndex.indexJDONREFDepartement("17");
//            dptIndex.indexJDONREFDepartement("18");
//            dptIndex.indexJDONREFDepartement("19");
            //dptIndex.indexJDONREFDepartement("20_a");
//            dptIndex.indexJDONREFDepartement("20_b");
//            dptIndex.indexJDONREFDepartement("21");
//            dptIndex.indexJDONREFDepartement("22");
//            dptIndex.indexJDONREFDepartement("23");
//            dptIndex.indexJDONREFDepartement("24");
//            dptIndex.indexJDONREFDepartement("25");      
//            dptIndex.indexJDONREFDepartement("75");      

            PoizonIndex pzIndex = new PoizonIndex();
            pzIndex.setVerbose(isVerbose());
            pzIndex.setConnection(connection);
            pzIndex.setWithGeometry(withGeometry);
            pzIndex.setUtil(util);
            pzIndex.indexJDONREFPoizon();
            
            CommuneIndex cIndex = new CommuneIndex();
            cIndex.setVerbose(isVerbose());
            cIndex.setConnection(connection);
            cIndex.setWithGeometry(withGeometry);
            cIndex.setUtil(util);
            cIndex.indexJDONREFCommune();
            
        }
        
        long end = Calendar.getInstance().getTimeInMillis();
        
        if (isVerbose())
            System.out.println("Indexation complète en "+(end-start)+" millis.");
    }
    
//
    public void AllDepVoieAdrTron(DepartementIndex dptIndex) throws IOException, SQLException
    {
        for(int i=95;i>0;i--){
            if(i == 20){
                dptIndex.indexJDONREFDepartement("20_a");
                dptIndex.indexJDONREFDepartement("20_b"); 
            }
            else{
                if(i<10) dptIndex.indexJDONREFDepartement("0"+i);
                else dptIndex.indexJDONREFDepartement(""+i);
            }
        }
        for(int j=1;j<=4;j++)
            dptIndex.indexJDONREFDepartement(97+""+j);
    }
    
    

    
        
    
}
