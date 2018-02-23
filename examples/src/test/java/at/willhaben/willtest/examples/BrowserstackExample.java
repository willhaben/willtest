package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.Rule;
import org.junit.Test;

public class BrowserstackExample {

    static {
        System.setProperty("browserstack.hub", "");
        System.setProperty("browserstack.local", "");
        System.setProperty("platforms", "");
        System.setProperty("platform.versions", "");
        System.setProperty("browsers", "");
        System.setProperty("browser.versions", "");
        System.setProperty("display.resolution", "");
    }

    @Rule
    public SeleniumRule rule = new SeleniumRule();

    @Test
    public void testBrowserstack() {
        rule.getWebDriver().navigate().to("https://www.google.com");
    }
}
