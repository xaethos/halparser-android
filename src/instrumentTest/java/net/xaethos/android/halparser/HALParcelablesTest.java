package net.xaethos.android.halparser;

import android.os.Parcel;
import android.os.Parcelable;

import net.xaethos.android.halparser.impl.BaseHALLink;
import net.xaethos.android.halparser.tests.R;

import static net.xaethos.android.halparser.matchers.HALLinkMatcher.halLinkTo;
import static net.xaethos.android.halparser.matchers.HALPropertyMatcher.halProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class HALParcelablesTest extends HALParserTestCase
{

    public void testBaseHALLink() {
        BaseHALLink link = new BaseHALLink("foo", "/foo");
        link.setAttribute("title", "Foolicious");

        BaseHALLink copy = copyFromParceling(link);

        assertThat(link.getRel(), is(copy.getRel()));
        assertThat(link.getHref(), is(equalTo(copy.getHref())));
        assertThat(link.getAttributes(), is(equalTo(copy.getAttributes())));
    }

    public void testParcelsProperties() throws Exception {
        resource = copyFromParceling(newResource(R.raw.example));

        assertThat(resource.getProperties(), contains(
                halProperty("age", 33),
                halProperty("expired", false),
                halProperty("id", 123456),
                halProperty("name", "Example Resource"),
                halProperty("optional", true)
        ));
    }

    public void testParcelsLinks() throws Exception {
        resource = copyFromParceling(newResource(R.raw.example));

        assertThat(resource.getLinkRels(), contains("curie", "self", "ns:parent", "ns:users"));
        assertThat(resource.getLinks("curie"), contains(
                halLinkTo("https://example.com/apidocs/accounts"),
                halLinkTo("https://example.com/apidocs/roles")
        ));
    }

    public void testParcelsSubresources() throws Exception {
        resource = copyFromParceling(newResource(R.raw.example_with_subresource));
        HALResource subresource = resource.getResource("ns:user");

        assertThat(subresource, is(notNullValue()));
        assertThat(subresource.getLink("self").getHref(), is("https://example.com/user/11"));
        assertThat((Integer) subresource.getValue("age"), is(32));
    }

    // *** Helper methods

    protected <T extends Parcelable> T copyFromParceling(T original) {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.writeParcelable(original, 0);
            parcel.setDataPosition(0);
            return parcel.readParcelable(original.getClass().getClassLoader());
        }
        finally {
            parcel.recycle();
        }
    }

}
