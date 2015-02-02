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
 * Représente le résultat d'un géocodage inverse au niveau troncon
 * 
 * @author Moquet Julien
 */
public class GeocodageInverse_Troncon extends GeocodageInverse_Voie {

    protected int numero;
    protected char rep;
    protected String idTr;
    protected String pointIntersecton;

    public String getPointIntersecton() {
        return pointIntersecton;
    }

    public void setPointIntersecton(String pointIntersecton) {
        this.pointIntersecton = pointIntersecton;
    }

    public String getIdTr() {
        return idTr;
    }

    public void setIdTr(String idTr) {
        this.idTr = idTr;
    }

    public GeocodageInverse_Troncon() {
        this.precision = GestionInverse.GestionInverse_TRONCON;
    }

    @Override
    public GeocodageInverse_Troncon clone() {
        GeocodageInverse_Troncon git = new GeocodageInverse_Troncon();
        git.code_dpt = this.code_dpt;
        git.code_insee = this.code_insee;
        git.codepostal = this.codepostal;
        git.commune = this.commune;
        git.commune_origine = this.commune_origine;
        git.distance = this.distance;
        git.id = this.id;
        git.ligne4 = this.ligne4;
        git.ligne4_origine = this.ligne4_origine;
        git.ligne6 = this.ligne6;
        git.ligne6_origine = this.ligne6_origine;
        git.nomVoie = this.nomVoie;
        git.nomVoie_Origine = this.nomVoie_Origine;
        git.numero = this.numero;
        git.point = this.point;
        git.precision = this.precision;
        git.rep = this.rep;
        git.t0 = this.t0;
        git.t1 = this.t1;
        return git;
    }

    public void setRep(char rep) {
        this.rep = rep;
        computeLigne4();
        computeLigne4Origine();
    }

    public char getRep() {
        return rep;
    }

    public void setNumero(int numero) {
        this.numero = numero;
        computeLigne4();
        computeLigne4Origine();
    }

    public int getNumero() {
        return numero;
    }

    protected String computeNumeroRep() {
        if (numero != 0) {
            if (rep == (char) 0 || rep == ' ') {
                return Integer.toString(numero);
            } else {
                return Integer.toString(numero) + " " + rep;
            }
        }
        return "";
    }

    @Override
    protected void computeLigne4() {
        String numeroRep = computeNumeroRep();
        if (numeroRep.length() > 0) {
            this.ligne4 = numeroRep + " " + nomVoie;
        } else {
            this.ligne4 = nomVoie;
        }
    }

    @Override
    protected void computeLigne4Origine() {
        String numeroRep = computeNumeroRep();
        if (numeroRep.length() > 0) {
            this.ligne4_origine = numeroRep + " " + nomVoie_Origine;
        } else {
            this.ligne4_origine = nomVoie_Origine;
        }
    }

    public GeocodageInverse_Troncon(Point pt) {
        super(pt);
        this.precision = GestionInverse.GestionInverse_TRONCON;
    }
}
