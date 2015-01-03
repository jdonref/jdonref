package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4Query.JDONREFv4Weight;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4TermQuery.TermWeight;

/**
 *
 * @author Julien
 */
public class JDONREFv4Scorer extends Scorer {
  public final static float ORDERMALUS = 0.5f;
  public final static float NUMBERMALUS = 0;
  
  protected AtomicReaderContext context;
  
  protected ConcurrentHashMap<Integer, Integer> payloadIndex;
  protected IPayloadChecker checker;
  
  int debugDoc = -1;
  
  int mode = -1;
  protected final int minnumber_should_match;
  
  public boolean cumuler(int payload)
  {
      return payload!=3;
  }
  
  /**
   * For debug purposes only
   */
  protected String getFullName(Bucket bucket)
  {
      String ligne1,ligne4, ligne7, code_postal, commune;
      
      if (bucket.d.getValues("ligne1").length>0) ligne1 = bucket.d.getValues("ligne1")[0];
      else ligne1 = "";
      if (bucket.d.getValues("ligne4").length>0) ligne4 = bucket.d.getValues("ligne4")[0];
      else ligne4 = "";
      if (bucket.d.getValues("code_postal").length>0) code_postal = bucket.d.getValues("code_postal")[0];
      else code_postal = "";
      if (bucket.d.getValues("commune").length>0) commune = bucket.d.getValues("commune")[0];
      else commune = "";
      if (bucket.d.getValues("ligne7").length>0) ligne7 = bucket.d.getValues("ligne7")[0];
      else ligne7 = "";
      return ligne1+(ligne1.length()>0?" ":"")+
             ligne4+(ligne4.length()>0?" ":"")+
             code_postal+(code_postal.length()>0?" ":"")+
             commune+(commune.length()>0?" ":"")+
             ligne7;
  }
    
    // DefaultSimilarity only
    // TODO : paste to JDONREFv3TermScorer with a tuned Similarity
    public float score(TermWeight weight,Document d, int doc, Term term,IndexSearcher searcher) throws IOException
    {
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==doc)
        {
            debug = true;
        }
        
        long start = Calendar.getInstance().getTimeInMillis();
        
        // DefaultSimilarity only
        //IndexSearcher searcher = weight.getSearcher();
        
        String field = term.field();
        CollectionStatistics collectionStats = searcher.collectionStatistics(field);
        JDONREFv4TermContext termStates = JDONREFv4TermContext.build(searcher.getTopReaderContext(), term);
        TermStatistics termStats = searcher.termStatistics(term, termStates.getContext());
        
        long docFreq = termStats.docFreq();
        float value = (float)(Math.log(collectionStats.maxDoc()/(double)(docFreq+1)) + 1.0);
        
        AtomicReader atomicreader = context.reader();
        DocsEnum docsEnum = atomicreader.termDocsEnum(term);
        if (debug)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" check docsEnum for doc "+(doc)+ " ");   
        }
        docsEnum.advance(doc);
        float freq;
        
        //if (cumuler(field))
            freq = docsEnum.freq();
        //else
        //    freq = 1; // les tokens doivent être recherchés autant de fois qu'il y a de termes ?
        //float freq = collectionStats.sumTotalTermFreq();
        
        float queryBoost = 1.0f;
        //float queryBoost = query.getBoost();
        float topLevelBoost = 1.0f;
        //float topLevelBoost = weight.getTopLevelBoost();
        
        NumericDocValues numdocvalues = atomicreader.getNormValues(field);
        long normvalues = numdocvalues.get(doc);
        float normvalue = ((DefaultSimilarity)IndexSearcher.getDefaultSimilarity()).decodeNormValue(normvalues);
        float newscore = (float)Math.sqrt(freq) * normvalue        // propre au document
                         * queryBoost * weight.getQueryNorm() * topLevelBoost  // propre à la requête
	                 * value * value
                         / freq ; // divided by the frequency inside the document.
        
        if (debug)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " ");
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " docFreq:"+docFreq);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " maxDoc:"+collectionStats.maxDoc());
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " queryNorm:"+weight.getQueryNorm());
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " fieldNorm:"+normvalue);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " topLevelBoost:"+topLevelBoost);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " queryBoost:"+queryBoost);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " idf:"+value);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+doc+ " tf:"+Math.sqrt(freq));

            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" Calcul du Score for Term :"+term.field()+"="+term.text()+" for doc :"+doc+" = "+newscore);
            long end = Calendar.getInstance().getTimeInMillis();
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" Calcul du Score for Term :"+term.field()+"="+term.text()+" for doc :"+doc+" took "+(end-start)+" ms");
        }
        
        return newscore;
    }
    
    // JDONREFv3TermSimilarity only
    public float score(JDONREFv4TermScorer scorer,Bucket bucket, Term term) throws IOException
    {
        assert(this.mode==JDONREFv4Query.AUTOCOMPLETE && bucket.doc == scorer.docID() || this.mode==JDONREFv4Query.BULK);
        if (bucket.doc == scorer.docID())
        {
            if (debugDoc!=-1 && debugDoc==bucket.doc)
            {
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc :"+bucket.doc+" use native scorer");
            }
            return scorer.score();
        }
        else
        {
            TermWeight weight = (TermWeight) scorer.getWeight();
        
            return score(weight,bucket.d,bucket.doc,term,scorer.getSearcher());
        }
    }
  
    /** Calcule le score total théorique que peut rapporter les tokens d'un document d'un payload donné
     *  Les termes de même offset ne sont comptés qu'une seule fois : le plus long ou celui de score le plus élevé.
     * 
     *  Le nombre de terme est aussi ajouté au bucket (qui tient compte des synonymes et nGram).
     */
    public void totalScore(JDONREFv4TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        String term = ((JDONREFv4TermQuery)(scorer.weight().getQuery())).term.field();
        
        AtomicReader atomicreader = context.reader();
        
        Terms terms = atomicreader.getTermVector(bucket.doc,term);
        if (terms==null) return; // le terme n'existe pas dans ce document.

        if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" totalScore for Term :"+term+", doc :"+bucket.doc);
        
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef current;
        
        Field fieldStartOffsets = termsEnum.getClass().getDeclaredField("startOffsets");
        fieldStartOffsets.setAccessible(true);
        int[] startOffsets = (int[]) fieldStartOffsets.get(termsEnum); // no security manager for now !
        
        Field fieldPayloads = termsEnum.getClass().getDeclaredField("payloads");
        fieldPayloads.setAccessible(true);
        BytesRef payloads = (BytesRef) fieldPayloads.get(termsEnum);
        
        Field fieldPayloadIndex = termsEnum.getClass().getDeclaredField("payloadIndex");
        fieldPayloadIndex.setAccessible(true);
        int[] payloadIndexs = (int[]) fieldPayloadIndex.get(termsEnum);
        
        bucket.maxcoord += startOffsets.length;
        
        float totalScore = 0.0f;
        int i = 0;
        while(!((current = termsEnum.next())==null))
        {
            Term t = new Term(term,current);
            int currentPayload = PayloadHelper.decodeInt(payloads.bytes,payloadIndexs[i]+payloads.offset);
            
            if (currentPayload/1000000==1) // to generalize
            {
                int payloadValue = currentPayload%1000;
                float currentScore = score(scorer,bucket,t);
                int pIndex = this.payloadIndex.get(payloadValue).intValue();

                if (cumuler(payloadValue))
                {
                    bucket.total_score_by_payload[pIndex] += currentScore;
                }
                else
                {
                    bucket.total_score_by_payload[pIndex] = Math.max(bucket.total_score_by_payload[pIndex],currentScore); 
                }
            }
            
            i++;
        }
        
        if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" totalScore for Term :"+term+", doc :"+bucket.doc+" = "+totalScore);
        
        return;
    }
    
    boolean foobar = false;
    
    public void setTotalScore(JDONREFv4TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        boolean ligne1Present = bucket.score_by_payload[payloadIndex.get(1)]>0;
        boolean codePresent = bucket.score_by_payload[payloadIndex.get(3)]>0;    
        boolean communePresent = bucket.score_by_payload[payloadIndex.get(5)]>0;
        boolean ligne4Present = bucket.score_by_payload[payloadIndex.get(2)]>0;
        boolean numeroPresent = bucket.score_by_payload[payloadIndex.get(11)]>0;
        boolean ligne7Present = bucket.score_by_payload[payloadIndex.get(9)]>0;
        boolean codePaysPresent = bucket.score_by_payload[payloadIndex.get(10)]>0;
        
        totalScore(scorer,bucket);
        
        // le score de la ligne 1 et du numero sont toujours présent.
        
        if (ligne1Present && !ligne4Present)
                bucket.total_score_by_payload[payloadIndex.get(2)] = 0;
        
        // le poids de la commune n'est pas prise en compte si seul un code est présent
        if (communePresent && !codePresent)
        {
            bucket.total_score_by_payload[payloadIndex.get(3)] = 0;
        }
        // le poids des codes n'est pas pris en compte si seul une commune est présente
        if (codePresent && !communePresent)
        {
            bucket.total_score_by_payload[payloadIndex.get(5)] = 0;
        }
        
        // fully optionnal score :
        if (!ligne7Present) bucket.total_score_by_payload[payloadIndex.get(9)] = 0;
        if (!codePaysPresent) bucket.total_score_by_payload[payloadIndex.get(10)] = 0;
    }
    
    public float getSumTotalScore(Bucket bucket)
    {
        float total = 0.0f;
        
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        for(int i=0;i<bucket.total_score_by_payload.length;i++)
        {
            float subscore = bucket.total_score_by_payload[i];
            if (debug)
            {
                 Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumTotalScore for doc :"+bucket.doc+" term "+i+": "+subscore);
            }
            total += subscore;
        }
          
        if (debug)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumTotalScore for doc :"+bucket.doc+" total: "+total);
        }
          
        return total;
    }
    
    public float getSumScore(Bucket bucket)
    {
        float sum = 0.0f;
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        if (debug)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" "+getFullName(bucket));
        }
        
        int[] freq_by_term = new int[bucket.score_by_payload.length];
        for(int i=0;i<bucket.score_by_subquery.length;i++)
        {
            float score = bucket.score_by_subquery[i];
            if (score>0)
            {
                //int token = bucket.token_by_subquery[i];
                //int freq = bucket.requestTokenFrequencies[token];
                int freq = bucket.payload_by_subquery[i].length;

                if (freq > 0) {
                    score /= freq;
                    
                    for(int j=0;j<bucket.payload_by_subquery[i].length;j++)
                    {
                        int payload = bucket.payload_by_subquery[i][j];
                        int payloadIdx = payloadIndex.get(payload);
                        
                        float lastscore = bucket.score_by_payload[payloadIdx];
                        
                        if (debug && freq>1)
                            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" ");

                        if (cumuler(payload))
                        {
                            sum += score;
                            bucket.coord += 1.0f/freq;
                            bucket.score_by_payload[payloadIdx] = score+lastscore;
                        } else {
                            if ((score = Math.max(lastscore, score))>lastscore)
                            {
                                sum += score;
                                bucket.coord += 1.0f/freq;
                                if (lastscore>0)
                                {
                                    sum -= lastscore;
                                    //bucket.coord -= 1.0f/freq_by_term[termIdx];
                                }   
                                bucket.score_by_payload[payloadIdx] = score;
                                freq_by_term[payloadIdx] = freq;
                            }
                            else
                                bucket.coord += 1.0f/freq;
                        }
                        if (debug)
                        {
                            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" payload: "+payload+" subquery "+i+"="+score);
                        }
                    }
                }
            }
        }
        
        if (debug) {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" total: " + sum);
        }
        
        return sum;
    }
    
    public boolean isOfTypeAdresse(Bucket bucket)
    {
        return getType(bucket).equals("adresse");
    }
    
    public String getType(Document d)
    {
        String[] values = d.getValues("_type");
        assert(values.length==1);
        return values[0];
    }
    
    public String getType(Bucket bucket)
    {
        return getType(bucket.d);
    }
    
    public void makeFinalScore(JDONREFv4TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        //Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" makeFinalScore doc "+bucket.doc);
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
          if (debug)
          {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"  makeFinalScore doc "+bucket.doc);
          }

          // Score relatif
          bucket.score = getSumScore(bucket);

          if (bucket.score>0)
          {
              if (mode==JDONREFv4Query.BULK)
              {
                // score absolu
                setTotalScore(scorer,bucket);
                float totalScore = getSumTotalScore(bucket);
                bucket.score /= totalScore;

                if (debug)
                {
                    Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+" Score brut :"+bucket.score);
                }

                // ramené à 200.
                bucket.score *= 200;

                if (debug)
                {
                    Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+" Score sur 200 :"+bucket.score);
                }
              }
          }
      }
    
    protected boolean isNumber(String val)
    {
        try
        {
            Integer.parseInt(val);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }
    
  protected void collectBucket(Bucket bucket, JDONREFv4TermScorer scorer) throws IOException
  {
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        // Additionnal collection of data for later scoring adjustment
        JDONREFv4TermQuery query = (JDONREFv4TermQuery) ((TermWeight) scorer.getWeight()).getQuery();
        
        if (query.isChecked())
        {        
            Term term = query.getTerm();
            int token = query.getToken();
            int termQueryIndex = query.getQueryIndex();

            // increment token frequencies
            //bucket.requestTokenFrequencies[token]++;

            if (debug) {
                Logger.getLogger(this.getClass().toString()).debug("Thread " + Thread.currentThread().getName() + " collect score for Term :" + term + ", termQueryIndex:" + termQueryIndex + " doc :" + bucket.doc);
            }
            float currentScore = score(scorer, bucket,term);

            bucket.score_by_subquery[termQueryIndex] = currentScore;
            //bucket.token_by_subquery[termQueryIndex] = token;            
            bucket.payload_by_subquery[termQueryIndex] = scorer.getCurrentPayloads();
            
            if (debug) {
                Logger.getLogger(this.getClass().toString()).debug("Thread " + Thread.currentThread().getName() + " end collect score for Term :" + term + ", termQueryIndex:" + termQueryIndex + " doc :" + bucket.doc+ " score="+currentScore);
            }
        }
  }
  
  private final class JDONREFv3ScorerCollector extends Collector {
    private BucketTable bucketTable;
    private int mask;
    private JDONREFv4TermScorer scorer;
    private AtomicReaderContext context;
    
    public JDONREFv3ScorerCollector(int mask, BucketTable bucketTable, AtomicReaderContext context) {
      this.mask = mask;
      this.bucketTable = bucketTable;
      this.context = context;
    }
    
    @Override
    /**
     * Score ramené à 200 
     */
    public void collect(final int doc) throws IOException {
      final BucketTable table = bucketTable;
      final int i = doc & BucketTable.MASK;
      final Bucket bucket = table.buckets[i];
      
      boolean debug = false;
      
      if (bucket.doc != doc) {                    // invalid bucket
        bucket.doc = doc;                         // set doc
        bucket.d = this.context.reader().document(doc);
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" new doc :"+bucket.doc+" fullName:"+getFullName(bucket));
        
        bucket.bits = mask;                       // initialize mask
        //bucket.coord = 1;                         // initialize coord ... not here because it depend on token frequency
        
        bucket.next = table.first;                // push onto valid list
        table.first = bucket;
      } else {                                    // valid bucket
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" old doc :"+bucket.doc);
        
        bucket.bits |= mask;                      // add bits in mask
        //bucket.coord++;                           // increment coord ... not here because it depend on token frequency
      }
      
      bucket.hits++;
      
      collectBucket(bucket,scorer);
    }
    
    @Override
    public void setNextReader(AtomicReaderContext context) {
      // not needed by this implementation
    }
    
    @Override
    public void setScorer(Scorer scorer) {
      assert(scorer instanceof JDONREFv4TermScorer);
      this.scorer = (JDONREFv4TermScorer) scorer;
    }
    
    @Override
    public boolean acceptsDocsOutOfOrder() {
      return true;
    }

  }
  
  // An internal class which is used in score(Collector, int) for setting the
  // current score. This is required since Collector exposes a setScorer method
  // and implementations that need the score will call scorer.score().
  // Therefore the only methods that are implemented are score() and doc().
  private static final class BucketScorer extends Scorer {

    double score;
    double malus = 1;
    int doc = NO_MORE_DOCS;
    int freq;
    
    public BucketScorer(Weight weight) { super(weight); }
    
    @Override
    public int advance(int target) { return NO_MORE_DOCS; }

    @Override
    public int docID() { return doc; }

    @Override
    public int freq() { return freq; }

    @Override
    public int nextDoc() { return NO_MORE_DOCS; }
    
    @Override
    public float score() { return (float)(score*malus); }
    
    @Override
    public long cost() { return 1; }

  }

  public final class Bucket {
    int doc = -1;             // tells if bucket is valid
    Document d = null;        // the original
    float score;             // incremental score
    float malus;             // malus final à appliquer
    
    //int[] requestTokenFrequencies; // Combien de fois chaque terme de la requête a-t-elle de correspondance ?
                                  // nous (je !) ne le savons qu'au moment de la collecte, terme par terme ...
    float[]  score_by_subquery; // le score de chaque sous requête
    //int[] token_by_subquery;    // la sous-requete associée à chaque score
    int[][] payload_by_subquery;     // le terme de chaque sous requête
    
    float[] score_by_payload;        // le score cumulé de chaque terme
    float[] total_score_by_payload;  // le score cumulé maximum de chaque terme
    
    int maxcoord;             // le nombre total de termes possibles (sans abbréviations et ngram)
    
    // TODO: break out bool anyProhibited, int
    // numRequiredMatched; then we can remove 32 limit on
    // required clauses
    int bits;                // used for bool constraints
    float coord;               // count of terms in score
    int hits;                // count of hits without frequencies
    Bucket next;             // next valid bucket
    
    public Bucket(int maxTokens)
    {
        int size = payloadIndex.size();
        
        score_by_payload = new float[size];
        total_score_by_payload = new float[size];
        
        //requestTokenFrequencies = new int[maxTokens];
        score_by_subquery = new float[maxTokens*size];
        //token_by_subquery = new int[maxTokens*size];
        payload_by_subquery = new int[maxTokens*size][];
    }
  }
  
  /** A simple hash table of document scores within a range. */
  final class BucketTable {
    public static final int SIZE = 1 << 11;
    public static final int MASK = SIZE - 1;

    final Bucket[] buckets = new Bucket[SIZE];
    Bucket first = null;                          // head of valid list
    AtomicReaderContext context;
  
    public BucketTable(AtomicReaderContext context, int maxTokens) {
      // Pre-fill to save the lazy init when collecting
      // each sub:
      for(int idx=0;idx<SIZE;idx++) {
        buckets[idx] = new Bucket(maxTokens);
      }
      this.context = context;
    }

    public Collector newCollector(int mask) {
      return new JDONREFv3ScorerCollector(mask, this,context);
    }

    public int size() { return SIZE; }
  }

  static final class SubScorer {
    public JDONREFv4TermScorer scorer;
    // TODO: re-enable this if BQ ever sends us required clauses
    //public boolean required = false;
    public boolean prohibited;
    public Collector collector;
    public SubScorer next;

    public SubScorer(Scorer scorer, boolean required, boolean prohibited,
        Collector collector, SubScorer next) {
        assert(scorer instanceof JDONREFv4TermScorer);
      if (required) {
        throw new IllegalArgumentException("this scorer cannot handle required=true");
      }
      this.scorer = (JDONREFv4TermScorer) scorer;
      // TODO: re-enable this if BQ ever sends us required clauses
      //this.required = required;
      this.prohibited = prohibited;
      this.collector = collector;
      this.next = next;
    }
  }
  
  private SubScorer scorers = null; // for collect mode
  private BucketTable bucketTable;
  
  // Any time a prohibited clause matches we set bit 0:
  private static final int PROHIBITED_MASK = 1;
  
  protected int maxCoord;
  
  protected int doc = -1;
  
  protected JDONREFv4Weight protectedWeight;
  
  protected JDONREFv4TermScorer[] subScorers = null; // for nextDoc & advance mode
  protected int numScorers;
  protected int nrMatchers = -1;
  protected double score = Float.NaN;
  
  public JDONREFv4Scorer(JDONREFv4Weight weight,
      List<JDONREFv4TermScorer> optionalScorers,
      int maxCoord, AtomicReaderContext context, ConcurrentHashMap<Integer, Integer> payloadIndex,
      int mode, int debugDoc, int maxSizePerType, IPayloadChecker checker,int minnumber_should_match) throws IOException {
    super(weight);
    
    this.protectedWeight = weight;
    this.context = context;
    this.maxCoord = maxCoord;
    this.payloadIndex = payloadIndex;
    this.mode = mode;
    this.debugDoc = debugDoc;
    this.checker = checker;
    this.minnumber_should_match = minnumber_should_match;
    //this.checker.setQuery(weight.getQuery()); // A CORRIGER
    
    bucketTable = new BucketTable(context, weight.weights().size());

    if (optionalScorers != null && optionalScorers.size() > 0)
    {
       // nextDoc & advance mode
       subScorers = new JDONREFv4TermScorer[optionalScorers.size()];
       numScorers = subScorers.length;
       
       // collect mode 
       for(int i=optionalScorers.size()-1;i>=0;i--) // set scorers in order because they are reversed chained !
       {
          JDONREFv4TermScorer scorer = optionalScorers.get(i);
          
          //if (scorer.nextDoc() != NO_MORE_DOCS)
          {
            scorers = new SubScorer(scorer, false, false, bucketTable.newCollector(0), scorers);
          }
       }
       // and set subScorers in order !
       for(int i=0;i<optionalScorers.size();i++)
       {
           JDONREFv4TermScorer scorer = optionalScorers.get(i);
           scorer.setParentScorer(this);
           subScorers[i] = scorer;
           subScorers[i].order = i;
       }
       
       heapify(); // NB: un nextDoc a été appliqué à chaque scorer
    }
  }

  /** 
   * extract from DisjunctionScorer
   * Organize subScorers into a min heap with scorers generating the earliest document on top.
   */
  protected final void heapify() {
    for (int i = (numScorers >> 1) - 1; i >= 0; i--) {
      heapAdjust(i);
    }
  }
  
  /** 
   * extract from DisjunctionScorer
   * The subtree of subScorers at root is a min heap except possibly for its root element.
   * Bubble the root down as required to make the subtree a heap.
   */
  protected final void heapAdjust(int root) {
    JDONREFv4TermScorer scorer = subScorers[root];
    int doc = scorer.docID();
    int i = root;
    while (i <= (numScorers >> 1) - 1) {
      int lchild = (i << 1) + 1;
      JDONREFv4TermScorer lscorer = subScorers[lchild];
      int ldoc = lscorer.docID();
      int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
      JDONREFv4TermScorer rscorer = null;
      if (rchild < numScorers) {
        rscorer = subScorers[rchild];
        rdoc = rscorer.docID();
      }
      if (ldoc < doc) {
        if (rdoc < ldoc) {
          subScorers[i] = rscorer;
          subScorers[rchild] = scorer;
          i = rchild;
        } else {
          subScorers[i] = lscorer;
          subScorers[lchild] = scorer;
          i = lchild;
        }
      } else if (rdoc < doc) {
        subScorers[i] = rscorer;
        subScorers[rchild] = scorer;
        i = rchild;
      } else {
        return;
      }
    }
  }
  
  /** 
   * extract from DisjunctionScorer
   * Remove the root Scorer from subScorers and re-establish it as a heap
   */
  protected final void heapRemoveRoot() {
    if (numScorers == 1) {
      subScorers[0] = null;
      numScorers = 0;
    } else {
      subScorers[0] = subScorers[numScorers - 1];
      subScorers[numScorers - 1] = null;
      --numScorers;
      heapAdjust(0);
    }
  }
  
  public float totalScore(ArrayList<JDONREFv4TermScorer> subscorers,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      JDONREFv4TermScorer scorer = null;
      for(int i=0;i<subscorers.size();i++)
      {
          scorer = subscorers.get(i);
          Term term = ((JDONREFv4TermQuery) ((TermWeight) scorer.getWeight()).getQuery()).getTerm();

          for(int j : scorer.getCurrentPayloads())
          {
            bucket.score_by_payload[payloadIndex.get(j)] = 1.0f; // 1.0f means existence, do not care about value
          }
      }
      
      setTotalScore(scorer,bucket);
      float totalScore = getSumTotalScore(bucket);
      
      return totalScore;
  }
  
  public float totalScore(ArrayList<JDONREFv4TermScorer> subscorers,int doc) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      Document d = this.context.reader().document(doc);
      
      Bucket bucket = new Bucket(this.maxCoord);
      bucket.d = d;
      bucket.doc = doc;
      
      return totalScore(subscorers,bucket);
  }
  
  public int maxCoord() throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      return ((JDONREFv4Query)this.getWeight().getQuery()).getNumTokens();
  }

  // TODO : make a bulkscorer out of this
  /*
  @Override
  public boolean score(Collector collector, int max) throws IOException
  {
    boolean more;
    Bucket tmp;
    BucketScorer bs = new BucketScorer(protectedWeight);

    //long start = java.util.Calendar.getInstance().getTimeInMillis();
    
    // The internal loop will set the score and doc before calling collect.
    collector.setScorer(bs);
    do {
      bucketTable.first = null;
      
      while (current != null) {         // more queued 

        // check prohibited & required
        if ((current.bits & PROHIBITED_MASK) == 0) {

          // TODO: re-enable this if BQ ever sends us required
          // clauses
          //&& (current.bits & requiredMask) == requiredMask) {
          
          // NOTE: Lucene always passes max =
          // Integer.MAX_VALUE today, because we never embed
          // a BooleanScorer inside another (even though
          // that should work)... but in theory an outside
          // app could pass a different max so we must check
          // it:
          if (current.doc >= max){
            tmp = current;
            current = current.next;
            tmp.next = bucketTable.first;
            bucketTable.first = tmp;
            continue;
          }
          
          if (current.hits >= 1) {
            boolean debug = false;
            if (debugDoc!=-1 && debugDoc==current.doc)
            {
                debug = true;
            }
            try
            {
                makeFinalScore(scorers.scorer,current); // peu importe le scorer choisi, seule la valeur de queryNorm est utilisée (la même pour tous les scorer).
            }
            catch(Exception ex)
            {
              Logger.getLogger(JDONREFv3Scorer.class.toString()).error(ex);
              current.score = 0;
            }
            
            // malus pour les éléments non trouvés.
            JDONREFv3Query query = (JDONREFv3Query)this.protectedWeight.getQuery();
            float maxCoord = (query).getNumTokens();
            bs.score = current.score * Math.pow(current.coord / maxCoord,3) ;
            
            // arrondi le résultat à 10^-2
            bs.score = (float)Math.ceil(100*bs.score)/100.0f;
            if (bs.score>200) bs.score = 200; // maximum
            
            if (debug)
            {
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" Collect doc "+current.doc+" coord: ("+current.coord+"/"+maxCoord+")^3 = "+Math.pow(current.coord / maxCoord,2));
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" Collect doc "+current.doc+" final Score: "+bs.score);
            }
            bs.doc = current.doc;
            bs.freq = current.coord;
            collector.collect(current.doc);
          }
        }
        
        current = current.next;         // pop the queue
      }
      
      if (bucketTable.first != null){
        current = bucketTable.first;
        bucketTable.first = current.next;
        
        //long end = java.util.Calendar.getInstance().getTimeInMillis();
        //Logger.getLogger(this.getClass().toString()).debug("Collect took "+(end-start)+" ms");
        return true;
      }

      // refill the queue
      more = false;
      end += BucketTable.SIZE;
      for (SubScorer sub = scorers; sub != null; sub = sub.next) {
        int subScorerDocID = sub.scorer.docID();
        if (subScorerDocID != NO_MORE_DOCS) {
          more |= sub.scorer.score(sub.collector, end, subScorerDocID);
        }
      }
      
      current = bucketTable.first;
      
    } while (current != null || more);
    //long end = java.util.Calendar.getInstance().getTimeInMillis();
    //Logger.getLogger(this.getClass().toString()).debug("Collect took "+(end-start)+" ms");
    return false;
  }*/
  
  @Override
  public int advance(int target) throws IOException {
    if (subScorers==null) return NO_MORE_DOCS;
      
    assert doc != NO_MORE_DOCS;
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==target)
    {
        debug = true;
    }
    if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" advance to "+target);
    while(true) {
      // NB: don't know why subScorers[0] may be null
      if (subScorers[0].advance(target) != NO_MORE_DOCS) {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" document "+subScorers[0].docID()+" trouvé dans "+((JDONREFv4TermQuery)subScorers[0].weight().getQuery()).getTerm().field());
          
        heapAdjust(0);
      } else {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" document "+target+" non trouvé dans "+((JDONREFv4TermQuery)subScorers[0].weight().getQuery()).getTerm().field());
        heapRemoveRoot();
        if (numScorers == 0) {
          return doc = NO_MORE_DOCS;
        }
      }
      if (subScorers[0].docID() >= target && checkPayloads()) {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calcul du score pour  "+subScorers[0].docID());
        
        if (afterNext())
        //if (score!=0.0f || doc==NO_MORE_DOCS)
            return doc;
      }
    }
  }
  
  protected boolean afterNext() throws IOException {
      final JDONREFv4TermScorer sub = subScorers[0];
      doc = sub.docID();
      boolean debug = false;
      if (debugDoc!=-1 && debugDoc==doc)
      {
          debug = true;
      }
      if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Préparation pour le document "+doc);
      
      if (doc != NO_MORE_DOCS) {
          Bucket b = new Bucket(this.maxCoord);
          b.doc = doc;
          b.d = this.subScorers[0].document(); //this.context.reader().document(doc);

          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Calcul du score pour le document "+doc+" scorer "+0);
          collectBucket(b, subScorers[0]);
          nrMatchers = 1;
          countMatches(b, 1);
          countMatches(b, 2);

          if (nrMatchers < minnumber_should_match) return false;
          
          try {
              makeFinalScore(scorers.scorer, b); // peu importe le scorer choisi, seule la valeur de queryNorm est utilisée (la même pour tous les scorer).

          } catch (Exception ex) {
              throw new IOException(ex);
          }

          // malus pour les éléments non trouvés.
          JDONREFv4Query query = (JDONREFv4Query) this.protectedWeight.getQuery();
          float maxCoord = (query).getNumTokens();
          score = b.score * Math.pow(b.coord / maxCoord, 3);

          // arrondi le résultat à 10^-2
          //score = (float) Math.ceil(100 * score) / 100.0f;
          if (score > 200) {
              score = 200; // maximum
          }

          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Calcul du score final pour le document "+doc+" score: "+score);
          
          return true;
      }
      return false;
  }
  
  protected void countMatches(Bucket b,int root) throws IOException {
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==doc)
    {
        debug = true;
    }
    if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" countMatches "+doc);
    if (root < numScorers && subScorers[root].docID() == doc)
    {
      nrMatchers++;
      collectBucket(b,subScorers[root]);
      countMatches(b,(root<<1)+1);
      countMatches(b,(root<<1)+2);
    }
  }

  @Override
  public int docID() {
    return doc;
  }

  @Override
  public int nextDoc() throws IOException {
    assert doc != NO_MORE_DOCS;
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==doc)
    {
        debug = true;
    }
    if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"subscorers size : "+subScorers.length+" first : "+(subScorers[0]==null));
    while(true) {
      int doc = subScorers[0].nextDoc();
      
      if (doc != NO_MORE_DOCS) {
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Nouveau document :"+subScorers[0].docID());
        heapAdjust(0);
      } else {
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Plus de documents dans root");
        heapRemoveRoot();
        if (numScorers == 0) {
          return this.doc = NO_MORE_DOCS;
        }
      }
      if (subScorers[0].docID() != this.doc && checkPayloads()) {
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Nouveau document trouvé, préparation.");
        
        if (afterNext())
            return doc;
      }
    }
  }
  
  public boolean checkPayloads() throws IOException
  {
      checker.clear();
      
      for(int i=0;i<subScorers.length;i++)
      {
          if (subScorers[i]!=null && subScorers[i].docID()==subScorers[0].docID() && subScorers[i].checked)
          {
            do
            {
                if (!checker.checkNextPayload(subScorers[i]))
                    return false;
            } while (subScorers[i].nextPayload());
          }
      }
      
      return checker.check();
  }

  @Override
  public float score() {
    return (float) score;
  }

  @Override
  public int freq() throws IOException {
    return nrMatchers;
  }

  @Override
  public long cost() {
    long sum = 0;
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==doc)
    {
        debug = true;
    }
    if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"subscorers size : "+subScorers.length+" numScorers : "+numScorers);
    for (int i = 0; i < numScorers; i++) {
      sum += subScorers[i].cost();
    }
    return sum;
  }
  
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("boolean(");
    for (SubScorer sub = scorers; sub != null; sub = sub.next) {
      buffer.append(sub.scorer.toString());
      buffer.append(" ");
    }
    buffer.append(")");
    return buffer.toString();
  }
  
  @Override
  public Collection<ChildScorer> getChildren() {
    ArrayList<ChildScorer> children = new ArrayList<ChildScorer>(numScorers);
    for (int i = 0; i < numScorers; i++) {
      children.add(new ChildScorer(subScorers[i], "SHOULD"));
    }
    return children;
  }
}