package at.willhaben.misc.test.util;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import org.openqa.selenium.firefox.FirefoxOptions;

public class HeadlessBrowserConfig extends BrowserOptionInterceptor {

    @Override
    public FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = super.getFirefoxOptions();
        options.addArguments("-headless");
        return options;
    }
}
