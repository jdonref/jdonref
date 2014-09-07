package mi.ppol.jdonref.espluginpoc.index.query;

import java.io.IOException;
import java.util.Hashtable;
import mi.ppol.jdonref.espluginpoc.common.lucene.search.MyLimitFilter;
import org.apache.log4j.Logger;
import org.apache.lucene.search.JDONREFv3Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.JDONREFv3TermQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.DocumentMapper;
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
    public static final int DEFAULTMAXSIZE = 300;
    
    private Settings settings;
    
    @Nullable
    private final ClusterService clusterService;
    
    protected Hashtable<String,Integer> termIndex = new Hashtable();
    
     public JDONREFv3QueryParser()
     {
          clusterService = null;
          termIndex.put("ligne4",0);
          termIndex.put("commune",1);
          termIndex.put("codes",2);
          termIndex.put("ligne7",3);
          termIndex.put("code_pays",4);
          termIndex.put("ligne1",5);
     }
    
    @Inject
    public JDONREFv3QueryParser(@Nullable ClusterService clusterService,Settings settings) {
        this.clusterService = clusterService;
        this.settings = settings;
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
        int debugDoc = -1;
        int maxSizePerType = DEFAULTMAXSIZE;
        int mode = JDONREFv3Query.AUTOCOMPLETE;

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
                } else if ("mode".equals(currentFieldName)) {
                } else if ("_name".equals(currentFieldName)) {
                } else if ("debugDoc".equals(currentFieldName)) {
                } else if ("boost".equals(currentFieldName)) {
                } else {
                    throw new QueryParsingException(parseContext.index(), "[jdonrefv3es] query does not support [" + currentFieldName + "]");
                }
            }
            else if (token.isValue())
            {
                if ("_name".equals(currentFieldName)) {
                    filterName = parser.text();
                } else if ("mode".equals(currentFieldName)) {
                    String modeStr = parser.text();
                    if (modeStr.equals("bulk")) mode = JDONREFv3Query.BULK;
                    else if (modeStr.equals("bulk")) mode = JDONREFv3Query.AUTOCOMPLETE;
                    else throw new QueryParsingException(parseContext.index(), "[jdonrefv3es] query does not support "+modeStr+" for [" + currentFieldName + "]");
                } else if ("boost".equals(currentFieldName)) {
                    boost = parser.floatValue();
                } else if ("maxSizePerType".equals(currentFieldName)) {
                    maxSizePerType = parser.intValue();
                } else if ("debugDoc".equals(currentFieldName)) {
                    debugDoc = parser.intValue();
                } else if ("value".equals(currentFieldName)) {
                    value = parser.text();
                } else if ("query".equals(currentFieldName)) {
                } else if ("default_field".equals(currentFieldName)) {
                } else {
                    throw new QueryParsingException(parseContext.index(), "[jdonrefv3es] query does not support [" + currentFieldName + "]");
                }
            }
        }

        if (value == null) {
            throw new QueryParsingException(parseContext.index(), "No value specified for term filter");
        }

        Query query = null;
        
        if (query == null) {
            query = getQueryStringQuery((String)value,parseContext,mode,debugDoc,maxSizePerType);
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
    
    public void addMatchQueryClause(BooleanQuery booleanQuery,MatchQuery mq,Term t,float boost, int token,int queryIndex,int maxSizePerType) throws IOException
    {
        JDONREFv3TermQuery query = new JDONREFv3TermQuery(t);
        query.setToken(token);
        query.setBoost(boost);
        query.setQueryIndex(queryIndex);
        query.setMaxSizePerType(maxSizePerType);
        booleanQuery.add(new BooleanClause(query,BooleanClause.Occur.SHOULD));
    }
    
    private Query getQueryStringQuery(String find, QueryParseContext parseContext,int mode,int debugDoc,int maxSizePerType) throws IOException
    {
        Analyzer analyser = parseContext.mapperService().analysisService().analyzer("jdonrefv3es_search");
        
        CachingTokenFilter buffer = null;
        TermToBytesRefAttribute termAtt = null;
        PositionIncrementAttribute posIncrAtt = null;
        TokenStream source = null;
        int numTokens = 0;
        boolean hasMoreTokens = false;
    
        try
        {
            source = analyser.tokenStream("fullName", find.toString());
            source.reset();
            buffer = new CachingTokenFilter(source);
            buffer.reset();
            
            if (buffer.hasAttribute(TermToBytesRefAttribute.class)) {
             termAtt = buffer.getAttribute(TermToBytesRefAttribute.class);
            }
            if (buffer.hasAttribute(PositionIncrementAttribute.class)) {
             posIncrAtt = buffer.getAttribute(PositionIncrementAttribute.class);
            }
            
            if (termAtt != null) {
        try {
          hasMoreTokens = buffer.incrementToken();
          while (hasMoreTokens) {
            numTokens++;
            hasMoreTokens = buffer.incrementToken();
          }
        } catch (IOException e) {
          // ignore
        }
      }
            
        }
        catch (IOException e) {
            throw new RuntimeException("Error analyzing query text", e);
        } finally {
            IOUtils.closeWhileHandlingException(source);
        }
        
        buffer.reset();
        
        BytesRef bytes = termAtt == null ? null : termAtt.getBytesRef();

        JDONREFv3Query booleanQuery = new JDONREFv3Query();
        booleanQuery.setMode(mode);
        booleanQuery.setDebugDoc(debugDoc);
        booleanQuery.setNumTokens(numTokens);
        booleanQuery.setTermIndex(termIndex);
        booleanQuery.setMaxSizePerType(maxSizePerType);
        
        BooleanFilter boolFilters = new BooleanFilter();
        
        MatchQuery mq = new MatchQuery(parseContext);
        
        // phrase query:
        int queryIndex = 0;
        for (int i = 0; i < numTokens; i++) {
            try {
                boolean hasNext = buffer.incrementToken();
                assert hasNext == true;
                termAtt.fillBytesRef();  // here, BytesRef is updated !
            } catch (IOException e) {
                // safe to ignore, because we know the number of tokens
            }

            if (bytes.length>0)
            {
                addMatchQueryClause(booleanQuery, mq, new Term("ligne4", BytesRef.deepCopyOf(bytes)), 1.0f, i, queryIndex++,maxSizePerType);
                addMatchQueryClause(booleanQuery, mq, new Term("commune", BytesRef.deepCopyOf(bytes)), 1.0f, i, queryIndex++,maxSizePerType);
                addMatchQueryClause(booleanQuery, mq, new Term("codes", BytesRef.deepCopyOf(bytes)), 1.0f, i, queryIndex++,maxSizePerType);
                addMatchQueryClause(booleanQuery, mq, new Term("ligne7", BytesRef.deepCopyOf(bytes)), 1.0f, i, queryIndex++,maxSizePerType);
                //addMatchQueryClause(booleanQuery,mq,new Term("code_pays", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,queryIndex++);
                addMatchQueryClause(booleanQuery, mq, new Term("ligne1", BytesRef.deepCopyOf(bytes)), 1.0f, i, queryIndex++,maxSizePerType);
            
                TermFilter filter = new TermFilter(new Term("fullName",BytesRef.deepCopyOf(bytes)));
                boolFilters.add(filter, Occur.MUST);
            }
        }
        
        FilteredQuery filteredQuery = new FilteredQuery(booleanQuery,boolFilters,FilteredQuery.LEAP_FROG_FILTER_FIRST_STRATEGY);
        
        if (debugDoc>0)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" query :"+filteredQuery.toString());   
        }
        return filteredQuery;
    }
    
    protected Query getAndTypeQuery(QueryParseContext parseContext,String type,int size)
    {
        Filter adresseTypeFilter = getTypeFilter(parseContext,type);
        Filter limitFilter = new MyLimitFilter(size);
        BooleanFilter typeAndFilter = new BooleanFilter();
        typeAndFilter.add(adresseTypeFilter, Occur.MUST);
        typeAndFilter.add(limitFilter, Occur.MUST);
        
        FilteredQuery filteredQuery = new FilteredQuery(new MatchAllDocsQuery(),typeAndFilter);
        
        return filteredQuery;
    }
    
    protected Filter getTypeFilter(QueryParseContext parseContext,String value)
    {
        Filter typeFilter;
        DocumentMapper documentMapper = parseContext.mapperService().documentMapper(value);
        if (documentMapper == null) {
            typeFilter = new TermFilter(new Term("type", value));
        } else {
            typeFilter = documentMapper.typeFilter();
        }
        return typeFilter;
    }
}