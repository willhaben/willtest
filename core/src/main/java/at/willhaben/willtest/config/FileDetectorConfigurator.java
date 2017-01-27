package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Created by liptak on 2016.11.21..
 */
public enum FileDetectorConfigurator implements WebDriverConfigurationParticipant {
    INSTANCE;
    private static final String ERROR_THROWN_BY_FIREFOX_LOCAL_DRIVER_WHICH_MEANS_NO_FILEDETECTOR_IS_NEEDED =
            "Setting the file detector only works on remote webdriver instances obtained via RemoteWebDriver";

    public static <T extends WebDriverProvider> T supportingFileUpload(T webDriverProvider) {
        webDriverProvider.addWebDriverConfigurationParticipant(INSTANCE);
        return webDriverProvider;
    }

    @Override
    public void postConstruct(WebDriver webDriver) {
        if (webDriver instanceof RemoteWebDriver) {
            try {
                ((RemoteWebDriver) webDriver).setFileDetector(new LocalFileDetector());
            } catch (WebDriverException e) {
                String message = e.getMessage();
                if (message != null &&
                        !message.contains(ERROR_THROWN_BY_FIREFOX_LOCAL_DRIVER_WHICH_MEANS_NO_FILEDETECTOR_IS_NEEDED)) {
                    throw e;
                }
            }
        }
    }
}
