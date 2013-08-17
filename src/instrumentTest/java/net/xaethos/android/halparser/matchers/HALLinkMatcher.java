package net.xaethos.android.halparser.matchers;

import net.xaethos.android.halparser.HALLink;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HALLinkMatcher extends TypeSafeDiagnosingMatcher<HALLink> {
    private final String mHref;

    public HALLinkMatcher(String href) {
        mHref = href;
    }

    @Override
    protected boolean matchesSafely(HALLink actual, Description mismatchDescription) {
        if (mHref.equals(actual.getHref())) return true;

        mismatchDescription.appendText("was " + actual.getHref());
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("link to " + mHref);
    }

    @Factory
    public static Matcher<HALLink> halLinkTo(String href) {
        return new HALLinkMatcher(href);
    }

}
