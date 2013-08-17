package net.xaethos.android.halparser;

import android.os.Parcelable;

import java.util.Collection;
import java.util.Set;

public interface HALResource extends Parcelable
{

    /* Properties */

    public HALProperty getProperty(String name);
    public void setProperty(HALProperty property);
    public void removeProperty(String name);

    public Collection<HALProperty> getProperties();
    public Collection<String> getPropertyNames();

    public Object getValue(String propertyName);
    public String getValueString(String propertyName);
    public void setValue(String propertyName, Object value);

    /* Links */

    public HALLink getLink(String rel);
    public Collection<HALLink> getLinks(String rel);
    public void addLink(HALLink link);
    public void removeLink(HALLink link);

    public Set<String> getLinkRels();

    /* Embedded resources */

    public HALResource getResource(String rel);
    public Collection<HALResource> getResources(String rel);
    public void addResource(HALResource resource, String rel);
    public void removeResource(HALResource resource, String rel);

    public Set<String> getResourceRels();

}
