package org.apache.lucene.search.similarities;

import java.io.IOException;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.util.BytesRef;

/**
 * 
 * @author Julien
 */
public class JDONREFv4TermSimilarity extends DefaultSimilarity
{
    /**
     * Retourne vrai si le poids de tous les champs du terme doivent être cumulés.
     * Retourne faux si seul le poids le plus élevé de tous les champs du terme doit être pris en compte.
     * @param term
     * @return
     */
    public static boolean cumuler(String term)
    {
        if (term.equals("codes")) return false;
        
        return true;
    }
    
    public class JDONREFv3TermSimScorer extends TFIDFSimilarity.SimScorer
    {
        private final JDONREFv3TermStats stats;
        private final float weightValue;
        private final NumericDocValues norms;

        JDONREFv3TermSimScorer(JDONREFv3TermStats stats, NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.weightValue = stats.value;
            this.norms = norms;
        }

        @Override
        public float score(int doc, float freq) {
            
            if (!cumuler(stats.getField())) freq = 1.0f;
            
            final float raw = (tf(freq)/freq) * weightValue; // compute (tf(f)/f)*weight

            return norms == null ? raw : raw * decodeNormValue(norms.get(doc));  // normalize for field
        }

        @Override
        public float computeSlopFactor(int distance) {
            return sloppyFreq(distance);
        }

        @Override
        public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return scorePayload(doc, start, end, payload);
        }

        @Override
        public Explanation explain(int doc, Explanation freq) {
            return explainScore(doc, freq, stats, norms);
        }
    }
    
    public class JDONREFv3TermStats extends SimWeight {
        protected final String field;
        /** The idf and its explanation */
        protected final Explanation idf;
        protected float queryNorm;
        protected float queryWeight;
        protected final float queryBoost;
        protected float value;
        
        public String getField()
        {
            return field;
        }
        
        public JDONREFv3TermStats(String field, Explanation idf, float queryBoost) {
            // TODO: Validate?
            this.field = field;
            this.idf = idf;
            this.queryBoost = queryBoost;
            this.queryWeight = idf.getValue() * queryBoost; // compute query weight
        }

        @Override
        public float getValueForNormalization() {
            // TODO: (sorta LUCENE-1907) make non-static class and expose this squaring via a nice method to subclasses?
            return queryWeight * queryWeight;  // sum of squared weights

        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.queryNorm = queryNorm * topLevelBoost;
            queryWeight *= this.queryNorm;              // normalize query weight

            value = queryWeight * idf.getValue();         // idf for document

        }
  }  

  protected Explanation explainScore(int doc, Explanation freq, JDONREFv3TermStats stats, NumericDocValues norms) {
    Explanation result = new Explanation();
    result.setDescription("score(doc="+doc+",freq="+freq+"), product of:");

    // explain query weight
    Explanation queryExpl = new Explanation();
    queryExpl.setDescription("queryWeight, product of:");

    Explanation boostExpl = new Explanation(stats.queryBoost, "boost");
    if (stats.queryBoost != 1.0f)
      queryExpl.addDetail(boostExpl);
    queryExpl.addDetail(stats.idf);

    Explanation queryNormExpl = new Explanation(stats.queryNorm,"queryNorm");
    queryExpl.addDetail(queryNormExpl);

    queryExpl.setValue(boostExpl.getValue() *
                       stats.idf.getValue() *
                       queryNormExpl.getValue());

    result.addDetail(queryExpl);

    // explain field weight
    Explanation fieldExpl = new Explanation();
    fieldExpl.setDescription("fieldWeight in "+doc+
                             ", product of:");

    Explanation tfExplanation = new Explanation();
    float freqValue = cumuler(stats.getField())?freq.getValue():1.0f;
    tfExplanation.setValue(tf(freqValue)/freqValue);
    tfExplanation.setDescription("tf(freq="+freq.getValue()+")/freq, with freq of:");
    tfExplanation.addDetail(freq);
    fieldExpl.addDetail(tfExplanation);
    fieldExpl.addDetail(stats.idf);
 
    Explanation fieldNormExpl = new Explanation();
    float fieldNorm = norms != null ? decodeNormValue(norms.get(doc)) : 1.0f;
    fieldNormExpl.setValue(fieldNorm);
    fieldNormExpl.setDescription("fieldNorm(doc="+doc+")");
    fieldExpl.addDetail(fieldNormExpl);
    
    fieldExpl.setValue(tfExplanation.getValue() *
                       stats.idf.getValue() *
                       fieldNormExpl.getValue());

    result.addDetail(fieldExpl);
    
    // combine them
    result.setValue(queryExpl.getValue() * fieldExpl.getValue());

    if (queryExpl.getValue() == 1.0f)
      return fieldExpl;

    return result;
  }
}