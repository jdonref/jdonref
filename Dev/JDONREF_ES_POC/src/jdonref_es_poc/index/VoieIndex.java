package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.dao.VoieDAO;
import jdonref_es_poc.entity.Voie;

/**
 *
 * @author Julien
 */
public class VoieIndex {
    boolean verbose = false;
    ElasticSearchUtil util;
    Connection connection;

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
        
        int c =0;
        
        while(rs.next())
        {
            if (isVerbose() && c++%1000==0)
                System.out.println(c+" voies traitées");
            
            Voie v = new Voie(rs);
            
            addVoie(v);
        }
    }

    void indexJDONREFVoiesDepartement(Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : voies");
        
        int c =0;
        
        for(int i=0;i<voies.length;i++)
        {
            if (isVerbose() && c++%1000==0)
                System.out.println(c+" voies traitées");
            
            Voie v = voies[i];
            
            addVoie(v);
        }
    }
}