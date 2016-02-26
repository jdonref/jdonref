package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.CommuneDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Commune;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;

/**
 *
 * @author Julien
 */
public class CommuneIndex
{
    boolean verbose = false;
    boolean withGeometry = true;
    ElasticSearchUtil util;
    
    static int idCommune=0;
    static int idCommuneTmp=0;
    int paquetsBulk=500;
    
    String index = null;
    String [] dept = new String[0];

    public String[] getDept() {
        return dept;
    }

    public void setDept(String[] dept) {
        this.dept = dept;
    }

    protected static CommuneIndex instance = null;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public static CommuneIndex getInstance()
    {
        if (instance==null)
            instance = new CommuneIndex();
        return instance;
    }
    
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
    
    public Connection getConnection() throws SQLException {
        return JDONREFIndex.getInstance().getNewConnection();
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
        
        util.indexResource(index,"commune", data.toString());
    }
    
    HashSet<String> ids = new HashSet<String>();
    
    public void indexJDONREFCommune() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        CommuneDAO dao = new CommuneDAO();
        Connection connection = getConnection();
        ResultSet rs = dao.getAllCommunes(connection, getDept());

//      creation de l'objet metaDataCommune
        MetaData metaDataCommune= new MetaData();
        metaDataCommune.setIndex(index);
        metaDataCommune.setType("commune");
   
        ids.clear();
        
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
                        
            if (ids.contains(c.codeinsee+" "+c.commune))
                continue;
            ids.add(c.codeinsee+" "+c.commune);
            
//            creation de l'objet metaDataCommune plus haut
            metaDataCommune.setId(new Long(++idCommune));
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
        rs.close();
        connection.close();
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
        metaDataCommune.setIndex(index);
        metaDataCommune.setType("commune");

        String bulk ="";
        int lastIdBulk=idCommuneTmp;
               
        for(int i=0;i<communes.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" communes traitées");
            
            Commune c = communes[i];
            
//            creation de l'objet metaDataCommune plus haut
            metaDataCommune.setId(new Long(++idCommune));
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