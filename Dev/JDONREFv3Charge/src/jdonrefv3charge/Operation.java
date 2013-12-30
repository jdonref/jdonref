/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdonrefv3charge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.jdom.Element;

/**
 *
 * @author marcanhe
 */
public class Operation {

    private final static Random RANDOM = new Random(Calendar.getInstance().getTimeInMillis());
    private String nom;
    private int operation;
    private final List<Integer> services = new ArrayList<Integer>();
    private final List<Option> options = new ArrayList<Option>();
    private double distanceMin = 1;
    private double distanceMax = 1;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public List<Integer> getServices() {
        return services;
    }

    public void addService(Integer service) {
        services.add(service);
    }

    public void load(Element e) {
        nom = e.getChild("nom").getText();
        operation = Integer.parseInt(e.getChild("operation").getText());
        final List e_services = e.getChildren("service");
        for (int i = 0; i < e_services.size(); i++) {
            services.add(Integer.parseInt(((Element) e_services.get(i)).getText()));
        }
        final List e_options = e.getChildren("option");
        for (int i = 0; i < e_options.size(); i++) {
            final Option option = new Option();
            option.load((Element) e_options.get(i));
            options.add(option);
        }
        final Element e_distance = e.getChild("distance");
        if (e_distance != null) {
            final Element e_min = e_distance.getChild("min");
            distanceMin = (e_min != null) ? Double.valueOf(e_min.getText()) : 1;
            final Element e_max = e_distance.getChild("max");
            distanceMax = (e_max != null) ? Double.valueOf(e_max.getText()) : 1;
        }
    }

    public int[] getServicesArray() {
        final int[] arrayRet = new int[services.size()];
        for (int i = 0; i < arrayRet.length; i++) {
            arrayRet[i] = services.get(i);
        }

        return arrayRet;
    }

    public String[] getOptionsArray() {
        final String[] arrayRet = new String[options.size()];
        for (int i = 0; i < arrayRet.length; i++) {
            arrayRet[i] = options.get(i).getNom() + "=" + options.get(i).getValeur();
        }

        return arrayRet;
    }

    public double getDistance() {
        return  distanceMin + (RANDOM.nextDouble() * (distanceMax - distanceMin));
    }
}
