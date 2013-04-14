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
import android.os.Parcel;
import android.os.Parcelable;

public class BaseHALResource implements HALResource
{

    private final HALEnclosure mEnclosure;
    private final LinkedHashMap<String, Object> mProperties = new LinkedHashMap<String, Object>();
    private final LinkedHashMap<String, ArrayList<HALLink>> mLinks = new LinkedHashMap<String, ArrayList<HALLink>>();
    private final LinkedHashMap<String, ArrayList<HALResource>> mResources = new LinkedHashMap<String, ArrayList<HALResource>>();

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
        return (HALResource) (mEnclosure instanceof HALResource ? mEnclosure : null);
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
        return getFirst(mLinks, rel);
    }

    @Override
    public List<HALLink> getLinks(String rel) {
        return Collections.unmodifiableList(mLinks.get(rel));
    }

    @Override
    public Set<String> getLinkRels() {
        return Collections.unmodifiableSet(mLinks.keySet());
    }

    @Override
    public HALResource getResource(String rel) {
        return getFirst(mResources, rel);
    }

    @Override
    public List<HALResource> getResources(String rel) {
        return Collections.unmodifiableList(mResources.get(rel));
    }

    @Override
    public Set<String> getResourceRels() {
        return Collections.unmodifiableSet(mResources.keySet());
    }

    // *** Helper methods

    private <T> T getFirst(Map<String, ArrayList<T>> map, String key) {
        ArrayList<T> list = map.get(key);
        if (list != null && !list.isEmpty()) return list.get(0);
        return null;
    }

    // *** Parcelable implementation

    public static final Parcelable.Creator<BaseHALResource> CREATOR = new Creator<BaseHALResource>() {
        @Override
        public BaseHALResource createFromParcel(Parcel source) {
            return new BaseHALResource(source);
        }

        @Override
        public BaseHALResource[] newArray(int size) {
            return new BaseHALResource[size];
        }

    };

    public BaseHALResource(Parcel in) {
        this((HALEnclosure) in.readParcelable(HALEnclosure.class.getClassLoader()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mEnclosure, flags);
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
            return putLink(link, link.getRel());
        }

        public Builder putLink(HALLink link, String rel) {
            addContent(mResource.mLinks, link.getRel(), link);
            return this;
        }

        public BaseHALLink.Builder buildLink() {
            return new BaseHALLink.Builder(mResource);
        }

        public BaseHALLink.Builder buildLink(String rel) {
            return buildLink().putAttribute(BaseHALLink.ATTR_REL, rel);
        }

        public Builder putResource(HALResource resource, String rel) {
            addContent(mResource.mResources, rel, resource);
            return this;
        }

        public BaseHALResource.Builder buildResource() {
            return new BaseHALResource.Builder(mResource);
        }

        // *** Helpers

        private <T> void addContent(LinkedHashMap<String, ArrayList<T>> map, String rel, T content) {
            ArrayList<T> list = map.get(rel);
            if (list == null) {
                list = new ArrayList<T>();
                map.put(rel, list);
            }

            list.add(content);
        }

    }

}
