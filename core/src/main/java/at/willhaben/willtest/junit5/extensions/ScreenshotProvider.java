package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.config.DefaultScreenshotGenerator;
import at.willhaben.willtest.junit5.ScreenshotInterceptor;
import at.willhaben.willtest.junit5.TestFailureListener;
import at.willhaben.willtest.util.TestReportFile;
import com.google.common.io.Files;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static at.willhaben.willtest.util.AnnotationHelper.getBrowserUtilExtensionList;
import static at.willhaben.willtest.util.ExceptionChecker.isAssumptionViolation;

public class ScreenshotProvider implements TestFailureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotProvider.class);
    public static final String ALLURE_FLAG = "allure";

    @Override
    public void onFailure(ExtensionContext context, WebDriver driver, Throwable throwable) throws Throwable {
        if (!isAssumptionViolation(throwable)) {
            createScreenshot(context, driver);
        }
    }

    public void createScreenshot(ExtensionContext context, WebDriver driver) throws Throwable {
        File screenshotFile;
        ScreenshotInterceptor screenshotInterceptor = getScreenshotInterceptor(context);
        BufferedImage screenShot = new AShot().shootingStrategy(screenshotInterceptor
                .provideShootingStrategy())
                .takeScreenshot(driver).getImage();
        screenshotFile = TestReportFile.forTest(context).withPostix(".png").build().getFile();
        LOGGER.info("Saved screenshot of failed test " +
                context.getRequiredTestClass().getSimpleName() + "." +
                context.getRequiredTestMethod().getName() + " to " + screenshotFile.getAbsolutePath());
        ImageIO.write(screenShot, "png", screenshotFile);
        String allureFlag = System.getProperty(ALLURE_FLAG);
        if (allureFlag != null && allureFlag.equals("true")) {
                Allure.addAttachment(context.getRequiredTestMethod().getName(), Files.asByteSource(screenshotFile).openStream());
        }

    }

    private ScreenshotInterceptor getScreenshotInterceptor(ExtensionContext context) {
        List<ScreenshotInterceptor> screenshotInterceptors =
                getBrowserUtilExtensionList(context, ScreenshotInterceptor.class, true);
        if (screenshotInterceptors.isEmpty()) {
            return defaultScreenshotInterceptor();
        } else {
            return screenshotInterceptors.get(screenshotInterceptors.size() - 1);
        }
    }

    private ScreenshotInterceptor defaultScreenshotInterceptor() {
        return () -> new ShootingStrategy() {
            @Override
            public BufferedImage getScreenshot(WebDriver webDriver) {
                return new DefaultScreenshotGenerator().takeScreenshot(webDriver);
            }
        };
    }
}
