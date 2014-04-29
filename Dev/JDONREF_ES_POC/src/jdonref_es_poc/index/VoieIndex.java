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
    
    static int idVoie=0;
    static int idVoieTmp=0;
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
        int lastIdBulk=idVoieTmp-1;

        
        while(rs.next())
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" voies traitées");
            
            Voie v = new Voie(rs);
                        
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(++idVoie);
            bulk += metaDataVoie.toJSONMetaData().toString()+"\n"+v.toJSONDocument().toString()+"\n";
            if((idVoie-idVoieTmp)%paquetsBulk==0){
                System.out.println("voie : bulk pour les ids de "+(idVoie-paquetsBulk+1)+" à "+idVoie);
                util.indexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idVoie;
            }
            i++;
//            addVoie(v);
        }
        if(!bulk.equals("")){
        System.out.println("voie : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
        util.indexResourceBulk(bulk);
        }
        idVoieTmp = idVoie;
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
        int lastIdBulk=idVoieTmp-1;
               
        for(int i=0;i<voies.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" voies traitées");
            
            Voie v = voies[i];
            
//            addVoie(v);
            
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(++idVoie);
            bulk += metaDataVoie.toJSONMetaData().toString()+"\n"+v.toJSONDocument().toString()+"\n";
            if((idVoie-idVoieTmp)%paquetsBulk==0){
                System.out.println("voie : bulk pour les ids de "+(idVoie-paquetsBulk+1)+" à "+idVoie);
                util.indexResourceBulk(bulk);
                bulk="";
            }
        }
        if(!bulk.equals("")){
        System.out.println("voie : bulk voie pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
        util.indexResourceBulk(bulk);
        }
        idVoieTmp = idVoie;
    }
}