package jdonref_lucene_poc.query;
import org.apache.lucene.search.BooleanQuery;

/**
 * Performs a boolean XOR Query.
 * Means A XOR B = +(A B) -(+A +B)
 * 
 * @author Julien
 */
public class XORQuery extends BooleanQuery
{
    
}
