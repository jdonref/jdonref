/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.talend;


import java.util.ArrayList;
import java.util.List;
import ppol.jdonref.talend.client.ResultatNormalisation;
import ppol.jdonref.talend.client.JDONREFService;
import ppol.jdonref.talend.client.JDONREF;

/**
 *
 * @author marcanhe
 */
public class JDONREFv3 {
    
    public static ResultatNormalisation normalise(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options){
        ResultatNormalisation resultat = null;
        try {
            JDONREFService service = Service.getService();
            JDONREF port = service.getJDONREFPort();
           resultat = port.normalise(
                    application, valueOf(services), operation, valueOf(donnees), valueOf(options));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return resultat;
    }
    
    private static List<String> valueOf(String[] anarray){
        final List<String> listRet = new ArrayList<String>();
        for(String str : anarray){
            listRet.add(str);
        }
        
        return listRet;
    }
    
    private static List<Integer> valueOf(int[] anarray){
        final List<Integer> listRet = new ArrayList<Integer>();
        for(Integer integer : anarray){
            listRet.add(integer);
        }
        
        return listRet;
    }
}
