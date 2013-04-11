package net.xaethos.android.halparser;

import java.io.InputStreamReader;
import java.io.Reader;

import net.xaethos.android.halparser.tests.R;
import android.test.AndroidTestCase;

public class HALJsonExampleParsingTest extends AndroidTestCase
{
    HALJsonParser parser;
    HALResource resource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parser = new HALJsonParser("http://example.com/");
    }

    public void testExampleWithoutHref() {
        resource = newResource(R.raw.example_without_href);
        assertEquals("Example Resource", resource.getProperty("name"));
    }

    // *** Helpers

    HALJsonParser getParser() {
        return parser != null ? parser : (parser = new HALJsonParser("http://example.com/"));
    }

    Reader newReader(int resId) {
        return new InputStreamReader(getContext().getResources().openRawResource(resId));
    }

    HALResource newResource(int resId) {
        try {
            return getParser().parse(newReader(resId));
        }
        catch (Exception e) {
            fail("Error parsing resource");
            return null;
        }
    }

}
