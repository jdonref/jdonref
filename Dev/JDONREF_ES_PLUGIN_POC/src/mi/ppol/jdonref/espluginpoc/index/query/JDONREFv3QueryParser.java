package mi.ppol.jdonref.espluginpoc.index.query;

import java.io.IOException;
import java.util.Hashtable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.index.query.QueryParser;
import org.elasticsearch.index.query.QueryParsingException;

import org.elasticsearch.index.search.MatchQuery;

/**
 *
 * @author Julien
 */
public class JDONREFv3QueryParser implements QueryParser
{
    public static final String NAME = "jdonrefv3es";
    
    @Nullable
    private final ClusterService clusterService;
    
     public JDONREFv3QueryParser()
     {
          clusterService = null;
     }
    
    @Inject
    public JDONREFv3QueryParser(@Nullable ClusterService clusterService) {
        this.clusterService = clusterService;
    }
    
    @Override
    public String[] names() {
        return new String[]{NAME};
    }
    
    @Override
    public Query parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();

        Object value = null;
        float boost = 1.0f;

        String filterName = null;
        String currentFieldName = null;
        XContentParser.Token token;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME)
            {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_OBJECT)
            {
                if ("value".equals(currentFieldName)) {
                } else if ("_name".equals(currentFieldName)) {
                } else if ("boost".equals(currentFieldName)) {
                } else {
                    throw new QueryParsingException(parseContext.index(), "[term] filter does not support [" + currentFieldName + "]");
                }
            }
            else if (token.isValue())
            {
                if ("_name".equals(currentFieldName)) {
                    filterName = parser.text();
                } else if ("boost".equals(currentFieldName)) {
                    boost = parser.floatValue();
                } else if ("value".equals(currentFieldName)) {
                    value = parser.text();
                } else {
                    throw new QueryParsingException(parseContext.index(), "[boosting] query does not support [" + currentFieldName + "]");
                }
            }
        }

        if (value == null) {
            throw new QueryParsingException(parseContext.index(), "No value specified for term filter");
        }

        Query query = null;
        
        if (query == null) {
            query = getQueryExact((String)value,parseContext);
            query.setBoost(boost);
            
            if (filterName != null) {
                parseContext.addNamedQuery(filterName, query);
            }
        }
        
        return query;
    }
    
    public boolean isInt(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }
    
    public void addMatchQueryClause(BooleanQuery booleanQuery,MatchQuery mq,String attr,String value,float boost,Occur occur) throws IOException
    {
        Query query = mq.parse(MatchQuery.Type.BOOLEAN,attr,value);
        if (query!=null)
        {
              query.setBoost(boost);
              booleanQuery.add(new BooleanClause(query,occur));
        }
    }
    
    public Query getQueryExact(String find, QueryParseContext parseContext) throws IOException
    {
        Hashtable<String,Boolean> hash = new Hashtable<String,Boolean>();
        
        MatchQuery mq = new MatchQuery(parseContext);
        BooleanQuery booleanQuery = new BooleanQuery();
        
        boolean isThereInt = false;
        
        String[] splitted = find.split(" ");
        for(int i=0;i<splitted.length;i++)
        {
            String stri = splitted[i];
            
            if (hash.get(stri)==null)
            {
                hash.put(stri,true);
                
                if (isInt(stri))
                {
                    isThereInt = true;

                    if (stri.length()==5)
                    {
                        addMatchQueryClause(booleanQuery,mq,"code_insee",stri,2,BooleanClause.Occur.SHOULD);
                    }
                    if (stri.length()==5)
                    {
                        addMatchQueryClause(booleanQuery,mq,"code_postal",stri,2,BooleanClause.Occur.SHOULD);
                    }
                    float dptBoost = 1.0f;
                    if (i==0)
                        dptBoost = 0.5f;
                    if (i>0)
                        dptBoost = 2.0f;
                    addMatchQueryClause(booleanQuery,mq,"code_departement",stri,dptBoost,BooleanClause.Occur.SHOULD);
                    
                    float ardtBoost = 1.0f;
                    if (i==0)
                        ardtBoost = 0.5f;
                    if (i>0)
                        ardtBoost = 2.0f;
                    addMatchQueryClause(booleanQuery,mq,"code_arrondissement",stri,ardtBoost,BooleanClause.Occur.SHOULD);
                    
                    float numBoost = 1.0f;
                    if (i>0)
                        numBoost = 2.5f;
                    addMatchQueryClause(booleanQuery,mq,"numero",stri,numBoost,BooleanClause.Occur.SHOULD);
                }
                else
                {
                    addMatchQueryClause(booleanQuery,mq,"fullName",stri,10,BooleanClause.Occur.SHOULD);
                }
            }
        }
        
        if (!isThereInt)
        {
            addMatchQueryClause(booleanQuery,mq,"numero","0",1.0f,BooleanClause.Occur.SHOULD);
        }
        
        return booleanQuery;
    }
}