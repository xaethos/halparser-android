package net.xaethos.android.halparser;

import java.net.URI;

import android.os.Parcelable;

public interface HALLink extends Parcelable
{

    public URI getBaseURI();

    public String getRel();

    public String getHref();

    public Object getAttribute(String name);

}
