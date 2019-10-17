package at.willhaben.willtest.examples;

import at.willhaben.willtest.examples.config.CustomShootingStrategy;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolverExtension;
import at.willhaben.willtest.junit5.extensions.ScreenshotProvider;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        DriverParameterResolverExtension.class,
})
@BrowserUtil({CustomShootingStrategy.class, ScreenshotProvider.class})
public class AbstractTestConfig {

}
