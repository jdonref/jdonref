package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.dao.VoieDAO;
import jdonref_es_poc.entity.MetaData;
import jdonref_es_poc.entity.Voie;

/**
 *
 * @author Julien
 */
public class VoieIndex {
    boolean verbose = false;
    ElasticSearchUtil util;
    Connection connection;

    public ElasticSearchUtil getUtil() {
        return util;
    }

    public void setUtil(ElasticSearchUtil util) {
        this.util = util;
    }
    
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void addVoie(Voie v)
            throws IOException
    {
        JsonObject data = v.toJSONDocument();
        
        util.indexResource("voie", data.toString());
    }

    public void indexJDONREFVoiesDepartement(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        VoieDAO dao = new VoieDAO();
        ResultSet rs = dao.getAllVoiesOfDepartement(connection, dpt);
//      creation de l'objet metaDataVoie
        MetaData metaDataVoie= new MetaData();
        metaDataVoie.setIndex(util.index);
//      un type voie pour tous les departements  
        metaDataVoie.setType("voie");
//        metaDataVoie.setType("voie_"+dpt);
              
        String bulk ="";
        int i =0;
        
        while(rs.next())
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" voies traitées");
            
            Voie v = new Voie(rs);
                        
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(i+1);
            bulk += metaDataVoie.toJSONMetaData().toString()+"\n"+v.toJSONDocument().toString()+"\n";
            if(i%1000==0){
//                System.out.println("affichage du fichier bulk : i = "+i+"\n"+bulk);
                util.indexResourceBulk(bulk);
                bulk="";
            }
            i++;
//            addVoie(v);
        }
        util.indexResourceBulk(bulk);
    }

    void indexJDONREFVoiesDepartement(Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        //int c =0;
//      creation de l'objet metaDataVoie
        MetaData metaDataVoie= new MetaData();
        metaDataVoie.setIndex(util.index);
//      un type voie pour tous les departements  
        metaDataVoie.setType("voie");
//        metaDataVoie.setType("voie_"+dpt);
        String bulk ="";
        
        for(int i=0;i<voies.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" voies traitées");
            
            Voie v = voies[i];
            
//            addVoie(v);
            
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(i+1);
            bulk += metaDataVoie.toJSONMetaData().toString()+"\n"+v.toJSONDocument().toString()+"\n";
            if(i%1000==0){
                util.indexResourceBulk(bulk);
                bulk="";
            }
        }
        util.indexResourceBulk(bulk);
    }
}