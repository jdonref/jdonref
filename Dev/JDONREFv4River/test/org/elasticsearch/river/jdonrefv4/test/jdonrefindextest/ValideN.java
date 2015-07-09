package org.elasticsearch.river.jdonrefv4.test.jdonrefindextest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.FrequentTermsUtil;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.InitParameters;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.index.JDONREFIndex;
import org.junit.Test;

public class ValideN {

    String indexName = "indexName";
    ArrayList<String> aliasName = new ArrayList<>();
    boolean bouchon = false;
    boolean reindex = true;
    boolean verboseIndexation = true;
    boolean withGeometry = false;
    boolean withSwitchAlias = true;
    boolean parent = false;
    boolean nested = false;
//    String url = "10.213.92.241:9200";
    String url = "localhost:9200";
//    String url = "plf.jdonrefv4.ppol.minint.fr";
//    String connectionString = "jdbc:postgresql://10.232.73.78:5433/jdonref_ign";
    String connectionString = "jdbc:postgresql://localhost:5432/JDONREF_IGN2";
    String user = "postgres";
    String passwd = "postgres";
    boolean restart = true;
    long millis = 0;

    public ArrayList<String> getAliasName() {
        return aliasName;
    }

    public void setAliasName(ArrayList<String> aliasName) {
        this.aliasName = aliasName;
    }

    public boolean isBouchon() {
        return bouchon;
    }

    public void setBouchon(boolean bouchon) {
        this.bouchon = bouchon;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public boolean isReindex() {
        return reindex;
    }

    public void setReindex(boolean reindex) {
        this.reindex = reindex;
    }

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
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isVerboseIndexation() {
        return verboseIndexation;
    }

    public void setVerboseIndexation(boolean verboseIndexation) {
        this.verboseIndexation = verboseIndexation;
    }

    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
    }

    public boolean isWithSwitchAlias() {
        return withSwitchAlias;
    }

    public void setWithSwitchAlias(boolean withSwitchAlias) {
        this.withSwitchAlias = withSwitchAlias;
    }
    
    
    
    
    public JDONREFIndex initJDONREFIndex(Connection connection, String indexName, ArrayList<String> aliasName, String url, boolean verboseIndexation, boolean restart, boolean withGeometry, boolean withSwitchAlias, boolean parent, long millis) {   
            
        JDONREFIndex jdonrefIndex = JDONREFIndex.getInstance();
        jdonrefIndex.setParent(parent);
        jdonrefIndex.setNested(nested);
        jdonrefIndex.setWithSwitchAlias(withSwitchAlias);
        jdonrefIndex.setIndex(indexName);
        jdonrefIndex.setAliasL(aliasName);
        jdonrefIndex.setUrl(url);
        jdonrefIndex.setVerbose(verboseIndexation);
        jdonrefIndex.setRestart(restart);
        jdonrefIndex.setWithGeometry(withGeometry);
        if (millis!=0)
            jdonrefIndex.setMillis(millis);
        jdonrefIndex.setConnection(connection);
        return jdonrefIndex;
     } 
    
    public void getJDONREFIndex(JDONREFIndex jdonrefIndex, String[] listeDepartement,ArrayList<String> flags) throws SQLException, IOException{
        
        jdonrefIndex.setCodesDepartements(listeDepartement);
        
        if(flags.contains("COMMUNE"))
            jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.COMMUNE); 
        if(flags.contains("TRONCON"))
            jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.TRONCON); //
        if(flags.contains("ADRESSE"))
            jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.ADRESSE);
        if(flags.contains("DEPARTEMENT"))
            jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.DEPARTEMENT);
        if(flags.contains("POIZON"))
            jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.POIZON); //
        if(flags.contains("VOIE"))
           jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.VOIE);
        if(flags.contains("PAYS"))
            jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.PAYS);
        
        jdonrefIndex.reindex();
    }  
     

    
    @Test
    public void valideTestsAfterIndexation() {
        FrequentTermsUtil.setFilePath("./src/resources/analysis/word84.txt");
        if(reindex){
            try {
                Connection connection = DriverManager.getConnection(connectionString,user,passwd); 
                InitParameters initParam = InitParameters.getInstance();
                initParam.allDeptInit(connection);

//                String[] departementsIDF = {"75", "94", "93", "95", "92", "91", "78", "77", "89", "60", "51", "45", "28", "27", "10", "02"};
//                String[] departementsIDF = {"75", "94", "93", "95", "92", "91", "78", "77"}; //1
//                initParam.init2(departementsIDF); // 1               
//                String[] listeDepartements = initParam.getListeDepartement(); //1
                String[] listeDepartements = (String[])initParam.getAllDept().toArray(new String[initParam.getAllDept().size()]); //2 ALL
//                String[] listeDepartements = {"75"}; 
              
//                initParam.depts_intervalle("01","10"); // 3
//                String[] listeDepartements = initParam.getListeDepartement(); //3
                
                //on commente ce qu'on veux indexer !!
                ArrayList<String> flags = new ArrayList<>();
                flags.add("DEPARTEMENT");
                flags.add("COMMUNE");
                flags.add("VOIE");
//                flags.add("ADRESSE");
                flags.add("POIZON");
                flags.add("PAYS");
                flags.add("TRONCON");

                setIndexName("jdonref_idf");
                aliasName.clear();
                aliasName.add("jdonref");
                aliasName.add("jdonref_idf"); 
                setAliasName(aliasName);
                
                JDONREFIndex jdonrefIndex = initJDONREFIndex(connection, indexName, aliasName, url, verboseIndexation, restart, withGeometry, withSwitchAlias, parent, millis);
                getJDONREFIndex(jdonrefIndex, listeDepartements,flags);
/*
//               String[] listeDepartementsN = {"13"};
                String[] listeDepartementsN = initParam.getListeDepartementN();
               
             
                flags.clear();
                flags.add("DEPARTEMENT");
                flags.add("COMMUNE");
                flags.add("VOIE");
//                flags.add("ADRESSE");
                flags.add("TRONCON");
                flags.add("POIZON");
                flags.add("PAYS");

                jdonrefIndex.setIndex("jdonref_without_idf");
                aliasName.clear();
                aliasName.add("jdonref");
                aliasName.add("jdonref_without_idf");
                jdonrefIndex.setAliasL(aliasName);

                getJDONREFIndex(jdonrefIndex, listeDepartementsN,flags);
*/
            } catch (SQLException | IOException ex) {
                Logger.getLogger(ValideN.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    //         public void getJDONREFIndexV3() throws SQLException, IOException
//    {
//
//        JDONREFIndex jdonrefIndex = initJDONREFIndex();
//        
//
//        jdonrefIndex.setCodesDepartements(listeDepartementN); 
//        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.COMMUNE); 
//        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.TRONCON); //
//        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.ADRESSE);
//        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.DEPARTEMENT);
//        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.POIZON); //
////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.VOIE);
//        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.PAYS);
//        jdonrefIndex.reindex();
//        
////        aliasName.add("jdonref_idf");
////        jdonrefIndex.setAliasL(aliasName);
////        jdonrefIndex.setIndex("jdonref_idf");
////        
////        jdonrefIndex.setCodesDepartements(listeDepartement); 
//////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.COMMUNE); 
////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.TRONCON); //
//////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.ADRESSE);
//////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.DEPARTEMENT);
//////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.POIZON);
//////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.VOIE);
//////        jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.PAYS);
////        jdonrefIndex.reindex();
//        
//    }  
//
//         
//    public static void main(String[] args) {
//        FrequentTermsUtil.setFilePath("./src/resources/analysis/word84.txt");
//        InitParameters initParam = InitParameters.getInstance();
//        initParam.setRestart(false);
//        initParam.setIndexName("jdonref_without_idf_adresse_1419844577042");  // modifier le nom de l'index en fonction jdonrefIndex.removeFlag(JDONREFIndex.FLAGS.?????) commenté
//        initParam.setWithSwitchAlias(false);
//        
//        initParam.allDept.clear();
//        for(int i=62;i<96;i++){
//            initParam.allDept.add(""+i);
//        }
//        initParam.allDept.add("971");
//        initParam.allDept.add("972");
//        initParam.allDept.add("973");
//        initParam.allDept.add("974");
//        
//        String[] departementsIDF = {"95", "94", "93", "92", "91", "78", "77", "75"};
//        initParam.init2(departementsIDF);
//   
//        try {
//            initParam.getJDONREFIndexV3();
//        } catch (SQLException | IOException ex) {
//            Logger.getLogger(InitParameters.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    
    
    
}