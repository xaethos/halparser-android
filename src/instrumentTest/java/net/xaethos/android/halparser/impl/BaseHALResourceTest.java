package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.tests.R;

import java.util.Collection;

import static net.xaethos.android.halparser.matchers.HALLinkMatcher.halLinkTo;
import static net.xaethos.android.halparser.matchers.HALPropertyMatcher.halProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

public class BaseHALResourceTest extends HALParserTestCase
{

    HALResource resource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        resource = new BaseHALResource();
    }

    public void testEmptyRels() throws Exception {
        assertThat(resource.getLink("foo"), is(nullValue()));
        assertThat(resource.getLinks("foo"), is(empty()));
        assertThat(resource.getLinkRels(), is(empty()));

        assertThat(resource.getResource("foo"), is(nullValue()));
        assertThat(resource.getResources("foo"), is(empty()));
        assertThat(resource.getResourceRels(), is(empty()));
    }

    public void testSetProperty() {
        HALProperty property = new HALProperty() {
            @Override public String getName() { return "foo"; }
            @Override public Object getValue() { return null; }
        };

        resource.setProperty(property);

        assertThat(resource.getProperty("foo"), is(sameInstance(property)));
    }

    public void testRemoveProperty() {
        resource.setValue("foo", 13);
        resource.removeProperty("foo");

        assertThat(resource.getProperty("foo"), is(nullValue()));
    }

    public void testGetProperties() throws Exception {
        resource = newResource(R.raw.example);
        Collection<HALProperty> properties = resource.getProperties();

        assertThat(properties, contains(
                halProperty("age", 33),
                halProperty("expired", false),
                halProperty("id", 123456),
                halProperty("name", "Example Resource"),
                halProperty("optional", true)
        ));
        assertUnmodifiable(properties);
    }

    public void testGetPropertyNames() throws Exception {
        resource = newResource(R.raw.example);
        Collection<String> names = resource.getPropertyNames();

        assertThat(names, contains("age", "expired", "id", "name", "optional"));
        assertUnmodifiable(names);
    }

    public void testSetValue() {
        Object value = new Object();

        resource.setValue("foo", value);

        assertThat(resource.getProperty("foo"), is(not(nullValue())));
        assertThat(resource.getProperty("foo").getValue(), is(sameInstance(value)));
    }

    public void testGetValue() {
        Object value = new Object();

        resource.setValue("foo", value);

        assertThat(resource.getValue("foo"), is(sameInstance(value)));
        assertThat(resource.getValue("bar"), is(nullValue()));
    }

    public void testGetValueString() {
        resource.setValue("string", "foo");
        resource.setValue("int", 13);
        resource.setValue("null", null);

        assertThat(resource.getValueString("string"), is("foo"));
        assertThat(resource.getValueString("int"), is("13"));
        assertThat(resource.getValueString("null"), is(nullValue()));
        assertThat(resource.getValueString("missing_value"), is(nullValue()));
    }

    public void testGetLinkRels() throws Exception {
        resource = newResource(R.raw.example);
        assertThat(resource.getLinkRels(), contains("curie", "self", "ns:parent", "ns:users"));
        assertUnmodifiable(resource.getLinkRels());
    }

    public void testGetLinks() throws Exception {
        resource = newResource(R.raw.example);

        assertThat(resource.getLinks("self"), contains(
                halLinkTo("https://example.com/api/customer/123456")
        ));
        assertThat(resource.getLinks("curie"), contains(
                halLinkTo("https://example.com/apidocs/accounts"),
                halLinkTo("https://example.com/apidocs/roles")
        ));
        assertThat(resource.getLinks("bad rel"), is(empty()));

        assertUnmodifiable(resource.getLinks("self"));
    }

    public void testAddLink() {
        HALLink sibling = new BaseHALLink("sibling", "/bundle/4");
        HALLink items[] = {
                new BaseHALLink("item", "/item/2"),
                new BaseHALLink("item", "/item/13")
        };

        resource.addLink(sibling);
        resource.addLink(items[0]);
        resource.addLink(items[1]);

        assertThat(resource.getLinkRels(), contains("sibling", "item"));
        assertThat(resource.getLinks("sibling"), contains(sibling));
        assertThat(resource.getLinks("item"), contains(items));
    }

    public void testRemoveLink() {
        HALLink link;

        link = new BaseHALLink("sibling", "/bundle/4");
        resource.addLink(link);
        resource.removeLink(link);

        link = new BaseHALLink("item", "/item/2");
        resource.addLink(link);
        resource.removeLink(link);

        link = new BaseHALLink("item", "/item/13");
        resource.addLink(new BaseHALLink("item", "/item/13"));
        resource.removeLink(link);

        assertThat(resource.getLinkRels(), contains("item"));
        assertThat(resource.getLinks("item"), contains(halLinkTo("/item/13")));
    }

    public void testAddResource() {
        resource.addResource(new BaseHALResource(), "sibling");
        resource.addResource(new BaseHALResource(), "item");
        resource.addResource(new BaseHALResource(), "item");

        assertThat(resource.getResourceRels(), contains("sibling", "item"));
        assertThat(resource.getResources("sibling"), hasSize(1));
        assertThat(resource.getResources("item"), hasSize(2));
    }

    public void testRemoveResource() {
        HALResource embedded;

        embedded = new BaseHALResource();
        resource.addResource(embedded, "sibling");
        resource.removeResource(embedded, "sibling");

        embedded = new BaseHALResource();
        resource.addResource(embedded, "item");
        resource.addResource(new BaseHALResource(), "item");
        resource.removeResource(embedded, "item");

        assertThat(resource.getResourceRels(), contains("item"));
        assertThat(resource.getResources("item"), hasSize(1));
    }

}
