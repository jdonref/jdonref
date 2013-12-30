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

import java.util.ArrayList;

/**
 * Reprأ©sent un noeud d'un arbre.
 * @author jmoquet
 */
public class Noeud
{
    /**
     * La valeur de ce noeud.
     */
    protected char c;
    
    /**
     * L'objet de ce noeud.
     */
    protected Object objet;
    
    /**
     * Les fils de ce noeud.
     */
    protected ArrayList<Noeud> fils = new ArrayList<Noeud>();
    
    /**
     * Obtient la valeur de ce fils.
     * @return
     */
    public char obtientValeur()
    {
        return c;
    }
    
    /**
     * Dأ©finit la valeur de ce noeud.
     * @param c
     */
    public void definitValeur(char c)
    {
        this.c = c;
    }
    
    /**
     * Représente le noeud d'un arbre.
     */
    public Noeud()
    {
    }
    
    /**
     * Crée une arborescence de noeud correspondant à chaine.
     * @param chaine
     */
    public Noeud(String chaine,Object objet)
    {
        this.c = chaine.charAt(0);
        if (chaine.length()==1)
            this.objet = objet;
        else
            fils.add(new Noeud(chaine.substring(1),objet));
    }
    
    /**
     * Obtient le nombre de fils de ce noeud.
     * @return
     */
    public int obtientCompteFils()
    {
        return fils.size();
    }
    
    /**
     * Obtient l'objet associé à ce noeud.
     * @return
     */
    public Object obtientObjet()
    {
        return objet;
    }
    
    /**
     * Définit l'objet de ce noeud.
     * @param o
     */
    public void definitObject(Object o)
    {
        this.objet = o;
    }
    
    /**
     * Obtient l'index du noeud de valeur donnée.
     * @param c
     * @param min
     * @param max
     * @return l'index ou -(min+1) de l'endroit oأ¹ l'insأ©rer.
     */
    private int indexOf(char c,int min,int max)
    {
        if (min==max)
        {
            Noeud n = fils.get(min);
            if (n.obtientValeur()==c) return min;
            if (n.obtientValeur()<c) return -(min+2);
            return -(min+1);
        }
        else if (min==max-1)
        {
            int indexmin = indexOf(c,min,min);
            if (indexmin>=0) return indexmin;
            int indexmax = indexOf(c,max,max);
            if (indexmax>=0) return indexmax;
            
            if (-indexmin-1==min)
            {
                return indexmin;
            }
            if (-indexmax-1==max+1)
            {
                return indexmax;
            }
            return -(max+1);
        }
        else
        {
            int indexmid = (min+max)/2;
            Noeud nmid = fils.get(indexmid);
            char cmid = nmid.obtientValeur();
            
            if (cmid==c) return indexmid;
            if (cmid>c) return indexOf(c,min,indexmid-1);
            else return indexOf(c,indexmid+1,max);
        }
    }
    
    /**
     * Obtient le fils correspondant à la valeur donnée.
     * @param c
     * @return
     */
    public Noeud obtientFils(char c)
    {
        if (fils.size()==0) return null;
        int index = indexOf(c,0,fils.size()-1);
        if (index>=0) return fils.get(index);
        return null;
    }
    
    /**
     * Obtient le fils d'index spécifié.
     */
    public Noeud obtientFils(int index)
    {
        return fils.get(index);
    }
    
    /**
     * Obtient la valeur du fils d'index spécifié
     * @param i
     * @return
     */
    public char obtientValeur(int i)
    {
        return fils.get(i).obtientValeur();
    }
    
    /**
     * Supprime un mot de l'arbre.
     * @param nom
     * @return true s'il faut supprimer le noeud courant.
     */
    public boolean supprimeFils(String nom)
    {
        if (nom.length()>0 && fils.size()>0)
        {
            char nomc = nom.charAt(0);
            int index = indexOf(nomc,0,fils.size()-1);
            
            if (index>=0)
            {
                Noeud n = fils.get(index);
                if (nom.length()==1)
                {
                    // Le mot est trouvé.
                    // La référence est supprimée.
                    n.objet = null;
                    // Si ce fils n'a pas de descendants, il est supprimé.
                    if (n.fils.size()==0)
                        fils.remove(index);
                }
                else
                if (nom.length()>1)
                {
                    // La recherche avance, mais le mot n'est pas encore trouvé.
                    if (n.supprimeFils(nom.substring(1)))
                    {
                        fils.remove(index);
                    }
                }
                
                // Vérifie s'il faut supprimer le noeud actuel.
                if (objet==null && fils.size()==0)
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Ajoute un fils au noeud.<br>
     * Ce fils peut contenir des fils.<br>
     * Les objets contenus par le noeud sont ajoutés à l'arborescence.
     * @param n
     * @return true si le fils a pu être ajouté. Cela peut arriver s'il est déjà présent.
     */
    public boolean ajouteFils(String nom,Object objet)
    {
        if (fils.size()==0)
        {
            fils.add(new Noeud(nom,objet));
            return true;
        }
        else
        {
            char cnouveau = nom.charAt(0);
            int index=indexOf(cnouveau,0,fils.size()-1);
            
            if (index>=0)
            {
                // Un noeud existe dأ©jأ  avec la mأھme valeur.
                Noeud n = fils.get(index);
                if (nom.length()==1)
                {
                    if (n.obtientObjet()==null)
                    {
                        n.definitObject(objet);
                        return true;
                    }
                    else
                        return false;
                }
                else
                {
                    return n.ajouteFils(nom.substring(1),objet);
                }
            }
            else
            {
                Noeud n = new Noeud(nom,objet);
                fils.add(-index-1,n);
                return true;
            }
        }
    }
    
    /**
     * Cherche le mot de l'arbre qui commence la chaine.<br>
     * Wholeword permet de savoir si le mot n'est pas directement suivi d'un caractère.<br>
     * Pour savoir s'il n'est pas directement précédé par un caractère, il faut effectuer
     * le test avant d'exécuter la méthode.<br>
     * @param index index à partir duquel commencer la recherche dans la chaine.
     * @param wholeword indique si le mot est suivi d'un espace ou de la fin de la chaine.
     * @return
     */
    public Object chercheDebut(String chaine,int index,boolean wholeword)
    {
        if (chaine.length()==0) return null;
        if (index>=chaine.length()) return null;
        
        char local_c = chaine.charAt(index);
        if (fils.size()==0)
        {
            if (!wholeword || chaine.length()==index || chaine.charAt(index)==' ')
                return this.objet;
            return null;
        }
        
        int idx = indexOf(local_c,0,fils.size()-1);
        
        if (idx<0) return null;

        Noeud n = fils.get(idx);
        
        Object o1 = n.obtientObjet();
        Object o2 = null;
        if (chaine.length()>index+1)
        {
            o2 = n.chercheDebut(chaine,index+1,wholeword);
        }

        if (o2!=null) return o2;
        
        if (o1!=null)
        {
            if (!wholeword || chaine.length()==index+1 || chaine.charAt(index+1)==' ')
                return o1;               
            else
                return null;
        }
        
        return o1;
    }
}