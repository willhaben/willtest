package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.FirefoxConfiguration;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Parent class for all implementations of {@link at.willhaben.willtest.config.SeleniumProvider} for Firefox.
 * Injects the output {@link org.openqa.selenium.firefox.FirefoxProfile} of {@link FirefoxConfiguration} as
 * {@link WebDriverConfigurationParticipant}.
 * @param <P> type of the provider implementation
 * @param <D> type of the webdriver, which will be provided
 */
public abstract class AbstractFirefoxProvider<P extends AbstractFirefoxProvider<P, D>, D extends WebDriver>
        extends AbstractSeleniumProvider<P, D> {
    private FirefoxConfiguration firefoxConfiguration;

    public AbstractFirefoxProvider() {
        addWebDriverConfigurationParticipant(new WebDriverConfigurationParticipant<D>() {
            @Override
            public void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
                desiredCapabilities.setCapability(FirefoxDriver.PROFILE, firefoxConfiguration.getFirefoxProfile());
            }
        });
    }

    protected FirefoxConfiguration getFirefoxConfiguration() {
        return firefoxConfiguration;
    }

    public void setFirefoxConfiguration(FirefoxConfiguration firefoxConfiguration) {
        this.firefoxConfiguration = firefoxConfiguration;
    }
}
