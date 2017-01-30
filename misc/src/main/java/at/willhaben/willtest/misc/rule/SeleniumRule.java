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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A sample default configuration we are using. Basicly a composite made from the building blocks.
 */
public class SeleniumRule implements WebDriverProvider, TestRule {
    private static final long DEFAULT_IMPLICIT_WAIT = 15;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final long DEFAULT_SCRIPT_TIMOUT_REQUIRED_BY_NG_DRIVER_SECONDS = 15;

    private final FirefoxConfig firefoxConfig = new FirefoxConfig();
    private final TimeoutsConfigurationParticipant timeoutsConfigurationParticipant =
            new TimeoutsConfigurationParticipant()
                    .withImplicitWait(DEFAULT_IMPLICIT_WAIT, DEFAULT_TIME_UNIT)
                    .withScriptTimeout(DEFAULT_SCRIPT_TIMOUT_REQUIRED_BY_NG_DRIVER_SECONDS, DEFAULT_TIME_UNIT);

    private final DefaultWebDriverProvider defaultWebDriverProvider =
            FileDetectorConfigurator.supportingFileUpload(
                    timeoutsConfigurationParticipant.addTo(
                            firefoxConfig.addTo(
                                    new DefaultWebDriverProvider())));

    private final LogContext logContext = new LogContext();
    private final LogFile logFile = new LogFile();
    private final ResourceHelper resourceHelper = new ResourceHelper();
    private final PageSource pageSource = new PageSource(defaultWebDriverProvider);
    private final Screenshot screenshot = new Screenshot(defaultWebDriverProvider);
    private final WebDriverLog webDriverLog = new WebDriverLog(defaultWebDriverProvider);
    private final JavascriptAlert javascriptAlert = new JavascriptAlert(defaultWebDriverProvider);
    private final JavascriptError javascriptError = new JavascriptError(defaultWebDriverProvider, false);

    private final RuleChain ruleChain = RuleChain
            .outerRule(logContext)
            .around(logFile)
            .around(defaultWebDriverProvider)
            .around(webDriverLog)
            .around(pageSource)
            .around(screenshot)
            .around(javascriptAlert)
            .around(javascriptError)
            .around(resourceHelper);

    @Override
    public WebDriver getWebDriver() {
        return defaultWebDriverProvider.getWebDriver();
    }

    @Override
    public WebDriverProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant) {
        return defaultWebDriverProvider.addWebDriverConfigurationParticipant(webDriverConfigurationParticipant);
    }

    @Override
    public WebDriverProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        return defaultWebDriverProvider.addFirefoxConfigurationParticipant(firefoxConfigurationParticipant);
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
        return timeoutsConfigurationParticipant.waitOverridingImplicitWait(defaultWebDriverProvider.getWebDriver(), seconds);
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

    public SeleniumRule withImplicitWait(Long timeout, TimeUnit timeUnit) {
        timeoutsConfigurationParticipant.withImplicitWait(timeout, timeUnit);
        return this;
    }

    public SeleniumRule withScriptTimeout(Long timeout, TimeUnit timeUnit) {
        timeoutsConfigurationParticipant.withScriptTimeout(timeout, timeUnit);
        return this;
    }

    public SeleniumRule withPageLoadTimeout(Long timeout, TimeUnit timeUnit) {
        timeoutsConfigurationParticipant.withPageLoadTimeout(timeout, timeUnit);
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

    public Optional<Long> getImplicitWaitInMilliSeconds() {
        return timeoutsConfigurationParticipant.getImplicitWaitInMilliSeconds();
    }

    public Optional<Long> getScriptTimeoutInMilliSeconds() {
        return timeoutsConfigurationParticipant.getScriptTimeoutInMilliSeconds();
    }

    public Optional<Long> getPageLoadTimeoutInMilliSeconds() {
        return timeoutsConfigurationParticipant.getPageLoadTimeoutInMilliSeconds();
    }
}
