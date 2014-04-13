package mi.ppol.jdonref.lucenepoc.index.query;

import java.io.IOException;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.BoostableQueryBuilder;

/**
 *
 * @author Julien
 */
public class JDONREFv3QueryBuilder extends BaseQueryBuilder implements BoostableQueryBuilder<JDONREFv3QueryBuilder>
{
    private final String value;
    
    private float boost = -1;
    
    private String queryName;
    
    /**
     *  Construct a new JDONREFv3 Query.
     * 
     * @param value The adress to search for.
     */
    public JDONREFv3QueryBuilder(String value)
    {
        this.value = value;
    }
    
    /**
    * Sets the boost for this query. Documents matching this query will (in addition to the normal
    * weightings) have their score multiplied by the boost provided.
    */
    public JDONREFv3QueryBuilder boost(float boost) {
        this.boost = boost;
        return this;
    }
    
    /**
    * Sets the query name for the filter that can be used when searching for matched_filters per hit.
    */
    public JDONREFv3QueryBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(JDONREFv3QueryParser.NAME);
        
        builder.field("value", value);
        if (boost != -1)
        {
            builder.field("boost", boost);
        }
        if (queryName != null) {
            builder.field("_name", queryName);
        }
        
        builder.endObject();
    }
}
