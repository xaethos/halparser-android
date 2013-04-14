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
import android.util.JsonReader;
import android.util.JsonToken;

public class HALJsonParser implements HALEnclosure
{
    private final URI mURI;

    private static final String LINKS = "_links";
    private static final String EMBEDDED = "_embedded";

    public HALJsonParser(URI baseURI) {
        if (!baseURI.isAbsolute()) throw new IllegalArgumentException("Base URI must be absolute");
        mURI = baseURI;
    }

    public HALJsonParser(String baseURI) {
        this(URI.create(baseURI));
    }

    @Override
    public URI getBaseURI() {
        return mURI;
    }

    @Override
    public HALEnclosure getEnclosure() {
        return null;
    }

    public HALResource parse(Reader reader) throws IOException {
        JsonReader jsonReader = new JsonReader(reader);
        HALResource resource = parseResource(jsonReader, new BaseHALResource.Builder(this));
        jsonReader.close();

        return resource;
    }

    // *** Parsing methods

    private HALResource parseResource(JsonReader reader, BaseHALResource.Builder builder) throws IOException {
        reader.beginObject();
        while (reader.peek() == JsonToken.NAME) {
            String name = reader.nextName();
            if (LINKS.equals(name)) {
                parseLinks(reader, builder);
            }
            else if (EMBEDDED.equals(name)) {
                parseEmbedded(reader, builder);
            }
            else {
                builder.putProperty(name, parseValue(reader));
            }
        }
        reader.endObject();

        return builder.build();
    }

    private Object parseValue(JsonReader reader) throws IOException {
        switch (reader.peek()) {
        case BEGIN_ARRAY:
            reader.beginArray();
            ArrayList<Object> array = new ArrayList<Object>();
            while (reader.peek() != JsonToken.END_ARRAY)
                array.add(parseValue(reader));
            reader.endArray();
            return Collections.unmodifiableList(array);

        case BEGIN_OBJECT:
            reader.beginObject();
            LinkedHashMap<String, Object> object = new LinkedHashMap<String, Object>();
            while (reader.peek() == JsonToken.NAME)
                object.put(reader.nextName(), parseValue(reader));
            reader.endObject();
            return Collections.unmodifiableMap(object);

        case BOOLEAN:
            return reader.nextBoolean();

        case NULL:
            reader.nextNull();
            return null;

        case NUMBER:
            try {
                return reader.nextInt();
            }
            catch (NumberFormatException e) {
                try {
                    return reader.nextLong();
                }
                catch (NumberFormatException e2) {
                    return reader.nextDouble();
                }
            }

        case STRING:
        default:
            return reader.nextString();
        }
    }

    private void parseLinks(JsonReader reader, BaseHALResource.Builder builder) throws IOException {
        reader.beginObject();
        while (reader.peek() == JsonToken.NAME) {
            String rel = reader.nextName();

            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                reader.beginArray();
                while (reader.peek() != JsonToken.END_ARRAY) {
                    builder.putLink(parseLink(reader, builder.buildLink(rel)));
                }
                reader.endArray();
            }
            else {
                builder.putLink(parseLink(reader, builder.buildLink(rel)));
            }
        }
        reader.endObject();
    }

    private HALLink parseLink(JsonReader reader, BaseHALLink.Builder builder) throws IOException {
        reader.beginObject();
        while (reader.peek() == JsonToken.NAME) {
            builder.putAttribute(reader.nextName(), parseValue(reader));
        }
        reader.endObject();

        return builder.build();
    }

    private void parseEmbedded(JsonReader reader, BaseHALResource.Builder builder) throws IOException {
        reader.beginObject();
        while (reader.peek() == JsonToken.NAME) {
            String rel = reader.nextName();
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                reader.beginArray();
                while (reader.peek() != JsonToken.END_ARRAY) {
                    builder.putResource(parseResource(reader, builder.buildResource()), rel);
                }
                reader.endArray();
            }
            else {
                builder.putResource(parseResource(reader, builder.buildResource()), rel);
            }
        }
        reader.endObject();
    }

    // *** Parcelable implementation

    public static final Parcelable.Creator<HALJsonParser> CREATOR = new Creator<HALJsonParser>() {
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
