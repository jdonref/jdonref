package ppol.jdonref.geocodeur;

import org.jdom.Element;

/**
 * Représente une adresse qui n'a pas pu être validée.<br>
 * Elle contient ainsi des propositions d'adresse.
 * @author jmoquet
 */
public abstract class AdresseNonValide extends Adresse
{
    /**
     * Construit une adresse non validée à partir d'une adresse
     * @param a
     */
    public AdresseNonValide(Adresse a)
    {
        super(a);
        /*
        this.id = a.id;
        this.ligne1 = a.ligne1;
        this.ligne2 = a.ligne2;
        this.ligne3 = a.ligne3;
        this.ligne4 = a.ligne4;
        this.ligne5 = a.ligne5;
        this.ligne6 = a.ligne6;
        */
        
        this.decoupe = a.decoupe;
        this.firstNumber = a.firstNumber;
        this.firstRep = a.firstRep;
        this.otherNumbers = a.otherNumbers;
        this.typedevoie = a.typedevoie;
        this.article = a.article;
        this.libelle = a.libelle;
        this.motdeterminant = a.motdeterminant;
        this.codedepartement = a.codedepartement;
        this.codepostal = a.codepostal;
        this.commune = a.commune;
        this.arrondissement = a.arrondissement;
        this.cedex = a.cedex;
        this.codecedex = a.codecedex;
        
        this.config = a.config;
    }
    
    /**
     * Constructeur par défaut.
     */
    public AdresseNonValide()
    {
        super();
    }    
    
    /**
     * Permet de charger une adresse non valide à partir de sa représentation XML.
     * @return null si l'élément n'a pas pu être déterminé.
     */
    public static AdresseNonValide loadElement(Element e,Config config)
    {
        Adresse a = new Adresse();
        a.config = config;
        a.load(e);
        
        if (e.getChild("erreur")!=null)
        {
            Erreur er = new Erreur(a);
            er.load(e);
            return er;
        }
        else if (e.getChild("propositions")!=null)
        {
            Propositions p = new Propositions(a);
            p.load(e);
            return p;
        }
        return null;
    }
}