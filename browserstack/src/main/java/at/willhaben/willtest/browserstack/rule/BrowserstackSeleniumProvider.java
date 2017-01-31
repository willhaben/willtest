package at.willhaben.willtest.browserstack.rule;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import at.willhaben.willtest.rule.AbstractWebDriverRule;
import at.willhaben.willtest.rule.DefaultSeleniumProvider;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by weisgrmi on 31.01.2017.
 */
//TODO: more options for selected OS and Browser, Multiple env run (see: https://github.com/browserstack/junit-browserstack)
public class BrowserstackSeleniumProvider extends AbstractWebDriverRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSeleniumProvider.class);
    private static final String BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY = "browserstackHub";
    private static final DateTimeFormatter BUILD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private WebDriver webDriver;
    private String testName = "default_test_name";
    private boolean isRunningLocal = false;

    public BrowserstackSeleniumProvider() {
    }

    public BrowserstackSeleniumProvider(boolean isRunningLocal) {
        this.isRunningLocal = isRunningLocal;
        //TODO: implement local Browserstack run
    }

    @Override
    public WebDriver getWebDriver() {
        if (webDriver == null) {
            throw new RuntimeException("WebDriver has not been created. Did you use this class as a junit rule? " +
                    "Can it be, that the before method has not been called?");
        }
        return webDriver;
    }

    @Override
    protected void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    protected void before(Description description) throws Throwable {
        createTestName(description);
        DesiredCapabilities desiredCapabilities = setupNameAndStandardConfig(new DesiredCapabilities());
        webDriver = new RemoteWebDriver(getBrowserstackHubURL(), desiredCapabilities);
    }

    @Override
    public SeleniumProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant) {
        return null;
    }

    @Override
    //TODO: remove this from interface??
    public SeleniumProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        return null;
    }

    private DesiredCapabilities setupNameAndStandardConfig(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability("build", BUILD_DATE_FORMAT.format(ZonedDateTime.now()));
        desiredCapabilities.setCapability("name", testName);
        return desiredCapabilities;
    }

    private void createTestName(Description description) {
        String className = description.getTestClass().getSimpleName();
        String methodName = description.getMethodName().replace('.', '_');
        testName = className + "_" + methodName;
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
                    "You did not specify '" + BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY + " system property. ");
        }
    }
}
