package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.dao.AdresseDAO;
import jdonref_es_poc.entity.Adresse;
import jdonref_es_poc.entity.MetaData;

/**
 *
 * @author Julien
 */
public class AdresseIndex {
    boolean verbose = false;
    ElasticSearchUtil util;
    Connection connection;

    static int idAdresse=0;
    static int idAdresseTmp=0;
    int paquetsBulk=1000;

    
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
        JsonObject data = adr.toJSONDocument();
        
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
            if (isVerbose() && i%1000==1)
                System.out.println(i+" adresses traitées");
            
            Adresse adr = new Adresse(rs);
            if (adr.numero!=null){
//            creation de l'objet metaDataAdresse plus haut
                metaDataAdresse.setId(++idAdresse);
                bulk += metaDataAdresse.toJSONMetaData().toString()+"\n"+adr.toJSONDocument().toString()+"\n"; 
                
// envoyé le bulk par paquet de 1000 à partir de idAdresseTmp 
// idAdresseTmp valeur de l'id de debut au moment de l'appel à cette methode
                if((idAdresse-idAdresseTmp)%paquetsBulk==0){
                    System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                    util.indexResourceBulk(bulk);
                    bulk="";
                    lastIdBulk=idAdresse;
                }
            }    
            i++;     
        }
        if(!bulk.equals("")){
        System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
        util.indexResourceBulk(bulk);
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
                bulk += metaDataAdresse.toJSONMetaData().toString()+"\n"+adr.toJSONDocument().toString()+"\n";
                if((idAdresse-idAdresseTmp)%paquetsBulk==0){
                    System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                    util.indexResourceBulk(bulk);
                    bulk="";
                    lastIdBulk=idAdresse;
                }
            }
        }
       if(!bulk.equals("")){
        System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
        util.indexResourceBulk(bulk);
       }
        idAdresseTmp = idAdresse;
       
    }
}