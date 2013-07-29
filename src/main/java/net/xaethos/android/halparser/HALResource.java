package net.xaethos.android.halparser;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Parcelable;

public interface HALResource extends Parcelable
{

    /* Resource info */

    public URI getBaseURI();

    /* Properties */

    public HALProperty getProperty(String name);
    public Map<String, ? extends HALProperty> getProperties();
    public Object getValue(String propertyName);

    /* Links */

    public HALLink getLink(String rel);
    public List<HALLink> getLinks(String rel);
    public Set<String> getLinkRels();

    /* Embedded resources */

    public HALResource getResource(String rel);
    public List<HALResource> getResources(String rel);
    public Set<String> getResourceRels();

}
