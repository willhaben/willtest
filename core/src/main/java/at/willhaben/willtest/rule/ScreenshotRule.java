package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.util.TestReportFile;
import org.junit.runner.Description;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Objects;

/**
 * Creates a PNG screenshot if there is a test failure. Please note, that creating the screenshot is not instant
 * when a failure happens. It happens a few milliseconds after the failure, and it can show a different state.
 */
public class ScreenshotRule extends TestFailureAwareRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotRule.class);
    private final SeleniumProvider seleniumProvider;
    private ShootingStrategy shootingStrategy;

    public ScreenshotRule(SeleniumProvider seleniumProvider) {
        this.seleniumProvider = seleniumProvider;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        WebDriver webDriver = seleniumProvider.getWebDriver();
        if (webDriver instanceof TakesScreenshot) {
            AShot aShot = new AShot();
            if(Objects.nonNull(shootingStrategy)) {
                aShot.shootingStrategy(shootingStrategy);
            }
            Screenshot screenshot = aShot.takeScreenshot(webDriver);

            File destFile = TestReportFile.forTest(description).withPostix(".png").build().getFile();
            ImageIO.write(screenshot.getImage(), "PNG", destFile);

            LOGGER.info("Saved screenshot as " + destFile.getAbsolutePath());
        } else {
            testFailure.addSuppressed(
                    new RuntimeException(
                            "Could not take screenshot, since webdriver is not a " +
                                    TakesScreenshot.class.getName() + " instance as expected. Actual class is " +
                                    webDriver.getClass().getName()));
        }
    }

    public ScreenshotRule withShootingStrategy(ShootingStrategy shootingStrategy) {
        this.shootingStrategy = shootingStrategy;
        return this;
    }
}
