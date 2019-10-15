package at.willhaben.misc.test.util;

import at.willhaben.willtest.junit5.OptionModifier;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions;

public class HeadlessBrowserConfig implements OptionModifier {

    @Override
    public FirefoxOptions modifyFirefoxOptions(FirefoxOptions options) {
        FirefoxBinary binary = options.getBinary();
        binary.addCommandLineOptions("--headless");
        options.setBinary(binary);
        return options;
    }
}
