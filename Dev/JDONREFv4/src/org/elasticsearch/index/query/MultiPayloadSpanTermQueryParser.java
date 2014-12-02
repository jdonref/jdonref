package org.elasticsearch.index.query;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.MultiPayloadSpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.MapperService;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermQueryParser implements QueryParser {

    public static String NAME = "span_multipayloadterm";

    @Inject
    public MultiPayloadSpanTermQueryParser() {
    }

    @Override
    public String[] names() {
        return new String[]{NAME, Strings.toCamelCase(NAME)};
    }

    @Override
    public Query parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();

        XContentParser.Token token = parser.currentToken();
        if (token == XContentParser.Token.START_OBJECT) {
            token = parser.nextToken();
        }
        assert token == XContentParser.Token.FIELD_NAME;
        String fieldName = parser.currentName();

        boolean checked = true;
        String value = null;
        float boost = 1.0f;
        String queryName = null;
        token = parser.nextToken();
        if (token == XContentParser.Token.START_OBJECT) {
            String currentFieldName = null;
            while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                if (token == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else {
                    if ("term".equals(currentFieldName)) {
                        value = parser.text();
                    } else if ("value".equals(currentFieldName)) {
                        value = parser.text();
                    } else if ("boost".equals(currentFieldName)) {
                        boost = parser.floatValue();
                    } else if ("checked".equals(currentFieldName)) {
                        checked = parser.booleanValue();
                    } else if ("_name".equals(currentFieldName)) {
                        queryName = parser.text();
                    }
                    else {
                        throw new QueryParsingException(parseContext.index(), "["+NAME+"] query does not support [" + currentFieldName + "]");
                    }
                }
            }
            parser.nextToken();
        } else {
            value = parser.text();
            // move to the next token
            parser.nextToken();
        }

        if (value == null) {
            throw new QueryParsingException(parseContext.index(), "No value specified for term query");
        }

        BytesRef valueBytes = null;
        MapperService.SmartNameFieldMappers smartNameFieldMappers = parseContext.smartFieldMappers(fieldName);
        if (smartNameFieldMappers != null) {
            if (smartNameFieldMappers.hasMapper()) {
                fieldName = smartNameFieldMappers.mapper().names().indexName();
                valueBytes = smartNameFieldMappers.mapper().indexedValueForSearch(value);
            }
        }
        if (valueBytes == null) {
            valueBytes = new BytesRef(value);
        }

        return makeQuery(parseContext, fieldName, valueBytes, boost, queryName, checked);
    }
    
    public Query makeQuery(QueryParseContext parseContext,String fieldName, BytesRef valueBytes,float boost,String queryName, boolean checked)
    {
        MultiPayloadSpanTermQuery query = new MultiPayloadSpanTermQuery(new Term(fieldName, valueBytes));
        query.setBoost(boost);
        query.setChecked(checked);
        if (queryName != null) {
            parseContext.addNamedQuery(queryName, query);
        }
        return query;
    }
}