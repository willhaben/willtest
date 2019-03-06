package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.junit5.ProxyWrapper;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(DriverParameterResolver.class)
class ProxyExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyExample.class);

    @Test
    void createProxyAndCaptureRequest(WebDriver driver, ProxyWrapper proxyWrapper) {
        proxyWrapper.getProxy().enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxyWrapper.getProxy().newHar();
        driver.get("https://www.google.com");
        proxyWrapper.getProxy().getHar()
                .getLog()
                .getEntries()
                .stream()
                .map(HarEntry::getRequest)
                .forEach(request -> {
                    LOGGER.info(request.getUrl());
                });
    }
}
