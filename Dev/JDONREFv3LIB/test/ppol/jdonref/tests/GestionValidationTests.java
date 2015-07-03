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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.jdom.JDOMException;
import org.junit.Test;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.Processus;
import ppol.jdonref.Base;
import ppol.jdonref.GestionLogs;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.mots.GestionMotsException;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;

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
public class GestionValidationTests
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

    void testValideVoie(GestionValidation validation, String voie, String code_postal, java.lang.String pays, Date date, boolean force,
            String resultat_attendu, Connection db1) throws ClassNotFoundException, SQLException, Exception
    {
        String[] res = validation.valideVoieCodePostal(2, new String[]
                {
                    "", "", "", voie, "", code_postal
                }, date, force, false, false, null, db1);

        if(res.length > 1)
        {
            if(res.length == 2)
            {
                throw (new Exception("Aucun résultat"));
            } else
            {
                int nb = Integer.parseInt(res[1]);
                for(int i = 0; i < nb; i++)
                {
                    // 6 entete + modulo 10 + ligne 4 en 2ème
                    if(res[6 + 10 * i + 1].compareTo(resultat_attendu) == 0)
                    {
                        return;
                    }
                }
            }
            throw (new Exception("Cherche la voie " + voie + " dans " + code_postal + " : résultat attendu non trouvé"));
        } else
        {
            throw (new Exception("Aucune résultat."));
        }
    }

    void testValideVoieCommune(GestionValidation validation, String ligne4, String ligne6, Date date, boolean force, String resultat_attendu,
            Connection db1) throws ClassNotFoundException, SQLException, Exception
    {
        String[] res = validation.valideVoieCodePostalCommune(2, new String[]
                {
                    "", "", "", ligne4, "", ligne6
                }, null, null, date, force, false, false,null, db1);

        afficheResultats(res, ligne4 + " " + ligne6, resultat_attendu, "", false);
    }

    void testValide(GestionReferentiel referentiel, String ligne4, String ligne6, String resultat_attendu, Connection connection,
            boolean gererPays) throws SQLException, Exception
    {
        String[] res = referentiel.valide(2, new String[]
                {
                    "", "", "", ligne4, "", ligne6
                }, null, false, false, gererPays, connection);

        afficheResultats(res, ligne4 + " " + ligne6, resultat_attendu, "",gererPays);
    }

    @Test
    public void testsValideVoie() throws ClassNotFoundException, SQLException, Exception
    {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc1 = new GestionConnection(params);
        gc1.load("connections.xml");
        Connection db1 = gc1.obtientConnection().connection;

        GestionDescriptionTables.definitJDONREFParams(params);
        Base.loadParameters(db1, params);
        GestionIdentifiants.setParams(params);
        GestionMots gm = new GestionMots();
        gm.loadAbbreviation("abbreviations.xml");
        gm.loadCles("cles.xml");
        gm.loadPrenoms("prenoms.txt");
        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
        GestionLogs glogs = new GestionLogs();
        glogs.definitRepertoire(".");
        GestionValidation validation = new GestionValidation();
        validation.setGestionMots(gm);
        validation.setJdonrefParams(params);

        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
        if(params.isUtilisationDeLaGestionDesDepartements())
        {
            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
        }
        
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();

        // codepostalvoiecomplete)
        testValideVoie(validation, "BD DE L HOPITAL", "75013", null, date, false, "BOULEVARD DE L HOPITAL", db1);
        testValideVoie(validation, "PL TASSIGNY", "75116", null, date, false, "PL DU MAL DE LATTRE DE TASSIGNY", db1);
        //testValideVoie(validation, "PL TASSIGNY", "75016", null, date, false, "P DU MAL DE LATTRE DE TASSIGNY", db1);
        testValideVoie(validation, "GAULLE", "75008", null, date, false, "PLACE CHARLES DE GAULLE", db1);
        testValideVoie(validation, "AVENUE DU COLONEL HENRI ROL-TAN", "75014", null, date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoie(validation, "AV DU CNL H ROL TAN", "75014", null, date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoie(validation, "AVENUE PAUL VAILLANT COUTURIER", "75", null, date, false, "AVENUE PAUL VAILLANT COUTURIER", db1);


        // departementvoiecomplete)
        testValideVoie(validation, "AV DU MAL DE LATTRE DE TASSIGNY", "75", null, date, false, "PL DU MAL DE LATTRE DE TASSIGNY", db1);

        // motsclescodepostal)
        testValideVoieCommune(validation, "GAULLE", "75016 PARIS", date, false, "PLACE CHARLES DE GAULLE", db1);

        // motsclesdepartement)
        // Adaptation referentiel BSPP mai 2011
        //testValideVoie(validation, "ROL TAN", "751", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        //testValideVoie(validation, "TANGUY", "751", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoie(validation, "ROL TANGU", "751", null, date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        //testValideVoie(validation, "TANGUY", "751", null, date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoie(validation, "VAILLANT COUTUMIER", "75", null, date, false, "AVENUE PAUL VAILLANT COUTURIER", db1);

        // motsclesdepartementcommune
        testValideVoieCommune(validation, "CHARBONNE", "75 PARIS", date, false, "RUE DE CHARONNE", db1);
        testValideVoieCommune(validation, "ROL TAN", "75014 PARIS", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoieCommune(validation, "SQUARE ALICE", "75 PARIS", date, false, "SQUARE ALICE", db1);
        testValideVoieCommune(validation, "ROL MANGI", "75 PARIS", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoieCommune(validation, "PAUL TANGI", "75 PARIS", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoieCommune(validation, "ROL TANVY", "75 PARIS", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoieCommune(validation, "TANVY", "75 PARIS", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValideVoieCommune(validation, "AV DU COLONEL HENRI ROL TANVY", "75 PARIS", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
        {
            testValideVoieCommune(validation, "AV H ROL TANGUY", "75 PARI", date, false, "AV DU COLONEL HENRI ROL TANGUY", db1);
        }
        testValide(referentiel, "HOPITAL", "75 PARIS 5", "BOULEVARD DE L HOPITAL", db1, false);
        //testValide(referentiel, "PARC", "75 PARIS", "VILLA DU PARC", db1, false); // pas d'ambiguité sur parc 75 ...
        testValide(referentiel, "AVENUE", "75 PARIS", "SQUARE DE L AVENUE DU BOIS", db1, false);
        // Adaptation referentiel BSPP mai 2011
        //testValide(referentiel, "ROL TAN", "PARIS", "AV DU COLONEL HENRI ROL TANGUY", db1);
        //testValide(referentiel, "ROL TAN", "75014 PARIS", "AV DU COLONEL HENRI ROL TANGUY", db1);
        //testValide(referentiel, "ROL TAN", "75014", "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValide(referentiel, "ROL TAN", "PARIS", "RUE ALFRED ROLL", db1, false);
        testValide(referentiel, "ROL TAN", "75014 PARIS", "AVENUE PAUL APPELL", db1, false);
        testValide(referentiel, "ROL TAN", "75014", "AVENUE PAUL APPELL", db1, false);

        testValide(referentiel, "HOPITAL SAINT", "75013 PARIS", "RUE DE L HOPITAL SAINT LOUIS", db1, false);
        testValide(referentiel, "OPITL LOUIS", "75 PARIS", "RUE DE L HOPITAL SAINT LOUIS", db1, false);
        // Adaptation referentiel BSPP mai 2011
        //testValide(referentiel, "ROL TAN", "75068 PARIS", "AV DU COLONEL HENRI ROL TANGUY", db1);
        testValide(referentiel, "ROL TAN", "75068 PARIS", "RUE ALFRED ROLL", db1, false);
        testValide(referentiel, "GAULLE", "75008 PARIS", "PLACE CHARLES DE GAULLE", db1, false);
        testValide(referentiel, "MACDONALD", "75015 PARIS", "BOULEVARD MACDONALD", db1, false);
        testValide(referentiel, "HOPITAL", "75013 PARIS", "BOULEVARD DE L HOPITAL", db1, false);

        // motsclescommune)
        //Modif WA 08/2011 : sur toute la france le meilleur resultat est
        //testValide(referentiel, "CHARLES DE GAULLE", "SAINT", "92210 SAINT CLOUD", db1,false); // choix de la commune ?
        //testValide(referentiel, "CHARLES DE GAULLE", "SAINT", "45130 SAINT AY", db1, false);

        // adressecomplete)
        testValide(referentiel, "BOULEVARD HOPITAL", "75013", "BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "BOULEVARD HOPITOL", "PARIS", "BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "BOULEVARD HOPITAL", "PARIS", "BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "RUE DARU", "75013 PARIS", "RUE DARU", db1, false);
        testValide(referentiel, "RUE DE L AVRE", "75015 PARIS", "RUE DE L AVRE", db1, false);
        testValide(referentiel, "2 ROUTE DE L OUEST", "94", "2 ROUTE DE L OUEST", db1, false);
        testValide(referentiel, "1 RUE PIERRE BROSSOLETTE", "92004", "1 RUE PIERRE BROSSOLETTE", db1, false);
        testValide(referentiel, "PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "75116 PARIS", "PL DU MAL DE LATTRE DE TASSIGNY", db1, false);
        testValide(referentiel, "24 B PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "75116 PARIS", "PL DU MAL DE LATTRE DE TASSIGNY", db1, false); // PS: pas de numÃ©ro 24 B sur cette place
        testValide(referentiel, "24 B BD DU MAL DE LATTRE DE TASSIGNY", "75008", "PL DU MAL DE LATTRE DE TASSIGNY", db1, false); // NB: pas de boulevard de ce nom
        testValide(referentiel, "PL DU MARECHAL DE LATTRE DE TASSIGNY", "75016 PARIS", "PL DU MAL DE LATTRE DE TASSIGNY", db1, false);
        // NB: type de voie compte plus que libellé ?
        if(5 * params.obtientNotePourTypeDeVoie() <= params.obtientNotePourLibelle())
        {
            testValide(referentiel, "11 RUE DU CHATELET", "75001 PARIS", "PLACE DU CHATELET", db1, false);
        } else
        {
            testValide(referentiel, "11 RUE DU CHATELET", "75001 PARIS", "11 RUE DE LA CHAPELLE", db1, false);
        }
        testValide(referentiel, "25 RUE CHAZAL", " 75004 PARIS", "25 RUE CHAPTAL", db1, false);
        testValide(referentiel, "24 BD HOPITAL", "75 PARIS", "24 BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "100 BOULEVARD HOPITAL", "75 PARIS", "100 BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "100 BOULEVARD HOPITAL", "PARIS", "100 BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "BOULEVARD HOPITAL", "75 PARIS 5", "BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "BOULEVARD HOPITAL", "PARIS", "BOULEVARD DE L HOPITAL", db1, false);
        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
        {
            testValide(referentiel, "BOULEVARD HOPITAL", "PARI", "BOULEVARD DE L HOPITAL", db1, false);
        }
        testValide(referentiel, "RUE DE PARIS", "BOBIGNY", "RUE DE PARIS", db1, false);
        testValide(referentiel, "RUE DE PARIS", "93 BOBIGNY", "RUE DE PARIS", db1, false);
        testValide(referentiel, "RUE DE PARI", "93 BOBIGNY", "RUE DE PARIS", db1, false);
        // Test pertinent si le département 76 est présent
        //testValide(referentiel,"24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945",db1);
        testValide(referentiel, "VOIE A 11", "75011 PARIS", "VOIE COMMUNALE A 11", db1, false);
        testValide(referentiel, "RUE DE PARIS", "93000 BOBIGNY", "RUE DE PARIS", db1, false);
        testValide(referentiel, "RUE DE PARIS", "BOBIGNY", "RUE DE PARIS", db1, false);
        // Test pertinent si le département 77 est présent.
        //testValide(referentiel,"123 RUE PARC DES RIGOUTS","77190 DAMMARIE","123 RUE PARC DES RIGOUTS",db1);
        testValide(referentiel, "AVENUE PORTE DE GENTILLY", "7 5 PARIS", "", db1, false);
        testValide(referentiel, "24 BOULEVARD DE L HOPITAL", "PARIS 05", "24 BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "6 RQUE BAULANT", "H 75 PARIM", "6 RUE BAULANT", db1, false);
        testValide(referentiel, "50 RUE DU GAL DE GAULLE", "94510 LA QUEUE EN BRIE", "50 RUE DU GENERAL DE GAULLE", db1, false);
        testValide(referentiel, "BOULEVARD HOPITALE", "75 PARIS", "BOULEVARD DE L HOPITAL", db1, false);
        testValide(referentiel, "PASSAGE LEMOINE", "PARIS", "PASSAGE LEMOINE", db1, false);
        testValide(referentiel, "2 ROUTE DE L OUEST", "94380 BONNEUIL SUR MARNE", "2 ROUTE DE L OUEST", db1, false);
        testValide(referentiel, "6 RUE PONT DE LODI", "75 8 PA 2 IS", "6 RUE DU PONT DE LODI", db1, false);
        testValide(referentiel, "59 AVENUE SAXF", "H7APRIS", "", db1, false);
        testValide(referentiel, "59 AVENUE SAXF", "H 7 APRIS", "", db1, false);
        testValide(referentiel, "59 AVENUE SAXF", "H 7 APRIS", "", db1, false);
        testValide(referentiel, "AVENUE PORTE LILAS", "75 4 ARIS", "AVENUE DE LA PORTE DES LILAS", db1, false);

        // WA 09/2011 Ajout de tests en corse et dom tom
        if(params.isUtilisationDeLaGestionDesDepartements())
        {
            if (gestDpt.isDptCodePresent("04"))
            {
                testValide(referentiel, "PLACE DE L HOTEL DE VILLE", "04", "PLACE DE L HOTEL DE VILLE", db1, false);
            }
            if (gestDpt.isDptCodePresent("20 A"))
            {
                testValide(referentiel, "PLACE DE L HOTEL DE VILLE", "20 A", "PLACE DE L HOTEL DE VILLE", db1, false);
                testValide(referentiel, "PLACE DE L HOTEL DE VILLE", "20 B", "PLACE DE LA FONT VIEILLE", db1, false);  // Pas de place de l'hotel de ville ds le 20B
                testValide(referentiel, "CHEMIN DE LA VERDOLINE", "20 A", "CHEMIN DE LA VERDOLINE", db1, false);
                testValide(referentiel, "CHEMIN DE LA VERDOLINE", "20070 DIGNE LES BAINS", "CHEMIN DE LA VERDOLINE", db1, false);
                testValide(referentiel, "CHEMIN DE LA VERDOLINE", "20 A DIGNE LES BAINS", "CHEMIN DE LA VERDOLINE", db1, false);
                testValide(referentiel, "CHEMIN DE LA VERDOLINE", "2 A DIGNE LES BAINS", "CHEMIN DE LA VERDOLINE", db1, false);
            }
            if (gestDpt.isDptCodePresent("90"))
            {
                // En test, les departements 971 et 972 sont remplis a partir du 90
                testValide(referentiel, "RUE EMILE ZOLA", "90000 BELFORT", "RUE EMILE ZOLA", db1, false);
            }
            if (gestDpt.isDptCodePresent("97"))
            {
                testValide(referentiel, "RUE EMILE ZOLA", "97110 BELFORT", "RUE EMILE ZOLA", db1, false);
                testValide(referentiel, "RUE EMILE ZOLA", "97210 BELFORT", "RUE EMILE ZOLA", db1, false);
            }
        }

        db1.close();
    }

    void testValideCommune(GestionReferentiel referentiel, String ligne6, Date date, boolean force, boolean gererPays,
            String resultat_attendu, Connection db1) throws ClassNotFoundException, SQLException, Exception
    {
        String[] res = referentiel.valide(2, new String[]
                {
                    "", "", "", "", "", ligne6
                }, sdformat.format(date), force, false,gererPays, db1);

        afficheResultats(res, ligne6, resultat_attendu, "", gererPays);
    }

    @Test
    public void testsValideCommune() throws ClassNotFoundException, SQLException, Exception
    {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc1 = new GestionConnection(params);
        gc1.load("connections.xml");
        Connection db1 = gc1.obtientConnection().connection;

        GestionDescriptionTables.definitJDONREFParams(params);
        Base.loadParameters(db1, params);
        GestionIdentifiants.setParams(params);
        GestionMots gm = new GestionMots();
        gm.loadAbbreviation("abbreviations.xml");
        gm.loadCles("cles.xml");
        gm.loadPrenoms("prenoms.txt");
        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
        //GestionLogs.getInstance().definitRepertoire(".");
        GestionValidation validation = new GestionValidation();
        validation.setGestionMots(gm);
        validation.setJdonrefParams(params);
        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
        if(params.isUtilisationDeLaGestionDesDepartements())
        {
            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
        }
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();

//        testValideCommune(referentiel,"SAVNTENY",date,false,"94 SANTENY",db1);

        testValideCommune(referentiel, "PARIS", date, false, false, "75001 PARIS", db1);
        // WA 09/2011 : la phonetisation de 'PARIS' est 'PARIS' -> 'PARI' n'est pas trouve
//        testValideCommune(referentiel,"PARI",date,false,"75001 PARIS",db1);
        testValideCommune(referentiel, "PARIQ", date, false, false, "75001 PARIS", db1);
        // WA 09/2011 : pour un referentiel nationnal, on trouve paris plus dificillement
//        testValideCommune(referentiel,"TORIS 5",date,false,"75005 PARIS",db1);
//        testValideCommune(referentiel,"TOTOS",date,true,"75001 PARIS",db1);
        testValideCommune(referentiel, "PORIS 5", date, false, false, "75005 PARIS", db1);

        // WA 09/2011 : 75000 PARIS est ds le refereentiel
//        testValideCommune(referentiel,"75000 PARIS",date,false,"75001 PARIS",db1);
        testValideCommune(referentiel, "75000 PARIS", date, false, false, "75001 PARIS", db1);
        testValideCommune(referentiel, "75005 PARIS 5", date, false, false, "75005 PARIS", db1);
        testValideCommune(referentiel, "PARIS 5", date, false, false, "75005 PARIS", db1);
        testValideCommune(referentiel, "75005 PARIS 5", date, false, false, "75005 PARIS", db1);
        testValideCommune(referentiel, "75013 PARIS 13 EME", date, false, false, "75013 PARIS", db1);
        testValideCommune(referentiel, "ASNIERES SUR SEINE", date, false, false, "92600 ASNIERES SUR SEINE", db1);
        testValideCommune(referentiel, "ASNIERES SUR", date, false, false, "92600 ASNIERES SUR SEINE", db1);
        // WA 09/2011 : Sur la france entiere -> 10110 BAR SUR SEINE
//        testValideCommune(referentiel,"SUR SEINE",date,false,"92600 ASNIERES SUR SEINE",db1);
        if (gestDpt.isDptCodePresent("10"))
            testValideCommune(referentiel, "SUR SEINE", date, false, false, "93800 EPINAY SUR SEINE", db1);
        testValideCommune(referentiel, "94 VITRY SEINE", date, false, false, "94400 VITRY SUR SEINE", db1);
        
        testValideCommune(referentiel, "75013", date, false, false, "75013 PARIS", db1);
        testValideCommune(referentiel, "75013", date, true, false, "75013 PARIS", db1);
        
        // Tests suite à la charge apres les pays
        testValideCommune(referentiel, "93 SONDY", date, false, false, "93140 BONDY", db1);
        
        db1.close();
    }

    void testValidePays(GestionValidation valid, String ligne7, Date date, boolean force, boolean gererPays, String resultat_attendu,
            String etat, String nb, String note, Connection db1) throws SQLException, Exception
    {
        String[] res = valid.validePays(2, new String[]
                {
                    "", "", "", "", "", "", ligne7
                }, date, force, db1);

        boolean err = true;
        if(res[0].equals(etat))
        {
            if(res[1].equals(nb))
            {
                if((nb.equals("0")) || (res[9].equals(resultat_attendu)))
                {
                    if((note.length() == 0) || (res[11].equals(note)))
                    {
                        err = false;
                    }
                }
            }
        }

        if(err)
        {
            throw (new Exception("Err ds valide pays de " + ligne7));
        }

    }

    @Test
    public void testsValidePays() throws JDOMException, IOException, JDONREFException, ClassNotFoundException, SQLException,
            GestionMotsException, Exception
    {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc1 = new GestionConnection(params);
        gc1.load("connections.xml");
        Connection db1 = gc1.obtientConnection().connection;

        GestionDescriptionTables.definitJDONREFParams(params);
        Base.loadParameters(db1, params);
        GestionIdentifiants.setParams(params);
        GestionMots gm = new GestionMots();
        gm.loadAbbreviation("abbreviations.xml");
        gm.loadCles("cles.xml");
        gm.loadPrenoms("prenoms.txt");
        GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);
        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
        //GestionLogs.getInstance().definitRepertoire(".");
        GestionValidation validation = new GestionValidation();
        validation.setGestionMots(gm);
        validation.setJdonrefParams(params);

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();

//        testValideCommune(referentiel,"SAVNTENY",date,false,"94 SANTENY",db1);

        testValidePays(validation, "FRANCE", date, false, true, "FRANCE", "5", "1", "200", db1);
        testValidePays(validation, "FRANSE", date, false, true, "FRANCE", "6", "1", "192", db1);
        testValidePays(validation, "FRENCE", date, false, true, "FRANCE", "6", "2", "", db1);
        testValidePays(validation, "LITUANIE", date, false, true, "LITUANIE", "5", "1", "200", db1);
        testValidePays(validation, "LITHUANIE", date, false, true, "LITUANIE", "6", "1", "", db1);
        testValidePays(validation, "YETI LAND", date, false, true, "", "6", "0", "", db1);
        testValidePays(validation, "BOSNIE HERZEGOVINE", date, false, true, "BOSNIE HERZEGOVINE", "5", "1", "200", db1);
        testValidePays(validation, "BOSNIE ERSEGOVINNE", date, false, true, "BOSNIE HERZEGOVINE", "6", "1", "", db1);
        testValidePays(validation, "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", date, false, true, "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", "5", "1",
                "200", db1);
        testValidePays(validation, "LA REPUBLIQUE DEMOKRATIQUE DU CONGO", date, false, true, "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", "6",
                "15", "", db1);
        testValidePays(validation, "DEMOCRATIQUE DU CONGO", date, false, true, "LA REPUBLIQUE DEMOCRATIQUE DU CONGO", "6", "15", "", db1);
        testValidePays(validation, "CONGO", date, false, true, "CONGO", "5", "1", "", db1);
        testValidePays(validation, "COMGO", date, false, true, "CONGO", "6", "2", "", db1);

        testValidePays(validation, "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", date, false, true,
                "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", "5", "1", "200", db1);
        testValidePays(validation, "REPUBLIQUE DE COREE", date, false, true,
                "REPUBLIQUE DE COREE", "5", "1", "200", db1);
        testValidePays(validation, "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", date, false, true,
                "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", "5", "1", "200", db1);
        testValidePays(validation, "REPUBLIQUE DE COREE", date, false, true,
                "REPUBLIQUE DE COREE", "5", "1", "200", db1);
        testValidePays(validation, "REPUBLIQUE DEMOCRATIQUE DE COREE", date, false, true,
                "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", "6", "15", "", db1);
        testValidePays(validation, "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", date, false, true,
                "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", "5", "1", "200", db1);
        testValidePays(validation, "REPUBLIQUE POP DEMOCRATIQUE DE COREE", date, false, true,
                "REPUBLIQUE POPULAIRE DEMOCRATIQUE DE COREE", "6", "15", "", db1);
        testValidePays(validation, "REPUBLIQUE POP DEM COREE", date, false, true,
                "REPUBLIQUE DE COREE", "6", "15", "", db1);

        db1.close();
    }

     void testValideFullOperation(GestionAdr adr, int[] services, int operation, String[] lines, String resultatAttendu, String str_departements) throws SQLException,
            Exception
    {
        boolean gererPays = (lines.length == 7);
        boolean gererAdresse = true;
        
        List<String[]> res = adr.valide(2, services, operation, lines, null, false,str_departements);

        afficheResultats(res, lines, resultatAttendu, "", gererPays);
    }
    
    void testValideFull(GestionReferentiel referentiel, String[] lines, String resultatAttendu, Connection conn) throws SQLException,
            Exception
    {
        boolean gererPays = (lines.length == 7);
        boolean gererAdresse = true;
        String[] res = referentiel.valide(2, lines, null, false, gererAdresse,gererPays, conn);

        afficheResultats(res, lines, resultatAttendu, "", gererPays);
    }
    
    
    void testValideFullService(GestionReferentiel referentiel,int[] services, String[] lines, String resultatAttendu, Connection conn) throws SQLException,
            Exception
    {
        testValideFullService(referentiel, services, lines, resultatAttendu, "", conn, "");
    }
    
    void testValideFullService(GestionReferentiel referentiel,int[] services, String[] lines, String resultatAttendu1, String resultatAttendu2, Connection conn) throws SQLException,
            Exception
    {
        testValideFullService(referentiel, services, lines, resultatAttendu1, resultatAttendu2, conn, "");
    }
    
    void testValideFullService(GestionReferentiel referentiel,int[] services, String[] lines, String resultatAttendu1, String resultatAttendu2, Connection conn, boolean not) throws SQLException,
            Exception
    {
        testValideFullService(referentiel, services, lines, resultatAttendu1, resultatAttendu2, conn, "", not);
    }
    
    void testValideFullService(GestionReferentiel referentiel,int[] services, String[] lines, String resultatAttendu1, Connection conn,String restriction_departements) throws SQLException,
            Exception
        {
            testValideFullService(referentiel, services, lines, resultatAttendu1, "", conn, restriction_departements, false);
        }
    
        void testValideFullService(GestionReferentiel referentiel,int[] services, String[] lines, String resultatAttendu1, String resultatAttendu2, Connection conn,String restriction_departements) throws SQLException,
            Exception
        {
            testValideFullService(referentiel, services, lines, resultatAttendu1, resultatAttendu2, conn, restriction_departements, false);
        }
    
    void testValideFullService(GestionReferentiel referentiel,int[] services, String[] lines, String resultatAttendu1, String resultatAttendu2, Connection conn,String restriction_departements, boolean not) throws SQLException,
            Exception
    {
        boolean gererPays = (lines.length == 7);
        boolean gererAdresse = true;
        List<String[]> res = referentiel.valide(2, services, lines, null, false, gererAdresse,gererPays, conn,restriction_departements);

        afficheResultats(res, lines, resultatAttendu1, resultatAttendu2, gererPays, not);
    }
    
//    //
//    // Seules les propositions dans le bon département doivent être retournées
//    //
//    public void testBonDepartement(Connection db1,GestionReferentiel referentiel) throws SQLException, Exception
//    {
//        
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "PARIS 13"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "75013"
//                }, "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "75013 PARIS"
//                }, "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "75013"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "24 BD DE L HOPITAL", "", "75013"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "75013"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "24 BD DE L HOPITAL", "", "75013"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "75013 PARIS"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "24 BD DE L HOPITAL", "", "75013 PARIS"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "75013 PARIS"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "24 BD DE L HOPITAL", "", "75013 PARIS"
//                }, "24 BOULEVARD DE L HOPITAL", "75013 PARIS", db1, true);
//    }
//        
//    @Test
//    public void testsValideFullShortList() throws JDOMException, IOException, JDONREFException, ClassNotFoundException, SQLException,
//            GestionMotsException, Exception
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
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
////        GestionLogs.getInstance().definitRepertoire(".");
//        GestionValidation validation = new GestionValidation();
//        validation.setGestionMots(gm);
//        validation.setJdonrefParams(params);
//        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        }
//        
//        JDONREFv3Lib.getInstance("params.xml");
//        
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PARC", "", "75 PARIS"
//                }, "VILLA DU PARC", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PARC", "", "75 PARIS", ""
//                }, "VILLA DU PARC", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PARC", "", "75 PARIS", "FRANCE"
//                }, "VILLA DU PARC", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75013 PARIS"
//                }, "PONT CHARLES DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75013 PARIS", "FRANCE"
//                }, "PONT CHARLES DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75013 PARIS", ""
//                }, "PONT CHARLES DE GAULLE", db1);
//    }
    
     @Test
    public void testsValideFullOperation() throws JDOMException, IOException, JDONREFException, ClassNotFoundException, SQLException,
            GestionMotsException, Exception
    {
        JDONREFParams params = new JDONREFParams();
        params.load("params.xml");
        GestionConnection gc1 = new GestionConnection(params);
        gc1.load("connections.xml");
        Connection db1 = gc1.obtientConnection().connection;

        GestionDescriptionTables.definitJDONREFParams(params);
        Base.loadParameters(db1, params);
        GestionIdentifiants.setParams(params);
        GestionMots gm = new GestionMots();
        gm.loadAbbreviation("abbreviations.xml");
        gm.loadCles("cles.xml");
        gm.loadPrenoms("prenoms.txt");
        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
        gm.definitGestionReferentiel(referentiel);
        gm.definitJDONREFParams(params);
//        GestionLogs.getInstance().definitRepertoire(".");
        GestionValidation validation = new GestionValidation();
        validation.setGestionMots(gm);
        validation.setJdonrefParams(params);
        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
        if(params.isUtilisationDeLaGestionDesDepartements())
        {
            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
        }
        GestionInverse gestionInverse = new GestionInverse(params);
        
        GestionAdr adr = new GestionAdr(gc1, gm, referentiel, gestionInverse);
        adr.definitJDONREFParams(params);
        
        JDONREFv3Lib.getInstance("params.xml");
        
        testValideFullOperation(adr, new int[]{1001}, 7, new String[]
                {
                    "", "", "", "24 BD HOPITAL PARIS", "", ""
                }, "24 BOULEVARD DE L HOPITAL", null);
        
        testValideFullOperation(adr, new int[]{1001}, 39, new String[]
                {
                    "", "", "", "24 BD HOPITAL PARIS", "", ""
                }, "24 BOULEVARD DE L HOPITAL", null);
    }
    
//    @Test
//    public void testsValideFull() throws JDOMException, IOException, JDONREFException, ClassNotFoundException, SQLException,
//            GestionMotsException, Exception
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
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
////        GestionLogs.getInstance().definitRepertoire(".");
//        GestionValidation validation = new GestionValidation();
//        validation.setGestionMots(gm);
//        validation.setJdonrefParams(params);
//        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        }
//        
//        JDONREFv3Lib.getInstance("params.xml");
//        
//        testBonDepartement(db1,referentiel);
//        
//        
//        
//        //regarder fichier \Dev\Src\JDONREFv3LIB\services.xml clé=1001
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        
//        // REPRISE DES ANCIENS TESTS (6 lignes)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PL TASSIGNY", "", "75016"
//                }, "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75016"
//                }, "PLACE CHARLES DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE DU COLONEL HENRI ROL-TAN", "", "75014"
//                },
//                "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU CNL H ROL TAN", "", "75014"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PAUL VAILLANT COUTURIER", "", "75"
//                }, "AVENUE PAUL VAILLANT COUTURIER",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013"
//                }, "BOULEVARD DE L HOPITAL", db1);
//
//        // departementvoiecomplete)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU MAL DE LATTRE DE TASSIGNY", "", "75"
//                }, "PL DU MAL DE LATTRE DE TASSIGNY",
//                db1);
//        // motsclescodepostal)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75016 PARIS"
//                }, "PLACE CHARLES DE GAULLE", db1);
//
//        // motsclesdepartement)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TANGU", "", "751"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "TANGUY", "", "751"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "VAILLANT COUTUMIER", "", "75"
//                }, "AVENUE PAUL VAILLANT COUTURIER", db1);
//
//        // motsclesdepartementcommune
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "CHARBONNE", "", "75 PARIS"
//                }, "RUE DE CHARONNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014 PARIS"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "SQUARE ALICE", "", "75 PARIS"
//                }, "SQUARE ALICE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL MANGI", "", "75 PARIS"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PAUL TANGI", "", "75 PARIS"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TANVY", "", "75 PARIS"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "TANVY", "", "75 PARIS"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU COLONEL HENRI ROL TANVY", "", "75 PARIS"
//                },
//                "AV DU COLONEL HENRI ROL TANGUY", db1);
//        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
//        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "AV H ROL TANGUY", "", "75 PARI"
//                    }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL", "", "75 PARIS 5"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE", "", "75 PARIS"
//                }, "SQUARE DE L AVENUE DU BOIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "PARIS"
//                }, "RUE ALFRED ROLL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014 PARIS"
//                }, "AVENUE PAUL APPELL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014"
//                }, "AVENUE PAUL APPELL", db1);
//
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL SAINT", "", "75013 PARIS"
//                }, "RUE DE L HOPITAL SAINT LOUIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "OPITL LOUIS", "", "75 PARIS"
//                }, "RUE DE L HOPITAL SAINT LOUIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75068 PARIS"
//                }, "RUE ALFRED ROLL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "MACDONALD", "", "75015 PARIS"
//                }, "BOULEVARD MACDONALD", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL", "", "75013 PARIS"
//                }, "BOULEVARD DE L HOPITAL", db1);
//
//        // motsclescommune)
//        if (gestDpt.isDptCodePresent("45"))
//        {
//            testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "CHARLES DE GAULLE", "", "SAINT"
//                }, "45130 SAINT AY", db1);
//        }
//        // adressecomplete)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "75013"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITOL", "", "PARIS"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "PARIS"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DARU", "", "75013 PARIS"
//                }, "RUE DARU", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE L AVRE", "", "75015 PARIS"
//                }, "RUE DE L AVRE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "2 ROUTE DE L OUEST", "", "94"
//                }, "2 ROUTE DE L OUEST", db1);
//
//        testValideFullService(referentiel, new int[]{1001},new String[]
//                {
//                    "", "", "", "", "", "PARIS DOUAI"
//                },
//                "75001 PARIS", db1, "75,77,78,91,92,93,94,95");
//        
//        
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75001 PARIS"
//                },
//                "75001 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75001 PARIS"
//                },
//                "75001 PARIS", db1, "75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75001 PARIS"
//                },
//                "", db1, "77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75001 PAROS"
//                },
//                "75001 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75001 PAROS"
//                },
//                "75001 PARIS", db1, "75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75001 PAROS"
//                },
//                "", db1, "77,78,91,92,93,94,95");
//        
//        
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "PARIS"
//                },
//                "75001 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "PARIS"
//                },
//                "75001 PARIS", db1, "75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "PARIS"
//                },
//                "", db1, "77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "PAROS"
//                },
//                "75001 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "PAROS"
//                },
//                "75001 PARIS", db1, "75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "PAROS"
//                },
//                "", db1, "77,78,91,92,93,94,95");
//        
//        
//        
//        testValideFullService(referentiel, new int[]{1006},new String[]
//                {
//                    "", "", "", "", "", "75012"
//                },
//                "75012 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1006},new String[]
//                {
//                    "", "", "", "", "", "75012"
//                },
//                "75012 PARIS", db1,"75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1006},new String[]
//                {
//                    "", "", "", "", "", "75012"
//                },
//                "", db1,"77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1006},new String[]
//                {
//                    "", "", "", "", "", "75112"
//                },
//                "75012 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1006},new String[]
//                {
//                    "", "", "", "", "", "75112"
//                },
//                "75012 PARIS", db1,"75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1006},new String[]
//                {
//                    "", "", "", "", "", "75112"
//                },
//                "", db1,"77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75012"
//                },
//                "75012 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75012"
//                },
//                "75012 PARIS", db1,"75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75012"
//                },
//                "", db1,"77,78,91,92,93,94,95");
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75112"
//                },
//                "75012 PARIS", db1);
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75112"
//                },
//                "75012 PARIS", db1,"75,77,78,91,92,93,94,95");
//        
//        testValideFullService(referentiel, new int[]{1005},new String[]
//                {
//                    "", "", "", "", "", "75112"
//                },
//                "", db1,"77,78,91,92,93,94,95");
//        
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "24 BOULEVARD HOPITAL", "", "PARIS"
//                },
//                "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BOULEVARD HOPITAL", "", "75116 PARIS"
//                },
//                "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFullService(referentiel, new int[]{1002}, new String[]
//                {
//                    "", "", "", "24 BOULEVARD HOPITAL", "", "75116 PARIS"
//                },
//                "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFullService(referentiel, new int[]{1003}, new String[]
//                {
//                    "", "", "", "24 BOULEVARD HOPITAL", "", "75116 PARIS"
//                },
//                "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFullService(referentiel, new int[]{1004}, new String[]
//                {
//                    "", "", "", "24 BOULEVARD HOPITAL", "", "75116 PARIS"
//                },
//                "24 BOULEVARD DE L HOPITAL", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1003},new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1004},new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        
//        testValideFullService(referentiel, new int[]{1002},new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1003},new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1004},new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92400 COURBEVOIE"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1002}, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1003}, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1004}, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1002}, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1003}, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFullService(referentiel, new int[]{1004}, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BRASSOLETTE", "", "92004"
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        
//        
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "", "75116 PARIS"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 B PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "", "75116 PARIS"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 B BD DU MAL DE LATTRE DE TASSIGNY", "", "75008"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PL DU MARECHAL DE LATTRE DE TASSIGNY", "", "75016 PARIS"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        // NB: type de voie compte plus que libellé ?
//        if(5 * params.obtientNotePourTypeDeVoie() <= params.obtientNotePourLibelle())
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "11 RUE DU CHATELET", "", "75001 PARIS"
//                    }, "PLACE DU CHATELET", db1);
//        } else
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "11 RUE DU CHATELET", "", "75001 PARIS"
//                    }, "11 RUE DE LA CHAPELLE", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "25 RUE CHAZAL", "", " 75004 PARIS"
//                }, "25 RUE CHAPTAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BD HOPITAL", "", "75 PARIS"
//                }, "24 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "100 BOULEVARD HOPITAL", "", "75 PARIS"
//                }, "100 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "100 BOULEVARD HOPITAL", "", "PARIS"
//                }, "100 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "75 PARIS 5"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "PARIS"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
//        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "BOULEVARD HOPITAL", "", "PARI"
//                    }, "BOULEVARD DE L HOPITAL", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "BOBIGNY"
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "93 BOBIGNY"
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARI", "", "93 BOBIGNY"
//                }, "RUE DE PARIS", db1);
//        // Test pertinent si le département 76 est présent
//        //testValide(referentiel,"24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945",db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "VOIE A 11", "", "75011 PARIS"
//                }, "VOIE COMMUNALE A 11", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "93000 BOBIGNY"
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "BOBIGNY"
//                }, "RUE DE PARIS", db1);
//        // Test pertinent si le département 77 est présent.
//        //testValide(referentiel,"123 RUE PARC DES RIGOUTS","77190 DAMMARIE","123 RUE PARC DES RIGOUTS",db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PORTE DE GENTILLY", "", "7 5 PARIS"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "PARIS 05"
//                }, "24 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "6 RQUE BAULANT", "", "H 75 PARIM"
//                }, "6 RUE BAULANT", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "50 RUE DU GAL DE GAULLE", "", "94510 LA QUEUE EN BRIE"
//                },
//                "50 RUE DU GENERAL DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITALE", "", "75 PARIS"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PASSAGE LEMOINE", "", "PARIS"
//                }, "PASSAGE LEMOINE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "2 ROUTE DE L OUEST", "", "94380 BONNEUIL SUR MARNE"
//                }, "2 ROUTE DE L OUEST", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "6 RUE PONT DE LODI", "", "75 8 PA 2 IS"
//                }, "6 RUE DU PONT DE LODI", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H7APRIS"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H 7 APRIS"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H 7 APRIS"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PORTE LILAS", "", "75 4 ARIS"
//                }, "AVENUE DE LA PORTE DES LILAS", db1);
//
//        // WA 09/2011 Ajout de tests en corse et dom tom
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            if (gestDpt.isDptCodePresent("04"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "04"
//                    }, "PLACE DE L HOTEL DE VILLE", db1);
//            }
//            if (gestDpt.isDptCodePresent("20 A"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "20 A"
//                    }, "PLACE DE L HOTEL DE VILLE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A"
//                    }, "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20070 DIGNE LES BAINS"
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A DIGNE LES BAINS"
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "2 A DIGNE LES BAINS"
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//            }
//            if (gestDpt.isDptCodePresent("20 B"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "20 B"
//                    }, "PLACE DE LA FONT VIEILLE", db1);
//            }
//            if (gestDpt.isDptCodePresent("93"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE DE PARI", "", "93 BOBIGNY"
//                    }, "RUE DE PARIS", db1);
//            }
//            if (gestDpt.isDptCodePresent("90"))
//            {
//                // En test, les departements 971 et 972 sont remplis a partir du 90
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "90000 BELFORT"
//                    }, "RUE EMILE ZOLA", db1);
//            }
//            if (gestDpt.isDptCodePresent("97"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "97110 BELFORT"
//                    }, "RUE EMILE ZOLA", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "97210 BELFORT"
//                    }, "RUE EMILE ZOLA", db1);
//            }
//        }
//
//
//        // REPRISE DES ANCIENS TESTS (7 lignes + FRANCE)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PL TASSIGNY", "", "75016", "FRANCE"
//                }, "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75016", "FRANCE"
//                }, "PLACE CHARLES DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE DU COLONEL HENRI ROL-TAN", "", "75014"
//                },
//                "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU CNL H ROL TAN", "", "75014", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PAUL VAILLANT COUTURIER", "", "75", "FRANCE"
//                },
//                "AVENUE PAUL VAILLANT COUTURIER",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//
//        // departementvoiecomplete)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU MAL DE LATTRE DE TASSIGNY", "", "75", "FRANCE"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        // motsclescodepostal)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75016 PARIS", "FRANCE"
//                }, "PLACE CHARLES DE GAULLE", db1);
//
//        // motsclesdepartement)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TANGU", "", "751", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "TANGUY", "", "751", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "VAILLANT COUTUMIER", "", "75", "FRANCE"
//                }, "AVENUE PAUL VAILLANT COUTURIER",
//                db1);
//
//        // motsclesdepartementcommune
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "CHARBONNE", "", "75 PARIS", "FRANCE"
//                }, "RUE DE CHARONNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014 PARIS", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "SQUARE ALICE", "", "75 PARIS", "FRANCE"
//                }, "SQUARE ALICE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL MANGI", "", "75 PARIS", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PAUL TANGI", "", "75 PARIS", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TANVY", "", "75 PARIS", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "TANVY", "", "75 PARIS", "FRANCE"
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU COLONEL HENRI ROL TANVY", "", "75 PARIS"
//                },
//                "AV DU COLONEL HENRI ROL TANGUY", db1);
//        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
//        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "AV H ROL TANGUY", "", "75 PARI", "FRANCE"
//                    },
//                    "AV DU COLONEL HENRI ROL TANGUY", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL", "", "75 PARIS 5", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE", "", "75 PARIS", "FRANCE"
//                }, "SQUARE DE L AVENUE DU BOIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "PARIS", "FRANCE"
//                }, "RUE ALFRED ROLL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014 PARIS", "FRANCE"
//                }, "AVENUE PAUL APPELL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014", "FRANCE"
//                }, "AVENUE PAUL APPELL", db1);
//
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL SAINT", "", "75013 PARIS", "FRANCE"
//                }, "RUE DE L HOPITAL SAINT LOUIS",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "OPITL LOUIS", "", "75 PARIS", "FRANCE"
//                }, "RUE DE L HOPITAL SAINT LOUIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75068 PARIS", "FRANCE"
//                }, "RUE ALFRED ROLL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "MACDONALD", "", "75015 PARIS", "FRANCE"
//                }, "BOULEVARD MACDONALD", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL", "", "75013 PARIS", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//
//        // motsclescommune)
//        if (gestDpt.isDptCodePresent("45"))
//        {
//            testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "CHARLES DE GAULLE", "", "SAINT", "FRANCE"
//                }, "45130 SAINT AY", db1);
//        }
//
//        // adressecomplete)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "75013", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITOL", "", "PARIS", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "PARIS", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DARU", "", "75013 PARIS", "FRANCE"
//                }, "RUE DARU", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE L AVRE", "", "75015 PARIS", "FRANCE"
//                }, "RUE DE L AVRE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "2 ROUTE DE L OUEST", "", "94", "FRANCE"
//                }, "2 ROUTE DE L OUEST", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92004", "FRANCE"
//                }, "1 RUE PIERRE BROSSOLETTE",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "", "75116 PARIS", "FRANCE"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 B PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "", "75116 PARIS", "FRANCE"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 B BD DU MAL DE LATTRE DE TASSIGNY", "", "75008", "FRANCE"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PL DU MARECHAL DE LATTRE DE TASSIGNY", "", "75016 PARIS", "FRANCE"
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        // NB: type de voie compte plus que libellé ?
//        if(5 * params.obtientNotePourTypeDeVoie() <= params.obtientNotePourLibelle())
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "11 RUE DU CHATELET", "", "75001 PARIS", "FRANCE"
//                    }, "PLACE DU CHATELET",
//                    db1);
//        } else
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "11 RUE DU CHATELET", "", "75001 PARIS", "FRANCE"
//                    }, "11 RUE DE LA CHAPELLE",
//                    db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "25 RUE CHAZAL", "", " 75004 PARIS", "FRANCE"
//                }, "25 RUE CHAPTAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BD HOPITAL", "", "75 PARIS", "FRANCE"
//                }, "24 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "100 BOULEVARD HOPITAL", "", "75 PARIS", "FRANCE"
//                },
//                "100 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "100 BOULEVARD HOPITAL", "", "PARIS", "FRANCE"
//                }, "100 BOULEVARD DE L HOPITAL",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "75 PARIS 5", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "PARIS", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
//        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "BOULEVARD HOPITAL", "", "PARI", "FRANCE"
//                    }, "BOULEVARD DE L HOPITAL", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "BOBIGNY", "FRANCE"
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "93 BOBIGNY", "FRANCE"
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARI", "", "93 BOBIGNY", "FRANCE"
//                }, "RUE DE PARIS", db1);
//        // Test pertinent si le département 76 est présent
//        //testValide(referentiel,"24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945",db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "VOIE A 11", "", "75011 PARIS", "FRANCE"
//                }, "VOIE COMMUNALE A 11", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "93000 BOBIGNY", "FRANCE"
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "BOBIGNY", "FRANCE"
//                }, "RUE DE PARIS", db1);
//        // Test pertinent si le département 77 est présent.
//        //testValide(referentiel,"123 RUE PARC DES RIGOUTS","77190 DAMMARIE","123 RUE PARC DES RIGOUTS",db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PORTE DE GENTILLY", "", "7 5 PARIS", "FRANCE"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "PARIS 05", "FRANCE"
//                },
//                "24 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "6 RQUE BAULANT", "", "H 75 PARIM", "FRANCE"
//                }, "6 RUE BAULANT", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "50 RUE DU GAL DE GAULLE", "", "94510 LA QUEUE EN BRIE", "FRANCE"
//                },
//                "50 RUE DU GENERAL DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITALE", "", "75 PARIS", "FRANCE"
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PASSAGE LEMOINE", "", "PARIS", "FRANCE"
//                }, "PASSAGE LEMOINE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "2 ROUTE DE L OUEST", "", "94380 BONNEUIL SUR MARNE", "FRANCE"
//                },
//                "2 ROUTE DE L OUEST", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "6 RUE PONT DE LODI", "", "75 8 PA 2 IS", "FRANCE"
//                }, "6 RUE DU PONT DE LODI",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H7APRIS", "FRANCE"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H 7 APRIS", "FRANCE"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H 7 APRIS", "FRANCE"
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PORTE LILAS", "", "75 4 ARIS", "FRANCE"
//                },
//                "AVENUE DE LA PORTE DES LILAS", db1);
//
//        
//
//        // REPRISE DES ANCIENS TESTS (7 lignes)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PL TASSIGNY", "", "75016", ""
//                }, "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75016", ""
//                }, "PLACE CHARLES DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE DU COLONEL HENRI ROL-TAN", "", "75014"
//                },
//                "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU CNL H ROL TAN", "", "75014", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PAUL VAILLANT COUTURIER", "", "75", ""
//                },
//                "AVENUE PAUL VAILLANT COUTURIER",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BD DE L HOPITAL", "", "75013", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//
//        // departementvoiecomplete)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU MAL DE LATTRE DE TASSIGNY", "", "75", ""
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY",
//                db1);
//        // motsclescodepostal)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "GAULLE", "", "75016 PARIS", ""
//                }, "PLACE CHARLES DE GAULLE", db1);
//
//        // motsclesdepartement)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TANGU", "", "751", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "TANGUY", "", "751", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "VAILLANT COUTUMIER", "", "75", ""
//                }, "AVENUE PAUL VAILLANT COUTURIER", db1);
//
//        // motsclesdepartementcommune
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "CHARBONNE", "", "75 PARIS", ""
//                }, "RUE DE CHARONNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014 PARIS", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "SQUARE ALICE", "", "75 PARIS", ""
//                }, "SQUARE ALICE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL MANGI", "", "75 PARIS", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PAUL TANGI", "", "75 PARIS", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TANVY", "", "75 PARIS", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "TANVY", "", "75 PARIS", ""
//                }, "AV DU COLONEL HENRI ROL TANGUY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AV DU COLONEL HENRI ROL TANVY", "", "75 PARIS"
//                },
//                "AV DU COLONEL HENRI ROL TANGUY", db1);
//        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
//        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "AV H ROL TANGUY", "", "75 PARI", ""
//                    }, "AV DU COLONEL HENRI ROL TANGUY",
//                    db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL", "", "75 PARIS 5", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE", "", "75 PARIS", ""
//                }, "SQUARE DE L AVENUE DU BOIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "PARIS", ""
//                }, "RUE ALFRED ROLL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014 PARIS", ""
//                }, "AVENUE PAUL APPELL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75014", ""
//                }, "AVENUE PAUL APPELL", db1);
//
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL SAINT", "", "75013 PARIS", ""
//                }, "RUE DE L HOPITAL SAINT LOUIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "OPITL LOUIS", "", "75 PARIS", ""
//                }, "RUE DE L HOPITAL SAINT LOUIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "ROL TAN", "", "75068 PARIS", ""
//                }, "RUE ALFRED ROLL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "MACDONALD", "", "75015 PARIS", ""
//                }, "BOULEVARD MACDONALD", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "HOPITAL", "", "75013 PARIS", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//
//        // motsclescommune)
//        if (gestDpt.isDptCodePresent("45"))
//        {
//            testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "CHARLES DE GAULLE", "", "SAINT", ""
//                }, "45130 SAINT AY", db1);
//        }
//
//        // adressecomplete)
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "75013", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITOL", "", "PARIS", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "PARIS", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DARU", "", "75013 PARIS", ""
//                }, "RUE DARU", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE L AVRE", "", "75015 PARIS", ""
//                }, "RUE DE L AVRE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "2 ROUTE DE L OUEST", "", "94", ""
//                }, "2 ROUTE DE L OUEST", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "1 RUE PIERRE BROSSOLETTE", "", "92004", ""
//                }, "1 RUE PIERRE BROSSOLETTE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "", "75116 PARIS", ""
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 B PLACE DU MARECHAL DE LATTRE DE TASSIGNY", "", "75116 PARIS", ""
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 B BD DU MAL DE LATTRE DE TASSIGNY", "", "75008", ""
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PL DU MARECHAL DE LATTRE DE TASSIGNY", "", "75016 PARIS", ""
//                },
//                "PL DU MAL DE LATTRE DE TASSIGNY", db1);
//        // NB: type de voie compte plus que libellé ?
//        if(5 * params.obtientNotePourTypeDeVoie() <= params.obtientNotePourLibelle())
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "11 RUE DU CHATELET", "", "75001 PARIS", ""
//                    }, "PLACE DU CHATELET", db1);
//        } else
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "11 RUE DU CHATELET", "", "75001 PARIS", ""
//                    }, "11 RUE DE LA CHAPELLE", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "25 RUE CHAZAL", "", " 75004 PARIS", ""
//                }, "25 RUE CHAPTAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BD HOPITAL", "", "75 PARIS", ""
//                }, "24 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "100 BOULEVARD HOPITAL", "", "75 PARIS", ""
//                }, "100 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "100 BOULEVARD HOPITAL", "", "PARIS", ""
//                }, "100 BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "75 PARIS 5", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITAL", "", "PARIS", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        // Ce test est valable uniquement si la tolérance vis à vis des communes est faible.
//        if(params.obtientPourcentageDeCorrespondanceDeCommune() < 60)
//        {
//            testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "BOULEVARD HOPITAL", "", "PARI", ""
//                    }, "BOULEVARD DE L HOPITAL", db1);
//        }
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "BOBIGNY", ""
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "93 BOBIGNY", ""
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARI", "", "93 BOBIGNY", ""
//                }, "RUE DE PARIS", db1);
//        // Test pertinent si le département 76 est présent
//        //testValide(referentiel,"24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945","76039 LES AUTHIEUX SUR LE PORT SAINT OUEN","24 ROND POINT DES 11 NOVEMBRE 1918 ET 8 MAI 1945",db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "VOIE A 11", "", "75011 PARIS", ""
//                }, "VOIE COMMUNALE A 11", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "93000 BOBIGNY", ""
//                }, "RUE DE PARIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "RUE DE PARIS", "", "BOBIGNY", ""
//                }, "RUE DE PARIS", db1);
//        // Test pertinent si le département 77 est présent.
//        //testValide(referentiel,"123 RUE PARC DES RIGOUTS","77190 DAMMARIE","123 RUE PARC DES RIGOUTS",db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PORTE DE GENTILLY", "", "7 5 PARIS", ""
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "24 BOULEVARD DE L HOPITAL", "", "PARIS 05", ""
//                }, "24 BOULEVARD DE L HOPITAL",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "6 RQUE BAULANT", "", "H 75 PARIM", ""
//                }, "6 RUE BAULANT", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "50 RUE DU GAL DE GAULLE", "", "94510 LA QUEUE EN BRIE", ""
//                },
//                "50 RUE DU GENERAL DE GAULLE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "BOULEVARD HOPITALE", "", "75 PARIS", ""
//                }, "BOULEVARD DE L HOPITAL", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "PASSAGE LEMOINE", "", "PARIS", ""
//                }, "PASSAGE LEMOINE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "2 ROUTE DE L OUEST", "", "94380 BONNEUIL SUR MARNE", ""
//                }, "2 ROUTE DE L OUEST",
//                db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "6 RUE PONT DE LODI", "", "75 8 PA 2 IS", ""
//                }, "6 RUE DU PONT DE LODI", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H7APRIS", ""
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H 7 APRIS", ""
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "59 AVENUE SAXF", "", "H 7 APRIS", ""
//                }, "", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "AVENUE PORTE LILAS", "", "75 4 ARIS", ""
//                }, "AVENUE DE LA PORTE DES LILAS", db1);
//
//        db1.close();
//    }    
//    
//    @Test
//    public void testsValideFullPays() throws JDOMException, IOException, JDONREFException, ClassNotFoundException, SQLException,
//            GestionMotsException, Exception
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
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
////        GestionLogs.getInstance().definitRepertoire(".");
//        GestionValidation validation = new GestionValidation();
//        validation.setGestionMots(gm);
//        validation.setJdonrefParams(params);
//        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        }
//        
//        JDONREFv3Lib.getInstance("params.xml");
//        
//        
//        // Tests Pays etrangers
//        testValideFull(referentiel, new String[]
//                {
//                    "MS S POLLARD", "1 CHAPEL STREET", "HESWALL", "BOURNEMOUTH", "", "BH1 1AA", "ROYAUME UNI"
//                },
//                "ROYAUME UNI", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "FIRMA ABC", "", "KUNDENDIENST", "HAUPTSTR 5", "", "01234 MUSTERSTADT", "ALLEMAGNE"
//                },
//                "ALLEMAGNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "FIRMA ABC", "", "KUNDENDIENST", "HAUPTSTR 5", "", "01234 MUSTERSTADT", "ALEMAGNE"
//                },
//                "ALLEMAGNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "JEREMY MARTINSON", "", "", "455 LARKSPUR DR", "", "CALIFORNIA SPRINGS, CA 92926 4601",
//                    "ETATS UNIS"
//                }, "ETATS UNIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "JEREMY MARTINSON", "", "", "455 LARKSPUR DR", "", "CALIFORNIA SPRINGS, CA 92926 4601",
//                    "ETAT UNIS"
//                }, "ETATS UNIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "PAUL VAN DE BRUURRR", "", "", "RUE ANATOLE FRANCE", "", "1030 SCHAERBEEK", "BELGIQUE"
//                },
//                "BELGIQUE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "PAUL VAN DE BRUURRR", "", "", "RUE ANATOLE FRANCE", "", "1030 SCHAERBEEK", "BELGIKUE"
//                },
//                "BELGIQUE", db1);
//
//
//
//        // Tests suite à la charge apres les pays
////        testValideCommune(referentiel, "93 SONDY", date, false, true, "93140 BONDY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "", "", "93 SONDY"
//                },
//                "93140 BONDY", db1);
//        testValideFull(referentiel, new String[] { "", "", "", "", "", "93 SONDY", ""},"93140 BONDY", db1);
//
//        testValideFull(referentiel, new String[] { "", "", "", "", "", "", "FRANCE"},"FRANCE", db1);
//        
//        // Tests Pays etrangers
//        testValideFull(referentiel, new String[]
//                {
//                    "MS S POLLARD", "1 CHAPEL STREET", "HESWALL", "BOURNEMOUTH", "", "BH1 1AA", "ROYAUME UNI"
//                },
//                "ROYAUME UNI", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "FIRMA ABC", "", "KUNDENDIENST", "HAUPTSTR 5", "", "01234 MUSTERSTADT", "ALLEMAGNE"
//                },
//                "ALLEMAGNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "FIRMA ABC", "", "KUNDENDIENST", "HAUPTSTR 5", "", "01234 MUSTERSTADT", "ALEMAGNE"
//                },
//                "ALLEMAGNE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "JEREMY MARTINSON", "", "", "455 LARKSPUR DR", "", "CALIFORNIA SPRINGS, CA 92926 4601",
//                    "ETATS UNIS"
//                }, "ETATS UNIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "JEREMY MARTINSON", "", "", "455 LARKSPUR DR", "", "CALIFORNIA SPRINGS, CA 92926 4601",
//                    "ETAT UNIS"
//                }, "ETATS UNIS", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "PAUL VAN DE BRUURRR", "", "", "RUE ANATOLE FRANCE", "", "1030 SCHAERBEEK", "BELGIQUE"
//                },
//                "BELGIQUE", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "PAUL VAN DE BRUURRR", "", "", "RUE ANATOLE FRANCE", "", "1030 SCHAERBEEK", "BELGIKUE"
//                },
//                "BELGIQUE", db1);
//
//
//
//        // Tests suite à la charge apres les pays
////        testValideCommune(referentiel, "93 SONDY", date, false, true, "93140 BONDY", db1);
//        testValideFull(referentiel, new String[]
//                {
//                    "", "", "", "", "", "93 SONDY"
//                },
//                "93140 BONDY", db1);
//        testValideFull(referentiel, new String[] { "", "", "", "", "", "93 SONDY", ""},"93140 BONDY", db1);
//
//        testValideFull(referentiel, new String[] { "", "", "", "", "", "", "FRANCE"},"FRANCE", db1);
//
//
//        db1.close();
//    }
//    
//    @Test
//    public void testsValideFullDomTom() throws JDOMException, IOException, JDONREFException, ClassNotFoundException, SQLException,
//            GestionMotsException, Exception
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
//        GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);
////        GestionLogs.getInstance().definitRepertoire(".");
//        GestionValidation validation = new GestionValidation();
//        validation.setGestionMots(gm);
//        validation.setJdonrefParams(params);
//        GestionCodesDepartements gestDpt = GestionCodesDepartements.getInstance();
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            gestDpt.loadDptCodes(db1, "departementsSynonymes.xml", "algosCP-Departements.xml");
//        }
//        
//        JDONREFv3Lib.getInstance("params.xml");
//
//        // WA 09/2011 Ajout de tests en corse et dom tom
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            if (gestDpt.isDptCodePresent("04"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "04", "FRANCE"
//                    },
//                    "PLACE DE L HOTEL DE VILLE", db1);
//            }
//            if (gestDpt.isDptCodePresent("20 A"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "20 A", "FRANCE"
//                    },
//                    "PLACE DE L HOTEL DE VILLE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A", "FRANCE"
//                    }, "CHEMIN DE LA VERDOLINE",
//                    db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20070 DIGNE LES BAINS", "FRANCE"
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A DIGNE LES BAINS", "FRANCE"
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "2 A DIGNE LES BAINS", "FRANCE"
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//            }
//            if (gestDpt.isDptCodePresent("20 B"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "20 B", "FRANCE"
//                    },
//                    "PLACE DE LA FONT VIEILLE", db1);
//            }
//            if (gestDpt.isDptCodePresent("93"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE DE PARI", "", "93 BOBIGNY", "FRANCE"
//                    }, "RUE DE PARIS", db1);
//            }
//            if (gestDpt.isDptCodePresent("90"))
//            {
//                // En test, les departements 971 et 972 sont remplis a partir du 90
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "90000 BELFORT", "FRANCE"
//                    }, "RUE EMILE ZOLA", db1);
//            }
//            if (gestDpt.isDptCodePresent("97"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "97110 BELFORT", "FRANCE"
//                    }, "RUE EMILE ZOLA", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "97210 BELFORT", "FRANCE"
//                    }, "RUE EMILE ZOLA", db1);
//            }
//        }
//        
//        // WA 09/2011 Ajout de tests en corse et dom tom
//        if(params.isUtilisationDeLaGestionDesDepartements())
//        {
//            if (gestDpt.isDptCodePresent("04"))
//            {  
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "04", ""
//                    }, "PLACE DE L HOTEL DE VILLE",
//                    db1);
//            }
//            if (gestDpt.isDptCodePresent("20 A"))
//            { 
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "20 A", ""
//                    }, "PLACE DE L HOTEL DE VILLE",
//                    db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A", ""
//                    }, "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20070 DIGNE LES BAINS", ""
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "20 A DIGNE LES BAINS", ""
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "CHEMIN DE LA VERDOLINE", "", "2 A DIGNE LES BAINS", ""
//                    },
//                    "CHEMIN DE LA VERDOLINE", db1);
//            }
//            if (gestDpt.isDptCodePresent("20 B"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "PLACE DE L HOTEL DE VILLE", "", "20 B", ""
//                    }, "PLACE DE LA FONT VIEILLE",
//                    db1);
//            }
//            if (gestDpt.isDptCodePresent("93"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE DE PARI", "", "93 BOBIGNY", ""
//                    }, "RUE DE PARIS", db1);
//            }
//            if (gestDpt.isDptCodePresent("90"))
//            {
//                // En test, les departements 971 et 972 sont remplis a partir du 90
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "90000 BELFORT", ""
//                    }, "RUE EMILE ZOLA", db1);
//            }
//            if (gestDpt.isDptCodePresent("97"))
//            {
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "97110 BELFORT", ""
//                    }, "RUE EMILE ZOLA", db1);
//                testValideFull(referentiel, new String[]
//                    {
//                        "", "", "", "RUE EMILE ZOLA", "", "97210 BELFORT", ""
//                    }, "RUE EMILE ZOLA", db1);
//            }
//        }
//
//
//        db1.close();
//    }

}
