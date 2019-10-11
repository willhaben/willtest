package at.willhaben.misc.test;

import at.willhaben.misc.test.util.HeadlessBrowserConfig;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolverExtension;
import at.willhaben.willtest.proxy.ProxyWrapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;

@Tag("realBrowser")

@ExtendWith(DriverParameterResolverExtension.class)
@BrowserUtil(HeadlessBrowserConfig.class)
class ProxyFirefoxTest {

    @Test
    void testProxy(WebDriver driver, ProxyWrapper proxyWrapper) {
        driver.get("https://github.com/willhaben/willtest");
        String pageSource = driver.getPageSource();
        assertThat(pageSource, Matchers.containsString("willtest"));
    }
}
