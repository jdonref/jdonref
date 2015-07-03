package ppol.jdonref;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Gestion des options par défaut
 * @author moquetju
 */
public class GestionOptions
{
    static protected GestionOptions opt = null;
    
    protected ConcurrentHashMap<Integer,String[]> options = new ConcurrentHashMap<Integer,String[]>();
    
    protected GestionOptions()
    {
    }
    
    public static GestionOptions getInstance()
    {
        if (opt==null)
            opt = new GestionOptions();
        return opt;
    }
    
    public void addOption(int application, String[] options)
    {
        this.options.put(application,options);
    }
    
    public String[] getOptions(Integer application)
    {
        return options.get(application);
    }
    
    public String[] getOptions(Integer application,String[] existingOptions)
    {
        String[] defaultOptions = getOptions(application);
        
        if (defaultOptions==null || defaultOptions.length==0) return existingOptions;
        
        if (existingOptions==null || existingOptions.length==0) return defaultOptions;
        
        String[] res = Arrays.copyOf(defaultOptions, defaultOptions.length + existingOptions.length);
        
        for(int i=0;i<existingOptions.length;i++)
            res[defaultOptions.length+i] = existingOptions[i];
        
        return res;
    }
    
    public void loadOptions(String file) throws JDOMException, IOException, JDONREFException
    {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(file);

        Element root = d.getRootElement();

        if (root.getName().compareTo("options") != 0) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "Le fichier " + file + " est mal structuré (balise " + root.getName() + ").");
            throw (new JDONREFException("Le fichier " + file + " est mal structuré."));
        }
        
        List childs = root.getChildren("option");
        for(Object c : childs)
        {
            Element child = (Element)c;
            int application = Integer.parseInt(child.getChildText("application"));
            
            List options = child.getChildren("options");
            String[] opts = new String[options.size()];
            int count = 0;
            for(Object o : options)
            {
                opts[count++] = ((Element)o).getValue();
            }
            
            addOption(application, opts);
        }
    }
}
