/*
 * 15 juillet 2008.
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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Permet de gérer une liste de mots sous la forme d'un arbre.<br>
 * L'arbre est composé d'une arborescence de noeuds.
 * @author jmoquet
 */
public class Arbre extends Noeud
{    
    /**
     * Crée un arbre vide.
     */
    public Arbre()
    {
    }
    
    /**
     * Ajoute un mot et son objet.
     * @param mot
     * @param obj
     * @throws GestionMotsException si le mot est déjà présent.
     */
    public void ajoute(String mot,Object obj) throws GestionMotsException
    {
        if (mot==null || mot.length()==0)
            throw(new GestionMotsException("Un mot null ou vide ne peut être ajouté.",GestionMotsException.DONNEEINCORRECTE));
        if (!ajouteFils(mot,obj))
        {
            throw(new GestionMotsException("Le noeud ajouté est déjà présent.",GestionMotsException.NOEUDEXISTANT));
        }
    }
    
    /**
     * Supprime un mot de l'arbre.
     * @param mot
     */
    public void supprime(String mot)
    {
        supprimeFils(mot);
    }
    
    /**
     * Cherche un mot dans l'arbre qui correspondant à la chaine spécifiée.
     * @param mot
     * @param wholeword indique si le mot cherché est suivi d'un espace ou de la fin de la chaine.
     * @return null si aucun mot n'est trouvé.
     */
    public Object cherche(String chaine,int index,boolean wholeword)
    {
        return chercheDebut(chaine,index,wholeword);
    }
    
    /**
     * Tests de la classe arbre
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            System.out.println("Teste la classe arbre.");
            System.out.println("Crée un arbre avec les mots : ");
            System.out.println("BD;AVENUE;AV;BOULEVARD;PT RUE;PTE;PTE RUE;PTE AV;PTE BD");
            Arbre a = new Arbre();
            a.ajoute("BD", "BD");
            a.ajoute("AVENUE", "AVENUE");
            a.ajoute("AV", "AV");
            a.ajoute("BOULEVARD", "BOULEVARD");
            a.ajoute("PT RUE", "PT RUE");
            a.ajoute("PTE", "PTE");
            a.ajoute("PTE RUE", "PTE RUE");
            a.ajoute("PTE AV", "PTE AV");
            a.ajoute("PTE BD", "PTE BD");
            System.out.println("Recherche le mot : PT RUE");
            System.out.println(a.cherche("PT RUE", 0,true).toString());
            System.out.println("Cherche les mots non entiers dans la chaine 'PTE BDAVENUE PTE AV'");
            String chaine = "PTE BDAVENUE PTE AV";
            for(int i=0;i<chaine.length();i++)
            {
                Object o = a.cherche(chaine,i,false);
                if (o!=null)
                    System.out.println(o.toString());
            }
            System.out.println("Cherche les mots entiers dans la chaine 'PTE BDAVENUE PTE AV'");
            chaine = "PTE BDAVENUE PTE AV";
            for(int i=0;i<chaine.length();i++)
            {
                Object o = a.cherche(chaine,i,true);
                if (o!=null)
                    System.out.println(o.toString());
            }
            System.out.println("Supprime la référence à PTE AV, AV, BOULEVARD, PTE BD");
            a.supprime("PTE AV");
            a.supprime("AV");
            a.supprime("BOULEVARD");
            a.supprime("PTE BD");
            System.out.println("Cherche les mots non entiers dans la chaine 'PTE BD AVENUE PTE AV'");
            chaine = "PTE BD AVENUE PTE AV";
            for(int i=0;i<chaine.length();i++)
            {
                Object o = a.cherche(chaine,i,false);
                if (o!=null)
                    System.out.println(o.toString());
            }
        }
        catch(GestionMotsException ex)
        {
            Logger.getLogger(Arbre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}