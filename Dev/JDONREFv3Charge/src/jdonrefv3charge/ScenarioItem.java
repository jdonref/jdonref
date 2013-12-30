package jdonrefv3charge;

/**
 *
 * @author marcanhe
 */
public class ScenarioItem extends AbstractItemWithProbability {

    String scenario;

    @Override
    protected void setItemName(String itemName) {
        scenario = itemName;
    }

    @Override
    protected String getItemXmlDesignation() {
        return "nom";
    }

    @Override
    protected String getItemName() {
        return "scenario";
    }

    @Override
    protected String specificToString() {
        return "Scenario : nom = " + scenario;
    }
}
