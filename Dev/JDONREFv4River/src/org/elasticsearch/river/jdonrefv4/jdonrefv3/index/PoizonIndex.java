package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.PoizonDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Poizon;




/**
 *
 * @author akchana
 */


public class PoizonIndex {
    boolean withGeometry = true;
    boolean verbose = false;
    ElasticSearchUtil util;
    Connection connection;
    
    static int idPoizon=0;
    static int idPoizonTmp=0;
    int paquetsBulk=100;

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

    
    public String getDatForm(Date d) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }
    
    //attribut = t0 forcement inferieur au moment d'execution 
    //egal si insertion en meme temps que l'execution
    public Date dateOfFirst(String object, String attribut) throws SQLException{ 
        Date lastUpdate = new Date();   
        PoizonDAO dao = new PoizonDAO();
        ResultSet rsDatesT0 = dao.getDateT0AllPoizon(connection);
        while(rsDatesT0.next()){
            Date date = rsDatesT0.getTimestamp(1);
            String output = util.search(object , URLEncoder.encode(attribut+":[\""+getDatForm(date)+"\" TO *]"));

            if(!output.contains("\"hits\":{\"total\":0,")) // TODO : convertir en JSON
                return lastUpdate;
            lastUpdate = date; 
        } 
        return lastUpdate;
    }

    
    public void indexJDONREFPoizon() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Poizon");
        
        String lastUpdate = getDatForm(dateOfFirst("poizon","t0"));
        
        PoizonDAO dao = new PoizonDAO();
        ResultSet rs = dao.getAllPoizon(connection,lastUpdate);
//      creation de l'objet metaDataPoizon
        MetaData metaDataPoizon= new MetaData();
        metaDataPoizon.setIndex(util.index);
        metaDataPoizon.setType("poizon");
        
        int i =0;
        String bulk ="";
        int lastIdBulk=idPoizonTmp;
        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" poizon traités");
            
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" poizon traités");
            
            Poizon p = new Poizon(rs);
            
//            creation de l'objet metaDataPoizon plus haut
//            metaDataPoizon.setId(++idPoizon);
            metaDataPoizon.setId(Integer.parseInt(p.poizon_id1));
            ++idPoizon;
            bulk += metaDataPoizon.toJSONMetaData().toString()+"\n"+p.toJSONDocument(withGeometry).toString()+"\n";
            if((idPoizon-idPoizonTmp)%paquetsBulk==0){
                System.out.println("poizon : bulk pour les ids de "+(idPoizon-paquetsBulk+1)+" à "+idPoizon);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idPoizon;
            }
            i++;
        }
        if(!bulk.equals("")){
                System.out.println("poizon : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idPoizon));
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idPoizonTmp = idPoizon;
    }
    

    

    
}