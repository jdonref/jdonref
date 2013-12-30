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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 * Permet de simuler des profils d'utilisateur avec des échantillons d'adresse pré-générés.
 * @author jmoquet
 */
public class Simulateur {

    Parametres parametres = new Parametres();
    ArrayList<UserThread> threads = new ArrayList<UserThread>();

    /**
     * Constructeur par défaut.
     */
    public Simulateur() throws ClassNotFoundException {
    }

    /**
     * Charge les fichiers nécessaires au simulateur.
     * @param rep
     */
    public void load(String rep) throws JDOMException, IOException, Exception {
        parametres.loadJDONREF(rep + "/jdonref.xml");
        parametres.loadProfils(rep + "/profils.xml");
        parametres.loadScenarios(rep + "/scenarios.xml");
    }

    /**
     * Lance la simulation d'un profil.
     */
    public void doone(Profil profil, String rep) throws MalformedURLException {
        for (int i = 0; i < profil.nbusers; i++) {
            UserThread ut = new UserThread();
            ut.profil = profil;
            ut.service = parametres.params.getService();
            ut.user = i;

            threads.add(ut);
            ut.start();
        }
    }

    /**
     * Lance la simulation.
     */
    public void doit(String rep, long time) throws FileNotFoundException, IOException, Exception {
        // Charge les échantillons
        parametres.profils.loadAdresses(rep);

        System.out.println("Lancement des threads");

        // Puis pour chaque profil,
        for (int i = 0; i < parametres.profils.profils.size(); i++) {
            Profil p = parametres.profils.profils.get(i);

            // lance la simulation sur ce profil.
            doone(p, rep);
        }

        System.out.println("En attente");
        Thread.sleep(time);


        // une fois le temps écoulé, la simulation sera arrêtée.
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).mustStop = true;
            threads.get(i).interrupt();
        }

        // Attemps que tous les threads soient arrêtés
        int count;
        do {
            int i;
            for (i = 0, count = 0; i < threads.size(); i++) {
                if (threads.get(i).stopped) {
                    count++;
                }
            }
        } while (count != threads.size());

        // et créé les fichiers de log
        for (int i = 0; i < parametres.profils.profils.size(); i++) {
            parametres.profils.profils.get(i).logs.write(rep, parametres.profils.profils.get(i));
        }
    }

    /**
     * Teste la classe.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Simulateur s = new Simulateur();
            s.load("test");
            s.doit("test", 30000);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Simulateur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Simulateur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Simulateur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Simulateur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
