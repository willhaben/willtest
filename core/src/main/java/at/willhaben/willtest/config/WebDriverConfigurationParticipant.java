package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Makes possible to adjust the behaviour of the {@link WebDriver} instances created by a {@link SeleniumProvider}.
 * Use {@link SeleniumProvider#addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant)} to
 * make your {@link WebDriverConfigurationParticipant} work.
 * <p>
 * Example: setting window size using {@link #postConstruct(WebDriver)} method.
 * <p>
 * It makes possible to encapsulate some configuration aspects, which you can reuse with different kind of
 * {@link WebDriver} instances created by different implementations of {@link SeleniumProvider}.
 *
 * @param <D> concrete webDriver implementation class
 */
@Deprecated
public interface WebDriverConfigurationParticipant<D extends WebDriver> {

    /**
     * Can add desired capabilities to a {@link WebDriver} before it gets created.
     *
     * @param desiredCapabilities desired capabilities object, which can be adjusted
     */
    default void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
    }

    /**
     * Can make some changes on a newly created {@link WebDriver} before it gets used.
     *
     * @param webDriver webdriver to be changed
     */
    default void postConstruct(D webDriver) {
    }
}
