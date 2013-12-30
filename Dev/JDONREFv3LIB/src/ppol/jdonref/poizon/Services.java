/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.poizon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ppol.jdonref.JDONREFv3Exception;

/**
 *
 * @author marcanhe
 */
public class Services {

    private final Map<Integer, Service> serviceMap = new HashMap<Integer, Service>();
    private static Services instance;
    
    public static Services getInstance(String configPath) throws JDONREFv3Exception {
        if (instance == null) {
            instance = new Services(configPath);
        }

        return instance;
    }

    private Services(String configPath) throws JDONREFv3Exception {
        final SAXBuilder saxBuilder = new SAXBuilder();
        Document document = null;
        try {
            document = saxBuilder.build(configPath + "services.xml");
        } catch (JDOMException jde) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de services.xml", jde);
            throw new JDONREFv3Exception(1, "Erreur lors de la configuration des services");
        } catch (IOException ioe) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de services.xml", ioe);
            throw new JDONREFv3Exception(1, "Erreur lors de la configuration des services");
        }
        final Element services = document.getRootElement();
        final List serviceList = services.getChildren();
        for (Object serviceObj : serviceList) {
            final Element serviceElement = (Element) serviceObj;
            final int identifiant = Integer.parseInt(serviceElement.getChildText("identifiant"));
            final String nom = serviceElement.getChildText("nom");
            final Element parentElement = serviceElement.getChild("parent");
            int parent = 0;
            if (parentElement != null) {
                parent = Integer.parseInt(parentElement.getText());
            }
            final Service service = new Service(identifiant, nom, parent);
            serviceMap.put(identifiant, service);
        }

        buildChildren();
        buildLeaves();
    }

    private void buildChildren() {
        final Collection<Service> allServices = serviceMap.values();
        for (Service service : allServices) {
            final int parentId = service.getParent();
            if (parentId != 0) {
                final Service parent = serviceMap.get(parentId);
                parent.addChild(service.getIdentifiant());
            }
        }
    }

    private void buildLeaves() {
        final Collection<Service> allServices = serviceMap.values();
        final Set<Integer> setLeaves = new TreeSet<Integer>();
        for (Service service : allServices) {
            setLeaves.clear();
            addLeaves(service, setLeaves);
            final Iterator<Integer> it = setLeaves.iterator();
            while (it.hasNext()) {
                service.addLeaf(it.next());
            }
        }

    }

    private void addLeaves(Service service, Set<Integer> set) {
        final List<Integer> children = service.getChildren();
        if (children.isEmpty()) {
            set.add(service.getIdentifiant());
        } else {
            for (Integer childId : children) {
                addLeaves(serviceMap.get(childId), set);
            }
        }
    }

    public int[] getLeaves(int[] services) {
        final Set<Integer> setOut = new TreeSet<Integer>();
        for (int i = 0; i < services.length; i++) {
            final Service service = serviceMap.get(services[i]);
            setOut.addAll(service.getLeaves());
        }
        int[] arrayRet = new int[setOut.size()];
        final Iterator<Integer> it = setOut.iterator();
        for (int i = 0; i < arrayRet.length; i++) {
            arrayRet[i] = it.next();
        }

        return arrayRet;
    }
    
    public List<String> getNoms(){
        final List<String> listRet = new ArrayList<String>();
        final Collection<Service> services = serviceMap.values();
        for(Service service : services){
            listRet.add(new String(service.getNom()));
        }
        
        return listRet;
    }
    
    public static void main(String[] args){
        try{
        //final Services services = Services.getInstance("C:\\JDONREF_v3\\Dev\\Src\\JPOIZONREFv3\\web\\META-INF\\");
       final Services services = Services.getInstance("C:\\Users\\akchana\\Desktop\\Mon projet\\SVN\\JDONREF_v3 _trunk\\Dev\\Src\\JPOIZON\\web\\META-INF\\");
            final int[] leaves = services.getLeaves(new int[]{100});
        for(Integer leaf : leaves){
            System.out.println(leaf);
        }
        }catch(Exception e ){
            e.printStackTrace();
        }
        
    }
}
