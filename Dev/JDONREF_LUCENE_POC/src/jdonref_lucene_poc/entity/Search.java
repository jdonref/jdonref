package jdonref_lucene_poc.entity;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

/**
 *
 * @author Julien
 */
public class Search {
    public TopScoreDocCollector collector;
    public IndexSearcher searcher;
    public Query q;
    public ScoreDoc[] res;
}
