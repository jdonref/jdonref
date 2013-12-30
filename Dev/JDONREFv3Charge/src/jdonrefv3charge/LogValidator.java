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
 * Permet de savoir si une ligne de log doit être comptée ou pas.
 * @author jmoquet
 */
public class LogValidator
{
    String profil = null;
    char scenario = ' ';
    String modele = null;
    /**
     * S'il est définit, seules les lignes qui ont au plus le nombre de fautes indiquées sont comptées.
     */
    Integer nbfautes = null;
    Integer structure = null;
    String departement = null;
    Boolean ok = null;
    Integer codeerreur = null;
    
    /**
     * Constructeur par défaut.
     */
    public LogValidator()
    {
    }
    
    /**
     * Retourne vrai si la ligne doit être prise en compte.
     */
    public boolean compte(Log log)
    {
        if (profil!=null && log.profil.compareTo(profil)!=0) return false;
        if (scenario!=' ' && log.scenario!=scenario) return false;
        if (modele!=null && log.modele.compareTo(modele)!=0) return false;
        if (nbfautes!=null && log.nbfautes>nbfautes) return false;
        if (structure!=null && log.structure>structure) return false;
        if (departement!=null && log.departement.compareTo(departement)!=0) return false;
        if (ok!=null && ok.booleanValue()!=log.ok) return false;
        if (codeerreur!=null && codeerreur.intValue()!=log.codeerreur) return false;
        return true;
    }
    
    /**
     * Définit la valeur du champ.
     */
    public void set(String name,String valeur)
    {
        if (name.compareTo("profil")==0) this.profil = valeur;
        else if (name.compareTo("scenario")==0) this.scenario = valeur.charAt(0);
        else if (name.compareTo("modele")==0) this.modele = valeur;
        else if (name.compareTo("nbfautes")==0) this.nbfautes = Integer.parseInt(valeur);
        else if (name.compareTo("structure")==0) this.structure = Integer.parseInt(valeur);
        else if (name.compareTo("departement")==0) this.departement = valeur;
        else if (name.compareTo("ok")==0) this.ok = Boolean.parseBoolean(valeur);
        else if (name.compareTo("codeerreur")==0) this.codeerreur = Integer.parseInt(valeur);
    }

    /**
     * Charge l'objet avec les paramètres spécifiés.
     */
    public void load(String[] args)
    {
        for(int i=0;i<args.length;i++)
        {
            String[] data = args[i].split("=");
            set(data[0],data[1]);
        }
    }
}