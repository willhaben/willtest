package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.config.DefaultScreenshotProvider;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.BrowserUtilExtension;
import at.willhaben.willtest.junit5.ScreenshotInterceptor;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class ScreenshotExtension implements TestExecutionExceptionHandler {

    private static final String TEST_REPORT_FOLDER = "surefire-reports";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss.SSS");

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
        WebDriver driver = (WebDriver) store.get(DriverParameterResolver.DRIVER_KEY);
        if (driver != null) {
            File screenshotAs;
            ScreenshotInterceptor screenshotInterceptor = getScreenshotInterceptor(extensionContext);
            BufferedImage screenShot = new AShot().shootingStrategy(screenshotInterceptor
                    .provideShootingStrategy())
                    .takeScreenshot(driver).getImage();
            new File(TEST_REPORT_FOLDER).mkdir();
            try {
                String testClassName = extensionContext.getRequiredTestClass().getSimpleName();
                String methodName = extensionContext.getRequiredTestMethod().getName();
                String screenShotName = "TR_" + testClassName + "_" + methodName + "-" +
                        DATE_FORMAT.format(ZonedDateTime.now()) + ".png";
                String finalFileName = TEST_REPORT_FOLDER + File.separator + screenShotName;
                screenshotAs = new File(finalFileName);
                ImageIO.write(screenShot,"png",screenshotAs);
                Files.move(screenshotAs.toPath(), new File(finalFileName).toPath());
            } catch (IOException e) {
                throw new FileNotFoundException("Error while moving screenshot to '" + TEST_REPORT_FOLDER + "' folder.");
            }
        } else {
            throw new WebDriverException("Driver isn't initialized");
        }
        throw throwable;
    }

    private ScreenshotInterceptor getScreenshotInterceptor(ExtensionContext context) {
        BrowserUtil browserUtil = context.getRequiredTestMethod().getAnnotation(BrowserUtil.class);
        if(browserUtil == null) {
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
