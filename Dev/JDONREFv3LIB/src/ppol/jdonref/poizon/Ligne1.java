/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.poizon;

import ppol.jdonref.Algos;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.mots.Abbreviation;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.mots.Mot;
import ppol.jdonref.referentiel.GestionMiseAJour;
import ppol.jdonref.referentiel.GestionReferentiel;

/**
 *
 * @author marcanhe
 */
public class Ligne1 {

    private final Services services;
    private final GestionMots gm;
    private final String ligne1;
    private String article = "";
    private String cle = "";
    private String cleAbb = "";
    private String articleDeLiaison = "";
    private String libelle = "";
    private String libelleSansArticle = "";
    private String nomService = "";

    public static Ligne1 getNewInstance(String ligne1, Services services, GestionMots gm) {
        return new Ligne1(ligne1, services, gm).decoupe();
    }
    
    public String getArticle() {
        return article;
    }

    public String getCle() {
        return cle;
    }

    public String getClePhonetique() {
        return Algos.phonexNonVide(cle);
    }

    public String getArticleDeLiaison() {
        return articleDeLiaison;
    }

    public String getLibelleSansArticle() {
        return libelleSansArticle;
    }

    public String getLibelleSansArticlePhonetique() {
        return Algos.phonexNonVide(libelleSansArticle);
    }

    public String getLibelle() {
        return libelle;
    }

    public String getLigne1() {
        return ligne1;
    }

    public String getNomService() {
        return nomService;
    }
    
    private Ligne1(String ligne1, Services services, GestionMots gm) {
        this.ligne1 = ligne1.trim();
        this.services = services;
        this.gm = gm;
    }

    private Ligne1 decoupe() {
        trouveCle(ligne1);
        if (cle.equals("")) {
            trouveArticle(ligne1);
            trouveCle(ligne1.substring(article.length()).trim());
            // Il n'y a pas de clé.
            if (cle.equals("")) {
                article = ""; // L'article appartient au libellé.
            }
        }
        trouveLibelle();
        
        return this;
    }

    private void trouveArticle(String ligne) {
        article = Algos.trouveArticle(ligne).trim();
    }

    private void trouveCle(String ligne) {
        final Mot mot = gm.chercheMotPourRoutage(ligne);
        final Abbreviation abb = gm.chercheAbbreviationPourRoutage(ligne);
        Mot motAbb = null;
        if (abb != null) {
            cleAbb = abb.obtientNom();
            int priorite = Abbreviation.PRIORITE_MAX;
            final int countMot = abb.obtientCompteMot();
            for (int i = 0; i < countMot; i++) {
                final Mot amot = abb.obtientMot(i);
                final int apriorite = abb.obtientPrioriteMot(i);
                if (apriorite < priorite || (motAbb == null && apriorite == priorite)) {
                    priorite = apriorite;
                    motAbb = amot;
                }
            }
        }
        if (mot != null && abb != null) {
            final Mot motChoisi = ((mot.obtientNom().length() >= motAbb.obtientNom().length()) ? mot : motAbb);
            final int size = motChoisi.obtientCompteCategorie();
            for (int i = 0; i < size; i++) {
                if (services.getNoms().contains(motChoisi.obtientCategorie(i).toString())) {
                    cle = motChoisi.obtientNom();
                    nomService = motChoisi.obtientCategorie(i).toString();
                    break;
                }
            }

        } else if (mot != null && abb == null) {
            final int countCat = mot.obtientCompteCategorie();
            for (int i = 0; i < countCat; i++) {
                if (services.getNoms().contains(mot.obtientCategorie(i).toString())) {
                    cle = mot.obtientNom();
                    nomService = mot.obtientCategorie(i).toString();
                    break;
                }
            }
        } else if (mot == null && abb != null) {
            final int countCat = motAbb.obtientCompteCategorie();
            for (int i = 0; i < countCat; i++) {
                if (services.getNoms().contains(motAbb.obtientCategorie(i).toString())) {
                    cle = motAbb.obtientNom();
                    nomService = motAbb.obtientCategorie(i).toString();
                    break;
                }
            }
        }
    }

    private void trouveLibelle() {
        final String cleStr = (cleAbb != null && cleAbb.trim().length() > 0) ? cleAbb : cle;
        final int indexDebut = ligne1.indexOf(cleStr);
        final StringBuilder sb = new StringBuilder();
        if (indexDebut > -1) {
            final String finStr = ligne1.substring(indexDebut + cleStr.length());
            sb.append((finStr != null) ? finStr.trim() : "");
        }
        final String libelleStr = sb.toString();
        if (libelleStr != null && libelleStr.trim().length() > 0) {
            articleDeLiaison = Algos.trouveArticleDeLiaison(libelleStr);
            libelle = libelleStr.substring(articleDeLiaison.length()).trim();
            libelleSansArticle = Algos.sansarticles(libelle).trim();
        }else if(libelleStr != null && libelleStr.trim().equals("")){
            libelle = ligne1;
            libelleSansArticle = Algos.sansarticles(libelle).trim();
            article = "";
            cle = "";
            cleAbb = "";
            articleDeLiaison = "";
            
        }
    }

    public static void main(String[] args) {
        try {
            final String configPath = "C:\\JDONREF_v3\\Dev\\Src\\JPOIZONREFv3\\web\\META-INF\\";
            final JDONREFParams params = new JDONREFParams();
            params.load(configPath, "params.xml");
            // Initialise les mots.
            GestionMots gm = new GestionMots();
            gm.initMots(configPath + "abbreviations.xml", configPath + "cles.xml", configPath + "prenoms.txt");

            // Définit le gestionnaire de mise à jour
            final GestionMiseAJour gmaj = new GestionMiseAJour(gm, params);

            // Définit le gestionnaire de référentiel.
            final GestionReferentiel referentiel = new GestionReferentiel(gm, gmaj, params);

            // Fait le lien entre le gestionnaire de mots et le gestionnaire de référentiel.
            gm.definitGestionReferentiel(referentiel);

            // Initialise les paramètres de gestionMots.
            gm.definitJDONREFParams(params);
            
            System.out.println("config-path : " + params.obtientConfigPath());
            final String ligne1Str = " LE ARDT II";
            Ligne1 ligne1 = new Ligne1(ligne1Str, Services.getInstance(configPath), gm).decoupe();            
            System.out.println("ligne1 : " + "|" + ligne1.ligne1 + "|");
            System.out.println("article : " + "|" + ligne1.article + "|");
            System.out.println("cle : " + "|" + ligne1.cle + "|");
            System.out.println("cleAbb : " + "|" + ligne1.cleAbb + "|");
            System.out.println("articleDeLiaison : " + "|" + ligne1.articleDeLiaison + "|");
            System.out.println("libelle : " + "|" + ligne1.libelle + "|");
            System.out.println("libelleSansArticle : " + "|" + ligne1.libelleSansArticle + "|");
            System.out.println("libelleSansArticlePhonetique : " + "|" + ligne1.getLibelleSansArticlePhonetique() + "|");
            System.out.println("nomService : " + "|" + ligne1.getNomService() + "|");
            System.out.println(ligne1.gm.corrige_abbreviations_ligneX(ligne1.getLigne1(), 1));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
