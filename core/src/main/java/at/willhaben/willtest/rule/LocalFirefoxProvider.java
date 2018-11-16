package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.FirefoxConfiguration;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Starts a local firefox instance. For configuration you can use {@link #setFirefoxConfiguration(FirefoxConfiguration)},
 * {@link #addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant)} methods
 */
@Deprecated
public class LocalFirefoxProvider extends AbstractFirefoxProvider<LocalFirefoxProvider, FirefoxDriver> {
    @Override
    public FirefoxDriver constructWebDriver(DesiredCapabilities desiredCapabilities) {
        FirefoxBinary firefoxBinary = getFirefoxConfiguration().getFirefoxBinary();
        FirefoxProfile profile = getFirefoxConfiguration().getFirefoxProfile();
        FirefoxOptions firefoxOptions = new FirefoxOptions(desiredCapabilities);
        firefoxOptions.setBinary(firefoxBinary);
        firefoxOptions.setProfile(profile);
        return new FirefoxDriver(firefoxOptions);
    }

    @Override
    public LocalFirefoxProvider getThis() {
        return this;
    }
}
