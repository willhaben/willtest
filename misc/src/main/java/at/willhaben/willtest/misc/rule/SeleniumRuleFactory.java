package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.browserstack.rule.BrowserstackSeleniumProvider;
import at.willhaben.willtest.rule.AbstractWebDriverRule;
import at.willhaben.willtest.rule.DefaultSeleniumProvider;
import at.willhaben.willtest.util.Environment;

/**
 * Creates {@link SeleniumRule} according to system property.
 */
//TODO: more customization
public class SeleniumRuleFactory {
    private static final String DEFAULT_SELENIUM_ENV = "local";

    /**
     * Creates {@link SeleniumRule} according to the system property [selenEnv].
     * Possible values:<br>
     * 'lcoal' - run tests on the local machine<br>
     * 'willhub' - run tests on selenium hub<br>
     * 'brStack' - run tests on Browserstack<br>
     * 'brStackLocal' - run tests on Browserstack tests local (more info <a href="https://www.browserstack.com/local-testing">Browserstack local testing</a>)<br>
     * @return rule according to system property
     */
    public static AbstractWebDriverRule create() {
        String seleniumEnvironment = Environment.getValue("selenEnv", DEFAULT_SELENIUM_ENV);
        switch (seleniumEnvironment) {
            case "local":
                return new DefaultSeleniumProvider().runLocal();
            case "willHub":
                return new DefaultSeleniumProvider();
            case "brStack":
                return new BrowserstackSeleniumProvider();
            case "brStackLocal":
                return new BrowserstackSeleniumProvider().runLocal();
            default:
                return new DefaultSeleniumProvider().runLocal();
        }
    }
}
