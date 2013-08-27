package net.xaethos.android.halparser.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.scurrilous.uritemplate.URITemplate;

import net.xaethos.android.halparser.HALLink;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseHALLink implements HALLink
{
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TITLE = "title";

    private final String mRel;
    private final String mHref;
    private final HashMap<String, Object> mAttributes;
    private final URITemplate mTemplate;

    public BaseHALLink(String rel, String href) {
        this(rel, href, null);
    }

    public BaseHALLink(String rel, String href, Map<String, ?> attributes) {
        if (rel == null) throw new NullPointerException();

        mRel = rel;
        mHref = href;
        mTemplate = new URITemplate(mHref);

        if (attributes == null) {
            mAttributes = new HashMap<String, Object>();
        }
        else {
            mAttributes = new HashMap<String, Object>(attributes);
        }
    }

    @Override
    public String getHref() {
        return mHref;
    }

    @Override
    public String getRel() {
        return mRel;
    }

    @Override
    public Object getAttribute(String name) {
        return mAttributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        mAttributes.put(name, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(mAttributes);
    }

    @Override
    public void removeAttribute(String name) {
        mAttributes.remove(name);
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
    public Set<String> getVariables() {
        return mTemplate.getVariableNames();
    }

    @Override
    public String getName() {
        return getAttributeString(ATTR_NAME);
    }

    @Override
    public String getTitle() {
        return getAttributeString(ATTR_TITLE);
    }

    @Override
    public boolean isTemplated() {
        return !mTemplate.getVariableNames().isEmpty();
    }

    // *** Helpers

    private String getAttributeString(String name) {
        Object val = mAttributes.get(name);
        return val == null ? null : val.toString();
    }

    // *** Parcelable implementation

    @SuppressWarnings("UnusedDeclaration")
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
        mRel = in.readString();
        mHref = in.readString();
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        in.readMap(attributes, null);
        mAttributes = attributes;
        mTemplate = new URITemplate(mHref);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mRel);
        out.writeString(mHref);
        out.writeMap(mAttributes);
    }

}
