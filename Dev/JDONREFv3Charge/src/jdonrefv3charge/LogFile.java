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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import org.jdom.JDOMException;

/**
 * Permet de gérer un fichier de logs
 * @author jmoquet
 */
public class LogFile {

    ArrayList<Log> logs = new ArrayList<Log>();

    /**
     * Constructeur par défaut.
     */
    public LogFile() {
    }

    /**
     * Cherche l'index d'un log à la même heure.
     * Sinon, retourne l'endroit où l'insérer.
     */
    public int indexOf(int start, int end, long time) {
        if (start == end) {
            long l_time = logs.get(start).starttime;
            if (l_time >= time) {
                return start;
            } else {
                return start + 1;
            }
        } else if (start + 1 == end) {
            long l_time1 = logs.get(start).starttime;
            long l_time2 = logs.get(end).starttime;

            if (time <= l_time1) {
                return start;
            }
            if (time <= l_time2) {
                return end;
            }
            return end + 1;
        } else {
            int pos1 = indexOf(start, start + (end - start) / 2, time);
            int pos2 = indexOf(start + (end - start) / 2 + 1, end, time);

            if (pos1 <= start + (end - start) / 2) {
                return pos1;
            }
            return pos2;
        }
    }

    /**
     * Ajoute un nouveau log.
     * @param log
     */
    public void addLog(Log log) {
        if (logs.size() > 0) {
            int index = indexOf(0, logs.size() - 1, log.starttime);
            logs.add(index, log);
        } else {
            logs.add(log);
        }
    }

    /**
     * Ajoute un fichier de log à un autre.
     * @param log
     */
    public void add(LogFile log) {
        for (int i = 0; i < log.logs.size(); i++) {
            addLog(log.logs.get(i));
        }
    }

    /**
     * Lit le fichier de log spécifié
     */
    public void load(String rep, Profil profil) throws UnsupportedEncodingException, FileNotFoundException, IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rep + "/logs/" + profil.nom + ".log"), "UTF-8"));

        // Passe la ligne d'entête.
        String line = reader.readLine();

        line = reader.readLine();
        while (line != null) {
            Log l = new Log();
            l.load(line);
            logs.add(l);

            line = reader.readLine();
        }
        reader.close();
    }

    /**
     * Charge les fichiers de log d'un répertoire de travail.
     */
    public void load(String rep) throws ClassNotFoundException, JDOMException, IOException, Exception {
        Parametres parametres = new Parametres();
        parametres.loadProfils(rep + "/profils.xml");

        for (int i = 0; i < parametres.profils.profils.size(); i++) {
            load(rep, parametres.profils.profils.get(i));
        }
    }

    /**
     * Ecrit le fichier de log d'un profil.
     */
    public void write(String rep, Profil profil) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String filePath = rep + "/logs/" + profil.nom + ".log";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));

        // Ecrit l'entête du fichier
        writer.write(Log.getHeader());

        // puis chaque ligne.
        for (int i = 0; i < logs.size(); i++) {
            writer.write(logs.get(i).toString());
            writer.write("\r\n");
        }

        writer.close();
    }

    /**
     * Teste l'ajout de log.
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Teste l'ajout de logs");

        LogFile lf = new LogFile();
        Log l = new Log();
        Random r = new Random(Calendar.getInstance().getTimeInMillis());

        int size = 1000;
        int max = 1000;

        for (int i = 0; i < size; i++) {
            l.starttime = r.nextInt(max);
            lf.addLog(l);
            l = new Log();
        }
        int erreurs = 0;
        for (int i = 0; i < lf.logs.size() - 1; i++) {
            if (lf.logs.get(i).starttime > lf.logs.get(i + 1).starttime) {
                erreurs++;
            }
        }
        if (erreurs > 0) {
            System.out.println(erreurs + " erreur(s) rencontrées.");
        } else {
            System.out.println("Aucune erreur");
        }
    }
}
