package org.apache.lucene.search.spans;

import java.util.Arrays;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.InPlaceMergeSorter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.util.BytesRef;

/**
 * A Spans that is formed from the subspans of a GroupedPayloadSpanQuery
 * where the subspans are grouped by payload values.
 * 
 * @author Julien
 */
public class SpansPayloadVersusType extends Spans
{
  protected boolean firstTime = true;
  protected boolean more = false;

  /** The spans in the same order as the SpanNearQuery */
  protected final MultiPayloadTermSpans[] subSpans;

  /** Indicates that all subSpans have same doc() */
  protected boolean inSameDoc = false;

  protected int matchDoc = -1;
  protected int matchStart = -1;
  protected int matchEnd = -1;
  protected List<byte[]> matchPayload;

  protected final MultiPayloadTermSpans[] subSpansByDoc;
  protected ConcurrentHashMap<String,BytesRef[]> requiredPayloads;
  protected String field = "_type";
  
  public void setField(String field)
  {
      this.field = field;
  }

  public void setRequiredPayloads(ConcurrentHashMap<String, BytesRef[]> requiredPayloads) {
        this.requiredPayloads = requiredPayloads;
  }

  // Even though the array is probably almost sorted, InPlaceMergeSorter will likely
  // perform better since it has a lower overhead than TimSorter for small arrays
  private final InPlaceMergeSorter sorter = new InPlaceMergeSorter() {
    @Override
    protected void swap(int i, int j) {
      ArrayUtil.swap(subSpansByDoc, i, j);
    }
    @Override
    protected int compare(int i, int j) {
      return subSpansByDoc[i].doc() - subSpansByDoc[j].doc();
    }
  };

  protected PayloadVersusTypeSpanQuery query;
  
  public SpansPayloadVersusType(PayloadVersusTypeSpanQuery gpsQuery, AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException {
    MultiPayloadSpanTermQuery[] clauses = gpsQuery.getClauses();
    if (clauses.length<1)
        throw new IllegalArgumentException("Less than 1 clauses: "
                                         + gpsQuery);
    subSpans = new MultiPayloadTermSpans[clauses.length];
    matchPayload = new LinkedList<>();
    subSpansByDoc = new MultiPayloadTermSpans[clauses.length];
    for (int i = 0; i < clauses.length; i++) {
      subSpans[i] = clauses[i].getSpans(context, acceptDocs, termContexts);
      subSpansByDoc[i] = subSpans[i]; // used in toSameDoc()
    }
    query = gpsQuery; // kept for toString() only.
  }

  // inherit javadocs
  @Override
  public int doc() { return matchDoc; }

  // inherit javadocs
  @Override
  public int start() { return matchStart; }

  // inherit javadocs
  @Override
  public int end() { return matchEnd; }
  
  public Spans[] getSubSpans() {
    return subSpans;
  }  

  // TODO: Remove warning after API has been finalized
  // TODO: Would be nice to be able to lazy load payloads
  @Override
  public Collection<byte[]> getPayload() throws IOException {
    return matchPayload;
  }

  // TODO: Remove warning after API has been finalized
  @Override
  public boolean isPayloadAvailable() {
    return matchPayload.isEmpty() == false;
  }

  @Override
  // TODO: understand what to do with that
  public long cost() {
    long minCost = Long.MAX_VALUE;
    for (int i = 0; i < subSpans.length; i++) {
      minCost = Math.min(minCost, subSpans[i].cost());
    }
    return minCost;
  }

  // inherit javadocs
  @Override
  public boolean next() throws IOException {

      firstTime = false;
      for (int i = 0; i < subSpans.length; i++) {
        if (! subSpans[i].next()) {
          more = false;
          return false;
        }
      }
      more = true;
      
    matchPayload.clear();
    return advanceAfterOrdered();
  }

  // inherit javadocs
  @Override
  public boolean skipTo(int target) throws IOException {
    if (firstTime) {
      firstTime = false;
      for (int i = 0; i < subSpans.length; i++) {
        if (! subSpans[i].skipTo(target)) {
          more = false;
          return false;
        }
      }
      more = true;
    } else if (more && (subSpans[0].doc() < target)) {
      if (subSpans[0].skipTo(target)) {
        inSameDoc = false;
      } else {
        more = false;
        return false;
      }
    }
    matchPayload.clear();
    return advanceAfterOrdered();
  }
  
  /** Advances the subSpans to just after payload values are grouped together.
   * @return true if there is such a match.
   */
  private boolean advanceAfterOrdered() throws IOException {
    while (more && (inSameDoc || toSameDoc())) {
      if (stretchToOrder()) {
        return true;
      }
    }
    return false; // no more matches
  }


  /** Advance the subSpans to the same document */
  private boolean toSameDoc() throws IOException {
    sorter.sort(0, subSpansByDoc.length);
    int firstIndex = 0;
    int maxDoc = subSpansByDoc[subSpansByDoc.length - 1].doc();
    while (subSpansByDoc[firstIndex].doc() != maxDoc) {
      if (! subSpansByDoc[firstIndex].skipTo(maxDoc)) {
        more = false;
        inSameDoc = false;
        return false;
      }
      maxDoc = subSpansByDoc[firstIndex].doc();
      if (++firstIndex == subSpansByDoc.length) {
        firstIndex = 0;
      }
    }
    for (int i = 0; i < subSpansByDoc.length; i++) {
      assert (subSpansByDoc[i].doc() == maxDoc)
             : " SpansGroupedByPayload.toSameDoc() spans " + subSpansByDoc[0]
                                 + "\n at doc " + subSpansByDoc[i].doc()
                                 + ", but should be at " + maxDoc;
    }
    inSameDoc = true;
    return true;
  }
  
  ConcurrentHashMap<byte[],Integer> countPayload = new ConcurrentHashMap<>();
  ConcurrentHashMap<byte[],Integer> totalCountPayload = new ConcurrentHashMap<>();
  
  protected boolean checkNumTokenByPayloads(BytesRef[] payloads) throws IOException
  {
      countPayload.clear();
      totalCountPayload.clear();
      
      for(int i=0;i<subSpans.length;i++)
      {
          Collection<byte[]> payloads_i = subSpans[i].getPayload();
          Collection<Integer> termCountsByPayload = subSpans[i].termCountsByPayload();
          Iterator<byte[]> iterator = payloads_i.iterator();
          Iterator<Integer> termCountsIterator = termCountsByPayload.iterator();
          
          int j=0;
          while(iterator.hasNext())
          {
              byte[] payload_j = iterator.next();
              for(int k=0;k<payloads.length;k++)
              {
                byte[] payload_k = payloads[k].bytes;
                if (Arrays.equals(payload_k, payload_j))
                {
                    Integer count = countPayload.get(payload_k);
                    if (count==null) count=1;
                    else count++;
                    countPayload.put(payload_k,count);
                    totalCountPayload.put(payload_k,termCountsIterator.next());
                }
              }
              j++;
          }
      }
      
      for(int k=0;k<payloads.length;k++)
      {
          int count = countPayload.get(payloads[k].bytes);
          int total = totalCountPayload.get(payloads[k].bytes);
          if (count==0 || count!=total)
              return false;
      }
      return true;
  }

  protected boolean checkNumTokenByOnePayload(BytesRef payload) throws IOException
  {
      int count = 0;
      int total = 0;
      byte[] payload_k = payload.bytes;
      for(int i=0;i<subSpans.length;i++)
      {
          Collection<byte[]> payloads_i = subSpans[i].getPayload();
          Collection<Integer> termCountsByPayload = subSpans[i].termCountsByPayload();
          Iterator<byte[]> iterator = payloads_i.iterator();
          Iterator<Integer> termCountsIterator = termCountsByPayload.iterator();
          
          int j=0;
          while(iterator.hasNext())
          {
              byte[] payload_j = iterator.next();
              if (Arrays.equals(payload_k, payload_j))
              {
                  count++;
                  total = termCountsIterator.next();
              }
              j++;
          }
      }

      return count>0 && total==count;
  }
  
  /**
   * Check subSpans for payload by type
   * @param lastpayloads
   * @param index
   * @return 
   */
  protected boolean checkPayloads(BytesRef[] payloads) throws IOException
  {
      if (payloads.length==0)
          return true;
      if (payloads.length==1)
          return checkNumTokenByOnePayload(payloads[0]);
      
      return checkNumTokenByPayloads(payloads);
  }
  
  /**
   * Check subSpans for payload by one field
   * @param lastpayloads
   * @param index
   * @return 
   */
  protected boolean checkPayloadsByOneField(String field) throws IOException
  {
      Document d = subSpans[0].document();
      
      String[] types = d.getValues(field);
      boolean checked = true;
      
      for(int i=0;i<types.length;i++)
      {
          String type = types[i];
          if (requiredPayloads.get(type)!=null)
          {
              // TODO : improve check
              checked &= checkPayloads(requiredPayloads.get(type));
          }
      }
      
      return checked;
  }
  
  /**
   * Check subSpans for payload by one field
   * @param lastpayloads
   * @param index
   * @return 
   */
  protected boolean checkPayloads() throws IOException
  {
      if (requiredPayloads!=null)
          return checkPayloadsByOneField(field);
      return true;
  }
  
  /** Order the subSpans within the same document by advancing all later spans
   * after the previous one.
   */
  private boolean stretchToOrder() throws IOException {
    matchDoc = subSpans[0].doc();
    
    if (!checkPayloads())
    {
        if (!subSpans[0].next())
        {
            more = false;
        }
        inSameDoc = false;
    }
    
    return inSameDoc;
  }

  @Override
  public String toString() {
    return getClass().getName() + "("+query.toString()+")@"+
      (firstTime?"START":(more?(doc()+":"+start()+"-"+end()):"END"));
  }
}