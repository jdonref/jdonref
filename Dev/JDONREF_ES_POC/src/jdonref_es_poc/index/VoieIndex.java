package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.JsonObject;
import jdonref_es_poc.dao.VoieDAO;
import jdonref_es_poc.entity.MetaData;
import jdonref_es_poc.entity.Troncon;
import jdonref_es_poc.entity.Voie;

/**
 *
 * @author Julien
 */
public class VoieIndex {
    boolean verbose = false;
    boolean withGeometry = true;
    ElasticSearchUtil util;
    Connection connection;
    
    static int idVoie=0;
    static int idVoieTmp=0;
    int paquetsBulk=500;

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

    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
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
        JsonObject data = v.toJSONDocument(withGeometry);
        
        util.indexResource("voie", data.toString());
    }

    public void indexJDONREFVoiesDepartement(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        VoieDAO dao = new VoieDAO();
        ResultSet rs = dao.getAllVoiesOfDepartement(connection, dpt);
        
        // la liste des troncons
        TronconIndex tIndex = new TronconIndex();
        tIndex.setUtil(util);
        tIndex.setConnection(connection);
        tIndex.setVerbose(isVerbose());
        HashMap<String, ArrayList<Troncon>> mapVoieTron = tIndex.getAllTronconsByVoieByDpt(dpt);

//      creation de l'objet metaDataVoie
        MetaData metaDataVoie= new MetaData();
        metaDataVoie.setIndex(util.index);
        metaDataVoie.setType("voie");
              
        String bulk ="";
        int i =0;
        int lastIdBulk=idVoieTmp;

        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" voies traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" voies traitées");
            
            Voie v = new Voie(rs);
            
            // recuperer la geometrie de la voie a partir des troncons de la voie
            ArrayList<Troncon> listTronc = mapVoieTron.get(v.idvoie);
//            System.out.println("voieid ======"+v.idvoie);

            String geometrie = tIndex.getGeometrieVoie(listTronc);
            v.setGeometrie(geometrie);
            
                        
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(++idVoie);
            bulk += metaDataVoie.toJSONMetaData().toString()+"\n"+v.toJSONDocument(withGeometry).toString()+"\n";
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
        metaDataVoie.setType("voie");
        
        String bulk ="";
        int lastIdBulk=idVoieTmp;
               
        for(int i=0;i<voies.length;i++)
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" voies traitées");
            
            Voie v = voies[i];
            
//            addVoie(v);
            
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(++idVoie);
            bulk += metaDataVoie.toJSONMetaData().toString()+"\n"+v.toJSONDocument(withGeometry).toString()+"\n";
            if((idVoie-idVoieTmp)%paquetsBulk==0){
                System.out.println("voie : bulk pour les ids de "+(idVoie-paquetsBulk+1)+" à "+idVoie);
                util.indexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idVoie;
            }
        }
        if(!bulk.equals("")){
        System.out.println("voie : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
        util.indexResourceBulk(bulk);
        }
        idVoieTmp = idVoie;
    }
}