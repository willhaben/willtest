package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.extensions.ScreenshotProvider;
import at.willhaben.willtest.util.TestReportFile;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static at.willhaben.willtest.test.mock.ExtensionMock.mockWithTestClassAndMethod;
import static at.willhaben.willtest.util.ExceptionChecker.isAssumptionViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class ScreenshotProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotProviderTest.class);

    private ExtensionContext context;
    private TestWebdriver driver;
    private String methodName;

    @BeforeEach
    void setUp() throws Throwable {
        methodName = "testMethod" + System.nanoTime();
        driver = mock(TestWebdriver.class);
        context = mockWithTestClassAndMethod(ScreenshotProviderTest.class, methodName);

        Path testImagePath = Paths.get(ScreenshotProvider.class.getClassLoader().getResource("test-image.png").toURI());
        byte[] image = Files.readAllBytes(testImagePath);

        doReturn(image).when(driver).getScreenshotAs(any());
    }

    @Test
    void testCreateScreenshot() throws Throwable {
        ScreenshotProvider extension = new ScreenshotProvider();
        extension.createScreenshot(context, driver);
        assertThat(getScreenshotNames(), Matchers.hasItem(Matchers.containsString(methodName)));
    }

    @Test
    void testThrowTestAbortException() {
        TestAbortedException assumption = new TestAbortedException("This is just an assumption!!!");
        assertThat(isAssumptionViolation(assumption), is(true));

        RuntimeException runtimeException = new RuntimeException("No assumption!!!");
        assertThat(isAssumptionViolation(runtimeException), is(false));
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
        File file = new File(reportFolderDir);
        if (file.exists() && file.isDirectory()) {
            return Arrays.asList(file.list());
        } else {
            return Collections.emptyList();
        }
    }

    public abstract class TestWebdriver implements WebDriver, TakesScreenshot {}
}
