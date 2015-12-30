package org.elasticsearch.index.query;

import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.MultiPayloadSpanTermFilter;
import org.apache.lucene.search.spans.PayloadCheckerSpanFilter;
import org.apache.lucene.search.spans.checkers.IPayloadChecker;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;

/**
 *
 * @author Julien
 */
public class PayloadCheckerSpanFilterParser implements FilterParser {
    
    public static final String NAME = "span_payloadchecker";
    
    @Inject
    public PayloadCheckerSpanFilterParser() {
    }

    @Override
    public String[] names() {
        return new String[]{NAME, Strings.toCamelCase(NAME)};
    }

    @Override
    public Filter parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();
        
        String filterName = null;
        List<MultiPayloadSpanTermFilter> clauses = newArrayList();
        IPayloadChecker checker = null;
        int termCountPayloadFactor = PayloadCheckerSpanFilter.NOTERMCOUNTPAYLOADFACTOR;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token == XContentParser.Token.START_OBJECT) {
                if ("checker".equals(currentFieldName)) {
                    checker = PayloadCheckerFactory.getInstance().parseInnerFilter(parseContext);
                }
            } else if (token == XContentParser.Token.START_ARRAY) {
                if ("clauses".equals(currentFieldName)) {
                    while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                        Filter filter = parseContext.parseInnerFilter();
                        if (!(filter instanceof MultiPayloadSpanTermFilter)) {
                            throw new QueryParsingException(parseContext.index(), NAME+" [clauses] must be of type span_multipayloadterm");
                        }
                        clauses.add((MultiPayloadSpanTermFilter) filter);
                    }
                } else {
                    throw new QueryParsingException(parseContext.index(), "["+NAME+"] filter does not support [" + currentFieldName + "]");
                }
            } else if (token.isValue()) {
                if ("_name".equals(currentFieldName)) {
                    filterName = parser.text();
                }
                else if ("termcountpayloadfactor".equals(currentFieldName)) {
                        termCountPayloadFactor = parser.intValue();
                } else {
                    throw new QueryParsingException(parseContext.index(), "["+NAME+"] filter does not support [" + currentFieldName + "]");
                }
            }
        }

        if (clauses.isEmpty()) {
            throw new QueryParsingException(parseContext.index(), NAME+" must include [clauses]");
        }
        
        PayloadCheckerSpanFilter gpsFilter = new PayloadCheckerSpanFilter(clauses.toArray(new MultiPayloadSpanTermFilter[clauses.size()]));
        
        gpsFilter.setChecker(checker);
        
        gpsFilter.setTermCountPayloadFactor(termCountPayloadFactor);
        if (filterName != null) {
            parseContext.addNamedFilter(filterName, gpsFilter);
        }
        
        return gpsFilter;
    }
}