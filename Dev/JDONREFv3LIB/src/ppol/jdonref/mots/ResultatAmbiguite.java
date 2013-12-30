/*
 * ResultatAmbiguite.java
 *
 * Created on 1 avril 2008, 17:43 not a joke
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
 * Résultat d'une recherche d'ambiguité dans une chaine.
 * @author jmoquet
 */
public class ResultatAmbiguite
{
    ArrayList<Mot> mots = null;
    ArrayList<CategorieAmbiguite> aca = null;
    
    /** Creates a new instance of ResultatAmbiguite 
     * @throws GestionMotsException la taille des listes est différente.
     */
    public ResultatAmbiguite(ArrayList<Mot> mots,ArrayList<CategorieAmbiguite> aca) throws GestionMotsException
    {
        if (mots.size()!=aca.size())
            throw(new GestionMotsException("La taille des listes est diff�rente.",GestionMotsException.TAILLESDIFFERENTES));
        this.mots = mots;
        this.aca = aca;
    }
    
    /**
     * Obtient la quantité de mots dans le résultat.
     */
    public int obtientQuantite()
    {
        return mots.size();
    }
    
    /**
     * Obtient le mot à l'index spécifié.
     */
    public Mot obtientMot(int index)
    {
        return mots.get(index);
    }
    
    /**
     * Obtient la catégorie d'ambiguité à l'index spécifié.
     */
    public CategorieAmbiguite obtientCategorieAmbiguite(int index)
    {
        return aca.get(index);
    }
}