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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Permet de gérer un ensemble d'adresses.
 * @author jmoquet
 */
public class Adresses {

    static Random r = new Random(Calendar.getInstance().getTimeInMillis());
    ArrayList<Adresse> adresses = new ArrayList<Adresse>();

    /**
     * Constructeur par défaut.
     */
    public Adresses() {
    }

    /**
     * Charge des adresses à partir d'un échantillon généré.
     */
    public void load(String repertoire, String profil) throws FileNotFoundException, IOException, Exception {
        BufferedReader br = new BufferedReader(new FileReader(repertoire + "/echantillons/" + profil + ".txt"));

        String line = br.readLine();

        while (line != null) {
            Adresse a = new Adresse();

            try {
                a.load(line);
            } catch (NumberFormatException nfe) {
                throw (new Exception("L'échantillon " + profil + " est incorrect.", nfe));
            }

            adresses.add(a);

            line = br.readLine();
        }

        br.close();
    }

    /**
     * Ecrit les adresses dans un fichier échantillon
     */
    public void write(String repertoire, String profil) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(repertoire + "/echantillon/" + profil + ".txt"));

        for (int i = 0; i < adresses.size(); i++) {
            writer.write(adresses.get(i).ecrit());
            writer.write("\r\n");
        }

        writer.close();
    }

    /**
     * Teste la classe.
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Teste la classe Adresses");
        Adresses adresses = new Adresses();
        int size = 10;
        for (int i = 0; i < size; i++) {
            Adresse a = new Adresse();
            a.ville = "PARIS";
            a.libelle = "RUE TRUC";
            a.genereArrondissement();
            a.genereCles();
            a.genereNumero();
            a.genereRepetition();
            a.calculeLignes();
            a.melangeLignes(7);
            adresses.adresses.add(a);
        }
        try {
            adresses.write("testrep", "test");
        } catch (IOException ex) {
            Logger.getLogger(Adresses.class.getName()).log(Level.SEVERE, null, ex);
        }

        Adresses resultat = new Adresses();
        try {
            resultat.load("testrep", "test");

            int erreurs = 0;

            for (int i = 0; i < Math.min(adresses.adresses.size(), resultat.adresses.size()); i++) {

            }
            if (adresses.adresses.size() != resultat.adresses.size()) {
                erreurs++;
            }
            if (erreurs != 0) {
                System.out.println(erreurs + " ont été trouvée(s).");
            } else {
                System.out.println("Aucune erreur trouvée.");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Adresses.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Adresses.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Adresses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtient une adresse aléatoire.
     * @return
     */
    public Adresse getAdresse() {
        int index = r.nextInt(adresses.size());
        return adresses.get(index);
    }
}
