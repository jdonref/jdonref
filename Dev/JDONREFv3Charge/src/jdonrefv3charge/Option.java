/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonrefv3charge;

import org.jdom.Element;

/**
 *
 * @author marcanhe
 */
public class Option {
    
    private String nom;
    private String valeur;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
    
    public void load(Element e){
        nom = e.getChild("nom").getText();
        valeur = e.getChild("valeur").getText();
    }   
}
