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

/**
 * Référence d'un type de voie.
 * @author jmoquet
 */
public class RefTypeVoie extends RefCle
{
    private String motAbbrevie = null;
    private boolean aEteAbbrevie = false;

    /**
     * Permet de savoir si le type de voie a été abbrévié.
     * @return
     */
    public boolean aEteAbbrevie()
    {
        return aEteAbbrevie;
    }

    /**
     * Permet d'informer la référence que le type de voie concerné a été abbrévié.
     * @param aEteAbbrevie
     */
    public void definitSiAEteAbbrevie(boolean aEteAbbrevie)
    {
        this.aEteAbbrevie=aEteAbbrevie;
    }

    /**
     * Obtient le mot en lequel le type de voie a été abbrévié.
     * @return
     */
    public String obtientMotAbbrevie()
    {
        return motAbbrevie;
    }
    
    /**
     * Définit le mot en lequel le type de voie a été abbrévié.
     * @param motAbbrevie
     */
    public void definitMotAbbrevie(String motAbbrevie)
    {
        this.motAbbrevie=motAbbrevie;
    }
    
    /**
     * Construit une nouvelle référence de type de voie.
     * @param mot
     * @param cle
     * @param index
     * @param chaineOriginale
     * @param categorie
     */
    public RefTypeVoie(String mot,Mot cle,int index,String chaineOriginale,CategorieMot categorie)
    {
        super(mot,cle,index,chaineOriginale,categorie);
    }

    /**
     * Construit une nouvelle référence de type de voie.
     * @param mot
     * @param index
     */
    public RefTypeVoie(String mot,int index)
    {
        super(mot,index);
    }
}
