package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.VoieDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Troncon;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Voie;

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
    int paquetsBulk=200;
    
    String index = null;

    protected static VoieIndex instance = null;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public static VoieIndex getInstance()
    {
        if (instance==null)
            instance = new VoieIndex();
        return instance;
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
        
        util.indexResource(index,"voie", data.toString());
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
        metaDataVoie.setIndex(index);
        metaDataVoie.setType("voie");
              
        StringBuilder bulk = new StringBuilder();
        int i =0;
        int lastIdBulk=idVoieTmp;

        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(dpt+": "+i+" voies traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(dpt+": "+i+" voies traitées");
            
            Voie v = new Voie(rs);
            
            // recuperer la geometrie de la voie a partir des troncons de la voie
            ArrayList<Troncon> listTronc = mapVoieTron.get(v.idvoie);
//            System.out.println("voieid ======"+v.idvoie);

            String geometrie = tIndex.getGeometrieVoie(listTronc);
            v.setGeometrie(geometrie);
            
                        
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(++idVoie);
            bulk.append(metaDataVoie.toJSONMetaData().toString()).append("\n").append(v.toJSONDocument(withGeometry).toString()).append("\n");
            if((idVoie-idVoieTmp)%paquetsBulk==0){
                System.out.println("voie : bulk pour les ids de "+(idVoie-paquetsBulk+1)+" à "+idVoie);
                if (!isVerbose())
                    util.indexResourceBulk(bulk.toString());
                else
                    util.showIndexResourceBulk(bulk.toString());
                bulk.setLength(0);
                lastIdBulk=idVoie;
            }
            
            i++;
//            addVoie(v);
        }
        rs.close();
        if(bulk.length()!=0){
                System.out.println("voie : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk.toString());
                else
                    util.showIndexResourceBulk(bulk.toString());
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
        metaDataVoie.setIndex(index);
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
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idVoie;
            }
        }
        if(!bulk.equals("")){
                System.out.println("voie : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idVoieTmp = idVoie;
    }
}