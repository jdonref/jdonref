/*
 * GestionReferentielException.java
 *
 * Created on 17 mars 2008, 16:23
 * 
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

package ppol.jdonref.referentiel;

/**
 *
 * @author jmoquet
 */
public class GestionReferentielException extends Exception
{
    public final static int TABLENEXISTEPAS = 0;
    public final static int COLONNESERRONEES = 1;
    public final static int COLONNEERRONEE = 2;
    public final static int MULTIPLESSHEMAS = 3;
    public final static int PASIDTRONCON = 4;
    public final static int MULTIPLESTABLES = 5;
    public final static int IDVOIEINCOHERENTS = 6;
    public final static int COLONNEMANQUANTE = 7;
    public final static int CHEMINERRONNE = 8;
    public final static int PARAMETREERRONNE = 9;
    public final static int ERREURNONREPERTORIEE = 10;
    public final static int TABLETROPGRANDE = 11;
    public final static int INDEXEXISTE = 12;
    public final static int ERREURTABLE = 13;
    public final static int PROBLEMEDONNEES = 14;
    
    private int type;
    
    private int numeroerreur = 7; // erreur inconnue par défaut.
    
    /**
     * Obtient le numéro d'erreur.
     */
    public int obtientNumeroErreur()
    {
        return numeroerreur;
    }
    
    /**
     * Obtient le type d'exception généré.
     */
    public int obtientType()
    {
        return type;
    }

    /** Creates a new instance of GestionReferentielException */
    public GestionReferentielException(String message,int type,int numeroerreur)
    {
        super(message);
        this.type = type;
        this.numeroerreur = numeroerreur;
    }
}
