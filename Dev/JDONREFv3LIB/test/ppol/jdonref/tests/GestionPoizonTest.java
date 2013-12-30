/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.tests;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.jdom.JDOMException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.JDONREFv3Exception;
import ppol.jdonref.dao.PoizonBean;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.referentiel.GestionMiseAJour;
import ppol.jdonref.referentiel.GestionReferentiel;
import ppol.jdonref.poizon.GestionPoizon;

/**
 *
 * @author marcanhe
 */
public class GestionPoizonTest {

    private GestionConnection gc;
    private GestionMots gm;
    private JDONREFParams params = new JDONREFParams();

    public GestionPoizonTest() {
        try {
            final String filepath = "C:\\JDONREF_v3\\Dev\\Src\\JPOIZONREFv3\\web\\META-INF\\";
            params.load(filepath, "params.xml");
            gc = new GestionConnection(params);
            gc.load("connections.xml");
            // Initialise les mots.
            gm = new GestionMots();
            gm.initMots(filepath + "abbreviations.xml", filepath + "cles.xml", filepath + "prenoms.txt");

            // Définit le gestionnaire de mise à jour
            final GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);

            // Définit le gestionnaire de référentiel.
            final GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);

            // Fait le lien entre le gestionnaire de mots et le gestionnaire de référentiel.
            gm.definitGestionReferentiel(referentiel);

            // Initialise les paramètres de gestionMots.
            gm.definitJDONREFParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of geocodeInverse method, of class GestionPoizon.
     */
    @Test
    public void geocodeInverse() throws JDOMException, ClassNotFoundException, IOException, JDONREFv3Exception {
        System.out.println("geocodeInverse");
        int[] services = {101};
        String[] position = {"652013.0", "6863430.0"};
        double distance = 1.0;
        final Calendar calendar = GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        int projection = 2154;
        GestionPoizon instance = new GestionPoizon(params, gm, gc);
        String id1 = "15";
        final List<PoizonBean> result = instance.geocodeInverse(services, position, distance, date, projection);
        assertTrue(result.size() > 0);
        assertEquals(id1, result.get(0).getId1());
        // LatLong
        position = new String[]{"2.34582766874071", "48.8691088865812"};
        projection = 4326;
        result.clear();
        result.addAll(instance.geocodeInverse(services, position, distance, date, projection));
        assertTrue(result.size() > 0);
        assertEquals(id1, result.get(0).getId1());
        assertTrue(result.get(0).getDistance() <= distance);
    }

    /**
     * Test of geocode method, of class GestionPoizon.
     */
    @Test
    public void geocode() throws JDOMException, ClassNotFoundException, IOException, JDONREFv3Exception {
        System.out.println("geocode");
        int[] services = {101};
        String[] donnees = {"MAGASIN MIKATEX", "", "", "16 RUE DU SENTIER", "", "75002 PARIS", null};
        String[] ids = {"15", null, null, "", null, "", null};
        final Calendar calendar = GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        int projection = 2154;
        GestionPoizon instance = new GestionPoizon(params, gm, gc);
        String x = "652012.999999987";
        String y = "6863429.99999848";
        List<PoizonBean> result = instance.geocode(services, donnees, ids, date, projection);
        assertTrue(result.size() > 0);
        assertEquals(x, String.valueOf(result.get(0).getGeometrie().getCentroid().getX()));
        assertEquals(y, String.valueOf(result.get(0).getGeometrie().getCentroid().getY()));
    }

    @Test
    public void valide() throws JDOMException, ClassNotFoundException, IOException, JDONREFv3Exception {
        System.out.println("valide");
        int[] services = {101};
        int operation = 64;
        String[] donnees = {"L ATELIER MIKATE"};
        final Calendar calendar = GregorianCalendar.getInstance();
        Date date = calendar.getTime();

        GestionPoizon instance = new GestionPoizon(params, gm, gc);
        final List<PoizonBean> result = instance.valide(services, operation, donnees, new String[0], false, date);
        assertTrue(result.size() > 0);
        assertEquals("ATELIER MIKATEX", result.get(0).getDonnee1());
    }

    @Test
    public void revalide() throws JDOMException, ClassNotFoundException, IOException, JDONREFv3Exception {
        System.out.println("revalide");
        int[] services = {101};
        String[] ids = {"9"};
        final Calendar calendar = GregorianCalendar.getInstance();
        Date dateParam = calendar.getTime();

        GestionPoizon instance = new GestionPoizon(params, gm, gc);
        final List<PoizonBean> result = instance.revalide(services, ids, dateParam, null);
        assertTrue(result.size() > 0);
        assertEquals("ATELIER MIKATO", result.get(0).getDonnee1());
    }

    @Test
    public void decoupe() throws JDOMException, ClassNotFoundException, IOException, JDONREFv3Exception {
        System.out.println("decoupe");
        int[] services = {100};
        int[] operations = {524288, 1048576};
        String[] donnees = {"ARDT II"};
        GestionPoizon instance = new GestionPoizon(params, gm, gc);
        final List<PoizonBean> result = instance.decoupe(services, operations, donnees);
        assertTrue(result.size() > 0);
        assertEquals("ARRONDISSEMENT", result.get(0).getDonnees()[0]);
        assertEquals("II", result.get(0).getDonnees()[1]);
        assertEquals(services[0], result.get(0).getService());
    }
}
