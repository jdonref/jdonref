package org.elasticsearch.index.query;

import java.io.IOException;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermFilterBuilder extends BaseFilterBuilder
{
    /* AAAARGH ! */
    protected String protectedName;
    protected Object protectedValue;
    protected float protectedBoost;
    protected String protectedFilterName;
    
    protected boolean checked = true;
    
    public MultiPayloadSpanTermFilterBuilder(String name, String value) {
        super();
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermFilterBuilder(String name, int value) {
        super();
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermFilterBuilder(String name, long value) {
        super();
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermFilterBuilder(String name, float value) {
        super();
        this.protectedName = name;
        this.protectedValue = value;
    }

    public MultiPayloadSpanTermFilterBuilder(String name, double value) {
        super();
        this.protectedName = name;
        this.protectedValue = value;
    }
    
    public MultiPayloadSpanTermFilterBuilder checked(boolean value)
    {
        this.checked = value;
        return this;
    }

    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public MultiPayloadSpanTermFilterBuilder filterName(String filterName) {
        //super.queryName(queryName);
        this.protectedFilterName = filterName;
        return this;
    }

    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(MultiPayloadSpanTermFilterParser.NAME);
        if (protectedBoost == -1 && protectedFilterName != null) {
            builder.field(protectedName, protectedValue);
        } else {
            builder.startObject(protectedName);
            builder.field("value", protectedValue);
            if (!checked)
                builder.field("checked",false);
            if (protectedBoost != -1) {
                builder.field("boost", protectedBoost);
            }
            if (protectedFilterName != null) {
                builder.field("_name", protectedFilterName);
            }
            builder.endObject();
        }
        builder.endObject();
    }
}