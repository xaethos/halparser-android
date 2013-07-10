package net.xaethos.android.halparser.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.xaethos.android.halparser.HALLink;
import android.os.Parcel;
import android.os.Parcelable;

import com.scurrilous.uritemplate.URITemplate;

public class BaseHALLink implements HALLink
{
    private final URI mBaseURI;
    private final String mRel;
    private final String mHref;
    private final HashMap<String, Object> mAttributes;
    private final URITemplate mTemplate;

    private BaseHALLink(URI baseURI, HashMap<String, Object> attributes) {
        mBaseURI = baseURI;
        mRel = attributes.get(ATTR_REL).toString();
        mHref = attributes.get(ATTR_HREF).toString();
        mAttributes = attributes;
        mTemplate = new URITemplate(mHref);
    }

    @Override
    public URI getBaseURI() {
        return mBaseURI;
    }

    @Override
    public String getRel() {
        return mRel;
    }

    @Override
    public String getTitle() {
        Object value = mAttributes.get(ATTR_TITLE);
        return value == null ? null : value.toString();
    }

    @Override
    public String getHref() {
        return mHref;
    }

    @Override
    public URI getURI() {
        Map<String, Object> map = Collections.emptyMap();
        return getURI(map);
    }

    @Override
    public URI getURI(Map<String, Object> map) {
        try {
            return mTemplate.expand(map);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getAttribute(String name) {
        return mAttributes.get(name);
    }

    @Override
    public Set<String> getVariables() {
        return mTemplate.getVariableNames();
    }

    @Override
    public boolean isTemplated() {
        return !mTemplate.getVariableNames().isEmpty();
    }

    // *** Parcelable implementation

    public static final Parcelable.Creator<BaseHALLink> CREATOR = new Creator<BaseHALLink>() {
        @Override
        public BaseHALLink createFromParcel(Parcel source) {
            return new BaseHALLink(source);
        }

        @Override
        public BaseHALLink[] newArray(int size) {
            return new BaseHALLink[size];
        }

    };

    public BaseHALLink(Parcel in) {
        mBaseURI = URI.create(in.readString());

        HashMap<String, Object> attributes = new HashMap<String, Object>();
        in.readMap(attributes, null);
        mRel = attributes.get(ATTR_REL).toString();
        mHref = attributes.get(ATTR_HREF).toString();
        mAttributes = attributes;
        mTemplate = new URITemplate(mHref);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mBaseURI.toString());
        out.writeMap(mAttributes);
    }

    // ***** Inner classes

    public static class Builder
    {
        private final URI mBaseURI;
        private final HashMap<String, Object> mAttrs = new HashMap<String, Object>();

        public Builder(URI baseURI) {
            mBaseURI = baseURI;
        }

        public HALLink build() {
            return new BaseHALLink(mBaseURI, mAttrs);
        }

        public Builder putAttribute(String name, Object value) {
            mAttrs.put(name, value);
            return this;
        }

    }

}
