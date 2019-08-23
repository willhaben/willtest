package at.willhaben.misc.test;

import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import at.willhaben.willtest.proxy.ProxyWrapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith(DriverParameterResolver.class)
@Tag("phantomtest")
class ProxyFirefoxTest {

    @Test
    void testProxy(WebDriver driver, ProxyWrapper proxyWrapper) {
        driver.get("http://www.google.at");
    }
}
