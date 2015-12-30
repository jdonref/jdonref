package org.elasticsearch.river.jdonrefv4.test.simpletest;

import com.sun.jersey.api.client.Client;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.index.ElasticSearchUtil;
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
        String url = "plf.jdonrefv4.ppol.minint.fr:80";
        String index = "jdonrefsimpletest";
        int size = 100;
        boolean nowait = false;
        ElasticSearchUtil m = new ElasticSearchUtil();
        Client client = Client.create();
        m.setClient(client);
        m.setUrl(url);
        m.showHealth();
        m.showDeleteIndex(index);
        for(int i=0;i<size;i++)
        {
            if (i%1000==1) System.out.println((i-1)+" communes indexed");
            m.indexResource(index,"commune",getJSONCommune("PARIS "+i));
        }
        if ((size-1)%1000!=1)
            System.out.println(size+" communes indexed");
        
        if (!nowait)
        {
            // Wait for indexation !
            try {

                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        m.showSearch(index,"commune" , "nom:PARIS");
        m.showSearch(index, "nom:PARIS");
        
        m.showIndexStats(index);
        
       // m.showDeleteIndex();
    }
}