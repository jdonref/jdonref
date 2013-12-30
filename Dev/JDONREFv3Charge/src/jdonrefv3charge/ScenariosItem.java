/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdonrefv3charge;

import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author marcanhe
 */
public class ScenariosItem extends AbstractItemsWithProbability {

    public static void main(String[] args) {
        System.out.println("Test de Scenarios ");
        ScenariosItem sr = new ScenariosItem();

        int size = 4;
        int nbtests = 10000;
        int tolerance = 3;
        System.out.println(size + " scenario(s)");
        System.out.println(nbtests + " tests effectué(s)");
        System.out.println("tolerance de " + tolerance);

        ScenarioItem[] d = new ScenarioItem[size];
        int max = 100;
        Random r = new Random(Calendar.getInstance().getTimeInMillis());
        System.out.println("Génère des probabilités de services : ");
        for (int i = 0; i < d.length; i++) {
            d[i] = new ScenarioItem();
            d[i].scenario = Integer.toString(i);

            if (i < d.length - 1 && max > 0) {
                d[i].probabilite = r.nextInt(max);
                max -= d[i].probabilite;
            } else {
                d[i].probabilite = max;
            }

            System.out.println(Integer.toString(i) + " : " + d[i].probabilite);
            sr.items.add(d[i]);
        }

        System.out.println("Teste les probabilités induites : ");
        int[] proba = new int[size];
        for (int i = 0; i < nbtests; i++) {
            String dep = sr.obtientScenarioItem();
            proba[Integer.parseInt(dep)]++;
        }
        int erreurs = 0;
        for (int i = 0; i < size; i++) {
            proba[i] *= 100;
            proba[i] /= nbtests;
            System.out.println(Integer.toString(i) + " : " + proba[i]);
            // un écart de 3 est toléré
//            if (Math.abs(proba[i]-ds.departements.get(i).probabilite)>tolerance)
            if (Math.abs(proba[i] - sr.items.get(i).probabilite) > tolerance) {
                System.out.println("Erreur");
                erreurs++;
            }
        }
        if (erreurs > 0) {
            System.out.println(erreurs + " ont été constatée(s).");
        } else {
            System.out.println("Aucune erreur");
        }
    }

    /**
     * Obtient un numéro de département selon les probabilités définies.
     * @return null peut être retourné si la somme n'est pas 100.
     */
    public String obtientScenarioItem() {
        AbstractItemWithProbability item = obtientItem();
        return (item == null) ? null : ((ScenarioItem) item).scenario;
    }

    /**
     * Retourne le scenarioItem d'index i
     * @param i
     * @return
     */
    public ScenarioItem getScenarioItem(int i) {
        return (ScenarioItem) items.get(i);
    }

    /**
     * Ajoute un departement aux items
     * @param scenarioItem
     */
    public void addScenarioItem(ScenarioItem scenarioItem) {
        items.add(scenarioItem);
    }

    @Override
    protected String getItemXmlDesignation() {
        return "scenario";
    }

    @Override
    protected AbstractItemWithProbability getNewEmptyItem() {
        return new ScenarioItem();
    }
}
