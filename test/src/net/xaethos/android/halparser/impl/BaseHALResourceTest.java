package net.xaethos.android.halparser.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import net.xaethos.android.halparser.HALEnclosure;
import net.xaethos.android.halparser.HALJsonParser;
import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.HALResource;

public class BaseHALResourceTest extends HALParserTestCase
{

    HALEnclosure root;
    BaseHALResource.Builder builder;
    HALResource resource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        root = new HALJsonParser("http://example.com/");
        builder = new BaseHALResource.Builder(root);
    }

    public void testProperty() {
        resource = builder.putProperty("foo", "bar").build();
        assertThat((String) resource.getProperty("foo"), is("bar"));
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
