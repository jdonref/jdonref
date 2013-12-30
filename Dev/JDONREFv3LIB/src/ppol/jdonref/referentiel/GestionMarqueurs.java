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

package ppol.jdonref.referentiel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import ppol.jdonref.Tables.Colonne;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.Tables.DescriptionTable;
import ppol.jdonref.Tables.GestionTables;

/**
 * Permet de gérer la création, la suppression de marqueurs sur des tables.
 * @author Julien
 */
public class GestionMarqueurs
{
    final static String markername = "marker";
    
    static public int MARQUEUR_DEPARTEMENTS = 0;
    static public int MARQUEUR_COMMUNES = 1;
    static public int MARQUEUR_TRONCONS = 2;
    static public int MARQUEUR_VOIES = 3;
    static public int MARQUEUR_CODEPOSTAUX = 4;
    static public int MARQUEUR_ADRESSES = 5;    
    /**
     * Ajoute un marqueur à une table (colonne de type integer de nom markerXXX).
     * Si le nom est déjà pris, en essaye un autre.
     * Retourne le nom du marqueur utilisé.
     * @throws SQLException un problème est survenu durant la requête permettant d'obtenir la liste des colonnes de la table.
     * @throws SQLException Une erreur est survenue durant la requête d'ajout du marqueur.
     * @throws GestionReferentielException la table n'existe pas.
     */
    public static String ajouteMarqueur(String nom,Connection connection) throws SQLException, GestionReferentielException
    {
        boolean done = false;
        int number = 0;
        Colonne c = null;
        DescriptionTable description = GestionTables.obtientDescription(nom,connection);
        
        do
        {
            try
            {
                c = new Colonne(GestionMarqueurs.markername+number,"INTEGER",0);
            }
            catch(ColonneException ce)
            {
                // Ceci ne devrait pas arriver.
            }
            
            if (description.contient(c)==Colonne.DIFFERENT)
            {
                done = true;
            }
            else
                number++;
        } while(!done);
        
        GestionTables.ajouteColonne(nom,c,connection);
        
        return c.getNom();
    }
    
    /**
     *  Supprime le marqueur spécifié de la table spécifiée.
     *  Ne fait rien si le marqueur n'existait pas.
     *  Ne vérifie pas le type de la colonne supprimée.
     * @throws ColonneException Le nom de marqueur est null.
     * @throws SQLException problème lors de la requête de suppression de la colonne.
     */
    public static void supprimeMarqueur(String nom,String nomMarqueur,Connection connection) throws SQLException, ColonneException
    {
        Colonne c = new Colonne(nomMarqueur,"INTEGER",0);
        
        GestionTables.supprimeColonne(nom,c,connection);
    }

    /**
     * Crée un marqueur pour la table de troncon spécifiée.
     * @return le nom du marqueur ou une chaine vide si la table n'est pas trouvée.
     */
    public static String ajouteMarqueurTronconOrigine(String nomTableTroncon,Connection connectionOrigine) throws SQLException, GestionReferentielException
    {
        if (GestionTables.tableExiste(nomTableTroncon,connectionOrigine))
            return ajouteMarqueur(nomTableTroncon,connectionOrigine);
        else
            return "";
    }
    
    /**
     * Retire les marqueurs précédemment Crées.
     * Le tableau nomMarqueurs contient les noms des marqueurs pour les tables dans cet ordre:
     * <ul<li>departements</li>
     *     <li>communesEtArrondissements</li>
     *     <li>troncons</li>
     *     <li>voies</li>
     *     <li>adr_adresses_dpt</li></ul>
     * @param nomMarqueurs Les noms des marqueurs précédemment créées par ajouteMarqueursMaj
     * @param code_departement Le département concerné
     * @param flags permet de spécifier les tables à purger en utilisant des combinaisons des bits spécifiés par
     * GestionReferentiel.MAJ_XXX.
     * @param connectionDestination La connection au référentiel où les marqueurs ont été crées.
     */
    public static void supprimeMarqueursMaj(String[] nomMarqueurs,String code_departement,int flags,Connection connectionDestination) throws SQLException, ColonneException
    {
        if ((flags&GestionReferentiel.MAJ_DEPARTEMENTS)!=0 && nomMarqueurs[GestionMarqueurs.MARQUEUR_DEPARTEMENTS]!=null && nomMarqueurs[GestionMarqueurs.MARQUEUR_DEPARTEMENTS].length()>0)
            supprimeMarqueur("dpt_departements",nomMarqueurs[GestionMarqueurs.MARQUEUR_DEPARTEMENTS],connectionDestination);
        if ((flags&GestionReferentiel.MAJ_COMMUNES)!=0 && nomMarqueurs[GestionMarqueurs.MARQUEUR_COMMUNES]!=null && nomMarqueurs[GestionMarqueurs.MARQUEUR_COMMUNES].length()>0)
            supprimeMarqueur("com_communes",nomMarqueurs[GestionMarqueurs.MARQUEUR_COMMUNES],connectionDestination);
        if ((flags&GestionReferentiel.MAJ_TRONCONS)!=0 && nomMarqueurs[GestionMarqueurs.MARQUEUR_TRONCONS]!=null && nomMarqueurs[GestionMarqueurs.MARQUEUR_TRONCONS].length()>0)
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            supprimeMarqueur("tro_troncons_"+code_departement,nomMarqueurs[GestionMarqueurs.MARQUEUR_TRONCONS],connectionDestination);
            supprimeMarqueur(GestionTables.getTroTronconsTableName(code_departement),nomMarqueurs[GestionMarqueurs.MARQUEUR_TRONCONS],connectionDestination);
        if ((flags&GestionReferentiel.MAJ_VOIES)!=0 && nomMarqueurs[GestionMarqueurs.MARQUEUR_VOIES]!=null && nomMarqueurs[GestionMarqueurs.MARQUEUR_VOIES].length()>0)
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            supprimeMarqueur("voi_voies_"+code_departement,nomMarqueurs[GestionMarqueurs.MARQUEUR_VOIES],connectionDestination);
            supprimeMarqueur(GestionTables.getVoiVoiesTableName(code_departement) ,nomMarqueurs[GestionMarqueurs.MARQUEUR_VOIES],connectionDestination);
        if ((flags&GestionReferentiel.MAJ_CODEPOSTAUX)!=0 && nomMarqueurs[GestionMarqueurs.MARQUEUR_CODEPOSTAUX]!=null && nomMarqueurs[GestionMarqueurs.MARQUEUR_CODEPOSTAUX].length()>0)
            supprimeMarqueur("cdp_codes_postaux",nomMarqueurs[GestionMarqueurs.MARQUEUR_CODEPOSTAUX],connectionDestination);
        if ((flags&GestionReferentiel.MAJ_ADRESSES)!=0 && nomMarqueurs[GestionMarqueurs.MARQUEUR_ADRESSES]!=null && nomMarqueurs[GestionMarqueurs.MARQUEUR_ADRESSES].length()>0)
            supprimeMarqueur("adr_adresses_"+code_departement,nomMarqueurs[GestionMarqueurs.MARQUEUR_ADRESSES],connectionDestination);
    }
        
    /**
     * Purge les marqueurs des tables regions, departements, communesetarrondissements, tro_troncons_dpt, et adr_adresses_dpt.
     * @throws GestionReferentielException De multiples schémas ont été trouvés pour une des tables pour cette connection.
     */
    public static void purgeMarqueurs(String code_departement,Connection connection) throws SQLException, GestionReferentielException
    {
        purgeMarqueurs(code_departement,1+2+4+8+16,connection);
    }        
            
    /**
     * Purge les marqueurs des tables regions, departements, communesetarrondissements, tro_troncons_dpt, et adr_adresses_dpt.
     * @param flags permet de spécifier les tables à purger en utilisant des combinaisons des bits spécifiés par
     * GestionReferentiel.MAJ_XXX.
     * @throws GestionReferentielException De multiples schémas ont été trouvés pour une des tables pour cette connection.
     */
    public static void purgeMarqueurs(String code_departement,int flags,Connection connection) throws SQLException, GestionReferentielException
    {
        if ((flags & GestionReferentiel.MAJ_DEPARTEMENTS)!=0)
        {
            ArrayList<String> marqueurs = chercheMarqueurs("dpt_departements",connection);
            purgeMarqueurs(marqueurs,"dpt_departements",connection);
        }
        
        if ((flags & GestionReferentiel.MAJ_COMMUNES)!=0)
        {
            ArrayList<String> marqueurs = chercheMarqueurs("com_communes",connection);
            purgeMarqueurs(marqueurs,"com_communes",connection);
        }
        
        if ((flags & GestionReferentiel.MAJ_TRONCONS)!=0)
        {
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            ArrayList<String> marqueurs = chercheMarqueurs("tro_troncons_"+code_departement,connection);
//            purgeMarqueurs(marqueurs,"tro_troncons_"+code_departement,connection);
            final String tableTroncon = GestionTables.getTroTronconsTableName(code_departement);
            ArrayList<String> marqueurs = chercheMarqueurs(tableTroncon,connection);
            purgeMarqueurs(marqueurs,tableTroncon,connection);
        }
        
        if ((flags & GestionReferentiel.MAJ_VOIES)!=0)
        {
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            ArrayList<String> marqueurs = chercheMarqueurs("voi_voies_"+code_departement,connection);
//            purgeMarqueurs(marqueurs,"voi_voies_"+code_departement,connection);
            String voiTableName = GestionTables.getVoiVoiesTableName(code_departement);
            ArrayList<String> marqueurs = chercheMarqueurs(voiTableName, connection);
            purgeMarqueurs(marqueurs,voiTableName,connection);
        }
        
        if ((flags & GestionReferentiel.MAJ_CODEPOSTAUX)!=0)
        {
            ArrayList<String> marqueurs = chercheMarqueurs("cdp_codes_postaux",connection);
            purgeMarqueurs(marqueurs,"cdp_codes_postaux",connection);
        }

        if ((flags & GestionReferentiel.MAJ_ADRESSES)!=0)
        {
            ArrayList<String> marqueurs = chercheMarqueurs("adr_adresses_"+code_departement,connection);
            purgeMarqueurs(marqueurs,"adr_adresses_"+code_departement,connection);
        }
    }
    
    /**
     * Purge les marqueurs d'une table.
     */
    public static void purgeMarqueursDeTable(String nomTable,Connection connection) throws SQLException, GestionReferentielException
    {
        ArrayList<String> marqueurs = chercheMarqueurs(nomTable,connection);
        purgeMarqueurs(marqueurs,nomTable,connection);        
    }
    
    /**
     * Purge les marqueurs spécifiés d'une table.<br>
     * Les noms de marqueurs ne doivent pas être null.
     */
    private static void purgeMarqueurs(ArrayList<String> marqueurs,String nomTable,Connection connection) throws SQLException
    {
        for(int i=0;i<marqueurs.size();i++)
        {
            try
            {
                supprimeMarqueur(nomTable, marqueurs.get(i), connection);
            }
            catch(ColonneException ce)
            {
                // Ne devrait pas se produire.
            }
        }
    }
    
    /**
     * Retourne les noms des marqueurs de la table spécifiée.<br>
     * Si la table n'existe pas, aucun marqueur n'est retourné.
     * @throws GestionReferentielException de multiples schémas ont été trouvés pour la table.
     */
    public static ArrayList<String> chercheMarqueurs(String nomTable,Connection connection) throws SQLException, GestionReferentielException
    {
        ArrayList<String> marqueurs = new ArrayList<String>();
        DescriptionTable dt = GestionTables.obtientDescription(nomTable,connection);
        
        if (dt!=null)
        {
            for(int i = 0; i < dt.getCount(); i++)
            {
                Colonne c = dt.getColonne(i);

                if (c.getNom().startsWith(markername))
                {
                    marqueurs.add(c.getNom());
                }
            }
        }
        
        return marqueurs;
    }
    
    /**
     * Supprime le marqueur de la table tronçon spécifié.
     */
    public static void supprimeMarqueurTroncon(String nomTableTroncon,String nomMarqueur,Connection connectionOrigine) throws SQLException, ColonneException
    {
        if (nomMarqueur!=null && nomMarqueur.length()>0)
            supprimeMarqueur(nomTableTroncon,nomMarqueur,connectionOrigine);
    }
    
    /**
     * Crée des marqueurs sur toutes les tables du référentiel spécifié.<br>
     * Les index des marqueurs dans le tableau retourné sont définis par les index GestionMarqueurs.MARQUEUR_XXX.
     * @param flags permet de spécifier les tables à marquer en utilisant des combinaisons des bits suivants:
     * <ul><li>1 departements</li>
     * <li>2 communes et arrondissements</li>
     * <li>4 troncons</li>
     * <li>8 voies</li>
     * <li>16 adresses</li>
     * </ul>
     * @throws GestionReferentielException une des tables n'a pas été trouvée.
     * @returns tableau de noms éventuellement vides si les tables ne sont pas trouvées.
     */
    public static String[] ajouteMarqueursMaj(String code_departement,int flags,Connection connectionDestination) throws SQLException, GestionReferentielException
    {
        String mqDepartement, mqCommune, mqTroncons, mqVoies, mqCodePostaux, mqAdresses;
        
        if ((flags&GestionReferentiel.MAJ_DEPARTEMENTS)!=0 && GestionTables.tableExiste("dpt_departements", connectionDestination))
                mqDepartement = ajouteMarqueur("dpt_departements",connectionDestination);
        else
            mqDepartement = "";
        if ((flags&GestionReferentiel.MAJ_COMMUNES)!=0 && GestionTables.tableExiste("com_communes", connectionDestination))
            mqCommune = ajouteMarqueur("com_communes",connectionDestination);
        else
            mqCommune = "";
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        if ((flags&GestionReferentiel.MAJ_TRONCONS)!=0 && GestionTables.tableExiste("tro_troncons_"+code_departement, connectionDestination))
//            mqTroncons = ajouteMarqueur("tro_troncons_"+code_departement,connectionDestination);
        final String tronconTableName = GestionTables.getTroTronconsTableName(code_departement);
        if ((flags&GestionReferentiel.MAJ_TRONCONS)!=0 && GestionTables.tableExiste(tronconTableName, connectionDestination))
            mqTroncons = ajouteMarqueur(tronconTableName, connectionDestination);
        else
            mqTroncons = "";
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        if ((flags&GestionReferentiel.MAJ_VOIES)!=0 && GestionTables.tableExiste("voi_voies_"+code_departement, connectionDestination))
//            mqVoies = ajouteMarqueur("voi_voies_"+code_departement,connectionDestination);
        final String voiTableName = GestionTables.getVoiVoiesTableName(code_departement);
        if ((flags&GestionReferentiel.MAJ_VOIES)!=0 && GestionTables.tableExiste(voiTableName, connectionDestination))
            mqVoies = ajouteMarqueur(voiTableName,connectionDestination);
        else
            mqVoies = "";
        if ((flags&GestionReferentiel.MAJ_CODEPOSTAUX)!=0 && GestionTables.tableExiste("cdp_codes_postaux", connectionDestination))
            mqCodePostaux = ajouteMarqueur("cdp_codes_postaux",connectionDestination);
        else
            mqCodePostaux = "";
        if ((flags&GestionReferentiel.MAJ_ADRESSES)!=0 && GestionTables.tableExiste("adr_adresses_"+code_departement, connectionDestination))
            mqAdresses = ajouteMarqueur("adr_adresses_"+code_departement,connectionDestination);
        else
            mqAdresses = "";
        
        return new String[]{mqDepartement,mqCommune,mqTroncons,mqVoies,mqCodePostaux,mqAdresses};
    }
}
