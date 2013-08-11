package net.xaethos.android.halparser;

import android.os.Parcelable;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface HALLink extends Parcelable
{

    public String getRel();
    public String getHref();

    public Object getAttribute(String name);
    public void setAttribute(String name, Object value);
    public void removeAttribute(String name);

    public Map<String, ?> getAttributes();

    public URI getURI();
    public URI getURI(Map<String, Object> map);

    public Set<String> getVariables();

    public String getTitle();
    public boolean isTemplated();

}
