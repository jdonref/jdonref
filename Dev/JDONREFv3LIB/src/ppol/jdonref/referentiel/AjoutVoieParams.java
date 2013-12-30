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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Contient les paramètres de la méthode GestionReferentiel.essayeDAjouterTroncon
 *
 * @author Julien
 */
public class AjoutVoieParams
{
    private ResultSet rsTroncons = null;
    private String suffixe = null;
    private PreparedStatement psUnique = null;
    private PreparedStatement psInsert = null;
    private Connection connection = null;
    private PreparedStatement psMaj = null;
    private PreparedStatement psUniqueId = null;
    private PreparedStatement psInsertId = null;
    private PreparedStatement psChercheFantoir = null;
    private String codeDepartement = null;
    private PreparedStatement psUpdateAdresse = null;
    
    /**
     * Constructeur par défaut, les propriétés ne sont pas définies.
     */
    public AjoutVoieParams()
    {
    }
    
    /**
     * Obtient la requête permettant de vérifier si un code fantoir n'a pas déjà été attribué.
     * @return
     */
    public PreparedStatement getPsChercheFantoire()
    {
        return psChercheFantoir;
    }

    /**
     * Défini la requête permettant de vérifier si un code fantoir n'a pas déjà été attribué.
     * @param psChercheFantoire
     */
    public void setPsChercheFantoir(PreparedStatement psChercheFantoire)
    {
        this.psChercheFantoir=psChercheFantoire;
    }
    
    /**
     * Obtient le code de département concerné.
     * @return
     */
    public String getCodeDepartement()
    {
        return codeDepartement;
    }

    /**
     * Définit le code de département concerné.
     * @param codeDepartement
     */
    public void setCodeDepartement(String codeDepartement)
    {
        this.codeDepartement=codeDepartement;
    }
    
    /**
     * Obtient la requête permettant de vérifier si un identifiant n'a pas déjà été attribué.
     * @return
     */
    public PreparedStatement getPsUniqueId()
    {
        return psUniqueId;
    }
    
    /**
     * Définit la requête permettant de vérifier si un identifiant n'a pas déjà été attribué.
     * @param psUniqueId
     */
    public void setPsUniqueId(PreparedStatement psUniqueId)
    {
        this.psUniqueId = psUniqueId;
    }
    
    /**
     * Obtient la requête permettant de répertorier un nouvel identifiant.
     * @return
     */
    public PreparedStatement getPsInsertId()
    {
        return psInsertId;
    }
    
    /**
     * Définit la requête permettant de répertorier un nouvel identifiant.
     * @param psUniqueId
     */
    public void setPsInsertId(PreparedStatement psInsertId)
    {
        this.psInsertId = psInsertId;
    }
    
    /**
     * Obtient la connection actuelle à la base de donnée qui contient les tables voies, troncons, et idvoies
     * @return
     */
    public Connection getConnection()
    {
        return connection;
    }
    
    /**
     * Définit la connection actuelle à la base de donnée qui contient les tables voies, troncons, et idvoies
     * @param connection
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
    
    /**
     * Obtient la requête qui permet d'ajouter une nouvelle voie.
     * @return
     */
    public PreparedStatement getPsInsert()
    {
        return psInsert;
    }
    
    /**
     * Définit la requête qui permet d'ajouter une nouvelle voie.
     * @param connection
     */
    public void setPsInsert(PreparedStatement psInsert)
    {
        this.psInsert = psInsert;
    }
    
    /**
     * Obtient la requête qui permet de mettre à jour l'identifiant de la voie pour le troncon.
     * @return
     */
    public PreparedStatement getPsMaj()
    {
        return psMaj;
    }
    
    /**
     * Définit la requête qui permet de mettre à jour l'identifiant de la voie pour le troncon.
     * @param connection
     */
    public void setPsMaj(PreparedStatement psMaj)
    {
        this.psMaj = psMaj;
    }
    
    /**
     * Obtient la requête permettant de savoir si la voie existe déjà.
     * @return
     */
    public PreparedStatement getPsUnique()
    {
        return psUnique;
    }
    
    /**
     * Définit la requête permettant de savoir si la voie existe déjà.
     * @param connection
     */
    public void setPsUnique(PreparedStatement psUnique)
    {
        this.psUnique = psUnique;
    }
    
    /**
     * Obtient le troncon en question.
     * @return
     */
    public ResultSet getRsTroncons()
    {
        return rsTroncons;
    }
    
    /**
     * Définit le troncon en question.
     * @param connection
     */
    public void setRsTroncons(ResultSet rsTroncons)
    {
        this.rsTroncons = rsTroncons;
    }

    /**
     * Obtient le suffixe à appliquer aux attributs (droit ou gauche).
     * @return
     */
    public String getSuffixe()
    {
        return suffixe;
    }

    /**
     * Définit le suffixe à appliquer aux attributs (droit ou gauche).
     * @param connection
     */
    public void setSuffixe(String suffixe)
    {
        this.suffixe = suffixe;
    }

    /**
     * Définit la requête qui permet de mettre à jour les informations d'adresse d'une voie.
     * @param psUpdateAdresse
     */
    public void setPsUpdateAdresse(PreparedStatement psUpdateAdresse)
    {
        this.psUpdateAdresse = psUpdateAdresse;
    }

    /**
     * Définit la requête qui permet de mettre à jour les informations d'adresse d'une voie.
     * @param psUpdateAdresse
     */
    public PreparedStatement getPsUpdateAdresse()
    {
        return psUpdateAdresse;
    }

}
