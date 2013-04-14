package net.xaethos.android.halparser;

import java.io.StringReader;
import java.net.URI;

public class HALJsonParserTest extends HALParserTestCase
{

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

    public void testParseSmoke() throws Exception {
        assertNotNull(getParser().parse(new StringReader("{}")));
    }

    public void testResourceBaseURI() throws Exception {
        assertEquals(exampleURI, getResource().getBaseURI());
    }

    public void testResourceEnclosure() throws Exception {
        assertEquals(getParser(), getResource().getEnclosure());
    }

    public void testResourceParentIsNull() throws Exception {
        assertNull(getResource().getParent());
    }

    // *** Helpers

    private HALResource getResource() throws Exception {
        return resource != null ? resource : (resource = newResource("{}"));
    }
}
