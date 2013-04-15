package net.xaethos.android.halparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.URI;
import java.util.HashMap;

import net.xaethos.android.halparser.tests.R;

public class HALLinkTemplatesTest extends HALParserTestCase
{
    public void testRecognizesURITemplates() throws Exception {
        resource = newResource(R.raw.example_with_template);

        assertThat(resource.getLink("self").isTemplated(), is(false));
        assertThat(resource.getLink("ns:customer").isTemplated(), is(true));
        assertThat(resource.getLink("ns:query").isTemplated(), is(true));
    }

    public void testPopulateURITemplates() throws Exception {
        resource = newResource(R.raw.example_with_template);
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("queryParam", "red&blue");

        assertThat(resource.getLink("ns:query").getURI(map),
                   is(equalTo(URI.create("https://example.com/api/customer/search?queryParam=red%26blue"))));
    }
}
