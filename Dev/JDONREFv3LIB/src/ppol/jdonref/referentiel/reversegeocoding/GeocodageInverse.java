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
 * Représente un résultat de géocodage inverse.
 * 
 * @author Julien Moquet
 */
public abstract class GeocodageInverse {
    
    /**
     * Niveau de la precision :
     * <ul>
     * <li>GestionInverse_DEPARTEMENT</li>
     * <li>GestionInverse_COMMUNE</li>7
     * <li>GestionInverse_VOIE</li>
     * <li>GestionInverse_TRONCON</li>
     * <li>GestionInverse_INTERPOLATION_PLAQUE</li>
     * <li>GestionInverse_PLAQUE</li>
     * </ul>
     */
    public int precision;
    /**
     * Point qui a permit de réaliser le géocodage inverse
     */
    public Point point;
    /**
     * Distance entre la solution trouvée et le point.
     */
    public double distance;
    public Date t0;
    public Date t1;
    private String centroide;

    public GeocodageInverse() {
    }

    public String getCentroide() {
        return centroide;
    }

    public void setCentroide(String centroide) {
        this.centroide = centroide;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setT0(Date t0) {
        this.t0 = t0;
    }

    public void setT1(Date t1) {
        this.t1 = t1;
    }

    public abstract String getLigne1();

    public abstract String getLigne2();

    public abstract String getLigne3();

    public abstract String getLigne4();

    public abstract String getLigne4Origine();

    public abstract String getLigne5();

    public abstract String getLigne6();

    public abstract String getLigne6Origine();

    public abstract String getLigne7();

    public abstract String getLigne7Origine();
    
    public abstract String getId();
    
    public abstract String getCodeInsee();
    
    public abstract String getSovA3();
    
    /**
     * Constructeur abstrait
     * @param pt
     */
    public GeocodageInverse(Point pt) {
        this.point = pt;
    }
}
