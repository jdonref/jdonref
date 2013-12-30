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
package ppol.jdonref;

import java.io.File;
import java.io.FileFilter;

/**
 * Filtre les noms de fichier qui sont sous la forme jdonref-admin-processus-version-*.log.
 * @author jmoquet
 */
public class LogAdminFilter implements FileFilter
{
    int processus;
    
    /**
     * Crée un filtre pour le processus spécifié.
     * @param processus
     */
    public LogAdminFilter(int processus)
    {
        this.processus = processus;
    }
    
    /**
     * Extrait le processus 
     * @param name le nom du fichier de log du processus
     * @return le numéro du processus
     */
    public static int extraitProcessus(String name)
    {
        int idx = name.indexOf('-',14);
        if (idx==-1) return -1;
        if (idx==14) return -1;
        String processus = name.substring(14,idx);
        
        try
        {
            return Integer.parseInt(processus);
        }
        catch(NumberFormatException nfe)
        {
            return -1;
        }
    }
    
    /**
     * Extrait le numéro de version
     * @param name le nom du fichier de log du processus
     * @return le numéro de version du processus
     */
    public static int extraitVersion(String name)
    {
        int idx = name.indexOf('-',14);
        if (idx==-1) return -1;
        if (idx==14) return -1;
        if (idx>=name.length()-2) return 0;
        int idx2 = name.indexOf('-',idx+1);
        if (idx2==-1) return -1;
        if (idx2==idx+1) return -1;
        
        String processus = name.substring(idx+1,idx2);
        
        try
        {
            return Integer.parseInt(processus);
        }
        catch(NumberFormatException nfe)
        {
            return -1;
        }
    }
    
    /**
     * Retourne si le chemin vers le fichier est accepté.
     * @param pathname fichier à vérifier
     * @return vrai s'il est accepté
     */
    public boolean accept(File pathname)
    {
        String name = pathname.getName();
        
        if (!name.startsWith("jdonref-admin-")) return false;
        if (!name.endsWith(".log")) return false;
        
        if (extraitProcessus(name)!=processus) return false;
        if (extraitVersion(name)==-1) return false;
        
        return true;
    }
    
    public static void main(String[] args)
    {
        LogAdminFilter filter=new LogAdminFilter(0);
        if (filter.accept(new File("jdonref-admin-0-0-10.log")))
        {
            System.out.println("OK");
        }
        else
        {
            System.out.println("Echec");
        }
        if (filter.accept(new File("jdonref-admin-1-0-10.log")))
        {
            System.out.println("Echec");
        }
        else
        {
            System.out.println("OK");
        }
        if (filter.accept(new File("jdonref-user.log")))
        {
            System.out.println("Echec");
        }
        else
        {
            System.out.println("OK");
        }
        if (filter.accept(new File("jdonref-admin-0-0-10.txt")))
        {
            System.out.println("Echec");
        }
        else
        {
            System.out.println("OK");
        }
        if (filter.accept(new File("jdonref-admin-0-18-10.log")))
        {
            System.out.println("OK");
        }
        else
        {
            System.out.println("Echec");
        }
    }
}