/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonrefv3v4;

import ppol.jdonref.wservice.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ppol.jdonref.referentiel.JDONREFv3Lib;
import ppol.jdonref.referentiel.reversegeocoding.GestionInverse;

/**
 *
 * @author marcanhe
 */
public class ResultAdapter {

    public static final int SERVICE_ADRESSE = 1;
    public static final int SERVICE_POINT_ADRESSE = 2;
    public static final int SERVICE_TRONCON = 3;
    public static final int SERVICE_VOIE = 4;
    public static final int SERVICE_COMMUNE = 5;
    public static final int SERVICE_DEPARTEMENT = 6;
    public static final int SERVICE_PAYS = 7;

    public static ResultatNormalisation adapteNormalise(String[] aresult, int operation) {
        final ResultatNormalisation resultatRet = new ResultatNormalisation();

        if (aresult[0].equals("0")) {
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(aresult[1]));
            erreur.setMessage(aresult[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            resultatRet.setCodeRetour(1);
            final PropositionNormalisationBuilder builder = new PropositionNormalisationBuilder();
            final List<String> result = valueOf(aresult);
            int offset = 7;
            //ligne1
            builder.setDonnee(0, result.get(1));
            //ligne2
            builder.setDonnee(1, result.get(2));
            //ligne3
            builder.setDonnee(2, result.get(3));
            //ligne4
            builder.setDonnee(3, result.get(4));
            //ligne5
            builder.setDonnee(4, result.get(5));
            //ligne6
            builder.setDonnee(5, result.get(6));
            // Pays il y a une ligne 7 ds le resultatRet si operation & 256 -> option pays activée
            if ((operation & 256) != 0) {
                //ligne7
                if (result.get(7) != null) {
                    builder.addDonnee(result.get(7));
                }
                offset++;
            }

            //supplements
            if ((operation & 4) != 0) {
                final String numeros = result.get(offset);
                if (numeros != null && numeros.trim().length() > 0) {
                    builder.addOption("numeros=" + numeros);
                    offset++; // les numéros sont toujours avant les départements.
                }
            }

            //departements
            if ((operation & 128) != 0 && (operation & 2) != 0) {
                final String departements = result.get(offset);
                if (departements != null && departements.trim().length() > 0) {
                    builder.addOption("dpt=" + departements);
                }
            }


            ///////////////////
            //builder.setService(1);
            Integer id = SERVICE_ADRESSE;
            Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
            builder.setService(cle);

            resultatRet.setPropositions(new PropositionNormalisation[]{builder.build()});
        }
        return resultatRet;
    }

    public static ResultatNormalisation adapteNormalise(String[] donnees, String[] result) {
        final ResultatNormalisation resultatRet = new ResultatNormalisation();
        if (result[0].equals("0")) {
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(result[1]));
            erreur.setMessage(result[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            resultatRet.setCodeRetour(1);
            final PropositionNormalisationBuilder builder = new PropositionNormalisationBuilder();
            //ligne1
            builder.setDonnee(0, result[1]);
            //ligne2
            builder.setDonnee(1, donnees[1]);
            //ligne3
            builder.setDonnee(2, donnees[2]);
            //ligne4
            builder.setDonnee(3, donnees[3]);
            //ligne5
            builder.setDonnee(4, donnees[4]);
            //ligne6
            builder.setDonnee(5, donnees[5]);
            //ligne7
            if (donnees.length > 6) {
                for (int i = 6; i < donnees.length; i++) {
                    builder.addDonnee(donnees[i]);
                }
            }
            //builder.setService(1);          
            Integer id = SERVICE_ADRESSE;
            Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
            builder.setService(cle);


            resultatRet.setPropositions(new PropositionNormalisation[]{builder.build()});
        }
        return resultatRet;
    }

    public static ResultatDecoupage adapteDecoupe(String[] result) {
        final ResultatDecoupage resultatRet = new ResultatDecoupage();
        if (result[0].equals("0")) {
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(result[1]));
            erreur.setMessage(result[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            resultatRet.setCodeRetour(1);
            final PropositionDecoupage proposition = new PropositionDecoupage();
            final String[] donnees = new String[result.length - 1];
            for (int i = 1; i < result.length; i++) {
                donnees[i - 1] = result[i];
            }
            proposition.setDonnees(donnees);

            //proposition.setService(1);
            Integer id = SERVICE_ADRESSE;
            Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
            proposition.setService(cle);

            resultatRet.setPropositions(new PropositionDecoupage[]{proposition});
        }

        return resultatRet;
    }

    public static ResultatGeocodage adapteGeocode(String[] result) {
        final ResultatGeocodage resultatRet = new ResultatGeocodage();
        if (result[0].equals("0")) {
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(result[1]));
            erreur.setMessage(result[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            resultatRet.setCodeRetour(Integer.parseInt(result[0]));
            final PropositionGeocodage proposition = new PropositionGeocodage();
            proposition.setType(result[2]);

            //proposition.setService(Integer.parseInt(result[3]));
            Integer id = Integer.parseInt(result[3]);
            Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
            proposition.setService(cle);

            proposition.setX(result[4]);
            proposition.setY(result[5]);
            proposition.setDate(result[6]);
            proposition.setProjection(result[7]);
            proposition.setReferentiel(result[8]);

            resultatRet.setPropositions(new PropositionGeocodage[]{proposition});
        }

        return resultatRet;
    }

    public static ResultatGeocodage adapteGeocode(List<String[]> resultList) {
        final List<ResultatGeocodage> listResultat = adapteGeocodeResultList(resultList);
        final ResultatGeocodage resultatRet = aggregateGeocodeResult(listResultat);

        return resultatRet;
    }

    public static ResultatGeocodageInverse adapteGeocodeInverse(String[] aresult) {
        final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
        if (aresult[0].equals("0")) {
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(aresult[1]));
            erreur.setMessage(aresult[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            resultatRet.setCodeRetour(1);
            final List<String> result = valueOf(aresult);
            final int size = Integer.parseInt(aresult[1]);
            final List<PropositionGeocodageInverse> propositionList = new ArrayList<PropositionGeocodageInverse>();
            final int offset = 19;
            for (int i = 0; i < size; i++) {
                final PropositionGeocodageInverseBuilder builder = new PropositionGeocodageInverseBuilder();

                //precision
                // builder.setService(getServiceFromPrecision(Integer.parseInt(result.get(offset * i + 2))));
                Integer id = getServiceFromPrecision(Integer.parseInt(result.get(offset * i + 2)));
                Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
                builder.setService(cle);

                //ligne1
                builder.setDonnee(0, result.get(offset * i + 3));
                //ligne2
                builder.setDonnee(1, result.get(offset * i + 4));
                //ligne3
                builder.setDonnee(2, result.get(offset * i + 5));
                //ligne4
                builder.setDonnee(3, result.get(offset * i + 6));
                //ligne5
                builder.setDonnee(4, result.get(offset * i + 7));
                //ligne6
                builder.setDonnee(5, result.get(offset * i + 8));
                //x
                builder.setX(result.get(offset * i + 9));
                //y
                builder.setY(result.get(offset * i + 10));
                //t0
                builder.setT0(result.get(offset * i + 11));
                //t1
                builder.setT1(result.get(offset * i + 12));
                //distance
                builder.setDistance(result.get(offset * i + 13));
                // Ligne4 Origine
                builder.setDonneeOrigine(3, result.get(offset * i + 14));
                // Ligne6 Origine
                builder.setDonneeOrigine(5, result.get(offset * i + 15));
                // Id voie
                builder.setId(3, result.get(offset * i + 16));
                // Code insee
                builder.setId(5, result.get(offset * i + 17));
                // Ligne7
                builder.setDonnee(6, result.get(offset * i + 18));
                // Ligne7 origine
                builder.setDonneeOrigine(6, result.get(offset * i + 19));
                // Id pays
                builder.setId(6, result.get(offset * i + 20));

                propositionList.add(builder.build());
            }

            Collections.sort(propositionList, PropositionGeocodageInverseComparator.getInstance());
            resultatRet.setPropositions(propositionList.toArray(new PropositionGeocodageInverse[propositionList.size()]));
        }

        return resultatRet;
    }

    public static ResultatValidation adapteValide(String[] aresult, boolean pays, boolean fantoire) {
        final ResultatValidation resultatRet = new ResultatValidation();
        if (aresult[0].equals("0")) {
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(aresult[1]));
            erreur.setMessage(aresult[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            final List<String> result = valueOf(aresult);
            int etat = Integer.parseInt(result.get(0));
            int size = Integer.parseInt(result.get(1));
            resultatRet.setCodeRetour(etat);
            final List<PropositionValidation> propositionList = new ArrayList<PropositionValidation>();
            if (etat == 3 || etat == 4) {
                int offset = 7;
                //ligne1
                final String ligne1 = result.get(2);
                //ligne2
                final String ligne2 = result.get(3);
                //ligne3
                final String ligne3 = result.get(4);
                //ligne4
                final String ligne4 = result.get(5);
                //ligne5
                final String ligne5 = result.get(6);
                String dligne7 = "";
                String codeSovAc3 = "";
                // Pays il y a une ligne 7 ds le resultatRet s'il y en avait une ds les lignes ou si operation & 256
                if (pays) {
                    String[] paysArray = result.get(7).split(";");
                    if (paysArray.length == 2) {
                        dligne7 = paysArray[0];
                        codeSovAc3 = paysArray[1];
                    } else {
                        dligne7 = result.get(7);
                    }
                    offset++;

                }
                //communes
                for (int i = 0; i < size; i++) {
                    final PropositionValidationBuilder builder = new PropositionValidationBuilder();
                    builder.setCode(etat);
                    builder.setDonnee(0, ligne1);
                    builder.setDonnee(1, ligne2);
                    builder.setDonnee(2, ligne3);
                    builder.setDonnee(3, ligne4);
                    builder.setDonnee(4, ligne5);
                    if (pays) {
                        builder.addDonnee(dligne7);
                        builder.addId(codeSovAc3);
                    }
                    //codeinsee
                    builder.setId(5, result.get(6 * i + offset));
                    //ligne6
                    builder.setDonnee(5, result.get(6 * i + offset + 1));
                    //ligne6_desabbrevie
                    builder.setDonneeOrigine(5, result.get(6 * i + offset + 2));
                    //note
                    builder.setNote(result.get(6 * i + offset + 3));
                    //t0
                    builder.setT0(result.get(6 * i + offset + 4));
                    //t1
                    builder.setT1(result.get(6 * i + offset + 5));

                    // service
                    // builder.setService(5); // getCle(5)
                    Integer id = SERVICE_COMMUNE;
                    Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
                    builder.setService(cle);

                    propositionList.add(builder.build());
                }

            } else if (etat == 1 || etat == 2) {
                int offset = 6;
                //ligne1
                final String ligne1 = result.get(2);
                //ligne2
                final String ligne2 = result.get(3);
                //ligne3
                final String ligne3 = result.get(4);
                //ligne5
                final String ligne5 = result.get(5);
                // Pays
                String dligne7 = "";
                String codeSovAc3 = "";
                // WA 01/2012 Pays il y a une ligne 7 ds le resultatRet s'il y en avait une ds les lignes ou si operation & 256
                if (pays) {
                    String[] paysArray = result.get(6).split(";");
                    if (paysArray.length == 2) {
                        dligne7 = paysArray[0];
                        codeSovAc3 = paysArray[1];
                    } else {
                        dligne7 = result.get(6);
                    }
                    offset++;

                }

                //adresses
                for (int i = 0; i <
                        size; i++) {
                    final PropositionValidationBuilder builder = new PropositionValidationBuilder();
                    builder.setCode(etat);
                    builder.setDonnee(0, ligne1);
                    builder.setDonnee(1, ligne2);
                    builder.setDonnee(2, ligne3);
                    builder.setDonnee(4, ligne5);
                    if (pays) {
                        builder.addDonnee(dligne7);
                        builder.addId(codeSovAc3);
                    }
                    //idvoie
                    builder.setId(3, result.get(11 * i + offset));
                    //ligne4
                    builder.setDonnee(3, result.get(11 * i + offset + 1));
                    //ligne4_desabbrevie
                    builder.setDonneeOrigine(3, result.get(11 * i + offset + 2));
                    //codeinsee
                    builder.setId(5, result.get(11 * i + offset + 3));
                    //ligne6
                    builder.setDonnee(5, result.get(11 * i + offset + 4));
                    //ligne6_desabbrevie
                    builder.setDonneeOrigine(5, result.get(11 * i + offset + 5));
                    //t0
                    builder.setT0(result.get(11 * i + offset + 6));
                    //t1
                    builder.setT1(result.get(11 * i + offset + 7));
                    //note
                    builder.setNote(result.get(11 * i + offset + 8));
                    //codefantoire
                    if (fantoire) {
                        builder.addOption("fantoire=" + result.get(11 * i + offset + 9));
                    }
                    // service
                    // builder.setService(Integer.parseInt(result.get(11 * i + offset + 10)));
                    Integer id = Integer.parseInt(result.get(11 * i + offset + 10));
                    Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
                    builder.setService(cle);

                    propositionList.add(builder.build());

                }

            } else if (etat == 5 || etat == 6) {
                //ligne1
                final String ligne1 = result.get(2);
                //ligne2
                final String ligne2 = result.get(3);
                //ligne3
                final String ligne3 = result.get(4);
                //ligne4
                final String ligne4 = result.get(5);
                //ligne5
                final String ligne5 = result.get(6);
                //ligne6
                final String ligne6 = result.get(7);

                //pays
                for (int i = 0; i <
                        size; i++) {
                    final PropositionValidationBuilder builder = new PropositionValidationBuilder();
                    builder.setCode(etat);
                    builder.setDonnee(1, ligne2);
                    builder.setDonnee(2, ligne3);
                    builder.setDonnee(3, ligne4);
                    builder.setDonnee(4, ligne5);
                    builder.setDonnee(5, ligne6);
                    //codeSovAc3 c'est à dire du pays
                    builder.addId(result.get(6 * i + 8));
                    //ligne7
                    builder.addDonnee(result.get(6 * i + 9));
                    //ligne7_desabbrevie
                    builder.addDonneeOrigine(result.get(6 * i + 10));
                    //note
                    builder.setNote(result.get(6 * i + 11));
                    //t0
                    builder.setT0(result.get(6 * i + 12));
                    //t1
                    builder.setT1(result.get(6 * i + 13));

                    // service
                    // builder.setService(7);
                    Integer id = SERVICE_PAYS;
                    Integer cle = JDONREFv3Lib.getInstance().getServices().getServiceFromId(id).getCle();
                    builder.setService(cle);

                    propositionList.add(builder.build());
                }

            }

            resultatRet.setPropositions(propositionList.toArray(new PropositionValidation[propositionList.size()]));

        }

        return resultatRet;

    }

    public static ResultatValidation adapteValide(List<String[]> resultList, boolean pays, boolean fantoire) {
        final List<ResultatValidation> listResultat = adapteValideResultList(resultList, pays, fantoire);
        final ResultatValidation resultatRet = aggregateValideResult(listResultat);

        return resultatRet;

    }

    public static ResultatRevalidation adapteRevalide(List<String[]> resultList, boolean pays, String[] lignes1235) {
        final List<ResultatRevalidation> listResultat = adapteRevalideResultList(resultList, pays, lignes1235);
        final ResultatRevalidation resultatRet = aggregateRevalideResult(listResultat);

        return resultatRet;

    }

    public static ResultatRevalidation adapteRevalide(String[] aresult, boolean pays, String[] lignes1235) {
        final ResultatRevalidation resultatRet = new ResultatRevalidation();
        if (aresult[0].equals("0")) {
            resultatRet.setCodeRetour(0);
            final ResultatErreur erreur = new ResultatErreur();
            erreur.setCode(Integer.parseInt(aresult[1]));
            erreur.setMessage(aresult[2]);
            resultatRet.setErreurs(new ResultatErreur[]{erreur});
        } else {
            resultatRet.setCodeRetour(Integer.parseInt(aresult[0]));
            final List<String> result = valueOf(aresult);
            final List<PropositionRevalidation> propositionList = new ArrayList<PropositionRevalidation>();
            int size = Integer.parseInt(result.get(1));
            int offset = 2;
            for (int i = 0; i <
                    size; i++) {
                final PropositionRevalidationBuilder builder = new PropositionRevalidationBuilder();
                //ligne1
                builder.setDonnee(0, lignes1235[0]);
                //ligne1
                builder.setDonnee(1, lignes1235[1]);
                //ligne1
                builder.setDonnee(2, lignes1235[2]);
                //ligne1
                builder.setDonnee(4, lignes1235[3]);
                //id_voie
                builder.setId(3, result.get(offset));
                //ligne4
                builder.setDonnee(3, result.get(offset + 1));
                //ligne6
                builder.setDonnee(5, result.get(offset + 2));
                //code_insee
                builder.setId(5, result.get(offset + 3));
                //Ligne4 origine
                builder.setDonneeOrigine(3, result.get(offset + 4));
                //Ligne6 origine
                builder.setDonneeOrigine(5, result.get(offset + 5));

                // Pays il y a une ligne 7 ds le resultatRet s'il y en avait une ds les lignes ou si operation & 256
                if (pays) {
                    //ligne7
                    builder.addDonnee(result.get(offset + 6));
                    //sovAc3
                    builder.addId(result.get(offset + 7));
                    //t0
                    builder.setT0(result.get(offset + 8));
                    //t1
                    builder.setT1(result.get(offset + 9));
                    offset += 10;
                } else {
                    //t0
                    builder.setT0(result.get(offset + 6));
                    //t1
                    builder.setT1(result.get(offset + 7));
                    offset += 8;
                }

                propositionList.add(builder.build());

            }

            resultatRet.setPropositions(propositionList.toArray(new PropositionRevalidation[propositionList.size()]));
        }

        return resultatRet;
    }

    private static List<ResultatValidation> adapteValideResultList(List<String[]> resultList, boolean pays, boolean fantoire) {
        final List<ResultatValidation> listRet = new ArrayList<ResultatValidation>();
        for (String[] result : resultList) {
            listRet.add(adapteValide(result, pays, fantoire));
        }
        return listRet;
    }

    private static List<ResultatRevalidation> adapteRevalideResultList(List<String[]> resultList, boolean pays, String[] lignes1235) {
        final List<ResultatRevalidation> listRet = new ArrayList<ResultatRevalidation>();
        for (String[] result : resultList) {
            listRet.add(adapteRevalide(result, pays, lignes1235));
        }
        return listRet;
    }

    private static List<ResultatGeocodage> adapteGeocodeResultList(List<String[]> resultList) {
        final List<ResultatGeocodage> listRet = new ArrayList<ResultatGeocodage>();
        for (String[] result : resultList) {
            listRet.add(adapteGeocode(result));
        }

        return listRet;
    }

    private static ResultatGeocodage aggregateGeocodeResult(List<ResultatGeocodage> list) {
        final ResultatGeocodage resultatRet = new ResultatGeocodage();
        final List<ResultatErreur> erreurList = new ArrayList<ResultatErreur>();
        final List<PropositionGeocodage> propositionList = new ArrayList<PropositionGeocodage>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ResultatGeocodage result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs

                retourErreur = true;
                for (ResultatErreur erreur : result.getErreurs()) {
                    erreurList.add(erreur);
                }
            } else { // Propositions

                final PropositionGeocodage[] propositions = result.getPropositions();
                for (PropositionGeocodage proposition : propositions) {
                    propositionList.add(proposition);
                }
            }
            for (String option : result.getOptions()) {
                optionList.add(option); // Options

            }

        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.

            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.

            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new PropositionGeocodage[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    private static ResultatValidation aggregateValideResult(List<ResultatValidation> list) {
        final ResultatValidation resultatRet = new ResultatValidation();
        final List<ResultatErreur> erreurList = new ArrayList<ResultatErreur>();
        final List<PropositionValidation> propositionList = new ArrayList<PropositionValidation>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ResultatValidation result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs

                retourErreur = true;
                for (ResultatErreur erreur : result.getErreurs()) {
                    erreurList.add(erreur);
                }
            } else { // Propositions

                final PropositionValidation[] propositions = result.getPropositions();
                for (PropositionValidation proposition : propositions) {
                    propositionList.add(proposition);
                }
            }
            for (String option : result.getOptions()) {
                optionList.add(option); // Options

            }

        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.

            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.

            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new PropositionValidation[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    private static ResultatRevalidation aggregateRevalideResult(List<ResultatRevalidation> list) {
        final ResultatRevalidation resultatRet = new ResultatRevalidation();
        final List<ResultatErreur> erreurList = new ArrayList<ResultatErreur>();
        final List<PropositionRevalidation> propositionList = new ArrayList<PropositionRevalidation>();
        final List<String> optionList = new ArrayList<String>();
        boolean retourErreur = false;
        for (ResultatRevalidation result : list) {
            if (result.getCodeRetour() == 0) { // Erreurs

                retourErreur = true;
                for (ResultatErreur erreur : result.getErreurs()) {
                    erreurList.add(erreur);
                }
            } else { // Propositions

                final PropositionRevalidation[] propositions = result.getPropositions();
                for (PropositionRevalidation proposition : propositions) {
                    propositionList.add(proposition);
                }
            }
            for (String option : result.getOptions()) {
                optionList.add(option); // Options

            }

        }
        // Code retour
        if (retourErreur) { // Un service au moins a retourné une erreur.

            resultatRet.setCodeRetour(0);
        } else {// Aucun service n'a retourné d'erreur.

            resultatRet.setCodeRetour(1);
        }

        resultatRet.setErreurs(erreurList.toArray(new ResultatErreur[erreurList.size()]));
        resultatRet.setPropositions(propositionList.toArray(new PropositionRevalidation[propositionList.size()]));
        resultatRet.setOptions(optionList.toArray(new String[optionList.size()]));

        return resultatRet;
    }

    private static List<String> valueOf(String[] anarray) {
        final List<String> listRet = new ArrayList<String>();
        for (String str : anarray) {
            listRet.add(str);
        }

        return listRet;
    }

    private static int getServiceFromPrecision(int precision) {
        int intRet = 0;
        switch (precision) {
            case GestionInverse.GestionInverse_PLAQUE:
                intRet = 2;
                break;
            case GestionInverse.GestionInverse_TRONCON:
                intRet = 3;
                break;
            case GestionInverse.GestionInverse_VOIE:
                intRet = 4;
                break;
            case GestionInverse.GestionInverse_COMMUNE:
                intRet = 5;
                break;
            case GestionInverse.GestionInverse_DEPARTEMENT:
                intRet = 6;
                break;
            case GestionInverse.GestionInverse_PAYS:
                intRet = 7;
                break;
        }

        return intRet;
    }
}
