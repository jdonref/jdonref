package org.elasticsearch.index.query.jdonrefv4;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.UnsplitFilter;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.MultiPayloadSpanTermFilter;
import org.apache.lucene.search.spans.PayloadCheckerSpanFilter;
import org.apache.lucene.search.spans.checkers.*;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.search.jdonrefv4.MaximumScoreBooleanQuery;
import org.elasticsearch.common.lucene.search.jdonrefv4.PayloadAsScoreTermQuery;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.*;

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
    public static boolean DEFAULTDEBUGMODE = false;
    public static boolean DEFAULTPROGRESSIVESHOULDMATCH = false;
    
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
        int debugDoc = -1;
        boolean debugMode = DEFAULTDEBUGMODE;
        boolean progressiveShouldMatch = DEFAULTPROGRESSIVESHOULDMATCH;
        
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
                    case  "progressive_should_match":
                        break;
                    case "value":
                        break;
                    case "_name":
                        break;
                    case "debugMode":
                        break;
                    case "debugDoc":
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
                    case "progressive_should_match":
                        progressiveShouldMatch = parser.booleanValue();
                        break;
                    case "debugMode":
                        debugMode = parser.booleanValue();
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
            query = getJDONREFv4Query(field,(String)value,parseContext,debugDoc,debugMode,progressiveShouldMatch);
            
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
    protected IPayloadChecker getChecker()
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
        //AllPayloadChecker allNumeroChecker = new AllPayloadChecker(numero);
        OnePayloadChecker communePresent = new OnePayloadChecker(commune);
        OnePayloadChecker codesPresent = new OnePayloadChecker(codes);
        
        OrPayloadChecker codesOrCommunePresent = new OrPayloadChecker(codesPresent,communePresent);
        PayloadBeforeAnotherChecker numeroAvantLigne4 = new PayloadBeforeAnotherChecker(numero, ligne4);
        //LimitChecker limit = new LimitChecker(maxSizePerType);
        
        SwitchPayloadConditionClause clause1 = new SwitchPayloadConditionClause("poizon", new OrPayloadChecker(ligne1Present, new AndPayloadChecker(ligne4Present, numeroAvantLigne4)));
        SwitchPayloadConditionClause clause2 = new SwitchPayloadConditionClause("adresse", new AndPayloadChecker(/*allNumeroChecker,*/ ligne4Present.clone(),/* codesOrCommunePresent,*/ numeroAvantLigne4.clone()));
        SwitchPayloadConditionClause clause3 = new SwitchPayloadConditionClause("voie", new AndPayloadChecker(ligne4Present.clone()/* ,codesOrCommunePresent.clone()*/));
        SwitchPayloadConditionClause clause4 = new SwitchPayloadConditionClause("commune", codesOrCommunePresent);
        SwitchPayloadConditionClause clause5 = new SwitchPayloadConditionClause("departement", codesPresent.clone());
        SwitchPayloadConditionClause clause6 = new SwitchPayloadConditionClause("pays", ligne7Present);
        SwitchPayloadChecker switchChecker = new SwitchPayloadChecker(clause1, clause2, clause3, clause4, clause5, clause6);
        AndPayloadChecker andChecker = new AndPayloadChecker(gpChecker, switchChecker);

        return andChecker;

//        NullPayloadChecker nullChecker = new NullPayloadChecker();
//        return nullChecker;
    }
    
    private Query getJDONREFv4Query(String term, String find, QueryParseContext parseContext,int debugDoc,boolean debugMode,boolean progressiveShouldMatch) throws IOException
    {
        Analyzer analyser = parseContext.mapperService().fieldSearchAnalyzer(term); //analysisService().analyzer(SEARCH_ANALYZER);
        
        if (debugMode)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" query on "+term+" with "+analyser.toString());
        }
        
        CachingTokenFilter buffer = null;
        TermToBytesRefAttribute termAtt = null;
        TypeAttribute typeAtt = null;
        TokenStream source = null;
        PayloadAttribute payloadAtt = null;
        int numTokens = 0;
    
        try
        {
            source = analyser.tokenStream(term, find);
            source.reset();
            buffer = new CachingTokenFilter(source);
            buffer.reset();
            
            if (buffer.hasAttribute(TermToBytesRefAttribute.class)) {
               termAtt = buffer.getAttribute(TermToBytesRefAttribute.class);
            }
            if (buffer.hasAttribute(TypeAttribute.class))
            {
               typeAtt = buffer.getAttribute(TypeAttribute.class);
            }
            if (buffer.hasAttribute(PayloadAttribute.class))
            {
               payloadAtt = buffer.getAttribute(PayloadAttribute.class);
            }
            
            if (termAtt != null) {
                try {
                    while (buffer.incrementToken())
                        numTokens++;
                } catch (IOException e) {
                // ignore (end of tokens)
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
        
        PayloadCheckerSpanFilter spanFilter = new PayloadCheckerSpanFilter();
        spanFilter.setChecker(getChecker());
        spanFilter.setTermCountPayloadFactor(TERMCOUNTDEFAULTFACTOR);
        
        BooleanQuery orQuery = new MaximumScoreBooleanQuery();
        ((MaximumScoreBooleanQuery)orQuery).setParseContext(parseContext); 
        ((MaximumScoreBooleanQuery)orQuery).setProgressiveShouldMatch(progressiveShouldMatch);
        orQuery.setMinimumNumberShouldMatch(1);
        int maxTokens = 0;
                
        // phrase query:
        PayloadAsScoreTermQuery q = null;
        MultiPayloadSpanTermFilter filter = null;
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
                
//                if (UnsplitFilter.UNSPLIT_TYPE.equals(type))
                {
                    int payload = 1;
//                    int payload = PayloadHelper.decodeInt(payloadAtt.getPayload().bytes,payloadAtt.getPayload().offset);
                
                    if (debugMode)
                    {
                        System.out.println("Thread "+Thread.currentThread().getId()+" add or query :"+bytes.utf8ToString());
                        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" add or query :"+bytes.utf8ToString());
                    }
                    q = new PayloadAsScoreTermQuery(new Term(term,BytesRef.deepCopyOf(bytes)));
//                    q.setNumTerms(payload);
                    q.setNumTerms(payload);
                    
                    orQuery.add(new BooleanClause(q,BooleanClause.Occur.SHOULD));
                }
//                else // word type
                {
                    maxTokens++;
                    if (debugMode)
                    {
                        System.out.println("Thread "+Thread.currentThread().getId()+" add span filter :"+bytes.utf8ToString());
                        Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" add span filter :"+bytes.utf8ToString());
                    }
                    filter = new MultiPayloadSpanTermFilter(new Term(term,BytesRef.deepCopyOf(bytes)));
                    spanFilter.addClause(filter);
                }
            }
        }
        if(q!=null)
            q.setFinalWildCard(true); // true uniquement pour le dernier
        if(filter!=null)
            filter.setFinalWildCard(true);
        
        FilteredQuery fQuery = new FilteredQuery(orQuery,spanFilter,FilteredQuery.LEAP_FROG_QUERY_FIRST_STRATEGY);
        ((MaximumScoreBooleanQuery)orQuery).setNumTokens(maxTokens);
        
        if (debugMode)
        {
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" "+fQuery.toString());   
        }
        
        return fQuery;
    }
}