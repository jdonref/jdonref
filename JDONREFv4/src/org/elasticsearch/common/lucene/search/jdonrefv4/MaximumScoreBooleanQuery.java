package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.elasticsearch.common.lucene.search.MatchNoDocsQuery;
import org.elasticsearch.index.query.QueryParseContext;


/**
 *
 * @author Julien
 */
public class MaximumScoreBooleanQuery extends BooleanQuery
{
    protected int debugDoc = -1;
    
    protected boolean progressiveShouldMatch = false;

    protected int numTokens;
    
    protected QueryParseContext parseContext;

    public QueryParseContext getParseContext() {
        return parseContext;
    }

    public void setParseContext(QueryParseContext parseContext) {
        this.parseContext = parseContext;
    }
    
    
    
    public int getNumTokens() {
        return numTokens;
    }

    public void setNumTokens(int numTokens) {
        this.numTokens = numTokens;
    }
    
    public boolean isProgressiveShouldMatch() {
        return progressiveShouldMatch;
    }

    public void setProgressiveShouldMatch(boolean progressiveShouldMatch) {
        this.progressiveShouldMatch = progressiveShouldMatch;
    }
        
    public void setDebugDoc(int debugDoc) {
        this.debugDoc = debugDoc;
    }
    
    public int getDebugDoc()
    {
        return debugDoc;
    }
    
    public Term[] getQueryTerms()
    {
        ArrayList<Term> terms = new ArrayList<>();
        for(int i=0;i<getClauses().length;i++)
        {
            terms.add(((PayloadAsScoreTermQuery)getClauses()[i].getQuery()).getTerm());
        }
        return terms.toArray(new Term[0]);  //
    }
    
/**
   * Expert: the Weight for BooleanQuery, used to
   * normalize, score and explain these queries.
   *
   * @lucene.experimental
   */
  protected class PayloadAsScoreWeight extends BooleanQuery.BooleanWeight
  {
    protected IndexSearcher searcher; // nécessaire pour affiner la notation
    protected boolean protectedDisableCoord;
    protected int debugDoc;
    
    protected int numTokens = 0;
    
    public PayloadAsScoreWeight(IndexSearcher searcher, boolean disableCoord,int debugDoc)
    throws IOException
    {
      super(searcher,disableCoord);
      
      this.protectedDisableCoord = disableCoord;
      this.searcher = searcher;
      this.debugDoc = debugDoc;
    }

        public int getNumTokens() {
            return numTokens;
        }

        public void setNumTokens(int numTokens) {
            this.numTokens = numTokens;
        }
    
    @Override
    public Query getQuery()
    {
        return MaximumScoreBooleanQuery.this;
    }
    
    @Override
    public float getValueForNormalization() throws IOException {
        return 1.0f;
    }
    
    public float coord(int overlap, int maxOverlap)
    {
        return 1.0f;
    }
    
    public void normalize(float norm, float topLevelBoost)
    {
    }
    
    @Override
    public Explanation explain(AtomicReaderContext context, int doc) throws IOException
    {
        ComplexExplanation maxExpl = new ComplexExplanation();
        maxExpl.setDescription("max of:");
        Iterator<BooleanClause> cIter = clauses().iterator();
        float score = 0.0f;
        
        for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();) {
          Weight w = wIter.next();
          BooleanClause c = cIter.next();
          Explanation e = w.explain(context, doc);
          if (e.isMatch())
          {
              maxExpl.addDetail(e);
              score = Math.max(e.getValue(),score);
          }
        }
        maxExpl.setMatch(score>0.0f);
        maxExpl.setValue(score);
        return maxExpl;
    }
    
    ArrayList<PayloadTermScorer> getSubScorers(AtomicReaderContext context, int doc) throws IOException
    {
        // Search matching subscorers
      ArrayList<PayloadTermScorer> subscorers = new ArrayList<>();
      for (Iterator<Weight> wIter = weights.iterator(); wIter.hasNext();)
      {
        Weight w = wIter.next();
        
        PayloadTermScorer subscorer = (PayloadTermScorer) w.scorer(context, context.reader().getLiveDocs());
        if (subscorer == null) continue;
        
        if (w.explain(context,doc).isMatch())
            subscorers.add(subscorer);
      }
      
      return subscorers;
    }

    //public Explanation explainScore(AtomicReaderContext context,TermWeight weight, int doc) throws IOException
    
    @Override
    public Scorer scorer(AtomicReaderContext context, Bits acceptDocs)
        throws IOException {
        
      List<PayloadTermScorer> optional = new ArrayList<>();
      Iterator<BooleanClause> cIter = clauses().iterator();
      for (Weight w  : weights) {
        BooleanClause c =  cIter.next();
        PayloadTermScorer subScorer = (PayloadTermScorer) w.scorer(context, acceptDocs);
        if (subScorer == null) {
          if (c.isRequired()) {
            return null;
          }
        } else if (c.isRequired()) {
            throw(new IOException("Required clauses are not available."));
        } else if (c.isProhibited()) {
            throw(new IOException("Prohibited clauses are not available."));
        } else {
          optional.add(subScorer);
        }
      }
      
      MaximumScoreBooleanScorer scorer = new MaximumScoreBooleanScorer(this, optional, context, this.debugDoc, this.numTokens);
      
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
    PayloadAsScoreWeight jw = new PayloadAsScoreWeight(searcher, isCoordDisabled(),this.debugDoc);
    
    jw.setNumTokens(numTokens);
    
    return jw;
  }


    public String unsplit(String[] splitted)
    {
        Arrays.sort(splitted, new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                if (o1==null) return 0;
                if (o2==null) return -1;
                return o1.compareTo(o2);
            }
        });
        StringBuilder builder = new StringBuilder();
        for(String s : splitted){
            if(builder.length()>0)
                    builder.append(' ');
            builder.append(s);
        }
        return builder.toString();
    }

  public List<String> ArrayCopy(List<String> field){
      List<String> fieldCopy = new ArrayList<>();
      for (String string : field) {
          fieldCopy.add(string);
      }
      return fieldCopy;
  }
    
    
   public List<String> getWithoutFinalWildcard()
  {
      List<String> field = new ArrayList<String>();
      for(int i=0;i<getClauses().length;i++)
        {
            PayloadAsScoreTermQuery q = (PayloadAsScoreTermQuery)getClauses()[i].getQuery();
            if(!q.isFinalWildCard()){              
                field.add(q.getTerm().text());
            }
        }
      return field;
  } 
    
  public PayloadAsScoreTermQuery getFinalWildcard()
  {
      for(int i=0;i<getClauses().length;i++)
        {
            PayloadAsScoreTermQuery q = (PayloadAsScoreTermQuery)getClauses()[i].getQuery();
            if(q.isFinalWildCard()){
                return q;
            }
        }
      return null;
  } 
  
  // Retourne une liste des termes qui commencent par le fin.getTerm().
  public HashSet<String> getAllWildcardTerms(IndexReader reader,PayloadAsScoreTermQuery fin) throws IOException
  {
        HashSet<String> hs = new HashSet<>();
        Term t_etoile = new Term("fullName", fin.getTerm().text()+"*");
        CompiledAutomaton auto = new CompiledAutomaton(WildcardQuery.toAutomaton(t_etoile));       
  
        final List<AtomicReaderContext> leaves = reader.leaves();
        for (AtomicReaderContext context : leaves) {
                final Fields fields = context.reader().fields();
                final Terms terms = fields.terms(fin.getTerm().field());
                final TermsEnum termsEnum = auto.getTermsEnum(terms);
                while((termsEnum.next())!=null)
                {
                    String str = termsEnum.term().utf8ToString();
                    if (str.indexOf(" ")==-1)
                        hs.add(str);
                }               
        }
        return hs;
  }
  
  
  // combinaison => nombre de termes
  public HashMap<BytesRef,Integer> getAllCombinaisons(IndexReader reader, HashSet<String> termsL) throws IOException
  {
    HashMap<BytesRef, Integer> hm = new HashMap<>();

    List<String> field = getWithoutFinalWildcard();
    for(String s : termsL){
        List<String> fieldCopy = ArrayCopy(field);
        fieldCopy.add(s);
        String unsplitted = unsplit((String[])fieldCopy.toArray(new String[fieldCopy.size()]));
        Analyzer analyser = parseContext.mapperService().analysisService().analyzer("jdonrefv4_search_unsplit"); 
        
        CachingTokenFilter buffer = null;
        TermToBytesRefAttribute termAtt = null;
        TypeAttribute typeAtt = null;
        TokenStream source = null;
        PayloadAttribute payloadAtt = null;
        int numTokens = 0;

        try
        {
            source = analyser.tokenStream("fullName", unsplitted);
            source.reset();
            buffer = new CachingTokenFilter(source);
            buffer.reset();

            if (buffer.hasAttribute(TermToBytesRefAttribute.class)) {
            termAtt = buffer.getAttribute(TermToBytesRefAttribute.class);
            }
            if (buffer.hasAttribute(TypeAttribute.class))
            {
            typeAtt = buffer.getAttribute(TypeAttribute.class);
            }
            if (buffer.hasAttribute(PayloadAttribute.class))
            {
            payloadAtt = buffer.getAttribute(PayloadAttribute.class);
            }

            BytesRef bytes = termAtt == null ? null : termAtt.getBytesRef();
            
            if (termAtt != null) {
                try {
                    while (buffer.incrementToken())
                    {
                        termAtt.fillBytesRef();
                        int payload = PayloadHelper.decodeInt(payloadAtt.getPayload().bytes,payloadAtt.getPayload().offset);
                        
                        hm.put(BytesRef.deepCopyOf(bytes), payload);
                    }
                } catch (IOException e) {
                // ignore (end of tokens)
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Error analyzing query text", e);
        } finally {
            IOUtils.closeWhileHandlingException(source);
        }
    }
      return hm;
  }

  public Query buildRewriteQuery(HashMap<BytesRef,Integer> terms)
  {
      MaximumScoreBooleanQuery query = new MaximumScoreBooleanQuery();
      for(BytesRef value : terms.keySet()){
        PayloadAsScoreTermQuery q = new PayloadAsScoreTermQuery(new Term("fullName", BytesRef.deepCopyOf(value)));
        int payload = terms.get(value);
        q.setNumTerms(payload);
        q.setFinalWildCard(false);
        query.add(new BooleanClause(q,BooleanClause.Occur.SHOULD));
      }
      query.setNumTokens(this.numTokens);
      query.setBoost(this.getBoost());
      query.setDebugDoc(this.debugDoc);
      query.setProgressiveShouldMatch(this.progressiveShouldMatch);
      query.setMinimumNumberShouldMatch(this.minNrShouldMatch);
      return query;
  }
  
  @Override
  public Query rewrite(IndexReader reader) throws IOException
  {
      // si sous-requête qui est à true
      
      // récupére le terme à true
      // cherche les termes qui commencent par
      // pour toutes les combinaisons de termes 
      //   analyser "unsplit"
      //   pour chaque combinaison analysée
      //     crée PayloadAsScoreTermQuery
      //     setFinal(false
      // return
      
      
      // sinon
     PayloadAsScoreTermQuery fin = null;
      
      if ((fin = getFinalWildcard())!=null)
      {
          HashSet<String> terms = getAllWildcardTerms(reader,fin);
          
          if (terms.size()==0) return this; // plantage pour les requêtes vides
          HashMap<BytesRef,Integer> combinaisons = getAllCombinaisons(reader,terms);
          
          return buildRewriteQuery(combinaisons);
      }
      else
      {
        // get clause order by frequencies
        final List<AtomicReaderContext> leaves = reader.leaves();
        TermContext[] contextArray = new TermContext[getClauses().length];
        Term[] queryTerms = getQueryTerms();
        collectTermContext(reader, leaves, contextArray, queryTerms);
        MaximumScoreBooleanQuery.TermFrequencyCount[] freq = getTermInOrder(contextArray);
                
        if (freq.length==0) return new MatchNoDocsQuery();
        
        if (!progressiveShouldMatch) // clean up empty clauses
        {
            MaximumScoreBooleanQuery query = new MaximumScoreBooleanQuery();
            query.setBoost(this.getBoost());
            query.setDebugDoc(this.getDebugDoc());
            query.setMinimumNumberShouldMatch(this.getMinimumNumberShouldMatch());
            query.setNumTokens(this.getNumTokens());
            for(int i=0;i<freq.length;i++)
            {
                if (freq[i].freq>0)
                    query.add(clauses().get(freq[i].i));
            }
            if (query.clauses().isEmpty()) return new MatchNoDocsQuery();
            
            if (query.clauses().size()==this.clauses().size()) return this;
            return query;
        }
        
        int max = freq[0].count;
        int i = 0;
        int start = 0;
        int maxfreq = 0;
        
        do
        {
            do
            {
                maxfreq = Math.max(maxfreq,freq[i].freq);
                i++;
            } while(i<freq.length && freq[i].count==max);
            if (maxfreq==0 && i<freq.length)
            {
                max = freq[i].count;
                start = i;
            }
        } while(maxfreq==0 && i<freq.length);
        
        if (maxfreq==0) return new MatchNoDocsQuery();
        
        if (start==0 && i==clauses().size()) return this;
        
        MaximumScoreBooleanQuery query = new MaximumScoreBooleanQuery();
        query.setBoost(this.getBoost());
        query.setDebugDoc(this.getDebugDoc());
        query.setMinimumNumberShouldMatch(this.getMinimumNumberShouldMatch());
        query.setNumTokens(this.getNumTokens());
        for(int j=start;j<i;j++)
        {
            if (freq[j].freq>0)
                query.add(clauses().get(freq[j].i));
        }
        return query;
      }
  }
  
  public void collectTermContext(IndexReader reader,
                                List<AtomicReaderContext> leaves, 
                                TermContext[] contextArray,
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
  
  /**
     * give the order by frequencies for term query from context
     * @param contextArray
     * @return 
     */
    public MaximumScoreBooleanQuery.TermFrequencyCount[] getTermInOrder(TermContext[] contextArray)
    {
        MaximumScoreBooleanQuery.TermFrequencyCount[] frequencies = new MaximumScoreBooleanQuery.TermFrequencyCount[contextArray.length];
        for(int i=0;i<contextArray.length;i++)
        {
            MaximumScoreBooleanQuery.TermFrequencyCount tf = new MaximumScoreBooleanQuery.TermFrequencyCount();
            tf.i = i;
            if (contextArray[i]!=null)
                tf.freq = contextArray[i].docFreq();
            else
                tf.freq = 0;
            tf.count = ((PayloadAsScoreTermQuery)(clauses().get(i).getQuery())).getNumTerms();
            frequencies[i] = tf;
        }
        Arrays.sort(frequencies);
        
        return frequencies;
    }
  
  public class TermFrequencyCount implements Comparable<MaximumScoreBooleanQuery.TermFrequencyCount>
    {
        public int i;
        public int freq;
        public int count;
        
        @Override
        public int compareTo(MaximumScoreBooleanQuery.TermFrequencyCount o)
        {
            return o.count - count;
        }
    }
  
  
    @Override @SuppressWarnings("unchecked")
  public MaximumScoreBooleanQuery clone() {
    MaximumScoreBooleanQuery clone = (MaximumScoreBooleanQuery)super.clone();
    clone.setNumTokens(this.getNumTokens());
    clone.setDebugDoc(this.getDebugDoc());
    clone.setProgressiveShouldMatch(this.progressiveShouldMatch);
    return clone;
  }
}