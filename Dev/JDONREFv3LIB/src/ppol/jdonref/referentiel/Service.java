/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.referentiel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author akchana
 */
public class Service {

    //private final int identifiant;
    private int identifiant;
    private String nom;
    private int parent;
    private int cle;
    private final List<Integer> children = new ArrayList<Integer>();
    private final List<Integer> leaves = new ArrayList<Integer>();

    public Service(int identifiant, String nom, int parent) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.parent = parent;
    }

    public Service(int identifiant, String nom, int parent, int cle) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.parent = parent;
        this.cle = cle;
    }

    public Service() {
    }

    public int getId() {
        return identifiant;
    }

    public String getNom() {
        return nom;
    }

    public int getParent() {
        return parent;
    }

    public int getCle() {
        return cle;
    }

    public List<Integer> getChildren() {
        return children;
    }

    public boolean addChild(int service) {
        return children.add(service);
    }

    public List<Integer> getLeaves() {
        return leaves;
    }

    public boolean addLeaf(int service) {
        return leaves.add(service);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("identifiant[");
        sb.append(identifiant);
        sb.append("] nom[");
        sb.append(nom);
        sb.append("] parent [");
        sb.append(parent);
        sb.append("] children [");
        for (Integer child : children) {
            sb.append(child);
            sb.append(" ");
        }
        sb.append("] leaves [");
        for (Integer leaf : leaves) {
            sb.append(leaf);
            sb.append(" ");
        }
        sb.append("]");

        final String stringRet = sb.toString();

        return stringRet.replace(" ]", "]");

    }
}