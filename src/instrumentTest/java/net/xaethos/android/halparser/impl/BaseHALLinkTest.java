package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.tests.R;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class BaseHALLinkTest extends HALParserTestCase
{

    public void testGetURI() {
        HALLink link = new BaseHALLink.Builder(exampleURI).putAttribute(BaseHALLink.ATTR_REL, "self")
                                                          .putAttribute(BaseHALLink.ATTR_HREF, "/index.json")
                                                          .build();

        assertThat(link.getURI(), is(equalTo(URI.create("/index.json"))));
    }

    public void testRecognizesURITemplates() throws Exception {
        resource = newResource(R.raw.example_with_template);

        assertThat(resource.getLink("self").isTemplated(), is(false));
        assertThat(resource.getLink("ns:customer").isTemplated(), is(true));
        assertThat(resource.getLink("ns:query").isTemplated(), is(true));
    }

    public void testPopulateURITemplates() throws Exception {
        HALLink link = getResourceLink(R.raw.example_with_template, "ns:query");
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("queryParam", "red&blue");

        assertThat(link.getURI(map),
                   is(equalTo(URI.create("https://example.com/api/customer/search?queryParam=red%26blue"))));

        map.clear();
        assertThat(link.getURI(map), is(equalTo(URI.create("https://example.com/api/customer/search"))));

        assertThat(link.getURI(), is(equalTo(URI.create("https://example.com/api/customer/search"))));
    }

    public void testGetVariables() {
        HALLink link = new BaseHALLink.Builder(exampleURI).putAttribute(BaseHALLink.ATTR_REL, "foo")
                                                          .putAttribute(BaseHALLink.ATTR_HREF, "/foo{;v,empty,who}{?q}")
                                                          .build();

        assertThat(link.getVariables(), contains("v", "empty", "who", "q"));
    }

    public void testGetAttributes() throws Exception {
        HALLink link = getResourceLink(R.raw.example_with_template, "ns:pet-search");

        Map<String, Object> attrs = link.getAttributes();
        assertThat(attrs.size(), is(4));
        assertThat(attrs.keySet(), hasItems("rel", "href", "title", "hreflang"));
        assertThat(attrs.get("rel").toString(), is("ns:pet-search"));
        assertThat(link.isTemplated(), is(true));
    }

    // *** Helper methods

    private HALLink getResourceLink(int resId, String rel) throws Exception {
        return newResource(resId).getLink(rel);
    }

}
