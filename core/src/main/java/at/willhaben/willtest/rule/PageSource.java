package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.util.TestReportFile;
import com.google.common.io.Files;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Saves the page source into a file in case of a test error
 */
public class PageSource extends TestFailureAwareRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageSource.class);

    private final SeleniumProvider seleniumProvider;

    public PageSource(SeleniumProvider seleniumProvider) {
        this.seleniumProvider = seleniumProvider;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        String pageSource = seleniumProvider.getWebDriver().getPageSource();
        PageContentException pageContentException = new PageContentException(pageSource);
        testFailure.addSuppressed(pageContentException);
        File destFile = TestReportFile.forTest(description).withPostix(".html").build().getFile();
        Files.write(pageSource.getBytes(StandardCharsets.UTF_8), destFile);
        LOGGER.info("Saved page source as " + destFile.getAbsolutePath());
    }
}
