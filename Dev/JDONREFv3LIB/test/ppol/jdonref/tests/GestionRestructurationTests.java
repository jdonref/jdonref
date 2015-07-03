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
public class GestionRestructurationTests
{
    void testRestructure(GestionMots gm,String[] chaines,String[] attendus, boolean gestPays, Connection connection) throws SQLException, Exception
    {
        testRestructure(gm, chaines, attendus, gestPays, connection, null);
    }
    
    void testRestructure(GestionMots gm,String[] chaines,String[] attendus, boolean gestPays, Connection connection,String dpt) throws SQLException, Exception
    {
        Calendar c1 = Calendar.getInstance();
        String[] res = gm.restructure(chaines, true, gestPays, connection,dpt);
        Calendar c2 = Calendar.getInstance();

        int error = 0;
        if (res!=null)
        {
            for(int i=0;i<attendus.length;i++)
            {
                if (res[i].compareTo(attendus[i])!=0)
                    error = 1;
            }
        }
        else
            error = 1;
        
        if (error==1)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Cherche les éléments présents dans :\r\n");
            for(int i=0;i<chaines.length;i++)
            {
                if (i>0) sb.append("\r\n");
                sb.append(chaines[i]);
            }
            sb.append("Temps de calcul (ms): "+(c2.getTimeInMillis()-c1.getTimeInMillis())+"\r\n");
            sb.append("Résultat :\r\n");
            if (res!=null)
                for(int i=0;i<res.length;i++)
                {
                    sb.append("Ligne "+(i+1)+": "+res[i]+"\r\n");
                }
            else
                sb.append(" (null)");
            throw(new Exception(sb.toString()));
        }
    }
    
    public void testRestructureParisPC(GestionMots gm, Connection db1) throws SQLException, Exception
    {
            testRestructure(gm,new String[]{"","","","97 RUE DE VARENNE PARIS","",""},
                    new String[]{"","","","97 RUE DE VARENNE","","PARIS","75"}, false, db1);
        
            testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","",""},
                    new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS","75"}, false, db1);
            testRestructure(gm,new String[]{"","","","75019 PARIS APPT 33 ETAGE 3 44 RUE DE THIONVILLE","",""},
                    new String[]{"","APPT 33 ETAGE 3","","44 RUE DE THIONVILLE","","75019 PARIS","75"}, false, db1);
            testRestructure(gm,new String[]{"","PARIS","59500","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","",""},
                    new String[]{"","PARIS","","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","","59500","59"}, false, db1);
            testRestructure(gm, new String[]{"", "24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY", "ENTREE A APPT 70 PARIS", "75013", "", ""},
                    new String[]{"", "APPT 70", "ENTREE A", "24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY", "", "75013 PARIS", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "24 B BD DU MARECHAL DE LATTRE DE TASSIGNY", "", "ENTREE A APPT 70", "PARIS", "75013"},
                    new String[]{"", "APPT 70", "ENTREE A", "24 B BD DU MARECHAL DE LATTRE DE TASSIGNY", "", "75013 PARIS", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "APT 70 ENT B", "DE 24 A 26 FBG ST JACQUES", "75013 PARIS 13 EME", "", ""},
                    new String[]{"", "APT 70", "ENT B", "DE 24 A 26 FBG ST JACQUES", "", "75013 PARIS 13 EME", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE DE PANTON", "75013", "", ""},
                    new String[]{"", "", "", "RUE DE PANTON", "", "75013", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "PORTE A", "RUE DE PANTON", "75013", "", ""},
                    new String[]{"", "PORTE A", "", "RUE DE PANTON", "", "75013", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "", "", "24 FAUBOURG SAINT JACQUES 75", "", ""},
                    new String[]{"", "", "", "24 FAUBOURG SAINT JACQUES", "", "75", "75"},  false, db1);
//            testRestructure(gm, new String[]{"", "", "", "8 RUE ES DEUX PONTS PARIS 4", "", ""},
//                    new String[]{"", "", "", "8 RUE ES DEUX PONTS", "", "PARIS 4", "75"}, db1);
            testRestructure(gm, new String[]{"", "", "", "14 CHAUMONT 75 PARIS", "", ""},
                    new String[]{"", "", "", "14 CHAUMONT", "", "75 PARIS", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "", "PARIS 10", "", "", ""},
                    new String[]{"", "", "", "", "", "PARIS 10", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "", "", "MONSIEUR MOQUET RUE DE THIONVILLE 75 PARIS", "", ""},
                    new String[]{"MONSIEUR MOQUET", "", "", "RUE DE THIONVILLE", "", "75 PARIS", "75"},  false, db1);
            testRestructure(gm, new String[]{"", "", "", "MONSIEUR MOQUET 75 PARIS RUE DE THIONVILLE", "", ""},
                    new String[]{"MONSIEUR MOQUET", "", "", "RUE DE THIONVILLE", "", "75 PARIS"},  false, db1);
            testRestructure(gm, new String[]{"", "", "", "CENTRE COMMERCIAL CARREFOUR RUE DE THIONVILLE 75019 PARIS", "", ""},
                    new String[]{"CENTRE COMMERCIAL CARREFOUR", "", "", "RUE DE THIONVILLE", "", "75019 PARIS", "75"},  false, db1);
            testRestructure(gm,new String[]{"","","","CHEZ MONSIEUR MOQUET RUE DE THIONVILLE 75019 PARIS","",""},
                    new String[]{"","CHEZ MONSIEUR MOQUET","","RUE DE THIONVILLE","","75019 PARIS","75"}, false, db1);
            testRestructure(gm,new String[]{"","","","RUE DE THIONVILLE CHEZ MOQUET 75019 PARIS","",""},
                    new String[]{"","CHEZ MOQUET","","RUE DE THIONVILLE","","75019 PARIS","75"}, false, db1);
            
            testRestructure(gm,new String[]{"","APPARTEMENT 70","","24 BD HOPITAL PARIS","",""},
                    new String[]{"","APPARTEMENT 70","","24 BD HOPITAL","","PARIS","75"}, false, db1);
            
            testRestructure(gm,new String[]{"","RN 70","","24 BD HOPITAL PARIS","",""},
                    new String[]{"","RN 70","","24 BD HOPITAL","","PARIS","75"}, false, db1);
            testRestructure(gm,new String[]{"","","","RUE TRUCMCUHE","","PARIS"},
                    new String[]{"","","","RUE TRUCMCUHE","","PARIS","75"}, false, db1);
            testRestructure(gm,new String[]{"","","","24 BIDON SAINT JACQUES","","75013 PARIS 13 EME"},
                    new String[]{"","","","24 BIDON SAINT JACQUES","","75013 PARIS 13 EME","75"}, false, db1);
            testRestructure(gm,new String[]{"","","","AVENUE PORTE DE GENTILLY","","7 5 PARIS"},
                    new String[]{"","","","AVENUE PORTE DE GENTILLY","","PARIS 7 5","75"}, false, db1);
            
            // Les deux tests qui suivent ne sont valables
            // que si le référentiel contient "PORTE DE PANTIN" dans le 75.
            PreparedStatement psConditionTest = db1.prepareStatement("SELECT count(*) FROM voi_voies_75 WHERE voi_nom=?");
            psConditionTest.setString(1,"PORTE DE PANTIN");
            ResultSet rsConditionTest = psConditionTest.executeQuery();
            rsConditionTest.next();
            int count = rsConditionTest.getInt(1);
            if (count>0)
            {
                testRestructure(gm,new String[]{"","","","PORTE PANTIN","","PARIS"},
                    new String[]{"","","","PORTE PANTIN","","PARIS","75"}, false, db1); // Attention, non résolu si le référentiel ne contient pas la porte de pantin
                testRestructure(gm,new String[]{"","","BAL 18","PORTE PANTIN","","PARIS"},
                    new String[]{"","BAL 18","","PORTE PANTIN","","PARIS","75"}, false, db1); // Attention, non résolu si le référentiel ne contient pas la porte de pantin
            }
            
            testRestructure(gm,new String[]{"","","","75 PARIS BOULEVARD HOPITAL","",""},
                    new String[]{"","","","BOULEVARD HOPITAL","","75 PARIS","75"}, false, db1);
            testRestructure(gm,new String[]{"","","","BOULEVARD PALAIS PARIS","",""},
                    new String[]{"","","","BOULEVARD PALAIS","","PARIS","75"}, false, db1); // pb de restructuration
            // Modif tests unitaires WA 31-05-2011 : discrimination impossible entre PORTE 14 en l.2 ou l.3
//            testRestructure(gm,new String[]{"","","6 EME ETG PORTE 14 8 BD DE LA BASTILLE","","","PARIS"},
//                    new String[]{"","ETG 6 PORTE 14","","8 BD DE LA BASTILLE","","PARIS","75"},db1);
            testRestructure(gm,new String[]{"","","6 EME ETG PORTE 14 8 BD DE LA BASTILLE","","","PARIS"},
                    new String[]{"","ETG 6","PORTE 14","8 BD DE LA BASTILLE","","PARIS","75"}, false, db1);
            
            testRestructure(gm,new String[]{"","","78 AVENUE FOCH","","","PARIS"},
                    new String[]{"","","","78 AVENUE FOCH","","PARIS","75"}, false, db1); // une voie contient AVENUE FOCH => AVENUE n'était pas considéré comme un type de voie mais comme un libellé !
            
            // Test pertinent si
            // ALLEE est une clé (et pas seulement un type de voie)
            // qui a pour abbréviation ALL.
            // et utilisée dans la ligne 3 NP
            Abbreviation motAll = gm.chercheAbbreviation("ALL");
            if (motAll!=null)
            {
                Mot motAllee = motAll.obtientMotPrefere(CategorieMot.Cle);
                if (motAllee!=null && motAllee.est("ALLEE") && motAllee.obtientLigneDeCle()==3)
                {
                    testRestructure(gm,new String[]{"","ALL 5 2 RUE FREERIC SCHEINDER","","","","PARIS"},
                        new String[]{"","","ALL 5","2 RUE FREERIC SCHEINDER","","PARIS","75"}, false, db1);
                }
                else motAll=null; // trick
            }
            // Sinon, le mécanisme par défaut est utilisé (ALL conservé dans la même ligne)
            if (motAll==null)
            {
                testRestructure(gm,new String[]{"","","ALL 5 2 RUE FREERIC SCHEINDER","","","PARIS"},
                        new String[]{"","","ALL 5","2 RUE FREERIC SCHEINDER","","PARIS","75"}, false, db1);
            }
            
          //  testRestructure(gm,new String[]{"","","36 BIS BALARD","","","PARIS"},
          //          new String[]{"","","","36 BIS BALARD","","PARIS","75"}, false, db1); // absence de type de voie réglé
            
            // Test pertinent si la clé HALL est référencée, pour la ligne 3.
            // A noter que ce test est différent du test avec ALL :
            // 1. HALL n'est pas une abbréviation
            // 2. TERRASE n'est pas un type de voie (c'est TERRASSE)
            Mot motHall = gm.chercheMot("HALL");
            if (motHall!=null && motHall.obtientLigneDeCle()==3)
            {
                testRestructure(gm,new String[]{"","","1 TERRASE DU PARC HALL KEPLER","","","PARIS"},
                        new String[]{"","","HALL KEPLER","1 TERRASE DU PARC","","PARIS","75"}, false, db1); // présence d'un article devant le type de voie
            }
            // Sinon, tout est reporté en ligne 4.
            else
            {
                testRestructure(gm,new String[]{"","","1 TERRASE DU PARC HALL KEPLER","","","PARIS"},
                        new String[]{"","","","1 TERRASE DU PARC HALL KEPLER","","PARIS","75"}, false, db1); // présence d'un article devant le type de voie
            }

            // Adaptation referentiel BSPP mai 2011
            //testRestructure(gm,new String[]{"","","107 109 RUE DES PYRENNEES PTE 52","","","PARIS"},
            //        new String[]{"","PTE 52","","107 109 RUE DES PYRENNEES","","PARIS","75"},db1);
            //testRestructure(gm,new String[]{"","","7 TERRASSE DU PARC ETG 4 PT 41","","","PARIS"},
            //        new String[]{"","ETG 4 PT 41","","7 TERRASSE DU PARC","","PARIS","75"},db1);
            testRestructure(gm,new String[]{"","","107 109 RUE DES PYRENNEES PTE 52","","","PARIS"},
                    new String[]{"","","PTE 52","107 109 RUE DES PYRENNEES","","PARIS","75"}, false, db1);
//            testRestructure(gm,new String[]{"","","7 TERRASSE DU PARC ETG 4 PT 41","","","PARIS"},
//                    new String[]{"","ETG 4","PT 41","7 TERRASSE DU PARC","","PARIS","75"},db1);
            
            testRestructure(gm,new String[]{"","","7 ET 4 ET 5 BD HOPITAL","","","PARIS"},
                    new String[]{"","","","7 ET 4 ET 5 BD HOPITAL","","PARIS","75"}, false, db1); // gestion des articles
            
            testRestructure(gm,new String[]{"","","14 DU CAPORAL PEUGEOT","","","PARIS"},
                    new String[]{"","","","14 DU CAPORAL PEUGEOT","","PARIS","75"}, false, db1); // un code département ne peut être suivi d'un article
            
//            testRestructure(gm,new String[]{"","","18 BD HENRI IV QUARTIER DES CELESTINS","","","PARIS"},
//                    new String[]{"","","QUARTIER DES CELESTINS","18 BD HENRI IV","","PARIS","75"},db1); // gestion des multiples types de voies
            
            testRestructure(gm,new String[]{"","","33 34 ET 35 BIS RUE DAVID D ANGERS","","","PARIS"},
                    new String[]{"","","","33 34 ET 35 BIS RUE DAVID D ANGERS","","PARIS","75"}, false, db1); // un code de département ne peut être suivi de suite de numéro suivi par article, répétition, ou type de voie
            
            testRestructure(gm,new String[]{"","","8 RUE ALPHONSE CARR","","","PARIS"},
                    new String[]{"","","","8 RUE ALPHONSE CARR","","PARIS","75"}, false, db1); // de préférence, le type de voie choisi ne termine pas une ligne
            
            testRestructure(gm,new String[]{"","","LES JARDINS DE LA MOUSAYA","","","PARIS"},
                    new String[]{"","","LES JARDINS DE LA MOUSAYA","","","PARIS","75"}, false, db1); // les libellés sans type de voie doivent disposer d'au moins un numéro
            
            // Le test suivant nécessite que le terme HALL soit répertorié comme type de voie
            Mot mot = gm.chercheMot("HALL");
            if (mot!=null && mot.estDeLaCategorie(CategorieMot.TypeDeVoie))
            {
                testRestructure(gm,new String[]{"","","TERRASSE DU PARC CHEZ MR BOUTIN 9 HALL COPERINC","","","PARIS"},
                      new String[]{"","CHEZ MR BOUTIN","TERRASSE DU PARC","9 HALL COPERINC","","PARIS","75"}, false, db1); // le hall copernic n'est pas référencé dans le référentiel
            }
    }
    
    public void testRestructurePC(GestionMots gm, Connection db1) throws Exception
    {
        testRestructure(gm,new String[]{"","","","ESCALIER B APPT 70 3 AVENUE PAUL ELUARD 93000 BOBIGNY","",""},
                    new String[]{"","ESCALIER B APPT 70","","3 AVENUE PAUL ELUARD","","93000 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","APPT 70","","ESCALIER B 3 AVENUE PAUL ELUARD 93000 BOBIGNY","",""},
                    new String[]{"","APPT 70 ESCALIER B","","3 AVENUE PAUL ELUARD","","93000 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","","","ESCALIER B 1 ER APPT BATIMENT C 24 B 26 AVENUE PAUL ELUARD 93 BOBIGNY","",""},
                    new String[]{"","ESCALIER B APPT 1","BATIMENT C","24 B 26 AVENUE PAUL ELUARD","","93 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","24 BIS RUE DE PARIS","BOBIGNY 93000","","",""},
                    new String[]{"","","","24 BIS RUE DE PARIS","","93000 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","","","92013 CHALON SUR SAONE 13 RUE DE PARIS","",""},
                    new String[]{"","","","13 RUE DE PARIS","","92013 CHALON SUR SAONE","92"}, false, db1);
            testRestructure(gm,new String[]{"","","","CENTRE COMMERCIAL CARREFOUR RUE DE THIONVILLE 93019 BOBIGNY PANTIN","",""},
                    new String[]{"CENTRE COMMERCIAL CARREFOUR","","","RUE DE THIONVILLE","PANTIN","93019 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","","","","ROSNY SOUS BOIS BOBIGNY","93000"},
                    new String[]{"","","","","ROSNY SOUS BOIS","93000 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","","","","BOBIGNY ROSNY SOUS BOIS","93000"},
                    new String[]{"","","","","BOBIGNY","93000 ROSNY SOUS BOIS","93"}, false, db1);
            testRestructure(gm,new String[]{"","24 BIS RUE DE PARIS","BOBIGNY 93000","","",""},
                    new String[]{"","","","24 BIS RUE DE PARIS","","93000 BOBIGNY","93"}, false, db1);
            testRestructure(gm,new String[]{"","","","AVENUE PAUL ELUARD BOBIGNY","",""},
                    new String[]{"","","","AVENUE PAUL ELUARD","","BOBIGNY","93"}, false, db1);
//            testRestructure(gm,new String[]{"","","","","","9 O SAINT MAUR IES FOSSES"},
//                    new String[]{"","","","","","SAINT MAUR IES FOSSES 9 O","94"},db1); // les communes doivent faire parti des ambiguites des autres départements
            testRestructure(gm,new String[]{"","","","94400 VITRY SUR SEINE 4 PLACE POULAGA","",""},
                    new String[]{"","","","4 PLACE POULAGA","","94400 VITRY SUR SEINE","94"}, false, db1); // pb de restructuration avec ambiguité
    }
    
    public void testRestructureBanlieue(GestionMots gm, Connection db1) throws Exception
    {
        testRestructure(gm,new String[]{"","","","59500 DOUAI 30 RUE REMY DUHEM","",""},
                    new String[]{"","","","30 RUE REMY DUHEM","","59500 DOUAI","59"}, false, db1);
            testRestructure(gm,new String[]{"","24 26 T ROND POINT DU ONZE NOVEMBRE 1918","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","","",""},
                    new String[]{"","","","24 26 T ROND POINT DU ONZE NOVEMBRE 1918","","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","76"}, false, db1);
            testRestructure(gm,new String[]{"","","","76039 LES AUTHIEUX SUR LE PORT ST OUEN","",""},
                    new String[]{"","","","","","76039 LES AUTHIEUX SUR LE PORT ST OUEN","76"}, false, db1);
            testRestructure(gm,new String[]{"","LES AUTHIEUX SUR LE PART SAINT OUEN","","76039","",""},
                    new String[]{"","","","","","76039 LES AUTHIEUX SUR LE PART SAINT OUEN","76"}, false, db1);
            testRestructure(gm,new String[]{"","","","","","VILLERS AUX BOIS"},
                    new String[]{"","","","","","VILLERS AUX BOIS","51"}, false, db1);
            testRestructure(gm,new String[]{"","","","","","VILLIERS AUX BOIS"},
                    new String[]{"","","","","","VILLIERS AUX BOIS","51"}, false, db1); // Pb résolu : ne trouvait pas le département de VILLERS AUX BOIS
            testRestructure(gm,new String[]{"","","","RUE DE PARIS 59 ","DOUAI",""},
                    new String[]{"","","","RUE DE PARIS","","59 DOUAI","59"}, false, db1);
            testRestructure(gm,new String[]{"","","","RUE DE LILLE 59","",""},
                    new String[]{"","","","RUE DE LILLE","","59","59"}, false, db1); // attention aux types de voies! (Nouvelle rue)
            testRestructure(gm,new String[]{"","","RUE DU GRAND ESCALIER","","","83"},
                    new String[]{"","","","RUE DU GRAND ESCALIER","","83","83"}, false, db1); // pb de restructuration avec ambiguité
            testRestructure(gm,new String[]{"","","IMPASSE DU PETIT PARIS","","","02"},
                    new String[]{"","","","IMPASSE DU PETIT PARIS","","02","02"}, false, db1); // pb de restructuration avec ambiguité
    }
    
    public void testRestructureDivers(GestionMots gm, Connection db1) throws Exception
    {
        testRestructure(gm,new String[]{"","","","24 BD HOPITAL 75","","",""},
                new String[]{"","","","24 BD HOPITAL","","75","75"}, false, db1);
        
        //testRestructure(gm,new String[]{"","","","24 HOPITAL PARIS","","",""},
        //        new String[]{"","","","24 HOPITAL","","PARIS","75"}, false, db1,"75");
        
        
        testRestructure(gm,new String[]{"","","1 ER CHEMIN","","",""},
                    new String[]{"","","1 ER","","","CHEMIN","39"}, false, db1); // pb: commune correspondant à CHEMIN a priorité sur le type de voie 1 er chemin
            testRestructure(gm,new String[]{"","","","AUTOROUTE A 11","",""},
                    new String[]{"","","","AUTOROUTE A 11","",""}, false, db1);
            testRestructure(gm,new String[]{"","","","CHARLES DE GAULLE","","SAINT"},
                    new String[]{"","","","CHARLES DE GAULLE","","SAINT"}, false, db1);
            
        testRestructure(gm,new String[]{"","","","RUE DU PETIT PARIS","","",""},
                new String[]{"","","","RUE DU PETIT","","PARIS","75"}, false, db1,"");
        
        testRestructure(gm,new String[]{"","","","DOUAI PARIS","","",""},
                new String[]{"","","","DOUAI","","PARIS","75"}, false, db1,"75");
        
        testRestructure(gm,new String[]{"","","","148 RUE DU FAUBOURG SAINT MARTIN PARIS","","",""},
                new String[]{"","","","148 RUE DU FAUBOURG SAINT MARTIN","","PARIS","75"}, false, db1);
        
        testRestructure(gm,new String[]{"","","","148 RUE DU FBG SAINT MARTIN PARIS","","",""},
                new String[]{"","","","148 RUE DU FBG SAINT MARTIN","","PARIS","75"}, false, db1);
    }
    
    public void testRestructureGestionDepartement(GestionMots gm,Connection db1,GestionCodesDepartements gcdpt) throws Exception
    {
        // WA 14/09/2011 Ajout des tests unitaires pour la corse et les dom toms
        testRestructure(gm, new String[]{"", "", "83 RUE CHMOLL", "", "", ""}, new String[]{"", "", "", "83 RUE CHMOLL", "", "", ""},  false, db1);
        testRestructure(gm, new String[]{"", "", "2 A RUE CHMOLL", "", "", ""}, new String[]{"", "", "", "2 A RUE CHMOLL", "", "", ""},  false, db1);
        testRestructure(gm, new String[]{"", "", "20 A RUE CHMOLL", "", "", ""}, new String[]{"", "", "", "20 A RUE CHMOLL", "", "", ""},  false, db1);
        if (gcdpt.isDptCodePresent("20 A"))
        {
            testRestructure(gm, new String[]{"", "", "20 RUE CHMOLL 20 A", "", "", ""}, new String[]{"", "", "", "20 RUE CHMOLL", "", "20 A", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "20 A RUE CHMOLL 20 A", "", "", ""}, new String[]{"", "", "", "20 A RUE CHMOLL", "", "20 A", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE CHMOLL 20 A", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "20 A", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE CHMOLL 2 A", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "2 A", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "2 A", "", "", ""}, new String[]{"", "", "", "", "", "2 A", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "20 A", "", "", ""}, new String[]{"", "", "", "", "", "20 A", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "75 20 A", "", ""}, new String[]{"", "", "75", "", "", "20 A", "20 A"},  false, db1);
            // Je ne sais que penser de celui ci ... apres tout 75 fait une commune tout a fait acceptable ...
            testRestructure(gm, new String[]{"", "", "20 A 75", "", ""}, new String[]{"", "", "", "", "", "20 A 75", "20 A"},  false, db1);

            testRestructure(gm, new String[]{"", "", "CHEMIN DE LA VERDOLINE 20 A DIGNE LES BAINS", "", ""}, new String[]{"", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A DIGNE LES BAINS", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "CHEMIN DE LA VERDOLINE 2 A DIGNE LES BAINS", "", ""}, new String[]{"", "", "", "CHEMIN DE LA VERDOLINE", "", "2 A DIGNE LES BAINS", "20 A"},  false, db1);
        }
        if (gcdpt.isDptCodePresent("20 B"))
        {
            testRestructure(gm, new String[]{"", "", "20 B RUE CHMOLL RISTOLIS", "", "", ""}, new String[]{"", "", "", "20 B RUE CHMOLL", "", "RISTOLIS", "20 B"},  false, db1);
            testRestructure(gm, new String[]{"", "", "20 B RUE CHMOLL 20 B RISTOLIS", "", "", ""}, new String[]{"", "", "", "20 B RUE CHMOLL", "", "20 B RISTOLIS", "20 B"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE TURLUTUTU 20 B BRUNET", "", ""}, new String[]{"", "", "", "RUE TURLUTUTU", "", "20 B BRUNET", "20 B"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE TURLUTUTU 20 B", "", ""}, new String[]{"", "", "", "RUE TURLUTUTU", "", "20 B", "20 B"},  false, db1);
            testRestructure(gm, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75 20 B", "", "", ""}, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75", "", "", "20 B", "20 B"},  false, db1);
        }
        if (gcdpt.isDptCodePresent("83"))
        {
            testRestructure(gm, new String[]{"", "", "RUE CHMOLL 83", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "83", "83"},  false, db1);
        }
        testRestructure(gm, new String[]{"", "", "APPT 83", "", "", ""}, new String[]{"", "APPT 83", "", "", "", "", ""},  false, db1);
        testRestructure(gm, new String[]{"", "", "APPT 2 A", "", "", ""}, new String[]{"", "APPT 2 A", "", "", "", "", ""},  false, db1);
        testRestructure(gm, new String[]{"", "", "APPT 20 A", "", "", ""}, new String[]{"", "APPT 20 A", "", "", "", "", ""},  false, db1);
        testRestructure(gm, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75 75", "", "", ""}, new String[]{"", "", "BORNE KILOMETRIQUE 20 A 75", "", "", "75", "75"},  false, db1);
        if (gcdpt.isDptCodePresent("04")&& gcdpt.isDptCodePresent("20 A"))
        {
            // Pour que ces tests ait un sens, il faut que D 20 A soit une voie avec ambiguite de type 'codeDepartementDansVoie'
            // -> a remplacer par un cas reel une fois un referentiel mis a jour pour la corse.
            testRestructure(gm, new String[]{"", "", "", "RUE D 20 A BRUNET", "", ""}, new String[]{"", "", "", "RUE D 20 A", "", "BRUNET", "04,20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE CHMOLL 20 A BRUNET", "", "", ""}, new String[]{"", "", "", "RUE CHMOLL", "", "20 A BRUNET", "20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "", "D 20 A BRUNET", "", ""}, new String[]{"", "", "", "D 20 A", "", "BRUNET", "04,20 A"},  false, db1);
            testRestructure(gm, new String[]{"", "", "RUE D 20 B BRUNET", "", ""}, new String[]{"", "", "", "RUE D 20 B", "", "BRUNET", "04,20 A"},  false, db1);
        }
        testRestructure(gm, new String[]{"", "", "RUE D 20 A", "", ""}, new String[]{"", "", "", "RUE D 20 A", "", "", ""},  false, db1);
        testRestructure(gm, new String[]{"", "", "2 A", "", "75 PARIS", ""}, new String[]{"", "", "2 A", "", "", "75 PARIS", "75"},  false, db1);
        testRestructure(gm, new String[]{"", "", "83 75", "", ""}, new String[]{"", "", "83", "", "", "75", "75"},  false, db1);
    }
    
    public void testRestructurePays(GestionMots gm,Connection db1,GestionCodesDepartements gcdpt) throws Exception
    {
        // WA 01/2012 Pays
        testRestructure(gm,new String[]{"MS S POLLARD","1 CHAPEL STREET","HESWALL","BOURNEMOUTH","","BH1 1AA","ROYAUME UNI"},
                new String[]{"MS S POLLARD","1 CHAPEL STREET","HESWALL","BOURNEMOUTH","","BH1 1AA","ROYAUME UNI"}, true, db1);
        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS FRANCE","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS FRANCE","75"}, false, db1);
        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS FRANCE","","", ""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS", "FRANCE", "75"}, true, db1);
        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS BANGLADESH","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS BANGLADESH","75"}, false, db1);
        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS BANGLADESH","","", ""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS BANGLADESH","FRANCE", "75"}, true, db1);
        testRestructure(gm,new String[]{"BANGLADESH","","","","","",""},
                new String[]{"","","","","","","BANGLADESH", ""}, true, db1);
        testRestructure(gm,new String[]{"BENGLADESH","","","","","",""},
                new String[]{"","","","","","","BENGLADESH", ""}, true, db1);
        testRestructure(gm,new String[]{"BURKINA FASO","","","","","",""},
                new String[]{"","","","","","","BURKINA FASO", ""}, true, db1);
        testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL BURKINA FASO","","","","","",""},
                new String[]{"24 26 BD DE L HOPITAL","","","","","","BURKINA FASO", ""}, true, db1);
        testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL BURKINA FAZO","","","","","",""},
                new String[]{"24 26 BD DE L HOPITAL","","","","","","BURKINA FAZO", ""}, true, db1);
        testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL ETAT PLURINATIONAL DE BOLIVIE","","","","","",""},
                new String[]{"24 26 BD DE L HOPITAL","","","","","","ETAT PLURINATIONAL DE BOLIVIE", ""}, true, db1);
        testRestructure(gm,new String[]{"", "24 26 BD DE L HOPITAL ETAT PLURINATIONAL DE BOLIVIE","","","","",""},
                new String[]{"","24 26 BD DE L HOPITAL","","","","","ETAT PLURINATIONAL DE BOLIVIE", ""}, true, db1);

        testRestructure(gm,new String[]{"FRANCE", "APPARTEMENT 70 ENTREE B","","24 26 TER RUE DES 4 CHENES","","76039 LES AUTHIEUX SUR LE PART SAINT OUEN",""},
                new String[]{"","APPARTEMENT 70","ENTREE B","24 26 TER RUE DES 4 CHENES","","76039 LES AUTHIEUX SUR LE PART SAINT OUEN","FRANCE", "76"}, true, db1);
        
        if (gcdpt.isDptCodePresent("67"))
        {
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARTIN 67","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL", "", "67 SAINT MARTIN", "67"}, false, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARIN 67","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL", "", "67 SAINT MARIN", "67"}, false, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARIN","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL", "", "SAINT MARIN", "FRANCE", "47"}, true, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARIN 67","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL", "", "67 SAINT MARIN", "FRANCE","67"}, true, db1);
        }
        if (gcdpt.isDptCodePresent("66"))
        {
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 66 SAINT MARTIN","","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL", "","66 SAINT MARTIN","FRANCE", "66"}, true, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MARTIN 66","","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL", "","66 SAINT MARTIN","FRANCE", "66"}, true, db1);
            testRestructure(gm,new String[]{"", "", "24 26 BD DE L HOPITAL SAINT MARIN 66","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","66 SAINT MARIN", "FRANCE","66"}, true, db1);
        }
        if (gcdpt.isDptCodePresent("63"))
        {
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MAURICE 63","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "63"}, false, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL SAINT MAURICE 63","","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "FRANCE", "63"}, true, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE","","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "FRANCE", "63"}, true, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE MAURICE","","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE MAURICE", "FRANCE", "63"}, true, db1);
            testRestructure(gm,new String[]{"24 26 BD DE L HOPITAL 63 SAINT MAURICE FRANCE","","","","","",""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","63 SAINT MAURICE", "FRANCE", "63"}, true, db1);
        }
        if (gcdpt.isDptCodePresent("08"))
        {
            testRestructure(gm,new String[]{"MENIL ANNELLES","","","","","",""},
                new String[]{"","","","","","MENIL ANNELLES", "FRANCE", "08"}, true, db1);
        }
        if (gcdpt.isDptCodePresent("61"))
        {
            testRestructure(gm,new String[]{"LE MENIL SCELLEUR","","","","","",""},
                new String[]{"","","","","","LE MENIL SCELLEUR", "61"}, false, db1);
        
        }
        if (gcdpt.isDptCodePresent("84"))
        {
        testRestructure(gm,new String[]{"LA ROQUE SUR PERNES","","","","","",""},
                new String[]{"","","","","","LA ROQUE SUR PERNES", "84"}, false, db1);
        
        }

        testRestructure(gm,new String[]{"FRANCE","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","","ENTREE A APPT 70","PARIS","75013",""},
                new String[]{"","APPT 70","ENTREE A","24 BIS BOULEVARD DU MARECHAL DE LATTRE DE TASSIGNY","","75013 PARIS", "FRANCE", "75"}, true, db1);
        testRestructure(gm,new String[]{"LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
                new String[]{"","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
        testRestructure(gm,new String[]{"CONGO","","","","","",""},
                new String[]{"","","","","","", "CONGO", ""}, true, db1);
        testRestructure(gm,new String[]{"LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
                new String[]{"","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
        testRestructure(gm,new String[]{"BULGARIE LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
                new String[]{"BULGARIE","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
        testRestructure(gm,new String[]{"", "BULGARIE LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","",""},
                new String[]{"","BULGARIE","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
        testRestructure(gm,new String[]{"CONGO LA REPUBLIQUE DEMOCRATIQUE DU CONGO","","","","","",""},
                new String[]{"CONGO","","","","","", "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", ""}, true, db1);
        
        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75013 PARIS","","","","",""},
                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75013 PARIS", "75"}, false, db1);
        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75013 PARIS","","","","","", ""},
                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75013 PARIS", "FRANCE", "75"}, true, db1);
        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75013 PARIS FRANCE","","","","","", ""},
                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75013 PARIS", "FRANCE", "75"}, true, db1);
        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE LIBRE 75 PARIS FRANCE","","","","","", ""},
                new String[]{"","","","RUE DES CADETS DE LA FRANCE LIBRE","","75 PARIS", "FRANCE", "75"}, true, db1);
        testRestructure(gm,new String[]{"RUE DES CADETS DE LA FRANCE FRANCE","","","","","", ""},
                new String[]{"","","","RUE DES CADETS DE LA FRANCE","","FRANCE", "FRANCE"}, true, db1);
        testRestructure(gm,new String[]{"","24 BIS RUE DE PARIS","BOBIGNY 93000 FRANCE","","","", ""},
                new String[]{"","","","24 BIS RUE DE PARIS","","93000 BOBIGNY", "FRANCE", "93"}, true, db1);

        // Pays par defaut
        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","","", ""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS", "75"}, false, db1);
        testRestructure(gm,new String[]{"","","","24 26 BD DE L HOPITAL 75013 PARIS","","", ""},
                new String[]{"","","","24 26 BD DE L HOPITAL","","75013 PARIS", "FRANCE", "75"}, true, db1);
    }
    
    @Test
    public void testRestructure() throws Exception
    {
        // Crée les paramètres et la connexion à la base
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc=new GestionConnection(params);
        gc.load("connections.xml");
        Connection db1 = gc.obtientConnection().connection;
        Base.loadParameters(db1,params);
        
        // Crée les classes de gestion
        GestionMots gm = new GestionMots();
        gm.definitJDONREFParams(params);
        GestionMiseAJour gmaj = new GestionMiseAJour(gm,params);
        GestionReferentiel gr = new GestionReferentiel(gm,gmaj,params);
        gm.definitGestionReferentiel(gr);
        GestionCodesDepartements gcdpt = GestionCodesDepartements.getInstance();
        
        gm.loadAbbreviation("abbreviations.xml");
        gm.loadCles("cles.xml");
        gm.loadPrenoms("prenoms.txt");
        if(params.isUtilisationDeLaGestionDesDepartements())
            gcdpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
        
        // Permet de choisir la localisation des tests a effectuer
        // Dépend de l'échantillon considéré pour les tests.
        boolean paris = true;
        boolean petite_couronne = true;
        boolean banlieue = true;
        boolean divers = true;
        boolean pays = true;
        
        if (paris || petite_couronne) testRestructureParisPC(gm,db1);
        if (petite_couronne) testRestructurePC(gm,db1);
        if (banlieue) testRestructureBanlieue(gm,db1);
        if (divers) testRestructureDivers(gm,db1);
        //if (pays) testRestructurePays(gm,db1,gcdpt);
        if(params.isUtilisationDeLaGestionDesDepartements()) testRestructureGestionDepartement(gm,db1,gcdpt);
        
        db1.close();
    }

}