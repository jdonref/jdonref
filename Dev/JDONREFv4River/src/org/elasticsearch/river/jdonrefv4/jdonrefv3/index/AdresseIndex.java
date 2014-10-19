package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.AdresseDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Adresse;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;

/**
 *
 * @author Julien
 */
public class AdresseIndex {
    boolean verbose = false;
    boolean withGeometry = true;
    ElasticSearchUtil util;
    Connection connection;

    static int idAdresse=0;
    static int idAdresseTmp=0;
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
    
    public void addAdresse(Adresse adr)
            throws IOException
    {
        JsonObject data = adr.toJSONDocument(withGeometry);
        
        util.indexResource("adresse", data.toString());
    }


    public void indexJDONREFAdressesDepartement(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : adresses");
        
        AdresseDAO dao = new AdresseDAO();
        ResultSet rs = dao.getAllAdressesOfDepartement(connection, dpt);
//      creation de l'objet metaDataAdresse
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(util.index);
        metaDataAdresse.setType("adresse");
              
        String bulk ="";
        int i =0;
        int lastIdBulk=idAdresseTmp;

        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" adresses traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" adresses traitées");
            
            Adresse adr = new Adresse(rs);
            if (adr.numero!=null){
//            creation de l'objet metaDataAdresse plus haut
                metaDataAdresse.setId(++idAdresse);
                bulk += metaDataAdresse.toJSONMetaData().toString()+"\n"+adr.toJSONDocument(withGeometry).toString()+"\n"; 
                
// envoyé le bulk par paquet de 1000 à partir de idAdresseTmp 
// idAdresseTmp valeur de l'id de debut au moment de l'appel à cette methode
                if((idAdresse-idAdresseTmp)%paquetsBulk==0){
                    System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                    if (!isVerbose())
                        util.indexResourceBulk(bulk);
                    else
                        util.showIndexResourceBulk(bulk);
                    bulk="";
                    lastIdBulk=idAdresse;
                }
            }    
            i++;     
        }
        if(!bulk.equals("")){
                System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idAdresseTmp = idAdresse;
    }

    void indexJDONREFAdressesDepartement(Adresse[] adresses, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : adresses");
        
        //int c =0;
//      creation de l'objet metaDataAdresse
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(util.index);
        metaDataAdresse.setType("adresse");
        
        String bulk ="";
       int lastIdBulk=idAdresseTmp;
       
        for(int i=0;i<adresses.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" adresses traitées");
            
            Adresse adr = adresses[i]; 
            if (adr.numero!=null){
//            creation de l'objet metaDataAdresse plus haut
                metaDataAdresse.setId(++idAdresse);                
                bulk += metaDataAdresse.toJSONMetaData().toString()+"\n"+adr.toJSONDocument(withGeometry).toString()+"\n";
                if((idAdresse-idAdresseTmp)%paquetsBulk==0){
                    System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                    if (!isVerbose())
                        util.indexResourceBulk(bulk);
                    else
                        util.showIndexResourceBulk(bulk);
                    bulk="";
                    lastIdBulk=idAdresse;
                }
            }
        }
       if(!bulk.equals("")){
                System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
       }
        idAdresseTmp = idAdresse;
       
    }
}