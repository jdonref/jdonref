package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
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
    boolean DEBUG = false;
    
    public static String DEBUGREFERENCE = "130 RUE REMY DUHEM 59500 DOUAI FRANCE";
    
    protected Hashtable<String, Integer> termIndex;
    
    protected int numTokens;

        public int getNumTokens() {
            return numTokens;
        }

        public void setNumTokens(int numTokens) {
            this.numTokens = numTokens;
        }

    public Hashtable<String, Integer> getTermIndex()
    {
        return termIndex;
    }
        
    public void setTermIndex(Hashtable<String, Integer> termIndex) {
        this.termIndex = termIndex;
    }
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
    
    ArrayList<JDONREFv3TermScorer> getSubScorers(AtomicReaderContext context, int doc) throws IOException
    {
        // Search matching subscorers
      ArrayList<JDONREFv3TermScorer> subscorers = new ArrayList<JDONREFv3TermScorer>();
      for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();)
      {
        Weight w = wIter.next();
        
        JDONREFv3TermScorer subscorer = (JDONREFv3TermScorer) w.scorer(context, true, true, context.reader().getLiveDocs());
        if (subscorer == null) continue;
        
        if (w.explain(context,doc).isMatch())
            subscorers.add(subscorer);
      }
      
      return subscorers;
    }

    /**
     * Copy from BooleanQuery without coord explanation.
     * @param context
     * @param doc
     * @return
     */
    public Explanation booleanScorerExplainWithoutCoord(AtomicReaderContext context, int doc) throws IOException
    {
        final int minShouldMatch =
        JDONREFv3Query.this.getMinimumNumberShouldMatch();
        
        ComplexExplanation sumExpl = new ComplexExplanation();
      sumExpl.setDescription("sum of:");
      int coord = 0;
      float sum = 0.0f;
      boolean fail = false;
      int shouldMatchCount = 0;
      Iterator<BooleanClause> cIter = clauses().iterator();
      for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();) {
        Weight w = wIter.next();
        BooleanClause c = cIter.next();
        if (w.scorer(context, true, true, context.reader().getLiveDocs()) == null) {
          if (c.isRequired()) {
            fail = true;
            Explanation r = new Explanation(0.0f, "no match on required clause (" + c.getQuery().toString() + ")");
            sumExpl.addDetail(r);
          }
          continue;
        }
        Explanation e = w.explain(context, doc);
        if (e.isMatch()) {
          if (!c.isProhibited()) {
            sumExpl.addDetail(e);
            sum += e.getValue();
            coord++;
          } else {
            Explanation r =
              new Explanation(0.0f, "match on prohibited clause (" + c.getQuery().toString() + ")");
            r.addDetail(e);
            sumExpl.addDetail(r);
            fail = true;
          }
          if (c.getOccur() == BooleanClause.Occur.SHOULD) {
            shouldMatchCount++;
          }
        } else if (c.isRequired()) {
          Explanation r = new Explanation(0.0f, "no match on required clause (" + c.getQuery().toString() + ")");
          r.addDetail(e);
          sumExpl.addDetail(r);
          fail = true;
        }
      }
      if (fail) {
        sumExpl.setMatch(Boolean.FALSE);
        sumExpl.setValue(0.0f);
        sumExpl.setDescription
          ("Failure to meet condition(s) of required/prohibited clause(s)");
        return sumExpl;
      } else if (shouldMatchCount < minShouldMatch) {
        sumExpl.setMatch(Boolean.FALSE);
        sumExpl.setValue(0.0f);
        sumExpl.setDescription("Failure to match minimum number "+
                               "of optional clauses: " + minShouldMatch);
        return sumExpl;
      }
      
      sumExpl.setMatch(0 < coord ? Boolean.TRUE : Boolean.FALSE);
      sumExpl.setValue(sum);
      
      return sumExpl;
    }
    
    @Override
    public Explanation explain(AtomicReaderContext context, int doc)
      throws IOException {
      
      float value = 0.0f;
      
      boolean debug = false;
      
      if (context.reader().document(doc).get("fullName").equals(DEBUGREFERENCE))
          debug = DEBUG;
      
      if (debug)
      System.out.println("Thread "+Thread.currentThread().getName()+" "+"Explain doc "+doc+" notation");
      
      ComplexExplanation dotExpl = new ComplexExplanation();
      dotExpl.setDescription("Thread "+Thread.currentThread().getName()+" doc "+doc+" product of:");
      
      Explanation boolExpl = booleanScorerExplainWithoutCoord(context,doc);
      value = boolExpl.getValue();
      dotExpl.addDetail(boolExpl);
      
      if (debug)
      System.out.println("Thread "+Thread.currentThread().getName()+" doc "+doc+" value "+value);
      
      JDONREFv3Scorer scorer = (JDONREFv3Scorer) scorer(context, false, true, context.reader().getLiveDocs());
      ArrayList<JDONREFv3TermScorer> subscorers = getSubScorers(context,doc);
      
      // Malus
      float malus = scorer.malus(subscorers,doc);
      if (malus!=1.0f)
      {
        Explanation malusExpl = new Explanation(malus,"adress malus (order,adresse number, codes)");
        dotExpl.addDetail(malusExpl);
      }
      value *= malus;
      
      if (debug)
      System.out.println("Thread "+Thread.currentThread().getName()+" doc "+doc+" and malus "+malus);
      
      // total
      try
      {
        float total = scorer.totalScore(subscorers,doc);
        Explanation totalExpl = new Explanation(1/total,"maximum Score ("+total+" inverted)");
        dotExpl.addDetail(totalExpl);
        value /= total;
        
        if (debug)
        System.out.println("Thread "+Thread.currentThread().getName()+" doc "+doc+" And max score 1/"+total+"="+(1/total));
      }
      catch(Exception e)
      {
          throw new IOException(e);
      }
      
      // coord
      try
      {
        int maxcoord = scorer.maxCoord();
        int coord = scorer.coord(subscorers);
        if (coord!=maxcoord)
        {
            Explanation coordExpl = new Explanation((float)Math.pow((float)coord/(float)maxcoord,2),"coord("+coord+"/"+maxcoord+")^2");
            dotExpl.addDetail(coordExpl);
            value *= Math.pow((float)coord/(float)maxcoord,2);
            
            if (debug)
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+doc+" And coord ("+coord+"/"+maxcoord+")^2="+coordExpl.getValue());
        }
        
      }
      catch(Exception e)
      {
          throw new IOException(e);
      }
      
      // 200
      Explanation twohundredexpl = new Explanation(200,"rapporté à 200");
      dotExpl.addDetail(twohundredexpl);
      value *= 200;
      
      // Set value
      dotExpl.setValue(value);
      dotExpl.setMatch(value>0);
      
      if (debug)
        System.out.println("Thread "+Thread.currentThread().getName()+" doc "+doc+" ce qui donne "+value);
      
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
        return new JDONREFv3Scorer(this, protectedDisableCoord, minNrShouldMatch, optional, prohibited, maxCoord, context, termIndex);
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
