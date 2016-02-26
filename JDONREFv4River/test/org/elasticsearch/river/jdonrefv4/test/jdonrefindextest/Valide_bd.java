package org.elasticsearch.river.jdonrefv4.test.jdonrefindextest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.apache.lucene.analysis.FrequentTermsUtil;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.InitParameters;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.index.JDONREFIndex;
import org.junit.Test;

public class Valide_bd {

    String indexName = "indexName";
    ArrayList<String> aliasName = new ArrayList<>();
    boolean bouchon = false;
    boolean reindex = true;
    boolean verboseIndexation = true;
    boolean withGeometry = false;
    boolean withSwitchAlias = true;
    boolean parent = false;
    boolean nested = false;
    String url = "10.232.73.106:9200";  // PLD
//    String url = "10.213.93.85:9200"; // VM
//    String url = "localhost:9200";
//    String url = "plf.jdonrefv4.ppol.minint.fr";
    String connectionString = "jdbc:postgresql://10.232.73.78:5433/jdonref_ign";
    //String connectionString = "jdbc:postgresql://localhost:5432/JDONREF_IGN2";
    String user = "postgres";
    String passwd = "postgres";
    boolean restart = true;
    long millis = 0;

    public void setAliasName(ArrayList<String> aliasName) {
        this.aliasName = aliasName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public JDONREFIndex initJDONREFIndex(String connectionString, String password, String user, String indexName, ArrayList<String> aliasName, String url, boolean verboseIndexation, boolean restart, boolean withGeometry, boolean withSwitchAlias, boolean parent, boolean nested, long millis) {   
            
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
        jdonrefIndex.setConnectionString(connectionString);
        jdonrefIndex.setPassword(password);
        jdonrefIndex.setUser(user);
        return jdonrefIndex;
     } 
    
    public void getJDONREFIndex(JDONREFIndex jdonrefIndex, String[] listeDepartement,ArrayList<String> flags) throws SQLException, IOException, Exception{
        
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
    public void valideTestsAfterIndexation() throws Exception {
//        FrequentTermsUtil.setFilePath("./src/resources/analysis/word84.txt");
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
//                String[] listeDepartements = {"75","94", "93", "92"}; 
              
//                initParam.depts_intervalle("01","10"); // 3
//                String[] listeDepartements = initParam.getListeDepartement(); //3
                
                //on commente ce qu'on veux indexer !!
                ArrayList<String> flags = new ArrayList<>();
//                flags.add("DEPARTEMENT");
//                flags.add("COMMUNE");
//                flags.add("VOIE");
//                flags.add("ADRESSE");
//                flags.add("POIZON");
//                flags.add("PAYS");
                flags.add("TRONCON");

                setIndexName("jdonref_idf");
                aliasName.clear();
                aliasName.add("jdonref");
                aliasName.add("jdonref_idf"); 
                setAliasName(aliasName);
                
                JDONREFIndex jdonrefIndex = initJDONREFIndex(connectionString, user, passwd, indexName, aliasName, url, verboseIndexation, restart, withGeometry, withSwitchAlias, parent,nested, millis);
                getJDONREFIndex(jdonrefIndex, listeDepartements ,flags);

            } catch (SQLException | IOException ex) {
                Logger.getLogger(Valide_bd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}