package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.*;
import at.willhaben.willtest.rule.*;
import org.apache.log4j.Level;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.ElementScrollBehavior;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A sample default configuration we are using. Basicly a composite made from the building blocks.
 */
public class SeleniumRule implements SeleniumProvider, TestRule {
    private static final Duration DEFAULT_IMPLICIT_WAIT = Duration.ofSeconds(15);
    private static final Duration DEFAULT_SCRIPT_TIMOUT_REQUIRED_BY_NG_DRIVER = Duration.ofSeconds(15);

    private final FirefoxConfig firefoxConfig = new FirefoxConfig();
    private final TimeoutsConfigurationParticipant timeoutsConfigurationParticipant =
            new TimeoutsConfigurationParticipant()
                    .withImplicitWait(DEFAULT_IMPLICIT_WAIT)
                    .withScriptTimeout(DEFAULT_SCRIPT_TIMOUT_REQUIRED_BY_NG_DRIVER);

    private final DefaultSeleniumProvider defaultSeleniumProvider =
            FileDetectorConfigurator.supportingFileUpload(
                    timeoutsConfigurationParticipant.addTo(
                            firefoxConfig.addTo(
                                    new DefaultSeleniumProvider())));

    private final LogContext logContext = new LogContext();
    private final LogFile logFile = new LogFile();
    private final ResourceHelper resourceHelper = new ResourceHelper();
    private final PageSource pageSource = new PageSource(defaultSeleniumProvider);
    private final Screenshot screenshot = new Screenshot(defaultSeleniumProvider);
    private final WebDriverLog webDriverLog = new WebDriverLog(defaultSeleniumProvider);
    private final JavascriptAlert javascriptAlert = new JavascriptAlert(defaultSeleniumProvider);
    private final JavascriptError javascriptError = new JavascriptError(defaultSeleniumProvider, false);

    private final RuleChain ruleChain = RuleChain
            .outerRule(logContext)
            .around(logFile)
            .around(defaultSeleniumProvider)
            .around(webDriverLog)
            .around(pageSource)
            .around(screenshot)
            .around(javascriptAlert)
            .around(javascriptError)
            .around(resourceHelper);

    @Override
    public WebDriver getWebDriver() {
        return defaultSeleniumProvider.getWebDriver();
    }

    @Override
    public SeleniumProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant) {
        return defaultSeleniumProvider.addWebDriverConfigurationParticipant(webDriverConfigurationParticipant);
    }

    @Override
    public SeleniumProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        return defaultSeleniumProvider.addFirefoxConfigurationParticipant(firefoxConfigurationParticipant);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return ruleChain.apply(base, description);
    }

    /**
     * Removes the implicit wait and creates a new explicit wait with a custom timeout. It can speed up the test
     * if the default implicit wait is not required.
     *
     * @param seconds explicit wait timeout in seconds
     * @return explicit webdriver wait
     */
    public Wait<WebDriver> waitOverridingImplicitWait(int seconds) {
        return timeoutsConfigurationParticipant.waitOverridingImplicitWait(defaultSeleniumProvider.getWebDriver(), seconds);
    }

    public SeleniumRule withAdBlocker() {
        return AdBlockerConfigurator.usingAdBlocker(this);
    }

    public ResourceHelper getResourceHelper() {
        return resourceHelper;
    }

    /**
     * See {@link ElementScrollBehaviourConfigurator} for details
     *
     * @param elementScrollBehaviour
     * @return
     */
    public SeleniumRule setElementScrollBehaviour(ElementScrollBehavior elementScrollBehaviour) {
        this.addWebDriverConfigurationParticipant(new ElementScrollBehaviourConfigurator(elementScrollBehaviour));
        return this;
    }

    public SeleniumRule withLogFileThreshold(Level threshold) {
        this.logFile.setThreshold(threshold);
        return this;
    }

    public SeleniumRule withImplicitWait(Duration implicitWait) {
        timeoutsConfigurationParticipant.withImplicitWait(implicitWait);
        return this;
    }

    public SeleniumRule withScriptTimeout(Duration scriptTimeout) {
        timeoutsConfigurationParticipant.withScriptTimeout(scriptTimeout);
        return this;
    }

    public SeleniumRule withPageLoadTimeout(Duration pageLoadTimeout) {
        timeoutsConfigurationParticipant.withPageLoadTimeout(pageLoadTimeout);
        return this;
    }

    public SeleniumRule withoutImplicitWait() {
        timeoutsConfigurationParticipant.withoutImplicitWait();
        return this;
    }

    public SeleniumRule withoutPageLoadTimeout() {
        timeoutsConfigurationParticipant.withoutPageLoadTimeout();
        return this;
    }

    public SeleniumRule withoutScriptTimeout() {
        timeoutsConfigurationParticipant.withoutScriptTimeout();
        return this;
    }

    public Optional<Duration> getImplicitWaitInMilliSeconds() {
        return timeoutsConfigurationParticipant.getImplicitWait();
    }

    public Optional<Duration> getScriptTimeoutInMilliSeconds() {
        return timeoutsConfigurationParticipant.getScriptTimeout();
    }

    public Optional<Duration> getPageLoadTimeoutInMilliSeconds() {
        return timeoutsConfigurationParticipant.getPageLoadTimeout();
    }
}
