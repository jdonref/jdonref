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

/**
 *
 * @author Julien
 */
public class NumeroProjection extends PointProjection
{
    int numero_min;
    int numero_max;
    int numero;
    
    /**
     * Permet de savoir si deux instances sont égales.
     * La comparaison porte sur :
     * <ul><li>numéro de voie</li></ul>
     * @param np
     * @return
     */
    public boolean equals(NumeroProjection np)
    {
        if (this.numero != np.numero) return false;
        return true;
    }
    
    public NumeroProjection(PointProjection pp)
    {
        this.distanceBeetweenPoints = pp.distanceBeetweenPoints;
        this.distanceFromStartOfGeometry = pp.distanceFromStartOfGeometry;
        this.totalLength = pp.totalLength;
        this.geometry = pp.geometry;
        this.origine = pp.origine;
        this.projection = pp.projection;
    }
    
    public int getNumeroMin()
    {
        return numero_min;
    }
    
    public int getNumeroMax()
    {
        return numero_max;
    }
    
    public void setNumeroMin(int numero)
    {
        this.numero_min = numero;
    }
    
    public void setNumeroMax(int numero)
    {
        this.numero_max = numero;
    }
    
    public void setNumero(int numero)
    {
        this.numero = numero;
    }
    
    public int getNumero()
    {
        return numero;
    }
}