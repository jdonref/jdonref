/*
 * GestionInverseTests.java
 *
 * Created on 10 janvier 2011
 * 
 * Version 2.1.8 – Janvier 2011
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
package ppol.jdonref.tests;

import ppol.jdonref.referentiel.reversegeocoding.InverseOption;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import org.jdom.JDOMException;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import org.junit.Test;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.referentiel.reversegeocoding.GeocodageInverse;
import ppol.jdonref.referentiel.reversegeocoding.GeocodageInverse_Commune;
import ppol.jdonref.referentiel.reversegeocoding.GeocodageInverse_Departement;
import ppol.jdonref.referentiel.reversegeocoding.GeocodageInverse_Pays;
import ppol.jdonref.referentiel.reversegeocoding.GeocodageInverse_Troncon;
import ppol.jdonref.referentiel.reversegeocoding.GeocodageInverse_Voie;
import static org.junit.Assert.*;

/**
 *
 * @author Julien
 */
public class GestionInverseTests {
    
    
    
    
    public void testinverseTronconsIntersections(GestionInverse gi, int projection, InverseOption io, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inverseTronconsIntersections(1, io, db1, projection, now);

    }

    @Test
    public void testinverseTronconsIntersections() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);

        GeometryFactory gf = new GeometryFactory();
        InverseOption io = new InverseOption();

//        long distance = 1;
//        String code_dpt = "75";
//        String voi_id = "751054649";
//        io.dpt=code_dpt;
//        io.voi_id=voi_id;
        
        String[] options = {"ids4=5915"};
        io = gi.extractOptionsIds4(options, db1);

        testinverseTronconsIntersections(gi, 2154, io, db1);
    
    }
    
    
    
    
    
    
    
    
    
    

    public void testInverseTroncon(GestionInverse gi, String code_dpt, String ligne4_attendue, String ligne6_attendue, Point pt, double distance, int projection, InverseOption io, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inverseTroncon(1, code_dpt, pt, distance, now, projection, io, db1);

        for (int i = 0; i < res.length; i++) {
            String ligne6 = ((GeocodageInverse_Troncon) res[i]).getLigne6();
            String ligne4 = ((GeocodageInverse_Troncon) res[i]).getLigne4();
            if (ligne6.equals(ligne6_attendue) && ligne4.equals(ligne4_attendue)) {
                return;
            }
        }

        double x = pt.getCoordinates()[0].x;
        double y = pt.getCoordinates()[0].y;
        throw (new Exception("La voie " + ligne4_attendue + " " + ligne6_attendue + " n'a pas été trouvé par reverse geocoding de (" + x + "," + y + ") sur une distance " + distance));
    }

    @Test
    public void testsInverseTroncon() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);

        GeometryFactory gf = new GeometryFactory();
        InverseOption io = new InverseOption();

        
        Coordinate c = new Coordinate(653950.290, 6859816.89);
        Point pt = gf.createPoint(c);
        long distance = 1;
        String code_dpt = "75";
        String ligne4 = "15 BOULEVARD VINCENT AURIOL";
        String ligne6 = "75013 PARIS";

        testInverseTroncon(gi, code_dpt, ligne4, ligne6, pt, distance, 2154, io, db1);
        
        Coordinate c1 = new Coordinate(652494.044, 6859869.69);
        Point pt1 = gf.createPoint(c1);
        long distance1 = 1;
        String code_dpt1 = "75";
        String ligne41 = "24 BOULEVARD SAINT MARCEL";
        String ligne61 = "75013 PARIS";

        testInverseTroncon(gi, code_dpt1, ligne41, ligne61, pt1, distance1, 2154, io, db1);
      
        Coordinate c2 = new Coordinate(652045.120, 6859755.82);
        Point pt2 = gf.createPoint(c2);
        long distance2 = 1;
        String code_dpt2 = "75";
        String ligne42 = "24 RUE SAINT HIPPOLYTE";
        String ligne62 = "75013 PARIS";

        testInverseTroncon(gi, code_dpt2, ligne42, ligne62, pt2, distance2, 2154, io, db1);
        
        Coordinate c3 = new Coordinate(600114.105, 2426027.88);
        Point pt3 = gf.createPoint(c3);
        long distance3 = 1;
        String code_dpt3 = "75";
        String ligne43 = "24 BOULEVARD SAINT JACQUES";
        String ligne63 = "75014 PARIS";

        testInverseTroncon(gi, code_dpt3, ligne43, ligne63, pt3, distance3, 2192, io, db1);
        
        Coordinate c4 = new Coordinate(2.33878248, 48.8326175);
        Point pt4 = gf.createPoint(c4);
        long distance4 = 1;
        String code_dpt4 = "75";
        String ligne44 = "24 BOULEVARD SAINT JACQUES";
        String ligne64 = "75014 PARIS";

        testInverseTroncon(gi, code_dpt4, ligne44, ligne64, pt4, distance4, 4030, io, db1);
        

    }
    
    /*
    public void testInversePlaque(GestionInverse gi, String code_dpt, String ligne4_attendue, String ligne6_attendue, Point pt, double distance, int projection, InverseOption io, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inversePlaque(1, code_dpt, pt, distance, now, projection ,io, db1);
        /*
        for (int i = 0; i < res.length; i++)
        {
        String ligne6 = ((GeocodageInverse_Plaque) res[i]).getLigne6();
        String ligne4 = ((GeocodageInverse_Plaque) res[i]).getLigne4();
        if (ligne6.equals(ligne6_attendue) && ligne4.equals(ligne4_attendue))
        {
        return ;
        }
        }*/
        /*
        for (int i = 0; i < res.length; i++) {
            System.out.print(((GeocodageInverse_Plaque) res[i]).getLigne4());
            System.out.print(" ");
            System.out.println(((GeocodageInverse_Plaque) res[i]).getLigne6());
        }
        if (res.length > 0) {
            return;
        }

        double x = pt.getCoordinates()[0].x;
        double y = pt.getCoordinates()[0].y;
        throw (new Exception("La voie " + ligne4_attendue + " " + ligne6_attendue + " n'a pas été trouvé par reverse geocoding de (" + x + "," + y + ") sur une distance " + distance));
    }

    @Test
    public void testsInversePlaque() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);

        GeometryFactory gf = new GeometryFactory();
        InverseOption io = new InverseOption();

        Coordinate c = new Coordinate(653950.290, 6859816.89);
        Point pt = gf.createPoint(c);
        long distance = 10;
        String code_dpt = "75";
        String ligne4 = "15 BOULEVARD VINCENT AURIOL";
        String ligne6 = "75013 PARIS";
        testInversePlaque(gi, code_dpt, ligne4, ligne6, pt, distance, 2154, io, db1);

    }
      */
    public void testInverseVoie(GestionInverse gi, String code_dpt, String ligne4_attendue, String ligne6_attendue, Point pt, double distance, int projection, InverseOption io, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inverseVoie(1, code_dpt, pt, distance, now, projection,io, db1);

        for (int i = 0; i < res.length; i++) {
            String ligne6 = ((GeocodageInverse_Voie) res[i]).getLigne6();
            String ligne4 = ((GeocodageInverse_Voie) res[i]).getLigne4();
            if (ligne6.equals(ligne6_attendue) && ligne4.equals(ligne4_attendue)) {
                return;
            }
        }

        double x = pt.getCoordinates()[0].x;
        double y = pt.getCoordinates()[0].y;
        throw (new Exception("La voie " + ligne4_attendue + " " + ligne6_attendue + " n'a pas été trouvé par reverse geocoding de (" + x + "," + y + ") sur une distance " + distance));
    }

    @Test
    public void testsInverseVoie() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);

        GeometryFactory gf = new GeometryFactory();
        InverseOption io = new InverseOption();

        Coordinate c = new Coordinate(653950.290, 6859816.89);
        Point pt = gf.createPoint(c);
        long distance = 10;
        String code_dpt = "75";
        String ligne4 = "BOULEVARD VINCENT AURIOL";
        String ligne6 = "75013 PARIS";
        testInverseVoie(gi, code_dpt, ligne4, ligne6, pt, distance, 2154, io, db1);
    }

    public void testInverseCommune(GestionInverse gi, String code_dpt, String code_insee, Point pt, double distance, int projection, InverseOption io, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inverseCommune(1, code_dpt, pt, distance, now, projection, io, db1);

        for (int i = 0; i < res.length; i++) {
            String code = ((GeocodageInverse_Commune) res[i]).getCodeInsee();
            if (code.equals(code_insee)) {
                return;
            }
        }

        double x = pt.getCoordinates()[0].x;
        double y = pt.getCoordinates()[0].y;
        throw (new Exception("La commune " + code_insee + " n'a pas été trouvé par reverse geocoding de (" + x + "," + y + ") sur une distance " + distance));
    }

    @Test
    public void testsInverseCommune() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);
        GeometryFactory gf = new GeometryFactory();
        InverseOption io = new InverseOption();

        Coordinate c = new Coordinate(649227.180, 6857788.86);
        Point pt = gf.createPoint(c);
        long distance = 10;
        String code_insee = "92049";
        String code_dpt = "92";

        testInverseCommune(gi, code_dpt, code_insee, pt, distance, 2154, io, db1);
    }

    public void testInverseDepartement(GestionInverse gi, String dpt, Point pt, double distance, int projection, InverseOption io, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inverseDepartement(1, pt, distance, now, projection, io, db1);

        for (int i = 0; i < res.length; i++) {
            String code = ((GeocodageInverse_Departement) res[i]).getCodeDepartement();
            if (code.equals(dpt)) {
                return;
            }
        }

        double x = pt.getCoordinates()[0].x;
        double y = pt.getCoordinates()[0].y;
        throw new Exception("Le departement " + dpt + " n'a pas été trouvé par reverse geocoding de (" + x + "," + y + ") sur une distance " + distance);
    }

    @Test
    public void testsInverseDepartement() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);

        GeometryFactory gf = new GeometryFactory();
        InverseOption io = new InverseOption();


        Coordinate c = new Coordinate(649227.180, 6857788.86);
        Point pt = gf.createPoint(c);
        long distance = 10;
        String dpt = "92";

        testInverseDepartement(gi, dpt, pt, distance, 2154, io, db1);
    }
    
    public void testInversePays(GestionInverse gi, String sov_a3, Point pt, double distance, int projection, Connection db1) throws SQLException, Exception {
        Date now = Calendar.getInstance().getTime();

        GeocodageInverse[] res = gi.inversePays(1, pt, distance, now, projection, db1);
        
        for (int i = 0; i < res.length; i++) {
            String code = ((GeocodageInverse_Pays) res[i]).getSovA3();
            if (code.equals(sov_a3)) {
                return;
            }
        }

        double x = pt.getCoordinates()[0].x;
        double y = pt.getCoordinates()[0].y;
        throw new Exception("Le pays " + sov_a3 + " n'a pas été trouvé par reverse geocoding de (" + x + "," + y + ") sur une distance " + distance);
    }

    @Test
    public void testsInversePays() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException, Exception {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc = new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        GestionInverse gi = new GestionInverse(params);

        GeometryFactory gf = new GeometryFactory();


        Coordinate c = new Coordinate(649227.180, 6857788.86);
        Point pt = gf.createPoint(c);
        long distance = 400000;
        String sov_a3 = "FR1";

        testInversePays(gi, sov_a3, pt, distance, 2154, db1);
    }
    
    
    
}
