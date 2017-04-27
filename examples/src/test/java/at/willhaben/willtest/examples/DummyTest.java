package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumProviderFactory;
import at.willhaben.willtest.misc.rule.SeleniumProviderFactory.ParameterObject;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DummyTest {
    private static final Pattern THIS_WILL_BE_INJECTED_INTO_DUMMY_SELENIUM_PROVIDER = Pattern.compile("fooooo");
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
    public final SeleniumRule seleniumRule = new SeleniumRule(
            new ParameterObject(Pattern.class,THIS_WILL_BE_INJECTED_INTO_DUMMY_SELENIUM_PROVIDER))
            .withoutImplicitWait()
            .withoutScriptTimeout();

    @Test
    public void testInjection() {
        assertThat(seleniumRule.getWebDriver().getCurrentUrl(),is("fooooo"));
    }
}
