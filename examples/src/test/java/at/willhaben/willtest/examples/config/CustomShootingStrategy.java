package at.willhaben.willtest.examples.config;

import at.willhaben.willtest.junit5.ScreenshotInterceptor;
import at.willhaben.willtest.util.FixedTopBarShootingStrategy;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

public class CustomShootingStrategy implements ScreenshotInterceptor {

    @Override
    public ShootingStrategy provideShootingStrategy() {
        return new FixedTopBarShootingStrategy(0);
    }
}
