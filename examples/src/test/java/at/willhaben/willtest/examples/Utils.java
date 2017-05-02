package at.willhaben.willtest.examples;

import at.willhaben.willtest.config.DefaultFirefoxBinaryProvider;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assume.assumeThat;

public final class Utils {
    private Utils() {}

    public static void assumeHavingFirefoxConfigured() {
        try {
            new FirefoxBinary();
        }
        catch ( WebDriverException e ) {
            if ( e.getMessage().contains("Cannot find firefox binary in PATH") ) {
                assumeThat(
                        "Please define the path to your firefox executable using " +
                                DefaultFirefoxBinaryProvider.FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY +
                                " system property, or add your firefox executable to the PATH variable! " +
                                "This is just an assumption to keep our build green.",
                        System.getProperty(DefaultFirefoxBinaryProvider.FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY),
                        is(notNullValue()));
            } else {
                throw e;
            }
        }
    }
}
