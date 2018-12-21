package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.config.DefaultScreenshotProvider;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.BrowserUtilExtension;
import at.willhaben.willtest.junit5.ScreenshotInterceptor;
import at.willhaben.willtest.util.TestReportFile;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class ScreenshotExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        WebDriver driver = DriverParameterResolver.getDriverFromStore(extensionContext);
        if (driver != null) {
            File screenshotAs;
            ScreenshotInterceptor screenshotInterceptor = getScreenshotInterceptor(extensionContext);
            BufferedImage screenShot = new AShot().shootingStrategy(screenshotInterceptor
                    .provideShootingStrategy())
                    .takeScreenshot(driver).getImage();
            screenshotAs = TestReportFile.forTest(extensionContext).withPostix(".png").build().getFile();
            ImageIO.write(screenShot, "png", screenshotAs);
        } else {
            throw new WebDriverException("Driver isn't initialized. " +
                    "This extension can only be used in combination with the DriverParameterResolver");
        }
        throw throwable;
    }

    private ScreenshotInterceptor getScreenshotInterceptor(ExtensionContext context) {
        BrowserUtil browserUtil = context.getRequiredTestMethod().getAnnotation(BrowserUtil.class);
        if (browserUtil == null) {
            browserUtil = context.getRequiredTestClass().getAnnotation(BrowserUtil.class);
        }
        if (browserUtil == null) {
            return defaultScreenshotInterceptor();
        }
        Optional<Class<? extends BrowserUtilExtension>> screenShotExtension = Arrays.stream(browserUtil.value())
                .filter(ScreenshotInterceptor.class::isAssignableFrom)
                .findFirst();

        if (screenShotExtension.isPresent()) {
            try {
                return (ScreenshotInterceptor) screenShotExtension.get().getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Can't instantiate ScreenshotExtension", e);
            }
        } else {
            return defaultScreenshotInterceptor();
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