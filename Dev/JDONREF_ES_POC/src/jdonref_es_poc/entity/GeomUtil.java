/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonref_es_poc.entity;

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

    public HashMap<String, String> getHash(String geometrie) {
        geometrie = geometrie.replaceAll("\"", "");
        geometrie = geometrie.substring(1, geometrie.length() - 1);
        String[] geo1 = geometrie.split(":");
        String[] geo2 = geo1[1].split(",");
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put(geo1[0], geo2[0].toLowerCase());
        hash.put(geo2[1], geo1[2]);
        return hash;
    }

    public String getGeoTYPE(String geometrie) {
        return getHash(geometrie).get("type");
    }

    public String getGeoCoor(String geometrie) {
        return getHash(geometrie).get("coordinates");
    }

    public void setXY(String string) {
        string = string.substring(0, string.length());
        String[] xy = string.split(",");
        lat = Double.parseDouble(xy[0]);
        lon = Double.parseDouble(xy[1]);
    }

    public JsonArrayBuilder pointJSON(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        setXY(geometrie);
        coordinates.add(lat);
        coordinates.add(lon);
        return coordinates;
    }

    public JsonArrayBuilder lineStringMultiPoint(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        String[] point = geometrie.split("\\],\\[");
        for (int i = 0; i < point.length; i++) {
            coordinates.add(pointJSON(point[i]));
        }
        return coordinates;
    }

    public JsonArrayBuilder polygonMultiLineString(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        String[] polyM = geometrie.split("\\]\\],\\[\\[");
        for (int i = 0; i < polyM.length; i++) {
            coordinates.add(lineStringMultiPoint(polyM[i]));
        }
        return coordinates;
    }

    public JsonArrayBuilder multiPolygon(String geometrie) {
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        String[] multiPoly = geometrie.split("\\]\\]\\],\\[\\[\\[");
        for (int i = 0; i < multiPoly.length; i++) {
            coordinates.add(polygonMultiLineString(multiPoly[i]));
        }
        return coordinates;
    }

    public JsonArray getGeoJSON(String geometrie, String type) {
//        geometrie=DeleteSpace(geometrie);
        geometrie = geometrie.replaceAll(" ", "");

        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        if (type.equals("point")) {
            geometrie = geometrie.substring(1, geometrie.length() - 1);
            coordinates = pointJSON(geometrie);
        }
        if (type.equals("linestring") || type.equals("multipoint")) {
            geometrie = geometrie.substring(2, geometrie.length() - 2);
            coordinates = lineStringMultiPoint(geometrie);
        }
        if (type.equals("polygon") || type.equals("multilinestring")) {
            geometrie = geometrie.substring(3, geometrie.length() - 3);
            coordinates = polygonMultiLineString(geometrie);
        }
        if (type.equals("multipolygon")) {
            geometrie = geometrie.substring(4, geometrie.length() - 4);
            coordinates = multiPolygon(geometrie);
        }
        return coordinates.build();
    }
}