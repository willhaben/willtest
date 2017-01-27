package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by liptak on 2016.08.24..
 */
public interface WebDriverProvider {
    long FIND_ELEMENT_TIMEOUT = 30;

    WebDriver getWebDriver();
    default WebDriverWait getDefaultWebDriverWait() {
        return new WebDriverWait(getWebDriver(), FIND_ELEMENT_TIMEOUT);
    }

    WebDriverProvider addWebDriverConfigurationParticipant(WebDriverConfigurationParticipant webDriverConfigurationParticipant);
    WebDriverProvider addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant);
}
