package at.willhaben.willtest.log4j;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Log4j {@link Filter} implementation, which lets through only logevents having a specific key-value pair in
 * {@link org.apache.log4j.MDC}.
 */
public class MDCFilter extends Filter {
    private final String key;
    private final String value;

    public MDCFilter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int decide(LoggingEvent event) {
        return value.equals(event.getMDC(key)) ? ACCEPT : DENY;
    }
}
