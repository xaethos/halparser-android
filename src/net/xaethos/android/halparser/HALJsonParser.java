package net.xaethos.android.halparser;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import net.xaethos.android.halparser.impl.BaseHALResource;
import android.util.JsonReader;
import android.util.JsonToken;

public class HALJsonParser implements HALEnclosure
{
    private final URI mURI;

    private static final String LINKS = "_links";

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
        HALResource resource = parseResource(jsonReader, this);
        jsonReader.close();

        return resource;
    }

    // *** Helper methods

    private HALResource parseResource(JsonReader reader, HALEnclosure parent) throws IOException {
        BaseHALResource.Builder builder = new BaseHALResource.Builder(parent);

        reader.beginObject();
        while (reader.peek() == JsonToken.NAME) {
            String name = reader.nextName();
            if (LINKS.equals(name)) {
                reader.skipValue();
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

}
