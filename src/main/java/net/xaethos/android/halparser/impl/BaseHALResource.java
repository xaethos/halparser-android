package net.xaethos.android.halparser.impl;

import android.os.Parcel;
import android.os.Parcelable;

import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;
import net.xaethos.android.halparser.serializers.HALJsonSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseHALResource implements HALResource
{

    private final URI mBaseURI;
    private final LinkedHashMap<String, HALProperty> mProperties = new LinkedHashMap<String, HALProperty>();
    private final LinkedHashMap<String, ArrayList<HALLink>> mLinks = new LinkedHashMap<String, ArrayList<HALLink>>();
    private final LinkedHashMap<String, ArrayList<HALResource>> mResources = new LinkedHashMap<String, ArrayList<HALResource>>();

    public BaseHALResource(URI baseURI) {
        mBaseURI = baseURI;
    }

    @Override
    public URI getBaseURI() {
        return mBaseURI;
    }

    @Override
    public HALProperty getProperty(String name) {
        return mProperties.get(name);
    }

    @Override
    public void setProperty(HALProperty property) {
        mProperties.put(property.getName(), property);
    }

    @Override
    public void removeProperty(String name) {
        mProperties.remove(name);
    }

    @Override
    public Map<String, ? extends HALProperty> getProperties() {
        return Collections.unmodifiableMap(mProperties);
    }

    @Override
    public Object getValue(String propertyName) {
        return mProperties.get(propertyName).getValue();
    }

    @Override
    public void setValue(String propertyName, Object value) {
        setProperty(new Property(propertyName, value));
    }

    @Override
    public HALLink getLink(String rel) {
        return getFirst(mLinks, rel);
    }

    @Override
    public List<HALLink> getLinks(String rel) {
        return getAll(mLinks, rel);
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
        return getAll(mResources, rel);
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

    private <T> List<T> getAll(Map<String, ArrayList<T>> map, String key) {
        if (map.containsKey(key)) {
            return Collections.unmodifiableList(map.get(key));
        }
        else {
            return Collections.emptyList();
        }
    }

    // *** Parcelable implementation

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<BaseHALResource> CREATOR = new Creator<BaseHALResource>() {

        @Override
        public BaseHALResource createFromParcel(Parcel source) {
            HALJsonSerializer serializer = new HALJsonSerializer(source.readString());
            StringReader reader = new StringReader(source.readString());
            try {
                return (BaseHALResource) serializer.parse(reader);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public BaseHALResource[] newArray(int size) {
            return new BaseHALResource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        HALJsonSerializer serializer = new HALJsonSerializer(mBaseURI);
        out.writeString(mBaseURI.toString());
        StringWriter writer = new StringWriter();
        try {
            serializer.write(this, writer);
        } catch (IOException e) {
        }
        out.writeString(writer.toString());
    }

    // ***** Inner classes

    public static class Property implements HALProperty
    {
        public final String name;
        public final Object value;

        public Property(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String getType() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }
    }

    public static class Builder
    {

        private URI mBaseURI;
        private BaseHALResource mResource;

        public Builder(URI baseURI) {
            mBaseURI = baseURI;
            mResource = new BaseHALResource(baseURI);
        }

        public HALResource build() {
            HALResource result = mResource;
            mResource = null;
            return result;
        }

        public Builder putProperty(String name, Object value) {
            mResource.mProperties.put(name, new Property(name, value));
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
            return new BaseHALLink.Builder(mBaseURI);
        }

        public BaseHALLink.Builder buildLink(String rel) {
            return buildLink().putAttribute(BaseHALLink.ATTR_REL, rel);
        }

        public Builder putResource(HALResource resource, String rel) {
            addContent(mResource.mResources, rel, resource);
            return this;
        }

        public BaseHALResource.Builder buildResource() {
            return new BaseHALResource.Builder(mBaseURI);
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
