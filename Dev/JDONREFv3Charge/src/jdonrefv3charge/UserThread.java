/*
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
package jdonrefv3charge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.JDONREFService;

/**
 * Thread simulant l'activité d'un utilisateur.
 * @author jmoquet
 */
public class UserThread extends Thread {

    /**
     * Le profil de cet utilisateur.
     */
    Profil profil = null;
    /**
     * L'accès à JDONREF de cet utilisateur.
     */
    JDONREFService service = null;
    /**
     * Le numéro de l'utilisateur.
     */
    int user = 0;
    /**
     * Permet d'arrêter le thread.
     */
    boolean mustStop = false;
    /**
     * Indique que le thread va s'arrêter imminemment (aucune écriture dans le writer).
     */
    boolean stopped = false;

    /**
     * Constructeur par défaut.
     */
    public UserThread() {
    }

    /**
     * Loggue les informations.
     */
    private void loggue(long starttime, String scenario, String modele, int nbfautes, int structure, String departement,
            boolean ok, int codeerreur,
            long dureetotale, long dureeNormalise1, long dureeNormalise2,
            long dureeValide1, long dureeValide2, long dureeGeocode1, long dureeGeocode2,
            long dureeReverse1, long dureeReverse2,
            String ligne1, String ligne4, String ligne6, String ligne7,
            String date, String x, String y, String note,
            String retourNormalise1, int serviceNormalise1, int operationNormalise1,
            String retourNormalise2, int serviceNormalise2, int operationNormalise2,
            String retourValide1, int serviceValide1, String retourValide2, int serviceValide2,
            String retourGeocode1, int serviceGeocode1, String retourGeocode2, int serviceGeocode2,
            String retourReverse1, int serviceReverse1, String retourReverse2, int serviceReverse2) throws IOException {
        Log log = new Log();
        log.starttime = starttime;
        log.profil = profil.nom;
        log.user = user;
        log.scenario = scenario.charAt(0);
        log.modele = modele;
        log.nbfautes = nbfautes;
        log.structure = structure;
        log.departement = departement;
        log.ok = ok;
        log.codeerreur = codeerreur;
        log.dureetotale = dureetotale;
        log.dureeNormalise1 = dureeNormalise1;
        log.dureeNormalise2 = dureeNormalise2;
        log.dureeValide1 = dureeValide1;
        log.dureeValide2 = dureeValide2;
        log.dureeGeocode1 = dureeGeocode1;
        log.dureeGeocode2 = dureeGeocode2;
        log.dureeReverse1 = dureeReverse1;
        log.dureeReverse2 = dureeReverse2;
        log.ligne1 = ligne1;
        log.ligne4 = ligne4;
        log.ligne6 = ligne6;
        log.ligne7 = ligne7;
        log.date = date;
        log.x = x;
        log.y = y;
        log.note = note;
        log.retourNormalise1 = retourNormalise1;
        log.serviceNormalise1 = serviceNormalise1;
        log.operationNormalise1 = operationNormalise1;
        log.retourNormalise2 = retourNormalise2;
        log.serviceNormalise2 = serviceNormalise2;
        log.operationNormalise2 = operationNormalise2;
        log.retourValide1 = retourValide1;
        log.serviceValide1 = serviceValide1;
        log.retourValide2 = retourValide2;
        log.serviceValide2 = serviceValide2;
        log.retourGeocode1 = retourGeocode1;
        log.serviceGeocode1 = serviceGeocode1;
        log.retourGeocode2 = retourGeocode2;
        log.serviceGeocode2 = serviceGeocode2;
        log.retourReverse1 = retourReverse1;
        log.serviceReverse1 = serviceReverse1;
        log.retourReverse2 = retourReverse2;
        log.serviceReverse2 = serviceReverse2;
        profil.logs.logs.add(log);
    }
    private final static int NANOTOMILI = 1000000;

    /**
     * Simule les manipulations d'un utilisateur pour une adresse spécifiée.<br>
    /**
     * Code d'erreur pour les méthodes valide, restructure, et géocode.
     * <ul>
     *   <li>0 si aucune erreur ne s'est produit.</li>
     *   <li>Concernant la validation<ul>
     *   <li>1 si la méthode a retourné une erreur.</li>
     *   <li>2 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>3 si aucune proposition n'est retournée.</li>
     *   <li>4 si jdonref a retourné la valeur null</li>
     *   <li>5 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant le géocodage<ul>
     *   <li>6 si la méthode a retourné une erreur.</li>
     *   <li>7 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>8 si aucune proposition n'est retournée.</li>
     *   <li>9 si jdonref a retourné la valeur null</li>
     *   <li>10 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant la restructuration<ul>
     *   <li>11 si la méthode a retourné une erreur.</li>
     *   <li>12 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>13 si aucune proposition n'est retournée.</li>
     *   <li>14 si jdonref a retourné la valeur null</li>
     *   <li>15 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Autres erreurs<ul>
     *   <li>16 pour les cas où un problème de timer est apparu</li>
     *   </ul></li>
     * </ul>
     * @param a
     */
    private void doitPredefini(Adresse a, long start, String nomScenario) throws InterruptedException, IOException {
        boolean ok = true;
        int codeerreur = 0;
        long dureetotale = 0,
                dureeNormalise1 = 0,
                dureeNormalise2 = 0,
                dureeValide1 = 0,
                dureeValide2 = 0,
                dureeGeocode1 = 0,
                dureeGeocode2 = 0,
                dureeReverse1 = 0,
                dureeReverse2 = 0;
        int serviceNormalise1 = 0,
                operationNormalise1 = 0,
                serviceNormalise2 = 0,
                operationNormalise2 = 0,
                serviceValide1 = 0,
                serviceValide2 = 0,
                serviceGeocode1 = 0,
                serviceGeocode2 = 0,
                serviceReverse1 = 0,
                serviceReverse2 = 0;
        String retourNormalise1 = "",
                retourNormalise2 = "",
                retourValide1 = "",
                retourValide2 = "",
                retourGeocode1 = "",
                retourGeocode2 = "",
                retourReverse1 = "",
                retourReverse2 = "";

        // Lance un timer
        long time0 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();

        // Obtient une adresse aléatoire.
        Adresse b = a.clone();

        // Si besoin, la restructure
        if (profil.restruct) {
            if (a != null) {
                long time1 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                Adresse c = a.normalise(service, new int[]{1}, 1 + 2, new String[0]);
                retourNormalise1 = a.retour;
                serviceNormalise1 = a.service;
                operationNormalise1 = 3;
                dureeNormalise1 = (System.nanoTime() - time1) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }
            }
        }

        // Effectue une validation
        if (ok && a != null) {
            long time2 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
            Adresse c = a.valide(service, new int[]{1}, 1, new String[0]); // service = adresse
            retourValide1 = a.retour;
            serviceValide1 = a.service;
            dureeValide1 = (System.nanoTime() - time2) / NANOTOMILI; // Calendar.getInstance().getTimeInMillis()-time2;
            if (c == null) {
                codeerreur = a.codeerreur;
                ok = false;
            } else {
                a = c;
            }
        }

        // attente du choix de l'utilisateur
        Thread.sleep(profil.acces.obtientSaisie());

        if (ok) {

            if (nomScenario.equals("a")) // scénario A : validation réussie
            {
                if (profil.geocod) // Si il est nécessaire de géocoder,
                {
                    long time3 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                    if (!a.geocode(service, new int[]{1}, new String[]{"projection=" + a.projection})) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourGeocode1 = a.retour;
                    serviceGeocode1 = a.service;
                    dureeGeocode1 = (System.nanoTime() - time3) / NANOTOMILI;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }

                if (profil.reverse) { // Geocodage inverse
                    long time31 = System.nanoTime();
                    if (!a.reverse(service)) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourReverse1 = a.retour;
                    serviceReverse1 = a.service;
                    dureeReverse1 = (System.nanoTime() - time31) / NANOTOMILI;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }
            } else if (nomScenario.equals("b")) // scénario B : validation échoue, validation commune réussie
            {
                long time6 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                Adresse c = a.valide(service, new int[]{5}, 1, new String[0]); // service = commune
                retourValide1 = a.retour;
                serviceValide1 = a.service;
                dureeValide1 = (System.nanoTime() - time6) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }
                // Attente du choix de l'utilisateur.
                Thread.sleep(profil.acces.obtientSaisie());

                if (ok && profil.geocod) { // Geocodage 
                    long time7 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                    if (!a.geocode(service, new int[]{5}, new String[]{"projection=" + a.projection})) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourGeocode1 = a.retour;
                    serviceGeocode1 = a.service;
                    dureeGeocode1 = (System.nanoTime() - time7) / NANOTOMILI;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }

                if (ok && profil.reverse) { // Geocodage inverse
                    long time71 = System.nanoTime();
                    if (!a.reverse(service)) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourReverse1 = a.retour;
                    serviceReverse1 = a.service;
                    dureeReverse1 = (System.nanoTime() - time71) / NANOTOMILI;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }
            } else if (nomScenario.equals("c")) // scénario C : échec de validation, même après validation commune
            {
                long time7 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                Adresse c = a.valide(service, new int[]{5}, 1, new String[0]); // service = commune
                retourValide1 = a.retour;
                serviceValide1 = a.service;
                dureeValide1 = (System.nanoTime() - time7) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }

                // Attente du choix de l'utilisateur.
                Thread.sleep(profil.acces.obtientSaisie());
            } else if (nomScenario.equals("d")) // scénario D : validation réussie après correction
            {
                long time4 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                Adresse c = a.valide(service, new int[]{1}, 1, new String[0]); // service = adresse
                retourValide2 = a.retour;
                serviceValide2 = a.service;
                dureeValide2 = (System.nanoTime() - time4) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }

                if (ok && profil.geocod) { // Geocodage 
                    long time5 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                    if (!a.geocode(service, new int[]{1}, new String[]{"projection=" + a.projection})) {
                        ok = false;
                        codeerreur = a.codeerreur;
                    }
                    retourGeocode1 = a.retour;
                    dureeGeocode1 = (System.nanoTime() - time5) / NANOTOMILI;
                    serviceGeocode1 = a.service;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }

                if (ok && profil.reverse) { // Geocodage inverse
                    long time51 = System.nanoTime();
                    if (!a.reverse(service)) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourReverse1 = a.retour;
                    dureeReverse1 = (System.nanoTime() - time51) / NANOTOMILI;
                    serviceReverse1 = a.service;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }
            } else if (nomScenario.equals("e")) // scénario E : validation échoue, validation commune réussie après correction
            {
                long time5 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                Adresse c = a.valide(service, new int[]{1}, 1, new String[0]); // service = adresse
                retourValide1 = a.retour;
                serviceValide1 = a.service;
                dureeValide1 = (System.nanoTime() - time5) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }

                // Attente du choix de l'utilisateur.
                Thread.sleep(profil.acces.obtientSaisie());

                if (ok) {
                    long time8 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                    c = a.valide(service, new int[]{5}, 1, new String[0]); // service = commune
                    retourValide2 = a.retour;
                    serviceValide2 = a.service;
                    dureeValide2 = (System.nanoTime() - time8) / NANOTOMILI;
                    if (c == null) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    } else {
                        a = c;
                    }

                    // Attente du choix de l'utilisateur.
                    Thread.sleep(profil.acces.obtientSaisie());

                    if (ok && profil.geocod) { // Geocodage 
                        long time9 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                        if (!a.geocode(service, new int[]{5}, new String[]{"projection=" + a.projection})) {
                            ok = false;
                            codeerreur = a.codeerreur;
                        }
                        retourGeocode1 = a.retour;
                        serviceGeocode1 = a.service;
                        dureeGeocode1 = (System.nanoTime() - time9) / NANOTOMILI;

                        // attente du choix de l'utilisateur
                        Thread.sleep(profil.acces.obtientSaisie());
                    }

                    if (ok && profil.reverse) { // Geocodage inverse
                        long time91 = System.nanoTime();
                        if (!a.reverse(service)) {
                            codeerreur = a.codeerreur;
                            ok = false;
                        }
                        retourReverse1 = a.retour;
                        dureeReverse1 = (System.nanoTime() - time91) / NANOTOMILI;
                        serviceReverse1 = a.service;

                        // attente du choix de l'utilisateur
                        Thread.sleep(profil.acces.obtientSaisie());
                    }
                }
            } else if (nomScenario.equals("f")) // scénario F : échec de validation, même après recorrection et validation commune
            {
                long time9 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                Adresse c = a.valide(service, new int[]{1}, 1, new String[0]); // service = adresse
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }
                retourValide2 = a.retour;
                serviceValide2 = a.service;
                dureeValide2 = (System.nanoTime() - time9) / NANOTOMILI;

                // Attente du choix de l'utilisateur.
                Thread.sleep(profil.acces.obtientSaisie());

                if (ok) {
                    long time10 = System.nanoTime();
                    c = a.valide(service, new int[]{5}, 1, new String[0]); // service = commune
                    if (c == null) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    } else {
                        a = c;
                    }
                    retourValide1 = a.retour;
                    serviceValide1 = a.service;
                    dureeValide1 = (System.nanoTime() - time10) / NANOTOMILI;

                    // Attente du choix de l'utilisateur.
                    Thread.sleep(profil.acces.obtientSaisie());
                }
            } else if (nomScenario.equals("g")) // Scénario g : validation au pays, pas de correction necessaire
            {
                long time10 = System.nanoTime();
                Adresse c = a.valide(service, new int[]{7}, 1, new String[0]); // service = pays
                retourValide1 = a.retour;
                serviceValide1 = a.service;
                dureeValide1 = (System.nanoTime() - time10) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }

                // Attente du choix de l'utilisateur.
                Thread.sleep(profil.acces.obtientSaisie());

                if (ok && profil.geocod) { // Geocodage 
                    long time11 = System.nanoTime();
                    if (!a.geocode(service, new int[]{7}, new String[]{"projection=" + a.projection})) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourGeocode1 = a.retour;
                    serviceGeocode1 = a.service;
                    dureeGeocode1 = (System.nanoTime() - time11) / NANOTOMILI;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }

                if (ok && profil.reverse) { // Geocodage inverse
                    long time111 = System.nanoTime();
                    if (!a.reverse(service)) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    }
                    retourReverse1 = a.retour;
                    serviceReverse1 = a.service;
                    dureeReverse1 = (System.nanoTime() - time111) / NANOTOMILI;

                    // attente du choix de l'utilisateur
                    Thread.sleep(profil.acces.obtientSaisie());
                }
            } else if (nomScenario.equals("h")) // Scenario h : validation au pays apres une correction
            {
                long time12 = System.nanoTime();
                //Adresse c = a.valide(service, false, false, true);
                Adresse c = a.valide(service, new int[]{7}, 1, new String[0]); // service = pays
                retourValide1 = a.retour;
                serviceValide1 = a.service;
                dureeValide1 = (System.nanoTime() - time12) / NANOTOMILI;
                if (c == null) {
                    codeerreur = a.codeerreur;
                    ok = false;
                } else {
                    a = c;
                }

                // Attente du choix de l'utilisateur.
                Thread.sleep(profil.acces.obtientSaisie());

                if (ok) {
                    long time13 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();
                    //c = a.valide(service, false, false, true);
                    c = a.valide(service, new int[]{7}, 1, new String[0]); // service = pays
                    retourValide2 = a.retour;
                    serviceValide2 = a.service;
                    dureeValide2 = (System.nanoTime() - time13) / NANOTOMILI;
                    if (c == null) {
                        codeerreur = a.codeerreur;
                        ok = false;
                    } else {
                        a = c;
                    }

                    // Attente du choix de l'utilisateur.
                    Thread.sleep(profil.acces.obtientSaisie());

                    if (ok && profil.geocod) { // Geocodage 
                        long time14 = System.nanoTime();
                        //if (!a.geocode(service, false, true)) {
                        if (!a.geocode(service, new int[]{7}, new String[]{"projection=" + a.projection})) {
                            ok = false;
                            codeerreur = a.codeerreur;
                        }
                        retourGeocode1 = a.retour;
                        serviceGeocode1 = a.service;
                        dureeGeocode1 = (System.nanoTime() - time14) / NANOTOMILI;

                        // attente du choix de l'utilisateur
                        Thread.sleep(profil.acces.obtientSaisie());
                    }

                    if (ok && profil.reverse) { // Geocodage inverse
                        long time141 = System.nanoTime();
                        if (!a.reverse(service)) {
                            codeerreur = a.codeerreur;
                            ok = false;
                        }
                        retourReverse1 = a.retour;
                        serviceReverse1 = a.service;
                        dureeReverse1 = (System.nanoTime() - time141) / NANOTOMILI;

                        // Attente du choix de l'utilisateur.
                        Thread.sleep(profil.acces.obtientSaisie());
                    }
                }
            }
        }
        dureetotale = (System.nanoTime() - time0) / NANOTOMILI; // Calendar.getInstance().getTimeInMillis()-time0;
        // Bizarrement, c'est arrivé. // WA : devrait etre corrige par les System.nanoTime() ...
        if (dureetotale < 0 || dureeValide1 < 0 || dureeValide2 < 0 ||
                dureeGeocode1 < 0 || dureeGeocode2 < 0 || dureeReverse1 < 0 ||
                dureeReverse2 < 0 || dureeNormalise1 < 0 || dureeNormalise2 < 0) {
            codeerreur = 16;
            ok = false;
        }
        loggue(start, nomScenario,
                b.modele, b.nbfautes, b.structure, b.departement,
                ok, codeerreur, dureetotale,
                dureeNormalise1, dureeNormalise2,
                dureeValide1, dureeValide2,
                dureeGeocode1, dureeGeocode2,
                dureeReverse1, dureeReverse2,
                b.ligne1, b.ligne4, b.ligne6, b.ligne7,
                a != null ? a.date : null, a.x, a.y, a.note,
                retourNormalise1, serviceNormalise1, operationNormalise1,
                retourNormalise2, serviceNormalise2, operationNormalise2,
                retourValide1, serviceValide1, retourValide2, serviceValide2,
                retourGeocode1, serviceGeocode1, retourGeocode2, serviceGeocode2,
                retourReverse1, serviceReverse1, retourReverse2, serviceReverse2);
    }

    /**
     * Simule les manipulations d'un utilisateur pour une adresse spécifiée.<br>
    /**
     * Code d'erreur pour les méthodes valide, restructure, et géocode.
     * <ul>
     *   <li>0 si aucune erreur ne s'est produit.</li>
     *   <li>Concernant la validation<ul>
     *   <li>1 si la méthode a retourné une erreur.</li>
     *   <li>2 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>3 si aucune proposition n'est retournée.</li>
     *   <li>4 si jdonref a retourné la valeur null</li>
     *   <li>5 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant le géocodage<ul>
     *   <li>6 si la méthode a retourné une erreur.</li>
     *   <li>7 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>8 si aucune proposition n'est retournée.</li>
     *   <li>9 si jdonref a retourné la valeur null</li>
     *   <li>10 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Concernant la restructuration<ul>
     *   <li>11 si la méthode a retourné une erreur.</li>
     *   <li>12 si la solution n'a pas été trouvée parmi les propositions.</li>
     *   <li>13 si aucune proposition n'est retournée.</li>
     *   <li>14 si jdonref a retourné la valeur null</li>
     *   <li>15 si une exception s'est produite</li>
     *   </ul></li>
     *   <li>Autres erreurs<ul>
     *   <li>16 pour les cas où un problème de timer est apparu</li>
     *   </ul></li>
     * </ul>
     * @param a
     */
    public void doit(Adresse a, long start, String nomScenario) throws InterruptedException, IOException {
        if (Scenarios.estPredefini(nomScenario)) {
            doitPredefini(a, start, nomScenario);
        } else {
            boolean ok = true;
            int codeerreur = 0;
            long dureetotale = 0,
                    dureeNormalise1 = 0,
                    dureeNormalise2 = 0,
                    dureeValide1 = 0,
                    dureeValide2 = 0,
                    dureeGeocode1 = 0,
                    dureeGeocode2 = 0,
                    dureeReverse1 = 0,
                    dureeReverse2 = 0;
            int serviceNormalise1 = 0,
                    operationNormalise1 = 0,
                    serviceNormalise2 = 0,
                    operationNormalise2 = 0,
                    serviceValide1 = 0,
                    serviceValide2 = 0,
                    serviceGeocode1 = 0,
                    serviceGeocode2 = 0,
                    serviceReverse1 = 0,
                    serviceReverse2 = 0;
            String retourNormalise1 = "",
                    retourNormalise2 = "",
                    retourValide1 = "",
                    retourValide2 = "",
                    retourGeocode1 = "",
                    retourGeocode2 = "",
                    retourReverse1 = "",
                    retourReverse2 = "";

            // Lance un timer
            long time0 = System.nanoTime(); // Calendar.getInstance().getTimeInMillis();

            // Obtient une adresse aléatoire.
            Adresse b = a.clone();
            final Scenario scenario = Scenarios.getInstance().obtientScenario(nomScenario);
            final List<Operation> operations = scenario.getOperations();
            int normaliseCount = 1;
            int valideCount = 1;
            int geocodeCount = 1;
            int reverseCount = 1;
            for (Operation operation : operations) {
                final String nom = operation.getNom();
                final int[] services = operation.getServicesArray();
                final String[] options = operation.getOptionsArray();
                final int operationInt = operation.getOperation();
                if (nom.equals("normalise")) {
                    if (a != null && normaliseCount < 3) {
                        long time1 = System.nanoTime();
                        Adresse c = a.normalise(service, services, operationInt, options);
                        if (normaliseCount == 1) {
                            retourNormalise1 = a.retour;
                            serviceNormalise1 = a.service;
                            operationNormalise1 = operationInt;
                            dureeNormalise1 = (System.nanoTime() - time1) / NANOTOMILI;
                        } else {
                            retourNormalise2 = a.retour;
                            serviceNormalise2 = a.service;
                            operationNormalise2 = operationInt;
                            dureeNormalise2 = (System.nanoTime() - time1) / NANOTOMILI;
                        }
                        normaliseCount += 1;
                        if (c == null) {
                            codeerreur = a.codeerreur;
                            ok = false;
                            break;
                        } else {
                            a = c;
                        }
                    }
                } else if (nom.equals("valide")) {
                    if (a != null && valideCount < 3) {
                        long time1 = System.nanoTime();
                        Adresse c = a.valide(service, services, operationInt, options);
                        if (valideCount == 1) {
                            retourValide1 = a.retour;
                            serviceValide1 = a.service;
                            dureeValide1 = (System.nanoTime() - time1) / NANOTOMILI;
                        } else {
                            retourValide2 = a.retour;
                            serviceValide2 = a.service;
                            dureeValide2 = (System.nanoTime() - time1) / NANOTOMILI;
                        }
                        valideCount += 1;
                        if (c == null) {
                            codeerreur = a.codeerreur;
                            ok = false;
                            break;
                        } else {
                            a = c;
                        }
                    }
                } else if (nom.equals("geocode")) {
                    if (a != null && geocodeCount < 3) {
                        long time1 = System.nanoTime();
                        ok = a.geocode(service, services, options);
                        if (geocodeCount == 1) {
                            retourGeocode1 = a.retour;
                            serviceGeocode1 = a.service;
                            dureeGeocode1 = (System.nanoTime() - time1) / NANOTOMILI;
                        } else {
                            retourGeocode2 = a.retour;
                            serviceGeocode2 = a.service;
                            dureeGeocode2 = (System.nanoTime() - time1) / NANOTOMILI;
                        }
                        if (!ok) {
                            codeerreur = a.codeerreur;
                            break;
                        }
                        geocodeCount += 1;
                    }
                } else if (nom.equals("reverse")) {
                    if (a != null && reverseCount < 3) {
                        long time1 = System.nanoTime();
                        ok = a.reverse(service, services, operation.getDistance(), options);
                        if (reverseCount == 1) {
                            retourReverse1 = a.retour;
                            serviceReverse1 = a.service;
                            dureeReverse1 = (System.nanoTime() - time1) / NANOTOMILI;
                        } else {
                            retourReverse2 = a.retour;
                            serviceReverse2 = a.service;
                            dureeReverse2 = (System.nanoTime() - time1) / NANOTOMILI;
                        }
                        if (!ok) {
                            codeerreur = a.codeerreur;
                            break;
                        }
                        reverseCount += 1;
                    }
                }
                // attente du choix de l'utilisateur
                Thread.sleep(profil.acces.obtientSaisie());
            }
            dureetotale = (System.nanoTime() - time0) / NANOTOMILI;
            // Bizarrement, c'est arrivé. // WA : devrait etre corrige par les System.nanoTime() ...
            if (dureetotale < 0 || dureeValide1 < 0 || dureeValide2 < 0 ||
                    dureeGeocode1 < 0 || dureeGeocode2 < 0 || dureeReverse1 < 0 ||
                    dureeReverse2 < 0 || dureeNormalise1 < 0 || dureeNormalise2 < 0) {
                codeerreur = 16;
                ok = false;
            }
            loggue(start, nomScenario,
                    b.modele, b.nbfautes, b.structure, b.departement,
                    ok, codeerreur, dureetotale,
                    dureeNormalise1, dureeNormalise2,
                    dureeValide1, dureeValide2,
                    dureeGeocode1, dureeGeocode2,
                    dureeReverse1, dureeReverse2,
                    b.ligne1, b.ligne4, b.ligne6, b.ligne7,
                    a != null ? a.date : null, a.x, a.y, a.note,
                    retourNormalise1, serviceNormalise1, operationNormalise1,
                    retourNormalise2, serviceNormalise2, operationNormalise2,
                    retourValide1, serviceValide1, retourValide2, serviceValide2,
                    retourGeocode1, serviceGeocode1, retourGeocode2, serviceGeocode2,
                    retourReverse1, serviceReverse1, retourReverse2, serviceReverse2);
        }
    }

    /**
     * Simule les manipulations de l'utilisateur.
     * Loggue les résultats.
     */
    public void doit() throws IOException, InterruptedException {
        // Lance un timer
        long start = Calendar.getInstance().getTimeInMillis();

        // Obtient un scénario aléatoire.
        String scenario = profil.getScenario();

        // Obtient une adresse aléatoire.
        Adresse a = profil.getAdresse();

        doit(a, start, scenario);
    }

    /**
     * Exécute le thread.
     */
    @Override
    public void run() {
        try {
            Acces acces = profil.acces;
            while (!mustStop) {
                sleep(acces.obtientAttenteSalve());

                int count = acces.obtientQuantite();

                while (!mustStop && count-- > 0) {
                    doit();
                    sleep(acces.obtientAttenteUnite());
                }
            }
            stopped = true;
        } catch (java.lang.InterruptedException ex) {
            if (!mustStop) {
                Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            stopped = true;
        } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
            stopped = true;
        }
    }

    /**
     * Teste la classe.
     * @param args
     */
    public static void main(String[] args) {
        try {
            boolean teststart = false;
            boolean testdoit = true;

            if (teststart) {
                Parametres params = new Parametres();
                params.loadProfils("test/profils.xml");
                params.loadJDONREF("test/jdonref.xml");

                params.profils.profils.get(1).loadAdresses("test");

                LogFile lf = new LogFile();

                UserThread ut = new UserThread();
                ut.profil = params.profils.profils.get(1);
                ut.service = params.params.getService();

                ut.start();

                Thread.sleep(30000);

                try {
                    ut.interrupt();
                } catch (Exception ex) {
                    Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                ut.profil.logs.write("test", ut.profil);
            }
            if (testdoit) {
                Parametres params = new Parametres();
                params.loadProfils("test/profils.xml");
                params.loadJDONREF("test/jdonref.xml");

                params.profils.profils.get(1).loadAdresses("test");

                UserThread ut = new UserThread();
                ut.service = params.params.getService();
                ut.profil = params.profils.profils.get(1);
                Adresse a = new Adresse();
                a.ligne4 = "RUE ROUGET DE LISLE";
                a.ligne6 = "92 COURBEV 2 IE";
                a.origine = new Adresse();
                a.origine.ligne4 = "RUE ROUGET DE LISLE";
                a.origine.ligne6 = "92 COURBEVOIE";

                ut.doit(a, Calendar.getInstance().getTimeInMillis(), "a");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
