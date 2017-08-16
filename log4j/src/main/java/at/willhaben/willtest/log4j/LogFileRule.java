package at.willhaben.willtest.log4j;

import at.willhaben.willtest.rule.TestFailureAwareRule;
import at.willhaben.willtest.rule.LogContext;
import at.willhaben.willtest.util.TestReportFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;
import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

import static at.willhaben.willtest.rule.LogContext.*;

/**
 * Saves thread (testcase) specific log entries into a file to help investigation.
 * Works only together with {@link LogContext}, since it is relying on its {@link MDC} values.
 * <p>
 * See also {@link LogContext} and {@link at.willhaben.willtest.rule.WebDriverLog}
 */
public class LogFileRule extends TestFailureAwareRule {
    private static final Logger LOGGER = Logger.getLogger(LogFileRule.class);
    private static final String PATTERN = "[%5p] %d{HH:mm:ss} {%X{" + THREAD_ID +
            "}} %m [%X{" + TEST_CLASS + "}.%X{" +
            TEST_METHOD + "}%X{" +
            TEST_DISPLAY_NAME + "}] at %c%n";

    private File tempFile;
    private Level threshold = Level.INFO;
    private boolean saveOnlyOnError = true;
    private boolean saveOnAssumptionViolation = false;
    private Appender appender;

    public LogFileRule() {
    }

    public LogFileRule(Level threshold, boolean saveOnlyOnError) {
        this.threshold = threshold;
        this.saveOnlyOnError = saveOnlyOnError;
    }

    public void setThreshold(Level threshold) {
        this.threshold = threshold;
    }

    public LogFileRule saveOnAssumptionViolation(boolean value) {
        this.saveOnAssumptionViolation = value;
        return this;
    }

    @Override
    public void before(Description description) throws Throwable {
        super.before(description);
        tempFile = File.createTempFile("selenium", ".log");
        appender = createAppender(tempFile);
        Logger.getRootLogger().addAppender(appender);
    }

    private Appender createAppender(File targetFile) throws IOException {
        PatternLayout patternLayout = new PatternLayout(PATTERN);
        FileAppender fileAppender = new FileAppender(patternLayout, targetFile.getAbsolutePath(), true, true, 8048);
        fileAppender.setThreshold(threshold);
        fileAppender.addFilter(new MDCFilter(THREAD_ID, MDC.get(THREAD_ID).toString()));
        return fileAppender;
    }

    @Override
    public void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        try {
            boolean isAssumptionViolation = false;
            if (testFailure != null) {
                if (AssumptionViolatedException.class.isAssignableFrom(testFailure.getClass())) {
                    isAssumptionViolation = true;
                    LOGGER.error("Test assumption was violated: ", testFailure);
                    testFailure = null;
                } else {
                    LOGGER.error("Test failed with error: ", testFailure);
                }
            }
            Logger.getRootLogger().removeAppender(appender);
            appender.close();
            appender = null;
            if (isAssumptionViolation && saveOnAssumptionViolation) {
                saveToFileWithPostfix(description, "_AV.log");
            } else if (!saveOnlyOnError || testFailure != null) {
                saveToFileWithPostfix(description, ".log");
            }
        } finally {
            if (!tempFile.delete()) {
                LOGGER.error("Could not delete temp file at " + tempFile.getAbsolutePath());
            }
            tempFile = null;
        }
    }

    private void saveToFileWithPostfix(Description description, String postfix) throws IOException {
        TestReportFile testReportFile = TestReportFile.forTest(description).withPostix(postfix).build();
        FileUtils.copyFile(tempFile, testReportFile.getFile());
    }
}
