/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public class PropositionGeocodage {

    private int service = 0;
    private String type = "";
    private String x = "";
    private String y = "";
    private String date = "";
    private String projection = "";
    private String referentiel = "";
    private String[] options = new String[0];

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProjection() {
        return projection;
    }

    public void setProjection(String projection) {
        this.projection = projection;
    }

    public String getReferentiel() {
        return referentiel;
    }

    public void setReferentiel(String referentiel) {
        this.referentiel = referentiel;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
    
    
}
