package jdonref_es_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.json.JsonObject;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.dao.CommuneDAO;
import jdonref_es_poc.entity.Commune;

/**
 *
 * @author Julien
 */
public class CommuneIndex
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
    
    public void addCommune(Commune commune) throws IOException
    {
        JsonObject data = commune.toJSONDocument();
        
        util.indexResource("commune", data.toString());
    }
    
    public void indexJDONREFCommune() throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        CommuneDAO dao = new CommuneDAO();
        ResultSet rs = dao.getAllCommunes(connection);
        
        int i =0;
        while(rs.next())
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" communes traitées");
            
            Commune commune = new Commune(rs,new int[]{1,4,2,5});
            
            addCommune(commune);
        }
    }

    void indexJDONREFCommune(Commune[] communes) throws IOException
    {
        if (isVerbose())
            System.out.println("Communes");
        
        for(int i=0;i<communes.length;i++)
        {
            if (isVerbose() && (i++)%1000==0)
                System.out.println(i+" communes traitées");
            
            Commune commune = communes[i];
            
            addCommune(commune);
        }
    }
}