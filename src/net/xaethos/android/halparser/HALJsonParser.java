package net.xaethos.android.halparser;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import net.xaethos.android.halparser.impl.BaseHALResource;
import android.util.JsonReader;
import android.util.JsonToken;

public class HALJsonParser implements HALEnclosure
{
    private final URI mURI;

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
            builder.putString(reader.nextName(), reader.nextString());
        }
        reader.endObject();

        return builder.build();
    }

}
