package org.apache.lucene.search;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.JDONREFv3TermContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.JDONREFv3Query.JDONREFv3ESWeight;
import org.apache.lucene.search.JDONREFv3TermQuery.TermWeight;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Julien
 */
public class JDONREFv3Scorer extends Scorer {
  public final static float ORDERMALUS = 0.5f;
  public final static float NUMBERMALUS = 0;
  
  protected AtomicReaderContext context;
  
  protected Hashtable<String, Integer> termIndex;
  protected int maxSizePerType;
  
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
  
    /**
     * Retourne vrai si le poids de tous les champs du terme doivent être cumulés.
     * Retourne faux si seul le poids le plus élevé de tous les champs du terme doit être pris en compte.
     * @param term
     * @return
     */
    public static boolean cumuler(String term)
    {
        if (term.equals("codes")) return false;
        
        return true;
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
        JDONREFv3TermContext termStates = JDONREFv3TermContext.build(searcher.getTopReaderContext(), term);
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
        
        if (cumuler(field))
            freq = docsEnum.freq();
        else
            freq = 1; // les tokens doivent être recherchés autant de fois qu'il y a de termes ?
        //float freq = collectionStats.sumTotalTermFreq();
        
        float queryBoost = 1.0f;
        //float queryBoost = query.getBoost();
        float topLevelBoost = 1.0f;
        //float topLevelBoost = weight.getTopLevelBoost();
        
        NumericDocValues numdocvalues = atomicreader.getNormValues(field);
        long normvalues = numdocvalues.get(doc);
        float normvalue = ((DefaultSimilarity)searcher.getDefaultSimilarity()).decodeNormValue(normvalues);
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
    public float score(JDONREFv3TermScorer scorer,Bucket bucket, Term term) throws IOException
    {
        assert(this.mode==JDONREFv3Query.AUTOCOMPLETE && bucket.doc == scorer.docID() || this.mode==JDONREFv3Query.BULK);
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
        
            return score(weight,bucket.d,bucket.doc,term,weight.searcher);
        }
    }
    
    // JDONREFv3TermSimilarity only
    public float score(JDONREFv3TermScorer scorer,Bucket bucket, Term term, IndexSearcher searcher) throws IOException
    {
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
        
            return score(weight,bucket.d,bucket.doc,term,searcher);
        }
    }
  
    /** Calcule le score total théorique que peut rapporter un terme d'un document
     *  Les termes de même offset ne sont comptés qu'une seule fois : le plus long ou celui de score le plus élevé.
     * 
     *  Le nombre de terme est aussi ajouté au bucket (qui tient compte des synonymes et nGram).
     */
    public float totalScore(JDONREFv3TermScorer scorer,String term, Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        AtomicReader atomicreader = context.reader();
        Terms terms = atomicreader.getTermVector(bucket.doc,term);
        if (terms==null) return 0.0f; // le terme n'existe pas dans ce document.
        
        IndexSearcher searcher = scorer.getSearcher(); // same searcher for all terms
        
        if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" totalScore for Term :"+term+", doc :"+bucket.doc);
        
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef current;
        
        Field field = termsEnum.getClass().getDeclaredField("startOffsets");
        field.setAccessible(true);
        int[] startOffsets = (int[]) field.get(termsEnum); // no security manager for now !
        
        bucket.maxcoord += startOffsets.length;
        
        HashMap<Integer,Float> scoresPosition = new HashMap<Integer,Float>();
        HashMap<Integer,Integer> lengthPosition = new HashMap<Integer,Integer>();
        
        float totalScore = 0.0f;
        int i = 0;
        while(!((current = termsEnum.next())==null))
        {
            Term t = new Term(term,current);
            int length = t.text().length();
            int offset = startOffsets[i];
            
            float score = score(scorer,bucket,t,searcher);
            
            Float lastscore = scoresPosition.get(offset);
            Integer lastlength = lengthPosition.get(offset);
            
            if (lastscore!=null)
            {
                if (lastscore < score || lastscore==score && lastlength<length) // lastlength < length  ... le plus gros score n'est pas toujours le plus pertinent !?
                {
                    if (cumuler(term))
                    {
                        totalScore -= lastscore;
                        totalScore += score;
                    }
                    else
                    {
                        totalScore = Math.max(score,totalScore); // lastscore may be == totalScore !? no need to check
                    }
                    scoresPosition.put(offset, score);
                    lengthPosition.put(offset, length);
                }
                // else do nothing
            }
            else
            {
                if (cumuler(term))
                    totalScore += score;
                else
                    totalScore = Math.max(totalScore,score);
                scoresPosition.put(offset, score);
                lengthPosition.put(offset, length);
            }
            
            i++;
        }
        
        if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" totalScore for Term :"+term+", doc :"+bucket.doc+" = "+totalScore);
        
        return totalScore;
    }
    
    boolean foobar = false;
    
    public void setTotalScore(JDONREFv3TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        boolean ligne1Present = bucket.score_by_term[termIndex.get("ligne1")]>0;
        boolean codePresent = bucket.score_by_term[termIndex.get("codes")]>0;
        boolean communePresent = bucket.score_by_term[termIndex.get("commune")]>0;
        boolean ligne4Present = bucket.score_by_term[termIndex.get("ligne4")]>0;
        boolean ligne7Present = bucket.score_by_term[termIndex.get("ligne7")]>0;
        boolean codePaysPresent = bucket.score_by_term[termIndex.get("code_pays")]>0;
        
        bucket.total_score_by_term[termIndex.get("ligne1")] = this.totalScore(scorer,"ligne1", bucket);
        
        if (ligne1Present)
        {
            if (ligne4Present)
                bucket.total_score_by_term[termIndex.get("ligne4")] = this.totalScore(scorer,"ligne4", bucket);
        }
        else
        {
            bucket.total_score_by_term[termIndex.get("ligne4")] = this.totalScore(scorer,"ligne4", bucket);
        }
        
        // le poids de la commune n'est pas prise en compte si seul un code est présent
        if (communePresent || !codePresent)
        {
            bucket.total_score_by_term[termIndex.get("commune")] = this.totalScore(scorer,"commune", bucket);
        }
        // le poids des codes n'est pas pris en compte si seul une commune est présente
        if (codePresent || !communePresent)
        {
            bucket.total_score_by_term[termIndex.get("codes")] = this.totalScore(scorer,"codes", bucket);
        }
        
        if (ligne7Present)
            bucket.total_score_by_term[termIndex.get("ligne7")] = this.totalScore(scorer,"ligne7", bucket);
        if (codePaysPresent)
            bucket.total_score_by_term[termIndex.get("code_pays")] = this.totalScore(scorer,"code_pays", bucket);
    }
    
    public float getSumTotalScore(Bucket bucket)
    {
        float total = 0.0f;
        
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        for(int i=0;i<bucket.total_score_by_term.length;i++)
        {
            float subscore = bucket.total_score_by_term[i];
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
        
        int[] freq_by_term = new int[bucket.score_by_term.length];
        for(int i=0;i<bucket.score_by_subquery.length;i++)
        {
            float score = bucket.score_by_subquery[i];
            if (score>0)
            {
                int token = bucket.token_by_subquery[i];
                int freq = bucket.requestTokenFrequencies[token];

                if (freq > 0) {
                    score /= freq;
                    
                    String term = bucket.term_by_subquery[i];
                    int termIdx = termIndex.get(term);
                    float lastscore = bucket.score_by_term[termIdx];

                    if (debug && freq>1)
                        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" ");
                    
                    if (cumuler(term)) {
                        sum += score;
                        bucket.coord += 1.0f/freq;
                        bucket.score_by_term[termIdx] = score+lastscore;
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
                            bucket.score_by_term[termIdx] = score;
                            freq_by_term[termIdx] = freq;
                        }
                        else
                            bucket.coord += 1.0f/freq;
                    }
                    if (debug)
                    {
                        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" term: "+term+" subquery "+i+"="+score);
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
    
    public void makeFinalScore(JDONREFv3TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
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
          
          // Malus
          calculateMalus(bucket);
          if (debug)
          {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+" Score avant malus :"+bucket.score);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+" malus :"+bucket.malus);
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+" Score après malus :"+bucket.score*bucket.malus);
          }
          bucket.score *= bucket.malus;
          
          if (bucket.score>0)
          {
              if (mode==JDONREFv3Query.BULK)
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
    
    /**
     * Check whether there is an adress number (or potentially) in the request
     * @param bucket
     * @param term
     */
    public void analyzeAdressNumber(Bucket bucket, Term term)
    {
        if (term.field().equals("ligne4"))
        {
            String[] numbers = bucket.d.getValues("numero");
            if (debugDoc!=-1 && debugDoc==bucket.doc)
            {
                if (numbers.length>0)
                    Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" check doc "+bucket.doc+" for adress number "+numbers[0]+"=="+term.text()+"?");
                else
                    Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" check doc "+bucket.doc+" no adress number");
            }
            if (numbers.length>0 && numbers[0].equals(term.text()))
                bucket.adressNumberPresent = true;
        }
    }
    
   /**
     * Check whether a code is present before an adress or not.
     * Need analyzeOrder execution before.
     * @param bucket
     * @param term
     */
    public void analyzeCodeBeforeAdress(Bucket bucket, Term term)
    {
        if (bucket.currentAnalyzedField.equals("codes"))
        {
            if (term.field().equals("ligne4"))
                bucket.isThereCodeBeforeAdress = true;
        }
    }
    
    /**
     * Check whether the term is analyzed in good order or not
     * @param bucket
     * @param term
     */
    public void analyzeOrder(Bucket bucket, Term term)
    {
        if (bucket.currentAnalyzedField==null)
        {
            bucket.currentAnalyzedField = term.field();
            
            bucket.analyzedFields[termIndex.get(term.field())] = true;
            //bucket.analyzedTypes.put(term.field(),true);
        }
        else
        {
            if (bucket.currentAnalyzedField.equals(term.field()))
            {
                // do nothing, right order
            }
            else if (!bucket.analyzedFields[termIndex.get(term.field())])
            //else if (bucket.analyzedTypes.get(term.field())==null)
            {
                // new field discovered !
                bucket.currentAnalyzedField = term.field();
                bucket.analyzedFields[termIndex.get(term.field())] = true;
                //bucket.analyzedTypes.put(term.field(),true);
            }
            else
            {
                // wrong order
                bucket.wrongOrder = true;
            }
        }
    }
    
  protected void collectBucket(Bucket bucket, JDONREFv3TermScorer scorer) throws IOException
  {
        boolean debug = false;
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        // Additionnal collection of data for later scoring adjustment
        JDONREFv3TermQuery query = (JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery();
        Term term = query.getTerm();
        int token = query.getToken();
        int termQueryIndex = query.getQueryIndex();

        // increment token frequencies
        bucket.requestTokenFrequencies[token]++;

        if (debug) {
            Logger.getLogger(this.getClass().toString()).debug("Thread " + Thread.currentThread().getName() + " collect score for Term :" + term + ", termQueryIndex:" + termQueryIndex + " doc :" + bucket.doc);
        //float score = scorer.score();
        }
        float score = score(scorer, bucket,term);
        analyzeOrder(bucket, term);
        analyzeCodeBeforeAdress(bucket, term);
        analyzeAdressNumber(bucket, term);

        //bucket.score_by_term.put(term.field(),score);
        bucket.score_by_subquery[termQueryIndex] = score;
        bucket.token_by_subquery[termQueryIndex] = token;
        bucket.term_by_subquery[termQueryIndex] = term.field();
        if (debug) {
            Logger.getLogger(this.getClass().toString()).debug("Thread " + Thread.currentThread().getName() + " end collect score for Term :" + term + ", termQueryIndex:" + termQueryIndex + " doc :" + bucket.doc+ " score="+score);
        }
  }
  
  private final class JDONREFv3ScorerCollector extends Collector {
    private BucketTable bucketTable;
    private int mask;
    private JDONREFv3TermScorer scorer;
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
        String type = getType(bucket);
        if (!typeReachLimit(type))
            increaseCountByType(type);
        else
        {
            if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc :"+bucket.doc+" de type "+type+" a atteint la limite");
            return;
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
        String type = getType(bucket);
        if (typeReachLimit(type))
        {
            if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" doc :"+bucket.doc+" de type "+type+" a atteint la limite");
            return;
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
      assert(scorer instanceof JDONREFv3TermScorer);
      this.scorer = (JDONREFv3TermScorer) scorer;
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
    
    boolean isOfTypeAdress;       // le document est une adresse
    boolean adressNumberPresent; // présence du numéro d'adresse ...
    boolean isThereCodeBeforeAdress; // présence d'un code devant la ligne 4 de l'adresse
    
    String currentAnalyzedField;            // current Type beeing analyzed
    //HashMap<String,Boolean> analyzedTypes; // all types already been analyzed in order
    boolean[] analyzedFields; // all types already been analyzed in order
    boolean wrongOrder;
    
    int[] requestTokenFrequencies; // Combien de fois chaque terme de la requête a-t-elle de correspondance ?
                                  // nous (je !) ne le savons qu'au moment de la collecte, terme par terme ...
    float[]  score_by_subquery; // le score de chaque sous requête
    int[] token_by_subquery;    // la sous-requete associée à chaque score
    String[] term_by_subquery;     // le terme de chaque sous requête
    
    float[] score_by_term;        // le score cumulé de chaque terme
    float[] total_score_by_term;  // le score cumulé maximum de chaque terme
    
    //HashMap<String,Float> score_by_term; // le score cumulé de chaque terme
    //HashMap<String,Float> total_score_by_term; // le score cumulé maximum de chaque terme
    
    int maxcoord;             // le nombre total de termes possibles (sans abbréviations et ngram)
    
    // TODO: break out bool anyProhibited, int
    // numRequiredMatched; then we can remove 32 limit on
    // required clauses
    int bits;                // used for bool constraints
    int coord;               // count of terms in score
    int hits;                // count of hits without frequencies
    Bucket next;             // next valid bucket
    
    public Bucket(int maxTokens)
    {
        //score_by_term = new HashMap<String,Float>();
        //total_score_by_term = new HashMap<String,Float>();
        //analyzedTypes = new HashMap<String,Boolean>();
        score_by_term = new float[termIndex.size()];
        total_score_by_term = new float[termIndex.size()];
        analyzedFields = new boolean[termIndex.size()];
        
        requestTokenFrequencies = new int[maxTokens];
        score_by_subquery = new float[maxTokens*termIndex.size()];
        token_by_subquery = new int[maxTokens*termIndex.size()];
        term_by_subquery = new String[maxTokens*termIndex.size()];
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
    public JDONREFv3TermScorer scorer;
    // TODO: re-enable this if BQ ever sends us required clauses
    //public boolean required = false;
    public boolean prohibited;
    public Collector collector;
    public SubScorer next;

    public SubScorer(Scorer scorer, boolean required, boolean prohibited,
        Collector collector, SubScorer next) {
        assert(scorer instanceof JDONREFv3TermScorer);
      if (required) {
        throw new IllegalArgumentException("this scorer cannot handle required=true");
      }
      this.scorer = (JDONREFv3TermScorer) scorer;
      // TODO: re-enable this if BQ ever sends us required clauses
      //this.required = required;
      this.prohibited = prohibited;
      this.collector = collector;
      this.next = next;
    }
  }
  
  private SubScorer scorers = null; // for collect mode
  private BucketTable bucketTable;
  private int end;
  private Bucket current;
  // Any time a prohibited clause matches we set bit 0:
  private static final int PROHIBITED_MASK = 1;
  
  protected int maxCoord;
  
  protected int mode;
  protected int debugDoc;
  
  protected int doc = -1;
  
  protected JDONREFv3ESWeight protectedWeight;
  
  protected HashMap<String,Integer> countByType;
  
  protected JDONREFv3TermScorer[] subScorers = null; // for nextDoc & advance mode
  protected int numScorers;
  protected int nrMatchers = -1;
  protected double score = Float.NaN;
  
  public void increaseCountByType(String type)
  {
      Integer i = countByType.get(type);
      if (i==null) i = 1;
      else i++;
      countByType.put(type,i);
  }
  
  public boolean typeReachLimit(String type)
  {
      Integer i = countByType.get(type);
      boolean reached = i!=null && i>maxSizePerType;
      return reached;
  }
  
  public JDONREFv3Scorer(JDONREFv3ESWeight weight,
      List<JDONREFv3TermScorer> optionalScorers,
      int maxCoord, AtomicReaderContext context, Hashtable<String, Integer> termIndex,
      int mode, int debugDoc, int maxSizePerType) throws IOException {
    super(weight);
    
    this.protectedWeight = weight;
    this.context = context;
    this.maxCoord = maxCoord;
    this.termIndex = termIndex;
    this.mode = mode;
    this.debugDoc = debugDoc;
    this.maxSizePerType = maxSizePerType;
    this.countByType = new HashMap<String,Integer>();
    
    bucketTable = new BucketTable(context, weight.weights.size());

    if (optionalScorers != null && optionalScorers.size() > 0)
    {
       // nextDoc & advance mode
       subScorers = new JDONREFv3TermScorer[optionalScorers.size()];
       numScorers = subScorers.length;
       
       // collect mode 
       for(int i=optionalScorers.size()-1;i>=0;i--) // set scorers in order because they are reversed chained !
       {
          JDONREFv3TermScorer scorer = optionalScorers.get(i);
          
          //if (scorer.nextDoc() != NO_MORE_DOCS)
          {
            scorers = new SubScorer(scorer, false, false, bucketTable.newCollector(0), scorers);
          }
       }
       // and set subScorers in order !
       for(int i=0;i<optionalScorers.size();i++)
       {
           JDONREFv3TermScorer scorer = optionalScorers.get(i);
           scorer.setParentScorer(this);
           subScorers[i] = scorer;
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
    JDONREFv3TermScorer scorer = subScorers[root];
    int doc = scorer.docID();
    int i = root;
    while (i <= (numScorers >> 1) - 1) {
      int lchild = (i << 1) + 1;
      JDONREFv3TermScorer lscorer = subScorers[lchild];
      int ldoc = lscorer.docID();
      int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
      JDONREFv3TermScorer rscorer = null;
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
  
  /**
   * Prérequis : analyzeOrder(bucket, term);
   *             analyzeCodeBeforeAdress(bucket,term);
   *             analyzeAdressNumber(bucket, term);
   * @param bucket
   */
  public void calculateMalus(Bucket bucket)
  {
      String type = getType(bucket);

      boolean debug = false;
      if (debugDoc!=-1 && debugDoc==bucket.doc)
      {
          debug = true;
      }
      
      if (type.equals("poizon"))
      {
          if (!(bucket.score_by_term[termIndex.get("ligne1")]>0 || bucket.score_by_term[termIndex.get("ligne4")]>0))
          {
              if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calculate malus for doc "+bucket.doc+" : poizon but ligne1 & 4 is not present");
              bucket.malus = 0.0f;
              return;
          }
      }
      else if (type.equals("voie") || type.equals("adresse"))
      {
          if (!(bucket.score_by_term[termIndex.get("ligne4")]>0 && 
                (bucket.score_by_term[termIndex.get("codes")]>0 || bucket.score_by_term[termIndex.get("commune")]>0) ))
          {
              if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calculate malus for doc "+bucket.doc+" : voie or adress but ligne4 & (commune || codes) is not present");
              bucket.malus = 0.0f;
              return;
          }
      }
      else if (type.equals("commune"))
      {
          if (!(bucket.score_by_term[termIndex.get("codes")]>0 || bucket.score_by_term[termIndex.get("commune")]>0))
          {
              if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calculate malus for doc "+bucket.doc+" : commune but codes || commune is not present");
              bucket.malus = 0.0f;
              return;
          }
      }
      else if (type.equals("departement"))
      {
          if (!(bucket.score_by_term[termIndex.get("codes")]>0))
          {
              if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calculate malus for doc "+bucket.doc+" : departement but codes is not present");

              bucket.malus = 0.0f;
              return;
          }
      }
      else if (type.equals("pays"))
      {
          if (!(bucket.score_by_term[termIndex.get("ligne7")]>0)) // TODO : || bucket.score_by_term[termIndex.get("code_pays")]>0 lorsqu'il sera supporté
          {
              if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calculate malus for doc "+bucket.doc+" : pays but ligne7 is not present");

              bucket.malus = 0.0f;
              return;
          }
      }
      bucket.isOfTypeAdress = isOfTypeAdresse(bucket);
      float malus = 1.0f;
      if (bucket.wrongOrder) {
          malus *= ORDERMALUS;
      }
      if (bucket.isOfTypeAdress) {
          if (!bucket.adressNumberPresent) {
             if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calculate malus for doc "+bucket.doc+" : adresse but number is not present");

              malus *= NUMBERMALUS;
          }
      }
      if (!bucket.adressNumberPresent) {
          if (bucket.isThereCodeBeforeAdress) {
              malus *= ORDERMALUS;
          }
      }
      
      bucket.malus = malus;
  }
  
  public float malus(ArrayList<JDONREFv3TermScorer> subscorers,Bucket bucket) throws IOException
  {
      JDONREFv3TermScorer scorer = null;
      for(int i=0;i<subscorers.size();i++)
      {
          scorer = subscorers.get(i);
          
          collectBucket(bucket, scorer);
      }
      
      getSumScore(bucket); // prérequis pour calculateMalus
      calculateMalus(bucket);
      
      return bucket.malus;
  }
  
  public float malus(ArrayList<JDONREFv3TermScorer> subscorers,int doc) throws IOException
  {
      Document d = this.context.reader().document(doc);
      
      Bucket bucket = new Bucket(this.maxCoord);
      bucket.d = d;
      bucket.doc = doc;
      
      return malus(subscorers,bucket);
  }
  
  public float totalScore(ArrayList<JDONREFv3TermScorer> subscorers,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      JDONREFv3TermScorer scorer = null;
      for(int i=0;i<subscorers.size();i++)
      {
          scorer = subscorers.get(i);
          Term term = ((JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery()).getTerm();
            
          bucket.score_by_term[termIndex.get(term.field())] = 1.0f; // 1.0f means existence, does not care about value
      }
      
      setTotalScore(scorer,bucket);
      float totalScore = getSumTotalScore(bucket);
      
      return totalScore;
  }
  
  public float totalScore(ArrayList<JDONREFv3TermScorer> subscorers,int doc) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      Document d = this.context.reader().document(doc);
      
      Bucket bucket = new Bucket(this.maxCoord);
      bucket.d = d;
      bucket.doc = doc;
      
      return totalScore(subscorers,bucket);
  }
  
  public int maxCoord() throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      return ((JDONREFv3Query)this.getWeight().getQuery()).getNumTokens();
  }
  
  // firstDocID is ignored since nextDoc() initializes 'current'
  @Override
  public boolean score(Collector collector, int max, int firstDocID) throws IOException
  {
    // Make sure it's only BooleanScorer that calls us:
    assert firstDocID == -1;
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
  }
  
  @Override
  public int advance(int target) throws IOException {
    assert doc != NO_MORE_DOCS;
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==target)
    {
        debug = true;
    }
    if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" advance to "+target);
    while(true) {
      if (subScorers[0].advance(target) != NO_MORE_DOCS) {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" document "+subScorers[0].docID()+" trouvé dans "+((JDONREFv3TermQuery)subScorers[0].weight.getQuery()).getTerm().field());
          
        heapAdjust(0);
      } else {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" document "+target+" non trouvé dans "+((JDONREFv3TermQuery)subScorers[0].weight.getQuery()).getTerm().field());
        heapRemoveRoot();
        if (numScorers == 0) {
          return doc = NO_MORE_DOCS;
        }
      }
      if (subScorers[0].docID() >= target) {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" calcul du score pour  "+subScorers[0].docID());
        afterNext();
        
        //if (score!=0.0f || doc==NO_MORE_DOCS)
            return doc;
      }
    }
  }
  
  protected void afterNext() throws IOException {
      final JDONREFv3TermScorer sub = subScorers[0];
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
          b.d = this.context.reader().document(doc);
          
          String type = getType(b); // they have already been check by TermScorer nextDoc
          increaseCountByType(type); // 0n Top Scorers only, this is the key

          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Calcul du score pour le document "+doc+" scorer "+0);
          collectBucket(b, subScorers[0]);
          nrMatchers = 1;
          countMatches(b, 1);
          countMatches(b, 2);

          try {
              makeFinalScore(scorers.scorer, b); // peu importe le scorer choisi, seule la valeur de queryNorm est utilisée (la même pour tous les scorer).

          } catch (Exception ex) {
              throw new IOException(ex);
          }

          // malus pour les éléments non trouvés.
          JDONREFv3Query query = (JDONREFv3Query) this.protectedWeight.getQuery();
          float maxCoord = (query).getNumTokens();
          score = b.score * Math.pow(b.coord / maxCoord, 3);

          // arrondi le résultat à 10^-2
          score = (float) Math.ceil(100 * score) / 100.0f;
          if (score > 200) {
              score = 200; // maximum
          }
          
          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Calcul du score final pour le document "+doc+" score: "+score);
      }
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
          return doc = NO_MORE_DOCS;
        }
      }
      if (subScorers[0].docID() != doc) {
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+"Nouveau document trouvé, préparation.");
        afterNext();
        
        //if (score!=0.0f || doc == NO_MORE_DOCS)
            return doc;
      }
    }
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
  public void score(Collector collector) throws IOException {
    score(collector, Integer.MAX_VALUE, -1);
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