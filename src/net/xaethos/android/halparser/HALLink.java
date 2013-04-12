package net.xaethos.android.halparser;

public interface HALLink
{

    public HALResource getResource();

    public String getRel();

    public String getHref();

    public String getAttribute(String name);

}
