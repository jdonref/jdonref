package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.json.JsonObject;
import jdonref_es_poc.dao.DepartementDAO;
import jdonref_es_poc.entity.Departement;
import jdonref_es_poc.entity.MetaData;
import jdonref_es_poc.entity.Voie;

/**
 *
 * @author Julien
 */
public class DepartementIndex
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

    
    public void addDepartment(Departement departement) throws IOException
    {
        JsonObject data = departement.toJSONDocument();
        
        util.indexResource("departement", data.toString());
    }
    
    public void indexJDONREFDepartements() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Départements");
        

        DepartementDAO dao = new DepartementDAO();
        ResultSet rs = dao.getAllDepartement(connection);
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(util.index);
        metaDataDep.setType("departement");
        
        int i =0;
        String bulk ="";
        while(rs.next())
        {
            if (isVerbose() && i%30==1)
                System.out.println(i+" départements traités");
            String dpt_code_departement = rs.getString(1);
            String dpt_projection = rs.getString(2);
            String dpt_referentiel = rs.getString(3);
            Date t0 = rs.getDate(4);
            Date t1 = rs.getDate(5);
            
            Departement d = new Departement();
            d.code_departement = dpt_code_departement;
            d.dpt_projection = dpt_projection;
            d.dpt_referentiel = dpt_referentiel;
            d.t0 = t0;
            d.t1 = t1;
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(i+1);
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument().toString()+"\n";
            if(i%30==0){
                util.indexResourceBulk(bulk);
                bulk="";
            }
            i++;
//            addDepartment(d);     
        }
        util.showIndexResourceBulk(bulk);
    }
    
    public void indexJDONREFDepartements(Departement[] departements) throws IOException
    {
        if (isVerbose())
            System.out.println("Départements");
        
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(util.index);
        metaDataDep.setType("departement");
        String bulk ="";
        
        for(int i=0;i<departements.length;i++)
        {
            if (isVerbose() && i%30==1)
                System.out.println(i+" départements traités");
            Departement d = departements[i];
            
//            addDepartment(d);
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(i+1);
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument().toString()+"\n";
            if(i%30==0){
                util.indexResourceBulk(bulk);
                bulk="";
            }
        }
        util.showIndexResourceBulk(bulk);
    }
    
    public void indexJDONREFDepartement(Voie[] voies,String dpt) throws IOException
    {
        VoieIndex vIndex = new VoieIndex();
        vIndex.setUtil(util);
        vIndex.setConnection(connection);
        vIndex.setVerbose(isVerbose());
        vIndex.indexJDONREFVoiesDepartement(voies, dpt);
        
        // non développé
//        AdresseIndex adrIndex = new AdresseIndex();
//        adrIndex.setUtil(util);
//        adrIndex.setConnection(connection);
//        adrIndex.setVerbose(isVerbose());
//        adrIndex.indexJDONREFAdressesDepartement(dpt);
        
//        TronconIndex tIndex = new TronconIndex();
//        tIndex.setUtil(util);
//        tIndex.setConnection(connection);
//        tIndex.setVerbose(isVerbose());
//        tIndex.indexJDONREFTronconsDroitDepartement(voies, dpt);
//        tIndex.indexJDONREFTronconsGaucheDepartement(voies, dpt);
    }
    
    public void indexJDONREFDepartement(String dpt) throws IOException, SQLException
    {
        VoieIndex vIndex = new VoieIndex();
        vIndex.setUtil(util);
        vIndex.setConnection(connection);
        vIndex.setVerbose(isVerbose());
        vIndex.indexJDONREFVoiesDepartement(dpt);
        
        AdresseIndex adrIndex = new AdresseIndex();
        adrIndex.setUtil(util);
        adrIndex.setConnection(connection);
        adrIndex.setVerbose(isVerbose());
        adrIndex.indexJDONREFAdressesDepartement(dpt);
        
//        TronconIndex tIndex = new TronconIndex();
//        tIndex.setUtil(util);
//        tIndex.setConnection(connection);
//        tIndex.setVerbose(isVerbose());
//        tIndex.indexJDONREFTronconsDroitDepartement(dpt);
//        tIndex.indexJDONREFTronconsGaucheDepartement(dpt);
    }    

    
}