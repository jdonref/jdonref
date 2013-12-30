/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdonrefv3charge;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.jdom.Element;

/**
 *
 * @author marcanhe
 */
public class Scenarios {

    private static Scenarios instance;
    private final Map<String, Scenario> scenarios = new Hashtable<String, Scenario>();

    public static Scenarios getInstance() {
        if (instance == null) {
            instance = new Scenarios();
        }

        return instance;
    }

    public void load(Element e) throws Exception {
        scenarios.clear();
        List e_scenarios = e.getChildren("scenario");
        for (int i = 0; i < e_scenarios.size(); i++) {
            Scenario scenario = new Scenario();
            scenario.load((Element) e_scenarios.get(i));
            scenarios.put(scenario.getNom(), scenario);
        }
    }

    public Scenario obtientScenario(String nom) {
        return scenarios.get(nom);
    }

    public static boolean estPredefini(String nom) {
        final String nomScenario = (nom != null) ? nom.trim() : "";
        
        return nomScenario.equals("a") ||
                nomScenario.equals("b") ||
                nomScenario.equals("c") || 
                nomScenario.equals("d") || 
                nomScenario.equals("e") || 
                nomScenario.equals("f") || 
                nomScenario.equals("g") || 
                nomScenario.equals("h");
    }
}
