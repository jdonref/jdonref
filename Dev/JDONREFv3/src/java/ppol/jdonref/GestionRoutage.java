/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref;



import ppol.jdonref.wservice.ServiceParameters;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ppol.jdonref.mots.Abbreviation;
import ppol.jdonref.mots.CategorieMot;
import ppol.jdonref.mots.Mot;
import ppol.jdonref.mots.RefCle;
import ppol.jdonref.mots.RefNumero;
import ppol.jdonref.utils.Utils;
import ppol.jdonref.wservice.IJDONREFv3Router;
import ppol.jdonref.wservice.ServiceDescription;


/**
 *
 * @author marcanhe
 */
public class GestionRoutage implements IJDONREFv3Router {

    private final Map<Integer, ServiceDescription> serviceMap = new HashMap<Integer, ServiceDescription>();
    private final Map<Integer, ServiceDescription> serviceParentMap = new HashMap<Integer, ServiceDescription>();
    private final Map<String, Integer> categorieMap = new HashMap<String, Integer>();
    private ppol.jdonref.referentiel.JDONREFv3Lib jdonrefv3lib;

    // Instantiation par reflection
    public GestionRoutage() throws JDONREFv3Exception {
        
    }
    
    public void init(ppol.jdonref.referentiel.JDONREFv3Lib jdonrefv3lib)throws JDONREFv3Exception{
        this.jdonrefv3lib = jdonrefv3lib;
        loadServices();
    }

    public List<ServiceParameters> normalise(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            listRet.addAll(services(new int[]{1}));
        } else {
            listRet.addAll(services(services));
        }
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setOperation(operation);
            parameters.setDonnees(donnees);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> valide(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] ids,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            // RESTRUCTURATION
            /*
            if ((operation & 2) != 0) {
            donnees = restructure(application, operation, donnees, options);
            operation -= 2;
            }*/
            int[] serviceArray = getServicesFromDonnees(donnees);
            listRet.addAll((serviceArray.length > 0) ? services(serviceArray) : services(getAllServiceParent()));
        } else {
            listRet.addAll(services(services));
        }
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setOperation(operation);
            parameters.setDonnees(donnees);
            parameters.setIds(ids);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> geocode(
            int application,
            int[] services,
            String[] donnees,
            String[] ids,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            throw new JDONREFv3Exception(5, "Aucun service n'est spécifié");
        } else {
            listRet.addAll(services(services));
        }

        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setDonnees(donnees);
            parameters.setIds(ids);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> revalide(
            int application,
            int[] services,
            String[] donnees,
            String[] ids,
            String date,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            throw new JDONREFv3Exception(5, "Aucun service n'est spécifié");
        } else {
            listRet.addAll(services(services));
        }
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setDonnees(donnees);
            parameters.setIds(ids);
            parameters.setDate(date);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> inverse(
            int application,
            int[] services,
            String[] donnees,
            double distance,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            int[] keyArray = getAllServiceParent();
            return services(keyArray);
        } else {
            listRet.addAll(services(services));
        }
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setDonnees(donnees);
            parameters.setDistance(distance);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> decoupe(
            int application,
            int[] services,
            int[] operations,
            String[] donnees,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            int[] serviceArray = getServicesFromDonnees(donnees);
            listRet.addAll((serviceArray.length > 0) ? services(serviceArray) : services(getAllServiceParent()));
        } else {
            listRet.addAll(services(services));
        }
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setOperations(operations);
            parameters.setDonnees(donnees);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> contacte(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        if (Utils.isEmpty(services)) {
            throw new JDONREFv3Exception(5, "Aucun service n'est spécifié");
        } else {
            listRet.addAll(services(services));
        }
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
            parameters.setOperation(operation);
            parameters.setDonnees(donnees);
            parameters.setOptions(options);
        }

        return listRet;
    }

    public List<ServiceParameters> getVersion(
            int application,
            int[] services)
            throws JDONREFv3Exception {
        List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        listRet.addAll(services(services));
        for (ServiceParameters parameters : listRet) {
            parameters.setApplication(application);
        }

        return listRet;

    }   

    private void loadServices() throws JDONREFv3Exception {
        final SAXBuilder saxBuilder = new SAXBuilder();
        Document document = null;
        try {
            document = saxBuilder.build(jdonrefv3lib.getParams().obtientConfigPath() + "routage.xml");
        } catch (JDOMException jde) {
            Logger.getLogger(GestionRoutage.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de routage.xml", jde);
            throw new JDONREFv3Exception(1, "Erreur lors de la configuration du service de routage");
        } catch (IOException ioe) {
            Logger.getLogger(GestionRoutage.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de routage.xml", ioe);
            throw new JDONREFv3Exception(1, "Erreur lors de la configuration du service de routage");
        } catch (Exception ex) {
            Logger.getLogger(GestionRoutage.class.getName()).log(Level.SEVERE, "Problème lors de la lecture de routage.xml", ex);
            throw new JDONREFv3Exception(1, "Erreur lors de la configuration du service de routage");
        }
        final Element routage = document.getRootElement();
        final Element rules = routage.getChild("rules");
        final List rulesList = rules.getChildren();
        for (Object ruleObj : rulesList) {
            final Element rule = (Element) ruleObj;
            final Element webservice = rule.getChild("webservice");
            final String wsdlStr = webservice.getChild("wsdl").getText();
            final String serviceStr = webservice.getChild("service").getText();
            final String uriStr = webservice.getChild("uri").getText();
            final List cleList = rule.getChildren("cle");
            for (Object cleObj : cleList) {
                final Element cle = (Element) cleObj;
                // Services
                final Element identifiantElement = cle.getChild("identifiant");
                final Integer identifiant = Integer.parseInt(identifiantElement.getText());
                final ServiceDescription serviceDescription = new ServiceDescription(wsdlStr, uriStr, serviceStr);
                serviceMap.put(identifiant, serviceDescription);
                final Element numeroLigneElement = cle.getChild("ligne");
                final int numeroLigne = Integer.parseInt(numeroLigneElement.getText());
                final List nomList = cle.getChildren("nom");
                for (Object nomObj : nomList) {
                    final String nom = ((Element) nomObj).getText().trim().toUpperCase();
                    // La cle est composee de la concatenation de la categorie
                    // et du numero de ligne.
                    categorieMap.put(nom + numeroLigne, identifiant);
                }
                final Element parent = cle.getChild("parent");
                if (parent == null) { // Eléments parents
                    serviceParentMap.put(identifiant, serviceDescription);
                }
            }
        }
    }
    
    private int[] getAllServiceParent() {
        final Set<Integer> keySet = serviceParentMap.keySet();
        final int[] keyArray = new int[keySet.size()];
        final Iterator<Integer> it = keySet.iterator();
        for (int i = 0; i < keyArray.length; i++) {
            keyArray[i] = it.next();
        }
        return keyArray;
    }

    private List<Integer> getServicesFromLigne(String ligne, int numeroLigne) {
        final List<Integer> listRet = new ArrayList<Integer>();

        // TRAITEMENT GENERIQUE
       // final Mot mot = jdonrefv3lib.getGestionMots().chercheMotPourRoutage(ligne);
        final Mot mot = ((JDONREFv3Lib)JDONREFv3Lib.getInstance()).getGestionMots().chercheMotPourRoutage(ligne);
        //final Abbreviation abb = jdonrefv3lib.getGestionMots().chercheAbbreviationPourRoutage(ligne);
        final Abbreviation abb = ((JDONREFv3Lib)JDONREFv3Lib.getInstance()).getGestionMots().chercheAbbreviationPourRoutage(ligne);
        List<CategorieMot> categorieList = new ArrayList<CategorieMot>();
        Mot motAbb = null;
        if (abb != null) {
            int priorite = Abbreviation.PRIORITE_MAX;
            final int countMot = abb.obtientCompteMot();
            for (int i = 0; i < countMot; i++) {
                final Mot amot = abb.obtientMot(i);
                final int apriorite = abb.obtientPrioriteMot(i);
                if (apriorite < priorite || (motAbb == null && apriorite == priorite)) {
                    priorite = apriorite;
                    motAbb = amot;
                }
            }
        }
        if (mot != null && abb != null) {
            final int size = motAbb.obtientCompteCategorie();
            for (int i = 0; i < size; i++) {
                categorieList.add((mot.obtientNom().length() >= motAbb.obtientNom().length()) ? mot.obtientCategorie(i) : motAbb.obtientCategorie(i));
            }

        } else if (mot != null && abb == null) {
            final int countCat = mot.obtientCompteCategorie();
            for (int i = 0; i < countCat; i++) {
                categorieList.add(mot.obtientCategorie(i));
            }
        } else if (mot == null && abb != null) {
            final int countCat = motAbb.obtientCompteCategorie();
            for (int i = 0; i < countCat; i++) {
                categorieList.add(motAbb.obtientCategorie(i));
            }
        }

        // TRAITEMENT SPECIFIQUE
        switch (numeroLigne) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                final ArrayList<RefNumero> numeros = ((JDONREFv3Lib)jdonrefv3lib).getGestionMots().trouveNumeros(ligne);
                final RefCle typedevoie = ((JDONREFv3Lib)jdonrefv3lib).getGestionMots().trouveTypeVoie(ligne, numeros);
                if (typedevoie != null) {
                    categorieList.add(CategorieMot.TypeDeVoie);
                }
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            }

        for (CategorieMot categorie : categorieList) {
            final String key = categorie.toString().trim().toUpperCase() + numeroLigne;
            if (categorieMap.containsKey(key)) {
                listRet.add(categorieMap.get(key));
            }
        }

        return listRet;
    }

    private int[] getServicesFromDonnees(String[] donnees) {
        final Set<Integer> serviceSet = new HashSet<Integer>();
        int numero = 1;
        for (String ligne : donnees) {
            if (ligne != null && ligne.trim().length() > 0) {
                serviceSet.addAll(getServicesFromLigne(ligne, numero));
            }
            numero += 1;
        }

        final int[] arrayRet = new int[serviceSet.size()];
        int index = 0;
        for (int serviceId : serviceSet) {
            arrayRet[index] = serviceId;
            index += 1;
        }
        return arrayRet;
    }

    private List<ServiceParameters> services(int[] services) {
        final Map<ServiceDescription, ServiceParameters> map = new HashMap<ServiceDescription, ServiceParameters>();
        for (Integer serviceId : services) {
            if (serviceMap.containsKey(serviceId)) {
                final ServiceDescription description = serviceMap.get(serviceId);
                if (map.containsKey(description)) {
                    final ServiceParameters parameters = map.get(description);
                    parameters.addService(serviceId);
                } else {
                    final ServiceParameters parameters = new ServiceParameters(description);
                    parameters.addService(serviceId);
                    map.put(description, parameters);
                }
            }
        }

        final List<ServiceParameters> listRet = new ArrayList<ServiceParameters>();
        listRet.addAll(map.values());

        return listRet;
    }
}
