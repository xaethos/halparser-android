package net.xaethos.android.halparser.serializers;

import net.xaethos.android.halparser.HALParserTestCase;
import net.xaethos.android.halparser.tests.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class HALJsonSerializerWriteTest extends HALParserTestCase {

    public void testWrite() throws Exception {
        String jsonString = serializedResource(R.raw.example_without_href);
        assertThat(jsonString, is("{\"name\":\"Example Resource\"}"));
    }

    public void testWriteProperties() throws Exception {
        JSONObject obj = new JSONObject(serializedResource(R.raw.example_with_null_property));

        assertThat(obj.get("age"), is(instanceOf(Integer.class)));
        assertThat(obj.get("id"), is(instanceOf(Integer.class)));
        assertThat(obj.get("name"), is(instanceOf(String.class)));
        assertThat(obj.get("expired"), is(instanceOf(Boolean.class)));
        assertThat(obj.get("optional"), is(instanceOf(Boolean.class)));

        assertTrue(obj.isNull("nullprop"));

        assertThat(obj.getInt("age"), is(33));
        assertThat(obj.getInt("id"), is(123456));
        assertThat(obj.getString("name"), is("Example Resource"));
        assertThat(obj.getBoolean("expired"), is(false));
        assertThat(obj.getBoolean("optional"), is(true));
    }

    public void testWriteLinks() throws Exception {
        String json = serializedResource(R.raw.example);

        JSONObject links = new JSONObject(json).getJSONObject("_links");
        ArrayList<String> names = new ArrayList<String>();
        Iterator keys = links.keys();
        while (keys.hasNext()) names.add(keys.next().toString());

        assertThat(names.size(), is(4));
        assertThat(names, hasItems("curie", "self", "ns:parent", "ns:users"));

        JSONArray curies = links.getJSONArray("curie");
        assertThat(curies.length(), is(2));
        assertThat(curies.getJSONObject(0).getString("name"), is("ns"));
        assertThat(curies.getJSONObject(1).getString("name"), is("role"));

        JSONObject parent = links.getJSONObject("ns:parent");
        assertThat(parent.length(), is(4));
        assertThat(parent.getString("href"), is("https://example.com/api/customer/1234"));
        assertThat(parent.getString("name"), is("bob"));
        assertThat(parent.getString("title"), is("The Parent"));
        assertThat(parent.getString("hreflang"), is("en"));
    }

    public void testWriteEmbeded() throws Exception {
        String json = serializedResource(R.raw.example_with_multiple_nested_subresources);

        JSONArray users = new JSONObject(json).getJSONObject("_embedded").getJSONArray("ns:user");
        assertThat(users.length(), is(2));
        assertThat(users.getJSONObject(0).getInt("id"), is(11));
        assertThat(users.getJSONObject(1).getInt("id"), is(12));

        JSONObject phone = users.getJSONObject(0).getJSONObject("_embedded").getJSONObject("phone:cell");
        assertThat(phone.getString("number"), is("555-666-7890"));
    }

    // *** Helpers

    private String serializedResource(int resId) throws Exception {
        Writer writer = new StringWriter();
        getParser().write(newResource(resId), writer);
        return writer.toString();
    }

}
