package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;
import org.elasticsearch.index.query.QueryParseContext;


/**
 * Ordonne les résultats suivant un calcul de pourcentage en tant que note.
 * 
 * A chaque terme est assigné un poids.
 * 
 * avec
 * numero : 10 points
 * libelle : 20 points
 * code postal : 10 points
 * 
 * pour matcher le document
 * numero libelle libelle code postal
 * 
 * numero libelle => 10+20/10+20+20+10 = 30/60 = 50%
 * numero libelle code postal => 10+20+10/10+20+20+10 = 40/60 = 66%
 * 
 * 
 * NB: minimumShouldMatch tient aussi compte des clauses MUST
 * 
 * @author Julien
 */
public class PercentScoreBooleanQuery extends BooleanQuery
{
    protected QueryParseContext parseContext;
    
    protected boolean active = true;
    
    protected int maxCoord = 0;
    
    protected int maxFreq = 5000;
    protected int limit = 500;
    
    public void setMaxFreq(int maxFreq)
    {
        this.maxFreq = maxFreq;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setMaxCoord(int maxCoord)
    {
        this.maxCoord = maxCoord;
    }
    
    public int getMaxCoord()
    {
        return maxCoord;
    }
    
    public QueryParseContext getParseContext() {
        return parseContext;
    }

    public void setParseContext(QueryParseContext parseContext) {
        this.parseContext = parseContext;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    public boolean isActive()
    {
        return active;
    }
    
/**
   * Expert: the Weight for BooleanQuery, used to
   * normalize, score and explain these queries.
   *
   * @lucene.experimental
   */
  protected class PercentScoreWeight extends BooleanQuery.BooleanWeight
  {
    protected IndexSearcher searcher; // nécessaire pour affiner la notation
    
    public PercentScoreWeight(IndexSearcher searcher, int maxCoord)
    throws IOException
    {
      super(searcher,true);
      
      this.searcher = searcher;
      
      this.maxCoord = maxCoord;
    }

    @Override
    public Query getQuery()
    {
        return PercentScoreBooleanQuery.this;
    }
    
    @Override
    public float getValueForNormalization() throws IOException {
        return 1.0f;
    }
    
    @Override
    public float coord(int overlap, int maxOverlap)
    {
        return 1.0f;
    }
    
    @Override
    public void normalize(float norm, float topLevelBoost)
    {
    }
    
    @Override
    public Explanation explain(AtomicReaderContext context, int doc) throws IOException
    {
        final int minShouldMatch =
        PercentScoreBooleanQuery.this.getMinimumNumberShouldMatch();
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
        if (w.scorer(context, context.reader().getLiveDocs()) == null) {
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
      
      return sumExpl;                             // eliminate wrapper
    }
    
    @Override
    public Scorer scorer(AtomicReaderContext context, Bits acceptDocs)
        throws IOException {
      
      int minShouldMatch = minNrShouldMatch;
      
      List<Scorer> optional = new ArrayList<>();
      List<Scorer> required = new ArrayList<>();
      Iterator<BooleanClause> cIter = clauses().iterator();
      for (Weight w  : weights) {
        BooleanClause c =  cIter.next();
        Scorer subScorer = w.scorer(context, acceptDocs);
        
        if (subScorer == null) {
          if (c.isRequired()) {
            return null;
          }
        } else if (c.isRequired()) {
          required.add(subScorer);
        } else if (c.isProhibited()) {
            throw(new IOException("Prohibited clauses are not available."));
        } else {
          optional.add(subScorer);
        }
      }
      
      if (optional.size() == minShouldMatch && required.isEmpty()) // required.isEmpty is needed to have a global minShouldMatch
      {
        // any optional clauses are in fact required
        required.addAll(optional);
        optional.clear();
        minShouldMatch = 0;
      }
      
      if (required.isEmpty() && optional.isEmpty()) {
        // no required and optional clauses.
        return null;
      } else if (optional.size() < minShouldMatch && required.isEmpty()) {
        // either >1 req scorer, or there are 0 req scorers and at least 1
        // optional scorer. Therefore if there are not enough optional scorers
        // no documents will be matched by the query
        return null;
      }
      
      // pure conjunction
      if (optional.isEmpty()) {
        return new PercentScoreBooleanScorer(this,req(required),active, maxCoord, minShouldMatch); // minShouldMatch is needed because we don't what's in required
      }
      
      // deux types d'optionnels, suivant la limite de fréquence.
      List<Scorer> under = new ArrayList<Scorer>();
      List<Scorer> over = new ArrayList<Scorer>();
      getUnderOver(under, over, optional);
      
      if (over.isEmpty())
      {
          return getInnerScorer(required,optional, minShouldMatch);
      }
      else
      {
          Scorer innerScorer = getInnerScorer(required,optional,minShouldMatch);
          int limit2 = 3*limit/2;
          
          Scorer secondScorer = null;
          if (!required.isEmpty())
          {
              Scorer req = req(required);

              //Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" under="+under.size()+" over="+over.size());

              if (under.isEmpty())
              {
                  int nbtokens_over = countToken(over);
                  secondScorer = new PercentScoreBooleanScorer(this, new PercentScoreReqOptSumScorer(req, opt(over,nbtokens_over)), active, maxCoord, minShouldMatch);
              }
              else
              {
                  int nbtokens_under = countToken(under);
                  Scorer opt = new PercentScoreReqOptSumScorer(opt(under,nbtokens_under), opt(over,0)); // fix it
                  secondScorer = new PercentScoreBooleanScorer(this,new PercentScoreReqOptSumScorer(req, opt),active, maxCoord, minShouldMatch);

              }
          }
          else
          {
              if (under.isEmpty())
              {
                  int nbtokens_over = countToken(over);
                  secondScorer = new PercentScoreBooleanScorer(this, req( Arrays.asList(new Scorer[]{opt(over, nbtokens_over)})), active, maxCoord, minShouldMatch);
              }
              else
              {
                  int nbtokens_under = countToken(under);
                  Scorer opt = new PercentScoreReqOptSumScorer(opt(under,nbtokens_under), opt(over,0)); // fix it
                  secondScorer = new PercentScoreBooleanScorer(this, opt, active, maxCoord, minShouldMatch);
              }
          }
          
//          return getInnerScorer(required,optional, minShouldMatch);
          
          return new PercentScoreBooleanTwoTimeScorer(this, innerScorer, secondScorer,limit,limit2);
      }
    }
    
    public int countToken(List<Scorer> scorers)
    {
        int mask = 0;
        for(Scorer scorer : scorers)
        {
            mask |= ((ICountable)scorer).getTokenMask();
        }
        int nbtokens = countBits(mask);
        return nbtokens;
    }
    
    public int countBits(int value)
    {
        int count=0;
        while(value>0)
        {
            count+=value&1;
            value>>=1;
        }
        return count;
    }
    
    protected Scorer getInnerScorer(List<Scorer> required, List<Scorer> optional, int minShouldMatch) throws IOException
    {
        // three cases: conjunction, disjunction, or mix
          // pure disjunction
          if (required.isEmpty()) {
            return new PercentScoreBooleanScorer(this,opt(optional, minShouldMatch),active, maxCoord,minShouldMatch); // idem
          }

          // conjunction-disjunction mix:
          Scorer req = req(required);
          Scorer opt = opt(optional, 0); 
    /*      if (minShouldMatch>0)
              return new PercentScoreBooleanScorer(this,new PercentScoreConjunctionScorer(this, new Scorer[]{ req, opt }),active, maxCoord, minShouldMatch);
          else */
              return new PercentScoreBooleanScorer(this,new PercentScoreReqOptSumScorer(req, opt),active, maxCoord, minShouldMatch);
    }
    
    protected void getUnderOver(List<Scorer> under, List<Scorer> over, List<Scorer> optional) throws IOException
    {
        TermContext[] contextArray = new TermContext[optional.size()];
        collectTermContext(searcher.getIndexReader(), searcher.getIndexReader().leaves(), contextArray, optional);
        
        for(int i=0;i<optional.size();i++)
        {
            if (contextArray[i].docFreq()<maxFreq)
                under.add(optional.get(i));
            else if (contextArray[i].docFreq()>0)
                over.add(optional.get(i));
        }
        
        for(int j=0;j<over.size();j++)
        {
            Scorer s = over.get(j);
            for(int i=0;i<under.size();i++)
            {
                if (((ICountable)under.get(i)).getTokenMask()==((ICountable)s).getTokenMask())
                {
                    Scorer u = under.get(i);
                    over.add(0, u);
                    j++;
                    under.remove(i);
                    i--;
                }
            }
        }
    }
    
    
    private Scorer req(List<Scorer> required) {
      if (required.size() == 1) {
        Scorer req = required.get(0);
        return req;
      } else {
        return new PercentScoreConjunctionScorer(this, 
                                     required.toArray(new Scorer[required.size()]));
      }
    }
  
  private Scorer opt(List<Scorer> optional, int minShouldMatch) throws IOException {
      if (optional.size() == 1) {
        Scorer opt = optional.get(0);
        return opt;
      } else {
        if (minShouldMatch > 1) {
          return new PercentScoreMinShouldMatchSumScorer(this, optional, minShouldMatch);
        } else {
          return new PercentScoreDisjunctionScorer(this, 
                                          optional.toArray(new Scorer[optional.size()]));
        }
      }
    }
  }
    
  @Override
  public Weight createWeight(IndexSearcher searcher) throws IOException {
    PercentScoreWeight jw = new PercentScoreWeight(searcher, maxCoord);
    
    return jw;
  }
  
  
  public List<String> ArrayCopy(List<String> field){
      List<String> fieldCopy = new ArrayList<>();
      for (String string : field) {
          fieldCopy.add(string);
      }
      return fieldCopy;
  }
  
    
  public void collectTermContext(IndexReader reader,
                                List<AtomicReaderContext> leaves, 
                                TermContext[] contextArray,
                                List<Scorer> queryTerms) throws IOException {
    TermsEnum termsEnum = null;
    for (AtomicReaderContext context : leaves) {
      final Fields fields = context.reader().fields();
      if (fields == null) {
        // reader has no fields
        continue;
      }
      for (int i = 0; i < queryTerms.size(); i++) {
        Term term = ((PercentScoreTermQuery)queryTerms.get(i).getWeight().getQuery()).getTerm();
        TermContext termContext = contextArray[i];
        final Terms terms = fields.terms(term.field());
        if (terms == null) {
          // field does not exist
          continue;
        }
        termsEnum = terms.iterator(termsEnum);
        assert termsEnum != null;
        
        if (termsEnum == TermsEnum.EMPTY) continue;
        if (termsEnum.seekExact(term.bytes())) {
          if (termContext == null) {
            contextArray[i] = new TermContext(reader.getContext(),
                                                termsEnum.termState(), 
                                                context.ord, 
                                                termsEnum.docFreq(),
                                                termsEnum.totalTermFreq());
          } else {
            termContext.register(termsEnum.termState(), 
                                    context.ord,
                                    termsEnum.docFreq(), 
                                    termsEnum.totalTermFreq());
          }
          
        }
        
      }
    }
  }
  
}