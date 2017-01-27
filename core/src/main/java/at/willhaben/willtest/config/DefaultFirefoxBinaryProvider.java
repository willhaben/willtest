package at.willhaben.willtest.config;

import org.openqa.selenium.firefox.FirefoxBinary;

import java.io.File;

/**
 * Created by liptak on 2017.01.26..
 */
public class DefaultFirefoxBinaryProvider implements FirefoxBinaryProvider {
    /**
     * Env entry to contain a path to the firefox binary to be used.
     */
    private static final String FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY = "ffBinary";

    @Override
    public FirefoxBinary getFirefoxBinary() {
        FirefoxBinary result;
        String firefoxBinaryLocation = System.getProperty(FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY);
        if ( firefoxBinaryLocation != null ) {
            File pathToBinary = new File(firefoxBinaryLocation);
            result = new FirefoxBinary(pathToBinary);
        }
        else {
            result = new FirefoxBinary();
        }
        return result;
    }
}
