/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class Adresse_csv {
    
    public String id;
    public String nom_voie;
    public String id_fantoir;
    public String numero;
    public String rep;
    public String code_insee;
    public String code_post;
    public String alias;
    public String nom_ld;
    public String nom_afnor;
    public String libelle_acheminement;
    public String x;
    public String y;
    public String lon;
    public String lat;
    public String nom_commune;

    public Adresse_csv(String[] line) {
        this.id = line[0];
        this.nom_voie= line[1];
        this.id_fantoir= line[2];
        this.numero= line[3];
        this.rep= line[4];
        this.code_insee= line[5];
        this.code_post= line[6];
        this.alias= line[7];
        this.nom_ld= line[8];
        this.nom_afnor= line[9];
        this.libelle_acheminement= line[10];
        this.x= line[11];
        this.y= line[12];
        this.lon= line[13];
        this.lat= line[14];
        this.nom_commune= line[15];
    }
    
    public JsonObject toJSONDocument_csv()
    {
        JsonObjectBuilder adresse = Json.createObjectBuilder();
        adresse.add("adr_id",id);
        if (numero!=null) adresse.add("voi_nom",nom_voie);
        if (numero!=null) adresse.add("id_fantoir",id_fantoir);
        if (numero!=null) adresse.add("numero",numero);
        if (numero!=null) adresse.add("repetition",rep);
        if (numero!=null) adresse.add("code_insee",code_insee);
        if (numero!=null) adresse.add("code_postal",code_post);
        if (numero!=null) adresse.add("alias",alias);
        if (numero!=null) adresse.add("nom_ld",nom_ld);
        if (numero!=null) adresse.add("nom_afnor",nom_afnor);
        if (numero!=null) adresse.add("libelle_acheminement",libelle_acheminement);
        if (numero!=null) adresse.add("nom_commune",nom_commune);
        adresse.add("pays","FRANCE");
        adresse.add("type","adresse");
        
        adresse.add("pinXY" , Json.createObjectBuilder().add("centroide", GeomUtil.toGeojson(x+","+y, "point")).build());
        adresse.add("pin" , Json.createObjectBuilder().add("centroide", GeomUtil.toGeojson(lat+","+lon, "point")).build());
        
        return adresse.build();
    }

}