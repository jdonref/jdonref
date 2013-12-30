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
package jdonrefv3charge;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Permet de gérer les probabilités de choix d'une adresse dans un département.
 * Les départements non spécifiés ont une probabilité d'occurence nulle.
 * Au moins un département doit être spécifié sous peine d'exception.
 * La somme des probabilités doit être 100, mais cette quantité n'est pas vérifiée.
 * @author jmoquet
 */
public class Departements extends AbstractItemsWithProbability
{
//    public ArrayList<Departement> departements = new ArrayList<Departement>();
//    public Random r = new Random(Calendar.getInstance().getTimeInMillis());

    /* Constructeur par défaut */
    public Departements()
    {
    }

    
//    /**
//     * Lit à partir d'un élément DOM la liste des départements et leurs probabilités d'occurence.<br>
//     */
//    public void load(Element e) throws Exception
//    {
//        List l = e.getChildren("departement");
//        for(int i=0;i<l.size();i++)
//        {
//            Departement d = new Departement();
//            d.load((Element)l.get(i));
//            departements.add(d);
//        }
//    }
    
//    /**
//     * Réparti dans la population de taille donnée les départements selon les probabilités établies.
//     * @return
//     */
//    public int[] obtientRepartition(int count)
//    {
//        int[] res = new int[departements.size()];
//
//        int somme = count;
//        for(int i=0;i<departements.size();i++)
//        {
//            int val = (departements.get(i).probabilite*count)/100;
//            res[i] = val;
//            somme -= val;
//        }
//
//        // les points non attribués restants sont répartis aléatoirement
//        int q = somme/count;
//        int reste = somme%count;
//        while(somme!=reste)
//        {
//            int i = r.nextInt(departements.size());
//            res[i] += q;
//            somme -= q;
//        }
//        if (reste!=0)
//        {
//            int i = r.nextInt(departements.size());
//            res[i] += reste;
//        }
//        
//        return res;
//    }
    
    /**
     * Obtient un numéro de département selon les probabilités définies.
     * @return null peut être retourné si la somme n'est pas 100.
     */
    public String obtientDepartement()
    {
//        int index = r.nextInt(100);
//        int sum = 0;
//        for(int i=0;i<departements.size();i++)
//        {
//            int proba = departements.get(i).probabilite;
//            if (index<=sum+proba) return departements.get(i).departement;
//            sum += proba;
//        }
//        return null;
        AbstractItemWithProbability item = obtientItem();
        return (item==null) ? null : ((Departement)item).departement;
    }
    
    public static void main(String[] args)
    {
        System.out.println("Test de Departements");
        Departements ds = new Departements();

        int size = 4;
        int nbtests = 10000;
        int tolerance = 3;
        System.out.println(size+" departement(s)");
        System.out.println(nbtests+" tests effectué(s)");
        System.out.println("tolerance de "+tolerance);

        Departement[] d = new Departement[size];
        int max = 100;
        Random r = new Random(Calendar.getInstance().getTimeInMillis());
        System.out.println("Génère des probabilités de département : ");
        for(int i=0;i<d.length;i++)
        {
            d[i] = new Departement();
            d[i].departement = Integer.toString(i);
            
            if (i<d.length-1 && max>0)
            {
                d[i].probabilite = r.nextInt(max);
                max -= d[i].probabilite;
            }
            else
                d[i].probabilite = max;
            
            System.out.println(Integer.toString(i)+ " : "+d[i].probabilite);
            
//            ds.departements.add(d[i]);
            ds.items.add(d[i]);
        }
        
        System.out.println("Teste les probabilités induites : ");
        int[] proba = new int[size];
        for(int i=0;i<nbtests;i++)
        {
            String dep  = ds.obtientDepartement();
            proba[Integer.parseInt(dep)]++;
        }
        int erreurs = 0;
        for(int i=0;i<size;i++)
        {
            proba[i] *=100;
            proba[i] /= nbtests;
            System.out.println(Integer.toString(i)+" : "+proba[i]);
            // un écart de 3 est toléré
//            if (Math.abs(proba[i]-ds.departements.get(i).probabilite)>tolerance)
            if (Math.abs(proba[i]-ds.items.get(i).probabilite)>tolerance)
            {
                System.out.println("Erreur");
                erreurs ++;
            }
        }
        if (erreurs>0)
        {
            System.out.println(erreurs+" ont été constatée(s).");
        }
        else
            System.out.println("Aucune erreur");
    }

    /**
     * Retourne le dpt d'index i
     * @param i
     * @return
     */
    public Departement getDepartement(int i)
    {
        return (Departement) items.get(i);
    }

    /**
     * Ajoute un departement aux items
     * @param dpt
     */
    public void addDepartement(Departement dpt)
    {
        items.add(dpt);
    }

    @Override
    protected String getItemXmlDesignation()
    {
        return "departement";
    }

    @Override
    protected AbstractItemWithProbability getNewEmptyItem()
    {
        return new Departement();
    }

}