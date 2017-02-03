package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Combines the Rule pattern with the {@link SeleniumProvider} so in this class a webDriver is created
 * for a test. This class implements the default cleanup after a test with a created webDriver.
 */
public abstract class AbstractWebDriverRule extends AbstractRule implements SeleniumProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWebDriverRule.class);

    protected abstract void setWebDriver(WebDriver webDriver);

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
            setWebDriver(null);
        }
    }
}
