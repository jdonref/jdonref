/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author akchana
 */
public class GeomUtil {

    public GeomUtil() {
    }
    public double lat;
    public double lon;

    public HashMap<String, String> toHashGeo(String geometrie) {
        geometrie = geometrie.replaceAll("\"", "");
        geometrie = geometrie.substring(1, geometrie.length() - 1);
        String[] geo1 = geometrie.split(":");
        String[] geo2 = geo1[1].split(",");
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put(geo1[0], geo2[0].toLowerCase());
        hash.put(geo2[1], geo1[2]);
        return hash;
    }

    public String toGeoTYPE(String geometrie) {
        return toHashGeo(geometrie).get("type");
    }

    public String toGeoCoor(String geometrie) {
        return toHashGeo(geometrie).get("coordinates");
    }

    public void setXY(String string) {
        string = string.substring(0, string.length());
        String[] xy = string.split(",");
        lat = Double.parseDouble(xy[0]);
        lon = Double.parseDouble(xy[1]);
    }

    public JsonArrayBuilder toGeojsonPoint(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        setXY(geometrie);
        coordinates.add(lat);
        coordinates.add(lon);
        return coordinates;
    }

    public JsonArrayBuilder toGeojsonLineStringMultiPoint(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        String[] point = geometrie.split("\\],\\[");
        for (int i = 0; i < point.length; i++) {
            coordinates.add(toGeojsonPoint(point[i]));
        }
        return coordinates;
    }

    public JsonArrayBuilder toGeojsonPolygonMultiLineString(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        String[] polyM = geometrie.split("\\]\\],\\[\\[");
        for (int i = 0; i < polyM.length; i++) {
            coordinates.add(toGeojsonLineStringMultiPoint(polyM[i]));
        }
        return coordinates;
    }

    public JsonArrayBuilder toGeojsonMultiPolygon(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        String[] multiPoly = geometrie.split("\\]\\]\\],\\[\\[\\[");
        for (int i = 0; i < multiPoly.length; i++) {
            coordinates.add(toGeojsonPolygonMultiLineString(multiPoly[i]));
        }
        return coordinates;
    }

    public JsonArray toGeojson(String geometrie, String type) {
//        geometrie=DeleteSpace(geometrie);
        geometrie = geometrie.replaceAll(" ", "");

        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        if (type.equals("point")) {
            geometrie = geometrie.substring(1, geometrie.length() - 1);
            coordinates = toGeojsonPoint(geometrie);
        }
        else if (type.equals("linestring") || type.equals("multipoint")) {
            geometrie = geometrie.substring(2, geometrie.length() - 2);
            coordinates = toGeojsonLineStringMultiPoint(geometrie);
        }
        else if (type.equals("polygon") || type.equals("multilinestring")) {
            geometrie = geometrie.substring(3, geometrie.length() - 3);
            coordinates = toGeojsonPolygonMultiLineString(geometrie);
        }
        else if (type.equals("multipolygon")) {
            geometrie = geometrie.substring(4, geometrie.length() - 4);
            coordinates = toGeojsonMultiPolygon(geometrie);
        }
        else 
            throw new Error("type geometrie inconnu");
        return coordinates.build();
    }
}