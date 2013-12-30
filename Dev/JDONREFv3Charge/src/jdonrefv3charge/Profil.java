/*
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
 * julien.moquet@interieur.gouv.fr
 * 
 * Ce logiciel est un service web servant à valider et géocoder des adresses postales.
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant 
 * les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier 
 * et/ou redistribuer ce programme sous les conditions de la licence CeCILL telle que 
 * diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info".
 * En contrepartie de l'accessibilité au code source et des droits de copie, de 
 * modification et de redistribution accordés par cette licence, il n'est offert aux 
 * utilisateurs qu'une garantie limitée.  Pour les mêmes raisons, seule une 
 * responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits 
 * patrimoniaux et les concédants successifs.
 * A cet égard l'attention de l'utilisateur est attirée sur les risques associés au 
 * chargement,  à l'utilisation,  à la modification et/ou au développement et à la 
 * reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel 
 * libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des 
 * développeurs et des professionnels avertis possédant  des  connaissances 
 * informatiques approfondies.  Les utilisateurs sont donc invités à charger  et tester
 * l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser
 * et l'exploiter dans les mêmes conditions de sécurité. 
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris 
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes.
 */
package jdonrefv3charge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import org.jdom.Element;

/**
 * Permet de gérer un profil.
 * @author jmoquet
 */
public class Profil {

    static Random r = new Random(Calendar.getInstance().getTimeInMillis());
    /**
     * Le nombre de fautes au maximum (supérieur à 1).
     */
    static int n = 5;
    String nom;
    int nbusers;
    Acces acces;
    boolean restruct;
    boolean reverse;
    boolean geocod;
    Hashtable<String, Integer> qualitecontenu = new Hashtable<String, Integer>();
    Structure structure = null;
    int probazero;
    int probaun;
    Departements departements = new Departements();
    ServicesReverse servicesReverse = new ServicesReverse();
    ScenariosItem scenariosItem = new ScenariosItem();
    Payss pays = new Payss();
    LogFile logs = new LogFile();
    Adresses adresses = new Adresses();

    /**
     * Constructeur par défaut.
     */
    public Profil() {
    }

    /**
     * Charge les adresses du profil.
     */
    public void loadAdresses(String repertoire) throws FileNotFoundException, IOException, Exception {
        adresses.load(repertoire, nom);
    }

    /**
     * Ecrit les adresses du profil.
     * @param repertoire
     */
    public void writeAdresses(String repertoire) throws IOException {
        adresses.write(repertoire, nom);
    }

    /**
     * Obtient une adresse aléatoire
     * @return
     */
    public Adresse getAdresse() {
        final Adresse adresseRet = adresses.getAdresse();
        adresseRet.serviceReverse = servicesReverse.obtientServiceReverse();
        return adresseRet;
    }

    /**
     * Obtient un nombre de fautes aléatoires.<br>
     * La probabilité d'avoir zéro fautes est donnée par probazero.<br>
     * La proabilité d'avoir une faute est donnée par probaun.<br>
     * Les autres probabilités sont déduites linéairement.
     * @return
     */
    public int getFautes() {
        float proba = r.nextInt(100);

        if (proba < probazero) {
            return 0;
        } 
        if (proba < probaun + probazero) {
            return 1;
        }

        proba /= 100;

        float sum = ((float) (probaun + probazero)) / 100;
        float c = 2 * ((n) * (((float) probaun) / 100) + (((float) probazero) / 100) - 1) / ((n - 1) * (n));
        float delta = ((float) probaun) / 100 - c;
        for (int x = 2; x < n; x++) {
            sum += delta;
            if (proba < sum) {
                return x;
            }
            delta -= c;
        }

        return n;
    }

    /**
     * Teste la classe.
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Teste getFautes");
        Profil p = new Profil();
        p.probazero = 89;
        p.probaun = 4;
        int count = 10000;
        int[] proba = new int[p.n + 1];
        for (int i = 0; i < count; i++) {
            int fautes = p.getFautes();
            proba[fautes]++;
        }
        for (int i = 0; i < p.n; i++) {
            System.out.println("Proba[" + i + "]=" + proba[i]);
        }
        if (Math.abs(100 * proba[0] / count - p.probazero) > 3 || Math.abs(100 * proba[1] / count - p.probaun) > 3) {
            System.out.println("Erreur");
        } else {
            System.out.println("Aucune erreur");
        }
    }

    /**
     * Obtient aléatoirement un nom de modèle (respectant les probabilités définies).
     * @return null si la somme des probabilités n'est pas 100.
     */
    public String getModele() {
        int index = r.nextInt(100);
        int count = 0;
        Enumeration<String> keys = qualitecontenu.keys();
        while (keys.hasMoreElements()) {
            String name = keys.nextElement();
            count += qualitecontenu.get(name).intValue();
            if (index < count) {
                return name;
            }
        }
        return null;
    }
    
    public String getScenario(){
        
        return scenariosItem.obtientScenarioItem();
    }

    /**
     * Charge le profil à partir d'un élément DOM-XML.
     * @param e
     */
    public void load(Element e) throws Exception {
        Element e_nom = e.getChild("nom");
        Element e_nbusers = e.getChild("nbusers");
        Element e_acces = e.getChild("acces");
        Element e_restruct = e.getChild("restructuration");
        Element e_reverse = e.getChild("reverse");
        Element e_geocod = e.getChild("geocodage");
        Element e_qualitecontenu = e.getChild("qualitecontenu");
        Element e_qualitestructure = e.getChild("qualitestructure");
        Element e_fautes = e.getChild("fautes");
        Element e_departements = e.getChild("departements");
        Element e_payss = e.getChild("payss");
        Element e_services_reverse = e.getChild("services_reverse");
        Element e_scenarios = e.getChild("scenarios");

        if (e_nom == null) {
            throw (new Exception("La balise nom est manquante au profil"));
        }
        if (e_nbusers == null) {
            throw (new Exception("La balise nbusers est manquante au profil"));
        }
        if (e_acces == null) {
            throw (new Exception("La balise acces est manquante au profil"));
        }
        
        if (e_scenarios == null) {
            throw (new Exception("La balise scenarios est manquante au profil"));
        }
        
        if (e_qualitecontenu == null) {
            throw (new Exception("La balise qualitecontenu est manquante au profil"));
        }
        if (e_qualitestructure == null) {
            throw (new Exception("La balise qualitestructure est manquante au profil"));
        }
        if (e_fautes == null) {
            throw (new Exception("La balise fautes est manquante au profil"));
        }
        if (e_departements == null) {
            throw (new Exception("La balise departements est manquante au profil"));
        }

        nom = e_nom.getValue();
        try {
            nbusers = Integer.parseInt(e_nbusers.getValue());
        } catch (NumberFormatException nfe) {
            throw (new Exception("Le nombre d'utilisateur est erroné", nfe));
        }
        acces = Acces.load(e_acces);
        restruct = e_restruct != null;
        reverse = e_reverse != null;
        geocod = e_geocod != null;

        List el_qualitecontenu = e_qualitecontenu.getChildren();
        for (int i = 0; i < el_qualitecontenu.size(); i++) {
            Element qualite = (Element) el_qualitecontenu.get(i);
            String q_nom = qualite.getName();
            try {
                int valeur = Integer.parseInt(qualite.getValue());
                qualitecontenu.put(q_nom, new Integer(valeur));
            } catch (NumberFormatException nfe) {
                throw (new Exception("La probabilité du modèle " + q_nom + " est incorrecte.", nfe));
            }
        }


        Element e_qualitestructure_format = e_qualitestructure.getChild("format");
        if (e_qualitestructure_format == null) {
            throw (new Exception("Le format de la structure n'a pas été spécifié."));
        }
        try {
            switch (Integer.parseInt(e_qualitestructure_format.getValue())) {
                default:
                case 1:
                    structure = new StructureFormat1();
                    break;
                case 2:
                    structure = new StructureFormat2();
                    break;
                case 3:
                    structure = new StructureFormat3();
                    break;
                case 4:
                    structure = new StructureFormat4();
                    break;
            }
        } catch (NumberFormatException nfe) {
            throw (new Exception("Le format de la structure est incorrect (valeur attendues 1 2 ou 3)", nfe));
        }

        List el_qualitestructure_proba = e_qualitestructure.getChildren("proba");
        for (int i = 0; i < el_qualitestructure_proba.size(); i++) {
            try {
                structure.setProba(i, Integer.parseInt(((Element) el_qualitestructure_proba.get(i)).getValue()));
            } catch (NumberFormatException nfe) {
                throw (new Exception("Une proba de structure spécifiée n'est pas correcte.", nfe));
            }
        }

        Element e_probazero = e_fautes.getChild("proba0");
        Element e_probaun = e_fautes.getChild("proba1");
        if (e_probazero == null) {
            throw (new Exception("La probabilité d'avoir 0 fautes n'a pas été spécifiée"));
        }
        if (e_probaun == null) {
            throw (new Exception("La probabilité d'avoir 1 faute n'a pas été spécifiée"));
        }

        try {
            probazero = Integer.parseInt(e_probazero.getValue());
            probaun = Integer.parseInt(e_probaun.getValue());
        } catch (NumberFormatException nfe) {
            throw (new Exception("Les probabilités d'avoir des fautes sont incorrectes.", nfe));
        }

        departements.load(e_departements);
        if (e_services_reverse != null) {
            servicesReverse.load(e_services_reverse);
        }
        
        if (e_scenarios != null) {
            scenariosItem.load(e_scenarios);
        }
        pays.load(e_payss);
    }
}
