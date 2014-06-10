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
import org.apache.lucene.search.JDONREFv3Query.JDONREFv3ESWeight;

/**
 *
 * @author Julien
 */
public class JDONREFv3Scorer extends Scorer {
  
  public final static float ORDERMALUS = 0.5f;
  public final static float NUMBERMALUS = 0;
    
  private static final class JDONREFv3ScorerCollector extends Collector {
    private BucketTable bucketTable;
    private int mask;
    private JDONREFv3TermScorer scorer;
    
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
        bucket.malus = scorer.getMalus();
        bucket.adressType = scorer.getAdressType();
        bucket.adressNumberPresent = scorer.getAdressNumberPresent();
        if (scorer.isLast())
        {
            bucket.score *=bucket.malus;          // applique le malus au total
            if (bucket.adressType && !bucket.adressNumberPresent)
                bucket.score *= JDONREFv3Scorer.NUMBERMALUS;
        }
        bucket.bits = mask;                       // initialize mask
        bucket.coord = 1;                         // initialize coord

        bucket.next = table.first;                // push onto valid list
        table.first = bucket;
      } else {                                    // valid bucket
        bucket.score += scorer.score();           // increment score
        bucket.malus *= scorer.getMalus();
        bucket.adressNumberPresent |= scorer.getAdressNumberPresent();
        if (scorer.isLast())
        {
            bucket.score *= bucket.malus;          // applique le malus au total
            if (bucket.adressType && !bucket.adressNumberPresent)
                bucket.score *=  JDONREFv3Scorer.NUMBERMALUS;
        }
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
    double score;             // incremental score
    double malus;             // malus final à appliquer
    boolean adressType;       // le document est une adresse
    boolean adressNumberPresent; // présence du numéro d'adresse ...
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
      final static int NUMEROADRESSE = 1;
      final static int ADRESSE = 2;
      final static int VOIE = 3;
      final static int COMMUNE = 4;
      final static int CODE_DEPARTEMENT = 5;
      final static int CODE_ARRONDISSEMENT = 6;
      final static int CODE_POSTAL = 7;
      final static int CODE_INSEE = 8;
      
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
              String docValue = docValues[i].toLowerCase();
              if (docValue.contains(value))
                  return true;
          }
          
          return false;
      }
      
      public boolean isAdressType(int docId) throws IOException
      {
          Document d = weight.getSearcher().getIndexReader().document(docId);
          
          String[] types = d.getValues("type");
          
          if (types[0].equals("adresse"))
          {
              return true;
          }
          
          return false;
      }
      
      boolean foodebug = false;
      
      /**
       * Retrouve la catégorie à laquelle appartient la valeur donnée dans le document en question.
       * @return
       */
      public HashMap<Integer,Boolean> getCategories(int docId,String value) throws IOException
      {
          HashMap<Integer,Boolean> categories = new HashMap<Integer,Boolean>();
          
          Document d = weight.getSearcher().getIndexReader().document(docId);
          
          if (d.getField("numero")!=null && d.getField("numero").stringValue().equals("59"))
              this.foodebug = true;
          
          if (contains(d,"ligne4",value))
          {
              if (d.getValues("type")[0].equals("voie"))
                categories.put(VOIE,true);
              else
              {
                  if (contains(d,"numero",value))
                    categories.put(NUMEROADRESSE,true);
                  else
                    categories.put(ADRESSE,true);
              }
          }
          if (contains(d,"commune",value)) categories.put(COMMUNE,true);
          if (contains(d,"code_departement",value)) categories.put(CODE_DEPARTEMENT,true);
          if (contains(d,"code_arrondissement",value)) categories.put(CODE_ARRONDISSEMENT,true);
          if (contains(d,"code_postal",value)) categories.put(CODE_POSTAL,true);
          if (contains(d,"code_insee",value)) categories.put(CODE_POSTAL,true);
          
          return categories;
      }
      

      protected boolean checkAdressNumberPresent()
      {
          Set<Integer> keys = current.keySet();
          Iterator<Integer> ite = keys.iterator();
          
          while(ite.hasNext())
          {
              int category = ite.next();
              
              if (category == NUMEROADRESSE)
              {
                  return true;
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
          Set<Integer> keys = current.keySet();
          Iterator<Integer> ite = keys.iterator();
          
          while(ite.hasNext())
          {
              int category = ite.next();
              
              if (category == ADRESSE)
              {
                  if (list.size()==0) return false;
                  
                  HashMap<Integer,Boolean> hash = list.get(list.size()-1);
                  
                  if (hash.get(NUMEROADRESSE)!=null || hash.get(ADRESSE)!=null)
                      return false;
                  else
                  {
                      if (hash.get(CODE_ARRONDISSEMENT)==null)
                            return true;
                      if (hash.get(CODE_DEPARTEMENT)==null)
                            return true;
                      if (hash.get(CODE_INSEE)==null)
                            return true;
                      if (hash.get(CODE_POSTAL)==null)
                            return true;
                      return false;
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
          Set<Integer> keys = current.keySet();
          Iterator<Integer> ite = keys.iterator();
          
          while(ite.hasNext())
          {
              int category = ite.next();
              
              if (list.size()==0) return true;
              
              boolean same = true;
              boolean check = false;
              
              if (category==NUMEROADRESSE) same = false; // numero is the first token from ligne4
              
              for(int j=list.size()-1;!check && j>=0;j--)
              {
                  if (same)
                  {
                      if (category==ADRESSE || category==NUMEROADRESSE)
                      {
                          if (list.get(j).get(ADRESSE)==null
                                  && list.get(j).get(NUMEROADRESSE)==null)
                                  same = false;
                      }
                      else
                      {
                          if (list.get(j).get(category)==null)
                            same = false;
                      }
                  }
                  else
                  {
                      if (category==ADRESSE || category==NUMEROADRESSE)
                      {
                        if ((list.get(j).get(ADRESSE)!=null || list.get(j).get(NUMEROADRESSE)!=null) && list.get(j).size()==1)
                        {
                          check = true;
                        }
                      }
                      else
                      {
                        if (list.get(j).get(category)!=null && list.get(j).size()==1)
                        {
                          check = true;
                        }
                      }
                  }
              }
              if (!same && check) return false;
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
  private BucketTable bucketTable = new BucketTable();
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
      List<Scorer> optionalScorers, List<Scorer> prohibitedScorers, int maxCoord) throws IOException {
    super(weight);
    
    this.protectedWeight = weight;
    this.minNrShouldMatch = minNrShouldMatch;
    
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
  
  // Calcule le malus appliqué à un document seul.
  public float malus(int doc) throws IOException
  {
      float malus = 1.0f;
      for (SubScorer sub = scorers; sub != null; sub = sub.next)
      {
            int subScorerDocID = sub.scorer.docID();
            if (subScorerDocID != NO_MORE_DOCS)
            {
                JDONREFv3TermQuery query = (JDONREFv3TermQuery)sub.scorer.weight.getQuery();
                HashMap<Integer,Boolean> categories = sub.scorer.checker.getCategories(subScorerDocID, query.getTerm().text());
                sub.scorer.checker.add(categories);
      
                malus *= sub.scorer.malus();
            }
      }
      return malus;
  }
  
  // Retrouve si un problème se pose avec l'absence du numéro d'adresse
  public boolean checkAdressType(int doc) throws IOException
  {
      for (SubScorer sub = scorers; sub != null; sub = sub.next)
      {
          if (sub.scorer.checkAdressType(doc))
              return sub.scorer.checkAdressNumberPresent();
          else
              return true;
      }
      return true;
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