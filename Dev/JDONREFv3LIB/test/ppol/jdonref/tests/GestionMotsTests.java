/*
 * GestionMotsTests.java
 *
 * Created on 24 avril 2008, 10:21
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

import ppol.jdonref.mots.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import org.jdom.JDOMException;
import ppol.jdonref.Base;
import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.referentiel.GestionMiseAJour;
import ppol.jdonref.referentiel.GestionReferentiel;
import ppol.jdonref.referentiel.GestionReferentielException;
import org.junit.Test;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.referentiel.GestionCodesDepartements;


/**
 * Tests du package ppol.jdonref.mots
 * @author jmoquet
 */
public class GestionMotsTests
{
//    private void testChercheDebutMot(GestionMots gm,String chaine,boolean mottrouve_attendu) throws Exception
//    {
//        Mot m = gm.chercheDebutMot(chaine,0,CategorieMot.TypeDeVoie);
//        
//        if (m==null && mottrouve_attendu)
//            throw(new Exception("Aucun mot trouvé au début de la chaîne "+chaine));
//        else if (m!=null && !mottrouve_attendu)
//            throw(new Exception("Un mot a été trouvé en début de chaine "+chaine));
//    }
    
    @Test
    public void testsLoadAbbreviation() throws GestionMotsException, JDOMException, IOException, Exception
    {
        GestionMots gm = new GestionMots();

        gm.loadAbbreviation("abbreviations.xml");
    }
//    
//    @Test
//    public void testsChercheDebutMot() throws GestionMotsException, JDOMException, IOException, Exception
//    {
//        GestionMots gm = new GestionMots();
//
//        System.out.println("Repertoire courant : " + System.getProperty("user.dir"));
//
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        
//        testChercheDebutMot(gm,"BOULEVARD DU MAL DE TASSIGNY",true);
//        testChercheDebutMot(gm,"CENTRE MASSY PALAISEAU",true);
//        testChercheDebutMot(gm,"CENTRE COMMERCIAL MASSY PALAISEAU",true);
//        testChercheDebutMot(gm,"PORTE A",true);
//    }
//    
//    private void testAbbreviation(GestionMots gm,String abbreviation,boolean trouve_attendu) throws Exception
//    {
//        Abbreviation abb = gm.chercheAbbreviation(abbreviation);
//        
//        if (abb!=null)
//        {
//            if (!trouve_attendu)
//                throw(new Exception("L'abbréviation "+abb.obtientNom()+" a été trouvé alors qu'aucune abbréviation ne devait être trouvée."));
//            
//            if (abb.obtientNom().compareTo(abbreviation)!=0)
//                throw(new Exception("L'abbréviation "+abb.obtientNom()+" a été trouvé à la place de "+abbreviation));
//        }
//        else if (abb==null && trouve_attendu)
//        {
//            throw(new Exception("L'abbréviation "+abbreviation+" n'a pas été trouvée"));
//        }
//    }
//    
//    /**
//     * Test du chargement des abbréviations. Quelques abbréviations sont recherchées.
//     */
//    @Test
//    public void testsAbbreviation() throws GestionMotsException, JDOMException, IOException, Exception
//    {
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        
//        testAbbreviation(gm,"MAL",true);
//        testAbbreviation(gm,"CAR",true);
//        testAbbreviation(gm,"FOR",true);
//    }
//
//    void testCherchePetitMot(GestionMots mots,String chaine,int index,String trouve)
//    {
//        RefCle ref = mots.cherchePetitMot(chaine,index);
//        
//        if (trouve==null && ref==null) return;
//        if (trouve!=null && ref!=null && ref.obtientMot()!=null && ref.obtientMot().compareTo(trouve)==0) return;
//        
//        StringBuilder message = new StringBuilder();
//        message.append("Cherche les petits mots de "+chaine+" à l'index "+index+" :");
//        if (ref==null) message.append("Rien n'a été trouvé.");
//        else message.append(ref.obtientCle().obtientNom());
//        
//        return;
//    }
//    
//    @Test
//    public void testsCherchePetitsMots() throws GestionMotsException, IOException, JDOMException
//    {
//        GestionMots mots = new GestionMots();
//        mots.loadCles("cles.xml");
//        
//        String[] chaines = new String[]{"A","AU","AUX","BIS","D","DE","DES","DU",
//                                        "E","ER","EME","LE","LA","LES","OU","PAR",
//                                        "QUATER","QUINQUIES","SE","SUR","TER"};
//        
//        testCherchePetitMot(mots,"LE AU BIS",0,"LE");
//        testCherchePetitMot(mots,"LE AU BIS",3,"AU");
//        testCherchePetitMot(mots,"LE AU BIS",6,"BIS");
//        
//        testCherchePetitMot(mots,"LE AU DU ",6,"DU");
//        testCherchePetitMot(mots,"LE AU DX",6,null);
//        
//        testCherchePetitMot(mots,"EM",0,null);
//        testCherchePetitMot(mots,"EM ",0,null);
//        testCherchePetitMot(mots,"EME",0,"EME");
//        testCherchePetitMot(mots,"EME ",0,"EME");
//        
//        // teste tous les petits mots
//        for(int i=0;i<chaines.length;i++)
//        {
//            String chaine = chaines[i];
//            for(int j=1;j<chaine.length()-1;j++) // sans la première lettre seule
//            {
//                if (! ((i==2 && j==1) || (i==6 && j==1) || (i==13 && j==1)))
//                {
//                    testCherchePetitMot(mots,chaine.substring(0,j+1),0,null);
//                    testCherchePetitMot(mots,chaine.substring(0,j+1)+" ",0,null);
//                }
//            }
//            testCherchePetitMot(mots,chaine,0,chaine);
//            testCherchePetitMot(mots,chaine+" ",0,chaine);
//        }
//    }
//    
//    /**
//     * 
//     */
//    @Test
//    public void testLoadCle() throws GestionMotsException, JDOMException, IOException, Exception
//    {
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        
//        int count = 0;
//        for(int i=0;i<gm.obtientCompteMots();i++)
//        {
//            Mot m = gm.obtientMot(i);
//            
//            if (m.estDeLaCategorie(CategorieMot.Cle))
//            {
//                count++;
//            }
//        }
//        if (count==0) throw(new Exception("Aucune clé trouvée !"));
//
//        // Modif tests unitaires WA 31-05-2011 : ALLEE n'est pas une cle -> remplace par ETAGE
//        //Abbreviation motAll = gm.chercheAbbreviation("ALL");
//        Abbreviation motAll = gm.chercheAbbreviation("ETG");
//        if (motAll!=null)
//        {
//            Mot motAllee = motAll.obtientMotPrefere(CategorieMot.Cle);
//            if (motAllee!=null)
//                motAllee.obtientLigneDeCle();
//        }
//    }
//    
//    @Test
//    public void testLoadPrenoms() throws GestionMotsException, IOException, JDOMException, Exception
//    {
//        GestionMots gm = new GestionMots();
//        
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadPrenoms("prenoms.txt");
//        
//        int count = 0;
//        for(int i=0;i<gm.obtientCompteMots();i++)
//        {
//            Mot m = gm.obtientMot(i);
//            
//            if (m.estDeLaCategorie(CategorieMot.Prenom))
//            {
//                count++;
//            }
//        }
//        if (count==0) throw(new Exception("Aucun prénom trouvé!"));
//    }
//    
//    /**
//     * Vérifie si la voie voie est ambigue.
//     */
//    void testVoieAmbigue(GestionMots gm,String voie,boolean estambigue) throws Exception
//    {
//        ResultatAmbiguite ra = gm.chercheSiVoieAmbigue(voie);
//        if (ra.obtientQuantite()>0 && !estambigue)
//        {
//            throw(new Exception("La voie "+voie+" a été identifiée comme ambigue alors qu'elle ne l'est pas."));
//        }
//        else if (ra.obtientQuantite()==0 && estambigue)
//        {
//            throw(new Exception("La voie "+voie+" n'a pas été identifiée comme ambigue alors qu'elle devrait l'être."));
//        }
//    }
//
//    @Test
//    public void testVoieAmbigue() throws GestionMotsException, JDOMException, IOException, Exception
//    {
//        GestionMots gm = new GestionMots();
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        
//        // MARECHAL est un titre (ligne 1)
//        testVoieAmbigue(gm,"MARECHAL DE LATTRE DE TASSIGNY",true);
//        
//        // MAL est l'abbréviation officielle de MARECHAL
//        // Il n'y a pas de désabbréviation préalable,
//        // donc malgré la présence de MAL
//        // cette voie n'est pas considérée comme ambigue.
//        testVoieAmbigue(gm,"MAL DE LATTRE DE TASSIGNY",false);
//        
//        // ESCALIER est une clé (ligne 2)
//        testVoieAmbigue(gm,"BD DE L ESCALIER",true);
//    }
//    
//    /**
//     * Teste le découpage de ligne4,ligne6
//     */
//    void testDecoupe(GestionMots gm,String ligne4,String ligne6, String ligne7, int[] natures,Connection connection,String[] resultat) throws SQLException, Exception
//    {
//        String[] res = gm.decoupe(2,new String[]{ligne4,ligne6, ligne7},natures,new int[]{4,6,7},connection);
//        
//        if (res[0].compareTo("1")==0)
//        {
//            boolean error = false;
//            for(int i=0;(i<resultat.length)&&!error;i++)
//            {
//                if (res[i+1].compareTo(resultat[i])!=0)
//                    error = true;
//            }
//            if (error)
//            {
//                String message = "ERREUR";
//                for(int i=0;i<res.length;i++)
//                {
//                    message += res[i];
//                }
//                throw(new Exception(message));
//            }
//        }
//        else
//        {
//            String message = "";
//            for(int i=0;i<res.length;i++)
//            {
//                message += res[i];
//            }
//            throw(new Exception(message));
//        }
//    }
//
//    private void testDecoupeFull(GestionMots gm,String[] lignes, int[] natures, int[] numeroslignes, Connection connection, String[] resultatAttendu) throws Exception
//    {
//        String[] res = gm.decoupe(2, lignes, natures, numeroslignes, connection);
//        if (res[0].compareTo("1")==0)
//        {
//            boolean error = false;
//            for(int i=0;(i<resultatAttendu.length)&&!error;i++)
//            {
//                if (res[i+1].compareTo(resultatAttendu[i])!=0)
//                    error = true;
//            }
//            if (error)
//            {
//                String message = "ERREUR";
//                for(int i=0;i<res.length;i++)
//                {
//                    message += res[i];
//                }
//                throw(new Exception(message));
//            }
//        }
//        else
//        {
//            String message = "";
//            for(int i=0;i<res.length;i++)
//            {
//                message += res[i];
//            }
//            throw(new Exception(message));
//        }
//    }
//    
//    @Test
//    public void testDecoupe() throws GestionMotsException, JDOMException, IOException, ClassNotFoundException, SQLException, JDONREFException, GestionReferentielException, Exception
//    {
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc=new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//
//        if(params.isUtilisationDeLaGestionDesDepartements())    // si ce parametre n'est pas a true : tous les test de corse et domtom echoueront.
//            GestionCodesDepartements.getInstance().loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        
//        GestionMots gm = new GestionMots();
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm,params);
//        GestionReferentiel gr = new GestionReferentiel(gm,gmaj,params);
//        gm.definitGestionReferentiel(gr);
//        GestionLogs.getInstance().definitRepertoire(".");
//        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        }
//        
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");        
//        gm.loadAbbreviation("abbreviations.xml");
//     
//     /*
//     * <ul>
//     * <li>1 pour numero</li>
//     * <li>2 pour repetition</li>
//     * <li>4 pour autres numeros</li>
//     * <li>8 pour type de voie</li>
//     * <li>16 pour article</li>
//     * <li>32 pour libelle</li>
//     * <li>64 pour mot déterminant</li>
//     * <li>128 pour code postal</li>
//     * <li>256 pour commune</li>
//     * <li>512 pour numero d'arrondissement</li>
//     * <li>1024 pour cedex</li>
//     * <li>2048 pour le code cedex</li>
//     * <li>4096 pour ligne1</li>
//     * <li>8192 pour ligne2</li>
//     * <li>16384 pour ligne3</li>
//     * <li>32768 pour ligne5</li>
//     * </ul>
//     */
//        testDecoupe(gm,"BOULEVARD DE L HOPITAL","75005 PARIS", "",new int[]{8,16,32,128+256},db1,
//                       new String[]{"BOULEVARD","DE L","HOPITAL","75005 PARIS"});
//        testDecoupe(gm,"24 26 BOULEVARD DE L HOPITAL","75005 PARIS", "",new int[]{1+8+16+32,4,128,256},db1,
//                       new String[]{"24 BOULEVARD DE L HOPITAL","26","75005","PARIS"});
//        testDecoupe(gm,"24 R DE L HOPITAL","PARIS", "",new int[]{1,2,8+16+32,128+256},db1,
//                       new String[]{"24","","R DE L HOPITAL","PARIS"});
//        testDecoupe(gm,"24 R","", "",new int[]{1,2},db1,
//                       new String[]{"24","R"});
//        testDecoupe(gm,"24 26 RPT DE L HOPITAL","75005", "",new int[]{1+2+8+16+32,128,256},db1,
//                       new String[]{"24 RPT DE L HOPITAL","75005",""});
//        testDecoupe(gm,"RUE DU VIEUX BOURG","01189", "",new int[]{8,32,64},db1,
//                       new String[]{"RUE","VIEUX BOURG","BOURG"});
//        // WA 09/2011 Maintenant que l'on a la liste des departement du referentiel, on sait que 7 n'est pas un code dpt valide
//        if( ! params.isUtilisationDeLaGestionDesDepartements())
//            testDecoupe(gm,"AVENUE PORTE GENTILLY","7 5 PARIS", "",new int[]{128,256},db1,new String[]{"7",""});
//        else
//            testDecoupe(gm,"AVENUE PORTE GENTILLY","7 5 PARIS", "",new int[]{128,256},db1,new String[]{"",""});
//        
//        testDecoupe(gm,"AVENUE PORTE GENTILLY","75 4 ARIS", "",new int[]{128,256},db1,new String[]{"75",""});
//        
//        testDecoupe(gm,"AVENUE PORTE GENTILLY","H 75 PARIS", "",new int[]{128,256},db1,new String[]{"75","PARIS"});
//
//        // WA 09/2011 Corse
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            if (gestDpt.isDptCodePresent("20 A"))
//            {
//                testDecoupe(gm,"AVENUE PORTE GENTILLY","H 20 A AJACCIO", "",new int[]{128,256},db1,new String[]{"20 A","AJACCIO"});
//                testDecoupe(gm,"2 B AVENUE PORTE GENTILLY","H 2 A AJACCIO", "",new int[]{128,256},db1,new String[]{"2 A","AJACCIO"});
//            }
//            if (gestDpt.isDptCodePresent("20 B"))
//            {
//                testDecoupe(gm,"20 A AVENUE PORTE GENTILLY","H 20 B AJACCIO", "",new int[]{128,256},db1,new String[]{"20 B","AJACCIO"});
//                testDecoupe(gm,"AVENUE PORTE GENTILLY","H 2 B AJACCIO", "",new int[]{128,256},db1,new String[]{"2 B","AJACCIO"});
//            }
//        }
//
//        // WA 01/2012 Pays
//        testDecoupe(gm, "BOULEVARD DE L HOPITAL", "75005 PARIS", "FRANCE", new int[]{8, 128, 256, 65536}, db1,
//                new String[]{"BOULEVARD", "75005", "PARIS", "FRANCE"});
//        testDecoupe(gm, "BOULEVARD DE L HOPITAL", "75005 PARIS", "L ETAT PLURINATIONAL DE BOLIVIE", new int[]{8, 128, 256, 65536}, db1,
//                new String[]{"BOULEVARD", "75005", "PARIS", "L ETAT PLURINATIONAL DE BOLIVIE"});
//
//        testDecoupeFull(gm, new String[]{"", "", "", "BOULEVARD DE L HOPITAL", "", "75005 PARIS", "L ETAT PLURINATIONAL DE BOLIVIE"},
//                new int[]{8, 128, 256, 8, 8, 8, 65536}, new int[] {1,2,3,4,5,6,7}, db1,
//                new String[]{"BOULEVARD", "75005", "PARIS", "BOULEVARD", "BOULEVARD", "BOULEVARD", "L ETAT PLURINATIONAL DE BOLIVIE"});
//
//    }
//
//    /**
//     * Teste la normalisation des lignes 3 4 et 6
//     */
//    void testNormaliseAdresse(GestionMots gm,String ligne3,String ligne4,String ligne6,String str_departements,Connection connection,String resultat3,String resultat4,String resultat6) throws SQLException, Exception
//    {
//        ArrayList<String> numerosSupplementaires = new ArrayList<String>();
//        
//        String[] res = gm.normalise_2(new String[]{"","", ligne3, ligne4, "", ligne6}, numerosSupplementaires,true,true, false,str_departements,connection);
//        
//        if (res[2].compareTo(resultat3) != 0 || res[3].compareTo(resultat4) != 0 || res[5].compareTo(resultat6) != 0)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Normalise à 38 caractères '" + ligne3 + "' '" + ligne4 + "' dans '" + ligne6 + "'");
//            for (int i = 0; i < res.length; i++)
//            {
//                sb.append(res[i]);
//                sb.append(" (");
//                sb.append(res[i].length());
//                sb.append(" caractères )");
//            }
//            throw (new Exception(sb.toString()));
//        }
//    }
//
//    /**
//     * Teste la normalisation 2 multilignes
//     * @param gm
//     * @param lignes
//     * @param str_departements
//     * @param connection
//     * @param resultats
//     * @throws SQLException
//     * @throws Exception
//     */
//    void testNormaliseFull(GestionMots gm, String[] lignes, String str_departements, boolean gererPays, Connection connection, String[] resultats) throws SQLException, Exception
//    {
//        ArrayList<String> numerosSupplementaires = new ArrayList<String>();
//
//        String[] res = gm.normalise_2(lignes, numerosSupplementaires, true, true, gererPays, str_departements, connection);
//
//        boolean error = false;
//        for(int i =0; i<resultats.length; i++)
//        {
//            if( ! res[i].equals(resultats[i]))
//            {
//                error = true;
//            }
//        }
//
//        if(error)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Normalise multiligne de '");
//            for(String lin : lignes)
//                sb.append(lin).append("-");
//            sb.append("'. Ne corresponds pas au resultat attendu.");
//            throw (new Exception(sb.toString()));
//        }
//    }
//    
//    /**
//     * Retourne 1 si le résultat n'est pas celui attendu.
//     * @param gm
//     * @param chaine
//     * @param connection
//     * @param attendu
//     * @return
//     * @throws java.sql.SQLException
//     */
//    void testNormalise38_ligne4(GestionMots gm,String chaine,Connection connection,String[] codes_departement,String attendu) throws SQLException, Exception
//    {
//        ArrayList<String> numerosSupplementaires = new ArrayList<String>();
//        String res = gm.normalise_2_ligne4(chaine,numerosSupplementaires,true,true,codes_departement,connection);
//        if (res.compareTo(attendu)==0)
//        {
//            return;
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("Normalise à 38 caractères '"+chaine+"'");
//        sb.append(res);
//        sb.append(" (");
//        sb.append(res.length());
//        sb.append(" caractères )");
//        for(int i=0;i<numerosSupplementaires.size();i++)
//        {
//            if (i>0)
//                sb.append(",");
//            else
//                sb.append(" Numeros supplémentaires : ");
//            sb.append(numerosSupplementaires.get(i));
//        }
//        throw(new Exception(sb.toString()));
//    }
//
//    void testNormalise2_ligneX(GestionMots gm,String chaine,int ligne,String resultat_attendu) throws Exception
//    {
//        String res = gm.normalise_2_ligneX(chaine,ligne,true,true);
//        if (res.compareTo(resultat_attendu)!=0)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Normalisation de ");
//            sb.append(chaine);
//            sb.append(" (ligne ");
//            sb.append(ligne);
//            sb.append(" ) : ");
//            sb.append(res);
//            sb.append(" (");
//            sb.append(res.length());
//            sb.append(" caractères )");
//            throw(new Exception(sb.toString()));
//        }
//    }
//    
//    // pour les autres lignes que la 4.
//    void testNormalise38_ligneX(GestionMots gm,String chaine,int ligne,String resultat_attendu) throws Exception
//    {
//        String res;
//        switch(ligne)
//        {
//            default:
//                res = gm.normalise_38_ligneX(chaine);
//                break;
//            case 2:
//                res = gm.normalise_38_ligne2(chaine);
//                break;
//            case 6:
//                res = gm.normalise_38_ligne6(chaine);
//                break;
//        }
//        if (res.compareTo(resultat_attendu)!=0)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Normalisation à 38 caractères la ligne ");
//            sb.append(ligne);
//            sb.append(" de ");
//            sb.append(chaine);
//            sb.append(" : ");
//            sb.append(res);
//            sb.append(" (");
//            sb.append(res.length());
//            sb.append(" caractères )");
//            throw(new Exception(sb.toString()));
//        }
//    }
//
//    @Test
//    public void testNormalise38() throws GestionMotsException, JDOMException, IOException, ClassNotFoundException, SQLException, Exception
//    {
//        // Crée les paramètres et la connexion à la base
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc=new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        if(params.isUtilisationDeLaGestionDesDepartements()){    // si ce parametre n'est pas a true : tous les test de corse et domtom echoueront.
//            GestionCodesDepartements.getInstance().loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        }
//        // Crée les classes de gestion
//        GestionMots gm = new GestionMots();
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm,params);
//        GestionReferentiel gr = new GestionReferentiel(gm,gmaj,params);
//        gm.definitGestionReferentiel(gr);
//        
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        
//        Calendar c = Calendar.getInstance();
//        Date date = c.getTime();
//        
//        int erreur = 0;
//        
//        // Sur la ligne 4, tests d'abbréviation des prénoms:
//        testNormalise38_ligne4(gm,"24 FBG SAINT ANTOINE JACQUES DELORS",db1,new String[]{"75"},"24 FAUBOURG ST AJ DELORS");
//        testNormalise38_ligne4(gm,"RUE MARIE ANDREE LAGROUA WEILL HALLE",db1,new String[]{"75"},"RUE MA LAGROUA WEILL HALLE");
//        
//        // Sur la ligne 4, tests de désabbréviation:
//        testNormalise38_ligne4(gm,"24 26 BD DE L HOPITAL",db1,new String[]{"75"},"24 BOULEVARD DE L HOPITAL");
//        testNormalise38_ligne4(gm,"24 R DES MOUTIERS",db1,new String[]{"75"},"24 RUE DES MOUTIERS");
//        testNormalise38_ligne4(gm,"24 R DES STS PERES",db1,new String[]{"75"},"24 RUE DES SAINTS PERES");
//        // Adaptation referentiel BSPP mai 2011
//        //testNormalise38_ligne4(gm,"24 BD DE M",db1,new String[]{"75"},"24 BOULEVARD DE MONSIEUR");
//        testNormalise38_ligne4(gm,"24 R DE M",db1,new String[]{"75"},"24 RUE DE MONSIEUR");
//
//        testNormalise38_ligne4(gm,"30 R DU FBG ST JACQUES",db1,new String[]{"75"},"30 RUE DU FBG SAINT JACQUES");
//        testNormalise38_ligne4(gm,"ROUTE DE LA PTE DAUPHINE A LA PTE DES SABLONS",db1,new String[]{"75"},"RTE PTE D A LA PTE DES SABLONS");
//        testNormalise38_ligne4(gm,"ROUTE DE LA PTE DES SABLONS A LA PTE MAILLOT",db1,new String[]{"75"},"RTE PTE DES S A LA PTE MAILLOT");
//        testNormalise38_ligne4(gm,"CARREFOUR ROUTE DE LA PORTE DE VERRIERES",db1,new String[]{"75"},"CARR R DE LA PORTE DE VERRIERES");
//        testNormalise38_ligne4(gm,"PLASSE DE GAULLE",db1,new String[]{"75"},"PLASSE DE GAULLE");
//        testNormalise38_ligne4(gm,"12",db1,new String[]{"75"},"12");
//        
//        // Sur la ligne 4,tests les numéros:
//        testNormalise38_ligne4(gm,"DE 24A 26  A28 29 30 BOULEVARD DE L HOPITAL",db1,new String[]{"75"},"24 BOULEVARD DE L HOPITAL");
//        testNormalise38_ligne4(gm,"DU 24 AU 26 ET29 OU30 BOULEVARD DE L HOPITAL",db1,new String[]{"75"},"24 BOULEVARD DE L HOPITAL");
//        
//        // Sur la ligne 4, tests de normalisation à 38 caractères:
//        testNormalise38_ligne4(gm,"24 BOULEVARD DE L HOPITAL DU MARECHAL DE TASSIGNY",db1,new String[]{"75"},"24 BD DE L HOP DU MAL DE TASSIGNY");
//        testNormalise38_ligne4(gm,"1280 BOULEVARD DE L HOPITAL DU MARECHAL DE TASSIGNY",db1,new String[]{"75"},"1280 BD DE L HOP DU MAL DE TASSIGNY");
//        testNormalise38_ligne4(gm,"24 BIS BOULEVARD DE L HOPITAL DU MARECHAL DE TASSIGNY",db1,new String[]{"75"},"24 B BD DE L HOP DU MAL DE TASSIGNY");
//        testNormalise38_ligne4(gm,"24 26 BOULEVARD DE L HOPITAL DU MARECHAL DE LATTRE DE TASSIGNY",db1,new String[]{"75"},"24 BD HOP DU MAL DE L DE TASSIGNY");
//        testNormalise38_ligne4(gm,"24 26 BOULEVARD DE L HOPITAL DE JULIEN DE LATTRE DE TASSIGNY",db1,new String[]{"75"},"24 BD HOP DE J DE L DE TASSIGNY");
//        testNormalise38_ligne4(gm,"24 TER ROND POINT DES ONZE NOVEMBRE 1918 ET HUIT MAI 1945",db1,new String[]{"75"},"24 T RPT 11 N 1918 ET 8 MAI 1945");
//        testNormalise38_ligne4(gm,"24 TER ROND DES ONZE NOVEMBRE 1918 ET HUIT MAI 1945",db1,new String[]{"75"},"24 T ROND 11 N 1918 ET 8 MAI 1945");
//        testNormalise38_ligne4(gm,"24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY",db1,new String[]{"75"},"24 B BD DU MAL DE LATTRE DE TASSIGNY");
//        testNormalise38_ligne4(gm,"24 QUATER AV DU DR BROUARDEL",db1,new String[]{"75"},"24 Q AVENUE DU DOCTEUR BROUARDEL");
//        testNormalise38_ligne4(gm,"24 QUINQUIES DR BROUARDEL",db1,new String[]{"75"},"24 C DOCTEUR BROUARDEL");
//        testNormalise38_ligne4(gm,"24 FAUBOURG SAINT JMLKJMLKJMLKJMLKJMKLJMLKJ JACQUES",db1,new String[]{"75"},"24 FAUB ST J JACQUES");
//        testNormalise38_ligne4(gm,"MQSLJFQMSKLDFJQSMKLDFJQMSJFQSKLDFJQSKLFJQSMDKLFJQSDM",db1,new String[]{"75"},"MQSLJFQMSKLDFJQSMKLDFJQMSJFQSKLDFJQSKLFJQSMDKLFJQSDM");
//        testNormalise38_ligne4(gm,"PLACE DU GENERAL TESSIER DE MARGUERITTES",db1,new String[]{"75"},"PL GAL TESSIER DE MARGUERITTES");
//        testNormalise38_ligne4(gm,"AVENUE DU ROI HUSSEIN IER DE JORDANIE",db1,new String[]{"75"},"AV DU ROI H IER DE JORDANIE");
//        testNormalise38_ligne4(gm,"HABITAT LOYER MODERE TEST TEST TEST TEST",db1,new String[]{"75"},"HABI LOYER MODERE T T TEST TEST");
//        
//        // Sur la ligne 6, test de normalisation à 38 caractères:
//        testNormalise38_ligneX(gm,"51513 SAINT REMY EN BOUZEMONT SAINT GENEST ET ISSON",6,"51513 ST R EN B ST GENEST ET ISSON");
//        testNormalise38_ligneX(gm,"80714 SAINT QUENTIN LA MOTTE CROIX AU BAILLY",6,"80714 ST Q LA MOTTE CRX AU BAILLY");
//        testNormalise38_ligneX(gm,"76039 LES AUTHIEUX SUR LE PORT SAINT OUEN",6,"76039 LES AUTHIEUX SUR LE PORT ST OUEN");
//        testNormalise38_ligneX(gm,"SAINT MARTIN LARS EN SAINTE HERMINE",6,"ST MARTIN LARS EN SAINTE HERMINE");
//        testNormalise38_ligneX(gm,"SAINT JEAN DE MARUEJOLS ET AVEJAN",6,"ST JEAN DE MARUEJOLS ET AVEJAN");
//        // normalisation maximale atteinte, les 38 caractères sont passés pour cet exemple.
//        testNormalise38_ligneX(gm,"76039 LES AUTHIEUX SUR LE PORT SAINT OUEN 06 CEDEX",6,"76039 LES AUTHIEUX SUR LE PORT ST OUEN 06 CEDEX");
//        
//        // Sur une ligne classique, test de désabbréviation:
//        testNormalise2_ligneX(gm,"APP 70 ENT B",2,"APPARTEMENT 70 ENTREE B");
//        testNormalise2_ligneX(gm,"VIEUX CHE DE GAIRAUT",2,"VIEUX CHEMIN DE GAIRAUT");
//        testNormalise2_ligneX(gm,"SAINT MARTIN LARS EN SAINTE HERMINE",6,"ST MARTIN LARS EN SAINTE HERMINE");
//        
//        // Sur une ligne classique, test de normalisation à 38 caractères:
//        testNormalise38_ligneX(gm,"24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY",5,"24 B B DU MAL DE LATTRE DE TASSIGNY");
//        
//        // Test sur une adresse complète:
//        testNormaliseAdresse(gm,"","24 26 BOULEVARD DE L HOPITAL DE LATTRE DE TASSIGNY","75","75",db1,"","24 BD HOP DE LATTRE DE TASSIGNY","75");
//        testNormaliseAdresse(gm,"","24 B BD DU MAL DE LATTRE DE TASSIGNY","75008","75",db1,"","24 B BD DU MAL DE LATTRE DE TASSIGNY","75008");
//        testNormaliseAdresse(gm,"","24 B BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","75008","75",db1,"","24 B BD DU MAL DE LATTRE DE TASSIGNY","75008");
//        testNormaliseAdresse(gm,"","ROUTE DE LA PTE DES SABLONS A LA PTE MAILLOT","75","75",db1,"","RTE PTE DES S A LA PTE MAILLOT","75");
//        testNormaliseAdresse(gm,"","24 26 BIS RUE DE PARIS","93000 BOBIGNY","75",db1,"","24 RUE DE PARIS","93000 BOBIGNY");
//        testNormaliseAdresse(gm,"","024 026 BIS RUE DE PARIS","01 BOBIGNY","75",db1,"","24 RUE DE PARIS","01 BOBIGNY");
//        // WA 24/10/2011 : test ajoutes a la rev. 120 : mis en commentaire jousqu'a resolution du mantis 0002423
////        testNormaliseAdresse(gm,"","VIEUX CHE DE GAIRAUT","75 PARIS","75",db1,"","VIEUX CHEMIN DE GAIRAUT","75 PARIS");
//        testNormaliseAdresse(gm,"","VX CHE DE GAIRAUT","75 PARIS","75",db1,"","VIEUX CHEMIN DE GAIRAUT","75 PARIS");
//        // Le test suivant est pertinent si le terme BOITES AUX LETTRES est ajouté au fichier abbreviations.xml avec 
//        // pour abbréviation BAL.
//        //testNormaliseAdresse(gm,"BOITES AUX LETTRES 18 APPT 7500 ETAGE 1800","","75 PARIS","75",db1,"BAL 18 APPT 75 ETAGE 180","","75 PARIS");
//
//        // WA 09/2011 Verification abreviations
//        testNormalise38_ligne4(gm,"PLACE DU MARECHAL DE LATTRE DE TASSIGNY",db1,new String[]{"75"},"PL DU MAL DE LATTRE DE TASSIGNY");
//        testNormalise38_ligne4(gm,"110 120 PLACE DU MARECHAL DE LATTRE DE TASSIGNY",db1,new String[]{"75"},"110 PL DU MAL DE LATTRE DE TASSIGNY");
//
//        testNormalise38_ligneX(gm,"PLACE DU MARECHAL DE LATTRE DE TASSIGNY",4,"PLACE DU MAL DE LATTRE DE TASSIGNY");
//        testNormalise38_ligneX(gm,"110 120 PLACE DU MARECHAL DE LATTRE DE TASSIGNY",4,"110 120 P DU MAL DE LATTRE DE TASSIGNY");
//
//        // WA 01/2012 Pays tests a 7 lignes
//        testNormaliseFull(gm, new String[]{"", "", "", "PLACE DU MARECHAL DE LATTRE DE TASSIGNY"}, "75", false, db1, new String[]{"", "", "", "PL DU MAL DE LATTRE DE TASSIGNY", "", ""});
//        testNormaliseFull(gm, new String[]{"", "", "", "24 26 BOULEVARD DE L HOPITAL DE LATTRE DE TASSIGNY", "", "75"}, "75", false, db1, new String[]{"", "", "", "24 BD HOP DE LATTRE DE TASSIGNY", "", "75", ""});
//        testNormaliseFull(gm, new String[]{"", "", "", "24 26 BOULEVARD DE L HOPITAL DE LATTRE DE TASSIGNY", "", "75", "FRANCE"}, "75", false, db1, new String[]{"", "", "", "24 BD HOP DE LATTRE DE TASSIGNY", "", "75"});
//        testNormaliseFull(gm, new String[]{"", "", "", "24 26 BOULEVARD DE L HOPITAL DE LATTRE DE TASSIGNY", "", "75", "FRANCE"}, "75", true, db1, new String[]{"", "", "", "24 BD HOP DE LATTRE DE TASSIGNY", "", "75", "FRANCE", ""});
//        testNormaliseFull(gm, new String[]{"TOTO 000001", "TUTU 000000002", "TATA 0000003","0000000000004 00000000000005 TYTY", "", "75", "UN PAYS OU UN AUTRE"}, "75", true, db1, new String[]{"TOTO 000001", "TUTU 2", "TATA 3","4 TYTY", "", "75", "UN PAYS OU UN AUTRE", ""});
//        testNormaliseFull(gm, new String[]{"MS S POLLARD", "1 CHAPEL STREET", "HESWALL","BOURNEMOUTH", "", "BH1 1AA", "ROYAUME UNI"}, "75", true, db1, new String[]{"MS S POLLARD", "1 CHAPEL STREET", "HESWALL","BOURNEMOUTH", "", "BH1 1AA", "ROYAUME UNI"});
//        testNormaliseFull(gm, new String[]{"MS S POLLARD", "1 CHAPEL STREET", "HESWALL","BOURNEMOUTH", "", "BH1 1AA", "ROYAUME UNI"}, "75", false, db1, new String[]{"MS S POLLARD", "1 CHAPEL STREET", "HESWALL","BOURNEMOUTH", "", "BH1 1AA" });
//    }
//    
//    @Test
//    public void testNumeros() throws Exception
//    {
//        // Crée les paramètres et la connexion à la base
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc=new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//
//        // Crée les classes de gestion
//        GestionMots gm = new GestionMots();
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm,params);
//        GestionReferentiel gr = new GestionReferentiel(gm,gmaj,params);
//        gm.definitGestionReferentiel(gr);
//        if(params.isUtilisationDeLaGestionDesDepartements())    // si ce parametre n'est pas a true : tous les test de corse et domtom echoueront.
//            GestionCodesDepartements.getInstance().loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        
//        // Sur la ligne 4,teste les numéros:
//        testNormalise38_ligne4(gm,"DE 24 BIS A 26  A28 29 30 BOULEVARD DE L HOPITAL",db1,new String[]{"75"},"24 B BOULEVARD DE L HOPITAL");
//        testNormalise38_ligne4(gm,"DU 24 AU 26 ET29TER OU30Q BOULEVARD DE L HOPITAL",db1,new String[]{"75"},"24 BOULEVARD DE L HOPITAL");
//        testNormalise38_ligne4(gm,"DES24 AUX 36 37 TER ET 48B OU 64A A 48 BOULEVARD DE L HOPITAL",db1,new String[]{"75"},"24 BOULEVARD DE L HOPITAL");
//        testNormalise38_ligne4(gm,"LES24OU36ET37TER ET 48B 64A A 48 BIS BOULEVARD DE L HOPITAL",db1,new String[]{"75"},"24 BOULEVARD DE L HOPITAL");
//        testNormalise38_ligne4(gm,"24 26 FBG ST JACQUES",db1,new String[]{"75"},"24 FAUBOURG SAINT JACQUES");
//        testNormalise38_ligne4(gm,"24 26 R ST JACQUES",db1,new String[]{"75"},"24 RUE SAINT JACQUES");
//        testNormalise38_ligne4(gm,"24 26 TER ROND POINT DES ONZE NOVEMBRE 1918 ET HUIT MAI 1945",db1,new String[]{"75"},"24 RPT 11 N 1918 ET 8 MAI 1945");
//        testNormalise38_ligne4(gm,"0024 0026 TER ROND POINT DES ONZE NOVEMBRE 1918 ET HUIT MAI 1945",db1,new String[]{"75"},"24 RPT 11 N 1918 ET 8 MAI 1945");
//    }
//    
//    void testDesabbreviation(GestionMots gm,String chaine,ArrayList<String> codes_departement,Connection connection,String resultat_attendu) throws SQLException, Exception
//    {
//        String res = gm.corrige_abbreviations_ligne4(chaine,codes_departement,connection);
//        
//        if (res.compareTo(resultat_attendu)!=0)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Désabbrévie '"+chaine+"' :");
//            sb.append(res);
//            sb.append(" (");
//            sb.append(res.length());
//            sb.append(" caractères )");
//            throw(new Exception(sb.toString()));
//        }
//    }
//    
//    void testDesabbreviation(GestionMots gm,int ligne,String chaine,String resultat_attendu) throws Exception
//    {
//        System.out.println();
//        String res = gm.corrige_abbreviations_ligneX(chaine,ligne);
//        if (res.compareTo(resultat_attendu)!=0)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Désabbrévie '"+chaine+"' :");
//            sb.append(res);
//            sb.append(" (");
//            sb.append(res.length());
//            sb.append(" caractères )");
//            throw(new Exception(sb.toString()));
//        }
//    }
//    
//    @Test
//    public void testDesabbreviation() throws Exception
//    {
//        // Crée les paramêtres et obtient la connexion à la base
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc=new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        Base.loadParameters(db1,params);
//        
//        // Crée les classes de gestion
//        GestionMots gm = new GestionMots();
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm,params);
//        GestionReferentiel gr = new GestionReferentiel(gm,gmaj,params);
//        gm.definitGestionReferentiel(gr);
//        if(params.isUtilisationDeLaGestionDesDepartements())    // si ce parametre n'est pas a true : tous les test de corse et domtom echoueront.
//            GestionCodesDepartements.getInstance().loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        
//        // Sur la ligne 4:
//        ArrayList<String> departements = new ArrayList<String>();
//        departements = new ArrayList<String>();
//        departements.add("75");
//        testDesabbreviation(gm,"24 26 BD DE L HOPITAL",departements,db1,"24 26 BOULEVARD DE L HOPITAL");
//        testDesabbreviation(gm,"24 26 AV DU GAL FERRIE",departements,db1,"24 26 AVENUE DU GENERAL FERRIE");
//        testDesabbreviation(gm,"24 QUATER AV DU DR BROUARDEL",departements,db1,"24 QUATER AVENUE DU DOCTEUR BROUARDEL");
//        // Adaptation referentiel BSPP mai 2011
//        //testDesabbreviation(gm,"24 BIS PL LT STEPHANE PIOBE",departements,db1,"24 BIS PLACE LIEUTENANT STEPHANE PIOBE");
//        testDesabbreviation(gm,"24 BIS PL LT PIOBETU",departements,db1,"24 BIS PLACE LIEUTENANT PIOBETU");
//        testDesabbreviation(gm,"24 TER RUE M",departements,db1,"24 TER RUE MONSIEUR");
//        
//        // Sur la ligne 4 (ne doit pas être corrigé car GAL PROUT n'existe pas):
//        testDesabbreviation(gm,"24 26 BD GAL PROUT",departements,db1,"24 26 BOULEVARD GAL PROUT");
//        
//        // Sur une ligne classique:
//        testDesabbreviation(gm,5,"24 BIS BD DU MAL DE LATTRE DE TASSIGNY","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY");
//        
//        testDesabbreviation(gm,5,"VIEUX CHE DE GAIRAUT","VIEUX CHEMIN DE GAIRAUT");
//        testDesabbreviation(gm,5,"VX CHE DE GAIRAUT","VIEUX CHEMIN DE GAIRAUT");
//    }
//    
//    void testRestructure(GestionMots gm,String[] chaines,String[] attendus, boolean gestPays, Connection connection) throws SQLException, Exception
//    {
//        Calendar c1 = Calendar.getInstance();
//        String[] res = gm.restructure(chaines, true, gestPays, connection);
//        Calendar c2 = Calendar.getInstance();
//
//        int error = 0;
//        if (res!=null)
//        {
//            for(int i=0;i<attendus.length;i++)
//            {
//                if (res[i].compareTo(attendus[i])!=0)
//                    error = 1;
//            }
//        }
//        else
//            error = 1;
//        
//        if (error==1)
//        {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Cherche les éléments présents dans :\r\n");
//            for(int i=0;i<chaines.length;i++)
//            {
//                if (i>0) sb.append("\r\n");
//                sb.append(chaines[i]);
//            }
//            sb.append("Temps de calcul (ms): "+(c2.getTimeInMillis()-c1.getTimeInMillis())+"\r\n");
//            sb.append("Résultat :\r\n");
//            if (res!=null)
//                for(int i=0;i<res.length;i++)
//                {
//                    sb.append("Ligne "+(i+1)+": "+res[i]+"\r\n");
//                }
//            else
//                sb.append(" (null)");
//            throw(new Exception(sb.toString()));
//        }
//    }
//    
//    @Test
//    public void testRestructure() throws Exception
//    {
//        // Crée les paramètres et la connexion à la base
//        JDONREFParams params = new JDONREFParams();
//        params.load("params.xml");
//        GestionConnection gc=new GestionConnection(params);
//        gc.load("connections.xml");
//        Connection db1 = gc.obtientConnection().connection;
//        Base.loadParameters(db1,params);
//        
//        // Crée les classes de gestion
//        GestionMots gm = new GestionMots();
//        gm.definitJDONREFParams(params);
//        GestionMiseAJour gmaj = new GestionMiseAJour(gm,params);
//        GestionReferentiel gr = new GestionReferentiel(gm,gmaj,params);
//        gm.definitGestionReferentiel(gr);
//        GestionCodesDepartements gcdpt = GestionCodesDepartements.getInstance();
//        
//        gm.loadAbbreviation("abbreviations.xml");
//        gm.loadCles("cles.xml");
//        gm.loadPrenoms("prenoms.txt");
//        if(params.isUtilisationDeLaGestionDesDepartements())    // si ce parametre n'est pas a true : tous les test de corse et domtom echoueront.
//            gcdpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        
//        // Permet de choisir la localisation des tests a effectuer
//        // Dépend de l'échantillon considéré pour les tests.
//        boolean paris = true;
//        boolean petite_couronne = true;
//        boolean banlieue = false;
//        boolean divers = true;
//        
//        if (paris || petite_couronne)
//        {
//            testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","",""},
//                    new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","","75019 PARIS APPT 33 ETAGE 3 44 RUE DE THIONVILLE","",""},
//                    new String[]{"","APPT 33 ETAGE 3","","44 RUE DE THIONVILLE","","75019 PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","PARIS","59500","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","",""},
//                    new String[]{"","PARIS","","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","","59500","59"}, false, db1);
//            testRestructure(gm, new String[]{"", "24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY", "ENTREE A APPT 70 PARIS", "75013", "", ""},
//                    new String[]{"", "APPT 70", "ENTREE A", "24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY", "", "75013 PARIS", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "24 B BD DU MARECHAL DE LATTRE DE TASSIGNY", "", "ENTREE A APPT 70", "PARIS", "75013"},
//                    new String[]{"", "APPT 70", "ENTREE A", "24 B BD DU MARECHAL DE LATTRE DE TASSIGNY", "", "75013 PARIS", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "APT 70 ENT B", "DE 24 A 26 FBG ST JACQUES", "75013 PARIS 13 EME", "", ""},
//                    new String[]{"", "APT 70", "ENT B", "DE 24 A 26 FBG ST JACQUES", "", "75013 PARIS 13 EME", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "RUE DE PANTON", "75013", "", ""},
//                    new String[]{"", "", "", "RUE DE PANTON", "", "75013", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "PORTE A", "RUE DE PANTON", "75013", "", ""},
//                    new String[]{"", "PORTE A", "", "RUE DE PANTON", "", "75013", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "", "24 FAUBOURG SAINT JACQUES 75", "", ""},
//                    new String[]{"", "", "", "24 FAUBOURG SAINT JACQUES", "", "75", "75"},  false, db1);
////            testRestructure(gm, new String[]{"", "", "", "8 RUE ES DEUX PONTS PARIS 4", "", ""},
////                    new String[]{"", "", "", "8 RUE ES DEUX PONTS", "", "PARIS 4", "75"}, db1);
//            testRestructure(gm, new String[]{"", "", "", "14 CHAUMONT 75 PARIS", "", ""},
//                    new String[]{"", "", "", "14 CHAUMONT", "", "75 PARIS", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "PARIS 10", "", "", ""},
//                    new String[]{"", "", "", "", "", "PARIS 10", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "", "MONSIEUR MOQUET RUE DE THIONVILLE 75 PARIS", "", ""},
//                    new String[]{"MONSIEUR MOQUET", "", "", "RUE DE THIONVILLE", "", "75 PARIS", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "", "MONSIEUR MOQUET 75 PARIS RUE DE THIONVILLE", "", ""},
//                    new String[]{"MONSIEUR MOQUET", "", "", "RUE DE THIONVILLE", "", "75 PARIS"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "", "CENTRE COMMERCIAL CARREFOUR RUE DE THIONVILLE 75019 PARIS", "", ""},
//                    new String[]{"CENTRE COMMERCIAL CARREFOUR", "", "", "RUE DE THIONVILLE", "", "75019 PARIS", "75"},  false, db1);
//            testRestructure(gm,new String[]{"","","","CHEZ MONSIEUR MOQUET RUE DE THIONVILLE 75019 PARIS","",""},
//                    new String[]{"","CHEZ MONSIEUR MOQUET","","RUE DE THIONVILLE","","75019 PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","","RUE DE THIONVILLE CHEZ MOQUET 75019 PARIS","",""},
//                    new String[]{"","CHEZ MOQUET","","RUE DE THIONVILLE","","75019 PARIS","75"}, false, db1);
//            
//            testRestructure(gm,new String[]{"","APPARTEMENT 70","","24 BD HOPITAL PARIS","",""},
//                    new String[]{"","APPARTEMENT 70","","24 BD HOPITAL","","PARIS","75"}, false, db1);
//            
//            testRestructure(gm,new String[]{"","RN 70","","24 BD HOPITAL PARIS","",""},
//                    new String[]{"","RN 70","","24 BD HOPITAL","","PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","","RUE TRUCMCUHE","","PARIS"},
//                    new String[]{"","","","RUE TRUCMCUHE","","PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","","24 BIDON SAINT JACQUES","","75013 PARIS 13 EME"},
//                    new String[]{"","","","24 BIDON SAINT JACQUES","","75013 PARIS 13 EME","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","","AVENUE PORTE DE GENTILLY","","7 5 PARIS"},
//                    new String[]{"","","","AVENUE PORTE DE GENTILLY","","PARIS 7 5","75"}, false, db1);
//            
//            // Les deux tests qui suivent ne sont valables
//            // que si le référentiel contient "PORTE DE PANTIN" dans le 75.
//            PreparedStatement psConditionTest = db1.prepareStatement("SELECT count(*) FROM voi_voies_75 WHERE voi_nom=?");
//            psConditionTest.setString(1,"PORTE DE PANTIN");
//            ResultSet rsConditionTest = psConditionTest.executeQuery();
//            rsConditionTest.next();
//            int count = rsConditionTest.getInt(1);
//            if (count>0)
//            {
//                testRestructure(gm,new String[]{"","","","PORTE PANTIN","","PARIS"},
//                    new String[]{"","","","PORTE PANTIN","","PARIS","75"}, false, db1); // Attention, non résolu si le référentiel ne contient pas la porte de pantin
//                testRestructure(gm,new String[]{"","","BAL 18","PORTE PANTIN","","PARIS"},
//                    new String[]{"","BAL 18","","PORTE PANTIN","","PARIS","75"}, false, db1); // Attention, non résolu si le référentiel ne contient pas la porte de pantin
//            }
//            
//            testRestructure(gm,new String[]{"","","","75 PARIS BOULEVARD HOPITAL","",""},
//                    new String[]{"","","","BOULEVARD HOPITAL","","75 PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","","BOULEVARD PALAIS PARIS","",""},
//                    new String[]{"","","","BOULEVARD PALAIS","","PARIS","75"}, false, db1); // pb de restructuration
//            // Modif tests unitaires WA 31-05-2011 : discrimination impossible entre PORTE 14 en l.2 ou l.3
////            testRestructure(gm,new String[]{"","","6 EME ETG PORTE 14 8 BD DE LA BASTILLE","","","PARIS"},
////                    new String[]{"","ETG 6 PORTE 14","","8 BD DE LA BASTILLE","","PARIS","75"},db1);
//            testRestructure(gm,new String[]{"","","6 EME ETG PORTE 14 8 BD DE LA BASTILLE","","","PARIS"},
//                    new String[]{"","ETG 6","PORTE 14","8 BD DE LA BASTILLE","","PARIS","75"}, false, db1);
//            
//            testRestructure(gm,new String[]{"","","78 AVENUE FOCH","","","PARIS"},
//                    new String[]{"","","","78 AVENUE FOCH","","PARIS","75"}, false, db1); // une voie contient AVENUE FOCH => AVENUE n'était pas considéré comme un type de voie mais comme un libellé !
//            
//            // Test pertinent si
//            // ALLEE est une clé (et pas seulement un type de voie)
//            // qui a pour abbréviation ALL.
//            // et utilisée dans la ligne 3 NP
//            Abbreviation motAll = gm.chercheAbbreviation("ALL");
//            if (motAll!=null)
//            {
//                Mot motAllee = motAll.obtientMotPrefere(CategorieMot.Cle);
//                if (motAllee!=null && motAllee.est("ALLEE") && motAllee.obtientLigneDeCle()==3)
//                {
//                    testRestructure(gm,new String[]{"","ALL 5 2 RUE FREERIC SCHEINDER","","","","PARIS"},
//                        new String[]{"","","ALL 5","2 RUE FREERIC SCHEINDER","","PARIS","75"}, false, db1);
//                }
//                else motAll=null; // trick
//            }
//            // Sinon, le mécanisme par défaut est utilisé (ALL conservé dans la même ligne)
//            if (motAll==null)
//            {
//                testRestructure(gm,new String[]{"","","ALL 5 2 RUE FREERIC SCHEINDER","","","PARIS"},
//                        new String[]{"","","ALL 5","2 RUE FREERIC SCHEINDER","","PARIS","75"}, false, db1);
//            }
//            
//            testRestructure(gm,new String[]{"","","36 BIS BALARD","","","PARIS"},
//                    new String[]{"","","","36 BIS BALARD","","PARIS","75"}, false, db1); // absence de type de voie réglé
//            
//            // Test pertinent si la clé HALL est référencée, pour la ligne 3.
//            // A noter que ce test est différent du test avec ALL :
//            // 1. HALL n'est pas une abbréviation
//            // 2. TERRASE n'est pas un type de voie (c'est TERRASSE)
//            Mot motHall = gm.chercheMot("HALL");
//            if (motHall!=null && motHall.obtientLigneDeCle()==3)
//            {
//                testRestructure(gm,new String[]{"","","1 TERRASE DU PARC HALL KEPLER","","","PARIS"},
//                        new String[]{"","","HALL KEPLER","1 TERRASE DU PARC","","PARIS","75"}, false, db1); // présence d'un article devant le type de voie
//            }
//            // Sinon, tout est reporté en ligne 4.
//            else
//            {
//                testRestructure(gm,new String[]{"","","1 TERRASE DU PARC HALL KEPLER","","","PARIS"},
//                        new String[]{"","","","1 TERRASE DU PARC HALL KEPLER","","PARIS","75"}, false, db1); // présence d'un article devant le type de voie
//            }
//
//            // Adaptation referentiel BSPP mai 2011
//            //testRestructure(gm,new String[]{"","","107 109 RUE DES PYRENNEES PTE 52","","","PARIS"},
//            //        new String[]{"","PTE 52","","107 109 RUE DES PYRENNEES","","PARIS","75"},db1);
//            //testRestructure(gm,new String[]{"","","7 TERRASSE DU PARC ETG 4 PT 41","","","PARIS"},
//            //        new String[]{"","ETG 4 PT 41","","7 TERRASSE DU PARC","","PARIS","75"},db1);
//            testRestructure(gm,new String[]{"","","107 109 RUE DES PYRENNEES PTE 52","","","PARIS"},
//                    new String[]{"","","PTE 52","107 109 RUE DES PYRENNEES","","PARIS","75"}, false, db1);
////            testRestructure(gm,new String[]{"","","7 TERRASSE DU PARC ETG 4 PT 41","","","PARIS"},
////                    new String[]{"","ETG 4","PT 41","7 TERRASSE DU PARC","","PARIS","75"},db1);
//            
//            testRestructure(gm,new String[]{"","","7 ET 4 ET 5 BD HOPITAL","","","PARIS"},
//                    new String[]{"","","","7 ET 4 ET 5 BD HOPITAL","","PARIS","75"}, false, db1); // gestion des articles
//            
//            testRestructure(gm,new String[]{"","","14 DU CAPORAL PEUGEOT","","","PARIS"},
//                    new String[]{"","","","14 DU CAPORAL PEUGEOT","","PARIS","75"}, false, db1); // un code département ne peut être suivi d'un article
//            
////            testRestructure(gm,new String[]{"","","18 BD HENRI IV QUARTIER DES CELESTINS","","","PARIS"},
////                    new String[]{"","","QUARTIER DES CELESTINS","18 BD HENRI IV","","PARIS","75"},db1); // gestion des multiples types de voies
//            
//            testRestructure(gm,new String[]{"","","33 34 ET 35 BIS RUE DAVID D ANGERS","","","PARIS"},
//                    new String[]{"","","","33 34 ET 35 BIS RUE DAVID D ANGERS","","PARIS","75"}, false, db1); // un code de département ne peut être suivi de suite de numéro suivi par article, répétition, ou type de voie
//            
//            testRestructure(gm,new String[]{"","","8 RUE ALPHONSE CARR","","","PARIS"},
//                    new String[]{"","","","8 RUE ALPHONSE CARR","","PARIS","75"}, false, db1); // de préférence, le type de voie choisi ne termine pas une ligne
//            
//            testRestructure(gm,new String[]{"","","LES JARDINS DE LA MOUSAYA","","","PARIS"},
//                    new String[]{"","","LES JARDINS DE LA MOUSAYA","","","PARIS","75"}, false, db1); // les libellés sans type de voie doivent disposer d'au moins un numéro
//            
//            // Le test suivant nécessite que le terme HALL soit répertorié comme type de voie
//            Mot mot = gm.chercheMot("HALL");
//            if (mot!=null && mot.estDeLaCategorie(CategorieMot.TypeDeVoie))
//            {
//                testRestructure(gm,new String[]{"","","TERRASSE DU PARC CHEZ MR BOUTIN 9 HALL COPERINC","","","PARIS"},
//                      new String[]{"","CHEZ MR BOUTIN","TERRASSE DU PARC","9 HALL COPERINC","","PARIS","75"}, false, db1); // le hall copernic n'est pas référencé dans le référentiel
//            }
//         }
//        if (petite_couronne)
//        {
//            testRestructure(gm,new String[]{"","","","ESCALIER B APPT 70 3 AVENUE PAUL ELUARD 93000 BOBIGNY","",""},
//                    new String[]{"","ESCALIER B APPT 70","","3 AVENUE PAUL ELUARD","","93000 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","APPT 70","","ESCALIER B 3 AVENUE PAUL ELUARD 93000 BOBIGNY","",""},
//                    new String[]{"","APPT 70 ESCALIER B","","3 AVENUE PAUL ELUARD","","93000 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","","","ESCALIER B 1 ER APPT BATIMENT C 24 B 26 AVENUE PAUL ELUARD 93 BOBIGNY","",""},
//                    new String[]{"","ESCALIER B APPT 1","BATIMENT C","24 B 26 AVENUE PAUL ELUARD","","93 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","24 BIS RUE DE PARIS","BOBIGNY 93000","","",""},
//                    new String[]{"","","","24 BIS RUE DE PARIS","","93000 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","","","92013 CHALON SUR SAONE 13 RUE DE PARIS","",""},
//                    new String[]{"","","","13 RUE DE PARIS","","92013 CHALON SUR SAONE","92"}, false, db1);
//            testRestructure(gm,new String[]{"","","","CENTRE COMMERCIAL CARREFOUR RUE DE THIONVILLE 93019 BOBIGNY PANTIN","",""},
//                    new String[]{"CENTRE COMMERCIAL CARREFOUR","","","RUE DE THIONVILLE","PANTIN","93019 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","","","","ROSNY SOUS BOIS BOBIGNY","93000"},
//                    new String[]{"","","","","ROSNY SOUS BOIS","93000 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","","","","BOBIGNY ROSNY SOUS BOIS","93000"},
//                    new String[]{"","","","","BOBIGNY","93000 ROSNY SOUS BOIS","93"}, false, db1);
//            testRestructure(gm,new String[]{"","24 BIS RUE DE PARIS","BOBIGNY 93000","","",""},
//                    new String[]{"","","","24 BIS RUE DE PARIS","","93000 BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","","","AVENUE PAUL ELUARD BOBIGNY","",""},
//                    new String[]{"","","","AVENUE PAUL ELUARD","","BOBIGNY","93"}, false, db1);
////            testRestructure(gm,new String[]{"","","","","","9 O SAINT MAUR IES FOSSES"},
////                    new String[]{"","","","","","SAINT MAUR IES FOSSES 9 O","94"},db1); // les communes doivent faire parti des ambiguites des autres départements
//            testRestructure(gm,new String[]{"","","","94400 VITRY SUR SEINE 4 PLACE POULAGA","",""},
//                    new String[]{"","","","4 PLACE POULAGA","","94400 VITRY SUR SEINE","94"}, false, db1); // pb de restructuration avec ambiguité
//        }
//        if (banlieue)
//        {
//            testRestructure(gm,new String[]{"","","","59500 DOUAI 30 RUE REMY DUHEM","",""},
//                    new String[]{"","","","30 RUE REMY DUHEM","","59500 DOUAI","59"}, false, db1);
//            testRestructure(gm,new String[]{"","24 26 T ROND POINT DU ONZE NOVEMBRE 1918","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","","",""},
//                    new String[]{"","","","24 26 T ROND POINT DU ONZE NOVEMBRE 1918","","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","76"}, false, db1);
//            testRestructure(gm,new String[]{"","","","76039 LES AUTHIEUX SUR LE PORT ST OUEN","",""},
//                    new String[]{"","","","","","76039 LES AUTHIEUX SUR LE PORT ST OUEN","76"}, false, db1);
//            testRestructure(gm,new String[]{"","LES AUTHIEUX SUR LE PART SAINT OUEN","","76039","",""},
//                    new String[]{"","","","","","76039 LES AUTHIEUX SUR LE PART SAINT OUEN","76"}, false, db1);
//            testRestructure(gm,new String[]{"","","","","","VILLERS AUX BOIS"},
//                    new String[]{"","","","","","VILLERS AUX BOIS","51"}, false, db1);
//            testRestructure(gm,new String[]{"","","","","","VILLIERS AUX BOIS"},
//                    new String[]{"","","","","","VILLIERS AUX BOIS",""}, false, db1); // Pb : ne trouve pas le département de VILLERS AUX BOIS
//            testRestructure(gm,new String[]{"","","","RUE DE PARIS 59 ","DOUAI",""},
//                    new String[]{"","","","RUE DE PARIS","","59 DOUAI","59"}, false, db1);
//            testRestructure(gm,new String[]{"","","","RUE DE LILLE 59","",""},
//                    new String[]{"","","","RUE DE LILLE","","59","59"}, false, db1); // attention aux types de voies! (Nouvelle rue)
//            testRestructure(gm,new String[]{"","","RUE DU GRAND ESCALIER","","","83"},
//                    new String[]{"","","","RUE DU GRAND ESCALIER","","83","83"}, false, db1); // pb de restructuration avec ambiguité
//            testRestructure(gm,new String[]{"","","IMPASSE DU PETIT PARIS","","","02"},
//                    new String[]{"","","","IMPASSE DU PETIT PARIS","","02","02"}, false, db1); // pb de restructuration avec ambiguité
//        }
//        if (divers)
//        {
//            testRestructure(gm,new String[]{"","","1 ER CHEMIN","","",""},
//                    new String[]{"","","","1 ER CHEMIN","",""}, false, db1);
//            testRestructure(gm,new String[]{"","","","AUTOROUTE A 11","",""},
//                    new String[]{"","","","AUTOROUTE A 11","",""}, false, db1);
//            testRestructure(gm,new String[]{"","","","CHARLES DE GAULLE","","SAINT"},
//                    new String[]{"","","","CHARLES DE GAULLE","","SAINT"}, false, db1);
//        }
//
//
//        // WA 14/09/2011 Ajout des tests unitaires pour la corse et les dom toms
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            testRestructure(gm, new String[]{"", "", "83 RUE CHMOLL", "", "", ""}, new String[]{"", "", "", "83 RUE CHMOLL", "", "", ""},  false, db1);
//            testRestructure(gm, new String[]{"", "", "2 A RUE CHMOLL", "", "", ""}, new String[]{"", "", "", "2 A RUE CHMOLL", "", "", ""},  false, db1);
//            testRestructure(gm, new String[]{"", "", "20 A RUE CHMOLL", "", "", ""}, new String[]{"", "", "", "20 A RUE CHMOLL", "", "", ""},  false, db1);
//            if (gcdpt.isDptCodePresent("20 A"))
//            {
//                testRestructure(gm, new String[]{"", "", "20 RUE CHMOLL 20 A", "", "", ""}, new String[]{"", "", "", "20 RUE CHMOLL", "", "20 A", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "20 A RUE CHMOLL 20 A", "", "", ""}, new String[]{"", "", "", "20 A RUE CHMOLL", "", "20 A", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "RUE CHMOLL 20 A", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "20 A", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "RUE CHMOLL 2 A", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "2 A", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "2 A", "", "", ""}, new String[]{"", "", "", "", "", "2 A", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "20 A", "", "", ""}, new String[]{"", "", "", "", "", "20 A", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "75 20 A", "", ""}, new String[]{"", "", "75", "", "", "20 A", "20 A"},  false, db1);
//                // Je ne sais que penser de celui ci ... apres tout 75 fait une commune tout a fait acceptable ...
//                testRestructure(gm, new String[]{"", "", "20 A 75", "", ""}, new String[]{"", "", "", "", "", "20 A 75", "20 A"},  false, db1);
//                
//                testRestructure(gm, new String[]{"", "", "CHEMIN DE LA VERDOLINE 20 A DIGNE LES BAINS", "", ""}, new String[]{"", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A DIGNE LES BAINS", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "CHEMIN DE LA VERDOLINE 2 A DIGNE LES BAINS", "", ""}, new String[]{"", "", "", "CHEMIN DE LA VERDOLINE", "", "2 A DIGNE LES BAINS", "20 A"},  false, db1);
//            }
//            if (gcdpt.isDptCodePresent("20 B"))
//            {
//                testRestructure(gm, new String[]{"", "", "20 B RUE CHMOLL RISTOLIS", "", "", ""}, new String[]{"", "", "", "20 B RUE CHMOLL", "", "RISTOLIS", "20 B"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "20 B RUE CHMOLL 20 B RISTOLIS", "", "", ""}, new String[]{"", "", "", "20 B RUE CHMOLL", "", "20 B RISTOLIS", "20 B"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "RUE TURLUTUTU 20 B BRUNET", "", ""}, new String[]{"", "", "", "RUE TURLUTUTU", "", "20 B BRUNET", "20 B"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "RUE TURLUTUTU 20 B", "", ""}, new String[]{"", "", "", "RUE TURLUTUTU", "", "20 B", "20 B"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75 20 B", "", "", ""}, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75", "", "", "20 B", "20 B"},  false, db1);
//            }
//            if (gcdpt.isDptCodePresent("83"))
//            {
//                testRestructure(gm, new String[]{"", "", "RUE CHMOLL 83", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "83", "83"},  false, db1);
//            }
//            testRestructure(gm, new String[]{"", "", "APPT 83", "", "", ""}, new String[]{"", "APPT 83", "", "", "", "", ""},  false, db1);
//            testRestructure(gm, new String[]{"", "", "APPT 2 A", "", "", ""}, new String[]{"", "APPT 2 A", "", "", "", "", ""},  false, db1);
//            testRestructure(gm, new String[]{"", "", "APPT 20 A", "", "", ""}, new String[]{"", "APPT 20 A", "", "", "", "", ""},  false, db1);
//            testRestructure(gm, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75 75", "", "", ""}, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75", "", "", "75", "75"},  false, db1);
//            if (gcdpt.isDptCodePresent("04")&& gcdpt.isDptCodePresent("20 A"))
//            {
//                // Pour que ces tests ait un sens, il faut que D 20 A soit une voie avec ambiguite de type 'codeDepartementDansVoie'
//                // -> a remplacer par un cas reel une fois un referentiel mis a jour pour la corse.
//                testRestructure(gm, new String[]{"", "", "", "RUE D 20 A BRUNET", "", ""}, new String[]{"", "", "", "RUE D 20 A", "", "BRUNET", "04,20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "RUE CHMOLL 20 A BRUNET", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "20 A BRUNET", "20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "", "D 20 A BRUNET", "", ""}, new String[]{"", "", "", "D 20 A", "", "BRUNET", "04,20 A"},  false, db1);
//                testRestructure(gm, new String[]{"", "", "RUE D 20 B BRUNET", "", ""}, new String[]{"", "", "", "RUE D 20 B", "", "BRUNET", "04,20 A"},  false, db1);
//            }
//            testRestructure(gm, new String[]{"", "", "RUE D 20 A", "", ""}, new String[]{"", "", "", "RUE D 20 A", "", "", ""},  false, db1);
//            testRestructure(gm, new String[]{"", "", "2 A", "", "75 PARIS", ""}, new String[]{"", "", "2 A", "", "", "75 PARIS", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "83 75", "", ""}, new String[]{"", "", "83", "", "", "75", "75"},  false, db1);
//            
//        }
//
//        // WA 01/2012 Pays
//        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS FRANCE","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS FRANCE","75"}, false, db1);
//        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS FRANCE","","", ""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS", "FRANCE", "75"}, true, db1);
//        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS BANGLADESH","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS BANGLADESH","75"}, false, db1);
//        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS BANGLADESH","","", ""},
//                new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","","","BANGLADESH", ""}, true, db1);
//        testRestructure(gm,new String[]{"BANGLADESH","","","","","",""},
//                new String[]{"","","","","","","BANGLADESH", ""}, true, db1);
//        testRestructure(gm,new String[]{"BENGLADESH","","","","","",""},
//                new String[]{"","","","","","","BENGLADESH", ""}, true, db1);
//        testRestructure(gm,new String[]{"BURKINA FASO","","","","","",""},
//                new String[]{"","","","","","","BURKINA FASO", ""}, true, db1);
//        testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL BURKINA FASO","","","","","",""},
//                new String[]{"24 26 BD DE L HOPITAL","","","","","","BURKINA FASO", ""}, true, db1);
//        testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL BURKINA FAZO","","","","","",""},
//                new String[]{"24 26 BD DE L HOPITAL","","","","","","BURKINA FAZO", ""}, true, db1);
//        testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL ETAT PLURINATIONAL DE BOLIVIE","","","","","",""},
//                new String[]{"24 26 BD DE L HOPITAL","","","","","","ETAT PLURINATIONAL DE BOLIVIE", ""}, true, db1);
//        testRestructure(gm,new String[]{"", "24 26 BD DE L HOPITAL ETAT PLURINATIONAL DE BOLIVIE","","","","",""},
//                new String[]{"","24 26 BD DE L HOPITAL","","","","","ETAT PLURINATIONAL DE BOLIVIE", ""}, true, db1);
//
//        testRestructure(gm,new String[]{"FRANCE", "APPARTEMENT 70 ENTREE B","","24 26 TER RUE DES 4 CHENES","","76039 LES AUTHIEUX SUR LE PART SAINT OUEN",""},
//                new String[]{"","APPARTEMENT 70","ENTREE B","24 26 TER RUE DES 4 CHENES","","76039 LES AUTHIEUX SUR LE PART SAINT OUEN","FRANCE", "76"}, true, db1);
//        
//        if (gcdpt.isDptCodePresent("67"))
//        {
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARTIN 67","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL", "", "67 SAINT MARTIN", "67"}, false, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARIN 67","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL SAINT MARIN", "", "67", "67"}, false, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARIN","","","","",""},
//                new String[]{"24 26 BD DE L HOPITAL","","","", "", "", "SAINT MARIN", ""}, true, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARIN 67","","","","",""},
//                new String[]{"24 26 BD DE L HOPITAL 67","","","", "", "", "SAINT MARIN", ""}, true, db1);
//        }
//        if (gcdpt.isDptCodePresent("66"))
//        {
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARTIN 66","","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL", "","66 SAINT MARTIN","FRANCE", "66"}, true, db1);
//            testRestructure(gm,new String[]{"", "", "24 26 BD DE L HOPITAL SAINT MARIN 66","","","",""},
//                new String[]{"","","24 26 BD DE L HOPITAL 66","","","","SAINT MARIN", ""}, true, db1);
//        }
//        if (gcdpt.isDptCodePresent("63"))
//        {
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MAURICE 63","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "63"}, false, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MAURICE 63","","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "FRANCE", "63"}, true, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE","","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "FRANCE", "63"}, true, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE MAURICE","","","","","",""},
//                new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE","","","","","", "MAURICE", ""}, true, db1);
//            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE FRANCE","","","","","",""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "FRANCE", "63"}, true, db1);
//        }
//        if (gcdpt.isDptCodePresent("08"))
//        {
//            testRestructure(gm,new String[]{"MENIL ANNELLES","","","","","",""},
//                new String[]{"","","","","","MENIL ANNELLES", "FRANCE", "08"}, true, db1);
//        }
//        if (gcdpt.isDptCodePresent("61"))
//        {
//            testRestructure(gm,new String[]{"LE MENIL SCELLEUR","","","","","",""},
//                new String[]{"","","","","","LE MENIL SCELLEUR", "61"}, false, db1);
//        
//        }
//        if (gcdpt.isDptCodePresent("84"))
//        {
//        testRestructure(gm,new String[]{"LA ROQUE SUR PERNES","","","","","",""},
//                new String[]{"","","","","","LA ROQUE SUR PERNES", "84"}, false, db1);
//        
//        }
//
//        testRestructure(gm,new String[]{"FRANCE","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","","ENTREE A APPT 70","PARIS","75013",""},
//                new String[]{"","APPT 70","ENTREE A","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","","75013 PARIS", "FRANCE", "75"}, true, db1);
//        testRestructure(gm,new String[]{"LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
//                new String[]{"","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
//        testRestructure(gm,new String[]{"CONGO","","","","","",""},
//                new String[]{"","","","","","", "CONGO", ""}, true, db1);
//        testRestructure(gm,new String[]{"LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
//                new String[]{"","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
//        testRestructure(gm,new String[]{"BULGARIE LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
//                new String[]{"BULGARIE","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
//        testRestructure(gm,new String[]{"", "BULGARIE LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","",""},
//                new String[]{"","BULGARIE","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
//        testRestructure(gm,new String[]{"CONGO LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
//                new String[]{"CONGO","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
//        
//        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75013 PARIS","","","","",""},
//                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75013 PARIS", "75"}, false, db1);
//        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75013 PARIS","","","","","", ""},
//                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75013 PARIS", "FRANCE", "75"}, true, db1);
//        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75013 PARIS FRANCE","","","","","", ""},
//                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75013 PARIS", "FRANCE", "75"}, true, db1);
//        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75 PARIS FRANCE","","","","","", ""},
//                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75 PARIS", "FRANCE", "75"}, true, db1);
//        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE FRANCE","","","","","", ""},
//                new String[]{"","","","RUE DES CADETS DE LA FRANCE","","", "FRANCE", ""}, true, db1);
//        testRestructure(gm,new String[]{"","24 BIS RUE DE PARIS","BOBIGNY 93000 FRANCE","","","", ""},
//                new String[]{"","","","24 BIS RUE DE PARIS","","93000 BOBIGNY", "FRANCE", "93"}, true, db1);
//
//        // Pays par defaut
//        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","","", ""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS", "75"}, false, db1);
//        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","","", ""},
//                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS", "FRANCE", "75"}, true, db1);
//
//        db1.close();
//    }
//    
//    @Test
//    public void testTrie() throws Exception
//    {
//        GestionMots gm = new GestionMots();
//        
//        Random r = new Random();
//        int nb_passes = 10;
//        int passes = nb_passes;
//        int min = 3;
//        int max = 10;
//        int minindex = 0;
//        int maxindex = 100;
//        
//        while(passes-->0)
//        {
//            ArrayList<RefCle> cles = new ArrayList<RefCle>();
//            
//            int size = r.nextInt(max-min)+min;
//            
//            StringBuilder sb_cles = new StringBuilder();
//            for(int i=0;i<size;i++)
//            {
//                int index = r.nextInt(maxindex-minindex)+minindex;
//                cles.add(new RefCle("",index));
//                if (i>0)
//                    sb_cles.append(",");
//                sb_cles.append(index);
//            }
//            
//            gm.trie(cles, 0, size-1);
//            
//            boolean error = false;
//            int lastindex = -1;
//            for(int i=0;(!error)&&(i<size);i++)
//            {
//                int templastindex = cles.get(i).obtientIndex();
//                if (templastindex < lastindex)
//                {
//                    error = true;
//                }
//                lastindex = templastindex;
//            }
//            
//            if (error)
//            {
//                StringBuilder chaine = new StringBuilder();
//                chaine.append("Problème avec la mise en ordre de :");
//                chaine.append(sb_cles.toString());
//                chaine.append("devenu :");
//                sb_cles.setLength(0);
//                for (int i = 0; i < size; i++) {
//                    if (i > 0) {
//                        sb_cles.append(",");
//                    }
//                    sb_cles.append(cles.get(i).obtientIndex());
//                }
//                chaine.append(sb_cles.toString());
//                throw(new Exception(chaine.toString()));
//            }
//        }
//    }

}