package jdonref_es_poc.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Julien
 */
public class Departement {
    public String code_departement;

    public Departement() {
    }

    public Departement(String string) {
        code_departement = string;
    }
    
    public String toString()
    {
        return code_departement;
    }
    
    public JsonObject toJSONDocument()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("toString", toString());
         builder.add("fullName",code_departement);
         builder.add("dpt_nom",code_departement);
        
        return builder.build();
    }
}
