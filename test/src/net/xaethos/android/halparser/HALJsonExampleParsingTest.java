package net.xaethos.android.halparser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        assertUnmodifiable(list);
    }

    public void testExampleWithProperties() throws Exception {
        resource = newResource(R.raw.example);

        assertEquals(33, resource.getProperty("age"));
        assertEquals(false, resource.getProperty("expired"));
        assertEquals(123456, resource.getProperty("id"));
        assertEquals("Example Resource", resource.getProperty("name"));
        assertEquals(true, resource.getProperty("optional"));
    }

    public void testExampleWithNullProperty() throws Exception {
        resource = newResource(R.raw.example_with_null_property);
        assertEquals(null, resource.getProperty("nullprop"));
    }

    public void testExampleWithNumbers() throws Exception {
        resource = newResource(R.raw.example_with_numbers);
        assertEquals(42, resource.getProperty("int"));
        assertEquals(9223372036854775807L, resource.getProperty("long"));
        assertEquals(1.3, resource.getProperty("double"));
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

        assertUnmodifiable(properties);
    }

    public void testExampleWithLink() throws Exception {
        resource = newResource(R.raw.example);
        HALLink link;
        link = resource.getLink("self");
        assertNotNull(link);
        assertEquals("self", link.getRel());
        assertEquals("https://example.com/api/customer/123456", link.getHref());
        assertSame(resource, link.getResource());
    }

    public void testExampleWithLinkArrays() throws Exception {
        resource = newResource(R.raw.example);
        List<HALLink> links;
        links = resource.getLinks("curie");
        assertNotNull(links);
        assertEquals(2, links.size());
        assertEquals("https://example.com/apidocs/accounts", links.get(0).getHref());
        assertEquals("https://example.com/apidocs/roles", links.get(1).getHref());

        assertUnmodifiable(links);

        assertEquals("https://example.com/apidocs/accounts", resource.getLink("curie").getHref());
    }

    public void testResourceRels() throws Exception {
        resource = newResource(R.raw.example);

        Set<String> relSet = resource.getLinkRels();
        assertUnmodifiable(relSet);

        String[] rels = new String[4];
        rels = relSet.toArray(rels);

        assertEquals(4, rels.length);
        assertEquals("curie", rels[0]);
        assertEquals("self", rels[1]);
        assertEquals("ns:parent", rels[2]);
        assertEquals("ns:users", rels[3]);
    }

    public void testLinkAttributes() throws Exception {
        resource = newResource(R.raw.example);
        HALLink link = resource.getLink("ns:parent");

        assertEquals("https://example.com/api/customer/1234", link.getAttribute("href"));
        assertEquals("bob", link.getAttribute("name"));
        assertEquals("The Parent", link.getAttribute("title"));
        assertEquals("en", link.getAttribute("hreflang"));
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
