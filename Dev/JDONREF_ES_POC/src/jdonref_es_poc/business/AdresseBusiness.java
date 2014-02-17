package jdonref_es_poc.business;

import com.sun.ws.rest.api.client.Client;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Hashtable;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jdonref_es_poc.index.ElasticSearchUtil;
import jdonref_es_poc.index.JDONREFIndex;

/**
 *
 * @author Julien
 */
public class AdresseBusiness
{
    int hitsPerPage = 5;
    float limit = 1.0f;

    ElasticSearchUtil es;

    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public void setHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }
    
    public AdresseBusiness(JDONREFIndex index) throws IOException
    {
       this.es = index.getUtil();
    }
    
    public String addTilde(String query)
    {
        String[] split = query.split(" ");
        
        String res = "";
        for(int i=0;i<split.length;i++)
        {
            if (i>0)
                res += " ";
            if (!split[i].matches("[0-9]*"))
                res += split[i]+"~";
            else
                res += split[i];
        }
        return res;
    }
    
    public void showHits(JsonArray obj) throws IOException
    {
        System.out.println("Found " + obj.size() + " hits.");
            for(int i=0;i<obj.size();++i) {
                
                JsonObject d = obj.getJsonObject(i);
                String docId = d.getString("_id");
                long score = d.getJsonNumber("_score").longValue();
                
                String fullNames = d.getJsonObject("_source").getString("toString");
                System.out.print((i + 1)+ " ("+score+") :");
                //for(int j=0;j<fullNames.length;j++)
                    System.out.println(fullNames);
               // System.out.println(searcher.explain(s.q, hits[i].doc));
            }
    }
    
    public boolean isOk(JsonArray hits, float limit)
    {
        if (hits.size()==0) return false;
        
        if (hits.getJsonObject(0).getJsonNumber("_score").longValue()>=limit) return true;
        
        return false;
    }
    
    public JsonArray valide(String querystr) throws IOException, Exception
    {
        System.out.println("-------------");
        System.out.println("Cherche "+querystr);
        //querystr = qp.escape(querystr); // warning : valideApprox ajoute ~ à la fin des mots. Les effets de bords n'ont pas été traités.
        
        JsonObject obj = valideExact(querystr);
        if (obj.containsKey("status") && obj.getInt("status")!=200)
        {
            throw(new Exception(obj.getString("error")));
        }
        
        JsonArray hits = obj.getJsonObject("hits").getJsonArray("hits");
        
        if (!isOk(hits, limit))
        {
            obj = valideApprox(querystr);
            if (obj.containsKey("status") && obj.getInt("status")!=200)
            {
                throw(new Exception(obj.getString("error")));
            }
            hits = obj.getJsonObject("hits").getJsonArray("hits");
        }
        
        showHits(hits);
        
        return hits;
    }
    
    public JsonObject valideExact(String querystr) throws IOException
    {
        //querystr = getQueryStrExact(querystr);
        System.out.println("Cherche exactement "+querystr);

        long start = Calendar.getInstance().getTimeInMillis();
        String output = es.search(querystr);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println((end-start)+" millis");
        
        StringReader reader = new StringReader(output);
        JsonReader jsonreader = Json.createReader(reader);
        JsonObject obj = jsonreader.readObject();
        return obj;
    }
    
    public JsonObject valideApprox(String querystr) throws IOException
    {
        querystr = addTilde(querystr);
        //querystr = getQueryStrExact(querystr);
        System.out.println("Cherche approximativement " + querystr);
        
        long start = Calendar.getInstance().getTimeInMillis();
        String output = es.search(querystr);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println((end-start)+" millis");
        
        StringReader reader = new StringReader(output);
        JsonReader jsonreader = Json.createReader(reader);
        JsonObject obj = jsonreader.readObject();
        return obj;
    }
    
    public boolean isInt(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }
    
    public String getQueryStrExact(String find)
    {
        Hashtable<String,Boolean> hash = new Hashtable<String,Boolean>();
        String str = "";
        
        boolean isThereInt = false;
        
        String[] splitted = find.split(" ");
        for(int i=0;i<splitted.length;i++)
        {
            String stri = splitted[i];
            
            if (hash.get(stri)==null)
            {
                hash.put(stri,true);
            
                if (isInt(stri))
                {
                    isThereInt = true;
                    //str += "(";
                    if (stri.length()==5)
                        str += " code_insee:"+stri+"^2";
                    if (stri.length()==5)
                        str += " code_postal:"+stri+"^2";
                    str += " code_departement:"+stri;
                    if (i==0)
                        str += "^0.5";
                    if (i>0)
                        str += "^2";
                    str += " code_arrondissement:"+stri;
                    if (i==0)
                        str += "^0.5";
                    if (i>0)
                        str += "^2";
                    str += " numero:"+stri;
                    if (i==0)
                        str += "^2.5";
                    str += " numero:AUCUN";
                    //str += ")";
                }
                else
                {
                    str += " fullName:"+stri+"^10";
                }
            }
        }
        
        if (!isThereInt)
            str += " numero:AUCUN";
        
        return str;
    }
}