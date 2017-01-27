package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.WebDriverProvider;
import at.willhaben.willtest.util.TestReportFile;
import com.google.common.io.Files;
import org.apache.log4j.Logger;
import org.junit.runner.Description;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Saves the page source into a file in case of a test error
 * <p>
 * Created by liptak on 2016.08.24..
 */
public class PageSource extends AbstractRule {
    private static final Logger LOGGER = Logger.getLogger(PageSource.class);

    private final WebDriverProvider webDriverProvider;

    public PageSource(WebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        String pageSource = webDriverProvider.getWebDriver().getPageSource();
        File destFile = TestReportFile.forTest(description).withPostix(".html").build().getFile();
        Files.write(pageSource, destFile, StandardCharsets.UTF_8);
        LOGGER.info("Saved page source as " + destFile.getAbsolutePath());
    }
}
