/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcanhe
 */
public final class PropositionNormalisationBuilder {

    private final PropositionNormalisation proposition = new PropositionNormalisation();
    private int service = 0;
    private final List<String> donnees = new ArrayList<String>();
    private final List<String> options = new ArrayList<String>();

    public PropositionNormalisationBuilder() {
        for (int i = 0; i < 6; i++) {
            donnees.add("");
        }
    }

    public PropositionNormalisation build() {
        proposition.setService(service);
        proposition.setDonnees(donnees.toArray(new String[donnees.size()]));
        proposition.setOptions(options.toArray(new String[options.size()]));

        return proposition;
    }

    public void setService(int service) {
        this.service = service;
    }

    public void setDonnee(int index, String donnee) {
        this.donnees.set(index, donnee);
    }

    public void addDonnee(String donnee) {
        this.donnees.add(donnee);
    }

    public void addOption(String option) {
        this.options.add(option);
    }
}
