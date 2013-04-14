package net.xaethos.android.halparser;

import java.net.URI;

import android.os.Parcelable;

public interface HALEnclosure extends Parcelable
{

    public URI getBaseURI();

    public HALEnclosure getEnclosure();

}
