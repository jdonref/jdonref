package org.elasticsearch.index.query;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;

/**
 * Construct a filter that only match the document within tokens are grouped by payload
 * 
 * @author Julien
 */
public class PayloadVersusTypeSpanQueryBuilder extends BaseQueryBuilder implements SpanQueryBuilder
{
    protected ArrayList<SpanQueryBuilder> clauses = new ArrayList<>();
    
    protected Hashtable<String,BytesRef[]> requiredPayloads = new Hashtable<>();
    
    protected String queryName;
    
    protected int termCountPayloadFactor = PayloadVersusTypeSpanQueryParser.NOTERMCOUNTPAYLOADFACTOR;
    
    /**
     * Define the factor applyed on payloads to get number of tokens by payloads.
     * @param factor
     * @return 
     */
    public PayloadVersusTypeSpanQueryBuilder termCountPayloadFactor(int factor)
    {
        this.termCountPayloadFactor = factor;
        return this;
    }

    public PayloadVersusTypeSpanQueryBuilder clause(TermVectorMultiPayloadSpanTermQueryBuilder clause) {
        clauses.add(clause);
        return this;
    }
    
    /**
     * Sets the query name for the filter that can be used when searching for matched_filters per hit.
     */
    public PayloadVersusTypeSpanQueryBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    public PayloadVersusTypeSpanQueryBuilder requiredPayloads(String type,BytesRef[] payloads)
    {
        requiredPayloads.put(type,payloads);
        return this;
    }


    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        if (clauses.isEmpty()) {
            throw new ElasticsearchIllegalArgumentException("Must have at least one clause when building a groupedPayloadSpan query");
        }
        
        builder.startObject(PayloadVersusTypeSpanQueryParser.NAME);
        
        builder.startArray("clauses");
        for (SpanQueryBuilder clause : clauses) {
            clause.toXContent(builder, params);
        }
        builder.endArray();
        
        builder.startArray("requiredPayloads");
        Enumeration<String> keys = requiredPayloads.keys();
        
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            
            builder.startObject();
            builder.field("type",key);
            
            builder.startArray("payloads");
            BytesRef[] payloads = requiredPayloads.get(key);
            for(int i=0;i<payloads.length;i++)
            {
                BytesArray ref = new BytesArray(payloads[i]);
                builder.value(ref);
            }
            builder.endArray();
            builder.endObject();
        }
        builder.endArray();

        if (termCountPayloadFactor != PayloadVersusTypeSpanQueryParser.NOTERMCOUNTPAYLOADFACTOR) {
            builder.field("termcountpayloadfactor", termCountPayloadFactor);
        }
        
        if (queryName != null) {
            builder.field("_name", queryName);
        }
        builder.endObject();
    }
}