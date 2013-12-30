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
import ppol.jdonref.JDONREFException;

/**
 *
 * @author arochewi
 */
public class ReferentielDao {

    private final static String COLNAME_GETALL_ACT_DPTS_CODES_CODE = "dpt_code_departement";
    private final static String RQ_GETALL_ACT_DPTS_CODES = "SELECT " + COLNAME_GETALL_ACT_DPTS_CODES_CODE + " FROM dpt_departements WHERE t0<=? and t1>=? ORDER BY dpt_code_departement ASC;";

    /**
     * Retourne la projection et le référentiel utilisé pour la construction.
     * @param code_departement
     * @param dt
     * @param connection
     * @return
     * @throws java.sql.SQLException
     */
    public static List<String> getAllActualDptCodes(Connection connection, Date dt) throws JDONREFException {
        List<String> result = null;

        PreparedStatement psAllActDpts = null;
        ResultSet rsAllActDpts = null;
        try {
            psAllActDpts = connection.prepareStatement(RQ_GETALL_ACT_DPTS_CODES);
            Timestamp ts = new Timestamp(dt.getTime());
            psAllActDpts.setTimestamp(1, ts);
            psAllActDpts.setTimestamp(2, ts);
            rsAllActDpts = psAllActDpts.executeQuery();

            if (rsAllActDpts.next()) {
                result = new ArrayList<String>();
                result.add(rsAllActDpts.getString(COLNAME_GETALL_ACT_DPTS_CODES_CODE));
            }
            while (rsAllActDpts.next()) {
                result.add(rsAllActDpts.getString(COLNAME_GETALL_ACT_DPTS_CODES_CODE));
            }
        } catch (NullPointerException npEx) {
            Logger.getLogger("ReferentielDao").log(Level.SEVERE, "Erreur lors de l'obtention des codes departement. " + npEx.getLocalizedMessage());
            throw new JDONREFException("Impossible d'obtenir la liste des codes departement.");
        } catch (SQLException sqlEx) {
            Logger.getLogger("ReferentielDao").log(Level.SEVERE, "Erreur JDBC lors de l'obtention des codes departement. " + sqlEx.getLocalizedMessage());
            throw new JDONREFException("Impossible d'obtenir la liste des codes departement.");
        } finally {
            DbUtils.close(rsAllActDpts);
            DbUtils.close(psAllActDpts);
        }

        return result;
    }
    
    private final static String COLNAME_GETALL_ACT_CP_INSEE = "com_code_insee";
    private final static String COLNAME_GETALL_ACT_CP_CP = "cdp_code_postal";
    private final static String COLNAME_GETALL_ACT_CP_DPT = "dpt_code_departement";
    private final static String RQ_GETALL_ACT_CP = "SELECT " + COLNAME_GETALL_ACT_CP_INSEE + ", " + COLNAME_GETALL_ACT_CP_CP + ", " + COLNAME_GETALL_ACT_CP_DPT + " FROM cdp_codes_postaux WHERE t0<=? and t1>=? ORDER BY dpt_code_departement ASC;";

    /**
     * Recupere en base tous les codes postaux valides a la date passee.
     * @param connection
     * @param dt
     * @return
     */
    public static List<CdpCodesPostauxBean> getAllActualCdpCodesPostaux(Connection connection, Date dt) throws JDONREFException {
        List<CdpCodesPostauxBean> result = new ArrayList<CdpCodesPostauxBean>();

        PreparedStatement psAllActCPs = null;
        ResultSet rsAllActCps = null;
        try {
            psAllActCPs = connection.prepareStatement(RQ_GETALL_ACT_CP);
            Timestamp ts = new Timestamp(dt.getTime());
            psAllActCPs.setTimestamp(1, ts);
            psAllActCPs.setTimestamp(2, ts);
            rsAllActCps = psAllActCPs.executeQuery();

            if (rsAllActCps.next()) {
                result = new ArrayList<CdpCodesPostauxBean>();
                CdpCodesPostauxBean bean = new CdpCodesPostauxBean(rsAllActCps.getString(COLNAME_GETALL_ACT_CP_INSEE), rsAllActCps.getString(COLNAME_GETALL_ACT_CP_CP), rsAllActCps.getString(COLNAME_GETALL_ACT_DPTS_CODES_CODE), null, null);
                result.add(bean);
            }
            while (rsAllActCps.next()) {
                CdpCodesPostauxBean bean = new CdpCodesPostauxBean(rsAllActCps.getString(COLNAME_GETALL_ACT_CP_INSEE), rsAllActCps.getString(COLNAME_GETALL_ACT_CP_CP), rsAllActCps.getString(COLNAME_GETALL_ACT_DPTS_CODES_CODE), null, null);
                result.add(bean);
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger("ReferentielDao").log(Level.SEVERE, "Erreur JDBC lors de l'obtention des codes postaux. " + sqlEx.getLocalizedMessage());
            throw new JDONREFException("Impossible d'obtenir la liste des codes postaux.");
        } finally {
            DbUtils.close(rsAllActCps);
            DbUtils.close(psAllActCPs);
        }

        return result;
    }
}
