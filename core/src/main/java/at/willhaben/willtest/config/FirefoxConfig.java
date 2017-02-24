package at.willhaben.willtest.config;

import at.willhaben.willtest.rule.FirefoxProvider;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * Features:
 * <ul>
 * <li>default settings of firefox profile</li>
 * <li>Enables using a different display, for instance a virtual frame buffer</li>
 * </ul>
 */
public class FirefoxConfig<D extends WebDriver>
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
        firefoxProfile.setEnableNativeEvents(true);
    }

    /**
     * Moves window to the first display, and maximizes there. This is practical in case of local testing.
     *
     * @see WebDriverConfigurationParticipant#postConstruct(WebDriver)
     */
    @Override
    public void postConstruct(D webDriver) {
        Window window = webDriver.manage().window();
        //Requirement for Jobs, Resolution greater than 1280 in width
        Dimension dimension = new Dimension(1920, 1080);
        Point thisPointIsAlwaysOnFirstDisplay = new Point(0, 0);
        window.setPosition(thisPointIsAlwaysOnFirstDisplay);
        window.setSize(dimension);
        window.maximize();

    }

    public static <T extends FirefoxProvider<T,D>,D extends WebDriver> T addTo(T firefoxProvider) {
        FirefoxConfig<D> config = new FirefoxConfig<D>();
        firefoxProvider.addFirefoxConfigurationParticipant(config);
        firefoxProvider.addWebDriverConfigurationParticipant(config);
        return firefoxProvider;
    }

    private void setDisplay(FirefoxBinary result) {
        String display = System.getProperty(DISPLAY_SYSTEM_PROPERTY_KEY);
        if (display != null) {
            result.setEnvironmentProperty("DISPLAY", display);
        }
    }
}
