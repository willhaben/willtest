package at.willhaben.willtest.rule;


import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import at.willhaben.willtest.config.SeleniumProvider;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

/**
 * In case of test error dumps the logs of selenium into the standard logger
 * <p>
 * Created by liptak on 2016.08.24..
 */
public class WebDriverLog extends AbstractRule implements WebDriverConfigurationParticipant {
    private static final Logger LOGGER = Logger.getLogger(WebDriverLog.class);
    private static final ThreadLocal<SimpleDateFormat> LOG_TIMESTAMP_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("HH.mm.ss.SSS"));

    private static final Map<String, Level> WEBDRIVER_LOG_LEVELS = ImmutableMap
            .<String, Level>builder()
            .put(LogType.BROWSER, Level.INFO)
            .put(LogType.CLIENT, Level.INFO)
            .put(LogType.DRIVER, Level.INFO)
            .put(LogType.PERFORMANCE, Level.INFO)
            .put(LogType.PROFILER, Level.INFO)
            .put(LogType.SERVER, Level.INFO)
            .build();

    private static final LoggingPreferences DEFAULT_LOGGING_PREFERENCES = createLoggingPreferences();


    private final SeleniumProvider seleniumProvider;

    public WebDriverLog(SeleniumProvider seleniumProvider) {
        this.seleniumProvider = seleniumProvider;
        this.seleniumProvider.addWebDriverConfigurationParticipant(this);
    }

    private static LoggingPreferences createLoggingPreferences() {
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        WEBDRIVER_LOG_LEVELS.forEach(loggingPreferences::enable);
        return loggingPreferences;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) {
        WebDriver webDriver = seleniumProvider.getWebDriver();
        processWebDriverLogs(webDriver);
    }

    private void processWebDriverLogs(WebDriver webDriver) {
        SimpleDateFormat dateFormat = LOG_TIMESTAMP_FORMAT.get();
        Logs logs = webDriver.manage().logs();
        for (String logType : WEBDRIVER_LOG_LEVELS.keySet()) {
            LOGGER.info("Dumping webdriver log for log type " + logType);
            LogEntries logEntries = logs.get(logType);
            for (LogEntry logEntry : logEntries) {
                String renderedMessage = "[" + logType + "] " +
                        dateFormat.format(new Date(logEntry.getTimestamp())) + " " + logEntry.getMessage();
                Level logEntryLevel = logEntry.getLevel();
                if (logEntryLevel.equals(Level.FINE)) {
                    LOGGER.debug(renderedMessage);
                } else if (logEntryLevel.equals(Level.INFO)) {
                    LOGGER.info(renderedMessage);
                } else if (logEntryLevel.equals(Level.WARNING)) {
                    LOGGER.warn(renderedMessage);
                } else if (logEntryLevel.equals(Level.SEVERE)) {
                    LOGGER.error(renderedMessage);
                } else if (logEntryLevel.equals(Level.FINER)) {
                    LOGGER.trace(renderedMessage);
                }
            }
        }
    }

    @Override
    public void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, DEFAULT_LOGGING_PREFERENCES);
    }
}
