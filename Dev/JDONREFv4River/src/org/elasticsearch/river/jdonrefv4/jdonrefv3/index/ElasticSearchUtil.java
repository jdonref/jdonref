package org.elasticsearch.river.jdonrefv4.jdonrefv3.index;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.json.*;


/**
 *
 * @author Julien
 */
public class ElasticSearchUtil
{
    String url;
    Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
    
    public String setRefreshInterval(String index,String interval)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/_settings");
        
        JsonObjectBuilder refresh_interval = Json.createObjectBuilder();
        refresh_interval.add("refresh_interval",interval);
        
        JsonObjectBuilder setting = Json.createObjectBuilder();
        setting.add("index", refresh_interval);
        
        ClientResponse response = webResource.accept("application/json").put(ClientResponse.class,setting.build().toString());
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showSetRefreshInterval(String index,String interval)
    {
        String output = setRefreshInterval(index,interval);
        
        System.out.println("setRefreshInterval : "+output);
    }
    
    public Iterator<String> getLastAliasIndices(String alias)
    {
        WebResource webResource = client.resource("http://"+url+"/_alias/"+alias);
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        String output = response.getEntity(String.class);
        
        JsonReader reader = Json.createReader(new StringReader(output));
        JsonObject obj = reader.readObject();
        
        if (obj.containsKey("error")) return null;
        
        Set<String> keys = obj.keySet();
        
        return keys.iterator();
    }
    
    public ArrayList<String> getLastAliasIndices(String alias,String start_with)
    {
        WebResource webResource = client.resource("http://"+url+"/_alias/"+alias);
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        String output = response.getEntity(String.class);
        
        JsonReader reader = Json.createReader(new StringReader(output));
        JsonObject obj = reader.readObject();
        
        if (obj.containsKey("error")) return null;
        
        Set<String> keys = obj.keySet();
        Iterator<String> lastindices = keys.iterator();
        ArrayList<String> next = new ArrayList<>();
        
        while(lastindices.hasNext())
        {
            String indice = lastindices.next();
            if (indice.startsWith(start_with))
                next.add(indice);
        }
        
        return next;
    }
    
    protected void removeLastIndices(ArrayList<String> lastindices_to_remove)
    {
        if (lastindices_to_remove!=null)
        for(int i=0;i<lastindices_to_remove.size();i++)
        {
            deleteIndex(lastindices_to_remove.get(i));
        }
    }
    
    protected void removeLastIndices(Iterator<String> lastindices_to_remove)
    {
        if (lastindices_to_remove!=null)
        while(lastindices_to_remove.hasNext())
        {
            deleteIndex(lastindices_to_remove.next());
        }
    }
    
    public void showExchangeIndexInAlias(String alias,String index,String startWith)
    {
        String output = exchangeIndexInAlias(alias,index,startWith);
        
        System.out.println("exchangeIndexInAlias : "+output);
    }
    
    public String exchangeIndexInAlias(String alias,String index,String startWith)
    {
        ArrayList<String> lastindices_to_remove = getLastAliasIndices(alias, startWith);
        
        WebResource webResource = client.resource("http://"+url+"/_aliases");
        
        JsonObjectBuilder newindex = Json.createObjectBuilder();
        newindex.add("index", index);
        newindex.add("alias", alias);
        
        JsonObjectBuilder addnewindex = Json.createObjectBuilder();
        addnewindex.add("add", newindex);
        
        JsonArrayBuilder jab = Json.createArrayBuilder();
        jab.add(addnewindex);
        
        if (lastindices_to_remove!=null)
        for(int i=0;i<lastindices_to_remove.size();i++)
        {
            JsonObjectBuilder lastindex = Json.createObjectBuilder();
            lastindex.add("index", lastindices_to_remove.get(i));
            lastindex.add("alias", alias);
        
            JsonObjectBuilder removelastindex = Json.createObjectBuilder();
            removelastindex.add("remove", lastindex);
            
            jab.add(removelastindex);
        }
        
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("actions", jab);
        
        String data = job.build().toString();
        System.out.println(data);
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,data);
        String output = response.getEntity(String.class);
        
        removeLastIndices(lastindices_to_remove);
        
        return output;
    }
    
    public String setNewAlias(String index,String alias)
    {
        Iterator<String> lastindices_to_remove = getLastAliasIndices(alias);
        Iterator<String> lastindices = getLastAliasIndices(alias);
        
        WebResource webResource = client.resource("http://"+url+"/_aliases");
        
        JsonObjectBuilder newindex = Json.createObjectBuilder();
        newindex.add("index", index);
        newindex.add("alias", alias);
        
        JsonObjectBuilder addnewindex = Json.createObjectBuilder();
        addnewindex.add("add", newindex);
        
        JsonArrayBuilder jab = Json.createArrayBuilder();
        jab.add(addnewindex);
        if (lastindices!=null)
        while(lastindices.hasNext())
        {
            JsonObjectBuilder lastindex = Json.createObjectBuilder();
            lastindex.add("index", lastindices.next());
            lastindex.add("alias", alias);
        
            JsonObjectBuilder removelastindex = Json.createObjectBuilder();
            removelastindex.add("remove", lastindex);
            
            jab.add(removelastindex);
        }
        
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("actions", jab);
        
        String data = job.build().toString();
        System.out.println(data);
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,data);
        String output = response.getEntity(String.class);
        
        removeLastIndices(lastindices_to_remove);
        
        return output;
    }
    
    public void showSetNewAlias(String index,String alias)
    {
        String output = setNewAlias(index, alias);
        
        System.out.println("setNewAlias : "+output);
    }
    
    public String indexResource(String index,String object,String data)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/");
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,data);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showIndexResourceBulk(String bulk)
    {
        String output = indexResourceBulk(bulk);
        
        JsonReader reader = Json.createReader(new StringReader(output));
        JsonObject object = reader.readObject();
        boolean errors = object.getBoolean("errors");
        
        if (!errors)
            System.out.println("bulk : "+output.substring(0,30)+" ...");
        else
            System.out.println("bulk : "+output);
    }
    
    public String indexResourceBulk(String bulk)
    {
        WebResource webResource = client.resource("http://"+url+"/_bulk?pretty=true");
        
        ClientResponse response = webResource.accept("application/json").post(ClientResponse.class,bulk);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    
    
    public void showIndexResource(String index,String object, String data)
    {
        String output = indexResource(index, object, data);
        
        System.out.println("index : "+output);
    }
    
    public String deleteIndex(String index)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/");
        
        ClientResponse response = webResource.accept("application/json").delete(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showDeleteIndex(String index)
    {
        System.out.println("Delete index : "+index);

        String output = deleteIndex(index);
        
        System.out.println(output);
    }
    
    public String deleteType(String index,String type)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+type);
        
        ClientResponse response = webResource.accept("application/json").delete(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showDeleteType(String index,String type)
    {
        System.out.println("Delete type : "+index+"/"+type);

        String output = deleteType(index,type);
        
        System.out.println(output);
    }
    
    public String indexStats(String index)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/_stats");
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showIndexStats(String index)
    {
        String output = indexStats(index);
        
        System.out.println("index : "+output);
    }
    
    public String search(String index,String query)
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
    
    public void showSearch(String index,String object,String query)
    {
        String output = search(index,object,query);
        
        System.out.println("index : "+output);
    }
    

    public String search(String index,String object,String query)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/_search?q="+query);
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showSearch(String index,String query)
    {
        String output = search(index,query);
        
        System.out.println("index : "+output);
    }

    public String resource(String index,String object, int i)
    {
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+object+"/"+i);
        
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    public void showResource(String index,String object, int i)
    {
        String output = resource(index, object,i);
        
        System.out.println("index : "+output);
    }

    void showCreateIndex(String index) {
        System.out.println("creating index : "+index);
        
        String res = createIndex(index);
        
        System.out.println(res);
    }

    String createIndex(String index) {
        WebResource webResource = client.resource("http://"+url+"/"+index);
        
        ClientResponse response = webResource.accept("application/json").put(ClientResponse.class);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
    
    void showCreateIndex(String index,String analysis) throws FileNotFoundException, IOException {
        System.out.println("Creating index : "+index);
        
        String res = createIndex(index,analysis);
        
        System.out.println(res);
    }

    String createIndex(String index,String analysis) throws FileNotFoundException, IOException {
        String content = readFile(analysis);
        
        WebResource webResource = client.resource("http://"+url+"/"+index);
        
        ClientResponse response = webResource.accept("application/json").put(ClientResponse.class,content);
        
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
    
    public void showPutMapping(String index,String type,String file) throws FileNotFoundException, IOException
    {
        System.out.println("Défini le mapping pour "+type+" à partir du fichier "+file);
        
        String res = putMapping(index,type,file);
        
        System.out.println(res);
    }
    
    public String putMapping(String index,String type,String file) throws FileNotFoundException, IOException
    {
        String content = readFile(file);
        
        WebResource webResource = client.resource("http://"+url+"/"+index+"/"+type+"/_mapping");
        
        ClientResponse response = webResource.accept("application/json").put(ClientResponse.class,content);
        
        String output = response.getEntity(String.class);
        
        return output;
    }
}