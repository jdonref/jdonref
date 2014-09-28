package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermQueryBuilder extends SpanTermQueryBuilder
{
    /* AAAARGH ! */
    protected String protectedName;
    protected Object protectedValue;
    protected float protectedBoost;
    protected String protectedQueryName;
        
    public MultiPayloadSpanTermQueryBuilder(String name, String value) {
        super(name,value);
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermQueryBuilder(String name, int value) {
        super(name,value);
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermQueryBuilder(String name, long value) {
        super(name,value);
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermQueryBuilder(String name, float value) {
        super(name,value);
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermQueryBuilder(String name, double value) {
        super(name,value);
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermQueryBuilder boost(float boost) {
        super.boost(boost);
        this.protectedBoost = boost;
        return this;
    }

    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public MultiPayloadSpanTermQueryBuilder queryName(String queryName) {
        super.queryName(queryName);
        this.protectedQueryName = queryName;
        return this;
    }

    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(MultiPayloadSpanTermQueryParser.NAME);
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