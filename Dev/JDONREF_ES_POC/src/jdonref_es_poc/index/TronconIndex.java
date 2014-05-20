package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.JsonObject;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.dao.TronconDAO;
import jdonref_es_poc.entity.Adresse;
import jdonref_es_poc.entity.MetaData;
import jdonref_es_poc.entity.Troncon;
import jdonref_es_poc.entity.Voie;

/**
 * 
 * @author Julien
 */
public class TronconIndex
{
     boolean verbose = false;
ElasticSearchUtil util;
    Connection connection;

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
        JsonObject data = a.toJSONDocument();
        
        util.indexResource("troncon", data.toString());
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

    static int idTroncon=0;
    static int idTronconTmp=0;
    int paquetsBulk=1000;

    public void indexJDONREFTronconsDep(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons ");
        
        TronconDAO dao = new TronconDAO();
        ResultSet rs = dao.getAllTronconsByDep(connection, dpt);    
//      creation de l'objet metaDataTroncon
        MetaData metaDataTroncon= new MetaData();
        metaDataTroncon.setIndex(util.index);
        metaDataTroncon.setType("troncon");
              
        String bulk ="";
        int i =0;
        int lastIdBulk=idTronconTmp;

            while(rs.next())
        {
            if (isVerbose() && i%1000==1)
                System.out.println(i+" troncons traités");
            
            Troncon tr = new Troncon(rs);
                        
//            creation de l'objet metaDataTroncon plus haut
            metaDataTroncon.setId(++idTroncon);
            bulk += metaDataTroncon.toJSONMetaData().toString()+"\n"+tr.toJSONDocument().toString()+"\n";
            if((idTroncon-idTronconTmp)%paquetsBulk==0){
                System.out.println("troncons : bulk pour les ids de "+(idTroncon-paquetsBulk+1)+" à "+idTroncon);
                util.indexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idTroncon;
            }
            i++;
        }
        if(!bulk.equals("")){
        System.out.println("troncons : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idTroncon));        
        util.indexResourceBulk(bulk);
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
        String geovoie = "(";
        for(Troncon tr : list){
             geovoie += getCoorGeo(tr.geometrie)+",";
        }
        geovoie = geovoie.substring(0, geovoie.length()-1);
        geovoie+=")";
        return geovoie; 
    }
    

    public String getCoorGeo(String geometrie){
        geometrie=geometrie.trim();
        String type = getGeoTYPE(geometrie);
//        if (type.equals("POINT") || type.equals("LINESTRING") || type.equals("MULTIPOINT")) {
//            geometrie = geometrie.substring(type.length(), geometrie.length());
//        }
//        if (type.equals("MULTILINESTRING") || type.equals("POLYGON") || type.equals("MULTIPOLYGON")) {
//            geometrie = geometrie.substring(type.length()+1, geometrie.length()-1);
//        }
        if (type.equals("LINESTRING")) {
            geometrie = geometrie.substring(type.length(), geometrie.length());
        }else if (type.equals("MULTILINESTRING")) {
            geometrie = geometrie.substring(type.length()+1, geometrie.length()-1);
        }else{
            throw new Error("le type geometrie non valide");
        }
        return geometrie;
    }

        public String getGeoTYPE(String geometrie) {
        String typeGeo = "";
        if (geometrie.equals("") || geometrie == null) {
            typeGeo = "";
        } else {
            for (int i = 0; i < geometrie.length(); i++) {
                if (geometrie.charAt(i) == '(' && geometrie.charAt(i - 1) != '(') {
                    return typeGeo = geometrie.substring(0, i);
                } else {
                    typeGeo = "";
                }
            }
        }
        return typeGeo;
    }
    
}