package ppol.jdonref.geocodeur;

import java.util.ArrayList;

/**
 * Présente le résultat d'une validation
 * @author jmoquet
 */
public class RetourValidation
{
    /**
     * Adresse à l'origine de la validation.
     */
    public Adresse adresse;
    /**
     * 0 = erreur
     */
    int etat;
    /**
     * Le code d'erreur associé à l'erreur:
     * <ul>
     * <li>0 s'il s'agit d'une erreur non gérée par JDONREF.</li>
     * <li>le code d'erreur retourné par JDONREF sinon.</li>
     * </ul>
     */
    int codeerreur;
    /**
     * Le message d'erreur associé à l'erreur.
     */
    String message;
    /**
     * Le résultat de la validation.
     */
    ArrayList<AdresseValide> adresses = new ArrayList<AdresseValide>();
    
    /**
     * Constructeur par défaut.
     */
    public RetourValidation(Adresse a)
    {
        adresse = a;
    }
    
    /**
     * Obtient le statut de la validation.
     * @return
     */
    public StatutValidation obtientStatut(Config config)
    {
        if (etat==0)
        {
            return StatutValidation.pb;
        }
        else if (adresses.size()==0)
        {
            message = "Aucune adresse correspondante trouvée.";
            return StatutValidation.pb;
        }
        else
        {
            if (adresses.get(0).note<config.note) return StatutValidation.choix;
            if (adresses.size()==1 || adresses.get(1).note<config.note) return StatutValidation.valide;
            return StatutValidation.choix;
        }
    }
    
    /**
     * Si le statut est valide, obtient l'adresse validée.
     * @return
     */
    public AdresseValide obtientAdresseValide()
    {
        return adresses.get(0);
    }
    
    /**
     * Si le statut est choix, obtient les propositions
     */
    public Propositions obtientPropositions()
    {
        Propositions p = new Propositions(adresse);
        
        for(int i=0;i<adresses.size();i++)
        {
            p.propositions.add(adresses.get(i));
        }
        
        return p;
    }
    
    /**
     * Si le statut est erreur, obtient l'erreur.
     */
    public Erreur obtientErreur()
    {
        Erreur e = new Erreur(adresse);
        e.message = message;
        return e;
    }
}