package at.willhaben.willtest.log4j;

import at.willhaben.willtest.rule.TestFailureAwareRule;
import at.willhaben.willtest.util.TestReportFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static at.willhaben.willtest.rule.LogContext.THREAD_ID;

/**
 * This implementation of {@link WebDriverEventListener} creates a separate log file and save some actions
 * of the test to it. Should help to determine the failure of a test. (Only created if the test fails)
 * <ul>
 *     <li>beforeClickOn: saves the current url</li>
 *     <li>afterClickOn: saves the identifier of the clicked element</li>
 *     <li>beforeFindBy: saves the identifier of the element</li>
 *     <li>afterChangeValueOf: saves the identifier of the input element and the entered value</li>
 *     <li>onError: saves the thrown exception</li>
 * </ul>
 */
public class SeleniumEventListener extends TestFailureAwareRule implements WebDriverEventListener{

    private static final String LOGGER_NAME = "selenium_event_logger";
    private static final String APPENDER_NAME = "selenium_event_appender";
    private static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);
    private static final String PATTERN = "[%5p] %d{HH:mm:ss} %m%n";

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
        LOGGER.info("Navigated to: " + s);
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
        LOGGER.info("Refresh page with url: " + webDriver.getCurrentUrl());
    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
        LOGGER.info("Current url: " + webDriver.getCurrentUrl());
    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
        LOGGER.info("Clicked on: " + getElementSelector(webElement));
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        if(Objects.nonNull(charSequences)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (CharSequence charSequence : charSequences) {
                stringBuilder.append("['").append(charSequence).append("']");
            }
            LOGGER.info("Input in: " + getElementSelector(webElement) + " --- Input text: " + stringBuilder.toString());
        } else {
            LOGGER.info("Input in: " + getElementSelector(webElement) + " --- Cleared field");
        }
    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    @Override
    public void beforeSwitchToWindow(String windowName, WebDriver driver) {

    }

    @Override
    public void afterSwitchToWindow(String windowName, WebDriver driver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {

    }

    private String getElementSelector(WebElement webElement) {
        String elementIdentifier = webElement.toString();
        String selector = elementIdentifier.substring(elementIdentifier.indexOf("->"));
        return selector;
    }

    @Override
    protected void before(Description description) throws Throwable {
        super.before(description);
        String className = description.getTestClass().getSimpleName();
        String methodName = description.getMethodName().replace('.', '_');
        tempFile = File.createTempFile("selenium_actions_" + className + "_" + methodName, ".log");
        Appender appender = createAppender(tempFile);
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.addAppender(appender);
        logger.setAdditivity(false);
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        try {
            LOGGER.error("\n\n\nTest failed with error: ", testFailure);
            TestReportFile testReportFile = TestReportFile.forTest(description).withPostix("_action.log").build();
            FileUtils.copyFile(tempFile, testReportFile.getFile());
        } finally {
            if (!tempFile.delete()) {
                LOGGER.error("Could not delete temp file at " + tempFile.getAbsolutePath());
            }
            tempFile = null;
            Logger.getLogger(LOGGER_NAME).removeAppender(APPENDER_NAME);
        }
    }

    private Appender createAppender(File file) throws IOException {
        PatternLayout patternLayout = new PatternLayout(PATTERN);
        FileAppender fileAppender = new FileAppender(patternLayout, file.getAbsolutePath(), true);
        fileAppender.setName(APPENDER_NAME);
        fileAppender.addFilter(new MDCFilter(THREAD_ID, MDC.get(THREAD_ID).toString()));
        return fileAppender;
    }
}
