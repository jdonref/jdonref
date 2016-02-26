package org.elasticsearch.common.lucene.search.jdonrefv4;

/**
 *
 * @author moquetju
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.search.Scorer;

public class PercentScoreReqOptSumScorer extends Scorer implements ICountable {
  /** The scorers passed from the constructor.
   * These are set to null as soon as their next() or skipTo() returns false.
   */
  protected Scorer reqScorer;
  protected Scorer optScorer;
  
  public int getTokenMask()
  {
      if (reqScorer.docID()==-1) return 0;
      
      if (optScorer==null || optScorer.docID()!=reqScorer.docID()) return ((ICountable)reqScorer).getTokenMask();
      
      return ((ICountable)reqScorer).getTokenMask() | ((ICountable)optScorer).getTokenMask();
  }
  
  public int getMinFreq()
  {
      if (reqScorer.docID()==-1) return 0;
      
      if (optScorer==null || optScorer.docID()!=reqScorer.docID()) return ((ICountable)reqScorer).getMinFreq();
      
      return Math.min(((ICountable)reqScorer).getMinFreq(),((ICountable)optScorer).getMinFreq());
  }
  
  public int getTypeMask()
  {
      if (reqScorer.docID()==-1) return 0;
      
      if (optScorer==null || optScorer.docID()!=reqScorer.docID()) return ((ICountable)reqScorer).getTypeMask();
      
      return ((ICountable)reqScorer).getTypeMask() | ((ICountable)optScorer).getTypeMask();
  }
  
  /**
    * @return Le masque des tokens qui matchent
    */
  public int tokenMatch()
  {
      if (reqScorer.docID()==-1) return 0;
      
      if (optScorer==null || optScorer.docID()!=reqScorer.docID()) return ((ICountable)reqScorer).tokenMatch();
      
      return ((ICountable)reqScorer).tokenMatch() | ((ICountable)optScorer).tokenMatch();
  }
    
  /**
    * @return Le masque des tokens qui matchent plusieurs fois
    */
  public int multiTokenMatch()
  {
      if (reqScorer.docID()==-1) return 0;
      
      if (optScorer==null || optScorer.docID()!=reqScorer.docID()) return ((ICountable)reqScorer).multiTokenMatch();
      
      return (((ICountable)reqScorer).tokenMatch() & ((ICountable)optScorer).tokenMatch())
           | ((ICountable)reqScorer).multiTokenMatch() | ((ICountable)optScorer).multiTokenMatch();
  }
  
  public int count()
  {
      if (reqScorer.docID()==-1) return 0;
      
      if (optScorer==null || optScorer.docID()!=reqScorer.docID()) return ((ICountable)reqScorer).count();
      
      return ((ICountable)reqScorer).count() + ((ICountable)optScorer).count();
  }

  /** Construct a <code>ReqOptScorer</code>.
   * @param reqScorer The required scorer. This must match.
   * @param optScorer The optional scorer. This is used for scoring only.
   */
  public PercentScoreReqOptSumScorer(
      Scorer reqScorer,
      Scorer optScorer)
  {
    super(reqScorer.getWeight());
    assert reqScorer != null;
    assert optScorer != null;
    this.reqScorer = reqScorer;
    this.optScorer = optScorer;
  }

  @Override
  public int nextDoc() throws IOException {
    int curDoc = reqScorer.nextDoc();
    
    if (optScorer!=null)
    {
        int optScorerDoc = optScorer.docID();
        if (optScorerDoc < curDoc && (optScorerDoc = optScorer.advance(curDoc)) == NO_MORE_DOCS) {
        optScorer = null;
        }
    }
    
    return curDoc;
  }
  
  @Override
  public int advance(int target) throws IOException {
    int curDoc = reqScorer.advance(target);
    
    if (optScorer!=null)
    {
        int optScorerDoc = optScorer.docID();
        if (optScorerDoc < curDoc && (optScorerDoc = optScorer.advance(curDoc)) == NO_MORE_DOCS) {
        optScorer = null;
        }
    }
    
    return curDoc;
  }
  
  @Override
  public int docID() {
    return reqScorer.docID();
  }
  
  /** Returns the score of the current document matching the query.
   * Initially invalid, until {@link #nextDoc()} is called the first time.
   * @return The score of the required scorer, eventually increased by the score
   * of the optional scorer when it also matches the current document.
   */
  @Override
  public float score() throws IOException {
    // TODO: sum into a double and cast to float if we ever send required clauses to BS1
    int curDoc = reqScorer.docID();
    float reqScore = reqScorer.score();
    if (optScorer == null) {
      return reqScore;
    }
    
    return optScorer.docID() == curDoc ? reqScore + optScorer.score() : reqScore;
  }

  @Override
  public int freq() throws IOException {
    // we might have deferred advance()
    score();
    return (optScorer != null && optScorer.docID() == reqScorer.docID()) ? 2 : 1;
  }

  @Override
  public Collection<ChildScorer> getChildren() {
    ArrayList<ChildScorer> children = new ArrayList<>(2);
    children.add(new ChildScorer(reqScorer, "MUST"));
    children.add(new ChildScorer(optScorer, "SHOULD"));
    return children;
  }

  @Override
  public long cost() {
    return reqScorer.cost();
  }
}

