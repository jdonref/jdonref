package org.elasticsearch.index.query.jdonrefv4;

import java.io.IOException;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BaseQueryBuilder;

/**
 *
 * @author Julien
 */
public class JDONREFv4QueryBuilder extends BaseQueryBuilder
{
    private final String value;
    
    private String queryName;
    
    protected int debugDoc = -1;
    protected boolean debugMode = JDONREFv4QueryParser.DEFAULTDEBUGMODE;
    
    protected String default_field = JDONREFv4QueryParser.DEFAULTFIELD;
    
    protected boolean progressiveShouldMatch = JDONREFv4QueryParser.DEFAULTPROGRESSIVESHOULDMATCH;
    
    public JDONREFv4QueryBuilder debugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
        return this;
    }
    
    /**
     *  Construct a new JDONREFv3 Query.
     * 
     * @param value The adress to search for.
     */
    public JDONREFv4QueryBuilder(String value)
    {
        this.value = value;
    }
    
    /**
     * Set the debugDoc for this query.
     * 
     * @param debugDoc
     * @return
     */
    public JDONREFv4QueryBuilder debugDoc(int debugDoc)
    {
        this.debugDoc = debugDoc;
        return this;
    }
    
    /**
    * Sets the query name for the filter that can be used when searching for matched_filters per hit.
    */
    public JDONREFv4QueryBuilder queryName(String queryName) {
        this.queryName = queryName;
        return this;
    }
    
    public JDONREFv4QueryBuilder field(String default_field) {
        this.default_field = default_field;
        return this;
    }
    
    public JDONREFv4QueryBuilder progressiveShouldMatch(boolean progressiveShouldMatch)
    {
        this.progressiveShouldMatch = progressiveShouldMatch;
        return this;
    }
    
    static boolean iamhere = false;
    
    
    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(JDONREFv4QueryParser.NAME);
        
        builder.field("value", value);
        if (!JDONREFv4QueryParser.DEFAULTFIELD.equals(default_field))
        {
            builder.field("default_field",default_field);
        }
        if (debugMode!=JDONREFv4QueryParser.DEFAULTDEBUGMODE)
        {
            builder.field("debugMode",debugMode);
        }
        if (progressiveShouldMatch!=JDONREFv4QueryParser.DEFAULTPROGRESSIVESHOULDMATCH)
        {
            builder.field("progressive_should_match",progressiveShouldMatch);
        }
        if (debugDoc!=-1)
        {
            builder.field("debugDoc",debugDoc);
        }
        if (queryName != null)
        {
            builder.field("_name", queryName);
        }
        builder.endObject();
    }
}
