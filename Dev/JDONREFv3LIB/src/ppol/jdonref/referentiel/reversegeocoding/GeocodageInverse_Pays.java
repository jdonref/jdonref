/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.referentiel.reversegeocoding;

import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author marcanhe
 */
public class GeocodageInverse_Pays extends GeocodageInverse {

    private String ligne7 = "";
    private String ligne7Origine = "";
    private String sovA3 = "";

    public GeocodageInverse_Pays(Point pt) {
        super(pt);
        this.precision = GestionInverse.GestionInverse_PAYS;
    }

    public String getLigne1() {
        return "";
    }

    public String getLigne2() {
        return "";
    }

    public String getLigne3() {
        return "";
    }

    public String getLigne4() {
        return "";
    }

    public String getLigne4Origine() {
        return "";
    }

    public String getLigne5() {
        return "";
    }

    public String getLigne6() {
        return "";
    }

    public String getLigne6Origine() {
        return "";
    }

    public String getId() {

        return "";
    }

    public String getCodeInsee() {

        return "";

    }

    public String getLigne7() {
        return ligne7;
    }

    public String getLigne7Origine() {
        return ligne7Origine;
    }

    public String getSovA3() {
        return sovA3;
    }

    public void setLigne7(String ligne7) {
        this.ligne7 = ligne7;
    }

    public void setLigne7Origine(String ligne7Origine) {
        this.ligne7Origine = ligne7Origine;
    }

    public void setSovA3(String sovA3) {
        this.sovA3 = sovA3;
    }
    
    
}
