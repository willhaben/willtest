package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BrowserSetup extends BrowserOptionInterceptor {
    @Override
    public ChromeOptions getChromeOptions() {
        ChromeOptions options = super.getChromeOptions();
        options.merge(getDesiredCapabilities("chrome"));
        options.addArguments("--start-maximized");
        return options;
    }

    public DesiredCapabilities getDesiredCapabilities(String nameOfBrowser) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("applicationCacheEnabled", false);
        capabilities.setJavascriptEnabled(true);
        capabilities.setBrowserName(nameOfBrowser);
        return capabilities;
    }
}
