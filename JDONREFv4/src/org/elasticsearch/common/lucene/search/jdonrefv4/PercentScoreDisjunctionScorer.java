package org.elasticsearch.common.lucene.search.jdonrefv4;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;

import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

/**
 * Base class for Scorers that score disjunctions.
 */
public class PercentScoreDisjunctionScorer extends Scorer implements ICountable {
  protected final Scorer subScorers[];
  protected int numScorers;
  private double score;

  /** The document number of the current match. */
  protected int doc = -1;
  /** Number of matching scorers for the current match. */
  protected int freq = -1;
  
  protected int ensureTokenMask = -1;
  
  public void ensureToken(int mask)
  {
      ensureTokenMask = mask;
  }
  
  public int getTokenMask()
  {
      if (doc==-1) return 0;

      int mask = 0;
      for(Scorer s : subScorers)
      {
          if (s!=null && s.docID()==doc)
              mask |= ((ICountable)s).getTokenMask();
      }
      return mask;
  }
  
  public int getMinFreq()
  {
      if (doc==-1) return 0;

      int min = Integer.MAX_VALUE;
      for(Scorer s : subScorers)
      {
          if (s!=null && s.docID()==doc)
              min = Math.min(min,((ICountable)s).getMinFreq());
      }
      return min;
  }
  
  public int getTypeMask()
  {
      if (doc==-1) return 0;

      int mask = 0;
      for(Scorer s : subScorers)
      {
          if (s!=null && s.docID()==doc)
              mask |= ((ICountable)s).getTypeMask();
      }
      return mask;
  }
  
  /**
    * @return Le masque des tokens qui matchent
    */
  @Override
  public int tokenMatch()
  {
      if (doc==-1) return 0;

      int mask = 0;
      for(Scorer s : subScorers)
      {
          if (s!=null && s.docID()==doc)
              mask |= ((ICountable)s).tokenMatch();
      }
      return mask;
  }
    
  /**
    * @return Le masque des tokens qui matchent plusieurs fois
    */
  @Override
  public int multiTokenMatch()
  {
      if (doc==-1) return 0;

      int mask = 0;
      int multimask = -1;
      for(Scorer s : subScorers)
      {
          if (s!=null && s.docID()==doc)
          {
              mask |= ((ICountable)s).multiTokenMatch();
              multimask &= ((ICountable)s).tokenMatch();
          }
      }
      return mask | multimask;
  }
  
  @Override
  public int count() {
        if (doc==-1) return 0;

        int count = 0;
        for(Scorer s : subScorers)
        {
            if (s!=null && s.docID()==doc)
                count += ((ICountable)s).count();
        }
        return count;
  }
  
  public PercentScoreDisjunctionScorer(Weight weight, Scorer subScorers[]) {
    super(weight);
    this.subScorers = subScorers;
    this.numScorers = subScorers.length;
    if (numScorers <= 1) {
      throw new IllegalArgumentException("There must be at least 2 subScorers");
    }
    heapify();
  }
  
  /** 
   * Organize subScorers into a min heap with scorers generating the earliest document on top.
   */
  private void heapify() {
    for (int i = (numScorers >>> 1) - 1; i >= 0; i--) {
      heapAdjust(i);
    }
  }
  
  /** 
   * The subtree of subScorers at root is a min heap except possibly for its root element.
   * Bubble the root down as required to make the subtree a heap.
   */
  private void heapAdjust(int root) {
    Scorer scorer = subScorers[root];
    int doc = scorer.docID();
    int i = root;
    while (i <= (numScorers >>> 1) - 1) {
      int lchild = (i << 1) + 1;
      Scorer lscorer = subScorers[lchild];
      int ldoc = lscorer.docID();
      int rdoc = Integer.MAX_VALUE, rchild = (i << 1) + 2;
      Scorer rscorer = null;
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
   * Remove the root Scorer from subScorers and re-establish it as a heap
   */
  private void heapRemoveRoot() {
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
  
  // if we haven't already computed freq + score, do so
  private void visitScorers() throws IOException {
    reset();
    freq = 1;
    accum(subScorers[0]);
    visit(1);
    visit(2);
  }
  
  // TODO: remove recursion.
  private void visit(int root) throws IOException {
    if (root < numScorers && subScorers[root].docID() == doc) {
      freq++;
      accum(subScorers[root]);
      visit((root<<1)+1);
      visit((root<<1)+2);
    }
  }

  protected void reset() {
    score = 0;
  }
  
  protected void accum(Scorer subScorer) throws IOException {
    score += subScorer.score();
  }
  
  protected float getFinal() {
    return (float)score ;
  }

    @Override
  public final long cost() {
    long sum = 0;
    for (int i = 0; i < numScorers; i++) {
      sum += subScorers[i].cost();
    }
    return sum;
  } 
  
  @Override
  public final int docID() {
   return doc;
  }
  
  @Override
  public final int nextDoc() throws IOException {
    assert doc != NO_MORE_DOCS;
    while(true) {
      if (subScorers[0].nextDoc() != NO_MORE_DOCS) {
        heapAdjust(0);
      } else {
        heapRemoveRoot();
        if (numScorers == 0) {
          return doc = NO_MORE_DOCS;
        }
      }
      int docID = subScorers[0].docID();
      if (docID != doc) {
            freq = -1;
            return doc = docID;
      }
    }
  }
  
  @Override
  public final int advance(int target) throws IOException {
    assert doc != NO_MORE_DOCS;
    while(true) {
      if (subScorers[0].advance(target) != NO_MORE_DOCS) {
        heapAdjust(0);
      } else {
        heapRemoveRoot();
        if (numScorers == 0) {
          return doc = NO_MORE_DOCS;
        }
      }
      int docID = subScorers[0].docID();
      if (docID >= target) {
          
            freq = -1;
            return doc = docID;
      }
    }
  }

  @Override
  public final float score() throws IOException {
    visitScorers();
    return getFinal();
  }

  @Override
  public final int freq() throws IOException {
    if (freq < 0) {
      visitScorers();
    }
    return freq;
  }
}