package net.xaethos.android.halparser.serializers;

import android.os.Parcel;
import android.os.Parcelable;

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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HALJsonSerializer implements Parcelable
{
    private static final String LINKS = "_links";
    private static final String EMBEDDED = "_embedded";

    private final URI mURI;
    private final JsonFactory mJsonFactory;

    public HALJsonSerializer(URI baseURI) {
        mJsonFactory = new JsonFactory();

        if (!baseURI.isAbsolute()) throw new IllegalArgumentException("Base URI must be absolute");
        mURI = baseURI;
    }

    public HALJsonSerializer(String baseURI) {
        this(URI.create(baseURI));
    }

    public URI getBaseURI() {
        return mURI;
    }

    public HALResource parse(Reader reader) throws IOException {
        JsonParser jsonParser = mJsonFactory.createParser(reader);
        jsonParser.nextToken();
        HALResource resource = parseResource(jsonParser, new BaseHALResource.Builder(mURI));
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
                    builder.putLink(parseLink(parser, builder.buildLink(rel)));
                }
            }
            else {
                builder.putLink(parseLink(parser, builder.buildLink(rel)));
            }
        }
    }

    private HALLink parseLink(JsonParser parser, BaseHALLink.Builder builder) throws IOException {
        verifyObject(parser);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String name = parser.getCurrentName();
            parser.nextToken();
            builder.putAttribute(name, parseValue(parser));
        }

        return builder.build();
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
        for (Map.Entry<String, Object> entry : link.getAttributes().entrySet()) {
            String name = entry.getKey();
            if (HALLink.ATTR_REL.equals(name)) continue;
            if (HALLink.ATTR_TEMPLATED.equals(name)) {
                jsonGenerator.writeObjectField(HALLink.ATTR_TEMPLATED, link.isTemplated());
            }
            else {
                jsonGenerator.writeObjectField(name, entry.getValue());
            }
        }

        jsonGenerator.writeEndObject();
    }

    // *** Parcelable implementation

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<HALJsonSerializer> CREATOR = new Parcelable.Creator<HALJsonSerializer>() {
        @Override
        public HALJsonSerializer createFromParcel(Parcel source) {
            return new HALJsonSerializer(source);
        }

        @Override
        public HALJsonSerializer[] newArray(int size) {
            return new HALJsonSerializer[size];
        }

    };

    public HALJsonSerializer(Parcel in) {
        this(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mURI.toString());
    }

}
