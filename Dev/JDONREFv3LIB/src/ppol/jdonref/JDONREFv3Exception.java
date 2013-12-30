/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref;

/**
 *
 * @author marcanhe
 */
public class JDONREFv3Exception extends Exception{
    
    private static final long serialVersionUID = 1L;

    private final int errorcode;
    private final String message;

    public JDONREFv3Exception(int errorcode, String message) {
        this.errorcode = errorcode;
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }

    public int getErrorcode() {
        return errorcode;
    }
    
    
}
