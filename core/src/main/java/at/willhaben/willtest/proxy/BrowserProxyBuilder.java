package at.willhaben.willtest.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserProxyBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserProxyBuilder.class);

    private BrowserProxyBuilder() {
    }

    public static BrowserProxyBuilder builder() {
        return new BrowserProxyBuilder();
    }

    public static Proxy createSeleniumProxy(BrowserMobProxy proxy) {
        return new SeleniumProxy(proxy);
    }

    public BrowserMobProxy startProxy() {
        LOGGER.info("Starting Proxy...");
        BrowserMobProxyServer proxy = new BrowserMobProxyServer();
        proxy.start();
        return proxy;
    }
}
