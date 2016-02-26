package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.AdresseDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.VoieAdr;

/**
 *
 * @author Julien
 */
public class VoieAdrIndex {
    boolean verbose = false;
    boolean withGeometry = true;
    ElasticSearchUtil util;
    
    static int idVoie=0;
    static int idVoieTmp=0;
    int paquetsBulk=200;
    
    String index = null;
    
    HashMap<String, VoieAdr> map_idIndexVoieES =  new HashMap<>();

    public HashMap<String, VoieAdr> getMap_idIndexVoieES() {
        return map_idIndexVoieES;
    }

    public void setMap_idIndexVoieES(HashMap<String, VoieAdr> map_idIndexVoieES) {
        this.map_idIndexVoieES = map_idIndexVoieES;
    }
    
    String source = "bdd"; // bdd ou csv
    
    void setSource(String string) {
        source = string;
    }

    protected static VoieAdrIndex instance = null;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public static VoieAdrIndex getInstance()
    {
//        idVoie=0;
//        idVoieTmp=0;   
        if (instance==null)
            instance = new VoieAdrIndex();
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
    
    public void indexJDONREFVoiesDepartement(String dpt) throws IOException, SQLException, Exception
    {
        if (source.equals("bdd"))
            indexJDONREFVoiesDepartementBDD(dpt);
        else if (source.equals("csv"))
            //indexJDONREFVoiesDepartementCSV(dpt);
            throw(new Exception("Not implemented yet"));
    }
    
    public int indexVoie = 0;
    
    public void storeVoie(VoieAdr voie)
    {
        String id = voie.ligne4;
        
        if (map_idIndexVoieES.get(id)==null)
        {
            voie.idvoie = indexVoie++;
            map_idIndexVoieES.put(id, voie);
        }
    }
    
    public void indexJDONREFVoiesDepartementBDD(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies parentes");
        
        AdresseDAO dao = new AdresseDAO();
        Connection connection = getConnection();
        ResultSet rs = dao.getAllAdressesOfDepartement(connection, dpt);
        
        int i = 0;
        
        while(rs.next())
        {
            if (isVerbose() && i%5000==0 && i>0)
                System.out.println(dpt+": "+i+" voies parentes importées");
            
            VoieAdr v = new VoieAdr(rs);
            storeVoie(v);
            
            i++;
        }
        rs.close();
        connection.close();
        
        idVoieTmp = idVoie;
    }
    
    public void addVoie(VoieAdr v)
            throws IOException
    {
        JsonObject data = v.toJSONDocument(withGeometry);
        
        util.indexResource(index,"voie_adr", data.toString());
    }
    
    public void indexJDONREFVoiesMap()
    {
        Collection<VoieAdr> voies = map_idIndexVoieES.values();
        Iterator<VoieAdr> ite = voies.iterator();

//      creation de l'objet metaDataVoie
        MetaData metaDataVoie= new MetaData();
        metaDataVoie.setIndex(index);
        metaDataVoie.setType("voie_adr");
              
        StringBuilder bulk = new StringBuilder();
        
        int i = 0;
        int count = 0;
        while(ite.hasNext())
        {
            VoieAdr voie = ite.next();
            metaDataVoie.setId(new Long(voie.idvoie));
            bulk.append(metaDataVoie.toJSONMetaData().toString()).append("\n").append(voie.toJSONDocument(withGeometry).toString()).append("\n");
            count++;
            i++;
            if (i==paquetsBulk)
            {
                System.out.println("voie parentes : bulk pour les ids de "+(count-paquetsBulk+1)+" à "+count);
                if (!isVerbose())
                    util.indexResourceBulk(bulk.toString());
                else
                    util.showIndexResourceBulk(bulk.toString());
                bulk.setLength(0);
                i=0;
            }
        }
        
        if(bulk.length()!=0)
        {
            System.out.println("voie parentes : bulk pour les ids jusque "+count);
            if (!isVerbose())
                util.indexResourceBulk(bulk.toString());
            else
                util.showIndexResourceBulk(bulk.toString());
        }
    }
}