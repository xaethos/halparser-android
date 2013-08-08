package net.xaethos.android.halparser.serializers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.impl.BaseHALLink;
import net.xaethos.android.halparser.impl.BaseHALResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HALJsonSerializer
{
    private static final String HREF = "href";
    private static final String TEMPLATED = "templated";

    private static final String LINKS = "_links";
    private static final String EMBEDDED = "_embedded";

    private final JsonFactory mJsonFactory;

    public HALJsonSerializer() {
        mJsonFactory = new JsonFactory();
    }

    public HALResource parse(Reader reader) throws IOException {
        JsonParser jsonParser = mJsonFactory.createParser(reader);
        jsonParser.nextToken();
        HALResource resource = parseResource(jsonParser, new BaseHALResource.Builder());
        jsonParser.close();

        return resource;
    }

    public void write(HALResource resource, Writer writer) throws IOException {
        JsonGenerator jsonGenerator = mJsonFactory.createGenerator(writer);
        writeResource(jsonGenerator, resource);
        jsonGenerator.close();
    }

    // *** Parsing methods

    private void verifyObject(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
    }

    private HALResource parseResource(JsonParser parser, BaseHALResource.Builder builder) throws IOException {
        verifyObject(parser);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String name = parser.getCurrentName();
            parser.nextToken();
            if (LINKS.equals(name)) {
                parseLinks(parser, builder);
            }
            else if (EMBEDDED.equals(name)) {
                parseEmbedded(parser, builder);
            }
            else {
                builder.putProperty(name, parseValue(parser));
            }
        }

        return builder.build();
    }

    private Object parseValue(JsonParser parser) throws IOException {
        switch (parser.getCurrentToken()) {
        case START_ARRAY:
            ArrayList<Object> array = new ArrayList<Object>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                array.add(parseValue(parser));
            }
            return Collections.unmodifiableList(array);

        case START_OBJECT:
            verifyObject(parser);

            LinkedHashMap<String, Object> object = new LinkedHashMap<String, Object>();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String name = parser.getCurrentName();
                parser.nextToken();
                object.put(name, parseValue(parser));
            }
            return Collections.unmodifiableMap(object);

        case VALUE_TRUE:
            return true;
        case VALUE_FALSE:
            return false;

        case VALUE_NULL:
            return null;

        case VALUE_NUMBER_INT:
        case VALUE_NUMBER_FLOAT:
            switch (parser.getNumberType()) {
            case INT:
                return parser.getIntValue();
            case LONG:
                return parser.getLongValue();
            case FLOAT:
                return parser.getFloatValue();
            case DOUBLE:
                return parser.getDoubleValue();
            case BIG_DECIMAL:
                return parser.getDecimalValue();
            case BIG_INTEGER:
                return parser.getBigIntegerValue();
            }

        case VALUE_STRING:
            return parser.getText();

        default:
            throw new IOException("Unhandled JSON token: " + parser.getCurrentToken());
        }
    }

    private void parseLinks(JsonParser parser, BaseHALResource.Builder builder) throws IOException {
        verifyObject(parser);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String rel = parser.getCurrentName();

            if (parser.nextToken() == JsonToken.START_ARRAY) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    builder.putLink(parseLink(parser, rel));
                }
            }
            else {
                builder.putLink(parseLink(parser, rel));
            }
        }
    }

    private HALLink parseLink(JsonParser parser, String rel) throws IOException {
        verifyObject(parser);
        String href = null;
        HashMap<String, Object> attributes = new HashMap<String, Object>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String name = parser.getCurrentName();
            parser.nextToken();
            if (HREF.equals(name)) href = parser.getText();
            else attributes.put(name, parseValue(parser));
        }

        return new BaseHALLink(rel, href, attributes);
    }

    private void parseEmbedded(JsonParser parser, BaseHALResource.Builder builder) throws IOException {
        verifyObject(parser);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String rel = parser.getCurrentName();
            if (parser.nextToken() == JsonToken.START_ARRAY) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    builder.putResource(parseResource(parser, builder.buildResource()), rel);
                }
            }
            else {
                builder.putResource(parseResource(parser, builder.buildResource()), rel);
            }
        }
    }

    // *** Writing methods

    private void writeResource(JsonGenerator jsonGenerator, HALResource resource) throws IOException {
        jsonGenerator.writeStartObject();
        writeLinks(jsonGenerator, resource);
        for (HALProperty prop : resource.getProperties().values()) {
            jsonGenerator.writeObjectField(prop.getName(), prop.getValue());
        }
        writeEmbedded(jsonGenerator, resource);
        jsonGenerator.writeEndObject();
    }

    private void writeLinks(JsonGenerator jsonGenerator, HALResource resource) throws IOException {
        Set<String> relations = resource.getLinkRels();
        if (relations.size() > 0) {
            jsonGenerator.writeObjectFieldStart(LINKS);

            for (String rel : relations) {
                jsonGenerator.writeFieldName(rel);
                List<HALLink> links = resource.getLinks(rel);

                if (links.size() > 1) {
                    jsonGenerator.writeStartArray();
                    for (HALLink link : links) writeLinkObject(jsonGenerator, link);
                    jsonGenerator.writeEndArray();
                }
                else if (links.size() == 1) {
                    writeLinkObject(jsonGenerator, links.get(0));
                }
            }
            jsonGenerator.writeEndObject();
        }
    }

    private void writeEmbedded(JsonGenerator jsonGenerator, HALResource resource) throws IOException {
        Set<String> relations = resource.getResourceRels();
        if (relations.size() > 0) {
            jsonGenerator.writeObjectFieldStart(EMBEDDED);

            for (String rel : relations) {
                jsonGenerator.writeFieldName(rel);
                List<HALResource> resources = resource.getResources(rel);

                if (resources.size() > 1) {
                    jsonGenerator.writeStartArray();
                    for (HALResource embed : resources) writeResource(jsonGenerator, embed);
                    jsonGenerator.writeEndArray();
                }
                else if (resources.size() == 1) {
                    writeResource(jsonGenerator, resources.get(0));
                }
            }
            jsonGenerator.writeEndObject();
        }
    }

    private void writeLinkObject(JsonGenerator jsonGenerator, HALLink link) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField(HREF, link.getHref());

        for (Map.Entry<String, ?> entry : link.getAttributes().entrySet()) {
            String name = entry.getKey();
            if (TEMPLATED.equals(name)) continue;
            jsonGenerator.writeObjectField(name, entry.getValue());
        }

        if (link.isTemplated()) jsonGenerator.writeObjectField(TEMPLATED, true);

        jsonGenerator.writeEndObject();
    }

}
