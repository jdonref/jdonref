package org.apache.lucene.search;

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

import java.util.HashMap;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.JDONREFv3Scorer.AdresseChecker;
import org.apache.lucene.search.similarities.Similarity;

/** Expert: A <code>Scorer</code> for documents matching a <code>Term</code>.
 */
public class JDONREFv3TermScorer extends Scorer {
  protected final DocsEnum docsEnum;
  protected final Similarity.SimScorer docScorer;
  
  protected AdresseChecker checker;

  public AdresseChecker getChecker()
  {
      return checker;
  }

  public void setChecker(AdresseChecker checker)
  {
      this.checker = checker;
  }
  
  /**
   * Construct a <code>TermScorer</code>.
   * 
   * @param weight
   *          The weight of the <code>Term</code> in the query.
   * @param td
   *          An iterator over the documents matching the <code>Term</code>.
   * @param docScorer
   *          The </code>Similarity.SimScorer</code> implementation 
   *          to be used for score computations.
   */
  public JDONREFv3TermScorer(Weight weight, DocsEnum td, Similarity.SimScorer docScorer) {
    super(weight);
    this.docScorer = docScorer;
    this.docsEnum = td;
  }

  @Override
  public int docID() {
    return docsEnum.docID();
  }

  @Override
  public int freq() throws IOException {
    return docsEnum.freq();
  }

  /**
   * Advances to the next document matching the query. <br>
   * 
   * @return the document matching the query or NO_MORE_DOCS if there are no more documents.
   */
  @Override
  public int nextDoc() throws IOException {
    return docsEnum.nextDoc();
  }
  
  boolean debugbar = false;
  
  @Override
  public float score() throws IOException
  {
    assert docID() != NO_MORE_DOCS;
    
    float score = docScorer.score(docsEnum.docID(), docsEnum.freq());
    
    if (weight.getQuery() instanceof JDONREFv3TermQuery)
    {
        JDONREFv3TermQuery query = (JDONREFv3TermQuery)weight.getQuery();
        
        if (query.getTerm().text().equals("DUHEM"))
            debugbar = true;
        
        HashMap<Integer,Boolean> categories = checker.getCategories(docID(), query.getTerm().text());
        checker.add(categories);
        boolean malus = checker.check();
        if (malus)
            score *= 0.9;
    }
    
    return score;
  }

  /**
   * Advances to the first match beyond the current whose document number is
   * greater than or equal to a given target. <br>
   * The implementation uses {@link DocsEnum#advance(int)}.
   * 
   * @param target
   *          The target document number.
   * @return the matching document or NO_MORE_DOCS if none exist.
   */
  @Override
  public int advance(int target) throws IOException {
    return docsEnum.advance(target);
  }
  
  @Override
  public long cost() {
    return docsEnum.cost();
  }

  /** Returns a string representation of this <code>TermScorer</code>. */
  @Override
  public String toString() { return "scorer(" + weight + ")"; }
}