/*
 * GestionStructure.java
 *
 * Created on 9 avril 2008, 11:16
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

package ppol.jdonref.referentiel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import ppol.jdonref.Tables.Colonne;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.Tables.DescriptionTable;
import ppol.jdonref.Tables.Difference;
import ppol.jdonref.Tables.GestionTables;
import ppol.jdonref.Tables.Index;

/**
 * Permet de gérer l'évolution de la structure du référentiel.<br>
 * Trois catégories de méthodes sont présentes:
 * <ul><li>Les méthodes pour créer des améliorer les structures : creeIndexesMaj, creeIndexesReferentiel, rendHistorisee</li>
 *     <li>Les méthodes pour créer les structures: creeStructure, creeStructure, creeStructures</li>
 *     <li>Les méthodes pour vérifier les structures : verifieStructure</li></ul>
 * @author jmoquet
 */
public class GestionStructure
{
    /**
     * Met à jour dans le référentiel destination la table désignée dans le référentiel d'origine.
     * Si la table n'existe pas dans le référentiel destination, elle est crée.
     * La table crée est rendue historisable si nécessaire.
     * Retourne vrai si la table a été mise à jour, faux si elle a été crée.
     *
     * @throws GestionReferentielException si des colonnes n'ont pas le même type entre l'origine et la destination avant mise à jour.
     * @throws SQLException si un problème est survenu alors que la méthode vérifiait si une des tables existe.
     * @throws SQLException si un problème est lors de l'obtention de la description d'une des table.
     * @throws SQLException si un problème est lors de l'ajout d'une colonne manquante
     * @throws SQLException si un problème est lorsque la table est rendue historisée
     * @throws SQLException si un problème est lorsque la table est crée.
     */
    public static boolean majStructure(DescriptionTable descriptionOrigine,String nomDestination,Connection connectionDestination,boolean historisee) throws GestionReferentielException, SQLException
    {
            if (GestionTables.tableExiste(nomDestination,connectionDestination))
            {
                DescriptionTable descriptionDestination = GestionTables.obtientDescription(nomDestination,connectionDestination);
                
                // Calcule la différence entre les tables
                Difference difference = null;
                
                descriptionOrigine.getCount();
                
                difference = descriptionDestination.compare(descriptionOrigine);
                
                // Si des types ou largeurs de champs sont erronnés, 
                if (difference.obtientQuantitéColonnesErronees()>0)
                {
                    StringBuffer sb = new StringBuffer();
                    
                    sb.append("Des colonnes sont erronnées entre l'origine et ");
                    sb.append(nomDestination);
                    sb.append(" (");
                    for(int i=0;i<difference.obtientQuantitéColonnesErronees();i++)
                    {
                        if (i>0)
                            sb.append(",");
                        sb.append(difference.obtientColonneErronee(i).getNom());
                    }
                    sb.append(")");
                    sb.append(".");
                    
                    throw(new GestionReferentielException(sb.toString(),GestionReferentielException.COLONNESERRONEES,10));
                }
                // Sinon,
                else
                {
                    // Ajoute les colonnes manquantes.
                    for(int i=0;i<difference.obtientQuantitéColonnesManquantes();i++)
                    {
                        Colonne c = difference.obtientColonneManquante(i);
                        
                        GestionTables.ajouteColonne(nomDestination,c,connectionDestination);
                    }
                    
                    DescriptionTable descriptionFinale = GestionTables.obtientDescription(nomDestination,connectionDestination);
                    
                    if (historisee && !descriptionFinale.estHistorisee());
                        rendHistorisee(nomDestination,connectionDestination);
                    
                    return true;
                }
            }
            else
            {
                // Si la table destination n'existe pas, il faut la créer.
                creeStructure(descriptionOrigine,nomDestination,connectionDestination,historisee);
                
                return false;
            }
    }
    
    /**
     * Met à jour dans le référentiel destination la table désignée dans le référentiel d'origine.
     * Si la table n'existe pas dans le référentiel destination, elle est crée.
     * La table crée est rendue historisable si nécessaire.
     * Retourne vrai si la table a été mise à jour, faux si elle a été crée.
     *
     * GestionReferentielException si la table nomOrigine n'a pas été trouvée.
     *                             si des colonnes n'ont pas le même type entre l'origine et la destination avant mise à jour.
     * SQLException si un problème est survenu alors que la méthode vérifiait si une des tables existe.
     *                                         lors de l'obtention de la description d'une des table.
     *                                         lors de l'ajout d'une colonne manquante
     *                                         lorsque la table est rendue historisée
     *                                         lorsque la table est crée.
     */
    public static boolean majStructure(String nomOrigine,Connection connectionOrigine,String nomDestination,Connection connectionDestination,boolean historisee) throws GestionReferentielException, SQLException
    {        
        if (GestionTables.tableExiste(nomOrigine,connectionOrigine))
        {
            DescriptionTable descriptionOrigine = GestionTables.obtientDescription(nomOrigine,connectionOrigine);

            return majStructure(descriptionOrigine,nomDestination,connectionDestination,historisee);
        }
        else
            throw(new GestionReferentielException("La table "+nomOrigine+" n'a pas été trouvée.",GestionReferentielException.TABLENEXISTEPAS,11));
    }
    
    /**
     * Crée une table à partir de la description spécifiée.
     * Si la table crée n'est pas historisée, la rends historisable.
     * @param historisee s'il est a true, la table crée sera historisable.
     * @throws GestionReferentielException : La colonne t0 ou t1 est déjà présente avec le mauvais type.
     * @throws SQLException : problème durant la cr�ation de la table.
     */
    public static void creeStructure(DescriptionTable description,String nom,Connection connection,boolean historisee) throws SQLException, GestionReferentielException
    {
        ArrayList<Colonne> colonnes = new ArrayList<Colonne>();
        
        for(int i=0;i<description.getCount();i++) {
            colonnes.add(description.getColonne(i));
        }
        
        GestionTables.creeTable(nom,colonnes,connection);
        
        // Si la table n'est pas historisée, la rend historisable.
        if (historisee && !description.estHistorisee())
        {
            try
            {
                rendHistorisee(nom,connection);
            }
            catch(GestionReferentielException gre)
            {
                if (gre.obtientType() != GestionReferentielException.TABLENEXISTEPAS)
                    throw(gre);
            }
        }
    }    
    
    /**
     * Rend historisable une table en ajoutant les colonnes t0 et t1 de type TIMESTAMP WITHOUT TIME ZONE.
     * GestionReferentielException : La colonne t0 ou t1 est déjà présente avec le mauvais type.
     *                               La table n'existe pas.
     * SQLException : un problème est survenu durant la requ�te permettant d'obtenir la liste des colonnes de la table.
     *                un problème est survenu durant l'ajout d'une colonne.
     */
    public static void rendHistorisee(String nom,Connection connection) throws GestionReferentielException, SQLException
    {
        if (GestionTables.tableExiste(nom,connection))
        {
            DescriptionTable description = GestionTables.obtientDescription(nom,connection);
            
            Colonne t0 = null;
            Colonne t1 = null;
            
            try
            {
                t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
                t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
            }
            catch(ColonneException ce)
            {
            }
            
            int comp0 = description.contient(t0);
            int comp1 = description.contient(t1);
            
            if (comp0==Colonne.TYPEDIFFERENT)
            {
                throw(new GestionReferentielException("La colonne t0 de la table "+nom+" est déjà présente avec un type erroné.",GestionReferentielException.COLONNEERRONEE,10));
            }
            if (comp1==Colonne.TYPEDIFFERENT)
            {
                throw(new GestionReferentielException("La colonne t1 de la table "+nom+" est déjà présente avec un type erroné.",GestionReferentielException.COLONNEERRONEE,10));
            }
            
            if (comp0==Colonne.DIFFERENT)
            {
                GestionTables.ajouteColonne(nom,t0,connection);
            }
            if (comp1==Colonne.DIFFERENT)
            {
                GestionTables.ajouteColonne(nom,t1,connection);
            }
        }
        else
            throw(new GestionReferentielException("La table "+nom+" n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
    }
    
        /**
     * Vérifie la structure d'une table , par rapport à la structure attendue.
     */
    public static void verifieStructure(String nomTable,DescriptionTable dt,DescriptionTable dtVoulue) throws GestionReferentielException
    {        
        Difference d = dt.compare(dtVoulue);

        if (d.obtientQuantitéColonnesErronees() > 0 || d.obtientQuantitéColonnesManquantes() > 0)
        {
            // Génère le message d'erreur.
            StringBuffer sb = new StringBuffer();

            sb.append("erronnees:");

            for (int i = 0; i < d.obtientQuantitéColonnesErronees(); i++)
            {
                sb.append(" ");
                sb.append(d.obtientColonneErronee(i).getNom());
            }

            sb.append(" manquantes:");

            for (int i = 0; i < d.obtientQuantitéColonnesManquantes(); i++)
            {
                sb.append(" ");
                sb.append(d.obtientColonneManquante(i).getNom());
            }

            throw (new GestionReferentielException("La structure de la table "+nomTable+" n'est pas correcte (" + sb.toString() + ").", GestionReferentielException.COLONNESERRONEES,11));
        }
    }   

    /**
     * Crée les index pour les tables de la mise à jour.
     * @param flags permet de spécifier les tables à purger en utilisant des combinaisons des bits suivants:
     * <ul><li>1 departements</li>
     * <li>2 communes et arrondissements</li>
     * <li>4 troncons</li>
     * <li>8 voies</li>
     * <li>16 adresses</li>
     * </ul>
     */
    public static void creeIndexesMaj(String code_departement,int flags,Connection connection) throws SQLException, GestionReferentielException
    {
        if ((flags&(GestionReferentiel.MAJ_TRONCONS+GestionReferentiel.MAJ_TABLEVOIES))!=0)
        {
            Index tr_id_troncon = new Index();
            tr_id_troncon.setNom("tr"+code_departement+"_tro_id");
            tr_id_troncon.ajouteColonne("tro_id");
            // WA 09/2011 utilisation de getTroTronconsTableName
//            GestionTables.ajouteIndex("tro_troncons_"+code_departement,tr_id_troncon,connection);
            GestionTables.ajouteIndex(GestionTables.getTroTronconsTableName(code_departement),tr_id_troncon,connection);
        }
        
        if ((flags&(GestionReferentiel.MAJ_VOIES+GestionReferentiel.MAJ_TABLECODEPOSTAUX))!=0)
        {
            Index voies_voi_id = new Index();
            voies_voi_id.setNom("voi_"+code_departement+"_voi_id");
            voies_voi_id.ajouteColonne("voi_id");
            // WA 09/2011 utilisation de getTroTronconsTableName
//            GestionTables.ajouteIndex("voi_voies_"+code_departement,voies_voi_id,connection);
            GestionTables.ajouteIndex(GestionTables.getVoiVoiesTableName(code_departement), voies_voi_id,connection);
        }
        
        if ((flags&GestionReferentiel.MAJ_COMMUNES)!=0)
        {
            Index com_noms_code_insee = new Index();
            com_noms_code_insee.setNom("com_noms_code_insee");
            com_noms_code_insee.ajouteColonne("com_code_insee");
            GestionTables.ajouteIndex("com_communes",com_noms_code_insee,connection);
        }
    }
    
    /**
     * Cree les index pour les tables du département spécifié.
     * @param flags permet de spécifier les tables à purger en utilisant des combinaisons des bits suivants:
     * <ul><li>1 departements</li>
     * <li>2 communes et arrondissements</li>
     * <li>4 troncons</li>
     * <li>8 voies</li>
     * <li>16 adresses</li>
     * </ul>
     * @return
     */
    public static void creeIndexesReferentiel(String code_departement,String tableTroncon,int flags,Connection connection) throws SQLException, GestionReferentielException
    {
        if ((flags&GestionReferentiel.MAJ_DEPARTEMENTS)!=0)
        {
            Index dpt_code_dpt = new Index();
            dpt_code_dpt.setNom("dpt_code_dpt");
            dpt_code_dpt.ajouteColonne("dpt_code_departement");
            GestionTables.ajouteIndex("dpt_departements",dpt_code_dpt,connection);
        }
        
        if ((flags&GestionReferentiel.MAJ_COMMUNES)!=0)
        {
            Index com_noms_code_dpt = new Index();
            com_noms_code_dpt.setNom("com_noms_code_dpt");
            com_noms_code_dpt.ajouteColonne("dpt_code_departement");
            GestionTables.ajouteIndex("com_communes",
                                      com_noms_code_dpt, connection);
            
            Index com_code_dpt = new Index();
            com_code_dpt.setNom("com_code_dpt");
            com_code_dpt.ajouteColonne("dpt_code_departement");
            GestionTables.ajouteIndex("com_communes",com_code_dpt,connection);
            
            Index com_noms_nom = new Index();
            com_noms_nom.setNom("com_noms_nom");
            com_noms_nom.ajouteColonne("com_nom");
            GestionTables.ajouteIndex("com_communes", com_noms_nom,
                                      connection);
            
            Index com_noms_code_insee = new Index();
            com_noms_code_insee.setNom("com_noms_code_insee");
            com_noms_code_insee.ajouteColonne("com_code_insee");
            GestionTables.ajouteIndex("com_communes",
                                      com_noms_code_insee, connection);
        }
        
        if ((flags&GestionReferentiel.MAJ_ADRESSES)!=0)
        {
            Index adr_voi_id = new Index();
            adr_voi_id.setNom("adr_"+code_departement+"_voi_id");
            adr_voi_id.ajouteColonne("voi_id");
            GestionTables.ajouteIndex("adr_adresses_"+code_departement,adr_voi_id,connection);
            
            Index adr_voi_id_num_rep = new Index();
            adr_voi_id_num_rep.setNom("adr_"+code_departement+"_num_rep");
            adr_voi_id_num_rep.ajouteColonne("voi_id");
            adr_voi_id_num_rep.ajouteColonne("adr_numero");
            adr_voi_id_num_rep.ajouteColonne("adr_rep");
            GestionTables.ajouteIndex("adr_adresses_"+code_departement,adr_voi_id_num_rep,connection);
        }
        
        if ((flags&GestionReferentiel.MAJ_TRONCONS)!=0)
        {
            Index tr_voi_id_droit = new Index();
            tr_voi_id_droit.setNom(tableTroncon + "_voi_id_droit");
            tr_voi_id_droit.ajouteColonne("voi_id_droit");
            GestionTables.ajouteIndex(tableTroncon, tr_voi_id_droit, connection);
            
            Index tr_voi_id_gauche = new Index();
            tr_voi_id_gauche.setNom(tableTroncon + "_voi_id_gauche");
            tr_voi_id_gauche.ajouteColonne("voi_id_gauche");
            GestionTables.ajouteIndex(tableTroncon, tr_voi_id_gauche,
                                      connection);
            
            Index tr_id_troncon = new Index();
            tr_id_troncon.setNom(tableTroncon + "_tro_id");
            tr_id_troncon.ajouteColonne("tro_id");
            GestionTables.ajouteIndex(tableTroncon, tr_id_troncon, connection);
        }

        if ((flags&GestionReferentiel.MAJ_VOIES)!=0)
        {
            Index histo_voi_id_precedent = new Index();
            histo_voi_id_precedent.setNom("vhi_" + code_departement +
                                           "_voi_id_precedent");
            histo_voi_id_precedent.ajouteColonne("voi_id_precedent");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            GestionTables.ajouteIndex("vhi_voies_historisee_" + code_departement, histo_voi_id_precedent, connection);
            GestionTables.ajouteIndex(GestionTables.getVhiVoiesHistoriseeTableName(code_departement), histo_voi_id_precedent, connection);
            
            // WA 09/2011 utilisation de getTroTronconsTableName
            final String voiTableName = GestionTables.getVoiVoiesTableName(code_departement);

            Index voies_voi_id = new Index();
            voies_voi_id.setNom("voi_" + code_departement + "_voi_id");
            voies_voi_id.ajouteColonne("voi_id");
            // WA 09/2011 utilisation de getTroTronconsTableName
//            GestionTables.ajouteIndex("voi_voies_" + code_departement, voies_voi_id, connection);
            GestionTables.ajouteIndex(voiTableName, voies_voi_id, connection);
            
            Index voies_voi_type_lbl = new Index();
            voies_voi_type_lbl.setNom("voi_" + code_departement + "_voi_type_lbl");
            voies_voi_type_lbl.ajouteColonne("voi_type_de_voie");
            voies_voi_type_lbl.ajouteColonne("voi_lbl_sans_articles");
            // WA 09/2011 utilisation de getTroTronconsTableName
//            GestionTables.ajouteIndex("voi_voies_" + code_departement, voies_voi_type_lbl,connection);
            GestionTables.ajouteIndex(voiTableName, voies_voi_type_lbl,connection);

            Index voies_voi_lbl = new Index();
            voies_voi_lbl.setNom("voi_" + code_departement + "_voi_lbl");
            voies_voi_lbl.ajouteColonne("voi_lbl_sans_articles");
            // WA 09/2011 utilisation de getTroTronconsTableName
//            GestionTables.ajouteIndex("voi_voies_" + code_departement, voies_voi_lbl,connection);
            GestionTables.ajouteIndex(voiTableName, voies_voi_lbl,connection);
            
            Index voies_code_insee = new Index();
            voies_code_insee.setNom("voi_" + code_departement + "_code_insee");
            voies_code_insee.ajouteColonne("com_code_insee");
            // WA 09/2011 utilisation de getTroTronconsTableName
//            GestionTables.ajouteIndex("voi_voies_" + code_departement,voies_code_insee, connection);
            GestionTables.ajouteIndex(voiTableName, voies_code_insee, connection);
            
            Index idvoies_voi_id = new Index();
            idvoies_voi_id.setNom("idv_voi_id");
            idvoies_voi_id.ajouteColonne("voi_id");
            GestionTables.ajouteIndex("idv_id_voies", idvoies_voi_id, connection);
        }

        if ((flags&(GestionReferentiel.MAJ_CODEPOSTAUX))!=0)
        {
            Index cp_code_dpt = new Index();
            cp_code_dpt.setNom("cdp_code_dpt");
            cp_code_dpt.ajouteColonne("dpt_code_departement");
            GestionTables.ajouteIndex("cdp_codes_postaux", cp_code_dpt, connection);
            
            Index cp_code_insee = new Index();
            cp_code_insee.setNom("cdp_code_insee");
            cp_code_insee.ajouteColonne("com_code_insee");
            GestionTables.ajouteIndex("cdp_codes_postaux", cp_code_insee,
                                      connection);
            
            Index cp_code_postal = new Index();
            cp_code_postal.setNom("cdp_code_postal");
            cp_code_postal.ajouteColonne("cdp_code_postal");
            GestionTables.ajouteIndex("cdp_codes_postaux", cp_code_postal,
                                      connection);
        }

        if ((flags&(GestionReferentiel.MAJ_COMMUNES+GestionReferentiel.MAJ_VOIES))!=0 &&
            (flags&GestionReferentiel.MAJ_VOIESAMBIGUES)==0 )
        {
            Index va_tout = new Index();
            va_tout.setNom("va_tout_"+code_departement);
            va_tout.ajouteColonne("voa_mot");
            va_tout.ajouteColonne("voa_lbl_pq");
            va_tout.ajouteColonne("voa_categorie_ambiguite");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            GestionTables.ajouteIndex("voa_voies_ambigues_"+code_departement, va_tout, connection);
            GestionTables.ajouteIndex(GestionTables.getVoaVoiesAmbiguesTableName(code_departement), va_tout, connection);
            
            Index va_mot_categorie = new Index();
            va_mot_categorie.setNom("voa_mot_categorie_"+code_departement);
            va_mot_categorie.ajouteColonne("voa_mot");
            va_mot_categorie.ajouteColonne("voa_categorie_ambiguite");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            GestionTables.ajouteIndex("voa_voies_ambigues_"+code_departement, va_mot_categorie, connection);
            GestionTables.ajouteIndex(GestionTables.getVoaVoiesAmbiguesTableName(code_departement), va_mot_categorie, connection);
        }
    }
            
    /**
     * Crée ou vérifie la structure de la table spécifiée.
     * @param nomTable
     * @param dt
     * @param dtVoulu
     * @param connectionReferentiel
     */
    public static void creeStructure(String nomTable,DescriptionTable dtVoulu,Connection connectionReferentiel) throws SQLException, GestionReferentielException
    {       
        if (GestionTables.tableExiste(nomTable, connectionReferentiel))
        {
            DescriptionTable dt = GestionTables.obtientDescription(nomTable, connectionReferentiel);
            verifieStructure(nomTable,dt,dtVoulu);
        }
        else
        {
            GestionTables.creeTable(nomTable, dtVoulu,
                                    connectionReferentiel);
        }
    }
    
   /**
     * Crée et vérifie les structures des tables du département concerné.<br>
     * Doit être utilisé dans le cadre de la mise à jour sans changement de référentiel (à
     * cause de la gestion de la table troncon) <br>
     * Une nouvelle table de troncon est enregistrée dans tablesTroncons si aucune n'est trouvée.<br>
     * Les tables suivantes sont crées:
     * <ul><li>ttr_tables_troncons</li>
     *     <li>voa_voies_ambigues_dpt</li>
     *     <li>tro_troncons_dpt</li>
     *     <li>voi_voies_dpt</li>
     *     <li>adr_adresses_dpt</li>
     *     <li>com_communes</li>
     *     <li>dpt_departements</li></ul>
     * @param code_departement le département concerné
     * @param flags permet de spécifier les tables à purger en utilisant des combinaisons des bits suivants:
     * <ul><li>1 departements</li>
     * <li>2 communes et arrondissements et voies ambigues</li>
     * <li>4 troncons et tables de troncons</li>
     * <li>8 voies et voiesambigues et id voies et voies historisees</li>
     * <li>16 codes postaux</li>
     * <li>32 adresses</li>
     * <li>64 voies et id voies</li>
     * <li>128> codes postaux</li>
     * </ul>
     * @param connectionOrigine la connection au référentiel à mettre à jour
     * @param date la date à laquelle la mise à jour à lieu (ultérieure à la date actuelle).
     * @throws GestionReferentielException Problème avec une structure.
     * @return
     */
    public static void creeStructures(String code_departement,int flags,Connection connectionReferentiel,Date date) throws SQLException, GestionReferentielException
    {
        if ((flags&GestionReferentiel.MAJ_DEPARTEMENTS)!=0)
            creeStructure("dpt_departements",GestionDescriptionTables.creeDescriptionTableDepartementsReferentiel(),connectionReferentiel);
        if ((flags&GestionReferentiel.MAJ_COMMUNES)!=0)
            creeStructure("com_communes",GestionDescriptionTables.creeDescriptionTableCommunesEtArrondissementsReferentiel(),connectionReferentiel);
        if ((flags&GestionReferentiel.MAJ_TRONCONS)!=0)
        {
            // Crée si nécessaire la structure de gestion des tables de troncons.
            GestionHistoriqueTables.creeTableGestionTroncons(connectionReferentiel);
            connectionReferentiel.commit();
                        
            String nomTableTroncon = GestionHistoriqueTables.obtientDerniereTableTroncon(connectionReferentiel,
                                                                                         code_departement);
            if (nomTableTroncon == null)
            {
                nomTableTroncon = GestionHistoriqueTables.
                        obtientNouveauNomPourTableTroncon(connectionReferentiel,
                                                          code_departement);
                GestionHistoriqueTables.enregistreNouvelleTableTroncons(
                        connectionReferentiel, nomTableTroncon, code_departement,
                        date);
            }

            creeStructure(nomTableTroncon, GestionDescriptionTables.
                          creeDescriptionTableTronconReferentiel(),
                          connectionReferentiel);
        }
        if ((flags&(GestionReferentiel.MAJ_VOIES+GestionReferentiel.MAJ_TABLEVOIES))!=0)
        {
            creeStructure("idv_id_voies",GestionDescriptionTables.creeDescriptionTableIdVoies(),connectionReferentiel);
            // WA 09/2011 utilisation de getTroTronconsTableName
//            creeStructure("voi_voies_"+code_departement,GestionDescriptionTables.creeDescriptionTableVoiesReferentiel(),connectionReferentiel);
            creeStructure(GestionTables.getVoiVoiesTableName(code_departement), GestionDescriptionTables.creeDescriptionTableVoiesReferentiel(),connectionReferentiel);
        }
        if ((flags&GestionReferentiel.MAJ_VOIES)!=0)
        {
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            creeStructure("vhi_voies_historisee_"+code_departement,GestionDescriptionTables.creeDescriptionTableHistorisationVoieReferentiel(),connectionReferentiel);
            creeStructure(GestionTables.getVhiVoiesHistoriseeTableName(code_departement),GestionDescriptionTables.creeDescriptionTableHistorisationVoieReferentiel(),connectionReferentiel);
        }
        if ((flags&(GestionReferentiel.MAJ_CODEPOSTAUX+GestionReferentiel.MAJ_TABLECODEPOSTAUX))!=0)
            creeStructure("cdp_codes_postaux",GestionDescriptionTables.creeDescriptionTableCodePostauxReferentiel(),connectionReferentiel);
        if ((flags&GestionReferentiel.MAJ_ADRESSES)!=0)
            creeStructure("adr_adresses_"+code_departement,GestionDescriptionTables.creeDescriptionTableAdressesReferentiel(),connectionReferentiel);
        if ((flags&(GestionReferentiel.MAJ_COMMUNES+GestionReferentiel.MAJ_VOIES))!=0 && // voies et communes
            (flags&GestionReferentiel.MAJ_VOIESAMBIGUES)==0)
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            creeStructure("voa_voies_ambigues_"+code_departement,GestionDescriptionTables.creeDescriptionTableVoiesAmbiguesReferentiel(),connectionReferentiel);
            creeStructure(GestionTables.getVoaVoiesAmbiguesTableName(code_departement),GestionDescriptionTables.creeDescriptionTableVoiesAmbiguesReferentiel(),connectionReferentiel);
        
        connectionReferentiel.commit();
    }
}