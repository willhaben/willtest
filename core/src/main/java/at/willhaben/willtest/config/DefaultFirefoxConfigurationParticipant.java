package at.willhaben.willtest.config;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Default settings for Firefox. Features:
 * <ul>
 * <li>Geo testing enabled</li>
 * <li>Enables javascript</li>
 * <li>Enables using a different display, for instance a virtual frame buffer</li>
 * <li>Moves the opened window to the left screen maximized with Full HD resolution.</li>
 * </ul>
 */
@Deprecated
public class DefaultFirefoxConfigurationParticipant<D extends WebDriver>
        implements FirefoxConfigurationParticipant, WebDriverConfigurationParticipant<D> {
    /**
     * Env entry, which can contain alternative display. For example ":99"
     */
    private static final String DISPLAY_SYSTEM_PROPERTY_KEY = "display";

    @Override
    public void adjustFirefoxBinary(FirefoxBinary firefoxBinary) {
        setDisplay(firefoxBinary);
    }

    @Override
    public void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
        firefoxProfile.setPreference("geo.prompt.testing", true);
        firefoxProfile.setPreference("geo.prompt.testing.allow", true);
        //disable multi-process tabs -> avoids crashing of tabs with the message "Gah. Your tab just crashed!!!".
        firefoxProfile.setPreference("browser.tabs.remote.autostart.2", false);
        firefoxProfile.setPreference("browser.tabs.remote.autostart", false);
    }

    @Override
    public void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability("applicationCacheEnabled", false);
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setBrowserName("firefox");
    }

    /**
     * Moves window to the first display, and maximizes there.
     * This is practical in case of local testing.
     *
     * @see WebDriverConfigurationParticipant#postConstruct(WebDriver)
     */
    @Override
    public void postConstruct(D webDriver) {
        Window window = webDriver.manage().window();
        Dimension dimension = new Dimension(1920, 1080);
        Point thisPointIsAlwaysOnFirstDisplay = new Point(0, 0);
        window.setPosition(thisPointIsAlwaysOnFirstDisplay);
        window.setSize(dimension);
        window.maximize();
    }

    private void setDisplay(FirefoxBinary result) {
        String display = System.getProperty(DISPLAY_SYSTEM_PROPERTY_KEY);
        if (display != null) {
            result.setEnvironmentProperty("DISPLAY", display);
        }
    }
}
