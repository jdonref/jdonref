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
package ppol.jdonref.mots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import ppol.jdonref.Algos;
import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.referentiel.GestionCodesDepartements;
import ppol.jdonref.referentiel.GestionReferentiel;
import ppol.jdonref.utils.DynamicEnum;

/**
 * Interface privilégiée du paquetage, regroupe les méthodes utilisant le référentiel de mots : prénoms, titres, types de voies, et autres.<br>
 * Les méthodes chercheXXX permettent de chercher un terme ou dans un terme à partir de la liste des mots et abbréviations enregistrées.<br>
 * Les méthodes obtientXXX permettent de récupérer une information ou un terme directement, sans recherche.<br>
 * Les méthodes trouveXXX effectuent des recherches complexes dans un terme.<br>
 * @author Julien
 */
public class GestionMots {

    private ArrayList<Abbreviation> abbreviations = new ArrayList<Abbreviation>();
    private ArrayList<Mot> mots = new ArrayList<Mot>();
    private Arbre arbre_abbreviations = new Arbre();
    private Arbre arbre_mots = new Arbre();
    private GestionReferentiel referentiel = null;
    private JDONREFParams params = null;
    /**
     * Utilisé pour optimisation de obtientRue. Doit être remis à null lorsque la liste mots
     * est vidée.
     */
    private Mot rue = null;
    private Mot bis = null;
    private Mot ter = null;
    private Mot quater = null;
    private Mot quinquies = null;
    private Mot cedex = null;

    /**
     * Définit les paramètres de JDONREF utilisés par cette classe.
     * @param params
     */
    public void definitJDONREFParams(JDONREFParams params) {
        this.params = params;
    }

    /**
     * Obtient les paramètres de JDONREF utilisés par cette classe.
     * @param params
     */
    public JDONREFParams obtientJDONREFParams() {
        return params;
    }

    /**
     * Définit l'objet Gestion Referentiel Utilisé.
     */
    public void definitGestionReferentiel(GestionReferentiel referentiel) {
        this.referentiel = referentiel;
    }

    /**
     * Obtient la référence à l'objet Gestion Referentiel Utilisé.
     * @return
     */
    public GestionReferentiel obtientGestionReferentiel() {
        return referentiel;
    }

    /**
     * Obtient le mot qui représente la RUE.<br>
     * Cette méthode est utilisée pour optimisation de trouveTypeVoie.<br>
     */
    public Mot obtientCedex() {
        // Recherche la clé rue si elle n'est pas initialisée
        if (cedex == null) {
            cedex = chercheMot("CEDEX");
        }
        return cedex;
    }

    /**
     * Obtient le mot qui représente la RUE.<br>
     * Cette méthode est utilisée pour optimisation de trouveTypeVoie.<br>
     */
    public Mot obtientQuinquies() {
        // Recherche la clé rue si elle n'est pas initialisée
        if (cedex == null) {
            cedex = chercheMot("QUINQUIES");
        }
        return quinquies;
    }

    /**
     * Obtient le mot qui représente la RUE.<br>
     * Cette méthode est utilisée pour optimisation de trouveTypeVoie.<br>
     */
    public Mot obtientQuater() {
        // Recherche la clé rue si elle n'est pas initialisée
        if (cedex == null) {
            cedex = chercheMot("QUATER");
        }
        return quater;
    }

    /**
     * Obtient le mot qui représente la RUE.<br>
     * Cette méthode est utilisée pour optimisation de trouveTypeVoie.<br>
     */
    public Mot obtientTer() {
        // Recherche la clé rue si elle n'est pas initialisée
        if (cedex == null) {
            cedex = chercheMot("TER");
        }
        return ter;
    }

    /**
     * Obtient le mot qui représente la RUE.<br>
     * Cette méthode est utilisée pour optimisation de trouveTypeVoie.<br>
     */
    public Mot obtientBis() {
        // Recherche la clé rue si elle n'est pas initialisée
        if (cedex == null) {
            cedex = chercheMot("BIS");
        }
        return bis;
    }

    /**
     * Obtient le mot qui représente la RUE.<br>
     * Cette méthode est utilisée pour optimisation de trouveTypeVoie.<br>
     */
    public Mot obtientRue() {
        // Recherche la clé rue si elle n'est pas initialisée
        if (rue == null) {
            rue = chercheMot("RUE");
        }
        return rue;
    }

    /**
     * Obtient une abbréviation.
     */
    public Abbreviation obtientAbbreviation(int index) {
        return abbreviations.get(index);
    }

    /**
     * Obtient un mot.
     */
    public Mot obtientMot(int index) {
        return mots.get(index);
    }

    /**
     * Retourne les lignes de l'adresse privées d'articles.
     * @param adresse
     * @return
     */
    public String[] sansArticles(String[] adresse) {
        String[] res = new String[adresse.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Algos.sansarticles(adresse[i]);
        }
        return res;
    }

    /**
     * Cherche l'index du mot spécifié.
     * @return
     */
    private int chercheAbbreviationIndex(String mot, int min, int max) {
        if (min == max) {
            int i;
            String motref = abbreviations.get(min).obtientNom();
            for (i = 0; (i < mot.length()) && (i < motref.length()); i++) {
                char cref = motref.charAt(i);
                char c = mot.charAt(i);
                if (cref == c) {
                    continue;
                } else if (c < cref) {
                    return -(min + 1); // le mot peut être inséré au plus en min.

                } else {
                    return -(min + 2); // le mot doit être inséré au moins en min+1.

                }
            }
            if (i == mot.length() && i == motref.length()) {
                return min;
            }
            if (i < mot.length()) {
                return -(min + 2); // le mot de référence est plus court, donc au moins en min+1.

            }
            return -(min + 1); // le mot de référence est plus long, donc au plus en min.

        } else if (min + 1 == max) {
            int index1 = chercheAbbreviationIndex(mot, min, min);
            if (index1 >= 0) {
                return index1;
            }
            int index2 = chercheAbbreviationIndex(mot, max, max);
            if (index2 >= 0) {
                return index2;
            // Seuls trois cas peuvent se pr�senter.
            }
            if (-index1 - 1 == min) {
                return index1;
            }
            if (-index2 - 1 == max + 1) {
                return index2;
            }
            return -(max + 1);
        } else {
            int i;
            int index = (min + max) / 2;
            String motref = abbreviations.get(index).obtientNom();

            for (i = 0; (i < mot.length()) && (i < motref.length()); i++) {
                char cref = motref.charAt(i);
                char c = mot.charAt(i);
                if (cref == c) {
                    continue;
                } else if (c < cref) {
                    return chercheAbbreviationIndex(mot, min, index - 1);
                } else {
                    return chercheAbbreviationIndex(mot, index + 1, max);
                }
            }
            if (i == mot.length() && i == motref.length()) {
                return index;
            }
            if (i < mot.length()) {
                return chercheAbbreviationIndex(mot, index + 1, max); // le mot de référence est plus court, donc au moins en min+1.

            }
            return chercheAbbreviationIndex(mot, min, index - 1); // le mot de référence est plus long, donc au plus en min.

        }
    }

    /**
     * Cherche le mot qui débute la chaine.<br>
     * Seul les mots entiers sont cherchés (commencant et finissant par un espace ou un début ou une fin de chaine).
     * Si le mot trouvé n'est pas de la catégorie spécifiée, null est renvoyé.
     * @param chaine
     * @return null si aucun mot n'est trouvé.
     */
    public Mot chercheDebutMot(String chaine, int index, CategorieMot categorie) {
        Mot m = (Mot) arbre_mots.cherche(chaine, index, true);

        if (m == null) {
            return null;


        }
        if (m.estDeLaCategorie(categorie)) {
            return m;

        } else {
            return null;

        }
    }

    /**
     * Cherche si un mot est enregistré.
     * @return null si aucun mot n'est enregistré.
     */
    public Mot chercheMot(String mot) {
        Mot m = (Mot) arbre_mots.cherche(mot, 0, true);
        if (m == null) {
            return null;

        }
        if (m.obtientNom().length() != mot.length()) // Les mots trouvés peuvent commencer la chaine
        {
            return null;

        }
        return m;
    }

    /**
     * Cherche si un mot est enregistré.
     * @return null si aucun mot n'est enregistré.
     */
    public Mot chercheMotPourRoutage(String mot) {
        Mot m = (Mot) arbre_mots.cherche(mot, 0, true);
        if (m == null) {
            return null;

        }

        return m;
    }

    /**
     * Cherche si un mot qui a une abbréviation officielle est enregistré.
     * @return null si aucun mot n'est enregistré.
     */
    public Mot chercheMotOfficiel(String mot) {
        Mot m = chercheMot(mot);
        if (m == null) {
            return null;

        }
        if (m.estOfficiel()) {
            return m;

        } else {
            return null;

        }
    }

    /**
     * Cherche si un mot est enregistré.
     * @return null si aucun mot n'est enregistré.
     */
    public Mot chercheMot(String mot, CategorieMot categorie) {
        Mot m = chercheMot(mot);
        if (m == null) {
            return null;

        }
        if (m.estDeLaCategorie(categorie)) {
            return m;

        }
        return null;
    }

    /**
     * Cherche l'index du mot spécifié.<br>
     * Utilisé pour la gestion de la liste.
     * @return
     */
    private int chercheMotIndex(String mot, int min, int max) {
        if (min == max) {
            int i;
            String motref = mots.get(min).obtientNom();
            for (i = 0; (i < mot.length()) && (i < motref.length()); i++) {
                char cref = motref.charAt(i);
                char c = mot.charAt(i);
                if (cref == c) {
                    continue;
                } else if (c < cref) {
                    return -(min + 1); // le mot peut être inséré au plus en min.

                } else {
                    return -(min + 2); // le mot doit être inséré au moins en min+1.

                }
            }
            if (i == mot.length() && i == motref.length()) {
                return min;
            }
            if (i < mot.length()) {
                return -(min + 2); // le mot de référence est plus court, donc au moins en min+1.

            }
            return -(min + 1); // le mot de référence est plus long, donc au plus en min.

        } else if (min + 1 == max) {
            int index1 = chercheMotIndex(mot, min, min);
            if (index1 >= 0) {
                return index1;
            }
            int index2 = chercheMotIndex(mot, max, max);
            if (index2 >= 0) {
                return index2;
            // Seuls trois cas peuvent se pr�senter.
            }
            if (-index1 - 1 == min) {
                return index1;
            }
            if (-index2 - 1 == max + 1) {
                return index2;
            }
            return -(max + 1);
        } else {
            int i;
            int index = (min + max) / 2;
            String motref = mots.get(index).obtientNom();

            for (i = 0; (i < mot.length()) && (i < motref.length()); i++) {
                char cref = motref.charAt(i);
                char c = mot.charAt(i);
                if (cref == c) {
                    continue;
                } else if (c < cref) {
                    return chercheMotIndex(mot, min, index - 1);
                } else {
                    return chercheMotIndex(mot, index + 1, max);
                }
            }
            if (i == mot.length() && i == motref.length()) {
                return index;
            }
            if (i < mot.length()) {
                return chercheMotIndex(mot, index + 1, max); // le mot de référence est plus court, donc au moins en min+1.

            }
            return chercheMotIndex(mot, min, index - 1); // le mot de référence est plus long, donc au plus en min.

        }
    }

    /**
     * Obtient l'index du mot spécifié.<br>
     * Utilisé pour la gestion de la liste.
     * @param mot
     * @return Si le mot n'est pas trouvé, retourne -(index+1) de l'endroit où l'insérer.
     */
    private int chercheAbbreviationIndex(String abbreviation) {
        if (abbreviations.size() == 0) {
            return -1;
        }
        return chercheAbbreviationIndex(abbreviation, 0, abbreviations.size() - 1);
    }

    /**
     * Obtient l'index du mot spécifié.<br>
     * Utilisé pour la gestion de la liste.
     * @param mot
     * @return Si le mot n'est pas trouvé, retourne -(index+1) de l'endroit où l'insérer.
     */
    private int chercheMotIndex(String mot) {
        if (mots.size() == 0) {
            return -1;
        }
        return chercheMotIndex(mot, 0, mots.size() - 1);
    }

    /**
     * Ajoute un nouveau mot.<br>
     * Le mot est ajouté dans les deux structures gérées : la liste et l'arbre.
     * @param mot
     * @throws GestionMotsException Si le mot est déjà présent.
     */
    public boolean ajouteMot(Mot mot) throws GestionMotsException {
        // Ajoute à la liste.
        int index = chercheMotIndex(mot.obtientNom());
        if (index >= 0) {
            return false;
        }
        index = -index - 1;

        mots.add(index, mot);

        // Ajoute à l'arbre
        arbre_mots.ajoute(mot.obtientNom(), mot);

        return true;
    }

    /**
     * Renomme un mot.<br>
     * Le mot est retiré, puis ajouté de nouveau après avoir changé son nom.
     * @param mot
     * @param nouveaunom
     * @throw GestionMotsException le nouveaunom existe déjà.
     */
    public void renommeMot(Mot mot, String nouveaunom) throws GestionMotsException {
        retireMot(mot);
        mot.definitNom(nouveaunom);
        ajouteMot(mot);
    }

    /**
     * Retire le mot spécifié.<br>
     * Le mot est retiré des deux structures : la liste et l'arbre.
     * @param mot
     */
    public void retireMot(Mot mot) {
        mots.remove(mot);
        arbre_mots.supprime(mot.obtientNom());
    }

    /**
     * Ajoute une nouvelle abbreviation.<br>
     * L'abbréviation est ajoutée dans les deux structures : la liste et l'arbre.
     * @param abbreviation
     */
    public boolean ajouteAbbreviation(Abbreviation abbreviation) throws GestionMotsException {
        // Ajoute à la liste
        int index = chercheAbbreviationIndex(abbreviation.obtientNom());

        if (index >= 0) {
            return false;
        }
        index = -index - 1;

        abbreviations.add(index, abbreviation);

        // Ajoute à l'arbre
        arbre_abbreviations.ajoute(abbreviation.obtientNom(), abbreviation);

        return true;
    }

    /**
     * Retire l'abbréviation spécifiée.<br>
     * L'abbréviation est retirée des deux structures gérées : la liste et l'arbre.
     * @param mot
     */
    public void retireAbbreviation(Abbreviation abbreviation) {
        abbreviations.remove(abbreviation);
        arbre_abbreviations.supprime(abbreviation.obtientNom());
    }

    /**
     * Renomme une abbréviation.
     * @param abbreviation
     * @param nouveaunom
     */
    public void renommeAbbreviation(Abbreviation abbreviation, String nouveaunom) throws GestionMotsException {
        retireAbbreviation(abbreviation);
        abbreviation.definitNom(nouveaunom);
        ajouteAbbreviation(abbreviation);
    }

    /**
     * retourne le nombre d'abbréviations.
     */
    public int obtientCompteAbbreviation() {
        return abbreviations.size();
    }

    /**
     * retourne le nombre de mots.
     */
    public int obtientCompteMots() {
        return mots.size();
    }

    /**
     * Normalise le type de voie non officiel s'il est trouvé, à 4 caractères.<br>
     * Seul le premier mot du type de voie est coupé à 4 caractères.<br>
     * Si typevoie est null, il est recalculé.<br>
     * Si typevoie n'était pas null, la méthode definitSiEstAbbrevie est utilisée, même s'il faisait moins
     * de cinq caractères.
     * @param chaine une chaine d'adresse sans numéro ni répétition
     */
    public String normalise_typevoie_nonofficiel(String chaine, RefTypeVoie typevoie) {
        if (typevoie == null) {
            typevoie = trouveTypeVoie(chaine, null);
        }

        String mot = typevoie.obtientMot();

        if (typevoie.obtientCle() != null && !typevoie.obtientCle().estOfficiel()) {
            // Seul le premier mot du type de voie est coupé.
            String[] mot_decoupe = mot.split(" ");
            if (mot_decoupe[0].length() > 4) {
                // Isole la chaine avant et après
                String prechaine = null;
                if (typevoie.obtientIndex() > 0) {
                    prechaine = chaine.substring(0, typevoie.obtientIndex());
                } else {
                    prechaine = "";
                }
                String postchaine = null;
                if (typevoie.obtientIndex() + typevoie.obtientMot().length() < chaine.length()) {
                    postchaine = chaine.substring(typevoie.obtientIndex() + typevoie.obtientMot().length());
                } else {
                    postchaine = "";
                }

                // Calcule le type de voie coupé.
                StringBuilder sb = new StringBuilder(mot_decoupe[0].substring(0, 4));

                for (int i = 1; i < mot_decoupe.length; i++) {
                    sb.append(' ');
                    sb.append(mot_decoupe[i]);
                }
                String motabbr = sb.toString();

                typevoie.definitMotAbbrevie(motabbr);

                chaine = prechaine + motabbr + postchaine;
            } else {
                typevoie.definitMotAbbrevie(mot);
            }
            typevoie.definitSiAEteAbbrevie(true);
        }
        return chaine;
    }

    /**
     * Normalise le type de voie s'il est trouvé.<br>
     * Si typevoie est null, il est recalculé.<br>
     * Si typevoie n'était pas null et qu'il est abbrévié, la méthode definitSiEstAbbrevie est utilisée.
     * @param chaine une chaine d'adresse sans numéro ni répétition
     */
    public String normalise_typevoie_officiel(String chaine, RefTypeVoie typevoie) {
        if (typevoie == null) {
            typevoie = trouveTypeVoie(chaine, null);
        }

        if (typevoie.obtientCle() != null && typevoie.obtientCle().estOfficiel()) {
            Abbreviation a = typevoie.obtientCle().obtientAbbreviationOfficielle();

            String prechaine = null;
            if (typevoie.obtientIndex() > 0) {
                prechaine = chaine.substring(0, typevoie.obtientIndex());
            } else {
                prechaine = "";
            }
            String postchaine = null;
            if (typevoie.obtientIndex() + typevoie.obtientMot().length() < chaine.length()) {
                postchaine = chaine.substring(typevoie.obtientIndex() + typevoie.obtientMot().length());
            } else {
                postchaine = "";
            }
            typevoie.definitSiAEteAbbrevie(true);
            typevoie.definitMotAbbrevie(a.obtientNom());

            chaine = prechaine + a.obtientNom() + postchaine;
        }
        return chaine;
    }

    /**
     * Supprime la référence de la chaine spécifiée.
     */
    public String supprime(String chaine, RefCle mot) {
        String first = null;
        if (mot.obtientIndex() > 0) {
            first = chaine.substring(0, mot.obtientIndex() - 1);
        } else {
            first = "";
        }
        int index = mot.obtientIndex() + mot.obtientMot().length();

        String second = null;
        if (index < chaine.length()) {
            second = chaine.substring(index);
        } else {
            second = "";
        }
        return first + second;
    }

    /**
     * Abbrevie le mot dans la chaine à la position spécifiée.
     * @param chaine
     * @param mot
     * @param abbreviation
     * @param position
     * @return
     */
    public String abbrevie(String chaine, String mot, char abbreviation, int position) {
        String first = null;
        if (position > 0) {
            first = chaine.substring(0, position);
        } else {
            first = "";
        }
        int index = position + mot.length();

        String second = null;
        if (index < chaine.length()) {
            second = chaine.substring(index);
        } else {
            second = "";
        }
        return first + abbreviation + second;
    }

    /**
     * Abbrevie le mot dans la chaine à la position spécifiée.
     * @param chaine
     * @param mot
     * @param abbreviation
     * @param position
     * @return
     */
    public String abbrevie(String chaine, String mot, String abbreviation, int position) {
        String first = null;
        if (position > 0) {
            first = chaine.substring(0, position);
        } else {
            first = "";
        }
        int index = position + mot.length();

        String second = null;
        if (index < chaine.length()) {
            second = chaine.substring(index);
        } else {
            second = "";
        }
        return first + abbreviation + second;
    }

    /**
     * Abbrevie la référence spécifiée dans la chaine spécifiée.<br>
     * @param cut si le mot n'a pas d'abbréviation, seules les cut premières lettres sont conservées.
     */
    public String abbrevie(String chaine, RefCle mot, int cut) {
        if (mot.obtientMot().length() <= cut) {
            return chaine;
        }

        Abbreviation abbr = null;

        if (mot.obtientCle() != null) {
            abbr = mot.obtientCle().obtientAbbreviationOfficielle();
        }

        String abbrmot = null;
        if (abbr == null) {
            abbrmot = mot.obtientMot().substring(0, cut);
        } else {
            abbrmot = abbr.obtientNom();
        }

        return abbrevie(chaine, mot.obtientMot(), abbrmot, mot.obtientIndex());
    }

    /**
     * Conserve le premier numéro de l'adresse spécifiée en paramètre.<br>
     * Les numéros trouvés sont ajoutés à la liste numéro, qui ne doit pas être null.<br>
     * Les index du numero conservé ne sont pas modifiés.
     * @param chaine 
     * @param autresnumeros Les numéros trouvés dans la chaine.
     * @param numerosrestants les numéros qui ne sont pas conservés.
     */
    public String conservePremierNumero(String chaine, ArrayList<RefNumero> numeros) {
        if (numeros.size() > 1) {
            RefCle last = numeros.get(numeros.size() - 1); // le dernier numéro et sa répétition

            int lastindex = last.obtientIndex() + last.obtientMot().length(); // index de l'espace qui suit le dernier numéro (et son éventuelle répétition)

            String first = numeros.get(0).obtientMot();
            String second = chaine.substring(lastindex);

            chaine = first + second;
        }

        return chaine;
    }

    /**
     * Trie la liste de communes spécifiées selon leur index.<br>
     */
    public void trieCommune(ArrayList<RefCommune> cles, int start, int end) {
        if (start >= end) {
            return;
        }
        if (start + 1 == end) {
            RefCommune cle0 = cles.get(start);
            RefCommune cle1 = cles.get(end);
            if (cle0.obtientIndex() > cle1.obtientIndex()) {
                cles.remove(end); // inversion des deux clés.
                cles.add(start, cle1);
            }
        } else {
            int last_i_index = (start + end) / 2;
            trieCommune(cles, start, last_i_index);
            trieCommune(cles, last_i_index + 1, end);

            // Boucle sur deux indices : i et j.
            int i = start, j = last_i_index + 1;
            while (i <= last_i_index && j <= end) {
                while (i <= last_i_index && cles.get(i).obtientIndex() <= cles.get(j).obtientIndex()) {
                    i++;
                }
                while (j <= end && i <= last_i_index && cles.get(j).obtientIndex() <= cles.get(i).obtientIndex()) {
                    RefCommune clej = cles.get(j);
                    cles.remove(j);
                    cles.add(i, clej); // What about a RefCommune.switch method instead ?
                    i++;
                    last_i_index++;
                    j++;
                }
            }
        }
    }

    /**
     * Trie la liste de pays spécifiées selon leur index.<br>
     */
    public void triePays(ArrayList<RefPays> cles, int start, int end) {
        if (start >= end) {
            return;
        }
        if (start + 1 == end) {
            RefPays cle0 = cles.get(start);
            RefPays cle1 = cles.get(end);
            if (cle0.obtientIndex() > cle1.obtientIndex()) {
                cles.remove(end); // inversion des deux clés.
                cles.add(start, cle1);
            }
        } else {
            int last_i_index = (start + end) / 2;
            triePays(cles, start, last_i_index);
            triePays(cles, last_i_index + 1, end);

            // Boucle sur deux indices : i et j.
            int i = start, j = last_i_index + 1;
            while (i <= last_i_index && j <= end) {
                while (i <= last_i_index && cles.get(i).obtientIndex() <= cles.get(j).obtientIndex()) {
                    i++;
                }
                while (j <= end && i <= last_i_index && cles.get(j).obtientIndex() <= cles.get(i).obtientIndex()) {
                    RefPays clej = cles.get(j);
                    cles.remove(j);
                    cles.add(i, clej); // What about a RefCommune.switch method instead ?
                    i++;
                    last_i_index++;
                    j++;
                }
            }
        }
    }

    /**
     * Trie la liste de clés spécifiées selon leur index.<br>
     */
    public void trie(ArrayList<RefCle> cles, int start, int end) {
        if (start >= end) {
            return;
        }
        if (start + 1 == end) {
            RefCle cle0 = cles.get(start);
            RefCle cle1 = cles.get(end);
            if (cle0.obtientIndex() > cle1.obtientIndex()) {
                cles.remove(end);
                cles.add(start, cle1);
            }
        } else {
            int last_i_index = (start + end) / 2;
            trie(cles, start, last_i_index);
            trie(cles, last_i_index + 1, end);

            int i = start, j = last_i_index + 1;
            while (i <= last_i_index && j <= end) {
                while (i <= last_i_index && cles.get(i).obtientIndex() <= cles.get(j).obtientIndex()) {
                    i++;
                }
                while (j <= end && i <= last_i_index && cles.get(j).obtientIndex() <= cles.get(i).obtientIndex()) {
                    RefCle clej = cles.get(j);
                    cles.remove(j);
                    cles.add(i, clej);
                    i++;
                    last_i_index++;
                    j++;
                }
            }
        }
    }

    /**
     * Trouve les abbréviations répertoriées dans la chaine spécifiée.
     * @param chaine la chaine à examiner.
     * @return
     */
    public ArrayList<RefCle> trouveAbbreviations(String chaine) {
        ArrayList<RefCle> cles = new ArrayList<RefCle>();
        int state = 0;

        // Pour chaque lettre,
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            switch (state) {
                case 0:
                    switch (c) {
                        case '\t':
                        case ' ':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }

            // Commence une nouvelle recherche binaire
            if (state == 1) {
                Abbreviation a = chercheDebutAbbreviation(chaine, i, CategorieMot.Tous);

                if (a != null) {
                    String nom = a.obtientNom();
                    int endindex = i + nom.length();
                    if (endindex == chaine.length() || chaine.charAt(endindex) == ' ' || chaine.charAt(endindex) == '\t') {
                        cles.add(new RefCle(nom, a, i, chaine, CategorieMot.Titre));
                        i = endindex - 1;
                    }
                }
            }
        }

        return cles;
    }

    /**
     * Trouve les noms dans la chaine spécifiée.<br>
     * @param chaine la chaine dans laquelle chercher les noms.
     * @param categorie la categorie des noms à chercher
     * @param officiel si à true, cherche uniquement les noms officiels.
     * NON OPTIMISE
     */
    public ArrayList<RefCle> trouveNoms(String chaine, CategorieMot categorie, boolean officiel) {
        ArrayList<RefCle> cles = new ArrayList<RefCle>();
        int state = 0;

        // Pour chaque lettre,
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            switch (state) {
                case 0:
                    switch (c) {
                        case '\t':
                        case ' ':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }

            // Commence une nouvelle recherche binaire
            if (state == 1) {
                Mot m = chercheDebutMot(chaine, i, categorie);

                if (m != null && (!officiel || m.estOfficiel())) {
                    String nom = m.obtientNom();
                    int endindex = i + nom.length();
                    if (endindex == chaine.length() || chaine.charAt(endindex) == ' ' || chaine.charAt(endindex) == '\t') {
                        cles.add(new RefCle(nom, m, i, chaine, categorie));
                    }
                }
            }
        }

        return cles;
    }

    /**
     * Obtient le gain accordé lorsque l'on abbrévie la clé spécifiée.<br>
     * Si la clé n'a pas d'abbréviation officielle, la taille cut est appliquée.
     * @return le nombre de caractères gagnés.
     */
    public int obtientGainAbbreviation(RefCle cle, int cut) {
        if (cle.obtientCle() != null) {
            return cle.obtientCle().obtientGainAbbreviation(cut);
        } else {
            if (cle.obtientMot().length() > 4) {
                return cle.obtientMot().length() - cut;
            } else {
                return 0;
            }
        }
    }

    /**
     * abbrévie les noms trouvés dans la chaine, s'il ne s'agit pas d'abbréviations officielles.<br>
     * Si le nom n'a pas d'abbréviation, le mot est coupé à cut caractères.<br>
     * Le dernier mot n'est jamais abbrévié.
     * @param chaine la chaine dans laquelle rechercher les noms.
     * @param categorie la catégorie des noms à normaliser.
     * @param officiel indique si les noms choisis doivent être officiel ou non.
     * @param tous indique si toutes les occurences trouvées doivent être supprimées, ou si seuls ceux qui permettent
     * d'atteindre la taille size sont abbréviés.
     * @param cut le nombre de lettre à conserver si le nom trouvé n'a pas d'abbréviation.
     * @param size le nombre de caractère en deça duquel la voie est normalisée.
     * @param sansespace si deux mots sont abbréviés l'un après l'autre, l'espace entre les deux est supprimé.
     */
    public String normalise_Noms(String chaine, CategorieMot categorie, boolean officiel, boolean tous, int cut, int size,
            boolean sansespace) {
        ArrayList<RefCle> noms = trouveNoms(chaine, categorie, officiel);

        int index = 0;
        int lastindex = -1;

        while (index < noms.size() && (tous || chaine.length() > size)) {
            int bonus = 0; // utilise lorsque sansespace = true

            RefCle titre = noms.get(index);

            // S'il ne s'agit pas du dernier mot, et qu'il ne s'agit pas d'une abbréviation répertoriée,
            if (chaine.indexOf(" ", titre.obtientIndex()) != -1 && chercheAbbreviationOfficielle(titre.obtientMot()) == null) {
                chaine = abbrevie(chaine, titre, cut);

                // Si deux mots sont trouvés à la suite,
                if (sansespace && titre.obtientIndex() == lastindex) {
                    // l'espace qui le précède peut être supprimé.
                    chaine = chaine.substring(0, lastindex - 1) + chaine.substring(lastindex);
                    bonus = 1;
                }

                // Il faut ensuite r�duire d'autant tous les index des titres trouvés.
                int gain = obtientGainAbbreviation(titre, cut);
                for (int i = index + 1; i < noms.size(); i++) {
                    noms.get(i).decale(gain + bonus);
                }

                // index du prochain mot dans la chaine mais pas forcemment du 
                // prochain mot à traiter.
                if (sansespace) {
                    lastindex = titre.obtientIndex() - bonus + titre.obtientMot().length() - gain + 1; // formule qui marche

                }
            }
            index++;
        }

        return chaine;
    }

    /**
     * abbrévie les autres noms dans la chaine, jusqu'à atteindre une taille inférieure à size caractère si possible.<br>
     * Les noms sont abbréviés à cut caractères.<br>
     * Attention : tous les mots de plus de cut caractères sont abbréviés, 
     * sauf s'il s'agit de titres, d'abbréviations officielles référencées ou de nombres.<br>
     * Les titres ou abbréviations officielles composés ne sont pas gérés.
     * @param chaine
     * @param cut
     * @param size
     * @return
     */
    public String normalise_AutreNoms(String chaine, int cut, int size) {
        int i = 0, gain = 0;
        int gainnecessaire = chaine.length() - size;
        StringBuilder sb = new StringBuilder();

        // Sépare les mots de la chaine
        String[] listemots = chaine.split(" ");

        // Le dernier nom n'est jamais abbrévié.
        for (; (i < listemots.length - 1) && (gain < gainnecessaire); i++) {
            String mot = listemots[i];

            if (i > 0) {
                sb.append(" ");
            }
            if (mot.length() > cut) {
                Mot m = chercheMotOfficiel(mot);

                // Vérifie s'il ne s'agit pas d'une abbréviation d'un titre qui possède une abbréviation officielle, ou d'un nombre
                if ((m == null || !m.estDeLaCategorie(CategorieMot.Titre)) && chercheAbbreviationOfficielle(mot) == null && !estArticle(mot,
                        0) && !Character.isDigit(mot.charAt(0))) {
                    gain += mot.length() - cut;
                    mot = mot.substring(0, cut);
                }
            }
            sb.append(mot);
        }

        for (; i < listemots.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(listemots[i]);
        }

        return sb.toString();
    }

    /**
     * Supprime l'article trouvé dans la voie.
     * @return
     */
    public String supprimeArticle(String chaine) {
        RefCle typevoie = trouveTypeVoie(chaine, null);

        if (typevoie.obtientMot().length() != 0) {
            RefCle article = trouveArticleVoie(chaine, typevoie);

            if (article.obtientMot().length() > 0) {
                String after = chaine.substring(article.obtientIndex() + article.obtientMot().length());

                return typevoie.obtientMot() + after;
            }
        }

        return chaine;
    }

    /**
     * Normalise les dates trouvées dans la chaine.<br>
     * Cela consiste à changer les jours du mois en nombre.
     * @param chaine
     * @return
     */
    public String normalise_date(String chaine) {
        ArrayList<RefDate> dates = trouveDates(chaine, null);

        for (int i = 0; i < dates.size(); i++) {
            RefDate rd = dates.get(i);

            // Si le jour du mois n'est pas normalisé,
            if (!rd.jourDuMoisEstNombre()) {
                int gain = rd.obtientJourDuMois().length() - rd.obtientJourDuMoisNormal().length();

                chaine = abbrevie(chaine, rd.obtientJourDuMois(), rd.obtientJourDuMoisNormal(), rd.obtientIndexJourDuMois());

                // D�cale les positions des autres dates dans la chaine à cause de l'abbréviation.
                for (int j = i + 1; j < dates.size(); j++) {
                    dates.get(j).decale(gain);
                }
            }
        }

        return chaine;
    }

    /**
     * Retourne l'équivalent phonétique de chaque ligne.
     * @param lignes
     * @return
     */
    public String[] phonetise(String[] lignes) {
        if (lignes == null) {
            return null;

        }
        String[] res = new String[lignes.length];
        for (int i = 0; i < lignes.length; i++) {
            res[i] = Algos.phonex(lignes[i]);
        }
        return res;
    }

    /**
     * Supprime les numéro supplémentaires de la voie spécifiée.<br>
     * numerosSupplementaires peut être null, auquel cas les numéros supplémentaires sont abandonnés.
     * @param numerosSupplementaires Les numéros supplémentaires trouvés dans la chaine.<br>
     * @return La chaine avec un seul numéro et son éventuelle répétition.
     */
    public String normaliseNumeros(String chaine, ArrayList<String> numerosSupplementaires) {
        if (chaine == null || chaine.length() == 0) {
            return "";
        }

        // Trouve les numéros pour ne conserver que le premier.
        ArrayList<RefNumero> numeros = trouveNumeros(chaine);

        // Nécessaire au cas où il y a ambiguité entre le type de voie R
        // et la répétition R.
        trouveTypeVoie(chaine, numeros);

        chaine = conservePremierNumero(chaine, numeros);

        if (numerosSupplementaires != null) {
            // Traite les numéros supplémentaires.
            for (int i = 1; i < numeros.size(); i++) {
                RefNumero rn = numeros.get(i);
                String num = rn.obtientNumeroNormalise();
                if (rn.obtientRepetition() != null) {
                    num = num + " " + rn.obtientRepetition();
                }
                numerosSupplementaires.add(num);
            }
        }

        return chaine;
    }

    /**
     * Normalise à 38 caractères la chaine.<br>
     * Quelque soit la taille de la chaine, seul le premier numéro de l'adresse est conservé.<br>
     * Quelque soit la taille de la chaine, les nombres en lettre des dates sont changés en chiffres.<br>
     * Quelque soit la taille de la chaine, la répétition est abbréviée.<br>
     * Le dernier nom n'est jamais abbrévié.<br>
     * Les opérations effectuées sont les suivantes, jusqu'à ce que la voie fasse moins de 38 caractères:
     * <ul><li>abbrévie le type de voie officiel s'il est présent.</li>
     *     <li>Normalise la répétition</li>
     *     <li>Appel normalise_ligneX pour la voie sans numéro ni répétition</li>
     *     <li>abbrévie les titres officiels</li>
     *     <li>abbrévie tous les prénoms</li>
     *     <li>Supprime l'article</li>
     *     <li>abbrévie les autres type de voie à quatre caractères</li>
     *     <li>abbrévie les autres noms</li>
     * </ul>
     * NB: la chaine complète est normalisée à 38 caractères,
     * mais la chaine sans numéro ni répétition est normalisée à 32.<br>
     * @param chaine la chaine à normaliser
     * @param numerosSupplementaires les éventuels numéros supplémentaires
     */
    public String normalise_38_ligne4(String chaine) {
        if (chaine == null || chaine.length() == 0) {
            return "";
        }

        // Trouve les numéros pour ne conserver que le premier.
        ArrayList<RefNumero> numeros = trouveNumeros(chaine);

        // Nécessaire au cas où il y a ambiguité entre le type de voie R
        // et la répétition R.
        RefTypeVoie typeVoie = trouveTypeVoie(chaine, numeros);

        chaine = conservePremierNumero(chaine, numeros);

        chaine = normalise_date(chaine);

        // Cherche l'éventuelle répétition dans la voie        
        String start = null, reste = null;

        // abbrévie la répétition trouvée, et isole le reste de la voie
        if (numeros.size() > 0) {
            RefNumero numero = numeros.get(0);

            // modifie les index du numéro conservé (car il va devenir le premier mot de la chaine)
            numero.decale(numero.obtientIndex());

            int reste_idx;

            if (numeros.get(0).obtientRepetition() != null) {
                if (numero.obtientRepetition().length() > 1) {
                    int gain = numero.obtientRepetition().length() - 1;

                    chaine = abbrevie(chaine, numero.obtientRepetition(), numero.obtientRepetitionNormalise(),
                            numero.obtientIndexRepetition());
                }

                start = numero.obtientNumero() + " " + numero.obtientRepetitionNormalise();
                reste_idx = numero.obtientIndexRepetition() + 2;
            } else {
                start = numero.obtientNumeroNormalise();
                reste_idx = numero.obtientIndex() + numero.obtientNumero().length() + 1;
            }

            if (reste_idx < chaine.length()) {
                reste = chaine.substring(reste_idx); // (prend en compte la normalisation)

            } else {
                reste = "";


            }
            start += " ";
        } else {
            start = "";
            reste = chaine;
        }

        // Décale le type de voie car c'est par lui que commence la chaine reste.
        typeVoie.decale(typeVoie.obtientIndex());

        // Si le reste de la voie prend moins de 32 caractères
        if (reste.length() <= 32) {
            return chaine;
        } else {
            // normalise le type de voie s'il est présent
            reste = normalise_typevoie_officiel(reste, typeVoie);

            if (reste.length() <= 32) {
                return start + reste;
            // abbrévie les titres officiels.
            }
            reste = normalise_Noms(reste, CategorieMot.Titre, true, false, 1, 32, false);

            if (reste.length() <= 32) {
                return start + reste;
            // abbrévie tous les prénoms référencés à 1 caractère.
            }
            reste = normalise_Noms(reste, CategorieMot.Prenom, false, true, 1, 32, true);

            if (reste.length() <= 32) {
                return start + reste;
            // Supprime l'article
            }
            reste = supprimeArticle(reste);

            if (reste.length() <= 32) {
                return start + reste;
            // abbrévie le type de voie non officiels si nécessaire
            }
            if (!typeVoie.aEteAbbrevie()) {
                reste = normalise_typevoie_nonofficiel(reste, typeVoie);
            }
            if (reste.length() <= 32) {
                return start + reste;
            }
            if (typeVoie.obtientMot().length() > 0 && typeVoie.aEteAbbrevie()) {
                int typevoieendindex = typeVoie.obtientIndex() + typeVoie.obtientMotAbbrevie().length() + 1;
                String startreste = reste.substring(0, typevoieendindex);
                String endreste = reste.substring(typevoieendindex);

                // abbrévie les autres noms non officiels, différents du type de voie.
                endreste = normalise_AutreNoms(endreste, 1, 32 - startreste.length());

                // la normalisation est sensée être terminée ici.
                return start + startreste + endreste;
            } else {
                // abbrévie les autres noms non officiels, différents du type de voie.
                reste = normalise_AutreNoms(reste, 1, 32);

                // la normalisation est sensée ici.
                return start + reste;
            }
        }
    }

    /**
     * normalise_1 à 38 caractères la chaine correspondant à une ligne 6.<br>
     * Seul le nom de la ville est abbrévié, à 32 caractères.
     * Les opérations effectuées sont les suivantes:
     * <ul><li>abbrévie les titres officiels s'ils sont présent</li>
     *     <li>abbrévie les prénoms</li>
     *     <li>abbrévie les autres noms à 1 caractères</li></ul>
     * N.B.: le dernier nom n'est jamais abbrévié.
     * Si un cedex est présent, il est possible que la chaine dépasse 38 caractères.
     */
    public String normalise_38_ligne6(String chaine) {
        // Trouve le nom de la ville
        RefCle nomVille = trouveNomVille(chaine, null);

        String start = null;
        if (nomVille.obtientIndex() != 0) {
            start = chaine.substring(0, nomVille.obtientIndex());
        } else {
            start = "";
        }
        String end = null;
        int endindex = nomVille.obtientIndex() + nomVille.obtientMot().length();

        if (endindex < chaine.length()) {
            end = chaine.substring(endindex);
        } else {
            end = "";
        }
        String ville = nomVille.obtientMot();

        ville = normalise_Noms(ville, CategorieMot.Titre, true, false, 1, 32, false);

        if (ville.length() <= 32) {
            return start + ville + end;
        }
        ville = normalise_Noms(ville, CategorieMot.Prenom, false, true, 1, 32, true);

        if (ville.length() <= 32) {
            return start + ville + end;
        }
        ville = normalise_AutreNoms(ville, 1, 32);

        return start + ville + end;
    }

    /**
     * Normalise à 38 caractères la chaine correspondant à une ligne 2.<br>
     * Les op�rations effectuées sont les suivantes:
     * <ul><li>abbrévie les titres officiels s'ils sont présent</li>
     *     <li>abbrévie les autres noms à 1 caractères</li></ul>
     * N.B.: le dernier nom n'est jamais abbrévié.
     */
    public String normalise_38_ligne2(String chaine) {
        if (chaine.length() <= 38) {
            return chaine;
        }
        chaine = normalise_Noms(chaine, CategorieMot.Titre, true, false, 1, 38, false);

        if (chaine.length() <= 38) {
            return chaine;
        }
        chaine = normalise_AutreNoms(chaine, 1, 38);

        return chaine;
    }

    /**
     * Normalise à 38 caractères la chaine.<br>
     * Les opérations effectuées sont les suivantes:
     * <ul><li>abbrévie les titres officiels s'ils sont présent</li>
     *     <li>abbrévie les prénoms</li>
     *     <li>abbrévie les autres noms à 1 caractères</li></ul>
     * N.B.: le dernier nom n'est jamais abbrévié.
     */
    public String normalise_38_ligneX(String chaine) {
        if (chaine.length() <= 38) {
            return chaine;
        }
        chaine = normalise_Noms(chaine, CategorieMot.Titre, true, false, 1, 38, false);

        if (chaine.length() <= 38) {
            return chaine;
        }
        chaine = normalise_Noms(chaine, CategorieMot.Prenom, false, true, 1, 38, true);

        if (chaine.length() <= 38) {
            return chaine;
        }
        chaine = normalise_AutreNoms(chaine, 1, 38);

        return chaine;
    }

    /**
     * Normalise à 38 caractères la chaine.<br>
     * @param chaine la chaine à normaliser
     * @param ligne le numéro de la ligne de cette chaine
     * @param numerosSupplementaires les éventuels numéros supplémentaires trouvés dans la ligne 4
     */
    public String normalise_38(String chaine, int ligne) {
        if (chaine == null || chaine.length() == 0) {
            return "";
        // Il n'est pas testé tout de suite si la chaine fait moins de 38 caractères
        // car certaines normalisation sont obligatoires (notamment un seul numéro).
        }
        switch (ligne) {
            case 4:
                // Cas particulier : il faut traiter les types de voies, les numéros.
                return normalise_38_ligne4(chaine);
            case 6:
                // Cas particulier : le dernier nom de la ville n'est pas forcemment le dernier nom et ne
                // doit pas être abbrévié.
                return normalise_38_ligne6(chaine);
            case 2:
                // Cas particulier : les prénoms ne sont doivent pas être abbréviés (mention Chez).
                return normalise_38_ligne2(chaine);
            case 1:
                // Cas particulier : les prénoms ne sont doivent pas être abbréviés (mention Chez).
                return normalise_38_ligne2(chaine);
            default:
                return normalise_38_ligneX(chaine);
        }
    }

    /**
     * Corrige les abbréviations trouvées dans la chaine.<br>
     * L'abbréviation du type de voie est corrigée. Pour cette opération, le code_departement n'est pas
     * obligatoire.<br>
     * Les abbréviations ne sont corrigées que si leur contexte correspond au contexte d'une
     * voie existante. Pour cette opération, le code_departement est obligatoire.<br>
     * Par exemple: le terme "AV" dans la chaine "AV DU MARECHAL DE TASSIGNY" à dans son contexte
     * les termes "MAL et TASSIGNY" qui font parti du contexte de la voie "AVENUE DU MARECHAL DE TASSIGNY".
     * Le terme est alors remplacé par sa version non abbréviée.<br>
     * Si une abbréviation de la ligne 4 correspond à plusieurs mots dont le contexte correspond à une voie
     * existante, le terme est corrigé en le terme de priorité la moins élevée. Si aucune priorité n'a été définie,
     * le terme n'est pas corrigé.
     * @param chaine la chaine dans laquelle est recherchée les abbréviations et qui sert de contexte.
     * @param ligne la ligne où a été trouvée la chaine.
     * @param code_departement s'il est null, les codes de département par défaut sont utilisés.
     * @param connection la connection au référentiel, nécessaire uniquement pour la ligne 4.
     */
    public String corrige_abbreviations_ligne4(String chaine, ArrayList<String> codes_departement, Connection connection) throws
            SQLException {
        if (chaine == null || chaine.length() == 0) {
            return "";
        }
        // Trouve le type de voie
        RefCle typedevoie = trouveTypeVoie(chaine, null);

        String mottypedevoie = typedevoie.obtientMot();

        // Si un type de voie est présent,
        if (mottypedevoie.length() > 0) {
            // Si son écriture est différente de son écriture complète,
            if (mottypedevoie.compareTo(typedevoie.obtientCle().obtientNom()) != 0) {
                // Corrige le type de voie.
                chaine = abbrevie(chaine, mottypedevoie, typedevoie.obtientCle().obtientNom(), typedevoie.obtientIndex());
            }
        }

        if (connection != null) {
            // Trouve les abbréviations.
            ArrayList<RefCle> cles = chercheAbbreviationsDansChaine(chaine, CategorieMot.Tous);

            for (int i = 0; i < cles.size(); i++) {
                RefCle cle = cles.get(i);
                Abbreviation a = cle.obtientCleAbbreviation();

                boolean trouve = false;
                boolean plusieurs = false; // indique si plusieurs mots pourraient correspondre

                int priorite = Abbreviation.PRIORITE_MAX;
                Mot mottrouve = null;

                // Pour chacun des mots pour lesquels elle est une abbréviation, il 
                // faut comparer son contexte au référentiel.
                for (int j = 0; j < a.obtientCompteMot(); j++) {
                    Mot m = a.obtientMot(j);
                    // Pour chacune des catégories à laquelle appartient le mot,
                    // Il n'est pas nécessaire de faire toutes les catégories à partir du moment
                    // ou l'une est trouvée (d'où mottrouve!=m).
                    for (int k = 0; (mottrouve != m) && (k < m.obtientCompteCategorie()); k++) {
                        CategorieAmbiguite ca;

                        if (m.obtientCategorie(k) == CategorieMot.Cle) {
                            ca = CategorieAmbiguite.CleDansVoie;
                        } else if (m.obtientCategorie(k) == CategorieMot.Titre) {
                            ca = CategorieAmbiguite.TitreDansVoie;
                        } else {
                            ca = CategorieAmbiguite.Aucune;
                        }

                        if (ca != CategorieAmbiguite.Aucune) {
                            // Si le contexte correspond à une voie existante pour cette catégorie,
                            if (referentiel.resoudAmbiguite(m.obtientNom(), a.obtientNom(), ca.toString(), chaine, codes_departement,
                                    connection)) {
                                // Un mot pourrait correspondre
                                if (!trouve) {
                                    trouve = true;
                                    mottrouve = m;
                                    priorite = a.obtientPrioriteMot(j);
                                } else // Si un mot correspondait déjà, il faut comparer leurs priorités.
                                {
                                    plusieurs = true;
                                    if (a.obtientPrioriteMot(j) < priorite) {
                                        // Si sa priorité est meilleure, ce mot est choisi.
                                        mottrouve = m;
                                        priorite = a.obtientPrioriteMot(j);
                                    }
                                }
                            }
                        }
                    }
                }

                // Le mot n'est pas choisi si plusieurs mots sans priorités ont été trouvés.
                // Il est choisi dans les autres cas.
                if (trouve && (priorite < Abbreviation.PRIORITE_MAX || !plusieurs)) {
                    int gain = a.obtientNom().length() - mottrouve.obtientNom().length();

                    // Désabbrévie le mot
                    chaine = abbrevie(chaine, a.obtientNom(), mottrouve.obtientNom(), cle.obtientIndex());

                    // Puis corrige les index des abbréviations suivantes
                    for (int j = i + 1; j < cles.size(); j++) {
                        cles.get(j).decale(gain);
                    }
                }
            }
        }

        return chaine;
    }

    /**
     * Corrige les abbréviations trouvées dans la chaine.<br>
     * Les abbréviations d'un seul mot sont corrigées.<br>
     * Les abbréviations de plusieurs mots ne sont pas corrigées.<br>
     * Les abbréviation d'une seule lettre ne sont pas corrigées.<br>
     * @param chaine la chaine dans laquelle est recherchée les abbréviations et qui sert de contexte.
     * @param ligne la ligne où a été trouvée la chaine.
     */
    public String corrige_abbreviations_ligneX(String chaine, int ligne) {
        // Trouve les abbréviations.
        ArrayList<RefCle> cles = trouveAbbreviations(chaine);

        for (int i = 0; i < cles.size(); i++) {
            RefCle cle = cles.get(i);
            Abbreviation a = cle.obtientCleAbbreviation();

            // Si l'abbréviation fait plus d'un caractère,
            if (a.obtientNom().length() > 1) {
                Mot m = null;

                if (a.obtientCompteMot() == 1) {
                    m = a.obtientMot(0); // S'il ne s'agit de l'abbréviation que d'un seul mot,

                } else {
                    m = a.obtientMotPrefere(); // Si plusieurs mots sont disponibles, le mot prefere est choisi

                }
                if (m != null) {
                    int gain = a.obtientNom().length() - m.obtientNom().length();

                    // Désabbrévie le mot
                    chaine = abbrevie(chaine, a.obtientNom(), m.obtientNom(), cle.obtientIndex());

                    // Puis corrige les index des abbréviations suivantes
                    for (int j = i + 1; j < cles.size(); j++) {
                        cles.get(j).decale(gain);
                    }
                }
            }
        }

        return chaine;
    }

    /**
     * Effectue la deuxième passe de normalisation, pour une ligne différente de la ligne 4.
     * <ul>
     *     <li>Supprime les zéros inutiles</li>
     *     <li>Corrige les abbréviations abusives</li>
     *     <li>Normalise à 38 caractères</li>
     * </ul>
     * Rien n'est fait s'il s'agit de la ligne 4.
     * @param chaine la chaine à normaliser
     * @param ligne la ligne où a été trouvée la chaine
     * @param abbrevie permet d'activer ou non la normalisation à 38 caractères
     * @param desabbrevie permet d'activer ou non la désabbréviation
     * @return
     */
    public String normalise_2_ligneX(String chaine, int ligne, boolean abbrevie, boolean desabbrevie) {
        if (ligne != 4) {
            chaine = Algos.supprimeZerosInutiles(chaine, false); // les codes de département et codes postaux ne devrait plus être présents.
            if (desabbrevie) {
                chaine = corrige_abbreviations_ligneX(chaine, ligne);

            }
            if (abbrevie) {
                chaine = normalise_38(chaine, ligne);

            }
        }
        return chaine;
    }

    /**
     * Effectue la deuxième passe de normalisation, pour la ligne 5 ou 6.
     * <ul>
     *     <li>Corrige les abbréviations abusives</li>
     *     <li>Normalise à 38 caractères</li>
     * </ul>
     * @param chaine la chaine à normaliser
     * @param ligne la ligne où a été trouvée la chaine
     * @param abbrevie permet d'activer ou non la normalisation à 38 caractères
     * @param desabbrevie permet d'activer ou non la désabbréviation
     * @return
     */
    public String normalise_2_ligne5ou6(String chaine, int ligne, boolean abbrevie, boolean desabbrevie) {
        if (desabbrevie) {
            chaine = corrige_abbreviations_ligneX(chaine, ligne);

        }
        if (abbrevie) {
            chaine = normalise_38(chaine, ligne);

        }
        return chaine;
    }

    /**
     * Effectue la deuxième passe de normalisation:
     * <ul><li>Supprime les zéros inutiles</li>
     *     <li>Corrige les abbréviations abusives</li>
     *     <li>Supprime l'éventuel numéro supplémentaire (pour la ligne 4)</li>
     *     <li>Normalise à 38 caractères</li>
     * </ul>
     * Les paramètres Utilisés par la ligne 4 ne sont pas nécessaires pour les autres lignes.<br>
     * Les abbréviations ne sont pas corrigées si le code_departement, la connection, ou la date n'est pas spécifiée.
     * @param chaine la chaine à normaliser
     * @param numerosSupplementaires la liste des numéros supplémentaires éventuellement trouvé dans la ligne 4
     * @param abbrevie permet d'activer ou non l'abbréviation à 38 caractères
     * @param desabbrevie permet d'activer ou non la désabbréviation
     * @param codes_departement le département auquel appartient la ligne 4, s'il est null, les codes de département par défaut sont utilisés.
     * @param connection la connection au référentiel Utilisé pour comparer les contextes pour la ligne 4
     * @return
     */
    public String normalise_2_ligne4(String chaine, ArrayList<String> numerosSupplementaires,
            boolean abbrevie, boolean desabbrevie,
            String[] codes_departement, Connection connection) throws SQLException {
        chaine = Algos.supprimeZerosInutiles(chaine, false);
        if (desabbrevie) {
            ArrayList<String> al_codes_departement = new ArrayList<String>();
            if (codes_departement != null) {
                for (int i = 0; i < codes_departement.length; i++) {
                    al_codes_departement.add(codes_departement[i]);

                }

            }
            chaine = corrige_abbreviations_ligne4(chaine, al_codes_departement, connection);
        }

        chaine = normaliseNumeros(chaine, numerosSupplementaires);
        if (!abbrevie) {
            chaine = normalise_date(chaine);
        } else {
            chaine = normalise_38_ligne4(chaine);

        }
        return chaine;
    }

    /**
     * Effectue la première passe de normalisation:
     * <ul><li>Mise en majuscule</li>
     *     <li>Ponctuation</li>
     *     <li>Termes entre parenth�ses</li>
     *     <li>Accentuation</li></ul>
     * @param lignes les lignes d'adresse à normaliser. 
     * @return
     */
    public String[] normalise_1(String[] lignes) {
        return Algos.normalise_1(lignes);
    }

    /**
     * Effectue la deuxième passe de normalisation:
     * <ul>
     *   <li>Supprime les zéros inutiles (hors code de département et code postal)</li>
     *   <li>Supprime les numéros supplémentaires</li>
     *   <li>Désabbrévie les noms de voies</li>
     *   <li>Normalise à 38 caractères</li>
     * </ul>
     * Toutefois, la normalisation à 38 caractères peut être désactivée pour les lignes 4 et 6.<br>
     * Si aucun code de département n'est trouvé, les codes de département par défaut sont utilisés.
     * @param lignes les lignes de l'adresse, au nombre de 6 ou 7.
     * @param numerosSupplementaires la liste qui contiendra les éventuels numéros supplémentaires trouvés.
     * @param abbrevie permet d'activer ou non la normalisation à 38 caractères.
     * @param desabbrevie permet d'activer ou non la désabbréviation.
     * @param gererPays permet d'activer ou non la gestion des pays
     * @param departements_presumes permet de retourner les départements présumés (égal à str_departements), en 
     * fin de tableau, après les éventuels numéros supplémentaires
     * @param str_departements est la liste des départements (séparés par des virgules et sans espaces) dans laquelle effectuer la
     * désabbréviation si desabbrevie est à true et qu'aucun code postal ou code de département n'est
     * spécifié sur la ligne 6. Si aucun département n'est spécifié, ou que departements est null, 
     * La liste des départements par défaut est utilisée.
     * @param connection la connection au référentiel qui permettra de gérer la désabbréviation.
     * @return les lignes normalisées (6 si !gererPays, 7 sinon) et une 7e resp 8e ligne contenant éventuellement les numéros supplémentaires trouvés dans la ligne 4.
     */
    public String[] normalise_2(String[] lignes,
            ArrayList<String> numerosSupplementaires,
            boolean abbrevie,
            boolean desabbrevie,
            boolean gererPays,
            String str_departements,
            Connection connection) throws SQLException {
        // WA 01/2012 Pays
        int nbLinesAddr = gererPays ? 7 : 6;

        String[] res = new String[nbLinesAddr + 1];
        if (lignes.length > 0) {
            res[0] = lignes[0];

        } else {
            res[0] = "";

        }
        if (lignes.length > 1) {
            res[1] = normalise_2_ligneX(lignes[1], 2, abbrevie, desabbrevie);

        } else {
            res[1] = "";

        }
        if (lignes.length > 2) {
            res[2] = normalise_2_ligneX(lignes[2], 3, abbrevie, desabbrevie);

        } else {
            res[2] = "";
        // La ligne 6 est normalisée avant la ligne 4 pour
        // obtenir le code de département.
        }
        if (lignes.length > 5) {
            res[5] = normalise_2_ligne5ou6(lignes[5], 6, abbrevie, desabbrevie);

        } else {
            res[5] = "";
        }
        String[] departements = null;
        if (desabbrevie) {
            RefCle rcdpt = trouveCodeDepartement(res[5]);
            String dpt = rcdpt.obtientMot();
            if (dpt.length() == 0) {
                dpt = null;
            }
            departements = new String[]{
                dpt
            };
        } else if (str_departements != null) {
            departements = str_departements.split(",");
            if (departements.length == 0) {
                departements = null;
            }
        }

        if (lignes.length > 3) {
            res[3] = normalise_2_ligne4(lignes[3], numerosSupplementaires, abbrevie, desabbrevie, departements, connection);

        } else {
            res[3] = "";


        }
        if (lignes.length > 4) {
            res[4] = normalise_2_ligne5ou6(lignes[4], 5, abbrevie, desabbrevie);

        } else {
            res[4] = "";
        }

        // WA 01/2012 Pays
        if ((lignes.length > 6) && (gererPays)) {
            res[6] = normalise_2_ligneX(lignes[6], 7, false, false);
        }

        if (numerosSupplementaires.size() == 0) {
            res[nbLinesAddr] = "";
            return res;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numerosSupplementaires.size(); i++) {
                if (i > 0) {
                    sb.append(";");
                }
                sb.append(numerosSupplementaires.get(i));
            }
            res[nbLinesAddr] = sb.toString();
            return res;
        }
    }

    /**
     * Retourne la liste des numéros trouvés en début de chaine.<br>
     * La liste de numéro doit répondre à ce motif:<br>
     * [DE|D|DU] {X [B|T|C|Q|BIS|TER|QUATER|QUINQUIES] [A|AU|ET|OU]}*<br>
     * où X est une suite ininterrompue de chiffres
     * @param chaine la chaine de ligne 4 normalisée et restructurée
     */
    public ArrayList<RefNumero> trouveNumeros(String chaine) {
        ArrayList<RefNumero> res = new ArrayList<RefNumero>();
        if (chaine == null) {
            return res;

        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sbnumero = new StringBuilder();
        char repetition = (char) 0;
        int state = 0;
        int spaces = 0;
        int index = 0;
        int startindex = 0;
        int repetitionindex = 0;
        while (state != -1 && index < chaine.length()) {
            char c = chaine.charAt(index);
            switch (state) {
                case 0: // départ. possible: chiffres 'DE' 'DU' 'DES' 'LE' 'LES' espaces

                    switch (c) {
                        case 'D':
                            state = 2;
                            break;
                        case 'L':
                            state = 3;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case ' ':
                        case '\t':
                            break;
                        default:
                            state = -1;
                            break;
                    }
                    break;
                case 1: // après un chiffre. possible : chiffre 'A' 'AU' 'AUX' 'B' 'BIS' 'C' 'QUINQUIES' 'Q'
                    // 'T' 'TER' n'importe quelle lettre 'ET' 'OU' espaces

                    switch (c) {
                        default:
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        // Repetitions sous la forme d'une seule lettre.
                        case 'C':
                        case 'D':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'P':
                        case 'R':
                        case 'S':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                            sb.append(c);
                            repetition = c;
                            repetitionindex = index;
                            state = 7;
                            break;
                        // répétition A suivi d'un article, article A, AU, AUX
                        case 'A':
                            sb.append(c);
                            repetition = 'A';
                            repetitionindex = index;
                            state = 8;
                            break;
                        // B, BIS
                        case 'B':
                            sb.append(c);
                            repetition = 'B';
                            repetitionindex = index;
                            state = 15;
                            break;
                        // Q, QUINQUIES
                        case 'Q':
                            sb.append(c);
                            repetition = 'Q';
                            repetitionindex = index;
                            state = 18;
                            break;
                        // T, TER
                        case 'T':
                            sb.append(c);
                            repetition = 'T';
                            repetitionindex = index;
                            state = 27;
                            break;
                        // O, OU
                        case 'O':
                            sb.append(c);
                            repetition = 'O';
                            repetitionindex = index;
                            state = 30;
                            break;
                        // E, ET
                        case 'E':
                            sb.append(c);
                            repetition = 'E';
                            repetitionindex = index;
                            state = 31;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case '\t':
                        case ' ':
                            sb.append(c);
                            spaces = 1;
                            state = 6;
                            break;
                    }
                    break;
                case 2: // après début article : D. possible : DE DU DES

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case 'E':
                            state = 5;
                            break;
                        case 'U':
                            state = 4;
                            break;
                    }
                    break;
                case 3: // après début article : L. possible LE LES

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case 'E':
                            state = 5;
                            break;
                    }
                    break;
                case 4: // après article DE DU LE LES A AU AUX ET OU: possible: chiffres, espace

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case ' ':
                        case '\t':
                            state = 4;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 5: // après début article LE DE. possible LES DES chiffres espaces

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case 'S':
                            state = 4;
                            break;
                        case ' ':
                        case '\t':
                            state = 4;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 6: // après un numéro et un espace. possibles: chiffre 'A' 'AU' 'AUX' 'B' 'BIS' 'C' 'QUINQUIES' 'Q'
                    // 'T' 'TER' n'importe quelle lettre 'ET' 'OU' espaces numero

                    switch (c) {
                        default:
                            // fin du numéro.
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - spaces); // supprime les espaces.

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        // Repetitions sous la forme d'une seule lettre.
                        case 'C':
                        case 'D':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'P':
                        case 'R':
                        case 'S':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                            sb.append(c);
                            repetition = c;
                            repetitionindex = index;
                            state = 7;
                            break;
                        // répétition A suivi d'un article, article A, AU, AUX
                        case 'A':
                            sb.append(c);
                            repetition = 'A';
                            repetitionindex = index;
                            state = 8;
                            break;
                        // B, BIS
                        case 'B':
                            sb.append(c);
                            repetition = 'B';
                            repetitionindex = index;
                            state = 15;
                            break;
                        // Q, QUINQUIES
                        case 'Q':
                            sb.append(c);
                            repetition = 'Q';
                            repetitionindex = index;
                            state = 18;
                            break;
                        // T, TER
                        case 'T':
                            sb.append(c);
                            repetition = 'T';
                            repetitionindex = index;
                            state = 27;
                            break;
                        // O, OU
                        case 'O':
                            sb.append(c);
                            repetition = 'O';
                            repetitionindex = index;
                            state = 30;
                            break;
                        // E, ET
                        case 'E':
                            sb.append(c);
                            repetition = 'E';
                            repetitionindex = index;
                            state = 31;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // le numéro est terminé, un deuxième commence
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - spaces); // supprime les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case '\t':
                        case ' ':
                            sb.append(c);
                            state = 6;
                            spaces++;
                            break;
                    }
                    break;
                case 7: // après un caractère non ambigu. possibles: espace, chiffres

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // supprime la répétition et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case ' ':
                        case '\t':
                            // la répétition est donc valide.
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition(Character.toString(repetition));
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14; // attendu : AUX AU A OU ET espaces ou chiffres

                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // la répétition est donc valide
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition(Character.toString(repetition));
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 8: // après un nombre et un A. possibles: répétition A suivi d'un article, article A, AU, AUX

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // supprime le A et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case '\t':
                        case ' ':
                            state = 9;
                            break;
                        case 'U':
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // supprime le A

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 11;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // Il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // supprime le A

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 9: // après un nombre, un a puis des espaces. possibles: espace, nombre, articles A AU AUX OU ET

                    switch (c) {
                        case '\t':
                        case ' ':
                            break;
                        default:
                            // il s'agissait de la répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("A");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case 'A':
                            // il s'agissait donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("A");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 10;
                            break;
                        case 'O':
                            // il s'agissait donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("A");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 12;
                            break;
                        case 'E':
                            // il s'agissait donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("A");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 13;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // Il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // supprime le A et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sb.toString());
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 10: // après un article A. possibles : espace, AU, AUX

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case ' ':
                        case '\t':
                            state = 4;
                            break;
                        case 'U':
                            state = 11;
                            break;
                    }
                    break;
                case 11: // après un article AU. possibles: chiffres, espaces, AUX

                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 4;
                            break;
                        case 'X':
                            state = 4;
                            break;
                        default:
                            state = -1;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 12: // après un article O. possibles: OU

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case 'U':
                            state = 4;
                            break;
                    }
                    break;
                case 13: // après un article E. possibles: ET

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case 'T':
                            state = 4;
                            break;
                    }
                    break;
                case 14: // après un numéro et une répétition et un espace. possibles: nombres, espace, ou article A AU AUX ET OU

                    switch (c) {
                        default:
                            state = -1;
                            break;
                        case '\t':
                        case ' ':
                            state = 14;
                            break;
                        case 'A':
                            state = 10;
                            break;
                        case 'E':
                            state = 13;
                            break;
                        case 'O':
                            state = 12;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 15: // après un numéro et un B. possibles: espaces, numéro, BIS

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le B et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case '\t':
                        case ' ':
                            // il s'agit d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("B");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        case 'I':
                            sb.append('I');
                            state = 16;
                            break;
                    }
                    break;
                case 16: // après un numéro et un BI. possibles: BIS

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 2 - spaces); // Supprime le BI et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case 'S':
                            sb.append('S');
                            state = 17;
                            break;
                    }
                    break;
                case 17: // après un numéro et un BIS. possibles: espaces numéro

                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 3 - spaces); // Supprime le BIS et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("BIS");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case '\t':
                        case ' ':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("BIS");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 3 - spaces); // Supprime le BIS et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 18: // après un numéro et un Q. possibles : espaces, numéros, QUINQUIES, QUATER
                    switch (c) {
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le Q et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case ' ':
                        case '\t':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("Q");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        case 'U':
                            sb.append('U');
                            state = 19;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("Q");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 19: // après un numéro et QU. possibles : QUINQUIES, QUATER
                    switch (c) {
                        case 'I':
                            state = 20;
                            sb.append('I');
                            break;
                        case 'A':
                            state = 32;
                            sb.append('A');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 2 - spaces); // Supprime le QU et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 20: // après un numéro et QUI. possibles : QUINQUIES.
                    switch (c) {
                        case 'N':
                            state = 21;
                            sb.append('N');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 3 - spaces); // Supprime le QUI et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 21: // après un numéro et QUIN. possibles : QUINQUIES.

                    switch (c) {
                        case 'Q':
                            state = 22;
                            sb.append('Q');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 4 - spaces); // Supprime le QUIN et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 22: // après un numéro et QUINQ. possibles : QUINQUIES.

                    switch (c) {
                        case 'U':
                            state = 23;
                            sb.append('U');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 5 - spaces); // Supprime le QUINQ et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 23: // après un numéro et QUINQU. possibles : QUINQUIES.

                    switch (c) {
                        case 'I':
                            state = 24;
                            sb.append('I');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 6 - spaces); // Supprime le QUINQU et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 24: // après un numéro et QUINQUI. possibles : QUINQUIES.

                    switch (c) {
                        case 'E':
                            state = 25;
                            sb.append('E');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 7 - spaces); // Supprime le QUINQUI et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 25: // après un numéro et QUINQUIE. possibles : QUINQUIES.

                    switch (c) {
                        case 'S':
                            state = 26;
                            sb.append('S');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 8 - spaces); // Supprime le QUINQUIE et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 26: // après un numéro et QUINQUIES. possibles : espaces, chiffres.

                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("QUINQUIES");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case '\t':
                        case ' ':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("QUINQUIES");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 9 - spaces); // Supprime le QUINQUIES et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 27: // Un numéro suivi de T. possibles : espace, numéro, TER

                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("T");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case 'E':
                            sb.append('E');
                            state = 28;
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le T et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case ' ':
                        case '\t':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("T");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                    }
                    break;

                case 28: // suivant un numéro et TE. possibles : TER.
                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 2 - spaces); // Supprime le TE et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case 'R':
                            sb.append('R');
                            state = 29;
                            break;
                    }
                    break;
                case 29: // suivant un numéro et TER: possibles :espaces, numero

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 3 - spaces); // Supprime le TER et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case ' ':
                        case '\t':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("TER");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("TER");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 30: // suivant un nombre et un O. possibles: espace, chiffres, OU

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le O et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case 'U':
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le O et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 4;
                            break;
                        case '\t':
                        case ' ':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("O");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("O");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 31: // après un numéro et un E. possibles : numéro, espace, ET

                    switch (c) {
                        default:
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le E et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                        case 'T':
                            // il ne s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1 - spaces); // Supprime le E et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 4;
                            break;
                        case '\t':
                        case ' ':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("E");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("E");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                    }
                    break;
                case 32: // après un numéro et QUA. possibles : QUATER.

                    switch (c) {
                        case 'T':
                            state = 33;
                            sb.append('T');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 3 - spaces); // Supprime le QUA et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 33: // après un numéro et QUAT. possibles : QUATER.

                    switch (c) {
                        case 'E':
                            state = 34;
                            sb.append('E');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 4 - spaces); // Supprime le QUAT et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 34: // après un numéro et QUATE. possibles : QUATER.

                    switch (c) {
                        case 'R':
                            state = 35;
                            sb.append('R');
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 5 - spaces); // Supprime le QUATE et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
                case 35: // après un numéro et QUATER. possibles : espaces, chiffres.

                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("QUATER");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 1;
                            startindex = index;
                            sb.append(c);
                            sbnumero.append(c);
                            break;
                        case '\t':
                        case ' ':
                            // il s'agit donc d'une répétition
                            if (sb.length() > 0) {
                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition("QUATER");
                                rn.definitIndexrepetition(repetitionindex);
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = 14;
                            break;
                        default:
                            // il s'agit donc pas d'une répétition
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 6 - spaces); // Supprime le QUATER et les espaces

                                RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                                rn.definitNumero(sbnumero.toString());
                                rn.definitRepetition();
                                res.add(rn);
                                sb.setLength(0);
                                sbnumero.setLength(0);
                            }
                            state = -1;
                            break;
                    }
                    break;
            }
            index++;
        }

        // après le caractère final
        switch (state) {
            case 1: // unique chiffre
            case 6: // après un numéro et un espace
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - spaces); // supprime les espaces.

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 7: // nombre espace lettre
            case 8: // nombre espace A
            case 15: // nombre espace B
            case 31: // nombre espace E
            case 18: // nombre espace Q
            case 27: // nombre espace T
            case 30: // nombre espace O
                if (sb.length() > 0) {
                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition(Character.toString(repetition));
                    rn.definitIndexrepetition(repetitionindex);
                    res.add(rn);
                }
                break;
            case 9: // nombre espace A espaces
                if (sb.length() > 0) {
                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition("A");
                    rn.definitIndexrepetition(repetitionindex);
                    res.add(rn);
                }
                break;
            case 16: // nombre espace BI ou nombre espace QU ou nombre espace TE
            case 19:
            case 28:
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2 - spaces); // Supprime le BI/QU et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 17: // nombre espace BIS
                if (sb.length() > 0) {
                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition("BIS");
                    rn.definitIndexrepetition(repetitionindex);
                    res.add(rn);
                }
                break;
            case 20: // nombre espace QUI ou nombre espace QUA
            case 32:
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 3 - spaces); // Supprime le QUI/QUA et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 21: // nombre espace QUIN ou nombre espace QUAT
            case 33:
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 4 - spaces); // Supprime le QUIN/QUAT et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 22: // nombre espace QUINQ ou nombre espace QUATE
            case 34:
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 5 - spaces); // Supprime le QUINQ/QUATE et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 23: // nombre espace QUINQU
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 6 - spaces); // Supprime le QUINQU et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 24: // nombre espace QUINQUI
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 7 - spaces); // Supprime le QUINQUI et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 25: // nombre espace QUINQUIE
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 8 - spaces); // Supprime le QUINQUIE et les espaces

                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition();
                    res.add(rn);
                }
                break;
            case 26: // nombre espace QUINQUIES
                if (sb.length() > 0) {
                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition("QUINQUIES");
                    rn.definitIndexrepetition(repetitionindex);
                    res.add(rn);
                    sb.setLength(0);
                    sbnumero.setLength(0);
                }
                break;
            case 29: // nombre espace TER
                if (sb.length() > 0) {
                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition("TER");
                    rn.definitIndexrepetition(repetitionindex);
                    res.add(rn);
                    sb.setLength(0);
                    sbnumero.setLength(0);
                }
                break;
            case 35: // nombre espace QUATER
                if (sb.length() > 0) {
                    RefNumero rn = new RefNumero(sb.toString(), startindex, chaine, CategorieMot.NumeroAdresse);
                    rn.definitNumero(sbnumero.toString());
                    rn.definitRepetition("QUATER");
                    rn.definitIndexrepetition(repetitionindex);
                    res.add(rn);
                    sb.setLength(0);
                    sbnumero.setLength(0);
                }
                break;
        }

        return res;
    }

    /**
     * Retourne le type de la voie décrite dans la chaine spécifiée.<br>
     * Le type de voie peut suivre un ou plusieurs nombre et un sigle de répétition comme BIS TER QUATER QUINTER<br>
     * Si repetition est null, la répétition est d'abord recherchée.<br>
     * La recherche du type de voie suit alors la répétition dans la chaine.<br>
     * Si aucun type de voie n'est trouvé et que la répétition est 'R', alors cette répétition est assimil�e au type
     * de voie RUE. La répétition est alors remise à zéro.<br>
     * Dans le résultat, c'est le mot trouvé qui est référencé, et non pas son abbréviation.
     * NON OPTIMISE
     * @param chaine la chaine de ligne 4 normalisée et restructurée
     * @param les numéros préalablement cherchés.
     * @return
     */
    public RefTypeVoie trouveTypeVoie(String chaine, ArrayList<RefNumero> numeros) {
        if (chaine == null) {
            return new RefTypeVoie("", 0);
        }
        if (numeros == null) {
            numeros = trouveNumeros(chaine);
        }
        int index = 0;
        if (numeros.size() > 0) {
            RefNumero lastnumero = numeros.get(numeros.size() - 1);
            index = lastnumero.obtientIndex() + lastnumero.obtientMot().length();
        }

        RefTypeVoie res = new RefTypeVoie("", index);

        if (index < chaine.length()) {
            if (Character.isSpaceChar(chaine.charAt(index))) // Si la chaine est normalisée, il ne peut y avoir qu'un seul espace.
            {
                index++;
            }
            String souschaine = chaine.substring(index);

            Mot m = chercheDebutMot(chaine, index, CategorieMot.TypeDeVoie);
            Abbreviation a = chercheDebutAbbreviation(chaine, index, CategorieMot.TypeDeVoie);

            if (m != null) {
                if (a == null) {
                    res = new RefTypeVoie(m.obtientNom(), m, index, chaine, CategorieMot.TypeDeVoie);
                } else {
                    // en cas d'ambiguité, le plus long est retourné.
                    if (a.obtientNom().length() > m.obtientNom().length()) {
                        m = a.obtientMotPrefere(CategorieMot.TypeDeVoie);

                        res = new RefTypeVoie(a.obtientNom(), m, index, chaine, CategorieMot.TypeDeVoie);
                    } else {
                        res = new RefTypeVoie(m.obtientNom(), m, index, chaine, CategorieMot.TypeDeVoie);
                    }
                }
            } else if (a != null) {
                m = a.obtientMotPrefere(CategorieMot.TypeDeVoie);
                res = new RefTypeVoie(a.obtientNom(), m, index, chaine, CategorieMot.TypeDeVoie);
            }
        }

        // Si aucun type de voie n'est trouvé, 
        if (res.obtientMot().length() == 0) {
            if (numeros.size() >= 1) {
                RefNumero rn = numeros.get(numeros.size() - 1);

                // et que la répétition est 'R'
                if (rn.obtientRepetitionNormalise() == 'R') {
                    // et qu'il y a d'autres caractères après ce R,
                    if (Algos.localiseCaractereApresIndex(chaine, rn.obtientIndexRepetition())) {
                        // Elle est prise comme type de voie à la place.
                        res = new RefTypeVoie("R", obtientRue(), rn.obtientIndexRepetition(), chaine, CategorieMot.TypeDeVoie);
                        rn.definitRepetition();
                    }
                }
            }
        }

        return res;
    }

    /**
     * Trouve le code de département dans la chaine spécifiée.<br>
     * WA 09/2011 : si GestionCodesDepartements a ete initialise, cette extraction est de sa responsabilite
     * et le cache des codes departement est utilise.
     * Sinon, on utilise toujours l'algo originel :
     * Les deux premiers chiffres du premier groupe de chiffres de la chaine.
     * Un unique caractère le précédent est toléré.
     * @param chaine
     * @return
     */
    public RefCle trouveCodeDepartement(String chaine) {
        // L'ancien algorithme est remplace par une methode de GestionCOdesDepartement, si ce dernier a ete initialide
        GestionCodesDepartements gestCdDpt = GestionCodesDepartements.getInstance();
        if (gestCdDpt.isInitialized()) {
            return gestCdDpt.extractCodeDptFromString(chaine);
        } else // Sinon, on revient a l'ancien algo
        {
            StringBuilder sb = new StringBuilder();
            char c;
            int i = 0, startindex = 0;

            while (i < chaine.length() && Character.isSpaceChar(chaine.charAt(i))) // Au cas où...
            {
                i++;
            }
            startindex = i;

            // Un unique caractère est toléré
            if (i < chaine.length() && Character.isLetter(chaine.charAt(i))) {
                i++;
                while (i < chaine.length() && Character.isSpaceChar(chaine.charAt(i))) {
                    i++;
                }
                startindex = i;
            }

            while (i < chaine.length() && Character.isDigit(c = chaine.charAt(i))) {
                sb.append(c);
                i++;
            }

            if (i >= startindex + 2) {
                return new RefCle(sb.substring(0, 2), (Mot) null, startindex, chaine, CategorieMot.CodeDepartement);
            } else {
                return new RefCle("", 0);
            }
        }
    }

    /**
     * Trouve le code postal dans la chaine spécifiée.<br>
     * WA 09/2011 : si GestionCodesDepartements a ete initialise, cette extraction est de sa responsabilite
     * et le cache des codes departement est utilise.
     * Il s'agit du premier groupe de caractères qui doivent être des chiffres.
     * Toutefois, un unique caractère avant ces chiffres est toléré.
     * @param chaine une chaine de ligne 6 normalisée et restructurée.
     * @return
     */
    public RefCle trouveCodePostal(String chaine) {
        // L'ancien algorithme est remplace par une methode de GestionCOdesDepartement, si ce dernier a ete initialise
        GestionCodesDepartements gestCdDpt = GestionCodesDepartements.getInstance();
        if (gestCdDpt.isInitialized()) {
            return gestCdDpt.extractCodeDptOrCodePostalFromString(chaine);
        } else // Sinon, on revient a l'ancien algo
        {
            StringBuilder sb = new StringBuilder();
            char c;
            int i = 0, startindex = 0;

            while (i < chaine.length() && Character.isSpaceChar(chaine.charAt(i))) // Au cas où...
            {
                i++;
            }
            startindex = i;

            // Un unique caractère est toléré
            if (i < chaine.length() && Character.isLetter(chaine.charAt(i))) {
                i++;
                while (i < chaine.length() && Character.isSpaceChar(chaine.charAt(i))) {
                    i++;
                }
                startindex = i;
            }

            while (i < chaine.length() && Character.isDigit(c = chaine.charAt(i))) {
                sb.append(c);
                i++;
            }

            if (startindex != i) {
                return new RefCle(sb.toString(), (Mot) null, startindex, chaine, CategorieMot.CodePostal);
            }
            return new RefCle("", 0);
        }
    }

    /**
     * Retourne l'éventuel numéro accompagnant le cedex.
     * @param chaine
     * @param cedex
     * @return
     */
    public RefCle trouveNumeroCedex(String chaine, RefCle cedex) {
        if (cedex == null) {
            cedex = trouveNomVille(chaine, null);
        }
        int startindex = cedex.obtientIndex() + cedex.obtientMot().length();

        if (startindex < chaine.length() && Character.isSpaceChar(chaine.charAt(startindex))) {
            startindex++;
        }

        if (startindex < chaine.length()) {
            // Cherche la fin du mot
            int endindex = chaine.indexOf(" ", startindex);

            String numero = null;

            // Obtient le soit disant numéro
            if (endindex == -1) {
                numero = chaine.substring(startindex);
            } else {
                numero = chaine.substring(startindex, endindex);
            }
            // Vérifie s'il s'agit bien d'un nombre
            try {
                Integer.parseInt(numero);
                // Il s'agit bien d'un numéro.
                return new RefCle(numero, (Mot) null, startindex, chaine, CategorieMot.Numero);
            } catch (NumberFormatException e) {
            // Il ne s'agit pas d'un nombre
            }
        }
        return new RefCle("", startindex);
    }

    /**
     * Retourne la chaine cedex trouvée dans la chaine.<br>
     * Le cedex est éventuellement accompagné d'un numéro.
     * @param chaine une chaine de ligne 6 normalisée et restructurée.
     * @param nomVille
     * @return
     */
    public RefCle trouveCedex(String chaine, RefCle numeroArrondissement) {
        if (numeroArrondissement == null) {
            numeroArrondissement = trouveNumeroArrondissement(chaine, null);
        }
        int startindex = numeroArrondissement.obtientIndex() + numeroArrondissement.obtientMot().length();

        if (startindex < chaine.length() && Character.isSpaceChar(chaine.charAt(startindex))) {
            startindex++;
        }

        if (startindex < chaine.length()) {
            // Cherche la fin du mot
            int endindex = chaine.indexOf(" ", startindex);

            String lcedex = null;

            // Obtient le soit disant cedex
            if (endindex == -1) {
                lcedex = chaine.substring(startindex);
            } else {
                lcedex = chaine.substring(startindex, endindex);
            }

            // Vérifie s'il s'agit bien du mot clé Cedex
            if (obtientCedex().est(lcedex)) {
                return new RefCle(lcedex, obtientCedex(), startindex, chaine, CategorieMot.Cedex);
            }
        }

        return new RefCle("", startindex);
    }

    /**
     * Retourne le numéro d'arrondissement trouvé dans la chaine<br>
     * Le numéro d'arrondissement suit généralement le nom de la ville.
     * @param chaine une chaine de ligne 6 normalisée et restructurée.
     * @return
     */
    public RefCle trouveNumeroArrondissement(String chaine, RefCle nomVille) {
        if (nomVille == null) {
            nomVille = trouveNomVille(chaine, null);
        }
        int startindex = nomVille.obtientIndex() + nomVille.obtientMot().length();

        if (startindex < chaine.length() && Character.isSpaceChar(chaine.charAt(startindex))) {
            startindex++;
        }

        if (startindex < chaine.length()) {
            // Cherche la fin du mot
            int endindex = chaine.indexOf(" ", startindex);

            String numero = null;

            // Obtient le soit disant numéro
            if (endindex == -1) {
                numero = chaine.substring(startindex);
            } else {
                numero = chaine.substring(startindex, endindex);
            }
            // Vérifie s'il s'agit bien d'un nombre
            try {
                Integer.parseInt(numero);
                // Il s'agit bien d'un numéro.
                return new RefCle(numero, (Mot) null, startindex, chaine, CategorieMot.Numero);
            } catch (NumberFormatException e) {
            // Il ne s'agit pas d'un nombre
            }
        }
        return new RefCle("", startindex);
    }

    /**
     * Retourne le nom de la ville trouvé dans la chaine.<br>
     * Le nom de la ville suit le code postal, et précède un numéro ou une mention CEDEX.<br>
     * Si le code postal est null, cherche le code Postal avec trouveCodePostal.<br>
     * @param chaine une chaine de ligne 6 normalisée et restructurée.
     * @param codePostal
     * @return
     */
    public RefCle trouveNomVille(String chaine, RefCle codePostal) {
        boolean first = true;

        if (codePostal == null) {
            codePostal = trouveCodePostal(chaine);
        }
        int i = codePostal.obtientIndex() + codePostal.obtientMot().length();

        StringBuilder nomville = new StringBuilder();
        StringBuilder currentword = new StringBuilder();

        // Passe l'espace suivant le code postal
        if (i < chaine.length() && Character.isSpaceChar(chaine.charAt(i))) {
            i++;
        }

        int startindex = i;

        // Recherche tous les groupes de mots qui suivent le code Postal, jusqu'à rencontrer un nombre ou une mention cedex.
        while (i < chaine.length()) {
            char c;
            // Cherche le premier mot (jusqu'un espace).
            while (i < chaine.length() && !Character.isSpaceChar(c = chaine.charAt(i))) {
                currentword.append(c);
                i++;
            }

            String currentString = currentword.toString();
            if (currentString.length() > 0) {
                // Vérifie s'il s'agit d'un cedex               
                if (obtientCedex().est(currentString)) {
                    // Dans ce cas, arrête la boucle.
                    break;
                }
                // Vérifie s'il s'agit d'un nombre
                try {
                    Integer.parseInt(currentString);
                    // Il s'agit d'un nombre, arrête la boucle.
                    break;
                } catch (NumberFormatException nfe) {
                // Il ne s'agit pas d'un nombre
                }
                // Il ne s'agit ni d'un cedex, ni d'un nombre
                // C'est qu'il fait partie du nom de ville
                if (!first) {
                    nomville.append(" ");
                } else {
                    first = false;
                }
                nomville.append(currentword);
            }

            currentword.setLength(0);

            // Passe l' espace
            if (i < chaine.length() && Character.isSpaceChar(chaine.charAt(i))) {
                i++;
            }
        }

        if (nomville.length() > 0) {
            return new RefCommune(nomville.toString(), (Mot) null, startindex, chaine, CategorieMot.Ville, -1);
        } else {
            return new RefCle("", codePostal.obtientIndex());
        }
    }

    /**
     * Retourne vrai si la chaine est un article.<br>
     * Les articles suivants sont reconnus:
     * <ul><li>DE LA</li>
     *     <li>DE L</li>
     *     <li>DE</li>
     *     <li>DU</li>
     *     <li>DES</li>
     *     <li>D</li>
     *     <li>AU</li>
     *     <li>A LA</li>
     *     <li>A</li>
     *     <li>EN</li>
     *     <li>ET</li>
     *     <li>OU</li></ul>
     * méthode OPTIMISE en O(Min(chaine.length,Constante<5)).
     * @param chaine Une chaine normalisée
     * @return
     */
    public boolean estArticle(String chaine, int index) {
        int state = 0;
        // 0 DE LA, DE L, DE, DU, DES, D, AU, A LA, A, EN, ET, OU, LA, LE, LES
        // 1 DE LA, DE L, DE, DU, DES, D
        // 2 AU, A LA, A
        // 3 DE LA, DE L, DE, DES
        // 4 DE LA, DE L
        // 5 EN, ET
        // 6 LA, LE, LES
        // 7 LE, LES
        int i = index;

        while (i < chaine.length()) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case 'D':
                            state = 1;
                            break;
                        case 'A':
                            state = 2;
                            break;
                        case 'E':
                            state = 5;
                            break;
                        case 'O':
                            if (chaine.length() == 2) {
                                if (chaine.charAt(1) == 'U') {
                                    return true;
                                }
                            }
                            return false;
                        case 'L':
                            state = 6;
                            break;
                        default:
                            return false;
                    }
                    break;
                case 1:
                    switch (c) {
                        case 'E':
                            state = 3;
                            break;
                        case 'U':
                            if (chaine.length() == 2) // DU
                            {
                                return true;
                            } else {
                                return false;
                            }
                        default:
                            return false;
                    }
                    break;
                case 2:
                    switch (c) {
                        case 'U':
                            if (chaine.length() == 2) // AU
                            {
                                return true;
                            } else {
                                return false;
                            }
                        case ' ':
                            if (chaine.length() == 4) {
                                if (chaine.charAt(2) == 'L' && chaine.charAt(3) == 'A') // A LA
                                {
                                    return true;
                                }
                            }
                            return false;
                        default:
                            return false;
                    }
                case 3:
                    switch (c) {
                        case ' ':
                            state = 4;
                            break;
                        case 'S':
                            if (chaine.length() == 3) // DES
                            {
                                return true;
                            } else {
                                return false;
                            }
                        default:
                            return false;
                    }
                    break;
                case 4:
                    switch (c) {
                        case 'A':
                            if (chaine.length() == 5) // DE LA
                            {
                                return true;
                            } else {
                                return false;
                            }
                        default:
                            return false;
                    }
                case 5:
                    switch (c) {
                        case 'N':
                            if (chaine.length() == 2) // EN
                            {
                                return true;
                            } else {
                                return false;
                            }
                        case 'T':
                            if (chaine.length() == 2) // ET
                            {
                                return true;
                            } else {
                                return false;
                            }
                        default:
                            return false;
                    }
                case 6:
                    switch (c) {
                        case 'A':
                            if (chaine.length() == 2) // LA
                            {
                                return true;
                            } else {
                                return false;
                            }
                        case 'E':
                            state = 7;
                            break;
                        default:
                            return false;
                    }
                    break;
                case 7:
                    switch (c) {
                        case 'S':
                            if (chaine.length() == 3) // LES
                            {
                                return true;
                            } else {
                                return false;
                            }
                        default:
                            return false;
                    }
            }
            i++;
        }
        switch (state) {
            case 1:
                return true; // D

            case 2:
                return true; // A

            case 3:
                return true; // DE

            case 4:
                return true; // DE L

            case 7:
                return true; // LE

            default:
                return false;
        }
    }

    /**
     * Retourne la date trouvée dans la chaine spécifiée.
     * Si le libelle est null, il est recherché à l'aide de trouveLibelle.
     * @param chaine chaine de ligne 4 normalisée et restructurée.
     * @param libelle le libelle dans lequel se trouve la date.
     * @return
     */
    public ArrayList<RefDate> trouveDates(String chaine, RefCle libelle) {
        ArrayList<RefDate> dates = new ArrayList<RefDate>();
        if (libelle == null) {
            libelle = trouveLibelleVoie(chaine, (RefCle) null);
        }
        if (libelle.obtientMot().length() > 0) {
            GestionDate gd = new GestionDate();

            RefDate date = gd.trouveDate(chaine, libelle.obtientIndex());

            while (date.obtientMot().length() > 0) {
                dates.add(date);
                date = gd.trouveDate(chaine, date.obtientIndex() + date.obtientMot().length());
            }
        }
        return dates;
    }

    /**
     * Retourne l'article de la voie décrite dans la chaine spécifiée.<br>
     * Si typevoie n'est pas spécifié (null), typevoie est recherché avec trouveTypeVoie.<br>
     * Les articles trouvés sont:
     * <ul>
     *     <li>DE LA</li>
     *     <li>DE L</li>
     *     <li>DE</li>
     *     <li>DU</li>
     *     <li>DES</li>
     *     <li>AUX</li>
     *     <li>AU</li>
     *     <li>A LA</li>
     *     <li>A</li>
     *     <li>D</li>
     * </ul>
     * Ces 'articles' ne sont trouvés que s'ils sont suivis d'un espace.
     * @param chaine la chaine de ligne 4 normalisée et restructurée
     * @param typevoie le type de voie trouvé au préalable.
     * @return un résultat partiel avec index si l'article est trouvé.
     */
    public RefCle trouveArticleVoie(String chaine, RefCle typevoie) {
        if (chaine == null) {
            return new RefCle("", 0);


        }
        if (typevoie == null) {
            typevoie = trouveTypeVoie(chaine, null);
        }

        int index = typevoie.obtientIndex() + typevoie.obtientMot().length();

        // Si le type de voie prend toute la chaine,
        if (index == chaine.length()) {
            return new RefCle("", index);
        }

        if (Character.isSpaceChar(chaine.charAt(index))) // Si la chaine est normalisée, il ne peut y avoir qu'un seul espace.
        {
            index++;
        }
        if (index >= chaine.length()) {
            return new RefCle("", index);
        }

        // Sinon, cherche l'article.
        chaine = chaine.substring(index);

        if (chaine.startsWith("DE LA ")) {
            chaine = "DE LA";
        } else if (chaine.startsWith("DE L ")) {
            chaine = "DE L";
        } else if (chaine.startsWith("DE ")) {
            chaine = "DE";
        } else if (chaine.startsWith("DU ")) {
            chaine = "DU";
        } else if (chaine.startsWith("DES ")) {
            chaine = "DES";
        } else if (chaine.startsWith("AUX ")) {
            chaine = "AUX";
        } else if (chaine.startsWith("AU ")) {
            chaine = "AU";
        } else if (chaine.startsWith("A LA ")) {
            chaine = "A LA";
        } else if (chaine.startsWith("A ")) {
            chaine = "A";
        } else if (chaine.startsWith("D ")) {
            chaine = "D";
        } else {
            return new RefCle("", index);
        }
        return new RefCle(chaine, (Mot) null, index, chaine, CategorieMot.Article);
    }

    /**
     * Retourne le libellé de la voie décrite par la chaine spécifiée
     * lorsque la voie ne contient pas de type de voie.<br>
     * Si numeros est à null, les numéros ne sont pas cherchés, mais ignorés.
     * L'article doit être vide dans ce cas.
     * @param chaine La chaine à examiner
     * @param numeros Les numéros trouvés dans la chaine.
     * @return
     */
    public RefCle trouveLibelleVoie(String chaine, ArrayList<RefNumero> numeros) {
        int index = 0;
        if (numeros != null && numeros.size() > 0) {
            RefNumero lastnumero = numeros.get(numeros.size() - 1);
            index = lastnumero.obtientIndex() + lastnumero.obtientMot().length();
        }

        if (index < chaine.length()) {
            if (Character.isSpaceChar(chaine.charAt(index))) // Si la chaine est normalisée, il ne peut y avoir qu'un seul espace.
            {
                index++;
            }

            if (index < chaine.length()) {
                return new RefCle(chaine.substring(index), index);

            } else {
                return new RefCle("", index);

            }
        } else {
            return new RefCle("", index);

        }
    }

    /**
     * Retourne le libellé de la voie décrite dans la chaine spécifiée.<br>
     * Si article n'est pas spécifié, le type de voie et l'article sont recherchés avec trouveTypeVoie et trouveArticle.
     * @param chaine la chaine de ligne 4 normalisée et restructurée
     * @param article l'article trouvé au préalable.
     * @return
     */
    public RefCle trouveLibelleVoie(String chaine, RefCle article) {
        if (chaine == null) {
            return new RefCle("", 0);


        }
        if (article == null) {
            RefCle typevoie = trouveTypeVoie(chaine, null);
            if (typevoie.obtientMot().length() != 0) {
                article = trouveArticleVoie(chaine, typevoie);

            } else {
                article = new RefCle("", 0);

            }
        }

        int index = article.obtientIndex() + article.obtientMot().length();

        if (index >= chaine.length()) {
            return new RefCle("", index);
        }
        while (index < chaine.length() && Character.isSpaceChar(chaine.charAt(index))) {
            index++;
        }
        chaine = chaine.substring(index);

        return new RefCle(chaine, index);
    }

    /**
     * Cherche les mots qui font parti de la voie.<br>
     * Les objets trouvés sont tels qu'ils sont entourés d'espaces ou en début ou en fin de chaine.<br>
     * @param categorie La catégorie des objets à chercher
     * @param categorieAmbiguite La catégorie d'ambiguité générée
     * @param nomVoie le nom de la voie dans laquelle chercher
     * @param mots la liste des mots trouvés
     * @param ambiguite et leurs ambiguités.
     */
    public ArrayList<RefCle> chercheAbbreviationsDansChaine(String chaine, CategorieMot categorie) {
        ArrayList<RefCle> res = new ArrayList<RefCle>();

        int state = 0;

        // Pour chaque nouvelle lettre,
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            // Détermine s'il s'agit d'un début de mot.
            switch (state) {
                case 0:
                    switch (c) {
                        case '\t':
                        case ' ':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }

            // Commence une nouvelle recherche binaire
            if (state == 1) {
                Abbreviation a = chercheDebutAbbreviation(chaine, i, categorie);

                if (a != null) {
                    int endindex = i + a.obtientNom().length();
                    if (endindex == chaine.length() || chaine.charAt(endindex) == ' ' || chaine.charAt(endindex) == '\t') {
                        RefCle ref = new RefCle(a.obtientNom(), a, i, chaine, categorie);
                        res.add(ref);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Cherche les mots qui font parti de la chaine.<br>
     * Les objets trouvés sont tels qu'ils sont entourés d'espaces ou en début ou en fin de chaine.<br>
     * Les abbréviations de ces mots ne sont pas cherchées.
     * @param categorie La catégorie des objets à chercher
     * @param categorieAmbiguite La catégorie d'ambiguité générée
     * @param nomVoie le nom de la voie dans laquelle chercher
     * @param mots la liste des mots trouvés
     * @param ambiguite et leurs ambiguités.
     */
    public void chercheMotsDansChaine(CategorieMot categorie, CategorieAmbiguite categorieAmbiguite, String chaine,
            ArrayList<Mot> mots, ArrayList<CategorieAmbiguite> ambiguite) {
        int state = 0;

        // Pour chaque lettre,
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            switch (state) {
                case 0:
                    switch (c) {
                        case '\t':
                        case ' ':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }

            // Commence une nouvelle recherche binaire
            if (state == 1) {
                Mot m = chercheDebutMot(chaine, i, categorie);

                if (m != null) {
                    int endindex = i + m.obtientNom().length();
                    if (endindex == chaine.length() || chaine.charAt(endindex) == ' ' || chaine.charAt(endindex) == '\t') {
                        mots.add(m);
                        ambiguite.add(categorieAmbiguite);
                    }
                }
            }
        }
    }

    /**
     * Cherche tous les mots séparés par des espaces dans la chaine.<br>
     * La catégorie CategorieMot.Autre leur est attribuée.
     * @param chaineOriginale la chaine qui est utilisée pour crée les objets RefCle retournés.
     * @return
     */
    public ArrayList<RefCle> chercheAutresMotsDansChaine(String chaine, String chaineOriginale) {
        ArrayList<RefCle> lmots = new ArrayList<RefCle>();
        int state = 0;
        StringBuilder sb = new StringBuilder();
        int startindex = 0;
        // Pour chaque lettre,
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                            break;
                        default:
                            sb.setLength(0);
                            sb.append(c);
                            state = 1;
                            startindex = i;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case ' ':
                            Mot m = new Mot(sb.toString());
                            m.ajouteCategorie(CategorieMot.Autre);
                            RefCle cle = new RefCle(sb.toString(), m, startindex, chaineOriginale, CategorieMot.Autre);
                            lmots.add(cle);
                            state = 0;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
            }
        }
        if (state == 1) {
            Mot m = new Mot(sb.toString());
            m.ajouteCategorie(CategorieMot.Autre);
            RefCle cle = new RefCle(sb.toString(), m, startindex, chaineOriginale, CategorieMot.Autre);
            lmots.add(cle);
        }

        return lmots;
    }

    /**
     * Cherche les mots qui font parti de la voie.<br>
     * Les objets trouvés sont tels qu'ils sont entourés d'espaces ou en début ou en fin de chaine.<br>
     * Les abbréviations de ces mots ne sont pas cherchées.
     * @param categorie La catégorie des objets à chercher
     * @param categorieAmbiguite La catégorie d'ambiguité générée
     * @param nomVoie le nom de la voie dans laquelle chercher
     * @param mots la liste des mots trouvés
     * @param ambiguite et leurs ambiguités.
     */
    public ArrayList<RefCle> chercheMotsDansChaine(String chaine, CategorieMot categorie) {
        ArrayList<RefCle> mots = new ArrayList<RefCle>();
        int state = 0;

        // Pour chaque lettre,
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            switch (state) {
                case 0:
                    switch (c) {
                        case '\t':
                        case ' ':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case '\t':
                        case ' ':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }

            // Commence une nouvelle recherche binaire
            if (state == 1) {
                Mot m = chercheDebutMot(chaine, i, categorie);

                if (m != null) {
                    int endindex = i + m.obtientNom().length();
                    if (endindex == chaine.length() || chaine.charAt(endindex) == ' ' || chaine.charAt(endindex) == '\t') {
                        RefCle ref = new RefCle(m.obtientNom(), m, i, chaine, categorie);
                        mots.add(ref);
                    }
                }
            }
        }
        return mots;
    }

    /**
     * Cherche les nombres présents dans la chaine spécifiée.
     * @param chaine
     * @return
     */
    public void chercheNombresDansChaine(String chaine, ArrayList<Mot> mots, ArrayList<CategorieAmbiguite> ambiguites) {
        StringBuilder sb = new StringBuilder();
        int state = 0;
        int startindex = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            switch (state) {
                case 0:
                    if (Character.isDigit(c)) {
                        sb.setLength(0);
                        sb.append(c);
                        state = 1;
                        startindex = i;
                    }
                    break;
                case 1:
                    if (!Character.isDigit(c)) {
                        state = 0;
                        Mot m = new Mot(sb.toString());
                        mots.add(m);
                        ambiguites.add(CategorieAmbiguite.NombreDansVoie);
                    } else {
                        sb.append(c);

                    }
                    break;
            }
        }
        if (state == 1) {
            Mot m = new Mot(sb.toString());
            mots.add(m);
            ambiguites.add(CategorieAmbiguite.NombreDansVoie);
        }
    }

    /**
     * Cherche les nombres présents dans la chaine spécifiée.
     * @param chaine
     * @return
     */
    public ArrayList<RefCle> chercheNombresDansChaine(String chaine) {
        ArrayList<RefCle> refcles = new ArrayList<RefCle>();
        StringBuilder sb = new StringBuilder();
        int state = 0;
        int startindex = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            switch (state) {
                case 0:
                    if (Character.isDigit(c)) {
                        sb.setLength(0);
                        sb.append(c);
                        state = 1;
                        startindex = i;
                    }
                    break;
                case 1:
                    if (!Character.isDigit(c)) {
                        state = 0;
                        Mot m = new Mot(sb.toString());
                        RefCle ref = new RefCle(sb.toString(), m, startindex, chaine, CategorieMot.Article.Numero);
                        refcles.add(ref);
                    } else {
                        sb.append(c);

                    }
                    break;
            }
        }
        if (state == 1) {
            Mot m = new Mot(sb.toString());
            RefCle ref = new RefCle(sb.toString(), m, startindex, chaine, CategorieMot.Article.Numero);
            refcles.add(ref);
        }

        return refcles;
    }

    /**
     * Cherche les lexemes de la chaine qui peuvent etre des codes postaux, des codes departement ou des numeros.
     * On cherche en priorite les motifs de Codes departements.
     * Puis les motifs de codes postaux.
     * Et enfin les motifs de simples nombres.
     * @param chaine
     * @return
     */
    public List<RefCle> chercheCodesDptOuCodesPostauxOuNumeroDansChaine(String chaine) {
        ArrayList<RefCle> result = new ArrayList<RefCle>();

        // Les codes postaux, puis les codes dept puis les nombres
        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
        Pattern dptPattern = gestDpt.getDptOrCpOrNumberPattern();
        int dptGroup = gestDpt.getDptGroupIntoCPOrDptOrNumberPattern();
        int cpGroup = gestDpt.getCpGroupIntoCPOrDptOrNumberPattern();
        Matcher match = dptPattern.matcher(chaine);
        String group;
        RefCle ref;
        while (match.find()) {
            group = match.group();
            Mot m = new Mot(group);

            String cp = match.group(cpGroup);
            String dpt = match.group(dptGroup);
            if (cp != null) // Si cela matche un code postal
            {
                ref = new RefCle(group, m, match.start(), chaine, CategorieMot.CodePostal);
            } else if (dpt != null) // Si cela matche un code dpt
            {
                ref = new RefCle(group, m, match.start(), chaine, CategorieMot.CodeDepartement);
            } else // Un simple nombre
            {
                ref = new RefCle(group, m, match.start(), chaine, CategorieMot.Article.Numero);
            }
            result.add(ref);
        }
        return result;
    }

    /**
     * Indique si le contenu de la chaine peut être considéré à la fois comme une répétition et comme un article.
     * 
     * Les cas connus sont les lettres A D L O et S.
     * 
     * @param chaine
     * @return
     */
    public boolean estRepetitionEtArticle(String chaine) {
        if (chaine == null || chaine.length() > 1) {
            return false;

        }
        switch (chaine.charAt(0)) {
            default:
                return false;
            case 'D':
            case 'L':
            case 'O':
            case 'S':
            case 'A':
                return true;
        }
    }

    /**
     * Cherche un petit mot (voir cherchePetitsMotsDansChaine) à l'index spécifié.<br>
     * Les mots cherchés sont:
     * <ul>
     * <li>Lettre seule</li>
     * <li>A</li>
     * <li>AU</li>
     * <li>AUX</li>
     * <li>BIS</li>
     * <li>D</li>
     * <li>DE</li>
     * <li>DES</li>
     * <li>DU</li>
     * <li>E</li>
     * <li>ER</li>
     * <li>ET</li>
     * <li>EME</li>
     * <li>LE</li>
     * <li>LA</li>
     * <li>LES</li>
     * <li>OU</li>
     * <li>PAR</li>
     * <li>QUINTER</li>
     * <li>QUINQUIES</li>
     * <li>SE</li>
     * <li>SUR</li>
     * <li>TER</li>
     * </ul>
     * 
     * A noter que A D L O et S sont retournés comme étant des articles alors qu'ils peuvent
     * aussi être considérés comme des répétitions.
     * 
     * @return null si rien n'a été trouvé.
     */
    public RefCle cherchePetitMot(String chaine, int index) {
        int length = chaine.length();
        char c;
        switch (c = chaine.charAt(index)) {
            default: // Lettre seule
                if (Character.isLetter(c) && (length == index + 1 || chaine.charAt(index + 1) == ' ')) {
                    Mot m = new Mot(Character.toString(c));
                    return new RefCle("" + c, m, index, chaine, CategorieMot.Repetition);
                } else {
                    return null;

                }
            case 'A': // A AU AUX
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("A");
                    return new RefCle("A", m, index, chaine, CategorieMot.Article);
                } else {
                    if (length > index) {
                        if (chaine.charAt(index + 1) == 'U') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("AU");
                                return new RefCle("AU", m, index, chaine, CategorieMot.Article);
                            } else if (length > index + 1) {
                                if (chaine.charAt(index + 2) == 'X') {
                                    if (length == index + 3 || chaine.charAt(index + 3) == ' ') {
                                        Mot m = new Mot("AUX");
                                        return new RefCle("AUX", m, index, chaine, CategorieMot.Article);
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }
            case 'B': // B BIS
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("B");
                    return new RefCle("B", m, index, chaine, CategorieMot.Repetition);
                } else {
                    if (length > index + 2) {
                        if (chaine.startsWith("IS", index + 1) && (length == index + 3 || chaine.charAt(index + 3) == ' ')) {
                            Mot m = new Mot("BIS");
                            return new RefCle("BIS", m, index, chaine, CategorieMot.Repetition);
                        }
                    }
                    return null;
                }
            case 'D': // D DE DES DU
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("D");
                    return new RefCle("D", m, index, chaine, CategorieMot.Article);
                } else {
                    if (length > index) {
                        if (chaine.charAt(index + 1) == 'E') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("DE");
                                return new RefCle("DE", m, index, chaine, CategorieMot.Article);
                            } else if (length > index + 1) {
                                if (chaine.charAt(index + 2) == 'S') {
                                    if (length == index + 3 || chaine.charAt(index + 3) == ' ') {
                                        Mot m = new Mot("DES");
                                        return new RefCle("DES", m, index, chaine, CategorieMot.Article);
                                    }
                                }
                            }
                        } else if (chaine.charAt(index + 1) == 'U') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("DU");
                                return new RefCle("DU", m, index, chaine, CategorieMot.Article);
                            }
                        }
                    }
                    return null;
                }
            case 'E': // E ER ET EME
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("E");
                    return new RefCle("E", m, index, chaine, CategorieMot.Repetition);
                } else {
                    if (length > index) {
                        if (chaine.charAt(index + 1) == 'M') {
                            if (length > index + 2 && chaine.charAt(index + 2) == 'E' && (length == index + 3 || chaine.charAt(index + 3) == ' ')) {
                                Mot m = new Mot("EME");
                                return new RefCle("EME", m, index, chaine, CategorieMot.Eme);
                            }
                        } else if (chaine.charAt(index + 1) == 'R') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("ER");
                                return new RefCle("ER", m, index, chaine, CategorieMot.Eme);
                            }
                        } else if (chaine.charAt(index + 1) == 'T') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("ET");
                                return new RefCle("ET", m, index, chaine, CategorieMot.Article);
                            }
                        }
                    }
                    return null;
                }
            case 'L': // L LE LA LES
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("L");
                    return new RefCle("L", m, index, chaine, CategorieMot.Article);
                } else {
                    if (length > index) {
                        if (chaine.charAt(index + 1) == 'E') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("LE");
                                return new RefCle("LE", m, index, chaine, CategorieMot.Article);
                            } else if (length > index + 1) {
                                if (chaine.charAt(index + 2) == 'S') {
                                    if (length == index + 3 || chaine.charAt(index + 3) == ' ') {
                                        Mot m = new Mot("LES");
                                        return new RefCle("LES", m, index, chaine, CategorieMot.Article);
                                    }
                                }
                            }
                        } else if (chaine.charAt(index + 1) == 'A') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("LA");
                                return new RefCle("LA", m, index, chaine, CategorieMot.Article);
                            }
                        }
                    }
                    return null;
                }
            case 'O': // O OU
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("O");
                    return new RefCle("O", m, index, chaine, CategorieMot.Article);
                } else {
                    if (length > index) {
                        if (chaine.charAt(index + 1) == 'U' && (length == index + 2 || chaine.charAt(index + 2) == ' ')) {
                            Mot m = new Mot("OU");
                            return new RefCle("OU", m, index, chaine, CategorieMot.Article);
                        }
                    }
                    return null;
                }
            case 'P': // P PAR
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("P");
                    return new RefCle("P", m, index, chaine, CategorieMot.Repetition);
                } else {
                    if (length > index + 2) {
                        if (chaine.startsWith("AR", index + 1) && (length == index + 3 || chaine.charAt(index + 3) == ' ')) {
                            Mot m = new Mot("PAR");
                            return new RefCle("PAR", m, index, chaine, CategorieMot.Repetition);
                        }
                    }
                    return null;
                }
            case 'Q': // Q QUATER QUINQUIES
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("Q");
                    return new RefCle("Q", m, index, chaine, CategorieMot.Repetition);
                } else {
                    if (length > index + 5) {
                        if (chaine.charAt(index + 1) == 'U') {
                            if (chaine.charAt(index + 2) == 'A') {
                                if (chaine.startsWith("TER", index + 3) && (length == index + 6 || chaine.charAt(index + 6) == ' ')) {
                                    Mot m = new Mot("QUATER");
                                    return new RefCle("QUATER", m, index, chaine, CategorieMot.Repetition);
                                }
                            } else if (chaine.charAt(index + 2) == 'I') {
                                if (chaine.startsWith("NQUIES", index + 3) && (length == index + 9 || chaine.charAt(index + 9) == ' ')) {
                                    Mot m = new Mot("QUINQUIES");
                                    return new RefCle("QUINQUIES", m, index, chaine, CategorieMot.Repetition);
                                }
                            }
                        }
                    }
                    return null;
                }
            case 'S': // S SE SUR
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("S");
                    return new RefCle("S", m, index, chaine, CategorieMot.Article);
                } else {
                    if (length > index) {
                        if (chaine.charAt(index + 1) == 'U') {
                            if (length > index + 2 && chaine.charAt(index + 2) == 'R' && (length == index + 3 || chaine.charAt(index + 3) == ' ')) {
                                Mot m = new Mot("SUR");
                                return new RefCle("SUR", m, index, chaine, CategorieMot.Article);
                            }
                        } else if (chaine.charAt(index + 1) == 'E') {
                            if (length == index + 2 || chaine.charAt(index + 2) == ' ') {
                                Mot m = new Mot("SE");
                                return new RefCle("SE", m, index, chaine, CategorieMot.Article);
                            }
                        }
                    }
                    return null;
                }
            case 'T': // T TER
                if (length == index + 1 || chaine.charAt(index + 1) == ' ') {
                    Mot m = new Mot("T");
                    return new RefCle("T", m, index, chaine, CategorieMot.Repetition);
                } else {
                    if (length > index + 2) {
                        if (chaine.startsWith("ER", index + 1) && (length == index + 3 || chaine.charAt(index + 3) == ' ')) {
                            Mot m = new Mot("TER");
                            return new RefCle("TER", m, index, chaine, CategorieMot.Repetition);
                        }
                    }
                    return null;
                }
        }
    }

    /**
     * Cherche les articles, les répétitions et les èmes.<br>
     * Ils ne sont recherchés que s'ils sont entourés d'espaces ou en début ou en fin de chaine.<br>
     * Les articles cherchés sont:
     * <ul>
     *     <li>a Mais attention, il peut s'agir d'une répétition.</li>
     *     <li>au</li>
     *     <li>aux</li>
     *     <li>d</li>
     *     <li>de</li>
     *     <li>des</li>
     *     <li>du</li>
     *     <li>et</li>
     *     <li>l Mais attention, il peut s'agir d'une répétition.</li>
     *     <li>la</li>
     *     <li>le</li>
     *     <li>les</li>
     *     <li>ou</li>
     *     <li>par</li>
     *     <li>s Mais attention, il peut s'agir d'une répétition.</li>
     *     <li>se</li>
     *     <li>sur</li>
     * </ul>
     * Les répétitions cherchées sont les lettres seules ainsi que:
     * <ul>
     *   <li>BIS</li>
     *   <li>TER</li>
     *   <li>QUATER</li>
     *   <li>QUINQUIES</li>
     * </ul>
     * Les èmes cherchés sont:
     * <ul>
     *   <li>ER</li>
     *   <li>EME</li>
     * </ul>
     * 
     * Attention, les articles suivants ne sont pas trouvés par cette méthode mais doivent
     * être renseignés dans le fichier abbreviations.xml en tant que mot et abbréviation pour
     * être reconnu et correctement traités :
     * <ul>
     * <li>ET</li>
     * </ul>
     * @return
     */
    public ArrayList<RefCle> cherchePetitsMotsDansChaine(String chaine) {
        ArrayList<RefCle> refcles = new ArrayList<RefCle>();

        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                            break;
                        default:
                            RefCle ref = cherchePetitMot(chaine, i);
                            if (ref != null) {
                                refcles.add(ref);

                            }
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case ' ':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

        return refcles;
    }

    /**
     * Retourne les termes ambigus et leurs catégories trouvées dans une voie.<br>
     * <ul><li>Les clés</li>
     *     <li>Les titres</li></ul>
     * Les termes trouvés sont tels qu'ils sont entourés d'espace ou en début ou en fin de chaine.
     * @param nomCommune
     * @return
     */
    public ResultatAmbiguite chercheSiCommuneAmbigue(String nomCommune) {
        ArrayList<Mot> am = new ArrayList<Mot>();
        ArrayList<CategorieAmbiguite> aca = new ArrayList<CategorieAmbiguite>();

        chercheMotsDansChaine(CategorieMot.Cle, CategorieAmbiguite.CleDansCommune, nomCommune, am, aca);
        chercheMotsDansChaine(CategorieMot.TypeDeVoie, CategorieAmbiguite.TypeDeVoieDansCommune, nomCommune, am, aca);
        chercheMotsDansChaine(CategorieMot.Titre, CategorieAmbiguite.TitreDansCommune, nomCommune, am, aca);

        ResultatAmbiguite ra = null;

        try {
            ra = new ResultatAmbiguite(am, aca);
        } catch (GestionMotsException gme) {
        // Ne devrait pas arriver.
        }

        return ra;
    }

    /**
     * Retourne les termes ambigus et leurs catégories trouvées dans une voie.<br>
     * Les ambiguités recherchées sont:
     * <ul><li>Les clés</li>
     *     <li>Les titres</li>
     *     <li>Les types de voie inclus dans la voie</li>
     *     <li>Les numéros</li>
     *     <li>Le type de voie est aussi une clé</li></ul>
     * Les termes trouvés sont tels qu'ils sont entourés d'espace ou en début ou en fin de chaine.
     * J'ai en effet constaté que toutes les abbréviations ont potentiellement plusieurs mots d'origine. 
     * Répertorier tous ces mots ne permettrait donc pas de gagner du temps.
     */
    public ResultatAmbiguite chercheSiVoieAmbigue(String nomVoie) {
        ArrayList<Mot> am = new ArrayList<Mot>();
        ArrayList<CategorieAmbiguite> aca = new ArrayList<CategorieAmbiguite>();

        // Extrait le type de voie.
        RefCle typedevoie = trouveTypeVoie(nomVoie, null);
        String nomSansTypeDeVoie = nomVoie.substring(typedevoie.obtientIndex() + typedevoie.obtientMot().length());

        chercheMotsDansChaine(CategorieMot.Cle, CategorieAmbiguite.CleDansVoie, nomSansTypeDeVoie, am, aca);
        chercheMotsDansChaine(CategorieMot.Titre, CategorieAmbiguite.TitreDansVoie, nomSansTypeDeVoie, am, aca);
        chercheMotsDansChaine(CategorieMot.TypeDeVoie, CategorieAmbiguite.TypeDeVoieDansVoie, nomSansTypeDeVoie, am, aca);
        chercheNombresDansChaine(nomSansTypeDeVoie, am, aca);

        // Si le type de voie de la voie est aussi une clé,
        Mot m = typedevoie.obtientCle();
        if (m != null) {
            if (m.estDeLaCategorie(CategorieMot.Cle)) {
                am.add(typedevoie.obtientCle());
                aca.add(CategorieAmbiguite.CleEstTypeDeVoie);
            }

            // Si le type de voie est aussi un titre
            if (m.estDeLaCategorie(CategorieMot.Titre)) {
                am.add(typedevoie.obtientCle());
                aca.add(CategorieAmbiguite.TitreEstTypeDeVoie);
            }
        }

        ResultatAmbiguite ra = null;

        try {
            ra = new ResultatAmbiguite(am, aca);
        } catch (GestionMotsException gme) {
        // Ne devrait pas arriver.
        }

        return ra;
    }

    /**
     * Charge les types ou les titres.
     * @param element l'élément qui doit être examiné.
     * @param nom "type" ou "titre".
     * @param categorie CategorieMot.TypeDeVoie ou CategorieMot.Titre
     * @param mots la table qui doit être remplie avec les mots.
     * @param abbreviations la table qui doit être remplie avec les abbreviations.
     */
    private void loadSubAbbreviation(Element element, Hashtable mots, Hashtable abbreviations) throws GestionMotsException {
        List childrens = element.getChildren();
        for (int i = 0; i < childrens.size(); i++) {
            Element e = (Element) childrens.get(i);
            // S'il s'agit d'une abbreviation
            if (e.getName().compareTo("abbreviation") == 0) {
                Element enom = e.getChild("nom");
                Element eid = e.getChild("id");
                Element erefid = e.getChild("refid");

                if (enom == null) {
                    throw (new GestionMotsException("La balise nom n'a pas été trouvé dans une abbréviation .",
                            GestionMotsException.BALISENONTROUVEE));
                }
                if (eid == null) {
                    throw (new GestionMotsException("La balise id n'a pas été trouvé dans une abbréviation .",
                            GestionMotsException.BALISENONTROUVEE));
                }
                if (erefid == null) {
                    throw (new GestionMotsException("La balise refid n'a pas été trouvé dans une abbréviation .",
                            GestionMotsException.BALISENONTROUVEE));
                }
                int id;
                try {
                    id = Integer.parseInt(eid.getText());
                } catch (NumberFormatException nfe) {
                    throw (new GestionMotsException(
                            "La balise id de l'abbréviation " + enom.getText() + " a pour valeur " + eid.getText() + ".",
                            GestionMotsException.BALISEERRONNEE));
                }

                Abbreviation a = new Abbreviation(enom.getText());

                // Pour chaque référence à un mot, 
                List liste_ids = erefid.getChildren();
                for (int j = 0; j < liste_ids.size(); j++) {
                    Element eid2 = (Element) liste_ids.get(j); // la référence à un mot

                    if (eid2.getName().compareTo("id") != 0) {
                        throw (new GestionMotsException(
                                "La balise refid de l'abbréviation " + enom.getText() + " contient une balise de nom " + eid2.getName() + ".",
                                GestionMotsException.BALISEERRONNEE));
                    }
                    int id2;

                    try {
                        id2 = Integer.parseInt(eid2.getText());
                    } catch (NumberFormatException nfe) {
                        throw (new GestionMotsException(
                                "Le contenu d'un refid de l'abbréviation " + enom.getText() + " a pour valeur " + eid2.getText() + ".",
                                GestionMotsException.BALISEERRONNEE));
                    }

                    // Détermine la priorité du mot
                    if (eid2.getAttribute("priorite") != null) {
                        int priorite;
                        try {
                            priorite = eid2.getAttribute("priorite").getIntValue();
                        } catch (DataConversionException dce) {
                            throw (new GestionMotsException("L'attribut priorite de l'id " + id2 + " de l'abbréviation " + enom.getText() + " a pour valeur " + eid2.getAttribute(
                                    "prefere").getValue() + ".", GestionMotsException.BALISEERRONNEE));
                        }
                        a.ajouteMotIndex(id2, priorite);
                    } else {
                        a.ajouteMotIndex(id2);
                    }
                }

                if (abbreviations.get(new Integer(id)) != null) {
                    throw (new GestionMotsException("Le pointeur " + id + " a été Utilisé plusieurs fois.",
                            GestionMotsException.BALISEERRONNEE));
                }
                abbreviations.put(new Integer(id), a);
            } // ou S'il s'agit d'un mot
            else if (e.getName().compareTo("mot") == 0) {
                Element enom = e.getChild("nom");
                Element eid = e.getChild("id");
                Element enormid = e.getChild("normid");
                Element eabbrid = e.getChild("abbrid");
                Element ecategories = e.getChild("categories");

                if (enom == null) {
                    throw (new GestionMotsException("La balise nom n'a pas été trouvé dans un mot.",
                            GestionMotsException.BALISENONTROUVEE));
                }
                if (eid == null) {
                    throw (new GestionMotsException("La balise id n'a pas été trouvé dans un mot.",
                            GestionMotsException.BALISENONTROUVEE));
                }
                if (ecategories == null) {
                    throw (new GestionMotsException("La balise categories n'a pas été trouvé dans un mot.",
                            GestionMotsException.BALISENONTROUVEE));
                }
                int id;
                try {
                    id = Integer.parseInt(eid.getText());
                } catch (NumberFormatException nfe) {
                    throw (new GestionMotsException(
                            "La balise id du mot " + enom.getText() + " a pour valeur " + eid.getText() + ".",
                            GestionMotsException.BALISEERRONNEE));
                }

                Mot m = new Mot(enom.getText());

                List categories = ecategories.getChildren();
                for (int j = 0; j < categories.size(); j++) {
                    Element ecategorie = (Element) categories.get(j);
                    if (ecategorie.getName().compareTo("categorie") != 0) {
                        throw (new GestionMotsException(
                                "la balise abbrid du mot " + enom.getText() + " contient une balise " + ecategorie.getName() + ".",
                                GestionMotsException.BALISEERRONNEE));
                    }
                    CategorieMot cm = CategorieMot.Autre;
                    try {
                        cm = CategorieMot.valueOf(ecategorie.getText().trim());
                    }catch(Exception ex){
                        DynamicEnum.addEnum(CategorieMot.class, ecategorie.getText().trim());
                        cm = CategorieMot.valueOf(ecategorie.getText().trim());
                    } /*catch (NumberFormatException nfe) {
                        throw (new GestionMotsException(
                                "La balise categories du mot " + enom.getText() + " contient une balise categorie de valeur " + ecategorie.getText() + ".",
                                GestionMotsException.BALISEERRONNEE));
                    }*/

                    m.ajouteCategorie(cm);
                }

                if (enormid != null) {
                    int normid;
                    try {
                        normid = Integer.parseInt(enormid.getText());
                    } catch (NumberFormatException nfe) {
                        throw (new GestionMotsException(
                                "La balise normid du mot " + enom.getText() + " a pour valeur " + enormid.getText() + ".",
                                GestionMotsException.BALISEERRONNEE));
                    }
                    m.ajouteAbbreviationIndex(normid, true);
                }

                if (eabbrid != null) {
                    List abbrids = eabbrid.getChildren();
                    for (int j = 0; j < abbrids.size(); j++) {
                        Element abbrid = (Element) abbrids.get(j);
                        if (abbrid.getName().compareTo("id") != 0) {
                            throw (new GestionMotsException(
                                    "la balise abbrid du mot " + enom.getText() + " contient une balise " + abbrid.getName() + ".",
                                    GestionMotsException.BALISEERRONNEE));
                        }
                        int abbid = -1;
                        try {
                            abbid = Integer.parseInt(abbrid.getText());
                        } catch (NumberFormatException nfe) {
                            throw (new GestionMotsException(
                                    "La balise abbrid du mot " + enom.getText() + " contient une balise id de valeur " + abbrid.getText() + ".",
                                    GestionMotsException.BALISEERRONNEE));
                        }
                        m.ajouteAbbreviationIndex(abbid, false);
                    }
                }

                if (mots.get(new Integer(id)) != null) {
                    throw (new GestionMotsException("Le pointeur " + id + " a été Utilisé plusieurs fois.",
                            GestionMotsException.BALISEERRONNEE));
                }
                mots.put(new Integer(id), m);
            } else {
                throw (new GestionMotsException("Erreur dans le fichier xml : une balise " + e.getName() + " a été trouvée.",
                        GestionMotsException.BALISEERRONNEE));
            }
        }
    }

    /**
     * Ajoute les abbreviations trouvées en effectuant le lien entre mot et abbreviations.
     */
    private void ajouteAbbreviations(Hashtable<Integer, Abbreviation> abbreviations, Hashtable<Integer, Mot> mots) throws
            GestionMotsException {
        // Examine les abbréviations
        Enumeration abbreviationsKeys = abbreviations.keys();
        while (abbreviationsKeys.hasMoreElements()) {
            Integer i = (Integer) abbreviationsKeys.nextElement();
            Abbreviation a = abbreviations.get(i);

            // Effectue le lien avec chaque mot
            for (int j = 0; j < a.obtientCompteMotIndex(); j++) {
                int index = a.obtientMotIndex(j);

                Mot m = mots.get(new Integer(index));
                if (m == null) {
                    throw (new GestionMotsException(
                            "Le mot " + a.obtientMotIndex(j) + " référencé dans l'abbréviation " + a.obtientNom() + " est incorrect.",
                            GestionMotsException.BALISEERRONNEE));
                }
                a.ajouteMot(m);
            }

            // Une fois les mots liés, les ressources peuvent être libérées
            a.finInitialisation();

            ajouteAbbreviation(a);
        }

        // Examine les mots
        Enumeration motsKeys = mots.keys();
        while (motsKeys.hasMoreElements()) {
            Integer i = (Integer) motsKeys.nextElement();
            Mot m = mots.get(i);

            // Effectue le lien avec chaque abbréviation
            for (int j = 0; j < m.obtientCompteAbbreviationIndex(); j++) {
                Abbreviation a = abbreviations.get(new Integer(m.obtientAbbreviationIndex(j)));
                if (a == null) {
                    throw (new GestionMotsException(
                            "L'abbréviation " + m.obtientAbbreviationIndex(j) + " référencé dans l'abbréviation " + m.obtientNom() + " est incorrect.",
                            GestionMotsException.BALISEERRONNEE));
                }
                m.ajouteAbbreviation(a, m.estTempOfficielle(j));
            }

            ManagementFactory.getMemoryPoolMXBeans();

            // Une fois les mots li�s, les ressources peuvent être lib�r�es.
            m.finInitialisation();

            ajouteMot(m);
        }
    }

    /**
     * D�charge les abbréviations, clés, et prénoms.
     */
    public void dechargeFichiers() {
        // Casse les liens des abbreviations et des mots avant de supprimer leurs r�f�rences
        // pour qu'ils soit trait�s par le garbage collector.
        for (int i = 0; i < abbreviations.size(); i++) {
            abbreviations.get(i).clear();
        }
        for (int i = 0; i < mots.size(); i++) {
            mots.get(i).clear();
        }
        abbreviations.clear();
        mots.clear();
        rue = null;
    }

    /**
     * Sauvegarde au format xml les types de voies, les titres, les clés, et leurs abbréviations.
     * @param fichier
     */
    public void saveAbbreviation(String fichier) throws IOException, GestionMotsException {
        Hashtable<Abbreviation, Integer> labbreviations = new Hashtable<Abbreviation, Integer>();
        Hashtable<Mot, Integer> lmots = new Hashtable<Mot, Integer>();
        File f = new File(fichier);

        Element racine = new Element("abbreviations");

        int index = 0; // permet de donner à chaque mot et chaque abbréviation un identifiant différent.

        // Commence par répertorier tous les mots.
        for (int i = 0; i < mots.size(); i++) {
            Mot m = mots.get(i);
            if (m.estDeLaCategorie(CategorieMot.Titre) ||
                    m.estDeLaCategorie(CategorieMot.TypeDeVoie) ||
                    m.estDeLaCategorie(CategorieMot.Cle)) {
                lmots.put(m, new Integer(index++));
            }
        }

        // Traite les abbréviations.
        for (int i = 0; i < abbreviations.size(); i++) {
            Abbreviation a = abbreviations.get(i);
            try {
                if (a.estAbbreviationDeCategorie(CategorieMot.Titre) ||
                        a.estAbbreviationDeCategorie(CategorieMot.TypeDeVoie) ||
                        a.estAbbreviationDeCategorie(CategorieMot.Cle)) {
                    labbreviations.put(a, new Integer(index)); // l'index est incrémenté plus loin.

                    Element abbr = new Element("abbreviation");

                    Element nom = new Element("nom");
                    nom.addContent(a.obtientNom());

                    Element id = new Element("id");
                    id.addContent(Integer.toString(index++)); // incrémenté ici.

                    Element refid = new Element("refid");

                    for (int j = 0; j < a.obtientCompteMot(); j++) {
                        Mot m = a.obtientMot(j);
                        if (m.estDeLaCategorie(CategorieMot.Titre) ||
                                m.estDeLaCategorie(CategorieMot.TypeDeVoie) ||
                                m.estDeLaCategorie(CategorieMot.Cle)) {
                            Element subid = new Element("id");

                            Integer mid = lmots.get(m); // Récupére l'identifiant du mot précalculé lors de la première phase.

                            if (mid != null) {
                                subid.addContent(mid.toString());

                                subid.setAttribute("priorite", Integer.toString(
                                        a.obtientPrioriteMot(j)));
                                refid.addContent(subid);
                            } else {
                                throw (new GestionMotsException(
                                        "L'abbréviation " + a.obtientNom() + " a un mot orphelin : " + m.obtientNom(),
                                        GestionMotsException.ORPHELINS));
                            }
                        }
                    }
                    abbr.addContent(nom);
                    abbr.addContent(id);
                    abbr.addContent(refid);
                    racine.addContent(abbr);
                }
            } catch (NullPointerException npe) {
                throw (new GestionMotsException("L'abbréviation " + a.obtientNom() + " a un problème.", GestionMotsException.ORPHELINS));
            }
        }

        // Traite les noms
        for (int i = 0; i < mots.size(); i++) {
            Mot m = mots.get(i);
            try {
                if (m.estDeLaCategorie(CategorieMot.Titre) ||
                        m.estDeLaCategorie(CategorieMot.TypeDeVoie) ||
                        m.estDeLaCategorie(CategorieMot.Cle)) {
                    int identifiant = lmots.get(m).intValue(); // récupère l'identifiant

                    Element mot = new Element("mot");

                    Element nom = new Element("nom");
                    nom.addContent(m.obtientNom());
                    Element id = new Element("id");
                    id.addContent(Integer.toString(identifiant));
                    Element normid = null;
                    Abbreviation abbroff = m.obtientAbbreviationOfficielle();
                    if (abbroff != null) {
                        normid = new Element("normid");
                        normid.addContent(labbreviations.get(abbroff).toString());
                    }

                    Element refid = new Element("abbrid");
                    for (int j = 0; j < m.obtientCompteAbbreviation(); j++) {
                        if (!m.estOfficielle(j)) {
                            Abbreviation a = m.obtientAbbreviation(j);
                            Element subid = new Element("id");
                            subid.addContent(labbreviations.get(a).toString());
                            refid.addContent(subid);
                        }
                    }

                    Element categories = new Element("categories");
                    for (int j = 0; j < m.obtientCompteCategorie(); j++) {
                        Element categorie = new Element("categorie");
                        categorie.addContent(m.obtientCategorie(j).toString());
                        categories.addContent(categorie);
                    }

                    mot.addContent(nom);
                    mot.addContent(id);
                    if (normid != null) {
                        mot.addContent(normid);
                    }
                    mot.addContent(refid);
                    mot.addContent(categories);
                    racine.addContent(mot);
                }
            } catch (NullPointerException npe) {
                throw (new GestionMotsException("Le mot " + m.obtientNom() + " a un problème.", GestionMotsException.ORPHELINS));
            }
        }

        Document d = new Document(racine);
        XMLOutputter outputter = new XMLOutputter();
        FileWriter writer = new FileWriter(f, false);
        outputter.output(d, writer);
    }

    /**
     * Initialise les rÃ©fÃ©rences de mots :
     * <ul><li>abbreviation</li>
     *     <li>cles</li>
     *     <li>prenoms</li></ul><br>
     * DÃ©charge ces rÃ©fÃ©rences au prÃ©alable.
     */
    public boolean initMots(String abbreviations,
            String cles,
            String prenoms) {
        boolean error = false;
        dechargeFichiers();

        try {
            loadAbbreviation(abbreviations);
        } catch (Exception e) {
            error = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE, "Problème durant le chargement des abbréviations", e);
        }
        try {
            loadCles(cles);
        } catch (Exception e) {
            error = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE, "Problème durant le chargement des clés", e);
        }
        try {
            loadPrenoms(prenoms);
        } catch (Exception e) {
            error = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE, "Problème durant le chargement des prénoms", e);
        }
        Logger.getLogger("JDONREF").log(Level.INFO,
                "Fichiers chargés. " + obtientCompteMots() + " mots trouvés. " + obtientCompteAbbreviation() + " abbréviations trouvées.");
        return !error;
    }

    /**
     * Charge le fichier décrivant les abbréviations.<br>
     * Il s'agit du premier fichier à charger car il contient des liens entre les mots et les abbréviations.
     * @throws GestionMotsException le fichier n'a pas été trouvé.
     * @throws JDOMException erreur dans le fichier xml.
     * @throws IOException erreur lors d'un traitement sur le fichier.
     */
    public void loadAbbreviation(String fichier) throws GestionMotsException, JDOMException, IOException {
        Hashtable<Integer, Abbreviation> labbreviations = new Hashtable<Integer, Abbreviation>();
        Hashtable<Integer, Mot> lmots = new Hashtable<Integer, Mot>();

        File f = new File(fichier);

        if (!f.exists()) {
            throw (new GestionMotsException("Le fichier " + fichier + " n'a pas été trouvé.",
                    GestionMotsException.FICHIERNONTROUVE));
        }

        // Charge le fichier xml
        SAXBuilder builder = new SAXBuilder();
        Document d = builder.build(f);
        Element racine = d.getRootElement();

        if (racine == null || racine.getName().compareTo("abbreviations") != 0) {
            throw (new GestionMotsException("Erreur dans le fichier xml : la racine abbreviations n'a pas été trouvée.",
                    GestionMotsException.BALISENONTROUVEE));
        }

        // Traite les types et les titres.
        loadSubAbbreviation(racine, lmots, labbreviations);
        ajouteAbbreviations(labbreviations, lmots);
    }

    /**
     * Cherche le mot qui débute la chaine.
     * @param chaine
     * @return null si aucun mot n'est trouvé.
     */
    public Abbreviation chercheDebutAbbreviation(String chaine, int index, CategorieMot categorie) {
        Abbreviation a = (Abbreviation) arbre_abbreviations.cherche(chaine, index, true);

        if (a == null) {
            return null;

        }
        if (a.estAbbreviationDeCategorie(categorie)) {
            return a;

        }
        return null;
    }

    /**
     * Cherche si une abbréviation est enregistrée.
     * @param mot le mot a chercher.
     * @return null si aucune abbréviation ne correspond.
     */
    public Abbreviation chercheAbbreviation(String abbreviation) {
        Abbreviation a = (Abbreviation) arbre_abbreviations.cherche(abbreviation, 0, true);
        if (a == null) {
            return null;

        }
        if (a.obtientNom().length() != abbreviation.length()) {
            return null; // l'abbréviation trouvé peut commencer la chaine

        }
        return a;
    }

    /**
     * Cherche si une abbréviation est enregistrée.
     * @param mot le mot a chercher.
     * @return null si aucune abbréviation ne correspond.
     */
    public Abbreviation chercheAbbreviationPourRoutage(String abbreviation) {
        Abbreviation a = (Abbreviation) arbre_abbreviations.cherche(abbreviation, 0, true);
        if (a == null) {
            return null;

        }

        return a;
    }

    /**
     * Cherche si une abbréviation officielle est enregist�e.
     * @param mot le mot a chercher.
     * @return null si aucune abbréviation ne correspond.
     */
    public Abbreviation chercheAbbreviationOfficielle(String abbreviation) {
        Abbreviation a = chercheAbbreviation(abbreviation);
        if (a != null && a.estOfficielle()) {
            return a;
        }
        return null;
    }

    /**
     * Charge les clés des lignes de l'adresse.<br>
     * Ce fichier ne doit pas être chargé le premier (suivant loadAbbreviations).
     * @param fichier le fichier xml.
     */
    public void loadCles(String fichier) throws GestionMotsException, JDOMException, IOException {
        File f = new File(fichier);

        if (!f.exists()) {
            throw (new GestionMotsException("Le fichier " + fichier + " n'a pas été trouvé.",
                    GestionMotsException.FICHIERNONTROUVE));
        }

        // Charge le fichier xml
        SAXBuilder builder = new SAXBuilder();
        Document d = builder.build(f);
        Element racine = d.getRootElement();

        if (racine == null || racine.getName().compareTo("repartition") != 0) {
            throw (new GestionMotsException(
                    "Erreur dans le fichier " + fichier + " : la racine repartition n'a pas été trouvée.",
                    GestionMotsException.BALISENONTROUVEE));
        }

        List lignes = racine.getChildren();

        for (int i = 0; i < lignes.size(); i++) {
            Element ligne = (Element) lignes.get(i);
            if (ligne.getName().compareTo("ligne") != 0) {
                throw (new GestionMotsException(
                        "Erreur dans le fichier " + fichier + " : une balise " + ligne.getName() + " a été trouvée.",
                        GestionMotsException.BALISEERRONNEE));
            }

            Attribute a = ligne.getAttribute("index");
            if (a == null) {
                throw (new GestionMotsException("Erreur dans le fichier " + fichier + " : une balise ligne n'a pas d'index.",
                        GestionMotsException.BALISEERRONNEE));
            }

            int index = -1;
            try {
                index = Integer.parseInt(a.getValue());
            } catch (NumberFormatException nfe) {
                throw (new GestionMotsException(
                        "Erreur dans le fichier " + fichier + " : une balise ligne a pour index " + a.getValue(),
                        GestionMotsException.BALISEERRONNEE));
            }

            List elements = ligne.getChildren();
            for (int j = 0; j < elements.size(); j++) {
                Element element = (Element) elements.get(j);
                if (element.getName().compareTo("element") != 0) {
                    throw (new GestionMotsException(
                            "Erreur dans le fichier " + fichier + " : une balise " + element.getName() + " a été trouvé dans la ligne ",
                            GestionMotsException.BALISEERRONNEE));
                }

                Mot m = chercheMot(element.getText());
                if (m == null) {
                    m = new Mot(element.getText());
                    ajouteMot(m);
                }
                if (!m.estDeLaCategorie(CategorieMot.Cle)) {
                    m.ajouteCategorie(CategorieMot.Cle);
                }
                m.ajouteLigne(index);
            }
        }
    }

    /**
     * Charge le fichier de prénoms.<br>
     * A chaque ligne doit correspondre un prénom.<br>
     * Les prénoms sont format�s :
     * <ul><li>en majuscule</li>
     *     <li>sans accents</li>
     *     <li>sans ponctuation</li>
     *     <li>sans espaces superflus</li></ul>
     * Les caractères suivant un / sont ignor�s.<br>
     * Ce fichier ne doit pas être charg� le premier (suivant loadAbbreviations).
     */
    public void loadPrenoms(String fichier) throws FileNotFoundException, IOException, GestionMotsException {
        BufferedReader reader = new BufferedReader(new FileReader(fichier));
        String line = null;

        while ((line = reader.readLine()) != null) {
            line = Algos.supprimeAccentRepetitionEspacePonctuation(line);

            Mot m = chercheMot(line);
            if (m == null) {
                m = new Mot(line);
                ajouteMot(m);
            }
            if (!m.estDeLaCategorie(CategorieMot.Prenom)) {
                m.ajouteCategorie(CategorieMot.Prenom);
            }
        }

        reader.close();
    }

    /**
     * Cherche les éléments clés dans la chaine spécifiée.<br>
     * A savoir : 
     * <ul>
     * <li>les articles</li>
     * <li>Les clés et leurs abbréviations</li>
     * <li>Les nombres</li>
     * <li>les émes</li>
     * <li>les répétitions</li>
     * <li>les types de voies et leurs abbréviations</li>
     * <li>les communes</li>
     * </ul>
     * @param chaine
     * @return les éléments trouvés triés par rapport à leur index.
     */
    private ArrayList<RefCle> chercheElements(String chaine) throws SQLException {
        // Cherche chaque catégorie d'élément.
        ArrayList<RefCle> elements = new ArrayList<RefCle>();

        elements.addAll(cherchePetitsMotsDansChaine(chaine));
        elements.addAll(chercheAbbreviationsDansChaine(chaine, CategorieMot.Article));
        elements.addAll(chercheAbbreviationsDansChaine(chaine, CategorieMot.Cle));
        elements.addAll(chercheMotsDansChaine(chaine, CategorieMot.Cle));
        // WA 09/2011 Ajout de chercheCodesDptOuCodesPostauxOuNumeroDansChaine : estampille tous les codes dpts potentiels comme codes dpts,
        // les CP comme des CP, et les numeros comme des numeros
        elements.addAll(chercheCodesDptOuCodesPostauxOuNumeroDansChaine(chaine));
//        elements.addAll(chercheNombresDansChaine(chaine));
        elements.addAll(chercheAbbreviationsDansChaine(chaine, CategorieMot.TypeDeVoie));
        elements.addAll(chercheMotsDansChaine(chaine, CategorieMot.TypeDeVoie));

        // Produit la chaine des éléments restants
        StringBuilder sb = new StringBuilder(chaine);
        for (int i = 0; i < elements.size(); i++) {
            RefCle cle = elements.get(i);
            int index = cle.obtientIndex();
            masque(sb, index, index + cle.obtientMot().length());
        }

        // Les éléments restant sont recherchés
        elements.addAll(chercheAutresMotsDansChaine(sb.toString(), chaine));

        // Les éléments trouvés sont ensuite triés par rapport à leur index.
        // (tri bulle)
        ArrayList<RefCle> elementstries = new ArrayList<RefCle>();

        for (int i = 0; i < elements.size(); i++) {
            int j = 0;
            RefCle cle = elements.get(i);
            int index = cle.obtientIndex();
            for (; j < i; j++) {
                if (elementstries.get(j).obtientIndex() > index) {
                    break;
                }
            }
            elementstries.add(j, cle);
        }

        return elementstries;
    }

    /**
     * Obtient l'indice du premier élément précédent l'élément spécifié dans la chaine.<br>
     * Définitions:
     * <ul>
     *   <li>La liste elements est issues de la méthode chercheElements.</li>
     *   <li>L'indice est l'indice dans la liste.</li>
     *   <li>L'index est l'index dans la chaine.</li>
     * </ul>
     * Comme il est possible que plusieurs éléments aient le même index dans la liste elements,
     * cette méthode permet d'obtenir le premier élément trouvé dans la liste tel que son index
     * soit inférieur à l'élément spécifié par son index.<br>
     * D'autres éléments peuvent suivre celui-ci avec le même index.
     * @param elements
     * @param indice
     * @return -1 si aucun élément ne précéde l'indice spécifié.
     */
    private int indiceElementPrecedent(ArrayList<RefCle> elements, int indice) {
        int index = elements.get(indice).obtientIndex();

        // Passe les éléments ne même index.
        do {
            indice--;
        } while (indice >= 0 && elements.get(indice).obtientIndex() == index);

        if (indice == -1) {
            return -1;


        }
        int lastindice;
        index = elements.get(indice).obtientIndex();

        // Cherche le premier élément d'index inférieur.
        do {
            lastindice = indice;
            indice--;
        } while (indice >= 0 && elements.get(indice).obtientIndex() == index);

        return lastindice;
    }

    /**
     * Obtient l'indice de l'élément suivant l'élément spécifié dans la chaine.<br>
     * Voir la méthode indiceElementPrecedent pour les définitions.
     * @param elements
     * @param indice
     * @return -1 si aucun élément ne précéde l'indice spécifié.
     */
    private int indiceElementSuivant(ArrayList<RefCle> elements, int indice) {
        int index = elements.get(indice).obtientIndex();
        int size = elements.get(indice).obtientNomDeCle().length();

        // Passe les éléments ne même index.
        do {
            indice++;
        } while (indice < elements.size() && elements.get(indice).obtientIndex() < index + size);

        if (indice == elements.size()) {
            return -1;


        }
        return indice;
    }

    /**
     * Chercher parmi les éléments de même index si l'un d'eux est un article ou d'une autre catégorie spécifiée.
     * @param elements liste triée par index d'élément.
     * @param indice premier élément d'une suite d'élément de même index
     * @param categorieAutre l'autre catégorie.
     * @return 0 si ni un article ni un type de voie n'est trouvé, 1 si un article est trouvé, 2 si l'autre catégorie est trouvé.
     */
    private int trouveArticleOuAutre(ArrayList<RefCle> elements, int indice, CategorieMot categorieAutre) {
        int index = elements.get(indice).obtientIndex();
        do {
            CategorieMot categorie = elements.get(indice).obtientCategorieMot();
            if (categorie == categorieAutre) {
                return 2;
            }
            if (categorie == CategorieMot.Article) {
                return 1;
            }
            indice++;
        } while (indice < elements.size() && elements.get(indice).obtientIndex() == index);
        return 0;
    }

    /**
     * Trouve le type de voie de même index que l'élément d'indice spécifié (voir indiceElementPrecedent pour les définitions des termes).<br>
     * L'élément trouvé est tel que la catégorie pour laquelle il a été trouvé est type de voie.
     * @param elements
     * @param indice
     * @return -1 si aucune element type de voie de même index n'est trouvé.
     */
    private int trouveElementDeCategorie(ArrayList<RefCle> elements, int indice, CategorieMot categorie) {
        int index = elements.get(indice).obtientIndex();
        // Cherche le premier élément de même index
        int lastindice;
        do {
            lastindice = indice;
            indice--;
        } while (indice >= 0 && elements.get(indice).obtientIndex() == index);

        // Cherche parmi les éléments de même index un élément de la catégorie spécifiée
        indice = lastindice;
        do {
            RefCle element = elements.get(indice);
            if (element.obtientCategorieMot() == categorie) {
                return indice;
            }
            indice++;
        } while (indice < elements.size() && elements.get(indice).obtientIndex() == index);
        return -1;
    }

    /**
     * Trouve la lettre A C D N E ou la chaine RN de même index que l'élément d'indice spécifié (voir indiceElementPrecedent pour les définitions des termes).<br>
     * @param elements
     * @param indice
     * @return -1 si aucune element A C D N E ou la chaine RN de même index n'est trouvé.
     */
    private int trouveElementACDNERN(ArrayList<RefCle> elements, int indice) {
        int index = elements.get(indice).obtientIndex();
        // Cherche le premier élément de même index
        int lastindice;
        do {
            lastindice = indice;
            indice--;
        } while (indice >= 0 && elements.get(indice).obtientIndex() == index);

        // Cherche parmi les éléments de même index un élément de la catégorie spécifiée
        indice = lastindice;
        do {
            RefCle element = elements.get(indice);
            String mot = element.obtientMot();
            if (mot.length() == 1) {
                char c = mot.charAt(0);
                switch (c) {
                    case 'A':
                    case 'C':
                    case 'D':
                    case 'N':
                    case 'E':
                        return indice;
                }
            } else if (mot.length() == 2 && mot.compareTo("RN") == 0) {
                return indice;

            }
            indice++;
        } while (indice < elements.size() && elements.get(indice).obtientIndex() == index);
        return -1;
    }

    /**
     * Permet d'isoler le contexte d'un élément dans une chaine.<br>
     * Le contexte d'un élément est composé:
     * <ul><li>Du terme qui le précède autre que des articles</li>
     * <li>De lui même</li>
     * <li>Du terme qui le suit, autre que des articles</li>
     * </ul>
     * @param indice l'indice de l'élément dont le contexte est à trouver dans la chaine.
     * @return
     */
    private String obtientContexte(ArrayList<RefCle> elements, int indice, boolean avant, boolean apres) {
        StringBuilder res = new StringBuilder();

        int index = elements.get(indice).obtientIndex();
        // Commence par chercher le contexte avant.
        if (indice != 0 && avant) {
            // trouve l'élément d'index inférieur le plus proche dans la liste.
            int indiceprecedent = indice;
            do {
                indiceprecedent--;
            } while (indiceprecedent > 0 && elements.get(indiceprecedent).obtientIndex() == index);

            if (elements.get(indiceprecedent).obtientIndex() != index) {
                // Cherche alors un élément qui n'est pas un article
                CategorieMot categorie = elements.get(indiceprecedent).obtientCategorieMot();
                while (indiceprecedent >= 0 && categorie == CategorieMot.Article) {
                    indiceprecedent--;
                    if (indiceprecedent >= 0) {
                        categorie = elements.get(indiceprecedent).obtientCategorieMot();

                    }
                }
                if (indiceprecedent >= 0) {
                    res.append(elements.get(indiceprecedent).obtientNomDeCle());
                }
            }
        }

        Algos.appendWithSpace(res, elements.get(indice).obtientNomDeCle(), true);

        // Puis cherche le contexte après.
        if (indice != elements.size() - 1 && apres) {
            int size = elements.get(indice).obtientNomDeCle().length();
            // trouve l'élément d'index supérieur le plus proche dans la liste.
            int indicesuivant = indice;
            do {
                indicesuivant++;
            } while (indicesuivant < elements.size() - 1 && elements.get(indicesuivant).obtientIndex() < index + size);
            if (elements.get(indicesuivant).obtientIndex() >= index + size) {
                // Cherche alors un élément qui n'est pas un article
                CategorieMot categorie = elements.get(indicesuivant).obtientCategorieMot();
                while (indicesuivant < elements.size() && categorie == CategorieMot.Article) {
                    indicesuivant++;
                    if (indicesuivant < elements.size()) {
                        categorie = elements.get(indicesuivant).obtientCategorieMot();

                    }
                }
                if (indicesuivant < elements.size()) {
                    Algos.appendWithSpace(res, elements.get(indicesuivant).obtientNomDeCle(), true);

                }
            }
        }

        // Concatène le tout.
        return res.toString();
    }

    /**
     * Permet de savoir si l'élément est précédé par plusieurs articles et un élément d'une autre catégorie.
     * @param elements
     * @param indiceprecedent_0 l'indice de l'élément qui précédé l'élément.
     * @param autrecategorie l'autre catégorie à rechercher.
     * @return
     */
    private boolean estPrecedeDe(ArrayList<RefCle> elements, int indiceprecedent_0, CategorieMot autrecategorie) {
        if (indiceprecedent_0 != -1) {
            int indiceprecedent = indiceprecedent_0;

            // Cherche si un ou des articles précédent le type de voie,
            int trouve, lastindiceprecedent = -1;
            do {
                lastindiceprecedent = indiceprecedent;
                trouve = trouveArticleOuAutre(elements, indiceprecedent, autrecategorie);
                if (trouve == 1) {
                    indiceprecedent = indiceElementPrecedent(elements, indiceprecedent);
                }
            } while (indiceprecedent != -1 && trouve == 1);
            // Si un ou des articles est trouvé,
            if (trouve == 1) {
                // Cherche si l'autre catégorie le précéde
                indiceprecedent = indiceElementPrecedent(elements, lastindiceprecedent);
                if (indiceprecedent != -1) {
                    trouve = trouveArticleOuAutre(elements, indiceprecedent, autrecategorie);
                }
            }
            // Si l'autre catégorie est trouvée
            if (trouve == 2) {
                if (autrecategorie == CategorieMot.Cle) {
                    // S'il s'agit d'une clé, il faut vérifier qu'elle n'est pas précédée d'un ème.
                    indiceprecedent = indiceElementPrecedent(elements, indiceprecedent);
                    if (indiceprecedent != -1 && trouveElementDeCategorie(elements, indiceprecedent, CategorieMot.Eme) != -1) {
                        return false;
                    }
                }

                // C'est qu'il s'agit d'un complément de cette catégorie
                return true;
            }
        }
        return false;
    }

    /**
     * Compte le nombre d'élément de la catégorie spécifiée.
     * @param elements
     * @param categorie
     * @return
     */
    private int compteCategorie(ArrayList<ArrayList<RefCle>> elements, CategorieMot categorie) {
        int count = 0;
        for (int i = 0; i < elements.size(); i++) {
            ArrayList<RefCle> elementi = elements.get(i);
            for (int j = 0; j < elementi.size(); j++) {
                if (elementi.get(j).obtientCategorieMot() == categorie) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Résouds les doublons de types de voies :
     * <ul>
     * <li>Si l'un d'eux est précédé d'une clé ou d'un type de voie, il est oublié.</li>
     * <li>Les type de voie "non officiels" sont supprimés (sauf s'ils sont tous "non officiels" 
     * auquel cas seul le dernier est conservé).</li>
     * <li>Si l'un d'eux n'est suivi de rien, il est supprimé.</li>
     * <li>Si un unique est précédé d'un numéro non précédé d'une clé ou d'un ème, il est conservé.</li>
     * <li>Sinon, le dernier est conservé</li>
     * </ul>
     * @param elements
     * @param count le nombre d'éléments type de voie trouvés au total parmi les éléments.
     */
    private void resoudAmbiguitesDoublonsTypeDeVoie(ArrayList<ArrayList<RefCle>> elements, int count) {
        // Si l'un d'eux est précédé d'une clé ou d'un type de voie, il est oublié.
        for (int i = 0; (i < elements.size()) && count > 1; i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 1; j++) {
                RefCle elementj = elementsi.get(j);
                if (elementj.obtientCategorieMot() == CategorieMot.TypeDeVoie) {

                    int indiceElementPrecedent = indiceElementPrecedent(elementsi, j);
                    if (indiceElementPrecedent != -1) {

                        // précédé par une clé (non précédée par ème)
                        if (estPrecedeeParCleNonPrecedeeParEme(elementsi, indiceElementPrecedent)) {
                            elementj.definitCategorieMot(CategorieMot.Cle);
                            // les éléments libelle qui le suivent doivent aussi être modifié en Autre
                            // pour la répartition des compléments qui devrait suivre.
                            for (int k = j + 1; k < elementsi.size(); k++) {
                                RefCle elementk = elementsi.get(k);
                                CategorieMot categoriek = elementk.obtientCategorieMot();
                                if (categoriek == CategorieMot.Article) {
                                    continue;

                                }
                                if (categoriek == CategorieMot.Libelle) {
                                    elementk.definitCategorieMot(CategorieMot.Autre);

                                } else {
                                    break;

                                }
                            }
                            count--;
                        } // précédé par un type de voie
                        else if (trouveElementDeCategorie(elementsi, indiceElementPrecedent, CategorieMot.TypeDeVoie) != -1) {
                            elementj.definitCategorieMot(CategorieMot.Libelle);
                            count--;
                        }
                    }
                }
            }
        }

        // les types de voies qui ne sont suivis de rien sont ignorés
        for (int i = 0; count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            if (elementsi.size() > 0) {
                RefCle elementj = elementsi.get(elementsi.size() - 1);
                if (elementj.obtientCategorieMot() == CategorieMot.TypeDeVoie) {
                    elementj.definitCategorieMot(CategorieMot.Autre);
                    count--;
                }
            }
        }

        // Si un seul type de voie est précédé d'un numéro non précédé d'un ème ou d'une clé il est conservé.
        boolean gotone = false;
        boolean gotonlyone = true;
        int index_i = -1;
        int index_j = -1;
        for (int i = 0; gotonlyone && count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; gotonlyone && count > 1 && j < elementsi.size(); j++) {
                RefCle elementj = elementsi.get(j);
                if (elementj.obtientCategorieMot() == CategorieMot.TypeDeVoie) {
                    int indiceElementPrecedent = indiceElementPrecedent(elementsi, j);
                    if (indiceElementPrecedent != -1) {
                        boolean precedeparnumero = trouveElementDeCategorie(elementsi, indiceElementPrecedent, CategorieMot.NumeroAdresse) != -1;

                        if (precedeparnumero) {
                            if (gotone) {
                                gotonlyone = false;
                            } else {
                                index_i = i;
                                index_j = j;
                                gotone = true;
                            }
                        }
                    }
                }
            }
        }
        // Si un unique est trouvé, retire les autres !
        if (gotone && gotonlyone) {
            for (int i = 0; count > 1 && i < elements.size(); i++) {
                ArrayList<RefCle> elementsi = elements.get(i);
                for (int j = 0; count > 1 && j < elementsi.size(); j++) {
                    if (j != index_j) {
                        RefCle elementj = elementsi.get(j);
                        if (elementj.obtientCategorieMot() == CategorieMot.TypeDeVoie) {
                            count--;
                            elementj.definitCategorieMot(CategorieMot.Cle);
                        }
                    }
                }
            }
        }

        // Les types de voies qui n'ont pas d'abbréviation officielle sont supprimés
        for (int i = 0; count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; count > 1 && j < elementsi.size(); j++) {
                RefCle elementj = elementsi.get(j);
                if (elementj.obtientCategorieMot() == CategorieMot.TypeDeVoie && !elementj.estofficiel()) {
                    elementj.definitCategorieMot(CategorieMot.Cle);
                    // elementsi.remove(j);
                    // j--;
                    // les éléments libelle qui le suivent doivent aussi être modifié en Autre
                    // pour la répartition des compléments qui devrait suivre.
                    for (int k = j + 1; k < elementsi.size(); k++) {
                        RefCle elementk = elementsi.get(k);
                        CategorieMot categoriek = elementk.obtientCategorieMot();
                        if (categoriek == CategorieMot.Article) {
                            continue;
                        }
                        if (categoriek == CategorieMot.Libelle) {
                            elementk.definitCategorieMot(CategorieMot.Autre);
                        } else {
                            break;
                        }
                    }
                    count--;
                }
            }
        }

        // Sinon, les premiers sont ignorés
        for (int i = 0; count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; count > 1 && j < elementsi.size(); j++) {
                RefCle elementj = elementsi.get(j);
                if (elementj.obtientCategorieMot() == CategorieMot.TypeDeVoie) {
                    elementj.definitCategorieMot(CategorieMot.Cle);
                    // elementsi.remove(j);
                    // j--;
                    // les éléments libelle qui le suivent doivent aussi être modifié en Autre
                    // pour la répartition des compléments qui devrait suivre.
                    for (int k = j + 1; k < elementsi.size(); k++) {
                        RefCle elementk = elementsi.get(k);
                        CategorieMot categoriek = elementk.obtientCategorieMot();
                        if (categoriek == CategorieMot.Article) {
                            continue;
                        }
                        if (categoriek == CategorieMot.Libelle) {
                            elementk.definitCategorieMot(CategorieMot.Autre);
                        } else {
                            break;
                        }
                    }
                    count--;
                }
            }
        }
    }

    /**
     * Résoud les doublons de codes postaux
     * <ul>
     * <li>Dans tous les cas, les codes de départements sont ignorés</li>
     * <li>Les premiers sont "poste restante", et le dernier code postal.</li>
     * <li>Les postes restantes hors du département sont éliminées.</li>
     * </ul>
     * @param elements
     * @param count
     * @param departements_finaux
     */
    private void resoudAmbiguitesDoublonsCodePostal(ArrayList<ArrayList<RefCle>> elements, int count, ArrayList<String> departements_finaux) {
        // Dans tous les cas, les codes de départements sont ignorés
        for (int i = 0; i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()); j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.CodeDepartement) {
                    elementsi.get(j).definitCategorieMot(CategorieMot.Numero);
                }
            }
        }
        // les premier sont poste restante, et le dernier code postal.
        String str_departement = null;
        for (int i = 0; count > 0 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 0; j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.CodePostal) {
                    if (count > 1) {
                        elementsi.get(j).definitCategorieMot(CategorieMot.CodePostalRestant);
                    } else {
                        // ajoute son code de département.
                        str_departement = elementsi.get(j).obtientMot().substring(0, 2);
                        departements_finaux.add(str_departement);
                    }
                    count--;
                }
            }
        }
        // Les postes restantes hors du département sont éliminées.
        for (int i = 0; i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()); j++) {
                RefCle elementj = elementsi.get(j);
                if (elementj.obtientCategorieMot() == CategorieMot.CodePostalRestant) {
                    if (elementj.obtientMot().substring(0, 2).compareTo(str_departement) != 0) {
                        elementj.definitCategorieMot(CategorieMot.Numero);
                    }
                }
            }
        }
    }

    /**
     * Résoud les doublons de code département:
     * <ul>
     * <li>Si l'un d'eux n'est pas suivi d'une ville, il est ignoré</li>
     * <li>Sinon, les premiers sont ignorés, et le dernier défini comme code de département</li>
     * </ul>
     */
    private void resoudAmbiguitesDoublonsCodeDepartement(ArrayList<ArrayList<RefCle>> elements, int count,
            ArrayList<String> departements_finaux) {
        // Si l'un d'eux n'est pas suivi d'une ville, il est ignoré
        for (int i = 0; count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 1; j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.CodeDepartement) {
                    int indiceElementSuivant = indiceElementSuivant(elementsi, j);
                    if (indiceElementSuivant == -1 || trouveElementDeCategorie(elementsi, indiceElementSuivant,
                            CategorieMot.Ville) == -1) {
                        elementsi.get(j).definitCategorieMot(CategorieMot.Numero);
                        count--;
                    }
                }
            }
        }
        // Sinon, les premiers sont ignorés, et le dernier défini comme code de département
        for (int i = 0; count > 0 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 0; j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.CodeDepartement) {
                    if (count > 1) {
                        elementsi.get(j).definitCategorieMot(CategorieMot.Numero);
                    } else {
                        // Modif WA 09/2011 le code dpt n'est plus repute tenir sur 2 cars (20 A par ex.)
//                        departements_finaux.add(elementsi.get(j).obtientMot().substring(0, 2));
                        departements_finaux.add(elementsi.get(j).obtientMot());
                    }
                    count--;
                }

            }
        }
    }

    /**
     * Résoud les doublons de commune:
     * <ul>
     * <li>Si l'un d'eux est précédé d'une clé ou d'un type de voie, il est oublié</li>
     * <li>Si l'un d'eux n'est pas précédé d'un code postal, il est défini comme poste restante uniquement si 
     * si un de ses départements est un département présumé (sinon, il est défini comme Autre mot).</li>
     * <li>Si l'un d'eux est précéde d'un code postal restant, il est restant.</li>
     * <li>Sinon, le premier est ville restante</li>
     * </ul>
     * 
     * Ce traitement est susceptible d'affecter le code de département :
     * <ul>
     * <li>Si aucun département n'a été assigné, ceux de la commune sont utilisés.</li>
     * </ul>
     * 
     * Les postes restantes n'appartenant pas à ce ou ces départements sont supprimés.
     */
    private void resoudAmbiguitesDoublonsVille(ArrayList<ArrayList<RefCle>> elements, int count, ArrayList<String> departements_finaux) {
        // Si l'un d'eux est précédé d'une clé ou d'un type de voie, il est oublié.
        for (int i = 0; (i < elements.size()) && count > 1; i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 1; j++) {
                RefCle elementj = elementsi.get(j);
                if (elementj.obtientCategorieMot() == CategorieMot.Ville) {
                    int indiceElementPrecedent = indiceElementPrecedent(elementsi, j);
                    if (indiceElementPrecedent != -1) {
                        if (trouveElementDeCategorie(elementsi, indiceElementPrecedent, CategorieMot.Cle) != -1) {
                            elementj.definitCategorieMot(CategorieMot.Autre);
                            count--;
                        } else if (trouveElementDeCategorie(elementsi, indiceElementPrecedent, CategorieMot.TypeDeVoie) != -1) {
                            elementj.definitCategorieMot(CategorieMot.Autre);
                            count--;
                        }
                    }
                }
            }
        }
        // Si l'un d'eux n'est pas précédé d'un code postal, il est défini comme poste restante,
        // si un de ses départements est un département final.
        for (int i = 0; count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 1; j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.Ville) {
                    int indiceElementPrecedent = indiceElementPrecedent(elementsi, j);
                    if (indiceElementPrecedent == -1 || trouveElementDeCategorie(elementsi, indiceElementPrecedent,
                            CategorieMot.CodePostal) == -1) {
                        RefCommune ref = (RefCommune) elementsi.get(j);
                        if (ref.contientDepartement(departements_finaux)) {
                            ref.definitCategorieMot(CategorieMot.VilleRestante);
                        } else {
                            ref.definitCategorieMot(CategorieMot.Autre);
                        }
                        count--;
                    }
                }
            }
        }
        // Si l'un d'eux est précéde d'un code postal restant, il est restant.
        for (int i = 0; (i < elements.size()) && count > 1; i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 1; j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.Ville) {
                    int indiceElementPrecedent = indiceElementPrecedent(elementsi, j);
                    if (indiceElementPrecedent != -1) {
                        if (trouveElementDeCategorie(elementsi, indiceElementPrecedent, CategorieMot.CodePostalRestant) != -1) {
                            RefCommune ref = (RefCommune) elementsi.get(j);
                            if (ref.contientDepartement(departements_finaux)) {
                                ref.definitCategorieMot(CategorieMot.VilleRestante);
                            } else {
                                ref.definitCategorieMot(CategorieMot.Autre);
                            }
                            count--;
                        }
                    }
                }
            }
        }
        // Sinon, le premier est ville restante
        for (int i = 0; count > 1 && i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            for (int j = 0; (j < elementsi.size()) && count > 1; j++) {
                if (elementsi.get(j).obtientCategorieMot() == CategorieMot.Ville) {
                    RefCommune ref = (RefCommune) elementsi.get(j);
                    if (ref.contientDepartement(departements_finaux)) {
                        ref.definitCategorieMot(CategorieMot.VilleRestante);
                    } else {
                        ref.definitCategorieMot(CategorieMot.Autre);
                    }
                    count--;
                }
            }
        }
        // Si aucun département n'a été assigné, ceux de la commune sont utilisés.
        if (departements_finaux.size() == 0) {
            // Sinon, les premiers sont ville restante
            for (int i = 0; count > 0 && i < elements.size(); i++) {
                ArrayList<RefCle> elementsi = elements.get(i);
                for (int j = 0; (j < elementsi.size()) && count > 0; j++) {
                    if (elementsi.get(j).obtientCategorieMot() == CategorieMot.Ville) {
                        RefCommune ref = (RefCommune) elementsi.get(j);
                        departements_finaux.addAll(ref.obtientCodesDepartement());
                    }
                }
            }

            // A partir de ce code de département, les villes restantes peuvent être eliminées.
            for (int i = 0; i < elements.size(); i++) {
                ArrayList<RefCle> elementsi = elements.get(i);
                for (int j = 0; j < elementsi.size(); j++) {
                    if (elementsi.get(j).obtientCategorieMot() == CategorieMot.VilleRestante) {
                        RefCommune ref = (RefCommune) elementsi.get(j);
                        if (!ref.contientDepartement(departements_finaux)) {
                            ref.definitCategorieMot(CategorieMot.Autre);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determine parmis les N (>1) pays presents lequel est le plus suceptible d'être le bon.
     * - le pays d'index et de ligne max est garde : le pays se situant generalement en fin de ligne dans une adresse.
     * @param elements
     * @param count
     */
    private void resoudAmbiguitesDoublonsPays(ArrayList<ArrayList<RefCle>> elements, int count) {
        // On cherche le pays le plus lointain dans l'adresse
        RefCle maxPays = null;
        for (ArrayList<RefCle> elementsLine : elements) {
            int lineMaxIndex = -1;
            for (RefCle cle : elementsLine) {
                if ((cle.obtientCategorieMot() == CategorieMot.Pays) && (cle.obtientIndex() > lineMaxIndex)) {
                    lineMaxIndex = cle.obtientIndex();
                    maxPays = cle;
                }
            }
        }
        // Puis on 'desactive' tous les autres
        if (maxPays != null) {
            for (ArrayList<RefCle> elementsLine : elements) {
                for (RefCle cle : elementsLine) {
                    if ((cle.obtientCategorieMot() == CategorieMot.Pays) && (!cle.equals(maxPays))) {
                        cle.definitCategorieMot(CategorieMot.Autre);
                    }
                }
            }
        }
    }

    /**
     * Résoud les ambiguités pour les éléments complexes comme les villes.
     * @param elements
     * @param departements_finaux
     * @param connection
     */
    private void resoudAmbiguitesDoublonsComplexes(ArrayList<ArrayList<RefCle>> elements, ArrayList<String> departements_finaux,
            Connection connection) {
        int count;
        // Traite les communes
        if ((count = compteCategorie(elements, CategorieMot.Ville)) > 0) {
            resoudAmbiguitesDoublonsVille(elements, count, departements_finaux);
        }
        // WA 01/2012 Pays
        if ((count = compteCategorie(elements, CategorieMot.Pays)) > 0) {
            resoudAmbiguitesDoublonsPays(elements, count);
        }
    }

    /**
     * Pour chaque code departement de la liste, le corrige avec son eventuelle version officielle.
     * @param codesDpt
     */
    private void officialiseCodesDepartements(List<String> codesDpt) {
        if (codesDpt != null) {
            for (int i = 0; i < codesDpt.size(); i++) {
                codesDpt.set(i, GestionCodesDepartements.getInstance().getOfficialCodeDpt(codesDpt.get(i)));
            }
        }
    }

    /**
     * Résoud les ambiguités pour les éléments en double ou en triple dans l'ensemble des chaines.
     * Les types de voies, noms de commune, ou codes postaux en double ou triple sont corrigés.
     * @return la liste des départements présumés de l'adresse (à partir de la ligne 6)
     */
    private ArrayList<String> resoudAmbiguitesDoublons(ArrayList<ArrayList<RefCle>> elements, Connection connection) {
        ArrayList<String> departements_finaux = new ArrayList<String>();
        int count;
        // traite les types de voies.
        if ((count = compteCategorie(elements, CategorieMot.TypeDeVoie)) > 1) {
            resoudAmbiguitesDoublonsTypeDeVoie(elements, count);
        }

        // traites les codes postaux
        count = compteCategorie(elements, CategorieMot.CodePostal);
        // Si des codes postaux sont présent, les codes de départements sont supprimés.
        if (count > 0) {
            resoudAmbiguitesDoublonsCodePostal(elements, count, departements_finaux);
        } else if ((count = compteCategorie(elements, CategorieMot.CodeDepartement)) > 0) {
            resoudAmbiguitesDoublonsCodeDepartement(elements, count, departements_finaux);
        }

        return departements_finaux;
    }

    /**
     * Supprime les éléments de même index que l'élément d'indice spécifié.
     * @param i
     * @param elements
     */
    private void supprimeElementMemeIndex(int i, ArrayList<RefCle> elements) {
        int index = elements.get(i).obtientIndex();
        int j = i + 1;
        while (j < elements.size() && elements.get(j).obtientIndex() == index) {
            elements.remove(j);
        }
    }

    /**
     * Supprime les éléments qui sont inclus dans l'élément d'indice spécifié.
     * 
     * Les éléments supprimés sont tels que
     * element.index < element_supprime.index < element.index + element.taille - 1
     * ou
     * element.index < element_supprime.index + element_supprime.taille - 1 < element.index + element.taille -1
     * ou encore
     * element_supprime.index < element.index et fin < element_supprime.index + element_supprime.taille - 1
     * 
     * @param i l'indice de l'élément spécifié.
     * @param elements Ensemble d'éléments dont il est supposé qu'ils sont en ordre d'index.
     * 
     * @return le nouvel indice de l'élement spécifié
     */
    private int supprimeElementsInclus(int i, ArrayList<RefCle> elements) {
        int indexToReturn = i;
        RefCle theCle = elements.get(i);
        int theIndexDebut = theCle.obtientIndex();
        int theIndexFin = theIndexDebut + theCle.obtientMot().length() - 1;
        List<RefCle> clesToRemove = new ArrayList<RefCle>();

        for (int j = 0; j < elements.size(); j++) {
            if (j != i) {
                RefCle curCle = elements.get(j);
                int curCleIndexDebut = curCle.obtientIndex();
                int curCleIndexFin = curCleIndexDebut + curCle.obtientMot().length() - 1;

                if (((curCleIndexDebut >= theIndexDebut) && (curCleIndexDebut <= theIndexFin)) // Index debut de curCle entre debut et fin de theCle
                        ||
                        ((curCleIndexFin >= theIndexDebut) && (curCleIndexFin <= theIndexFin)) // Index fin de curCle entre debut et fin de theCle
                        ||
                        ((curCleIndexDebut <= theIndexDebut) && (curCleIndexFin >= theIndexFin)) // curCle contient theCle
                        ) {
                    clesToRemove.add(curCle);
                    if (j < i) {
                        indexToReturn--;
                    }
                }
            }
        }
        for (RefCle cleToRemove : clesToRemove) {
            elements.remove(cleToRemove);
        }
        return indexToReturn;
    }

    /**
     * Supprime les éléments qui sont inclus dans l'élément d'indice spécifié.
     * 
     * Les éléments supprimés sont tels que
     * element.index < element_supprime.index < element.index + element.taille - 1
     * ou
     * element.index < element_supprime.index + element_supprime.taille - 1 < element.index + element.taille -1
     * ou encore
     * element_supprime.index < element.index et fin < element_supprime.index + element_supprime.taille - 1
     * 
     * @param i l'indice de l'élément spécifié.
     * @param elements Ensemble d'éléments dont il est supposé qu'ils sont en ordre d'index.
     * 
     * @return le nouvel indice de l'élement spécifié
     */
//    private int supprimeElementsInclus(int i, ArrayList<RefCle> elements)
//    {
//        RefCle rc = elements.get(i);
//        int index = rc.obtientIndex();
//        int fin = index + rc.obtientMot().length() - 1;
//
//        for(int j = 0; j < elements.size(); j++)
//        {
//            if(i != j)
//            {
//                RefCle rc_asupprimer = elements.get(j);
//                int index_asupprimer = rc_asupprimer.obtientIndex();
//                int fin_asupprimer = index_asupprimer + rc_asupprimer.obtientMot().length() - 1;
//
//                boolean asupprimer = false;
//
//                if(index <= index_asupprimer && index_asupprimer <= fin)
//                {
//                    asupprimer = true;
//
//                } else if(index <= fin_asupprimer && fin_asupprimer <= fin)
//                {
//                    asupprimer = true;
//
//                } else if(index_asupprimer <= index && fin <= fin_asupprimer)
//                {
//                    asupprimer = true;
//
//
//                }
//                if(asupprimer)
//                {
//                    elements.remove(j);
//                    if(j < i)
//                    {
//                        i--;
//                    }
//                }
//            }
//        }
//        return i;
//    }
    /**
     * Donne une valeur à chaque catégorie de mot pour mieux pouvoir les ordonner.
     * La priorité est donnée aux articles, clés, types de voies, puis aux communes.
     * @param categorie
     * @return
     */
    private int valeur(CategorieMot categorie) {
        if (categorie == CategorieMot.Article) {
            return 0;
        } else if (categorie == CategorieMot.TypeDeVoie) {
            return 2;
        } else if (categorie == CategorieMot.Cle) {
            return 1;
        } else if (categorie == CategorieMot.Ville) {
            return 3;
        } else {
            return 4;

        }
    }

    /**
     * Trie les éléments de même index.<br>
     * La priorité est donnée aux articles,clés, types de voies, puis aux communes.
     * @param elements
     */
    private void trieElements(List<RefCle> elements) {
        for (int i = 0; i < elements.size(); i++) {
            RefCle cle = elements.get(i);
            int index = cle.obtientIndex();
            int valeur = valeur(cle.obtientCategorieMot());

            // Cherche les éléments de même index mais de priorité supérieure qui suivent i
            int j = i + 1;
            while (j < elements.size() && elements.get(j).obtientIndex() == index && valeur(elements.get(j).obtientCategorieMot()) < valeur) {
                j++;
            }
            if (j > i + 1) {
                // modifie la position de i
                elements.remove(i);
                elements.add(j - 1, cle);
                i--;
            }
        }
    }

    /**
     * Le terme est étiqueté libellé si :
     * 1. il est précédé d'un autre terme libellé
     * 2. il est précédé d'une suite de répétition, article,
     * avec au moins un numéros non précédé d'une clé précédé d'un ème
     */
    private void resoudAutresAmbiguites(ArrayList<RefCle> elements, RefCle element, int indiceprecedent_0) {
        if (indiceprecedent_0 != -1) {
            // S'il est précédé d'un libellé, il s'agit d'un libellé
            if (trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.Libelle) != -1) {
                element.definitCategorieMot(CategorieMot.Libelle);
                return;
            }

            // S'il est précédé d'une suite de numéro, répétition, article avec au moins un numéro
            int indiceprecedent = indiceprecedent_0;
            int numeros = 0;
            CategorieMot categorie_precedent = elements.get(indiceprecedent).obtientCategorieMot();
            while (indiceprecedent != -1 && (categorie_precedent == CategorieMot.Repetition || categorie_precedent == CategorieMot.Article || categorie_precedent == CategorieMot.Numero)) {
                if (categorie_precedent == CategorieMot.Numero) {
                    numeros++;
                }
                indiceprecedent = indiceElementPrecedent(elements, indiceprecedent);
                if (indiceprecedent != -1) {
                    categorie_precedent = elements.get(indiceprecedent).obtientCategorieMot();
                }
            }
            if (numeros > 1) {
                element.definitCategorieMot(CategorieMot.Libelle);
                return;
            }

            // Si un seul numéro a été trouvé, et que le premier élément de cette chaine de
            // numéro, répétition, article est ce numéro,
            // il ne doit pas être précédé d'une clé précédée d'un Eme.
            if (numeros == 1 && categorie_precedent == CategorieMot.Numero) {
                if (indiceprecedent != -1) {
                    if (trouveElementDeCategorie(elements, indiceprecedent, CategorieMot.Cle) != -1) {
                        indiceprecedent = indiceElementPrecedent(elements, indiceprecedent);

                        if (indiceprecedent != -1) {
                            if (trouveElementDeCategorie(elements, indiceprecedent, CategorieMot.Eme) != -1) {
                                element.definitCategorieMot(CategorieMot.Libelle);
                                return;
                            }
                        }
                    } else {
                        element.definitCategorieMot(CategorieMot.Libelle);
                        return;
                    }
                } else {
                    element.definitCategorieMot(CategorieMot.Libelle);
                    return;
                }
            } else {
                element.definitCategorieMot(CategorieMot.Autre);
                return;
            }
        }
    }

    /**
     * Résoud les ambiguités des types de voies<br>
     * Plusieurs cas:<br>
     * 1. le type de voie est précédé d'un article (s'il ne s'agit pas d'une répétition précédé d'un numéro)<br>
     * 1bis. le type de voie est précédé d'un type de voie<br>
     * 2. il est précédé par un numéro non précédé d'une clé non précédée d'un ème<br>
     * 3. le type de voie est précédé d'une clé non précédé d'un ème<br>
     * supprimé : 4. Le type de voie est inclu dans un nom de voie, le contexte est utilisé pour trancher.<br>
     * 5. Le type de voie est inclu dans un nom de commune, le contexte est utilisé pour trancher.<br>
     * 6. Le type de voie est égal à une commune.<br>
     * 7. ABANDONNE : Le type de voie est égal à un nom de voie.<br>
     * 8. Il s'agit effectivement d'un type de voie.<br>
     * Le cas où le type de voie est une clé est géré dans la catégorie clé.<br>
     * Le cas où le type de voie est un titre est géré dans la catégorie titre.<br>
     *
     * Le cas 3 est supprimé : le type de voie peut être considéré comme tel dans ces deux cas
     * ce qui ne perturbe pas les bons résultats (traité comme un doublon de type de voie)
     * et élimine des faux positifs.
     * @param elements
     * @param elements
     */
    private int resoudAmbiguitesTypeDeVoie(ArrayList<RefCle> elements, RefCle element, int indiceprecedent_0, String contexte,
            ArrayList<String> codes_departement, Connection connection, int i) throws SQLException {
        // Cas 1, il est précédé d'un article
        if (indiceprecedent_0 != -1) {
            int indexArticle = -1;
            if ((indexArticle = trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.Article)) != -1) {
                // Exception : certains articles peuvent être des répétitions, auquel cas, ils sont toujours précédés d'un numéro.
                RefCle refArticle = elements.get(indexArticle);
                if (estRepetitionEtArticle(refArticle.obtientMot())) // si cet article est aussi une répétition
                {
                    int superPrecedent = indiceElementPrecedent(elements, indiceprecedent_0);

                    if (superPrecedent != -1) {
                        if (trouveElementDeCategorie(elements, superPrecedent, CategorieMot.Numero) != -1) {
                        // il s'agit donc à priori d'un type de voie.
                        } else {
                            // il ne s'agit donc pas d'un type de voie
                            element.definitCategorieMot(CategorieMot.Autre);
                            return i;
                        }
                    } else {
                        // il ne s'agit donc pas d'un type de voie
                        element.definitCategorieMot(CategorieMot.Autre);
                        return i;
                    }
                } else {
                    // il ne s'agit donc pas d'un type de voie
                    element.definitCategorieMot(CategorieMot.Autre);
                    return i;
                }
            }

            // Cas 1bis, il est précédé d'un type de voie (ou règle ultime : faut pas le faire exprès)
            if (trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.TypeDeVoie) != -1) {
                // il ne s'agit donc pas d'un type de voie
                element.definitCategorieMot(CategorieMot.Autre);
                return i;
            }

            // Cas 2, il est précédé d'un numéro non précédé d'une clé non précédée d'un ème
            // Ce critère est repris dans la résolution des ambiguités de type clé.
            if (trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.Numero) != -1) {
                int superPrecedent = indiceElementPrecedent(elements, indiceprecedent_0);

                if (superPrecedent == -1) {
                    supprimeElementMemeIndex(i, elements);
                    return i; // il s'agit d'un type de voie
                } else {
                    if (!estPrecedeeParCleNonPrecedeeParEme(elements, superPrecedent)) {
                        supprimeElementMemeIndex(i, elements);
                        return i; // il s'agit d'un type de voie
                    }
                }
            }

            // Cas 2, il est précédé d'une clé non précédée d'un ème
            if (estPrecedeeParCleNonPrecedeeParEme(elements, indiceprecedent_0)) {
                element.definitCategorieMot(CategorieMot.Autre);
                return i;
            }
        }

        // Cas 3, il peut etre inclus dans une voie
        if (!debutDeSegment(elements, indiceprecedent_0, i) && referentiel.resoudAmbiguiteAvecContexte(element.obtientNomDeCle(),
                element.obtientMot(),
                CategorieAmbiguite.TypeDeVoieDansVoie.toString(),
                contexte,
                codes_departement,
                connection)) {
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }

        // Cas 4 et 5, il est égal ou inclus dans une commune.
        int indiceVille;
        if ((indiceVille = trouveElementDeCategorie(elements, i, CategorieMot.Ville)) != -1) {
            // il est inclus dans une commune, il s'agit d'une commune
            if (elements.get(indiceVille).obtientMot().length() > element.obtientMot().length()) {
                elements.remove(i);
                i--;
                return i;
            } // il est égale à une commune, cela dépend des éléments qui suivent.
            else {
                int indiceElementSuivant = indiceElementSuivant(elements, i);

                if (indiceElementSuivant == -1) {
                    // rien ne suit, il s'agit bien de la commune.
                    elements.remove(i);
                    i--;
                    return i;
                }
            }
        }
        /*
        // Cas 4 et 5, ANCIENNE IMPLEMENTATION
        // Cas 4, il peut être inclu dans une commune, selon le contexte
        if (referentiel.resoudAmbiguiteAvecContexte(element.obtientNomDeCle(),
        element.obtientMot(),
        CategorieAmbiguite.TypeDeVoieDansCommune.toString(),
        contexte,
        codes_departement,
        connection)) {
        element.definitCategorieMot(CategorieMot.PortionVille);
        return i;
        }
        // Cas 5, il est égal à une commune
        if (referentiel.resoudAmbiguiteSansContexte(element.obtientNomDeCle(),
        CategorieAmbiguite.TypeDeVoieDansCommune.toString(),
        codes_departement,
        connection)) {
        // dans ce cas, il faut vérifier que rien ne suit,
        int indiceElementSuivant = indiceElementSuivant(elements, i);
        if (indiceElementSuivant == -1) {
        // rien ne suit, il s'agit bien de la commune.
        // un des éléments suivant est certainement la ville.
        elements.remove(i);
        i--;
        return i;
        }
        }*/

        // Cas 6, il est égal à une voie (ABANDONNE)
//        if (referentiel.resoudAmbiguiteSansContexte(element.obtientNomDeCle(),
//                CategorieAmbiguite.TypeDeVoieDansVoie.toString(),
//                codes_departement,
//                connection)) {
//            // dans ce cas, il faut vérifier que rien ne suit,
//            int indiceElementSuivant = indiceElementSuivant(elements, i);
//
//            if (indiceElementSuivant == -1) {
//                element.definitCategorieMot(CategorieMot.Libelle);
//                return i;
//            }
//            if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.TypeDeVoie)) {
//                element.definitCategorieMot(CategorieMot.Libelle);
//                return i;
//            }
//        }

        //
        // Cas 7, il s'agit d'un type de voie.
        // les éléments de même index sont supprimés
        supprimeElementMemeIndex(i, elements);

        return i;
    }

    /**
     * Retourne vrai si un élément est précédé d'une clé non précédée d'un Eme.
     * @param elements les éléments.
     * @param indiceElementPrecedent l'indice précédent l'élément en question.
     * @return
     */
    private boolean estPrecedeeParCleNonPrecedeeParEme(ArrayList<RefCle> elements, int indiceElementPrecedent) {
        if (indiceElementPrecedent != -1 && trouveElementDeCategorie(elements, indiceElementPrecedent,
                CategorieMot.Cle) != -1) {
            indiceElementPrecedent = indiceElementPrecedent(elements, indiceElementPrecedent);
            if (indiceElementPrecedent == -1 || trouveElementDeCategorie(elements, indiceElementPrecedent,
                    CategorieMot.Eme) == -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne vrai si un élément est précédé d'une clé précédée d'un Eme.
     * @param elements les éléments.
     * @param indiceElementPrecedent l'indice précédent l'élément en question.
     * @return
     */
    private boolean estPrecedeeParClePrecedeeParEme(ArrayList<RefCle> elements, int indiceElementPrecedent) {
        if (indiceElementPrecedent != -1 && trouveElementDeCategorie(elements, indiceElementPrecedent,
                CategorieMot.Cle) != -1) {
            indiceElementPrecedent = indiceElementPrecedent(elements, indiceElementPrecedent);

            if (indiceElementPrecedent != -1 && trouveElementDeCategorie(elements, indiceElementPrecedent,
                    CategorieMot.Eme) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne vrai si un élément est précédé d'un texte quelconque ou d'un numéro.
     * @param elements
     * @param indiceElementPrecedent
     * @return
     */
    private boolean estPrecedeeParTexteOuNumero(ArrayList<RefCle> elements, int indiceElementPrecedent) {
        if (indiceElementPrecedent == -1) {
            return false;


        }
        if (trouveElementDeCategorie(elements, indiceElementPrecedent, CategorieMot.Autre) != -1) {
            return true;

        }
        if (trouveElementDeCategorie(elements, indiceElementPrecedent, CategorieMot.Numero) != -1) {
            return true;

        }
        if (trouveElementDeCategorie(elements, indiceElementPrecedent, CategorieMot.NumeroAdresse) != -1) {
            return true;

        }
        if (trouveElementDeCategorie(elements, indiceElementPrecedent, CategorieMot.NumeroCleApres) != -1) {
            return true;


        }
        return false;
    }

    /**
     * Retourne vrai si un élément est précédé d'un texte quelconque (ou numéro) précédée d'une clé non précédée d'un ème.
     * @param elements les éléments.
     * @param indiceElementPrecedent l'indice précédent l'élément en question.
     * @return
     */
    private boolean estPrecedeeParTextePrecedeeParCle(ArrayList<RefCle> elements, int indiceElementPrecedent) {
        if (estPrecedeeParTexteOuNumero(elements, indiceElementPrecedent)) {
            indiceElementPrecedent = indiceElementPrecedent(elements, indiceElementPrecedent);

            if (estPrecedeeParCleNonPrecedeeParEme(elements, indiceElementPrecedent)) {
                return true;

            }
        }
        return false;
    }

    /**
     * Cette méthode est utilisée dans la gestion des ambiguités
     * pour limiter le nombre d'appels à la base.
     * 
     * Les cas d'ambiguités où le terme ambigu débute un élément de
     * grammaire de l'adresse comme :
     * <ul>
     * <li>ESCALIER 2</li>
     * <li>PORTE PANTIN</li>
     * </ul>
     * sont simplement considérés comme n'appartenant pas au libellé d'une voie.
     * 
     * Si une personne ecrit délibéremment 'MONSIEUR' plutôt que 'RUE MONSIEUR'
     * en ayant conscience du problème d'ambiguité, je considère qu'il l'a
     * fait exprès, et par conséquent, l'erreur n'est pas corrigée.
     * 
     * 
     * Pour mettre en oeuvre ce mécanisme, cette méthode permet de savoir si,
     * au cour d'une résolution d'ambiguité (à savoir que l'élément qui précède
     * à déjà été résolu), l'élément en cours débute un élément de grammaire ou non.
     * 
     * Cas pris en charge:
     * <ul>
     *   <li>début de ligne</li>
     *   <li>après un nom de commune</li>
     *   <li>après une clé précédée par Eme</li>
     *   <li>après le numéro d'une clé</li>
     *   <li>après un texte quelconque précédé d'une clé (non précédée d'un Eme)</li>
     * </ul>
     * 
     * @return
     */
    private boolean debutDeSegment(ArrayList<RefCle> elements, int indiceprecedent_0, int i) {
        if (indiceprecedent_0 == -1) {
            return true;


        }
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.Ville)) {
            return true;
        }
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.NumeroCleApres)) {
            return true;
        }
        int indiceElementPrecedent = indiceElementPrecedent(elements, i);

        if (estPrecedeeParClePrecedeeParEme(elements, indiceElementPrecedent)) {
            return true;
        }

        if (estPrecedeeParTextePrecedeeParCle(elements, indiceElementPrecedent)) {
            return true;
        }
        return false;
    }

    /**
     * Résoud les ambiguités des clés<br>
     * Plusieurs cas possibles:<br>
     * 1. La clé est précédée d'articles et d'un type de voie.<br>
     * 2. La clé est précédée d'une clé.<br>
     * 3. La clé est aussi un type de voie, auquel cas seul le contexte permet de trancher.<br>
     * 4. La clé fait partie d'un nom de voie (pas en première position), auquel cas seul le contexte permet de trancher.<br>
     * 5. La clé fait partie d'un nom de commune, auquel cas seul le contexte permet de trancher.<br>
     * 6. La clé égale une commune<br>
     * 7. Il s'agit vraiment d'une clé.<br>
     */
    private int resoudAmbiguitesCles(ArrayList<RefCle> elements, RefCle element, int indiceprecedent_0, Mot m, String contexte,
            String contexte_apres, ArrayList<String> codes_departement, Connection connection, int i) throws SQLException {
        // Cas 1 : vérifie si la clé est précédé d'un type de voie, auquel cas il ne s'agit pas d'une clé
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.TypeDeVoie)) {
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }

        // Cas 2 : La clé est directement précédé d'une autre clé non précédée d'un ème.
        int indiceElementPrecedent = indiceElementPrecedent(elements, i);
        if (estPrecedeeParCleNonPrecedeeParEme(elements, indiceElementPrecedent)) {
            // il ne s'agit donc pas d'une clé.
            element.definitCategorieMot(CategorieMot.Autre);
            return i;
        }

        // Teste le cas 3
        if (m != null && m.estDeLaCategorie(CategorieMot.TypeDeVoie)) {
            // Reprise des critères de la résolution d'ambiguité des types de voie.
            if (indiceprecedent_0 != -1 && trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.Numero) != -1) {
                int superPrecedent = indiceElementPrecedent(elements, indiceprecedent_0);

                if (superPrecedent == -1) {
                    // il s'agit d'un type de voie !
                    // la catégorie est modifiée, et les autres éléments supprimés.
                    element.definitCategorieMot(CategorieMot.TypeDeVoie);
                    supprimeElementMemeIndex(i, elements);
                    return i;
                } else {
                    if (!estPrecedeeParCleNonPrecedeeParEme(elements, superPrecedent)) {
                        // il s'agit d'un type de voie !
                        // la catégorie est modifiée, et les autres éléments supprimés.
                        element.definitCategorieMot(CategorieMot.TypeDeVoie);
                        supprimeElementMemeIndex(i, elements);
                        return i;
                    }
                }
            }

            // Vérifie le contexte de la clé, pour savoir s'il s'agit d'un type de voie.
            if (referentiel.resoudAmbiguite(element.obtientNomDeCle(),
                    element.obtientMot(),
                    CategorieAmbiguite.CleEstTypeDeVoie.toString(),
                    contexte_apres,
                    codes_departement,
                    connection)) {
                // Si il s'agit d'un type de voie, c'est qu'il ne s'agit pas d'une clé, auquel cas
                // la référence est supprimée.
                elements.remove(i);
                i--;
                return i; // les autres cas ne peuvent correpondre, puisqu'il s'agit d'un type de voie.

            } else {
                // Sinon, c'est qu'il s'agit bien d'une clé, auquel cas
                // il faut trouver la référence au type de voie et la supprimer.
                int indicetypedevoie = trouveElementDeCategorie(elements, i, CategorieMot.TypeDeVoie);
                elements.remove(indicetypedevoie);
                if (indicetypedevoie < i) {
                    i--;
                // Attention toutefois, il ne s'agit pas forcemment d'une clé,
                // et les cas suivant doivent toujours être examinés.
                }
            }
        }

        //
        // Teste le cas 4 : la clé est dans la voie
        // Le cas 4 n'est pertinent que si la clé ne débute pas le nom de voie,
        // sinon, un type de voie est attendu.
        if (!debutDeSegment(elements, indiceprecedent_0, i) && referentiel.resoudAmbiguiteAvecContexte(element.obtientNomDeCle(),
                element.obtientMot(),
                CategorieAmbiguite.CleDansVoie.toString(),
                contexte,
                codes_departement,
                connection)) {
            // Il s'agit alors d'un nom de voie, et la référence peut être supprimée
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }

        // Teste le cas 5 : la clé est dans la commune
        if (referentiel.resoudAmbiguiteAvecContexte(element.obtientNomDeCle(),
                element.obtientMot(),
                CategorieAmbiguite.CleDansCommune.toString(),
                contexte,
                codes_departement,
                connection)) {
            // Il s'agit alors d'un nom de commune, et la référence peut être supprimée.
            element.definitCategorieMot(CategorieMot.PortionVille);
            return i;
        }

        // Cas 6: la clé égal la commune
        if (referentiel.resoudAmbiguiteSansContexte(element.obtientNomDeCle(),
                CategorieAmbiguite.CleDansCommune.toString(),
                codes_departement,
                connection)) {
            // dans ce cas, il faut vérifier que rien ne suit, ou qu'il s'agit d'un code postal, 
            // ou d'un nom de commune.
            int indiceElementSuivant = indiceElementSuivant(elements, i);

            if (indiceElementSuivant == -1) {
                // rien ne suit, il s'agit bien de la commune
                elements.remove(i);
                i--;
                return i;
            } else {
                // Une ville ou un code postal suit, il s'agit d'une commune.
                if (trouveElementDeCategorie(elements, i, CategorieMot.Ville) != -1 ||
                        trouveElementDeCategorie(elements, i, CategorieMot.CodePostal) != -1) {
                    elements.remove(i);
                    i--;
                    return i;
                }
            }
        }


        // Sinon, il s'agit vraiment d'une clé (cas 6).
        // les éléments de même index sont supprimés
        supprimeElementMemeIndex(i, elements);

        return i;
    }

    /**
     * Résoud les ambiguités des communes
     * Plusieurs cas différents peuvent être rencontrés:
     * 1. Elle débute la ligne, il s'agit d'une commune
     * 2. Il s'agit d'un nom de voie du type RUE DE PARIS, pour lequel le contexte du libellé n'est pas intéressant
     * 3. Elle est précédé d'une clé, il s'agit d'un simple mot
     * 4. Elle est précédé d'une valeur précédée d'une clé, il s'agit d'une commune.
     * 5. Il s'agit d'un nom de voie du type RUE DU FAUBOURG DE PARIS, pour lequel le contexte du libellé est intéressant
     * 6. Il s'agit pas d'une commune.
     */
    private int resoudAmbiguitesVille(ArrayList<RefCle> elements, RefCle element, int indiceprecedent_0, String contexte,
            ArrayList<String> codes_departement, Connection connection, int i) throws SQLException {
        if (indiceprecedent_0 == -1) {
            i = supprimeElementsInclus(i, elements);
            return i;
        }
        // Cas numéro 1,
        // Il faut vérifier qu'un type de voie et éventuellement un ou des articles précédent le nom de commune
        // A cet effet, les éléments précédent sont analysés.
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.TypeDeVoie)) {
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.NumeroCleApres)) {
            // la commune est trouvée seule, il est présupposé qu'il s'agit d'une commune
            i = supprimeElementsInclus(i, elements);
            return i;
        }
        // Cas numéro 3,
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.Cle)) {
            element.definitCategorieMot(CategorieMot.Autre);
            return i;
        }
        // Cas numéro 2,
        if (!debutDeSegment(elements, indiceprecedent_0, i) &&
                referentiel.resoudAmbiguiteAvecContexte(element.obtientCle().obtientNom(),
                element.obtientMot(),
                CategorieAmbiguite.CommuneDansVoie.toString(),
                contexte,
                codes_departement,
                connection)) {
            // Il ne s'agit pas d'une ville, mais d'un mot ordinaire.
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }

        // Sinon, il s'agit du cas numéro 4, et la commune est laissée telle quelle.
        i = supprimeElementsInclus(i, elements);
        return i;
    }

    /**
     * Résoud les ambiguités des pays
     * Plusieurs cas différents peuvent être rencontrés:
     * 1. Il débute la ligne, il s'agit d'un pays
     * 2. Il est precede d'un type de voie eventuellement suivi d'articles (RUE D ITALIE) -> libelle
     * 3. Il est precede d'une cle (monsieur, couloir, immeuble ...) eventuellement suivi d'articles -> autre
     * 4. Il est precede d'un code postal -> pays
     * 5. Il s'agit d'un nom de voie du type SQUARE DES ECRIVAINS COMBATTANTS MORTS POUR LA FRANCE, pour lequel le contexte du libellé est intéressant
     * 6. Il s'agit d'un nom de commune du type SEYNE SUR FRANCE, pour lequel le contexte du libellé est intéressant
     */
    private int resoudAmbiguitesPays(ArrayList<RefCle> elements, RefCle element, int indiceprecedent_0, String contexte,
            ArrayList<String> codes_departement, Connection connection, int i) throws SQLException {
        if (indiceprecedent_0 == -1) {
            i = supprimeElementsInclus(i, elements);
            return i;
        }
        // Cas 1 : $<Pays>
        if (debutDeSegment(elements, indiceprecedent_0, i)) {
            element.definitCategorieMot(CategorieMot.Pays);
            return i;
        }
        // Cas 2 : <typeVoie> <Article>* <Pays> -> <Libelle>
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.TypeDeVoie)) {
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }
        // Cas 3 : <Cle> <Article>* <Pays> -> <Autre>
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.Cle)) {
            element.definitCategorieMot(CategorieMot.Autre);
        }
        // Cas 4 : <CP> <Pays> -> pays ss chercher les ambiguites
        if (estPrecedeDe(elements, indiceprecedent_0, CategorieMot.CodePostal)) {
            return i;
        }
        // Cas 5 : recherched'ambiguite : pays dans voie
        if (referentiel.resoudAmbiguiteAvecContexte(element.obtientCle().obtientNom(),
                element.obtientMot(), CategorieAmbiguite.PaysDansVoie.toString(),
                contexte, codes_departement, connection)) {
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }
        // Cas 6 : recherched'ambiguite : pays dans commune
        if (referentiel.resoudAmbiguiteAvecContexte(element.obtientCle().obtientNom(),
                element.obtientMot(), CategorieAmbiguite.PaysDansCommune.toString(),
                contexte, codes_departement, connection)) {
            element.definitCategorieMot(CategorieMot.Libelle);
            return i;
        }

        // Sinon, c'est bien un pays. On supprime tous les elements le constituant.
        i = supprimeElementsInclus(i, elements);
        return i;
    }

    /**
     * Résoud les ambiguités liées aux codes de département.<br>
     * 1 seul cas: Vérifie si le code de département ne fait pas partie d'une voie
     */
    private void resoudAmbiguitesCodeDepartement(RefCle element, String contexte, ArrayList<String> codes_departement, Connection connection)
            throws SQLException {
        // 1 seul cas: Vérifie si le code de département ne fait pas partie d'une voie
        if (referentiel.resoudAmbiguiteAvecContexte(element.obtientNomDeCle(),
                element.obtientMot(),
                CategorieAmbiguite.NombreDansVoie.toString(),
                contexte,
                codes_departement,
                connection)) {
            // Il s'agit alors d'un nom de voie.
            element.definitCategorieMot(CategorieMot.Libelle);
            return;
        }
    }

    /**
     * Résoud les ambiguités concernant les numéros.
     * Plusieurs cas sont possibles:
     * 1. S'il est composé de 5 chiffres, il s'agit d'un code postal
     * 2. Sinon, s'il est précédé d'une clé qui n'est pas précédé par un ème, il s'agit d'un numéro de clé.
     * 3. Supprimé (faux) : Sinon, s'il est composé de 2 chiffres et qu'il est suivi d'une commune, il est considéré comme un code de département.
     * 4. S'il est suivi d'un ème puis d'une clé, il s'agit d'un numéro de clé.
     * 5. Sinon, s'il s'agit d'une alternance d'articles, de répétitions, et de numéros suivis d'un type de voie, il s'agit d'un numéro d'adresse.
     * 6. S'il suit un nom de commune, il est considéré comme un numéro d'arrondissement
     * 7. Sinon, il s'agit d'un numéro comme un autre.
     *
     * Le cas 2 précède le cas 3 car sinon les numéros de clés seraient considérés comme des code de département.
     */
    private void resoudAmbiguitesNumero(ArrayList<RefCle> elements, RefCle element, int i, CategorieMot categorie) {
        // Cas 1
        if (element.obtientMot().length() == 5) {
            element.definitCategorieMot(CategorieMot.CodePostal);
        } else {
            // Cas 2
            // Si le numéro est précédé d'une clé, qui n'est pas précédé d'un ème
            int indiceElementPrecedent = indiceElementPrecedent(elements, i);
            if (indiceElementPrecedent != -1 && trouveElementDeCategorie(elements, indiceElementPrecedent,
                    CategorieMot.Cle) != -1) {
                indiceElementPrecedent = indiceElementPrecedent(elements, indiceElementPrecedent);

                if (indiceElementPrecedent == -1 || trouveElementDeCategorie(elements, indiceElementPrecedent,
                        CategorieMot.Eme) == -1) {
                    // Il s'agit alors d'un numéro de clé.
                    element.definitCategorieMot(CategorieMot.NumeroCleApres);
                    return;
                }
            }
            int indiceElementSuivant = indiceElementSuivant(elements, i);
            /*
            // Cas 3
            // Si le numéro est de deux chiffres et suivi d'un nom de ville, il s'agit d'un code de département.
            if (element.obtientMot().length()==2 && indiceElementSuivant!=-1)
            {
            if (trouveElementDeCategorie(elements,indiceElementSuivant,CategorieMot.Ville)!=-1)
            {
            element.definitCategorieMot(CategorieMot.CodeDepartement);
            continue;
            }
            }*/
            // Cas 4
            // Si le numéro est suivi d'un ème, puis d'une clé, il s'agit d'un numéro de clé.
            if (indiceElementSuivant != -1 && trouveElementDeCategorie(elements, indiceElementSuivant,
                    CategorieMot.Eme) != -1) {
                indiceElementSuivant = indiceElementSuivant(elements, indiceElementSuivant);
                if (indiceElementSuivant != -1 && trouveElementDeCategorie(elements, indiceElementSuivant,
                        CategorieMot.Cle) != -1) {
                    element.definitCategorieMot(CategorieMot.NumeroCleAvant);
                    return;
                }
            }

            int current = i;
            // Cas 5
            // Si les éléments qui suivent sont des numéros, des répétitions ou des articles, puis un type de voie,
            // il s'agit d'un numéro d'adresse.
            do {
                current++;
                if (current < elements.size()) {
                    categorie = elements.get(current).obtientCategorieMot();
                }
            } while (current < elements.size() && (categorie == CategorieMot.Numero || categorie == CategorieMot.Repetition || categorie == CategorieMot.Article));

            // Si un type de voie est trouvé après tout cela,
            if (current < elements.size()) {
                if (elements.get(current).obtientCategorieMot() == CategorieMot.TypeDeVoie) {
                    // lui et les numéros suivants sont donc des numéros d'adresse.
                    for (int j = i; j < current; j++) {
                        if (elements.get(j).obtientCategorieMot() == CategorieMot.Numero) {
                            elements.get(j).definitCategorieMot(CategorieMot.NumeroAdresse);
                        }
                    }
                    return;
                }
            }

            // Cas 6, s'il suit un nom de commune, il s'agit d'un numéro d'arrondissement.
            indiceElementPrecedent = indiceElementPrecedent(elements, i);
            if (indiceElementPrecedent != -1 && trouveElementDeCategorie(elements, indiceElementPrecedent,
                    CategorieMot.Ville) != -1) {
                element.definitCategorieMot(CategorieMot.NumeroArrondissement);
                return;
            }

        // Sinon, il s'agit d'un numéro ordinaire.
        }
    }

    /**
     * Résoud les ambiguités des éléments qui sont contenus dans d'autres 
     */
    private void resoudAmbiguitesInclusion(ArrayList<RefCle> elements, RefCle element, int i) {
        int indice = i;
        // Cherche le premier élément de même index
        int index = elements.get(indice).obtientIndex();
        int lastindice;
        do {
            lastindice = indice;
            indice--;
        } while (indice >= 0 && elements.get(indice).obtientIndex() == index);

        int startindex = element.obtientIndex();
        int endindex = startindex + element.obtientMot().length();

        for (int j = lastindice; j < elements.size() && elements.get(j).obtientIndex() < endindex; j++) {
            if (j != i) {
                if (j < i) {
                    i--;
                }
                elements.remove(j);
                j--;
            }
        }
    }

    /**
     * Résoud les ambiguités concernant les articles.
     * Les cas gérés sont :
     * - les articles qui peuvent être des clés :
     * il ne s'agit pas d'un article s'il n'est pas précédé d'un numéro ou d'une répétition
     * - les articles qui peuvent être des répétitions
     * non géré
     * WA 01 2012 Pays
     * - les articles au meme niveau qu'un pays ou une commune : alors, le pays ou la commune prevaut
     * @param elements
     * @param element
     * @param indiceprecedent_0
     */
    private int resoudAmbiguitesArticle(ArrayList<RefCle> elements, RefCle element, int indiceprecedent_0, int i) {
        if (trouveElementDeCategorie(elements, i, CategorieMot.Cle) != -1) {
            if (trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.Numero) == -1 &&
                    trouveElementDeCategorie(elements, indiceprecedent_0, CategorieMot.Repetition) == -1) {
                elements.remove(i);
                i--;
                return i;
            }
        }
        // WA 01 2012 Pays
        if ((trouveElementDeCategorie(elements, i, CategorieMot.Ville) != -1) || (trouveElementDeCategorie(elements, i, CategorieMot.Pays) != -1)) {
            elements.remove(i);
            i--;
            return i;
        }

        // sinon, il s'agit bien d'un article et les références suivantes peuvent être supprimées.
        supprimeElementMemeIndex(i, elements);

        return i;
    }

    /**
     * Résoud les ambiguités pour les éléments de la chaine spécifiée.<br>
     * Les ambiguités possibles sont:
     * <ul>
     * <li>Clés qui peuvent être des types de voie.</li>
     * <li>Communes qui peuvent faire partie d'une voie.</li>
     * <li>Les types de voies qui peuvent faire partie d'une commune.</li>
     * <li>Les clés qui peuvent faire partie d'une commune.</li>
     * <li>Les articles ou autres éléments restant qui font parti des noms de communes ou des types de voies.</li>
     * </ul>
     * Les ambiguités suivantes ne sont pas résolues suite à cette méthode:
     * <ul>
     * <li>Les ambiguités sur les articles ne sont pas résolues car elles ne posent pas de problème
     * pour la restructuration de l'adresse.</li>
     * <li>Ambiguités des numéros, traités à part.</li>
     * </ul>
     * @param chaine
     * @param elements liste d'éléments triés qui ont été trouvés préalablement dans la chaine.
     * @param codes_departement s'il est vide, les codes de département par défaut sont utilisés.
     */
    private void resoudAmbiguites(String chaine, ArrayList<RefCle> elements, ArrayList<String> codes_departement, Connection connection)
            throws SQLException {
        // parcours les éléments trouvés.
        // Les numéros sont traités lors d'un deuxième passage car ils nécessitent d'avoir
        // traité les noms de commune.
        // idem pour les articles.
        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            CategorieMot categorie = element.obtientCategorieMot();

            String contexte = obtientContexte(elements, i, true, true);
            String contexte_apres = obtientContexte(elements, i, false, true);

            int indiceprecedent_0 = indiceElementPrecedent(elements, i);
            Mot m = element.obtientCle();

            // Ambiguité pour les termes non classés.
            // Il peut en effet s'agir de libellés de voie.
            if (categorie == CategorieMot.Autre) {
                resoudAutresAmbiguites(elements, element, indiceprecedent_0);
            } // ambiguité de type article : peut-il être confondu avec une clé ?
            else if (categorie == CategorieMot.Article) {
                i = resoudAmbiguitesArticle(elements, element, indiceprecedent_0, i);
            } // Résoud l'ambiguité des types de voies : fait-il parti d'un nom de commune?
            else if (categorie == CategorieMot.TypeDeVoie) {
                i = resoudAmbiguitesTypeDeVoie(elements, element, indiceprecedent_0, contexte, codes_departement, connection, i);
            } // Ambiguité sur les clés: s'agit-il d'une clé, d'un type de voie, ou du nom d'une voie?
            else if (categorie == CategorieMot.Cle) {
                i = resoudAmbiguitesCles(elements, element, indiceprecedent_0, m, contexte, contexte_apres, codes_departement, connection, i);
            } // Résoud l'ambiguité de type ville : S'agit-il d'un nom de commune ou d'un nom de voie?
            else if (categorie == CategorieMot.CodeDepartement) {
                resoudAmbiguitesCodeDepartement(element, contexte, codes_departement, connection);
            }
        }

        // Effectue le deuxième passage pour les numéros.
        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            CategorieMot categorie = element.obtientCategorieMot();

            if (categorie == CategorieMot.Numero) {
                resoudAmbiguitesNumero(elements, element, i, categorie);
            }
        }
    }

    /**
     * <ul>
     * <li>Résolution des numéros : adresse, codes postal, numéro d'arrondissement, numéro de clé...</li>
     * </ul>
     * @param elements
     */
    protected void resoudAmbiguitesNumero(ArrayList<RefCle> elements) {
        // Effectue le deuxième passage pour les numéros.
        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            CategorieMot categorie = element.obtientCategorieMot();

            if (categorie == CategorieMot.Numero ||
                    categorie == CategorieMot.NumeroAdresse ||
                    categorie == CategorieMot.NumeroCleApres ||
                    categorie == CategorieMot.NumeroCleAvant) {
                resoudAmbiguitesNumero(elements, element, i, categorie);
            }
        }
    }

    /**
     * Résoud les ambiguités plus complexes pour les éléments de la chaine spécifiée.<br>
     * Les ambiguités possibles sont:
     * <ul>
     * <li>Communes qui peuvent faire partie d'une voie.</li>
     * </ul>
     * Les ambiguités suivantes ne sont pas résolues suite à cette méthode:
     * <ul>
     * <li>Les ambiguités sur les articles ne sont pas résolues car elles ne posent pas de problème
     * pour la restructuration de l'adresse.</li>
     * </ul>
     * @param chaine
     * @param elements liste d'éléments triés qui ont été trouvés préalablement dans la chaine.
     * @param codes_departement s'il est vide, les codes de département par défaut sont utilisés.
     */
    private void resoudAmbiguitesComplexe(String chaine, ArrayList<RefCle> elements, ArrayList<String> codes_departement,
            Connection connection) throws SQLException {
        // parcours les éléments trouvés.
        // Les numéros sont traités lors d'un deuxième passage car ils nécessitent d'avoir
        // traité les noms de commune.
        // idem pour les articles.
        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            CategorieMot categorie = element.obtientCategorieMot();

            if (categorie == CategorieMot.Ville) {
                String contexte = obtientContexte(elements, i, true, true);
                String contexte_apres = obtientContexte(elements, i, false, true);

                int indiceprecedent_0 = indiceElementPrecedent(elements, i);
                Mot m = element.obtientCle();

                i = resoudAmbiguitesVille(elements, element, indiceprecedent_0, contexte, codes_departement, connection, i);
            }
        }

        // effectue un deuxième passage pour les éléments qui font parti d'autres éléments.
        // les éléments qui sont prioritaires sont : les villes, les types de voies et les pays.
        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            CategorieMot categorie = element.obtientCategorieMot();
            if ((categorie == CategorieMot.Ville) || (categorie == CategorieMot.TypeDeVoie) || (categorie == CategorieMot.Pays)) // WA 01/2012 PAys
            {
                resoudAmbiguitesInclusion(elements, element, i);
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            resoudAmbiguitesInclusion(elements, element, i);
        }
    }

    /**
     * Resoud les ambiguites concernant les pays : pays dans voie, pays dans commune.
     * @param foundPays
     * @param chaine
     * @param elements
     * @param connection
     */
    private void resoudAmbiguitesPays(List<RefPays> foundPays, String chaine, ArrayList<RefCle> elements,
            ArrayList<String> codes_departement, Connection connection) throws SQLException {
        for (int i = 0; i < elements.size(); i++) {
            RefCle element = elements.get(i);
            CategorieMot categorie = element.obtientCategorieMot();

            if (categorie == CategorieMot.Pays) {
                String contexte = obtientContexte(elements, i, true, true);
                int indiceprecedent_0 = indiceElementPrecedent(elements, i);
                i = resoudAmbiguitesPays(elements, element, indiceprecedent_0, contexte, codes_departement, connection, i);
            }
        }
    }

    /**
     * Recherche les compléments après la clé spécifiée jusque la clé, la ville, le code postal, le code département, le numéro d'adresse, ou le numéro de clé qui suit.<br>
     * Les éléments correspondants sont masqués et deviennent de la catégorie Complément.
     * @param indiceCle
     * @param ligne
     * @param elements
     * @param sb le masque
     */
    /*    private void rechercheComplementsApres(int indiceCle,String ligne,ArrayList<RefCle> elements,StringBuilder sb)
    {
    RefCle cle = elements.get(indiceCle);
    CategorieMot categorie = CategorieMot.Vide;
    int indiceNumeroSuivant = indiceCle, lastindice;
    do
    {
    lastindice=indiceNumeroSuivant;
    indiceNumeroSuivant=indiceElementSuivant(elements,indiceNumeroSuivant);
    if (indiceNumeroSuivant!=-1)
    {
    categorie=elements.get(indiceNumeroSuivant).obtientCategorieMot();
    if (!(categorie==CategorieMot.Cle||
    categorie==CategorieMot.Ville||
    categorie==CategorieMot.CodeDepartement||
    categorie==CategorieMot.CodePostal||
    categorie==CategorieMot.CodePostalRestant||
    categorie==CategorieMot.VilleRestante||
    categorie==CategorieMot.NumeroAdresse||
    categorie==CategorieMot.NumeroCleAvant||
    categorie==CategorieMot.TypeDeVoie))
    elements.get(indiceNumeroSuivant).definitCategorieMot(CategorieMot.Complement);
    }
    }
    while (indiceNumeroSuivant!=-1&&
    !(categorie==CategorieMot.Cle||
    categorie==CategorieMot.Ville||
    categorie==CategorieMot.CodeDepartement||
    categorie==CategorieMot.CodePostalRestant||
    categorie==CategorieMot.VilleRestante||
    categorie==CategorieMot.CodePostal||
    categorie==CategorieMot.NumeroAdresse||
    categorie==CategorieMot.NumeroCleAvant||
    categorie==CategorieMot.TypeDeVoie));
    int startindex=cle.obtientIndex()+cle.obtientMot().length();
    int endindex;
    if (indiceNumeroSuivant!=-1)
    {
    endindex=elements.get(indiceNumeroSuivant).obtientIndex()-1; // un espace est présent devant cet élément!
    }
    else
    {
    endindex=ligne.length();
    }
    masque(sb,startindex,endindex);
    cle.definitComplementApres(ligne.substring(startindex,endindex));
    }*/
    /**
     * Recherche les compléments après la clé spécifiée jusque la clé, la ville, le code postal, le code département, le pays
     * ou le numéro d'adresse qui suit.<br>
     * Les éléments correspondants sont masqués et deviennent de la catégorie Complément.
     * @param indiceCle
     * @param ligne
     * @param elements
     * @param sb le masque
     */
    private void rechercheComplementsApresCle(int indiceCle, String ligne, ArrayList<RefCle> elements, StringBuilder sb) {
        RefCle cle = elements.get(indiceCle);
        CategorieMot categorie = CategorieMot.Vide;
        int indiceNumeroSuivant = indiceCle, lastindice;
        do {
            lastindice = indiceNumeroSuivant;
            indiceNumeroSuivant = indiceElementSuivant(elements, indiceNumeroSuivant);
            if (indiceNumeroSuivant != -1) {
                categorie = elements.get(indiceNumeroSuivant).obtientCategorieMot();
                if (!(categorie == CategorieMot.Cle ||
                        categorie == CategorieMot.Ville ||
                        categorie == CategorieMot.CodeDepartement ||
                        categorie == CategorieMot.CodePostal ||
                        categorie == CategorieMot.CodePostalRestant ||
                        categorie == CategorieMot.VilleRestante ||
                        categorie == CategorieMot.NumeroAdresse ||
                        categorie == CategorieMot.TypeDeVoie ||
                        categorie == CategorieMot.NumeroCleAvant ||
                        categorie == CategorieMot.Pays)) // WA 01 2012 Pays
                {
                    elements.get(indiceNumeroSuivant).definitCategorieMot(CategorieMot.Complement);

                }
            }
        } while (indiceNumeroSuivant != -1 &&
                !(categorie == CategorieMot.Cle ||
                categorie == CategorieMot.Ville ||
                categorie == CategorieMot.CodeDepartement ||
                categorie == CategorieMot.CodePostal ||
                categorie == CategorieMot.CodePostalRestant ||
                categorie == CategorieMot.VilleRestante ||
                categorie == CategorieMot.NumeroAdresse ||
                categorie == CategorieMot.TypeDeVoie ||
                categorie == CategorieMot.NumeroCleAvant ||
                categorie == CategorieMot.Pays));   // WA 01 2012 Pays

        int startindex = cle.obtientIndex() + cle.obtientMot().length();
        int endindex;
        if (indiceNumeroSuivant != -1) {
            endindex = elements.get(indiceNumeroSuivant).obtientIndex() - 1; // -1 pour l'espace
        } else {
            endindex = ligne.length();
        }

        masque(sb, startindex, endindex);
        cle.definitComplementApres(ligne.substring(startindex, endindex));
    }

    /**
     * Recherche un complément formant un nom de Ville après une clé.<br>
     * Ce complément est masqué, et les éléments correspondant deviennent de la catégorie Complément.
     * @param indiceCle
     * @param ligne
     * @param elements
     */
    private void rechercheComplementVilleApres(int indiceCle, String ligne, ArrayList<RefCle> elements, StringBuilder sb) {
        RefCle cle = elements.get(indiceCle);
        CategorieMot categorie = CategorieMot.Vide;
        int indiceNumeroSuivant = indiceCle, lastindice;
        do {
            if (categorie == CategorieMot.Ville) // La ville est supprimée ici, car le numéro d'indice doit être modifié, ce qui perturbe le reste de l'algorithme.
            {
                elements.remove(indiceNumeroSuivant);
                indiceNumeroSuivant--;
            }
            lastindice = indiceNumeroSuivant;
            indiceNumeroSuivant = indiceElementSuivant(elements, indiceNumeroSuivant);
            if (indiceNumeroSuivant != -1) {
                RefCle clesuivante = elements.get(indiceNumeroSuivant);
                categorie = clesuivante.obtientCategorieMot();
                if (!(categorie == CategorieMot.Cle ||
                        categorie == CategorieMot.CodeDepartement ||
                        categorie == CategorieMot.CodePostal ||
                        categorie == CategorieMot.CodePostalRestant ||
                        categorie == CategorieMot.VilleRestante ||
                        categorie == CategorieMot.NumeroAdresse ||
                        categorie == CategorieMot.NumeroCleAvant ||
                        categorie == CategorieMot.TypeDeVoie ||
                        categorie == CategorieMot.Pays)) // WA 01 2012 Pays
                {
                    elements.get(indiceNumeroSuivant).definitCategorieMot(CategorieMot.Complement);
                }
            }
        } while (indiceNumeroSuivant != -1 &&
                !(categorie == CategorieMot.Cle ||
                categorie == CategorieMot.CodeDepartement ||
                categorie == CategorieMot.CodePostal ||
                categorie == CategorieMot.CodePostalRestant ||
                categorie == CategorieMot.VilleRestante ||
                categorie == CategorieMot.NumeroAdresse ||
                categorie == CategorieMot.NumeroCleAvant ||
                categorie == CategorieMot.TypeDeVoie ||
                categorie == CategorieMot.Pays));                  // WA 01 2012 Pays

        int startindex = cle.obtientIndex() + cle.obtientMot().length();
        int endindex;
        if (indiceNumeroSuivant != -1) {
            endindex = elements.get(indiceNumeroSuivant).obtientIndex() - 1; // -1 pour l'espace qui le précède.
        } else {
            endindex = ligne.length();
        }

        masque(sb, startindex, endindex);
        cle.definitComplementApres(ligne.substring(startindex, endindex));
    }

    /**
     * Remplace les caractères de l'intervalle spécifié [start,end[ par des espaces.
     * @param ligne
     * @param start
     * @param end
     * @return
     */
    private void masque(StringBuilder sb, int start, int end) {
        for (int i = start; i < end; i++) {
            sb.setCharAt(i, ' ');
        }
    }

    /**
     * Repartit les complements pour les types de voies.
     */
    private void repartitComplementsTypeDeVoie(StringBuilder sb, RefCle cle, int i, ArrayList<RefCle> elements, CategorieMot categorie,
            String ligne) {
        masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length()); // Les éléments utilisés sont marqués au fur et à mesure avec des espaces.

        // Recherche les éléments numéro d'adresse, répétition, et article qui le précédent.
        // Au moins un numéro d'adresse doit être présent.
        int indiceNumeroPrecedent = i, lastindice;
        boolean numerodadressepresent = false;
        do {
            lastindice = indiceNumeroPrecedent;
            indiceNumeroPrecedent = indiceElementPrecedent(elements, indiceNumeroPrecedent);
            if (indiceNumeroPrecedent != -1) {
                categorie = elements.get(indiceNumeroPrecedent).obtientCategorieMot();
                if (categorie == CategorieMot.NumeroAdresse || categorie == CategorieMot.Repetition || categorie == CategorieMot.Article) {
                    elements.get(indiceNumeroPrecedent).definitCategorieMot(CategorieMot.Complement);
                }
                if (categorie == CategorieMot.NumeroAdresse) {
                    numerodadressepresent = true;
                }
            }
        } while (indiceNumeroPrecedent != -1 && (categorie == CategorieMot.NumeroAdresse || categorie == CategorieMot.Repetition || categorie == CategorieMot.Article));

        if (lastindice != i && numerodadressepresent) {
            int startindex = elements.get(lastindice).obtientIndex();
            int endindex = cle.obtientIndex();

            masque(sb, startindex, endindex);

            cle.definitComplementAvant(ligne.substring(startindex, endindex));
        }

        // Recherche les compléments qui suivent jusqu'à la clé, ville, code postal, code département, numéro d'adresse qui suit.
        rechercheComplementsApresCle(i, ligne, elements, sb);
    }

    /**
     * Dote un libellé de son complément.
     */
    private void repartitComplementLibelle(StringBuilder sb, RefCle cle, int i, ArrayList<RefCle> elements, CategorieMot categorie,
            String ligne) {
        masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length()); // Les éléments utilisés sont marqués au fur et à mesure avec des espaces.

        // Recherche les éléments numéro d'adresse, répétition, et article qui le précédent.
        // Au moins un numéro d'adresse doit être présent.
        int indiceNumeroPrecedent = i, lastindice;
        boolean numerodadressepresent = false;
        do {
            lastindice = indiceNumeroPrecedent;
            indiceNumeroPrecedent = indiceElementPrecedent(elements, indiceNumeroPrecedent);
            if (indiceNumeroPrecedent != -1) {
                categorie = elements.get(indiceNumeroPrecedent).obtientCategorieMot();
                if (categorie == CategorieMot.Numero || categorie == CategorieMot.Repetition || categorie == CategorieMot.Article) {
                    elements.get(indiceNumeroPrecedent).definitCategorieMot(CategorieMot.Complement);
                }
                if (categorie == CategorieMot.Numero) {
                    numerodadressepresent = true;
                }
            }
        } while (indiceNumeroPrecedent != -1 && (categorie == CategorieMot.Numero || categorie == CategorieMot.Repetition || categorie == CategorieMot.Article));

        if (lastindice != i && numerodadressepresent) {
            int startindex = elements.get(lastindice).obtientIndex();
            int endindex = cle.obtientIndex();

            masque(sb, startindex, endindex);

            cle.definitComplementAvant(ligne.substring(startindex, endindex));
        }

        // Recherche les compléments qui suivent jusqu'à la clé, ville, code postal, code département, numéro d'adresse qui suit.
        rechercheComplementsApresCle(i, ligne, elements, sb);
    }

    /**
     * Dote une clé de ses compléments:
     * <ul>
     * <li>Avant avec une mention Eme</li>
     * <li>Après</li>
     * </ul>
     */
    private int repartitComplementCle(StringBuilder sb, RefCle cle, ArrayList<RefCle> elements, int i, String ligne) {
        masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());

        // Recherche le complément avant : numéro et ème.
        int indiceElementPrecedent = indiceElementPrecedent(elements, i);
        if (indiceElementPrecedent != -1 && elements.get(indiceElementPrecedent).obtientCategorieMot() == CategorieMot.Eme) {
            int indiceEme = indiceElementPrecedent;
            indiceElementPrecedent = indiceElementPrecedent(elements, indiceElementPrecedent);

            if (indiceElementPrecedent != -1 && elements.get(indiceElementPrecedent).obtientCategorieMot() == CategorieMot.NumeroCleAvant) {
                RefCle numero = elements.get(indiceElementPrecedent);
                int startindex = numero.obtientIndex();
                int endindex = cle.obtientIndex();

                masque(sb, startindex, endindex);

                // Le numéro passe alors après la clé et le ème est effacé.
                numero.definitCategorieMot(CategorieMot.Complement);
                cle.definitComplementApres(numero.obtientMot());
                elements.remove(indiceEme);
                i--;
                return i;

            }
        }
        // Recherche le complément après : jusque la clé suivante.
        rechercheComplementsApresCle(i, ligne, elements, sb);

        return i;
    }

    /**
     * Dote une ville de ses compléments
     * @return
     */
    private void repartitComplementVille(StringBuilder sb, RefCle cle, ArrayList<RefCle> elements, int i, String ligne) {
        masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());

        int indiceElementSuivant = indiceElementSuivant(elements, i);
        if (indiceElementSuivant != -1) {
            RefCle ref = elements.get(indiceElementSuivant);
            // Vérifie si un code postal la suit.
            if (ref.obtientCategorieMot() == CategorieMot.CodePostal) {
                masque(sb, ref.obtientIndex(), ref.obtientIndex() + ref.obtientMot().length());
                cle.definitComplementAvant(ref.obtientMot());
                elements.remove(indiceElementSuivant); // Dans ce cas, le code postal est déjà traité, et donc supprimé.

            } // ou vérifie si un numéro d'arrondissement la suit.
            else if (elements.get(indiceElementSuivant).obtientCategorieMot() == CategorieMot.NumeroArrondissement) {
                ref = elements.get(indiceElementSuivant);
                masque(sb, ref.obtientIndex(), ref.obtientIndex() + ref.obtientMot().length());

                // Dans ce cas, le numéro est placé après.
                cle.definitComplementApres(ref.obtientMot());

                elements.remove(indiceElementSuivant);
            } else {
                // Sinon, les éléments Autre sont pris comme suivant.
                rechercheComplementsApresCle(i, ligne, elements, sb);
            }
        }
    }

    /**
     * Répartit les compléments de la ligne entre les différentes clés, type de voie, communes ou code postaux de la ligne.<br>
     * Les éléments pris en compte sont:
     * <ul>
     * <li>Le type de voie, avec les numéros d'adresses, articles et répétitions qui le précédent, ainsi que les éléments qui le suivent
     * jusqu'à la clé, ville, code postal, code departement, ou numéro d'adresse qui le suit.</li>
     * <li>Le libellé, avec les numéros d'adresses, articles et répétitions qui le précédent
     * jusqu'à la clé, ville, code postal, code departement, ou numéro d'adresse qui le suit.</li>
     * <li>La clé, avec le numéro et ème qui le précéde ou jusque la clé, la ville, le code postal, le code département, le numéro d'adresse qui suit.</li>
     * <li>La ville, si un code postal ou un code departement ne la précéde pas, avec le code postal éventuel qui la suit, 
     * si il n'est pas suivi d'un nom de ville.</li>
     * <li>Le code postal ou le code département, avec la ville éventuelle qui la suit.</li>
     * </ul>
     * @param ligne
     * @param elements
     * @return les éléments qui ne sont pas répartis, avec des espaces superflus.
     */
    private String repartitComplements(String ligne, ArrayList<RefCle> elements) {
        StringBuilder sb = new StringBuilder(ligne);

        // Pour chaque élément,
        for (int i = 0; i < elements.size(); i++) {
            RefCle cle = elements.get(i);
            CategorieMot categorie = cle.obtientCategorieMot();
            if (categorie == CategorieMot.TypeDeVoie) {
                repartitComplementsTypeDeVoie(sb, cle, i, elements, categorie, ligne);
            } else if (categorie == CategorieMot.Libelle) {
                repartitComplementLibelle(sb, cle, i, elements, categorie, ligne);
            } else if (categorie == CategorieMot.Cle) {
                i = repartitComplementCle(sb, cle, elements, i, ligne);
            } else if (categorie == CategorieMot.VilleRestante) {
                masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());
            } else if (categorie == CategorieMot.CodePostalRestant) {
                masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());
            } else if (categorie == CategorieMot.Ville) {
                repartitComplementVille(sb, cle, elements, i, ligne);
            } else if (categorie == CategorieMot.CodePostal) {
                masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());
                // Si une ville suit, elle est supprimée.
                rechercheComplementVilleApres(i, ligne, elements, sb);
            } else if (categorie == CategorieMot.CodeDepartement) {
                masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());
                // Si une ville suit, elle est supprimée.
                rechercheComplementVilleApres(i, ligne, elements, sb);
            } // WA 01/2012 Pays
            else if (categorie == CategorieMot.Pays) {
                masque(sb, cle.obtientIndex(), cle.obtientIndex() + cle.obtientMot().length());
            }
        }

        return Algos.supprimeRepetitionEspace(sb.toString());
    }

    private void appendCle(StringBuilder sb, RefCle cle, boolean apres) {
        StringBuilder toappend = new StringBuilder();
        Algos.appendWithSpace(toappend, cle.obtientComplementAvant(), true);
        Algos.appendWithSpace(toappend, cle.obtientMot(), true);
        Algos.appendWithSpace(toappend, cle.obtientComplementApres(), true);

        Algos.appendWithSpace(sb, toappend, apres);
    }

    /**
     * Replace les éléments dans la ligne correspondante.<br>
     * Les 6 premiers éléments restants sont conservés dans leur ligne, les autres
     * sont placés sur la ligne 3.
     * @return
     */
    private String[] reconstitue(String[] restants, ArrayList<ArrayList<RefCle>> elements, ArrayList<String> codes_departements_finaux,
            RefCle foundPays) {
        // WA 01 2012 Pays
        int nbLignesAddr = ((foundPays != null) ? 7 : 6);

        StringBuilder[] sbs = new StringBuilder[nbLignesAddr];

        // Crée les string builder des 6 première lignes,
        for (int i = 0; i < nbLignesAddr; i++) {
            sbs[i] = new StringBuilder();
        }

        // Replace chaque élément dans sa ligne,
        for (int j = 0; j < elements.size(); j++) {
            ArrayList<RefCle> cles = elements.get(j);
            for (int i = 0; i < cles.size(); i++) {
                boolean apres = true;

                int ligne = -1;
                RefCle cle = cles.get(i);
                CategorieMot categorie = cle.obtientCategorieMot();
                // Certaines clés disposent d'un traitement différent :
                // le code postal et le code département sont placés en début de ligne.
                if (categorie == CategorieMot.Cle) {
                    ligne = cle.obtientLigneDeCle(CategorieMot.Cle);
                    if (ligne == -1) // tentative de rattrapage des clés non référencées.
                    {
                        // Si la clé est un type de voie potentiel, il est positionné sur la ligne 3 (complement d'adresse)
                        if (cle.estDeLaCategorie(CategorieMot.TypeDeVoie)) {
                            ligne = 3;

                        }
                        ligne = j + 1; // Si la clé n'a pas de ligne, elle est conservée sur la même
                    }
                } else if (categorie == CategorieMot.TypeDeVoie) {
                    ligne = 4;
                } else if (categorie == CategorieMot.Libelle) {
                    ligne = 4;
                } else if (categorie == CategorieMot.PortionVille) {
                    ligne = 6;
                } else if (categorie == CategorieMot.Ville) {
                    ligne = 6;
                } else if (categorie == CategorieMot.CodePostal) {
                    ligne = 6;
                    apres = false;
                } else if (categorie == CategorieMot.CodeDepartement) {
                    ligne = 6;
                    apres = false;
                } else if (categorie == CategorieMot.CodePostalRestant) {
                    ligne = 5;
                    apres = false;
                } else if (categorie == CategorieMot.VilleRestante) {
                    ligne = 5;
                } // WA 01 2012 Pays
                else if ((foundPays != null) && (categorie == CategorieMot.Pays)) {
                    ligne = 7;
                }

                if (ligne >= 1 && ligne <= nbLignesAddr) {
                    appendCle(sbs[ligne - 1], cle, apres);
                }
            }
        }

        // Ajoute les restants en bout de ligne
        for (int i = 0; i < Math.min(6, restants.length); i++) {
            Algos.appendWithSpace(sbs[i], restants[i], true);
        }
        // et ajoute à la 3 ème ligne les autres.
        for (int i = 6; i < restants.length; i++) {
            Algos.appendWithSpace(sbs[2], restants[i], true);
        }

        // WA 01/2012 Pays : Ajout du pays par defaut si non present
        if ((foundPays != null) && (sbs[6].length() == 0)) {
            appendCle(sbs[6], foundPays, true);
        }

        // Formate le résultat.
        String[] res;
        if (codes_departements_finaux == null) {
            res = new String[nbLignesAddr];

        } else {
            res = new String[nbLignesAddr + 1];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < codes_departements_finaux.size(); i++) {
                if (i > 0) {
                    sb.append(',');

                }
                sb.append(codes_departements_finaux.get(i));
            }
            res[nbLignesAddr] = sb.toString();
        }
        for (int i = 0; i < nbLignesAddr; i++) {
            res[i] = sbs[i].toString();
        }

        return res;
    }

    /**
     * Determine si le pays trouve est le pays par defaut (a priori FRANCE).
     * @param elements
     * @return
     */
    private boolean isPaysTheDefaultOne(RefCle pays) {
        if ((pays != null) && (pays.obtientCategorieMot() == CategorieMot.Pays) && (pays.obtientMot().equals(params.obtientPaysParDefaut()))) {
            return true;
        }
        return false;
    }

    /**
     * Retourne le pays de l'adresse.
     * Si on ne gere pas les pays, retourne null
     * Sinon, retourne soit le pays trouve, soit le pays par defaut.
     * Prerequis : l'analyse syntaxique a ete faite et 0 ou 1 pays a ete trouve.
     * @param elements : les elements trouves dans l'adresse.
     * @param gererPays : indique si on doit gerer les pays ou pas.
     * @return le pays
     */
    private RefCle obtientPaysTrouve(ArrayList<ArrayList<RefCle>> elements, boolean gererPays) {
        if (!gererPays) {
            return null;
        }

        for (ArrayList<RefCle> lineElts : elements) {
            for (RefCle elt : lineElts) {
                if ((elt.obtientCategorieMot() == CategorieMot.Pays)) {
                    return elt;
                }
            }
        }
        // Si on n'a pas trouve de pays (et que gererPays est vrai) -> pays par defaut
        RefCle defaultCountry = new RefCle(params.obtientPaysParDefaut(), 0, CategorieMot.Pays);
        return defaultCountry;
    }

    /**
     * Reconstitue une adresse etrangere.
     * Les lignes sont laissees telles quelles, seul le pays est extrait.
     * @param restants
     * @param elements
     * @return
     */
    private String[] reconstitueAdresseEtrangere(String[] lignes, RefCle pays, boolean ajouter_departements_finaux) {
        int resultNbLines = ajouter_departements_finaux ? 8 : 7;
        String[] resultTab = new String[resultNbLines];
        String ligneOriginalePays = ((pays.obtientChaineOriginale() == null) || (pays.obtientChaineOriginale().length() == 0)) ? null : pays.obtientChaineOriginale();

        for (int i = 0; i < lignes.length; i++) {
            String lign = lignes[i];
            if ((ligneOriginalePays != null) && (lign.equals(ligneOriginalePays))) {
                int indexDebut = pays.obtientIndex();
                int indexFin = indexDebut + pays.obtientMot().length();

                StringBuilder lineWithoutCountry = new StringBuilder();
                String startPiece = (indexDebut > 0) ? lign.substring(0, indexDebut - 1) : "";
                String endPiece = (indexFin < lign.length()) ? lign.substring(indexFin, lign.length()) : "";
                lineWithoutCountry.append(startPiece);
                Algos.appendWithSpace(lineWithoutCountry, endPiece.trim(), true);
                resultTab[i] = lineWithoutCountry.toString().trim();
            } else {
                resultTab[i] = lign;
            }
        }
        for (int i = 0; i < resultNbLines; i++) {
            if (resultTab[i] == null) {
                resultTab[i] = "";
            }
        }
        // S'il y a un contenu en ligne 7, il est deplace en ligne 4 (a la fin)
        if (resultTab[6].length() != 0) {
            resultTab[4] = resultTab[4] + ((resultTab[4].length() > 0) ? " " : "") + resultTab[6];
        }
        resultTab[6] = pays.obtientMot();
        return resultTab;
    }

    /**
     * Réordonne les éléments de la ligne 6 : le code postal ou le code département est placé devant les autres.
     * @param elements
     */
    private void reordonne(ArrayList<RefCle> elements) {
        // Cherche les codes postaux et code département
        for (int i = 1; i < elements.size(); i++) {
            RefCle cle = elements.get(i);
            CategorieMot categorie = cle.obtientCategorieMot();

            if (categorie == CategorieMot.CodePostal || categorie == CategorieMot.CodeDepartement) {
                elements.remove(i);
                elements.add(0, cle);
            }
        }
    }

//    /**
//     * Cherche les elements qui pourraient être des codes de département.<br>
//     * Parmi ceux-ci:
//     * <ul>
//     *   <li>2 chiffres non précédés d'une clé, d'un type de voie, un article ou de la lettre A, D, N, ou E (autoroute, départementale, etc...),</li>
//     *   <li>et non suivi de zéro ou plusieurs numéros suivis d'un article optionnel, d'une répétition optionnelle, d'un type de voie obligatoire
//     * et qui n'appartient pas à une date (jour ou année), ou qui n'est pas un arrondissement</li>
//     *   <li>5 chiffres (codes postaux)</li>
//     * </ul>
//     * @param elements les éléments qui pourraient être des codes de département.
//     * @return
//     */
//    private ArrayList<ArrayList<RefCle>> trouvePretendusCodesDepartements(ArrayList<ArrayList<RefCle>> elements,Connection connection) throws SQLException
//    {
//        ArrayList<ArrayList<RefCle>> cles = new ArrayList<ArrayList<RefCle>>();
//
//        for(int i=0;i<elements.size();i++)
//        {
//            ArrayList<RefCle> elementsi = elements.get(i);
//            ArrayList<RefCle> nombres = new ArrayList<RefCle>();
//
//            for(int j=0;j<elementsi.size();j++)
//            {
//                // NB: les éléments dans la liste sont encore des numéros. Il faut distinguer ici les codes postaux.
//                RefCle elementj = elementsi.get(j);
//                String motj = elementj.obtientMot();
//
//                if (elementj.obtientCategorieMot()==CategorieMot.Numero)
//                {
//                    int size = motj.length();
//                    if (size==2)
//                    {
//                        // Il faut vérifier:
//                        // 1. qu'une clé n'est pas présente devant (non précédée d'un ème)
//                        int index = indiceElementPrecedent(elementsi,j);
//                        if (index!=-1)
//                        {
//                            if (trouveElementDeCategorie(elementsi,index,CategorieMot.Cle)!=-1)
//                            {
//                                int index2=indiceElementPrecedent(elementsi,index);
//
//                                if (index2==-1||trouveElementDeCategorie(elementsi,index2,CategorieMot.Eme)==-1)
//                                    continue;
//                            }
//
//                        // 2. si le nombre n'est pas précédé d'un mois
//                            if (GestionDate.estUnMois(elementsi.get(index).obtientMot()))
//                                continue;
//
//                        // 3. qu'il n'est pas précédé par une lettre A, D, N ou E ou la chaine RN.
//                            if (trouveElementACDNERN(elementsi,index)!=-1)
//                                continue;
//
//                        // 4. qu'il n'est pas précédé par un type de voie
//                            if (trouveElementDeCategorie(elementsi,index,CategorieMot.TypeDeVoie)!=-1)
//                                continue;
//
//                        // 5. qu'il n'est pas précédé par un article
//                            if (trouveElementDeCategorie(elementsi,index,CategorieMot.Article)!=-1)
//                                continue;
//
//                        // 6. qui n'est pas un arrondissement
//                            String contexte = obtientContexte(elementsi,j,true,false);
//                            if (referentiel.resoudAmbiguiteArrondissement(motj,contexte,connection))
//                            {
//                                elementj.definitCategorieMot(CategorieMot.NumeroArrondissement);
//                                continue;
//                            }
//                        }
//
//                        // 7. qu'un ème n'est pas présent derrière
//                        index = indiceElementSuivant(elementsi,j);
//                        if (index!=-1)
//                        {
//                            if (trouveElementDeCategorie(elementsi,index,CategorieMot.Eme)!=-1)
//                                continue;
//
//                        // 8. qu'une succession de nombres, d'articles et de répétitions
//                        // suivie par un type de voie n'est pas présent derrière.
//                            int current = j;
//                            CategorieMot categorie = CategorieMot.Autre;
//                            do
//                            {
//                                current++;
//                                if (current<elementsi.size())
//                                {
//                                    categorie=elementsi.get(current).obtientCategorieMot();
//                                }
//                            } while(current<elementsi.size()&&(categorie==CategorieMot.Numero));
//
//                            if (categorie==CategorieMot.Repetition||categorie==CategorieMot.Article||categorie==CategorieMot.TypeDeVoie)
//                                continue;
//
//                        // 9. si le nombre est inférieur à 32, s'il n'est pas suivi d'un mois
//                            int valeur = Integer.parseInt(motj);
//                            if (valeur<32 && GestionDate.estUnMois(elementsi.get(index).obtientMot()))
//                                continue;
//                        }
//                        elementj.definitCategorieMot(CategorieMot.CodeDepartement);
//                        nombres.add(elementj);
//                    }
//                    else if (size==5)
//                    {
//                        elementj.definitCategorieMot(CategorieMot.CodePostal);
//                        nombres.add(elementj);
//                    }
//                }
//            }
//
//            cles.add(nombres);
//        }
//
//        return cles;
//    }
    /**
     * Cherche les elements qui pourraient être des codes de département ou des codes postaux.<br>
     * Parmi ceux-ci:
     * <ul>
     *   <li>2 chiffres non précédés d'une clé, d'un type de voie, un article ou de la lettre A, D, N, ou E (autoroute, départementale, etc...),</li>
     *   <li>et non suivi de zéro ou plusieurs numéros suivis d'un article optionnel, d'une répétition optionnelle, d'un type de voie obligatoire
     * et qui n'appartient pas à une date (jour ou année), ou qui n'est pas un arrondissement</li>
     *   <li>5 chiffres (codes postaux)</li>
     * </ul>
     * @param elements les éléments qui pourraient être des codes de département.
     * @return
     */
    private ArrayList<ArrayList<RefCle>> trouvePretendusCodesDepartements(ArrayList<ArrayList<RefCle>> elements, Connection connection)
            throws SQLException {
        ArrayList<ArrayList<RefCle>> cles = new ArrayList<ArrayList<RefCle>>();

        for (int i = 0; i < elements.size(); i++) {
            ArrayList<RefCle> elementsi = elements.get(i);
            ArrayList<RefCle> nombres = new ArrayList<RefCle>();

            for (int j = 0; j < elementsi.size(); j++) {
                // Modif WA 09/2011 : les codes Dpts potentiels sont marques comme codes departement des l'extraction des lexemes
                // Il faut supprimer les faux positifs et les convertir en 'numero'
                RefCle elementj = elementsi.get(j);
                String motj = elementj.obtientMot();

                if (elementj.obtientCategorieMot() == CategorieMot.CodeDepartement) {
                    // Verifications vis a vis des elements precedents
                    int index = indiceElementPrecedent(elementsi, j);
                    if (index != -1) // Il y a bien un elt precedent
                    {
                        // 1 - Une cle est presente devant, non precedee d'un eme -- APPARTEMENT 20, RUE 30, ...
                        // Mais pas -- 30 EME RUE 75, 02 EME APPARTEMENT 83
                        // Alors, pas un code dpt
                        if (trouveElementDeCategorie(elementsi, index, CategorieMot.Cle) != -1) // il y a bien une cle d'index 'index'
                        {
                            int index2 = indiceElementPrecedent(elementsi, index);  // l'index de l'elt precedent la cle
                            if ((index2 == -1) || (trouveElementDeCategorie(elementsi, index2, CategorieMot.Eme) == -1)) // il n'y a pas de eme devant la cle
                            {
                                // PAS un code dpt
                                reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                                continue;
                            }
                        }

                        // 2 - Un mois est present devant le code potentiel
                        // Alors pas un code dpt
                        if (GestionDate.estUnMois(elementsi.get(index).obtientMot())) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }

                        // 3 - Le code potentiel est precede d'un A, RN, D, ... -- RN 12, A 83, ...
                        // Alors pas un code dpt
                        if (trouveElementACDNERN(elementsi, index) != -1) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }

                        // 4 - Le code potentiel est precede d'un type de voie -- RUE 20
                        // Alors pas un code dpt
                        if (trouveElementDeCategorie(elementsi, index, CategorieMot.TypeDeVoie) != -1) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }

                        // 5 - Le code potentiel est precede d'un article -- LE 30
                        // Alors pas un code dpt
                        if (trouveElementDeCategorie(elementsi, index, CategorieMot.Article) != -1) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }
                    }

                    // Verifications intrinseque
                    // 6 - Le code potentiel represente un arrondissement
                    // Alors pas un code dpt
                    String contexte = obtientContexte(elementsi, j, true, false);
                    if (referentiel.resoudAmbiguiteArrondissement(motj, contexte, connection)) {
                        // PAS un code dpt mais un num arrondissement
                        reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                        continue;
                    }

                    // Verifications vis a vis des elements suivants
                    index = indiceElementSuivant(elementsi, j);
                    if (index != -1) {
                        // 7 - Un eme est present deriere
                        // Alors pas un code dpt
                        if (trouveElementDeCategorie(elementsi, index, CategorieMot.Eme) != -1) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }

                        // 8 - Une succession de nombres (ou de codes dpt potentiels),
                        // d'articles et de repetitions suivie d'un type de voie est presente deriere.
                        // Alors pas un code dpt
                        int current = indiceElementSuivant(elementsi, j);
                        CategorieMot categorie = elementsi.get(current).obtientCategorieMot();
                        while ((current > 0) && ((categorie == CategorieMot.Numero) || ((categorie == CategorieMot.CodeDepartement)))) {
                            current = indiceElementSuivant(elementsi, current);
                            categorie = (current > 0) ? (elementsi.get(current).obtientCategorieMot()) : CategorieMot.Autre;
                        }
                        if ((categorie == CategorieMot.Repetition) || (categorie == CategorieMot.Article) || (categorie == CategorieMot.TypeDeVoie)) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }




                        // 9 - Le code potentiel est un nombre < 32 et il est suivi d'un mois
                        // Alors pas un code dpt
                        int valeur = Algos.parseInt(motj);
                        if ((valeur > 0) && (valeur < 32) && GestionDate.estUnMois(elementsi.get(index).obtientMot())) {
                            // PAS un code dpt
                            reaffectPotentialCodeDptToNumero(elementj, elementsi, j);
                            continue;
                        }
                    }
                    // Sinon, c'est un code dpt potentiel tout a fait acceptable
                    nombres.add(elementj);
                } else if (elementj.obtientCategorieMot() == CategorieMot.CodePostal) {
                    // Code postal potentiel (est deja estampille comme code postal lors de l'analyse lexicale)
//                    elementj.definitCategorieMot(CategorieMot.CodePostal);
                    nombres.add(elementj);
                }
            }
            cles.add(nombres);
        }

        return cles;
    }
    private final static Pattern NUMBER = Pattern.compile("\\b[0-9]+\\b");

    /**
     * Reestampille un element en numero plutot qu'en autre chose
     * L'element doit etre de la forme [^0-9]*[0-9]+[^0-9]*
     * On en extrait la partie numerique et on en change la categorie.
     */
    private void reaffectPotentialCodeDptToNumero(RefCle element, ArrayList<RefCle> elementsi, int index) {
        // Extraction de la premiere partie numerique de l'element
        Matcher match = NUMBER.matcher(element.obtientMot());
        if (match.matches()) // L'element est deja un nombre, on ne change que la categorie
        {
            element.definitCategorieMot(CategorieMot.Article.Numero);
        } else {
            match.reset();
            if (match.find()) // Sinon, il faut extraire la partie numerique de l'element
            {
                String newMot = match.group();
                int newIndex = match.start() + element.obtientIndex();

                // Creation d'un nouvel element de categorie numero et de mot la partie numerique du l'ancien element
                Mot m = new Mot(newMot);
                RefCle newElement = new RefCle(newMot, m, newIndex, element.obtientChaineOriginale(), CategorieMot.Article.Numero);

                // Remplacement de l'ancien element par le nouveau
                elementsi.remove(index);
                elementsi.add(index, newElement);
            }
        }
    }

    /**
     * Extrait des nombres sélectionnés comme pouvant être le code postal, celui 
     * qui a le plus de chance de le représenter.<br>
     * Le code de département correspondant au premier code postal en partant de la dernière ligne est
     * retourné.<br>
     * S'il n'y a pas de code postal, le code de département du premier de ces nombres en partant de la
     * dernière ligne est retourné.<br>
     * Sinon, retourne null.
     * @param nombres
     * @return
     */
    private ArrayList<String> selectionneCodeDepartement(ArrayList<ArrayList<RefCle>> nombres) {
        ArrayList<String> res = new ArrayList<String>();
        for (int i = nombres.size() - 1; i >= 0; i--) {
            ArrayList<RefCle> nombresi = nombres.get(i);
            for (int j = 0; j < nombresi.size(); j++) {
                RefCle nbj = nombresi.get(j);
                CategorieMot categorie = nbj.obtientCategorieMot();
                String mot = nbj.obtientMot();

                if (categorie == CategorieMot.CodePostal) {
                    // WA 09/2011 on utilise maintenant GestionDepartement qui se charge d'extraire le code departement d'un code postal.
//                    res.add(nbj.obtientMot().substring(0, 2));
                    res.add(GestionCodesDepartements.getInstance().computeCodeDptFromCodePostal(mot));

                } else if (categorie == CategorieMot.CodeDepartement || mot.length() == 2) {
                    // WA 09/2011 on utilise maintenant GestionDepartement qui se charge de donner la denomination officielle
                    // d'un dpt a partir d'un eventuel synonyme.
//                    res.add(nbj.obtientMot());
                    res.add(GestionCodesDepartements.getInstance().getOfficialCodeDpt(mot));
                }
            }
        }
        return res;
    }

    /**
     * Trouve le code de département dans les éléments trouvées dans une adresse.
     * @param lignes
     * @return
     */
    private ArrayList<String> trouveCodeDepartement(ArrayList<ArrayList<RefCle>> elements, Connection connection) throws SQLException {
        ArrayList<ArrayList<RefCle>> nombres = trouvePretendusCodesDepartements(elements, connection);

        return selectionneCodeDepartement(nombres);
    }

    /**
     * Cherche les communes de France présentes dans la ligne.<br>
     * Les éléments de Catégorie AutreNom qui correspondaient sont supprimés.<br>
     * Les communes sont insérées dans les éléments, en fonction de leur index.<br>
     * @param ligne rien n'est fait si ligne est vide.
     * @param elements
     * @param code_departement s'il est null, les communes sont recherchées dans les départements par défaut.
     * @param connection
     * @return le nombre de communes trouvées.
     */
    private ArrayList<String> chercheCommunes(String ligne, ArrayList<RefCle> elements, Connection connection) throws SQLException {
        ArrayList<String> departements = new ArrayList<String>();
        if (ligne.length() == 0) {
            return departements;
        }
        ArrayList<RefCommune> communes = referentiel.chercheCommuneDansChaine(ligne, connection);

        // Commence par supprimer celles qui se chevauchent.
        supprimeDoublonsCommune(communes);

        // Puis trie les communes par leur index.
        trieCommune(communes, 0, communes.size() - 1);

        for (int i = 0,  index = 0; (i < communes.size()) && (index < elements.size()); i++, index = 0) {
            RefCommune commune = communes.get(i);
            departements.addAll(commune.obtientCodesDepartement());

            int startindex = commune.obtientIndex();
            int endindex = startindex + commune.obtientMot().length();

            // Cherche un élément de la ligne dont l'index est supérieur ou égal à celui de la commune.
            while (index < elements.size() && elements.get(index).obtientIndex() < startindex) {
                index++;

            // La commune est alors insérée à son index.

            }
            if (index < elements.size()) {
                elements.add(index, commune);

            } else {
                elements.add(commune);

            }
            index++;

            // Pour tous les éléments qui commencent avant la fin de la commune
            while (index < elements.size() && elements.get(index).obtientIndex() < endindex) {
                // S'ils sont de catégorie Autre, ils sont supprimés.
                if (elements.get(index).obtientCategorieMot() == CategorieMot.Autre) {
                    elements.remove(index);

                } else {
                    index++;

                }
            }
        }
        return departements;
    }

    /**
     * Permet de supprimer les communes superflues qui sont positionnées au même index.
     * Les choix effectués sont les suivants:
     * <ul>
     * <li>Les communes dont le nombre de fautes ne sont pas connues sont supprimées.</li>
     * <li>Si elles ont autant de fautes ou si leurs fautes ne sont pas connues, la plus longue est conservée.</li>
     * <li>Seules les communes ayant la valeur la plus grande de (longueur-2*fautes) sont conservées</li>
     * <li>Si la valeur est la même, les commmunes les plus longues sont conservées</li>
     * </ul>
     * Les départements sont toutefois conservés dans la commune conservée.
     * @param communes
     */
    private void supprimeDoublonsCommune(ArrayList<RefCommune> communes) {
        for (int i = 0; i < communes.size() - 1; i++) {
            RefCommune communei = communes.get(i);
            for (int j = i + 1; j < communes.size(); j++) {
                RefCommune communej = communes.get(j);
                if (communei.obtientIndex() == communej.obtientIndex()) {
                    int fautei = communei.obtientFautes();
                    int fautej = communej.obtientFautes();

                    if (fautei == -1 && fautej != -1) {
                        communes.remove(i);
                        i--;
                        break;
                    } else if (fautej == -1 && fautei != -1) {
                        communes.remove(j);
                        j--;
                    } else {
                        int eurisi = communei.obtientMot().length() - 2 * fautei;
                        int eurisj = communej.obtientMot().length() - 2 * fautej;

                        if (eurisi < eurisj) {
                            communes.remove(i);
                            i--;
                            break;
                        } else if (eurisj < eurisi) {
                            communes.remove(j);
                            j--;
                        } else {
                            if (communei.obtientMot().length() > communej.obtientMot().length()) {
                                communes.remove(j);
                                j--;
                            } else {
                                communes.remove(i);
                                i--;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Permet de supprimer les pays superflus qui sont positionnées au même index.
     * Les choix effectués sont les suivants:
     * <ul>
     * <li>Les pays dont le nombre de fautes ne sont pas connus sont supprimés.</li>
     * <li>Si ils ont autant de fautes ou si leurs fautes ne sont pas connues, la plus longue est conservée.</li>
     * <li>Seules les communes ayant la valeur la plus grande de (longueur-2*fautes) sont conservées</li>
     * <li>Si la valeur est la même, les commmunes les plus longues sont conservées</li>
     * </ul>
     * Les départements sont toutefois conservés dans la commune conservée.
     * @param communes
     */
    private void supprimeDoublonsPays(ArrayList<RefPays> pays) {
        for (int i = 0; i < pays.size() - 1; i++) {
            RefPays curCleI = pays.get(i);
            for (int j = i + 1; j < pays.size(); j++) {
                RefPays curCleJ = pays.get(j);
                if (curCleI.obtientIndex() == curCleJ.obtientIndex()) {
                    int fautesI = curCleI.obtientFautes();
                    int fautesJ = curCleJ.obtientFautes();

                    if (fautesI == -1 && fautesJ != -1) {
                        pays.remove(i);
                        i--;
                        break;
                    } else if (fautesI != -1 && fautesJ == -1) {
                        pays.remove(j);
                        j--;
                    } else {
                        int noteI = curCleI.obtientMot().length() - (2 * fautesI);
                        int noteJ = curCleJ.obtientMot().length() - (2 * fautesJ);

                        if (noteI < noteJ) {
                            pays.remove(i);
                            i--;
                            break;
                        } else if (noteJ < noteI) {
                            pays.remove(j);
                            j--;
                        } else {
                            if (curCleI.obtientMot().length() > curCleJ.obtientMot().length()) {
                                pays.remove(j);
                                j--;
                            } else {
                                pays.remove(i);
                                i--;
                                break;
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * Cherche les communes du département présentes dans la ligne.<br>
     * Si un mot peut être interprété sous la forme de plusieurs communes, la commune la plus ressemblante sera
     * conservée.<br>
     * Si des communes se chevauchent, la première sera conservée.
     * Les éléments de Catégorie AutreNom qui correspondaient sont supprimés.<br>
     * Les communes sont insérées dans les éléments, en fonction de leur index.<br>
     * @param ligne rien n'est fait si ligne est vide.
     * @param elements
     * @param code_departement s'il est null, les communes sont recherchées dans les départements par défaut.
     * @param connection
     * @return les départements des communes trouvées.
     */
    private ArrayList<String> chercheCommunes(String ligne, ArrayList<RefCle> elements, ArrayList<String> codes_departement,
            Connection connection) throws SQLException {
        if (ligne.length() == 0) {
            return new ArrayList<String>();
        }
        ArrayList<RefCommune> communes = referentiel.chercheCommuneDansChaine(ligne, codes_departement, connection);

        // Commence par supprimer celles qui se chevauchent.
        supprimeDoublonsCommune(communes);

        // Puis trie les communes dans l'ordre d'index dans la chaine.
        trieCommune(communes, 0, communes.size() - 1);

        // Puis efface les elements trouvés à la place des communes restantes
        for (int i = 0,  index = 0; (i < communes.size()) && (index < elements.size()); i++) {
            RefCommune commune = communes.get(i);

            int startindex = commune.obtientIndex();
            int endindex = startindex + commune.obtientMot().length();

            // Cherche un élément de la ligne dont l'index est supérieur ou égal à celui de la commune.
            while (index < elements.size() && elements.get(index).obtientIndex() < startindex) {
                index++;

            // La commune est alors insérée à son index.

            }
            if (index < elements.size()) {
                elements.add(index, commune);

            } else {
                elements.add(commune);

            }
            index++;

            // Pour tous les éléments qui commencent avant la fin de la commune
            while (index < elements.size() && elements.get(index).obtientIndex() < endindex) {
                // S'ils sont de catégorie Autre, ils sont supprimés.
                if (elements.get(index).obtientCategorieMot() == CategorieMot.Autre) {
                    elements.remove(index);

                } else {
                    index++;

                }
            }

            // Ajoute les codes de département de la commune si nécessaire.
            ArrayList<String> codes = commune.obtientCodesDepartement();
            for (int j = 0; j < codes.size(); j++) {
                String code = codes.get(j);
                if (codes_departement.indexOf(code) == -1) {
                    codes_departement.add(code);

                }
            }
        }

        return codes_departement;
    }

    /**
     * Cherche les noms de pays présents dans les lignes passees, aux erreurs pres.
     * @param lignes
     * @param connection
     * @return
     * @throws SQLException
     */
    private ArrayList<RefPays> trouvePays(String[] lignes, ArrayList<ArrayList<RefCle>> elements, Connection connection) throws SQLException {
        ArrayList<RefPays> pays = new ArrayList<RefPays>();
        for (int l = 0; l < lignes.length; l++) {
            String ligne = lignes[l];
            ArrayList<RefPays> paysDsChaine = referentiel.cherchePaysDansChaine(ligne, connection);

            // Suppression des doublons de mm index
            supprimeDoublonsPays(paysDsChaine);
            // Et trie les pays par leur index
            triePays(paysDsChaine, 0, paysDsChaine.size() - 1);

            // MAJ de elements de la ligne
            List<RefCle> lineElements = elements.get(l);
            for (int i = 0,  index = 0; (i < paysDsChaine.size()) && (index < lineElements.size()); i++, index = 0) {
                RefPays curPays = paysDsChaine.get(i);

                int startindex = curPays.obtientIndex();
                int endindex = startindex + curPays.obtientMot().length();

                // Cherche un élément de la ligne dont l'index est supérieur ou égal à celui du pays
                while (index < lineElements.size() && lineElements.get(index).obtientIndex() < startindex) {
                    index++;
                }
                // Le pays est alors insérée à son index.
                if (index < lineElements.size()) {
                    lineElements.add(index, curPays);
                } else {
                    lineElements.add(curPays);
                }
                index++;

                // Suppression de tous les elements de categorie autre commencant a l'interieur du pays
                while (index < lineElements.size() && lineElements.get(index).obtientIndex() < endindex) {
                    // S'ils sont de catégorie Autre, ils sont supprimés.
                    if (lineElements.get(index).obtientCategorieMot() == CategorieMot.Autre) {
                        lineElements.remove(index);
                    } else {
                        index++;
                    }
                }
            }
            pays.addAll(paysDsChaine);
        }
        return pays;
    }

    /**
     * Restructure l'adresse spécifiée.<br>
     * Les éléments qui ne peuvent pas être placés sont laissés dans leur ligne d'origine pour 
     * les 6 (ou 7 si gestionPays) premières lignes, et placés dans la ligne 3 pour les lignes suivantes.<br>
     * Si aucun code de département n'est trouvé et qu'aucune commune n'est trouvée pour 
     * les départements par défaut, les communes sont cherchées en france entière (beaucoup plus long).
     * Si la gestion des pays est activee et qu'un autre pays que le pays trouve n'est pas paysParDefaut les autres lignes sont
     * laissees telles quelles.
     * @param ajouter_departements_finaux permet d'ajouter une ligne résultat avec la liste des départements
     * finaux.
     * @param gererPays indique si on doit prendre en compte les eventuels pays presents
     * @return les 6 ou 7 lignes d'adresse restructurées.
     */
    public String[] restructure(String[] lignes, boolean ajouter_departements_finaux, boolean gererPays, Connection connection) throws
            SQLException {
        ArrayList<ArrayList<RefCle>> elements = new ArrayList<ArrayList<RefCle>>();
        String[] restants = new String[lignes.length];

        for (int i = 0; i < lignes.length; i++) {
            // Identifie les éléments clés dans chaque ligne d'adresse.
            // Les clés, leurs abbréviations, les nombres, les èmes, les articles, les répétitions,
            // les types de voie, les codes departement potentiels, et tous les autres mots.
            elements.add(chercheElements(lignes[i]));
        }

        ArrayList<String> codes_departement = trouveCodeDepartement(elements, connection);

        // WA 01/2012 Pays pays a priori, avant desambiguisation
        List<RefPays> pays = null;
        if (params.isUtilisationDeLaGestionDesPays() && gererPays) {
            pays = trouvePays(lignes, elements, connection);
        }

        if (codes_departement.size() != 0) {
            // Cherche les communes
            // Les éléments sont triés pour conserver les priorités.
            for (int i = 0; i < lignes.length; i++) {
                chercheCommunes(lignes[i], elements.get(i), codes_departement, connection);
                trieElements(elements.get(i));
            }
        } else {
            // Cherche les communes en france entière pour déterminer le numéro de département.
            // Les éléments sont triés pour conserver les priorités.
            for (int i = 0; i < lignes.length; i++) {
                codes_departement.addAll(chercheCommunes(lignes[i], elements.get(i), connection));
                trieElements(elements.get(i));
            }
        }

        // Résoud les ambiguités :
        // ambiguité de certains articles qui peuvent être des répétitions.
        // ambiguité des clés qui peuvent être des types de voies (et vice versa)
        // ambiguité des numéros : numéro d'adresse, code postal, ...
        for (int i = 0; i < lignes.length; i++) {
            resoudAmbiguites(lignes[i], elements.get(i), codes_departement, connection);
            resoudAmbiguitesNumero(elements.get(i));
        }

        // WA 01/2012 Pays : ambiguites : pays dans voie
        if ((gererPays) && (pays != null)) {
            for (int i = 0; i < lignes.length; i++) {
                resoudAmbiguitesPays(pays, lignes[i], elements.get(i), codes_departement, connection);
            }
        }

        // Corrige les doublons
        ArrayList<String> codes_departement_finaux = resoudAmbiguitesDoublons(elements, connection);
        // Officialise les codes dpt finaux
        officialiseCodesDepartements(codes_departement_finaux);

        // Une fois les doublons corrigés, les numéros peuvent être réassignés (notamment numéros des clés et numéro d'adresse).
        for (int i = 0; i < lignes.length; i++) {
            resoudAmbiguitesNumero(elements.get(i));
        }

        // Résoud les ambiguités :
        // ambiguité des communes qui peuvent appartenir à des noms de voies.
        // des communes et types de voies qui se chevauchent
        for (int i = 0; i < lignes.length; i++) {
            resoudAmbiguitesComplexe(lignes[i], elements.get(i), codes_departement, connection);
        }

        // Traite les doublons de communes, notamment concernant les postes restantes.
        resoudAmbiguitesDoublonsComplexes(elements, codes_departement_finaux, connection);

        // Associe à chaque élement clé leur compléments.
        for (int i = 0; i < lignes.length; i++) {
            restants[i] = repartitComplements(lignes[i], elements.get(i));
        }

        // WA 01/2012 Pays : reconstitution differente si on a trouve un pays etranger
        String[] res = null;
        RefCle foundPay = obtientPaysTrouve(elements, gererPays);
        if (gererPays && (!isPaysTheDefaultOne(foundPay))) {
            res = reconstitueAdresseEtrangere(lignes, foundPay, ajouter_departements_finaux);
        } else if (ajouter_departements_finaux) {
            res = reconstitue(restants, elements, codes_departement_finaux, foundPay);
        } else {
            res = reconstitue(restants, elements, null, foundPay);
        }
        return res;
    }
    static final int FLAG_DECOUPE_NUMERO = 1;
    static final int FLAG_DECOUPE_REPETITION = 2;
    static final int FLAG_DECOUPE_AUTRES_NUMEROS = 4;
    static final int FLAG_DECOUPE_TYPE_DE_VOIE = 8;
    static final int FLAG_DECOUPE_ARTICLE = 16;
    static final int FLAG_DECOUPE_LIBELLE = 32;
    static final int FLAG_DECOUPE_MOT_DETERMINANT = 64;
    static final int FLAG_DECOUPE_CODE_POSTAL = 128;
    static final int FLAG_DECOUPE_COMMUNE = 256;
    static final int FLAG_DECOUPE_NUMERO_ARRDT = 512;
    static final int FLAG_DECOUPE_CEDEX = 1024;
    static final int FLAG_DECOUPE_CODE_CEDEX = 2048;
    static final int FLAG_DECOUPE_LIGNE1 = 4096;
    static final int FLAG_DECOUPE_LIGNE2 = 8192;
    static final int FLAG_DECOUPE_LIGNE3 = 16384;
    static final int FLAG_DECOUPE_LIGNE5 = 32768;
    // WA 01/2012 Pays
    static final int FLAG_DECOUPE_PAYS = 32768 * 2;

    /**
     * Découpe l'adresse normalisée et restructurée spécifiée en différents champs.<br>
     * Le tableau nature spécifie le format du tableau de retour, où la première ligne est 1
     * si aucun problème n'est arrivé, et où les éléments suivants sont formés à partir
     * des éléments du paramètre natures.<br>
     * Les éléments du paramètre natures peuvent être une combinaison des flags suivants:
     * <ul>
     * <li>1 pour numero</li>
     * <li>2 pour repetition</li>
     * <li>4 pour autres numeros</li>
     * <li>8 pour type de voie</li>
     * <li>16 pour article</li>
     * <li>32 pour libelle</li>
     * <li>64 pour le mot déterminant</li>
     * <li>128 pour code postal</li>
     * <li>256 pour commune</li>
     * <li>512 pour numero d'arrondissement</li>
     * <li>1024 pour cedex</li>
     * <li>2049 pour le code cedex</li>
     * <li>4096 pour ligne1</li>
     * <li>8192 pour ligne2</li>
     * <li>16384 pour ligne3</li>
     * <li>32768 pour ligne5</li>
     * <li>65536 pour le pays</li>
     * </ul>
     * L'ordre des éléments des termes générés respectent l'ordre établi ci-dessus.<br>
     * ex: decoupe(new String[]{"24 BD HOPITAL 75005 PARIS'},new int[]{1,8+32,64+128});<br>
     * retourne {"1","24","BD HOPITAL","75005 PARIS"}<br>
     * 
     * Si une chaine de la forme "Numero Répétition" est découpée, la répétition 'R' n'est jamais
     * confondue avec le type de voie 'R' (ou "RUE").
     * @param lignes
     * @param natures le format des lignes retournées
     * @param numeroslignes les numéros des lignes sources. S'il est null, une restructuration est effectuée.
     * @return
     */
    public String[] decoupe(int application, String[] lignes, int[] natures, int[] numeroslignes, Connection referentiel) throws
            SQLException {
        if (lignes.length == 0 || natures.length == 0 || numeroslignes.length != lignes.length) {
            GestionLogs.getInstance().logDecoupage(application, true);
            return new String[]{"1"};
        }

        String[] res = new String[natures.length + 1];
        String[] adresse = null;

        if (numeroslignes == null) {
            adresse = normalise_1(lignes);
            adresse = restructure(adresse, false, false, referentiel);
        } else {
            adresse = new String[7];
            for (int i = 0; i < 7; i++) {
                adresse[i] = "";
            }
            for (int i = 0; i < lignes.length; i++) {
                int ligne = numeroslignes[i] - 1;
                if (adresse[ligne] == null || adresse[ligne].length() == 0) {
                    adresse[ligne] = lignes[i];
                } else {
                    adresse[ligne] += " " + lignes[i];
                }
            }
        }

        // La voie est découpée,
        ArrayList<RefNumero> numeros = trouveNumeros(adresse[3]);
        RefCle typedevoie = trouveTypeVoie(adresse[3], numeros);
        RefCle article, libelle;
        if (typedevoie.obtientMot().length() != 0) {
            article = trouveArticleVoie(adresse[3], typedevoie);
            libelle = trouveLibelleVoie(adresse[3], article);
        } else {
            article = new RefCle("", 0);
            libelle = trouveLibelleVoie(adresse[3], numeros);
        }

        RefCle codepostal = trouveCodePostal(adresse[5]);
        RefCle commune = trouveNomVille(adresse[5], codepostal);
        RefCle numeroArrdt = trouveNumeroArrondissement(adresse[5], commune);
        RefCle lcedex = trouveCedex(adresse[5], numeroArrdt);
        RefCle numeroCedex = trouveNumeroCedex(adresse[5], lcedex);

        StringBuilder sb = new StringBuilder();
        // et les champs découpés affectés.
        res[0] = "1";
        for (int i = 0; i < natures.length; i++) {
            int nature = natures[i];
            sb.setLength(0);

            if ((nature & FLAG_DECOUPE_NUMERO) != 0) // numéro
            {
                if (numeros.size() > 0) {
                    Algos.appendWithSpace(sb, numeros.get(0).
                            obtientNumeroNormalise(), true);
                }
            }
            if ((nature & FLAG_DECOUPE_REPETITION) != 0) // repetition
            {
                if (numeros.size() > 0) {
                    char rep = numeros.get(0).obtientRepetitionNormalise();
                    if (rep != 0) {
                        Algos.appendWithSpace(sb, rep, true);
                    }
                }
            }
            if ((nature & FLAG_DECOUPE_AUTRES_NUMEROS) != 0) // autres numéros
            {
                for (int j = 1; j < numeros.size(); j++) {
                    if (j != 1) {
                        sb.append(';');
                    }
                    Algos.appendWithSpace(sb, numeros.get(j).
                            obtientNumeroNormalise(), true);

                    char rep = numeros.get(j).obtientRepetitionNormalise();
                    if (rep != 0) {
                        Algos.appendWithSpace(sb, rep, true);
                    }
                }
            }
            if ((nature & FLAG_DECOUPE_TYPE_DE_VOIE) != 0) // type de voie
            {
                Algos.appendWithSpace(sb, typedevoie.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_ARTICLE) != 0) // article
            {
                Algos.appendWithSpace(sb, article.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_LIBELLE) != 0) // libelle
            {
                Algos.appendWithSpace(sb, libelle.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_MOT_DETERMINANT) != 0) // mot déterminant
            {
                Algos.appendWithSpace(sb, Algos.derniermot(libelle.obtientMot()), true);
            }
            if ((nature & FLAG_DECOUPE_CODE_POSTAL) != 0) // code postal
            {
                Algos.appendWithSpace(sb, codepostal.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_COMMUNE) != 0) // commune
            {
                Algos.appendWithSpace(sb, commune.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_NUMERO_ARRDT) != 0) // code arrondissement
            {
                Algos.appendWithSpace(sb, numeroArrdt.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_CEDEX) != 0) // cedex
            {
                Algos.appendWithSpace(sb, lcedex.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_CODE_CEDEX) != 0) // code cedex
            {
                Algos.appendWithSpace(sb, numeroCedex.obtientMot(), true);
            }
            if ((nature & FLAG_DECOUPE_LIGNE1) != 0) // ligne 1
            {
                Algos.appendWithSpace(sb, adresse[0], true);
            }
            if ((nature & FLAG_DECOUPE_LIGNE2) != 0) // ligne 2
            {
                Algos.appendWithSpace(sb, adresse[1], true);
            }
            if ((nature & FLAG_DECOUPE_LIGNE3) != 0) // ligne 3
            {
                Algos.appendWithSpace(sb, adresse[2], true);
            }
            if ((nature & FLAG_DECOUPE_LIGNE5) != 0) // ligne 5
            {
                Algos.appendWithSpace(sb, adresse[4], true);
            }
            if ((nature & FLAG_DECOUPE_PAYS) != 0) {
                Algos.appendWithSpace(sb, adresse[6], true);
            }

            res[i + 1] = sb.toString();
        }

        GestionLogs.getInstance().logDecoupage(application, true);
        return res;
    }
}
