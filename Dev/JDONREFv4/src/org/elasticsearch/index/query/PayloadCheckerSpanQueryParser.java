package org.elasticsearch.index.query;

import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.apache.lucene.search.spans.checkers.PayloadChecker;
import org.apache.lucene.search.spans.PayloadCheckerSpanQuery;
import org.apache.lucene.search.spans.MultiPayloadSpanTermQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.List;
import org.elasticsearch.common.Strings;

import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author Julien
 */
public class PayloadCheckerSpanQueryParser implements QueryParser {
    
    public static final String NAME = "span_payloadchecker";

    public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    
    @Inject
    public PayloadCheckerSpanQueryParser() {
    }

    @Override
    public String[] names() {
        return new String[]{NAME, Strings.toCamelCase(NAME)};
    }

    @Override
    public Query parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();
        
        String queryName = null;
        List<MultiPayloadSpanTermQuery> clauses = newArrayList();
        IPayloadChecker checker = null;
        int termCountPayloadFactor = NOTERMCOUNTPAYLOADFACTOR;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token == XContentParser.Token.START_OBJECT) {
                if ("checker".equals(currentFieldName)) {
                    checker = PayloadCheckerFactory.getInstance().parseInnerQuery(parseContext);
                }
            } else if (token == XContentParser.Token.START_ARRAY) {
                if ("clauses".equals(currentFieldName)) {
                    while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                        Query query = parseContext.parseInnerQuery();
                        if (!(query instanceof MultiPayloadSpanTermQuery)) {
                            throw new QueryParsingException(parseContext.index(), NAME+" [clauses] must be of type span_multipayloadterm");
                        }
                        clauses.add((MultiPayloadSpanTermQuery) query);
                    }
                } else {
                    throw new QueryParsingException(parseContext.index(), "["+NAME+"] query does not support [" + currentFieldName + "]");
                }
            } else if (token.isValue()) {
                if ("_name".equals(currentFieldName)) {
                    queryName = parser.text();
                } else if ("termcountpayloadfactor".equals(currentFieldName)) {
                        termCountPayloadFactor = parser.intValue();
                } else {
                    throw new QueryParsingException(parseContext.index(), "["+NAME+"] filter does not support [" + currentFieldName + "]");
                }
            }
        }

        if (clauses.isEmpty()) {
            throw new QueryParsingException(parseContext.index(), NAME+" must include [clauses]");
        }
        
        PayloadCheckerSpanQuery gpsQuery = new PayloadCheckerSpanQuery(clauses.toArray(new MultiPayloadSpanTermQuery[clauses.size()]));
        
        gpsQuery.setChecker(checker);
        
        gpsQuery.setTermCountPayloadFactor(termCountPayloadFactor);
        if (queryName != null) {
            parseContext.addNamedQuery(queryName, gpsQuery);
        }
        
        return gpsQuery;
    }
}