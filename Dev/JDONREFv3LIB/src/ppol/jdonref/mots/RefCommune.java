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

import java.util.ArrayList;

/**
 * Référence une commune suite à une recherche.
 * @author jmoquet
 */
public class RefCommune extends RefCle
{
    private ArrayList<String> departements = new ArrayList<String>();
    private int fautes = 0;
    
    /**
     * Obtient le nombre de fautes.
     * @return
     */
    public int obtientFautes()
    {
        return fautes;
    }
    
    /**
     * Définit le nombre de fautes.
     * @param fautes -1 si le nombre de fautes n'est pas connu.
     */
    public void definitFautes(int fautes)
    {
        this.fautes = fautes;
    }
    
    /**
     * Obtient le code de département.
     * @return
     */
    public ArrayList<String> obtientCodesDepartement()
    {
        return departements;
    }
    
    /**
     * Définit le code de département
     */
    public void ajouteCodeDepartement(String codeDepartement)
    {
        if (!contientDepartement(codeDepartement))
            this.departements.add(codeDepartement);
    }
    
    /**
     * Ajoute si nécessaire les départements spécifiés.
     * @param obtientCodesDepartement
     */
    void ajouteCodesDepartement(ArrayList<String> codesDepartement)
    {
        for(int i=0;i<codesDepartement.size();i++)
            ajouteCodeDepartement(codesDepartement.get(i));
    }
    
    /**
     * Retourne vrai si un des départements de la commune contient le département spécifié.
     * @param departements
     */
    public boolean contientDepartement(String departement)
    {
        return this.departements.indexOf(departement)!=-1;
    }
    
    /**
     * Retourne vrai si un des départements de la commune contient un des départements spécifiés.
     * @param departements
     */
    public boolean contientDepartement(ArrayList<String> departements)
    {
        for(int i=0;i<departements.size();i++)
        {
            if (this.departements.indexOf(departements.get(i))!=-1)
                return true;
        }
        return false;
    }
    
    /**
     * Crée une nouvelle référence.
     */
    public RefCommune(String mot,Mot cle,int startindex,String chaineOriginale,CategorieMot categorie,int fautes)
    {
        super(mot,cle,startindex,chaineOriginale,categorie);
        this.fautes = fautes;
    }
}
