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
        
        ElasticSearchUtil m = new ElasticSearchUtil();
        Client client = Client.create();
        
        m.showHealth(client,url);
        m.showDeleteIndex(client,url,"jdonref");
        
        m.showIndexResource(client,url,"jdonref","commune",getJSONCommune("PARIS"));
        m.showIndexResource(client,url,"jdonref","commune",getJSONCommune("PARIS 12"));
        m.showIndexResource(client,url,"jdonref","commune",getJSONCommune("PARIS 13"));
        m.showIndexResource(client,url,"jdonref","commune",getJSONCommune("PARIS 14"));
        
        // Wait for indexation !
        try {

            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        m.showSearch(client, url, "jdonref","commune" , "nom:PARIS");
        
        m.showIndexStats(client,url,"jdonref");
        
        m.showDeleteIndex(client,url,"jdonref");
    }
}