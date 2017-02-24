package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.*;
import at.willhaben.willtest.rule.*;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.ElementScrollBehavior;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.Optional;

/**
 * A sample default configuration we are using. Basically a composite made from the building blocks.
 */
public class SeleniumRule<W extends SeleniumProvider<W,D> & TestRule,D extends WebDriver> implements
        FirefoxProvider<SeleniumRule<W,D>,D>, TestRule {
    private static final Duration DEFAULT_IMPLICIT_WAIT = Duration.ofSeconds(15);
    private static final Duration DEFAULT_SCRIPT_TIMEOUT_REQUIRED_BY_NG_DRIVER = Duration.ofSeconds(15);

    private final TimeoutsConfigurationParticipant<D> timeoutsConfigurationParticipant =
            new TimeoutsConfigurationParticipant<D>()
                    .withImplicitWait(DEFAULT_IMPLICIT_WAIT)
                    .withScriptTimeout(DEFAULT_SCRIPT_TIMEOUT_REQUIRED_BY_NG_DRIVER);

    private final W defaultSeleniumProvider = SeleniumProviderFactory.create();

    private final LogContext logContext = new LogContext();
    private final LogFile logFile = new LogFile();
    private final ResourceHelper resourceHelper = new ResourceHelper();
    private final PageSource pageSource = new PageSource(defaultSeleniumProvider);
    private final Screenshot screenshot = new Screenshot(defaultSeleniumProvider);
    private final WebDriverLog<W,D> webDriverLog = new WebDriverLog<>(defaultSeleniumProvider);
    private final JavascriptAlert javascriptAlert = new JavascriptAlert(defaultSeleniumProvider);

    private RuleChain ruleChain;

    public SeleniumRule() {
        ruleChain = RuleChain
                .outerRule(logContext)
                .around(logFile)
                .around(defaultSeleniumProvider)
                .around(webDriverLog)
                .around(pageSource)
                .around(screenshot)
                .around(javascriptAlert);

        FileDetectorConfigurator.supportingFileUpload(defaultSeleniumProvider);
        timeoutsConfigurationParticipant.addTo(defaultSeleniumProvider);
        if ( defaultSeleniumProvider instanceof FirefoxProvider ) {
            FirefoxProvider firefoxProvider = (FirefoxProvider) defaultSeleniumProvider;
            FirefoxConfig.addTo(firefoxProvider);
            ruleChain = ruleChain.around(new JavascriptError<>( firefoxProvider,false));
        }
        ruleChain = ruleChain.around(resourceHelper);
    }

    @Override
    public D getWebDriver() {
        return defaultSeleniumProvider.getWebDriver();
    }

    @Override
    public SeleniumRule<W, D> getThis() {
        return this;
    }


    @Override
    public SeleniumRule<W, D> addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant) {
        defaultSeleniumProvider.addWebDriverConfigurationParticipant(webDriverConfigurationParticipant);
        return this;
    }

    @Override
    public SeleniumRule<W, D> addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        if ( defaultSeleniumProvider instanceof FirefoxProvider ) {
            ((FirefoxProvider)defaultSeleniumProvider).addFirefoxConfigurationParticipant(firefoxConfigurationParticipant);
        }
        return this;
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
     * @return explicit {@link WebDriver} wait
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
    public SeleniumRule<W, D> setElementScrollBehaviour(ElementScrollBehavior elementScrollBehaviour) {
        this.addWebDriverConfigurationParticipant(new ElementScrollBehaviourConfigurator(elementScrollBehaviour));
        return this;
    }

    public SeleniumRule<W, D> withLogFileThreshold(Level threshold) {
        this.logFile.setThreshold(threshold);
        return this;
    }

    public SeleniumRule<W, D> withImplicitWait(Duration implicitWait) {
        timeoutsConfigurationParticipant.withImplicitWait(implicitWait);
        return this;
    }

    public SeleniumRule<W, D> withScriptTimeout(Duration scriptTimeout) {
        timeoutsConfigurationParticipant.withScriptTimeout(scriptTimeout);
        return this;
    }

    public SeleniumRule<W, D> withPageLoadTimeout(Duration pageLoadTimeout) {
        timeoutsConfigurationParticipant.withPageLoadTimeout(pageLoadTimeout);
        return this;
    }

    public SeleniumRule<W, D> withoutImplicitWait() {
        timeoutsConfigurationParticipant.withoutImplicitWait();
        return this;
    }

    public SeleniumRule<W, D> withoutPageLoadTimeout() {
        timeoutsConfigurationParticipant.withoutPageLoadTimeout();
        return this;
    }

    public SeleniumRule<W, D> withoutScriptTimeout() {
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
