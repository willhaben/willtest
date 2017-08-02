package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.util.TestReportFile;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ViewportPastingStrategy;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Creates a PNG screenshot if there is a test failure. Please note, that creating the screenshot is not instant
 * when a failure happens. It happens a few milliseconds after the failure, and it can show a different state.
 */
public class Screenshot extends TestFailureAwareRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(Screenshot.class);

    private final SeleniumProvider seleniumProvider;

    public Screenshot(SeleniumProvider seleniumProvider) {
        this.seleniumProvider = seleniumProvider;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        WebDriver webDriver = seleniumProvider.getWebDriver();
        if (webDriver instanceof TakesScreenshot) {
            WebElement body = webDriver.findElement(By.cssSelector(".page-wrap"));
            ru.yandex.qatools.ashot.Screenshot screenshot = new AShot()
                    .shootingStrategy(new ViewportPastingStrategy(1000, 79)).takeScreenshot(webDriver, body);

            //byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
            File destFile = TestReportFile.forTest(description).withPostix(".png").build().getFile();
            ImageIO.write(screenshot.getImage(), "PNG", destFile);

            //Files.write(screenshot, destFile);
            LOGGER.info("Saved screenshot as " + destFile.getAbsolutePath());
        } else {
            testFailure.addSuppressed(
                    new RuntimeException(
                            "Could not take screenshot, since webdriver is not a " +
                                    TakesScreenshot.class.getName() + " instance as expected. Actual class is " +
                                    webDriver.getClass().getName()));
        }
    }
}
