package at.willhaben.willtest.examples;

import at.willhaben.willtest.browserstack.rule.WillSeleniumTestScheduler;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.Platform;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by michael on 10.07.17.
 */
@RunWith(WillSeleniumTestScheduler.class)
public class BrowserstackExampleTest {

    @Parameterized.Parameters
    public static Collection<Object[]> asdf() {
        return Collections.emptyList();
    }

    @Rule
    public SeleniumRule rule = new SeleniumRule();

    @Test
    @Category(BrTest.class)
    public void testBrowserstack1() {
        rule.getWebDriver().get("https://www.google.at");
    }

    @Test
    @Category(BrTest.class)
    public void testBrowserstack2() {
        rule.getWebDriver().get("https://www.google.at");
    }

    @Test
    @Category(BrTest.class)
    public void testBrowserstack3() {
        rule.getWebDriver().get("https://www.google.at");
    }

}
