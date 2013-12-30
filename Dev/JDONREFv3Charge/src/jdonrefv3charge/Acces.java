/**
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

import java.util.Calendar;
import java.util.Random;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 * Classe générique pour la gestion des fréquences d'accès à JDONREF.<br>
 * Les ryhmes d'accès sont :
 * <table border=1>
 * <tr><td>Regulier</td>
 * <td>Les accès sont effectués régulièrement.</td>
 * <td>Deux couples de paramètres:
 * <ul>
 * <li>saisie_min</li>
 * <li>saisie_max</li>
 * <li>intervalle_min</li>
 * <li>intervalle_max</li>
 * </ul>
 * </td>
 * </tr>
 * <tr><td>Salves</td><td>Les accès sont effectués de manière regroupés.</td>
 * <td> 4 couples de paramètres
 * <ul>
 * <li>saisie_min</li>
 * <li>saisie_max</li>
 * <li>quantite_min</li>
 * <li>quantite_max</li>
 * <li>salve_min</li>
 * <li>salve_max</li>
 * <li>intervalle_min</li>
 * <li>intervalle_max</li>
 * </ul>
 * </td>
 * </tr>
 * <tr><td>Masse</td><td>Les accès sont effectués de manière regroupés, sans arrêt.</td>
 * <td> Deux couples de paramètres:
 * <ul>
 * <li>quantite_min</li>
 * <li>quantite_max</li>
 * <li>intervalle_min</li>
 * <li>intervalle_max</li>
 * </ul>
 * </td>
 * </tr>
 * <tr><td>Continu</td><td>Les acccès sont effectués sans arrêt.</td>
 * <td>Aucun paramètres</td>
 * </tr>
 * </table>
 * @author jmoquet
 */
public abstract class Acces
{
    RythmeAcces acces;
    Random r = new Random(Calendar.getInstance().getTimeInMillis());
    
    long saisiemin = 0;
    long saisiemax = 0;
    
    /**
     * Obtient un temps de saisie aléatoire.
     */
    public long obtientSaisie()
    {
        if (saisiemax==0) return 0;
        return Math.abs(r.nextLong())%(saisiemax-saisiemin+1)+saisiemin;
    }    
    
    /**
     * Lit un rythme d'accès à partir d'un flux xml.
     * @return null si le type n'est pas reconnu ou qu'une erreur est survenue.
     */
    public static Acces load(Element e)
    {
        Attribute type_attribute = e.getAttribute("type");
        String type_name = type_attribute.getValue();
        
        if (type_name.compareTo("regulier")==0)
        {
            AccesRegulier ar = new AccesRegulier();

            Element intervalle_saisie_min_e = e.getChild("saisie_min");
            if (intervalle_saisie_min_e==null) return null;
            Element intervalle_saisie_max_e = e.getChild("saisie_max");
            if (intervalle_saisie_max_e==null) return null;
            Element intervalle_min_e = e.getChild("intervalle_min");
            if (intervalle_min_e==null) return null;
            Element intervalle_max_e = e.getChild("intervalle_max");
            if (intervalle_max_e==null) return null;
            
            long intervalle_saisie_min_l, intervalle_saisie_max_l;
            long intervalle_min_l, intervalle_max_l;
            try
            {
                intervalle_saisie_min_l = Long.parseLong(intervalle_saisie_min_e.getValue());
                intervalle_saisie_max_l = Long.parseLong(intervalle_saisie_max_e.getValue());
                intervalle_min_l = Long.parseLong(intervalle_min_e.getValue());
                intervalle_max_l = Long.parseLong(intervalle_max_e.getValue());
            }
            catch(NumberFormatException nfe)
            {
                return null;
            }
            
            ar.saisiemin = intervalle_saisie_min_l;
            ar.saisiemax = intervalle_saisie_max_l;
            ar.frequencemin = intervalle_min_l;
            ar.frequencemax = intervalle_max_l;
            
            return ar;
        }
        else if (type_name.compareTo("salves")==0)
        {
            AccesSalves as = new AccesSalves();
            
            Element intervalle_saisie_min_e = e.getChild("saisie_min");
            if (intervalle_saisie_min_e==null) return null;
            Element intervalle_saisie_max_e = e.getChild("saisie_max");
            if (intervalle_saisie_max_e==null) return null;
            
            Element quantite_min_e = e.getChild("quantite_min");
            if (quantite_min_e==null) return null;
            Element quantite_max_e = e.getChild("quantite_max");
            if (quantite_max_e==null) return null;
            
            Element salve_min_e = e.getChild("salve_min");
            if (salve_min_e==null) return null;
            Element salve_max_e = e.getChild("salve_max");
            if (salve_max_e==null) return null;
            
            Element intervalle_min_e = e.getChild("intervalle_min");
            if (intervalle_min_e==null) return null;
            Element intervalle_max_e = e.getChild("intervalle_max");
            if (intervalle_max_e==null) return null;
            
            long intervalle_saisie_min_l, intervalle_saisie_max_l;
            long salve_min_l, salve_max_l;
            long intervalle_min_l, intervalle_max_l;
            int quantite_min_i, quantite_max_i;
            
            try
            {
                intervalle_saisie_min_l = Long.parseLong(intervalle_saisie_min_e.getValue());
                intervalle_saisie_max_l = Long.parseLong(intervalle_saisie_max_e.getValue());
                quantite_min_i = Integer.parseInt(quantite_min_e.getValue());
                quantite_max_i = Integer.parseInt(quantite_max_e.getValue());
                salve_min_l = Long.parseLong(salve_min_e.getValue());
                salve_max_l = Long.parseLong(salve_max_e.getValue());
                intervalle_min_l = Long.parseLong(intervalle_min_e.getValue());
                intervalle_max_l = Long.parseLong(intervalle_max_e.getValue());                
            }
            catch(NumberFormatException nfe)
            {
                return null;
            }
            
            as.saisiemin = intervalle_saisie_min_l;
            as.saisiemax = intervalle_saisie_max_l;
            as.frequencemin = intervalle_min_l;
            as.frequencemax = intervalle_max_l;
            as.salvemin = salve_min_l;
            as.salvemax = salve_max_l;
            as.quantitemin = quantite_min_i;
            as.quantitemax = quantite_max_i;
            
            return as;
        }
        else if (type_name.compareTo("masse")==0)
        {
            AccesMasse am = new AccesMasse();
            
            Element quantite_min_e = e.getChild("quantite_min");
            if (quantite_min_e==null) return null;
            Element quantite_max_e = e.getChild("quantite_max");
            if (quantite_max_e==null) return null;
            
            Element intervalle_min_e = e.getChild("intervalle_min");
            if (intervalle_min_e==null) return null;
            Element intervalle_max_e = e.getChild("intervalle_max");
            if (intervalle_max_e==null) return null;
            
            long intervalle_min_l, intervalle_max_l;
            int quantite_min_i, quantite_max_i;
            
            try
            {
                quantite_min_i = Integer.parseInt(quantite_min_e.getValue());
                quantite_max_i = Integer.parseInt(quantite_max_e.getValue());
                intervalle_min_l = Long.parseLong(intervalle_min_e.getValue());
                intervalle_max_l = Long.parseLong(intervalle_max_e.getValue());                
            }
            catch(NumberFormatException nfe)
            {
                return null;
            }
            
            am.saisiemin = 0;
            am.saisiemax = 0;
            am.frequencemin = intervalle_min_l;
            am.frequencemax = intervalle_max_l;
            am.quantitemin = quantite_min_i;
            am.quantitemax = quantite_max_i;
            
            return am;
        }
        else if (type_name.compareTo("continu")==0)
        {
            AccesContinu ac = new AccesContinu();
                        
            ac.saisiemin = 0;
            ac.saisiemax = 0;
            
            return ac;
        }
        
        return null;
    }
    
    /**
     * Obtient la quantité d'accès par salve.<br>
     * Cette quantité peut être aléatoire.
     * @return
     */
    public abstract int obtientQuantite();
    
    /**
     * Obtient le délai d'attente en ms entre chaque acces.<br>
     * Ce délai d'attente peut être aléatoire.
     */
    public abstract long obtientAttenteUnite();
    
    /**
     * Obtient le délai d'attente en ms entre chaque salve d'accès.<br>
     * Ce délai peut être aléatoire.
     */
    public abstract long obtientAttenteSalve();
}