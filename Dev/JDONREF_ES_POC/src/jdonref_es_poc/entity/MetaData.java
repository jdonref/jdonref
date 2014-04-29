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

    public MetaData() {
    }

    
    
    public MetaData(String index, String type, int id) {
        this.index = index;
        this.type = type;
        this.id = id;
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
    
    
    
    
}
