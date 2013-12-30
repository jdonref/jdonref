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
 * Représente le résultat d'une recherche de clé.<br>
 * Utilisé pour référencer une clé dans une chaine suite à une recherche.<br>
 * Cette classe permet aussi de conserver les modifications effectuées sur cette clé.<br>
 * Les classes RefDate, RefNumero, et RefTypeVoie sont des extensions de cette classe.
 * Selon les usages, obtientCle ou obtientCleAbbreviation doit être utilisé.
 * @author Julien
 */
public class RefCle
{
    protected Mot cleMot = null;
    protected Abbreviation cleAbbreviation = null;
    
    protected int index = 0;
    
    protected String chaineOriginale = "";
    
    protected String mot = null;
    
    protected CategorieMot categorieMot = CategorieMot.Vide;
    
    protected String complementAvant = null;
    protected String complementApres = null;
    
    /**
     * Obtient si oui ou non le mot ou l'abbréviation référencé est officiel.
     * @return
     */
    public boolean estofficiel()
    {
        if (cleMot!=null) return cleMot.estOfficiel();
        if (cleAbbreviation!=null) return cleAbbreviation.estOfficielle();
        return false;
    }
    
    /**
     * Obtient si oui ou non le mot ou l'abbréviation référencé est de la catégorie indiquée.
     * @param categorie
     * @return
     */
    public boolean estDeLaCategorie(CategorieMot categorie)
    {
        if (cleMot!=null) return cleMot.estDeLaCategorie(categorie);
        if (cleAbbreviation!=null) return cleAbbreviation.estAbbreviationDeCategorie(categorie);
        return false;
    }
    
    /**
     * Construit un résultat partiel.
     */
    public RefCle(String mot)
    {
        this.mot = mot;
    }

    /**
     * Construit un résultat partiel, avec index.
     */
    public RefCle(String mot,int index)
    {
        this.mot = mot;
        this.index = index;
    }

    /**
     * Construit un résultat partiel, avec index et catégorie.
     */
    public RefCle(String mot,int index,CategorieMot categorie)
    {
        this.mot = mot;
        this.index = index;
        this.categorieMot = categorie;
    }
    
    /**
     * Construit un résultat complet.<br>
     * La méthode obtientCle permet alors d'obtenir la clé.
     * @param mot le mot trouvé
     * @param cle la structure qui représente le mot trouvé
     * @param index l'index du mot trouvé dans la chaine originale
     * @param chaineOriginale la chaine dans laquelle le mot a été trouvé.
     * @param categorie la categorie du mot trouvé
     */
    public RefCle(String mot,Mot cle,int index,String chaineOriginale,CategorieMot categorie)
    {
        this.mot = mot;
        this.cleMot = cle;
        this.index = index;
        this.chaineOriginale = chaineOriginale;
        this.categorieMot = categorie;
    }
    
    /**
     * Construit un résultat complet.<br>
     * La méthode obtientCleAbbreviation permet alors d'obtenir la clé.
     * @param mot le mot trouvé
     * @param cle la structure qui représente le mot trouvé
     * @param index l'index du mot trouvé dans la chaine originale
     * @param chaineOriginale la chaine dans laquelle le mot a été trouvé.
     * @param categorie la categorie du mot trouvé
     */
    public RefCle(String mot,Abbreviation cle,int index,String chaineOriginale,CategorieMot categorie)
    {
        this.mot = mot;
        this.cleAbbreviation = cle;
        this.index = index;
        this.chaineOriginale = chaineOriginale;
        this.categorieMot = categorie;
    }
    
    /**
     * Reduit l'index de la quantité spécifiée, suite à une abbréviation par exemple.<br>
     * Attention, suite à l'exécution de cette fonction, l'index obtenu par la méthode
     * obtientIndex ne correspond plus à l'index du mot dans la chaine obtenue par la méthode
     * obtientChaineOriginale.
     */
    public void decale(int nb)
    {
        index -= nb;
    }
    
    /**
     * Obtient le mot trouvé.
     */
    public String obtientMot()
    {
        return mot;
    }
    
    /**
     * Obtient la chaine dans laquelle a été trouvée le mot.
     */
    public String obtientChaineOriginale()
    {
        return chaineOriginale;
    }
    
    /**
     * Obtient la structure de mot trouvé.
     */
    public Mot obtientCle()
    {
        return cleMot;
    }
    
    /**
     * Obtient la structure de mot trouvé.
     */
    public Abbreviation obtientCleAbbreviation()
    {
        return cleAbbreviation;
    }
    
    /**
     * Obtient le nom de la clé mot ou clé abbréviation selon le choix effectué lors de la création
     * de la classe.
     * @return null si il ne s'agit ni d'un mot ni d'une abbréviation.
     */
    public String obtientNomDeCle()
    {
       if (cleMot!=null) return cleMot.obtientNom();
       if (cleAbbreviation!=null) return cleAbbreviation.obtientNom();
       return null;
    }
    
    /**
     * Obtient la ligne du mot ou de l'abbréviation référencée pour la catégorie spécifiée.<br>
     * Si plusieurs lignes ou aucune sont disponibles, -1 est retourné.
     * @return
     */
    public int obtientLigneDeCle(CategorieMot categorie)
    {
        if (cleMot!=null)
        {
            if (cleMot.obtientCompteLigne()==1)
                return cleMot.obtientLigne(0);
        }
        else if (cleAbbreviation!=null)
        {
            Mot m = cleAbbreviation.obtientMotPrefere(categorie);

            // Correction WA 31-05-2011
            if( (m != null) && (m.obtientCompteLigne()==1) )
                return m.obtientLigne(0);
        }
        return -1;
    }
    
    /**
     * Obtient l'index dans la chaine originale du mot trouvé.<br>
     * Suite à l'appel de la méthode decale, il ne s'agit plus de l'index dans la chaine
     * originale du mot trouvé, mais l'index dans la chaine transformée.
     */
    public int obtientIndex()
    {
        return index;
    }
    
    /**
     * Obtient la catégorie du mot trouvé.
     */
    public CategorieMot obtientCategorieMot()
    {
        return categorieMot;
    }
    
    /**
     * Redéfinit la catégorie du mot trouvé.
     * @param categorie
     */
    public void definitCategorieMot(CategorieMot categorie)
    {
        this.categorieMot = categorie;
    }
    
    /**
     * Obtient le complement avant.
     * @return
     */
    public String obtientComplementAvant()
    {
        return complementAvant;
    }
    
    /**
     * Définit le complément de la clé.
     * @param complement
     */
    public void definitComplementAvant(String complementAvant)
    {
        this.complementAvant = complementAvant;
    }
    
    /**
     * Obtient le complement après.
     * @return
     */
    public String obtientComplementApres()
    {
        return complementApres;
    }
    
    /**
     * Définit le complément de la clé.
     * @param complement
     */
    public void definitComplementApres(String complementApres)
    {
        this.complementApres = complementApres;
    }
    
    /**
     * Annule le contenu de cette référence, en conservant l'index.<br>
     * La chaine trouvée est alors vide, la clé trouvée est null, la catégorie Vide.
     */
    public void reset()
    {
        cleMot = null;
        cleAbbreviation = null;
        categorieMot = CategorieMot.Vide;
        mot = "";
    }
    
    /**
     * Surcharge pour l'affichage en mode debug.
     * @return
     */
    @Override
    public String toString()
    {
        // WA 09/2011 affichage aussi de la categorie
        return ((mot == null) ? "null" : mot) + "(" + ((categorieMot == null)?"":categorieMot.toString()) + ")";
//        return obtientMot();
    }
}
