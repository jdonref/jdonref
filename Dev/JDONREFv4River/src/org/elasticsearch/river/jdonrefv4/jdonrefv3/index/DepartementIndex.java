package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.DepartementDAO;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Departement;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.MetaData;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.entity.Voie;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.index.JDONREFIndex.FLAGS;


/**
 *
 * @author Julien
 */
public class DepartementIndex
{
    boolean verbose = false;
    ElasticSearchUtil util;
    Connection connection;
    
    static int idDep=0;
    static int idDepTmp=0;
    int paquetsBulk=30;
    
    boolean withGeometry = true;
    
    HashSet<FLAGS> flags = new HashSet<>();
    
    String index;
        
    String [] dept = new String[0];

    public String[] getDept() {
        return dept;
    }

    public void setDept(String[] dept) {
        this.dept = dept;
    }
    
    protected static DepartementIndex instance = null;
    
    public static DepartementIndex getInstance()
    {
        if (instance==null)
            instance = new DepartementIndex();
        return instance;
    }
    
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public void setFlags(HashSet<FLAGS> flags)
    {
        this.flags = flags;
    }

    public void addFlag(FLAGS flag)
    {
        flags.add(flag);
    }
    
    public void removeFlag(FLAGS flag)
    {
        flags.remove(flag);
    }
    
    public boolean isFlag(FLAGS flag)
    {
        return flags.contains(flag);
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

    public boolean isWithGeometry() {
        return withGeometry;
    }

    public void setWithGeometry(boolean withGeometry) {
        this.withGeometry = withGeometry;
    }
    
    public void addDepartment(Departement departement) throws IOException
    {
        JsonObject data = departement.toJSONDocument(withGeometry);
        util.indexResource(index,"departement", data.toString());
    }
    
    public void indexJDONREFDepartements() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Départements");
        
        DepartementDAO dao = new DepartementDAO();
        ResultSet rs = dao.getAllDepartement(connection, getDept());
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(index);
        metaDataDep.setType("departement");
        
        int i =0;
        String bulk ="";
        int lastIdBulk=idDepTmp;
        while(rs.next())
        {
            if(paquetsBulk == 1) System.out.println(i+" départements traités");
            if (isVerbose() && i%paquetsBulk==1)
                System.out.println(i+" départements traités");
            
            Departement d = new Departement(rs);
//            Departement d = new Departement(rs,new int[]{1,2,3,4,5,6});
//            String dpt_code_departement = rs.getString(1);
//            String dpt_projection = rs.getString(2);
//            String dpt_referentiel = rs.getString(3);
//            Date t0 = rs.getDate(4);
//            Date t1 = rs.getDate(5);
//            
//            Departement d = new Departement();
//            d.code_departement = dpt_code_departement;
//            d.dpt_projection = dpt_projection;
//            d.dpt_referentiel = dpt_referentiel;
//            d.t0 = t0;
//            d.t1 = t1;
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(new Long(++idDep));
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument(withGeometry).toString()+"\n";
            if((idDep-idDepTmp)%paquetsBulk==0){
                System.out.println("departement : bulk pour les ids de "+(idDep-paquetsBulk+1)+" à "+idDep);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idDep;
            }
            i++;
        }
        rs.close();
        if(!bulk.equals("")){
        System.out.println("departement : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idDep));        
        if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idDepTmp = idDep;
    }
    
    public void indexJDONREFDepartements(Departement[] departements) throws IOException
    {
        if (isVerbose())
            System.out.println("Départements");
        
//      creation de l'objet metaDataDep
        MetaData metaDataDep= new MetaData();
        metaDataDep.setIndex(index);
        metaDataDep.setType("departement");

        String bulk ="";
        int lastIdBulk=idDepTmp;        
        
        for(int i=0;i<departements.length;i++)
        {
            if (isVerbose() && i%30==1)
                System.out.println(i+" départements traités");
            Departement d = departements[i];
            
//            addDepartment(d);
            
//            creation de l'objet metaDataDep plus haut
            metaDataDep.setId(new Long(++idDep));
            bulk += metaDataDep.toJSONMetaData().toString()+"\n"+d.toJSONDocument(withGeometry).toString()+"\n";
            if((idDep-idDepTmp)%paquetsBulk==0){
                System.out.println("departement : bulk pour les ids de "+(idDep-paquetsBulk+1)+" à "+idDep);
                if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
                bulk="";
                lastIdBulk=idDep;
            }
        }
        if(!bulk.equals("")){
            System.out.println("departement : bulk pour les ids de "+(lastIdBulk+1)+" à "+(idDep));        
            if (!isVerbose())
                    util.indexResourceBulk(bulk);
                else
                    util.showIndexResourceBulk(bulk);
        }
        idDepTmp = idDep;
    }
    
    public void indexJDONREFDepartement(Voie[] voies,String dpt) throws IOException
    {
        if (isFlag(FLAGS.VOIE))
        {
            VoieIndex vIndex = VoieIndex.getInstance();
            vIndex.indexJDONREFVoiesDepartement(voies, dpt);
        }
        
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
    
    public void indexJDONREFDepartement(boolean nested, boolean parent, String dpt) throws IOException, SQLException
    {
        if (isFlag(FLAGS.VOIE))
        {
            VoieIndex vIndex = VoieIndex.getInstance();
            vIndex.indexJDONREFVoiesDepartement(dpt);
        }
        
        if (isFlag(FLAGS.ADRESSE))
        {
            AdresseIndex adrIndex = AdresseIndex.getInstance();
            adrIndex.setMap_idIndexVoieES(VoieIndex.getInstance().getMap_idIndexVoieES());
            if(nested) adrIndex.indexJDONREFAdressesDepartementNested(dpt);
            else adrIndex.indexJDONREFAdressesDepartement(parent, dpt);
        }
        
        if (isFlag(FLAGS.TRONCON))
        {
            TronconIndex tIndex = TronconIndex.getInstance();            
            tIndex.indexJDONREFTronconsDepSide(dpt,"droit");
            tIndex.indexJDONREFTronconsDepSide(dpt,"gauche");
            // osbolete
            //tIndex.indexJDONREFTronconsDroitDepartement(dpt); 
            //tIndex.indexJDONREFTronconsGaucheDepartement(dpt);
        }
    }
}