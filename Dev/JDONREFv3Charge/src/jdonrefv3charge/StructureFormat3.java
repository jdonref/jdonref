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
 * Représente une structure d'adresse sous la forme de six lignes.<br>
 * Cette structure peut être destructurée selon trois qualités différentes:
 * <ul><li>1. correctement structurée</li>
 *     <li>2. léger désordre: les mauvaises lignes sont utilisées</li>
 *     <li>3. désordre : les lignes sont fusionnées</li></ul>
 * @author jmoquet
 */
public class StructureFormat3 extends Structure
{
    /**
     * La probabilité de ne pas mélanger les lignes.
     */
    int proba1;
    
    /**
     * La probabilité de mélanger les lignes.
     */
    int proba2;

    @Override
    public Adresse altereStructure(Adresse a)
    {
        int sum2 = proba1+proba2;
        
        int choix = r.nextInt()%100;
        
        if (choix<proba1)
        {
            Adresse b = a.clone();
            b.structure = 3;
            return b;
        }
        if (choix<sum2)
        {
            Adresse b = a.melangeLignes(6);
            b.structure = 3;
            return b;
        }
        Adresse b = a.melangeElements(6);
        b.structure = 3;
        return b;
    }
    
    public void setProba(int index,int proba)
    {
        switch(index)
        {
            case 0:
                proba1 = proba;
                break;
            case 1:
                proba2 = proba;
                break;
        }
    }
}
