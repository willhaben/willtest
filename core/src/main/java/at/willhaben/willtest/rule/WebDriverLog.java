package at.willhaben.willtest.rule;


import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import com.google.common.collect.ImmutableMap;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Level;

/**
 * In case of test error dumps the logs of selenium into the standard logger
 * <p>
 */
public class WebDriverLog<P extends SeleniumProvider<P,D>,D extends WebDriver>
        extends AbstractRule
        implements WebDriverConfigurationParticipant<D> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverLog.class);
    private static final DateTimeFormatter LOG_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("HH.mm.ss.SSS");

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
    private static final String MESSAGE_PATTERN = "[{}] {} {}";

    private final SeleniumProvider<P,D> seleniumProvider;

    public WebDriverLog(SeleniumProvider<P,D> seleniumProvider) {
        this.seleniumProvider = seleniumProvider;
        this.seleniumProvider.addWebDriverConfigurationParticipant(this);
    }

    @Override
    protected void onError(Description description, Throwable testFailure) {
        WebDriver webDriver = seleniumProvider.getWebDriver();
        processWebDriverLogs(webDriver);
    }

    @Override
    public void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, DEFAULT_LOGGING_PREFERENCES);
    }

    private void processWebDriverLogs(WebDriver webDriver) {
        Logs logs = webDriver.manage().logs();
        for (String logType : WEBDRIVER_LOG_LEVELS.keySet()) {
            LOGGER.info("Dumping webdriver log for log type " + logType);
            LogEntries logEntries = logs.get(logType);
            for (LogEntry logEntry : logEntries) {
                String formattedOriginalTimestamp = LOG_TIMESTAMP_FORMAT
                        .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(logEntry.getTimestamp()), ZoneOffset.UTC));
                Level logEntryLevel = logEntry.getLevel();
                if (logEntryLevel.equals(Level.FINE)) {
                    LOGGER.debug(MESSAGE_PATTERN, logType, formattedOriginalTimestamp, logEntry.getMessage());
                } else if (logEntryLevel.equals(Level.INFO)) {
                    LOGGER.info(MESSAGE_PATTERN, logType, formattedOriginalTimestamp, logEntry.getMessage());
                } else if (logEntryLevel.equals(Level.WARNING)) {
                    LOGGER.warn(MESSAGE_PATTERN, logType, formattedOriginalTimestamp, logEntry.getMessage());
                } else if (logEntryLevel.equals(Level.SEVERE)) {
                    LOGGER.error(MESSAGE_PATTERN, logType, formattedOriginalTimestamp, logEntry.getMessage());
                } else if (logEntryLevel.equals(Level.FINER)) {
                    LOGGER.trace(MESSAGE_PATTERN, logType, formattedOriginalTimestamp, logEntry.getMessage());
                }
            }
        }
    }

    private static LoggingPreferences createLoggingPreferences() {
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        WEBDRIVER_LOG_LEVELS.forEach(loggingPreferences::enable);
        return loggingPreferences;
    }
}
