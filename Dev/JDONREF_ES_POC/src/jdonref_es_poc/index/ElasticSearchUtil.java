package jdonref_es_poc.index;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


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
    
    public String indexResourceBulk(String bulk)
    {
        WebResource webResource = client.resource("http://"+url+"/_bulk");
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,bulk);
        
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
        String resource = "http://"+url+"/"+index+"/_search?q="+query;
        
        WebResource webResource = client.resource(resource);
        /*
        JsonObjectBuilder order = Json.createObjectBuilder().add("order", "desc");
        JsonObjectBuilder score = Json.createObjectBuilder().add("_score",order);
        JsonArrayBuilder sort_array = Json.createArrayBuilder().add(score);
        JsonObject sort = Json.createObjectBuilder().add("sort", sort_array).build();
        
        JsonObject query_string = Json.createObjectBuilder().add("query",query).build();
        JsonObject queryJson = Json.createObjectBuilder().add("sort",sort_array).add("query_string",query_string).build();
        */
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class);//,queryJson.toString());
        
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

    void showCreateIndex() {
        System.out.println("creating index : "+index);
        
        String res = createIndex();
        
        System.out.println(res);
    }

    String createIndex() {
        WebResource webResource = client.resource("http://"+url+"/"+index);
        
        ClientResponse response = webResource.accept("application/json").put(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    
    public String readFile(String file) throws FileNotFoundException, IOException
    {
        BufferedReader reader = (new BufferedReader(new FileReader(new File(file))));
        String line = reader.readLine();
        String res = "";
        while(line!=null)
        {
            res += line+System.getProperty("line.separator");
            line = reader.readLine();
        }
        reader.close();
        return res;
    }
    
    public void showPutMapping(String type,String file) throws FileNotFoundException, IOException
    {
        System.out.println("Défini le mapping pour "+type+" à partir du fichier "+file);
        
        String res = putMapping(type,file);
        
        System.out.println(res);
    }
    
    public String putMapping(String type,String file) throws FileNotFoundException, IOException
    {
        String content = readFile(file);
        
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+type+"/_mapping");
        
        ClientResponse response = webResource.accept("application/json").put(ClientResponse.class,content);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
}