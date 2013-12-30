/*
 * Version 2.4.0 – 2012
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
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

package jdonrefv3charge;

/**
 *
 * @author arochewi
 */
public class Payss extends AbstractItemsWithProbability
{
    /**
     * Obtient un nom de pays selon les probabilités définies.
     * @return null peut être retourné si la somme n'est pas 100.
     */
    public String obtientPays()
    {
        AbstractItemWithProbability item = obtientItem();
        return (item==null) ? null : ((Pays)item).pays;
    }

    /**
     * Retourne le pays d'index i
     * @param i
     * @return
     */
    public Pays getPays(int i)
    {
        return (Pays) items.get(i);
    }

    /**
     * Ajoute un pays aux items
     * @param pay
     */
    public void addPays(Pays pay)
    {
        items.add(pay);
    }

    @Override
    protected String getItemXmlDesignation()
    {
        return "pays";
    }

    @Override
    protected AbstractItemWithProbability getNewEmptyItem()
    {
        return new Pays();
    }

}