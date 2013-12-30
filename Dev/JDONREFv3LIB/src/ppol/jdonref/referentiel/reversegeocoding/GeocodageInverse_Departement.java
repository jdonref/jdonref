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
import java.util.Date;

/**
 * Représente le résultat d'un géocodage inverse au niveau département
 * 
 * @author Julien Moquet
 */
public class GeocodageInverse_Departement extends GeocodageInverse {

    protected String code_dpt;
    protected String ligne6;
    protected String ligne6_origine;

    public GeocodageInverse_Departement() {
    }

    @Override
    public String getLigne6() {
        return ligne6;
    }

    @Override
    public String getLigne6Origine() {
        return ligne6_origine;
    }

    /**
     * Définit le code de département.
     * @param code_dpt
     */
    public void setCodeDepartement(String code_dpt) {
        this.code_dpt = code_dpt;
        computeLigne6();
        computeLigne6Origine();
    }

    public String getCodeDepartement() {
        return code_dpt;
    }

    public GeocodageInverse_Departement(Point pt) {
        super(pt);
        this.precision = GestionInverse.GestionInverse_DEPARTEMENT;
    }

    public String getLigne1() {
        return "";
    }

    public String getLigne2() {
        return "";
    }

    public String getLigne3() {
        return "";
    }

    public String getLigne4() {
        return "";
    }

    public String getLigne4Origine() {
        return "";
    }

    public String getLigne5() {
        return "";
    }

    public String getId() {

        return "";
    }

    public String getCodeInsee() {

        return "";

    }

    public String getLigne7() {
        return "";
    }

    public String getLigne7Origine() {
        return "";
    }

    public String getSovA3() {
        return "";
    }

    protected void computeLigne6() {
        ligne6 = code_dpt;
    }

    protected void computeLigne6Origine() {
        ligne6_origine = code_dpt;
    }
}
