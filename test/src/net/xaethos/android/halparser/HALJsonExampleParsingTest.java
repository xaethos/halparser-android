package net.xaethos.android.halparser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

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

    public void testExampleWithoutHref() throws Exception {
        resource = newResource(R.raw.example_without_href);
        assertEquals("Example Resource", resource.getProperty("name"));
    }

    public void testExampleWithArray() throws Exception {
        resource = newResource(R.raw.example_with_array);
        assertEquals("Example Resource", resource.getProperty("name"));

        assertTrue(resource.getProperty("array") instanceof List);
        List<?> list = (List<?>) resource.getProperty("array");
        assertEquals(3, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals("three", list.get(2));
    }

    public void testExampleWithProperties() throws Exception {
        resource = newResource(R.raw.example);

        assertEquals(33, resource.getProperty("age"));
        assertEquals(false, resource.getProperty("expired"));
        assertEquals(123456, resource.getProperty("id"));
        assertEquals("Example Resource", resource.getProperty("name"));
        assertEquals(true, resource.getProperty("optional"));
    }

    public void testPropertiesMap() throws Exception {
        resource = newResource(R.raw.example);

        Map<String, Object> properties = resource.getProperties();
        String[] names = new String[5];
        names = properties.keySet().toArray(names);
        assertEquals(5, names.length);
        assertEquals("age", names[0]);
        assertEquals("expired", names[1]);
        assertEquals("id", names[2]);
        assertEquals("name", names[3]);
        assertEquals("optional", names[4]);

        try {
            properties.put("age", 42);
            fail("Properties should be immutable");
        }
        catch (UnsupportedOperationException e) {
            // All is good
        }
    }

    // *** Helpers

    HALJsonParser getParser() {
        return parser != null ? parser : (parser = new HALJsonParser("http://example.com/"));
    }

    Reader newReader(int resId) {
        return new InputStreamReader(getContext().getResources().openRawResource(resId));
    }

    HALResource newResource(int resId) throws Exception {
        return getParser().parse(newReader(resId));
    }

}
