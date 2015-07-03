package org.elasticsearch.index.query;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.search.spans.MultiPayloadSpanTermFilter;
import org.apache.lucene.search.spans.PayloadCheckerSpanFilter;
import org.apache.lucene.search.spans.checkers.AbstractPayloadChecker;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * Construct a filter that only match the document within tokens are grouped by payload
 * 
 * @author Julien
 */
public class PayloadCheckerSpanFilterBuilder extends BaseFilterBuilder
{
    protected ArrayList<MultiPayloadSpanTermFilterBuilder> clauses = new ArrayList<>();
    
    protected AbstractPayloadChecker checker = null;
    
    protected String queryName;
    
    protected int limit = -1;
    
    protected int termCountPayloadFactor = PayloadCheckerSpanFilter.NOTERMCOUNTPAYLOADFACTOR;
    
    /**
     * Do not return results if over limit documents should be scanned.
     * @param limit -1 = no limit (default value)
     * @return 
     */
    public PayloadCheckerSpanFilterBuilder limit(int limit)
    {
        this.limit = limit;
        return this;
    }
    
    /**
     * Define the factor applyed on payloads to get number of tokens by payloads.
     * @param factor
     * @return 
     */
    public PayloadCheckerSpanFilterBuilder termCountPayloadFactor(int factor)
    {
        this.termCountPayloadFactor = factor;
        return this;
    }

    public PayloadCheckerSpanFilterBuilder clause(MultiPayloadSpanTermFilterBuilder clause) {
        clauses.add(clause);
        return this;
    }
    
    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public PayloadCheckerSpanFilterBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    public PayloadCheckerSpanFilterBuilder checker(AbstractPayloadChecker checker) {
        this.checker = checker;
        return this;
    }


    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        if (clauses.isEmpty()) {
            throw new ElasticsearchIllegalArgumentException("Must have at least one clause when building a payloadcheckerSpanQuery query");
        }
        
        builder.startObject(PayloadCheckerSpanFilterParser.NAME);
        
        if (limit!=-1)
        {
            builder.field("limit",limit);
        }
        
        builder.startArray("clauses");
        for (MultiPayloadSpanTermFilterBuilder clause : clauses) {
            clause.toXContent(builder, params);
        }
        builder.endArray();
        
        if (checker!=null)
        {
            builder.startObject("checker");
            PayloadCheckerFactory.getInstance().doXContent(checker,builder,params);
            builder.endObject();
        }

        if (termCountPayloadFactor != PayloadCheckerSpanFilter.NOTERMCOUNTPAYLOADFACTOR) {
            builder.field("termcountpayloadfactor", termCountPayloadFactor);
        }
        
        if (queryName != null) {
            builder.field("_name", queryName);
        }
        builder.endObject();
    }
}