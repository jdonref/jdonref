/*
 * JDONREFConfigFileNameFilter.java
 *
 * Created on 24 february 2011, 21:45
 *
 * Version 2.2 – Février 2011
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
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes. */
package ppol.jdonref;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filtre permettant de retrouver le dossier contenant les fichiers de configuration de JDONREF.
 * 
 * Ce dossier doit contenir un fichier du nom JDONREFv<<numeroversion>>.txt
 * Si setVersion n'est pas utilisé, le fichier recherché est JDONREFv.txt
 * 
 * @author Julien
 */
public class JDONREFConfigFileNameFilter implements FilenameFilter
{
    String version = "";
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    /**
     * La valeur par défaut est "".
     * @return
     */
    public String getVersion()
    {
        return version;
    }
    
    /**
     * Accepte les fichiers dont le nom est JDONREFv<<numeroversion>>.txt
     * @param dir
     * @param name
     * @return
     */
    public boolean accept(File dir, String name)
    {
        String filename = "JDONREFv"+version+".txt";
        
        if (name.compareTo(filename)==0) return true;
        return false;
    }
}