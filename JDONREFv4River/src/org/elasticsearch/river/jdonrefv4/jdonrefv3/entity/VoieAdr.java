package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Julien
 */
public class VoieAdr
{
    public int idvoie; //voi_id
    public int numero;
    public String repetition;
    public String typedevoie; //voi_type_de_voie
    public String article;
    public String libelle; //voi_lbl
    public String ligne4;

    public VoieAdr(ResultSet rs) throws SQLException
    {
        Voie v = new Voie(rs,new int[]{10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27},new int[]{1,2,3,4,5,6,7,8,9,15});
        
        this.numero = Integer.parseInt(rs.getString("adr_numero"));
        this.repetition = rs.getString("adr_rep");
        this.typedevoie = v.typedevoie;
        this.article = v.article;
        this.libelle = v.libelle;
        this.ligne4 = this.numero+" "+((this.repetition!=null&&this.repetition.trim().length()>0)?(this.repetition+" "):"")+v.toLigne4().trim();
    }

    VoieAdr(Voie v, int numero, String repetition) {
        this.numero = numero;
        this.repetition = repetition;
        this.typedevoie = v.typedevoie;
        this.article = v.article;
        this.libelle = v.libelle;
        this.ligne4 = this.numero+" "+((this.repetition!=null&&this.repetition.trim().length()>0)?(this.repetition+" "):"")+v.toLigne4().trim();
    }
    
    @Override
    public String toString()
    {
        return ligne4;
    }
    
    public String getDatForm(Date d){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(d);
    }        
    
    public JsonObject toJSONDocument(boolean withGeometry)
    {
        JsonObjectBuilder voie = Json.createObjectBuilder();
        
        voie.add("numero",numero);
        if (repetition!=null)
            voie.add("repetition",repetition);
        if (typedevoie!=null)
            voie.add("typedevoie",typedevoie);
        if (article!=null)
            voie.add("article",article);
        voie.add("libelle",libelle);
        voie.add("ligne4",ligne4);
     
        return voie.build();
    }
}