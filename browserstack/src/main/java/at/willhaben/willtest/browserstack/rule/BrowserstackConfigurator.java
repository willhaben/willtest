package at.willhaben.willtest.browserstack.rule;

import at.willhaben.willtest.junit5.OptionModifier;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class BrowserstackConfigurator implements OptionModifier {

    private Map<String, Object> createOptionsDefault() {

    }

    @Override
    public FirefoxOptions modifyFirefoxOptions(FirefoxOptions options) {
        return null;
    }

    @Override
    public ChromeOptions modifyChromeOptions(ChromeOptions options) {
        return null;
    }

    @Override
    public EdgeOptions modifyEdgeOptions(EdgeOptions options) {
        return null;
    }

    @Override
    public InternetExplorerOptions modifyInternetExplorerOptions(InternetExplorerOptions options) {
        return null;
    }
}
