/*
 * Version 2.4.0 – 2012
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.jdom.Element;

/**
 *
 * @author arochewi
 */
public abstract class AbstractItemsWithProbability {

    public List<AbstractItemWithProbability> items = new ArrayList<AbstractItemWithProbability>();
    public Random r = new Random(Calendar.getInstance().getTimeInMillis());

    /**
     * Lit à partir d'un élément DOM la liste des items et leurs probabilités d'occurence.<br>
     */
    public void load(Element e) throws Exception {
        List l = e.getChildren(getItemXmlDesignation());
        for (int i = 0; i < l.size(); i++) {
            AbstractItemWithProbability item = getNewEmptyItem();
            item.load((Element) l.get(i));
            items.add(item);
        }
    }

    /**
     * Réparti dans la population de taille donnée les items selon les probabilités établies.
     * @return
     */
    public int[] obtientRepartition(int count) {
        int[] res = new int[items.size()];

        int somme = count;
        for (int i = 0; i < items.size(); i++) {
            int val = (items.get(i).probabilite * count) / 100;
            res[i] = val;
            somme -= val;
        }

        // les points non attribués restants sont répartis aléatoirement
        int q = somme / count;
        int reste = somme % count;
        while (somme != reste) {
            int i = r.nextInt(items.size());
            res[i] += q;
            somme -= q;
        }
        if (reste != 0) {
            int i = r.nextInt(items.size());
            res[i] += reste;
        }

        return res;
    }

    /**
     * Obtient un item selon les probabilités définies.
     * @return null peut être retourné si la somme n'est pas 100.
     */
    public AbstractItemWithProbability obtientItem() {
        int index = r.nextInt(100);
        int sum = 0;
        for (int i = 0; i < items.size(); i++) {
            int proba = items.get(i).probabilite;
            if (index <= sum + proba) {
                return items.get(i);
            }
            sum += proba;
        }
        return null;
    }

    protected abstract String getItemXmlDesignation();

    protected abstract AbstractItemWithProbability getNewEmptyItem();
}
