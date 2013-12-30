package ppol.jdonref.geocodeur;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.CaretEvent;
import org.jdom.JDOMException;
import ppol.jdonref.JDONREFService;

/**
 * Permet de valider et de géocoder un fichier d'adresses.<br>
 * Le géocodeur est accompagné d'un fichier de configuration et travaille sur des répertoires qui peuvent contenir quatre types de fichier:
 * <ul>
 *     <li>in.csv, les adresses à valider ou géocoder</li>
 *     <li>out.csv, les adresses validées et géocodées</li>
 *     <li>rejets.xml, les rejets de validation</li>
 *     <li>info.txt, les statistiques issues de la validation</li>
 * </ul>
 * @author jmoquet
 */
public class JDONREFv2Geocodeur implements IJDONREFDialogueController
{
    Config config;
    In in;
    Out out;
    Rejets rejets;
    Info info;
    Erreurs erreurs;
    
    static String version = "1.20";
    
    /**
     * Un répertoire de travail.
     */
    public String repertoire = null;
    
    private JDONREFService service;
    
    /**
     * Les propositions actuellement effectuées
     */
    Propositions propositions = null;
    
    /**
     * Définit la variable propositions si nécessaire (classe Propositions)
     * @param a
     */
    private void setPropositions(Adresse a)
    {
        if (a!=null && a.getClass().getName().compareTo(Propositions.class.getName())==0)
        {
            propositions=(Propositions) a;
        }
        else
        {
            propositions=null;
        }
    }
    
    /**
     * Constructeur par défaut.
     */
    public JDONREFv2Geocodeur()
    {
    }
    
    /**
     * Permet de charger le fichier de configuration et le fichier d'adresse à valider ou géocoder.<br>
     * Les fichiers suivants sont lus:
     * <ul>
     * <li>config.xml</li>
     * <li>in.csv</li>
     * </ul>
     * @param repertoire
     */
    public void load(String repertoire) throws JDOMException, IOException, Exception
    {
        config = new Config();
        config.load("config.xml");
        
        in = new In();
        in.read(repertoire,config);
        
        out = new Out();
        
        rejets = new Rejets();
        
        erreurs = new Erreurs();
        
        if (config.geocodage==Geocodage.Aucun)
            info = new Info();
        else
            info = new InfoGeocodage();
    }
    
    /**
     * Permet de charger tous les fichiers de configuration et les fichiers ressources.<br>
     * Les fichiers suivants sont lus:
     * <ul>
     * <li>config.xml</li>
     * <li>in.csv</li>
     * <li>out.csv</li>
     * <li>rejets.xml</li>
     * <li>erreurs.txt</li>
     * <li>info.xml</li>
     * </ul>

     * @param repertoire
     */
    public void loadAll(String repertoire) throws JDOMException, IOException, Exception
    {
        config = new Config();
        config.load("config.xml");
        
        in = new In();
        in.read(repertoire,config);
        
        out = new Out();
        out.read(repertoire,config);
        
        rejets = new Rejets();
        rejets.read(repertoire,config);
        
        erreurs = new Erreurs();
        erreurs.read(repertoire);

        info = Info.read(repertoire);
        
        service = config.getService();
    }
    
    JDONREFv2Dialogue dialogue = new JDONREFv2Dialogue();
    
    /**
     * Effectue la reprise manuelle du fichier en entrée.<br>
     */
    public void doit2() throws MalformedURLException
    {   
        if (rejets.adresses.size()>0)
        {
            dialogue.setVersion(version);
            dialogue.connect(this);
            
            dialogue.setMax(rejets.adresses.size());
            dialogue.setIndex(info.start+1);
            
            info.nb_erreurs_grave = 0; // Le compteur repart de zéro car les erreurs peuvent être cumulées.
            
            if (info.start<rejets.adresses.size())
            {
                AdresseNonValide anv = rejets.adresses.get(info.start);
                setPropositions(anv);
                dialogue.setAdresse(anv);
                dialogue.setVisible(true);
            }
            else
            {
                System.out.println("Traitement terminé");
            }
        }
    }
    
    /**
     * Effectue l'échec de toutes les propositions.<br>
     * Aucun email n'est envoyé.
     * 
     * Si spécifié, la normalisation est effectuée sur les adresses rejetées.
     * Si spécifié, un découpage est effectué sur les adresses rejetées.
     */
    public void doit3()
    {
        while(rejets.adresses.size()>0)
        {
            AdresseNonValide anv = rejets.adresses.get(0);
            if (config.normalise)
            {
                try {
                    anv.normalise(service, config, 1);
                } catch (Exception ex) {
                    erreurs.erreurs.add("Echec de normalisation de l'adresse id="+anv.id+" ligne4="+anv.ligne4+" ligne6="+anv.ligne6+".");
                }
            }
            
            if (config.decoupe)
            {
                try {
                    anv.decoupe(service, config);
                } catch (Exception ex) {
                    erreurs.erreurs.add("Echec de découpage de l'adresse id="+anv.id+" ligne4="+anv.ligne4+" ligne6="+anv.ligne6+".");
                }
            }
            
            rejets.adresses.remove(0);
            out.adresses.add(anv);
        }
    }
    
    /**
     * Abaisse la note à la valeur indiquée.<br>
     * Aucun email n'est envoyé.
     */
    public void doit4(int note)
    {
        for(int i=0;i<rejets.adresses.size();i++)
        {
            AdresseNonValide anv = rejets.adresses.get(i);
            
            if (anv.getClass().toString().compareTo(Propositions.class.toString())==0)
            {
                Propositions p = (Propositions)anv;
                
                if (p.propositions.size()>0)
                {
                    AdresseValide a = p.propositions.get(0);
                    if (a.note>=note)
                    {
                        if (p.propositions.size()==1 || p.propositions.get(1).note<note)
                        {
                            AdresseGeocodee ag = null;
                            rejets.adresses.remove(i);
                            i--;
                            
                            try
                            {
                                if (config.decoupe)
                                    a.decoupe(service,config);
                            }
                            catch(Exception e)
                            {
                                erreurs.erreurs.add("Echec de découpage de l'adresse idvoie="+ag.idvoie+" ligne4="+ag.ligne4+" ligne6="+ag.ligne6+".");
                            }
                            
                            try
                            {
                                ag = a.geocode(service,config,false, false);
                                out.adresses.add(ag);
                            }
                            catch(Exception ex)
                            {
                                erreurs.erreurs.add("Echec de géocodage de l'adresse idvoie="+ag.idvoie+" ligne4="+ag.ligne4+" ligne6="+ag.ligne6+" tentative de géocodage à la commune.");
                                
                                try
                                {
                                    ag = a.geocode(service,config,true, false);
                                    out.adresses.add(ag);
                                }
                                catch(Exception exV)
                                {
                                    erreurs.erreurs.add("Echec de géocodage de l'adresse idvoie="+ag.idvoie+" ligne4="+ag.ligne4+" ligne6="+ag.ligne6+" à la commune.");
                                    try
                                    {
                                        ag = a.geocode(service, config, false, true);
                                        out.adresses.add(ag);
                                    }
                                    catch(Exception exP)
                                    {
                                        erreurs.erreurs.add("Echec de géocodage de l'adresse idvoie="+ag.idvoie+" ligne4="+ag.ligne4+" ligne6="+ag.ligne6+" au pays.");
                                    }
                                    out.adresses.add(a);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Crée les scripts SQL de création et d'insertion des résultats.
     */
    public void doit5(String rep,String tablename) throws UnsupportedEncodingException, FileNotFoundException, IOException, Exception
    {
        out.create(config,rep,tablename);
        out.writeSQL(rep,tablename,config);
    }
    
    /**
     * Géocode une adresse valide.
     * L'adresse est alors placée dans le fichier out, et les compteurs concernés mis à jour.
     * En cas d'erreur, une erreur est ajoutée dans le fichier d'erreur.
     * Un géocodage à la commune est tenté si le géocodage à la voie échoue.
     * @param ville pour forcer le géocodage à la ville
     * @param pays pour forcer le géocodage au pays
     */
    private boolean geocode(AdresseValide av, boolean ville, boolean pays)
    {
        try
        {
            AdresseGeocodee ag=av.geocode(service,config, ville, pays);
            ((InfoGeocodage) info).nb_adresse_geocodee[TypeGeocodage.getValue(ag.type)]++;
            
            try {
                //System.out.println("Découpe la voie !");
                ag.decoupe(service, config);
            } catch (Exception ex) {
                erreurs.erreurs.add("Erreur durant le découpage de " + ag.toString() + ".");
            }
            
            out.adresses.add(ag);
            return true;
        }
        catch(Exception ex)
        {
            if (config.geocodage==Geocodage.Voie && !ville && !pays)
            {
                erreurs.erreurs.add("Erreur durant le géocodage de "+av.toString()+". Un géocodage à la commune est tenté.");

                try
                {
                    AdresseGeocodee ag=av.geocode(service,config, true, false);
                    ((InfoGeocodage) info).nb_adresse_geocodee[TypeGeocodage.getValue(ag.type)]++;
                    
                    try {
                        ag.decoupe(service, config);
                    } catch (Exception exe) {
                        erreurs.erreurs.add("Erreur durant le découpage de " + ag.toString() + ".");
                    }
                    
                    out.adresses.add(ag);
                    return true;
                }
                catch(Exception ex2)
                {
                    erreurs.erreurs.add("Erreur durant le géocodage à la commune de "+av.toString()+" ("+ex2.getMessage()+").");
                    return false;
                }
            }
            else if(config.geocodage==Geocodage.Ville && !pays)
            {
                try
                {
                    AdresseGeocodee ag = av.geocode(service, config, false, true);
                    ((InfoGeocodage) info).nb_adresse_geocodee[TypeGeocodage.getValue(ag.type)]++;
                    try
                    {
                        ag.decoupe(service, config);
                    }
                    catch (Exception exe)
                    {
                        erreurs.erreurs.add("Erreur durant le découpage de " + ag.toString() + ".");
                    }

                    out.adresses.add(ag);
                    return true;
                }
                catch(Exception ex2)
                {
                    erreurs.erreurs.add("Erreur durant le géocodage au pays de "+av.toString()+" ("+ex2.getMessage()+").");
                    return false;
                }
            }
            else
            {
                erreurs.erreurs.add("Erreur durant le géocodage de "+av.toString()+" ("+ex.getMessage()+").");
                return false;
            }
        }
    }
    
    /**
     * Valide une adresse.
     * L'adresse est éventuellemenent géocodée (voir geocode), puis placée dans le fichier out.
     * En cas d'erreur ou de choix d'adresse, elle est placée dans le fichier rejets.
     * @param a l'adresse à valider
     * @param ville permet de forcer la validation à la ville
     * @param pays permet de forcer la validation au pays
     */
    private void valide(Adresse a, boolean ville, boolean pays)
    {
        RetourValidation rv=a.valide(service, config, ville, pays, dialogue.getButtonState()==EtatBoutonValide.Forcer?true:false);
        StatutValidation sv=rv.obtientStatut(config);

        if (sv==StatutValidation.valide)
        {
            info.nb_adresses_validees++;
            AdresseValide av=rv.obtientAdresseValide();
            info.total_notes_validees+=av.note;
            if (config.geocodage!=Geocodage.Aucun) // géocode si nécessaire
            {
                if (!geocode(av, ville, pays))
                {
                    try {
                        av.decoupe(service, config);
                    } catch (Exception ex) {
                        erreurs.erreurs.add("Erreur durant le découpage de " + av.toString() + ".");
                    }
                    
                    out.adresses.add(av);
                }
            }
            else
            {
                try {
                    av.decoupe(service, config);
                } catch (Exception ex) {
                    erreurs.erreurs.add("Erreur durant le découpage de " + av.toString() + ".");
                }
                
                out.adresses.add(av);
            }
        }
        else if (sv==StatutValidation.choix)
        {
            info.nb_adresses_non_validees++;
            rejets.adresses.add(rv.obtientPropositions());
        }
        else if (sv==StatutValidation.pb)
        {
            if (rv.codeerreur==0) info.nb_erreurs_grave++;
            info.nb_adresses_non_validees++;
            rejets.adresses.add(rv.obtientErreur());
        }
    }
    
    /**
     * Effectue le géocodage ou la validation du fichier en entrée.<br>
     * Lorsqu'un problème de géocodage survient, le géocodage à la commune est forcé.
     */
    public void doit() throws MalformedURLException, Exception
    {
        service = config.getService();
        
        long start = Calendar.getInstance().getTimeInMillis();

        int lastprogression = 0;       
        int size = in.adresses.size();

        int delta = 100;
        if (size>1000) delta = 10;
        if (size>10000) delta = 1;
        
        System.out.println("Progression : 0 %");
        for(int i=0;i<size;i++)
        {
            int progression = (i*100)/in.adresses.size();
            
            if (progression-lastprogression>=delta)
            {
                System.out.println("Progression : "+progression+" %");
                lastprogression = progression;
            }
            
            Adresse a = in.adresses.get(i);
            
            valide(a,config.geocodage==Geocodage.Ville, config.geocodage==Geocodage.Pays);
        }
        System.out.println("Progression : 100 %");
        
        long end = Calendar.getInstance().getTimeInMillis();
        info.duree += end-start;
        
        System.out.println("Durée : "+info.duree+" ms");
    }
    
    /**
     * Ecrit les fichiers en sortie.
     * @param repertoire
     */
    public boolean write(String repertoire) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        boolean errors = false;
        
        errors |= out.write(repertoire,config);
        errors |= rejets.write(repertoire);
        errors |= info.write(repertoire);
        errors |= erreurs.write(repertoire);
        
        return errors;
    }
    
    /**
     * Permet d'exécuter le validateur ou géocodeur sur un répertoire de travail.
     */
    public static void main(String[] args)
    {
        boolean error = false;
        
        System.out.println("JDONREFv2Geocodeur v"+version);
        
        if (args.length>0)
        {
            if (args[0].compareTo("scripts")==0 && args.length==3)
            {
                try
                {
                    JDONREFv2Geocodeur geocodeur=new JDONREFv2Geocodeur();
                    geocodeur.loadAll(args[1]);
                    geocodeur.doit5(args[1],args[2]);
                }
                catch(JDOMException ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(IOException ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(Exception ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
            else if (args[0].compareTo("valide")==0 && args.length==2)
            {
                try
                {
                    JDONREFv2Geocodeur geocodeur=new JDONREFv2Geocodeur();
                    geocodeur.load(args[1]);
                    geocodeur.doit();
                    geocodeur.write(args[1]);
                }
                catch(JDOMException ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(IOException ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(Exception ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
            else if (args[0].compareTo("reprend")==0 && args.length==2)
            {
                try
                {
                    JDONREFv2Geocodeur geocodeur=new JDONREFv2Geocodeur();
                    geocodeur.repertoire = args[1];
                    geocodeur.loadAll(args[1]);
                    geocodeur.doit2();
                }
                catch(JDOMException ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(IOException ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(Exception ex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
            else if (args[0].compareTo("echec")==0 && args.length==2)
            {
                try
                {
                    JDONREFv2Geocodeur geocodeur = new JDONREFv2Geocodeur();
                    geocodeur.repertoire = args[1];
                    geocodeur.loadAll(args[1]);
                    geocodeur.doit3();
                    geocodeur.write(args[1]);
                }
                catch(JDOMException jex)
                {
                    error = true;
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,jex);
                }
                catch(IOException ex)
                {
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
                catch(Exception ex)
                {
                    Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
            else if (args[0].compareTo("note")==0 && args.length==3)
            {
                int note=200;

                try
                {
                    note=Integer.parseInt(args[2]);
                }
                catch(NumberFormatException nfe)
                {
                    error=true;
                }

                if (!error)
                {
                    try
                    {
                        JDONREFv2Geocodeur geocodeur=new JDONREFv2Geocodeur();
                        geocodeur.repertoire=args[1];
                        geocodeur.loadAll(args[1]);
                        geocodeur.doit4(note);
                        geocodeur.write(args[1]);
                    }
                    catch(JDOMException jex)
                    {
                        error=true;
                        Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,jex);
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                    }
                    catch(Exception ex)
                    {
                        Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
                    }
                }
            }
            else
                error = true;
        }
        else
            error = true;
        
        if (error)
        {
            System.out.println("Syntaxe: ");
            System.out.println("  valide repertoire-de-travail");
            System.out.println("  reprend repertoire-de-travail");
            System.out.println("  echec repertoire-de-travail");
            System.out.println("  note repertoire-de-travail nouvelleNoteSur200");
            System.out.println("  scripts repertoire-de-travail nomDeLaTable");
        }
    }

    /**
     * Effectue une action suite au clic sur un bouton.
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        // On saute l'adresse
        if (action.compareTo("suivant")==0)
        {
            int index = dialogue.getIndex();
            if (index++<rejets.adresses.size())
            {
                dialogue.setIndex(index);
                
                // Cas particulier des propositions
                AdresseNonValide anv  = rejets.adresses.get(index-1);
                setPropositions(anv);
                dialogue.setAdresse(anv);
            }
            else
            {
                System.out.println("Traitement terminé");
                dialogue.dispose();
                close();
            }
        }
        else if (action.compareTo("restructure")==0)
        {
            // constuit l'adresse à partir de la saisie
            String[] saisie=dialogue.getSaisie();
            int index=dialogue.getIndex();
            Adresse a=(Adresse) rejets.adresses.get(index-1).clone();
            a.ligne2=saisie[0];
            a.ligne3=saisie[1];
            a.ligne4=saisie[2];
            a.ligne5=saisie[3];
            a.ligne6=saisie[4];
            if( (saisie.length > 5) && (saisie[5] != null) && (saisie[5].length()>0))
                a.ligne7 = saisie[5];
            
            try
            {
                // et la restructure
                a.normalise(service,config, config.gererPays ? 256+7 : 7);
            }
            catch(Exception ex)
            {
                Erreur er = new Erreur(a);
                er.message = ex.getMessage();
                
                setPropositions(null);
                dialogue.setAdresse(er);
                return ;
            }
            
            if (dialogue.getButtonState()==EtatBoutonValide.Forcer)
                dialogue.setButtonState(EtatBoutonValide.Valider);
            if (dialogue.getState()==3)
                dialogue.setState(0);
        
            dialogue.setAdresse(a);
            
            dialogue.setChoisir(false);
        }
        // Validation de l'adresse
        else if (action.compareTo("valide")==0)
        {
            // constuit l'adresse à partir de la saisie
            String[] saisie=dialogue.getSaisie();
            int index=dialogue.getIndex();
            Adresse a=(Adresse) rejets.adresses.get(index-1).clone();
            a.ligne2=saisie[0];
            a.ligne3=saisie[1];
            a.ligne4=saisie[2];
            a.ligne5=saisie[3];
            a.ligne6=saisie[4];
            if( (saisie.length > 5) && (saisie[5]!=null) && (saisie[5].length()>0) )
                a.ligne7 = saisie[5];
            
            // et la valide (exception pour le cas où une validation à la voie a échoué).
            RetourValidation rv=a.valide(service,config,
                    dialogue.getEtat()==3?true:false,
                    dialogue.getEtat()==6 ? true : false,
                    dialogue.getButtonState()==EtatBoutonValide.Forcer?true:false);
                    
            // analyse du résultat
            StatutValidation sv=rv.obtientStatut(config);
            
            if (sv==StatutValidation.choix||sv==StatutValidation.valide)
            {
                setPropositions(rv.obtientPropositions());
                dialogue.setAdresse(propositions);
            }
            else
            {
                setPropositions(null);
                dialogue.setAdresse(rv.obtientErreur());
            }
            
            if (dialogue.getButtonState()==EtatBoutonValide.Forcer)
                dialogue.setButtonState(EtatBoutonValide.Valider);
            else if (rv.etat==3 || rv.etat==1)
                dialogue.setButtonState(EtatBoutonValide.Forcer);
            
            dialogue.setChoisir(true);
        }
        // Retour à la case départ
        else if (action.compareTo("reset")==0)
        {
            int index = dialogue.getIndex();
            setPropositions(rejets.adresses.get(index-1).clone());
            dialogue.setAdresse(rejets.adresses.get(index-1).clone());
        }
        // Une des adresses est choisie
        else if (action.compareTo("choisir")==0)
        {
            int indexChoix = dialogue.getChoix();
            int index = dialogue.getIndex();
            
            if (indexChoix!=-1 && propositions!=null)
            {
                AdresseValide av=propositions.propositions.get(indexChoix);
                
                switch(dialogue.getEtat())
                {
                    // S'il s'agit d'une validation de voie ou de commune
                    // un géocodage est tenté.
                    case 0:
                    case 1:
                        // Tentative de géocodage de l'adresse (voie ou commune)
                        if (!geocode(av,false, false))
                        {
                            try
                            {
                                av.decoupe(service,config);
                            }
                            catch(Exception ex)
                            {
                                erreurs.erreurs.add("Erreur durant le découpage de "+av.toString()+".");
                            }
                            
                            out.adresses.add(av);
                        }
                        
                        // Mise à jour des compteurs
                        info.nb_adresses_validees++;
                        info.total_notes_validees+=av.note;
                        info.nb_adresses_non_validees--;
                        
                        // Suppression des rejets
                        rejets.adresses.remove(index-1);
                        
                        // Puis initialisation de la fenêtre vers la voie suivante.
                        if (index-1<rejets.adresses.size())
                        {
                            setPropositions(rejets.adresses.get(index-1));
                            dialogue.setMax(rejets.adresses.size());
                            dialogue.setAdresse(rejets.adresses.get(index-1));
                        }
                        else // ou fermeture
                        {
                            System.out.println("Traitement terminé");
                            info.start = 0;
                            dialogue.dispose();
                            close();
                        }
                        break;
                        
                    // S'il s'agit d'un choix de département
                    case 2:                       
                        // Construit l'adresse à partir du choix effectué
                        String[] saisie=dialogue.getSaisie();
                        Adresse p = (Adresse) rejets.adresses.get(index-1).clone();
                        p.ligne2 = saisie[0];
                        p.ligne3 = saisie[1];
                        p.ligne4 = saisie[2];
                        p.ligne5 = saisie[3];
                        p.ligne6 = av.ligne6valide;
                        
                        // Valide la voie,
                        RetourValidation rv = p.valide(service,config,
                                false, false,
                                dialogue.getButtonState()==EtatBoutonValide.Forcer?true:false);
                        
                        // Analyse le retour de la validation
                        StatutValidation sv = rv.obtientStatut(config);
                        
                        // 
                        if (sv==StatutValidation.choix||sv==StatutValidation.valide)
                        {
                            setPropositions(rv.obtientPropositions());
                            dialogue.setAdresse(propositions);
                        }
                        else
                        {
                            setPropositions(null);
                            dialogue.setAdresse(rv.obtientErreur());
                        }
                        break;

                    case 3:
                        // Tentative de géocodage de la commune (forcée)
                        if (!geocode(av,true, false))
                        {
                            try
                            {
                                av.decoupe(service,config);
                            }
                            catch(Exception ex)
                            {
                                erreurs.erreurs.add("Erreur durant le découpage de "+av.toString()+".");
                            }
                            
                            out.adresses.add(av);
                        }
                        
                        // Mise à jour des compteurs
                        info.nb_adresses_validees++;
                        info.total_notes_validees+=av.note;
                        info.nb_adresses_non_validees--;
                        
                        // Suppression des rejets
                        rejets.adresses.remove(index-1);
                        
                        // Puis initialisation de la fenêtre vers la voie suivante.
                        if (index-1<rejets.adresses.size())
                        {
                            dialogue.setMax(rejets.adresses.size());
                            setPropositions(rejets.adresses.get(index-1));
                            dialogue.setAdresse(rejets.adresses.get(index-1));
                        }
                        else // ou fermeture
                        {
                            System.out.println("Traitement terminé");
                            dialogue.dispose();
                            close();
                        }
                        break;
                    case 5:
                    {   // Validation manuelle pays
                        if (!geocode(av,false, true))
                        {
                            try
                            {
                                av.decoupe(service,config);
                            }
                            catch(Exception ex)
                            {
                                erreurs.erreurs.add("Erreur durant le découpage de "+av.toString()+".");
                            }

                            out.adresses.add(av);
                        }

                        // Mise à jour des compteurs
                        info.nb_adresses_validees++;
                        info.total_notes_validees+=av.note;
                        info.nb_adresses_non_validees--;

                        // Suppression des rejets
                        rejets.adresses.remove(index-1);

                        // Puis initialisation de la fenêtre vers la voie suivante.
                        if (index-1<rejets.adresses.size())
                        {
                            setPropositions(rejets.adresses.get(index-1));
                            dialogue.setMax(rejets.adresses.size());
                            dialogue.setAdresse(rejets.adresses.get(index-1));
                        }
                        else // ou fermeture
                        {
                            System.out.println("Traitement terminé");
                            info.start = 0;
                            dialogue.dispose();
                            close();
                        }
                        break;
                    }
                    case 6:
                    {   // Validation pays après echec commune
                        // Tentative de géocodage au pays
                        if (!geocode(av,false, true))
                        {
                            try
                            {
                                av.decoupe(service,config);
                            }
                            catch(Exception ex)
                            {
                                erreurs.erreurs.add("Erreur durant le découpage de "+av.toString()+".");
                            }

                            out.adresses.add(av);
                        }

                        // Mise à jour des compteurs
                        info.nb_adresses_validees++;
                        info.total_notes_validees+=av.note;
                        info.nb_adresses_non_validees--;

                        // Suppression des rejets
                        rejets.adresses.remove(index-1);

                        // Puis initialisation de la fenêtre vers la voie suivante.
                        if (index-1<rejets.adresses.size())
                        {
                            dialogue.setMax(rejets.adresses.size());
                            setPropositions(rejets.adresses.get(index-1));
                            dialogue.setAdresse(rejets.adresses.get(index-1));
                        }
                        else // ou fermeture
                        {
                            System.out.println("Traitement terminé");
                            dialogue.dispose();
                            close();
                        }
                        break;
                    }
                }
                dialogue.setEtat(0);
            }
        }
        else if (action.compareTo("echec")==0)
        {
            int index=dialogue.getIndex();
            
            switch(dialogue.getEtat())
            {
                // S'il s'agissait d'une validation à la voie, une validation à la commune est tentée.
                case 0:
                    dialogue.setEtat(3);
                    
                    // Construit l'adresse à partir de la saisie
                    String[] saisie=dialogue.getSaisie();
                    
                    Adresse a=(Adresse) rejets.adresses.get(index-1).clone();
                    a.ligne2=saisie[0];
                    a.ligne3=saisie[1];
                    a.ligne4=saisie[2];
                    a.ligne5=saisie[3];
                    a.ligne6=saisie[4];
                    
                    // Analyse l'état de la validation
                    RetourValidation rv = a.valide(service,config,
                            true, false,
                            dialogue.getButtonState()==EtatBoutonValide.Forcer?true:false);
                    StatutValidation sv = rv.obtientStatut(config);
                    
                    // 
                    if (sv==StatutValidation.choix || sv==StatutValidation.valide)
                    {
                        setPropositions(rv.obtientPropositions());
                        dialogue.setAdresse(propositions);
                    }
                    else
                    {
                        propositions = null;
                        dialogue.setAdresse(rv.obtientErreur());
                    }
                    break;
                    
                // Dans ce cas, rien ne peut être fait.
                case 1:
                case 2:
                case 3:
                    Adresse rejet = new Adresse(rejets.adresses.get(index-1));
                    rejets.adresses.remove(index-1);
                    rejet.config = config;
                    try
                    {
                        rejet.decoupe(service,config);
                    }
                    catch(Exception ex)
                    {
                        erreurs.erreurs.add("Erreur durant le découpage de "+rejet.toString()+".");
                    }
                    out.adresses.add(rejet);

                    // Puis initialisation de la fenêtre vers la voie suivante.
                    if (index-1<rejets.adresses.size())
                    {
                        setPropositions(rejets.adresses.get(index-1));
                        dialogue.setMax(rejets.adresses.size());
                        dialogue.setAdresse(rejets.adresses.get(index-1));
                    }
                    else // ou fermeture
                    {
                        System.out.println("Traitement terminé");
                        dialogue.dispose();
                        close();
                    }
                    break;
            }
        }
    }

    /**
     * Termine la sessions de reprise manuelle.
     */
    private void close()
    {
        try
        {
            info.start = dialogue.getIndex()-1;
            write(repertoire);
        }
        catch(FileNotFoundException ex)
        {
            Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(UnsupportedEncodingException ex)
        {
            Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(IOException ex)
        {
            Logger.getLogger(JDONREFv2Geocodeur.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    
    /**
     * Effectue une action suite à la saisie dans une zone de saisie.
     * @param e
     */
    public void caretUpdate(CaretEvent e)
    {
        if (dialogue.getButtonState()==EtatBoutonValide.Forcer)
            dialogue.setButtonState(EtatBoutonValide.Valider);
        if (dialogue.getState()==3)
            dialogue.setState(0);
        
        dialogue.setChoisir(false);
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        close();
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {   
    }

    public void windowDeiconified(WindowEvent e)
    {   
    }

    public void windowActivated(WindowEvent e)
    {   
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
}