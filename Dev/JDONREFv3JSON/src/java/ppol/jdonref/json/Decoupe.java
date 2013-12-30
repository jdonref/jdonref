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

import java.io.*;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import ppol.jdonref.json.client.JDONREF;
import ppol.jdonref.json.client.JDONREFService;
import ppol.jdonref.json.client.ResultatDecoupage;
import ppol.jdonref.json.client.ResultatErreur;

/**
 * Seul l'UTF8 est reconnu.
 * @author jmoquet,hmarcantoni
 */
public class Decoupe extends HttpServlet {

    private static final long serialVersionUID = 1L;
    JDONREFService service = null;
    final static DateFormat dtformat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
    ServletConfig config = null;

    /**
     * Initialise la servlet.<br>
     * Le service web spécifié dans le paramètre JDONREFwsdl est utilisé.<br>
     * S'il n'est pas trouvé, la valeur par défaut est utilisée (en dur).
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        String JDONREFwsdl = config.getServletContext().getInitParameter("JDONREFwsdl");
        String JDONREFuri = config.getServletContext().getInitParameter("JDONREFuri");
        String JDONREFservice = config.getServletContext().getInitParameter("JDONREFservice");

        service = Commons.getJDONREFService(JDONREFwsdl, JDONREFuri, JDONREFservice);
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.<br>
     * Retourne un objet JSON de format : 
     * { etat:code, ...} où code détermine le reste de la structure.<br>
     * si code==0, il s'agit d'une erreur et la structure est:<br>
     * {etat:0,message:"message d'erreur"}<br>
     * si code==1 ou 2, il s'agit d'un choix de voies, et la structure est:<br>
     * {etat:1 ou 2, compte:nb de lignes, adresses:[{idvoie:id,nom:nom,codeinsee:codeinsee,commune:nomcommune,codepostal:codepostal,t0:t0,t1:t1,distance:distance,codefantoir:code fantoir}]}
     * si code==3 ou 4, il s'agit d'un choix de communes, et la structure est:<br>
     * {etat:3 ou 4, compte:nb de lignes, communes:[{codeinsee:codeinsee,commune:nomcommune,codepostal:codepostal,distance:distance,t0:t0,t1:t1}]}
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        final PrintWriter out = response.getWriter();
        ResultatDecoupage resultat = new ResultatDecoupage();
        resultat.setCodeRetour(0);
        resultat.getErreurs().add(Commons.getErreurInconnue());

        // L'encodage UTF-8 est supposé
        // Vérifie si le client supporte l'UTF-8.
//        if (!Commons.verifieEncodageClient(request.getHeader("Accept-Charset"))) {
//            resultat.getErreurs().add(Commons.getErreurClientUTF8());
//            Commons.printJSONResponse(out, resultat);
//            return;
//        }

        // L'encodage UTF-8 est supposé
        // Vérifie si l'encodage de la requête est faîte en UTF-8.
//        if (!Commons.verifieEncodageRequete(out, request.getCharacterEncoding())) {
//            resultat.getErreurs().add(Commons.getErreurRequeteUTF8());
//            Commons.printJSONResponse(out, resultat);
//            return;
//        }

        if (service == null) {
            resultat.getErreurs().add(Commons.getErreurServiceIntrouvable());
            Commons.printJSONResponse(out, resultat);
            return;
        }

        // TRAITEMENT DES PARAMETRES
        final String applicationParam = (request.getParameter("application") != null) ? URLDecoder.decode(request.getParameter("application").trim(), "UTF-8") : "0";
        final String[] operationsParam = (request.getParameterValues("operations") != null) ? request.getParameterValues("operations") : new String[0];
        final String[] optionsParam = (request.getParameterValues("options") != null) ? request.getParameterValues("options") : new String[0];
        final String[] donneesParam = (request.getParameterValues("donnees") != null) ? request.getParameterValues("donnees") : new String[0];
        final String[] servicesParam = (request.getParameterValues("services") != null) ? request.getParameterValues("services") : new String[0];
        final List<String> donnees = new ArrayList<String>();
        for (String donnee : donneesParam) {
            if (donnee == null) {
                donnee = "";
            }
            donnees.add(new String(donnee.getBytes("UTF-8"), "UTF-8"));
        }
        final List<String> options = new ArrayList<String>();
        for (String option : optionsParam) {
            if (option != null && option.trim().length() > 0) {
                options.add(new String(option.getBytes("UTF-8"), "UTF-8"));
            }
        }
        final List<Integer> operations = new ArrayList<Integer>();
        for (String operation : operationsParam) {
            try {
                operations.add(Integer.parseInt(new String(operation.getBytes("UTF-8"), "UTF-8")));
            } catch (NumberFormatException nfe) {
                resultat.getErreurs().add(Commons.getErreurParametre("operations"));
                Commons.printJSONResponse(out, resultat);
                return;
            }
        }
        final List<Integer> services = new ArrayList<Integer>();
        for (String serviceStr : servicesParam) {
            try {
                services.add(Integer.parseInt(new String(serviceStr.getBytes("UTF-8"), "UTF-8")));
            } catch (NumberFormatException nfe) {
                resultat.getErreurs().add(Commons.getErreurParametre("services"));
                Commons.printJSONResponse(out, resultat);
                return;
            }
        }
        int application = 0;
        try {
            application = Integer.parseInt(applicationParam);
        } catch (NumberFormatException nfe) {
            resultat.getErreurs().add(Commons.getErreurParametre("application"));
            Commons.printJSONResponse(out, resultat);
            return;
        }


        // LOGS
        if (config != null && config.getServletContext().getInitParameter("debug") != null) {
            final byte[] bytes4 = (donneesParam.length > 4) ? donneesParam[3].getBytes() : "".getBytes();
            final StringBuilder sb = new StringBuilder();
            sb.append("Décodage de la ligne 4: ");
            for (int i = 0; i < bytes4.length; i++) {
                sb.append(bytes4[i]);
                sb.append(" ");
            }
            Logger.getLogger("Normalise").log(Level.INFO, sb.toString());
        }

        // EXECUTION DU SERVICE
        try {
            final JDONREF port = service.getJDONREFPort();
            resultat = port.decoupe(application, services, operations, donnees, options);

            // TRAITEMENT DE LA REPONSE
            if (resultat == null) {
                Logger.getLogger("Decoupe").log(Level.SEVERE, "Le service web ne répond pas.");
                resultat = new ResultatDecoupage();
                resultat.setCodeRetour(0);
                resultat.getErreurs().add(Commons.getErreurServiceSansReponse());
            }
        } catch (Exception ex) {
            Commons.log("Decoupe", Level.SEVERE, ex);
            resultat.getErreurs().add(Commons.getResultatErreur(7, "Erreur durant le découpage."));
        } finally {
            Commons.printJSONResponse(out, resultat);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
