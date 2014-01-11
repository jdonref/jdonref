package jdonref_lucene_poc.tests;

import org.junit.Test;

/**
 *
 * @author Julien
 */
public class Voie
{
    public static void testGetArticle(String article, String voie, String typedevoie, String libelle)
    {
        jdonref_lucene_poc.entity.Voie v = new jdonref_lucene_poc.entity.Voie();
        String res = v.getArticle(voie,typedevoie,libelle);
        assert(article.compareTo(res)==0);
    }
    
    @Test
    public void getArticle()
    {
        testGetArticle("DU","RUE DU CHEVAL","RUE","CHEVAL");
        testGetArticle("DU","RUE  DU CHEVAL","RUE","CHEVAL");
        testGetArticle("DU","RUE  DU  CHEVAL","RUE","CHEVAL");
        testGetArticle("","RUE CHEVAL","RUE","CHEVAL");
        testGetArticle("DU","RUE  CHEVAL","RUE","CHEVAL");
        testGetArticle("DU","DU CHEVAL","","CHEVAL");
        testGetArticle("DU","DU  CHEVAL","","CHEVAL");
        testGetArticle("","CHEVAL","","CHEVAL");
        testGetArticle("","  CHEVAL","","CHEVAL");
        testGetArticle("","RUE","RUE","");
        testGetArticle("","RUE  ","RUE","");
        testGetArticle("","RUE  DU","RUE","");
        testGetArticle("","   ","","");
        testGetArticle("","RUE DU C ET A DU CHEVAL","","CHARLES ET ANTONY DU CHEVAL");
    }
}