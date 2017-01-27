package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.WebDriverProvider;
import at.willhaben.willtest.misc.JavascriptErrorException;
import at.willhaben.willtest.rule.AbstractRule;
import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;
import org.junit.runner.Description;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;
import java.util.List;

/**
 * Created by liptak on 2016.09.26..
 */
public class JavascriptError extends AbstractRule implements FirefoxConfigurationParticipant {
    private final WebDriverProvider webDriverProvider;
    private boolean initialized;
    private final boolean throwExceptionForSuccessfulTests;

    public JavascriptError(WebDriverProvider webDriverProvider, boolean throwExceptionForSuccessfulTests ) {
        this.webDriverProvider = webDriverProvider;
        webDriverProvider.addFirefoxConfigurationParticipant(this);
        this.throwExceptionForSuccessfulTests = throwExceptionForSuccessfulTests;
    }

    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        try {
            if ( initialized ) {
                List<JavaScriptError> jsErrors = JavaScriptError.readErrors(webDriverProvider.getWebDriver());
                if (!jsErrors.isEmpty()) {
                    JavascriptErrorException javascriptErrorException = new JavascriptErrorException(
                            "Javascript errors are detected!",
                            jsErrors );
                    if ( testFailure != null ) {
                        testFailure.addSuppressed( javascriptErrorException );
                    }
                    else {
                        if ( throwExceptionForSuccessfulTests ) {
                            throw javascriptErrorException;
                        }
                    }
                }
            }
        }
        finally {
            initialized = false;
        }
    }

    @Override
    public void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
        try {
            JavaScriptError.addExtension(firefoxProfile);
            initialized = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
