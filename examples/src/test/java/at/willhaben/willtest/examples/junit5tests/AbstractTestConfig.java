package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.examples.junit5tests.config.CustomShootingStrategy;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.DefaultBrowserOptionInterceptor;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import at.willhaben.willtest.junit5.extensions.ScreenshotExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        DriverParameterResolver.class,
        ScreenshotExtension.class
})
@BrowserUtil({DefaultBrowserOptionInterceptor.class, CustomShootingStrategy.class})
public class AbstractTestConfig {

}
