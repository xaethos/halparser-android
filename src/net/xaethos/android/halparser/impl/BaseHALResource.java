package net.xaethos.android.halparser.impl;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.xaethos.android.halparser.HALEnclosure;
import net.xaethos.android.halparser.HALResource;

public class BaseHALResource implements HALResource
{

    private final HALEnclosure mEnclosure;
    private final Map<String, Object> mProperties;

    private BaseHALResource(HALEnclosure enclosure, Map<String, Object> properties) {
        mEnclosure = enclosure;
        mProperties = properties;
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

    @Override
    public Map<String, Object> getProperties() {
        return mProperties;
    }

    // ***** Inner classes

    public static class Builder
    {

        private final HALEnclosure mEnclosure;
        private final LinkedHashMap<String, Object> mProperties = new LinkedHashMap<String, Object>();

        public Builder(HALEnclosure enclosure) {
            mEnclosure = enclosure;
        }

        public HALResource build() {
            return new BaseHALResource(mEnclosure, Collections.unmodifiableMap(mProperties));
        }

        public Builder putProperty(String name, Object value) {
            mProperties.put(name, value);
            return this;
        }

    }

}
