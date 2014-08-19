package mi.ppol.jdonref.espluginpoc.index.query;

import java.io.IOException;
import java.util.Hashtable;
import org.apache.lucene.search.JDONREFv3Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MapperQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.JDONREFv3TermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.index.query.QueryParser;
import org.elasticsearch.index.query.QueryParsingException;

import org.elasticsearch.index.query.QueryStringQueryParser;
import org.elasticsearch.index.search.MatchQuery;

/**
 *
 * @author Julien
 */
public class JDONREFv3QueryParser implements QueryParser
{
    public static final String NAME = "jdonrefv3es";
    
    public static final int SMART = 1;
    public static final int SPAN = 2;
    public static final int STRING = 3;

    private Settings settings;
    
    @Nullable
    private final ClusterService clusterService;
    
     public JDONREFv3QueryParser()
     {
          clusterService = null;
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
        int mode = JDONREFv3QueryParser.SMART;

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
                } else if ("boost".equals(currentFieldName)) {
                } else {
                    throw new QueryParsingException(parseContext.index(), "[term] filter does not support [" + currentFieldName + "]");
                }
            }
            else if (token.isValue())
            {
                if ("_name".equals(currentFieldName)) {
                    filterName = parser.text();
                } else if ("mode".equals(currentFieldName)) {
                    mode = parser.intValue();
                } else if ("boost".equals(currentFieldName)) {
                    boost = parser.floatValue();
                } else if ("value".equals(currentFieldName)) {
                    value = parser.text();
                } else if ("query".equals(currentFieldName)) {
                } else if ("default_field".equals(currentFieldName)) {
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
            if (mode==JDONREFv3QueryParser.SMART)
                query = getQueryExact((String)value,parseContext);
            else if (mode==JDONREFv3QueryParser.STRING)
                query = getQueryStringQuery((String)value,parseContext);
            else
                query = getSpanQuery((String)value,parseContext);
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
        Term t = new Term(attr,BytesRef.deepCopyOf(new BytesRef(value)));
        JDONREFv3TermQuery query = new JDONREFv3TermQuery(t);
        //TermQuery query = new TermQuery(t);
        
        //Query query = mq.parse(MatchQuery.Type.BOOLEAN,attr,value);
        if (query!=null)
        {
              query.setBoost(boost);
              booleanQuery.add(new BooleanClause(query,occur));
        }
    }
    
    public void addMatchQueryClause(BooleanQuery booleanQuery,MatchQuery mq,Term t,float boost,Occur occur, int index, boolean last) throws IOException
    {
        JDONREFv3TermQuery query = new JDONREFv3TermQuery(t);
        //TermQuery query = new TermQuery(t);
        
        //Query query = mq.parse(MatchQuery.Type.BOOLEAN,attr,value);
        if (query!=null)
        {
            if (last)
              query.setIsLast();
            query.setIndex(index);
            query.setBoost(boost);
            booleanQuery.add(new BooleanClause(query,occur));
        }
    }
    
    public void addMatchQueryClause(BooleanQuery booleanQuery,MatchQuery mq,String attr,int value,float boost,Occur occur) throws IOException
    {
        Query query = mq.parse(MatchQuery.Type.BOOLEAN,attr,value);
        if (query!=null)
        {
              query.setBoost(boost);
              booleanQuery.add(new BooleanClause(query,occur));
        }
    }
    
    public Query getSpanQuery(String find, QueryParseContext parseContext) throws IOException
    {
        Hashtable<String,Boolean> hash = new Hashtable<String,Boolean>();
        
        String[] splitted = find.split(" ");
        SpanTermQuery[] query = new SpanTermQuery[splitted.length];
        for(int i=0;i<splitted.length;i++)
        {
            String stri = splitted[i];
            SpanTermQuery q = new SpanTermQuery(new Term("fullName",stri));
            query[i] = q;
        }
        
        SpanNearQuery spanNear = new SpanNearQuery(query,5,true);
        
        System.out.println(spanNear.toString());
        
        return spanNear;
    }
    
    public Query getQueryExact(String find, QueryParseContext parseContext) throws IOException
    {
        Hashtable<String,Boolean> hash = new Hashtable<String,Boolean>();
        
        MatchQuery mq = new MatchQuery(parseContext);
        BooleanQuery booleanQuery = new BooleanQuery();
        
        boolean isThereInt = false;
        int firstNumber = -1;
        
        String[] splitted = find.split(" ");
        for(int i=0;i<splitted.length;i++)
        {
            String stri = splitted[i];
            
            if (hash.get(stri)==null)
            {
                hash.put(stri,true);
                
                if (isInt(stri))
                {
                    if (firstNumber==-1) firstNumber = i;
                    isThereInt = true;

                    if (stri.length()==5)
                    {
                        addMatchQueryClause(booleanQuery,mq,"code_insee",stri,2.5f,BooleanClause.Occur.SHOULD);
                    }
                    if (stri.length()==5)
                    {
                        addMatchQueryClause(booleanQuery,mq,"code_postal",stri,2.5f,BooleanClause.Occur.SHOULD);
                    }
                    float dptBoost = 1.0f;
                    if (i==0)
                        dptBoost = 0.5f;
                    if (i>0 || splitted.length==1)
                        dptBoost = 2.5f;
                    addMatchQueryClause(booleanQuery,mq,"code_departement",stri.length()<=2?stri:stri.substring(0,2),dptBoost,BooleanClause.Occur.SHOULD);
                    
                    float ardtBoost = 1.0f;
                    if (i==0)
                        ardtBoost = 0.5f;
                    if (i>0 || splitted.length==1)
                        ardtBoost = 2.5f;
                    addMatchQueryClause(booleanQuery,mq,"code_arrondissement",stri.length()<=2?stri:stri.substring(stri.length()-2),ardtBoost,BooleanClause.Occur.SHOULD);
                    
                    float numBoost = 1.0f;
                    if (i==0 && splitted.length!=1)
                        numBoost = 2.5f;
                    else if (i>0 || splitted.length==1)
                        numBoost = 0.5f;
                    addMatchQueryClause(booleanQuery,mq,"numero",stri,numBoost,BooleanClause.Occur.SHOULD);
                    
                    addMatchQueryClause(booleanQuery,mq,"ligne4",stri,0.5f,BooleanClause.Occur.SHOULD);
                    addMatchQueryClause(booleanQuery,mq,"ligne6",stri,0.5f,BooleanClause.Occur.SHOULD);
                }
                else
                {
                    addMatchQueryClause(booleanQuery,mq,"ligne4",stri,10f,BooleanClause.Occur.SHOULD);
                    addMatchQueryClause(booleanQuery,mq,"ligne6",stri,0.5f,BooleanClause.Occur.SHOULD);
                }
            }
        }
        
        float num0Boost = 1.0f;
        if (!isThereInt || firstNumber > 0 || splitted.length==1)
            num0Boost = 2.5f;
        else if (firstNumber==0)
            num0Boost = 0.5f;
        addMatchQueryClause(booleanQuery,mq,"numero","0",num0Boost,BooleanClause.Occur.SHOULD);
        
        //System.out.println(booleanQuery.toString());
        
        return booleanQuery;
    }

    String[] fields = new String[]{"fullName"};
    
    private Query getQueryStringQuery(String find, QueryParseContext parseContext) throws IOException
    {
        JDONREFv3Query booleanQuery = new JDONREFv3Query();
        MatchQuery mq = new MatchQuery(parseContext);
        
        Analyzer analyser = parseContext.mapperService().fieldSearchAnalyzer("fullName");
        
        CachingTokenFilter buffer = null;
        TermToBytesRefAttribute termAtt = null;
        PositionIncrementAttribute posIncrAtt = null;
        TokenStream source = null;
        int numTokens = 0;
        int positionCount = 0;
        boolean severalTokensAtSamePosition = false;
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
            int positionIncrement = (posIncrAtt != null) ? posIncrAtt.getPositionIncrement() : 1;
            if (positionIncrement != 0) {
              positionCount += positionIncrement;
            } else {
              severalTokensAtSamePosition = true;
            }
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

        booleanQuery.setNumTokens(numTokens);
        
        // phrase query:
          int position = -1;
          for (int i = 0; i < numTokens; i++) {
            int positionIncrement = 1;
            try {
              boolean hasNext = buffer.incrementToken();
              assert hasNext == true;
              termAtt.fillBytesRef();
              if (posIncrAtt != null) {
                positionIncrement = posIncrAtt.getPositionIncrement();
              }
            } catch (IOException e) {
              // safe to ignore, because we know the number of tokens
            }

            position += positionIncrement;
            //addMatchQueryClause(booleanQuery,mq,new Term("fullName", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i==numTokens-1);
            addMatchQueryClause(booleanQuery,mq,new Term("ligne4", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,false);
            addMatchQueryClause(booleanQuery,mq,new Term("commune", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,false);
            addMatchQueryClause(booleanQuery,mq,new Term("codes", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,false);
            //addMatchQueryClause(booleanQuery,mq,new Term("code_arrondissement", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,false);
            //addMatchQueryClause(booleanQuery,mq,new Term("code_insee", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,false);
            //addMatchQueryClause(booleanQuery,mq,new Term("code_departement", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,false);
            addMatchQueryClause(booleanQuery,mq,new Term("ligne7", BytesRef.deepCopyOf(bytes)),1.0f,BooleanClause.Occur.SHOULD,i,i==numTokens-1);
          }
        
        //System.out.println(booleanQuery.toString());
        
        return booleanQuery;
    }
}