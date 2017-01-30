package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.WebDriverProvider;
import at.willhaben.willtest.util.TestReportFile;
import com.google.common.io.Files;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 * Created by liptak on 2016.08.24..
 */
public class Screenshot extends AbstractRule {
    private static final Logger LOGGER = Logger.getLogger(Screenshot.class);

    private final WebDriverProvider webDriverProvider;

    public Screenshot(WebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        WebDriver webDriver = webDriverProvider.getWebDriver();
        if (webDriver instanceof TakesScreenshot) {
            byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
            File destFile = TestReportFile.forTest(description).withPostix(".png").build().getFile();
            Files.write(screenshot, destFile);
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
