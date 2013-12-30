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
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 * Interface en ligne de commande de JDONREFv2Charge
 * @author jmoquet
 */
public class Charge {

    /**
     * Point d'entrée de l'interface.
     * @param args La liste des paramètres
     * <ul>
     * <li>echantillon rep quantite</li>
     * <li>simulation rep duree</li>
     * <li>analyse rep1 rep2 ... [-p arg1 arg2 ...]</li>
     * <li>fusion rep1 rep2 ... </li>
     * </ul>
     */
    public static void main(String[] args) {
        boolean erreur = false;

        if (args.length > 0) {
            if (args[0].compareTo("fusion") == 0) {
                if (args.length >= 3) {
                    try {
                        Fusion f = new Fusion();

                        String[] rep = new String[args.length - 1];
                        for (int i = 1; i < args.length; i++) {
                            rep[i - 1] = args[i];
                        }

                        f.doit(rep);
                    } catch (JDOMException ex) {
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    erreur = true;
                }
            } else if (args[0].compareTo("echantillon") == 0) {
                if (args.length == 3) {
                    String rep = args[1];
                    int quantite = 0;
                    try {
                        quantite = Integer.parseInt(args[2]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Le paramètre quantite est invalide.");
                        erreur = true;
                    }
                    if (!erreur) {
                        try {
                            Echantillonneur e = new Echantillonneur();
                            e.load(rep);
                            e.doall(rep, quantite);
                        } catch (UnsupportedEncodingException ex) {
                            erreur = true;
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FileNotFoundException ex) {
                            erreur = true;
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (JDOMException ex) {
                            erreur = true;
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            erreur = true;
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            erreur = true;
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            erreur = true;
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    erreur = true;
                }
            } else if (args[0].compareTo("simulation") == 0) {
                String rep = args[1];
                int duree = 0;
                try {
                    duree = Integer.parseInt(args[2]);
                } catch (NumberFormatException nfe) {
                    System.out.println("Le paramètre duree est invalide.");
                    erreur = true;
                }
                if (!erreur) {
                    try {
                        Simulateur s = new Simulateur();
                        s.load(rep);
                        s.doit(rep, duree);
                    } catch (FileNotFoundException ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (args[0].compareTo("analyse") == 0) {
                final String file = args[1]; // HM
                
                int optionindex = args.length;
                for (int i = 2; i < args.length; i++) {
                    if (args[i].compareTo("-p") == 0) {
                        optionindex = i;
                        break;
                    }
                }
                String[] reps = new String[optionindex - 2];
                for (int i = 2; i < optionindex; i++) {
                    reps[i - 2] = args[i];
                }

                String[] options;
                if (optionindex != args.length) {
                    options = new String[args.length - optionindex - 2];
                    for (int i = optionindex + 2; i < args.length; i++) {
                        options[i - optionindex - 2] = args[i];
                    }
                } else {
                    options = new String[0];
                }

                if (!erreur) {
                    try {
                        Analyseur a = new Analyseur();
                        a.load(reps);
                        a.analyse(options);
                        a.write(file);
                        System.out.println(a.toString());
                    } catch (ClassNotFoundException ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JDOMException ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        erreur = true;
                        Logger.getLogger(Charge.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                erreur = true;
            }
        } else {
            erreur = true;
        }

        if (erreur) {
            System.out.println("Permet de simuler l'utilisation de JDONREF v3");
            System.out.println();
            System.out.println("echantillon repertoire quantite");
            System.out.println("  Crée des échantillons de taille indiquée pour le répertoire de travail donné");
            System.out.println("simulation repertoire duree");
            System.out.println("  Lance la simulation du répertoire de travail indiqué sur la duree en millisecondes donnée");
            System.out.println("analyse rep1 rep2 ... [-p arg1 arg2 ...]");
            System.out.println("  Analyse les résultats des simulations effectuées dans les répertoires indiqués avec les options spécifiées.");
            System.out.println("fusion rep1 rep2 ...");
            System.out.println(" Fusionne les logs des répertoires de travail dans le premier répertoire.");
        }
    }
}
