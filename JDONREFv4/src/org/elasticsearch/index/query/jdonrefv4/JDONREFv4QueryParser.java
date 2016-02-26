package org.elasticsearch.index.query.jdonrefv4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.checkers.*;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.search.NotFilter;
import org.elasticsearch.common.lucene.search.XBooleanFilter;
import org.elasticsearch.common.lucene.search.XFilteredQuery;
import org.elasticsearch.common.lucene.search.jdonrefv4.PercentScoreBooleanQuery;
import org.elasticsearch.common.lucene.search.jdonrefv4.PercentScoreTermQuery;
import org.elasticsearch.common.regex.Regex;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.fielddata.plain.ParentChildIndexFieldData;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.internal.ParentFieldMapper;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.search.child.ParentConstantScoreQuery;
import org.elasticsearch.index.search.child.ParentQuery;
import org.elasticsearch.search.fetch.innerhits.InnerHitsContext;
import org.elasticsearch.search.internal.SubSearchContext;

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
    public static String DEFAULTFIELD = "libelle";
    public static boolean DEFAULTDEBUGMODE = false;
    public static boolean DEFAULTPROGRESSIVESHOULDMATCH = false;
    
    private Settings settings;
        
    IntegerEncoder encoder = new IntegerEncoder();
    
    @Inject public JDONREFv4QueryParser(Settings indexSettings) {
        //this.service = service;
        this.settings = indexSettings;
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
    
    // From HasParentFilterParser
    static Query createParentQuery(Query innerQuery, String parentType, boolean score, QueryParseContext parseContext, Tuple<String, SubSearchContext> innerHits) {
        DocumentMapper parentDocMapper = parseContext.mapperService().documentMapper(parentType);
        if (parentDocMapper == null) {
            throw new QueryParsingException(parseContext.index(), "[has_parent] query configured 'parent_type' [" + parentType + "] is not a valid type");
        }

        if (innerHits != null) {
            InnerHitsContext.ParentChildInnerHits parentChildInnerHits = new InnerHitsContext.ParentChildInnerHits(innerHits.v2(), innerQuery, null, parseContext.mapperService(), parentDocMapper);
            String name = innerHits.v1() != null ? innerHits.v1() : parentType;
            parseContext.addInnerHits(name, parentChildInnerHits);
        }

        Set<String> parentTypes = new HashSet<>(5);
        parentTypes.add(parentDocMapper.type());
        ParentChildIndexFieldData parentChildIndexFieldData = null;
        for (DocumentMapper documentMapper : parseContext.mapperService().docMappers(false)) {
            ParentFieldMapper parentFieldMapper = documentMapper.parentFieldMapper();
            if (parentFieldMapper.active()) {
                DocumentMapper parentTypeDocumentMapper = parseContext.mapperService().documentMapper(parentFieldMapper.type());
                parentChildIndexFieldData = parseContext.getForField(parentFieldMapper);
                if (parentTypeDocumentMapper == null) {
                    // Only add this, if this parentFieldMapper (also a parent)  isn't a child of another parent.
                    parentTypes.add(parentFieldMapper.type());
                }
            }
        }
        if (parentChildIndexFieldData == null) {
            throw new QueryParsingException(parseContext.index(), "[has_parent] no _parent field configured");
        }

        Filter parentFilter = null;
        if (parentTypes.size() == 1) {
            DocumentMapper documentMapper = parseContext.mapperService().documentMapper(parentTypes.iterator().next());
            if (documentMapper != null) {
                parentFilter = documentMapper.typeFilter();
            }
        } else {
            XBooleanFilter parentsFilter = new XBooleanFilter();
            for (String parentTypeStr : parentTypes) {
                DocumentMapper documentMapper = parseContext.mapperService().documentMapper(parentTypeStr);
                if (documentMapper != null) {
                    parentsFilter.add(documentMapper.typeFilter(), BooleanClause.Occur.SHOULD);
                }
            }
            parentFilter = parentsFilter;
        }

        if (parentFilter == null) {
            return null;
        }

        // wrap the query with type query
        innerQuery = new XFilteredQuery(innerQuery, parseContext.cacheFilter(parentDocMapper.typeFilter(), null));
        Filter childrenFilter = parseContext.cacheFilter(new NotFilter(parentFilter), null);
        if (score) {
            return new ParentQuery(parentChildIndexFieldData, innerQuery, parentDocMapper.type(), childrenFilter);
        } else {
            return new ParentConstantScoreQuery(parentChildIndexFieldData, innerQuery, parentDocMapper.type(), childrenFilter);
        }
    }
    
    // From HasParentFilterParser
    public Query getHasParentQuery(QueryParseContext parseContext,Query innerQuery,String parentType) throws IOException
    {
       if (parentType == null) {
            throw new QueryParsingException(parseContext.index(), "[has_parent] filter requires 'parent_type' field");
        }
        
        Query parentQuery = createParentQuery(innerQuery, parentType, false, parseContext, null);
        if (parentQuery == null) {
            return null;
        }
        
        return parentQuery;
    }
    
    public Query getTermsQuery(QueryParseContext parseContext,String fieldName,List<BytesRef> values, int minimumShouldMatch, float score, int typemask, Predicate<BytesRef> match)
    {
        float boost = 1.0f;
        
        try {
            PercentScoreBooleanQuery booleanQuery = new PercentScoreBooleanQuery();
            booleanQuery.setActive(false);
            for(int i=0;i<values.size();i++) {
                if (match==null || match.test(values.get(i)))
                    booleanQuery.add(new PercentScoreTermQuery(new Term(fieldName, BytesRef.deepCopyOf(values.get(i))), score, typemask, 1<<i), BooleanClause.Occur.SHOULD);
            }
            booleanQuery.setBoost(boost);
            booleanQuery.setMinimumNumberShouldMatch(minimumShouldMatch);
            return booleanQuery;
        } finally {
        }
    }
    
    public Query getTermsQuery(QueryParseContext parseContext,String fieldName,List<BytesRef> values, int minimumShouldMatch, float score, int typemask)
    {
        return getTermsQuery(parseContext, fieldName, values, minimumShouldMatch, score, typemask, null);
    }
    
    public void addTermsQuery(BooleanQuery booleanQuery,QueryParseContext parseContext,String fieldName,List<BytesRef> values, float score, int typemask)
    {
        addTermsQuery(booleanQuery, parseContext, fieldName, values, score, typemask, null);
    }
    
    public void addTermsQuery(BooleanQuery booleanQuery,QueryParseContext parseContext,String fieldName,List<BytesRef> values, float score, int typemask, Predicate<BytesRef> match)
    {
        for(int i=0;i<values.size();i++) {
            if (match==null || match.test(values.get(i)))
                booleanQuery.add(new PercentScoreTermQuery(new Term(fieldName, BytesRef.deepCopyOf(values.get(i))), score, typemask, 1<<i), BooleanClause.Occur.SHOULD);
        }
    }
    
    public Query getQueryStringQuery(QueryParseContext parseContext, String ligne4, ArrayList<Object> terms, int minimumShouldMatch) {
        
        BooleanQuery bq = new BooleanQuery();
        for(int i=0;i<terms.size()-1;i++)
        {
            TermQuery tq = new TermQuery(new Term(ligne4,(BytesRef)terms.get(i)));
            bq.add(tq, BooleanClause.Occur.SHOULD);
        }
        WildcardQuery wq = new WildcardQuery(new Term(ligne4, (BytesRef) terms.get(terms.size()-1)));
        bq.add(wq, BooleanClause.Occur.SHOULD);
        bq.setMinimumNumberShouldMatch(Math.min(terms.size(), minimumShouldMatch));
        
        return bq;
    }
    
    public Query getQueryStringQuery(QueryParseContext parseContext, String ligne4, String ligne6, ArrayList<Object> terms, int minimumShouldMatch) {
        
        BooleanQuery bq = new BooleanQuery();
        for(int i=0;i<terms.size()-1;i++)
        {
            TermQuery tq = new TermQuery(new Term(ligne4,(BytesRef)terms.get(i)));
            bq.add(tq, BooleanClause.Occur.SHOULD);
            tq = new TermQuery(new Term(ligne6,(BytesRef)terms.get(i)));
            bq.add(tq, BooleanClause.Occur.SHOULD);
        }
        WildcardQuery wq = new WildcardQuery(new Term(ligne4, (BytesRef) terms.get(terms.size()-1)));
        bq.add(wq, BooleanClause.Occur.SHOULD);
        wq = new WildcardQuery(new Term(ligne6, (BytesRef) terms.get(terms.size()-1)));
        bq.add(wq, BooleanClause.Occur.SHOULD);
        bq.setMinimumNumberShouldMatch(Math.min(terms.size(), minimumShouldMatch));
        
        return bq;
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
        //TypeAttribute typeAtt = null;
        TokenStream source = null;
        //PayloadAttribute payloadAtt = null;
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
            /*if (buffer.hasAttribute(TypeAttribute.class))
            {
               typeAtt = buffer.getAttribute(TypeAttribute.class);
            }
            if (buffer.hasAttribute(PayloadAttribute.class))
            {
               payloadAtt = buffer.getAttribute(PayloadAttribute.class);
            }*/
            
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
        
        if (debugMode)
            Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" found "+numTokens+" tokens");
        
        buffer.reset();
        
        BytesRef bytes = termAtt == null ? null : termAtt.getBytesRef();
        ArrayList<BytesRef> terms = new ArrayList<>(); // chaque terme composant la requête
        
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
                terms.add(BytesRef.deepCopyOf(bytes));
            }
        }
        
        PercentScoreBooleanQuery thisQuery = new PercentScoreBooleanQuery();
        thisQuery.setMaxCoord(terms.size());
        
        if (matchesIndices(parseContext.index().name(), "*adresse*")
                && terms.stream().filter((i)->isInteger(i)).count()>0    // au moins un numéro dans la requête
                && terms.size()>1)                                       // et deux éléments saisis
        {
            if (debugMode)
                Logger.getLogger(this.getClass().toString()).debug("Thread "+Thread.currentThread().getId()+" make adresse query");
            
            // cas de l'index sur les adresses pour imposer le filtre sur le numéro            
            Query numeros = getTermsQuery(parseContext,"numero", terms,1,30,1,(i)->isInteger(i));
            thisQuery.add(numeros,BooleanClause.Occur.MUST);
            
            // ainsi qu'un autre élément de l'adresse (attention, actuellement, il peut s'agir d'une répetition !)
            //addTermsQuery(thisQuery,parseContext,"numero", numbers,30,2);
            addTermsQuery(thisQuery,parseContext,"repetition", terms,30,2);
            addTermsQuery(thisQuery,parseContext,"type_de_voie", terms,10,4);
            addTermsQuery(thisQuery,parseContext,"libelle", terms,50,8);
            addTermsQuery(thisQuery,parseContext,"codes", terms,50,16);
            addTermsQuery(thisQuery,parseContext,"commune", terms,50,32);
            
            thisQuery.setMinimumNumberShouldMatch(terms.size()-1);
        }
        else if (matchesIndices(parseContext.index().name(), "*voie*"))
        {
            // la présence du libelle est imposé pour les voies
            Query libelle = getTermsQuery(parseContext,"libelle", terms,1,50,8);
            thisQuery.add(libelle,BooleanClause.Occur.MUST);
            
            addTermsQuery(thisQuery,parseContext,"type_de_voie", terms,10,4);
            addTermsQuery(thisQuery,parseContext,"codes", terms,50,16);
            addTermsQuery(thisQuery,parseContext,"commune", terms,50,32);
            thisQuery.setMinimumNumberShouldMatch(terms.size()-1);
        }
        else if (matchesIndices(parseContext.index().name(), "*commune*"))
        {
            // 1 élément parmi les codes ou le nom de la commune
            addTermsQuery(thisQuery,parseContext,"codes", terms,50,16);
            addTermsQuery(thisQuery,parseContext,"commune", terms,50,32);
            thisQuery.setMinimumNumberShouldMatch(terms.size()-1);
        }
        else if (matchesIndices(parseContext.index().name(), "*pays*"))
        {
            // la présence du pays est obligatoire pour un pays !
            Query ligne7 = getTermsQuery(parseContext,"ligne7", terms,terms.size()-1,50,64);
            thisQuery.add(ligne7,BooleanClause.Occur.MUST);
            //thisQuery.setMinimumNumberShouldMatch(terms1.size()-1); // no need
        }
        else if (matchesIndices(parseContext.index().name(), "*poizon*"))
        {
            // ligne 1 obligatoire pour les poizon
            Query ligne1 = getTermsQuery(parseContext,"ligne1", terms,1,50,128);
            thisQuery.add(ligne1,BooleanClause.Occur.MUST);
            
            addTermsQuery(thisQuery,parseContext,"ligne4", terms,50,256);
            addTermsQuery(thisQuery,parseContext,"codes", terms,50,512);
            addTermsQuery(thisQuery,parseContext,"commune", terms,50,1024);
            thisQuery.setMinimumNumberShouldMatch(terms.size()-1);
        }
        else
            return new EmptyQuery();
        
        if (debugMode)
        {
            Logger.getLogger(this.getClass().toString()).info("Thread "+Thread.currentThread().getId()+" "+thisQuery.toString());   
        }
        
        return thisQuery;
    }
    
    // from org.elasticsearch.index.query.IndicesQueryParser
    protected boolean matchesIndices(String currentIndex, String... indices) {
        for (String index : indices) {
            if (Regex.simpleMatch(index, currentIndex)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInteger(BytesRef t)
        {
            try
            {
                Integer.parseInt(t.utf8ToString());
                return true;
            }
            catch(NumberFormatException nfe)
            {
                return false;
            }
        }
    
    public class EmptyQuery extends Query
    {
        public EmptyQuery() {
        }
        
        public Weight createWeight(IndexSearcher is) throws IOException {
            return new EmptyWeight();
        }
        
        protected class EmptyWeight extends Weight {

            @Override
            public Explanation explain(AtomicReaderContext arc, int i) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Query getQuery() {
                return EmptyQuery.this;
            }

            @Override
            public float getValueForNormalization() throws IOException {
                return 0;
            }

            @Override
            public void normalize(float f, float f1) {
            }

            @Override
            public Scorer scorer(AtomicReaderContext arc, Bits bits) throws IOException {
                return null;
            }
        }
        
        @Override
        public String toString(String string) {
            return "Empty";
        }
        
    }
}