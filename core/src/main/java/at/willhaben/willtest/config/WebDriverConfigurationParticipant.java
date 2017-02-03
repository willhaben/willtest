package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Makes possible to adjust the behaviour of the {@link WebDriver} instances created by a {@link SeleniumProvider}.
 * Use {@link SeleniumProvider#addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant)} to
 * make your {@link WebDriverConfigurationParticipant} work.<br/>
 * Example: setting window size using {@link #postConstruct(WebDriver)} method.<br/>
 * It makes possible to encapsulate some configuration aspects, which you can reuse with different kind of
 * {@link WebDriver} instances created by different implementations of {@link SeleniumProvider}.
 */
public interface WebDriverConfigurationParticipant {

    /**
     * Can add desired capabilities to a {@link WebDriver} before it gets created.
     *
     * @param desiredCapabilities
     */
    default void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
    }

    /**
     * Can make some changes on a newly created {@link WebDriver} before it gets used.
     *
     * @param webDriver
     */
    default void postConstruct(WebDriver webDriver) {
    }
}
