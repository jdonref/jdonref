package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BooleanQuery.BooleanWeight;
import org.apache.lucene.search.JDONREFv3Query.JDONREFv3ESWeight;

/**
 *
 * @author Julien
 */
public class JDONREFv3Scorer extends Scorer {
  
  private static final class JDONREFv3ScorerCollector extends Collector {
    private BucketTable bucketTable;
    private int mask;
    private Scorer scorer;
    
    public JDONREFv3ScorerCollector(int mask, BucketTable bucketTable) {
      this.mask = mask;
      this.bucketTable = bucketTable;
    }
    
    @Override
    public void collect(final int doc) throws IOException {
      final BucketTable table = bucketTable;
      final int i = doc & BucketTable.MASK;
      final Bucket bucket = table.buckets[i];
      
      if (bucket.doc != doc) {                    // invalid bucket
        bucket.doc = doc;                         // set doc
        bucket.score = scorer.score();            // initialize score
        bucket.bits = mask;                       // initialize mask
        bucket.coord = 1;                         // initialize coord

        bucket.next = table.first;                // push onto valid list
        table.first = bucket;
      } else {                                    // valid bucket
        bucket.score += scorer.score();           // increment score
        bucket.bits |= mask;                      // add bits in mask
        bucket.coord++;                           // increment coord
      }
    }
    
    @Override
    public void setNextReader(AtomicReaderContext context) {
      // not needed by this implementation
    }
    
    @Override
    public void setScorer(Scorer scorer) {
      this.scorer = scorer;
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
    public float score() { return (float)score; }
    
    @Override
    public long cost() { return 1; }

  }

  static final class Bucket {
    int doc = -1;            // tells if bucket is valid
    double score;             // incremental score
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
  
    public BucketTable() {
      // Pre-fill to save the lazy init when collecting
      // each sub:
      for(int idx=0;idx<SIZE;idx++) {
        buckets[idx] = new Bucket();
      }
    }

    public Collector newCollector(int mask) {
      return new JDONREFv3ScorerCollector(mask, this);
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
      final static int LIGNE4 = 1;
      final static int COMMUNE = 2;
      final static int CODE_DEPARTEMENT = 3;
      final static int CODE_ARRONDISSEMENT = 4;
      final static int CODE_POSTAL = 5;
      final static int CODE_INSEE = 6;
      
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
      }
      
      public boolean contains(Document d,String field,String value)
      {
          String[] docValues = d.getValues(field);
          
          for(int i=0;i<docValues.length;i++)
          {
              String docValue = docValues[i];
              if (docValue.contains(value))
                  return true;
          }
          
          return false;
      }
      
      /**
       * Retrouve la catégorie à laquelle appartient la valeur donnée dans le document en question.
       * @return
       */
      public HashMap<Integer,Boolean> getCategories(int docId,String value) throws IOException
      {
          HashMap<Integer,Boolean> categories = new HashMap<Integer,Boolean>();
          
          Document d = weight.getSearcher().getIndexReader().document(docId);
          
          if (contains(d,"ligne4",value)) categories.put(LIGNE4,true);
          if (contains(d,"commune",value)) categories.put(COMMUNE,true);
          if (contains(d,"code_departement",value)) categories.put(CODE_DEPARTEMENT,true);
          if (contains(d,"code_arrondissement",value)) categories.put(CODE_ARRONDISSEMENT,true);
          if (contains(d,"code_postal",value)) categories.put(CODE_POSTAL,true);
          if (contains(d,"code_insee",value)) categories.put(CODE_POSTAL,true);
          
          return categories;
      }
      
      /**
       * 
       * @return Vrai si l'une des catégories courante n'était pas référencée auparavant.
       */
      public boolean check()
      {
          Set<Integer> keys = current.keySet();
          Iterator<Integer> ite = keys.iterator();
          
          while(ite.hasNext())
          {
              int category = ite.next();
              
              boolean same = true;
              boolean check = false;
              for(int j=list.size()-1;!check && j>=0;j--)
              {
                  if (same)
                  {
                      if (list.get(j).get(category)==null)
                          same = false;
                  }
                  else
                  {
                      if (list.get(j).get(category)!=null && list.get(j).size()>1)
                      {
                          check = true;
                      }
                  }
              }
              if (!check) return false;
          }
          
          return true;
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
          list.add(current);
          current.clear();
      }
  }

  static final class SubScorer {
    public Scorer scorer;
    // TODO: re-enable this if BQ ever sends us required clauses
    //public boolean required = false;
    public boolean prohibited;
    public Collector collector;
    public SubScorer next;

    public SubScorer(Scorer scorer, boolean required, boolean prohibited,
        Collector collector, SubScorer next, AdresseChecker checker) {
      if (required) {
        throw new IllegalArgumentException("this scorer cannot handle required=true");
      }
      this.scorer = scorer;
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
  private BucketTable bucketTable = new BucketTable();
  private final float[] coordFactors;
  // TODO: re-enable this if BQ ever sends us required clauses
  //private int requiredMask = 0;
  private final int minNrShouldMatch;
  private int end;
  private Bucket current;
  // Any time a prohibited clause matches we set bit 0:
  private static final int PROHIBITED_MASK = 1;
  
  protected JDONREFv3ESWeight weight;
  
  public JDONREFv3Scorer(JDONREFv3ESWeight weight, boolean disableCoord, int minNrShouldMatch,
      List<Scorer> optionalScorers, List<Scorer> prohibitedScorers, int maxCoord) throws IOException {
    super(weight);
    
    this.weight = weight;
    this.minNrShouldMatch = minNrShouldMatch;
    
    AdresseChecker checker = new AdresseChecker();
    checker.setWeight(weight);

    if (optionalScorers != null && optionalScorers.size() > 0) {
      for (Scorer scorer : optionalScorers) {
        if (scorer.nextDoc() != NO_MORE_DOCS) {
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
    BucketScorer bs = new BucketScorer(weight);

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
            bs.score = current.score * coordFactors[current.coord];
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