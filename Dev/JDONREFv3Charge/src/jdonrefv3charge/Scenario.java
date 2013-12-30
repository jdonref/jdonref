/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdonrefv3charge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom.Element;

/**
 *
 * @author marcanhe
 */
public class Scenario {
    
    private String nom;
    private final List<Operation> operations = new ArrayList<Operation>();
    
    public void load(Element e){
       nom = e.getChild("nom").getText();
       final List e_operations = e.getChildren("operation");
       for(int i = 0 ; i < e_operations.size() ; i++){
           final Operation operation = new Operation();
           operation.load((Element)e_operations.get(i));
           operations.add(operation);
       }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Operation> getOperations() {
        return Collections.unmodifiableList(operations);
    }
    
    public void addOperation(Operation operation){
        operations.add(operation);
    }    
    
}
