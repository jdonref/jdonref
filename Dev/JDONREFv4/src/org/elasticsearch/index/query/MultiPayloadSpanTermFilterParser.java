package org.elasticsearch.index.query;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.spans.MultiPayloadSpanTermFilter;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.MapperService;

/**
 *
 * @author Julien
 */
public class MultiPayloadSpanTermFilterParser implements FilterParser {

    public static String NAME = "span_multipayloadterm";

    @Inject
    public MultiPayloadSpanTermFilterParser() {
    }

    @Override
    public String[] names() {
        return new String[]{NAME, Strings.toCamelCase(NAME)};
    }

    @Override
    public Filter parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
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
        String filterName = null;
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
                        filterName = parser.text();
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

        return makeFilter(parseContext, fieldName, valueBytes, boost, filterName, checked);
    }
    
    public Filter makeFilter(QueryParseContext parseContext,String fieldName, BytesRef valueBytes,float boost,String filterName, boolean checked)
    {
        MultiPayloadSpanTermFilter filter = new MultiPayloadSpanTermFilter(new Term(fieldName, valueBytes));
        filter.setChecked(checked);
        if (filterName != null) {
            parseContext.addNamedFilter(filterName, filter);
        }
        return filter;
    }
}