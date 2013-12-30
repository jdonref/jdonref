/*
 * Version 2.3.0 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
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

package ppol.jdonref.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arochewi
 */
public class RecherchesDao
{
    private final static String RQCODEPAYSSEARCH = "SELECT DISTINCT pay_pays.pay_sov_a3, pay_pays.pay_nom_origine, " +
            "pay_pays.pay_nom_int, pay_pays.pay_nom_fr, pay_pays.pay_nom_fr_desab, pay_pays.pay_type, pay_pays.pay_nom_capitale, " +
            "pay_pays.t0, pay_pays.t1 " +
            "FROM pay_pays INNER JOIN ( " +
            "SELECT pay_pays.pay_sov_a3, MAX(pay_pays.t1) AS maxt1 " +
            "FROM pay_pays WHERE pay_sov_a3 = ? AND pay_pays.t0 <= ? AND pay_pays.t1 >= ? " +
            "GROUP BY pay_sov_a3 ) grp " +
            "ON pay_pays.pay_sov_a3 = grp.pay_sov_a3 AND pay_pays.t1 = grp.maxt1 LIMIT ?; ";
    public static List<PayPaysBean> foundPaysByCodeAtDate(Connection connection, String codeSovAc3, Date dt, int limit) throws SQLException
    {
        List<PayPaysBean> result = new ArrayList<PayPaysBean>();

        PreparedStatement psExactName = null;
        ResultSet rsExactName = null;
        try
        {
            psExactName = connection.prepareStatement(RQCODEPAYSSEARCH);
            Timestamp ts = new Timestamp(dt.getTime());
            psExactName.setString(1, codeSovAc3);
            psExactName.setTimestamp(2, ts);
            psExactName.setTimestamp(3, ts);
            psExactName.setInt(4, limit);
            rsExactName = psExactName.executeQuery();

            while(rsExactName.next())
            {
                PayPaysBean bean = new PayPaysBean(rsExactName.getString("pay_sov_a3"), rsExactName.getString("pay_nom_origine"),
                        rsExactName.getString("pay_nom_int"), rsExactName.getString("pay_nom_fr"), rsExactName.getString("pay_nom_fr_desab"),
                        rsExactName.getString("pay_type"), rsExactName.getString("pay_nom_capitale"), rsExactName.getDate("t0"),
                        rsExactName.getDate("t1"), 200);
                result.add(bean);
            }
        } catch(SQLException sqlEx)
        {
            Logger.getLogger("RecherchesDao").log(Level.SEVERE, "Erreur JDBC lors de la recherche par code de pays. " + sqlEx.
                    getLocalizedMessage());
            throw sqlEx;
        } finally
        {
            DbUtils.close(rsExactName);
            DbUtils.close(psExactName);
        }

        return result;
    }

    private final static String RQLATESTEXACTPAYSSEARCH = "SELECT DISTINCT pay_pays.pay_sov_a3, pay_pays.pay_nom_origine, " +
            "pay_pays.pay_nom_int, pay_pays.pay_nom_fr, pay_pays.pay_nom_fr_desab, pay_pays.pay_type, pay_pays.pay_nom_capitale, " +
            "pay_pays.t0, pay_pays.t1 " +
            "FROM pay_pays INNER JOIN ( " +
            "SELECT pay_pays.pay_sov_a3, MAX(pay_pays.t1) AS maxt1 " +
            "FROM pay_pays WHERE pay_nom_fr_desab = ? AND pay_pays.t0 <= ? AND pay_pays.t1 >= ? " +
            "GROUP BY pay_sov_a3 ) grp " +
            "ON pay_pays.pay_sov_a3 = grp.pay_sov_a3 AND pay_pays.t1 = grp.maxt1 LIMIT ?; ";
    public static List<PayPaysBean> foundPaysByNameAtDate(Connection connection, String desabName, Date dt, int limit) throws SQLException
    {
        List<PayPaysBean> result = new ArrayList<PayPaysBean>();

        PreparedStatement psExactName = null;
        ResultSet rsExactName = null;
        try
        {
            psExactName = connection.prepareStatement(RQLATESTEXACTPAYSSEARCH);
            Timestamp ts = new Timestamp(dt.getTime());
            psExactName.setString(1, desabName);
            psExactName.setTimestamp(2, ts);
            psExactName.setTimestamp(3, ts);
            psExactName.setInt(4, limit);
            rsExactName = psExactName.executeQuery();

            while(rsExactName.next())
            {
                PayPaysBean bean = new PayPaysBean(rsExactName.getString("pay_sov_a3"), rsExactName.getString("pay_nom_origine"),
                        rsExactName.getString("pay_nom_int"), rsExactName.getString("pay_nom_fr"), rsExactName.getString("pay_nom_fr_desab"),
                        rsExactName.getString("pay_type"), rsExactName.getString("pay_nom_capitale"), rsExactName.getDate("t0"),
                        rsExactName.getDate("t1"), 200);
                result.add(bean);
            }
        } catch(SQLException sqlEx)
        {
            Logger.getLogger("RecherchesDao").log(Level.SEVERE, "Erreur JDBC lors de la recherche exacte de pays. " + sqlEx.
                    getLocalizedMessage());
            throw sqlEx;
        } finally
        {
            DbUtils.close(rsExactName);
            DbUtils.close(psExactName);
        }

        return result;
    }

    private final static String RQLATESTLEVPAYSSEARCH = "SELECT DISTINCT pay_pays.pay_sov_a3, pay_pays.pay_nom_origine, " +
            "pay_pays.pay_nom_int, pay_pays.pay_nom_fr, pay_pays.pay_nom_fr_desab, pay_pays.pay_type, pay_pays.pay_nom_capitale, " +
            "pay_pays.t0, pay_pays.t1, note_commune_seul(?, pay_nom_fr_desab) AS note " +
            "FROM pay_pays INNER JOIN ( " +
            "SELECT pay_pays.pay_sov_a3, MAX(pay_pays.t1) AS maxt1 " +
            "FROM pay_pays WHERE note_commune_seul(?, pay_nom_fr_desab) > ? AND pay_pays.t0 <= ? AND pay_pays.t1 >= ? " +
            "GROUP BY pay_sov_a3 ) grp " +
            "ON pay_pays.pay_sov_a3 = grp.pay_sov_a3 AND pay_pays.t1 = grp.maxt1 ORDER BY note DESC LIMIT ?; ";
    public static List<PayPaysBean> foundPaysByLevAtDate(Connection connection, String desabName, Date dt, int seuil, int limit) throws SQLException
    {
        List<PayPaysBean> result = new ArrayList<PayPaysBean>();

        PreparedStatement psLevName = null;
        ResultSet rsLevName = null;
        try
        {
            psLevName = connection.prepareStatement(RQLATESTLEVPAYSSEARCH);
            Timestamp ts = new Timestamp(dt.getTime());
            psLevName.setString(1, desabName);
            psLevName.setString(2, desabName);
            psLevName.setInt(3, seuil);
            psLevName.setTimestamp(4, ts);
            psLevName.setTimestamp(5, ts);
            psLevName.setInt(6, limit);
            rsLevName = psLevName.executeQuery();

            while(rsLevName.next())
            {
                PayPaysBean bean = new PayPaysBean(rsLevName.getString("pay_sov_a3"), rsLevName.getString("pay_nom_origine"),
                        rsLevName.getString("pay_nom_int"), rsLevName.getString("pay_nom_fr"), rsLevName.getString("pay_nom_fr_desab"),
                        rsLevName.getString("pay_type"), rsLevName.getString("pay_nom_capitale"), rsLevName.getDate("t0"),
                        rsLevName.getDate("t1"), rsLevName.getInt("note"));
                result.add(bean);
            }
        } catch(SQLException sqlEx)
        {
            Logger.getLogger("RecherchesDao").log(Level.SEVERE, "Erreur JDBC lors de la recherche approximative de pays. " + sqlEx.
                    getLocalizedMessage());
            throw sqlEx;
        } finally
        {
            DbUtils.close(rsLevName);
            DbUtils.close(psLevName);
        }

        return result;
    }
    
    
   
}
