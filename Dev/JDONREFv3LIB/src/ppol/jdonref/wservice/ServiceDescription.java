package ppol.jdonref.wservice;

import java.util.ArrayList;

/**
 *
 * @author marcanhe
 */
public class ServiceDescription {
    private final String wsdl;
    private final String uri;
    private final String service;
    private final ArrayList<String> operations;

    public ServiceDescription(String wsdl, String uri, String service) {
        this.wsdl = wsdl;
        this.uri = uri;
        this.service = service;
        this.operations = new ArrayList<String>();
    }
    
    public void addOperation(String operation)
    {
        operations.add(operation);
    }
    
    public void addAllOperation(ArrayList<String> operations)
    {
        this.operations.addAll(operations);
    }
    
    public boolean isOperation(String operation)
    {
        return operations.contains(operation);
    }

    public String getService() {
        return service;
    }

    public String getUri() {
        return uri;
    }

    public String getWsdl() {
        return wsdl;
    }

    @Override
    public String toString() {
        
        return wsdl + "|" + uri + "|" + service;
    }

    @Override
    public int hashCode() {

        return wsdl.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean booleanRet = false;
        if (obj instanceof ServiceDescription) {
            final ServiceDescription description = (ServiceDescription) obj;
            booleanRet = wsdl.equals(description.getWsdl());
        }

        return booleanRet;
    }
}
