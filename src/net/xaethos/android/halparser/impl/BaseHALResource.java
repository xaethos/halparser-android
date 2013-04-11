package net.xaethos.android.halparser.impl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import net.xaethos.android.halparser.HALEnclosure;
import net.xaethos.android.halparser.HALResource;

public class BaseHALResource implements HALResource
{

    private final HALEnclosure mEnclosure;
    private final Map<String, Object> mProperties = new LinkedHashMap<String, Object>();

    private BaseHALResource(HALEnclosure enclosure) {
        mEnclosure = enclosure;
    }

    @Override
    public URI getBaseURI() {
        return mEnclosure.getBaseURI();
    }

    @Override
    public HALEnclosure getEnclosure() {
        return mEnclosure;
    }

    @Override
    public HALResource getParent() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return mProperties.get(name);
    }

    // ***** Inner classes

    public static class Builder
    {

        private BaseHALResource mResource;

        public Builder(HALEnclosure enclosure) {
            mResource = new BaseHALResource(enclosure);
        }

        public HALResource build() {
            HALResource resource = mResource;
            mResource = null;
            return resource;
        }

        public Builder putString(String name, String value) {
            mResource.mProperties.put(name, value);
            return this;
        }

    }

}
