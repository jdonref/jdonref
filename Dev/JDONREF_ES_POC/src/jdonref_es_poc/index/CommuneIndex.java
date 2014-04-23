package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.dao.CommuneDAO;
import jdonref_es_poc.entity.Commune;
import jdonref_es_poc.entity.MetaData;

/**
 *
 * @author Julien
 */
public class CommuneIndex
{
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
    
    public void addCommune(Commune commune) throws IOException
    {
        JsonObject data = commune.toJSONDocument();
        
        util.indexResource("commune", data.toString());
    }
    
    public void indexJDONREFCommune() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        CommuneDAO dao = new CommuneDAO();
        ResultSet rs = dao.getAllCommunes(connection);
//      creation de l'objet metaDateDep
        MetaData metaDateDep= new MetaData();
        metaDateDep.setIndex(util.index);
        metaDateDep.setType("commune");
        
        int i =0;
        String bulk ="";
        while(rs.next())
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" communes traitées");
            
//            Commune commune = new Commune(rs,new int[]{1,4,2,5});
            Commune commune = new Commune(rs,new int[]{1,2,3,4,5,6,7,8,9,10});
            
//            addCommune(commune);
            
//            creation de l'objet metaDateDep plus haut
            metaDateDep.setId(i);
            bulk += metaDateDep.toJSONMetaData().toString()+"\n"+commune.toJSONDocument().toString()+"\n";
            if(i%3000==0){
//                System.out.println("affichage du fichier bulk : i = "+i+"\n"+bulk);
                util.indexResourceBulk(bulk);
                bulk="";
            }
        }
        util.indexResourceBulk(bulk);
    }

    void indexJDONREFCommune(Commune[] communes) throws IOException
    {
        if (isVerbose())
            System.out.println("Communes");

//      creation de l'objet metaDateDep
        MetaData metaDateDep= new MetaData();
        metaDateDep.setIndex(util.index);
        metaDateDep.setType("commune");
        String bulk ="";
        int i=0;
        
        for(int j=0;j<communes.length;j++)
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" communes traitées");
            
            Commune commune = communes[j];
            
//            addCommune(commune);
            //            creation de l'objet metaDateDep plus haut
            metaDateDep.setId(j+1);
            bulk += metaDateDep.toJSONMetaData().toString()+"\n"+commune.toJSONDocument().toString()+"\n";
            if(i%100==0){
                util.indexResourceBulk(bulk);
                bulk="";
            }
        }
        util.indexResourceBulk(bulk);
    }
    
    

    
    
    
    
    
    
    
    
    
    
}