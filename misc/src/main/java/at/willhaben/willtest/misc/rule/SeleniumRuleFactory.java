package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.browserstack.rule.BrowserstackSeleniumProvider;
import at.willhaben.willtest.rule.AbstractWebDriverRule;
import at.willhaben.willtest.rule.DefaultSeleniumProvider;
import at.willhaben.willtest.util.Environment;

/**
 * Created by weisgrmi on 31.01.2017.
 */
//TODO: more customization
public class SeleniumRuleFactory {
    private static final String DEFAULT_SELENIUM_ENV = "local";

    public static AbstractWebDriverRule create() {
        String seleniumEnvironment = Environment.getValue("selenEnv", DEFAULT_SELENIUM_ENV);
        switch (seleniumEnvironment) {
            case "local":
                return new DefaultSeleniumProvider(true);
            case "willHub":
                return new DefaultSeleniumProvider();
            case "brStack":
                return new BrowserstackSeleniumProvider();
            case "brStackLocal":
                return new BrowserstackSeleniumProvider(true);
            default:
                return new DefaultSeleniumProvider(true);
        }
    }
}
