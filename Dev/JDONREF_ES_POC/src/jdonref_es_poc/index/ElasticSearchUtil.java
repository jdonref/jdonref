package jdonref_es_poc.index;

import com.sun.ws.rest.api.client.Client;
import com.sun.ws.rest.api.client.ClientResponse;
import com.sun.ws.rest.api.client.WebResource;

/**
 *
 * @author Julien
 */
public class ElasticSearchUtil
{
    public String getHealth(Client client,String url)
    {
        WebResource webResource = client.resource("http://"+url+"/_cluster/health");
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        if (response.getStatus() != 200)
        {
            System.out.println("Failed with HTTP "+response.getStatus());
        }
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showHealth(Client client,String url)
    {
        String output = getHealth(client,url);
        
        System.out.println("health : "+output);
    }
    
    public String indexResource(Client client,String url, String index,String object,String data)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/");
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,data);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showIndexResource(Client client, String url, String index, String object, String data)
    {
        String output = indexResource(client, url, index, object, data);
        
        System.out.println("index : "+output);
    }
    
    public String deleteIndex(Client client,String url,String index)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/");
        
        ClientResponse response = webResource.accept("application/json").delete(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showDeleteIndex(Client client,String url,String index)
    {
        String output = deleteIndex(client, url, index);
        
        System.out.println("index : "+output);
    }
    
    public String indexStats(Client client,String url, String index)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/_stats");
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showIndexStats(Client client,String url,String index)
    {
        String output = indexStats(client, url, index);
        
        System.out.println("index : "+output);
    }
}
