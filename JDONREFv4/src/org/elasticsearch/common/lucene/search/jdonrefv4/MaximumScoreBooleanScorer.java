package org.elasticsearch.common.lucene.search.jdonrefv4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.elasticsearch.common.lucene.search.jdonrefv4.MaximumScoreBooleanQuery.PayloadAsScoreWeight;

/**
 *
 * @author Julien
 */
public class MaximumScoreBooleanScorer extends Scorer
{
  protected AtomicReaderContext context;
  int debugDoc = -1;
    
  protected void collectBucket(Bucket bucket, PayloadTermScorer scorer) throws IOException
  {
        bucket.score = Math.max(bucket.score,scorer.score());
  }
  
  private final class JDONREFv3ScorerCollector extends Collector {
    private BucketTable bucketTable;
    private int mask;
    private PayloadTermScorer scorer;
    
    public JDONREFv3ScorerCollector(int mask, BucketTable bucketTable) {
      this.mask = mask;
      this.bucketTable = bucketTable;
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
        
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" new doc :"+bucket.doc);
        
        bucket.bits = mask;                       // initialize mask
        
        bucket.next = table.first;                // push onto valid list
        table.first = bucket;
      } else {                                    // valid bucket
        if (debugDoc!=-1 && debugDoc==bucket.doc)
        {
            debug = true;
        }
        
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" old doc :"+bucket.doc);
        
        bucket.bits |= mask;                        // add bits in mask
      }
      
      collectBucket(bucket,scorer);
    }
    
    @Override
    public void setNextReader(AtomicReaderContext context) {
      // not needed by this implementation
    }
    
    @Override
    public void setScorer(Scorer scorer) {
      assert(scorer instanceof PayloadTermScorer);
      this.scorer = (PayloadTermScorer) scorer;
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
    public float score() { return (float)(score); }
    
    @Override
    public long cost() { return 1; }

  }

  public final class Bucket {
    int doc = -1;             // tells if bucket is valid
    float score;             // incremental score
    
    // TODO: break out bool anyProhibited, int
    // numRequiredMatched; then we can remove 32 limit on
    // required clauses
    int bits;                // used for bool constraints
    float coord;               // count of terms in score
    Bucket next;             // next valid bucket
  }
  
  /** A simple hash table of document scores within a range. */
  final class BucketTable {
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

  static final class SubScorer {
    public PayloadTermScorer scorer;
    // TODO: re-enable this if BQ ever sends us required clauses
    //public boolean required = false;
    public boolean prohibited;
    public Collector collector;
    public SubScorer next;

    public SubScorer(Scorer scorer, 
        Collector collector, SubScorer next) {
        assert(scorer instanceof PayloadTermScorer);
      this.scorer = (PayloadTermScorer) scorer;
      this.collector = collector;
      this.next = next;
    }
  }
  
  private SubScorer scorers = null; // for collect mode
  private BucketTable bucketTable;
  
  protected int doc = -1;
  
  protected PayloadAsScoreWeight protectedWeight;
  
  protected PayloadTermScorer[] subScorers = null; // for nextDoc & advance mode
  protected int numScorers;
  protected int nrMatchers = -1;
  protected double score = Float.NaN;
  
  protected int numTokens;
  
  public MaximumScoreBooleanScorer(PayloadAsScoreWeight weight,
      List<PayloadTermScorer> optionalScorers, AtomicReaderContext context, 
      int debugDoc, int numTokens) throws IOException {
    super(weight);
    
    this.numTokens = numTokens;
    
    this.protectedWeight = weight;
    this.context = context;
    this.debugDoc = debugDoc;
    //this.checker.setQuery(weight.getQuery()); // A CORRIGER
    
    bucketTable = new BucketTable();

    if (optionalScorers != null && optionalScorers.size() > 0)
    {
       // nextDoc & advance mode
       subScorers = new PayloadTermScorer[optionalScorers.size()];
       numScorers = subScorers.length;
       
       // collect mode 
       for(int i=optionalScorers.size()-1;i>=0;i--) // set scorers in order because they are reversed chained !
       {
          PayloadTermScorer scorer = optionalScorers.get(i);
          
          //if (scorer.nextDoc() != NO_MORE_DOCS)
          {
            scorers = new SubScorer(scorer, bucketTable.newCollector(0), scorers); // toString only .. waste
          }
          
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
    PayloadTermScorer scorer = subScorers[root];
    int tmpDoc = scorer.docID();
    int i = root;
    while (i <= (numScorers >> 1) - 1) {
      int lchild = (i << 1) + 1;
      PayloadTermScorer lscorer = subScorers[lchild];
      int ldoc = lscorer.docID();
      int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
      PayloadTermScorer rscorer = null;
      if (rchild < numScorers) {
        rscorer = subScorers[rchild];
        rdoc = rscorer.docID();
      }
      if (ldoc < tmpDoc) {
        if (rdoc < ldoc) {
          subScorers[i] = rscorer;
          subScorers[rchild] = scorer;
          i = rchild;
        } else {
          subScorers[i] = lscorer;
          subScorers[lchild] = scorer;
          i = lchild;
        }
      } else if (rdoc < tmpDoc) {
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
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" Collect doc "+current.doc+" coord: ("+current.coord+"/"+maxCoord+")^3 = "+Math.pow(current.coord / maxCoord,2));
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" Collect doc "+current.doc+" final Score: "+bs.score);
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
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" advance to "+target);
    while(true) {
      // NB: don't know why subScorers[0] may be null
      if (subScorers[0].advance(target) != NO_MORE_DOCS) {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" document "+subScorers[0].docID()+" trouvé dans "+((PayloadAsScoreTermQuery)subScorers[0].weight().getQuery()).getTerm().field());
          
        heapAdjust(0);
      } else {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" document "+target+" non trouvé dans "+((PayloadAsScoreTermQuery)subScorers[0].weight().getQuery()).getTerm().field());
        heapRemoveRoot();
        if (numScorers == 0) {
          return doc = NO_MORE_DOCS;
        }
      }
      if (subScorers[0].docID() >= target) {
        if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" calcul du score pour  "+subScorers[0].docID());
        
        this.doc = subScorers[0].docID();
        
        if (afterNext())
            return doc;
      }
    }
  }
  
  protected boolean afterNext() throws IOException
  {
      long startAfterNext = System.nanoTime();
      
      final PayloadTermScorer sub = subScorers[0];
      doc = sub.docID();
      boolean debug = false;
      if (debugDoc!=-1 && debugDoc==doc)
      {
          debug = true;
      }
      if (debug)
          Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"Préparation pour le document "+doc);
      
      if (doc != NO_MORE_DOCS) {
          Bucket b = new Bucket();
          b.doc = doc;

          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"Calcul du score pour le document "+doc+" scorer "+0);
          collectBucket(b, subScorers[0]);
          //nrMatchers = 1;
          nrMatchers = ((PayloadAsScoreTermQuery)subScorers[0].getWeight().getQuery()).getNumTerms();
          countMatches(b, 1);
          countMatches(b, 2);
          
          score = (b.score * nrMatchers* nrMatchers) / (numTokens*numTokens);
          //score = b.score; // score evaluated while indexing

          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"Calcul du score final pour le document "+doc+" score: "+score);
          
          if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"  End afterNext doc "+sub.docID()+" "+(System.nanoTime()-startAfterNext));
          return true;
      }
      if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"  End afterNext doc "+sub.docID()+" "+(System.nanoTime()-startAfterNext));
      return false;
  }
  
  protected void countMatches(Bucket b,int root) throws IOException {
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==doc)
    {
        debug = true;
    }
    if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" countMatches "+doc);
    if (root < numScorers && subScorers[root].docID() == doc)
    {
      nrMatchers=Math.max(nrMatchers,((PayloadAsScoreTermQuery)subScorers[root].getWeight().getQuery()).getNumTerms());
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
  public int nextDoc() throws IOException
  {
      long startNextDoc = System.nanoTime();
      
    assert doc != NO_MORE_DOCS;
    boolean debug = false;
    if (debugDoc!=-1 && debugDoc==doc)
    {
        debug = true;
    }
    if (debug)
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"subscorers size : "+subScorers.length+" first : "+(subScorers[0]==null));
    
    if (numScorers==0){
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"  End nextDoc no doc in "+(System.nanoTime()-startNextDoc));
        return NO_MORE_DOCS;
    }
    
    while(true) {
      int doc = subScorers[0].nextDoc();
      
      if (doc != NO_MORE_DOCS) {
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"Nouveau document :"+subScorers[0].docID());
        heapAdjust(0);
      } else {
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"Plus de documents dans root");
        heapRemoveRoot();
        if (numScorers == 0) {
            if (debug)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"  End nextDoc no doc in "+(System.nanoTime()-startNextDoc));
          return this.doc = NO_MORE_DOCS;
        }
      }
      if (subScorers[0].docID() == doc && doc!=-1) { // check if heap has been adjusted
        if (debug)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"Nouveau document trouvé, préparation.");
        
        //this.doc = subScorers[0].docID();
        
        if (afterNext())
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"  End nextDoc doc "+subScorers[0].docID()+" in "+(System.nanoTime()-startNextDoc));
            return this.doc;
        }
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
        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+"subscorers size : "+subScorers.length+" numScorers : "+numScorers);
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