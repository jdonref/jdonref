/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public class PropositionValidation {
    
    private int service = 1;
    private int code = 0;
    private String[] donnees = new String[0];
    private String[] ids = new String[0];
    private String[] donneesOrigine = new String[0];
    private String note = "";
    private String t0 = "";
    private String t1 = "";
    private String[] options = new String[0];

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }    

    public String[] getDonnees() {
        return donnees;
    }

    public void setDonnees(String[] donnees) {
        this.donnees = donnees;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String[] getDonneesOrigine() {
        return donneesOrigine;
    }

    public void setDonneesOrigine(String[] donneesOrigine) {
        this.donneesOrigine = donneesOrigine;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getT0() {
        return t0;
    }

    public void setT0(String t0) {
        this.t0 = t0;
    }

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
}
