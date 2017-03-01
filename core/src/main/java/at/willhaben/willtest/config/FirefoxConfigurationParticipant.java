package at.willhaben.willtest.config;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * Can change the settings of a Firefox instance used by the tests.
 */
public interface FirefoxConfigurationParticipant {
    /**
     * It can adjust a {@link FirefoxProfile}. It can load for example browser plugins.
     *
     * @param firefoxProfile
     */
    default void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
    }

    /**
     * It can adjust a {@link FirefoxBinary}. For example it can set environment entries for the Firefox process
     *
     * @param firefoxBinary
     */
    default void adjustFirefoxBinary(FirefoxBinary firefoxBinary) {
    }
}
