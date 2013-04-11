package net.xaethos.android.halparser;

import java.io.Reader;
import java.net.URI;

import net.xaethos.android.halparser.impl.BaseHALResource;

public class HALJsonParser implements HALEnclosure
{
    private final URI mURI;

    public HALJsonParser(URI baseURI) {
        if (!baseURI.isAbsolute()) throw new IllegalArgumentException("Base URI must be absolute");
        mURI = baseURI;
    }

    public HALJsonParser(String baseURI) {
        this(URI.create(baseURI));
    }

    @Override
    public URI getBaseURI() {
        return mURI;
    }

    @Override
    public HALEnclosure getEnclosure() {
        return null;
    }

    public HALResource parse(Reader reader) {
        return new BaseHALResource.Builder(this).build();
    }

}
