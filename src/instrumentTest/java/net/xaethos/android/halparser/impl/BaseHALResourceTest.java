package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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
}
