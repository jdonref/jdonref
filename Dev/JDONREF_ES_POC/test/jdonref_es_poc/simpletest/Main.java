package jdonref_es_poc.simpletest;

import com.sun.ws.rest.api.client.Client;
import com.sun.ws.rest.api.client.ClientResponse;
import com.sun.ws.rest.api.client.WebResource;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import jdonref_es_poc.index.ElasticSearchUtil;
import org.junit.Test;

/**
 *
 * @author Julien
 */
public class Main
{
    public static String getJSONCommune(String nom)
    {
        JsonObject model = Json.createObjectBuilder()
                .add("nom", nom)
                .build();
        return model.toString();
    }
    
    @Test
    public void main()
    {
        String url = "192.168.0.12:9200";
        int size = 2000;
        boolean nowait = true;
        
        ElasticSearchUtil m = new ElasticSearchUtil();
        Client client = Client.create();
        
        m.showHealth(client,url);
        m.showDeleteIndex(client,url,"jdonref");
        
        for(int i=0;i<size;i++)
        {
            if (i%1000==1) System.out.println((i-1)+" communes indexed");
            m.indexResource(client,url,"jdonref","commune",getJSONCommune("PARIS "+i));
        }
        
        if (!nowait)
        {
            // Wait for indexation !
            try {

                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        m.showSearch(client, url, "jdonref","commune" , "nom:PARIS");
        
        m.showIndexStats(client,url,"jdonref");
        
        //m.showDeleteIndex(client,url,"jdonref");
    }
}