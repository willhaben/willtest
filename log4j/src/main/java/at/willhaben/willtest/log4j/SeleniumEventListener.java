package at.willhaben.willtest.log4j;

import at.willhaben.willtest.rule.TestFailureAwareRule;
import at.willhaben.willtest.util.TestReportFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by michael on 24.07.17.
 */
public class SeleniumEventListener extends TestFailureAwareRule implements WebDriverEventListener{

    private static final String APPENDER_NAME = "selenium_event_appender";
    private static final Logger LOGGER = Logger.getLogger(APPENDER_NAME);
    private static final String PATTERN = "[%5p] %d{HH:mm:ss} %m";

    private File tempFile;

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {

    }

    @Override
    public void afterNavigateTo(String s, WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
        LOGGER.info("Find following element: " + by + "\n");
    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
        LOGGER.info("Current url: " + webDriver.getCurrentUrl() + "\n");
    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
        LOGGER.info("Clicked on: " + webElement + "\n");
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        LOGGER.info("Input in: " + webElement + "\n");
    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {

    }

    @Override
    protected void before(Description description) throws Throwable {
        super.before(description);
        tempFile = File.createTempFile("selenium_actions", ".log");
        Appender appender = createAppender(tempFile);
        Logger.getRootLogger().addAppender(appender);
    }

    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        try {
            if (Objects.nonNull(testFailure)) {
                LOGGER.error("\n\n\nTest failed with error: ", testFailure);
                TestReportFile testReportFile = TestReportFile.forTest(description).withPostix("_action.log").build();
                FileUtils.copyFile(tempFile, testReportFile.getFile());
            }
        } finally {
            if (!tempFile.delete()) {
                LOGGER.error("Could not delete temp file at " + tempFile.getAbsolutePath());
            }
            tempFile = null;
            Logger.getRootLogger().removeAppender(APPENDER_NAME);
        }
    }

    private Appender createAppender(File file) throws IOException {
        PatternLayout patternLayout = new PatternLayout(PATTERN);
        FileAppender fileAppender = new FileAppender(patternLayout, tempFile.getAbsolutePath(), true);
        fileAppender.setName(APPENDER_NAME);
        return fileAppender;
    }
}
