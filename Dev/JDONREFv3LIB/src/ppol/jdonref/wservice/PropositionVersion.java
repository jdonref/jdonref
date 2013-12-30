/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public class PropositionVersion {

    private int service = 0;
    private String nom = "";
    private String version = "";
    private String[] options = new String[0];
    

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }
    
    
    

}
