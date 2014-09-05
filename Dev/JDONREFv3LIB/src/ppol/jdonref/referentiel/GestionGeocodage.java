/*
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ , ME : GS
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ppol.jdonref.AGestionLogs;
import ppol.jdonref.Algos;
//import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.Tables.GestionTables;
import ppol.jdonref.dao.GeometryDao;
import ppol.jdonref.dao.JDRWktGeometryBean;
import ppol.jdonref.utils.DateUtils;
import ppol.jdonref.utils.GeometryUtils;

/**
 * Contient les méthodes permettant d'effectuer les différentes catégories de géocodage.<br>
 * <li>Les catégories de géocodage (de moins en moins précis):
 * <ul>
 * <li>1 pour à la plaque,</li>
 * <li>2 pour à l'interpolation de la plaque,</li>
 * <li>3 pour à l'interpolation métrique du troncon ou les bornes du troncon (qualité équivalente),</li>
 * <li>4 au centroide du troncon,</li>
 * <li>5 pour à le centroide de la voie.</li>
 * <li>6 à l'arrondissement ou la commune.</li>
 * <li>7 au département.</li>
 * </ul>
 * @author jmoquet
 * 
 * patch de GS sur geocodeInterpolationPointAdresse
 * -erreur NaN : il s'agit d'erreurs dans l'algorithme d'interpolation.
 * -erreur numéro hors des bornes : le point projeté pouvait se retrouver
 * en dehors des troncons de route. En remplaçant la méthode "projete" par
 * "projetePointSurTroncon" , le point projeté est bien sur le tronçon.
 * -erreur numéro hors des bornes (suite à une division par zéro) : erreur 
 * intervenant lorsqu'un numéro de voie (avec indice de répétition) est 
 * encadré par deux numéros de voie identiques
 */
public class GestionGeocodage {

    private JDONREFParams jdonrefParams;
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;

    public GestionGeocodage(JDONREFParams jdonrefParams) {
        this.jdonrefParams = jdonrefParams;
    }

    /**
     * Obtient le vrai centroide du troncon spécifié.
     * @param g
     * @return
     */
    private static Coordinate calculePositionTroncon(double position, GeometryFactory gf, Geometry g, boolean sensnormal) throws
            GestionReferentielException {
        double currentlength = 0, lastlength = 0;

        Coordinate[] cs = g.getCoordinates();
        Coordinate[] tab = new Coordinate[2];

        if (sensnormal) {
            int i = 0;
            for (; i < cs.length - 1; i++) {
                tab[0] = cs[i];
                tab[1] = cs[i + 1];

                LineString line = gf.createLineString(tab);

                currentlength += line.getLength();

                if (lastlength <= position && position <= currentlength) {
                    break;
                }

                lastlength = currentlength;
            }

            if (i == cs.length) {
                throw (new GestionReferentielException("Le milieu du troncon est hors du troncon.",
                        GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }
        } else {
            int i = cs.length - 1;
            for (; i > 0; i--) {
                tab[0] = cs[i];
                tab[1] = cs[i - 1];

                LineString line = gf.createLineString(tab);

                currentlength += line.getLength();

                if (lastlength <= position && position <= currentlength) {
                    break;
                }

                lastlength = currentlength;
            }

            if (i == 0) {
                throw (new GestionReferentielException("Le milieu du troncon est hors du troncon.",
                        GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }
        }

        position -= lastlength; // obtient la distance depuis le début du morceau de troncon.
        currentlength -= lastlength; // optimisation
        double x = tab[0].x;
        double y = tab[0].y;
        x += (position * (tab[1].x - x)) / (currentlength); // simple produit en croix.
        y += (position * (tab[1].y - y)) / (currentlength);

        Coordinate c = new Coordinate(x, y);

        return c;
    }
    private final static String rqProjectionEtReferentiel = "SELECT dpt_referentiel FROM \"dpt_departements\" WHERE dpt_code_departement=? and t0<=? and t1>=? LIMIT 1";

    /**
     * Retourne la projection et le référentiel utilisé pour la construction.
     * @param code_departement
     * @param dt
     * @param connection
     * @return
     * @throws java.sql.SQLException
     */
    private String[] obtientProjectionEtReferentiel(String code_departement, Date dt, int projection, Connection connection) throws SQLException {
        // Récupére la projection et le référentiel utilisé.
        PreparedStatement psChercheDepartement = connection.prepareStatement(rqProjectionEtReferentiel);
        psChercheDepartement.setString(1, code_departement);
        Timestamp ts = null;
        psChercheDepartement.setTimestamp(2, ts = new Timestamp(dt.getTime()));
        psChercheDepartement.setTimestamp(3, ts);
        ResultSet rsChercheDepartement = psChercheDepartement.executeQuery();

        if (!rsChercheDepartement.next()) {
            rsChercheDepartement.close();
            psChercheDepartement.close();
            return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                "0", "10", "Le référentiel ne contient pas le département spécifié, à la date spécifiée (Departement:" + code_departement + " Date:" +
                /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")"
            };
        }
        String projectionStr = (projection != 0) ? String.valueOf(projection) : jdonrefParams.obtientProjectionPardefaut();
        String referentiel = rsChercheDepartement.getString(1);
        rsChercheDepartement.close();
        psChercheDepartement.close();

        return new String[]{
            projectionStr, referentiel
        };
    }

    private String rqGeocodeDepartement(int projection) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(" FROM \"dpt_departements\" WHERE dpt_code_departement=? AND t0<=? AND t1>=? LIMIT 1");

        return sb.toString();
    }

    /**
     * géocode au centroïde d'un département.<br>
     * La géométrie est convertie sous forme WKT avec la méthode astext de postgis.<br>
     * @param code_departement
     * @param dt
     * @param connection
     * @return
     */
    public RetourGeocodage geocodeDepartement(
            String code_departement, Date dt, int projection, Connection connection) throws SQLException {
        RetourGeocodage res = new RetourGeocodage();

        // Récupére la géométrie de la commune.
        PreparedStatement psCherche = connection.prepareStatement(rqGeocodeDepartement(projection));
        psCherche.setString(1, code_departement);
        Timestamp ts = new Timestamp(dt.getTime());
        psCherche.setTimestamp(2, ts);
        psCherche.setTimestamp(3, ts);
        ResultSet rsCherche = psCherche.executeQuery();

        if (!rsCherche.next()) {
            res.message = "Le code departement spécifié n'est pas valide à cette date.";
            res.errorcode = 8;
            rsCherche.close();
            psCherche.close();
            return res;
        }

        // Transforme cette géométrie en objet Geometry.
        String strGeometrie = rsCherche.getString(1);
        rsCherche.close();
        psCherche.close();
        Geometry g = null;

        if (strGeometrie == null) {   // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            res.message = "Erreur de géométrie dans le référentiel (Code Departement:" + code_departement + " Date:" + /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        }

        try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
            g = GeometryUtils.readGeometryFromWKT(strGeometrie);
        } catch (ParseException pe) {   // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            res.message = "Erreur de géométrie dans le référentiel (Code Departement:" + code_departement + " Date:" + /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        } catch (NullPointerException npe) {   // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            res.message = "Erreur de géométrie dans le référentiel (Code Departement:" + code_departement + " Date:" + /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        }

        // Récupére le centroïde.
        Point p = g.getCentroid();

        // Formate et retourne le résultat.
        res.errorcode = 0;
        res.nontrouve = false;
        res.x = p.getX();
        res.y = p.getY();
        
        res.setService(GestionReferentiel.SERVICE_DEPARTEMENT);



        return res;
    }

    private RetourGeocodage geocodePays(String codeSovAc3, Date dt, String defaultProj, int projection, Connection connection) throws SQLException {
        RetourGeocodage res = new RetourGeocodage();
        if (dt == null) {
            dt = new Date();
        }
        final GeometryDao dao = new GeometryDao(jdonrefParams);
        JDRWktGeometryBean wktB = dao.getPaysPtCentralWktGeomFromSovAc3(connection, codeSovAc3, dt, projection);

        if (wktB == null) {
            res.message = "Le code sov_ac3 spécifié n'est pas valide à cette date (Code Sov Ac3:" + codeSovAc3 + " Date:" + DateUtils.formatDateToString(dt, sdformat) + ").";
            res.errorcode = 8;
            return res;
        }

        // Transforme cette géométrie en objet Geometry.
        Point pointCentral = null;
        try {
            pointCentral = GeometryUtils.readPointFromWKT(wktB.getWkt());
        } catch (ParseException pe) {
            res.message = "Erreur de lecture de géométrie dans le référentiel (Code Sov Ac3:" + codeSovAc3 + " Date:" + DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        } catch (NullPointerException npe) {
            res.message = "Erreur de géométrie (null) dans le référentiel (Code Sov Ac3:" + codeSovAc3 + " Date:" + DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        } catch (ClassCastException cce) {
            res.message = "Erreur de type de géométrie dans le référentiel (Code Sov Ac3:" + codeSovAc3 + " Date:" + DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        }

        // Formate et retourne le résultat.
        res.errorcode = 0;
        res.nontrouve = false;
        res.x = pointCentral.getX();
        res.y = pointCentral.getY();
        res.setService(GestionReferentiel.SERVICE_PAYS);
        res.setReferentielText(wktB.getReferentielText());
        res.setProjectionText(wktB.getProjectionSRID());

        return res;
    }

    private String rqGeocodeCommune(int projection) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(" FROM \"com_communes\" WHERE com_code_insee=? AND t0<=? AND t1>=? LIMIT 1");

        return sb.toString();
    }

    /**
     * Géocode au centroïde d'une commune.<br>
     * La géométrie est convertie sous forme WKT avec la méthode astext de postgis.<br>
     * @param code_insee le code insee de la commune.
     * @param dt la date à laquelle géocoder.
     * @param connection la connection utilisée pour acc�der au référentiel.
     * @return
     */
    public RetourGeocodage geocodeCommune(
            String code_insee, Date dt, int projection, Connection connection) throws SQLException {
        RetourGeocodage res = new RetourGeocodage();

        // Récupére la géométrie de la commune.
        PreparedStatement psCherche = connection.prepareStatement(rqGeocodeCommune(projection));
        psCherche.setString(1, code_insee);
        Timestamp ts = new Timestamp(dt.getTime());
        psCherche.setTimestamp(2, ts);
        psCherche.setTimestamp(3, ts);
        ResultSet rsCherche = psCherche.executeQuery();

        if (!rsCherche.next()) {
            res.message = "Le code insee spécifié n'est pas valide à cette date.";
            res.errorcode = 8;
            rsCherche.close();
            psCherche.close();
            return res;
        }

        // Transforme cette géométrie en objet Geometry.
        String strGeometrie = rsCherche.getString(1);
        rsCherche.close();
        psCherche.close();
        Geometry g = null;

        if (strGeometrie == null) {   // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            res.message = "Erreur de géométrie dans le référentiel (Code Insee:" + code_insee + " Date:" + /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        }

        try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
            g = GeometryUtils.readGeometryFromWKT(strGeometrie);
        } catch (ParseException pe) {   // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            res.message = "Erreur de géométrie dans le référentiel (Code Insee:" + code_insee + " Date:" + /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        } catch (NullPointerException npe) {   // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            res.message = "Erreur de géométrie dans le référentiel (Code Insee:" + code_insee + " Date:" + /*sdformat.format(dt)*/ DateUtils.formatDateToString(dt, sdformat) + ")";
            res.errorcode = 10;
            return res;
        }

        // Récupère le centroïde.
        Point p = g.getCentroid();

        // Formate et retourne le résultat.
        res.errorcode = 0;
        res.nontrouve = false;
        res.x = p.getX();
        res.y = p.getY();
        res.setService(GestionReferentiel.SERVICE_COMMUNE);

        return res;
    }

    /**
     * Retourne l'index où placer le numéro.
     */
    private static int indexOf(ArrayList<Integer> numeros, int numero, int min, int max) {
        if (min == max) {
            int n = numeros.get(min).intValue();
            if (n == numero) {
                return numero;
            } else if (n < numero) {
                return -(min + 1) - 1; // le mot peut être inséré au plus en min.
            } else {
                return -(min - 1) - 1; // le mot doit être inséré au moins en min+1.
            }

        } else if (min + 1 == max) {
            int v1 = numeros.get(min).intValue();
            int v2 = numeros.get(max).intValue();

            int index1 = indexOf(numeros, numero, min, min);
            if (v1 == v2) {
                if (-index1 - 1 == min - 1) {
                    return index1;
                }

                return -max - 1;
            }

            int index2 = indexOf(numeros, numero, max, max);
            if (index2 >= 0) {
                return index2;
            }

            // Seuls trois cas peuvent se présenter.
            if (-index1 - 1 == min) {
                return index1;
            }

            if (-index2 - 1 == max + 1) {
                return index2;
            }

            return -(max + 1);
        } else {
            int i;
            int index = (min + max) / 2;
            int n = numeros.get(index).intValue();

            if (n <= numero) {
                return indexOf(numeros, numero, index + 1, max);
            } else {
                return indexOf(numeros, numero, min, index - 1);
            }

        }
    }

    /**
     * Proj�te un point sur un tronçon.<br>
     * Le point est projeté sur le bout de tronçon le plus proche.<br>
     * Si le point projeté est hors du tronçon, l'extrémité la plus proche est choisie à la place.<br>
     * Si le troncon n'est composé d'aucun point, le point est retourné non projeté.<br>
     * Si le troncon n'est composé que d'un point, ce point est retourné.
     */
    private static RetourGeocodage projetePointSurTroncon(GeometryFactory gf, Point point, Geometry geometrie) {
        RetourGeocodage res = new RetourGeocodage();
        // Retourne les coordonnées de l'unique point si c'est le cas.
        Coordinate[] cs = geometrie.getCoordinates();
        if (cs.length == 1) {
            res.errorcode = 0;
            res.nontrouve = false;
            res.x = cs[0].x;
            res.y = cs[0].y;

            return res;
        }

        // Cherche le bout de troncon le plus proche.
        Coordinate[] bout = new Coordinate[2];
        Geometry g = gf.createLineString(bout);
        double mindistance = Double.MAX_VALUE;
        int minindex = -1;
        for (int i = 0; i <
                cs.length - 1; i++) {
            bout[0] = cs[i];
            bout[1] = cs[i + 1];
            double distance = DistanceOp.distance(point, g);
            if (distance < mindistance) {
                mindistance = distance;
                minindex =
                        i;
            }

        }

        // cas particulier
        if (bout[0].x == bout[1].x && bout[0].y == bout[1].y) {
            res.x = bout[0].x;
            res.y = bout[0].y;
            res.errorcode = 0;
            res.nontrouve = false;
            return res;
        }

        // Projete le point sur ce bout de tronçon.
        bout[0] = cs[minindex];
        bout[1] = cs[minindex + 1];
        Coordinate projete = projete(point, geometrie);

        // Vérifie si le projete est inclus dans le bout de troncon,
        // sinon utilise l'extrémité ad�quate.
        if (bout[0].x == bout[1].x) {
            // Comparaison par les y
            if (bout[0].y < bout[1].y) {
                if (projete.y < bout[0].y) {
                    projete = bout[0];
                } else if (bout[1].y < projete.y) {
                    projete = bout[1];
                }

            } else // (bout[0].y>bout[1].y)
            {
                if (projete.y > bout[0].y) {
                    projete = bout[0];
                } else if (bout[1].y > projete.y) {
                    projete = bout[1];
                }

            }
        } else if (bout[0].x < bout[1].x) {
            if (projete.x < bout[0].x) {
                projete = bout[0];
            } else if (bout[1].x < projete.x) {
                projete = bout[1];
            }

        } else // (bout[0].x>bout[1].x)
        {
            if (projete.x > bout[0].x) {
                projete = bout[0];
            } else if (bout[1].x > projete.x) {
                projete = bout[1];
            }

        }

        res.x = projete.x;
        res.y = projete.y;
        res.errorcode = 0;
        res.nontrouve = false;
        res.setService(GestionReferentiel.SERVICE_VOIE);

        return res;
    }

    /**
     * Géocode au centroïde de la voie spécifiée.<br>
     * La géométrie est extraite des tronçons composant la voie à l'aide de la méthode astext() de postgis.<br>
     * Le centroïde de la voie est ici le projeté du centroïde des points constituant la voie sur 
     * le troncon le plus proche de ce ce point. Si ce point est hors de ce troncon, l'extrémité du troncon
     * la plus proche est choisie à la place.<br>
     * @param voi_id L'identifiant de la voie à géocoder.
     * @param date La date à laquelle le géocodage doit être effectuée.
     * @param connection La connection au référentiel.
     * @return
     */
    private RetourGeocodage geocodeCentroideVoie(GeometryFactory gf, String voi_id, Date date, int projection, String tableTroncon,
            Connection connection) throws SQLException, GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();

        // Récupére les géométries des tronçons de la voie.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(" FROM \"");
        sb.append(tableTroncon);
        sb.append("\" WHERE (voi_id_droit=? OR voi_id_gauche=?) AND t0<=? AND t1>=?");
        PreparedStatement psChercheTroncons = connection.prepareStatement(sb.toString());
        psChercheTroncons.setString(1, voi_id);
        psChercheTroncons.setString(2, voi_id);
        Timestamp tsdate = new Timestamp(date.getTime());
        psChercheTroncons.setTimestamp(3, tsdate);
        psChercheTroncons.setTimestamp(4, tsdate);
        ResultSet rsChercheTroncons = psChercheTroncons.executeQuery();

        // Extrait les géométries des tronçons concernés.
        ArrayList<Geometry> geometries = new ArrayList<Geometry>();
        while (rsChercheTroncons.next()) {
            String geometrie = rsChercheTroncons.getString(1);

            if (geometrie == null) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie pour la voie " + voi_id;
                return res;
            }

            try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                geometries.add(GeometryUtils.readGeometryFromWKT(geometrie));
            } catch (ParseException pe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie pour la voie " + voi_id;
                return res;
            } catch (NullPointerException npe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie pour la voie " + voi_id;
                return res;
            }

        }

        rsChercheTroncons.close();
        psChercheTroncons.close();

        // Cas d'erreur.
        if (geometries.size() == 0) {
            res.errorcode = 10;
            res.message = "La voie " + voi_id + " n'est constituée d'aucun tronçon.";
            return res;
        }

        // Cherche le centroïde de la voie.
        Geometry[] geos = new Geometry[geometries.size()];
        for (int i = 0; i <
                geometries.size(); i++) {
            geos[i] = geometries.get(i);
        }

        Geometry gc = gf.createGeometryCollection(geos);
        Point centroid = gc.getCentroid();

        // Cherche le troncon le plus proche du centroide.
        double mindistance = Double.MAX_VALUE;
        int minindex = -1;
        for (int i = 0; i <
                geometries.size(); i++) {
            double distance = DistanceOp.distance(geos[i], centroid);
            if (distance < mindistance) {
                mindistance = distance;
                minindex = i;
            }

        }

        // Projète le centroide sur ce troncon.
        res = projetePointSurTroncon(gf, centroid, geos[minindex]);

        return res;
    }

    /**
     * Retourne le géocodage du numéro spécifié dans la voie spécifiée à partir de points adresse.<br>
     * La géométrie est extraite à l'aide de la méthode astext() de postgis.<br>
     * Si la table adr_adresses_dpt n'est pas trouvée, le géocodage n'est pas trouvé.
     * @param voi_id
     * @param numero
     * @param repetition
     * @param code_departement
     * @param date
     * @param connection
     * @return les coordonnées du point adresse trouvé ou null s'il n'est pas trouvé.
     */
    private RetourGeocodage geocodePointAdresse(String voi_id, int numero, char repetition, String code_departement, Date date, int projection,
            Connection connection) throws SQLException {
        RetourGeocodage res = new RetourGeocodage();

        if (!GestionTables.tableExiste("adr_adresses_" + code_departement, connection)) {
            res.errorcode = 0;
            res.nontrouve = true;
            return res;
        }

        Timestamp tsdate = new Timestamp(date.getTime());

        PreparedStatement psCherchePointAdresse = null;

        // Cherche l'adresse dans les points adresse.
        StringBuilder sb = new StringBuilder();
        if (repetition == 0) {
            sb.append("SELECT astext(st_transform(geometrie,");
            sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
            sb.append("))");
            sb.append(" FROM \"adr_adresses_");
            sb.append(code_departement);
            sb.append("\" WHERE voi_id=? AND adr_numero=? AND adr_rep is null AND t0<=? AND ?<=t1");
            psCherchePointAdresse =
                    connection.prepareStatement(sb.toString());
            psCherchePointAdresse.setString(1, voi_id);
            psCherchePointAdresse.setInt(2, numero);
            psCherchePointAdresse.setTimestamp(3, tsdate);
            psCherchePointAdresse.setTimestamp(4, tsdate);
        } else {
            sb.append("SELECT astext(st_transform(geometrie,");
            sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
            sb.append("))");
            sb.append(" FROM \"adr_adresses_");
            sb.append(code_departement);
            sb.append("\" WHERE voi_id=? AND adr_numero=? AND adr_rep=? AND t0<=? AND ?<=t1");
            psCherchePointAdresse =
                    connection.prepareStatement(sb.toString());
            psCherchePointAdresse.setString(1, voi_id);
            psCherchePointAdresse.setInt(2, numero);
            psCherchePointAdresse.setString(3, Character.toString(repetition));
            psCherchePointAdresse.setTimestamp(4, tsdate);
            psCherchePointAdresse.setTimestamp(5, tsdate);
        }

        ResultSet rsCherchePointAdresse = psCherchePointAdresse.executeQuery();

        // Si un point adresse est trouvé, extrait ses coordonnées.
        if (!rsCherchePointAdresse.next()) {
            res.errorcode = 0;
            res.nontrouve = true;
            return res;
        }

        String strgeometrie = rsCherchePointAdresse.getString(1);
        Geometry g = null;
        if (strgeometrie == null) {
            res.errorcode = 10;
            res.message = "Erreur de géométrie pour la voie " + voi_id + " au numéro " + numero + " et répétition " + Character.toString(
                    repetition);
            return res;
        }

        try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
            g = GeometryUtils.readGeometryFromWKT(strgeometrie);
        } catch (ParseException pe) {
            res.errorcode = 10;
            res.message = "Erreur de géométrie pour la voie " + voi_id + " au numéro " + numero + " et répétition " + Character.toString(
                    repetition);
            return res;
        } catch (NullPointerException npe) {
            res.errorcode = 10;
            res.message = "Erreur de géométrie pour la voie " + voi_id + " au numéro " + numero + " et répétition " + Character.toString(
                    repetition);
            return res;
        }

        Coordinate c = g.getCoordinate();

        // Retourne le résultat.
        res.x = c.x;
        res.y = c.y;
        res.setService(GestionReferentiel.SERVICE_POINT_ADRESSE);

        return res;
    }

    /**
     * Obtient la position relative d'un numéro par rapport à un troncon.<br>
     * <ul>
     * <li>-1 si le numero est en dehors du troncon.</li>
     * <li>0 si le numero est strictement inclus dans le tronçon.</li>
     * <li>1 si le numero est le départ du tronçon.</li>
     * <li>2 si le numéro est la fin du tronçon.</li>
     * </ul>
     */
    private static int obtientPositionRelative(int numero, char repetition, int numero_debut, int numero_fin, char repetition_debut,
            char repetition_fin) {
        boolean sensnormal = numero_debut <= numero_fin;
        if (sensnormal) {
            if (numero < numero_debut || numero_fin < numero) {
                return -1;
            }

        } else if (numero < numero_fin || numero_debut < numero) {
            return -1;
        }

        if (sensnormal) {
            if (numero_debut < numero && numero < numero_fin) {
                return 0;
            }

        } else if (numero_fin < numero && numero < numero_debut) {
            return 0;
        }

        if (numero == numero_debut) {
            if (repetition == repetition_debut) {
                if (sensnormal) {
                    return 1;
                }

                return 2;
            }

            if (sensnormal) {
                if (repetition < repetition_debut) {
                    return -1;
                }

            } else if (repetition > repetition_debut) {
                return -1;
            }
        // A ce point l'adresse se situe après la borne de début.
        // les tests suivant vont permettre de les déterminer.
        }

        if (numero == numero_fin) {
            if (repetition == repetition_fin) {
                if (sensnormal) {
                    return 2;
                }

                return 1;
            }

            if (sensnormal) {
                if (repetition > repetition_fin) {
                    return -1;
                }

            } else if (repetition < repetition_fin) {
                return -1;
            }
        // A ce point l'adresse se situe avant la borne de fin.
        }

        // Ici, l'adresse se situe après la borne de début, et avant la borne de fin,
        // elle fait donc partie du tronçon.
        return 0;
    }

    /**
     * Retourne le résultat d'une interpolation métrique au troncon<br>
     * @param length L'abscisse longiligne de l'adresse
     * @param currentlength L'absisse longiligne du début du troncon
     * @param geometri la géométrie du troncon
     * @param numero_debut le numero de début du troncon
     * @param numero_fin le numero de fin du troncon
     */
    private static RetourGeocodage geocodeInterpolationMetriqueTroncon(double length, double currentlength, Geometry geometrie,
            int numero_debut, int numero_fin) throws GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();
        // Cherche le bout de troncon auquel appartient le numéro d'après sa distance.
        Coordinate[] coordinates = geometrie.getCoordinates();

        double lastlength = currentlength;
        int i;

        if (numero_debut <= numero_fin) {
            for (i = 0; i < coordinates.length - 1; i++) {
                currentlength += coordinates[i].distance(coordinates[i + 1]);
                if (lastlength <= length && length <= currentlength) {
                    break;
                }

                lastlength = currentlength;
            }

            if (i == coordinates.length - 1) {
                // Ne devrait pas arriver, doit l'exception.
                throw (new GestionReferentielException("Le numero est hors des bornes.", GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }

        } else {
            for (i = coordinates.length - 1; i >
                    0; i--) {
                currentlength += coordinates[i].distance(coordinates[i - 1]);
                if (lastlength <= length && length <= currentlength) {
                    break;
                }

                lastlength = currentlength;
            }

            if (i == 0) {
                throw (new GestionReferentielException("Le numero est hors des bornes.", GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }

        }

        // Le place sur ce bout de troncon : entre lastlength et currentlength.
        Coordinate start = null, end = null;
        if (numero_debut <= numero_fin) {
            start = coordinates[i];
            end = coordinates[i + 1];
        } else {
            start = coordinates[i];
            end = coordinates[i - 1];
        }

        double sublength = currentlength - lastlength; // la longueur du bout de troncon où est présent le numéro.
        length -= lastlength;                        // la position du numéro sur ce bout de troncon.

        res.errorcode = 0;
        res.x = start.x + (length * (end.x - start.x)) / sublength; // simple produit en croix.
        res.y = start.y + (length * (end.y - start.y)) / sublength;
        res.setService(GestionReferentiel.SERVICE_TRONCON);

        return res;
    }

    private static RetourGeocodage geocodeInterpolationMetriqueTroncon(double length, double currentlength, Geometry geometrie,
            int numero_debut, int numero_fin, double distance, int coteDecalage) throws GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();
        // Cherche le bout de troncon auquel appartient le numéro d'après sa distance.
        Coordinate[] coordinates = geometrie.getCoordinates();

        double lastlength = currentlength;
        int i;

        if (numero_debut <= numero_fin) {
            for (i = 0; i < coordinates.length - 1; i++) {
                currentlength += coordinates[i].distance(coordinates[i + 1]);
                if (lastlength <= length && length <= currentlength) {
                    break;
                }

                lastlength = currentlength;
            }

            if (i == coordinates.length - 1) {
                // Ne devrait pas arriver, doit l'exception.
                throw (new GestionReferentielException("Le numero est hors des bornes.", GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }

        } else {
            for (i = coordinates.length - 1; i > 0; i--) {
                currentlength += coordinates[i].distance(coordinates[i - 1]);
                if (lastlength <= length && length <= currentlength) {
                    break;
                }

                lastlength = currentlength;
            }

            if (i == 0) {
                throw (new GestionReferentielException("Le numero est hors des bornes.", GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }

        }

        // Le place sur ce bout de troncon : entre lastlength et currentlength.
        Coordinate start = null, end = null;
        if (numero_debut <= numero_fin) {
            start = coordinates[i];
            end = coordinates[i + 1];
        } else {
            start = coordinates[i];
            end = coordinates[i - 1];
        }

        double sublength = currentlength - lastlength; // la longueur du bout de troncon où est présent le numéro.
        length -= lastlength;                          // la position du numéro sur ce bout de troncon.

        res.errorcode = 0;

        final double x = start.x + (length * (end.x - start.x)) / sublength; // simple produit en croix.
        final double y = start.y + (length * (end.y - start.y)) / sublength;

        final Coordinate pointProjete = projettePoint(start, end, x, y, distance, coteDecalage);
        res.x = pointProjete.x;
        res.y = pointProjete.y;

        res.setService(GestionReferentiel.SERVICE_TRONCON);

        return res;
    }

    /**
     * Retourne le résultat d'une interpolation métrique au troncon.<br>
     * Si les numéros de début et de fin sont inversés, le numéro est positionné à partir de la fin.<br>
     * Si les numéros de début et de fin sont identiques, l'interpolation le place au milieu du troncon.
     * @param numero le numéro où se positionner.
     * @param geometrie la géometrie du troncon.
     * @param numero_debut le numéro de début du troncon.
     * @param numero_fin le numéro de fin du troncon.
     * @return
     */
    private static RetourGeocodage geocodeInterpolationMetriqueTroncon(int numero, Geometry geometrie, int numero_debut, int numero_fin)
            throws GestionReferentielException {
        // Trouve la longeur du troncon.
        double totallength = geometrie.getLength();

        // Interpole la distance du numéro par rapport au début du troncon.
        double length = 0;
        if (numero_debut <= numero_fin) {
            if (numero < numero_debut || numero_fin < numero) {
                throw (new GestionReferentielException("Le numéro est hors bornes.", GestionReferentielException.PARAMETREERRONNE, 7));
            }

            if (numero_fin == numero_debut) {
                length = totallength / 2;
            } else if (numero == numero_debut) {
                length = 0;
            } else if (numero == numero_fin) {
                length = totallength;
            } else {
                length = ((numero - numero_debut) * totallength) / (numero_fin - numero_debut);
            }

        } else {
            if (numero < numero_fin || numero_debut < numero) {
                throw (new GestionReferentielException("Le numéro est hors bornes.", GestionReferentielException.PARAMETREERRONNE, 7));
            }

            if (numero_fin == numero_debut) {
                length = totallength / 2;
            } else if (numero == numero_debut) {
                length = totallength;
            } else if (numero == numero_fin) {
                length = 0;
            } else {
                length = ((numero - numero_fin) * totallength) / (numero_debut - numero_fin);
            }

        }

        return geocodeInterpolationMetriqueTroncon(length, 0, geometrie, numero_debut, numero_fin);
    }

    /**
     * Retourne le résultat d'une interpolation métrique au troncon.<br>
     * Si les numéros de début et de fin sont inversés, le numéro est positionné à partir de la fin.<br>
     * Si les numéros de début et de fin sont identiques, l'interpolation le place au milieu du troncon.
     * @param numero le numéro où se positionner.
     * @param geometrie la géometrie du troncon.
     * @param numero_debut le numéro de début du troncon.
     * @param numero_fin le numéro de fin du troncon.
     * @return
     */
    private static RetourGeocodage geocodeInterpolationMetriqueTroncon(int numero, Geometry geometrie, int numero_debut, int numero_fin, double distance, boolean isCoteDroit)
            throws GestionReferentielException {
        // Trouve la longeur du troncon.
        double totallength = geometrie.getLength();

        // Interpole la distance du numéro par rapport au début du troncon.
        double length = 0;
        if (numero_debut <= numero_fin) {
            if (numero < numero_debut || numero_fin < numero) {
                throw (new GestionReferentielException("Le numéro est hors bornes.", GestionReferentielException.PARAMETREERRONNE, 7));
            }

            if (numero_fin == numero_debut) {
                length = totallength / 2;
            } else if (numero == numero_debut) {
                length = 0;
            } else if (numero == numero_fin) {
                length = totallength;
            } else {
                length = ((numero - numero_debut) * totallength) / (numero_fin - numero_debut);
            }

        } else {
            if (numero < numero_fin || numero_debut < numero) {
                throw (new GestionReferentielException("Le numéro est hors bornes.", GestionReferentielException.PARAMETREERRONNE, 7));
            }

            if (numero_fin == numero_debut) {
                length = totallength / 2;
            } else if (numero == numero_debut) {
                length = totallength;
            } else if (numero == numero_fin) {
                length = 0;
            } else {
                length = ((numero - numero_fin) * totallength) / (numero_debut - numero_fin);
            }

        }

        int coteDecalage = determineCoteDecalage(numero, numero_debut, numero_fin, isCoteDroit);

        return geocodeInterpolationMetriqueTroncon(length, 0, geometrie, numero_debut, numero_fin, distance, coteDecalage);
    }

    /**
     * Retourne si le numero est placé sur le bon coté d'un troncon.<br>
     * Deux cas peuvent être distingués:
     * <ul>
     * <li>Si le type d'adressage est Classique, le numero doit respecter la parité des bornes.</li>
     * <li>Si le type d'adressage est Métrique, et que le tronçon semble respecter la parité, le numéro doit
     * respecter la parité des bornes.</li>
     * <li>Sinon, le coté peut correspondre dans tous les cas, puisque la parité n'est pas respectée.</li>
     * </ul>
     * N.B.: Le type d'adressage n'a pas besoin de respecter la casse.
     * @param numero Le numero a tester.
     * @param numero_debut La borne de début du troncon.
     * @param numero_fin La borne de fin du troncon.
     * @param typadr Le type d'adressage du troncon : Classique, Metrique, ...
     * @return
     */
    private static boolean coteCorrespond(int numero, int numero_debut, int numero_fin, String typadr) {
        typadr = typadr.toLowerCase();
        typadr = Algos.supprimeAccents(typadr);

        if (typadr.compareTo("classique") == 0) {
            int parite_debut = numero_debut % 2;
            int parite_numero = numero % 2;

            if (parite_debut == parite_numero) {
                return true;
            }

            return false;
        } else if (typadr.compareTo("metrique") == 0) {
            int parite_debut = numero_debut % 2;
            int parite_fin = numero_fin % 2;
            int parite_numero = numero % 2;

            if (parite_debut == parite_fin) {
                return parite_numero == parite_debut;
            }

            return true;
        }

        return true;
    }

    /**
     * Cherche les cot�s de troncons qui contiennent le numéro spécifié.<br>
     * @param voi_id
     * @param numero
     * @param date
     * @param code_departement
     * @param table
     * @param connection
     * @return
     */
    private ArrayList<CoteDeTroncon> chercheCoteDeTroncons(String voi_id, Date date, int projection, String tableTroncons, Connection connection)
            throws SQLException {
        ArrayList<CoteDeTroncon> res = new ArrayList<CoteDeTroncon>();

        // Les coté sont traités séparemment pour l'accés à la BD.
        // Cela évite des tests trop gourmands dans la requête.

        // Extrait les tronçons à droite qui pourraient correspondre au numéro.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(",tro_numero_debut_droit");
        sb.append(" FROM \"");
        sb.append(tableTroncons);
        sb.append("\" ");
        sb.append("WHERE voi_id_droit=? AND ");
        sb.append("t0<=? AND ?<=t1");
        PreparedStatement psChercheTroncons = connection.prepareStatement(sb.toString());
        psChercheTroncons.setString(1, voi_id);
        Timestamp tsdate = null;
        psChercheTroncons.setTimestamp(2, tsdate = new Timestamp(date.getTime()));
        psChercheTroncons.setTimestamp(3, tsdate);
        ResultSet rs = psChercheTroncons.executeQuery();
        while (rs.next()) {
            CoteDeTroncon t = new CoteDeTroncon();
            t.geometrie = rs.getString(1);
            t.numero_debut = rs.getInt(2);
            t.isCoteDroit = true;
            res.add(t);
        }

        rs.close();
        psChercheTroncons.close();

        // Extrait les tronçons à gauche qui pourraient correspondre au numéro.
        sb.setLength(0);
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(",tro_numero_debut_gauche");
        sb.append(" FROM \"");
        sb.append(tableTroncons);
        sb.append("\" ");
        sb.append("WHERE voi_id_gauche=? AND ");
        sb.append("t0<=? AND ?<=t1");
        psChercheTroncons =
                connection.prepareStatement(sb.toString());
        psChercheTroncons.setString(1, voi_id);
        psChercheTroncons.setTimestamp(2, tsdate);
        psChercheTroncons.setTimestamp(3, tsdate);
        rs =
                psChercheTroncons.executeQuery();
        while (rs.next()) {
            CoteDeTroncon t = new CoteDeTroncon();
            t.geometrie = rs.getString(1);
            t.numero_debut = rs.getInt(2);
            t.isCoteDroit = false;
            res.add(t);
        }

        rs.close();
        psChercheTroncons.close();

        return res;
    }

    /**
     * Cherche les cot�s de troncons qui contiennent le numéro spécifié.<br>
     * @param voi_id
     * @param numero
     * @param date
     * @param code_departement
     * @param table
     * @param connection
     * @return
     */
    private ArrayList<CoteDeTroncon> chercheCoteDeTroncons(String voi_id, int numero, Date date, String code_departement, int projection,
            String tableTroncons, Connection connection) throws SQLException {
        ArrayList<CoteDeTroncon> res = new ArrayList<CoteDeTroncon>();

        // Les coté sont traités séparemment pour l'accés à la BD.
        // Cela évite des tests trop gourmands dans la requete.

        // Extrait les tronçons à droite qui pourraient correspondre au numéro.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(",tro_typ_adr,");
        sb.append("tro_numero_debut_droit,tro_numero_fin_droit,tro_rep_debut_droit,tro_rep_fin_droit");
        sb.append(" FROM \"");
        sb.append(tableTroncons);
        sb.append("\" ");
        sb.append("WHERE voi_id_droit=? AND ");
        sb.append("((tro_numero_debut_droit<=? AND ?<=tro_numero_fin_droit) OR ");
        sb.append("(tro_numero_debut_droit>=? AND ?>=tro_numero_fin_droit)) AND ");
        sb.append("t0<=? AND ?<=t1");
        PreparedStatement psChercheTroncons = connection.prepareStatement(sb.toString());
        psChercheTroncons.setString(1, voi_id);
        psChercheTroncons.setInt(2, numero);
        psChercheTroncons.setInt(3, numero);
        psChercheTroncons.setInt(4, numero);
        psChercheTroncons.setInt(5, numero);
        Timestamp tsdate = null;
        psChercheTroncons.setTimestamp(6, tsdate = new Timestamp(date.getTime()));
        psChercheTroncons.setTimestamp(7, tsdate);
        ResultSet rs = psChercheTroncons.executeQuery();
        while (rs.next()) {
            CoteDeTroncon t = new CoteDeTroncon();
            t.geometrie = rs.getString(1);
            t.typadr = rs.getString(2);
            t.numero_debut = rs.getInt(3);
            t.numero_fin = rs.getInt(4);
            String strrepetition = rs.getString(5);
            if (strrepetition != null) {
                t.repetition_debut = strrepetition.charAt(0);
            } else {
                t.repetition_debut = 0;
            }

            strrepetition = rs.getString(6);
            if (rs.getString(6) != null) {
                t.repetition_fin = strrepetition.charAt(0);
            } else {
                t.repetition_fin = 0;
            }
            t.isCoteDroit = true;
            res.add(t);
        }

        rs.close();
        psChercheTroncons.close();

        // Extrait les tronçons à gauche qui pourraient correspondre au numéro.
        sb.setLength(0);
        sb.append("SELECT astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(",tro_typ_adr,");
        sb.append("tro_numero_debut_gauche,tro_numero_fin_gauche,tro_rep_debut_gauche,tro_rep_fin_gauche");
        sb.append(" FROM \"");
        sb.append(tableTroncons);
        sb.append("\" ");
        sb.append("WHERE voi_id_gauche=? AND ");
        sb.append("((tro_numero_debut_gauche<=? AND ?<=tro_numero_fin_gauche) OR ");
        sb.append("(tro_numero_debut_gauche>=? AND ?>=tro_numero_fin_gauche)) AND ");
        sb.append("t0<=? AND ?<=t1");
        psChercheTroncons =
                connection.prepareStatement(sb.toString());
        psChercheTroncons.setString(1, voi_id);
        psChercheTroncons.setInt(2, numero);
        psChercheTroncons.setInt(3, numero);
        psChercheTroncons.setInt(4, numero);
        psChercheTroncons.setInt(5, numero);
        psChercheTroncons.setTimestamp(6, tsdate);
        psChercheTroncons.setTimestamp(7, tsdate);
        rs = psChercheTroncons.executeQuery();
        while (rs.next()) {
            CoteDeTroncon t = new CoteDeTroncon();
            t.geometrie = rs.getString(1);
            t.typadr = rs.getString(2);
            t.numero_debut = rs.getInt(3);
            t.numero_fin = rs.getInt(4);
            String strrepetition = rs.getString(5);
            if (strrepetition == null) {
                t.repetition_debut = 0;
            } else {
                t.repetition_debut = strrepetition.charAt(0);
            }

            strrepetition = rs.getString(6);
            if (strrepetition == null) {
                t.repetition_fin = 0;
            } else {
                t.repetition_fin = strrepetition.charAt(0);
            }
            t.isCoteDroit = false;
            res.add(t);
        }

        rs.close();
        psChercheTroncons.close();

        return res;
    }

    /**
     * Filtre les tronçons pour ne garder que ceux qui contiennent le numéro
     * et ceux du bon coté.
     * @param troncons
     * @return la position relative du numéro sur les tronçons trouvés
     * <ul>
     * <li>-1 si le numero est en dehors du troncon.</li>
     * <li>0 si le numero est strictement inclus dans le tronçon.</li>
     * <li>1 si le numero est le départ du tronçon.</li>
     * <li>2 si le numéro est la fin du tronçon.</li>
     * </ul>
     */
    private static ArrayList<Integer> filtreTroncons(int numero, char repetition, ArrayList<CoteDeTroncon> troncons) {
        ArrayList<Integer> positions = new ArrayList<Integer>();
        for (int i = 0; i <
                troncons.size(); i++) {
            CoteDeTroncon t = troncons.get(i);
            int temp_numero_debut = t.numero_debut;
            int temp_numero_fin = t.numero_fin;

            // Obtient la position relative au tronçon.
            int position = obtientPositionRelative(numero, repetition,
                    temp_numero_debut, temp_numero_fin,
                    t.repetition_debut, t.repetition_fin);

            // Vérifie si le bon coté du tronçon est choisi.
            boolean correspond = coteCorrespond(numero, temp_numero_debut, temp_numero_fin, t.typadr);

            if (position == -1 || !correspond) {
                troncons.remove(i);
                i--;

            } else {
                positions.add(new Integer(position));
            }

        }
        return positions;
    }

    /**
     * géocode un numéro alors que plusieurs tronçons revendiquent son appartenance.<br>
     * Le géocodage n'est alors possible que si le point est situé aux extrémités de ces tronçons.<br>
     * Un résultat n'est alors renvoyé que si les points trouvés sur chaque tronçon sont les même.<br>
     * Sinon, une erreur 8 est renvoyée.
     * @return
     */
    private static RetourGeocodage geocodeMultipleTroncons(String voi_id, int numero, char repetition, ArrayList<CoteDeTroncon> troncons,
            ArrayList<Integer> positions) throws GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();
        if (troncons.size() < 2) {
            throw new GestionReferentielException("La méthode geocodeMultipleTroncons doit être utilisée avec plusieurs tronçons.",
                    GestionReferentielException.PARAMETREERRONNE, 5);
        }

        double[] pttemp = null;
        Coordinate[] cs = null;
        double[] pt = null;
        Coordinate c = null;
        // Il est possible que plusieurs tronçons correspondent,
        // si toutes les positions relatives sont aux extrémités,
        // et que tous les points trouvés sont identiques.
        for (int i = 0; i < troncons.size(); i++) {
            Geometry g = null;
            int position = positions.get(i).intValue();
            if (position == 0) // Erreur : plusieurs positions serait disponibles.
            {
                res.errorcode = 8;
                res.message = "Plusieurs adresses disponibles pour " + numero + " " + repetition + " dans " + voi_id;
                return res;
            }

            try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                g = GeometryUtils.readGeometryFromWKT(troncons.get(i).geometrie);
            } catch (ParseException pe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id;
                return res;
            } catch (NullPointerException npe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id;
                return res;
            }

            cs = g.getCoordinates();

            if (position == 1) {
                c = cs[cs.length - 1];
            } else if (position == 2) {
                c = cs[0];
            } else {
                // S'il ne s'agit pas d'une extrémité, il y a erreur.
                res.errorcode = 8;
                res.message = "Plusieurs adresses disponibles pour " + numero + " " + repetition + " dans " + voi_id;
                return res;
            }

            pttemp = new double[]{
                c.x, c.y
            };

            if (i == 0) {
                pt = pttemp;
            } else {
                if (pttemp[0] != pt[0] || pttemp[1] != pt[1]) {
                    res.errorcode = 8;
                    res.message = "Plusieurs adresses disponibles pour " + numero + " " + repetition + " dans " + voi_id;
                    return res;
                }

            }
        }
        res.errorcode = 0;
        res.x = pt[0];
        res.y = pt[1];
        res.setService(GestionReferentiel.SERVICE_TRONCON);

        return res;
    }

    /**
     * géocode un numéro alors que plusieurs tronçons revendiquent son appartenance.<br>
     * Le géocodage n'est alors possible que si le point est situé aux extrémités de ces tronçons.<br>
     * Un résultat n'est alors renvoyé que si les points trouvés sur chaque tronçon sont les même.<br>
     * Sinon, une erreur 8 est renvoyée.
     * @return
     */
    private static RetourGeocodage geocodeMultipleTroncons(String voi_id, int numero, double distance, char repetition, ArrayList<CoteDeTroncon> troncons,
            ArrayList<Integer> positions) throws GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();
        if (troncons.size() < 2) {
            throw new GestionReferentielException("La méthode geocodeMultipleTroncons doit être utilisée avec plusieurs tronçons.",
                    GestionReferentielException.PARAMETREERRONNE, 5);
        }

        double[] pttemp = null;
        Coordinate[] cs = null;
        double[] pt = null;
        Coordinate c = null;
        // Il est possible que plusieurs tronçons correspondent,
        // si toutes les positions relatives sont aux extrémités,
        // et que tous les points trouvés sont identiques.
        for (int i = 0; i < troncons.size(); i++) {
            Geometry g = null;
            int position = positions.get(i).intValue();
            if (position == 0) // Erreur : plusieurs positions serait disponibles.
            {
                res.errorcode = 8;
                res.message = "Plusieurs adresses disponibles pour " + numero + " " + repetition + " dans " + voi_id;
                return res;
            }

            try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                g = GeometryUtils.readGeometryFromWKT(troncons.get(i).geometrie);
            } catch (ParseException pe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id;
                return res;
            } catch (NullPointerException npe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id;
                return res;
            }

            cs = g.getCoordinates();
            if (position == 1) {
                c = cs[cs.length - 1];
                if (cs.length > 1) {
                    int coteDecalage = determineCoteDecalage(numero, troncons.get(i).numero_debut, troncons.get(i).numero_fin, troncons.get(i).isCoteDroit);
                    Coordinate start;
                    Coordinate end;
                    if (troncons.get(i).numero_debut <= troncons.get(i).numero_fin) { // sens de la géométrie
                        start = cs[cs.length - 2];
                        end = c;
                    } else {// sens inverse
                        start = c;
                        end = cs[cs.length - 2];
                    }
                    final Coordinate pointProjete = projettePoint(start, end, c.x, c.y, distance, coteDecalage);
                    c.x = pointProjete.x;
                    c.y = pointProjete.y;
                }
            } else if (position == 2) {
                c = cs[0];
                if (cs.length > 1) {
                    int coteDecalage = determineCoteDecalage(numero, troncons.get(i).numero_debut, troncons.get(i).numero_fin, troncons.get(i).isCoteDroit);
                    Coordinate start;
                    Coordinate end;
                    if (troncons.get(i).numero_debut <= troncons.get(i).numero_fin) { // sens de la géométrie
                        start = c;
                        end = cs[1];
                    } else { // sens inverse
                        start = cs[1];
                        end = c;
                    }
                    final Coordinate pointProjete = projettePoint(start, end, c.x, c.y, distance, coteDecalage);
                    c.x = pointProjete.x;
                    c.y = pointProjete.y;
                }
            } else {
                // S'il ne s'agit pas d'une extrémité, il y a erreur.
                res.errorcode = 8;
                res.message = "Plusieurs adresses disponibles pour " + numero + " " + repetition + " dans " + voi_id;
                return res;
            }

            pttemp = new double[]{
                c.x, c.y
            };

            if (i == 0) {
                pt = pttemp;
            } else {
                if (pttemp[0] != pt[0] || pttemp[1] != pt[1]) {
                    res.errorcode = 8;
                    res.message = "Plusieurs adresses disponibles pour " + numero + " " + repetition + " dans " + voi_id;
                    return res;
                }

            }
        }
        res.errorcode = 0;


        res.x = pt[0];
        res.y = pt[1];
        res.setService(GestionReferentiel.SERVICE_TRONCON);

        return res;
    }

    /**
     * géocode au centroide du troncon spécifié.
     * @param sensnormal indique si le sens du troncon est le sens normal.
     * @return
     */
    private static RetourGeocodage geocodeCentroideTroncon(GeometryFactory gf, Geometry g, boolean sensnormal) throws
            GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();

        Coordinate cs = calculePositionTroncon(g.getLength() / 2, gf, g, sensnormal);

        res.errorcode = 0;
        res.nontrouve = false;
        res.x = cs.x;
        res.y = cs.y;

        res.setService(GestionReferentiel.SERVICE_TRONCON);

        return res;
    }

    /**
     * Projete le point sur le bout de troncon spécifié, qui doit n'être constitué que d'une ligne.
     */
    public static Coordinate projete(
            Geometry point, Geometry boutdetroncon) {
        Coordinate[] cs = boutdetroncon.getCoordinates();

        // Si le segment est vertical
        if (cs[0].x == cs[1].x) {
            return new Coordinate(cs[0].x, point.getCoordinate().y);
        } else {
            // Sinon,
            // D(x0,y0)
            // D1 : y=a.x+b
            // Gx = (x0+a.(y0-b))/(1+a�)
            // Gy = b + a.(x0+a.(y0-b))/(1+a�)

            // transform� en 
            // A2 = 1+a�
            // C = x0+a.(y0-b)
            // Gx = C/A2
            // Gy = b + aC/A2

            double a = (cs[0].y - cs[1].y) / (cs[0].x - cs[1].x);
            double b = cs[0].y - a * cs[0].x;

            double A2 = 1 + a * a;
            double C = point.getCoordinate().x + a * (point.getCoordinate().y - b);

            double x = C / A2;
            double y = b + (a * C) / A2;

            return new Coordinate(x, y);
        }

    }

    /**
     * géocode à l'interpolation des points adresses trouvés autour du numéro.<br>
     * Cette interpolation n'est effectuée que si les points adresses si situent sur un même morceau de troncon.<br>
     * Si la table d'adresses n'existe pas, aucun résultat n'est trouvé.
     * 
     * Patché par GS.
     * 
     * @return
     */
    private RetourGeocodage geocodeInterpolationPointAdresse(GeometryFactory gf, String voi_id, int numero, char repetition,
            CoteDeTroncon troncon, String code_departement,
            Date date, int projection, Connection connection) throws GestionReferentielException, SQLException {
        RetourGeocodage res = new RetourGeocodage();

        if (!GestionTables.tableExiste("adr_adresses_" + code_departement, connection)) {
            res.nontrouve = true;
            return res;
        }

        // Trouve tous les points adresse du troncon concerné
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT adr_numero,adr_rep,astext(st_transform(geometrie,");
        sb.append((projection != 0) ? projection : jdonrefParams.obtientProjectionPardefaut());
        sb.append("))");
        sb.append(" FROM \"adr_adresses_");
        sb.append(code_departement);
        sb.append("\" WHERE voi_id=? AND t0<=? AND ?<=t1 AND ((?<=adr_numero AND adr_numero<=?) OR (?>=adr_numero AND adr_numero>=?))");
        PreparedStatement psChercheAdresse = connection.prepareStatement(sb.toString());
        psChercheAdresse.setString(1, voi_id);
        Timestamp tsdate = null;
        psChercheAdresse.setTimestamp(2, tsdate = new Timestamp(date.getTime()));
        psChercheAdresse.setTimestamp(3, tsdate);
        psChercheAdresse.setInt(4, troncon.numero_debut);
        psChercheAdresse.setInt(5, troncon.numero_fin);
        psChercheAdresse.setInt(6, troncon.numero_debut);
        psChercheAdresse.setInt(7, troncon.numero_fin);
        ResultSet rsChercheAdresse = psChercheAdresse.executeQuery();

        int minnumero = -1, maxnumero = Integer.MAX_VALUE;
        String mingeometrie = null, maxgeometrie = null;
        char minrepetition = 0, maxrepetition = 0;
        // Trouve les points adresse les plus proches du numéro, et du bon coté
        while (rsChercheAdresse.next()) {
            int currentnumero = rsChercheAdresse.getInt(1);
            String strcurrentrepetition = rsChercheAdresse.getString(2);
            char currentrepetition;
            if (strcurrentrepetition == null) {
                currentrepetition = 0;
            } else {
                currentrepetition = strcurrentrepetition.charAt(0);
            }

            // Vérifie le coté de ce numéro.
            if (!coteCorrespond(currentnumero, troncon.numero_debut, troncon.numero_fin, troncon.typadr)) {
                continue;
            }

            // Cherche le plus proche avant
            if (currentnumero < numero || (currentnumero == numero && currentrepetition < repetition)) {
                if (currentnumero > minnumero || currentnumero == minnumero && currentrepetition > minrepetition) {
                    minnumero = currentnumero;
                    minrepetition =
                            currentrepetition;
                    mingeometrie =
                            rsChercheAdresse.getString(3);
                }

            } else // et le plus proche après.
            if (currentnumero > numero || (currentnumero == numero && currentrepetition > repetition)) {
                if (currentnumero < maxnumero || currentnumero == maxnumero && currentrepetition < maxrepetition) {
                    maxnumero = currentnumero;
                    maxrepetition =
                            currentrepetition;
                    maxgeometrie =
                            rsChercheAdresse.getString(3);
                }

            }
        }

        rsChercheAdresse.close();
        psChercheAdresse.close();

        // Si aucun numéro trouvé,
        if (minnumero == -1 && maxnumero == Integer.MAX_VALUE) {
            res.nontrouve = true;
            return res;
        }

        // Sinon, trouve les portions de troncon qui correspondent (sauf si aucun point adresse n'a été trouvé).
        Geometry g = null;
        Geometry ptmin = null;
        Geometry ptmax = null;
        if (troncon.geometrie == null) {
            res.errorcode = 10;
            res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                    " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat);  // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            return res;
        }

        try {
            // Extrait les géométries.  // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
            g = GeometryUtils.readGeometryFromWKT(troncon.geometrie);
            if (minnumero != -1) {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                ptmin = GeometryUtils.readGeometryFromWKT(mingeometrie);
            }

            if (maxnumero != Integer.MAX_VALUE) {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                ptmax = GeometryUtils.readGeometryFromWKT(maxgeometrie);
            }

        } catch (ParseException pe) {
            res.errorcode = 10;
            res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                    " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat); // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            return res;
        } catch (NullPointerException npe) {
            res.errorcode = 10;
            res.message = "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                    " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat);  // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
            return res;
        }
        //GS Si un seul point adresse est trouvé, on attribue au point cherché les coordonnées du point adresse
        //de numéro le plus proche trouvé  (on aurait aussi pu prendre l'extrémité du tronçon)
        if (minnumero == -1 || maxnumero == Integer.MAX_VALUE) {
            if (minnumero > -1) {
                res.x = ptmin.getCoordinate().x;
                res.y = ptmin.getCoordinate().y;
            } else {
                res.x = ptmax.getCoordinate().x;
                res.y = ptmax.getCoordinate().y;
            }

            res.errorcode = 0;
            res.setService(GestionReferentiel.SERVICE_POINT_ADRESSE);

            return res;
        }

        Coordinate[] cs = g.getCoordinates();
        Coordinate[] tab = null;

        double distancemin = Double.MAX_VALUE, distancemax = Double.MAX_VALUE; // la distance entre les points adresse et le bout de troncon analysé

        Geometry gtronconmin = null, gtronconmax = null; // la géométrie des bouts de troncon contenant les points adresse.

        double abscissemin = 0, abscissemax = 0; // l'abscisse longiligne des bouts de troncon contenant les points adresse.

        int boutmin = 0, boutmax = cs.length - 2; // les index des bouts de troncons contenant les points adresse.

        double currentlength = 0; // l'abscisse longiligne du bout de troncon analysé.

        // les troncons les plus proches des points adresses sont conservés.
        if (troncon.numero_debut <= troncon.numero_fin) {
            for (int i = 0; i <=
                    cs.length - 2; i++) {
                tab = new Coordinate[2];
                tab[0] = cs[i];
                tab[1] = cs[i + 1];
                Geometry boutdetroncon = gf.createLineString(tab); // La géométrie de la portion de troncon.

                if (minnumero != -1) {
                    double tempdistancemin = DistanceOp.distance(boutdetroncon, ptmin);
                    if (tempdistancemin < distancemin) {
                        distancemin = tempdistancemin;
                        gtronconmin =
                                boutdetroncon;
                        boutmin =
                                i;
                        abscissemin =
                                currentlength;
                    }

                }
                if (maxnumero != Integer.MAX_VALUE) {
                    double tempdistancemax = DistanceOp.distance(boutdetroncon, ptmax);
                    if (tempdistancemax < distancemax) {
                        distancemax = tempdistancemax;
                        gtronconmax =
                                boutdetroncon;
                        boutmax =
                                i;
                        abscissemax =
                                currentlength;
                    }

                }
                currentlength += boutdetroncon.getLength();
            }

        } else {
            for (int i = cs.length - 1; i >=
                    1; i--) {
                tab = new Coordinate[2];
                tab[0] = cs[i];
                tab[1] = cs[i - 1];
                Geometry boutdetroncon = gf.createLineString(tab); // La géométrie de la portion de troncon.

                if (minnumero != -1) {
                    double tempdistancemin = DistanceOp.distance(boutdetroncon, ptmin);
                    if (tempdistancemin < distancemin) {
                        distancemin = tempdistancemin;
                        gtronconmin =
                                boutdetroncon;
                        boutmin =
                                i;
                        abscissemin =
                                currentlength;
                    }

                }
                if (maxnumero != Integer.MAX_VALUE) {
                    double tempdistancemax = DistanceOp.distance(boutdetroncon, ptmax);
                    if (tempdistancemax < distancemax) {
                        distancemax = tempdistancemax;
                        gtronconmax =
                                boutdetroncon;
                        boutmax =
                                i;
                        abscissemax =
                                currentlength;
                    }

                }
                currentlength += boutdetroncon.getLength();
            }

        }
        Coordinate ptminprojete = null;
        // Projete les points sur ces portions
        Coordinate ptmaxprojete = null;
        if (minnumero != -1) {
            //calcul de la projection sur le segment
            ptminprojete = projete(ptmin, gtronconmin);
            RetourGeocodage ptminp = projetePointSurTroncon(gf, (Point) ptmin, gtronconmin);
            ptminprojete.x = ptminp.x;
            ptminprojete.y = ptminp.y;

        }

        if (maxnumero != Integer.MAX_VALUE) {
            ptmaxprojete = projete(ptmax, gtronconmax);
            RetourGeocodage ptmaxp = projetePointSurTroncon(gf, (Point) ptmax, gtronconmax);
            ptmaxprojete.x = ptmaxp.x;
            ptmaxprojete.y = ptmaxp.y;
        }

        // détermine les absisses longilignes de ces points sur le troncon.
        double abscisseptmin = 0, abscisseptmax = g.getLength();

        if (minnumero != -1) {
            // L'abscisse sur la portion est la distance du point au début de la portion du troncon.
            //GS ci-dessous on fait intervenir les points projetés sur le tronçon calculés précédemment
            //plutôt que les point adresses
            Geometry ptstart = gf.createPoint(gtronconmin.getCoordinates()[0]);
            //GSabscisseptmin=abscissemin+DistanceOp.distance(ptstart,ptmin);
            Geometry ptminproj = gf.createPoint(ptminprojete);
            abscisseptmin =
                    abscissemin + DistanceOp.distance(ptstart, ptminproj);
        }

        if (maxnumero != Integer.MAX_VALUE) {
            Geometry ptstart = gf.createPoint(gtronconmax.getCoordinates()[0]);
            //GSabscisseptmax=abscissemax+DistanceOp.distance(ptstart,ptmax);
            Geometry ptmaxproj = gf.createPoint(ptmaxprojete);
            abscisseptmax =
                    abscissemax + DistanceOp.distance(ptstart, ptmaxproj);
        }

        // Interpole l'absisse longiligne du numéro
        //GS si les numéros encadrant sont égaux, on retient l'un des numéro de voie trouvé
        //ex : cas d'une numéro avec répétition (ex n° cherchée 19A ; n° trouvés : 19 et 19B) 
        //sinon on calule l'interpolation entre les 2 numéros de voie
        //GSdouble length=abscisseptmin+((numero-minnumero)*(abscisseptmax-abscisseptmin))/(maxnumero-minnumero);
        double length = abscisseptmin;
        if (maxnumero != minnumero) {
            length += ((numero - minnumero) * (abscisseptmax - abscisseptmin)) / (maxnumero - minnumero);
        }
        // En déduit la portion à laquelle le numéro appartient
        double lastlength = 0;
        double abscissebout = 0;
        if (troncon.numero_debut <= troncon.numero_fin) {
            lastlength = currentlength = abscissemin;
            int i = boutmin;
            for (; i <=
                    boutmax; i++) {
                tab[0] = cs[i]; // il ne plus nécessaire de créer un nouveau tableau car la géométrie n'est plus utilisée par la suite.
                tab[1] = cs[i + 1];
                Geometry boutdetroncon = gf.createLineString(tab);
                currentlength +=
                        boutdetroncon.getLength();
                if (lastlength <= length && length <= currentlength) {
                    abscissebout = lastlength;
                    break;
                }
                //GS
                lastlength = currentlength;
            }

            if (i > boutmax) {
                // ne devrait pas arriver
                throw (new GestionReferentielException("Le numero est hors des bornes.",
                        GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }

        } else {
            //GS 	lastlength=currentlength=abscissemax;
            //GS	int i=boutmax;
            //GS	for(; i>=boutmin; i--)
            lastlength = currentlength = abscissemin;
            int i = boutmin;
            for (; i >=
                    boutmax; i--) {
                tab[0] = cs[i]; // il ne plus nécessaire de créer un nouveau tableau car la géométrie n'est plus utilisée par la suite.
                tab[1] = cs[i - 1];
                Geometry boutdetroncon = gf.createLineString(tab);
                currentlength +=
                        boutdetroncon.getLength();

                if (lastlength <= length && length <= currentlength) {
                    abscissebout = lastlength;
                    break;
                }
                //GS
                lastlength = currentlength;
            }
            //GS if (i>boutmax)
            if (i < boutmax) {
                // ne devrait pas arriver
                throw (new GestionReferentielException("Le numero est hors des bornes.",
                        GestionReferentielException.ERREURNONREPERTORIEE, 7));
            }

        }

        // En déduit sa position sur ce bout de troncon.
        //GS       length-=length-abscissebout;
        length -= abscissebout;
        currentlength -=
                lastlength; // optimisation

        res.x = tab[0].x;
        res.y = tab[0].y;

        res.errorcode = 0;
        res.x += (length * (tab[1].x - res.x)) / (currentlength); // simple produit en croix.
        res.y += (length * (tab[1].y - res.y)) / (currentlength);

        res.setService(GestionReferentiel.SERVICE_POINT_ADRESSE);

        return res;
    }

    /**
     * géocode par interpolation métrique sur la totalité de la voie spécifiée.<br>
     * Les troncons sont ordonnés par numéro de début.
     * 
     * @return
     */
    public RetourGeocodage geocodeInterpolationMetriqueVoie(String voi_id, int numero, Date date, int projection, String nomTableTroncon,
            Connection connection) throws SQLException, GestionReferentielException {
        RetourGeocodage res = new RetourGeocodage();

        // Commence par obtenir tous les troncons de la voie.
        ArrayList<CoteDeTroncon> troncons = chercheCoteDeTroncons(voi_id, date, projection, nomTableTroncon, connection);

        // Trie les troncons par numero de debut.
        for (int i = 0; i <
                troncons.size(); i++) {
            int j = 0;
            CoteDeTroncon t = troncons.get(i);
            for (; j <
                    i; j++) {
                CoteDeTroncon t2 = troncons.get(j);
                if (t2.numero_debut > t.numero_debut) {
                    break;
                }

            }
            if (i != j) {
                troncons.remove(i);
                troncons.add(j - 1, t);
            }

        }

        // Cherche à quel troncon appartient le numéro.
        double length = numero;
        double lastlength = 0;
        double currentlength = 0;
        int i = 0;
        Geometry g = null;
        for (; i <
                troncons.size(); i++) {
            if (troncons.get(i).geometrie == null) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat);
                return res;
            }

            try {
                // Extrait les géométries.  // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                g = GeometryUtils.readGeometryFromWKT(troncons.get(i).geometrie);
            } catch (ParseException pe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat);  // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                return res;
            } catch (NullPointerException npe) {
                res.errorcode = 10;
                res.message = "Erreur de géométrie dans le géocodage de " + numero + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat);  // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                return res;
            }

            currentlength += g.getLength();
            if (lastlength <= numero && numero <= currentlength) {
                break;
            }

        }

        if (i == troncons.size()) {
            res.nontrouve = true;
            return res;
        }

        // détermine la position du numéro sur ce troncon.
        return geocodeInterpolationMetriqueTroncon(length, currentlength, g, troncons.get(i).numero_debut, troncons.get(i).numero_fin);
    }

    /**
     * Formate les paramêtres pour le retour de la méthode geocodeAdresse.
     * @param x 
     * @param y 
     * @param typegeocodage 
     * @param projectionEtReferentiel 
     * @return
     */
    public static String[] formateResultatGeocodage(RetourGeocodage rg, int typegeocodage, Date date, String[] projectionEtReferentiel) {
        if (rg.nontrouve) {
            return new String[]{
                "8", "0"
            };
        } else if (rg.errorcode != 0) {
            return new String[]{
                "0", Integer.toString(rg.errorcode), rg.message
            };
        } else {
            // WA 09/2011 Passage de '2 chiffres apres la virgule' a '9 chiffres significatifs'
            // -> 47.6375064 pour du WGS84 ou 6733431.82 pour du Lambert 93
            // String x=Double.toString(((double) Math.rint(rgDep.x*100))/100);
            // String y=Double.toString(((double) Math.rint(rgDep.y*100))/100);
            String x = String.format(Locale.US, "%9.9g", rg.x);
            String y = String.format(Locale.US, "%9.9g", rg.y);
            String[] res = new String[]{
                "8", "1",
                Integer.toString(typegeocodage),
                Integer.toString(rg.getService()),
                x, y,
                DateUtils.formatDateToString(date, sdformat),
                projectionEtReferentiel[0], projectionEtReferentiel[1]
            };
            return res;
        }

    }

    /**
     * Formate les paramêtres pour le retour de la méthode geocodeAdresse, pour
     * les messages d'erreur.
     * @param x 
     * @param y 
     * @param typegeocodage 
     * @param projectionEtReferentiel 
     * @return
     */
    public static String[] formateResultatGeocodage(RetourGeocodage rg) {
        return new String[]{
            "0", Integer.toString(rg.errorcode), rg.message
        };
    }

    /**
     * Retourne le géocodage de l'adresse spécifiée dans la voie spécifiée du département spécifié.
     * 
     * NB concernant le géocodagre à la commune : si un problème se pose avec le géocodage à la commune,
     * le géocodage au département est effectué dans la foulée.
     * 
     * Selon les informations fournies par le référentiel, la précision du géocodage peut être:
     * <ul>
     * <li>Au centroide de la voie si le numéro est 0.</li>
     * <li>A la plaque, si le point adresse a été trouvé.</li>
     * <li>Sinon, à l'interpolation de la plaque, si des points adresse proches sont trouvés et que le troncon contenant le numéro a un adressage classique ou métrique</li>
     * <li>Sinon, à l'interpolation métrique sur le troncon, si le troncon concenant le numéro a un adressage classique ou métrique</li>
     * <li>Sinon, à l'une des extrémité du troncon, si le troncon contenant le numéro a été trouvé, et qu'il s'agit d'une des bornes.</li>
     * <li>Sinon, au centroïde du troncon, si le troncon contenant le numéro a été trouvé.</li>
     * <li>Sinon, au centroïde de la voie.</li>
     * </ul>
     * Si la méthode réussi, le retour est de la forme:
     * <ul>
     * <li>Code de la méthode: 7</li>
     * <li>Nombre de résultats: 1</li>
     * <li>Type de géocodage (de moins en moins précis):
     * <ul>
     * <li>1 pour à la plaque,</li>
     * <li>2 pour à l'interpolation de la plaque,</li>
     * <li>3 pour à l'interpolation métrique du troncon ou les bornes du troncon (qualité équivalente),</li>
     * <li>4 au centroide du troncon,</li>
     * <li>5 pour le centroide de la voie.</li>
     * <li>6 pour la commune.</li>
     * <li>7 pour le dpt.</li>
     * <li>8 pour le pays.</li>
     * </ul>
     * <li>X précision cm</li>
     * <li>Y précision cm</li>
     * <li>Date de validation</li>
     * <li>Referentiel</li>
     * <li>Projection</li>
     * </ul>
     * En cas d'erreur, le retour est de la forme:
     * <ul>
     * <li>0</li>
     * <li>Code d'erreur</li>
     * <li>Message d'erreur</li>
     * </ul>
     */
    public String[] geocodeAdresse(int application, String voi_id, int numero, char repetition, String code_insee, String paysSovAc3,
            int projection, String defaultWorldProj, Date date, Connection connection) throws SQLException, GestionReferentielException {
        // WA 01/2012 Pays : Effectue un geocodage au pays
        if ((voi_id == null) && (code_insee == null)) {
            RetourGeocodage rg = geocodePays(paysSovAc3, date, defaultWorldProj, projection, connection);
            if ((!rg.nontrouve) && (rg.errorcode == 0)) {
//                GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_PAYS, true);
           jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PAYS, true);
                return formateResultatGeocodage(rg, 8, date, new String[]{rg.getProjectionText(), rg.getReferentielText()});
            } else {
//                GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
                jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                return formateResultatGeocodage(rg);
            }

        }

        // WA 09/2011 Remplace par computeCodeDptFromCodeInseeOrCodeDpt de GestionCodesDepartements
        // if (code_insee.length()<2) // logguée à un niveau supérieur.
        // throw(new GestionReferentielException("Le code insee est invalide",GestionReferentielException.PARAMETREERRONNE,5));
        //  String code_departement = code_insee.substring(0,2);
        String code_departement = GestionCodesDepartements.getInstance().computeCodeDptFromCodeInseeOrCodeDpt(code_insee);

        GeometryFactory gf = new GeometryFactory();

        // Obtient la projection et le référentiel concerné
        String[] projectionEtReferentiel = obtientProjectionEtReferentiel(code_departement, date, projection, connection);

        // Effectue un géocodage au département ou à la commune.
        if (voi_id == null) {
            if (code_insee.length() == 5) {
                RetourGeocodage rg = geocodeCommune(code_insee, date, projection, connection);
                if (!rg.nontrouve && rg.errorcode == 0) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_COMMUNE, true);
                    return formateResultatGeocodage(rg, 6, date, projectionEtReferentiel);
                }
            // Comportement par défaut : si un problème se pose avec le géocodage à la commune,
            // le géocodage au département est effectué dans la foulée.
            }

            RetourGeocodage rg = geocodeDepartement(code_departement, date, projection, connection);
            if (!rg.nontrouve && rg.errorcode == 0) {
                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_DEPARTEMENT, true);
                return formateResultatGeocodage(rg, 7, date, projectionEtReferentiel);
            }

            // loggué à un niveau supérieur
            throw (new GestionReferentielException("L'adresse ne peut pas être géocodée.",
                    GestionReferentielException.PARAMETREERRONNE, 5));
        }

        // Cherche les points adresse
        // Si ils existent, retourne le résultat
        RetourGeocodage res = geocodePointAdresse(voi_id, numero, repetition, code_departement, date, projection, connection);
        if (!res.nontrouve && res.errorcode == 0) {
             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PLAQUE, true);
            return formateResultatGeocodage(res, 1, date, projectionEtReferentiel);
        }


        // Cherche les tronçons
        String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, code_departement, date);

        if (tableTroncons == null) {
            res.errorcode = 10;
            res.message = "Le département spécifié n'existe pas à la date mentionnée.";
             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            return formateResultatGeocodage(res);
        }

        if (numero != 0) {
            ArrayList<CoteDeTroncon> troncons = chercheCoteDeTroncons(voi_id, numero, date, code_departement, projection, tableTroncons,
                    connection);
            ArrayList<Integer> positions = filtreTroncons(numero, repetition, troncons);

            // Si aucun troncon n'est trouvé,
            if (troncons.size() == 0) {
            // passe directement à la suite.
            } // Si un seul troncon est trouvé
            else if (troncons.size() == 1) {
                Coordinate[] cs = null;
                Coordinate c = null;
                double[] pt = null;
                Geometry g = null;

                // pour optimisation de base
                CoteDeTroncon t = troncons.get(0);
                int position = positions.get(0).intValue();
                boolean classiqueoumetrique = t.typadr.compareToIgnoreCase("classique") == 0 || t.typadr.compareToIgnoreCase(
                        "metrique") == 0;

                // Commence par essayer une interpolation de point adresse si possible.
                if (classiqueoumetrique) {
                    res = geocodeInterpolationPointAdresse(gf, voi_id, numero, repetition, t, code_departement, date, projection, connection);
                    if (!res.nontrouve && res.errorcode == 0) {
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_PLAQUE, true);
                        return formateResultatGeocodage(res, 2, date, projectionEtReferentiel);
                    }

                }

                if (t.geometrie == null) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        "0", "10",
                        "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                    };
                }

                try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                    g = GeometryUtils.readGeometryFromWKT(t.geometrie);
                } catch (ParseException pe) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        "0", "10",
                        "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                    };
                } catch (NullPointerException npe) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        "0", "10",
                        "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                    };
                }

                // Sinon, essaye une interpolation métrique au troncon
                if (classiqueoumetrique) {
                    res = geocodeInterpolationMetriqueTroncon(numero, g, t.numero_debut, t.numero_fin);
                    if (!res.nontrouve && res.errorcode == 0) {
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                        return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                    }

                }

                // Trouve le point selon sa position.
                switch (position) {
                    default:
                        // Cas normalement impossible (cf requête de chercheCoteDeTroncons).
                        // loggué à un niveau supérieur
                        throw (new GestionReferentielException("Le numéro est hors bornes",
                                GestionReferentielException.ERREURNONREPERTORIEE, 7));
                    case 0:
                        // retourne le centroide du troncon.
                        res = geocodeCentroideTroncon(gf, g, t.numero_debut <= t.numero_fin);
                        if (!res.nontrouve && res.errorcode == 0) {
                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_TRONCON, true);
                            return formateResultatGeocodage(res, 4, date, projectionEtReferentiel);
                        }
                    case 1: // extrémité de troncon
                        cs = g.getCoordinates();
                        c = cs[cs.length - 1];
                        res = new RetourGeocodage();
                        res.x = c.x;
                        res.y = c.y;
                        res.setService(GestionReferentiel.SERVICE_TRONCON);
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                        return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                    case 2: // extrémité de troncon
                        cs = g.getCoordinates();
                        c = cs[0];
                        res = new RetourGeocodage();
                        res.x = c.x;
                        res.y = c.y;
                        res.setService(GestionReferentiel.SERVICE_TRONCON);
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                        return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                }

            } // Si plusieurs troncons sont trouvés
            //   Si le numéro est a des extrémités, et qu'il s'agit du même point pour tous, retourne le point
            else {
                res = geocodeMultipleTroncons(voi_id, numero, repetition, troncons, positions);
                if (!res.nontrouve && res.errorcode == 0) {
                    return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                }

            }
        }
        // Sinon, si aucun troncon n'est trouvé,
        // ou que les méthodes précédentes échoue,
        // essaye une interpolation métrique à la voie.
        res = geocodeCentroideVoie(gf, voi_id, date, projection, tableTroncons, connection);
         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_VOIE, true);
        return formateResultatGeocodage(res, 5, date, projectionEtReferentiel);
    }

    public String[] geocodeAdresse(int application, String voi_id, int numero, double distance, char repetition, String code_insee, String paysSovAc3,
            int projection, String defaultWorldProj, Date date, Connection connection) throws SQLException, GestionReferentielException {
        // WA 01/2012 Pays : Effectue un geocodage au pays
        if ((voi_id == null) && (code_insee == null)) {
            RetourGeocodage rg = geocodePays(paysSovAc3, date, defaultWorldProj, projection, connection);
            if ((!rg.nontrouve) && (rg.errorcode == 0)) {
                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PAYS, true);
                return formateResultatGeocodage(rg, 8, date, new String[]{rg.getProjectionText(), rg.getReferentielText()});
            } else {
                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                return formateResultatGeocodage(rg);
            }

        }

        // WA 09/2011 Remplace par computeCodeDptFromCodeInseeOrCodeDpt de GestionCodesDepartements
        // if (code_insee.length()<2) // logguée à un niveau supérieur.
        // throw(new GestionReferentielException("Le code insee est invalide",GestionReferentielException.PARAMETREERRONNE,5));
        //  String code_departement = code_insee.substring(0,2);
        String code_departement = GestionCodesDepartements.getInstance().computeCodeDptFromCodeInseeOrCodeDpt(code_insee);

        GeometryFactory gf = new GeometryFactory();

        // Obtient la projection et le référentiel concerné
        String[] projectionEtReferentiel = obtientProjectionEtReferentiel(code_departement, date, projection, connection);

        // Effectue un géocodage au département ou à la commune.
        if (voi_id == null) {
            if (code_insee.length() == 5) {
                RetourGeocodage rg = geocodeCommune(code_insee, date, projection, connection);
                if (!rg.nontrouve && rg.errorcode == 0) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_COMMUNE, true);
                    return formateResultatGeocodage(rg, 6, date, projectionEtReferentiel);
                }
            // Comportement par défaut : si un problème se pose avec le géocodage à la commune,
            // le géocodage au département est effectué dans la foulée.
            }

            RetourGeocodage rg = geocodeDepartement(code_departement, date, projection, connection);
            if (!rg.nontrouve && rg.errorcode == 0) {
                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_DEPARTEMENT, true);
                return formateResultatGeocodage(rg, 7, date, projectionEtReferentiel);
            }

            // loggué à un niveau supérieur
            throw (new GestionReferentielException("L'adresse ne peut pas être géocodée.",
                    GestionReferentielException.PARAMETREERRONNE, 5));
        }

        // Cherche les points adresse
        // Si ils existent, retourne le résultat
        RetourGeocodage res = geocodePointAdresse(voi_id, numero, repetition, code_departement, date, projection, connection);
        if (!res.nontrouve && res.errorcode == 0) {
             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PLAQUE, true);
            return formateResultatGeocodage(res, 1, date, projectionEtReferentiel);
        }


        // Cherche les tronçons
        String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, code_departement, date);

        if (tableTroncons == null) {
            res.errorcode = 10;
            res.message = "Le département spécifié n'existe pas à la date mentionnée.";
             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            return formateResultatGeocodage(res);
        }

        if (numero != 0) {
            ArrayList<CoteDeTroncon> troncons = chercheCoteDeTroncons(voi_id, numero, date, code_departement, projection, tableTroncons,
                    connection);
            ArrayList<Integer> positions = filtreTroncons(numero, repetition, troncons);

            // Si aucun troncon n'est trouvé,
            if (troncons.size() == 0) {
            // passe directement à la suite.
            } // Si un seul troncon est trouvé
            else if (troncons.size() == 1) {
                Coordinate[] cs = null;
                Coordinate c = null;
                double[] pt = null;
                Geometry g = null;

                // pour optimisation de base
                CoteDeTroncon t = troncons.get(0);
                int position = positions.get(0).intValue();
                boolean classiqueoumetrique = t.typadr.compareToIgnoreCase("classique") == 0 || t.typadr.compareToIgnoreCase(
                        "metrique") == 0;

                // Commence par essayer une interpolation de point adresse si possible.
                if (classiqueoumetrique) {
                    res = geocodeInterpolationPointAdresse(gf, voi_id, numero, repetition, t, code_departement, date, projection, connection);
                    if (!res.nontrouve && res.errorcode == 0) {
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_PLAQUE, true);
                        return formateResultatGeocodage(res, 2, date, projectionEtReferentiel);
                    }

                }

                if (t.geometrie == null) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        "0", "10",
                        "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                    };
                }

                try {   // WA 03/2012 WKTReader semble de pas etre thread-safe : remplace par GeometryUtils
                    g = GeometryUtils.readGeometryFromWKT(t.geometrie);
                } catch (ParseException pe) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        "0", "10",
                        "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                    };
                } catch (NullPointerException npe) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    return new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                        "0", "10",
                        "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                        " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                    };
                }

                // Sinon, essaye une interpolation métrique au troncon
                if (classiqueoumetrique) {
                    res = geocodeInterpolationMetriqueTroncon(numero, g, t.numero_debut, t.numero_fin, distance, t.isCoteDroit);
                    if (!res.nontrouve && res.errorcode == 0) {
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                        return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                    }

                }

                // Trouve le point selon sa position.
                switch (position) {
                    default:
                        // Cas normalement impossible (cf requête de chercheCoteDeTroncons).
                        // loggué à un niveau supérieur
                        throw (new GestionReferentielException("Le numéro est hors bornes",
                                GestionReferentielException.ERREURNONREPERTORIEE, 7));
                    case 0:
                        // retourne le centroide du troncon.
                        res = geocodeCentroideTroncon(gf, g, t.numero_debut <= t.numero_fin);
                        if (!res.nontrouve && res.errorcode == 0) {
                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_TRONCON, true);
                            return formateResultatGeocodage(res, 4, date, projectionEtReferentiel);
                        }
                        break;
                    case 1: // extrémité de troncon (fin)
                        cs = g.getCoordinates();
                        c = cs[cs.length - 1];
                        res = new RetourGeocodage();
                        if (cs.length > 1) {
                            int coteDecalage = determineCoteDecalage(numero, t.numero_debut, t.numero_fin, t.isCoteDroit);
                            Coordinate start;
                            Coordinate end;
                            if (t.numero_debut <= t.numero_fin) { // sens de la géométrie
                                start = cs[cs.length - 2];
                                end = c;
                            } else {
                                start = c;
                                end = cs[cs.length - 2]; // sens inverse
                            }
                            final Coordinate pointProjete = projettePoint(start, end, c.x, c.y, distance, coteDecalage);
                            res.x = pointProjete.x;
                            res.y = pointProjete.y;
                        } else {
                            res.x = c.x;
                            res.y = c.y;
                        }
                        res.setService(GestionReferentiel.SERVICE_TRONCON);
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                        return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                    case 2: // extrémité de troncon (debut)
                        cs = g.getCoordinates();
                        c = cs[0];
                        res = new RetourGeocodage();
                        if (cs.length > 1) {
                            int coteDecalage = determineCoteDecalage(numero, t.numero_debut, t.numero_fin, t.isCoteDroit);
                            Coordinate start;
                            Coordinate end;
                            if (t.numero_debut <= t.numero_fin) { // sens de la géométrie
                                start = c;
                                end = cs[1];
                            } else { // sens inverse
                                start = cs[1];
                                end = c;
                            }
                            final Coordinate pointProjete = projettePoint(start, end, c.x, c.y, distance, coteDecalage);
                            res.x = pointProjete.x;
                            res.y = pointProjete.y;
                        } else {
                            res.x = c.x;
                            res.y = c.y;
                        }
                        res.setService(GestionReferentiel.SERVICE_TRONCON);
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                        return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                }

            } // Si plusieurs troncons sont trouvés
            //   Si le numéro est a des extrémités, et qu'il s'agit du même point pour tous, retourne le point
            else {
                res = geocodeMultipleTroncons(voi_id, numero, distance, repetition, troncons, positions);
                if (!res.nontrouve && res.errorcode == 0) {
                    return formateResultatGeocodage(res, 3, date, projectionEtReferentiel);
                }

            }
        }
        // Sinon, si aucun troncon n'est trouvé,
        // ou que les méthodes précédentes échoue,
        // essaye une interpolation métrique à la voie.
        res = geocodeCentroideVoie(gf, voi_id, date, projection, tableTroncons, connection);
         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_VOIE, true);
        return formateResultatGeocodage(res, 5, date, projectionEtReferentiel);
    }

    public List<String[]> geocodeAdresse(int application, int[] services, String voi_id, int numero, char repetition, String code_insee, String paysSovAc3,
            int projection, String defaultWorldProj, Date date, Connection connection) throws SQLException, GestionReferentielException {
        final List<String[]> listRet = new ArrayList<String[]>();
        for (int service : services) {
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(service).getId();
            if (id == GestionReferentiel.SERVICE_ADRESSE) {
                listRet.add(geocodeAdresse(application, voi_id, numero, repetition, code_insee, paysSovAc3, projection, defaultWorldProj, date, connection));
            } else if (id == GestionReferentiel.SERVICE_PAYS) {
                RetourGeocodage rgPay = geocodePays(paysSovAc3, date, defaultWorldProj, projection, connection);
                if ((!rgPay.nontrouve) && (rgPay.errorcode == 0)) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PAYS, true);
                    listRet.add(formateResultatGeocodage(rgPay, 8, date, new String[]{rgPay.getProjectionText(), rgPay.getReferentielText()}));
                } else {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    listRet.add(formateResultatGeocodage(rgPay));
                }
            } else {
                // WA 09/2011 Remplace par computeCodeDptFromCodeInseeOrCodeDpt de GestionCodesDepartements
                //  if (code_insee.length()<2) // logguée à un niveau supérieur.
                //   throw(new GestionReferentielException("Le code insee est invalide",GestionReferentielException.PARAMETREERRONNE,5));
                //   String code_departement = code_insee.substring(0,2);
                String code_departement = GestionCodesDepartements.getInstance().computeCodeDptFromCodeInseeOrCodeDpt(code_insee);
                GeometryFactory gf = new GeometryFactory();

                // Obtient la projection et le référentiel concerné
                String[] projectionEtReferentiel = obtientProjectionEtReferentiel(code_departement, date, projection, connection);
                if (id == GestionReferentiel.SERVICE_POINT_ADRESSE || id == GestionReferentiel.SERVICE_DEPARTEMENT || id == GestionReferentiel.SERVICE_COMMUNE) {
                    switch (id) {
                        case GestionReferentiel.SERVICE_DEPARTEMENT:
                            RetourGeocodage rgDep = geocodeDepartement(code_departement, date, projection, connection);
                            if (!rgDep.nontrouve && rgDep.errorcode == 0) {
                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_DEPARTEMENT, true);
                                listRet.add(formateResultatGeocodage(rgDep, 7, date, projectionEtReferentiel));
                            }
                            break;
                        case GestionReferentiel.SERVICE_COMMUNE:
                            if (code_insee.length() == 5) {
                                RetourGeocodage rgCom = geocodeCommune(code_insee, date, projection, connection);
                                if (!rgCom.nontrouve && rgCom.errorcode == 0) {
                                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_COMMUNE, true);
                                    listRet.add(formateResultatGeocodage(rgCom, 6, date, projectionEtReferentiel));
                                }
                            }
                            break;
                        case GestionReferentiel.SERVICE_POINT_ADRESSE:
                            // Cherche les points adresse
                            // Si ils existent, retourne le résultat
                            RetourGeocodage rgAdr = geocodePointAdresse(voi_id, numero, repetition, code_departement, date, projection, connection);
                            if (!rgAdr.nontrouve && rgAdr.errorcode == 0) {
                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PLAQUE, true);
                                listRet.add(formateResultatGeocodage(rgAdr, 1, date, projectionEtReferentiel));
                            }
                            break;
                    }
                } else {
                    String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, code_departement, date);

                    if (tableTroncons == null) {
                        RetourGeocodage rg = new RetourGeocodage();
                        rg.errorcode = 10;
                        rg.message = "Le département spécifié n'existe pas à la date mentionnée.";
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                        listRet.add(formateResultatGeocodage(rg));
                    } else {
                        switch (id) {
                            case GestionReferentiel.SERVICE_VOIE:
                                RetourGeocodage rgVoi = new RetourGeocodage();
                                rgVoi = geocodeCentroideVoie(gf, voi_id, date, projection, tableTroncons, connection);
                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_VOIE, true);
                                listRet.add(formateResultatGeocodage(rgVoi, 5, date, projectionEtReferentiel));
                                break;
                            case GestionReferentiel.SERVICE_TRONCON:
                                // Cherche les tronçons
                                RetourGeocodage rgTro = new RetourGeocodage();

                                if (numero != 0) {
                                    ArrayList<CoteDeTroncon> troncons = chercheCoteDeTroncons(voi_id, numero, date, code_departement, projection, tableTroncons,
                                            connection);
                                    ArrayList<Integer> positions = filtreTroncons(numero, repetition, troncons);

                                    // Si aucun troncon n'est trouvé,
                                    if (troncons.size() == 0) {
                                    // passe directement à la suite.
                                    } // Si un seul troncon est trouvé
                                    else if (troncons.size() == 1) {
                                        Coordinate[] cs = null;
                                        Coordinate c = null;
                                        double[] pt = null;
                                        Geometry g = null;

                                        // pour optimisation de base
                                        CoteDeTroncon t = troncons.get(0);
                                        int position = positions.get(0).intValue();
                                        boolean classiqueoumetrique = t.typadr.compareToIgnoreCase("classique") == 0 || t.typadr.compareToIgnoreCase(
                                                "metrique") == 0;

                                        if (t.geometrie == null) {
                                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                                            listRet.add(new String[]{
                                                "0", "10",
                                                "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                                                " à " + DateUtils.formatDateToString(date, sdformat)
                                            });
                                        }

                                        try {
                                            g = GeometryUtils.readGeometryFromWKT(t.geometrie);
                                        } catch (ParseException pe) {
                                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                                            listRet.add(new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                                                "0", "10",
                                                "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                                                " à " + DateUtils.formatDateToString(date, sdformat)
                                            });
                                        } catch (NullPointerException npe) {
                                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                                            listRet.add(new String[]{
                                                "0", "10",
                                                "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                                                " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                                            });
                                        }

                                        // Interpolation métrique au troncon
                                        if (classiqueoumetrique) {
                                            rgTro = geocodeInterpolationMetriqueTroncon(numero, g, t.numero_debut, t.numero_fin);
                                            if (!rgTro.nontrouve && rgTro.errorcode == 0) {
                                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                                                listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                                break;
                                            }

                                        }

                                        // Trouve le point selon sa position.
                                        switch (position) {
                                            default:
                                                // Cas normalement impossible (cf requête de chercheCoteDeTroncons).
                                                // loggué à un niveau supérieur
                                                throw (new GestionReferentielException("Le numéro est hors bornes",
                                                        GestionReferentielException.ERREURNONREPERTORIEE, 7));
                                            case 0:
                                                // retourne le centroide du troncon.
                                                rgTro = geocodeCentroideTroncon(gf, g, t.numero_debut <= t.numero_fin);
                                                if (!rgTro.nontrouve && rgTro.errorcode == 0) {
                                                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_TRONCON, true);
                                                    listRet.add(formateResultatGeocodage(rgTro, 4, date, projectionEtReferentiel));
                                                }
                                                break;
                                            case 1: // extrémité de troncon
                                                cs = g.getCoordinates();
                                                c = cs[cs.length - 1];
                                                rgTro = new RetourGeocodage();
                                                rgTro.x = c.x;
                                                rgTro.y = c.y;
                                                rgTro.setService(GestionReferentiel.SERVICE_TRONCON);
                                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                                                listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                                break;
                                            case 2: // extrémité de troncon
                                                cs = g.getCoordinates();
                                                c = cs[0];
                                                rgTro = new RetourGeocodage();
                                                rgTro.x = c.x;
                                                rgTro.y = c.y;
                                                rgTro.setService(GestionReferentiel.SERVICE_TRONCON);
                                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                                                listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                                break;
                                        }

                                    } // Si plusieurs troncons sont trouvés
                                    //   Si le numéro est a des extrémités, et qu'il s'agit du même point pour tous, retourne le point
                                    else {
                                        rgTro = geocodeMultipleTroncons(voi_id, numero, repetition, troncons, positions);
                                        if (!rgTro.nontrouve && rgTro.errorcode == 0) {
                                            listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                        }

                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }

        return listRet;
    }

    public List<String[]> geocodeAdresse(int application, int[] services, String voi_id, int numero, double distance, char repetition, String code_insee, String paysSovAc3,
            int projection, String defaultWorldProj, Date date, Connection connection) throws SQLException, GestionReferentielException {
        final List<String[]> listRet = new ArrayList<String[]>();
        for (int service : services) {
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(service).getId();
            if (id == GestionReferentiel.SERVICE_ADRESSE) {
                listRet.add(geocodeAdresse(application, voi_id, numero, distance, repetition, code_insee, paysSovAc3, projection, defaultWorldProj, date, connection));
            } else if (id == GestionReferentiel.SERVICE_PAYS) {
                RetourGeocodage rgPay = geocodePays(paysSovAc3, date, defaultWorldProj, projection, connection);
                if ((!rgPay.nontrouve) && (rgPay.errorcode == 0)) {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PAYS, true);
                    listRet.add(formateResultatGeocodage(rgPay, 8, date, new String[]{rgPay.getProjectionText(), rgPay.getReferentielText()}));
                } else {
                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                    listRet.add(formateResultatGeocodage(rgPay));
                }
            } else {
                // WA 09/2011 Remplace par computeCodeDptFromCodeInseeOrCodeDpt de GestionCodesDepartements
                //  if (code_insee.length()<2) // logguée à un niveau supérieur.
                //   throw(new GestionReferentielException("Le code insee est invalide",GestionReferentielException.PARAMETREERRONNE,5));
                //   String code_departement = code_insee.substring(0,2);
                String code_departement = GestionCodesDepartements.getInstance().computeCodeDptFromCodeInseeOrCodeDpt(code_insee);
                GeometryFactory gf = new GeometryFactory();

                // Obtient la projection et le référentiel concerné
                String[] projectionEtReferentiel = obtientProjectionEtReferentiel(code_departement, date, projection, connection);
                if (id == GestionReferentiel.SERVICE_POINT_ADRESSE || id == GestionReferentiel.SERVICE_DEPARTEMENT || id == GestionReferentiel.SERVICE_COMMUNE) {
                    switch (id) {
                        case GestionReferentiel.SERVICE_DEPARTEMENT:
                            RetourGeocodage rgDep = geocodeDepartement(code_departement, date, projection, connection);
                            if (!rgDep.nontrouve && rgDep.errorcode == 0) {
                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_DEPARTEMENT, true);
                                listRet.add(formateResultatGeocodage(rgDep, 7, date, projectionEtReferentiel));
                            }
                            break;
                        case GestionReferentiel.SERVICE_COMMUNE:
                            if (code_insee.length() == 5) {
                                RetourGeocodage rgCom = geocodeCommune(code_insee, date, projection, connection);
                                if (!rgCom.nontrouve && rgCom.errorcode == 0) {
                                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_COMMUNE, true);
                                    listRet.add(formateResultatGeocodage(rgCom, 6, date, projectionEtReferentiel));
                                }
                            }
                            break;
                        case GestionReferentiel.SERVICE_POINT_ADRESSE:
                            // Cherche les points adresse
                            // Si ils existent, retourne le résultat
                            RetourGeocodage rgAdr = geocodePointAdresse(voi_id, numero, repetition, code_departement, date, projection, connection);
                            if (!rgAdr.nontrouve && rgAdr.errorcode == 0) {
                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_PLAQUE, true);
                                listRet.add(formateResultatGeocodage(rgAdr, 1, date, projectionEtReferentiel));
                            }
                            break;
                    }
                } else {
                    String tableTroncons = GestionHistoriqueTables.obtientTableTroncon(connection, code_departement, date);

                    if (tableTroncons == null) {
                        RetourGeocodage rg = new RetourGeocodage();
                        rg.errorcode = 10;
                        rg.message = "Le département spécifié n'existe pas à la date mentionnée.";
                         jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                        listRet.add(formateResultatGeocodage(rg));
                    } else {
                        switch (id) {
                            case GestionReferentiel.SERVICE_VOIE:
                                RetourGeocodage rgVoi = new RetourGeocodage();
                                rgVoi = geocodeCentroideVoie(gf, voi_id, date, projection, tableTroncons, connection);
                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_VOIE, true);
                                listRet.add(formateResultatGeocodage(rgVoi, 5, date, projectionEtReferentiel));
                                break;
                            case GestionReferentiel.SERVICE_TRONCON:
                                // Cherche les tronçons
                                RetourGeocodage rgTro = new RetourGeocodage();

                                if (numero != 0) {
                                    ArrayList<CoteDeTroncon> troncons = chercheCoteDeTroncons(voi_id, numero, date, code_departement, projection, tableTroncons,
                                            connection);
                                    ArrayList<Integer> positions = filtreTroncons(numero, repetition, troncons);

                                    // Si aucun troncon n'est trouvé,
                                    if (troncons.size() == 0) {
                                    // passe directement à la suite.
                                    } // Si un seul troncon est trouvé
                                    else if (troncons.size() == 1) {
                                        Coordinate[] cs = null;
                                        Coordinate c = null;
                                        double[] pt = null;
                                        Geometry g = null;

                                        // pour optimisation de base
                                        CoteDeTroncon t = troncons.get(0);
                                        int position = positions.get(0).intValue();
                                        boolean classiqueoumetrique = t.typadr.compareToIgnoreCase("classique") == 0 || t.typadr.compareToIgnoreCase(
                                                "metrique") == 0;

                                        if (t.geometrie == null) {
                                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                                            listRet.add(new String[]{
                                                "0", "10",
                                                "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                                                " à " + DateUtils.formatDateToString(date, sdformat)
                                            });
                                        }

                                        try {
                                            g = GeometryUtils.readGeometryFromWKT(t.geometrie);
                                        } catch (ParseException pe) {
                                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                                            listRet.add(new String[]{ // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
                                                "0", "10",
                                                "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                                                " à " + DateUtils.formatDateToString(date, sdformat)
                                            });
                                        } catch (NullPointerException npe) {
                                             jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
                                            listRet.add(new String[]{
                                                "0", "10",
                                                "Erreur de géométrie dans le géocodage de " + numero + " " + repetition + " dans " + voi_id +
                                                " à " + /*sdformat.format(date)*/ DateUtils.formatDateToString(date, sdformat)
                                            });
                                        }

                                        // Interpolation métrique au troncon
                                        if (classiqueoumetrique) {
                                            rgTro = geocodeInterpolationMetriqueTroncon(numero, g, t.numero_debut, t.numero_fin, distance, t.isCoteDroit);
                                            if (!rgTro.nontrouve && rgTro.errorcode == 0) {
                                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                                                listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                                break;
                                            }

                                        }

                                        // Trouve le point selon sa position.
                                        switch (position) {
                                            default:
                                                // Cas normalement impossible (cf requête de chercheCoteDeTroncons).
                                                // loggué à un niveau supérieur
                                                throw (new GestionReferentielException("Le numéro est hors bornes",
                                                        GestionReferentielException.ERREURNONREPERTORIEE, 7));
                                            case 0:
                                                // retourne le centroide du troncon.
                                                rgTro = geocodeCentroideTroncon(gf, g, t.numero_debut <= t.numero_fin);
                                                if (!rgTro.nontrouve && rgTro.errorcode == 0) {
                                                     jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_CENTROIDE_TRONCON, true);
                                                    listRet.add(formateResultatGeocodage(rgTro, 4, date, projectionEtReferentiel));
                                                }
                                                break;
                                            case 1: // extrémité de troncon (fin)
                                                cs = g.getCoordinates();
                                                c = cs[cs.length - 1];
                                                rgTro = new RetourGeocodage();
                                                if (cs.length > 1) {
                                                    int coteDecalage = determineCoteDecalage(numero, t.numero_debut, t.numero_fin, t.isCoteDroit);
                                                    Coordinate start;
                                                    Coordinate end;
                                                    if (t.numero_debut <= t.numero_fin) {
                                                        start = cs[cs.length - 2];
                                                        end = c;
                                                    } else {
                                                        start = c;
                                                        end = cs[cs.length - 2];
                                                    }
                                                    final Coordinate pointProjete = projettePoint(start, end, c.x, c.y, distance, coteDecalage);
                                                    rgTro.x = pointProjete.x;
                                                    rgTro.y = pointProjete.y;
                                                } else {
                                                    rgTro.x = c.x;
                                                    rgTro.y = c.y;
                                                }
                                                rgTro.setService(GestionReferentiel.SERVICE_TRONCON);
                                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                                                listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                                break;
                                            case 2: // extrémité de troncon (debut)
                                                cs = g.getCoordinates();
                                                c = cs[0];
                                                rgTro = new RetourGeocodage();
                                                if (cs.length > 1) {
                                                    int coteDecalage = determineCoteDecalage(numero, t.numero_debut, t.numero_fin, t.isCoteDroit);
                                                    Coordinate start;
                                                    Coordinate end;
                                                    if (t.numero_debut <= t.numero_fin) {
                                                        start = c;
                                                        end = cs[1];
                                                    } else {
                                                        start = cs[1];
                                                        end = c;
                                                    }
                                                    final Coordinate pointProjete = projettePoint(start, end, c.x, c.y, distance, coteDecalage);
                                                    rgTro.x = pointProjete.x;
                                                    rgTro.y = pointProjete.y;
                                                } else {
                                                    rgTro.x = c.x;
                                                    rgTro.y = c.y;
                                                }
                                                rgTro.setService(GestionReferentiel.SERVICE_TRONCON);
                                                 jdonrefParams.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_INTERPOLATION_TRONCON, true);
                                                listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                                break;
                                        }

                                    } // Si plusieurs troncons sont trouvés
                                    //   Si le numéro est a des extrémités, et qu'il s'agit du même point pour tous, retourne le point
                                    else {
                                        rgTro = geocodeMultipleTroncons(voi_id, numero, distance, repetition, troncons, positions);
                                        if (!rgTro.nontrouve && rgTro.errorcode == 0) {
                                            listRet.add(formateResultatGeocodage(rgTro, 3, date, projectionEtReferentiel));
                                        }

                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }

        return listRet;
    }

    private static int determineCoteDecalage(int numero, int numero_debut, int numero_fin, boolean isCoteDroit) {

        int intRet = 0; // pas de décalage
        if (isCoteDroit) { // coté droit
            if (numero_debut % 2 == 0 && numero_fin % 2 == 0) { // coté droit = numeros pairs
                if (numero % 2 == 0) { // numero pair
                    intRet = 1; // décalage à droite
                }
            } else if (numero_debut % 2 == 1 && numero_fin % 2 == 1) { // coté droit = numeros impairs
                if (numero % 2 == 1) { // numero impair
                    intRet = 1; // décalage à droite
                }
            }
        } else { // coté gauche
            if (numero_debut % 2 == 0 && numero_fin % 2 == 0) { // coté gauche = numeros pairs
                if (numero % 2 == 0) { // numero pair
                    intRet = 2; // décalage à gauche
                }
            } else if (numero_debut % 2 == 1 && numero_fin % 2 == 1) { // coté gauche = numeros impairs
                if (numero % 2 == 1) { // numero impair
                    intRet = 2; // décalage à gauche
                }
            }
        }
        if (numero_debut > numero_fin) { // inversion du décalage car la géométrie de la voie est inversée dans le référentiel.
            intRet = (intRet > 0) ? 3 - intRet : 0;
        }

        return intRet;
    }

    private static Coordinate projettePoint(Coordinate start, Coordinate end, double x, double y, double distance, int coteDecalage) {
        Coordinate res = new Coordinate();

        double angle = Math.acos((end.x - start.x) / Math.sqrt((Math.pow(start.x - end.x, 2)) + (Math.pow(start.y - end.y, 2))));

        if (end.y < start.y) {
            angle = -angle;
        }

        if (distance > 0 && coteDecalage == 1) {// décalage à droite
            res.x = x + (distance * (Math.cos(angle - Math.PI / 2)));
            res.y = y + (distance * (Math.sin(angle - Math.PI / 2)));
        } else if (distance > 0 && coteDecalage == 2) { // décalage à gauche
            res.x = x + (distance * (Math.cos(angle + Math.PI / 2)));
            res.y = y + (distance * (Math.sin(angle + Math.PI / 2)));
        } else { // pas de décalage ou distance égale ou inférieure à 0
            res.x = x;
            res.y = y;
        }

        // PAS DE RESULTAT NAN
        if (res.x == Double.NaN) {
            res.x = x;
        }
        if (res.y == Double.NaN) {
            res.y = y;
        }

        return res;
    }
}
