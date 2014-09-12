package ppol.jdonref.dao;

import ppol.jdonref.JDONREFv3Exception;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.RefConnection;
import ppol.jdonref.utils.GeometryUtils;
import ppol.jdonref.poizon.Ligne1;
import ppol.jdonref.poizon.Services;
import ppol.jdonref.utils.MiscUtils;

/**
 *
 * @author marcanhe
 */
public class PoizonDao {

    private final JDONREFParams params;
    private final GestionConnection gestionConnection;
    private final Services servicesTree;

    protected HashMap<Integer,Integer> serviceByCle = null;

    public PoizonDao(JDONREFParams params, GestionConnection gestionConnection, Services servicesTree) {
        this.params = params;
        this.gestionConnection = gestionConnection;
        this.servicesTree = servicesTree;
    }
    
    public HashMap<Integer, Integer> getServiceByCle() {
        return serviceByCle;
    }

    public void setServiceByCle(HashMap<Integer, Integer> serviceByCle) {
        this.serviceByCle = serviceByCle;
    }
    
    public List<PoizonBean> findGeocodageInverse(
            int[] services,
            String[] position,
            double distance,
            Date date,
            int projection)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        PreparedStatement ps = null;
        RefConnection refConnection = null;
        try {
            refConnection = gestionConnection.obtientConnection();
        } catch (JDONREFException jde) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "La connection n'a pas pu être effectuée.", jde);
            throw new JDONREFv3Exception(3, "La connection n'a pas pu être effectuée.");

        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant le géocodage inverse.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant le géocodage inverse.");
        }
        try {
            final String query = getQueryForGeocodeInverse(services, position, projection, distance);
            ps = refConnection.connection.prepareStatement(query);
            final java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
            ps.setTimestamp(1, timestamp);
            ps.setTimestamp(2, timestamp);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listRet.add(getBeanForGeocodageInverse(rs));
            }
            ps.close();
        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant le géocodage inverse.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant le géocodage inverse.");
        } finally {
            if (refConnection != null) {
                gestionConnection.relache(refConnection);
            }
        }

        return listRet;

    }

    public List<PoizonBean> findGeocodage(
            int[] services,
            String[] donnees,
            String[] ids,
            Date date,
            int projection)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        PreparedStatement ps = null;
        RefConnection refConnection = null;
        try {
            refConnection = gestionConnection.obtientConnection();
        } catch (JDONREFException jde) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "La connection n'a pas pu être effectuée.", jde);
            throw new JDONREFv3Exception(3, "La connection n'a pas pu être effectuée.");

        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant le géocodage.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant le géocodage.");
        }
        try {
            final String query = getQueryForGeocode(services, projection);
            ps = refConnection.connection.prepareStatement(query);
            final java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
            ps.setString(1, donnees[0]);
            ps.setString(2, ids[0]);
            ps.setTimestamp(3, timestamp);
            ps.setTimestamp(4, timestamp);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listRet.add(getBeanForGeocodage(rs, projection, date));
            }
            ps.close();
        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant le géocodage.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant le géocodage.");
        } finally {
            if (refConnection != null) {
                gestionConnection.relache(refConnection);
            }
        }

        return listRet;

    }

    public List<PoizonBean> findValidation(
            int[] services,
            String[] donnees,
            String[] ids,
            boolean force,
            Date date,
            Ligne1 ligne1)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        RefConnection refConnection = null;
        try {
            refConnection = gestionConnection.obtientConnection();
        } catch (JDONREFException jde) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "La connection n'a pas pu être effectuée.", jde);
            throw new JDONREFv3Exception(3, "La connection n'a pas pu être effectuée.");

        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant la validation.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant la validation.");
        }
        try {
            final String cle = ligne1.getCle();
            final String libelle = ligne1.getLibelleSansArticle();
            if (force) {
                final String libellePq = ligne1.getLibelleSansArticlePhonetique();
                final String clePq = ligne1.getClePhonetique();
                listRet.addAll(rechercheOrthographiqueEtPhonetique(services, clePq, libellePq, donnees, ids, date, refConnection.connection));
            } else {
                List<PoizonBean> list = rechercheExacte(services, cle, libelle, donnees, ids, date, refConnection.connection);
                if (list.isEmpty()) {
                    final String libellePq = ligne1.getLibelleSansArticlePhonetique();
                    final String clePq = ligne1.getClePhonetique();
                    list = rechercheOrthographiqueEtPhonetique(services, clePq, libellePq, donnees, ids, date, refConnection.connection);
                }
                listRet.addAll(list);
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant la validation.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant la validation.");
        } finally {
            if (refConnection != null) {
                gestionConnection.relache(refConnection);
            }
        }

        return listRet;

    }

    public List<PoizonBean> findRevalidation(
            int[] services,
            String[] ids,
            Date dateParam,
            Date dateOption)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        PreparedStatement ps = null;
        RefConnection refConnection = null;
        try {
            refConnection = gestionConnection.obtientConnection();
        } catch (JDONREFException jde) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "La connection n'a pas pu être effectuée.", jde);
            throw new JDONREFv3Exception(3, "La connection n'a pas pu être effectuée.");

        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant la validation.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant la validation.");
        }
        try {
            final String query = getQueryForRevalidation(services, ids, dateOption != null);
            ps = refConnection.connection.prepareStatement(query);
            final java.sql.Timestamp timestampParam = new java.sql.Timestamp(dateParam.getTime());
            ps.setTimestamp(1, timestampParam);
            if (dateOption != null) {
                final java.sql.Timestamp timestampOption = new java.sql.Timestamp(dateOption.getTime());
                ps.setTimestamp(2, timestampOption);
                ps.setInt(3, params.obtientNombreDeVoieParDefaut());
            } else {
                ps.setInt(2, params.obtientNombreDeVoieParDefaut());
            }
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listRet.add(getBeanForRevalidation(rs));
            }
            ps.close();
        } catch (SQLException sqle) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Problème SQl durant la validation.", sqle);
            throw new JDONREFv3Exception(3, "Problème SQl durant la validation.");
        } finally {
            if (refConnection != null) {
                gestionConnection.relache(refConnection);
            }
        }

        return listRet;

    }

    private List<PoizonBean> rechercheExacte(int[] services, String cle, String libelle, String[] donnees, String[] ids, Date date, Connection connection)
            throws SQLException {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        final String query = getQueryForRechercheExacte(services, cle, libelle, donnees, ids);
        final PreparedStatement ps = connection.prepareStatement(query);
        final java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        ps.setTimestamp(1, timestamp);
        ps.setTimestamp(2, timestamp);
        ps.setInt(3, params.obtientNombreDeVoieParDefaut());
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            listRet.add(getBeanForValidation(rs));
        }
        ps.close();

        return listRet;

    }

    private List<PoizonBean> rechercheOrthographiqueEtPhonetique(int[] services, String cle, String libelle, String[] donnees, String[] ids, Date date, Connection connection)
            throws SQLException {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        final String query = getQueryForRechercheOrthographiqueEtPhonetique(services, cle, libelle, donnees, ids);
        final PreparedStatement ps = connection.prepareStatement(query);
        final java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        ps.setTimestamp(1, timestamp);
        ps.setTimestamp(2, timestamp);
        ps.setInt(3, params.obtientNombreDeVoieParDefaut());
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            listRet.add(getBeanForValidation(rs));
        }
        ps.close();

        return listRet;

    }

    private PoizonBean getBeanForValidation(ResultSet rs) throws SQLException {
        final PoizonBean beanRet = new PoizonBean();
        beanRet.setNote(rs.getInt(1));
        beanRet.setService(this.serviceByCle.get(rs.getInt(2)));
        beanRet.setT0(new Date(rs.getTimestamp(3).getTime()));
        beanRet.setT1(new Date(rs.getTimestamp(4).getTime()));
        beanRet.setDonnee1(rs.getString(5));
        beanRet.setDonnee2(rs.getString(6));
        beanRet.setDonnee3(rs.getString(7));
        beanRet.setDonnee4(rs.getString(8));
        beanRet.setDonnee5(rs.getString(9));
        beanRet.setDonnee6(rs.getString(10));
        beanRet.setDonnee7(rs.getString(11));
        beanRet.setId1(rs.getString(12));
        beanRet.setId2(rs.getString(13));
        beanRet.setId3(rs.getString(14));
        beanRet.setId4(rs.getString(15));
        beanRet.setId5(rs.getString(16));
        beanRet.setId6(rs.getString(17));
        beanRet.setId7(rs.getString(18));
        beanRet.setDonneeOrigine1(rs.getString(19));
        beanRet.setDonneeOrigine2(rs.getString(20));
        beanRet.setDonneeOrigine3(rs.getString(21));
        beanRet.setDonneeOrigine4(rs.getString(22));
        beanRet.setDonneeOrigine5(rs.getString(23));
        beanRet.setDonneeOrigine6(rs.getString(24));
        beanRet.setDonneeOrigine7(rs.getString(25));

        return beanRet;
    }

    private PoizonBean getBeanForRevalidation(ResultSet rs) throws SQLException {
        final PoizonBean beanRet = new PoizonBean();
        beanRet.setService(this.serviceByCle.get(rs.getInt(1)));
        beanRet.setT0(new Date(rs.getTimestamp(2).getTime()));
        beanRet.setT1(new Date(rs.getTimestamp(3).getTime()));
        beanRet.setDonnee1(rs.getString(4));
        beanRet.setDonnee2(rs.getString(5));
        beanRet.setDonnee3(rs.getString(6));
        beanRet.setDonnee4(rs.getString(7));
        beanRet.setDonnee5(rs.getString(8));
        beanRet.setDonnee6(rs.getString(9));
        beanRet.setDonnee7(rs.getString(10));
        beanRet.setId1(rs.getString(11));
        beanRet.setId2(rs.getString(12));
        beanRet.setId3(rs.getString(13));
        beanRet.setId4(rs.getString(14));
        beanRet.setId5(rs.getString(15));
        beanRet.setId6(rs.getString(16));
        beanRet.setId7(rs.getString(17));
        beanRet.setDonneeOrigine1(rs.getString(18));
        beanRet.setDonneeOrigine2(rs.getString(19));
        beanRet.setDonneeOrigine3(rs.getString(20));
        beanRet.setDonneeOrigine4(rs.getString(21));
        beanRet.setDonneeOrigine5(rs.getString(22));
        beanRet.setDonneeOrigine6(rs.getString(23));
        beanRet.setDonneeOrigine7(rs.getString(24));

        return beanRet;
    }

    private PoizonBean getBeanForGeocodage(ResultSet rs, int projection, Date date) throws SQLException, JDONREFv3Exception {
        final PoizonBean beanRet = new PoizonBean();
        beanRet.setProjection((projection != 0) ? String.valueOf(projection) : params.obtientProjectionPardefaut());
        beanRet.setService(this.serviceByCle.get(rs.getInt(2)));
        beanRet.setReferentiel(rs.getString(3));
        beanRet.setDonnee1(rs.getString(4));
        beanRet.setId1(rs.getString(5));
        beanRet.setDate(date);
        final String strgeometrie = rs.getString(1);
        try {
            final Geometry geometry = GeometryUtils.readGeometryFromWKT(strgeometrie);
            beanRet.setGeometrie(geometry);
        } catch (ParseException pe) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Erreur de géométrie pour " + beanRet.getDonnee1() + " " + beanRet.getId1() + " .", pe);
            throw new JDONREFv3Exception(10, "Erreur de géométrie pour " + beanRet.getDonnee1() + " " + beanRet.getId1() + " .");
        }

        return beanRet;
    }

    private PoizonBean getBeanForGeocodageInverse(ResultSet rs) throws SQLException, JDONREFv3Exception {
        final PoizonBean beanRet = new PoizonBean();
        beanRet.setDistance(MiscUtils.truncate(rs.getDouble(1), 2));
        beanRet.setService(this.serviceByCle.get(rs.getInt(2)));
        beanRet.setT0(new Date(rs.getTimestamp(3).getTime()));
        beanRet.setT1(new Date(rs.getTimestamp(4).getTime()));
        beanRet.setDonnee1(rs.getString(5));
        beanRet.setDonnee2(rs.getString(6));
        beanRet.setDonnee3(rs.getString(7));
        beanRet.setDonnee4(rs.getString(8));
        beanRet.setDonnee5(rs.getString(9));
        beanRet.setDonnee6(rs.getString(10));
        beanRet.setDonnee7(rs.getString(11));
        beanRet.setId1(rs.getString(12));
        beanRet.setId2(rs.getString(13));
        beanRet.setId3(rs.getString(14));
        beanRet.setId4(rs.getString(15));
        beanRet.setId5(rs.getString(16));
        beanRet.setId6(rs.getString(17));
        beanRet.setId7(rs.getString(18));
        beanRet.setDonneeOrigine1(rs.getString(19));
        beanRet.setDonneeOrigine2(rs.getString(20));
        beanRet.setDonneeOrigine3(rs.getString(21));
        beanRet.setDonneeOrigine4(rs.getString(22));
        beanRet.setDonneeOrigine5(rs.getString(23));
        beanRet.setDonneeOrigine6(rs.getString(24));
        beanRet.setDonneeOrigine7(rs.getString(25));
        beanRet.setReferentiel(rs.getString(26));
        final String strgeometrie = rs.getString(27);
        try {
            final Geometry geometry = GeometryUtils.readGeometryFromWKT(strgeometrie);
            beanRet.setGeometrie(geometry);
        } catch (ParseException pe) {
            Logger.getLogger(PoizonDao.class.getName()).log(Level.SEVERE, "Erreur de géométrie pour " + beanRet.getDonnee1() + " " + beanRet.getId1() + " .", pe);
            throw new JDONREFv3Exception(10, "Erreur de géométrie pour " + beanRet.getDonnee1() + " " + beanRet.getId1() + " .");
        }
        return beanRet;
    }

    private String getQueryForGeocodeInverse(int[] services, String[] position, int projection, double distance) {
        final StringBuilder sb = new StringBuilder();
        final String pointExpression = getTransformPointExpression(position, projection);
        sb.append("SELECT st_distance_spheroid(geometrie,");
        sb.append(pointExpression);
        sb.append(",'");
        sb.append(params.obtientSpheroidPardefaut());
        sb.append("'), ");
        sb.append("poizon_service, t0, t1, ");
        sb.append("poizon_donnee1, poizon_donnee2, poizon_donnee3, poizon_donnee4, poizon_donnee5, poizon_donnee6, poizon_donnee7, ");
        sb.append("poizon_id1, poizon_id2, poizon_id3, poizon_id4, poizon_id5, poizon_id6, poizon_id7, ");
        sb.append("poizon_donnee_origine1, poizon_donnee_origine2, poizon_donnee_origine3, poizon_donnee_origine4, ");
        sb.append("poizon_donnee_origine5, poizon_donnee_origine6, poizon_donnee_origine7, poizon_referentiel, st_astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : params.obtientProjectionPardefaut());
        sb.append(")) ");
        sb.append("FROM POIZON ");
        sb.append("WHERE ((st_dwithin(geometrie,"); // POUR UTILISER L'INDEX DE LA COLONNE GEOMETRIE !!
        sb.append(pointExpression);
        sb.append(", ");
        sb.append(MiscUtils.distanceSpheroid(distance));
        sb.append(") ");
        sb.append("AND st_distance_spheroid(geometrie,");
        sb.append(pointExpression);
        sb.append(",'");
        sb.append(params.obtientSpheroidPardefaut());
        sb.append("') < ");
        sb.append(distance);
        sb.append(") OR (");
        sb.append("st_within(");
        sb.append(pointExpression);
        sb.append(",geometrie");
        sb.append("))) ");
        sb.append("AND t0 < ? AND t1 > ? ");
        if (services != null && services.length > 0) {
            sb.append("AND (");
            sb.append(getAddServicesExpression(services));
            sb.append(")");
        }

        return sb.toString();

    }

    private String getQueryForRechercheExacte(int[] services, String cle, String libelle, String[] donnees, String[] ids) {
        final StringBuilder sb = new StringBuilder();
        final String[] donnees7 = new String[7];
        for (int i = 0; i < 7; i++) {
            donnees7[i] = (i < donnees.length && donnees[i] != null) ? donnees[i] : "";
        }
        sb.append("SELECT note_cle_poizon(");
        sb.append("'");
        sb.append((cle != null) ? cle : "");
        sb.append("'");
        sb.append(", poizon_cle, ");
        sb.append("'");
        sb.append((libelle != null) ? libelle : "");
        sb.append("'");
        sb.append(", poizon_lbl_sans_articles");
        for (int i = 1; i < donnees7.length; i++) {
            sb.append(", ");
            sb.append("'");
            sb.append(donnees7[i]);
            sb.append("'");
            sb.append(", poizon_donnee");
            sb.append(i + 1);
        }
        sb.append(") as note,");
        sb.append(" poizon_service, t0, t1, ");
        sb.append(" poizon_donnee1, poizon_donnee2, poizon_donnee3, poizon_donnee4, poizon_donnee5, poizon_donnee6, poizon_donnee7,");
        sb.append(" poizon_id1, poizon_id2, poizon_id3, poizon_id4, poizon_id5, poizon_id6, poizon_id7,");
        sb.append(" poizon_donnee_origine1, poizon_donnee_origine2, poizon_donnee_origine3, poizon_donnee_origine4,");
        sb.append(" poizon_donnee_origine5, poizon_donnee_origine6, poizon_donnee_origine7");
        sb.append(" FROM POIZON ");
        sb.append(" WHERE poizon_lbl_sans_articles = '");
        sb.append(libelle);
        sb.append("'");
        if (cle != null && cle.trim().length() > 0) {
            sb.append(" AND poizon_cle = '");
            sb.append(cle);
            sb.append("'");
        }
        for (int i = 1; i < donnees.length; i++) {
            if (donnees[i] != null && donnees[i].trim().length() > 0) {
                sb.append(" AND poizon_donnee");
                sb.append(i + 1);
                sb.append(" = '");
                sb.append(donnees[i]);
                sb.append("'");
            }
        }
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != null && ids[i].trim().length() > 0) {
                sb.append(" AND poizon_id");
                sb.append(i + 1);
                sb.append(" = '");
                sb.append(ids[i]);
                sb.append("'");
            }
        }
        sb.append(" AND t0 < ? AND t1 > ? ");
        if (services != null && services.length > 0) {
            sb.append(" AND (");
            sb.append(getAddServicesExpression(services));
            sb.append(")");
        }
        sb.append(" ORDER BY note DESC LIMIT ?");

        return sb.toString();

    }

    private String getQueryForRevalidation(int[] services, String[] ids, boolean withDateOption) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" SELECT poizon_service, t0, t1, ");
        sb.append(" poizon_donnee1, poizon_donnee2, poizon_donnee3, poizon_donnee4, poizon_donnee5, poizon_donnee6, poizon_donnee7,");
        sb.append(" poizon_id1, poizon_id2, poizon_id3, poizon_id4, poizon_id5, poizon_id6, poizon_id7,");
        sb.append(" poizon_donnee_origine1, poizon_donnee_origine2, poizon_donnee_origine3, poizon_donnee_origine4,");
        sb.append(" poizon_donnee_origine5, poizon_donnee_origine6, poizon_donnee_origine7");
        sb.append(" FROM POIZON ");
        sb.append(" WHERE poizon_id1 = '");
        sb.append(ids[0]);
        sb.append("'");
        for (int i = 1; i < ids.length; i++) {
            if (ids[i] != null && ids[i].trim().length() > 0) {
                sb.append(" AND poizon_id");
                sb.append(i + 1);
                sb.append(" = '");
                sb.append(ids[i]);
                sb.append("'");
            }
        }
        sb.append(" AND t1 > ? ");
        if (withDateOption) {
            sb.append("AND t1 > ? ");
        }
        if (services != null && services.length > 0) {
            sb.append(" AND (");
            sb.append(getAddServicesExpression(services));
            sb.append(")");
        }
        sb.append(" ORDER BY t0 DESC LIMIT ?");

        return sb.toString();

    }

    private String getQueryForRechercheOrthographiqueEtPhonetique(int[] services, String cle, String libelle, String[] donnees, String[] ids) {
        final StringBuilder sb = new StringBuilder();
        final String[] donnees7 = new String[7];
        for (int i = 0; i < 7; i++) {
            donnees7[i] = (i < donnees.length && donnees[i] != null) ? donnees[i] : "";
        }
        sb.append("SELECT note_cle_poizon(");
        sb.append("'");
        sb.append((cle != null) ? cle : "");
        sb.append("'");
        sb.append(", poizon_cle_pq, ");
        sb.append("'");
        sb.append((libelle != null) ? libelle : "");
        sb.append("'");
        sb.append(", poizon_lbl_sans_articles_pq");
        for (int i = 1; i < donnees7.length; i++) {
            sb.append(", ");
            sb.append("'");
            sb.append(donnees7[i]);
            sb.append("'");
            sb.append(", poizon_donnee");
            sb.append(i + 1);
        }
        sb.append(") as note,");
        sb.append(" poizon_service, t0, t1, ");
        sb.append(" poizon_donnee1, poizon_donnee2, poizon_donnee3, poizon_donnee4, poizon_donnee5, poizon_donnee6, poizon_donnee7,");
        sb.append(" poizon_id1, poizon_id2, poizon_id3, poizon_id4, poizon_id5, poizon_id6, poizon_id7,");
        sb.append(" poizon_donnee_origine1, poizon_donnee_origine2, poizon_donnee_origine3, poizon_donnee_origine4,");
        sb.append(" poizon_donnee_origine5, poizon_donnee_origine6, poizon_donnee_origine7");
        sb.append(" FROM POIZON ");
        sb.append(" WHERE t0 < ? AND t1 > ? ");
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != null && ids[i].trim().length() > 0) {
                sb.append(" AND poizon_id");
                sb.append(i + 1);
                sb.append(" = '");
                sb.append(ids[i]);
                sb.append("'");
            }
        }
        if (services != null && services.length > 0) {
            sb.append(" AND (");
            sb.append(getAddServicesExpression(services));
            sb.append(")");
        }
        sb.append(" ORDER BY note DESC LIMIT ?");

        return sb.toString();

    }

    private String getQueryForGeocode(int[] services, int projection) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT st_astext(st_transform(geometrie, ");
        sb.append((projection != 0) ? projection : params.obtientProjectionPardefaut());
        sb.append(")), poizon_service, poizon_referentiel, poizon_donnee1, poizon_id1 ");
        sb.append("FROM POIZON ");
        sb.append("WHERE poizon_donnee1 = ? ");
        sb.append("AND poizon_id1 = ? ");
        sb.append("AND t0 < ? ");
        sb.append("AND t1 > ? ");
        if (services != null && services.length > 0) {
            sb.append("AND (");
            sb.append(getAddServicesExpression(services));
            sb.append(")");
        }

        return sb.toString();

    }

    private String getTransformPointExpression(String[] position, int projection) {
        final StringBuilder sb = new StringBuilder();
        sb.append("st_transform(st_geometryfromtext('POINT(");
        sb.append(position[0]);
        sb.append(" ");
        sb.append(position[1]);
        sb.append(")',");
        sb.append((projection != 0) ? projection : params.obtientProjectionPardefaut());
        sb.append("), ");
        sb.append(params.obtientProjectionSpheroidPardefaut());
        sb.append(")");

        return sb.toString();
    }

    private String getAddServicesExpression(int[] services) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < services.length; i++) {
            sb.append("poizon_service=");
            sb.append(services[i]);
            if (i < services.length - 1) {
                sb.append(" OR ");
            }
        }

        return sb.toString();
    }
}
