package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.tests.R;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static net.xaethos.android.halparser.matchers.ThrowsMatcher.throwsA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;


public class BaseHALLinkTest extends HALParserTestCase
{

    public void testConstructors() {
        HALLink link;
        final String rel = "foo";
        final String href = "http://example.com";
        Map<String, ?> attrs = singletonMap("title", "Link to Foo");

        link = new BaseHALLink(rel, href);
        assertThat(link.getRel(), is(rel));
        assertThat(link.getHref(), is(href));
        assertThat(link.getAttributes().size(), is(0));

        link = new BaseHALLink(rel, href, attrs);
        assertThat(link.getRel(), is(rel));
        assertThat(link.getHref(), is(href));
        assertThat(link.getAttributes().size(), is(1));
        assertThat(link.getAttributes().get("title").toString(), is("Link to Foo"));

        assertThat(new Runnable() {
            @Override
            public void run() {
                new BaseHALLink(null, href);
            }
        }, throwsA(NullPointerException.class));

        assertThat(new Runnable() {
            @Override
            public void run() {
                new BaseHALLink(rel, null);
            }
        }, throwsA(NullPointerException.class));

        assertThat(new Runnable() {
            @Override
            public void run() {
                new BaseHALLink(rel, href, null);
            }
        }, not(throwsA(Throwable.class)));
    }

    public void testGetURI() {
        HALLink link = new BaseHALLink("self", "/index.json");
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
        HALLink link = new BaseHALLink("foo", "/foo{;v,empty,who}{?q}");
        assertThat(link.getVariables(), contains("v", "empty", "who", "q"));
    }

    public void testGetAttributes() throws Exception {
        HALLink link = getResourceLink(R.raw.example_with_template, "ns:pet-search");

        Map<String, ?> attrs = link.getAttributes();
        assertThat(attrs.size(), is(2));
        assertThat(attrs.keySet(), hasItems("title", "hreflang"));
        assertThat(attrs.get("title").toString(), is("Search Pets by Name"));
        assertThat(link.isTemplated(), is(true));
    }

    public void testSetAttribute() throws Exception {
        HALLink link = new BaseHALLink("foo", "/foo");
        link.setAttribute("title", "Footurama");

        assertThat((String) link.getAttribute("title"), is("Footurama"));

        Map<String, ?> attrs = link.getAttributes();
        assertThat(attrs.size(), is(1));
        assertThat(attrs.keySet(), hasItems("title"));
    }

    public void testRemoveAttribute() throws Exception {
        HALLink link = new BaseHALLink("foo", "/foo");
        link.setAttribute("title", "Footurama");
        link.removeAttribute("title");

        assertThat(link.getAttribute("title"), is(nullValue()));
        assertThat(link.getAttributes().size(), is(0));
    }

    public void testGetTitle() throws Exception {
        HALLink link;

        link = new BaseHALLink("foo", "/foo");
        assertThat(link.getTitle(), is(nullValue()));

        link = new BaseHALLink("foo", "/foo", singletonMap("title", "Foontastic"));
        assertThat(link.getTitle(), is("Foontastic"));

        link = new BaseHALLink("foo", "/foo", singletonMap("title", 42));
        assertThat(link.getTitle(), is("42"));
    }

    // *** Helper methods

    private HALLink getResourceLink(int resId, String rel) throws Exception {
        return newResource(resId).getLink(rel);
    }

}
