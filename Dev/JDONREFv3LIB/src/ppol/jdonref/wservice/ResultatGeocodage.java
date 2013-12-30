/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public class ResultatGeocodage {
    
    private int codeRetour = 0;
    private ResultatErreur[] erreurs = new ResultatErreur[0];
    private PropositionGeocodage[] propositions = new PropositionGeocodage[0];
    private String[] options = new String[0];

    public int getCodeRetour() {
        return codeRetour;
    }

    public void setCodeRetour(int codeRetour) {
        this.codeRetour = codeRetour;
    }

    public ResultatErreur[] getErreurs() {
        return erreurs;
    }

    public void setErreurs(ResultatErreur[] erreurs) {
        this.erreurs = erreurs;
    }

    public PropositionGeocodage[] getPropositions() {
        return propositions;
    }

    public void setPropositions(PropositionGeocodage[] propositions) {
        this.propositions = propositions;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

}
