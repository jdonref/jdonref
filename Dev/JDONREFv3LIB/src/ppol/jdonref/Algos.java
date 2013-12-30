/*
 * Algos.java
 *
 * Created on 27 mars 2008, 15:40
 * 
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
package ppol.jdonref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Algorithmes simples génériques.<br>
 * Les algorithmes de cette classe ne nécessitent pas d'accès à une base de donnée
 * ou de leur fournir une liste de mots.
 * @author jmoquet
 */
public class Algos {

    static final String vide = "";
    static Random r = new Random(Calendar.getInstance().getTimeInMillis());

    /**
     * Retourne le plus petit des trois entiers.
     * @return le plus petit des trois entiers
     */
    public static int min(int i, int i0, int i1) {
        if (i < i0) {
            if (i1 < i) {
                return i1;
            }
            return i;
        } else {
            if (i0 < i1) {
                return i0;
            }
            return i1;
        }
    }

    /**
     * Retourne le nombre de mots (séparés par des espaces) de la chaine.
     * @return le nombre de mots
     */
    public static int nombreDeMots(String chaine) {
        int state = 0;
        int count = 0;
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                            break;
                        default:
                            count++;
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        default:
                            break;
                        case ' ':
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        return count;
    }
    final static String zerozero = "00";

    /**
     * Ajoute un zéro si l'arrondissement n'est composé que d'un seul chiffre.
     * @param arrondissement
     * @return Le nombre avec un zéro éventuel pour tenir sur deux caractères.
     */
    public static String deuxChiffresObligatoires(String arrondissement) {
        if (arrondissement == null) {
            return zerozero;
        }
        if (arrondissement.length() == 1) {
            return '0' + arrondissement;
        }
        return arrondissement;
    }

    /**
     * Retourne vrai si un caractère alphanumérique est présent après l'index spécifié dans la chaine.
     * @param chaine la chaine a scanner
     * @param obtientIndexRepetition l'index à partir duquel commencer le scan
     * @return vrai si un caractère alphanumérique est présent après l'index spécifié dans la chaine.
     */
    public static boolean localiseCaractereApresIndex(String chaine, int obtientIndexRepetition) {
        for (int i = obtientIndexRepetition + 1; i < chaine.length(); i++) {
            if (Character.isLetterOrDigit(chaine.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtient une lettre majuscule aléatoirement choisie.
     */
    public static char obtientLettreAleatoire() {
        return (char) ('A' + r.nextInt(26));
    }

    /**
     * Obtient un chiffre aléatoirement choisi.
     * @return un chiffre
     */
    public static char obtientChiffreAleatoire() {
        return (char) ('0' + r.nextInt(10));
    }

    /**
     * Retourne un code Fantoire aléatoirement choisi composé de 6 chiffres, d'un caractère majuscule ou un chiffre, de trois chiffres.
     */
    public static String obtientFantoireAleatoire() {
        StringBuilder sb = new StringBuilder();

        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());
        if (r.nextBoolean()) {
            sb.append(obtientChiffreAleatoire());
        } else {
            sb.append(obtientLettreAleatoire());
        }
        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());
        sb.append(obtientChiffreAleatoire());

        return sb.toString();
    }

    /**
     * Supprime les zéros inutiles, sauf pour un code postal (02500 par exemple).
     * @param numero
     * @return la chaine sans zéros inutiles
     */
    public static String supprimeZerosInutilesCodePostal(String numero) {
        if (numero == null) {
            return vide;
        }
        int index = 0;
        while (index < (numero.length() - 1) && numero.charAt(index) == '0') {
            index++;
        }
        if (numero.length() >= 5 && index == numero.length() - 4) {
            return numero.substring(numero.length() - 5);
        } else {
            return numero.substring(index);
        }
    }

    /**
     * Supprime les zéros inutiles, en prenant ou non en compte le code postal.
     * @param numero 
     * @param codepostal true pour prendre en compte les codes postaux (02500) et codes de département, c'est à dire ne pas
     * supprimer leur zéro.
     * @return la chaine sans zéros inutiles
     */
    public static String supprimeZerosInutiles(String numero, boolean codepostal) {
        StringBuilder sb = new StringBuilder();
        StringBuilder nombre = new StringBuilder();
        int length = 1;
        int state = 0;
        char c = 0;
        for (int i = 0; i < numero.length(); i++) {
            c = numero.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case '0':
                            state = 1;
                            break;
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
                            state = 2;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
                // 0...
                case 1:
                    switch (c) {
                        case '0':
                            break;
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            nombre.append(c);
                            length = 2;
                            state = 3;
                            break;
                        default:
                            sb.append('0');
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                // nombre...
                case 2:
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
                            sb.append(c);
                            break;
                        default:
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                // 0chiffre<>0nombre...
                case 3:
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
                            nombre.append(c);
                            length++;
                            break;
                        default:
                            if (codepostal && (length == 5 || length == 2)) {
                                sb.append('0');
                            }
                            sb.append(nombre);
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('0');
                break;
            case 3:
                if (codepostal && (length == 5 || length == 2)) {
                    sb.append('0');
                }
                sb.append(nombre);
                break;
        }

        return sb.toString();
    }

    /**
     * Supprime les zéros inutiles du nombre trouvés dans la chaine.
     * @param numero
     * @return une chaine vide si numero est null.
     */
    public static String supprimeZerosInutiles(String numero) {
        if (numero == null) {
            return vide;
        }
        int index = 0;
        while (index < (numero.length() - 1) && numero.charAt(index) == '0') {
            index++;
        }
        return numero.substring(index);
    }

    /**
     * Concatène en ajoutant un espace entre les portions données.<br>
     * Si l'une des portions est nulle ou vide, elle est ignorée.
     * @return  la concaténation des chaines
     */
    public static String unsplit(String word1, String word2) {
        if (word1 == null || word1.length() == 0) {
            if (word2 == null) {
                return vide;
            }
            return word2;
        } else if (word2 == null || word2.length() == 0) {
            return word1;
        }

        StringBuilder sb = new StringBuilder();
        if (word1.length() != 0) {
            sb.append(word1);
            sb.append(" ");
        }
        sb.append(word2);
        return sb.toString();
    }

    /**
     * Concatène en ajoutant un espace entre les portions données.<br>
     * Si l'une des portions est nulle ou vide, elle est ignorée.
     * @return la concaténation des chaines
     */
    public static String unsplit(String word1, String word2, String word3) {
        if (word1 == null || word1.length() == 0) {
            return unsplit(word2, word3);
        }
        if (word2 == null || word2.length() == 0) {
            return unsplit(word1, word3);
        }
        if (word3 == null || word3.length() == 0) {
            return unsplit(word1, word2);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(word1);
        sb.append(" ");
        sb.append(word2);
        sb.append(" ");
        sb.append(word3);

        return sb.toString();
    }

    /**
     * Supprime les occurences exactes isolées par des espaces du mot trouvé dans la chaine.
     */
    public static String supprimeMot(String chaine, String mot) {
        StringBuilder sb = new StringBuilder();

        int motindex = 0;
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            char cmot;

            switch (state) {
                // début de chaine
                case 0:
                    if (c == mot.charAt(motindex)) {
                        state = 1;
                        motindex++;
                    } else if (c != ' ') {
                        sb.append(c);
                        state = 2;
                    }
                    break;
                case 2: // autre mot
                    if (c == ' ') {
                        state = 3;
                    } else {
                        sb.append(c);
                    }
                    break;
                case 3: // attente mot suivant
                    if (c == mot.charAt(motindex)) {
                        state = 4;
                        motindex++;
                    } else if (c != ' ') {
                        sb.append(' ');
                        sb.append(c);
                        state = 2;
                    }
                    break;
                case 1: // début du mot en début de chaine
                    if (motindex == mot.length()) {
                        motindex = 0;
                        if (c == ' ') {
                            state = 0; // fin du mot
                        } else {
                            sb.append(mot);
                            sb.append(c);
                            state = 2; // autre mot
                        }
                    } else {
                        if (c == mot.charAt(motindex)) {
                            motindex++;
                        } else if (c != ' ') {
                            sb.append(mot.substring(0, motindex));
                            sb.append(c);
                            state = 2; // autre mot
                            motindex = 0;
                        } else {
                            sb.append(mot.substring(0, motindex));
                            sb.append(' ');
                            motindex = 0;
                            state = 0;
                        }
                    }
                    break;
                case 4: // début du mot après un espace
                    if (motindex == mot.length()) {
                        motindex = 0;
                        if (c == ' ') {
                            state = 3; // fin du mot
                        } else {
                            sb.append(' ');
                            sb.append(mot);
                            sb.append(c);
                            state = 2; // autre mot
                        }
                    } else {
                        if (c == mot.charAt(motindex)) {
                            motindex++;
                        } else if (c != ' ') {
                            sb.append(' ');
                            sb.append(mot.substring(0, motindex));
                            sb.append(c);
                            state = 2; // autre mot
                            motindex = 0;
                        } else {
                            sb.append(' ');
                            sb.append(mot.substring(0, motindex));
                            motindex = 0;
                            state = 3;
                        }
                    }
                    break;
            }
        }

        switch (state) {
            case 1:
                if (motindex != mot.length()) {
                    sb.append(mot.substring(0, motindex));
                }
                break;
            case 4:
                if (motindex != mot.length()) {
                    sb.append(' ');
                    sb.append(mot.substring(0, motindex));
                }
                break;
        }

        return sb.toString();
    }

    /**
     * Obtient le mot sans article de moins de quatre lettres.
     * Les articles référencés sont : 
     * <ul>
     *     <li>A</li>
     *     <li>AU</li>
     *     <li>AUX</li>
     *     <li>D</li>
     *     <li>DE</li>
     *     <li>DES</li>
     *     <li>DU</li>
     *     <li>ET</li>
     *     <li>L</li>
     *     <li>LA</li>
     *     <li>LE</li>
     *     <li>LES</li>
     *     <li>OU</li>
     *     <li>PAR</li>
     *     <li>S</li>
     *     <li>SE</li>
     *     <li>SUR</li>
     *     </ul>
     * @param chaine une chaine normalisée.
     * @return la chaine sans articles
     */
    public static String sansarticles(String chaine) {
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                        case '\t':
                            break;
                        default:
                            sb.append(c);
                            state = 8;
                            break;
                        case 'A':
                            state = 1;
                            break;
                        case 'D':
                            state = 2;
                            break;
                        case 'E':
                            state = 3;
                            break;
                        case 'L':
                            state = 4;
                            break;
                        case 'O':
                            state = 5;
                            break;
                        case 'P':
                            state = 6;
                            break;
                        case 'S':
                            state = 7;
                            break;
                    }
                    break;
                case 1: // A
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'U':
                            state = 9;
                            break;
                        default:
                            sb.append('A');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 2: // D
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'E':
                            state = 11;
                            break;
                        case 'U':
                            state = 12;
                            break;
                        default:
                            sb.append('D');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 3: // E
                    switch (c) {
                        case ' ':
                        case '\t':
                            sb.append('E');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'T':
                            state = 14;
                            break;
                        default:
                            sb.append('E');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 4: // L
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'A':
                            state = 15;
                            break;
                        case 'E':
                            state = 16;
                            break;
                        default:
                            sb.append('L');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 5: // O
                    switch (c) {
                        case ' ':
                        case '\t':
                            sb.append('O');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'U':
                            state = 18;
                            break;
                        default:
                            sb.append('O');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 6: // P
                    switch (c) {
                        case ' ':
                        case '\t':
                            sb.append('P');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            state = 19;
                            break;
                        default:
                            sb.append('P');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 7: // S
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'E':
                            state = 23;
                            break;
                        case 'U':
                            state = 21;
                            break;
                        default:
                            sb.append('S');
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 8: // autre mot
                    switch (c) {
                        case ' ':
                        case '\t':
                            sb.append(c);
                            state = 0;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
                case 9: // AU
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'X':
                            state = 10;
                            break;
                        default:
                            sb.append("AU");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 10: // AUX
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("AUX");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 11: // DE
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'S':
                            state = 13;
                            break;
                        default:
                            sb.append("DE");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 12: // DU
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("DU");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 13: // DES
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("DES");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 14: // ET
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("ET");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 15: // LA
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("LA");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 16: // LE
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        case 'S':
                            state = 17;
                            break;
                        default:
                            sb.append("LE");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 17: // LES
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("LES");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 18: // OU
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("OU");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 19: // PA
                    switch (c) {
                        case ' ':
                        case '\t':
                            sb.append("PA");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'R':
                            state = 20;
                            break;
                        default:
                            sb.append("PA");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 20: // PAR
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("PAR");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 21: // SU
                    switch (c) {
                        case ' ':
                        case '\t':
                            sb.append("SU");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'R':
                            state = 22;
                            break;
                        default:
                            sb.append("SU");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 22: // SUR
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("SUR");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
                case 23: // SE
                    switch (c) {
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append("SE");
                            sb.append(c);
                            state = 8;
                            break;
                    }
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * Obtient le premier article de moins de quatre lettres.
     * Les articles référencés sont : 
     * <ul>
     *     <li>D</li>
     *     <li>DE</li>
     *     <li>DES</li>
     *     <li>DU</li>
     *     <li>ET</li>
     *     <li>OU</li>
     *     <li>PAR</li>
     *     <li>S</li>
     *     <li>SE</li>
     *     <li>SUR</li>
     *     </ul>
     * @param chaine une chaine normalisée.
     * @return le premier article
     */
    public static String trouveArticle(String chaine) {
        if (chaine.startsWith("A L ")) {
            return "A L";
        }else if (chaine.startsWith("AUX ")) {
            return "AUX";
        } else if (chaine.startsWith("AU ")) {
            return "AU";
        } else if (chaine.startsWith("A ")) {
            return "A";
        } else if (chaine.startsWith("DES ")) {
            return "DES";
        } else if (chaine.startsWith("DE ")) {
            return "DE";
        } else if (chaine.startsWith("DU ")) {
            return "DU";
        } else if (chaine.startsWith("D ")) {
            return "D";
        } else if (chaine.startsWith("LES ")) {
            return "LES";
        } else if (chaine.startsWith("LA ")) {
            return "LA";
        } else if (chaine.startsWith("LE ")) {
            return "LE";
        } else if (chaine.startsWith("L ")) {
            return "L";
        } else if (chaine.startsWith("SUR ")) {
            return "SUR";
        } else if (chaine.startsWith("SE ")) {
            return "SE";
        } else if (chaine.startsWith("S ")) {
            return "S";
        } else if (chaine.startsWith("PAR ")) {
            return "PAR";
        } else if (chaine.startsWith("ET ")) {
            return "ET";
        } else if (chaine.startsWith("OU ")) {
            return "OU";
        } else {
            return "";
        }
    }
    
     /**
     * Obtient l'article de liaison entre deux mots.
     * Les articles référencés sont : 
     * <ul>
     *     <li>A</li>
     *     <li>AU</li>
     *     <li>AUX</li>
     *     <li>D</li>
     *     <li>DE</li>
     *     <li>DES</li>
     *     <li>DU</li>
     *     <li>ET</li>
     *     <li>L</li>
     *     <li>LA</li>
     *     <li>LE</li>
     *     <li>LES</li>
     *     <li>OU</li>
     *     <li>PAR</li>
     *     <li>S</li>
     *     <li>SE</li>
     *     <li>SUR</li>
     *     </ul>
     * @param chaine une chaine normalisée.
     * @return l'article de liaison
     */
     public static String trouveArticleDeLiaison(String chaine) {
        if (chaine.startsWith("DES ")) {
            return "DES";
        } else if (chaine.startsWith("DE ")) {
            return "DE";
        } else if (chaine.startsWith("DU ")) {
            return "DU";
        } else if (chaine.startsWith("D ")) {
            return "D";
        } if (chaine.startsWith("SUR ")) {
            return "SUR";
        } else if (chaine.startsWith("SE ")) {
            return "SE";
        } else if (chaine.startsWith("S ")) {
            return "S";
        } else if (chaine.startsWith("PAR ")) {
            return "PAR";
        } else if (chaine.startsWith("ET ")) {
            return "ET";
        } else if (chaine.startsWith("OU ")) {
            return "OU";
        } else {
            return "";
        }
    }

    /**
     * Obtient le dernier mot de la chaine.<br>
     * Le dernier mot est identifié par l'espace qui le précède.<br>
     * @param chaine chaine normalisée
     * @return une chaine vide si chaine est null.
     */
    public static String derniermot(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int startindex, state, endindex, i;

        state = 0;
        startindex = endindex = -1;

        for (int index = 0; index < chaine.length(); index++) {
            char c = chaine.charAt(index);
            switch (state) {
                case 0: // départ

                    switch (c) {
                        case ' ':
                            state = 1;
                            break;
                        default:
                            sb.append(c);
                            startindex = endindex = 0;
                            state = 2;
                            break;
                    }
                    break;
                case 1: // suite d'espaces

                    switch (c) {
                        case ' ':
                            break;
                        default:
                            sb.setLength(0);
                            sb.append(c);
                            startindex = endindex = index;
                            state = 2;
                            break;
                    }
                    break;
                case 2: // suite de lettres

                    switch (c) {
                        case ' ':
                            endindex = index;
                            state = 1;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Supprime les termes entre parenthèses, entre crochets, entre accolades.
     * @return une chaine vide si chaine est null.
     */
    public static String supprimeEntreParenthese(String ligne) {
        if (ligne == null) {
            return vide;
        }
        int state = 0;
        // etat 0 = normal
        // etat 1 = entre parenthèses
        // etat 2 = entre crochets
        // etat 2 = entre accolades
        int index = 0;
        StringBuilder sb = new StringBuilder();
        while (index < ligne.length()) {
            char c = ligne.charAt(index);
            switch (state) {
                case 0:
                    switch (c) {
                        case '(':
                            state = 1;
                            break;
                        case '[':
                            state = 2;
                            break;
                        case '{':
                            state = 3;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
                case 1:
                    if (c == ')') {
                        state = 0;
                    }
                    break;
                case 2:
                    if (c == ']') {
                        state = 0;
                    }
                case 3:
                    if (c == '}') {
                        state = 0;
                    }
            }
            index++;
        }
        return sb.toString();
    }

    /**
     * Insère des espaces entre les groupes de caractères et les groupes de nombre.
     * Les caractères d'espacement reconnus sont:
     * <ul><li>l'espace</li>
     *     <li>la tabulation</li></ul>
     */
    public static String separeEspacesEtNombres(String ligne) {
        if (ligne == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;
        // 0 = groupe d'espaces
        // 1 = groupe de caractère
        // 2 = groupe de nombre
        int index = 0;
        while (index < ligne.length()) {
            char c = ligne.charAt(index);
            switch (state) {
                case 0: // les espaces
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
                            state = 2;
                            break;
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1: // les caractères
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
                            sb.append(' ');
                            state = 2;
                            break;
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
                case 2: // les chiffres
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
                            break;
                        case ' ':
                        case '\t':
                            state = 0;
                            break;
                        default:
                            sb.append(' ');
                            state = 1;
                            break;
                    }
                    break;
            }
            sb.append(c);
            index++;
        }
        return sb.toString();
    }

    /**
     * Retourne le résultat de la première passe de normalisation à savoir:
     * <ul><li>Mise en majuscule</li>
     *     <li>Suppression des termes entre parenthèses ou entre crochets</li>
     *     <li>Suppression de la ponctuation</li>
     *     <li>Ajoute un espace entre les nombres et les lettres</li>
     *     <li>Suppression des accents</li>
     *     <li>Supprime les zéros inutiles pour les nombres qui ne sont pas des codes de département
     * ou des codes postaux potentiels</li>
     *     <li>Suppression des espaces inutiles</li></ul>
     * Si ligne est null, retourne une chaine vide.
     */
    public static String normalise_1(String ligne) {
        if (ligne == null || ligne.length() == 0) {
            return vide;
        }
        ligne = supprimeEntreParenthese(ligne);
        ligne = separeEspacesEtNombres(ligne);
        ligne = supprimeZerosInutiles(ligne, true);
        return supprimeAccentRepetitionEspacePonctuation(ligne);
    }

    /**
     * Retourne le résultat de la première passe de normalisation à savoir:
     * <ul><li>Mise en majuscule</li>
     *     <li>Suppression des termes entre parenthèses ou entre crochets</li>
     *     <li>Suppression de la ponctuation (avec ou sans les caractères pourcent et underscore)</li>
     *     <li>Ajoute un espace entre les nombres et les lettres</li>
     *     <li>Supprime les zéros inutiles pour les nombres qui ne sont pas des codes de département
     * ou des codes postaux potentiels</li>
     *     <li>Suppression des espaces inutiles</li></ul>
     * @param supprimepourcent permet de spécifier si les caractères pourcentage et underscore sont supprimés ou non.
     */
    public static String normalise_1(String ligne, boolean supprimepourcent) {
        if (ligne == null || ligne.length() == 0) {
            return vide;
        }
        ligne = supprimeEntreParenthese(ligne);
        ligne = separeEspacesEtNombres(ligne);
        ligne = supprimeZerosInutiles(ligne, true);
        if (supprimepourcent) {
            return supprimeAccentRepetitionEspacePonctuation(ligne);
        } else {
            return supprimeAccentRepetitionEspacePonctuationSaufPourcent(ligne);
        }
    }

    /**
     * Retourne le résultat de la première passe de normalisation à savoir:
     * <ul>
     *     <li>Mise en majuscule</li>
     *     <li>Suppression des termes entre parenthèses ou entre crochets</li>
     *     <li>Suppression de la ponctuation</li>
     *     <li>Suppression des accents</li>
     *     <li>Suppression des espaces inutiles</li>
     * </ul>
     */
    public static String[] normalise_1(String[] lignes) {
        String[] res = new String[lignes.length];
        for (int i = 0; i < lignes.length; i++) {
            res[i] = normalise_1(lignes[i]);
        }
        return res;
    }

    /**
     * Retourne vrai si la chaine commence par le mot spécifié.<br>
     * Un espace ou la fin de la chaine doit suivre le mot.<br>
     * La méthode Character.isSpaceChar est utilisée.
     */
    public static boolean commencePar(String chaine, String mot) {
        if (chaine == null) {
            return false;
        }
        if (chaine.startsWith(mot)) {
            if (chaine.length() > mot.length() && !Character.isSpaceChar(chaine.charAt(mot.length()))) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Retourne vrai si la chaine contient le mot spécifié.<br>
     * La casse est respectée.<br>
     * Le mot trouvé est tel qu'il soit entouré d'espaces ou en fin ou en début de chaine.<br>
     * La méthode Character.isSpaceChar est utilisée.
     * @param chaine chaine normalisée.
     * @param mot le mot cherché normalisé.
     */
    public static boolean contientMot(String chaine, String mot) {
        if (chaine == null) {
            return false;
        }
        int index = -1;
        while ((index = chaine.indexOf(mot, index + 1)) >= 0) {
            if (index > 0 && !Character.isSpaceChar(chaine.charAt(index - 1))) {
                continue;
            }
            if (index + mot.length() < chaine.length() && !Character.isSpaceChar(chaine.charAt(index + mot.length()))) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Retourne la position du mot spécifié dans la chaine.<br>
     * La casse est respectée.<br>
     * Le mot trouvé est tel qu'il soit entouré d'espaces ou en fin ou en début de chaine.<br>
     * La méthode Character.isSpaceChar est utilisée.
     * @param chaine chaine normalisée : pas de ponctuation.
     * @param mot le mot cherché.
     * @param index l'index auquel commencer la recherche.
     * @return -1 si le mot n'est pas trouvé, même si chaine est null.
     */
    public static int localiseMot(String chaine, String mot, int index) {
        if (chaine == null) {
            return -1;
        }
        index--;
        while ((index = chaine.indexOf(mot, index + 1)) >= 0) {
            if (index > 0 && !Character.isSpaceChar(chaine.charAt(index - 1))) {
                continue;
            }
            if (index + mot.length() < chaine.length() && !Character.isSpaceChar(chaine.charAt(index + mot.length()))) {
                continue;
            }

            return index;
        }

        return -1;
    }

    /**
     * Formate une chaine pour être utilisée dans une chaine SQL.<br>
     * Les modifications apportées sont les suivantes:
     * <ul><li>Les apostrophes sont doublées</li></ul>
     * @return une chaine vide si chaine est null.
     */
    public static String formatSQL(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            sb.append(c);
            if (c == '\'') {
                sb.append('\'');
            }
        }
        return sb.toString();
    }

    /**
     * Convertit un entier en chaine sans générer d'exception.
     * @return l'entier ou 0 si une exception de format se produit ou si entier est null.
     */
    public static int parseInt(String entier) {
        if (entier == null) {
            return 0;
        }
        try {
            return Integer.parseInt(entier);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Supprime la ponctuation dans la chaine spécifiée.<br>
     * La ponctuation est remplacée par des espaces.
     * @return une chaine vide si chaine est null.
     */
    public static String supprimePonctuation(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    /**
     * Supprime les accents, la ponctuation, la répétition des espaces, et met en majuscule<br>
     * Seuls les caractères % et _ sont éparnés.<br>
     * Les espaces au début et à la fin sont aussi supprimés.<br>
     */
    public static String supprimeAccentRepetitionEspacePonctuationSaufPourcent(String s) {
        if (s == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length() && Character.isSpaceChar(s.charAt(i))) {
            i++;
        }

        boolean lastisspace = false;
        for (; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isLetter(c) || Character.isDigit(c) || c == '%' || c == '_') {
                c = supprimeAccent(c);
                sb.append(Character.toUpperCase(c));
                lastisspace = false;
            } else if (c == '/') {
                break;
            } else {
                if (!lastisspace) {
                    lastisspace = true;
                    sb.append(' ');
                }
            }
        }

        if (lastisspace) // Supprime les espaces finaux.
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * Supprime les accents, la ponctuation, la répétition des espaces, et met en majuscule.<br>
     * Les espaces au début et à la fin sont aussi supprimés.<br>
     */
    public static String supprimeAccentRepetitionEspacePonctuation(String s) {
        if (s == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length() && Character.isSpaceChar(s.charAt(i))) {
            i++;
        }

        boolean lastisspace = false;
        for (; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isLetter(c) || Character.isDigit(c)) {
                c = supprimeAccent(c);
                sb.append(Character.toUpperCase(c));
                lastisspace = false;
            } else {
                if (!lastisspace) {
                    lastisspace = true;
                    sb.append(' ');
                }
            }
        }

        if (lastisspace) // Supprime les espaces finaux.
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * Supprime l'accent sur la lettre spécifiée.<br>
     * Les tabulations sont aussi changées en espace.
     */
    public static char supprimeAccent(char c) {
        boolean isuppercase = false;

        if (Character.isUpperCase(c)) {
            c = Character.toLowerCase(c);
            isuppercase = true;
        }

        switch (c) {
            case '\t':
            case ' ':
                c = ' ';
                break;
            case '\n':
                c = '\n';
                break;
            case '\r':
                c = '\r';
                break;

            case 'ǽ':
                c = 'æ';
                break;

            case 'ā':
            case 'ǎ':
            case 'ą':
            case 'ă':
            case 'á':
            case 'ǻ':
            case 'à':
            case 'ẩ':
            case 'ắ':
            case 'ẵ':
            case 'ặ':
            case 'ậ':
            case 'ẳ':
            case 'ằ':
            case 'å':
            case 'ầ':
            case 'ẫ':
            case 'ấ':
            case 'ả':
            case 'ạ':
            case 'ä':
            case 'â':
            case 'ã':
                c = 'a';
                break;

            case 'ć':
            case 'ĉ':
            case 'ċ':
            case 'č':
            case 'ç':
                c = 'c';
                break;

            case 'đ':
            case 'ď':
                c = 'd';
                break;

            case 'ě':
            case 'ę':
            case 'ė':
            case 'ĕ':
            case 'ē':
            case 'ể':
            case 'ế':
            case 'é':
            case 'è':
            case 'ệ':
            case 'ễ':
            case 'ề':
            case 'ẹ':
            case 'ẽ':
            case 'ẻ':
            case 'ë':
            case 'ê':
                c = 'e';
                break;

            case 'ƒ':
                c = 'f';
                break;

            case 'ģ':
            case 'ġ':
            case 'ğ':
            case 'ĝ':
                c = 'g';
                break;

            case 'ħ':
            case 'ĥ':
                c = 'h';
                break;

            case 'ı':
            case 'į':
            case 'ỉ':
            case 'ĭ':
            case 'ī':
            case 'ị':
            case 'ĩ':
            case 'í':
            case 'ì':
            case 'ǐ':
            case 'ï':
            case 'î':
                c = 'i';
                break;

            case 'ĵ':
                c = 'j';
                break;

            case 'ķ':
                c = 'k';
                break;

            case 'ņ':
            case 'ŉ':
            case 'ŋ':
            case 'ń':
                c = 'n';
                break;

            case 'ö':
            case 'ó':
            case 'ơ':
            case 'ő':
            case 'ŏ':
            case 'ồ':
            case 'ỗ':
            case 'ỡ':
            case 'ợ':
            case 'ở':
            case 'ớ':
            case 'ỏ':
            case 'ờ':
            case 'ộ':
            case 'ổ':
            case 'ố':
            case 'ō':
            case 'ọ':
            case 'ò':
            case 'ǒ':
            case 'ô':
            case 'õ':
                c = 'o';
                break;

            case 'ñ':
                c = 'n';
                break;

            case 'ŗ':
            case 'ř':
            case 'ŕ':
                c = 'r';
                break;

            case 'ŝ':
            case 'š':
            case 'ş':
            case 'ś':
                c = 's';
                break;

            case 'ť':
            case 'ŧ':
            case 'ţ':
                c = 't';
                break;

            case 'ù':
            case 'ụ':
            case 'ũ':
            case 'ů':
            case 'ự':
            case 'ữ':
            case 'ừ':
            case 'ử':
            case 'ứ':
            case 'ủ':
            case 'ų':
            case 'ű':
            case 'ư':
            case 'ŭ':
            case 'ū':
            case 'ǔ':
            case 'ǜ':
            case 'ǖ':
            case 'ú':
            case 'ǚ':
            case 'ǘ':
            case 'ü':
            case 'û':
                c = 'u';
                break;

            case 'ŵ':
                c = 'w';
                break;

            case 'ý':
            case 'ŷ':
            case 'ỷ':
            case 'ỳ':
            case 'ỹ':
            case 'ÿ':
                c = 'y';
                break;

            case 'ż':
            case 'ž':
            case 'ź':
                c = 'z';
                break;
        }

        if (isuppercase) {
            c = Character.toUpperCase(c);
        }

        return c;
    }

    /**
     * Remplace les caractères accentués par des caractères non accentués.
     */
    public static String supprimeAccents(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);

            sb.append(supprimeAccent(c));
        }
        return sb.toString();
    }

    /**
     * Supprime les répétitions d'espaces.<br>
     * Les espaces au départ et à la fin sont aussi supprimés.
     * @return une chaine où chaque espace n'apparait qu'une fois à la suite.
     */
    public static String supprimeRepetitionEspace(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();

        int state = 0;
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    if (c != ' ') {
                        sb.append(c);
                        state = 1;
                    }
                    break;
                case 1:
                    if (c == ' ') {
                        state = 2;
                    } else {
                        sb.append(c);
                    }
                    break;
                case 2:
                    if (c != ' ') {
                        sb.append(' ');
                        sb.append(c);
                        state = 1;
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Supprime tous les espaces dans la chaine spécifiée.
     */
    public static String supprimeEspaces(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            if (!Character.isSpaceChar(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Isole la répétition du numéro d'adresse d'une borne.<br>
     * Si la 'répétition' trouvée est composée de plusieurs caractères, seul le premier caractère est conservé.<br>
     * Si ce premier caractère n'est pas une lettre, il est ignoré.<br>
     * Si aucun numéro n'est trouvé, la répétition est ignorée.<br>
     * retourne des chaines vide si borne est null.
     * @param borne une borne de la forme NUMEROREPETITION ou REPETITION est composé d'un caractère.
     * @return un tableau de deux chaines qui représentent le numéro et la répétition dans cet ordre ou des chaines vides s'ils sont ignorés.
     */
    public static String[] isoleRepetition(String borne) {
        if (borne != null && borne.length() > 0) {
            StringBuilder sbNumero = new StringBuilder();
            String repetition = vide;
            char c = 0;
            int i = 0;
            boolean stop = false;

            borne = supprimeEspaces(borne);

            while (!stop && i < borne.length()) {
                c = borne.charAt(i++);

                if (c >= '0' && c <= '9') {
                    sbNumero.append(c);
                } else {
                    stop = true;
                }
            }

            if (stop && sbNumero.length() > 0) {
                if (Character.isLetter(c)) {
                    repetition = Character.toString(c);
                }
            }

            return new String[]{sbNumero.toString(), repetition};
        }
        return new String[]{vide, vide};
    }

    /**
     * Retourne la liste ininterrompue de nombres séparés par des espaces trouvés au début de la chaine spécifiée.<br>
     * Les caractères d'espacement reconnus sont :
     * <ul><li>L'espace</li>
     *     <li>la tabulation</li></ul>
     */
    public static ArrayList<Integer> trouvePremiersNumeros(String chaine) {
        ArrayList<Integer> nombres = new ArrayList<Integer>();
        if (chaine == null) {
            return nombres;
        }
        int nombre = 0;
        int state = 0;
        // 0 = espaces
        // 1 = chiffres
        // 2 = fin
        int index = 0;
        while (state != 2 && index < chaine.length()) {
            char c = chaine.charAt(index);
            switch (state) {
                case 0:
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
                            state = 1;
                            nombre = (c - '0');
                            break;
                        case ' ':
                        case '\t':
                            break;
                        default:
                            state = 2;
                            break;
                    }
                    break;
                case 1:
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
                            nombre = 10 * nombre + (c - '0');
                            break;
                        case ' ':
                        case '\t':
                            nombres.add(new Integer(nombre));
                            state = 0;
                            nombre = 0;
                            break;
                        default:
                            nombres.add(new Integer(nombre));
                            state = 2;
                    }
                    break;
            }
            index++;
        }

        switch (state) {
            case 1:
                nombres.add(new Integer(nombre));
                break;
        }

        return nombres;
    }

    /**
     * Enlève les h qui ne sont pas précédé de p, h, ou s.
     * @param chaine
     * @return la chaine traitée
     */
    public static String enleveH(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'H':
                            break;
                        case 'P':
                        case 'C':
                        case 'S':
                            sb.append(c);
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    sb.append(c);
                    state = 0;
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * Remplace les ga suivis de n, m, in, ou im par des ka.
     * @param chaine
     * @return la chaine traitée
     */
    public static String remplaceGAKA(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case 'G':
                            state = 1;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case 'A':
                            state = 2;
                            break;
                        case 'G':
                            sb.append('G');
                            state = 1;
                            break;
                        default:
                            sb.append('G');
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        default:
                            sb.append("GA");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'G':
                            sb.append("GA");
                            state = 1;
                            break;
                        case 'N':
                            sb.append("KAN");
                            state = 0;
                            break;
                        case 'M':
                            sb.append("KAM");
                            state = 0;
                            break;
                        case 'I':
                            state = 3;
                            break;
                    }
                    break;
                case 3:
                    switch (c) {
                        default:
                            sb.append("GAI");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'G':
                            sb.append("GAI");
                            state = 1;
                            break;
                        case 'M':
                            sb.append("KAIM");
                            state = 0;
                            break;
                        case 'N':
                            sb.append("KAIN");
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('G');
                break;
            case 2:
                sb.append("GA");
                break;
            case 3:
                sb.append("GAI");
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace AIN, EIN, AIM, EIM par y s'ils sont suivi de a e i o ou u.
     * @param chaine
     * @return la chaine traitée
     */
    public static String remplaceAIN(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'A':
                            state = 1;
                            break;
                        case 'E':
                            state = 2;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        default:
                            sb.append('A');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('A');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('A');
                            state = 2;
                            break;
                        case 'I':
                            state = 3;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        default:
                            sb.append('E');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('E');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('E');
                            state = 2;
                            break;
                        case 'I':
                            state = 6;
                            break;
                    }
                    break;
                case 3:
                    switch (c) {
                        default:
                            sb.append("AI");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("AI");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("AI");
                            state = 2;
                            break;
                        case 'N':
                            state = 4;
                            break;
                        case 'M':
                            state = 5;
                            break;
                    }
                    break;
                case 4:
                    switch (c) {
                        default:
                            sb.append("AIN");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('Y');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('Y');
                            state = 2;
                            break;
                        case 'I':
                            sb.append("YI");
                            state = 0;
                            break;
                        case 'O':
                            sb.append("YO");
                            state = 0;
                            break;
                        case 'U':
                            sb.append("YU");
                            state = 0;
                            break;
                    }
                    break;
                case 5:
                    switch (c) {
                        default:
                            sb.append("AIM");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('Y');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('Y');
                            state = 2;
                            break;
                        case 'I':
                            sb.append("YI");
                            state = 0;
                            break;
                        case 'O':
                            sb.append("YO");
                            state = 0;
                            break;
                        case 'U':
                            sb.append("YU");
                            state = 0;
                            break;
                    }
                    break;
                case 6:
                    switch (c) {
                        default:
                            sb.append("EI");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("EI");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("EI");
                            state = 2;
                            break;
                        case 'N':
                            state = 7;
                            break;
                        case 'M':
                            state = 8;
                            break;
                    }
                    break;
                case 7:
                    switch (c) {
                        default:
                            sb.append("EIN");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('Y');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('Y');
                            state = 2;
                            break;
                        case 'I':
                            sb.append("YI");
                            state = 0;
                            break;
                        case 'O':
                            sb.append("YO");
                            state = 0;
                            break;
                        case 'U':
                            sb.append("YU");
                            state = 0;
                            break;
                    }
                    break;
                case 8:
                    switch (c) {
                        default:
                            sb.append("EIM");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('Y');
                            state = 1;
                            break;
                        case 'E':
                            sb.append("Y");
                            state = 2;
                            break;
                        case 'I':
                            sb.append("YI");
                            state = 0;
                            break;
                        case 'O':
                            sb.append("YO");
                            state = 0;
                            break;
                        case 'U':
                            sb.append("YU");
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('A');
                break;
            case 2:
                sb.append('E');
                break;
            case 3:
                sb.append("AI");
                break;
            case 4:
                sb.append("AIN");
                break;
            case 5:
                sb.append("AIM");
                break;
            case 6:
                sb.append("EI");
                break;
            case 7:
                sb.append("EIN");
                break;
            case 8:
                sb.append("EIM");
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace EAU par O, OUA par 2, EIN, AIN, EIM, AIM par 4.
     * @param chaine
     * @return la chaine traitée
     */
    public static String remplaceEAU(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'E':
                            state = 1;
                            break;
                        case 'A':
                            state = 2;
                            break;
                        case 'O':
                            state = 3;
                            break;
                    }
                    break;
                case 1: // E...
                    switch (c) {
                        default:
                            sb.append('E');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append('E');
                            state = 1;
                            break;
                        case 'O':
                            sb.append('E');
                            state = 3;
                            break;
                        case 'A':
                            state = 4;
                            break;
                        case 'I':
                            state = 5;
                            break;
                    }
                    break;
                case 2: // A...
                    switch (c) {
                        default:
                            sb.append('A');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append('A');
                            state = 1;
                            break;
                        case 'A':
                            sb.append('A');
                            state = 2;
                            break;
                        case 'O':
                            sb.append('A');
                            state = 3;
                            break;
                        case 'I':
                            state = 6;
                            break;
                    }
                    break;
                case 3: // O...
                    switch (c) {
                        default:
                            sb.append('O');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append('O');
                            state = 1;
                            break;
                        case 'A':
                            sb.append('O');
                            state = 2;
                            break;
                        case 'O':
                            sb.append('O');
                            state = 3;
                            break;
                        case 'U':
                            state = 7;
                            break;
                    }
                    break;
                case 4: // EA...
                    switch (c) {
                        default:
                            sb.append("EA");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append("EA");
                            state = 1;
                            break;
                        case 'A':
                            sb.append("EA");
                            state = 2;
                            break;
                        case 'O':
                            sb.append("EA");
                            state = 3;
                            break;
                        case 'U':
                            sb.append('O');
                            state = 0;
                            break;
                    }
                    break;
                case 5: // EI...
                    switch (c) {
                        default:
                            sb.append("EI");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append("EI");
                            state = 1;
                            break;
                        case 'A':
                            sb.append("EI");
                            state = 2;
                            break;
                        case 'O':
                            sb.append("EI");
                            state = 3;
                            break;
                        case 'N':
                            sb.append('4');
                            state = 0;
                            break;
                        case 'M':
                            sb.append('4');
                            state = 0;
                            break;
                    }
                    break;
                case 6: // AI...
                    switch (c) {
                        default:
                            sb.append("AI");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append("AI");
                            state = 1;
                            break;
                        case 'O':
                            sb.append("AI");
                            state = 3;
                            break;
                        case 'A':
                            sb.append("AI");
                            state = 2;
                            break;
                        case 'N':
                            sb.append('4');
                            state = 0;
                            break;
                        case 'M':
                            sb.append('4');
                            state = 0;
                            break;
                    }
                    break;
                case 7: // OU
                    switch (c) {
                        default:
                            sb.append("OU");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append("OU");
                            state = 1;
                            break;
                        case 'O':
                            sb.append("OU");
                            state = 3;
                            break;
                        case 'A':
                            sb.append('2');
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('E');
                break;
            case 2:
                sb.append('A');
                break;
            case 3:
                sb.append('O');
                break;
            case 4:
                sb.append("EA");
                break;
            case 5:
                sb.append("EI");
                break;
            case 6:
                sb.append("AI");
                break;
            case 7:
                sb.append("OU");
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace AI, EI par Y, ER par YR, ESS par YSS, ET par YT.
     * @param chaine
     * @return la chaine traitée
     */
    public static String remplaceAI(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'E':
                            state = 1;
                            break;
                        case 'A':
                            state = 2;
                            break;
                    }
                    break;
                case 1: // E...
                    switch (c) {
                        default:
                            sb.append('E');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append('E');
                            state = 1;
                            break;
                        case 'A':
                            sb.append('E');
                            state = 2;
                            break;
                        case 'I':
                            sb.append('Y');
                            state = 0;
                            break;
                        case 'R':
                            sb.append("YR");
                            state = 0;
                            break;
                        case 'S':
                            state = 3;
                            break;
                        case 'T':
                            sb.append("YT");
                            state = 0;
                            break;
                    }
                    break;
                case 2: // A...
                    switch (c) {
                        default:
                            sb.append('A');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append('A');
                            state = 1;
                            break;
                        case 'A':
                            sb.append('A');
                            state = 2;
                            break;
                        case 'I':
                            sb.append('Y');
                            state = 0;
                            break;
                    }
                    break;
                case 3: // ES...
                    switch (c) {
                        default:
                            sb.append("ES");
                            sb.append(c);
                            state = 0;
                            break;
                        case 'E':
                            sb.append("ES");
                            state = 1;
                            break;
                        case 'A':
                            sb.append("ES");
                            state = 2;
                            break;
                        case 'S':
                            sb.append("YSS");
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('E');
                break;
            case 2:
                sb.append('A');
                break;
            case 3:
                sb.append("ES");
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace AN, AM, EN, EM par 1, et IN par 4 lorsqu'il ne sont pas suivi de A E I O U 1 2 3 4
     * @param chaine
     * @return la chaine traitée
     */
    public static String remplaceAN(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'A':
                            state = 1;
                            break;
                        case 'E':
                            state = 2;
                            break;
                        case 'I':
                            state = 3;
                            break;
                    }
                    break;
                case 1: // A...
                    switch (c) {
                        default:
                            sb.append('A');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('A');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('A');
                            state = 2;
                            break;
                        case 'I':
                            sb.append('A');
                            state = 3;
                            break;
                        case 'N':
                            state = 4;
                            break;
                        case 'M':
                            state = 5;
                            break;
                    }
                    break;
                case 2: // E...
                    switch (c) {
                        default:
                            sb.append('E');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('E');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('E');
                            state = 2;
                            break;
                        case 'I':
                            sb.append('E');
                            state = 3;
                            break;
                        case 'N':
                            state = 6;
                            break;
                        case 'M':
                            state = 7;
                            break;
                    }
                    break;
                case 3: // I...
                    switch (c) {
                        default:
                            sb.append('I');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('I');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('I');
                            state = 2;
                            break;
                        case 'I':
                            sb.append('I');
                            state = 3;
                            break;
                        case 'N':
                            state = 8;
                            break;
                    }
                    break;
                case 4: // AN...
                    switch (c) {
                        default:
                            sb.append('1');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("AN");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("AN");
                            state = 2;
                            break;
                        case 'I':
                            sb.append("AN");
                            state = 3;
                            break;
                        case 'O':
                        case 'U':
                        case 'Y':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append("AN");
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                case 5: // AM...
                    switch (c) {
                        default:
                            sb.append('1');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("AM");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("AM");
                            state = 2;
                            break;
                        case 'I':
                            sb.append("AM");
                            state = 3;
                            break;
                        case 'O':
                        case 'U':
                        case 'Y':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append("AM");
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                case 6: // EN...
                    switch (c) {
                        default:
                            sb.append('1');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("EN");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("EN");
                            state = 2;
                            break;
                        case 'I':
                            sb.append("EN");
                            state = 3;
                            break;
                        case 'O':
                        case 'U':
                        case 'Y':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append("EN");
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                case 7: // EM...
                    switch (c) {
                        default:
                            sb.append('1');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("EM");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("EM");
                            state = 2;
                            break;
                        case 'I':
                            sb.append("EM");
                            state = 3;
                            break;
                        case 'O':
                        case 'U':
                        case 'Y':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append("EM");
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
                case 8: // IN...
                    switch (c) {
                        default:
                            sb.append('4');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append("IN");
                            state = 1;
                            break;
                        case 'E':
                            sb.append("IN");
                            state = 2;
                            break;
                        case 'I':
                            sb.append("IN");
                            state = 3;
                            break;
                        case 'O':
                        case 'U':
                        case 'Y':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append("IN");
                            sb.append(c);
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('A');
                break;
            case 2:
                sb.append('E');
                break;
            case 3:
                sb.append('I');
                break;
            case 4:
                sb.append('1');
                break;
            case 5:
                sb.append('1');
                break;
            case 6:
                sb.append('1');
                break;
            case 7:
                sb.append('1');
                break;
            case 8:
                sb.append('4');
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace le s par un z s'il est précédé et suivi d'un a e i o u 1 2 3 4
     */
    public static String remplaceS(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'A':
                        case 'E':
                        case 'I':
                        case 'O':
                        case 'U':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append(c);
                            state = 1;
                            break;
                    }
                    break;
                case 1: // après A E I O U 1 2 3 ou 4
                    switch (c) {
                        default:
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                        case 'E':
                        case 'I':
                        case 'O':
                        case 'U':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append(c);
                            state = 1;
                            break;
                        case 'S':
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        default:
                            sb.append('S');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                        case 'E':
                        case 'I':
                        case 'O':
                        case 'U':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            sb.append('Z');
                            sb.append(c);
                            state = 1;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 2:
                sb.append('S');
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace OE et EU par E, AU par O, OI et OY par 2, OU par 3
     */
    public static String remplaceOE(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'A':
                            state = 1;
                            break;
                        case 'E':
                            state = 2;
                            break;
                        case 'O':
                            state = 3;
                            break;
                    }
                    break;
                case 1: // A...
                    switch (c) {
                        default:
                            sb.append('A');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('A');
                            break;
                        case 'E':
                            sb.append('A');
                            state = 2;
                            break;
                        case 'O':
                            sb.append('A');
                            state = 3;
                            break;
                        case 'U':
                            sb.append("O");
                            state = 0;
                            break;
                    }
                    break;
                case 2: // E...
                    switch (c) {
                        default:
                            sb.append('E');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('E');
                            state = 1;
                            break;
                        case 'E':
                            sb.append('E');
                            state = 2;
                            break;
                        case 'O':
                            sb.append('E');
                            state = 3;
                            break;
                        case 'U':
                            sb.append("E");
                            state = 0;
                            break;
                    }
                    break;
                case 3: // O...
                    switch (c) {
                        default:
                            sb.append('O');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'A':
                            sb.append('O');
                            state = 1;
                            break;
                        case 'O':
                            sb.append('O');
                            state = 3;
                            break;
                        case 'E':
                            sb.append("E");
                            state = 0;
                            break;
                        case 'I':
                            sb.append("2");
                            state = 0;
                            break;
                        case 'Y':
                            sb.append("2");
                            state = 0;
                            break;
                        case 'U':
                            sb.append("3");
                            state = 0;
                            break;
                    }
            }
        }
        switch (state) {
            case 1:
                sb.append('A');
                break;
            case 2:
                sb.append('E');
                break;
            case 3:
                sb.append('O');
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace CH, SCH, SH, SS, SC par 5.
     */
    public static String remplaceCH(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'C':
                            state = 1;
                            break;
                        case 'S':
                            state = 2;
                            break;
                    }
                    break;
                case 1: // C...
                    switch (c) {
                        default:
                            sb.append('C');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'C':
                            sb.append('C');
                            break;
                        case 'S':
                            sb.append('C');
                            state = 2;
                            break;
                        case 'H':
                            sb.append('5');
                            state = 0;
                            break;
                    }
                    break;
                case 2: // S...
                    switch (c) {
                        default:
                            sb.append('S');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'H':
                            sb.append('5');
                            state = 0;
                            break;
                        case 'S':
                            sb.append('5');
                            state = 0;
                            break;
                        case 'C':
                            state = 3;
                            break;
                    }
                    break;
                case 3: // SC...
                    switch (c) {
                        default:
                            sb.append('5');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'C':
                            sb.append('5');
                            state = 1;
                            break;
                        case 'S':
                            sb.append('5');
                            state = 2;
                            break;
                        case 'H':
                            sb.append('5');
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('C');
                break;
            case 2:
                sb.append('S');
                break;
            case 3:
                sb.append('5');
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace C par S s'il est suivi de E ou I.
     */
    public static String remplaceC(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'C':
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        default:
                            sb.append('C');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'C':
                            sb.append('C');
                            break;
                        case 'E':
                            sb.append("SE");
                            state = 0;
                            break;
                        case 'I':
                            sb.append("SI");
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('C');
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace C, Q, QU, GU par K, GA par KA, GO par KO et GY par KY.
     */
    public static String remplaceK(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        int state = 0;

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        default:
                            sb.append(c);
                            break;
                        case 'C':
                            sb.append('K');
                            break;
                        case 'Q':
                            state = 1;
                            break;
                        case 'G':
                            state = 2;
                            break;
                    }
                    break;
                case 1: // Q...
                    switch (c) {
                        default:
                            sb.append('K');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'Q':
                            sb.append('K');
                            state = 1;
                            break;
                        case 'G':
                            sb.append('K');
                            state = 2;
                            break;
                        case 'U':
                            sb.append("K");
                            state = 0;
                            break;
                    }
                    break;
                case 2: // G...
                    switch (c) {
                        default:
                            sb.append('G');
                            sb.append(c);
                            state = 0;
                            break;
                        case 'Q':
                            sb.append('G');
                            state = 1;
                            break;
                        case 'G':
                            sb.append('G');
                            state = 2;
                            break;
                        case 'U':
                            sb.append('K');
                            state = 0;
                            break;
                        case 'A':
                            sb.append("KA");
                            state = 0;
                            break;
                        case 'O':
                            sb.append("KO");
                            state = 0;
                            break;
                        case 'Y':
                            sb.append("KY");
                            state = 0;
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                sb.append('K');
                break;
            case 2:
                sb.append('G');
                break;
        }

        return sb.toString();
    }

    /**
     * Remplace J par G, M par N.<br>
     * L'algorithme originale effectuait les modifications suivantes:<br>
     * Remplace A par O, D et P par T, J par G, B et V par F, M par N.
     */
    public static String remplaceDivers(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (c) {
                default:
                    sb.append(c);
                    break;
                /*case 'A':
                sb.append('O');
                break;*/
                /*case 'D':
                case 'P':
                sb.append('T');
                break;*/
                case 'J':
                    sb.append('G');
                    break;
                /*case 'B':
                case 'V':
                sb.append('F');
                break;*/
                case 'M':
                    sb.append('N');
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * Supprime les caractères répétés deux fois de suite dans la chaine.
     * @param chaine la chaine sans lettres en double
     * @return
     */
    public static String supprimeDoublons(String chaine) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();

        if (chaine.length() > 0) {
            char precedent = chaine.charAt(0);

            sb.append(precedent);

            for (int i = 1; i < chaine.length(); i++) {
                char c = chaine.charAt(i);
                if (c != precedent) {
                    precedent = c;
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    /**
     * Supprime le T ou le X final de chaque mot trouvé.
     * @param chaine
     * @return
     */
    private static String supprimeTXFinal(String chaine) {
        if (chaine == null) {
            return vide;
        }
        if (chaine.length() < 2) {
            return chaine;
        }

        StringBuilder sb = new StringBuilder();

        int state = 0;
        char lastc = chaine.charAt(0);
        switch (lastc) {
            case ' ':
                state = 0;
                break;
            default:
                state = 1;
                break;
        }
        char c;
        for (int i = 1; i < chaine.length(); i++) {
            c = chaine.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                            break;
                        default:
                            sb.append(lastc);
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case ' ':
                            sb.append(lastc);
                            state = 0;
                            break;
                        default:
                            sb.append(lastc);
                            state = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case ' ':
                            if (lastc != 'X' && lastc != 'T') {
                                sb.append(lastc);
                            }
                            state = 0;
                            break;
                        default:
                            sb.append(lastc);
                            break;
                    }
                    break;
            }
            lastc = c;
        }
        if (state == 2 && lastc != ' ') {
            if (lastc != 'X' && lastc != 'T') {
                sb.append(lastc);
            }
        }

        return sb.toString();
    }

    /**
     * Remplace les chiffres 1 2 3 4 5 par des caractères non utilisés par l'algorithme phonétique, et vice versa.<br>
     * La correspondance utilisée est:
     * <table border=1>
     *  <tr><td>1</td><td>#</td></tr>
     *  <tr><td>2</td><td>!</td></tr>
     *  <tr><td>3</td><td>&</td></tr>
     *  <tr><td>4</td><td>)</td></tr>
     *  <tr><td>5</td><td>(</td></tr>
     * </table>
     * @param chaine
     * @param crypte si à true, convertit les chiffres en caractères non utilisés, sinon le contraire.
     * @return  la chaine traitée
     */
    public static String remplaceChiffres(String chaine, boolean crypte) {
        if (chaine == null) {
            return vide;
        }
        StringBuilder sb = new StringBuilder();
        if (crypte) {
            for (int i = 0; i < chaine.length(); i++) {
                char c = chaine.charAt(i);
                char newc = 0;
                switch (c) {
                    default:
                        newc = c;
                        break;
                    case '1':
                        newc = '#';
                        break;
                    case '2':
                        newc = '!';
                        break;
                    case '3':
                        newc = '&';
                        break;
                    case '4':
                        newc = ')';
                        break;
                    case '5':
                        newc = '(';
                        break;
                }
                sb.append(newc);
            }
        } else {
            for (int i = 0; i < chaine.length(); i++) {
                char c = chaine.charAt(i);
                char newc = 0;
                switch (c) {
                    default:
                        newc = c;
                        break;
                    case '#':
                        newc = '1';
                        break;
                    case '!':
                        newc = '2';
                        break;
                    case '&':
                        newc = '3';
                        break;
                    case ')':
                        newc = '4';
                        break;
                    case '(':
                        newc = '5';
                        break;
                }
                sb.append(newc);
            }
        }
        return sb.toString();
    }

    /**
     * Convertit un chiffre en lettres.<br>
     * Retourne l'entier donné s'il n'est pas un chiffre.
     */
    public static String convertitChiffreEnLettres(int chiffre) {
        switch (chiffre) {
            case 0:
                return "ZERO";
            case 1:
                return "UN";
            case 2:
                return "DEUX";
            case 3:
                return "TROIS";
            case 4:
                return "QUATRE";
            case 5:
                return "CINQ";
            case 6:
                return "SIX";
            case 7:
                return "SEPT";
            case 8:
                return "HUIT";
            case 9:
                return "NEUF";
        }
        return Integer.toString(chiffre);
    }

    /**
     * Convertit un nombre inférieur à mille en lettres
     * @param chiffre
     * @param mille vrai si il s'agit d'un millier.
     * @return Le nombre en toute lettre.
     */
    public static String convertitCentaineEnLettres(int chiffre, boolean mille) {
        int n1_cent, n2_cent;
        String x_cent = null;
        StringBuilder t_cent;

        n1_cent = chiffre;
        t_cent = new StringBuilder();

        //Centaine
        n2_cent = n1_cent / 100;
        n1_cent = n1_cent - n2_cent * 100;
        if (n2_cent != 0) {
            x_cent = convertitChiffreEnLettres(n2_cent);

            if (n2_cent != 1) {
                t_cent.append(x_cent);
                t_cent.append(' ');
            }
            t_cent.append("CENT");

            if (n2_cent != 1) {
                // Pas de s s'il y a un nombre derrière la centaine
                if (n1_cent == 0) {
                    // pas de s s'il y a le mot mille derrière la centaine
                    if (!mille) {
                        t_cent.append('S');
                    }
                }
            }
        }

        // Dizaine
        n2_cent = n1_cent;

        switch (n2_cent) {
            case 0:
                if (t_cent.length() == 0) // pour éviter CENT ZERO.
                {
                    return "ZERO";
                } else {
                    return t_cent.toString();
                }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                x_cent = convertitChiffreEnLettres(n2_cent);
                break;
            case 10:
                x_cent = "DIX";
                break;
            case 11:
                x_cent = "ONZE";
                break;
            case 12:
                x_cent = "DOUZE";
                break;
            case 13:
                x_cent = "TREIZE";
                break;
            case 14:
                x_cent = "QUATORZE";
                break;
            case 15:
                x_cent = "QUINZE";
                break;
            case 16:
                x_cent = "SEIZE";
                break;
            case 17:
                x_cent = "DIX SEPT";
                break;
            case 18:
                x_cent = "DIX HUIT";
                break;
            case 19:
                x_cent = "DIX NEUF";
                break;
            case 20:
                x_cent = "VINGT";
                break;
            case 21:
                x_cent = "VINGT ET UN";
                break;
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
                x_cent = "VINGT " + convertitChiffreEnLettres(n2_cent - (n2_cent / 10) * 10);
                break;
            case 30:
                x_cent = "TRENTE";
                break;
            case 31:
                x_cent = "TRENTE ET UN";
                break;
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
                x_cent = "TRENTE " + convertitChiffreEnLettres(n2_cent - (n2_cent / 10) * 10);
                break;
            case 40:
                x_cent = "QUARANTE";
                break;
            case 41:
                x_cent = "QUARANTE ET UN";
                break;
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                x_cent = "QUARANTE " + convertitChiffreEnLettres(n2_cent - (n2_cent / 10) * 10);
                break;
            case 50:
                x_cent = "CINQUANTE ";
                break;
            case 51:
                x_cent = "CINQUANTE ET UN";
                break;
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
                x_cent = "CINQUANTE " + convertitChiffreEnLettres(n2_cent - (n2_cent / 10) * 10);
                break;
            case 60:
                x_cent = "SOIXANTE ";
                break;
            case 61:
                x_cent = "SOIXANTE ET UN";
                break;
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
                x_cent = "SOIXUANTE " + convertitChiffreEnLettres(n2_cent - (n2_cent / 10) * 10);
                break;
            case 70:
                x_cent = "SOIXANTE DIX";
                break;
            case 71:
                x_cent = "SOIXANTE ET ONZE";
                break;
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                x_cent = "SOIXANTE " + convertitCentaineEnLettres(n2_cent - 60, false);
                break;
            case 80:
                x_cent = "QUATRE VINGTS";
                break;
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
                x_cent = "QUATRE VINGT " + convertitCentaineEnLettres(n2_cent - 80, false);
                break;
        }

        n1_cent = n1_cent - n2_cent;

        if (t_cent.length() > 0) {
            t_cent.append(' ');
        }
        t_cent.append(x_cent);

        return t_cent.toString();
    }

    /**
     * Obtient la représentation en toutes lettres d'un nombre.<br>
     * Basé sur un algorithme trouvé sur codesources.fr crée par gipp.<br>
     * La valeur maximale supportée est 999 999 999. Au delà, l'équivalent sous forme
     * de chaîne est retourné.
     * @param nombre
     * @return Le nombre en toute lettre
     */
    public static String convertitNombreEnLettres(int nombre) {
        int n1, n2;
        String x = null;
        StringBuilder t;

        if (nombre > 999999999) {
            return Integer.toString(nombre);
        }

        n1 = nombre;
        t = new StringBuilder();

        // million
        n2 = n1 / 1000000;
        x = convertitCentaineEnLettres(n2, false);
        n1 = n1 - n2 * 1000000;

        if (n2 != 0) {
            t.append(x);
            t.append(" MILLION");

            if (n2 != 1) {
                t.append('S');
            }
        }

        // milliers
        n2 = n1 / 1000;
        x = convertitCentaineEnLettres(n2, true);
        n1 = n1 - n2 * 1000;

        if (n2 != 0) {
            if (n2 != 1) {
                t.append(x);
                t.append(" MILLE");
            } else {
                if (t.length() > 0) {
                    t.append(' ');
                }
                t.append("MILLE");
            }
        }

        // unité
        n2 = n1;
        x = convertitCentaineEnLettres(n2, false);
        n1 = n1 - n2;

        if (n2 != 0) {
            if (t.length() > 0) {
                t.append(' ');
            }
            t.append(x);
        }

        // zéro
        if (t.length() == 0) {
            t.append("ZERO");
        }

        return t.toString();
    }

    /**
     * Convertit les nombres écrits sous forme de chiffres dans la chaine en leur
     * équivalent sous forme de lettres.
     * @param chaine
     * @return Le nombre en toute lettre
     */
    public static String convertitNombresEnLettres(String chaine) {
        int state = 0;
        int nombre = 0;
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < chaine.length(); i++) {
            char c = chaine.charAt(i);
            switch (state) {
                case 0:
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
                            nombre = c - '0';
                            state = 1;
                            break;
                        default:
                            res.append(c);
                            break;
                    }
                    break;
                case 1:
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
                            nombre = 10 * nombre + (c - '0');
                            break;
                        default:
                            res.append(convertitNombreEnLettres(nombre));
                            res.append(c);
                            state = 0;
                            break;
                    }
                    break;
            }
        }

        switch (state) {
            case 1:
                res.append(convertitNombreEnLettres(nombre));
                break;
        }

        return res.toString();
    }

    /**
     * Retourne le résultat du phonex, sauf lorsque ce résultat est vide, auquel cas la chaine originale
     * est renvoyée.
     * @param chaine
     * @return
     */
    public static String phonexNonVide(String chaine) {
        String res = phonex(chaine);
        if (res.length() == 0) {
            return chaine;
        }
        return res;
    }

    /**
     * Retourne la conversion phonétique de la chaine spécifiée.<br>
     * Inspiré d'un algorithme de Frédéric BROUARD conçu avec Florence Marquis, orthophoniste.<br>
     * Les règles suivantes ne sont pas prises en compte:
     * <ul>
     *     <li>A par O</li>
     *     <li>D et P par T</li>
     *     <li>B et V par F</li>
     * </ul>
     * En outre, l'algorithme ne prends pas en compte les accents.
     * @return l'équivalent sous forme de chaine
     */
    public static String phonex(String chaine) {
        if (chaine == null) {
            return vide;
        }

        chaine = convertitNombresEnLettres(chaine);

        chaine = remplaceChiffres(chaine, true);

        chaine = chaine.replace('Y', 'I');

        chaine = enleveH(chaine);

        chaine = chaine.replace("PH", "F");

        chaine = remplaceGAKA(chaine);

        chaine = remplaceAIN(chaine);

        chaine = remplaceEAU(chaine);

        chaine = remplaceAI(chaine);

        chaine = remplaceAN(chaine);

        chaine = chaine.replace("ON", "1");

        chaine = remplaceS(chaine);

        chaine = remplaceOE(chaine);

        chaine = remplaceCH(chaine);

        chaine = remplaceC(chaine);

        chaine = remplaceK(chaine);

        chaine = remplaceDivers(chaine);

        chaine = supprimeDoublons(chaine);

        chaine = supprimeTXFinal(chaine);

        return chaine.trim();
    }

    /**
     * Ajoute ligne à sb, avant ou après. Des espaces sont ajoutés entre le contenu de sb et ligne.
     * @param sb
     * @param ligne
     * @param apres true pour l'ajouter apres.
     */
    public static void appendWithSpace(StringBuilder sb, String ligne, boolean apres) {
        if (ligne != null) {
            if (apres) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ' && ligne.length() > 0 && ligne.charAt(0) != ' ') {
                    sb.append(' ');
                }
                sb.append(ligne);
            } else {
                if (ligne.length() > 0 && ligne.charAt(ligne.length() - 1) != ' ' && sb.length() > 0 && sb.charAt(0) != ' ') {
                    sb.insert(0, ' ');
                }
                sb.insert(0, ligne);
            }
        }
    }

    /**
     * Ajoute ligne à sb, avant ou après. Des espaces sont ajoutés entre le contenu de sb et ligne.
     * @param sb
     * @param ligne
     * @param apres true pour l'ajouter apres.
     */
    public static void appendWithSpace(StringBuilder sb, char ligne, boolean apres) {
        if (ligne != 0) {
            if (apres) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ' && ligne != ' ') {
                    sb.append(' ');
                }
                sb.append(ligne);
            } else {
                if (ligne != ' ' && sb.length() > 0 && sb.charAt(0) != ' ') {
                    sb.insert(0, ' ');
                }
                sb.insert(0, ligne);
            }
        }
    }

    /**
     * Ajoute ligne à sb, avant ou après.
     * @param sb
     * @param ligne
     * @param apres true pour l'ajouter apres.
     */
    public static void appendWithSpace(StringBuilder sb, StringBuilder ligne, boolean apres) {
        if (ligne != null) {
            if (apres) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ' && ligne.length() > 0 && ligne.charAt(0) != ' ') {
                    sb.append(' ');
                }
                sb.append(ligne);
            } else {
                if (ligne.length() > 0 && ligne.charAt(ligne.length() - 1) != ' ' && sb.length() > 0 && sb.charAt(0) != ' ') {
                    sb.insert(0, ' ');
                }
                sb.insert(0, ligne);
            }
        }
    }

    /**
     * Compare deux chaines, même lorsqu'elles sont nulles.<br>
     * Les chaines null et vide sont considérées comme égales.<br>
     * Les chaines sont différentes si l'une est nulle ou vide et l'autre non.
     * @return false si elle sont egales.
     */
    public static boolean compareString(String s1, String s2) {
        if (s1 == null || s1.trim().length() == 0) {
            if (s2 == null || s2.trim().length() == 0) {
                return false;
            }
            return true;
        } else // s1!=null && s1 non vide
        {
            if (s2 == null || s2.trim().length() == 0) {
                return true;
            }
            return s1.trim().compareTo(s2.trim()) != 0;
        }
    }
    static final int MAX_CHARACTERS = 200;
    static final int VERYMAX = 3000;

    /**
     * Retourne la position de arg2 dans arg1 avec une distance de levenstein maximale de pourcentagedecorrespondance.<br>
     * L'algorithme utilisé est une traduction de l'algorithme implémenté en c pour les procédures stockées.
     * @param arg1 la chaine dans laquelle rechercher arg2
     * @param arg2 la chaine à rechercher dans arg1
     * @param pourcentagedecorrespondance le nombre de fautes tolérées au maximum
     * @param start l'index à partir duquel chercher la chaine
     * @return 0 si la chaine n'est pas trouvée ou dépasse MAX_CHARACTERS caractères ou 1+fin_chaine+256*debut_chaine+65536*nb_fautes
     */
    public static int position_levenstein_joker(String arg1, String arg2, int pourcentagedecorrespondance, int start) {
        int i, offset, nb_fautes;
        int h, w;
        int min;
        int endindex;
        int premiere_colonne;

        int[] test = new int[(MAX_CHARACTERS + 1) * (MAX_CHARACTERS + 1)];

        h = arg1.length();
        w = arg2.length();

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return 0;
        }

        if (start >= h) {
            return 0;
        }

        i = 0;
        for (int j = 0; j <= w; j++) {
            test[j] = j;
        }

        offset = w + 2;
        nb_fautes = 0;
        min = VERYMAX; // les valeurs suivantes sont forcemment petites
        endindex = VERYMAX;

        // pour chaque ligne
        for (i = 1, premiere_colonne = 0; i <= (h - start) && endindex == VERYMAX; i++, offset++) {
            char c;
            int tempmin;
            c = arg1.charAt(i - 1 + start);
            if (c == ' ') {
                test[offset - 1] = 0;
            } else {
                test[offset - 1] = ++premiere_colonne;
            }

            // pour chaque colonne
            for (int j = 1; j <= w; j++, offset++) {
                int cout;
                char c2 = arg2.charAt(j - 1);
                if (c == c2) {
                    cout = 0;
                } else {
                    cout = 1;
                }

                test[offset] = Algos.min(test[offset - (w + 1)] + 1,
                        test[offset - 1] + 1,
                        test[offset - 1 - (w + 1)] + cout);
            }

            if ((i == h - start || arg1.charAt(i + start) == ' ') &&
                    (100 * (w - (nb_fautes = test[offset - 1]))) >= (pourcentagedecorrespondance * w)) {
                endindex = i; // Si un mot satisfaisant est trouvé, la recherche s'arrête.
            }
        }

        if (endindex != VERYMAX) {
            int startindex = endindex;
            int j = w;

            // Cherche l'index de départ : c'est l'index du caractère qui commence une suite de
            // correspondance avec la chaine.
            // Pour cela, la suite des correspondances trouvées jusqu'au caractère final
            // est remontée, jusqu'à aboutir au premier caractère d'une des deux chaines.
            while (j != 1 && startindex != 1) {
                int dessus, cote, diag;
                min = test[j + startindex * (w - 1)];
                dessus = test[j - 1 + startindex * (w + 1)];
                cote = test[j + (startindex - 1) * (w + 1)];
                diag = test[j - 1 + (startindex - 1) * (w + 1)];

                // le dessus est pris comme référence
                if (dessus <= diag) {
                    if (dessus <= cote) {
                        j--;
                    } else {
                        startindex--;
                    }
                } else // puis la diagonale
                {
                    if (diag <= cote) {
                        j--;
                        startindex--;
                    } else {
                        startindex--;
                    }
                }
            }

            startindex += start - 1;
            endindex += start - 1;

            // Pour plus de lisibilité, des mots entiers sont recherchés

            // Cherche le début du mot trouvé
            while (startindex > start && arg1.charAt(startindex - 1) != ' ') {
                startindex--;
            }

            // Cherche la fin du mot trouvé
            while (endindex < h - 1 && arg1.charAt(endindex + 1) != ' ') {
                endindex++;
            }

            // Retourne la position
            return 1 + endindex + 256 * startindex + 65536 * nb_fautes;
        }

        return 0;
    }

    public static void main(String[] args) {
        System.out.println("Récupère l'équivalent phonétique de H H H");
        System.out.println(phonex("H H H"));

        System.out.println("Récupère l'équivalent phonétique de ABC H H H ABC ");
        System.out.println(phonex("ABC H H H ABC"));

        System.out.println("Récupère l'équivalent phonétique de X 12");
        System.out.println(phonex("X 12") + " attendu : X 12");

        System.out.println("Récupère l'équivalent phonétique de PHYLAURHEIMSMET LEMOINET");
        System.out.println(phonex("PHYLAURHEIMSMET LEMOINET") + " attendu : FILOR4SNY LEN2NY");

        System.out.println("Récupère l'équivalent phonétique de HOPITAL");
        System.out.println(phonex("HOPITAL") + " attendu : OTITOL");

        System.out.println("Récupère l'équivalent phonétique de PHYLAURHEIMSMET");
        System.out.println(phonex("PHYLAURHEIMSMET") + " attendu : FILOR4SNY");

        System.out.println("Récupère l'équivalent phonétique de LEMOINE");
        System.out.println(phonex("LEMOINE") + " attendu : LEN2NE");

        System.out.println("Récupère l'équivalent phonétique de AO");
        System.out.println(phonex("AO 20") + " attendu : O 20");

        System.out.println("Récupère l'équivalent phonétique de CASANOVA");
        System.out.println(phonex("CASANOVA") + " attendu : KOZONOFO");

        System.out.println("Supprime le t ou le x final dans TEST");
        System.out.println(supprimeTXFinal("TEST") + " attendu : TES");

        System.out.println("Supprime les lettres en double dans TARTUFFETT");
        System.out.println(supprimeDoublons("TARTUFFETT") + " attendu : TARTUFET");

        System.out.println("Remplace A par O, D et P par T, J par G, B et V par F, M par N dans AODPJGBVM");
        System.out.println(remplaceDivers("AODPJGBVM") + " attendu : OOTTGGFFN");

        System.out.println("Remplace C, Q, QU, GU par K, GA par KA, GO par KO, GY par KY dans CQQUGAGOGUC");
        System.out.println(remplaceK("CQQUGAGOGUC") + " attendu : KKKKAKOKK");

        System.out.println("Remplace C par S s'il est suivi par E ou I dans CECICC");
        System.out.println(remplaceC("CECICC") + " attendu : SESICC");

        System.out.println("Remplace CH, SCH, SH, SS, SC par 5 dans CHSCHSSCHSC");
        System.out.println(remplaceCH("CHSCHSSCHSC") + " attendu : 55555");

        System.out.println("Remplace OE, EU par E, AU par O, OI OY par 2, OU par 3dans OEEUAUOIOYO");
        System.out.println(remplaceOE("OEEUAUOIOYO") + " attendu : EEO22O");

        System.out.println("Remplace S en Z s'il est suivi et précédé par a e i o u 1 2 3 4 dans ASAKS1SSAS");
        System.out.println(remplaceS("ASAKS1SSAS") + " attendu : AZAKS1SSAS");

        System.out.println("Remplace AN,AM,EN,EM par 1 et IN par 4 s'il ne sont pas suivi de a e i o u 1 2 3 4 dans ANAMAMKEININKIN");
        System.out.println(remplaceAN("ANAMAMKEININKIN") + " attendu : ANAM1KEIN4K4");

        System.out.println("Remplace AI, EI par Y, ER par YR, ESS par YSS, ET par YT dans AIEIESSETER");
        System.out.println(remplaceAI("AIEIESSETER"));

        System.out.println("Remplace EAU par O, OUA par 2, EIN, AIN, EIM, AIM par 4 dans EAUOUAINAIKEINEIM");
        System.out.println(remplaceEAU("EAUOUAINAIKEINEIM"));

        System.out.println("Remplace ain, ein, aim, eim par y s'il est suivi de a,e,i,o,u dans AINAINEIMEINKAINQ");
        System.out.println(remplaceAIN("AINAINEIMEINKAINQ"));

        System.out.println("Remplace ga par ka s'il est suivi de n, m, in, ou im dans GAINGARGANGKGAL");
        System.out.println(remplaceGAKA("GAINGARGANGKGAL"));

        System.out.println("Enlève les h non précédés de p c s dans HERCHIN");
        System.out.println(enleveH("HERCHIN"));

        System.out.println("Supprime les zéros inutiles de 0001 et 000 et 0");
        System.out.println(supprimeZerosInutiles("0001") + " " + supprimeZerosInutiles("000") + " " + supprimeZerosInutiles("0"));

        System.out.println("Supprime les zéros inutiles de 0001 et 000 et 0 et 75001 et 075001 et 05001 et 005001 et 5001");
        System.out.println(supprimeZerosInutilesCodePostal("0001") + " " + supprimeZerosInutilesCodePostal("000") + " " + supprimeZerosInutilesCodePostal("0"));
        System.out.println(supprimeZerosInutilesCodePostal("75001") + " " + supprimeZerosInutilesCodePostal("075001") + " " + supprimeZerosInutilesCodePostal("05001"));
        System.out.println(supprimeZerosInutilesCodePostal("005001") + " " + supprimeZerosInutilesCodePostal("5001"));

        System.out.println("Supprime les zéros inutiles de 'test 0001 test' et '000 test 000' et '0 test' et '75001' et 'test 075001' et '05001 test' et '005001 test' et '5001' et '01 test' et '01'");
        System.out.println(supprimeZerosInutiles("test 0001 test", true) + " " + supprimeZerosInutiles("000 test 000", true) + " " + supprimeZerosInutiles("0 test", true));
        System.out.println(supprimeZerosInutiles("75001", true) + " " + supprimeZerosInutiles("test 075001", true) + " " + supprimeZerosInutiles("05001 test", true));
        System.out.println(supprimeZerosInutiles("005001 test", true) + " " + supprimeZerosInutiles("5001", true));
        System.out.println(supprimeZerosInutiles("01 test", true) + " " + supprimeZerosInutiles("01", true));

        System.out.println("Supprime ST dans 'ST EUSTACHE ST BERNADETTE ST'");
        System.out.println(supprimeMot("ST EUSTACHE ST BERNADETTE ST", "ST"));

        System.out.println("Supprime PANTIN dans 'PORTE A PORTE PANTIN'");
        System.out.println(supprimeMot("PORTE A PORTE PANTIN", "PANTIN"));

        System.out.println("Supprime les articles dans 'BD DE L HOPITAL A SEINE DU MARECHAL DES SABLONS SUR ROCHE A AUX SE ET S ASSEOIR PAR TERRE AU DESSUS OU LES PETUNIAS D AVANT LA GUERRE LE LE LE'");
        System.out.println(sansarticles("BD DE L HOPITAL A SEINE DU MARECHAL DES SABLONS SUR ROCHE A AUX SE ET S ASSEOIR PAR TERRE AU DESSUS OU LES PETUNIAS D AVANT LA GUERRE LE LE LE"));

        System.out.println("Trouve le dernier mot de 'BD DE L HOPITAL'");
        System.out.println(derniermot("BD DE L HOPITAL"));

        System.out.println("Supprime les espaces dans '  BD  DE   L'HOPITAL  ':");
        System.out.println(supprimeRepetitionEspace("BD  DE   L'HOPITAL"));

        System.out.println("Supprime les accents dans 'bd de l'hôpital'");
        System.out.println(supprimeAccents("bd de l'hôpital"));

        System.out.println("Supprime les accents dans 'abâtardi'");
        System.out.println(supprimeAccents("abâtardi"));

        System.out.println("Supprime la ponctuation dans '24, bd de l'hôpital'");
        System.out.println(supprimePonctuation("24, bd de l'hôpital"));

        System.out.println("Supprime les accents, la ponctuation et les esapces répétés dans '24,  bd de l'hôpital'");
        System.out.println(supprimeAccentRepetitionEspacePonctuation("24,  bd de l'hôpital"));

        System.out.println("Sépare les nombres des lettres par un espace dand '24-26 bd de l'hopital75000 paris'");
        System.out.println(separeEspacesEtNombres("24-26 bd de l'hopital75000 paris"));

        System.out.println("Première passe de normalisation sur '24-26 bd de l'hôpital75000 paris'");
        System.out.println(normalise_1("24-26 bd de l'hôpital75000 paris"));

        System.out.println("Première passe de normalisation sur '24-26 bd de l'hôpital\r\n75000 paris'");
        System.out.println(normalise_1("24-26 bd de l'hôpital\r\n75000 paris"));

        System.out.println("Première passe de normalisation sur '24-26 bd de l'hôpital♪75000 paris'");
        System.out.println(normalise_1("24-26 bd de l'hôpital♪75000 paris"));

        System.out.println("Trouve les premiers numéros de ' 24 26-28 test'");
        ArrayList<Integer> numeros = Algos.trouvePremiersNumeros(" 24 26-28 test");
        for (int i = 0; i < numeros.size(); i++) {
            System.out.println(numeros.get(i));
        }

        System.out.println("Convertit des nombres en lettres:");
        int nombre = 0;
        System.out.println("Convertit " + nombre + " : '" + convertitNombreEnLettres(nombre) + "'");
        nombre = 10;
        System.out.println("Convertit " + nombre + " : '" + convertitNombreEnLettres(nombre) + "'");
        nombre = 137;
        System.out.println("Convertit " + nombre + " : '" + convertitNombreEnLettres(nombre) + "'");
        nombre = 1944;
        System.out.println("Convertit " + nombre + " : '" + convertitNombreEnLettres(nombre) + "'");
        nombre = 101;
        System.out.println("Convertit " + nombre + " : '" + convertitNombreEnLettres(nombre) + "'");

        System.out.println("Convertit les nombres de la chaine en lettres:");
        String chaine = "VOIE 10";
        System.out.println("Convertit " + chaine + " : " + convertitNombresEnLettres(chaine));
        chaine = "AV 4 JUIN 1944 et 6 SEPTEMBRE 1912";
        System.out.println("Convertit " + chaine + " : " + convertitNombresEnLettres(chaine));

        System.out.println("Cherche PARIS dans RUE DE PARI avec fautes d'orthographes:");
        int position = position_levenstein_joker("RUE DE PARI", "PARIS", 80, 0);
        if (position != (1 + 10 + 7 * 256 + 1 * 65536)) {
            if (position == 0) {
                System.out.println("Erreur : la chaine n'a pas été trouvée.");
            } else {
                int end = (position - 1) % 256;
                int start = ((position - 1) % 65536) / 256;
                int fautes = ((position - 1) / 65536);
                System.out.println("Erreur: la chaine a été trouvée en [" + start + "," + end + "] en " + fautes + " faute(s)");
            }
        } else {
            System.out.println("Succes");
        }

        System.out.println("Cherche PARIS dans RUE DE PARI avec fautes d'orthographes:");
        position = position_levenstein_joker("PARI GAGNANT", "PARIS", 80, 0);
        if (position != (1 + 3 + 0 * 256 + 1 * 65536)) {
            if (position == 0) {
                System.out.println("Erreur : la chaine n'a pas été trouvée.");
            } else {
                int end = (position - 1) % 256;
                int start = ((position - 1) % 65536) / 256;
                int fautes = ((position - 1) / 65536);
                System.out.println("Erreur: la chaine a été trouvée en [" + start + "," + end + "] en " + fautes + " faute(s)");
            }
        } else {
            System.out.println("Succes");
        }
    }
}
