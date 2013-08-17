package net.xaethos.android.halparser.matchers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import net.xaethos.android.halparser.HALProperty;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.IOException;
import java.io.StringWriter;

public class HALPropertyMatcher extends TypeSafeDiagnosingMatcher<HALProperty> {
    private final String mName;
    private final Object mValue;

    public HALPropertyMatcher(String name, Object value) {
        mName = name;
        mValue = value;
    }

    @Override
    protected boolean matchesSafely(HALProperty actual, Description mismatchDescription) {
        String actualName = actual.getName();
        Object actualValue = actual.getValue();

        if (mName.equals(actualName) && mValue.equals(actualValue)) {
            return true;
        }

        mismatchDescription.appendText("was " + propertyToString(actualName, actualValue));
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(propertyToString(mName, mValue));
    }

    private String propertyToString(String name, Object value) {
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator gen = new JsonFactory().createGenerator(writer);
            gen.writeStartObject();
            gen.writeObjectField(name, value);
            gen.writeEndObject();
            gen.close();
            return writer.toString();
        } catch (IOException e) {
            return "Invalid value: " + value.toString();
        }
    }

    @Factory
    public static Matcher<HALProperty> halProperty(String name, Object value) {
        return new HALPropertyMatcher(name, value);
    }

}
