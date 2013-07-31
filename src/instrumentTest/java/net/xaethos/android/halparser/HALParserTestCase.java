package net.xaethos.android.halparser;

import android.test.AndroidTestCase;

import net.xaethos.android.halparser.serializers.HALJsonSerializer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class HALParserTestCase extends AndroidTestCase
{

    protected static final URI exampleURI = URI.create("http://example.com/");

    protected HALJsonSerializer parser;
    protected HALResource resource;

    // *** Helpers

    protected HALJsonSerializer getParser() {
        return parser != null ? parser : (parser = new HALJsonSerializer(exampleURI));
    }

    protected Reader newReader(int resId) {
        return new InputStreamReader(getContext().getResources().openRawResource(resId));
    }

    protected HALResource newResource(String jsonString) throws Exception {
        return getParser().parse(new StringReader(jsonString));
    }

    protected HALResource newResource(int resId) throws Exception {
        return getParser().parse(newReader(resId));
    }

    protected void assertUnmodifiable(Collection<?> collection) {
        try {
            collection.clear();
            fail("Collection should be unmodifiable");
        }
        catch (UnsupportedOperationException e) {
            // All is good
        }
    }

    protected void assertUnmodifiable(Map<?, ?> map) {
        try {
            map.clear();
            fail("Map should be unmodifiable");
        }
        catch (UnsupportedOperationException e) {
            // All is good
        }
    }

}
