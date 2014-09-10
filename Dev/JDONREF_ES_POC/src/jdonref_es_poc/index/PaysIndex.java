package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.dao.PaysDAO;
import jdonref_es_poc.entity.MetaData;
import jdonref_es_poc.entity.Pays;

/**
 *
 * @author Julien
 */
public class PaysIndex
{
    boolean verbose = false;
    ElasticSearchUtil util;
    Connection connection;
    
    static int idPays=0;
    static int idPaysTmp=0;
    int paquetsBulk=30;
    
    boolean withGeometry = true;
    
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
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
        util.indexResource("pays", data.toString());
    }
    
    public void indexJDONREFPays() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Pays");
        
        PaysDAO dao = new PaysDAO();
        ResultSet rs = dao.getAllPays(connection);
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(util.index);
        metaDataDep.setType("pays");
        
        int i =0;
        String bulk ="";
        int lastIdBulk=idPaysTmp;
        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" pays traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" pays traités");
            
            Pays d = new Pays(rs);
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(++idPays);
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument(withGeometry).toString()+"\n";
            if((idPays-idPaysTmp)%paquetsBulk==0){
                System.out.println("pays : bulk pour les ids de "+(idPays-paquetsBulk+1)+" à "+idPays);
                util.indexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idPays;
            }
            i++;
        }
        if(!bulk.equals("")){
        System.out.println("pays : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idPays));        
        util.indexResourceBulk(bulk);
        }
        idPaysTmp = idPays;
    }
    
    public void indexJDONREFPays(Pays[] pays) throws IOException
    {
        if (isVerbose())
            System.out.println("Pays");
        
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(util.index);
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
            metaDataDep.setId(++idPays);
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument(withGeometry).toString()+"\n";
            if((idPays-idPaysTmp)%paquetsBulk==0){
                System.out.println("pays : bulk pour les ids de "+(idPays-paquetsBulk+1)+" à "+idPays);
                util.indexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idPays;
            }
        }
        if(!bulk.equals("")){
        System.out.println("pays : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idPays));        
        util.indexResourceBulk(bulk);
        }
        idPaysTmp = idPays;
    }
}