package ppol.log4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Permet de créer un fichier de log à chaque changement dans le pattern de date définie
 *
 */
public class DatedRollingFileAppender extends FileAppender {

    /**
     * Datepattern
     */
    //private String datePattern = "yyyy-MM-dd";
    private String datePattern = "dd-MM-yyyy";
    /**
     * Date in use for the actual logs file
     */
    private String dateFileInUse = null;
    /**
     * The file pattern name
     */
    private String fileNamePattern = "%date%_logs.log";

    public DatedRollingFileAppender() {
    }

    public String getDatePattern() {
        return datePattern;
    }

    /**
     * Set Date pattern
     * This date pattern is used to define the time interval between two log
     * Le pattern de la date est aussi utilisé pour définir l'interval de temps entre chaque création de fichier de log
     *
     * <i>Exemple</i>
     * <ul><li>
     * datePattern = yyyy-MM-dd<br />
     * <br />
     * actualDate = 2010-12-01 // the date of today<br />
     * dateFileInUse 2010-12-01 // creation date of the log file<br />
     * <br />
     * actualDate == dateFileInUse -> no file is created<br /><br />
     *
     * </li>
     * <li>
     * datePattern = yyyy-MM-dd<br />
     * <br />
     * actualDate = 2010-12-02 // the date of today<br />
     * dateFileInUse 2010-12-01 // creation date of the log file<br />
     * <br />
     * actualDate != dateFileInUse -> file is created with the actualDate
     * </li>
     * </ul>
     *
     * @param datePattern
     */
    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    /**
     * Return the file pattern.
     */
//    public String getFile() {
//        return fileNamePattern;
//    }

//    /**
//     * Set the file paterne name.
//     * %date% will be replace with the current date
//     *
//     * @param logs file name pattern
//     */
//    public void setFile(String str)
//    {
//        super.setFile(str);
//        if (fileNamePattern==null)
//            fileNamePattern = str;
//        AdminLoggingEvent e = new AdminLoggingEvent(0,0);
//        changeFileName(0,0);
//    }
    
    @Override
    public
  synchronized
  void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
                                                            throws IOException {
        if (fileName.contains("%date%"))
        {
            fileNamePattern = fileName;
            this.fileName = changeFileName(0,0);
        }
        super.setFile(this.fileName,append,bufferedIO,bufferSize);
    }

    /**
     * Called by AppenderSkeleton.doAppend() to write a log message formatted
     * according to the layout defined for this appender.
     */
    public void append(LoggingEvent event) {
        if (this.layout == null) {
            errorHandler.error("No layout set for the appender named [" + name + "].");
            return;
        }
        
// va créer ou non un nouveau fichier de log
        makeNewFileLog(event);
        if (this.qw == null) { // should never happen
            errorHandler.error("No output stream or file set for the appender named [" + name + "].");
            return;
        }
        subAppend(event);
    }

    private String changeFileName()
    {
        String fileName = fileNamePattern;
// si la date en cours d'utilisation est différente de l'actuelle (toujours en fonction du pattern)
        if (datePattern != null && fileNamePattern != null)
        {
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                String strDate = sdf.format(new Date());
                dateFileInUse = strDate;
                fileName = fileName.replaceAll("%date%", strDate);
        }
        return fileName;
    }
    
    private String changeFileName(int version, int processus)
    {
        String fileName = changeFileName();
        if (fileNamePattern != null && fileNamePattern.contains("%version%"))
        {           
             fileName = fileName.replaceAll("%version%", Integer.toString(version));
             fileName = fileName.replaceAll("%processus%", Integer.toString(processus));
        }
        return fileName;
    }
    
    /**
     * Va créer ou non un nouveau fichier de log
     */
    private void makeNewFileLog(LoggingEvent event) {
        if (fileNamePattern.contains("%version%"))
        {
            AdminLoggingEvent e = (AdminLoggingEvent)event;
            this.fileName = changeFileName(e.version,e.processus);
        }
        else
            this.fileName = changeFileName();
        activateOptions();
    }
}