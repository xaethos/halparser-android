package net.xaethos.android.halparser.impl;

import net.xaethos.android.halparser.HALEnclosure;
import net.xaethos.android.halparser.HALJsonParser;
import net.xaethos.android.halparser.HALResource;
import android.test.AndroidTestCase;

public class BaseHALResourceTest extends AndroidTestCase
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
        assertEquals("bar", resource.getProperty("foo"));
    }

}
