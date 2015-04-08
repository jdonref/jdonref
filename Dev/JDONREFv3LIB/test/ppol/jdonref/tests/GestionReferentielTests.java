/*
 * GestionReferentielTests.java
 *
 * Created on 18 mars 2008, 15:27
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
package ppol.jdonref.tests;

import ppol.jdonref.referentiel.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ppol.jdonref.Processus;

/**
 * Tests des méthodes de GestionReferentiel.<br>
 * Pour ajouter une méthode à tester qui nécessite un traitement par un thread séparé (méthode d'administrations utilisant
 * la classe Processus), il faut modifier les méthodes suivantes:
 * <ul>
 * <li>run</li>
 * <li>main</li>
 * </ul> 
 * et bien sûr créer la méthode de tests et la méthode de test.
 * @author jmoquet
 */
public class GestionReferentielTests implements Runnable
{
    // Ce paramètre permet de savoir quelle méthode exécuter
    // sur un thread séparé.

    int methode = 0; // 0 pour normaliser
    // 1 pour calculeCommunesAmbigueDansVoies
    // 2 pour calculeClesAmbiguesDansCommunes
    // 3 pour miseAJour
    // 4 pour miseAJourIdentifiants
    // 5 pour changementReferentiel
    // 6 pour creeTableVoie
    // 7 pour prepareChangementReferentiel
    // 8 pour phonetise
    // 9 pour prepareMaj
    // 10 pour calculeVoiesAmbigues
    // 11 pour genereFantoires
    // 12 pour decoupe
    // 13 pour genereIdTroncons
    /**
     * Le delai en millisecondes entre chaque mise à jour d'affichage du retour de la méthode
     * d'administration en cour d'exécution.
     */
    static int delai = 5000;
    // Tous ces paramètres sont utilisés pour lancer les tests
    // sur un thread séparé.
    GestionMiseAJour gmaj;
    GestionReferentiel r;
    String code_departement;
    String id;
    String nom_original;
    String nom;
    String table;
    Connection c1;
    Connection c2;
    Processus p;
    Date date;
    boolean idsource;
    private String tabletroncon;
    private String tablevoies;
    private String tableid;
    private String colonneDestination;
    private String colonneSource;
    private String nomTable;
    private String fantoire;
    private String[] champs;
    private String[] decoupages;
    private int[] natures;
    private int[] lignes;
    int flags;
    private int ligne;
    static SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

//    void testChercheCommuneDansChaine(GestionReferentiel referentiel, String chaine, ArrayList<String> codes_departement,
//            String resultat_attendu, Connection connection) throws SQLException, Exception
//    {
//        ArrayList<RefCommune> communes = referentiel.chercheCommuneDansChaine(chaine, codes_departement, connection);
//
//        for(int i = 0; i < communes.size(); i++)
//        {
//            RefCommune commune = communes.get(i);
//            if(resultat_attendu.compareTo(commune.obtientCle().obtientNom()) == 0)
//            {
//                return;
//            }
//        }
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("Cherche les communes de ");
//        for(int i = 0; i < codes_departement.size(); i++)
//        {
//            if(i > 0)
//            {
//                sb.append(",");
//            }
//            sb.append(codes_departement.get(i));
//        }
//        sb.append(" dans ");
//        sb.append(chaine);
//        sb.append(" trouve : ");
//        for(int i = 0; i < communes.size(); i++)
//        {
//            sb.append(communes.get(i).obtientCle().obtientNom());
//            sb.append(' ');
//        }
//
//        throw (new Exception(sb.toString()));
//    }
//
//    @Test
//    public void testsGestionCodesDepartements()
//    {
//        try
//        {
//            JDONREFParams params = new JDONREFParams();
//            params.load("params.xml");
//            GestionConnection gc = new GestionConnection(params);
//            gc.load("connections.xml");
//            Connection db1 = gc.obtientConnection().connection;
//            GestionDescriptionTables.definitJDONREFParams(params);
//
//            // WA 09/2011 : ajout de GestionDepartements
//            GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
//            if(params.isUtilisationDeLaGestionDesDepartements()) // si ce parametre n'est pas a true : tous les test de corse et domtom echoueront.
//            {
//                gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//
//                junit.framework.Assert.assertTrue("GestionCodesDepartements n'est pas initialise.", gestDpt.isInitialized());
//
//                junit.framework.Assert.assertEquals("75", gestDpt.getOfficialCodeDpt("75"));
//                if (gestDpt.isDptCodePresent("20 A"))
//                {
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.getOfficialCodeDpt("2 A"));
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.getOfficialCodeDpt("20 A"));
//                }
//                if (gestDpt.isDptCodePresent("20 B"))
//                {
//                    junit.framework.Assert.assertEquals("20 B", gestDpt.getOfficialCodeDpt("2 B"));
//                    junit.framework.Assert.assertEquals("20 B", gestDpt.getOfficialCodeDpt("20 B"));
//                }
//
//                junit.framework.Assert.assertTrue("Departement 75 non present dans la liste des dpts.", gestDpt.isDptCodePresent("75"));
//                junit.framework.Assert.assertFalse("Departement 8 3 non present dans la liste des dpts.", gestDpt.isDptCodePresent("8 3"));
//
//                Pattern dptsPattern = gestDpt.getDptsPattern();
//
//                Matcher matcher;
//                if (gestDpt.isDptCodePresent("83"))
//                {
//                    matcher = dptsPattern.matcher("blahh 83");
//                    junit.framework.Assert.assertTrue(matcher.find());
//                    junit.framework.Assert.assertEquals(matcher.group(), "83");
//                }
//
//                matcher = dptsPattern.matcher("75 blahh");
//                junit.framework.Assert.assertTrue(matcher.find());
//                junit.framework.Assert.assertEquals(matcher.group(), "75");
//                junit.framework.Assert.assertEquals("75", gestDpt.extractCodeDptFromString("75001 PARIS").obtientMot());
//                junit.framework.Assert.assertEquals("75", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("75"));
//                junit.framework.Assert.assertEquals("75", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("75116"));
//                junit.framework.Assert.assertEquals("75", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("751"));
//
//                matcher = dptsPattern.matcher("2 A rue truc blahh 2 B");
//                if (gestDpt.isDptCodePresent("20 A"))
//                {
//                    junit.framework.Assert.assertTrue(matcher.find());
//                    junit.framework.Assert.assertEquals("2 A", matcher.group());
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.extractCodeDptFromString("20001").obtientMot());
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.extractCodeDptFromString(" X 20001").obtientMot());
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.extractCodeDptFromString(" X 20001 AJACCIO").obtientMot());
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.extractCodeDptFromString("20001 AJACCIO").obtientMot());
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("20 A"));
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("2 A"));
//                    junit.framework.Assert.assertEquals("20 A", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("20001"));
//                    matcher = dptsPattern.matcher("20 A");
//                    junit.framework.Assert.assertTrue(matcher.find());
//                    junit.framework.Assert.assertEquals(matcher.group(), "20 A");
//                
//                    matcher = dptsPattern.matcher("turlututu 20 A toto tete");
//                    junit.framework.Assert.assertTrue(matcher.find());
//                    junit.framework.Assert.assertEquals(matcher.group(), "20 A");
//                }
//                if (gestDpt.isDptCodePresent("20 B"))
//                {
//                    junit.framework.Assert.assertTrue(matcher.find());
//                    junit.framework.Assert.assertEquals("2 B", matcher.group());
//                    junit.framework.Assert.assertEquals("20 B", gestDpt.extractCodeDptFromString(" X 20801 AJACCIO").obtientMot());
//                    junit.framework.Assert.assertEquals("20 B", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("20 B"));
//                    junit.framework.Assert.assertEquals("20 B", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("2 B"));
//                }
//                if (gestDpt.isDptCodePresent("72"))
//                {
//                    junit.framework.Assert.assertEquals("72", gestDpt.extractCodeDptFromString("72001").obtientMot());
//                    junit.framework.Assert.assertEquals("72", gestDpt.extractCodeDptFromString("72001 PARIS").obtientMot());
//                }
//                if (gestDpt.isDptCodePresent("05"))
//                {
//                    junit.framework.Assert.assertEquals("05", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("05130"));
//                }
//                if (gestDpt.isDptCodePresent("80"))
//                {
//                    junit.framework.Assert.assertEquals("80", gestDpt.computeCodeDptFromCodeInseeOrCodeDpt("80756"));
//                }
//            }
//        } catch(Exception ex)
//        {
//            ex.printStackTrace();
//            junit.framework.TestCase.fail();
//        }
//        System.out.println("----------------------------------------------------");
//    }
//
//    @Test
//    public void testsChercheCommuneDansChaine() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException,
//            GestionMotsException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc = new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        ArrayList<String> departements = new ArrayList<String>();
//        departements.add("75");
//        testChercheCommuneDansChaine(referentiel, "RUE DE PARIS", departements, "PARIS", db1);
//
//        // Test pertinent si le département 59 est présent en base.
//        //departements.add("59");
//        //testChercheCommuneDansChaine(referentiel,"BOULEVARD DE DOUAI PARIS 75000",departements,"DOUAI",db1);
//    }
//
//    void testGenereIdTroncon(GestionReferentiel r, String code_departement, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//
//        grt.code_departement = code_departement;
//        grt.c1 = c1;
//        grt.methode = 13;
//        grt.r = r;
//        grt.p = p;
//
//        launch(grt);
//    }
//
//    //@Test
//    // Test unitaire à modifier : il faut créer un échantillon test
//    public void testsGenereIdTroncon() throws ClassNotFoundException, SQLException, JDOMException, IOException, JDONREFException,
//            GestionMotsException, GestionReferentielException
//    {
//        // Nécessaire pour le chargement de la classe gérant la connection avec une base postgresql.
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc = new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        testGenereIdTroncon(referentiel, "75", db1);
//    }
//
//    void testCreeTableVoie(GestionMiseAJour gmaj, String code_departement, String tabletroncon, String tablevoies, String tableidvoies,
//            Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//
//        grt.tabletroncon = tabletroncon;
//        grt.tablevoies = tablevoies;
//        grt.tableid = tableidvoies;
//        grt.code_departement = code_departement;
//        grt.c1 = c1;
//        grt.methode = 6;
//        grt.gmaj = gmaj;
//        grt.p = p;
//
//        p.state = new String[]
//                {
//                    "EN COURS", "CREATION", "TABLE VOIE", "LANCEMENT", "TRONCON TRAITES", "0", "SUR 0"
//                };
//        launch(grt);
//    }
//
//    //@Test
//    // Test unitaire à modifier : il faut créer un échantillon test
//    public void testsCreeTableVoie() throws ClassNotFoundException, SQLException, GestionReferentielException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc = new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        testCreeTableVoie(gmaj, "92", "tro_troncons_92", "voi_voies_92", "idv_id_voies", db1);
//    }
//
//    //@Test
//    // Test unitaire à modifier : il faut créer un échantillon test
//    public void testAjouteMarqueur() throws SQLException, ColonneException, ClassNotFoundException, GestionReferentielException,
//            JDOMException, IOException, JDONREFException, GestionMotsException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc = new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        // Nettoye.
//        GestionTables.supprimeTable("test2.table1", db1);
//
//        // Crée la table table1 sur la première connection:
//        // nom CHARACTER(80)
//        // date TIMESTAMP
//
//        ArrayList<Colonne> cs = new ArrayList<Colonne>();
//        Colonne c0 = null, c1 = null;
//
//        c0 = new Colonne("nom", "CHARACTER", 80);
//        c1 = new Colonne("date", "TIMESTAMP", 0);
//
//        cs.add(c0);
//        cs.add(c1);
//
//        GestionTables.creeTable("test2.table1", cs, db1);
//
//        // Essaye d'ajouter un marqueur
//        String marqueur1 = GestionMarqueurs.ajouteMarqueur("test2.table1", db1);
//
//        // Essaye d'ajouter un marqueur
//        String marqueur2 = GestionMarqueurs.ajouteMarqueur("test2.table1", db1);
//
//        // Supprime le marqueur
//        GestionMarqueurs.supprimeMarqueur("test2.table1", marqueur1, db1);
//
//        // Essaye d'ajouter un marqueur
//        String marqueur3 = GestionMarqueurs.ajouteMarqueur("table1", db1);
//
//        decritTable("test2.table1", db1);
//    }
//
//    /**
//     * Affiche sur la sortie standard la description de la table.
//     * @throws GestionReferentielException plusieurs shemas contiennent une table de ce nom.
//     */
//    private static void decritTable(String name, Connection c) throws GestionReferentielException
//    {
//        System.out.println("\r\nObtiens la description de la table " + name + ":");
//
//        DescriptionTable dt = null;
//
//        try
//        {
//            dt = GestionTables.obtientDescription(name, c); // GestionReferentielException
//
//            for(int i = 0; i < dt.getCount(); i++)
//            {
//                System.out.println(dt.getColonne(i).getNom() + " " + dt.getColonne(i).getType() + "(" + dt.getColonne(i).getLength() + ")");
//            }
//        } catch(SQLException ex)
//        {
//            ex.printStackTrace();
//        }
//    }
//
//    //@Test
//    // Test unitaire à modifier : il faut créer un échantillon test
//    public static void testsMajStructure() throws SQLException, GestionReferentielException, JDOMException, ClassNotFoundException,
//            IOException, JDONREFException, GestionMotsException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        GestionTables.supprimeTable("table1", db1);
//        GestionTables.supprimeTable("table2", db2);
//
//        // Crée la table table1 sur la première connection:
//        // nom CHARACTER(80)
//        // date TIMESTAMP
//
//        ArrayList<Colonne> cs = new ArrayList<Colonne>();
//        Colonne c0 = null, c1 = null;
//        try
//        {
//            c0 = new Colonne("nom", "CHARACTER", 80);
//            c1 = new Colonne("date", "TIMESTAMP", 0);
//        } catch(ColonneException ex)
//        {
//            ex.printStackTrace();
//        }
//
//        cs.add(c0);
//        cs.add(c1);
//        try
//        {
//
//            GestionTables.creeTable("table1", cs, db1);
//        } catch(SQLException ex)
//        {
//            ex.printStackTrace();
//        }
//
//        // Essaye de mettre à jour la structure de la table1 dans la connection 2 avec pour nom table2
//        try
//        {
//
//            GestionStructure.majStructure("table1", db1, "table2", db2, true);
//        } catch(GestionReferentielException ex)
//        {
//            ex.printStackTrace();
//        } catch(SQLException ex)
//        {
//            ex.printStackTrace();
//        }
//
//        // Vérifie l'existence de table2 dans la deuxième connection.
//        try
//        {
//            if(GestionTables.tableExiste("table2", db2))
//            {
//                System.out.println("La table existe");
//            } else
//            {
//                System.out.println("La table n'existe pas.");
//            }
//        } catch(SQLException sqle)
//        {
//            sqle.printStackTrace();
//        }
//
//        // Ajoute des colonnes à table1.
//        Colonne c = null;
//
//        try
//        {
//            c = new Colonne("col3", "CHARACTER", 38);
//        } catch(ColonneException ce)
//        {
//        }
//        try
//        {
//            GestionTables.ajouteColonne("table1", c, db1);
//        } catch(SQLException se)
//        {
//            se.printStackTrace();
//        }
//
//        // Met à jour la table 2
//        try
//        {
//
//            GestionStructure.majStructure("table1", db1, "table2", db2, true);
//        } catch(GestionReferentielException ex)
//        {
//            ex.printStackTrace();
//        } catch(SQLException ex)
//        {
//            ex.printStackTrace();
//        }
//
//        // Obtiens la description de la table table2:
//
//        DescriptionTable dt = null;
//
//        try
//        {
//            dt = GestionTables.obtientDescription("table2", db2);
//
//            for(int i = 0; i < dt.getCount(); i++)
//            {
//                System.out.println(dt.getColonne(i).getNom() + " " + dt.getColonne(i).getType() + "(" + dt.getColonne(i).getLength() + ")");
//            }
//        } catch(SQLException ex)
//        {
//            ex.printStackTrace();
//        }
//    }
//
//    void testCalculeVoiesAmbigues(GestionMiseAJour gmaj, String code_departement, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.methode = 10;
//        grt.p = p;
//        grt.gmaj = gmaj;
//        grt.code_departement = code_departement;
//        grt.c1 = c1;
//
//        new Thread(grt).start();
//    }
//
//    //@Test
//    // Test unitaire à modifier : il faut créer un échantillon test
//    public void testsCalculVoiesAmbigues() throws ClassNotFoundException, SQLException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        testCalculeVoiesAmbigues(gmaj, "92", db1);
//        testCalculeVoiesAmbigues(gmaj, "93", db1);
//        testCalculeVoiesAmbigues(gmaj, "94", db1);
//
//        db1.close();
//    }
//
//    private static void testCalculeCommunesAmbigueDansVoies(GestionMiseAJour gmaj, String code_departement, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "ATTENTE"
//                };
//
//        GestionReferentielTests tests = new GestionReferentielTests();
//        tests.gmaj = gmaj;
//        tests.c1 = c1;
//        tests.code_departement = code_departement;
//        tests.p = p;
//        tests.methode = 1;
//
//        launch(tests);
//    }
//
//    void testCalculeClesAmbiguesDansCommunes(GestionMiseAJour gmaj, String code_departement, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "ATTENTE"
//                };
//
//        GestionReferentielTests tests = new GestionReferentielTests();
//        tests.gmaj = gmaj;
//        tests.code_departement = code_departement;
//        tests.c1 = c1;
//        tests.p = p;
//        tests.methode = 2;
//
//        launch(tests);
//    }
//
//    //@Test
//    // Test unitaire à modifier : il faut créer un échantillon test 
//    public void testsCalculeCommunesAmbigues() throws ClassNotFoundException, SQLException, GestionReferentielException,
//            GestionMotsException, JDOMException, IOException, JDONREFException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        testCalculeCommunesAmbigueDansVoies(gmaj, "75", db1);
//        testCalculeClesAmbiguesDansCommunes(gmaj, "75", db1);
//        testCalculeCommunesAmbigueDansVoies(gmaj, "92", db1);
//        testCalculeClesAmbiguesDansCommunes(gmaj, "92", db1);
//        testCalculeCommunesAmbigueDansVoies(gmaj, "93", db1);
//        testCalculeClesAmbiguesDansCommunes(gmaj, "93", db1);
//        testCalculeCommunesAmbigueDansVoies(gmaj, "94", db1);
//        testCalculeClesAmbiguesDansCommunes(gmaj, "94", db1);
//
//        db1.close();
//    }
//
//    //@Test
//    // Test unitaire à modifier :
//    //  il faut créer un échantillon test
//    //  il faut vérifier le retour de la fonction
//    public void testsChercheVoie() throws ClassNotFoundException, SQLException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        ResultSet rs = db1.getMetaData().getTypeInfo();
//
//        // Obtiens la géométrie de la voie de la table tronconsbdhopital
//        ArrayList<Geometry> geometries = referentiel.obtientGeometries("10", "tronconbdhopital", db1);
//
//        // Cherche les voies correspondant à BOULEVARD DE L'HOPITAL 75113 pour ces géométries
//        ArrayList<String> voies = referentiel.chercheCorrespondanceVoie("BOULEVARD DE L HOPITAL", "75113", geometries, "voies75",
//                "troncons75", db1);
//
//
//        db1.close();
//    }
//
//    private static void testCreeFantoire(GestionReferentiel r, String nomTable, String id, String fantoire, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.p = p;
//        grt.r = r;
//        grt.methode = 11;
//        grt.c1 = c1;
//        grt.id = id;
//        grt.nomTable = nomTable;
//        grt.fantoire = fantoire;
//
//        launch(grt);
//    }
//
//    //@Test
//    // Test unitaire à modifier :
//    //  il faut créer un échantillon test
//    //  il faut vérifier le retour de la fonction
//    public void testsCreeFantoire() throws ClassNotFoundException, SQLException, GestionReferentielException, JDOMException, IOException,
//            JDONREFException, GestionMotsException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        testCreeFantoire(referentiel, "idvoies", "voi_id", "fantoire", db1);
//
//        db1.close();
//    }
//
//    /**
//     * 
//     * @param r
//     * @param nomTable
//     * @param id
//     * @param champs
//     * @param decoupages
//     * @param natures
//     * @param c1 la connection au référentiel
//     * @param c2
//     */
//    void testDecoupe(GestionReferentiel r, String nomTable, String id, String[] champs, String[] decoupages, int[] natures, int[] lignes,
//            Connection c1, Connection c2)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//        p.name = "test";
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.methode = 12;
//        grt.p = p;
//        grt.r = r;
//        grt.nomTable = nomTable;
//        grt.champs = champs;
//        grt.decoupages = decoupages;
//        grt.natures = natures;
//        grt.c1 = c1;
//        grt.c2 = c2;
//        grt.id = id;
//        grt.lignes = lignes;
//
//        launch(grt);
//    }
//
//    //@Test
//    // Test unitaire à modifier :
//    //  il faut créer un échantillon test
//    //  il faut vérifier le retour de la fonction
//    public void testsDecoupe() throws ClassNotFoundException, SQLException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        testDecoupe(referentiel, "voi_voies_01", "oid", new String[]
//                {
//                    "voi_nom_desab"
//                }, new String[]
//                {
//                    "voi_type_de_voie", "voi_lbl", "voi_mot_determinant"
//                }, new int[]
//                {
//                    8, 32, 64
//                }, new int[]
//                {
//                    4
//                }, db1, db1);
//
//        db1.close();
//        db2.close();
//    }
//
//    void testsGeocodeAdresse(String voi_id, int numero, char repetition, String code_insee, Date date, String[] resultat_attendu,
//            Connection connection) throws SQLException, GestionReferentielException, Exception
//    {
//        System.out.println();
//        
//        String[] res = new GestionGeocodage(new JDONREFParams()).geocodeAdresse(2, voi_id, numero, repetition, code_insee, null, 2154, null, date, connection);
//
//        if(res[0].compareTo("0") == 0)
//        {
//            throw (new Exception("Erreur " + res[1] + " : " + res[2]));
//        } else
//        {
//            if(res[1].compareTo("1") == 0)
//            {
//                if(!((res[3].equals(resultat_attendu[0]) && res[4].equals(resultat_attendu[1]))))
//                {
//                    throw (new Exception("Test de géocodage de la voie " + voi_id + " au numero " + numero + " " + repetition + " dans " + code_insee + " à " + sdformat.
//                            format(date) + " a retourné (" + res[3] + "," + res[4] + ")"));
//                }
//            } else
//            {
//                throw (new Exception("géocodage impossible."));
//            }
//        }
//    }
//
//    void testsGeocode(String voi_id, String ligne4, String code_insee, Date date, String[] resultat_attendu,
//            Connection connection, GestionReferentiel referentiel) throws SQLException, GestionReferentielException, Exception
//    {
//        System.out.println();
//
//        String dateStr = DateUtils.formatDateToStringSimpleSlashed(date);
//        String[] res = referentiel.geocode(2, voi_id, ligne4, code_insee, dateStr, 2154, connection);
//
//        if(res[0].equals("0"))
//        {
//            throw (new Exception("Erreur " + res[1] + " : " + res[2]));
//        } else
//        {
//            if(res[1].equals("1"))
//            {
//                if(!((res[3].equals(resultat_attendu[0]) && res[4].equals(resultat_attendu[1]))))
//                {
//                    throw (new Exception("Test de géocodage de " + voi_id + " , " + code_insee + " , " + ligne4 + " à " + sdformat.format(
//                            date) + " a retourné (" + res[3] + "," + res[4] + ")"));
//                }
//            } else
//            {
//                throw (new Exception("géocodage impossible."));
//            }
//        }
//    }
//
//    @Test
//    public void testsGeocode() throws ClassNotFoundException, SQLException, GestionReferentielException, java.text.ParseException,
//            JDOMException, IOException, JDONREFException, GestionMotsException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        Date dt = Calendar.getInstance().getTime();
//
//        // Geocodage au département:
//        String[] res = new String[2];
//        // Adaptation referentiel BSPP mai 2011
//        //res[0] = "651782.39";
//        //res[1] = "6862043.28";
//        res[0] = "651780.745"; // "651780.74"; avant passage a 9 chiffres significatifs
//        res[1] = "6862042.77";
//        testsGeocodeAdresse((String) null, 0, (char) 0, "75", dt, res, db1);
//        testsGeocodeAdresse((String) null, 0, (char) 0, "751", dt, res, db1);
//        
//        // Geocodage à la commune:
//        // NB: mon référentiel ne dispose pas de la géométrie
//        // des arrondissements... le géocodage est alors systématiquement effectué
//        // au niveau département.
//        // Adaptation referentiel BSPP mai 2011
//        //res[0] = "651782.39";
//        //res[1] = "6862043.28";
//        res[0] = "648096.216";   // "648096.22"; avant passage a 9 chiffres significatifs
//        res[1] = "6860238.09";
//        testsGeocodeAdresse((String) null, 0, (char) 0, "75115", dt, res, db1);
//        // Adaptation referentiel BSPP mai 2011
//        res[0] = "645850.971";  // "645850.97"; avant passage a 9 chiffres significatifs
//        res[1] = "6862515.34";
//        testsGeocodeAdresse((String) null, 0, (char) 0, "75116", dt, res, db1);
//        // Adaptation referentiel BSPP mai 2011
//        res[0] = "649165.755";  // "649165.75"; avant passage a 9 chiffres significatifs
//        res[1] = "6865478.90";   // "6865478.9"; avant passage a 9 chiffres significatifs
//        testsGeocodeAdresse((String) null, 0, (char) 0, "75117", dt, res, db1);
//        // Adaptation referentiel BSPP mai 2011
//        res[0] = "652206.791";   // "652206.79"; avant passage a 9 chiffres significatifs
//        res[1] = "6866035.98";
//        testsGeocodeAdresse((String) null, 0, (char) 0, "75118", dt, res, db1);
//        testsGeocodeAdresse((String) null, 0, (char) 0, "75118", dt, res, db1);
//        // Adaptation referentiel BSPP mai 2011
//        //res[0] = "658935.29";
//        //res[1] = "6867639.47";
//        res[0] = "658940.209";   // "658940.21"; avant passage a 9 chiffres significatifs
//        res[1] = "6867639.00";   // "6867639.0"; avant passage a 9 chiffres significatifs
//        testsGeocodeAdresse((String) null, 0, (char) 0, "93008", dt, res, db1);
//
//        // Geocodage au numéro:
//        // Adaptation referentiel BSPP mai 2011 puis db_navteq2005_bspp2011_sshisto
//        //res[0] = "601991.28";
//        //res[1] = "1126882.81";
//        res[0] = "653223.834";  // "653223.83"; avant passage a 9 chiffres significatifs
//        res[1] = "6860288.49";
//        //testsGeocode("1802220246495210702",28,(char)0,"75",dt,res,db1);
//        //testsGeocode("5439",28,(char)0,"75",dt,res,db1);  // Le troncon correspondant est absent du referentiel ...
////        testsGeocode("5439",24,(char)0,"75",dt,res,db1);
//        testsGeocodeAdresse("-3894215581059364001", 24, (char) 0, "75", dt, res, db1);
//        //res[0] = "601886.84";
//        //res[1] = "1126668.76";
//        res[0] = "653223.834";  // "653223.83"; avant passage a 9 chiffres significatifs
//        res[1] = "6860288.49";
//        //testsGeocode("1802220246495210702",30,(char)0,"75",dt,res,db1);
////        testsGeocode("5439",40,(char)0,"75",dt,res,db1);
//        testsGeocodeAdresse("-3894215581059364001", 40, (char) 0, "75", dt, res, db1);
//        //res[0] = "601848.25";
//        //res[1] = "1128112.62";
//        res[0] = "653162.169";  // "653162.17"; avant passage a 9 chiffres significatifs
//        res[1] = "6861680.99";
//        //testsGeocode("-7055169325780383101",25,(char)0,"75",dt,res,db1);
////        testsGeocode("5262",25,(char)0,"75",dt,res,db1);
//        testsGeocodeAdresse("-5042612625379779568", 25, (char) 0, "75", dt, res, db1);
//        //res[0] = "601848.25";
//        //res[1] = "1128112.62";
//        res[0] = "653162.169";  // "653162.17"; avant passage a 9 chiffres significatifs
//        res[1] = "6861680.99";
//        //testsGeocode("-7055169325780383101",10,(char)0,"75",dt,res,db1);
////        testsGeocode("5262",10,(char)0,"75",dt,res,db1);
//        testsGeocodeAdresse("-5042612625379779568", 10, (char) 0, "75", dt, res, db1);
//
//        // Nécessite l'existence des points adresses pour le 75
//        // et notamment le numéro 12 de la rue SAINT PAUL (non vérifié)
//        //
//        // INSERT INTO adr_adresses_75(
//        //    adr_id, adr_rep, voi_id, adr_numero, t0, t1, geometrie)
//        //    VALUES ('1', null, '-7055169325780383101', 12, '2009-01-01', '2059-01-01',
//        //    geomFromText('POINT(601848.25 1128112.62)',27571));
//        if(GestionTables.tableExiste("adr_adresses_75", db1))
//        {
//            res[0] = "601848.25";
//            res[1] = "1128112.62";
//            testsGeocodeAdresse("-7055169325780383101", 12, (char) 0, "75", dt, res, db1);
//        }
//
//        // WA 09/2011 Corse les donnees du 20 A ont ete recuperees depuis le 04
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            res[0] = "930143.893";  // "930143.89"; avant passage a 9 chiffres significatifs
//            res[1] = "6307430.51";
//            testsGeocodeAdresse("4917884613483787954", 25, (char) 0, "04", dt, res, db1);
//            testsGeocodeAdresse("4917884613483787954", 25, (char) 0, "20 A", dt, res, db1);
//            testsGeocodeAdresse("4917884613483787954", 25, (char) 0, "2 A", dt, res, db1);
//            testsGeocodeAdresse("4917884613483787954", 25, (char) 0, "20230", dt, res, db1);
//
//            res[0] = "989768.771";
//            res[1] = "6733431.83";
//            testsGeocodeAdresse("-764191784954048452", 25, (char) 0, "90010", dt, res, db1);
//            res[0] = "6.86050793";
//            res[1] = "47.6375064";
//            // Rq. : SELECT ST_ASText(ST_Transform(ST_GeomFromText('POINT(6.86050793431647 47.637506367609)', 4326), 2154))
//            // -> "POINT(989768.77110009 6733431.82499815)"
//            testsGeocodeAdresse("-764191784954048452", 25, (char) 0, "97110", dt, res, db1);
//
//            res[0] = "956952.723";
//            res[1] = "6336671.13";
//            testsGeocodeAdresse("-6885091946784100116", 25, (char) 0, "04", dt, res, db1);
//            testsGeocodeAdresse("-6885091946784100116", 25, (char) 0, "20070", dt, res, db1);
//            testsGeocodeAdresse("-6885091946784100116", 25, (char) 0, "20 A", dt, res, db1);
//            testsGeocodeAdresse("-6885091946784100116", 25, (char) 0, "2 A", dt, res, db1);
//            testsGeocodeAdresse("-6885091946784100116", 25, (char) 0, "20070", dt, res, db1);
//
//            String[] result = referentiel.geocode(-1, "-6885091946784100116", "CHEMIN DE LA VERDOLINE", "20070", "1/1/2005", 2154, db1);
//            junit.framework.Assert.assertEquals("Erreur geocodage CHEMIN DE LA VERDOLINE 20070", "7", result[0]);
//            junit.framework.Assert.assertEquals("Erreur geocodage CHEMIN DE LA VERDOLINE 20070", "1", result[1]);
//            junit.framework.Assert.assertEquals("Erreur geocodage CHEMIN DE LA VERDOLINE 20070", "5", result[2]);
//            junit.framework.Assert.assertEquals("Erreur geocodage CHEMIN DE LA VERDOLINE 20070", "956888.288", result[3]);
//            junit.framework.Assert.assertEquals("Erreur geocodage CHEMIN DE LA VERDOLINE 20070", "6336788.15", result[4]);
//            result = referentiel.geocode(-1, "-6885091946784100116", "12 CHEMIN DE LA VERDOLINE", "20070", "1/1/2005", 2154, db1);
//            junit.framework.Assert.assertEquals("Erreur geocodage 12 CHEMIN DE LA VERDOLINE 20070", "7", result[0]);
//            junit.framework.Assert.assertEquals("Erreur geocodage 12 CHEMIN DE LA VERDOLINE 20070", "1", result[1]);
//            junit.framework.Assert.assertEquals("Erreur geocodage 12 CHEMIN DE LA VERDOLINE 20070", "3", result[2]);
//            junit.framework.Assert.assertEquals("Erreur geocodage 12 CHEMIN DE LA VERDOLINE 20070", "956936.451", result[3]);
//            junit.framework.Assert.assertEquals("Erreur geocodage 12 CHEMIN DE LA VERDOLINE 20070", "6336705.81", result[4]);
//        }
//        // WA 01/2012 Pays
//        res[0] = "2.33138947";
//        res[1] = "48.8686388";
//        testsGeocode(null, "FR1", null, dt, res, db1, referentiel);
//        res[0] = "25.9119478";
//        res[1] = "-24.6463135";
//        testsGeocode(null, "BWA", null, dt, res, db1, referentiel);
//        res[0] = "19.9211327";
//        res[1] = "-80.5085641";
//        testsGeocode(null, "ATA", null, dt, res, db1, referentiel);
//        res[0] = "171.380000";
//        res[1] = "7.10300431";
//        testsGeocode(null, "MHL", null, dt, res, db1, referentiel);
//        String[] result = referentiel.geocode(2, "", "MHL", null, "1/1/2005", 2154, db1);
//        junit.framework.Assert.assertEquals("Geocodage pays sans null", "Le code insee est invalide", result[2]);
//
//        db1.close();
//    }
//
//    @Test
//    /**
//     * Test special suite au TT Kace 2869 ouvert par le ministere de l'écologie qui utilise les point adresses.
//     * Ces tests pourront etre reutilises plus tard pour les points adresse.
//     * NB : il est necessaire d'avoir les tables de points adresse présentes.
//     */
//    public void testsGeocodePtAddrME() throws JDOMException, IOException, ClassNotFoundException, JDONREFException, SQLException,
//            GestionMotsException, GestionReferentielException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//
//        Date dt = Calendar.getInstance().getTime();
//
//        if(GestionTables.tableExiste("adr_adresses_35", db1))
//        {
//            try
//            {
//                // Insertion des points adresse pour le test (adapter le chemin)
//                executeSQL("C:/Data/SVN/JDONREF/Dev/Src/JDONREFv2Lib/test/files/ScriptInsertionPointsAdressesTestsU.sql", db1);
//
//                // Tests issus du tt kace 2869
//                // 2 RUE DU CHAPITRE	35000 RENNES -> 351774.4,6789289.8
//                testGeocodePtAddrME("351774.600", "6789289.80", new String[] {"", "", "", "2 RUE DU CHAPITRE", "", "35000 RENNES"}, referentiel, db1, dt);
//
//                // 9 BOULEVARD DE LA TOUR D AUVERGNE 35000 RENNES -> 351544.78,6788899.23
//                testGeocodePtAddrME("351544.780", "6788899.23", new String[] {"", "", "", "9 BOULEVARD DE LA TOUR D AUVERGNE", "", "35000 RENNES"}, referentiel, db1, dt);
//
//                // 12 BOULEVARD DE LA TOUR D AUVERGNE 35000 RENNES	200	2	351544.69,6788896.57
//                testGeocodePtAddrME("351544.690", "6788896.57", new String[] {"", "", "", "12 BOULEVARD DE LA TOUR D AUVERGNE", "", "35000 RENNES"}, referentiel, db1, dt);
//
//                // 104 RUE DE SAINT BRIEUC	35000 RENNES	200	2 349847.5,6789887.8
//                testGeocodePtAddrME("349847.500", "6789887.80", new String[] {"", "", "", "104 RUE DE SAINT BRIEUC", "", "35000 RENNES"}, referentiel, db1, dt);
//
//                // 19 RUE DE LA RETARDAIS	 35000 RENNES	200	2 6788899.23,6788853.45
//                testGeocodePtAddrME("348227.350", "6788853.45", new String[] {"", "", "", "19 RUE DE LA RETARDAIS", "", "35000 RENNES"}, referentiel, db1, dt);
//
//                // 7 RUE D ESTREES		35000 RENNES	200	2 351960.3,6789406.7
//                testGeocodePtAddrME("351960.300", "6789406.70", new String[] {"", "", "", "7 RUE D ESTREES", "", "35000 RENNES"}, referentiel, db1, dt);
//
//                // 16 RUE SAINT MELAINE	35000 RENNES	200	2 352094.0,6789699.7
//                testGeocodePtAddrME("352094.000", "6789699.70", new String[] {"", "", "", "16 RUE SAINT MELAINE", "", "35000 RENNES"}, referentiel, db1, dt);
//            } finally
//            {
//                // Suppression des points adresse de test
//                executeSQL("C:/Data/SVN/JDONREF/Dev/Src/JDONREFv2Lib/test/files/ScriptSuppressionPointsAdressesTestsU.sql", db1);
//            }
//        }
//    }
//
//    private static void testGeocodePtAddrME(String xatt, String yatt, String[] lines, GestionReferentiel referentiel, Connection db1, Date dt) throws Exception
//    {
//        String[] resAtt = new String[2];
//        resAtt[0] = xatt;
//        resAtt[1] = yatt;
//
//        // Validation
//        boolean gererPays = (lines.length == 7);
//        String[] resVal = referentiel.valide(2, lines, null, false, gererPays, db1);
//        String voiId = resVal[6];
//        String ligne4 = resVal[7];
//        String codeInsee = resVal[9];
//
//        String dateStr = DateUtils.formatDateToStringSimpleSlashed(dt);
//        String[] resGeo = referentiel.geocode(2, voiId, ligne4, codeInsee, dateStr, 2154, db1);
//
//        String typeInterpolation = resGeo[2];
//        // Rappel : type interpolation :
//        // 1 pour à la plaque
//        // 2 pour à l'interpolation de la plaque,
//        // 3 pour à l'interpolation métrique du troncon ou les bornes du troncon (qualité équivalente),
//        // 4 au centroide du troncon,
//        // 5 pour le centroide de la voie.
//        // 6 pour la commune.
//        // 7 pour le dpt.
//        // 8 pour le pays.
//        if(!typeInterpolation.equals("2"))
//        {
//            throw new Exception("Test geocode ME : type interpolation != 2.");
//        }
//        if(!resGeo[3].equals(resAtt[0]))
//        {
//            throw new Exception("Test geocode ME : X <> attendu.");
//        }
//        if(!resGeo[4].equals(resAtt[1]))
//        {
//            throw new Exception("Test geocode ME : Y <> attendu.");
//        }
//    }
//
//    private static void executeSQL(String scriptFileName, Connection con)
//    {
//        try
//        {
//            Statement st = con.createStatement();
//            readAndAddBatch(scriptFileName, st);
//            System.out.println("Executing batch " + scriptFileName);
//            st.executeBatch();
//        } catch(FileNotFoundException ex)
//        {
//            ex.printStackTrace();
//        } catch(SQLException ex)
//        {
//            ex.printStackTrace();
//            ex.getNextException().printStackTrace();
//        }
//    }
//
//    private static void readAndAddBatch(String fileName, Statement st) throws FileNotFoundException, SQLException
//    {
//        StringBuilder text = new StringBuilder();
//        String NL = System.getProperty("line.separator");
//        Scanner scanner = new Scanner(new FileInputStream(fileName), "UTF-8");
//        try
//        {
//            while(scanner.hasNextLine())
//            {
//                st.addBatch(scanner.nextLine());
//            }
//        } finally
//        {
//            scanner.close();
//        }
//    }
//
    private static void afficheResultats(String[] res, int dim, int max)
    {
        int size = Integer.parseInt(res[0]);
        System.out.println("Nombre de résultats : " + size);
        if(res.length == 2)
        {
            System.out.println("Trop de résultats");
        } else
        {
            if(size > 0)
            {
                System.out.println("Extrait:");
                for(int i = 1; i < Math.min(dim * size + 1, max); i++)
                {
                    System.out.println(res[i]);
                }
            }
        }
    }
//
//    void testsMiseAJour(GestionReferentiel referentiel, String code_departement, Connection db1, Connection db2, Date date, int flags)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "ATTENTE"
//                };
//
//        GestionReferentielTests tests = new GestionReferentielTests();
//        tests.r = referentiel;
//        tests.code_departement = code_departement;
//        tests.c1 = db1;
//        tests.c2 = db2;
//        tests.date = date;
//        tests.p = p;
//        tests.methode = 3;
//        tests.flags = flags;
//
//        launch(tests);
//    }
//
//    // @Test
//    // Nécessite un échantillon de test et des critères de test.
//    public void testsMiseAJour() throws ClassNotFoundException, SQLException, GestionReferentielException, ParseException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        Calendar c = Calendar.getInstance();
//
//        //testsMiseAJour(referentiel, "75", db1, db2, c.getTime(),GestionReferentiel.MAJ_CODEPOSTAUX);
//        testsMiseAJour(referentiel, "92", db1, db2, c.getTime(), GestionReferentiel.MAJ_CODEPOSTAUX);
//        testsMiseAJour(referentiel, "93", db1, db2, c.getTime(), GestionReferentiel.MAJ_CODEPOSTAUX);
//        testsMiseAJour(referentiel, "94", db1, db2, c.getTime(), GestionReferentiel.MAJ_CODEPOSTAUX);
//        //testsMiseAJour(referentiel, "92", db1, db2, c.getTime(),128);
//        //testsMiseAJour(referentiel, "93", db1, db2, c.getTime(),128);
//        //testsMiseAJour(referentiel, "94", db1, db2, c.getTime(),128);
//
//        db1.close();
//        db2.close();
//    }
//
//    void testRevalide(String ligne4, String ligne6, String dateValidation, String date, GestionReferentiel referentiel,
//            String resultat_attendu, Connection connection) throws SQLException, Exception
//    {
//        System.out.println();
//
//        String[] res = referentiel.revalide(2, new String[]
//                {
//                    "", "", "", ligne4, "", ligne6
//                }, dateValidation, date, connection);
//
//        if(res[0].compareTo("0") == 0)
//        {
//            throw (new Exception("Erreur :" + res[2]));
//        } else
//        {
//            int size = Integer.parseInt(res[1]);
//            if(size > 0)
//            {
//                int module = (res.length - 2) / size;
//                for(int i = 0; i < size; i++)
//                {
//                    for(int j = 0; j < module; j++)
//                    {
//                        if(resultat_attendu.compareTo(res[2 + j + i * module]) == 0)
//                        {
//                            return;
//                        }
//                    }
//                }
//                throw (new Exception(
//                        "Revalide " + ligne4 + " " + ligne6 + " du " + dateValidation.toString() + " au " + date.toString() + " mise à jour " + resultat_attendu));
//            } else
//            {
//                throw (new Exception("Aucune mise à jour"));
//            }
//        }
//    }
//
//    void testRevalideFull(String[] lignes, String dateValidation, String date, GestionReferentiel referentiel,
//            String[] resultat_attendu, Connection connection) throws SQLException, Exception
//    {
//        System.out.println();
//
//        String[] res = referentiel.revalide(2, lignes, dateValidation, date, connection);
//
//        for(int i = 0; i < resultat_attendu.length; i++)
//        {
//            if(!res[i].equals(resultat_attendu[i]))
//            {
//                throw (new Exception(
//                        "Revalide " + lignes + " du " + dateValidation.toString() + " au " + date.toString() + " mise à jour " + resultat_attendu));
//            }
//        }
//    }
//
//    @Test
//    // nécessite un échantillon de test
//    public void testsRevalide() throws ClassNotFoundException, SQLException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
////        testRevalide("VOIE A 11", "75011 PARIS", "31/05/2008", "31/12/2008", referentiel, "resultat_attendu", db1);
////        testRevalide("PERIRISTYLE DE BEAUJOLAIS", "75001 PARIS", "7/3/2008", "10/10/2008", referentiel, "resultat_attendu", db1);
//
//        testRevalideFull(new String[]
//                {
//                    "", "", "", "", "", "", "FRANCE"
//                }, "31/05/2012", "01/01/2050", referentiel,
//                new String[]
//                {
//                    "6", "1", "", "", "", "", "FRANCE", "FR1"
//                }, db1);
//        testRevalideFull(new String[]
//                {
//                    "", "", "", "24 PLACE SAINT JACQUES", "", "75014 PARIS"
//                }, "31/05/2012", "01/01/2013", referentiel,
//                new String[]
//                {
//                    "6", "1", "-1692001112142849238", "24 PLACE SAINT JACQUES", "75014 PARIS", "75114"
//                }, db1);
//        testRevalideFull(new String[]
//                {
//                    "", "", "", "24 PLACE SAINT JACQUES", "", "75014 PARIS", "FRANCE"
//                }, "31/05/2012", "01/01/2013",
//                referentiel,
//                new String[]
//                {
//                    "6", "1", "-1692001112142849238", "24 PLACE SAINT JACQUES", "75014 PARIS", "75114", "FRANCE", "FR1"
//                }, db1);
//        testRevalideFull(new String[]
//                {
//                    "AAAA", "BBBB", "CCCC", "DDDD", "EEEE", "FFFF", "ALLEMAGNE"
//                }, "31/05/2012", "01/01/2050", referentiel,
//                new String[]
//                {
//                    "6", "1", "", "", "", "", "ALLEMAGNE", "DEU"
//                }, db1);
//    }
//
//    private static void testPrepareChangement(GestionReferentiel r, String code_departement, Connection c1, Connection c2)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.methode = 7;
//        grt.r = r;
//        grt.code_departement = code_departement;
//        grt.c1 = c1;
//        grt.c2 = c2;
//
//        new Thread(grt).start();
//    }
//
//    private static void testsPrepareChangement() throws ClassNotFoundException, SQLException, GestionReferentielException, ColonneException,
//            Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        testPrepareChangement(referentiel, "75", db1, db2);
//    }
//
//    void testChangeId(String code_departement, Connection c1, Connection c2, boolean idsource)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.c1 = c1;
//        grt.c2 = c2;
//        grt.idsource = idsource;
//        grt.methode = 4;
//
//        new Thread(grt).start();
//    }
//
//    // @Test 
//    // manque un échantillon et des critères de test
//    public void testsChangeId() throws ClassNotFoundException, SQLException, GestionReferentielException, ColonneException, JDOMException,
//            IOException, JDONREFException, GestionMotsException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        testChangeId("75", db1, db2, true);
//    }
//
//    void testChangementReferentiel(GestionReferentiel r, String code_departement, Connection c1, Connection c2, Date date, boolean idsource,
//            int flags)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.r = r;
//        grt.code_departement = code_departement;
//        grt.c1 = c1;
//        grt.c2 = c2;
//        grt.date = date;
//        grt.idsource = idsource;
//        grt.methode = 5;
//        grt.flags = flags;
//
//        new Thread(grt).start();
//    }
//
//    // @Test
//    // manque un échantillonage et des critères de test
//    public void testsChangeReferentiel() throws ClassNotFoundException, SQLException, GestionReferentielException, ColonneException,
//            ParseException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        Calendar c = Calendar.getInstance();
//
//        testChangementReferentiel(referentiel, "75", db1, db2, c.getTime(), true, 1 + 2 + 4 + 8 + 16 + 32);
//    }
//
//    void testPrepareMaj(GestionReferentiel r, String code_departement, Connection c1, Connection c2)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.methode = 9;
//        grt.r = r;
//        grt.p = p;
//        grt.code_departement = code_departement;
//        grt.c1 = c1;
//        grt.c2 = c2;
//
//        new Thread(grt).start();
//    }
//
//    void testsPrepareMaj() throws ClassNotFoundException, SQLException, ParseException, GestionReferentielException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        testPrepareMaj(referentiel, "75", db1, db2);
//        testPrepareMaj(referentiel, "92", db1, db2);
//        testPrepareMaj(referentiel, "93", db1, db2);
//        testPrepareMaj(referentiel, "94", db1, db2);
//    }
//
//    void testPhonetise(GestionReferentiel r, String colonneDestination, String colonneSource, String table, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "EN ATTENTE"
//                };
//
//        GestionReferentielTests grt = new GestionReferentielTests();
//        grt.r = r;
//        grt.c1 = c1;
//        grt.colonneDestination = colonneDestination;
//        grt.colonneSource = colonneSource;
//        grt.table = table;
//        grt.methode = 8;
//
//        launch(grt);
//    }
//
//    //@Test
//    // nécessite un échantillon et des critères de test
//    public void testsPhonetise() throws ClassNotFoundException, SQLException, GestionReferentielException, JDOMException, IOException,
//            JDONREFException, GestionMotsException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        //testPhonetise(referentiel,"libelle","libellephonetique","voies-75",db1);
//        //testPhonetise(referentiel,"motdeterminant","motdeterminantphonetique","voies-75",db1);
//        testPhonetise(referentiel, "nom", "nomphonetique", "communes_30_06_08", db1);
//    }
//
//    // @Test
//    // Nécessite un échantillon et des critères de test
//    public void testsMajAvecMots() throws ClassNotFoundException, SQLException, GestionReferentielException, ParseException,
//            GestionMotsException, JDOMException, IOException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections2.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        Base.loadParameters(db2, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        Calendar c = Calendar.getInstance();
//
//        c.set(Calendar.MONTH, 6);
//        c.set(Calendar.DAY_OF_MONTH, 30);
//
//        testsMiseAJour(referentiel, "75", db1, db2, c.getTime(), 16);
//
//        db1.close();
//        db2.close();
//    }
//
//    void testResoudAmbiguite(GestionReferentiel referentiel, String mot, String motoriginal, String voie,
//            ArrayList<String> codes_departement, Connection db1, Date dt, boolean resultat_attendu) throws SQLException, Exception
//    {
//        boolean ambigue = referentiel.resoudAmbiguite(mot, motoriginal, "TitreDansVoie", voie, codes_departement, db1);
//
//        if(ambigue != resultat_attendu)
//        {
//            throw (new Exception("Cherche si " + voie + " est ambigue : " + ambigue));
//        }
//    }
//
//    @Test
//    public void testsResoudAmbiguite() throws ClassNotFoundException, SQLException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        Calendar c = Calendar.getInstance();
//
//        ArrayList<String> departements = new ArrayList<String>();
//        departements.add("75");
//        testResoudAmbiguite(referentiel, "SAINTE", "ST", "BD ST LEONIE", departements, db1, c.getTime(), true);
//        testResoudAmbiguite(referentiel, "SAINT", "ST", "BD ST MOUTON", departements, db1, c.getTime(), false); // pas de bd st mouton donc pas d'ambiguité
//
//        db1.close();
//    }
//
//    /**
//     * Vérifie que la note d'une adresse est la même avec des calculs partiels ou non.
//     * Les différents calculs sont (si les éléments correspondant sont présents):
//     * <ul><li>NUMERO TYPE LIBELLE CODEPOSTAL COMMUNE</li>
//     * <li>NUMERO TYPE LIBELLE COMMUNE</li>
//     * <li>NUMERO TYPE LIBELLE CODEPOSTAL</li>
//     * <li>NUMERO TYPE LIBELLE COMMUNE ARRONDISSEMENT</li>
//     * <li>TYPE LIBELLE CODEPOSTAL COMMUNE</li>
//     * <li>TYPE LIBELLE COMMUNE</li>
//     * <li>TYPE LIBELLE CODEPOSTAL</li>
//     * <li>TYPE LIBELLE COMMUNE ARRONDISSEMENT</li>
//     * <li>LIBELLE CODEPOSTAL COMMUNE</li>
//     * <li>LIBELLE COMMUNE</li>
//     * <li>LIBELLE CODEPOSTAL</li>
//     * <li>LIBELLE COMMUNE ARRONDISSEMENT</li>
//     * <li>NUMERO LIBELLE CODEPOSTAL COMMUNE</li>
//     * <li>NUMERO LIBELLE COMMUNE</li>
//     * <li>NUMERO LIBELLE CODEPOSTAL</li>
//     * <li>NUMERO LIBELLE COMMUNE ARRONDISSEMENT</li>
//     * </ul>
//     * Attention toutefois:
//     * <ul><li>la répétition n'est pas prise en compte</li>
//     * <li>Si le numéro est présent, la voie doit contenir ce numéro</li></ul>
//     * @param elements des éléments d'adresse soit dans l'ordre:
//     * <ul>
//     * <li>numero</li>
//     * <li>type de voie</li>
//     * <li>libelle</li>
//     * <li>code postal</li>
//     * <li>commune</li>
//     * <li>arrondissement</li>
//     * </li>
//     * </ul>
//     * @return
//     */
//    private static void testNote(GestionReferentiel referentiel, String[] elements, boolean gererPays, Connection connexion) throws
//            SQLException, Exception
//    {
//        int nb_classes = 2;
//        int[] notes = new int[nb_classes];
//        int[] casreference = new int[nb_classes];
//        int nombredecas = 16;
//
//        for(int i = 0; i < nb_classes; i++)
//        {
//            notes[i] = -1;
//            casreference[i] = -1;
//        }
//
//        // ligne des éléments de l'adresse, dans l'ordre
//        int ligne[] = new int[]
//        {
//            4, 4, 4, 6, 6, 6
//        };
//
//        // les différents cas de figures gérés
//        boolean[][] cas = new boolean[16][6];
//        int[] classe = new int[16]; // les cas de figure sont regroupés par classes.
//
//        //NUMERO TYPE LIBELLE CODEPOSTAL COMMUNE
//        cas[0] = new boolean[]
//                {
//                    true, true, true, true, true, false
//                };
//        classe[0] = 0;
//        //NUMERO TYPE LIBELLE COMMUNE
//        cas[1] = new boolean[]
//                {
//                    true, true, true, false, true, false
//                };
//        classe[1] = 0;
//        //NUMERO TYPE LIBELLE CODEPOSTAL
//        cas[2] = new boolean[]
//                {
//                    true, true, true, true, false, false
//                };
//        classe[2] = 0;
//        //NUMERO TYPE LIBELLE COMMUNE ARRONDISSEMENT
//        cas[3] = new boolean[]
//                {
//                    true, true, true, false, true, true
//                };
//        classe[3] = 0;
//        //TYPE LIBELLE CODEPOSTAL COMMUNE
//        cas[4] = new boolean[]
//                {
//                    false, true, true, true, true, false
//                };
//        classe[4] = 0;
//        //TYPE LIBELLE COMMUNE
//        cas[5] = new boolean[]
//                {
//                    false, true, true, false, true, false
//                };
//        classe[5] = 0;
//        //TYPE LIBELLE CODEPOSTAL
//        cas[6] = new boolean[]
//                {
//                    false, true, true, true, false, false
//                };
//        classe[6] = 0;
//        //TYPE LIBELLE COMMUNE ARRONDISSEMENT
//        cas[7] = new boolean[]
//                {
//                    false, true, true, false, true, true
//                };
//        classe[7] = 0;
//        //LIBELLE CODEPOSTAL COMMUNE
//        cas[8] = new boolean[]
//                {
//                    false, false, true, true, true, false
//                };
//        classe[8] = 1;
//        //LIBELLE COMMUNE
//        cas[9] = new boolean[]
//                {
//                    false, false, true, false, true, false
//                };
//        classe[9] = 1;
//        //LIBELLE CODEPOSTAL
//        cas[10] = new boolean[]
//                {
//                    false, false, true, true, false, false
//                };
//        classe[10] = 1;
//        //LIBELLE COMMUNE ARRONDISSEMENT
//        cas[11] = new boolean[]
//                {
//                    false, false, true, false, true, true
//                };
//        classe[11] = 1;
//        //NUMERO LIBELLE CODEPOSTAL COMMUNE
//        cas[12] = new boolean[]
//                {
//                    true, false, true, true, true, false
//                };
//        classe[12] = 1;
//        //NUMERO LIBELLE COMMUNE
//        cas[13] = new boolean[]
//                {
//                    true, false, true, false, true, false
//                };
//        classe[13] = 1;
//        //NUMERO LIBELLE CODEPOSTAL
//        cas[14] = new boolean[]
//                {
//                    true, false, true, true, false, false
//                };
//        classe[14] = 1;
//        //NUMERO LIBELLE COMMUNE ARRONDISSEMENT
//        cas[15] = new boolean[]
//                {
//                    true, false, true, false, true, true
//                };
//        classe[15] = 1;
//
//        StringBuilder ligne4 = new StringBuilder();
//        StringBuilder ligne6 = new StringBuilder();
//        String[] adresse = new String[]
//        {
//            "", "", "", "", "", ""
//        };
//
//        // Examine chaque cas de figure.
//        for(int i = 0; i < nombredecas; i++)
//        {
//            // Vérifie si ce cas doit être étudié ou pas
//            boolean stop = false;
//            for(int j = 0; !stop && j < elements.length; j++)
//            {
//                String elementj = elements[j];
//
//                if(elementj == null || elementj.length() == 0)
//                {
//                    stop = true;
//                }
//            }
//            if(stop)
//            {
//                continue;
//            }
//
//            // Construit une adresse avec les éléments spécifiés
//            ligne4.setLength(0);
//            ligne6.setLength(0);
//
//            for(int j = 0; j < elements.length; j++)
//            {
//                if(cas[i][j])
//                {
//                    if(ligne[j] == 4)
//                    {
//                        Algos.appendWithSpace(ligne4, elements[j], true);
//                    } else if(ligne[j] == 6)
//                    {
//                        Algos.appendWithSpace(ligne6, elements[j], true);
//                    }
//                }
//            }
//
//            adresse[3] = ligne4.toString();
//            adresse[5] = ligne6.toString();
//
//            String[] res = referentiel.valide(2, adresse, null, false, gererPays, connexion);
//
//            if(res == null || res.length == 0)
//            {
//                throw new Exception("Erreur avec la validation du cas " + i + " de l'adresse (" + adresse[3] + "," + adresse[5] + ")");
//            } else
//            {
//                String coderesultat = res[0];
//                if(coderesultat.compareTo("0") == 0)
//                {
//                    throw new Exception(
//                            "Erreur avec la validation du cas " + i + " de l'adresse (" + adresse[3] + "," + adresse[5] + "). Erreur " + res[1] + " : " + res[2]);
//                } else
//                {
//                    String nombreresultat = res[1];
//
//                    if(nombreresultat.compareTo("0") == 0)
//                    {
//                        throw new Exception(
//                                "Erreur avec la validation du cas " + i + " de l'adresse (" + adresse[3] + "," + adresse[5] + ") : pas de résultats.");
//                    } else
//                    {
//                        int tempnote = -1;
//                        if(coderesultat.compareTo("1") == 0 || coderesultat.compareTo("2") == 0)
//                        {
//                            tempnote = Integer.parseInt(res[14]);
//                        } else if(coderesultat.compareTo("3") == 0 || coderesultat.compareTo("4") == 0)
//                        {
//                            tempnote = Integer.parseInt(res[11]);
//                        } else
//                        {
//                            throw new Exception(
//                                    "Erreur avec la validation du cas " + i + " de l'adresse (" + adresse[3] + "," + adresse[5] + ") : résultats non géré (code résultat : " + coderesultat + ").");
//                        }
//                        if(tempnote != -1)
//                        {
//                            int note = notes[classe[i]];
//                            if(note == -1)
//                            {
//                                notes[classe[i]] = tempnote;
//                                casreference[classe[i]] = i;
//                            } else
//                            {
//                                if(note != tempnote)
//                                {
//                                    throw new Exception(
//                                            "Erreur avec la validation du cas " + i + " de l'adresse (" + adresse[3] + "," + adresse[5] + ") : la note trouvée est " + tempnote + " alors que la note du cas référence " + casreference[classe[i]] + " est " + note + ".");
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Permet de tester unitairement la cohérence de l'attribution des notes lorsque
//     * des éléments d'adresse sont présents ou non.
//     */
//    @Test
//    public void testsNote() throws JDONREFException, SQLException, ClassNotFoundException, JDOMException, IOException, GestionMotsException,
//            Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//
//        testNote(referentiel, new String[]
//                {
//                    "", "PASSAGE", "LEMOINE", "75002", "PARIS", "2"
//                }, false, db1);
//        testNote(referentiel, new String[]
//                {
//                    "24", "BOULEVARD", "HOPITAL", "75005", "PARIS", "5"
//                }, false, db1);
//        testNote(referentiel, new String[]
//                {
//                    "24", "BOULEVARD", "HAPITAL", "75005", "PARIS", "5"
//                }, false, db1);
//        testNote(referentiel, new String[]
//                {
//                    "3", "RUE", "GABRIEL VICAIRE", "75003", "PARIS", "3"
//                }, false, db1);
//        testNote(referentiel, new String[]
//                {
//                    "3", "RUE", "GABRIELA VICAIRI", "75003", "PARIS", "3"
//                }, false, db1);
//    }
//
    private void afficheResultats(String[] res, String[] lignes, String resultatAttendu1, String resultatAttendu2, boolean gererPays) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        for(String lin : lignes)
        {
            sb.append(lin).append(" - ");
        }
        afficheResultats(res, sb.toString(), resultatAttendu1, resultatAttendu2, gererPays);
    }
    
    private void afficheResultats(List<String[]> res, String[] lignes, String resultatAttendu1, String resultatAttendu2, boolean gererPays, boolean not) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        for(String lin : lignes)
        {
            sb.append(lin).append(" - ");
        }
        afficheResultats(res, sb.toString(), resultatAttendu1, resultatAttendu2, gererPays, not);
    }
    
    private void afficheResultats(List<String[]> res, String[] lignes, String resultatAttendu1, String resultatAttendu2, boolean gererPays) throws Exception
    {
        afficheResultats(res,lignes, resultatAttendu1, resultatAttendu2, gererPays, false);
    }
    
    private void afficheResultats(List<String[]> res, String[] lignes, String resultatAttendu, boolean gererPays) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        for(String lin : lignes)
        {
            sb.append(lin).append(" - ");
        }
        afficheResultats(res, sb.toString(), resultatAttendu, gererPays);
    }

    void afficheResultats(String[] res, String adresse_recherchee, String resultatAttendu1, String resultatAttendu2, boolean gererPays) throws Exception
    {
        afficheResultats(res, adresse_recherchee, resultatAttendu1, resultatAttendu2, gererPays, false);
    }
    
    void afficheResultats(String[] res, String adresse_recherchee, String resultatAttendu1, String resultatAttendu2, boolean gererPays, boolean not) throws Exception
    {
        if(res.length > 1)
        {
            int etat = Integer.parseInt(res[0]);
            int mallusPays = gererPays ? 1 : 0;

            if(etat != 0)
            {
                int entete;
                int index_resultat_attendu1;
                int index_resultat_attendu2;
                switch (etat)
                {
                    default:
                    case 1:
                    case 2:
                        entete = GestionValidation.VALIDEVOIE_ID + mallusPays;
                        index_resultat_attendu1 = 1;
                        index_resultat_attendu2 = 4;
                        break;
                    case 3:
                    case 4:
                        entete = GestionValidation.VALIDECOMMUNE_CODEINSEE_NP + mallusPays;
                        index_resultat_attendu1 = 1;
                        index_resultat_attendu2 = -1;
                        break;
                    case 5:
                    case 6:
                        entete = GestionValidation.VALIDEPAYS_CODEAC3;
                        index_resultat_attendu1 = 1;
                        index_resultat_attendu2 = -1;
                        break;
                }

                int size = Integer.parseInt(res[1]);

                if(size > 0)
                {
                    int modulo = (res.length - entete) / size;
                    for(int i = 0; i < size; i++)
                    {
                        if(res[modulo * i + entete + index_resultat_attendu1].equals(resultatAttendu1) && 
                                
                           (resultatAttendu2.length()==0 || res[modulo * i + entete + index_resultat_attendu2].equals(resultatAttendu2)))
                        {
                            if (not)
                                throw(new Exception("le traitement de " + adresse_recherchee + " a abouti à " + resultatAttendu1 + " "+resultatAttendu2));
                            return;
                        }
                    }
                    if(resultatAttendu1.length() > 0)
                    {
                        if (not) return;
                        throw (new Exception("le traitement de " + adresse_recherchee + " n'a pas abouti à " + resultatAttendu1 + " "+resultatAttendu2));
                    }
                } else
                {
                    if(resultatAttendu1.length() > 0)
                    {
                        if (not) return;
                        throw (new Exception("Aucun résultats."));
                    }
                }
            } else
            {
                if(resultatAttendu1.length() > 0)
                {
                    if (not) return;
                    throw (new Exception("Erreur " + res[1] + " " + res[2]));
                }
            }
        } else
        {
            if (not) return;
            throw (new Exception("Aucun résultat."));
        }
    }
    
    void afficheResultats(List<String[]> res, String adresse_recherchee, String resultat_attendu1, String resultat_attendu2, boolean gererPays, boolean not) throws Exception
    {
        if (res.size()==0) throw(new Exception("Aucun résultat"));
        
        for(int i=0;i<res.size();i++)
            afficheResultats(res.get(i),adresse_recherchee,resultat_attendu1,resultat_attendu2,gererPays, not);
    }
    
    void afficheResultats(List<String[]> res, String adresse_recherchee, String resultat_attendu1, String resultat_attendu2, boolean gererPays) throws Exception
    {
        afficheResultats(res, adresse_recherchee, resultat_attendu1, resultat_attendu2, gererPays, false);
    }
    
    void afficheResultats(List<String[]> res, String adresse_recherchee, String resultat_attendu, boolean gererPays) throws Exception
    {
        if (res.size()==0) throw(new Exception("Aucun résultat"));
        
        for(int i=0;i<res.size();i++)
            afficheResultats(res.get(i),adresse_recherchee,resultat_attendu,"",gererPays);
    }

    private static void launch(GestionReferentielTests tests)
    {
        new Thread(tests).start();

        while(!tests.p.finished)
        {
            try
            {
                Thread.sleep(delai);
                for(int i = 0; i < tests.p.state.length;
                        i++)
                {
                    System.out.println(tests.p.state[i]);
                }
            } catch(InterruptedException ex)
            {
                System.out.println(ex.getStackTrace());
            }
        }

        for(int i = 0; i < tests.p.state.length;
                i++)
        {
            System.out.println(tests.p.state[i]);
        }
        for(int i = 0; i < tests.p.resultat.size(); i++)
        {
            System.out.println(tests.p.resultat.get(i));
        }
    }

//    void testNormalise(GestionReferentiel r, String table, String nom_original, String nom, String code_departement, int flags, int ligne,
//            Connection c2, Connection c1)
//    {
//        Processus p = new Processus();
//        p.state = new String[]
//                {
//                    "ATTENTE"
//                };
//
//        GestionReferentielTests tests = new GestionReferentielTests();
//        tests.r = r;
//        tests.nom_original = nom_original;
//        tests.table = table;
//        tests.c1 = c1;
//        tests.nom = nom;
//        tests.c2 = c2;
//        tests.methode = 0;
//        tests.code_departement = code_departement;
//        tests.p = p;
//        tests.ligne = ligne;
//        tests.flags = flags;
//
//        launch(tests);
//    }
//
//    //@Test
//    // nécessite un échantillon test et des critères de test
//    public void testsNormalise() throws ClassNotFoundException, SQLException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//        GestionValidation validation = new GestionValidation();
//        validation.setGestionMots(gm);
//        validation.setJdonrefParams(params);
//
//        try
//        {
//            testNormalise(referentiel, "tro_troncons_75", "tro_nom_original_droit", "tro_nom_droit", "75", 13, 4, db2, db1);
//            testNormalise(referentiel, "tro_troncons_75", "tro_nom_original_gauche", "tro_nom_gauche", "75", 13, 4, db2, db1);
//        } finally
//        {
//            db2.close();
//        }
//    }
//
//    @Test
//    // Pas d'erreur à remonter. Critères de tests à proposer ?
//    public void testsComptes() throws ClassNotFoundException, JDOMException, IOException, JDONREFException, SQLException,
//            GestionMotsException
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc1 = new GestionConnection(params);
//        gc1.load("connections.xml");
//        Connection db1 = gc1.obtientConnection().connection;
//        GestionConnection gc2 = new GestionConnection(params);
//        gc2.load("connections.xml");
//        Connection db2 = gc2.obtientConnection().connection;
//
//        GestionDescriptionTables.definitJDONREFParams(params);
//        Base.loadParameters(db1, params);
//        GestionIdentifiants.setParams(params);
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
//        GestionLogs.getInstance().definitRepertoire(".");
//        GestionValidation validation = new GestionValidation();
//        validation.setGestionMots(gm);
//        validation.setJdonrefParams(params);
//
//        String[] total_voies = referentiel.obtientTotalVoies(db1);
//        String[] total_troncons = referentiel.obtientTotalTroncons(db1);
//        String[] total_communes = referentiel.obtientTotalCommunes(db1);
//        String[] total_codes_postaux = referentiel.obtientTotalCodesPostaux(db1);
//
//        System.out.println("Nombre de voies : " + total_voies[0]);
//        System.out.println("Nombre de voies maximum : " + total_voies[3] + " dans " + total_voies[4]);
//        System.out.println("Nombre de voies minimum : " + total_voies[1] + " dans " + total_voies[2]);
//
//        System.out.println("Nombre de troncons : " + total_troncons[0]);
//        System.out.println("Nombre de troncons maximum : " + total_troncons[3] + " dans " + total_troncons[4]);
//        System.out.println("Nombre de troncons minimum : " + total_troncons[1] + " dans " + total_troncons[2]);
//
//        System.out.println("Nombre de codes postaux : " + total_codes_postaux[0]);
//        System.out.println("Nombre de codes postaux maximum : " + total_codes_postaux[3] + " dans " + total_codes_postaux[4]);
//        System.out.println("Nombre de codes postaux minimum : " + total_codes_postaux[1] + " dans " + total_codes_postaux[2]);
//
//        System.out.println("Nombre de communes : " + total_communes[0]);
//        System.out.println("Nombre de communes maximum : " + total_communes[3] + " dans " + total_communes[4]);
//        System.out.println("Nombre de communes minimum : " + total_communes[1] + " dans " + total_communes[2]);
//    }
//
    /**
     * Exécute la méthode choisie avec les paramètres choisis.
     */
    public void run()
    {
        try
        {
            switch (methode)
            {
                case 0:
                    r.normalise(p, table, nom_original, nom, code_departement, flags, ligne, c2, c1);
                    break;
                case 1:
                    gmaj.calculeCommunesAmbiguesDansVoies(p, code_departement, c1);
                    break;
                case 2:
                    gmaj.calculeClesAmbiguesDansCommunes(p, code_departement, c1);
                    break;
                case 3:
                    r.mise_a_jour(p, code_departement, flags, c1, c2, date);
                    break;
                case 4:
                    GestionIdentifiants.mise_a_jour_identifiants(p,
                            code_departement,
                            c1, c2,
                            idsource);
                    break;
                case 5:
                    r.changementReferentiel(p, code_departement, flags, c1, c2,
                            date, idsource);
                    break;
                case 6:
                    gmaj.creeTableVoieReferentiel(p, code_departement, tabletroncon, tablevoies, tableid, c1);
                    break;
                case 7:
                    r.prepareChangementReferentiel(p, code_departement, c1, c2);
                    break;
                case 8:
                    r.phonetise(p, colonneSource, colonneDestination, table, c1);
                    break;
                case 9:
                    r.prepareMajReferentiel(p, code_departement, c1, c2);
                    break;
                case 10:
                    gmaj.calculeClesAmbiguesDansVoies(p, code_departement, c1);
                    break;
                case 11:
                    r.genereFantoir(p, nomTable, id, fantoire, c1);
                    break;
                case 12:
                    r.decoupe(p, nomTable, id, champs, decoupages, natures, lignes, c2, c1);
                    break;
                case 13:
                    r.genereIdTroncon(p, code_departement, c1);
                    break;
            }
            if(p.state[0].compareTo("ERREUR") != 0)
            {
                p.state = new String[]
                        {
                            "TERMINE"
                        };
                p.finished = true;
            }
        } catch(Exception e)
        {
            p.finished = true;
            p.resultat.add(e.getMessage());
            for(int i = 0; i < e.getStackTrace().length; i++)
            {
                p.resultat.add(e.getStackTrace()[i].toString());
            }
            p.state = new String[]
                    {
                        "ERREUR", e.getMessage()
                    };
        }
    }
}
