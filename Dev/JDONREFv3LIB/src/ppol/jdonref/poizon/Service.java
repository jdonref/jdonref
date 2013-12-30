/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.poizon;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcanhe
 */
public class Service{

    private final int identifiant;
    private final String nom;
    private final int parent;
    private final List<Integer> children = new ArrayList<Integer>();
    private final List<Integer> leaves = new ArrayList<Integer>();

    public Service(int identifiant, String nom, int parent) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.parent = parent;
    }

    public int getIdentifiant() {
        return identifiant;
    }

    public String getNom() {
        return nom;
    }

    public int getParent() {
        return parent;
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
    
    public boolean addLeaf(int service){
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
