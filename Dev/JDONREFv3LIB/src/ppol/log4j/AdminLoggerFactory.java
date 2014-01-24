/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.RootLogger;

/**
 *
 * @author Naj
 */
public class AdminLoggerFactory implements LoggerFactory
{
    public Logger makeNewLoggerInstance(String arg0)
    {
        if (arg0.equals("logAdmin"))
            return new AdminLogger(arg0);
        else
            return new RootLogger(Level.DEBUG);
    }
}