/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.tests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import org.junit.Test;
import ppol.jdonref.GestionOptions;
import ppol.jdonref.JDONREFException;

/**
 *
 * @author moquetju
 */
public class GestionOptionsTests
{
    @Test
    public void test1()
    {
        try {
            GestionOptions opt = GestionOptions.getInstance();
            opt.loadOptions("options.xml");
            String[] options = opt.getOptions(10006);

            for (String option : options) {
                System.out.println(option);
            }
            
            options = opt.getOptions(10006,new String[]{"dpt=75,77,78"});
            for (String option : options) {
                System.out.println(option);
            }
            
            options = opt.getOptions(10005,new String[]{"dpt=75,77,78"});
            for (String option : options) {
                System.out.println(option);
            }
            
            options = opt.getOptions(10005);
            if (options!=null)
                for (String option : options) {
                    System.out.println(option);
                }
        } catch (JDOMException ex) {
            Logger.getLogger(GestionOptionsTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GestionOptionsTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDONREFException ex) {
            Logger.getLogger(GestionOptionsTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
