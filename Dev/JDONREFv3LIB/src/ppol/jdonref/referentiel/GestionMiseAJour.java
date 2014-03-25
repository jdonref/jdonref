/*
 * GestionMiseAJour.java
 *
 * Created on 31 mars 2008, 14:34
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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.ParseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import ppol.jdonref.Processus;
import ppol.jdonref.Algos;
//import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.Tables.Colonne;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.Tables.DescriptionTable;
import ppol.jdonref.Tables.GestionTables;
import ppol.jdonref.Tables.Index;
import ppol.jdonref.mots.CategorieAmbiguite;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.mots.RefCle;
import ppol.jdonref.mots.RefNumero;
import ppol.jdonref.mots.ResultatAmbiguite;

/**
 * Offre des méthodes permettant de gérer la mise à jour du référentiel.<br>
 * Les méthodes d'invalidation doivent être utilisées avant les méthodes de création : elles initialisent les marqueurs
 * qui doivent être préalablement initialisés et qui sont utilisés par les méthodes des création.<br>
 * Des méthodes annexes sont utilisées de manière générique : 
 * <ul>
 * <li>definitValeur</li>
 * <li>essayeDAjouterTroncon</li>
 * <li>sontEgales</li>
 * </ul>
 * La modification du schéma d'une table implique la modification des méthodes suivantes:
 * <table>
 * <tr>
 * <td>Communes</td>
 * <td><ul><li>creeNouvellesCommunes</li><li>invalideCommunes</li></ul></td>
 * </tr>
 * <tr>
 * <td>CodesPostaux</td>
 * <td><ul><li>creeTableCodesPostauxMaj</li><li>creeTableCodesPostauxReferentiel</li></uL></td>
 * </tr>
 * <tr>
 * <td>Voies</td>
 * <td><ul><li>creeNouvellesVoies</li><li>invalideVoies</li><li>creeTableVoieReferentiel</li><li>creeTableVoieMaj</li></ul></td>
 * </tr>
 * <tr>
 * <td>Troncons</td>
 * <td><ul><li>invalideTroncons</li><li>creeTableVoieReferentiel</li><li>creeTableVoieMaj</li></ul></td>
 * </tr>
 * </table>
 * Les tables départements et adresses sont gérées par les méthodes creeNouveauxObjets et invalideObjets.<br>
 * Les tables troncons sont aussi gérées par creeNouveauxObjets.<br>
 * Les méthodes creeTableVoieMaj et creeTableVoieReferentiel permettent de créer des tables de voies à partir de tables de troncons.
 * Les méthodes creeTableCodePostauxMaj et creeTableCodePostauxReferentiel permettante de créer des tables de codes postaux à partir
 * des tables de voies.
 * Elles sont utilisées dans le cadre des mises à jour.<br>
 * Enfin, les méthodes suivantes permettent de référencer l'ambiguité en dehors des mises à jour:
 * <ul>
 * <li>calculeClesAmbiguesDansCommune</li>
 * <li>calculeCommunesAmbigueDansVoies</li>
 * <li>calculeVoieAmbigue</li>
 * </ul>
 * Les méthodes sont suivies par deux moyens : les logs d'administration, et l'état courant du processus.
 * @author jmoquet
 */
public class GestionMiseAJour
{
    WKTReader wkt = new WKTReader();
    JDONREFParams jdonrefParams = null;
    
    GestionMots gestionMots = null;
    
    public GestionMiseAJour(GestionMots gestionMots,JDONREFParams params)
    {
        this.gestionMots = gestionMots;
        this.jdonrefParams = params;
    }
    
    /**
     * Définit les paramères génériques.
     */
    public void definitParametres(JDONREFParams params)
    {
        this.jdonrefParams = params;
    }
    
    /**
     * Obtient les paramètres génériques.
     */
    public JDONREFParams getJDONREFParams()
    {
        return jdonrefParams;
    }

    /**
     * Définit une valeur d'une colonne d'un PreparedStatement à partir d'un ResultSet du type
     * spécifié. <br>
     * Les types SQL supportés par la méthode JDONREFParams.obtientType sont supportés. <br>
     * Le type "GEOMETRY" est assimilé à un type STRING pour être comparé.<br>
     * Les chaines null sont remplacée par des chaines vide (pour VARCHAR ET CHAR).<br>
     * Les espaces superflus des chaines sont supprimés (pour VARCHAR ET CHAR).
     * @param colonnePs la colonne dans l'objet PreparedStatement
     * @param colonneRs la colonne dans l'objet ResultSet
     * @param type le type de la valeur concernée.
     * @return true si l'opération a pu être effectuée.
     */
    private boolean definitValeur(PreparedStatement ps,ResultSet rs,int colonnePs,int colonneRs,String type) throws SQLException
    {
        if ("GEOMETRY".compareTo(type)==0)
        {
            ps.setString(colonnePs,rs.getString(colonneRs));
            return true;
        }
        else
        {
            int sqlType = jdonrefParams.obtientType(type);
            
            switch(sqlType)
            {
                case Types.BOOLEAN:
                    ps.setBoolean(colonnePs,rs.getBoolean(colonneRs));
                    break;
                    
                case Types.CHAR:
                    if (rs.getString(colonneRs)==null)
                        ps.setString(colonnePs,null);
                    else
                        ps.setString(colonnePs,rs.getString(colonneRs).trim());
                    break;
                    
                case Types.DATE:
                    ps.setDate(colonnePs,rs.getDate(colonneRs));
                    break;
                    
                case Types.DOUBLE:
                    ps.setDouble(colonnePs,rs.getDouble(colonneRs));
                    break;
                    
                case Types.FLOAT:
                    ps.setFloat(colonnePs,rs.getFloat(colonneRs));
                    break;
                    
                case Types.INTEGER:
                    ps.setInt(colonnePs,rs.getInt(colonneRs));
                    break;
                    
                case Types.REAL:
                    ps.setFloat(colonnePs,rs.getFloat(colonneRs));
                    break;
                    
                case Types.SMALLINT:
                    ps.setInt(colonnePs,rs.getInt(colonneRs));
                    break;
                    
                case Types.TIME:
                    ps.setTime(colonnePs,rs.getTime(colonneRs));
                    break;
                    
                case Types.TIMESTAMP:
                    ps.setTimestamp(colonnePs,rs.getTimestamp(colonneRs));
                    break;
                    
                case Types.VARCHAR:
                    if (rs.getString(colonneRs)==null)
                        ps.setString(colonnePs,null);
                    else
                        ps.setString(colonnePs,rs.getString(colonneRs).trim());
                    break;
                    
                default:
                    return false;
            }
            
            return true;
        }
    }
    
    /**
     * Définit une valeur d'une colonne d'un PreparedStatement à partir d'un ResultSet du type
     * spécifié.
     * Les types SQL suivants sont supportés:<br>
     * <ul>
     *     <li>BOOLEAN</li>
     *     <li>CHAR </li>
     *     <li>DATE</li>
     *     <li>DOUBLE</li>
     *     <li>FLOAT</li>
     *     <li>INTEGER</li>
     *     <li>REAL</li>
     *     <li>SMALLINT</li>
     *     <li>TIME</li>
     *     <li>TIMESTAMP</li>
     * </ul>
     * Les chaines null sont remplacées par des chaines vides<br>
     * Les espaces superflus des chaines sont supprimés.
     * @return true si l'opération a pu être effectuée.
     */
    private boolean definitValeur(PreparedStatement ps,ResultSet rs,int colonne,String type) throws SQLException
    {
        return definitValeur(ps,rs,colonne,colonne,type);
    }
    
    /**
     * Retourne si la valeur à la colonne donnée du type donné a été complétée.<br>
     * Les types SQL de la méthode JDONREFParams.obtientType sont supportés.<br>
     * Si le type est GEOMETRY, la méthode Geometry.compareTo est utilisée (sauf si une des 
     * géométrie est null, auquel cas retourne false).<br>
     * Avant de comparer des chaines, les espaces superflus sont supprimés.<br>
     * Des chaines vides ou null sont considérées comme égales.
     */
    private boolean sontEgaux(ResultSet rs,ResultSet rs2,String colonne,String type) throws SQLException, ParseException, NullPointerException
    {
        Comparable o1 = null, o2 = null;
        colonne = colonne.toLowerCase();
        
        if ("GEOMETRY".compareTo(type)==0)
        {
            if (rs.getString(colonne)!=null && rs2.getString(colonne)!=null)
            {
                Geometry g1 = wkt.read(rs.getString(colonne));
                Geometry g2 = wkt.read(rs2.getString(colonne));
            
                return g1.compareTo(g2)==0;
            }
            else
                return false;
        }
        else
        {
            int sqlType = jdonrefParams.obtientType(type);

            switch (sqlType)
            {
                case Types.BOOLEAN:
                    o1 = new Boolean(rs.getBoolean(colonne));
                    o2 = new Boolean(rs2.getBoolean(colonne));
                    break;

                case Types.CHAR:
                    o1 = rs.getString(colonne);
                    o2 = rs2.getString(colonne);
                    if (o1!=null)
                        o1 = ((String)o1).trim();
                    else
                        o1 = "";
                    if (o2!=null)
                        o2 = ((String)o2).trim();
                    else
                        o2 = "";
                    break;

                case Types.DATE:
                    o1 = rs.getDate(colonne);
                    o2 = rs2.getDate(colonne);
                    break;

                case Types.DOUBLE:
                    o1 = new Double(rs.getDouble(colonne));
                    o2 = new Double(rs2.getDouble(colonne));
                    break;

                case Types.FLOAT:
                    o1 = new Float(rs.getFloat(colonne));
                    o2 = new Float(rs2.getFloat(colonne));
                    break;

                case Types.INTEGER:
                    o1 = new Integer(rs.getInt(colonne));
                    o2 = new Integer(rs2.getInt(colonne));
                    break;

                case Types.REAL:
                    o1 = new Float(rs.getFloat(colonne));
                    o2 = new Float(rs2.getFloat(colonne));
                    break;

                case Types.SMALLINT:
                    o1 = new Integer(rs.getInt(colonne));
                    o2 = new Integer(rs2.getInt(colonne));
                    break;

                case Types.TIME:
                    o1 = rs.getTime(colonne);
                    o2 = rs2.getTime(colonne);
                    break;

                case Types.TIMESTAMP:
                    o1 = rs.getTimestamp(colonne);
                    o2 = rs2.getTimestamp(colonne);
                    break;

                case Types.VARCHAR:
                    o1 = rs.getString(colonne);
                    if (o1!=null)
                        o1 = ((String)o1).trim();
                    else
                        o1 = "";
                    o2 = rs2.getString(colonne);
                    if (o2!=null)
                        o2 = ((String)o2).trim();
                    else
                        o2 = "";
                    break;
            }

            if (o1 != null && o2 != null)
            {
                return o1.compareTo(o2) == 0;
            }
            if (o1==null && o2==null)
                return true;
        }
        
        return false;
    }
    
    /**
     * Retourne si les objets sont égaux ou différents.<br>
     * Les colonnes géométries sont aussi comparées.<br>
     * Si une nouvelle colonne est trouvée, les objets sont différents.
     * @param original l'objet original.
     * @param destination l'objet destination.
     * @return true si les objets sont égaux
     */
    private boolean sontEgales(ResultSet original,ResultSet destination,DescriptionTable dt) throws SQLException, ParseException, NullPointerException
    {
        // Pour chaque colonne,
        for(int i=0;i<dt.getCount();i++)
        {
            Colonne c = dt.getColonne(i);
            
            if (c.estSupplémentaire())
            {
                return false;
            }
            else
            {
                if ("t0".compareTo(c.getNom())==0 ||
                    "t1".compareTo(c.getNom())==0 ||
                    "voi_id_precedent".compareTo(c.getNom())==0 ||
                    "voi_id_gauche".compareTo(c.getNom())==0 ||
                    "voi_id_droit".compareTo(c.getNom())==0)
                {
                    continue;
                }
                else if (c.estGeometrie())
                {
                    if (!sontEgaux(original,destination,c.getNom(),"GEOMETRY")) // Le type GEOMETRY n'est pas référencé sous ce nom.
                        return false;
                }
                else if (!sontEgaux(original,destination,c.getNom(),c.getType()))
                {
                    return false;
                }
            }
        }
        return true;
    }
        
    /**
     * Invalide les objets qui ne sont plus référencés et 
     * marque ceux qui n'ont pas changé et qui doivent être mis à jour.<br>
     * Les objets mis à jour ont alors comme date de fin de validité la date tsdatemoinsun.<br>
     * INDEX A CREER pour optimiser cette méthode:
     * <ul><li>sur tableDestination : id</li>
     *     <li>sur la table Origine : id</li>
     *     <li>sur la table Origine : t1</li></ul>
     * @param code_departement le numéro du département concerné.
     * @param marqueur le nom du marqueur utilisé.
     * @param dt La description de la table.
     * @param connectionDestination La connection au référentiel qui contient la mise à jour.
     * @param connectionOrigine La connection au référentiel à mettre à jour.
     * @param tsdate la date à laquelle la mise à jour est effectuée.
     * @param tsdatemoinsun une date antérieure à la date à laquelle la mise à jour est effectuée.
     */
    public void invalideObjets(Processus p,String id,String tableOrigine, String tableDestination,String marqueur,DescriptionTable dt,Connection connectionDestination,Connection connectionOrigine,Timestamp tsdate,Timestamp tsdatemoinsun) throws SQLException
    {
        int plusvalables=0, traitees=0, inchangees=0, modifiees=0;
        
        p.state[4] = "PREPARE LES REQUETES";
//        GestionLogs.getInstance().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Prépare la requête permettant de trouver la mise à jour d'un objet.
        StringBuilder sb = new StringBuilder();
        sb.append("Select ");
        boolean first = true;
        for(int i=0;i<dt.getCount();i++)
        {
            Colonne c = dt.getColonne(i);
            if ("t0".compareTo(c.getNom())!=0 && "t1".compareTo(c.getNom())!=0)
            {
                if (!first)
                {
                    sb.append(",");
                }
                else
                    first = false;
                if (c.estGeometrie())
                {
                    sb.append("astext(");
                }
                sb.append(c.getNom());
                if (c.estGeometrie())
                {
                    sb.append(") as geometrie");
                }
            }
        }
        sb.append(" from \"");
        sb.append(tableDestination);
        sb.append("\" where ");
        sb.append(id);
        sb.append("=?");
        String requete =  sb.toString();
        PreparedStatement psChercheMaj = connectionDestination.prepareStatement(requete);
        
        // Prépare la requête permettant de modifier la date de validité de l'objet
        requete = "Update \""+tableOrigine+"\" set t1=? where "+id+"=? AND t0<=? AND t1>=?";
        PreparedStatement psInvalide = connectionOrigine.prepareStatement(requete);
        psInvalide.setTimestamp(1,tsdatemoinsun);
        psInvalide.setTimestamp(3,tsdate);
        psInvalide.setTimestamp(4,tsdate);
        
        // Prépare la requête permettant de marquer l'objet
        requete = "Update \""+tableDestination+"\" set \""+marqueur+"\"=? where "+id+"=?";
        PreparedStatement psMarque = connectionDestination.prepareStatement(requete);
        
        // Prépare la requête permettant d'énumérer toutes les objets actuels.
        // Toutes les informations sont nécessaires pour être comparées.
        sb = new StringBuilder();
        sb.append("SELECT ");
        first = true;
        for(int i=0;i<dt.getCount();i++)
        {
            Colonne c = dt.getColonne(i);
            if ("t0".compareTo(c.getNom())!=0 && "t1".compareTo(c.getNom())!=0)
            {
                if (!first)
                {
                    sb.append(",");
                }
                else
                    first = false;
                if (c.estGeometrie())
                {
                    sb.append("astext(");
                }
                sb.append(c.getNom());
                if (c.estGeometrie())
                {
                    sb.append(") as geometrie");
                }
            }
        }
        sb.append(" from \"");
        sb.append(tableOrigine);
        sb.append("\" where t1>=?"); // Seul les objets valides à la date sont conservés
        requete = sb.toString();
        PreparedStatement psCherche = connectionOrigine.prepareStatement(requete);
        psCherche.setTimestamp(1,tsdate);
        
        if (p.stop)
        {
            psChercheMaj.close();
            psCherche.close();
            psMarque.close();
            psInvalide.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        // Pour toutes les objets valides à la date,
        p.state[4] = "CHERCHE LES OBJETS";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES OBJETS");
        ResultSet rsCherche = psCherche.executeQuery();
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            String id_value = rsCherche.getString(id);
            
            psChercheMaj.setString(1,id_value);
            ResultSet rsChercheMaj = psChercheMaj.executeQuery();
            
            // Une mise à jour est trouvée
            if (rsChercheMaj.next())
            {
                try
                {
                    // Marquage différent si une mise à jour est effectuée ou pas,
                    if (sontEgales(rsCherche, rsChercheMaj, dt))
                    {
                        inchangees++;
                        psMarque.setInt(1, 1);
                    }
                    else
                    {
                        modifiees++;
                        psMarque.setInt(1, 0);

                        // Dans ce cas, l'objet est invalidé.
                        // mais n'est pas compté parmi les objets qui ne sont plus valables.
                        psInvalide.setString(2,id_value);
                        psInvalide.execute();
                    }
                    
                    psMarque.setString(2, id_value);
                    psMarque.execute();
                }
                catch(ParseException pe)
                {
                    p.resultat.add("Erreur de géométrie sur "+p.state[3]+": "+id_value);
                    jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Erreur de géométrie sur "+p.state[3]+": "+id_value);
                }
                catch(NullPointerException npe)
                {
                    p.resultat.add("Erreur de géométrie sur "+p.state[3]+": "+id_value);
                    jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Erreur de géométrie sur "+p.state[3]+": "+id_value);                    
                }
            }
            else
            // L'objet n'est plus valable, il est invalidé.
            {
                plusvalables++;
                psInvalide.setString(2,id_value);
                psInvalide.execute();
            }
            rsChercheMaj.close();
            
            // Valide les modifications à chaque objet.
            connectionOrigine.commit();
            connectionDestination.commit();
            traitees++;
            if (traitees%500==0)
                p.state[6] = Integer.toString(traitees);
        }
        psChercheMaj.close();
        psCherche.close();
        rsCherche.close();
        psMarque.close();
        psInvalide.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }

        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INVALIDATION DES "+p.state[3]+" TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets invalidés = "+plusvalables);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets inchangés = "+inchangees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets modifiés = "+modifiees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets traités = "+traitees);
        
        p.state[4] = "TERMINE";
        p.resultat.add("INVALIDATION DES "+p.state[3]);
        p.resultat.add("Nombre d'objets invalidés = "+plusvalables);
        p.resultat.add("Nombre d'objets inchangés = "+inchangees);
        p.resultat.add("Nombre d'objets modifiés = "+modifiees);
        p.resultat.add("Nombre d'objets traités = "+traitees);
    }
    
    /**
     * Invalide les codes postaux.<br>
     * Les codes postaux déjà existant sont marqués 1 dans la base de mise à jour.<br>
     * L'état du processus doit être un tableau de 8 chaines dont:
     * <ul><li>La chaine 5 est utilisée pour l'état de la méthode</li>
     *     <li>La chaine 7 est utilisée pour le compte des lignes traitées</li>
     *     <li>La chaine 8 est utilisée pour le total des lignes à traiter</li>
     * </ul>
     * @param p
     */
    public void invalideCodePostaux(Processus p,String code_departement,String marqueur,Timestamp tsdate,Timestamp tsdatemoinsun,Connection connectionMaj,Connection connectionRef) throws SQLException
    {
        int invalidees=0, traitees=0, inchangees=0;
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE REQUETES");
        p.state[4] = "PREPARE REQUETES";
        
        // Prépare la requête permettant de compter les lignes à traiter.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(*) FROM cdp_codes_postaux WHERE dpt_code_departement=? AND t1>=?");
        PreparedStatement psCompte = connectionRef.prepareStatement(sb.toString());
        psCompte.setString(1,code_departement);
        psCompte.setTimestamp(2,tsdate);
        
        ResultSet rsCompte = psCompte.executeQuery();
        
        rsCompte.next();
        
        int compte = rsCompte.getInt(1);
        
        // Prépare la requête permettant de chercher les codes postaux actuels.
        sb.setLength(0);
        sb.append("SELECT com_code_insee,cdp_code_postal FROM \"cdp_codes_postaux\" WHERE dpt_code_departement=? AND t1>=?");
        PreparedStatement psCherche = connectionRef.prepareStatement(sb.toString());
        psCherche.setString(1,code_departement);
        psCherche.setTimestamp(2,tsdate);
        
        // Prépare la requête permettant de trouver si le code postal existe toujours.
        sb.setLength(0);
        sb.append("SELECT com_code_insee,cdp_code_postal FROM \"cdp_codes_postaux\" WHERE dpt_code_departement=? AND com_code_insee=? AND cdp_code_postal=?");
        PreparedStatement psChercheMaj = connectionMaj.prepareStatement(sb.toString());
        psChercheMaj.setString(1,code_departement);
        
        // Prépare la requête permettant de marquer un code postal qui existe toujours
        sb.setLength(0);
        sb.append("UPDATE \"cdp_codes_postaux\" set \"");
        sb.append(marqueur);
        sb.append("\"=1 WHERE dpt_code_departement=? AND com_code_insee=? AND cdp_code_postal=?");
        PreparedStatement psMarque = connectionMaj.prepareStatement(sb.toString());
        psMarque.setString(1,code_departement);
        
        // Prépare la requête permettant de corriger la date de validité des codes postaux qui n'existent plus
        sb.setLength(0);
        sb.append("UPDATE \"cdp_codes_postaux\" set t1=? WHERE dpt_code_departement=? AND com_code_insee=? AND cdp_code_postal=? AND t1>=?");
        PreparedStatement psCorrige = connectionRef.prepareStatement(sb.toString());
        psCorrige.setTimestamp(1,tsdatemoinsun);
        psCorrige.setString(2,code_departement);
        psCorrige.setTimestamp(5,tsdate);
        
        ResultSet rsCherche = psCherche.executeQuery();
        
        if (p.stop)
        {
            psCherche.close();
            psChercheMaj.close();
            psMarque.close();
            psCorrige.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        p.state[7] = "SUR "+compte;
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT DE "+compte+" codes postaux");
        
        while(!p.stop && rsCherche.next())
        {
            String code_insee = rsCherche.getString(1);
            String code_postal = rsCherche.getString(2);
            
            psChercheMaj.setString(2,code_insee);
            psChercheMaj.setString(3,code_postal);
            ResultSet rsChercheMaj = psChercheMaj.executeQuery();
            
            if (rsChercheMaj.next())
            {
                psMarque.setString(2,code_insee);
                psMarque.setString(3,code_postal);
                psMarque.execute();
                
                inchangees++;
            }
            else
            {
                psCorrige.setString(3,code_insee);
                psCorrige.setString(4,code_postal);
                psCorrige.execute();
                
                invalidees++;
            }
            
            traitees++;
            
            if (traitees%200==0)
                p.state[6] = Integer.toString(traitees);
            
            rsChercheMaj.close();
            
            connectionMaj.commit();
            connectionRef.commit();
        }
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        rsCherche.close();
        psCherche.close();
        psChercheMaj.close();
        psMarque.close();
        psCorrige.close();
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INVALIDATION DES CODES POSTAUX TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CODES POSTAUX INCHANGES: "+inchangees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CODES POSTAUX INVALIDES: "+invalidees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CODES POSTAUX TRAITEES: "+traitees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CODES POSTAUX TOTAL: "+compte);
        p.resultat.add("INVALIDATION DES CODES POSTAUX");
        p.resultat.add("CODES POSTAUX INCHANGES: "+inchangees);
        p.resultat.add("CODES POSTAUX INVALIDES: "+invalidees);
        p.resultat.add("CODES POSTAUX TRAITEES: "+traitees);
        p.resultat.add("CODES POSTAUX TOTAL: "+compte);
    }
    
    /**
     * Invalide les troncons qui ne sont plus référencés et 
     * marque ceux qui n'ont pas changé et qui doivent être mis à jour.<br>
     * Les objets mis à jour ont alors comme date de fin de validité la date tsdatemoinsun.<br>
     * INDEX A CREER pour optimiser la méthode:
     * <ul><li>Sur tableDestination : id_troncon</li>
     *     <li>Sur tableOrigine : voi_id_droit, voi_id_gauche,t0,t1,tro_id</li>
     *     <li>Sur tableVoieOrigine : voi_id, t0, t1</li></ul>
     * @param id la colonne clé primaire dans les tables.
     * @param tableVoieOrigine le nom de la table de voie à mettre à jour
     * @param tableVoieAmbiguesOrigine le nom de la table de voies ambigues à mettre à jour
     * @param tableOrigine la table de troncon à mettre à jour
     * @param tableDestination la table de troncon qui contient la mise à jour
     * @param marqueur le nom du marqueur utilisé.
     * @param dt La description de la table.
     * @param connectionDestination La connection au référentiel qui contient la mise à jour.
     * @param connectionOrigine La connection au référentiel à mettre à jour.
     * @param tsdate la date à laquelle la mise à jour est effectuée.
     * @param tsdatemoinsun une date antérieure à la date à laquelle la mise à jour est effectuée.
     */
    public void invalideTroncons(Processus p,String code_departement,String marqueur,String nomTableTronconReferentiel,Connection connectionMaj,Connection connectionReferentiel,Timestamp tsdate,Timestamp tsdatemoinsun) throws SQLException
    {
        int invalidees=0, traitees=0, inchangees=0, modifiees=0;
        
        p.state[4] = "PREPARE REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE REQUETES");
        
        // Prépare la requête permettant de trouver la mise à jour d'un troncon.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_id_droit,voi_id_gauche,");
        sb.append("tro_numero_debut_droit,tro_numero_debut_gauche,tro_numero_fin_droit,tro_numero_fin_gauche,");
        sb.append("tro_rep_debut_droit,tro_rep_debut_gauche,tro_rep_fin_droit,tro_rep_fin_gauche,");
        sb.append("astext(geometrie) ");
        sb.append("FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("tro_troncons_").append(code_departement);
        sb.append(GestionTables.getTroTronconsTableName(code_departement));
        sb.append("\" ");
        sb.append("WHERE tro_id=? LIMIT 1");
        String requete =  sb.toString();
        PreparedStatement psChercheMaj = connectionMaj.prepareStatement(requete);
        
        // Prépare la requête permettant de modifier la date de validité du troncon
        sb.setLength(0);
        sb.append("UPDATE \"");
        sb.append(nomTableTronconReferentiel);
        sb.append("\" set t1=? where tro_id=? AND t0<=? AND t1>=?");
        PreparedStatement psInvalide = connectionReferentiel.prepareStatement(sb.toString());
        psInvalide.setTimestamp(1,tsdatemoinsun);
        psInvalide.setTimestamp(3,tsdate);
        psInvalide.setTimestamp(4,tsdate);        
        
        // Prépare la requête permettant de marquer l'objet
        sb.setLength(0);
        sb.append("UPDATE \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("tro_troncons_").append(code_departement);
        sb.append(GestionTables.getTroTronconsTableName(code_departement));
        sb.append("\" set \""+marqueur+"\"=? where tro_id=?");
        PreparedStatement psMarque = connectionMaj.prepareStatement(sb.toString());
        
        // Prépare la requête permettant d'historiser une modification de voie.
        sb.setLength(0);
        sb.append("INSERT INTO \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("vhi_voies_historisee_").append(code_departement);
        sb.append(GestionTables.getVhiVoiesHistoriseeTableName(code_departement));
        sb.append("\" (voi_id_precedent,voi_id_suivant,t0) ");
        sb.append("VALUES (?,?,?)");
        PreparedStatement psInsertHistorisee = connectionReferentiel.prepareStatement(sb.toString());
        psInsertHistorisee.setTimestamp(3, tsdate);
        
        // Prépare la requête permettant de vérifier si une voie n'est pas déjà historisée
        sb.setLength(0);
        sb.append("SELECT voi_id_precedent ");
        sb.append("FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("vhi_voies_historisee_").append(code_departement);
        sb.append(GestionTables.getVhiVoiesHistoriseeTableName(code_departement));
        sb.append("\" WHERE voi_id_precedent=? AND voi_id_suivant=? AND t0=? LIMIT 1");
        PreparedStatement psChercheHistorisee = connectionReferentiel.prepareStatement(sb.toString());
        psChercheHistorisee.setTimestamp(3,tsdate);
        
        // Prépare la requête permettant d'énumérer toutes les objets actuels.
        // Toutes les informations sont nécessaires pour être comparées (sauf celles qui 
        // permettent de construire les voies : code_insee, nom).
        sb.setLength(0);
        sb.append("SELECT tro_id,voi_id_droit,voi_id_gauche,");
        sb.append("tro_numero_debut_droit,tro_numero_debut_gauche,tro_numero_fin_droit,tro_numero_fin_gauche,");
        sb.append("tro_rep_debut_droit,tro_rep_debut_gauche,tro_rep_fin_droit,tro_rep_fin_gauche,");
        sb.append("astext(geometrie) ");
        sb.append("FROM \"");
        sb.append(nomTableTronconReferentiel);
        sb.append("\" WHERE t1>=? "); // Seul les objets valides à la date sont recherchés.
        PreparedStatement psCherche = connectionReferentiel.prepareStatement(sb.toString());
        psCherche.setTimestamp(1,tsdate);
        
        // Pour tous les objets valides à la date,
        p.state[4] = "CHERCHE LES TRONCONS";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES TRONCONS");
        ResultSet rsCherche = psCherche.executeQuery();
        
        if (p.stop)
        {
            psCherche.close();
            psChercheMaj.close();
            psMarque.close();
            psChercheHistorisee.close();
            psInsertHistorisee.close();
            psInvalide.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            String id_value = rsCherche.getString(1);
            
            psChercheMaj.setString(1,id_value);
            ResultSet rsChercheMaj = psChercheMaj.executeQuery();
            
            // Une mise à jour est trouvée
            if (rsChercheMaj.next())
            {
                boolean newiddroit = false;
                boolean newidgauche = false;
                String voi_id_droit_actuel = rsCherche.getString(2);
                String voi_id_gauche_actuel = rsCherche.getString(3);
                
                String voi_id_droit_nv = rsChercheMaj.getString(1);
                String voi_id_gauche_nv = rsChercheMaj.getString(2);
                
                // Si le troncon a été modifié,
                if ((newiddroit = Algos.compareString(voi_id_droit_actuel,voi_id_droit_nv)) ||  
                    (newidgauche = Algos.compareString(voi_id_gauche_actuel,voi_id_gauche_nv)) || 
                    rsCherche.getInt(4)!=rsChercheMaj.getInt(3) || 
                    rsCherche.getInt(5)!=rsChercheMaj.getInt(4) || 
                    rsCherche.getInt(6)!=rsChercheMaj.getInt(5) || 
                    rsCherche.getInt(7)!=rsChercheMaj.getInt(6) || 
                    Algos.compareString(rsCherche.getString(8),rsChercheMaj.getString(7)) || 
                    Algos.compareString(rsCherche.getString(9),rsChercheMaj.getString(8)) || 
                    Algos.compareString(rsCherche.getString(10),rsChercheMaj.getString(9)) || 
                    Algos.compareString(rsCherche.getString(11),rsChercheMaj.getString(10)) || 
                    Algos.compareString(rsCherche.getString(12),rsChercheMaj.getString(11)))
                {
                    if (newiddroit && // Il est alors nécessaire d'historiser ce changement
                        voi_id_droit_nv!=null && voi_id_droit_nv.length()>0 &&  // si toutefois, la voie n'a pas disparu,
                        voi_id_droit_actuel!=null && voi_id_droit_actuel.length()>0)  // et ne vient pas d'apparaitre.
                    {
                        psChercheHistorisee.setString(1, voi_id_droit_actuel);
                        psChercheHistorisee.setString(2, voi_id_droit_nv);
                        ResultSet rsChercheHistorisee = psChercheHistorisee.executeQuery();
                        if (!rsChercheHistorisee.next())
                        {
                            psInsertHistorisee.setString(1,voi_id_droit_actuel);
                            psInsertHistorisee.setString(2,voi_id_droit_nv);
                        }
                        rsChercheHistorisee.close();
                    }
                    if (newidgauche && // idem
                        voi_id_gauche_nv!=null && voi_id_gauche_nv.length()>0 &&
                        voi_id_gauche_actuel!=null && voi_id_gauche_actuel.length()>0) 
                    {
                        psChercheHistorisee.setString(1, voi_id_gauche_actuel);
                        psChercheHistorisee.setString(2,voi_id_gauche_nv);
                        ResultSet rsChercheHistorisee = psChercheHistorisee.executeQuery();
                        if (!rsChercheHistorisee.next())
                        {
                            psInsertHistorisee.setString(1,voi_id_gauche_actuel);
                            psInsertHistorisee.setString(2,voi_id_gauche_nv);
                        }
                        rsChercheHistorisee.close();
                    }
                    
                    modifiees++;
                    psMarque.setInt(1,0); // marquage 0

                    // Et dans ce cas, invalide l'objet.
                    psInvalide.setString(2,id_value);
                    psInvalide.execute();
                }
                else // sinon
                {
                    inchangees++;
                    psMarque.setInt(1,1); // marquage 1
                }
                psMarque.setString(2,id_value);
                psMarque.execute();
            }
            else
            // Le troncon n'est plus valable, il est invalidé.
            {
                invalidees++;
                psInvalide.setString(2,id_value);
                psInvalide.execute();
            }
            
            // Valide les modifications à chaque troncon.
            connectionReferentiel.commit();
            connectionMaj.commit();
            traitees++;
            if (traitees%500==0)
                p.state[6] = Integer.toString(traitees);
        }
        
        rsCherche.close();
        psCherche.close();
        psChercheMaj.close();
        psMarque.close();
        psChercheHistorisee.close();
        psInsertHistorisee.close();
        psInvalide.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INVALIDATION DES TRONCONS");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre de troncons invalidés = "+invalidees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre de troncons inchangés = "+inchangees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre de troncons modifiés = "+modifiees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre de troncons traités = "+traitees);
        p.state[4] = "TERMINE";
        p.resultat.add("INVALIDATION DES TRONCONS");
        p.resultat.add("Nombre de troncons invalidés = "+invalidees);
        p.resultat.add("Nombre de troncons inchangés = "+inchangees);
        p.resultat.add("Nombre de troncons modifiés = "+modifiees);
        p.resultat.add("Nombre de troncons traités = "+traitees);
    }
    
    /**
     * Invalide les communes qui ne sont plus référencés dans la mise à jour et 
     * marque ceux qui n'ont pas changé et qui doivent être mis à jour.<br>
     * La valeur du marqueur est un masque dont les champs sont défini ainsi:
     * <ul>
     * <li>1 si la valeur est inchangée</li>
     * <li>2 si le nom, le code postal, ou le com_code_insee_commune a changé</li>
     * </ul>
     * Les objets mis à jour ont alors comme date de fin de validité la date tsdatemoinsun.<br>
     * @param marqueur le nom du marqueur utilisé.
     * @param connectionMaj La connection au référentiel qui contient la mise à jour.
     * @param connectionReferentiel La connection au référentiel à mettre à jour.
     * @param tsdate la date à laquelle la mise à jour est effectuée.
     * @param tsdatemoinsun une date antérieure à la date à laquelle la mise à jour est effectuée.
     */
    public void invalideCommunes(Processus p,String marqueur,Connection connectionMaj,Connection connectionReferentiel,Timestamp tsdate,Timestamp tsdatemoinsun) throws SQLException
    {
        int invalidees=0, traitees=0, inchangees=0, modifiees=0;
        
        p.state[4] = "PREPARE LES REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Prépare la requête permettant de trouver la mise à jour d'un objet.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT com_code_insee,com_nom,com_code_insee_commune,dpt_code_departement,astext(geometrie) ");
        sb.append("FROM \"com_communes\" ");
        sb.append("WHERE com_code_insee=?");
        String requete =  sb.toString();
        PreparedStatement psChercheMaj = connectionMaj.prepareStatement(requete);
        
        // Prépare la requête permettant de modifier la date de validité de l'objet
        requete = "Update \"com_communes\" set t1=? where com_code_insee=? AND t0<=? AND ?<=t1";
        PreparedStatement psInvalide = connectionReferentiel.prepareStatement(requete);
        psInvalide.setTimestamp(1,tsdatemoinsun);
        psInvalide.setTimestamp(3,tsdate);
        psInvalide.setTimestamp(4,tsdate);
        
        // Prépare la requête permettant de marquer l'objet
        requete = "Update \"com_communes\" set \""+marqueur+"\"=? where com_code_insee=?";
        PreparedStatement psMarque = connectionMaj.prepareStatement(requete);
        
        // Prépare la requête permettant d'énumérer toutes les objets actuels.
        // Toutes les informations sont nécessaires pour être comparées.
        sb = new StringBuilder();
        sb.append("SELECT com_code_insee,com_nom,com_code_insee_commune,dpt_code_departement,astext(geometrie) ");
        sb.append("FROM com_communes ");
        sb.append("WHERE com_communes.t1>=? ");
        requete = sb.toString();
        PreparedStatement psCherche = connectionReferentiel.prepareStatement(requete);
        psCherche.setTimestamp(1,tsdate);
        
        // Pour toutes les objets valides à la date,
        p.state[4] = "CHERCHE LES COMMUNES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES COMMUNES");
        ResultSet rsCherche = psCherche.executeQuery();
        
        if (p.stop)
        {
            psCherche.close();
            psChercheMaj.close();
            psMarque.close();
            psInvalide.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            String id_value = rsCherche.getString(1);
            
            psChercheMaj.setString(1,id_value);
            ResultSet rsChercheMaj = psChercheMaj.executeQuery();
            
            // Une mise à jour est trouvée
            if (rsChercheMaj.next())
            {
                boolean nomachange = false;
                boolean attributachange = false;
                
                if (Algos.compareString(rsCherche.getString(2),rsChercheMaj.getString(2))) // le nom
                {
                    nomachange = true;
                }
                if (Algos.compareString(rsCherche.getString(3),rsChercheMaj.getString(3))) // le code insee de la commune
                {
                    attributachange = true;
                }
                if (Algos.compareString(rsCherche.getString(4),rsChercheMaj.getString(4))) // le code departement
                {
                    nomachange = true;
                    attributachange = true;
                }
                if (Algos.compareString(rsCherche.getString(5),rsChercheMaj.getString(5))) // la geometrie
                {
                    attributachange = true;
                }

                // Invalide les objets des tables en conséquence, et définit la valeur du marqueur.
                int valeur_marqueur = 1;
                if (nomachange || attributachange)
                {
                    psInvalide.setTimestamp(1,tsdatemoinsun);
                    psInvalide.setString(2,id_value);
                    psInvalide.execute();
                    valeur_marqueur |= 2;
                    modifiees++;
                }
                else
                    inchangees++;
                
                // Puis assigne le marquage.
                psMarque.setInt(1,valeur_marqueur);
                psMarque.setString(2,id_value);
                psMarque.execute();
            }
            else
            // sinon, l'objet n'est plus valable, et il est invalidé.
            {
                invalidees++;
                psInvalide.setTimestamp(1,tsdatemoinsun);
                psInvalide.setString(2,id_value);
                psInvalide.execute();
            }
            rsChercheMaj.close();
            
            // Valide les modifications à chaque objet.
            connectionReferentiel.commit();
            connectionMaj.commit();
            traitees++;
            if (traitees%500==0)
                p.state[6] = Integer.toString(traitees);
        }
        rsCherche.close();
        psCherche.close();
        psChercheMaj.close();
        psMarque.close();
        psInvalide.close();

        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INVALIDATION DES COMMUNES ET ARRONDISSEMENTS");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets invalidés = "+invalidees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets inchangés = "+inchangees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets modifiées = "+modifiees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets traitées = "+traitees);
        p.state[4] = "TERMINE";
        p.resultat.add("INVALIDATION DES COMMUNES ET ARRONDISSEMENTS");
        p.resultat.add("Nombre d'objets invalidés = "+invalidees);
        p.resultat.add("Nombre d'objets inchangés = "+inchangees);
        p.resultat.add("Nombre d'objets modifiées = "+modifiees);
        p.resultat.add("Nombre d'objets traitées = "+traitees);
    }
    
    /**
     * Invalide les objets qui ne sont plus référencés et 
     * marque ceux qui n'ont pas changé et qui doivent être mis à jour.<br>
     * Les objets mis à jour ont alors comme date de fin de validité la date tsdatemoinsun.<br>
     * @param code_departement le numéro du département concerné.
     * @param marqueur le nom du marqueur utilisé.
     * @param connectionMaj La connection au référentiel qui contient la mise à jour.
     * @param connectionReferentiel La connection au référentiel à mettre à jour.
     * @param tsdate la date à laquelle la mise à jour est effectuée.
     * @param tsdatemoinsun une date antérieure à la date à laquelle la mise à jour est effectuée.
     */
    public void invalideVoies(Processus p,String code_departement,String marqueur,Connection connectionMaj,Connection connectionReferentiel,Timestamp tsdate,Timestamp tsdatemoinsun) throws SQLException
    {
        int invalidees=0, traitees=0, inchangees=0, modifiees=0;
        
        p.state[4] = "Prepare les requêtes";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Prepare les requêtes");
        
        // Prépare la requête permettant de trouver la mise à jour d'un objet.
        StringBuilder sb = new StringBuilder();
        sb.append("Select voi_id,voi_nom,com_code_insee,cdp_code_postal,");
        sb.append("voi_min_numero,voi_max_numero,voi_code_fantoir,voi_nom_desab from \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voi_voies_").append(code_departement);
        sb.append(GestionTables.getVoiVoiesTableName(code_departement));
        sb.append("\" where voi_id=?");
        String requete =  sb.toString();
        PreparedStatement psChercheMaj = connectionMaj.prepareStatement(requete);
        
        // Prépare la requête permettant de modifier la date de validité de l'objet
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        requete = "Update \"voi_voies_"+code_departement+"\" set t1=? where voi_id=? AND t0<=? AND t1>=?";
        requete = "Update \"" + GestionTables.getVoiVoiesTableName(code_departement) + "\" set t1=? where voi_id=? AND t0<=? AND t1>=?";
        PreparedStatement psInvalide = connectionReferentiel.prepareStatement(requete);
        psInvalide.setTimestamp(3, tsdate);
        psInvalide.setTimestamp(4, tsdate);
        
        // Prépare la requête permettant de marquer l'objet
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        requete = "Update \"voi_voies_"+code_departement+"\" set \""+marqueur+"\"=? where voi_id=?";
        requete = "Update \"" + GestionTables.getVoiVoiesTableName(code_departement) + "\" set \""+marqueur+"\"=? where voi_id=?";
        PreparedStatement psMarque = connectionMaj.prepareStatement(requete);
        
        // Prépare la requête permettant d'énumérer toutes les objets actuels.
        // Toutes les informations sont nécessaires pour être comparées.
        sb = new StringBuilder();
        sb.append("SELECT voi_id,voi_nom,com_code_insee,cdp_code_postal,");
        sb.append("voi_min_numero,voi_max_numero,voi_code_fantoir,voi_nom_desab from \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voi_voies_").append(code_departement);
        sb.append(GestionTables.getVoiVoiesTableName(code_departement));
        sb.append("\" where t1>=?"); // Seul les objets valides à la date sont conservés
        requete = sb.toString();
        PreparedStatement psCherche = connectionReferentiel.prepareStatement(requete);
        psCherche.setTimestamp(1,tsdate);
        
        // Pour toutes les objets valides à la date,
        p.state[4] = "CHERCHE LES VOIES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES VOIES");
        ResultSet rsCherche = psCherche.executeQuery();
        
        if (p.stop)
        {
            psChercheMaj.close();
            psCherche.close();
            psMarque.close();
            psInvalide.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            String id_value = rsCherche.getString(1);
            
            psChercheMaj.setString(1,id_value);
            ResultSet rsChercheMaj = psChercheMaj.executeQuery();
            
            // Une mise à jour est trouvée
            if (rsChercheMaj.next())
            {
                // Si la voie a été modifiée,
                if (Algos.compareString(rsCherche.getString(2),rsChercheMaj.getString(2)) ||
                    Algos.compareString(rsCherche.getString(3),rsChercheMaj.getString(3)) ||
                    Algos.compareString(rsCherche.getString(4),rsChercheMaj.getString(4)) ||
                    Algos.compareString(rsCherche.getString(5),rsChercheMaj.getString(5)) ||
                    Algos.compareString(rsCherche.getString(6),rsChercheMaj.getString(6)) ||
                    Algos.compareString(rsCherche.getString(7),rsChercheMaj.getString(7)) ||
                    Algos.compareString(rsCherche.getString(8),rsChercheMaj.getString(8)))
                {
                    modifiees++;
                    psMarque.setInt(1,0); // marquage 0
                    
                    // Dans ce cas, sa date de validité est invalidée.
                    psInvalide.setTimestamp(1,tsdatemoinsun);
                    psInvalide.setString(2,id_value);
                    psInvalide.execute();
                }
                else // sinon
                {
                    inchangees++;
                    psMarque.setInt(1,1); // marquage 1
                }
                psMarque.setString(2,id_value);
                
                psMarque.execute();
            }
            else
            // sinon, l'objet n'est plus valable, et il est invalidé.
            {
                invalidees++;
                psInvalide.setTimestamp(1,tsdatemoinsun);
                psInvalide.setString(2,id_value);
                psInvalide.execute();
            }
            rsChercheMaj.close();
                        
            // Valide les modifications à chaque objet.
            connectionReferentiel.commit();
            connectionMaj.commit();
            traitees++;
            if (traitees%500==0)
                p.state[6] = Integer.toString(traitees);
        }
        psChercheMaj.close();
        rsCherche.close();
        psCherche.close();
        psMarque.close();
        psInvalide.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INVALIDATION DES VOIES TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets invalidés = "+invalidees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets inchangés = "+inchangees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets modifiées = "+modifiees);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre d'objets traitées = "+traitees);
        p.state[4] = "TERMINE";
        p.resultat.add("INVALIDATION DES VOIES");
        p.resultat.add("Nombre d'objets invalidés = "+invalidees);
        p.resultat.add("Nombre d'objets inchangés = "+inchangees);
        p.resultat.add("Nombre d'objets modifiées = "+modifiees);
        p.resultat.add("Nombre d'objets traitées = "+traitees);
    }    
    
    /**
     * Invalide tous les tronçons à la date donnée.
     */
    public void invalideTroncons(String tableTroncon,Connection connectionOrigine,Timestamp tsdate,Timestamp tsdatemoinsun) throws SQLException
    {
        // Prépare la requête permettant d'invalider tous les tronçons
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE \"");
        sb.append(tableTroncon);
        sb.append("\" set t1=? where t0<=? and t1>=?");
        PreparedStatement psUpdate = connectionOrigine.prepareStatement(sb.toString());
        psUpdate.setTimestamp(1,tsdatemoinsun);
        psUpdate.setTimestamp(2,tsdate);
        psUpdate.setTimestamp(3,tsdate);
        
        psUpdate.execute();
        
        connectionOrigine.commit();
    }
    
    /**
     * Crée dans le référentiel les codes postaux de la mise à jour qui sont non marqués.<br>
     * N.B.: t0 et t1 doivent être les dernières colonnes dans la table du référentiel. 
     * @param tsdate la date à laquelle est faîte la mise à jour.
     * @param tsinfini une date considérée comme infinie.
     */
    public void creeNouveauxCodesPostaux(Processus p,String codeDepartement,String marqueur,Connection connectionReferentiel,Connection connectionMaj,Timestamp tsdate,Timestamp tsinfini) throws SQLException
    {
        int objetscrees = 0;
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        p.state[4] = "PREPARE LES REQUETES";
        
        // Prépare la requête permettant d'énumérer les objets à créer.
        StringBuilder sb = new StringBuilder();
        sb.append("Select cdp_code_postal,com_code_insee ");
        sb.append("from cdp_codes_postaux ");
        sb.append("where \"");
        sb.append(marqueur);
        sb.append("\"=0 OR \"");
        sb.append(marqueur);
        sb.append("\" is null ");
        sb.append("AND dpt_code_departement=?");
        String requeteCherche = sb.toString();
        PreparedStatement psCherche = connectionMaj.prepareStatement(requeteCherche);
        psCherche.setString(1,codeDepartement);
        
        // Prépare la requête permettant de créer les objets.
        sb = new StringBuilder();
        sb.append("INSERT INTO cdp_codes_postaux ");
        sb.append("(dpt_code_departement,cdp_code_postal,com_code_insee,t0,t1) ");
        sb.append("VALUES (?,?,?,?,?)");
        String requete = sb.toString();
        PreparedStatement psInsert = connectionReferentiel.prepareStatement(requete);
        psInsert.setString(1,codeDepartement);
        psInsert.setTimestamp(4,tsdate);
        psInsert.setTimestamp(5,tsinfini);
        
        // Pour chaque objet à créer ,
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES OBJETS");
        p.state[4] = "CHERCHE LES OBJETS";
        ResultSet rsCherche = psCherche.executeQuery();
        
        if (p.stop)
        {
            rsCherche.close();
            psCherche.close();
            psInsert.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            psInsert.setString(2,rsCherche.getString(1));
            psInsert.setString(3,rsCherche.getString(2));
            
            // Exécute,
            try
            {
                psInsert.execute();
            }
            catch(SQLException sqle)
            {
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Problème avec l'objet code postal "+rsCherche.getString(1)+", code insee "+rsCherche.getString(2));
                p.resultat.add("Problème avec l'objet code postal "+rsCherche.getString(1)+", code insee "+rsCherche.getString(2));
                throw(sqle);
            }
            objetscrees++;
            
            if (objetscrees%200==0)
                p.state[6] = Integer.toString(objetscrees);
            
            // Et valide.
            connectionReferentiel.commit();
        }
        
        rsCherche.close();
        psCherche.close();
        psInsert.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CREATION DE CODES POSTAUX "+p.state[3]);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nouveaux objets :"+objetscrees);
        p.resultat.add("CREATION DE CODES POSTAUX "+p.state[3]);
        p.resultat.add("Nouveaux objets :"+objetscrees);
    }
    
    /**
     * Crée les objets disponibles dans la mise à jour qui sont non marqués.<br>
     * N.B.: t0 et t1 doivent être les dernières colonnes dans la table du référentiel. 
     * @param tsdate la date à laquelle est faîte la mise à jour.
     * @param tsinfini une date considérée comme infinie.
     */
    public void creeNouveauxObjets(Processus p,String tableReferentiel,String tableMaj,String marqueur,DescriptionTable dtReferentiel,Connection connectionReferentiel,Connection connectionMaj,Timestamp tsdate,Timestamp tsinfini) throws SQLException
    {
        int objetscrees = 0;
        
        p.state[4] = "PREPARE LES REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Prépare la requête permettant d'énumérer les objets à créer.
        StringBuilder sb = new StringBuilder();
        sb.append("Select ");
        for(int i=0;i<dtReferentiel.getCount();i++)
        {
            Colonne c = dtReferentiel.getColonne(i);
            if (c.getNom().compareTo("t0")!=0 && c.getNom().compareTo("t1")!=0)
            {
                if (i>0)
                    sb.append(",");
                sb.append(c.getNom());
            }
        }
        sb.append(" from \"");
        sb.append(tableMaj);
        sb.append("\" where \"");
        sb.append(marqueur);
        sb.append("\"=0 OR \"");
        sb.append(marqueur);
        sb.append("\" is null");
        String requeteCherche = sb.toString();
        Statement sCherche = connectionMaj.createStatement();

        // Prépare la requête permettant de créer les objets.
        sb = new StringBuilder();
        sb.append("INSERT INTO \"");
        sb.append(tableReferentiel);
        sb.append("\" (");
        for(int i=0;i<dtReferentiel.getCount();i++)
        {
            Colonne c = dtReferentiel.getColonne(i);
            if (i>0)
                sb.append(",");
            sb.append(c.getNom());
        }
        sb.append(") VALUES (");
        for(int i=0;i<dtReferentiel.getCount();i++)
        {
            Colonne c = dtReferentiel.getColonne(i);
            if (i>0)
                sb.append(",");
            sb.append("?");
        }
        sb.append(")");
        String requete = sb.toString();
        PreparedStatement psInsert = connectionReferentiel.prepareStatement(requete);
        
        // Pour chaque objet à créer ,
        p.state[4] = "CHERCHE LES OBJETS";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES OBJETS");
        ResultSet rsChercheAdresse = sCherche.executeQuery(requeteCherche);
        
        if (p.stop)
        {
            rsChercheAdresse.close();
            sCherche.close();
            psInsert.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsChercheAdresse.next())
        {
            // Initialise la requête de création
            for(int i=0;i<dtReferentiel.getCount();i++)
            {
                Colonne c = dtReferentiel.getColonne(i);
                
                // Selon les colonnes, définit l'intervalle de validité
                if (c.getNom().compareTo("t0")==0)
                {
                    psInsert.setTimestamp(i+1,tsdate);
                }
                else if (c.getNom().compareTo("t1")==0)
                {
                    psInsert.setTimestamp(i+1,tsinfini);
                }
                else if (c.estGeometrie()) // la géométrie
                {
                    psInsert.setObject(i+1,rsChercheAdresse.getObject(i+1));
                }
                else
                {
                    // ou la valeur de la propriété.
                    definitValeur(psInsert,rsChercheAdresse,i+1,c.getType());
                }
            }
            // Exécute,
            try
            {
                psInsert.execute();
            }
            catch(SQLException sqle)
            {
                p.resultat.add("Problème avec l'objet "+rsChercheAdresse.getString(1));
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Problème avec l'objet "+rsChercheAdresse.getString(1));
                throw(sqle);
            }
            objetscrees++;
            
            if (objetscrees%200==0)
                p.state[6] = Integer.toString(objetscrees);
            
            // Et valide.
            connectionReferentiel.commit();
        }
        
        rsChercheAdresse.close();
        sCherche.close();
        psInsert.close();
        
        if (p.stop)
        {
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Creation de "+p.state[3]);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nouveaux objets :"+objetscrees);
        p.resultat.add("Creation de "+p.state[3]);
        p.resultat.add("Nouveaux objets :"+objetscrees);
    }
    
    /**
     * Crée les communes disponibles dans la mise à jour qui sont non marquées.<br>
     * La table qui contient les mises à jour est nommée 'communesEtArrondissements'.<br>
     * La table du référentiel mis à jour est nommée 'communesEtArrondissements'.<br>
     * Les communes sont réparties dans ses trois tables dans un souci d'optimisation des
     * requête de validation d'adresse.
     * @param marqueur le nom du marqueur utilisé.
     * @param connectionReferentiel la connection au référentiel à mettre à jour.
     * @param connectionMaj la connection au référentiel qui contient la mise à jour.
     * @param tsdate la date à laquelle est faîte la mise à jour.
     * @param tsinfini une date considérée comme infinie.
     */
    public void creeNouvellesCommunes(Processus p,String marqueur,Connection connectionReferentiel,Connection connectionMaj,Timestamp tsdate,Timestamp tsinfini) throws SQLException
    {
        int objetscrees = 0;
        
        p.state[4] = "PREPARE LES REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Prépare la requête permettant d'énumérer les objets à créer.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT com_code_insee,dpt_code_departement,com_nom,com_nom_desab,com_nom_origine,com_code_insee_commune,geometrie,\"");
        sb.append(marqueur);
        sb.append("\"");
        sb.append("FROM \"com_communes\" WHERE \"");
        sb.append(marqueur);
        sb.append("\">1 OR \""); // les communes modifiées.
        sb.append(marqueur);
        sb.append("\" IS NULL"); // les nouvelles communes.
        String requeteCherche = sb.toString();
        Statement sCherche = connectionMaj.createStatement();
        
        // Prépare la requête permettant de créer les objets dans la table
        // communesEtArrondissements
        sb = new StringBuilder();
        sb.append("INSERT INTO \"com_communes\" ");
        sb.append("(com_code_insee,dpt_code_departement,com_nom,com_nom_desab,com_nom_origine,com_nom_pq,com_code_insee_commune,geometrie,t0,t1) ");
        sb.append("VALUES (?,?,?,?,?,?,?,?,?,?);");
        String requeteInsert = sb.toString();
        PreparedStatement psInsert = connectionReferentiel.prepareStatement(requeteInsert);
        psInsert.setTimestamp(9,tsdate);
        psInsert.setTimestamp(10,tsinfini);
        
        // Pour chaque objet à créer ,
        p.state[4] = "CHERCHE LES COMMUNES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES COMMUNES");
        ResultSet rsCherche = sCherche.executeQuery(requeteCherche);
        
        if (p.stop)
        {
            sCherche.close();
            psInsert.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            // code_insee,code_departement,code_postal,nom,geometrie
            String code_insee = rsCherche.getString(1);
            String code_departement = rsCherche.getString(2);
            String nom = rsCherche.getString(3);
            String nom_desabbrevie = rsCherche.getString(4);
            String nom_origine = rsCherche.getString(5);
            String code_insee_commune = rsCherche.getString(6);
            Object geometrie = rsCherche.getObject(7);
            int valeur_marqueur = rsCherche.getInt(8); // si null retourne 0.
            
            // code_insee,code_departement,nom,t0,t1
            if (valeur_marqueur==0 || (valeur_marqueur == 2))
            {
                psInsert.setString(1,code_insee);
                psInsert.setString(2,code_departement);
                psInsert.setString(3,nom);
                psInsert.setString(4,nom_desabbrevie);
                psInsert.setString(5,nom_origine);
                psInsert.setString(6,Algos.phonex(Algos.normalise_1(nom_origine))); // C'est le phonétique du nom d'origine qui est utilisé.
                psInsert.setString(7,code_insee_commune);
                psInsert.setObject(8,geometrie);
                
                try
                {
                    psInsert.execute();
                }
                catch(SQLException sqle)
                {
                    jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Problème avec la commune "+code_insee);
                    p.resultat.add("Problème avec la commune "+code_insee);
                    throw(sqle);
                }
            }
            
            objetscrees++;
            if (objetscrees%50==0)
                p.state[6] = Integer.toString(objetscrees);
            
            // Et valide.
            connectionReferentiel.commit();
        }
        
        rsCherche.close();
        sCherche.close();
        psInsert.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Creation des Communes");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nouveaux objets :"+objetscrees);
        p.resultat.add("Creation des Communes");
        p.resultat.add("Nouveaux objets :"+objetscrees);
    }

    /**
     * Crée les voies disponibles dans la mise à jour qui sont non marquées.<br>
     * La table qui contient les mises à jour est nommée 'voi_voies_code_departement'.<br>
     * Les tables du référentiel mises à jour sont nommées 'voi_voies_code_departement' et 'voiesambigues'.<br>
     * 
     * @param marqueur le nom du marqueur utilisé.
     * @param code_departement le département où le travail est effectué.
     * @param connectionReferentiel la connection au référentiel à mettre à jour.
     * @param connectionMaj la connection au référentiel qui contient la mise à jour.
     * @param voiesambigues indique si les voies ambigues doivent être mises à jour.
     * @param tsdate la date à laquelle est faîte la mise à jour.
     * @param tsinfini une date considérée comme infinie.
     */
    public void creeNouvellesVoies(Processus p,String marqueur,String code_departement,Connection connectionMaj,Connection connectionReferentiel,boolean voiesambigues,Timestamp tsdate,Timestamp tsinfini) throws SQLException
    {
        int objetscrees = 0,ambiguitees=0;
        
        p.state[4] = "PREPARE LES REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Prépare la requête permettant d'énumérer les objets à créer.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_id,com_code_insee,cdp_code_postal,voi_nom,voi_nom_desab,voi_nom_origine,voi_code_fantoir,");
        sb.append("voi_min_numero,voi_max_numero FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voi_voies_").append(code_departement);
        sb.append(GestionTables.getVoiVoiesTableName(code_departement));
        sb.append("\" WHERE \"");
        sb.append(marqueur);
        sb.append("\"=0 OR \"");
        sb.append(marqueur);
        sb.append("\" IS NULL");
        String requeteCherche = sb.toString();
        Statement sCherche = connectionMaj.createStatement();
        
        // Prépare la requête permettant de créer les objets dans la table
        sb.setLength(0);
        sb.append("INSERT INTO \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voi_voies_").append(code_departement);
        sb.append(GestionTables.getVoiVoiesTableName(code_departement));
        sb.append("\" (voi_id,com_code_insee,cdp_code_postal,voi_nom,voi_nom_desab,voi_nom_origine,voi_code_fantoir,");
        sb.append("voi_min_numero,voi_max_numero,");
        sb.append("voi_type_de_voie,voi_type_de_voie_pq,");
        sb.append("voi_lbl,voi_lbl_pq,voi_lbl_sans_articles,voi_lbl_sans_articles_pq,");
        sb.append("voi_mot_determinant,voi_mot_determinant_pq,t0,t1) ");
        sb.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        PreparedStatement psInsert = connectionReferentiel.prepareStatement(sb.toString());
        psInsert.setTimestamp(18,tsdate);
        psInsert.setTimestamp(19,tsinfini);
        
        PreparedStatement psChercheAmbigue = null;
        PreparedStatement psInsertAmbigue = null;
        if (voiesambigues)
        {
            // Prépare la requête permettant de chercher si la voie ambigue n'est pas déjà référencée.
            sb.setLength(0);
            sb.append("SELECT voa_mot FROM \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sb.append("voa_voies_ambigues_").append(code_departement);
            sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
            sb.append("\" WHERE voa_mot=? AND voa_categorie_ambiguite=? AND voa_lbl_pq=? LIMIT 1");
            psChercheAmbigue=connectionReferentiel.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de créer les voies ambigues.
            sb.setLength(0);
            sb.append("INSERT INTO \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sb.append("voa_voies_ambigues_").append(code_departement);
            sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
            sb.append("\" (voa_mot,voa_categorie_ambiguite,voa_lbl_pq) ");
            sb.append("VALUES (?,?,?)");
            psInsertAmbigue=connectionReferentiel.prepareStatement(sb.toString());
        }
        
        // Prépare la requête permettant de référencer l'id de voie
        sb.setLength(0);
        sb.append("INSERT INTO idv_id_voies (voi_id,dpt_code_departement,idv_code_fantoir) ");
        sb.append("VALUES (?,?,?)");
        PreparedStatement psInsertId = connectionReferentiel.prepareStatement(sb.toString());
        
        // Prépare la requête permettant de vérifier si l'id de voie n'est pas déjà référencé
        sb.setLength(0);
        sb.append("SELECT voi_id FROM idv_id_voies WHERE voi_id=? LIMIT 1");
        PreparedStatement psChercheId = connectionReferentiel.prepareStatement(sb.toString());
        
        // Pour chaque objet à créer,
        p.state[4] = "CHERCHE LES VOIES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES VOIES");
        ResultSet rsCherche = sCherche.executeQuery(requeteCherche);
        
        if (p.stop)
        {
            sCherche.close();
            psChercheAmbigue.close();
            psInsertAmbigue.close();
            psInsert.close();
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        while(!p.stop && rsCherche.next())
        {
            String voi_id = rsCherche.getString(1);
            String code_insee = rsCherche.getString(2);
            String code_postal = rsCherche.getString(3);
            String nom = rsCherche.getString(4);
            String nom_desabbrevie = rsCherche.getString(5);
            String nom_origine = rsCherche.getString(6);
            String code_fantoir = rsCherche.getString(7);
            
            // Découpe la voie désabbreviée.
            RefCle libelle, article;
            RefCle typedevoie = gestionMots.trouveTypeVoie(nom_desabbrevie,(ArrayList<RefNumero>) null);
            if (typedevoie.obtientMot().length()>0)
            {
                article = gestionMots.trouveArticleVoie(nom_desabbrevie,typedevoie);
                libelle = gestionMots.trouveLibelleVoie(nom_desabbrevie,article);
            }
            else
            {
                article = new RefCle("",0);
                libelle = gestionMots.trouveLibelleVoie(nom_desabbrevie,(ArrayList<RefNumero>)null);
            }
            
            //voi_id,code_insee,nom,nom_origine,code_fantoir
            psInsert.setString(1,voi_id);
            psInsert.setString(2,code_insee);
            psInsert.setString(3,code_postal);
            psInsert.setString(4,nom);
            psInsert.setString(5,nom_desabbrevie);
            psInsert.setString(6,nom_origine);
            psInsert.setString(7,code_fantoir);
            
            //min_numero, max_numero,
            psInsert.setInt(8,rsCherche.getInt(8));
            psInsert.setInt(9,rsCherche.getInt(9));
            
            //typedevoie,typedevoie_phonetique,libelle,libelle_phonetique,
            psInsert.setString(10,typedevoie.obtientMot());
            psInsert.setString(11,Algos.phonex(typedevoie.obtientMot()));
            psInsert.setString(12,libelle.obtientMot());
            psInsert.setString(13,Algos.phonex(libelle.obtientMot()));
            
            //libellesansarticle,libellesansarticle_phonetique,motdeterminant,motdeterminant_phonetique
            String libellesansarticle = Algos.sansarticles(libelle.obtientMot());
            psInsert.setString(14,libellesansarticle);
            psInsert.setString(15,Algos.phonex(libellesansarticle));
            
            String motdeterminant = Algos.derniermot(libellesansarticle);
            psInsert.setString(16,motdeterminant);
            psInsert.setString(17,Algos.phonex(motdeterminant));
            
            // Exécute,
            try
            {
                psInsert.execute();
            }
            catch(SQLException sqle)
            {
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Pb pour la voie "+voi_id+" "+code_insee);
                p.resultat.add("Pb pour la voie "+voi_id+" "+code_insee);
                throw(sqle);
            }
            objetscrees++;
            if (objetscrees%200==0)
                p.state[6] = Integer.toString(objetscrees);
            
            // Insère l'id si nécessaire
            psChercheId.setString(1,voi_id);
            ResultSet rsChercheId = psChercheId.executeQuery();
            if (!rsChercheId.next())
            {
                psInsertId.setString(1,voi_id);
                psInsertId.setString(2,code_departement);
                psInsertId.setString(3,code_fantoir);
                psInsertId.execute();
            }
            rsChercheId.close();
            
            // Cherche les ambiguités si nécessaire
            if (voiesambigues)
            {
                ResultatAmbiguite ra=gestionMots.chercheSiVoieAmbigue(nom_desabbrevie);

                String libelle_phonetique=null;
                boolean psChercheAmbigueInitialise=false, psInsertAmbigueInitialise=false;

                for(int i=0; i<ra.obtientQuantite(); i++)
                {
                    if (libelle_phonetique==null)
                    {
                        libelle_phonetique=Algos.phonex(libelle.obtientMot());
                    }
                    String nomi=ra.obtientMot(i).obtientNom();
                    String categoriei=ra.obtientCategorieAmbiguite(i).toString();

                    // Cherche si l'ambiguité n'a pas déjà été répertoriée.
                    if (!psChercheAmbigueInitialise)
                    {
                        psChercheAmbigue.setString(3,libelle_phonetique);
                        psChercheAmbigueInitialise=true;
                    }
                    psChercheAmbigue.setString(1,nomi);
                    psChercheAmbigue.setString(2,categoriei);
                    ResultSet rsChercheAmbigue=psChercheAmbigue.executeQuery();
                    if (!rsChercheAmbigue.next())
                    {
                        ambiguitees++;
                        // Répertorie l'ambiguité
                        if (!psInsertAmbigueInitialise)
                        {
                            psInsertAmbigue.setString(3,libelle_phonetique);
                            psInsertAmbigueInitialise=true;
                        }
                        psInsertAmbigue.setString(1,nomi);
                        psInsertAmbigue.setString(2,categoriei);
                        psInsertAmbigue.execute();
                    }
                    rsChercheAmbigue.close();
                }
            }
            
            // Et valide.
            connectionReferentiel.commit();
        }
        
        rsCherche.close();
        sCherche.close();
        if (voiesambigues)
        {
            psChercheAmbigue.close();
            psInsertAmbigue.close();
        }
        psInsert.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Creation des Voies");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Creation des Voies");
        if (voiesambigues)
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nouvelles ambiguitées :"+ambiguitees);
        p.resultat.add("Creation des Voies");
        p.resultat.add("Nouvelles voies :"+objetscrees);
        if (voiesambigues)
            p.resultat.add("Nouvelles ambiguitées :"+ambiguitees);
    }
    
   /**
     * Essaye d'ajouter une voie à la table voie à partir d'un troncon.<br>
     * La table des identifiants de voies est alors complétée.<br>
     * Génère les identifiant de voie et code fantoir si nécessaire.<br>
     * @param params l'ensemble des paramètres nécessaires.
     * @param maj indique s'il s'agit d'une table de mise à jour ou de référentiel (les tables sont différentes).
     * @return true si une requête update ou insert a été générée.
     */
    private boolean essayeDAjouterVoie(AjoutVoieParams params,boolean maj) throws SQLException, GestionReferentielException
    {
        String suffixe = params.getSuffixe();
        ResultSet rsTroncons = params.getRsTroncons();
        
        String id_troncon = rsTroncons.getString("tro_id");
        
        if (id_troncon==null || id_troncon.length()==0)
        {
            throw(new GestionReferentielException("Un troncon n'a pas d'identifiant.",GestionReferentielException.PASIDTRONCON,10));
        }
        
        String codeInsee = rsTroncons.getString("com_code_insee_"+suffixe);
        String codePostal = rsTroncons.getString("cdp_code_postal_"+suffixe);
        String nomVoie = rsTroncons.getString("tro_nom_"+suffixe);
        String nomVoieOrigine = rsTroncons.getString("tro_nom_origine_"+suffixe);
        String codeFantoir = rsTroncons.getString("tro_code_fantoir_"+suffixe);
        String nomVoieDesabbrevie = rsTroncons.getString("tro_nom_desab_"+suffixe);
        
        int numero_debut = rsTroncons.getInt("tro_numero_debut_"+suffixe);
        int numero_fin = rsTroncons.getInt("tro_numero_fin_"+suffixe);
        
        if (nomVoie != null && nomVoie.trim().length()>0 && codeInsee != null && codeInsee.trim().length()>0)
        {
            String voi_id = null;
            
            voi_id = rsTroncons.getString("voi_id_"+suffixe);
            
            // Cherche les infos sur la voie et vérifie si elle existe déjà.
            PreparedStatement psUnique=params.getPsUnique();
            
            psUnique.setString(1,codeInsee);
            psUnique.setString(2,nomVoie);
            psUnique.setString(3,codePostal);
            
            ResultSet rsUnique=psUnique.executeQuery();
            
            if (!rsUnique.next()) // Si la voie n'a jamais été crée
            {
                // Si la voie n'est pas présente,
                    PreparedStatement psInsertId=params.getPsInsertId();

                    // Si aucun identifiant n'a été affecté à cette voie,crée un identifiant
                    if (voi_id==null||voi_id.length()==0)
                    {
                        // Cherche un identifiant libre,
                        voi_id=GestionIdentifiants.generateId(params.getPsUniqueId());

                        // Si aucun code fantoir n'a été affecté, crée un code fantoir
                        if (codeFantoir==null||codeFantoir.length()==0)
                        {
                            // Cherche un code fantoir inutilisé.
                            codeFantoir=GestionIdentifiants.generateFantoir(params.getPsChercheFantoire());
                        }

                        // Et l'ajoute avec les autres.
                        psInsertId.setString(1,voi_id);
                        psInsertId.setString(2,params.getCodeDepartement());
                        psInsertId.setString(3,codeFantoir);
                        psInsertId.setString(4,codeFantoir.substring(2));
                        psInsertId.execute();
                    }
                    else
                    {
                        // Si l'identifiant n'est pas encore présent dans idvoies, l'ajoute à idvoies
                        params.getPsUniqueId().setString(1,voi_id);
                        ResultSet rsPsUniqueId=params.getPsUniqueId().executeQuery();
                        if (!rsPsUniqueId.next())
                        {
                            // Si aucun code fantoir n'a été affecté, crée un code fantoir
                            if (codeFantoir==null||codeFantoir.length()==0)
                            {
                                // Cherche un code fantoir inutilisé.
                                codeFantoir=GestionIdentifiants.generateFantoir(params.getPsChercheFantoire());
                            }

                            // et l'ajoute avec les autres.
                            psInsertId.setString(1,voi_id);
                            psInsertId.setString(2,params.getCodeDepartement());
                            psInsertId.setString(3,codeFantoir);
                            psInsertId.setString(4,codeFantoir.substring(2));
                            psInsertId.execute();
                        }
                        rsPsUniqueId.close();
                    }

                    PreparedStatement psInsert=params.getPsInsert();

                    // Ajoute la nouvelle voie.
                    // voi_id,code_insee,nom,typedevoie,article,libelle,typedevoiesansarticle.
                    psInsert.setString(1,voi_id);
                    psInsert.setString(2,codeInsee);
                    psInsert.setString(3,codePostal);
                    psInsert.setString(4,nomVoie);
                    psInsert.setString(5,nomVoieDesabbrevie);
                    psInsert.setString(6,nomVoieOrigine);
                    psInsert.setString(7,codeFantoir);

                    if (numero_debut<numero_fin)
                    {
                        psInsert.setInt(8,numero_debut);
                        psInsert.setInt(9,numero_fin);
                    }
                    else
                    {
                        psInsert.setInt(8,numero_fin);
                        psInsert.setInt(9,numero_debut);
                    }

                    if (maj)
                    {
                        RefCle typedevoie= gestionMots.trouveTypeVoie(nomVoieDesabbrevie,null);
                        RefCle article,libelle;
                        if (typedevoie.obtientMot().length()!=0)
                        {
                            article=gestionMots.trouveArticleVoie(nomVoieDesabbrevie,
                                typedevoie);
                            libelle=gestionMots.trouveLibelleVoie(nomVoieDesabbrevie,
                                article);
                        }
                        else
                        {
                            article = new RefCle("",0);
                            libelle = gestionMots.trouveLibelleVoie(nomVoieDesabbrevie,(ArrayList<RefNumero>)null);
                        }

                        // Ajoute la nouvelle voie.
                        psInsert.setString(10,typedevoie.obtientMot());
                        psInsert.setString(11,Algos.phonex(typedevoie.obtientMot()));
                        psInsert.setString(12,libelle.obtientMot());
                        psInsert.setString(13,Algos.phonex(libelle.obtientMot()));

                        String libellesansarticles=Algos.sansarticles(libelle.obtientMot());
                        psInsert.setString(14,libellesansarticles);
                        psInsert.setString(15,Algos.phonex(libellesansarticles));

                        String motdeterminant=Algos.derniermot(libellesansarticles); // optimisation : utilisation de libellesansarticles

                        psInsert.setString(16,motdeterminant);
                        psInsert.setString(17,Algos.phonex(motdeterminant));
                    }
                    psInsert.execute();
            }
            else
            {
                // Obtient l'identifiant de cette voie
                voi_id = rsUnique.getString(1);
                
                // Si la voie est déjà présente, il faut peut être mettre à jour les numéros d'adresses.
                int min_numero = rsUnique.getInt(2);
                int max_numero = rsUnique.getInt(3);
                int new_min_numero=min_numero, new_max_numero=max_numero;

                if (numero_debut<numero_fin)
                {
                    if (numero_debut!=0 && numero_debut<min_numero)
                    {
                        new_min_numero=numero_debut;
                    }
                    if (numero_fin>max_numero)
                    {
                        new_max_numero=numero_fin;
                    }
                }
                else
                {
                    if (numero_fin!=0 && numero_fin<min_numero)
                    {
                        new_min_numero=numero_fin;
                    }
                    if (numero_debut>max_numero)
                    {
                        new_max_numero=numero_debut;
                    }
                }

                if (new_min_numero!=min_numero||new_max_numero!=max_numero)
                {
                    PreparedStatement psUpdateAdresse=params.getPsUpdateAdresse();
                    psUpdateAdresse.setInt(1,new_min_numero);
                    psUpdateAdresse.setInt(2,new_max_numero);
                    psUpdateAdresse.setString(3,voi_id);
                    psUpdateAdresse.execute();
                }
            }

            rsUnique.close();
            
            PreparedStatement psMaj = params.getPsMaj();
            
            // Puis met à jour la table des troncons avec ce nouvel identifiant.
            psMaj.setString(1, voi_id);
            psMaj.setString(2, id_troncon);
            
            psMaj.execute();
            
            return true;
        }
        return false;
    }
    
    /**
     *  Crée une table voie pour le référentiel à partir d'une table de tronçons. 
     *  Toutefois, le temps n'est pas pris en compte par cette méthode.<br>
     *  Si la table voie n'existe pas, elle est crée (sans être historisable).<br>
     *  Sinon, cette table est supprimée au préalable.<br>
     *  Sinon, cette table est vidée au préalable.<br>
     *  Attention : Les id de voie sont ajoutés s'il ne sont pas attribués.
     *  Pour cela, la table nomTableVoies est utilisée pour savoir si les identifiants sont déjà attribués.
     *  Il faut ainsi qu'elle soit déjà initialisée avec tous les id connus (y compris des autres
     *  départements).
     *  Attention : faille de sécurité (injection sql à partir de nomTableVoie) pour la requête de recherche d'unicité de voies.<br>
     *  L'état de la structure processus doit être un tableau d'au moins 6 chaines dont les chaines 4 et 6 seront modifiées
     *  par cette méthode:
     *  <ul><li>Le champ 4 contient l'opération en cour</li>
     *      <li>Le champ 6 contient le compte du nombre de troncon en cour d'analyse</li>
     *      <li>Le champ 7 contient le compte du nombre de troncon au total</li>
     * </ul>
     *  @param nomTableTroncon est le nom de la table de troncons. Tous les troncons doivent appartenir à un même département.
     *  @param nomTableVoie est le nom de la table de voies qui contiendra les voies de la table troncon.
     *  @throws GestionReferentielException Si la table de voie existait déjà avec des types de colonnes incompatibles.
     *  @throws GestionReferentielException Si la table de tronçons n'existe pas.
     *  @throws SQLException une erreur est survenue lors de l'ajout d'une colonne, de l'obtention de la description, la table est crée, la table est vidée.
     */
    public void creeTableVoieReferentiel(Processus p,String codeDepartement,String nomTableTroncon,String nomTableVoies,String nomTableIdVoies,Connection connection) throws GestionReferentielException, SQLException
    {
        if (GestionTables.tableExiste(nomTableTroncon,connection))
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES TABLES");
            p.state[3] = "PREPARE LES TABLES";
            
            // Vérifie la structure de la table de troncon.
            DescriptionTable dttroncon = GestionTables.obtientDescription(nomTableTroncon,connection);
            GestionStructure.verifieStructure(nomTableTroncon,dttroncon,GestionDescriptionTables.creeDescriptionTableTronconMaj());
            
            // Teste l'existence de la table de voies.
            if (GestionTables.tableExiste(nomTableVoies,connection))
            {
                // Dans ce cas, la supprime.
                GestionTables.supprimeTable(nomTableVoies, connection);
            }
            
            // Crée la table des voies.
            DescriptionTable dtVoies = GestionDescriptionTables.creeDescriptionTableVoiesReferentiel();
            GestionTables.creeTable(nomTableVoies, dtVoies, connection);
            
            // Crée la table idvoie si nécessaire
            GestionTables.creeTable(nomTableIdVoies, GestionDescriptionTables.creeDescriptionTableIdVoies(),connection);
            
            // Ajoute une colonne code_fantoire_bis
            DescriptionTable dt=GestionTables.obtientDescription(nomTableIdVoies,connection);
            Colonne code_fantoire_bis=null;
            try
            {
                code_fantoire_bis=new Colonne("code_fantoir_bis","CHARACTER VARYING",10);
                int contient=dt.contient(code_fantoire_bis);
                if (contient==Colonne.DIFFERENT)
                {
                    GestionTables.ajouteColonne("idv_id_voies",code_fantoire_bis,connection);
                }
                else if (contient==Colonne.TYPEDIFFERENT)
                {
                    throw (new GestionReferentielException(
                            "Une colonne code_fantoir_bis existe déjà avec le mauvais type. Type attendu : character varying 10",
                            GestionReferentielException.COLONNEERRONEE,10));
                }
            }
            catch(ColonneException ex)
            {
                // Ne devrait pas arriver.
            }
            
            // Crée les index
            Index idx_code_fantoir_bis = new Index();
            idx_code_fantoir_bis.setNom("idv_code_fantoir_bis");
            idx_code_fantoir_bis.ajouteColonne("code_fantoir_bis");
            boolean idx_code_fantoir_bis_cree = GestionTables.ajouteIndex(nomTableIdVoies,idx_code_fantoir_bis,connection);
            
            Index idx_id_troncon = new Index();
            idx_id_troncon.setNom(nomTableTroncon+"_id_troncon");
            idx_id_troncon.ajouteColonne("tro_id");
            boolean idx_id_troncon_ajoute = GestionTables.ajouteIndex(nomTableTroncon,idx_id_troncon,connection);
            
            Index idx_nom_insee = new Index();
            idx_nom_insee.setNom(nomTableVoies+"_nom_insee");
            idx_nom_insee.ajouteColonne("voi_nom");
            idx_nom_insee.ajouteColonne("com_code_insee");
            boolean idx_nom_insee_ajoute = GestionTables.ajouteIndex(nomTableVoies,idx_nom_insee,connection);
            
            Index idx_voi_id = new Index();
            idx_voi_id.setNom(nomTableIdVoies+"_voi_id");
            idx_voi_id.ajouteColonne("voi_id");
            boolean idx_voi_id_ajoute = GestionTables.ajouteIndex(nomTableIdVoies,idx_voi_id,connection);
            
            // Nettoye les tables
            nomTableTroncon = GestionTables.formateNom(nomTableTroncon);
            nomTableVoies = GestionTables.formateNom(nomTableVoies);
            nomTableIdVoies = GestionTables.formateNom(nomTableIdVoies);
            
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"NETTOYE "+nomTableVoies);
            p.state[3] = "NETTOYE "+nomTableVoies;
            GestionTables.nettoye(nomTableVoies,connection);
            
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"NETTOYE "+nomTableTroncon);
            p.state[3] = "NETTOYE "+nomTableTroncon;
            GestionTables.nettoye(nomTableTroncon,connection);
            
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"NETTOYE "+nomTableIdVoies);
            p.state[3] = "NETTOYE "+nomTableIdVoies;
            GestionTables.nettoye(nomTableIdVoies,connection);
            
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
            p.state[3] = "PREPARE LES REQUETES";
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT count(*) FROM ");
            sb.append(nomTableTroncon);
            Statement stCompte = connection.createStatement();
            ResultSet rsCompte = stCompte.executeQuery(sb.toString());
            
            int compte = 0;
            if (rsCompte.next())
            {
                compte = rsCompte.getInt(1);
            }
            p.state[6] = "SUR "+compte;
            
            // Prépare la requête permettant de mettre à jour l'identifiant de voie droite du tronçon.
            sb.setLength(0);
            sb.append("UPDATE ");
            sb.append(nomTableTroncon);
            sb.append(" set voi_id_droit=? WHERE tro_id=?");
            PreparedStatement psMajDroite = connection.prepareStatement(
                    sb.toString());
            
            sb.setLength(0);
            sb.append("UPDATE ");
            sb.append(nomTableTroncon);
            sb.append(" set voi_id_gauche=? WHERE tro_id=?");
            PreparedStatement psMajGauche = connection.prepareStatement(
                    sb.toString());
            
            // Prépare la requête d'ajout d'une nouvelle voie (tient compte des colonnes supplémentaires).
            sb.setLength(0);
            sb.append("INSERT INTO ");
            sb.append(nomTableVoies);
            sb.append(" (voi_id,com_code_insee,cdp_code_postal,");
            sb.append("voi_nom,voi_nom_desab,voi_nom_origine,voi_code_fantoir,voi_min_numero,voi_max_numero,");
            sb.append("voi_type_de_voie,voi_type_de_voie_pq,");
            sb.append("voi_lbl,voi_lbl_pq,voi_lbl_sans_articles,voi_lbl_sans_articles_pq,");
            sb.append("voi_mot_determinant,voi_mot_determinant_pq) ");
            sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            PreparedStatement psInsert = connection.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de savoir si la voie a déjà été crée.
            // on en profite aussi pour obtenir ses numéros d'adresses
            sb.setLength(0);
            sb.append("SELECT voi_id,voi_min_numero,voi_max_numero FROM ");
            sb.append(nomTableVoies);
            sb.append(" WHERE com_code_insee=? AND voi_nom=? and cdp_code_postal=? LIMIT 1;");
            PreparedStatement psUnique = connection.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de savoir si l'identifiant a déjà été attribué.
            sb.setLength(0);
            sb.append("SELECT voi_id FROM ");
            sb.append(nomTableIdVoies);
            sb.append(" WHERE voi_id=? LIMIT 1");
            PreparedStatement psUniqueId = connection.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de référencer un nouvel identifiant.
            sb.setLength(0);
            sb.append("INSERT INTO ");
            sb.append(nomTableIdVoies);
            sb.append(" (voi_id,dpt_code_departement,idv_code_fantoir,code_fantoir_bis) VALUES (?,?,?,?)");
            PreparedStatement psInsertId = connection.prepareStatement(sb.toString());
            
            // Prépare la requête pour vérifier qu'un fantoire n'est pas déjà attribué
            sb.setLength(0);
            sb.append("SELECT idv_code_fantoir ");
            sb.append("FROM ");
            sb.append(nomTableIdVoies);
            sb.append(" WHERE code_fantoir_bis=? LIMIT 1");
            PreparedStatement psChercheFantoir = connection.prepareStatement(sb.toString());

            // Prépare la requête pour mettre à jour les informations de numéros d'adresse
            sb.setLength(0);
            sb.append("UPDATE ");
            sb.append(nomTableVoies);
            sb.append(" SET voi_min_numero=?,voi_max_numero=? ");
            sb.append(" WHERE voi_id=?");
            PreparedStatement psUpdateAdresse = connection.prepareStatement(sb.toString());
            
            // L'étude de chaque troncon consiste en une transaction.
            boolean autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            
            // Pour chaque troncon,
            Statement stTroncons = connection.createStatement();           
            int index = 0;
            
            sb.setLength(0);
            sb.append("SELECT tro_id,");
            sb.append("voi_id_gauche,tro_nom_gauche,tro_nom_desab_gauche,tro_nom_origine_gauche,com_code_insee_gauche,cdp_code_postal_gauche,");
            sb.append("voi_id_droit,tro_nom_droit,tro_nom_desab_droit,tro_nom_origine_droit,com_code_insee_droit,cdp_code_postal_droit,");
            sb.append("tro_code_fantoir_droit,tro_code_fantoir_gauche,");
            sb.append("tro_numero_debut_droit,tro_numero_debut_gauche,tro_numero_fin_droit,tro_numero_fin_gauche");
            sb.append(" FROM ");
            sb.append(nomTableTroncon);
            sb.append("");
            p.state[3] = "CHERCHE LES TRONCONS";
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES TRONCONS");
            ResultSet rsTroncons=stTroncons.executeQuery(sb.toString());

            if (p.stop)
            {
                stTroncons.close();
                
                psUnique.close();
                psInsert.close();
                psMajDroite.close();
                psMajGauche.close();
                p.state=new String[]{"TERMINE"};
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                return;
            }
            
            // Prépare les paramètres des méthodes d'ajout des voies
            AjoutVoieParams paramDroite = new AjoutVoieParams();
            paramDroite.setConnection(connection);
            paramDroite.setPsInsert(psInsert);
            paramDroite.setPsMaj(psMajDroite);
            paramDroite.setPsUnique(psUnique);
            paramDroite.setRsTroncons(rsTroncons);
            paramDroite.setPsUniqueId(psUniqueId);
            paramDroite.setPsInsertId(psInsertId);
            paramDroite.setSuffixe("droit");
            paramDroite.setCodeDepartement(codeDepartement);
            paramDroite.setPsChercheFantoir(psChercheFantoir);
            paramDroite.setPsUpdateAdresse(psUpdateAdresse);
            
            AjoutVoieParams paramGauche = new AjoutVoieParams();
            paramGauche.setConnection(connection);
            paramGauche.setPsInsert(psInsert);
            paramGauche.setPsMaj(psMajGauche);
            paramGauche.setPsUnique(psUnique);
            paramGauche.setRsTroncons(rsTroncons);
            paramGauche.setPsUniqueId(psUniqueId);
            paramGauche.setPsInsertId(psInsertId);
            paramGauche.setSuffixe("gauche");
            paramGauche.setCodeDepartement(codeDepartement);
            paramGauche.setPsChercheFantoir(psChercheFantoir);
            paramGauche.setPsUpdateAdresse(psUpdateAdresse);
            
            try
            {
                p.state[3] = "TRAITEMENT";
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT de "+compte+" troncons");
                while(!p.stop && rsTroncons.next())
                {
                    boolean misajour = false;
                    
                    misajour |= essayeDAjouterVoie(paramDroite,true);
                    
                    misajour |= essayeDAjouterVoie(paramGauche,true);
                    
                    if (misajour)
                    {
                        connection.commit(); // La transaction est commitée si nécessaire avant de passer au tronçon suivant.
                    }
                    index++;
                    if (index % 100 == 0)
                    {
                        p.state[5] = Integer.toString(index);
                    }
                }
                
                rsTroncons.close();
                stTroncons.close();
                
                psUnique.close();
                psInsert.close();
                psMajDroite.close();
                psMajGauche.close();
          
                if (p.stop)
                {
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                    jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                }
                
                p.state[3] = "SUPPRIME LES INDEX";
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"SUPPRIME LES INDEX");
                if (idx_id_troncon_ajoute)
                    GestionTables.supprimeIndex(idx_id_troncon,connection);
                if (idx_voi_id_ajoute)
                    GestionTables.supprimeIndex(idx_voi_id,connection);
                if (idx_nom_insee_ajoute)
                    GestionTables.supprimeIndex(idx_nom_insee,connection);
                if (idx_code_fantoir_bis_cree)
                    GestionTables.supprimeIndex(idx_code_fantoir_bis,connection);
                
                p.state[3] = "SUPPRIME LA COLONNE";
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"SUPPRIME LA COLONNE");
                GestionTables.supprimeColonne(nomTableIdVoies,code_fantoire_bis,connection);                
            }
            finally
            {
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CREATION DE LA TABLE VOIE TERMINE");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRONCONS PARCOURUS :"+index);
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"SUR "+compte);
                p.state[3] = "TERMINE";
                p.resultat.add("CREATION DE LA TABLE VOIE");
                p.resultat.add("TRONCONS PARCOURUS :"+index);
                p.resultat.add("SUR "+compte);
                connection.setAutoCommit(autocommit);
            }
        }
        else
            throw(new GestionReferentielException("La table "+nomTableTroncon+" n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
    }

    /**
     *  Crée une table voie pour la mise à jour à partir d'une table de tronçons.<br>
     *  Si la table voie n'existe pas, elle est crée (sans être historisable).<br>
     *  Sinon, cette table est supprimée au préalable.<br>
     *  Sinon, cette table est vidée au préalable.<br>
     *  Attention : Les id de voie sont ajoutés s'il ne sont pas attribués.<br>
     *  Attention : faille de sécurité (injection sql à partir de nomTableVoie) pour la requête de recherche d'unicité de voies.<br>
     *  INDEX A CREER pour optimiser la méthode:
     *  <ul><li>Sur la table voie : code_insee et nom</li>
     *      <li>Sur la table troncon : id_troncon</li></ul>
     *  L'état de la structure processus doit être un tableau d'au moins 7 chaines dont les chaines 4 et 6 seront modifiées
     *  par cette méthode:
     *  <ul><li>Le champ 4 contient l'opération en cour</li>
     *      <li>Le champ 6 contient le compte du nombre de troncon en cour d'analyse</li>
     *      <li>Le champ 7 contient le total du nombre de troncon à analyser.</li>
     * </ul>
     *  @param nomTableTroncon est le nom de la table de troncons. Tous les troncons doivent appartenir à un même département.
     *  @param nomTableVoie est le nom de la table de voies qui contiendra les voies de la table troncon.
     *  @throws GestionReferentielException Si la table de voie existait déjà avec des types de colonnes incompatibles.
     *  @throws GestionReferentielException Si la table de tronçons n'existe pas.
     *  @throws SQLException une erreur est survenue lors de l'ajout d'une colonne, de l'obtention de la description, la table est crée, la table est vidée.
     */
    public void creeTableVoieMaj(Processus p,String codeDepartement,String nomTableTroncon,String nomTableVoies,String nomTableIdVoies,Connection connection) throws GestionReferentielException, SQLException
    {
        if (GestionTables.tableExiste(nomTableTroncon,connection))
        {
            p.state[3] = "PREPARE LES TABLES";
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES TABLES");
            
            // Vérifie la structure de la table de troncon.
            DescriptionTable dttroncon = GestionTables.obtientDescription(nomTableTroncon,connection);
            GestionStructure.verifieStructure(nomTableTroncon,dttroncon,GestionDescriptionTables.creeDescriptionTableTronconMaj());

            // Teste l'existence de la table de voies et la supprime si nécessaire.
            try
            {
                GestionTables.supprimeTable(nomTableVoies, connection);
            }
            catch(GestionReferentielException gre)
            {
                if (gre.obtientType()!=GestionReferentielException.TABLENEXISTEPAS)
                    throw(gre);
            }
            
            // Crée la table des voies.
            DescriptionTable dtVoies = GestionDescriptionTables.creeDescriptionTableVoiesMaj();
            GestionTables.creeTable(nomTableVoies, dtVoies, connection);
            
            // Crée la table idvoies si nécessaire.
            GestionTables.creeTable(nomTableIdVoies,GestionDescriptionTables.creeDescriptionTableIdVoies(),connection);
            
            // Ajoute une colonne code_fantoire_bis
            DescriptionTable dt=GestionTables.obtientDescription(nomTableIdVoies,connection);
            Colonne code_fantoire_bis=null;
            try
            {
                code_fantoire_bis=new Colonne("code_fantoir_bis","CHARACTER VARYING",10);
                int contient=dt.contient(code_fantoire_bis);
                if (contient==Colonne.DIFFERENT)
                {
                    GestionTables.ajouteColonne("idv_id_voies",code_fantoire_bis,connection);
                }
                else if (contient==Colonne.TYPEDIFFERENT)
                {
                    jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Une colonne code_fantoir_bis existe déjà avec le mauvais type. Type attendu : character varying 10");
                    throw (new GestionReferentielException(
                            "Une colonne code_fantoir_bis existe déjà avec le mauvais type. Type attendu : character varying 10",
                            GestionReferentielException.COLONNEERRONEE,10));
                }
            }
            catch(ColonneException ex)
            {
                // Ne devrait pas arriver.
            }
            
            // Crée les index
            Index idx_code_fantoir_bis = new Index();
            idx_code_fantoir_bis.setNom("idv_code_fantoir_bis");
            idx_code_fantoir_bis.ajouteColonne("code_fantoir_bis");
            boolean idx_code_fantoire_bis_cree = GestionTables.ajouteIndex(nomTableIdVoies,idx_code_fantoir_bis,connection);
            
            Index idx_voi_id_droit = new Index();
            Index idx_voi_id_gauche = new Index();
            idx_voi_id_droit.setNom(nomTableTroncon+"_voi_id_droit");
            idx_voi_id_droit.ajouteColonne("voi_id_droit");
            idx_voi_id_gauche.setNom(nomTableTroncon+"_voi_id_gauche");
            idx_voi_id_gauche.ajouteColonne("voi_id_gauche");
            boolean idx_voi_id_droit_ajoute = GestionTables.ajouteIndex(nomTableTroncon,idx_voi_id_droit,connection);
            boolean idx_voi_id_gauche_ajoute = GestionTables.ajouteIndex(nomTableTroncon,idx_voi_id_gauche,connection);
            
            Index idx_insee_nom_origine = new Index();
            idx_insee_nom_origine.setNom(nomTableVoies+"_insee_nom_origine");
            idx_insee_nom_origine.ajouteColonne("com_code_insee");
            idx_insee_nom_origine.ajouteColonne("voi_nom");
            idx_insee_nom_origine.ajouteColonne("voi_nom_origine");
            boolean idx_insee_nom_origine_ajoute = GestionTables.ajouteIndex(nomTableVoies,idx_insee_nom_origine,connection);
            
            Index idx_voi_id = new Index();
            idx_voi_id.setNom(nomTableIdVoies+"_voi_id");
            idx_voi_id.ajouteColonne("voi_id");
            boolean idx_voi_id_ajoute = GestionTables.ajouteIndex(nomTableIdVoies,idx_voi_id,connection);
            
            nomTableTroncon = GestionTables.formateNom(nomTableTroncon);
            nomTableVoies = GestionTables.formateNom(nomTableVoies);
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT count(*) FROM ");
            sb.append(nomTableTroncon);
            Statement stCompte = connection.createStatement();
            ResultSet rsCompte = stCompte.executeQuery(sb.toString());
            
            int compte = 0;
            if (rsCompte.next())
            {
                compte = rsCompte.getInt(1);
            }
            p.state[6] = "SUR "+compte;
            
            // Prépare la requête permettant de mettre à jour l'identifiant de voie droite du tronçon.
            sb.setLength(0);
            sb.append("UPDATE ");
            sb.append(nomTableTroncon);
            sb.append(" set voi_id_droit=? WHERE tro_id=?");
            PreparedStatement psMajDroite = connection.prepareStatement(
                    sb.toString());
            
            sb.setLength(0);
            sb.append("UPDATE ");
            sb.append(nomTableTroncon);
            sb.append(" set voi_id_gauche=? WHERE tro_id=?");
            PreparedStatement psMajGauche = connection.prepareStatement(
                    sb.toString());
            
            // Prépare la requête d'ajout d'une nouvelle voie.
            sb.setLength(0);
            sb.append("INSERT INTO ");
            sb.append(nomTableVoies);
            sb.append(" (voi_id,com_code_insee,cdp_code_postal,voi_nom,voi_nom_desab,voi_nom_origine,voi_code_fantoir,");
            sb.append("voi_min_numero,voi_max_numero)");
            sb.append(" VALUES (?,?,?,?,?,?,?,?,?);");
            PreparedStatement psInsert = connection.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de savoir si la voie a déjà été crée.
            sb.setLength(0);
            sb.append("SELECT voi_id,voi_min_numero,voi_max_numero FROM ");
            sb.append(nomTableVoies);
            sb.append(" WHERE com_code_insee=? AND voi_nom=? and cdp_code_postal=? LIMIT 1;");
            PreparedStatement psUnique = connection.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de savoir si l'identifiant a déjà été attribué.
            sb.setLength(0);
            sb.append("SELECT voi_id FROM ");
            sb.append(nomTableIdVoies);
            sb.append(" WHERE voi_id=? LIMIT 1");
            PreparedStatement psUniqueId = connection.prepareStatement(sb.toString());
            
            // Prépare la requête permettant de référencer un nouvel identifiant.
            sb.setLength(0);
            sb.append("INSERT INTO ");
            sb.append(nomTableIdVoies);
            sb.append(" (voi_id,dpt_code_departement,idv_code_fantoir,code_fantoir_bis) VALUES (?,?,?,?)");
            PreparedStatement psInsertId = connection.prepareStatement(sb.toString());
            
            // Prépare la requête pour vérifier qu'un fantoire n'est pas déjà attribué
            sb.setLength(0);
            sb.append("SELECT idv_code_fantoir");
            sb.append(" FROM ");
            sb.append(nomTableIdVoies);
            sb.append(" WHERE code_fantoir_bis=? ");
            sb.append("LIMIT 1");
            PreparedStatement psChercheFantoire = connection.prepareStatement(sb.toString());
            
            // Prépare la requête pour mettre à jour les informations de numéros d'adresse
            sb.setLength(0);
            sb.append("UPDATE ");
            sb.append(nomTableVoies);
            sb.append(" SET voi_min_numero=?,voi_max_numero=? ");
            sb.append(" WHERE voi_id=?");
            PreparedStatement psUpdateAdresse = connection.prepareStatement(sb.toString());
            
            // L'étude de chaque troncon consiste en une transaction.
            boolean autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            
            // Pour chaque troncon,
            Statement stTroncons = connection.createStatement();           
            int index = 0;
            
            sb.setLength(0);
            sb.append("SELECT tro_id,");
            sb.append("voi_id_gauche,tro_nom_gauche,tro_nom_desab_gauche,tro_nom_origine_gauche,com_code_insee_gauche,cdp_code_postal_gauche,");
            sb.append("voi_id_droit,tro_nom_droit,tro_nom_desab_droit,tro_nom_origine_droit,com_code_insee_droit,cdp_code_postal_droit,");
            sb.append("tro_code_fantoir_droit,tro_code_fantoir_gauche,");
            sb.append("tro_numero_debut_droit,tro_numero_debut_gauche,tro_numero_fin_droit,tro_numero_fin_gauche");
            sb.append(" FROM ");
            sb.append(nomTableTroncon);
            p.state[3] = "CHERCHE LES TRONCONS";
            ResultSet rsTroncons = stTroncons.executeQuery(sb.toString());
            
            if (p.stop)
            {
                stTroncons.close();

                psUnique.close();
                psInsert.close();
                psMajDroite.close();
                psMajGauche.close();
                p.state=new String[]{"TERMINE"};
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                return;
            }
            
            // Prépare les paramètres des méthodes d'ajout des voies
            AjoutVoieParams paramDroite = new AjoutVoieParams();
            paramDroite.setConnection(connection);
            paramDroite.setPsInsert(psInsert);
            paramDroite.setPsInsertId(psInsertId);
            paramDroite.setPsMaj(psMajDroite);
            paramDroite.setPsUnique(psUnique);
            paramDroite.setRsTroncons(rsTroncons);
            paramDroite.setPsUniqueId(psUniqueId);
            paramDroite.setSuffixe("droit");
            paramDroite.setCodeDepartement(codeDepartement);
            paramDroite.setPsChercheFantoir(psChercheFantoire);
            paramDroite.setPsUpdateAdresse(psUpdateAdresse);
            
            AjoutVoieParams paramGauche = new AjoutVoieParams();
            paramGauche.setConnection(connection);
            paramGauche.setPsInsert(psInsert);
            paramGauche.setPsInsertId(psInsertId);
            paramGauche.setPsMaj(psMajGauche);
            paramGauche.setPsUnique(psUnique);
            paramGauche.setRsTroncons(rsTroncons);
            paramGauche.setPsUniqueId(psUniqueId);
            paramGauche.setSuffixe("gauche");
            paramGauche.setCodeDepartement(codeDepartement);
            paramGauche.setPsChercheFantoir(psChercheFantoire);
            paramGauche.setPsUpdateAdresse(psUpdateAdresse);
            
            try
            {
                p.state[3] = "TRAITEMENT";
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
                while(!p.stop && rsTroncons.next())
                {
                    boolean misajour = false;

                    misajour |= essayeDAjouterVoie(paramDroite,false);
                    misajour |= essayeDAjouterVoie(paramGauche,false);

                    if (misajour)
                    {
                        connection.commit(); // La transaction est commitée si nécessaire avant de passer au tronçon suivant.
                    }
                    index++;
                    if (index % 100 == 0)
                    {
                        p.state[5] = Integer.toString(index);
                    }
                    if (index%2000==0)
                    {
                        connection.setAutoCommit(true);
                        GestionTables.nettoye(nomTableVoies, connection);
                        connection.setAutoCommit(false);
                    }
                }

                rsTroncons.close();
                stTroncons.close();

                psUnique.close();
                psInsert.close();
                psMajDroite.close();
                psMajGauche.close();
                
                if (p.stop)
                {
                    jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                    p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                }

                if (idx_voi_id_droit_ajoute)
                    GestionTables.supprimeIndex(idx_voi_id_droit,connection);
                if (idx_voi_id_gauche_ajoute)
                    GestionTables.supprimeIndex(idx_voi_id_gauche,connection);
                if (idx_insee_nom_origine_ajoute)
                    GestionTables.supprimeIndex(idx_insee_nom_origine,connection);
                if (idx_voi_id_ajoute)
                    GestionTables.supprimeIndex(idx_voi_id,connection);
                if (idx_code_fantoire_bis_cree)
                    GestionTables.supprimeIndex(idx_code_fantoir_bis,connection);
            }
            finally
            {
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TERMINE");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CREATION DE LA TABLE VOIE");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"troncons parcourus :"+index);
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"sur "+compte);
                p.state[3] = "TERMINE";
                p.resultat.add("CREATION DE LA TABLE VOIE");
                p.resultat.add("troncons parcourus :"+index);
                p.resultat.add("sur "+compte);
                connection.setAutoCommit(autocommit);
            }
        }
        else
            throw(new GestionReferentielException("La table "+nomTableTroncon+" n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
    }
    
    /**
     * Permet de créer la table des codes postaux de la base de mise à jour.<br>
     * La table des codes postaux est calculée à partir de la table des voies.
     *  L'état de la structure processus doit être un tableau d'au moins 6 chaines dont les chaines 4 et 6 seront modifiées
     *  par cette méthode:
     *  <ul><li>Le champ 4 contient l'opération en cour</li>
     *      <li>Le champ 6 contient le compte du nombre de troncon en cour d'analyse</li></ul>
     */
    public void creeTableCodePostauxMaj(Processus p,String code_departement,Connection connection) throws SQLException, GestionReferentielException
    {
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        if (GestionTables.tableExiste("voi_voies_"+code_departement,connection))
        if (GestionTables.tableExiste(GestionTables.getVoiVoiesTableName(code_departement),connection))
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES TABLES ET INDEX");
            p.state[3]="PREPARE LES TABLES ET INDEX";
            
            // Crée la table de codes postaux
            DescriptionTable dtCodesPostaux=GestionDescriptionTables.creeDescriptionTableCodePostauxReferentiel();
            GestionTables.creeTable("cdp_codes_postaux",dtCodesPostaux,connection);
            
            // Crée les index       
            Index idx_code_insee_postal=null;
            idx_code_insee_postal=new Index();
            idx_code_insee_postal.setNom("idx_cdp_code_insee_postal");
            idx_code_insee_postal.ajouteColonne("dpt_code_departement");
            idx_code_insee_postal.ajouteColonne("com_code_insee");
            idx_code_insee_postal.ajouteColonne("cdp_code_postal");
            boolean idx_code_insee_postal_cree=GestionTables.ajouteIndex("cdp_codes_postaux",idx_code_insee_postal,connection);
            
            // Prépare la requête qui liste les voies.
            StringBuilder sb=new StringBuilder();
            sb.append("SELECT com_code_insee, cdp_code_postal FROM \"");
            // WA 09/2011 utilisation de GestionTables.getXXTableName
//            sb.append("voi_voies_").append(code_departement);
            sb.append(GestionTables.getVoiVoiesTableName(code_departement));
            sb.append("\"");
            String rqCherche=sb.toString();
            
            // Prépare la requête qui cherche si un code postal est déjà attribué.
            sb.setLength(0);
            sb.append("SELECT com_code_insee FROM cdp_codes_postaux WHERE dpt_code_departement=? AND com_code_insee=? AND cdp_code_postal=? LIMIT 1");
            PreparedStatement psUnique=connection.prepareStatement(sb.toString());
            
            // Prépare la requête qui insére le nouveau code postal.
            sb.setLength(0);
            sb.append("INSERT INTO cdp_codes_postaux (dpt_code_departement,com_code_insee,cdp_code_postal)");
            sb.append(" VALUES (?,?,?)");
            PreparedStatement psInsert=connection.prepareStatement(sb.toString());
            
            Statement sCherche=connection.createStatement();
            ResultSet rsCherche=sCherche.executeQuery(rqCherche);
            
            if (p.stop)
            {
                rsCherche.close();
                sCherche.close();
                psInsert.close();
                psUnique.close();
                p.state=new String[]{"TERMINE"};
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                return;
            }
            
            int count=0;
            
            while(!p.stop&&rsCherche.next())
            {
                String code_insee=rsCherche.getString(1);
                String code_postal=rsCherche.getString(2);
                
                psUnique.setString(1,code_departement);
                psUnique.setString(2,code_insee);
                psUnique.setString(3,code_postal);
                ResultSet rsUnique=psUnique.executeQuery();
                
                if (!rsUnique.next())
                {
                    psInsert.setString(1,code_departement);
                    psInsert.setString(2,code_insee);
                    psInsert.setString(3,code_postal);
                    psInsert.execute();
                    
                    connection.commit();
                }
                rsUnique.close();
                
                count++;
                
                if (count%200==0)
                {
                    p.state[5]=Integer.toString(count);
                }
            }
            
            rsCherche.close();
            sCherche.close();
            psInsert.close();
            psUnique.close();
            
            if (p.stop)
            {
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            }
            
            p.resultat.add("TERMINE");
            p.resultat.add("CREATION DE LA TABLE DES CODES POSTAUX");
            p.resultat.add(count+" codes postaux ajoutés.");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CREATION DE LA TABLE DES CODES POSTAUX TERMINE");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,count+" codes postaux ajoutés.");
            
            if (idx_code_insee_postal_cree)
            {
                GestionTables.supprimeIndex(idx_code_insee_postal,connection);
            }
        }
        else
            // WA 09/2011 utilisation de GestionTables.getXXTableName
            throw(new GestionReferentielException("La table " + GestionTables.getVoiVoiesTableName(code_departement) + " n'existe pas.",GestionReferentielException.TABLENEXISTEPAS,11));
    }
    
    /**
     * Permet de recalculer la table voie ambigue du département spécifié.<br>
     * La table n'est pas vidée, et les éléments déjà présent ne sont pas ajoutés de nouveau.
     */
    public void calculeClesAmbiguesDansVoies(Processus p,String code_departement,Connection connection) throws SQLException
    {
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"MANAGE LES TABLES");
        p.state = new String[]{"EN COURS","MANAGE LES TABLES"};

        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        String nomTableVoies = "voi_voies_"+code_departement;
        String nomTableVoies = GestionTables.getVoiVoiesTableName(code_departement);
        
        // Crée la table voiesambigues si nécessaire
        DescriptionTable dtVoiesAmbigues = GestionDescriptionTables.creeDescriptionTableVoiesAmbiguesReferentiel();
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        GestionTables.creeTable("voa_voies_ambigues_"+code_departement,dtVoiesAmbigues,connection);
        GestionTables.creeTable(GestionTables.getVoaVoiesAmbiguesTableName(code_departement),dtVoiesAmbigues,connection);
        
        // Prépare la requête permettant d'énumérer les voies
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_id,voi_nom_desab FROM \"");
        sb.append(nomTableVoies);
        sb.append("\"");
        String rqCherche = sb.toString();
        Statement stCherche = connection.createStatement();
        
        // Prépare la requête permettant de vérifier si la voie ambigue n'a pas déjà été répertoriée
        sb.setLength(0);
        sb.append("SELECT voa_mot FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voa_voies_ambigues_").append(code_departement);
        sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
        sb.append("\" WHERE voa_mot=? AND voa_categorie_ambiguite=? AND voa_lbl_pq=? ");
        sb.append("LIMIT 1");
        String rqChercheVoieAmbigue = sb.toString();
        PreparedStatement psChercheVoieAmbigue = connection.prepareStatement(rqChercheVoieAmbigue);
        
        // Prépare la requête permettant d'ajouter une voie ambigue.
        sb.setLength(0);
        sb.append("INSERT INTO \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voa_voies_ambigues_").append(code_departement);
        sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
        sb.append("\" (voa_mot,voa_categorie_ambiguite,voa_lbl_pq) ");
        sb.append(" VALUES (?,?,?)");
        String rqInsertVoieAmbigue = sb.toString();
        PreparedStatement psInsertVoieAmbigue = connection.prepareStatement(rqInsertVoieAmbigue);
        
        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        
        int count = 0,ambiguesajoutees=0,ambiguestrouvees=0;
        try
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES VOIES");
            p.state[1] = "CHERCHE LES VOIES";
            ResultSet rsCherche=stCherche.executeQuery(rqCherche);
            
            if (p.stop)
            {
                stCherche.close();
                psChercheVoieAmbigue.close();
                psInsertVoieAmbigue.close();
                p.state=new String[]{"TERMINE"};
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                return;
            }
            
            // Pour chaque voie,
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
            p.state = new String[]{"EN COURS","TRAITEMENT","VOIES TRAITEES","0"};
            while(!p.stop && rsCherche.next())
            {
                // Optimisation: Permet de n'initialiser qu'une fois par voie 
                // les paramètres de la requête permettant de savoir si une
                // ambiguité est déjà répertoriée
                boolean initPsChercheVoieAmbigue=true; 
                // Optimisation : Permet de n'itialiser qu'une fois par voie
                // les paramètres de la requête permettant de répertorier
                // une ambiguité.
                boolean initPsInsertVoieAmbigue=true;
                
                boolean needcommit = false;
                String id=rsCherche.getString(1);
                String nom_desabbrevie=rsCherche.getString(2);
                
                // Trouve le libelle dans la voie
                String libelle_phonetique = null;
                
                // Cherche l'ambiguité dans ce libellé
                ResultatAmbiguite ra=gestionMots.chercheSiVoieAmbigue(nom_desabbrevie);

                // Pour chaque ambiguité trouvée,
                for(int i=0; i<ra.obtientQuantite(); i++)
                {
                    ambiguestrouvees++;
                    
                    if (libelle_phonetique==null)
                        libelle_phonetique = Algos.phonex(gestionMots.trouveLibelleVoie(nom_desabbrevie,(RefCle)null).obtientMot());
                    
                    // Initialise les paramêtres permettant de savoir si l'ambiguité a été répertoriée
                    if (initPsChercheVoieAmbigue)
                    {
                        psChercheVoieAmbigue.setString(3,libelle_phonetique);
                    }
                    else
                    {
                        initPsChercheVoieAmbigue=false;
                    }
                    psChercheVoieAmbigue.setString(1,ra.obtientMot(i).obtientNom());
                    psChercheVoieAmbigue.setString(2,ra.obtientCategorieAmbiguite(i).toString());
                    
                    // Cherche si l'ambiguité a été répertoriée,
                    ResultSet rsChercheVoieAmbigue=psChercheVoieAmbigue.executeQuery();
                    
                    // Si elle n'a pas été répertoriée,
                    if (!rsChercheVoieAmbigue.next())
                    {
                        // Initialise les paramêtres
                        if (initPsInsertVoieAmbigue)
                        {
                            psInsertVoieAmbigue.setString(3,libelle_phonetique);
                        }
                        else
                        {
                            initPsInsertVoieAmbigue=false;
                        }
                        psInsertVoieAmbigue.setString(1,ra.obtientMot(i).obtientNom());
                        psInsertVoieAmbigue.setString(2,ra.obtientCategorieAmbiguite(i).toString());
                        
                        // Répertorie l'ambiguité
                        ambiguesajoutees++;
                        psInsertVoieAmbigue.execute();
                        needcommit = true;
                    }
                    rsChercheVoieAmbigue.close();
                }
                
                if (needcommit)
                    connection.commit();
                
                count++;
                
                if (count%200==0)
                    p.state[3] = Integer.toString(count);
            }
            
            rsCherche.close();
            stCherche.close();
            psChercheVoieAmbigue.close();
            psInsertVoieAmbigue.close();
            
            if (p.stop)
            {
                jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
                p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            }
        }
        finally
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CALCULE DES VOIES AMBIGUES");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Ambiguites trouvées:"+ambiguestrouvees);
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Ambiguites ajoutées:"+ambiguesajoutees);
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"Nombre de voies traitees:"+count);
            p.resultat.add("CALCULE DES VOIES AMBIGUES");
            p.resultat.add("Ambiguites trouvées:"+ambiguestrouvees);
            p.resultat.add("Ambiguites ajoutées:"+ambiguesajoutees);
            p.resultat.add("Nombre de voies traitees:"+count);
            connection.setAutoCommit(autocommit);
        }
    }
    
    /**
     * Permet de recalculer la portion de la table voie ambigue qui concerne les clés incluses
     * dans les noms de commune.<br>
     * L'état du processus doit être un tableau de chaine dont les chaines suivantes sont utilisées
     * par cette méthode:
     * <ul>
     *     <li>La chaine 5 est utilisée pour décrire l'état de la méthode.</li>
     *     <li>La chaine 7 est utilisée pour spécifier le nombre de lignes traitées.</li>
     * </ul>
     * @param code_departement seules les communes de ce département sont concernées.
     */
    public void calculeClesAmbiguesDansCommunes(Processus p,String code_departement,Connection connection) throws SQLException
    {
        p.state[4] = "PREPARE LES REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Crée la table voiesambigues si nécessaire
        DescriptionTable dtVoiesAmbigues = GestionDescriptionTables.creeDescriptionTableVoiesAmbiguesReferentiel();
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        GestionTables.creeTable("voa_voies_ambigues_"+code_departement,dtVoiesAmbigues,connection);
        GestionTables.creeTable(GestionTables.getVoaVoiesAmbiguesTableName(code_departement),dtVoiesAmbigues,connection);
        
        // Prépare la requête permettant d'énumérer les communes
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT com_nom_desab,com_code_insee FROM com_communes WHERE dpt_code_departement=?"); // Toutes les communes sont référencées
        PreparedStatement psCherche = connection.prepareStatement(sb.toString());
        psCherche.setString(1,code_departement);
        
        // Prépare la requête permettant de chercher si l'ambiguité trouvée est déjà référencée.
        sb.setLength(0);
        sb.append("SELECT voa_mot ");
        sb.append("FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voa_voies_ambigues_").append(code_departement);
        sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
        sb.append("\" WHERE voa_mot=? AND voa_categorie_ambiguite=? AND voa_lbl_pq=? LIMIT 1");
        PreparedStatement psChercheAmbiguite = connection.prepareStatement(sb.toString());
        
        // Prépare la requête qui permet d'insérer l'ambiguité trouvée.
        sb.setLength(0);
        sb.append("INSERT INTO \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voa_voies_ambigues_").append(code_departement);
        sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
        sb.append("\" (voa_mot,voa_categorie_ambiguite,voa_lbl_pq) VALUES (?,?,?)");
        PreparedStatement psInsertAmbiguite = connection.prepareStatement(sb.toString());
        
        p.state[4] = "CHERCHE LES COMMUNES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES COMMUNES");
        ResultSet rsCherche = psCherche.executeQuery();
      
        if (p.stop)
        {
            psCherche.close();
            psChercheAmbiguite.close();
            psInsertAmbiguite.close();
            psChercheAmbiguite.close();
            p.state=new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENT";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENT");
        int count = 0,ambiguite=0;
        while(!p.stop && rsCherche.next())
        {
            String commune_desabbrevie = rsCherche.getString(1);
            
            ResultatAmbiguite ra = gestionMots.chercheSiCommuneAmbigue(commune_desabbrevie);
            
            for(int i=0;i<ra.obtientQuantite();i++)
            {
                String mot = ra.obtientMot(i).obtientNom();
                String categorie = ra.obtientCategorieAmbiguite(i).toString();
                String commune_phonetique = Algos.phonex(commune_desabbrevie);
                psChercheAmbiguite.setString(1,mot);
                psChercheAmbiguite.setString(2,categorie);
                psChercheAmbiguite.setString(3,commune_phonetique);
                ResultSet rsChercheAmbiguite = psChercheAmbiguite.executeQuery();
                if (!rsChercheAmbiguite.next())
                {
                    psInsertAmbiguite.setString(1,mot);
                    psInsertAmbiguite.setString(2,categorie);
                    psInsertAmbiguite.setString(3,commune_phonetique);
                    psInsertAmbiguite.execute();
                    
                    ambiguite++;
                }
                rsChercheAmbiguite.close();
            }
            count++;
            if (count%200==0)
                p.state[6] = Integer.toString(count);
        }
        
        rsCherche.close();
        psCherche.close();
        psChercheAmbiguite.close();
        psInsertAmbiguite.close();
        psChercheAmbiguite.close();
        
        if (p.stop)
        {
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
        }
        
        p.state[4] = "TERMINE";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"RECHERCHE DE CLES DANS COMMUNE TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"communes traitées : "+count);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"ambiguités trouvées : "+ambiguite);
        p.resultat.add("RECHERCHE DE CLES DANS COMMUNE");
        p.resultat.add("communes traitées : "+count);
        p.resultat.add("ambiguités trouvées : "+ambiguite);
    }
    
    /**
     * Permet de recalculer la portion de la table voie ambigue qui concerne les communes incluses dans des
     * noms de voies.
     * L'état du processus doit être un tableau de chaine dont les chaines suivantes sont utilisées
     * par cette méthode:
     * <ul>
     *     <li>La chaine 5 est utilisée pour décrire l'état de la méthode.</li>
     *     <li>La chaine 7 est utilisée pour spécifier le nombre de lignes traitées.</li>
     * </ul>
     * @param code_departement
     * @param connection
     */
    public void calculeCommunesAmbiguesDansVoies(Processus p,String code_departement,Connection connection) throws SQLException
    {
        p.state[4] = "PREPARE LES REQUETES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"PREPARE LES REQUETES");
        
        // Crée la table voiesambigues si nécessaire
        DescriptionTable dtVoiesAmbigues = GestionDescriptionTables.creeDescriptionTableVoiesAmbiguesReferentiel();
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        GestionTables.creeTable("voa_voies_ambigues_"+code_departement,dtVoiesAmbigues,connection);
        GestionTables.creeTable(GestionTables.getVoaVoiesAmbiguesTableName(code_departement),dtVoiesAmbigues,connection);
        
        // Prépare la requête permettant d'énumérer les voies
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_nom_desab FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voi_voies_").append(code_departement);
        sb.append(GestionTables.getVoiVoiesTableName(code_departement));
        sb.append("\"");
        PreparedStatement psChercheVoie = connection.prepareStatement(sb.toString());
        
        // Prépare la requête permettant de chercher les voies qui contiennent un nom de commune.
        sb.setLength(0);
        sb.append("SELECT com_nom ");
        sb.append("FROM com_communes ");
        sb.append("WHERE position_levenstein_joker(?,com_nom_desab,?,0)<>0");
        PreparedStatement psChercheCommune = connection.prepareStatement(sb.toString());
        psChercheCommune.setInt(2,jdonrefParams.obtientPourcentageDeCorrespondanceDeCommune());
        
        // Prépare la requête qui permet de chercher si l'ambiguité trouvée est déjà référencée.
        sb.setLength(0);
        sb.append("SELECT voa_mot ");
        sb.append("FROM \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voa_voies_ambigues_").append(code_departement);
        sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
        sb.append("\" WHERE voa_mot=? AND voa_categorie_ambiguite=? AND voa_lbl_pq=? LIMIT 1");
        PreparedStatement psChercheAmbiguite = connection.prepareStatement(sb.toString());
        psChercheAmbiguite.setString(2,CategorieAmbiguite.CommuneDansVoie.toString());
        
        // Prépare la requête qui permet d'insérer l'ambiguité trouvée.
        sb.setLength(0);
        sb.append("INSERT INTO \"");
        // WA 09/2011 utilisation de GestionTables.getXXTableName
//        sb.append("voa_voies_ambigues_").append(code_departement);
        sb.append(GestionTables.getVoaVoiesAmbiguesTableName(code_departement));
        sb.append("\" (voa_mot,voa_categorie_ambiguite,voa_lbl_pq) VALUES (?,?,?)");
        PreparedStatement psInsertAmbiguite = connection.prepareStatement(sb.toString());
        psInsertAmbiguite.setString(2,CategorieAmbiguite.CommuneDansVoie.toString());
        
        // Pour chaque voie,
        p.state[4] = "CHERCHE LES VOIES";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"CHERCHE LES VOIES");
        ResultSet rsChercheVoie = psChercheVoie.executeQuery();
        
        if (p.stop)
        {
            psChercheVoie.close();
            psChercheCommune.close();
            psChercheAmbiguite.close();
            psInsertAmbiguite.close();
            p.state=new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return;
        }
        
        p.state[4] = "TRAITEMENTS";
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"TRAITEMENTS");
        int count = 0,ambiguite=0;
        while(!p.stop && rsChercheVoie.next())
        {
            String libelle = rsChercheVoie.getString(1);
            String libelle_phonetique = Algos.phonex(libelle);
            boolean initChercheAmbiguite = true; // légère optimisation
            boolean initInsertAmbiguite = true;
            
            // Cherche les communes
            psChercheCommune.setString(1,libelle);
            ResultSet rsChercheCommune = psChercheCommune.executeQuery();
            while(rsChercheCommune.next())
            {
                // Vérifie si l'ambiguité n'est pas déjà référencée.
                String commune = rsChercheCommune.getString(1);
                
                if (initChercheAmbiguite)
                {
                    psChercheAmbiguite.setString(3,libelle_phonetique);
                    initChercheAmbiguite = false;
                }
                psChercheAmbiguite.setString(1,commune);
                
                ResultSet rsChercheAmbiguite = psChercheAmbiguite.executeQuery();
                
                // Si elle n'est pas référencée,
                if (!rsChercheAmbiguite.next())
                {
                    // Insère la nouvelle ambiguité.
                    if (initInsertAmbiguite)
                    {
                        psInsertAmbiguite.setString(3,libelle_phonetique);
                        initInsertAmbiguite = false;
                    }
                    psInsertAmbiguite.setString(1,commune);
                    
                    psInsertAmbiguite.execute();
                    connection.commit();
                    ambiguite++;
                }
                
                rsChercheAmbiguite.close();
            }
            
            rsChercheCommune.close();
            
            count++;
            if (count%200==0)
                p.state[6] = Integer.toString(count);
        }
        
        rsChercheVoie.close();
        psChercheVoie.close();
        psChercheCommune.close();
        psChercheAmbiguite.close();
        psInsertAmbiguite.close();
        
        if (p.stop)
        {
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
        }
        
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"RECHERCHE DES COMMUNES DANS LES VOIES TERMINE");
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"voies traitées:"+count);
        jdonrefParams.getGestionLog().logAdmin(p.numero,p.version,"ambiguite trouvées:"+ambiguite);
        
        p.state[4] = "TERMINE";
        p.resultat.add("RECHERCHE DES COMMUNES DANS LES VOIES");
        p.resultat.add("voies traitées:"+count);
        p.resultat.add("ambiguite trouvées:"+ambiguite);
    }
}