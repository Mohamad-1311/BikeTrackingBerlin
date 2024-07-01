package de.htw_berlin.mob_sys.biketrackingberlin;

import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class CustomMatchers {
    public static TypeSafeMatcher<View> withTextIgnoringCommas(final String expectedText) {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof TextView)) {
                    return false;
                }
                String text = ((TextView) view).getText().toString().replace(",", ".");
                return text.equals(expectedText.replace(",", "."));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text ignoring commas: " + expectedText);
            }
        };
    }
}
