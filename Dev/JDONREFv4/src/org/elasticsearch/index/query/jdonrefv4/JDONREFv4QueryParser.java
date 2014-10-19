package org.elasticsearch.index.query.jdonrefv4;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.MultiPayloadSpanTermQuery;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;
import org.apache.lucene.search.spans.checkers.*;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4Query;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4TermQuery;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.index.query.QueryParser;
import org.elasticsearch.index.query.QueryParsingException;
import org.elasticsearch.index.search.MatchQuery;

/**
 *
 * @author Julien
 */
public class JDONREFv4QueryParser implements QueryParser
{
    public static final String NAME = "jdonrefv4";
    public static final String SEARCH_ANALYZER = "jdonrefv4_search";
    public static final int DEFAULTMAXSIZE = Integer.MAX_VALUE;
    public static final int TERMCOUNTDEFAULTFACTOR = 1000;
    
    private Settings settings;
    
    @Nullable
    private final ClusterService clusterService;
    
    protected ConcurrentHashMap<String,Integer> termIndex = new ConcurrentHashMap<>();
    
     public JDONREFv4QueryParser()
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
    public JDONREFv4QueryParser(@Nullable ClusterService clusterService,Settings settings) {
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
        String field = "fullName";
        float boost = 1.0f;
        int debugDoc = -1;
        int maxSizePerType = DEFAULTMAXSIZE;
        int mode = JDONREFv4Query.AUTOCOMPLETE;

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
                switch (currentFieldName) {
                    default:
                        throw new QueryParsingException(parseContext.index(), "[jdonrefv3es] query does not support [" + currentFieldName + "]");
                    case "default_field":
                        break;
                    case "value":
                        break;
                    case "mode":
                        break;
                    case "_name":
                        break;
                    case "debugDoc":
                        break;
                    case "boost":
                        break;
                }
            }
            else if (token.isValue())
            {
                switch (currentFieldName) {
                    default:
                        throw new QueryParsingException(parseContext.index(), "[jdonrefv3es] query does not support [" + currentFieldName + "]");
                    case "_name":
                        filterName = parser.text();
                        break;
                    case "mode":
                        String modeStr = parser.text();
                        switch (modeStr) {
                            default:
                                throw new QueryParsingException(parseContext.index(), "[jdonrefv3es] query does not support "+modeStr+" for [" + currentFieldName + "]");
                            case "bulk":
                                mode = JDONREFv4Query.BULK;
                                break;
                            case "autocomplete":
                                mode = JDONREFv4Query.AUTOCOMPLETE;
                                break;
                        }
                        break;
                    case "boost":
                        boost = parser.floatValue();
                        break;
                    case "maxSizePerType":
                        maxSizePerType = parser.intValue();
                        break;
                    case "debugDoc":
                        debugDoc = parser.intValue();
                        break;
                    case "value":
                        value = parser.text(); // default field Name
                        break;
                    case "default_field":
                        field = parser.text();
                        break;
                }
            }
        }

        if (value == null) {
            throw new QueryParsingException(parseContext.index(), "No value specified for term filter");
        }

        Query query = null;
        
        if (query == null) {
            query = getJDONREFv4Query(field,(String)value,parseContext,mode,debugDoc,maxSizePerType);
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
    
    public void addMatchQueryClause(BooleanQuery booleanQuery,MatchQuery mq,Term t,float boost, int token,int queryIndex) throws IOException
    {
        JDONREFv4TermQuery query = new JDONREFv4TermQuery(t);
        query.setToken(token);
        query.setBoost(boost);
        query.setQueryIndex(queryIndex);
        booleanQuery.add(new BooleanClause(query,BooleanClause.Occur.SHOULD));
    }
    
    /**
     * adresse
     * commune
     * departement
     * pays
     * poizon
     * troncon
     * voie
     * 
     * code_pays = 10
     * ligne7 = 9
     * code_departement = 8
     * code_insee = 7
     * code_insee_commune = 6
     * commune = 5
     * code_arrondissement = 4
     * code_postal = 3
     * numero = 11
     * repetition = 11
     * type_de_voie = 2
     * article = 2
     * libelle = 2
     * ligne1 = 1
     * @return 
     */
    protected IPayloadChecker getChecker(int maxSizePerType)
    {
        IntegerEncoder encoder = new IntegerEncoder();
        
        GroupedPayloadChecker gpChecker = new GroupedPayloadChecker();
        
        byte[] ligne1 = encoder.encode("1".toCharArray()).bytes;
        byte[] ligne4 = encoder.encode("2".toCharArray()).bytes;
        byte[] ligne7 = encoder.encode("9".toCharArray()).bytes;
        byte[] numero = encoder.encode("11".toCharArray()).bytes;
        byte[] commune = encoder.encode("5".toCharArray()).bytes;
        byte[] code_departement = encoder.encode("8".toCharArray()).bytes;
        byte[] code_insee = encoder.encode("7".toCharArray()).bytes;
        byte[] code_insee_commune = encoder.encode("6".toCharArray()).bytes;
        byte[] code_arrondissement = encoder.encode("4".toCharArray()).bytes;
        byte[] code_postal = encoder.encode("3".toCharArray()).bytes;
        
        OnePayloadChecker ligne1Present = new OnePayloadChecker(ligne1);
        OnePayloadChecker ligne4Present = new OnePayloadChecker(ligne4);
        OnePayloadChecker ligne7Present = new OnePayloadChecker(ligne7);
        AllPayloadChecker allNumeroChecker = new AllPayloadChecker(numero);
        OnePayloadChecker communePresent = new OnePayloadChecker(commune);
        OnePayloadChecker codeDepartementPresent = new OnePayloadChecker(code_departement);
        OnePayloadChecker codeInseePresent = new OnePayloadChecker(code_insee);
        OnePayloadChecker codeInseeCommunePresent = new OnePayloadChecker(code_insee_commune);
        OnePayloadChecker codeArrondissementPresent = new OnePayloadChecker(code_arrondissement);
        OnePayloadChecker codePostalPresent = new OnePayloadChecker(code_postal);
        OrPayloadChecker codesPresent = new OrPayloadChecker(codeDepartementPresent,
                                                             codeArrondissementPresent,
                                                             codeInseePresent,
                                                             codeInseeCommunePresent,
                                                             codePostalPresent);
        OrPayloadChecker codesOrCommunePresent = new OrPayloadChecker(codesPresent,communePresent);
        PayloadBeforeAnotherChecker numeroAvantLigne4 = new PayloadBeforeAnotherChecker(numero, ligne4);
        LimitChecker limit = new LimitChecker(maxSizePerType);
        
        if (maxSizePerType==Integer.MAX_VALUE)
        {
            SwitchPayloadConditionClause clause1 = new SwitchPayloadConditionClause("poizon", new AndPayloadChecker(new OrPayloadChecker(ligne1Present,ligne4Present),numeroAvantLigne4));
            SwitchPayloadConditionClause clause2 = new SwitchPayloadConditionClause("adresse", new AndPayloadChecker(allNumeroChecker,ligne4Present.clone(),codesOrCommunePresent,numeroAvantLigne4));
            SwitchPayloadConditionClause clause3 = new SwitchPayloadConditionClause("voie", new AndPayloadChecker(ligne4Present.clone(),codesOrCommunePresent.clone()));
            SwitchPayloadConditionClause clause4 = new SwitchPayloadConditionClause("commune", new AndPayloadChecker(codesOrCommunePresent.clone()));
            SwitchPayloadConditionClause clause5 = new SwitchPayloadConditionClause("departement", new AndPayloadChecker(codeDepartementPresent.clone()));
            SwitchPayloadConditionClause clause6 = new SwitchPayloadConditionClause("pays", new AndPayloadChecker(ligne7Present.clone()));
            SwitchPayloadChecker switchChecker = new SwitchPayloadChecker(clause1,clause2,clause3,clause4,clause5,clause6);
            AndPayloadChecker andChecker = new AndPayloadChecker(gpChecker, switchChecker);

            return andChecker;
        }
        else
        {
            SwitchPayloadConditionClause clause1 = new SwitchPayloadConditionClause("poizon", new AndPayloadChecker(new OrPayloadChecker(ligne1Present,ligne4Present),numeroAvantLigne4,limit));
            SwitchPayloadConditionClause clause2 = new SwitchPayloadConditionClause("adresse", new AndPayloadChecker(allNumeroChecker,ligne4Present.clone(),codesOrCommunePresent,numeroAvantLigne4,limit.clone()));
            SwitchPayloadConditionClause clause3 = new SwitchPayloadConditionClause("voie", new AndPayloadChecker(ligne4Present.clone(),codesOrCommunePresent.clone(),limit.clone()));
            SwitchPayloadConditionClause clause4 = new SwitchPayloadConditionClause("commune", new AndPayloadChecker(codesOrCommunePresent.clone(),limit.clone()));
            SwitchPayloadConditionClause clause5 = new SwitchPayloadConditionClause("departement", new AndPayloadChecker(codeDepartementPresent.clone(),limit.clone()));
            SwitchPayloadConditionClause clause6 = new SwitchPayloadConditionClause("pays", new AndPayloadChecker(ligne7Present.clone(),limit.clone()));
            SwitchPayloadChecker switchChecker = new SwitchPayloadChecker(clause1,clause2,clause3,clause4,clause5,clause6);
            AndPayloadChecker andChecker = new AndPayloadChecker(gpChecker, switchChecker);

            return andChecker;
        }
        //NullPayloadChecker nullChecker = new NullPayloadChecker();
        //return nullChecker;
    }
    
    private Query getJDONREFv4Query(String term, String find, QueryParseContext parseContext,int mode,int debugDoc,int maxSizePerType) throws IOException
    {
        Analyzer analyser = parseContext.mapperService().analysisService().analyzer(SEARCH_ANALYZER);
        
        CachingTokenFilter buffer = null;
        TermToBytesRefAttribute termAtt = null;
        PositionIncrementAttribute posIncrAtt = null;
        TokenStream source = null;
        int numTokens = 0;
        boolean hasMoreTokens = false;
    
        try
        {
            source = analyser.tokenStream(term, find.toString());
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
        
        // JDONREFv4 Work rules
        PayloadCheckerSpanQuery spanQuery = new PayloadCheckerSpanQuery();
        spanQuery.setTermCountPayloadFactor(TERMCOUNTDEFAULTFACTOR);
        spanQuery.setChecker(getChecker(maxSizePerType));
        
        // phrase query:
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
                MultiPayloadSpanTermQuery spanTermQuery = new MultiPayloadSpanTermQuery(new Term(term,BytesRef.deepCopyOf(bytes)));
                spanQuery.addClause(spanTermQuery);
            }
        }
        
        if (debugDoc>0)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" query :"+spanQuery.toString());   
        }
                
        return spanQuery;
    }
}