/*
 * Difference.java
 *
 * Created on 12 mars 2008, 09:05
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

package ppol.jdonref.Tables;

import java.util.ArrayList;

/**
 * Résultat de la différence entre deux tables.
 * La méthode obtientEtat retourne le résultat.
 * Il peut être Difference.EGALITE, DIFFERENCE.MANQUECOLONNES, ou DIFFERENCE.COLONNESERRONEES.
 * Les colonnes erronnees sont celles qui n'ont pas le bon type.
 * @author jmoquet
 */
public class Difference {
    /**
     * Il n'y a aucune différence.
     */
    public static final int EGALITE = 1;
    /**
     * Il manque des colonnes.
     */
    public static final int MANQUECOLONNES = 2;
    /**
     * Certaines colonnes n'ont pas le bon type.
     */
    public static final int COLONNESERRONEES = 4;
    
    private int etat;
    
    private ArrayList<Colonne> colonnesManquantes = new ArrayList();
    private ArrayList<Colonne> colonnesErronees = new ArrayList();
    
    /**
     * Obtient le r�sultat de la comparaison : EGALITE, MANQUECOLONNES ou COLONNESERRONEES.
     * Les r�sultats MANQUECOLONNE et COLONNESERRONEES peuvent �tre combin�es en un masque.
     */
    public int obtientEtat()
    {
        return etat;
    }
    
    /**
     * Obtient le nombre de colonnes manquantes.
     */
    public int obtientQuantitéColonnesManquantes()
    {
        return colonnesManquantes.size();
    }
    
    /**
     * Obtient le nombre de colonnes erronnees.
     */
    public int obtientQuantitéColonnesErronees()
    {
        return colonnesErronees.size();
    }
    
    /**
     * retourne la colonne manquante d'index indiqu�.
     */
    public Colonne obtientColonneManquante(int index)
    {
        return colonnesManquantes.get(index);
    }
    
    /**
     * retourne la colonne erronee d'index indiqu�.
     */
    public Colonne obtientColonneErronee(int index)
    {
        return colonnesErronees.get(index);
    }
    
    /**
     * Cr�e une nouvelle instance.
     * Les objets colonnesManquantes et colonnesErronees ne sont pas dupliqu�s.
     * Si colonnesManquantes ou colonnesErronees sont null, aucune colonnes manquante
     * ou aucune colonnes erronees n'est pr�sente dans l'instance.
     */
    public Difference(ArrayList<Colonne> colonnesManquantes,ArrayList<Colonne> colonnesErronees)
    {
        if (colonnesManquantes==null)
            this.colonnesManquantes = new ArrayList<Colonne>();
        else
            this.colonnesManquantes = colonnesManquantes;
        
        if (colonnesErronees==null)
            this.colonnesErronees = new ArrayList<Colonne>();
        else
            this.colonnesErronees = colonnesErronees;
        
        if (colonnesManquantes.size()==0 && colonnesErronees.size()==0)
        {
            etat = Difference.EGALITE;
        }
        else
        {
            etat = 0;
            if (colonnesManquantes.size()!=0)
                etat |= Difference.MANQUECOLONNES;
            if (colonnesErronees.size()!=0)
                etat |= Difference.COLONNESERRONEES;
        }
    }
}