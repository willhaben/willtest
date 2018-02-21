package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.Rule;
import org.junit.Test;

public class BrowserstackExample {

    @Rule
    public SeleniumRule rule = new SeleniumRule();

    @Test
    public void testBrowserstack() {
        rule.getWebDriver().navigate().to("https://www.google.com");
    }
}
