package at.willhaben.willtest.junit5.impl;

import at.willhaben.willtest.junit5.ProxyWrapper;
import net.lightbody.bmp.BrowserMobProxy;

public class ProxyWrapperImpl implements ProxyWrapper {

    private BrowserMobProxy proxy;

    public ProxyWrapperImpl(BrowserMobProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public BrowserMobProxy getProxy() {
        return proxy;
    }
}
