package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractWebDriverRule extends AbstractRule implements SeleniumProvider{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWebDriverRule.class);

    protected abstract void setWebDriver(WebDriver webDriver);

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
                LOGGER.warn("Error while closing browser. This error cannot be avoided somehow. " +
                        "This is not a big problem.", ex);
            }
            setWebDriver(null);
        }
    }
}
