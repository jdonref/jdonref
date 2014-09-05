/**
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
package ppol.jdonref.referentiel;

//import ppol.jdonref.*;
import ppol.jdonref.utils.MiscUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ppol.jdonref.AGestionLogs;
import ppol.jdonref.GestionConnection;
//import ppol.jdonref.GestionLogs;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.RefConnection;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;

/**
 * Service web JDONREF v2.1.<br>
 * Cette classe est utilisée pour générer le proxy de l'interface SOAP de JDONREF v2.<br>
 * En cas d'erreur, le code de retour utilise la codification suivante:
 * <ul>
 * <li>Code 1 = GestionMots non initialisé.</li>
 * <li>Code 2 = GestionConnection non initialisé.</li>
 * <li>Code 3 = Problème SQL durant la normalisation.</li>
 * <li>Code 4 = Le service web ne répond pas.</li>
 * <li>Code 5 = Un paramètre a été mal formaté.</li>
 * <li>Code 6 = Problème d'encodage (requete ou client).</li>
 * <li>Code 7 = Erreur inconnue</li>
 * <li>Code 8 = L'adresse fournie n'est pas correcte.</li>
 * <li>Code 9 = Pas encore implémenté</li>
 * <li>Code 10 = Erreur dans le référentiel</li>
 * <li>Code 11 = Erreur liée aux paramètres et au référentiel.</li>
 * <li>Code 12 = Le processus est déjà en cour d'exécution.</li>
 * </ul>
 * @author jmoquet
 */
public class GestionAdr {

    private final GestionConnection gestionConnection;
    private final GestionMots gestionMots;
    private final GestionReferentiel gestionReferentiel;
    private final GestionInverse gestionInverse;
    protected JDONREFParams params;
    

    public GestionAdr(
            GestionConnection gestionConnection,
            GestionMots gestionMots,
            GestionReferentiel gestionReferentiel,
            GestionInverse gestionInverse) {
        this.gestionConnection = gestionConnection;
        this.gestionMots = gestionMots;
        this.gestionReferentiel = gestionReferentiel;
        this.gestionInverse = gestionInverse;

    }
    
    
    /**
     * Définit les paramètres de JDONREF utilisés par cette classe.
     * @param params
     */
    public void definitJDONREFParams(JDONREFParams params) {
        this.params = params;
    }

    /**
     * Obtient les paramètres de JDONREF utilisés par cette classe.
     * @param params
     */
    public JDONREFParams obtientJDONREFParams() {
        return params;
    }    

    /**
     * Revalide une adresse validée au préalable.
     */
    public List<String[]> revalide(
            int application,
            int[] services,
            String[] lignes,
            String dateValidation,
            String dateRevalidation) {

        final List<String[]> listRet = new ArrayList<String[]>();
        //TODO write your implementation code here:
        if (gestionConnection == null) {
//            GestionLogs.getInstance().logRevalidation(application, false);
            params.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "1", "GestionConnection non initialisé."});
            return listRet;
        }
        if (dateValidation == null) {
//            GestionLogs.getInstance().logRevalidation(application, false);
            params.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "5", "La date de validation n'a pas été mentionnée."});
            return listRet;
        }
        RefConnection refconnection = null;

        try {
            refconnection = gestionConnection.obtientConnection();
        } catch (SQLException ex) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connexion n'a pas pu être effectuée.", ex);
//            GestionLogs.getInstance().logRevalidation(application, false);
              params.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant la revalidation."});
            return listRet;
        } catch (JDONREFException je) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "Ne devrait pas se produire.", je);
//            GestionLogs.getInstance().logRevalidation(application, false);
            params.getGestionLog().logRevalidation(application, false);
        } catch (Exception ex) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connexion n'a pas pu être effectuée.", ex);
//            GestionLogs.getInstance().logRevalidation(application, false);
            params.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "7", "Erreur non répertoriée durant la revalidation."});
            return listRet;
        }

        if (refconnection == null || refconnection.connection == null) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                    "La connection n'a pas pu être effectuée.");

//            GestionLogs.getInstance().logRevalidation(application, false);
            params.getGestionLog().logRevalidation(application, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant la revalidation."});
            return listRet;
        }

        try {

            // les logs sont effectués dans cette méthode.
            listRet.addAll(gestionReferentiel.revalide(application, services, lignes, dateValidation, dateRevalidation, refconnection.connection));

            return listRet;
        } catch (SQLException ex) {
            StringBuilder sb = new StringBuilder();

            sb.append("Problème SQL durant la requête à revalide avec les paramètres:\r\n");
            for (int i = 0; i < lignes.length; i++) {
                sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
            }
            sb.append("datevalidation : " + dateValidation.toString() + "\r\n");
            sb.append("daterevalidation : " + dateRevalidation.toString() + "\r\n");

            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);

//            GestionLogs.getInstance().logRevalidation(application, false);
            params.getGestionLog().logRevalidation(application, false);

            listRet.add(new String[]{"0", "3", "Problème SQL durant la validation."});
            return listRet;
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder();

            sb.append("La revalidation n'a pas pu être effectuée:\r\n");
            for (int i = 0; i < lignes.length; i++) {
                sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
            }
            sb.append("datevalidation : " + dateValidation.toString() + "\r\n");
            sb.append("daterevalidation : " + dateRevalidation.toString() + "\r\n");

            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
            listRet.add(new String[]{"0", "7", "Erreur non répertoriée durant la revalidation."});
            return listRet;
        } finally {
            if (refconnection != null) {
                gestionConnection.relache(refconnection);
            }
        }
    }

    /**
     * Géocode l'adresse valide spécifiée.
     * @return voir GestionReferentiel.geocode et GestionValidation.
     */
    public List<String[]> geocode(
            int application,
            int[] services,
            String voi_id,
            String ligne4,
            String code_insee,
            String pays_id,
            String date,
            double distance,
            int projection) {

        final List<String[]> listRet = new ArrayList<String[]>();
        if (gestionConnection == null) {
//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "1", "GestionConnection non initialisé."});
        }
        if (date == null) {
//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "5", "La date n'a pas été mentionnée."});
        }
        RefConnection refconnection = null;

        try {
            refconnection = gestionConnection.obtientConnection();
        } catch (SQLException ex) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.", ex);

//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant le géocodage."});
        } catch (JDONREFException je) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "Ne devrait pas se produire.", je);
//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "7", "Problème indéterminé durant le géocodage."});
        } catch (Exception ex) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.", ex);
//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "7", "Problème indéterminé durant le géocodage."});
        }

        if (refconnection == null || refconnection.connection == null) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.");

//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant la revalidation."});
        }

        try {
            if (voi_id != null && voi_id.length() == 0) {
                voi_id = null;
            } // Nécessaire car le passage de null par le web service n'est pas pratique.
            if (code_insee != null && code_insee.length() == 0) {
                code_insee = null;
            } // Nécessaire car le passage de null par le web service n'est pas pratique.
            // les logs sont effectués dans cette méthode.

            
            listRet.addAll(gestionReferentiel.geocode(application, services, voi_id, ligne4, code_insee, pays_id, date, distance, projection, refconnection.connection));
        } catch (SQLException sqle) {
            StringBuilder sb = new StringBuilder();

            sb.append("Problème SQL durant la requête à géocode avec les paramètres:\r\n");
            sb.append("voi_id : " + voi_id + "\r\n");
            sb.append("ligne4 : " + ligne4 + "\r\n");
            sb.append("code_insee : " + code_insee + "\r\n");
            sb.append("date : " + date + "\r\n");

            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), sqle);
//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant le géocodage."});
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();

            sb.append("Exception non répertoriée durant le géocodage:\r\n");
            sb.append("voi_id : " + voi_id + "\r\n");
            sb.append("ligne4 : " + ligne4 + "\r\n");
            sb.append("code_insee : " + code_insee + "\r\n");
            sb.append("date : " + date + "\r\n");

            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), e);
//            GestionLogs.getInstance().logGeocodage(application, GestionLogs.FLAG_GEOCODE_ERREUR, false);
            params.getGestionLog().logGeocodage(application, AGestionLogs.FLAG_GEOCODE_ERREUR, false);
            listRet.add(new String[]{"0", "7", "Une erreur non répertoriée est survenue."});
        } finally {
            if (refconnection != null) {
                gestionConnection.relache(refconnection);
            }
        }

        return listRet;
    }

    /**
     * Découpe l'adresse en les différents éléments qui la compose.
     * Les éléments du paramètre nature sont une combinaison des flags suivants:
     * <ul>
     * <li>1 pour numero</li>
     * <li>2 pour repetition</li>
     * <li>4 pour autres numeros</li>
     * <li>8 pour type de voie</li>
     * <li>16 pour article</li>
     * <li>32 pour libelle</li>
     * <li>64 pour code postal</li>
     * <li>128 pour commune</li>
     * <li>256 pour numero d'arrondissement</li>
     * <li>512 pour cedex</li>
     * <li>1024 pour le code cedex</li>
     * <li>2048 pour ligne1</li>
     * <li>4096 pour ligne2</li>
     * <li>8192 pour ligne3</li>
     * <li>16384 pour ligne5</li>
     * </ul>
     * @param lignes les lignes à découper.
     * @param natures la nature des informations à obtenir.
     * @param numeros les numéros des lignes à découper. Si numeros est null, une restrucuration est effectuée.
     */
    public java.lang.String[] decoupe(
            int application,
            String[] lignes,
            int[] natures,
            int[] numeros) {


        if (gestionConnection == null) {
//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            return new String[]{"0", "1", "GestionConnection non initialisé."};
        }

        RefConnection refconnection = null;

        try {
            refconnection = gestionConnection.obtientConnection();
        } catch (SQLException ex) {
//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.", ex);

            return new String[]{"0", "3", "Problème SQL durant le découpage."};
        } catch (JDONREFException je) {
//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "Ne devrait pas se produire.", je);
            return new String[]{"0", "7", "Problème indéterminé durant le découpage."};
        } catch (Exception ex) {
//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.", ex);
            return new String[]{"0", "7", "Problème indéterminé durant le découpage."};
        }

        if (refconnection == null || refconnection.connection == null) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                    "La connection n'a pas pu être effectuée.");

//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            return new String[]{"0", "3", "Problème SQL durant la revalidation."};
        }

        try {

            // les logs sont effectués dans cette méthode
            String[] res = gestionMots.decoupe(application, lignes, natures, numeros, refconnection.connection);
            return res;
        } catch (SQLException ex) {
            StringBuilder sb = new StringBuilder();

            sb.append("Problème SQL durant la requête découpe avec les paramètres:\r\n");
            for (int i = 0; i < lignes.length; i++) {
                sb.append(lignes[i]);
            }
            for (int i = 0; i < natures[i]; i++) {
                sb.append(natures[i]);
            }

//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);

            return new String[]{"0", "3", "Problème SQL durant le découpage."};
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder();

            sb.append("Le découpage n'a pas pu être effectué:\r\n");
            for (int i = 0; i < lignes.length; i++) {
                sb.append(lignes[i]);
            }
            for (int i = 0; i < natures[i]; i++) {
                sb.append(natures[i]);
            }

//            GestionLogs.getInstance().logDecoupage(application, false);
            params.getGestionLog().logDecoupage(application, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
            return new String[]{"0", "7", "Problème indéterminé durant le découpage."};
        } finally {
            if (refconnection != null) {
                gestionConnection.relache(refconnection);
            }
        }
    }

    /**
     * Effectue les opérations de normalisation demandées.
     * @param operation une combinaison de bits :
     * <ul>
     *     <li>1 pour la première passe de normalisation</li>
     *     <li>2 pour la restructuration</li>
     *     <li>4 pour la deuxième passe de normalisation</li>
     *     <li>8 pour activer la réduction à 38 caractère durant la deuxième passe de normalisation</li>
     *     <li>16 pour retourner l'équivalent phonétique de chaque ligne</li>
     *     <li>32 pour activer la désabbréviation durant la deuxième passe de normalisation</li>
     *     <li>64 pour retourne l'équivalent sans articles</li>
     *     <li>128 pour que la restructuration retourne les numéros de départements présumés de l'adresse (dernière ligne, séparés par des virgules)</li>
     * </ul>
     * @param departements la liste des départements dans laquelle effectuer la deuxième passe de normalisation (séparés par des , sans espaces)
     * Ce paramètre peut être défini à null si le code de département est spécifié dans la ligne 6 ou si la restructuration est
     * effectuée avant. Sinon, les départements par défaut seront choisis.
     *
     * Attention, une deuxième passe de normalisation sur une adresse non normalisée (passe 1) et non structurée
     * peut donner des résultats innatendus.
     */
    public String[] normalise(
            int application,
            int operation,
            String[] lignes,
            String str_departements) {


        String[] adresse = null;
        RefConnection refconnection = null;


        // Vérifications préalables.
        if ((operation & (1 + 2 + 4)) != 0) {
            if (gestionMots == null) {
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                return new String[]{"0", "1", "GestionMots non initialisé"};
            }
        }
        if ((operation & 2) != 0) {
            if (gestionConnection == null) {
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                return new String[]{"0", "1", "GestionConnection non initialisé"};
            }
        }

        // Initialisations
        if ((operation & (1 + 2 + 4 + 16 + 64)) != 0) {
            adresse = lignes.clone();
        }
        if ((operation & (2 + 4)) != 0) {
            try {
                refconnection = gestionConnection.obtientConnection();
            } catch (SQLException ex) {
                StringBuilder sb = new StringBuilder();

                sb.append("La connection n'a pas pu être effectuée:\r\n");
                for (int i = 0; i < lignes.length; i++) {
                    sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                }
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);

                return new String[]{
                    "0", "3", "Problème SQL durant la normalisation."
                };
            } catch (JDONREFException je) {
                StringBuilder sb = new StringBuilder();

                sb.append("Ne devrait pas se produire:\r\n");
                for (int i = 0; i < lignes.length; i++) {
                    sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                }
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), je);
                return new String[]{"0", "7", "Problème indéterminé durant la normalisation."};
            } catch (Exception ex) {
                StringBuilder sb = new StringBuilder();

                sb.append("La connection n'a pas pu être effectuée:\r\n");
                for (int i = 0; i < lignes.length; i++) {
                    sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                }
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
                return new String[]{"0", "7", "Problème indéterminé durant la normalisation."};
            }

            if (refconnection == null || refconnection.connection == null) {
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                        "La connection n'a pas pu être effectuée.");

                return new String[]{
                    "0", "3", "Problème SQL durant la normalisation."
                };
            }
        }

        try {

            // Première passe de normalisation
            if ((operation & 1) != 0) {
                try {
                    adresse = gestionMots.normalise_1(adresse);
//                    GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_1, true);
                    params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_1, true);
                } catch (Exception e) {
//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                            "La normalisation 1 n'a pas pu être effectuée.");

                    return new String[]{
                        "0", "3", "Problème SQL durant la normalisation 1."
                    };
                }
            }

            boolean retourne_departements_presumes = false;
            if ((operation & 128) != 0 || (operation & 4) != 0) {
                retourne_departements_presumes = true;
            }

            // WA 01/2012 Pays code 256 ie gerer le pays dans les divers traitements.
            boolean gestionPays = false;
            if ((operation & 256) != 0) {
                gestionPays = true;
            }

            // Restructuration
            if ((operation & 2) != 0) {
                try {
                    adresse = gestionMots.restructure(adresse, retourne_departements_presumes, gestionPays, refconnection.connection);

//                    if (retourne_departements_presumes)
//                    {
//                        str_departements = adresse[6];
//                        String[] tmp_adresse = new String[6];
//                        for(int i=0;i<6;i++) tmp_adresse[i] = adresse[i];
//                          adresse = tmp_adresse;
//                    }
                    // WA 01/2012 Pays
                    if (retourne_departements_presumes) {
                        if (gestionPays) {
                            str_departements = adresse[7];
                            adresse = MiscUtils.extractPartOfTab(adresse, 0, 6);
                        } else {
                            str_departements = adresse[6];
                            adresse = MiscUtils.extractPartOfTab(adresse, 0, 5);
                        }
                    }

//                    GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_RESTRUCTURE, true);
                    params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_RESTRUCTURE, true);
                } catch (java.lang.StringIndexOutOfBoundsException sqle) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Erreur SQL durant la restructuration:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), sqle);

                    return new String[]{"0", "7", "Erreur durant la restructuration."};
                } catch (SQLException sqle) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Erreur SQL durant la restructuration:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), sqle);

                    return new String[]{
                        "0", "3", "Problème SQL durant la restructuration."
                    };
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("La restructuration n'a pas pu être effectuée:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
                    return new String[]{
                        "0", "7", "Problème indéterminé durant la restructuration."
                    };
                }
            }

            // Deuxième passe de normalisation
            if ((operation & 4) != 0) {
                ArrayList<String> numerosSupplementaires = new ArrayList<String>();

                try {
                    boolean abbrevie = (operation & 8) != 0;
                    boolean desabbrevie = (operation & 32) != 0;
                    adresse = gestionMots.normalise_2(adresse, numerosSupplementaires, abbrevie, desabbrevie, gestionPays,
                            str_departements, refconnection.connection);
                    if (abbrevie) {
//                        GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_2_38, true);
                        params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_2_38, true);
                    } else {
//                        GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_2, true);
                        params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_2, true);
                    }
                } catch (SQLException ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Problème SQL durant la requête à Normalise 2 avec les paramètres:\r\n");
                    for (int i = 0; i < adresse.length; i++) {
                        sb.append("Ligne " + i + ":" + adresse[i] + "\r\n");
                    }

//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);

                    return new String[]{
                        "0", "3", "Problème SQL durant la normalisation 2."
                    };
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("La normalisation 2 n'a pas pu être effectuée:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
                    return new String[]{
                        "0", "7", "Problème indéterminé durant la normalisation 2."
                    };
                }
            }

            // Phonétisation
            if ((operation & 16) != 0) {
                try {
                    adresse = gestionMots.phonetise(adresse);
//                    GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_PHONETISE, true);
                    params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_PHONETISE, true);
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("La phonétisation n'a pas pu être effectuée:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
//                    GestionLogs.getInstance().logNormalisation(application, operation, false);
                    params.getGestionLog().logNormalisation(application, operation, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
                    return new String[]{
                        "0", "7", "Problème indéterminé durant la phonétisation."
                    };
                }
            }

            // suppression des articles
            if ((operation & 64) != 0) {
                adresse = gestionMots.sansArticles(adresse);
            }

            if (adresse != null) {
                // Formatage du résultat.
                String[] fres = null;
                if (retourne_departements_presumes) {
                    fres = new String[1 + adresse.length + 1];
                    fres[fres.length - 1] = str_departements;
                } else {
                    fres = new String[1 + adresse.length];
                }
                fres[0] = "1";
                for (int i = 0; i < adresse.length; i++) {
                    fres[i + 1] = adresse[i];
                }
                return fres;
            } else {
//                GestionLogs.getInstance().logNormalisation(application, operation, false);
                params.getGestionLog().logNormalisation(application, operation, false);
                return new String[]{
                    "0", "5", "L'opération demandée ne correspond à rien"
                };
            }
        } finally {
            if ((operation & (2 + 4)) != 0) {
                gestionConnection.relache(refconnection);
            }
        }
    }

    /**
     * Valide en normalisant au préalable si nécessaire.
     * @param operation une combinaison de bits :
     * <ul><li>1 pour la première passe de normalisation</li>
     *     <li>2 pour la restructuration</li>
     *     <li>4 pour la deuxième passe de normalisation</li>
     *     <li>8 pour activer la réduction à 38 caractères durant la deuxième passe de normalisation.</li>
     *     <li>32 pour activer la désabbréviation</li>
     *     <li>64 pour supprimer les articles</li>
     *     <li>128 pour que la restructuration retourne les numéros de départements présumés de l'adresse (dernière ligne, séparés par des virgules)</li>
     * </ul>
     * @param departements la liste des départements dans laquelle effectuer la deuxième passe de normalisation (séparés par des , sans espaces)
     * Ce paramètre peut être défini à null si le code de département est spécifié dans la ligne 6 ou si la restructuration est
     * effectuée avant. Sinon, les départements par défaut seront choisis.
     * @return voir GestionValidation pour les valeurs de retour.
     */
    public List<String[]> valide(
            int application,
            int[] services,
            int operation,
            String[] lignes,
            String date,
            boolean force,
            String str_departements) {
        final List<String[]> listRet = new ArrayList<String[]>();
        String[] adresse = null;

        RefConnection refconnection = null;
        // PAYS
        boolean gestionPays = lignes.length > 6 || ((operation & 256) != 0);
        for (Integer service : services) {
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(service).getId();
            if (id == GestionReferentiel.SERVICE_PAYS) {
                gestionPays = true;
                break;
            }
        }


        // Vérifications préalables.
        if (gestionMots == null) {
            listRet.add(new String[]{"0", "1", "GestionMots non initialisé"});
            return listRet;
        }
        if (gestionConnection == null) {
            listRet.add(new String[]{"0", "1", "GestionConnection non initialisé"});
            return listRet;
        }


        if (gestionReferentiel == null) {
            listRet.add(new String[]{"0", "1", "GestionReferentiel non initialisé"});
            return listRet;
        }

        // Initialisations
        adresse = lignes.clone();

        try {
            refconnection = gestionConnection.obtientConnection();
        } catch (SQLException ex) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                    "La connection n'a pas pu être effectuée.", ex);
//            GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
            params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant la validation."});
            return listRet;
        } catch (JDONREFException je) {
//            GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
            params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "Ne devrait pas se produire.", je);
            listRet.add(new String[]{"0", "7", "Problème indéterminé durant la connection."});
            return listRet;
        } catch (Exception ex) {
//            GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
            params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.", ex);
            listRet.add(new String[]{"0", "7", "Problème indéterminé durant la connection."});
            return listRet;
        }

        if (refconnection == null || refconnection.connection == null) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.");
//            GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
            params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
            listRet.add(new String[]{"0", "3", "Problème SQL durant la validation."});
            return listRet;
        }

        try {
            // Première passe de normalisation
            if ((operation & 1) != 0) {
                try {
                    adresse = gestionMots.normalise_1(adresse);
//                    GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_1, true);
                    params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_1, true);
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("La normalisation 1 n'a pas pu être effectuée:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
//                    GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                    params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
                    listRet.add(new String[]{"0", "7", "Problème indéterminé durant la normalisation 1."});
                    return listRet;
                }
            }
            // Restructuration
            if ((operation & 2) != 0) {
                try {
                    boolean retourne_departements_presumes = false;
                    if ((operation & 128) != 0 || (operation & 4) != 0) {
                        retourne_departements_presumes = true;
                    }

                    adresse = gestionMots.restructure(adresse, retourne_departements_presumes, gestionPays, refconnection.connection);

                    if (retourne_departements_presumes) {
                        str_departements = adresse[6];
                        // Les départements présumés sont ensuite supprimés
                        String[] tmp_adresse = new String[6];
                        for (int i = 0; i < 6; i++) {
                            tmp_adresse[i] = adresse[i];
                        }
                        adresse = tmp_adresse;
                    }
//                    GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_RESTRUCTURE, true);
                    params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_RESTRUCTURE, true);
                } catch (java.lang.StringIndexOutOfBoundsException sqle) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Erreur SQL durant la restructuration:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), sqle);
//                    GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                    params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);

                    listRet.add(new String[]{"0", "7", "Erreur durant la restructuration."});
                    return listRet;
                } catch (SQLException sqle) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Erreur SQL durant la restructuration:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), sqle);
//                    GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                    params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);

                    listRet.add(new String[]{"0", "3", "Problème SQL durant la restructuration."});
                    return listRet;
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("La restructuration n'a pas pu être effectuée:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
//                    GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                    params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                    listRet.add(new String[]{"0", "7", "Problème indéterminé durant la restructuration."});
                    return listRet;
                }
            }
            // Deuxième passe de normalisation
            if ((operation & 4) != 0) {
                ArrayList<String> numerosSupplementaires = new ArrayList<String>();

                try {
                    boolean desabbrevie = (operation & 32) != 0;
                    boolean abbrevie = (operation & 8) != 0;
                    adresse = gestionMots.normalise_2(adresse, numerosSupplementaires, abbrevie, desabbrevie, gestionPays,
                            str_departements, refconnection.connection);

                    if (!abbrevie) {
//                        GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_2, true);
                        params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_2, true);
                    } else {
//                        GestionLogs.getInstance().logNormalisation(application, GestionLogs.FLAG_NORMALISE_2_38, true);
                        params.getGestionLog().logNormalisation(application, AGestionLogs.FLAG_NORMALISE_2_38, true);
                    }
                } catch (SQLException ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Problème SQL durant la requête à Normalise 2 avec les paramètres:\r\n");
                    for (int i = 0; i < adresse.length; i++) {
                        sb.append("Ligne " + i + ":" + adresse[i] + "\r\n");
                    }

                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
//                    GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                    params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);

                    listRet.add(new String[]{"0", "3", "Problème SQL durant la normalisation 2."});
                    return listRet;
                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("La normalisation 2 n'a pas pu être effectuée:\r\n");
                    for (int i = 0; i < lignes.length; i++) {
                        sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                    }
                    Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
//                    GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                    params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                    listRet.add(new String[]{"0", "7", "Problème indéterminé durant la normalisation 2."});
                    return listRet;
                }
            }

            // suppression des articles
            if ((operation & 64) != 0) {
                adresse = gestionMots.sansArticles(adresse);
            }

            // et enfin effectue la validation.
            try {
                // La gestion du pays est activee si operation&256 ou si adresse comporte 7 lignes
                //gestionPays = gestionPays || (lignes.length == 7);

                // Les logs sont effectués dans cette méthode.
                listRet.addAll(gestionReferentiel.valide(application, services, adresse, date, force, gestionPays, refconnection.connection));

                return listRet;
            } catch (SQLException ex) {
                StringBuilder sb = new StringBuilder();

                sb.append("Problème SQL durant la requête à valider avec les paramètres:\r\n");
                for (int i = 0; i < lignes.length; i++) {
                    sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                }
                Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
//                GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);

                listRet.add(new String[]{"0", "3", "Problème SQL durant la validation."});
                return listRet;
            } catch (Exception ex) {
                StringBuilder sb = new StringBuilder();

                sb.append("La validation n'a pas pu être effectuée:\r\n");
                for (int i = 0; i < lignes.length; i++) {
                    sb.append("Ligne" + i + " : " + lignes[i] + "\r\n");
                }
                Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), ex);
//                GestionLogs.getInstance().logValidation(application, null, GestionLogs.FLAG_VALIDE_ERREUR, false);
                params.getGestionLog().logValidation(application, null, AGestionLogs.FLAG_VALIDE_ERREUR, false);
                listRet.add(new String[]{"0", "7", "Problème indéterminé durant la validation."});
                return listRet;
            }
        } finally {
            if (refconnection != null) {
                gestionConnection.relache(refconnection);
            }
        }
    }

    /**
     * Effectue un géocodage inverse des coordonnées spécifiées.
     */
    public java.lang.String[] inverse(
            int application,
            int[] services,
            int operation,
            String[] position,
            String distance,
            String date,
            int projection,
            String[] options) {

        RefConnection refconnection = null;

        // Vérifications préalables.
        if (gestionConnection == null) {
            return new String[]{"0", "1", "GestionConnection non initialisé"};
        }

        if (gestionInverse == null) {
            return new String[]{"0", "1", "GestionReferentiel non initialisé"};
        }

        try {
            refconnection = gestionConnection.obtientConnection();
        } catch (SQLException ex) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                    "La connection n'a pas pu être effectuée.", ex);
//            GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
            params.getGestionLog().logInverse(application, AGestionLogs.FLAG_INVERSE_ERREUR, false);

            return new String[]{"0", "3", "Problème SQL durant le reverse geocoding."};
        } catch (JDONREFException je) {
//            GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
            params.getGestionLog().logInverse(application, AGestionLogs.FLAG_INVERSE_ERREUR, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "Ne devrait pas se produire.", je);
            return new String[]{"0", "7", "Problème indéterminé durant la connection."};
        } catch (Exception ex) {
//            GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
            params.getGestionLog().logInverse(application, AGestionLogs.FLAG_INVERSE_ERREUR, false);
            Logger.getLogger("JDONREFv2").log(Level.SEVERE, "La connection n'a pas pu être effectuée.", ex);
            return new String[]{"0", "7", "Problème indéterminé durant la connection."};
        }

        if (refconnection == null || refconnection.connection == null) {
            Logger.getLogger("JDONREFv2").log(Level.SEVERE,
                    "La connection n'a pas pu être effectuée.");

//            GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
            params.getGestionLog().logInverse(application, AGestionLogs.FLAG_INVERSE_ERREUR, false);
            return new String[]{
                "0", "3", "Problème SQL durant le reverse geocoding."
            };
        }

        String[] res = null;

        try {
            // les logs sont effectués dans cette méthode.
            res = gestionInverse.inverse(application, services, operation, position, distance, date, projection, options, refconnection.connection);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();

            sb.append("Exception non répertoriée durant le reverse geocoding:\r\n");
            sb.append("operation : " + operation + "\r\n");
            sb.append("position : {");
            if (position != null) {
                for (int i = 0; i < position.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(position[i]);
                }
            }
            sb.append("}\r\n");
            sb.append("distance : " + distance + "\r\n");
            sb.append("date : " + date + "\r\n");
            if (options != null) {
                for (int i = 0; i < options.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(options[i]);
                }
            }

            Logger.getLogger("JDONREFv2").log(Level.SEVERE, sb.toString(), e);
//            GestionLogs.getInstance().logInverse(application, GestionLogs.FLAG_INVERSE_ERREUR, false);
            params.getGestionLog().logInverse(application, AGestionLogs.FLAG_INVERSE_ERREUR, false);
            return new String[]{"0", "7", "Une erreur non répertoriée est survenue."};
        } finally {
            if (refconnection != null) {
                gestionConnection.relache(refconnection);
            }
        }

        return res;
    }
}
