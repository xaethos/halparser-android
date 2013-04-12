package net.xaethos.android.halparser.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.xaethos.android.halparser.HALEnclosure;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALResource;

public class BaseHALResource implements HALResource
{

    private final HALEnclosure mEnclosure;
    private final LinkedHashMap<String, Object> mProperties = new LinkedHashMap<String, Object>();
    private final LinkedHashMap<String, ArrayList<HALLink>> mLinks = new LinkedHashMap<String, ArrayList<HALLink>>();

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

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(mProperties);
    }

    @Override
    public HALLink getLink(String rel) {
        ArrayList<HALLink> links = mLinks.get(rel);
        if (links != null && !links.isEmpty()) return links.get(0);
        return null;
    }

    @Override
    public List<HALLink> getLinks(String rel) {
        return Collections.unmodifiableList(mLinks.get(rel));
    }

    @Override
    public Set<String> getLinkRels() {
        return Collections.unmodifiableSet(mLinks.keySet());
    }

    // ***** Inner classes

    public static class Builder
    {

        private BaseHALResource mResource;

        public Builder(HALEnclosure enclosure) {
            mResource = new BaseHALResource(enclosure);
        }

        public HALResource build() {
            HALResource result = mResource;
            mResource = null;
            return result;
        }

        public Builder putProperty(String name, Object value) {
            mResource.mProperties.put(name, value);
            return this;
        }

        public Builder putLink(HALLink link) {
            String rel = link.getRel();

            ArrayList<HALLink> linkList = mResource.mLinks.get(rel);
            if (linkList == null) {
                linkList = new ArrayList<HALLink>();
                mResource.mLinks.put(rel, linkList);
            }

            linkList.add(link);
            return this;
        }

        public BaseHALLink.Builder buildLink() {
            return new BaseHALLink.Builder(mResource);
        }

        public BaseHALLink.Builder buildLink(String rel) {
            return buildLink().putAttribute(BaseHALLink.ATTR_REL, rel);
        }

    }

}
