package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.extensions.PageSourceExtension;
import at.willhaben.willtest.test.mock.ExtensionMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static at.willhaben.willtest.util.TestReportFile.getReportFolderDir;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageSourceExtensionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageSourceExtensionTest.class);
    private static final String PAGESOURCE = "This is the Pagesource!";

    private ExtensionContext context;
    private WebDriver driver;
    private String testMethodName;

    @BeforeEach
    void setUp() {
        testMethodName = "testPageSourceMethod" + System.nanoTime();
        context = ExtensionMock.mockWithTestClassAndMethod(PageSourceExtensionTest.class, testMethodName);
        driver = Mockito.mock(WebDriver.class);
        Mockito.when(driver.getPageSource()).thenReturn(PAGESOURCE);
    }

    @Test
    void testPagesourceFileCreation() throws Throwable {
        new PageSourceExtension().onFailure(context, driver, new AssertionError("Test failure"));
        File reportDirectory = new File(getReportFolderDir());
        assertTrue(reportDirectory.exists());
        assertTrue(reportDirectory.isDirectory());

        assertThat(asList(reportDirectory.list()), Matchers.hasItem(Matchers.containsString(testMethodName)));

        List<String> reportFileNames = Arrays.stream(reportDirectory.list())
                .filter(fileName -> fileName.contains(testMethodName))
                .collect(Collectors.toList());

        assertThat(reportFileNames, Matchers.hasSize(1));

        String fileContent = new String(Files.readAllBytes(
                Paths.get(getReportFolderDir(), reportFileNames.get(0))), StandardCharsets.UTF_8);
        assertThat(fileContent, is(PAGESOURCE));
    }

    @AfterEach
    void cleanUp() {
        String reportDirName = getReportFolderDir();
        File reportDir = new File(reportDirName);
        if (reportDir.exists() && reportDir.isDirectory()) {
            asList(reportDir.list()).stream()
                    .filter(fileName -> fileName.contains(testMethodName))
                    .forEach(fileName -> {
                        String generatedFile = reportDirName + File.separator + fileName;
                        LOGGER.info("Clean generated file [" + generatedFile + "]");
                        new File(generatedFile).delete();
                    });
        }
    }
}
