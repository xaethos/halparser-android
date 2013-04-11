package net.xaethos.android.halparser;

import java.net.URI;

import android.test.AndroidTestCase;

public class HALParserTest extends AndroidTestCase
{
    HALParser parser;
    URI exampleUri = URI.create("http://example.com/");

    public void testBaseURI() {
        parser = new HALParser(exampleUri);
        assertEquals(exampleUri, parser.getBaseURI());
    }

    public void testEnclosureIsNull() {
        parser = new HALParser(exampleUri);
        assertNull(parser.getEnclosure());
    }

    public void testBaseURIMustBeAbsolute() {
        URI relativeURI = URI.create("/index.html");
        try {
            new HALParser(relativeURI);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // All is good
        }
    }

    public void testStringConstructor() {
        parser = new HALParser("http://example.com/");
        assertEquals(exampleUri, parser.getBaseURI());
    }

}
