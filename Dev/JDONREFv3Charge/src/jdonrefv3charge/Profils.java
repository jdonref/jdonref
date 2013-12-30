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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

/**
 * Permet de gérer des profils d'accès à JDONREF.
 * @author jmoquet
 */
public class Profils
{
    ArrayList<Profil> profils = new ArrayList<Profil>();
    
    /**
     * Constructeur par défaut.
     */
    public Profils()
    {
    }
    
    /**
     * Charge le profil à partir d'une structure DOM XML.
     */
    public void load(Element e) throws Exception
    {
        List e_profils = e.getChildren("profil");
        
        for(int i=0;i<e_profils.size();i++)
        {
            Element e_profil = (Element) e_profils.get(i);
            Profil p = new Profil();
            p.load(e_profil);
            profils.add(p);
        }
    }
    
    /**
     * Charge tous les échantillons d'adresses concernés.
     * @param rep
     */
    public void loadAdresses(String rep) throws FileNotFoundException, IOException, Exception
    {
        for(int i=0;i<profils.size();i++)
            profils.get(i).loadAdresses(rep);
    }
}