/*
 * 16/07/08
 *
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
package ppol.jdonref;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import ppol.jdonref.Tables.ColonneException;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.referentiel.GestionIdentifiants;
import ppol.jdonref.referentiel.GestionMiseAJour;
import ppol.jdonref.referentiel.GestionReferentiel;
import ppol.jdonref.referentiel.GestionReferentielException;
import ppol.jdonref.utils.DateUtils;

/**
 * Regroupe les fonctionnalités d'administration.<br>
 * Cette classe effectue essentiellement la traduction vers et depuis l'interface SOAP, incluant la gestion des 
 * exceptions.
 * Les fonctions supportées sont:
 * <ul>
 * <li>normaliseVoie</li>
 * <li>changementReferentiel</li>
 * <li>phonetise</li>
 * <li>prepareChangement</li>
 * <li>prepareMaj</li>
 * <li>genereFantoirs</li>
 * <li>calculeClesAmbiguesDansVoies</li>
 * <li>calculeClesAmbiguesDansCommunes</li>
 * <li>calculeCommunesAmbiguesDansVoies</li>
 * <li>changeId</li>
 * <li>maj</li> 
 * <li>creeTableVoie</li>
 * <li>decoupe</li>
 * <li>restructure</li>
 * <li>genereIdTroncons</li>
 * <li>decoupeNumeros</li>
 * </ul>
 * Pour ajouter une nouvelles fonctions les méthodes suivantes doivent être modifiées:
 * <ul>
 * <li>administre</li>
 * <li>checkOperation</li>
 * </ul>
 * et la méthode correspondante doit être crée.<br><br>
 * 
 * Les tâches de méta-administration sont loggées au format suivant:<br>
 * numero:... où numéro correspond à:
 * <ul><li>0 pour la méthode administre</li>
 *     <li>1 pour la méthode stop</li>
 *     <li>2 pour getState</li>
 *     <li>3 pour obtientProcessus</li>
 *     <li>4 pour free</li>
 *     <li>5 pour getList</li></ul>
 * @author jmoquet
 */
public class GestionAdministration
{
    GestionReferentiel referentiel = null;
    GestionMots mots = null;
    GestionConnection gc = null;
    GestionMiseAJour gmaj = null;
    GestionIdentifiants gi = null;
    
    NumerosProcessus np = new NumerosProcessus();
    JDONREFParams params = null;
    
    
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

    // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//    static final SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE);
    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;
    
    /**
     * Crée le gestionnaire.
     * @param referentiel
     * @param mots
     */
    public GestionAdministration(GestionReferentiel referentiel,GestionMots mots,GestionConnection gc,GestionMiseAJour gmaj,GestionIdentifiants gi, JDONREFParams params)
    {
        this.referentiel = referentiel;
        this.mots = mots;
        this.gc = gc;
        this.gmaj = gmaj;
        this.gi = gi;
        
        np.add(0,null); // L'état de JDONREF est géré par GestionConnection, GestionMots, ...
        this.params = params;
    }
    
    /**
     * Retourne l'état de JDONREF v2.<br>
     * Cette opération est loggée en tant que méthode utilisateur et méthode de méta-administration.<br>
     * Le log de la méthode de méta-administration a pour forme "2:1:0:L'état de JDONREF a été récupéré".
     * @return le tableau résultat : <ul>
     * <li>1</li>
     * <li>La charge actuelle de JDONREF</li>
     * <li>Nombre de mots chargés</li>
     * <li>Nombre d'abbréviations chargées</li>
     * </ul>
     */
    public String[] getState(int application)
    {
        String[] res = new String[4];
        
        res[0] = "1";
        res[1] = Integer.toString(gc.obtientCharge());
        res[2] = Integer.toString(this.mots.obtientCompteMots());
        res[3] = Integer.toString(this.mots.obtientCompteAbbreviation());
        
//        GestionLogs.getInstance().logMetaAdmin(2,1,"0:L'état de JDONREF a été récupéré");
        params.getGestionLog().logMetaAdmin(2,1,"0:L'état de JDONREF a été récupéré");
        params.getGestionLog().logGetState(application,true);
        
        return res;
    }
    
    /**
     * Retourne l'état de JDONREFv2 ou du processus spécifié.<br>
     * Les opérations sont logguées en tant que tâche de méta administration avec le format suivant:<br>
     * 2:etat:processus:message<br>
     * où
     * <ul><li>etat vaut 0 si une erreur a été rencontrée, 1 si la tâche a été exécutée.</li>
     *     <li>processus correspond au paramètre de la méthode</li>
     *     <li>message est un complément d'information sur l'opération effectuée</li></ul>
     * @param processus 0 pour obtenir l'état de JDONREFv2, le numéro du processus sinon.
     * @return le tableau resultat, qui dépend du processus, de la forme:
     * <ul><li>1</li><li>nom du processus</li><li>statut (TERMINE, EN COURS, ATTENTE, ERREUR)</li><li>...</li></ul>
     */
    public String[] getState(int application,int processus)
    {
        if (processus==0)
        {
            // loggé dans la méthode, il s'agit d'une méthode utilisateur et de méta-administration.
            return getState(application);
        }
        
        Processus p = np.obtientParametres(processus);
        
        if (p==null)
        {
            params.getGestionLog().logMetaAdmin(2,0,processus+":Le processus "+processus+" n'a pas été trouvé");
            return new String[]{"0","5","Aucun processus ne correspond au numéro demandé."};
        }
        
        String[] res = p.getState();
        params.getGestionLog().logMetaAdmin(2,1,processus+":L'état du processus "+processus+" a été récupéré");
        return res;
    }
    
    /**
     * Permet d'obtenir la liste des processus.<br>
     * Si aucune erreur ne survient, le résultat a la forme suivante:
     * <ul>
     * <li>1</li>
     * <li>Nombre de processus</li>
     * <li>numéro de processus</li>
     * <li>Nom du processus</li>
     * <li>Etat du processus : ATTENTE, EN COURS, ERREUR, ou TERMINE.
     * </ul>
     * Les opérations sont logguées en tant que tâche de méta administration avec le format suivant:<br>
     * 5:etat:message<br>
     * où
     * <ul><li>etat vaut 0 si une erreur a été rencontrée, 1 si la tâche a été exécutée.</li>
     *     <li>message est un complément d'information sur l'opération effectuée</li></ul>
     * @return des triplets : numéro de processus
     */
    public String[] getList()
    {
        String[] res = new String[2+3*(np.count()-1)];
        res[0] = "1";
        res[1] = Integer.toString(np.count());
        for(int i=1;i<np.count();i++) // le processus 1 est réservé!
        {
            res[3*i-1] = Integer.toString(np.numero(i));
            Processus p = np.obtientParametres(np.numero(i));
            res[3*i] = p.name;
            if (p.state.length>0)
                res[3*i+1] = p.state[0];
            else
                res[3*i+1] = "null";
        }
        params.getGestionLog().logMetaAdmin(5,1,"Récupération de la liste des processus");
        return res;
    }
    
    /**
     * Libère les processus spécifiés.<br>
     * Si le tableau est null ou vide, tous les processus sont libérés.<br>
     * Le nombre de processus trouvés et libérés est retourné par cet appel (deuxième élément de la liste).<br>
     * Les opérations sont logguées en tant que tâche de méta administration avec le format suivant:<br>
     * 4:etat:processus:message<br>
     * où
     * <ul><li>etat vaut 0 si une erreur a été rencontrée, 1 si la tâche a été exécutée.</li>
     *     <li>processus correspond au paramètre de la méthode</li>
     *     <li>message est un complément d'information sur l'opération effectuée</li></ul>
     * @param processus
     * @return le tableau résultat : [1,nombre de processus libéré]
     */
    public String[] free(int[] processus)
    {
        int count=0;
        if (processus==null || processus.length==0)
        {
            int total = np.count();
            
            params.getGestionLog().logMetaAdmin(4,1,"s'apprête à libérer "+(total-1)+" processus");
            
            for(int i=1; i<total; i++) // l'état 0 est réservé à JDONREF.
            {
                int numero = np.numero(i);
                Processus p = np.obtientParametres(numero);
                
                if (p!=null && p.finished)
                {
                    if (np.remove(numero))
                    {
                        params.getGestionLog().logMetaAdmin(4,1,numero+":Le processus "+numero+" a été libéré");
                        i--;
                        total--;
                        count++;
                    }
                }
            }
        }
        else
        {
            for(int i=0; i<processus.length; i++)
            {
                Processus p = np.obtientParametres(processus[i]);
                
                if (p!=null && p.finished)
                {
                    int numero = processus[i];
                    if (np.remove(numero))
                    {
                        params.getGestionLog().logMetaAdmin(4,1,numero+":Le processus "+numero+" a été libéré");
                        count++;
                    }
                }
            }
        }
        return new String[]{"1",Integer.toString(count)};
    }
    
    /**
     * Arrête le processus spécifié.<br>
     * Les opérations sont logguées en tant que tâche de méta administration avec le format suivant:<br>
     * 1:etat:processus:message<br>
     * où
     * <ul><li>etat vaut 0 si une erreur a été rencontrée, 1 si le processus va être arrété.</li>
     *     <li>processus correspond au paramètre de la méthode</li>
     *     <li>message est un complément d'information sur l'opération effectuée</li></ul>
     * @param processus
     */
    public String[] stop(int processus)
    {
        Processus p = np.obtientParametres(processus);
        
        if (p==null)
        {
            params.getGestionLog().logMetaAdmin(1,0,processus+":Le processus "+processus+" n'a pas été trouvé");
            return new String[]{"0","5","Aucun processus ne correspond au numéro demandé"};
        }
        
        params.getGestionLog().logMetaAdmin(1,1,processus+":Arrêt du processus : "+processus);
        p.stop = true;
        
        return new String[]{"1"};
    }
    
    /**
     * Effectue l'opération d'administration précédemment attribuée.<br>
     * Les opérations sont logguées en tant que tâche de méta-administration avec le format suivant:<br>
     * 0:code:processus:message<br>où
     * <ul><li>code prend une des valeurs suivantes:
     *         <ul><li>0 pour un lancement de processus inconnu</li>
     *             <li>1 pour le lancement d'un processus en cour d'exécution</li>
     *             <li>2 pour le lancement d'un processus</li>
     *             <li>3 pour la fin d'exécution d'un processus</li></ul></li>
     *     <li>processus est le numéro du processus passé en paramètre</li>
     *     <li>message est un message correspondant au code d'erreur ou donnant plus de détail sur l'opération.</li></ul>
     * @param processus
     */
    public String[] administre(int processus)
    {
        Processus p = np.obtientParametres(processus);
        
        if (p==null)
        {
            params.getGestionLog().logMetaAdmin(0,0,processus+":Lancement d'un processus inconnu : "+processus);
            return new String[]{"0","5","Aucun processus ne correspond au numéro demandé."};
        }
        
        if (p.state!=null && p.state.length>0 && p.state[0].compareTo("EN COURS")==0)
        {
            params.getGestionLog().logMetaAdmin(0,1,processus+":Lancement d'un processus en cour d'exécution : "+processus);
            return new String[]{"0","12","Le processus est déjà en cour d'exécution."};
        }
        
        String operation = p.parametres[0];
        
        Calendar t0 = Calendar.getInstance();
        
        String[] res = null;
        
        if (operation.compareTo("normalise")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de normalisation, processus "+processus);
            // logs dans la méthode
            res = normalise(p);
        }
        else if (operation.compareTo("changementReferentiel")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de changement de référentiel, processus "+processus);
            // logs dans la méthode
            res = changementReferentiel(p);
        }
        else if (operation.compareTo("prepareChangement")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de préparation de changement de référentiel, processus "+processus);
            // logs dans la méthode
            res = prepareChangement(p);
        }
        else if (operation.compareTo("phonetise")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de phonétisation, processus "+processus);
            // logs dans la méthode
            res = phonetise(p);
        }
        else if (operation.compareTo("prepareMaj")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de préparation de mise à jour, processus "+processus);
            // logs dans la méthode
            res = prepareMaj(p);
        }
        else if (operation.compareTo("genereFantoirs")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de génération de codes fantoirs, processus "+processus);
            // logs dans la méthode
            res = genereFantoir(p);
        }
        else if (operation.compareTo("calculeClesAmbiguesDansVoies")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de calcul de cles ambigues dans les voies, processus "+processus);
            // logs dans la méthode
            res = calculeClesAmbiguesDansVoies(p);
        }
        else if (operation.compareTo("calculeCommunesAmbiguesDansVoies")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de calcul de communes ambigues dans les voies, processus "+processus);
            // logs dans la méthode
            res = calculeCommunesAmbiguesDansVoies(p);
        }
        else if (operation.compareTo("calculeClesAmbiguesDansCommunes")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de cles ambigues dans les communes, processus "+processus);
            // logs dans la méthode
            res = calculeClesAmbiguesDansCommunes(p);
        }
        else if (operation.compareTo("changeId")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de changement d'identifiants, processus "+processus);
            // logs dans la méthode
            res = changeId(p);
        }
        else if (operation.compareTo("creeTableVoie")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de création de table de voies, processus "+processus);
            // logs dans la méthode
            res = creeTableVoie(p);
        }
        else if (operation.compareTo("decoupe")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de découpage, processus "+processus);
            // logs dans la méthode
            res = decoupe(p);
        }
        else if (operation.compareTo("restructure")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de restructuration, processus "+processus);
            // logs dans la méthode
            res = restructure(p);
        }
        else if (operation.compareTo("maj")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de mise à jour, processus "+processus);
            // logs dans la méthode
            res = maj(p);
        }
        else if (operation.compareTo("decoupeNumeros")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de découpage de numéros, processus "+processus);
            // logs dans la méthode
            res = decoupeNumeros(p);
        }
        else if (operation.compareTo("genereIdTroncons")==0)
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération de génération de tronçons, processus "+processus);
            // logs dans la méthode
            res = genereIdTroncons(p);
        }
        else
        {
            params.getGestionLog().logMetaAdmin(0,2,processus+":Lancement d'une opération non supportée, processus "+processus);
            res = new String[]{"0","5","Opération non supportée"};
        }
        
        Calendar t1=Calendar.getInstance();
        
        long delta = t1.getTimeInMillis()-t0.getTimeInMillis();
        params.getGestionLog().logMetaAdmin(0,3,processus+":Fin d'éxécution du processus "+processus+" après "+delta+" ms");
        p.resultat.add("Délai d'exécution: "+delta+" ms");
        
        p.finished = true;
        if (p.state[0].compareTo("ERREUR")!=0)
        {
            p.state=new String[]{"TERMINE"};
        }
        
        return res;
    }
    
    /**
     * Vérifie si l'opération spécifiée existe.
     * @param operation
     * @return true si l'opération existe
     */
    public boolean checkOperation(String operation)
    {
        if (operation.compareTo("normalise")==0)
            return true;
        else if (operation.compareTo("changementReferentiel")==0)
            return true;
        else if (operation.compareTo("prepareChangement")==0)
            return true;
        else if (operation.compareTo("phonetise")==0)
            return true;
        else if (operation.compareTo("prepareMaj")==0)
            return true;
        else if (operation.compareTo("genereFantoirs")==0)
            return true;
        else if (operation.compareTo("calculeClesAmbiguesDansVoies")==0)
            return true;
        else if (operation.compareTo("calculeClesAmbiguesDansCommunes")==0)
            return true;
        else if (operation.compareTo("calculeCommunesAmbiguesDansVoies")==0)
            return true;
        else if (operation.compareTo("changeId")==0)
            return true;
        else if (operation.compareTo("creeTableVoie")==0)
            return true;
        else if (operation.compareTo("decoupe")==0)
            return true;
        else if (operation.compareTo("restructure")==0)
            return true;
        else if (operation.compareTo("maj")==0)
            return true;
        else if (operation.compareTo("decoupeNumeros")==0)
            return true;
        else if (operation.compareTo("genereIdTroncons")==0)
            return true;
        return false;
    }
    
    /**
     * Attribue un nouveau numéro de processus.<br>
     * Le premier paramètre est le type d'opération, les paramètres suivants
     * les paramètres de l'opération.<br>
     * Les opérations sont logguées en tant que tâche de méta-administration avec le format suivant:<br>
     * 3:code:message<br>où
     * <ul><li>code prend une des valeurs suivantes:
     *         <ul><li>0 si les paramètres sont incorrects.</li>
     *             <li>1 si l'opération n'existe pas.</li>
     *             <li>2 si l'opération s'est bien déroulée.</li></ul></li>
     *     <li>message est un message correspondant au code d'erreur ou donnant plus de détail sur l'opération.</li></ul>
     * Si code vaut 2, le message a la forme "numero:message" où numéro est le numéro de processus attribué.
     */
    public String[] attribueProcessus(String name,String[] parametres,String[] connection1,String[] connection2)
    {
        if (parametres.length==0)
        {
            params.getGestionLog().logMetaAdmin(3,0,"Attribution d'un processus avec des paramètres incorrect : "+name);
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        int numero = np.getFree();
        
        if (!checkOperation(parametres[0]))
        {
            params.getGestionLog().logMetaAdmin(3,1,"Attribution d'un processus avec une méthode inconnue ("+parametres[0]+") : "+name);
            return new String[]{"0","5","L'opération spécifiée n'existe pas."};
        }
        
        Processus p = new Processus();
        p.name = name;
        p.parametres = parametres;
        p.connection1 = connection1;
        p.connection2 = connection2;
        p.state = new String[]{"ATTENTE"};

        p.numero = numero;
        // Obtient un numéro de log.
        p.version = params.getGestionLog().obtientNumeroVersion(numero);
        
        np.add(numero,p);
        
        params.getGestionLog().logMetaAdmin(3,2,numero+":Attribution du processus "+numero+" : "+name);
        return new String[]{"1",Integer.toString(numero)};
    }

    /**
     * Permet de découper les quatres numéros d'une table.<br>
     * Les paramètres nécessaires sont (outre la base concernée):
     * <ul>
     * <li>Le nom de la table concernée</li>
     * <li>L'identifiant unique dans cette table</li>
     * <li>le prefixe de la colonne source</li>
     * <li>Le prefixe de la colonne numéro destination</li>
     * <li>Le prefixe de la colonne répétition destination</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] decoupeNumeros(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length<6)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        String table = parametres[1];
        String id = parametres[2];
        String source = parametres[3];
        String numero = parametres[4];
        String repetition = parametres[5];
        
        Connection c1;
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.finished = false;
        try
        {
            c1 = Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        try
        {
            // logs dans la méthode
            referentiel.decoupenumeros(p,table,id,source,numero,repetition,c1);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la restructuration: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la restructuration: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la restructuration.",ex);
            return new String[]{"0","3","Problème SQL durant la retructuration."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la restructuration: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la restructuration: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la restructuration.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la restructuration"
                    };
        }
        
        return new String[]{"1"};
    }
    
    /**
     * Permet de restructuer les adresses d'une table.<br>
     * Les paramètres nécessaires sont (outre la base référentiel et la base concernée):
     * <ul>
     * <li>Le nom de la table concernée</li>
     * <li>L'identifiant unique dans cette table</li>
     * <li>Le nombre de colonne d'adresse gérée</li>
     * <li>Les noms de ces colonnes une à une</li>
     * <li>Les 6 noms des colonnes résultats de la restructuration. Ces colonnes peuvent être nul ou vide, et ne
     * sont alors pas prises en compte.</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] restructure(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length<10)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        String nomTable = parametres[1];
        String id = parametres[2];
        int nb_columns = 0;
        try
        {
            nb_columns = Integer.parseInt(parametres[3]);
        }
        catch(NumberFormatException nfe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Le nombre de colonnes est incorrect :"+parametres[3]);
            p.state = new String[]{"ERREUR","Le nombre de colonnes est incorrect :"+parametres[3]};
            p.finished = true;
            return new String[]{"0","5","Le nombre de colonnes est incorrect :"+parametres[3]};
        }
        String[] columns = new String[nb_columns];
        for(int i=0;i<nb_columns;i++)
        {
            columns[i] = parametres[4+i];
        }
        String[] restructuration = new String[6];
        for(int i=0;i<6;i++)
            restructuration[i] = parametres[5+nb_columns+i];
        
        Connection c1;
        Connection c2;
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }
        
        try
        {
            // logs dans la méthode
            referentiel.restructure(p, nomTable, id, columns, restructuration, c1, c2);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la restructuration: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la restructuration: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la restructuration.",ex);
            return new String[]{"0","3","Problème SQL durant la retructuration."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la restructuration. "+e.getMessage());
            p.state = new String[]{"ERREUR","Problème indéterminé durant la restructuration. "+e.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la restructuration.",e);
            return new String[]{"0","7","Problème indéterminé durant la restructuration."};
        }
        return new String[]{"1"};
    }
    
    /**
     * Permet de découper une adresse en plusieurs champs.<br>
     * Les paramètres nécessaires sont (outre la base référentiel et la base concernée):
     * <ul>
     *   <li>Le nom de la table concernée</li>
     *   <li>L'identifiant unique dans cette table</li>
     *   <li>Le nombre de colonne d'adresse gérée</li>
     *   <li>Les noms de ces colonnes une à une</li>
     *   <li>Le nombre de découpages à produire</li>
     *   <li>Les couples de noms de colonnes cible et les valeurs de leurs découpages un à un</li>
     *   <li>La liste des lignes auxquelles appartiennent chaque colonne d'adresse</li>
     * </ul>
     * Si la liste des lignes n'est pas présente, une restructuration est effectuée.<br>
     * Les valeurs des découpages sont des combinaisons des bits définis ainsi:
     * <ul>
     *   <li>1 pour numero</li>
     *   <li>2 pour repetition</li>
     *   <li>4 pour autres numeros</li>
     *   <li>8 pour type de voie</li>
     *   <li>16 pour article</li>
     *   <li>32 pour libelle</li>
     *   <li>64 pour le mot déterminant</li>
     *   <li>128 pour code postal</li>
     *   <li>256 pour commune</li>
     *   <li>512 pour numero d'arrondissement</li>
     *   <li>1024 pour cedex</li>
     *   <li>2048 pour le code cedex</li>
     *   <li>4096 pour ligne1</li>
     *   <li>8192 pour ligne2</li>
     *   <li>16384 pour ligne3</li>
     *   <li>32768 pour ligne5</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] decoupe(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length<5)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        String nomTable = parametres[1];
        String id = parametres[2];
        int nb_columns = 0;
        try
        {
            nb_columns = Integer.parseInt(parametres[3]);
        }
        catch(NumberFormatException nfe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Le nombre de colonnes est incorrect :"+parametres[3]);
            p.state = new String[]{"ERREUR","Le nombre de colonnes est incorrect :"+parametres[3]};
            p.finished = true;
            return new String[]{"0","5","Le nombre de colonnes est incorrect :"+parametres[3]};
        }
        String[] columns = new String[nb_columns];
        for(int i=0;i<nb_columns;i++)
        {
            columns[i] = parametres[4+i];
        }
        int nb_decoupages = 0;
        try
        {
            nb_decoupages = Integer.parseInt(parametres[4+nb_columns]);
        }
        catch(NumberFormatException nfe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Le nombre de découpages est incorrect :"+parametres[4+nb_columns]);
            p.state = new String[]{"ERREUR","Le nombre de découpages est incorrect :"+parametres[4+nb_columns]};
            p.finished = true;
            return new String[]{"0","5","Le nombre de découpages est incorrect :"+parametres[4+nb_columns]};
        }
        String[] decoupages = new String[nb_decoupages];
        int[] natures = new int[nb_decoupages];
        for(int i=0;i<nb_decoupages;i++)
        {
            decoupages[i] = parametres[5+nb_columns+2*i];
            try
            {
                natures[i] = Integer.parseInt(parametres[5+nb_columns+2*i+1]);
            }
            catch(NumberFormatException nfe)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"La nature numéro "+i+" est incorrecte :"+parametres[5+nb_columns+2*i+1]);
                p.state = new String[]{"ERREUR","La nature numéro "+i+" est incorrecte :"+parametres[5+nb_columns+2*i+1]};
                p.finished = true;
                return new String[]{"0","5","La nature numéro "+i+" est incorrecte :"+parametres[5+nb_columns+2*i+1]};                
            }
        }
        int[] lignes;
        if (parametres.length==5+nb_columns+2*nb_decoupages)
        {
            lignes = null;
        }
        else
        {
            lignes = new int[nb_columns];
            for(int i=0; i<nb_columns; i++)
            {
                try
                {
                    lignes[i]=Integer.parseInt(parametres[5+nb_columns+2*nb_decoupages+i]);
                }
                catch(NumberFormatException nfe)
                {
                    params.getGestionLog().logAdmin(p.numero,p.version,
                            "La ligne numéro "+i+" est incorrecte :"+parametres[5+nb_columns+2*nb_decoupages+i]);
                    p.state=new String[]
                            {
                                "ERREUR",
                                "La ligne numéro "+i+" est incorrecte :"+parametres[5+nb_columns+2*nb_decoupages+i]
                            };
                    p.finished=true;
                    return new String[]
                            {
                                "0","5",
                                "La ligne numéro "+i+" est incorrecte :"+parametres[5+nb_columns+2*nb_decoupages+i]
                            };
                }
            }
        }
        
        Connection c1;
        Connection c2;
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }
        
        try
        {
            // logs dans la méthode
            referentiel.decoupe(p, nomTable, id, columns, decoupages, natures,lignes,c1, c2);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant le découpage: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant le découpage: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant le découpage.",ex);
            return new String[]{"0","3","Problème SQL durant le découpage."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant le découpage: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant le découpage: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant le découpage.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant le découpage"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Formate un message d'erreur et modifie l'état du processus en conséquence.
     * @param p le processus concerné
     * @param message Le macro-message décrivant l'erreur
     * @param numeroerreur le code d'erreur à retourner
     * @param ex l'exception générée.
     * @return
     */
    private String[] erreur(Processus p,String message,int numeroerreur,Exception ex,boolean log)
    {
        if (log)
        {
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,message);
        }
        p.resultat.add("ERREUR");
        p.resultat.add(ex.getMessage());
        for(int i=0; i<ex.getStackTrace().length; i++)
        {
            p.resultat.add(ex.getStackTrace()[i].toString());
            if (log)
                Logger.getLogger("GestionAdministration").log(Level.SEVERE,ex.getStackTrace()[i].toString());
        }
        p.finished=true;
        p.state=new String[]{"ERREUR",message};
        return new String[]{"0",Integer.toString(numeroerreur),message};
    }
    
    /**
     * Permet de changer les id référencés dans une table selon la table de correspondance spécifiée.
     * Les paramètres nécessaires sont (outre la base concernée):
     * <ul>
     * <li>Le code de département à insérer dans la table idvoies</li>
     * <li>Le nom de la table tronçons.</li>
     * <li>Le nom de la table voies.</li>
     * <li>Le nom de la table idvoies.</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] creeTableVoie(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=5)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            return erreur(p,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage(),5,ex,true);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            return erreur(p,"Problème durant l'établissement de la connection 1 : "+ex.getMessage(),3,ex,true);
        }

        String codeDepartement = parametres[1];
        String nomTableTroncon = parametres[2];
        String nomTableVoies = parametres[3];
        String nomTableIdVoies = parametres[4];
        try
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"CREATION TABLE VOIE");
            p.state = new String[]{"EN COURS","CREATION","TABLE VOIE","LANCEMENT","TRONCONS TRAITES","0","SUR 0"};
            // logs dans la méthode
            gmaj.creeTableVoieReferentiel(p,codeDepartement,nomTableTroncon,nomTableVoies, nomTableIdVoies, c1);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            return erreur(p,gre.getMessage(),gre.obtientNumeroErreur(),gre,false);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la création de la table: "+ex.getMessage());
            return erreur(p,"Problème SQL durant la création de la table: "+ex.getMessage(),3,ex,true);
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la création de la table: "+e.getMessage());
            return erreur(p,"Problème indéterminé durant la création de la table: "+e.getMessage(),7,e,true);
        }
        return new String[]{"1"};
    }
    
    /**
     * Permet de changer les id référencés dans une table selon la table de correspondance spécifiée.
     * Les paramètres nécessaires sont (outre le référentiel):
     * <ul>
     * <li>Le code de département concerné</li>
     * <li>Un booléen indiquant si les identifiants du référentiel doivent être conservé (true) ou si les identifiants
     * de la table mise à jour doivent être conservé (false)</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] changeId(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=3)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        Connection c2;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }

        String code_departement = parametres[1];
        boolean idsource = Boolean.parseBoolean(parametres[2]);
        try
        {
            // logs dans la méthode
            GestionIdentifiants.mise_a_jour_identifiants(p, code_departement, c1, c2, idsource);
        }
        catch(ColonneException ce)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,ce.getMessage());
            p.state = new String[]{"ERREUR",ce.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(ce.getMessage());
            p.finished = true;
            return new String[]{"0","10",ce.getMessage()};
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la mise à jour des identifiants: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la mise à jour des identifiants: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la mise à jour des identifiants.",ex);
            return new String[]{"0","3","Problème SQL durant la mise à jour des identifiants."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la mise à jour des identifiants: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la mise à jour des identifiants: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la mise à jour des identifiants.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la mise à jour des identifiants"
                    };
        }
        return new String[]{"1"};
    }
    
   /**
     * Calcule les communes ambigues d'un référentiel.<br>
     * Outre le référentiel, le code de département est nécessaire.
     */
    private String[] calculeClesAmbiguesDansCommunes(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=2)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        String code_departement = parametres[1];
        if (code_departement==null || code_departement.length()==0)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Le code de département est incorrect");
            p.state = new String[]{"ERREUR","Le code de département est incorrect"};
            p.finished = true;
            return new String[]{"0","5","Le code de département est incorrect."};
        }
        
        try
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"CALCULE CLES AMBIGUES DANS COMMUNES");
            p.state = new String[]{"EN COURS","CALCULE CLES AMBIGUES DANS COMMUNES","","","","COMMUNES TRAITEES","0"};
            // logs dans la méthode.
            gmaj.calculeClesAmbiguesDansCommunes(p,code_departement,c1);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la création des ambiguités: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la création des ambiguités: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des ambiguités.",ex);
            return new String[]{"0","3","Problème SQL durant la création des ambiguités."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la création des ambiguités: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la création des ambiguités: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des ambiguités.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la création des ambiguités"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Calcule les communes ambigues d'un référentiel.
     * Les paramètres nécessaires sont (outre le référentiel):
     * <ul>
     * <li>Le code de département concerné</li>
     * </ul>
     */
    private String[] calculeCommunesAmbiguesDansVoies(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=2)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        String codeDepartement = parametres[1];
        try
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"CALCULE COMMUNES AMBIGUES DANS VOIES");
            p.state = new String[]{"EN COURS","CALCULE COMMUNES AMBIGUES DANS VOIES","","","","VOIES TRAITEES","0"};
            // logs dans la méthode
            gmaj.calculeCommunesAmbiguesDansVoies(p,codeDepartement, c1);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la création des ambiguités: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la création des ambiguités: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des ambiguités.",ex);
            return new String[]{"0","3","Problème SQL durant la création des ambiguités."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la création des ambiguités: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la création des ambiguités: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des ambiguités.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la création des ambiguités"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Calcule les voies ambigues d'une table de voies.
     * Les paramètres nécessaires sont (outre le référentiel):
     * <ul>
     * <li>Le code de département concerné</li>
     * </ul>
     */
    private String[] calculeClesAmbiguesDansVoies(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=2)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects.");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        String codeDepartement = parametres[1];
        try
        {
            // logs dans la méthode
            gmaj.calculeClesAmbiguesDansVoies(p,codeDepartement, c1);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la création des ambiguités: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la création des ambiguités: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des ambiguités.",ex);
            return new String[]{"0","3","Problème SQL durant la création des ambiguités."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la création des ambiguités: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la création des ambiguités: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des ambiguités.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la création des ambiguités"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Crée des codes fantoirs aléatoires dans une colonne d'une table.
     * Les paramètres nécessaires sont (outre la base concernée):
     * <ul>
     * <li>Le nom de la table concernée.</li>
     * <li>La colonne identifiant unique dans cette table.</li>
     * <li>La colonne qui contiendra le code fantoir.</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] genereFantoir(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=4)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        String nomTable = parametres[1];
        String id = parametres[2];
        String colonne = parametres[3];
        try
        {
            // logs dans la méthode
            referentiel.genereFantoir(p,nomTable,id,colonne,c1);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la création des codes fantoirs: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la création des codes fantoirs: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des codes fantoirs.",ex);
            return new String[]{"0","3","Problème SQL durant la création des codes fantoirs."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la création des codes fantoirs: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la création des codes fantoirs: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la création des codes fantoirs.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la création des codes fantoirs"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Effectue une mise à jour du référentiel.<br>
     * Les paramètres nécessaires sont (outre les bases référentiel et mise à jour):
     * <ul>
     * <li>Le code département concerné.</li>
     * <li>La date à laquelle la mise à jour sera valable.</li>
     * <li>Une combinaison de drapeaux spécifiant les objets à mettre à jour décris par les flags
     * GestionReferentiel.MAJ_XXX.</li></ul>
     * NB: la base référentiel doit contenir les procédures stockées.
     * @param p
     * @return
     */
    private String[] maj(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=4)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        if (p.stop)
        {
            p.state = new String[]{"TERMINE"};
            p.resultat.add("INTERRUPTION PAR L UTILISATEUR");
            params.getGestionLog().logAdmin(p.numero,p.version,"INTERRUPTION PAR L UTILISATEUR");
            return new String[]{"1"};
        }
        
        Connection c1;
        Connection c2;
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }

        String code_departement = parametres[1];
        Date date = null;
        try
        {
            // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//            date = sdformat.parse(parametres[2]);
            date = DateUtils.parseStringToDate(parametres[2], sdformat);
        }
        catch(ParseException pe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"La date est invalide :"+pe.getMessage());
            p.state = new String[]{"ERREUR","La date est invalide :"+pe.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("La date est invalide : "+pe.getMessage());
            p.finished = true;
            return new String[]{"0","5","La date est invalide."};
        }
        int flags = 0;
        try
        {
            flags = Integer.parseInt(parametres[3]);
        }
        catch(NumberFormatException nfe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les drapeaux sont invalides");
            p.state = new String[]{"ERREUR","Les drapeaux sont invalides"};
            p.resultat.add("ERREUR");
            p.resultat.add("Les drapeaux sont invalides");
            p.finished = true;
            return new String[]{"0","5","Les drapeaux sont invalides."};
        }
        try
        {
            // logs dans la méthode
            referentiel.mise_a_jour(p,code_departement,flags,c1,c2,date);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,ex.getMessage());
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la mise à jour: "+ex.getMessage()};
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la mise à jour.",ex);
            return new String[]{"0","3","Problème SQL durant la mise à jour."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la mise à jour: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant la mise à jour: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème indéterminé durant la mise à jour.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la mise à jour"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Prépare les données pour une mise à jour de référentiel.<br>
     * Les paramètres nécessaires sont (outre les bases référentiel et mise à jour):
     * <ul>
     * <li>Le code département concerné</li>
     * </ul>
     * NB: la base référentiel doit contenir les procédures stockées.
     * @param p
     * @return
     */
    private String[] prepareMaj(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=2)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        Connection c2;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }

        String code_departement = parametres[1];
        
        try
        {
            // logs dans la méthode
            referentiel.prepareMajReferentiel(p,code_departement,c1,c2);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la préparation de la mise à jour de référentiel: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la préparation de la mise à jour de référentiel: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la préparation de la mise à jour de référentiel.",ex);
            return new String[]{"0","3","Problème SQL durant la préparation de la mise à jour de référentiel."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la préparation de la mise à jour de référentiel "+e.getMessage());
            p.state = new String[]{"ERREUR","Problème indéterminé durant la préparation de la mise à jour de référentiel "+e.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la préparation de la mise à jour de référentiel.",e);
            return new String[]{"0","7","Problème indéterminé durant la préparation de la mise à jour de référentiel"};
        }
        return new String[]{"1"};
    }
    
    /**
     * Crée le phonétique d'une colonne dans une autre colonne d'une table.
     * Les paramètres nécessaires sont (outre la base concernée):
     * <ul>
     * <li>Nom de la table</li>
     * <li>Nom de la colonne source</li>
     * <li>Nom de la colonne destination</li>
     * </ul>
     * @param p
     * @return
     */
    private String[] phonetise(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=4)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        p.state = new String[]{"EN COURS","CREE LA CONNEXION"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        String nomTable = parametres[1];
        String source = parametres[2];
        String destination = parametres[3];
        
        try
        {
            // les logs sont dans la méthode.
            referentiel.phonetise(p,source,destination,nomTable,c1);
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la phonétisation: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la phonétisation: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la phonétisation.",ex);
            return new String[]{"0","3","Problème SQL durant la phonétisation."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la phonétisation: "+e.getMessage());
                p.state = new String[]{"ERREUR","Problème indéterminé durant la phonétisation: "+e.getMessage()};
                p.resultat.add("ERREUR");
                p.resultat.add(e.getMessage());
                Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la phonétisation.",e);
                return new String[]{"0","7","Problème indéterminé durant la phonétisation"};
        }
        return new String[]{"1"};
    }
    
    /**
     * Prépare les données pour un changement de référentiel.<br>
     * Les paramètres nécessaires sont (outre les bases référentiel et mise à jour):
     * <ul>
     * <li>Le code département concerné</li>
     * </ul>
     * NB: la base référentiel doit contenir les procédures stockées.
     * @param p
     * @return
     */
    private String[] prepareChangement(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=2)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        Connection c2;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }

        String code_departement = parametres[1];
        
        try
        {
            // logs dans la méthode.
            referentiel.prepareChangementReferentiel(p,code_departement,c1,c2);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la préparation du changement de référentiel: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la préparation du changement de référentiel: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la préparation du changement de référentiel.",ex);
            return new String[]{"0","3","Problème SQL durant la préparation du changement de référentiel."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la préparation du changement de référentiel "+e.getMessage());
            p.state = new String[]{"ERREUR","Problème indéterminé durant la préparation du changement de référentiel "+e.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la préparation du changement de référentiel.",e);
            return new String[]{"0","7","Problème indéterminé durant la préparation du changement de référentiel"};
        }
        return new String[]{"1"};
    }
    
    /**
     * Effectue un changement de référentiel.<br>
     * Les paramètres nécessaires sont (outre les bases référentiel et destination):
     * <ul>
     * <li>changementReferentiel</li>
     * <li>Le code de département concerné</li>
     * <li>La date à laquelle le changement est effectué</li>
     * <li>true pour conserver les id du référentiel, ou false pour conserver ceux de la destination.</li>
     * <li>Une combinaison de drapeaux spécifiant les objets à mettre à jour comprenant:
     * <ul><li>1 les départements</li>
     * <li>2 les communes et arrondissements</li>
     * <li>4 les troncons</li>
     * <li>8 les voies</li>
     * <li>16 les adresses</li>
     * <li>32 le recalcul de la table de voies de la mise à jour</li>
     * </ul>
     * </ul>
     * NB: la base référentiel doit contenir les procédures stockées.
     */
    private String[] changementReferentiel(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=6)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects"};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        Connection c1;
        Connection c2;
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        try
        {
            c2=Base.connecte(p.connection2);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
            p.finished = true;
            return new String[]{"0","5","Les paramètres de la connection 2 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 2."};
        }
        
        String code_departement = parametres[1];
        Date date = null;
        try
        {
            // WA 09/2011 DateFormat n'est pas threadSafe : remplace par DateUtils
//            date = sdformat.parse(parametres[2]);
            date = DateUtils.parseStringToDate(parametres[2], sdformat);
        }
        catch(ParseException pe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"La date est invalide :"+pe.getMessage());
            p.state = new String[]{"ERREUR","La date est invalide :"+pe.getMessage()};
            p.finished = true;
            return new String[]{"0","5","La date est invalide."};
        }
        boolean idsource = Boolean.parseBoolean(parametres[3]);
        int flags = 0;
        try
        {
            flags = Integer.parseInt(parametres[4]);
        }
        catch(NumberFormatException nfe)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les drapeaux sont invalides");
            p.state = new String[]{"ERREUR","Les drapeaux sont invalides"};
            p.resultat.add("ERREUR");
            p.resultat.add("Les drapeaux sont invalides");
            p.finished = true;
            return new String[]{"0","5","Les drapeaux sont invalides."};
        }
        try
        {
            // logs dans la méthode
            referentiel.changementReferentiel(p,code_departement,flags,c1,c2,date,idsource);
        }
        catch(ColonneException ce)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,ce.getMessage());
            p.state = new String[]{"ERREUR",ce.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(ce.getMessage());
            p.finished = true;
            return new String[]{"0","10",ce.getMessage()};
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant le changement de Referentiel: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant le changement de Referentiel: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant le changement de Referentiel.",ex);
            return new String[]{"0","3","Problème SQL durant le changement de Referentiel."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant le changement de Referentiel: "+e.getMessage());
            p.state=new String[]
                    {
                        "ERREUR","Problème indéterminé durant le changement de Referentiel: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant le changement de Referentiel.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant le changement de Referentiel"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Normalise la colonne de type voie d'une table à partir d'une autre colonne de type voie.<br>
     * Les paramètres nécessaires sont les suivants, en plus de la base référentiel et la base cible:
     * <ul>
     * <li>normalise</li>
     * <li>Nom de la table</li>
     * <li>Colonne source</li>
     * <li>Colonne destination</li>
     * <li>Colonne departement (peut être null, auquel cas le département par défaut est choisi)</li>
     * <li>flags (valeurs 1 4 8 16 32)</li>
     * <li>ligne</li>
     * </ul>
     */
    private String[] normalise(Processus p)
    {
        Connection c1=null;
        Connection c2=null;
        
        String[] parametres = p.parametres;
        
        if (parametres.length==1)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Obtient la documentation de la méthode");
            p.state = new String[]{"DOCUMENTATION"};
            p.resultat.add("DOCUMENTATION");
            p.resultat.add("Normalise le champ spécifié d'une table dans un autre de ses champs.");
            p.resultat.add("Les paramètres nécessaires (outre les connections sont les suivants):");
            p.resultat.add("normalise");
            p.resultat.add("nom de la table");
            p.resultat.add("colonne source");
            p.resultat.add("colonne destination");
            p.resultat.add("colonne département ou code postal ou code insee");
            p.resultat.add("une combinaison des bits suivants:");
            p.resultat.add("  1  première passe de normalisation");
            p.resultat.add("  4  deuxième passe de normalisation");
            p.resultat.add("  8  indique si la réduction à 38 caractères doit être appliquée durant la seconde passe");
            p.resultat.add("  16 phonétisation");
            p.resultat.add("  32 indique si la désabbréviation doit être appliquée ou non");
            p.resultat.add("  64 retourne l'équivalent sans articles");
            p.resultat.add("la ligne de la norme postale à laquelle appartient ce champ");
            p.finished = true;
            return new String[]{"0","5","Pas assez de paramètres"};
        }
        
        if (parametres.length!=7)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects."};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }
        
        try
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"crée les connexions");
            p.state=new String[]
                    {
                        "EN COURS","CREE LES CONNEXIONS"
                    };
            p.finished=false;
            try
            {
                c1=Base.connecte(p.connection1);
            }
            catch(JDONREFException ex)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés "+ex.getMessage());
                p.state=new String[]
                        {
                            "ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()
                        };
                p.finished=true;
                p.resultat.add("ERREUR");
                p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
                return new String[]
                        {
                            "0","5","Les paramètres de la connection 1 sont mal formatés."
                        };
            }
            catch(SQLException ex)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
                p.state=new String[]
                        {
                            "ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()
                        };
                p.resultat.add("ERREUR");
                p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
                p.finished=true;
                Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
                return new String[]
                        {
                            "0","3","Problème durant l'établissement de la connection 1."
                        };
            }
            try
            {
                c2=Base.connecte(p.connection2);
            }
            catch(JDONREFException ex)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage());
                p.state=new String[]
                        {
                            "ERREUR","Les paramètres de la connection 2 sont mal formatés : "+ex.getMessage()
                        };
                p.resultat.add("ERREUR");
                p.resultat.add("Les paramètres de la connection 2 sont mal formatés :"+ex.getMessage());
                p.finished=true;
                return new String[]
                        {
                            "0","5","Les paramètres de la connection 2 sont mal formatés."
                        };
            }
            catch(SQLException ex)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 2: "+ex.getMessage());
                p.state=new String[]
                        {
                            "ERREUR","Problème durant l'établissement de la connection 2: "+ex.getMessage()
                        };
                p.resultat.add("ERREUR");
                p.resultat.add("Problème durant l'établissement de la connection 2 :"+ex.getMessage());
                p.finished=true;
                Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 2.",ex);
                return new String[]
                        {
                            "0","3","Problème durant l'établissement de la connection 2."
                        };
            }
            
            String nomTable=parametres[1];
            String source=parametres[2];
            String destination=parametres[3];
            String departement=parametres[4];
            if (parametres[4]!=null && parametres[4].compareTo("null")==0)
                departement = null;
            int flags=0;
            int ligne=0;
            try
            {
                flags=Integer.parseInt(parametres[5]);
            }
            catch(NumberFormatException nfe)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Le paramètre flags est mal formaté ("+parametres[5]+")");
                p.state=new String[]
                        {
                            "ERREUR","Le paramètre flags est mal formaté ("+parametres[5]+")."
                        };
                p.resultat.add("ERREUR sur le paramètre flags ("+parametres[5]+")");
                p.finished=true;
                return new String[]
                        {
                            "0","5","Le paramètre flags est mal formaté ("+parametres[5]+")."
                        };
            }
            try
            {
                ligne=Integer.parseInt(parametres[6]);
            }
            catch(NumberFormatException nfe)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Le paramètre ligne est mal formaté.");
                p.state=new String[]
                        {
                            "ERREUR","Le paramètre ligne est mal formaté."
                        };
                p.resultat.add("ERREUR sur le paramètre ligne.");
                p.finished=true;
                return new String[]
                        {
                            "0","5","Le paramètre ligne est mal formaté."
                        };
            }
            
            try
            {
                // logs dans la méthode.
                referentiel.normalise(p,nomTable,source,destination,departement,flags,ligne,c2,c1);
            }
            catch(SQLException ex)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la normalisation: "+ex.getMessage());
                p.state=new String[]
                        {
                            "ERREUR","Problème SQL durant la normalisation: "+ex.getMessage()
                        };
                p.resultat.add("ERREUR SQL");
                p.resultat.add(ex.getMessage());
                p.finished=true;
                Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la normalisation.",ex);
                return new String[]
                        {
                            "0","3","Problème SQL durant la normalisation."
                        };
            }
            catch(Exception e)
            {
                params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la normalisation: "+e.getMessage());
                p.state = new String[]{"ERREUR","Problème indéterminé durant la normalisation: "+e.getMessage()};
                p.resultat.add("ERREUR");
                p.resultat.add(e.getMessage());
                Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la normalisation.",e);
                return new String[]{"0","7","Problème indéterminé durant la normalisation"};
            }
            params.getGestionLog().logAdmin(p.numero,p.version,"Processus terminé.");
            return new String[]
                    {
                        "1"
                    };
        }
        finally
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Fermeture des connexions.");
            try { if (c1!=null) c1.close();} catch(SQLException sqle){}
            try { if (c2!=null) c2.close();} catch(SQLException sqle){}
        }
    }

    /**
     * Génère des identifiants de tronçon aléatoire.<br>
     * Les paramètres attendus sont les suivants (en plus d'une connection à une base):
     * <ul>
     * <li>Le code de département concerné</li>
     * </ul>
     * La table troncon-dpt est alors remplie en respectant l'unicité dans idtroncons.
     * @param p
     * @return
     */
    private String[] genereIdTroncons(Processus p)
    {
        String[] parametres = p.parametres;
        
        if (parametres.length!=2)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres sont incorrects.");
            p.state = new String[]{"ERREUR","Les paramètres sont incorrects."};
            p.finished = true;
            return new String[]{"0","5","Les paramètres sont incorrects."};
        }

        Connection c1;
        params.getGestionLog().logAdmin(p.numero,p.version,"CREE LES CONNEXIONS");
        p.state = new String[]{"EN COURS","CREE LES CONNEXIONS"};
        p.finished = false;
        try
        {
            c1=Base.connecte(p.connection1);
        }
        catch(JDONREFException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Les paramètres de la connection 1 sont mal formatés : "+ex.getMessage()};
            p.finished = true;
            p.resultat.add("ERREUR");
            p.resultat.add("Les paramètres de la connection 1 sont mal formatés :"+ex.getMessage());
            return new String[]{"0","5","Les paramètres de la connection 1 sont mal formatés."};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème durant l'établissement de la connection 1 : "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème durant l'établissement de la connection 1 : "+ex.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add("Problème durant l'établissement de la connection 1 :"+ex.getMessage());
            p.finished = true;
            Logger.getLogger("JDONREF").log(Level.SEVERE,"Problème durant l'établissement de la connection 1.",ex);
            return new String[]{"0","3","Problème durant l'établissement de la connection 1."};
        }
        
        String code_departement = parametres[1];
        
        try
        {
            // logs dans la méthode
            referentiel.genereIdTroncon(p,code_departement,c1);
        }
        catch(GestionReferentielException gre)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,gre.getMessage());
            p.state = new String[]{"ERREUR",gre.getMessage()};
            p.resultat.add("ERREUR");
            p.resultat.add(gre.getMessage());
            p.finished = true;
            return new String[]{"0",Integer.toString(gre.obtientNumeroErreur()),gre.getMessage()};
        }
        catch(SQLException ex)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème SQL durant la génération des id de troncon: "+ex.getMessage());
            p.state = new String[]{"ERREUR","Problème SQL durant la génération des id de troncon: "+ex.getMessage()};
            p.resultat.add("ERREUR SQL");
            p.resultat.add(ex.getMessage());
            p.finished = true;
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la génération des id de troncon.",ex);
            return new String[]{"0","3","Problème SQL durant la génération des id de troncon."};
        }
        catch(Exception e)
        {
            params.getGestionLog().logAdmin(p.numero,p.version,"Problème indéterminé durant la génération des id de troncon: "+e.getMessage());
            p.state = new String[]
                    {
                        "ERREUR","Problème indéterminé durant la génération des id de troncon: "+e.getMessage()
                    };
            p.resultat.add("ERREUR");
            p.resultat.add(e.getMessage());
            Logger.getLogger("GestionAdministration").log(Level.SEVERE,"Problème sql durant la génération des id de troncon.",e);
            return new String[]
                    {
                        "0","7","Problème indéterminé durant la génération des id de troncon"
                    };
        }
        return new String[]{"1"};
    }
    
    /**
     * Quelques tests de la classe.
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            int error=0;
            System.out.println("\r\nCrée un objet GestionAdministration");
            JDONREFParams params=new JDONREFParams();
            params.load("params.xml");
            GestionMots gm = new GestionMots();
            GestionConnection gc=new GestionConnection(params);
            GestionAdministration ga=new GestionAdministration(null,gm,gc,null,null,params);

//            GestionLogs.getInstance().repertoire="test";
            System.out.println("\r\nAttribue des processus");
            int p1=Integer.parseInt(ga.attribueProcessus("test",
                    new String[]{"calculeClesAmbiguesDansVoies","75"},null,null)[1]);
            System.out.println(p1);
            int p2=Integer.parseInt(ga.attribueProcessus("test",
                    new String[]{"calculeClesAmbiguesDansVoies","75"},null,null)[1]);
            System.out.println(p2);
            int p3=Integer.parseInt(ga.attribueProcessus("test",
                    new String[]{"calculeClesAmbiguesDansVoies","75"},null,null)[1]);
            System.out.println(p3);

            System.out.println("\r\nExécute les processus (simulation)");
            ga.np.processus.get(1).state=new String[]{"TERMINE"};
            ga.np.processus.get(1).finished=true;
            ga.np.processus.get(2).state=new String[]{"TERMINE"};
            ga.np.processus.get(2).finished=true;
            ga.np.processus.get(3).state=new String[]{"TERMINE"};
            ga.np.processus.get(3).finished=true;

            System.out.println("\r\nObtient l'état du processus "+p2+" avec getState");
            String[] state=ga.getState(p2);
            for(int i=0; i<state.length;
                    i++)
            {
                System.out.println(state[i]);
            }
            System.out.println("\r\nLibère le processus "+p2+" avec free");
            String[] res=ga.free(new int[]{p2});
            for(int i=0; i<res.length;
                    i++)
            {
                System.out.println(res[i]);
            }
            if ("1".compareTo(res[1])!=0)
            {
                error++;
                System.out.println("ERREUR!");
            }

            System.out.println("\r\nObtient l'état du processus "+p2+" avec getState");
            state=ga.getState(3,p2);
            for(int i=0; i<state.length;
                    i++)
            {
                System.out.println(state[i]);
            }
            if ("0".compareTo(res[0])==0)
            {
                error++;
                System.out.println("ERREUR!");
            }

            System.out.println("\r\nLibère tous les processus avec free(null)");
            res=ga.free(null);
            for(int i=0; i<res.length;
                    i++)
            {
                System.out.println(res[i]);
            }
            if ("2".compareTo(res[1])!=0)
            {
                error++;
                System.out.println("ERREUR!");
            }

            System.out.println("\r\nObtient l'état du processus "+p3+" avec getState");
            state=ga.getState(3,p3);
            for(int i=0; i<state.length;
                    i++)
            {
                System.out.println(state[i]);
            }
            if ("1".compareTo(state[0])==0)
            {
                error++;
                System.out.println("ERREUR!");
            }

            if (error>0)
            {
                System.out.println("\r\n"+error+" erreur(s) rencontrée(s).");
            }
            else
            {
                System.out.println("\r\nAucunes erreurs rencontrées.");
            }
        }
        catch(ClassNotFoundException ex)
        {
            Logger.getLogger(GestionAdministration.class.getName()).log(Level.SEVERE,null,ex);
        }        catch(JDOMException ex)
        {
            Logger.getLogger(GestionAdministration.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(IOException ex)
        {
            Logger.getLogger(GestionAdministration.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(JDONREFException ex)
        {
            Logger.getLogger(GestionAdministration.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}