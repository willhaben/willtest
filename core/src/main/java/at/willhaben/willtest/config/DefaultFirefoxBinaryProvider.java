package at.willhaben.willtest.config;

import org.openqa.selenium.firefox.FirefoxBinary;

import java.io.File;

/**
 * Lets the user to customize the firefox installation to be used, when a test runs locally. This is useful,
 * when the selenium version you are using does not support the latest firefox version. The place of the firefox
 * binary can be defined by {@value FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY} system property.
 */
@Deprecated
public class DefaultFirefoxBinaryProvider implements FirefoxBinaryProvider {
    /**
     * Env entry to contain a path to the firefox binary to be used.
     */
    public static final String FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY = "ffBinary";

    @Override
    public FirefoxBinary getFirefoxBinary() {
        FirefoxBinary result;
        String firefoxBinaryLocation = System.getProperty(FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY);
        if (firefoxBinaryLocation != null) {
            File pathToBinary = new File(firefoxBinaryLocation);
            result = new FirefoxBinary(pathToBinary);
        } else {
            result = new FirefoxBinary();
        }
        return result;
    }
}
