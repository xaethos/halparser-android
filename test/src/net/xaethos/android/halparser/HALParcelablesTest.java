package net.xaethos.android.halparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

    // *** Helper methods

    protected <T extends Parcelable> T copyFromParceling(T original) {
        Parcel parcel = Parcel.obtain();
        parcel.writeParcelable(original, 0);
        parcel.setDataPosition(0);
        return parcel.readParcelable(original.getClass().getClassLoader());
    }

}
