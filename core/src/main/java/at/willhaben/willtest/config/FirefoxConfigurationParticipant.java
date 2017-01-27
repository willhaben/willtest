package at.willhaben.willtest.config;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * Created by liptak on 2016.08.25..
 */
public interface FirefoxConfigurationParticipant {
    default void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
    }

    default void adjustFirefoxBinary(FirefoxBinary firefoxBinary) {
    }
}
