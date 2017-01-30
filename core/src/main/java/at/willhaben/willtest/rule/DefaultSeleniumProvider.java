package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.DefaultFirefoxBinaryProvider;
import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.util.Environment;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultSeleniumProvider extends AbstractRule implements SeleniumProvider {
    private static final Logger LOGGER = Logger.getLogger(DefaultSeleniumProvider.class);
    private static final String DEFAULT_PLATFORM_LINUX = "Linux";
    private static final String DEFAULT_SELENIUM_HUB = "http://enter_your_hub_url_using_seleniumHub_system_property.io";
    private static final String SELENIUM_HUB_SYSTEM_PROPERTY_KEY = "seleniumHub";
    private final List<WebDriverConfigurationParticipant> webDriverConfigurationParticipantList = new ArrayList<>();
    private final List<FirefoxConfigurationParticipant> firefoxConfigurationParticipantList = new ArrayList<>();

    private WebDriver webDriver;

    private Optional<URL> getSeleniumHubURL() {
        if (Boolean.getBoolean("local")) {
            return Optional.empty();
        } else {
            String hubUrl = System.getProperty(SELENIUM_HUB_SYSTEM_PROPERTY_KEY, DEFAULT_SELENIUM_HUB);
            try {
                return Optional.of(new URL(hubUrl));
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid selenium hub URL.", e);
            }
        }
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
    protected void before(Description description) throws Throwable {
        super.before(description);
        DesiredCapabilities desiredCapabilities = createDesiredCapabilities();
        FirefoxProfile firefoxProfile = createFirefoxProfile();
        desiredCapabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
        webDriver = callPostConstruct(
                getSeleniumHubURL()
                        .map(seleniumHubURL -> new RemoteWebDriver(seleniumHubURL, desiredCapabilities))
                        .orElseGet(() -> new FirefoxDriver(createFirefoxBinary(), firefoxProfile)));
    }

    private WebDriver callPostConstruct(WebDriver webDriverToBeChangedAfterConstruction) {
        for (WebDriverConfigurationParticipant webDriverConfigurationParticipant : this.webDriverConfigurationParticipantList) {
            webDriverConfigurationParticipant.postConstruct(webDriverToBeChangedAfterConstruction);
        }
        return webDriverToBeChangedAfterConstruction;
    }

    private DesiredCapabilities addDefaultDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability("applicationCacheEnabled", false);
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setBrowserName("firefox");
        return desiredCapabilities;
    }

    private DesiredCapabilities addPlatform(DesiredCapabilities desiredCapabilities) {
        String platformString = Environment.getValue("platform", DEFAULT_PLATFORM_LINUX);
        Platform platform = Platform.fromString(platformString);
        desiredCapabilities.setPlatform(platform);
        return desiredCapabilities;
    }

    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception ex) {
                if (!ex.getMessage().contains("It may have died")) {
                    throw ex;
                }
                LOGGER.warn("Error while closing browser. This error cannot be avoided somehow. " +
                        "This is not a big problem.", ex);
            }
            webDriver = null;
        }
    }

    private DesiredCapabilities createDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities =
                addDefaultDesiredCapabilities(
                        addPlatform(new DesiredCapabilities()));
        for (WebDriverConfigurationParticipant webDriverConfigurationParticipant : this.webDriverConfigurationParticipantList) {
            webDriverConfigurationParticipant.addDesiredCapabilities(desiredCapabilities);
        }
        return desiredCapabilities;
    }

    private FirefoxBinary createFirefoxBinary() {
        //TODO:make it configurable
        return new DefaultFirefoxBinaryProvider().getFirefoxBinary();
    }


    private FirefoxProfile createFirefoxProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        for (FirefoxConfigurationParticipant firefoxConfigurationParticipant : this.firefoxConfigurationParticipantList) {
            firefoxConfigurationParticipant.adjustFirefoxProfile(profile);
        }
        return profile;
    }

    @Override
    public SeleniumProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant) {
        Objects.requireNonNull(webDriverConfigurationParticipant);
        this.webDriverConfigurationParticipantList.add(webDriverConfigurationParticipant);
        return this;
    }

    @Override
    public SeleniumProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        Objects.requireNonNull(firefoxConfigurationParticipant);
        this.firefoxConfigurationParticipantList.add(firefoxConfigurationParticipant);
        return this;
    }
}