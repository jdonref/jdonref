package org.elasticsearch.index.query;

import org.apache.lucene.search.spans.TermVectorMultiPayloadSpanTermQuery;
import org.apache.lucene.analysis.payloads.IdentityEncoder;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.spans.PayloadVersusTypeSpanQuery;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.lucene.search.spans.MultiPayloadSpanTermQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.GroupedPayloadSpanQuery;
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
public class PayloadVersusTypeSpanQueryParser implements QueryParser {
    
    public static final String NAME = "span_payloadversustype";

    public static final int NOTERMCOUNTPAYLOADFACTOR = -1;
    
    @Inject
    public PayloadVersusTypeSpanQueryParser() {
    }

    @Override
    public String[] names() {
        return new String[]{NAME, Strings.toCamelCase(NAME)};
    }

    @Override
    public Query parse(QueryParseContext parseContext) throws IOException, QueryParsingException {
        XContentParser parser = parseContext.parser();
        
        String queryName = null;
        ArrayList<BytesRef> payloads = new ArrayList<>();
        
        List<TermVectorMultiPayloadSpanTermQuery> clauses = newArrayList();
        Hashtable<String,BytesRef[]> requiredPayloads = new Hashtable<>();
        int termCountPayloadFactor = NOTERMCOUNTPAYLOADFACTOR;
        
        IntegerEncoder intEncoder = new IntegerEncoder();
        FloatEncoder floatEncoder = new FloatEncoder();
        IdentityEncoder identityEncoder = new IdentityEncoder();
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token == XContentParser.Token.START_ARRAY) {
                if ("clauses".equals(currentFieldName)) {
                    while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                        Query query = parseContext.parseInnerQuery();
                        if (!(query instanceof TermVectorMultiPayloadSpanTermQuery)) {
                            throw new QueryParsingException(parseContext.index(), NAME+" [clauses] must be of type span_multipayloadterm");
                        }
                        clauses.add((TermVectorMultiPayloadSpanTermQuery) query);
                    }
                } else if ("requiredPayloads".equals(currentFieldName)) {
                    while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY)
                    {
                        if (token==XContentParser.Token.START_OBJECT)
                        {
                            String currentTypeName = null;
                            payloads.clear();
                            
                            while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                                if (token == XContentParser.Token.FIELD_NAME) {
                                    currentFieldName = parser.currentName();
                                }
                                else if (token == XContentParser.Token.START_ARRAY)
                                {
                                    if ("payloads".equals(currentFieldName))
                                    {
                                        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                                            if (token == XContentParser.Token.VALUE_EMBEDDED_OBJECT)
                                            {
                                                byte[] bytes = parser.binaryValue();
                                                payloads.add(new BytesRef(bytes));
                                            }
                                            else if (token == XContentParser.Token.VALUE_STRING)
                                            {
                                                BytesRef bytes = identityEncoder.encode(parser.text().toCharArray());
                                                payloads.add(bytes);
                                            }
                                            else if (token == XContentParser.Token.VALUE_NUMBER)
                                            {
                                                if (parser.numberType()==XContentParser.NumberType.INT)
                                                {
                                                    BytesRef bytes = intEncoder.encode(Integer.toString(parser.intValue()).toCharArray());
                                                    payloads.add(bytes);
                                                }
                                                else if (parser.numberType()==XContentParser.NumberType.FLOAT)
                                                {
                                                    BytesRef bytes = floatEncoder.encode(Float.toString(parser.floatValue()).toCharArray());
                                                    payloads.add(bytes);
                                                }
                                            }
                                            else
                                                throw new QueryParsingException(parseContext.index(), NAME+" [requiredPayloads] for type "+currentTypeName+" may contain payloads");
                                        }
                                        requiredPayloads.put(currentTypeName,payloads.toArray(new BytesRef[payloads.size()]));
                                    } else {
                                        throw new QueryParsingException(parseContext.index(), "["+NAME+"] [requiredPayloads] does not support [" + currentFieldName + "]");
                                    }
                                }
                                else if (token.isValue()) {
                                    if ("type".equals(currentFieldName)) {
                                        currentTypeName = parser.text();
                                    } else {
                                        throw new QueryParsingException(parseContext.index(), "["+NAME+"] [requiredPayloads] does not support [" + currentFieldName + "]");
                                    }
                                }
                            }
                        }
                        else throw new QueryParsingException(parseContext.index(), NAME+" [requiredPayloads] may contain types and payloads");
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
            throw new QueryParsingException(parseContext.index(), "span_payloadversustype must include [clauses]");
        }
        
        PayloadVersusTypeSpanQuery gpsQuery = new PayloadVersusTypeSpanQuery(clauses.toArray(new TermVectorMultiPayloadSpanTermQuery[clauses.size()]));
        gpsQuery.addRequiredPayloads(requiredPayloads);
        gpsQuery.setTermCountPayloadFactor(termCountPayloadFactor);
        if (queryName != null) {
            parseContext.addNamedQuery(queryName, gpsQuery);
        }
        
        return gpsQuery;
    }
}