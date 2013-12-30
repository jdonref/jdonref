/*
 * Abbreviation.java
 *
 * Created on 24 avril 2008, 09:11
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
 * Une abbréviation et une liste de mots pour laquelle elle est une abbréviation.<br>
 * A chaque mot peut être défini une priorité. La priorité la plus basse désigne le 
 * mot choisi en cas d'ambiguité.
 * @author jmoquet
 */
public class Abbreviation
{
    /**
     * La prioriété maximale qui peut être utilisée.<br>
     * Elle est égale à Integer.MAX_VALUE.
     */
    public final static int PRIORITE_MAX = Integer.MAX_VALUE;
    
    private String nom = null;
    private ArrayList<Mot> mots = new ArrayList<Mot>();
    private ArrayList<Integer> motsindex = new ArrayList<Integer>();
    private ArrayList<Integer> priorites = new ArrayList<Integer>();
    
    // Le mot qui a la plus petite priorité.
    private int motPrefere = -1;
    
    /** Creates a new instance of Abbreviation */
    public Abbreviation(String nom)
    {
        this.nom = nom;
    }
    
    /**
     * Casse les liens avec les mots.<br>
     * Utilisé avant de supprimer l'objet pour ne pas laisser de références cycliques.
     */
    public void clear()
    {
        mots.clear();
        motPrefere = -1;
        if (motsindex!=null)
            motsindex.clear();
    }

    /**
     * Obtient le mot préféré de la catégorie spécifiée.<br>
     * L'abbréviation doit être finalisée.
     * 
     * Si aucun mot préféré n'a été choisis (tous de catégorie maximale, 
     * un mot préféré arbitraire de cette catégorie est retourné).
     * 
     * @param categorie
     * @return
     */
    public Mot obtientMotPrefere(CategorieMot categorie)
    {
        int min = Integer.MAX_VALUE;
        Mot motPrefereTrouve = null;
        for(int i = 0; i < mots.size(); i++)
        {
            if (mots.get(i).estDeLaCategorie(categorie) && priorites.get(i).intValue()<=min)
            {
                min = priorites.get(i).intValue();
                motPrefereTrouve = mots.get(i);
            }
        }
        return motPrefereTrouve;
    }
    
    /**
     * Obtient le mot qui a la plus petite priorité.<br>
     * @return null si plusieurs mots n'ont aucune priorité.
     */
    public Mot obtientMotPrefere()
    {
        if (motPrefere!=-1)
            return mots.get(motPrefere);
        else
            return null;
    }
    
    /**
     * Retourne l'index du mot préféré de cette abbréviation.
     * @return -1 si l'abbréviation n'a pas de priorité définie pour ses mots.
     */
    public int obtientPrioriteMot(int index)
    {
        return priorites.get(index).intValue();
    }
    
    /**
     * définit l'index du mot préféré de cette abbréviation.
     * @return -1 si l'abbréviation n'a pas de priorité définie pour ses mots.
     */
    public void definitPrioriteMot(int index,int priorite)
    {
        priorites.set(index,new Integer(priorite));
        if (priorite!=PRIORITE_MAX)
        {
            if (motPrefere==-1 || priorite<priorites.get(motPrefere).intValue())
                motPrefere=index;
        }
    }
    
    /**
     * Retourne vrai si l'abbréviation est officielle.<br>
     * Une abbréviation est officielle si elle est l'abbréviation officielle d'au moins un mot.
     * @return
     */
    public boolean estOfficielle()
    {
        for(int i=0;i<mots.size();i++)
        {
            Mot m = mots.get(i);
            if (m.obtientAbbreviationOfficielle()==this)
                return true;
        }
        return false;
    }
    
    /**
     * Retourne vrai si l'abbréviation a un mot de la catégorie spécifiée.<br>
     * Si la catégorie testée est CategorieMot.Tous retourne vrai.<br>
     * Si la catégorie testée est CategorieMot.Vide retourne false.<br>
     * @param cm
     * @return
     */
    public boolean estAbbreviationDeCategorie(CategorieMot cm)
    {
        if (cm==CategorieMot.Tous) return true;
        if (cm==CategorieMot.Vide) return false;
        for(int i=0;i<mots.size();i++)
        {
            if (mots.get(i).estDeLaCategorie(cm))
            {
               return true;
            }
        }
        return false;        
    }
    
    /**
     * Retourne vrai si il s'agit d'une abbréviation de plusieurs type de voie.
     */
    public boolean estAbbreviationDePlusieursTypeDeVoie()
    {
        int count = 0;
        for(int i=0;i<mots.size();i++)
        {
            if (mots.get(i).estDeLaCategorie(CategorieMot.TypeDeVoie))
            {
                count++;
                if (count==2)
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Obtient le nom de cette abbréviation.
     */
    public String obtientNom()
    {
        return nom;
    }
    
    /**
     * Définit le nouveau nom de cette abbréviation.
     * @param nouveaunom
     */
    public void definitNom(String nouveaunom)
    {
        nom = nouveaunom;
    }
    
    /**
     * Obtient le nombre d'index de mots.<br>
     * Utilisé lors de l'initialisation.<br>
     * Ne fonctionne plus suite à un appel à finInitialisation().
     * @returns -1 si la méthode ne fonctionne plus.
     */
    public int obtientCompteMotIndex()
    {
        if (motsindex!=null)
            return motsindex.size();
        else
            return -1;
    }
    
    /**
     * Obtient l'index d'index spécifié.<br>
     * Utilisé lors de l'initialisation.<br>
     * Ne fonctionne plus suite à un appel à finInitialisation().
     * @returns -1 si la méthode ne fonctionne plus.
     */
    public int obtientMotIndex(int index)
    {
        if (motsindex!=null)
        {
            return motsindex.get(index).intValue();
        }
        else
            return -1;
    }
    
    /**
     * Ajoute l'index d'un mot.<br>
     * Utilisé lors de l'initialisation.<br>
     * La priorité maximale est d�termin�e par Abbreviation.PRIORITE_MAX.
     * Ne fonctionne plus suite à un appel à finInitialisation().
     */
    public void ajouteMotIndex(int index,int priorite)
    {
        if (motsindex!=null)
        {
            motsindex.add(new Integer(index));
            priorites.add(new Integer(priorite));
            
            if (priorite!=PRIORITE_MAX)
            {
                if (motPrefere==-1 || priorite<priorites.get(motPrefere).intValue())
                {
                    motPrefere = priorites.size()-1;
                }
            }
        }
    }
    
     /**
     * Ajoute l'index d'un mot.<br>
     * Utilisé lors de l'initialisation.<br>
     * La priorité utilisée est alors la priorité maximale Abbreviation.PRIORITE_MAX.<br>
     * Ne fonctionne plus suite à un appel à finInitialisation().
     */
    public void ajouteMotIndex(int index)
    {
        if (motsindex!=null)
        {
            motsindex.add(new Integer(index));
            priorites.add(new Integer(Abbreviation.PRIORITE_MAX));
        }
    }
    
    /**
     * Met fin à la phase d'initialisation et libère les ressources attribuées à cet usage.<br>
     * Les méthodes suivantes ne fonctionnent plus suite à son appel:
     * <ul><li>ajouteMotIndex</li>
     *     <li>obtientCompteMotIndex</li>
     *     <li>obtientMotIndex</li></ul>
     */
    public void finInitialisation()
    {
        motsindex = null;
    }

    /**
     * Ajoute un mot dont l'abbréviation en est une.<br>
     * Si sa priorité n'a pas été définie durant l'initialisation, la priorité maximale est utilisée.
     */
    public void ajouteMot(Mot mot)
    {
        if (mots.size()==priorites.size())
            priorites.add(new Integer(Abbreviation.PRIORITE_MAX));
        mots.add(mot);
    }
    
    /**
     * Ajoute un mot dont l'abbréviation en est une.<br>
     */
    public void ajouteMot(Mot mot,int priorite)
    {
        if (mots.size()==priorites.size())
            priorites.add(new Integer(priorite));
        else
            definitPrioriteMot(mots.size(),priorite);
        mots.add(mot);
        
        if (priorite!=PRIORITE_MAX)
        {
            if (motPrefere==-1 || priorite<priorites.get(motPrefere).intValue())
            {
                motPrefere=priorites.size()-1;
            }
        }
    }
    
    /**
     * Obtient l'index du mot qui à le même nom.<br>
     * @param m
     * @return
     */
    public int indexOf(Mot m)
    {
        for(int i=0;i<mots.size();i++)
        {
            if (mots.get(i).obtientNom().compareTo(m.obtientNom())==0)
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Retire un mot dont l'abbréviation en est une.
     */
    public void retireMot(Mot m)
    {
       int index = indexOf(m);
       if (index!=-1)
       {
           if (motPrefere==index)
               motPrefere = -1;
           else if (motPrefere>index)
               motPrefere--;
           mots.remove(index);
           priorites.remove(index);
       }
    }
    
    /**
     * Obtient le nombre de mots dont l'abbréviation en est une.
     */
    public int obtientCompteMot()
    {
        return mots.size();
    }
    
    /**
     * Obtient le mot d'index spécifié.
     */
    public Mot obtientMot(int index)
    {
        return mots.get(index);
    }
}
