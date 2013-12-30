/*
 * DescriptionTablesReferentiel.java
 *
 * Created on 20 mars 2008, 11:08
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

import ppol.jdonref.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.Tables.Colonne;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.Tables.DescriptionTable;

/**
 * Permet d'obtenir la description des tables qui sont utilisées par le référentiel et pour
 * les mises à jour.<br>
 * La première colonne des descriptions doit être l'identifiant unique.<br>
 * 
 * Les types des colonnes t0 et t1 est TIME WITHOUT TIME STAMP.<br>
 * La colonne géométrie est une colonne postgis.<br>
 * 
 * Les règles de nommage suivantes ont été appliquées pour les noms de colonne:
 * <ul>
 * <li>les champs sont préfixés par le trigramme de la table</li>
 * <li>sauf pour les clés étrangères qui sont préfixées par le trigramme de leur table d'origine</li>
 * <li>les identifiants unique ont pour nom id.</li>
 * <li>trois colonnes font exceptions à ces trois règles:
 * <ul><li>geometrie</li>
 *     <li>t0</li>
 *     <li>t1</li></ul></li>
 * </ul>
 * @author jmoquet
 */
public class GestionDescriptionTables
{
    static JDONREFParams jdonrefParams = null;
    
    /** Creates a new instance of DescriptionTablesReferentiel */
    public GestionDescriptionTables()
    {
    }
    
    /**
     * Obtient les paramêtres génériques à l'application définis pour cette classe.
     */
    public static JDONREFParams obtientJDONREFParams()
    {
        return jdonrefParams;
    }
    
    /**
    * définit les paramêtres génériques à l'application pour cette classe.
    */
    public static void definitJDONREFParams(JDONREFParams params)
    {
        GestionDescriptionTables.jdonrefParams = params;
    }

    /**
     * Crée une structure de table historisant les tables.
     */
    public static DescriptionTable creeDescriptionTableGestionTables()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne dpt_code_departement = null;
        Colonne ttr_nom = null;
        Colonne t0 = null;
        Colonne t1 = null;
        
        try
        {
           dpt_code_departement = new Colonne("dpt_code_departement","character varying",5);
           ttr_nom = new Colonne("ttr_nom","character varying",50);
           t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
           t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException e)
        {
            // Ne devrait pas arriver
        }
        
        colonnes.add(dpt_code_departement);
        colonnes.add(ttr_nom);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table permettant d'effectuer un lien entre les voies
     * d'un référentiel et d'un autre.
     * <ul><li>voi_id_source</li>
     *     <li>voi_id_destination</li>
     *     <li>lvo_flag</li></ul>
     */
    public static DescriptionTable creeDescriptionTableLienVoies()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voi_id_source = null;
        Colonne voi_id_destination = null;
        Colonne lvo_flag = null;
        
        try
        {
           voi_id_source = new Colonne("voi_id_source","character varying",jdonrefParams.obtientTailleDesCles());
           voi_id_destination = new Colonne("voi_id_destination","character varying",jdonrefParams.obtientTailleDesCles());
           lvo_flag = new Colonne("lvo_flag","integer",0);
        }
        catch(ColonneException e)
        {
            // Ne devrait pas arriver
        }
        
        colonnes.add(voi_id_source);
        colonnes.add(voi_id_destination);
        colonnes.add(lvo_flag);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table pour gérer les identifiants de voies entre les tables de voies.
     * <ul><li>voi_id</li>
     * <li>dpt_code_departement</li>
     * <li>idv_code_fantoir</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableIdVoies()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voi_id = null;
        Colonne dpt_code_departement = null;
        Colonne idv_code_fantoir = null;
        
        try
        {
           voi_id = new Colonne("voi_id","character varying",jdonrefParams.obtientTailleDesCles());
           dpt_code_departement = new Colonne("dpt_code_departement","CHARACTER VARYING",5);
           idv_code_fantoir = new Colonne("idv_code_fantoir","CHARACTER VARYING",10);
        }
        catch(ColonneException e)
        {
            // Ne devrait pas arriver
        }
        
        colonnes.add(voi_id);
        colonnes.add(dpt_code_departement);
        colonnes.add(idv_code_fantoir);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table pour les identifiants de tronçon, non historisée.
     * <ul>
     * <li>tro_id</li>
     * <li>dpt_code_departement</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableIdTroncons()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne tro_id = null;
        Colonne dpt_code_departement = null;
        
        try
        {
           tro_id = new Colonne("tro_id","character varying",jdonrefParams.obtientTailleDesCles());
           dpt_code_departement = new Colonne("dpt_code_departement","CHARACTER VARYING",5);
        }
        catch(ColonneException e)
        {
            // Ne devrait pas arriver
        }
        
        colonnes.add(tro_id);
        colonnes.add(dpt_code_departement);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée la portion attributs de la structure de table pour les communes et arrondissements, utilisée par le 
     * référentiel.
     * <ul><li>com_code_insee CHARACTER (5)</li>
     * <li>dpt_code_departement CHARACTER VARYING (5)</li>
     * <li>geometrie GEOMETRY</li>
     * <li>com_nom CHARACTER VARYING(32)</li>
     * <li>com_nom_desab CHARACTER VARYING(32)</li>
     * <li>com_nom_origine CHARACTER VARYING(255)</li>
     * <li>com_nom_pq CHARACTER VARYING(255)</li>
     * <li>com_code_insee_commune CHARACTER(5)</li>
     * <li>t0 TIMESTAMP WITHOUT TIME ZONE</li>
     * <li>t1 TIMESTAMP WITHOUT TIME ZONE</li></ul>
     */
    public static DescriptionTable creeDescriptionTableCommunesEtArrondissementsReferentiel()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne com_code_insee = null;
        Colonne dpt_code_departement = null;
        Colonne geometrie = null;
        Colonne com_nom = null;
        Colonne com_nom_desab = null;
        Colonne com_nom_origine = null;
        Colonne com_nom_pq = null;
        Colonne t0 = null;
        Colonne t1 = null;
        Colonne com_code_insee_commune = null;
        
        try
        {
           com_code_insee = new Colonne("com_code_insee","character",5);
           dpt_code_departement = new Colonne("dpt_code_departement","character varying",5);
           geometrie = Colonne.creeColonneGeometrie();
           com_nom = new Colonne("com_nom","character varying",32);
           com_nom_desab = new Colonne("com_nom_desab","character varying",255);
           com_nom_origine = new Colonne("com_nom_origine","character varying",255);
           com_nom_pq = new Colonne("com_nom_pq","character varying",255);
           com_code_insee_commune = new Colonne("com_code_insee_commune","character",5);
           t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
           t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException e)
        {
            // Ne devrait pas arriver
        }
        
        colonnes.add(com_code_insee);
        colonnes.add(dpt_code_departement);
        colonnes.add(com_nom);
        colonnes.add(com_nom_desab);
        colonnes.add(com_nom_origine);
        colonnes.add(com_nom_pq);
        colonnes.add(com_code_insee_commune);
        colonnes.add(geometrie);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table pour les communes et arrondissements, utilisée pour la mise à jour.
     * <ul><li>com_code_insee CHARACTER (5)</li>
     * <li>dpt_code_departement CHARACTER VARYING (5)</li>
     * <li>com_nom CHARACTER VARYING(32)</li>
     * <li>com_nom_desab CHARACTER VARYING(255)</li>
     * <li>com_nom_origine CHARACTER VARYING(255)</li>
     * <li>com_code_insee_commune CHARACTER (5)</li>
     * <li>geometrie GEOMETRY</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableCommunesEtArrondissementsMaj()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne com_code_insee = null;
        Colonne dpt_code_departement = null;
        Colonne com_nom = null;
        Colonne com_nom_desab = null;
        Colonne com_nom_origine = null;
        Colonne com_code_insee_commune = null;
        Colonne geometrie = null;
        
        try
        {
           com_code_insee = new Colonne("com_code_insee","character",5);
           dpt_code_departement = new Colonne("dpt_code_departement","character varying",5);
           com_nom = new Colonne("com_nom","character varying",32);
           com_nom_desab = new Colonne("com_nom_desab","character varying",255);
           com_nom_origine = new Colonne("com_nom_origine","character varying",255);
           com_code_insee_commune = new Colonne("com_code_insee_commune","character",5);
           geometrie = Colonne.creeColonneGeometrie();
        }
        catch(ColonneException e)
        {
            // Ne devrait pas arriver
        }
        
        colonnes.add(com_code_insee);
        colonnes.add(dpt_code_departement);
        colonnes.add(com_nom);
        colonnes.add(com_nom_desab);
        colonnes.add(com_nom_origine);
        colonnes.add(com_code_insee_commune);
        colonnes.add(geometrie);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table de code postaux pour le référentiel.<br>
     * <ul>
     *   <li>com_code_insee CHARACTER (5)</li>
     *   <li>cdp_code_postal CHARACTER (5)</li>
     *   <li>dpt_code_departement CHARACTER (5)</li>
     *   <li>t0 TIMESTAMP WITHOU TIME ZONE</li>
     *   <li>t1 TIMESTAMP WITHOU TIME ZONE</li>
     * </ul>
     * @return
     * @throws ppol.jdonref.referentiel.GestionReferentielException
     */
    public static DescriptionTable creeDescriptionTableCodePostauxReferentiel() throws GestionReferentielException
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        
        Colonne com_code_insee = null;
        Colonne cdp_code_postal = null;
        Colonne dpt_code_departement = null;
        Colonne t0 = null;
        Colonne t1 = null;
        
        try
        {
            com_code_insee = new Colonne("com_code_insee","CHARACTER",5);
            cdp_code_postal = new Colonne("cdp_code_postal","CHARACTER",5);
            dpt_code_departement = new Colonne("dpt_code_departement","CHARACTER VARYING",5);
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
            t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
            Logger.getLogger("GestionDescriptionTables").log(Level.INFO,"Ne devrait pas arriver",ce);
        }
        
        colonnes.add(com_code_insee);
        colonnes.add(cdp_code_postal);
        colonnes.add(dpt_code_departement);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table de code postaux pour la base de mise à jour.<br>
     * <ul>
     *   <li>com_code_insee CHARACTER (5)</li>
     *   <li>cdp_code_postal CHARACTER (5)</li>
     *   <li>dpt_code_departement CHARACTER (5)</li>
     * </ul>
     * @return
     * @throws ppol.jdonref.referentiel.GestionReferentielException
     */
    public DescriptionTable creeDescriptionTableCodePostauxMaj() throws GestionReferentielException
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        
        Colonne com_code_insee = null;
        Colonne cdp_code_postal = null;
        Colonne dpt_code_departement = null;
        
        try
        {
            com_code_insee = new Colonne("com_code_insee","CHARACTER",5);
            cdp_code_postal = new Colonne("cdp_code_postal","CHARACTER",5);
            dpt_code_departement = new Colonne("dpt_code_departement","CHARACTER VARYING",5);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
            Logger.getLogger("GestionDescriptionTables").log(Level.INFO,"Ne devrait pas arriver",ce);
        }
        
        colonnes.add(com_code_insee);
        colonnes.add(cdp_code_postal);
        colonnes.add(dpt_code_departement);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table voie utilisée pour le référentiel.<br>
     * <ul><li>voi_id CHARACTER VARYING(taillecles)</li>
     *     <li>voi_code_fantoir CHARACTER VARYING(taillecles)</li>
     *     <li>voi_nom CHARACTER VARYING(32)</li>
     *     <li>voi_nom_origine CHARACTER VARYING(255)</li>
     *     <li>voi_nom_desab CHARACTER VARYING(255)</li>
     *     <li>com_code_insee CHARACTER(5)</li>
     *     <li>cdp_code_postal CHARACTER(5)</li>
     *     <li>voi_type_de_voie CHARACTER VARYING(32)</li>
     *     <li>voi_type_de_voie_pq CHARACTER VARYING(32)</li>
     *     <li>voi_lbl CHARACTER VARYING(32)</li>
     *     <li>voi_lbl_pq CHARACTER VARYING(255)</li>
     *     <li>voi_lbl_sans_articles CHARACTER VARYING(32)</li>
     *     <li>voi_lbl_sans_articles_pq CHARACTER VARYING(255)</li>
     *     <li>voi_mot_determinant CHARACTER VARYING(32)</li>
     *     <li>voi_mot_determinant_pq CHARACTER VARYING(255)</li>
     *     <li>voi_min_numero INTEGER</li>
     *     <li>voi_max_numero INTEGER</li>
     *     <li>t0 TIMESTAMP WITHOUT TIME ZONE</li>
     *     <li>t1 TIMESTAMP WITHOUT TIME ZONE</li></ul>
     */
    public static DescriptionTable creeDescriptionTableVoiesReferentiel() throws GestionReferentielException
    {        
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voi_id = null;
        Colonne voi_code_fantoir = null;
        Colonne voi_nom = null;
        Colonne voi_nom_desab = null;
        Colonne voi_nom_origine = null;
        Colonne com_code_insee = null;
        Colonne cdp_code_postal = null;
        Colonne t0 = null;
        Colonne t1 = null;
        Colonne voi_type_de_voie = null;
        Colonne voi_type_de_voie_pq = null;
        Colonne voi_lbl = null;
        Colonne voi_lbl_pq = null;
        Colonne voi_lbl_sans_articles = null;
        Colonne voi_lbl_sans_articles_pq = null;
        Colonne voi_mot_determinant = null;
        Colonne voi_mot_determinant_pq = null;
        Colonne voi_min_numero = null;
        Colonne voi_max_numero = null;
        
        try
        {
            voi_id = new Colonne("voi_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            com_code_insee = new Colonne("com_code_insee","CHARACTER",5);
            cdp_code_postal = new Colonne("cdp_code_postal","CHARACTER",5);
            voi_nom = new Colonne("voi_nom","CHARACTER VARYING",32);
            voi_nom_desab = new Colonne("voi_nom_desab","CHARACTER VARYING",255);
            voi_nom_origine = new Colonne("voi_nom_origine","CHARACTER VARYING",255);
            voi_code_fantoir = new Colonne("voi_code_fantoir","CHARACTER",10);
            voi_min_numero = new Colonne("voi_min_numero","INTEGER",0);
            voi_max_numero = new Colonne("voi_max_numero","INTEGER",0);

            voi_type_de_voie = new Colonne("voi_type_de_voie","CHARACTER VARYING",255);
            voi_type_de_voie_pq = new Colonne("voi_type_de_voie_pq","CHARACTER VARYING",255);
            voi_lbl = new Colonne("voi_lbl","CHARACTER VARYING",255);
            voi_lbl_pq = new Colonne("voi_lbl_pq","CHARACTER VARYING",255);
            voi_lbl_sans_articles = new Colonne("voi_lbl_sans_articles","CHARACTER VARYING",255);
            voi_lbl_sans_articles_pq = new Colonne("voi_lbl_sans_articles_pq","CHARACTER VARYING",255);
            voi_mot_determinant = new Colonne("voi_mot_determinant","CHARACTER VARYING",255);
            voi_mot_determinant_pq = new Colonne("voi_mot_determinant_pq","CHARACTER VARYING",255);
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
            t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
            Logger.getLogger("GestionDescriptionTables").log(Level.INFO,"Ne devrait pas arriver",ce);
        }
        
        colonnes.add(voi_id);
        colonnes.add(voi_code_fantoir);
        colonnes.add(voi_nom);
        colonnes.add(voi_nom_desab);
        colonnes.add(voi_nom_origine);
        colonnes.add(com_code_insee);
        colonnes.add(cdp_code_postal);
        colonnes.add(voi_type_de_voie);
        colonnes.add(voi_type_de_voie_pq);
        colonnes.add(voi_lbl);
        colonnes.add(voi_lbl_pq);
        colonnes.add(voi_lbl_sans_articles);
        colonnes.add(voi_lbl_sans_articles_pq);
        colonnes.add(voi_mot_determinant);
        colonnes.add(voi_mot_determinant_pq);
        colonnes.add(voi_min_numero);
        colonnes.add(voi_max_numero);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table voie utilisée par le référentiel pour la mise à jour.<br>
     * <ul>
     *     <li>voi_id CHARACTER VARYING(taillecles)</li>
     *     <li>voi_nom CHARACTER VARYING(32)</li>
     *     <li>voi_nom_desab CHARACTER VARYING(255)</li>
     *     <li>voi_nom_origine CHARACTER VARYING(255)</li>
     *     <li>com_code_insee CHARACTER(5)</li>
     *     <li>cdp_code_postal CHARACTER(5)</li>
     *     <li>voi_min_numero INTEGER</li>
     *     <li>voi_max_numero INTEGER</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableVoiesMaj() throws GestionReferentielException
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voi_id = null;
        Colonne voi_code_fantoir = null;
        Colonne voi_nom = null;
        Colonne voi_nom_desab = null;
        Colonne voi_nom_origine = null;
        Colonne com_code_insee = null;
        Colonne cdp_code_postal = null;
        Colonne voi_min_numero = null;
        Colonne voi_max_numero = null;
        
        try
        {
            voi_id = new Colonne("voi_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_code_fantoir = new Colonne("voi_code_fantoir","CHARACTER",10);
            voi_nom = new Colonne("voi_nom","CHARACTER VARYING",32);
            voi_nom_desab = new Colonne("voi_nom_desab","CHARACTER VARYING",255);
            voi_nom_origine = new Colonne("voi_nom_origine","CHARACTER VARYING",255);
            com_code_insee = new Colonne("com_code_insee","CHARACTER",5);
            cdp_code_postal = new Colonne("cdp_code_postal","CHARACTER",5);
            voi_min_numero = new Colonne("voi_min_numero","integer",0);
            voi_max_numero = new Colonne("voi_max_numero","integer",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(voi_id);
        colonnes.add(voi_code_fantoir);
        colonnes.add(voi_nom);
        colonnes.add(voi_nom_desab);
        colonnes.add(voi_nom_origine);
        colonnes.add(com_code_insee);
        colonnes.add(cdp_code_postal);
        colonnes.add(voi_min_numero);
        colonnes.add(voi_max_numero);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table adresse utilisée par le référentiel.
     * <ul><li>adr_id CHARACTER VARYING(taillecles)</li>
     *     <li>voi_id CHARACTER VARYING(taillecles)</li>
     *     <li>adr_numero INTEGER</li>
     *     <li>adr_rep CHARACTER</li>
     *     <li>geometrie GEOMETRY</li>
     *     <li>t0 TIMESTAMP WITHOUT TIME ZONE</li>
     *     <li>t1 TIMESTAMP WITHOUT TIME ZONE</li></ul>
     * @return
     */
    static DescriptionTable creeDescriptionTableAdressesReferentiel()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne adr_id = null;
        Colonne voi_id = null;
        Colonne adr_numero = null;
        Colonne adr_rep = null;
        Colonne geometrie = null;
        Colonne t0 = null;
        Colonne t1 = null;
        
        try
        {
            adr_id = new Colonne("adr_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id = new Colonne("voi_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            adr_numero = new Colonne("adr_numero","INTEGER",0);
            adr_rep = new Colonne("adr_rep","CHARACTER",1);
            geometrie = Colonne.creeColonneGeometrie();
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
            t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(adr_id);
        colonnes.add(voi_id);
        colonnes.add(adr_numero);
        colonnes.add(adr_rep);
        colonnes.add(geometrie);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
     /**
     * Crée une structure de table adresse utilisée pour la mise à jour.
     * <ul><li>adr_id CHARACTER VARYING(taillecles)</li>
     *     <li>voi_id CHARACTER VARYING(taillecles)</li>
     *     <li>adr_numero INTEGER</li>
     *     <li>adr_rep CHARACTER</li>
     *     <li>geometrie GEOMETRY</li>
     * @return
     */
    static DescriptionTable creeDescriptionTableAdressesMaj()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne adr_id = null;
        Colonne voi_id = null;
        Colonne adr_numero = null;
        Colonne adr_rep = null;
        Colonne geometrie = null;
        
        try
        {
            adr_id = new Colonne("adr_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id = new Colonne("voi_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            adr_numero = new Colonne("adr_numero","INTEGER",0);
            adr_rep = new Colonne("adr_rep","CHARACTER",1);
            geometrie = Colonne.creeColonneGeometrie();
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(adr_id);
        colonnes.add(voi_id);
        colonnes.add(adr_numero);
        colonnes.add(adr_rep);
        colonnes.add(geometrie);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table de voies ambigues utilisé par
     * le référentiel pour optimiser la désambiguité.
     * <ul><li>voa_libelle CHARACTER VARYING(32)</li>
     *     <li>voa_mot CHARACTER VARYING(32)</li>
     *     <li>voa_categorie_ambiguite INTEGER(0)</li>
     */
    public static DescriptionTable creeDescriptionTableVoiesAmbiguesReferentiel()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voa_mot = null;
        Colonne voa_lbl_pq = null;
        Colonne voa_categorie_ambiguite = null;

        try
        {
            voa_mot = new Colonne("voa_mot","CHARACTER VARYING",32);
            voa_categorie_ambiguite = new Colonne("voa_categorie_ambiguite","character varying",255);
            voa_lbl_pq = new Colonne("voa_lbl_pq","character varying",255);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(voa_mot);
        colonnes.add(voa_lbl_pq);
        colonnes.add(voa_categorie_ambiguite);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table permettant de suivre l'évolution
     * des voies dans le référentiel.
     * <ul>
     *     <li>voi_id_precedent CHARACTER VARYING(taillecles)</li>
     *     <li>voi_id_suivant CHARACTER VARYING(taillecles)</li>
     *     <li>t0 TIMESTAMP WITHOUT TIME ZONE(0)</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableHistorisationVoieReferentiel()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voi_id_precedent = null;
        Colonne voi_id_suivant = null;
        Colonne t0 = null;
        
        try
        {
            voi_id_precedent = new Colonne("voi_id_precedent","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id_suivant = new Colonne("voi_id_suivant","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(voi_id_precedent);
        colonnes.add(voi_id_suivant);
        colonnes.add(t0);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table de départements pour la mise à jour.
     * <ul>
     *     <li>dpt_code_departement INTEGER(0)</li>
     *     <li>dpt_projection CHARACTER VARYING(255)</li>
     *     <li>dpt_referentiel CHARACTER VARYING(255)</li>
     *     <li>geometrie</li>
     *     <li>t0</li>
     *     <li>t1</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableDepartementsReferentiel()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne dpt_code_departement = null;
        Colonne geometrie = null;
        Colonne dpt_projection = null;
        Colonne dpt_referentiel = null;
        Colonne t0 = null;
        Colonne t1 = null;
        
        try
        {
            dpt_code_departement = new Colonne("dpt_code_departement","CHARACTER VARYING",5);
            geometrie = Colonne.creeColonneGeometrie();
            dpt_projection = new Colonne("dpt_projection","character varying",255);
            dpt_referentiel = new Colonne("dpt_referentiel","character varying",255);
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
            t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(dpt_code_departement);
        colonnes.add(geometrie);
        colonnes.add(dpt_projection);
        colonnes.add(dpt_referentiel);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table de départements pour la mise à jour.
     * <ul><li>dpt_code_departement INTEGER(0)</li>
     *     <li>dpt_projection CHARACTER VARYING(255)</li>
     *     <li>dpt_referentiel CHARACTER VARYING(255)</li>
     *     <li>geometrie</li>
     */
    public static DescriptionTable creeDescriptionTableDepartementsMaj()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne dpt_code_departement = null;
        Colonne geometrie = null;
        Colonne dpt_projection = null;
        Colonne dpt_referentiel = null;
        
        try
        {
            dpt_code_departement = new Colonne("dpt_code_departement","CHARACTER VARYING",5);
            geometrie = Colonne.creeColonneGeometrie();
            dpt_projection = new Colonne("dpt_projection","character varying",255);
            dpt_referentiel = new Colonne("dpt_referentiel","character varying",255);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(dpt_code_departement);
        colonnes.add(geometrie);
        colonnes.add(dpt_projection);
        colonnes.add(dpt_referentiel);
        
        return new DescriptionTable(colonnes);
    }

    /**
     * Crée une structure de table de regions.
     * <ul>
     *     <li>voi_id_precedent CHARACTER VARYING(taillecles)</li>
     *     <li>voi_id_suivant CHARACTER VARYING(taillecles)</li>
     *     <li>t0 TIMESTAMP WITHOUT TIME ZONE(0)</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableHistorisationVoie()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne voi_id_precedent = null;
        Colonne voi_id_suivant = null;
        Colonne t0 = null;
        
        try
        {
            voi_id_precedent = new Colonne("voi_id_precedent","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id_suivant = new Colonne("voi_id_suivant","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver, sans le faire exprès.
        }
        
        colonnes.add(voi_id_precedent);
        colonnes.add(voi_id_suivant);
        colonnes.add(t0);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table troncon utilisé par le référentiel.
     * <ul><li>tro_id CHARACTER VARYING(taillecles)</li>
     * <li>voi_id_droite CHARACTER VARYING(taillecles)</li>
     * <li>voi_id_gauche CHARACTER VARYING(taillecles)</li>
     * <li>tro_numero_debut_droit INTEGER</li>
     * <li>tro_numero_debut_gauche INTEGER</li>
     * <li>tro_numero_fin_droit INTEGER</li>
     * <li>tro_numero_fin_gauche INTEGER</li>
     * <li>tro_rep_debut_droit CHARACTER</li>
     * <li>tro_rep_debut_gauche CHARACTER</li>
     * <li>tro_rep_fin_droit CHARACTER</li>
     * <li>tro_rep_fin_gauche CHARACTER</li>
     * <li>tro_type_adr CHARACTER VARYING 30</li>
     * <li>geometrie</li>
     * <li>t0 TIMESTAMP WITHOUT TIME ZONE</li>
     * <li>t1 TIMESTAMP WITHOUT TIME ZONE</li>
     * </ul>
     */
    public static DescriptionTable creeDescriptionTableTronconReferentiel()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne tro_id = null;
        Colonne voi_id_droit = null;
        Colonne voi_id_gauche = null;
        Colonne tro_numero_debut_droit = null;
        Colonne tro_numero_debut_gauche = null;
        Colonne tro_numero_fin_droit = null;
        Colonne tro_numero_fin_gauche = null;
        Colonne tro_rep_debut_droit = null;
        Colonne tro_rep_debut_gauche = null;
        Colonne tro_rep_fin_droit = null;
        Colonne tro_rep_fin_gauche = null;
        Colonne tro_typ_adr = null;
        Colonne geometrie = null;        
        Colonne t0 = null;
        Colonne t1 = null;
        
        try
        {
            tro_id = new Colonne("tro_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id_droit = new Colonne("voi_id_droit","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id_gauche= new Colonne("voi_id_gauche","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            tro_numero_debut_droit= new Colonne("tro_numero_debut_droit","INTEGER",0);
            tro_numero_debut_gauche= new Colonne("tro_numero_debut_gauche","INTEGER",0);
            tro_numero_fin_droit= new Colonne("tro_numero_fin_droit","INTEGER",0);
            tro_numero_fin_gauche= new Colonne("tro_numero_fin_gauche","INTEGER",0);
            tro_rep_debut_droit= new Colonne("tro_rep_debut_droit","CHARACTER",1);
            tro_rep_debut_gauche= new Colonne("tro_rep_debut_gauche","CHARACTER",1);
            tro_rep_fin_droit= new Colonne("tro_rep_fin_droit","CHARACTER",1);
            tro_rep_fin_gauche= new Colonne("tro_rep_fin_gauche","CHARACTER",1);
            tro_typ_adr = new Colonne("tro_typ_adr","CHARACTER VARYING",30);
            geometrie= Colonne.creeColonneGeometrie();
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
            t1 = new Colonne("t1","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver.
        }
        
        colonnes.add(tro_id);
        colonnes.add(voi_id_droit);
        colonnes.add(voi_id_gauche);
        colonnes.add(tro_numero_debut_droit);
        colonnes.add(tro_numero_debut_gauche);
        colonnes.add(tro_numero_fin_droit);
        colonnes.add(tro_numero_fin_gauche);
        colonnes.add(tro_rep_debut_droit);
        colonnes.add(tro_rep_debut_gauche);
        colonnes.add(tro_rep_fin_droit);
        colonnes.add(tro_rep_fin_gauche);
        colonnes.add(tro_typ_adr);
        colonnes.add(geometrie);
        colonnes.add(t0);
        colonnes.add(t1);
        
        return new DescriptionTable(colonnes);
    }
    
    /**
     * Crée une structure de table troncon utilisé pour la mise à jour.<br>
     * La différence avec creeDescriptionTableTronconReferentiel consiste en la gestion de l'historisation
     * dans cette dernière.
     * <ul><li>tro_id CHARACTER VARYING(taillecles)</li>
     * <li>voi_id_droite CHARACTER VARYING(taillecles)</li>
     * <li>voi_id_gauche CHARACTER VARYING(taillecles)</li>
     * <li>tro_numero_debut_droit INTEGER</li>
     * <li>tro_numero_debut_gauche INTEGER</li>
     * <li>tro_numero_fin_droit INTEGER</li>
     * <li>tro_numero_fin_gauche INTEGER</li>
     * <li>tro_rep_debut_droit CHARACTER</li>
     * <li>tro_rep_debut_gauche CHARACTER</li>
     * <li>tro_rep_fin_droit CHARACTER</li>
     * <li>tro_rep_fin_gauche CHARACTER</li>
     * <li>tro_nom_droit CHARACTER VARYING(32)</li>
     * <li>tro_nom_gauche CHARACTER VARYING(32></li>
     * <li>com_code_insee_droit CHARACTER(5)</li>
     * <li>cdp_code_postal_droit CHARACTER(5)</li>
     * <li>com_code_insee_gauche CHARACTER(5)</li>
     * <li>cdp_code_postal_gauche CHARACTER(5)</li>
     * <li>geometrie</li></ul>
     */
    public static DescriptionTable creeDescriptionTableTronconMaj()
    {
        ArrayList<Colonne> colonnes = new ArrayList();
        Colonne tro_id = null;
        Colonne voi_id_droit = null;
        Colonne voi_id_gauche = null;
        Colonne tro_code_fantoir_droit = null;
        Colonne tro_code_fantoir_gauche = null;
        Colonne tro_numero_debut_droit = null;
        Colonne tro_numero_debut_gauche = null;
        Colonne tro_numero_fin_droit = null;
        Colonne tro_numero_fin_gauche = null;
        Colonne tro_rep_debut_droit = null;
        Colonne tro_rep_debut_gauche = null;
        Colonne tro_rep_fin_droit = null;
        Colonne tro_rep_fin_gauche = null;
        Colonne tro_nom_droit = null;
        Colonne tro_nom_gauche = null;
        Colonne tro_nom_desab_droit = null;
        Colonne tro_nom_desab_gauche = null;
        Colonne tro_nom_origine_droit = null;
        Colonne tro_nom_origine_gauche = null;
        Colonne com_code_insee_droit = null;
        Colonne com_code_insee_gauche = null;
        Colonne cdp_code_postal_droit = null;
        Colonne cdp_code_postal_gauche = null;
        Colonne tro_typ_adr = null;
        Colonne geometrie = null;
        
        try
        {
            tro_id = new Colonne("tro_id","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id_droit = new Colonne("voi_id_droit","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            voi_id_gauche= new Colonne("voi_id_gauche","CHARACTER VARYING",jdonrefParams.obtientTailleDesCles());
            tro_code_fantoir_droit = new Colonne("tro_code_fantoir_droit","CHARACTER",10);
            tro_code_fantoir_gauche= new Colonne("tro_code_fantoir_gauche","CHARACTER",10);
            tro_numero_debut_droit= new Colonne("tro_numero_debut_droit","INTEGER",0);
            tro_numero_debut_gauche= new Colonne("tro_numero_debut_gauche","INTEGER",0);
            tro_numero_fin_droit= new Colonne("tro_numero_fin_droit","INTEGER",0);
            tro_numero_fin_gauche= new Colonne("tro_numero_fin_gauche","INTEGER",0);
            tro_rep_debut_droit= new Colonne("tro_rep_debut_droit","CHARACTER",1);
            tro_rep_debut_gauche= new Colonne("tro_rep_debut_gauche","CHARACTER",1);
            tro_rep_fin_droit= new Colonne("tro_rep_fin_droit","CHARACTER",1);
            tro_rep_fin_gauche= new Colonne("tro_rep_fin_gauche","CHARACTER",1);
            tro_nom_droit= new Colonne("tro_nom_droit","CHARACTER VARYING",32);
            tro_nom_origine_droit= new Colonne("tro_nom_origine_droit","CHARACTER VARYING",255);
            tro_nom_desab_droit = new Colonne("tro_nom_desab_droit","CHARACTER VARYING",255);
            tro_nom_gauche= new Colonne("tro_nom_gauche","CHARACTER VARYING",32);
            tro_nom_origine_gauche= new Colonne("tro_nom_origine_gauche","CHARACTER VARYING",255);
            tro_nom_desab_gauche = new Colonne("tro_nom_desab_gauche","CHARACTER VARYING",255);
            com_code_insee_droit= new Colonne("com_code_insee_droit","CHARACTER",5);
            com_code_insee_gauche= new Colonne("com_code_insee_gauche","CHARACTER",5);
            cdp_code_postal_droit= new Colonne("cdp_code_postal_droit","CHARACTER",5);
            cdp_code_postal_gauche= new Colonne("cdp_code_postal_gauche","CHARACTER",5);
            tro_typ_adr = new Colonne("tro_typ_adr","CHARACTER VARYING",30);
            geometrie= Colonne.creeColonneGeometrie();
        }
        catch(ColonneException ce)
        {
            // Ne devrait pas arriver.
        }        
        
        colonnes.add(tro_id);
        colonnes.add(voi_id_droit);
        colonnes.add(voi_id_gauche);
        colonnes.add(tro_code_fantoir_droit);
        colonnes.add(tro_code_fantoir_gauche);
        colonnes.add(tro_numero_debut_droit);
        colonnes.add(tro_numero_debut_gauche);
        colonnes.add(tro_numero_fin_droit);
        colonnes.add(tro_numero_fin_gauche);
        colonnes.add(tro_rep_debut_droit);
        colonnes.add(tro_rep_debut_gauche);
        colonnes.add(tro_rep_fin_droit);
        colonnes.add(tro_rep_fin_gauche);
        colonnes.add(tro_nom_droit);
        colonnes.add(tro_nom_gauche);
        colonnes.add(tro_nom_desab_droit);
        colonnes.add(tro_nom_desab_gauche);
        colonnes.add(tro_nom_origine_droit);
        colonnes.add(tro_nom_origine_gauche);
        colonnes.add(com_code_insee_droit);
        colonnes.add(com_code_insee_gauche);
        colonnes.add(cdp_code_postal_droit);
        colonnes.add(cdp_code_postal_gauche);
        colonnes.add(geometrie);
        colonnes.add(tro_typ_adr);
        
        return new DescriptionTable(colonnes);
    }
}