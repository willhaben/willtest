package at.willhaben.willtest.examples;

import at.willhaben.willtest.matcher.ExceptionMatcher;
import at.willhaben.willtest.misc.rule.SeleniumProviderFactory;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import at.willhaben.willtest.rule.Retry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.CoreMatchers.containsString;

public class RetryExample {
    private static int counter;
    private static String originalProvider;

    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule()
            //Important to bind the retry rule early in the rule chain so that other rules like SeleniumProvider will be
            // re-run in case of test failure
            .secondOuterRule(
                    new Retry(
                            new ExceptionMatcher<WebDriverException>(
                                    WebDriverException.class,
                                    containsString("Unable to bind to locking port")),
                            5));

    @BeforeClass
    public static void beforeClass() {
        originalProvider = System.getProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME);
        //setting name here because I do not want to set properties somewhere else.
        System.setProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME, MockedSeleniumProvider.class.getName());
    }

    @AfterClass
    public static void afterClass() {
        if ( originalProvider != null ) {
            System.setProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME, originalProvider);
        }
    }

    @Test
    public void testRetry() {
        counter++;
        if ( counter < 4 ) {
            throw new WebDriverException("Unable to bind to locking port 63333");
        }
        //this line will be reached, since the retry rule works
    }
}
