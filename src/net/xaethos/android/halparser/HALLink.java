package net.xaethos.android.halparser;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import android.os.Parcelable;

public interface HALLink extends Parcelable
{

    public URI getBaseURI();

    public String getRel();

    public String getHref();

    public URI getURI();

    public URI getURI(Map<String, Object> map);

    public Set<String> getVariables();

    public Object getAttribute(String name);

    public boolean isTemplated();

}
