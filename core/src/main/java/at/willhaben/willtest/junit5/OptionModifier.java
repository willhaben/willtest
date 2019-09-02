package at.willhaben.willtest.junit5;

import at.willhaben.willtest.util.AndroidOptions;
import at.willhaben.willtest.util.IOsOptions;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface OptionModifier extends BrowserUtilExtension {

    default FirefoxOptions modifyFirefoxOptions(FirefoxOptions options) {
        return options;
    }

    default ChromeOptions modifyChromeOptions(ChromeOptions options) {
        return options;
    }

    default EdgeOptions modifyEdgeOptions(EdgeOptions options) {
        return options;
    }

    default InternetExplorerOptions modifyInternetExplorerOptions(InternetExplorerOptions options) {
        return options;
    }

    default AndroidOptions modifyAndroidOptions(AndroidOptions options) {
        return options;
    }

    default IOsOptions modifyIOsOptions(IOsOptions options) {
        return options;
    }
}
