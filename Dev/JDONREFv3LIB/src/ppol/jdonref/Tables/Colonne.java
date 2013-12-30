/*
 * Colonne.java
 *
 * Created on 12 mars 2008, 09:04
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

package ppol.jdonref.Tables;

/**
 *
 * @author jmoquet
 */
public class Colonne
{    
    /**
     * Les colonnes sont identiques.
     */
    public final static int EGALITE = 1;
    /**
     * Les colonnes ont le même nom mais un type différent (s'ils n'ont pas la même taille,
     * ils sont différents).
     */
    public final static int TYPEDIFFERENT = 2;
    /**
     * les colonnes n'ont pas le même nom.
     */
    public final static int DIFFERENT = 3;
    
    private boolean geometrie = false;
    private String nom;
    private String type;
    private int length;
    
    private boolean estDroiteOuGauche = false;
    private boolean estSupplémentaire = false;

    /**
     * Obtient si la colonne a été ajoutée après la création de la classe.
     * @return
     */
    public boolean estSupplémentaire()
    {
        return estSupplémentaire;
    }

    /**
     * D�finit si la colonne a été ajoutée après la création de la classe.
     * @param estSupplémentaire
     */
    public void definitSiEstSupplémentaire(boolean estSupplémentaire)
    {
        this.estSupplémentaire = estSupplémentaire;
    }   
    
    /**
     * Obtient si la colonne est relative au coté d'un troncon (droite ou gauche).
     * @return
     */
    public boolean estDroiteOuGauche()
    {
        return estDroiteOuGauche;
    }

    /**
     * D�finit si la colonne est relative au coté d'un troncon (droite ou gauche).
     * @param estDroiteOuGauche
     */
    public void definitSiEstDroiteOuGauche(boolean estDroiteOuGauche)
    {
        this.estDroiteOuGauche = estDroiteOuGauche;
    }
    
    /** Creates a new instance of Colonne.<br>
     *  Pour créer un type géométrie, il est nécessaire d'utiliser la méthode creeColonneGeometrie().<br>
     *  La longueur est toujours utilisée pour effectuer les comparaisons.
     */
    public Colonne(String nom,String type,int length) throws ColonneException
    {
        if (nom==null || type==null)
            throw(new ColonneException("Ni nom, ni type ne peuvent �tre null."));
        if (length<0)
            throw(new ColonneException("La taille d'un champ caract�re ne peut �tre n�gative."));
                
        this.nom = nom.toLowerCase();
        this.type = type.toLowerCase();
        this.length = length;
        this.geometrie = false;
    }
    
    private Colonne()
    {
    }
    
    /**
     * Obtient une colonne de type géometrie, de nom geometry.
     */
    public static Colonne creeColonneGeometrie()
    {
        Colonne c = new Colonne();
        c.geometrie = true;
        return c;
    }
    
    /**
     * Retourne vrai si la colonne est de type géométrie.
     */
    public boolean estGeometrie()
    {
        return geometrie;
    }
    
    /**
     * Retourne le nom de la colonne.<br>
     * Le nom d'une colonne géométrie est geometrie.
     */
    public String getNom()
    {
        if (geometrie)
            return "geometrie";
        return nom;
    }
    
    /**
     * Retourne le type de la colonne.<br>
     * Le type d'une colonne géométrie est USER-DEFINED.
     */
    public String getType()
    {
        if (geometrie)
            return "USER-DEFINED";
        return type;
    }
    
    public int getLength()
    {
        return length;
    }
    
    /**
     * Compare avec une autre colonne.
     * La casse des noms de colonnes et des types n'est pas conservée pour la comparaison.
     * Par exemple les colonnes T0 et t0 ont le même nom.
     * La longueur est toujours utilisée pour effectuer la comparaison.
     * retourne EGALITE, TYPEDIFFERENT, ou DIFFERENT.
     */
    public int Compare(Colonne c)
    {
        if (c.getNom().toLowerCase().compareTo(getNom().toLowerCase())==0)
        {
            if (c.getType().toLowerCase().compareTo(getType().toLowerCase())==0)
            {
                if (c.getLength()==length)
                    return EGALITE;
                else
                    return TYPEDIFFERENT;
            }
            else
                return TYPEDIFFERENT;
        }
        else
            return DIFFERENT;
    }
    
    /**
     * Retourne un clone de la colonne.
     */
    public Colonne clone()
    {
        Colonne res = null;
        try
        {
            if (!geometrie)
                res = new Colonne(getNom(),getType(),getLength());
            else
                res = creeColonneGeometrie();
            
            res.definitSiEstDroiteOuGauche(estDroiteOuGauche);
            res.definitSiEstSupplémentaire(estSupplémentaire);
        }
        catch(ColonneException ce)
        {
            // Déjà traité lors de la construction de l'instance.
        }
        return res;
    }
}
