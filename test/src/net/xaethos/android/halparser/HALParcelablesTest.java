package net.xaethos.android.halparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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

        assertThat(resource.getEnclosure().getBaseURI(), is(exampleURI));
        Map<String, Object> properties = resource.getProperties();

        assertThat(properties.keySet(), contains("age", "expired", "id", "name", "optional"));

        assertThat((String) properties.get("name"), is("Example Resource"));

        assertThat((Integer) properties.get("age"), is(33));
        assertThat((Integer) properties.get("id"), is(123456));

        assertThat((Boolean) properties.get("expired"), is(false));
        assertThat((Boolean) properties.get("optional"), is(true));
    }

    // *** Helper methods

    protected <T extends Parcelable> T copyFromParceling(T original) {
        Parcel parcel = Parcel.obtain();
        parcel.writeParcelable(original, 0);
        parcel.setDataPosition(0);
        return parcel.readParcelable(original.getClass().getClassLoader());
    }

}
