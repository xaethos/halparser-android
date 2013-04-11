package net.xaethos.android.halparser;

import java.util.Map;

public interface HALResource extends HALEnclosure
{

    public HALResource getParent();

    public Object getProperty(String name);

    public Map<String, Object> getProperties();

}
