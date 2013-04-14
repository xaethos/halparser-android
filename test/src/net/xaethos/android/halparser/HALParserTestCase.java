package net.xaethos.android.halparser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import android.test.AndroidTestCase;

public class HALParserTestCase extends AndroidTestCase {

    static final URI exampleURI = URI.create("http://example.com/");

    HALJsonParser parser;
    HALResource resource;

    // *** Helpers

    HALJsonParser getParser() {
        return parser != null ? parser : (parser = new HALJsonParser(exampleURI));
    }

    Reader newReader(int resId) {
        return new InputStreamReader(getContext().getResources().openRawResource(resId));
    }

    HALResource newResource(String jsonString) throws Exception {
        return getParser().parse(new StringReader(jsonString));
    }

    HALResource newResource(int resId) throws Exception {
        return getParser().parse(newReader(resId));
    }

    void assertUnmodifiable(Collection<?> collection) {
        try {
            collection.clear();
            fail("Collection should be unmodifiable");
        }
        catch (UnsupportedOperationException e) {
            // All is good
        }
    }

    void assertUnmodifiable(Map<?, ?> map) {
        try {
            map.clear();
            fail("Map should be unmodifiable");
        }
        catch (UnsupportedOperationException e) {
            // All is good
        }
    }

}
