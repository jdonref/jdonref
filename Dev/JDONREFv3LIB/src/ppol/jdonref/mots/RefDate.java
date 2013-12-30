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
package ppol.jdonref.mots;

/**
 * Utilisé pour référencer une date dans une chaine suite à une recherche.
 * @author Julien
 */
public class RefDate extends RefCle
{
    boolean jourDuMoisEstNombre = false;
    boolean nomDuMoisEstNombre = false;

    String annee = null;
    String nomDuMois = null;
    String jourDuMois = null;
    String nomDuJour = null;
    
    String jourDuMoisNormal = null;
    
    int indexAnnee = -1;
    int indexNomDuMois = -1;
    int indexJourDuMois = -1;
    int indexNomDuJour = -1;    

    /**
     * Permet de savoir si le nom du mois trouvé est un nombre
     * @return
     */
    public boolean isNomDuMoisEstNombre() {
        return nomDuMoisEstNombre;
    }
    
    /**
     * Permet de savoir si le jour du mois trouvé est un nombre.
     */
    public boolean jourDuMoisEstNombre()
    {
        return jourDuMoisEstNombre;
    }

    /**
     * Obtient l'année de la date
     * @return null si l'année n'était pas spécifiée.
     */
    public String obtientAnnee()
    {
        return annee;
    }

    /**
     * Obtient le jour du mois de la date
     * @return
     */
    public String obtientJourDuMois() {
        return jourDuMois;
    }

    /**
     * Obtient le nom du mois de la date
     * @return
     */
    public String obtientNomDuMois() {
        return nomDuMois;
    }

    /**
     * Obtient le nom du jour de la date.
     * @return null si le nom n'était pas spécifié.
     */
    public String obtientNomDuJour() {
        return nomDuJour;
    }

    /**
     * Obtient le jour du mois normalisé (en chiffres et non en lettres).
     * @return
     */
    public String obtientJourDuMoisNormal() {
        return jourDuMoisNormal;
    }

    public int obtientIndexAnnee() {
        return indexAnnee;
    }

    public int obtientIndexJourDuMois() {
        return indexJourDuMois;
    }

    public int obtientIndexNomDuJour() {
        return indexNomDuJour;
    }

    public int obtientIndexNomDuMois() {
        return indexNomDuMois;
    }
    
    /**
     * Construit une référence de date vers
     */
    public RefDate(String chaine,int index)
    {
        super(chaine,index);
    }
    
    /**
     * Construit une référence vers une date.
     * @param chaine
     * @param index
     * @param chaineOriginale
     * @param nomDuJour peut être une chaine vide
     * @param indexNomDuJour
     * @param jourDuMois
     * @param indexJourDuMois
     * @param nomDuMois
     * @param indexNomDuMois
     * @param annee peut être une chaine vide
     * @param indexAnnee
     */
    public RefDate(String chaine, int index, String chaineOriginale,String nomDuJour,int indexNomDuJour,String jourDuMois,int indexJourDuMois,String jourDuMoisNormal,boolean jourDuMoisEstNombre,String nomDuMois,int indexNomDuMois,boolean nomDuMoisEstNombre,String annee,int indexAnnee)
    {
        super(chaine,(Mot)null,index,chaineOriginale,CategorieMot.Date);

        this.nomDuJour = nomDuJour;
        this.indexNomDuJour = indexNomDuJour;
        
        this.jourDuMois = jourDuMois;
        this.indexJourDuMois = indexJourDuMois;
        this.jourDuMoisNormal = jourDuMoisNormal;
        this.jourDuMoisEstNombre = jourDuMoisEstNombre;
        
        this.nomDuMois = nomDuMois;
        this.indexNomDuMois = indexNomDuMois;
        this.nomDuMoisEstNombre = nomDuMoisEstNombre;
        
        this.annee = annee;
        this.indexAnnee = indexAnnee;
    }

    @Override
    public void decale(int nb)
    {
        super.decale(nb);
        indexNomDuJour-=nb;
        indexJourDuMois-=nb;
        indexNomDuMois-=nb;
        indexAnnee-=nb;
    }
}