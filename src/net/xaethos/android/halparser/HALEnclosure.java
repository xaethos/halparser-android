package net.xaethos.android.halparser;

import java.net.URI;

public interface HALEnclosure
{

    public URI getBaseURI();

    public HALEnclosure getEnclosure();

}
