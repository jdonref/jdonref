package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.VoieDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.VoieDAO_csv;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Troncon;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Voie;
import static org.elasticsearch.river.jdonrefv4.jdonrefv3.index.AdresseIndex.SEPARATOR;

/**
 *
 * @author Julien
 */
public class VoieIndex {
    boolean verbose = false;
    boolean withGeometry = true;
    ElasticSearchUtil util;
    
    static int idVoie=0;
    static int idVoieTmp=0;
    int paquetsBulk=200;
    
    String index = null;
    
    HashMap<String, Integer> map_idIndexVoieES =  new HashMap<>();

    public HashMap<String, Integer> getMap_idIndexVoieES() {
        return map_idIndexVoieES;
    }

    public void setMap_idIndexVoieES(HashMap<String, Integer> map_idIndexVoieES) {
        this.map_idIndexVoieES = map_idIndexVoieES;
    }
    
    String source = "bdd"; // bdd ou csv
    
    void setSource(String string) {
        source = string;
    }

    protected static VoieIndex instance = null;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public static VoieIndex getInstance()
    {
//        idVoie=0;
//        idVoieTmp=0;   
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
    
    public Connection getConnection() throws SQLException {
        return JDONREFIndex.getInstance().getNewConnection();
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
        if (source.equals("bdd"))
            indexJDONREFVoiesDepartementBDD(dpt);
        else if (source.equals("csv"))
            indexJDONREFVoiesDepartementCSV(dpt);
    }
    
    HashSet<String> ids = new HashSet<String>();
    
    public void indexJDONREFVoiesDepartementBDD(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        VoieDAO dao = new VoieDAO();
        Connection connection = getConnection();
        ResultSet rs = dao.getAllVoiesOfDepartement(connection, dpt);
        
        // la liste des troncons
        TronconIndex tIndex = new TronconIndex();
        tIndex.setUtil(util);
        tIndex.setVerbose(isVerbose());
        HashMap<String, ArrayList<Troncon>> mapVoieTron = tIndex.getAllTronconsByVoieByDpt(dpt);

//      creation de l'objet metaDataVoie
        MetaData metaDataVoie= new MetaData();
        metaDataVoie.setIndex(index);
        metaDataVoie.setType("voie");
        
        ids.clear();
              
        StringBuilder bulk = new StringBuilder();
        int i =0;
        int lastIdBulk=idVoieTmp;

        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(dpt+": "+i+" voies traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(dpt+": "+i+" voies traitées");
            
            Voie v = new Voie(rs);
            
            if (ids.contains(v.idvoie))
                continue;
            ids.add(v.idvoie);
            
            // recuperer la geometrie de la voie a partir des troncons de la voie
            ArrayList<Troncon> listTronc = mapVoieTron.get(v.idvoie);
//            System.out.println("voieid ======"+v.idvoie);

            String geometrie = tIndex.getGeometrieVoie(listTronc);
            v.setGeometrie(geometrie);
            
                        
//            creation de l'objet metaDataVoie plus haut
            metaDataVoie.setId(new Long(++idVoie));
            map_idIndexVoieES.put(v.idvoie, idVoie);
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
        connection.close();
        if(bulk.length()!=0){
                System.out.println("voie : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk.toString());
                else
                    util.showIndexResourceBulk(bulk.toString());
        }
        idVoieTmp = idVoie;
    }
    
    public void indexJDONREFVoiesDepartementCSV(String dptFile) throws IOException, SQLException
    {
        BufferedReader br = null;
        CSVReader csvReader = null;
        try {
            String dpt = dptFile.substring(dptFile.lastIndexOf("_")+1, dptFile.lastIndexOf("."));
            if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dptFile), "UTF8"));
            csvReader = new CSVReader(br, SEPARATOR);
            VoieDAO_csv csv = new VoieDAO_csv(csvReader);
            this.setMap_idIndexVoieES(csv.getMap_IdIndexVoieES());
            
            // creation de l'objet metaDataVoie
            MetaData metaDataVoie= new MetaData();
            metaDataVoie.setIndex(index);
            metaDataVoie.setType("voie");

            StringBuilder bulk = new StringBuilder();
            int lastIdBulk=idVoieTmp;

            for(int i=0;i<csv.datas.size();i++)
            {
                if(paquetsBulk == 1) System.out.println(dpt+": "+i+" voies traités");
                if (isVerbose() && i%paquetsBulk==1)
                    System.out.println(dpt+": "+i+" voies traitées");

                Voie v = new Voie(csv.datas.get(i));

                // recuperer la geometrie de la voie a partir des troncons de la voie
//                ArrayList<Troncon> listTronc = mapVoieTron.get(v.idvoie);
    //            System.out.println("voieid ======"+v.idvoie);

  //              String geometrie = tIndex.getGeometrieVoie(listTronc);
//                v.setGeometrie(geometrie);


    //            creation de l'objet metaDataVoie plus haut
                metaDataVoie.setId(Integer.parseInt(v.idvoie));
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
    //            addVoie(v);
            }
            if(bulk.length()!=0){
                    System.out.println("voie : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idVoie));        
                    if (!isVerbose())
                        util.indexResourceBulk(bulk.toString());
                    else
                        util.showIndexResourceBulk(bulk.toString());
            }
            idVoieTmp = idVoie;
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AdresseIndex.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                csvReader.close();
            } catch (IOException ex) {
                Logger.getLogger(AdresseIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            metaDataVoie.setId(new Long(++idVoie));
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