package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.TronconDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Adresse;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Troncon;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Voie;

/**
 * 
 * @author Julien
 */
public class TronconIndex
{
     boolean verbose = false;
     boolean withGeometry = true;
ElasticSearchUtil util;
    Connection connection;

    static int idTroncon=0;
    static int idTronconTmp=0;
    int paquetsBulk=500;
    
    String index = null;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    protected static TronconIndex instance = null;
    
    public static TronconIndex getInstance()
    {
        if (instance==null)
            instance = new TronconIndex();
        return instance;
    }
    
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
    
    private void addAdresse(Adresse a)
            throws IOException
    {
        JsonObject data = a.toJSONDocument(withGeometry);
        
        util.indexResource(index,"troncon", data.toString());
    }
    
    
    
    public void indexJDONREFTronconsDepartement(Voie[] voies,String dpt,String side) throws IOException
    {
        int c =0;
        
        for(int i=0;i<voies.length;i++)
        {
            Voie v = voies[i];
            
            int voi_min_numero = v.min_numero;
            int voi_max_numero = v.max_numero;
            
            if (voi_min_numero!=0 || voi_max_numero!=0)
            {
                if (voi_min_numero > voi_max_numero)
                {
                    int temp = voi_max_numero;
                    voi_max_numero = voi_min_numero;
                    voi_min_numero = temp;
                }
                
                if ( voi_min_numero%2 != voi_max_numero%2)
                {
                    if (side.equals("gauches"))
                    {
                        for(int j=voi_min_numero;j<=voi_max_numero;j++,c++)
                        {
                            Adresse adresse = new Adresse(v,Integer.toString(j),"");
                    
                            addAdresse(adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
                else
                {
                    if (side.equals("gauches") && voi_min_numero%2==0 ||
                        side.equals("droits")  && voi_min_numero%2==1)
                    {
                        for(int j=voi_min_numero;j<=voi_max_numero;j+=2,c++)
                        {
                            Adresse adresse = new Adresse(v,Integer.toString(j),"");
                        
                            addAdresse(adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
            }
        }
    }
    
    public void indexJDONREFTronconsDepartement(ResultSet rs, String side, String dpt) throws IOException, SQLException
    {
        int c =0;
        
        while(rs.next())
        {
            String voi_id = rs.getString(1);
            String voi_nom = rs.getString(2);
            String com_code_insee = rs.getString(3);
            String com_code_postal = rs.getString(4);
            String voi_type_voie = rs.getString(5);
            String voi_lbl = rs.getString(6);
            int voi_min_numero = rs.getInt(7);
            int voi_max_numero = rs.getInt(8);
            String com_nom = rs.getString(9);
            
            if (voi_min_numero!=0 || voi_max_numero!=0)
            {
                if (voi_min_numero > voi_max_numero)
                {
                    int temp = voi_max_numero;
                    voi_max_numero = voi_min_numero;
                    voi_min_numero = temp;
                }
                
                if (voi_min_numero%2 != voi_max_numero%2)
                {
                    if (side.equals("gauches"))
                    {
                        for(int i=voi_min_numero;i<=voi_max_numero;i++,c++)
                        {
                            Adresse adresse = new Adresse(rs,Integer.toString(i),"",0,0);
                    
                            addAdresse(adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
                else
                {
                    if (side.equals("gauches") && voi_min_numero%2==0 ||
                        side.equals("droits")  && voi_min_numero%2==1)
                    {
                        for(int i=voi_min_numero;i<=voi_max_numero;i+=2,c++)
                        {
                            Adresse adresse = new Adresse(rs,Integer.toString(i),"",0,0);
                            
                            addAdresse(adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
            }
        }
    }
    
    public void indexJDONREFTronconsGaucheDepartement(Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons gauche");
        
        indexJDONREFTronconsDepartement(voies, dpt,"gauches");
    }
    
    public void indexJDONREFTronconsGaucheDepartement(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons gauche");
        
        TronconDAO dao = new TronconDAO();
        
        ResultSet rs = dao.getAllTronconsGaucheOfDepartment(connection,dpt);
        indexJDONREFTronconsDepartement(rs, "gauches", dpt);
    }
    
    
    public void indexJDONREFTronconsDroitDepartement(Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons droits");
        
        indexJDONREFTronconsDepartement(voies, dpt, "droits");
    }
    
    public void indexJDONREFTronconsDroitDepartement(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons droits");
        
        TronconDAO dao = new TronconDAO();
        
        ResultSet rs = dao.getAllTronconsDroitOfDepartment(connection,dpt);
        indexJDONREFTronconsDepartement(rs, "droits", dpt);
    }

    public void indexJDONREFTronconsDepD(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons droit");
        
        TronconDAO dao = new TronconDAO();
//        ResultSet rs = dao.getAllTronconsByDep(connection, dpt);    
        ResultSet rs = dao.getAllTronconsByDepD(connection, dpt);    
//      creation de l'objet metaDataTroncon
        MetaData metaDataTroncon= new MetaData();
        metaDataTroncon.setIndex(index);
        metaDataTroncon.setType("troncon");
              
        String bulk ="";
        int i =0;
        int lastIdBulk=idTronconTmp;

            while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" troncons droit traités");    
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" troncons droit traités");
            
            Troncon tr = new Troncon(rs,0);
                        
//            creation de l'objet metaDataTroncon plus haut
            metaDataTroncon.setId(++idTroncon);
            bulk += metaDataTroncon.toJSONMetaData().toString()+"\n"+tr.toJSONDocument(withGeometry).toString()+"\n";
            if((idTroncon-idTronconTmp)%paquetsBulk==0){
                System.out.println("troncons droit : bulk pour les ids de "+(idTroncon-paquetsBulk+1)+" à "+idTroncon);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idTroncon;
            }
            i++;
        }
        rs.close();
        if(!bulk.equals("")){
                System.out.println("troncons droit : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idTroncon));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idTronconTmp = idTroncon;
    }
    public void indexJDONREFTronconsDepG(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons gauche");
        
        TronconDAO dao = new TronconDAO();
//        ResultSet rs = dao.getAllTronconsByDep(connection, dpt);    
        ResultSet rs = dao.getAllTronconsByDepG(connection, dpt);    
//      creation de l'objet metaDataTroncon
        MetaData metaDataTroncon= new MetaData();
        metaDataTroncon.setIndex(index);
        metaDataTroncon.setType("troncon");
              
        String bulk ="";
        int i =0;
        int lastIdBulk=idTronconTmp;

            while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" troncons gauche traités");     
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" troncons gauche traités");
            
            Troncon tr = new Troncon(rs,0);
                        
//            creation de l'objet metaDataTroncon plus haut
            metaDataTroncon.setId(++idTroncon);
            bulk += metaDataTroncon.toJSONMetaData().toString()+"\n"+tr.toJSONDocument(withGeometry).toString()+"\n";
            if((idTroncon-idTronconTmp)%paquetsBulk==0){
                System.out.println("troncons gauche : bulk pour les ids de "+(idTroncon-paquetsBulk+1)+" à "+idTroncon);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idTroncon;
            }
            i++;
        }
        rs.close();
        if(!bulk.equals("")){
                System.out.println("troncons gauche : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idTroncon));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idTronconTmp = idTroncon;
    }

    void add(HashMap<String, ArrayList<Troncon>> hashMap,String voi_id, Troncon tr )
    {
        ArrayList<Troncon> liste = hashMap.get(voi_id);
        if (liste==null)
            liste = new ArrayList<Troncon>();
        liste.add(tr);
        hashMap.put(voi_id, liste);
        
    }
    
    public HashMap<String, ArrayList<Troncon>> getAllTronconsByVoieByDpt(String dep) throws SQLException{
        
        HashMap<String, ArrayList<Troncon>> hashMap = new HashMap<String, ArrayList<Troncon>>();

        TronconDAO dao = new TronconDAO();
        ResultSet rs = dao.getAllTronconsByDep(connection, dep);
        
        while(rs.next())
        {
            Troncon tr = new Troncon(rs);
            if(!(tr.voi_id_droit==null && tr.voi_id_gauche==null)){
                if(tr.voi_id_gauche==null)             
                    add(hashMap,tr.voi_id_droit,tr);
                else if(tr.voi_id_droit==null)     
                    add(hashMap,tr.voi_id_gauche,tr);
                else{
                    add(hashMap,tr.voi_id_gauche,tr); 
                    if(!tr.voi_id_droit.equals(tr.voi_id_gauche))
                    {   
                        add(hashMap,tr.voi_id_droit,tr);  
                    }
                }
            }
        }

        return hashMap;
    }
    
    public String getGeometrieVoie(ArrayList<Troncon> list)
    {
        String geovoie = "[";
        for(Troncon tr : list){
             geovoie += getGeoCoor(tr.geometrie)+",";
        }
        geovoie = geovoie.substring(0, geovoie.length()-1);
        geovoie+="]";
        return geovoie; 
    }
    
    public HashMap<String, String> getHash(String geometrie) {
        geometrie = geometrie.replaceAll("\"", "");
        geometrie = geometrie.substring(1, geometrie.length() - 1);
        String[] geo1 = geometrie.split(":");
        String[] geo2 = geo1[1].split(",");
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put(geo1[0], geo2[0].toLowerCase());
        hash.put(geo2[1], geo1[2]);
        return hash;
    }
    
    public String getGeoCoor(String geometrie) {
        String type = getHash(geometrie).get("type");
        String coor;
        if (type.equals("linestring")) {
            coor = getHash(geometrie).get("coordinates");
        }else if (type.equals("multilinestring")) {
            coor = getHash(geometrie).get("coordinates");
            coor = coor.substring(1, coor.length()-1);
        }else{
            throw new Error("le type geometrie doit etre soit linestring soit multilinestring");
        }
        return coor;
    }

}