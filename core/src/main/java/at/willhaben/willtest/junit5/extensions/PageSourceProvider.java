package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.junit5.FailureListener;
import at.willhaben.willtest.util.TestReportFile;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PageSourceProvider implements FailureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageSourceProvider.class);

    @Override
    public void onFailure(ExtensionContext context, WebDriver driver, Throwable throwable) throws Throwable {
        String pageSource = driver.getPageSource();
        TestReportFile testReportFile = TestReportFile.forTest(context).withPostix(".html").build();
        File pageSourceFile = testReportFile.getFile();
        Files.write(pageSourceFile.toPath(), pageSource.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("Saved page source of failed test " +
                context.getRequiredTestClass().getSimpleName() + "." +
                context.getRequiredTestMethod().getName() + " to " + pageSourceFile.getAbsolutePath());
    }
}
