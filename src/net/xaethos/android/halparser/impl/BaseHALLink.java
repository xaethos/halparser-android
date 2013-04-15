package net.xaethos.android.halparser.impl;

import java.net.URI;
import java.util.HashMap;

import net.xaethos.android.halparser.HALLink;
import android.os.Parcel;
import android.os.Parcelable;

public class BaseHALLink implements HALLink
{
    public static final String ATTR_REL = "rel";
    public static final String ATTR_HREF = "href";

    private final URI mBaseURI;
    private final String mRel;
    private final String mHref;
    private final HashMap<String, Object> mAttributes;

    private BaseHALLink(URI baseURI, HashMap<String, Object> attributes) {
        mBaseURI = baseURI;
        mRel = attributes.get(ATTR_REL).toString();
        mHref = attributes.get(ATTR_HREF).toString();
        mAttributes = attributes;
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
    public String getHref() {
        return mHref;
    }

    @Override
    public Object getAttribute(String name) {
        return mAttributes.get(name);
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
