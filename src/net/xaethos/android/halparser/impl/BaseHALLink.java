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
    private final HashMap<String, Object> mAttributes;

    private BaseHALLink(HALResource resource, HashMap<String, Object> attributes) {
        mResource = resource;
        mRel = attributes.get(ATTR_REL).toString();
        mHref = attributes.get(ATTR_HREF).toString();
        mAttributes = attributes;
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
    public Object getAttribute(String name) {
        return mAttributes.get(name);
    }

    public static class Builder
    {
        private final HALResource mResource;
        private final HashMap<String, Object> mAttrs = new HashMap<String, Object>();

        public Builder(HALResource resource) {
            mResource = resource;
        }

        public HALLink build() {
            return new BaseHALLink(mResource, mAttrs);
        }

        public Builder putAttribute(String name, Object value) {
            mAttrs.put(name, value);
            return this;
        }

    }

}
