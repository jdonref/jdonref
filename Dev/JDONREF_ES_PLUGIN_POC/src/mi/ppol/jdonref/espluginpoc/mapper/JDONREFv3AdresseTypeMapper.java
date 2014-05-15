package mi.ppol.jdonref.espluginpoc.mapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.FieldMapperListener;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.mapper.MergeContext;
import org.elasticsearch.index.mapper.MergeMappingException;
import org.elasticsearch.index.mapper.ObjectMapperListener;
import org.elasticsearch.index.mapper.ParseContext;
import org.elasticsearch.index.mapper.object.ArrayValueMapperParser;
import org.elasticsearch.threadpool.ThreadPool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.index.mapper.ContentPath;
import org.elasticsearch.index.mapper.InternalMapper;

import org.elasticsearch.index.mapper.core.AbstractFieldMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper.ValueAndBoost;
import static org.elasticsearch.index.mapper.core.TypeParsers.parsePathType;

/**
 *
 * @author Julien
 */
public class JDONREFv3AdresseTypeMapper implements Mapper {
    
    public static final String CONTENT_TYPE = "adresse";
    private final String name;
    private ThreadPool threadPool;
    private Settings settings;
    private volatile ImmutableOpenMap<String, Mapper> mappers = ImmutableOpenMap.of();
    private final ContentPath.Type pathType;
    private final String LIGNE4 = "ligne4";
    private final String LIGNE6 = "ligne6";
    private final String LIGNE7 = "ligne7";
    private final String CODEDEPARTEMENT = "code_departement";
    private final String FULLNAME = "fullName";
    
    public static class Defaults {
        public static final boolean ENABLED = true;
        public static final ContentPath.Type PATH_TYPE = ContentPath.Type.FULL;
        public static final FieldType FULLNAME_FIELD_TYPE = new FieldType(AbstractFieldMapper.Defaults.FIELD_TYPE);
    }
    
    public static class Builder<T extends Builder, Y extends JDONREFv3AdresseTypeMapper> extends Mapper.Builder<T, Y> {

        protected final List<Mapper.Builder> mappersBuilders = new ArrayList<Mapper.Builder>();
        
        protected ContentPath.Type pathType = Defaults.PATH_TYPE;
        
        public Builder(String name) {
            super(name);
            this.builder = (T) this;
        }
        
        public T add(Mapper.Builder builder) {
            mappersBuilders.add(builder);
            return this.builder;
        }
        
        public T pathType(ContentPath.Type pathType) {
            this.pathType = pathType;
            return builder;
        }
        
        @Override
        public Y build(BuilderContext context) {
            ContentPath.Type origPathType = context.path().pathType();
            context.path().pathType(pathType);
            context.path().add(name);
            
            Map<String, Mapper> mappers = new HashMap<String, Mapper>();
            for (Mapper.Builder builder : mappersBuilders) {
                Mapper mapper = builder.build(context);
                mappers.put(mapper.name(), mapper);
            }
            context.path().pathType(origPathType);
            context.path().remove();
            
            JDONREFv3AdresseTypeMapper objectMapper = createMapper(name, mappers, pathType);

            return (Y) objectMapper;
        }

        protected JDONREFv3AdresseTypeMapper createMapper(String name, Map<String, Mapper> mappers, ContentPath.Type pathType) {
            return new JDONREFv3AdresseTypeMapper(name, mappers, pathType);
        }
    }
    
    public static class TypeParser implements Mapper.TypeParser {
        @Override
        public Mapper.Builder parse(String name, Map<String, Object> node, ParserContext parserContext) throws MapperParsingException {
            Map<String, Object> objectNode = node;
            JDONREFv3AdresseTypeMapper.Builder builder = new JDONREFv3AdresseTypeMapper.Builder(name);

            for (Map.Entry<String, Object> entry : objectNode.entrySet()) {
                String fieldName = Strings.toUnderscoreCase(entry.getKey());
                Object fieldNode = entry.getValue();

                if (fieldName.equals("type")) {
                    String type = fieldNode.toString();
                    if (!type.equals(CONTENT_TYPE)) {
                        throw new MapperParsingException("Trying to parse an object but has a different type [" + type + "] for [" + name + "]");
                    }
                }
                else if (fieldName.equals("path")) {
                    builder.pathType(parsePathType(name, fieldNode.toString()));
                }
                else if (fieldName.equals("properties"))
                {
                    if (fieldNode instanceof Collection && ((Collection) fieldNode).isEmpty()) {
                        // nothing to do here, empty (to support "adresse_properties: []" case)
                    } else if (!(fieldNode instanceof Map)) {
                        throw new ElasticsearchParseException("adresse_properties must be a map type");
                    } else {
                        parseProperties(builder, (Map<String, Object>) fieldNode, parserContext);
                    }
                } else {
                    processField(builder, fieldName, fieldNode);
                }
            }

            return builder;
        }

        private void parseProperties(JDONREFv3AdresseTypeMapper.Builder objBuilder, Map<String, Object> propsNode, ParserContext parserContext) {
            for (Map.Entry<String, Object> entry : propsNode.entrySet()) {
                String propName = entry.getKey();
                Map<String, Object> propNode = (Map<String, Object>) entry.getValue();

                String type;
                Object typeNode = propNode.get("type");
                if (typeNode != null) {
                    type = typeNode.toString();
                } else {
                    // lets see if we can derive this...
                    if (propNode.get("properties") != null) {
                        type = JDONREFv3AdresseTypeMapper.CONTENT_TYPE;
                    } else {
                        throw new MapperParsingException("No type specified for property [" + propName + "]");
                    }
                }

                Mapper.TypeParser typeParser = parserContext.typeParser(type);
                if (typeParser == null) {
                    throw new MapperParsingException("No handler for type [" + type + "] declared on field [" + propName + "]");
                }
                objBuilder.add(typeParser.parse(propName, propNode, parserContext));
            }
        }

        protected void processField(Builder builder, String fieldName, Object fieldNode) {

        }
    }
    
    public JDONREFv3AdresseTypeMapper(String name, Map<String, Mapper> mappers,ContentPath.Type pathType) {
        this.name = name;
        this.pathType = pathType;
        if (mappers != null) {
            this.mappers = ImmutableOpenMap.builder(this.mappers).putAll(mappers).build();
        }
    }
    
    @Override
    public String name() {
        return name;
    }

    @Override
    public void parse(ParseContext context) throws IOException {
        
        XContentParser parser = context.parser();
        
        String currentFieldName = parser.currentName();
        XContentParser.Token token = parser.currentToken();
        if (token == XContentParser.Token.VALUE_NULL) {
            // the object is null ("obj1" : null), simply bail
            return;
        }
        
        ContentPath.Type origPathType = context.path().pathType();
        context.path().pathType(pathType);
        
        if (token == XContentParser.Token.END_OBJECT) {
            token = parser.nextToken();
        }
        if (token == XContentParser.Token.START_OBJECT) {
            // if we are just starting an OBJECT, advance, this is the object we are parsing, we need the name first
            token = parser.nextToken();
        }
        
        boolean isThereLigne4   = false;
        boolean isThereLigne6   = false;
        boolean isThereLigne7   = false;
        boolean isThereCodeDepartement   = false;
        boolean isThereFullName = false;
        
        HashMap<String,String> values = new HashMap<String,String>();
        
        while (token != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.START_OBJECT) {
                serializeObject(context, currentFieldName);
            } else if (token == XContentParser.Token.START_ARRAY) {
                serializeArray(context, currentFieldName,values);
            } else if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
                if (currentFieldName.equals(FULLNAME))
                    isThereFullName = true;
                else if (currentFieldName.equals(LIGNE4))
                    isThereLigne4 = true;
                else if (currentFieldName.equals(LIGNE6))
                    isThereLigne6 = true;
                else if (currentFieldName.equals(LIGNE7))
                    isThereLigne7 = true;
                else if (currentFieldName.equals(CODEDEPARTEMENT))
                    isThereCodeDepartement = true;
            } else if (token == XContentParser.Token.VALUE_NULL) {
                serializeNullValue(context, currentFieldName);
            } else if (token == null) {
                throw new MapperParsingException("object mapping for [" + name + "] tried to parse as object, but got EOF, has a concrete value been provided to it?");
            } else if (token.isValue()) {
                serializeValue(context, currentFieldName, token,values);
            }
            token = parser.nextToken();
        }
        
        if (!isThereLigne4)
            addLigne4(context,values);
        if (!isThereLigne6)
            addLigne6(context,values);
        if (!isThereLigne7)
            addLigne7(context,values);
        if (!isThereFullName)
            addFullName(context,values);
        
        context.path().pathType(origPathType);
    }
    
    
    private void addLigne4(ParseContext context,HashMap<String,String> values) throws IOException
    {
        ValueAndBoost value = new ValueAndBoost(getLigne4(values),1.0f);
        
        Mapper mapper = mappers.get(LIGNE4);
        if (mapper!=null)
        {
            context.externalValue(value.value());
            mapper.parse(context);
        }
    }
    
    private void addLigne6(ParseContext context,HashMap<String,String> values) throws IOException
    {
        ValueAndBoost value = new ValueAndBoost(getLigne6(values),1.0f);
        
        Mapper mapper = mappers.get(LIGNE6);
        if (mapper!=null)
        {
            context.externalValue(value.value());
            mapper.parse(context);
        }
    }
    
    private void addLigne7(ParseContext context,HashMap<String,String> values) throws IOException
    {
        ValueAndBoost value = new ValueAndBoost(getLigne7(values),1.0f);
        
        Mapper mapper = mappers.get(LIGNE7);
        if (mapper!=null)
        {
            context.externalValue(value.value());
            mapper.parse(context);
        }
    }
    
    private void addFullName(ParseContext context,HashMap<String,String> values) throws IOException
    {
        ValueAndBoost value = new ValueAndBoost(getFullName(values),1.0f);
        
        Mapper mapper = mappers.get(FULLNAME);
        if (mapper!=null)
        {
            context.externalValue(value.value());
            mapper.parse(context);
            System.out.println(context.doc().getField(FULLNAME).stringValue());
        }
    }
    
    private String addString(String chaine,String toAdd)
    {
        if (toAdd!=null)
        {
            if (chaine.length()>0) chaine += " ";
            chaine += toAdd;
        }
        return chaine;
    }
    
    private String getLigne4(HashMap<String,String> values)
    {
        String fullName = addString("",values.get("numero"));
        fullName = addString(fullName,values.get("repetition"));
        fullName = addString(fullName,values.get("type_de_voie"));
        fullName = addString(fullName,values.get("article"));
        fullName = addString(fullName,values.get("libelle"));
        
        return fullName;
    }
    
    private String getLigne6(HashMap<String,String> values)
    {
        String fullName = addString("",values.get("code_postal"));
        fullName = addString(fullName,values.get("commune"));
        fullName = addString(fullName,values.get("code_arrondissement"));
        
        if (fullName.trim().length()==0)
            fullName = addString(fullName,values.get("code_departement"));
        
        return fullName;
    }
    
    private String getLigne7(HashMap<String,String> values)
    {
        String fullName = addString("",values.get("pays"));
        
        return fullName;
    }
    
    private String getFullName(HashMap<String,String> values)
    {
        String fullName = addString("",getLigne4(values));
        fullName = addString(fullName,getLigne6(values));
        fullName = addString(fullName,values.get("pays"));
        
        return fullName;
    }

    private void serializeNullValue(ParseContext context, String lastFieldName) throws IOException {
        // we can only handle null values if we have mappings for them
        Mapper mapper = mappers.get(lastFieldName);
        if (mapper != null) {
            mapper.parse(context);
        }
    }

    private void serializeObject(final ParseContext context, String currentFieldName) throws IOException {
        if (currentFieldName == null) {
            throw new MapperParsingException("object mapping [" + name + "] trying to serialize an object with no field associated with it, current value [" + context.parser().textOrNull() + "]");
        }
        context.path().add(currentFieldName);

        Mapper objectMapper = mappers.get(currentFieldName);
        if (objectMapper != null) {
            objectMapper.parse(context);
        } // no dynamic mapping for now

        context.path().remove();
    }

    private void serializeArray(ParseContext context, String lastFieldName,HashMap<String,String> values) throws IOException {
        String arrayFieldName = lastFieldName;
        Mapper mapper = mappers.get(lastFieldName);
        if (mapper != null && mapper instanceof ArrayValueMapperParser) {
            mapper.parse(context);
        } else {
            XContentParser parser = context.parser();
            XContentParser.Token token;
            while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                if (token == XContentParser.Token.START_OBJECT) {
                    serializeObject(context, lastFieldName);
                } else if (token == XContentParser.Token.START_ARRAY) {
                    serializeArray(context, lastFieldName, values);
                } else if (token == XContentParser.Token.FIELD_NAME) {
                    lastFieldName = parser.currentName();
                } else if (token == XContentParser.Token.VALUE_NULL) {
                    serializeNullValue(context, lastFieldName);
                } else if (token == null) {
                    throw new MapperParsingException("object mapping for [" + name + "] with array for [" + arrayFieldName + "] tried to parse as array, but got EOF, is there a mismatch in types for the same field?");
                } else {
                    serializeValue(context, lastFieldName, token,values);
                }
            }
        }
    }
    
    private void serializeValue(final ParseContext context, String currentFieldName, XContentParser.Token token,HashMap<String,String> values) throws IOException {
        if (currentFieldName == null) {
            throw new MapperParsingException("object mapping [" + name + "] trying to serialize a value with no field associated with it, current value [" + context.parser().textOrNull() + "]");
        }
        Mapper mapper = mappers.get(currentFieldName);
        if (mapper != null) {
            mapper.parse(context);
            IndexableField field = context.doc().getFields().get(context.doc().getFields().size()-1);
            values.put(field.name(),field.stringValue());
        } // no dynamic mapping for now
    }

    @Override
    public void merge(Mapper arg0, MergeContext arg1) throws MergeMappingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void traverse(FieldMapperListener fieldMapperListener) {
        for (ObjectObjectCursor<String, Mapper> cursor : mappers) {
            cursor.value.traverse(fieldMapperListener);
        }
    }

    @Override
    public void traverse(ObjectMapperListener objectMapperListener) {
        // objectMapperListener.objectMapper(this); // not for now
        for (ObjectObjectCursor<String, Mapper> cursor : mappers) {
            cursor.value.traverse(objectMapperListener);
        }
    }

    @Override
    public void close() {
        for (ObjectObjectCursor<String, Mapper> cursor : mappers) {
            cursor.value.close();
        }
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        toXContent(builder, params, null, Mapper.EMPTY_ARRAY);
        return builder;
    }

    private void toXContent(XContentBuilder builder, Params params, Object object, Mapper[] EMPTY_ARRAY) throws IOException {
        builder.startObject(name);
        
        builder.field("type", CONTENT_TYPE);
        
        // sort the mappers so we get consistent serialization format
        TreeMap<String, Mapper> sortedMappers = new TreeMap<String, Mapper>();
        for (ObjectObjectCursor<String, Mapper> cursor : mappers) {
            sortedMappers.put(cursor.key, cursor.value);
        }

        // check internal mappers first (this is only relevant for root object)
        for (Mapper mapper : sortedMappers.values()) {
            if (mapper instanceof InternalMapper) {
                mapper.toXContent(builder, params);
            }
        }
        
        if (pathType != Defaults.PATH_TYPE)
        {
            builder.field("path",pathType.name().toLowerCase(Locale.ROOT));
        }

        if (!mappers.isEmpty()) {
            builder.startObject("properties");
            for (Mapper mapper : sortedMappers.values()) {
                if (!(mapper instanceof InternalMapper)) {
                    mapper.toXContent(builder, params);
                }
            }
            builder.endObject();
        }
        builder.endObject();
    }
}