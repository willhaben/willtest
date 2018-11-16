package at.willhaben.willtest.config;

import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Creates a {@link WebDriver} instance with respecting all configuration participants given to it.
 * <p>
 *
 * @param <P> concrete implementation class. Makes type safe method chaining in implementations possible
 * @param <D> concrete implementation of {@link WebDriver} provided
 */
@Deprecated
public interface SeleniumProvider<P extends SeleniumProvider, D extends WebDriver> {
    long FIND_ELEMENT_TIMEOUT = 30;

    /**
     * @return a new {@link WebDriver} instance
     */
    WebDriver getWebDriver();

    /**
     * Since {@link org.openqa.selenium.support.ui.Wait} is something, what is fast always used in selenium tests,
     * this method returns a default wait to be used with the {@link WebDriver}. This way there is no need to create
     * different instances in page objects with different timeouts. This makes easy to change all timeouts at a single
     * place later. Default implementation returns a {@link WebDriverWait} with {@value FIND_ELEMENT_TIMEOUT} seconds
     * timeout.
     *
     * @return the default {@link WebDriverWait} instance to be used with the WebDriver
     */
    default WebDriverWait getDefaultWebDriverWait() {
        return new WebDriverWait(getWebDriver(), FIND_ELEMENT_TIMEOUT);
    }

    /**
     * After creating a new {@link WebDriver} instance, a {@link SeleniumProvider} implementation should pass that instance
     * to all {@link WebDriverConfigurationParticipant} to let them adjust it.
     *
     * @param webDriverConfigurationParticipant participant to be added to the list
     * @return the provider itself to make method chaining possible
     */
    P addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant<D> webDriverConfigurationParticipant);

    /**
     * Registers a new instance of {@link WebDriverEventListener} {@literal &} {@link TestRule} to the created
     * {@link SeleniumProvider}.
     *
     * @param listener Implementation of {@link WebDriverEventListener} {@literal &} {@link TestRule} added to the list of listeners
     * @return the provider itself to make method chaining possible
     */
    <T extends WebDriverEventListener & TestRule> P addWebDriverEventListener(T listener);

    /**
     * Gives back the current instance as #P back to make method chaining possible in a type safe manner.
     *
     * @return the current instance. Enables type safe method chaining in implementations
     */
    P getThis();
}
