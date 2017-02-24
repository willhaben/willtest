package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.SeleniumProvider;
import org.openqa.selenium.WebDriver;

public interface FirefoxProvider<P extends FirefoxProvider<P,D>,D extends WebDriver> extends SeleniumProvider<P,D> {
    /**
     * After creating a new {@link WebDriver} instance, a {@link SeleniumProvider} implementation should pass that instance
     * to all {@link FirefoxConfigurationParticipant} to let them to adjust it. This is used for local Firefox instances
     * or for ones running on a Selenium Hub.
     *
     * @param firefoxConfigurationParticipant the configuration participant to be added
     * @return the provider itself to make method chaining possible
     */
    P addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant);
}
