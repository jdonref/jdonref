package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.util.List;
import org.apache.lucene.search.Query;

/**
 *
 * @author moquetju
 */

public interface ICountable
{
    /**
     * @return Nombre de termes pris en compte.
     */
    int count();
    
    /**
     * @return Le masque des tokens qui matchent
     */
    int tokenMatch();
    
    /**
     * @return Le masque des tokens qui matchent plusieurs fois
     */
    int multiTokenMatch();
    
    /**
     * @return Le masque des types de token qui matchent
     */
    int getTypeMask();
    
    /*
     * La fréquence minimale des termes qui matchent
     */
    int getMinFreq();
    
    /**
     * Les tokens pris en charge par la requête.
     * @return 
     */
    int getTokenMask();
}
