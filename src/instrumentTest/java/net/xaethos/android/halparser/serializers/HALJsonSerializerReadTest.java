package net.xaethos.android.halparser.serializers;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.tests.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.xaethos.android.halparser.matchers.HALLinkMatcher.halLinkTo;
import static net.xaethos.android.halparser.matchers.HALPropertyMatcher.halProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class HALJsonSerializerReadTest extends HALParserTestCase
{
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parser = new HALJsonSerializer();
    }

    public void testExampleWithoutHref() throws Exception {
        resource = newResource(R.raw.example_without_href);
        assertEquals("Example Resource", resource.getValue("name"));
    }

    public void testExampleWithArray() throws Exception {
        resource = newResource(R.raw.example_with_array);
        assertEquals("Example Resource", resource.getValue("name"));

        assertTrue(resource.getValue("array") instanceof List);
        List<?> list = (List<?>) resource.getValue("array");
        assertEquals(3, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals("three", list.get(2));

        assertUnmodifiable(list);
    }

    public void testExampleWithProperties() throws Exception {
        resource = newResource(R.raw.example);

        assertEquals(33, resource.getValue("age"));
        assertEquals(false, resource.getValue("expired"));
        assertEquals(123456, resource.getValue("id"));
        assertEquals("Example Resource", resource.getValue("name"));
        assertEquals(true, resource.getValue("optional"));
    }

    public void testExampleWithNullProperty() throws Exception {
        resource = newResource(R.raw.example_with_null_property);
        assertEquals(null, resource.getValue("nullprop"));
    }

    public void testExampleWithNumbers() throws Exception {
        resource = newResource(R.raw.example_with_numbers);
        assertEquals(42, resource.getValue("int"));
        assertEquals(9223372036854775807L, resource.getValue("long"));
        assertEquals(1.3, resource.getValue("double"));
    }

    public void testPropertiesCollections() throws Exception {
        resource = newResource(R.raw.example);

        assertThat(resource.getProperties(), contains(
                halProperty("age", 33),
                halProperty("expired", false),
                halProperty("id", 123456),
                halProperty("name", "Example Resource"),
                halProperty("optional", true)
        ));
    }

    public void testExampleWithLink() throws Exception {
        resource = newResource(R.raw.example);
        HALLink link;
        link = resource.getLink("self");
        assertNotNull(link);
        assertEquals("self", link.getRel());
        assertEquals("https://example.com/api/customer/123456", link.getHref());
    }

    public void testExampleWithLinkArrays() throws Exception {
        resource = newResource(R.raw.example);

        assertThat(resource.getLinks("curie"), contains(
                halLinkTo("https://example.com/apidocs/accounts"),
                halLinkTo("https://example.com/apidocs/roles")
        ));
    }

    public void testLinkRels() throws Exception {
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

        assertEquals(null, link.getAttribute("href"));
        assertEquals("bob", link.getAttribute("name"));
        assertEquals("The Parent", link.getAttribute("title"));
        assertEquals("en", link.getAttribute("hreflang"));
    }

    public void testExampleWithSubresource() throws Exception {
        resource = newResource(R.raw.example_with_subresource);
        HALResource subresource = resource.getResource("ns:user");

        assertNotNull(subresource);
        assertTrue(resource.getProperties().isEmpty());

        assertEquals(32, subresource.getValue("age"));
        assertEquals(false, subresource.getValue("expired"));
        assertEquals(11, subresource.getValue("id"));
        assertEquals("Example User", subresource.getValue("name"));
        assertEquals(true, subresource.getValue("optional"));
    }

    public void testExampleWithSubresourceArrays() throws Exception {
        resource = newResource(R.raw.example_with_multiple_subresources);
        List<HALResource> embedded = new ArrayList<HALResource>(resource.getResources("ns:user"));
        assertNotNull(embedded);
        assertEquals(2, embedded.size());
        assertEquals("https://example.com/user/11", embedded.get(0).getLink("self").getHref());
        assertEquals("https://example.com/user/12", embedded.get(1).getLink("self").getHref());

        assertUnmodifiable(resource.getResources("ns:user"));

        assertEquals("https://example.com/user/11", resource.getResource("ns:user").getLink("self").getHref());
    }

    public void testSubresourceRels() throws Exception {
        resource = newResource(R.raw.example_with_multiple_subresources);

        Set<String> relSet = resource.getResourceRels();
        assertUnmodifiable(relSet);

        String[] rels = new String[1];
        rels = relSet.toArray(rels);

        assertEquals(1, rels.length);
        assertEquals("ns:user", rels[0]);
    }

    public void testNestedSubresources() throws Exception {
        resource = newResource(R.raw.example_with_multiple_nested_subresources);
        assertEquals("555-666-7890", resource.getResource("ns:user").getResource("phone:cell").getValue("number"));
    }

}
