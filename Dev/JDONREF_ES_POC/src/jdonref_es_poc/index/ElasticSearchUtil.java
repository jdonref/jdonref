package jdonref_es_poc.index;

import com.sun.ws.rest.api.client.Client;
import com.sun.ws.rest.api.client.ClientResponse;
import com.sun.ws.rest.api.client.WebResource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Julien
 */
public class ElasticSearchUtil
{
    String url;
    Client client;
    String index;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getHealth()
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
    
    public void showHealth()
    {
        String output = getHealth();
        
        System.out.println("health : "+output);
    }
    
    public String indexResource(String object,String data)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/");
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,data);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showIndexResource(String object, String data)
    {
        String output = indexResource(object, data);
        
        System.out.println("index : "+output);
    }
    
    public String deleteIndex()
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/");
        
        ClientResponse response = webResource.accept("application/json").delete(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showDeleteIndex()
    {
        String output = deleteIndex();
        
        System.out.println("index : "+output);
    }
    
    public String indexStats( )
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/_stats");
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showIndexStats( )
    {
        String output = indexStats();
        
        System.out.println("index : "+output);
    }
    
    public String search(String query)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/_search?q="+query);
        
        JsonObjectBuilder order = Json.createObjectBuilder().add("order", "desc");
        JsonObjectBuilder score = Json.createObjectBuilder().add("_score",order);
        JsonArrayBuilder sort_array = Json.createArrayBuilder().add(score);
        JsonObject sort = Json.createObjectBuilder().add("sort", sort_array).build();
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,sort.toString());
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showSearch(String object,String query)
    {
        String output = search(object,query);
        
        System.out.println("index : "+output);
    }

    public String search(String object,String query)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/_search?q="+query);
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showSearch(String query)
    {
        String output = search(query);
        
        System.out.println("index : "+output);
    }

    public String resource(String object, int i)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/"+i);
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showResource( String object, int i)
    {
        String output = resource( object,i);
        
        System.out.println("index : "+output);
    }
}