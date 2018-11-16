package at.willhaben.willtest.junit5;

import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

public interface ScreenshotInterceptor extends BrowserUtilExtension{

    ShootingStrategy provideShootingStrategy();
}
