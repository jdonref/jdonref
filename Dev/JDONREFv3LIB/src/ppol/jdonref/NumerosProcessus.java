/*
 * NumerosProcessus.java
 *
 * Created on 17 mars 2008, 14:36
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

/**
 * Permet de gérer l'attribution de numéros de processus.
 * @author jmoquet
 */
public class NumerosProcessus
{
    ArrayList<Integer> numeros = new ArrayList();
    ArrayList<Processus> processus = new ArrayList<Processus>();
    
    public NumerosProcessus()
    {
    }
    
    /**
     * Obtient le nombre de processus déjà affectés.
     */
    public int count()
    {
        return numeros.size();
    }
    
    /**
     * Obtient le numero de processus d'index spécifié.
     */
    public int numero(int index)
    {
        return numeros.get(index).intValue();
    }
    
    /**
     * Obtiens true si la liste contient le numéro spécifié.
     */
    public boolean contains(int numero)
    {
        return indexOf(numero)>=0;
    }
    
    /**
     * Retourne l'index du numéro trouvé.<br>
     * NON OPTIMISE
     * ou l'inverse -1 de l'endroit ou l'insérer.
     */
    private int indexOf(int numero)
    {
        for(int i=0;i<numeros.size();i++)
        {
            int j = ((Integer)numeros.get(i)).intValue();
            if (j==numero)
            {
                return i;
            }
            if (j>numero)
            {
                return -i-1;
            }
        }
        return -numeros.size()-1;
    }

    /**
     * Permet d'obtenir les paramètres d'un processus.
     * @return null si le processus n'est pas référencé.
     */
    public Processus obtientParametres(int numero)
    {
        int i = indexOf(numero);
        
        if (i<0) return null;
        
        return processus.get(i);
    }
    
    /**
     * Ajoute un numéro.
     * S'il était déjà présent, retourne false.
     */
    public boolean add(int numero,Processus parametres)
    {
        int index = indexOf(numero);
        
        if (index<0)
        {
            numeros.add(-(index+1),new Integer(numero));
            processus.add(parametres);
            return true;
        }
        return false;
    }
    
    /**
     * Obtient le numéro à l'index spécifié.
     */
    private int getNumber(int index)
    {
        return ((Integer)numeros.get(index)).intValue();
    }
    
    /**
     * Supprime le numero spécifié.
     * Retourne true s'il a été supprimé.
     */
    public boolean remove(int numero)
    {
        int index = indexOf(numero);
        
        if (index<0)
            return false;
        else
        {
            numeros.remove(index);
            processus.remove(index);
            return true;
        }
    }
    
    /**
     * Efface la liste.
     */
    public void clear()
    {
        numeros.clear();
    }
    
    /**
     * Obtient le plus petit entier positif qui ne fait pas partie de la liste.
     */
    public int getFree()
    {
        int i = 0;
        while(i<numeros.size())
        {
            if (getNumber(i)==i)
                i++;
            else
                return i;
        }
        return last()+1;
    }
    
    /**
     * Obtient le plus grand entier de la liste.
     * -1, si aucun entier n'est présent.
     */
    private int last()
    {
        if (numeros.size()==0) return -1;
        return ((Integer)numeros.get(numeros.size()-1)).intValue();
    }
    
    public static void main(String[] args)
    {
        System.out.println("Tests de la classe NumerosProcessus");
        System.out.println("");
        
        NumerosProcessus np = new NumerosProcessus();
        
        System.out.println("\r\nCherche un identifiant libre");
        System.out.println(np.getFree()+" est libre.");
        System.out.println("Ajoute cet identifiant");
        np.add(np.getFree(),null);
        System.out.println("\r\nCherche un identifiant libre");
        System.out.println(np.getFree()+" est libre.");
        
        System.out.println("Crée une liste et ajoute 0 5 3 2 7");
        
        np.add(0,null);
        np.add(5,null);
        np.add(3,null);
        np.add(2,null);
        np.add(7,null);
        
        System.out.println("\r\nCherche un identifiant libre");
        System.out.println(np.getFree()+" est libre.");
        System.out.println("Ajoute cet identifiant");
        np.add(np.getFree(),null);
        
        System.out.println("\r\nVérifie ce que contient la liste");
        for(int i=0;i<8;i++)
        {
            if (np.contains(i))
                System.out.println("La liste contient "+i);
            else
                System.out.println("La liste ne contient pas "+i);
        }
        
        System.out.println("\r\nSupprime les numéros 0 2 5 6");
        np.remove(0);
        np.remove(2);
        np.remove(5);
        np.remove(6);
        
        System.out.println("\r\nVérifie ce que contient la liste");
        for(int i=0;i<8;i++)
        {
            if (np.contains(i))
                System.out.println("La liste contient "+i);
            else
                System.out.println("La liste ne contient pas "+i);
        }
        
        System.out.println("\r\nCherche un identifiant libre");
        System.out.println(np.getFree()+" est libre.");
        System.out.println("Ajoute cet identifiant");
        np.add(np.getFree(),null);
        System.out.println("\r\nCherche un identifiant libre");
        System.out.println(np.getFree()+" est libre.");
    }
}