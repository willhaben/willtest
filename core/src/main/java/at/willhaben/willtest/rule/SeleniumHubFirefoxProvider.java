package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.FirefoxConfiguration;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import at.willhaben.willtest.util.Environment;
import org.junit.runner.Description;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Starts a remote firefox instance on a selenium hub. For configuration you can use
 * {@link #setFirefoxConfiguration(FirefoxConfiguration)},
 * {@link #addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant)} methods
 */
@Deprecated
public class SeleniumHubFirefoxProvider extends AbstractFirefoxProvider<SeleniumHubFirefoxProvider, RemoteWebDriver> {
    private static final String DEFAULT_PLATFORM_LINUX = "Linux";
    private static final String SELENIUM_HUB_SYSTEM_PROPERTY_KEY = "seleniumHub";

    @Override
    protected RemoteWebDriver constructWebDriver(DesiredCapabilities desiredCapabilities) {
        return new RemoteWebDriver(getSeleniumHubURL(), desiredCapabilities);
    }

    private Platform getPlatform() {
        String platformString = Environment.getValue("platform", DEFAULT_PLATFORM_LINUX);
        return Platform.fromString(platformString);
    }

    @Override
    protected DesiredCapabilities createDesiredCapabilities(Description description) {
        DesiredCapabilities desiredCapabilities = super.createDesiredCapabilities(description);
        desiredCapabilities.setPlatform(getPlatform());
        return desiredCapabilities;
    }


    private URL getSeleniumHubURL() {
        String hubUrl = System.getProperty(SELENIUM_HUB_SYSTEM_PROPERTY_KEY);
        if (hubUrl != null) {
            try {
                return new URL(hubUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid selenium hub URL set by '" + SELENIUM_HUB_SYSTEM_PROPERTY_KEY +
                        "' system property: " + hubUrl, e);
            }
        } else {
            throw new IllegalStateException(
                    "You did specify that you want to run the tests using " +
                            this.getClass().getName() + " as provider on a seleniumHub. " +
                            "You need to specify the URL of your Selenium HUB URL using '" +
                            SELENIUM_HUB_SYSTEM_PROPERTY_KEY + "' system property!");
        }
    }

    @Override
    public SeleniumHubFirefoxProvider getThis() {
        return this;
    }
}