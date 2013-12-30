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

/**
 * Permet de trouver des dates dans une chaine.<br>
 * Une instance de cette classe doit être crée pour chaque nouvelle recherche.
 * Les dates sont de la forme [nom_de_jour] jour_du_mois mois [annee].
 * nom_du_jour peut avoir les valeurs suivantes:
 * <ul><li>lundi</li>
 *     <li>mardi</li>
 * <li>mercredi</li>
 * <li>jeudi</li>
 * <li>vendredi</li>
 * <li>samedi</li>
 * <li>dimanche</li></ul>
 * Les jours du mois peuvent être écrit en lettre ou en chiffre<br>
 * Les mois peuvent être écrit en chiffre ou sous l'une des forme suivante:
 * <ul><li>janvier</li>
 * <li>fevrier</li>
 * <li>mars<li>
 * <li>avril</li>
 * <li>mai</li>
 * <li>juin</li>
 * <li>juillet</li>
 * <li>ao�t</li>
 * <li>septembre</li>
 * <li>octobre</li>
 * <li>novembre</li>
 * <li>decembre</li></ul>
 * L'année peut être écrite avec 2 ou quatre chiffres, mais avant 2100.<br>
 * @author Julien
 */
public class GestionDate {

    final static int TOKEN_AVRIL = 1;
    final static int TOKEN_AOUT = 2;
    final static int TOKEN_CINQ = 3;
    final static int TOKEN_DECEMBRE = 4;
    final static int TOKEN_DEUX = 5;
    final static int TOKEN_DIMANCHE = 6;
    final static int TOKEN_DIX = 7;
    final static int TOKEN_SEPT = 8;
    final static int TOKEN_HUIT = 9;
    final static int TOKEN_NEUF = 10;
    final static int TOKEN_DOUZE = 11;
    final static int TOKEN_JANVIER = 12;
    final static int TOKEN_JEUDI = 13;
    final static int TOKEN_JUIN = 14;
    final static int TOKEN_JUILLET = 15;
    final static int TOKEN_FEVRIER = 16;
    final static int TOKEN_LUNDI = 18;
    final static int TOKEN_MAI = 19;
    final static int TOKEN_MARS = 20;
    final static int TOKEN_MARDI = 21;
    final static int TOKEN_MERCREDI = 22;
    final static int TOKEN_NOVEMBRE = 24;
    final static int TOKEN_OCTOBRE = 25;
    final static int TOKEN_ONZE = 26;
    final static int TOKEN_QUATRE = 27;
    final static int TOKEN_QUATORZE = 28;
    final static int TOKEN_QUINZE = 29;
    final static int TOKEN_SAMEDI = 30;
    final static int TOKEN_SEIZE = 31;
    final static int TOKEN_SEPTEMBRE = 33;
    final static int TOKEN_SIX = 34;
    final static int TOKEN_TREIZE = 35;
    final static int TOKEN_TRENTE = 36;
    final static int TOKEN_ET = 37;
    final static int TOKEN_UN = 38;
    final static int TOKEN_TROIS = 39;
    final static int TOKEN_VENDREDI = 40;
    final static int TOKEN_VINGT = 41;
    
    final static int TOKEN_NOM_DE_JOUR = 100;
    final static int TOKEN_JOUR_DU_MOIS = 200;
    final static int TOKEN_MOIS = 300;
    final static int TOKEN_NOMBRE_INF_32 = 400;
    final static int TOKEN_NOMBRE_INF_2100 = 500;
    final static int TOKEN_AUTRE = 600;
    final static int TOKEN_FINCHAINE = 700;
    final static int TOKEN_NOMBRE_INF_13 = 800;
    /** Le caractère qui est en cours de traitement*/
    char c;
    /** La chaine dans laquelle la date est recherchée */
    String chaine;
    /** La position actuelle dans la chaine */
    int index;
    /** Le type du dernier token trouvé */
    int token;
    /** la chaine du token trouvé */
    String chaineToken;
    /** L'index du token trouvé */
    int indexToken;
    
    /**
     * Les mêmes informations pour un super-token (grammaire LL2)
     */
    int superToken;
    int indexSuperToken;
    String chaineSuperTokenNormal;
    String chaineSuperToken;

    /**
     * Retourne si la chaine est un mois de l'année (français).
     * @param chaine
     * @return
     */
    public static boolean estUnMois(String chaine)
    {
        if (chaine==null) return false;
        if (chaine.length()<3) return false;
        
        switch(chaine.charAt(0))
        {
            case 'J':
                switch(chaine.charAt(1))
                {
                    case 'A':
                        if (chaine.length()!=7) return false;
                        return chaine.indexOf("NVIER",2)!=-1;
                    case 'U':
                        if (chaine.charAt(2)=='I')
                        {
                            if (chaine.length()<4) return false;
                            switch(chaine.charAt(3))
                            {
                                case 'N':
                                    if (chaine.length()==4) return true;
                                    return false;
                                case 'L':
                                    if (chaine.length()!=7) return false;
                                    return chaine.indexOf("LET",4)!=-1;
                            }
                        }
                        break;
                }
                break;
            case 'F':
                if (chaine.length()!=7) return false;
                return chaine.indexOf("EVRIER",1)!=-1;
            case 'M':
                if (chaine.charAt(1)=='A')
                {
                    switch(chaine.charAt(2))
                    {
                        case 'I':
                            if (chaine.length()==3) return true;
                            return false;
                        case 'R':
                            if (chaine.length()==4 && chaine.charAt(3)=='S') return true;
                            return false;
                    }
                }
                break;
            case 'A':
                switch(chaine.charAt(1))
                {
                    case 'V':
                        if (chaine.length()!=5) return false;
                        return chaine.indexOf("RIL",2)!=-1;
                    case 'O':
                        if (chaine.length()!=4) return false;
                        return chaine.indexOf("UT",2)!=-1;
                }
                break;
            case 'S':
                if (chaine.length()!=9) return false;
                return chaine.indexOf("EPTEMBRE",1)!=-1;
            case 'O':
                if (chaine.length()!=7) return false;
                return chaine.indexOf("CTOBRE",1)!=-1;
            case 'N':
                if (chaine.length()!=8) return false;
                return chaine.indexOf("OVEMBRE",1)!=-1;
            case 'D':
                if (chaine.length()!=8) return false;
                return chaine.indexOf("ECEMBRE",1)!=-1;
        }
        return false;
    }
    
    /**
     * Trouve une date dans la chaine spécifiée, à partir de l'index spécifié.
     * @param chaine
     * @param index
     * @return
     */
    public RefDate trouveDate(String chaine, int index) {
        this.chaine = chaine;
        String nomDeJour = null;
        int indexNomDeJour = -1;

        String jourDuMois = null;
        int indexJourDuMois = -1;
        boolean jourDuMoisEstNombre = false;
        String jourDuMoisNormal = null;

        String nomDuMois = null;
        int indexNomDuMois = -1;
        boolean nomDuMoisEstNombre = false;

        String annee = null;
        int indexAnnee = -1;

        this.index = index;
        boolean done = false;
        int state = 0;

        nextChar();
        nextToken();
        
        while (!done && (superToken = nextSuperToken()) != GestionDate.TOKEN_FINCHAINE)
        {
            switch (state)
            {
                case 0:
                    switch (superToken) {
                        case GestionDate.TOKEN_NOMBRE_INF_32:
                        case GestionDate.TOKEN_NOMBRE_INF_13:
                            jourDuMois = chaineSuperToken;
                            indexJourDuMois = indexSuperToken;
                            nomDeJour = null;
                            indexNomDeJour = -1;
                            jourDuMoisEstNombre = true;
                            state = 2;
                            break;
                        case GestionDate.TOKEN_NOM_DE_JOUR:
                            state = 1;
                            nomDeJour = chaineSuperToken;
                            indexNomDeJour = indexSuperToken;
                            break;
                        case GestionDate.TOKEN_JOUR_DU_MOIS:
                            jourDuMois = chaineSuperToken;
                            indexJourDuMois = indexSuperToken;
                            jourDuMoisNormal = chaineSuperTokenNormal;
                            nomDeJour = null;
                            indexNomDeJour = -1;
                            state = 2;
                            break;
                        default:
                            break;
                    }
                    break;
                case 1: // après un nom de jour
                    switch (superToken) {
                        case GestionDate.TOKEN_NOMBRE_INF_13:
                        case GestionDate.TOKEN_NOMBRE_INF_32:
                            jourDuMois = chaineSuperToken;
                            indexJourDuMois = indexSuperToken;
                            jourDuMoisEstNombre = true;
                            state = 2;
                            break;
                        case GestionDate.TOKEN_NOM_DE_JOUR:
                            state = 1;
                            nomDeJour = chaineSuperToken;
                            indexNomDeJour = indexSuperToken;
                            break;

                        case GestionDate.TOKEN_JOUR_DU_MOIS:
                            jourDuMois = chaineSuperToken;
                            indexJourDuMois = indexSuperToken;
                            jourDuMoisNormal = chaineSuperTokenNormal;
                            jourDuMoisEstNombre = false;
                            state = 2;
                            break;
                        default:
                            state = 0;
                            break;
                    }
                    break;
                case 2: // après un jour du mois
                    switch (superToken) {
                        case GestionDate.TOKEN_NOM_DE_JOUR:
                            state = 1;
                            nomDeJour = chaineSuperToken;
                            indexNomDeJour = indexSuperToken;
                            break;

                        case GestionDate.TOKEN_NOMBRE_INF_13:
                            nomDuMois = chaineSuperToken;
                            indexNomDuMois = indexSuperToken;
                            nomDuMoisEstNombre = true;
                            state = 3;
                            break;

                        case GestionDate.TOKEN_NOMBRE_INF_32:
                            jourDuMois = chaineSuperToken;
                            indexJourDuMois = indexSuperToken;
                            jourDuMoisEstNombre = true;
                            nomDeJour = null;
                            indexNomDeJour = -1;
                            state = 2;
                            break;
                            
                        case GestionDate.TOKEN_JOUR_DU_MOIS:
                            jourDuMois = chaineSuperToken;
                            indexJourDuMois = indexSuperToken;
                            jourDuMoisEstNombre = false;
                            jourDuMoisNormal = chaineSuperTokenNormal;
                            nomDeJour = null;
                            indexNomDeJour = -1;
                            state = 2;
                            break;
                            
                        case GestionDate.TOKEN_MOIS:
                            nomDuMois = chaineSuperToken;
                            indexNomDuMois = indexSuperToken;
                            nomDuMoisEstNombre = false;
                            state = 3;
                            break;
                        default:
                            state = 0;
                            break;
                    }
                    break;
                case 3: // après un nom de mois
                    switch (superToken) {
                        default: // l'année est optionnelle.
                            done = true;
                            break;                            

                        case GestionDate.TOKEN_NOMBRE_INF_13:
                        case GestionDate.TOKEN_NOMBRE_INF_32:
                        case GestionDate.TOKEN_NOMBRE_INF_2100:
                            annee = chaineSuperToken;
                            indexAnnee = indexSuperToken;
                            done = true;
                            break;
                    }
                    break;
            }
        }
        if (done) {
            int lastindex = -1;
            if (indexAnnee != -1) {
                lastindex = indexAnnee + annee.length();
            } else {
                lastindex = indexNomDuMois + nomDuMois.length();
            }
            int firstindex = -1;
            if (indexNomDeJour != -1) {
                firstindex = indexNomDeJour;
            } else {
                firstindex = indexJourDuMois;
            }
            String chaineTrouvee = chaine.substring(firstindex, lastindex);

            return new RefDate(chaineTrouvee, firstindex, chaine, nomDeJour, indexNomDeJour, jourDuMois, indexJourDuMois, jourDuMoisNormal, jourDuMoisEstNombre, nomDuMois, indexNomDuMois, nomDuMoisEstNombre, annee, indexAnnee);
        }

        // Aucune date n'a été trouvée.
        return new RefDate("", index);
    }

    /**
     * Obtient le super-token suivant (ensemble de token formant un supertoken ex:MOIS).
     * @return
     */
    public int nextSuperToken()
    {
        indexSuperToken = indexToken;
        StringBuilder sb = new StringBuilder();
        
        switch(token)
        {
            case GestionDate.TOKEN_FINCHAINE:
                chaineSuperToken = "";
                superToken = GestionDate.TOKEN_FINCHAINE;
                break;
            default:
                superToken = GestionDate.TOKEN_AUTRE;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_LUNDI:
            case GestionDate.TOKEN_MARDI:
            case GestionDate.TOKEN_MERCREDI:
            case GestionDate.TOKEN_JEUDI:
            case GestionDate.TOKEN_VENDREDI:
            case GestionDate.TOKEN_SAMEDI:
            case GestionDate.TOKEN_DIMANCHE:
                superToken = GestionDate.TOKEN_NOM_DE_JOUR;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
                
            case GestionDate.TOKEN_JANVIER:
            case GestionDate.TOKEN_FEVRIER:
            case GestionDate.TOKEN_MARS:
            case GestionDate.TOKEN_AVRIL:
            case GestionDate.TOKEN_MAI:
            case GestionDate.TOKEN_JUIN:
            case GestionDate.TOKEN_JUILLET:
            case GestionDate.TOKEN_AOUT:
            case GestionDate.TOKEN_SEPTEMBRE:
            case GestionDate.TOKEN_OCTOBRE:
            case GestionDate.TOKEN_NOVEMBRE:
            case GestionDate.TOKEN_DECEMBRE:
                superToken = GestionDate.TOKEN_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_UN:
                chaineSuperTokenNormal = "1";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_DEUX:
                chaineSuperTokenNormal = "2";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_TROIS:
                chaineSuperTokenNormal = "3";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_QUATRE:
                chaineSuperTokenNormal = "4";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_CINQ:
                chaineSuperTokenNormal = "5";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_SIX:
                chaineSuperTokenNormal = "6";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_SEPT:
                chaineSuperTokenNormal = "7";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_HUIT:
                chaineSuperTokenNormal = "8";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_NEUF:
                chaineSuperTokenNormal = "9";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_ONZE:
                chaineSuperTokenNormal = "11";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_DOUZE:
                chaineSuperTokenNormal = "12";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_TREIZE:
                chaineSuperTokenNormal = "13";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_QUATORZE:
                chaineSuperTokenNormal = "14";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_QUINZE:
                chaineSuperTokenNormal = "15";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_SEIZE:
                chaineSuperTokenNormal = "16";
                superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
            case GestionDate.TOKEN_DIX:
                sb.append("DIX");
                token = nextToken();
                switch(token)
                {
                    default:
                        chaineSuperTokenNormal = "10";
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        chaineSuperToken = "DIX";
                        break;
                    case GestionDate.TOKEN_SEPT:
                        chaineSuperTokenNormal = "17";
                        sb.append(" SEPT");
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        chaineSuperToken = sb.toString();
                        token = nextToken();
                        break;
                    case GestionDate.TOKEN_HUIT:
                        chaineSuperTokenNormal = "18";
                        sb.append(" HUIT");
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        chaineSuperToken = sb.toString();
                        token = nextToken();
                        break;
                    case GestionDate.TOKEN_NEUF:
                        chaineSuperTokenNormal = "19";
                        sb.append(" NEUF");
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        chaineSuperToken = sb.toString();
                        token = nextToken();
                        break;
                }
                break;
            case GestionDate.TOKEN_VINGT:
                sb.append("VINGT");
                token = nextToken();
                switch(token)
                {
                    default:
                        chaineSuperTokenNormal = "20";
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        chaineSuperToken = "VINGT";
                        break;
                    case GestionDate.TOKEN_ET:
                        sb.append(" ET");
                        token = nextToken();
                        if (token == GestionDate.TOKEN_UN)
                        {
                            chaineSuperTokenNormal = "21";
                            superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                            chaineSuperToken = "VINGT ET UN";
                            nextToken();
                        }
                        else
                        {
                            superToken = GestionDate.TOKEN_AUTRE;
                            chaineSuperToken = sb.toString();
                        }
                        break;
                    case GestionDate.TOKEN_DEUX:
                        chaineSuperTokenNormal = "22";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_TROIS:
                        chaineSuperTokenNormal = "23";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_QUATRE:
                        chaineSuperTokenNormal = "24";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_CINQ:
                        chaineSuperTokenNormal = "25";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_SIX:
                        chaineSuperTokenNormal = "26";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_SEPT:
                        chaineSuperTokenNormal = "27";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_HUIT:
                        chaineSuperTokenNormal = "28";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                    case GestionDate.TOKEN_NEUF:
                        chaineSuperTokenNormal = "29";
                        sb.append(' ');
                        sb.append(chaineToken);
                        chaineSuperToken = sb.toString();
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        nextToken();
                        break;
                }
                break;
            case GestionDate.TOKEN_TRENTE:
                sb.append("TRENTE");
                token = nextToken();
                switch(token)
                {
                    case GestionDate.TOKEN_ET:
                        sb.append(" ET");
                        token = nextToken();
                        if (token==GestionDate.TOKEN_UN)
                        {
                            chaineSuperTokenNormal = "31";
                            chaineSuperToken = "TRENTE ET UN";
                            superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                            nextToken();
                        }
                        else
                        {
                            chaineSuperToken = sb.toString();
                            superToken = GestionDate.TOKEN_AUTRE;
                        }
                        break;
                    default:
                        chaineSuperTokenNormal = "30";
                        chaineSuperToken = "TRENTE";
                        superToken = GestionDate.TOKEN_JOUR_DU_MOIS;
                        break;
                }
                break;
            case GestionDate.TOKEN_NOMBRE_INF_13:
            case GestionDate.TOKEN_NOMBRE_INF_2100:
            case GestionDate.TOKEN_NOMBRE_INF_32:
                superToken = token;
                chaineSuperToken = chaineToken;
                token = nextToken();
                break;
        }
        return superToken;
    }
    
    /**
     * Retourne le prochain caractère à traiter.
     * @return
     */
    public char nextChar() {
        if (index < chaine.length()) {
            c = chaine.charAt(index);
            index++;
        } else {
            c = 0;
        }
        return c;
    }

    /**
     * Valide le token ou valide un token autre
     * @param chaineToken
     * @param index
     * @param token
     * @return
     */
    public void checkToken(StringBuilder sb, String chaineToken, int index, int token) {
        while (index < chaineToken.length() && c == chaineToken.charAt(index)) {
            sb.append(c);
            c = nextChar();
            index++;
        }
        if (index == chaineToken.length()) {
            if (c == ' ') {
                c = nextChar();
                this.chaineToken = chaineToken;
                this.token = token;
                return;
            } else if (c == 0) {
                this.chaineToken = chaineToken;
                this.token = token;
                return;
            }
        }
        else
            checkAutre(sb);
    }

    /**
     * Valide les caractères demandés.
     * @param sb
     * @param chaineToken
     * @param index
     * @return
     */
    public boolean checkFirst(StringBuilder sb, String chaineToken, int index) {
        while (index < chaineToken.length() && c == chaineToken.charAt(index)) {
            sb.append(c);
            c = nextChar();
            index++;
        }
        if (index == chaineToken.length()) {
            return true;
        }
        return false;
    }

    /**
     * Valide un autre mot.
     * @param sb
     */
    public void checkAutre(StringBuilder sb) {
        while (c != 0 && Character.isLetter(c)) {
            sb.append(c);
            c = nextChar();
        }
        this.chaineToken = sb.toString();
        this.token = GestionDate.TOKEN_AUTRE;
    }

    public void checkNumber(StringBuilder sb)
    {
        do {
            c = nextChar();
        } while (c != 0 && c == '0');
        if (c == 0) {
            chaineToken = "";
            token = GestionDate.TOKEN_AUTRE;
        } else
            checkNumber2(sb);
    }
    
    /**
     * Valide un nombre.
     * @param sb
     */
    public void checkNumber2(StringBuilder sb)
    {
        switch (c)
        {
            case '1':
                sb.append('1');
                c = nextChar();
                if (c == 0) {
                    chaineToken = "1";
                    token = GestionDate.TOKEN_NOMBRE_INF_13;
                }
                switch (c)
                {
                    case '0':
                    case '1':
                    case '2':
                        sb.append(c);
                        c = nextChar();
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
                                c = nextChar();
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
                                        c = nextChar();
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
                                                do {
                                                    sb.append(c);
                                                    c = nextChar();
                                                } while (c != 0 && Character.isDigit(c));
                                                chaineToken = sb.toString();
                                                token = GestionDate.TOKEN_AUTRE;
                                                break;
                                            default:
                                                chaineToken = sb.toString();
                                                token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                                break;
                                        }
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_13;
                                break;
                        }
                        break;
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        sb.append(c);
                        c = nextChar();
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
                                c = nextChar();
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
                                        c = nextChar();
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
                                                do {
                                                    sb.append(c);
                                                    c = nextChar();
                                                } while (c != 0 && Character.isDigit(c));
                                                chaineToken = sb.toString();
                                                token = GestionDate.TOKEN_AUTRE;
                                                break;
                                            default:
                                                chaineToken = sb.toString();
                                                token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                                break;
                                        }
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_32;
                                break;
                        }
                        break;
                    default:
                        chaineToken = "1";
                        token = GestionDate.TOKEN_NOMBRE_INF_13;
                        break;
                }
                break;
            case '2':
                sb.append('2');
                c = nextChar();
                switch (c) {
                    case '0':
                        sb.append('0');
                        c = nextChar();
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
                                c = nextChar();
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
                                        c = nextChar();
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
                                                do {
                                                    sb.append(c);
                                                    c = nextChar();
                                                } while (c != 0 && Character.isDigit(c));
                                                chaineToken = sb.toString();
                                                token = GestionDate.TOKEN_AUTRE;
                                                break;
                                            default:
                                                chaineToken = sb.toString();
                                                token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                                break;
                                        }
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_32;
                                break;
                        }
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
                        c = nextChar();
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
                                c = nextChar();
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
                                        do {
                                            sb.append(c);
                                            c = nextChar();
                                        } while (c != 0 && Character.isSpaceChar(c));
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_AUTRE;
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_32;
                                break;
                        }
                        break;
                    default:
                        chaineToken = "2";
                        token = GestionDate.TOKEN_NOMBRE_INF_13;
                        break;
                }
                break;
            case '3':
                sb.append('3');
                c = nextChar();
                switch (c) {
                    case '0':
                    case '1':
                        sb.append(c);
                        c = nextChar();
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
                                c = nextChar();
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
                                        do {
                                            sb.append(c);
                                            c = nextChar();
                                        } while (c != 0 && Character.isDigit(c));
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_32;
                                break;
                        }
                        break;
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        sb.append(c);
                        c = nextChar();
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
                                c = nextChar();
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
                                        do {
                                            sb.append(c);
                                            c = nextChar();
                                        } while (c != 0 && Character.isDigit(c));
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_AUTRE;
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                break;
                        }
                        break;
                    default:
                        chaineToken = "3";
                        token = GestionDate.TOKEN_NOMBRE_INF_13;
                        break;
                }
                break;
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                sb.append(c);
                c = nextChar();
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
                        c = nextChar();
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
                                c = nextChar();
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
                                        do {
                                            sb.append(c);
                                            c = nextChar();
                                        } while (c != 0 && Character.isDigit(c));
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_AUTRE;
                                        break;
                                    default:
                                        chaineToken = sb.toString();
                                        token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                        break;
                                }
                                break;
                            default:
                                chaineToken = sb.toString();
                                token = GestionDate.TOKEN_NOMBRE_INF_2100;
                                break;
                        }
                        break;
                    default:
                        chaineToken = sb.toString();
                        token = GestionDate.TOKEN_NOMBRE_INF_13;
                        break;
                }
                break;
        }
    }

    /**
     * Obtient le token suivant.
     * @param chaine chaine normalisée.
     * @return
     */
    public int nextToken() {
        indexToken = index-1;
        // avril aout
        // cinq
        // decembre deux dimanche dix douze
        // et
        // janvier jeudi juin juillet
        // fevrier
        // huit
        // lundi
        // mai mars mardi mercredi
        // neuf novembre
        // octobre onze
        // quatre quatorze quinze
        // samedi seize sept septembre six
        // treize trente trois
        // un
        // vendredi vingt 

        if (index >= chaine.length()) {
            token = GestionDate.TOKEN_FINCHAINE;
            return token;
        }

        while (c != 0 && (c == ' ' || c=='-' || c==',' || c=='\'')) {
            c = nextChar();
        }

        StringBuilder sb = new StringBuilder();

        switch (c) {
            case 0:
                token = GestionDate.TOKEN_FINCHAINE;
                chaineToken = "";
                break;
            default:
                checkAutre(sb);
                break;
            case 'A':
                sb.append('A');
                c = nextChar();
                switch (c) {
                    default:
                        checkAutre(sb);
                        break;
                    case 'V':
                        sb.append('V');
                        c = nextChar();
                        checkToken(sb, "AVRIL", 2, GestionDate.TOKEN_AVRIL);
                        break;
                    case 'O':
                        sb.append('O');
                        c = nextChar();
                        checkToken(sb, "AOUT", 2, GestionDate.TOKEN_AOUT);
                        break;
                }
                break;
            case 'C':
                sb.append('C');
                c = nextChar();
                checkToken(sb, "CINQ", 1, GestionDate.TOKEN_CINQ);
                break;
            case 'D':
                sb.append('D');
                c = nextChar();
                switch (c) {
                    case 'E':
                        sb.append('E');
                        c = nextChar();
                        switch (c) {
                            default:
                                checkAutre(sb);
                                break;
                            case 'C':
                                sb.append('C');
                                c = nextChar();
                                checkToken(sb, "DECEMBRE", 3, GestionDate.TOKEN_DECEMBRE);
                                break;
                            case 'U':
                                sb.append('U');
                                c = nextChar();
                                checkToken(sb, "DEUX", 3, GestionDate.TOKEN_DEUX);
                                break;
                        }
                        break;
                    case 'I':
                        sb.append('I');
                        c = nextChar();
                        switch (c) {
                            default:
                                checkAutre(sb);
                                break;
                            case 'X':
                                sb.append('X');
                                c = nextChar();
                                if (c == 0 || c == ' ') {
                                    token = GestionDate.TOKEN_DIX;
                                    chaineToken = "DIX";
                                } else {
                                    checkAutre(sb);
                                }
                                break;
                            case 'M':
                                sb.append('M');
                                c = nextChar();
                                checkToken(sb, "DIMANCHE", 3, GestionDate.TOKEN_DIMANCHE);
                                break;
                        }
                        break;
                    case 'O':
                        sb.append('O');
                        c = nextChar();
                        checkToken(sb, "DOUZE", 2, GestionDate.TOKEN_DOUZE);
                        break;
                    default:
                        checkAutre(sb);
                        break;
                }
                break;
            case 'E':
                sb.append('E');
                c = nextChar();
                checkToken(sb, "ET", 1, GestionDate.TOKEN_ET);
                break;
            case 'J':
                sb.append('J');
                c = nextChar();
                switch (c) {
                    default:
                        checkAutre(sb);
                        break;
                    case 'A':
                        sb.append('A');
                        c = nextChar();
                        checkToken(sb, "JANVIER", 2, GestionDate.TOKEN_JANVIER);
                        break;
                    case 'E':
                        sb.append('E');
                        c = nextChar();
                        checkToken(sb, "JEUDI", 2, GestionDate.TOKEN_JEUDI);
                        break;
                    case 'U':
                        sb.append('U');
                        c = nextChar();
                        if (c == 'I') {
                            sb.append('I');
                            c = nextChar();
                            switch (c) {
                                default:
                                    checkAutre(sb);
                                    break;
                                case 'N':
                                    sb.append('N');
                                    c = nextChar();
                                    if (c == 0 || c == ' ') {
                                        token = GestionDate.TOKEN_JUIN;
                                        chaineToken = "JUIN";
                                    } else {
                                        checkAutre(sb);
                                    }
                                    break;
                                case 'L':
                                    sb.append('L');
                                    c = nextChar();
                                    checkToken(sb, "JUILLET", 4, GestionDate.TOKEN_JUILLET);
                                    break;
                            }
                        } else {
                            checkAutre(sb);
                        }
                        break;
                }
                break;
            case 'F':
                sb.append('F');
                c = nextChar();
                checkToken(sb, "FEVRIER", 1, GestionDate.TOKEN_FEVRIER);
                break;
            case 'H':
                sb.append('H');
                c = nextChar();
                checkToken(sb, "HUIT", 1, GestionDate.TOKEN_HUIT);
                break;
            case 'L':
                sb.append('L');
                c = nextChar();
                checkToken(sb, "LUNDI", 1, GestionDate.TOKEN_LUNDI);
                break;
            case 'M':
                sb.append('M');
                c = nextChar();
                switch (c) {
                    default:
                        checkAutre(sb);
                        break;
                    case 'A':
                        sb.append('A');
                        c = nextChar();
                        switch (c) {
                            default:
                                checkAutre(sb);
                                break;
                            case 'R':
                                sb.append('R');
                                c = nextChar();
                                switch (c) {
                                    case 'S':
                                        sb.append('S');
                                        c = nextChar();
                                        if (c == 0 || c == ' ') {
                                            token = GestionDate.TOKEN_MARS;
                                            chaineToken = "MARS";
                                        } else {
                                            checkAutre(sb);
                                        }
                                        break;
                                    case 'D':
                                        sb.append('D');
                                        c = nextChar();
                                        checkToken(sb, "MARDI", 4, GestionDate.TOKEN_MARDI);
                                        break;
                                    default:
                                        checkAutre(sb);
                                        break;
                                }
                                break;
                            case 'I':
                                sb.append('I');
                                c = nextChar();
                                if (c == 0 || c == ' ') {
                                    token = GestionDate.TOKEN_MAI;
                                    chaineToken = "MAI";
                                } else {
                                    checkAutre(sb);
                                }
                                break;
                        }
                        break;
                    case 'E':
                        sb.append('E');
                        c = nextChar();
                        checkToken(sb, "MERCREDI", 2, GestionDate.TOKEN_MERCREDI);
                        break;
                }
                break;
            case 'N':
                sb.append('N');
                c = nextChar();
                switch (c) {
                    default:
                        checkAutre(sb);
                        break;
                    case 'E':
                        sb.append('E');
                        c = nextChar();
                        checkToken(sb, "NEUF", 2, GestionDate.TOKEN_NEUF);
                        break;
                    case 'O':
                        sb.append('O');
                        c = nextChar();
                        checkToken(sb, "NOVEMBRE", 2, GestionDate.TOKEN_NOVEMBRE);
                        break;
                }
                break;
            case 'O':
                sb.append('O');
                c = nextChar();
                switch (c) {
                    default:
                        checkAutre(sb);
                        break;
                    case 'C':
                        sb.append('C');
                        c = nextChar();
                        checkToken(sb, "OCTOBRE", 2, GestionDate.TOKEN_OCTOBRE);
                        break;
                    case 'N':
                        sb.append('N');
                        c = nextChar();
                        checkToken(sb, "ONZE", 2, GestionDate.TOKEN_ONZE);
                        break;
                }
                break;
            case 'Q':
                sb.append('Q');
                c = nextChar();
                if (c == 'U') {
                    sb.append('U');
                    c = nextChar();
                    switch (c) {
                        case 'A':
                            sb.append('A');
                            c = nextChar();
                            if (c == 'T') {
                                sb.append('T');
                                c = nextChar();
                                switch (c) {
                                    default:
                                        checkAutre(sb);
                                        break;
                                    case 'O':
                                        sb.append('O');
                                        c = nextChar();
                                        checkToken(sb, "QUATORZE", 5, GestionDate.TOKEN_QUATORZE);
                                        break;
                                    case 'R':
                                        sb.append('R');
                                        c = nextChar();
                                        checkToken(sb, "QUATRE", 5, GestionDate.TOKEN_QUATRE);
                                        break;
                                }
                            } else {
                                checkAutre(sb);
                            }
                            break;
                        case 'I':
                            sb.append('I');
                            c = nextChar();
                            checkToken(sb, "QUINZE", 2, GestionDate.TOKEN_QUINZE);
                            break;
                        default:
                            checkAutre(sb);
                            break;
                    }
                } else {
                    checkAutre(sb);
                }
                break;
            case 'S':
                sb.append('S');
                c = nextChar();
                switch (c) {
                    case 'A':
                        sb.append('A');
                        c = nextChar();
                        checkToken(sb, "SAMEDI", 2, GestionDate.TOKEN_SAMEDI);
                        break;
                    case 'I':
                        sb.append('I');
                        c = nextChar();
                        checkToken(sb, "SIX", 2, GestionDate.TOKEN_SIX);
                        break;
                    case 'E':
                        sb.append('E');
                        c = nextChar();
                        switch (c) {
                            case 'I':
                                sb.append('I');
                                c = nextChar();
                                checkToken(sb, "SEIZE", 3, GestionDate.TOKEN_SEIZE);
                                break;
                            case 'P':
                                sb.append('P');
                                c = nextChar();
                                if (checkFirst(sb, "SEPT", 3))
                                {
                                    if (c == 0 || c == ' ')
                                    {
                                        token = GestionDate.TOKEN_SEPT;
                                        chaineToken = "SEPT";
                                    }
                                    else
                                    {
                                        checkToken(sb, "SEPTEMBRE", 4, GestionDate.TOKEN_SEPTEMBRE);
                                    }
                                }
                                else
                                {
                                    checkAutre(sb);
                                }
                                break;
                            default:
                                checkAutre(sb);
                                break;
                        }
                        break;
                    default:
                        checkAutre(sb);
                        break;
                }
                break;
            case 'T':
                sb.append('T');
                c = nextChar();
                if (c == 'R') {
                    sb.append('R');
                    c = nextChar();
                    switch (c) {
                        case 'O':
                            sb.append('O');
                            c = nextChar();
                            checkToken(sb, "TROIS", 3, GestionDate.TOKEN_TROIS);
                            break;
                        case 'E':
                            sb.append('E');
                            c = nextChar();
                            switch (c) {
                                case 'I':
                                    sb.append('I');
                                    c = nextChar();
                                    checkToken(sb, "TREIZE", 4, GestionDate.TOKEN_TREIZE);
                                    break;
                                case 'N':
                                    sb.append('N');
                                    c = nextChar();
                                    checkToken(sb, "TRENTE", 4, GestionDate.TOKEN_TRENTE);
                                    break;
                                default:
                                    checkAutre(sb);
                                    break;
                            }
                            break;
                        default:
                            checkAutre(sb);
                            break;
                    }
                } else {
                    checkAutre(sb);
                }
                break;
            case 'U':
                sb.append('U');
                c = nextChar();
                checkToken(sb, "UN", 1, GestionDate.TOKEN_UN);
                break;
            case 'V':
                sb.append('V');
                c = nextChar();
                switch (c) {
                    case 'E':
                        sb.append('E');
                        c = nextChar();
                        checkToken(sb, "VENDREDI", 2, GestionDate.TOKEN_VENDREDI);
                        break;
                    case 'I':
                        sb.append('I');
                        c = nextChar();
                        checkToken(sb, "VINGT", 2, GestionDate.TOKEN_VINGT);
                        break;
                    default:
                        checkAutre(sb);
                        break;
                }
                break;
            case '0':
                checkNumber(sb);
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
                checkNumber2(sb);
                break;
        }
        return token;
    }
    
    private static void test(GestionDate gd,String s)
    {
       System.out.println("Trouve la date de "+s);
       
       RefDate rd =  gd.trouveDate(s, 0);
       
       System.out.println("Jour : "+rd.obtientNomDuJour());
       System.out.println("Jour : "+rd.obtientJourDuMois());
       System.out.println("Jour Normal : "+rd.obtientJourDuMoisNormal());
       System.out.println("Mois : "+rd.obtientNomDuMois());
       System.out.println("Ann�e : "+rd.obtientAnnee());
       System.out.println("\r\n");
    }
    
    private static int testMois(String mois,boolean attendu)
    {
        System.out.println(mois+" est-il un mois? ");
        boolean res = GestionDate.estUnMois(mois);
        System.out.println(res?"oui":"non");
        if (res!=attendu)
        {
            System.out.println("Erreur");
            return 1;
        }
        return 0;
    }
    
    /**
     * Tests de la classe.
     * @param args
     */
    public static void main(String[] args)
    {
       GestionDate gd = new GestionDate();
       
       test(gd,"LUNDI 10 MAI 2005");
       test(gd,"MARDI DIX JANVIER 005");
       test(gd,"MERCREDI DIX NEUF FEVRIER 1005");
       test(gd,"JEUDI VINGT DEUX MARS 1955");
       test(gd,"VENDREDI VINGT ET UN AVRIL 2099");
       test(gd,"SAMEDI 31 MAI 1200");
       test(gd,"DIMANCHE TRENTE ET UN JUIN 12");
       test(gd,"DIX HUIT JUILLET");
       test(gd,"7 AOUT 18");
       test(gd,"LUNDI 17 SEPTEMBRE 1900");
       test(gd,"MARDI 17 OCTOBRE 1901");
       test(gd,"LUNDI LUNDI 17 NOVEMBRE 1900");
       test(gd,"LUNDI 32 DIMANCHE 17 DECEMBRE 0001900");
       test(gd,"JEUDI 25 12 1980");
       
       int erreurs = 0;
       erreurs += testMois("JANVIER",true);
       erreurs += testMois("JANVIE",false);
       erreurs += testMois("FEVRIER",true);
       erreurs += testMois("FEVRIERE",false);
       erreurs += testMois("MARS",true);
       erreurs += testMois("BARS",false);
       erreurs += testMois("AVRIL",true);
       erreurs += testMois("MAI",true);
       erreurs += testMois("JUIN",true);
       erreurs += testMois("JUILLET",true);
       erreurs += testMois("AOUT",true);
       erreurs += testMois("SEPTEMBRE",true);
       erreurs += testMois("OCTOBRE",true);
       erreurs += testMois("NOVEMBRE",true);
       erreurs += testMois("DECEMBRE",true);
       
       if (erreurs>0) System.out.println(erreurs+" erreur(s).");
    }
}