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

import java.io.IOException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Gère les profils est les modèles.
 * @author jmoquet
 */
public class Parametres {

    /**
     * Les profils d'accès à JDONREF.
     */
    public Profils profils = new Profils();
    /**
     * Les modèles d'adresse utilisés.
     */
    public Modeles modeles = new Modeles();
    /**
     * Les paramètres d'accès au service web.
     */
    public JDONREFParams params = new JDONREFParams();
    /**
     * Les paramètres de connection au référentiel de JDONREF.
     */
    public ConnectionStruct connectionStruct = new ConnectionStruct();

    /**
     * Constructeur par défaut.
     */
    public Parametres() throws ClassNotFoundException {
    }

    /**
     * Charge les paramètres de connexion à JDONREF.
     * @param jdonrefFile
     */
    public void loadJDONREF(String jdonrefFile) throws JDOMException, IOException, Exception {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(jdonrefFile);
        Element e_jdonref = d.getRootElement();
        params.load(e_jdonref);
    }

    /**
     * Obtient les modeles à partir d'une réprésentation XML.
     */
    public void loadModeles(String modelesFile) throws JDOMException, IOException, Exception {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(modelesFile);
        Element e_modeles = d.getRootElement();
        modeles.load(e_modeles);
    }

    /**
     * Obtient les scenarios à partir d'une réprésentation XML.
     */
    public void loadScenarios(String scenariosFile) throws JDOMException, IOException, Exception {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(scenariosFile);
        Element e_scenarios = d.getRootElement();
        Scenarios.getInstance().load(e_scenarios);
    }

    /**
     * Obtient les profils à partir d'une réprésentation XML.
     */
    public void loadProfils(String profilsFile) throws JDOMException, IOException, Exception {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(profilsFile);
        Element e_profils = d.getRootElement();
        profils.load(e_profils);
    }

    /**
     * Obtient les paramètres de connection à la base de jdonref à partir d'une réprésentation XML.
     */
    public void loadConnection(String connectionsFile) throws JDOMException, IOException, Exception {
        SAXBuilder sb = new SAXBuilder();
        Document d = sb.build(connectionsFile);
        Element e_connections = d.getRootElement();
        connectionStruct.load(e_connections);
    }
}
