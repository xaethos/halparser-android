package net.xaethos.android.halparser.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ThrowsMatcher extends TypeSafeDiagnosingMatcher<Runnable> {
    private final Class<? extends Throwable> mKlass;

    public ThrowsMatcher(Class<? extends Throwable> klass) {
        mKlass = klass;
    }

    @Override
    protected boolean matchesSafely(Runnable runnable, Description mismatchDescription) {
        try {
            runnable.run();
            mismatchDescription.appendText("did not throw.");
        }
        catch (Throwable t) {
            if (mKlass.isInstance(t)) return true;
            mismatchDescription.appendText("threw " + t.getClass().getSimpleName());
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("throws " + mKlass.getSimpleName());
    }

    @Factory
    public static <T> Matcher<Runnable> throwsA(Class<? extends Throwable> klass) {
        return new ThrowsMatcher(klass);
    }

}
