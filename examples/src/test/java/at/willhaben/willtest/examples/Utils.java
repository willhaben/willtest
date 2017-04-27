package at.willhaben.willtest.examples;

import at.willhaben.willtest.config.DefaultFirefoxBinaryProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assume.assumeThat;

public final class Utils {
    private Utils() {}

    public static void assumeHavingFirefoxConfigured() {
        assumeThat(
                "Please define the path to your firefox executable using " +
                        DefaultFirefoxBinaryProvider.FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY + " system property! " +
                        "This is just an assumption to keep our build green.",
                System.getProperty(DefaultFirefoxBinaryProvider.FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY),
                is(notNullValue()));
    }
}
