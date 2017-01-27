package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by liptak on 2016.08.24..
 */
public interface WebDriverConfigurationParticipant {
    default void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {}
    default void postConstruct(WebDriver webDriver) {}
}
