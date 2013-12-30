/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.tests;

import ppol.jdonref.mots.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marcanhe
 */
public class LevensteinTest {

    public LevensteinTest() {
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
    
    @Test
    public void note_type_de_voie() {
        System.out.println("note_type_de_voie");
        String arg1 = "PRIS";
        String arg2 = "PARIS";
        int i2 = 200;
        int i3 = 60;
        int i4 = 2;
        Levenstein instance = new Levenstein();
        int expResult = 200;
        int result = instance.note_type_de_voie(arg1, arg2, i2, i3, i4);
        assertEquals(expResult, result);
    }
    
    @Test
    public void note_pourcentage_seuil() {
        System.out.println("note_pourcentage_seuil");
        String arg1 = "PRIS";
        String arg2 = "PARIS";
        int i2 = 200;
        int i3 = 60;
        Levenstein instance = new Levenstein();
        int expResult = 190;
        int result = instance.note_pourcentage_seuil(arg1, arg2, i2, i3);
        assertEquals(expResult, result);
    }    
    
    @Test
    public void note_pourcentage_seuil_n() {
        System.out.println("note_pourcentage_seuil_n");
        String arg1 = "PRIS";
        String arg2 = "PARIS";
        int i2 = 200;
        int i3 = 60;
        Levenstein instance = new Levenstein();
        int expResult = 100;
        int result = instance.note_pourcentage_seuil_n(arg1, arg2, i2, i3);
        assertEquals(expResult, result);
    }
    
    @Test
    public void note_pourcentage_seuil_total() {
        System.out.println("note_pourcentage_seuil_total");
        String arg1 = "PRIS";
        String arg2 = "PARIS";
        int i2 = 200;
        int i3 = 60;
        Levenstein instance = new Levenstein();
        int expResult = 190;
        int result = instance.note_pourcentage_seuil_total(arg1, arg2, i2, i3);
        assertEquals(expResult, result);
    }
    
    @Test
    public void note_arrondissement() {
        System.out.println("note_arrondissement");
        String s0 = "13";
        String s1 = "75013";
        boolean c2 = false;
        Levenstein instance = new Levenstein();
        int expResult = 10;
        int result = instance.note_arrondissement(s0, s1, c2);
        assertEquals(expResult, result);
    }
    
    @Test
    public void note_voie_codepostal_commune() {
        System.out.println("note_voie_codepostal_commune");
        
        String s0 = "GAKES";
        String s1 = "GAKES";
        String s2 = "S4 GAKES";
        String s3 = "S4 GAKES";
        String s4 = "FOB3RG";
        String s5 = "B3LEVARD";
        String s6 = "PRIS";
        String s7 = "PARIS";
        String s8 = "75013";
        String s9 = "75014";
        String s10 = "13";
        boolean c11 = false;
        Levenstein instance = new Levenstein();
        int expResult = 143;
        int result = instance.note_voie_codepostal_commune(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, c11);
        assertEquals(expResult, result);
    }

    
    @Test
    public void note_voie_codepostal() {
        System.out.println("note_voie_codepostal");
        String s0 = "GAKES";
        String s1 = "GAKES";
        String s2 = "S4 GAKES";
        String s3 = "S4 GAKES";
        String s4 = "FOB3RG";
        String s5 = "B3LEVARD";
        String s6 = "PARIS";
        String s7 = "75013";
        String s8 = "75014";
        Levenstein instance = new Levenstein();
        int expResult = 155;
        int result = instance.note_voie_codepostal(s0, s1, s2, s3, s4, s5, s6, s7, s8);
        assertEquals(expResult, result);
        
    }

    
    @Test
    public void note_codepostal() {
        System.out.println("note_codepostal");
        String s0 = "95";
        String s1 = "95100";
        Levenstein instance = new Levenstein();
        int expResult = 100;
        int result = instance.note_codepostal(s0, s1);
        assertEquals(expResult, result);
    }

   
    @Test
    public void note_codepostal_commune() {
        System.out.println("note_codepostal_commune");
        String s0 = "PRIS";
        String s1 = "PARIS";
        String s2 = "75013";
        String s3 = "75014";
        String s4 = "14";
        boolean c5 = false;
        Levenstein instance = new Levenstein();
        int expResult = 145;
        int result = instance.note_codepostal_commune(s0, s1, s2, s3, s4, c5);
        assertEquals(expResult, result);
    }
    
    @Test
    public void note_commune() {
        System.out.println("note_commune");
        String s0 = "PRIS";
        String s1 = "PARIS";
        String s2 = "14";
        String s3 = "75014";
        boolean c4 = false;
        Levenstein instance = new Levenstein();
        int expResult = 183;
        int result = instance.note_commune(s0, s1, s2, s3, c4);
        assertEquals(expResult, result);
    }

    
    @Test
    public void note_commune_seul() {
        System.out.println("note_commune_seul");
        String s0 = "PRIS";
        String s1 = "PARIS";
        Levenstein instance = new Levenstein();
        int expResult = 180;
        int result = instance.note_commune_seul(s0, s1);
        assertEquals(expResult, result);
    }
     

}