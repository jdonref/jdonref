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
import org.elasticsearch.index.mapper.object.ObjectMapper;
import org.elasticsearch.threadpool.ThreadPool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.index.mapper.ContentPath;
import org.elasticsearch.index.mapper.InternalMapper;

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
    
    public static class Defaults {
        public static final boolean ENABLED = true;
        public static final ContentPath.Type PATH_TYPE = ContentPath.Type.FULL;
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
            
            JDONREFv3AdresseTypeMapper objectMapper = createMapper(name, mappers);

            return (Y) objectMapper;
        }

        protected JDONREFv3AdresseTypeMapper createMapper(String name, Map<String, Mapper> mappers) {
            return new JDONREFv3AdresseTypeMapper(name, mappers);
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
    
    public JDONREFv3AdresseTypeMapper(String name, Map<String, Mapper> mappers) {
        this.name = name;
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
        
        if (token == XContentParser.Token.END_OBJECT) {
            token = parser.nextToken();
        }
        if (token == XContentParser.Token.START_OBJECT) {
            // if we are just starting an OBJECT, advance, this is the object we are parsing, we need the name first
            token = parser.nextToken();
        }
        
        while (token != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.START_OBJECT) {
                serializeObject(context, currentFieldName);
            } else if (token == XContentParser.Token.START_ARRAY) {
                serializeArray(context, currentFieldName);
            } else if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token == XContentParser.Token.VALUE_NULL) {
                serializeNullValue(context, currentFieldName);
            } else if (token == null) {
                throw new MapperParsingException("object mapping for [" + name + "] tried to parse as object, but got EOF, has a concrete value been provided to it?");
            } else if (token.isValue()) {
                serializeValue(context, currentFieldName, token);
            }
            token = parser.nextToken();
        }
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

    private void serializeArray(ParseContext context, String lastFieldName) throws IOException {
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
                    serializeArray(context, lastFieldName);
                } else if (token == XContentParser.Token.FIELD_NAME) {
                    lastFieldName = parser.currentName();
                } else if (token == XContentParser.Token.VALUE_NULL) {
                    serializeNullValue(context, lastFieldName);
                } else if (token == null) {
                    throw new MapperParsingException("object mapping for [" + name + "] with array for [" + arrayFieldName + "] tried to parse as array, but got EOF, is there a mismatch in types for the same field?");
                } else {
                    serializeValue(context, lastFieldName, token);
                }
            }
        }
    }

    private void serializeValue(final ParseContext context, String currentFieldName, XContentParser.Token token) throws IOException {
        if (currentFieldName == null) {
            throw new MapperParsingException("object mapping [" + name + "] trying to serialize a value with no field associated with it, current value [" + context.parser().textOrNull() + "]");
        }
        Mapper mapper = mappers.get(currentFieldName);
        if (mapper != null) {
            mapper.parse(context);
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