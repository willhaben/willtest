package at.willhaben.willtest.config;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Takes a simple screenshot in case of an error. Only the viewport of the browser is captured.
 */
public class DefaultScreenshotProvider implements ScreenshotProvider {

    @Override
    public BufferedImage takeScreenshot(WebDriver webDriver) {
        TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
        ByteArrayInputStream imageArrayStream = new ByteArrayInputStream(takesScreenshot.getScreenshotAs(OutputType.BYTES));
        try {
            return ImageIO.read(imageArrayStream);
        } catch (IOException e) {
            throw new RuntimeException("Can not parse screenshot to BufferedImage.", e);
        }
    }
}
