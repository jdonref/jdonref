package jdonref_es_poc.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author akchana
 */

public class MetaData {
    String index;
    String type;
    int id;
    int version;

    
    public MetaData() {
    }
    
    public MetaData(String index, String type, int id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }

    public MetaData(String index, String type, int id, int version) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.version = version;
    }
        
     
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    
    public JsonObject toJSONMetaData()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
         JsonObjectBuilder builder1 = Json.createObjectBuilder();
         
         builder.add("_index", index);
         builder.add("_type", type);
         builder.add("_id", id);
         
         builder1.add("index", builder);


        return builder1.build();
    }
    
    public JsonObject toJSONMetaDataVersionning()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
         JsonObjectBuilder builder1 = Json.createObjectBuilder();
         
         builder.add("_index", index);
         builder.add("_type", type);
         builder.add("_id", id);
         builder.add("_version", version);
         builder.add("_version_type", "force");
         
         builder1.add("index", builder);


        return builder1.build();
    }
    
    public JsonObject toJSONMetaDataWithoutID()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
         JsonObjectBuilder builder1 = Json.createObjectBuilder();
         
         builder.add("_index", index);
         builder.add("_type", type);
         
         builder1.add("index", builder);


        return builder1.build();
    }
    
        
    
    
    
}
