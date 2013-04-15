package net.xaethos.android.halparser.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.URI;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALParserTestCase;

public class BaseHALLinkTest extends HALParserTestCase
{

    public void testGetURI() {
        HALLink link = new BaseHALLink.Builder(exampleURI).putAttribute(BaseHALLink.ATTR_REL, "self")
                                                          .putAttribute(BaseHALLink.ATTR_HREF, "/index.json")
                                                          .build();

        assertThat(link.getURI(), is(equalTo(URI.create("/index.json"))));
    }

}
