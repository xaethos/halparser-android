package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

public class BaseHALResourceTest extends HALParserTestCase
{

    BaseHALResource.Builder builder;
    HALResource resource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        builder = new BaseHALResource.Builder(exampleURI);
    }

    public void testProperty() {
        resource = builder.putProperty("foo", "bar").build();
        assertThat((String) resource.getValue("foo"), is("bar"));
    }

    public void testEmptyRels() throws Exception {
        resource = newResource("{}");

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
            @Override public String getType() { return null; }
            @Override public String getTitle() { return null; }
        };

        resource = new BaseHALResource(exampleURI);
        resource.setProperty(property);

        assertThat(resource.getProperty("foo"), is(sameInstance(property)));
    }

    public void testRemoveProperty() {
        resource = new BaseHALResource(exampleURI);
        resource.setValue("foo", 13);
        resource.removeProperty("foo");

        assertThat(resource.getProperty("foo"), is(nullValue()));
    }

    public void testSetValue() {
        Object value = new Object();

        resource = new BaseHALResource(exampleURI);
        resource.setValue("foo", value);

        assertThat(resource.getProperty("foo").getValue(), is(sameInstance(value)));
    }

    public void testAddLink() {
        resource = new BaseHALResource(exampleURI);

        resource.addLink(new BaseHALLink("sibling", "/bundle/4"));
        resource.addLink(new BaseHALLink("item", "/item/2"));
        resource.addLink(new BaseHALLink("item", "/item/13"));

        assertThat(resource.getLinkRels(), contains("sibling", "item"));

        assertThat(resource.getLinks("sibling"), hasSize(1));
        assertThat(resource.getLinks("sibling").get(0).getHref(), is("/bundle/4"));

        assertThat(resource.getLinks("item"), hasSize(2));
        assertThat(resource.getLinks("item").get(0).getHref(), is("/item/2"));
        assertThat(resource.getLinks("item").get(1).getHref(), is("/item/13"));
    }

    public void testRemoveLink() {
        resource = new BaseHALResource(exampleURI);
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

        assertThat(resource.getLinks("item"), hasSize(1));
        assertThat(resource.getLinks("item").get(0).getHref(), is("/item/13"));
    }

    public void testAddResource() {
        resource = new BaseHALResource(exampleURI);

        resource.addResource(new BaseHALResource(exampleURI), "sibling");
        resource.addResource(new BaseHALResource(exampleURI), "item");
        resource.addResource(new BaseHALResource(exampleURI), "item");

        assertThat(resource.getResourceRels(), contains("sibling", "item"));
        assertThat(resource.getResources("sibling"), hasSize(1));
        assertThat(resource.getResources("item"), hasSize(2));
    }

    public void testRemoveResource() {
        resource = new BaseHALResource(exampleURI);
        HALResource embedded;

        embedded = new BaseHALResource(exampleURI);
        resource.addResource(embedded, "sibling");
        resource.removeResource(embedded, "sibling");

        embedded = new BaseHALResource(exampleURI);
        resource.addResource(embedded, "item");
        resource.addResource(new BaseHALResource(exampleURI), "item");
        resource.removeResource(embedded, "item");

        assertThat(resource.getResourceRels(), contains("item"));
        assertThat(resource.getResources("item"), hasSize(1));
    }

}
