/*
 * 16-08-07
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
 * Etat et paramètres d'un processus.
 * @author jmoquet
 */
public class Processus
{
    public int numero;
    public int version;
    public String name;
    public String[] parametres;
    
    /**
     * Etat du processus en cour, la première chaine doit être l'état du processus:
     * <ul>
     * <li>ATTENTE</li>
     * <li>EN COURS</li>
     * <li>TERMINE</li>
     * <li>ERREUR</li>
     * </ul>
     * Les chaines suivantes peuvent être une description, des seuils à atteindre, ou des
     * valeurs de compteurs en cour.
     */
    public String[] state = new String[]{"ATTENTE"};
    public String[] connection1;
    public String[] connection2;
    public ArrayList<String> resultat = new ArrayList<String>();
    public boolean finished = false;
    public boolean stop = false;
    
    /**
     * Permet d'obtenir l'état du processus.<br>
     * @return Le tableau en retour dépend de la nature de l'opération.<br>
     * La première chaine consiste en le nom du processus.<br>
     * Les chaines suivantes correspondent au contenu de state.<br>
     * Lorsque l'état est TERMINE ou ERREUR, le contenu de getResults est ajouté à getState.
     */
    public String[] getState()
    {
        String[] res = null;
        if (finished || state[0].compareTo("TERMINE")==0 || state[0].compareTo("ERREUR")==0)
        {
            res = new String[state.length+resultat.size()+2];
            res[0] = "1";
            res[1] = name;
            for(int i=0;i<state.length;i++)
                res[2+i] = state[i];
            for(int i=0;i<resultat.size();i++)
                res[2+i+state.length] = resultat.get(i);
        }
        else
        {
            res = new String[state.length+2];
            res[0] = "1";
            res[1] = name;
            for(int i=0;i<state.length;i++)
                res[2+i] = state[i];
        }
        
        return res;
    }
    
    /**
     * Obtient un bilan des opérations effectuées.<br>
     * Ce résultat est aussi ajouté à getState lorsque l'état est TERMINE ou ERREUR.
     */
    public ArrayList<String> getResults()
    {
        return resultat;
    }
}
