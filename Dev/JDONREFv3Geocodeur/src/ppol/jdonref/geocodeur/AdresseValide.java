package ppol.jdonref.geocodeur;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import ppol.jdonref.JDONREF;
import ppol.jdonref.JDONREFService;
import ppol.jdonref.PropositionDecoupage;
import ppol.jdonref.PropositionGeocodage;
import ppol.jdonref.ResultatDecoupage;
import ppol.jdonref.ResultatGeocodage;

/**
 * Représente une adresse validée.
 * @author jmoquet
 */
public class AdresseValide extends Adresse
{
    public int note;
    public String idvoie;
    public String codeinsee;
    public String codeSovAc3;
    public String t0;
    public String t1;
    public String ligne1valide;
    public String ligne2valide;
    public String ligne3valide;
    public String ligne4valide;
    public String ligne5valide;
    public String ligne6valide;
    public String ligne7valide;
    
    /**
     * Construit une adresse valide à partir d'une adresse.
     * @param a
     */
    public AdresseValide(Adresse a)
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
        ligne7 = a.ligne7;
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
    public AdresseValide()
    {
    }
    
    /**
     * Obtient une portion de représentation pour script SQL.
     * @return
     */
    @Override
    public String toStringSQL()
    {
        /*
        String str = super.toStringSQLToOverride();
        
        str += note+"','"+traite(ligne1valide)+"','"+traite(ligne2valide)+"','"+traite(ligne3valide)+"','"+traite(ligne4valide)+
                "','"+traite(ligne5valide)+"','"+traite(ligne6valide)+"'";
        
        return str;
        */

        StringBuilder str = new StringBuilder(super.toStringSQLToOverride());
        str.append(note).append("','").append(traite(ligne1valide)).append("','");
        str.append(traite(ligne2valide)).append("','").append(traite(ligne3valide)).append("','");
        str.append(traite(ligne4valide)).append("','").append(traite(ligne5valide)).append("','");
        str.append(traite(ligne6valide)).append("'");
        if(ligne7valide != null)
            str.append(", '").append(traite(ligne6valide)).append("'");

        return str.toString();
    }
    
    /**
     * Retourne la représentation xml de la classe dans une arborescence xml.
     */
    @Override
    public String toString()
    {
        /*
        String adresse = super.toString();
        
        adresse += ";"+note+";";
        
        if (config!=null)
        {
            if (config.idvoie)
                adresse += "\""+idvoie+"\"";
            if (config.codeinsee)
                adresse += "\""+codeinsee+"\"";
            if (config.t0)
                adresse += "\""+t0+"\"";
            if (config.t1)
               adresse += "\""+t1+"\"";
        }
        
        adresse += "\""+ligne1valide+"\";\""+ligne2valide+"\";\""+ligne3valide+"\";\""+ligne4valide+"\";\""+ligne5valide+"\";\""+
         ligne6valide+"\"";
        
        return adresse;
        */
        StringBuilder str = new StringBuilder(super.toString());
        str.append(";").append(note).append(";");
        if(config != null)
        {
            if(config.idvoie)
                str.append("\"").append(idvoie).append("\"");
            if(config.codeinsee)
                str.append("\"").append(codeinsee).append("\"");
            if(config.codeSovAc3)
                str.append("\"").append(codeSovAc3).append("\"");
            if(config.t0)
                str.append("\"").append(t0).append("\"");
            if(config.t1)
                str.append("\"").append(t1).append("\"");
        }
        str.append("\"").append(ligne1valide).append("\";\"");
        str.append(ligne2valide).append("\";\"").append(ligne3valide).append("\";\"");
        str.append(ligne4valide).append("\";\"").append(ligne5valide).append("\";\"");
        str.append(ligne6valide).append("\"");
        if(config.gererPays)
        {
            str.append(";");
            if(ligne7valide != null)
                str.append("\"").append(traite(ligne7valide)).append("\"");
        }

        return str.toString();
    }
    
    /**
     * Découpe la portion valide de l'adresse
     * 
     * @param service
     * @param config
     * @param natures
     * @param numeros
     */
    @Override
    public void decoupe(JDONREFService service,Config config) throws Exception
    {
        JDONREF port = service.getJDONREFPort();
        
        //System.out.println("Decoupe l'adresse");
        
        ArrayList<String> lignes=new ArrayList<String>();
        lignes.add(ligne1valide);
        lignes.add(ligne2valide);
        lignes.add(ligne3valide);
        lignes.add(ligne4valide);
        lignes.add(ligne5valide);
        lignes.add(ligne6valide);
        if(ligne7valide != null)
            lignes.add(ligne7valide);
        
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
        if(ligne7valide != null)
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
     * Géocode l'adresse.
     * @param service
     * @param forceville permet de forcer le géocodage à la ville.
     * @return
     */
    public AdresseGeocodee geocode(JDONREFService service, Config config, boolean forceville, boolean forcePays) throws Exception
    {
        AdresseGeocodee ag = new AdresseGeocodee(this);

        JDONREF port = service.getJDONREFPort();

        String idvoieAGeocoder = idvoie;
        String ligne4_a_geocoder = ligne4valide;
        String codeInseeAGeocoder = codeinsee;
        
        if(config.geocodage == Geocodage.Pays || forcePays || (codeSovAc3 != null) )
        {
            idvoieAGeocoder = null;
            codeInseeAGeocoder = null;
            ligne4_a_geocoder = codeSovAc3;
        }
        else if (config.geocodage==Geocodage.Ville || forceville)
            ligne4_a_geocoder = "";

        ArrayList<Integer> l_services = new ArrayList<Integer>();
        l_services.add(1); // JADRREF automatique
        
        ArrayList<String> options = new ArrayList<String>();
        
        ArrayList<String> donnees = new ArrayList<String>();
        donnees.add("");
        donnees.add("");
        donnees.add("");
        donnees.add(ligne4_a_geocoder);
        donnees.add("");
        donnees.add("");
        
        ArrayList<String> ids = new ArrayList<String>();
        ids.add("");
        ids.add("");
        ids.add("");
        ids.add(idvoieAGeocoder);
        ids.add("");
        ids.add(codeInseeAGeocoder);
        
        ResultatGeocodage res = port.geocode(config.application, l_services, donnees, ids, options);
        
        // List<String> res = port.geocode(config.application, idvoieAGeocoder, ligne4_a_geocoder, codeInseeAGeocoder, t0); // JDONREFv2
        
    /* <ul>
     * <li>Code de la méthode: 11</li>
     * <li>Nombre de résultats: 1</li>
     * <li>Type de géocodage (de moins en moins précis):
     * <ul>
     * <li>1 pour à la plaque,</li>
     * <li>2 pour à l'interpolation de la plaque,</li>
     * <li>3 pour à l'interpolation métrique du troncon ou les bornes du troncon (qualité équivalente),</li>
     * <li>4 au centroide du troncon,</li>
     * <li>5 pour le centroide de la voie.</li>
     * </ul>
     * <li>X précision cm</li>
     * <li>Y précision cm</li>
     * <li>Date de validation</li>
     * <li>Referentiel</li>
     * <li>Projection</li>
     * </ul>
     */
        int etat = res.getCodeRetour();
        
        if (etat==0) throw(new Exception("Erreur durant le géocodage "+res.getErreurs().get(0).getMessage()));
        
        PropositionGeocodage pg = res.getPropositions().get(0);
        
        ag.x = pg.getX();
        ag.y = pg.getY();
        ag.type = TypeGeocodage.getValue(Integer.parseInt(pg.getType())-1);
        
        return ag;
    }
    
    /**
     * Permet de charger l'objet à partir de sa représentation XML.<br>
     * Seule la portion valide de l'adresse est chargée
     */
    @Override
    public void load(Element e)
    {
        Element e_id = e.getChild("idvoie");
        Element e_codeinsee = e.getChild("codeinsee");
        Element e_codeSovAc3 = e.getChild("codesovac3");
        Element e_ligne1 = e.getChild("ligne1");
        Element e_ligne2 = e.getChild("ligne2");
        Element e_ligne3 = e.getChild("ligne3");
        Element e_ligne4 = e.getChild("ligne4");
        Element e_ligne5 = e.getChild("ligne5");
        Element e_ligne6 = e.getChild("ligne6");
        Element e_ligne7 = e.getChild("ligne7");
        Element e_note = e.getChild("note");
        Element e_t0 = e.getChild("t0");
        Element e_t1 = e.getChild("t1");
        
        idvoie = e_id.getValue();
        codeinsee = e_codeinsee.getValue();
        ligne1valide = e_ligne1.getValue();
        ligne2valide = e_ligne2.getValue();
        ligne3valide = e_ligne3.getValue();
        ligne4valide = e_ligne4.getValue();
        ligne5valide = e_ligne5.getValue();
        ligne6valide = e_ligne6.getValue();
        if(e_ligne7 != null)
            ligne7valide = e_ligne7.getValue();
        if(e_codeSovAc3 != null)
            codeSovAc3 = e_codeSovAc3.getValue();
        t0 = e_t0.getValue();
        t1 = e_t1.getValue();
        note = Integer.parseInt(e_note.getValue());
    }
    
    /**
     * Obtient une représentation sous forme xml de la proposition d'adresse.<br>
     * Cette représentation tient uniquement compte de la portion valide de l'adresse.
     * @return
     */
    @Override
    public Element toXml()
    {
        Element e = new Element("proposition");
        
        Element e_id = new Element("idvoie");
        e_id.setText(idvoie);
        Element e_codeinsee = new Element("codeinsee");
        e_codeinsee.setText(codeinsee);
        
        Element e_ligne1 = new Element("ligne1");
        e_ligne1.setText(ligne1valide);
        Element e_ligne2 = new Element("ligne2");
        e_ligne2.setText(ligne2valide);
        Element e_ligne3 = new Element("ligne3");
        e_ligne3.setText(ligne3valide);
        
        Element e_ligne4 = new Element("ligne4");
        e_ligne4.setText(ligne4valide);
        Element e_ligne5 = new Element("ligne5");
        e_ligne5.setText(ligne5valide);
        Element e_ligne6 = new Element("ligne6");
        e_ligne6.setText(ligne6valide);
        Element e_ligne7 = null;
        if(ligne7valide != null)
        {
            e_ligne7 = new Element("ligne7");
            e_ligne7.setText(ligne7valide);
        }
        Element e_codeSovAc3 = null;
        if(codeSovAc3 != null)
        {
            e_codeSovAc3 = new Element("codesovac3");
            e_codeSovAc3.setText(codeSovAc3);
        }
        
        Element e_note = new Element("note");
        e_note.setText(Integer.toString(note));
        Element e_t0 = new Element("t0");
        e_t0.setText(t0);
        Element e_t1 = new Element("t1");
        e_t1.setText(t1);
        
        e.addContent(e_id);
        e.addContent(e_codeinsee);
        if(e_codeSovAc3 != null)
            e.addContent(e_codeSovAc3);
        e.addContent(e_ligne1);
        e.addContent(e_ligne2);
        e.addContent(e_ligne3);
        e.addContent(e_ligne4);
        e.addContent(e_ligne5);
        e.addContent(e_ligne6);
        if(ligne7valide != null)
        {
            e.addContent(e_ligne7);
        }
        e.addContent(e_note);
        e.addContent(e_t0);
        e.addContent(e_t1);
        
        return e;
    }
}