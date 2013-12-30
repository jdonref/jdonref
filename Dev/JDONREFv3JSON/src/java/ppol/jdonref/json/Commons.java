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
package ppol.jdonref.json;

import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import ppol.jdonref.json.client.JDONREFService;
import ppol.jdonref.json.client.ResultatErreur;

/**
 * Ensemble de fonctionnalités communes
 * @author jmoquet,hmarcantoni
 */
public class Commons {

    static final int CLIENTUTF8 = 1;
    static final int REQUETEUTF8 = 2;
    static final Charset encodageUTF8 = Charset.forName("UTF-8");
    static final Charset encodageutf8 = Charset.forName("utf-8");

    /**
     * Ecrit un message d'erreur sur la sortie standard, au format JSON.
     * @param out
     */
    public static void errorMessage(PrintWriter out, int message) {
        String texte = null;
        switch (message) {
            default:
                texte = "{\"etat\":0,\"codeerreur\":7,\"message\":\"Erreur inconnue.\"}";
            case CLIENTUTF8:
                texte = "{\"etat\":0,\"codeerreur\":6,\"message\":\"L'encodage UTF-8 n'est pas supporté par le client.\"}";
                break;
            case REQUETEUTF8:
                texte = "{\"etat\":0,\"codeerreur\":6,\"message\":\"Seul l'encodage UTF-8 est supporté pour les requêtes.\"}";
                break;
        }
        out.print(texte);
    }

    public static void printJSONResponse(PrintWriter out, Object obj) {
        final Gson gson = new Gson();
        out.print(gson.toJson(obj));
        out.close();
    }

    public static ResultatErreur getResultatErreur(int code, String message) {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(code);
        erreurRet.setMessage(message);

        return erreurRet;
    }

    public static ResultatErreur getErreurServiceIntrouvable() {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(4);
        erreurRet.setMessage("Le service web n'a pas été trouvé.");

        return erreurRet;
    }

    public static ResultatErreur getErreurServiceSansReponse() {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(4);
        erreurRet.setMessage("Le service web ne répond pas.");

        return erreurRet;
    }

    public static ResultatErreur getErreurRequeteUTF8() {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(6);
        erreurRet.setMessage("Seul l'encodage UTF-8 est supporté pour les requêtes.");

        return erreurRet;
    }

    public static ResultatErreur getErreurClientUTF8() {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(6);
        erreurRet.setMessage("L'encodage UTF-8 n'est pas supporté par le client.");

        return erreurRet;
    }

    public static ResultatErreur getErreurInconnue() {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(7);
        erreurRet.setMessage("Erreur inconnue.");

        return erreurRet;
    }

    public static ResultatErreur getErreurParametre(String name) {
        final ResultatErreur erreurRet = new ResultatErreur();
        erreurRet.setCode(5);
        erreurRet.setMessage("Le paramètre " + name + " a été mal formaté.");

        return erreurRet;
    }

    /**
     * Obtient l'objet permettant de dialoguer avec le web service.
     * 
     * Les exceptions sont toutes capturées. En cas de problème, les logs
     * sont complétés et la valeur null est retournée.
     * 
     * @param JDONREFwsdl
     * @param uri
     * @param service
     * @return
     */
    public static JDONREFService getJDONREFService(String JDONREFwsdl, String uri, String service) {
        if (JDONREFwsdl != null) {
            URL url = null;
            try {
                url = new URL(JDONREFwsdl);
            } catch (MalformedURLException ex) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + JDONREFwsdl + " spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", ex);
            }

            try {
                QName qname = new QName(uri, service);

                return new JDONREFService(url, qname);

            } catch (Exception e) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + JDONREFwsdl + " spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", e);
                return null;
            }
        } else {
            try {
                return new JDONREFService();
            } catch (Exception e) {
                Logger.getLogger("Commons").log(Level.SEVERE, "L'url " + JDONREFwsdl + " spécifiée pour JDONREF dans le fichier de configuration est incorrecte.", e);
                return null;
            }
        }
    }

    /**
     * Vérifie l'encodage de la requête.
     * @param out
     * @param encodageRequete
     * @return
     */
    public static boolean verifieEncodageRequete(PrintWriter out, String encodageRequete) {
        boolean encodageNonSupporte = false;
        try {
            if (encodageRequete != null) {
                Charset csEncodageRequete = Charset.forName(encodageRequete);

                if (csEncodageRequete.compareTo(encodageUTF8) != 0 && csEncodageRequete.compareTo(encodageutf8) != 0) {
                    encodageNonSupporte = true;
                }
            } else {
                encodageNonSupporte = true;
            }
        } catch (IllegalCharsetNameException icnex) {
            // L'encodage de la requête n'a pas été trouvé.
            encodageNonSupporte = true;
        }
        if (encodageNonSupporte) {
            return false;
        }
        return true;
    }

    /**
     * Logue l'exception spécifiée avec ses informations de log.
     */
    public static void log(String classname, Level level, Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMessage());

        StackTraceElement[] trace = ex.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            sb.append("\r\n");
            sb.append(trace[i].toString());
        }

        Logger.getLogger(classname).log(level, sb.toString());
    }

    /**
     * Vérifie que le client supporte bien l'UTF-8.
     * 
     * Si le client ne précise rien, on suppose que oui.
     * @return
     */
    public static boolean verifieEncodageClient(String encodage) {
        if (encodage != null && encodage.trim().length() > 0) {
            String[] encodages = encodage.split(",");

            for (int i = 0; i < encodages.length; i++) {
                String strEncodage = encodages[i];
                String encodageTitle = null;

                int index = strEncodage.indexOf(";");
                if (index >= 0) {
                    encodageTitle = strEncodage.substring(0, index);
                } else {
                    encodageTitle = strEncodage;
                }
                try {
                    Charset csEncodage = Charset.forName(encodageTitle);
                    if (csEncodage.contains(encodageUTF8) || csEncodage.contains(encodageutf8)) //                    if (csEncodage.compareTo(encodageUTF8)==0)
                    {
                        return true;
                    }
                } catch (IllegalCharsetNameException icnex) {
                // Ignoré.
                }
            }
            return false;
        }
        return true; // it don't, so just try it !
    }
}
