package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 *
 * @author Julien
 */
public class TermVectorMultiPayloadSpanTermQueryBuilder extends MultiPayloadSpanTermQueryBuilder
{
    public TermVectorMultiPayloadSpanTermQueryBuilder(String name, String value) {
        super(name,value);
    }

    public TermVectorMultiPayloadSpanTermQueryBuilder(String name, int value) {
        super(name,value);
    }

    public TermVectorMultiPayloadSpanTermQueryBuilder(String name, long value) {
        super(name,value);
    }

    public TermVectorMultiPayloadSpanTermQueryBuilder(String name, float value) {
        super(name,value);
    }

    public TermVectorMultiPayloadSpanTermQueryBuilder(String name, double value) {
        super(name,value);
    }

    public TermVectorMultiPayloadSpanTermQueryBuilder boost(float boost) {
        super.boost(boost);
        return this;
    }

    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public TermVectorMultiPayloadSpanTermQueryBuilder queryName(String queryName) {
        super.queryName(queryName);
        return this;
    }

    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(TermVectorMultiPayloadSpanTermQueryParser.NAME);
        if (protectedBoost == -1 && protectedQueryName != null) {
            builder.field(protectedName, protectedValue);
        } else {
            builder.startObject(protectedName);
            builder.field("value", protectedValue);
            if (protectedBoost != -1) {
                builder.field("boost", protectedBoost);
            }
            if (protectedQueryName != null) {
                builder.field("_name", protectedQueryName);
            }
            builder.endObject();
        }
        builder.endObject();
    }
}