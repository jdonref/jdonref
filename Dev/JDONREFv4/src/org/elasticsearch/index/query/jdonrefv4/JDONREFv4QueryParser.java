package org.elasticsearch.index.query.jdonrefv4;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.UnsplitFilter;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
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
    public static String DEFAULTFIELD = "fullName";
    
    private Settings settings;
    
    @Nullable
    private final ClusterService clusterService;
    
    protected ConcurrentHashMap<Integer,Integer> payloadIndex = new ConcurrentHashMap<>();
    
     public JDONREFv4QueryParser()
     {
          clusterService = null;
          payloadIndex.put(1,0); // ligne1
          payloadIndex.put(2,1); // ligne4
          payloadIndex.put(11,2); // numero
          payloadIndex.put(5,3); // commune
          payloadIndex.put(3,4); // codes
          payloadIndex.put(9,5); // ligne7
          payloadIndex.put(10,6); // code pays
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
        String field = DEFAULTFIELD;
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
        byte[] codes = encoder.encode("3".toCharArray()).bytes;
//        byte[] code_departement = encoder.encode("8".toCharArray()).bytes;
//        byte[] code_insee = encoder.encode("7".toCharArray()).bytes;
//        byte[] code_insee_commune = encoder.encode("6".toCharArray()).bytes;
//        byte[] code_arrondissement = encoder.encode("4".toCharArray()).bytes;
//        byte[] code_postal = encoder.encode("3".toCharArray()).bytes;
        
        OnePayloadChecker ligne1Present = new OnePayloadChecker(ligne1);
        OnePayloadChecker ligne4Present = new OnePayloadChecker(ligne4);
        OnePayloadChecker ligne7Present = new OnePayloadChecker(ligne7);
        AllPayloadChecker allNumeroChecker = new AllPayloadChecker(numero);
        OnePayloadChecker communePresent = new OnePayloadChecker(commune);
        OnePayloadChecker codesPresent = new OnePayloadChecker(codes);
//        OnePayloadChecker codeDepartementPresent = new OnePayloadChecker(code_departement);
//        OnePayloadChecker codeInseePresent = new OnePayloadChecker(code_insee);
//        OnePayloadChecker codeInseeCommunePresent = new OnePayloadChecker(code_insee_commune);
//        OnePayloadChecker codeArrondissementPresent = new OnePayloadChecker(code_arrondissement);
//        OnePayloadChecker codePostalPresent = new OnePayloadChecker(code_postal);
//        OrPayloadChecker codesPresent = new OrPayloadChecker(codeDepartementPresent,
//                                                             codeArrondissementPresent,
//                                                             codeInseePresent,
//                                                             codeInseeCommunePresent,
//                                                             codePostalPresent);
        OrPayloadChecker codesOrCommunePresent = new OrPayloadChecker(codesPresent,communePresent);
        PayloadBeforeAnotherChecker numeroAvantLigne4 = new PayloadBeforeAnotherChecker(numero, ligne4);
        LimitChecker limit = new LimitChecker(maxSizePerType);
        
        SwitchPayloadConditionClause clause1 = new SwitchPayloadConditionClause("poizon", new OrPayloadChecker(ligne1Present, new AndPayloadChecker(ligne4Present, numeroAvantLigne4)));
        SwitchPayloadConditionClause clause2 = new SwitchPayloadConditionClause("adresse", new AndPayloadChecker(allNumeroChecker, ligne4Present.clone(),/* codesOrCommunePresent,*/ numeroAvantLigne4.clone()));
        SwitchPayloadConditionClause clause3 = new SwitchPayloadConditionClause("voie", new AndPayloadChecker(ligne4Present.clone()/* ,codesOrCommunePresent.clone()*/));
        SwitchPayloadConditionClause clause4 = new SwitchPayloadConditionClause("commune", codesOrCommunePresent);
        SwitchPayloadConditionClause clause5 = new SwitchPayloadConditionClause("departement", codesPresent.clone());
        SwitchPayloadConditionClause clause6 = new SwitchPayloadConditionClause("pays", ligne7Present);
        SwitchPayloadChecker switchChecker = new SwitchPayloadChecker(clause1, clause2, clause3, clause4, clause5, clause6);
        AndPayloadChecker andChecker = new AndPayloadChecker(gpChecker, switchChecker);

        return andChecker;

        //NullPayloadChecker nullChecker = new NullPayloadChecker();
        //return nullChecker;
    }
    
    private Query getJDONREFv4Query(String term, String find, QueryParseContext parseContext,int mode,int debugDoc,int maxSizePerType) throws IOException
    {
        Analyzer analyser = parseContext.mapperService().fieldSearchAnalyzer(term); //analysisService().analyzer(SEARCH_ANALYZER);
        
        CachingTokenFilter buffer = null;
        TermToBytesRefAttribute termAtt = null;
        PositionIncrementAttribute posIncrAtt = null;
        TypeAttribute typeAtt = null;
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
            if (buffer.hasAttribute(TypeAttribute.class))
            {
               typeAtt = buffer.getAttribute(TypeAttribute.class);
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
        //PayloadCheckerSpanQuery spanQuery = new PayloadCheckerSpanQuery();
        JDONREFv4Query spanQuery = new JDONREFv4Query();
        spanQuery.setMode(mode);
        spanQuery.setTermCountPayloadFactor(TERMCOUNTDEFAULTFACTOR);
        spanQuery.setChecker(getChecker(maxSizePerType));
        spanQuery.setPayloadIndex(payloadIndex);
        if (maxSizePerType!=-1 && maxSizePerType!=DEFAULTMAXSIZE)
            spanQuery.setLimit(maxSizePerType);
        
//        BooleanFilter filter = new BooleanFilter();

//        BooleanFilter switchFilter = new BooleanFilter();
//        filter.add(switchFilter, BooleanClause.Occur.MUST);
        
//        BooleanFilter departementFilter = new BooleanFilter();
//        switchFilter.add(departementFilter,BooleanClause.Occur.SHOULD);
//        TermFilter departementTypeFilter = new TermFilter(new Term("_type","departement"));
//        departementFilter.add(departementTypeFilter,BooleanClause.Occur.MUST);
//        BooleanFilter departementClausesFilter = new BooleanFilter();
//        departementFilter.add(departementClausesFilter,BooleanClause.Occur.MUST);
//        
//        BooleanFilter communeFilter = new BooleanFilter();
//        switchFilter.add(communeFilter,BooleanClause.Occur.SHOULD);
//        TermFilter communeTypeFilter = new TermFilter(new Term("_type","commune"));
//        communeFilter.add(communeTypeFilter,BooleanClause.Occur.MUST);
//        BooleanFilter communeClausesFilter = new BooleanFilter();
//        communeFilter.add(communeClausesFilter,BooleanClause.Occur.MUST);
        
        // phrase query:
        int numTokenNotUnplited = 0;
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
                String type = typeAtt.type();
                
                //MultiPayloadSpanTermQuery spanTermQuery = new MultiPayloadSpanTermQuery(new Term(term,BytesRef.deepCopyOf(bytes)));
                JDONREFv4TermQuery spanTermQuery = new JDONREFv4TermQuery(new Term(term,BytesRef.deepCopyOf(bytes)));
                spanTermQuery.setToken(i);
                if (UnsplitFilter.UNSPLIT_TYPE.equals(type))
                {
                    //System.out.println("HITCH ! "+find+" span "+spanTermQuery.getTerm().text()+ " on "+parseContext.index());
                    spanTermQuery.setChecked(false);
                }
                else numTokenNotUnplited++;
                //spanQuery.addClause(spanTermQuery);
                spanQuery.add(spanTermQuery,BooleanClause.Occur.SHOULD);
            }
        }
        
        spanQuery.setNumTokens(numTokenNotUnplited);
        
        if (debugDoc>0)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getName()+" query :"+spanQuery.toString());   
        }
                
        return spanQuery;
    }
}