/*
 * Version 2.4.0 – 2012
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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.JDONREFParams;

/**
 *
 * @author arochewi
 */
public class GeometryDao {
    private final JDONREFParams jdonrefparams;

    public GeometryDao(JDONREFParams jdonrefparams) {
        this.jdonrefparams = jdonrefparams;
    }
    
    
    /**
     * Recupere la geometrie du point central d'un pays a partir de son code sovAc3 (code ISO sov ac3)
     * La geometrie est retournee au format EWKT ie SRID=????;POINT(XXXXX YYYYY)
     * Ex.: "SRID=4326;POINT(2.33138946713035 48.8686387898146)"
     * @param connection
     * @param sovAc3
     * @param dt
     * @return
     * @throws SQLException
     */
    @Deprecated
    public static JDRWktGeometryBean getPaysPtCentralEwktGeomFromSovAc3(Connection connection, String sovAc3, Date dt) throws SQLException {
        return getPaysPtCentralWktGeomFromSovAc3(connection, sovAc3, dt, true);
    }

    /**
     * Recupere la geometrie du point central d'un pays a partir de son code sovAc3 (code ISO sov ac3)
     * La geometrie est retournee au format WKT ie POINT(XXXXX YYYYY)
     * Ex.: "POINT(2.33138946713035 48.8686387898146)"
     * @param connection
     * @param sovAc3
     * @param dt
     * @return
     * @throws SQLException
     */
    @Deprecated
    public static JDRWktGeometryBean getPaysPtCentralWktGeomFromSovAc3(Connection connection, String sovAc3, Date dt) throws SQLException {
        return getPaysPtCentralWktGeomFromSovAc3(connection, sovAc3, dt, false);
    }
    
    @Deprecated
    private final static String RQ_PAYS_PT_CENTRAL_EWKT_GEOMFROM_SOVAC3_DEB = "SELECT pay_sov_a3, ST_AsEWKT(pay_point_central) AS geomStr, " +
            "pay_projection, pay_referentiel ";
    @Deprecated
    private final static String RQ_PAYS_PT_CENTRAL_WKT_GEOMFROM_SOVAC3_DEB = "SELECT pay_sov_a3, ST_AsTEXT(pay_point_central) AS geomStr, " +
            "pay_projection, pay_referentiel ";
    private final static String RQ_PAYS_PT_CENTRAL_GEOMFROM_SOVAC3_END = "FROM pay_pays WHERE pay_sov_a3 = ? AND t0 <= ? AND t1 >= ? " +
            "LIMIT 1 ;";

    @Deprecated
    private static JDRWktGeometryBean getPaysPtCentralWktGeomFromSovAc3(Connection connection, String sovAc3, Date dt, boolean withSrid) throws SQLException {
        JDRWktGeometryBean result = null;


        PreparedStatement psCountryWkt = null;
        ResultSet rsCountryWkt = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (withSrid) {
                sb.append(RQ_PAYS_PT_CENTRAL_EWKT_GEOMFROM_SOVAC3_DEB);
            } else {
                sb.append(RQ_PAYS_PT_CENTRAL_WKT_GEOMFROM_SOVAC3_DEB);
            }
            sb.append(RQ_PAYS_PT_CENTRAL_GEOMFROM_SOVAC3_END);
            psCountryWkt = connection.prepareStatement(sb.toString());
            Timestamp ts = new Timestamp(dt.getTime());
            psCountryWkt.setString(1, sovAc3);
            psCountryWkt.setTimestamp(2, ts);
            psCountryWkt.setTimestamp(3, ts);
            rsCountryWkt = psCountryWkt.executeQuery();

            if (rsCountryWkt.next()) {
                result = new JDRWktGeometryBean();
                result.setWkt(rsCountryWkt.getString("geomStr"));
                result.setProjectionText(rsCountryWkt.getString("pay_projection"));
                result.setReferentielText(rsCountryWkt.getString("pay_referentiel"));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger("RecherchesDao").log(Level.SEVERE, "Erreur JDBC lors de l'obtention de la geometry de pays. " + sqlEx.getLocalizedMessage());
            throw sqlEx;
        } finally {
            DbUtils.close(rsCountryWkt);
            DbUtils.close(psCountryWkt);
        }

        return result;
    }
    
    public JDRWktGeometryBean getPaysPtCentralWktGeomFromSovAc3(Connection connection, String sovAc3, Date dt, int projection) throws SQLException {
        return getPaysPtCentralWktGeomFromSovAc3(connection, sovAc3, dt, projection, false);
    }
    
    private JDRWktGeometryBean getPaysPtCentralWktGeomFromSovAc3(Connection connection, String sovAc3, Date dt, int projection, boolean withSrid) throws SQLException {
        JDRWktGeometryBean result = null;


        PreparedStatement psCountryWkt = null;
        ResultSet rsCountryWkt = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (withSrid) {
                sb.append("SELECT pay_sov_a3, ST_AsEWKT(st_transform(pay_point_central,");
            } else {
                sb.append("SELECT pay_sov_a3, ST_AsTEXT(st_transform(pay_point_central,");
            }
            sb.append((projection != 0) ? projection : jdonrefparams.obtientProjectionPaysParDefaut());
            sb.append(")) AS geomStr, pay_referentiel ");
            sb.append(RQ_PAYS_PT_CENTRAL_GEOMFROM_SOVAC3_END);
            psCountryWkt = connection.prepareStatement(sb.toString());
            Timestamp ts = new Timestamp(dt.getTime());
            psCountryWkt.setString(1, sovAc3);
            psCountryWkt.setTimestamp(2, ts);
            psCountryWkt.setTimestamp(3, ts);
            rsCountryWkt = psCountryWkt.executeQuery();
            final String projectionSRID = (projection != 0) ? String.valueOf(projection) : jdonrefparams.obtientProjectionPaysParDefaut();
            if (rsCountryWkt.next()) {
                result = new JDRWktGeometryBean();
                result.setWkt(rsCountryWkt.getString("geomStr"));
                result.setProjectionSRID(projectionSRID);
                result.setReferentielText(rsCountryWkt.getString("pay_referentiel"));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger("RecherchesDao").log(Level.SEVERE, "Erreur JDBC lors de l'obtention de la geometry de pays. " + sqlEx.getLocalizedMessage());
            throw sqlEx;
        } finally {
            DbUtils.close(rsCountryWkt);
            DbUtils.close(psCountryWkt);
        }

        return result;
    }
}
