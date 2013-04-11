package net.xaethos.android.halparser;

public interface HALResource extends HALEnclosure
{

    public HALResource getParent();

    public Object getProperty(String name);

}
