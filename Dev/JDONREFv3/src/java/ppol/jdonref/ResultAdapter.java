/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcanhe
 */
public class ResultAdapter {

    public static  ppol.jdonref.wservice.ResultatGeocodage adapteGeocode(List<ppol.jdonref.wsclient.ResultatGeocodage> list) {
        final ppol.jdonref.wservice.ResultatGeocodage resultatRet = new ppol.jdonref.wservice.ResultatGeocodage();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionGeocodage> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionGeocodage>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatGeocodage result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else { // Propositions
                final List<ppol.jdonref.wsclient.PropositionGeocodage> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionGeocodage proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }
        
        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionGeocodage[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    public static  ppol.jdonref.wservice.ResultatGeocodageInverse adapteGeocodeInverse(List<ppol.jdonref.wsclient.ResultatGeocodageInverse> list) {
        final ppol.jdonref.wservice.ResultatGeocodageInverse resultatRet = new ppol.jdonref.wservice.ResultatGeocodageInverse();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionGeocodageInverse> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionGeocodageInverse>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatGeocodageInverse result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else { // Propositions
                final List<ppol.jdonref.wsclient.PropositionGeocodageInverse> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionGeocodageInverse proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionGeocodageInverse[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    public static  ppol.jdonref.wservice.ResultatNormalisation adapteNormalise(List<ppol.jdonref.wsclient.ResultatNormalisation> list) {
        final ppol.jdonref.wservice.ResultatNormalisation resultatRet = new ppol.jdonref.wservice.ResultatNormalisation();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionNormalisation> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionNormalisation>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatNormalisation result : list) {
            if (result.getCodeRetour() == 0) {
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else {
                final List<ppol.jdonref.wsclient.PropositionNormalisation> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionNormalisation proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionNormalisation[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    public static  ppol.jdonref.wservice.ResultatValidation adapteValide(List<ppol.jdonref.wsclient.ResultatValidation> list) {
        final ppol.jdonref.wservice.ResultatValidation resultatRet = new ppol.jdonref.wservice.ResultatValidation();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionValidation> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionValidation>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatValidation result : list) {
            if (result.getCodeRetour() == 0) {
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else {
                final List<ppol.jdonref.wsclient.PropositionValidation> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionValidation proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur. 
            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionValidation[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    public static  ppol.jdonref.wservice.ResultatRevalidation adapteRevalide(List<ppol.jdonref.wsclient.ResultatRevalidation> list) {
        final ppol.jdonref.wservice.ResultatRevalidation resultatRet = new ppol.jdonref.wservice.ResultatRevalidation();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionRevalidation> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionRevalidation>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatRevalidation result : list) {
            if (result.getCodeRetour() == 0) {
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else {
                final List<ppol.jdonref.wsclient.PropositionRevalidation> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionRevalidation proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionRevalidation[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    public static  ppol.jdonref.wservice.ResultatDecoupage adapteDecoupe(List<ppol.jdonref.wsclient.ResultatDecoupage> list) {
        final ppol.jdonref.wservice.ResultatDecoupage resultatRet = new ppol.jdonref.wservice.ResultatDecoupage();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionDecoupage> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionDecoupage>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatDecoupage result : list) {
            if (result.getCodeRetour() == 0) {
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else {
                final List<ppol.jdonref.wsclient.PropositionDecoupage> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionDecoupage proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionDecoupage[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }
    
    public static  ppol.jdonref.wservice.ResultatContacte adapteContacte(List<ppol.jdonref.wsclient.ResultatContacte> list) {
        final ppol.jdonref.wservice.ResultatContacte resultatRet = new ppol.jdonref.wservice.ResultatContacte();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatContacte result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } 
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }
        
        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }
    
    public static  ppol.jdonref.wservice.ResultatVersion adapteGetVersion(List<ppol.jdonref.wsclient.ResultatVersion> list) {
        final ppol.jdonref.wservice.ResultatVersion resultatRet = new ppol.jdonref.wservice.ResultatVersion();
        final List<ppol.jdonref.wservice.ResultatErreur> erreurList = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        final List<ppol.jdonref.wservice.PropositionVersion> propositionList = new ArrayList<ppol.jdonref.wservice.PropositionVersion>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ppol.jdonref.wsclient.ResultatVersion result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs
                retourErreur = true;
                erreurList.addAll(valueOf(result.getErreurs()));
            } else { // Propositions
                final List<ppol.jdonref.wsclient.PropositionVersion> propositions = result.getPropositions();
                for (ppol.jdonref.wsclient.PropositionVersion proposition : propositions) {
                    propositionList.add(valueOf(proposition));
                }
            }
            optionList.addAll(result.getOptions()); // Options
        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.
            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.
            resultatRet.setCodeRetour(1);
        }
        
        resultatRet.setErreurs(erreurList.toArray(new ppol.jdonref.wservice.ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new ppol.jdonref.wservice.PropositionVersion[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    private static  ppol.jdonref.wservice.PropositionGeocodage valueOf(ppol.jdonref.wsclient.PropositionGeocodage aproposition) {
        final ppol.jdonref.wservice.PropositionGeocodage propositionRet = new ppol.jdonref.wservice.PropositionGeocodage();
        propositionRet.setDate(aproposition.getDate());
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));
        propositionRet.setProjection(aproposition.getProjection());
        propositionRet.setReferentiel(aproposition.getReferentiel());
        propositionRet.setService(aproposition.getService());
        propositionRet.setType(aproposition.getType());
        propositionRet.setX(aproposition.getX());
        propositionRet.setY(aproposition.getY());

        return propositionRet;
    }

    private static  ppol.jdonref.wservice.PropositionGeocodageInverse valueOf(ppol.jdonref.wsclient.PropositionGeocodageInverse aproposition) {
        final ppol.jdonref.wservice.PropositionGeocodageInverse propositionRet = new ppol.jdonref.wservice.PropositionGeocodageInverse();
        propositionRet.setCode(aproposition.getCode());
        propositionRet.setDistance(aproposition.getDistance());
        propositionRet.setDonnees(aproposition.getDonnees().toArray(new String[aproposition.getDonnees().size()]));
        propositionRet.setDonneesOrigine(aproposition.getDonneesOrigine().toArray(new String[aproposition.getDonneesOrigine().size()]));
        propositionRet.setIds(aproposition.getIds().toArray(new String[aproposition.getIds().size()]));
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));
        propositionRet.setReferentiel(aproposition.getReferentiel());
        propositionRet.setService(aproposition.getService());
        propositionRet.setT0(aproposition.getT0());
        propositionRet.setT1(aproposition.getT1());
        propositionRet.setX(aproposition.getX());
        propositionRet.setY(aproposition.getY());

        return propositionRet;
    }

    private static  ppol.jdonref.wservice.PropositionNormalisation valueOf(ppol.jdonref.wsclient.PropositionNormalisation aproposition) {
        final ppol.jdonref.wservice.PropositionNormalisation propositionRet = new ppol.jdonref.wservice.PropositionNormalisation();
        propositionRet.setDonnees(aproposition.getDonnees().toArray(new String[aproposition.getDonnees().size()]));
        propositionRet.setService(aproposition.getService());
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));
        propositionRet.setService(aproposition.getService());

        return propositionRet;
    }
    
    private static  ppol.jdonref.wservice.PropositionValidation valueOf(ppol.jdonref.wsclient.PropositionValidation aproposition) {
        final ppol.jdonref.wservice.PropositionValidation propositionRet = new ppol.jdonref.wservice.PropositionValidation();
        propositionRet.setCode(aproposition.getCode());
        propositionRet.setDonnees(aproposition.getDonnees().toArray(new String[aproposition.getDonnees().size()]));
        propositionRet.setDonneesOrigine(aproposition.getDonneesOrigine().toArray(new String[aproposition.getDonneesOrigine().size()]));
        propositionRet.setService(aproposition.getService());
        propositionRet.setIds(aproposition.getIds().toArray(new String[aproposition.getIds().size()]));
        propositionRet.setNote(aproposition.getNote());
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));
        propositionRet.setService(aproposition.getService());
        propositionRet.setT0(aproposition.getT0());
        propositionRet.setT1(aproposition.getT1());

        return propositionRet;
    }

    private static  ppol.jdonref.wservice.PropositionRevalidation valueOf(ppol.jdonref.wsclient.PropositionRevalidation aproposition) {
        final ppol.jdonref.wservice.PropositionRevalidation propositionRet = new ppol.jdonref.wservice.PropositionRevalidation();
        propositionRet.setDonnees(aproposition.getDonnees().toArray(new String[aproposition.getDonnees().size()]));
        propositionRet.setDonneesOrigine(aproposition.getDonneesOrigine().toArray(new String[aproposition.getDonneesOrigine().size()]));
        propositionRet.setService(aproposition.getService());
        propositionRet.setIds(aproposition.getIds().toArray(new String[aproposition.getIds().size()]));
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));
        propositionRet.setService(aproposition.getService());
        propositionRet.setT0(aproposition.getT0());
        propositionRet.setT1(aproposition.getT1());

        return propositionRet;
    }

    private static  ppol.jdonref.wservice.PropositionDecoupage valueOf(ppol.jdonref.wsclient.PropositionDecoupage aproposition) {
        final ppol.jdonref.wservice.PropositionDecoupage propositionRet = new ppol.jdonref.wservice.PropositionDecoupage();
        propositionRet.setDonnees(aproposition.getDonnees().toArray(new String[aproposition.getDonnees().size()]));
        propositionRet.setService(aproposition.getService());
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));
        propositionRet.setService(aproposition.getService());

        return propositionRet;
    }

    private static  ppol.jdonref.wservice.ResultatErreur valueOf(ppol.jdonref.wsclient.ResultatErreur anerreur) {
        final ppol.jdonref.wservice.ResultatErreur erreurRet = new ppol.jdonref.wservice.ResultatErreur();
        erreurRet.setCode(anerreur.getCode());
        erreurRet.setMessage(anerreur.getMessage());
        erreurRet.setService(anerreur.getService());
        erreurRet.setOptions(anerreur.getOptions().toArray(new String[anerreur.getOptions().size()]));

        return erreurRet;
    }

    private static  List<ppol.jdonref.wservice.ResultatErreur> valueOf(List<ppol.jdonref.wsclient.ResultatErreur> alist) {
        final List<ppol.jdonref.wservice.ResultatErreur> listRet = new ArrayList<ppol.jdonref.wservice.ResultatErreur>();
        for (ppol.jdonref.wsclient.ResultatErreur erreur : alist) {
            listRet.add(valueOf(erreur));
        }

        return listRet;
    }
    
    private static  ppol.jdonref.wservice.PropositionVersion valueOf(ppol.jdonref.wsclient.PropositionVersion aproposition) {
        final ppol.jdonref.wservice.PropositionVersion propositionRet = new ppol.jdonref.wservice.PropositionVersion();
        propositionRet.setService(aproposition.getService());
        propositionRet.setNom(aproposition.getNom());
        propositionRet.setVersion(aproposition.getVersion());
        propositionRet.setOptions(aproposition.getOptions().toArray(new String[aproposition.getOptions().size()]));

        return propositionRet;
    }
}
