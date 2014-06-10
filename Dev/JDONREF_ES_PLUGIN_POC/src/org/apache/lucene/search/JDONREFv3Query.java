package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    public Explanation explain(AtomicReaderContext context, int doc)
      throws IOException {
        
      ComplexExplanation dotExpl = new ComplexExplanation();
      dotExpl.setDescription("product of:");
      
      Explanation boolExpl = super.explain(context,doc);
      dotExpl.addDetail(boolExpl);
      
      JDONREFv3Scorer scorer = (JDONREFv3Scorer) scorer(context, false, true, context.reader().getLiveDocs());
      
      float malus = scorer.malus(doc);
      if (malus!=1.0f)
      {
        Explanation malusExpl = new Explanation(malus,"adress malus (order)");
        dotExpl.addDetail(malusExpl);
      }
      
      boolean adressType = scorer.checkAdressType(doc);
      if (!adressType)
      {
        Explanation adressExpl = new Explanation(0f,"adress malus (adress number)");
        dotExpl.addDetail(adressExpl);
      }
      
      float value = malus*boolExpl.getValue();
      if (!adressType) value *= JDONREFv3Scorer.NUMBERMALUS;
      
      dotExpl.setValue(value);
      if (value>0)
        dotExpl.setMatch(true);
      
      return dotExpl;
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
    }
  }
    
    @Override
  public Weight createWeight(IndexSearcher searcher) throws IOException {
    return new JDONREFv3ESWeight(searcher, isCoordDisabled());
  }
}
