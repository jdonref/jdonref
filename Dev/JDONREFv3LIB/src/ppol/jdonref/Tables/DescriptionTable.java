/*
 * DescriptionTable.java
 *
 * Created on 12 mars 2008, 09:01
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

import java.util.ArrayList;

/**
 * Décrit sommairement une table.<br>
 * Contient les colonnes et les index.<br>
 * Une mécanisme permet aussi de diff�rencier les colonnes présentes à la création de la description
 * des colonnes ajoutées ensuite.<br>
 * Permet de faire une comparaison sommaire de deux tables.<br>
 * @author jmoquet
 */
public class DescriptionTable
{
    private ArrayList<Colonne> colonnes = null;
    private boolean estValide = false;
    
    private ArrayList<Index> indexes = null;
    
    private int nombreDeColonnesAuDepart;
    
    /**
     * Crée une nouvelle instance de DescriptionTable, vide.
     */
    public DescriptionTable()
    {
    }

    /**
     * Obtient un clone de la description.<br>
     * Les colonnes aussi sont clonées.
     */
    @Override
    public DescriptionTable clone()
    {
        ArrayList<Colonne> cols = new ArrayList<Colonne>();
        for(int i=0;i<colonnes.size();i++)
        {
            cols.add(colonnes.get(i).clone());
        }
        return new DescriptionTable(cols);
    }
    
    /**
     * Obtient le nombre de colonnes de cette description.
     * @return
     */
    public int obtientQuantitéColonnes()
    {
        return colonnes.size();
    }
    
    /**
     * Permet de vérifier si la description est valide.<br>
     * Elle est considérée comme valide si:
     * <ul><li>Les colonnes ne sont pas en double</li>
     *     <li>Les index références des colonnes existantes.</li></ul>
     */
    private void evalueValidite()
    {
        for(int i=0;i<colonnes.size();i++)
        {
            Colonne ci = colonnes.get(i);
            for(int j=i+1;j<colonnes.size();j++)
            {
                Colonne cj = colonnes.get(j);
                
                if (ci.Compare(cj)!=Colonne.DIFFERENT)
                {
                    estValide = false;
                    return;
                }
            }
        }
        
        for(int i=0;i<indexes.size();i++)
        {
            if (!contient(indexes.get(i).getNom()))
            {
                estValide = false;
                return;
            }
        }
        estValide = true;
    }

    /** Creates a new instance of DescriptionTable.
     *  Si colonnes est null, aucune colonne ne fait partie de la description.
     */
    public DescriptionTable(ArrayList<Colonne> colonnes,ArrayList<Index> indexes)
    {
        if (colonnes==null)
            this.colonnes = new ArrayList<Colonne>();
        else
            this.colonnes = colonnes;
        
        if (indexes==null)
            this.indexes = new ArrayList<Index>();
        else
            this.indexes = indexes;
        
        nombreDeColonnesAuDepart = this.colonnes.size();
        
        evalueValidite();
    }
    
    /** Creates a new instance of DescriptionTable.
     *  Si colonnes est null, aucune colonne ne fait partie de la description.
     */
    public DescriptionTable(ArrayList<Colonne> colonnes)
    {
        if (colonnes==null)
            this.colonnes = new ArrayList<Colonne>();
        else
            this.colonnes = colonnes;
        
        this.indexes = new ArrayList<Index>();
        
        nombreDeColonnesAuDepart = this.colonnes.size();
        
        evalueValidite();
    }
    
    /**
     * Ajoute une colonne à la description.<br>
     * La validité de la description reste inchangée.<br>
     * Les colonnes ajoutée par cette méthode sont définies comme étant supplémentaires.<br>
     * @return false si une colonne du même nom existait déjà.<br>
     */
    public boolean ajoute(Colonne c)
    {
        if (contient(c)==Colonne.DIFFERENT)
        {
            c.definitSiEstSupplémentaire(true);
            colonnes.add(c);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Cherche la colonne spécifiée dans la description et retourne son
     * index si elle est trouvée.
     * @param c
     * @return -1 si la colonne n'est pas trouvée.
     */
    public int indexOf(Colonne c)
    {
        for(int i=0;i<colonnes.size();i++)
        {
            int comp = colonnes.get(i).Compare(c);
            if (comp==Colonne.EGALITE)
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Retourne vrai si la description de la table est valide.<br>
     * Une description est valide quand chaque nom de colonne est unique.<br>
     * @return
     */
    public boolean estValide()
    {
        return estValide;
    }
    
    /**
     * Retourne si la description contient la colonne c.
     * Colonne.EGALITE si une colonne correspond exactement,
     * Colonne.TYPEDIFFERENT si une colonne diff�re d'un type ou d'une taille,
     * Colonne.DIFFERENT si aucune colonne ne correspond.
     */
    public int contient(Colonne c)
    {
        for(int i=0;i<colonnes.size();i++)
        {
            int comp = colonnes.get(i).Compare(c);
            if (comp!=Colonne.DIFFERENT)
            {
                return comp;
            }
        }
        return Colonne.DIFFERENT;
    }
    
    /**
     * Retourne si la description contient une colonne du nom sp�cifi�.
     * @param name
     * @return
     */
    public boolean contient(String name)
    {
        for(int i=0;i<colonnes.size();i++)
        {
            if (colonnes.get(i).getNom().compareTo(name)==0)
                return true;
        }
        return false;
    }
    
    /**
     * Obtient le nombre de colonnes de la table.<br>
     * 0 si les colonnes n'ont pas été initialisées.
     */
    public int getCount()
    {
        if (colonnes==null) return 0;
        return colonnes.size();
    }
    
    /**
     * Obtiens la colonne d'index spécifié.<br>
     * @return null si les colonnes n'ont pas été initialisées.
     * @throws ArrayIndexOutOfBoundException
     */
    public Colonne getColonne(int index)
    {
        if (colonnes==null) return null;
        return colonnes.get(index);
    }
    
    /**
     * Compare la description de deux tables.<br>
     * La différence contiendra les colonnes de la table en paramétre qui sont en trop,
     * et les colonnes qui sont erronnées entre les deux tables.<br>
     * Les colonnes en trop sont ignorées.<br>
     * La différence contient des clones des colonnes des descriptions.
     */
    public Difference compare(DescriptionTable dt)
    {
       // Permet d'identifier les colonnes de dt qui correspondent
       // à une colonne de l'instance.
       boolean[] correspondance = new boolean[dt.getCount()];

       ArrayList<Colonne> colonnesManquantes = new ArrayList();
       ArrayList<Colonne> colonnesErronees = new ArrayList();
               
       // Commence par chercher les correspondances.
       for(int i=0;i<colonnes.size();i++)
       {
           boolean done = false;
           Colonne c = colonnes.get(i);
           
           for(int j=0;j<(dt.getCount())&&!done;j++) // La recherche s'arr�te d�s qu'une correspondance est trouv�e.
           {
               if (!correspondance[j]) // Si la colonnes de dt correspond d�j� � une colonne de l'instance, ce n'est pas la peine.
               {
                   int res = c.Compare(dt.getColonne(j));
                   
                   if (res==Colonne.TYPEDIFFERENT)
                   {
                       colonnesErronees.add(c.clone());
                       correspondance[j] = true;
                       done = true;
                   }
                   else if (res==Colonne.EGALITE)
                   {
                       correspondance[j] = true;
                       done = true;
                   }
               }
           }
       }
       
       // Puis ajoute les colonnes qui n'ont pas eu de correspondances dans dt.
       for(int i=0;i<dt.getCount();i++)
       {
           if (!correspondance[i]) // Si la correspondance n'a pas pu �tre �tablie, c'est que la colonne manque.
               colonnesManquantes.add(dt.getColonne(i).clone());
       }
       
       return new Difference(colonnesManquantes,colonnesErronees);
    }
    
    /**
     * Retourne si la table est une table historis�e, 
     * c'est � dire qu'elle contient les colonnes t0 et t1 de type TIMESTAMP WITHOUT TIME ZONE.
     */   
    public boolean estHistorisee()
    {
        Colonne t0=null;
        Colonne t1=null;
        try
        {
            t1 = new Colonne("t1", "TIMESTAMP WITHOUT TIME ZONE", 0);
            t0 = new Colonne("t0","TIMESTAMP WITHOUT TIME ZONE",0);
        }
        catch (ColonneException ex)
        {
            ex.printStackTrace();
        }
        
        boolean t0trouve = false;
        boolean t1trouve = false;
        
        for(int i=0;(i<colonnes.size())&&!(t0trouve&&t1trouve);i++)
        {
            Colonne c = colonnes.get(i);
            
            if (!t0trouve && t0.Compare(c)==Colonne.EGALITE)
            {
                t0trouve = true;
            }
            if (!t1trouve && t1.Compare(c)==Colonne.EGALITE)
            {
                t1trouve = true;
            }
        }
        
        return t1trouve && t0trouve;
    }
    
    /**
     * Permet de tester la pr�sente classe.
     */
    public static void main(String[] args)
    {
        System.out.println("Test de la classe DescriptionTable");
        System.out.println("La première table contient : ");
        System.out.println(" nom VARCHAR(255)");
        System.out.println(" date TIMESTAMP");
        System.out.println("La seconde table contient :");
        System.out.println(" nom VARCHAR(80)");
        System.out.println(" date TIMESTAMP");
        System.out.println(" t0 TIMESTAMP");
        System.out.println(" t1 TIMESTAMP");
        Colonne c1=null,c2=null,c3=null,c4=null,c5=null;

        try
        {
            c1 = new Colonne("nom", "VARCHAR", 255);
            c2 = new Colonne("date","TIMESTAMP",0);
            c3 = new Colonne("t0","TIMESTAMP",0);
            c4 = new Colonne("t1","TIMESTAMP",0);
            c5 = new Colonne("nom","VARCHAR",80);
        }
        catch (ColonneException ex)
        {
            ex.printStackTrace();
        }
        
        
        ArrayList<Colonne> ens1 = new ArrayList<Colonne>();
        ens1.add(c1);
        ens1.add(c2);
        
        ArrayList<Colonne> ens2 = new ArrayList<Colonne>();
        ens2.add(c5);
        ens2.add(c2);
        ens2.add(c3);
        ens2.add(c4);
        
        DescriptionTable dt1 = new DescriptionTable(ens1);
        DescriptionTable dt2 = new DescriptionTable(ens2);
        
        System.out.println("\r\nCompare dt1 à dt2:");
        Difference res = dt1.compare(dt2);
        if (res.obtientEtat()==Difference.EGALITE)
        {
            System.out.println("Les tables sont �gales");
        }
        else
        {
            if ((res.obtientEtat()& Difference.MANQUECOLONNES)!=0)
            {
                System.out.println("Il manque des colonnes :");
                for(int i=0;i<res.obtientQuantitéColonnesManquantes();i++)
                {
                    Colonne c = res.obtientColonneManquante(i);
                    System.out.println(c.getNom()+" "+c.getType());
                }
            }
            if ((res.obtientEtat()& Difference.COLONNESERRONEES)!=0)
            {
                System.out.println("Des colonnes sont erronées :");
                for(int i=0;i<res.obtientQuantitéColonnesErronees();i++)
                {
                    Colonne c = res.obtientColonneErronee(i);
                    System.out.println(c.getNom()+" "+c.getType());
                }
            }
        }

        System.out.println("\r\nCompare dt2 à dt1:");
        res = dt2.compare(dt1);
        if (res.obtientEtat()==Difference.EGALITE)
        {
            System.out.println("Les tables sont égales");
        }
        else
        {
            if ((res.obtientEtat()& Difference.MANQUECOLONNES)!=0)
            {
                System.out.println("Il manque des colonnes :");
                for(int i=0;i<res.obtientQuantitéColonnesManquantes();i++)
                {
                    Colonne c = res.obtientColonneManquante(i);
                    System.out.println(c.getNom()+" "+c.getType());
                }
            }
            if ((res.obtientEtat()& Difference.COLONNESERRONEES)!=0)
            {
                System.out.println("Des colonnes sont erronées :");
                for(int i=0;i<res.obtientQuantitéColonnesErronees();i++)
                {
                    Colonne c = res.obtientColonneErronee(i);
                    System.out.println(c.getNom()+" "+c.getType());
                }
            }
        }
        
        System.out.println("\r\nVerifie si dt1 est historisee");
        if (dt1.estHistorisee())
            System.out.println("dt1 est historisee.");
        else
            System.out.println("dt1 n'est pas historisee.");
        
        System.out.println("\r\nVerifie si dt2 est historisee");
        if (dt2.estHistorisee())
            System.out.println("dt2 est historisee.");
        else
            System.out.println("dt2 n'est pas historisee.");
    }
}