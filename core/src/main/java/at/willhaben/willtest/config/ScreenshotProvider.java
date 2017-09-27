package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;

import java.awt.image.BufferedImage;

/**
 * Allows to use a custom screenshot library or to implement own screenshot behaviour.
 */
public interface ScreenshotProvider {

    /**
     * This method is called if an error occurred in the test.
     * @param webDriver {@link WebDriver} to take the screenshot.
     * @return the image which is safed to the surefire-reports folder
     */
    BufferedImage takeScreenshot(WebDriver webDriver);
}
