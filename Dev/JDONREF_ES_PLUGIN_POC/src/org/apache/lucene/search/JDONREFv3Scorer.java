package org.apache.lucene.search;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.JDONREFv3TermContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.JDONREFv3Query.JDONREFv3ESWeight;
import org.apache.lucene.search.JDONREFv3TermQuery.TermWeight;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.index.similarity.SimilarityService;

/**
 *
 * @author Julien
 */
public class JDONREFv3Scorer extends Scorer {
  
    boolean DEBUG = false;
    
  public final static float ORDERMALUS = 0.5f;
  public final static float NUMBERMALUS = 0;
  
  public final static float NOTE_LIGNE4 = 50;
  public final static float NOTE_CODEPOSTAL = 50;
  public final static float NOTE_COMMUNE = 50;
  public final static float NOTE_LIGNE7 = 50;
  
  protected AtomicReaderContext context;
  
  protected Hashtable<String, Integer> termIndex;
  
    /**
     * Retourne vrai si le poids de tous les champs du terme doivent être cumulés.
     * Retourne faux si seul le poids le plus élevé de tous les champs du terme doit être pris en compte.
     * @param term
     * @return
     */
    public boolean cumuler(String term)
    {
        if (term.equals("codes")) return false;
        
        return true;
    }
    
    // DefaultSimilarity only
    public float score(JDONREFv3TermScorer scorer,Bucket bucket, Term term) throws IOException
    {
        int doc = bucket.doc;
        long start = Calendar.getInstance().getTimeInMillis();
        
        // DefaultSimilarity only
        TermWeight weight = (TermWeight) scorer.getWeight();
        JDONREFv3TermQuery query = (JDONREFv3TermQuery) weight.getQuery();
        IndexSearcher searcher = ((TermWeight)scorer.weight).getSearcher();
        
        String field = term.field();
        CollectionStatistics collectionStats = searcher.collectionStatistics(field);
        JDONREFv3TermContext termStates = JDONREFv3TermContext.build(searcher.getTopReaderContext(), term);
        TermStatistics termStats = searcher.termStatistics(term, termStates.getContext());
        float value = (float)(Math.log(collectionStats.maxDoc()/(double)(termStats.docFreq()+1)) + 1.0);
        
        AtomicReader atomicreader = context.reader();
        DocsEnum docsEnum = atomicreader.termDocsEnum(term);
        docsEnum.advance(doc);
        float freq;
        
        if (cumuler(field))
            freq = docsEnum.freq();
        else
            freq = 1;
        //float freq = collectionStats.sumTotalTermFreq();
        
        NumericDocValues numdocvalues = atomicreader.getNormValues(field);
        long normvalues = numdocvalues.get(doc);
        float normvalue = ((DefaultSimilarity)searcher.getDefaultSimilarity()).decodeNormValue(normvalues);
        float newscore = (float)Math.sqrt(freq) * normvalue        // propre au document
                         * query.getBoost() * weight.getQueryNorm() * weight.getTopLevelBoost()  // propre à la requête
	                 * value * value ;
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        
        if (debug)
        {
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " ");
        
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " docFreq:"+termStats.docFreq());
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " maxDoc:"+collectionStats.maxDoc());
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " queryNorm:"+weight.getQueryNorm());
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " fieldNorm:"+normvalue);
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " topLevelBoost:"+weight.getTopLevelBoost());
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " queryBoost:"+query.getBoost());
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " idf:"+value);
            System.out.println("Thread "+Thread.currentThread().getName()+" doc "+bucket.doc+ " tf:"+Math.sqrt(freq));

            System.out.println("Thread "+Thread.currentThread().getName()+" Calcul du Score for Term :"+term.field()+"="+term.text()+" for doc :"+doc+" = "+newscore);
            long end = Calendar.getInstance().getTimeInMillis();
            System.out.println("Thread "+Thread.currentThread().getName()+" Calcul du Score for Term :"+term.field()+"="+term.text()+" for doc :"+doc+" took "+(end-start)+" ms");
        }
        
        return newscore;
    }
  
    /** Calcule le score total théorique que peut rapporter un terme d'un document
     *  Les termes de même offset ne sont comptés qu'une seule fois : le plus long ou celui de score le plus élevé.
     * 
     *  Le nombre de terme est aussi ajouté au bucket (qui tient compte des synonymes et nGram).
     */
    public float totalScore(JDONREFv3TermScorer scorer,String term, Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        
        AtomicReader atomicreader = context.reader();
        Terms terms = atomicreader.getTermVector(bucket.doc,term);
        if (terms==null) return 0.0f; // le terme n'existe pas dans ce document.
        
        if (debug)
        System.out.println("Thread "+Thread.currentThread().getName()+" totalScore for Term :"+term+", doc :"+bucket.doc);
        
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
            
            //System.out.println("Check "+t.text()+" at "+offset);
            
            float score = score(scorer,bucket,t);
            
            Float lastscore = scoresPosition.get(offset);
            Integer lastlength = lengthPosition.get(offset);
            
            if (lastscore!=null)
            {
                if (lastlength < length)// || lastscore < score) ... le plus long n'est pas toujours le plus pertinent !?
                {
                    if (cumuler(term))
                    {
                        totalScore -= lastscore;
                        totalScore += score;
                    }
                    else
                    {
                        totalScore = Math.max(score,totalScore);
                    }
                    //System.out.println("totalScore now :"+totalScore);
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
                //System.out.println("totalScore now :"+totalScore);
            }
            
            i++;
        }
        
        if (debug)
        System.out.println("Thread "+Thread.currentThread().getName()+" totalScore for Term :"+term+", doc :"+bucket.doc+" = "+totalScore);
        
        return totalScore;
    }
    
    boolean foobar = false;
    
    public void setTotalScore(JDONREFv3TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        // le poids de la commune n'est pas prise en compte si seul un code est présent
        if (!(bucket.score_by_term[termIndex.get("codes")]>0 && bucket.score_by_term[termIndex.get("commune")]==0))
        {
            bucket.total_score_by_term[termIndex.get("commune")] = this.totalScore(scorer,"commune", bucket);
        }
        
        // le poids des codes n'est pas pris en compte si seul une commune est présente
        if (!(bucket.score_by_term[termIndex.get("commune")]>0 && bucket.score_by_term[termIndex.get("codes")]==0))
        {
            bucket.total_score_by_term[termIndex.get("codes")] = this.totalScore(scorer,"codes", bucket);
        }
        
        bucket.total_score_by_term[termIndex.get("ligne4")] = this.totalScore(scorer,"ligne4", bucket);
        
        if (bucket.score_by_term[termIndex.get("ligne7")]>0)
            bucket.total_score_by_term[termIndex.get("ligne7")] = this.totalScore(scorer,"ligne7", bucket);
        if (bucket.score_by_term[termIndex.get("code_pays")]>0)
            bucket.total_score_by_term[termIndex.get("code_pays")] = this.totalScore(scorer,"code_pays", bucket);
        
        // le poids de la commune n'est pas prise en compte si seul un code est présent
        /*if (bucket.score_by_term.get("codes")!=null && bucket.score_by_term.get("commune")==null)
            bucket.total_score_by_term.put("commune",null);
        else
            bucket.total_score_by_term.put("commune", this.totalScore(scorer,"commune", bucket));*/
        
        // le poids des codes n'est pas pris en compte si seul une commune est présente
        /*if (bucket.score_by_term.get("commune")!=null && bucket.score_by_term.get("codes")==null)
            bucket.total_score_by_term.put("codes",null);
        else
            bucket.total_score_by_term.put("codes", this.totalScore(scorer,"codes", bucket));*/
        
        //bucket.total_score_by_term.put("ligne4", this.totalScore(scorer,"ligne4", bucket));
        
        // le pays n'est pris en compte que si un pays est recherché.
        /*if (bucket.score_by_term.get("ligne7")!=null)
            bucket.total_score_by_term.put("ligne7", this.totalScore(scorer,"ligne7", bucket));
        if (bucket.score_by_term.get("code_pays")!=null)
            bucket.total_score_by_term.put("code_pays", this.totalScore(scorer,"code_pays", bucket));*/
    }
    
    public float getSumTotalScore(Bucket bucket)
    {
        float total = 0.0f;
        
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        
        for(int i=0;i<bucket.total_score_by_term.length;i++)
        {
            float subscore = bucket.total_score_by_term[i];
            if (debug)
            {
                 System.out.println("Thread "+Thread.currentThread().getName()+" SumTotalScore for doc :"+bucket.doc+" term "+i+": "+subscore);
            }
            total += subscore;
        }
        
          /*Iterator<String> terms = bucket.total_score_by_term.keySet().iterator();
          while (terms.hasNext()) {
              try {
                  String term = terms.next();

                  Float subscore = bucket.total_score_by_term.get(term);
                  if (subscore!=null)
                  {
                    total += subscore;
                  
                    if (debug)
                    {
                        System.out.println("Thread "+Thread.currentThread().getName()+" SumTotalScore for doc :"+bucket.doc+" "+term+": "+subscore);
                    }
                  }

              } catch (IllegalArgumentException ex) {
                  Logger.getLogger(JDONREFv3Scorer.class.getName()).log(Level.SEVERE, null, ex);
                  total = 0;
              }
          }*/
          
          if (debug)
          {
               System.out.println("Thread "+Thread.currentThread().getName()+" SumTotalScore for doc :"+bucket.doc+" total: "+total);
          }
          
          return total;
    }
    
    public float getSumScore(Bucket bucket)
    {
        float sum = 0.0f;
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        
        if (debug)
        {
            System.out.println("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc);
        }
        
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
                    float lastscore = bucket.score_by_term[termIndex.get(term)];

                    if (cumuler(term)) {
                        sum += score;
                        score += lastscore;
                    } else {
                        score = Math.max(lastscore, score);
                        sum += score;
                        sum -= lastscore;
                    }
                    bucket.score_by_term[termIndex.get(term)] = score;
                }
            }
        }
        /*
        Iterator<String> terms = bucket.score_by_term.keySet().iterator();
        while (terms.hasNext()) {
            try {
                String term = terms.next();

                float subscore = bucket.score_by_term.get(term);
                sum += subscore;

                if (debug) {
                    System.out.println(term + ": " + subscore);
                }

            } catch (IllegalArgumentException ex) {
                Logger.getLogger(JDONREFv3Scorer.class.getName()).log(Level.SEVERE, null, ex);
                sum = 0;
            }
        }*/
        
        if (debug) {
            System.out.println("Thread "+Thread.currentThread().getName()+" SumScore for doc :"+bucket.doc+" total: " + sum);
        }
        
        return sum;
    }
    
    public boolean isOfTypeAdresse(Bucket bucket)
    {
        String[] values = bucket.d.getValues("type");
        if (values==null || values.length==0) return false;
        return Arrays.binarySearch(values, "adresse")>=0;
    }
    
    public void makeFinalScore(JDONREFv3TermScorer scorer,Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        //System.out.println("Thread "+Thread.currentThread().getName()+" makeFinalScore doc "+bucket.doc);
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        
          if (debug)
          {
            System.out.println("Thread "+Thread.currentThread().getName()+" "+bucket.d.get("fullName")+" doc "+bucket.doc);
          }
          
          // Score relatif
          bucket.score = getSumScore(bucket);
          setTotalScore(scorer,bucket);
          float totalScore = getSumTotalScore(bucket);
          bucket.score /= totalScore;
          
          if (debug)
          {
            System.out.println("Thread "+Thread.currentThread().getName()+" "+bucket.d.get("fullName")+" doc "+bucket.doc+" Score brut :"+bucket.score);
          }
          
          // ramené à 200.
          bucket.score *= 200;
          if (bucket.score>200) bucket.score = 200; // maximum
          
          if (debug)
          {
            System.out.println("Thread "+Thread.currentThread().getName()+" "+bucket.d.get("fullName")+" doc "+bucket.doc+" Score sur 200 :"+bucket.score);
          }
          
          // Malus
          calculateMalus(bucket);
          if (debug)
          {
            System.out.println("Thread "+Thread.currentThread().getName()+" "+bucket.d.get("fullName")+" doc "+bucket.doc+" Score avant malus :"+bucket.score);
            System.out.println("Thread "+Thread.currentThread().getName()+" "+bucket.d.get("fullName")+" doc "+bucket.doc+" malus :"+bucket.malus);
            System.out.println("Thread "+Thread.currentThread().getName()+" "+bucket.d.get("fullName")+" doc "+bucket.doc+" Score après malus :"+bucket.score*bucket.malus);
          }
          bucket.score *= bucket.malus;
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
            if (isNumber(term.text()))
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
        if (bucket.currentAnalyzedType.equals("codes"))
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
        if (bucket.currentAnalyzedType==null)
        {
            bucket.currentAnalyzedType = term.field();
            bucket.analyzedTypes[termIndex.get(term.field())] = true;
            //bucket.analyzedTypes.put(term.field(),true);
        }
        else
        {
            if (bucket.currentAnalyzedType.equals(term.field()))
            {
                // do nothing, right order
            }
            else if (!bucket.analyzedTypes[termIndex.get(term.field())])
            //else if (bucket.analyzedTypes.get(term.field())==null)
            {
                // new field discovered !
                bucket.currentAnalyzedType = term.field();
                bucket.analyzedTypes[termIndex.get(term.field())] = true;
                //bucket.analyzedTypes.put(term.field(),true);
            }
            else
            {
                // wrong order
                bucket.wrongOrder = true;
            }
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
      
      if (bucket.doc != doc) {                    // invalid bucket
        bucket.doc = doc;                         // set doc
        
        //bucket.d = this.scorer.getSearcher().doc(doc); // which one ?
        bucket.d = this.context.reader().document(doc);
        
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        
        JDONREFv3TermQuery query = (JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery();
        Term term = query.getTerm();
        int termIndex = query.getIndex();
        int termQueryIndex = query.getQueryIndex();
        
        // term frequencies
        bucket.requestTokenFrequencies[termIndex]++;
        
        if (debug)
        System.out.println("Thread "+Thread.currentThread().getName()+" collect score for Term :"+term+", termQueryIndex:"+termQueryIndex+" doc :"+bucket.doc);
        
        //float score = scorer.score();
        float score = score(scorer,bucket,term);
        analyzeOrder(bucket, term);
        analyzeCodeBeforeAdress(bucket,term);
        analyzeAdressNumber(bucket, term);
        
        // premier score (pas de cumul)
        //bucket.score_by_term.put(term.field(),score);
        bucket.score_by_subquery[termQueryIndex] = score;
        bucket.token_by_subquery[termQueryIndex] = termIndex;
        bucket.term_by_subquery[termQueryIndex] = term.field();
        
        bucket.bits = mask;                       // initialize mask
        bucket.coord = 1;                         // initialize coord

        bucket.next = table.first;                // push onto valid list
        table.first = bucket;
      } else {                                    // valid bucket
        
        boolean debug = false;
        if (bucket.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
        {
            debug = DEBUG;
        }
        // second score (cumul éventuel)
        JDONREFv3TermQuery query = (JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery();
        Term term = query.getTerm();
        int termIndex = query.getIndex();
        int termQueryIndex = query.getQueryIndex();
        
        // term frequencies
        bucket.requestTokenFrequencies[termIndex]++;
        
        if (debug)
        System.out.println("Thread "+Thread.currentThread().getName()+" collect score for Term :"+term+", termQueryIndex:"+termQueryIndex+", doc :"+bucket.doc);
        
        //float score = scorer.score();
        float score = score(scorer,bucket,term);
        analyzeOrder(bucket, term);
        analyzeCodeBeforeAdress(bucket,term);
        analyzeAdressNumber(bucket, term);
        
        bucket.score_by_subquery[termQueryIndex] = score;
        bucket.token_by_subquery[termQueryIndex] = termIndex;
        bucket.term_by_subquery[termQueryIndex] = term.field();
        
        /*Float lastscore;
        if (cumuler(term.field()))
        {
            lastscore = ((lastscore=bucket.score_by_term.get(term.field()))==null)?0.0f:lastscore;
            //System.out.println("Ajoute "+term.field()+"="+term.text()+" : "+score+" à "+lastscore);
            score += lastscore;
        }
        else
        {
            score = Math.max(score,((lastscore=bucket.score_by_term.get(term.field()))==null)?0.0f:lastscore);
            //System.out.println("Remplace "+term.field()+"="+term.text()+" : "+lastscore+" par "+score);
        }
        bucket.score_by_term.put(term.field(),score);*/
        
        bucket.bits |= mask;                      // add bits in mask
        bucket.coord++;                           // increment coord
      }
      
      if (scorer.isLast())
      {
          try
          {
            makeFinalScore(scorer,bucket);
          }
          catch(Exception ex)
          {
              Logger.getLogger(JDONREFv3Scorer.class.getName()).log(Level.SEVERE, null, ex);
              bucket.score = 0;
          }
      }
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
    
    String currentAnalyzedType;            // current Type beeing analyzed
    //HashMap<String,Boolean> analyzedTypes; // all types already been analyzed in order
    boolean[] analyzedTypes; // all types already been analyzed in order
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
    Bucket next;             // next valid bucket
    
    public Bucket(int maxTokens)
    {
        //score_by_term = new HashMap<String,Float>();
        //total_score_by_term = new HashMap<String,Float>();
        //analyzedTypes = new HashMap<String,Boolean>();
        score_by_term = new float[termIndex.size()];
        total_score_by_term = new float[termIndex.size()];
        analyzedTypes = new boolean[termIndex.size()];
        
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
  
  private SubScorer scorers = null;
  private BucketTable bucketTable;
  //private float[] coordFactors; // no need for that
  
  // TODO: re-enable this if BQ ever sends us required clauses
  //private int requiredMask = 0;
  private final int minNrShouldMatch;
  private int end;
  private Bucket current;
  // Any time a prohibited clause matches we set bit 0:
  private static final int PROHIBITED_MASK = 1;
  
  protected int maxCoord;
  
  protected JDONREFv3ESWeight protectedWeight;
  
  public JDONREFv3Scorer(JDONREFv3ESWeight weight, boolean disableCoord, int minNrShouldMatch,
      List<Scorer> optionalScorers, List<Scorer> prohibitedScorers,
      int maxCoord, AtomicReaderContext context, Hashtable<String, Integer> termIndex) throws IOException {
    super(weight);
    
    this.protectedWeight = weight;
    this.minNrShouldMatch = minNrShouldMatch;
    this.context = context;
    this.maxCoord = maxCoord;
    this.termIndex = termIndex;
    
    bucketTable = new BucketTable(context, maxCoord/4);

    if (optionalScorers != null && optionalScorers.size() > 0)
    {
       for(int i=optionalScorers.size()-1;i>=0;i--) // set scorers in order !
       {
          JDONREFv3TermScorer scorer = (JDONREFv3TermScorer) optionalScorers.get(i);
          if (i==optionalScorers.size()-1)
              scorer.setIsLast(); // some scorer might have no match, we (I) only know here which ones !
          
          if (scorer.nextDoc() != NO_MORE_DOCS)
          {
            scorers = new SubScorer(scorer, false, false, bucketTable.newCollector(0), scorers);
          }
       }
    }
    
    /* // These are not usefull
    if (prohibitedScorers != null && prohibitedScorers.size() > 0) {
      for (Scorer scorer : prohibitedScorers) {
        if (scorer.nextDoc() != NO_MORE_DOCS) {
          scorers = new SubScorer(scorer, false, true, bucketTable.newCollector(PROHIBITED_MASK), scorers);
        }
      }
    }

    coordFactors = new float[optionalScorers.size() + 1];
    for (int i = 0; i < coordFactors.length; i++) {
      coordFactors[i] = disableCoord ? 1.0f : weight.coord(i, maxCoord); 
    }*/
  }

  // firstDocID is ignored since nextDoc() initializes 'current'
  @Override
  public boolean score(Collector collector, int max, int firstDocID) throws IOException {
    // Make sure it's only BooleanScorer that calls us:
    assert firstDocID == -1;
    boolean more;
    Bucket tmp;
    BucketScorer bs = new BucketScorer(protectedWeight);

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
          
          if (current.coord >= minNrShouldMatch) {
            boolean debug = false;
            if (current.d.get("fullName").equals("130 RUE REMY DUHEM 59500 DOUAI FRANCE"))
            {
                debug = DEBUG;
            }
            
            // malus pour les éléments non trouvés.
            float maxCoord = ((JDONREFv3Query)this.protectedWeight.getQuery()).getNumTokens();
            bs.score = current.score * Math.pow(current.coord / maxCoord,2) ;
            
            if (debug)
            {
                System.out.println("Thread "+Thread.currentThread().getName()+" Collect doc "+current.doc+" "+current.d.get("fullName")+" coord: ("+current.coord+"/"+maxCoord+")^2 = "+Math.pow(current.coord / maxCoord,2));
            }
            
            if (debug)
            {
                System.out.println("Thread "+Thread.currentThread().getName()+" Collect doc "+current.doc+" "+current.d.get("fullName")+" final Score: "+bs.score);
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

    return false;
  }
  
  /**
   * Prérequis : analyzeOrder(bucket, term);
   *             analyzeCodeBeforeAdress(bucket,term);
   *             analyzeAdressNumber(bucket, term);
   * @param bucket
   */
  public void calculateMalus(Bucket bucket)
  {
      bucket.isOfTypeAdress = isOfTypeAdresse(bucket);
      float malus = 1.0f;
      if (bucket.wrongOrder) {
          malus *= ORDERMALUS;
      }
      if (bucket.isOfTypeAdress) {
          if (!bucket.adressNumberPresent) {
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
          
          Term term = ((JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery()).getTerm();

          //float score = scorer.score();
          score(scorer, bucket, term);
          analyzeOrder(bucket, term);
          analyzeCodeBeforeAdress(bucket, term);
          analyzeAdressNumber(bucket, term);
      }
      
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
  
  public int coord(ArrayList<JDONREFv3TermScorer> subscorers)
  {
      return subscorers.size();
  }
  
  public int maxCoord() throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
      return ((JDONREFv3Query)this.getWeight().getQuery()).getNumTokens();
  }
  
  @Override
  public int advance(int target) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int docID() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int nextDoc() {
    throw new UnsupportedOperationException();
  }

  @Override
  public float score() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int freq() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long cost() {
    return Integer.MAX_VALUE;
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
    throw new UnsupportedOperationException();
  }
}