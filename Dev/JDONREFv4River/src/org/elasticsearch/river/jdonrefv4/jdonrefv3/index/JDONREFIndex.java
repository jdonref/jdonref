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
    boolean restart = true;
    boolean verbose = false;
    boolean withGeometry = true;
    boolean withSwitchAlias = false;
    boolean parent = false;

    ElasticSearchUtil util;
    String index = null;
//    String alias = null;
    ArrayList<String> aliasL = new ArrayList<>();
    
    String departement_index;
    String voie_index;
    String adresse_index;
    String pays_index;
    String commune_index;
    String troncon_index;
    String poizon_index;
        
    //ArrayList<String> departements;
    
    HashSet<FLAGS> flags = new HashSet<>();

    String url;
    
    protected static JDONREFIndex instance = null;

    public boolean isRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        util.setUrl(url);
    }
    
    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }
    
    public static JDONREFIndex getInstance()
    {
        if (instance==null)
            instance = new JDONREFIndex();
        return instance;
    }
    
    public void setWithSwitchAlias(boolean withSwitchAlias)
    {
        this.withSwitchAlias = withSwitchAlias;
        
        departement_index = voie_index = adresse_index = pays_index = commune_index = troncon_index = poizon_index = index;
        
        if (withSwitchAlias)
        {
            departement_index += "_departement_"+millis;
            if(parent == false){
                voie_index += "_voie_"+millis;
                adresse_index += "_adresse_"+millis;
            }else{
                voie_index += "_adr_voie_"+millis;
                adresse_index += "_adr_voie_"+millis;
            }
            pays_index += "_pays_"+millis;
            commune_index += "_commune_"+millis;
            troncon_index += "_troncon_"+millis;
            poizon_index += "_poizon_"+millis;
        }
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

    long millis = Calendar.getInstance().getTimeInMillis();

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        if (millis!=0)
            this.millis = millis;
        else
            this.millis = Calendar.getInstance().getTimeInMillis();
        
        departement_index = voie_index = adresse_index = pays_index = commune_index = troncon_index = poizon_index = index;
        
        if (withSwitchAlias)
        {
            departement_index += "_departement_"+millis;
            if(parent == false){
                voie_index += "_voie_"+millis;
                adresse_index += "_adresse_"+millis;
            }else{
                voie_index += "_adr_voie_"+millis;
                adresse_index += "_adr_voie_"+millis;
            }
            pays_index += "_pays_"+millis;
            commune_index += "_commune_"+millis;
            troncon_index += "_troncon_"+millis;
            poizon_index += "_poizon_"+millis;
        }
    }
    
    public void setIndex(String index) {
        this.index = index;
        
        departement_index = voie_index = adresse_index = pays_index = commune_index = troncon_index = poizon_index = index;
        
        if (withSwitchAlias)
        {
            departement_index += "_departement_"+millis;
            if(parent == false){
                voie_index += "_voie_"+millis;
                adresse_index += "_adresse_"+millis;
            }else{
                voie_index += "_adr_voie_"+millis;
                adresse_index += "_adr_voie_"+millis;
            }
            pays_index += "_pays_"+millis;
            commune_index += "_commune_"+millis;
            troncon_index += "_troncon_"+millis;
            poizon_index += "_poizon_"+millis;
        }
    }
    
    

//    public String getAlias() {
//        return alias;
//    }
//
//    public void setAlias(String alias) {
//        this.alias = alias;
//    }

    public ArrayList<String> getAliasL() {
        return aliasL;
    }

    public void setAliasL(ArrayList<String> aliasL) {
        this.aliasL = aliasL;
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
    
    
    protected JDONREFIndex()
    {
        this.util = new ElasticSearchUtil();
        
        Client client = Client.create();
        util.setClient(client);
        
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
        if (isVerbose())
            System.out.println("Démarrage de l'indexation");
        long start = Calendar.getInstance().getTimeInMillis();
        
        if (restart)
        {
            if (!withSwitchAlias)
            {
                this.setIndex(this.index);
                util.showDeleteIndex(index);

                util.showCreateIndex(index,"./src/resources/index/jdonrefv4-settings.json","5");
                util.showSetRefreshInterval(index,"-1");
            }
            else
            {
                if (isFlag(FLAGS.DEPARTEMENT))
                {
                    util.showCreateIndex(departement_index,"./src/resources/index/jdonrefv4-settings.json","1");
                    util.showSetRefreshInterval(departement_index,"-1");
                }
                if (isFlag(FLAGS.VOIE))
                {
                    util.showCreateIndex(voie_index,"./src/resources/index/jdonrefv4-settings.json","6");
                    util.showSetRefreshInterval(voie_index,"-1");
                }
                if (isFlag(FLAGS.ADRESSE))
                {
                    if(!adresse_index.equals(voie_index)){
                        util.showCreateIndex(adresse_index,"./src/resources/index/jdonrefv4-settings.json","6");
                        util.showSetRefreshInterval(adresse_index,"-1");
                    }
                }
                if (isFlag(FLAGS.PAYS))
                {
                    util.showCreateIndex(pays_index,"./src/resources/index/jdonrefv4-settings.json","1");
                    util.showSetRefreshInterval(pays_index,"-1");
                }
                if (isFlag(FLAGS.COMMUNE))
                {
                    util.showCreateIndex(commune_index,"./src/resources/index/jdonrefv4-settings.json","1");
                    util.showSetRefreshInterval(commune_index,"-1");
                }
                if (isFlag(FLAGS.TRONCON))
                {
                    util.showCreateIndex(troncon_index,"./src/resources/index/jdonrefv4-settings.json","5");
                    util.showSetRefreshInterval(troncon_index,"-1");
                }
                if (isFlag(FLAGS.POIZON))
                {
                    util.showCreateIndex(poizon_index,"./src/resources/index/jdonrefv4-settings.json","1");
                    util.showSetRefreshInterval(poizon_index,"-1");
                }
            }
        }

        if (isFlag(FLAGS.DEPARTEMENT))
        {
            if (restart)
                util.showPutMapping(departement_index,"departement", "./src/resources/mapping/mapping-departement.json");
        }
        if (isFlag(FLAGS.DEPARTEMENT) || isFlag(FLAGS.VOIE) || isFlag(FLAGS.ADRESSE) || isFlag(FLAGS.TRONCON))
        {
            DepartementIndex dptIndex = DepartementIndex.getInstance();
            dptIndex.setDept(codesDepartements);
            dptIndex.setFlags(flags);
            dptIndex.setConnection(connection);
            dptIndex.setUtil(util);
            dptIndex.setVerbose(isVerbose());
            dptIndex.setWithGeometry(withGeometry);
            dptIndex.setIndex(departement_index);
        }
        if (isFlag(FLAGS.VOIE))
        {
            if (restart)
                util.showPutMapping(voie_index,"voie", "./src/resources/mapping/mapping-voie.json");
            VoieIndex vIndex = VoieIndex.getInstance();
            vIndex.setUtil(util);
            vIndex.setConnection(connection);
            vIndex.setVerbose(isVerbose());
            vIndex.setWithGeometry(withGeometry);
            vIndex.setIndex(voie_index);
        }
        if (isFlag(FLAGS.ADRESSE))
        {
            if (restart){
                if(parent==false) util.showPutMapping(adresse_index,"adresse", "./src/resources/mapping/mapping-adresse.json");
                else util.showPutMapping(adresse_index,"adresse", "./src/resources/mapping/mapping-adresse-parent.json");
            }
            AdresseIndex adrIndex = AdresseIndex.getInstance();
            adrIndex.setUtil(util);
            adrIndex.setConnection(connection);
            adrIndex.setVerbose(isVerbose());
            adrIndex.setWithGeometry(withGeometry);
            adrIndex.setIndex(adresse_index);
        }
        if (isFlag(FLAGS.PAYS))
        {
            if (restart)
                util.showPutMapping(pays_index,"pays", "./src/resources/mapping/mapping-pays.json");
            PaysIndex paysIndex = PaysIndex.getInstance();
            paysIndex.setVerbose(isVerbose());
            paysIndex.setConnection(connection);
            paysIndex.setUtil(util);
            paysIndex.setWithGeometry(withGeometry);
            paysIndex.setIndex(pays_index);
        }
        if (isFlag(FLAGS.COMMUNE))
        {
            if (restart)
                util.showPutMapping(commune_index,"commune", "./src/resources/mapping/mapping-commune.json");
            CommuneIndex cIndex = CommuneIndex.getInstance();
            cIndex.setDept(codesDepartements);
            cIndex.setVerbose(isVerbose());
            cIndex.setConnection(connection);
            cIndex.setWithGeometry(withGeometry);
            cIndex.setUtil(util);
            cIndex.setIndex(commune_index);
        }
        if (isFlag(FLAGS.TRONCON))
        {
            if (restart)
                util.showPutMapping(troncon_index,"troncon", "./src/resources/mapping/mapping-troncon.json");
            TronconIndex tIndex = TronconIndex.getInstance();
            tIndex.setUtil(util);
            tIndex.setConnection(connection);
            tIndex.setVerbose(isVerbose());
            tIndex.setWithGeometry(withGeometry);
            tIndex.setIndex(troncon_index);
        }
        if (isFlag(FLAGS.POIZON))
        {
            if (restart)
                util.showPutMapping(poizon_index,"poizon", "./src/resources/mapping/mapping-poizon.json");
            PoizonIndex pzIndex = PoizonIndex.getInstance();
            pzIndex.setVerbose(isVerbose());
            pzIndex.setConnection(connection);
            pzIndex.setWithGeometry(withGeometry);
            pzIndex.setUtil(util);
            pzIndex.setIndex(poizon_index);
        }
        
        if (bouchon)
        {
            if (isFlag(FLAGS.DEPARTEMENT))
                DepartementIndex.getInstance().indexJDONREFDepartements(getDepartements());
            if (isFlag(FLAGS.VOIE))
                DepartementIndex.getInstance().indexJDONREFDepartement(getVoies(), "75");
            if (isFlag(FLAGS.COMMUNE))
                CommuneIndex.getInstance().indexJDONREFCommune(getCommunes());
            util.showSetRefreshInterval(index,"30s");
        }
        else
        {
            if (isFlag(FLAGS.PAYS))
            {
                PaysIndex.getInstance().indexJDONREFPays();   
                util.showSetRefreshInterval(pays_index,"30s");
                util.showSetReplicaNumber(pays_index, "1");
                if (withSwitchAlias)
                    for(String alias : aliasL)
                        util.showExchangeIndexInAlias(alias, pays_index, index+"_pays");
            }
            if (isFlag(FLAGS.DEPARTEMENT))
            {
                DepartementIndex.getInstance().indexJDONREFDepartements();
                util.showSetRefreshInterval(departement_index,"30s");
                util.showSetReplicaNumber(departement_index, "1");
                if (withSwitchAlias)
                    for(String alias : aliasL)
                        util.showExchangeIndexInAlias(alias, departement_index, index+"_departement");                
            }
            if (isFlag(FLAGS.COMMUNE))
            {
                CommuneIndex.getInstance().indexJDONREFCommune();
                util.showSetRefreshInterval(commune_index,"30s");
                util.showSetReplicaNumber(commune_index, "1");
                if (withSwitchAlias)
                    for(String alias : aliasL)
                        util.showExchangeIndexInAlias(alias, commune_index, index+"_commune");
            }
            if (isFlag(FLAGS.POIZON))
            {
                PoizonIndex.getInstance().indexJDONREFPoizon();
                util.showSetRefreshInterval(poizon_index,"30s");
                util.showSetReplicaNumber(poizon_index, "1");
                if (withSwitchAlias)
                    for(String alias : aliasL)
                        util.showExchangeIndexInAlias(alias, poizon_index, index+"_poizon");
            }
            for(int i=0;i<codesDepartements.length;i++)
                DepartementIndex.getInstance().indexJDONREFDepartement(parent, codesDepartements[i]);
            
            if (isFlag(FLAGS.VOIE))
            {
                util.showSetRefreshInterval(voie_index,"30s");
                util.showSetReplicaNumber(voie_index, "1");
                if (withSwitchAlias){
                    if(parent==false){
                        for(String alias : aliasL)
                            util.showExchangeIndexInAlias(alias, voie_index, index+"_voie"); 
                    }
                }
            }
            if (isFlag(FLAGS.ADRESSE))
            {
                util.showSetRefreshInterval(adresse_index,"30s");
                util.showSetReplicaNumber(adresse_index, "1");
                if (withSwitchAlias){
                    if(parent==false){
                        for(String alias : aliasL)
                            util.showExchangeIndexInAlias(alias, adresse_index, index+"_adresse"); 
                    }else{
                        for(String alias : aliasL)
                            util.showExchangeIndexInAlias(alias, adresse_index, index+"_adr_voie"); 
                    }
                }
            }            
            if (isFlag(FLAGS.TRONCON))
            {
                util.showSetRefreshInterval(troncon_index,"30s");
                util.showSetReplicaNumber(troncon_index, "1");
                if (withSwitchAlias)
                    for(String alias : aliasL)
                        util.showExchangeIndexInAlias(alias, troncon_index, index+"_troncon");
            }
        }
        
        long end = Calendar.getInstance().getTimeInMillis();
        
        if (isVerbose())
            System.out.println("Indexation complète en "+(end-start)+" millis.");
    }
}