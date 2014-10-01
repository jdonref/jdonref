package org.elasticsearch.index.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.inject.Inject;

import org.apache.lucene.search.spans.TermVectorMultiPayloadSpanTermQuery;
import org.elasticsearch.common.Strings;

/**
 *
 * @author Julien
 */
public class TermVectorMultiPayloadSpanTermQueryParser extends MultiPayloadSpanTermQueryParser {

    public static String NAME = "span_termvectormultipayloadterm";

    @Override
    public String[] names() {
        return new String[]{NAME, Strings.toCamelCase(NAME)};
    }
    
    @Inject
    public TermVectorMultiPayloadSpanTermQueryParser() {
    }
    
    public Query makeQuery(QueryParseContext parseContext,String fieldName, BytesRef valueBytes,float boost,String queryName)
    {
        TermVectorMultiPayloadSpanTermQuery query = new TermVectorMultiPayloadSpanTermQuery(new Term(fieldName, valueBytes));
        query.setBoost(boost);
        if (queryName != null) {
            parseContext.addNamedQuery(queryName, query);
        }
        return query;
    }
}