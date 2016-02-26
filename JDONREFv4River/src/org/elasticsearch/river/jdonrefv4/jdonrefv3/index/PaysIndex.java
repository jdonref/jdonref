package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.PaysDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Pays;

/**
 *
 * @author Julien
 */
public class PaysIndex
{
    boolean verbose = false;
    ElasticSearchUtil util;
    
    static int idPays=0;
    static int idPaysTmp=0;
    int paquetsBulk=30;
    
    String index = null;
    
    boolean withGeometry = true;
    
    protected static PaysIndex instance = null;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public static PaysIndex getInstance()
    {
        if (instance==null)
            instance = new PaysIndex();
        return instance;
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

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Connection getConnection() throws SQLException {
        return JDONREFIndex.getInstance().getNewConnection();
    }

    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
    }
    
    public void addPays(Pays pays) throws IOException
    {
        JsonObject data = pays.toJSONDocument(withGeometry);
        util.indexResource(index,"pays", data.toString());
    }
    
    HashSet<String> ids = new HashSet<String>();
    
    public void indexJDONREFPays() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Pays");
        
        PaysDAO dao = new PaysDAO();
        Connection connection = getConnection();
        ResultSet rs = dao.getAllPays(connection);
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(index);
        metaDataDep.setType("pays");
        
        ids.clear();
        
        int i =0;
        String bulk ="";
        int lastIdBulk=idPaysTmp;
        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" pays traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" pays traités");
            
            Pays d = new Pays(rs);
            
            if (ids.contains(d.pays_sov_a3))
                continue;
            ids.add(d.pays_sov_a3);
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(new Long(++idPays));
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument(withGeometry).toString()+"\n";
            if((idPays-idPaysTmp)%paquetsBulk==0){
                System.out.println("pays : bulk pour les ids de "+(idPays-paquetsBulk+1)+" à "+idPays);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idPays;
            }
            i++;
        }
        rs.close();
        connection.close();
        if(!bulk.equals("")){
                System.out.println("pays : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idPays));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idPaysTmp = idPays;
    }
    
    public void indexJDONREFPays(Pays[] pays) throws IOException
    {
        if (isVerbose())
            System.out.println("Pays");
        
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(index);
        metaDataDep.setType("pays");

        String bulk ="";
        int lastIdBulk=idPaysTmp;        
        
        for(int i=0;i<pays.length;i++)
        {
            if (isVerbose() && i%30==1)
                System.out.println(i+" pays traités");
            Pays d = pays[i];
            
//            addDepartment(d);
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(new Long(++idPays));
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument(withGeometry).toString()+"\n";
            if((idPays-idPaysTmp)%paquetsBulk==0){
                System.out.println("pays : bulk pour les ids de "+(idPays-paquetsBulk+1)+" à "+idPays);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idPays;
            }
        }
        if(!bulk.equals("")){
        System.out.println("pays : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idPays));        
        if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idPaysTmp = idPays;
    }
}