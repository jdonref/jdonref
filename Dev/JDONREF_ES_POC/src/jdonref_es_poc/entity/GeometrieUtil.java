/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdonref_es_poc.entity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author ROG
 */
public class GeometrieUtil {

   static GeometrieUtil instance = null; 
   public static GeometrieUtil getInstance() {
        if (instance == null) {
            instance = new GeometrieUtil();
        }
        return instance;
    }
    public GeometrieUtil() {
    }
    
    public float lat;
    public float lon;
//   public double lat;
//   public double lon;

    public void setXY(String string, int deb, int fin) {
        string = string.substring(deb, fin);
        String[] xy = string.split(" ");
        lat = Float.parseFloat(xy[0]);
        lon = Float.parseFloat(xy[1]);
//        lat = Double.parseDouble(xy[0]);
//        lon = Double.parseDouble(xy[1]);
    }

    public String getGeoTYPE(String geometrie) {
        String typeGeo = "";
        if (geometrie.equals("") || geometrie == null) {
            typeGeo = "";
        } else {
            for (int i = 0; i < geometrie.length(); i++) {
                if (geometrie.charAt(i) == '(' && geometrie.charAt(i - 1) != '(') {
                    return typeGeo = geometrie.substring(0, i);
                } else {
                    typeGeo = "";
                }
            }
        }
        return typeGeo;
    }
    
    //laisse uniquement un espace entre les X Y
    public String DeleteSpace(String geometrie){
        geometrie = geometrie.trim(); 
        String geoWithout=" ";
        int j=0;
        int i=1;
        while(i<geometrie.length()-1){
            if(geometrie.charAt(i)==' '                  
                    && (!Character.isDigit(geometrie.charAt(i-1))
                    || !Character.isDigit(geometrie.charAt(i+1))))
                if(!(Character.isDigit(geometrie.charAt(i+1))
                && Character.isDigit(geoWithout.charAt(geoWithout.length()-1)))){
                    
                geoWithout+=geometrie.substring(j,i);
                j=i+1;
            }
            i++;
        }
        geoWithout+=geometrie.substring(j,geometrie.length());
        return geoWithout.trim();
    }

    public JsonArray getGeoJSON(String geometrie, String type) {
        geometrie=DeleteSpace(geometrie);
        
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        if (type.equals("POINT")) {
            setXY(geometrie, type.length() + 1, geometrie.length() - 1);
            coordinates.add(lat);
            coordinates.add(lon);
        }
        if (type.equals("LINESTRING") || type.equals("MULTIPOINT")) {
            geometrie = geometrie.substring(type.length() + 1, geometrie.length() - 1);
            String[] tabxy = geometrie.split(",");
            for (int i = 0; i < tabxy.length; i++) {
                setXY(tabxy[i], 0, tabxy[i].length());
                JsonArrayBuilder coord1 = Json.createArrayBuilder();
                coord1.add(lat);
                coord1.add(lon);
                coordinates.add(coord1);
            }
        }
        if (type.equals("POLYGON") || type.equals("MULTILINESTRING")) {
            geometrie = geometrie.substring(type.length() + 2, geometrie.length() - 2);
            String[] tab = geometrie.split("\\),\\(");
            for (int j = 0; j < tab.length; j++) {
                JsonArrayBuilder coord2 = Json.createArrayBuilder();
                String[] tabxy = tab[j].split(",");
                for (int i = 0; i < tabxy.length; i++) {
                    setXY(tabxy[i], 0, tabxy[i].length());
                    JsonArrayBuilder coord1 = Json.createArrayBuilder();
                    coord1.add(lat);
                    coord1.add(lon);
                    coord2.add(coord1);
                }
                coordinates.add(coord2);
            }
        }
        if (type.equals("MULTIPOLYGON")) {
            geometrie = geometrie.substring(type.length() + 3, geometrie.length() - 3);
            String[] tab1 = geometrie.split("\\)\\),\\(\\(");
            for (int k = 0; k < tab1.length; k++) {
                JsonArrayBuilder coord3 = Json.createArrayBuilder();
                String[] tab = tab1[k].split("\\),\\(");
                for (int j = 0; j < tab.length; j++) {
                    JsonArrayBuilder coord2 = Json.createArrayBuilder();
                    String[] tabxy = tab[j].split(",");
                    for (int i = 0; i < tabxy.length; i++) {
                        setXY(tabxy[i], 0, tabxy[i].length());
                        JsonArrayBuilder coord1 = Json.createArrayBuilder();
                        coord1.add(lat);
                        coord1.add(lon);
                        coord2.add(coord1);
                    }
                    coord3.add(coord2);
                }
                coordinates.add(coord3);
            }
        }
        return coordinates.build();
    }
}
