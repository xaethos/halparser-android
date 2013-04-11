package net.xaethos.android.halparser;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import android.test.AndroidTestCase;

public class HALJsonParserTest extends AndroidTestCase
{
    static final URI exampleURI = URI.create("http://example.com/");

    HALJsonParser parser;
    HALResource resource;

    public void testBaseURI() {
        assertEquals(exampleURI, getParser().getBaseURI());
    }

    public void testEnclosureIsNull() {
        assertNull(getParser().getEnclosure());
    }

    public void testBaseURIMustBeAbsolute() {
        URI relativeURI = URI.create("/index.html");
        try {
            new HALJsonParser(relativeURI);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // All is good
        }
    }

    public void testStringConstructor() {
        parser = new HALJsonParser("http://example.com/");
        assertEquals(exampleURI, parser.getBaseURI());
    }

    public void testParseSmoke() throws IOException {
        assertNotNull(getParser().parse(new StringReader("{}")));
    }

    public void testResourceBaseURI() {
        assertEquals(exampleURI, getResource().getBaseURI());
    }

    public void testResourceEnclosure() {
        assertEquals(getParser(), getResource().getEnclosure());
    }

    public void testResourceParentIsNull() {
        assertNull(getResource().getParent());
    }

    // *** Helpers

    private HALJsonParser getParser() {
        return parser != null ? parser : (parser = new HALJsonParser(exampleURI));
    }

    private HALResource getResource() {
        try {
            return resource != null ? resource : (resource = getParser().parse(new StringReader("{}")));
        }
        catch (Exception e) {
            fail("Error parsing resource");
            return null;
        }
    }

}
