package net.xaethos.android.halparser.serializers;

import junit.framework.Assert;

import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALResource;

import java.io.StringReader;
import java.net.URI;

public class HALJsonSerializerReadTest extends HALParserTestCase
{

    public void testBaseURI() {
        assertEquals(exampleURI, getParser().getBaseURI());
    }

    public void testBaseURIMustBeAbsolute() {
        URI relativeURI = URI.create("/index.html");
        try {
            new HALJsonSerializer(relativeURI);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // All is good
        }
    }

    public void testStringConstructor() {
        parser = new HALJsonSerializer("http://example.com/");
        assertEquals(exampleURI, parser.getBaseURI());
    }

    public void testParseSmoke() throws Exception {
        assertNotNull(getParser().parse(new StringReader("{}")));
    }

    public void testResourceBaseURI() throws Exception {
        Assert.assertEquals(exampleURI, getResource().getBaseURI());
    }

    // *** Helpers

    private HALResource getResource() throws Exception {
        return resource != null ? resource : (resource = newResource("{}"));
    }
}
