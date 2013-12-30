// Julien Moquet (+ Willy Aroche + HM)
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
// 06/10/2012 - correction des notes > 200 ou < 0 (calcul du levenstein)
//              contexte ne retourne plus VERYMAX en cas d'erreur mais -1
//              condition rejet supplémentaire dans la serie note_pourcentage_seuil
// 14/11/2012 - note des POIZON

#define WIN32
#define WIN32_CLIENT_ONLY
#define _USE_32BIT_TIME_T

#include <postgres.h>
#include <fmgr.h>

PG_MODULE_MAGIC;
// Ces définitions sont propres à levenstein.c
#define TRUE 1
#define FALSE 0

// Constante utilisée pour le calcul d'un minimum
// ou lorsque les dimensions des chaines sont 
// trop importantes.
#define VERYMAX 3000

#define MAX_CHARACTERS 250
#define MAX_WORDS_COUNT 15
#define MAX_FAUTES 5 // le nombre maximal de fautes pour lequel un pourcentage peut encore être appliqué (voir definitFautes)

#define MALUS_TRANSPOSITION 2
#define MALUS_AJOUT_MOT 4
#define MALUS_SUPPRESSION_MOT 0


int maluspasdemot = 4;
int maluspasdemotdirecteur = 7;

int notecodepostal = 50;
int notecommune = 50;
int notedeterminant = 2;
int notelibelle = 50;
int notetypedevoie = 28;
int notenumero = 10;
int notearrondissement = 10;

int notecle = 28;
int notepoizon = 50;
int noteligne2 = 10;
int noteligne3 = 10;
int noteligne4 = 10;
int noteligne5 = 10;
int noteligne6 = 10;
int noteligne7 = 10;

int pourcentagecodepostal = 40;
int pourcentagecommune = 80;
int pourcentagedeterminant = 65;
int pourcentagelibelle = 60;
int pourcentagetypedevoie = 60;

int pourcentagecle = 60;
int pourcentagepoizon = 60;
int pourcentageligne2 = 80;
int pourcentageligne3 = 80;
int pourcentageligne4 = 80;
int pourcentageligne5 = 80;
int pourcentageligne6 = 80;
int pourcentageligne7 = 80;

int taille_abbreviation_minimale = 2;

int taille_abbreviation_minimale_poizon = 2;

PG_FUNCTION_INFO_V1(definitNotes);

// Enregistre les valeurs des notes
// int codepostal,int commune,int determinant,int libelle,int typedevoie,int numero,int arrondissement
void definitNotes(PG_FUNCTION_ARGS)
{
  notecodepostal = PG_GETARG_INT32(0);
  notecommune = PG_GETARG_INT32(1);
  notedeterminant = PG_GETARG_INT32(2);
  notelibelle = PG_GETARG_INT32(3);
  notetypedevoie = PG_GETARG_INT32(4);
  notenumero = PG_GETARG_INT32(5);
  notearrondissement = PG_GETARG_INT32(6);
}

PG_FUNCTION_INFO_V1(definitNotesPOIZON);

// Enregistre les valeurs des notes appliquées aux POI et Zones
// int cle,int poizon, int ligne2,int ligne3, int ligne4, int ligne5, int ligne6, int ligne7
void definitNotesPOIZON(PG_FUNCTION_ARGS)
{
  notecle = PG_GETARG_INT32(0);
  notepoizon = PG_GETARG_INT32(1);
  noteligne2 = PG_GETARG_INT32(2);
  noteligne3 = PG_GETARG_INT32(3);
  noteligne4 = PG_GETARG_INT32(4);
  noteligne5 = PG_GETARG_INT32(5);
  noteligne6 = PG_GETARG_INT32(6);
  noteligne7 = PG_GETARG_INT32(7);
}

PG_FUNCTION_INFO_V1(definitPourcentages);

// Enregistre les valeurs des pourcentages
// int codepostal,int commune,int determinant,int libelle,int typedevoie
void definitPourcentages(PG_FUNCTION_ARGS)
{
  pourcentagecodepostal = PG_GETARG_INT32(0);
  pourcentagecommune = PG_GETARG_INT32(1);
  pourcentagedeterminant = PG_GETARG_INT32(2);
  pourcentagelibelle = PG_GETARG_INT32(3);
  pourcentagetypedevoie = PG_GETARG_INT32(4);
}

PG_FUNCTION_INFO_V1(definitPourcentagesPOIZON);

// Enregistre les valeurs des pourcentages appliquées aux POI et Zones
// int cle,int poizon, int ligne2,int ligne3, int ligne4, int ligne5, int ligne6, int ligne7
void definitPourcentagesPOIZON(PG_FUNCTION_ARGS)
{
  pourcentagecle = PG_GETARG_INT32(0);
  pourcentagepoizon = PG_GETARG_INT32(1);
  pourcentageligne2 = PG_GETARG_INT32(2);
  pourcentageligne3 = PG_GETARG_INT32(3);
  pourcentageligne4 = PG_GETARG_INT32(4);
  pourcentageligne5 = PG_GETARG_INT32(5);
  pourcentageligne6 = PG_GETARG_INT32(6);
  pourcentageligne7 = PG_GETARG_INT32(7);
}

PG_FUNCTION_INFO_V1(definitMalus);

// Enregistre les valeurs des malus
// int mot,int motdirecteur
void definitMalus(PG_FUNCTION_ARGS)
{
  maluspasdemot = PG_GETARG_INT32(0);
  maluspasdemotdirecteur = PG_GETARG_INT32(1);
}

PG_FUNCTION_INFO_V1(definitDivers);

// Enregistre divers paramètres.
// int nv_taille_abbr
void definitDivers(PG_FUNCTION_ARGS)
{
  taille_abbreviation_minimale = PG_GETARG_INT32(0);
}


PG_FUNCTION_INFO_V1(definitDiversPOIZON);

// Enregistre divers paramètres concernant les POI et Zones.
// int nv_taille_abbr
void definitDiversPOIZON(PG_FUNCTION_ARGS)
{
  taille_abbreviation_minimale_poizon = PG_GETARG_INT32(0);
}

// 
int mymin(int val0,int val1,int val2)
{
  if (val0<=val1)
  {
    if (val0<=val2)
      return val0;
    else
      return val2;
  }
  else
  {
    if (val1<=val2)
      return val1;
    else
      return val2;
  }
}

PG_FUNCTION_INFO_V1(size);

// obtient la taille d'une chaine
Datum size(PG_FUNCTION_ARGS)
{
  text *t = PG_GETARG_VARCHAR_P(0);

  int32 i = VARSIZE(t) - VARHDRSZ;

  PG_RETURN_INT32(i);
}

PG_FUNCTION_INFO_V1(echo);

Datum echo(PG_FUNCTION_ARGS)
{
  PG_RETURN_TEXT_P(PG_GETARG_VARCHAR_P(0));
}

// obtient la taille d'une chaine classique
int size2(char *arg1)
{
  int i=0;
  while(*(arg1+i))
    i++;
  return i;
}

// obtient la taille d'une chaine PostgreSQL
int size3(text *arg1)
{
  return VARSIZE(arg1)-VARHDRSZ;
}


PG_FUNCTION_INFO_V1(nombre_de_mots);

// EXPERIMENTAL
// Retourne le nombre de mots trouvés dans la chaine.
// La chaine doit être normalisée.
// text *arg1
Datum nombre_de_mots(PG_FUNCTION_ARGS)
{
  int i,d1,mots,state;
  text *arg1 = PG_GETARG_VARCHAR_P(0);

  // Découpe arg1 en mots
  d1 = VARSIZE(arg1)-VARHDRSZ;

  if (d1==0) return 0;

  state = mots = 0;

  for(i=0;i<d1;i++)
  {
    char c = *(VARDATA(arg1)+i);
    switch(state)
    {
      case 0:
        switch(c)
        {
          case ' ':
          case '\t':
            break;
          default: 
            state = 1;
            break;
        }
        break;
      case 1:
        switch(c)
        {
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
  switch(state)
  {
    case 1: mots++; break;
  }

  PG_RETURN_INT32(mots);
}

// EXPERIMENTAL
// Retourne le nombre de mots trouvés dans la chaine.
// La chaine doit être normalisée.
int nombre_de_mots2(text *arg1)
{
  int i,d1,mots,state;

  // Découpe arg1 en mots
  d1 = VARSIZE(arg1)-VARHDRSZ;

  if (d1==0) return 0;

  state = mots = 0;

  for(i=0;i<d1;i++)
  {
    char c = *(VARDATA(arg1)+i);
    switch(state)
    {
      case 0:
        switch(c)
        {
          case ' ':
          case '\t':
            break;
          default: 
            state = 1;
            break;
        }
        break;
      case 1:
        switch(c)
        {
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
  switch(state)
  {
    case 1: mots++; break;
  }
  return mots;
}

// EXPERIMENTAL
// Retourne le nombre de mots trouvés dans la chaine ou 0 si la chaîne est nulle.
// La chaine doit être normalisée.
int nombre_de_mots3(text *arg1){
	if(size3(arg1) == 0) return 1;
	return nombre_de_mots2(arg1);
}

PG_FUNCTION_INFO_V1(estAbbreviation);

// Retourne si arg1 est une abbréviation de arg2
// arg1 est une abbréviation de arg2 lorsque:
//   1. leur initiale est la même
//   2. arg1 = arg2 privé de certaines lettres
// text *arg1,text *arg2
Datum estAbbreviation(PG_FUNCTION_ARGS)
{
  int i,j,l1,l2;
  text *arg1, *arg2;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);

  l1 = VARSIZE(arg1)-VARHDRSZ;
  l2 = VARSIZE(arg2)-VARHDRSZ;

  if (l1==0)
    return FALSE;
  if (l2==0)
    return FALSE;

  if (*(VARDATA(arg1)) != *(VARDATA(arg2)))
    return FALSE;

  for(i=1,j=1;i<l1;i++)
  {
    char c = *(VARDATA(arg1)+i);

    while((j<l2) && (*(VARDATA(arg2)+j)!=c))
      j++;

    if (j==l2)
    {
      PG_RETURN_INT32(FALSE);
    }

    j++;
  }

  PG_RETURN_INT32(TRUE);
}

PG_FUNCTION_INFO_V1(distance_levenstein);

//
// Retourne la distance de levenshtein entre arg1 et arg2
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
// ou en cas de problème d'allocation mémoire
//
// text *arg1,text *arg2
//
// return int
Datum distance_levenstein(PG_FUNCTION_ARGS)
{
  int i,j,offset;
  int w,h,res;
  text *arg1, *arg2;
  int *ptest;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);

  w = VARSIZE(arg2)-VARHDRSZ+1;
  h = VARSIZE(arg1)-VARHDRSZ+1;

  if (h==1) return w-1;
  if (w==1) return h-1;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  ptest = (int*)palloc(sizeof(int)*(h+1)*(w+1));
  if (ptest==NULL)
   PG_RETURN_INT32(VERYMAX);

  for(j=0;j<w;j++)
  {
    *(ptest+j) = j;
  }

  offset = w+1;

  // Pour chaque ligne, 
  for(i=1;i<h;i++,offset++)
  {
    char c;
    c = *(VARDATA(arg1)+i-1);
    *(ptest+offset-1) = i;

    // Pour chaque colonne,
    for(j=1;j<w;j++,offset++)
    {
      int cout;

      if (c==*(VARDATA(arg2)+j-1))
        cout=0;
      else
        cout=1;

      *(ptest+offset) = mymin (*(ptest+offset-w)+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-w)+cout);
    }
  }

  res = *(ptest+offset-2);
  pfree(ptest);

  // récupère l'élément de la dernière ligne et de la dernière colonne.
  PG_RETURN_INT32(res);
}

//
// Retourne la distance de levenshtein entre arg1 et arg2
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
// ou en cas de problème d'allocation mémoire
int distance_levenstein2(char *arg1,char *arg2)
{
  int i,j,offset;
  int w,h,res;
  int *ptest;

  w = size2(arg2)+1;
  h = size2(arg1)+1;

  if (h==1) return w-1;
  if (w==1) return h-1;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    return VERYMAX;

  ptest = (int*)palloc(sizeof(int)*(h+1)*(w+1));
  if (ptest==NULL)
    return VERYMAX;

  for(j=0;j<w;j++)
  {
    *(ptest+j) = j;
  }

  offset = w+1;

  // Pour chaque ligne, 
  for(i=1;i<h;i++,offset++)
  {
    char c;
    c = *(arg1+i-1);
    *(ptest+offset-1) = i;

    // Pour chaque colonne,
    for(j=1;j<w;j++,offset++)
    {
      int cout;

      if (c==*(arg2+j-1))
        cout=0;
      else
        cout=1;

      *(ptest+offset) = mymin (*(ptest+offset-w)+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-w)+cout);
    }
  }

  // récupère l'élément de la dernière ligne et de la dernière colonne.
  res = *(ptest+offset-2);
  pfree(ptest);

  return res;
}

//
// Retourne la distance de levenshtein entre arg1 et arg2
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
// ou en cas de problème d'allocation mémoire
int distance_levenstein3(text *arg1,text *arg2)
{
  int i,j,offset;
  int w,h,res;
  int *ptest;

  w = VARSIZE(arg2)-VARHDRSZ+1;
  h = VARSIZE(arg1)-VARHDRSZ+1;

  if (h==1) return w-1;
  if (w==1) return h-1;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  ptest = (int*)palloc(sizeof(int)*(w+1)*(h+1));
  if (ptest==NULL)
    PG_RETURN_INT32(VERYMAX);

  for(j=0;j<w;j++)
  {
    *(ptest+j) = j;
  }

  offset = w+1;

  // Pour chaque ligne, 
  for(i=1;i<h;i++,offset++)
  {
    char c;
    c = *(VARDATA(arg1)+i-1);
    *(ptest+offset-1) = i;

    // Pour chaque colonne,
    for(j=1;j<w;j++,offset++)
    {
      int cout;

      if (c==*(VARDATA(arg2)+j-1))
        cout=0;
      else
        cout=1;

      *(ptest+offset) = mymin (*(ptest+offset-w)+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-w)+cout);
    }
  }

  // récupère l'élément de la dernière ligne et de la dernière colonne.
  res = *(ptest+offset-2);
  pfree(ptest);

  PG_RETURN_INT32(res);
}

// Retourne la distance de levenstein entre arg1 et arg2
// Le mot arg2 est utilisé avec des jokers au début et à la fin
//
// arg1 et arg2 se terminent par un 0.
//
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS ou en
// cas de problème d'allocation mémoire.
int distance_levenstein_joker(char *arg1,char *arg2)
{
  int i,j,offset;
  int h,w;
  int min;
  int *ptest;

  h = size2(arg1);
  w = size2(arg2);

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    return VERYMAX;

  ptest = palloc(sizeof(int)*(w+1)*(h+1));
  if (ptest==NULL)
    return VERYMAX;

  for(j=0;j<=w;j++)
  {
    *(ptest+j) = j;
  }

  offset = w+2;

  min = VERYMAX; // les valeurs suivantes seront forcemment plus petites.

  // Pour chaque ligne, 
  for(i=1;i<=h;i++,offset++)
  {
    char c;
    int  tempmin;
    c = *(arg1+i-1);
    *(ptest+offset-1) = 0; // La première colonne est à zéro
                           // pour simuler le joker de départ sur arg2;

    // Pour chaque colonne,
    for(j=1;j<=w;j++,offset++)
    {
      int cout;

      if (c==*(arg2+j-1))
        cout=0;
      else
        cout=1;

      *(ptest+offset) = mymin (*(ptest+offset-(w+1))+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-(w+1))+cout);
    }

    if ((tempmin= *(ptest+offset-1))<min)
      min = tempmin; // récupérer le plus petit élément de la dernière colonne
                     // permet de simuler le joker final.
  }

  pfree(ptest);

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
int distance_levenstein_joker_no_spaces_classic(char *arg1,char *arg2)
{
  int i,j,offset;
  int h,w;
  int min;
  int *ptest;

  h = size2(arg1);
  w = size2(arg2);

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    return VERYMAX;

  ptest = (int*)palloc(sizeof(int)*(w+1)*(h+1));
  if (ptest==NULL)
  {
    return VERYMAX;
  }

  // Les valeurs de départ sont modifiées
  // pour que les espaces soient ignorés
  i = 0;
  for(j=0;j<w;j++)
  {
    if (*(arg2+j)!=' ')
    {
      if (j)
        *(ptest+j) = ++i;
      else
        *(ptest+j) = i;
    }
    else
      *(ptest+j) = i;
  }
  *(ptest+w) = w;

  offset = w+2;

  min = VERYMAX; // les valeurs suivantes seront forcemment plus petites.

  // Pour chaque ligne, 
  for(i=1;i<=h;i++,offset++)
  {
    char c;
    int  tempmin;
    c = *(arg1+i-1);
    *(ptest+offset-1) = 0; // La première colonne est à zéro
                           // pour simuler le joker de départ sur arg2;

    if (c==' ') // les espaces sur la ligne sont ignorés
    {
      // recopie la ligne précédente
      for(j=1;j<=w;j++,offset++)
      {
        *(ptest+offset) = *(ptest+offset-(w+1)); 
      }
    }
    else
    // Pour chaque colonne,
    for(j=1;j<=w;j++,offset++)
    {
      int cout;
      char c2 = *(arg2+j-1);
      
      if (c2==' ')
      {
        *(ptest+offset) = *(ptest+offset-1);
      }
      else
      {
        if (c==c2)
          cout=0;
        else
          cout=1;

        *(ptest+offset) = mymin (*(ptest+offset-(w+1))+1,
                                 *(ptest+offset-1)+1,
                                 *(ptest+offset-1-(w+1))+cout);
      }
    }

    if ((tempmin= *(ptest+offset-1))<min)
      min = tempmin; // récupérer le plus petit élément de la dernière colonne
                     // permet de simuler le joker final.
  }

  pfree(ptest);

  // récupère le plus petit élément de la dernière colonne.
  return min;
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
int distance_levenstein_joker_no_spaces(char *arg1,char *arg2)
{
  int i,j,offset;
  int h,w;
  int min,premiere_colonne;
 
  int *ptest;

  h = size2(arg1);
  w = size2(arg2);

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    return VERYMAX;

  ptest = (int*)palloc(sizeof(int)*(h+1)*(w+1));
  if (ptest==NULL)
  {
    return VERYMAX;
  }

  // Les valeurs de départ sont modifiées
  // pour que les espaces soient ignorés
  i = 0;
  for(j=0;j<w;j++)
  {
    if (*(arg2+j)!=' ')
    {
      if (j)
        *(ptest+j) = ++i;
      else
        *(ptest+j) = 0;
    }
    else
      *(ptest+j) = i;
  }
  *(ptest+w) = w;

  offset = w+2;

  min = VERYMAX; // les valeurs suivantes seront forcemment plus petites.

  // Pour chaque ligne, 
  for(i=1,premiere_colonne=0;i<=h;i++,offset++)
  {
    char c;
    int  tempmin;
    c = *(arg1+i-1);
    if (c==' ')
    {
      *(ptest+offset-1) = 0; // La première colonne est à zéro au niveau des espaces
                             // pour simuler le joker initial à chaque mot
    }
    else
    {
      *(ptest+offset-1) = ++premiere_colonne;
    }

    if (c==' ') // les espaces sur la ligne sont ignorés
    {
      // recopie la ligne précédente
      for(j=1;j<=w;j++,offset++)
      {
        *(ptest+offset) = *(ptest+offset-(w+1)); 
      }
    }
    else
    // Pour chaque colonne,
    for(j=1;j<=w;j++,offset++)
    {
      int cout;
      char c2 = *(arg2+j-1);
      
      if (c2==' ')
      {
        *(ptest+offset) = *(ptest+offset-1);
      }
      else
      {
        if (c==c2)
          cout=0;
        else
          cout=1;

        *(ptest+offset) = mymin (*(ptest+offset-(w+1))+1,
                                 *(ptest+offset-1)+1,
                                 *(ptest+offset-1-(w+1))+cout);
      }
    }

    if ((i==h || *(arg1+i)==' ') &&
        (tempmin= *(ptest+offset-1))<min )
      min = tempmin; // récupérer le plus petit élément de la dernière colonne
                     // permet de simuler le joker final en fin de mot.
  }

  pfree(ptest);

  // récupère le plus petit élément de la dernière colonne.
  return min;
}

PG_FUNCTION_INFO_V1(position_levenstein_joker_classic);

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
// text *arg1,text *arg2,int pourcentagedecorrespondance,int start
// return int
Datum position_levenstein_joker_classic(PG_FUNCTION_ARGS)
{
  int i,j,offset;
  int h,w;
  int min;
  int endindex;

  text *arg1, *arg2;
  int pourcentagedecorrespondance,start;

  int *ptest;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  pourcentagedecorrespondance = PG_GETARG_INT32(2);
  start = PG_GETARG_INT32(3);

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(0);

  if (start>=h) PG_RETURN_INT32(0);

  ptest = (int*)palloc(sizeof(int)*(w+1)*(h+1));
  if (ptest==NULL)
  {
    PG_RETURN_INT32(0);
  }

  i = 0;
  for(j=0;j<=w;j++)
  {
    *(ptest+j) = j;
  }

  offset = w+2;

  min = VERYMAX; // Les valeurs suivantes seront forcemment plus petites.
  endindex = VERYMAX;

  // Pour chaque ligne,
  for(i=1;i<=h-start;i++,offset++)
  {
    char c;
    int  tempmin;
    c = *(VARDATA(arg1)+i-1+start);
    *(ptest+offset-1) = 0; // La première colonne est à zéro
                           // pour simuler le joker de départ sur arg2;

    // Pour chaque colonne,
    for(j=1;j<=w;j++,offset++)
    {
      int cout;
      char c2 = *(VARDATA(arg2)+j-1);
	
      if (c==c2)
        cout=0;
      else
        cout=1;

      *(ptest+offset) = mymin (*(ptest+offset-(w+1))+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-(w+1))+cout);
    }

    if ((tempmin= *(ptest+offset-1))<min)
    {
      min = tempmin; // Récupérer le plus petit élément de la dernière colonne
                     // permet de simuler le joker final.
      endindex = i;
    }
  }

  // Si le nombre d'erreurs est satisfaisant,
  if (100*(w-min) >= (pourcentagedecorrespondance*w))
  {
    int startindex = endindex;
    int j = w;

    // Cherche l'index de départ : c'est l'index du caractère qui commence une suite
    // de correspondance avec la chaine.
    // Pour cela, la suite des correspondances trouvées jusqu'au caractère final
    // est remontée, jusqu'à aboutir au premier caractère d'une des deux chaines.
    while(j!=1 && startindex!=1)
    {
      int dessus,cote,diag;
      min = *(ptest+j+startindex*(w+1));
      dessus = *(ptest+j-1+startindex*(w+1));
      cote = *(ptest+j+(startindex-1)*(w+1));
      diag = *(ptest+j-1+(startindex-1)*(w+1));

      // Le dessus est pris comme référence
      if (dessus<=diag)
      {
        if (dessus<=cote)
          j--;
        else
          startindex--;
      }
      else // puis la diagonale.
      {
        if (diag<=cote)
        {
          j--;
          startindex--;
        }
        else
          startindex--;
      }
    }

    startindex+=start-1;
    endindex+=start-1;

    // Pour plus de lisibilité, des mots entiers sont recherchés.

    // Cherche le début du mot trouvé
    while(startindex>start && *(VARDATA(arg1)+startindex-1)!=' ')
      startindex--;
    
    // Cherche la fin du mot trouvé
    while(endindex<h-1 && *(VARDATA(arg1)+endindex+1)!=' ')
      endindex++;
    
    pfree(ptest);

    // Retourne la position
    PG_RETURN_INT32(1 + endindex + 256*startindex);
  }
  pfree(ptest);
  PG_RETURN_INT32(0);
}

PG_FUNCTION_INFO_V1(position_levenstein_joker);


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
// text *arg1,text *arg2,int pourcentagedecorrespondance,int start
// return int
Datum position_levenstein_joker(PG_FUNCTION_ARGS)
{
  int i,j,offset,nb_fautes;
  int h,w;
  int min;
  int endindex;
  int premiere_colonne;
  int *ptest;
  text *arg1, *arg2;
  int pourcentagedecorrespondance,start;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  pourcentagedecorrespondance = PG_GETARG_INT32(2);
  start = PG_GETARG_INT32(3);

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(0);

  if (start>=h) PG_RETURN_INT32(0);

  ptest = (int*)palloc(sizeof(int)*(w+1)*(h+1));

  i = 0;
  for(j=0;j<=w;j++)
  {
    *(ptest+j) = j;
  }

  offset = w+2;

  min = VERYMAX; // Les valeurs suivantes seront forcemment plus petites.
  endindex = VERYMAX;

  // Pour chaque ligne,
  for(i=1,premiere_colonne=0;(i<=h-start)&&(endindex==VERYMAX);i++,offset++)
  {
    char c;
    int  tempmin;
    c = *(VARDATA(arg1)+i-1+start);
    if (c==' ')
    {
      *(ptest+offset-1) = 0; // La première colonne est à zéro
                             // pour simuler le joker de départ sur arg2;
    }
    else
    {
      *(ptest+offset-1) = ++premiere_colonne;
    }

    // Pour chaque colonne,
    for(j=1;j<=w;j++,offset++)
    {
      int cout;
      char c2 = *(VARDATA(arg2)+j-1);
	
      if (c==c2)
        cout=0;
      else
        cout=1;

      *(ptest+offset) = mymin (*(ptest+offset-(w+1))+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-(w+1))+cout);
    }

    if ((i==h-start || *(VARDATA(arg1)+i+start)==' ') &&
        (100*(w-(nb_fautes=*(ptest+offset-1))) >= (pourcentagedecorrespondance*w)))
    {
      endindex = i;  // si un mot satisfaisant est trouvé, la recherche s'arrête.
    }
  }

  // Si le nombre d'erreurs est satisfaisant,
  if (endindex!=VERYMAX)
  {
    int startindex;
    int j;
    int stop = 0;

    // Cherche si les mots suivants ne permettent pas de compléter le mot recherché 
    for(;(i<=h-start)&&!stop;i++,offset++)
    {
      char c;
      int  tempmin;
      c = *(VARDATA(arg1)+i-1+start);
      if (c==' ')
      {
        *(ptest+offset-1) = 0; // La première colonne est à zéro
                             // pour simuler le joker de départ sur arg2;
      }
      else
      {
        *(ptest+offset-1) = ++premiere_colonne;
      }

      // Pour chaque colonne,
      for(j=1;j<=w;j++,offset++)
      {
        int cout;
        char c2 = *(VARDATA(arg2)+j-1);
	
        if (c==c2)
          cout=0;
        else
          cout=1;

        *(ptest+offset) = mymin (*(ptest+offset-(w+1))+1,
                                 *(ptest+offset-1)+1,
                                 *(ptest+offset-1-(w+1))+cout);
      }

      if (i==h-start || *(VARDATA(arg1)+i+start)==' ')
      {
        if (*(ptest+offset-1)<=nb_fautes)
        {
           nb_fautes = *(ptest+offset-1);
           endindex = i;  // ce mot suivant peut être ajouté.
        }
        else
           stop = 1; // sinon, la recherche est terminée.
      }
    }

    j = w;
    startindex = endindex;

    // Cherche l'index de départ : c'est l'index du caractère qui commence une suite
    // de correspondance avec la chaine.
    // Pour cela, la suite des correspondances trouvées jusqu'au caractère final
    // est remontée, jusqu'à aboutir au premier caractère d'une des deux chaines.
    while(j!=1 && startindex!=1)
    {
      int dessus,cote,diag;
      min = *(ptest+j+startindex*(w+1));
      dessus = *(ptest+j-1+startindex*(w+1));
      cote = *(ptest+j+(startindex-1)*(w+1));
      diag = *(ptest+j-1+(startindex-1)*(w+1));

      // Le dessus est pris comme référence
      if (dessus<=diag)
      {
        if (dessus<=cote)
          j--;
        else
          startindex--;
      }
      else // puis la diagonale.
      {
        if (diag<=cote)
        {
          j--;
          startindex--;
        }
        else
          startindex--;
      }
    }

    startindex+=start-1;
    endindex+=start-1;

    // Pour plus de lisibilité, des mots entiers sont recherchés.

    // Cherche le début du mot trouvé
    while(startindex>start && *(VARDATA(arg1)+startindex-1)!=' ')
      startindex--;
    
    // Cherche la fin du mot trouvé
    while(endindex<h-1 && *(VARDATA(arg1)+endindex+1)!=' ')
      endindex++;
    
    pfree(ptest);

    // Retourne la position
    PG_RETURN_INT32(1 + endindex + 256*startindex + 65536*nb_fautes);
  }
  pfree(ptest);
  PG_RETURN_INT32(0);
}

PG_FUNCTION_INFO_V1(contexte);

// Retourne le nombre de mots de arg1 contenus dans arg2 avec correspondanceparmot% de correspondance minimum
// arg1 est normalisé. Ses mots sont séparés par des espaces.
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
// text *arg1,text *arg2,int correspondanceparmot
// return int
Datum contexte(PG_FUNCTION_ARGS)
{
  int count,d1,d2,i,j;
  char *pmot1,*pmot2;
  text *arg1, *arg2;
  int correspondanceparmot;
 
  count = 0;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  correspondanceparmot = PG_GETARG_INT32(2);
 
  // Check taille arg1 et arg2
  d1 = VARSIZE(arg1)-VARHDRSZ;
  
  if (d1>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  d2 = VARSIZE(arg2)-VARHDRSZ;

  if (d2>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  // Découpe arg1 en mots
  pmot1 = (char*)palloc(sizeof(char)*(d1+2));
  if (pmot1==NULL)
    PG_RETURN_INT32(VERYMAX);

  for(i=0;i<d1;i++)
  {
    char c = *(VARDATA(arg1)+i);
    if (c==' ')
      *(pmot1+i) = 0;
    else
      *(pmot1+i) = c;
  }
  *(pmot1+d1) = 0;
  *(pmot1+d1+1) = 0;

  // Découpe arg2 en mots
  pmot2 = (char*)palloc(sizeof(char)*(d2+2));
  if (pmot2==NULL)
  {
    pfree(pmot1);
    PG_RETURN_INT32(VERYMAX);
  }

  for(i=0;i<d2;i++)
  {
    char c = *(VARDATA(arg2)+i);
    if (c==' ')
      *(pmot2+i) = 0;
    else
      *(pmot2+i) = c;
  }
  *(pmot2+d2) = 0;
  *(pmot2+d2+1) = 0;

  // Cherche chaque mot de arg1
  i=0;
  while(*(pmot1+i))
  {
    int size = 0;
    int distance = VERYMAX;

    // dans chaque mot de arg2 : seul le plus proche est conservé.
    j=0;
    while(*(pmot2+j))
    {
      int temp_distance = distance_levenstein2(pmot2+j,pmot1+i);
      if (temp_distance<distance)
        distance = temp_distance;

      while(*(pmot2+j))
      {
        j++;
      }
      j++;
    }
     
    while(*(pmot1+i))
    {
      i++;
      size++;
    }
    i++;

    // produit en croix en nombre entiers
    if (100*distance <= (100-correspondanceparmot)*size)
      count++;
  }

  pfree(pmot1);
  pfree(pmot2); 

  PG_RETURN_INT32(count);
}

PG_FUNCTION_INFO_V1(distance_libelle);

// Retourne la distance entre deux libelles
// Les mots de arg1 sont cherchés dans arg2.
// Les espaces sont ignorés.
// Si le mot directeur n'est pas trouvé, le malus est appliqué.
// Si aucun mot n'est troué, VERYMAX est retourné.
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
//
// text *arg1,text *arg2,int correspondanceparmot,int maluspasdemot,int maluspasdemotdirecteur
// return int
Datum distance_libelle(PG_FUNCTION_ARGS)
{
  int distance_totale ,d1,d2,i,mots,count;
  char *pmot1,*pmot2;
  text *arg1, *arg2;
  int correspondanceparmot, maluspasdemot, maluspasdemotdirecteur;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  correspondanceparmot = PG_GETARG_INT32(2);
  maluspasdemot = PG_GETARG_INT32(3);
  maluspasdemotdirecteur = PG_GETARG_INT32(4);
  
  distance_totale = mots = count = 0;
 
  // vérification vis à vis du seuil de taille de chaîne. 
  d1 = VARSIZE(arg1)-VARHDRSZ;
  
  if (d1>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  d2 = VARSIZE(arg2)-VARHDRSZ;

  if (d2>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  // découpe le mot arg1
  pmot1 = (char*)palloc(sizeof(char)*(d1+2));
  if (pmot1==NULL)
    PG_RETURN_INT32(VERYMAX);
  for(i=0;i<d1;i++)
  {
    char c = *(VARDATA(arg1)+i);
    if (c==' ' || i==d1-1)
    {
      *(pmot1+i) = 0;
      mots++;
    }
    else
      *(pmot1+i) = c;
  }
  *(pmot1+d1) = 0;
  *(pmot1+d1+1) = 0;
  
  // copie arg2
  pmot2 = (char*)palloc(sizeof(char)*(d2+2));
  if (pmot2==NULL)
  {
    pfree(pmot1);
    PG_RETURN_INT32(VERYMAX);
  }
  for(i=0;i<d2;i++)
  {
    *(pmot2+i) = *(VARDATA(arg2)+i);
  }
  *(pmot2+d2) = 0;
  
  // Cherche chaque mot de arg1 dans arg2
  i=0;
  while(*(pmot1+i))
  {
    int size = 0;
    int distance = distance_levenstein_joker_no_spaces(pmot2,pmot1+i);
 
    while(*(pmot1+i))
    {
      i++;
      size++;
    }
    i++;

//    if (100*distance <= 100*size - size*correspondanceparmot) // calcul par pourcentage
    if (distance <= maluspasdemot) // calcul par distance absolue
    {
      distance_totale += distance;
      count++;
    }
    else
    {
      if (*(pmot1+i))
        distance_totale += maluspasdemot;
      else
        distance_totale += maluspasdemotdirecteur;
    }
  }

  pfree(pmot1);
  pfree(pmot2);

  if (count==0) PG_RETURN_INT32(VERYMAX);
  
  PG_RETURN_INT32(distance_totale);
}

PG_FUNCTION_INFO_V1(ontabbreviationcommune);

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
// text *arg1,text *arg2,int taille_abbreviation_minimale
// return int
Datum ontabbreviationcommune(PG_FUNCTION_ARGS)
{
  int i,j,offset;
  int w,h,res;
  text *arg1, *arg2;
  int taille_abbreviation_minimale;
  int *ptest;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  taille_abbreviation_minimale = PG_GETARG_INT32(2);

  w = VARSIZE(arg2)-VARHDRSZ+1;
  h = VARSIZE(arg1)-VARHDRSZ+1;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  if (w>2 && h>2)
  {
    if (*(VARDATA(arg1))!=*(VARDATA(arg2)))
      PG_RETURN_INT32(0);
  }

  ptest = (int*)palloc(sizeof(int)*(w+1)*(h+1));
  if (ptest==NULL)
    PG_RETURN_INT32(VERYMAX);

  for(j=0;j<w;j++)
  {
    *(ptest+j) = j;
  }

  offset = VARSIZE(arg2)+2;

  // Pour chaque ligne, 
  for(i=1;i<h;i++,offset++)
  {
    char c;
    c = *(VARDATA(arg1)+i-1);
    *(ptest+offset-1) = i;

    // Pour chaque colonne,
    for(j=1;j<w;j++,offset++)
    {
      int cout;

      if (c==*(VARDATA(arg2)+j-1))
        cout=0;
      else
        cout=200;

      offset = j+i*w;

      *(ptest+offset) = mymin (*(ptest+offset-w)+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-w)+cout);

    }
  }

  if (*(ptest+offset-2) <= w+h-2-2*taille_abbreviation_minimale)
    res = 1;
  else
    res = 0;

  pfree(ptest);

  PG_RETURN_INT32(res);
}

PG_FUNCTION_INFO_V1(distance_type_de_voie);

// EXPERIMENTAL
//
// Calcule la distance entre deux types de voies.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne malus si le nombre d'erreur ne depasse pas le malus
// Sinon, retourne 0.
//
// text *arg1,text *arg2,int taille_abbreviation_minimale,int malus
// return int
Datum distance_type_de_voie(PG_FUNCTION_ARGS)
{
  int w,h,d,pctg;
  text *arg1, *arg2;
  int taille_abbreviation_minimale, malus;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  taille_abbreviation_minimale = PG_GETARG_INT32(2);
  malus = PG_GETARG_INT32(3);

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (h<w)
  {
    if (estAbbreviationDe2(arg1,arg2,taille_abbreviation_minimale))
      PG_RETURN_INT32(malus);
  }
  else if (w<h)
  {
    if (estAbbreviationDe2(arg2,arg1,taille_abbreviation_minimale))
      PG_RETURN_INT32(malus);
  }
  else
  {
    if (estAbbreviationDe2(arg1,arg2,taille_abbreviation_minimale) || estAbbreviationDe2(arg2,arg1,taille_abbreviation_minimale))
      PG_RETURN_INT32(malus);
  }

  PG_RETURN_INT32(0);
}

// EXPERIMENTAL
//
// Calcule la distance entre deux types de voies.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne malus si le nombre d'erreur ne depasse pas le malus
// Sinon, retourne 0.
//
int distance_type_de_voie2(text *arg1,text *arg2,int taille_abbreviation_minimale,int malus)
{
  int w,h,d,pctg;

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (h<w)
  {
    if (estAbbreviationDe2(arg1,arg2,taille_abbreviation_minimale))
      return (malus);
  }
  else if (w<h)
  {
    if (estAbbreviationDe2(arg2,arg1,taille_abbreviation_minimale))
      return (malus);
  }
  else
  {
    if (estAbbreviationDe2(arg1,arg2,taille_abbreviation_minimale) || estAbbreviationDe2(arg2,arg1,taille_abbreviation_minimale))
      return (malus);
  }

  return (0);
}

PG_FUNCTION_INFO_V1(note_type_de_voie);

// EXPERIMENTAL
// Calcule la note de correspondance entre deux types de voies.
// arg2 est la chaine de référence.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne 0.
//
//
// text *arg1,text *arg2,int note_max,int seuil,int taille_abbreviation_minimale
// return int
Datum note_type_de_voie(PG_FUNCTION_ARGS)
{
  int w,h,i;
  text *arg1, *arg2;
  int note_max, seuil, taille_abbreviation_minimale;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  note_max = PG_GETARG_INT32(2);
  seuil = PG_GETARG_INT32(3);
  taille_abbreviation_minimale = PG_GETARG_INT32(4);

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (h==0) PG_RETURN_INT32(0);
  if (w==0) PG_RETURN_INT32(0);

  if (h<w)
  {
    if (estAbbreviationDe2(arg1,arg2,taille_abbreviation_minimale))
      PG_RETURN_INT32(note_max);
    PG_RETURN_INT32(0);
  }
  else if (w<h)
  {
    if (estAbbreviationDe2(arg2,arg1,taille_abbreviation_minimale))
      PG_RETURN_INT32(note_max);
    PG_RETURN_INT32(0);
  }
  // w=h
  for(i=0;i<h;i++)
  {
    if (*(VARDATA(arg1)+i)!=*(VARDATA(arg2)+i))
      PG_RETURN_INT32(0);
  }
  PG_RETURN_INT32(note_max);
}

// EXPERIMENTAL
// Calcule la note de correspondance entre deux types de voies.
// arg2 est la chaine de référence.
// Si l'un est l'abbréviation de l'autre, retourne note_max.
// Sinon, retourne 0.
//
int note_type_de_voie2(text *arg1,text *arg2,int note_max,int seuil,int taille_abbreviation_minimale)
{
  int w,h,i;
  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (h==0) return(0);
  if (w==0) return(0);

  if (h<w)
  {
    if (estAbbreviationDe2(arg1,arg2,taille_abbreviation_minimale))
      return(note_max);
    return(0);
  }
  else if (w<h)
  {
    if (estAbbreviationDe2(arg2,arg1,taille_abbreviation_minimale))
      return(note_max);
    return(0);
  }
  // w=h
  for(i=0;i<h;i++)
  {
    if (*(VARDATA(arg1)+i)!=*(VARDATA(arg2)+i))
      return(0);
  }
  return(note_max);
}

PG_FUNCTION_INFO_V1(note_pourcentage_seuil);

// EXPERIMENTAL
// Calcule la note de correspondance entre deux mots selon un calcul par pourcentage et seuil
// arg2 est le mot de référence.
// Si le nombre d'erreur ne depasse pas le seuil de pourcentage toléré ou 5 fautes, 
// retourne un pourcentage de la note maximal fonction du nombre de fautes.
// Sinon, retourne 0.
// retourne 0 si les chaines dépassent MAX_CHARACTER.
//
// text *arg1,text *arg2,int note_max,int seuil
// return int
Datum note_pourcentage_seuil(PG_FUNCTION_ARGS)
{
  int d,pctg,w,h;
  text *arg1, *arg2;
  int note_max, seuil;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  note_max = PG_GETARG_INT32(2);
  seuil = PG_GETARG_INT32(3);

  w = VARSIZE(arg2)-VARHDRSZ;
  h = VARSIZE(arg1)-VARHDRSZ;

  if (w==0) PG_RETURN_INT32(0);
  if (h==0) PG_RETURN_INT32(0);

  d = distance_levenstein3(arg1,arg2);

  if (d==VERYMAX || d>MAX_FAUTES)
    PG_RETURN_INT32(0);

  // WA 03/2012 : mauvaise gestion de l'arrondi
  //pctg = ((w-d)*100)/w;
  //if (pctg >= seuil)
  if((w-d) >= nbMinCarsBons(w, seuil))
  {
	if (d+1<=w/2)
      PG_RETURN_INT32(note_max - (note_max*d*d)/(w*(w-1)));
    else
      PG_RETURN_INT32(note_max - (note_max*d)/w);
  }
  PG_RETURN_INT32(0);
}

// EXPERIMENTAL
// Calcule la note de correspondance entre deux mots selon un calcul par pourcentage et seuil
// arg2 est le mot de référence.
// Si le nombre d'erreur ne depasse pas le seuil de pourcentage toléré ou 5 fautes, 
// retourne un pourcentage de la note maximal fonction du nombre de fautes.
// Sinon, retourne 0.
// retourne 0 si les chaines dépassent MAX_CHARACTER.
//
int note_pourcentage_seuil2(text *arg1,text *arg2,int note_max,int seuil)
{
  int d,pctg,w,h;

  w = VARSIZE(arg2)-VARHDRSZ;
  h = VARSIZE(arg1)-VARHDRSZ;

  if (w==0) return(0);
  if (h==0) return(0);

  d = distance_levenstein3(arg1,arg2);

  if (d==VERYMAX || d>MAX_FAUTES)
    return(0);

  // WA 03/2012 : mauvaise gestion de l'arrondi
  //pctg = ((w-d)*100)/w;
  //if (pctg >= seuil)
  if((w-d) >= nbMinCarsBons(w, seuil))
  {
	if (d+1<=w/2)
      return(note_max - (note_max*d*d)/(w*(w-1)));
    else
      return(note_max - (note_max*d)/w);
  }
  return(0);
}

PG_FUNCTION_INFO_V1(note_pourcentage_seuil_n);

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
// text *arg1,text *arg2,int note_max,int seuil
// return int
Datum note_pourcentage_seuil_n(PG_FUNCTION_ARGS)
{
  int d1,d2,i,mots,noteparmot,bonus;
  double note;
  char *pmot2,*pmot1;
  text *arg1, *arg2;
  int note_max, seuil;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  note_max = PG_GETARG_INT32(2);
  seuil = PG_GETARG_INT32(3);
  
  mots = 0;
  
  d1 = VARSIZE(arg1)-VARHDRSZ;

  if (d1>MAX_CHARACTERS)
    PG_RETURN_INT32(0);

  if (d1==0) PG_RETURN_INT32(0);

  // Découpe arg2 en mots
  d2 = VARSIZE(arg2)-VARHDRSZ;

  if (d2==0) PG_RETURN_INT32(0);
  
  if (d2>MAX_CHARACTERS)
    PG_RETURN_INT32(0);

  // découpe arg2 en mots
  pmot2 = (char*)palloc(sizeof(char)*(d2+2));
  if (pmot2==NULL)
    PG_RETURN_INT32(0);
  for(i=0;i<d2;i++)
  {
    char c = *(VARDATA(arg2)+i);
    if (c==' ' || i==d2-1)
    {
      *(pmot2+i) = 0;
      mots++;
    }
    else
      *(pmot2+i) = c;
  }
  *(pmot2+d2) = 0;
  *(pmot2+d2+1) = 0;

  // Copie arg1
  pmot1 = (char*)palloc(sizeof(char)*(d1+1));
  if (pmot1==NULL)
  {
    pfree(pmot2);
    PG_RETURN_INT32(0);
  }
  for(i=0;i<d1;i++)
  {
    *(pmot1+i) = *(VARDATA(arg1)+i);
  }
  *(pmot1+d1) = 0;

  noteparmot = note_max/mots;
  note       = note_max%mots;

  // Cherche chaque mot de arg2 dans arg1
  i=0;
  while(*(pmot2+i))
  {
    int pctg,size = 0;
    int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);

    while(*(pmot2+i))
    {
      i++;
      size++;
    }
    i++;

    if (distance <= MAX_FAUTES)
    {
	  // WA 03/2012 : mauvaise gestion de l'arrondi
      //pctg = (100*(size-distance))/size;
      //if (pctg >= seuil)
	  if((size-distance) >= nbMinCarsBons(size, seuil))
      {
        // attenuation autour de 0 fautes
        if (distance+1<=size/2)
          note += noteparmot - ((double)(noteparmot*distance*distance))/((double)(size*(size-1)));
        else
          note += noteparmot - (noteparmot*distance)/(size);
      }
    }
  }

  pfree(pmot1);
  pfree(pmot2);

  PG_RETURN_INT32((int)note); // troncature double=>int
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
int note_pourcentage_seuil_n2(text *arg1,text *arg2,int note_max,int seuil)
{
  int d1,d2,i,mots,noteparmot,bonus;
  double note;
  char *pmot2,*pmot1;

  mots = 0;
  
  d1 = VARSIZE(arg1)-VARHDRSZ;

  if (d1>MAX_CHARACTERS)
    return(0);

  if (d1==0) return(0);

  // Découpe arg2 en mots
  d2 = VARSIZE(arg2)-VARHDRSZ;

  if (d2==0) return(0);
  
  if (d2>MAX_CHARACTERS)
    return(0);

  // découpe arg2 en mots
  pmot2 = (char*)palloc(sizeof(char)*(d2+2));
  if (pmot2==NULL)
    return 0;
  for(i=0;i<d2;i++)
  {
    char c = *(VARDATA(arg2)+i);
    if (c==' ' || i==d2-1)
    {
      *(pmot2+i) = 0;
      mots++;
    }
    else
      *(pmot2+i) = c;
  }
  *(pmot2+d2) = 0;
  *(pmot2+d2+1) = 0;

  // Copie arg1
  pmot1 = (char*)palloc(sizeof(char)*(d1+1));
  if (pmot1==NULL)
  {
    pfree(pmot2);
    return 0;
  }
  for(i=0;i<d1;i++)
  {
    *(pmot1+i) = *(VARDATA(arg1)+i);
  }
  *(pmot1+d1) = 0;

  noteparmot = note_max/mots;
  note       = note_max%mots;

  // Cherche chaque mot de arg2 dans arg1
  i=0;
  while(*(pmot2+i))
  {
    int pctg,size = 0;
    int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);

    while(*(pmot2+i))
    {
      i++;
      size++;
    }
    i++;

    if (distance <= MAX_FAUTES)
    {
	  // WA 03/2012 : mauvaise gestion de l'arrondi
      //pctg = (100*(size-distance))/size;
      //if (pctg >= seuil)
	  if((size-distance) >= nbMinCarsBons(size, seuil))
      {
        if (distance+1<=size/2)
          note += noteparmot - ((double)(noteparmot*distance*distance))/((double)(size*(size-1)));
        else
          note += noteparmot - (noteparmot*distance)/(size);
      }
    }
  }

  pfree(pmot1);
  pfree(pmot2);

  return((int)note);
}

PG_FUNCTION_INFO_V1(note_pourcentage_seuil_total);

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
// text *arg1,text *arg2,int noteparmot,int seuil
// return int
Datum note_pourcentage_seuil_total(PG_FUNCTION_ARGS)
{
  int d1,d2,i,bonus,note;
  int nbmots1,nbmots2;
  char *pmot1,*pmot2;
  text *arg1, *arg2;
  int noteparmot, seuil;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  noteparmot = PG_GETARG_INT32(2);
  seuil = PG_GETARG_INT32(3);
  
  d1 = VARSIZE(arg1)-VARHDRSZ;

  if (d1>MAX_CHARACTERS)
    PG_RETURN_INT32(0);

  if (d1==0) PG_RETURN_INT32(0);
  
  d2 = VARSIZE(arg2)-VARHDRSZ;
  
  if (d2==0) PG_RETURN_INT32(0);
  
  if (d2>MAX_CHARACTERS)
    PG_RETURN_INT32(0);
  
  // découpe arg2 en mots
  pmot2 = (char*)palloc(sizeof(char)*(d2+2));
  if (pmot2==NULL)
    PG_RETURN_INT32(0);
  for(i=0,nbmots2=1;i<d2;i++)
  {
    char c = *(VARDATA(arg2)+i);
    if (c==' ') //  || i==d2-1)
    {
      nbmots2++;
      *(pmot2+i) = 0;
    }
    else
      *(pmot2+i) = c;
  }
  *(pmot2+d2) = 0;
  *(pmot2+d2+1) = 0;
  
  // Copie arg1
  pmot1 = (char*)palloc(sizeof(char)*(d1+1));
  if (pmot1==NULL)
  {
    pfree(pmot2);
    PG_RETURN_INT32(0);
  }
  for(i=0;i<d1;i++)
  {
    *(pmot1+i) = *(VARDATA(arg1)+i);
  }
  *(pmot1+d1) = 0;
  nbmots1 = nombre_de_mots2(arg1);

  note       = 0;

  // Cherche chaque mot de arg2 dans arg1
  i=0;
  while(*(pmot2+i))
  {
    int pctg,size = 0;
    int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);

    while(*(pmot2+i))
    {
      i++;
      size++;
    }
    i++;

    if (distance <= MAX_FAUTES)
    {
	  // WA 03/2012 : mauvaise gestion de l'arrondi
      //pctg = (100*(size-distance))/size;
      //if (pctg >= seuil)
	  if((size-distance) >= nbMinCarsBons(size, seuil))
      {
        if (distance+1<=size/2)
          note += noteparmot - ((double)(noteparmot*distance*distance))/((double)(size*(size-1)));
        else
          note += noteparmot - (noteparmot*distance)/(size);

      }
    }
  }

  pfree(pmot1);
  pfree(pmot2);

  PG_RETURN_INT32(note - maluspasdemot*(nbmots1>nbmots2?nbmots1-nbmots2:nbmots2-nbmots1));
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
int note_pourcentage_seuil_total2(text *arg1,text *arg2,int noteparmot,int seuil)
{
  int d1,d2,i,bonus,note;
  int nbmots1,nbmots2;
  char *pmot2,*pmot1;
 
  d1 = VARSIZE(arg1)-VARHDRSZ;

  if (d1>MAX_CHARACTERS)
    return(0);

  if (d1==0) return(0);
  
  d2 = VARSIZE(arg2)-VARHDRSZ;
  
  if (d2==0) return(0);
  
  if (d2>MAX_CHARACTERS)
    return(0);
  
  // découpe arg2 en mots
  pmot2 = (char*)palloc(sizeof(char)*(d2+2));
  if (pmot2==NULL)
    return 0;
  for(i=0,nbmots2=1;i<d2;i++)
  {
    char c = *(VARDATA(arg2)+i);
    if (c==' ') //  || i==d2-1)
    {
      nbmots2++;
      *(pmot2+i) = 0;
    }
    else
      *(pmot2+i) = c;
  }
  *(pmot2+d2) = 0;
  *(pmot2+d2+1) = 0;
  
  // Copie arg1
  pmot1 = (char*)palloc(sizeof(char)*(d1+1));
  if (pmot1==NULL)
  {
    pfree(pmot2);
    return 0;
  }
  for(i=0;i<d1;i++)
  {
    *(pmot1+i) = *(VARDATA(arg1)+i);
  }
  *(pmot1+d1) = 0;
  nbmots1 = nombre_de_mots2(arg1);

  note       = 0;

  // Cherche chaque mot de arg2 dans arg1
  i=0;
  while(*(pmot2+i))
  {
    int pctg,size = 0;
    int distance = distance_levenstein_joker_no_spaces(pmot1,pmot2+i);

    while(*(pmot2+i))
    {
      i++;
      size++;
    }
    i++;

    if (distance <= MAX_FAUTES)
    {
	  // WA 03/2012 : mauvaise gestion de l'arrondi
      //pctg = (100*(size-distance))/size;
      //if (pctg >= seuil)
	  if((size-distance) >= nbMinCarsBons(size, seuil))
      {
        if (distance+1<=size/2)
          note += noteparmot - ((double)(noteparmot*distance*distance))/((double)(size*(size-1)));
        else
          note += noteparmot - (noteparmot*distance)/(size);
      }
    }
  }

  pfree(pmot1);
  pfree(pmot2);

  return(note - maluspasdemot*(nbmots1>nbmots2?nbmots1-nbmots2:nbmots2-nbmots1));
}

// Retourne le nombre minimal de caracteres corrects devant etre presents dans une chaine de longueur size 
// pour etre au dessus du seuil seuilPercent en pourcents.
int nbMinCarsBons(int size, int seuilPercent)
{
	double nbCarsBonsMin = (double)((seuilPercent *size)/100);
	return (int)(nbCarsBonsMin + 0.5);
}

PG_FUNCTION_INFO_V1(seterminepar);

// Retourne 1 si chaine2 termine chaine1.
// Retourne 0 sinon.
//
//
// text *chaine1, text *chaine2
// return int
Datum seterminepar(PG_FUNCTION_ARGS)
{
  int d1,d2,i,j;
  text *chaine1, *chaine2;

  chaine1 = PG_GETARG_VARCHAR_P(0);
  chaine2 = PG_GETARG_VARCHAR_P(1);

  d2 = VARSIZE(chaine2)-VARHDRSZ;
  if (d2==0) PG_RETURN_INT32(1);

  d1 = VARSIZE(chaine1)-VARHDRSZ;
  if (d2>d1) PG_RETURN_INT32(0);

  for(i=d1-d2,j=0;i<d1;i++,j++)
  {
    if (*(VARDATA(chaine1)+i)!=*(VARDATA(chaine2)+j))
      PG_RETURN_INT32(0);
  }

  PG_RETURN_INT32(1);
}

// Retourne 1 si chaine2 termine chaine1.
// Retourne 0 sinon.
//
int seterminepar2(text *chaine1, text *chaine2)
{
  int d1,d2,i,j;

  d2 = VARSIZE(chaine2)-VARHDRSZ;
  if (d2==0) return 1;

  d1 = VARSIZE(chaine1)-VARHDRSZ;
  if (d2>d1) return 0;

  for(i=d1-d2,j=0;i<d1;i++,j++)
  {
    if (*(VARDATA(chaine1)+i)!=*(VARDATA(chaine2)+j))
      return 0;
  }

  return 1;
}

PG_FUNCTION_INFO_V1(note_arrondissement);

// Obtient la note de l'arrondissement
// si la référence est un arrondissement alors refestarrondissement vaut 0 et notearrondissement est retourné
//
// a utiliser de la sorte : note_arrondissement('15','75015',code_insee_commune is null);
//
//
// text *arrondissement, text *codepostalreference, char refestarrondissement
// return int
Datum note_arrondissement(PG_FUNCTION_ARGS)
{ 
  text *arrondissement, *codepostalreference;
  char refestarrondissement;

  arrondissement = PG_GETARG_VARCHAR_P(0);
  codepostalreference = PG_GETARG_VARCHAR_P(1);
  refestarrondissement = PG_GETARG_CHAR(2);

  if (VARSIZE(arrondissement)==0) PG_RETURN_INT32(notearrondissement);
  if (VARSIZE(codepostalreference)<5) PG_RETURN_INT32(notearrondissement);
  if (((int)refestarrondissement)) PG_RETURN_INT32(notearrondissement);
  if (VARSIZE(codepostalreference)==0) PG_RETURN_INT32(0);
  if (seterminepar2(codepostalreference,arrondissement)) PG_RETURN_INT32(notearrondissement);
  PG_RETURN_INT32(0);
}

// Obtient la note de l'arrondissement
// si la référence est un arrondissement alors refestarrondissement vaut 0 et notearrondissement est retourné
//
// a utiliser de la sorte : note_arrondissement('15','75015',code_insee_commune is null);
//
int note_arrondissement2(text *arrondissement, text *codepostalreference, char refestarrondissement)
{ 
  if (VARSIZE(arrondissement)==0) PG_RETURN_INT32(notearrondissement);
  if (VARSIZE(codepostalreference)<5) PG_RETURN_INT32(notearrondissement);
  if (((int)refestarrondissement)) PG_RETURN_INT32(notearrondissement);
  if (VARSIZE(codepostalreference)==0) PG_RETURN_INT32(0);
  if (seterminepar2(codepostalreference,arrondissement)) PG_RETURN_INT32(notearrondissement);

  return (0);
}

PG_FUNCTION_INFO_V1(estAbbreviationDe);

// Retourne vrai si arg1 est une abbreviation de arg2
//
// text *arg1,text *arg2,int  taille_abbreviation_minimale
// return int / BOOLEAN
Datum estAbbreviationDe(PG_FUNCTION_ARGS)
{
  int w,h,i,j;
  text *arg1, *arg2;
  int taille_abbreviation_minimale;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  taille_abbreviation_minimale = PG_GETARG_INT32(2);

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(FALSE);

  if (h==0) PG_RETURN_INT32(FALSE);
  if (w==0) PG_RETURN_INT32(FALSE);

  if (h<taille_abbreviation_minimale)
    PG_RETURN_INT32(FALSE);

  if (w<taille_abbreviation_minimale)
    PG_RETURN_INT32(FALSE);

  if (*(VARDATA(arg1))!=*(VARDATA(arg2)))
    PG_RETURN_INT32(FALSE);

  j = 1;

  for(i=1;i<h;i++)
  {
    char c = *(VARDATA(arg1)+i);
    for(j=1;j<w;j++)
    {
      if (c == *(VARDATA(arg2)+j))
        break;
    }
    if (j==w)
      PG_RETURN_INT32(FALSE);
  }

  PG_RETURN_INT32(TRUE);
}

// Retourne vrai si arg1 est une abbreviation de arg2
//
// return int / BOOLEAN
int estAbbreviationDe2(text *arg1,text *arg2,int  taille_abbreviation_minimale)
{
  int w,h,i,j;

  h = VARSIZE(arg1)-VARHDRSZ;
  w = VARSIZE(arg2)-VARHDRSZ;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    return FALSE;

  if (h==0) return FALSE;
  if (w==0) return FALSE;

  if (h<taille_abbreviation_minimale)
    return FALSE;

  if (w<taille_abbreviation_minimale)
    return FALSE;

  if (*(VARDATA(arg1))!=*(VARDATA(arg2)))
    return FALSE;

  j = 1;

  for(i=1;i<h;i++)
  {
    char c = *(VARDATA(arg1)+i);
    for(j=1;j<w;j++)
    {
      if (c == *(VARDATA(arg2)+j))
        break;
    }
    if (j==w)
      return FALSE;
  }

  return TRUE;
}

PG_FUNCTION_INFO_V1(estSuspension);

// EXPERIMENTAL
// Permet de savoir si les chaines ont une abbréviation commune de type suspension
// Une suspension est telle que seules les premières lettres sont conservées.
// Le paramètres taille_min indique le nombre de lettres conservées.
//
// text *arg1,text *arg2,int taille_min
// return int / BOOLEAN
int estSuspension(PG_FUNCTION_ARGS)
{
  int i;
  int w,h;
  text *arg1, *arg2;
  int taille_min;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  taille_min = PG_GETARG_INT32(2);

  w = VARSIZE(arg2)-VARHDRSZ;
  h = VARSIZE(arg1)-VARHDRSZ;

  if (w<taille_min || h<taille_min) PG_RETURN_INT32(FALSE);

  for(i=0;i<taille_min;i++)
  {
    if (*(VARDATA(arg1)+i) != *(VARDATA(arg2)+i))
      PG_RETURN_INT32(FALSE);
  }
  PG_RETURN_INT32(TRUE);
}

PG_FUNCTION_INFO_V1(estLettreSuscrite);

// EXPERIMENTAL
// Permet de savoir si les chaines ont une abbréviation commune de type lettre suscrite
// Une lettre suscrite est telle que seules les premières lettres sont conservées ainsi que la dernière.
// Le paramètres taille_min indique le nombre de lettres de l'abbréviation au minimum.
//
// text *arg1,text *arg2,int taille_min
// return int / BOOLEAN
Datum estLettreSuscrite(PG_FUNCTION_ARGS)
{
  int i;
  int w,h;
  text *arg1, *arg2;
  int taille_min;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  taille_min = PG_GETARG_INT32(2);

  w = VARSIZE(arg2)-VARHDRSZ;
  h = VARSIZE(arg1)-VARHDRSZ;

  if (w<taille_min || h<taille_min) PG_RETURN_INT32(0);

  for(i=0;i<taille_min-1;i++)
  {
    if (*(VARDATA(arg1)+i) != *(VARDATA(arg2)+i))
      PG_RETURN_INT32(0);
  }

  PG_RETURN_INT32(*(VARDATA(arg1)+h-1) == *(VARDATA(arg2)+w-1));
}

PG_FUNCTION_INFO_V1(estContraction);

// EXPERIMENTAL
// Permet de savoir si les chaines ont une abbréviation commune de type contraction
// Une contraction est telle que la première, la dernière, et des lettres intermédiaires sont conservées.
// Le paramètres taille_min indique le nombre de lettres conservées dans l'abbréviation au minimum.
// retourne VERYMAX si les chaines dépassent MAX_CHARACTERS
//
// text *arg1,text *arg2,int taille_min
// return int
Datum estContraction(PG_FUNCTION_ARGS)
{
  int i,j,offset;
  int w,h,res;
  text *arg1, *arg2;
  int taille_min;
  int *ptest;

  arg1 = PG_GETARG_VARCHAR_P(0);
  arg2 = PG_GETARG_VARCHAR_P(1);
  taille_min = PG_GETARG_INT32(2);

  w = VARSIZE(arg2)-VARHDRSZ+1;
  h = VARSIZE(arg1)-VARHDRSZ+1;

  if (w>MAX_CHARACTERS || h>MAX_CHARACTERS)
    PG_RETURN_INT32(VERYMAX);

  if (w<=taille_min || h<=taille_min) return 0;

  if (w>2 && h>2)
  {
    if (*(VARDATA(arg1))!=*(VARDATA(arg2)))
      PG_RETURN_INT32(0);
  }
  
  ptest = (int *)palloc(sizeof(int)*(w+1));

  for(j=0;j<w;j++)
  {
    *(ptest+j) = j;
  }
  
  offset = VARSIZE(arg2)+2;

  // Pour chaque ligne, 
  for(i=1;i<h;i++,offset++)
  {
    char c;
    c = *(VARDATA(arg1)+i-1);
    *(ptest+offset-1) = i;

    // Pour chaque colonne,
    for(j=1;j<w;j++,offset++)
    {
      int cout;

      if (c==*(VARDATA(arg2)+j-1))
        cout=0;
      else
        cout=200;

      offset = j+i*w;

      *(ptest+offset) = mymin (*(ptest+offset-w)+1,
                               *(ptest+offset-1)+1,
                               *(ptest+offset-1-w)+cout);

    }
  }

  if (*(ptest+offset-2) <= w+h-2-2*taille_min)
  {
    res = (*(VARDATA(arg1)+h-2) == *(VARDATA(arg2)+w-2));
  }
  else
    res = 0;

  pfree(ptest);

  PG_RETURN_INT32(res);
}

PG_FUNCTION_INFO_V1(note_codepostal_base);

// Deux codes postaux correspondent s'ils sont égaux.
// Si seul leur département correspond, la moitié de la note est retournée
// Sinon, 0.
//
// text *codepostal,text *codepostalreference
// return int
Datum note_codepostal_base(PG_FUNCTION_ARGS)
{
  text *codepostal, *codepostalreference;
  int w,h,i;

  codepostal = PG_GETARG_VARCHAR_P(0);
  codepostalreference = PG_GETARG_VARCHAR_P(1);

  w = VARSIZE(codepostal)-VARHDRSZ;
  h = VARSIZE(codepostalreference)-VARHDRSZ;

  if (w<2 || h<2) PG_RETURN_INT32(0);

  if (*(VARDATA(codepostal))!=*(VARDATA(codepostalreference)) || *(VARDATA(codepostal)+1)!=*(VARDATA(codepostalreference)+1))
    PG_RETURN_INT32(0);

  if (w!=h) PG_RETURN_INT32(notecodepostal>>1);

  for(i=2;i<w;i++)
  {
    if (*(VARDATA(codepostal)+i)!=*(VARDATA(codepostalreference)+i))
      PG_RETURN_INT32(notecodepostal>>1);
  }

  PG_RETURN_INT32(notecodepostal);
}

// Deux codes postaux correspondent s'ils sont égaux.
// Si seul leur département correspond, la moitié de la note est retournée
// Sinon, 0.
//
int note_codepostal_base2(text *codepostal,text *codepostalreference)
{
  int w,h,i;

  w = VARSIZE(codepostal)-VARHDRSZ;
  h = VARSIZE(codepostalreference)-VARHDRSZ;

  if (w<2 || h<2) return 0;

  if (*(VARDATA(codepostal))!=*(VARDATA(codepostalreference)) || *(VARDATA(codepostal)+1)!=*(VARDATA(codepostalreference)+1))
    return (0);

  if (w!=h) return (notecodepostal>>1);

  for(i=2;i<w;i++)
  {
    if (*(VARDATA(codepostal)+i)!=*(VARDATA(codepostalreference)+i))
      return (notecodepostal>>1);
  }

  return (notecodepostal);
}

PG_FUNCTION_INFO_V1(note_numero);

// Obtient la note du numéro
//
// int numero,int numero_min,int numero_max
// return int
Datum note_numero(PG_FUNCTION_ARGS)
{
  int numero, numero_min, numero_max;

  numero = PG_GETARG_INT32(0);
  numero_min = PG_GETARG_INT32(1);
  numero_max = PG_GETARG_INT32(2);

  if (numero==0 && numero_min==0 && numero_max==0) PG_RETURN_INT32(notenumero);
  PG_RETURN_INT32((numero>=numero_min && numero<=numero_max)?notenumero:0);
}

// Obtient la note du numéro
//
// int numero,int numero_min,int numero_max
// return int
int note_numero2(int numero, int numero_min, int numero_max)
{
  if (numero==0 && numero_min==0 && numero_max==0) PG_RETURN_INT32(notenumero);
  return (numero>=numero_min && numero<=numero_max)?notenumero:0;
}

PG_FUNCTION_INFO_V1(note_voie_codepostal_commune_numero);

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
//
// text *motdeterminant,text *motdeterminantreferentiel,
// text *libelle,text *libellesansarticlereferentiel,
// text *typedevoie,text *typedevoiereferentiel,
// text *commune,text *communereferentiel,
// text *codepostal,text *codepostalreferentiel,
// text *arrondissement,char refestarrondissement,
// int numero,int min_numero,int max_numero
//
// return int
Datum note_voie_codepostal_commune_numero(PG_FUNCTION_ARGS)
{
  int note_determinant,note_libelle,note_typedevoie,note_commune,note_codepostal,note__numero,note__arrondissement,max;
  text *motdeterminant, *motdeterminantreferentiel;
  text *libelle, *libellesansarticlereferentiel;
  text *typedevoie, *typedevoiereferentiel;
  text *commune,*communereferentiel;
  text *codepostal,*codepostalreferentiel;
  text *arrondissement;
  char refestarrondissement;
  int numero, min_numero, max_numero;

  motdeterminant = PG_GETARG_VARCHAR_P(0);
  motdeterminantreferentiel = PG_GETARG_VARCHAR_P(1);
  libelle = PG_GETARG_VARCHAR_P(2);
  libellesansarticlereferentiel = PG_GETARG_VARCHAR_P(3);
  typedevoie = PG_GETARG_VARCHAR_P(4);
  typedevoiereferentiel = PG_GETARG_VARCHAR_P(5);
  commune = PG_GETARG_VARCHAR_P(6);
  communereferentiel = PG_GETARG_VARCHAR_P(7);
  codepostal = PG_GETARG_VARCHAR_P(8);
  codepostalreferentiel = PG_GETARG_VARCHAR_P(9);
  arrondissement = PG_GETARG_VARCHAR_P(10);

  refestarrondissement = PG_GETARG_CHAR(11);

  numero = PG_GETARG_INT32(12);
  min_numero = PG_GETARG_INT32(13);
  max_numero = PG_GETARG_INT32(14);

  //
  // ATTENTION : ne pas confondre note_XX avec noteXX
  //
  note_determinant = note_pourcentage_seuil2(motdeterminant,motdeterminantreferentiel,notedeterminant,pourcentagedeterminant);
  note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel,libelle,notelibelle,pourcentagelibelle);
  note_typedevoie = note_type_de_voie2(typedevoie,typedevoiereferentiel,notetypedevoie,pourcentagetypedevoie,taille_abbreviation_minimale);
  note_commune = note_pourcentage_seuil_total2(communereferentiel,commune,notecommune,pourcentagecommune);
  note_codepostal = note_codepostal_base2(codepostal,codepostalreferentiel);
  note__numero = note_numero2(numero,min_numero,max_numero);
  note__arrondissement = note_arrondissement2(arrondissement,codepostalreferentiel,refestarrondissement);
  max = notedeterminant+notetypedevoie+notecodepostal+notelibelle*nombre_de_mots2(libelle)+
        notenumero+notecommune*nombre_de_mots2(commune)+notearrondissement;

  PG_RETURN_INT32((200*(note_determinant+note_libelle+note_typedevoie+note_commune+note_codepostal+note__numero+note__arrondissement))/max);
}

PG_FUNCTION_INFO_V1(note_voie_codepostal_commune);

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
//
//
// text *motdeterminant,text *motdeterminantreferentiel,
// text *libelle,text *libellesansarticlereferentiel,
// text *typedevoie,text *typedevoiereferentiel,
// text *commune,text *communereferentiel,
// text *codepostal,text *codepostalreferentiel,
// text *arrondissement,char refestarrondissement
//
// return int
Datum note_voie_codepostal_commune(PG_FUNCTION_ARGS)
{
  int note_determinant,note_libelle,note_typedevoie,note_commune,note_codepostal,note__arrondissement,max;

  text *motdeterminant, *motdeterminantreferentiel;
  text *libelle, *libellesansarticlereferentiel;
  text *typedevoie, *typedevoiereferentiel;
  text *commune,*communereferentiel;
  text *codepostal,*codepostalreferentiel;
  text *arrondissement;
  char refestarrondissement;

  motdeterminant = PG_GETARG_VARCHAR_P(0);
  motdeterminantreferentiel = PG_GETARG_VARCHAR_P(1);
  libelle = PG_GETARG_VARCHAR_P(2);
  libellesansarticlereferentiel = PG_GETARG_VARCHAR_P(3);
  typedevoie = PG_GETARG_VARCHAR_P(4);
  typedevoiereferentiel = PG_GETARG_VARCHAR_P(5);
  commune = PG_GETARG_VARCHAR_P(6);
  communereferentiel = PG_GETARG_VARCHAR_P(7);
  codepostal = PG_GETARG_VARCHAR_P(8);
  codepostalreferentiel = PG_GETARG_VARCHAR_P(9);
  arrondissement = PG_GETARG_VARCHAR_P(10);

  refestarrondissement = PG_GETARG_CHAR(11);

  //
  // ATTENTION : ne pas confondre note_XX avec noteXX
  //
  note_determinant = note_pourcentage_seuil2(motdeterminant,motdeterminantreferentiel,notedeterminant,pourcentagedeterminant);
  note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel,libelle,notelibelle,pourcentagelibelle);
  note_typedevoie = note_type_de_voie2(typedevoie,typedevoiereferentiel,notetypedevoie,pourcentagetypedevoie,taille_abbreviation_minimale);
  note_commune = note_pourcentage_seuil_total2(communereferentiel,commune,notecommune,pourcentagecommune);
  note_codepostal = note_codepostal_base2(codepostal,codepostalreferentiel);
  note__arrondissement = note_arrondissement2(arrondissement,codepostalreferentiel,refestarrondissement);
  max = notedeterminant+notetypedevoie+notecodepostal+notelibelle*nombre_de_mots2(libelle)+
        notecommune*nombre_de_mots2(commune)+notearrondissement;

  PG_RETURN_INT32((200*(note_determinant+note_libelle+note_typedevoie+note_commune+note_codepostal+note__arrondissement))/max);
}

PG_FUNCTION_INFO_V1(note_voie_codepostal_numero);


// Obtient la note d'une voie, code postal et commune, comparée à une autre.
// Les points de la commune sont donnés.
//
//
// text *motdeterminant,text *motdeterminantreferentiel,
// text *libelle,text *libellesansarticlereferentiel,
// text *typedevoie,text *typedevoiereferentiel,
// text *communereferentiel,
// text *codepostal,text *codepostalreferentiel,
// int numero,int min_numero,int max_numero
//
// return int;
Datum note_voie_codepostal_numero(PG_FUNCTION_ARGS)
{
  int note_determinant,note_libelle,note_typedevoie,note_commune,note_codepostal,note_arrondissement,note__numero,max;

  text *motdeterminant, *motdeterminantreferentiel;
  text *libelle, *libellesansarticlereferentiel;
  text *typedevoie, *typedevoiereferentiel;
  text *communereferentiel;
  text *codepostal, *codepostalreferentiel;
  int numero, min_numero, max_numero;

  motdeterminant = PG_GETARG_VARCHAR_P(0);
  motdeterminantreferentiel = PG_GETARG_VARCHAR_P(1);
  libelle = PG_GETARG_VARCHAR_P(2);
  libellesansarticlereferentiel = PG_GETARG_VARCHAR_P(3);
  typedevoie = PG_GETARG_VARCHAR_P(4);
  typedevoiereferentiel = PG_GETARG_VARCHAR_P(5);
  communereferentiel = PG_GETARG_VARCHAR_P(6);
  codepostal = PG_GETARG_VARCHAR_P(7);
  codepostalreferentiel = PG_GETARG_VARCHAR_P(8);

  numero = PG_GETARG_INT32(9);
  min_numero = PG_GETARG_INT32(10);
  max_numero = PG_GETARG_INT32(11);

  note_determinant = note_pourcentage_seuil2(motdeterminant,motdeterminantreferentiel,notedeterminant,pourcentagedeterminant);
  note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel,libelle,notelibelle,pourcentagelibelle);
  note_typedevoie = note_type_de_voie2(typedevoie,typedevoiereferentiel,notetypedevoie,pourcentagetypedevoie,taille_abbreviation_minimale);
  note_commune = notecommune*nombre_de_mots2(communereferentiel); // les points de la commune sont donnés.
  note_codepostal = note_codepostal_base2(codepostal,codepostalreferentiel);
  note_arrondissement = notearrondissement; // les points de l'arrondissement sont donnés.
  note__numero = note_numero2(numero,min_numero,max_numero);
  max = notedeterminant+notetypedevoie+notecodepostal+notelibelle*nombre_de_mots2(libelle)
       +notenumero
       +note_commune // plutôt que notecommune*nombre_de_mots(communereferentiel)
       +notearrondissement;
  PG_RETURN_INT32((200*(note_determinant+note_libelle+note_typedevoie+note_commune+note_codepostal+note__numero+note_arrondissement))/max);
}

PG_FUNCTION_INFO_V1(note_voie_codepostal);

// Obtient la note d'une voie, code postal et commune, comparée à une autre.
// Les points de la commune sont donnés.
//
// text *motdeterminant,text *motdeterminantreferentiel,
// text *libelle,text *libellesansarticlereferentiel,
// text *typedevoie,text *typedevoiereferentiel,
// text *communereferentiel,
// text *codepostal,text *codepostalreferentiel
//
// return int
Datum note_voie_codepostal(PG_FUNCTION_ARGS)
{
  int note_determinant,note_libelle,note_typedevoie,note_commune,note_codepostal,note_arrondissement,max;

  text *motdeterminant, *motdeterminantreferentiel;
  text *libelle, *libellesansarticlereferentiel;
  text *typedevoie, *typedevoiereferentiel;
  text *communereferentiel;
  text *codepostal, *codepostalreferentiel;

  motdeterminant = PG_GETARG_VARCHAR_P(0);
  motdeterminantreferentiel = PG_GETARG_VARCHAR_P(1);
  libelle = PG_GETARG_VARCHAR_P(2);
  libellesansarticlereferentiel = PG_GETARG_VARCHAR_P(3);
  typedevoie = PG_GETARG_VARCHAR_P(4);
  typedevoiereferentiel = PG_GETARG_VARCHAR_P(5);
  communereferentiel = PG_GETARG_VARCHAR_P(6);
  codepostal = PG_GETARG_VARCHAR_P(7);
  codepostalreferentiel = PG_GETARG_VARCHAR_P(8);

  note_determinant = note_pourcentage_seuil2(motdeterminant,motdeterminantreferentiel,notedeterminant,pourcentagedeterminant);
  note_libelle = note_pourcentage_seuil_total2(libellesansarticlereferentiel,libelle,notelibelle,pourcentagelibelle);
  note_typedevoie = note_type_de_voie2(typedevoie,typedevoiereferentiel,notetypedevoie,pourcentagetypedevoie,taille_abbreviation_minimale);
  note_commune = notecommune*nombre_de_mots2(communereferentiel); // les points de la commune sont donnés.
  note_codepostal = note_codepostal_base2(codepostal,codepostalreferentiel);
  note_arrondissement = notearrondissement; // les points de l'arrondissement sont donnés
  max = notedeterminant+notetypedevoie+notecodepostal+notelibelle*nombre_de_mots2(libelle)+note_commune+notearrondissement;

  PG_RETURN_INT32((200*(note_determinant+note_libelle+note_typedevoie+note_codepostal+note_commune+note_arrondissement))/max);
}

PG_FUNCTION_INFO_V1(note_codepostal);

// Obtient la note d'un code postal comparé à un autre.
//
// text *codepostal,text *codepostalreference
//
// return int;
Datum note_codepostal(PG_FUNCTION_ARGS)
{
  int note_codepostal,max;

  text *codepostal, *codepostalreference;

  codepostal = PG_GETARG_VARCHAR_P(0);
  codepostalreference = PG_GETARG_VARCHAR_P(1);

  note_codepostal = note_codepostal_base2(codepostal,codepostalreference);

  max = notecodepostal;

  PG_RETURN_INT32((200*note_codepostal)/max);
}

PG_FUNCTION_INFO_V1(note_codepostal_commune);

// Obtient la note d'un code postal et d'une commune comparé à un autre couple.
//
// text *commune,text *communereference,text *codepostal,text *codepostalreference,text *arrondissement,char refestarrondissement
//
// return int
Datum note_codepostal_commune(PG_FUNCTION_ARGS)
{
  int note_codepostal, note_commune, note__arrondissement, max;

  text *commune, *communereference, *codepostal, *codepostalreference, *arrondissement;
  char refestarrondissement;

  commune = PG_GETARG_VARCHAR_P(0);
  communereference = PG_GETARG_VARCHAR_P(1);
  codepostal = PG_GETARG_VARCHAR_P(2);
  codepostalreference = PG_GETARG_VARCHAR_P(3);
  arrondissement = PG_GETARG_VARCHAR_P(4);

  refestarrondissement = PG_GETARG_CHAR(5);

  note_codepostal = note_codepostal_base2(codepostal,codepostalreference);
  note_commune = note_pourcentage_seuil_total2(communereference,commune,notecommune,pourcentagecommune);
  note__arrondissement = note_arrondissement2(arrondissement,codepostalreference,refestarrondissement);

  max = notecodepostal + notecommune*nombre_de_mots2(commune) + notearrondissement;

  PG_RETURN_INT32((200*(note_commune+note_codepostal+note__arrondissement))/max);
}

PG_FUNCTION_INFO_V1(note_cle_poizon);

// Obtient la note d'un poizon comparé à un autre couple.
//
//
// text *cle,text *clereference,text *poizon,text *poizonreference, text *ligne2, text *ligne2reference, text *ligne3, text *ligne3reference, text *ligne4, text *ligne4reference, text *ligne5, text *ligne5reference, text *ligne6, text *ligne6reference, text *ligne7, text *ligne7reference
//
// return int;
Datum note_cle_poizon(PG_FUNCTION_ARGS)
{

  int note_cle,note_poizon,note_ligne2,note_ligne3,note_ligne4,note_ligne5,note_ligne6,note_ligne7,max;

  text *cle, *clereferentiel;
  text *poizon, *poizonsansarticlereferentiel;
  text *ligne2, *ligne2referentiel;
  text *ligne3, *ligne3referentiel;
  text *ligne4, *ligne4referentiel;
  text *ligne5, *ligne5referentiel;
  text *ligne6, *ligne6referentiel;
  text *ligne7, *ligne7referentiel;

  cle = PG_GETARG_VARCHAR_P(0);
  clereferentiel = PG_GETARG_VARCHAR_P(1);
  poizon = PG_GETARG_VARCHAR_P(2);
  poizonsansarticlereferentiel = PG_GETARG_VARCHAR_P(3);
  ligne2 = PG_GETARG_VARCHAR_P(4);
  ligne2referentiel = PG_GETARG_VARCHAR_P(5);
  ligne3 = PG_GETARG_VARCHAR_P(6);
  ligne3referentiel = PG_GETARG_VARCHAR_P(7);
  ligne4 = PG_GETARG_VARCHAR_P(8);
  ligne4referentiel = PG_GETARG_VARCHAR_P(9);
  ligne5 = PG_GETARG_VARCHAR_P(10);
  ligne5referentiel = PG_GETARG_VARCHAR_P(11);
  ligne6 = PG_GETARG_VARCHAR_P(12);
  ligne6referentiel = PG_GETARG_VARCHAR_P(13);
  ligne7 = PG_GETARG_VARCHAR_P(14);
  ligne7referentiel = PG_GETARG_VARCHAR_P(15);

  //
  // ATTENTION : ne pas confondre note_XX avec noteXX
  //
  note_cle = note_type_de_voie2(cle,clereferentiel,notecle,pourcentagecle,taille_abbreviation_minimale_poizon);
  note_poizon = note_pourcentage_seuil_total2(poizonsansarticlereferentiel,poizon,notepoizon,pourcentagepoizon);
  note_ligne2 = (size3(ligne2) > 0) ? note_pourcentage_seuil_total2(ligne2referentiel,ligne2,noteligne2,pourcentageligne2) : noteligne2;
  note_ligne3 = (size3(ligne3) > 0) ? note_pourcentage_seuil_total2(ligne3referentiel,ligne3,noteligne3,pourcentageligne3) : noteligne3;
  note_ligne4 = (size3(ligne4) > 0) ? note_pourcentage_seuil_total2(ligne4referentiel,ligne4,noteligne4,pourcentageligne4) : noteligne4;
  note_ligne5 = (size3(ligne5) > 0) ? note_pourcentage_seuil_total2(ligne5referentiel,ligne5,noteligne5,pourcentageligne5) : noteligne5;
  note_ligne6 = (size3(ligne6) > 0) ? note_pourcentage_seuil_total2(ligne6referentiel,ligne6,noteligne6,pourcentageligne6) : noteligne6;
  note_ligne7 = (size3(ligne7) > 0) ? note_pourcentage_seuil_total2(ligne7referentiel,ligne7,noteligne7,pourcentageligne7) : noteligne7;

//  note_cle = notecle;
//  note_poizon = notepoizon;
//  note_ligne2 = note_ligne3 = note_ligne4 = note_ligne5 = note_ligne6 = note_ligne7 = noteligne2;

  max = notepoizon*nombre_de_mots3(poizon)+noteligne2*nombre_de_mots3(ligne2)+noteligne3*nombre_de_mots3(ligne3)+noteligne4*nombre_de_mots3(ligne4)+
  noteligne5*nombre_de_mots3(ligne5)+noteligne6*nombre_de_mots3(ligne6)+noteligne7*nombre_de_mots3(ligne7)+notecle;

  PG_RETURN_INT32((200*(note_cle+note_poizon+note_ligne2+note_ligne3+note_ligne4+note_ligne5+note_ligne6+note_ligne7))/max);
}

PG_FUNCTION_INFO_V1(note_commune);

// Obtient la note d'une commune et d'un éventuel arrondissement comparé à un autre couple.
//
//
// text *commune,text *communereference,text *arrondissement,text *codepostalreference,char refestarrondissement
//
// return int;
Datum note_commune(PG_FUNCTION_ARGS)
{
  int note__arrondissement, note_commune, max;

  text *commune,*communereference,*arrondissement,*codepostalreference;
  char refestarrondissement;

  commune = PG_GETARG_VARCHAR_P(0);
  communereference = PG_GETARG_VARCHAR_P(1);
  arrondissement = PG_GETARG_VARCHAR_P(2);
  codepostalreference = PG_GETARG_VARCHAR_P(3);

  refestarrondissement = PG_GETARG_CHAR(4);

  note_commune = note_pourcentage_seuil_total2(communereference,commune,notecommune,pourcentagecommune);
  note__arrondissement = note_arrondissement2(arrondissement,codepostalreference,refestarrondissement);

  max = notecommune*nombre_de_mots2(commune)+notearrondissement;

  PG_RETURN_INT32((200*(note_commune+note__arrondissement))/max);
}

PG_FUNCTION_INFO_V1(note_commune_seul);

// Obtient la note de la commune comparée à une commune de référence.
//
// text *commune,text *communereference
//
// return int;
Datum note_commune_seul(PG_FUNCTION_ARGS)
{
  int note__arrondissement, note_commune, max;

  text *commune, *communereference;

  commune = PG_GETARG_VARCHAR_P(0);
  communereference = PG_GETARG_VARCHAR_P(1);

  note_commune = note_pourcentage_seuil_total2(communereference,commune,notecommune,pourcentagecommune);

  max = notecommune*nombre_de_mots2(commune);

  PG_RETURN_INT32((200*note_commune)/max);
}

PG_FUNCTION_INFO_V1(index_derniermot);

// Trouve l'index du dernier mot de la chaine spécifiée.
//
// text *mot
//
// return int;
Datum index_derniermot(PG_FUNCTION_ARGS)
{
  int size,index,startindex,state,endindex,i;
  char c;
  text *mot;

  mot = PG_GETARG_VARCHAR_P(0);
  size = VARSIZE(mot)-VARHDRSZ;
  state = 0;
  startindex = endindex = -1;

  for(index=0;index<size;index++)
  {
    c = *(VARDATA(mot)+index);
    switch(state)
    {
      case 0: // départ
        switch(c)
        {
          case ' ':   state = 1; break;
          default: startindex=endindex=0; state=2; break;
        }
        break;
      case 1: // suite d'espaces
        switch(c)
        {
          case ' ': break;
          default: startindex = endindex = index; state = 2; break;
        }
        break;
      case 2: // suite de lettres
        switch(c)
        {
          case ' ': endindex=index-1; state=1; break;
          default: break;
        }
        break;        
    }
  }

  PG_RETURN_INT32(startindex);
}

// Trouve l'index du dernier mot de la chaine spécifiée.
//
// text *mot
//
// return int;
int index_derniermot2(text *mot)
{
  int size,index,startindex,state,endindex,i;
  char c;

  size = VARSIZE(mot)-VARHDRSZ;
  state = 0;
  startindex = endindex = -1;

  for(index=0;index<size;index++)
  {
    c = *(VARDATA(mot)+index);
    switch(state)
    {
      case 0: // départ
        switch(c)
        {
          case ' ':   state = 1; break;
          default: startindex=endindex=0; state=2; break;
        }
        break;
      case 1: // suite d'espaces
        switch(c)
        {
          case ' ': break;
          default: startindex = endindex = index; state = 2; break;
        }
        break;
      case 2: // suite de lettres
        switch(c)
        {
          case ' ': endindex=index-1; state=1; break;
          default: break;
        }
        break;
    }
  }

  return (startindex);
}

PG_FUNCTION_INFO_V1(dernier_mot);

// Trouve le dernier mot de la chaine.
// la chaine derniermot doit être suffisamment longue.
//
// text *mot,text *derniermot
//
// return int
Datum dernier_mot(PG_FUNCTION_ARGS)
{
  int size,index,startindex,state,endindex,i;
  char c;
  text *mot, *derniermot;

  mot = PG_GETARG_VARCHAR_P(0);
  derniermot = PG_GETARG_VARCHAR_P(1);

  size = VARSIZE(mot)-VARHDRSZ;
  
  state = 0;
  startindex = endindex = -1;

  for(index=0;index<size;index++)
  {
    c = *(VARDATA(mot)+index);
    switch(state)
    {
      case 0: // départ
        switch(c)
        {
          case ' ':   state = 1; break;
          default: startindex=endindex=0; state=2; break;
        }
        break;
      case 1: // suite d'espaces
        switch(c)
        {
          case ' ': break;
          default: startindex = endindex = index; state = 2; break;
        }
        break;
      case 2: // suite de lettres
        switch(c)
        {
          case ' ': endindex=index-1; state=1; break;
          default: break;
        }
        break;        
    }
  }
  if (state==2) endindex = size-1;

  if (startindex==-1)
  {
    *((int*)derniermot) = VARHDRSZ;
    PG_RETURN_INT32(0);
  }
  else
  {
    *((int*)derniermot) = endindex-startindex+1+VARHDRSZ;
    for(index=startindex,i=0;index<=endindex;)
    {
      *(VARDATA(derniermot)+i++) = *(VARDATA(mot)+index++);
    }

    PG_RETURN_INT32(endindex-startindex+1);
  }
}

// Trouve le dernier mot de la chaine.
// la chaine derniermot doit être suffisamment longue.
//
// text *mot,text *derniermot
//
// return int
int dernier_mot2(text *mot,text *derniermot)
{
  int size,index,startindex,state,endindex,i;
  char c;
  size = VARSIZE(mot)-VARHDRSZ;

  state = 0;
  startindex = endindex = -1;

  for(index=0;index<size;index++)
  {
    c = *(VARDATA(mot)+index);
    switch(state)
    {
      case 0: // départ
        switch(c)
        {
          case ' ':   state = 1; break;
          default: startindex=endindex=0; state=2; break;
        }
        break;
      case 1: // suite d'espaces
        switch(c)
        {
          case ' ': break;
          default: startindex = endindex = index; state = 2; break;
        }
        break;
      case 2: // suite de lettres
        switch(c)
        {
          case ' ': endindex=index-1; state=1; break;
          default: break;
        }
        break;        
    }
  }
  if (state==2) endindex = size-1;

  if (startindex==-1)
  {
    *((int*)derniermot) = VARHDRSZ;
    return (0);
  }
  else
  {
    *((int*)derniermot) = endindex-startindex+1+VARHDRSZ;
    for(index=startindex,i=0;index<=endindex;)
    {
      *(VARDATA(derniermot)+i++) = *(VARDATA(mot)+index++);
    }

    return (endindex-startindex+1);
  }
}
