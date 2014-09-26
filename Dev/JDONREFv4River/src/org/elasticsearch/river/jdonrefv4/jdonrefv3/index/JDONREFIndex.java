package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import com.sun.jersey.api.client.Client;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Commune;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Departement;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Voie;

/**
 *
 * @author Julien
 */
public class JDONREFIndex
{
    boolean verbose = false;
    boolean withGeometry = true;
    boolean withAlias = false;
    ElasticSearchUtil util;
    String index = "jdonref";
    String alias = "jdonref";
    //ArrayList<String> departements;
    
    HashSet<FLAGS> flags = new HashSet<>();

    public void setWithAlias(boolean withAlias)
    {
        this.withAlias = withAlias;
    }

    public static enum FLAGS
    {
        PAYS,
        DEPARTEMENT,
        COMMUNE,
        VOIE,
        TRONCON,
        ADRESSE,
        POIZON
    };
    
    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
    }

    public void addFlag(FLAGS flag)
    {
        flags.add(flag);
    }
    
    public void removeFlag(FLAGS flag)
    {
        flags.remove(flag);
    }
    
    public boolean isFlag(FLAGS flag)
    {
        return flags.contains(flag);
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
        
        this.index += Calendar.getInstance().getTimeInMillis();
        
        Client client = Client.create();
        util.setClient(client);
        util.setUrl(url);
        util.setIndex(index);
        
        setDefaultFlags();
        setDefaultCodeDepartements();
    }
    
    public void setDefaultFlags()
    {
        flags.add(FLAGS.PAYS);
        flags.add(FLAGS.DEPARTEMENT);
        flags.add(FLAGS.COMMUNE);
        flags.add(FLAGS.VOIE);
        flags.add(FLAGS.ADRESSE);
        flags.add(FLAGS.POIZON);
    }
    
    String[] codesDepartements = null;

    public void setCodesDepartements(String[] codesDepartements) {
        this.codesDepartements = codesDepartements;
    }
    
    public void setDefaultCodeDepartements()
    {
        codesDepartements = new String[100];
        int index = 0;
        for(int i=95;i>0;i--){
            if(i == 20){
                codesDepartements[index++] = "20_a";
                codesDepartements[index++] = "20_b";
            }
            else{
                if(i<10) codesDepartements[index++] = "0"+i;
                else codesDepartements[index++] = ""+i;
            }
        }
        for(int j=1;j<=4;j++)
            codesDepartements[index++] = 97+""+j;
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
        if (!withAlias)
            setIndex(this.alias);
        
        if (isVerbose())
            System.out.println("Démarrage de l'indexation");
        long start = Calendar.getInstance().getTimeInMillis();
        
        if (!withAlias)
            util.showDeleteIndex(); // useless : random index
        
//        util.showDeleteType("departement");
//        util.showDeleteType("voie");
//        util.showDeleteType("adresse");
//        util.showDeleteType("pays");
//        util.showDeleteType("commune");
//        util.showDeleteType("troncon");
//        util.showDeleteType("poizon");   

        util.showCreateIndex("./src/resources/index/jdonrefv4-settings.json");
        util.showPutMapping("departement", "./src/resources/mapping/mapping-departement.json");
        util.showPutMapping("voie", "./src/resources/mapping/mapping-voie.json");
        util.showPutMapping("adresse", "./src/resources/mapping/mapping-adresse.json");
        util.showPutMapping("pays", "./src/resources/mapping/mapping-pays.json");
        util.showPutMapping("commune", "./src/resources/mapping/mapping-commune.json");
        util.showPutMapping("troncon", "./src/resources/mapping/mapping-troncon.json");
        util.showPutMapping("poizon", "./src/resources/mapping/mapping-poizon.json");
        
        if (bouchon)
        {
            if (isFlag(FLAGS.DEPARTEMENT))
            {
                DepartementIndex dptIndex = new DepartementIndex();
                dptIndex.setVerbose(isVerbose());
                dptIndex.setConnection(connection);
                dptIndex.setUtil(util);
                dptIndex.indexJDONREFDepartements(getDepartements());
                dptIndex.indexJDONREFDepartement(getVoies(), "75");
            }
        
            if (isFlag(FLAGS.COMMUNE))
            {
                CommuneIndex cIndex = new CommuneIndex();
                cIndex.setVerbose(isVerbose());
                cIndex.setConnection(connection);
                cIndex.setUtil(util);
                cIndex.indexJDONREFCommune(getCommunes());
            }
        }
        else
        {
            if (isFlag(FLAGS.PAYS))
            {
                PaysIndex paysIndex = new PaysIndex();
                paysIndex.setVerbose(isVerbose());
                paysIndex.setConnection(connection);
                paysIndex.setUtil(util);
                paysIndex.setWithGeometry(withGeometry);
                paysIndex.indexJDONREFPays();
            }
            
            DepartementIndex dptIndex = new DepartementIndex();
            dptIndex.setFlags(flags);
            if (isFlag(FLAGS.DEPARTEMENT))
            {
                dptIndex.setVerbose(isVerbose());
                dptIndex.setConnection(connection);
                dptIndex.setUtil(util);
                dptIndex.setWithGeometry(withGeometry);
                dptIndex.indexJDONREFDepartements();
            }
            
            if (isFlag(FLAGS.COMMUNE))
            {
                CommuneIndex cIndex = new CommuneIndex();
                cIndex.setVerbose(isVerbose());
                cIndex.setConnection(connection);
                cIndex.setWithGeometry(withGeometry);
                cIndex.setUtil(util);
                cIndex.indexJDONREFCommune();
            }
            
            AllDepVoieAdrTron(dptIndex);
            if (isFlag(FLAGS.POIZON))
            {
                PoizonIndex pzIndex = new PoizonIndex();
                pzIndex.setVerbose(isVerbose());
                pzIndex.setConnection(connection);
                pzIndex.setWithGeometry(withGeometry);
                pzIndex.setUtil(util);
                pzIndex.indexJDONREFPoizon();
            }
        }
        
        if (withAlias)
            util.showSetNewAlias(index, alias);
        
        long end = Calendar.getInstance().getTimeInMillis();
        
        if (isVerbose())
            System.out.println("Indexation complète en "+(end-start)+" millis.");
    }
    
//
    public void AllDepVoieAdrTron(DepartementIndex dptIndex) throws IOException, SQLException
    {
        for(int i=0;i<codesDepartements.length;i++)
        {
            dptIndex.indexJDONREFDepartement(codesDepartements[i]);
        }
    }
}
