package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.spans.IPayloadCheckerSpanQuery;
import org.apache.lucene.search.spans.MultiPayloadTermSpans;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;
import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.apache.lucene.util.Bits;
import org.elasticsearch.common.lucene.search.MatchNoDocsQuery;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4TermQuery.TermWeight;
import org.elasticsearch.index.query.jdonrefv4.JDONREFv4QueryParser;

/**
 *
 * @author Julien
 */
public class JDONREFv4Query extends BooleanQuery implements IPayloadCheckerSpanQuery
{
    //public static boolean DEBUG = false;
    //public static int DEBUGDOCREFERENCE = 10366; //2092;
    public static final int AUTOCOMPLETE = 1;
    public static final int BULK = 2;

    protected ConcurrentHashMap<Integer, Integer> payloadIndex;
    
    protected int numTokens;
    protected String field;
    
    protected int debugDoc = -1;
    protected int mode = JDONREFv4Query.AUTOCOMPLETE;
    
    protected int maxSizePerType = JDONREFv4QueryParser.DEFAULTMAXSIZE;

    protected IPayloadChecker checker;
    protected int termCountPayloadFactor = MultiPayloadTermSpans.NOTERMCOUNTPAYLOADFACTOR;
  
  /** Adds a clause to a boolean query.
   *
   * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number
   * @see #getMaxClauseCount()
   */
    @Override
  public void add(Query query, BooleanClause.Occur occur) {
    add(new BooleanClause(query, occur));
  }

  /** Adds a clause to a boolean query.
   * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number
   * @see #getMaxClauseCount()
   */
    @Override
  public void add(BooleanClause clause) {
    super.add(clause);
    if (clause.getQuery() instanceof JDONREFv4TermQuery)
    {
        JDONREFv4TermQuery q = (JDONREFv4TermQuery)clause.getQuery();
    
        ((JDONREFv4TermQuery)q).setOrder(getClauses().length-1);
        ((JDONREFv4TermQuery)q).setQueryIndex(getClauses().length-1);
        
        if (field == null) {
            field = q.getTerm().field();
        } else if (q.getTerm().field() != null && !q.getTerm().field().equals(field)) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
        q.setTermCountPayloadFactor(termCountPayloadFactor);
    }
  }
    
    /** Returns true iff <code>o</code> is equal to this. */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && o instanceof JDONREFv4Query &&
                checker.equals(((JDONREFv4Query)o).getChecker()) &&
                  termCountPayloadFactor == ((JDONREFv4Query)o).getTermCountPayloadFactor();
    }

    /** Returns a hash code value for this object.*/
    @Override
    public int hashCode() {
      return super.hashCode() + checker.hashCode() + termCountPayloadFactor;
    }
    
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getTermCountPayloadFactor() {
        return termCountPayloadFactor;
    }

    public void setTermCountPayloadFactor(int termCountPayloadFactor) {
        this.termCountPayloadFactor = termCountPayloadFactor;
    }
    
    public IPayloadChecker getChecker() {
        return checker;
    }

    public void setChecker(IPayloadChecker checker) {
        this.checker = checker;
        this.checker.setQuery(this);
    }
    
    public int getNumTokens() {
        return numTokens;
    }

        public void setNumTokens(int numTokens) {
            this.numTokens = numTokens;
        }
        
    public void setDebugDoc(int debugDoc) {
        this.debugDoc = debugDoc;
    }
    
    public int getDebugDoc()
    {
        return debugDoc;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public int getMode()
    {
        return mode;
    }

    public ConcurrentHashMap<Integer, Integer> getPayloadIndex()
    {
        return payloadIndex;
    }
        
    public void setPayloadIndex(ConcurrentHashMap<Integer, Integer> payloadIndex) {
        this.payloadIndex = payloadIndex;
    }

    public int getMaxSizePerType() {
        return maxSizePerType;
    }

    public void setMaxSizePerType(int maxSizePerType) {
        this.maxSizePerType = maxSizePerType;
    }
    
    public Term[] getQueryTerms()
    {
        ArrayList<Term> terms = new ArrayList<>();
        for(int i=0;i<getClauses().length;i++)
        {
            terms.add(((JDONREFv4TermQuery)getClauses()[i].getQuery()).getTerm());
        }
        return terms.toArray(new Term[0]);
    }
    
    @Override
    public JDONREFv4Query clone() {
        JDONREFv4Query query = new JDONREFv4Query();
                
        for(BooleanClause clause : this.getClauses())
        {
            query.add(clause.getQuery().clone(),clause.getOccur());
        }
        
        query.setChecker(checker.clone()); // do checker.setQuery
        query.setTermCountPayloadFactor(termCountPayloadFactor);
        query.setBoost(this.getBoost());
        query.setDebugDoc(this.debugDoc);
        query.setField(this.field);
        query.setLimit(this.limit);
        query.setMaxSizePerType(this.maxSizePerType);
        query.setMinimumNumberShouldMatch(this.minNrShouldMatch);
        query.setMode(this.mode);
        query.setNumTokens(this.numTokens);
        query.setPayloadIndex(this.payloadIndex);
        
        return query;
    }
    
    @Override
    public Query rewrite(IndexReader reader) throws IOException
    {
        // get clause order by frequencies
        final List<AtomicReaderContext> leaves = reader.leaves();
        TermContext[] contextArray = new TermContext[getClauses().length];
        Term[] queryTerms = getQueryTerms();
        collectTermContext(reader, leaves, contextArray, queryTerms);
        JDONREFv4Query.TermFrequency[] freq = getTermInOrder(contextArray);
        
        //MultiPayloadSpanTermQuery mostFrequentQuery = null;
        
        // check limits
        if (limit!=-1 && overLimits(freq))
        {
            return new MatchNoDocsQuery();
//            LongArrayList currentMostFrequentTerms = FrequentTermsUtil.getMostFrequentTerms(queryTerms);
//            
//            if (currentMostFrequentTerms.size()<2)
//            {
//                BooleanFilter boolFilter = new BooleanFilter();
//                for(int i=0;i<queryTerms.length;i++)
//                {
//                    if (freq[i].freq<limit)
//                        boolFilter.add(new TermFilter(queryTerms[i]),Occur.MUST);
//                    else
//                        boolFilter.add(new TermFilter(queryTerms[i]),Occur.SHOULD);
//                }
//                ConstantScoreQuery constantQuery = new ConstantScoreQuery(boolFilter);
//                constantQuery.setBoost(1);
//                return constantQuery;
//            }
//            
//            String currentMostFrequentTerm = FrequentTermsUtil.generateMostFrequentTerms(currentMostFrequentTerms);
//            Term term = new Term("mostFrequentTerms",currentMostFrequentTerm);
//            
//            TermContext mostFrequentContext = collectTermContext(reader,leaves,term);
//            if (mostFrequentContext == null || mostFrequentContext.docFreq()>limit)
//            {
//                BooleanFilter boolFilter = new BooleanFilter();
//                for(int i=0;i<queryTerms.length;i++)
//                {
//                    if (freq[i].freq<limit)
//                        boolFilter.add(new TermFilter(queryTerms[i]),Occur.MUST);
//                    else
//                        boolFilter.add(new TermFilter(queryTerms[i]),Occur.SHOULD);
//                }
//                ConstantScoreQuery constantQuery = new ConstantScoreQuery(boolFilter);
//                constantQuery.setBoost(1);
//                return constantQuery;
//            }
//            
//            mostFrequentQuery = new MultiPayloadSpanTermQuery(term);
//            mostFrequentQuery.setChecked(false);
//            
//            contextArray = Arrays.copyOf(contextArray, contextArray.length+1);
//            contextArray[contextArray.length-1] = mostFrequentContext;
//            
//            this.clauses.add(mostFrequentQuery);
        }
        
        // rewrite
        JDONREFv4Query clone = null;
        for (int i = 0; i < getClauses().length; i++) {
            JDONREFv4TermQuery c = (JDONREFv4TermQuery) getClauses()[i].getQuery();
            JDONREFv4TermQuery query = (JDONREFv4TermQuery) c.rewrite(reader);
            if (query != c) {                     // clause rewrote: must clone
                if (clone == null) {
                    clone = (JDONREFv4Query) this.clone();
                }
                clone.getClauses()[i].setQuery(query); // write over existing clause
            }
        }
        
        if (clone != null) {
            return clone;                        // some clauses rewrote
        } else {
            return this;                         // no clauses rewrote
        }
    }
    
    protected int limit;
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public int getLimit()
    {
        return this.limit;
    }
    
    /**
     * Return true if the less term frequencies is over the limit
     * 
     * @param contextArray assume frequencies are in order
     * @return 
     */
    protected boolean overLimits(JDONREFv4Query.TermFrequency[] freq)
    {
        if (freq.length==0) return true; // empty indeed ! => NoMatch
        
        return freq[0].freq>limit;
    }

    @Override
    public int getClausesCount() {
        return getClauses().length;
    }
    
    public class TermFrequency implements Comparable<JDONREFv4Query.TermFrequency>
    {
        public int i;
        public int freq;
        
        @Override
        public int compareTo(JDONREFv4Query.TermFrequency o)
        {
            return freq - o.freq;
        }
    }
    
    /**
     * give the order by frequencies for term query from context
     * @param contextArray
     * @return 
     */
    public JDONREFv4Query.TermFrequency[] getTermInOrder(TermContext[] contextArray)
    {
        JDONREFv4Query.TermFrequency[] frequencies = new JDONREFv4Query.TermFrequency[contextArray.length];
        for(int i=0;i<contextArray.length;i++)
        {
            JDONREFv4Query.TermFrequency tf = new JDONREFv4Query.TermFrequency();
            tf.i = i;
            if (contextArray[i]!=null)
                tf.freq = contextArray[i].docFreq();
            else
                tf.freq = 0;
            frequencies[i] = tf;
        }
        Arrays.sort(frequencies);
        
        return frequencies;
    }
    
    public int[] revertFrequencies(PayloadCheckerSpanQuery.TermFrequency[] freq)
    {
        int[] res = new int[freq.length];
        for(int i=0;i<freq.length;i++)
            res[freq[i].i] = i;
        
        return res;
    }

  public TermContext collectTermContext(IndexReader reader,
      List<AtomicReaderContext> leaves, Term term) throws IOException
  {
    TermContext termContext = null;
    TermsEnum termsEnum = null;
    for (AtomicReaderContext context : leaves) {
      final Fields fields = context.reader().fields();
      if (fields == null) {
        // reader has no fields
        continue;
      }
        final Terms terms = fields.terms(term.field());
        if (terms == null) {
          // field does not exist
          continue;
        }
        termsEnum = terms.iterator(termsEnum);
        assert termsEnum != null;
        
        if (termsEnum == TermsEnum.EMPTY) continue;
        if (termsEnum.seekExact(term.bytes()))
        {
            if (termContext == null)
                termContext = new TermContext(reader.getContext(),
                    termsEnum.termState(), context.ord, termsEnum.docFreq(),
                    termsEnum.totalTermFreq());
            else
            {
                termContext.register(termsEnum.termState(), context.ord,
                termsEnum.docFreq(), termsEnum.totalTermFreq());
            }
        }
    }
    return termContext;
  }

  public void collectTermContext(IndexReader reader,
      List<AtomicReaderContext> leaves, TermContext[] contextArray,
      Term[] queryTerms) throws IOException {
    TermsEnum termsEnum = null;
    for (AtomicReaderContext context : leaves) {
      final Fields fields = context.reader().fields();
      if (fields == null) {
        // reader has no fields
        continue;
      }
      for (int i = 0; i < queryTerms.length; i++) {
        Term term = queryTerms[i];
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
                termsEnum.termState(), context.ord, termsEnum.docFreq(),
                termsEnum.totalTermFreq());
          } else {
            termContext.register(termsEnum.termState(), context.ord,
                termsEnum.docFreq(), termsEnum.totalTermFreq());
          }
          
        }
        
      }
    }
  }
    
/**
   * Expert: the Weight for BooleanQuery, used to
   * normalize, score and explain these queries.
   *
   * @lucene.experimental
   */
  protected class JDONREFv4Weight extends BooleanQuery.BooleanWeight
  {
    protected IndexSearcher searcher; // nécessaire pour affiner la notation
    protected boolean protectedDisableCoord;
    protected int mode;
    protected int debugDoc;
    
    public ArrayList<Weight> weights()
    {
        return weights;
    }
    
    public IndexSearcher getSearcher()
    {
        return searcher;
    }
    
    public JDONREFv4Weight(IndexSearcher searcher, boolean disableCoord,int mode,int debugDoc)
    throws IOException
    {
      super(searcher,disableCoord);
      
      this.protectedDisableCoord = disableCoord;
      this.searcher = searcher;
      this.mode = mode;
      this.debugDoc = debugDoc;
    }
    
    ArrayList<JDONREFv4TermScorer> getSubScorers(AtomicReaderContext context, int doc) throws IOException
    {
        // Search matching subscorers
      ArrayList<JDONREFv4TermScorer> subscorers = new ArrayList<>();
      for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();)
      {
        Weight w = wIter.next();
        
        JDONREFv4TermScorer subscorer = (JDONREFv4TermScorer) w.scorer(context, context.reader().getLiveDocs());
        if (subscorer == null) continue;
        
        if (w.explain(context,doc).isMatch())
            subscorers.add(subscorer);
      }
      
      return subscorers;
    }

    public Explanation explainScore(AtomicReaderContext context,TermWeight weight, int doc) throws IOException
    {
        JDONREFv4TermScorer scorer = (JDONREFv4TermScorer) weight.scorer(context, context.reader().getLiveDocs());
        
        if (scorer!=null)
        {
            int newDoc = scorer.advance(doc);

            if (newDoc==doc)
            {
                ComplexExplanation expl = new ComplexExplanation();

                // DefaultSimilarity only
                JDONREFv4TermQuery query = (JDONREFv4TermQuery) weight.getQuery(); // NB: pas de cohérence entre term et query. En fait, le boost query n'est pas supporté, et le weight est le même quelque soit la requête ...
                IndexSearcher searcher = weight.getSearcher();
                Term term = query.getTerm();

                String field = term.field();
                CollectionStatistics collectionStats = searcher.collectionStatistics(field);
                JDONREFv4TermContext termStates = JDONREFv4TermContext.build(searcher.getTopReaderContext(), term);
                TermStatistics termStats = searcher.termStatistics(term, termStates.getContext());
                float value = (float)(Math.log(collectionStats.maxDoc()/(double)(termStats.docFreq()+1)) + 1.0);

                AtomicReader atomicreader = context.reader();
                DocsEnum docsEnum = atomicreader.termDocsEnum(term);
                docsEnum.advance(doc);
                float freq;

                if (true) //JDONREFv4Scorer.cumuler(field))
                    freq = docsEnum.freq();
                //else
                //    freq = 1; // les tokens doivent être recherchés autant de fois qu'il y a de termes ?

                float queryBoost = 1.0f;
                float topLevelBoost = 1.0f;

                NumericDocValues numdocvalues = atomicreader.getNormValues(field);
                long normvalues = numdocvalues.get(doc);
                float normvalue = ((DefaultSimilarity)searcher.getDefaultSimilarity()).decodeNormValue(normvalues);
                float newscore = (float)Math.sqrt(freq) * normvalue        // propre au document
                                 * queryBoost * weight.getQueryNorm() * topLevelBoost  // propre à la requête
                                 * value * value
                                 / freq ;

                Explanation freqExpl = new Explanation(1.0f/freq, "1/frequency(freq="+freq+")="+(1.0f/freq));
                
                Explanation fieldNormExpl = new Explanation(normvalue,"fieldnorm(doc="+doc+")");
                Explanation idfExpl = new Explanation(value,"idf(docFreq="+termStats.docFreq()+",maxDocs="+collectionStats.maxDoc()+")");
                Explanation tfExpl = new Explanation((float)Math.sqrt(freq),"tf(freq="+freq+")");

                Explanation fieldWeightExpl = new ComplexExplanation();
                fieldWeightExpl.setDescription("fieldWeight in "+doc+", product of:");
                fieldWeightExpl.setValue((float)Math.sqrt(freq)*value*normvalue);
                fieldWeightExpl.addDetail(tfExpl);
                fieldWeightExpl.addDetail(idfExpl);
                fieldWeightExpl.addDetail(fieldNormExpl);

                Explanation queryNormExpl = new Explanation(weight.getQueryNorm(),"queryNorm");

                Explanation queryWeightExpl = new ComplexExplanation();
                queryWeightExpl.setValue(value*weight.getQueryNorm());
                queryWeightExpl.setDescription("queryWeight, product of:");
                queryWeightExpl.addDetail(idfExpl);
                queryWeightExpl.addDetail(queryNormExpl);

                Explanation productExpl = new ComplexExplanation();
                productExpl.setValue(newscore);
                productExpl.setDescription("score(doc="+doc+",freq="+freq+", termFreq="+freq+"), product of:");
                productExpl.addDetail(queryWeightExpl);

                expl.setValue(newscore);
                expl.setDescription("weight("+term.field()+":"+term.text()+" in "+doc+") ["+weight.getSimilarity()+"], result of");
                expl.addDetail(productExpl);
                expl.addDetail(fieldWeightExpl);
                expl.addDetail(freqExpl);
                expl.setMatch(newscore>0);

                return expl;
            }
        }
        
        return new ComplexExplanation(false, 0.0f, "no matching term");
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
        JDONREFv4Query.this.getMinimumNumberShouldMatch();
        
        ComplexExplanation sumExpl = new ComplexExplanation();
          sumExpl.setDescription("sum of:");
          float coord = 0;
          float sum = 0.0f;
          int shouldMatchCount = 0;
          int[] requestTokenFrequencies = new int[JDONREFv4Query.this.numTokens];
          ArrayList<Explanation> explanations = new ArrayList<Explanation>();
          ArrayList<Integer> tokens = new ArrayList<Integer>();
          ArrayList<Integer> terms = new ArrayList<Integer>();
          ArrayList<Boolean> cumuler = new ArrayList<Boolean>();

          // Get explanations
          Iterator<BooleanClause> cIter = clauses().iterator();
          for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();) {
              Weight w = wIter.next();
              BooleanClause c = cIter.next();
              if (w.scorer(context,context.reader().getLiveDocs()) == null) {
                  continue;
              }
              Explanation e = explainScore(context, (TermWeight) w, doc);
              if (e.isMatch()) {
                  JDONREFv4TermQuery query = (JDONREFv4TermQuery) w.getQuery();
                  String term = query.getTerm().field();
                  int token = query.getToken();
                  boolean cumul = true; //JDONREFv4Scorer.cumuler(term);

                  requestTokenFrequencies[token]++;
                  tokens.add(token);
                  explanations.add(e);
               //   terms.add(termIndex.get(term));
                  cumuler.add(cumul);

                  if (c.getOccur() == BooleanClause.Occur.SHOULD) {
                      shouldMatchCount++;
                  }
              }
          }

          int maxterms = ((JDONREFv4Query)this.getQuery()).payloadIndex.size();
          float[] max_by_term = new float[maxterms];
          Explanation[] noncumulExpl = new Explanation[maxterms];

          // Apply token frequencies and add details
          for (int i = 0; i < explanations.size(); i++) {
              Explanation e = explanations.get(i);
              int token = tokens.get(i);
              int freq = requestTokenFrequencies[token];
              assert (freq > 0);

              if (cumuler.get(i)) {
                  sum += e.getValue() / freq;
                  coord += 1.0f / freq;

                  if (freq == 1) {
                      sumExpl.addDetail(e);
                  } else {
                      Explanation dotExpl = new ComplexExplanation();
                      dotExpl.setDescription("coord product of");
                      dotExpl.addDetail(e);

                      Explanation freqExpl = new Explanation(1.0f / freq, "coord : 1/" + freq);
                      dotExpl.addDetail(freqExpl);

                      dotExpl.setValue(e.getValue() / freq);
                      sumExpl.addDetail(dotExpl);
                  }
              } else {
                  int term = terms.get(i);
                  float score = e.getValue() / freq;
                  float lastscore = max_by_term[term];
                  if (score > lastscore) {
                      sum += score;
                      coord += 1.0f / freq;
                      if (lastscore > 0) {
                          sum -= lastscore;
                          //coord -= coord_by_term[term];
                      } else // noncumulExpl creation shall be here !
                      {
                          noncumulExpl[term] = new ComplexExplanation();
                          noncumulExpl[term].setDescription("Maximum of");
                          sumExpl.addDetail(noncumulExpl[term]);
                      }

                      max_by_term[term] = score;
                      //coord_by_term[term] = 1.0f / freq;
                      noncumulExpl[term].setValue(max_by_term[term]);
                  }
                  else
                      coord += 1.0f / freq;

                  if (freq == 1) {
                      noncumulExpl[term].addDetail(e);
                  } else {
                      Explanation dotExpl = new ComplexExplanation();
                      dotExpl.setDescription("coord product of");
                      dotExpl.addDetail(e);

                      Explanation freqExpl = new Explanation(1.0f / freq, "coord : 1/" + freq);
                      dotExpl.addDetail(freqExpl);

                      dotExpl.setValue(sum);
                      noncumulExpl[term].addDetail(dotExpl);
                  }
              }
          }

          // return
          if (shouldMatchCount < minShouldMatch) {
              sumExpl.setMatch(Boolean.FALSE);
              sumExpl.setValue(0.0f);
              sumExpl.setDescription("Failure to match minimum number " +
                      "of optional clauses: " + minShouldMatch);
              return sumExpl;
          }

          sumExpl.setMatch(0 < coord ? Boolean.TRUE : Boolean.FALSE);
          sumExpl.setValue(sum);

          return sumExpl;
      }
    
    public float explainCoord(AtomicReaderContext context, int doc) throws IOException
    {
      final int minShouldMatch =
        JDONREFv4Query.this.getMinimumNumberShouldMatch();
        
        ComplexExplanation sumExpl = new ComplexExplanation();
          sumExpl.setDescription("sum of:");
          float coord = 0;
          int shouldMatchCount = 0;
          int[] requestTokenFrequencies = new int[JDONREFv4Query.this.numTokens];
          ArrayList<Integer> tokens = new ArrayList<Integer>();

          // Get explanations
          Iterator<BooleanClause> cIter = clauses().iterator();
          for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();) {
              Weight w = wIter.next();
              BooleanClause c = cIter.next();
              if (w.scorer(context, context.reader().getLiveDocs()) == null) {
                  continue;
              }
              Explanation e = explainScore(context, (TermWeight) w, doc);
              if (e.isMatch()) {
                  JDONREFv4TermQuery query = (JDONREFv4TermQuery) w.getQuery();
                  int token = query.getToken();
                  
                  requestTokenFrequencies[token]++;
                  tokens.add(token);
                  
                  if (c.getOccur() == BooleanClause.Occur.SHOULD) {
                      shouldMatchCount++;
                  }
              }
          }

          // Apply token frequencies and add details
          for (int i = 0; i < tokens.size(); i++) {
              int token = tokens.get(i);
              int freq = requestTokenFrequencies[token];
              assert (freq > 0);
              coord += 1.0f / freq;
          }

          // return
          if (shouldMatchCount < minShouldMatch) {
              return 1;
          }

          return coord;
    }
    
    @Override
    public Explanation explain(AtomicReaderContext context, int doc)
      throws IOException {
      
      float value = 0.0f;
      
      boolean debug = false;
      
      // Attention ! context.reader().document(doc) ne permet pas d'identifier le bon document !
      if (debugDoc!=-1 && doc==debugDoc)
          debug = true;
      /*
      if (DEBUG && context.reader().document(doc+context.docBase).get("fullName").equals(DEBUGREFERENCE))
          debug = DEBUG;
      
      if (DEBUG && context.reader().document(doc+context.docBase).get("fullName").equals(DEBUGREFERENCE))
          debug = DEBUG;
      */
      if (debug)
      Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" "+"Explain doc "+doc+" notation");
      
      ComplexExplanation dotExpl = new ComplexExplanation();
      dotExpl.setDescription("product of:");
      
      Explanation boolExpl = booleanScorerExplainWithoutCoord(context,doc);
      value = boolExpl.getValue();
      dotExpl.addDetail(boolExpl);
      
      if (debug)
      Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+" value "+value);
      
      JDONREFv4Scorer scorer = (JDONREFv4Scorer) scorer(context, context.reader().getLiveDocs());
      ArrayList<JDONREFv4TermScorer> subscorers = getSubScorers(context,doc);
      
      if (value>0)
      {
          if (mode==JDONREFv4Query.BULK)
          {
              // total
              try
              {
                float total = scorer.totalScore(subscorers,doc);
                Explanation totalExpl = new Explanation(1/total,"maximum Score ("+total+" inverted)");
                dotExpl.addDetail(totalExpl);
                value /= total;

                if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+" And max score 1/"+total+"="+(1/total));
              }
              catch(Exception e)
              {
                  throw new IOException(e);
              }
          }
          // coord
          try
          {
            int maxcoord = scorer.maxCoord();
            float coord = explainCoord(context,doc);
            if (coord!=maxcoord)
            {
                float pow = (float)Math.pow(coord/(float)maxcoord,3);
                Explanation coordExpl = new Explanation(pow,"coord("+coord+"/"+maxcoord+")^3");
                dotExpl.addDetail(coordExpl);
                value *= pow;

                if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+" And coord ("+coord+"/"+maxcoord+")^3="+pow);
            }
          }
          catch(Exception e)
          {
              throw new IOException(e);
          }
      }
      
      if (mode==BULK)
      {
        // 200
        Explanation twohundredexpl = new Explanation(200,"rapporté à 200");
        dotExpl.addDetail(twohundredexpl);
        value *= 200;
      
        // arrondi le résultat à 10^-2
        value = (float)Math.ceil(100*value)/100.0f;
        if (value>200) value=200.0f; // maximum
      }
      
      // Set value
      dotExpl.setValue(value);
      dotExpl.setMatch(value>0);
      
      if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+" ce qui donne "+value);
      
      return dotExpl;
    }
    
    @Override
    public Scorer scorer(AtomicReaderContext context, Bits acceptDocs)
        throws IOException {
        
      int count = 0;
        
      List<JDONREFv4TermScorer> optional = new ArrayList<>();
      Iterator<BooleanClause> cIter = clauses().iterator();
      for (Weight w  : weights) {
        BooleanClause c =  cIter.next();
        JDONREFv4TermScorer subScorer = (JDONREFv4TermScorer) w.scorer(context, acceptDocs);
        if (subScorer == null) {
          if (c.isRequired()) {
            return null;
          }
        } else if (c.isRequired()) {
            throw(new IOException("Required clauses are not available."));
        } else if (c.isProhibited()) {
            throw(new IOException("Prohibited clauses are not available."));
        } else {
          if (((JDONREFv4TermQuery)w.getQuery()).isChecked())
                  count++;
          optional.add(subScorer);
        }
      }

      if (count>1)
        count = Math.round(70.0f*count/100.0f);
      
      JDONREFv4Scorer scorer = new JDONREFv4Scorer(this, optional, maxCoord, context, payloadIndex,this.mode,this.debugDoc, maxSizePerType, checker, count);
      
      return scorer;
    }
    
    // from FilteredQuery
    public class QueryFirstBulkScorer extends BulkScorer {

    private final Scorer scorer;
    private final Bits filterBits;

    public QueryFirstBulkScorer(Scorer scorer, Bits filterBits) {
      this.scorer = scorer;
      this.filterBits = filterBits;
    }

    @Override
    public boolean score(Collector collector, int maxDoc) throws IOException {
      // the normalization trick already applies the boost of this query,
      // so we can use the wrapped scorer directly:
      collector.setScorer(scorer);
      if (scorer.docID() == -1) {
        scorer.nextDoc();
      }
      while (true) {
        final int scorerDoc = scorer.docID();
        if (scorerDoc < maxDoc) {
          if (filterBits==null || filterBits.get(scorerDoc)) { // filterBits may be null
            collector.collect(scorerDoc);
          }
          scorer.nextDoc();
        } else {
          break;
        }
      }

      return scorer.docID() != Scorer.NO_MORE_DOCS;
    }
  }
    
    @Override
    public BulkScorer bulkScorer(AtomicReaderContext context, boolean scoreDocsInOrder,
                                 Bits acceptDocs) throws IOException {

      return new QueryFirstBulkScorer(scorer(context,acceptDocs),acceptDocs);
    }
  }
    
  @Override
  public Weight createWeight(IndexSearcher searcher) throws IOException {
    Weight jw = new JDONREFv4Weight(searcher, isCoordDisabled(),this.mode,this.debugDoc);
    
    return jw;
  }
}