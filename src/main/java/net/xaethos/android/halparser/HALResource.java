package net.xaethos.android.halparser;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Parcelable;

public interface HALResource extends Parcelable
{

    public URI getBaseURI();

    public Object getProperty(String name);

    public Map<String, Object> getProperties();

    public HALLink getLink(String rel);

    public List<HALLink> getLinks(String rel);

    public Set<String> getLinkRels();

    public HALResource getResource(String rel);

    public List<HALResource> getResources(String rel);

    public Set<String> getResourceRels();

}
