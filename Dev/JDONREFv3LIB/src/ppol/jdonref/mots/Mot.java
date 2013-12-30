/*
 * Mot.java
 *
 * Created on 1 avril 2008, 17:46 not a joke
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

package ppol.jdonref.mots;

import java.util.ArrayList;

/**
 * Définit un mot qui peut avoir plusieurs abbreviations, officielle ou non.<br>
 * Dans la pratique, une seule de ces abbreviations sera officielle.<br>
 * Cette classe doit être alimentée en deux temps.<br>
 * Tout d'abord la méthode ajouteAbbreviationIndex peut être utilisée pour ajouter plusieurs références à des abbréviations.
 * Puis, la méthode ajouteAbbreviation peut être utilisée pour initialiser la classe avec les vrais objets.
 * Enfin, la méthode finInitialisation doit être appelée pour libérer la mémoire utilisée par l'initialisation.
 * @author jmoquet
 */
public class Mot
{
    private String nom = "";
    private ArrayList<Abbreviation> abbreviations = new ArrayList<Abbreviation>();
    private ArrayList<Boolean> officielles = new ArrayList<Boolean>();
    
    private ArrayList<Integer> abbreviationsindex = new ArrayList<Integer>();
    private ArrayList<Boolean> tempofficielles = new ArrayList<Boolean>();
    
    private ArrayList<CategorieMot> categories = new ArrayList<CategorieMot>();
    
    private ArrayList<Integer> lignes = new ArrayList<Integer>();
    
    /**
     * Optimisation de la méthode obtientAbbreviationOfficielle.
     * Doit être remis à null lorsqu'une abbréviation est ajoutée, ce qui est fait dans
     * la méthodes ajouteAbbreviation.
     */
    private Abbreviation abbreviationOfficielle = null;
    
    /** Creates a new instance of Mot */
    public Mot(String nom)
    {
        this.nom = nom;
    }
    
     /**
     * Efface les liens avec les abbreviations.<br>
     * utilisé avant de supprimer l'objet pour ne pas laisser de références cycliques.
     */
    public void clear()
    {
        abbreviations.clear();
        officielles.clear();
        if (abbreviationsindex!=null)
            abbreviationsindex.clear();
        else
            abbreviationsindex = new ArrayList<Integer>();
        if (tempofficielles!=null)
            tempofficielles.clear();
        else
            tempofficielles = new ArrayList<Boolean>();
        categories.clear();
        lignes.clear();
    }
    
    /**
     * Obtient le nombre de ligne à laquelle appartient ce mot.
     * @return 0 si il ne s'agit pas d'une clé.
     */
    public int obtientCompteLigne()
    {
        if (estDeLaCategorie(CategorieMot.Cle))
            return lignes.size();
        return 0;
    }
    
    /**
     * Obtient la ligne à laquelle appartient ce mot.
     * @return -1 si le mot n'a pas pour catégorie CategorieMot.Cle
     */
    public int obtientLigne(int index)
    {
        if (estDeLaCategorie(CategorieMot.Cle))
            return lignes.get(index).intValue();
        else
            return -1;
    }
    
    /**
     * Obtient la première ligne du mot s'il appartient à la catégorie Cle.
     * @return
     */
    public int obtientLigneDeCle()
    {
        if (obtientCompteLigne()>0)
        {
            int i=0;
            for(;i<categories.size();i++)
            {
                if (categories.get(i)==CategorieMot.Cle)
                {
                    return lignes.get(0).intValue();
                }
            }
        }
        return -1;
    }
    
    /**
     * Définit la ligne à laquelle appartient ce mot.<br>
     * Ne fonctionne que si le mot a pour catégorie CategorieMot.Cle.
     * 
     * Attention, dans la pratique seule la première ligne renseignée est utilisée.
     */
    public void ajouteLigne(int ligne)
    {
        if (estDeLaCategorie(CategorieMot.Cle))
            lignes.add(new Integer(ligne));
    }
    
    /**
     * Obtient si oui ou non le mot appartient à la catégorie spécifiée.
     */
    public boolean estDeLaCategorie(CategorieMot categorie)
    {
        for(int i=0;i<categories.size();i++)
        {
            if (categories.get(i)==categorie)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtient l'index de l'abbréviation officielle.
     * @return -1 si l'index n'est pas officielle.
     */
    private int obtientIndexAbbreviationOfficielle()
    {
        for(int i=0;i<officielles.size();i++)
            if (officielles.get(i).booleanValue())
                return i;
        return -1;
    }
    
    /**
     * Un mot est officiel si il dispose d'une abbréviation officielle.
     */
    public boolean estOfficiel()
    {
        return obtientIndexAbbreviationOfficielle()!=-1;
    }  
    
    /**
     * Retourne le nombre de caractères gagnés lorsque l'abbréviation est appliquée.
     * @param cut nombre de lettre à conserver si le nom n'a pas d'abbréviation.
     */
    public int obtientGainAbbreviation(int cut)
    {
        Abbreviation a = obtientAbbreviationOfficielle();
        if (a==null)
            return nom.length()-cut;
        else
            return nom.length()-a.obtientNom().length();
    }
    
    /**
     * Obtient l'abbréviation officielle du mot.<br>
     * @return null s'il n'a pas d'abbréviation officielle.
     */
    public Abbreviation obtientAbbreviationOfficielle()
    {
        if (abbreviationOfficielle==null)
        {
            int i = obtientIndexAbbreviationOfficielle();
            if (i==-1) return null;
            abbreviationOfficielle = abbreviations.get(i);
        }
        return abbreviationOfficielle;
    }
    
    /**
     * Définit l'abbréviation officielle de ce mot.<br>
     * N'est effectué que si l'abbréviation est une abbréviation de ce mot.
     * @param a
     */
    public void definitAbbreviationOfficielle(Abbreviation a)
    {
        int index;
        if (obtientAbbreviationOfficielle()!=null)
        {
            index = abbreviations.indexOf(abbreviationOfficielle);
            officielles.set(index,false);
        }
        index = abbreviations.indexOf(a);
        if (index>=0)
        {
            abbreviationOfficielle = a;
            officielles.set(index,true);
        }
    }
    
    /**
     * Obtient le nombre de catégories auquel appartient ce mot.
     */
    public int obtientCompteCategorie()
    {
        return categories.size();
    }
    
    /**
     * Obtient la catégorie du mot.
     */
    public CategorieMot obtientCategorie(int index)
    {
        return categories.get(index);
    }
    
    /**
     * Ajoute une catégorie.
     */
    public void ajouteCategorie(CategorieMot categorie)
    {
        categories.add(categorie);
    }
    
    /**
     * Retire une catégorie.
     * @param categorie
     */
    public void retireCategorie(CategorieMot categorie)
    {
        categories.remove(categorie);
    }
    
    /**
     * Obtient le nom de ce mot.
     */
    public String obtientNom()
    {
        return nom;
    }
    
    /**
     * Modifie le nom de ce mot.
     */
    public void definitNom(String nouveaunom)
    {
        nom = nouveaunom;
    }
    
    /**
     * Marque la fin de la phase d'initialisation.<br>
     * Les méthodes suivantes ne fonctionnent plus suite à l'exécution de cette méthode:
     * <ul><li>ajouteAbbreviationIndex</li>
     *     <li>obtientAbbreviationIndex</li>
     *     <li>obtientCompteAbbreviationIndex</li></ul>
     */
    public void finInitialisation()
    {
        abbreviationsindex = null;
        tempofficielles = null;
    }
    
    /**
     * Obtient le nombre d'index d'abbréviation.<br>
     * Cette méthode ne fonctionne plus suite à l'exécution de la méthode finInitialisation.
     * @returns -1 si la méthode ne fonctionne plus.
     */
    public int obtientAbbreviationIndex(int index)
    {
        if (abbreviationsindex!=null)
            return abbreviationsindex.get(index).intValue();
        return -1;
    }    
    
    /**
     * Obtient si l'abbréviation est temporairement officielle.
     * @returns toujours false si la méthode ne fonctionne plus.
     */
    public boolean estTempOfficielle(int index)
    {
        if (abbreviationsindex!=null)
            return tempofficielles.get(index).booleanValue();
        return false;
    }
    
    /**
     * Obtient le nombre d'index d'abbréviation.<br>
     * Cette méthode ne fonctionne plus suite à l'exécution de la méthode finInitialisation.
     * @returns -1 si la méthode ne fonctionne plus.
     */
    public int obtientCompteAbbreviationIndex()
    {
        if (abbreviationsindex!=null)
            return abbreviationsindex.size();
        return -1;
    }
    
    /**
     * Ajoute un nouvel index d'abbréviation.<br>
     * Cette méthode ne fonctionne plus suite à l'exécution de la méthode finInitialisation.
     */
    public void ajouteAbbreviationIndex(int index,boolean officielle)
    {
        if (abbreviationsindex!=null)
        {
            abbreviationsindex.add(new Integer(index));
            tempofficielles.add(new Boolean(officielle));
        }
    }
    
    /**
     * Obtient la quantité d'abbréviation de ce mot.
     */
    public int obtientCompteAbbreviation()
    {
        return abbreviations.size();
    }
    
    /**
     * Obtient l'abbreviation officielle de ce mot.
     */
    public Abbreviation obtientAbbreviation(int index)
    {
        return abbreviations.get(index);
    }
    
    /**
     * Definit l'abbréviation non officielle de ce mot.<br>
     * Aucune abbreviation ne peut être ajoutée si finInitialisation n'a pas été appelée.
     */
    public void ajouteAbbreviation(Abbreviation abbreviation,boolean officielle)
    {
        abbreviationOfficielle = null;
        abbreviations.add(abbreviation);
        officielles.add(officielle);
    }
    
    /**
     * Obtient l'index de l'abbréviation spécifiée.
     * @param a
     * @return
     */
    public int indexOf(Abbreviation a)
    {
        for(int i=0;i<abbreviations.size();i++)
        {
            if (abbreviations.get(i).obtientNom().compareTo(a.obtientNom())==0)
                return i;
        }
        return -1;
    }
    
    /**
     * Retire une abbréviation
     * @param abbreviation
     */
    public void retireAbbreviation(Abbreviation a)
    {
        int index = indexOf(a);
        if (index!=-1)
        {
            if (abbreviationOfficielle!=null && abbreviationOfficielle.obtientNom().compareTo(a.obtientNom())==0)
                abbreviationOfficielle = null;

            abbreviations.remove(index);
            officielles.remove(index);
        }
    }
    
    /**
     * Obtient si l'abbréviation est officielle ou non.
     */
    public boolean estOfficielle(int index)
    {
        return officielles.get(index).booleanValue();
    }
    
    /**
     * Obtient si le mot correspond ou s'il correspond à une abbréviation.
     * @param mot
     * @return
     */
    public boolean est(String mot)
    {
        if (this.nom.compareTo(mot)==0)
            return true;
        for(int i=0;i<abbreviations.size();i++)
        {
            if (abbreviations.get(i).obtientNom().compareTo(mot)==0)
                return true;
        }
        return false;
    }
}