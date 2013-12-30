/*
 * Version 2.2 – Janvier 2010
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC
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
package ppol.jdonref.referentiel.reversegeocoding;

import com.vividsolutions.jts.geom.Point;

/**
 * Représente le résultat d'un géocodage inverse au niveau voie
 * 
 * @author Moquet Julien
 */
public class GeocodageInverse_Voie extends GeocodageInverse_Commune {

    /**
     * Identifiant de la voie trouvée
     */
    protected String id;
    protected String ligne4;
    protected String ligne4_origine;
    protected String nomVoie;
    protected String nomVoie_Origine;

    public GeocodageInverse_Voie() {
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNomVoie() {
        return nomVoie;
    }

    public void setNomVoie(String nomVoie) {
        this.nomVoie = nomVoie;
        computeLigne4();
    }

    public String getNomVoieOrigine() {
        return nomVoie_Origine;
    }

    public void setNomVoieOrigine(String nomVoie) {
        this.nomVoie_Origine = nomVoie;
        computeLigne4Origine();
    }

    /**
     * Méthode à usage interne à utiliser dans les setters des éléments participants
     * à la construction de la ligne 4.
     */
    protected void computeLigne4() {
        ligne4 = nomVoie;
    }

    /**
     * Méthode à usage interne à utiliser dans les setters des éléments participants
     * à la construction de la ligne 4.
     */
    protected void computeLigne4Origine() {
        ligne4_origine = nomVoie_Origine;
    }

    @Override
    public String getLigne4() {
        return ligne4;
    }

    @Override
    public String getLigne4Origine() {
        return ligne4_origine;
    }

    public GeocodageInverse_Voie(Point pt) {
        super(pt);
        this.precision = GestionInverse.GestionInverse_VOIE;
    }
}
