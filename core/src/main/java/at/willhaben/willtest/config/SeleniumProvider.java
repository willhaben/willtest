package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Creates a webdriver instance with respecting all configuration participants given to it.
 * <p>
 * Created by liptak on 2016.08.24..
 */
public interface SeleniumProvider {
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
     * to all {@link WebDriverConfigurationParticipant} to let them to adjust it.
     *
     * @param webDriverConfigurationParticipant
     * @return the provider itself to make method chaining possible
     */
    SeleniumProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant);

    /**
     * After creating a new {@link WebDriver} instance, a {@link SeleniumProvider} implementation should pass that instance
     * to all {@link FirefoxConfigurationParticipant} to let them to adjust it. This is used for local Firefox instances
     * or for ones running on a Selenium Hub.
     *
     * @param firefoxConfigurationParticipant
     * @return the provider itself to make method chaining possible
     */
    //TODO: is it good here?
    SeleniumProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant);
}
