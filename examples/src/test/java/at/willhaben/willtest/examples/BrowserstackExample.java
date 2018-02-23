package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * How to use the BrowserstackSeleniumProvider
 * You have to set the following vm-properties to run the test on Browserstack.
 */
public class BrowserstackExample {
    static {
        System.setProperty("seleniumProvider", "at.willhaben.willtest.browserstack.rule.BrowserstackSeleniumProvider");
        System.setProperty("browserstack.hub", "https://<USERNAME>:<PASSWORD>@hub-cloud.browserstack.com/wd/hub");
        System.setProperty("browserstack.local", "true");
        System.setProperty("platforms", "OS X");
        System.setProperty("platform.versions", "Sierra");
        System.setProperty("browsers", "Chrome");
        System.setProperty("browser.versions", "62.0");
        System.setProperty("display.resolution", "1920x1080");
    }

    @Rule
    public SeleniumRule rule = new SeleniumRule();

    @Test
    public void testBrowserstack() {
        rule.getWebDriver().navigate().to("https://www.google.com");
    }
}
