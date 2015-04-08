/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.mots;

import ppol.jdonref.JDONREFParams;

/**
 *
 * @author marcanhe
 */
public class Levenstein {
    // Julien Moquet (+ Willy Aroche)
    // 27/11/2007 - test de procédure stockée en C pour postgreSQL
    // 10/04/2008 - adaptation pour la version 8.0
    // 29/10/2008 - livraison v2.1
    // 28/12/2009 - les notes sont uniformes d'une méthode à l'autre
    //              compilation avec le compilateur microsoft visual C++ 2008 Express Edition
    //              NB: pg_config.h modifié : la ligne "#define HAVE_STRINGS_H 1" a été commentée (strings.h indisponible).
    //              NB: pg_config.h modifié : la ligne "#define ENABLE_NLS 1" a été commentée (libintl.h indisponible).
    //              Avertissement à la compilation concernant PG_MODULE_MAGIC.
    // 30/12/2009 - une faute d'orthographe vaut moins que deux fautes d'orthographe
    // 23/02/2011 - utilisation de palloc
    // 01/04/2011 - compatibilité 8.3 ok
    // 12/03/2012 - correction de l'arrondi sur le seuil de fautes
    static final int VERYMAX = 3000;
    static final int MAX_CHARACTERS = 250;
    static final int MAX_WORDS_COUNT = 15;
    static final int MAX_FAUTES = 5;// le nombre maximal de fautes pour lequel un pourcentage peut encore être appliqué (voir definitFautes)
    static final int MALUS_TRANSPOSITION = 2;
    static final int MALUS_AJOUT_MOT = 4;
    static final int MALUS_SUPPRESSION_MOT = 0;
    int maluspasdemot = 4;
    int maluspasdemotdirecteur = 7;
    int notecodepostal = 50;
    int notecommune = 50;
    int notedeterminant = 2;
    int notelibelle = 50;
    int notetypedevoie = 28;
    int notenumero = 10;
    int notearrondissement = 10;
    int pourcentagecodepostal = 40;
    int pourcentagecommune = 80;
    int pourcentagedeterminant = 65;
    int pourcentagelibelle = 60;
    int pourcentagetypedevoie = 60;
    int taille_abbreviation_minimale = 2;

    public Levenstein() {
    }

    public Levenstein(JDONREFParams params) {
        maluspasdemot = params.obtientMalusPasDeMot();
        maluspasdemotdirecteur = params.obtientMalusPasDeMotDirecteur();
        notecodepostal = params.obtientNotePourCodePostal();
        notecommune = params.obtientNotePourCommune();
        notedeterminant = params.obtientNotePourMotDeterminant();
        notelibelle = params.obtientNotePourLibelle();
        notetypedevoie = params.obtientNotePourTypeDeVoie();
        notenumero = params.obtientNotePourNumero();
        notearrondissement = params.obtientNotePourArrondissement();
        pourcentagecodepostal = params.obtientPourcentageDeCorrespondanceDeCodePostal();
        pourcentagecommune = params.obtientPourcentageDeCorrespondanceDeCommune();
        pourcentagedeterminant = params.obtientPourcentageDeCorrespondanceDeMotDeterminant();
        pourcentagelibelle = params.obtientPourcentageDeCorrespondanceDeLibelle();
        pourcentagetypedevoie = params.obtientPourcentageDeCorrespondanceDeTypeDeVoie();
        taille_abbreviation_minimale = params.obtientTailleMinimaleDAbbreviation();
    }

// Enregistre les valeurs des notes
// int codepostal,int commune,int determinant,int libelle,int typedevoie,int numero,int arrondissement
    public void definitNotes(int i0, int i1, int i2, int i3, int i4, int i5, int i6) {
        notecodepostal = i0;
        notecommune = i1;
        notedeterminant = i2;
        notelibelle = i3;
        notetypedevoie = i4;
        notenumero = i5;
        notearrondissement = i6;
    }


// Enregistre les valeurs des pourcentages
// int codepostal,int commune,int determinant,int libelle,int typedevoie
    public void definitPourcentages(int i0, int i1, int i2, int i3, int i4) {
        pourcentagecodepostal = i0;
        pourcentagecommune = i1;
        pourcentagedeterminant = i2;
        pourcentagelibelle = i3;
        pourcentagetypedevoie = i4;
    }

// Enregistre les valeurs des malus
// int mot,int motdirecteur
    public void definitMalus(int i0, int i1) {
        maluspasdemot = i0;
        maluspasdemotdirecteur = i1;
    }

// Enregistre divers paramètres.
// int nv_taille_abbr
    public void definitDivers(int i0) {
        taille_abbreviation_minimale = i0;
    }

// 
    int mymin(int val0, int val1, int val2) {
        if (val0 <= val1) {
            if (val0 <= val2) {
                return val0;
            } else {
                return val2;
            }
        } else {
            if (val1 <= val2) {
                return val1;
            } else {
                return val2;
            }
        }
    }

// EXPERIMENTAL
// Retourne le nombre de mots trouvés dans la chaine.
// La chaine doit être normalisée.
// String arg1
    int nombre_de_mots(String s0) {
        int i, d1, mots, state;

        // Découpe arg1 en mots
        d1 = s0.length();

        if (d1 == 0) {
            return 0;
        }

        state = mots = 0;

        for (i = 0; i < d1; i++) {
            char c = s0.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                        case '\t':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case ' ':
                        case '\t':
                            mots++;
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                mots++;
                break;
        }

        return mots;
    }

// EXPERIMENTAL
// Retourne le nombre de mots trouvés dans la chaine.
// La chaine doit être normalisée.
    int nombre_de_mots2(String arg1) {
        int i, d1, mots, state;

        // Découpe arg1 en mots
        d1 = arg1.length();

        if (d1 == 0) {
            return 0;
        }

        state = mots = 0;

        for (i = 0; i < d1; i++) {
            char c = arg1.charAt(i);
            switch (state) {
                case 0:
                    switch (c) {
                        case ' ':
                        case '\t':
                            break;
                        default:
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case ' ':
                        case '\t':
                            mots++;
                            state = 0;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case 1:
                mots++;
                break;
        }

        return mots;
    }

// Retourne si arg1 est une abbréviation de arg2
// arg1 est une abbréviation de arg2 lorsque:
//   1. leur initiale est la même
//   2. arg1 = arg2 privé de certaines lettres
// String arg1,String arg2
    int estAbbreviation(String arg1, String arg2) {
        int i, j, l1, l2;

        l1 = arg1.length();
        l2 = arg2.length();

        if (l1 == 0) {
            return 0;
        }
        if (l2 == 0) {
            return 0;
        }

        //if (arg1.charAt(0) != arg2.charAt(0))
        //return false;

        for (i = 1, j = 1; i < l1; i++) {
            char c = arg1.charAt(i);

            while ((j < l2) && (arg2.charAt(j) != c)) {
                j++;
            }

            if (j == l2) {
                return 0;
            }

            j++;
        }

        return 1;
    }

//
// Retourne la distance de levenshtein entre arg1 et arg2
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
// ou en cas de problème d'allocation mémoire
//
// String arg1,String arg2
//
// return int
    int distance_levenstein(String arg1, String arg2) {
        int i, j, offset;
        int w, h, res;
        int[] ptest;

        //w = arg2.length()+1;
        //h = arg1.length()+1;
        w = arg2.length();
        h = arg1.length();

        if (h == 1) {
            return w - 1;
        }
        if (w == 1) {
            return h - 1;
        }

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        //ptest = (int*)palloc(sizeof(int)*(h+1)*(w+1));
        ptest = new int[(h + 1) * (w + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        for (j = 0; j < w; j++) {
            ptest[j] = j;
        }

        offset = w + 2;

        // Pour chaque ligne, 
        for (i = 1; i < h; i++, offset++) {
            char c;
            //c = arg1.charAt(i-1);
            c = arg1.charAt(i - 1);
            //ptest[offset-1] = i;
            ptest[offset - 1] = i;

            // Pour chaque colonne,
            for (j = 1; j < w; j++, offset++) {
                int count;

                //if (c==arg2.charAt(j-1))
                if (c == arg2.charAt(j - 1)) {
                    count = 0;
                } else {
                    count = 1;
                }

                offset = j + i * w;
                /*
                ptest[offset] = mymin (ptest[offset-w)+1,
                ptest[offset-1]+1,
                ptest[offset-1-w)+count);
                 */
                ptest[offset] = mymin(ptest[offset - w] + 1, ptest[offset - 1] + 1, ptest[offset - 1 - w] + count);
            }
        }

        //res = ptest[offset-2);
        res = ptest[offset - 2];
        //

        // récupère l'élément de la dernière ligne et de la dernière colonne.
        return res;
    }

//
// Retourne la distance de levenshtein entre arg1 et arg2
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
// ou en cas de problème d'allocation mémoire
    int distance_levenstein2(char[] arg1, char[] arg2) {
        int i, j, offset;
        int w, h, res;
        int[] ptest;

        //w = size2(arg2)+1;
        //h = size2(arg1)+1;

        w = arg2.length;
        h = arg1.length;

        if (h == 1) {
            return w - 1;
        }
        if (w == 1) {
            return h - 1;
        }

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        //ptest = (int*)palloc(sizeof(int)*(h+1)*(w+1));
        ptest = new int[(h + 1) * (w + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        for (j = 0; j < w; j++) {
            ptest[j] = j;
        }

        offset = w + 2;

        // Pour chaque ligne, 
        for (i = 1; i < h; i++, offset++) {
            char c;
            //c = *(arg1+i-1);
            c = arg1[i - 1];
            //ptest[offset-1] = i;
            ptest[offset - 1] = i;

            // Pour chaque colonne,
            for (j = 1; j < w; j++, offset++) {
                int count;

                //if (c==*(arg2+j-1))
                if (c == arg2[j - 1]) {
                    count = 0;
                } else {
                    count = 1;
                }

                offset = j + i * w;

                /*
                ptest[offset] = mymin (ptest[offset-w)+1,
                ptest[offset-1]+1,
                ptest[offset-1-w)+count);
                 */
                ptest[offset] = mymin(ptest[offset - w] + 1, ptest[offset - 1] + 1, ptest[offset - 1 - w] + count);
            }
        }

        // récupère l'élément de la dernière ligne et de la dernière colonne.
        //res = ptest[offset-2);
        res = ptest[offset - 2];
        //

        return res;
    }

//
// Retourne la distance de levenshtein entre arg1 et arg2
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
// ou en cas de problème d'allocation mémoire
    int distance_levenstein3(String arg1, String arg2) {
        int i, j, offset;
        int w, h, res;
        int[] ptest;

        //w = arg2.length()+1;
        //h = arg1.length()+1;
        w = arg2.length();
        h = arg1.length();

        if (h == 1) {
            return w - 1;
        }
        if (w == 1) {
            return h - 1;
        }

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        //ptest= new int[(w+1)*(h+1)];
        ptest = new int[(w + 1) * (h + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        for (j = 0; j < w; j++) {
            ptest[j] = j;
        }

        offset = w + 2;

        // Pour chaque ligne, 
        for (i = 1; i < h; i++, offset++) {
            char c;
            c = arg1.charAt(i - 1);
            ptest[offset - 1] = i;
            // Pour chaque colonne,
            for (j = 1; j < w; j++, offset++) {
                int count;

                if (c == arg2.charAt(j - 1)) {
                    count = 0;
                } else {
                    count = 1;
                }

                offset = j + i * w;

                /*
                ptest[offset] = mymin (ptest[offset-w)+1,
                ptest[offset-1]+1,
                ptest[offset-1-w)+count);
                 */
                ptest[offset] = mymin(ptest[offset - w] + 1, ptest[offset - 1] + 1, ptest[offset - 1 - w] + count);
            }
        }

        // récupère l'élément de la dernière ligne et de la dernière colonne.
        //res = ptest[offset-2);
        res = ptest[offset - 2];
        //

        return res;
    }

// Retourne la distance de levenstein entre arg1 et arg2
// Le mot arg2 est utilisé avec des jokers au début et à la fin
//
// arg1 et arg2 se terminent par un 0.
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS ou en
// cas de problème d'allocation mémoire.
    int distance_levenstein_joker(char[] arg1, char[] arg2) {
        int i, j, offset;
        int h, w;
        int min;
        int[] ptest;

        //h=arg1.length;
        //w=arg2.length;

        h = arg1.length;
        w = arg2.length;

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        ptest = new int[(w + 1) * (h + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        for (j = 0; j <= w; j++) {
            ptest[j] = j;
        }

        offset = w + 2;

        min = VERYMAX; // les valeurs suivantes seront forcemment plus petites.

        // Pour chaque ligne, 
        for (i = 1; i <= h; i++, offset++) {
            char c;
            int tempmin;
            c = arg1[i - 1];
            ptest[offset - 1] = 0; // La première colonne est à zéro
            // pour simuler le joker de départ sur arg2;

            // Pour chaque colonne,
            for (j = 1; j <= w; j++, offset++) {
                int count;

                if (c == arg2[j - 1]) {
                    count = 0;
                } else {
                    count = 1;
                }
                /*
                ptest[offset] = mymin (ptest[offset-(w+1)])+1,
                ptest[offset-1]+1,
                ptest[offset-1-(w+1)]+count);
                 */

                ptest[offset] = mymin(ptest[offset - (w + 1)] + 1, ptest[offset - 1] + 1, ptest[offset - 1 - (w + 1)] + count);
            }

            if ((tempmin = ptest[offset - 1]) < min) {
                min = tempmin;
            } // récupérer le plus petit élément de la dernière colonne
        // permet de simuler le joker final.
        }

        //

        // récupère le plus petit élément de la dernière colonne.
        return min;
    }

// Retourne la distance de levenstein entre arg1 et arg2
// Le mot arg2 est utilisé avec des jokers au début et à la fin
//
// arg1 et arg2 se terminent par un 0.
//
// Les fautes sur les espaces ne sont pas prises en compte.
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
    int distance_levenstein_joker_no_spaces_classic(char[] arg1, char[] arg2) {
        int i, j, offset;
        int h, w;
        int min;
        int[] ptest;

        h = arg1.length;
        w = arg2.length;

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        ptest = new int[(w + 1) * (h + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        // Les valeurs de départ sont modifiées
        // pour que les espaces soient ignorés
        i = 0;
        for (j = 0; j < w; j++) {
            if (arg2[j] != ' ') {
                if (j != 0) // if(j)
                {
                    ptest[j] = ++i;
                } else {
                    ptest[j] = i;
                }
            } else {
                ptest[j] = i;
            }
        }
        ptest[w] = w;

        offset = w + 2;

        min = VERYMAX; // les valeurs suivantes seront forcemment plus petites.

        // Pour chaque ligne, 
        for (i = 1; i <= h; i++, offset++) {
            char c;
            int tempmin;
            c = arg1[i - 1];
            ptest[offset - 1] = 0; // La première colonne est à zéro
            // pour simuler le joker de départ sur arg2;

            if (c == ' ') // les espaces sur la ligne sont ignorés
            {
                // recopie la ligne précédente
                for (j = 1; j <= w; j++, offset++) {
                    ptest[offset] = ptest[offset - (w + 1)];
                }
            } else // Pour chaque colonne,
            {
                for (j = 1; j <= w; j++, offset++) {
                    int cout;
                    char c2 = arg2[j - 1];

                    if (c2 == ' ') {
                        ptest[offset] = ptest[offset - 1];
                    } else {
                        if (c == c2) {
                            cout = 0;
                        } else {
                            cout = 1;
                        }

                        ptest[offset] = mymin(ptest[offset - (w + 1)] + 1,
                                ptest[offset - 1] + 1,
                                ptest[offset - 1 - (w + 1)] + cout);
                    }
                }
            }

            if ((tempmin = ptest[offset - 1]) < min) {
                min = tempmin;
            } // récupérer le plus petit élément de la dernière colonne
        // permet de simuler le joker final.
        }

        //

        // récupère le plus petit élément de la dernière colonne.
        return min;
    }

    // obtient la taille d'une chaine classique
    int size2(char[] arg1) {
        int i = 0;
        while (arg1[i] != 0) {
            i++;
        }
        return i;
    }

// Retourne la distance de levenstein entre arg1 et arg2
// Le mot arg2 est utilisé avec des jokers au début et à la fin, 
// à la limite de chaque mot de arg1
//
// arg1 et arg2 se terminent par un 0.
//
// Les fautes sur les espaces ne sont pas prises en compte.
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
    int distance_levenstein_joker_no_spaces(char[] arg1, char[] arg2) {
        int i, j, offset;
        int h, w;
        int min, premiere_colonne;

        int[] ptest;

        h = size2(arg1);
        w = size2(arg2);

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }


        ptest = new int[(h + 1) * (w + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        // Les valeurs de départ sont modifiées
        // pour que les espaces soient ignorés
        i = 0;
        for (j = 0; j < w; j++) {
            if (arg2[j] != ' ') {
                if (j != 0) // if(j)
                {
                    ptest[j] = ++i;
                } else {
                    ptest[j] = 0;
                }
            } else {
                ptest[j] = i;
            }
        }
        ptest[w] = w;

        offset = w + 2;

        min = VERYMAX; // les valeurs suivantes seront forcemment plus petites.

        // Pour chaque ligne, 
        for (i = 1, premiere_colonne = 0; i <= h; i++, offset++) {
            char c;
            int tempmin;
            c = arg1[i - 1];
            if (c == ' ') {
                ptest[offset - 1] = 0; // La première colonne est à zéro au niveau des espaces
            // pour simuler le joker initial à chaque mot
            } else {
                ptest[offset - 1] = ++premiere_colonne;
            }

            if (c == ' ') // les espaces sur la ligne sont ignorés
            {
                // recopie la ligne précédente
                for (j = 1; j <= w; j++, offset++) {
                    ptest[offset] = ptest[offset - (w + 1)];
                }
            } else // Pour chaque colonne,
            {
                for (j = 1; j <= w; j++, offset++) {
                    int cout;
                    char c2 = arg2[j - 1];

                    if (c2 == ' ') {
                        ptest[offset] = ptest[offset - 1];
                    } else {
                        if (c == c2) {
                            cout = 0;
                        } else {
                            cout = 1;
                        }

                        ptest[offset] = mymin(ptest[offset - (w + 1)] + 1,
                                ptest[offset - 1] + 1,
                                ptest[offset - 1 - (w + 1)] + cout);
                    }

                }
            }

            if ((i == h || arg1[i] == ' ') &&
                    (tempmin = ptest[offset - 1]) < min) {
                min = tempmin;
            } // récupérer le plus petit élément de la dernière colonne
        // permet de simuler le joker final en fin de mot.
        }

        //

        // récupère le plus petit élément de la dernière colonne.
        return min;
    }

// Retourne la position du mot arg2 dans arg1 avec une distance de levenstein maximale de pourcentagedecorrespondance.
// Le mot arg2 est utilisé avec des jokers au début et à la fin
//
// Le terme arg2 est recherché à partir du caractère start dans la chaine.
// Les fautes sur les espaces ne sont pas prises en compte.
//
// retourne 0 si les chaines dépassent MAX_CHARACTERS
// retourn 0 si la chaine n'est pas trouvé à pourcentagedecorrespondance près.
// retourne 1+fin_chaine+256*debut_chaine sinon.
//
// String arg1,String arg2,int pourcentagedecorrespondance,int start
// return int
    int position_levenstein_joker_classic(String arg1, String arg2, int i2, int i3) {
        int i, j, offset;
        int h, w;
        int min;
        int endindex;

        int pourcentagedecorrespondance, start;

        int[] ptest;


        pourcentagedecorrespondance = i2;
        start = i3;

        h = arg1.length();
        w = arg2.length();

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) //return 0;
        {
            return 0;
        }

        if (start >= h) {
            return 0;
        }

        ptest = new int[(w + 1) * (h + 1)];
        if (ptest == null) {
            return 0;
        }

        i = 0;
        for (j = 0; j <= w; j++) {
            ptest[j] = j;
        }

        offset = w + 2;

        min = VERYMAX; // Les valeurs suivantes seront forcemment plus petites.
        endindex = VERYMAX;

        // Pour chaque ligne,
        for (i = 1; i <= h - start; i++, offset++) {
            char c;
            int tempmin;
            c = arg1.charAt(i - 1 + start);
            ptest[offset - 1] = 0; // La première colonne est à zéro
            // pour simuler le joker de départ sur arg2;

            // Pour chaque colonne,
            for (j = 1; j <= w; j++, offset++) {
                int count;
                char c2 = arg2.charAt(j - 1);

                if (c == c2) {
                    count = 0;
                } else {
                    count = 1;
                }

                ptest[offset] = mymin(ptest[offset - (w + 1)] + 1,
                        ptest[offset - 1] + 1,
                        ptest[offset - 1 - (w + 1)] + count);
            }

            if ((tempmin = ptest[offset - 1]) < min) {
                min = tempmin; // Récupérer le plus petit élément de la dernière colonne
                // permet de simuler le joker final.
                endindex = i;
            }
        }

        // Si le nombre d'erreurs est satisfaisant,
        if (100 * (w - min) >= (pourcentagedecorrespondance * w)) {
            int startindex = endindex;
            //int j = w;
            j = w;

            // Cherche l'index de départ : c'est l'index du caractère qui commence une suite
            // de correspondance avec la chaine.
            // Pour cela, la suite des correspondances trouvées jusqu'au caractère final
            // est remontée, jusqu'à aboutir au premier caractère d'une des deux chaines.
            while (j != 1 && startindex != 1) {
                int dessus, cote, diag;
                min = ptest[j + startindex * (w + 1)];
                dessus = ptest[j - 1 + startindex * (w + 1)];
                cote = ptest[j + (startindex - 1) * (w + 1)];
                diag = ptest[j - 1 + (startindex - 1) * (w + 1)];

                // Le dessus est pris comme référence
                if (dessus <= diag) {
                    if (dessus <= cote) {
                        j--;
                    } else {
                        startindex--;
                    }
                } else // puis la diagonale.
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

            // Pour plus de lisibilité, des mots entiers sont recherchés.

            // Cherche le début du mot trouvé
            while (startindex > start && arg1.charAt(startindex - 1) != ' ') {
                startindex--;
            }

            // Cherche la fin du mot trouvé
            while (endindex < h - 1 && arg1.charAt(endindex + 1) != ' ') {
                endindex++;
            }

            //

            // Retourne la position
            return (1 + endindex + 256 * startindex);
        }
        //
        return 0;
    }

// Retourne la position du mot arg2 dans arg1 avec une distance de levenstein maximale de pourcentagedecorrespondance.
// Le mot arg2 est utilisé avec des jokers au début et à la fin, mais doit être précédé et suivi par le début de la chaine,
// la fin de la chaine, ou un espace.
//
// Le terme arg2 est recherché à partir du caractère start dans la chaine.
// Les fautes sur les espaces "à l'intérieur" de arg2 ne sont pas prises en compte.
//
// retourne 0 si les chaines dépassent MAX_CHARACTERS
// retourn 0 si la chaine n'est pas trouvé à pourcentagedecorrespondance près.
// retourne 1+fin_chaine+256*debut_chaine+65536*nb_fautes sinon.
//
// String arg1,String arg2,int pourcentagedecorrespondance,int start
// return int
    int position_levenstein_joker(String arg1, String arg2, int i2, int i3) {
        int i, j, offset;
        int nb_fautes = 0;
        int h, w;
        int min;
        int endindex;
        int premiere_colonne;
        int[] ptest;
        int pourcentagedecorrespondance, start;

        pourcentagedecorrespondance = i2;
        start = i3;

        h = arg1.length();
        w = arg2.length();

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return 0;
        }

        if (start >= h) {
            return 0;
        }

        ptest = new int[(w + 1) * (h + 1)];

        i = 0;
        for (j = 0; j <= w; j++) {
            ptest[j] = j;
        }

        offset = w + 2;

        min = VERYMAX; // Les valeurs suivantes seront forcemment plus petites.
        endindex = VERYMAX;

        // Pour chaque ligne,
        for (i = 1, premiere_colonne = 0; (i <= h - start) && (endindex == VERYMAX); i++, offset++) {
            char c;
            int tempmin;
            c = arg1.charAt(i - 1 + start);
            if (c == ' ') {
                ptest[offset - 1] = 0; // La première colonne est à zéro
            // pour simuler le joker de départ sur arg2;
            } else {
                ptest[offset - 1] = ++premiere_colonne;
            }

            // Pour chaque colonne,
            for (j = 1; j <= w; j++, offset++) {
                int count;
                char c2 = arg2.charAt(j - 1);

                if (c == c2) {
                    count = 0;
                } else {
                    count = 1;
                }

                ptest[offset] = mymin(ptest[offset - (w + 1)] + 1, ptest[offset - 1] + 1, ptest[offset - 1 - (w + 1)] + count);
            }

            if ((i == h - start || arg1.charAt(i + start) == ' ') &&
                    (100 * (w - (nb_fautes = ptest[offset - 1])) >= (pourcentagedecorrespondance * w))) {
                endindex = i;  // si un mot satisfaisant est trouvé, la recherche s'arrête.
            }
        }

        // Si le nombre d'erreurs est satisfaisant,
        if (endindex != VERYMAX) {
            int startindex;
            //int j;
            int stop = 0;

            // Cherche si les mots suivants ne permettent pas de compléter le mot recherché 
            for (; (i <= h - start) && stop > 0; i++, offset++) // !stop
            {
                char c;
                int tempmin;
                c = arg1.charAt(i - 1 + start);
                if (c == ' ') {
                    ptest[offset - 1] = 0; // La première colonne est à zéro
                // pour simuler le joker de départ sur arg2;
                } else {
                    ptest[offset - 1] = ++premiere_colonne;
                }

                // Pour chaque colonne,
                for (j = 1; j <= w; j++, offset++) {
                    int count;
                    char c2 = arg2.charAt(j - 1);

                    if (c == c2) {
                        count = 0;
                    } else {
                        count = 1;
                    }

                    ptest[offset] = mymin(ptest[offset - (w + 1)] + 1,
                            ptest[offset - 1] + 1,
                            ptest[offset - 1 - (w + 1)] + count);
                }

                if (i == h - start || arg1.charAt(i + start) == ' ') {
                    if (ptest[offset - 1] <= nb_fautes) {
                        nb_fautes = ptest[offset - 1];
                        endindex = i;  // ce mot suivant peut être ajouté.
                    } else {
                        stop = 1;
                    } // sinon, la recherche est terminée.
                }
            }

            j = w;
            startindex = endindex;

            // Cherche l'index de départ : c'est l'index du caractère qui commence une suite
            // de correspondance avec la chaine.
            // Pour cela, la suite des correspondances trouvées jusqu'au caractère final
            // est remontée, jusqu'à aboutir au premier caractère d'une des deux chaines.
            while (j != 1 && startindex != 1) {
                int dessus, cote, diag;
                min = ptest[j + startindex * (w + 1)];
                dessus = ptest[j - 1 + startindex * (w + 1)];
                cote = ptest[j + (startindex - 1) * (w + 1)];
                diag = ptest[j - 1 + (startindex - 1) * (w + 1)];

                // Le dessus est pris comme référence
                if (dessus <= diag) {
                    if (dessus <= cote) {
                        j--;
                    } else {
                        startindex--;
                    }
                } else // puis la diagonale.
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

            // Pour plus de lisibilité, des mots entiers sont recherchés.

            // Cherche le début du mot trouvé
            while (startindex > start && arg1.charAt(startindex - 1) != ' ') {
                startindex--;
            }

            // Cherche la fin du mot trouvé
            while (endindex < h - 1 && arg1.charAt(endindex + 1) != ' ') {
                endindex++;
            }

            //

            // Retourne la position
            return (1 + endindex + 256 * startindex + 65536 * nb_fautes);
        }
        //
        return 0;
    }

// Retourne le nombre de mots de arg1 contenus dans arg2 avec correspondanceparmot% de correspondance minimum
// arg1 est normalisé. Ses mots sont séparés par des espaces.
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
// String arg1,String arg2,int correspondanceparmot
// return int
    int contexte(String arg1, String arg2, int i2) {
        int count, d1, d2, i, j;
        char[] pmot1, pmot2;
        int correspondanceparmot;

        count = 0;
        correspondanceparmot = i2;

        // Check taille arg1 et arg2
        d1 = arg1.length();

        if (d1 > MAX_CHARACTERS) {
            return VERYMAX;
        }

        d2 = arg2.length();

        if (d2 > MAX_CHARACTERS) {
            return VERYMAX;
        }

        // Découpe arg1 en mots
        //pmot1 = new char[d1+2];
        pmot1 = new char[d1 + 2];
        if (pmot1 == null) {
            return VERYMAX;
        }

        for (i = 0; i < d1; i++) {
            char c = arg1.charAt(i);
            if (c == ' ') {
                pmot1[i] = 0;
            } else {
                pmot1[i] = c;
            }
        }
        pmot1[d1] = 0;
        pmot1[d1 + 1] = 0;

        // Découpe arg2 en mots
        //pmot2 = new char[d2+2];
        pmot2 = new char[d2 + 2];
        if (pmot2 == null) {
            //pfree(pmot1);
            return VERYMAX;
        }

        for (i = 0; i < d2; i++) {
            char c = arg2.charAt(i);
            if (c == ' ') {
                pmot2[i] = 0;
            } else {
                pmot2[i] = c;
            }
        }
        pmot2[d2] = 0;
        pmot2[d2 + 1] = 0;

        // Cherche chaque mot de arg1
        i = 0;
        while (pmot1[i] != ' ') // while pmot1[i]
        {
            int size = 0;
            int distance = VERYMAX;

            // dans chaque mot de arg2 : seul le plus proche est conservé.
            j = 0;
            while (pmot2[j] != ' ') // while pmot1[i]
            {
                //int temp_distance = distance_levenstein2(pmot2+j,pmot1+i);
                int temp_distance = distance_levenstein2(pmot2, pmot1);
                if (temp_distance < distance) {
                    distance = temp_distance;
                }

                while (pmot2[j] != ' ') {
                    j++;
                }
                j++;
            }

            while (pmot1[i] != ' ') {
                i++;
                size++;
            }
            i++;

            // produit en croix en nombre entiers
            if (100 * distance <= (100 - correspondanceparmot) * size) {
                count++;
            }
        }

        //pfree(pmot1);
        //pfree(pmot2); 

        return count;
    }

// Retourne la distance entre deux libelles
// Les mots de arg1 sont cherchés dans arg2.
// Les espaces sont ignorés.
// Si le mot directeur n'est pas trouvé, le malus est appliqué.
// Si aucun mot n'est troué, VERYMAX est retourné.
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
//
// String arg1,String arg2,int correspondanceparmot,int maluspasdemot,int maluspasdemotdirecteur
// return int
    int distance_libelle(String arg1, String arg2, int i2, int i3, int i4) {
        int distance_totale, d1, d2, i, mots, count;
        char[] pmot1, pmot2;
        int correspondanceparmot, maluspasdemot, maluspasdemotdirecteur;

        correspondanceparmot = i2;
        maluspasdemot = i3;
        maluspasdemotdirecteur = i4;

        distance_totale = mots = count = 0;

        // vérification vis à vis du seuil de taille de chaîne. 
        d1 = arg1.length();

        if (d1 > MAX_CHARACTERS) {
            return VERYMAX;
        }

        d2 = arg2.length();

        if (d2 > MAX_CHARACTERS) {
            return VERYMAX;
        }

        // découpe le mot arg1
        pmot1 = new char[d1 + 2];
        if (pmot1 == null) {
            return VERYMAX;
        }
        for (i = 0; i < d1; i++) {
            char c = arg1.charAt(i);
            if (c == ' ' || i == d1 - 1) {
                pmot1[i] = 0;
                mots++;
            } else {
                pmot1[i] = c;
            }
        }
        pmot1[d1] = 0;
        pmot1[d1 + 1] = 0;

        // copie arg2
        pmot2 = new char[d2 + 2];
        if (pmot2 == null) {
            //pfree(pmot1);
            return VERYMAX;
        }
        for (i = 0; i < d2; i++) {
            pmot2[i] = arg2.charAt(i);
        }
        pmot2[d2] = 0;

        // Cherche chaque mot de arg1 dans arg2
        i = 0;
        while (pmot1[i] != ' ') // pmot1[i]
        {
            int size = 0;
            //int distance = distance_levenstein_joker_no_spaces(pmot2,pmot1+i);
            int distance = distance_levenstein_joker_no_spaces(pmot2, pmot1);

            while (pmot1[i] != ' ') {
                i++;
                size++;
            }
            i++;

//    if (100*distance <= 100*size - size*correspondanceparmot) // calcul par pourcentage
            if (distance <= maluspasdemot) // calcul par distance absolue
            {
                distance_totale += distance;
                count++;
            } else {
                if (pmot1[i] != ' ') {
                    distance_totale += maluspasdemot;
                } else {
                    distance_totale += maluspasdemotdirecteur;
                }
            }
        }

        //pfree(pmot1);
        //pfree(pmot2);

        if (count == 0) {
            return VERYMAX;
        }

        return distance_totale;
    }

// EXPERIMENTAL
// Permet de savoir si deux chaines ont une abbréviation commune.
// C'est le cas si le résultat est inférieur ou égal à d1+d2-2*taille_abbréviation_minimale.
//
// Le calcul est effectué à partir d'un levenstein modifié tel que
// seuls les ajout et suppression soient autorisés.
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
//
// String arg1,String arg2,int taille_abbreviation_minimale
// return int
    int ontabbreviationcommune(String arg1, String arg2, int i2) {
        int i, j, offset;
        int w, h, res;
        int taille_abbreviation_minimale;
        int[] ptest;
        taille_abbreviation_minimale = i2;

        w = arg2.length() + 1;
        h = arg1.length() + 1;

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        if (w > 2 && h > 2) {
            if (arg1.charAt(0) != arg2.charAt(0)) {
                return 0;
            }
        }

        ptest = new int[(w + 1) * (h + 1)];
        if (ptest == null) {
            return VERYMAX;
        }

        for (j = 0; j < w; j++) {
            ptest[j] = j;
        }

        offset = arg2.length() + 2;

        // Pour chaque ligne, 
        for (i = 1; i < h; i++, offset++) {
            char c;
            c = arg1.charAt(i - 1);
            ptest[offset - 1] = i;

            // Pour chaque colonne,
            for (j = 1; j < w; j++, offset++) {
                int cout;

                if (c == arg2.charAt(j - 1)) {
                    cout = 0;
                } else {
                    cout = 200;
                }

                offset = j + i * w;

                ptest[offset] = mymin(ptest[offset - w] + 1,
                        ptest[offset - 1] + 1,
                        ptest[offset - 1 - w] + cout);

            }
        }

        if (ptest[offset - 2] <= w + h - 2 - 2 * taille_abbreviation_minimale) {
            res = 1;
        } else {
            res = 0;
        }

        //

        return res;
    }

// EXPERIMENTAL
//
// Calcule la distance entre deux types de voies.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne malus si le nombre d'erreur ne depasse pas le malus
// Sinon, retourne 0.
//
// String arg1,String arg2,int taille_abbreviation_minimale,int malus
// return int
    int distance_type_de_voie(String arg1, String arg2, int i2, int i3) {
        int w, h, d, pctg;
        int taille_abbreviation_minimale, malus;

        taille_abbreviation_minimale = i2;
        malus = i3;

        h = arg1.length();
        w = arg2.length();

        if (h < w) {
            if (estAbbreviationDe2(arg1, arg2, taille_abbreviation_minimale) != 0) {
                return malus;
            }
        } else if (w < h) {
            if (estAbbreviationDe2(arg2, arg1, taille_abbreviation_minimale) != 0) {
                return malus;
            }
        } else {
            if (estAbbreviationDe2(arg1, arg2, taille_abbreviation_minimale) != 0 || estAbbreviationDe2(arg2, arg1, taille_abbreviation_minimale) != 0) {
                return malus;
            }
        }

        return 0;
    }

// EXPERIMENTAL
//
// Calcule la distance entre deux types de voies.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne malus si le nombre d'erreur ne depasse pas le malus
// Sinon, retourne 0.
//
    int distance_type_de_voie2(String arg1, String arg2, int taille_abbreviation_minimale, int malus) {
        int w, h, d, pctg;

        h = arg1.length();
        w = arg2.length();

        if (h < w) {
            if (estAbbreviationDe2(arg1, arg2, taille_abbreviation_minimale) != 0) {
                return malus;
            }
        } else if (w < h) {
            if (estAbbreviationDe2(arg2, arg1, taille_abbreviation_minimale) != 0) {
                return malus;
            }
        } else {
            if (estAbbreviationDe2(arg1, arg2, taille_abbreviation_minimale) != 0 || estAbbreviationDe2(arg2, arg1, taille_abbreviation_minimale) != 0) {
                return malus;
            }
        }

        return (0);
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux types de voies.
// arg2 est la chaine de référence.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne 0.
//
//
// String arg1,String arg2,int note_max,int seuil,int taille_abbreviation_minimale
// return int
    public int note_type_de_voie(String arg1, String arg2, int i2, int i3, int i4) {
        int w, h, i;
        int note_max, seuil, taille_abbreviation_minimale;
        note_max = i2;
        seuil = i3;
        taille_abbreviation_minimale = i4;

        h = arg1.length();
        w = arg2.length();

        if (h == 0) {
            return 0;
        }
        if (w == 0) {
            return 0;
        }

        if (h < w) {
            if (estAbbreviationDe2(arg1, arg2, taille_abbreviation_minimale) != 0) {
                return note_max;
            }
            return 0;
        } else if (w < h) {
            if (estAbbreviationDe2(arg2, arg1, taille_abbreviation_minimale) != 0) {
                return note_max;
            }
            return 0;
        }
        // w=h
        for (i = 0; i < h; i++) {
            if (arg1.charAt(i) != arg2.charAt(i)) {
                return 0;
            }
        }
        return note_max;
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux types de voies.
// arg2 est la chaine de référence.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne 0.
//
    int note_type_de_voie2(String arg1, String arg2, int note_max, int seuil, int taille_abbreviation_minimale) {
        int w, h, i;
        h = arg1.length();
        w = arg2.length();

        if (h == 0) {
            return 0;
        }
        if (w == 0) {
            return 0;
        }

        if (h < w) {
            if (estAbbreviationDe2(arg1, arg2, taille_abbreviation_minimale) != 0) {
                return note_max;
            }
            return 0;
        } else if (w < h) {
            if (estAbbreviationDe2(arg2, arg1, taille_abbreviation_minimale) != 0) {
                return note_max;
            }
            return 0;
        }
        // w=h
        for (i = 0; i < h; i++) {
            if (arg1.charAt(i) != arg2.charAt(i)) {
                return 0;
            }
        }
        return note_max;
    }


// EXPERIMENTAL
// Calcule la note de correspondance entre deux mots selon un calcul par pourcentage et seuil
// arg2 est le mot de référence.
// Si le nombre d'erreur ne depasse pas le seuil de pourcentage toléré ou 5 fautes, 
// retourne un pourcentage de la note maximal fonction du nombre de fautes.
// Sinon, retourne 0.
// retourne 0 si les chaines dépassent MAX_CHARACTER.
//
// String arg1,String arg2,int note_max,int seuil
// return int
    public int note_pourcentage_seuil(String arg1, String arg2, int i2, int i3) {
        int d, pctg, w, h;
        int note_max, seuil;

        note_max = i2;
        seuil = i3;

        w = arg2.length();
        h = arg1.length();

        if (w == 0) {
            return 0;
        }
        if (h == 0) {
            return 0;
        }

        d = distance_levenstein3(arg1, arg2);

        if (d == VERYMAX || d > MAX_FAUTES) {
            return 0;
        }

        // WA 03/2012 : mauvaise gestion de l'arrondi
        //pctg = ((w-d)*100)/w;
        //if (pctg >= seuil)
        if ((w - d) >= nbMinCarsBons(w, seuil)) {
            if (d + 1 <= w / 2) {
                return (note_max - (note_max * d * d) / (w * (w - 1)));
            } else {
                return (note_max - (note_max * d) / w);
            }
        }
        return 0;
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux mots selon un calcul par pourcentage et seuil
// arg2 est le mot de référence.
// Si le nombre d'erreur ne depasse pas le seuil de pourcentage toléré ou 5 fautes, 
// retourne un pourcentage de la note maximal fonction du nombre de fautes.
// Sinon, retourne 0.
// retourne 0 si les chaines dépassent MAX_CHARACTER.
//
    int note_pourcentage_seuil2(String arg1, String arg2, int note_max, int seuil) {
        int d, pctg, w, h;

        w = arg2.length();
        h = arg1.length();

        if (w == 0) {
            return 0;
        }
        if (h == 0) {
            return 0;
        }

        d = distance_levenstein3(arg1, arg2);

        if (d == VERYMAX || d > MAX_FAUTES) {
            return 0;
        }

        // WA 03/2012 : mauvaise gestion de l'arrondi
        //pctg = ((w-d)*100)/w;
        //if (pctg >= seuil)
        if ((w - d) >= nbMinCarsBons(w, seuil)) {
            if (d + 1 <= w / 2) {
                return (note_max - (note_max * d * d) / (w * (w - 1)));
            } else {
                return (note_max - (note_max * d) / w);
            }
        }
        return 0;
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux chaines.
// Arg2 est la chaine de référence.
// Chaque mot dispose d'une note de note_max/nombre de mots.
// (les points restants sont donnés ex: 2 mots pour 3 points => 1 point donné).
// A chaque mot, la note note_pourcentage_seuil_joker est attribuée.
// Le total des notes est retourné.
// Si arg1 ou arg2 ont plus de MAX_CHARACTERS, retourne 0.
//
//
// String arg1,String arg2,int note_max,int seuil
// return int
    public int note_pourcentage_seuil_n(String arg1, String arg2, int i2, int i3) {
        int d1, d2, i, mots, noteparmot, bonus;
        double note;
        char[] pmot1, pmot2;
        int note_max, seuil;

        note_max = i2;
        seuil = i3;

        mots = 0;

        d1 = arg1.length();

        if (d1 > MAX_CHARACTERS) {
            return 0;
        }

        if (d1 == 0) {
            return 0;
        }

        // Découpe arg2 en mots
        d2 = arg2.length();

        if (d2 == 0) {
            return 0;
        }

        if (d2 > MAX_CHARACTERS) {
            return 0;
        }

        // découpe arg2 en mots
        pmot2 = new char[d2 + 2];
        if (pmot2 == null) {
            return 0;
        }
        for (i = 0; i < d2; i++) {
            char c = arg2.charAt(i);
            if (c == ' ' || i == d2 - 1) {
                pmot2[i] = 0;
                mots++;
            } else {
                pmot2[i] = c;
            }
        }
        pmot2[d2] = 0;
        pmot2[d2 + 1] = 0;

        // Copie arg1
        pmot1 = new char[d1 + 1];
        if (pmot1 == null) {
            //pfree(pmot2);
            return 0;
        }
        for (i = 0; i < d1; i++) {
            pmot1[i] = arg1.charAt(i);
        }
        pmot1[d1] = 0;

        noteparmot = note_max / mots;
        note = note_max % mots;

        // Cherche chaque mot de arg2 dans arg1
        i = 0;
        while (pmot2[i] != 0) {
            int pctg, size = 0;
            //int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);
            int distance = distance_levenstein_joker_no_spaces(pmot1, pmot2);

            while (pmot2[i] != 0) {
                i++;
                size++;
            }
            i++;

            if (distance <= MAX_FAUTES) {
                // WA 03/2012 : mauvaise gestion de l'arrondi
                //pctg = (100*(size-distance))/size;
                //if (pctg >= seuil)
                if ((size - distance) >= nbMinCarsBons(size, seuil)) {
                    // attenuation autour de 0 fautes
                    if (distance + 1 <= size / 2) {
                        note += noteparmot - ((double) (noteparmot * distance * distance)) / ((double) (size * (size - 1)));
                    } else {
                        note += noteparmot - (noteparmot * distance) / (size);
                    }
                }
            }
        }

        //pfree(pmot1);
        //pfree(pmot2);

        return ((int) note); // troncature double=>int
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux chaines.
// Arg2 est la chaine de référence.
// Chaque mot dispose d'une note de note_max/nombre de mots.
// (les points restants sont donnés ex: 2 mots pour 3 points => 1 point donné).
// A chaque mot, la note note_pourcentage_seuil_joker est attribuée.
// Le total des notes est retourné.
// Si arg1 ou arg2 ont plus de MAX_CHARACTERS, retourne 0.
//
    int note_pourcentage_seuil_n2(String arg1, String arg2, int note_max, int seuil) {
        int d1, d2, i, mots, noteparmot, bonus;
        double note;
        char[] pmot1, pmot2;

        mots = 0;

        d1 = arg1.length();

        if (d1 > MAX_CHARACTERS) {
            return 0;
        }

        if (d1 == 0) {
            return 0;
        }

        // Découpe arg2 en mots
        d2 = arg2.length();

        if (d2 == 0) {
            return 0;
        }

        if (d2 > MAX_CHARACTERS) {
            return 0;
        }

        // découpe arg2 en mots
        pmot2 = new char[d2 + 2];
        if (pmot2 == null) {
            return 0;
        }
        for (i = 0; i < d2; i++) {
            char c = arg2.charAt(i);
            if (c == ' ' || i == d2 - 1) {
                pmot2[i] = 0;
                mots++;
            } else {
                pmot2[i] = c;
            }
        }
        pmot2[d2] = 0;
        pmot2[d2 + 1] = 0;

        // Copie arg1
        pmot1 = new char[d1 + 1];
        if (pmot1 == null) {
            //pfree(pmot2);
            return 0;
        }
        for (i = 0; i < d1; i++) {
            pmot1[i] = arg1.charAt(i);
        }
        pmot1[d1] = 0;

        noteparmot = note_max / mots;
        note = note_max % mots;

        // Cherche chaque mot de arg2 dans arg1
        i = 0;
        while (pmot2[i] != 0) {
            int pctg, size = 0;
            //int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);
            int distance = distance_levenstein_joker_no_spaces(pmot1, pmot2);

            while (pmot2[i] != 0) {
                i++;
                size++;
            }
            i++;

            if (distance <= MAX_FAUTES) {
                // WA 03/2012 : mauvaise gestion de l'arrondi
                //pctg = (100*(size-distance))/size;
                //if (pctg >= seuil)
                if ((size - distance) >= nbMinCarsBons(size, seuil)) {
                    if (distance + 1 <= size / 2) {
                        note += noteparmot - ((double) (noteparmot * distance * distance)) / ((double) (size * (size - 1)));
                    } else {
                        note += noteparmot - (noteparmot * distance) / (size);
                    }
                }
            }
        }

        //pfree(pmot1);
        //pfree(pmot2);

        return ((int) note);
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux chaines.
// Chaque mot de arg2 est recherché dans arg1.
// arg1 est utilisé comme chaine de référence.
// Chaque mot dispose d'une note de note_par_mot.
// A chaque mot, la note issue de distance_levenstein_joker_no_spaces est attribuée.
// Le total des notes est retourné.
// Si arg1 ou arg2 ont plus de MAX_CHARACTERS, retourne 0.
//
//
// String arg1,String arg2,int noteparmot,int seuil
// return int
    public int note_pourcentage_seuil_total(String arg1, String arg2, int i2, int i3) {
        int d1, d2, i, bonus, note;
        int nbmots1, nbmots2;
        char[] pmot1, pmot2;
        int noteparmot, seuil;
        noteparmot = i2;
        seuil = i3;

        d1 = arg1.length();

        if (d1 > MAX_CHARACTERS) {
            return 0;
        }

        if (d1 == 0) {
            return 0;
        }

        d2 = arg2.length();

        if (d2 == 0) {
            return 0;
        }

        if (d2 > MAX_CHARACTERS) {
            return 0;
        }

        // découpe arg2 en mots
        pmot2 = new char[d2 + 2];
        if (pmot2 == null) {
            return 0;
        }
        for (i = 0, nbmots2 = 1; i < d2; i++) {
            char c = arg2.charAt(i);
            if (c == ' ') //  || i==d2-1)
            {
                nbmots2++;
                pmot2[i] = 0;
            } else {
                pmot2[i] = c;
            }
        }
        pmot2[d2] = 0;
        pmot2[d2 + 1] = 0;

        // Copie arg1
        pmot1 = new char[d1 + 1];
        if (pmot1 == null) {
            //pfree(pmot2);
            return 0;
        }
        for (i = 0; i < d1; i++) {
            pmot1[i] = arg1.charAt(i);
        }
        pmot1[d1] = 0;
        nbmots1 = nombre_de_mots2(arg1);

        note = 0;

        // Cherche chaque mot de arg2 dans arg1
        i = 0;
        while (pmot2[i] != 0) {
            int pctg, size = 0;
            //int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);
            int distance = distance_levenstein_joker_no_spaces(pmot1, pmot2);

            while (pmot2[i] != 0) {
                i++;
                size++;
            }
            i++;

            if (distance <= MAX_FAUTES) {
                // WA 03/2012 : mauvaise gestion de l'arrondi
                //pctg = (100*(size-distance))/size;
                //if (pctg >= seuil)
                if ((size - distance) >= nbMinCarsBons(size, seuil)) {
                    if (distance + 1 <= size / 2) {
                        note += noteparmot - ((double) (noteparmot * distance * distance)) / ((double) (size * (size - 1)));
                    } else {
                        note += noteparmot - (noteparmot * distance) / (size);
                    }

                }
            }
        }

        //pfree(pmot1);
        //pfree(pmot2);

        return (note - maluspasdemot * (nbmots1 > nbmots2 ? nbmots1 - nbmots2 : nbmots2 - nbmots1));
    }

// EXPERIMENTAL
// Calcule la note de correspondance entre deux chaines.
// Chaque mot de arg2 est recherché dans arg1.
// arg1 est utilisé comme chaine de référence.
// Chaque mot dispose d'une note de note_par_mot.
// A chaque mot, la note issue de distance_levenstein_joker_no_spaces est attribuée.
// Le total des notes est retourné.
// Si arg1 ou arg2 ont plus de MAX_CHARACTERS, retourne 0.
//
    int note_pourcentage_seuil_total2(String arg1, String arg2, int noteparmot, int seuil) {
        int d1, d2, i, bonus, note;
        int nbmots1, nbmots2;
        char[] pmot1, pmot2;

        d1 = arg1.length();

        if (d1 > MAX_CHARACTERS) {
            return 0;
        }

        if (d1 == 0) {
            return 0;
        }

        d2 = arg2.length();

        if (d2 == 0) {
            return 0;
        }

        if (d2 > MAX_CHARACTERS) {
            return 0;
        }

        // découpe arg2 en mots
        pmot2 = new char[d2 + 2];
        if (pmot2 == null) {
            return 0;
        }
        for (i = 0, nbmots2 = 1; i < d2; i++) {
            char c = arg2.charAt(i);
            if (c == ' ') //  || i==d2-1)
            {
                nbmots2++;
                pmot2[i] = 0;
            } else {
                pmot2[i] = c;
            }
        }
        pmot2[d2] = 0;
        pmot2[d2 + 1] = 0;

        // Copie arg1
        pmot1 = new char[d1 + 1];
        if (pmot1 == null) {
            //pfree(pmot2);
            return 0;
        }
        for (i = 0; i < d1; i++) {
            pmot1[i] = arg1.charAt(i);
        }
        pmot1[d1] = 0;
        nbmots1 = nombre_de_mots2(arg1);

        note = 0;

        // Cherche chaque mot de arg2 dans arg1
        i = 0;
        while (pmot2[i] != 0) {
            int pctg, size = 0;
            //int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);
            final char[] pmot2SubArray = new char[pmot2.length - i];
            for (int j = 0; j < pmot2SubArray.length; j++) {
                pmot2SubArray[j] = pmot2[i + j];
            }
            int distance = distance_levenstein_joker_no_spaces(pmot1, pmot2SubArray);

            while (pmot2[i] != 0) {
                i++;
                size++;
            }
            i++;

            if (distance <= MAX_FAUTES) {
                // WA 03/2012 : mauvaise gestion de l'arrondi
                //pctg = (100*(size-distance))/size;
                //if (pctg >= seuil)
                if ((size - distance) >= nbMinCarsBons(size, seuil)) {
                    if (distance + 1 <= size / 2) {
                        note += noteparmot - ((double) (noteparmot * distance * distance)) / ((double) (size * (size - 1)));
                    } else {
                        note += noteparmot - (noteparmot * distance) / (size);
                    }
                }
            }
        }

        //pfree(pmot1);
        //pfree(pmot2);

        return (note - maluspasdemot * (nbmots1 > nbmots2 ? nbmots1 - nbmots2 : nbmots2 - nbmots1));
    }

// Retourne le nombre minimal de caracteres corrects devant etre presents dans une chaine de longueur size 
// pour etre au dessus du seuil seuilPercent en pourcents.
    int nbMinCarsBons(int size, int seuilPercent) {
        double nbCarsBonsMin = (double) ((seuilPercent * size) / 100);
        return (int) (nbCarsBonsMin + 0.5);
    }

// Retourne 1 si chaine2 termine chaine1.
// Retourne 0 sinon.
//
//
// String chaine1, String chaine2
// return int
    int seterminepar(String chaine1, String chaine2) {
        int d1, d2, i, j;

        d2 = chaine2.length();
        if (d2 == 0) {
            return 1;
        }

        d1 = chaine1.length();
        if (d2 > d1) {
            return 0;
        }

        for (i = d1 - d2, j = 0; i < d1; i++, j++) {
            if (chaine1.charAt(i) != chaine2.charAt(j)) {
                return 0;
            }
        }

        return 1;
    }

// Retourne 1 si chaine2 termine chaine1.
// Retourne 0 sinon.
//
    int seterminepar2(String chaine1, String chaine2) {
        int d1, d2, i, j;

        d2 = chaine2.length();
        if (d2 == 0) {
            return 1;
        }

        d1 = chaine1.length();
        if (d2 > d1) {
            return 0;
        }

        for (i = d1 - d2, j = 0; i < d1; i++, j++) {
            if (chaine1.charAt(i) != chaine2.charAt(j)) {
                return 0;
            }
        }

        return 1;
    }

// Obtient la note de l'arrondissement
// si la référence est un arrondissement alors refestarrondissement vaut 0 et notearrondissement est retourné
//
// a utiliser de la sorte : note_arrondissement('15','75015',code_insee_commune is null);
//
//
// String arrondissement, String codepostalreference, char refestarrondissement
// return int
    public int note_arrondissement(String s0, String s1, boolean b2) {
        String arrondissement, codepostalreference;
        boolean refestarrondissement;

        arrondissement = s0;
        codepostalreference = s1;
        refestarrondissement = b2;

        if (arrondissement.length() == 0) {
            return notearrondissement;
        } // VARSIZE
        if (codepostalreference.length() < 5) {
            return notearrondissement;
        } // VARSIZE
        if (refestarrondissement) {
            return notearrondissement;
        }
        if (codepostalreference.length() == 0) {
            return 0;
        } // VARSIZE
        if (seterminepar2(codepostalreference, arrondissement) != 0) {
            return notearrondissement;
        }

        return 0;
    }

// Obtient la note de l'arrondissement
// si la référence est un arrondissement alors refestarrondissement vaut 0 et notearrondissement est retourné
//
// a utiliser de la sorte : note_arrondissement('15','75015',code_insee_commune is null);
//
    int note_arrondissement2(String arrondissement, String codepostalreference, boolean refestarrondissement) {
        if (arrondissement.length() == 0) {
            return notearrondissement;
        } // VARSIZE
        if (codepostalreference.length() < 5) {
            return notearrondissement;
        } // VARSIZE
        if (refestarrondissement)  {
            return notearrondissement;
        }
        if (codepostalreference.length() == 0) {
            return 0;
        } // VARSIZE
        if (seterminepar2(codepostalreference, arrondissement) != 0) {
            return notearrondissement;
        }

        return (0);
    }


// Retourne vrai si arg1 est une abbreviation de arg2
//
// String arg1,String arg2,int  taille_abbreviation_minimale
// return int / BOOLEAN
    int estAbbreviationDe(String arg1, String arg2, int i2) {
        int w, h, i, j;
        int taille_abbreviation_minimale;
        taille_abbreviation_minimale = i2;

        h = arg1.length();
        w = arg2.length();

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return 0;
        }

        if (h == 0) {
            return 0;
        }
        if (w == 0) {
            return 0;
        }

        if (h < taille_abbreviation_minimale) {
            return 0;
        }

        if (w < taille_abbreviation_minimale) {
            return 0;
        }

        if (arg1.charAt(0) != arg2.charAt(0)) {
            return 0;
        }

        j = 1;

        for (i = 1; i < h; i++) {
            char c = arg1.charAt(i);
            for (j = 1; j < w; j++) {
                if (c == arg2.charAt(j)) {
                    break;
                }
            }
            if (j == w) {
                return 0;
            }
        }

        return 1;
    }

// Retourne vrai si arg1 est une abbreviation de arg2
//
// return int / BOOLEAN
    int estAbbreviationDe2(String arg1, String arg2, int taille_abbreviation_minimale) {
        int w, h, i, j;

        h = arg1.length();
        w = arg2.length();

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return 0;
        }

        if (h == 0) {
            return 0;
        }
        if (w == 0) {
            return 0;
        }

        if (h < taille_abbreviation_minimale) {
            return 0;
        }

        if (w < taille_abbreviation_minimale) {
            return 0;
        }

        if (arg1.charAt(0) != arg2.charAt(0)) {
            return 0;
        }

        j = 1;

        for (i = 1; i < h; i++) {
            char c = arg1.charAt(i);
            for (j = 1; j < w; j++) {
                if (c == arg2.charAt(j)) {
                    break;
                }
            }
            if (j == w) {
                return 0;
            }
        }

        return 1;
    }

// EXPERIMENTAL
// Permet de savoir si les chaines ont une abbréviation commune de type suspension
// Une suspension est telle que seules les premières lettres sont conservées.
// Le paramètres taille_min indique le nombre de lettres conservées.
//
// String arg1,String arg2,int taille_min
// return int / BOOLEAN
    int estSuspension(String arg1, String arg2, int i2) {
        int i;
        int w, h;
        int taille_min;
        taille_min = i2;

        w = arg2.length();
        h = arg1.length();

        if (w < taille_min || h < taille_min) {
            return 0;
        }

        for (i = 0; i < taille_min; i++) {
            if (arg1.charAt(i) != arg2.charAt(i)) {
                return 0;
            }
        }
        return 1;
    }

// EXPERIMENTAL
// Permet de savoir si les chaines ont une abbréviation commune de type lettre suscrite
// Une lettre suscrite est telle que seules les premières lettres sont conservées ainsi que la dernière.
// Le paramètres taille_min indique le nombre de lettres de l'abbréviation au minimum.
//
// String arg1,String arg2,int taille_min
// return int / BOOLEAN
    int estLettreSuscrite(String arg1, String arg2, int i2) {
        int i;
        int w, h;
        int taille_min;
        taille_min = i2;

        w = arg2.length();
        h = arg1.length();

        if (w < taille_min || h < taille_min) {
            return 0;
        }

        for (i = 0; i < taille_min - 1; i++) {
            if (arg1.charAt(i) != arg2.charAt(i)) {
                return 0;
            }
        }

        return (arg1.charAt(h - 1) == arg2.charAt(w - 1)) ? 1 : 0;
    }

// EXPERIMENTAL
// Permet de savoir si les chaines ont une abbréviation commune de type contraction
// Une contraction est telle que la première, la dernière, et des lettres intermédiaires sont conservées.
// Le paramètres taille_min indique le nombre de lettres conservées dans l'abbréviation au minimum.
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
// String arg1,String arg2,int taille_min
// return int
    int estContraction(String arg1, String arg2, int i2) {
        int i, j, offset;
        int w, h, res;
        int taille_min;
        int[] ptest;

        taille_min = i2;

        w = arg2.length() + 1;
        h = arg1.length() + 1;

        if (w > MAX_CHARACTERS || h > MAX_CHARACTERS) {
            return VERYMAX;
        }

        if (w <= taille_min || h <= taille_min) {
            return 0;
        }

        if (w > 2 && h > 2) {
            if (arg1.charAt(0) != arg2.charAt(0)) {
                return 0;
            }
        }

        ptest = new int[w + 1];

        for (j = 0; j < w; j++) {
            ptest[j] = j;
        }

        offset = arg2.length() + 2; // VARSIZE

        // Pour chaque ligne, 
        for (i = 1; i < h; i++, offset++) {
            char c;
            c = arg1.charAt(i - 1);
            ptest[offset - 1] = i;

            // Pour chaque colonne,
            for (j = 1; j < w; j++, offset++) {
                int count;

                if (c == arg2.charAt(j - 1)) {
                    count = 0;
                } else {
                    count = 200;
                }

                offset = j + i * w;

                ptest[offset] = mymin(ptest[offset - w] + 1,
                        ptest[offset - 1] + 1,
                        ptest[offset - 1 - w] + count);

            }
        }

        if (ptest[offset - 2] <= w + h - 2 - 2 * taille_min) {
            res = (arg1.charAt(h - 2) == arg2.charAt(w - 2)) ? 1 : 0;
        } else {
            res = 0;
        }



        return res;
    }


// Deux codes postaux correspondent s'ils sont égaux.
// Si seul leur département correspond, la moitié de la note est retournée
// Sinon, 0.
//
// Stringcodepostal,String codepostalreference
// return int
    int note_codepostal_base(String s0, String s1) {
        String codepostal, codepostalreference;
        int w, h, i;

        codepostal = s0;
        codepostalreference = s1;

        w = codepostal.length();
        h = codepostalreference.length();

        if (w < 2 || h < 2) {
            return 0;
        }

        if (codepostal.charAt(0) != codepostalreference.charAt(0) || codepostal.charAt(1) != codepostalreference.charAt(1)) {
            return 0;
        }

        if (w != h) {
            return (notecodepostal >> 1);
        }

        for (i = 2; i < w; i++) {
            if (codepostal.charAt(i) != codepostalreference.charAt(i)) {
                return (notecodepostal >> 1);
            }
        }

        return notecodepostal;
    }

// Deux codes postaux correspondent s'ils sont égaux.
// Si seul leur département correspond, la moitié de la note est retournée
// Sinon, 0.
//
    int note_codepostal_base2(String codepostal, String codepostalreference) {
        int w, h, i;

        w = codepostal.length();
        h = codepostalreference.length();

        if (w < 2 || h < 2) {
            return 0;
        }

        if (codepostal.charAt(0) != codepostalreference.charAt(0) || codepostal.charAt(1) != codepostalreference.charAt(1)) {
            return 0;
        }

        if (w != h) {
            return (notecodepostal >> 1);
        }

        for (i = 2; i < w; i++) {
            if (codepostal.charAt(i) != codepostalreference.charAt(i)) {
                return (notecodepostal >> 1);
            }
        }

        return (notecodepostal);
    }

// Obtient la note du numéro
//
// int numero,int numero_min,int numero_max
// return int
    int note_numero(int i0, int i1, int i2) {
        int numero, numero_min, numero_max;

        numero = i0;
        numero_min = i1;
        numero_max = i2;

        if (numero == 0 && numero_min == 0 && numero_max == 0) {
            return notenumero;
        }
        return ((numero >= numero_min && numero <= numero_max) ? notenumero : 0);
    }

// Obtient la note du numéro
//
// int numero,int numero_min,int numero_max
// return int
    int note_numero2(int numero, int numero_min, int numero_max) {
        if (numero == 0 && numero_min == 0 && numero_max == 0) {
            return notenumero;
        }
        return (numero >= numero_min && numero <= numero_max) ? notenumero : 0;
    }

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
//
// String motdeterminant,String motdeterminantreferentiel,
// Stringlibelle,Stringlibellesansarticlereferentiel,
// Stringtypedevoie,Stringtypedevoiereferentiel,
// Stringcommune,Stringcommunereferentiel,
// Stringcodepostal,Stringcodepostalreferentiel,
// String arrondissement,char refestarrondissement,
// int numero,int min_numero,int max_numero
//
// return int
    int note_voie_codepostal_commune_numero(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, boolean b11, int i12, int i13, int i14) {
        int note_determinant, note_libelle, note_typedevoie, note_commune, note_codepostal, note__numero, note__arrondissement, max;
        String motdeterminant, motdeterminantreferentiel;
        String libelle, libellesansarticlereferentiel;
        String typedevoie, typedevoiereferentiel;
        String commune, communereferentiel;
        String codepostal, codepostalreferentiel;
        String arrondissement;
        boolean refestarrondissement;
        int numero, min_numero, max_numero;

        motdeterminant = s0;
        motdeterminantreferentiel = s1;
        libelle = s2;
        libellesansarticlereferentiel = s3;
        typedevoie = s4;
        typedevoiereferentiel = s5;
        commune = s6;
        communereferentiel = s7;
        codepostal = s8;
        codepostalreferentiel = s9;
        arrondissement = s10;

        refestarrondissement = b11;

        numero = i12;
        min_numero = i13;
        max_numero = i14;

        //
        // ATTENTION : ne pas confondre note_XX avec noteXX
        //
        note_determinant = note_pourcentage_seuil2(motdeterminant, motdeterminantreferentiel, notedeterminant, pourcentagedeterminant);
        note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel, libelle, notelibelle, pourcentagelibelle);
        note_typedevoie = note_type_de_voie2(typedevoie, typedevoiereferentiel, notetypedevoie, pourcentagetypedevoie, taille_abbreviation_minimale);
        note_commune = note_pourcentage_seuil_total2(communereferentiel, commune, notecommune, pourcentagecommune);
        note_codepostal = note_codepostal_base2(codepostal, codepostalreferentiel);
        note__numero = note_numero2(numero, min_numero, max_numero);
        note__arrondissement = note_arrondissement2(arrondissement, codepostalreferentiel, refestarrondissement);
        max = notedeterminant + notetypedevoie + notecodepostal + notelibelle * nombre_de_mots2(libelle) +
                notenumero + notecommune * nombre_de_mots2(commune) + notearrondissement;

        return ((200 * (note_determinant + note_libelle + note_typedevoie + note_commune + note_codepostal + note__numero + note__arrondissement)) / max);
    }

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
//
//
// String motdeterminant,String motdeterminantreferentiel,
// Stringlibelle,Stringlibellesansarticlereferentiel,
// Stringtypedevoie,Stringtypedevoiereferentiel,
// Stringcommune,Stringcommunereferentiel,
// Stringcodepostal,Stringcodepostalreferentiel,
// String arrondissement,char refestarrondissement
//
// return int
    public int note_voie_codepostal_commune(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, boolean b11) {
        int note_determinant, note_libelle, note_typedevoie, note_commune, note_codepostal, note__arrondissement, max;

        String motdeterminant, motdeterminantreferentiel;
        String libelle, libellesansarticlereferentiel;
        String typedevoie, typedevoiereferentiel;
        String commune, communereferentiel;
        String codepostal, codepostalreferentiel;
        String arrondissement;
        boolean refestarrondissement;

        motdeterminant = s0;
        motdeterminantreferentiel = s1;
        libelle = s2;
        libellesansarticlereferentiel = s3;
        typedevoie = s4;
        typedevoiereferentiel = s5;
        commune = s6;
        communereferentiel = s7;
        codepostal = s8;
        codepostalreferentiel = s9;
        arrondissement = s10;

        refestarrondissement = b11;

        //
        // ATTENTION : ne pas confondre note_XX avec noteXX
        //
        note_determinant = note_pourcentage_seuil2(motdeterminant, motdeterminantreferentiel, notedeterminant, pourcentagedeterminant);
        note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel, libelle, notelibelle, pourcentagelibelle);
        note_typedevoie = note_type_de_voie2(typedevoie, typedevoiereferentiel, notetypedevoie, pourcentagetypedevoie, taille_abbreviation_minimale);
        note_commune = note_pourcentage_seuil_total2(communereferentiel, commune, notecommune, pourcentagecommune);
        note_codepostal = note_codepostal_base2(codepostal, codepostalreferentiel);
        note__arrondissement = note_arrondissement2(arrondissement, codepostalreferentiel, refestarrondissement);
        max = notedeterminant + notetypedevoie + notecodepostal + notelibelle * nombre_de_mots2(libelle) +
                notecommune * nombre_de_mots2(commune) + notearrondissement;

        return ((200 * (note_determinant + note_libelle + note_typedevoie + note_commune + note_codepostal + note__arrondissement)) / max);
    }

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
// Les points de la commune sont donnés.
//
//
// String motdeterminant,String motdeterminantreferentiel,
// Stringlibelle,Stringlibellesansarticlereferentiel,
// Stringtypedevoie,Stringtypedevoiereferentiel,
// Stringcommunereferentiel,
// Stringcodepostal,Stringcodepostalreferentiel,
// int numero,int min_numero,int max_numero
//
// return int;
    int note_voie_codepostal_numero(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, int i9, int i10, int i11) {
        int note_determinant, note_libelle, note_typedevoie, note_commune, note_codepostal, note_arrondissement, note__numero, max;

        String motdeterminant, motdeterminantreferentiel;
        String libelle, libellesansarticlereferentiel;
        String typedevoie, typedevoiereferentiel;
        String communereferentiel;
        String codepostal, codepostalreferentiel;
        int numero, min_numero, max_numero;

        motdeterminant = s0;
        motdeterminantreferentiel = s1;
        libelle = s2;
        libellesansarticlereferentiel = s3;
        typedevoie = s4;
        typedevoiereferentiel = s5;
        communereferentiel = s6;
        codepostal = s7;
        codepostalreferentiel = s8;

        numero = i9;
        min_numero = i10;
        max_numero = i11;

        note_determinant = note_pourcentage_seuil2(motdeterminant, motdeterminantreferentiel, notedeterminant, pourcentagedeterminant);
        note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel, libelle, notelibelle, pourcentagelibelle);
        note_typedevoie = note_type_de_voie2(typedevoie, typedevoiereferentiel, notetypedevoie, pourcentagetypedevoie, taille_abbreviation_minimale);
        note_commune = notecommune * nombre_de_mots2(communereferentiel); // les points de la commune sont donnés.
        note_codepostal = note_codepostal_base2(codepostal, codepostalreferentiel);
        note_arrondissement = notearrondissement; // les points de l'arrondissement sont donnés.
        note__numero = note_numero2(numero, min_numero, max_numero);
        max = notedeterminant + notetypedevoie + notecodepostal + notelibelle * nombre_de_mots2(libelle) + notenumero + note_commune // plutôt que notecommune*nombre_de_mots(communereferentiel)
                + notearrondissement;
        return ((200 * (note_determinant + note_libelle + note_typedevoie + note_commune + note_codepostal + note__numero + note_arrondissement)) / max);
    }

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
// Les points de la commune sont donnés.
//
// String motdeterminant,String motdeterminantreferentiel,
// Stringlibelle,Stringlibellesansarticlereferentiel,
// Stringtypedevoie,Stringtypedevoiereferentiel,
// Stringcommunereferentiel,
// Stringcodepostal,Stringcodepostalreferentiel
//
// return int
    public int note_voie_codepostal(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {
        int note_determinant, note_libelle, note_typedevoie, note_commune, note_codepostal, note_arrondissement, max;

        String motdeterminant, motdeterminantreferentiel;
        String libelle, libellesansarticlereferentiel;
        String typedevoie, typedevoiereferentiel;
        String communereferentiel;
        String codepostal, codepostalreferentiel;

        motdeterminant = s0;
        motdeterminantreferentiel = s1;
        libelle = s2;
        libellesansarticlereferentiel = s3;
        typedevoie = s4;
        typedevoiereferentiel = s5;
        communereferentiel = s6;
        codepostal = s7;
        codepostalreferentiel = s8;

        note_determinant = note_pourcentage_seuil2(motdeterminant, motdeterminantreferentiel, notedeterminant, pourcentagedeterminant);
        note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel, libelle, notelibelle, pourcentagelibelle);
        note_typedevoie = note_type_de_voie2(typedevoie, typedevoiereferentiel, notetypedevoie, pourcentagetypedevoie, taille_abbreviation_minimale);
        note_commune = notecommune * nombre_de_mots2(communereferentiel); // les points de la commune sont donnés.
        note_codepostal = note_codepostal_base2(codepostal, codepostalreferentiel);
        note_arrondissement = notearrondissement; // les points de l'arrondissement sont donnés
        max = notedeterminant + notetypedevoie + notecodepostal + notelibelle * nombre_de_mots2(libelle) + note_commune + notearrondissement;


        return ((200 * (note_determinant + note_libelle + note_typedevoie + note_codepostal + note_commune + note_arrondissement)) / max);
    }

// Obtient la note d'un code postal comparé à un autre.
//
// Stringcodepostal,String codepostalreference
//
// return int;
    public int note_codepostal(String s0, String s1) {
        int note_codepostal, max;

        String codepostal, codepostalreference;

        codepostal = s0;
        codepostalreference = s1;

        note_codepostal = note_codepostal_base2(codepostal, codepostalreference);

        max = notecodepostal;

        return ((200 * note_codepostal) / max);
    }

// Obtient la note d'un code postal et d'une commune comparé à un autre couple.
//
// Stringcommune,Stringcommunereference,Stringcodepostal,String codepostalreference,String arrondissement,char refestarrondissement
//
// return int
    public int note_codepostal_commune(String s0, String s1, String s2, String s3, String s4, boolean b5) {
        int note_codepostal, note_commune, note__arrondissement, max;

        String commune, communereference, codepostal, codepostalreference, arrondissement;
        boolean refestarrondissement;

        commune = s0;
        communereference = s1;
        codepostal = s2;
        codepostalreference = s3;
        arrondissement = s4;

        refestarrondissement = b5;

        note_codepostal = note_codepostal_base2(codepostal, codepostalreference);
        note_commune = note_pourcentage_seuil_total2(communereference, commune, notecommune, pourcentagecommune);
        note__arrondissement = note_arrondissement2(arrondissement, codepostalreference, refestarrondissement);

        max = notecodepostal + notecommune * nombre_de_mots2(commune) + notearrondissement;

        return ((200 * (note_commune + note_codepostal + note__arrondissement)) / max);
    }

// Obtient la note d'une commune et d'un éventuel arrondissement comparé à un autre couple.
//
//
// Stringcommune,Stringcommunereference,String arrondissement,String codepostalreference,char refestarrondissement
//
// return int;
    public int note_commune(String s0, String s1, String s2, String s3, boolean b4) {
        int note__arrondissement, note_commune, max;

        String commune, communereference, arrondissement, codepostalreference;
        boolean refestarrondissement;

        commune = s0;
        communereference = s1;
        arrondissement = s2;
        codepostalreference = s3;

        refestarrondissement = b4;

        note_commune = note_pourcentage_seuil_total2(communereference, commune, notecommune, pourcentagecommune);
        note__arrondissement = note_arrondissement2(arrondissement, codepostalreference, refestarrondissement);

        max = notecommune * nombre_de_mots2(commune) + notearrondissement;

        return ((200 * (note_commune + note__arrondissement)) / max);
    }

// Obtient la note de la commune comparée à une commune de référence.
//
// Stringcommune,Stringcommunereference
//
// return int;
    public int note_commune_seul(String s0, String s1) {
        int note__arrondissement, note_commune, max;

        String commune, communereference;

        commune = s0;
        communereference = s1;

        note_commune = note_pourcentage_seuil_total2(communereference, commune, notecommune, pourcentagecommune);

        max = notecommune * nombre_de_mots2(commune);

        return ((200 * note_commune) / max);
    }

// Trouve l'index du dernier mot de la chaine spécifiée.
//
// String mot
//
// return int;
    int index_derniermot(String mot) {
        int size, index, startindex, state, endindex, i;
        char c;
        size = mot.length();
        state = 0;
        startindex = endindex = -1;

        for (index = 0; index < size; index++) {
            c = mot.charAt(index);
            switch (state) {
                case 0: // départ
                    switch (c) {
                        case ' ':
                            state = 1;
                            break;
                        default:
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
                            startindex = endindex = index;
                            state = 2;
                            break;
                    }
                    break;
                case 2: // suite de lettres
                    switch (c) {
                        case ' ':
                            endindex = index - 1;
                            state = 1;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

        return startindex;
    }

// Trouve l'index du dernier mot de la chaine spécifiée.
//
// String mot
//
// return int;
    int index_derniermot2(String mot) {
        int size, index, startindex, state, endindex, i;
        char c;

        size = mot.length();
        state = 0;
        startindex = endindex = -1;

        for (index = 0; index < size; index++) {
            c = mot.charAt(index);
            switch (state) {
                case 0: // départ
                    switch (c) {
                        case ' ':
                            state = 1;
                            break;
                        default:
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
                            startindex = endindex = index;
                            state = 2;
                            break;
                    }
                    break;
                case 2: // suite de lettres
                    switch (c) {
                        case ' ':
                            endindex = index - 1;
                            state = 1;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

        return startindex;
    }

// Trouve le dernier mot de la chaine.
// la chaine derniermot doit être suffisamment longue.
//
// String mot,String derniermot
//
// return int
    int dernier_mot(String s0, String s1) {
        int size, index, startindex, state, endindex, i;
        char c;
        char[] mot, derniermot;

        mot = s0.toCharArray();
        derniermot = s1.toCharArray();

        size = mot.length;

        state = 0;
        startindex = endindex = -1;

        for (index = 0; index < size; index++) {
            c = mot[index];
            switch (state) {
                case 0: // départ
                    switch (c) {
                        case ' ':
                            state = 1;
                            break;
                        default:
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
                            startindex = endindex = index;
                            state = 2;
                            break;
                    }
                    break;
                case 2: // suite de lettres
                    switch (c) {
                        case ' ':
                            endindex = index - 1;
                            state = 1;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        if (state == 2) {
            endindex = size - 1;
        }

        if (startindex == -1) {
            return 0;
        } else {

            for (index = startindex, i = 0; index <= endindex;) {
                derniermot[i++] = mot[index++];
            }

            return (endindex - startindex + 1);
        }
    }

// Trouve le dernier mot de la chaine.
// la chaine derniermot doit être suffisamment longue.
//
// String mot,String derniermot
//
// return int
    int dernier_mot2(String s0, String s1) {
        int size, index, startindex, state, endindex, i;
        char c;
        char[] mot = s0.toCharArray();
        char[] derniermot = s1.toCharArray();
        size = mot.length;

        state = 0;
        startindex = endindex = -1;

        for (index = 0; index < size; index++) {
            c = mot[index];
            switch (state) {
                case 0: // départ
                    switch (c) {
                        case ' ':
                            state = 1;
                            break;
                        default:
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
                            startindex = endindex = index;
                            state = 2;
                            break;
                    }
                    break;
                case 2: // suite de lettres
                    switch (c) {
                        case ' ':
                            endindex = index - 1;
                            state = 1;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        if (state == 2) {
            endindex = size - 1;
        }

        if (startindex == -1) {

            return (0);
        } else {

            for (index = startindex, i = 0; index <= endindex;) {
                derniermot[i++] = mot[index++];
            }

            return (endindex - startindex + 1);
        }
    }
}
