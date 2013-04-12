package net.xaethos.android.halparser;

import java.util.List;
import java.util.Map;

public interface HALResource extends HALEnclosure
{

    public HALResource getParent();

    public Object getProperty(String name);

    public Map<String, Object> getProperties();

    public HALLink getLink(String rel);

    public List<HALLink> getLinks(String rel);

}
