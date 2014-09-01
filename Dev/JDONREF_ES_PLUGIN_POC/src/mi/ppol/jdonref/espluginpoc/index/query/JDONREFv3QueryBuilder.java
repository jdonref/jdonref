package mi.ppol.jdonref.espluginpoc.index.query;

import java.io.IOException;
import org.apache.lucene.search.JDONREFv3Query;
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
    
    private int mode = JDONREFv3Query.AUTOCOMPLETE;
    
    protected int debugDoc = -1;
    
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
     * Set the mode for this query.
     * BULK => les scores sont relatifs aux documents trouvés (qualité de la notation)
     * AUTOCOMPLETE => les scores sont absolus (performance)
     * 
     * @param mode
     * @return
     */
    public JDONREFv3QueryBuilder mode(int mode)
    {
        this.mode = mode;
        return this;
    }
    
    /**
     * Set the mode for this query.
     * BULK => les scores sont relatifs aux documents trouvés (qualité de la notation)
     * AUTOCOMPLETE => les scores sont absolus (performance)
     * 
     * @param mode
     * @return
     */
    public JDONREFv3QueryBuilder debugDoc(int debugDoc)
    {
        this.debugDoc = debugDoc;
        return this;
    }
    
    /**
    * Sets the query name for the filter that can be used when searching for matched_filters per hit.
    */
    public JDONREFv3QueryBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    static boolean iamhere = false;
    
    
    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(JDONREFv3QueryParser.NAME);
        
        builder.field("value", value);
        builder.field("mode",mode==JDONREFv3Query.BULK?"bulk":"autocomplete");
        if (debugDoc!=-1)
        {
            builder.field("debugDoc",debugDoc);
        }
        if (boost != -1)
        {
            builder.field("boost", boost);
        }
        if (queryName != null)
        {
            builder.field("_name", queryName);
        }
        builder.endObject();
    }
}
