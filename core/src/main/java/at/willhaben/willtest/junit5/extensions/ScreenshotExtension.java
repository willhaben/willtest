package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.config.DefaultScreenshotProvider;
import at.willhaben.willtest.junit5.ScreenshotInterceptor;
import at.willhaben.willtest.util.TestReportFile;
import org.junit.AssumptionViolatedException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static at.willhaben.willtest.util.AnnotationHelper.getBrowserUtilExtensionList;

public class ScreenshotExtension implements TestExecutionExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotExtension.class);

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        WebDriver driver = DriverParameterResolver.getDriverFromStore(extensionContext);
        if (driver != null) {
            try {
                if (!isAssumptionViolation(throwable)) {
                    createScreenshot(extensionContext, driver);
                }
            } catch (Throwable th) {
                throwable.addSuppressed(th);
            }
        } else {
            throwable.addSuppressed(new WebDriverException("Driver isn't initialized. " +
                    "This extension can only be used in combination with the DriverParameterResolver"));
        }
        throw throwable;
    }

    public boolean isAssumptionViolation(Throwable throwable) {
        return AssumptionViolatedException.class.isAssignableFrom(throwable.getClass()) ||
                TestAbortedException.class.isAssignableFrom(throwable.getClass());
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
                return new DefaultScreenshotProvider().takeScreenshot(webDriver);
            }
        };
    }
}
