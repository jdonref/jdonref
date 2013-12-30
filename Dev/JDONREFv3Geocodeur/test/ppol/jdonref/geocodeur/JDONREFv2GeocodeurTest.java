/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.geocodeur;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author arochewi
 */
public class JDONREFv2GeocodeurTest
{

    public JDONREFv2GeocodeurTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of main method, of class JDONREFv2Geocodeur.
     * Principalement test de non régression.
     * Puis tests a 7 lignes
     */
    @Test
    public void testMain() throws FileNotFoundException, IOException
    {
        System.out.println("main");

        // Test de no régression
        File config = new File("./config.xml");
        File oldConfig = new File("./config_old.xml");
        File configToUse = new File("./test/files/testgeocodage/config.xml");
        try
        {
            // utilisation du bon fichier de conf
            config.renameTo(oldConfig);
            copyFile(configToUse, config);

            // Suppression des anciens fichiers
            File fil = new File("./test/files/testgeocodage/erreurs.txt");
            fil.delete();
            fil = new File("./test/files/testgeocodage/info.xml");
            fil.delete();
            fil = new File("./test/files/testgeocodage/out.csv");
            fil.delete();
            fil = new File("./test/files/testgeocodage/rejets.xml");
            fil.delete();

            // Lancement de la validation
            String[] args = new String[]
            {
                "valide", "./test/files/testgeocodage/"
            };
            JDONREFv2Geocodeur.main(args);

            // Comparaison avec le resultat attendu
            File obtenu = new File("./test/files/testgeocodage/out.csv");
            File attendu = new File("./test/files/testgeocodageAttendu/out.csv");
            if(obtenu.length() == attendu.length())
            {
                if(!compareFiles(obtenu, attendu))
                {
                    fail("La sortie obtenue ne corresponds pas à celle attendue.");
                }
            } else
            {
                fail("La sortie obtenue n'a pas la meme taille que celle attendue.");
            }
        } catch(Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        } finally
        {
            // Vieux fichier de config
            copyFile(oldConfig, config);
        }
    }

    @Test
    // Test a 7 lignes
    public void testMain2() throws FileNotFoundException, IOException
    {
        System.out.println("main 2");

        File config = new File("./config.xml");
        File oldConfig = new File("./config_old.xml");
        File configToUse = new File("./test/files/testgeocodage_pays/config.xml");
        try
        {
            // utilisation du bon fichier de conf
            config.renameTo(oldConfig);
            copyFile(configToUse, config);

            // Suppression des anciens fichiers
            File fil = new File("./test/files/testgeocodage_pays/erreurs.txt");
            fil.delete();
            fil = new File("./test/files/testgeocodage_pays/info.xml");
            fil.delete();
            fil = new File("./test/files/testgeocodage_pays/out.csv");
            fil.delete();
            fil = new File("./test/files/testgeocodage_pays/rejets.xml");
            fil.delete();

            // Lancement de la validation
            String[] args = new String[]
            {
                "valide", "./test/files/testgeocodage_pays/"
            };
            JDONREFv2Geocodeur.main(args);

            // Comparaison avec le resultat attendu
            File obtenu = new File("./test/files/testgeocodage_pays/out.csv");
            File attendu = new File("./test/files/testgeocodageAttendu_pays/out.csv");
            if(obtenu.length() == attendu.length())
            {
                if(!compareFiles(obtenu, attendu))
                {
                    fail("La sortie obtenue ne corresponds pas à celle attendue.");
                }
            } else
            {
                fail("La sortie obtenue n'a pas la meme taille que celle attendue.");
            }
        } catch(Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        } finally
        {
            // Vieux fichier de config
            copyFile(oldConfig, config);
        }
    }

    private static boolean compareFiles(File obtenu, File attendu) throws FileNotFoundException, IOException
    {
        BufferedInputStream bisO = new BufferedInputStream(new FileInputStream(obtenu));
        BufferedInputStream bisA = new BufferedInputStream(new FileInputStream(attendu));
        int o = 0;
        int a = 0;
        do
        {
            o = bisO.read();
            a = bisA.read();
            if(o != a)
            {
                return false;
            }
        } while(o != -1);
        return true;
    }

    private static void copyFile(File src, File dest) throws FileNotFoundException, IOException
    {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);
        try
        {
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        } finally
        {
            in.close();
            out.close();
        }
    }
}
