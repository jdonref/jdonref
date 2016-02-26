/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticsearch.river.jdonrefv4.test.jdonrefindextest;



import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.apache.lucene.analysis.FrequentTermsUtil;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.index.JDONREFIndex;
import org.junit.Test;

public class Valide_json {

    private String RESOURCES_PATH = "C:\\Users\\moquetju\\Desktop\\ban";
    private String DEPT_FILE_NAME = "BAN_licence_gratuite_repartage_";

    String indexName = "indexName";
    ArrayList<String> aliasName = new ArrayList<>();
    boolean bouchon = false;
    boolean reindex = true;
    boolean verboseIndexation = true;
    boolean withGeometry = false;
    boolean withSwitchAlias = true;
    boolean parent = true;
    boolean nested = false;
    boolean csv = true;
    String url = "10.213.93.85:9200";
    boolean restart = true;
    long millis = 0;

    public void setAliasName(ArrayList<String> aliasName) {
        this.aliasName = aliasName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public JDONREFIndex initJDONREFIndex(String indexName, ArrayList<String> aliasName, String url, boolean verboseIndexation, boolean restart, boolean withGeometry, boolean withSwitchAlias, boolean parent, boolean nested, boolean csv, long millis) {   
            
        JDONREFIndex jdonrefIndex = JDONREFIndex.getInstance();
        jdonrefIndex.setParent(parent);
        jdonrefIndex.setNested(nested);
        jdonrefIndex.setCsv(csv);
        jdonrefIndex.setWithSwitchAlias(withSwitchAlias);
        jdonrefIndex.setIndex(indexName);
        jdonrefIndex.setAliasL(aliasName);
        jdonrefIndex.setUrl(url);
        jdonrefIndex.setVerbose(verboseIndexation);
        jdonrefIndex.setRestart(restart);
        jdonrefIndex.setWithGeometry(withGeometry);
        if (millis!=0)
            jdonrefIndex.setMillis(millis);
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
    

    private static FilenameFilter csvFileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                        return name.endsWith(".csv");
                }
    };
    
    @Test
    public void valideTestsAfterIndexation() throws Exception {
        //FrequentTermsUtil.setFilePath("./src/resources/analysis/word84.txt");
        if(reindex){
            try {
                File repertoire = new File(RESOURCES_PATH);
                
//                String[] listeDepts = {"all"};
                String[] listeDepts = {"75"}; //, "94", "93", "95", "92", "91", "78", "77"};
                
                String[] listeDepartements = null;
                if(Arrays.asList(listeDepts).contains("all")){
                    String[] listeDepFile=repertoire.list(csvFileFilter);
                    String[] listeDeptTmp = new String[listeDepFile.length];
                    for(int i=0; i<listeDepFile.length;i++){
                        String l = RESOURCES_PATH+listeDepFile[i];
                        listeDeptTmp[i]= l;
                    }
                    listeDepartements = listeDeptTmp;
                    
                }else{
                    String[] listeDeptTmp = new String[listeDepts.length];
                    for(int i=0; i<listeDepts.length;i++){
                        String l = RESOURCES_PATH+DEPT_FILE_NAME+listeDepts[i]+".csv";
                        listeDeptTmp[i]= l;
                    }
                    listeDepartements = listeDeptTmp;
                }

                //on commente ce qu'on veux indexer !!
                ArrayList<String> flags = new ArrayList<>();
                flags.add("DEPARTEMENT");
                flags.add("COMMUNE");
                //flags.add("VOIE");
                //flags.add("ADRESSE");
                flags.add("POIZON");
                flags.add("PAYS");
                flags.add("TRONCON");

                setIndexName("jdonref_idf");
                aliasName.clear();
                aliasName.add("jdonref");
                aliasName.add("jdonref_idf"); 
                setAliasName(aliasName);
                
                JDONREFIndex jdonrefIndex = initJDONREFIndex(indexName, aliasName, url, verboseIndexation, restart, withGeometry, withSwitchAlias, parent,nested, csv, millis);
                getJDONREFIndex(jdonrefIndex, listeDepartements ,flags);

            } catch (SQLException | IOException ex) {
                Logger.getLogger(Valide_bd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    

    
    
    
    
}