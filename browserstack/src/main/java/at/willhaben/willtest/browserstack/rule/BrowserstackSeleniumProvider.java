package at.willhaben.willtest.browserstack.rule;

import at.willhaben.willtest.rule.AbstractSeleniumProvider;
import at.willhaben.willtest.util.Environment;
import org.junit.runner.Description;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link at.willhaben.willtest.config.SeleniumProvider} implementation to run tests on BrowserStack. It is
 * basically a normal selenium hub connection with some special properties
 */
//TODO: more options for selected OS and Browser, Multiple env run (see: https://github.com/browserstack/junit-browserstack)
public class BrowserstackSeleniumProvider extends
        AbstractSeleniumProvider<BrowserstackSeleniumProvider, RemoteWebDriver> {
    private static final String BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY = "browserstack.hub";
    private static final String BROWSERSTACK_HUB_LOCAL_SYSTEM_PROPERTY_KEY = "browserstack.local";

    private final DateTimeFormatter BUILD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    protected DesiredCapabilities createDesiredCapabilities(Description description) {
        DesiredCapabilities desiredCapabilities = super.createDesiredCapabilities(description);
        desiredCapabilities.setCapability("build", BUILD_DATE_FORMAT.format(ZonedDateTime.now()));
        desiredCapabilities.setCapability("name", getTestName(description));
        desiredCapabilities.setCapability(BROWSERSTACK_HUB_LOCAL_SYSTEM_PROPERTY_KEY,
                Boolean.parseBoolean(
                        Environment.getValue(BROWSERSTACK_HUB_LOCAL_SYSTEM_PROPERTY_KEY,
                                Boolean.FALSE.toString())));
        return desiredCapabilities;
    }

    @Override
    protected RemoteWebDriver constructWebDriver(DesiredCapabilities desiredCapabilities) {
        return new RemoteWebDriver(getBrowserstackHubURL(), desiredCapabilities);
    }

    @Override
    public BrowserstackSeleniumProvider getThis() {
        return this;
    }

    private String getTestName(Description description) {
        String className = description.getTestClass().getSimpleName();
        String methodName = description.getMethodName().replace('.', '_');
        return className + "_" + methodName;
    }

    private URL getBrowserstackHubURL() {
        String hubUrl = System.getProperty(BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY);
        if (hubUrl != null) {
            try {
                return new URL(hubUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid browserstack hub URL set by '" + BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY +
                        "' system property: " + hubUrl, e);
            }
        } else {
            throw new IllegalStateException(
                    "You did not specify '" + BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY + " system property which is a " +
                            "requirement if you use " + this.getClass().getName() + "!");
        }
    }
}
