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
  protected boolean last;
  
  public boolean isLast()
  {
      return last;
  }
  
  public void setIsLast()
  {
      last = true;
  }

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
  public JDONREFv3TermScorer(Weight weight, DocsEnum td, Similarity.SimScorer docScorer, boolean last) {
    super(weight);
    this.docScorer = docScorer;
    this.docsEnum = td;
    this.last = last;
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
  
  double malus;
  
  public double getMalus() {
      return malus;
  }
  
  boolean adressNumberPresent = false;

  public boolean getAdressNumberPresent()
  {
      return adressNumberPresent;
  }
  
  @Override
  public float score() throws IOException
  {
    assert docID() != NO_MORE_DOCS;
    
    float score = docScorer.score(docsEnum.docID(), docsEnum.freq());
    
    malus = 1;
    
    if (weight.getQuery() instanceof JDONREFv3TermQuery)
    {
        JDONREFv3TermQuery query = (JDONREFv3TermQuery)weight.getQuery();
        
        HashMap<Integer,Boolean> categories = checker.getCategories(docID(), query.getTerm().text());
        checker.add(categories);
        
        malus = malus();
        
        adressNumberPresent = checkAdressNumberPresent();
        
        adressType = checkAdressType(docID());
    
        checker.next();
    }
    
    return score;
  }

  public float malus() throws IOException
  {
        float lmalus = 1.0f;
        boolean malusOrder = !checker.checkOrder();
        if (malusOrder)
            lmalus *= JDONREFv3Scorer.ORDERMALUS;
        boolean malusNumber = checker.checkOtherNumberBeforeAdresse();
        if (malusNumber)
            lmalus *= JDONREFv3Scorer.ORDERMALUS;
        return lmalus;
  }
  
  boolean adressType = false;
  
  protected boolean getAdressType()
  {
      return adressType;
  }
  
  protected boolean checkAdressNumberPresent()
  {
        return checker.checkAdressNumberPresent();
  }
  
  
  protected boolean checkAdressType(int docID) throws IOException
  {
        return checker.isAdressType(docID);
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