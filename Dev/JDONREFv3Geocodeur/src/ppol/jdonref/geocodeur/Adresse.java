package ppol.jdonref.geocodeur;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import ppol.jdonref.JDONREF;
import ppol.jdonref.JDONREFService;
import ppol.jdonref.PropositionDecoupage;
import ppol.jdonref.PropositionValidation;
import ppol.jdonref.ResultatDecoupage;
import ppol.jdonref.ResultatNormalisation;
import ppol.jdonref.ResultatValidation;

/**
 * Représente une adresse.
 * @author jmoquet
 */
public class Adresse
{
    public String id;
    public String ligne1;
    public String ligne2 = "";
    public String ligne3 = "";
    public String ligne4;
    public String ligne5 = "";
    public String ligne6;
    public String ligne7;
    
    public boolean decoupe = false;
    
    public String firstNumber;
    public String firstRep;
    public String[] otherNumbers;
    public String typedevoie;
    public String article;
    public String libelle;
    public String motdeterminant;
    public String codedepartement;
    public String codepostal;
    public String commune;
    public String arrondissement;
    public String cedex;
    public String codecedex;
    public String pays;
    
    public Config config = null;
    
    public Adresse()
    {
    }
    
    public Adresse(Adresse a)
    {
        this.id = a.id;
        this.ligne1 = a.ligne1;
        this.ligne2 = a.ligne2;
        this.ligne3 = a.ligne3;
        this.ligne4 = a.ligne4;
        this.ligne5 = a.ligne5;
        this.ligne6 = a.ligne6;
        ligne7 = a.ligne7;
    }
    
    @Override
    public Adresse clone()
    {
        Adresse res = new Adresse();
        res.id = id;
        res.ligne1 = ligne1;
        res.ligne2 = ligne2;
        res.ligne3 = ligne3;
        res.ligne4 = ligne4;
        res.ligne5 = ligne5;
        res.ligne6 = ligne6;
        res.ligne7 = ligne7;
        res.config = config;
        return res;
    }
    
    /**
     * Permet de charger une adresse à partir de sa représentation XML.
     * @param e
     */
    public void load(Element e)
    {
        Element e_id = e.getChild("id");
        Element e_ligne1 = e.getChild("ligne1");
        Element e_ligne2 = e.getChild("ligne2");
        Element e_ligne3 = e.getChild("ligne3");
        Element e_ligne4 = e.getChild("ligne4");
        Element e_ligne5 = e.getChild("ligne5");
        Element e_ligne6 = e.getChild("ligne6");
        Element e_ligne7 = e.getChild("ligne7");
        
        id = e_id.getValue();
        ligne1 = e_ligne1.getValue();
        ligne2 = e_ligne2.getValue();
        ligne3 = e_ligne3.getValue();
        ligne4 = e_ligne4.getValue();
        ligne5 = e_ligne5.getValue();
        ligne6 = e_ligne6.getValue();
        ligne7 = (e_ligne7 == null) ? null : e_ligne7.getValue();
        
        if (config.decoupe)
        {
            Element e_firstNumber = e.getChild("firstNumber");
            Element e_firstRep = e.getChild("firstRep");
            Element e_otherNumbers = e.getChild("otherNumbers");
            Element e_typedevoie = e.getChild("typedevoie");
            Element e_article = e.getChild("article");
            Element e_libelle = e.getChild("libelle");
            Element e_motdeterminant = e.getChild("motdeterminant");
            Element e_codedepartement = e.getChild("codedepartement");
            Element e_codepostal = e.getChild("codepostal");
            Element e_commune = e.getChild("commune");
            Element e_arrondissement = e.getChild("arrondissement");
            Element e_cedex = e.getChild("cedex");
            Element e_codecedex = e.getChild("codecedex");
            Element e_pays = e.getChild("pays");

            firstNumber = e_firstNumber.getValue();
            firstRep = e_firstRep.getValue();
            otherNumbers = e_otherNumbers.getValue().split(";");
            typedevoie = e_typedevoie.getValue();
            article = e_article.getValue();
            libelle = e_libelle.getValue();
            motdeterminant = e_motdeterminant.getValue();
            codedepartement = e_codedepartement.getValue();
            codepostal = e_codepostal.getValue();
            commune = e_commune.getValue();
            arrondissement = e_arrondissement.getValue();
            cedex = e_cedex.getValue();
            codecedex = e_codecedex.getValue();
            pays = (e_pays == null)? null : e_pays.getValue();
            
            if (firstNumber.length()>0 || firstRep.length()>0 || otherNumbers.length>0 || typedevoie.length()>0 || article.length()>0 ||
                    libelle.length()>0 || motdeterminant.length()>0 || codedepartement.length()>0 || codepostal.length()>0 || commune.length()>0 ||
                    arrondissement.length()>0 || cedex.length()>0 || codecedex.length()>0 || (pays!=null) ) {
                decoupe = true;
            } else {
                decoupe = false;
            }
        }
    }
    
    /**
     * Ajoute la représentation xml de la classe à l'arborescence xml spécifiée.
     * @param adresses
     */
    public Element toXml()
    {
        Element e_adresse = new Element("adresse");
        
        Element e_id = new Element("id");
        Element e_ligne1 = new Element("ligne1");
        Element e_ligne2 = new Element("ligne2");
        Element e_ligne3 = new Element("ligne3");
        Element e_ligne4 = new Element("ligne4");
        Element e_ligne5 = new Element("ligne5");
        Element e_ligne6 = new Element("ligne6");
        Element e_ligne7 = null;
        if( (ligne7 != null) && (ligne7.length() > 0))
            e_ligne7 = new Element("ligne7");
        
        e_id.addContent(id);
        e_ligne1.addContent(ligne1);
        e_ligne2.addContent(ligne2);
        e_ligne3.addContent(ligne3);
        e_ligne4.addContent(ligne4);
        e_ligne5.addContent(ligne5);
        e_ligne6.addContent(ligne6);
        if(e_ligne7 != null)
            e_ligne7.addContent(ligne7);
        
        e_adresse.addContent(e_id);
        e_adresse.addContent(e_ligne1);
        e_adresse.addContent(e_ligne2);
        e_adresse.addContent(e_ligne3);
        e_adresse.addContent(e_ligne4);
        e_adresse.addContent(e_ligne5);
        e_adresse.addContent(e_ligne6);
        if(e_ligne7 != null)
            e_adresse.addContent(e_ligne7);
        
        if (config.decoupe)
        {
            Element e_firstNumber = new Element("firstNumber");
            Element e_firstRep = new Element("firstRep");
            Element e_otherNumbers = new Element("otherNumbers");
            Element e_typedevoie = new Element("typedevoie");
            Element e_article = new Element("article");
            Element e_libelle = new Element("libelle");
            Element e_motdeterminant = new Element("motdeterminant");
            Element e_codedepartement = new Element("codedepartement");
            Element e_codepostal = new Element("codepostal");
            Element e_commune = new Element("commune");
            Element e_arrondissement = new Element("arrondissement");
            Element e_cedex = new Element("cedex");
            Element e_codecedex = new Element("codecedex");
            Element e_pays = null;
            if(pays != null)
                e_pays = new Element("pays");

            if (decoupe)
            {
                e_firstNumber.addContent(firstNumber);
                e_firstRep.addContent(firstRep);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < otherNumbers.length; i++) {
                    if (i > 0) {
                        sb.append(";");
                    }
                    sb.append(otherNumbers[i]);
                }
                e_otherNumbers.addContent(sb.toString());
                e_typedevoie.addContent(typedevoie);
                e_article.addContent(article);
                e_libelle.addContent(libelle);
                e_motdeterminant.addContent(motdeterminant);
                e_codedepartement.addContent(codedepartement);
                e_codepostal.addContent(codepostal);
                e_commune.addContent(commune);
                e_arrondissement.addContent(arrondissement);
                e_cedex.addContent(cedex);
                e_codecedex.addContent(codecedex);
                if(e_pays != null)
                    e_pays.addContent(pays);
            }

            e_adresse.addContent(e_firstNumber);
            e_adresse.addContent(e_firstRep);
            e_adresse.addContent(e_otherNumbers);
            e_adresse.addContent(e_typedevoie);
            e_adresse.addContent(e_article);
            e_adresse.addContent(e_libelle);
            e_adresse.addContent(e_motdeterminant);
            e_adresse.addContent(e_codedepartement);
            e_adresse.addContent(e_codepostal);
            e_adresse.addContent(e_commune);
            e_adresse.addContent(e_arrondissement);
            e_adresse.addContent(e_cedex);
            e_adresse.addContent(e_codecedex);
            if(e_pays != null)
                e_adresse.addContent(e_pays);
        }
        
        return e_adresse;
    }
    
    /**
     * Découpe 
     * 
     * @param service
     * @param config
     * @param natures
     * @param numeros
     */
    public void decoupe(JDONREFService service,Config config) throws Exception
    {
        JDONREF port = service.getJDONREFPort();
        
        ArrayList<String> lignes=new ArrayList<String>();
        lignes.add(ligne1);
        lignes.add(ligne2);
        lignes.add(ligne3);
        lignes.add(ligne4);
        lignes.add(ligne5);
        lignes.add(ligne6);
        if(ligne7 != null)
            lignes.add(ligne7);
        
        ArrayList<Integer> l_natures = new ArrayList<Integer>();
        for(int i=1;i<=32768;i*=2)
        {
            l_natures.add(i);
        }
        ArrayList<Integer> l_numeros = new ArrayList<Integer>();
        for(int i=1;i<=6;i++)
        {
            l_numeros.add(i);
        }
        if(ligne7 != null)
        {
            l_natures.add(2^16);
            l_numeros.add(7);
        }
        
        ArrayList<Integer> l_services = new ArrayList<Integer>();
        l_services.add(1); // JADRREF automatique
        
        ArrayList<String> options = new ArrayList<String>();
        
        ResultatDecoupage res = port.decoupe(config.application, l_services, l_natures, lignes, options);
        //List<String> res = port.decoupe(config.application, lignes, l_natures, l_numeros); // JDONREFv2
        
        if (res.getCodeRetour()==0)
            throw (new Exception("Problème durant le découpage "+res.getErreurs().get(0).getMessage()));
        
        PropositionDecoupage prop = res.getPropositions().get(0);
        List<String> donnees = prop.getDonnees();
        
        decoupe = true;
        firstNumber = donnees.get(0);
        firstRep = donnees.get(1);
        otherNumbers = donnees.get(2).split(";");
        typedevoie = donnees.get(3);
        article = donnees.get(4);
        libelle = donnees.get(5);
        motdeterminant = donnees.get(6);
        codedepartement = donnees.get(7);
        codepostal = donnees.get(8);
        commune = donnees.get(9);
        arrondissement = donnees.get(10);
        cedex = donnees.get(11);
        codecedex = donnees.get(12);
        if(ligne7 != null)
            pays = donnees.get(13);
    }
    
    /**
     * Normalise l'adresse donnée avec le service JDONREF spécifié, en forçant l'opération.
     */
    public void normalise(JDONREFService service,Config config,int operation) throws Exception
    {
            JDONREF port=service.getJDONREFPort();
            
            ArrayList<String> lignes=new ArrayList<String>();
            lignes.add(ligne1);
            lignes.add(ligne2);
            lignes.add(ligne3);
            lignes.add(ligne4);
            lignes.add(ligne5);
            lignes.add(ligne6);
            if(ligne7 != null)
                lignes.add(ligne7);
            
            //System.out.println("Operation : "+operation);
            
            ArrayList<Integer> l_services = new ArrayList<Integer>();
            l_services.add(1); // JADRREF automatique
            
            ArrayList<String> options = new ArrayList<String>();
            
            ResultatNormalisation res = port.normalise(config.application,l_services,operation,lignes,options);
            //List<String> res=port.normalise(config.application,operation,lignes,null); // JDONREFv2
            
            if (res.getCodeRetour()==0)
            {
                throw (new Exception("Problème durant la normalisation "+res.getErreurs().get(0).getMessage()));
            }
            
            List<String> donnees = res.getPropositions().get(0).getDonnees();
            
            ligne1=donnees.get(0);
            ligne2=donnees.get(1);
            ligne3=donnees.get(2);
            ligne4=donnees.get(3);
            ligne5=donnees.get(4);
            ligne6=donnees.get(5);
            if((config.gererPays) || (ligne7 != null) )
                ligne7 = donnees.get(6);
    }
    
    /**
     * Normalise l'adresse donnée avec le service JDONREF spécifié.
     */
    public void normalise(JDONREFService service,Config config) throws Exception
    {
        if (config.normalise || config.restructure)
        {
            JDONREF port=service.getJDONREFPort();

            ArrayList<String> lignes=new ArrayList<String>();
            lignes.add(ligne1);
            lignes.add(ligne2);
            lignes.add(ligne3);
            lignes.add(ligne4);
            lignes.add(ligne5);
            lignes.add(ligne6);
            if(ligne7 != null)
                lignes.add(ligne7);

            int operation=0;
            if (config.normalise)
                operation+=1+4;
            if (config.restructure)
            {
                operation+=2;
                if(config.gererPays)
                    operation += 256;
            }
            
            //System.out.println("Operation : "+operation);

            ArrayList<Integer> l_services = new ArrayList<Integer>();
            l_services.add(1); // JADRREF automatique
            
            ArrayList<String> options = new ArrayList<String>();
            
            ResultatNormalisation res = port.normalise(config.application,l_services,operation,lignes,options);
            //List<String> res=port.normalise(config.application,operation,lignes,null); // JDONREF v2

            if (res.getCodeRetour()==0)
            {
                throw (new Exception("Problème durant la normalisation "+res.getErreurs().get(0).getMessage()));
            }
            
            List<String> donnees = res.getPropositions().get(0).getDonnees();
            
            ligne1=donnees.get(0);
            ligne2=donnees.get(1);
            ligne3=donnees.get(2);
            ligne4=donnees.get(3);
            ligne5=donnees.get(4);
            ligne6=donnees.get(5);
            if((config.gererPays) || (ligne7 != null) )
                ligne7 = donnees.get(6);
        }
    }
    
    /**
     * Valide l'adresse donnée avec le service JDONREF spécifié.
     * @param ville pour forcer la validation à la ville.
     * @param pays pour forcer la validation au pays.
     * @param force pour forcer la validation orthographique.
     */
    public RetourValidation valide(JDONREFService service,Config config,boolean ville, boolean pays, boolean force)
    {
        RetourValidation retour = new RetourValidation(this);
        
        JDONREF port = service.getJDONREFPort();
        
        List<String> lignes = new ArrayList<String>();

        if(pays)
        {
            lignes.add(""); lignes.add(""); lignes.add(""); lignes.add(""); lignes.add(""); lignes.add("");
            lignes.add(ligne7);
        }
        else
        {
            lignes.add(ligne1);
            lignes.add(ligne2);
            lignes.add(ligne3);
            if (!ville)
                lignes.add(ligne4);
            else
                lignes.add("");
            lignes.add(ligne5);
            lignes.add(ligne6);
            if(ligne7 != null)
                lignes.add(ligne7);
        }

        int operation = 0;
        if (config.normalise)
            operation+=1+4;
        if (config.restructure)
            operation+=2;
        if(config.gererPays)
            operation += 256;

        try
        {
            ArrayList<Integer> l_services = new ArrayList<Integer>();
            l_services.add(1); // JADRREF automatique
            
            ArrayList<String> ids = new ArrayList<String>();
            for(int i=0;i<7;i++)
                ids.add("");
            
            ArrayList<String> options = new ArrayList<String>();
            
            ResultatValidation res = port.valide(config.application, l_services, operation, lignes, ids, options);
            //List<String> res=port.valide(config.application,operation,lignes,null,force,null); // JDONREFv2

            retour.etat=res.getCodeRetour();
            int offset = ( (ligne7 != null) || (config.gererPays)) ? 1 : 0;

            if (retour.etat==0)
            {
                retour.codeerreur=res.getCodeRetour();
                retour.message=res.getErreurs().get(0).getMessage();
            }
            else if (retour.etat==1||retour.etat==2)
            {
                retour.adresse=this.clone();

                int size=res.getPropositions().size();
                for(int i=0; i<size; i++)
                {
                    /* <ul><li>1 ou 2 selon que la recherche est exacte ou non</li>
                     *     <li>Nombre de résultats</li>
                     *     <li>ligne1</li>
                     *     <li>ligne2</li>
                     *     <li>ligne3</li>
                     *     <li>ligne5</li>
                     *     <li>ligne7</li> si pays
                     *     <li>Identifiant de la voie 1</li>
                     *     <li>ligne4 1</li>
                     *     <li>ligne4 desabbreviée 1</li>
                     *     <li>code insee 1</li>
                     *     <li>ligne6 1</li>
                     *     <li>ligne6 désabbreviée 1</li>
                     *     <li>t0 1 sous la forme JJ/MM/AA</li>
                     *     <li>t1 1 sous la forme JJ/MM/AA</li>
                     *     <li>distance trouvée 1</li>
                     *     <li>code fantoir 1</li>
                     *     <li>Identifiant de la voie 2</li>
                     *     <li>nom de la voie 2</li>
                     *     <li>...</li></ul>
                     * </ul>
                     */
                    PropositionValidation pv = res.getPropositions().get(i);
                    
                    AdresseValide adresse=new AdresseValide(this);
                    adresse.idvoie=pv.getIds().get(3); //res.get(6+offset+10*i);
                    adresse.codeinsee=pv.getIds().get(5); //res.get(9+offset+10*i);
                    adresse.ligne1valide=pv.getDonnees().get(0); //res.get(2);
                    adresse.ligne2valide=pv.getDonnees().get(1); //res.get(3);
                    adresse.ligne3valide=pv.getDonnees().get(2); //res.get(4);
                    adresse.ligne5valide=pv.getDonnees().get(4); //res.get(5);
                    if( (ligne7 != null) || (config.restructure && config.gererPays))
                        adresse.ligne7valide=pv.getDonnees().get(6); //res.get(6);
                    adresse.ligne4valide=pv.getDonnees().get(3); //res.get(8+offset+10*i);
                    adresse.ligne6valide=pv.getDonnees().get(5); //res.get(11+offset+10*i);
                    adresse.note=Integer.parseInt(pv.getNote()); //Integer.parseInt(res.get(14+offset+10*i));
                    adresse.t0=pv.getT0(); //res.get(12+offset+10*i);
                    adresse.t1=pv.getT1(); //res.get(13+offset+10*i);
                    retour.adresses.add(adresse);
                }
            }
            else if (retour.etat==3||retour.etat==4)
            {
                retour.adresse=this;
                int size=res.getPropositions().size();
                for(int i=0; i<size; i++)
                {
                    /* <ul><li>3 ou 4 selon que la recherche est exacte ou non</li>
                     *     <li>Nombre de communes retournées</li>
                     *     <li>ligne1</li>
                     *     <li>ligne2</li>
                     *     <li>ligne3</li>
                     *     <li>ligne4</li>
                     *     <li>ligne5</li>
                     *     <li>ligne7</li> si pays
                     *     <li>CodeInsee 1</li>
                     *     <li>ligne6 1</li>
                     *     <li>ligne6 desabbrevie 1</li>
                     *     <li>Note sur 20 1</li>
                     *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
                     *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
                     *     <li>CodeInsee 2</li>
                     *     <li>lign6 2</li>
                     *     <li>...</li></ul>
                     */
                    PropositionValidation pv = res.getPropositions().get(i);
                    
                    AdresseValide adresse=new AdresseValide(this);
                    adresse.codeinsee=pv.getIds().get(5); //res.get(9+offset+10*i);
                    adresse.ligne1valide=pv.getDonnees().get(0); //res.get(2);
                    adresse.ligne2valide=pv.getDonnees().get(1); //res.get(3);
                    adresse.ligne3valide=pv.getDonnees().get(2); //res.get(4);
                    adresse.ligne5valide=pv.getDonnees().get(4); //res.get(5);
                    if( (ligne7 != null) || (config.restructure && config.gererPays))
                        adresse.ligne7valide=pv.getDonnees().get(6); //res.get(6);
                    adresse.ligne4valide=pv.getDonnees().get(3); //res.get(8+offset+10*i);
                    adresse.ligne6valide=pv.getDonnees().get(5); //res.get(11+offset+10*i);
                    adresse.note=Integer.parseInt(pv.getNote()); //Integer.parseInt(res.get(14+offset+10*i));
                    adresse.t0=pv.getT0(); //res.get(12+offset+10*i);
                    adresse.t1=pv.getT1(); //res.get(13+offset+10*i);
                    retour.adresses.add(adresse);
                }
            }
            else if (retour.etat==5||retour.etat==6)
            {
                retour.adresse=this;
                int size=res.getPropositions().size();
                for(int i=0; i<size; i++)
                {
                    /* <ul><li>5 ou 6 selon que la recherche est exacte ou non</li>
                     *     <li>Nombre de pays retournées</li>
                     *     <li>ligne1</li>
                     *     <li>ligne2</li>
                     *     <li>ligne3</li>
                     *     <li>ligne4</li>
                     *     <li>ligne5</li>
                     *     <li>ligne6</li> si pays
                     *     <li>Code Sov Ac3 1</li>
                     *     <li>ligne7 1</li>
                     *     <li>ligne7 desabbrevie 1</li>
                     *     <li>Note sur 20 1</li>
                     *     <li>t0 1 sous la forme JJ/MM/AAAA</li>
                     *     <li>t1 1 sous la forme JJ/MM/AAAA</li>
                     *     <li>Code Sov Ac3 2</li>
                     *     <li>lign7 2</li>
                     *     <li>...</li></ul>
                     */
                    
                    PropositionValidation pv = res.getPropositions().get(i);
                    
                    AdresseValide adresse=new AdresseValide(this);
                    adresse.ligne1valide=pv.getDonnees().get(0); //res.get(2);
                    adresse.ligne2valide=pv.getDonnees().get(1); //res.get(3);
                    adresse.ligne3valide=pv.getDonnees().get(2); //res.get(4);
                    adresse.ligne5valide=pv.getDonnees().get(4); //res.get(5);
                    adresse.ligne4valide=pv.getDonnees().get(3); //res.get(8+offset+10*i);
                    adresse.ligne6valide=pv.getDonnees().get(5); //res.get(11+offset+10*i);
                    adresse.ligne7valide=pv.getDonnees().get(6); //res.get(11+offset+10*i);
                    adresse.note=Integer.parseInt(pv.getNote()); //Integer.parseInt(res.get(14+offset+10*i));
                    adresse.t0=pv.getT0(); //res.get(12+offset+10*i);
                    adresse.t1=pv.getT1(); //res.get(13+offset+10*i);
                    adresse.codeSovAc3 = pv.getIds().get(6); //res.get(8+6*i);
                    retour.adresses.add(adresse);
                }
            }
        }
        catch(Exception e)
        {
            retour.codeerreur = 0;
            retour.etat = 0; // indique une erreur
            retour.message = "Pb de validation avec ("+toString()+") :"+e.getMessage();
        }
        return retour;
    }
    
    protected String traite(String traite)
    {
        return traite.replaceAll("'","''");
    }
    
    /**
     * Permet d'implémenter toStringSQL pour les classes héritant de celle-ci.
     * @return
     */
    protected String toStringSQLToOverride()
    {
        /*
        String str = "'";
        
        str += traite(id)+"','"+traite(ligne1)+"','"+traite(ligne2)+"','"+traite(ligne3)+"','"+traite(ligne4)+"','"+traite(ligne5)+"','"+traite(ligne6)+"','";
        
        if (config.decoupe)
        {
            if (decoupe)
            {
                str += traite(firstNumber) + "','" +
                        traite(firstRep) + "','";

                for (int i = 0; i < otherNumbers.length; i++) {
                    if (i > 0) {
                        str += ';';
                    }
                    str += otherNumbers[i];
                }

                str += "','" + traite(typedevoie) + "','" +
                        traite(article) + "','" +
                        traite(libelle) + "','" +
                        traite(motdeterminant) + "','" +
                        traite(codedepartement) + "','" +
                        traite(codepostal) + "','" +
                        traite(commune) + "','" +
                        traite(arrondissement) + "','" +
                        traite(cedex) + "','" +
                        traite(codecedex) + "','";
            }
            else
            {
                str += "','','','','','','','','','','','','','";
            }
        }
        
        return str;
        */
        return internalToString(',', '\'');
    }
    
    /**
     * Obtient une portion de représentation pour script SQL.
     * @return
     */
    public String toStringSQL()
    {
        /*
        String str = "'";
        
        str += traite(id)+"','"+traite(ligne1)+"','"+traite(ligne2)+"','"+traite(ligne3)+"','"+traite(ligne4)+"','"+traite(ligne5)+
                "','"+traite(ligne6)+"','";
        
        if (config.decoupe)
        {
            if (decoupe)
            {
                str += traite(firstNumber) + "','" +
                        traite(firstRep) + "','";

                for (int i = 0; i < otherNumbers.length; i++) {
                    if (i > 0) {
                        str += ';';
                    }
                    str += otherNumbers[i];
                }

                str += "','" + traite(typedevoie) + "','" +
                        traite(article) + "','" +
                        traite(libelle) + "','" +
                        traite(motdeterminant) + "','" +
                        traite(codedepartement) + "','" +
                        traite(codepostal) + "','" +
                        traite(commune) + "','" +
                        traite(arrondissement) + "','" +
                        traite(cedex) + "','" +
                        traite(codecedex) + "','";
            }
            else
            {
                str += "','','','','','','','','','','','','','";
            }
        }
        
        str += "','','','','','',''";
        
        return str;
         * */
        // Identique a toStringSQLToOverride + "','','','','','',''";
        return toStringSQLToOverride() + "','','','','','',''";
    }
    
    /**
     * Retourne la représentation sous forme de chaine de la classe.
     * @return
     */
    @Override
    public String toString()
    {
        /*
        String str = "\""+id+"\";\""+ligne1+"\";\""+ligne2+"\";\""+ligne3+"\";\""+ligne4+"\";\""+ligne5+"\";\""+ligne6+"\"";
        
        if (config.decoupe)
        {
            if (decoupe)
            {
                str += ";\"" + traite(firstNumber) + "\";\"" +
                        traite(firstRep) + "\";\"";

                for (int i = 0; i < otherNumbers.length; i++) {
                    if (i > 0) {
                        str += ';';
                    }
                    str += otherNumbers[i];
                }

                str += "\";\"" + traite(typedevoie) + "\";\"" +
                        traite(article) + "\";\"" +
                        traite(libelle) + "\";\"" +
                        traite(motdeterminant) + "\";\"" +
                        traite(codedepartement) + "\";\"" +
                        traite(codepostal) + "\";\"" +
                        traite(commune) + "\";\"" +
                        traite(arrondissement) + "\";\"" +
                        traite(cedex) + "\";\"" +
                        traite(codecedex) + "\"";
            }
            else
            {
                str += ";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\";\"\"";
            }
        }
        
        return str;
        */
        return internalToString(';', '"');
    }

    private String internalToString(char sep, char quot)
    {
        StringBuilder str = new StringBuilder();

        StringBuilder quotSepQuot = new StringBuilder();
        quotSepQuot.append(quot).append(sep).append(quot);

        str.append(quot).append(traite(id)).append(quotSepQuot).append(traite(ligne1)).append(quotSepQuot);
        str.append(traite(ligne2)).append(quotSepQuot).append(traite(ligne3)).append(quotSepQuot);
        str.append(traite(ligne4)).append(quotSepQuot).append(traite(ligne5)).append(quotSepQuot);
        str.append(traite(ligne6)).append(quot);
        if(config.gererPays)
        {
            str.append(sep);
            if(ligne7 != null)
                str.append(quot).append(traite(ligne7)).append(quot);
        }

        if (config.decoupe)
        {
            if (decoupe)
            {
                str.append(traite(firstNumber)).append(quotSepQuot).append(traite(firstRep)).append(quotSepQuot);

                for (int i = 0; i < otherNumbers.length; i++)
                {
                    if (i > 0)
                        str.append(sep);
                    str.append(otherNumbers[i]);
                }
                str.append(quotSepQuot).append(traite(typedevoie)).append(quotSepQuot);
                str.append(traite(article)).append(quotSepQuot);
                str.append(traite(libelle)).append(quotSepQuot);
                str.append(traite(motdeterminant)).append(quotSepQuot);
                str.append(traite(codedepartement)).append(quotSepQuot);
                str.append(traite(codepostal)).append(quotSepQuot);
                str.append(traite(commune)).append(quotSepQuot);
                str.append(traite(arrondissement)).append(quotSepQuot);
                str.append(traite(cedex)).append(quotSepQuot);
                str.append(traite(codecedex)).append(quot);
                if(config.gererPays)
                    str.append(sep).append(quot).append(traite(pays)).append(quot);
            }
            else
            {
                str.append(quotSepQuot).append(quotSepQuot).append(quotSepQuot).append(quotSepQuot).append(quotSepQuot);
                str.append(quotSepQuot).append(quotSepQuot).append(quotSepQuot).append(quotSepQuot).append(quotSepQuot);
                str.append(quotSepQuot).append(quotSepQuot).append(quotSepQuot);
                /*str.append("','','','','','','','','','','','','','");*/
            }
        }
        return str.toString();
    }
}