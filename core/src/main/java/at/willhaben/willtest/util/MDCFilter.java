package at.willhaben.willtest.util;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by liptak on 2016.10.01..
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
        return value.equals( event.getMDC(key) ) ? ACCEPT : DENY;
    }
}
