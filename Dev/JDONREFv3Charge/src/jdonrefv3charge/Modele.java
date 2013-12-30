/*
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
package jdonrefv3charge;

import org.jdom.Element;

/**
 * Permet de gérer un modèle qui génère des adresses.
 * @author jmoquet
 */
public class Modele {

    public String nom;
    public boolean cle_poizon;
    public boolean libelle_poizon;
    public boolean ligne4_poizon;
    public boolean ligne6_poizon;
    public boolean cles;
    public boolean numero;
    public boolean repetition;
    public boolean numerossupplementaires;
    public boolean typedevoie;
    public boolean article;
    public boolean libelle;
    public boolean departement;
    public boolean cp;
    public boolean ville;
    public boolean arrondissement;
    public boolean pays;
    public boolean id_ville;
    public boolean id_pays;
    public boolean xy;

    /**
     * Constructeur par défaut.
     */
    public Modele() {
    }

    /**
     * Génère une adresse utilisant le modèle.<br>
     * L'adresse en paramètre ou son origine est alors l'origine de l'adresse retournée.
     * N.B.: le code postal est prioritaire au département.
     * @param adresse une adresse contenant un typedevoie, un article, un libellé, un code postal, une ville.
     * @return l'adresse correspondant au modèle dont les lignes n'ont pas été recalculées.
     */
    public void genereAdresse(Adresse adresse) {
        if (cles) {
            adresse.genereCles();
        } else {
            adresse.cles.clear();
        }

        if (numero) {
            adresse.genereNumero();
        } else {
            adresse.numero = "";
        }

        if (repetition) {
            adresse.genereRepetition();
        } else {
            adresse.repetition = "";
        }

        if (numerossupplementaires) {
            adresse.genereNumerosSupplementaires();
        } else {
            adresse.numerossupplementaires = "";
        }

        if (!typedevoie) {
            adresse.typedevoie = "";
        }

        if (!article) {
            adresse.article = "";
        }

        if (!libelle) {
            adresse.libelle = "";
        }

        if (!cp) {
            if (departement && adresse.codepostal != null && adresse.codepostal.length() > 1) {
                adresse.codepostal = adresse.codepostal.substring(0, 2);
            } else {
                adresse.codepostal = "";
            }
        }

        if (!ville) {
            adresse.ville = "";
        }

        if (arrondissement) {
            adresse.genereArrondissement();
        } else {
            adresse.arrondissement = "";
        }

        if (!pays) {
            adresse.pays = "";
        }

        if (!cle_poizon) {
            adresse.clePoizon = "";
        }

        if (!libelle_poizon) {
            adresse.libellePoizon = "";
        }

        if (!ligne4_poizon) {
            adresse.ligne4Poizon = "";
        }


        if (!ligne6_poizon) {
            adresse.ligne6Poizon = "";
        }

        if (!id_ville) {
            adresse.codeinsee = "";
        }

        if (!id_pays) {
            adresse.codeSovAc3 = "";
        }

        if (!xy) {
            adresse.x = "";
            adresse.y = "";
        }

        adresse.modele = nom;
    }

    /**
     * Permet de charger le modèle à partir d'une représentation DOM XML.<br>
     * Les valeurs non trouvées sont définies à false.
     * @param e
     */
    public void load(Element e) throws Exception {
        Element e_nom = e.getChild("nom");
        Element e_cles = e.getChild("cles");
        Element e_numero = e.getChild("numero");
        Element e_repetition = e.getChild("repetition");
        Element e_numerossupplementaires = e.getChild("numerossupplementaires");
        Element e_typedevoie = e.getChild("typedevoie");
        Element e_article = e.getChild("article");
        Element e_libelle = e.getChild("libelle");
        Element e_cp = e.getChild("cp");
        Element e_departement = e.getChild("departement");
        Element e_ville = e.getChild("ville");
        Element e_arrondissement = e.getChild("arrondissement");
        Element e_pays = e.getChild("pays");
        Element e_cle_poizon = e.getChild("cle_poizon");
        Element e_libelle_poizon = e.getChild("libelle_poizon");
        Element e_ligne4_poizon = e.getChild("ligne4_poizon");
        Element e_ligne6_poizon = e.getChild("ligne6_poizon");
        Element e_id_ville = e.getChild("id_ville");
        Element e_id_pays = e.getChild("id_pays");
        Element e_xy = e.getChild("xy");

        if (e_nom == null) {
            throw (new Exception("Le modèle n'a pas de nom."));
        }

        nom = e_nom.getValue();
        if (e_cles != null) {
            cles = true;
        }
        if (e_numero != null) {
            numero = true;
        }
        if (e_repetition != null) {
            repetition = true;
        }
        if (e_numerossupplementaires != null) {
            numerossupplementaires = true;
        }
        if (e_typedevoie != null) {
            typedevoie = true;
        }
        if (e_article != null) {
            article = true;
        }
        if (e_libelle != null) {
            libelle = true;
        }
        if (e_cp != null) {
            cp = true;
        }
        if (e_departement != null) {
            departement = true;
        }
        if (e_ville != null) {
            ville = true;
        }
        if (e_arrondissement != null) {
            arrondissement = true;
        }
        if (e_pays != null) {
            pays = true;
        }
        if (e_cle_poizon != null) {
            cle_poizon = true;
        }
        if (e_libelle_poizon != null) {
            libelle_poizon = true;
        }
        if (e_ligne4_poizon != null) {
            ligne4_poizon = true;
        }        
        if (e_ligne6_poizon != null) {
            ligne6_poizon = true;
        }
        if (e_id_ville != null) {
            id_ville = true;
        }
        if (e_id_pays != null) {
            id_pays = true;
        }
        if (e_xy != null) {
            xy = true;
        }
    }
}
