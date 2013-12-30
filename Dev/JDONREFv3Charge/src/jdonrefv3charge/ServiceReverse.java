/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdonrefv3charge;

/**
 *
 * @author marcanhe
 */
public class ServiceReverse extends AbstractItemWithProbability {

    String serviceReverse;

    @Override
    protected void setItemName(String itemName) {
        serviceReverse = itemName;
    }

    @Override
    protected String getItemXmlDesignation() {
        return "service";
    }

    @Override
    protected String getItemName() {
        return "service_reverse";
    }

    @Override
    protected String specificToString() {
        return "Service_reverse : service= " + serviceReverse;
    }
}
