package at.willhaben.willtest.rule;

import at.willhaben.willtest.util.MDCFilter;
import at.willhaben.willtest.util.TestReportFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

import static at.willhaben.willtest.rule.LogContext.*;

/**
 * Saves thread (testcase) specific log file to help investigation.
 * Works only together with {@link LogContext}, since it is relying on its {@link MDC} values.
 *
 * Created by liptak on 2016.07.14..
 */
public class LogFile extends AbstractRule {
    private static final Logger LOGGER = Logger.getLogger(LogFile.class);
    private static final String PATTERN = "[%5p] %d{HH:mm:ss} {%X{" + THREAD_ID +
            "}} %m [%X{" + TEST_CLASS + "}.%X{" +
            TEST_METHOD + "}%X{" +
            TEST_DISPLAY_NAME + "}] at %c%n";

    private File tempFile;
    private Level threshold = Level.INFO;
    private boolean saveOnlyOnError = true;
    private Appender appender;

    public LogFile() {
    }

    public LogFile(Level threshold, boolean saveOnlyOnError) {
        this.threshold = threshold;
        this.saveOnlyOnError = saveOnlyOnError;
    }

    public void setThreshold(Level threshold) {
        this.threshold = threshold;
    }

    @Override
    public void before( Description description ) throws Throwable {
        super.before(description);
        tempFile = File.createTempFile( "selenium", ".log" );
        appender = createAppender( tempFile );
        Logger.getRootLogger().addAppender( appender );
    }

    private Appender createAppender(File targetFile) throws IOException {
        PatternLayout patternLayout = new PatternLayout(PATTERN);
        FileAppender fileAppender = new FileAppender(patternLayout, targetFile.getAbsolutePath(), true, true, 8048);
        fileAppender.setThreshold(threshold);
        fileAppender.addFilter( new MDCFilter( THREAD_ID, MDC.get(THREAD_ID).toString()));
        return fileAppender;
    }

    @Override
    public void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        try {
            if ( testFailure != null ) {
                LOGGER.error( "Test failed with error: ", testFailure );
            }
            Logger.getRootLogger().removeAppender(appender);
            appender.close();
            appender = null;
            if ( !saveOnlyOnError || testFailure != null ) {
                TestReportFile testReportFile = TestReportFile.forTest(description).withPostix(".log").build();
                FileUtils.copyFile(tempFile, testReportFile.getFile());
            }
        }
        finally{
            if ( !tempFile.delete() ) {
                LOGGER.error( "Could not delete temp file at " + tempFile.getAbsolutePath() );
            }
            tempFile = null;
        }
    }
}
