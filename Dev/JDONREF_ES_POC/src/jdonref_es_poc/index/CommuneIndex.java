package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
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
    boolean withGeometry = true;
    ElasticSearchUtil util;
    Connection connection;
    
    static int idCommune=0;
    static int idCommuneTmp=0;
    int paquetsBulk=500;

    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
    }
    
    

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
        JsonObject data = commune.toJSONDocument(withGeometry);
        
        util.indexResource("commune", data.toString());
    }
    
    public void indexJDONREFCommune() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        CommuneDAO dao = new CommuneDAO();
        ResultSet rs = dao.getAllCommunes(connection);
//      creation de l'objet metaDataCommune
        MetaData metaDataCommune= new MetaData();
        metaDataCommune.setIndex(util.index);
        metaDataCommune.setType("commune");
   
        String bulk ="";
        int i =0;
        int lastIdBulk=idCommuneTmp;

        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" communes traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" communes traitées");

//            Commune c = new Commune(rs,new int[]{1,2,3,4,5,6,7,8,9,10,11});        
            Commune c = new Commune(rs);        
                        
//            creation de l'objet metaDataCommune plus haut
            metaDataCommune.setId(++idCommune);
            bulk += metaDataCommune.toJSONMetaData().toString()+"\n"+c.toJSONDocument(withGeometry).toString()+"\n";
            if((idCommune-idCommuneTmp)%paquetsBulk==0){
                System.out.println("commune : bulk pour les ids de "+(idCommune-paquetsBulk+1)+" à "+idCommune);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idCommune;
            }
            i++;
        }
        if(!bulk.equals("")){
        System.out.println("commune : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idCommune));        
        util.indexResourceBulk(bulk);
        }
        idCommuneTmp = idCommune;
    }
        

    void indexJDONREFCommune(Commune[] communes) throws IOException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        //int c =0;
//      creation de l'objet metaDataCommune
        MetaData metaDataCommune= new MetaData();
        metaDataCommune.setIndex(util.index);
        metaDataCommune.setType("commune");

        String bulk ="";
        int lastIdBulk=idCommuneTmp;
               
        for(int i=0;i<communes.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" communes traitées");
            
            Commune c = communes[i];
            
//            creation de l'objet metaDataCommune plus haut
            metaDataCommune.setId(++idCommune);
            bulk += metaDataCommune.toJSONMetaData().toString()+"\n"+c.toJSONDocument(withGeometry).toString()+"\n";
            if((idCommune-idCommuneTmp)%paquetsBulk==0){
                System.out.println("commune : bulk pour les ids de "+(idCommune-paquetsBulk+1)+" à "+idCommune);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idCommune;
            }
        }
        if(!bulk.equals("")){
        System.out.println("commune : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idCommune));        
        util.indexResourceBulk(bulk);
        }
        idCommuneTmp = idCommune;
    }
    
}