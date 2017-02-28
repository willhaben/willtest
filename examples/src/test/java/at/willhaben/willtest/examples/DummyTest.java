package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumProviderFactory;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DummyTest {
    private static Pattern THIS_WILL_BE_INJECTED_INTO_DUMMY_SELENIUM_PROVIDER = Pattern.compile("fooooo");
    private static String originalProvider;

    @BeforeClass
    public static void beforeClass() {
        originalProvider = System.getProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME);
        //setting name here because I do not want to set properties somewhere else.
        System.setProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME, DummySeleniumProvider.class.getName());
    }

    @AfterClass
    public static void afterClass() {
        if ( originalProvider != null ) {
            System.setProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME, originalProvider);
        }
    }

    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule(
            new SeleniumProviderFactory.ParameterObject(Pattern.class,THIS_WILL_BE_INJECTED_INTO_DUMMY_SELENIUM_PROVIDER))
            .withoutImplicitWait()
            .withoutScriptTimeout();

    @Test
    public void testInjection() {
        assertThat(seleniumRule.getWebDriver().getCurrentUrl(),is("fooooo"));
    }
}
