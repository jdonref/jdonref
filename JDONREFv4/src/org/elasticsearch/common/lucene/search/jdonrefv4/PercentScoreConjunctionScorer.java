package org.elasticsearch.common.lucene.search.jdonrefv4;

/**
 * @author moquetju
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

import org.apache.lucene.util.ArrayUtil;

/** Scorer for conjunctions, sets of queries, all of which are required. */
class PercentScoreConjunctionScorer extends Scorer implements ICountable {
  protected int lastDoc = -1;
  protected final DocsAndFreqs[] docsAndFreqs;
  private final DocsAndFreqs lead;

  protected int ensureTokenMask = -1;
  
  @Override
  public int getTokenMask()
  {
      if (lastDoc==-1) return 0;

      int mask = 0;
      for(DocsAndFreqs daf : docsAndFreqs)
      {
          if (daf.scorer!=null && daf.scorer.docID()==lastDoc)
              mask |= ((ICountable)daf.scorer).getTokenMask();
      }
      return mask;
  }
  
  @Override
  public int getMinFreq()
  {
      if (lastDoc==-1) return 0;

      int min = 0;
      for(DocsAndFreqs daf : docsAndFreqs)
      {
          if (daf.scorer!=null && daf.scorer.docID()==lastDoc)
              min = Math.min(min,((ICountable)daf.scorer).getMinFreq());
      }
      return min;
  }
  
  @Override
  public int getTypeMask()
  {
      if (lastDoc==-1) return 0;

      int mask = 0;
      for(DocsAndFreqs daf : docsAndFreqs)
      {
          if (daf.scorer!=null && daf.scorer.docID()==lastDoc)
              mask |= ((ICountable)daf.scorer).getTypeMask();
      }
      return mask;
  }
  
  /**
    * @return Le masque des tokens qui matchent
    */
  @Override
  public int tokenMatch()
  {
      if (lastDoc==-1) return 0;

      int mask = 0;
      for(DocsAndFreqs daf : docsAndFreqs)
      {
          if (daf.scorer!=null && daf.scorer.docID()==lastDoc)
              mask |= ((ICountable)daf.scorer).tokenMatch();
      }
      return mask;
  }
    
  /**
    * @return Le masque des tokens qui matchent plusieurs fois
    */
  @Override
  public int multiTokenMatch()
  {
      if (lastDoc==-1) return 0;

      int mask = 0;
      int multimask = -1;
      for(DocsAndFreqs daf : docsAndFreqs)
      {
          if (daf.scorer!=null && daf.scorer.docID()==lastDoc)
          {
              mask |= ((ICountable)daf.scorer).multiTokenMatch();
              multimask &= ((ICountable)daf.scorer).tokenMatch();
          }
      }
      return mask | multimask;
  }  
  
    @Override
    public int count() {
        if (lastDoc==-1) return 0;
        
        int count = 0;
        for(DocsAndFreqs daf : docsAndFreqs)
        {
            if (daf.scorer.docID()==lastDoc)
                count += ((ICountable)daf.scorer).count();
        }
        return count;
    }
  
  PercentScoreConjunctionScorer(Weight weight, Scorer[] scorers) {
    super(weight);
    this.docsAndFreqs = new DocsAndFreqs[scorers.length];
    for (int i = 0; i < scorers.length; i++) {
      docsAndFreqs[i] = new DocsAndFreqs(scorers[i]);
    }
    // Sort the array the first time to allow the least frequent DocsEnum to
    // lead the matching.
    ArrayUtil.timSort(docsAndFreqs, new Comparator<DocsAndFreqs>() {
      @Override
      public int compare(DocsAndFreqs o1, DocsAndFreqs o2) {
        return Long.compare(o1.cost, o2.cost);
      }
    });

    lead = docsAndFreqs[0]; // least frequent DocsEnum leads the intersection
  }

  private int doNext(int doc) throws IOException {
    for(;;) {
      // doc may already be NO_MORE_DOCS here, but we don't check explicitly
      // since all scorers should advance to NO_MORE_DOCS, match, then
      // return that value.
      advanceHead: for(;;) {
        for (int i = 1; i < docsAndFreqs.length; i++) {
          // invariant: docsAndFreqs[i].doc <= doc at this point.

          // docsAndFreqs[i].doc may already be equal to doc if we "broke advanceHead"
          // on the previous iteration and the advance on the lead scorer exactly matched.
          if (docsAndFreqs[i].doc < doc) {
            docsAndFreqs[i].doc = docsAndFreqs[i].scorer.advance(doc);

            if (docsAndFreqs[i].doc > doc) {
              // DocsEnum beyond the current doc - break and advance lead to the new highest doc.
              doc = docsAndFreqs[i].doc;
              break advanceHead;
            }
          }
        }
        // success - all DocsEnums are on the same doc
        return doc;
      }
      // advance head for next iteration
      doc = lead.doc = lead.scorer.advance(doc);
    }
  }

  @Override
  public int advance(int target) throws IOException {
    lead.doc = lead.scorer.advance(target);
    
    return lastDoc = doNext(lead.doc);
  }

  @Override
  public int docID() {
    return lastDoc;
  }

  @Override
  public int nextDoc() throws IOException {
    lead.doc = lead.scorer.nextDoc();
    return lastDoc = doNext(lead.doc);
  }

  @Override
  public float score() throws IOException {
    // TODO: sum into a double and cast to float if we ever send required clauses to BS1
    float sum = 0.0f;
    for (DocsAndFreqs docs : docsAndFreqs) {
      sum += docs.scorer.score();
    }
    return sum;
  }
  
  @Override
  public int freq() {
    return docsAndFreqs.length;
  }

  @Override
  public long cost() {
    return lead.scorer.cost();
  }

  @Override
  public Collection<ChildScorer> getChildren() {
    ArrayList<ChildScorer> children = new ArrayList<>(docsAndFreqs.length);
    for (DocsAndFreqs docs : docsAndFreqs) {
      children.add(new ChildScorer(docs.scorer, "MUST"));
    }
    return children;
  }

  static final class DocsAndFreqs {
    final long cost;
    final Scorer scorer;
    int doc = -1;
   
    DocsAndFreqs(Scorer scorer) {
      this.scorer = scorer;
      this.cost = scorer.cost();
    }
  }
}
