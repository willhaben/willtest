package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.*;
import at.willhaben.willtest.misc.rule.SeleniumProviderFactory.ParameterObject;
import at.willhaben.willtest.rule.*;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.ElementScrollBehavior;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A sample default configuration we are using. Basically a composite made from the building blocks.<br/>
 * Some features, which are activated by default:
 * <ul>
 *     <li>15 seconds of implicit wait and script timeout. It can be disabled using {@link #withoutImplicitWait()} and
 *     {@link #withoutScriptTimeout()} methods</li>
 *     <li>In case of errors a screenshot and page source are saved automatically into surefire-reports
 *     folder. See {@link Screenshot} and {@link PageSource} for details</li>
 *     <li>Javascript errors are added as suppressed exception in case of test failures</li>
 *     <li>Based on {@link SeleniumProviderFactory#SELENIUM_PROVIDER_CLASS_NAME} system property different
 *     implementations {@link SeleniumProvider} can be loaded. Default is a local firefox.</li>
 *     <li>In case of test failure javascript alert message is added as suppressed expression, if any alert is present</li>
 *     <li>Supports file upload using {@link ResourceHelper}. Files can be in the file system, or in any jar inside
 *     the classpath</li>
 * </ul>
 * The rule can be easily still customized using {@link #addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant)},
 * {@link #withAdjustmentsOfFirefoxConfiguration(Consumer)}, {@link #secondOuterRule(TestRule)},
 * {@link #around(TestRule)} and other methods.
 */
public class SeleniumRule<P extends SeleniumProvider<P, D> & TestRule, D extends WebDriver> implements
        SeleniumProvider<SeleniumRule<P, D>, D>, TestRule {
    private static final Duration DEFAULT_IMPLICIT_WAIT = Duration.ofSeconds(15);
    private static final Duration DEFAULT_SCRIPT_TIMEOUT_REQUIRED_BY_NG_DRIVER = Duration.ofSeconds(15);

    private final TimeoutsConfigurationParticipant<D> timeoutsConfigurationParticipant =
            new TimeoutsConfigurationParticipant<D>()
                    .withImplicitWait(DEFAULT_IMPLICIT_WAIT)
                    .withScriptTimeout(DEFAULT_SCRIPT_TIMEOUT_REQUIRED_BY_NG_DRIVER);

    private final P defaultSeleniumProvider;

    private final ResourceHelper resourceHelper = new ResourceHelper();
    private final FirefoxConfiguration<D> firefoxConfiguration = new FirefoxConfiguration<>();
    private final LogContext logContext = new LogContext();

    private RuleChain ruleChain;

    /**
     * @param parameterObjects objects wich can be injected into {@link SeleniumProvider} implementations.
     *                         See {@link SeleniumProviderFactory}
     */
    public SeleniumRule(ParameterObject... parameterObjects) {
        ParameterObject[] allParameterObjects =
                ArrayUtils.addAll(parameterObjects, new ParameterObject(firefoxConfiguration.getClass(), firefoxConfiguration));

        defaultSeleniumProvider = SeleniumProviderFactory.createSeleniumProviderRule(allParameterObjects);
        defaultSeleniumProvider.addWebDriverConfigurationParticipant(new FileDetectorConfigurator<>());
        defaultSeleniumProvider.addWebDriverConfigurationParticipant(timeoutsConfigurationParticipant);

        DefaultFirefoxConfigurationParticipant<D> defaultFirefoxConfigurationParticipant =
                new DefaultFirefoxConfigurationParticipant<>();
        defaultSeleniumProvider.addWebDriverConfigurationParticipant(defaultFirefoxConfigurationParticipant);
        firefoxConfiguration.addFirefoxConfigurationParticipant(defaultFirefoxConfigurationParticipant);

        JavascriptError<P, D> javascriptErrorRule =
                new JavascriptError<>(defaultSeleniumProvider, false);
        firefoxConfiguration.addFirefoxConfigurationParticipant(javascriptErrorRule);
        WebDriverLog<P, D> webDriverLog = new WebDriverLog<>(defaultSeleniumProvider);
        PageSource pageSource = new PageSource(defaultSeleniumProvider);
        Screenshot screenshot = new Screenshot(defaultSeleniumProvider);
        JavascriptAlert javascriptAlert = new JavascriptAlert(defaultSeleniumProvider);

        ruleChain = RuleChain
                .outerRule(defaultSeleniumProvider)
                .around(webDriverLog)
                .around(pageSource)
                .around(screenshot)
                .around(javascriptAlert)
                .around(javascriptErrorRule)
                .around(resourceHelper);
    }

    @Override
    public D getWebDriver() {
        return defaultSeleniumProvider.getWebDriver();
    }

    @Override
    public SeleniumRule<P, D> addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant<D> webDriverConfigurationParticipant) {
        defaultSeleniumProvider.addWebDriverConfigurationParticipant(webDriverConfigurationParticipant);
        return this;
    }

    @Override
    public SeleniumRule<P, D> getThis() {
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return RuleChain
                .outerRule(logContext)
                .around(ruleChain)
                .apply(base,description);
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

    /**
     * Activates AdBlocker in firefox. See {@link AdBlockerConfigurator}
     * @return
     */
    public SeleniumRule<P, D> withAdBlocker() {
        firefoxConfiguration.addFirefoxConfigurationParticipant(new AdBlockerConfigurator());
        return this;
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
    public SeleniumRule<P, D> setElementScrollBehaviour(ElementScrollBehavior elementScrollBehaviour) {
        this.addWebDriverConfigurationParticipant(new ElementScrollBehaviourConfigurator<>(elementScrollBehaviour));
        return this;
    }

    /**
     * See
     * @param implicitWait
     * @return
     */
    public SeleniumRule<P, D> withImplicitWait(Duration implicitWait) {
        timeoutsConfigurationParticipant.withImplicitWait(implicitWait);
        return this;
    }

    public SeleniumRule<P, D> withScriptTimeout(Duration scriptTimeout) {
        timeoutsConfigurationParticipant.withScriptTimeout(scriptTimeout);
        return this;
    }

    public SeleniumRule<P, D> withPageLoadTimeout(Duration pageLoadTimeout) {
        timeoutsConfigurationParticipant.withPageLoadTimeout(pageLoadTimeout);
        return this;
    }

    public SeleniumRule<P, D> withoutImplicitWait() {
        timeoutsConfigurationParticipant.withoutImplicitWait();
        return this;
    }

    public SeleniumRule<P, D> withoutPageLoadTimeout() {
        timeoutsConfigurationParticipant.withoutPageLoadTimeout();
        return this;
    }

    public SeleniumRule<P, D> withoutScriptTimeout() {
        timeoutsConfigurationParticipant.withoutScriptTimeout();
        return this;
    }

    public Optional<Duration> getImplicitWait() {
        return timeoutsConfigurationParticipant.getImplicitWait();
    }

    public Optional<Duration> getScriptTimeout() {
        return timeoutsConfigurationParticipant.getScriptTimeout();
    }

    public Optional<Duration> getPageLoadTimeout() {
        return timeoutsConfigurationParticipant.getPageLoadTimeout();
    }

    public FirefoxConfiguration<D> getFirefoxConfiguration() {
        return firefoxConfiguration;
    }

    /**
     * You can add {@link FirefoxConfigurationParticipant} instances using a {@link Consumer} implementation, which
     * will get the {@link FirefoxConfiguration} as parameter
     * @param adjustment
     * @return
     */
    public SeleniumRule<P, D> withAdjustmentsOfFirefoxConfiguration(Consumer<FirefoxConfiguration<D>> adjustment) {
        adjustment.accept(firefoxConfiguration);
        return this;
    }

    public SeleniumRule<P, D> withDefaultFirefoxSettings() {
        DefaultFirefoxConfigurationParticipant<D> defaultFirefoxConfigurationParticipant =
                new DefaultFirefoxConfigurationParticipant<>();
        defaultSeleniumProvider.addWebDriverConfigurationParticipant(defaultFirefoxConfigurationParticipant);
        firefoxConfiguration.addFirefoxConfigurationParticipant(defaultFirefoxConfigurationParticipant);
        return this;
    }

    /**
     * Adds a new rule as the second outer rule to the current chain. LogContext is always the outest rule.
     * @param testRule
     * @return
     */
    public SeleniumRule<P, D> secondOuterRule(TestRule testRule) {
        ruleChain = RuleChain.outerRule(testRule).around(ruleChain);
        return this;
    }

    /**
     * Adds a rule as inner rule into the current chain.
     * @param testRule
     * @return
     */
    public SeleniumRule<P, D> around(TestRule testRule) {
        ruleChain = ruleChain.around(testRule);
        return this;
    }

    public SeleniumRule<P, D> withFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        firefoxConfiguration.addFirefoxConfigurationParticipant(firefoxConfigurationParticipant);
        return this;
    }

    public SeleniumRule<P, D> withFirefoxBinaryProvider(FirefoxBinaryProvider firefoxBinaryProvider) {
        firefoxConfiguration.setFirefoxBinaryProvider(firefoxBinaryProvider);
        return this;
    }
}
