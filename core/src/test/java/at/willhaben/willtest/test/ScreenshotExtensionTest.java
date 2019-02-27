package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.extensions.ScreenshotExtension;
import at.willhaben.willtest.util.Environment;
import at.willhaben.willtest.util.TestReportFile;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class ScreenshotExtensionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotExtensionTest.class);

    private ExtensionContext context;
    private TestWebdriver driver;
    private String methodName;

    @BeforeEach
    void setUp() throws Throwable {
        methodName = "testMethod" + System.nanoTime();
        driver = mock(TestWebdriver.class);
        byte[] image = Files.readAllBytes(Paths.get(ScreenshotExtension.class.getClassLoader().getResource("Google-Screenshot.png").toURI()));
        when(driver.getScreenshotAs(OutputType.BYTES))
                .thenReturn(image);
        context = mock(ExtensionContext.class);
        when(context.getRequiredTestClass())
                .thenReturn(((Class) ScreenshotExtension.class));
        when(context.getRequiredTestClass())
                .thenReturn(((Class) ScreenshotExtension.class));
        Method testMethod = mock(Method.class);
        when(testMethod.getName()).thenReturn(methodName);
        when(context.getRequiredTestMethod()).thenReturn(testMethod);
    }

    @Test
    void testCreateScreenshot() throws Throwable {
        ScreenshotExtension extension = new ScreenshotExtension();
        extension.createScreenshot(context, driver);
        assertThat(getScreenshotNames(), Matchers.hasItem(Matchers.containsString(methodName)));
    }

    @AfterEach
    void cleanUp() {
        Optional<String> createdScreenshot = getScreenshotNames().stream()
                .filter(fileName -> fileName.contains(methodName))
                .findFirst();
        if (createdScreenshot.isPresent()) {
            String pathToCreatedScreenshot = TestReportFile.getReportFolderDir() + File.separator + createdScreenshot.get();
            LOGGER.debug("Clean up test files. Delete file " + pathToCreatedScreenshot);
            new File(pathToCreatedScreenshot).delete();
        }
    }

    private List<String> getScreenshotNames() {
        String reportFolderDir = TestReportFile.getReportFolderDir();
        return Arrays.asList(new File(reportFolderDir).list());
    }

    public abstract class TestWebdriver implements WebDriver, TakesScreenshot {}
}
