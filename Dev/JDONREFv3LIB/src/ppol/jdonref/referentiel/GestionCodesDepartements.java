/*
 * GestionCodesDepartements.java
 *
 * Created on 15/09/2011
 *
 * Version 2.3.0 – Sept 2011
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
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
package ppol.jdonref.referentiel;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ppol.jdonref.JDONREFException;
import ppol.jdonref.dao.CdpCodesPostauxBean;
import ppol.jdonref.dao.ReferentielDao;
import ppol.jdonref.mots.CategorieMot;
import ppol.jdonref.mots.Mot;
import ppol.jdonref.mots.RefCle;

/**
 * Gere une liste de codes departements et leurs eventuelles abreviations.
 * Permet de reconnaitre un departement a partie d'un code postal ou insee.
 * @author arochewi
 */
public class GestionCodesDepartements
{
    // Cles du fichier XML des synonymes

    private final static String SYN_ROOTELEMENT = "departements";
    private final static String SYN_CODEDPTELEMENT = "codeDpt";
    private final static String SYN_SYNONYMESCONT = "synonymes";
    private final static String SYN_CODEDPTCODEATTR = "code";
    private final static String SYN_SYNONYMEELEMENT = "synonyme";
    // Cles du fichier XML des algos
    private final static String ALGO_ROOTELEMENT = "departements";
    private final static String ALGO_CODEDPTELEMENT = "codeDpt";
    private final static String ALGO_CODEDPTATTR = "code";
    private final static String ALGO_ALGO = "algo";
    private final static String ALGO_ALGOTYPE = "type";
    private final static String ALGO_ALGOPARAM = "param";
    private final static String ALGO_ALGOPARAMNAME = "name";

    private enum algosTypes
    {

        FIXED, DEFAULT
    };
    // La taille d'un code insee ou d'un code postal : 5
    private final static int CODEINSEEORPOSTALSIZE = 5;
    // Les synonymes de codes departements
    private Map<String, String> synonymesCodes = null;
    // Les prefixes identifiant les codes departement a partir de leur code postal
    private Map<String, String> cpPrefixsCodes = null;
    // Les prefixes identifiant les codes departement a partir de leur code insee
    private Map<String, String> inseePrefixsCodes = null;
    // Le pattern representant tous les departement du referentiel
    private Pattern dptsPattern = null;
    // Le pattern representant un code postal, un departement du referentiel ou un simple nombre.
    private Pattern cPOrDptOrNumberPattern = null;
    // Le pattern representant un code postal ou un code departement dans une ligne 6 correcte
    private Pattern cpOrDptIntoLine6Pattern = null;
    // Indique si l'instance a ete initialisee ie les fichiers et les dpt ont ete charges
    private boolean initialized = false;
    // Singleton
    private static GestionCodesDepartements instance = null;

    private GestionCodesDepartements()
    {
        super();
        computeRegexps(null);
    }

    public static synchronized GestionCodesDepartements getInstance()
    {
        if(instance == null)
        {
            instance = new GestionCodesDepartements();
        }
        return instance;
    }

    /**
     * Charge tous les departements depuis le referentiel et le fichier de synonymes.
     * Calcule les expressions regulieres reconnaissant tous les departements charges.
     * Calcule les prefixes permettant de reconnaitre le departement d'un code postal.
     * @param dbConn
     * @param synonymesFilePath
     * @throws JDONREFException
     */
    public void loadDptCodes(Connection dbConn, String synonymesFilePath, String algosFilePath) throws JDONREFException
    {
        Set<CodeDepartement> codesDpt = loadAllDptsCodes(dbConn, synonymesFilePath);
        computeRegexps(codesDpt);
        computeSynonymes(codesDpt);
        List<AlgoDpt> algos = loadAlgosCPDptFile(algosFilePath);
        computePrefixesForDpts(dbConn, algos);   // CP et INSEE
        initialized = true;
    }

    /**
     * Calcule les expressions regulieres necessaires a la detection des codes departement et postaux
     * @param dpts
     */
    private void computeRegexps(Set<CodeDepartement> codesDpt)
    {
        StringBuilder cpOrDptOrNumBuilder = new StringBuilder();
        StringBuilder cpOrDptIntoLine6Builder = new StringBuilder();
        StringBuilder dptsBuilder = new StringBuilder("(");

        if((codesDpt != null) && (codesDpt.size() > 0))
        {
            boolean isFirst = true;
            for(CodeDepartement dpt : codesDpt)
            {
                if(!isFirst)
                {
                    dptsBuilder.append('|');
                }
                isFirst = false;
                dptsBuilder.append(dpt.getCodeOfficiel());
                if(dpt.getSynonymes() != null)
                {
                    for(String syn : dpt.getSynonymes())
                    {
                        dptsBuilder.append("|").append(syn);
                    }
                }
            }
        } else    // Valeur par defaut
        {
            dptsBuilder.append("[0-9]{2}");
        }
        dptsBuilder.append(")");

        // Le pattern des codes departements
        dptsPattern = Pattern.compile("\\b" + dptsBuilder.toString() + "\\b");

        // Le pattern global des codes postaux (groupe 1), codes departements (groupe 2), ou simples nombres (groupe 3).
        cpOrDptOrNumBuilder.append("\\b");
        cpOrDptOrNumBuilder.append("([0-9]{5})|");
        cpOrDptOrNumBuilder.append(dptsBuilder);
        cpOrDptOrNumBuilder.append("|([0-9]+)\\b");
        cPOrDptOrNumberPattern = Pattern.compile(cpOrDptOrNumBuilder.toString());

        // Le pattern des codes postaux ou departement au sein d'une ligne 6 correcte -> cp : gr 3, dpt : gr 4
        cpOrDptIntoLine6Builder.append("^([ ]*[A-Z]?[ ]*)");
        cpOrDptIntoLine6Builder.append("(");
        cpOrDptIntoLine6Builder.append("([0-9]{5})|(").append(dptsBuilder).append(")");
        cpOrDptIntoLine6Builder.append(")");
        cpOrDptIntoLine6Pattern = Pattern.compile(cpOrDptIntoLine6Builder.toString());
    }

    /**
     * Charge le fichier dont le chemin est passe, le parse pour en extraire les synonymes de codes departement.
     * @param filePath
     * @throws JDONREFException
     */
    private Set<CodeDepartement> loadSynonymesDptsFile(String filePath) throws JDONREFException
    {
        Set<CodeDepartement> codesFromFile = new HashSet<CodeDepartement>();

        SAXBuilder sb = new SAXBuilder();
        try
        {
            File file = new File(filePath);
            if(!file.exists())
            {
                throw (new IOException(filePath));
            }
            Document d = sb.build(file);
            Element root = d.getRootElement();
            if(root.getName().equals(SYN_ROOTELEMENT))
            {
                List<Element> children = (List<Element>) root.getChildren();
                for(Element child : children)
                {
                    if(child.getName().equals(SYN_CODEDPTELEMENT))
                    {
                        Attribute codeAttr = child.getAttribute(SYN_CODEDPTCODEATTR);
                        if((codeAttr != null) && (codeAttr.getValue() != null))
                        {
                            CodeDepartement dpt = new CodeDepartement(codeAttr.getValue());
                            // Synonymes
                            Element synonymesContainer = child.getChild(SYN_SYNONYMESCONT);
                            if(synonymesContainer != null)
                            {
                                List<Element> synonymes = (List<Element>) synonymesContainer.getChildren(SYN_SYNONYMEELEMENT);
                                for(Element syno : synonymes)
                                {
                                    dpt.addSynonyme(syno.getTextTrim());
                                }
                            }
                            codesFromFile.add(dpt);
                        } else
                        {
                            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE,
                                    "Un code departement du fichier de synomymes est vide.");
                        }
                    }
                }
            }
        } catch(JDOMException ex)
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Le fichier " + filePath + " est mal structuré.");
            throw (new JDONREFException("Le fichier " + filePath + " est mal structuré."));
        } catch(IOException ex)
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Le fichier " + filePath + " n'existe pas ou n'est pas lisible.");
            throw (new JDONREFException("Le fichier " + filePath + " n'existe pas ou n'est pas lisible."));
        } catch(NullPointerException npe)
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Exception lors du chargement du fichier de synomymes.");
            throw (new JDONREFException("Exception lors du chargement du fichier de synomymes."));
        }

        return codesFromFile;
    }

    /**
     * Charge le fichier dont le chemin est passe, le parse pour en extraire les algorithmes a utiliser
     * pour determiner a quel departement appartient un code postal.
     * @param filePath
     * @throws JDONREFException
     */
    private List<AlgoDpt> loadAlgosCPDptFile(String filePath) throws JDONREFException
    {
        List<AlgoDpt> algosForDpts = new ArrayList<AlgoDpt>();

        SAXBuilder sb = new SAXBuilder();
        try
        {
            File file = new File(filePath);
            if(!file.exists())
            {
                throw (new IOException(filePath));
            }
            Document d = sb.build(file);
            Element root = d.getRootElement();
            if(root.getName().equals(ALGO_ROOTELEMENT))
            {
                List<Element> children = (List<Element>) root.getChildren();
                for(Element child : children)
                {
                    AlgoDpt algo = new AlgoDpt();

                    if(child.getName().equals(ALGO_CODEDPTELEMENT))
                    {
                        Attribute codeAttr = child.getAttribute(ALGO_CODEDPTATTR);
                        if((codeAttr != null) && (codeAttr.getValue() != null))
                        {
                            algo.codeDpt = codeAttr.getValue();
                            Element algoElt = child.getChild(ALGO_ALGO);
                            if(algoElt != null)
                            {
                                Attribute algoTypeAttr = algoElt.getAttribute(ALGO_ALGOTYPE);
                                if((algoTypeAttr != null) && (algoTypeAttr.getValue() != null))
                                {
                                    List<Element> params = algoElt.getChildren(ALGO_ALGOPARAM);
                                    if(params != null)
                                    {
                                        for(Element algoParamElt : params)
                                        {
                                            algo.setTypeAlgo(algoTypeAttr.getValue());
                                            algo.params = new HashMap<String, String>();
                                            Attribute paramNameAttr = algoParamElt.getAttribute(ALGO_ALGOPARAMNAME);
                                            if((paramNameAttr != null) && (paramNameAttr.getValue() != null))
                                            {
                                                algo.params.put(paramNameAttr.getValue(), algoParamElt.getValue());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if((algo != null) && (algo.codeDpt != null) && (algo.typeAlgo != null))
                    {
                        algosForDpts.add(algo);
                    }
                }
            }
        } catch(JDOMException ex)
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Le fichier " + filePath + " est mal structuré.");
            throw (new JDONREFException("Le fichier " + filePath + " est mal structuré."));
        } catch(IOException ex)
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Le fichier " + filePath + " n'existe pas ou n'est pas lisible.");
            throw (new JDONREFException("Le fichier " + filePath + " n'existe pas ou n'est pas lisible."));
        } catch(NullPointerException npe)
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Exception lors du chargement du fichier des algorithmes de codes postaux.");
            throw (new JDONREFException("Exception lors du chargement du fichier des algorithmes de codes postaux."));
        }

        return algosForDpts;
    }

    /**
     * Calcule une Map ayant pour cle les synonymes de code departement et comme valeur le code officiel.
     * NB. Le code officiel est son propre synonyme
     * Ex. : (75, 75), (2 A, 20 A), (20 A, 20 A), etc ...
     * @param codesDpt
     */
    private void computeSynonymes(Set<CodeDepartement> codesDpt)
    {
        if(codesDpt != null)
        {
            synonymesCodes = new HashMap<String, String>();
            for(CodeDepartement dpt : codesDpt)
            {
                synonymesCodes.put(dpt.getCodeOfficiel(), dpt.getCodeOfficiel());
                if(dpt.getSynonymes() != null)
                {
                    for(String synonyme : dpt.getSynonymes())
                    {
                        synonymesCodes.put(synonyme, dpt.getCodeOfficiel());
                    }
                }
            }
        }
    }

    /**
     * Charge tous les codes departements de la base et leur associe leurs eventuels synonymes
     * @param rawDptsCodes
     * @param synonymesFromFile
     * @return
     */
    private Set<CodeDepartement> associatesDptsWithSynonyms(List<String> rawDptsCodes, Set<CodeDepartement> synonymesFromFile)
    {
        if((rawDptsCodes == null) || (synonymesFromFile == null) || rawDptsCodes.isEmpty())
        {
            Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE,
                    "Aucun departement trouve en base ou fichier de synonyme incorrect.");
        }

        Set<CodeDepartement> allDpts = new HashSet<CodeDepartement>();

        for(String rawDptCode : rawDptsCodes)
        {
            CodeDepartement dpt = null;
            for(CodeDepartement synDpt : synonymesFromFile)
            {
                if(synDpt.hasSameCodeOfficiel(rawDptCode))
                {
                    dpt = synDpt;
                    synonymesFromFile.remove(synDpt);
                    break;
                }
            }
            if(dpt == null) // ie pas de synonyme trouve
            {
                dpt = new CodeDepartement(rawDptCode);
            }
            allDpts.add(dpt);
        }

        return allDpts;
    }

    /**
     * Charge les departements en base, le fichier des synonymes, croise les deux.
     * Charge le fichier des algo specifiques pour le couplage CP / dpt
     * @param dbConn
     * @param synonymesFilePath
     * @param algosFilePath
     * @throws JDONREFException
     */
    private Set<CodeDepartement> loadAllDptsCodes(Connection dbConn, String synonymesFilePath) throws JDONREFException
    {
        List<String> rawsDpts = ReferentielDao.getAllActualDptCodes(dbConn, new Date());
        Set<CodeDepartement> syn = loadSynonymesDptsFile(synonymesFilePath);
        Set<CodeDepartement> codesDpt = associatesDptsWithSynonyms(rawsDpts, syn);
        return codesDpt;
    }

    /**
     * Calcule pour chaque departement du referentiel les plus petits prefixes permetant d'associer 
     * un code postal et insee a un departement.
     * Typiquement, il sagira des deux premiers caracteres du code postal / insee sauf pour la corse et les dom toms.
     * @param dbConn
     * @throws JDONREFException
     */
    private void computePrefixesForDpts(Connection dbConn, List<AlgoDpt> algos) throws JDONREFException
    {
        List<Set<CdpCodesPostauxBean>> cpAndInseeByCdp = getCpsByCodesDpt(dbConn);
        computePrefixesForDpts(true, cpAndInseeByCdp, algos);
        computePrefixesForDpts(false, cpAndInseeByCdp, algos);
    }

    /**
     * Calcule pour chaque departement du referentiel les prefixes premettant d'identifier respectivement les codes insee ou postaux.
     * Les algorithmes de la liste 'algos'sont utilises si presents, le defaut etant l'algorithme permettant de trouver les plus petit
     * prefixes automatiquement.
     * @param forCp true : effectue le calcul pour les codes postaux, false pour les codes insee
     * @param  cpAndInseeByCdp : les codesPostaux et codesInsee par departement
     * @throws JDONREFException
     */
    private void computePrefixesForDpts(boolean forCp, List<Set<CdpCodesPostauxBean>> cpAndInseeByCdp, List<AlgoDpt> algos) throws
            JDONREFException
    {
        if(forCp)
        {
            cpPrefixsCodes = new HashMap<String, String>();
        } else
        {
            inseePrefixsCodes = new HashMap<String, String>();
        }

        if(cpAndInseeByCdp != null)
        {
            for(Set<CdpCodesPostauxBean> curSetForDpt : cpAndInseeByCdp)
            {
                String curDptCode = "";
                if((curSetForDpt != null) && (curSetForDpt.size() > 0))
                {
                    curDptCode = curSetForDpt.iterator().next().getCodeDepartement();
                }

                Set<String> algoResult;
                if(forCp)
                {
                    algosTypes curAlgo = findAlgoToUseForDpt(curDptCode, algos);
                    switch (curAlgo)
                    {
                        case FIXED: { algoResult = doFixedAlgo(curDptCode, algos); break; }
                        case DEFAULT:
                        default: { algoResult = doSmallestPrefixAlgo(curDptCode, curSetForDpt, forCp, cpAndInseeByCdp); break; }
                    }
                    if( (algoResult == null) || (algoResult.isEmpty()) )
                    {
                        // Si on n'a pas trouve de resultat avec l'algo choisi, on utilise une strategie de repli : on revient aux deux premiers chiffres du code postal
                        Logger.getLogger("GestionCodesDepartements").log(Level.WARNING,
                                "Utilisation d'un prefixe de code postal par défaut pour le département " + curDptCode + " certains de ses codes postaux sont aussi présents dans d'autres départements.");
                        cpPrefixsCodes.put(curDptCode, curDptCode);
                    } else
                    {
                        for(String resultItem : algoResult)
                        {
                            cpPrefixsCodes.put(resultItem, curDptCode);
                        }
                    }
                } else
                {
                    algoResult = doSmallestPrefixAlgo(curDptCode, curSetForDpt, forCp, cpAndInseeByCdp);
                    if( (algoResult == null) || (algoResult.isEmpty()) )
                    {
                        // Par contre c'est le signe d'une inconcistance du referentiel pour les codes insee.
                        Logger.getLogger("GestionCodesDepartements").log(Level.SEVERE, "Erreur lors du calcul des préfixes. Il est impossible d'établir de listes de prefixes de codes insee pour le code département " + curDptCode);
                        throw new JDONREFException("Impossible d'établir de liste de préfixes de codes insee pour le département " + curDptCode + ".");
                    } else
                    {
                        for(String resultItem : algoResult)
                        {
                            inseePrefixsCodes.put(resultItem, curDptCode);
                        }
                    }
                }
            }
        }
    }

    private algosTypes findAlgoToUseForDpt(String dptCode, List<AlgoDpt> algos)
    {
        algosTypes result = algosTypes.DEFAULT;
        if(dptCode != null)
        {
            for(AlgoDpt item : algos)
            {
                if(dptCode.equalsIgnoreCase(item.codeDpt))
                {
                    result = item.typeAlgo;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Retourne comme prefixe pour un departement une valeur fixe.
     * @param dptCode
     * @return
     */
    private Set<String> doFixedAlgo(String dptCode, List<AlgoDpt> algos)
    {
        Set<String> prefixsForCurDpt = new HashSet<String>();
        for(AlgoDpt algo : algos)
        {
            if(dptCode.equalsIgnoreCase(algo.codeDpt))
            {
                String prefix = algo.params.get("prefix");
                if(prefix != null)
                    prefixsForCurDpt.add(prefix);
            }
        }
        
        return prefixsForCurDpt;
    }

    /**
     * Calcule pour un departement les plus petits prefixes permetant d'associer
     * un code postal / insee a un departement.
     * Typiquement, il sagira des deux premiers caracteres du code postal / insee sauf pour la corse et les dom toms.
     */
    private Set<String> doSmallestPrefixAlgo(String dptCode, Set<CdpCodesPostauxBean> curSetForDpt, boolean forCp,
            List<Set<CdpCodesPostauxBean>> cpAndInseeByCdp) throws JDONREFException
    {
        // On cherche tous les prefixes differents de longueur N
        Set<String> prefixsForCurDpt = new HashSet<String>();
        boolean allPrefixsAreGood = false;
        String tmpPref;
        for(int i = 2; i <= CODEINSEEORPOSTALSIZE; i++)
        {
            Set<String> potentialPrefixs = new HashSet<String>();
            for(CdpCodesPostauxBean item : curSetForDpt)
            {
                tmpPref = (forCp) ? item.getCodePostal().substring(0, i) : item.getCodeInsee().substring(0, i);
                potentialPrefixs.add(tmpPref);  // Un set -> pas de doublons
            }
            // On verifie pour chaque preffixe si il n'est pas present dans un autre departement
            allPrefixsAreGood = true;
            for(String pref : potentialPrefixs)
            {
                // On ignore les prefixes deja couverts par un prefix plus court.
                if(!isAPreffixMatched(pref, prefixsForCurDpt))
                {
                    if(!isPrefixPresentIntoOtherDpt(dptCode, pref, cpAndInseeByCdp, forCp))
                    {
                        prefixsForCurDpt.add(pref);
                    } else
                    {
                        allPrefixsAreGood = false;
                    }
                }
            }
            if(allPrefixsAreGood)
                break;
        }
        if(allPrefixsAreGood)
            return prefixsForCurDpt;
        else
            return null;
    }

    /**
     * Recupere tous codes postaux, codes insee et codes dpt depuis la base
     * Organise le tout sous forme de liste de Set : un set par departement
     * @param dbConn
     * @return
     * @throws JDONREFException
     */
    private List<Set<CdpCodesPostauxBean>> getCpsByCodesDpt(Connection dbConn) throws JDONREFException
    {
        List<CdpCodesPostauxBean> rawCodesPostaux = ReferentielDao.getAllActualCdpCodesPostaux(dbConn, new Date());
        List<Set<CdpCodesPostauxBean>> cpByCdp = new ArrayList<Set<CdpCodesPostauxBean>>();

        if((rawCodesPostaux != null))
        {
            String curCodeDpt = null;
            Set<CdpCodesPostauxBean> listForDpt = null;
            for(CdpCodesPostauxBean item : rawCodesPostaux)
            {
                if(!item.getCodeDepartement().equals(curCodeDpt))
                {
                    if(listForDpt != null)
                    {
                        cpByCdp.add(listForDpt);
                    }
                    listForDpt = new HashSet<CdpCodesPostauxBean>();
                    curCodeDpt = item.getCodeDepartement();
                }
                listForDpt.add(item);
            }
            if(listForDpt != null)
            {
                cpByCdp.add(listForDpt);
            }
        }
        return cpByCdp;
    }

    /**
     * Cherche si il existe un code postal d'un departement autre que dptCode
     * commencant par prefix.
     * La recherche est faite dans la liste de sets de codes postaux cps
     * @param dptCode
     * @param prefix
     * @param cps
     * @param forCp : true pour les codes postaux, false pour les codes insee.
     * @return
     */
    private boolean isPrefixPresentIntoOtherDpt(String dptCode, String prefix, List<Set<CdpCodesPostauxBean>> cps, boolean forCp)
    {
        boolean found = false;
        for(Set<CdpCodesPostauxBean> curSetForDpt : cps)
        {
            if((curSetForDpt != null) && (curSetForDpt.size() > 0))
            {
                String curDptCode = "";
                if((curSetForDpt != null) && (curSetForDpt.size() > 0))
                {
                    curDptCode = curSetForDpt.iterator().next().getCodeDepartement();
                }
                if(!curDptCode.equals(dptCode))
                {
                    for(CdpCodesPostauxBean item : curSetForDpt)
                    {
                        String code = (forCp) ? item.getCodePostal() : item.getCodeInsee();
                        if(code.startsWith(prefix))
                        {
                            found = true;
                            break;
                        }
                    }
                }
            }
            if(found)
            {
                break;
            }
        }
        return found;
    }

    /**
     * Indique si une chaine corresponds a un des preffixs du set de prefixs.
     * Pour une meilleure optimisation, il est souhaitable que l'implementation du Set
     * soit la plus rapide possible dans le domaine de la recherche -> hashSet.
     * @param str
     * @param prefixs
     * @return
     */
    private boolean isAPreffixMatched(String str, Set<String> prefixs)
    {
        if((str != null) && (str.length() > 1))
        {
            for(int i = 2; i < str.length(); i++)
            {
                if(prefixs.contains(str.substring(0, i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Indique si le code dpt est present dans la liste.
     * @param dpt
     * @return
     */
    public boolean isDptCodePresent(String dpt)
    {
        boolean result = false;
        if((synonymesCodes != null) && (dpt != null))
        {
            result = (synonymesCodes.get(dpt) != null);
        }
        return result;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public Pattern getDptsPattern()
    {
        return dptsPattern;
    }

    public Pattern getDptOrCpOrNumberPattern()
    {
        return cPOrDptOrNumberPattern;
    }

    public int getDptGroupIntoCPOrDptOrNumberPattern()
    {
        return 2;
    }

    public int getCpGroupIntoCPOrDptOrNumberPattern()
    {
        return 1;
    }

    public int getNumberGroupIntoCPOrDptOrNumberPattern()
    {
        return 3;
    }

    public int getCpGroupIntoCpOrDptIntoLine6Pattern()
    {
        return 3;
    }

    public int getDptGroupIntoCpOrDptIntoLine6Pattern()
    {
        return 4;
    }

    /**
     * Calcule de code departement correspondant a un code postal.
     * Pour ce faire, utilise la Map des couples 'prefix', code departement initialisée
     * dans computePrefixesForDpts
     * Si GestionCodesDepartements n'a pas ete correctement initialise, l'ancien algo est utilise :
     * deux premiers caracteres du code postal.
     * @param codePostal
     * @return le code departement
     */
    public String computeCodeDptFromCodePostal(String codePostal)
    {
        if((codePostal != null) && (codePostal.length() > 1))
        {
            if(initialized)
            {
                for(int i = 2; i < codePostal.length(); i++)
                {
                    String potentialCodeDpt = cpPrefixsCodes.get(codePostal.substring(0, i));
                    if(potentialCodeDpt != null)
                    {
                        return potentialCodeDpt;
                    }
                }
            } else
            {
                return codePostal.substring(0, 2);
            }
        }
        return null;
    }

    /**
     * Calcule de code departement correspondant a un code insee.
     * Si taille de codeInsee <2 on leve une erreur.
     * Si taille de codeInsee == CODEINSEEORPOSTALSIZE : c'est un code insee : on utilise alors la Map de prefixes de codes insee
     * Si codeInsee matche la regexp calculee a l'initialisation, c'est un code departement : on utilise alors getOfficialCodeDpt
     * Si GestionCodesDepartements n'a pas ete correctement initialise, ou si on ne trouve pas, l'ancien algo est utilise :
     * deux premiers caracteres du code insee.
     * @param codeInsee
     * @return le code departement
     */
    public String computeCodeDptFromCodeInseeOrCodeDpt(String codeInsee) throws GestionReferentielException
    {
        if((codeInsee == null) || (codeInsee.length() < 2)) // logguée à un niveau supérieur.
        {
            throw (new GestionReferentielException("Le code insee est invalide", GestionReferentielException.PARAMETREERRONNE, 5));
        }

        String potentialCodeDpt = null;
        if(initialized)
        {
            if(codeInsee.length() == CODEINSEEORPOSTALSIZE)
            {   // C'est un code insee
                potentialCodeDpt = computeCodeDptFromCodeInsee(codeInsee);
            } else
            {
                Matcher match = dptsPattern.matcher(codeInsee);
                if(match.find())
                {
                    potentialCodeDpt = this.getOfficialCodeDpt(codeInsee);
                }
            }
        }
        if(potentialCodeDpt == null)
        {
            potentialCodeDpt = codeInsee.substring(0, 2);
        }
        return potentialCodeDpt;
    }

    /**
     * Determine le code departement correspondant a un code insee bien forme (CODEINSEEORPOSTALSIZE chiffres de long)
     * @param codeInsee
     * @return le code dpt officiel trouve, null sinon
     */
    private String computeCodeDptFromCodeInsee(String codeInsee)
    {
        if((codeInsee != null) && (codeInsee.length() > 1))
        {
            if(initialized)
            {
                for(int i = 2; i < codeInsee.length(); i++)
                {
                    String potentialCodeDpt = inseePrefixsCodes.get(codeInsee.substring(0, i));
                    if(potentialCodeDpt != null)
                    {
                        return potentialCodeDpt;
                    }
                }
            } else
            {
                return codeInsee.substring(0, 2);
            }
        }
        return null;
    }

    /**
     * Extrait un code departement d'une chaine
     * Le code departement est extrait en utilisant les expressions regulieres calculees a l'initialisation.
     * Le code postal ou le code dpt doivent etre situes en debut de chaine eventuellement precede d'un
     * unique caractere eventuellement entoure d'espaces.
     * Si c'est un code postal qui est trouve, son code departement en est extrait.
     * Le code departement retourne est la version officielle du code departement et non pas son synonyme.
     * La chaine en entree doit etre a un format de ligne 6.
     * @param srcStr
     * @return
     */
    public RefCle extractCodeDptFromString(String srcStr)
    {
        RefCle result = extractCodeDptOrCodePostalFromString(srcStr);

        if(result != null)
        {
            if(result.obtientCategorieMot() == CategorieMot.CodePostal)
            {   // Deduction du code departement depuis le code postal
                String computedDpt = computeCodeDptFromCodePostal(result.obtientMot());
                if(computedDpt != null)
                {
                    return new RefCle(computedDpt, (Mot) null, result.obtientIndex(), result.obtientChaineOriginale(),
                            CategorieMot.CodeDepartement);
                } else
                {
                    return new RefCle("");
                }
            } else if(result.obtientCategorieMot() == CategorieMot.CodeDepartement)
            {   // Deduction du code departement officiel depuis un potentiel sysnonyme
                return new RefCle(getOfficialCodeDpt(result.obtientMot()), (Mot) null, result.obtientIndex(),
                        result.obtientChaineOriginale(),
                        CategorieMot.CodeDepartement);
            }
        }
        return result;
    }

    /**
     * Extrait, en utilisant les expressions calculees a l'initialisation, les codes posteaux ou departement
     * trouves dans la cahine en entree.
     * Le code postal ou le code dpt doivent etre situes en debut de chaine eventuellement precede d'un
     * unique caractere eventuellement entoure d'espaces.
     * La chaine en entree doit etre a un format de ligne 6.
     * @param srcStr
     * @return
     */
    public RefCle extractCodeDptOrCodePostalFromString(String srcStr)
    {
        RefCle result = null;

        if(cpOrDptIntoLine6Pattern != null)
        // On dispose d'un cache de codes departements on l'utilise donc.
        {
            Matcher match = cpOrDptIntoLine6Pattern.matcher(srcStr);
            if(match.find())
            {
                String cp = match.group(getCpGroupIntoCpOrDptIntoLine6Pattern());
                String dpt = match.group(getDptGroupIntoCpOrDptIntoLine6Pattern());

                if(cp != null)
                {
                    result = new RefCle(cp, (Mot) null, match.start(getCpGroupIntoCPOrDptOrNumberPattern()), srcStr, CategorieMot.CodePostal);
                } else if(dpt != null)
                {
                    result = new RefCle(dpt, (Mot) null, match.start(getDptGroupIntoCPOrDptOrNumberPattern()), srcStr,
                            CategorieMot.CodeDepartement);
                }
            }
        }
        return (result == null) ? (new RefCle("", 0)) : result;
    }

    /**
     * A partir d'un code departement dont on ignore s'il sagit d'un code officiel ou d'un synonyme,
     * retourne le code officiel.
     * @param potentialDptCode
     * @return
     */
    public String getOfficialCodeDpt(String potentialDptCode)
    {
        String result = null;
        if((synonymesCodes != null) && (potentialDptCode != null))
        {
            result = synonymesCodes.get(potentialDptCode);
        }
        return (result == null) ? (potentialDptCode) : result;
    }

    /**
     * Classe privee representant un code departement avec ses sysnonymes.
     */
    private class CodeDepartement implements Comparable
    {

        private final static String NULLDPTCODEERRSTR = "Null department code !";
        private final String codeOfficiel;
        private Set<String> synonymes = null;

        /**
         * Instancie un nouveau code de departement
         *
         * @param code le code officiel du departement. Doit etre non null.
         * @throws
         */
        public CodeDepartement(String code) throws NullPointerException
        {
            if(code == null)
            {
                throw new NullPointerException(NULLDPTCODEERRSTR);
            }
            codeOfficiel = code;
        }

        private void initSynonymes()
        {
            if(synonymes == null)
            {
                synonymes = new TreeSet<String>();
            }
        }

        public boolean addSynonyme(String synonyme)
        {
            boolean alreadyInSet = false;
            if(synonyme != null)
            {
                initSynonymes();
                alreadyInSet = synonymes.add(synonyme);
            }
            return alreadyInSet;
        }

        public String getCodeOfficiel()
        {
            return codeOfficiel;
        }

        public Set<String> getSynonymes()
        {
            return synonymes;
        }

        /**
         * Indique si le codeDepartement a pour code officiel celui passe en parametre.
         * @param otherCode
         * @return
         */
        public boolean hasSameCodeOfficiel(String otherCode)
        {
            return codeOfficiel.equals(otherCode);
        }

        /**
         * Indique si le codeDepartement a pour code ou pour synonyme celui passe en param.
         * @param otherCode
         * @return
         */
        public boolean hasSameCode(String otherCode)
        {
            return hasSameCodeOfficiel(otherCode) || ((synonymes != null) && synonymes.contains(otherCode));
        }

        @Override
        public boolean equals(Object obj)
        {
            if((obj == null) || (!(obj instanceof CodeDepartement)))
            {
                return false;
            }

            final CodeDepartement otherCodeDpt = (CodeDepartement) obj;
            if(!codeOfficiel.equals(otherCodeDpt.codeOfficiel))
            {
                return false;
            }
            if((synonymes == null) && (otherCodeDpt.synonymes == null))
            {
                return true;
            }
            if(((synonymes == null) && (otherCodeDpt.synonymes != null)) || ((synonymes != null) && (otherCodeDpt.synonymes == null)))
            {
                return false;
            }
            return synonymes.equals(otherCodeDpt.synonymes);
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 97 * hash + (this.codeOfficiel != null ? this.codeOfficiel.hashCode() : 0);
            hash = 97 * hash + (this.synonymes != null ? this.synonymes.hashCode() : 0);
            return hash;
        }

        public int compareTo(Object o)
        {
            if((o == null) || (!(o instanceof CodeDepartement)))
            {
                return 1;
            }
            final CodeDepartement otherCodeDpt = (CodeDepartement) o;

            return codeOfficiel.compareToIgnoreCase(otherCodeDpt.codeOfficiel);
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder((codeOfficiel == null) ? "NULL" : codeOfficiel);
            if(synonymes != null)
            {
                builder.append(" (");
                for(String syn : synonymes)
                {
                    builder.append(syn).append(",");
                }
                builder.append(")");
            }
            return builder.toString();
        }
    }

    /**
     * Classe privee representant un element du fichier des algos
     * permettant de reconnaitre un departement a aprtir d'un code postal.
     */
    private class AlgoDpt
    {

        private String codeDpt;
        private algosTypes typeAlgo;
        private Map<String, String> params;

        public void setTypeAlgo(String typeAlgoStr)
        {
            typeAlgo = algosTypes.DEFAULT;
            if(typeAlgoStr != null)
            {
                for(algosTypes type : algosTypes.values())
                {
                    if(typeAlgoStr.equalsIgnoreCase(type.name()))
                    {
                        typeAlgo = type;
                        break;
                    }
                }
            }
        }
    }
}
