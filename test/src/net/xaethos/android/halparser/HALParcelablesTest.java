package net.xaethos.android.halparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;

import net.xaethos.android.halparser.tests.R;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class HALParcelablesTest extends HALParserTestCase
{

    public void testParcelableTesting() {
        Point p = new Point(1, 2);
        assertThat(copyFromParceling(p), is(equalTo(p)));
    }

    public void testHALJsonParser() {
        parser = getParser();
        HALJsonParser copy = copyFromParceling(parser);
        assertThat(copy.getBaseURI(), is(exampleURI));
    }

    public void testParcelsProperties() throws Exception {
        resource = copyFromParceling(newResource(R.raw.example));

        assertThat(resource.getBaseURI(), is(exampleURI));
        Map<String, Object> properties = resource.getProperties();

        assertThat(properties.keySet(), contains("age", "expired", "id", "name", "optional"));

        assertThat((String) properties.get("name"), is("Example Resource"));

        assertThat((Integer) properties.get("age"), is(33));
        assertThat((Integer) properties.get("id"), is(123456));

        assertThat((Boolean) properties.get("expired"), is(false));
        assertThat((Boolean) properties.get("optional"), is(true));
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

    // *** Helper methods

    protected <T extends Parcelable> T copyFromParceling(T original) {
        Parcel parcel = Parcel.obtain();
        parcel.writeParcelable(original, 0);
        parcel.setDataPosition(0);
        return parcel.readParcelable(original.getClass().getClassLoader());
    }

}
