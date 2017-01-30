package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Configures the {@link WebDriver} to support file uploads. If you use local browser, you will need an
 * {@link org.openqa.selenium.remote.UselessFileDetector}, otherwise a {@link LocalFileDetector}, which lets selenium to
 * uploads your file to a selenium hub for example. This class makes file upload work with both Firefox and Selenium Hub.
 */
public enum FileDetectorConfigurator implements WebDriverConfigurationParticipant {
    INSTANCE;
    private static final String NO_FILE_DETECTOR_NEEDED =
            "Setting the file detector only works on remote webdriver instances obtained via RemoteWebDriver";

    /**
     * Convinience method, which lets the caller to add this configurator using chained method calls.
     * @param seleniumProvider
     * @param <T>
     * @return the seleniumProvider parameter, which was passed into the method as parameter. Enables method chaining.
     */
    public static <T extends SeleniumProvider> T supportingFileUpload(T seleniumProvider) {
        seleniumProvider.addWebDriverConfigurationParticipant(INSTANCE);
        return seleniumProvider;
    }

    @Override
    public void postConstruct(WebDriver webDriver) {
        if (webDriver instanceof RemoteWebDriver) {
            try {
                ((RemoteWebDriver) webDriver).setFileDetector(new LocalFileDetector());
            } catch (WebDriverException e) {
                String message = e.getMessage();
                if (message != null &&
                        !message.contains(NO_FILE_DETECTOR_NEEDED)) {
                    throw e;
                }
            }
        }
    }
}
