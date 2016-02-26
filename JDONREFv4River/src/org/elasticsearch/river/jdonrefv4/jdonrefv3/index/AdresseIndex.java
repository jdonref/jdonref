package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import au.com.bytecode.opencsv.CSVReader;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.AdresseDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.AdresseDAO_csv;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Adresse;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Adresse_csv;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.VoieAdr;

/**
 *
 * @author Julien
 */
public class AdresseIndex {

    public static char SEPARATOR = ';';
    
    boolean verbose = false;
    boolean withGeometry = true;
    ElasticSearchUtil util;

    static int idAdresse=0;
    static int idAdresseTmp=0;
    int paquetsBulk=1000;

    protected static AdresseIndex instance = null;
    HashMap<String, VoieAdr> map_idIndexVoieES =  new HashMap<>();

    public HashMap<String, VoieAdr> getMap_idIndexVoieES() {
        return map_idIndexVoieES;
    }

    public void setMap_idIndexVoieES(HashMap<String, VoieAdr> map_idIndexVoieES) {
        this.map_idIndexVoieES = map_idIndexVoieES;
    }
    String index;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public static AdresseIndex getInstance()
    {
        if (instance==null)
            instance = new AdresseIndex();
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
    
    public Connection getConnection() throws SQLException {
        return JDONREFIndex.getInstance().getNewConnection();
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
        
        util.indexResource(index,"adresse", data.toString());
    }

    HashSet<String> ids = new HashSet<String>();
    
public void indexJDONREFAdressesDepartement(Boolean parent, String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : adresses");
        AdresseDAO dao = new AdresseDAO();
        Connection c = getConnection();
        ResultSet rs = dao.getAllAdressesOfDepartement(c, dpt);
//      creation de l'objet metaDataAdresse
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(index);
        metaDataAdresse.setType("adresse");
        
        ids.clear();
        
        StringBuilder bulk = new StringBuilder();
        int i =0;
        int lastIdBulk=idAdresseTmp;
        int lastI = -1;
        
        while(rs.next())
        {
            if (isVerbose() && i%paquetsBulk==1)
            {
                if (i!=lastI)
                {
                    System.out.println(dpt+": "+i+" adresses traitées");
                    lastI = i;
                }
            }
            
            Adresse adr = new Adresse(rs);
            
            if (ids.contains(adr.idadresse))
                continue;
            ids.add(adr.idadresse);
            
            if (adr.numero!=null){
//            creation de l'objet metaDataAdresse plus haut
                metaDataAdresse.setId(new Long(++idAdresse));
                if(parent == false)
                    bulk.append(metaDataAdresse.toJSONMetaData().toString()).append("\n").append(adr.toJSONDocument(withGeometry).toString()).append("\n");
                else
                {
                    metaDataAdresse.setParent(Integer.toString(map_idIndexVoieES.get(adr.voiAdr.ligne4).idvoie));
                    bulk.append(metaDataAdresse.toJSONMetaDataWithParent().toString()).append("\n").append(adr.toJSONDocument(withGeometry).toString()).append("\n");
                }
// envoyé le bulk par paquet de 1000 à partir de idAdresseTmp
// idAdresseTmp valeur de l'id de debut au moment de l'appel à cette methode
                if((idAdresse-idAdresseTmp)%paquetsBulk==0)
                {
                    System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                    if (!isVerbose())
                        util.indexResourceBulk(bulk.toString());
                    else
                        util.showIndexResourceBulk(bulk.toString());
                    bulk.setLength(0);
                    lastIdBulk=idAdresse;
                }
            }
            adr = null;
            i++;     
        }
        rs.close();
        c.close();
        if(bulk.length()!=0){
                System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk.toString());
                else
                    util.showIndexResourceBulk(bulk.toString());
        }
        idAdresseTmp = idAdresse;
    }

    public void indexJDONREFAdressesDepartementNested(String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : adresses");
        AdresseDAO dao = new AdresseDAO();
        Connection c = getConnection();
        ResultSet rs = dao.getAllAdressesOfDepartement(c, dpt);
//      creation de l'objet metaDataAdresse
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(index);
        metaDataAdresse.setType("adresse");
              
        StringBuilder bulk = new StringBuilder();
        int i =0;
        int lastIdBulk=idAdresseTmp;
        
        String voi_nom = "";
        String codeinsee = "";
        JsonArrayBuilder ad = Json.createArrayBuilder();
        boolean first = true;
        boolean show = true;
        Adresse adrLast=null;
        Adresse adr=null;
        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(dpt+": "+i+" adresses traités");
            if (isVerbose() && i%paquetsBulk==1 && show){
                System.out.println(dpt+": "+i+" adresses traitées");
                show = false;
            }
            adr = new Adresse(rs);
            if(first){
                voi_nom = adr.voie.voi_nom;
                codeinsee = adr.voie.commune.codeinsee;
                first = false;
                adr.toJSONDocumentNestedAddWithoutNum(ad,withGeometry);
            }
            if(adr.voie.voi_nom.equals(voi_nom)){
                //Ajouter sans numero avec code insee different ????
                if(!adr.voie.commune.codeinsee.equals(codeinsee)){
                    codeinsee = adr.voie.commune.codeinsee;
                    adr.toJSONDocumentNestedAddWithoutNum(ad,withGeometry);
                }
                adrLast = adr;
                if (adr.numero!=null)
                    adr.toJSONDocumentNestedAdd(ad,withGeometry); 
            }else{
                if(!show) show = true;
                String adjson = adrLast.toJSONDocumentNested(ad).toString();
//                System.out.println(adjson);
                ad = Json.createArrayBuilder();
                adr.toJSONDocumentNestedAddWithoutNum(ad,withGeometry);
                if (adr.numero!=null)
                    adr.toJSONDocumentNestedAdd(ad,withGeometry); 
                voi_nom = adr.voie.voi_nom; 
                codeinsee = adr.voie.commune.codeinsee;
    //            creation de l'objet metaDataAdresse plus haut
                metaDataAdresse.setId(new Long(++idAdresse));
                bulk.append(metaDataAdresse.toJSONMetaData().toString()).append("\n").append(adjson).append("\n"); 

                adrLast = adr;
// envoyé le bulk par paquet de 1000 à partir de idAdresseTmp 
// idAdresseTmp valeur de l'id de debut au moment de l'appel à cette methode
                if((idAdresse-idAdresseTmp)%paquetsBulk==0){
                    System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                    if (!isVerbose())
                        util.indexResourceBulk(bulk.toString());
                    else
                        util.showIndexResourceBulk(bulk.toString());
                    bulk.setLength(0);
                    lastIdBulk=idAdresse;
                }
                i++;     
            }
        }
        rs.close();
        c.close();
       
        if(adr!=null){
            String adjson = adr.toJSONDocumentNested(ad).toString();
    //        System.out.println(adjson);
    //        System.out.println(bulk.toString());
            metaDataAdresse.setId(new Long(++idAdresse));
            bulk.append(metaDataAdresse.toJSONMetaData().toString()).append("\n").append(adjson).append("\n"); 
        }
        if(bulk.length()!=0){
                System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
                if (!isVerbose())
                    util.indexResourceBulk(bulk.toString());
                else
                    util.showIndexResourceBulk(bulk.toString());
        }
        idAdresseTmp = idAdresse;
    }
    
    public void indexJDONREFAdressesDepartement_csv(String dptFile) 
    {
        BufferedReader br = null;
        CSVReader csvReader = null;
        try {
            String dpt = dptFile.substring(dptFile.lastIndexOf("_")+1, dptFile.lastIndexOf("."));
            if (isVerbose())
                System.out.println("dpt "+dpt+" : adresses");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dptFile), "UTF8"));
            csvReader = new CSVReader(br, SEPARATOR);
            AdresseDAO_csv csv = new AdresseDAO_csv(csvReader);
            List<String[]> adresses = csv.datas;
            //      creation de l'objet metaDataAdresse
            MetaData metaDataAdresse= new MetaData();
            metaDataAdresse.setIndex(index);
            metaDataAdresse.setType("adresse");
            StringBuilder bulk = new StringBuilder();
            int i =0;
            int lastIdBulk=idAdresseTmp;
            for(String[] adresse : adresses)
            {
                if(paquetsBulk == 1) System.out.println(dpt+": "+i+" adresses traités");
                if (isVerbose() && i%paquetsBulk==1)
                    System.out.println(dpt+": "+i+" adresses traitées");

                Adresse_csv adr = new Adresse_csv(adresse);

                if (adr.numero!=null){
    //            creation de l'objet metaDataAdresse plus haut
                    metaDataAdresse.setId(new Long(++idAdresse));
                    bulk.append(metaDataAdresse.toJSONMetaData().toString()).append("\n").append(adr.toJSONDocument_csv().toString()).append("\n"); 

    // envoyé le bulk par paquet de 1000 à partir de idAdresseTmp 
    // idAdresseTmp valeur de l'id de debut au moment de l'appel à cette methode
                    if((idAdresse-idAdresseTmp)%paquetsBulk==0){
                        System.out.println("adresse : bulk pour les ids de "+(idAdresse-paquetsBulk+1)+" à "+idAdresse);
                        if (!isVerbose())
                            util.indexResourceBulk(bulk.toString());
                        else
                            util.showIndexResourceBulk(bulk.toString());
                        bulk.setLength(0);
                        lastIdBulk=idAdresse;
                    }
                }    
                i++;     
            }
            if(bulk.length()!=0){
                    System.out.println("adresse : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idAdresse));        
                    if (!isVerbose())
                        util.indexResourceBulk(bulk.toString());
                    else
                        util.showIndexResourceBulk(bulk.toString());
            }
            idAdresseTmp = idAdresse;
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

    void indexJDONREFAdressesDepartement(Adresse[] adresses, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : adresses");
        
        //int c =0;
//      creation de l'objet metaDataAdresse
        MetaData metaDataAdresse= new MetaData();
        metaDataAdresse.setIndex(index);
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
                metaDataAdresse.setId(new Long(++idAdresse));                
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