package org.apache.lucene.search;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
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
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.index.similarity.SimilarityService;

/**
 *
 * @author Julien
 */
public class JDONREFv3Scorer extends Scorer {
  
  public final static float ORDERMALUS = 0.5f;
  public final static float NUMBERMALUS = 0;
  
  public final static float NOTE_LIGNE4 = 50;
  public final static float NOTE_CODEPOSTAL = 50;
  public final static float NOTE_COMMUNE = 50;
  public final static float NOTE_LIGNE7 = 50;
  
  protected AtomicReaderContext context;
    
  private static final class JDONREFv3ScorerCollector extends Collector {
    private BucketTable bucketTable;
    private int mask;
    private JDONREFv3TermScorer scorer;
    private AtomicReaderContext context;
    
    public JDONREFv3ScorerCollector(int mask, BucketTable bucketTable, AtomicReaderContext context) {
      this.mask = mask;
      this.bucketTable = bucketTable;
      this.context = context;
    }
    
    // DefaultSimilarity only
    public float score(final int doc, Term term) throws IOException
    {
        // DefaultSimilarity only
        TermWeight weight = (TermWeight) scorer.getWeight();
        JDONREFv3TermQuery query = (JDONREFv3TermQuery) weight.getQuery();
        String field = term.field();
        IndexSearcher searcher = ((TermWeight)scorer.weight).getSearcher();
        CollectionStatistics collectionStats = searcher.collectionStatistics(term.field());
        JDONREFv3TermContext termStates = JDONREFv3TermContext.build(searcher.getTopReaderContext(), term);
        TermStatistics termStats = searcher.termStatistics(term, termStates.getContext());
        float value = (float)(Math.log(collectionStats.maxDoc()/(double)(termStats.docFreq()+1)) + 1.0);
        
        float freq = scorer.freq();
        
        AtomicReader atomicreader = context.reader();
        NumericDocValues numdocvalues = atomicreader.getNormValues(field);
        long normvalues = numdocvalues.get(doc);
        float normvalue = ((DefaultSimilarity)searcher.getDefaultSimilarity()).decodeNormValue(normvalues);
        float newscore = (float)Math.sqrt(freq) * normvalue        // propre au document
                         * query.getBoost() * weight.getQueryNorm() * weight.getTopLevelBoost()  // propre à la requête
	                 * value * value ;
        
        return newscore;
    }
    
    /** Calcule le score total théorique que peut rapporter un document
     *  Les termes de même offset ne sont comptés qu'une seule fois : le plus long ou celui de score le plus élevé.
     * 
     *  Le nombre de terme est aussi ajouté au bucket.
     */
    public float totalScore(String term, Bucket bucket) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        AtomicReader atomicreader = context.reader();
        Terms terms = atomicreader.getTermVector(bucket.doc,term);
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef current;
        
        Field field = termsEnum.getClass().getDeclaredField("startOffsets");
        field.setAccessible(true);
        int[] startOffsets = (int[]) field.get(termsEnum); // no security manager for now !
        
        bucket.maxcoord = startOffsets.length;
        
        HashMap<Integer,Float> scoresPosition = new HashMap<Integer,Float>();
        HashMap<Integer,Integer> lengthPosition = new HashMap<Integer,Integer>();
        
        float totalScore = 0.0f;
        int i = 0;
        while(!((current = termsEnum.next())==null))
        {
            Term t = new Term(term,current);
            int length = t.text().length();
            int offset = startOffsets[i];
            
            float score = score(bucket.doc,t);
            
            Float lastscore = scoresPosition.get(offset);
            Integer lastlength = lengthPosition.get(offset);
            
            if (lastscore!=null)
            {
                if (lastlength < length)// || lastscore < score) ... le plus long n'est pas toujours le plus pertinent ! 
                {
                    totalScore -= lastscore;
                    totalScore += score;
                    scoresPosition.put(offset, score);
                    lengthPosition.put(offset, length);
                }
                // else do nothing
            }
            else
            {
                totalScore += score;
                scoresPosition.put(offset, score);
                lengthPosition.put(offset, length);
            }
            
            i++;
        }
        
        return totalScore;
    }
    
    /**
     * Gère les exceptions concernant le score :
     * 1. le pays et le code pays
     */
    public void exceptions(Bucket bucket)
    {
        if (bucket.score_by_term.get("ligne7")!=null && bucket.score_by_term.size()>1)
            bucket.score_by_term.remove("ligne7");
        if (bucket.score_by_term.get("code_pays")!=null && bucket.score_by_term.size()>1)
            bucket.score_by_term.remove("code_pays");
    }
    
    /**
     * Gère les exceptions concernant le score :
     * 1. le pays et le code pays
     * 2. le code postal, code insee, code arrondissement et code département
     */
    public float poids(Bucket bucket,String term)
    {
        if (term.equals("ligne7"))
        {
            if (bucket.score_by_term.get("code_pays")!=null) return 50/2;
        }
        else if (term.equals("code_pays"))
        {
            if (bucket.score_by_term.get("ligne7")!=null) return 50/2;
        }
        else if (term.equals("code_postal"))
        {
            int count = 1;
            if (bucket.score_by_term.get("code_departement")!=null) count++;
            if (bucket.score_by_term.get("code_arrondissement")!=null) count++;
            if (bucket.score_by_term.get("code_insee")!=null) count++;
            return 50/count;
        }
        else if (term.equals("code_departement"))
        {
            int count = 1;
            if (bucket.score_by_term.get("code_postal")!=null) count++;
            if (bucket.score_by_term.get("code_arrondissement")!=null) count++;
            if (bucket.score_by_term.get("code_insee")!=null) count++;
            return 50/count;
        }
        else if (term.equals("code_arrondissement"))
        {
            int count = 1;
            if (bucket.score_by_term.get("code_postal")!=null) count++;
            if (bucket.score_by_term.get("code_departement")!=null) count++;
            if (bucket.score_by_term.get("code_insee")!=null) count++;
            return 50/count;
        }
        else if (term.equals("code_insee"))
        {
            int count = 1;
            if (bucket.score_by_term.get("code_postal")!=null) count++;
            if (bucket.score_by_term.get("code_departement")!=null) count++;
            if (bucket.score_by_term.get("code_arrondissement")!=null) count++;
            return 50/count;
        }
        
        return 50;
    }
    
    boolean foobar = false;
    
    public void makeFinalScore(Bucket bucket) throws IOException
    {
        exceptions(bucket);
        
        String[] numero = bucket.d.getValues("numero");
        if (numero!=null && numero.length>0 && (numero[0].equals("130")))
            foobar = true;
        
          bucket.score = 0.0f;
          Iterator<String> terms = bucket.score_by_term.keySet().iterator();
          while (terms.hasNext()) {
              try {
                  String term = terms.next();
                  
                  float poids = poids(bucket,term);
                  float totalScore = this.totalScore(term, bucket); 

                  float value = bucket.score_by_term.get(term);
                  float subscore = poids * (value / totalScore);
                  if (subscore>poids) subscore = poids; // le terme le plus long prime.
                  
                  bucket.score += subscore;

              } catch (NoSuchFieldException ex) {
                  Logger.getLogger(JDONREFv3Scorer.class.getName()).log(Level.SEVERE, null, ex);
                  bucket.score = 0;
              } catch (IllegalArgumentException ex) {
                  Logger.getLogger(JDONREFv3Scorer.class.getName()).log(Level.SEVERE, null, ex);
                  bucket.score = 0;
              } catch (IllegalAccessException ex) {
                  Logger.getLogger(JDONREFv3Scorer.class.getName()).log(Level.SEVERE, null, ex);
                  bucket.score = 0;
              }
          }

          scorer.checker.next();

          int maxFields = scorer.checker.getMaxFields(bucket.d);
          
          // ramené à 200.
          bucket.score = (200  / (50 * maxFields)) * bucket.score;

          // avec un malus éventuel (ordre, présence d'un numéro devant une voie)
          bucket.score *= scorer.checker.malus();

          if (scorer.checker.isAdressType(bucket.d) && !scorer.checker.checkAdressNumberPresent()) {
              bucket.score *= JDONREFv3Scorer.NUMBERMALUS;
          }
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
        
        float score = scorer.score();
        scorer.check(bucket.d); // trace les catégories
        
        Term term = ((JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery()).getTerm();
        // premier score (pas de cumul)
        bucket.score_by_term.put(term.field(),score);
        
        bucket.bits = mask;                       // initialize mask
        bucket.coord = 1;                         // initialize coord

        bucket.next = table.first;                // push onto valid list
        table.first = bucket;
      } else {                                    // valid bucket
        float score = scorer.score();
        scorer.check(bucket.d); // trace les catégories
        
        // second score (cumul éventuel)
        Term term = ((JDONREFv3TermQuery) ((TermWeight) scorer.getWeight()).getQuery()).getTerm();
        
        Float lastscore;
        score += ((lastscore=bucket.score_by_term.get(term.field()))==null)?0.0f:lastscore;
        bucket.score_by_term.put(term.field(),score);
        
        bucket.bits |= mask;                      // add bits in mask
        bucket.coord++;                           // increment coord
      }
      
      if (scorer.isLast())
      {
            makeFinalScore(bucket);
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

  static final class Bucket {
    int doc = -1;             // tells if bucket is valid
    Document d = null;        // the original
    double score;             // incremental score
    double malus;             // malus final à appliquer
    boolean adressType;       // le document est une adresse
    boolean adressNumberPresent; // présence du numéro d'adresse ...
    
    HashMap<String,Float> score_by_term; // le score cumulé de chaque terme
    
    int maxcoord;             // le nombre total de termes possibles (sans abbréviations et ngram)
    
    // TODO: break out bool anyProhibited, int
    // numRequiredMatched; then we can remove 32 limit on
    // required clauses
    int bits;                // used for bool constraints
    int coord;               // count of terms in score
    Bucket next;             // next valid bucket
  }
  
  /** A simple hash table of document scores within a range. */
  static final class BucketTable {
    public static final int SIZE = 1 << 11;
    public static final int MASK = SIZE - 1;

    final Bucket[] buckets = new Bucket[SIZE];
    Bucket first = null;                          // head of valid list
    AtomicReaderContext context;
  
    public BucketTable(AtomicReaderContext context) {
      // Pre-fill to save the lazy init when collecting
      // each sub:
      for(int idx=0;idx<SIZE;idx++) {
        buckets[idx] = new Bucket();
        buckets[idx].score_by_term = new HashMap<String,Float>();
      }
      this.context = context;
    }

    public Collector newCollector(int mask) {
      return new JDONREFv3ScorerCollector(mask, this,context);
    }

    public int size() { return SIZE; }
  }
  
  /**
   * Ajouter toutes les catégories correspondant à un TermScorer avec add
   * puis utiliser check pour s'avoir s'il faut leur ajouter un malus
   * ne pas oublier next avant de passer au TermScorer suivant.
   */
  static public final class AdresseChecker
  {
      final static int NONE = 0;
      final static int NUMEROADRESSE = 1;
      final static int ADRESSE = 2;
      final static int VOIE = 3;
      final static int COMMUNE = 4;
      final static int CODE_DEPARTEMENT = 5;
      final static int CODE_ARRONDISSEMENT = 6;
      final static int CODE_POSTAL = 7;
      final static int CODE_INSEE = 8;
      final static int LIGNE7 = 9;
      
      int currentIndex = -1;
      
      public void setCurrentIndex(int currentIndex)
      {
          this.currentIndex = currentIndex;
      }
      
      public int getCurrentIndex()
      {
          return currentIndex;
      }
      
      public int getCategoryFromString(String category)
      {
          if (category.equals("ligne4") || category.equals("voie"))
          {
              return VOIE;
          }
          else if (category.equals("adresse"))
          {
              return ADRESSE;
          }
          else if (category.equals("commune"))
          {
              return COMMUNE;
          }
          else if (category.equals("code_postal"))
          {
              return CODE_POSTAL;
          }
          else if (category.equals("code_arrondissement"))
          {
              return CODE_ARRONDISSEMENT;
          }
          else if (category.equals("code_insee"))
          {
              return CODE_INSEE;
          }
          else if (category.equals("code_departement") || category.equals("departement"))
          {
              return CODE_DEPARTEMENT;
          }
          else if (category.equals("ligne7") || category.equals("pays"))
          {
              return LIGNE7;
          }
          return NONE;
      }
      
      public HashMap<Integer,Double> idfMax;
      
      public void addIdf(int category, double idf)
      {
          Double current = idfMax.get(category);
          if (current == null)
              current = idf;
          else
              current += idf;
          idfMax.put(category,current);
      }
      
      public ArrayList<HashMap<Integer,Boolean>> list;
      public HashMap<Integer,Boolean> current;
      
      public JDONREFv3ESWeight weight;
      
      public JDONREFv3ESWeight getWeight()
      {
          return weight;
      }
      
      public void setWeight(JDONREFv3ESWeight weight)
      {
          this.weight = weight;
      }
      
      public AdresseChecker()
      {
          list = new ArrayList<HashMap<Integer,Boolean>>();
          current = new HashMap<Integer,Boolean>();
          idfMax = new HashMap<Integer,Double>();
      }
      
      public boolean contains(Document d,String field,String value)
      {
          String[] docValues = d.getValues(field);
          
          for(int i=0;i<docValues.length;i++)
          {
              String docValue = docValues[i].toLowerCase();
              if (docValue.startsWith(value)) // autocompletion case
                  return true;
          }
          
          return false;
      }
      
      public int getType(Document d) throws IOException
      {
          String[] types = d.getValues("type");
          String type = types[0];
          
          return getCategoryFromString(type);
      }
      
      public int getMaxFields(Document d) throws IOException
      {
          int type = getType(d);
          
          switch(type) // pour les documents autres que le pays, le pays n'est pas pris en compte.
          {
              case ADRESSE: 
                  return 3;
              case COMMUNE:
                  return 2;
              case VOIE:
                  return 3;
              case CODE_DEPARTEMENT:
                  return 1;
              case LIGNE7:
                  return 1;
          }
          return 0;
      }
      
      public boolean isAdressType(Document d) throws IOException
      {
          String[] types = d.getValues("type");
          
          if (types[0].equals("adresse"))
          {
              return true;
          }
          
          return false;
      }
      
      protected boolean checkAdressNumberPresent()
      {
          for(int i=0;i<list.size();i++)
          {
              HashMap<Integer,Boolean> hashi = list.get(i);
              Set<Integer> keys = hashi.keySet();
              Iterator<Integer> ite = keys.iterator();

              while (ite.hasNext()) {
                  int category = ite.next();

                  if (category == NUMEROADRESSE) {
                      return true;
                  }
              }
          }
          return false;
      }
        
      /**
       * @return Faux si les conditions suivantes sont réunies :
       * 1. Une adresse est présente dans la proposition, sans avoir précisé son numéro d'adresse
       * 2. Un numéro du type code_arrondissement, code_postal, code_insee ou code_departement est présent devant l'adresse
       */
      protected boolean checkOtherNumberBeforeAdresse()
      {
          for(int i=1;i<list.size();i++)
          {
              HashMap<Integer,Boolean> hashi = list.get(i);
              Set<Integer> keys = hashi.keySet();
              Iterator<Integer> ite = keys.iterator();
          
              while (ite.hasNext()) {
                  int category = ite.next();

                  if (category == ADRESSE) {
                      HashMap<Integer, Boolean> hash = list.get(i - 1);

                      if (hash.get(NUMEROADRESSE) != null || hash.get(ADRESSE) != null) {
                          return false;
                      } else {
                          if (hash.get(CODE_ARRONDISSEMENT) == null) {
                              return true;
                          }
                          if (hash.get(CODE_DEPARTEMENT) == null) {
                              return true;
                          }
                          if (hash.get(CODE_INSEE) == null) {
                              return true;
                          }
                          if (hash.get(CODE_POSTAL) == null) {
                              return true;
                          }
                          return false;
                      }
                  }
              }
          }
          return false;
      }
      
      /**
       * 
       * @return Vrai si l'ordre des termes de chaque catégorie est respectée.
       */
      public boolean checkOrder()
      {
          for(int i=0;i<list.size();i++)
          {
              HashMap<Integer,Boolean> hash = list.get(i);
              Set<Integer> keys = hash.keySet();
              Iterator<Integer> ite = keys.iterator();
              
              while (ite.hasNext()) {
                  int category = ite.next();

                  if (list.size() == 0) {
                      return true;
                  }
                  boolean same = true;
                  boolean check = false;

                  if (category == NUMEROADRESSE) {
                      same = false; // numero is the first token from ligne4

                  }
                  for (int j = i-1; !check && j >= 0; j--) {
                      if (same) {
                          if (category == ADRESSE || category == NUMEROADRESSE) {
                              if (list.get(j).get(ADRESSE) == null && list.get(j).get(NUMEROADRESSE) == null) {
                                  same = false;
                              }
                          } else {
                              if (list.get(j).get(category) == null) {
                                  same = false;
                              }
                          }
                      } else {
                          if (category == ADRESSE || category == NUMEROADRESSE) {
                              if ((list.get(j).get(ADRESSE) != null || list.get(j).get(NUMEROADRESSE) != null) && list.get(j).size() == 1) {
                                  check = true;
                              }
                          } else {
                              if (list.get(j).get(category) != null && list.get(j).size() == 1) {
                                  check = true;
                              }
                          }
                      }
                  }
                  if (!same && check) {
                      return false;
                  }
              }
          }
          return true;
      }
      
      public float malus() throws IOException
      {
          float lmalus = 1.0f;
          boolean malusOrder = !checkOrder();
          if (malusOrder) {
              lmalus *= JDONREFv3Scorer.ORDERMALUS;
          }
          boolean malusNumber = checkOtherNumberBeforeAdresse();
          if (malusNumber) {
              lmalus *= JDONREFv3Scorer.ORDERMALUS;
          }
          return lmalus;
      }
      
      public void add(int category)
      {
          current.put(category,true);
      }
      
      public void add(HashMap<Integer,Boolean> categories)
      {
          current.putAll(categories);
      }
      
      public void next()
      {
          if (current.size()>0)
          {
            list.add((HashMap<Integer, Boolean>) current.clone());
            current.clear();
          }
          currentIndex = -1;
      }
  }

  static final class SubScorer {
    public JDONREFv3TermScorer scorer;
    // TODO: re-enable this if BQ ever sends us required clauses
    //public boolean required = false;
    public boolean prohibited;
    public Collector collector;
    public SubScorer next;

    public SubScorer(Scorer scorer, boolean required, boolean prohibited,
        Collector collector, SubScorer next, AdresseChecker checker) {
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
      
      if (scorer instanceof JDONREFv3TermScorer)
        ((JDONREFv3TermScorer)this.scorer).setChecker(checker);
    }
  }
  
  private SubScorer scorers = null;
  private BucketTable bucketTable;
  private final float[] coordFactors;
  // TODO: re-enable this if BQ ever sends us required clauses
  //private int requiredMask = 0;
  private final int minNrShouldMatch;
  private int end;
  private Bucket current;
  // Any time a prohibited clause matches we set bit 0:
  private static final int PROHIBITED_MASK = 1;
  
  protected JDONREFv3ESWeight protectedWeight;
  
  public JDONREFv3Scorer(JDONREFv3ESWeight weight, boolean disableCoord, int minNrShouldMatch,
      List<Scorer> optionalScorers, List<Scorer> prohibitedScorers, int maxCoord, AtomicReaderContext context) throws IOException {
    super(weight);
    
    this.protectedWeight = weight;
    this.minNrShouldMatch = minNrShouldMatch;
    this.context = context;
    
    bucketTable = new BucketTable(context);
    
    AdresseChecker checker = new AdresseChecker();
    checker.setWeight(weight);

    if (optionalScorers != null && optionalScorers.size() > 0)
    {
       for(int i=optionalScorers.size()-1;i>=0;i--) // scorers in order !
       {
          JDONREFv3TermScorer scorer = (JDONREFv3TermScorer) optionalScorers.get(i);
          if (i==optionalScorers.size()-1)
              scorer.setIsLast(); // some scorer might have no match !
          if (scorer.nextDoc() != NO_MORE_DOCS)
          {
            scorers = new SubScorer(scorer, false, false, bucketTable.newCollector(0), scorers, checker);
          }
       }
    }
    
    if (prohibitedScorers != null && prohibitedScorers.size() > 0) {
      for (Scorer scorer : prohibitedScorers) {
        if (scorer.nextDoc() != NO_MORE_DOCS) {
          scorers = new SubScorer(scorer, false, true, bucketTable.newCollector(PROHIBITED_MASK), scorers, checker);
        }
      }
    }

    coordFactors = new float[optionalScorers.size() + 1];
    for (int i = 0; i < coordFactors.length; i++) {
      coordFactors[i] = disableCoord ? 1.0f : weight.coord(i, maxCoord); 
    }
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
            // malus pour les éléments non trouvés.
            bs.score = current.score * current.coord / ((JDONREFv3Query)this.protectedWeight.getQuery()).getNumTokens();
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
  
  public float malus(Document d) throws IOException
  {
      for (SubScorer sub = scorers; sub != null; sub = sub.next)
      {
          sub.scorer.check(d);
          
          if (sub.scorer.isLast())
          {
              sub.scorer.checker.next();
              return sub.scorer.checker.malus();
          }
      }
      
      throw(new UnsupportedOperationException());
  }
  
  public float malus(int doc) throws IOException
  {
      Document d = this.context.reader().document(doc);
      return malus(d);
  }
  
  // Retrouve si un problème se pose avec l'absence du numéro d'adresse
  public boolean checkAdressType(Document d) throws IOException
  {
      for (SubScorer sub = scorers; sub != null; sub = sub.next)
      {
          if (sub.scorer.checker.isAdressType(d))
              return sub.scorer.checker.checkAdressNumberPresent();
          else
              return true;
      }
      return true;
  }
  
  // Retrouve si un problème se pose avec l'absence du numéro d'adresse
  public boolean checkAdressType(int doc) throws IOException
  {
      Document d = this.context.reader().document(doc);
      return checkAdressType(d);
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