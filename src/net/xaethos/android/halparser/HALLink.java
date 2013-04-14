package net.xaethos.android.halparser;

import android.os.Parcelable;

public interface HALLink extends Parcelable
{

    public HALResource getResource();

    public String getRel();

    public String getHref();

    public Object getAttribute(String name);

}
