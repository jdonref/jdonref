/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 *
 * @author Naj
 */
public class AdminLoggingEvent extends LoggingEvent
{
    int processus;
    public void setProcessus(int processus) {
        this.processus = processus;
    }
    
    int version;
    public void setVersion(int version) {
        this.version = version;
    }

    public int getProcessus() {
        return processus;
    }

    public int getVersion() {
        return version;
    }

    public AdminLoggingEvent(int processus, int version) {
        super(null,null,null,null,null);
        this.processus = processus;
        this.version = version;
    }
    
    /**
     Instantiate a LoggingEvent from the supplied parameters.

     <p>Except {@link #timeStamp} all the other fields of
     <code>LoggingEvent</code> are filled when actually needed.
     <p>
     @param logger The logger generating this event.
     @param level The level of this event.
     @param message  The message of this event.
     @param throwable The throwable of this event.  */
  public AdminLoggingEvent(String fqnOfCategoryClass, Category logger,
		      Priority level, Object message, Throwable throwable) {
    super(fqnOfCategoryClass,logger,level,message,throwable);
  }
}
