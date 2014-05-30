package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryBuilder;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.util.Bits;


/**
 *
 * @author Julien
 */
public class JDONREFv3Query extends BooleanQuery
{
    
/**
   * Expert: the Weight for BooleanQuery, used to
   * normalize, score and explain these queries.
   *
   * @lucene.experimental
   */
  protected class JDONREFv3ESWeight extends BooleanQuery.BooleanWeight
  {
    protected IndexSearcher searcher; // nécessaire pour affiner la notation
    protected boolean protectedDisableCoord;
    
    public IndexSearcher getSearcher()
    {
        return searcher;
    }
    
    public JDONREFv3ESWeight(IndexSearcher searcher, boolean disableCoord)
    throws IOException
    {
      super(searcher,disableCoord);
      
      this.protectedDisableCoord = disableCoord;
      this.searcher = searcher;
    }

    @Override
    public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder,
        boolean topScorer, Bits acceptDocs)
        throws IOException {
        
      List<Scorer> required = new ArrayList<Scorer>();
      List<Scorer> prohibited = new ArrayList<Scorer>();
      List<Scorer> optional = new ArrayList<Scorer>();
      Iterator<BooleanClause> cIter = clauses().iterator();
      for (Weight w  : weights) {
        BooleanClause c =  cIter.next();
        Scorer subScorer = w.scorer(context, true, false, acceptDocs);
        if (subScorer == null) {
          if (c.isRequired()) {
            return null;
          }
        } else if (c.isRequired()) {
          required.add(subScorer);
        } else if (c.isProhibited()) {
          prohibited.add(subScorer);
        } else {
          optional.add(subScorer);
        }
      }

      if (!scoreDocsInOrder && topScorer && required.size() == 0 && minNrShouldMatch <= 1) {
        return new JDONREFv3Scorer(this, protectedDisableCoord, minNrShouldMatch, optional, prohibited, maxCoord);
      }
      else
          throw new IOException("MultiNrShouldMatch nor required clause are not supported by JDONREFv3Scorer.");
      
      /* Original BooleanQuery Code
      if (required.size() == 0 && optional.size() == 0) {
        // no required and optional clauses.
        return null;
      } else if (optional.size() < minNrShouldMatch) {
        // either >1 req scorer, or there are 0 req scorers and at least 1
        // optional scorer. Therefore if there are not enough optional scorers
        // no documents will be matched by the query
        return null;
      }
      
      // simple conjunction
      if (optional.size() == 0 && prohibited.size() == 0) {
        float coord = protectedDisableCoord ? 1.0f : coord(required.size(), maxCoord);
        return new ConjunctionScorer(this, required.toArray(new Scorer[required.size()]), coord);
      }
      
      // simple disjunction
      if (required.size() == 0 && prohibited.size() == 0 && minNrShouldMatch <= 1 && optional.size() > 1) {
        float coord[] = new float[optional.size()+1];
        for (int i = 0; i < coord.length; i++) {
          coord[i] = protectedDisableCoord ? 1.0f : coord(i, maxCoord);
        }
        return new DisjunctionSumScorer(this, optional.toArray(new Scorer[optional.size()]), coord);
      }
      
      // Return a BooleanScorer2
      return new BooleanScorer2(this, protectedDisableCoord, minNrShouldMatch, required, prohibited, optional, maxCoord);
       **/
    }
  }
    
    @Override
  public Weight createWeight(IndexSearcher searcher) throws IOException {
    return new JDONREFv3ESWeight(searcher, isCoordDisabled());
  }
}
