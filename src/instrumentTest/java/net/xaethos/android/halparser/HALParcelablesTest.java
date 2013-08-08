package net.xaethos.android.halparser;

import android.os.Parcel;
import android.os.Parcelable;

import net.xaethos.android.halparser.impl.BaseHALLink;
import net.xaethos.android.halparser.tests.R;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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

        Map<String, ? extends HALProperty> properties = resource.getProperties();

        assertThat(properties.keySet(), contains("age", "expired", "id", "name", "optional"));

        assertThat((String) properties.get("name").getValue(), is("Example Resource"));

        assertThat((Integer) properties.get("age").getValue(), is(33));
        assertThat((Integer) properties.get("id").getValue(), is(123456));

        assertThat((Boolean) properties.get("expired").getValue(), is(false));
        assertThat((Boolean) properties.get("optional").getValue(), is(true));
    }

    public void testParcelsLinks() throws Exception {
        resource = copyFromParceling(newResource(R.raw.example));

        List<HALLink> links;
        links = resource.getLinks("curie");
        assertThat(links, is(notNullValue()));
        assertThat(links, hasSize(2));
        assertThat(links.get(0).getHref(), is("https://example.com/apidocs/accounts"));
        assertThat(links.get(1).getHref(), is("https://example.com/apidocs/roles"));

        assertUnmodifiable(links);

        assertThat(resource.getLinkRels(), contains("curie", "self", "ns:parent", "ns:users"));
        assertEquals("https://example.com/apidocs/accounts", resource.getLink("curie").getHref());
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
