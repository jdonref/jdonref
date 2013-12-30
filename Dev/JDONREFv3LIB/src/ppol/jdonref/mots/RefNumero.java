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
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes. */
package ppol.jdonref.mots;

import ppol.jdonref.Algos;

/**
 * Utilisé pour référencer un numéro dans une chaine.
 * @author Julien
 */
public class RefNumero extends RefCle
{
    int indexrepetition = -1;
    String numero = null;
    String repetition = null;
    
    /**
     * 
     */
    public RefNumero(String mot,int index,String chaineOriginale,CategorieMot categorie)
    {
        super(mot,(Mot)null,index,chaineOriginale,categorie);
    }
    
    /**
     * Définit le numéro 
     * @param numero
     * @return
     */
    public void definitNumero(String numero)
    {
        this.numero = numero;
    }
    
    /**
     * Obtient le numero
     * @param numero
     * @return
     */
    public String obtientNumero()
    {
        return numero;
    }
    
    /**
     * Obtient le numéro normalisé.<br>
     * Les zéros inutiles sont supprimés.
     * @return
     */
    public String obtientNumeroNormalise()
    {
        return Algos.supprimeZerosInutiles(numero);
    }

    /**
     * Obtient l'index de la répétition dans la chaine de référence.
     * @return
     */
    public int obtientIndexRepetition()
    {
        return indexrepetition;
    }

    /**
     * Définit l'index de la répétition dans la chaine de référence.
     * @param indexrepetition
     */
    public void definitIndexrepetition(int indexrepetition)
    {
        this.indexrepetition = indexrepetition;
    }
    
    /**
     * Définit que le numéro n'a pas de répétition
     * @param repetition
     */
    public void definitRepetition()
    {
        if (this.repetition!=null)
        {
            this.mot = numero;
        }
        this.repetition = null;
        this.indexrepetition = -1;
    }
    
    /**
     * Définit la répétition
     * @param repetition null si le numéro n'a pas de répétition
     */
    public void definitRepetition(String repetition)
    {
        this.repetition = repetition;
    }
    
    /**
     * Obtient la répétition.
     * @return null si le numéro n'a pas de répétition
     */
    public String obtientRepetition()
    {
        return repetition;
    }
    
    /**
     * Obtient la répétition normalisée.
     * @return
     */
    public char obtientRepetitionNormalise()
    {
        if (repetition==null) return (char)0;
        if (repetition.length()==9) return 'C';
        return repetition.charAt(0);
    }

    @Override
    /**
     * Décale l'index de la chaine et de la répétition.
     */
    public void decale(int nb)
    {
        super.decale(nb);
        if (repetition!=null)
            indexrepetition -= nb;
    }
}
