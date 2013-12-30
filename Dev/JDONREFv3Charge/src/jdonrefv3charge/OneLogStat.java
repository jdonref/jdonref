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
package jdonrefv3charge;

/**
 * Permet de comptabiliser une statistique.
 * @author jmoquet
 */
public class OneLogStat
{
    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;
    int count = 0;
    long sum = 0;
    int count_sup1000 = 0;
    
    /**
     * Remet à zéro les statistiques.
     */
    public void reset()
    {
        min = Long.MAX_VALUE;
        max = Long.MIN_VALUE;
        count = 0;
        count_sup1000 = 0;
        sum = 0;
    }
    
    /**
     * Retourne la représentation textuelle des statistiques.
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Min:");
        if (count==0)
            sb.append("-1");
        else
            sb.append(min);
        sb.append(" Max:");
        if (count==0)
            sb.append("-1");
        else
            sb.append(max);
        sb.append(" Moy:");
        sb.append(getMoyenne());
        sb.append(" Count:");
        sb.append(count);
        sb.append(" Count>1000:");
        sb.append(count_sup1000);
        return sb.toString();
    }
    
    public String[] toArray(String name){
        final String[] arrayRet = new String[6];
        arrayRet[0] = name;
        arrayRet[1] = (count==0) ? "-1" : String.valueOf(min);
        arrayRet[2] = (count==0) ? "-1" : String.valueOf(max);
        arrayRet[3] = String.valueOf(getMoyenne());
        arrayRet[4] = String.valueOf(count);
        arrayRet[5] = String.valueOf(count_sup1000);
        
        return arrayRet;
    }
    
    /**
     * Obtient la moyenne.<br>
     * @return -1 si le compte est nul.
     */
    public long getMoyenne()
    {
       if (count!=0)
        return sum/count;
       else
        return -1;
    }
    
    /**
     * Constructeur par défaut.
     */
    public OneLogStat()
    {
    }
    
    /**
     * Comptabilise une donnée de plus.
     */
    public void add(long valeur)
    {
        count++;
        sum += valeur;
        if (valeur<min) min=valeur;
        if (valeur>max) max=valeur;
        if (valeur>1000) count_sup1000++;
    }
}