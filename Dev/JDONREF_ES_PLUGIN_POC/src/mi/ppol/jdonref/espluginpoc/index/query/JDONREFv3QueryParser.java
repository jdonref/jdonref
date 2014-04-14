package mi.ppol.jdonref.espluginpoc.index.query;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.BytesRefs;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.mapper.ParseContext;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MatchQueryParser;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.index.query.QueryParser;
import org.elasticsearch.index.query.QueryParsingException;

import org.elasticsearch.index.search.MatchQuery;
import static org.elasticsearch.index.query.support.QueryParsers.wrapSmartNameQuery;

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
            MatchQuery mq = new MatchQuery(parseContext);
            query = mq.parse(MatchQuery.Type.BOOLEAN,"ligne7",value);
            //query = new TermQuery(new Term("ligne7",BytesRefs.toBytesRef(value)));
            
            query.setBoost(boost);
            
            if (filterName != null) {
                parseContext.addNamedQuery(filterName, query);
            }
        }
        
        System.out.println("query : "+query.toString());
        
        return query;
    }
    
    protected Analyzer getAnalyzer(FieldMapper mapper, MapperService.SmartNameFieldMappers smartNameFieldMappers,QueryParseContext parseContext) {
        Analyzer analyzer = null;
        if (mapper != null) {
                analyzer = mapper.searchAnalyzer();
        }
            if (analyzer == null && smartNameFieldMappers != null) {
                analyzer = smartNameFieldMappers.searchAnalyzer();
            }
            if (analyzer == null) {
                analyzer = parseContext.mapperService().searchAnalyzer();
            }
        return analyzer;
    }
}