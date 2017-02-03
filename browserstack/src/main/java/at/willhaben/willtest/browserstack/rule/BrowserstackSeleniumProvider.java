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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO: more options for selected OS and Browser, Multiple env run (see: https://github.com/browserstack/junit-browserstack)
public class BrowserstackSeleniumProvider extends AbstractWebDriverRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSeleniumProvider.class);
    private static final String BROWSERSTACK_HUB_SYSTEM_PROPERTY_KEY = "browserstackHub";
    private static final DateTimeFormatter BUILD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final List<WebDriverConfigurationParticipant> webDriverConfigurationParticipantList = new ArrayList<>();

    private WebDriver webDriver;
    private String testName = "default_test_name";
    private boolean isRunningLocal = false;

    public BrowserstackSeleniumProvider runLocal() {
        isRunningLocal = true;
        //TODO: implement local Browserstack run
        return this;
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
        DesiredCapabilities desiredCapabilities =
                addDesiredCapailities(
                        setupNameAndStandardConfig(new DesiredCapabilities()));
        callPostConstruct(webDriver =
                new RemoteWebDriver(getBrowserstackHubURL(), desiredCapabilities));
    }

    @Override
    public SeleniumProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant) {
        Objects.requireNonNull(webDriverConfigurationParticipant);
        this.webDriverConfigurationParticipantList.add(webDriverConfigurationParticipant);
        return this;
    }

    @Override
    //TODO: remove this from interface??
    public SeleniumProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        return null;
    }

    private WebDriver callPostConstruct(WebDriver webDriverToBeChangedAfterConstruction) {
        for (WebDriverConfigurationParticipant webDriverConfigurationParticipant : this.webDriverConfigurationParticipantList) {
            webDriverConfigurationParticipant.postConstruct(webDriverToBeChangedAfterConstruction);
        }
        return webDriverToBeChangedAfterConstruction;
    }

    private DesiredCapabilities setupNameAndStandardConfig(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability("build", BUILD_DATE_FORMAT.format(ZonedDateTime.now()));
        desiredCapabilities.setCapability("name", testName);
        if(isRunningLocal) {
            desiredCapabilities.setCapability("browserstack.local", "true");
        }
        return desiredCapabilities;
    }

    private void createTestName(Description description) {
        String className = description.getTestClass().getSimpleName();
        String methodName = description.getMethodName().replace('.', '_');
        testName = className + "_" + methodName;
    }

    private DesiredCapabilities addDesiredCapailities(DesiredCapabilities desiredCapabilities) {
        for (WebDriverConfigurationParticipant webDriverConfigurationParticipant : this.webDriverConfigurationParticipantList) {
            webDriverConfigurationParticipant.addDesiredCapabilities(desiredCapabilities);
        }
        return desiredCapabilities;
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
