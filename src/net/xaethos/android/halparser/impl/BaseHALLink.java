package net.xaethos.android.halparser.impl;

import java.util.HashMap;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALResource;

public class BaseHALLink implements HALLink
{
    public static final String ATTR_REL = "rel";
    public static final String ATTR_HREF = "href";

    private final HALResource mResource;
    private final String mRel;
    private final String mHref;

    // private final Map<String, Object> mProperties;

    private BaseHALLink(HALResource resource, String rel, String href) {
        mResource = resource;
        mRel = rel;
        mHref = href;
    }

    @Override
    public HALResource getResource() {
        return mResource;
    }

    @Override
    public String getRel() {
        return mRel;
    }

    @Override
    public String getHref() {
        return mHref;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    public static class Builder
    {
        private final HALResource mResource;
        private final HashMap<String, Object> mProperties = new HashMap<String, Object>();

        public Builder(HALResource resource) {
            mResource = resource;
        }

        public HALLink build() {
            return new BaseHALLink(mResource, (String) mProperties.get(ATTR_REL), (String) mProperties.get(ATTR_HREF));
        }

        public Builder putAttribute(String name, Object value) {
            mProperties.put(name, value);
            return this;
        }

    }

}
