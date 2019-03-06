package at.willhaben.willtest.junit5;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;

public class BrowserProxyFactory {

    public BrowserMobProxy createProxy() {
        BrowserMobProxyServer proxy = new BrowserMobProxyServer();
        proxy.start();
        return proxy;
    }
}
