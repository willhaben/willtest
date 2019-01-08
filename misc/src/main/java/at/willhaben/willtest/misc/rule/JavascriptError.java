package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.misc.JavascriptErrorException;
import at.willhaben.willtest.rule.TestFailureAwareRule;
import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;
import java.util.List;

/**
 * A junit test rule, which adds the javascript errors fetched from the browser into the test failure as suppressed
 * exception. You can also use it to break your tests, if any javascript error happens. To do so, pass true into the
 * boolean parameter of the constructor.
 * <p>
 * Uses https://github.com/mguillem/JSErrorCollector and works only with local or remote firefox.
 * <p>
 * See {@link at.willhaben.willtest.config.FirefoxConfiguration}
 * @param <P> {@link SeleniumProvider} implementation
 * @param <D> {@link WebDriver} implementation
 */
@Deprecated
public class JavascriptError<P extends SeleniumProvider<P, D>, D extends WebDriver>
        extends TestFailureAwareRule
        implements FirefoxConfigurationParticipant {
    private final P seleniumProvider;
    private boolean initialized;
    private final boolean throwExceptionForSuccessfulTests;

    /**
     * @param seleniumProvider the {@link SeleniumProvider} to be attached to
     * @param throwExceptionForSuccessfulTests true if you want to break even successful tests if there is a javascript
     *                                         error, false otherwise. False is default.
     */
    public JavascriptError(P seleniumProvider, boolean throwExceptionForSuccessfulTests) {
        this.seleniumProvider = seleniumProvider;
        this.throwExceptionForSuccessfulTests = throwExceptionForSuccessfulTests;
    }

    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        try {
            if (initialized) {
                List<JavaScriptError> jsErrors = JavaScriptError.readErrors(seleniumProvider.getWebDriver());
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
