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
public final class PropositionGeocodageInverseBuilder {

    private final PropositionGeocodageInverse proposition = new PropositionGeocodageInverse();
    private int service = 0;
    private final List<String> donnees = new ArrayList<String>();
    private final List<String> donneesOrigine = new ArrayList<String>();
    private List<String> ids = new ArrayList<String>();
    private String x = "";
    private String y = "";
    private String distance = "";
    private String t0 = "";
    private String t1 = "";
    private String referentiel = "";
    private final List<String> options = new ArrayList<String>();

    public PropositionGeocodageInverseBuilder() {
        for (int i = 0; i < 7; i++) {
            donnees.add("");
            donneesOrigine.add("");
            ids.add("");
        }
    }

    public PropositionGeocodageInverse build() {
        proposition.setService(service);
        proposition.setDonnees(donnees.toArray(new String[donnees.size()]));
        proposition.setDonneesOrigine(donneesOrigine.toArray(new String[donneesOrigine.size()]));
        proposition.setIds(ids.toArray(new String[ids.size()]));
        proposition.setX(x);
        proposition.setY(y);
        proposition.setDistance(distance);
        proposition.setT0(t0);
        proposition.setT1(t1);
        proposition.setReferentiel(referentiel);
        proposition.setOptions(options.toArray(new String[options.size()]));

        return proposition;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setService(int service) {
        this.service = service;
    }

    public void setT0(String t0) {
        this.t0 = t0;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setDonnee(int index, String donnee) {
        this.donnees.set(index, donnee);
    }

    public void addDonnee(String donnee) {
        this.donnees.add(donnee);
    }

    public void setDonneeOrigine(int index, String donnee) {
        this.donneesOrigine.set(index, donnee);
    }

    public void addDonneeOrigine(String donnee) {
        this.donneesOrigine.add(donnee);
    }

    public void setId(int index, String id) {
        this.ids.set(index, id);
    }

    public void addId(String id) {
        this.ids.add(id);
    }

    public void setReferentiel(String referentiel) {
        this.referentiel = referentiel;
    }

    public void addOption(String option) {
        this.options.add(option);
    }
}
