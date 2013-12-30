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

import java.util.Hashtable;
import java.util.List;
import org.jdom.Element;

/**
 * Permet de gérer des modèles.
 * @author jmoquet
 */
public class Modeles
{
    Hashtable<String,Modele> modeles = new Hashtable<String,Modele>();
    
    /**
     * Constructeur par défaut.
     */
    public Modeles()
    {
    }
    
    /**
     * Permet de charger la liste des modèles à partir d'une représentation DOM XML.
     */
    public void load(Element e) throws Exception
    {
        List e_modeles = e.getChildren("modele");
        for(int i=0;i<e_modeles.size();i++)
        {
            Modele m = new Modele();
            m.load((Element) e_modeles.get(i));
            modeles.put(m.nom,m);
        }
    }
    
    /**
     * Obtient le modèle désigné par son nom. 
     * @param modele
     * @return Null si le modèle n'existe pas.
     */
    public Modele getModele(String modele)
    {
        return modeles.get(modele);
    }
}
