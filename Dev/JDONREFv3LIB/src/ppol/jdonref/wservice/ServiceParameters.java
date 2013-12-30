/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.wservice;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author marcanhe
 */
public class ServiceParameters {

    private final ServiceDescription description;
    private final Set<Integer> serviceIdentifiantSet = new HashSet<Integer>();
    private int application;
    private int operation;
    private String[] donnees;
    private String[] ids;
    private String[] options;
    private double distance;
    private String date;
    private int[] operations;

    public ServiceParameters(ServiceDescription description) {
        this.description = description;
    }

    public String getService() {
        return description.getService();
    }

    public String getUri() {
        return description.getUri();
    }

    public String getWsdl() {
        return description.getWsdl();
    }

    public int getApplication() {
        return application;
    }

    public void setApplication(int application) {
        this.application = application;
    }

    public String[] getDonnees() {
        return donnees;
    }

    public void setDonnees(String[] donnees) {
        this.donnees = donnees;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void addService(int service) {
        serviceIdentifiantSet.add(service);
    }

    public int[] getServices() {
        final int[] arrayRet = new int[serviceIdentifiantSet.size()];
        int index = 0;
        for (Integer serviceId : serviceIdentifiantSet) {
            arrayRet[index] = serviceId;
            index++;
        }

        return arrayRet;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int[] getOperations() {
        return operations;
    }

    public void setOperations(int[] operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Integer anint : serviceIdentifiantSet) {
            sb.append(anint);
            sb.append(" ");
        }
        return description.toString() + "|" + sb.toString();
    }

    @Override
    public int hashCode() {

        return description.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean booleanRet = false;
        if (obj instanceof ServiceParameters) {
            final ServiceParameters parameters = (ServiceParameters) obj;
            booleanRet = description.equals(parameters.description);
        }

        return booleanRet;
    }
}
