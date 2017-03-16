package at.willhaben.willtest.config;

import org.openqa.selenium.firefox.FirefoxBinary;

/**
 * You might want to run tests with different versions of Firefox. The implementations of this interface should
 * return a {@link FirefoxBinary} instance.
 */
public interface FirefoxBinaryProvider {

    /**
     * Creates a firefox binary (executable of firefox)
     *
     * @return firefox binary instance
     */
    FirefoxBinary getFirefoxBinary();
}
