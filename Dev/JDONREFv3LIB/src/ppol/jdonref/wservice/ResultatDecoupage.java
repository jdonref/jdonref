/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public class ResultatDecoupage {
    private int codeRetour = 0;
    private ResultatErreur[] erreurs;
    private PropositionDecoupage[] propositions = new PropositionDecoupage[0];
    private String[] options = new String[0];

    public int getCodeRetour() {
        return codeRetour;
    }

    public void setCodeRetour(int codeRetour) {
        this.codeRetour = codeRetour;
    }

    public PropositionDecoupage[] getPropositions() {
        return propositions;
    }

    public void setPropositions(PropositionDecoupage[] propositions) {
        this.propositions = propositions;
    }

    public ResultatErreur[] getErreurs() {
        return erreurs;
    }

    public void setErreurs(ResultatErreur[] erreurs) {
        this.erreurs = erreurs;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

}
