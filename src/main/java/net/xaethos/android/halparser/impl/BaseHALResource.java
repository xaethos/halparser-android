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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseHALResource implements HALResource
{

    private final LinkedHashMap<String, HALProperty> mProperties = new LinkedHashMap<String, HALProperty>();
    private final LinkedHashMap<String, ArrayList<HALLink>> mLinks = new LinkedHashMap<String, ArrayList<HALLink>>();
    private final LinkedHashMap<String, ArrayList<HALResource>> mResources = new LinkedHashMap<String, ArrayList<HALResource>>();

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
    public Collection<HALProperty> getProperties() {
        return Collections.unmodifiableCollection(mProperties.values());
    }

    @Override
    public Collection<String> getPropertyNames() {
        return Collections.unmodifiableCollection(mProperties.keySet());
    }

    @Override
    public Object getValue(String propertyName) {
        HALProperty property = mProperties.get(propertyName);
        return property == null ? null : property.getValue();
    }

    @Override
    public String getValueString(String propertyName) {
        HALProperty property = mProperties.get(propertyName);
        return property == null ? null : property.getValueString();
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
    public void addLink(HALLink link) {
        getOrCreateList(mLinks, link.getRel()).add(link);
    }

    @Override
    public void removeLink(HALLink link) {
        removeItem(mLinks, link.getRel(), link);
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
    public void addResource(HALResource resource, String rel) {
        getOrCreateList(mResources, rel).add(resource);
    }

    @Override
    public void removeResource(HALResource resource, String rel) {
        removeItem(mResources, rel, resource);
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

    private <T> ArrayList<T> getOrCreateList(Map<String, ArrayList<T>> map, String key) {
        ArrayList<T> list = map.get(key);
        if (list == null) {
            list = new ArrayList<T>();
            map.put(key, list);
        }
        return list;
    }

    private <T> void removeItem(Map<String, ArrayList<T>> map, String key, T item) {
        ArrayList<T> list = map.get(key);
        if (list == null) return;
        if (list.remove(item)) {
            if (list.isEmpty()) map.remove(key);
        }
    }

    private <T> List<T> getAll(Map<String, ArrayList<T>> map, String key) {
        if (map.containsKey(key)) {
            return Collections.unmodifiableList(map.get(key));
        } else {
            return Collections.emptyList();
        }
    }

    // *** Parcelable implementation

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<BaseHALResource> CREATOR = new Creator<BaseHALResource>() {

        @Override
        public BaseHALResource createFromParcel(Parcel source) {
            HALJsonSerializer serializer = new HALJsonSerializer();
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
        HALJsonSerializer serializer = new HALJsonSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.write(this, writer);
        } catch (IOException e) {
        }
        out.writeString(writer.toString());
    }

    // ***** Inner classes

    public static class Property implements HALProperty {
        private final String mName;
        private final Object mValue;

        public Property(String name, Object value) {
            this.mName = name;
            this.mValue = value;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public Object getValue() {
            return mValue;
        }

        @Override
        public String getValueString() {
            return mValue == null ? null : mValue.toString();
        }
    }

}
