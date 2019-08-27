package at.willhaben.misc.test;

import at.willhaben.misc.test.util.HeadlessBrowserConfig;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import at.willhaben.willtest.proxy.ProxyWrapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@Tag("phantomtest")

@ExtendWith(DriverParameterResolver.class)
@BrowserUtil(HeadlessBrowserConfig.class)
class ProxyFirefoxTest {

    @Test
    void testProxy(WebDriver driver, ProxyWrapper proxyWrapper) {
        driver.get("http://www.google.at");
    }
}
