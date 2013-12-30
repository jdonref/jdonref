/*
 * JDONREFParams.java
 *
 * Created on 21 mars 2008, 15:57
 *
 * Version 2.2 – Février 2011
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
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes. */
package ppol.jdonref;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Définit les paramètres des fonctionnalités d'équivalences de nom, de géométrie.
 * @author jmoquet
 */
public class JDONREFParams {

    private String[] codeDepartementParDefaut = new String[]{"75", "92", "93", "94"};
    private String version = "2.4";
    private int malusDeTypeDeVoie = 1;
    private int malusPasDeMotDirecteur = 7;
    private int malusPasDeMot = 4;
    private int nombreDePaysParDefaut = 15;
    private int nombreDeCommuneParDefaut = 15;
    private int nombreDeVoieParDefaut = 15;
    private int notePourLibelle = 5;
    private int notePourMotDeterminant = 2;
    private int notePourCommune = 3;
    private int notePourCommuneValide = 15;
    private int notePourCommuneSelectionnee = 10;
    private int notePourCodePostal = 6;    // 
    private int notePourTypeDeVoie = 3;
    private int notePourPaysValide = 15;
    private int notePourNumero = 2;
    private int notePourArrondissement = 1;
    private int pourcentageDeCorrespondanceDeTypeDeVoie = 60;
    private int pourcentageDeCorrespondanceDeMotDeterminant = 65;
    private int pourcentageDeCorrespondanceDeLibelle = 60;
    private int pourcentageDeCorrespondanceDeCommune = 80;
    private int pourcentageDeCorrespondanceDeCodePostal = 40;
    private int pourcentageDeCorrespondanceDePays = 80;
    private int pourcentageDeCorrespondanceDeMot = 81;
    private int seuilDeCorrespondanceDAdresse = 6;
    private double seuilDeDistanceDeVoies = 150;
    private int seuilDeTronconsProches = 3;
    private int seuilDeCorrespondanceDeLibelle = 3;
    private int seuilDeCorrespondanceDeTypeDeVoie = 2;
    private int seuilDeCorrespondanceDeVoie = 10;
    private int seuilDeNombreDeMotsCorrespondant = 1;
    private int seuilDeCorrespondanceDeCommune = 1;
    private int seuilDeCorrespondanceDeCodePostal = 1;
    private int seuilDeCorrespondanceDePays = 1;
    private int tailleDesCles = 30;
    private int tailleMinimaleDAbbreviation = 2;
    private Hashtable<String, Integer> correspondanceTypes = new Hashtable<String, Integer>();
    // chemin vers le répertoire qui contiendra les logs
    private String logPath = "logs";

    // WA 09/2011 Ajout d'un parametre stipulant si on doit utiliser la gestion des departements ou non.
    private boolean utilisationDeLaGestionDesDepartements = false;
    // WA 01/2012 Ajout d'un parametre stipulant si on doit chercher les pays ou non
    private boolean utilisationDeLaGestionDesPays = false;
    // WA 01/2012 Pays par defaut
    private String paysParDefaut = "FRANCE";
    private String projectionPayspardefaut = "4326";
    // HM
    private String projectionPardefaut = "2154";
    private String configPath = "";
    // HM POIZON
    private int notePourCle = 28;
    private int notePourPoizon = 50;
    private int notePourLigne2 = 10;
    private int notePourLigne3 = 10;
    private int notePourLigne4 = 10;
    private int notePourLigne5 = 10;
    private int notePourLigne6 = 10;
    private int notePourLigne7 = 10;
    private int pourcentagePourCle = 60;
    private int pourcentagePourPoizon = 60;
    private int pourcentagePourLigne2 = 80;
    private int pourcentagePourLigne3 = 80;
    private int pourcentagePourLigne4 = 80;
    private int pourcentagePourLigne5 = 80;
    private int pourcentagePourLigne6 = 80;
    private int pourcentagePourLigne7 = 80;
    private int tailleAbbreviationMminimalePoizon = 2;
    
    private String routerClassName="";
    
    private int projectionSpheroidPardefaut = 4326;
    private String spheroidPardefaut = "SPHEROID[\"WGS 84\",6378137,298.257223563]";

    public String obtientConfigPath() {
        return configPath;
    }
    /**
     * setConfigPath recupere le chemin d'acces au fichier donné en parametre 
     * @param file
     */
    public void setConfigPath(String file){
       // int i=file.lastIndexOf("/");
        int i=file.lastIndexOf(File.separatorChar);
        configPath=file.substring(0, i+1);
    }

    /**
     * Obtient le chemin vers le répertoire qui contiendra les logs.
     * 
     * Valeur par défaut : logs (à partir du répertoire courant).
     */
    public String obtientLogPath() {
        return logPath;
    }

    /**
     * Obtient le numéro de version de JDONREF.
     */
    public String getVersion() {
        return version;
    }

    private int getInt(Element root, String chaine, int defaut) {
        Element e = root.getChild(chaine);

        if (e == null) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " n'a pas été spécifiée. La valeur par défaut " + defaut + " a été choisie à la place.");
            return defaut;
        }

        int value = -1;
        boolean error = false;

        try {
            value = Integer.parseInt(e.getValue());
        } catch (NumberFormatException nfe) {
            error = true;
        }

        if (value < 0) {
            error = true;
        }
        if (error) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " est incorrecte ('" + e.getValue() + "' n'est pas un entier positif). La valeur par défaut " + defaut + " a été choisie à la place.");
            return defaut;
        }

        return value;
    }

    /**
     * Parse d'un booleen dans xml en cours -> true si la valeurs equalsIgnoreCase "true", false sinon
     * @param root
     * @param chaine
     * @param defaut
     * @return
     */
    private boolean getBoolean(Element root, String chaine, boolean defaut) {
        Element e = root.getChild(chaine);

        if (e == null) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " n'a pas été spécifiée. La valeur par défaut " + defaut + " a été choisie à la place.");
            return defaut;
        }
        boolean value = Boolean.parseBoolean(e.getValue());

        return value;
    }

    private double getDouble(Element root, String chaine, double defaut) {
        Element e = root.getChild(chaine);

        if (e == null) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " n'a pas été spécifiée. La valeur par defaut " + defaut + " a été choisie à la place.");
            return defaut;
        }

        double value = -1;
        boolean error = false;

        try {
            value = Double.parseDouble(e.getValue());
        } catch (NumberFormatException nfe) {
            error = true;
        }

        if (value < 0) {
            error = true;
        }
        if (error) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " est incorrecte ('" + e.getValue() + "' n'est pas un entier positif). La valeur par défaut " + defaut + " a été choisie à la place.");
            return defaut;
        }

        return value;
    }

    private String getString(Element root, String chaine, String defaut) {
        Element e = root.getChild(chaine);

        if (e == null) {
            if (defaut != null) {
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " n'a pas été spécifiée. La valeur par défaut " + defaut + " a été choisie à la place.");
            } else {
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " n'a pas été spécifiée.");
            }
            return defaut;
        }

        String value = null;
        boolean error = false;

        value = e.getValue();

        if (value == null || value.length() == 0) {
            error = true;
        }
        if (error) {
            if (defaut != null) {
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " est incorrecte. La valeur par defaut " + defaut + " a été choisie à la place.");
            } else {
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " est incorrecte.");
            }
            return defaut;
        }

        return value;
    }

    private String[] getStringArray(Element root, String chaine, String[] defaut) {
        Element e = root.getChild(chaine);

        if (e == null) {
            if (defaut != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("La valeur de l'attribut ");
                sb.append(chaine);
                sb.append(" n'a pas été spécifiée. La valeur par defaut [");
                for (int i = 0; i < defaut.length; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(defaut[i]);
                }
                sb.append("] a été choisie à la place.");
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, sb.toString());
            } else {
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " n'a pas été spécifiée.");
            }
            return defaut;
        }

        String value = null;
        boolean error = false;

        value = e.getValue();

        if (value == null || value.length() == 0) {
            error = true;
        }
        String[] res = null;

        if (!error) {
            int count = 0;
            String[] valuetab = value.split(",");
            for (int i = 0; i < valuetab.length; i++) {
                valuetab[i] = valuetab[i].trim();
                if (valuetab[i].length() > 0) {
                    count++;
                }
            }
            if (count == 0) {
                error = true;
            }
            if (!error) {
                res = new String[count];
                for (int i = 0,  j = 0; i < valuetab.length; i++) {
                    if (valuetab[i].length() > 0) {
                        res[j++] = valuetab[i];
                    }
                }
            }
        }

        if (error) {
            if (defaut != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("La valeur de l'attribut ");
                sb.append(chaine);
                sb.append(" est incorrecte. La valeur par defaut [");
                for (int i = 0; i < defaut.length; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(defaut[i]);
                }
                sb.append("] a été choisie à la place.");
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, sb.toString());
            } else {
                Logger.getLogger("JDONREFParams").log(Level.SEVERE, "La valeur de l'attribut " + chaine + " est incorrecte.");
            }
            return defaut;
        } else {
            return res;
        }
    }

    /**
     * Recherche le dossier qui contient les fichiers de configuration de JDONREFv2.
     * 
     * Les répertoires pour lesquels l'accès n'est pas permis sont ignorés.
     * 
     * @param baseDir
     * @return Tous les dossiers trouvés sont retournés.
     */
    protected ArrayList<File> searchPath(File baseDir, JDONREFConfigFileNameFilter filter, FileFilter dirfilter) {
        ArrayList<File> res = new ArrayList<File>();

        // Vérifie si ce répertoire contient les fichiers de configuration
        try {
            File[] conffiles = baseDir.listFiles(filter);
            if (conffiles != null && conffiles.length == 1) {
                res.add(baseDir);
            }
        } catch (SecurityException se) {
        // ignoré.
        }

        // Cherche les enfants du répertoire
        try {
            File[] dirs = baseDir.listFiles(dirfilter);
            if (dirs == null) {
                return res;
            }
            for (int i = 0; i < dirs.length; i++) {
                // Vérifie chaque répertoire
                File current = dirs[i];
                ArrayList<File> files = searchPath(current, filter, dirfilter);
                if (files != null) {
                    res.addAll(files);
                }
            }
        } catch (SecurityException se) {
        // ignoré.
        }

        return res;
    }

    /**
     * Vaine tentative de recherche du chemin du dossier qui contient les fichiers de configuration.
     * A partir de la racine bien entendu.
     * 
     * Les fichiers sont recherchés au moyen du filtre JDONREFConfigFileNameFilter
     * Les répertoires pour lesquels l'accès n'est pas permis sont ignorés.
     * 
     * @return Si plusieurs dossiers sont trouvés, null est retourné
     */
    public String searchPath() {
        JDONREFConfigFileNameFilter filter = new JDONREFConfigFileNameFilter();
        filter.setVersion(this.version);
        JDONREFDirectoryFilter dirfilter = new JDONREFDirectoryFilter();
        ArrayList<File> res = new ArrayList<File>();

        try {
            File[] roots = File.listRoots();
            if (roots == null) {
                return null;
            }


            for (int i = 0; i < roots.length; i++) {
                ArrayList<File> files = searchPath(roots[i], filter, dirfilter);
                if (files != null) {
                    res.addAll(files);
                    if (res.size() >= 2) {
                        return null;
                    }
                }
            }
        } catch (SecurityException se) {
        // ignoré
        }

        if (res.size() == 1) {
            return res.get(0).getPath();
        }
        return null;
    }

    public void load(String path, String file) throws JDOMException, IOException, JDONREFException {
        //configPath = path;
        load(path + file);
    }

    /**
     * Charge les paramètres à partir d'un fichier de configuration.
     * @param file
     */
    public void load(String file) throws JDOMException, IOException, JDONREFException {
        setConfigPath(file);
        
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(file);

        Element root = d.getRootElement();

        if (root.getName().compareTo("params") != 0) {
            Logger.getLogger("JDONREFParams").log(Level.SEVERE, "Le fichier " + file + " est mal structuré (balise " + root.getName() + ").");
            throw (new JDONREFException("Le fichier " + file + " est mal structuré."));
        }

        logPath = getString(root, "logpath", logPath);
        version = getString(root, "version", version);
        notePourCodePostal = getInt(root, "notepourcodepostal", notePourCodePostal);
        malusDeTypeDeVoie = getInt(root, "malusdetypedevoie", malusDeTypeDeVoie);
        malusPasDeMotDirecteur = getInt(root, "maluspasdemotdirecteur", malusPasDeMotDirecteur);
        malusPasDeMot = getInt(root, "maluspasdemot", malusPasDeMot);
        nombreDePaysParDefaut = getInt(root, "nombredepayspardefaut", nombreDePaysParDefaut);
        nombreDeCommuneParDefaut = getInt(root, "nombredecommunepardefaut", nombreDeCommuneParDefaut);
        nombreDeVoieParDefaut = getInt(root, "nombredevoiepardefaut", nombreDeVoieParDefaut);
        notePourLibelle = getInt(root, "notepourlibelle", notePourLibelle);
        notePourMotDeterminant = getInt(root, "notepourmotdeterminant", notePourMotDeterminant);
        notePourCommune = getInt(root, "notepourcommune", notePourCommune);
        notePourCommuneValide = getInt(root, "notepourcommunevalide", notePourCommuneValide);
        notePourCommuneSelectionnee = getInt(root, "notepourcommuneselectionnee", notePourCommuneSelectionnee);
        notePourCodePostal = getInt(root, "notepourcodepostal", notePourCodePostal);
        notePourTypeDeVoie = getInt(root, "notepourtypedevoie", notePourTypeDeVoie);
        notePourNumero = getInt(root, "notepournumero", notePourNumero);
        notePourArrondissement = getInt(root, "notepourarrondissement", notePourArrondissement);
        pourcentageDeCorrespondanceDeTypeDeVoie = getInt(root, "pourcentagedecorrespondancedetypedevoie", pourcentageDeCorrespondanceDeTypeDeVoie);
        pourcentageDeCorrespondanceDeMotDeterminant = getInt(root, "pourcentagedecorrespondancedemotdeterminant", pourcentageDeCorrespondanceDeMotDeterminant);
        pourcentageDeCorrespondanceDeLibelle = getInt(root, "pourcentagedecorrespondancedelibelle", pourcentageDeCorrespondanceDeLibelle);
        pourcentageDeCorrespondanceDePays = getInt(root, "pourcentagedecorrespondancedepays", pourcentageDeCorrespondanceDePays);
        pourcentageDeCorrespondanceDeCommune = getInt(root, "pourcentagedecorrespondancedecommune", pourcentageDeCorrespondanceDeCommune);
        pourcentageDeCorrespondanceDeCodePostal = getInt(root, "pourcentagedecorrespondancedecodepostal", pourcentageDeCorrespondanceDeCodePostal);
        pourcentageDeCorrespondanceDeMot = getInt(root, "pourcentagedecorrespondancedemot", pourcentageDeCorrespondanceDeMot);
        seuilDeCorrespondanceDAdresse = getInt(root, "seuildecorrespondancedadresse", seuilDeCorrespondanceDAdresse);
        seuilDeDistanceDeVoies = getDouble(root, "seuildedistancedevoies", seuilDeDistanceDeVoies);
        seuilDeTronconsProches = getInt(root, "seuildetronconsproches", seuilDeTronconsProches);
        seuilDeCorrespondanceDeLibelle = getInt(root, "seuildecorrespondancedelibelle", seuilDeCorrespondanceDeLibelle);
        seuilDeCorrespondanceDeTypeDeVoie = getInt(root, "seuildecorrespondancedetypedevoie", seuilDeCorrespondanceDeTypeDeVoie);
        seuilDeCorrespondanceDeVoie = getInt(root, "seuildecorrespondancedevoie", seuilDeCorrespondanceDeVoie);
        seuilDeNombreDeMotsCorrespondant = getInt(root, "seuildenombredemotscorrespondant", seuilDeNombreDeMotsCorrespondant);
        seuilDeCorrespondanceDeCommune = getInt(root, "seuildecorrespondancedecommune", seuilDeCorrespondanceDeCommune);
        seuilDeCorrespondanceDeCodePostal = getInt(root, "seuildecorrespondancedecodepostal", seuilDeCorrespondanceDeCodePostal);
        tailleDesCles = getInt(root, "tailledescles", tailleDesCles);
        tailleMinimaleDAbbreviation = getInt(root, "tailleminimaledabbreviation", tailleMinimaleDAbbreviation);
        codeDepartementParDefaut = getStringArray(root, "codedepartementpardefaut", codeDepartementParDefaut);
        // WA 09/2011 Ajout d'un parametre stipulant si on doit utiliser la gestion des departements ou non.
        utilisationDeLaGestionDesDepartements = getBoolean(root, "gestiondepartements", false);
        // WA 01/2012 Pays
        pourcentageDeCorrespondanceDePays = getInt(root, "pourcentagedecorrespondancedepays", pourcentageDeCorrespondanceDePays);
        utilisationDeLaGestionDesPays = getBoolean(root, "gestionpays", false);
        paysParDefaut = getString(root, "payspardefaut", paysParDefaut);
        projectionPayspardefaut = getString(root, "projectionpayspardefaut", projectionPayspardefaut);
        // HM Lambert-93
        projectionPardefaut = getString(root, "projectionpardefaut", projectionPardefaut);
        // HM POIZON
        notePourCle = getInt(root, "notepourcle", notePourCle);
        notePourPoizon = getInt(root, "notepourpoizon", notePourPoizon);
        notePourLigne2 = getInt(root, "notepourligne2", notePourLigne2);
        notePourLigne3 = getInt(root, "notepourligne3", notePourLigne3);
        notePourLigne4 = getInt(root, "notepourligne4", notePourLigne4);
        notePourLigne5 = getInt(root, "notepourligne5", notePourLigne5);
        notePourLigne6 = getInt(root, "notepourligne6", notePourLigne6);
        notePourLigne7 = getInt(root, "notepourligne7", notePourLigne7);

        pourcentagePourCle = getInt(root, "pourcentagepourcle", pourcentagePourCle);
        pourcentagePourPoizon = getInt(root, "pourcentagepourpoizon", pourcentagePourPoizon);
        pourcentagePourLigne2 = getInt(root, "pourcentagepourligne2", pourcentagePourLigne2);
        pourcentagePourLigne3 = getInt(root, "pourcentagepourligne3", pourcentagePourLigne3);
        pourcentagePourLigne4 = getInt(root, "pourcentagepourligne4", pourcentagePourLigne4);
        pourcentagePourLigne5 = getInt(root, "pourcentagepourligne5", pourcentagePourLigne5);
        pourcentagePourLigne6 = getInt(root, "pourcentagepourligne6", pourcentagePourLigne6);
        pourcentagePourLigne7 = getInt(root, "pourcentagepourligne7", pourcentagePourLigne7);

        tailleAbbreviationMminimalePoizon = getInt(root, "tailleabbreviationmminimalepoizon", tailleAbbreviationMminimalePoizon);
        
        routerClassName = getString(root, "routerclassname", routerClassName);
        
        projectionSpheroidPardefaut = getInt(root, "projectionspheroidpardefaut", projectionSpheroidPardefaut);
        spheroidPardefaut = getString(root, "spheroidpardefaut", spheroidPardefaut);
    }

    public String obtientRouterClassName() {
        return routerClassName;
    }

    public void definitRouterClassName(String routerClassName) {
        this.routerClassName = routerClassName;
    }

    /**
     * Obtient la part de note attribuée à la correspondance du code postal
     */
    public int obtientNotePourCodePostal() {
        return notePourCodePostal;
    }

    /**
     * Définit la note à partir de laquelle une commune est déterminée.
     * @param notePourCodePostal
     */
    public void definitNotePourCodePostal(int notePourCodePostal) {
        this.notePourCodePostal = notePourCodePostal;
    }

    /**
     * Obtient la note à partir de laquelle une commune est déterminée.
     */
    public int obtientNotePourCommuneValide() {
        return notePourCommuneValide;
    }

    /**
     * Définit la part de note attribuée à la correspondance de la commune
     */
    public void definitNotePourCommuneValide(int notePourCommuneValide) {
        this.notePourCommuneValide = notePourCommuneValide;
    }

    /**
     * Obtient la note à partir de laquelle une commune est déterminée.
     */
    public int obtientNotePourCommuneSelectionnee() {
        return notePourCommuneSelectionnee;
    }

    /**
     * Définit la part de note attribuée à la correspondance de la commune
     */
    public void definitNotePourCommuneSelectionnee(int notePourCommuneSelectionnee) {
        this.notePourCommuneSelectionnee = notePourCommuneSelectionnee;
    }

    /**
     * Obtient la part de note attribuée à la correspondance de la commune
     */
    public int obtientNotePourCommune() {
        return notePourCommune;
    }

    /**
     * Définit la part de note attribuée à la correspondance de la commune
     */
    public void definitNotePourCommune(int notePourCommune) {
        this.notePourCommune = notePourCommune;
    }

    /**
     * Obtient la part de note attribuée à la correspondance du libelle
     */
    public int obtientNotePourLibelle() {
        return notePourLibelle;
    }

    /**
     * Définit la part de note attribuée à la correspondance du libelle
     */
    public void definitNotePourLibelle(int notePourLibelle) {
        this.notePourLibelle = notePourLibelle;
    }

    /**
     * Obtient la part de note attribuée à la correspondance du déterminant
     */
    public int obtientNotePourMotDeterminant() {
        return notePourMotDeterminant;
    }

    /**
     * Définit la part de note attribuée à la correspondance du déterminant
     */
    public void definitNotePourMotDeterminant(int notePourMotDeterminant) {
        this.notePourMotDeterminant = notePourMotDeterminant;
    }

    /**
     * Obtient la part de note attribuée à la correspondance du type de voie
     */
    public int obtientNotePourTypeDeVoie() {
        return notePourTypeDeVoie;
    }

    /**
     * Définit la part de note attribuée à la correspondance du type de voie
     */
    public void definitNotePourTypeDeVoie(int notePourTypeDeVoie) {
        this.notePourTypeDeVoie = notePourTypeDeVoie;
    }

    /**
     * Obtient la part de note attribuée à la correspondance du numéro d'adresse
     */
    public int obtientNotePourNumero() {
        return notePourNumero;
    }

    /**
     * Définit la note à partir de laquelle une correspondance de numéro d'adresse est attribuée.
     */
    public void definitNotePourNumero(int notePourNumero) {
        this.notePourNumero = notePourNumero;
    }

    /**
     * Obtient la part de note attribuée à la correspondance du numéro d'adresse
     */
    public int obtientNotePourArrondissement() {
        return notePourArrondissement;
    }

    /**
     * Définit la note à partir de laquelle une correspondance de numéro d'adresse est attribuée.
     */
    public void definitNotePourArrondissement(int notePourArrondissement) {
        this.notePourArrondissement = notePourArrondissement;
    }

    /**
     * Obtient le pourcentage de correspondance d'une voie nécessaire au minimum.
     */
    public int obtientPourcentageDeCorrespondanceDeMot() {
        return pourcentageDeCorrespondanceDeMot;
    }

    /**
     * Définit le pourcentage de correspondance d'une voie nécessaire au minimum.
     */
    public void definitPourcentageDeCorrespondanceDeMot(int pourcentageDeCorrespondanceDeMot) {
        this.pourcentageDeCorrespondanceDeMot = pourcentageDeCorrespondanceDeMot;
    }

    /**
     * Obtient le pourcentage de correspondance d'un type de voie nécessaire au minimum.
     */
    public int obtientPourcentageDeCorrespondanceDeTypeDeVoie() {
        return pourcentageDeCorrespondanceDeTypeDeVoie;
    }

    /**
     * Définit le pourcentage de correspondance d'un mot déterminant nécessaire au minimum.
     */
    public void definitPourcentageDeCorrespondanceDeTypeDeVoie(int pourcentage) {
        pourcentageDeCorrespondanceDeTypeDeVoie = pourcentage;
    }

    /**
     * Obtient le pourcentage de correspondance d'un mot déterminant nécessaire au minimum.
     */
    public int obtientPourcentageDeCorrespondanceDeMotDeterminant() {
        return pourcentageDeCorrespondanceDeMotDeterminant;
    }

    /**
     * Définit le pourcentage de correspondance d'un mot déterminant nécessaire au minimum.
     */
    public void definitPourcentageDeCorrespondanceDeMotDeterminant(int pourcentage) {
        pourcentageDeCorrespondanceDeMotDeterminant = pourcentage;
    }

    /**
     * Obtient le pourcentage de correspondance de libelle hors mot déterminant nécessaire au minimum.
     */
    public int obtientPourcentageDeCorrespondanceDeLibelle() {
        return pourcentageDeCorrespondanceDeLibelle;
    }

    /**
     * Définit le pourcentage de correspondance de libelle hors mot déterminant nécessaire au minimum.
     */
    public void definitPourcentageDeCorrespondanceDeLibelle(int pourcentageDeCorrespondanceDeLibelle) {
        this.pourcentageDeCorrespondanceDeLibelle = pourcentageDeCorrespondanceDeLibelle;
    }

    /**
     * Obtient le pourcentage de correspondance de commune nécessaire au minimum.
     */
    public int obtientPourcentageDeCorrespondanceDeCommune() {
        return pourcentageDeCorrespondanceDeCommune;
    }

    /**
     * Définit le pourcentage de correspondance de commune nécessaire au minimum.
     * @param pourcentageDeCorrespondanceDeCommune
     */
    public void definitPourcentageDeCorrespondanceDeCommune(int pourcentageDeCorrespondanceDeCommune) {
        this.pourcentageDeCorrespondanceDeCommune = pourcentageDeCorrespondanceDeCommune;
    }

    /**
     * Obtient le pourcentage de correspondance de code postal nécessaire au minimum.
     */
    public int obtientPourcentageDeCorrespondanceDeCodePostal() {
        return pourcentageDeCorrespondanceDeCodePostal;
    }

    /**
     * Définit le pourcentage de correspondance de code postal nécessaire au minimum.
     * @param pourcentageDeCorrespondanceDeCodePostal
     */
    public void definitPourcentageDeCorrespondanceDeCodePostal(int pourcentageDeCorrespondanceDeCodePostal) {
        this.pourcentageDeCorrespondanceDeCodePostal = pourcentageDeCorrespondanceDeCodePostal;
    }

    /**
     * Obtient le malus appliqué lorsqu'un mot différent du mot directeur n'est pas trouvé dans une proposition.
     */
    public int obtientMalusPasDeMot() {
        return malusPasDeMot;
    }

    /**
     * Définit le malus appliqué lorsqu'un mot différent du mot directeur n'est pas trouvé dans une proposition.
     * @param malusPasDeMot
     */
    public void definitMalusPasDeMot(int malusPasDeMot) {
        this.malusPasDeMot = malusPasDeMot;
    }

    /**
     * Obtient le malus appliqué lorsque le mot directeur n'est pas trouvé lors d'une comparaison de voies.
     */
    public int obtientMalusPasDeMotDirecteur() {
        return malusPasDeMotDirecteur;
    }

    /**
     * Définit le malus appliqué lorsque le mot directeur n'est pas trouvé lors d'une comparaison de voies.
     * @param malusPasDeMotDirecteur
     */
    public void definitMalusPasDeMotDirecteur(int malusPasDeMotDirecteur) {
        this.malusPasDeMotDirecteur = malusPasDeMotDirecteur;
    }

    /**
     * Obtient le malus donné lorsque le type de voie est différent.
     */
    public int obtientMalusDeTypeDeVoie() {
        return malusDeTypeDeVoie;
    }

    /**
     * Définit le malus donné lorsque le type de voie est différent.
     * @param malusDeTypeDeVoie
     */
    public void definitMalusDeTypeDeVoie(int malusDeTypeDeVoie) {
        this.malusDeTypeDeVoie = malusDeTypeDeVoie;
    }

    /**
     * Obtient la taille minimale des abbréviations.
     */
    public int obtientTailleMinimaleDAbbreviation() {
        return tailleMinimaleDAbbreviation;
    }

    /**
     * Définit la taille minimale des abbréviations.
     * @param tailleMinimaleDAbbreviation
     */
    public void definitTailleMinimaleDAbbreviation(int tailleMinimaleDAbbreviation) {
        this.tailleMinimaleDAbbreviation = tailleMinimaleDAbbreviation;
    }

    /**
     * Obtient le seuil au delà duquel les voies correspondent.
     */
    public int obtientSeuilDeCorrespondanceDAdresse() {
        return seuilDeCorrespondanceDAdresse;
    }

    /**
     * Définit le seuil au delà duquel les voies correspondent.
     * @param seuilDeCorrespondanceDAdresse
     */
    public void definitSeuilDeCorrespondanceDAdresse(int seuilDeCorrespondanceDAdresse) {
        this.seuilDeCorrespondanceDAdresse = seuilDeCorrespondanceDAdresse;
    }

    /**
     * Obtient le seuil en deça duquel les voies correspondent.
     */
    public int obtientSeuilDeCorrespondanceDeVoie() {
        return seuilDeCorrespondanceDeVoie;
    }

    /**
     * Définit le seuil en deça duquel les voies correspondent.
     * @param seuilDeCorrespondanceDeVoie
     */
    public void definitSeuilDeCorrespondanceDeVoie(int seuilDeCorrespondanceDeVoie) {
        this.seuilDeCorrespondanceDeVoie = seuilDeCorrespondanceDeVoie;
    }

    /**
     * Obtient le seuil de correspondance de commune.
     */
    public int obtientSeuilDeCorrespondanceDeCodePostal() {
        return seuilDeCorrespondanceDeCodePostal;
    }

    /**
     * Définit le seuil de correspondance de commune.
     */
    public void definitSeuilDeCorrespondanceDeCodePostal(int seuilDeCorrespondanceDeCodePostal) {
        this.seuilDeCorrespondanceDeCodePostal = seuilDeCorrespondanceDeCodePostal;
    }

    /**
     * Obtient le nombre de voies retournées au maximum
     */
    public int obtientNombreDeVoieParDefaut() {
        return nombreDeVoieParDefaut;
    }

    /**
     * Définit le nombre de voies retournées au maximum
     */
    public void definitNombreDeVoieParDefaut(int nombreDeVoieParDefaut) {
        this.nombreDeVoieParDefaut = nombreDeVoieParDefaut;
    }

    /**
     * Obtient le nombre de pays par défaut.
     */
    public int obtientNombreDePaysParDefaut() {
        return nombreDePaysParDefaut;
    }

    /**
     * Définit le nombre de pays par défaut.
     */
    public void definitNombreDePaysParDefaut(int nb) {
        this.nombreDePaysParDefaut = nb;
    }

    /**
     * Obtient le nombre de commune par défaut.
     */
    public int obtientNombreDeCommuneParDefaut() {
        return nombreDeCommuneParDefaut;
    }

    /**
     * Définit le nombre de commune par défaut.
     */
    public void definitNombreDeCommuneParDefaut(int nb) {
        this.nombreDeCommuneParDefaut = nb;
    }

    /**
     * Obtient le seuil de correspondance de pays.
     */
    public int obtientSeuilDeCorrespondanceDePays() {
        return seuilDeCorrespondanceDePays;
    }

    /**
     * Définit le seuil de correspondance de pays.
     */
    public void definitSeuilDeCorrespondanceDePays(int seuilDeCorrespondance) {
        this.seuilDeCorrespondanceDePays = seuilDeCorrespondance;
    }

    /**
     * Obtient le seuil de correspondance de commune.
     */
    public int obtientSeuilDeCorrespondanceDeCommune() {
        return seuilDeCorrespondanceDeCommune;
    }

    /**
     * Définit le seuil de correspondance de commune.
     */
    public void definitSeuilDeCorrespondanceDeCommune(int seuilDeCorrespondanceDeCommune) {
        this.seuilDeCorrespondanceDeCommune = seuilDeCorrespondanceDeCommune;
    }

    /**
     * Obtient le nombre de mots requis pour qu'une phrase corresponde
     */
    public int obtientSeuilDeNombreDeMotsCorrespondants() {
        return seuilDeNombreDeMotsCorrespondant;
    }

    /**
     * Définit le nombre de mots requis pour qu'une phrase corresponde
     */
    public void definitSeuilDeNombreDeMotsCorrespondants(int nombre) {
        seuilDeNombreDeMotsCorrespondant = nombre;
    }

    /** Creates a new instance of JDONREFParams */
    public JDONREFParams() {
    }

    /**
     * Initialise les types gérées par le driver de BD.
     */
    public void initialiseTypes(DatabaseMetaData metadata) throws SQLException {
        ResultSet rs = metadata.getTypeInfo();

        while (rs.next()) {
            String typeBD = rs.getString("TYPE_NAME");
            int typeSQL = rs.getInt("DATA_TYPE");

            correspondanceTypes.put(typeBD, new Integer(typeSQL));
        }
    }

    /**
     * Obtient le type SQL correspondant au type de la BD spécifié.<br>
     * Utilise les métadonnées obtenues par la méthode initialiseTypes.<br>
     * Si le type n'est pas trouvé par cette méthode, les correspondances suivantes sont 
     * choisies:
     * <ul><li>bit BIT</li>
     *     <li>bit varying BIT</li>
     *     <li>bigint BIGINT</li>
     *     <li>boolean BOOLEAN</li>
     *     <li>character CHAR</li>
     *     <li>character varying VARCHAR</li>
     *     <li>date DATE</li>
     *     <li>double precision DOUBLE</li>
     *     <li>integer INTEGER</li>
     *     <li>numeric NUMERIC</li>
     *     <li>real REAL</li>
     *     <li>smallint SMALLINT</li>
     *     <li>text VARCHAR</li>
     *     <li>time with time zone TIME</li>
     *     <li>time without time zone TIME</li>
     *     <li>timestamp with time zone TIMESTAMP</li>
     *     <li>timestamp with time zone TIMESTAMP</li>
     *     <li>[] ARRAY</li>
     * </ul>
     */
    public int obtientType(String typeBD) {
        typeBD = typeBD.toLowerCase();

        Integer type = correspondanceTypes.get(typeBD);

        if (type != null) {
            int i = type.intValue();
            return i;
        } else {
            if ("bit".compareTo(typeBD) == 0) {
                return Types.BIT;
            }
            if ("bit varying".compareTo(typeBD) == 0) {
                return Types.BIT;
            }
            if ("bigint".compareTo(typeBD) == 0) {
                return Types.BIGINT;
            }
            if ("boolean".compareTo(typeBD) == 0) {
                return Types.BOOLEAN;
            }
            if ("character".compareTo(typeBD) == 0) {
                return Types.CHAR;
            }
            if ("character varying".compareTo(typeBD) == 0) {
                return Types.VARCHAR;
            }
            if ("date".compareTo(typeBD) == 0) {
                return Types.DATE;
            }
            if ("integer".compareTo(typeBD) == 0) {
                return Types.INTEGER;
            }
            if ("numeric".compareTo(typeBD) == 0) {
                return Types.NUMERIC;
            }
            if ("real".compareTo(typeBD) == 0) {
                return Types.REAL;
            }
            if ("smallint".compareTo(typeBD) == 0) {
                return Types.SMALLINT;
            }
            if ("text".compareTo(typeBD) == 0) {
                return Types.VARCHAR;
            }
            if ("time with time zone".compareTo(typeBD) == 0) {
                return Types.TIME;
            }
            if ("time without time zone".compareTo(typeBD) == 0) {
                return Types.TIME;
            }
            if ("timestamp without time zone".compareTo(typeBD) == 0) {
                return Types.TIMESTAMP;
            }
            if ("timestamp without time zone".compareTo(typeBD) == 0) {
                return Types.TIMESTAMP;
            }
            if ("double precision".compareTo(typeBD) == 0) {
                return Types.DOUBLE;
            }
            if (typeBD.contains("[]")) {
                return Types.ARRAY;
            }
            return Types.OTHER;
        }
    }

    /**
     * Obtient le nombre de caractères composant les clés des voies et des troncons.
     */
    public int obtientTailleDesCles() {
        return tailleDesCles;
    }

    /**
     * Définit le nombre de caractères composant les clés des voies et des tronçons.
     * @param tailleDesCles
     */
    public void definitTailleDesCles(int tailleDesCles) {
        this.tailleDesCles = tailleDesCles;
    }

    /**
     * Obtient le seuil d'erreurs en deça duquel les types de voies sont considérés
     * comme équivalents.
     */
    public int obtientSeuilDeCorrespondanceDeTypeDeVoie() {
        return seuilDeCorrespondanceDeTypeDeVoie;
    }

    /**
     * Définit le seuil d'erreurs en deça duquel les types de voies sont considérés
     * comme équivalents.
     */
    public void definitSeuilDeCorrespondanceDeTypeDeVoie(int seuilDeCorrespondanceDeTypeDeVoie) {
        this.seuilDeCorrespondanceDeTypeDeVoie = seuilDeCorrespondanceDeTypeDeVoie;
    }

    /**
     * Obtient le seuil d'erreurs en deça duquel les libell�s sont considérés comme
     * équivalents.
     */
    public int obtientSeuilDeCorrespondanceDeLibelle() {
        return seuilDeCorrespondanceDeLibelle;
    }

    /**
     * Définit le seuil d'erreurs en deça duquel les libell�s sont considérés comme
     * équivalents.
     * @param seuilDeCorrespondanceDeLibelle
     */
    public void definitSeuilDeCorrespondanceDeLibelle(int seuilDeCorrespondanceDeLibelle) {
        this.seuilDeCorrespondanceDeLibelle = seuilDeCorrespondanceDeLibelle;
    }

    /**
     * Définit le seuil de distance en deça duquel les voies sont
     * considérée être les m�mes.
     */
    public void definitSeuilDeDistanceDeVoies(double seuilDeDistanceDeVoies) {
        this.seuilDeDistanceDeVoies = seuilDeDistanceDeVoies;
    }

    /**
     * Définit le seuil de distance en deça duquel les voies sont
     * considérée être les m�mes.
     */
    public double obtientSeuilDeDistanceDeVoies() {
        return seuilDeDistanceDeVoies;
    }

    /**
     * Obtient le nombre de troncons qui doivent être proches
     * pour qu'une voie soit proche.<br>
     * N.B.: si la voie contient moins de tronçon que ce nombre, la valeur 1 doit être e.
     */
    public int obtientSeuilDeTronconsProches() {
        return seuilDeTronconsProches;
    }

    /**
     * Définit le nombre de troncons qui doivent être proches
     * pour qu'une voie soit proche.
     */
    public void definitSeuilDeTronconsProches(int seuilDeTronconsProches) {
        this.seuilDeTronconsProches = seuilDeTronconsProches;
    }

    /**
     * Obtient le code de département utilisé par défaut.<br>
     * Il est notamment utilisé pour la normalisation et la restructuration.
     */
    public String[] obtientCodeDepartementParDefaut() {
        return codeDepartementParDefaut;
    }

    /**
     * Définit le code de département utilisé par défaut.<br>
     * Il est notamment utilisé pour la normalisation et la restructuration.
     * @param codeDepartementParDefaut
     */
    public void definitCodeDepartementParDefaut(String[] codeDepartementParDefaut) {
        this.codeDepartementParDefaut = codeDepartementParDefaut;
    }

    /**
     * @return the utilisationDeLaGestionDesDepartements
     */
    public boolean isUtilisationDeLaGestionDesDepartements() {
        return utilisationDeLaGestionDesDepartements;
    }

    /**
     * @return the pourcentageDeCorrespondanceDePays
     */
    public int obtientPourcentageDeCorrespondanceDePays() {
        return pourcentageDeCorrespondanceDePays;
    }

    /**
     * @param pourcentageDeCorrespondanceDePays the pourcentageDeCorrespondanceDePays to set
     */
    public void definitPourcentageDeCorrespondanceDePays(int pourcentageDeCorrespondanceDePays) {
        this.pourcentageDeCorrespondanceDePays = pourcentageDeCorrespondanceDePays;
    }

    /**
     *
     * @param gesPays : booleen : indique si on doit chercher les pays
     */
    public void definitUtilisationDeLaGestionDesPays(boolean gesPays) {
        utilisationDeLaGestionDesPays = gesPays;
    }

    public boolean isUtilisationDeLaGestionDesPays() {
        return utilisationDeLaGestionDesPays;
    }

    public String obtientPaysParDefaut() {
        return paysParDefaut;
    }

    public void definitPaysParDefaut(String pays) {
        paysParDefaut = pays;
    }

    /**
     * La note a partie de laquelle un pays est
     * @return
     */
    public int obtientNotePourPaysValide() {
        return notePourPaysValide;
    }

    public void definitNotePourPaysValide(int note) {
        notePourPaysValide = note;
    }

    public String obtientProjectionPaysParDefaut() {
        return projectionPayspardefaut;
    }

    public void definitProjectionPaysParDefaut(String proj) {
        projectionPayspardefaut = proj;
    }

    public String obtientProjectionPardefaut() {
        return projectionPardefaut;
    }

    public void definitProjectionPardefaut(String projectionPardefaut) {
        this.projectionPardefaut = projectionPardefaut;
    }

    public Hashtable<String, Integer> obtientCorrespondanceTypes() {
        return correspondanceTypes;
    }

    public void definitCorrespondanceTypes(Hashtable<String, Integer> correspondanceTypes) {
        this.correspondanceTypes = correspondanceTypes;
    }

    public int obtientNotePourCle() {
        return notePourCle;
    }

    public void definitNotePourCle(int notePourCle) {
        this.notePourCle = notePourCle;
    }

    public int obtientNotePourLigne2() {
        return notePourLigne2;
    }

    public void definitNotePourLigne2(int notePourLigne2) {
        this.notePourLigne2 = notePourLigne2;
    }

    public int obtientNotePourLigne3() {
        return notePourLigne3;
    }

    public void definitNotePourLigne3(int notePourLigne3) {
        this.notePourLigne3 = notePourLigne3;
    }

    public int obtientNotePourLigne4() {
        return notePourLigne4;
    }

    public void definitNotePourLigne4(int notePourLigne4) {
        this.notePourLigne4 = notePourLigne4;
    }

    public int obtientNotePourLigne6() {
        return notePourLigne6;
    }

    public void definitNotePourLigne6(int notePourLigne6) {
        this.notePourLigne6 = notePourLigne6;
    }

    public int obtientNotePourLigne7() {
        return notePourLigne7;
    }

    public void definitNotePourLigne7(int notePourLigne7) {
        this.notePourLigne7 = notePourLigne7;
    }

    public int obtientNotePourPoizon() {
        return notePourPoizon;
    }

    public void definitNotePourPoizon(int notePourPoizon) {
        this.notePourPoizon = notePourPoizon;
    }

    public int obtientNotePourLigne5() {
        return notePourLigne5;
    }

    public void definitNotePourLigne5(int notePourLigne5) {
        this.notePourLigne5 = notePourLigne5;
    }

    public int obtientPourcentagePourCle() {
        return pourcentagePourCle;
    }

    public void definitPourcentagePourCle(int pourcentagePourCle) {
        this.pourcentagePourCle = pourcentagePourCle;
    }

    public int obtientPourcentagePourLigne2() {
        return pourcentagePourLigne2;
    }

    public void definitPourcentagePourLigne2(int pourcentagePourLigne2) {
        this.pourcentagePourLigne2 = pourcentagePourLigne2;
    }

    public int obtientPourcentagePourLigne3() {
        return pourcentagePourLigne3;
    }

    public void definitPourcentagePourLigne3(int pourcentagePourLigne3) {
        this.pourcentagePourLigne3 = pourcentagePourLigne3;
    }

    public int obtientPourcentagePourLigne4() {
        return pourcentagePourLigne4;
    }

    public void definitPourcentagePourLigne4(int pourcentagePourLigne4) {
        this.pourcentagePourLigne4 = pourcentagePourLigne4;
    }

    public int obtientPourcentagePourLigne5() {
        return pourcentagePourLigne5;
    }

    public void definitPourcentagePourLigne5(int pourcentagePourLigne5) {
        this.pourcentagePourLigne5 = pourcentagePourLigne5;
    }

    public int obtientPourcentagePourLigne6() {
        return pourcentagePourLigne6;
    }

    public void definitPourcentagePourLigne6(int pourcentagePourLigne6) {
        this.pourcentagePourLigne6 = pourcentagePourLigne6;
    }

    public int obtientPourcentagePourLigne7() {
        return pourcentagePourLigne7;
    }

    public void definitPourcentagePourLigne7(int pourcentagePourLigne7) {
        this.pourcentagePourLigne7 = pourcentagePourLigne7;
    }

    public int obtientPourcentagePourPoizon() {
        return pourcentagePourPoizon;
    }

    public void definitPourcentagePourPoizon(int pourcentagePourPoizon) {
        this.pourcentagePourPoizon = pourcentagePourPoizon;
    }

    public String obtientProjectionPayspardefaut() {
        return projectionPayspardefaut;
    }

    public void definitProjectionPayspardefaut(String projectionPayspardefaut) {
        this.projectionPayspardefaut = projectionPayspardefaut;
    }

    public int obtientSeuilDeNombreDeMotsCorrespondant() {
        return seuilDeNombreDeMotsCorrespondant;
    }

    public void definitSeuilDeNombreDeMotsCorrespondant(int seuilDeNombreDeMotsCorrespondant) {
        this.seuilDeNombreDeMotsCorrespondant = seuilDeNombreDeMotsCorrespondant;
    }

    public int obtientTailleAbbreviationMminimalePoizon() {
        return tailleAbbreviationMminimalePoizon;
    }

    public void definitTailleAbbreviationMminimalePoizon(int tailleAbbreviationMminimalePoizon) {
        this.tailleAbbreviationMminimalePoizon = tailleAbbreviationMminimalePoizon;
    }

    public int obtientProjectionSpheroidPardefaut() {
        return projectionSpheroidPardefaut;
    }

    public void definitProjectionSpheroidPardefaut(int projectionSpheroidPardefaut) {
        this.projectionSpheroidPardefaut = projectionSpheroidPardefaut;
    }

    public String obtientSpheroidPardefaut() {
        return spheroidPardefaut;
    }

    public void definitSpheroidPardefaut(String spheroidPardefaut) {
        this.spheroidPardefaut = spheroidPardefaut;
    }
    
    public static void main(String[] args){
        JDONREFParams params = new JDONREFParams();
        try{
        params.load("C:\\JDONREF_v3\\Dev\\Src\\JDONREFv3LIB\\params.xml");
        System.out.println(params.obtientSpheroidPardefaut());
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    
}
