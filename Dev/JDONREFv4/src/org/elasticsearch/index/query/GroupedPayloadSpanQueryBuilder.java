package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import org.elasticsearch.ElasticsearchIllegalArgumentException;

/**
 * Construct a filter that only match the document within tokens are grouped by payload
 * 
 * @author Julien
 */
public class GroupedPayloadSpanQueryBuilder extends BaseQueryBuilder implements SpanQueryBuilder
{
    protected ArrayList<SpanQueryBuilder> clauses = new ArrayList<>();
    
    protected String queryName;

    public GroupedPayloadSpanQueryBuilder clause(MultiPayloadSpanTermQueryBuilder clause) {
        clauses.add(clause);
        return this;
    }
    
    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public GroupedPayloadSpanQueryBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }


    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        if (clauses.isEmpty()) {
            throw new ElasticsearchIllegalArgumentException("Must have at least one clause when building a groupedPayloadSpan query");
        }
        
        builder.startObject(GroupedPayloadSpanQueryParser.NAME);
        
        builder.startArray("clauses");
        for (SpanQueryBuilder clause : clauses) {
            clause.toXContent(builder, params);
        }
        builder.endArray();
        if (queryName != null) {
            builder.field("_name", queryName);
        }
        builder.endObject();
    }
}