package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Combines the Rule pattern with the {@link SeleniumProvider}, so in this class a webDriver is created
 * for a test. This class implements the default cleanup after a test with a created webDriver. Registers and calls
 * {@link WebDriverConfigurationParticipant} instances, which can influence the creation of the {@link WebDriver}.
 */
public abstract class AbstractSeleniumProvider<P extends SeleniumProvider<P, D>, D extends WebDriver>
        extends AbstractRule
        implements SeleniumProvider<P, D> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeleniumProvider.class);
    private final List<WebDriverConfigurationParticipant<D>> webDriverConfigurationParticipantList = new ArrayList<>();
    private D webDriver;


    @Override
    public D getWebDriver() {
        if (webDriver == null) {
            throw new RuntimeException("WebDriver has not been created. Did you use this class as a junit rule? " +
                    "Can it be, that the before method has not been called?");
        }
        return webDriver;
    }

    @Override
    public P addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant<D> webDriverConfigurationParticipant) {
        Objects.requireNonNull(webDriverConfigurationParticipant);
        this.webDriverConfigurationParticipantList.add(webDriverConfigurationParticipant);
        return getThis();
    }

    @Override
    protected void before(Description description) throws Throwable {
        super.before(description);
        webDriver = callPostConstruct(constructWebDriver(getDesiredCapabilities(description)));
    }

    /**
     * Closes the webDriver which was created in this rule. Takes care if there is an exception while
     * closing the webDriver.
     */
    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        if (getWebDriver() != null) {
            try {
                getWebDriver().quit();
            } catch (Exception ex) {
                if (!ex.getMessage().contains("It may have died")) {
                    throw ex;
                }
                LOGGER.info("Error while closing browser. This error cannot be avoided somehow. " +
                        "This is not a big problem.", ex);
            }
            webDriver = null;
        }
    }

    protected abstract D constructWebDriver(DesiredCapabilities desiredCapabilities);

    protected DesiredCapabilities createDesiredCapabilities(Description description) {
        return new DesiredCapabilities();
    }

    private D callPostConstruct(D webDriverToBeChangedAfterConstruction) {
        webDriverConfigurationParticipantList
                .forEach(participant -> participant.postConstruct(webDriverToBeChangedAfterConstruction));
        return webDriverToBeChangedAfterConstruction;
    }

    private DesiredCapabilities getDesiredCapabilities(Description description) {
        DesiredCapabilities desiredCapabilities = createDesiredCapabilities(description);
        webDriverConfigurationParticipantList
                .forEach(participant -> participant.addDesiredCapabilities(desiredCapabilities));
        return desiredCapabilities;
    }
}
