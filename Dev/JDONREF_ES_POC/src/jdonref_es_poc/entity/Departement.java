package jdonref_es_poc.entity;

import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 *
 * @author Julien
 */
public class Departement {
    public String code_departement;
    public String dpt_projection;      
    public String dpt_referentiel; 
    public Date t0;
    public Date t1;

    public Departement() {
    }

    public Departement(String string) {
        code_departement = string;
    }

    public Departement(String code_departement, String dpt_projection, String dpt_referentiel, Date t0, Date t1) {
        this.code_departement = code_departement;
        this.dpt_projection = dpt_projection;
        this.dpt_referentiel = dpt_referentiel;
        this.t0 = t0;
        this.t1 = t1;
    }

    @Override
    public String toString()
    {
        return code_departement;

    }
    
    
    
    
    public JsonObject toJSONDocument()
    {
         JsonObjectBuilder builder = Json.createObjectBuilder();
//                .add("toString", toString());
         
//         builder.add("fullName",code_departement);
         
         builder.add("dpt_code_departement", code_departement);
         builder.add("dpt_projection", dpt_projection);
         builder.add("dpt_referentiel", dpt_referentiel);
         builder.add("t0" , t0.toString());
         builder.add("t1" , t1.toString());
        
        return builder.build();
    }
    
    
    
}
