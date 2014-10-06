package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.apache.lucene.search.spans.checkers.PayloadChecker;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.bytes.BytesArray;

/**
 * Construct a filter that only match the document within tokens are grouped by payload
 * 
 * @author Julien
 */
public class PayloadCheckerSpanQueryBuilder extends BaseQueryBuilder implements SpanQueryBuilder
{
    protected ArrayList<SpanQueryBuilder> clauses = new ArrayList<>();
    
    protected PayloadChecker checker = null;
    
    protected String queryName;
    
    protected int termCountPayloadFactor = PayloadCheckerSpanQueryParser.NOTERMCOUNTPAYLOADFACTOR;
    
    /**
     * Define the factor applyed on payloads to get number of tokens by payloads.
     * @param factor
     * @return 
     */
    public PayloadCheckerSpanQueryBuilder termCountPayloadFactor(int factor)
    {
        this.termCountPayloadFactor = factor;
        return this;
    }

    public PayloadCheckerSpanQueryBuilder clause(MultiPayloadSpanTermQueryBuilder clause) {
        clauses.add(clause);
        return this;
    }
    
    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public PayloadCheckerSpanQueryBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    public PayloadCheckerSpanQueryBuilder checker(PayloadChecker checker) {
        this.checker = checker;
        return this;
    }


    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        if (clauses.isEmpty()) {
            throw new ElasticsearchIllegalArgumentException("Must have at least one clause when building a groupedPayloadSpan query");
        }
        
        builder.startObject(PayloadCheckerSpanQueryParser.NAME);
        
        builder.startArray("clauses");
        for (SpanQueryBuilder clause : clauses) {
            clause.toXContent(builder, params);
        }
        builder.endArray();
        
        if (checker!=null)
        {
            builder.startObject("checker");
            PayloadCheckerFactory.getInstance().doXContent(checker,builder,params);
            builder.endObject();
        }

        if (termCountPayloadFactor != PayloadCheckerSpanQueryParser.NOTERMCOUNTPAYLOADFACTOR) {
            builder.field("termcountpayloadfactor", termCountPayloadFactor);
        }
        
        if (queryName != null) {
            builder.field("_name", queryName);
        }
        builder.endObject();
    }
}