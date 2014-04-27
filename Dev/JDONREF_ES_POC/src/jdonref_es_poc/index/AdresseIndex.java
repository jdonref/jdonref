package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.dao.AdresseDAO;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.dao.VoieDAO;
import jdonref_es_poc.entity.Adresse;
import jdonref_es_poc.entity.MetaData;
import jdonref_es_poc.entity.Voie;

/**
 *
 * @author Julien
 */
public class AdresseIndex {
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
//      creation de l'objet metaDataVoie
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(util.index);
//      un type voie pour tous les departements  
        metaDataAdresse.setType("adresse");
              
        String bulk ="";
        int i =0;
        
        while(rs.next())
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" adresses traitées");
            
            Adresse adr = new Adresse(rs);
            
//            creation de l'objet metaDataVoie plus haut
            metaDataAdresse.setId(i+1);
            if (adr.numero!=null)
                bulk += metaDataAdresse.toJSONMetaData().toString()+"\n"+adr.toJSONDocument().toString()+"\n";
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

    void indexJDONREFAdressesDepartement(Adresse[] adresses, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : adresses");
        
        //int c =0;
//      creation de l'objet metaDataVoie
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(util.index);
//      un type voie pour tous les departements  
        metaDataAdresse.setType("adresse");
        String bulk ="";
        
        for(int i=0;i<adresses.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" adresses traitées");
            
            Adresse adr = adresses[i];
            
//            creation de l'objet metaDataVoie plus haut
            metaDataAdresse.setId(i+1);
            if (adr.numero!=null)
                bulk += metaDataAdresse.toJSONMetaData().toString()+"\n"+adr.toJSONDocument().toString()+"\n";
            if(i%1000==0){
                util.indexResourceBulk(bulk);
                bulk="";
            }
        }
        util.indexResourceBulk(bulk);
    }
}