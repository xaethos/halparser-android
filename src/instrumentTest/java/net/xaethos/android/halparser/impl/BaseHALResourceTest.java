package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
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
}
