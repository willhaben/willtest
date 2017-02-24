package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.misc.JavascriptErrorException;
import at.willhaben.willtest.rule.AbstractRule;
import at.willhaben.willtest.rule.FirefoxProvider;
import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;
import java.util.List;

public class JavascriptError<P extends FirefoxProvider<P,D>,D extends WebDriver>
        extends AbstractRule
        implements FirefoxConfigurationParticipant {
    private final P firefoxProvider;
    private boolean initialized;
    private final boolean throwExceptionForSuccessfulTests;

    public JavascriptError(P firefoxProvider, boolean throwExceptionForSuccessfulTests) {
        this.firefoxProvider = firefoxProvider;
        firefoxProvider.addFirefoxConfigurationParticipant(this);
        this.throwExceptionForSuccessfulTests = throwExceptionForSuccessfulTests;
    }

    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        try {
            if (initialized) {
                List<JavaScriptError> jsErrors = JavaScriptError.readErrors(firefoxProvider.getWebDriver());
                if (!jsErrors.isEmpty()) {
                    JavascriptErrorException javascriptErrorException = new JavascriptErrorException(
                            "Javascript errors are detected!",
                            jsErrors);
                    if (testFailure != null) {
                        testFailure.addSuppressed(javascriptErrorException);
                    } else {
                        if (throwExceptionForSuccessfulTests) {
                            throw javascriptErrorException;
                        }
                    }
                }
            }
        } finally {
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
