/*  Version 2.1.5 – Juin 2009
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 * Permet d'analyser les logs générés après une simulation.
 * @author jmoquet
 */
public class Analyseur {

    LogFile logs = new LogFile();
    Hashtable<String, OneLogStat> stats = new Hashtable<String, OneLogStat>();

    /**
     * Constructeur par défaut.
     */
    public Analyseur() {
        stats.put("normalisation1", new OneLogStat());
        stats.put("normalisation2", new OneLogStat());
        stats.put("validation1", new OneLogStat());
        stats.put("validation2", new OneLogStat());
        stats.put("geocodage1", new OneLogStat());
        stats.put("geocodage2", new OneLogStat());
        stats.put("reverse1", new OneLogStat());
        stats.put("reverse2", new OneLogStat());
        stats.put("total", new OneLogStat());
    }

    /**
     * Charge les logs des répertoires de travail spécifiés.
     */
    public void load(String[] reps) throws ClassNotFoundException, JDOMException, IOException, Exception {
        for (int i = 0; i < reps.length; i++) {
            logs.load(reps[i]);
        }
    }

    /**
     * Remet à zéro les statistiques.
     */
    public void reset() {
        stats.get("normalisation1").reset();
        stats.get("normalisation2").reset();
        stats.get("validation1").reset();
        stats.get("validation2").reset();
        stats.get("geocodage1").reset();
        stats.get("geocodage2").reset();
        stats.get("reverse1").reset();
        stats.get("reverse2").reset();
        stats.get("total").reset();
    }

    /**
     * Obtient une représentation textuelle des statistiques.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Durée de normalisation\r\n");
        sb.append("  " + stats.get("normalisation1").toString() + "\r\n");
        sb.append("  " + stats.get("normalisation2").toString() + "\r\n");
        sb.append("Durée de validation\r\n");
        sb.append("  " + stats.get("validation1").toString() + "\r\n");
        sb.append("  " + stats.get("validation2").toString() + "\r\n");
        sb.append("Durée de géocodage\r\n");
        sb.append("  " + stats.get("geocodage1").toString() + "\r\n");
        sb.append("  " + stats.get("geocodage2").toString() + "\r\n");
        sb.append("Durée de géocodage inverse\r\n");
        sb.append("  " + stats.get("reverse1").toString() + "\r\n");
        sb.append("  " + stats.get("reverse2").toString() + "\r\n");
        sb.append("Durée de total\r\n");
        sb.append("  " + stats.get("total").toString() + "\r\n");

        return sb.toString();
    }

    public void write(String file) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(file + ".csv"));
        final List<String[]> list = new ArrayList<String[]>();
        list.add(stats.get("normalisation1").toArray("normalisation1"));
        list.add(stats.get("normalisation2").toArray("normalisation2"));
        list.add(stats.get("validation1").toArray("validation1"));
        list.add(stats.get("validation2").toArray("validation2"));
        list.add(stats.get("geocodage1").toArray("g\u00E9ocodage1"));
        list.add(stats.get("geocodage2").toArray("g\u00E9ocodage2"));
        list.add(stats.get("reverse1").toArray("inverse1"));
        list.add(stats.get("reverse2").toArray("inverse2"));
        list.add(stats.get("total").toArray("total"));
        
        bw.write("op\u00E9ration,min,max,moy,count,count>1000");
        for (String[] anarray : list) {
            bw.newLine();
            for (String astring : anarray) {
                bw.write(astring);
                bw.write(",");
            }
        }
        bw.close();

    }

    /**
     * Analyse les logs chargés selon les paramètres demandés
     */
    public void analyse(String[] args) {
        LogValidator lv = new LogValidator();
        lv.load(args);

        for (int i = 0; i < logs.logs.size(); i++) {
            Log log = logs.logs.get(i);
            if (lv.compte(log)) {
                if (log.dureeNormalise1 != 0) {
                    stats.get("normalisation1").add(log.dureeNormalise1);
                }
                if (log.dureeNormalise2 != 0) {
                    stats.get("normalisation2").add(log.dureeNormalise2);
                }
                if (log.dureeValide1 != 0) {
                    stats.get("validation1").add(log.dureeValide1);
                }
                if (log.dureeValide2 != 0) {
                    stats.get("validation2").add(log.dureeValide2);
                }
                if (log.dureeGeocode1 != 0) {
                    stats.get("geocodage1").add(log.dureeGeocode1);
                }
                if (log.dureeGeocode2 != 0) {
                    stats.get("geocodage2").add(log.dureeGeocode2);
                }
                if (log.dureeReverse1 != 0) {
                    stats.get("reverse1").add(log.dureeReverse1);
                }
                if (log.dureeReverse2 != 0) {
                    stats.get("reverse2").add(log.dureeReverse2);
                }
                if (log.dureetotale != 0) {
                    stats.get("total").add(log.dureetotale);
                }
            }
        }
    }

    /**
     * Teste l'obtention de statistiques d'activité d'utilisateur.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Analyseur a = new Analyseur();
            System.out.println("Charge les profils et les données");
            a.load(new String[]{"test"});
            System.out.println("Analyse les données");
            a.analyse(new String[]{});
            System.out.println("Résultat:");
            System.out.println(a.toString());

            a.reset();
            a.analyse(new String[]{"ok=true"});
            System.out.println("Résultat avec ok=true:");
            System.out.println(a.toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Analyseur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(Analyseur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Analyseur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Analyseur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
