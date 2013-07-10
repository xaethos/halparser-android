package net.xaethos.android.halparser;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import net.xaethos.android.halparser.impl.BaseHALLink;
import net.xaethos.android.halparser.impl.BaseHALResource;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class HALJsonParser implements Parcelable
{
    private static final String LINKS = "_links";
    private static final String EMBEDDED = "_embedded";

    private final URI mURI;
    private final JsonFactory mJsonFactory;

    public HALJsonParser(URI baseURI) {
        mJsonFactory = new JsonFactory();

        if (!baseURI.isAbsolute()) throw new IllegalArgumentException("Base URI must be absolute");
        mURI = baseURI;
    }

    public HALJsonParser(String baseURI) {
        this(URI.create(baseURI));
    }

    public URI getBaseURI() {
        return mURI;
    }

    public HALResource parse(Reader reader) throws IOException {
        JsonParser jsonParser = mJsonFactory.createJsonParser(reader);
        jsonParser.nextToken();
        HALResource resource = parseResource(jsonParser, new BaseHALResource.Builder(mURI));
        jsonParser.close();

        return resource;
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

    // *** Parcelable implementation

    public static final Parcelable.Creator<HALJsonParser> CREATOR = new Parcelable.Creator<HALJsonParser>() {
        @Override
        public HALJsonParser createFromParcel(Parcel source) {
            return new HALJsonParser(source);
        }

        @Override
        public HALJsonParser[] newArray(int size) {
            return new HALJsonParser[size];
        }

    };

    public HALJsonParser(Parcel in) {
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
