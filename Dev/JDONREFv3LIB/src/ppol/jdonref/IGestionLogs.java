/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref;

/**
 *
 * @author akchana
 */
public interface IGestionLogs {
    
    
    public void definitRepertoire(String repertoire) throws JDONREFException;
    public String obtientRepertoire();
    public int obtientNumeroVersion(int processus);
    
    public void logEchecValidation(int application, boolean statut);
    public void logNormalisation(int application, int flags, boolean statut);
    public void logValidation(int application, String departement, int flags, boolean statut);
    public void logInverse(int application, int operation, boolean statut);
    public void logGeocodage(int application, int qualite, boolean statut);
    public void logVersion(int application);
    public void logContacte(int application, boolean statut);
    public void logDecoupage(int application, boolean statut);
    public void logGetState(int application, boolean statut);
    public void logRevalidation(int application, boolean statut);
    public void logMetaAdmin(int numero, int code, String message);
    public void logAdmin(int processus, int version, String message);
    
    public void logs(String message);
    public void logs(String message, Throwable thrown);
    
    
    

}
