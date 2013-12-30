/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.wservice;

/**
 *
 * @author marcanhe
 */
public class ServiceDescription {


    private final String wsdl;
    private final String uri;
    private final String service;

    public ServiceDescription(String wsdl, String uri, String service) {
        this.wsdl = wsdl;
        this.uri = uri;
        this.service = service;
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
