package org.elasticsearch.index.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.search.spans.checkers.*;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;

/**
 *
 * @author Julien
 */
public class PayloadCheckerFactory {
    
    protected String NAME = "payloadChecker";
    
    protected static PayloadCheckerFactory instance = null;
    
    protected IntegerEncoder intEncoder = new IntegerEncoder();
    
    protected PayloadCheckerFactory()
    {
    }
    
    public static PayloadCheckerFactory getInstance()
    {
        if (instance==null)
            instance = new PayloadCheckerFactory();
        return instance;
    }
    
    public IPayloadChecker parseInnerQuery(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        IPayloadChecker checker = null;
        String type = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) { // NB: useless loop
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                if ("type".equals(currentFieldName)) {
                    type = parser.text();
                    
                    if (type.equals("And"))
                    {
                        return parseAndPayloadChecker(parseContext);
                    }
                    else if (type.equals("Or"))
                    {
                        return parseOrPayloadChecker(parseContext);
                    }
                    else if (type.equals("Xor"))
                    {
                        return parseXorPayloadChecker(parseContext);
                    }
                    else if (type.equals("Not"))
                    {
                        return parseNotPayloadChecker(parseContext);
                    }
                    else if (type.equals("Switch"))
                    {
                        return parseSwitchPayloadChecker(parseContext);
                    }
                    else if (type.equals("Grouped"))
                    {
                        return parseGroupedPayloadChecker(parseContext);
                    }
                    else if (type.equals("Null"))
                    {
                        return parseNullPayloadChecker(parseContext);
                    }
                    else if (type.equals("All"))
                    {
                        return parseAllPayloadChecker(parseContext);
                    }
                    else if (type.equals("One"))
                    {
                        return parseOnePayloadChecker(parseContext);
                    }
                    else if (type.equals("Field"))
                    {
                        return parseFieldChecker(parseContext);
                    }
                    else if (type.equals("If"))
                    {
                        return parseIfChecker(parseContext);
                    }
                    else if (type.equals("IfElse"))
                    {
                        return parseIfElseChecker(parseContext);
                    }
                    else if (type.equals("BeforeAnother"))
                    {
                        return parseBeforeAnotherChecker(parseContext);
                    }
                    else
                    {
                        throw new QueryParsingException(parseContext.index(), "["+NAME+"] filter does not support type [" + type + "]");
                    }
                } else {
                    throw new QueryParsingException(parseContext.index(), "["+NAME+"] filter does not support [" + currentFieldName + "]");
                }
            }
        }

        return checker;
    }
    
    
    public ArrayList<IPayloadChecker> parseArrayChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        ArrayList<IPayloadChecker> checkers = new ArrayList<>();
        String type = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
            if (token == XContentParser.Token.START_OBJECT)
            {
                IPayloadChecker checker = parseInnerQuery(parseContext);
                checkers.add(checker);
            }
        }
        
        return checkers;
    }
    
    public AndPayloadChecker parseAndPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        ArrayList<IPayloadChecker> checkers = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_ARRAY)
            {
                if ("checkers".equals(currentFieldName))
                    checkers = parseArrayChecker(parseContext);
            }
        }
        
        AndPayloadChecker checker = new AndPayloadChecker(checkers.toArray(new IPayloadChecker[checkers.size()]));
        return checker;
    }
    
    public OrPayloadChecker parseOrPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        ArrayList<IPayloadChecker> checkers = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_ARRAY)
            {
                if ("checkers".equals(currentFieldName))
                    checkers = parseArrayChecker(parseContext);
            }
        }
        
        OrPayloadChecker checker = new OrPayloadChecker(checkers.toArray(new IPayloadChecker[checkers.size()]));
        return checker;
    }
    
    public NotPayloadChecker parseNotPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        IPayloadChecker innerchecker = null;
       
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_OBJECT)
            {
                if ("checker".equals(currentFieldName))
                    innerchecker = parseInnerQuery(parseContext);
            }
        }
        
        NotPayloadChecker checker = new NotPayloadChecker(innerchecker);
        return checker;
    }
    
    public XorPayloadChecker parseXorPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        ArrayList<IPayloadChecker> checkers = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_ARRAY)
            {
                if ("checkers".equals(currentFieldName))
                    checkers = parseArrayChecker(parseContext);
            }
        }
        
        XorPayloadChecker checker = new XorPayloadChecker(checkers.toArray(new IPayloadChecker[checkers.size()]));
        return checker;
    }
    
    public ArrayList<SwitchPayloadConditionClause> parseArrayClauses(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        ArrayList<SwitchPayloadConditionClause> clauses = new ArrayList<>();
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY)
        {
            if (token == XContentParser.Token.FIELD_NAME)
            {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_OBJECT)
            {
                IPayloadChecker checker = parseInnerQuery(parseContext);
                SwitchPayloadConditionClause clause = new SwitchPayloadConditionClause(currentFieldName, checker);
                clauses.add(clause);
            }
        }
        
        return clauses;
    }
    
    public SwitchPayloadChecker parseSwitchPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        ArrayList<SwitchPayloadConditionClause> clauses = null;
        String field = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_ARRAY)
            {
                if ("clauses".equals(currentFieldName))
                    clauses = parseArrayClauses(parseContext);
            }
            else if (token.isValue())
            {
                if (currentFieldName.equals("field"))
                {
                    field = currentFieldName;
                }
            }
        }
        
        SwitchPayloadChecker checker = new SwitchPayloadChecker(field,clauses.toArray(new SwitchPayloadConditionClause[clauses.size()]));
        return checker;
    }
    
    public GroupedPayloadChecker parseGroupedPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
        }
        return new GroupedPayloadChecker();
    }
    
    public NullPayloadChecker parseNullPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
        }
        return new NullPayloadChecker();
    }
    
    public FieldChecker parseFieldChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        String field = null;
        String value = null;
        BytesRef payload= null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                switch (currentFieldName) {
                    default:
                        throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [requiredPayloads] does not support [" + currentFieldName + "]");
                    case "field":
                        field = parser.text();
                        break;
                    case "value":
                        value = parser.text();
                        break;
                }
            }
        }
        
        FieldChecker checker = null;
        if (field==null)
            checker = new FieldChecker(value);
        else
            checker = new FieldChecker(field,value);
        
        return checker;
    }
    
    public OnePayloadChecker parseOnePayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        BytesRef payload= null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                switch (currentFieldName) {
                    default:
                        throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [requiredPayloads] does not support [" + currentFieldName + "]");
                    case "payload":
                        if (token == XContentParser.Token.VALUE_EMBEDDED_OBJECT)
                        {
                            byte[] bytes = parser.binaryValue();
                            payload = new BytesRef(bytes);
                        }
                        else if (token == XContentParser.Token.VALUE_NUMBER) {
                                if (parser.numberType() == XContentParser.NumberType.INT) {
                                    payload = intEncoder.encode(Integer.toString(parser.intValue()).toCharArray());
                                }
                                else
                                    throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        }
                        else
                            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        break;
                }
            }
        }
        
        OnePayloadChecker checker = new OnePayloadChecker(payload.bytes);
        
        return checker;
    }
    
    public AllPayloadChecker parseAllPayloadChecker(QueryParseContext parseContext) throws IOException
    {
        XContentParser parser = parseContext.parser();
        
        BytesRef payload= null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                switch (currentFieldName) {
                    default:
                        throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [requiredPayloads] does not support [" + currentFieldName + "]");
                    case "payload":
                        if (token == XContentParser.Token.VALUE_EMBEDDED_OBJECT)
                        {
                            byte[] bytes = parser.binaryValue();
                            payload = new BytesRef(bytes);
                        }
                        else if (token == XContentParser.Token.VALUE_NUMBER) {
                                if (parser.numberType() == XContentParser.NumberType.INT) {
                                    payload = intEncoder.encode(Integer.toString(parser.intValue()).toCharArray());
                                }
                                else
                                    throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        }
                        else
                            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        break;
                }
            }
        }
        
        AllPayloadChecker checker = new AllPayloadChecker(payload.bytes);
        
        return checker;
    }
    
    protected IPayloadChecker parseIfChecker(QueryParseContext parseContext) throws IOException {
        XContentParser parser = parseContext.parser();
        
        IPayloadChecker condition = null;
        IPayloadChecker then = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_OBJECT)
            {
                if ("condition".equals(currentFieldName))
                    condition = parseInnerQuery(parseContext);
                else if ("then".equals(currentFieldName))
                    then = parseInnerQuery(parseContext);
                else
                    throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support "+currentFieldName);
            }
        }
        
        if (condition==null | then==null)
            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] need condition and action");
        
        IfPayloadChecker checker = new IfPayloadChecker(condition, then);
        return checker;
    }
    
    protected IPayloadChecker parseIfElseChecker(QueryParseContext parseContext) throws IOException {
        XContentParser parser = parseContext.parser();
        
        IPayloadChecker condition = null;
        IPayloadChecker then = null;
        IPayloadChecker elseChecker = null; 
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token == XContentParser.Token.START_OBJECT)
            {
                if ("condition".equals(currentFieldName))
                    condition = parseInnerQuery(parseContext);
                else if ("then".equals(currentFieldName))
                    then = parseInnerQuery(parseContext);
                else if ("else".equals(currentFieldName))
                    elseChecker = parseInnerQuery(parseContext);
                else
                    throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support "+currentFieldName);
            }
        }
        
        if (condition==null | then==null)
            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] need condition and action");
        
        IfPayloadElseChecker checker = new IfPayloadElseChecker(condition, then, elseChecker);
        return checker;
    }
    
    protected IPayloadChecker parseBeforeAnotherChecker(QueryParseContext parseContext) throws IOException {
        XContentParser parser = parseContext.parser();
        
        BytesRef payloadbefore = null;
        BytesRef another = null;
        
        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT)
        {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            }
            else if (token.isValue())
            {
                switch (currentFieldName) {
                    default:
                        throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [requiredPayloads] does not support [" + currentFieldName + "]");
                    case "payloadbefore":
                        if (token == XContentParser.Token.VALUE_EMBEDDED_OBJECT)
                        {
                            byte[] bytes = parser.binaryValue();
                            payloadbefore = new BytesRef(bytes);
                        }
                        else if (token == XContentParser.Token.VALUE_NUMBER) {
                                if (parser.numberType() == XContentParser.NumberType.INT) {
                                    payloadbefore = intEncoder.encode(Integer.toString(parser.intValue()).toCharArray());
                                }
                                else
                                    throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        }
                        else
                            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        break;
                    case "another":
                        if (token == XContentParser.Token.VALUE_EMBEDDED_OBJECT)
                        {
                            byte[] bytes = parser.binaryValue();
                            another = new BytesRef(bytes);
                        }
                        else if (token == XContentParser.Token.VALUE_NUMBER) {
                                if (parser.numberType() == XContentParser.NumberType.INT) {
                                    another = intEncoder.encode(Integer.toString(parser.intValue()).toCharArray());
                                }
                                else
                                    throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        }
                        else
                            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] does not support other numberType than INT");
                        break;
                }
            }
        }
        
        if (payloadbefore==null || another==null)
            throw new QueryParsingException(parseContext.index(), "[" + NAME + "] [payload] need 2 payloads");
        
        PayloadBeforeAnotherChecker checker = new PayloadBeforeAnotherChecker(payloadbefore, another);
        return checker;
    }
    
    public void doXContent(IPayloadChecker checker, XContentBuilder builder, Params params) throws IOException
    {
        if (checker instanceof AndPayloadChecker)
            doAndPayloadCheckerXContent((AndPayloadChecker)checker,builder,params);
        else if (checker instanceof OrPayloadChecker)
            doOrPayloadCheckerXContent((OrPayloadChecker)checker,builder,params);
        else if (checker instanceof XorPayloadChecker)
            doXorPayloadCheckerXContent((XorPayloadChecker)checker,builder,params);
        else if (checker instanceof NotPayloadChecker)
            doNotPayloadCheckerXContent((NotPayloadChecker)checker,builder,params);
        else if (checker instanceof SwitchPayloadChecker)
            doSwitchPayloadCheckerXContent((SwitchPayloadChecker)checker,builder,params);
        else if (checker instanceof GroupedPayloadChecker)
            doGroupedPayloadCheckerXContent((GroupedPayloadChecker)checker,builder,params);
        else if (checker instanceof AllPayloadChecker)
            doAllPayloadCheckerXContent((AllPayloadChecker)checker,builder,params);
        else if (checker instanceof OnePayloadChecker)
            doOnePayloadCheckerXContent((OnePayloadChecker)checker,builder,params);
        else if (checker instanceof FieldChecker)
            doFieldCheckerXContent((FieldChecker)checker,builder,params);
        else if (checker instanceof IfPayloadChecker)
            doIfPayloadCheckerXContent((IfPayloadChecker)checker,builder,params);
        else if (checker instanceof IfPayloadElseChecker)
            doIfPayloadElseCheckerXContent((IfPayloadElseChecker)checker,builder,params);
        else if (checker instanceof PayloadBeforeAnotherChecker)
            doPayloadBeforeAnotherCheckerXContent((PayloadBeforeAnotherChecker)checker,builder,params);
        else if (checker instanceof NullPayloadChecker)
            doNullPayloadCheckerXContent((NullPayloadChecker)checker,builder,params);
    }

    protected void doAndPayloadCheckerXContent(AndPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        AndPayloadChecker a = (AndPayloadChecker)checker;
        
        builder.field("type", "And");
        builder.startArray("checkers");
        for(int i=0;i<a.getCheckers().length;i++)
        {
            builder.startObject();
            doXContent(a.getCheckers()[i],builder,params);
            builder.endObject();
        }
        builder.endArray();
    }

    protected void doOrPayloadCheckerXContent(OrPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        OrPayloadChecker a = (OrPayloadChecker)checker;
        
        builder.field("type", "Or");
        builder.startArray("checkers");
        for(int i=0;i<a.getCheckers().length;i++)
        {
            builder.startObject();
            doXContent(a.getCheckers()[i],builder,params);
            builder.endObject();
        }
        builder.endArray();
    }

    protected void doXorPayloadCheckerXContent(XorPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        XorPayloadChecker a = (XorPayloadChecker)checker;
        
        builder.field("type", "Xor");
        builder.startArray("checkers");
        for(int i=0;i<a.getCheckers().length;i++)
        {
            builder.startObject();
            doXContent(a.getCheckers()[i],builder,params);
            builder.endObject();
        }
        builder.endArray();
    }

    protected void doSwitchPayloadCheckerXContent(SwitchPayloadChecker checker, XContentBuilder builder, Params params) throws IOException
    {
        SwitchPayloadChecker a = (SwitchPayloadChecker)checker;
        
        builder.field("type", "Switch");
        builder.field("field",a.getField());
        builder.startArray("clauses");
        Enumeration<String> eKeys = a.getClauses().keys();
        while(eKeys.hasMoreElements())
        {
            String key = eKeys.nextElement();
            
            builder.startObject(key);
            doXContent(a.getClauses().get(key), builder, params);
            builder.endObject();
        }
        builder.endArray();
    }
    
    protected void doNotPayloadCheckerXContent(NotPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        NotPayloadChecker a = (NotPayloadChecker)checker;
        
        builder.field("type", "Not");
        builder.startObject("checker");
        doXContent(a.getChecker(),builder,params);
        builder.endObject();
    }

    protected void doGroupedPayloadCheckerXContent(GroupedPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        GroupedPayloadChecker a = (GroupedPayloadChecker)checker;
        
        builder.field("type", "Grouped");
    }
    
    protected void doNullPayloadCheckerXContent(NullPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        NullPayloadChecker a = (NullPayloadChecker)checker;
        
        builder.field("type", "Null");
    }

    protected void doFieldCheckerXContent(FieldChecker checker, XContentBuilder builder, Params params) throws IOException {
        FieldChecker a = (FieldChecker)checker;
        
        builder.field("type", "Field");
        
        builder.field("field",a.getField());
        builder.field("value",a.getValue());
    }
    
    protected void doAllPayloadCheckerXContent(AllPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        AllPayloadChecker a = (AllPayloadChecker)checker;
        
        builder.field("type", "All");
        
        builder.field("payload",new BytesArray(a.getPayload()));
    }
    
    protected void doOnePayloadCheckerXContent(OnePayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        OnePayloadChecker a = (OnePayloadChecker)checker;
        
        builder.field("type", "One");
        
        builder.field("payload",new BytesArray(a.getPayload()));
    }

    protected void doIfPayloadCheckerXContent(IfPayloadChecker checker, XContentBuilder builder, Params params) throws IOException {
        IfPayloadChecker a = (IfPayloadChecker)checker;
        
        builder.field("type", "If");
        
        builder.startObject("condition");
        doXContent(a.getCondition(), builder, params);
        builder.endObject();
        
        builder.startObject("then");
        doXContent(a.getThen(), builder, params);
        builder.endObject();
    }

    protected void doIfPayloadElseCheckerXContent(IfPayloadElseChecker checker, XContentBuilder builder, Params params) throws IOException {
        IfPayloadElseChecker a = (IfPayloadElseChecker)checker;
        
        builder.field("type", "If");
        
        builder.startObject("condition");
        doXContent(a.getCondition(), builder, params);
        builder.endObject();
        
        builder.startObject("then");
        doXContent(a.getThen(), builder, params);
        builder.endObject();
        
        builder.startObject("else");
        doXContent(a.getElse(), builder, params);
        builder.endObject();
    }
    
    protected void doPayloadBeforeAnotherCheckerXContent(PayloadBeforeAnotherChecker checker, XContentBuilder builder, Params params) throws IOException {
        PayloadBeforeAnotherChecker a = (PayloadBeforeAnotherChecker)checker;
        
        builder.field("type", "BeforeAnother");
        
        builder.field("payloadbefore",new BytesArray(a.getPayloadbefore()));
        
        builder.field("another",new BytesArray(a.getAnother()));
    }
}