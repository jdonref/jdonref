/*
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
 * julien.moquet@interieur.gouv.fr
 * 
 * Ce logiciel est un JDONREFservice web servant à valider et géocoder des adresses postales.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.JDONREF;
import ppol.jdonref.JDONREFService;
import ppol.jdonref.PropositionGeocodage;
import ppol.jdonref.PropositionGeocodageInverse;
import ppol.jdonref.PropositionNormalisation;
import ppol.jdonref.PropositionValidation;
import ppol.jdonref.ResultatErreur;
import ppol.jdonref.ResultatGeocodage;
import ppol.jdonref.ResultatGeocodageInverse;
import ppol.jdonref.ResultatNormalisation;
import ppol.jdonref.ResultatValidation;

/**
 * Représente une adresse.<br>
 * Les champs donneesIn sont à calculer manuellement.<br>
 * Attention! Suite aux traitements de validation ou restructuration les informations
 * de base (numero, repetition, typedevoie,...) ne sont pas valables pour l'adresse :
 * il s'agit des informations de base d'origine.
 * @author jmoquet
 */
public class Adresse {

    static Random r = new Random(Calendar.getInstance().getTimeInMillis());
    ArrayList<String> cles = new ArrayList<String>();
    String clePoizon = "";
    String libellePoizon = "";
    String ligne4Poizon = "";
    String ligne6Poizon = "";
    String idPoizon = null;
    String idVoie = null;
    String codeinsee = null;
    String date = null;
    String codeSovAc3 = null;
    int numeromin = 0;
    int numeromax = 0;
    String numero = "";
    String repetition = "";
    String numerossupplementaires = "";
    String typedevoie = "";
    String article = "";
    String libelle = "";
    String codepostal = "";
    String ville = "";
    String arrondissement = "";
    String pays = "";
    String departement = "";
    String ligne1 = "";
    String ligne2 = "";
    String ligne3 = "";
    String ligne4 = "";
    String ligne5 = "";
    String ligne6 = "";
    String ligne7 = "";
    String modele = "";
    String projection = "";
    String serviceReverse = "";
    int service = 0;
    int nbfautes = 0;
    int structure = 0; // 1, 2, 3 ou 4
    String x;
    String y;
    String retour;
    /**
     * L'adresse originale avant introduction de fautes et d'erreurs de structure.
     */
    Adresse origine = null;
    String ligne1SansFaute = "";
    String ligne4SansFaute = "";
    String ligne6SansFaute = "";
    String ligne7SansFaute = "";
    /**
     * Le nombre d'élément du retour d'appel à JDONREF qui sont conservés.
     */
    int retour_size = 20;
    int erreurjdonref = 0;
    String messagejdonref = null;
    /**
     * Code d'erreur pour les méthodes valide, restructure, et géocode.
     * <ul>
     *   <li>0 si aucune erreur ne s'est produit.</li>
     *   <li>Concernant la validation<ul>
     *   <li>1 si la méthode a retourné une erreur.</li>
     *   <li>2 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>3 si aucune aproposition n'est retournée.</li>
     *   <li>4 si jdonref a retourné la valeur null</li>
     *   <li>5 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant le géocodage<ul>
     *   <li>6 si la méthode a retourné une erreur.</li>
     *   <li>7 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>8 si aucune aproposition n'est retournée.</li>
     *   <li>9 si jdonref a retourné la valeur null</li>
     *   <li>10 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant la restructuration<ul>
     *   <li>11 si la méthode a retourné une erreur.</li>
     *   <li>12 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>13 si aucune aproposition n'est retournée.</li>
     *   <li>14 si jdonref a retourné la valeur null</li>
     *   <li>15 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant le geocodage inverse<ul>
     *   <li>16 si la méthode a retourné une erreur.</li>
     *   <li>17 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>18 si aucune aproposition n'est retournée.</li>
     *   <li>19 si jdonref a retourné la valeur null</li>
     *   <li>20 si une exception s'est produite</li>
     *   </ul></li>
     * </ul>
     */
    int codeerreur = 0;
    String note = "";

    /**
     *  Compare les adresses (uniquement les donneesIn calculées).<br>
     * La gestion des codes de département ou codes postaux n'est pas prise en compte.
     */
    public boolean compareTo2(Adresse a) {
        if (ligne1.compareTo(a.ligne1) != 0) {
            return false;
        }
        if (ligne2.compareTo(a.ligne2) != 0) {
            return false;
        }
        if (ligne3.compareTo(a.ligne3) != 0) {
            return false;
        }
        if (ligne4.compareTo(a.ligne4) != 0) {
            return false;
        }
        if (ligne5.compareTo(a.ligne5) != 0) {
            return false;
        }
        if (ligne6.compareTo(a.ligne6) != 0) {
            return false;
        }
        if (!ligne7.equals(a.ligne7)) {
            return false;
        }
        return true;
    }

    /**
     * Trouve le code postal dans la chaine spécifiée.<br>
     * Il s'agit du premier groupe de caractères qui doivent être des chiffres.
     * @param chaine une chaine de ligneIndex 6 normalisée et restructurée.
     * @return
     */
    public static String trouveCodePostal(String chaine) {
        StringBuilder sb = new StringBuilder();
        char c;
        int i = 0, startindex = 0;

        while (i < chaine.length() && Character.isSpaceChar(c = chaine.charAt(i))) // Au cas où...
        {
            i++;
        }
        startindex = i;

        while (i < chaine.length() && Character.isDigit(c = chaine.charAt(i))) {
            sb.append(c);
            i++;
        }

        if (startindex != i) {
            return sb.toString();
        }
        return "";
    }

    /**
     * Trouve le code postal et la commune de la voie spécifiée.
     * @return
     */
    public static String[] trouveCodeEtCommune(String ligne6) {
        String code = trouveCodePostal(ligne6);
        String commune;
        if (code.length() > 0 && ligne6.length() > code.length() + 1) {
            commune = ligne6.substring(code.length() + 1);
        } else {
            commune = ligne6;
        }
        return new String[]{code, commune};
    }

    /**
     * Compare à l'adresse composée de ses donneesIn 4 et 6.<br>
     * <ul>
     * <li>La gestion des codes de département ou codes postaux est prise en compte.</li>
     * <li>La aligne4 doit contenir ou être contenu dans l'autre.</li>
     * <li>Le code postal ou le code de département de la ligneIndex 6 doit être le même.</li>
     * <li>Si un des deux codes est absents, la commune doit être la même</li>
     * <li>Les ligneIndex 7 sont comparées en acceptant que vide == FRANCE</li>
     * </ul>
     */
    public boolean compareTo(String aligne4, String aligne6, String aligne7) {
        if (!(aligne4.contains(ligne4SansFaute) || ligne4SansFaute.contains(aligne4))) {
            return false;
        }
        String[] thiscodes = trouveCodeEtCommune(ligne6SansFaute);
        String[] codes = trouveCodeEtCommune(aligne6);

        boolean code = false;
        boolean com = false;
        if (thiscodes[0].length() != 0 && codes[0].length() != 0) {
            if (!(thiscodes[0].startsWith(codes[0]) || codes[0].startsWith(thiscodes[0]))) {
                return false;
            }
            code = true;
        }
        if (thiscodes[1].length() != 0 && codes[1].length() != 0) {
            if (thiscodes[1].compareTo(codes[1]) != 0) {
                return false;
            }
            com = true;
        }

        if (!comparePays(aligne7)) {
            return false;
        }

        return com || code;
    }

    /**
     * Compare à l'adresse composée de ses donneesIn 4 et 6.<br>
     * <ul>
     * <li>La gestion des codes de département ou codes postaux est prise en compte.</li>
     * <li>La aligne4 doit contenir ou être contenu dans l'autre.</li>
     * <li>Le code postal ou le code de département de la ligneIndex 6 doit être le même.</li>
     * <li>Si un des deux codes est absents, la commune doit être la même</li>
     * <li>Les ligneIndex 7 sont comparées en acceptant que vide == FRANCE</li>
     * </ul>
     */
    public boolean compareTo(String aligne1, String aligne4, String aligne6, String aligne7) {
        final boolean b1 = compareTo(aligne4, aligne6, aligne7);
        boolean b2 = (ligne1SansFaute != null && ligne1SansFaute.length() > 0 && aligne1 != null && aligne1.trim().length() > 0)
                ? aligne1.contains(ligne1SansFaute) || ligne1SansFaute.contains(aligne1)
                : true;

        return b1 && b2;
    }

    private boolean comparePays(String pays2) {
        String thisPays = ((ligne7SansFaute == null) || (ligne7SansFaute.trim().length() == 0)) ? "FRANCE" : ligne7SansFaute;
        String otherPays = ((pays2 == null) || (pays2.trim().length() == 0)) ? "FRANCE" : pays2;
        if (!thisPays.equalsIgnoreCase(otherPays)) {
            return false;
        }
        return true;
    }

    /**
     * Permet de cloner une adresse.<br>
     * Les éléments, les donneesIn, l'état de l'adresse est cloné.<br>
     * L'origine du clone est soit l'objet en cour soit son origine s'il en dispose d'une.
     */
    @Override
    public Adresse clone() {
        Adresse a = new Adresse();
        for (int i = 0; i < this.cles.size(); i++) {
            a.cles.add(cles.get(i));
        }

        if (this.origine == null) {
            a.origine = this;
        } else {
            a.origine = this.origine;
        }
        a.idVoie = idVoie;
        a.codeinsee = codeinsee;
        a.date = date;
        a.codeSovAc3 = codeSovAc3;
        a.modele = modele;
        a.nbfautes = nbfautes;
        a.structure = structure;

        a.numero = numero;
        a.numeromin = numeromin;
        a.numeromax = numeromax;
        a.repetition = repetition;
        a.numerossupplementaires = numerossupplementaires;
        a.typedevoie = typedevoie;
        a.article = article;
        a.libelle = libelle;
        a.codepostal = codepostal;
        a.ville = ville;
        a.departement = departement;
        a.arrondissement = arrondissement;
        a.pays = pays;
        a.ligne1 = ligne1;
        a.ligne2 = ligne2;
        a.ligne3 = ligne3;
        a.ligne4 = ligne4;
        a.ligne5 = ligne5;
        a.ligne6 = ligne6;
        a.ligne7 = ligne7;
        a.ligne4SansFaute = ligne4SansFaute;
        a.ligne6SansFaute = ligne6SansFaute;
        a.ligne7SansFaute = ligne7SansFaute;

        a.x = x;
        a.y = y;
        a.projection = projection;
        a.serviceReverse = serviceReverse;
        // POIZON
        a.idPoizon = idPoizon;
        a.clePoizon = clePoizon;
        a.libellePoizon = libellePoizon;
        a.ligne4Poizon = ligne4Poizon;
        a.ligne6Poizon = ligne6Poizon;

        return a;
    }

    /**
     * Obtient la ligneIndex désignée par son numéro
     * @param x un numéro entre 1 et 6.
     * @return la ligneIndex désignée.
     */
    public String obtientLigneX(int x) {
        switch (x) {
            default:
            case 1:
                return ligne1;
            case 2:
                return ligne2;
            case 3:
                return ligne3;
            case 4:
                return ligne4;
            case 5:
                return ligne5;
            case 6:
                return ligne6;
            case 7:
                return ligne7;
        }
    }

    /**
     * Définit la ligneIndex spécifiée
     * @param ligneIndex la valeur à affecter à la ligneIndex
     * @param x un numéro de ligneIndex de 1 à 6
     */
    public void definitLigneX(String ligne, int x) {
        switch (x) {
            default:
                break;
            case 1:
                ligne1 = ligne;
                break;
            case 2:
                ligne2 = ligne;
                break;
            case 3:
                ligne3 = ligne;
                break;
            case 4:
                ligne4 = ligne;
                break;
            case 5:
                ligne5 = ligne;
                break;
            case 6:
                ligne6 = ligne;
                break;
            case 7:
                ligne7 = ligne;
                break;
        }
    }

    /**
     * Mélange les éléments de l'adresse.<br>
     * Les éléments suivants sont placés dans une ligneIndex aléatoirement choisie entre la 4 et la 6:
     * <ul>
     *     <li>chaque clé</li>
     *     <li>le code postal</li>
     *     <li>la ville</li>
     *     <li>la ligneIndex 4</li>
     * </ul>
     * @return
     */
    Adresse melangeLignes4et6() {
        Adresse a = this.clone();
        a.ligne1 = "";
        a.ligne2 = "";
        a.ligne3 = "";
        a.ligne4 = "";
        a.ligne5 = "";
        a.ligne6 = "";
        int ligne;
        for (int i = 0; i < cles.size(); i++) {
            ligne = 2 * r.nextInt(2) + 4;
            a.definitLigneX(concatene(a.obtientLigneX(ligne), cles.get(i)), ligne);
        }

        ligne = 2 * r.nextInt(2) + 4;
        a.definitLigneX(concatene(a.obtientLigneX(ligne), numero), ligne);
        a.definitLigneX(concatene(a.obtientLigneX(ligne), repetition), ligne);
        a.definitLigneX(concatene(a.obtientLigneX(ligne), numerossupplementaires), ligne);
        a.definitLigneX(concatene(a.obtientLigneX(ligne), typedevoie), ligne);
        a.definitLigneX(concatene(a.obtientLigneX(ligne), article), ligne);
        a.definitLigneX(concatene(a.obtientLigneX(ligne), libelle), ligne);

        ligne = 2 * r.nextInt(2) + 4;
        if (codepostal.length() > 0) {
            a.definitLigneX(concatene(a.obtientLigneX(ligne), codepostal), ligne);
        } else if (departement.length() > 0) {
            a.definitLigneX(concatene(a.obtientLigneX(ligne), departement), ligne);
        }

        ligne = 2 * r.nextInt(2) + 4;
        a.definitLigneX(concatene(a.obtientLigneX(ligne), ville), ligne);
        a.definitLigneX(concatene(a.obtientLigneX(ligne), arrondissement), ligne);

        return a;
    }

    /**
     * Mélange les éléments de l'adresse.<br>
     * @return
     */
    Adresse melangeLignes1et4et6() {
        Adresse a = this.clone();
        a.ligne1 = concatene(clePoizon, libellePoizon);
        a.ligne2 = "";
        a.ligne3 = "";
        a.ligne4 = "";
        a.ligne5 = "";
        a.ligne6 = "";
        int ligneIndex;
        for (int i = 0; i < cles.size(); i++) {
            ligneIndex = 2 * r.nextInt(2) + 4;
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), cles.get(i)), ligneIndex);
        }
        ligneIndex = 2 * r.nextInt(2) + 4;
        if (ligne4Poizon != null && ligne4Poizon.length() > 0) {
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), ligne4Poizon), ligneIndex);
        } else {
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), numero), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), repetition), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), numerossupplementaires), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), typedevoie), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), article), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), libelle), ligneIndex);
        }
        ligneIndex = 2 * r.nextInt(2) + 4;
        if (ligne6Poizon != null && ligne6Poizon.length() > 0) {
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), ligne6Poizon), ligneIndex);
        } else {
            if (codepostal.length() > 0) {
                a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), codepostal), ligneIndex);
            } else if (departement.length() > 0) {
                a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), departement), ligneIndex);
            }

            ligneIndex = 2 * r.nextInt(2) + 4;
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), ville), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), arrondissement), ligneIndex);
        }

        return a;
    }

    /**
     * Mélange les éléments de l'adresse.<br>
     * Les éléments suivants sont placés dans une ligneIndex aléatoirement choisie:
     * <ul>
     *     <li>chaque clé</li>
     *     <li>le code postal</li>
     *     <li>le département</li>
     *     <li>la ville</li>
     *     <li>la ligneIndex 4</li>
     * </ul>
     * @return
     */
    Adresse melangeElements(
            int nbLignes) {
        Adresse a = this.clone();
        a.ligne1 = "";
        a.ligne2 = "";
        a.ligne3 = "";
        a.ligne4 = "";
        a.ligne5 = "";
        a.ligne6 = "";
        a.ligne7 = "";
        int ligneIndex;
        for (int i = 0; i < cles.size(); i++) {
            ligneIndex = r.nextInt(nbLignes) + 1;
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), cles.get(i)), ligneIndex);
        }

        ligneIndex = r.nextInt(nbLignes) + 1;
        if (ligne4Poizon != null && ligne4Poizon.length() > 0) {
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), ligne4Poizon), ligneIndex);
        } else {
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), numero), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), repetition), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), numerossupplementaires), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), typedevoie), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), article), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), libelle), ligneIndex);
        }
        ligneIndex = r.nextInt(nbLignes) + 1;
        if (ligne6Poizon != null && ligne6Poizon.length() > 0) {
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), ligne6Poizon), ligneIndex);
        } else {
            if (codepostal.length() > 0) {
                a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), codepostal), ligneIndex);
            } else if (departement.length() > 0) {
                a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), departement), ligneIndex);
            }
            ligneIndex = r.nextInt(6) + 1;
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), ville), ligneIndex);
            a.definitLigneX(concatene(a.obtientLigneX(ligneIndex), arrondissement), ligneIndex);
        }
        return a;
    }

    /**
     * Mélange les donneesIn de l'adresse.
     * @return
     */
    Adresse melangeLignes(int nblignes) {
        int[] ordre = new int[nblignes];
        boolean[] lignes = new boolean[nblignes];

        for (int i = 0; i <
                nblignes; i++) {
            int val = r.nextInt(nblignes);
            while (lignes[val]) {
                val = r.nextInt(nblignes);
            }

            lignes[val] = true;
            ordre[i] = val + 1;
        }

        Adresse a = this.clone();
        for (int i = 1; i <= nblignes; i++) {
            a.definitLigneX(obtientLigneX(ordre[i - 1]), i);
        }

        return a;
    }

    /**
     * Concatène les chaînes spécifiées, avec un espace éventuel.
     * @param s1
     * @param s2
     * @return
     */
    private String concatene(String s1, String s2) {
        if (s1.length() == 0) {
            return s2;
        }

        if (s2.length() == 0) {
            return s1;
        }

        return s1 + ' ' + s2;
    }

    /**
     * Nettoye les donneesIn.
     */
    public void resetLignes() {
        ligne1 = "";
        ligne2 = "";
        ligne3 = "";
        ligne4 = "";
        ligne5 = "";
        ligne6 = "";
        ligne7 = "";
    }

    /**
     * Calcule les différentes donneesIn d'adresse.
     */
    public void calculeLignes() {
        resetLignes();
        ligne1 = concatene(clePoizon, libellePoizon);
        for (int i = 0; i <
                cles.size(); i++) {
            ligne3 = concatene(ligne3, cles.get(i));
        }

        if (ligne4Poizon != null && ligne4Poizon.length() > 0) {
            ligne4 = ligne4Poizon;
        } else {
            ligne4 = concatene(ligne4, numero);
            ligne4 = concatene(ligne4, repetition);
            ligne4 = concatene(ligne4, numerossupplementaires);
            ligne4 = concatene(ligne4, typedevoie);
            ligne4 = concatene(ligne4, article);
            ligne4 = concatene(ligne4, libelle);
        }

        if (ligne6Poizon != null && ligne6Poizon.length() > 0) {
            ligne6 = ligne6Poizon;
        } else {
            if (codepostal.length() > 0) {
                ligne6 = concatene(ligne6, codepostal);
            } else if (departement.length() > 0) {
                ligne6 = concatene(ligne6, departement);
            }

            ligne6 = concatene(ligne6, ville);
            ligne6 = concatene(ligne6, arrondissement);
        }

        ligne7 = pays;
    }

    /**
     * Calcule les donneesIn 4 et 6 (format 2)
     * @return
     */
    void calculeLignes1et4et6() {
        resetLignes();
        ligne1 = concatene(clePoizon, libellePoizon);
        for (int i = 0; i < cles.size(); i++) {
            ligne4 = concatene(ligne4, cles.get(i));
        }

        if (ligne4Poizon != null && ligne4Poizon.length() > 0) {
            ligne4 = ligne4Poizon;
        } else {
            ligne4 = concatene(ligne4, numero);
            ligne4 = concatene(ligne4, repetition);
            ligne4 = concatene(ligne4, numerossupplementaires);
            ligne4 = concatene(ligne4, typedevoie);
            ligne4 = concatene(ligne4, article);
            ligne4 = concatene(ligne4, libelle);
        }

        if (ligne6Poizon != null && ligne6Poizon.length() > 0) {
            ligne6 = ligne6Poizon;
        } else {
            if (codepostal.length() > 0) {
                ligne6 = concatene(ligne6, codepostal);
            } else if (departement.length() > 0) {
                ligne6 = concatene(ligne6, departement);
            }

            ligne6 = concatene(ligne6, ville);
            ligne6 = concatene(ligne6, arrondissement);
        }

    }

    void calculeLigne4() {
        resetLignes();
        ligne4 =
                concatene(clePoizon, libellePoizon);
        for (int i = 0; i <
                cles.size(); i++) {
            ligne4 = concatene(ligne4, cles.get(i));
        }

        if (ligne4Poizon != null && ligne4Poizon.length() > 0) {
            ligne4 = concatene(ligne4, ligne4Poizon);
        } else {
            ligne4 = concatene(ligne4, numero);
            ligne4 =
                    concatene(ligne4, repetition);
            ligne4 =
                    concatene(ligne4, numerossupplementaires);
            ligne4 =
                    concatene(ligne4, typedevoie);
            ligne4 =
                    concatene(ligne4, article);
            ligne4 =
                    concatene(ligne4, libelle);
        }

        if (ligne6Poizon != null && ligne6Poizon.length() > 0) {
            ligne4 = concatene(ligne4, ligne6Poizon);
        } else {
            if (codepostal.length() > 0) {
                ligne4 = concatene(ligne4, codepostal);
            } else if (departement.length() > 0) {
                ligne4 = concatene(ligne4, departement);
            }

            ligne4 = concatene(ligne4, ville);
            ligne4 =
                    concatene(ligne4, arrondissement);
        }

    }

    /**
     * Génère un arrondissement aléatoire.<br>
     * Les nombres 01 à 20 sont utilisés.
     */
    public void genereArrondissement() {
        arrondissement = Integer.toString(r.nextInt(20) + 1);
    }

    /**
     * Génère un complément pour une clé.<br>
     * <ul><li>Un nombre</li>
     *     <li>Un caractère</li>
     *     <li>Une chaine</li></ul>
     * @return le complément à la nom de clé
     */
    private String genereComplement() {
        int mode = r.nextInt(3);
        switch (mode) {
            default:
            case 0:
                return Integer.toString(r.nextInt(9999) + 1);
            case 1:
                return Character.toString((char) ('a' + r.nextInt(26)));
            case 2:
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <
                        r.nextInt(5); i++) {
                    sb.append((char) ('a' + r.nextInt(26)));
                }

                return sb.toString();
        }

    }

    /**
     * Génère un nom de clé.
     * @return le nom de clé généré
     */
    private String genereNomCle() {
        int typecle = r.nextInt(9);

        switch (typecle) {
            default:
            case 0:
                return "appartement";
            case 1:
                return "appt";
            case 2:
                return "escalier";
            case 3:
                return "etage";
            case 4:
                return "monsieur";
            case 5:
                return "madame";
            case 6:
                return "batiment";
            case 7:
                return "chez";
            case 8:
                return "bat";
        }

    }

    /**
     * Génère 1 à 3 clés aléatoires.<br>
     * Les clés sont générées parmi:
     * <ul>
     *   <li>appartement</li>
     *   <li>porte</li>
     *   <li>escalier</li>
     *   <li>etage</li>
     *   <li>monsieur</li>
     *   <li>madame</li>
     *   <li>batiment</li>
     *   <li>chez</li>
     * </ul>
     * leurs compléments sont des nombres, des lettres ou des chaines aléatoires.
     */
    public void genereCles() {
        int nbcles = r.nextInt(3) + 1;

        for (int i = 0; i <
                nbcles; i++) {
            String nomdecle = genereNomCle();
            String valeurcle = genereComplement();

            cles.add(nomdecle + ' ' + valeurcle);
        }

    }

    /**
     * Retourne un nombre aléatoire entre min et max.<br>
     * Si min et max sont nuls, une chaine vide est retournée.
     * @return
     */
    private String genereNombre(int min, int max) {
        try {
            if (max == 0) {
                return "";
            } else if (min == max) {
                return Integer.toString(min);
            } else {
                return Integer.toString(r.nextInt(max - min) + min);
            }

        } catch (IllegalArgumentException iae) {
            System.out.println("Attention : la voie " + this.toString() + " a des bornes invalides.");
            return Integer.toString(r.nextInt(min - max) + max);
        }

    }

    /**
     * Retourne un nombre aléatoire entre 1 et 9999.
     * @return
     */
    private String genereNombre() {
        return Integer.toString(r.nextInt(9999) + 1);
    }

    /**
     * Génère un numéro aléatoire.<br>
     * Le numéro est compris entre numeromin et numeromax.
     */
    public void genereNumero() {
        numero = genereNombre(numeromin, numeromax);
    }

    /**
     * Génère une répétition aléatoire.<br>
     * La répétition est une lettre de l'alphabet ou
     * <ul><li>BIS</li>
     *     <li>TER</li>
     *     <li>QUINQUIES</li>
     *     <li>QUATER</li></ul>
     */
    public void genereRepetition() {
        int rep = r.nextInt(30);

        if (rep >= 26) {
            switch (rep - 26) {
                default:
                case 0:
                    repetition = "BIS";
                    break;
                case 1:
                    repetition = "TER";
                    break;
                case 2:
                    repetition = "QUINQUIES";
                    break;
                case 3:
                    repetition = "QUATER";
                    break;
            }

        } else {
            repetition = Character.toString((char) ('a' + rep));
        }

    }

    /**
     * Génère des articles ou des répétitions
     */
    private String genereArticleRepetition() {
        int rep = r.nextInt(34);

        if (rep >= 26) {
            switch (rep - 26) {
                default:
                case 0:
                    return "BIS";
                case 1:
                    return "TER";
                case 2:
                    return "QUINQUIES";
                case 3:
                    return "QUATER";
                case 4:
                    return "LE";
                case 5:
                    return "LA";
                case 6:
                    return "LES";
                case 7:
                    return "DU";
                case 8:
                    return "AU";
            }

        } else {
            return Character.toString((char) ('a' + rep));
        }

    }

    /**
     * Génère 1 à 4 numéros supplémentaires.<br>
     * Les numéros supplémentaires sont des suites d'articles, de numéros, et de répétitions, parmi:
     * <ul><li>les lettres de l'alphabet</li>
     *     <li>les nombres de 1 à 9999</li>
     *     <li>BIS</li>
     *     <li>TER</li>
     *     <li>QUINQUIES</li>
     *     <li>QUATER</li>
     *     <li>LE</li>
     *     <li>LA</li>
     *     <li>LES</li>
     *     <li>DU</li>
     *     <li>AU</li>
     * </ul>
     */
    public void genereNumerosSupplementaires() {
        int nb = r.nextInt(4) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <
                nb; i++) {
            if (i > 0) {
                sb.append(' ');
            }

            sb.append(genereNombre());
            boolean choix = r.nextBoolean();
            if (choix) {
                sb.append(' ');
                sb.append(genereArticleRepetition());
            }

        }
        numerossupplementaires = sb.toString();
    }

    public void load(String line) throws NumberFormatException {
        String[] data = line.split("\t");
        modele = data[0].substring(1, data[0].length() - 1);
        nbfautes = Integer.parseInt(data[1]);
        structure = Integer.parseInt(data[2]);
        departement = data[3].substring(1, data[3].length() - 1);
        ligne1 = data[4].substring(1, data[4].length() - 1);
        ligne2 = data[5].substring(1, data[5].length() - 1);
        ligne3 = data[6].substring(1, data[6].length() - 1);
        ligne4 = data[7].substring(1, data[7].length() - 1);
        ligne5 = data[8].substring(1, data[8].length() - 1);
        ligne6 = data[9].substring(1, data[9].length() - 1);
        ligne7 = data[10].substring(1, data[10].length() - 1);
        origine = new Adresse();
        origine.ligne4 = data[11].substring(1, data[11].length() - 1);
        origine.ligne6 = data[12].substring(1, data[12].length() - 1);
        origine.ligne7 = data[13].substring(1, data[13].length() - 1);
        ligne4SansFaute = data[11].substring(1, data[11].length() - 1);
        ligne6SansFaute = data[12].substring(1, data[12].length() - 1);
        ligne7SansFaute = data[13].substring(1, data[13].length() - 1);
        ligne1SansFaute = data[14].substring(1, data[14].length() - 1);
        idPoizon = data[15].substring(1, data[15].length() - 1);
        codeinsee = data[16].substring(1, data[16].length() - 1);
        codeSovAc3 = data[17].substring(1, data[17].length() - 1);
        x = data[18].substring(1, data[18].length() - 1);
        y = data[19].substring(1, data[19].length() - 1);
    }

    /**
     * Ecrit l'adresse au format des fichiers d'échantillon.
     */
    public String ecrit() {
        StringBuilder sb = new StringBuilder();

        sb.append('"');
        sb.append(modele);
        sb.append("\"\t");
        sb.append(nbfautes);
        sb.append("\t");
        sb.append(structure);
        sb.append("\t\"");
        sb.append(departement);
        sb.append("\"\t\"");
        sb.append(ligne1);
        sb.append("\"\t\"");
        sb.append(ligne2);
        sb.append("\"\t\"");
        sb.append(ligne3);
        sb.append("\"\t\"");
        sb.append(ligne4);
        sb.append("\"\t\"");
        sb.append(ligne5);
        sb.append("\"\t\"");
        sb.append(ligne6);
        sb.append("\"\t\"");
        sb.append(ligne7);
        sb.append("\"\t\"");
        sb.append((origine != null) ? origine.ligne4 : "");
        sb.append("\"\t\"");
        sb.append((origine != null) ? origine.ligne6 : "");
        sb.append("\"\t\"");
        sb.append((origine != null) ? origine.ligne7 : "");
        sb.append("\"\t\"");
        sb.append((origine != null) ? origine.ligne1 : "");
        sb.append("\"\t\"");
        sb.append(idPoizon);
        sb.append("\"\t\"");
        sb.append(codeinsee);
        sb.append("\"\t\"");
        sb.append(codeSovAc3);
        sb.append("\"\t\"");
        sb.append(x);
        sb.append("\"\t\"");
        sb.append(y);
        sb.append('"');

        return sb.toString();
    }

    /**
     * Retourne un équivalent sous forme de chaîne.
     * @return
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i <
                cles.size(); i++) {
            str += cles.get(i) + " ";
        }

        str += numero + " " + repetition + " " + numerossupplementaires + " " + libelle + " " + ville + " " + arrondissement + " " + pays;
        return str;
    }

    /**
     * Retourne un équivalent sous forme de chaîne.
     * @return
     */
    public String toString(
            boolean lignes) {
        if (!lignes) {
            return toString();
        } else {
            String str = ligne1 + ", " + ligne2 + ", " + ligne3 + ", " + ligne4 + ", " + ligne5 + ", " + ligne6 + ", " + ligne7;
            return str;
        }

    }

    /**
     * Petits tests unitaires.
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Tests de la classe Adresse.");
        System.out.println("Teste l'ajout d'un numéro, d'une répétition, des numéros supplémentaires, des cles, de l'arrondissement:");
        Adresse[] adresses = new Adresse[10];
        for (int i = 0; i <
                adresses.length; i++) {
            Adresse a = new Adresse();
            a.ville = "PARIS";
            a.libelle = "RUE TEST";
            a.genereArrondissement();
            a.genereCles();
            a.genereNumero();
            a.genereRepetition();
            a.genereNumerosSupplementaires();
            System.out.println(a.toString());
            adresses[i] = a;
        }

        System.out.println("Calcule les lignes:");
        for (int i = 0; i <
                adresses.length; i++) {
            Adresse a = adresses[i];
            a.calculeLignes();
            System.out.println("Ligne 1 " + a.ligne1);
            System.out.println("Ligne 2 " + a.ligne2);
            System.out.println("Ligne 3 " + a.ligne3);
            System.out.println("Ligne 4 " + a.ligne4);
            System.out.println("Ligne 5 " + a.ligne5);
            System.out.println("Ligne 6 " + a.ligne6);
        }

        System.out.println("Teste quelques mélanges:");
        for (int i = 0; i <
                adresses.length; i++) {
            Adresse a = adresses[i];
            int l_r = Adresse.r.nextInt(3);
            switch (l_r) {
                case 0:
                    System.out.println("Mélange les éléments");
                    a =
                            a.melangeElements(6);
                    break;
                case 1:
                    System.out.println("Mélange les lignes");
                    a =
                            a.melangeLignes(6);
                    break;
                case 2:
                    System.out.println("Mélange les lignes 4 et 6");
                    a =
                            a.melangeLignes1et4et6();
                    break;
            }

            System.out.println("Ligne 1 " + a.ligne1);
            System.out.println("Ligne 2 " + a.ligne2);
            System.out.println("Ligne 3 " + a.ligne3);
            System.out.println("Ligne 4 " + a.ligne4);
            System.out.println("Ligne 5 " + a.ligne5);
            System.out.println("Ligne 6 " + a.ligne6);
        }

        System.out.println("Teste la génération de fautes");
        for (int i = 0; i <
                adresses.length; i++) {
            Adresse a = adresses[i];
            Adresse b = a.introduitFautes(i, 6);

            System.out.println("Introduit " + i + " faute(s)");
            System.out.println("Ligne 1 " + a.ligne1 + " devient " + b.ligne1);
            System.out.println("Ligne 2 " + a.ligne2 + " devient " + b.ligne2);
            System.out.println("Ligne 3 " + a.ligne3 + " devient " + b.ligne3);
            System.out.println("Ligne 4 " + a.ligne4 + " devient " + b.ligne4);
            System.out.println("Ligne 5 " + a.ligne5 + " devient " + b.ligne5);
            System.out.println("Ligne 6 " + a.ligne6 + " devient " + b.ligne6);
        }

        System.out.println("Teste l'égalité entre adresses.");
        Adresse a = new Adresse();
        a.ligne4 = "RUE DE PARIS";
        a.ligne6 = "75 PARIS";
        Adresse b = new Adresse();
        b.ligne4 = "RUE DE PARIS";
        b.ligne6 = "75005 PARIS";
        if (a.compareTo(b.ligne4, b.ligne6, null)) {
            System.out.println("OK");
        } else {
            System.out.println("ECHEC");
        }

    }

    /**
     * Effectue une délétion aléatoire sur la ligneIndex spécifiée<br>
     * La ligneIndex ne doit pas être vide.
     * @param ligneIndex une ligneIndex entre 1 et 6.
     */
    private void introduitDeletion(int ligne) {
        String chaine = obtientLigneX(ligne);
        int index = r.nextInt(chaine.length()); // obtient une position aléatoire.
        chaine =
                chaine.substring(0, index) + chaine.substring(index + 1);
        definitLigneX(chaine, ligne);
    }

    /**
     * Obtient un caractère choisi aléatoirement.
     */
    private char obtientCaractere() {
        int index = r.nextInt(26 * 2 + 9 + 3 + 1);
        if (index < 26) {
            return (char) ('a' + (char) index);
        }

        if (index < 52) {
            return (char) ('A' + (char) (index - 26));
        }

        if (index < 61) {
            return (char) ('0' + (char) (index - 52));
        }

        if (index == 61) {
            return ',';
        }

        if (index == 62) {
            return '-';
        }

        if (index == 63) {
            return '&';
        }

        return ' ';
    }

    /**
     * Effectue une insertion aléatoire sur la ligneIndex spécifiée<br>
     * La ligneIndex ne doit pas être vide.
     * @param ligneIndex une ligneIndex entre 1 et 6.
     */
    private void introduitInsertion(int ligne) {
        String chaine = obtientLigneX(ligne);
        int index = r.nextInt(chaine.length()); // obtient une position aléatoire.
        chaine =
                chaine.substring(0, index) + obtientCaractere() + chaine.substring(index);
        definitLigneX(chaine, ligne);
    }

    /**
     * Effectue une modification aléatoire sur la ligneIndex spécifiée.
     * La ligneIndex ne doit pas être vide.
     * @param ligneIndex une ligneIndex entre 1 et 6.
     */
    private void introduitModification(int ligne) {
        String chaine = obtientLigneX(ligne);
        int index = r.nextInt(chaine.length()); // obtient une position aléatoire.
        chaine =
                chaine.substring(0, index) + obtientCaractere() + chaine.substring(index + 1);
        definitLigneX(chaine, ligne);
    }

    /**
     * Introduit des fautes dans une adresse.<br>
     * Les fautes sont introduites aléatoirement dans une des donneesIn non vide de l'adresse.<br>
     * Il peut s'agir d'insertion, de suppression, de modification de lettres.
     * Les donneesIn doivent avoir été calculées (avec calculeLignes()).
     * @param nbfautes le nombre de fautes à introduire.
     */
    public Adresse introduitFautes(
            int nbfautes, int nbLines) {
        Adresse res = this.clone();

        res.nbfautes = nbfautes;
        boolean allEmpty = false;
        while (nbfautes-- > 0) {
            // Cherche une ligneIndex non vide aléatoire
            int firstLigne = r.nextInt(nbLines) + 1;
            int ligne = firstLigne;
            while (res.obtientLigneX(ligne).length() == 0) {
                ligne++;
                if (ligne > nbLines) {
                    ligne = 1;
                }

                if (ligne == firstLigne) {
                    allEmpty = true;
                    break;
                }

            }
            if (!allEmpty) {
                // Détermine le type de faute
                int type = r.nextInt(3); // 0 pour délétion, 1 pour insertion, 2 pour modification

                switch (type) {
                    default:
                    case 0:
                        res.introduitDeletion(ligne);
                        break;
                    case 1:
                        res.introduitInsertion(ligne);
                        break;
                    case 2:
                        res.introduitModification(ligne);
                        break;
                }

            }
        }

        return res;
    }

    /**
     * Restructure le JDONREFservice spécifié. Les cas d'erreurs sont ignorés.
     */
    public Adresse normalise(
            JDONREFService JDONREFService, int[] services, int operation, String[] options) {
        try {
            JDONREF port = JDONREFService.getJDONREFPort();
            ArrayList<String> donnees = new ArrayList<String>();
            donnees.add(ligne1);
            donnees.add(ligne2);
            donnees.add(ligne3);
            donnees.add(ligne4);
            donnees.add(ligne5);
            donnees.add(ligne6);
            donnees.add((ligne7 != null && !ligne7.trim().equals("")) ? ligne7 : "");

            final List<String> optionsList = new ArrayList<String>();
            for (String option : options) {
                optionsList.add(option);
            }

            final List<Integer> servicesList = new ArrayList<Integer>();
            for (int serviceId : services) {
                servicesList.add(serviceId);
            }

            // APPEL DU SERVICE
            final ResultatNormalisation resultat = port.normalise(2, servicesList, operation, donnees, optionsList);

            // TRAITEMENT DU RESULTAT
            codeerreur = 0;
            if (resultat != null) {
                final StringBuilder sb = new StringBuilder();
                if (resultat.getCodeRetour() == 0) {
                    // les cas d'erreurs ne sont pas gérés.
                    final List<ResultatErreur> erreurList = resultat.getErreurs();
                    final ResultatErreur erreur = erreurList.get(0);
                    codeerreur = 11;
                    erreurjdonref = erreur.getCode();
                    messagejdonref = erreur.getMessage();

                    // RETOUR
                    final int erreurSize = erreurList.size();
                    for (int i = 0; i <
                            erreurSize; i++) {
                        final ResultatErreur anerreur = erreurList.get(i);
                        sb.append(anerreur.getCode());
                        sb.append(" ");
                        sb.append(anerreur.getMessage());
                        sb.append(" ");
                    }

                    retour = sb.toString();
                    return null;

                } else {
                    Adresse a = this.clone();
                    final List<PropositionNormalisation> propositionList = resultat.getPropositions();
                    final PropositionNormalisation proposition = propositionList.get(0);
                    final List<String> resdonnees = proposition.getDonnees();
                    a.ligne1 = resdonnees.get(0);
                    a.ligne2 = resdonnees.get(1);
                    a.ligne3 = resdonnees.get(2);
                    a.ligne4 = resdonnees.get(3);
                    a.ligne5 = resdonnees.get(4);
                    a.ligne6 = resdonnees.get(5);
                    if (resdonnees.size() > 6) {
                        a.ligne7 = resdonnees.get(6);
                    }
                    // Correction WA 09/2011 : cas ou on a un dpt sur 1 car.
                    if (a.ligne6.length() > 1) {
                        a.departement = a.ligne6.substring(0, 2);
                    } else {
                        a.departement = "";
                    }
                    a.service = proposition.getService();
                    // RETOUR
                    final int size = propositionList.size();
                    for (int i = 0; i <
                            size; i++) {
                        final PropositionNormalisation aproposition = propositionList.get(i);
                        final List<String> somedonnees = aproposition.getDonnees();
                        for (String adonnee : somedonnees) {
                            sb.append(adonnee);
                            sb.append(" ");
                        }

                        final List<String> someoptions = aproposition.getOptions();
                        for (String anoption : someoptions) {
                            sb.append(anoption);
                            sb.append(" ");
                        }

                        sb.append(aproposition.getService());
                        sb.append(" ");
                    }

                    retour = sb.toString();
                    service = proposition.getService();
                    return a;
                }

            }
            retour = "";
            codeerreur = 14;
            return null;
        } catch (Exception e) {
            retour = "";
            codeerreur = 15;
            Logger.getLogger("Adresse").log(Level.SEVERE, "Erreur durant la restructuration de " + toString() + " sous la forme " + toString(true), e);
            return null;
        }

    }

    /**
     * Valide l'adresse, seule la première est retournée. Les cas d'erreurs sont ignorés.<br>
     * Si l'origine de l'adresse est connue, elle est recherchée parmi les propositions. Si elle n'est pas trouvée, NON
     * null est retourné.
     * null est retourné si aucune adresse n'est trouvée.
     */
    public Adresse valide(
            JDONREFService JDONREFservice, int[] services, int operation, String[] options) {
        try {
            JDONREF port = JDONREFservice.getJDONREFPort();
            ArrayList<String> donneesIn = new ArrayList<String>();
            donneesIn.add(ligne1);
            donneesIn.add(ligne2);
            donneesIn.add(ligne3);
            donneesIn.add(ligne4);
            donneesIn.add(ligne5);
            donneesIn.add(ligne6);
            donneesIn.add((ligne7 != null && !ligne7.trim().equals("")) ? ligne7 : "FRANCE");

            final List<String> idsIn = new ArrayList<String>();
            idsIn.add(idPoizon);
            idsIn.add("");
            idsIn.add("");
            idsIn.add(idVoie);
            idsIn.add("");
            idsIn.add(codeinsee);
            idsIn.add(codeSovAc3);
            final List<String> optionsIn = new ArrayList<String>();
            for (String option : options) {
                optionsIn.add(option);
            }

            final List<Integer> servicesList = new ArrayList<Integer>();
            for (int serviceId : services) {
                servicesList.add(serviceId);
            }

            // APPEL DU SERVICE
            final ResultatValidation resultat = port.valide(2, servicesList, operation, donneesIn, idsIn, optionsIn);

            // TRAITEMENT DU RESULTAT
            codeerreur = 0;
            if (resultat != null) {
                final StringBuilder sb = new StringBuilder();
                if (resultat.getCodeRetour() == 0) {
                    codeerreur = 1;
                    final List<ResultatErreur> erreurList = resultat.getErreurs();
                    final ResultatErreur erreur = erreurList.get(0);
                    erreurjdonref = erreur.getCode();
                    messagejdonref = erreur.getMessage();

                    // RETOUR
                    for (ResultatErreur anerreur : erreurList) {
                        sb.append(anerreur.getCode());
                        sb.append(" ");
                        sb.append(anerreur.getMessage());
                        sb.append(" ");
                    }

                    retour = sb.toString();
                    return null;
                } else {
                    final List<PropositionValidation> propositionList = resultat.getPropositions();
                    if (propositionList.size() > 0) {
                        PropositionValidation proposition = propositionList.get(0); // Note la plus grande.

                        boolean resultMatch = false;
                        for (PropositionValidation aproposition : propositionList) {
                            final List<String> donneesOut = aproposition.getDonnees();
                            final List<String> idsOut = aproposition.getIds();

                            // RETOUR
                            for (String adonnee : donneesOut) {
                                sb.append(adonnee);
                                sb.append(" ");
                            }

                            for (String anid : idsOut) {
                                sb.append(anid);
                                sb.append(" ");
                            }

                            final List<String> optionsOut = aproposition.getOptions();
                            for (String anoption : optionsOut) {
                                sb.append(anoption);
                                sb.append(" ");
                            }

                            final List<String> donneesOrigines = aproposition.getDonneesOrigine();
                            for (String adonneeOrigine : donneesOrigines) {
                                sb.append(adonneeOrigine);
                                sb.append(" ");
                            }

                            sb.append(aproposition.getCode());
                            sb.append(" ");
                            sb.append(aproposition.getService());
                            sb.append(" ");
                            sb.append(aproposition.getNote());
                            sb.append(" ");
                            sb.append(aproposition.getT0());
                            sb.append(" ");
                            sb.append(aproposition.getT1());
                            sb.append(" ");

                            // COMPARAISON DES RESULTATS
                            if (!resultMatch) {
                                boolean match = compareTo(donneesOut.get(0), donneesOut.get(3), donneesOut.get(5),
                                        (donneesOut.size() > 6) ? donneesOut.get(6) : null);
                                if (match) {
                                    resultMatch = true;
                                    proposition = aproposition;
                                }

                            }
                        }

                        retour = sb.toString();
                        service = proposition.getService();

                        // RESULTAT RETENU                        
                        final Adresse a = this.clone();
                        final List<String> resdonnees = proposition.getDonnees();
                        a.ligne1 = resdonnees.get(0);
                        a.ligne2 = resdonnees.get(1);
                        a.ligne3 = resdonnees.get(2);
                        a.ligne4 = resdonnees.get(3);
                        a.ligne5 = resdonnees.get(4);
                        a.ligne6 = resdonnees.get(5);
                        a.ligne7 = (resdonnees.size() > 6) ? resdonnees.get(6) : "";
                        final List<String> resIds = proposition.getIds();
                        a.idPoizon = resIds.get(0); // Poizon
                        a.idVoie = resIds.get(3); // voie
                        a.codeinsee = resIds.get(5); // commune
                        if (resIds.size() > 6) { // CODE PAYS
                            a.codeSovAc3 = resIds.get(6);
                        }

                        a.date = proposition.getT0();
                        a.departement = (a.ligne6.length() > 1) ? a.ligne6.substring(0, 2) : "";
                        a.note = proposition.getNote();
                        a.service = proposition.getService();
                        if (resultMatch) {
                            return a;
                        } else {
                            codeerreur = 2;
                            return null;
                        }

                    } else {
                        retour = "";
                        codeerreur = 3;
                        return null;
                    }

                }
            } else {
                retour = "";
                codeerreur = 4;
                return null;
            }

        } catch (Exception e) {
            retour = "";
            codeerreur = 5;
            Logger.getLogger("Adresse").log(Level.SEVERE, "Erreur durant la validation de " + toString() + " sous la forme " + toString(true), e);
            return null;
        }

    }

    /**
     * Effectue un géocodage.<br>
     * Codes d'erreurs concernant le géocodage
     * <ul>
     *   <li>6 si la méthode a retourné une erreur.</li>
     *   <li>7 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>8 si le résultat n'a pas de sens (problème réseau).</li>
     *   <li>9 si jdonref a retourné la valeur null</li>
     *   <li>10 si une exception s'est produite</li>
     *   </ul>
     * @param JDONREFservice
     * @param voie s'il est à false, le géocodage est effectué à la ville.
     * @return false en cas d'erreur
     */
    public boolean geocode(JDONREFService JDONREFservice, int[] services, String[] options) {
        try {
            x = y = null;
            JDONREF port = JDONREFservice.getJDONREFPort();

            final List<String> optionsList = new ArrayList<String>();
            for (String option : options) {
                optionsList.add(option);
            }

            final List<Integer> servicesList = new ArrayList<Integer>();
            for (int serviceId : services) {
                servicesList.add(serviceId);
            }
            // PARAM DONNEES
            final List<String> donnees = new ArrayList<String>();
            donnees.add(ligne1);
            donnees.add(ligne2);
            donnees.add(ligne3);
            donnees.add(ligne4);
            donnees.add(ligne5);
            donnees.add(ligne6);
            donnees.add((ligne7 != null && !ligne7.trim().equals("")) ? ligne7 : "FRANCE");

            // PARAM IDS
            final List<String> ids = new ArrayList<String>();
            ids.add(idPoizon);
            ids.add("");
            ids.add("");
            ids.add(idVoie);
            ids.add("");
            ids.add(codeinsee);
            ids.add(codeSovAc3);

            // APPEL DU SERVICE
            final ResultatGeocodage resultat = port.geocode(2, servicesList, donnees, ids, optionsList);

            // TRAITEMENT DU RESULTAT
            codeerreur = 0;
            if (resultat != null) {
                final StringBuilder sb = new StringBuilder();
                if (resultat.getCodeRetour() == 0) {
                    codeerreur = 6;
                    final List<ResultatErreur> erreurList = resultat.getErreurs();
                    final ResultatErreur erreur = erreurList.get(0);
                    erreurjdonref = erreur.getCode();
                    messagejdonref = erreur.getMessage();
                    // RETOUR
                    for (ResultatErreur anerreur : erreurList) {
                        sb.append(anerreur.getCode());
                        sb.append(" ");
                        sb.append(anerreur.getMessage());
                        sb.append(" ");
                    }

                    retour = sb.toString();
                    return false;
                } else {
                    final List<PropositionGeocodage> propositionList = resultat.getPropositions();
                    if (propositionList.size() > 0) {
                        final PropositionGeocodage proposition = propositionList.get(0);
                        x = proposition.getX();
                        y = proposition.getY();
                        projection = proposition.getProjection();
                        service = proposition.getService();

                        // RETOUR
                        for (PropositionGeocodage aproposition : propositionList) {
                            sb.append(aproposition.getDate());
                            sb.append(" ");
                            sb.append(aproposition.getProjection());
                            sb.append(" ");
                            sb.append(aproposition.getReferentiel());
                            sb.append(" ");
                            sb.append(aproposition.getService());
                            sb.append(" ");
                            sb.append(aproposition.getType());
                            sb.append(" ");
                            sb.append(aproposition.getX());
                            sb.append(" ");
                            sb.append(aproposition.getY());
                            sb.append(" ");
                        }

                        retour = sb.toString();
                        return true;
                    } else {
                        retour = "";
                        codeerreur = 8;
                        return false;
                    }

                }
            } else {
                retour = "";
                codeerreur = 9;
                return false;
            }

        } catch (Exception e) {
            retour = "";
            codeerreur = 10;
            Logger.getLogger("Adresse").log(Level.SEVERE, "Erreur durant le géocodage de " + toString() + " sous la forme " + toString(true), e);
            return false;
        }

    }

    /**
     * Reverse le JDONREFservice spécifié. Les cas d'erreurs sont ignorés.
     */
    public boolean reverse(JDONREFService JDONREFservice, int[] services, double distance, String[] options) {
        try {
            JDONREF port = JDONREFservice.getJDONREFPort();
            ArrayList<String> donneesIn = new ArrayList<String>();
            donneesIn.add(x);
            donneesIn.add(y);

            final List<String> optionsList = new ArrayList<String>();
            for (String option : options) {
                optionsList.add(option);
            }

            final List<Integer> servicesList = new ArrayList<Integer>();
            for (int serviceId : services) {
                servicesList.add(serviceId);
            }

            // APPEL DU SERVICE
            final ResultatGeocodageInverse resultat = port.reverse(2, servicesList, donneesIn, distance, optionsList);

            // TRAITEMENT DU RESULTAT
            codeerreur = 0;
            if (resultat != null) {
                final StringBuilder sb = new StringBuilder();
                if (resultat.getCodeRetour() == 0) {
                    // les cas d'erreurs ne sont pas gérés.
                    final List<ResultatErreur> erreurList = resultat.getErreurs();
                    final ResultatErreur erreur = erreurList.get(0);
                    codeerreur = 16;
                    erreurjdonref = erreur.getCode();
                    messagejdonref = erreur.getMessage();

                    // RETOUR
                    for (ResultatErreur anerreur : erreurList) {
                        sb.append(anerreur.getCode());
                        sb.append(" ");
                        sb.append(anerreur.getMessage());
                        sb.append(" ");
                    }

                    retour = sb.toString();
                    return false;

                } else {
                    final List<PropositionGeocodageInverse> propositionList = resultat.getPropositions();
                    if (propositionList.size() > 0) {
                        PropositionGeocodageInverse proposition = propositionList.get(0);

                        for (PropositionGeocodageInverse aproposition : propositionList) {
                            final List<String> donneesOut = aproposition.getDonnees();
                            final List<String> idsOut = aproposition.getIds();
                            // RETOUR
                            for (String adonnee : donneesOut) {
                                sb.append(adonnee);
                                sb.append(" ");
                            }

                            for (String anid : idsOut) {
                                sb.append(anid);
                                sb.append(" ");
                            }

                            final List<String> optionsOut = aproposition.getOptions();
                            for (String anoption : optionsOut) {
                                sb.append(anoption);
                                sb.append(" ");
                            }

                            final List<String> donneesOrigines = aproposition.getDonneesOrigine();
                            for (String adonneeOrigine : donneesOrigines) {
                                sb.append(adonneeOrigine);
                                sb.append(" ");
                            }

                            sb.append(aproposition.getCode());
                            sb.append(" ");
                            sb.append(aproposition.getReferentiel());
                            sb.append(" ");
                            sb.append(aproposition.getService());
                            sb.append(" ");
                            sb.append(aproposition.getT0());
                            sb.append(" ");
                            sb.append(aproposition.getT1());
                            sb.append(" ");
                            sb.append(aproposition.getDistance());
                            sb.append(" ");
                            sb.append(aproposition.getX());
                            sb.append(" ");
                            sb.append(aproposition.getY());
                            sb.append(" ");

                        }

                        retour = sb.toString();

                        // RESULTAT RETENU (PAS DE COMPARAISON POUR LE REVERSE)
                        final List<String> resdonnees = proposition.getDonnees();
                        ligne1 = resdonnees.get(0);
                        ligne2 = resdonnees.get(1);
                        ligne3 = resdonnees.get(2);
                        ligne4 = resdonnees.get(3);
                        ligne5 = resdonnees.get(4);
                        ligne6 = resdonnees.get(5);
                        if (resdonnees.size() > 6) {
                            ligne7 = resdonnees.get(6);
                        }
                        // Correction WA 09/2011 : cas ou on a un dpt sur 1 car.
                        if (ligne6.length() > 1) {
                            departement = ligne6.substring(0, 2);
                        } else {
                            departement = "";
                        }

                        x = proposition.getX();
                        y = proposition.getY();
                        service = proposition.getService();

                        return true;
                    } else {
                        codeerreur = 18;
                        retour = serviceReverse;
                        return false;
                    }

                }
            } else {
                retour = "";
                codeerreur = 19;
                return false;
            }

        } catch (Exception e) {
            codeerreur = 20;
            retour = "";
            Logger.getLogger("Adresse").log(Level.SEVERE, "Erreur durant le geocodage inverse de " + toString() + " sous la forme " + toString(true), e);
            return false;
        }

    }

    /**
     * Reverse le JDONREFservice spécifié. Les cas d'erreurs sont ignorés.
     */
    public boolean reverse(JDONREFService JDONREFservice) {
        final int serviceId = (serviceReverse != null && serviceReverse.trim().length() > 0) ? Integer.parseInt(serviceReverse) : 1;
        final String option = "projection=" + projection;

        return reverse(JDONREFservice, new int[]{serviceId}, 1, new String[]{option});
    }
}
