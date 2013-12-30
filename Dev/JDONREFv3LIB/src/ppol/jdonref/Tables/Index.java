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

package ppol.jdonref.Tables;

import java.util.ArrayList;

/**
 * Référence un index d'une table.
 * @author jmoquet
 */
public class Index
{
    String nom;
    ArrayList<String> colonnes = new ArrayList<String>();
    String type = "btree";
    
    /**
     * Crée un index sans nom ni colonne, mais avec la technique btree.
     */
    public Index()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type=type;
    }
    
    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom=nom;
    }
    
    public void ajouteColonne(String nom)
    {
        colonnes.add(nom.trim());
    }
    
    public int obtientCompteColonnes()
    {
        return colonnes.size();
    }
    
    public String obtientColonne(int index)
    {
        return colonnes.get(index);
    }
    
    /**
     * Obtient la requête permettant de créer cet index, avec la technique choisie.<br>
     * Supporte les noms composés d'un shéma et du nom de la table.
     * @return
     */
    public String toString(String tableName)
    {
        tableName = GestionTables.formateNom(tableName);
        
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE INDEX \"");
        sb.append(getNom());
        sb.append("\" ON ");
        sb.append(tableName);
        sb.append(" USING "+type+" (");
        for(int i=0;i<colonnes.size();i++)
        {
            if (i>0)
                sb.append(",");
            sb.append(colonnes.get(i));
        }
        sb.append(")");
        return sb.toString();
    }
    
    /**
     * Obtient la requête permettant de supprimer l'index.
     * @param tableName
     * @return
     */
    public String dropString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP INDEX \"");
        sb.append(getNom());
        sb.append("\"");
        return sb.toString();
    }
    
    /**
     * Retourne si l'index r�f�rence la colonne sp�cifi�e.
     * @param nom
     * @return
     */
    public boolean contient(String nom)
    {
        for(int i=0;i<colonnes.size();i++)
        {
            if (colonnes.get(i).compareTo(nom)==0)
                return true;
        }
        return false;
    }
    
    /**
     * Compare les colonnes de l'index aux noms de colonnes spécifiées.
     * @param names
     * @return
     */
    public boolean compareTo(String[] names)
    {
        int compte=0;
        for(int i=0;i<names.length;i++)
        {
            if (!contient(names[i].trim()))
                return false;
        }
        return true;
    }
}